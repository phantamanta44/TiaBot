package io.github.phantamanta44.tiabot.module.encounter.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import sx.blah.discord.handle.obj.IUser;

public class CommandEncSave implements ICommand {
	
	@Override
	public String getName() {
		return "encsave";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Save the encounter database, overwriting the current data files.";
	}

	@Override
	public String getUsage() {
		return "encsave";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		EncounterData.save();
		ctx.sendMessage("Data Saved.");
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
