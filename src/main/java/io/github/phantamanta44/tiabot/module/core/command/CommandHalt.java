package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

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
		return "halt";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		ctx.sendMessage("Halting!");
		Runtime.getRuntime().exit(0);
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
