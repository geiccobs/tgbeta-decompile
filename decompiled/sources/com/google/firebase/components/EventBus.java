package com.google.firebase.components;

import com.google.firebase.events.Event;
import com.google.firebase.events.EventHandler;
import com.google.firebase.events.Publisher;
import com.google.firebase.events.Subscriber;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class EventBus implements Subscriber, Publisher {
    private final Executor defaultExecutor;
    private final Map<Class<?>, ConcurrentHashMap<EventHandler<Object>, Executor>> handlerMap = new HashMap();
    private Queue<Event<?>> pendingEvents = new ArrayDeque();

    public EventBus(Executor defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
    }

    @Override // com.google.firebase.events.Publisher
    public void publish(final Event<?> event) {
        Preconditions.checkNotNull(event);
        synchronized (this) {
            Queue<Event<?>> queue = this.pendingEvents;
            if (queue != null) {
                queue.add(event);
                return;
            }
            for (final Map.Entry<EventHandler<Object>, Executor> handlerData : getHandlers(event)) {
                handlerData.getValue().execute(new Runnable() { // from class: com.google.firebase.components.EventBus$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ((EventHandler) handlerData.getKey()).handle(event);
                    }
                });
            }
        }
    }

    private synchronized Set<Map.Entry<EventHandler<Object>, Executor>> getHandlers(Event<?> event) {
        Map<EventHandler<Object>, Executor> handlers;
        handlers = this.handlerMap.get(event.getType());
        return handlers == null ? Collections.emptySet() : handlers.entrySet();
    }

    @Override // com.google.firebase.events.Subscriber
    public synchronized <T> void subscribe(Class<T> type, Executor executor, EventHandler<? super T> handler) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(handler);
        Preconditions.checkNotNull(executor);
        if (!this.handlerMap.containsKey(type)) {
            this.handlerMap.put(type, new ConcurrentHashMap<>());
        }
        this.handlerMap.get(type).put(handler, executor);
    }

    @Override // com.google.firebase.events.Subscriber
    public <T> void subscribe(Class<T> type, EventHandler<? super T> handler) {
        subscribe(type, this.defaultExecutor, handler);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<com.google.firebase.events.EventHandler<java.lang.Object>, java.util.concurrent.Executor> */
    @Override // com.google.firebase.events.Subscriber
    public synchronized <T> void unsubscribe(Class<T> type, EventHandler<? super T> handler) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(handler);
        if (!this.handlerMap.containsKey(type)) {
            return;
        }
        ConcurrentHashMap<EventHandler<Object>, Executor> concurrentHashMap = this.handlerMap.get(type);
        concurrentHashMap.remove(handler);
        if (concurrentHashMap.isEmpty()) {
            this.handlerMap.remove(type);
        }
    }

    public void enablePublishingAndFlushPending() {
        Queue<Event<?>> pending = null;
        synchronized (this) {
            Queue<Event<?>> queue = this.pendingEvents;
            if (queue != null) {
                pending = queue;
                this.pendingEvents = null;
            }
        }
        if (pending != null) {
            for (Event<?> event : pending) {
                publish(event);
            }
        }
    }
}
