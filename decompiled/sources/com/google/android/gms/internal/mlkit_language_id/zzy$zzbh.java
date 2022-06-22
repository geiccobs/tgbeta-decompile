package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
import org.telegram.tgnet.ConnectionsManager;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzy$zzbh extends zzeo<zzy$zzbh, zza> implements zzgb {
    private static final zzy$zzbh zzo;
    private static volatile zzgj<zzy$zzbh> zzp;
    private int zzc;
    private String zzd = "";
    private String zze = "";
    private String zzf = "";
    private String zzg = "";
    private String zzh = "";
    private String zzi = "";
    private String zzj = "";
    private zzew<String> zzk = zzeo.zzl();
    private String zzl = "";
    private boolean zzm;
    private boolean zzn;

    private zzy$zzbh() {
    }

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes.dex */
    public static final class zza extends zzeo.zzb<zzy$zzbh, zza> implements zzgb {
        private zza() {
            super(zzy$zzbh.zzo);
        }

        public final zza zza(String str) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzbh) this.zza).zza(str);
            return this;
        }

        public final zza zzb(String str) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzbh) this.zza).zzb(str);
            return this;
        }

        public final zza zzc(String str) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzbh) this.zza).zzc(str);
            return this;
        }

        public final zza zzd(String str) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzbh) this.zza).zzd(str);
            return this;
        }

        public final zza zza(Iterable<String> iterable) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzbh) this.zza).zza(iterable);
            return this;
        }

        public final zza zze(String str) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzbh) this.zza).zze(str);
            return this;
        }

        public final zza zza(boolean z) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzbh) this.zza).zza(true);
            return this;
        }

        public final zza zzb(boolean z) {
            if (this.zzb) {
                zzc();
                this.zzb = false;
            }
            ((zzy$zzbh) this.zza).zzb(true);
            return this;
        }

        /* synthetic */ zza(zzx zzxVar) {
            this();
        }
    }

    public final void zza(String str) {
        str.getClass();
        this.zzc |= 1;
        this.zzd = str;
    }

    public final void zzb(String str) {
        str.getClass();
        this.zzc |= 2;
        this.zze = str;
    }

    public final void zzc(String str) {
        str.getClass();
        this.zzc |= 8;
        this.zzg = str;
    }

    public final String zza() {
        return this.zzh;
    }

    public final void zzd(String str) {
        str.getClass();
        this.zzc |= 16;
        this.zzh = str;
    }

    public final void zza(Iterable<String> iterable) {
        zzew<String> zzewVar = this.zzk;
        if (!zzewVar.zza()) {
            this.zzk = zzeo.zza(zzewVar);
        }
        zzde.zza(iterable, this.zzk);
    }

    public final void zze(String str) {
        str.getClass();
        this.zzc |= ConnectionsManager.RequestFlagNeedQuickAck;
        this.zzl = str;
    }

    public final void zza(boolean z) {
        this.zzc |= 256;
        this.zzm = true;
    }

    public final void zzb(boolean z) {
        this.zzc |= 512;
        this.zzn = true;
    }

    public static zza zzb() {
        return zzo.zzh();
    }

    public static zza zza(zzy$zzbh zzy_zzbh) {
        return zzo.zza(zzy_zzbh);
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.mlkit_language_id.zzgj<com.google.android.gms.internal.mlkit_language_id.zzy$zzbh>, com.google.android.gms.internal.mlkit_language_id.zzeo$zza] */
    @Override // com.google.android.gms.internal.mlkit_language_id.zzeo
    public final Object zza(int i, Object obj, Object obj2) {
        zzgj<zzy$zzbh> zzgjVar;
        switch (zzx.zza[i - 1]) {
            case 1:
                return new zzy$zzbh();
            case 2:
                return new zza(null);
            case 3:
                return zzeo.zza(zzo, "\u0001\u000b\u0000\u0001\u0001\u000b\u000b\u0000\u0001\u0000\u0001ဈ\u0000\u0002ဈ\u0001\u0003ဈ\u0002\u0004ဈ\u0003\u0005ဈ\u0004\u0006ဈ\u0005\u0007ဈ\u0006\b\u001a\tဈ\u0007\nဇ\b\u000bဇ\t", new Object[]{"zzc", "zzd", "zze", "zzf", "zzg", "zzh", "zzi", "zzj", "zzk", "zzl", "zzm", "zzn"});
            case 4:
                return zzo;
            case 5:
                zzgj<zzy$zzbh> zzgjVar2 = zzp;
                zzgj<zzy$zzbh> zzgjVar3 = zzgjVar2;
                if (zzgjVar2 == null) {
                    synchronized (zzy$zzbh.class) {
                        zzgj<zzy$zzbh> zzgjVar4 = zzp;
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
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static zzy$zzbh zzc() {
        return zzo;
    }

    static {
        zzy$zzbh zzy_zzbh = new zzy$zzbh();
        zzo = zzy_zzbh;
        zzeo.zza(zzy$zzbh.class, zzy_zzbh);
    }
}
