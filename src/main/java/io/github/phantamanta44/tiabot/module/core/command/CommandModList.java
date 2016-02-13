package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.ModuleManager;
import sx.blah.discord.handle.obj.IUser;

public class CommandModList implements ICommand {

	@Override
	public String getName() {
		return "modlist";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Get a list of modules.";
	}

	@Override
	public String getUsage() {
		return "modlist";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		ctx.sendMessage("**Module List:**\n```%s```", ModuleManager.streamStatus()
				.map(entry -> String.format("%s | %s", entry.getValue().getValue() ? "1" : "0", entry.getKey()))
				.reduce((a, b) -> a.concat("\n").concat(b)).get());
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
