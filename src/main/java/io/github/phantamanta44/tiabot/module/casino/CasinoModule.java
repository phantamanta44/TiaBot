package io.github.phantamanta44.tiabot.module.casino;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.casino.command.CommandBitLottery;
import io.github.phantamanta44.tiabot.module.casino.event.LotteryHandler;

public class CasinoModule extends CTModule {

	public CasinoModule() {
		commands.add(new CommandBitLottery());
		listeners.add(new LotteryHandler());
	}
	
	@Override
	public String getName() {
		return "casino";
	}
	
	@Override
	public String getDesc() {
		return "Things to do with bits.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}

}
