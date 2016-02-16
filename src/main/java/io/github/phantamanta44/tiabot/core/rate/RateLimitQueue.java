package io.github.phantamanta44.tiabot.core.rate;

import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

import io.github.phantamanta44.tiabot.util.IFuture;

public class RateLimitQueue {

	private static Timer queueTimer = new Timer();
	
	private Deque<QueuedAction<?>> queue = new ConcurrentLinkedDeque<>();
	private long ttl = 0L;
	private TimerTask updateTask;
	
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
		if (updateTask == null && ttl > 0) {
			updateTask = new UpdateTask(this);
			queueTimer.schedule(updateTask, time);
		}
	}
	
	public void setInactive() {
		ttl = 0L;
		updateTask.cancel();
		updateTask = null;
		queue.forEach(a -> a.doAction());
	}
	
	private static class UpdateTask extends TimerTask {
		
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
			else {
				parent.updateTask = new UpdateTask(parent);
				queueTimer.schedule(parent.updateTask, 1000L);
			}
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
