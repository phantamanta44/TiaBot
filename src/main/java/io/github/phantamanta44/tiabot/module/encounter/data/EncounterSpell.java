package io.github.phantamanta44.tiabot.module.encounter.data;

import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.module.encounter.BattleContext;
import io.github.phantamanta44.tiabot.module.encounter.script.EncounterScript;
import io.github.phantamanta44.tiabot.util.data.ISerializable;
import io.github.phantamanta44.tiabot.util.data.SafeJsonWrapper;

public class EncounterSpell implements ISerializable {

	private String name, desc;
	private int mana, cooldown;
	private SpellType type;
	private String effect;
	
	public EncounterSpell(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		name = data.getString("name");
		desc = data.getString("desc");
		mana = data.getInt("cost");
		cooldown = data.getInt("cooldown");
		type = SpellType.valueOf(data.getString("type"));
		effect = data.getString("script");
	}
	
	@Override
	public JsonObject serialize() {
		JsonObject ser = new JsonObject();
		ser.addProperty("name", name);
		ser.addProperty("desc", desc);
		ser.addProperty("cost", mana);
		ser.addProperty("cooldown", cooldown);
		ser.addProperty("type", type.toString());
		ser.addProperty("script", effect);
		return ser;
	}
	
	public void applyEffect(BattleContext ctx, StatsDto stats) {
		EncounterScript.execute(effect, ctx.getContext(), ctx, stats);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public int getManaCost() {
		return mana;
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public SpellType getSpellType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "encspell$" + name;
	}
	
	public static enum SpellType {
		
		SINGLE_TARGET, ALL_TARGET,		// Target opponents
		SINGLE_TEAMMATE, ALL_TEAMMATE,	// Target teammates
		SINGLE_UNIT, ALL_UNIT,			// Target anything
		SELF;							// Self-cast
		
	}

}
