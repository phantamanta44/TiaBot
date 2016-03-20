package io.github.phantamanta44.tiabot.module.econ.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.econ.EconData;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandBitSend implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"bitgive"});
	
	@Override
	public String getName() {
		return "bitsend";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Send bits to another user.";
	}

	@Override
	public String getUsage() {
		return "bitsend <@user> <amount>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 2) {
			ctx.sendMessage("Not enough arguments!");
			return;
		}
		IUser target = MessageUtils.resolveMention(args[0]);
		if (target == null) {
			ctx.sendMessage("No such user!");
			return;
		}
		long amount;
		try {
			amount = Long.parseLong(args[1]);
			if (amount < 1)
				throw new NumberFormatException();
		} catch (NumberFormatException ex) {
			ctx.sendMessage("Invalid amount specified!");
			return;
		}
		if (EconData.getBits(sender) < amount) {
			ctx.sendMessage("You don't have enough bits to make this transaction!");
			return;
		}
		EconData.removeBits(target, amount);
		EconData.addBits(target, amount);
		ctx.sendMessage("**Transaction completed successfully.** New balances:\n**%s**: %d\n**%s**: %d",
				sender.getName(), EconData.getBits(sender), target.getName(), EconData.getBits(target));
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
