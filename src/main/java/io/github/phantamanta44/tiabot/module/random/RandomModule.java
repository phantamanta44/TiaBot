package io.github.phantamanta44.tiabot.module.random;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.random.command.CommandBash;
import io.github.phantamanta44.tiabot.module.random.command.CommandDuel;
import io.github.phantamanta44.tiabot.module.random.command.CommandHaiku;
import io.github.phantamanta44.tiabot.module.random.command.CommandKhaled;
import io.github.phantamanta44.tiabot.module.random.command.CommandLevelCheck;
import io.github.phantamanta44.tiabot.module.random.command.CommandRoll;
import io.github.phantamanta44.tiabot.module.random.command.CommandSlap;
import io.github.phantamanta44.tiabot.module.random.event.DuelManager;

public class RandomModule extends CTModule {

	public RandomModule() {
		commands.add(new CommandBash());
		commands.add(new CommandDuel());
		commands.add(new CommandHaiku());
		commands.add(new CommandKhaled());
		commands.add(new CommandLevelCheck());
		commands.add(new CommandRoll());
		commands.add(new CommandSlap());
		listeners.add(new DuelManager());
	}
	
	@Override
	public String getName() {
		return "random";
	}
	
	@Override
	public String getDesc() {
		return "Provides various random features for entertainment.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}

}
