package io.github.phantamanta44.tiabot.module.encounter.data;

public class StatsDto {

	public static final StatsDto IDENTITY = new StatsDto();
	
	public final int atk, def, ap, hp, maxHp;
	public final double crit, critDmg;
	public final double lifeSteal;
	public final double armorPen;
	public final int mana, maxMana, manaGen;
	
	public StatsDto(EncounterPlayer player) {
		atk = player.getDamageModifier();
		def = player.getDefenseModifier();
		ap = player.getAbilityPower();
		hp = player.getHealth();
		maxHp = player.getMaxHealth();
		crit = player.getCritChance();
		critDmg = player.getCritModifier();
		lifeSteal = player.getLifeSteal();
		armorPen = player.getArmorPen();
		mana = player.getMana();
		maxMana = player.getMaxMana();
		manaGen = player.getManaGen();
	}
	
	private StatsDto() {
		atk = def = ap = hp = maxHp = mana = maxMana = manaGen = 0;
		crit = critDmg = lifeSteal = armorPen = 0D;
	}
	
}
