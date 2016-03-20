package io.github.phantamanta44.tiabot.module.econplus.command;

import java.util.Arrays;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.econ.EconData;
import io.github.phantamanta44.tiabot.module.econplus.StockBank;
import io.github.phantamanta44.tiabot.module.econplus.StockData;
import sx.blah.discord.handle.obj.IUser;

public class CommandStockMarket implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"stmarket"});
	
	@Override
	public String getName() {
		return "stockmarket";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Check stock information and buy/sell shares.";
	}

	@Override
	public String getUsage() {
		return "stockmarket (check <symbol>)|(buy|sell <symbol> [#shares])";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 2)
			ctx.sendMessage("You must specify an action and a stock!");
		StockData quote;
		try {
			quote = StockData.getQuote(args[1]);
			if (quote == null) {
				ctx.sendMessage("Stock not found! Did you mistype the ticker symbol?");
				return;
			}
		} catch (Exception ex) {
			ctx.sendMessage("Errored while retrieving stock data!");
			ex.printStackTrace();
			return;
		}
		switch (args[0].toLowerCase()) {
		case "check":
			ctx.sendMessage("__**Stock Details: %s**__\nTicker Symbol: %s\nPrice Per Share: %.2f (%+.2f)\nHigh/Low: %.2f/%.2f\nOpening Price: %.2f",
					quote.getName(), quote.getSymbol(), quote.getPrice(), quote.getChange(), quote.getHighPrice(), quote.getLowPrice(), quote.getOpenPrice());
			break;
		case "buy":
			tryBuy(sender, quote, args, ctx);
			break;
		case "sell":
			trySell(sender, quote, args, ctx);
			break;
		default:
			ctx.sendMessage("Invalid action requested!");
			return;
		}
	}

	private void tryBuy(IUser sender, StockData quote, String[] args, IEventContext ctx) {
		int vol = parseArgs(args);
		if (vol == -1) {
			ctx.sendMessage("Buy volume must be a positive integer!");
			return;
		}
		long price = (long)Math.ceil(quote.getPrice() * (float)vol);
		if (EconData.getBits(sender) < price) {
			ctx.sendMessage("You can't afford to buy %d shares for %d bits!", vol, price);
			return;
		}
		EconData.removeBits(sender, price);
		StockBank.addShares(sender, quote.getSymbol(), vol);
		ctx.sendMessage("Bought %d shares of %s for %d bits.\nNew balance: %d bits\nNew ownership: %d shares",
				vol, quote.getSymbol(), price, EconData.getBits(sender), StockBank.getShares(sender, quote.getSymbol()));
	}
	
	private void trySell(IUser sender, StockData quote, String[] args, IEventContext ctx) {
		int vol = parseArgs(args);
		if (vol == -1) {
			ctx.sendMessage("Sell volume must be a positive integer!");
			return;
		}
		if (StockBank.getShares(sender, quote.getSymbol()) < vol) {
			ctx.sendMessage("You don't own enough shares of %s!", quote.getSymbol());
			return;
		}
		long value = (long)Math.floor(quote.getPrice() * (float)vol);
		EconData.addBits(sender, value);
		StockBank.removeShares(sender, quote.getSymbol(), vol);
		ctx.sendMessage("Sold %d shares of %s for %d bits.\nNew balance: %d bits\nNew ownership: %d shares",
				vol, quote.getSymbol(), value, EconData.getBits(sender), StockBank.getShares(sender, quote.getSymbol()));
	}
	
	private int parseArgs(String[] args) {
		try {
			int ret = Integer.parseInt(args[2]);
			return ret < 1 ? -1 : ret;
		} catch (IndexOutOfBoundsException|NumberFormatException ex) {
			return -1;
		}
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
