package io.github.phantamanta44.tiabot.module.encounter;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.encounter.command.CommandEncounter;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler;

public class EncounterModule extends CTModule {

	public EncounterModule() {
		commands.add(new CommandEncounter());
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

}
