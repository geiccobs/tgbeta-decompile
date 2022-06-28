package com.google.android.gms.internal.mlkit_language_id;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public class zzef {
    private static volatile zzef zzc;
    private final Map<Object, Object> zze;
    private static volatile boolean zza = false;
    private static boolean zzb = true;
    private static final zzef zzd = new zzef(true);

    public static zzef zza() {
        zzef zzefVar = zzc;
        if (zzefVar == null) {
            synchronized (zzef.class) {
                zzefVar = zzc;
                if (zzefVar == null) {
                    zzefVar = zzd;
                    zzc = zzefVar;
                }
            }
        }
        return zzefVar;
    }

    zzef() {
        this.zze = new HashMap();
    }

    private zzef(boolean z) {
        this.zze = Collections.emptyMap();
    }
}
