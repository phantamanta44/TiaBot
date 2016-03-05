package io.github.phantamanta44.tiabot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SafeJsonWrapper {
	
	private JsonObject src;
	
	public SafeJsonWrapper(JsonObject src) {
		this.src = src;
	}
	
	public SafeJsonWrapper getJsonObject(String key) {
		try {
			return new SafeJsonWrapper(src.get(key).getAsJsonObject());
		} catch (Exception ex) {
			return new SafeJsonWrapper(new JsonObject());
		}
	}
	
	public JsonArray getJsonArray(String key) {
		try {
			return src.get(key).getAsJsonArray();
		} catch (Exception ex) {
			return new JsonArray();
		}
	}
	
	public int getInt(String key) {
		try {
			return src.get(key).getAsInt();
		} catch (Exception ex) {
			return 0;
		}
	}
	
	public String getString(String key) {
		try {
			return src.get(key).getAsString();
		} catch (Exception ex) {
			return "";
		}
	}
	
	public long getLong(String key) {
		try {
			return src.get(key).getAsLong();
		} catch (Exception ex) {
			return 0L;
		}
	}
	
	public double getDouble(String key) {
		try {
			return src.get(key).getAsDouble();
		} catch (Exception ex) {
			return 0D;
		}
	}

	public boolean getBoolean(String key) {
		try {
			return src.get(key).getAsBoolean();
		} catch (Exception ex) {
			return false;
		}
	}
	
	public boolean containsKey(String key) {
		return src.has(key);
	}
	
	public JsonObject getSource() {
		return src;
	}
	
}
