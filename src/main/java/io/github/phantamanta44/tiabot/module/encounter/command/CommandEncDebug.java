package io.github.phantamanta44.tiabot.module.encounter.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.fge.lambdas.Throwing;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterBoss;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler.Encounter;
import sx.blah.discord.handle.obj.IUser;

public class CommandEncDebug implements ICommand {

	@Override
	public String getName() {
		return "encdebug";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Print debug information.";
	}

	@Override
	public String getUsage() {
		return "encdebug [battle|boss]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify a debug target!");
			return;
		}
		Encounter enc = EncounterHandler.getEncounter(ctx.getChannel());
		if (enc == null) {
			ctx.sendMessage("There is no encounter in progress!");
			return;
		}
		try {
			final Map<String, String> props = new HashMap<>();
			switch (args[0]) {
			case "battle":
				Arrays.stream(enc.getClass().getDeclaredFields())
						.forEach(Throwing.consumer(f -> {
							f.setAccessible(true);
							props.put(f.getName(), f.get(enc).toString());
						}));
				break;
			case "boss":
				EncounterBoss boss = enc.getBoss();
				Arrays.stream(boss.getClass().getDeclaredFields())
						.forEach(Throwing.consumer(f -> {
							f.setAccessible(true);
							props.put(f.getName(), f.get(boss).toString());
						}));
				break;
			default:
				ctx.sendMessage("Invalid debug target!");
				return;
			}
			ctx.sendMessage(props.entrySet().stream()
					.map(e -> String.format("%s: %s", e.getKey(), e.getValue()))
					.reduce((a, b) -> a.concat("\n").concat(b)).orElse("Error!"));
		} catch (Exception ex) {
			ctx.sendMessage("Error retrieving debug info!");
			ex.printStackTrace();
		}
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
