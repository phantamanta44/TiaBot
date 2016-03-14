package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.ModuleManager;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandHelp implements ICommand {
	
	private static final List<String> ALIASES = Arrays.asList(new String[] {"?", "helpme", "man"});

	@Override
	public String getName() {
		return "help";
	}
	
	@Override
	public List<String> getAliases() {
		return ALIASES;
	}
	
	@Override
	public String getDesc() {
		return "Gets information about modules and commands.";
	}

	@Override
	public String getUsage() {
		return "help [module] [command]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length == 0) {
			String mods = ModuleManager.streamModules()
					.filter(m -> ModuleManager.isEnabled(m.getKey()))
					.map(m -> String.format("**%s** \u2013 %s", MessageUtils.capitalize(m.getValue().getName()), m.getValue().getDesc()))
					.reduce((a, b) -> a.concat("\n").concat(b)).get();
			ctx.sendMessage("__**Available Modules:**__\n%s", mods);
		} else if (args.length == 1) {
			CTModule mod = ModuleManager.getModule(args[0].toLowerCase());
			if (mod == null) {
				ctx.sendMessage("No such module!");
				return;
			}
			String pref = TiaBot.getPrefix();
			String cmds = mod.getCommands().stream()
					.map(m -> String.format("%s%s - %s", pref, m.getUsage(), m.getDesc()))
					.sorted()
					.reduce((a, b) -> a.concat("\n").concat(b)).orElse("*This module exposes no commands.*");
			ctx.sendMessage("__**Module: %s**__\nAuthor: %s\n%s\n\nCommand Summary:\n```%s```", MessageUtils.capitalize(mod.getName()), mod.getAuthor(), mod.getDesc(), cmds);
		} else if (args.length == 2) {
			CTModule mod = ModuleManager.getModule(args[0].toLowerCase());
			if (mod == null) {
				ctx.sendMessage("No such module!");
				return;
			}
			ICommand cmd = mod.getCommands().stream()
					.filter(c -> c.getName().equalsIgnoreCase(args[1]))
					.findAny().orElse(null);
			if (cmd == null) {
				ctx.sendMessage("No such command for module %s!", mod.getName());
				return;
			}
			String pref = TiaBot.getPrefix();
			String aliases = cmd.getAliases().stream()
					.map(a -> String.format("`%s%s`", pref, a))
					.reduce((a, b) -> a.concat(", ").concat(b)).orElse("None");
			ctx.sendMessage("__**Command: %s**__\nUsage: `%s%s`\nAliases: %s\n%s", cmd.getName(), pref, cmd.getUsage(), aliases, cmd.getDesc());
		}
		else
			ctx.sendMessage("Invalid syntax!");
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
		return null;
	}

}