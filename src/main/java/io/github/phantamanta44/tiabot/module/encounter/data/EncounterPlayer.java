package io.github.phantamanta44.tiabot.module.encounter.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.EventDispatcher;
import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.BattleContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterDamage.Element;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ICriticalChance;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITargetable;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITurnable;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.TurnFuture;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler;
import io.github.phantamanta44.tiabot.util.MathUtils;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import io.github.phantamanta44.tiabot.util.data.ISerializable;
import io.github.phantamanta44.tiabot.util.data.SafeJsonWrapper;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class EncounterPlayer implements ITurnable, ICriticalChance, ISerializable, ICTListener {

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
		w.addProperty("cooldown", 5);
		w.addProperty("type", "SINGLE_TEAMMATE");
		w.addProperty("script", "bctx.getTarget().addStatusEffect(new EncounterEffect(EncounterEffect.EffectType.SHIELD, 3))");
		e.addProperty("name", "Oceanic Barrage");
		e.addProperty("desc", "Launches a volley of pressurized fluid that deals **60 + 30% AP + 15% AD** water damage to each opponent.");
		e.addProperty("cost", 35);
		e.addProperty("cooldown", 3);
		e.addProperty("type", "ALL_TARGET");
		e.addProperty("script", "EncounterDamage.noCrit(bctx.getTarget(), 60 + 0.3 * stats.ap + 0.15 * stats.atk, EncounterDamage.Element.WATER, stats, bctx.getSource());");
		r.addProperty("name", "The Tempest");
		r.addProperty("desc", "Batters the opponents with a freezing storm that deals **240 + 275% AP** ice damage and freezes for 1 turn.");
		r.addProperty("cost", 100);
		r.addProperty("cooldown", 15);
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
		mana = baseMana = 127;
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
	
	public void setMana(int mana) {
		this.mana = mana;
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
			if (amt > getExpNeeded() - xp) {
				amt -= getExpNeeded() - xp;
				levelUp();
			}
			else {
				xp += amt;
				amt = 0;
			}
		}
		EncounterData.save();
	}
	
	public void levelUp() {
		xp = 0;
		level++;
		baseHp = (int)(640D + Math.sqrt(level * 270) * 21D);
		baseAtk = (int)(59D + Math.sqrt(level * 3) * 9D);
		baseDef = (int)(29D + Math.sqrt(level * 3) * 4.1D);
		baseAp = (int)(19D + Math.sqrt(level * 50) * 1.4D);
		baseMana = (int)(127D + Math.sqrt(level * 6) * 20D);
		baseManaGen = 3 + (int)Math.floor(Math.sqrt(level) / 1.8D);
	}

	public String getName() {
		return userName;
	}
	
	public String getId() {
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
	
	private static final String STAT_FORMAT = new StringBuilder()
			.append("__**Stats:**__\n")
			.append("Attack Damage: %d\n")
			.append("Ability Power: %d\n")
			.append("Armor: %d\n")
			.append("Armor Penetration: %.0f%%\n")
			.append("Life Steal: %.0f%%\n")
			.append("Critical Strike Chance: %.0f%%\n")
			.append("Critical Strike Damage: %.0f%%")
			.toString();
	
	private volatile AtomicBoolean done = new AtomicBoolean(false);
	private Predicate<IEventContext> expected = null;
	private EncounterContext ec;
	private Map<String, MutableInt> cd = new HashMap<>();
	
	@ListenTo
	public void onMessage(MessageReceivedEvent event, IEventContext ctx) {
		if (ctx.getChannel().isPrivate()
				|| !ctx.getChannel().getID().equalsIgnoreCase(EncounterHandler.getEncChannel(userId))
				|| !ctx.getUser().getID().equalsIgnoreCase(userId))
			return;
		if (expected != null) {
			if (expected.test(ctx)) {
				synchronized (done) {
					done.set(true);
					done.notify();
				}
			}
			expected = null;
			return;
		}
		String msg = ctx.getMessage().getContent();
		boolean isDone = false;
		if (msg.toLowerCase().startsWith("attack")) {
			ctx.sendMessage("Who do you want to attack?");
			expected = c -> {
				try {
					ITargetable target = ec.enemies.stream()
							.filter(e -> MessageUtils.lenientMatch(e.getName(), c.getMessage().getContent()))
							.findAny().get();
					autoAtk.applyEffect(new BattleContext(this, target, ctx), new StatsDto(this));
					ctx.sendMessage("%s attacks %s!", getName(), target.getName());
					return true;
				} catch (NoSuchElementException ex) {
					ctx.sendMessage("Target doesn't exist!");
					return false;
				}
			};
		}
		else if (msg.toLowerCase().startsWith("cast")) {
			try {
				String spellName = msg.split("\\s", 2)[1];
				EncounterSpell spell = Arrays.stream(baseKit)
						.filter(s -> MessageUtils.lenientMatch(s.getName(), spellName))
						.findAny().get();
				MutableInt cdVal;
				if (!cd.containsKey(spell.getName()))
					cd.put(spell.getName(), new MutableInt(0));
				else if ((cdVal = cd.get(spell.getName())).getValue() > 0) {
					ctx.sendMessage("%s is still on cooldown for %d turns!", spell.getName(), cdVal.getValue());
					return;
				}
				if (mana < spell.getManaCost()) {
					ctx.sendMessage("You don't have enough mana to cast %s!", spell.getName());
					return;
				}
				StatsDto stats = new StatsDto(this);
				switch (spell.getSpellType()) {
				case ALL_TARGET:
					ec.enemies.forEach(e -> spell.applyEffect(new BattleContext(this, e, ctx), stats));
					mana -= spell.getManaCost();
					cd.get(spell.getName()).setValue(spell.getCooldown());
					ctx.sendMessage("%s cast %s!", getName(), spell.getName());
					isDone = true;
					break;
				case ALL_TEAMMATE:
					ec.allies.forEach(a -> spell.applyEffect(new BattleContext(this, a, ctx), stats));
					mana -= spell.getManaCost();
					cd.get(spell.getName()).setValue(spell.getCooldown());
					ctx.sendMessage("%s cast %s!", getName(), spell.getName());
					isDone = true;
					break;
				case ALL_UNIT:
					ec.allies.forEach(a -> spell.applyEffect(new BattleContext(this, a, ctx), stats));
					ec.enemies.forEach(e -> spell.applyEffect(new BattleContext(this, e, ctx), stats));
					mana -= spell.getManaCost();
					cd.get(spell.getName()).setValue(spell.getCooldown());
					ctx.sendMessage("%s cast %s!", getName(), spell.getName());
					isDone = true;
					break;
				case SELF:
					spell.applyEffect(new BattleContext(this, this, ctx), stats);
					mana -= spell.getManaCost();
					cd.get(spell.getName()).setValue(spell.getCooldown());
					ctx.sendMessage("%s cast %s!", getName(), spell.getName());
					isDone = true;
					break;
				case SINGLE_TARGET:
					ctx.sendMessage("Who do you want to cast %s on?", spell.getName());
					expected = c -> {
						try {
							ITargetable target = ec.enemies.stream()
									.filter(e -> MessageUtils.lenientMatch(e.getName(), c.getMessage().getContent()))
									.findAny().get();
							spell.applyEffect(new BattleContext(this, target, ctx), stats);
							mana -= spell.getManaCost();
							cd.get(spell.getName()).setValue(spell.getCooldown());
							ctx.sendMessage("%s cast %s on %s!", getName(), spell.getName(), target.getName());
							return true;
						} catch (NoSuchElementException ex) {
							ctx.sendMessage("Target doesn't exist!");
							return false;
						}
					};
					break;
				case SINGLE_TEAMMATE:
					ctx.sendMessage("Who do you want to cast %s on?", spell.getName());
					expected = c -> {
						try {
							ITargetable target = ec.allies.stream()
									.filter(e -> MessageUtils.lenientMatch(e.getName(), c.getMessage().getContent()))
									.findAny().get();
							spell.applyEffect(new BattleContext(this, target, ctx), stats);
							mana -= spell.getManaCost();
							cd.get(spell.getName()).setValue(spell.getCooldown());
							ctx.sendMessage("%s cast %s on %s!", getName(), spell.getName(), target.getName());
							return true;
						} catch (NoSuchElementException ex) {
							ctx.sendMessage("Target doesn't exist!");
							return false;
						}
					};
					break;
				case SINGLE_UNIT:
					ctx.sendMessage("Who do you want to cast %s on?", spell.getName());
					expected = c -> {
						try {
							ITargetable target = Stream.concat(ec.allies.stream(), ec.enemies.stream())
									.filter(e -> MessageUtils.lenientMatch(e.getName(), c.getMessage().getContent()))
									.findAny().get();
							spell.applyEffect(new BattleContext(this, target, ctx), stats);
							mana -= spell.getManaCost();
							cd.get(spell.getName()).setValue(spell.getCooldown());
							ctx.sendMessage("%s cast %s on %s!", getName(), spell.getName(), target.getName());
							return true;
						} catch (NoSuchElementException ex) {
							ctx.sendMessage("Target doesn't exist!");
							return false;
						}
					};
					break;
				}
			} catch (NoSuchElementException ex) {
				ctx.sendMessage("You don't know a spell by this name!");
			} catch (IndexOutOfBoundsException ex) {
				ctx.sendMessage("You must specify a spell to use!");
			}
		}
		else if (msg.toLowerCase().startsWith("item")) {
			try {
				String itemName = msg.split("\\s", 2)[1];
				EncounterItem item = inv.stream()
						.filter(i -> MessageUtils.lenientMatch(i.getName(), itemName))
						.findAny().get();
				if (!item.hasActive()) {
					ctx.sendMessage("%s doesn't have an active!", item.getName());
					return;
				}
				MutableInt cdVal;
				if (!cd.containsKey(item.getName()))
					cd.put(item.getName(), new MutableInt(0));
				else if ((cdVal = cd.get(item.getName())).getValue() > 0) {
					ctx.sendMessage("%s is still on cooldown for %d turns!", item.getName(), cdVal.getValue());
					return;
				}
				ctx.sendMessage("Who do you want to use %s on?", item.getName());
				expected = c -> {
					try {
						ITargetable target = ec.enemies.stream()
								.filter(e -> MessageUtils.lenientMatch(e.getName(), c.getMessage().getContent()))
								.findAny().get();
						item.procActive(new BattleContext(this, target, ctx), new StatsDto(this));
						cd.get(item.getName()).setValue(4);
						ctx.sendMessage("%s used %s on %s!", getName(), item.getName(), target.getName());
						return true;
					} catch (NoSuchElementException ex) {
						ctx.sendMessage("Target doesn't exist!");
						return false;
					}
				};
			} catch (NoSuchElementException ex) {
				ctx.sendMessage("You don't have an item by this name!");
			} catch (IndexOutOfBoundsException ex) {
				ctx.sendMessage("You must specify an item to use!");
			}
		}
		else if (msg.toLowerCase().startsWith("spells")) {
			ctx.sendMessage("__**Spellbook:**__\n%s", Arrays.stream(baseKit)
					.map(s -> String.format("**%s** *(%s Mana) (%s Cooldown)*\n%s", s.getName(), s.getManaCost(), s.getCooldown(), s.getDesc()))
					.reduce((a, b) -> a.concat("\n\n").concat(b)).get());
		}
		else if (msg.toLowerCase().startsWith("inv")) {
			ctx.sendMessage("__**Inventory:**__\n%s\nUse `%sencitem <item>` to learn more about an item.", inv.stream()
					.map(i -> i.getName())
					.reduce((a, b) -> a.concat(", ").concat(b)).orElse("(You have no items.)"), TiaBot.getPrefix());
		}
		else if (msg.toLowerCase().startsWith("stats"))
			ctx.sendMessage(STAT_FORMAT, getDamageModifier(), getAbilityPower(), getDefenseModifier(), getArmorPen() * 100D, getLifeSteal() * 100D, getCritChance() * 100D, getCritModifier() * 100D);
		else if (msg.toLowerCase().startsWith("skip")) {
			ctx.sendMessage("%s skipped their turn.", getName());
			isDone = true;
		}
		if (isDone) {
			synchronized (done) {
				done.set(true);
				done.notify();
			}
		}
	}
	
	@Override
	public TurnFuture onTurn(IEventContext ctx, Random rand, EncounterContext ec) {
		this.ec = ec;
		done.set(false);
		expected = null;
		EventDispatcher.registerHandler(this);
		return new TurnFuture(() -> {
			mana = Math.min(mana + getManaGen(), getMaxMana());
			applyEffects();
			inv.forEach(i -> {
				if (i.hasPassive())
					i.procPassive(new BattleContext(this, this, ctx), new StatsDto(this));
			});
			cd.forEach((k, v) -> v.setValue(Math.max(v.getValue() - 1, 0)));
			ctx.sendMessage("%s\n%s", getStatusMsg(),
					"\nValid actions: `attack`, `cast <spell>`, `item <item>`, `spells`, `inv`, `stats`, `skip`");
			synchronized (done) {
				while (!done.get() && !Thread.currentThread().isInterrupted()) {
					try {
						done.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
			cancelTurn();
		});
	}
	
	private String getStatusMsg() {
		String effList = status.stream()
				.map(e -> String.format("[%s %d]", e.getType(), e.getDuration()))
				.reduce((a, b) -> a + ' ' + b).orElse("");
		if (!effList.isEmpty())
			effList = ' ' + effList;
		String cdList = cd.entrySet().stream()
				.filter(c -> c.getValue().getValue() > 0)
				.map(c -> String.format("[%s %d]", c.getKey(), c.getValue().getValue()))
				.reduce((a, b) -> a + ' ' + b).orElse("");
		if (!cdList.isEmpty())
			cdList = ' ' + cdList;
		return String.format("**%s** [HP: %d/%d] [Mana: %d/%d (+%d)]%s%s",
				getName(), getHealth(), getMaxHealth(), mana, getMaxMana(), getManaGen(), effList, cdList);
	}

	@Override
	public void cancelTurn() {
		EventDispatcher.unregisterHandler(this);
	}
	
	public void resetCooldowns() {
		cd.forEach((k, v) -> v.setValue(0));
	}

}
