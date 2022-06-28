package com.google.android.gms.internal.vision;

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
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzko<T> implements zzlc<T> {
    private static final int[] zza = new int[0];
    private static final Unsafe zzb = zzma.zzc();
    private final int[] zzc;
    private final Object[] zzd;
    private final int zze;
    private final int zzf;
    private final zzkk zzg;
    private final boolean zzh;
    private final boolean zzi;
    private final boolean zzj;
    private final boolean zzk;
    private final int[] zzl;
    private final int zzm;
    private final int zzn;
    private final zzks zzo;
    private final zzju zzp;
    private final zzlu<?, ?> zzq;
    private final zziq<?> zzr;
    private final zzkh zzs;

    private zzko(int[] iArr, Object[] objArr, int i, int i2, zzkk zzkkVar, boolean z, boolean z2, int[] iArr2, int i3, int i4, zzks zzksVar, zzju zzjuVar, zzlu<?, ?> zzluVar, zziq<?> zziqVar, zzkh zzkhVar) {
        this.zzc = iArr;
        this.zzd = objArr;
        this.zze = i;
        this.zzf = i2;
        this.zzi = zzkkVar instanceof zzjb;
        this.zzj = z;
        this.zzh = zziqVar != null && zziqVar.zza(zzkkVar);
        this.zzk = false;
        this.zzl = iArr2;
        this.zzm = i3;
        this.zzn = i4;
        this.zzo = zzksVar;
        this.zzp = zzjuVar;
        this.zzq = zzluVar;
        this.zzr = zziqVar;
        this.zzg = zzkkVar;
        this.zzs = zzkhVar;
    }

    /* JADX WARN: Removed duplicated region for block: B:157:0x0349  */
    /* JADX WARN: Removed duplicated region for block: B:173:0x03a2  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x03af A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static <T> com.google.android.gms.internal.vision.zzko<T> zza(java.lang.Class<T> r33, com.google.android.gms.internal.vision.zzki r34, com.google.android.gms.internal.vision.zzks r35, com.google.android.gms.internal.vision.zzju r36, com.google.android.gms.internal.vision.zzlu<?, ?> r37, com.google.android.gms.internal.vision.zziq<?> r38, com.google.android.gms.internal.vision.zzkh r39) {
        /*
            Method dump skipped, instructions count: 1073
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zza(java.lang.Class, com.google.android.gms.internal.vision.zzki, com.google.android.gms.internal.vision.zzks, com.google.android.gms.internal.vision.zzju, com.google.android.gms.internal.vision.zzlu, com.google.android.gms.internal.vision.zziq, com.google.android.gms.internal.vision.zzkh):com.google.android.gms.internal.vision.zzko");
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

    @Override // com.google.android.gms.internal.vision.zzlc
    public final T zza() {
        return (T) this.zzo.zza(this.zzg);
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final boolean zza(T t, T t2) {
        int length = this.zzc.length;
        int i = 0;
        while (true) {
            boolean z = true;
            if (i < length) {
                int zzd = zzd(i);
                long j = zzd & 1048575;
                switch ((zzd & 267386880) >>> 20) {
                    case 0:
                        if (!zzc(t, t2, i) || Double.doubleToLongBits(zzma.zze(t, j)) != Double.doubleToLongBits(zzma.zze(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 1:
                        if (!zzc(t, t2, i) || Float.floatToIntBits(zzma.zzd(t, j)) != Float.floatToIntBits(zzma.zzd(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 2:
                        if (!zzc(t, t2, i) || zzma.zzb(t, j) != zzma.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 3:
                        if (!zzc(t, t2, i) || zzma.zzb(t, j) != zzma.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 4:
                        if (!zzc(t, t2, i) || zzma.zza(t, j) != zzma.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 5:
                        if (!zzc(t, t2, i) || zzma.zzb(t, j) != zzma.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 6:
                        if (!zzc(t, t2, i) || zzma.zza(t, j) != zzma.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 7:
                        if (!zzc(t, t2, i) || zzma.zzc(t, j) != zzma.zzc(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 8:
                        if (!zzc(t, t2, i) || !zzle.zza(zzma.zzf(t, j), zzma.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 9:
                        if (!zzc(t, t2, i) || !zzle.zza(zzma.zzf(t, j), zzma.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 10:
                        if (!zzc(t, t2, i) || !zzle.zza(zzma.zzf(t, j), zzma.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                    case 11:
                        if (!zzc(t, t2, i) || zzma.zza(t, j) != zzma.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 12:
                        if (!zzc(t, t2, i) || zzma.zza(t, j) != zzma.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 13:
                        if (!zzc(t, t2, i) || zzma.zza(t, j) != zzma.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 14:
                        if (!zzc(t, t2, i) || zzma.zzb(t, j) != zzma.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 15:
                        if (!zzc(t, t2, i) || zzma.zza(t, j) != zzma.zza(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 16:
                        if (!zzc(t, t2, i) || zzma.zzb(t, j) != zzma.zzb(t2, j)) {
                            z = false;
                            break;
                        }
                        break;
                    case 17:
                        if (!zzc(t, t2, i) || !zzle.zza(zzma.zzf(t, j), zzma.zzf(t2, j))) {
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
                        z = zzle.zza(zzma.zzf(t, j), zzma.zzf(t2, j));
                        break;
                    case 50:
                        z = zzle.zza(zzma.zzf(t, j), zzma.zzf(t2, j));
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
                        long zze = zze(i) & 1048575;
                        if (zzma.zza(t, zze) != zzma.zza(t2, zze) || !zzle.zza(zzma.zzf(t, j), zzma.zzf(t2, j))) {
                            z = false;
                            break;
                        }
                        break;
                }
                if (!z) {
                    return false;
                }
                i += 3;
            } else if (!this.zzq.zzb(t).equals(this.zzq.zzb(t2))) {
                return false;
            } else {
                if (!this.zzh) {
                    return true;
                }
                return this.zzr.zza(t).equals(this.zzr.zza(t2));
            }
        }
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final int zza(T t) {
        int length = this.zzc.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2 += 3) {
            int zzd = zzd(i2);
            int i3 = this.zzc[i2];
            long j = 1048575 & zzd;
            int i4 = 37;
            switch ((zzd & 267386880) >>> 20) {
                case 0:
                    i = (i * 53) + zzjf.zza(Double.doubleToLongBits(zzma.zze(t, j)));
                    break;
                case 1:
                    i = (i * 53) + Float.floatToIntBits(zzma.zzd(t, j));
                    break;
                case 2:
                    i = (i * 53) + zzjf.zza(zzma.zzb(t, j));
                    break;
                case 3:
                    i = (i * 53) + zzjf.zza(zzma.zzb(t, j));
                    break;
                case 4:
                    i = (i * 53) + zzma.zza(t, j);
                    break;
                case 5:
                    i = (i * 53) + zzjf.zza(zzma.zzb(t, j));
                    break;
                case 6:
                    i = (i * 53) + zzma.zza(t, j);
                    break;
                case 7:
                    i = (i * 53) + zzjf.zza(zzma.zzc(t, j));
                    break;
                case 8:
                    i = (i * 53) + ((String) zzma.zzf(t, j)).hashCode();
                    break;
                case 9:
                    Object zzf = zzma.zzf(t, j);
                    if (zzf != null) {
                        i4 = zzf.hashCode();
                    }
                    i = (i * 53) + i4;
                    break;
                case 10:
                    i = (i * 53) + zzma.zzf(t, j).hashCode();
                    break;
                case 11:
                    i = (i * 53) + zzma.zza(t, j);
                    break;
                case 12:
                    i = (i * 53) + zzma.zza(t, j);
                    break;
                case 13:
                    i = (i * 53) + zzma.zza(t, j);
                    break;
                case 14:
                    i = (i * 53) + zzjf.zza(zzma.zzb(t, j));
                    break;
                case 15:
                    i = (i * 53) + zzma.zza(t, j);
                    break;
                case 16:
                    i = (i * 53) + zzjf.zza(zzma.zzb(t, j));
                    break;
                case 17:
                    Object zzf2 = zzma.zzf(t, j);
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
                    i = (i * 53) + zzma.zzf(t, j).hashCode();
                    break;
                case 50:
                    i = (i * 53) + zzma.zzf(t, j).hashCode();
                    break;
                case 51:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzjf.zza(Double.doubleToLongBits(zzb(t, j)));
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + Float.floatToIntBits(zzc(t, j));
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzjf.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzjf.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzjf.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzjf.zza(zzf(t, j));
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + ((String) zzma.zzf(t, j)).hashCode();
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzma.zzf(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzma.zzf(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzjf.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzd(t, j);
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzjf.zza(zze(t, j));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zza((zzko<T>) t, i3, i2)) {
                        i = (i * 53) + zzma.zzf(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
            }
        }
        int hashCode = (i * 53) + this.zzq.zzb(t).hashCode();
        if (this.zzh) {
            return (hashCode * 53) + this.zzr.zza(t).hashCode();
        }
        return hashCode;
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zzb(T t, T t2) {
        if (t2 == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < this.zzc.length; i += 3) {
            int zzd = zzd(i);
            long j = 1048575 & zzd;
            int i2 = this.zzc[i];
            switch ((zzd & 267386880) >>> 20) {
                case 0:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza(t, j, zzma.zze(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zzd(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zzb(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 3:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zzb(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 4:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zza(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zzb(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 6:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zza(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 7:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza(t, j, zzma.zzc(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 8:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza(t, j, zzma.zzf(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 9:
                    zza(t, t2, i);
                    break;
                case 10:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza(t, j, zzma.zzf(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 11:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zza(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 12:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zza(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 13:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zza(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 14:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zzb(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 15:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zza(t2, j));
                        zzb((zzko<T>) t, i);
                        break;
                    } else {
                        break;
                    }
                case 16:
                    if (zza((zzko<T>) t2, i)) {
                        zzma.zza((Object) t, j, zzma.zzb(t2, j));
                        zzb((zzko<T>) t, i);
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
                    zzle.zza(this.zzs, t, t2, j);
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
                    if (zza((zzko<T>) t2, i2, i)) {
                        zzma.zza(t, j, zzma.zzf(t2, j));
                        zzb((zzko<T>) t, i2, i);
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
                    if (zza((zzko<T>) t2, i2, i)) {
                        zzma.zza(t, j, zzma.zzf(t2, j));
                        zzb((zzko<T>) t, i2, i);
                        break;
                    } else {
                        break;
                    }
                case 68:
                    zzb(t, t2, i);
                    break;
            }
        }
        zzle.zza(this.zzq, t, t2);
        if (this.zzh) {
            zzle.zza(this.zzr, t, t2);
        }
    }

    private final void zza(T t, T t2, int i) {
        long zzd = zzd(i) & 1048575;
        if (!zza((zzko<T>) t2, i)) {
            return;
        }
        Object zzf = zzma.zzf(t, zzd);
        Object zzf2 = zzma.zzf(t2, zzd);
        if (zzf != null && zzf2 != null) {
            zzma.zza(t, zzd, zzjf.zza(zzf, zzf2));
            zzb((zzko<T>) t, i);
        } else if (zzf2 != null) {
            zzma.zza(t, zzd, zzf2);
            zzb((zzko<T>) t, i);
        }
    }

    private final void zzb(T t, T t2, int i) {
        int zzd = zzd(i);
        int i2 = this.zzc[i];
        long j = zzd & 1048575;
        if (!zza((zzko<T>) t2, i2, i)) {
            return;
        }
        Object obj = null;
        if (zza((zzko<T>) t, i2, i)) {
            obj = zzma.zzf(t, j);
        }
        Object zzf = zzma.zzf(t2, j);
        if (obj != null && zzf != null) {
            zzma.zza(t, j, zzjf.zza(obj, zzf));
            zzb((zzko<T>) t, i2, i);
        } else if (zzf != null) {
            zzma.zza(t, j, zzf);
            zzb((zzko<T>) t, i2, i);
        }
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final int zzb(T t) {
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
                int zzd = zzd(i5);
                int i7 = (zzd & i2) >>> 20;
                int i8 = this.zzc[i5];
                long j2 = zzd & 1048575;
                if (i7 >= zziv.DOUBLE_LIST_PACKED.zza() && i7 <= zziv.SINT64_LIST_PACKED.zza()) {
                    int i9 = this.zzc[i5 + 2];
                }
                switch (i7) {
                    case 0:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzb(i8, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                            break;
                        } else {
                            break;
                        }
                    case 1:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzb(i8, 0.0f);
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzd(i8, zzma.zzb(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 3:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zze(i8, zzma.zzb(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 4:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzf(i8, zzma.zza(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 5:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzg(i8, 0L);
                            break;
                        } else {
                            break;
                        }
                    case 6:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzi(i8, 0);
                            break;
                        } else {
                            break;
                        }
                    case 7:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzb(i8, true);
                            break;
                        } else {
                            break;
                        }
                    case 8:
                        if (zza((zzko<T>) t, i5)) {
                            Object zzf = zzma.zzf(t, j2);
                            if (zzf instanceof zzht) {
                                i6 += zzii.zzc(i8, (zzht) zzf);
                                break;
                            } else {
                                i6 += zzii.zzb(i8, (String) zzf);
                                break;
                            }
                        } else {
                            break;
                        }
                    case 9:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzle.zza(i8, zzma.zzf(t, j2), zza(i5));
                            break;
                        } else {
                            break;
                        }
                    case 10:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzc(i8, (zzht) zzma.zzf(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 11:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzg(i8, zzma.zza(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 12:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzk(i8, zzma.zza(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 13:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzj(i8, 0);
                            break;
                        } else {
                            break;
                        }
                    case 14:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzh(i8, 0L);
                            break;
                        } else {
                            break;
                        }
                    case 15:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzh(i8, zzma.zza(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 16:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzf(i8, zzma.zzb(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 17:
                        if (zza((zzko<T>) t, i5)) {
                            i6 += zzii.zzc(i8, (zzkk) zzma.zzf(t, j2), zza(i5));
                            break;
                        } else {
                            break;
                        }
                    case 18:
                        i6 += zzle.zzi(i8, zza(t, j2), false);
                        break;
                    case 19:
                        i6 += zzle.zzh(i8, zza(t, j2), false);
                        break;
                    case 20:
                        i6 += zzle.zza(i8, (List<Long>) zza(t, j2), false);
                        break;
                    case 21:
                        i6 += zzle.zzb(i8, (List<Long>) zza(t, j2), false);
                        break;
                    case 22:
                        i6 += zzle.zze(i8, zza(t, j2), false);
                        break;
                    case 23:
                        i6 += zzle.zzi(i8, zza(t, j2), false);
                        break;
                    case 24:
                        i6 += zzle.zzh(i8, zza(t, j2), false);
                        break;
                    case 25:
                        i6 += zzle.zzj(i8, zza(t, j2), false);
                        break;
                    case 26:
                        i6 += zzle.zza(i8, zza(t, j2));
                        break;
                    case 27:
                        i6 += zzle.zza(i8, zza(t, j2), zza(i5));
                        break;
                    case 28:
                        i6 += zzle.zzb(i8, zza(t, j2));
                        break;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                        i6 += zzle.zzf(i8, zza(t, j2), false);
                        break;
                    case 30:
                        i6 += zzle.zzd(i8, zza(t, j2), false);
                        break;
                    case 31:
                        i6 += zzle.zzh(i8, zza(t, j2), false);
                        break;
                    case 32:
                        i6 += zzle.zzi(i8, zza(t, j2), false);
                        break;
                    case 33:
                        i6 += zzle.zzg(i8, zza(t, j2), false);
                        break;
                    case 34:
                        i6 += zzle.zzc(i8, zza(t, j2), false);
                        break;
                    case 35:
                        int zzi = zzle.zzi((List) unsafe.getObject(t, j2));
                        if (zzi > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzi) + zzi;
                            break;
                        } else {
                            break;
                        }
                    case 36:
                        int zzh = zzle.zzh((List) unsafe.getObject(t, j2));
                        if (zzh > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzh) + zzh;
                            break;
                        } else {
                            break;
                        }
                    case 37:
                        int zza2 = zzle.zza((List) unsafe.getObject(t, j2));
                        if (zza2 > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zza2) + zza2;
                            break;
                        } else {
                            break;
                        }
                    case 38:
                        int zzb2 = zzle.zzb((List) unsafe.getObject(t, j2));
                        if (zzb2 > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzb2) + zzb2;
                            break;
                        } else {
                            break;
                        }
                    case 39:
                        int zze = zzle.zze((List) unsafe.getObject(t, j2));
                        if (zze > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zze) + zze;
                            break;
                        } else {
                            break;
                        }
                    case 40:
                        int zzi2 = zzle.zzi((List) unsafe.getObject(t, j2));
                        if (zzi2 > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzi2) + zzi2;
                            break;
                        } else {
                            break;
                        }
                    case 41:
                        int zzh2 = zzle.zzh((List) unsafe.getObject(t, j2));
                        if (zzh2 > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzh2) + zzh2;
                            break;
                        } else {
                            break;
                        }
                    case 42:
                        int zzj = zzle.zzj((List) unsafe.getObject(t, j2));
                        if (zzj > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzj) + zzj;
                            break;
                        } else {
                            break;
                        }
                    case 43:
                        int zzf2 = zzle.zzf((List) unsafe.getObject(t, j2));
                        if (zzf2 > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzf2) + zzf2;
                            break;
                        } else {
                            break;
                        }
                    case 44:
                        int zzd2 = zzle.zzd((List) unsafe.getObject(t, j2));
                        if (zzd2 > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzd2) + zzd2;
                            break;
                        } else {
                            break;
                        }
                    case 45:
                        int zzh3 = zzle.zzh((List) unsafe.getObject(t, j2));
                        if (zzh3 > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzh3) + zzh3;
                            break;
                        } else {
                            break;
                        }
                    case 46:
                        int zzi3 = zzle.zzi((List) unsafe.getObject(t, j2));
                        if (zzi3 > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzi3) + zzi3;
                            break;
                        } else {
                            break;
                        }
                    case 47:
                        int zzg = zzle.zzg((List) unsafe.getObject(t, j2));
                        if (zzg > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzg) + zzg;
                            break;
                        } else {
                            break;
                        }
                    case 48:
                        int zzc = zzle.zzc((List) unsafe.getObject(t, j2));
                        if (zzc > 0) {
                            i6 += zzii.zze(i8) + zzii.zzg(zzc) + zzc;
                            break;
                        } else {
                            break;
                        }
                    case 49:
                        i6 += zzle.zzb(i8, (List<zzkk>) zza(t, j2), zza(i5));
                        break;
                    case 50:
                        i6 += this.zzs.zza(i8, zzma.zzf(t, j2), zzb(i5));
                        break;
                    case 51:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzb(i8, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                            break;
                        } else {
                            break;
                        }
                    case 52:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzb(i8, 0.0f);
                            break;
                        } else {
                            break;
                        }
                    case 53:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzd(i8, zze(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 54:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zze(i8, zze(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 55:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzf(i8, zzd(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzg(i8, 0L);
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzi(i8, 0);
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_TEXT_COPIED /* 58 */:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzb(i8, true);
                            break;
                        } else {
                            break;
                        }
                    case 59:
                        if (zza((zzko<T>) t, i8, i5)) {
                            Object zzf3 = zzma.zzf(t, j2);
                            if (zzf3 instanceof zzht) {
                                i6 += zzii.zzc(i8, (zzht) zzf3);
                                break;
                            } else {
                                i6 += zzii.zzb(i8, (String) zzf3);
                                break;
                            }
                        } else {
                            break;
                        }
                    case UndoView.ACTION_PHONE_COPIED /* 60 */:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzle.zza(i8, zzma.zzf(t, j2), zza(i5));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzc(i8, (zzht) zzma.zzf(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 62:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzg(i8, zzd(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzk(i8, zzd(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 64:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzj(i8, 0);
                            break;
                        } else {
                            break;
                        }
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzh(i8, 0L);
                            break;
                        } else {
                            break;
                        }
                    case 66:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzh(i8, zzd(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 67:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzf(i8, zze(t, j2));
                            break;
                        } else {
                            break;
                        }
                    case 68:
                        if (zza((zzko<T>) t, i8, i5)) {
                            i6 += zzii.zzc(i8, (zzkk) zzma.zzf(t, j2), zza(i5));
                            break;
                        } else {
                            break;
                        }
                }
                i5 += 3;
                i2 = 267386880;
            }
            return i6 + zza((zzlu) this.zzq, (Object) t);
        }
        Unsafe unsafe2 = zzb;
        int i10 = 0;
        int i11 = 0;
        int i12 = 1048575;
        int i13 = 0;
        while (i10 < this.zzc.length) {
            int zzd3 = zzd(i10);
            int[] iArr = this.zzc;
            int i14 = iArr[i10];
            int i15 = (zzd3 & 267386880) >>> 20;
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
            long j3 = zzd3 & i3;
            switch (i15) {
                case 0:
                    j = 0;
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzb(i14, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                        break;
                    } else {
                        break;
                    }
                case 1:
                    j = 0;
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzb(i14, 0.0f);
                        break;
                    } else {
                        break;
                    }
                case 2:
                    j = 0;
                    if ((i & i13) != 0) {
                        i11 += zzii.zzd(i14, unsafe2.getLong(t, j3));
                        break;
                    } else {
                        break;
                    }
                case 3:
                    j = 0;
                    if ((i & i13) != 0) {
                        i11 += zzii.zze(i14, unsafe2.getLong(t, j3));
                        break;
                    } else {
                        break;
                    }
                case 4:
                    j = 0;
                    if ((i & i13) != 0) {
                        i11 += zzii.zzf(i14, unsafe2.getInt(t, j3));
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
                        i11 += zzii.zzg(i14, 0L);
                        break;
                    }
                case 6:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzi(i14, 0);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 7:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzb(i14, true);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 8:
                    if ((i13 & i) != 0) {
                        Object object = unsafe2.getObject(t, j3);
                        if (object instanceof zzht) {
                            i11 += zzii.zzc(i14, (zzht) object);
                            j = 0;
                            break;
                        } else {
                            i11 += zzii.zzb(i14, (String) object);
                            j = 0;
                            break;
                        }
                    } else {
                        j = 0;
                        break;
                    }
                case 9:
                    if ((i13 & i) != 0) {
                        i11 += zzle.zza(i14, unsafe2.getObject(t, j3), zza(i10));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 10:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzc(i14, (zzht) unsafe2.getObject(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 11:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzg(i14, unsafe2.getInt(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 12:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzk(i14, unsafe2.getInt(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 13:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzj(i14, 0);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 14:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzh(i14, 0L);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 15:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzh(i14, unsafe2.getInt(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 16:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzf(i14, unsafe2.getLong(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 17:
                    if ((i13 & i) != 0) {
                        i11 += zzii.zzc(i14, (zzkk) unsafe2.getObject(t, j3), zza(i10));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 18:
                    i11 += zzle.zzi(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 19:
                    i11 += zzle.zzh(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 20:
                    i11 += zzle.zza(i14, (List<Long>) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 21:
                    i11 += zzle.zzb(i14, (List<Long>) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 22:
                    i11 += zzle.zze(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 23:
                    i11 += zzle.zzi(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 24:
                    i11 += zzle.zzh(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 25:
                    i11 += zzle.zzj(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 26:
                    i11 += zzle.zza(i14, (List) unsafe2.getObject(t, j3));
                    j = 0;
                    break;
                case 27:
                    i11 += zzle.zza(i14, (List<?>) unsafe2.getObject(t, j3), zza(i10));
                    j = 0;
                    break;
                case 28:
                    i11 += zzle.zzb(i14, (List) unsafe2.getObject(t, j3));
                    j = 0;
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    i11 += zzle.zzf(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 30:
                    i11 += zzle.zzd(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 31:
                    i11 += zzle.zzh(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 32:
                    i11 += zzle.zzi(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 33:
                    i11 += zzle.zzg(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 34:
                    i11 += zzle.zzc(i14, (List) unsafe2.getObject(t, j3), false);
                    j = 0;
                    break;
                case 35:
                    int zzi4 = zzle.zzi((List) unsafe2.getObject(t, j3));
                    if (zzi4 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzi4) + zzi4;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 36:
                    int zzh4 = zzle.zzh((List) unsafe2.getObject(t, j3));
                    if (zzh4 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzh4) + zzh4;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 37:
                    int zza3 = zzle.zza((List) unsafe2.getObject(t, j3));
                    if (zza3 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zza3) + zza3;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 38:
                    int zzb3 = zzle.zzb((List) unsafe2.getObject(t, j3));
                    if (zzb3 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzb3) + zzb3;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 39:
                    int zze2 = zzle.zze((List) unsafe2.getObject(t, j3));
                    if (zze2 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zze2) + zze2;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 40:
                    int zzi5 = zzle.zzi((List) unsafe2.getObject(t, j3));
                    if (zzi5 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzi5) + zzi5;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 41:
                    int zzh5 = zzle.zzh((List) unsafe2.getObject(t, j3));
                    if (zzh5 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzh5) + zzh5;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 42:
                    int zzj2 = zzle.zzj((List) unsafe2.getObject(t, j3));
                    if (zzj2 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzj2) + zzj2;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 43:
                    int zzf4 = zzle.zzf((List) unsafe2.getObject(t, j3));
                    if (zzf4 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzf4) + zzf4;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 44:
                    int zzd4 = zzle.zzd((List) unsafe2.getObject(t, j3));
                    if (zzd4 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzd4) + zzd4;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 45:
                    int zzh6 = zzle.zzh((List) unsafe2.getObject(t, j3));
                    if (zzh6 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzh6) + zzh6;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 46:
                    int zzi6 = zzle.zzi((List) unsafe2.getObject(t, j3));
                    if (zzi6 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzi6) + zzi6;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 47:
                    int zzg2 = zzle.zzg((List) unsafe2.getObject(t, j3));
                    if (zzg2 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzg2) + zzg2;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 48:
                    int zzc2 = zzle.zzc((List) unsafe2.getObject(t, j3));
                    if (zzc2 > 0) {
                        i11 += zzii.zze(i14) + zzii.zzg(zzc2) + zzc2;
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 49:
                    i11 += zzle.zzb(i14, (List) unsafe2.getObject(t, j3), zza(i10));
                    j = 0;
                    break;
                case 50:
                    i11 += this.zzs.zza(i14, unsafe2.getObject(t, j3), zzb(i10));
                    j = 0;
                    break;
                case 51:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzb(i14, (double) FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 52:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzb(i14, 0.0f);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 53:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzd(i14, zze(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 54:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zze(i14, zze(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 55:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzf(i14, zzd(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzg(i14, 0L);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzi(i14, 0);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzb(i14, true);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 59:
                    if (zza((zzko<T>) t, i14, i10)) {
                        Object object2 = unsafe2.getObject(t, j3);
                        if (object2 instanceof zzht) {
                            i11 += zzii.zzc(i14, (zzht) object2);
                            j = 0;
                            break;
                        } else {
                            i11 += zzii.zzb(i14, (String) object2);
                            j = 0;
                            break;
                        }
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzle.zza(i14, unsafe2.getObject(t, j3), zza(i10));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzc(i14, (zzht) unsafe2.getObject(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 62:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzg(i14, zzd(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzk(i14, zzd(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 64:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzj(i14, 0);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzh(i14, 0L);
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 66:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzh(i14, zzd(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 67:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzf(i14, zze(t, j3));
                        j = 0;
                        break;
                    } else {
                        j = 0;
                        break;
                    }
                case 68:
                    if (zza((zzko<T>) t, i14, i10)) {
                        i11 += zzii.zzc(i14, (zzkk) unsafe2.getObject(t, j3), zza(i10));
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
        int zza4 = i11 + zza((zzlu) this.zzq, (Object) t);
        if (this.zzh) {
            zziu<?> zza5 = this.zzr.zza(t);
            for (int i19 = 0; i19 < zza5.zza.zzc(); i19++) {
                Map.Entry<?, Object> zzb4 = zza5.zza.zzb(i19);
                i18 += zziu.zzc((zziw) zzb4.getKey(), zzb4.getValue());
            }
            for (Map.Entry<?, Object> entry : zza5.zza.zzd()) {
                i18 += zziu.zzc((zziw) entry.getKey(), entry.getValue());
            }
            return zza4 + i18;
        }
        return zza4;
    }

    private static <UT, UB> int zza(zzlu<UT, UB> zzluVar, T t) {
        return zzluVar.zzf(zzluVar.zzb(t));
    }

    private static List<?> zza(Object obj, long j) {
        return (List) zzma.zzf(obj, j);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003f  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x059f  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x05e2  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x0b42  */
    @Override // com.google.android.gms.internal.vision.zzlc
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void zza(T r14, com.google.android.gms.internal.vision.zzmr r15) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 3196
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zza(java.lang.Object, com.google.android.gms.internal.vision.zzmr):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0035  */
    /* JADX WARN: Removed duplicated region for block: B:188:0x0555  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void zzb(T r18, com.google.android.gms.internal.vision.zzmr r19) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1534
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zzb(java.lang.Object, com.google.android.gms.internal.vision.zzmr):void");
    }

    private final <K, V> void zza(zzmr zzmrVar, int i, Object obj, int i2) throws IOException {
        if (obj != null) {
            zzmrVar.zza(i, this.zzs.zzb(zzb(i2)), this.zzs.zzc(obj));
        }
    }

    private static <UT, UB> void zza(zzlu<UT, UB> zzluVar, T t, zzmr zzmrVar) throws IOException {
        zzluVar.zza((zzlu<UT, UB>) zzluVar.zzb(t), zzmrVar);
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zza(T t, zzld zzldVar, zzio zzioVar) throws IOException {
        Object obj;
        zziu<?> zziuVar;
        if (zzioVar == null) {
            throw new NullPointerException();
        }
        zzlu zzluVar = this.zzq;
        zziq<?> zziqVar = this.zzr;
        zziu<?> zziuVar2 = null;
        Object obj2 = null;
        while (true) {
            try {
                int zza2 = zzldVar.zza();
                int zzg = zzg(zza2);
                if (zzg < 0) {
                    if (zza2 == Integer.MAX_VALUE) {
                        for (int i = this.zzm; i < this.zzn; i++) {
                            obj2 = zza((Object) t, this.zzl[i], (int) obj2, (zzlu<UT, int>) zzluVar);
                        }
                        if (obj2 != null) {
                            zzluVar.zzb((Object) t, (T) obj2);
                            return;
                        }
                        return;
                    }
                    if (!this.zzh) {
                        obj = null;
                    } else {
                        obj = zziqVar.zza(zzioVar, this.zzg, zza2);
                    }
                    if (obj != null) {
                        if (zziuVar2 != null) {
                            zziuVar = zziuVar2;
                        } else {
                            zziuVar = zziqVar.zzb(t);
                        }
                        obj2 = zziqVar.zza(zzldVar, obj, zzioVar, zziuVar, obj2, zzluVar);
                        zziuVar2 = zziuVar;
                    } else {
                        zzluVar.zza(zzldVar);
                        if (obj2 == null) {
                            obj2 = zzluVar.zzc(t);
                        }
                        if (!zzluVar.zza((zzlu) obj2, zzldVar)) {
                            for (int i2 = this.zzm; i2 < this.zzn; i2++) {
                                obj2 = zza((Object) t, this.zzl[i2], (int) obj2, (zzlu<UT, int>) zzluVar);
                            }
                            if (obj2 != null) {
                                zzluVar.zzb((Object) t, (T) obj2);
                                return;
                            }
                            return;
                        }
                    }
                } else {
                    int zzd = zzd(zzg);
                    switch ((267386880 & zzd) >>> 20) {
                        case 0:
                            zzma.zza(t, zzd & 1048575, zzldVar.zzd());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 1:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zze());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 2:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzg());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 3:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzf());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 4:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzh());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 5:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzi());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 6:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzj());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 7:
                            zzma.zza(t, zzd & 1048575, zzldVar.zzk());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 8:
                            zza(t, zzd, zzldVar);
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 9:
                            if (zza((zzko<T>) t, zzg)) {
                                long j = zzd & 1048575;
                                zzma.zza(t, j, zzjf.zza(zzma.zzf(t, j), zzldVar.zza(zza(zzg), zzioVar)));
                                break;
                            } else {
                                zzma.zza(t, zzd & 1048575, zzldVar.zza(zza(zzg), zzioVar));
                                zzb((zzko<T>) t, zzg);
                                break;
                            }
                        case 10:
                            zzma.zza(t, zzd & 1048575, zzldVar.zzn());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 11:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzo());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 12:
                            int zzp = zzldVar.zzp();
                            zzjg zzc = zzc(zzg);
                            if (zzc != null && !zzc.zza(zzp)) {
                                obj2 = zzle.zza(zza2, zzp, obj2, zzluVar);
                                break;
                            }
                            zzma.zza((Object) t, zzd & 1048575, zzp);
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 13:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzq());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 14:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzr());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 15:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzs());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 16:
                            zzma.zza((Object) t, zzd & 1048575, zzldVar.zzt());
                            zzb((zzko<T>) t, zzg);
                            break;
                        case 17:
                            if (zza((zzko<T>) t, zzg)) {
                                long j2 = zzd & 1048575;
                                zzma.zza(t, j2, zzjf.zza(zzma.zzf(t, j2), zzldVar.zzb(zza(zzg), zzioVar)));
                                break;
                            } else {
                                zzma.zza(t, zzd & 1048575, zzldVar.zzb(zza(zzg), zzioVar));
                                zzb((zzko<T>) t, zzg);
                                break;
                            }
                        case 18:
                            zzldVar.zza(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 19:
                            zzldVar.zzb(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 20:
                            zzldVar.zzd(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 21:
                            zzldVar.zzc(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 22:
                            zzldVar.zze(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 23:
                            zzldVar.zzf(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 24:
                            zzldVar.zzg(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 25:
                            zzldVar.zzh(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 26:
                            if (zzf(zzd)) {
                                zzldVar.zzj(this.zzp.zza(t, zzd & 1048575));
                                break;
                            } else {
                                zzldVar.zzi(this.zzp.zza(t, zzd & 1048575));
                                break;
                            }
                        case 27:
                            zzldVar.zza(this.zzp.zza(t, zzd & 1048575), zza(zzg), zzioVar);
                            break;
                        case 28:
                            zzldVar.zzk(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                            zzldVar.zzl(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 30:
                            List<Integer> zza3 = this.zzp.zza(t, zzd & 1048575);
                            zzldVar.zzm(zza3);
                            obj2 = zzle.zza(zza2, zza3, zzc(zzg), obj2, zzluVar);
                            break;
                        case 31:
                            zzldVar.zzn(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 32:
                            zzldVar.zzo(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 33:
                            zzldVar.zzp(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 34:
                            zzldVar.zzq(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 35:
                            zzldVar.zza(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 36:
                            zzldVar.zzb(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 37:
                            zzldVar.zzd(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 38:
                            zzldVar.zzc(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 39:
                            zzldVar.zze(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 40:
                            zzldVar.zzf(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 41:
                            zzldVar.zzg(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 42:
                            zzldVar.zzh(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 43:
                            zzldVar.zzl(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 44:
                            List<Integer> zza4 = this.zzp.zza(t, zzd & 1048575);
                            zzldVar.zzm(zza4);
                            obj2 = zzle.zza(zza2, zza4, zzc(zzg), obj2, zzluVar);
                            break;
                        case 45:
                            zzldVar.zzn(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 46:
                            zzldVar.zzo(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 47:
                            zzldVar.zzp(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 48:
                            zzldVar.zzq(this.zzp.zza(t, zzd & 1048575));
                            break;
                        case 49:
                            zzldVar.zzb(this.zzp.zza(t, zzd & 1048575), zza(zzg), zzioVar);
                            break;
                        case 50:
                            Object zzb2 = zzb(zzg);
                            long zzd2 = zzd(zzg) & 1048575;
                            Object zzf = zzma.zzf(t, zzd2);
                            if (zzf == null) {
                                zzf = this.zzs.zzf(zzb2);
                                zzma.zza(t, zzd2, zzf);
                            } else if (this.zzs.zzd(zzf)) {
                                Object zzf2 = this.zzs.zzf(zzb2);
                                this.zzs.zza(zzf2, zzf);
                                zzma.zza(t, zzd2, zzf2);
                                zzf = zzf2;
                            }
                            zzldVar.zza(this.zzs.zza(zzf), this.zzs.zzb(zzb2), zzioVar);
                            break;
                        case 51:
                            zzma.zza(t, zzd & 1048575, Double.valueOf(zzldVar.zzd()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 52:
                            zzma.zza(t, zzd & 1048575, Float.valueOf(zzldVar.zze()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 53:
                            zzma.zza(t, zzd & 1048575, Long.valueOf(zzldVar.zzg()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 54:
                            zzma.zza(t, zzd & 1048575, Long.valueOf(zzldVar.zzf()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 55:
                            zzma.zza(t, zzd & 1048575, Integer.valueOf(zzldVar.zzh()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                            zzma.zza(t, zzd & 1048575, Long.valueOf(zzldVar.zzi()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                            zzma.zza(t, zzd & 1048575, Integer.valueOf(zzldVar.zzj()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case UndoView.ACTION_TEXT_COPIED /* 58 */:
                            zzma.zza(t, zzd & 1048575, Boolean.valueOf(zzldVar.zzk()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 59:
                            zza(t, zzd, zzldVar);
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case UndoView.ACTION_PHONE_COPIED /* 60 */:
                            if (zza((zzko<T>) t, zza2, zzg)) {
                                long j3 = zzd & 1048575;
                                zzma.zza(t, j3, zzjf.zza(zzma.zzf(t, j3), zzldVar.zza(zza(zzg), zzioVar)));
                            } else {
                                zzma.zza(t, zzd & 1048575, zzldVar.zza(zza(zzg), zzioVar));
                                zzb((zzko<T>) t, zzg);
                            }
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                            zzma.zza(t, zzd & 1048575, zzldVar.zzn());
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 62:
                            zzma.zza(t, zzd & 1048575, Integer.valueOf(zzldVar.zzo()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                            int zzp2 = zzldVar.zzp();
                            zzjg zzc2 = zzc(zzg);
                            if (zzc2 != null && !zzc2.zza(zzp2)) {
                                obj2 = zzle.zza(zza2, zzp2, obj2, zzluVar);
                                break;
                            }
                            zzma.zza(t, zzd & 1048575, Integer.valueOf(zzp2));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 64:
                            zzma.zza(t, zzd & 1048575, Integer.valueOf(zzldVar.zzq()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case VoIPService.CALL_MIN_LAYER /* 65 */:
                            zzma.zza(t, zzd & 1048575, Long.valueOf(zzldVar.zzr()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 66:
                            zzma.zza(t, zzd & 1048575, Integer.valueOf(zzldVar.zzs()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 67:
                            zzma.zza(t, zzd & 1048575, Long.valueOf(zzldVar.zzt()));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        case 68:
                            zzma.zza(t, zzd & 1048575, zzldVar.zzb(zza(zzg), zzioVar));
                            zzb((zzko<T>) t, zza2, zzg);
                            break;
                        default:
                            if (obj2 == null) {
                                try {
                                    obj2 = zzluVar.zza();
                                } catch (zzjn e) {
                                    zzluVar.zza(zzldVar);
                                    if (obj2 == null) {
                                        obj2 = zzluVar.zzc(t);
                                    }
                                    if (!zzluVar.zza((zzlu) obj2, zzldVar)) {
                                        for (int i3 = this.zzm; i3 < this.zzn; i3++) {
                                            obj2 = zza((Object) t, this.zzl[i3], (int) obj2, (zzlu<UT, int>) zzluVar);
                                        }
                                        if (obj2 != null) {
                                            zzluVar.zzb((Object) t, (T) obj2);
                                            return;
                                        }
                                        return;
                                    }
                                    break;
                                }
                            }
                            if (!zzluVar.zza((zzlu) obj2, zzldVar)) {
                                for (int i4 = this.zzm; i4 < this.zzn; i4++) {
                                    obj2 = zza((Object) t, this.zzl[i4], (int) obj2, (zzlu<UT, int>) zzluVar);
                                }
                                if (obj2 != null) {
                                    zzluVar.zzb((Object) t, (T) obj2);
                                    return;
                                }
                                return;
                            }
                            break;
                    }
                }
            } catch (Throwable th) {
                for (int i5 = this.zzm; i5 < this.zzn; i5++) {
                    obj2 = zza((Object) t, this.zzl[i5], (int) obj2, (zzlu<UT, int>) zzluVar);
                }
                if (obj2 != null) {
                    zzluVar.zzb((Object) t, (T) obj2);
                }
                throw th;
            }
        }
    }

    private static zzlx zze(Object obj) {
        zzjb zzjbVar = (zzjb) obj;
        zzlx zzlxVar = zzjbVar.zzb;
        if (zzlxVar == zzlx.zza()) {
            zzlx zzb2 = zzlx.zzb();
            zzjbVar.zzb = zzb2;
            return zzb2;
        }
        return zzlxVar;
    }

    private static int zza(byte[] bArr, int i, int i2, zzml zzmlVar, Class<?> cls, zzhn zzhnVar) throws IOException {
        switch (zzkr.zza[zzmlVar.ordinal()]) {
            case 1:
                int zzb2 = zzhl.zzb(bArr, i, zzhnVar);
                zzhnVar.zzc = Boolean.valueOf(zzhnVar.zzb != 0);
                return zzb2;
            case 2:
                return zzhl.zze(bArr, i, zzhnVar);
            case 3:
                zzhnVar.zzc = Double.valueOf(zzhl.zzc(bArr, i));
                return i + 8;
            case 4:
            case 5:
                zzhnVar.zzc = Integer.valueOf(zzhl.zza(bArr, i));
                return i + 4;
            case 6:
            case 7:
                zzhnVar.zzc = Long.valueOf(zzhl.zzb(bArr, i));
                return i + 8;
            case 8:
                zzhnVar.zzc = Float.valueOf(zzhl.zzd(bArr, i));
                return i + 4;
            case 9:
            case 10:
            case 11:
                int zza2 = zzhl.zza(bArr, i, zzhnVar);
                zzhnVar.zzc = Integer.valueOf(zzhnVar.zza);
                return zza2;
            case 12:
            case 13:
                int zzb3 = zzhl.zzb(bArr, i, zzhnVar);
                zzhnVar.zzc = Long.valueOf(zzhnVar.zzb);
                return zzb3;
            case 14:
                return zzhl.zza(zzky.zza().zza((Class) cls), bArr, i, i2, zzhnVar);
            case 15:
                int zza3 = zzhl.zza(bArr, i, zzhnVar);
                zzhnVar.zzc = Integer.valueOf(zzif.zze(zzhnVar.zza));
                return zza3;
            case 16:
                int zzb4 = zzhl.zzb(bArr, i, zzhnVar);
                zzhnVar.zzc = Long.valueOf(zzif.zza(zzhnVar.zzb));
                return zzb4;
            case 17:
                return zzhl.zzd(bArr, i, zzhnVar);
            default:
                throw new RuntimeException("unsupported field type.");
        }
    }

    private final int zza(T t, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, long j, int i7, long j2, zzhn zzhnVar) throws IOException {
        int i8;
        Unsafe unsafe = zzb;
        zzjl zzjlVar = (zzjl) unsafe.getObject(t, j2);
        if (!zzjlVar.zza()) {
            int size = zzjlVar.size();
            zzjlVar = zzjlVar.zza(size == 0 ? 10 : size << 1);
            unsafe.putObject(t, j2, zzjlVar);
        }
        switch (i7) {
            case 18:
            case 35:
                if (i5 == 2) {
                    zzin zzinVar = (zzin) zzjlVar;
                    int zza2 = zzhl.zza(bArr, i, zzhnVar);
                    int i9 = zzhnVar.zza + zza2;
                    while (zza2 < i9) {
                        zzinVar.zza(zzhl.zzc(bArr, zza2));
                        zza2 += 8;
                    }
                    if (zza2 != i9) {
                        throw zzjk.zza();
                    }
                    return zza2;
                } else if (i5 == 1) {
                    zzin zzinVar2 = (zzin) zzjlVar;
                    zzinVar2.zza(zzhl.zzc(bArr, i));
                    int i10 = i + 8;
                    while (i10 < i2) {
                        int zza3 = zzhl.zza(bArr, i10, zzhnVar);
                        if (i3 != zzhnVar.zza) {
                            return i10;
                        }
                        zzinVar2.zza(zzhl.zzc(bArr, zza3));
                        i10 = zza3 + 8;
                    }
                    return i10;
                }
                break;
            case 19:
            case 36:
                if (i5 == 2) {
                    zzja zzjaVar = (zzja) zzjlVar;
                    int zza4 = zzhl.zza(bArr, i, zzhnVar);
                    int i11 = zzhnVar.zza + zza4;
                    while (zza4 < i11) {
                        zzjaVar.zza(zzhl.zzd(bArr, zza4));
                        zza4 += 4;
                    }
                    if (zza4 != i11) {
                        throw zzjk.zza();
                    }
                    return zza4;
                } else if (i5 == 5) {
                    zzja zzjaVar2 = (zzja) zzjlVar;
                    zzjaVar2.zza(zzhl.zzd(bArr, i));
                    int i12 = i + 4;
                    while (i12 < i2) {
                        int zza5 = zzhl.zza(bArr, i12, zzhnVar);
                        if (i3 != zzhnVar.zza) {
                            return i12;
                        }
                        zzjaVar2.zza(zzhl.zzd(bArr, zza5));
                        i12 = zza5 + 4;
                    }
                    return i12;
                }
                break;
            case 20:
            case 21:
            case 37:
            case 38:
                if (i5 == 2) {
                    zzjy zzjyVar = (zzjy) zzjlVar;
                    int zza6 = zzhl.zza(bArr, i, zzhnVar);
                    int i13 = zzhnVar.zza + zza6;
                    while (zza6 < i13) {
                        zza6 = zzhl.zzb(bArr, zza6, zzhnVar);
                        zzjyVar.zza(zzhnVar.zzb);
                    }
                    if (zza6 != i13) {
                        throw zzjk.zza();
                    }
                    return zza6;
                } else if (i5 == 0) {
                    zzjy zzjyVar2 = (zzjy) zzjlVar;
                    int zzb2 = zzhl.zzb(bArr, i, zzhnVar);
                    zzjyVar2.zza(zzhnVar.zzb);
                    while (zzb2 < i2) {
                        int zza7 = zzhl.zza(bArr, zzb2, zzhnVar);
                        if (i3 != zzhnVar.zza) {
                            return zzb2;
                        }
                        zzb2 = zzhl.zzb(bArr, zza7, zzhnVar);
                        zzjyVar2.zza(zzhnVar.zzb);
                    }
                    return zzb2;
                }
                break;
            case 22:
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
            case 39:
            case 43:
                if (i5 == 2) {
                    return zzhl.zza(bArr, i, zzjlVar, zzhnVar);
                }
                if (i5 == 0) {
                    return zzhl.zza(i3, bArr, i, i2, zzjlVar, zzhnVar);
                }
                break;
            case 23:
            case 32:
            case 40:
            case 46:
                if (i5 == 2) {
                    zzjy zzjyVar3 = (zzjy) zzjlVar;
                    int zza8 = zzhl.zza(bArr, i, zzhnVar);
                    int i14 = zzhnVar.zza + zza8;
                    while (zza8 < i14) {
                        zzjyVar3.zza(zzhl.zzb(bArr, zza8));
                        zza8 += 8;
                    }
                    if (zza8 != i14) {
                        throw zzjk.zza();
                    }
                    return zza8;
                } else if (i5 == 1) {
                    zzjy zzjyVar4 = (zzjy) zzjlVar;
                    zzjyVar4.zza(zzhl.zzb(bArr, i));
                    int i15 = i + 8;
                    while (i15 < i2) {
                        int zza9 = zzhl.zza(bArr, i15, zzhnVar);
                        if (i3 != zzhnVar.zza) {
                            return i15;
                        }
                        zzjyVar4.zza(zzhl.zzb(bArr, zza9));
                        i15 = zza9 + 8;
                    }
                    return i15;
                }
                break;
            case 24:
            case 31:
            case 41:
            case 45:
                if (i5 == 2) {
                    zzjd zzjdVar = (zzjd) zzjlVar;
                    int zza10 = zzhl.zza(bArr, i, zzhnVar);
                    int i16 = zzhnVar.zza + zza10;
                    while (zza10 < i16) {
                        zzjdVar.zzc(zzhl.zza(bArr, zza10));
                        zza10 += 4;
                    }
                    if (zza10 != i16) {
                        throw zzjk.zza();
                    }
                    return zza10;
                } else if (i5 == 5) {
                    zzjd zzjdVar2 = (zzjd) zzjlVar;
                    zzjdVar2.zzc(zzhl.zza(bArr, i));
                    int i17 = i + 4;
                    while (i17 < i2) {
                        int zza11 = zzhl.zza(bArr, i17, zzhnVar);
                        if (i3 != zzhnVar.zza) {
                            return i17;
                        }
                        zzjdVar2.zzc(zzhl.zza(bArr, zza11));
                        i17 = zza11 + 4;
                    }
                    return i17;
                }
                break;
            case 25:
            case 42:
                if (i5 == 2) {
                    zzhr zzhrVar = (zzhr) zzjlVar;
                    int zza12 = zzhl.zza(bArr, i, zzhnVar);
                    int i18 = zzhnVar.zza + zza12;
                    while (zza12 < i18) {
                        zza12 = zzhl.zzb(bArr, zza12, zzhnVar);
                        zzhrVar.zza(zzhnVar.zzb != 0);
                    }
                    if (zza12 != i18) {
                        throw zzjk.zza();
                    }
                    return zza12;
                } else if (i5 == 0) {
                    zzhr zzhrVar2 = (zzhr) zzjlVar;
                    int zzb3 = zzhl.zzb(bArr, i, zzhnVar);
                    zzhrVar2.zza(zzhnVar.zzb != 0);
                    while (zzb3 < i2) {
                        int zza13 = zzhl.zza(bArr, zzb3, zzhnVar);
                        if (i3 == zzhnVar.zza) {
                            zzb3 = zzhl.zzb(bArr, zza13, zzhnVar);
                            zzhrVar2.zza(zzhnVar.zzb != 0);
                        } else {
                            return zzb3;
                        }
                    }
                    return zzb3;
                }
                break;
            case 26:
                if (i5 == 2) {
                    if ((j & 536870912) == 0) {
                        int zza14 = zzhl.zza(bArr, i, zzhnVar);
                        int i19 = zzhnVar.zza;
                        if (i19 < 0) {
                            throw zzjk.zzb();
                        }
                        if (i19 == 0) {
                            zzjlVar.add("");
                        } else {
                            zzjlVar.add(new String(bArr, zza14, i19, zzjf.zza));
                            zza14 += i19;
                        }
                        while (zza14 < i2) {
                            int zza15 = zzhl.zza(bArr, zza14, zzhnVar);
                            if (i3 == zzhnVar.zza) {
                                zza14 = zzhl.zza(bArr, zza15, zzhnVar);
                                int i20 = zzhnVar.zza;
                                if (i20 < 0) {
                                    throw zzjk.zzb();
                                }
                                if (i20 == 0) {
                                    zzjlVar.add("");
                                } else {
                                    zzjlVar.add(new String(bArr, zza14, i20, zzjf.zza));
                                    zza14 += i20;
                                }
                            } else {
                                return zza14;
                            }
                        }
                        return zza14;
                    }
                    int zza16 = zzhl.zza(bArr, i, zzhnVar);
                    int i21 = zzhnVar.zza;
                    if (i21 < 0) {
                        throw zzjk.zzb();
                    }
                    if (i21 == 0) {
                        zzjlVar.add("");
                    } else {
                        int i22 = zza16 + i21;
                        if (!zzmd.zza(bArr, zza16, i22)) {
                            throw zzjk.zzh();
                        }
                        zzjlVar.add(new String(bArr, zza16, i21, zzjf.zza));
                        zza16 = i22;
                    }
                    while (zza16 < i2) {
                        int zza17 = zzhl.zza(bArr, zza16, zzhnVar);
                        if (i3 == zzhnVar.zza) {
                            zza16 = zzhl.zza(bArr, zza17, zzhnVar);
                            int i23 = zzhnVar.zza;
                            if (i23 < 0) {
                                throw zzjk.zzb();
                            }
                            if (i23 == 0) {
                                zzjlVar.add("");
                            } else {
                                int i24 = zza16 + i23;
                                if (!zzmd.zza(bArr, zza16, i24)) {
                                    throw zzjk.zzh();
                                }
                                zzjlVar.add(new String(bArr, zza16, i23, zzjf.zza));
                                zza16 = i24;
                            }
                        } else {
                            return zza16;
                        }
                    }
                    return zza16;
                }
                break;
            case 27:
                if (i5 == 2) {
                    return zzhl.zza(zza(i6), i3, bArr, i, i2, zzjlVar, zzhnVar);
                }
                break;
            case 28:
                if (i5 == 2) {
                    int zza18 = zzhl.zza(bArr, i, zzhnVar);
                    int i25 = zzhnVar.zza;
                    if (i25 < 0) {
                        throw zzjk.zzb();
                    }
                    if (i25 > bArr.length - zza18) {
                        throw zzjk.zza();
                    }
                    if (i25 == 0) {
                        zzjlVar.add(zzht.zza);
                    } else {
                        zzjlVar.add(zzht.zza(bArr, zza18, i25));
                        zza18 += i25;
                    }
                    while (zza18 < i2) {
                        int zza19 = zzhl.zza(bArr, zza18, zzhnVar);
                        if (i3 != zzhnVar.zza) {
                            return zza18;
                        }
                        zza18 = zzhl.zza(bArr, zza19, zzhnVar);
                        int i26 = zzhnVar.zza;
                        if (i26 < 0) {
                            throw zzjk.zzb();
                        }
                        if (i26 > bArr.length - zza18) {
                            throw zzjk.zza();
                        }
                        if (i26 == 0) {
                            zzjlVar.add(zzht.zza);
                        } else {
                            zzjlVar.add(zzht.zza(bArr, zza18, i26));
                            zza18 += i26;
                        }
                    }
                    return zza18;
                }
                break;
            case 30:
            case 44:
                if (i5 == 2) {
                    i8 = zzhl.zza(bArr, i, zzjlVar, zzhnVar);
                } else if (i5 == 0) {
                    i8 = zzhl.zza(i3, bArr, i, i2, zzjlVar, zzhnVar);
                }
                zzjb zzjbVar = (zzjb) t;
                zzlx zzlxVar = zzjbVar.zzb;
                if (zzlxVar == zzlx.zza()) {
                    zzlxVar = null;
                }
                zzlx zzlxVar2 = (zzlx) zzle.zza(i4, zzjlVar, zzc(i6), zzlxVar, this.zzq);
                if (zzlxVar2 != null) {
                    zzjbVar.zzb = zzlxVar2;
                }
                return i8;
            case 33:
            case 47:
                if (i5 == 2) {
                    zzjd zzjdVar3 = (zzjd) zzjlVar;
                    int zza20 = zzhl.zza(bArr, i, zzhnVar);
                    int i27 = zzhnVar.zza + zza20;
                    while (zza20 < i27) {
                        zza20 = zzhl.zza(bArr, zza20, zzhnVar);
                        zzjdVar3.zzc(zzif.zze(zzhnVar.zza));
                    }
                    if (zza20 != i27) {
                        throw zzjk.zza();
                    }
                    return zza20;
                } else if (i5 == 0) {
                    zzjd zzjdVar4 = (zzjd) zzjlVar;
                    int zza21 = zzhl.zza(bArr, i, zzhnVar);
                    zzjdVar4.zzc(zzif.zze(zzhnVar.zza));
                    while (zza21 < i2) {
                        int zza22 = zzhl.zza(bArr, zza21, zzhnVar);
                        if (i3 != zzhnVar.zza) {
                            return zza21;
                        }
                        zza21 = zzhl.zza(bArr, zza22, zzhnVar);
                        zzjdVar4.zzc(zzif.zze(zzhnVar.zza));
                    }
                    return zza21;
                }
                break;
            case 34:
            case 48:
                if (i5 == 2) {
                    zzjy zzjyVar5 = (zzjy) zzjlVar;
                    int zza23 = zzhl.zza(bArr, i, zzhnVar);
                    int i28 = zzhnVar.zza + zza23;
                    while (zza23 < i28) {
                        zza23 = zzhl.zzb(bArr, zza23, zzhnVar);
                        zzjyVar5.zza(zzif.zza(zzhnVar.zzb));
                    }
                    if (zza23 != i28) {
                        throw zzjk.zza();
                    }
                    return zza23;
                } else if (i5 == 0) {
                    zzjy zzjyVar6 = (zzjy) zzjlVar;
                    int zzb4 = zzhl.zzb(bArr, i, zzhnVar);
                    zzjyVar6.zza(zzif.zza(zzhnVar.zzb));
                    while (zzb4 < i2) {
                        int zza24 = zzhl.zza(bArr, zzb4, zzhnVar);
                        if (i3 != zzhnVar.zza) {
                            return zzb4;
                        }
                        zzb4 = zzhl.zzb(bArr, zza24, zzhnVar);
                        zzjyVar6.zza(zzif.zza(zzhnVar.zzb));
                    }
                    return zzb4;
                }
                break;
            case 49:
                if (i5 == 3) {
                    zzlc zza25 = zza(i6);
                    int i29 = (i3 & (-8)) | 4;
                    int zza26 = zzhl.zza(zza25, bArr, i, i2, i29, zzhnVar);
                    zzjlVar.add(zzhnVar.zzc);
                    while (zza26 < i2) {
                        int zza27 = zzhl.zza(bArr, zza26, zzhnVar);
                        if (i3 == zzhnVar.zza) {
                            zza26 = zzhl.zza(zza25, bArr, zza27, i2, i29, zzhnVar);
                            zzjlVar.add(zzhnVar.zzc);
                        } else {
                            return zza26;
                        }
                    }
                    return zza26;
                }
                break;
        }
        return i;
    }

    private final <K, V> int zza(T t, byte[] bArr, int i, int i2, int i3, long j, zzhn zzhnVar) throws IOException {
        int i4;
        Unsafe unsafe = zzb;
        Object zzb2 = zzb(i3);
        Object object = unsafe.getObject(t, j);
        if (this.zzs.zzd(object)) {
            Object zzf = this.zzs.zzf(zzb2);
            this.zzs.zza(zzf, object);
            unsafe.putObject(t, j, zzf);
            object = zzf;
        }
        zzkf<?, ?> zzb3 = this.zzs.zzb(zzb2);
        Map<?, ?> zza2 = this.zzs.zza(object);
        int zza3 = zzhl.zza(bArr, i, zzhnVar);
        int i5 = zzhnVar.zza;
        if (i5 < 0 || i5 > i2 - zza3) {
            throw zzjk.zza();
        }
        int i6 = i5 + zza3;
        Object obj = (K) zzb3.zzb;
        Object obj2 = (V) zzb3.zzd;
        while (zza3 < i6) {
            int i7 = zza3 + 1;
            int i8 = bArr[zza3];
            if (i8 >= 0) {
                i4 = i7;
            } else {
                int zza4 = zzhl.zza(i8, bArr, i7, zzhnVar);
                i8 = zzhnVar.zza;
                i4 = zza4;
            }
            int i9 = i8 & 7;
            switch (i8 >>> 3) {
                case 1:
                    if (i9 != zzb3.zza.zzb()) {
                        break;
                    } else {
                        zza3 = zza(bArr, i4, i2, zzb3.zza, (Class<?>) null, zzhnVar);
                        obj = (K) zzhnVar.zzc;
                        continue;
                    }
                case 2:
                    if (i9 != zzb3.zzc.zzb()) {
                        break;
                    } else {
                        zza3 = zza(bArr, i4, i2, zzb3.zzc, zzb3.zzd.getClass(), zzhnVar);
                        obj2 = (V) zzhnVar.zzc;
                        continue;
                    }
            }
            zza3 = zzhl.zza(i8, bArr, i4, i2, zzhnVar);
        }
        if (zza3 != i6) {
            throw zzjk.zzg();
        }
        zza2.put(obj, obj2);
        return i6;
    }

    private final int zza(T t, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, long j, int i8, zzhn zzhnVar) throws IOException {
        int i9;
        Object obj;
        Object obj2;
        Unsafe unsafe = zzb;
        long j2 = this.zzc[i8 + 2] & 1048575;
        switch (i7) {
            case 51:
                if (i5 == 1) {
                    unsafe.putObject(t, j, Double.valueOf(zzhl.zzc(bArr, i)));
                    i9 = i + 8;
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 52:
                if (i5 == 5) {
                    unsafe.putObject(t, j, Float.valueOf(zzhl.zzd(bArr, i)));
                    i9 = i + 4;
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 53:
            case 54:
                if (i5 == 0) {
                    i9 = zzhl.zzb(bArr, i, zzhnVar);
                    unsafe.putObject(t, j, Long.valueOf(zzhnVar.zzb));
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 55:
            case 62:
                if (i5 == 0) {
                    i9 = zzhl.zza(bArr, i, zzhnVar);
                    unsafe.putObject(t, j, Integer.valueOf(zzhnVar.zza));
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case UndoView.ACTION_USERNAME_COPIED /* 56 */:
            case VoIPService.CALL_MIN_LAYER /* 65 */:
                if (i5 == 1) {
                    unsafe.putObject(t, j, Long.valueOf(zzhl.zzb(bArr, i)));
                    i9 = i + 8;
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
            case 64:
                if (i5 == 5) {
                    unsafe.putObject(t, j, Integer.valueOf(zzhl.zza(bArr, i)));
                    i9 = i + 4;
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case UndoView.ACTION_TEXT_COPIED /* 58 */:
                if (i5 == 0) {
                    i9 = zzhl.zzb(bArr, i, zzhnVar);
                    unsafe.putObject(t, j, Boolean.valueOf(zzhnVar.zzb != 0));
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 59:
                if (i5 == 2) {
                    int zza2 = zzhl.zza(bArr, i, zzhnVar);
                    int i10 = zzhnVar.zza;
                    if (i10 == 0) {
                        unsafe.putObject(t, j, "");
                    } else if ((i6 & 536870912) != 0 && !zzmd.zza(bArr, zza2, zza2 + i10)) {
                        throw zzjk.zzh();
                    } else {
                        unsafe.putObject(t, j, new String(bArr, zza2, i10, zzjf.zza));
                        zza2 += i10;
                    }
                    unsafe.putInt(t, j2, i4);
                    return zza2;
                }
                return i;
            case UndoView.ACTION_PHONE_COPIED /* 60 */:
                if (i5 == 2) {
                    int zza3 = zzhl.zza(zza(i8), bArr, i, i2, zzhnVar);
                    if (unsafe.getInt(t, j2) == i4) {
                        obj = unsafe.getObject(t, j);
                    } else {
                        obj = null;
                    }
                    if (obj == null) {
                        unsafe.putObject(t, j, zzhnVar.zzc);
                    } else {
                        unsafe.putObject(t, j, zzjf.zza(obj, zzhnVar.zzc));
                    }
                    unsafe.putInt(t, j2, i4);
                    return zza3;
                }
                return i;
            case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                if (i5 == 2) {
                    i9 = zzhl.zze(bArr, i, zzhnVar);
                    unsafe.putObject(t, j, zzhnVar.zzc);
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                if (i5 == 0) {
                    int zza4 = zzhl.zza(bArr, i, zzhnVar);
                    int i11 = zzhnVar.zza;
                    zzjg zzc = zzc(i8);
                    if (zzc == null || zzc.zza(i11)) {
                        unsafe.putObject(t, j, Integer.valueOf(i11));
                        i9 = zza4;
                        unsafe.putInt(t, j2, i4);
                        return i9;
                    }
                    zze(t).zza(i3, Long.valueOf(i11));
                    return zza4;
                }
                return i;
            case 66:
                if (i5 == 0) {
                    i9 = zzhl.zza(bArr, i, zzhnVar);
                    unsafe.putObject(t, j, Integer.valueOf(zzif.zze(zzhnVar.zza)));
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 67:
                if (i5 == 0) {
                    i9 = zzhl.zzb(bArr, i, zzhnVar);
                    unsafe.putObject(t, j, Long.valueOf(zzif.zza(zzhnVar.zzb)));
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 68:
                if (i5 == 3) {
                    i9 = zzhl.zza(zza(i8), bArr, i, i2, (i3 & (-8)) | 4, zzhnVar);
                    if (unsafe.getInt(t, j2) == i4) {
                        obj2 = unsafe.getObject(t, j);
                    } else {
                        obj2 = null;
                    }
                    if (obj2 == null) {
                        unsafe.putObject(t, j, zzhnVar.zzc);
                    } else {
                        unsafe.putObject(t, j, zzjf.zza(obj2, zzhnVar.zzc));
                    }
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            default:
                return i;
        }
    }

    private final zzlc zza(int i) {
        int i2 = (i / 3) << 1;
        zzlc zzlcVar = (zzlc) this.zzd[i2];
        if (zzlcVar != null) {
            return zzlcVar;
        }
        zzlc<T> zza2 = zzky.zza().zza((Class) ((Class) this.zzd[i2 + 1]));
        this.zzd[i2] = zza2;
        return zza2;
    }

    private final Object zzb(int i) {
        return this.zzd[(i / 3) << 1];
    }

    private final zzjg zzc(int i) {
        return (zzjg) this.zzd[((i / 3) << 1) + 1];
    }

    /* JADX WARN: Code restructure failed: missing block: B:193:0x073f, code lost:
        if (r1 == 1048575) goto L195;
     */
    /* JADX WARN: Code restructure failed: missing block: B:194:0x0741, code lost:
        r31.putInt(r13, r1, r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:195:0x0747, code lost:
        r1 = r8.zzm;
     */
    /* JADX WARN: Code restructure failed: missing block: B:197:0x074c, code lost:
        if (r1 >= r8.zzn) goto L302;
     */
    /* JADX WARN: Code restructure failed: missing block: B:198:0x074e, code lost:
        r4 = (com.google.android.gms.internal.vision.zzlx) r8.zza((java.lang.Object) r13, r8.zzl[r1], (int) r4, (com.google.android.gms.internal.vision.zzlu<UT, int>) r8.zzq);
        r1 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x075e, code lost:
        if (r4 == null) goto L201;
     */
    /* JADX WARN: Code restructure failed: missing block: B:200:0x0760, code lost:
        r8.zzq.zzb((java.lang.Object) r13, (T) r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:201:0x0765, code lost:
        if (r9 != 0) goto L205;
     */
    /* JADX WARN: Code restructure failed: missing block: B:202:0x0767, code lost:
        if (r0 != r6) goto L203;
     */
    /* JADX WARN: Code restructure failed: missing block: B:204:0x076e, code lost:
        throw com.google.android.gms.internal.vision.zzjk.zzg();
     */
    /* JADX WARN: Code restructure failed: missing block: B:205:0x076f, code lost:
        if (r0 > r6) goto L208;
     */
    /* JADX WARN: Code restructure failed: missing block: B:206:0x0771, code lost:
        if (r3 != r9) goto L208;
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x0773, code lost:
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x0779, code lost:
        throw com.google.android.gms.internal.vision.zzjk.zzg();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final int zza(T r34, byte[] r35, int r36, int r37, int r38, com.google.android.gms.internal.vision.zzhn r39) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 2004
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zza(java.lang.Object, byte[], int, int, int, com.google.android.gms.internal.vision.zzhn):int");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v12, types: [int] */
    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zza(T t, byte[] bArr, int i, int i2, zzhn zzhnVar) throws IOException {
        byte b;
        int i3;
        int i4;
        int i5;
        int i6;
        Unsafe unsafe;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        zzko<T> zzkoVar = this;
        T t2 = t;
        byte[] bArr2 = bArr;
        int i14 = i2;
        zzhn zzhnVar2 = zzhnVar;
        if (zzkoVar.zzj) {
            Unsafe unsafe2 = zzb;
            int i15 = -1;
            int i16 = i;
            int i17 = -1;
            int i18 = 0;
            int i19 = 0;
            int i20 = 1048575;
            while (i16 < i14) {
                int i21 = i16 + 1;
                byte b2 = bArr2[i16];
                if (b2 >= 0) {
                    b = b2;
                    i3 = i21;
                } else {
                    i3 = zzhl.zza(b2, bArr2, i21, zzhnVar2);
                    b = zzhnVar2.zza;
                }
                int i22 = b >>> 3;
                int i23 = b & 7;
                if (i22 > i17) {
                    i4 = zzkoVar.zza(i22, i18 / 3);
                } else {
                    i4 = zzkoVar.zzg(i22);
                }
                if (i4 == i15) {
                    i5 = i3;
                    i6 = i22;
                    unsafe = unsafe2;
                    i7 = 0;
                } else {
                    int[] iArr = zzkoVar.zzc;
                    int i24 = iArr[i4 + 1];
                    int i25 = (i24 & 267386880) >>> 20;
                    int i26 = i3;
                    long j = i24 & 1048575;
                    if (i25 <= 17) {
                        int i27 = iArr[i4 + 2];
                        int i28 = 1 << (i27 >>> 20);
                        int i29 = i27 & 1048575;
                        if (i29 == i20) {
                            i8 = i4;
                        } else {
                            if (i20 == 1048575) {
                                i8 = i4;
                            } else {
                                i8 = i4;
                                unsafe2.putInt(t2, i20, i19);
                            }
                            if (i29 != 1048575) {
                                i19 = unsafe2.getInt(t2, i29);
                            }
                            i20 = i29;
                        }
                        switch (i25) {
                            case 0:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 1) {
                                    zzma.zza(t2, j, zzhl.zzc(bArr2, i9));
                                    i16 = i9 + 8;
                                    i19 |= i28;
                                    i18 = i10;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 1:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 5) {
                                    zzma.zza((Object) t2, j, zzhl.zzd(bArr2, i9));
                                    i16 = i9 + 4;
                                    i19 |= i28;
                                    i18 = i10;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 2:
                            case 3:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 0) {
                                    int zzb2 = zzhl.zzb(bArr2, i9, zzhnVar2);
                                    unsafe2.putLong(t, j, zzhnVar2.zzb);
                                    i19 |= i28;
                                    i16 = zzb2;
                                    i18 = i10;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 4:
                            case 11:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 0) {
                                    i16 = zzhl.zza(bArr2, i9, zzhnVar2);
                                    unsafe2.putInt(t2, j, zzhnVar2.zza);
                                    i19 |= i28;
                                    i18 = i10;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 5:
                            case 14:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 1) {
                                    unsafe2.putLong(t, j, zzhl.zzb(bArr2, i9));
                                    i16 = i9 + 8;
                                    i19 |= i28;
                                    i18 = i10;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 6:
                            case 13:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 5) {
                                    unsafe2.putInt(t2, j, zzhl.zza(bArr2, i9));
                                    i16 = i9 + 4;
                                    i19 |= i28;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    i18 = i10;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 7:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 0) {
                                    int zzb3 = zzhl.zzb(bArr2, i9, zzhnVar2);
                                    zzma.zza(t2, j, zzhnVar2.zzb != 0);
                                    i19 |= i28;
                                    i16 = zzb3;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    i18 = i10;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 8:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 2) {
                                    if ((i24 & 536870912) == 0) {
                                        i16 = zzhl.zzc(bArr2, i9, zzhnVar2);
                                    } else {
                                        i16 = zzhl.zzd(bArr2, i9, zzhnVar2);
                                    }
                                    unsafe2.putObject(t2, j, zzhnVar2.zzc);
                                    i19 |= i28;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    i18 = i10;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 9:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 2) {
                                    i16 = zzhl.zza(zzkoVar.zza(i10), bArr2, i9, i2, zzhnVar2);
                                    Object object = unsafe2.getObject(t2, j);
                                    if (object == null) {
                                        unsafe2.putObject(t2, j, zzhnVar2.zzc);
                                    } else {
                                        unsafe2.putObject(t2, j, zzjf.zza(object, zzhnVar2.zzc));
                                    }
                                    i19 |= i28;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    i18 = i10;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 10:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 2) {
                                    i16 = zzhl.zze(bArr2, i9, zzhnVar2);
                                    unsafe2.putObject(t2, j, zzhnVar2.zzc);
                                    i19 |= i28;
                                    i18 = i10;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 12:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 0) {
                                    i16 = zzhl.zza(bArr2, i9, zzhnVar2);
                                    unsafe2.putInt(t2, j, zzhnVar2.zza);
                                    i19 |= i28;
                                    i18 = i10;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 15:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                if (i23 == 0) {
                                    i16 = zzhl.zza(bArr2, i9, zzhnVar2);
                                    unsafe2.putInt(t2, j, zzif.zze(zzhnVar2.zza));
                                    i19 |= i28;
                                    i18 = i10;
                                    i17 = i6;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                } else {
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                }
                            case 16:
                                if (i23 != 0) {
                                    i9 = i26;
                                    i10 = i8;
                                    i6 = i22;
                                    i5 = i9;
                                    unsafe = unsafe2;
                                    i7 = i10;
                                    break;
                                } else {
                                    int zzb4 = zzhl.zzb(bArr2, i26, zzhnVar2);
                                    unsafe2.putLong(t, j, zzif.zza(zzhnVar2.zzb));
                                    i19 |= i28;
                                    i16 = zzb4;
                                    i18 = i8;
                                    i17 = i22;
                                    i15 = -1;
                                    i14 = i2;
                                    break;
                                }
                            default:
                                i9 = i26;
                                i10 = i8;
                                i6 = i22;
                                i5 = i9;
                                unsafe = unsafe2;
                                i7 = i10;
                                break;
                        }
                    } else {
                        i6 = i22;
                        int i30 = i4;
                        if (i25 == 27) {
                            if (i23 != 2) {
                                i11 = i20;
                                i12 = i19;
                                unsafe = unsafe2;
                                i13 = i26;
                                i7 = i30;
                                i5 = i13;
                                i20 = i11;
                                i19 = i12;
                            } else {
                                zzjl zzjlVar = (zzjl) unsafe2.getObject(t2, j);
                                if (!zzjlVar.zza()) {
                                    int size = zzjlVar.size();
                                    zzjlVar = zzjlVar.zza(size == 0 ? 10 : size << 1);
                                    unsafe2.putObject(t2, j, zzjlVar);
                                }
                                i16 = zzhl.zza(zzkoVar.zza(i30), b, bArr, i26, i2, zzjlVar, zzhnVar);
                                i19 = i19;
                                i18 = i30;
                                i17 = i6;
                                i15 = -1;
                                i14 = i2;
                            }
                        } else if (i25 <= 49) {
                            int i31 = i19;
                            int i32 = i20;
                            unsafe = unsafe2;
                            i7 = i30;
                            i16 = zza((zzko<T>) t, bArr, i26, i2, b, i6, i23, i30, i24, i25, j, zzhnVar);
                            if (i16 != i26) {
                                zzkoVar = this;
                                t2 = t;
                                bArr2 = bArr;
                                i14 = i2;
                                zzhnVar2 = zzhnVar;
                                i20 = i32;
                                i17 = i6;
                                i18 = i7;
                                i19 = i31;
                                unsafe2 = unsafe;
                                i15 = -1;
                            } else {
                                i5 = i16;
                                i20 = i32;
                                i19 = i31;
                            }
                        } else {
                            i12 = i19;
                            i11 = i20;
                            unsafe = unsafe2;
                            i13 = i26;
                            i7 = i30;
                            if (i25 == 50) {
                                if (i23 == 2) {
                                    i16 = zza((zzko<T>) t, bArr, i13, i2, i7, j, zzhnVar);
                                    if (i16 != i13) {
                                        zzkoVar = this;
                                        t2 = t;
                                        bArr2 = bArr;
                                        i14 = i2;
                                        zzhnVar2 = zzhnVar;
                                        i20 = i11;
                                        i17 = i6;
                                        i18 = i7;
                                        i19 = i12;
                                        unsafe2 = unsafe;
                                        i15 = -1;
                                    } else {
                                        i5 = i16;
                                        i20 = i11;
                                        i19 = i12;
                                    }
                                } else {
                                    i5 = i13;
                                    i20 = i11;
                                    i19 = i12;
                                }
                            } else {
                                i16 = zza((zzko<T>) t, bArr, i13, i2, b, i6, i23, i24, i25, j, i7, zzhnVar);
                                if (i16 == i13) {
                                    i5 = i16;
                                    i20 = i11;
                                    i19 = i12;
                                } else {
                                    zzkoVar = this;
                                    t2 = t;
                                    bArr2 = bArr;
                                    i14 = i2;
                                    zzhnVar2 = zzhnVar;
                                    i20 = i11;
                                    i17 = i6;
                                    i18 = i7;
                                    i19 = i12;
                                    unsafe2 = unsafe;
                                    i15 = -1;
                                }
                            }
                        }
                    }
                }
                i16 = zzhl.zza(b, bArr, i5, i2, zze(t), zzhnVar);
                zzkoVar = this;
                t2 = t;
                bArr2 = bArr;
                i14 = i2;
                zzhnVar2 = zzhnVar;
                i17 = i6;
                i18 = i7;
                unsafe2 = unsafe;
                i15 = -1;
            }
            int i33 = i19;
            Unsafe unsafe3 = unsafe2;
            if (i20 != 1048575) {
                unsafe3.putInt(t, i20, i33);
            }
            if (i16 != i2) {
                throw zzjk.zzg();
            }
            return;
        }
        zza((zzko<T>) t, bArr, i, i2, 0, zzhnVar);
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zzc(T t) {
        int i;
        int i2 = this.zzm;
        while (true) {
            i = this.zzn;
            if (i2 >= i) {
                break;
            }
            long zzd = zzd(this.zzl[i2]) & 1048575;
            Object zzf = zzma.zzf(t, zzd);
            if (zzf != null) {
                zzma.zza(t, zzd, this.zzs.zze(zzf));
            }
            i2++;
        }
        int length = this.zzl.length;
        while (i < length) {
            this.zzp.zzb(t, this.zzl[i]);
            i++;
        }
        this.zzq.zzd(t);
        if (this.zzh) {
            this.zzr.zzc(t);
        }
    }

    private final <UT, UB> UB zza(Object obj, int i, UB ub, zzlu<UT, UB> zzluVar) {
        int i2 = this.zzc[i];
        Object zzf = zzma.zzf(obj, zzd(i) & 1048575);
        if (zzf == null) {
            return ub;
        }
        zzjg zzc = zzc(i);
        if (zzc == null) {
            return ub;
        }
        return (UB) zza(i, i2, this.zzs.zza(zzf), zzc, (zzjg) ub, (zzlu<UT, zzjg>) zzluVar);
    }

    private final <K, V, UT, UB> UB zza(int i, int i2, Map<K, V> map, zzjg zzjgVar, UB ub, zzlu<UT, UB> zzluVar) {
        zzkf<?, ?> zzb2 = this.zzs.zzb(zzb(i));
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> next = it.next();
            if (!zzjgVar.zza(((Integer) next.getValue()).intValue())) {
                if (ub == null) {
                    ub = zzluVar.zza();
                }
                zzib zzc = zzht.zzc(zzkc.zza(zzb2, next.getKey(), next.getValue()));
                try {
                    zzkc.zza(zzc.zzb(), zzb2, next.getKey(), next.getValue());
                    zzluVar.zza((zzlu<UT, UB>) ub, i2, zzc.zza());
                    it.remove();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ub;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v16, types: [com.google.android.gms.internal.vision.zzlc] */
    /* JADX WARN: Type inference failed for: r1v22 */
    /* JADX WARN: Type inference failed for: r1v5, types: [com.google.android.gms.internal.vision.zzlc] */
    @Override // com.google.android.gms.internal.vision.zzlc
    public final boolean zzd(T t) {
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
            int zzd = zzd(i6);
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
            if (((268435456 & zzd) != 0) && !zza((zzko<T>) t, i6, i2, i, i10)) {
                return false;
            }
            switch ((267386880 & zzd) >>> 20) {
                case 9:
                case 17:
                    if (zza((zzko<T>) t, i6, i2, i, i10) && !zza(t, zzd, zza(i6))) {
                        return false;
                    }
                    break;
                case 27:
                case 49:
                    List list = (List) zzma.zzf(t, zzd & 1048575);
                    if (!list.isEmpty()) {
                        ?? zza2 = zza(i6);
                        int i11 = 0;
                        while (true) {
                            if (i11 < list.size()) {
                                if (!zza2.zzd(list.get(i11))) {
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
                    Map<?, ?> zzc = this.zzs.zzc(zzma.zzf(t, zzd & 1048575));
                    if (!zzc.isEmpty()) {
                        if (this.zzs.zzb(zzb(i6)).zzc.zza() == zzmo.MESSAGE) {
                            zzlc<T> zzlcVar = 0;
                            Iterator<?> it = zzc.values().iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    Object next = it.next();
                                    if (zzlcVar == null) {
                                        zzlcVar = zzky.zza().zza((Class) next.getClass());
                                    }
                                    boolean zzd2 = zzlcVar.zzd(next);
                                    zzlcVar = zzlcVar;
                                    if (!zzd2) {
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
                    if (zza((zzko<T>) t, i7, i6) && !zza(t, zzd, zza(i6))) {
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
    private static boolean zza(Object obj, int i, zzlc zzlcVar) {
        return zzlcVar.zzd(zzma.zzf(obj, i & 1048575));
    }

    private static void zza(int i, Object obj, zzmr zzmrVar) throws IOException {
        if (obj instanceof String) {
            zzmrVar.zza(i, (String) obj);
        } else {
            zzmrVar.zza(i, (zzht) obj);
        }
    }

    private final void zza(Object obj, int i, zzld zzldVar) throws IOException {
        if (zzf(i)) {
            zzma.zza(obj, i & 1048575, zzldVar.zzm());
        } else if (this.zzi) {
            zzma.zza(obj, i & 1048575, zzldVar.zzl());
        } else {
            zzma.zza(obj, i & 1048575, zzldVar.zzn());
        }
    }

    private final int zzd(int i) {
        return this.zzc[i + 1];
    }

    private final int zze(int i) {
        return this.zzc[i + 2];
    }

    private static boolean zzf(int i) {
        return (i & 536870912) != 0;
    }

    private static <T> double zzb(T t, long j) {
        return ((Double) zzma.zzf(t, j)).doubleValue();
    }

    private static <T> float zzc(T t, long j) {
        return ((Float) zzma.zzf(t, j)).floatValue();
    }

    private static <T> int zzd(T t, long j) {
        return ((Integer) zzma.zzf(t, j)).intValue();
    }

    private static <T> long zze(T t, long j) {
        return ((Long) zzma.zzf(t, j)).longValue();
    }

    private static <T> boolean zzf(T t, long j) {
        return ((Boolean) zzma.zzf(t, j)).booleanValue();
    }

    private final boolean zzc(T t, T t2, int i) {
        return zza((zzko<T>) t, i) == zza((zzko<T>) t2, i);
    }

    private final boolean zza(T t, int i, int i2, int i3, int i4) {
        if (i2 == 1048575) {
            return zza((zzko<T>) t, i);
        }
        return (i3 & i4) != 0;
    }

    private final boolean zza(T t, int i) {
        int zze = zze(i);
        long j = zze & 1048575;
        if (j != 1048575) {
            return (zzma.zza(t, j) & (1 << (zze >>> 20))) != 0;
        }
        int zzd = zzd(i);
        long j2 = zzd & 1048575;
        switch ((zzd & 267386880) >>> 20) {
            case 0:
                return zzma.zze(t, j2) != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            case 1:
                return zzma.zzd(t, j2) != 0.0f;
            case 2:
                return zzma.zzb(t, j2) != 0;
            case 3:
                return zzma.zzb(t, j2) != 0;
            case 4:
                return zzma.zza(t, j2) != 0;
            case 5:
                return zzma.zzb(t, j2) != 0;
            case 6:
                return zzma.zza(t, j2) != 0;
            case 7:
                return zzma.zzc(t, j2);
            case 8:
                Object zzf = zzma.zzf(t, j2);
                if (zzf instanceof String) {
                    return !((String) zzf).isEmpty();
                } else if (!(zzf instanceof zzht)) {
                    throw new IllegalArgumentException();
                } else {
                    return !zzht.zza.equals(zzf);
                }
            case 9:
                return zzma.zzf(t, j2) != null;
            case 10:
                return !zzht.zza.equals(zzma.zzf(t, j2));
            case 11:
                return zzma.zza(t, j2) != 0;
            case 12:
                return zzma.zza(t, j2) != 0;
            case 13:
                return zzma.zza(t, j2) != 0;
            case 14:
                return zzma.zzb(t, j2) != 0;
            case 15:
                return zzma.zza(t, j2) != 0;
            case 16:
                return zzma.zzb(t, j2) != 0;
            case 17:
                return zzma.zzf(t, j2) != null;
            default:
                throw new IllegalArgumentException();
        }
    }

    private final void zzb(T t, int i) {
        int zze = zze(i);
        long j = 1048575 & zze;
        if (j == 1048575) {
            return;
        }
        zzma.zza((Object) t, j, (1 << (zze >>> 20)) | zzma.zza(t, j));
    }

    private final boolean zza(T t, int i, int i2) {
        return zzma.zza(t, (long) (zze(i2) & 1048575)) == i;
    }

    private final void zzb(T t, int i, int i2) {
        zzma.zza((Object) t, zze(i2) & 1048575, i);
    }

    private final int zzg(int i) {
        if (i >= this.zze && i <= this.zzf) {
            return zzb(i, 0);
        }
        return -1;
    }

    private final int zza(int i, int i2) {
        if (i >= this.zze && i <= this.zzf) {
            return zzb(i, i2);
        }
        return -1;
    }

    private final int zzb(int i, int i2) {
        int length = (this.zzc.length / 3) - 1;
        while (i2 <= length) {
            int i3 = (length + i2) >>> 1;
            int i4 = i3 * 3;
            int i5 = this.zzc[i4];
            if (i == i5) {
                return i4;
            }
            if (i < i5) {
                length = i3 - 1;
            } else {
                i2 = i3 + 1;
            }
        }
        return -1;
    }
}
