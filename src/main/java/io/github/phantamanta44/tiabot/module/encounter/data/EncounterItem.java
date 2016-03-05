package io.github.phantamanta44.tiabot.module.encounter.data;

import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.module.encounter.BattleContext;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ICriticalChance;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.IModifierSusceptible;
import io.github.phantamanta44.tiabot.module.encounter.script.EncounterScript;
import io.github.phantamanta44.tiabot.util.ISerializable;
import io.github.phantamanta44.tiabot.util.SafeJsonWrapper;

public class EncounterItem implements IModifierSusceptible, ICriticalChance, ISerializable {

	private String name, desc;
	private int atk, def, ap, health;
	private double crit, critDmg;
	private double lifeSteal;
	private double armorPen;
	private int mana, manaGen;
	private String passive, active;
	
	public EncounterItem(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		SafeJsonWrapper stats = data.getJsonObject("stats");
		SafeJsonWrapper props = data.getJsonObject("props");
		name = data.getString("name");
		desc = data.getString("desc");
		atk = stats.getInt("atk");
		def = stats.getInt("def");
		ap = stats.getInt("ap");
		health = stats.getInt("hp");
		crit = stats.getDouble("critCnc");
		critDmg = stats.getDouble("critDmg");
		lifeSteal = stats.getDouble("lifeSteal");
		armorPen = stats.getDouble("armorPen");
		mana = stats.getInt("manaPool");
		manaGen = stats.getInt("manaReg");
		if (props.containsKey("active"))
			active = props.getString("active");
		if (props.containsKey("passive"))
			passive = props.getString("passive");
	}
	
	@Override
	public JsonObject serialize() {
		JsonObject ser = new JsonObject();
		ser.addProperty("name", name);
		ser.addProperty("desc", desc);
		JsonObject stats = new JsonObject();
		stats.addProperty("atk", atk);
		stats.addProperty("def", def);
		stats.addProperty("ap", ap);
		stats.addProperty("hp", health);
		stats.addProperty("critCnc", crit);
		stats.addProperty("critDmg", critDmg);
		stats.addProperty("lifeSteal", lifeSteal);
		stats.addProperty("armorPen", armorPen);
		stats.addProperty("manaPool", mana);
		stats.addProperty("manaReg", manaGen);
		ser.add("stats", stats);
		JsonObject props = new JsonObject();
		if (active != null)
			props.addProperty("active", active);
		if (passive != null)
			props.addProperty("passive", passive);
		ser.add("props", props);
		return ser;
	}

	@Override
	public int getDamageModifier() {
		return atk;
	}
	
	public int getAbilityPower() {
		return ap;
	}

	@Override
	public int getDefenseModifier() {
		return def;
	}

	@Override
	public double getCritChance() {
		return crit;
	}

	@Override
	public double getCritModifier() {
		return critDmg;
	}
	
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getHealth() {
		return health;
	}

	public double getLifeSteal() {
		return lifeSteal;
	}

	public double getArmorPen() {
		return armorPen;
	}

	public int getMana() {
		return mana;
	}

	public int getManaGen() {
		return manaGen;
	}
	
	public boolean hasPassive() {
		return passive != null;
	}
	
	public void procPassive(BattleContext ctx, StatsDto stats) {
		EncounterScript.execute(passive, ctx.getContext(), ctx, stats);
	}
	
	public boolean hasActive() {
		return active != null;
	}
	
	public void procActive(BattleContext ctx, StatsDto stats) {
		EncounterScript.execute(active, ctx.getContext(), ctx, stats);
	}

}
