package com.google.firebase.components;

import com.google.firebase.inject.Deferred$DeferredHandler;
import com.google.firebase.inject.Provider;
/* loaded from: classes.dex */
public class OptionalProvider<T> implements Provider<T> {
    private volatile Provider<T> delegate;
    private Deferred$DeferredHandler<T> handler;
    private static final Deferred$DeferredHandler<Object> NOOP_HANDLER = OptionalProvider$$ExternalSyntheticLambda0.INSTANCE;
    private static final Provider<Object> EMPTY_PROVIDER = OptionalProvider$$ExternalSyntheticLambda1.INSTANCE;

    public static /* synthetic */ void lambda$static$0(Provider provider) {
    }

    public static /* synthetic */ Object lambda$static$1() {
        return null;
    }

    private OptionalProvider(Deferred$DeferredHandler<T> deferred$DeferredHandler, Provider<T> provider) {
        this.handler = deferred$DeferredHandler;
        this.delegate = provider;
    }

    public static <T> OptionalProvider<T> empty() {
        return new OptionalProvider<>(NOOP_HANDLER, EMPTY_PROVIDER);
    }

    @Override // com.google.firebase.inject.Provider
    public T get() {
        return this.delegate.get();
    }

    public void set(Provider<T> provider) {
        Deferred$DeferredHandler<T> deferred$DeferredHandler;
        if (this.delegate != EMPTY_PROVIDER) {
            throw new IllegalStateException("provide() can be called only once.");
        }
        synchronized (this) {
            deferred$DeferredHandler = this.handler;
            this.handler = null;
            this.delegate = provider;
        }
        deferred$DeferredHandler.handle(provider);
    }
}
