package io.github.phantamanta44.tiabot.module.image.script.impl;

import io.github.phantamanta44.tiabot.module.image.script.IImgInstruction;
import io.github.phantamanta44.tiabot.module.image.script.ImageScriptContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;

import java.util.Arrays;

public class ImgText implements IImgInstruction {

	@Override
	public String getInvocation() {
		return "text";
	}

	@Override
	public int execute(ImageScriptContext ctx, String[] args) {
		if (args.length < 3)
			throw new IllegalArgumentException("Requires at 3 arguments!");
		int x = Integer.parseInt(args[0]), y = Integer.parseInt(args[1]);
		String text = MessageUtils.concat(Arrays.copyOfRange(args, 2, args.length));
		ctx.getGraphics().drawString(text, x + ctx.getGraphics().getFontMetrics().getHeight(), y);
		return 1;
	}

}
