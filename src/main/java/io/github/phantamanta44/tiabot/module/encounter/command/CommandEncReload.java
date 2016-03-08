package io.github.phantamanta44.tiabot.module.encounter.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import sx.blah.discord.handle.obj.IUser;

public class CommandEncReload implements ICommand {
	
	@Override
	public String getName() {
		return "encreload";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Reload the encounter database, without saving.";
	}

	@Override
	public String getUsage() {
		return "encreload";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		EncounterData.load();
		ctx.sendMessage("Data reloaded.");
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
