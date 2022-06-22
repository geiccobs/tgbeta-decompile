package com.google.android.gms.internal.mlkit_language_id;

import android.content.Context;
import com.google.android.gms.clearcut.ClearcutLogger;
import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.internal.mlkit_language_id.zzcv;
import com.google.firebase.components.Component;
import com.google.firebase.components.Dependency;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public class zzcr implements zzcv.zzb {
    private final ClearcutLogger zzc;
    private static final GmsLogger zzb = new GmsLogger("ClearcutTransport", "");
    public static final Component<?> zza = Component.builder(zzcr.class).add(Dependency.required(Context.class)).factory(zzcq.zza).build();

    public zzcr(Context context) {
        this.zzc = ClearcutLogger.anonymousLogger(context, "FIREBASE_ML_SDK");
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzcv.zzb
    public final void zza(zzy$zzad zzy_zzad) {
        GmsLogger gmsLogger = zzb;
        String valueOf = String.valueOf(zzy_zzad);
        StringBuilder sb = new StringBuilder(valueOf.length() + 30);
        sb.append("Logging FirebaseMlSdkLogEvent ");
        sb.append(valueOf);
        gmsLogger.d("ClearcutTransport", sb.toString());
        try {
            this.zzc.newEvent(zzy_zzad.zzf()).log();
        } catch (SecurityException e) {
            zzb.e("ClearcutTransport", "Exception thrown from the logging side", e);
        }
    }
}
