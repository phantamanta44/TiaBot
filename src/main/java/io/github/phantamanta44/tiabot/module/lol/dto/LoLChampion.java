package io.github.phantamanta44.tiabot.module.lol.dto;

import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.util.SafeJsonWrapper;

public class LoLChampion {

	private int id;
	private String name, title, key;
	private String icon;
	
	public LoLChampion(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		id = data.getInt("id");
		name = data.getString("name");
		title = data.getString("title");
		key = data.getString("key");
		icon = data.getJsonObject("image").getString("full");
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getKey() {
		return key;
	}

	public String getIcon() {
		return icon;
	}

}
