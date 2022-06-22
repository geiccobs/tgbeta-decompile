package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzaj extends zzeo<zzy$zzaj, zzb> implements zzgb {
    private static final zzy$zzaj zze;
    private static volatile zzgj<zzy$zzaj> zzf;
    private int zzc;
    private int zzd;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzy$zzaj, zzb> implements zzgb {
        private zzb() {
            super(zzy$zzaj.zze);
        }

        /* synthetic */ zzb(zzx zzxVar) {
            this();
        }
    }

    static {
        zzy$zzaj zzy_zzaj = new zzy$zzaj();
        zze = zzy_zzaj;
        zzeo.zza(zzy$zzaj.class, zzy_zzaj);
    }

    private zzy$zzaj() {
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzaj>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzaj> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzaj();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zze, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001ဌ\u0000", new Object[]{"zzc", "zzd", zza.zzb()});
            case 4:
                return zze;
            case 5:
                zzgj<zzy$zzaj> zzgjVar2 = zzf;
                zzgj<zzy$zzaj> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzaj.class) {
                        zzgj<zzy$zzaj> zzgjVar4 = zzf;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zze);
                            zzf = zzaVar;
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

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        UNKNOWN(0),
        TRANSLATE(1);
        
        private final int zzd;

        zza(int i) {
            this.zzd = i;
        }

        public static zzev zzb() {
            return zzaz.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzd + " name=" + name() + '>';
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzd;
        }

        static {
            new zzba();
        }
    }
}
