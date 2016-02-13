package io.github.phantamanta44.tiabot.module.scripting.host;

import java.util.stream.Stream;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class HostObjectGuild extends ScriptableObject {

	private static final long serialVersionUID = 1L;
	private IGuild dataSrc;
	
	public HostObjectGuild() {
		// NO-OP
	}
	
	public static HostObjectGuild impl(IGuild src, Scriptable scope) {
		HostObjectGuild inst = (HostObjectGuild)Context.getCurrentContext().newObject(scope, "Guild");
		inst.dataSrc = src;
		return inst;
	}
	
	@Override
	public String getClassName() {
		return "Guild";
	}
	
	@JSGetter
	public String name() {
		return dataSrc.getName();
	}
	
	@JSGetter
	public String id() {
		return dataSrc.getID();
	}
	
	@JSGetter
	public Stream<HostObjectUser> bannedUsers() throws Exception {
		Scriptable scope = ScriptableObject.getTopLevelScope(this);
		return dataSrc.getBannedUsers().stream()
				.map(u -> HostObjectUser.impl(u, scope));
	}
	
	@JSFunction
	public HostObjectChannel getChannel(String id) {
		IChannel chan = dataSrc.getChannelByID(id);
		if (chan == null)
			return null;
		return HostObjectChannel.impl(chan, ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public Stream<HostObjectChannel> channels() {
		Scriptable scope = ScriptableObject.getTopLevelScope(this);
		return dataSrc.getChannels().stream()
				.map(c -> HostObjectChannel.impl(c, scope));
	}
	
	@JSGetter
	public String iconUrl() {
		return dataSrc.getIconURL();
	}
	
	@JSGetter
	public HostObjectUser owner() {
		return HostObjectUser.impl(dataSrc.getOwner(), ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public String region() {
		return dataSrc.getRegion().getName();
	}
	
	@JSFunction
	public HostObjectRole getRole(String id) {
		IRole role = dataSrc.getRoleForID(id);
		if (role == null)
			return null;
		return HostObjectRole.impl(role, ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public Stream<HostObjectRole> roles() {
		Scriptable scope = ScriptableObject.getTopLevelScope(this);
		return dataSrc.getRoles().stream()
				.map(r -> HostObjectRole.impl(r, scope));
	}
	
	@JSFunction
	public HostObjectUser getUser(String id) {
		IUser user = dataSrc.getUserByID(id);
		if (user == null)
			return null;
		return HostObjectUser.impl(user, ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public Stream<HostObjectUser> users() {
		Scriptable scope = ScriptableObject.getTopLevelScope(this);
		return dataSrc.getUsers().stream()
				.map(u -> HostObjectUser.impl(u, scope));
	}
	
	public IGuild getDataSrc() {
		return dataSrc;
	}

}
