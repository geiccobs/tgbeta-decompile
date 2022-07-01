package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzan extends zzeo<zzy$zzan, zza> implements zzgb {
    private static final zzy$zzan zzh;
    private static volatile zzgj<zzy$zzan> zzi;
    private int zzc;
    private int zzd;
    private float zze;
    private int zzf;
    private int zzg;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public enum zzb implements zzet {
        CATEGORY_UNKNOWN(0),
        CATEGORY_HOME_GOOD(1),
        CATEGORY_FASHION_GOOD(2),
        CATEGORY_ANIMAL(3),
        CATEGORY_FOOD(4),
        CATEGORY_PLACE(5),
        CATEGORY_PLANT(6);
        
        private final int zzi;

        @Override // com.google.android.gms.internal.mlkit_language_id.zzet
        public final int zza() {
            return this.zzi;
        }

        public static zzev zzb() {
            return zzbi.zza;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return "<" + zzb.class.getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " number=" + this.zzi + " name=" + name() + '>';
        }

        zzb(int i) {
            this.zzi = i;
        }

        static {
            new zzbh();
        }
    }

    private zzy$zzan() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzan, zza> implements zzgb {
        private zza() {
            super(zzy$zzan.zzh);
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzan>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzan> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzan();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzh, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001ဌ\u0000\u0002ခ\u0001\u0003င\u0002\u0004ဋ\u0003", new Object[]{"zzc", "zzd", zzb.zzb(), "zze", "zzf", "zzg"});
            case 4:
                return zzh;
            case 5:
                zzgj<zzy$zzan> zzgjVar2 = zzi;
                zzgj<zzy$zzan> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzan.class) {
                        zzgj<zzy$zzan> zzgjVar4 = zzi;
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
        zzy$zzan zzy_zzan = new zzy$zzan();
        zzh = zzy_zzan;
        zzeo.zza(zzy$zzan.class, zzy_zzan);
    }
}
