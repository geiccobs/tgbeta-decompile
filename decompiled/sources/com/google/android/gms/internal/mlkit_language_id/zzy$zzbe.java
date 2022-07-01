package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzbe extends zzeo<zzy$zzbe, zza> implements zzgb {
    private static final zzy$zzbe zzl;
    private static volatile zzgj<zzy$zzbe> zzm;
    private int zzc;
    private zzy$zzaf zzd;
    private zzy$zzbi zze;
    private int zzf;
    private int zzg;
    private int zzh;
    private int zzi;
    private int zzj;
    private int zzk;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzb implements zzet {
        NO_ERROR(0),
        METADATA_FILE_UNAVAILABLE(1),
        METADATA_ENTRY_NOT_FOUND(2),
        METADATA_JSON_INVALID(3),
        METADATA_HASH_NOT_FOUND(4),
        DOWNLOAD_MANAGER_SERVICE_MISSING(5),
        DOWNLOAD_MANAGER_HTTP_UNKNOWN_STATUS(6),
        DOWNLOAD_MANAGER_HTTP_BAD_REQUEST(400),
        DOWNLOAD_MANAGER_HTTP_UNAUTHORIZED(401),
        DOWNLOAD_MANAGER_HTTP_FORBIDDEN(403),
        DOWNLOAD_MANAGER_HTTP_NOT_FOUND(404),
        DOWNLOAD_MANAGER_HTTP_REQUEST_TIMEOUT(408),
        DOWNLOAD_MANAGER_HTTP_ABORTED(409),
        DOWNLOAD_MANAGER_HTTP_TOO_MANY_REQUESTS(429),
        DOWNLOAD_MANAGER_HTTP_CANCELLED(499),
        DOWNLOAD_MANAGER_HTTP_UNIMPLEMENTED(501),
        DOWNLOAD_MANAGER_HTTP_INTERNAL_SERVICE_ERROR(500),
        DOWNLOAD_MANAGER_HTTP_SERVICE_UNAVAILABLE(503),
        DOWNLOAD_MANAGER_HTTP_DEADLINE_EXCEEDED(504),
        DOWNLOAD_MANAGER_HTTP_NETWORK_AUTHENTICATION_REQUIRED(511),
        DOWNLOAD_MANAGER_FILE_ERROR(7),
        DOWNLOAD_MANAGER_UNHANDLED_HTTP_CODE(8),
        DOWNLOAD_MANAGER_HTTP_DATA_ERROR(9),
        DOWNLOAD_MANAGER_TOO_MANY_REDIRECTS(10),
        DOWNLOAD_MANAGER_INSUFFICIENT_SPACE(11),
        DOWNLOAD_MANAGER_DEVICE_NOT_FOUND(12),
        DOWNLOAD_MANAGER_CANNOT_RESUME(13),
        DOWNLOAD_MANAGER_FILE_ALREADY_EXISTS(14),
        DOWNLOAD_MANAGER_UNKNOWN_ERROR(15),
        POST_DOWNLOAD_FILE_NOT_FOUND(16),
        POST_DOWNLOAD_MOVE_FILE_FAILED(17),
        POST_DOWNLOAD_UNZIP_FAILED(18),
        RAPID_RESPONSE_COULD_NOT_BE_WRITTEN(19),
        DRIVER_OBJECT_DEALLOCATED(20);
        
        private final int zzaj;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzaj;
        }

        public static zzev zzb() {
            return zzbz.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzaj + " name=" + name() + '>';
        }

        zzb(int i) {
            this.zzaj = i;
        }

        static {
            new zzby();
        }
    }

    private zzy$zzbe() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzbe, zza> implements zzgb {
        private zza() {
            super(zzy$zzbe.zzl);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzbe>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzbe> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzbe();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzl, "\u0001\b\u0000\u0001\u0001\b\b\u0000\u0000\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003င\u0002\u0004င\u0003\u0005င\u0004\u0006င\u0005\u0007ဌ\u0006\bင\u0007", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj", zzb.zzb(), "zzk"});
            case 4:
                return zzl;
            case 5:
                zzgj<zzy$zzbe> zzgjVar2 = zzm;
                zzgj<zzy$zzbe> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzbe.class) {
                        zzgj<zzy$zzbe> zzgjVar4 = zzm;
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

    static {
        zzy$zzbe zzy_zzbe = new zzy$zzbe();
        zzl = zzy_zzbe;
        zzeo.zza(zzy$zzbe.class, zzy_zzbe);
    }
}
