package io.github.phantamanta44.tiabot.util;

import java.util.Arrays;

import io.github.phantamanta44.tiabot.TiaBot;
import sx.blah.discord.handle.obj.IChannel;

public class MessageUtils {
	
	public static String concat(String[] parts) {
		return Arrays.stream(parts).reduce((a, b) -> a.concat(" ").concat(b)).orElse("");
	}
	
	public static void sendMessage(IChannel channel, String msg) {
		try {				
			channel.sendMessage(msg);
		} catch (Exception ex) {
			TiaBot.logger.warn("Could not send message \"%s\"!", msg);
			ex.printStackTrace();
		}
	}
	
}
