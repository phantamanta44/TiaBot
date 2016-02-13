package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandPrefix implements ICommand {
	
	@Override
	public String getName() {
		return "chpref";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Change the command prefix.";
	}

	@Override
	public String getUsage() {
		return "chpref <prefix>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You need to provide a prefix!");
			return;
		}
		String pref = MessageUtils.concat(args);
		if ((pref.startsWith("'") && pref.endsWith("'"))
				|| (pref.startsWith("\"") && pref.endsWith("\"")))
			pref = pref.substring(1, pref.length() - 1);
		TiaBot.setPrefix(pref);
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
		return null;
	}

}
