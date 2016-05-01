package io.github.phantamanta44.tiabot;

import io.github.phantamanta44.tiabot.core.EventDispatcher;
import io.github.phantamanta44.tiabot.core.command.CommandDispatcher;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory.PoolType;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory.QueueType;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent.Reason;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Discord {
	
	private static final Discord instance = new Discord();
	private static ScheduledExecutorService taskPool;
	
	public static Discord getInstance() {
		return instance;
	}
	
	private IDiscordClient dcCli;
	private Runnable readyCb;
	
	static {
		taskPool = new ThreadPoolFactory()
				.withPool(PoolType.SCHEDULED)
				.withQueue(QueueType.CACHED)
				.construct();
	}
	
	public Discord buildClient(String token) throws DiscordException {
		TiaBot.logger.info("Building Discord API...");
		ClientBuilder cb = new ClientBuilder();
		dcCli = cb.withToken(token).build();
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
	
	@EventSubscriber
	public void onDisconnect(DiscordDisconnectedEvent event) {
		if (event.getReason() != Reason.LOGGED_OUT) {
			TiaBot.logger.warn("Disconnected from Discord! Attempting to reconnect...");
			attemptReconnect(0L);
		}
	}
	
	public void attemptReconnect(long delay) {
		if (!dcCli.isReady())
			taskPool.schedule(() -> {
				try {
					Discord.getInstance().dcCli.login();
				} catch (Exception ex) {
					TiaBot.logger.warn("Could not reconnect: %s", ex.getMessage());
					TiaBot.logger.warn("Trying again in 15 seconds...");
					Discord.getInstance().attemptReconnect(15000L);
				}
			}, delay, TimeUnit.MILLISECONDS);
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
	
	public IChannel getChannelById(String id) {
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

	public IInvite getInviteByCode(String code) {
		return dcCli.getInviteForCode(code);
	}

}
