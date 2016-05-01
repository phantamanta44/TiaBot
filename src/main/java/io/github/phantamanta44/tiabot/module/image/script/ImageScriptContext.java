package io.github.phantamanta44.tiabot.module.image.script;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageScriptContext {

	private BufferedImage img;
	private Graphics2D graphics;

	public ImageScriptContext(int x, int y) {
		img = new BufferedImage(x, y, BufferedImage.TYPE_4BYTE_ABGR);
		graphics = img.createGraphics();
	}

	public void execute(String script) {
		String[] instructions = script.split(";");
		int iPtr = 0;
		try {
			while (iPtr < instructions.length) {
				if (!instructions[iPtr].trim().isEmpty()) {
					String[] ins = instructions[iPtr].trim().split("\\s+");
					IImgInstruction executor = ImgInstructionRegistry.resolve(ins[0]);
					if (executor == null)
						throw new NoSuchMethodException(String.format("Could not resolve instruction '%s'!", ins[0].toLowerCase()));
					int off = executor.execute(this, Arrays.copyOfRange(ins, 1, ins.length));
					iPtr += off;
					continue;
				}
				iPtr++;
			}
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Execution failed at instruction '%s' (%d): %s", instructions[iPtr].trim(), iPtr + 1, e.getLocalizedMessage()));
		}
	}

	public BufferedImage render() {
		graphics.dispose();
		return img;
	}

	public Graphics2D getGraphics() {
		return graphics;
	}

}
