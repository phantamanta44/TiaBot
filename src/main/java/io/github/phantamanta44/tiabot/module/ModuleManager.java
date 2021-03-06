package io.github.phantamanta44.tiabot.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableBoolean;

public class ModuleManager {

	private static final Map<String, CTModule> modMap = new HashMap<>();
	private static final Map<String, MutableBoolean> status = new HashMap<>();
	
	public static void registerModule(CTModule module, boolean active) {
		modMap.put(module.getName().toLowerCase(), module);
		status.put(module.getName(), new MutableBoolean(active));
		if (active)
			module.onEnable();
	}
	
	public static int getModuleCount() {
		return modMap.size();
	}
	
	public static boolean isEnabled(String moduleId) {
		if (status.containsKey(moduleId))
			return status.get(moduleId).getValue();
		return false;
	}
	
	public static void setState(String modId, boolean state) {
		if (!status.containsKey(modId) || !modMap.containsKey(modId))
			throw new IllegalArgumentException("No such module");
		CTModule mod = modMap.get(modId);
		if (state && !status.get(modId).getValue()) {
			mod.onEnable();
			status.get(modId).setValue(true);
		}
		else if (!state && status.get(modId).getValue()) {
			mod.onDisable();
			status.get(modId).setValue(false);
		}
	}

	public static boolean isModule(String id) {
		return modMap.containsKey(id);
	}
	
	public static CTModule getModule(String id) {
		return modMap.get(id);
	}
	
	public static Stream<Entry<String, CTModule>> streamModules() {
		return modMap.entrySet().stream();
	}

	public static Stream<Entry<String, MutableBoolean>> streamStatus() {
		return status.entrySet().stream();
	}

	public static void onShutdown() {
		try {
			modMap.forEach((k, v) -> {
				try {
					if (isEnabled(k))
						v.onDisable();
				} catch (UnsupportedOperationException ex) { }
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}