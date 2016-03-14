package io.github.phantamanta44.tiabot.module.encounter.data.abst;

import java.util.Random;

import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterContext;

public interface ITurnable extends ITargetable {
	
	public TurnFuture onTurn(IEventContext ctx, Random rand, EncounterContext ec);
	
	public void cancelTurn();
	
}
