package io.github.phantamanta44.tiabot.module.random.command;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandLevelCheck implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"checklevel"});
	
	@Override
	public String getName() {
		return "levelcheck";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Checks a property of a user.";
	}

	@Override
	public String getUsage() {
		return "levelcheck <@user> <property>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 2) {
			ctx.sendMessage("You must specify a user and a property to check!");
			return;
		}
		IUser user = MessageUtils.resolveMention(args[0]);
		if (user == null) {
			ctx.sendMessage("No such user!");
			return;
		}
		String prop = MessageUtils.concat(Arrays.copyOfRange(args, 1, args.length)).trim().toLowerCase();
		Random rand = new Random(prop.hashCode() ^ user.getID().hashCode());
		ctx.sendMessage("%s Level: %.1f%%", MessageUtils.capitalize(prop), rand.nextDouble() * 100D);
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
		return ".*(?:what level(?: of)?|how(?: much)?) (?<a1>.+) (?:is|are|does) (?<a0>\\<@\\S+\\>).*";
	}

}
