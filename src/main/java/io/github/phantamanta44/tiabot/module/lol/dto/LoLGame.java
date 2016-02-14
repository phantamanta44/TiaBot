package io.github.phantamanta44.tiabot.module.lol.dto;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.util.SafeJsonWrapper;

public class LoLGame {

	private static final String CS_URL = "http://cs.phanta.xyz/game.html?g=%s&s=%s&t=%s&c=%s";
	
	private long id;
	private long timestamp, dur;
	private boolean win;
	private LoLGamePlayer player;
	private int[] kda;
	private int cs, gold, level;
	private int[] items;
	private Collection<LoLGamePlayer> fellowPlayers;
	private LoLGameMode mode;
	private LoLRegion region;
	
	public LoLGame(JsonObject dto, LoLRegion rg, LoLSummoner summ) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		SafeJsonWrapper stats = data.getJsonObject("stats");
		id = data.getLong("gameId");
		timestamp = data.getLong("createDate");
		dur = stats.getLong("timePlayed") * 1000;
		win = stats.getBoolean("win");
		player = new LoLGamePlayer(summ.getId(), data.getInt("championId"), data.getInt("teamId"));
		kda = new int[] {stats.getInt("championsKilled"), stats.getInt("numDeaths"), stats.getInt("assists")};
		cs = stats.getInt("minionsKilled") + stats.getInt("neutralMinionsKilled");
		gold = stats.getInt("goldEarned");
		level = stats.getInt("level");
		items = new int[7];
		for (int i = 0; i < 7; i++)
			items[i] = stats.getInt("item" + i);
		fellowPlayers = StreamSupport.stream(data.getJsonArray("fellowPlayers").spliterator(), true)
				.map(e -> new LoLGamePlayer(new SafeJsonWrapper(e.getAsJsonObject())))
				.collect(Collectors.toList());
		mode = LoLGameMode.valueOf(data.getString("gameMode"));
		region = rg;
	}
	
	public long getId() {
		return id;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public long getDuration() {
		return dur;
	}
	
	public boolean isWin() {
		return win;
	}
	
	public LoLGamePlayer getPlayer() {
		return player;
	}
	
	public int[] getKda() {
		return kda;
	}
	
	public int getCreepScore() {
		return cs;
	}
	
	public int getGold() {
		return gold;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int[] getItems() {
		return items;
	}
	
	public Collection<LoLGamePlayer> getFellowPlayers() {
		return fellowPlayers;
	}
	
	public LoLGameMode getMode() {
		return mode;
	}

	public LoLRegion getRegion() {
		return region;
	}
	
	public String getChillingSmiteUrl() {
		return String.format(CS_URL, id, region, player.team, player.champ);
	}
	
	public static class LoLGamePlayer {
		
		public final long id;
		public final int champ;
		public final int team;
		
		public LoLGamePlayer(long id, int champId, int teamId) {
			this.id = id;
			this.champ = champId;
			this.team = teamId;
		}
		
		public LoLGamePlayer(SafeJsonWrapper dto) {
			id = dto.getLong("summonerId");
			champ = dto.getInt("championId");
			team = dto.getInt("teamId");
		}
		
	}

}
