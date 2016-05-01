package io.github.phantamanta44.tiabot.core.context;

import sx.blah.discord.api.Event;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class EventContextGuild implements IEventContext {

	private long timestamp;
	private IGuild guild;
	private Class<? extends Event> eventType;
	
	public EventContextGuild(IGuild source, Class<? extends Event> clazz) {
		timestamp = System.currentTimeMillis();
		guild = source;
		eventType = clazz;
	}
	
	@Override
	public void sendMessage(String msg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendMessage(String format, Object... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public Class<? extends Event> getType() {
		return eventType;
	}

	@Override
	public IGuild getGuild() {
		return guild;
	}

	@Override
	public IChannel getChannel() {
		return null;
	}

	@Override
	public IUser getUser() {
		return null;
	}

	@Override
	public IMessage getMessage() {
		return null;
	}

}
