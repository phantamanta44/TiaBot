package io.github.phantamanta44.tiabot.module.encounter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.module.encounter.data.EncounterItem;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterPlayer;
import io.github.phantamanta44.tiabot.util.data.ISerializable;
import io.github.phantamanta44.tiabot.util.data.SafeJsonWrapper;

public class EncounterBank {
	
	public static final int INV_SIZE = 7;
	public static final Map<String, BankAccount> bank = new ConcurrentHashMap<>();
	
	public static JsonObject serialize() {
		JsonObject bankObj = new JsonObject();
		bank.forEach((k, v) -> bankObj.add(k, v.serialize()));
		return bankObj;
	}
	
	public static void deserialize(JsonObject dto) {
		bank.clear();
		dto.entrySet().forEach(e -> bank.put(e.getKey(), new BankAccount(e.getValue().getAsJsonObject())));
	}
	
	public static BankAccount getAccount(EncounterPlayer player) {
		BankAccount acc = bank.get(player.getId());
		if (acc == null) {
			acc = new BankAccount(player);
			bank.put(player.getId(), acc);
			EncounterData.save();
		}
		return acc;
	}
	
	public static BankStatus addItem(EncounterPlayer player, EncounterItem item) {
		if (player.getInv().size() < INV_SIZE) {
			player.getInv().add(item);
			return BankStatus.ADDED;
		}
		getAccount(player).addItem(item);
		return BankStatus.DEPOSITED;
	}
	
	public static class BankAccount implements ISerializable {
		
		private final Map<String, MutableInt> items = new ConcurrentHashMap<>();
		private final String id;
		
		public BankAccount(EncounterPlayer owner) {
			id = owner.getId();
		}

		public BankAccount(JsonObject dto) {
			items.clear();
			SafeJsonWrapper data = new SafeJsonWrapper(dto);
			id = data.getString("id");
			SafeJsonWrapper itemList = data.getJsonObject("items");
			itemList.getSource().entrySet().forEach(e -> items.put(e.getKey(), new MutableInt(e.getValue().getAsInt())));
		}
		
		@Override
		public JsonObject serialize() {
			JsonObject ser = new JsonObject();
			ser.addProperty("id", id);
			JsonObject itemMap = new JsonObject();
			items.forEach((k, v) -> itemMap.addProperty(k, v.getValue()));
			ser.add("items", itemMap);
			return ser;
		}
		
		public String getId() {
			return id;
		}
		
		public EncounterPlayer getPlayer() {
			return EncounterData.getPlayer(id);
		}
		
		public void addItem(EncounterItem item) {
			addItem(item, 1);
		}
		
		public void addItem(EncounterItem item, int cnt) {
			if (items.containsKey(item.getId()))
				items.get(item.getId()).add(cnt);
			else
				items.put(item.getId(), new MutableInt(cnt));
		}
		
		public BankStatus removeItem(EncounterItem item) {
			return removeItem(item, 1);
		}
		
		public BankStatus removeItem(EncounterItem item, int cnt) {
			MutableInt itemCnt = items.get(item.getId());
			if (itemCnt == null)
				return BankStatus.ERROR;
			itemCnt.setValue(Math.max(itemCnt.getValue() - cnt, 0));
			if (itemCnt.getValue() == 0)
				items.remove(item.getId());
			return BankStatus.REMOVED;
		}
		
		public boolean containsItem(EncounterItem item) {
			return getItemCount(item) > 0;
		}
		
		public int getItemCount(EncounterItem item) {
			MutableInt cnt = items.get(item);
			if (cnt == null)
				return 0;
			return cnt.getValue();
		}
		
		public Map<EncounterItem, Integer> getItems() {
			Map<EncounterItem, Integer> ret = new HashMap<>();
			items.forEach((k, v) -> ret.put(EncounterData.getItem(k), v.getValue()));
			return ret;
		}
		
	}

	public static enum BankStatus {
		ADDED, REMOVED,
		DEPOSITED, ERROR;
	}

}
