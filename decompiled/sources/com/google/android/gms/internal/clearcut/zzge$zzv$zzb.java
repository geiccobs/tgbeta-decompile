package com.google.android.gms.internal.clearcut;
/* loaded from: classes.dex */
public enum zzge$zzv$zzb implements zzcj {
    DEFAULT(0),
    UNMETERED_ONLY(1),
    UNMETERED_OR_DAILY(2),
    FAST_IF_RADIO_AWAKE(3),
    NEVER(4);
    
    private final int value;

    static {
        new zzck<zzge$zzv$zzb>() { // from class: com.google.android.gms.internal.clearcut.zzgr
            @Override // com.google.android.gms.internal.clearcut.zzck
            public final /* synthetic */ zzge$zzv$zzb zzb(int i) {
                return zzge$zzv$zzb.zzbc(i);
            }
        };
    }

    zzge$zzv$zzb(int i) {
        this.value = i;
    }

    public static zzge$zzv$zzb zzbc(int i) {
        if (i != 0) {
            if (i == 1) {
                return UNMETERED_ONLY;
            }
            if (i == 2) {
                return UNMETERED_OR_DAILY;
            }
            if (i == 3) {
                return FAST_IF_RADIO_AWAKE;
            }
            if (i == 4) {
                return NEVER;
            }
            return null;
        }
        return DEFAULT;
    }

    @Override // com.google.android.gms.internal.clearcut.zzcj
    public final int zzc() {
        return this.value;
    }
}
