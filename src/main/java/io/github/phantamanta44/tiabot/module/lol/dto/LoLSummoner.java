package io.github.phantamanta44.tiabot.module.lol.dto;

import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.util.data.SafeJsonWrapper;

public class LoLSummoner {
	
	private long id;
	private String name;
	private int profileIconId;
	private int level;
	private LoLRegion region;

	public LoLSummoner(JsonObject dto, LoLRegion rg) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		id = data.getLong("id");
		name = data.getString("name");
		profileIconId = data.getInt("profileIconId");
		level = data.getInt("summonerLevel");
		region = rg;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getStandardizedName() {
		return name.replaceAll("\\s", "").toLowerCase();
	}
	
	public int getProfileIcon() {
		return profileIconId;
	}
	
	public int getLevel() {
		return level;
	}

	public LoLRegion getRegion() {
		return region;
	}

}
