package io.github.phantamanta44.tiabot.module.image.script.impl;

import io.github.phantamanta44.tiabot.module.image.script.IImgInstruction;
import io.github.phantamanta44.tiabot.module.image.script.ImageScriptContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class ImgImport implements IImgInstruction {

	@Override
	public String getInvocation() {
		return "import";
	}

	@Override
	public int execute(ImageScriptContext ctx, String[] args) {
		if (args.length < 3)
			throw new IllegalArgumentException("Requires 3 arguments!");
		int x = Integer.parseInt(args[0]), y = Integer.parseInt(args[1]);
		try {
			Image img = ImageIO.read(new URL(args[2]));
			ctx.getGraphics().drawImage(img, x, y, img.getWidth(null), img.getHeight(null), null);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return 1;
	}

}
