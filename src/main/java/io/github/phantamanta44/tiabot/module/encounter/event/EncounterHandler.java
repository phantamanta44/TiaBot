package io.github.phantamanta44.tiabot.module.encounter.event;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterBank;
import io.github.phantamanta44.tiabot.module.encounter.EncounterContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterBoss;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterItem;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterPlayer;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITargetable;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITurnable;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.TurnFuture;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory.PoolType;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory.QueueType;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class EncounterHandler implements ICTListener {

	private static Map<String, Encounter> inProg = new ConcurrentHashMap<>();
	private static Map<String, Encounter> engaged = new ConcurrentHashMap<>();
	private static ScheduledExecutorService taskPool;
	
	static {
		taskPool = new ThreadPoolFactory()
				.withPool(PoolType.SCHEDULED)
				.withQueue(QueueType.CACHED)
				.construct();
	}
	
	public static String a() {
		return ((ScheduledThreadPoolExecutor)taskPool).getQueue().stream()
				.map(r -> r.toString())
				.reduce((a, b) -> a.concat("\n").concat(b))
				.orElse("nothing here");
	}
	
	@ListenTo
	public void onMessageReceived(MessageReceivedEvent event, IEventContext ctx) {
		Encounter enc = inProg.get(ctx.getChannel().getID());
		if (enc == null)
			return;
		enc.onMessage(ctx);
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
		if (engaged.containsKey(sender.getID())) {
			ctx.sendMessage("You are already engaged in an encounter!");
			return;
		}
		Random rand = new Random();
		EncounterBoss boss;
		int queueSize = 5;
		if (args.length > 0) {
			try {
				queueSize = Integer.parseInt(args[0]);
				if (queueSize < 1 || queueSize > 10)
					throw new NumberFormatException();
			} catch (NumberFormatException ex) {
				ctx.sendMessage("Not a valid queue size!");
				return;
			}
			if (args.length > 1) {
				try {
					boss = EncounterData.getBoss(MessageUtils.concat(Arrays.copyOfRange(args, 1, args.length)));
				} catch (Exception ex) {
					ctx.sendMessage("No such boss!");
					return;
				}
			}
			else
				boss = EncounterData.getBoss(rand);
		}
		else
			boss = EncounterData.getBoss(rand);
		Encounter enc = new Encounter(sender, boss, ctx, queueSize, rand);
		inProg.put(ctx.getChannel().getID(), enc);
		engaged.put(sender.getID(), enc);
		if (queueSize > 1)
			ctx.sendMessage("A wild **%s** appears! Type `fight me` to join the fight!", boss.getName());
	}
	
	public static EncounterPlayer getEncPlayer(IUser user) {
		EncounterPlayer pl = EncounterData.getPlayer(user.getID());
		if (pl == null)
			pl = EncounterData.registerPlayer(user);
		return pl;
	}
	
	public static boolean isEngaged(EncounterPlayer pl) {
		return engaged.containsKey(pl.getId());
	}
	
	public static String getEncChannel(String userId) {
		Encounter enc = engaged.get(userId);
		if (enc == null)
			return null;
		return enc.getChannel().getID();
	}
	
	public static Encounter getEncounter(IChannel chan) {
		return inProg.get(chan.getID());
	}
	
	public static class Encounter {
		
		private Random rand;
		private IEventContext ctx;
		private EncounterState state = EncounterState.WAIT;
		private EncounterPlayer[] parts;
		private ITurnable[] turns;
		private int partInd = 1;
		private EncounterBoss boss;
		private Future<?> task, turnTask;
		private TurnFuture turnStatus;
		private Collection<ITurnable> dead = new HashSet<>();
		
		public Encounter(IUser initializer, EncounterBoss boss, IEventContext context, int queueSize, Random random) {
			try {
				this.ctx = context;
				this.boss = boss;
				this.rand = random;
				this.parts = new EncounterPlayer[queueSize];
				this.turns = new ITurnable[queueSize + 1];
				parts[0] = getEncPlayer(initializer);
				if (queueSize == 1)
					begin();
				else {
					task = taskPool.schedule(() -> {
						ctx.sendMessage("Not enough fighters appeared! The **%s** wandered off.", boss.getName());
						cancel();
					}, 20000L, TimeUnit.MILLISECONDS);
				}
			} catch (Exception ex) {
				ctx.sendMessage("Error constructing encounter! View console for details.");
				ex.printStackTrace();
				cancel();
			}
		}
		
		public void onMessage(IEventContext msgCtx) {
			if (state == EncounterState.WAIT) {
				if (MessageUtils.lenientMatch("fight me", msgCtx.getMessage().getContent())) {
					if (!engaged.containsKey(msgCtx.getUser().getID())) {
						if (!Arrays.asList(parts).contains(getEncPlayer(msgCtx.getUser()))) {
							parts[partInd] = getEncPlayer(msgCtx.getUser());
							engaged.put(msgCtx.getUser().getID(), this);
							ctx.sendMessage("%s has joined the fight! (%s/%d)", parts[partInd].getName(), partInd + 1, parts.length);
							if (++partInd >= parts.length) {
								task.cancel(true);
								begin();
							}
						}
						else
							ctx.sendMessage("%s: You are already in the battle!", msgCtx.getUser().mention());
					}
					else
						ctx.sendMessage("%s: You can only be in one battle at once!", msgCtx.getUser().mention());
				}
			}
		}
		
		private void begin() {
			try {
				Arrays.stream(parts).forEach(p -> {
					p.setHealth(p.getMaxHealth());
					p.setMana(p.getMaxMana());
					p.resetCooldowns();
				});
				partInd = turns.length - 1;
				state = EncounterState.FIGHT;
				System.arraycopy(parts, 0, turns, 0, parts.length);
				turns[parts.length] = boss;
				ctx.sendMessage("**%s** approaches...", boss.getName());
				taskPool.schedule(() -> turnSwap(), 2700L, TimeUnit.MILLISECONDS);
			} catch (Exception ex) {
				ctx.sendMessage("Fatal error initializing encounter! Check console for details.");
				ex.printStackTrace();
				cancel();
			}
		}
		
		private void turnSwap() {
			try {
				if (task != null)
					task.cancel(true);
				if (dead.size() >= parts.length || boss.getHealth() <= 0)
					throw new IllegalStateException();
				do
					partInd = (partInd + 1) % turns.length;
				while (turns[partInd].getHealth() <= 0);
				ITurnable turn = turns[partInd];
				ctx.sendMessage("%s**It's %s's turn.**", getPartDisplay(), turn.getName());
				EncounterContext ec = partInd < parts.length ? new EncounterContext(parts, new ITargetable[] {boss}) : new EncounterContext(new ITargetable[] {boss}, parts);
				turnStatus = turn.onTurn(ctx, rand, ec);
				turnTask = taskPool.submit(() -> turnStatus.dispatch());
				task = taskPool.schedule(() -> {
					try {
						turnStatus.cancel();
						turnTask.cancel(true);
						turn.cancelTurn();
						ctx.sendMessage("%s's turn was skipped because they didn't act fast enough.", turn.getName());
						taskPool.schedule(() -> turnSwap(), 2700L, TimeUnit.MILLISECONDS);
					} catch (Exception ex) {
						ctx.sendMessage("Fatal error timing out turn! Check console for details.");
						ex.printStackTrace();
						cancel();
					}
				}, 30000L, TimeUnit.MILLISECONDS);
				turnStatus.promise(() -> {
					task.cancel(true);
					if (deathCheck())
						taskPool.schedule(() -> turnSwap(), 2700L, TimeUnit.MILLISECONDS);
				});
			} catch (Exception ex) {
				ctx.sendMessage("Fatal error swapping turns! Check console for details.");
				ex.printStackTrace();
				cancel();
			}
		}
		
		private String getPartDisplay() {
			StringBuffer names = new StringBuffer(Arrays.stream(parts)
					.map(p -> MessageUtils.rightPad(p.getName(), 12))
					.reduce((a, b) -> a + ' ' + b).get());
			names.append(' ').append(MessageUtils.rightPad(boss.getName(), 12));
			StringBuffer hpBars = new StringBuffer(Arrays.stream(parts)
					.map(p -> generateHealthBar(p))
					.reduce((a, b) -> a + ' ' + b).get());
			hpBars.append(' ').append(generateHealthBar(boss));
			StringBuffer nums = new StringBuffer(Arrays.stream(parts)
					.map(p -> MessageUtils.rightPad(String.format("%s/%s", p.getHealth(), p.getMaxHealth()), 12))
					.reduce((a, b) -> a + ' ' + b).get());
			nums.append(' ').append(MessageUtils.rightPad(String.format("%s/%s", boss.getHealth(), boss.getMaxHealth()), 12));
			return String.format("```%s\n%s\n%s```", names, hpBars, nums);
		}
		
		private String generateHealthBar(ITargetable src) {
			int barLen = (int)(10F * (float)src.getHealth() / (float)src.getMaxHealth());
			String bar = MessageUtils.rightPad("[", barLen + 1, '#');
			return MessageUtils.rightPad(bar, 11, ' ') + ']';
		}
		
		private boolean deathCheck() {
			if (boss.getHealth() <= 0) {
				ctx.sendMessage(boss.getDeathMessage());
				state = EncounterState.LOOT;
				distributeLoot();
				return false;
			}
			Arrays.stream(parts).forEach(p -> {
				if (p.getHealth() <= 0 && !dead.contains(p)) {
					ctx.sendMessage("%s hath perished!", p.getName());
					dead.add(p);
				}
			});
			if (dead.size() >= parts.length) {
				ctx.sendMessage("There are no more combatants to continue the fight! The battle has been lost!");
				cancel();
				return false;
			}
			return true;
		}
		
		private void distributeLoot() {
			try {
				cancel();
				final StringBuilder loot = new StringBuilder("__**Loot Distribution:**__\n");
				Arrays.stream(parts).forEach(p -> {
					int xp = (int)((float)boss.getExperience() / (float)parts.length);
					if (dead.contains(p))
						xp = (int)((float)xp / 1.5F);
					EncounterItem drop = boss.getDrop(rand);
					loot.append(String.format("%s received: [%s] [%d XP]\n", p.getName(), drop.getName(), xp));
					p.addExp(xp);
					EncounterBank.addItem(p, drop);
					EncounterData.save();
				});
				ctx.sendMessage(loot.append("Excess loot has been deposited in the bank. Use `[]encbank` to access it.").toString());
			} catch (Exception ex) {
				ctx.sendMessage("Fatal error distributing loot! Check console for details.");
				ex.printStackTrace();
			}
		}
		
		public void cancel() {
			try {
				if (task != null)
					task.cancel(true);
				if (state == EncounterState.FIGHT) {
					turns[partInd].cancelTurn();
					if (turnStatus != null)
						turnStatus.cancel();
					if (turnTask != null)
						turnTask.cancel(true);
				}
				Arrays.stream(parts)
				.filter(p -> p != null)
				.forEach(p -> {
					engaged.remove(p.getId());
					p.setHealth(p.getMaxHealth());
					p.setMana(p.getMaxMana());
					p.resetCooldowns();
				});
				inProg.remove(ctx.getChannel().getID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		public IChannel getChannel() {
			return ctx.getChannel();
		}
		
		public EncounterBoss getBoss() {
			return boss;
		}
		
	}
	
	public static enum EncounterState {
		
		WAIT, FIGHT, LOOT
		
	}
	
}
