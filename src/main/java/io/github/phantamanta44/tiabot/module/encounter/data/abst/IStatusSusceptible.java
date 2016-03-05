package io.github.phantamanta44.tiabot.module.encounter.data.abst;

import io.github.phantamanta44.tiabot.module.encounter.data.EncounterEffect;

public interface IStatusSusceptible extends IModifierSusceptible {

	public void addStatusEffect(EncounterEffect effect);
	
	public void applyEffects();
	
}
