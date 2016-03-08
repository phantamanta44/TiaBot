package io.github.phantamanta44.tiabot.module.encounter.data;

public class StatsDto {

	public StatsDto(int atk, int def, int ap, int hp, int maxHp, double crit, double critDmg, double lifeSteal,
			double armorPen, int mana, int maxMana, int manaGen) {
		super();
		this.atk = atk;
		this.def = def;
		this.ap = ap;
		this.hp = hp;
		this.maxHp = maxHp;
		this.crit = crit;
		this.critDmg = critDmg;
		this.lifeSteal = lifeSteal;
		this.armorPen = armorPen;
		this.mana = mana;
		this.maxMana = maxMana;
		this.manaGen = manaGen;
	}

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
