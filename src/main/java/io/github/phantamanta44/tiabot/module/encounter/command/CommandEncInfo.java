package io.github.phantamanta44.tiabot.module.encounter.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterPlayer;
import io.github.phantamanta44.tiabot.module.encounter.data.StatsDto;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandEncInfo implements ICommand {

	private static final String STAT_FORMAT = new StringBuilder()
			.append("Health: %d (%d base)\n")
			.append("Armor: %d (%d base)\n")
			.append("Attack Damage: %d (%d base)\n")
			.append("Ability Power: %d (%d base)\n")
			.append("Mana Pool: %d (%d base)\n")
			.append("Mana Regeneration: %d per turn (%d base)\n")
			.append("Armor Penetration: %.0f%%\n")
			.append("Life Steal: %.0f%%\n")
			.append("Critical Strike Chance: %.0f%%\n")
			.append("Critical Strike Damage: %.0f%%")
			.toString();
	
	@Override
	public String getName() {
		return "encinfo";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Look up a game's info.";
	}

	@Override
	public String getUsage() {
		return "encinfo [@game]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		IUser user;
		if (args.length < 1)
			user = ctx.getUser();
		else {
			user = MessageUtils.resolveMention(args[0]);
			if (user == null) {
				ctx.sendMessage("Nonexistent or unknown lookup target!");
				return;
			}
		}
		EncounterPlayer pl = EncounterHandler.getEncPlayer(user);
		String itemStr = pl.getInv().stream()
				.map(i -> i.getName())
				.reduce((a, b) -> a.concat(", ").concat(b)).orElse("None");
		String kitStr = Arrays.stream(pl.getKit())
				.map(s -> s.getName())
				.reduce((a, b) -> a.concat(", ").concat(b)).orElse("No Spells? what");
		StatsDto base = pl.getBaseStats();
		String stats = String.format(STAT_FORMAT,
				pl.getMaxHealth(), base.maxHp,
				pl.getDefenseModifier(), base.def,
				pl.getDamageModifier(), base.atk,
				pl.getAbilityPower(), base.ap,
				pl.getMaxMana(), base.maxMana,
				pl.getManaGen(), base.manaGen,
				pl.getArmorPen() * 100D, pl.getLifeSteal() * 100D,
				pl.getCritChance() * 100D, pl.getCritModifier() * 100D);
		String msg = String.format("__**Player Info:** %s__\nLevel %s (%s/%s)\nStats:\n```%s```\nItems: %s\nKit: %s",
				pl.getName(), pl.getLevel(), pl.getExp(), pl.getExpNeeded(), stats, itemStr, kitStr);
		ctx.sendMessage(msg);
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
