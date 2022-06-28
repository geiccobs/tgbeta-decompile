package com.google.firebase.components;

import com.google.firebase.inject.Deferred;
import com.google.firebase.inject.Provider;
/* loaded from: classes3.dex */
public class OptionalProvider<T> implements Provider<T>, Deferred<T> {
    private volatile Provider<T> delegate;
    private Deferred.DeferredHandler<T> handler;
    private static final Deferred.DeferredHandler<Object> NOOP_HANDLER = OptionalProvider$$ExternalSyntheticLambda1.INSTANCE;
    private static final Provider<Object> EMPTY_PROVIDER = OptionalProvider$$ExternalSyntheticLambda2.INSTANCE;

    public static /* synthetic */ void lambda$static$0(Provider p) {
    }

    public static /* synthetic */ Object lambda$static$1() {
        return null;
    }

    private OptionalProvider(Deferred.DeferredHandler<T> handler, Provider<T> provider) {
        this.handler = handler;
        this.delegate = provider;
    }

    public static <T> OptionalProvider<T> empty() {
        return new OptionalProvider<>(NOOP_HANDLER, EMPTY_PROVIDER);
    }

    public static <T> OptionalProvider<T> of(Provider<T> provider) {
        return new OptionalProvider<>(null, provider);
    }

    @Override // com.google.firebase.inject.Provider
    public T get() {
        return this.delegate.get();
    }

    public void set(Provider<T> provider) {
        Deferred.DeferredHandler<T> localHandler;
        if (this.delegate != EMPTY_PROVIDER) {
            throw new IllegalStateException("provide() can be called only once.");
        }
        synchronized (this) {
            localHandler = this.handler;
            this.handler = null;
            this.delegate = provider;
        }
        localHandler.handle(provider);
    }

    @Override // com.google.firebase.inject.Deferred
    public void whenAvailable(final Deferred.DeferredHandler<T> handler) {
        Provider<T> provider;
        Provider<T> provider2 = this.delegate;
        Provider<Object> provider3 = EMPTY_PROVIDER;
        if (provider2 != provider3) {
            handler.handle(provider2);
            return;
        }
        Provider<T> toRun = null;
        synchronized (this) {
            provider = this.delegate;
            if (provider != provider3) {
                toRun = provider;
            } else {
                final Deferred.DeferredHandler<T> existingHandler = this.handler;
                this.handler = new Deferred.DeferredHandler() { // from class: com.google.firebase.components.OptionalProvider$$ExternalSyntheticLambda0
                    @Override // com.google.firebase.inject.Deferred.DeferredHandler
                    public final void handle(Provider provider4) {
                        OptionalProvider.lambda$whenAvailable$2(Deferred.DeferredHandler.this, handler, provider4);
                    }
                };
            }
        }
        if (toRun != null) {
            handler.handle(provider);
        }
    }

    public static /* synthetic */ void lambda$whenAvailable$2(Deferred.DeferredHandler existingHandler, Deferred.DeferredHandler handler, Provider p) {
        existingHandler.handle(p);
        handler.handle(p);
    }
}
