package io.github.phantamanta44.tiabot.module.scripting.command;

import java.util.Arrays;
import java.util.regex.Matcher;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.scripting.host.HostObjectDiscordAPI;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandSudo extends CommandEval {

	@Override
	public String getName() {
		return "sudo";
	}

	@Override
	public String getDesc() {
		return "Evaluate some JS without restrictions.";
	}

	@Override
	public String getUsage() {
		return "sudo <script>";
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
			Scriptable scope = con.initStandardObjects();
			defineObjects(scope, ec);
			Object rtObj = con.evaluateString(scope, script, "<eval>", 1, null);
			Object apiHost = scope.get("api", scope);
			if (apiHost != Scriptable.NOT_FOUND && apiHost instanceof HostObjectDiscordAPI)
				((HostObjectDiscordAPI)apiHost).flushBuffer(ec);
			String rtVal = Context.toString(rtObj);
			if (!(rtObj instanceof Undefined) && !rtVal.isEmpty())
				ec.sendMessage(rtVal);
		} catch (RhinoException ex) {
			ec.sendMessage("```%s\n%s```", ex.getMessage(), Arrays.stream(ex.getScriptStack(8, null))
					.reduce("", (a, b) -> a.toString().concat("\n").concat(b.toString()), (a, b) -> a.concat(b)));
		} catch (Exception ex) {
			StackTraceElement ste = ex.getStackTrace()[0];
			ec.sendMessage("`%s` thrown while executing script (at `%s:%s`)", ex.getClass().getName(), ste.getClassName(), ste.getLineNumber());
			ex.printStackTrace();
		} finally {
			Context.exit();
		}
	}
	
	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return TiaBot.isAdmin(sender);
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		return "No permission!";
	}

}
