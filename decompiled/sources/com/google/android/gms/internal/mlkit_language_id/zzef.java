package com.google.android.gms.internal.mlkit_language_id;

import java.util.Collections;
import java.util.HashMap;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public class zzef {
    private static volatile zzef zzc;
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
        new HashMap();
    }

    private zzef(boolean z) {
        Collections.emptyMap();
    }
}
