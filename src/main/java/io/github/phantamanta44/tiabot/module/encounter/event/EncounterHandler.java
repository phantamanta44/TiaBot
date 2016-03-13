package io.github.phantamanta44.tiabot.module.encounter.event;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterBoss;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterPlayer;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITurnable;
import io.github.phantamanta44.tiabot.util.IFuture;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class EncounterHandler implements ICTListener {

	private static Map<String, Encounter> inProg = new ConcurrentHashMap<>();
	private static List<String> engaged = new CopyOnWriteArrayList<>();
	private static ScheduledExecutorService taskPool = Executors.newSingleThreadScheduledExecutor();
	
	@ListenTo
	public void onMessageReceived(MessageReceivedEvent event, IEventContext ctx) {
		if (ctx.getMessage().getContent().equalsIgnoreCase("fight me") && !engaged.contains(ctx.getUser().getID())) {
			
		} else if (engaged.contains(ctx.getUser().getID()) && inProg.containsKey(ctx.getChannel())) {
			inProg.get(ctx.getChannel()).onMessage(ctx);
		}
	}
	
	public static void procCmd(IUser sender, String[] args, IEventContext ctx) {
		if (ctx.getChannel().isPrivate()) {
			ctx.sendMessage("You cannot initiate encounters in private channels!");
			return;
		}
		if (inProg.containsKey(ctx.getChannel().getID())) {
			ctx.sendMessage("There is already an encounter in progress!");
			return;
		}
		Random rand = new Random();
		EncounterBoss boss;
		try {
			boss = EncounterData.getBoss(args[0]);
		} catch (Exception ex) {
			boss = EncounterData.getBoss(rand);
		}
		inProg.put(ctx.getChannel().getID(), new Encounter(sender, boss, ctx, rand));
		engaged.add(sender.getID());
		ctx.sendMessage("A wild **%s** appears! Type `fight me` to join the fight!", boss.getName());
	}
	
	public static EncounterPlayer getEncPlayer(IUser user) {
		EncounterPlayer pl = EncounterData.getPlayer(user.getID());
		if (pl == null)
			pl = EncounterData.registerPlayer(user);
		return pl;
	}
	
	public static class Encounter {
		
		private Random rand;
		private IEventContext ctx;
		private EncounterState state = EncounterState.WAIT;
		private EncounterPlayer[] parts = new EncounterPlayer[5];
		private ITurnable[] turns = new ITurnable[6];
		private int partInd = 1;
		private EncounterBoss boss;
		private ScheduledFuture<?> task;
		private IFuture<?> turnStatus;
		
		public Encounter(IUser initializer, EncounterBoss boss, IEventContext context, Random random) {
			this.ctx = context;
			this.boss = boss;
			this.rand = random;
			parts[0] = getEncPlayer(initializer);
			task = taskPool.schedule(() -> {
				ctx.sendMessage("Not enough fighters appeared! The **%s** wandered off.", boss.getName());
				cancel();
			}, 20000L, TimeUnit.MILLISECONDS);
		}
		
		public void onMessage(IEventContext msgCtx) {
			EncounterState toSet = state;
			switch (state) {
			case WAIT:
				if (msgCtx.getMessage().getContent().trim().equalsIgnoreCase("fight me")) {
					if (!engaged.contains(msgCtx.getUser().getID())) {
						if (!Arrays.asList(parts).contains(getEncPlayer(msgCtx.getUser()))) {
							parts[partInd] = getEncPlayer(msgCtx.getUser());
							engaged.add(msgCtx.getUser().getID());
							ctx.sendMessage("%s has joined the fight! (%s/5)", parts[partInd].getName(), partInd + 1);
						}
						else
							ctx.sendMessage("%s: You are already in the battle!", msgCtx.getUser().mention());
					}
					else
						ctx.sendMessage("%s: You can only be in one battle at once!", msgCtx.getUser().mention());
				}
				if (++partInd >= 5) {
					task.cancel(true);
					partInd = turns.length - 1;
					toSet = EncounterState.FIGHT;
					System.arraycopy(parts, 0, turns, 0, parts.length);
					ctx.sendMessage("The **%s** approaches...", boss.getName());
					taskPool.schedule(() -> turnSwap(), 2700L, TimeUnit.MILLISECONDS);
				}
				break;
			case FIGHT:
				break;
			case LOOT:
				break;
			default:
				break;
			}
			state = toSet;
		}
		
		private void turnSwap() {
			task.cancel(true);
			partInd = (partInd + 1) % turns.length;
			ITurnable turn = turns[partInd];
			turnStatus = turn.onTurn(ctx, rand);
			turnStatus.dispatch();
			if (turnStatus.isDone())
				taskPool.schedule(() -> turnSwap(), 2700L, TimeUnit.MILLISECONDS);
			else {
				task = taskPool.schedule(() -> {
					ctx.sendMessage("%s's turn was skipped because they didn't act fast enough.", turn.getName());
					turnSwap();
				}, 30000L, TimeUnit.MILLISECONDS);
			}
		}
		
		private void cancel() {
			task.cancel(true);
			engaged.removeAll(Arrays.asList(parts));
			inProg.remove(ctx.getChannel().getID());
		}
		
	}
	
	public static enum EncounterState {
		
		WAIT, FIGHT, LOOT
		
	}
	
}
