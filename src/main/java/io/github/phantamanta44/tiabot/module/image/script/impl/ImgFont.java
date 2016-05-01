package io.github.phantamanta44.tiabot.module.image.script.impl;

import io.github.phantamanta44.tiabot.module.image.script.IImgInstruction;
import io.github.phantamanta44.tiabot.module.image.script.ImageScriptContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;

import java.awt.*;
import java.util.Arrays;

public class ImgFont implements IImgInstruction {

	@Override
	public String getInvocation() {
		return "font";
	}

	@Override
	public int execute(ImageScriptContext ctx, String[] args) {
		if (args.length < 3)
			throw new IllegalArgumentException("Requires 3 arguments!");

		int bitmask = Font.PLAIN;
		if (args[1].toLowerCase().contains("b"))
			bitmask |= Font.BOLD;
		if (args[1].toLowerCase().contains("i"))
			bitmask |= Font.ITALIC;

		ctx.getGraphics().setFont(new Font(MessageUtils.concat(Arrays.copyOfRange(args, 2, args.length)), bitmask, Integer.parseInt(args[0])));
		return 1;
	}

}
