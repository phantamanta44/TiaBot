package io.github.phantamanta44.tiabot.module.encounter.event;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.phantamanta44.tiabot.core.ICTListener;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterBoss;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterPlayer;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class EncounterHandler implements ICTListener {

	private static Map<String, Encounter> inProg = new ConcurrentHashMap<>();
	private static List<String> engaged = new CopyOnWriteArrayList<>();
	private static Timer taskTimer = new Timer();
	
	public EncounterHandler() {
		EncounterData.load();
	}
	
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
		inProg.put(ctx.getChannel().getID(), new Encounter(sender, boss, ctx));
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
		
		private IEventContext ctx;
		private EncounterState state = EncounterState.WAIT;
		private EncounterPlayer[] parts = new EncounterPlayer[5];
		private EncounterBoss boss;
		
		public Encounter(IUser initializer, EncounterBoss boss, IEventContext context) {
			this.ctx = context;
			this.boss = boss;
			EncounterPlayer pl = getEncPlayer(initializer);
			parts[0] = pl;
		}
		
		public void onMessage(IEventContext msgCtx) {
			
		}

		public boolean canJoin() {
			return state == EncounterState.WAIT;
		}
		
	}
	
	public static enum EncounterState {
		
		WAIT, FIGHT, LOOT
		
	}
	
}
