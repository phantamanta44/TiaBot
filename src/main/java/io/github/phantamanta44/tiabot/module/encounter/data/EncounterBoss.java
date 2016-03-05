package io.github.phantamanta44.tiabot.module.encounter.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.module.encounter.data.EncounterDamage.Element;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITargetable;
import io.github.phantamanta44.tiabot.util.ChanceList;
import io.github.phantamanta44.tiabot.util.ISerializable;
import io.github.phantamanta44.tiabot.util.MathUtils;
import io.github.phantamanta44.tiabot.util.SafeJsonWrapper;

public class EncounterBoss implements ITargetable, ISerializable, Cloneable {
	
	private String name;
	private int xpWorth;
	private int hp, maxHp;
	private int atk, def;
	private ChanceList<EncounterItem> drops;
	private ChanceList<EncounterSpell> spells;
	private List<EncounterEffect> status;
	private Element element;
	
	public EncounterBoss(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		name = data.getString("name");
		xpWorth = data.getInt("xp");
		hp = maxHp = data.getInt("hp");
		atk = data.getInt("atk");
		def = data.getInt("def");
		drops = new ChanceList<>();
		spells = new ChanceList<>();
		status = new ArrayList<>();
		data.getJsonArray("drops").forEach(i -> drops.addOutcome(new EncounterItem(i.getAsJsonObject())));
		data.getJsonArray("spells").forEach(a -> spells.addOutcome(new EncounterSpell(a.getAsJsonObject())));
		element = Element.valueOf(data.getString("element"));
	}
	
	private EncounterBoss(EncounterBoss orig) {
		this.name = orig.name;
		this.hp = orig.hp;
		this.maxHp = orig.maxHp;
		this.drops = orig.drops;
		this.spells = orig.spells;
		this.status = orig.status;
		this.atk = orig.atk;
		this.def = orig.def;
		this.element = orig.element;
		this.xpWorth = orig.xpWorth;
	}
	
	@Override
	public JsonObject serialize() {
		JsonObject ser = new JsonObject();
		ser.addProperty("name", name);
		ser.addProperty("xp", xpWorth);
		ser.addProperty("hp", maxHp);
		ser.addProperty("atk", atk);
		ser.addProperty("def", def);
		ser.addProperty("element", element.toString());
		JsonArray dropList = new JsonArray();
		drops.stream().forEach(d -> dropList.add(d.serialize()));
		ser.add("drops", dropList);
		JsonArray spellList = new JsonArray();
		spells.stream().forEach(d -> spellList.add(d.serialize()));
		ser.add("spells", spellList);
		return ser;
	}
	
	public EncounterItem getDrop(Random rand) {
		return drops.getAtRandom(rand);
	}
	
	public EncounterSpell getSpell(Random rand) {
		return spells.getAtRandom(rand);
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int getHealth() {
		return hp;
	}

	@Override
	public int getMaxHealth() {
		return maxHp;
	}

	@Override
	public void addHealth(int health) {
		hp = MathUtils.clamp(hp + health, 0, maxHp);
	}

	@Override
	public void setHealth(int health) {
		hp = MathUtils.clamp(health, 0, maxHp);
	}
	
	@Override
	public EncounterBoss clone() {
		return new EncounterBoss(this);
	}
	
	@Override
	public void addStatusEffect(EncounterEffect effect) {
		status.add(effect);
	}
	
	@Override
	public void applyEffects() {
		status.removeIf(e -> e.proc(this));
	}

	@Override
	public int getDamageModifier() {
		return status.stream()
				.reduce(atk, (m, e) -> m + e.getDamageModifier(), (a, b) -> a + b);
	}

	@Override
	public int getDefenseModifier() {
		return status.stream()
				.reduce(def, (m, e) -> m + e.getDefenseModifier(), (a, b) -> a + b);
	}

	@Override
	public Element getElement() {
		return element;
	}
	
}
