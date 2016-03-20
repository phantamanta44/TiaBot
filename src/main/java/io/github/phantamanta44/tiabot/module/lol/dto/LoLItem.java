package io.github.phantamanta44.tiabot.module.lol.dto;

import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.module.lol.LoLModule;
import io.github.phantamanta44.tiabot.util.data.SafeJsonWrapper;

public class LoLItem {

	public static final LoLItem NONE = new LoLItem();
	
	private int id;
	private String name, desc, statDesc, group;
	private int costBuy, costSell, costComb;
	private String icon;
	
	public LoLItem(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		SafeJsonWrapper cost = data.getJsonObject("gold");
		id = data.getInt("id");
		name = data.getString("name");
		desc = data.getString("plaintext");
		statDesc = data.getString("description")
				.replaceAll("<br>", "\n")
				.replaceAll("</?(?:unique|passive|aura|active)>", "**")
				.replaceAll("</?grouplimit>", "*")
				.replaceAll("</?\\w+(?: \\S+=\\S+)*/?>", "");
		group = data.getString("group");
		costBuy = cost.getInt("total");
		costSell = cost.getInt("sell");
		costComb = cost.getInt("base");
		icon = LoLModule.dataDragon("img/item/" + data.getJsonObject("image").getString("full"));
	}
	
	private LoLItem() {
		name = "None";
		id = costBuy = costSell = costComb = 0;
		statDesc = group = icon = "";
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

	public String getStats() {
		return statDesc;
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
