package io.github.phantamanta44.tiabot.module.encounter;

import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.data.abst.ITargetable;

public class BattleContext {

	private ITargetable source;
	private ITargetable target;
	private IEventContext context;
	
	public BattleContext(ITargetable source, ITargetable target, IEventContext ctx) {
		this.source = source;
		this.target = target;
		this.context = ctx;
	}

	public ITargetable getSource() {
		return source;
	}

	public ITargetable getTarget() {
		return target;
	}
	
	public IEventContext getContext() {
		return context;
	}
	
}
