package io.github.phantamanta44.tiabot.module.fandom.command;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import io.github.phantamanta44.tiabot.util.http.HttpUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandAnimeChar implements ICommand {
	
	private static final Pattern CHAR_PAGE_PAT = Pattern.compile("character\\.php\\?id=\\d+");
	private static final Pattern CHAR_ANIM_PAT = Pattern.compile("> (.+) from (.+) \\|");
	private static final Pattern CHAR_IMG_PAT = Pattern.compile("<a href=\"(http://ami.animecharactersdatabase.com/(?:\\S+))\">View Full Size Image</a>");
	private static final Pattern CHAR_PROP_PAT = Pattern.compile(
			"<dl>\\s+"
			+ "(<dt class=\"ace\">)ID</dt>\\s+<dd>\\d+</dd>\\s+"
			+ "\\1Gender</dt>\\s+<dd>(.*)</dd>\\s+"
			+ "\\1Eye Color</dt>\\s+<dd>(.*)</dd>\\s+"
			+ "\\1Hair Color</dt>\\s+<dd>(.*)</dd>\\s+"
			+ "\\1Hair Length</dt>\\s+<dd>(.*)</dd>\\s+"
			+ "\\1Apparent Age</dt>\\s+<dd>(.*)</dd>\\s+"
			+ "\\1Animal Ears</dt>\\s+<dd>.*</dd>\\s+"
			+ "</dl>"
			);
	
	@Override
	public String getName() {
		return "animechar";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Look up some anime character.";
	}

	@Override
	public String getUsage() {
		return "animechar <name>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify a character to look up!");
			return;
		}
		String name = MessageUtils.concat(args);
		try {
			String res = HttpUtils.requestXml("http://www.animecharactersdatabase.com/rapidsearch_ajax.php?s=" + name);
			Matcher mat = CHAR_PAGE_PAT.matcher(res);
			if (!mat.find()) {
				ctx.sendMessage("No results found!");
				return;
			}
			String charPage = HttpUtils.requestXml("http://www.animecharactersdatabase.com/" + mat.group());
			
			Matcher animeMatcher = CHAR_ANIM_PAT.matcher(charPage);
			animeMatcher.find();
			String chName = animeMatcher.group(1), anime = animeMatcher.group(2);
			
			Matcher imgMatcher = CHAR_IMG_PAT.matcher(charPage);
			imgMatcher.find();
			String img = imgMatcher.group(1);
			
			Matcher propMatcher = CHAR_PROP_PAT.matcher(charPage);
			propMatcher.find();
			String gender = propMatcher.group(2), eyes = propMatcher.group(3), hair = propMatcher.group(4),
					hairLen = propMatcher.group(5), age = propMatcher.group(6);
			
			ctx.sendMessage("__**Character: %s**__\nAnime: %s\nGender: %s\nAge: %s\nEye Colour: %s\nHair Colour: %s\nHair Length: %s\n%s",
					chName, anime, gender, age, eyes, hair, hairLen, img);
		} catch (Exception ex) {
			ctx.sendMessage("Errored while looking up character!");
			ex.printStackTrace();
		}
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
		return ".*who is(?: the)? best(?: little| small| smol)? (?:pony|ho(?:rse|ers)(?:fr(?:iend|end?))?).*";
	}

}
