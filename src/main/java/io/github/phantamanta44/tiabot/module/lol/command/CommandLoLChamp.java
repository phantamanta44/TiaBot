package io.github.phantamanta44.tiabot.module.lol.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.lol.LoLModule;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion.ChampPassive;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion.ChampSpell;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion.ChampSpell.SpellKey;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandLoLChamp implements ICommand {
	
	private static final List<String> ALIASES = Arrays.asList(new String[] {"lolchampion"});
	private static final String RESULT_FORMAT = "__**League of Legends Champion Profile:**__\n**%s**\n*%s*\n\n**Passive: %s**\n%s\n\n%s%s";
	
	@Override
	public String getName() {
		return "lolchamp";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Get information about a LoL champion.";
	}

	@Override
	public String getUsage() {
		return "lolchamp <name|id>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify a champion to look up!");
			return;
		}
		LoLChampion champ = LoLModule.getChampion(MessageUtils.concat(args));
		if (champ == null) {
			ctx.sendMessage("No such champion!");
			return;
		}
		ChampPassive passive = champ.getPassive();
		List<ChampSpell> spellList = Arrays.asList(champ.getSpells());
		String spells = spellList.stream()
				.map(s -> String.format("**%s: %s** *(%s)*\n%s\n\n", SpellKey.values()[spellList.indexOf(s)], s.name, s.getCostFormatted(), s.getTooltipFormatted()))
				.reduce((a, b) -> a.concat(b)).orElse("No Spells");
		String msg = String.format(RESULT_FORMAT, champ.getName(), MessageUtils.capitalize(champ.getTitle()), passive.name, passive.desc, spells, champ.getIcon());
		if (msg.length() < 2000)
			ctx.sendMessage(msg);
		else {
			int ind = 0, temp;
			while (ind >= 0 && (temp = msg.indexOf("\n\n", ind + 1)) < 2000)
				ind = temp;
			int splitInd = ind == -1 ? 1999 : ind;
			ctx.sendMessage(msg.substring(0, splitInd));
			ctx.sendMessage(msg.substring(splitInd + 1));
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
		return ".*(?:what|who is(?: the)?) (?:lol|league of legends) champ(?:ion)?(:? is)? (?<a0>(?:[A-Za-z']+)(?: [A-Za-z']+)?).*";
	}

}
