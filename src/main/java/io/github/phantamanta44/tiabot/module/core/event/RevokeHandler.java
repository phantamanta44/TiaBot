
package io.github.phantamanta44.tiabot.module.core.event;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.MathUtils;
import sx.blah.discord.handle.impl.events.MessageSendEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class RevokeHandler implements ICTListener {

	private static Map<String, Deque<IMessage>> msgStacks = new HashMap<>();
	
	@ListenTo
	public void onMessageSend(MessageSendEvent event, IEventContext ctx) {
		if (ctx.getUser().getID().equalsIgnoreCase(Discord.getInstance().getBot().getID())) {
			String id = ctx.getChannel().getID();
			if (!msgStacks.containsKey(id))
				msgStacks.put(id, new ArrayDeque<>());
			msgStacks.get(id).offer(ctx.getMessage());
		}
	}
	
	public static void procCmd(IUser sender, String[] args, IEventContext ctx) {
		Deque<IMessage> msgStack = msgStacks.get(ctx.getChannel().getID());
		if (msgStack == null)
			return;
		
		int toDelete = 1;
		try {
			toDelete = MathUtils.clamp(Integer.parseInt(args[0]), 1, 10);
		} catch (Exception ex) { }
		
		for (int i = 0; i < toDelete; i++) {
			IMessage td = msgStack.pollLast();
			if (td == null)
				break;
			try {
				td.delete();
			} catch (Exception ex) {
				TiaBot.logger.warn(ex.getMessage());
				toDelete++;
			}
		}
	}
	
}
