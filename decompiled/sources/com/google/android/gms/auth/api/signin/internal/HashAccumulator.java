package com.google.android.gms.auth.api.signin.internal;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public class HashAccumulator {
    private static int zaa = 31;
    private int zab = 1;

    public HashAccumulator addObject(Object obj) {
        this.zab = (zaa * this.zab) + (obj == null ? 0 : obj.hashCode());
        return this;
    }

    public final HashAccumulator zaa(boolean z) {
        this.zab = (zaa * this.zab) + (z ? 1 : 0);
        return this;
    }

    public int hash() {
        return this.zab;
    }
}
