package com.google.android.gms.internal.mlkit_common;

import androidx.core.text.HtmlCompat;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.UndoView;
import sun.misc.Unsafe;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzhf<T> implements zzhr<T> {
    private static final int[] zza = new int[0];
    private static final Unsafe zzb = zzip.zzc();
    private final int[] zzc;
    private final Object[] zzd;
    private final int zze;
    private final int zzf;
    private final zzhb zzg;
    private final boolean zzh;
    private final boolean zzi;
    private final boolean zzj;
    private final boolean zzk;
    private final int[] zzl;
    private final int zzm;
    private final int zzn;
    private final zzhg zzo;
    private final zzgl zzp;
    private final zzij<?, ?> zzq;
    private final zzfg<?> zzr;
    private final zzgu zzs;

    private zzhf(int[] iArr, Object[] objArr, int i, int i2, zzhb zzhbVar, boolean z, boolean z2, int[] iArr2, int i3, int i4, zzhg zzhgVar, zzgl zzglVar, zzij<?, ?> zzijVar, zzfg<?> zzfgVar, zzgu zzguVar) {
        this.zzc = iArr;
        this.zzd = objArr;
        this.zze = i;
        this.zzf = i2;
        this.zzi = zzhbVar instanceof zzfq;
        this.zzj = z;
        this.zzh = zzfgVar != null && zzfgVar.zza(zzhbVar);
        this.zzk = false;
        this.zzl = iArr2;
        this.zzm = i3;
        this.zzn = i4;
        this.zzo = zzhgVar;
        this.zzp = zzglVar;
        this.zzq = zzijVar;
        this.zzr = zzfgVar;
        this.zzg = zzhbVar;
        this.zzs = zzguVar;
    }

    /* JADX WARN: Removed duplicated region for block: B:157:0x0349  */
    /* JADX WARN: Removed duplicated region for block: B:173:0x03a2  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x03af A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static <T> com.google.android.gms.internal.mlkit_common.zzhf<T> zza(java.lang.Class<T> r33, com.google.android.gms.internal.mlkit_common.zzgz r34, com.google.android.gms.internal.mlkit_common.zzhg r35, com.google.android.gms.internal.mlkit_common.zzgl r36, com.google.android.gms.internal.mlkit_common.zzij<?, ?> r37, com.google.android.gms.internal.mlkit_common.zzfg<?> r38, com.google.android.gms.internal.mlkit_common.zzgu r39) {
        /*
            Method dump skipped, instructions count: 1073
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.mlkit_common.zzhf.zza(java.lang.Class, com.google.android.gms.internal.mlkit_common.zzgz, com.google.android.gms.internal.mlkit_common.zzhg, com.google.android.gms.internal.mlkit_common.zzgl, com.google.android.gms.internal.mlkit_common.zzij, com.google.android.gms.internal.mlkit_common.zzfg, com.google.android.gms.internal.mlkit_common.zzgu):com.google.android.gms.internal.mlkit_common.zzhf");
    }

    private static Field zza(Class<?> cls, String str) {
        try {
            return cls.getDeclaredField(str);
        } catch (NoSuchFieldException e) {
            Field[] declaredFields = cls.getDeclaredFields();
            for (Field field : declaredFields) {
                if (str.equals(field.getName())) {
                    return field;
                }
            }
            String name = cls.getName();
            String arrays = Arrays.toString(declaredFields);
            StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 40 + String.valueOf(name).length() + String.valueOf(arrays).length());
            sb.append("Field ");
            sb.append(str);
            sb.append(" for ");
            sb.append(name);
            sb.append(" not found. Known fields are ");
            sb.append(arrays);
            throw new RuntimeException(sb.toString());
        }
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final boolean zza(T t, T t2) {
        int length = this.zzc.length;
        int i = 0;
        while (true) {
            boolean z = true;
            if (i < length) {
                int zzc = zzc(i);
                long j = zzc & 1048575;
                switch ((zzc & 267386880) >>> 20) {
                    case 0:
                        if (!zzc(t, t2, i) || Double.doubleToLongBits(zzip.zze(t, j)) != Double.doubleToLongBits(zzip.zze(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 1:
                        if (!zzc(t, t2, i) || Float.floatToIntBits(zzip.zzd(t, j)) != Float.floatToIntBits(zzip.zzd(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 2:
                        if (!zzc(t, t2, i) || zzip.zzb(t, j) != zzip.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 3:
                        if (!zzc(t, t2, i) || zzip.zzb(t, j) != zzip.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 4:
                        if (!zzc(t, t2, i) || zzip.zza(t, j) != zzip.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 5:
                        if (!zzc(t, t2, i) || zzip.zzb(t, j) != zzip.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 6:
                        if (!zzc(t, t2, i) || zzip.zza(t, j) != zzip.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 7:
                        if (!zzc(t, t2, i) || zzip.zzc(t, j) != zzip.zzc(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 8:
                        if (!zzc(t, t2, i) || !zzht.zza(zzip.zzf(t, j), zzip.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 9:
                        if (!zzc(t, t2, i) || !zzht.zza(zzip.zzf(t, j), zzip.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 10:
                        if (!zzc(t, t2, i) || !zzht.zza(zzip.zzf(t, j), zzip.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 11:
                        if (!zzc(t, t2, i) || zzip.zza(t, j) != zzip.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 12:
                        if (!zzc(t, t2, i) || zzip.zza(t, j) != zzip.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 13:
                        if (!zzc(t, t2, i) || zzip.zza(t, j) != zzip.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 14:
                        if (!zzc(t, t2, i) || zzip.zzb(t, j) != zzip.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 15:
                        if (!zzc(t, t2, i) || zzip.zza(t, j) != zzip.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 16:
                        if (!zzc(t, t2, i) || zzip.zzb(t, j) != zzip.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 17:
                        if (!zzc(t, t2, i) || !zzht.zza(zzip.zzf(t, j), zzip.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 49:
                        z = zzht.zza(zzip.zzf(t, j), zzip.zzf(t2, j));
                        break;
                    case 50:
                        z = zzht.zza(zzip.zzf(t, j), zzip.zzf(t2, j));
                        break;
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    case 59:
                    case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    case 62:
                    case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    case 64:
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                    case 66:
                    case 67:
                    case 68:
                        long zzd = zzd(i) & 1048575;
                        if (zzip.zza(t, zzd) != zzip.zza(t2, zzd) || !zzht.zza(zzip.zzf(t, j), zzip.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                }
                if (!z) {
                    return false;
                }
                i += 3;
            } else if (!this.zzq.zza(t).equals(this.zzq.zza(t2))) {
                return false;
            } else {
                if (!this.zzh) {
                    return true;
                }
                return this.zzr.zza(t).equals(this.zzr.zza(t2));
            }
        }
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final int zza(T t) {
        int length = this.zzc.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2 += 3) {
            int zzc = zzc(i2);
            int i3 = this.zzc[i2];
            long j = 1048575 & zzc;
            int i4 = 37;
            switch ((zzc & 267386880) >>> 20) {
                case 0:
                    i = (i * 53) + zzfs.zza(Double.doubleToLongBits(zzip.zze(t, j)));
                    break;
                case 1:
                    i = (i * 53) + Float.floatToIntBits(zzip.zzd(t, j));
                    break;
                case 2:
                    i = (i * 53) + zzfs.zza(zzip.zzb(t, j));
                    break;
                case 3:
                    i = (i * 53) + zzfs.zza(zzip.zzb(t, j));
                    break;
                case 4:
                    i = (i * 53) + zzip.zza(t, j);
                    break;
                case 5:
                    i = (i * 53) + zzfs.zza(zzip.zzb(t, j));
                    break;
                case 6:
                    i = (i * 53) + zzip.zza(t, j);
                    break;
                case 7:
                    i = (i * 53) + zzfs.zza(zzip.zzc(t, j));
                    break;
                case 8:
                    i = (i * 53) + ((String) zzip.zzf(t, j)).hashCode();
                    break;
                case 9:
                    Object zzf = zzip.zzf(t, j);
                    if (zzf != null) {
                        i4 = zzf.hashCode();
                    }
                    i = (i * 53) + i4;
                    break;
                case 10:
                    i = (i * 53) + zzip.zzf(t, j).hashCode();
                    break;
                case 11:
                    i = (i * 53) + zzip.zza(t, j);
                    break;
                case 12:
                    i = (i * 53) + zzip.zza(t, j);
                    break;
                case 13:
                    i = (i * 53) + zzip.zza(t, j);
                    break;
                case 14:
                    i = (i * 53) + zzfs.zza(zzip.zzb(t, j));
                    break;
                case 15:
                    i = (i * 53) + zzip.zza(t, j);
                    break;
                case 16:
                    i = (i * 53) + zzfs.zza(zzip.zzb(t, j));
                    break;
                case 17:
                    Object zzf2 = zzip.zzf(t, j);
                    if (zzf2 != null) {
                        i4 = zzf2.hashCode();
                    }
                    i = (i * 53) + i4;
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                    i = (i * 53) + zzip.zzf(t, j).hashCode();
                    break;
                case 50:
                    i = (i * 53) + zzip.zzf(t, j).hashCode();
                    break;
                case 51:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzfs.zza(Double.doubleToLongBits(zzb(t, j)));
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + Float.floatToIntBits(zzc(t, j));
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzfs.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzfs.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzfs.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzfs.zza(zzf(t, j));
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + ((String) zzip.zzf(t, j)).hashCode();
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzip.zzf(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzip.zzf(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzfs.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzfs.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zza((zzhf<T>) t, i3, i2)) {
                        i = (i * 53) + zzip.zzf(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
            }
        }
        int hashCode = (i * 53) + this.zzq.zza(t).hashCode();
        if (this.zzh) {
            return (hashCode * 53) + this.zzr.zza(t).hashCode();
        }
        return hashCode;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final void zzb(T t, T t2) {
        if (t2 == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < this.zzc.length; i += 3) {
            int zzc = zzc(i);
            long j = 1048575 & zzc;
            int i2 = this.zzc[i];
            switch ((zzc & 267386880) >>> 20) {
                case 0:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza(t, j, zzip.zze(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zzd(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zzb(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 3:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zzb(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 4:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zza(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zzb(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 6:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zza(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 7:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza(t, j, zzip.zzc(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 8:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza(t, j, zzip.zzf(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 9:
                    zza(t, t2, i);
                    break;
                case 10:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza(t, j, zzip.zzf(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 11:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zza(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 12:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zza(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 13:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zza(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 14:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zzb(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 15:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zza(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 16:
                    if (zza((zzhf<T>) t2, i)) {
                        zzip.zza((Object) t, j, zzip.zzb(t2, j));
                        zzb((zzhf<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 17:
                    zza(t, t2, i);
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                    this.zzp.zza(t, t2, j);
                    break;
                case 50:
                    zzht.zza(this.zzs, t, t2, j);
                    break;
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                case 59:
                    if (zza((zzhf<T>) t2, i2, i)) {
                        zzip.zza(t, j, zzip.zzf(t2, j));
                        zzb((zzhf<T>) t, i2, i);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    zzb(t, t2, i);
                    break;
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                case 62:
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                case 64:
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                case 66:
                case 67:
                    if (zza((zzhf<T>) t2, i2, i)) {
                        zzip.zza(t, j, zzip.zzf(t2, j));
                        zzb((zzhf<T>) t, i2, i);
                        break;
                    } else {
                        break;
                    }
                case 68:
                    zzb(t, t2, i);
                    break;
            }
        }
        zzht.zza(this.zzq, t, t2);
        if (this.zzh) {
            zzht.zza(this.zzr, t, t2);
        }
    }

    private final void zza(T t, T t2, int i) {
        long zzc = zzc(i) & 1048575;
        if (!zza((zzhf<T>) t2, i)) {
            return;
        }
        Object zzf = zzip.zzf(t, zzc);
        Object zzf2 = zzip.zzf(t2, zzc);
        if (zzf != null && zzf2 != null) {
            zzip.zza(t, zzc, zzfs.zza(zzf, zzf2));
            zzb((zzhf<T>) t, i);
        } else if (zzf2 != null) {
            zzip.zza(t, zzc, zzf2);
            zzb((zzhf<T>) t, i);
        }
    }

    private final void zzb(T t, T t2, int i) {
        int zzc = zzc(i);
        int i2 = this.zzc[i];
        long j = zzc & 1048575;
        if (!zza((zzhf<T>) t2, i2, i)) {
            return;
        }
        Object zzf = zzip.zzf(t, j);
        Object zzf2 = zzip.zzf(t2, j);
        if (zzf != null && zzf2 != null) {
            zzip.zza(t, j, zzfs.zza(zzf, zzf2));
            zzb((zzhf<T>) t, i2, i);
        } else if (zzf2 != null) {
            zzip.zza(t, j, zzf2);
            zzb((zzhf<T>) t, i2, i);
        }
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final int zzd(T t) {
        int i;
        long j;
        int i2 = 267386880;
        int i3 = 1048575;
        int i4 = 1;
        if (this.zzj) {
            Unsafe unsafe = zzb;
            int i5 = 0;
            int i6 = 0;
            while (i5 < this.zzc.length) {
                int zzc = zzc(i5);
                int i7 = (zzc & i2) >>> 20;
                int i8 = this.zzc[i5];
                long j2 = zzc & 1048575;
                if (i7 >= zzfm.DOUBLE_LIST_PACKED.zza() && i7 <= zzfm.SINT64_LIST_PACKED.zza()) {
                    int i9 = this.zzc[i5 + 2];
                }
                switch (i7) {
                    case 0:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzb(i8, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                            break;
                        } else {
                            break;
                        }
                    case 1:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzb(i8, 0.0f);
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzd(i8, zzip.zzb(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 3:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zze(i8, zzip.zzb(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 4:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzf(i8, zzip.zza(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 5:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzg(i8, 0L);
                            break;
                        } else {
                            break;
                        }
                    case 6:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzi(i8, 0);
                            break;
                        } else {
                            break;
                        }
                    case 7:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzb(i8, true);
                            break;
                        } else {
                            break;
                        }
                    case 8:
                        if (zza((zzhf<T>) t, i5)) {
                            Object zzf = zzip.zzf(t, j2);
                            if (zzf instanceof zzep) {
                                i6 += zzfc.zzc(i8, (zzep) zzf);
                                break;
                            } else {
                                i6 += zzfc.zzb(i8, (String) zzf);
                                break;
                            }
                        } else {
                            break;
                        }
                    case 9:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzht.zza(i8, zzip.zzf(t, j2), zza(i5));
                            break;
                        } else {
                            break;
                        }
                    case 10:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzc(i8, (zzep) zzip.zzf(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 11:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzg(i8, zzip.zza(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 12:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzk(i8, zzip.zza(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 13:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzj(i8, 0);
                            break;
                        } else {
                            break;
                        }
                    case 14:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzh(i8, 0L);
                            break;
                        } else {
                            break;
                        }
                    case 15:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzh(i8, zzip.zza(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 16:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzf(i8, zzip.zzb(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 17:
                        if (zza((zzhf<T>) t, i5)) {
                            i6 += zzfc.zzc(i8, (zzhb) zzip.zzf(t, j2), zza(i5));
                            break;
                        } else {
                            break;
                        }
                    case 18:
                        i6 += zzht.zzi(i8, zza(t, j2), false);
                        break;
                    case 19:
                        i6 += zzht.zzh(i8, zza(t, j2), false);
                        break;
                    case 20:
                        i6 += zzht.zza(i8, (List<Long>) zza(t, j2), false);
                        break;
                    case 21:
                        i6 += zzht.zzb(i8, (List<Long>) zza(t, j2), false);
                        break;
                    case 22:
                        i6 += zzht.zze(i8, zza(t, j2), false);
                        break;
                    case 23:
                        i6 += zzht.zzi(i8, zza(t, j2), false);
                        break;
                    case 24:
                        i6 += zzht.zzh(i8, zza(t, j2), false);
                        break;
                    case 25:
                        i6 += zzht.zzj(i8, zza(t, j2), false);
                        break;
                    case 26:
                        i6 += zzht.zza(i8, zza(t, j2));
                        break;
                    case 27:
                        i6 += zzht.zza(i8, zza(t, j2), zza(i5));
                        break;
                    case 28:
                        i6 += zzht.zzb(i8, zza(t, j2));
                        break;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                        i6 += zzht.zzf(i8, zza(t, j2), false);
                        break;
                    case 30:
                        i6 += zzht.zzd(i8, zza(t, j2), false);
                        break;
                    case 31:
                        i6 += zzht.zzh(i8, zza(t, j2), false);
                        break;
                    case 32:
                        i6 += zzht.zzi(i8, zza(t, j2), false);
                        break;
                    case 33:
                        i6 += zzht.zzg(i8, zza(t, j2), false);
                        break;
                    case 34:
                        i6 += zzht.zzc(i8, zza(t, j2), false);
                        break;
                    case 35:
                        int zzi = zzht.zzi((List) unsafe.getObject(t, j2));
                        if (zzi > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzi) + zzi;
                            break;
                        } else {
                            break;
                        }
                    case 36:
                        int zzh = zzht.zzh((List) unsafe.getObject(t, j2));
                        if (zzh > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzh) + zzh;
                            break;
                        } else {
                            break;
                        }
                    case 37:
                        int zza2 = zzht.zza((List) unsafe.getObject(t, j2));
                        if (zza2 > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zza2) + zza2;
                            break;
                        } else {
                            break;
                        }
                    case 38:
                        int zzb2 = zzht.zzb((List) unsafe.getObject(t, j2));
                        if (zzb2 > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzb2) + zzb2;
                            break;
                        } else {
                            break;
                        }
                    case 39:
                        int zze = zzht.zze((List) unsafe.getObject(t, j2));
                        if (zze > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zze) + zze;
                            break;
                        } else {
                            break;
                        }
                    case 40:
                        int zzi2 = zzht.zzi((List) unsafe.getObject(t, j2));
                        if (zzi2 > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzi2) + zzi2;
                            break;
                        } else {
                            break;
                        }
                    case 41:
                        int zzh2 = zzht.zzh((List) unsafe.getObject(t, j2));
                        if (zzh2 > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzh2) + zzh2;
                            break;
                        } else {
                            break;
                        }
                    case 42:
                        int zzj = zzht.zzj((List) unsafe.getObject(t, j2));
                        if (zzj > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzj) + zzj;
                            break;
                        } else {
                            break;
                        }
                    case 43:
                        int zzf2 = zzht.zzf((List) unsafe.getObject(t, j2));
                        if (zzf2 > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzf2) + zzf2;
                            break;
                        } else {
                            break;
                        }
                    case 44:
                        int zzd = zzht.zzd((List) unsafe.getObject(t, j2));
                        if (zzd > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzd) + zzd;
                            break;
                        } else {
                            break;
                        }
                    case 45:
                        int zzh3 = zzht.zzh((List) unsafe.getObject(t, j2));
                        if (zzh3 > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzh3) + zzh3;
                            break;
                        } else {
                            break;
                        }
                    case 46:
                        int zzi3 = zzht.zzi((List) unsafe.getObject(t, j2));
                        if (zzi3 > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzi3) + zzi3;
                            break;
                        } else {
                            break;
                        }
                    case 47:
                        int zzg = zzht.zzg((List) unsafe.getObject(t, j2));
                        if (zzg > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzg) + zzg;
                            break;
                        } else {
                            break;
                        }
                    case 48:
                        int zzc2 = zzht.zzc((List) unsafe.getObject(t, j2));
                        if (zzc2 > 0) {
                            i6 += zzfc.zze(i8) + zzfc.zzg(zzc2) + zzc2;
                            break;
                        } else {
                            break;
                        }
                    case 49:
                        i6 += zzht.zzb(i8, (List<zzhb>) zza(t, j2), zza(i5));
                        break;
                    case 50:
                        i6 += this.zzs.zza(i8, zzip.zzf(t, j2), zzb(i5));
                        break;
                    case 51:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzb(i8, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                            break;
                        } else {
                            break;
                        }
                    case 52:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzb(i8, 0.0f);
                            break;
                        } else {
                            break;
                        }
                    case 53:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzd(i8, zze(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 54:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zze(i8, zze(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 55:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzf(i8, zzd(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzg(i8, 0L);
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzi(i8, 0);
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_TEXT_COPIED /* 58 */:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzb(i8, true);
                            break;
                        } else {
                            break;
                        }
                    case 59:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            Object zzf3 = zzip.zzf(t, j2);
                            if (zzf3 instanceof zzep) {
                                i6 += zzfc.zzc(i8, (zzep) zzf3);
                                break;
                            } else {
                                i6 += zzfc.zzb(i8, (String) zzf3);
                                break;
                            }
                        } else {
                            break;
                        }
                    case UndoView.ACTION_PHONE_COPIED /* 60 */:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzht.zza(i8, zzip.zzf(t, j2), zza(i5));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzc(i8, (zzep) zzip.zzf(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 62:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzg(i8, zzd(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzk(i8, zzd(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 64:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzj(i8, 0);
                            break;
                        } else {
                            break;
                        }
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzh(i8, 0L);
                            break;
                        } else {
                            break;
                        }
                    case 66:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzh(i8, zzd(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 67:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzf(i8, zze(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 68:
                        if (zza((zzhf<T>) t, i8, i5)) {
                            i6 += zzfc.zzc(i8, (zzhb) zzip.zzf(t, j2), zza(i5));
                            break;
                        } else {
                            break;
                        }
                }
                i5 += 3;
                i2 = 267386880;
            }
            return i6 + zza((zzij) this.zzq, (Object) t);
        }
        Unsafe unsafe2 = zzb;
        int i10 = 0;
        int i11 = 0;
        int i12 = 1048575;
        int i13 = 0;
        while (i10 < this.zzc.length) {
            int zzc3 = zzc(i10);
            int[] iArr = this.zzc;
            int i14 = iArr[i10];
            int i15 = (zzc3 & 267386880) >>> 20;
            if (i15 > 17) {
                i = 0;
            } else {
                int i16 = iArr[i10 + 2];
                int i17 = i16 & i3;
                i = i4 << (i16 >>> 20);
                if (i17 != i12) {
                    i13 = unsafe2.getInt(t, i17);
                    i12 = i17;
                }
            }
            long j3 = zzc3 & i3;
            switch (i15) {
                case 0:
                    j = 0;
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzb(i14, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                        break;
                    } else {
                        break;
                    }
                case 1:
                    j = 0;
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzb(i14, 0.0f);
                        break;
                    } else {
                        break;
                    }
                case 2:
                    j = 0;
                    if ((i & i13) != 0) {
                        i11 += zzfc.zzd(i14, unsafe2.getLong(t, j3));
                        break;
                    } else {
                        break;
                    }
                case 3:
                    j = 0;
                    if ((i & i13) != 0) {
                        i11 += zzfc.zze(i14, unsafe2.getLong(t, j3));
                        break;
                    } else {
                        break;
                    }
                case 4:
                    j = 0;
                    if ((i & i13) != 0) {
                        i11 += zzfc.zzf(i14, unsafe2.getInt(t, j3));
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if ((i13 & i) == 0) {
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        i11 += zzfc.zzg(i14, 0L);
                        break;
                    }
                case 6:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzi(i14, 0);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 7:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzb(i14, true);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 8:
                    if ((i13 & i) != 0) {
                        Object object = unsafe2.getObject(t, j3);
                        if (object instanceof zzep) {
                            i11 += zzfc.zzc(i14, (zzep) object);
                            j = 0;
                            break;
                        } else {
                            i11 += zzfc.zzb(i14, (String) object);
                            j = 0;
                            break;
                        }
                    } else {
                        j = 0;
                        break;
                    }
                case 9:
                    if ((i13 & i) != 0) {
                        i11 += zzht.zza(i14, unsafe2.getObject(t, j3), zza(i10));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 10:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzc(i14, (zzep) unsafe2.getObject(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 11:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzg(i14, unsafe2.getInt(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 12:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzk(i14, unsafe2.getInt(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 13:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzj(i14, 0);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 14:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzh(i14, 0L);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 15:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzh(i14, unsafe2.getInt(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 16:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzf(i14, unsafe2.getLong(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 17:
                    if ((i13 & i) != 0) {
                        i11 += zzfc.zzc(i14, (zzhb) unsafe2.getObject(t, j3), zza(i10));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 18:
                    i11 += zzht.zzi(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 19:
                    i11 += zzht.zzh(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 20:
                    i11 += zzht.zza(i14, (List<Long>) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 21:
                    i11 += zzht.zzb(i14, (List<Long>) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 22:
                    i11 += zzht.zze(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 23:
                    i11 += zzht.zzi(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 24:
                    i11 += zzht.zzh(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 25:
                    i11 += zzht.zzj(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 26:
                    i11 += zzht.zza(i14, (List) unsafe2.getObject(t, j3));
                    j = 0;
                    break;
                case 27:
                    i11 += zzht.zza(i14, (List<?>) unsafe2.getObject(t, j3), zza(i10));
                    j = 0;
                    break;
                case 28:
                    i11 += zzht.zzb(i14, (List) unsafe2.getObject(t, j3));
                    j = 0;
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    i11 += zzht.zzf(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 30:
                    i11 += zzht.zzd(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 31:
                    i11 += zzht.zzh(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 32:
                    i11 += zzht.zzi(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 33:
                    i11 += zzht.zzg(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 34:
                    i11 += zzht.zzc(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 35:
                    int zzi4 = zzht.zzi((List) unsafe2.getObject(t, j3));
                    if (zzi4 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzi4) + zzi4;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 36:
                    int zzh4 = zzht.zzh((List) unsafe2.getObject(t, j3));
                    if (zzh4 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzh4) + zzh4;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 37:
                    int zza3 = zzht.zza((List) unsafe2.getObject(t, j3));
                    if (zza3 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zza3) + zza3;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 38:
                    int zzb3 = zzht.zzb((List) unsafe2.getObject(t, j3));
                    if (zzb3 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzb3) + zzb3;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 39:
                    int zze2 = zzht.zze((List) unsafe2.getObject(t, j3));
                    if (zze2 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zze2) + zze2;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 40:
                    int zzi5 = zzht.zzi((List) unsafe2.getObject(t, j3));
                    if (zzi5 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzi5) + zzi5;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 41:
                    int zzh5 = zzht.zzh((List) unsafe2.getObject(t, j3));
                    if (zzh5 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzh5) + zzh5;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 42:
                    int zzj2 = zzht.zzj((List) unsafe2.getObject(t, j3));
                    if (zzj2 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzj2) + zzj2;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 43:
                    int zzf4 = zzht.zzf((List) unsafe2.getObject(t, j3));
                    if (zzf4 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzf4) + zzf4;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 44:
                    int zzd2 = zzht.zzd((List) unsafe2.getObject(t, j3));
                    if (zzd2 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzd2) + zzd2;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 45:
                    int zzh6 = zzht.zzh((List) unsafe2.getObject(t, j3));
                    if (zzh6 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzh6) + zzh6;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 46:
                    int zzi6 = zzht.zzi((List) unsafe2.getObject(t, j3));
                    if (zzi6 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzi6) + zzi6;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 47:
                    int zzg2 = zzht.zzg((List) unsafe2.getObject(t, j3));
                    if (zzg2 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzg2) + zzg2;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 48:
                    int zzc4 = zzht.zzc((List) unsafe2.getObject(t, j3));
                    if (zzc4 > 0) {
                        i11 += zzfc.zze(i14) + zzfc.zzg(zzc4) + zzc4;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 49:
                    i11 += zzht.zzb(i14, (List) unsafe2.getObject(t, j3), zza(i10));
                    j = 0;
                    break;
                case 50:
                    i11 += this.zzs.zza(i14, unsafe2.getObject(t, j3), zzb(i10));
                    j = 0;
                    break;
                case 51:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzb(i14, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 52:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzb(i14, 0.0f);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 53:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzd(i14, zze(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 54:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zze(i14, zze(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 55:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzf(i14, zzd(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzg(i14, 0L);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzi(i14, 0);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzb(i14, true);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 59:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        Object object2 = unsafe2.getObject(t, j3);
                        if (object2 instanceof zzep) {
                            i11 += zzfc.zzc(i14, (zzep) object2);
                            j = 0;
                            break;
                        } else {
                            i11 += zzfc.zzb(i14, (String) object2);
                            j = 0;
                            break;
                        }
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzht.zza(i14, unsafe2.getObject(t, j3), zza(i10));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzc(i14, (zzep) unsafe2.getObject(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 62:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzg(i14, zzd(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzk(i14, zzd(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 64:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzj(i14, 0);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzh(i14, 0L);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 66:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzh(i14, zzd(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 67:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzf(i14, zze(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 68:
                    if (zza((zzhf<T>) t, i14, i10)) {
                        i11 += zzfc.zzc(i14, (zzhb) unsafe2.getObject(t, j3), zza(i10));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                default:
                    j = 0;
                    break;
            }
            i10 += 3;
            i3 = 1048575;
            i4 = 1;
        }
        int i18 = 0;
        int zza4 = i11 + zza((zzij) this.zzq, (Object) t);
        if (this.zzh) {
            zzfl<?> zza5 = this.zzr.zza(t);
            for (int i19 = 0; i19 < zza5.zza.zzc(); i19++) {
                Map.Entry<?, Object> zzb4 = zza5.zza.zzb(i19);
                i18 += zzfl.zza((zzfn) zzb4.getKey(), zzb4.getValue());
            }
            for (Map.Entry<?, Object> entry : zza5.zza.zzd()) {
                i18 += zzfl.zza((zzfn) entry.getKey(), entry.getValue());
            }
            return zza4 + i18;
        }
        return zza4;
    }

    private static <UT, UB> int zza(zzij<UT, UB> zzijVar, T t) {
        return zzijVar.zzd(zzijVar.zza(t));
    }

    private static List<?> zza(Object obj, long j) {
        return (List) zzip.zzf(obj, j);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003f  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x059f  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x05e2  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x0b42  */
    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void zza(T r14, com.google.android.gms.internal.mlkit_common.zzjd r15) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 3196
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.mlkit_common.zzhf.zza(java.lang.Object, com.google.android.gms.internal.mlkit_common.zzjd):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0035  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x0559  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void zzb(T r18, com.google.android.gms.internal.mlkit_common.zzjd r19) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1538
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.mlkit_common.zzhf.zzb(java.lang.Object, com.google.android.gms.internal.mlkit_common.zzjd):void");
    }

    private final <K, V> void zza(zzjd zzjdVar, int i, Object obj, int i2) throws IOException {
        if (obj != null) {
            zzjdVar.zza(i, this.zzs.zzc(zzb(i2)), this.zzs.zza(obj));
        }
    }

    private static <UT, UB> void zza(zzij<UT, UB> zzijVar, T t, zzjd zzjdVar) throws IOException {
        zzijVar.zza((zzij<UT, UB>) zzijVar.zza(t), zzjdVar);
    }

    private final zzhr zza(int i) {
        int i2 = (i / 3) << 1;
        zzhr zzhrVar = (zzhr) this.zzd[i2];
        if (zzhrVar != null) {
            return zzhrVar;
        }
        zzhr<T> zza2 = zzhm.zza().zza((Class) ((Class) this.zzd[i2 + 1]));
        this.zzd[i2] = zza2;
        return zza2;
    }

    private final Object zzb(int i) {
        return this.zzd[(i / 3) << 1];
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final void zzb(T t) {
        int i;
        int i2 = this.zzm;
        while (true) {
            i = this.zzn;
            if (i2 >= i) {
                break;
            }
            long zzc = zzc(this.zzl[i2]) & 1048575;
            Object zzf = zzip.zzf(t, zzc);
            if (zzf != null) {
                zzip.zza(t, zzc, this.zzs.zzb(zzf));
            }
            i2++;
        }
        int length = this.zzl.length;
        while (i < length) {
            this.zzp.zza(t, this.zzl[i]);
            i++;
        }
        this.zzq.zzb(t);
        if (this.zzh) {
            this.zzr.zzc(t);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v16, types: [com.google.android.gms.internal.mlkit_common.zzhr] */
    /* JADX WARN: Type inference failed for: r1v22 */
    /* JADX WARN: Type inference failed for: r1v5, types: [com.google.android.gms.internal.mlkit_common.zzhr] */
    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final boolean zzc(T t) {
        int i;
        int i2;
        int i3 = 1048575;
        int i4 = 0;
        int i5 = 0;
        while (true) {
            boolean z = true;
            if (i5 >= this.zzm) {
                return !this.zzh || this.zzr.zza(t).zzf();
            }
            int i6 = this.zzl[i5];
            int i7 = this.zzc[i6];
            int zzc = zzc(i6);
            int i8 = this.zzc[i6 + 2];
            int i9 = i8 & 1048575;
            int i10 = 1 << (i8 >>> 20);
            if (i9 == i3) {
                i2 = i3;
                i = i4;
            } else if (i9 == 1048575) {
                i = i4;
                i2 = i9;
            } else {
                i = zzb.getInt(t, i9);
                i2 = i9;
            }
            if (((268435456 & zzc) != 0) && !zza(t, i6, i2, i, i10)) {
                return false;
            }
            switch ((267386880 & zzc) >>> 20) {
                case 9:
                case 17:
                    if (zza(t, i6, i2, i, i10) && !zza(t, zzc, zza(i6))) {
                        return false;
                    }
                    break;
                case 27:
                case 49:
                    List list = (List) zzip.zzf(t, zzc & 1048575);
                    if (!list.isEmpty()) {
                        ?? zza2 = zza(i6);
                        int i11 = 0;
                        while (true) {
                            if (i11 < list.size()) {
                                if (!zza2.zzc(list.get(i11))) {
                                    z = false;
                                } else {
                                    i11++;
                                }
                            }
                        }
                    }
                    if (z) {
                        break;
                    } else {
                        return false;
                    }
                case 50:
                    Map<?, ?> zza3 = this.zzs.zza(zzip.zzf(t, zzc & 1048575));
                    if (!zza3.isEmpty()) {
                        if (this.zzs.zzc(zzb(i6)).zzb.zza() == zzja.MESSAGE) {
                            zzhr<T> zzhrVar = 0;
                            Iterator<?> it = zza3.values().iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    Object next = it.next();
                                    if (zzhrVar == null) {
                                        zzhrVar = zzhm.zza().zza((Class) next.getClass());
                                    }
                                    boolean zzc2 = zzhrVar.zzc(next);
                                    zzhrVar = zzhrVar;
                                    if (!zzc2) {
                                        z = false;
                                    }
                                }
                            }
                        }
                    }
                    if (z) {
                        break;
                    } else {
                        return false;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                case 68:
                    if (zza((zzhf<T>) t, i7, i6) && !zza(t, zzc, zza(i6))) {
                        return false;
                    }
                    break;
            }
            i5++;
            i3 = i2;
            i4 = i;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static boolean zza(Object obj, int i, zzhr zzhrVar) {
        return zzhrVar.zzc(zzip.zzf(obj, i & 1048575));
    }

    private static void zza(int i, Object obj, zzjd zzjdVar) throws IOException {
        if (obj instanceof String) {
            zzjdVar.zza(i, (String) obj);
        } else {
            zzjdVar.zza(i, (zzep) obj);
        }
    }

    private final int zzc(int i) {
        return this.zzc[i + 1];
    }

    private final int zzd(int i) {
        return this.zzc[i + 2];
    }

    private static <T> double zzb(T t, long j) {
        return ((Double) zzip.zzf(t, j)).doubleValue();
    }

    private static <T> float zzc(T t, long j) {
        return ((Float) zzip.zzf(t, j)).floatValue();
    }

    private static <T> int zzd(T t, long j) {
        return ((Integer) zzip.zzf(t, j)).intValue();
    }

    private static <T> long zze(T t, long j) {
        return ((Long) zzip.zzf(t, j)).longValue();
    }

    private static <T> boolean zzf(T t, long j) {
        return ((Boolean) zzip.zzf(t, j)).booleanValue();
    }

    private final boolean zzc(T t, T t2, int i) {
        return zza((zzhf<T>) t, i) == zza((zzhf<T>) t2, i);
    }

    private final boolean zza(T t, int i, int i2, int i3, int i4) {
        if (i2 == 1048575) {
            return zza((zzhf<T>) t, i);
        }
        return (i3 & i4) != 0;
    }

    private final boolean zza(T t, int i) {
        int zzd = zzd(i);
        long j = zzd & 1048575;
        if (j != 1048575) {
            return (zzip.zza(t, j) & (1 << (zzd >>> 20))) != 0;
        }
        int zzc = zzc(i);
        long j2 = zzc & 1048575;
        switch ((zzc & 267386880) >>> 20) {
            case 0:
                return zzip.zze(t, j2) != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            case 1:
                return zzip.zzd(t, j2) != 0.0f;
            case 2:
                return zzip.zzb(t, j2) != 0;
            case 3:
                return zzip.zzb(t, j2) != 0;
            case 4:
                return zzip.zza(t, j2) != 0;
            case 5:
                return zzip.zzb(t, j2) != 0;
            case 6:
                return zzip.zza(t, j2) != 0;
            case 7:
                return zzip.zzc(t, j2);
            case 8:
                Object zzf = zzip.zzf(t, j2);
                if (zzf instanceof String) {
                    return !((String) zzf).isEmpty();
                } else if (!(zzf instanceof zzep)) {
                    throw new IllegalArgumentException();
                } else {
                    return !zzep.zza.equals(zzf);
                }
            case 9:
                return zzip.zzf(t, j2) != null;
            case 10:
                return !zzep.zza.equals(zzip.zzf(t, j2));
            case 11:
                return zzip.zza(t, j2) != 0;
            case 12:
                return zzip.zza(t, j2) != 0;
            case 13:
                return zzip.zza(t, j2) != 0;
            case 14:
                return zzip.zzb(t, j2) != 0;
            case 15:
                return zzip.zza(t, j2) != 0;
            case 16:
                return zzip.zzb(t, j2) != 0;
            case 17:
                return zzip.zzf(t, j2) != null;
            default:
                throw new IllegalArgumentException();
        }
    }

    private final void zzb(T t, int i) {
        int zzd = zzd(i);
        long j = 1048575 & zzd;
        if (j == 1048575) {
            return;
        }
        zzip.zza((Object) t, j, (1 << (zzd >>> 20)) | zzip.zza(t, j));
    }

    private final boolean zza(T t, int i, int i2) {
        return zzip.zza(t, (long) (zzd(i2) & 1048575)) == i;
    }

    private final void zzb(T t, int i, int i2) {
        zzip.zza((Object) t, zzd(i2) & 1048575, i);
    }
}
