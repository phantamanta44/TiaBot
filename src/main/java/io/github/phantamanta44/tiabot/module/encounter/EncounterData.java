package io.github.phantamanta44.tiabot.module.encounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterBoss;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterPlayer;
import io.github.phantamanta44.tiabot.util.ChanceList;
import sx.blah.discord.handle.obj.IUser;

public class EncounterData {

	private static final File ENC_FILE = new File("encounter.json");
	private static final File DATA_FILE = new File("encounter_data.json");
	private static Map<String, EncounterPlayer> players = new ConcurrentHashMap<>();
	private static ChanceList<EncounterBoss> bosses;
	
	public static void load() {
		JsonParser parser = new JsonParser();
		bosses = new ChanceList<>();
		players.clear();
		try (BufferedReader dfIn = new BufferedReader(new FileReader(DATA_FILE));
				BufferedReader cfgIn = new BufferedReader(new FileReader(ENC_FILE))) {
			JsonObject dfMap = parser.parse(dfIn).getAsJsonObject();
			JsonObject plMap = dfMap.get("players").getAsJsonObject();
			plMap.entrySet().forEach(e -> players.put(e.getKey(), new EncounterPlayer(e.getValue().getAsJsonObject())));
			JsonObject cfgMap = parser.parse(cfgIn).getAsJsonObject();
			JsonArray bossList = cfgMap.get("bosses").getAsJsonArray();
			bossList.forEach(b -> bosses.addOutcome(new EncounterBoss(b.getAsJsonObject())));
		} catch (Exception ex) {
			TiaBot.logger.severe("Errored while loading encounter data!");
			ex.printStackTrace();
		}
	}
	
	public static void save() {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.disableHtmlEscaping()
				.create();
		try (PrintWriter strOut = new PrintWriter(new FileWriter(DATA_FILE))) {
			JsonObject dfMap = new JsonObject();
			JsonObject plMap = new JsonObject();
			players.forEach((k, v) -> plMap.add(k, v.serialize()));
			dfMap.add("players", plMap);
			strOut.println(gson.toJson(dfMap));
		} catch (Exception ex) {
			TiaBot.logger.severe("Errored while saving encounter data!");
			ex.printStackTrace();
		}
	}
	
	public static EncounterPlayer getPlayer(String id) {
		return players.get(id);
	}
	
	public static EncounterBoss getBoss(Random random) {
		return bosses.getAtRandom(random).clone();
	}
	
	public static EncounterBoss getBoss(String name) {
		return bosses.stream()
				.filter(b -> b.getName().equalsIgnoreCase(name))
				.findAny().get().clone();
	}

	public static EncounterPlayer registerPlayer(IUser user) {
		EncounterPlayer pl = new EncounterPlayer(user);
		players.put(user.getID(), pl);
		return pl;
	}
	
}
