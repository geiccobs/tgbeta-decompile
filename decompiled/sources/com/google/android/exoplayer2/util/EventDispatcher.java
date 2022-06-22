package com.google.android.exoplayer2.util;

import android.os.Handler;
import com.google.android.exoplayer2.util.EventDispatcher;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public final class EventDispatcher<T> {
    private final CopyOnWriteArrayList<HandlerAndListener<T>> listeners = new CopyOnWriteArrayList<>();

    /* loaded from: classes.dex */
    public interface Event<T> {
        void sendTo(T t);
    }

    public void addListener(Handler handler, T t) {
        Assertions.checkArgument((handler == null || t == null) ? false : true);
        removeListener(t);
        this.listeners.add(new HandlerAndListener<>(handler, t));
    }

    public void removeListener(T t) {
        Iterator<HandlerAndListener<T>> it = this.listeners.iterator();
        while (it.hasNext()) {
            HandlerAndListener<T> next = it.next();
            if (((HandlerAndListener) next).listener == t) {
                next.release();
                this.listeners.remove(next);
            }
        }
    }

    public void dispatch(Event<T> event) {
        Iterator<HandlerAndListener<T>> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().dispatch(event);
        }
    }

    /* loaded from: classes.dex */
    public static final class HandlerAndListener<T> {
        private final Handler handler;
        private final T listener;
        private boolean released;

        public HandlerAndListener(Handler handler, T t) {
            this.handler = handler;
            this.listener = t;
        }

        public void release() {
            this.released = true;
        }

        public void dispatch(final Event<T> event) {
            this.handler.post(new Runnable() { // from class: com.google.android.exoplayer2.util.EventDispatcher$HandlerAndListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EventDispatcher.HandlerAndListener.this.lambda$dispatch$0(event);
                }
            });
        }

        public /* synthetic */ void lambda$dispatch$0(Event event) {
            if (!this.released) {
                event.sendTo(this.listener);
            }
        }
    }
}
