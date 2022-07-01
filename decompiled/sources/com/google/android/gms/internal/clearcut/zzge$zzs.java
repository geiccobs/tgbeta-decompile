package com.google.android.gms.internal.clearcut;

import com.google.android.gms.internal.clearcut.zzcg;
import org.telegram.messenger.R;
/* loaded from: classes.dex */
public final class zzge$zzs extends zzcg<zzge$zzs, zza> implements zzdq {
    private static final zzge$zzs zzbfc;
    private static volatile zzdz<zzge$zzs> zzbg;
    private int zzbb;
    private int zzbfa = -1;
    private int zzbfb;

    /* loaded from: classes.dex */
    public static final class zza extends zzcg.zza<zzge$zzs, zza> implements zzdq {
        private zza() {
            super(zzge$zzs.zzbfc);
        }

        /* synthetic */ zza(zzgf zzgfVar) {
            this();
        }
    }

    /* loaded from: classes.dex */
    public enum zzb implements zzcj {
        UNKNOWN_MOBILE_SUBTYPE(0),
        GPRS(1),
        EDGE(2),
        UMTS(3),
        CDMA(4),
        EVDO_0(5),
        EVDO_A(6),
        RTT(7),
        HSDPA(8),
        HSUPA(9),
        HSPA(10),
        IDEN(11),
        EVDO_B(12),
        LTE(13),
        EHRPD(14),
        HSPAP(15),
        GSM(16),
        TD_SCDMA(17),
        IWLAN(18),
        LTE_CA(19),
        COMBINED(100);
        
        private static final zzck<zzb> zzbq = new zzgo();
        private final int value;

        zzb(int i) {
            this.value = i;
        }

        public static zzb zzaz(int i) {
            if (i != 100) {
                switch (i) {
                    case 0:
                        return UNKNOWN_MOBILE_SUBTYPE;
                    case 1:
                        return GPRS;
                    case 2:
                        return EDGE;
                    case 3:
                        return UMTS;
                    case 4:
                        return CDMA;
                    case 5:
                        return EVDO_0;
                    case 6:
                        return EVDO_A;
                    case 7:
                        return RTT;
                    case 8:
                        return HSDPA;
                    case 9:
                        return HSUPA;
                    case 10:
                        return HSPA;
                    case 11:
                        return IDEN;
                    case 12:
                        return EVDO_B;
                    case 13:
                        return LTE;
                    case 14:
                        return EHRPD;
                    case 15:
                        return HSPAP;
                    case 16:
                        return GSM;
                    case 17:
                        return TD_SCDMA;
                    case R.styleable.MapAttrs_uiScrollGesturesDuringRotateOrZoom /* 18 */:
                        return IWLAN;
                    case R.styleable.MapAttrs_uiTiltGestures /* 19 */:
                        return LTE_CA;
                    default:
                        return null;
                }
            }
            return COMBINED;
        }

        public static zzck<zzb> zzd() {
            return zzbq;
        }

        @Override // com.google.android.gms.internal.clearcut.zzcj
        public final int zzc() {
            return this.value;
        }
    }

    /* loaded from: classes.dex */
    public enum zzc implements zzcj {
        NONE(-1),
        MOBILE(0),
        WIFI(1),
        MOBILE_MMS(2),
        MOBILE_SUPL(3),
        MOBILE_DUN(4),
        MOBILE_HIPRI(5),
        WIMAX(6),
        BLUETOOTH(7),
        DUMMY(8),
        ETHERNET(9),
        MOBILE_FOTA(10),
        MOBILE_IMS(11),
        MOBILE_CBS(12),
        WIFI_P2P(13),
        MOBILE_IA(14),
        MOBILE_EMERGENCY(15),
        PROXY(16),
        VPN(17);
        
        private static final zzck<zzc> zzbq = new zzgp();
        private final int value;

        zzc(int i) {
            this.value = i;
        }

        public static zzc zzba(int i) {
            switch (i) {
                case -1:
                    return NONE;
                case 0:
                    return MOBILE;
                case 1:
                    return WIFI;
                case 2:
                    return MOBILE_MMS;
                case 3:
                    return MOBILE_SUPL;
                case 4:
                    return MOBILE_DUN;
                case 5:
                    return MOBILE_HIPRI;
                case 6:
                    return WIMAX;
                case 7:
                    return BLUETOOTH;
                case 8:
                    return DUMMY;
                case 9:
                    return ETHERNET;
                case 10:
                    return MOBILE_FOTA;
                case 11:
                    return MOBILE_IMS;
                case 12:
                    return MOBILE_CBS;
                case 13:
                    return WIFI_P2P;
                case 14:
                    return MOBILE_IA;
                case 15:
                    return MOBILE_EMERGENCY;
                case 16:
                    return PROXY;
                case 17:
                    return VPN;
                default:
                    return null;
            }
        }

        public static zzck<zzc> zzd() {
            return zzbq;
        }

        @Override // com.google.android.gms.internal.clearcut.zzcj
        public final int zzc() {
            return this.value;
        }
    }

    static {
        zzge$zzs zzge_zzs = new zzge$zzs();
        zzbfc = zzge_zzs;
        zzcg.zza(zzge$zzs.class, zzge_zzs);
    }

    private zzge$zzs() {
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [com.google.android.gms.internal.clearcut.zzdz<com.google.android.gms.internal.clearcut.zzge$zzs>, com.google.android.gms.internal.clearcut.zzcg$zzb] */
    @Override // com.google.android.gms.internal.clearcut.zzcg
    public final Object zza(int i, Object obj, Object obj2) {
        zzdz<zzge$zzs> zzdzVar;
        switch (zzgf.zzba[i - 1]) {
            case 1:
                return new zzge$zzs();
            case 2:
                return new zza(null);
            case 3:
                return zzcg.zza(zzbfc, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0003\u0000\u0000\u0000\u0001\f\u0000\u0002\f\u0001", new Object[]{"zzbb", "zzbfa", zzc.zzd(), "zzbfb", zzb.zzd()});
            case 4:
                return zzbfc;
            case 5:
                zzdz<zzge$zzs> zzdzVar2 = zzbg;
                zzdz<zzge$zzs> zzdzVar3 = zzdzVar2;
                if (zzdzVar2 == null) {
                    synchronized (zzge$zzs.class) {
                        zzdz<zzge$zzs> zzdzVar4 = zzbg;
                        zzdzVar = zzdzVar4;
                        if (zzdzVar4 == null) {
                            ?? zzbVar = new zzcg.zzb(zzbfc);
                            zzbg = zzbVar;
                            zzdzVar = zzbVar;
                        }
                    }
                    zzdzVar3 = zzdzVar;
                }
                return zzdzVar3;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
