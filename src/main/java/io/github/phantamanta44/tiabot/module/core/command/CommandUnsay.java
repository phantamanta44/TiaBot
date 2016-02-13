package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.core.event.RevokeHandler;
import io.github.phantamanta44.tiabot.util.PermissionHelper;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class CommandUnsay implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"delete", "revoke"});
	
	@Override
	public String getName() {
		return "unsay";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Revokes a previous message sent by the bot.";
	}

	@Override
	public String getUsage() {
		return "unsay [#count]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		RevokeHandler.procCmd(sender, args, ctx);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return TiaBot.isAdmin(sender) || PermissionHelper.hasPermission(sender, ctx.getChannel(), Permissions.MANAGE_MESSAGES);
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		return "No permission!";
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
	