package io.github.phantamanta44.tiabot.module.image.command;

import com.github.fge.lambdas.Throwing;
import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.image.ImageModule;
import io.github.phantamanta44.tiabot.module.image.script.ImageScriptContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandCompose implements ICommand {

	private static final List<String> ALIASES = Collections.singletonList("imgcomp");

	@Override
	public String getName() {
		return "imgcompose";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Compose an image using ImgScript.";
	}

	@Override
	public String getUsage() {
		return "imgcompose <dim1>[x<dim2>] <script>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 2) {
			ctx.sendMessage("Not enough arguments!");
			return;
		}

		String dims = args[0];
		int x, y;
		try {
			if (dims.matches("^\\d+x\\d+$")) {
				String[] dimArr = dims.split("x");
				x = Integer.parseInt(dimArr[0]);
				y = Integer.parseInt(dimArr[1]);
			}
			else
				x = y = Integer.parseInt(dims);
		} catch (NumberFormatException e) {
			ctx.sendMessage("Must supply valid integers as dimensions!");
			return;
		}

		String script = MessageUtils.concat(Arrays.copyOfRange(args, 1, args.length));
		if (script.startsWith("```") && script.endsWith("```"))
			script = script.substring(3, script.length() - 3);
		else if (script.startsWith("`") && script.endsWith("`"))
			script = script.substring(1, script.length() - 1);

		ImageScriptContext isc = new ImageScriptContext(x, y);
		try {
			isc.execute(script);
		} catch (IllegalStateException e) {
			ctx.sendMessage(e.getMessage());
			return;
		}

		try {
			ImageModule.fileify(isc.render(), Throwing.consumer(ctx.getChannel()::sendFile));
		} catch (Exception e) {
			ctx.sendMessage("Failed to upload composed image: %s", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return TiaBot.isAdmin(sender);
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		return "No permission!";
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
