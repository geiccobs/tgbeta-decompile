package com.google.android.gms.internal.mlkit_common;

import android.content.Context;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.Dependency;
import com.google.mlkit.common.sdkinternal.CommonUtils;
import com.google.mlkit.common.sdkinternal.MLTaskExecutor;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;
import java.util.HashMap;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
public final class zzds {
    public static final Component<?> zza = Component.builder(zzds.class).add(Dependency.required(Context.class)).add(Dependency.required(SharedPrefManager.class)).add(Dependency.required(zza.class)).factory(zzdv.zza).build();
    private final zza zze;

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes.dex */
    public interface zza {
    }

    private zzds(Context context, SharedPrefManager sharedPrefManager, zza zzaVar) {
        new HashMap();
        new HashMap();
        context.getPackageName();
        CommonUtils.getAppVersion(context);
        this.zze = zzaVar;
        MLTaskExecutor.getInstance().scheduleCallable(zzdr.zza);
        MLTaskExecutor mLTaskExecutor = MLTaskExecutor.getInstance();
        sharedPrefManager.getClass();
        mLTaskExecutor.scheduleCallable(zzdu.zza(sharedPrefManager));
    }

    public static final /* synthetic */ zzds zza(ComponentContainer componentContainer) {
        return new zzds((Context) componentContainer.get(Context.class), (SharedPrefManager) componentContainer.get(SharedPrefManager.class), (zza) componentContainer.get(zza.class));
    }
}
