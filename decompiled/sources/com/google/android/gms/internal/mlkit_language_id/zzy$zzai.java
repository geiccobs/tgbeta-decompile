package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzai extends zzeo<zzy$zzai, zza> implements zzgb {
    private static final zzy$zzai zzg;
    private static volatile zzgj<zzy$zzai> zzh;
    private int zzc;
    private float zzd;
    private float zze;
    private float zzf;

    private zzy$zzai() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzai, zza> implements zzgb {
        private zza() {
            super(zzy$zzai.zzg);
        }

        public final zza zza(float f) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzai) this.zza).zza(f);
            return this;
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    public final void zza(float f) {
        this.zzc |= 4;
        this.zzf = f;
    }

    public static zza zza() {
        return zzg.zzh();
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzai>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzai> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzai();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ခ\u0000\u0002ခ\u0001\u0003ခ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf"});
            case 4:
                return zzg;
            case 5:
                zzgj<zzy$zzai> zzgjVar2 = zzh;
                zzgj<zzy$zzai> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzai.class) {
                        zzgj<zzy$zzai> zzgjVar4 = zzh;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzg);
                            zzh = zzaVar;
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

    public static zzy$zzai zzb() {
        return zzg;
    }

    static {
        zzy$zzai zzy_zzai = new zzy$zzai();
        zzg = zzy_zzai;
        zzeo.zza(zzy$zzai.class, zzy_zzai);
    }
}
