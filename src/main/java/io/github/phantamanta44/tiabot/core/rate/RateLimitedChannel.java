package io.github.phantamanta44.tiabot.core.rate;

import com.github.fge.lambdas.Throwing;
import com.github.fge.lambdas.runnable.ThrowingRunnable;
import com.github.fge.lambdas.supplier.ThrowingSupplier;
import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.util.concurrent.IFuture;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MissingPermissionsException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class RateLimitedChannel implements IChannel {

	private static final RateLimitQueue queue = new RateLimitQueue();
	
	private final IChannel parent;
	
	public RateLimitedChannel(IChannel channel) {
		parent = channel;
	}
	
	private <T> T queueBlocking(ThrowingSupplier<T> action, long delay) {
		TiaBot.logger.info("Encountered a 429! Queueing previous action...");
		IFuture<T> block = queue.push(Throwing.supplier(action));
		queue.setActive(delay);
		synchronized (block) {
			while (!block.isDone()) {
				try {
					block.wait();
				} catch (InterruptedException e) { }
			}
		}
		return block.getResult();
	}
	
	private void queueBlocking(ThrowingRunnable action, long delay) {
		queueBlocking(() -> {
			action.doRun();
			return null;
		}, delay);
	}
	
	@Override
	public IMessage sendMessage(String content) throws MissingPermissionsException, DiscordException {
		try {
			return parent.sendMessage(content);
		} catch (HTTP429Exception ex) {
			return queueBlocking(() -> sendMessage(content), ex.getRetryDelay());
		}
	}

	@Override
	public IMessage sendMessage(String content, boolean tts) throws MissingPermissionsException, DiscordException {
		try {
			return parent.sendMessage(content, tts);
		} catch (HTTP429Exception ex) {
			return queueBlocking(() -> sendMessage(content, tts), ex.getRetryDelay());
		}
	}

	@Override
	public IMessage sendFile(File file, String s) throws IOException, MissingPermissionsException, DiscordException {
		try {
			return parent.sendFile(file, s);
		} catch (HTTP429Exception ex) {
			return queueBlocking(() -> sendFile(file, s), ex.getRetryDelay());
		}
	}

	@Override
	public IMessage sendFile(File file) throws IOException, MissingPermissionsException, DiscordException {
		try {
			return parent.sendFile(file);
		} catch (HTTP429Exception ex) {
			return queueBlocking(() -> sendFile(file), ex.getRetryDelay());
		}
	}

	@Override
	public IInvite createInvite(int maxAge, int maxUses, boolean temporary, boolean useXkcdPass)
			throws MissingPermissionsException, DiscordException {
		try {
			return parent.createInvite(maxAge, maxUses, temporary, useXkcdPass);
		} catch (HTTP429Exception ex) {
			return queueBlocking(() -> createInvite(maxAge, maxUses, temporary, useXkcdPass), ex.getRetryDelay());
		}
	}

	@Override
	public void changeName(String name) throws DiscordException, MissingPermissionsException {
		try {
			parent.changeName(name);
		} catch (HTTP429Exception ex) {
			queueBlocking(() -> changeName(name), ex.getRetryDelay());
		}
	}

	@Override
	public void changePosition(int position) throws DiscordException, MissingPermissionsException {
		try {
			parent.changePosition(position);
		} catch (HTTP429Exception ex) {
			queueBlocking(() -> changePosition(position), ex.getRetryDelay());
		}
	}

	@Override
	public void changeTopic(String topic) throws DiscordException, MissingPermissionsException {
		try {
			parent.changeTopic(topic);
		} catch (HTTP429Exception ex) {
			queueBlocking(() -> changeTopic(topic), ex.getRetryDelay());
		}
	}

	@Override
	public void delete() throws MissingPermissionsException, DiscordException {
		try {
			parent.delete();
		} catch (HTTP429Exception ex) {
			queueBlocking(this::delete, ex.getRetryDelay());
		}
	}
	
	@Override
	public List<IInvite> getInvites() throws DiscordException {
		try {
			return parent.getInvites();
		} catch (HTTP429Exception ex) {
			return queueBlocking(this::getInvites, ex.getRetryDelay());
		}
	}

	// Delegated methods

	@Override
	public LocalDateTime getCreationDate() {
		return parent.getCreationDate();
	}

	@Override
	public IDiscordClient getClient() {
		return parent.getClient();
	}

	@Override
	public String getName() {
		return parent.getName();
	}

	@Override
	public String getID() {
		return parent.getID();
	}

	@Override
	public IMessage getMessageByID(String messageID) {
		return parent.getMessageByID(messageID);
	}

	@Override
	public IGuild getGuild() {
		return parent.getGuild();
	}

	@Override
	public boolean isPrivate() {
		return parent.isPrivate();
	}

	@Override
	public String getTopic() {
		return parent.getTopic();
	}

	@Override
	public String mention() {
		return parent.mention();
	}

	@Override
	public void toggleTypingStatus() {
		parent.toggleTypingStatus();
	}

	@Override
	public boolean getTypingStatus() {
		return parent.getTypingStatus();
	}

	@Override
	public int getPosition() {
		return parent.getPosition();
	}

	@Override
	public Map<String, PermissionOverride> getUserOverrides() {
		return parent.getUserOverrides();
	}

	@Override
	public Map<String, PermissionOverride> getRoleOverrides() {
		return parent.getRoleOverrides();
	}

	@Override
	public EnumSet<Permissions> getModifiedPermissions(IUser user) {
		return parent.getModifiedPermissions(user);
	}

	@Override
	public EnumSet<Permissions> getModifiedPermissions(IRole role) {
		return parent.getModifiedPermissions(role);
	}

	@Override
	public MessageList getMessages() {
		return parent.getMessages();
	}

	@Override
	public void removePermissionsOverride(IUser user)
			throws MissingPermissionsException, HTTP429Exception, DiscordException {
		parent.removePermissionsOverride(user);
	}

	@Override
	public void removePermissionsOverride(IRole role)
			throws MissingPermissionsException, HTTP429Exception, DiscordException {
		parent.removePermissionsOverride(role);
	}

	@Override
	public void overrideRolePermissions(IRole role, EnumSet<Permissions> toAdd, EnumSet<Permissions> toRemove)
			throws MissingPermissionsException, HTTP429Exception, DiscordException {
		parent.overrideRolePermissions(role, toAdd, toRemove);
	}

	@Override
	public void overrideUserPermissions(IUser user, EnumSet<Permissions> toAdd, EnumSet<Permissions> toRemove)
			throws MissingPermissionsException, HTTP429Exception, DiscordException {
		parent.overrideUserPermissions(user, toAdd, toRemove);
	}

}
