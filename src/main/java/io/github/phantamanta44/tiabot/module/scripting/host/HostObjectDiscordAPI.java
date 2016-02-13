package io.github.phantamanta44.tiabot.module.scripting.host;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

import io.github.phantamanta44.tiabot.core.IMessageable;

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
