package io.github.phantamanta44.tiabot.util;

public interface IFuture<T> {

	public void dispatch();
	
	public boolean isDone();
	
	public T getResult();
	
}
