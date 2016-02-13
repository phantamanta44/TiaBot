package io.github.phantamanta44.tiabot.module;

import java.util.ArrayList;
import java.util.List;

import io.github.phantamanta44.tiabot.core.EventDispatcher;
import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.command.CommandDispatcher;
import io.github.phantamanta44.tiabot.core.command.ICommand;

public abstract class CTModule {

	protected final List<ICommand> commands = new ArrayList<>();
	protected final List<ICTListener> listeners= new ArrayList<>();
	
	public abstract String getName();
	
	public void onEnable() {
		commands.forEach(c -> CommandDispatcher.registerCommand(c));
		listeners.forEach(l -> EventDispatcher.registerHandler(l));
	}
	
	public void onDisable() {
		commands.forEach(c -> CommandDispatcher.unregisterCommand(c));
		listeners.forEach(l -> EventDispatcher.unregisterHandler(l));
	}
	
}
