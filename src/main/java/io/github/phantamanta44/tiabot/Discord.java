package io.github.phantamanta44.tiabot;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.phantamanta44.tiabot.core.EventDispatcher;
import io.github.phantamanta44.tiabot.core.command.CommandDispatcher;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;

public class Discord {
	
	private static final Discord instance = new Discord();
	
	public static Discord getInstance() {
		return instance;
	}
	
	private IDiscordClient dcCli;
	private Runnable readyCb;
	
	public Discord buildClient(String email, String pass) throws DiscordException {
		TiaBot.logger.info("Building Discord API...");
		ClientBuilder cb = new ClientBuilder();
		dcCli = cb.withLogin(email, pass).build();
		registerListener(this);
		registerListener(new EventDispatcher());
		EventDispatcher.registerHandler(new CommandDispatcher());
		return this;
	}
	
	public Discord onReady(Runnable callback) {
		readyCb = callback;
		return this;
	}
	
	public void login() throws DiscordException {
		TiaBot.logger.info("Attempting login...");
		dcCli.login();
	}
	
	private void registerListener(Object listener) {		
		dcCli.getDispatcher().registerListener(listener);
	}
	
	@EventSubscriber
	public void onReady(ReadyEvent event) {
		readyCb.run();
		TiaBot.logger.info("Logged in as \"%s\". Token: %s", dcCli.getOurUser().getName(), dcCli.getToken());
		setGameCaption(TiaBot.config.get("game"));
	}
	
	public IUser getBot() {
		return dcCli.getOurUser();
	}
	
	public Collection<IGuild> getGuilds() {
		return dcCli.getGuilds();
	}
	
	public IGuild getGuildById(String id) {
		return dcCli.getGuildByID(id);
	}
	
	public Collection<IUser> getUsers() {
		return getGuilds().stream()
				.map(IGuild::getUsers)
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());
	}
	
	public IUser getUserById(String id) {
		return dcCli.getUserByID(id);
	}
	
	public Collection<IChannel> getChannels() {
		return getGuilds().stream()
				.map(IGuild::getChannels)
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());
	}
	
	public IChannel getChannel(String id) {
		return dcCli.getChannelByID(id);
	}

	public IChannel getPrivateChat(IUser user) {
		try {
			return dcCli.getOrCreatePMChannel(user);
		} catch (Exception ex) {
			TiaBot.logger.severe("Error retrieving private channel!");
			ex.printStackTrace();
			return null;
		}
	}

	public void setGameCaption(String gameName) {
		Optional<String> opt;
		if (gameName == null || gameName.isEmpty())
			opt = Optional.empty();
		else
			opt = Optional.of(gameName);
		dcCli.updatePresence(getBot().getPresence() == Presences.IDLE, opt);
	}

}
