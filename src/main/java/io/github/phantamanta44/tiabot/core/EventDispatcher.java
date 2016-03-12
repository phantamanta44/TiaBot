package io.github.phantamanta44.tiabot.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.github.phantamanta44.tiabot.TiaBot;
import io.github.phantamanta44.tiabot.core.ICTListener.ListenTo;
import io.github.phantamanta44.tiabot.core.context.EventContextChannel;
import io.github.phantamanta44.tiabot.core.context.EventContextGuild;
import io.github.phantamanta44.tiabot.core.context.EventContextMessage;
import io.github.phantamanta44.tiabot.core.context.EventContextUser;
import io.github.phantamanta44.tiabot.core.context.GenericEventContext;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import sx.blah.discord.handle.Event;
import sx.blah.discord.handle.EventSubscriber;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("unchecked")
public class EventDispatcher {

	private static final Map<Class<? extends ICTListener>, HandlerSignature> handlerSigMap = new ConcurrentHashMap<>();
	private static final List<ICTListener> handlers = new CopyOnWriteArrayList<>();
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static final ScheduledExecutorService taskPool = Executors.newSingleThreadScheduledExecutor();
	
	public static void registerHandler(ICTListener handler) {
		handlers.add(handler);
		Class<? extends ICTListener> handlerClass = handler.getClass();
		if (!handlerSigMap.containsKey(handlerClass))
			handlerSigMap.put(handlerClass, new HandlerSignature(handlerClass));
	}
	
	public static void unregisterHandler(ICTListener handler) {
		handlers.remove(handler);
	}
	
	@EventSubscriber
	public void acceptEvent(Event event) {
		Class<? extends Event> eventType = event.getClass();
		for (ICTListener listener : handlers) {
			Method listenerMethod;
			HandlerSignature handlerSig = handlerSigMap.get(listener.getClass());
			if ((listenerMethod = handlerSig.listenerMethods.get(eventType)) != null) {
				final Future<?> eventFuture = executor.submit(() -> {
					try {
						listenerMethod.invoke(listener, event, getContext(event));
					} catch (Exception ex) {
						TiaBot.logger.severe("Event handling error!");
						ex.printStackTrace();
					}
				});
				taskPool.schedule(() -> {
					if (!eventFuture.isDone()) {
						eventFuture.cancel(true);
						TiaBot.logger.warn("Executor task timed out! Location: %s#%s", listener.getClass().getName(), listenerMethod.getName());
					}
				}, 15000L, TimeUnit.MILLISECONDS);
			}
		}
	}
	
	public static IEventContext getContext(Event event) {
		try {
			Class<? extends Event> clazz = event.getClass();
			if (clazz.getSimpleName().matches("(?:Message\\w+|Mention)Event")) {
				Method m = clazz.getMethod("getMessage");
				return new EventContextMessage((IMessage)m.invoke(event), clazz);
			} else if (clazz.getSimpleName().matches("User\\w+Event")) {
				Method m = clazz.getMethod("getUser");
				return new EventContextUser((IUser)m.invoke(event), clazz);
			} else if (clazz.getSimpleName().matches("Channel\\w+Event")) {
				Method m = clazz.getMethod("getChannel");
				return new EventContextChannel((IChannel)m.invoke(event), clazz);
			} else if (clazz.getSimpleName().matches("Guild\\w+Event")) {
				Method m = clazz.getMethod("getGuild");
				return new EventContextGuild((IGuild)m.invoke(event), clazz);
			}
		} catch (Exception ex) { /* Fall back to {@link GenericEventContext} */ }
		return new GenericEventContext(event);
	}

	private static class HandlerSignature {
		
		public final Map<Class<? extends Event>, Method> listenerMethods = new HashMap<>();
		
		public HandlerSignature(Class<? extends ICTListener> listenerClass) {
			Method[] methods = listenerClass.getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(ListenTo.class)
					&& method.getParameterCount() == 2
					&& method.getParameterTypes()[1] == IEventContext.class
					&& Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
					Class<? extends Event> eventType = (Class<? extends Event>)method.getParameterTypes()[0];
					if (this.listenerMethods.containsKey(eventType))
						throw new IllegalStateException("Duplicate listener methods for event " + eventType.getName() + "!");
					this.listenerMethods.put(eventType, method);
				}
			}
		}
		
	}
	
}