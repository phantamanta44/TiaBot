package io.github.phantamanta44.tiabot.module.random.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import io.github.phantamanta44.tiabot.util.data.CollectionUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandDecide implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"choose"});
	
	@Override
	public String getName() {
		return "decide";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Pick between multiple decisions.";
	}

	@Override
	public String getUsage() {
		return "decide <choice> or <choice> [or <choice>...]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 3) {
			ctx.sendMessage("You must supply at least two choices, separated by \"or\"!");
			return;
		}
		String msg = MessageUtils.concat(args).trim();
		if (msg.startsWith("or") || msg.endsWith("or")) {
			ctx.sendMessage("You can't have a trailing \"or\"!");
			return;
		}
		String[] choices = msg.split("or");
		ctx.sendMessage("I choose **%s**!", CollectionUtils.any(choices).trim());
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
