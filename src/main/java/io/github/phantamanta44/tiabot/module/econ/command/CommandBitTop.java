package io.github.phantamanta44.tiabot.module.econ.command;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.econ.EconData;
import sx.blah.discord.handle.obj.IUser;

public class CommandBitTop implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"bitscoreboard"});
	
	@Override
	public String getName() {
		return "bittop";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "List the top 10 bit balances.";
	}

	@Override
	public String getUsage() {
		return "bittop";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		AtomicInteger i = new AtomicInteger(1);
		ctx.sendMessage("__**Top 10 Bit Balances:**__\n%s", EconData.streamBankData()
				.sorted((a, b) -> (int)(b.getValue() - a.getValue()))
				.limit(10L)
				.map(e -> String.format("%d] **%s** \u2013 %d bits", i.getAndIncrement(), Discord.getInstance().getUserById(e.getKey()).getName(), e.getValue()))
				.reduce((a, b) -> a.concat("\n").concat(b)).orElse("(No data available)"));
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
