package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzae extends zzeo<zzy$zzae, zza> implements zzgb {
    private static final zzy$zzae zzg;
    private static volatile zzgj<zzy$zzae> zzh;
    private int zzc;
    private int zzd;
    private int zze;
    private int zzf;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzb implements zzet {
        UNKNOWN_FORMAT(0),
        NV16(1),
        NV21(2),
        YV12(3),
        YUV_420_888(7),
        JPEG(8),
        BITMAP(4),
        CM_SAMPLE_BUFFER_REF(5),
        UI_IMAGE(6);
        
        private final int zzk;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzk;
        }

        public static zzev zzb() {
            return zzaw.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzk + " name=" + name() + '>';
        }

        zzb(int i) {
            this.zzk = i;
        }

        static {
            new zzav();
        }
    }

    private zzy$zzae() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzae, zza> implements zzgb {
        private zza() {
            super(zzy$zzae.zzg);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzae>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzae> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzae();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzg, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001ဌ\u0000\u0002ဋ\u0001\u0003ဋ\u0002", new Object[]{"zzc", "zzd", zzb.zzb(), "zze", "zzf"});
            case 4:
                return zzg;
            case 5:
                zzgj<zzy$zzae> zzgjVar2 = zzh;
                zzgj<zzy$zzae> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzae.class) {
                        zzgj<zzy$zzae> zzgjVar4 = zzh;
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

    static {
        zzy$zzae zzy_zzae = new zzy$zzae();
        zzg = zzy_zzae;
        zzeo.zza(zzy$zzae.class, zzy_zzae);
    }
}
