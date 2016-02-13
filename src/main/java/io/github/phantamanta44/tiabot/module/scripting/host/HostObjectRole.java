package io.github.phantamanta44.tiabot.module.scripting.host;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSGetter;

import sx.blah.discord.handle.obj.IRole;

public class HostObjectRole extends ScriptableObject {

	private static final long serialVersionUID = 1L;
	private IRole dataSrc;
	
	public HostObjectRole() {
		// NO-OP
	}
	
	public static HostObjectRole impl(IRole src, Scriptable scope) {
		HostObjectRole inst = (HostObjectRole)Context.getCurrentContext().newObject(scope, "Role");
		inst.dataSrc = src;
		return inst;
	}
	
	@Override
	public String getClassName() {
		return "Role";
	}
	
	@JSGetter
	public String colour() {
		return Integer.toHexString(dataSrc.getColor().getRGB());
	}
	
	@JSGetter
	public HostObjectGuild guild() {
		return HostObjectGuild.impl(dataSrc.getGuild(), ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public String id() {
		return dataSrc.getID();
	}
	
	@JSGetter
	public String name() {
		return dataSrc.getName();
	}
	
	@JSGetter
	public int position() {
		return dataSrc.getPosition();
	}
	
	public IRole getDataSrc() {
		return dataSrc;
	}

}
