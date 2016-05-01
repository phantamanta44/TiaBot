package io.github.phantamanta44.tiabot.module.core.command;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandHalt implements ICommand {

	@Override
	public String getName() {
		return "halt";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Kills the bot.";
	}

	@Override
	public String getUsage() {
		return "halt [condition]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		String msg = "Halting!";
		int code = 0;
		if (args.length > 0) {
			switch (args[0]) {
				case "reboot":
					msg = "Rebooting!";
					code = 32;
					break;
				case "update":
					msg = "Rebooting for update!";
					code = 33;
					break;
				default:
					ctx.sendMessage("Unknown exit condition!");
					return;
			}
		}

		ctx.sendMessage(msg);
		Runtime.getRuntime().exit(code);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return TiaBot.isAdmin(sender);
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		return "No permission!";
	}
	
	@Override
	public String getEnglishInvocation() {
		return ".*(?:kill yourself|cease to be).*";
	}

}
