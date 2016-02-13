package io.github.phantamanta44.tiabot.core;

public interface IMessageable {

	public void sendMessage(String msg);
	
	public void sendMessage(String format, Object... args);
	
}
