package io.github.phantamanta44.tiabot.module.random.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.Requests;

public class CommandHaiku implements ICommand {

	private static final String WORD_EPT = "http://api.wordnik.com:80/v4/words.json/randomWords?minCorpusCount=15000&limit=768&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
	private static final Collection<Word> STANDARD = Arrays.asList(new Word[] {
			new Word("the"), new Word("an"), new Word("a"), new Word("some"), new Word("much"),
			new Word("he"), new Word("she"), new Word("it"), new Word("they"), new Word("them"), new Word("I"), new Word("you")
	});
	
	@Override
	public String getName() {
		return "haiku";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Composes a beautiful Japanese poem.";
	}

	@Override
	public String getUsage() {
		return "haiku";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		Random rand = new Random();
		JsonParser parser = new JsonParser();
		try {
			JsonArray resp = parser.parse(Requests.GET.makeRequest(WORD_EPT)).getAsJsonArray();
			List<Word> words = StreamSupport.stream(resp.spliterator(), false)
					.map(w -> new Word(w.getAsJsonObject().get("word").getAsString()))
					.collect(Collectors.toList());
			words.addAll(STANDARD);
			Sentence[] lines = new Sentence[] {new Sentence(5), new Sentence(7), new Sentence(5)};
			for (Sentence s : lines) {
				int rem;
				while ((rem = s.getRemainingSyls()) > 0) {
					List<Word> poss;
					int syl = rand.nextInt(rem);
					int iter = 0;
					do {
						final int i = iter, j = rem;
						poss = words.stream()
							.filter(w -> w.getSyllables() == ((syl + i) % j) + 1)
							.collect(Collectors.toList());
						iter++;
					} while (poss.isEmpty() && iter < rem);
					s.append(poss.get(rand.nextInt(poss.size())));
				}
			}
			ctx.sendMessage(Arrays.stream(lines)
					.map(l -> String.format("`%s`", l.toString()))
					.reduce((a, b) -> a.concat("\n").concat(b)).get());
		} catch (Exception ex) {
			ctx.sendMessage("Errored while composing poem: %s", ex.getMessage());
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
		return ".*(?:write|generate|make|tell|compose|create)(:? me|us|him|her|it|them)? an?(:? \\w+)? haiku.*";
	}
	
	public static class Sentence {
		
		public final int targetSyls;
		private List<Word> words = new ArrayList<>();
		
		public Sentence(int syl) {
			this.targetSyls = syl;
		}
		
		public void append(Word w) {
			words.add(w);
		}
		
		public int getSyllables() {
			return words.stream()
					.mapToInt(Word::getSyllables)
					.reduce((a, b) -> a + b).orElse(0);
		}
		
		public int getRemainingSyls() {
			return targetSyls - getSyllables();
		}
		
		@Override
		public String toString() {
			return words.stream()
					.map(Word::toString)
					.reduce((a, b) -> a.concat(" ").concat(b)).orElse("");
		}
		
	}
	
	public static class Word {
		
		private static final Pattern VOWEL_PAT = Pattern.compile("([aeiou]|(?>![aeiou])y)+");
		private static final Pattern END_PAT = Pattern.compile("(?<!(?<![aeiouylp])[aeiouylp])((?<!tr)e|(?<!s)es|(?<![dt])ed)(\\W|$)");
		
		public final String text;
		
		public Word(String text) {
			this.text = text;
		}
		
		public int getSyllables() {
			Matcher m = VOWEL_PAT.matcher(text.toLowerCase());
			int syls = 0;
			while (m.find())
				syls++;
			Matcher m2 = END_PAT.matcher(text.toLowerCase());
			while (m2.find())
				syls--;
			return Math.max(syls, 1);
		}
		
		@Override
		public String toString() {
			return MessageUtils.capitalize(text);
		}
		
	}

}
