package io.github.phantamanta44.tiabot.module.random.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.util.ChanceList;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class DuelManager implements ICTListener {

	private static final ChanceList<String> words = new ChanceList<>();
	
	private static Timer taskTimer = new Timer();
	private static Map<String, Duel> duels = new ConcurrentHashMap<>();
	
	public DuelManager() {
		try {
			BufferedReader strIn = new BufferedReader(new FileReader(new File("duel.txt")));
			String line;
			while ((line = strIn.readLine()) != null)
				words.addOutcome(line);
			strIn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@ListenTo
	public void onMessageReceived(MessageReceivedEvent event, IEventContext ctx) {
		Duel duel = duels.get(ctx.getChannel().getID());
		if (duel != null)
			duel.update(ctx.getUser(), ctx.getMessage().getContent());
	}
	
	public static void procCmd(IUser sender, String[] args, IEventContext ctx) {
		if (ctx.getChannel().isPrivate()) {
			ctx.sendMessage("You cannot initiate 1v1s in private channels!");
			return;
		}
		if (duels.containsKey(ctx.getChannel().getID())) {
			ctx.sendMessage("There is already a 1v1 in progress!");
			return;
		}
		if (args.length < 1) {
			ctx.sendMessage("You need to specify someone to 1v1!");
			return;
		}
		IUser victim = MessageUtils.resolveMention(args[0]);
		if (victim == null) {
			ctx.sendMessage("Nobody to 1v1!");
			return;
		}
		ctx.sendMessage("**%s and %s\u2014Get ready to battle!**", sender.mention(), victim.mention());
		duels.put(ctx.getChannel().getID(), new Duel(sender, victim, ctx));
	}
	
	private static void cancelDuel(String id) {
		duels.remove(id);
	}
	
	public static class Duel {
		
		private DuelState state = DuelState.INACTIVE;
		private Random rand = new Random();
		private IEventContext ctx;
		private IUser a, b;
		private String target;
		private TimerTask cancelTask;
		
		public Duel(IUser a, IUser b, IEventContext eventContext) {
			this.a = a;
			this.b = b;
			this.ctx = eventContext;
			taskTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					start();
				}
			}, 3000L + rand.nextInt(2000));
			state = DuelState.PREPARING;
		}
		
		private void start() {
			target = words.getAtRandom(rand);
			state = DuelState.ACTIVE;
			ctx.sendMessage("**You have 10 seconds to type \"%s\"!**", target);
			cancelTask = new TimerTask() {
				@Override
				public void run() {
					state = DuelState.INACTIVE;
					ctx.sendMessage("**1v1 cancelled.**");
					cancelDuel(ctx.getChannel().getID());
				}
			};
			taskTimer.schedule(cancelTask, 10000L);
		}
		
		public void update(IUser sender, String msg) {
			if (state == DuelState.ACTIVE
					&& (sender.getID().equals(a.getID()) || sender.getID().equals(b.getID()))
					&& msg.equalsIgnoreCase(target)) {
				ctx.sendMessage("**%s wins the 1v1!**", sender.mention());
				state = DuelState.INACTIVE;
				cancelTask.cancel();
				cancelDuel(ctx.getChannel().getID());
			}
		}
		
	}
	
	public static enum DuelState {
		
		INACTIVE, PREPARING, ACTIVE;
		
	}
	
}
