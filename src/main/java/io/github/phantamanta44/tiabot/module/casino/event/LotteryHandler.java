package io.github.phantamanta44.tiabot.module.casino.event;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.phantamanta44.tiabot.Discord;
import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.econ.EconData;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory.PoolType;
import io.github.phantamanta44.tiabot.util.concurrent.ThreadPoolFactory.QueueType;
import io.github.phantamanta44.tiabot.util.data.ChanceList;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class LotteryHandler implements ICTListener {

	private static final Map<String, Lottery> inProg = new ConcurrentHashMap<>();
	private static ScheduledExecutorService taskPool;
	
	static {
		taskPool = new ThreadPoolFactory()
				.withPool(PoolType.SCHEDULED)
				.withQueue(QueueType.CACHED)
				.construct();
	}
	
	public static boolean hasLottery(IChannel chan) {
		return inProg.containsKey(chan.getID());
	}
	
	public static void newLottery(IEventContext ctx, long amount, long ticketPrc, int ticketCnt) {
		inProg.put(ctx.getChannel().getID(), new Lottery(ctx, amount, ticketPrc, ticketCnt));
	}
	
	@ListenTo
	public void onMessage(MessageReceivedEvent event, IEventContext ctx) {
		if (inProg.containsKey(ctx.getChannel().getID()))
			inProg.get(ctx.getChannel().getID()).onMessage(ctx);
	}
	
	private static class Lottery {

		private IEventContext ctx;
		private long jackpot, price;
		private int tickets;
		private ChanceList<String> contestants = new ChanceList<>();
		private ScheduledFuture<?> termTask;
		
		public Lottery(IEventContext ctx, long amount, long ticketPrc, int ticketCnt) {
			this.ctx = ctx;
			this.jackpot = amount;
			this.price = ticketPrc;
			this.tickets = ticketCnt;
			ctx.sendMessage("__**A lottery is beginning for %d bits!**__\nAvailable Tickets: %d\nPrice Per Ticket: %d bit(s)\nType `lotto me <#tickets>` to buy in.\n**You have 5 minutes to buy tickets!**", jackpot, tickets, price);
			termTask = taskPool.schedule(() -> {
				distributeRewards();
			}, 5L, TimeUnit.MINUTES);
		}

		public void onMessage(IEventContext ctx) {
			String msg = ctx.getMessage().getContent();
			if (!msg.toLowerCase().startsWith("lotto me "))
				return;
			IUser user = ctx.getUser();
			String[] parts = msg.split("\\s");
			if (parts.length < 3) {
				ctx.sendMessage("%s: You must specify a number of tickets to buy!", ctx.getUser().mention());
				return;
			}
			int toBuy;
			try {
				toBuy = Integer.parseInt(parts[2]);
				if (toBuy < 1)
					throw new NumberFormatException();
			} catch (NumberFormatException ex) {
				ctx.sendMessage("%s: Invalid number of tickets!", ctx.getUser().mention());
				return;
			}
			if (tickets < toBuy) {
				ctx.sendMessage("%s: There aren't enough remaining tickets to buy this many!", ctx.getUser().mention());
				return;
			}
			long totalPrc = price * toBuy;
			if (EconData.getBits(user) < totalPrc) {
				ctx.sendMessage("%s: You can't afford to buy this many tickets!", ctx.getUser().mention());
				return;
			}
			tickets -= toBuy;
			EconData.removeBits(user, totalPrc);
			jackpot += totalPrc;
			for (int i = 0; i < toBuy; i++)
				contestants.addOutcome(user.getID());
			ctx.sendMessage("**%s** just bought **%d** tickets for **%d bits**, increasing the jackpot to **%d bits**!",
					ctx.getUser().mention(), toBuy, totalPrc, jackpot);
			if (tickets < 1)
				distributeRewards();
		}
		
		public void distributeRewards() {
			terminate();
			if (contestants.size() < 1) {
				ctx.sendMessage("**The lottery is over!**\nNobody bought any tickets, so the money was returned to %s.", ctx.getUser().getName());
				EconData.addBits(ctx.getUser(), jackpot);
				return;
			}
			IUser winner = Discord.getInstance().getUserById(contestants.getAtRandom(new Random()));
			EconData.addBits(winner, jackpot);
			ctx.sendMessage("**The lottery is over!**\n**%s** won a jackpot of **%d bits**! Congratulations!", winner.getName(), jackpot);
		}
		
		public void terminate() {
			if (termTask != null)
				termTask.cancel(true);
			inProg.remove(ctx.getChannel().getID());
		}
		
	}

}
