package io.github.phantamanta44.tiabot.module.encounter.data;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterDamage.Element;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ICriticalChance;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITurnable;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.TurnFuture;
import io.github.phantamanta44.tiabot.util.IFuture;
import io.github.phantamanta44.tiabot.util.ISerializable;
import io.github.phantamanta44.tiabot.util.MathUtils;
import io.github.phantamanta44.tiabot.util.SafeJsonWrapper;
import sx.blah.discord.handle.obj.IUser;

public class EncounterPlayer implements ITurnable, ICriticalChance, ISerializable {

	private static final EncounterSpell[] BASE_KIT;
	private static final EncounterSpell AUTO_ATK;
	
	static {
		JsonObject auto = new JsonObject(), q = new JsonObject(), w = new JsonObject(), e = new JsonObject(), r = new JsonObject();
		auto.addProperty("name", "Auto Attack");
		auto.addProperty("desc", "Attacks a target.");
		auto.addProperty("cost", 0);
		auto.addProperty("cooldown", 0);
		auto.addProperty("type", "SINGLE_TARGET");
		auto.addProperty("script", "EncounterDamage.dmg(bctx.getTarget(), stats.atk, EncounterDamage.Element.NORMAL, stats, bctx.getSource());");
		AUTO_ATK = new EncounterSpell(auto);
		q.addProperty("name", "Glacial Shard");
		q.addProperty("desc", "Lobs a chunk of magically-augmented ice that deals **80 + 75% AP** ice damage.");
		q.addProperty("cost", 18);
		q.addProperty("cooldown", 3);
		q.addProperty("type", "SINGLE_TARGET");
		q.addProperty("script", "EncounterDamage.noCrit(bctx.getTarget(), 80 + 0.75 * stats.ap, EncounterDamage.Element.ICE, stats, bctx.getSource());");
		w.addProperty("name", "Frigid Bulwark");
		w.addProperty("desc", "Raises a protective sheet of ice that grants **20 Armor** and lasts for 3 turns.");
		w.addProperty("cost", 30);
		q.addProperty("cooldown", 5);
		w.addProperty("type", "SINGLE_TEAMMATE");
		w.addProperty("script", "bctx.getTarget().addStatusEffect(new EncounterEffect(EncounterEffect.EffectType.SHIELD, 3))");
		e.addProperty("name", "Oceanic Barrage");
		e.addProperty("desc", "Launches a volley of pressurized fluid that deals **60 + 30% AP + 15% AD** water damage to each opponent.");
		e.addProperty("cost", 35);
		q.addProperty("cooldown", 3);
		e.addProperty("type", "ALL_TARGET");
		e.addProperty("script", "EncounterDamage.noCrit(bctx.getTarget(), 60 + 0.3 * stats.ap + 0.15 * stats.atk, EncounterDamage.Element.WATER, stats, bctx.getSource());");
		r.addProperty("name", "The Tempest");
		r.addProperty("desc", "Batters the opponents with a freezing storm that deals **240 + 275% AP** ice damage and freezes for 1 turn.");
		r.addProperty("cost", 100);
		q.addProperty("cooldown", 15);
		r.addProperty("type", "ALL_TARGET");
		r.addProperty("script", "EncounterDamage.noCrit(bctx.getTarget(), 240 + 2.75 * stats.ap, EncounterDamage.Element.ICE, stats, bctx.getSource()); bctx.getTarget().addStatusEffect(new EncounterEffect(EncounterEffect.EffectType.FREEZE, 3));");
		BASE_KIT = new EncounterSpell[] {new EncounterSpell(q), new EncounterSpell(w), new EncounterSpell(e), new EncounterSpell(r)};
	}
	
	private String userName, userId;
	private int level, xp;
	private int hp, baseHp;
	private int baseAtk, baseDef, baseAp;
	private int mana, baseMana, baseManaGen;
	private EncounterSpell autoAtk;
	private EncounterSpell[] baseKit;
	private List<EncounterItem> inv = new CopyOnWriteArrayList<>();
	private List<EncounterEffect> status = new CopyOnWriteArrayList<>();
	
	public EncounterPlayer(IUser user) { // TODO Class system
		userName = user.getName();
		userId = user.getID();
		level = 1;
		xp = 0;
		hp = baseHp = 640;
		mana = baseMana = 400;
		baseManaGen = 3;
		baseAtk = 59;
		baseAp = 19;
		baseDef = 29;
		baseKit = BASE_KIT;
		autoAtk = AUTO_ATK;
	}
	
