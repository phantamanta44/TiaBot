package io.github.phantamanta44.tiabot.module.encounter.data.abst;

import io.github.phantamanta44.tiabot.util.IFuture;

@SuppressWarnings("rawtypes")
public class TurnFuture implements IFuture {

	protected boolean done;
	private Runnable callback;
	
	public TurnFuture(Runnable func) {
		this.callback = func;
	}
	
	@Override
	public void dispatch() {
		this.callback.run();
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public Object getResult() {
		throw new UnsupportedOperationException();
	}

}
