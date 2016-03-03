package io.github.phantamanta44.tiabot.module.scripting.host;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSGetter;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;

public class HostObjectUser extends ScriptableObject {

	private static final long serialVersionUID = 1L;
	private IUser dataSrc;
	
	public HostObjectUser() {
		// NO-OP
	}
	
	public static HostObjectUser impl(IUser src, Scriptable scope) {
		HostObjectUser inst = (HostObjectUser)Context.getCurrentContext().newObject(scope, "User");
		inst.dataSrc = src;
		return inst;
	}
	
	@Override
	public String getClassName() {
		return "User";
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
	public String mention() {
		return dataSrc.mention();
	}
	
	@JSGetter
	public boolean status() {
		return dataSrc.getPresence() == Presences.ONLINE;
	}
	
	@JSGetter
	public String game() {
		return dataSrc.getGame().orElse("");
	}
	
	@JSGetter
	public String avatarUrl() {
		return dataSrc.getAvatarURL();
	}
	
	public IUser getDataSrc() {
		return dataSrc;
	}

}
