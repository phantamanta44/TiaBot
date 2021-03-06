package io.github.phantamanta44.tiabot.module.encounter;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncBank;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncCancel;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncDebug;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncInfo;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncItem;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncReload;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncSave;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncounter;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler;

public class EncounterModule extends CTModule {

	public EncounterModule() {
		commands.add(new CommandEncounter());
		commands.add(new CommandEncInfo());
		commands.add(new CommandEncItem());
		commands.add(new CommandEncBank());
		commands.add(new CommandEncCancel());
		commands.add(new CommandEncReload());
		commands.add(new CommandEncSave());
		commands.add(new CommandEncDebug());
		listeners.add(new EncounterHandler());
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		EncounterData.load();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		EncounterData.save();
	}
	
	@Override
	public String getName() {
		return "encounter";
	}
	
	@Override
	public String getDesc() {
		return "An RPG-like turn-based game.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}

}
