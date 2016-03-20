package io.github.phantamanta44.tiabot.util.concurrent;

public interface IFuture<T> {

	public void dispatch();
	
	public boolean isDone();
	
	public T getResult();
	
}
