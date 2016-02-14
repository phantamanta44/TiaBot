package io.github.phantamanta44.tiabot.module.random.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.random.event.DuelManager;
import sx.blah.discord.handle.obj.IUser;

public class CommandDuel implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"duel", "fight", "fiteme"});
	
	@Override
	public String getName() {
		return "1v1";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Fight another person in a typing battle.";
	}

	@Override
	public String getUsage() {
		return "1v1 <@person>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		DuelManager.procCmd(sender, args, ctx);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
