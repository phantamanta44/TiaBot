package io.github.phantamanta44.tiabot.module.casino.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.casino.event.LotteryHandler;
import io.github.phantamanta44.tiabot.module.econ.EconData;
import sx.blah.discord.handle.obj.IUser;

public class CommandBitLottery implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"bitlotto"});
	
	@Override
	public String getName() {
		return "bitlottery";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Put some bits up for lottery.";
	}

	@Override
	public String getUsage() {
		return "bitlottery <#amount> <#tickets> <#ticketprice>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (ctx.getChannel().isPrivate()) {
			ctx.sendMessage("Lotteries cannot be started in private channels!");
			return;
		}
		if (LotteryHandler.hasLottery(ctx.getChannel())) {
			ctx.sendMessage("There is already a lottery in progress in this channel!");
			return;
		}
		if (args.length < 3) {
			ctx.sendMessage("Not enough arguments!");
			return;
		}
		long amount, ticketPrc;
		int ticketCnt;
		try {
			amount = Long.parseLong(args[0]);
			ticketPrc = Long.parseLong(args[2]);
			ticketCnt = Integer.parseInt(args[1]);
			if (ticketPrc < 0 || ticketCnt < 1 || amount < 1)
				return;
		} catch (NumberFormatException ex) {
			ctx.sendMessage("Incorrectly formatted arguments!");
			return;
		}
		if (EconData.getBits(sender) < amount) {
			ctx.sendMessage("You can't afford to start this lottery!");
			return;
		}
		EconData.removeBits(sender, amount);
		LotteryHandler.newLottery(ctx, amount, ticketPrc, ticketCnt);
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
