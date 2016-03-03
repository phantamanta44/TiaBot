package io.github.phantamanta44.tiabot.module.scripting.host;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import io.github.phantamanta44.tiabot.module.scripting.DelegateStream;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public class HostObjectChannel extends ScriptableObject {

	private static final long serialVersionUID = 1L;
	private IChannel dataSrc;
	
	public HostObjectChannel() {
		// NO-OP
	}
	
	public static HostObjectChannel impl(IChannel src, Scriptable scope) {
		HostObjectChannel inst = (HostObjectChannel)Context.getCurrentContext().newObject(scope, "Channel");
		inst.dataSrc = src;
		return inst;
	}
	
	@Override
	public String getClassName() {
		return "Channel";
	}
	
	@JSGetter
	public HostObjectGuild guild() {
		return HostObjectGuild.impl(dataSrc.getGuild(), ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public String id() {
		return dataSrc.getID();
	}
	
	@JSFunction
	public HostObjectMessage getMessage(String id) {
		IMessage msg = dataSrc.getMessageByID(id);
		if (msg == null)
			return null;
		return HostObjectMessage.impl(msg, ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public DelegateStream<HostObjectMessage> messages() {
		Scriptable scope = ScriptableObject.getTopLevelScope(this);
		return new DelegateStream<>(dataSrc.getMessages().stream()
				.map(m -> HostObjectMessage.impl(m, scope)));
	}
	
	@JSGetter
	public String name() {
		return dataSrc.getName();
	}
	
	@JSGetter
	public int position() {
		return dataSrc.getPosition();
	}
	
	@JSGetter
	public String topic() {
		return dataSrc.getTopic();
	}
	
	// Private is a keyword. Rip
	@JSGetter
	public boolean isPrivate() {
		return dataSrc.isPrivate();
	}
	
	@JSGetter
	public String mention() {
		return dataSrc.mention();
	}

	public IChannel getDataSrc() {
		return dataSrc;
	}

}
