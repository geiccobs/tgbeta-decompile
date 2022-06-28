package com.google.android.gms.internal.play_billing;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes3.dex */
public enum zza {
    RESPONSE_CODE_UNSPECIFIED(-999),
    SERVICE_TIMEOUT(-3),
    FEATURE_NOT_SUPPORTED(-2),
    SERVICE_DISCONNECTED(-1),
    OK(0),
    USER_CANCELED(1),
    SERVICE_UNAVAILABLE(2),
    BILLING_UNAVAILABLE(3),
    ITEM_UNAVAILABLE(4),
    DEVELOPER_ERROR(5),
    ERROR(6),
    ITEM_ALREADY_OWNED(7),
    ITEM_NOT_OWNED(8),
    EXPIRED_OFFER_TOKEN(11);
    
    private static final zzx zzo;
    private final int zzq;

    static {
        zza[] values;
        zzw zzwVar = new zzw();
        for (zza zzaVar : values()) {
            zzwVar.zza(Integer.valueOf(zzaVar.zzq), zzaVar);
        }
        zzo = zzwVar.zzb();
    }

    zza(int i) {
        this.zzq = i;
    }

    public static zza zza(int i) {
        zzx zzxVar = zzo;
        Integer valueOf = Integer.valueOf(i);
        return !zzxVar.containsKey(valueOf) ? RESPONSE_CODE_UNSPECIFIED : (zza) zzxVar.get(valueOf);
    }
}
