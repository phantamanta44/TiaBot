package io.github.phantamanta44.tiabot.core.command;

import java.util.List;

import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

public interface ICommand {

	public String getName();
	
	public List<String> getAliases();
	
	public String getDesc();
	
	public String getUsage();
	
	public void execute(IUser sender, String[] args, IEventContext ctx);
	
	public boolean canUseCommand(IUser sender, IEventContext ctx);
	
	public String getPermissionMessage(IUser sender, IEventContext ctx);
	
	public String getEnglishInvocation();
	
}