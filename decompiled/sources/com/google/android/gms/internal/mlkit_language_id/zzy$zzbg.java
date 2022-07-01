package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzbg extends zzeo<zzy$zzbg, zzb> implements zzgb {
    private static final zzex<Integer, zza> zzf = new zzcc();
    private static final zzex<Integer, zza> zzh = new zzce();
    private static final zzex<Integer, zza> zzj = new zzcd();
    private static final zzy$zzbg zzl;
    private static volatile zzgj<zzy$zzbg> zzm;
    private int zzc;
    private long zzd;
    private zzeu zze = zzeo.zzk();
    private zzeu zzg = zzeo.zzk();
    private zzeu zzi = zzeo.zzk();
    private int zzk;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        UNKNOWN_ERROR(0),
        NO_CONNECTION(1),
        RPC_ERROR(2),
        RPC_RETURNED_INVALID_RESULT(3),
        RPC_RETURNED_MALFORMED_RESULT(4),
        RPC_EXPONENTIAL_BACKOFF_FAILED(5),
        DIRECTORY_CREATION_FAILED(10),
        FILE_WRITE_FAILED_DISK_FULL(11),
        FILE_WRITE_FAILED(12),
        FILE_READ_FAILED(13),
        FILE_READ_RETURNED_INVALID_DATA(14),
        FILE_READ_RETURNED_MALFORMED_DATA(15);
        
        private final int zzn;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzn;
        }

        public static zzev zzb() {
            return zzcf.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzn + " name=" + name() + '>';
        }

        zza(int i) {
            this.zzn = i;
        }

        static {
            new zzcg();
        }
    }

    private zzy$zzbg() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzb extends zzeo.zzb<zzy$zzbg, zzb> implements zzgb {
        private zzb() {
            super(zzy$zzbg.zzl);
        }

        /* synthetic */ zzb(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzbg>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzbg> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzbg();
            case 2:
                return new zzb(null);
            case 3:
                return zzeo.zza(zzl, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0003\u0000\u0001ဃ\u0000\u0002\u001e\u0003\u001e\u0004\u001e\u0005င\u0001", new Object[]{"zzc", "zzd", "zze", zza.zzb(), "zzg", zza.zzb(), "zzi", zza.zzb(), "zzk"});
            case 4:
                return zzl;
            case 5:
                zzgj<zzy$zzbg> zzgjVar2 = zzm;
                zzgj<zzy$zzbg> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzbg.class) {
                        zzgj<zzy$zzbg> zzgjVar4 = zzm;
                        zzgjVar = zzgjVar4;
                        if (zzgjVar4 == null) {
                            ?? zzaVar = new zzeo.zza(zzl);
                            zzm = zzaVar;
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

    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_language_id.zzcc, com.google.android.gms.internal.mlkit_language_id.zzex<java.lang.Integer, com.google.android.gms.internal.mlkit_language_id.zzy$zzbg$zza>] */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.google.android.gms.internal.mlkit_language_id.zzce, com.google.android.gms.internal.mlkit_language_id.zzex<java.lang.Integer, com.google.android.gms.internal.mlkit_language_id.zzy$zzbg$zza>] */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.google.android.gms.internal.mlkit_language_id.zzcd, com.google.android.gms.internal.mlkit_language_id.zzex<java.lang.Integer, com.google.android.gms.internal.mlkit_language_id.zzy$zzbg$zza>] */
    static {
        zzy$zzbg zzy_zzbg = new zzy$zzbg();
        zzl = zzy_zzbg;
        zzeo.zza(zzy$zzbg.class, zzy_zzbg);
    }
}
