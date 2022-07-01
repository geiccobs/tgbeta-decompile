package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzid$zzf extends zzeo<zzid$zzf, zza> implements zzgb {
    private static final zzid$zzf zzo;
    private static volatile zzgj<zzid$zzf> zzp;
    private int zzc;
    private zzid$zzb zzd;
    private zzid$zzi zze;
    private zzid$zzd zzf;
    private int zzg;
    private zzid$zzc zzh;
    private zzid$zzl zzi;
    private long zzj;
    private long zzk;
    private boolean zzl;
    private int zzm;
    private byte zzn = 2;

    private zzid$zzf() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzid$zzf, zza> implements zzgb {
        private zza() {
            super(zzid$zzf.zzo);
        }

        /* synthetic */ zza(zzic zzicVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r3v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzid$zzf>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzid$zzf> zzgjVar;
        int i2 = 1;
        switch (zzic.zza[i - 1]) {
            case 1:
                return new zzid$zzf();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzo, "\u0001\n\u0000\u0001\u0001\n\n\u0000\u0000\u0001\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဌ\u0003\u0004ဉ\u0004\u0005ᐉ\u0005\u0006ဂ\u0006\u0007ဂ\u0007\bဇ\b\tင\t\nဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzg", zzij.zzb(), "zzh", "zzi", "zzj", "zzk", "zzl", "zzm", "zzf"});
            case 4:
                return zzo;
            case 5:
                zzgj<zzid$zzf> zzgjVar2 = zzp;
                zzgj<zzid$zzf> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzid$zzf.class) {
                        zzgj<zzid$zzf> zzgjVar4 = zzp;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzo);
                            zzp = zzaVar;
                            zzgjVar = zzaVar;
                        }
                    }
                    zzgjVar3 = zzgjVar;
                }
                return zzgjVar3;
            case 6:
                return Byte.valueOf(this.zzn);
            case 7:
                if (obj == null) {
                    i2 = 0;
                }
                this.zzn = (byte) i2;
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        zzid$zzf zzid_zzf = new zzid$zzf();
        zzo = zzid_zzf;
        zzeo.zza(zzid$zzf.class, zzid_zzf);
    }
}