	public EncounterPlayer(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto), stats = data.getJsonObject("stats");
		userName = data.getString("name");
		userId = data.getString("id");
		level = data.getInt("level");
		xp = data.getInt("xp");
		hp = baseHp = stats.getInt("hp");
		mana = baseMana = stats.getInt("mana");
		baseManaGen = stats.getInt("manaGen");
		baseAtk = stats.getInt("atk");
		baseAp = stats.getInt("ap");
		baseDef = stats.getInt("def");
		autoAtk = new EncounterSpell(data.getJsonObject("autoAtk").getSource());
		JsonArray kit = data.getJsonArray("kit");
		baseKit = StreamSupport.stream(kit.spliterator(), false)
				.map(e -> new EncounterSpell(e.getAsJsonObject()))
				.toArray(l -> new EncounterSpell[4]);
		JsonArray items = data.getJsonArray("inv");
		items.forEach(i -> inv.add(EncounterData.getItem(i.getAsString())));
	}
	
	@Override
	public JsonObject serialize() {
		JsonObject ser = new JsonObject();
		ser.addProperty("name", userName);
		ser.addProperty("id", userId);
		ser.addProperty("level", level);
		ser.addProperty("xp", xp);
		JsonObject stats = new JsonObject();
		stats.addProperty("hp", baseHp);
		stats.addProperty("mana", baseMana);
		stats.addProperty("manaGen", baseManaGen);
		stats.addProperty("atk", baseAtk);
		stats.addProperty("ap", baseAp);
		stats.addProperty("def", baseDef);
		ser.add("stats", stats);
		ser.add("autoAtk", autoAtk.serialize());
		JsonArray kit = new JsonArray();
		Arrays.stream(baseKit)
				.forEach(s -> {
					if (s != null)
						kit.add(s.serialize());
				});
		ser.add("kit", kit);
		JsonArray items = new JsonArray();
		inv.forEach(i -> {
			if (i != null)
				items.add(i.getId());
		});
		ser.add("inv", items);
		return ser;
	}

	@Override
	public int getDamageModifier() {
		int items = inv.stream()
				.reduce(baseAtk, (a, b) -> a + b.getDamageModifier(), (a, b) -> a + b);
		return status.stream()
				.reduce(items, (a, b) -> a + b.getDamageModifier(), (a, b) -> a + b);
	}

	@Override
	public int getDefenseModifier() {
		int items = inv.stream()
				.reduce(baseDef, (a, b) -> a + b.getDefenseModifier(), (a, b) -> a + b);
		return status.stream()
				.reduce(items, (a, b) -> a + b.getDefenseModifier(), (a, b) -> a + b);
	}

	@Override
	public double getCritChance() {
		return inv.stream()
				.reduce(0D, (a, b) -> a + b.getCritChance(), (a, b) -> a + b);
	}

	@Override
	public double getCritModifier() {
		return inv.stream()
				.reduce(2D, (a, b) -> a + b.getCritModifier(), (a, b) -> a + b);
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
	public int getHealth() {
		return hp;
	}

	@Override
	public int getMaxHealth() {
		return inv.stream()
				.reduce(baseHp, (a, b) -> a + b.getHealth(), (a, b) -> a + b);
	}

	@Override
	public void addHealth(int health) {
		hp = MathUtils.clamp(hp + health, 0, getMaxHealth());
	}

	@Override
	public void setHealth(int health) {
		hp = MathUtils.clamp(health, 0, getMaxHealth());
	}

	@Override
	public Element getElement() {
		return Element.NORMAL;
	}
	
	public int getAbilityPower() {
		return inv.stream()
				.reduce(baseAp, (a, b) -> a + b.getAbilityPower(), (a, b) -> a + b);
	}
	
	public double getLifeSteal() {
		return inv.stream()
				.reduce(0D, (a, b) -> a + b.getLifeSteal(), (a, b) -> a + b);
	}
	
	public double getArmorPen() {
		return inv.stream()
				.reduce(0D, (a, b) -> a + b.getArmorPen(), (a, b) -> a + b);
	}
	
	public int getMana() {
		return mana;
	}
	
	public int getMaxMana() {
		return inv.stream()
				.reduce(baseMana, (a, b) -> a + b.getMana(), (a, b) -> a + b);
	}
	
	public int getManaGen() {
		return inv.stream()
				.reduce(baseManaGen, (a, b) -> a + b.getManaGen(), (a, b) -> a + b);
	}
	
	public int getExp() {
		return xp;
	}
	
	public int getExpNeeded() {
		return 90 + 3 * level * level;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void addExp(int amt) {
		while (amt > 0) {
			if (amt > getExpNeeded()) {
				amt -= getExpNeeded();
				level++;
				xp = 0;
			}
			else {
				xp += amt;
				amt = 0;
			}
		}
	}

	public String getName() {
		return userName;
	}
	
	public String getID() {
		return userId;
	}
	
	public IUser getUser() {
		return Discord.getInstance().getUserById(userId);
	}

	public List<EncounterItem> getInv() {
		return inv;
	}
	
	public EncounterSpell[] getKit() {
		return baseKit;
	}
	
	public EncounterSpell getAuto() {
		return autoAtk;
	}
	
	public StatsDto getBaseStats() {
		return new StatsDto(baseAtk, baseDef, baseAp, hp, baseHp, 0D, 2D, 0D, 0D, mana, baseMana, baseManaGen);
	}
	
	@Override
	public IFuture<?> onTurn(IEventContext ctx, Random rand) {
		return new TurnFuture(() -> {
			
		});
	}

}
