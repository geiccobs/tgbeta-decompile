package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzjb;
import com.huawei.hms.adapter.internal.AvailableCode;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import sun.misc.Unsafe;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzko<T> implements zzlc<T> {
    private static final int[] zza = new int[0];
    private static final Unsafe zzb = zzma.zzc();
    private final int[] zzc;
    private final Object[] zzd;
    private final int zze;
    private final int zzf;
    private final zzkk zzg;
    private final boolean zzh;
    private final boolean zzj;
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
        boolean z3 = zzkkVar instanceof zzjb;
        this.zzj = z;
        this.zzh = zziqVar != null && zziqVar.zza(zzkkVar);
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

    /* JADX WARN: Removed duplicated region for block: B:159:0x033a  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x039c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static <T> com.google.android.gms.internal.vision.zzko<T> zza(java.lang.Class<T> r33, com.google.android.gms.internal.vision.zzki r34, com.google.android.gms.internal.vision.zzks r35, com.google.android.gms.internal.vision.zzju r36, com.google.android.gms.internal.vision.zzlu<?, ?> r37, com.google.android.gms.internal.vision.zziq<?> r38, com.google.android.gms.internal.vision.zzkh r39) {
        /*
            Method dump skipped, instructions count: 1054
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zza(java.lang.Class, com.google.android.gms.internal.vision.zzki, com.google.android.gms.internal.vision.zzks, com.google.android.gms.internal.vision.zzju, com.google.android.gms.internal.vision.zzlu, com.google.android.gms.internal.vision.zziq, com.google.android.gms.internal.vision.zzkh):com.google.android.gms.internal.vision.zzko");
    }

    private static Field zza(Class<?> cls, String str) {
        try {
            return cls.getDeclaredField(str);
        } catch (NoSuchFieldException unused) {
            Field[] declaredFields = cls.getDeclaredFields();
            for (Field field : declaredFields) {
                if (str.equals(field.getName())) {
                    return field;
                }
            }
            String name = cls.getName();
            String arrays = Arrays.toString(declaredFields);
            StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 40 + name.length() + String.valueOf(arrays).length());
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

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0038, code lost:
        if (com.google.android.gms.internal.vision.zzle.zza(com.google.android.gms.internal.vision.zzma.zzf(r10, r6), com.google.android.gms.internal.vision.zzma.zzf(r11, r6)) != false) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x006a, code lost:
        if (com.google.android.gms.internal.vision.zzle.zza(com.google.android.gms.internal.vision.zzma.zzf(r10, r6), com.google.android.gms.internal.vision.zzma.zzf(r11, r6)) != false) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x007e, code lost:
        if (com.google.android.gms.internal.vision.zzma.zzb(r10, r6) == com.google.android.gms.internal.vision.zzma.zzb(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0090, code lost:
        if (com.google.android.gms.internal.vision.zzma.zza(r10, r6) == com.google.android.gms.internal.vision.zzma.zza(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00a4, code lost:
        if (com.google.android.gms.internal.vision.zzma.zzb(r10, r6) == com.google.android.gms.internal.vision.zzma.zzb(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00b6, code lost:
        if (com.google.android.gms.internal.vision.zzma.zza(r10, r6) == com.google.android.gms.internal.vision.zzma.zza(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00c8, code lost:
        if (com.google.android.gms.internal.vision.zzma.zza(r10, r6) == com.google.android.gms.internal.vision.zzma.zza(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00da, code lost:
        if (com.google.android.gms.internal.vision.zzma.zza(r10, r6) == com.google.android.gms.internal.vision.zzma.zza(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00f0, code lost:
        if (com.google.android.gms.internal.vision.zzle.zza(com.google.android.gms.internal.vision.zzma.zzf(r10, r6), com.google.android.gms.internal.vision.zzma.zzf(r11, r6)) != false) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0106, code lost:
        if (com.google.android.gms.internal.vision.zzle.zza(com.google.android.gms.internal.vision.zzma.zzf(r10, r6), com.google.android.gms.internal.vision.zzma.zzf(r11, r6)) != false) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x011c, code lost:
        if (com.google.android.gms.internal.vision.zzle.zza(com.google.android.gms.internal.vision.zzma.zzf(r10, r6), com.google.android.gms.internal.vision.zzma.zzf(r11, r6)) != false) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x012e, code lost:
        if (com.google.android.gms.internal.vision.zzma.zzc(r10, r6) == com.google.android.gms.internal.vision.zzma.zzc(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0140, code lost:
        if (com.google.android.gms.internal.vision.zzma.zza(r10, r6) == com.google.android.gms.internal.vision.zzma.zza(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0154, code lost:
        if (com.google.android.gms.internal.vision.zzma.zzb(r10, r6) == com.google.android.gms.internal.vision.zzma.zzb(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x0165, code lost:
        if (com.google.android.gms.internal.vision.zzma.zza(r10, r6) == com.google.android.gms.internal.vision.zzma.zza(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0178, code lost:
        if (com.google.android.gms.internal.vision.zzma.zzb(r10, r6) == com.google.android.gms.internal.vision.zzma.zzb(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x018b, code lost:
        if (com.google.android.gms.internal.vision.zzma.zzb(r10, r6) == com.google.android.gms.internal.vision.zzma.zzb(r11, r6)) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x01a4, code lost:
        if (java.lang.Float.floatToIntBits(com.google.android.gms.internal.vision.zzma.zzd(r10, r6)) == java.lang.Float.floatToIntBits(com.google.android.gms.internal.vision.zzma.zzd(r11, r6))) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x01bf, code lost:
        if (java.lang.Double.doubleToLongBits(com.google.android.gms.internal.vision.zzma.zze(r10, r6)) == java.lang.Double.doubleToLongBits(com.google.android.gms.internal.vision.zzma.zze(r11, r6))) goto L86;
     */
    @Override // com.google.android.gms.internal.vision.zzlc
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean zza(T r10, T r11) {
        /*
            Method dump skipped, instructions count: 640
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zza(java.lang.Object, java.lang.Object):boolean");
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final int zza(T t) {
        int i;
        int i2;
        int length = this.zzc.length;
        int i3 = 0;
        for (int i4 = 0; i4 < length; i4 += 3) {
            int zzd = zzd(i4);
            int i5 = this.zzc[i4];
            long j = 1048575 & zzd;
            int i6 = 37;
            switch ((zzd & 267386880) >>> 20) {
                case 0:
                    i2 = i3 * 53;
                    i = zzjf.zza(Double.doubleToLongBits(zzma.zze(t, j)));
                    i3 = i2 + i;
                    break;
                case 1:
                    i2 = i3 * 53;
                    i = Float.floatToIntBits(zzma.zzd(t, j));
                    i3 = i2 + i;
                    break;
                case 2:
                    i2 = i3 * 53;
                    i = zzjf.zza(zzma.zzb(t, j));
                    i3 = i2 + i;
                    break;
                case 3:
                    i2 = i3 * 53;
                    i = zzjf.zza(zzma.zzb(t, j));
                    i3 = i2 + i;
                    break;
                case 4:
                    i2 = i3 * 53;
                    i = zzma.zza(t, j);
                    i3 = i2 + i;
                    break;
                case 5:
                    i2 = i3 * 53;
                    i = zzjf.zza(zzma.zzb(t, j));
                    i3 = i2 + i;
                    break;
                case 6:
                    i2 = i3 * 53;
                    i = zzma.zza(t, j);
                    i3 = i2 + i;
                    break;
                case 7:
                    i2 = i3 * 53;
                    i = zzjf.zza(zzma.zzc(t, j));
                    i3 = i2 + i;
                    break;
                case 8:
                    i2 = i3 * 53;
                    i = ((String) zzma.zzf(t, j)).hashCode();
                    i3 = i2 + i;
                    break;
                case 9:
                    Object zzf = zzma.zzf(t, j);
                    if (zzf != null) {
                        i6 = zzf.hashCode();
                    }
                    i3 = (i3 * 53) + i6;
                    break;
                case 10:
                    i2 = i3 * 53;
                    i = zzma.zzf(t, j).hashCode();
                    i3 = i2 + i;
                    break;
                case 11:
                    i2 = i3 * 53;
                    i = zzma.zza(t, j);
                    i3 = i2 + i;
                    break;
                case 12:
                    i2 = i3 * 53;
                    i = zzma.zza(t, j);
                    i3 = i2 + i;
                    break;
                case 13:
                    i2 = i3 * 53;
                    i = zzma.zza(t, j);
                    i3 = i2 + i;
                    break;
                case 14:
                    i2 = i3 * 53;
                    i = zzjf.zza(zzma.zzb(t, j));
                    i3 = i2 + i;
                    break;
                case 15:
                    i2 = i3 * 53;
                    i = zzma.zza(t, j);
                    i3 = i2 + i;
                    break;
                case 16:
                    i2 = i3 * 53;
                    i = zzjf.zza(zzma.zzb(t, j));
                    i3 = i2 + i;
                    break;
                case 17:
                    Object zzf2 = zzma.zzf(t, j);
                    if (zzf2 != null) {
                        i6 = zzf2.hashCode();
                    }
                    i3 = (i3 * 53) + i6;
                    break;
                case 18:
                case 19:
                case R.styleable.MapAttrs_uiZoomControls /* 20 */:
                case 21:
                case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
                case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
                case 24:
                case AvailableCode.ERROR_ON_ACTIVITY_RESULT /* 25 */:
                case AvailableCode.ERROR_NO_ACTIVITY /* 26 */:
                case AvailableCode.USER_IGNORE_PREVIOUS_POPUP /* 27 */:
                case AvailableCode.APP_IS_BACKGROUND_OR_LOCKED /* 28 */:
                case AvailableCode.HMS_IS_SPOOF /* 29 */:
                case AvailableCode.USER_ALREADY_KNOWS_SERVICE_UNAVAILABLE /* 30 */:
                case AvailableCode.CURRENT_SHOWING_SERVICE_UNAVAILABLE /* 31 */:
                case ConnectionsManager.RequestFlagForceDownload /* 32 */:
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
                    i2 = i3 * 53;
                    i = zzma.zzf(t, j).hashCode();
                    i3 = i2 + i;
                    break;
                case 50:
                    i2 = i3 * 53;
                    i = zzma.zzf(t, j).hashCode();
                    i3 = i2 + i;
                    break;
                case 51:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzjf.zza(Double.doubleToLongBits(zzb(t, j)));
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = Float.floatToIntBits(zzc(t, j));
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzjf.zza(zze(t, j));
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzjf.zza(zze(t, j));
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzd(t, j);
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 56:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzjf.zza(zze(t, j));
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 57:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzd(t, j);
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 58:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzjf.zza(zzf(t, j));
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = ((String) zzma.zzf(t, j)).hashCode();
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 60:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzma.zzf(t, j).hashCode();
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 61:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzma.zzf(t, j).hashCode();
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzd(t, j);
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 63:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzd(t, j);
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzd(t, j);
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzjf.zza(zze(t, j));
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzd(t, j);
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzjf.zza(zze(t, j));
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zza((zzko<T>) t, i5, i4)) {
                        i2 = i3 * 53;
                        i = zzma.zzf(t, j).hashCode();
                        i3 = i2 + i;
                        break;
                    } else {
                        break;
                    }
            }
        }
        int hashCode = (i3 * 53) + this.zzq.zzb(t).hashCode();
        return this.zzh ? (hashCode * 53) + this.zzr.zza(t).hashCode() : hashCode;
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zzb(T t, T t2) {
        t2.getClass();
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
                case R.styleable.MapAttrs_uiZoomControls /* 20 */:
                case 21:
                case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
                case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
                case 24:
                case AvailableCode.ERROR_ON_ACTIVITY_RESULT /* 25 */:
                case AvailableCode.ERROR_NO_ACTIVITY /* 26 */:
                case AvailableCode.USER_IGNORE_PREVIOUS_POPUP /* 27 */:
                case AvailableCode.APP_IS_BACKGROUND_OR_LOCKED /* 28 */:
                case AvailableCode.HMS_IS_SPOOF /* 29 */:
                case AvailableCode.USER_ALREADY_KNOWS_SERVICE_UNAVAILABLE /* 30 */:
                case AvailableCode.CURRENT_SHOWING_SERVICE_UNAVAILABLE /* 31 */:
                case ConnectionsManager.RequestFlagForceDownload /* 32 */:
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
                case 56:
                case 57:
                case 58:
                case 59:
                    if (zza((zzko<T>) t2, i2, i)) {
                        zzma.zza(t, j, zzma.zzf(t2, j));
                        zzb((zzko<T>) t, i2, i);
                        break;
                    } else {
                        break;
                    }
                case 60:
                    zzb(t, t2, i);
                    break;
                case 61:
                case 62:
                case 63:
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
        } else if (zzf2 == null) {
        } else {
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
        } else if (zzf == null) {
        } else {
            zzma.zza(t, j, zzf);
            zzb((zzko<T>) t, i2, i);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.android.gms.internal.vision.zzlc
    public final int zzb(T t) {
        int i;
        long j;
        int i2;
        int zzb2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int zzb3;
        int i8;
        int i9;
        int i10;
        int i11 = 267386880;
        int i12 = 1048575;
        int i13 = 1;
        if (this.zzj) {
            Unsafe unsafe = zzb;
            int i14 = 0;
            int i15 = 0;
            while (i14 < this.zzc.length) {
                int zzd = zzd(i14);
                int i16 = (zzd & i11) >>> 20;
                int i17 = this.zzc[i14];
                long j2 = zzd & 1048575;
                if (i16 >= zziv.DOUBLE_LIST_PACKED.zza() && i16 <= zziv.SINT64_LIST_PACKED.zza()) {
                    int i18 = this.zzc[i14 + 2];
                }
                switch (i16) {
                    case 0:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzb(i17, 0.0d);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 1:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzb(i17, 0.0f);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 2:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzd(i17, zzma.zzb(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 3:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zze(i17, zzma.zzb(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 4:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzf(i17, zzma.zza(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 5:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzg(i17, 0L);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 6:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzi(i17, 0);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 7:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzb(i17, true);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 8:
                        if (zza((zzko<T>) t, i14)) {
                            Object zzf = zzma.zzf(t, j2);
                            if (zzf instanceof zzht) {
                                zzb3 = zzii.zzc(i17, (zzht) zzf);
                                break;
                            } else {
                                zzb3 = zzii.zzb(i17, (String) zzf);
                                break;
                            }
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 9:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzle.zza(i17, zzma.zzf(t, j2), zza(i14));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 10:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzc(i17, (zzht) zzma.zzf(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 11:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzg(i17, zzma.zza(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 12:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzk(i17, zzma.zza(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 13:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzj(i17, 0);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 14:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzh(i17, 0L);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 15:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzh(i17, zzma.zza(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 16:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzf(i17, zzma.zzb(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 17:
                        if (zza((zzko<T>) t, i14)) {
                            zzb3 = zzii.zzc(i17, (zzkk) zzma.zzf(t, j2), zza(i14));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 18:
                        zzb3 = zzle.zzi(i17, zza(t, j2), false);
                        break;
                    case 19:
                        zzb3 = zzle.zzh(i17, zza(t, j2), false);
                        break;
                    case R.styleable.MapAttrs_uiZoomControls /* 20 */:
                        zzb3 = zzle.zza(i17, (List<Long>) zza(t, j2), false);
                        break;
                    case 21:
                        zzb3 = zzle.zzb(i17, (List<Long>) zza(t, j2), false);
                        break;
                    case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
                        zzb3 = zzle.zze(i17, zza(t, j2), false);
                        break;
                    case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
                        zzb3 = zzle.zzi(i17, zza(t, j2), false);
                        break;
                    case 24:
                        zzb3 = zzle.zzh(i17, zza(t, j2), false);
                        break;
                    case AvailableCode.ERROR_ON_ACTIVITY_RESULT /* 25 */:
                        zzb3 = zzle.zzj(i17, zza(t, j2), false);
                        break;
                    case AvailableCode.ERROR_NO_ACTIVITY /* 26 */:
                        zzb3 = zzle.zza(i17, zza(t, j2));
                        break;
                    case AvailableCode.USER_IGNORE_PREVIOUS_POPUP /* 27 */:
                        zzb3 = zzle.zza(i17, zza(t, j2), zza(i14));
                        break;
                    case AvailableCode.APP_IS_BACKGROUND_OR_LOCKED /* 28 */:
                        zzb3 = zzle.zzb(i17, zza(t, j2));
                        break;
                    case AvailableCode.HMS_IS_SPOOF /* 29 */:
                        zzb3 = zzle.zzf(i17, zza(t, j2), false);
                        break;
                    case AvailableCode.USER_ALREADY_KNOWS_SERVICE_UNAVAILABLE /* 30 */:
                        zzb3 = zzle.zzd(i17, zza(t, j2), false);
                        break;
                    case AvailableCode.CURRENT_SHOWING_SERVICE_UNAVAILABLE /* 31 */:
                        zzb3 = zzle.zzh(i17, zza(t, j2), false);
                        break;
                    case ConnectionsManager.RequestFlagForceDownload /* 32 */:
                        zzb3 = zzle.zzi(i17, zza(t, j2), false);
                        break;
                    case 33:
                        zzb3 = zzle.zzg(i17, zza(t, j2), false);
                        break;
                    case 34:
                        zzb3 = zzle.zzc(i17, zza(t, j2), false);
                        break;
                    case 35:
                        i9 = zzle.zzi((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 36:
                        i9 = zzle.zzh((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 37:
                        i9 = zzle.zza((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 38:
                        i9 = zzle.zzb((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 39:
                        i9 = zzle.zze((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 40:
                        i9 = zzle.zzi((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 41:
                        i9 = zzle.zzh((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 42:
                        i9 = zzle.zzj((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 43:
                        i9 = zzle.zzf((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 44:
                        i9 = zzle.zzd((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 45:
                        i9 = zzle.zzh((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 46:
                        i9 = zzle.zzi((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 47:
                        i9 = zzle.zzg((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 48:
                        i9 = zzle.zzc((List) unsafe.getObject(t, j2));
                        if (i9 > 0) {
                            i10 = zzii.zze(i17);
                            i8 = zzii.zzg(i9);
                            zzb3 = i10 + i8 + i9;
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 49:
                        zzb3 = zzle.zzb(i17, (List<zzkk>) zza(t, j2), zza(i14));
                        break;
                    case 50:
                        zzb3 = this.zzs.zza(i17, zzma.zzf(t, j2), zzb(i14));
                        break;
                    case 51:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzb(i17, 0.0d);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 52:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzb(i17, 0.0f);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 53:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzd(i17, zze(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 54:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zze(i17, zze(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 55:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzf(i17, zzd(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 56:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzg(i17, 0L);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 57:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzi(i17, 0);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 58:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzb(i17, true);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 59:
                        if (zza((zzko<T>) t, i17, i14)) {
                            Object zzf2 = zzma.zzf(t, j2);
                            if (zzf2 instanceof zzht) {
                                zzb3 = zzii.zzc(i17, (zzht) zzf2);
                                break;
                            } else {
                                zzb3 = zzii.zzb(i17, (String) zzf2);
                                break;
                            }
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 60:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzle.zza(i17, zzma.zzf(t, j2), zza(i14));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 61:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzc(i17, (zzht) zzma.zzf(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 62:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzg(i17, zzd(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 63:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzk(i17, zzd(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 64:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzj(i17, 0);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzh(i17, 0L);
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 66:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzh(i17, zzd(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 67:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzf(i17, zze(t, j2));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    case 68:
                        if (zza((zzko<T>) t, i17, i14)) {
                            zzb3 = zzii.zzc(i17, (zzkk) zzma.zzf(t, j2), zza(i14));
                            break;
                        } else {
                            continue;
                            i14 += 3;
                            i11 = 267386880;
                        }
                    default:
                        i14 += 3;
                        i11 = 267386880;
                }
                i15 += zzb3;
                i14 += 3;
                i11 = 267386880;
            }
            return i15 + zza((zzlu) this.zzq, (Object) t);
        }
        Unsafe unsafe2 = zzb;
        int i19 = 0;
        int i20 = 0;
        int i21 = 1048575;
        int i22 = 0;
        while (i19 < this.zzc.length) {
            int zzd2 = zzd(i19);
            int[] iArr = this.zzc;
            int i23 = iArr[i19];
            int i24 = (zzd2 & 267386880) >>> 20;
            if (i24 <= 17) {
                int i25 = iArr[i19 + 2];
                int i26 = i25 & i12;
                i = i13 << (i25 >>> 20);
                if (i26 != i21) {
                    i22 = unsafe2.getInt(t, i26);
                    i21 = i26;
                }
            } else {
                i = 0;
            }
            long j3 = zzd2 & i12;
            switch (i24) {
                case 0:
                    j = 0;
                    if ((i22 & i) != 0) {
                        i20 += zzii.zzb(i23, 0.0d);
                        continue;
                        i19 += 3;
                        i12 = 1048575;
                        i13 = 1;
                    }
                    break;
                case 1:
                    j = 0;
                    if ((i22 & i) != 0) {
                        i20 += zzii.zzb(i23, 0.0f);
                        break;
                    }
                    break;
                case 2:
                    j = 0;
                    if ((i & i22) != 0) {
                        i2 = zzii.zzd(i23, unsafe2.getLong(t, j3));
                        i20 += i2;
                        break;
                    }
                    break;
                case 3:
                    j = 0;
                    if ((i & i22) != 0) {
                        i2 = zzii.zze(i23, unsafe2.getLong(t, j3));
                        i20 += i2;
                        break;
                    }
                    break;
                case 4:
                    j = 0;
                    if ((i & i22) != 0) {
                        i2 = zzii.zzf(i23, unsafe2.getInt(t, j3));
                        i20 += i2;
                        break;
                    }
                    break;
                case 5:
                    j = 0;
                    if ((i22 & i) != 0) {
                        i2 = zzii.zzg(i23, 0L);
                        i20 += i2;
                        break;
                    }
                    break;
                case 6:
                    if ((i22 & i) != 0) {
                        i20 += zzii.zzi(i23, 0);
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 7:
                    if ((i22 & i) != 0) {
                        i20 += zzii.zzb(i23, true);
                        j = 0;
                        i19 += 3;
                        i12 = 1048575;
                        i13 = 1;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 8:
                    if ((i22 & i) != 0) {
                        Object object = unsafe2.getObject(t, j3);
                        if (object instanceof zzht) {
                            zzb2 = zzii.zzc(i23, (zzht) object);
                        } else {
                            zzb2 = zzii.zzb(i23, (String) object);
                        }
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 9:
                    if ((i22 & i) != 0) {
                        zzb2 = zzle.zza(i23, unsafe2.getObject(t, j3), zza(i19));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 10:
                    if ((i22 & i) != 0) {
                        zzb2 = zzii.zzc(i23, (zzht) unsafe2.getObject(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 11:
                    if ((i22 & i) != 0) {
                        zzb2 = zzii.zzg(i23, unsafe2.getInt(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 12:
                    if ((i22 & i) != 0) {
                        zzb2 = zzii.zzk(i23, unsafe2.getInt(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 13:
                    if ((i22 & i) != 0) {
                        i3 = zzii.zzj(i23, 0);
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 14:
                    if ((i22 & i) != 0) {
                        zzb2 = zzii.zzh(i23, 0L);
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 15:
                    if ((i22 & i) != 0) {
                        zzb2 = zzii.zzh(i23, unsafe2.getInt(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 16:
                    if ((i22 & i) != 0) {
                        zzb2 = zzii.zzf(i23, unsafe2.getLong(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 17:
                    if ((i22 & i) != 0) {
                        zzb2 = zzii.zzc(i23, (zzkk) unsafe2.getObject(t, j3), zza(i19));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 18:
                    zzb2 = zzle.zzi(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += zzb2;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 19:
                    i4 = zzle.zzh(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case R.styleable.MapAttrs_uiZoomControls /* 20 */:
                    i4 = zzle.zza(i23, (List<Long>) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 21:
                    i4 = zzle.zzb(i23, (List<Long>) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
                    i4 = zzle.zze(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
                    i4 = zzle.zzi(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 24:
                    i4 = zzle.zzh(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case AvailableCode.ERROR_ON_ACTIVITY_RESULT /* 25 */:
                    i4 = zzle.zzj(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case AvailableCode.ERROR_NO_ACTIVITY /* 26 */:
                    zzb2 = zzle.zza(i23, (List) unsafe2.getObject(t, j3));
                    i20 += zzb2;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case AvailableCode.USER_IGNORE_PREVIOUS_POPUP /* 27 */:
                    zzb2 = zzle.zza(i23, (List<?>) unsafe2.getObject(t, j3), zza(i19));
                    i20 += zzb2;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case AvailableCode.APP_IS_BACKGROUND_OR_LOCKED /* 28 */:
                    zzb2 = zzle.zzb(i23, (List) unsafe2.getObject(t, j3));
                    i20 += zzb2;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case AvailableCode.HMS_IS_SPOOF /* 29 */:
                    zzb2 = zzle.zzf(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += zzb2;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case AvailableCode.USER_ALREADY_KNOWS_SERVICE_UNAVAILABLE /* 30 */:
                    i4 = zzle.zzd(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case AvailableCode.CURRENT_SHOWING_SERVICE_UNAVAILABLE /* 31 */:
                    i4 = zzle.zzh(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case ConnectionsManager.RequestFlagForceDownload /* 32 */:
                    i4 = zzle.zzi(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 33:
                    i4 = zzle.zzg(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 34:
                    i4 = zzle.zzc(i23, (List) unsafe2.getObject(t, j3), false);
                    i20 += i4;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 35:
                    i7 = zzle.zzi((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 36:
                    i7 = zzle.zzh((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 37:
                    i7 = zzle.zza((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 38:
                    i7 = zzle.zzb((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 39:
                    i7 = zzle.zze((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 40:
                    i7 = zzle.zzi((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 41:
                    i7 = zzle.zzh((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 42:
                    i7 = zzle.zzj((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 43:
                    i7 = zzle.zzf((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 44:
                    i7 = zzle.zzd((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 45:
                    i7 = zzle.zzh((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 46:
                    i7 = zzle.zzi((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 47:
                    i7 = zzle.zzg((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 48:
                    i7 = zzle.zzc((List) unsafe2.getObject(t, j3));
                    if (i7 > 0) {
                        i6 = zzii.zze(i23);
                        i5 = zzii.zzg(i7);
                        i3 = i6 + i5 + i7;
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 49:
                    zzb2 = zzle.zzb(i23, (List) unsafe2.getObject(t, j3), zza(i19));
                    i20 += zzb2;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 50:
                    zzb2 = this.zzs.zza(i23, unsafe2.getObject(t, j3), zzb(i19));
                    i20 += zzb2;
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 51:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzb(i23, 0.0d);
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 52:
                    if (zza((zzko<T>) t, i23, i19)) {
                        i3 = zzii.zzb(i23, 0.0f);
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 53:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzd(i23, zze(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 54:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zze(i23, zze(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 55:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzf(i23, zzd(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 56:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzg(i23, 0L);
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 57:
                    if (zza((zzko<T>) t, i23, i19)) {
                        i3 = zzii.zzi(i23, 0);
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 58:
                    if (zza((zzko<T>) t, i23, i19)) {
                        i3 = zzii.zzb(i23, true);
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 59:
                    if (zza((zzko<T>) t, i23, i19)) {
                        Object object2 = unsafe2.getObject(t, j3);
                        if (object2 instanceof zzht) {
                            zzb2 = zzii.zzc(i23, (zzht) object2);
                        } else {
                            zzb2 = zzii.zzb(i23, (String) object2);
                        }
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 60:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzle.zza(i23, unsafe2.getObject(t, j3), zza(i19));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 61:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzc(i23, (zzht) unsafe2.getObject(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 62:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzg(i23, zzd(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 63:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzk(i23, zzd(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 64:
                    if (zza((zzko<T>) t, i23, i19)) {
                        i3 = zzii.zzj(i23, 0);
                        i20 += i3;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzh(i23, 0L);
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 66:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzh(i23, zzd(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 67:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzf(i23, zze(t, j3));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                case 68:
                    if (zza((zzko<T>) t, i23, i19)) {
                        zzb2 = zzii.zzc(i23, (zzkk) unsafe2.getObject(t, j3), zza(i19));
                        i20 += zzb2;
                    }
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
                default:
                    j = 0;
                    i19 += 3;
                    i12 = 1048575;
                    i13 = 1;
            }
            i19 += 3;
            i12 = 1048575;
            i13 = 1;
        }
        int i27 = 0;
        int zza2 = i20 + zza((zzlu) this.zzq, (Object) t);
        if (!this.zzh) {
            return zza2;
        }
        zziu<?> zza3 = this.zzr.zza(t);
        for (int i28 = 0; i28 < zza3.zza.zzc(); i28++) {
            Map.Entry<?, Object> zzb4 = zza3.zza.zzb(i28);
            i27 += zziu.zzc((zziw) zzb4.getKey(), zzb4.getValue());
        }
        for (Map.Entry<?, Object> entry : zza3.zza.zzd()) {
            i27 += zziu.zzc((zziw) entry.getKey(), entry.getValue());
        }
        return zza2 + i27;
    }

    private static <UT, UB> int zza(zzlu<UT, UB> zzluVar, T t) {
        return zzluVar.zzf(zzluVar.zzb(t));
    }

    private static List<?> zza(Object obj, long j) {
        return (List) zzma.zzf(obj, j);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003b  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x0513  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x0552  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x0a2a  */
    @Override // com.google.android.gms.internal.vision.zzlc
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void zza(T r14, com.google.android.gms.internal.vision.zzmr r15) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 2916
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zza(java.lang.Object, com.google.android.gms.internal.vision.zzmr):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0031  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x0491  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void zzb(T r18, com.google.android.gms.internal.vision.zzmr r19) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1338
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

    /* JADX WARN: Removed duplicated region for block: B:114:0x0236  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x016e  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x01e8  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:112:0x0233 -> B:113:0x0234). Please submit an issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:63:0x016b -> B:64:0x016c). Please submit an issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:92:0x01e5 -> B:93:0x01e6). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final int zza(T r16, byte[] r17, int r18, int r19, int r20, int r21, int r22, int r23, long r24, int r26, long r27, com.google.android.gms.internal.vision.zzhn r29) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1126
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zza(java.lang.Object, byte[], int, int, int, int, int, int, long, int, long, com.google.android.gms.internal.vision.zzhn):int");
    }

    private final <K, V> int zza(T t, byte[] bArr, int i, int i2, int i3, long j, zzhn zzhnVar) throws IOException {
        Unsafe unsafe = zzb;
        Object zzb2 = zzb(i3);
        Object object = unsafe.getObject(t, j);
        if (this.zzs.zzd(object)) {
            Object zzf = this.zzs.zzf(zzb2);
            this.zzs.zza(zzf, object);
            unsafe.putObject(t, j, zzf);
            object = zzf;
        }
        this.zzs.zzb(zzb2);
        this.zzs.zza(object);
        int zza2 = zzhl.zza(bArr, i, zzhnVar);
        int i4 = zzhnVar.zza;
        if (i4 < 0 || i4 > i2 - zza2) {
            throw zzjk.zza();
        }
        throw null;
    }

    private final int zza(T t, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, long j, int i8, zzhn zzhnVar) throws IOException {
        int i9;
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
            case 56:
            case VoIPService.CALL_MIN_LAYER /* 65 */:
                if (i5 == 1) {
                    unsafe.putObject(t, j, Long.valueOf(zzhl.zzb(bArr, i)));
                    i9 = i + 8;
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 57:
            case 64:
                if (i5 == 5) {
                    unsafe.putObject(t, j, Integer.valueOf(zzhl.zza(bArr, i)));
                    i9 = i + 4;
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 58:
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
            case 60:
                if (i5 == 2) {
                    int zza3 = zzhl.zza(zza(i8), bArr, i, i2, zzhnVar);
                    Object object = unsafe.getInt(t, j2) == i4 ? unsafe.getObject(t, j) : null;
                    if (object == null) {
                        unsafe.putObject(t, j, zzhnVar.zzc);
                    } else {
                        unsafe.putObject(t, j, zzjf.zza(object, zzhnVar.zzc));
                    }
                    unsafe.putInt(t, j2, i4);
                    return zza3;
                }
                return i;
            case 61:
                if (i5 == 2) {
                    i9 = zzhl.zze(bArr, i, zzhnVar);
                    unsafe.putObject(t, j, zzhnVar.zzc);
                    unsafe.putInt(t, j2, i4);
                    return i9;
                }
                return i;
            case 63:
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
                    Object object2 = unsafe.getInt(t, j2) == i4 ? unsafe.getObject(t, j) : null;
                    if (object2 == null) {
                        unsafe.putObject(t, j, zzhnVar.zzc);
                    } else {
                        unsafe.putObject(t, j, zzjf.zza(object2, zzhnVar.zzc));
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

    public final int zza(T t, byte[] bArr, int i, int i2, int i3, zzhn zzhnVar) throws IOException {
        Unsafe unsafe;
        int i4;
        int i5;
        T t2;
        zzko<T> zzkoVar;
        int i6;
        int i7;
        byte b;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        boolean z;
        int i14;
        int i15;
        T t3;
        byte[] bArr2;
        zzhn zzhnVar2;
        int i16;
        Object obj;
        Object zza2;
        long j;
        int i17;
        int i18;
        int i19;
        int i20;
        int i21;
        int i22;
        T t4;
        int i23;
        int i24;
        zzko<T> zzkoVar2 = this;
        T t5 = t;
        byte[] bArr3 = bArr;
        int i25 = i2;
        int i26 = i3;
        zzhn zzhnVar3 = zzhnVar;
        Unsafe unsafe2 = zzb;
        int i27 = i;
        int i28 = -1;
        int i29 = 0;
        int i30 = 0;
        int i31 = 0;
        int i32 = 1048575;
        while (true) {
            Object obj2 = null;
            if (i27 < i25) {
                int i33 = i27 + 1;
                byte b2 = bArr3[i27];
                if (b2 < 0) {
                    int zza3 = zzhl.zza(b2, bArr3, i33, zzhnVar3);
                    b = zzhnVar3.zza;
                    i33 = zza3;
                } else {
                    b = b2;
                }
                int i34 = b >>> 3;
                int i35 = b & 7;
                if (i34 > i28) {
                    i8 = zzkoVar2.zza(i34, i29 / 3);
                } else {
                    i8 = zzkoVar2.zzg(i34);
                }
                int i36 = i8;
                if (i36 == -1) {
                    i9 = i34;
                    i10 = i33;
                    i11 = b;
                    i12 = i31;
                    unsafe = unsafe2;
                    i13 = i26;
                    z = true;
                    i14 = 0;
                } else {
                    int[] iArr = zzkoVar2.zzc;
                    int i37 = iArr[i36 + 1];
                    int i38 = (i37 & 267386880) >>> 20;
                    int i39 = b;
                    long j2 = i37 & 1048575;
                    if (i38 <= 17) {
                        int i40 = iArr[i36 + 2];
                        int i41 = 1 << (i40 >>> 20);
                        int i42 = i40 & 1048575;
                        if (i42 != i32) {
                            if (i32 != 1048575) {
                                long j3 = i32;
                                t4 = t;
                                j = j2;
                                unsafe2.putInt(t4, j3, i31);
                            } else {
                                t4 = t;
                                j = j2;
                            }
                            i31 = unsafe2.getInt(t4, i42);
                            t5 = t4;
                        } else {
                            t5 = t;
                            j = j2;
                            i42 = i32;
                        }
                        int i43 = i31;
                        switch (i38) {
                            case 0:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                long j4 = j;
                                if (i35 == 1) {
                                    zzma.zza(t5, j4, zzhl.zzc(bArr3, i33));
                                    i27 = i33 + 8;
                                    i31 = i12 | i41;
                                    i32 = i17;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 1:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                long j5 = j;
                                if (i35 == 5) {
                                    zzma.zza((Object) t5, j5, zzhl.zzd(bArr3, i33));
                                    i27 = i33 + 4;
                                    i31 = i12 | i41;
                                    i32 = i17;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 2:
                            case 3:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                long j6 = j;
                                if (i35 == 0) {
                                    i21 = zzhl.zzb(bArr3, i33, zzhnVar3);
                                    unsafe2.putLong(t, j6, zzhnVar3.zzb);
                                    i31 = i12 | i41;
                                    i32 = i17;
                                    i27 = i21;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 4:
                            case 11:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                long j7 = j;
                                if (i35 == 0) {
                                    i27 = zzhl.zza(bArr3, i33, zzhnVar3);
                                    unsafe2.putInt(t5, j7, zzhnVar3.zza);
                                    i31 = i12 | i41;
                                    i32 = i17;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 5:
                            case 14:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                long j8 = j;
                                if (i35 == 1) {
                                    unsafe2.putLong(t, j8, zzhl.zzb(bArr3, i33));
                                    i27 = i33 + 8;
                                    i31 = i12 | i41;
                                    i32 = i17;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 6:
                            case 13:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                long j9 = j;
                                if (i35 == 5) {
                                    unsafe2.putInt(t5, j9, zzhl.zza(bArr3, i33));
                                    i27 = i33 + 4;
                                    i31 = i12 | i41;
                                    i32 = i17;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 7:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                long j10 = j;
                                if (i35 == 0) {
                                    i27 = zzhl.zzb(bArr3, i33, zzhnVar3);
                                    zzma.zza(t5, j10, zzhnVar3.zzb != 0);
                                    i31 = i12 | i41;
                                    i32 = i17;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 8:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                long j11 = j;
                                if (i35 == 2) {
                                    if ((536870912 & i37) == 0) {
                                        i27 = zzhl.zzc(bArr3, i33, zzhnVar3);
                                    } else {
                                        i27 = zzhl.zzd(bArr3, i33, zzhnVar3);
                                    }
                                    unsafe2.putObject(t5, j11, zzhnVar3.zzc);
                                    i31 = i12 | i41;
                                    i32 = i17;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 9:
                                i18 = i34;
                                i19 = i36;
                                i17 = i42;
                                i20 = i39;
                                long j12 = j;
                                if (i35 == 2) {
                                    int zza4 = zzhl.zza(zzkoVar2.zza(i19), bArr3, i33, i2, zzhnVar3);
                                    if ((i43 & i41) == 0) {
                                        unsafe2.putObject(t5, j12, zzhnVar3.zzc);
                                    } else {
                                        unsafe2.putObject(t5, j12, zzjf.zza(unsafe2.getObject(t5, j12), zzhnVar3.zzc));
                                    }
                                    int i44 = i43 | i41;
                                    i32 = i17;
                                    i30 = i20;
                                    i28 = i18;
                                    i25 = i2;
                                    i31 = i44;
                                    i27 = zza4;
                                    i29 = i19;
                                    i26 = i3;
                                    break;
                                } else {
                                    i12 = i43;
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 10:
                                i18 = i34;
                                i19 = i36;
                                i17 = i42;
                                i20 = i39;
                                long j13 = j;
                                if (i35 == 2) {
                                    i22 = zzhl.zze(bArr3, i33, zzhnVar3);
                                    unsafe2.putObject(t5, j13, zzhnVar3.zzc);
                                    i31 = i43 | i41;
                                    i32 = i17;
                                    i27 = i22;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i12 = i43;
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 12:
                                i18 = i34;
                                i19 = i36;
                                i17 = i42;
                                i20 = i39;
                                long j14 = j;
                                if (i35 != 0) {
                                    i12 = i43;
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                } else {
                                    i22 = zzhl.zza(bArr3, i33, zzhnVar3);
                                    int i45 = zzhnVar3.zza;
                                    zzjg zzc = zzkoVar2.zzc(i19);
                                    if (zzc == null || zzc.zza(i45)) {
                                        unsafe2.putInt(t5, j14, i45);
                                        i31 = i43 | i41;
                                        i32 = i17;
                                        i27 = i22;
                                        i30 = i20;
                                        i29 = i19;
                                        i28 = i18;
                                        i25 = i2;
                                        i26 = i3;
                                        break;
                                    } else {
                                        zze(t).zza(i20, Long.valueOf(i45));
                                        i27 = i22;
                                        i31 = i43;
                                        i30 = i20;
                                        i29 = i19;
                                        i28 = i18;
                                        i32 = i17;
                                        i25 = i2;
                                        i26 = i3;
                                    }
                                }
                                break;
                            case 15:
                                i18 = i34;
                                i19 = i36;
                                i17 = i42;
                                i20 = i39;
                                long j15 = j;
                                if (i35 == 0) {
                                    i22 = zzhl.zza(bArr3, i33, zzhnVar3);
                                    unsafe2.putInt(t5, j15, zzif.zze(zzhnVar3.zza));
                                    i31 = i43 | i41;
                                    i32 = i17;
                                    i27 = i22;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i12 = i43;
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 16:
                                i18 = i34;
                                i19 = i36;
                                long j16 = j;
                                if (i35 == 0) {
                                    i21 = zzhl.zzb(bArr3, i33, zzhnVar3);
                                    i17 = i42;
                                    i20 = i39;
                                    unsafe2.putLong(t, j16, zzif.zza(zzhnVar3.zzb));
                                    i31 = i43 | i41;
                                    i32 = i17;
                                    i27 = i21;
                                    i30 = i20;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i17 = i42;
                                    i20 = i39;
                                    i12 = i43;
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            case 17:
                                if (i35 == 3) {
                                    i18 = i34;
                                    i19 = i36;
                                    i27 = zzhl.zza(zzkoVar2.zza(i36), bArr, i33, i2, (i34 << 3) | 4, zzhnVar);
                                    if ((i43 & i41) == 0) {
                                        unsafe2.putObject(t5, j, zzhnVar3.zzc);
                                    } else {
                                        long j17 = j;
                                        unsafe2.putObject(t5, j17, zzjf.zza(unsafe2.getObject(t5, j17), zzhnVar3.zzc));
                                    }
                                    i31 = i43 | i41;
                                    i30 = i39;
                                    i32 = i42;
                                    i29 = i19;
                                    i28 = i18;
                                    i25 = i2;
                                    i26 = i3;
                                    break;
                                } else {
                                    i18 = i34;
                                    i19 = i36;
                                    i12 = i43;
                                    i17 = i42;
                                    i20 = i39;
                                    i32 = i17;
                                    i13 = i3;
                                    i10 = i33;
                                    i11 = i20;
                                    unsafe = unsafe2;
                                    i14 = i19;
                                    i9 = i18;
                                    z = true;
                                    break;
                                }
                            default:
                                i18 = i34;
                                i19 = i36;
                                i12 = i43;
                                i17 = i42;
                                i20 = i39;
                                i32 = i17;
                                i13 = i3;
                                i10 = i33;
                                i11 = i20;
                                unsafe = unsafe2;
                                i14 = i19;
                                i9 = i18;
                                z = true;
                                break;
                        }
                    } else {
                        i12 = i31;
                        int i46 = i32;
                        t5 = t;
                        if (i38 != 27) {
                            i14 = i36;
                            if (i38 <= 49) {
                                int i47 = i33;
                                i24 = i39;
                                z = true;
                                unsafe = unsafe2;
                                i13 = i3;
                                i9 = i34;
                                i27 = zza((zzko<T>) t, bArr, i33, i2, i39, i34, i35, i14, i37, i38, j2, zzhnVar);
                                if (i27 == i47) {
                                    i10 = i27;
                                } else {
                                    t5 = t;
                                    bArr3 = bArr;
                                    i25 = i2;
                                    zzhnVar3 = zzhnVar;
                                    i26 = i13;
                                    i30 = i24;
                                    i32 = i46;
                                    i31 = i12;
                                    i29 = i14;
                                    i28 = i9;
                                    unsafe2 = unsafe;
                                    zzkoVar2 = this;
                                }
                            } else {
                                i13 = i3;
                                i23 = i33;
                                i24 = i39;
                                unsafe = unsafe2;
                                i9 = i34;
                                z = true;
                                if (i38 != 50) {
                                    i27 = zza((zzko<T>) t, bArr, i23, i2, i24, i9, i35, i37, i38, j2, i14, zzhnVar);
                                    if (i27 != i23) {
                                        t5 = t;
                                        bArr3 = bArr;
                                        i25 = i2;
                                        zzhnVar3 = zzhnVar;
                                        i30 = i24;
                                        i26 = i13;
                                        i32 = i46;
                                        i31 = i12;
                                        i29 = i14;
                                        i28 = i9;
                                        unsafe2 = unsafe;
                                        zzkoVar2 = this;
                                    }
                                } else if (i35 == 2) {
                                    i27 = zza((zzko<T>) t, bArr, i23, i2, i14, j2, zzhnVar);
                                    if (i27 != i23) {
                                        t5 = t;
                                        bArr3 = bArr;
                                        i25 = i2;
                                        zzhnVar3 = zzhnVar;
                                        i26 = i13;
                                        i30 = i24;
                                        i32 = i46;
                                        i31 = i12;
                                        i29 = i14;
                                        i28 = i9;
                                        unsafe2 = unsafe;
                                        zzkoVar2 = this;
                                    }
                                } else {
                                    i10 = i23;
                                }
                                i10 = i27;
                            }
                        } else if (i35 == 2) {
                            zzjl zzjlVar = (zzjl) unsafe2.getObject(t5, j2);
                            if (!zzjlVar.zza()) {
                                int size = zzjlVar.size();
                                zzjlVar = zzjlVar.zza(size == 0 ? 10 : size << 1);
                                unsafe2.putObject(t5, j2, zzjlVar);
                            }
                            i27 = zzhl.zza(zzkoVar2.zza(i36), i39, bArr, i33, i2, zzjlVar, zzhnVar);
                            i26 = i3;
                            i30 = i39;
                            i28 = i34;
                            i32 = i46;
                            i31 = i12;
                            i29 = i36;
                            i25 = i2;
                        } else {
                            i14 = i36;
                            i13 = i3;
                            i23 = i33;
                            i24 = i39;
                            unsafe = unsafe2;
                            i9 = i34;
                            z = true;
                            i10 = i23;
                        }
                        i11 = i24;
                        i32 = i46;
                    }
                }
                if (i11 != i13 || i13 == 0) {
                    int i48 = i13;
                    if (this.zzh) {
                        zzhnVar2 = zzhnVar;
                        if (zzhnVar2.zzd != zzio.zzb()) {
                            int i49 = i9;
                            zzjb.zze zza5 = zzhnVar2.zzd.zza(this.zzg, i49);
                            if (zza5 == null) {
                                i27 = zzhl.zza(i11, bArr, i10, i2, zze(t), zzhnVar);
                                t3 = t;
                                i15 = i32;
                                i9 = i49;
                                bArr2 = bArr;
                                i16 = i2;
                            } else {
                                t3 = t;
                                zzjb.zzc zzcVar = (zzjb.zzc) t3;
                                zzcVar.zza();
                                zziu<zzjb.zzf> zziuVar = zzcVar.zzc;
                                zzjb.zzf zzfVar = zza5.zzd;
                                boolean z2 = zzfVar.zzd;
                                zzml zzmlVar = zzfVar.zzc;
                                if (zzmlVar == zzml.ENUM) {
                                    zzhl.zza(bArr, i10, zzhnVar2);
                                    throw null;
                                }
                                int[] iArr2 = zzhk.zza;
                                switch (iArr2[zzmlVar.ordinal()]) {
                                    case 1:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        obj2 = Double.valueOf(zzhl.zzc(bArr2, i10));
                                        i10 += 8;
                                        obj = obj2;
                                        break;
                                    case 2:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        obj2 = Float.valueOf(zzhl.zzd(bArr2, i10));
                                        i10 += 4;
                                        obj = obj2;
                                        break;
                                    case 3:
                                    case 4:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        i10 = zzhl.zzb(bArr2, i10, zzhnVar2);
                                        obj2 = Long.valueOf(zzhnVar2.zzb);
                                        obj = obj2;
                                        break;
                                    case 5:
                                    case 6:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        i10 = zzhl.zza(bArr2, i10, zzhnVar2);
                                        obj2 = Integer.valueOf(zzhnVar2.zza);
                                        obj = obj2;
                                        break;
                                    case 7:
                                    case 8:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        obj2 = Long.valueOf(zzhl.zzb(bArr2, i10));
                                        i10 += 8;
                                        obj = obj2;
                                        break;
                                    case 9:
                                    case 10:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        obj2 = Integer.valueOf(zzhl.zza(bArr2, i10));
                                        i10 += 4;
                                        obj = obj2;
                                        break;
                                    case 11:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        i10 = zzhl.zzb(bArr2, i10, zzhnVar2);
                                        if (zzhnVar2.zzb == 0) {
                                            z = false;
                                        }
                                        obj2 = Boolean.valueOf(z);
                                        obj = obj2;
                                        break;
                                    case 12:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        i10 = zzhl.zza(bArr2, i10, zzhnVar2);
                                        obj2 = Integer.valueOf(zzif.zze(zzhnVar2.zza));
                                        obj = obj2;
                                        break;
                                    case 13:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        i10 = zzhl.zzb(bArr2, i10, zzhnVar2);
                                        obj2 = Long.valueOf(zzif.zza(zzhnVar2.zzb));
                                        obj = obj2;
                                        break;
                                    case 14:
                                        throw new IllegalStateException("Shouldn't reach here.");
                                    case 15:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        i10 = zzhl.zze(bArr2, i10, zzhnVar2);
                                        obj = zzhnVar2.zzc;
                                        break;
                                    case 16:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        i10 = zzhl.zzc(bArr2, i10, zzhnVar2);
                                        obj = zzhnVar2.zzc;
                                        break;
                                    case 17:
                                        int i50 = (i49 << 3) | 4;
                                        i15 = i32;
                                        i16 = i2;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i10 = zzhl.zza(zzky.zza().zza((Class) zza5.zzc.getClass()), bArr, i10, i2, i50, zzhnVar);
                                        obj = zzhnVar2.zzc;
                                        break;
                                    case 18:
                                        i10 = zzhl.zza(zzky.zza().zza((Class) zza5.zzc.getClass()), bArr, i10, i2, zzhnVar2);
                                        obj = zzhnVar2.zzc;
                                        i15 = i32;
                                        i9 = i49;
                                        i16 = i2;
                                        bArr2 = bArr;
                                        break;
                                    default:
                                        i15 = i32;
                                        i9 = i49;
                                        bArr2 = bArr;
                                        i16 = i2;
                                        obj = obj2;
                                        break;
                                }
                                zzjb.zzf zzfVar2 = zza5.zzd;
                                if (zzfVar2.zzd) {
                                    zziuVar.zzb(zzfVar2, obj);
                                } else {
                                    int i51 = iArr2[zzfVar2.zzc.ordinal()];
                                    if ((i51 == 17 || i51 == 18) && (zza2 = zziuVar.zza((zziu<zzjb.zzf>) zza5.zzd)) != null) {
                                        obj = zzjf.zza(zza2, obj);
                                    }
                                    zziuVar.zza((zziu<zzjb.zzf>) zza5.zzd, obj);
                                }
                                i27 = i10;
                            }
                            i30 = i11;
                            zzkoVar2 = this;
                            bArr3 = bArr2;
                            t5 = t3;
                            i31 = i12;
                            i29 = i14;
                            i28 = i9;
                            i25 = i16;
                            i26 = i48;
                            zzhnVar3 = zzhnVar2;
                            unsafe2 = unsafe;
                            i32 = i15;
                        } else {
                            t3 = t;
                            bArr2 = bArr;
                        }
                    } else {
                        t3 = t;
                        bArr2 = bArr;
                        zzhnVar2 = zzhnVar;
                    }
                    i15 = i32;
                    i16 = i2;
                    i27 = zzhl.zza(i11, bArr, i10, i2, zze(t), zzhnVar);
                    i30 = i11;
                    zzkoVar2 = this;
                    bArr3 = bArr2;
                    t5 = t3;
                    i31 = i12;
                    i29 = i14;
                    i28 = i9;
                    i25 = i16;
                    i26 = i48;
                    zzhnVar3 = zzhnVar2;
                    unsafe2 = unsafe;
                    i32 = i15;
                } else {
                    zzkoVar = this;
                    t2 = t;
                    i27 = i10;
                    i6 = i32;
                    i30 = i11;
                    i4 = i13;
                    i31 = i12;
                    i7 = 1048575;
                    i5 = i2;
                }
            } else {
                int i52 = i32;
                unsafe = unsafe2;
                i4 = i26;
                i5 = i25;
                t2 = t5;
                zzkoVar = zzkoVar2;
                i6 = i52;
                i7 = 1048575;
            }
        }
        if (i6 != i7) {
            unsafe.putInt(t2, i6, i31);
        }
        zzlx zzlxVar = null;
        for (int i53 = zzkoVar.zzm; i53 < zzkoVar.zzn; i53++) {
            zzlxVar = (zzlx) zzkoVar.zza((Object) t2, zzkoVar.zzl[i53], (int) zzlxVar, (zzlu<UT, int>) zzkoVar.zzq);
        }
        if (zzlxVar != null) {
            zzkoVar.zzq.zzb((Object) t2, (T) zzlxVar);
        }
        if (i4 == 0) {
            if (i27 != i5) {
                throw zzjk.zzg();
            }
        } else if (i27 > i5 || i30 != i4) {
            throw zzjk.zzg();
        }
        return i27;
    }

    /* JADX WARN: Code restructure failed: missing block: B:102:0x02dc, code lost:
        if (r0 == r5) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x02e0, code lost:
        r15 = r30;
        r14 = r31;
        r12 = r32;
        r13 = r34;
        r11 = r35;
        r2 = r18;
        r1 = r25;
        r6 = r27;
        r7 = r28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x0323, code lost:
        if (r0 == r15) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x0346, code lost:
        if (r0 == r15) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x0348, code lost:
        r2 = r0;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v11, types: [int] */
    @Override // com.google.android.gms.internal.vision.zzlc
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void zza(T r31, byte[] r32, int r33, int r34, com.google.android.gms.internal.vision.zzhn r35) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 966
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzko.zza(java.lang.Object, byte[], int, int, com.google.android.gms.internal.vision.zzhn):void");
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
        zzjg zzc;
        int i2 = this.zzc[i];
        Object zzf = zzma.zzf(obj, zzd(i) & 1048575);
        return (zzf == null || (zzc = zzc(i)) == null) ? ub : (UB) zza(i, i2, this.zzs.zza(zzf), zzc, (zzjg) ub, (zzlu<UT, zzjg>) zzluVar);
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
            if (i9 != i3) {
                if (i9 != 1048575) {
                    i4 = zzb.getInt(t, i9);
                }
                i = i4;
                i2 = i9;
            } else {
                i2 = i3;
                i = i4;
            }
            if (((268435456 & zzd) != 0) && !zza((zzko<T>) t, i6, i2, i, i10)) {
                return false;
            }
            int i11 = (267386880 & zzd) >>> 20;
            if (i11 == 9 || i11 == 17) {
                if (zza((zzko<T>) t, i6, i2, i, i10) && !zza(t, zzd, zza(i6))) {
                    return false;
                }
            } else {
                if (i11 != 27) {
                    if (i11 == 60 || i11 == 68) {
                        if (zza((zzko<T>) t, i7, i6) && !zza(t, zzd, zza(i6))) {
                            return false;
                        }
                    } else if (i11 != 49) {
                        if (i11 == 50 && !this.zzs.zzc(zzma.zzf(t, zzd & 1048575)).isEmpty()) {
                            this.zzs.zzb(zzb(i6));
                            throw null;
                        }
                    }
                }
                List list = (List) zzma.zzf(t, zzd & 1048575);
                if (!list.isEmpty()) {
                    zzlc zza2 = zza(i6);
                    int i12 = 0;
                    while (true) {
                        if (i12 >= list.size()) {
                            break;
                        } else if (!zza2.zzd(list.get(i12))) {
                            z = false;
                            break;
                        } else {
                            i12++;
                        }
                    }
                }
                if (!z) {
                    return false;
                }
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

    private final int zzd(int i) {
        return this.zzc[i + 1];
    }

    private final int zze(int i) {
        return this.zzc[i + 2];
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
                return zzma.zze(t, j2) != 0.0d;
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
        if (i < this.zze || i > this.zzf) {
            return -1;
        }
        return zzb(i, 0);
    }

    private final int zza(int i, int i2) {
        if (i < this.zze || i > this.zzf) {
            return -1;
        }
        return zzb(i, i2);
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
