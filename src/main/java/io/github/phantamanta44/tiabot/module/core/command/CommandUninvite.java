package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

public class CommandUninvite implements ICommand {
	
	@Override
	public String getName() {
		return "uninvite";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Removes the bot from a server.";
	}

	@Override
	public String getUsage() {
		return "uninvite";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		ctx.sendMessage("Leaving guild %s.", ctx.getGuild().getName());
		try {
			ctx.getGuild().leaveGuild();
		} catch (Exception ex) {
			ctx.sendMessage("Errored while leaving guild! Try again.");
			ex.printStackTrace();
		}
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return TiaBot.isAdmin(sender);
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		return "No permission! Message a TiaBot administrator if you want to remove the bot.";
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
