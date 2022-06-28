package com.android.billingclient.api;

import android.text.TextUtils;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes3.dex */
public final class zzbt {
    private String zza;

    private zzbt() {
    }

    public /* synthetic */ zzbt(zzbs zzbsVar) {
    }

    public final zzbt zza(String str) {
        this.zza = str;
        return this;
    }

    public final zzbv zzb() {
        if (TextUtils.isEmpty(this.zza)) {
            throw new IllegalArgumentException("SKU must be set.");
        }
        return new zzbv(this.zza, null, null, 0, null);
    }
}
