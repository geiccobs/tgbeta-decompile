package com.google.android.gms.common.api.internal;

import android.os.Looper;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.internal.Preconditions;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public class ListenerHolders {
    private final Set<ListenerHolder<?>> zaa = Collections.newSetFromMap(new WeakHashMap());

    public final void zaa() {
        for (ListenerHolder<?> listenerHolder : this.zaa) {
            listenerHolder.clear();
        }
        this.zaa.clear();
    }

    @RecentlyNonNull
    public static <L> ListenerHolder<L> createListenerHolder(@RecentlyNonNull L l, @RecentlyNonNull Looper looper, @RecentlyNonNull String str) {
        Preconditions.checkNotNull(l, "Listener must not be null");
        Preconditions.checkNotNull(looper, "Looper must not be null");
        Preconditions.checkNotNull(str, "Listener type must not be null");
        return new ListenerHolder<>(looper, l, str);
    }

    @RecentlyNonNull
    public static <L> ListenerHolder.ListenerKey<L> createListenerKey(@RecentlyNonNull L l, @RecentlyNonNull String str) {
        Preconditions.checkNotNull(l, "Listener must not be null");
        Preconditions.checkNotNull(str, "Listener type must not be null");
        Preconditions.checkNotEmpty(str, "Listener type must not be empty");
        return new ListenerHolder.ListenerKey<>(l, str);
    }
}
