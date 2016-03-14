package io.github.phantamanta44.tiabot.util;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.TiaBot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

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

	public static IUser resolveMention(String men) {
		if (!men.matches("<@\\S+>"))
			return null;
		return Discord.getInstance().getUserById(men.substring(2, men.length() - 1));
	}
	
	public static String formatDuration(long time) {
		long millis = time;
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		StringBuilder b = new StringBuilder();
		if (days > 0)
			b.append(days).append(" Days, ");
		if (hours > 0)
			b.append(hours).append(" Hours, ");
		if (minutes > 0)
			b.append(minutes).append(" Minutes, ");
		return b.append(seconds).append(" Seconds").toString();
	}
	
	public static boolean lenientMatch(String matcher, String toTest) {
		return matcher.equalsIgnoreCase(toTest)
				|| matcher.replaceAll("\\s", "").equalsIgnoreCase(toTest)
				|| matcher.replaceAll("\\W", "").equalsIgnoreCase(toTest);
	}

	public static String capitalize(String str) {
		if (str.length() < 1)
			return str;
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static String rightPad(String str, int len, char c) {
		if (str.length() >= len)
			return str;
		StringBuilder ret = new StringBuilder(str);
		while (ret.length() < len)
			ret.append(c);
		return ret.toString();
	}
	
	public static String rightPad(String str, int len) {
		return rightPad(str, len, ' ');
	}
	
	public static String leftPad(String str, int len, char c) {
		if (str.length() >= len)
			return str;
		StringBuilder ret = new StringBuilder();
		while (ret.length() < len - str.length())
			ret.append(c);
		return ret.append(str).toString();
	}
	
	public static String leftPad(String str, int len) {
		return leftPad(str, len, ' ');
	}
	
}
