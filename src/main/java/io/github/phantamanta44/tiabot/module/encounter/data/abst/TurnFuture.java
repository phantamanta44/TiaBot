package io.github.phantamanta44.tiabot.module.encounter.data.abst;

import io.github.phantamanta44.tiabot.util.IFuture;

@SuppressWarnings("rawtypes")
public class TurnFuture implements IFuture {

	protected boolean done;
	private Runnable func, cb;
	
	public TurnFuture(Runnable func) {
		this.func = func;
	}
	
	@Override
	public void dispatch() {
		this.func.run();
		this.done = true;
		cb.run();
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public Object getResult() {
		throw new UnsupportedOperationException();
	}

	public void promise(Runnable callback) {
		if (done)
			callback.run();
		else
			this.cb = callback;
	}

}
