package io.github.phantamanta44.tiabot.module.random.command;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

public class CommandBash implements ICommand {

	private static final List<String> ALIASES = Arrays.asList(new String[] {"qdb", "quote", "bashquote", "ircquote"});
	private static final String BASH_URL = "http://bash.org/?random", BASH_LOOKUP = "http://bash.org/?quote=";
		
	@Override
	public String getName() {
		return "bash";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Retrieves a quote from http://bash.org.";
	}

	@Override
	public String getUsage() {
		return "bash [#quote]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		try {
			Document doc;
			if (args.length > 0) {
				int qN = Integer.parseInt(args[0]);
				if (qN < 0)
					throw new NumberFormatException();
				doc = Jsoup.connect(BASH_LOOKUP + qN).get();
			}
			else
				doc = Jsoup.connect(BASH_URL).get();
			Element qList = doc.getElementsByAttributeValue("valign", "top").get(0);
			String id = qList.child(0).child(0).child(0).text(), quote = qList.child(1).html().replaceAll("<br>", "\n");
			ctx.sendMessage("**Quote %s**\n```%s```", id, StringEscapeUtils.unescapeHtml4(quote));
		} catch (IndexOutOfBoundsException ex) {
			ctx.sendMessage("Quote does not exist!");
		} catch (NumberFormatException ex) {
			ctx.sendMessage("Invalid quote number \"%s\"!", args[0]);
		} catch (Exception ex) {
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
		return ".*(?:get(?: me)?|retrieve)(?: an?| the)? (?:random )?(?:bash |irc |qdb )?quote(?: (?:number |#)?(?<a0>\\d{1,}))?.*";
	}

}
