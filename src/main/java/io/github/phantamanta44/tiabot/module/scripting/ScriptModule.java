package io.github.phantamanta44.tiabot.module.scripting;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.scripting.command.CommandEval;
import io.github.phantamanta44.tiabot.module.scripting.command.CommandSudo;

public class ScriptModule extends CTModule {

	public ScriptModule() {
		commands.add(new CommandEval());
		commands.add(new CommandSudo());
	}
	
	@Override
	public String getName() {
		return "scripting";
	}

	@Override
	public String getDesc() {
		return "Provides a means of scripting at runtime.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}
	
}
