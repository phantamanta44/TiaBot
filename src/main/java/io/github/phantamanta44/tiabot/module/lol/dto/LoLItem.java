package io.github.phantamanta44.tiabot.module.lol.dto;

import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.util.SafeJsonWrapper;

public class LoLItem {

	public static final LoLItem NONE = new LoLItem();
	
	private int id;
	private String name, desc, group;
	private int costBuy, costSell, costComb;
	private String icon;
	
	public LoLItem(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		SafeJsonWrapper cost = data.getJsonObject("gold");
		id = data.getInt("id");
		name = data.getString("name");
		desc = data.getString("sanitizedDescription");
		group = data.getString("group");
		costBuy = cost.getInt("total");
		costSell = cost.getInt("sell");
		costComb = cost.getInt("base");
		icon = data.getJsonObject("image").getString("full");
	}
	
	private LoLItem() {
		name = "Nothing";
		id = costBuy = costSell = costComb = 0;
		desc = group = icon = "";
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return desc;
	}

	public String getGroup() {
		return group;
	}

	public int getBuyCost() {
		return costBuy;
	}

	public int getSellCost() {
		return costSell;
	}

	public int getCombineCost() {
		return costComb;
	}

	public String getIcon() {
		return icon;
	}
	
}
