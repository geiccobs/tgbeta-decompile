package com.google.android.gms.internal.mlkit_common;

import android.content.Context;
import com.google.android.gms.clearcut.ClearcutLogger;
import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.internal.mlkit_common.zzds;
import com.google.firebase.components.Component;
import com.google.firebase.components.Dependency;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
public class zzdo implements zzds.zza {
    public static final Component<?> zza = Component.builder(zzdo.class).add(Dependency.required(Context.class)).factory(zzdn.zza).build();

    public zzdo(Context context) {
        ClearcutLogger.anonymousLogger(context, "FIREBASE_ML_SDK");
    }

    static {
        new GmsLogger("ClearcutTransport", "");
    }
}
