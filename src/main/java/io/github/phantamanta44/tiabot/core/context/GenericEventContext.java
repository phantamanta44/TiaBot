package io.github.phantamanta44.tiabot.core.context;

import java.lang.reflect.Method;

import io.github.phantamanta44.tiabot.core.rate.RateLimitedChannel;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.api.Event;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class GenericEventContext implements IEventContext {

	private Class<? extends Event> clazz;
	private long timestamp;
	private IGuild guild;
	private IChannel channel;
	private IUser user;
	private IMessage msg;
	
	public GenericEventContext(Event event) {
		timestamp = System.currentTimeMillis();
		clazz = event.getClass();
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			try {
				if (m.getName().equalsIgnoreCase("getUser"))
					user = (IUser)m.invoke(event);
				else if (m.getName().equalsIgnoreCase("getChannel")) {
					channel = new RateLimitedChannel((IChannel)m.invoke(event));
					guild = channel.getGuild();
				}
				else if (m.getName().equalsIgnoreCase("getMessage")) {
					msg = (IMessage)m.invoke(event);
					user = msg.getAuthor();
					channel = new RateLimitedChannel(msg.getChannel());
					guild = channel.getGuild();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void sendMessage(String msg) {
		if (channel != null)
			MessageUtils.sendMessage(channel, msg);
		else
			throw new UnsupportedOperationException();
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
		return clazz;
	}
	
	@Override
	public IGuild getGuild() {
		return guild;
	}
	
	@Override
	public IChannel getChannel() {
		return channel;
	}
	
	@Override
	public IUser getUser() {
		return user;
	}
	
	@Override
	public IMessage getMessage() {
		return msg;
	}

}
