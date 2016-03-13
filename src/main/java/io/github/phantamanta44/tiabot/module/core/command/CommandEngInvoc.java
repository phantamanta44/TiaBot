package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.CommandDispatcher;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

public class CommandEngInvoc implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"enginvok", "plainenglish"});
	
	@Override
	public String getName() {
		return "enginvoc";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Retrieves the regular expression for a command's plain english invocation.";
	}

	@Override
	public String getUsage() {
		return "enginvoc [command]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify a command to look up!");
			return;
		}
		ICommand cmd = CommandDispatcher.streamCommands()
				.filter(c -> c.getName().equalsIgnoreCase(args[0]))
				.findAny().orElse(null);
		if (cmd == null) {
			ctx.sendMessage("No such command!");
			return;
		}
		String engInvoc = cmd.getEnglishInvocation();
		if (engInvoc == null || engInvoc.isEmpty())
			ctx.sendMessage("Command \"%s\" has no english invocation.", cmd.getName());
		else
			ctx.sendMessage("Command \"%s\" has english invocation: `%s`", cmd.getName(), engInvoc);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEnglishInvocation() {
		return ".*(?:what is|what'?s) (?:the )?(?:enginvoc|(?:plain)?english(?: invocation)?) (?:for|of|to) (?:the )?(?:command|cmd) (?<a0>\\w+).*";
	}

}
