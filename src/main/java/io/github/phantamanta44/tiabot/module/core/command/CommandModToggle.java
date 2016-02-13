package io.github.phantamanta44.tiabot.module.core.command;

import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.ModuleManager;
import sx.blah.discord.handle.obj.IUser;

public class CommandModToggle implements ICommand {

	@Override
	public String getName() {
		return "modtoggle";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Enable/disable a module.";
	}

	@Override
	public String getUsage() {
		return "modtoggle <module>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You need to specify a module!");
			return;
		}
		if (!ModuleManager.isModule(args[0])) {
			ctx.sendMessage("Not a valid module!");
			return;
		}
		String id = args[0].toLowerCase();
		boolean newState = !ModuleManager.isEnabled(id);
		ModuleManager.setState(id, newState);
		ctx.sendMessage("%s the %s module.", newState ? "Enabled" : "Disabled", id.toLowerCase());
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
