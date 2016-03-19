package io.github.phantamanta44.tiabot.module.fandom;

import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.fandom.command.CommandAnimeChar;
import io.github.phantamanta44.tiabot.module.fandom.command.CommandBestPony;

public class FandomModule extends CTModule {

	public FandomModule() {
		commands.add(new CommandAnimeChar());
		commands.add(new CommandBestPony());
	}
	
	@Override
	public String getName() {
		return "fandom";
	}
	
	@Override
	public String getDesc() {
		return "The manifestation of Tumblr.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}

}
