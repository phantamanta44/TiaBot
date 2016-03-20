package io.github.phantamanta44.tiabot.module.econ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableLong;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.phantamanta44.tiabot.TiaBot;
import sx.blah.discord.handle.obj.IUser;

public class EconData {

	private static final File DATA_FILE = new File("econ.json");
	private static final Map<String, MutableLong> bank = new ConcurrentHashMap<>();
	
	public static void save() {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.disableHtmlEscaping()
				.create();
		try (PrintWriter strOut = new PrintWriter(new FileWriter(DATA_FILE))) {
			JsonObject bankObj = new JsonObject();
			bank.forEach((k, v) -> bankObj.addProperty(k, v.getValue()));
			strOut.println(gson.toJson(bankObj));
		} catch (Exception ex) {
			TiaBot.logger.severe("Failed to save economy data!");
			ex.printStackTrace();
		}
	}
	
	public static void load() {
		JsonParser parser = new JsonParser();
		try (BufferedReader strIn = new BufferedReader(new FileReader(DATA_FILE))) {
			bank.clear();
			JsonObject data = parser.parse(strIn).getAsJsonObject();
			data.entrySet().forEach(e -> bank.put(e.getKey(), new MutableLong(e.getValue().getAsLong())));
		} catch (Exception ex) {
			TiaBot.logger.severe("Failed to load economy data!");
			ex.printStackTrace();
		}
	}
	
	private static MutableLong getBitCount(String id) {
		MutableLong cnt = bank.get(id);
		if (cnt == null) {
			cnt = new MutableLong(0);
			bank.put(id, cnt);
			save();
		}
		return cnt;
	}
	
	public static long getBits(IUser user) {
		return getBitCount(user.getID()).getValue();
	}
	
	public static void addBits(IUser user, long amt) {
		MutableLong cnt = getBitCount(user.getID());
		cnt.setValue(Math.max(cnt.getValue() + amt, 0));
		save();
	}
	
	public static void removeBits(IUser user, long amt) {
		addBits(user, -amt);
	}
	
	public static Stream<Entry<String, Long>> streamBankData() {
		return bank.entrySet().stream()
				.map(e -> new SimpleEntry<>(e.getKey(), e.getValue().getValue()));
	}
	
}
