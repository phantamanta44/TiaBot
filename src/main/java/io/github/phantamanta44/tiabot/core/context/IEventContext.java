package io.github.phantamanta44.tiabot.core.context;

import io.github.phantamanta44.tiabot.core.IMessageable;
import sx.blah.discord.handle.Event;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public interface IEventContext extends IMessageable {

	public long getTimestamp();
	
	public Class<? extends Event> getType();
	
	public IGuild getGuild();
	
	public IChannel getChannel();
	
	public IUser getUser();
	
	public IMessage getMessage();
	
}
