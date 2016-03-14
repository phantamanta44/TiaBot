package io.github.phantamanta44.tiabot.module.encounter.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterItem;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandEncItem implements ICommand {
	
	@Override
	public String getName() {
		return "encitem";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Look up an item.";
	}

	@Override
	public String getUsage() {
		return "encitem <item>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You need to specify an item!");
			return;
		}
		String itemName = MessageUtils.concat(args);
		EncounterItem item = EncounterData.getItem(itemName);
		if (item == null) {
			item = EncounterData.getItems().stream()
					.filter(i -> MessageUtils.lenientMatch(i.getName(), itemName))
					.findAny().orElse(null);
		}
		if (item == null) {
			ctx.sendMessage("Nonexistent item!");
			return;
		}
		ctx.sendMessage(String.format("__**%s**__\n%s", item.getName(), item.getDesc()));
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
