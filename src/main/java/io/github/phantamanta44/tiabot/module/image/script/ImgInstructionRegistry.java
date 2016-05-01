package io.github.phantamanta44.tiabot.module.image.script;

import io.github.phantamanta44.tiabot.module.image.script.impl.*;

import java.util.HashMap;
import java.util.Map;

public class ImgInstructionRegistry {

	private static final Map<String, IImgInstruction> instructions = new HashMap<>();

	public static void init() {
		register(new ImgColor());
		register(new ImgFont());
		register(new ImgImport());
		register(new ImgRect());
		register(new ImgText());
	}

	private static void register(IImgInstruction instruction) {
		instructions.put(instruction.getInvocation(), instruction);
	}

	public static IImgInstruction resolve(String inv) {
		return instructions.entrySet().stream()
				.filter(i -> i.getKey().equalsIgnoreCase(inv))
				.map(Map.Entry::getValue)
				.findAny().orElse(null);
	}

}
