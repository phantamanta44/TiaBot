package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.obj.IInvite;
import sx.blah.discord.handle.obj.IInvite.InviteResponse;
import sx.blah.discord.handle.obj.IUser;

public class CommandInvite implements ICommand {
	
	private static final Pattern INVITE_PAT = Pattern.compile("(?:(?:https?://)?discord.gg/)?(\\w{16})");

	@Override
	public String getName() {
		return "invite";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Adds the bot to a server.";
	}

	@Override
	public String getUsage() {
		return "invite <link|code>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must provide an invite link or code!");
			return;
		}
		Matcher m = INVITE_PAT.matcher(args[0]);
		if (!m.matches()) {
			ctx.sendMessage("Invalid invite link or code!");
			return;
		}
		try {
			IInvite inv = Discord.getInstance().getInviteByCode(m.group(1));
			InviteResponse resp = inv.details();
			if (Discord.getInstance().getGuilds().contains(resp.getGuildID())) {
				ctx.sendMessage("TiaBot is already in that guild!");
				return;
			}
			inv.accept();
			ctx.sendMessage("Joined guild %s.", resp.getGuildName());
		} catch (Exception ex) {
			
		}
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
