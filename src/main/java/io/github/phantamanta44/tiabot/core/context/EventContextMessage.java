package io.github.phantamanta44.tiabot.core.context;

import io.github.phantamanta44.tiabot.core.rate.RateLimitedChannel;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.api.Event;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class EventContextMessage implements IEventContext {

	private long timestamp;
	private IMessage message;
	private IChannel channel;
	private Class<? extends Event> eventType;
	
	public EventContextMessage(IMessage msg, Class<? extends Event> clazz) {
		timestamp = System.currentTimeMillis();
		message = msg;
		channel = new RateLimitedChannel(msg.getChannel());
		eventType = clazz;
	}
	
	@Override
	public void sendMessage(String msg) {
		MessageUtils.sendMessage(channel, msg);
	}

	@Override
	public void sendMessage(String format, Object... args) {
		sendMessage(String.format(format, args));
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
		return channel.getGuild();
	}

	@Override
	public IChannel getChannel() {
		return channel;
	}

	@Override
	public IUser getUser() {
		return message.getAuthor();
	}

	@Override
	public IMessage getMessage() {
		return message;
	}

}
