package io.github.phantamanta44.tiabot.module.econplus.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.econplus.StockBank;
import sx.blah.discord.handle.obj.IUser;

public class CommandStockData implements ICommand {
	
	@Override
	public String getName() {
		return "stockdata";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Save or load stock portfolio data.";
	}

	@Override
	public String getUsage() {
		return "stockdata save|reload";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify an action!");
			return;
		}
		if (args[0].equalsIgnoreCase("save")) {
			StockBank.save();
			ctx.sendMessage("Stock portfolio data saved.");
		}
		else if (args[0].equals("load")) {
			StockBank.load();
			ctx.sendMessage("Stock portfolio data reloaded.");
		}
		else
			ctx.sendMessage("Invalid action requested!");
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return TiaBot.isAdmin(sender);
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		return "No permission!";
	}
	
	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
