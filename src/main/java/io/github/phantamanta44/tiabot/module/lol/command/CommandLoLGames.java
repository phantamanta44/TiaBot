package io.github.phantamanta44.tiabot.module.lol.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.lol.LoLModule;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLGame;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLRegion;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLSummoner;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandLoLGames implements ICommand {
	
	private static final String RESULT_FORMAT = "%s\n```[%s] %s\nLength: %s\nGamemode: %s\nChampion: %s\nLevel: %s\nKDA: %s/%s/%s\nCreep Score: %s\nGold Earned: %s\nItems: %s```";
	private static final DateFormat EPOCH_FORMAT = new SimpleDateFormat("yyyy-dd-mm, HH:mm:ss");
	
	@Override
	public String getName() {
		return "lolgames";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Get data about a summoner's previous game.";
	}

	@Override
	public String getUsage() {
		return "lolgames <region> <summoner> [index]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 2) {
			ctx.sendMessage("You must provide a region and summoner name!");
			return;
		}
		int ind = 1;
		try {
			ind = Integer.parseInt(args[2]);
		} catch (NumberFormatException ex) {
			ctx.sendMessage("Invalid index!");
			return;
		} catch (Exception ex) { }
		if (ind > 10 || ind < 1) {
			ctx.sendMessage("Index out of range! (1\u201310)");
			return;
		}
		LoLRegion rg = LoLRegion.parseRegion(args[0]);
		if (rg == null) {
			ctx.sendMessage("Invalid region!");
			return;
		}
		LoLSummoner summ = LoLModule.getSummoner(rg, args[1]);
		if (summ == null) {
			ctx.sendMessage("Summoner doesn't exist!");
			return;
		}
		List<LoLGame> games = LoLModule.getRecents(summ);
		if (games.isEmpty()) {
			ctx.sendMessage("No games found!");
			return;
		}
		if (games.size() < ind) {
			ctx.sendMessage("Indicated game index does not exist!");
			return;
		}
		LoLGame game = games.get(ind - 1);
		int[] kda = game.getKda();
		String items = Arrays.stream(game.getItems())
				.mapToObj(i -> LoLModule.getItem(rg, i).getName())
				.reduce((a, b) -> a.concat(", ").concat(b)).get();
		ctx.sendMessage(String.format(RESULT_FORMAT, game.getChillingSmiteUrl(),
				game.isWin() ? "Victory" : "Defeat",
				EPOCH_FORMAT.format(new Date(game.getTimestamp())),
				MessageUtils.formatDuration(game.getDuration()), game.getMode().name,
				LoLModule.getChampion(rg, game.getPlayer().champ).getName(),
				game.getLevel(), kda[0], kda[1], kda[2],
				game.getCreepScore(), game.getGold(), items));
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
