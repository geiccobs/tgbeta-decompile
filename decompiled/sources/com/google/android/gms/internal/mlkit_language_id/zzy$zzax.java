package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzax extends zzeo<zzy$zzax, zza> implements zzgb {
    private static final zzy$zzax zzh;
    private static volatile zzgj<zzy$zzax> zzi;
    private int zzc;
    private zzy$zzaf zzd;
    private zzy$zzae zze;
    private zzy$zzaw zzf;
    private zzew<zzy$zzan> zzg = zzeo.zzl();

    private zzy$zzax() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzax, zza> implements zzgb {
        private zza() {
            super(zzy$zzax.zzh);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzax>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzax> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzax();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0001\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003ဉ\u0002\u0004\u001b", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", zzy$zzan.class});
            case 4:
                return zzh;
            case 5:
                zzgj<zzy$zzax> zzgjVar2 = zzi;
                zzgj<zzy$zzax> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzax.class) {
                        zzgj<zzy$zzax> zzgjVar4 = zzi;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzh);
                            zzi = zzaVar;
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
        zzy$zzax zzy_zzax = new zzy$zzax();
        zzh = zzy_zzax;
        zzeo.zza(zzy$zzax.class, zzy_zzax);
    }
}
