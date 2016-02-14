package io.github.phantamanta44.tiabot.module.scripting;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.scripting.command.CommandEval;

public class ScriptModule extends CTModule {

	public ScriptModule() {
		commands.add(new CommandEval());
	}
	
	@Override
	public String getName() {
		return "scripting";
	}
	
}
