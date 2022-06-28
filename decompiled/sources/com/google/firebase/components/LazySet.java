package com.google.firebase.components;

import com.google.firebase.inject.Provider;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
/* loaded from: classes3.dex */
public class LazySet<T> implements Provider<Set<T>> {
    private volatile Set<T> actualSet = null;
    private volatile Set<Provider<T>> providers = Collections.newSetFromMap(new ConcurrentHashMap());

    LazySet(Collection<Provider<T>> providers) {
        this.providers.addAll(providers);
    }

    public static LazySet<?> fromCollection(Collection<Provider<?>> providers) {
        Collection<Provider<Object>> casted = (Set) providers;
        return new LazySet<>(casted);
    }

    @Override // com.google.firebase.inject.Provider
    public Set<T> get() {
        if (this.actualSet == null) {
            synchronized (this) {
                if (this.actualSet == null) {
                    this.actualSet = Collections.newSetFromMap(new ConcurrentHashMap());
                    updateSet();
                }
            }
        }
        return Collections.unmodifiableSet(this.actualSet);
    }

    public synchronized void add(Provider<T> newProvider) {
        if (this.actualSet == null) {
            this.providers.add(newProvider);
        } else {
            this.actualSet.add(newProvider.get());
        }
    }

    private synchronized void updateSet() {
        for (Provider<T> provider : this.providers) {
            this.actualSet.add(provider.get());
        }
        this.providers = null;
    }
}
