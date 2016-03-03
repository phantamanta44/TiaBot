package io.github.phantamanta44.tiabot.module.scripting.host;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.core.IMessageable;
import io.github.phantamanta44.tiabot.module.scripting.DelegateStream;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class HostObjectDiscordAPI extends ScriptableObject {

	private static final long serialVersionUID = 1L;
	private List<String> msgBuffer = new ArrayList<>();
	
	@Override
	public String getClassName() {
		return "DiscordAPI";
	}
	
	@JSFunction
	public void print(Object obj) {
		msgBuffer.add(Context.toString(obj));
	}
	
	@JSGetter
	public DelegateStream<HostObjectGuild> guilds() {
		return new DelegateStream<>(Discord.getInstance().getGuilds().stream()
				.map(g -> HostObjectGuild.impl(g, ScriptableObject.getTopLevelScope(this))));
	}
	
	@JSGetter
	public DelegateStream<HostObjectUser> users() {
		return new DelegateStream<>(Discord.getInstance().getUsers().stream()
				.map(u -> HostObjectUser.impl(u, ScriptableObject.getTopLevelScope(this))));
	}
	
	@JSGetter
	public DelegateStream<HostObjectChannel> channels() {
		return new DelegateStream<>(Discord.getInstance().getChannels().stream()
				.map(c -> HostObjectChannel.impl(c, ScriptableObject.getTopLevelScope(this))));
	}
	
	@JSFunction
	public HostObjectGuild getGuild(String id) {
		IGuild guild = Discord.getInstance().getGuildById(id);
		if (guild == null)
			return null;
		return HostObjectGuild.impl(guild, ScriptableObject.getTopLevelScope(this));
	}
	
	@JSFunction
	public HostObjectUser getUser(String id) {
		IUser user = Discord.getInstance().getUserById(id);
		if (user == null)
			return null;
		return HostObjectUser.impl(user, ScriptableObject.getTopLevelScope(this));
	}
	
	@JSFunction
	public HostObjectChannel getChannel(String id) {
		IChannel channel = Discord.getInstance().getChannelById(id);
		if (channel == null)
			return null;
		return HostObjectChannel.impl(channel, ScriptableObject.getTopLevelScope(this));
	}
	
	public void flushBuffer(IMessageable chan) {
		if (msgBuffer.isEmpty())
			return;
		String toSend = msgBuffer.stream()
				.reduce((a, b) -> a.concat("\n").concat(b)).orElse("");
		if (toSend.isEmpty())
			return;
		chan.sendMessage(toSend);
	}
	
}
