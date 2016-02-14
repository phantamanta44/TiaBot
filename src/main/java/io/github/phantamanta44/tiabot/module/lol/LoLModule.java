package io.github.phantamanta44.tiabot.module.lol;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.lol.command.CommandLoLGames;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLGame;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLItem;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLRegion;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLSummoner;
import sx.blah.discord.util.Requests;

public class LoLModule extends CTModule {

	private static final String SUMM_BY_NAME = "https://%1$s.api.pvp.net/api/lol/%1$s/v1.4/summoner/by-name/%2$s";
	private static final String RECENTS_BY_SUMM = "https://%1$s.api.pvp.net/api/lol/%1$s/v1.3/game/by-summoner/%2$s/recent";
	private static final String ITEM_BY_ID = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/item/%s";
	private static final String CHAMP_BY_ID = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/champion/%s";
	
	public LoLModule() {
		commands.add(new CommandLoLGames());
	}
	
	@Override
	public String getName() {
		return "lol";
	}
	
	public static String getApiKey() {
		String key = TiaBot.config.get("riotApiKey");
		if (key == null)
			throw new UnsupportedOperationException("No provided Riot API key! Supply it in the config under key \"riotApiKey\".");
		return key;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends JsonElement> T requestFromApi(String reqUrl) throws Exception {
		JsonParser parser = new JsonParser();
		String withKey = String.format("%s?api_key=%s", reqUrl, getApiKey());
		return (T)parser.parse(Requests.GET.makeRequest(withKey));
	}
	
	public static LoLSummoner getSummoner(LoLRegion region, String name) {
		try {
			String reqUrl = String.format(SUMM_BY_NAME, region, name);
			JsonObject response = requestFromApi(reqUrl);
			String stdName = name.replaceAll("\\s", "").toLowerCase();
			if (!response.has(stdName))
				return null;
			return new LoLSummoner(response.get(stdName).getAsJsonObject(), region);
		} catch (Exception ex) {
			TiaBot.logger.warn("Error retrieving LoL summoner by name!");
			ex.printStackTrace();
			return null;
		}
	}
	
	public static List<LoLSummoner> getSummoners(LoLRegion region, String[] names) {
		try {
			String reqUrl = String.format(SUMM_BY_NAME, region, Arrays.stream(names)
					.reduce((a, b) -> a.concat(",").concat(b)).get());
			JsonObject response = requestFromApi(reqUrl);
			return response.entrySet().stream()
					.map(e -> new LoLSummoner(e.getValue().getAsJsonObject(), region))
					.collect(Collectors.toList());
		} catch (Exception ex) {
			TiaBot.logger.warn("Error retrieving LoL summoners by name!");
			ex.printStackTrace();
			return null;
		}
	}
	
	public static List<LoLGame> getRecents(LoLSummoner summ) {
		try {
			String reqUrl = String.format(RECENTS_BY_SUMM, summ.getRegion(), summ.getId());
			JsonObject response = requestFromApi(reqUrl);
			JsonArray games = response.get("games").getAsJsonArray();
			return StreamSupport.stream(games.spliterator(), true)
					.map(g -> new LoLGame(g.getAsJsonObject(), summ.getRegion(), summ))
					.collect(Collectors.toList());
		} catch (Exception ex) {
			TiaBot.logger.warn("Error retrieving recent LoL games!");
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	public static LoLItem getItem(LoLRegion rg, int id) {
		if (id == 0)
			return LoLItem.NONE;
		try {
			String reqUrl = String.format(ITEM_BY_ID, rg, id);
			JsonObject response = requestFromApi(reqUrl);
			return new LoLItem(response);
		} catch (Exception ex) {
			TiaBot.logger.warn("Error retrieving LoL item!");
			ex.printStackTrace();
			return null;
		}
	}

	public static LoLChampion getChampion(LoLRegion rg, int id) {
		try {
			String reqUrl = String.format(CHAMP_BY_ID, rg, id);
			JsonObject response = requestFromApi(reqUrl);
			return new LoLChampion(response);
		} catch (Exception ex) {
			TiaBot.logger.warn("Error retrieving LoL champion!");
			ex.printStackTrace();
			return null;
		}
	}

}
