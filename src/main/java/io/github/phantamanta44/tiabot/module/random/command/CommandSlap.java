package io.github.phantamanta44.tiabot.module.random.command;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.ChanceList;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandSlap implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"whack"});
	private static final ChanceList<String> slapStrings = new ChanceList<>(
			"\\*Slaps %s\\*", "\\*Smacks %s with a fish\\*", "\\*Whacks %s\\*", "\\*Wallops %s\\*");
	
	@Override
	public String getName() {
		return "slap";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Smacks another person about the face.";
	}

	@Override
	public String getUsage() {
		return "slap <@person>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("There's nobody to slap!");
			return;
		}
		IUser user = MessageUtils.resolveMention(args[0]);
		if (user == null) {
			ctx.sendMessage("There's nobody to slap!");
			return;
		}
		Random rand = new Random();
		ctx.sendMessage(slapStrings.getAtRandom(rand), user.mention());
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getEnglishInvocation() {
		return ".*(?:slap|whack|wallop) (?<a0><\\S+>).*";
	}

}
