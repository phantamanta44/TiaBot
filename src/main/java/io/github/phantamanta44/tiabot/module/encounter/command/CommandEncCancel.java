package io.github.phantamanta44.tiabot.module.encounter.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler.Encounter;
import sx.blah.discord.handle.obj.IUser;

public class CommandEncCancel implements ICommand {

	@Override
	public String getName() {
		return "enccancel";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Cancel an ongoing battle.";
	}

	@Override
	public String getUsage() {
		return "enccancel";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		Encounter enc = EncounterHandler.getEncounter(ctx.getChannel());
		if (enc == null) {
			ctx.sendMessage("There is no encounter in progress!");
			return;
		}
		enc.cancel();
		ctx.sendMessage("Encounter cancelled.");
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
