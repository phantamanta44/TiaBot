package io.github.phantamanta44.tiabot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import io.github.phantamanta44.tiabot.module.ModuleManager;
import io.github.phantamanta44.tiabot.module.core.CoreModule;
import io.github.phantamanta44.tiabot.module.scripting.ScriptModule;
import sx.blah.discord.handle.obj.IUser;

public class TiaBot {
	
	public static final LogWrapper logger = new LogWrapper("TiaBot");
	public static final IniConfig config = new IniConfig("tiabot.conf");
	
	private static final Set<String> controllers = new HashSet<>();
	private static String prefix;
	
	public static void main(String[] args) {
		try {
			config.read();
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
		setPrefix(config.get("prefix"));
		new CoreModule().onEnable();
		ModuleManager.registerModule(new ScriptModule(), config.getBoolean("mod.scripting"));
	}
	
	private static void getAdmins() throws IOException {
		BufferedReader strIn = new BufferedReader(new FileReader(new File("admins.txt")));
		String line;
		while ((line = strIn.readLine()) != null)
			controllers.add(line);
		strIn.close();
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