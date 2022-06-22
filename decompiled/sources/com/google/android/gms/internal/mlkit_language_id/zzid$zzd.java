package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzid$zzd extends zzeo<zzid$zzd, zza> implements zzgb {
    private static final zzid$zzd zzk;
    private static volatile zzgj<zzid$zzd> zzl;
    private int zzc;
    private int zzg;
    private int zzh;
    private int zzj;
    private String zzd = "";
    private String zze = "";
    private String zzf = "";
    private String zzi = "";

    private zzid$zzd() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzid$zzd, zza> implements zzgb {
        private zza() {
            super(zzid$zzd.zzk);
        }

        /* synthetic */ zza(zzic zzicVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzd>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzid$zzd> zzgjVar;
        switch (zzic.zza[i - 1]) {
            case 1:
                return new zzid$zzd();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzk, "\u0001\u0007\u0000\u0001\u0001\u0007\u0007\u0000\u0000\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0004င\u0003\u0005င\u0004\u0006ဈ\u0005\u0007င\u0006", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj"});
            case 4:
                return zzk;
            case 5:
                zzgj<zzid$zzd> zzgjVar2 = zzl;
                zzgj<zzid$zzd> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzid$zzd.class) {
                        zzgj<zzid$zzd> zzgjVar4 = zzl;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzk);
                            zzl = zzaVar;
                            zzgjVar = zzaVar;
                        }
                    }
                    zzgjVar3 = zzgjVar;
                }
                return zzgjVar3;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        zzid$zzd zzid_zzd = new zzid$zzd();
        zzk = zzid_zzd;
        zzeo.zza(zzid$zzd.class, zzid_zzd);
    }
}
