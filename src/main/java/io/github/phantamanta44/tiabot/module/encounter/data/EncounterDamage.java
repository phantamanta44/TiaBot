package io.github.phantamanta44.tiabot.module.encounter.data;

import java.util.Arrays;
import java.util.Collection;

import io.github.phantamanta44.tiabot.module.encounter.data.abst.IDamageable;

public class EncounterDamage {
	
	private int baseDmg;
	private Element element;
	
	public EncounterDamage(int damage) {
		this(damage, Element.NORMAL);
	}
	
	public EncounterDamage(int damage, Element type) {
		baseDmg = damage;
		element = type;
	}
	
	public int dealTo(IDamageable target) {
		return dealTo(target, 0D);
	}
	
	public int dealTo(IDamageable target, double armorPen) {
		if (element == Element.TRUE) {
			target.addHealth(-baseDmg);
			return baseDmg;
		}
		double def = target.getDamageModifier() * (1 - armorPen);
		double defMod = def >= 0 ? 100D / (100D + def) : 2 - (100 / (100 - def));
		int dmg = (int)((double)baseDmg * defMod * element.getDamageModifier(target.getElement()));
		target.addHealth(-dmg);
		return dmg;
	}
	
	public static int dmg(IDamageable target, int damage, Element element) {
		return new EncounterDamage(damage, element).dealTo(target);
	}
	
	public static int dmg(IDamageable target, int damage, Element element, StatsDto stats) {
		int modDmg = damage;
		if (Math.random() >= stats.crit)
			damage *= stats.critDmg;
		return new EncounterDamage(modDmg, element).dealTo(target, stats.armorPen);
	}

	public static void dmg(IDamageable target, int damage, Element element, StatsDto stats, IDamageable source) {
		source.addHealth((int)(dmg(target, damage, element, stats) * stats.lifeSteal));
	}
	
	public static void noCrit(IDamageable target, int damage, Element element, StatsDto stats, IDamageable source) {
		source.addHealth((int)(new EncounterDamage(damage, element).dealTo(target) * stats.lifeSteal));
	}

	public static enum Element {
		
		NORMAL("", ""),
		FIRE("ICE:NATURE", "FIRE:EARTH:STEEL"),
		WATER("FIRE:EARTH:STEEL", "WATER:WIND:ICE:NATURE"),
		EARTH("WIND:POISON:FIRE", "EARTH:ENERGY:STEEL:ICE"),
		WIND("EARTH:FIRE:STEEL", "WIND:WATER:POISON"),
		ICE("EARTH:WIND:NATURE", "ICE:STEEL:FIRE:WATER"),
		ENERGY("WATER:STEEL", "ENERGY:EARTH:NATURE"),
		POISON("WATER:NATURE", "POISON:FIRE:EARTH"),
		STEEL("ENERGY:ICE:EARTH", "WATER:ICE:FIRE:LIGHT:DARK"),
		NATURE("WATER:EARTH", "NATURE:ICE:WIND:FIRE"),
		LIGHT("DARK", ""),
		DARK("LIGHT", ""),
		TRUE("", "");
		
		public final Collection<String> strong, weak;
		
		private Element(String s, String w) {
			strong = Arrays.asList(s.split(":"));
			weak = Arrays.asList(w.split(":"));
		}
		
		public double getDamageModifier(Element def) {
			if (strong.contains(def.toString()))
				return 1.5D;
			if (weak.contains(def.toString()))
				return 0.7D;
			return 1.0D;
		}
		
	}
	
}
