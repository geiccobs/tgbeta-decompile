package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
import org.telegram.tgnet.ConnectionsManager;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzao extends zzeo<zzy$zzao, zzc> implements zzgb {
    private static final zzex<Integer, zza> zzg = new zzbk();
    private static final zzex<Integer, zzb> zzi = new zzbj();
    private static final zzy$zzao zzk;
    private static volatile zzgj<zzy$zzao> zzl;
    private int zzc;
    private zzy$zzaf zzd;
    private zzci$zza zze;
    private zzeu zzf = zzeo.zzk();
    private zzeu zzh = zzeo.zzk();
    private zzy$zzae zzj;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zza implements zzet {
        FORMAT_UNKNOWN(0),
        FORMAT_CODE_128(1),
        FORMAT_CODE_39(2),
        FORMAT_CODE_93(4),
        FORMAT_CODABAR(8),
        FORMAT_DATA_MATRIX(16),
        FORMAT_EAN_13(32),
        FORMAT_EAN_8(64),
        FORMAT_ITF(ConnectionsManager.RequestFlagNeedQuickAck),
        FORMAT_QR_CODE(256),
        FORMAT_UPC_A(512),
        FORMAT_UPC_E(1024),
        FORMAT_PDF417(2048),
        FORMAT_AZTEC(4096);
        
        private final int zzp;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzp;
        }

        public static zzev zzb() {
            return zzbm.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zza.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzp + " name=" + name() + '>';
        }

        zza(int i) {
            this.zzp = i;
        }

        static {
            new zzbl();
        }
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzb implements zzet {
        TYPE_UNKNOWN(0),
        TYPE_CONTACT_INFO(1),
        TYPE_EMAIL(2),
        TYPE_ISBN(3),
        TYPE_PHONE(4),
        TYPE_PRODUCT(5),
        TYPE_SMS(6),
        TYPE_TEXT(7),
        TYPE_URL(8),
        TYPE_WIFI(9),
        TYPE_GEO(10),
        TYPE_CALENDAR_EVENT(11),
        TYPE_DRIVER_LICENSE(12);
        
        private final int zzo;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzo;
        }

        public static zzev zzb() {
            return zzbn.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzo + " name=" + name() + '>';
        }

        zzb(int i) {
            this.zzo = i;
        }

        static {
            new zzbo();
        }
    }

    private zzy$zzao() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zzc extends zzeo.zzb<zzy$zzao, zzc> implements zzgb {
        private zzc() {
            super(zzy$zzao.zzk);
        }

        /* synthetic */ zzc(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzao>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzao> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzao();
            case 2:
                return new zzc(null);
            case 3:
                return zzeo.zza(zzk, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0002\u0000\u0001ဉ\u0000\u0002ဉ\u0001\u0003\u001e\u0004\u001e\u0005ဉ\u0002", new Object[]{"zzc", "zzd", "zze", "zzf", zza.zzb(), "zzh", zzb.zzb(), "zzj"});
            case 4:
                return zzk;
            case 5:
                zzgj<zzy$zzao> zzgjVar2 = zzl;
                zzgj<zzy$zzao> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzao.class) {
                        zzgj<zzy$zzao> zzgjVar4 = zzl;
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

    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.android.gms.internal.mlkit_language_id.zzbk, com.google.android.gms.internal.mlkit_language_id.zzex<java.lang.Integer, com.google.android.gms.internal.mlkit_language_id.zzy$zzao$zza>] */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.google.android.gms.internal.mlkit_language_id.zzex<java.lang.Integer, com.google.android.gms.internal.mlkit_language_id.zzy$zzao$zzb>, com.google.android.gms.internal.mlkit_language_id.zzbj] */
    static {
        zzy$zzao zzy_zzao = new zzy$zzao();
        zzk = zzy_zzao;
        zzeo.zza(zzy$zzao.class, zzy_zzao);
    }
}
