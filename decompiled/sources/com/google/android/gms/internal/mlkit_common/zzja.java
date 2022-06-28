package com.google.android.gms.internal.mlkit_common;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public enum zzja {
    INT(0),
    LONG(0L),
    FLOAT(Float.valueOf(0.0f)),
    DOUBLE(Double.valueOf((double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE)),
    BOOLEAN(false),
    STRING(""),
    BYTE_STRING(zzep.zza),
    ENUM(null),
    MESSAGE(null);
    
    private final Object zzj;

    zzja(Object obj) {
        this.zzj = obj;
    }
}
