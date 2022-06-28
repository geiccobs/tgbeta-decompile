package com.google.android.gms.internal.clearcut;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
/* loaded from: classes3.dex */
public enum zzfq {
    INT(0),
    LONG(0L),
    FLOAT(Float.valueOf(0.0f)),
    DOUBLE(Double.valueOf((double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE)),
    BOOLEAN(false),
    STRING(""),
    BYTE_STRING(zzbb.zzfi),
    ENUM(null),
    MESSAGE(null);
    
    private final Object zzlj;

    zzfq(Object obj) {
        this.zzlj = obj;
    }
}
