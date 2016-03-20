package io.github.phantamanta44.tiabot.module.econplus.command;

import java.util.Arrays;
import java.util.List;

import com.github.fge.lambdas.Throwing;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.econplus.StockBank;
import io.github.phantamanta44.tiabot.module.econplus.StockData;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandStockPortfolio implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"stportfolio"});
	
	@Override
	public String getName() {
		return "stockportfolio";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Check a user's owned stock shares.";
	}

	@Override
	public String getUsage() {
		return "stockportfolio [@user]";
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
		String stocks = StockBank.getPortfolio(target).entrySet().stream()
				.map(Throwing.function(e -> {
					try {
						StockData quote = StockData.getQuote(e.getKey());
						return String.format("- **%s | %s**: %d shares (%d bits)",
								quote.getSymbol(), quote.getName(), e.getValue(), (long)Math.floor(quote.getPrice() * (float)e.getValue()));
					} catch (Exception ex) {
						return String.format("- **%s**: %d shares", e.getKey(), e.getValue());
					}
				}))
				.reduce((a, b) -> a.concat("\n").concat(b)).orElse("This user owns no stocks.");
		ctx.sendMessage("__**Stock Portfolio: %s**__\n%s", target.getName(), stocks);
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
