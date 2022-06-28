package com.google.android.gms.internal.mlkit_common;

import java.io.IOException;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class zzgb extends IOException {
    private zzhb zza = null;

    public zzgb(String str) {
        super(str);
    }

    public static zzga zza() {
        return new zzga("Protocol message tag had invalid wire type.");
    }
}
