package io.github.phantamanta44.tiabot.module.econ;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.econ.command.CommandBitBalance;
import io.github.phantamanta44.tiabot.module.econ.command.CommandBitData;
import io.github.phantamanta44.tiabot.module.econ.command.CommandBitSend;
import io.github.phantamanta44.tiabot.module.econ.command.CommandBitTop;

public class EconModule extends CTModule {

	public EconModule() {
		commands.add(new CommandBitBalance());
		commands.add(new CommandBitData());
		commands.add(new CommandBitSend());
		commands.add(new CommandBitTop());
	}
	
	@Override
	public String getName() {
		return "econ";
	}
	
	@Override
	public String getDesc() {
		return "Provides a currency system.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		EconData.load();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		EconData.save();
	}

}
