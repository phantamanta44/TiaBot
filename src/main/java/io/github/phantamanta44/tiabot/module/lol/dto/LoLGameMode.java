package io.github.phantamanta44.tiabot.module.lol.dto;

public enum LoLGameMode {

	CLASSIC("Classic Match"),
	ODIN("Dominion Match"),
	ARAM("ARAM Match"),
	TUTORIAL("Tutorial"),
	ONEFORALL("One-for-All Match"),
	ASCENSION("Ascension Match"),
	FIRSTBLOOD("Showdown Match"),
	KINGPORO("King Poro Match");
	
	public final String name;
	
	private LoLGameMode(String name) {
		this.name = name;
	}
	
}
