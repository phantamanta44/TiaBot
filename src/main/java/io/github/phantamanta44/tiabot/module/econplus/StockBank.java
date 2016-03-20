package io.github.phantamanta44.tiabot.module.econplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.http.client.ClientProtocolException;

import com.github.fge.lambdas.Throwing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.util.http.HttpException;
import sx.blah.discord.handle.obj.IUser;

public class StockBank {

	private static final File DATA_FILE = new File("stockportfolio.json");
	private static final Map<String, Map<String, MutableInt>> bank = new ConcurrentHashMap<>();
	
	public static void save() {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.disableHtmlEscaping()
				.create();
		try (PrintWriter strOut = new PrintWriter(new FileWriter(DATA_FILE))) {
			JsonObject bankObj = new JsonObject();
			bank.forEach((k, v) -> {
				JsonObject accObj = new JsonObject();
				v.forEach((k2, v2) -> accObj.addProperty(k2, v2.getValue()));
				bankObj.add(k, accObj);
			});
			strOut.println(gson.toJson(bankObj));
		} catch (Exception ex) {
			TiaBot.logger.severe("Failed to save stock portfolio data!");
			ex.printStackTrace();
		}
	}
	
	public static void load() {
		JsonParser parser = new JsonParser();
		try (BufferedReader strIn = new BufferedReader(new FileReader(DATA_FILE))) {
			bank.clear();
			JsonObject data = parser.parse(strIn).getAsJsonObject();
			data.entrySet().forEach(e -> {
				JsonObject o = e.getValue().getAsJsonObject();
				Map<String, MutableInt> acc = new ConcurrentHashMap<>();
				o.entrySet().forEach(e2 -> acc.put(e.getKey(), new MutableInt(e.getValue().getAsInt())));
				bank.put(e.getKey(), acc);
			});
		} catch (Exception ex) {
			TiaBot.logger.severe("Failed to load stock portfolio data!");
			ex.printStackTrace();
		}
	}
	
	private static Map<String, MutableInt> getAccount(String id) {
		Map<String, MutableInt> acc = bank.get(id);
		if (acc == null) {
			acc = new ConcurrentHashMap<>();
			bank.put(id, acc);
			save();
		}
		return acc;
	}
	
	public static Map<String, Integer> getPortfolio(IUser user) {
		Map<String, Integer> ret = new HashMap<>();
		getAccount(user.getID()).forEach((k, v) -> ret.put(k, v.getValue()));
		return ret;
	}
	
	public static int getShares(IUser user, String ticker) {
		MutableInt shares = getAccount(user.getID()).get(ticker);
		return shares == null ? 0 : shares.getValue();
	}
	
	public static void addShares(IUser user, String ticker, int amount) {
		Map<String, MutableInt> acc = getAccount(user.getID());
		MutableInt shares = acc.get(ticker.toUpperCase());
		if (shares == null) {
			shares = new MutableInt(0);
			acc.put(ticker.toUpperCase(), shares);
		}
		shares.setValue(Math.max(shares.getValue() + amount, 0));
		if (shares.getValue() < 1)
			acc.remove(ticker.toUpperCase());
		save();
	}

	public static void removeShares(IUser user, String ticker, int amount) {
		addShares(user, ticker, -amount);
	}
	
	public static float getNetWorth(IUser user) {
		return getAccount(user.getID()).entrySet().stream()
				.map(Throwing.function(e -> getNetWorth(user, e.getKey())))
				.reduce((a, b) -> a + b).orElse(0F);
	}
	
	public static float getNetWorth(IUser user, String ticker) throws ClientProtocolException, HttpException, IOException {
		MutableInt shares = getAccount(user.getID()).get(ticker.toUpperCase());
		if (shares == null)
			return 0F;
		return (float)shares.getValue() * StockData.getQuote(ticker).getPrice();
	}
	
}
