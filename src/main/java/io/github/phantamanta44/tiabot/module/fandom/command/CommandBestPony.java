package io.github.phantamanta44.tiabot.module.fandom.command;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.data.CollectionUtils;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.Requests;

public class CommandBestPony implements ICommand {

	private static final String PONY_URL = "https://cdn.rawgit.com/phantamanta44/9f2d882c6560b5662dda/raw/b3e0764b9d2235ff6ea29c42caa63252b2e589a7/ponies.json";
	
	@Override
	public String getName() {
		return "bestpony";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Finds out who best pony is.";
	}

	@Override
	public String getUsage() {
		return "bestpony";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		JsonParser parser = new JsonParser();
		try {
			JsonObject ponies = parser.parse(Requests.GET.makeRequest(PONY_URL)).getAsJsonObject();
			Entry<String, JsonElement> bestPony = CollectionUtils.any(ponies.entrySet());
			ctx.sendMessage("**%s is best pony!**\n%s", bestPony.getKey(), bestPony.getValue().getAsString());
		} catch (Exception ex) {
			ctx.sendMessage("Best pony could not be found!");
			ex.printStackTrace();
		}
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEnglishInvocation() {
		return ".*who is(?: the)? best(?: little| small| smol)? (?:pony|ho(?:rse|ers)(?:fr(?:iend|end?))?).*";
	}

}
