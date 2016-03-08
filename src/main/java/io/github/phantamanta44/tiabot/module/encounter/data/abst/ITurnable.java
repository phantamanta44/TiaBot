package io.github.phantamanta44.tiabot.module.encounter.data.abst;

import java.util.Random;

import io.github.phantamanta44.tiabot.util.IFuture;

public interface ITurnable extends ITargetable {

	public String getName();
	
	public IFuture<?> onTurn(Random rand);
	
}