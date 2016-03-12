package io.github.phantamanta44.tiabot.module.lol.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.lol.LoLModule;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion.ChampInfo;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion.ChampPassive;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandLoLChamp implements ICommand {
	
	private static final List<String> ALIASES = Arrays.asList(new String[] {"lolchampion"});
	private static final String RESULT_FORMAT = "**League of Legends Champion Profile:**\n**%s**\n*%s*\n\n```Tank: [%s]\nAtk : [%s]\nAP  : [%s]\nDiff: [%s]```\n**Passive: %s**\n\n%s\n%s%s";
	
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
		
		ChampInfo info = champ.getRating();
		StringBuffer atk = new StringBuffer(), ap = new StringBuffer(), hp = new StringBuffer(), diff = new StringBuffer();
		for (int i = 1; i <= 10; i++) {
			if (info.atk >= i) atk.append("\u25b0");
			if (info.ap >= i) ap.append("\u25b0");
			if (info.def >= i) hp.append("\u25b0");
			if (info.diff >= i) diff.append("\u25b0");
		}
		ChampPassive passive = champ.getPassive();
		String spells = Arrays.stream(champ.getSpells())
				.map(s -> String.format("**%s** *(%s)*\n%s\n\n", s.name, s.getCostFormatted(), s.getTooltipFormatted()))
				.reduce((a, b) -> a.concat(b)).orElse("No Spells");
		ctx.sendMessage(String.format(RESULT_FORMAT,
				champ.getName(), champ.getTitle(),
				hp, atk, ap, diff, passive.name, passive.desc, spells, champ.getIcon()));
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
		return ".*(?:what|who is the) (?:lol |league of legends) champ(?:ion)? is (?<a0>(?:[A-Za-z']+)(?: [A-Za-z']+)?).*";
	}

}
