package io.github.phantamanta44.tiabot.module.encounter;

import java.util.Arrays;
import java.util.Collection;

import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITargetable;

public class EncounterContext {

	public final Collection<ITargetable> allies, enemies;
	
	public EncounterContext(ITargetable[] allies, ITargetable[] enemies) {
		this.allies = Arrays.asList(allies);
		this.enemies = Arrays.asList(enemies);
	}
	
	public EncounterContext(Collection<ITargetable> allies, Collection<ITargetable> enemies) {
		this.allies = allies;
		this.enemies = enemies;
	}
	
}
