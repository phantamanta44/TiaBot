package io.github.phantamanta44.tiabot.module.lol.dto;

public enum LoLRegion {

	BR("Brazil"),
	EUNE("Europe Nordic and East"),
	EUW("Europe West"),
	KR("Korea"),
	LAN("Latin America North"),
	LAS("Latin America South"),
	NA("North America"),
	OCE("Oceania"),
	TR("Turkey"),
	RU("Russia"),
	PBE("Public Beta Environment"),
	GLOBAL("Global");
	
	public final String name;
	
	private LoLRegion(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
	public static LoLRegion parseRegion(String rg) {
		return valueOf(rg.toUpperCase());
	}
	
}
