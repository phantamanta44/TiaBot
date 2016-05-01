package io.github.phantamanta44.tiabot.module.image.script.impl;

import io.github.phantamanta44.tiabot.module.image.script.IImgInstruction;
import io.github.phantamanta44.tiabot.module.image.script.ImageScriptContext;

public class ImgRect implements IImgInstruction {

	@Override
	public String getInvocation() {
		return "rect";
	}

	@Override
	public int execute(ImageScriptContext ctx, String[] args) {
		if (args.length < 4)
			throw new IllegalArgumentException("Requires 4 arguments!");
		int x = Integer.parseInt(args[0]), y = Integer.parseInt(args[1]);
		ctx.getGraphics().fillRect(x, y, Integer.parseInt(args[2]) - x, Integer.parseInt(args[3]) - y);
		return 1;
	}

}
