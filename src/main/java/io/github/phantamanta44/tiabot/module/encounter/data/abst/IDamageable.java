package io.github.phantamanta44.tiabot.module.encounter.data.abst;

import io.github.phantamanta44.tiabot.module.encounter.data.EncounterDamage.Element;

public interface IDamageable extends IModifierSusceptible {

	public int getHealth();
	
	public int getMaxHealth();
	
	public void addHealth(int health);
	
	public void setHealth(int health);
	
	public Element getElement();
	
}
