package io.github.phantamanta44.tiabot.module.lol;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.lol.command.CommandLoLChamp;
import io.github.phantamanta44.tiabot.module.lol.command.CommandLoLGame;
import io.github.phantamanta44.tiabot.module.lol.command.CommandLoLItem;
import io.github.phantamanta44.tiabot.module.lol.dto.*;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import io.github.phantamanta44.tiabot.util.http.HttpUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoLModule extends CTModule {

	private static final String DATA_DRAGON = "http://ddragon.leagueoflegends.com/cdn/6.5.1/";
	private static final String SUMM_BY_NAME = "https://%1$s.api.pvp.net/api/lol/%1$s/v1.4/summoner/by-name/%2$s";
	private static final String RECENTS_BY_SUMM = "https://%1$s.api.pvp.net/api/lol/%1$s/v1.3/game/by-summoner/%2$s/recent";
	private static final String ITEM_LIST = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/item";
	private static final String CHAMP_LIST = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/champion";
	
	private static final Map<Integer, LoLItem> itemMap = new HashMap<>();
	private static final Map<Integer, LoLChampion> champMap = new HashMap<>();
	
	public LoLModule() {
		commands.add(new CommandLoLGame());
		commands.add(new CommandLoLItem());
		commands.add(new CommandLoLChamp());
		loadStaticData(LoLRegion.NA);
	}
	
	@Override
	public String getName() {
		return "lol";
	}
	
	@Override
	public String getDesc() {
		return "Provides commands for looking up League of Legends data.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}
	
	public static String getApiKey() {
		String key = TiaBot.config.get("riotApiKey");
		if (key == null)
			throw new UnsupportedOperationException("No provided Riot API key! Supply it in the config under key \"riotApiKey\".");
		return key;
	}
	
	private static <T extends JsonElement> T requestFromApi(String reqUrl) throws Exception {
		return requestFromApi(reqUrl, "");
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends JsonElement> T requestFromApi(String reqUrl, String args) throws Exception {
		JsonParser parser = new JsonParser();
		String withKey = String.format("%s?api_key=%s%s", reqUrl, getApiKey(), args);
		return (T)parser.parse(HttpUtils.requestXml(withKey));
	}
	
	private static void loadStaticData(LoLRegion rg) {
		TiaBot.logger.info("Loading LoL static data...");
		try {
			String itemUrl = String.format(ITEM_LIST, rg);
			JsonObject itemJson = requestFromApi(itemUrl, "&itemListData=all");
			itemJson.get("data").getAsJsonObject().entrySet().forEach(i ->
					itemMap.put(Integer.parseInt(i.getKey()), new LoLItem(i.getValue().getAsJsonObject()))
					);
			String champUrl = String.format(CHAMP_LIST, rg);
			JsonObject champJson = requestFromApi(champUrl, "&champData=image,info,spells,passive,stats");
			champJson.get("data").getAsJsonObject().entrySet().forEach(i -> {
				LoLChampion champ = new LoLChampion(i.getValue().getAsJsonObject());
				champMap.put(champ.getId(), champ);
			});
		} catch (Exception ex) {
			TiaBot.logger.warn("Error retrieving LoL static data!");
			ex.printStackTrace();
		}
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
		return itemMap.get(id);
	}
	
	public static LoLItem getItem(String name) {
		return itemMap.values().stream()
				.filter(i -> MessageUtils.lenientMatch(i.getName(), name))
				.findAny().orElse(LoLItem.NONE);
	}

	public static LoLChampion getChampion(LoLRegion rg, int id) {
		return champMap.get(id);
	}
	
	public static LoLChampion getChampion(String name) {
		return champMap.entrySet().stream()
				.filter(e -> MessageUtils.lenientMatch(e.getValue().getName(), name))
				.map(Map.Entry::getValue)
				.findAny().orElse(null);
	}

	public static String dataDragon(String ept) {
		return DATA_DRAGON + ept;
	}

}
