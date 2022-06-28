package com.google.android.exoplayer2.util;

import android.os.Handler;
import com.google.android.exoplayer2.util.EventDispatcher;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes3.dex */
public final class EventDispatcher<T> {
    private final CopyOnWriteArrayList<HandlerAndListener<T>> listeners = new CopyOnWriteArrayList<>();

    /* loaded from: classes3.dex */
    public interface Event<T> {
        void sendTo(T t);
    }

    public void addListener(Handler handler, T eventListener) {
        Assertions.checkArgument((handler == null || eventListener == null) ? false : true);
        removeListener(eventListener);
        this.listeners.add(new HandlerAndListener<>(handler, eventListener));
    }

    public void removeListener(T eventListener) {
        Iterator<HandlerAndListener<T>> it = this.listeners.iterator();
        while (it.hasNext()) {
            HandlerAndListener<T> handlerAndListener = it.next();
            if (((HandlerAndListener) handlerAndListener).listener == eventListener) {
                handlerAndListener.release();
                this.listeners.remove(handlerAndListener);
            }
        }
    }

    public void dispatch(Event<T> event) {
        Iterator<HandlerAndListener<T>> it = this.listeners.iterator();
        while (it.hasNext()) {
            HandlerAndListener<T> handlerAndListener = it.next();
            handlerAndListener.dispatch(event);
        }
    }

    /* loaded from: classes3.dex */
    public static final class HandlerAndListener<T> {
        private final Handler handler;
        private final T listener;
        private boolean released;

        public HandlerAndListener(Handler handler, T eventListener) {
            this.handler = handler;
            this.listener = eventListener;
        }

        public void release() {
            this.released = true;
        }

        public void dispatch(final Event<T> event) {
            this.handler.post(new Runnable() { // from class: com.google.android.exoplayer2.util.EventDispatcher$HandlerAndListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EventDispatcher.HandlerAndListener.this.m78xc52e451c(event);
                }
            });
        }

        /* renamed from: lambda$dispatch$0$com-google-android-exoplayer2-util-EventDispatcher$HandlerAndListener */
        public /* synthetic */ void m78xc52e451c(Event event) {
            if (!this.released) {
                event.sendTo(this.listener);
            }
        }
    }
}
