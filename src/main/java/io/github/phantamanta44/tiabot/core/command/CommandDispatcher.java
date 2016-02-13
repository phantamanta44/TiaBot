package io.github.phantamanta44.tiabot.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.impl.events.MentionEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class CommandDispatcher implements ICTListener {
	
	protected static final Map<String, ICommand> cmdMapping = new ConcurrentHashMap<>();
	protected static final Map<String, ICommand> aliasMapping = new ConcurrentHashMap<>();
	protected static final Map<String, ICommand> regexMapping = new ConcurrentHashMap<>();
	
	public static void registerCommand(ICommand cmd) {
		cmdMapping.put(cmd.getName().toLowerCase(), cmd);
		aliasMapping.put(cmd.getName().toLowerCase(), cmd);
		if (cmd.getEnglishInvocation() != null && !cmd.getEnglishInvocation().isEmpty())
			regexMapping.put(cmd.getEnglishInvocation(), cmd);
		cmd.getAliases().forEach(a -> aliasMapping.put(a.toLowerCase(), cmd));
	}
	
	public static void unregisterCommand(ICommand cmd) {
		cmdMapping.remove(cmd.getName().toLowerCase(), cmd);
		aliasMapping.remove(cmd.getName().toLowerCase(), cmd);
		if (cmd.getEnglishInvocation() != null && !cmd.getEnglishInvocation().isEmpty())
			regexMapping.remove(cmd.getEnglishInvocation(), cmd);
		cmd.getAliases().forEach(a -> aliasMapping.remove(a.toLowerCase(), cmd));
	}
	
	public CommandDispatcher() {
		cmdMapping.clear();
		aliasMapping.clear();
	}
	
	public static Stream<ICommand> streamCommands() {
		return cmdMapping.values().stream();
	}

	@ListenTo
	public void onMessageReceived(MessageReceivedEvent event, IEventContext ctx) {
		processEvent(event.getMessage().getAuthor(), event.getMessage().getContent(), ctx);
	}
	
	private void processEvent(IUser sender, String msg, IEventContext ctx) {
		String pref = TiaBot.getPrefix();
		if (!msg.toLowerCase().startsWith(pref.toLowerCase()))
			return;
		String[] msgSplit = msg.substring(pref.length()).split("\\s");
		String cmd = msgSplit[0];
		String[] args;
		if (msgSplit.length > 1)
			args = Arrays.copyOfRange(msgSplit, 1, msgSplit.length);
		else
			args = new String[0];
		processCommand(sender, cmd, args, ctx);
	}
	
	@ListenTo
	public void onMention(MentionEvent event, IEventContext ctx) {
		String msg = event.getMessage().getContent(), men = Discord.getInstance().getBot().mention();
		IUser sender = event.getMessage().getAuthor();
		if (!msg.startsWith(men) && !msg.endsWith(men))
			return;
		for (Entry<String, ICommand> entry : regexMapping.entrySet()) {
			Matcher m = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE).matcher(msg);
			if (!m.matches())
				continue;
			ICommand cmd = entry.getValue();
			List<String> args = new ArrayList<>();
			for (int i = 0; true; i++) {
				try {
					String g = m.group("a" + i);
					if (g == null)
						break;
					args.add(g);
				} catch (IllegalArgumentException ex) {
					break;
				}
			}
			TiaBot.logger.info("E %s/%s %s: %s for %s %s", ctx.getGuild().getName(),
					ctx.getChannel().getName(),	ctx.getUser().getName(), msg, cmd.getName(),
					args.stream().reduce((a, b) -> a.concat(" ").concat(b)).orElse(""));
			if (cmd.canUseCommand(sender, ctx))
				cmd.execute(sender, args.toArray(new String[0]), ctx);
			else
				ctx.sendMessage("%s: %s", sender.mention(), cmd.getPermissionMessage(sender, ctx));
		}
	}
	
	private void processCommand(IUser sender, String cmdName, String[] args, IEventContext ctx) {
		ICommand cmd;
		if ((cmd = aliasMapping.get(cmdName)) != null) {
			TiaBot.logger.info("C %s/%s %s: %s %s", ctx.getGuild().getName(), ctx.getChannel().getName(),
					ctx.getUser().getName(), cmd.getName(), MessageUtils.concat(args));
			if (cmd.canUseCommand(sender, ctx))
				cmd.execute(sender, args, ctx);
			else
				ctx.sendMessage("%s: %s", sender.mention(), cmd.getPermissionMessage(sender, ctx));
		}
	}
	
}