package io.github.phantamanta44.tiabot.module.core;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.core.command.CommandEngInvoc;
import io.github.phantamanta44.tiabot.module.core.command.CommandGameSet;
import io.github.phantamanta44.tiabot.module.core.command.CommandHalt;
import io.github.phantamanta44.tiabot.module.core.command.CommandHelp;
import io.github.phantamanta44.tiabot.module.core.command.CommandInfo;
import io.github.phantamanta44.tiabot.module.core.command.CommandModList;
import io.github.phantamanta44.tiabot.module.core.command.CommandModToggle;
import io.github.phantamanta44.tiabot.module.core.command.CommandPrefix;
import io.github.phantamanta44.tiabot.module.core.command.CommandUnsay;
import io.github.phantamanta44.tiabot.module.core.event.RevokeHandler;

public class CoreModule extends CTModule {

	public CoreModule() {
		commands.add(new CommandEngInvoc());
		commands.add(new CommandGameSet());
		commands.add(new CommandHalt());
		commands.add(new CommandHelp());
		commands.add(new CommandInfo());
		commands.add(new CommandModList());
		commands.add(new CommandModToggle());
		commands.add(new CommandPrefix());
		commands.add(new CommandUnsay());
		listeners.add(new RevokeHandler());
	}
	
	@Override
	public String getName() {
		return "core";
	}

	@Override
	public String getDesc() {
		return "Pseudo-module that provides the core of TiaBot.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}
	
	@Override
	public void onDisable() {
		throw new UnsupportedOperationException();
	}

}
