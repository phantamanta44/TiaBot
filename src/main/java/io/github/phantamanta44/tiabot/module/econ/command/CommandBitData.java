package io.github.phantamanta44.tiabot.module.econ.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.econ.EconData;
import sx.blah.discord.handle.obj.IUser;

public class CommandBitData implements ICommand {
	
	@Override
	public String getName() {
		return "bitdata";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Save or load economy data.";
	}

	@Override
	public String getUsage() {
		return "bitdata save|reload";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify an action!");
			return;
		}
		if (args[0].equalsIgnoreCase("save")) {
			EconData.save();
			ctx.sendMessage("Economy data saved.");
		}
		else if (args[0].equals("load")) {
			EconData.load();
			ctx.sendMessage("Economy data reloaded.");
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
