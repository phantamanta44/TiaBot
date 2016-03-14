package io.github.phantamanta44.tiabot.module.lol.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.lol.LoLModule;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLItem;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLRegion;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandLoLItem implements ICommand {
	
	private static final String RESULT_FORMAT = "**League of Legends Item Profile:**\n**%s**\n*%s*\n\n%sg / %sg Crafted\n\n%s\n%s";
	
	@Override
	public String getName() {
		return "lolitem";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Get information about a LoL item.";
	}

	@Override
	public String getUsage() {
		return "lolitem <name|id>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify an item to look up!");
			return;
		}
		LoLItem item;
		if (args[0].matches("\\d+"))
			item = LoLModule.getItem(LoLRegion.NA, Integer.parseInt(args[0]));
		else
			item = LoLModule.getItem(MessageUtils.concat(args));
		if (item == null || item == LoLItem.NONE) {
			ctx.sendMessage("No such item!");
			return;
		}
		ctx.sendMessage(String.format(RESULT_FORMAT, 
				item.getName(),
				item.getDescription(),
				item.getBuyCost(),
				item.getCombineCost(),
				item.getStats(),
				item.getIcon()));
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
		return ".*what (?:lol|league of legends) item is (?<a0>(?:[A-Za-z']+)(?: [A-Za-z']+)*).*";
	}

}
