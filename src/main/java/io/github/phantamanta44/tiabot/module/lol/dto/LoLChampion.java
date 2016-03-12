package io.github.phantamanta44.tiabot.module.lol.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.phantamanta44.tiabot.module.lol.LoLModule;
import io.github.phantamanta44.tiabot.module.lol.dto.LoLChampion.ChampSpell.SpellKey;
import io.github.phantamanta44.tiabot.util.SafeJsonWrapper;

public class LoLChampion {

	private int id;
	private String name, title, key;
	private String icon;
	private ChampInfo rating;
	private ChampPassive passive;
	private ChampSpell[] spells;
	
	public LoLChampion(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		id = data.getInt("id");
		name = data.getString("name");
		title = data.getString("title");
		key = data.getString("key");
		icon = LoLModule.dataDragon("img/champion/" + data.getJsonObject("image").getString("full"));;
		rating = new ChampInfo(data.getJsonObject("info"));
		passive = new ChampPassive(data.getJsonObject("passive"));
		JsonArray spellList = data.getJsonArray("spells");
		spells = StreamSupport.stream(spellList.spliterator(), false)
				.map(s -> new ChampSpell(new SafeJsonWrapper(s.getAsJsonObject())))
				.toArray(l -> new ChampSpell[l]);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getKey() {
		return key;
	}

	public String getIcon() {
		return icon;
	}
	
	public ChampInfo getRating() {
		return rating;
	}
	
	public ChampPassive getPassive() {
		return passive;
	}

	public ChampSpell[] getSpells() {
		return spells;
	}
	
	public ChampSpell getSpell(SpellKey key) {
		return spells[key.ordinal()];
	}

	public static final class ChampPassive {
		
		public final String name, desc, icon;
		
		public ChampPassive(SafeJsonWrapper dto) {
			name = dto.getString("name");
			desc = dto.getString("sanitizedDescription");
			icon = LoLModule.dataDragon("img/passive/" + dto.getJsonObject("image").getString("full"));
		}
		
	}
	
	public static final class ChampSpell {
	
		private static final Pattern replRegex = Pattern.compile("\\{\\{ (\\S+) \\}\\}");
		private static final Map<String, String> varLinkMap = new HashMap<>();
		
		static {
			varLinkMap.put("bonusattackdamage", "Bonus AD");
			varLinkMap.put("spelldamage", "AP");
			varLinkMap.put("attackdamage", "AD");
			varLinkMap.put("bonushealth", "Bonus Health");
			varLinkMap.put("bonusarmor", "Bonus Armor");
			varLinkMap.put("bonusspellblock", "Bonus MR");
			varLinkMap.put("spelldamage", "AP");
		}
		
		public final String name, desc, tooltip, icon;
		public final String[] effectBurn;
		public final String resource, costType, costBurn;
		public final Map<String, SpellArg> vars;
		
		public ChampSpell(SafeJsonWrapper dto) {
			name = dto.getString("name");
			desc = dto.getString("sanitizedDescription");
			tooltip = dto.getString("sanitizedTooltip");
			icon = LoLModule.dataDragon("img/spell/" + dto.getJsonObject("image").getString("full"));
			effectBurn = StreamSupport.stream(dto.getJsonArray("effectBurn").spliterator(), false)
					.map(o -> o.getAsString())
					.toArray(l -> new String[l]);
			resource = dto.getString("resource");
			costType = dto.getString("costType");
			costBurn = dto.getString("costBurn");
			vars = new HashMap<>();
			dto.getJsonArray("vars").forEach(o -> {
				SpellArg var = new SpellArg(new SafeJsonWrapper(o.getAsJsonObject()));
				vars.put(var.key, var);
			});
		}
		
		public String getCostFormatted() {
			return parseRepl(resource);
		}
		
		public String getTooltipFormatted() {
			return parseRepl(tooltip);
		}
		
		private String parseRepl(String orig) {
			Matcher mat = replRegex.matcher(orig);
			StringBuffer replBuffer = new StringBuffer();
			while (mat.find())
				mat.appendReplacement(replBuffer, keyVal(mat.group(1)));
			mat.appendTail(replBuffer);
			return replBuffer.toString();
		}
		
		private String keyVal(String key) {
			try {
				if (key.equals("cost"))
					return costBurn;
				else if (key.matches("e\\d+"))
					return effectBurn[Integer.parseInt(key.substring(1))];
				else if (key.matches("[af]\\d+"))
					return getVar(key);
				return "<no value>";
			} catch (ArrayIndexOutOfBoundsException ex) {
				ex.printStackTrace();
				return "<parse error>";
			}
		}
		
		private String getVar(String key) {
			SpellArg var = vars.get(key);
			if (var == null)
				return "<var error>";
			return String.format("%.0f%% %s", var.coeffs[0] * 100D, varLinkMap.get(var.link));
		}
		
		public static class SpellArg {
			
			public final String key, link;
			public final double[] coeffs;
			
			public SpellArg(SafeJsonWrapper dto) {
				key = dto.getString("key");
				link = dto.getString("link");
				coeffs = StreamSupport.stream(dto.getJsonArray("coeff").spliterator(), false)
						.mapToDouble(o -> o.getAsDouble())
						.toArray();
			}
			
		}
		
		public static enum SpellKey {
			
			Q, W, E, R;
			
		}
		
	}
	
	public static final class ChampInfo {
		
		public final int atk, def, ap, diff;
		
		public ChampInfo(SafeJsonWrapper dto) {
			atk = dto.getInt("attack");
			def = dto.getInt("defense");
			ap = dto.getInt("magic");
			diff = dto.getInt("difficulty");
		}
		
	}
	
}
