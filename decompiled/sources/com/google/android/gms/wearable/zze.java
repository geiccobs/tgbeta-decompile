package com.google.android.gms.wearable;

import com.google.android.gms.common.Feature;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zze {
    public static final Feature zza;
    public static final Feature zzb;
    public static final Feature zzc;
    public static final Feature zzd;
    public static final Feature[] zze;

    static {
        Feature feature = new Feature("wearable_services", 1L);
        zza = feature;
        Feature feature2 = new Feature("carrier_auth", 1L);
        zzb = feature2;
        Feature feature3 = new Feature("wear3_oem_companion", 1L);
        zzc = feature3;
        Feature feature4 = new Feature("wear_fast_pair_account_key_sync", 1L);
        zzd = feature4;
        zze = new Feature[]{feature, feature2, feature3, feature4};
    }
}
