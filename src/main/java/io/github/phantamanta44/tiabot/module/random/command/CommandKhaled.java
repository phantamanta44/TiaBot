package io.github.phantamanta44.tiabot.module.random.command;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.data.ChanceList;
import sx.blah.discord.handle.obj.IUser;

public class CommandKhaled implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"djkhaled", "anotherone", "inspireme"});
	private static final ChanceList<String> khaledisms = new ChanceList<>(
			"Another one.", "Bless up.", "We the best.", "I changed. A lot.", "Win, win, win, no matter what.",
			"You smart.", "You loyal.", "You grateful.", "I appreciate you.", "You a genius.",
			"You can put the hinges on the hands, too.", "I changed. A lot. You can too.",
			"Never give up.", "Never surrender.", "Buy yo' mama a house.", "Buy yo' whole family houses.",
			"Never play yourself.", "They don't want you to succeed.", "You very smart.", "Always have faith.",
			"Always have hope.", "The \uD83D\uDD11 is to make it.", "\uD83D\uDD11 to more success is clean heart and clean face.",
			"Smh they get mad when you have joy.", "Give thanks to the most high.",
			"They will try to close the door on you, just open it.", "They don't want you to win.",
			"Those that weather the storm are the great ones.", "The \uD83D\uDD11 to more success is cocoa butter.",
			"There will be road blocks, but we will overcome it.", "They don't want you to jetski.",
			"Don't play yourself.", "Another one\u2014no, another two.", "Ain't nothing like bamboo.",
			"To succeed, you must believe. When you believe, you will succeed.", "Be very smart and extra focused.",
			"They don't want you to have a Rolls-Royce. I promise you.", "The \uD83D\uDD11 is: never fold.",
			"They wanna come stress me out? Heh. Bye.", "Enjoy life, man. Live it up.", "You have to make it through the jungle.",
			"The \uD83D\uDD11 to more success is to get a massage one a week.", "They gon' try to stop you. They gon' try.",
			"Some people can't handle winnin'. I can.", "They never said winning was easy.",
			"The \uD83D\uDD11 is to stay clean at all times.", "Walk with me thru the pathway to more success.",
			"They don't want you to have an amazing breakfast.", "The \uD83D\uDD11 is not to drive your jetski in the dark.",
			"They dont' want you to be healthy.", "The \uD83D\uDD11 is to make the right choice.", "Never be afraid of being yourself.",
			"It's important to shape up your hedges.", "Be a star. Be a superstar.", "Just say lion.", "The \uD83D\uDD11 is to be honest.",
			"Bless up. Don't play yourself.", "Very important\u2014major \uD83D\uDD11. Cloth talk.",
			"Egg whites. Chicken sausage. Water. They don't want you to eat.", "The \uD83D\uDD11 is: don't entertain they.",
			"The \uD83D\uDD11 is to weather the storm.");
	
	@Override
	public String getName() {
		return "khaled";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Another one.";
	}

	@Override
	public String getUsage() {
		return "khaled [#lines]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		int iter = 3;
		try {
			iter = Integer.parseInt(args[0]);
		} catch (Exception ex) { }
		StringBuilder toSend = new StringBuilder();
		Random rand = new Random();
		for (int i = 0; i < iter; i++)
			toSend.append(khaledisms.getAtRandom(rand)).append("\n");
		ctx.sendMessage(toSend.toString());
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
		return ".*(?:say |(?:get|tell) me )(?:(?<a0>\\d+) |an? |the )?(?:(?:some)? ?(?:thing|quote)s? )?(?:(?:inspiring|inspirational)(?: (?:thing|quote)s?)?|keys? to success).*";
	}

}
