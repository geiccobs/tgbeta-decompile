package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
import com.google.android.gms.internal.mlkit_language_id.zzy$zzae;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzag extends zzeo<zzy$zzag, zzb> implements zzgb {
    private static final zzy$zzag zzk;
    private static volatile zzgj<zzy$zzag> zzl;
    private int zzc;
    private long zzd;
    private int zze;
    private int zzf;
    private int zzg;
    private int zzh;
    private int zzi;
    private int zzj;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        SOURCE_UNKNOWN(0),
        BITMAP(1),
        BYTEARRAY(2),
        BYTEBUFFER(3),
        FILEPATH(4),
        ANDROID_MEDIA_IMAGE(5);
        
        private final int zzh;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzh;
        }

        public static zzev zzb() {
            return zzax.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzh + " name=" + name() + '>';
        }

        zza(int i) {
            this.zzh = i;
        }

        static {
            new zzay();
        }
    }

    static {
        zzy$zzag zzy_zzag = new zzy$zzag();
        zzk = zzy_zzag;
        zzeo.zza(zzy$zzag.class, zzy_zzag);
    }

    private zzy$zzag() {
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzag>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzag> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzag();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zzk, "\u0001\u0007\u0000\u0001\u0001\u0007\u0007\u0000\u0000\u0000\u0001ဃ\u0000\u0002ဌ\u0001\u0003ဌ\u0002\u0004ဋ\u0003\u0005ဋ\u0004\u0006ဋ\u0005\u0007ဋ\u0006", new Object[]{"zzc", "zzd", "zze", zza.zzb(), "zzf", zzy$zzae.zzb.zzb(), "zzg", "zzh", "zzi", "zzj"});
            case 4:
                return zzk;
            case 5:
                zzgj<zzy$zzag> zzgjVar2 = zzl;
                zzgj<zzy$zzag> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzag.class) {
                        zzgj<zzy$zzag> zzgjVar4 = zzl;
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

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzy$zzag, zzb> implements zzgb {
        private zzb() {
            super(zzy$zzag.zzk);
        }

        /* synthetic */ zzb(zzx zzxVar) {
            this();
        }
    }
}
