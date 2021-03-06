package io.github.phantamanta44.tiabot.module.scripting.command;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.scripting.host.*;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import org.mozilla.javascript.*;
import sx.blah.discord.handle.obj.IUser;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandEval implements ICommand {
	
	protected static final Pattern lambdaRegex = Pattern.compile("([A-Za-z]+)\\|\\s*\\(?(\\w+(?:,\\s*\\w+)*)\\)?\\s*->\\s*\\{?(.*?)\\}?\\|");
	protected static final Map<String, String> lambdaAbbrev = new HashMap<>();
	
	static {
		lambdaAbbrev.put("c", "Consumer");
		lambdaAbbrev.put("bc", "BiConsumer");
		lambdaAbbrev.put("f", "Function");
		lambdaAbbrev.put("bf", "BiFunction");
		lambdaAbbrev.put("p", "Predicate");
		lambdaAbbrev.put("bp", "BiPredicate");
		lambdaAbbrev.put("s", "Supplier");
		lambdaAbbrev.put("uo", "UnaryOperator");
		lambdaAbbrev.put("bo", "BinaryOperator");
	}

	@Override
	public String getName() {
		return "eval";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Evaluate some JS.";
	}

	@Override
	public String getUsage() {
		return "eval <script>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ec) {
		if (args.length < 1) {
			ec.sendMessage("You must provide a script!");
			return;
		}
		String script = MessageUtils.concat(args);
		if (script.startsWith("```") && script.endsWith("```"))
			script = script.substring(3, script.length() - 3);
		else if (script.startsWith("`") && script.endsWith("`"))
			script = script.substring(1, script.length() - 1);
		Matcher mat = lambdaRegex.matcher(script);
		StringBuffer replBuffer = new StringBuffer();
		while (mat.find())
			mat.appendReplacement(replBuffer, parseLambda(mat.group()));
		mat.appendTail(replBuffer);
		script = replBuffer.toString();
		
		Context con = Context.enter();
		try {
			Scriptable scope = con.initSafeStandardObjects();
			defineObjects(scope, ec);
			Object rtObj = con.evaluateString(scope, script, "<eval>", 1, null);
			Object apiHost = scope.get("api", scope);
			if (apiHost != Scriptable.NOT_FOUND && apiHost instanceof HostObjectDiscordAPI)
				((HostObjectDiscordAPI)apiHost).flushBufferSafe(ec);
			String rtVal = Context.toString(rtObj);
			if (!(rtObj instanceof Undefined) && !rtVal.isEmpty())
				ec.sendMessage(rtVal);
		} catch (RhinoException ex) {
			ec.sendMessage("```%s\n%s```", ex.getMessage(), Arrays.stream(ex.getScriptStack(8, null))
					.reduce("", (a, b) -> a.concat("\n").concat(b.toString()), String::concat));
		} catch (Exception ex) {
			StackTraceElement ste = ex.getStackTrace()[0];
			ec.sendMessage("`%s` thrown while executing script (at `%s:%s`)", ex.getClass().getName(), ste.getClassName(), ste.getLineNumber());
			ex.printStackTrace();
		} finally {
			Context.exit();
		}
	}

	protected static void defineObjects(Scriptable scope, IEventContext ec) throws Exception {
		ScriptableObject.defineClass(scope, HostObjectChannel.class);
		ScriptableObject.defineClass(scope, HostObjectDiscordAPI.class);
		ScriptableObject.defineClass(scope, HostObjectGuild.class);
		ScriptableObject.defineClass(scope, HostObjectMessage.class);
		ScriptableObject.defineClass(scope, HostObjectRole.class);
		ScriptableObject.defineClass(scope, HostObjectUser.class);
		ScriptableObject.putProperty(scope, "me", HostObjectUser.impl(ec.getUser(), scope));
		ScriptableObject.putProperty(scope, "bot", HostObjectUser.impl(Discord.getInstance().getBot(), scope));
		ScriptableObject.putProperty(scope, "channel", HostObjectChannel.impl(ec.getChannel(), scope));
		if (!ec.getChannel().isPrivate())
			ScriptableObject.putProperty(scope, "guild", HostObjectGuild.impl(ec.getGuild(), scope));
		ScriptableObject.putProperty(scope, "api", Context.getCurrentContext().newObject(scope, "DiscordAPI"));
	}
	
	protected static String parseLambda(String exp) {
		try {
			Matcher mat = lambdaRegex.matcher(exp);
			if (!mat.matches())
				return exp;
			String cName = expandLambda(mat.group(1)), args = mat.group(2), body = mat.group(3);
			Class<?> funcInt = Class.forName("java.util.function." + cName);
			if (!funcInt.isAnnotationPresent(FunctionalInterface.class) || !funcInt.isInterface())
				return exp;
			Method funcMtd = Arrays.stream(funcInt.getMethods())
					.filter(m -> !m.isDefault() && !Modifier.isStatic(m.getModifiers()))
					.findFirst().get();
			if (funcMtd.getReturnType() != Void.TYPE && !body.contains("return"))
				body = "return " + body;
			return String.format("new Object({%s: function(%s) {%s}})", funcMtd.getName(), args, body);
		} catch (Exception ex) {
			return exp;
		}
	}

	protected static String expandLambda(String cName) {
		if (!lambdaAbbrev.containsKey(cName))
			return cName;
		return lambdaAbbrev.get(cName);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new IllegalArgumentException();
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
