package io.github.phantamanta44.tiabot.module.core.command;

import java.lang.management.ManagementFactory;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

public class CommandInfo implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"uptime", "about", "stats"});
	
	@Override
	public String getName() {
		return "info";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Gets information about the bot.";
	}

	@Override
	public String getUsage() {
		return "info";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		List<Entry<String, Object>> info = new ArrayList<>();
		info.add(new SimpleEntry<>("Uptime", getUptime()));
		info.add(new SimpleEntry<>("Servers", Discord.getInstance().getGuilds().size()));
		info.add(new SimpleEntry<>("Channels", Discord.getInstance().getChannels().size()));
		info.add(new SimpleEntry<>("Users", Discord.getInstance().getUsers().size()));
		Runtime rt = Runtime.getRuntime();
		info.add(new SimpleEntry<>("Used Mem", String.format("%.2f/%.2fMB", (rt.totalMemory() - rt.freeMemory()) / 1000000F, rt.totalMemory() / 1000000F)));
		String infoStr = info.stream()
				.map(e -> e.getKey().concat(": ").concat(String.valueOf(e.getValue())))
				.reduce((a, b) -> a.concat("\n").concat(b)).get();
		ctx.sendMessage("**Bot Information:**\n```%s```\nSource code available at https://github.com/phantamanta44/TiaBot", infoStr);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}
	
	private static String getUptime() {
		long millis = ManagementFactory.getRuntimeMXBean().getUptime();
		
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		return String.format("%s Days, %s Hours, %s Minutes, %s Seconds", days, hours, minutes, seconds);
	}
	
	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
