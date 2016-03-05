package io.github.phantamanta44.tiabot.module.encounter.data;

import java.util.function.Consumer;

import io.github.phantamanta44.tiabot.module.encounter.data.EncounterDamage.Element;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.IDamageable;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.IModifierSusceptible;

public class EncounterEffect implements IModifierSusceptible {

	private EffectType type;
	private int ttl;
	
	public EncounterEffect(EffectType type, int turns) {
		this.type = type;
		this.ttl = turns;
	}
	
	public EncounterEffect(String type, int turns) {
		this(EffectType.valueOf(type), turns);
	}

	@Override
	public int getDamageModifier() {
		return type.atkMod;
	}

	@Override
	public int getDefenseModifier() {
		return type.defMod;
	}

	public boolean proc(IDamageable target) {
		type.effect.accept(target);
		return --ttl <= 0;
	}
	
	public static enum EffectType {
		
		POISON(0, 0, t -> new EncounterDamage(80, Element.POISON).dealTo(t)),
		BURN(0, 0, t -> new EncounterDamage(100, Element.FIRE).dealTo(t)),
		FREEZE(0, 0, t -> new EncounterDamage(35, Element.ICE).dealTo(t)),
		SHOCK(0, 0, t -> new EncounterDamage(20, Element.ENERGY).dealTo(t)),
		REND(0, -15, t -> new EncounterDamage(50, Element.TRUE).dealTo(t)),
		STUN(0, 0, t -> {}),
		SILENCE(0, 0, t -> {}),
		SHIELD(0, 20, t -> {}),
		WRATH(30, 0, t -> {});
		
		public final int atkMod, defMod;
		public final Consumer<IDamageable> effect;
		
		private EffectType(int aM, int dM, Consumer<IDamageable> eff) {
			atkMod = aM;
			defMod = dM;
			effect = eff;
		}
		
	}

}
