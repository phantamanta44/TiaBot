package io.github.phantamanta44.tiabot.module.econ.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.econ.EconData;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandBitBalance implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"bitcount", "bitbal", "bitbalance"});
	
	@Override
	public String getName() {
		return "bits";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Check a user's bit balance.";
	}

	@Override
	public String getUsage() {
		return "bits [@user]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		IUser target = sender;
		if (args.length > 0)
			target = MessageUtils.resolveMention(args[0]);
		if (target == null) {
			ctx.sendMessage("No such user!");
			return;
		}
		ctx.sendMessage("**%s has %d bit(s).**", target.getName(), EconData.getBits(target));
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
