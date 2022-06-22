package com.google.android.gms.common.providers;

import androidx.annotation.RecentlyNonNull;
import java.util.concurrent.ScheduledExecutorService;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
@Deprecated
/* loaded from: classes.dex */
public class PooledExecutorsProvider {
    private static PooledExecutorFactory zza;

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public interface PooledExecutorFactory {
        @RecentlyNonNull
        @Deprecated
        ScheduledExecutorService newSingleThreadScheduledExecutor();
    }

    @RecentlyNonNull
    @Deprecated
    public static synchronized PooledExecutorFactory getInstance() {
        PooledExecutorFactory pooledExecutorFactory;
        synchronized (PooledExecutorsProvider.class) {
            if (zza == null) {
                zza = new zza();
            }
            pooledExecutorFactory = zza;
        }
        return pooledExecutorFactory;
    }
}
