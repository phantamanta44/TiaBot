package io.github.phantamanta44.tiabot.module.scripting.host;

import java.time.ZoneOffset;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSGetter;

import io.github.phantamanta44.tiabot.module.scripting.DelegateStream;
import sx.blah.discord.handle.obj.IMessage;

public class HostObjectMessage extends ScriptableObject {

	private static final long serialVersionUID = 1L;
	private IMessage dataSrc;
	
	public HostObjectMessage() {
		// NO-OP
	}
	
	public static HostObjectMessage impl(IMessage src, Scriptable scope) {
		HostObjectMessage inst = (HostObjectMessage)Context.getCurrentContext().newObject(scope, "Message");
		inst.dataSrc = src;
		return inst;
	}
	
	@Override
	public String getClassName() {
		return "Message";
	}
	
	@JSGetter
	public String content() {
		return dataSrc.getContent();
	}
	
	@JSGetter
	public HostObjectUser author() {
		return HostObjectUser.impl(dataSrc.getAuthor(), ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public String id() {
		return dataSrc.getID();
	}
	
	@JSGetter
	public HostObjectChannel channel() {
		return HostObjectChannel.impl(dataSrc.getChannel(), ScriptableObject.getTopLevelScope(this));
	}
	
	@JSGetter
	public long timestamp() {
		return dataSrc.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli();
	}
	
	@JSGetter
	public DelegateStream<HostObjectUser> lol() {
		Scriptable scope = ScriptableObject.getTopLevelScope(this);
		return new DelegateStream<>(dataSrc.getMentions().stream()
				.map(u -> HostObjectUser.impl(u, scope)));
	}
	
	public IMessage getDataSrc() {
		return dataSrc;
	}

}
