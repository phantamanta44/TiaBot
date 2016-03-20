package io.github.phantamanta44.tiabot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.ModuleManager;
import io.github.phantamanta44.tiabot.module.core.CoreModule;
import io.github.phantamanta44.tiabot.module.econ.EconModule;
import io.github.phantamanta44.tiabot.module.econplus.EconPlusModule;
import io.github.phantamanta44.tiabot.module.encounter.EncounterModule;
import io.github.phantamanta44.tiabot.module.fandom.FandomModule;
import io.github.phantamanta44.tiabot.module.lol.LoLModule;
import io.github.phantamanta44.tiabot.module.random.RandomModule;
import io.github.phantamanta44.tiabot.module.scripting.ScriptModule;
import sx.blah.discord.handle.obj.IUser;

public class TiaBot {
	
	public static final LogWrapper logger = new LogWrapper("TiaBot");
	public static final IniConfig config = new IniConfig("tiabot.conf");
	
	private static final File ADMINS_FILE = new File("admins.txt");
	private static final Set<String> controllers = new HashSet<>();
	private static String prefix;
	
	public static void main(String[] args) {
		try {
			config.read();
			setPrefix(config.get("prefix"));
			getAdmins();
			Discord.getInstance()
					.buildClient(config.get("email"), config.get("pass"))
					.onReady(() -> registerModules())
					.login();
		} catch (Exception e) {
			logger.severe("Something went wrong!");
			e.printStackTrace();
		}
	}
	
	private static void registerModules() {
		ModuleManager.registerModule(new CoreModule(), true);
		registerModule(new RandomModule());
		registerModule(new ScriptModule());
		registerModule(new LoLModule());
		registerModule(new EncounterModule());
		registerModule(new FandomModule());
		registerModule(new EconModule());
		registerModule(new EconPlusModule());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> ModuleManager.onShutdown()));
	}
	
	private static void registerModule(CTModule module) {
		ModuleManager.registerModule(module, config.getBoolean("mod." + module.getName()));
	}
	
	private static void getAdmins() {
		try (BufferedReader strIn = new BufferedReader(new FileReader(ADMINS_FILE))) {
			String line;
			while ((line = strIn.readLine()) != null)
				controllers.add(line);
		} catch (IOException ex) {
			logger.severe("Error retrieving admin list!");
			ex.printStackTrace();
		}
	}
	
	public static boolean isAdmin(IUser user) {
		return controllers.contains(user.getName());
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static void setPrefix(String newPrefix) {
		prefix = newPrefix;
	}

}