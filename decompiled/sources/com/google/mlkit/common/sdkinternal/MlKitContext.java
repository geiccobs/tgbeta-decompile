package com.google.mlkit.common.sdkinternal;

import android.content.Context;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentDiscovery;
import com.google.firebase.components.ComponentRuntime;
import com.google.mlkit.common.internal.MlKitComponentDiscoveryService;
import java.util.concurrent.atomic.AtomicReference;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class MlKitContext {
    private static final AtomicReference<MlKitContext> zzb = new AtomicReference<>();
    private final ComponentRuntime zza;

    private MlKitContext(Context context) {
        ComponentRuntime componentRuntime = new ComponentRuntime(TaskExecutors.MAIN_THREAD, ComponentDiscovery.forContext(context, MlKitComponentDiscoveryService.class).discover(), Component.of(context, Context.class, new Class[0]), Component.of(this, MlKitContext.class, new Class[0]));
        this.zza = componentRuntime;
        componentRuntime.initializeEagerComponents(true);
    }

    public <T> T get(Class<T> cls) {
        Preconditions.checkState(zzb.get() == this, "MlKitContext has been deleted");
        return (T) this.zza.get(cls);
    }

    public static MlKitContext zza(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (applicationContext != null) {
            context = applicationContext;
        }
        MlKitContext mlKitContext = new MlKitContext(context);
        Preconditions.checkState(zzb.getAndSet(mlKitContext) == null, "MlKitContext is already initialized");
        return mlKitContext;
    }

    public static MlKitContext getInstance() {
        MlKitContext mlKitContext = zzb.get();
        Preconditions.checkState(mlKitContext != null, "MlKitContext has not been initialized");
        return mlKitContext;
    }

    public Context getApplicationContext() {
        return (Context) get(Context.class);
    }
}
