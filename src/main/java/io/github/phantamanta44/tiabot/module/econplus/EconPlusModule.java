package io.github.phantamanta44.tiabot.module.econplus;
import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.econplus.command.CommandBitLottery;
import io.github.phantamanta44.tiabot.module.econplus.command.CommandStockData;
import io.github.phantamanta44.tiabot.module.econplus.command.CommandStockMarket;
import io.github.phantamanta44.tiabot.module.econplus.command.CommandStockPortfolio;
import io.github.phantamanta44.tiabot.module.econplus.event.LotteryHandler;

public class EconPlusModule extends CTModule {

	public EconPlusModule() {
		commands.add(new CommandBitLottery());
		commands.add(new CommandStockData());
		commands.add(new CommandStockMarket());
		commands.add(new CommandStockPortfolio());
		listeners.add(new LotteryHandler());
	}
	
	@Override
	public String getName() {
		return "econplus";
	}
	
	@Override
	public String getDesc() {
		return "Things to do with bits.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		StockBank.load();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		StockBank.save();
	}

}
