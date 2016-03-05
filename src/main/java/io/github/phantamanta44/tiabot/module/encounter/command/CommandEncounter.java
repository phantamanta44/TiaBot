package io.github.phantamanta44.tiabot.module.encounter.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler;
import sx.blah.discord.handle.obj.IUser;

public class CommandEncounter implements ICommand {
	
	@Override
	public String getName() {
		return "encounter";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Begin a random encounter event.";
	}

	@Override
	public String getUsage() {
		return "encounter [boss]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		EncounterHandler.procCmd(sender, args, ctx);
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
		return ".*(?:start|begin) a (?:(?:boss ?)?fight|random encounter)(?:(?: with| against) (?:a )?(?<a0>\\S+))?.*";
	}

}
