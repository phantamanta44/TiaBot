package io.github.phantamanta44.tiabot.module.image;

import io.github.phantamanta44.tiabot.module.CTModule;
import io.github.phantamanta44.tiabot.module.image.command.CommandCompose;
import io.github.phantamanta44.tiabot.module.image.script.ImgInstructionRegistry;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class ImageModule extends CTModule {

	public ImageModule() {
		commands.add(new CommandCompose());
		ImgInstructionRegistry.init();
	}
	
	@Override
	public String getName() {
		return "image";
	}
	
	@Override
	public String getDesc() {
		return "Image processing stuff.";
	}

	@Override
	public String getAuthor() {
		return "Phanta";
	}

	public static void fileify(RenderedImage image, Consumer<File> action) throws IOException {
		File temp = File.createTempFile("tiabot_img", ".png");
		ImageIO.write(image, "png", temp);
		action.accept(temp);
		temp.delete();
	}

}
