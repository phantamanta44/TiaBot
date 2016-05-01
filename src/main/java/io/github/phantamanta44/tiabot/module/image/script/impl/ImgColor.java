package io.github.phantamanta44.tiabot.module.image.script.impl;

import io.github.phantamanta44.tiabot.module.image.script.IImgInstruction;
import io.github.phantamanta44.tiabot.module.image.script.ImageScriptContext;

import java.awt.*;

public class ImgColor implements IImgInstruction {

	@Override
	public String getInvocation() {
		return "col";
	}

	@Override
	public int execute(ImageScriptContext ctx, String[] args) {
		if (args.length < 1)
			throw new IllegalArgumentException("Requires 1 argument!");
		ctx.getGraphics().setColor(parseColor(args[0]));
		return 1;
	}

	private static Color parseColor(String raw) {
		String trimmed = raw.trim();
		if (!trimmed.matches("(?:#|0x)?[A-Fa-f0-9]{6}(?:[A-Fa-f0-9]{2})?"))
			throw new NumberFormatException(String.format("Not a valid colour string: '%s'", raw));
		if (trimmed.startsWith("0x"))
			trimmed = trimmed.substring(2);
		else if (trimmed.startsWith("#"))
			trimmed = trimmed.substring(1);

		int c, alpha = 255;
		if (trimmed.length() == 8) {
			c = Integer.parseInt(trimmed.substring(2), 16);
			alpha = Integer.parseInt(trimmed.substring(0, 2), 16);
		}
		else
			c = Integer.parseInt(trimmed, 16);

		int r = (c & 0xFF0000) >> 16, g = (c & 0xFF00) >> 8, b = c & 0xFF;
		return new Color(r, g, b, alpha);
	}

}
