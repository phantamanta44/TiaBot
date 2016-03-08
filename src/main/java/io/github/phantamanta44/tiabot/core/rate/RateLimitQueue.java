package io.github.phantamanta44.tiabot.core.rate;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import io.github.phantamanta44.tiabot.util.IFuture;

public class RateLimitQueue {

	private static ScheduledExecutorService taskPool = Executors.newSingleThreadScheduledExecutor();
	
	private Deque<QueuedAction<?>> queue = new ConcurrentLinkedDeque<>();
	private long ttl = 0L;
	private ScheduledFuture<?> updateFuture;
	
	public <T> IFuture<T> push(Supplier<T> task) {
		QueuedAction<T> action = new QueuedAction<T>(task);
		queue.offer(action);
		return action;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public boolean isActive() {
		return ttl > 0;
	}
	
	public void setActive(long time) {
		ttl = Math.max(time, ttl);
		if (updateFuture == null && ttl > 0)
			updateFuture = taskPool.schedule(new UpdateTask(this), time, TimeUnit.MILLISECONDS);
	}
	
	public void setInactive() {
		ttl = 0L;
		updateFuture.cancel(false);
		updateFuture = null;
		queue.forEach(a -> a.doAction());
	}
	
	private static class UpdateTask implements Runnable {
		
		private RateLimitQueue parent;
		
		public UpdateTask(RateLimitQueue parent) {
			this.parent = parent;
		}
	
		@Override
		public void run() {
			if (parent.queue.isEmpty())
				parent.setInactive();
			QueuedAction<?> action = parent.queue.pop();
			synchronized (action) {
				action.doAction();
				action.notify();
			}
			parent.ttl -= 1000L;
			if (parent.ttl <= 0 && parent.isEmpty())
				parent.setInactive();
			else
				parent.updateFuture = taskPool.schedule(new UpdateTask(parent), 1000L, TimeUnit.MILLISECONDS);
		}
		
	}
	
	private static class QueuedAction<T> implements IFuture<T> {
		
		private Supplier<T> action;
		private T result;
		private boolean done = false;
		
		public QueuedAction(Supplier<T> toDo) {
			action = toDo;
		}
		
		public void doAction() {
			result = action.get();
			done = true;
		}

		@Override
		public void dispatch() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isDone() {
			return done;
		}

		@Override
		public T getResult() {
			return result;
		}
		
	}
	
}
