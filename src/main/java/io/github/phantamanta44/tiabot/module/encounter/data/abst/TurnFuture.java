package io.github.phantamanta44.tiabot.module.encounter.data.abst;

import io.github.phantamanta44.tiabot.util.concurrent.IFuture;

@SuppressWarnings("rawtypes")
public class TurnFuture implements IFuture {

	protected boolean done, cancelled;
	private Runnable func, cb;
	
	public TurnFuture(Runnable func) {
		this.func = func;
	}
	
	@Override
	public void dispatch() {
		this.func.run();
		this.done = true;
		if (!cancelled)
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
		if (cancelled)
			return;
		if (done)
			callback.run();
		else
			this.cb = callback;
	}
	
	public void cancel() {
		cancelled = true;
	}

}
