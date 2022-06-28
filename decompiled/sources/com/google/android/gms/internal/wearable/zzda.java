package com.google.android.gms.internal.wearable;

import androidx.core.text.HtmlCompat;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.CharacterCompat;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.UndoView;
import sun.misc.Unsafe;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzda<T> implements zzdi<T> {
    private static final int[] zza = new int[0];
    private static final Unsafe zzb = zzeg.zzq();
    private final int[] zzc;
    private final Object[] zzd;
    private final int zze;
    private final int zzf;
    private final zzcx zzg;
    private final boolean zzh;
    private final boolean zzi;
    private final int[] zzj;
    private final int zzk;
    private final int zzl;
    private final zzcl zzm;
    private final zzdw<?, ?> zzn;
    private final zzbh<?> zzo;
    private final zzdc zzp;
    private final zzcs zzq;

    /* JADX WARN: Multi-variable type inference failed */
    private zzda(int[] iArr, int[] iArr2, Object[] objArr, int i, int i2, zzcx zzcxVar, boolean z, boolean z2, int[] iArr3, int i3, int i4, zzdc zzdcVar, zzcl zzclVar, zzdw<?, ?> zzdwVar, zzbh<?> zzbhVar, zzcs zzcsVar) {
        this.zzc = iArr;
        this.zzd = iArr2;
        this.zze = objArr;
        this.zzf = i;
        this.zzi = zzcxVar;
        boolean z3 = false;
        if (zzdwVar != 0 && zzdwVar.zza(i2)) {
            z3 = true;
        }
        this.zzh = z3;
        this.zzj = z2;
        this.zzk = iArr3;
        this.zzl = i3;
        this.zzp = i4;
        this.zzm = zzdcVar;
        this.zzn = zzclVar;
        this.zzo = zzdwVar;
        this.zzg = i2;
        this.zzq = zzbhVar;
    }

    private final int zzA(int i) {
        return this.zzc[i + 1];
    }

    private final int zzB(int i) {
        return this.zzc[i + 2];
    }

    private static int zzC(int i) {
        return (i >>> 20) & 255;
    }

    private static <T> double zzD(T t, long j) {
        return ((Double) zzeg.zzn(t, j)).doubleValue();
    }

    private static <T> float zzE(T t, long j) {
        return ((Float) zzeg.zzn(t, j)).floatValue();
    }

    private static <T> int zzF(T t, long j) {
        return ((Integer) zzeg.zzn(t, j)).intValue();
    }

    private static <T> long zzG(T t, long j) {
        return ((Long) zzeg.zzn(t, j)).longValue();
    }

    private static <T> boolean zzH(T t, long j) {
        return ((Boolean) zzeg.zzn(t, j)).booleanValue();
    }

    private final boolean zzI(T t, T t2, int i) {
        return zzK(t, i) == zzK(t2, i);
    }

    private final boolean zzJ(T t, int i, int i2, int i3, int i4) {
        if (i2 == 1048575) {
            return zzK(t, i);
        }
        return (i3 & i4) != 0;
    }

    private final boolean zzK(T t, int i) {
        int zzB = zzB(i);
        long j = zzB & 1048575;
        if (j != 1048575) {
            return (zzeg.zzd(t, j) & (1 << (zzB >>> 20))) != 0;
        }
        int zzA = zzA(i);
        long j2 = zzA & 1048575;
        switch (zzC(zzA)) {
            case 0:
                return zzeg.zzl(t, j2) != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            case 1:
                return zzeg.zzj(t, j2) != 0.0f;
            case 2:
                return zzeg.zzf(t, j2) != 0;
            case 3:
                return zzeg.zzf(t, j2) != 0;
            case 4:
                return zzeg.zzd(t, j2) != 0;
            case 5:
                return zzeg.zzf(t, j2) != 0;
            case 6:
                return zzeg.zzd(t, j2) != 0;
            case 7:
                return zzeg.zzh(t, j2);
            case 8:
                Object zzn = zzeg.zzn(t, j2);
                if (zzn instanceof String) {
                    return !((String) zzn).isEmpty();
                } else if (!(zzn instanceof zzau)) {
                    throw new IllegalArgumentException();
                } else {
                    return !zzau.zzb.equals(zzn);
                }
            case 9:
                return zzeg.zzn(t, j2) != null;
            case 10:
                return !zzau.zzb.equals(zzeg.zzn(t, j2));
            case 11:
                return zzeg.zzd(t, j2) != 0;
            case 12:
                return zzeg.zzd(t, j2) != 0;
            case 13:
                return zzeg.zzd(t, j2) != 0;
            case 14:
                return zzeg.zzf(t, j2) != 0;
            case 15:
                return zzeg.zzd(t, j2) != 0;
            case 16:
                return zzeg.zzf(t, j2) != 0;
            case 17:
                return zzeg.zzn(t, j2) != null;
            default:
                throw new IllegalArgumentException();
        }
    }

    private final void zzL(T t, int i) {
        int zzB = zzB(i);
        long j = 1048575 & zzB;
        if (j == 1048575) {
            return;
        }
        zzeg.zze(t, j, (1 << (zzB >>> 20)) | zzeg.zzd(t, j));
    }

    private final boolean zzM(T t, int i, int i2) {
        return zzeg.zzd(t, (long) (zzB(i2) & 1048575)) == i;
    }

    private final void zzN(T t, int i, int i2) {
        zzeg.zze(t, zzB(i2) & 1048575, i);
    }

    private final int zzO(int i) {
        if (i < this.zze || i > this.zzf) {
            return -1;
        }
        return zzQ(i, 0);
    }

    private final int zzP(int i, int i2) {
        if (i < this.zze || i > this.zzf) {
            return -1;
        }
        return zzQ(i, i2);
    }

    private final int zzQ(int i, int i2) {
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

    private final void zzR(T t, zzbc zzbcVar) throws IOException {
        int i;
        if (this.zzh) {
            this.zzo.zzb(t);
            throw null;
        }
        int length = this.zzc.length;
        Unsafe unsafe = zzb;
        int i2 = 1048575;
        int i3 = 0;
        int i4 = 0;
        int i5 = 1048575;
        while (i3 < length) {
            int zzA = zzA(i3);
            int i6 = this.zzc[i3];
            int zzC = zzC(zzA);
            if (zzC <= 17) {
                int i7 = this.zzc[i3 + 2];
                int i8 = i7 & i2;
                if (i8 != i5) {
                    i4 = unsafe.getInt(t, i8);
                    i5 = i8;
                }
                i = 1 << (i7 >>> 20);
            } else {
                i = 0;
            }
            long j = zzA & i2;
            switch (zzC) {
                case 0:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzf(i6, zzeg.zzl(t, j));
                        break;
                    }
                case 1:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zze(i6, zzeg.zzj(t, j));
                        break;
                    }
                case 2:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzc(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 3:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzh(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 4:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzi(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 5:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzj(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 6:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzk(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 7:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzl(i6, zzeg.zzh(t, j));
                        break;
                    }
                case 8:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzT(i6, unsafe.getObject(t, j), zzbcVar);
                        break;
                    }
                case 9:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzr(i6, unsafe.getObject(t, j), zzv(i3));
                        break;
                    }
                case 10:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzn(i6, (zzau) unsafe.getObject(t, j));
                        break;
                    }
                case 11:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzo(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 12:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzg(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 13:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzb(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 14:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzd(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 15:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzp(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 16:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzq(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 17:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzbcVar.zzs(i6, unsafe.getObject(t, j), zzv(i3));
                        break;
                    }
                case 18:
                    zzdk.zzJ(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 19:
                    zzdk.zzK(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 20:
                    zzdk.zzL(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 21:
                    zzdk.zzM(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 22:
                    zzdk.zzQ(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 23:
                    zzdk.zzO(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 24:
                    zzdk.zzT(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 25:
                    zzdk.zzW(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 26:
                    zzdk.zzX(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar);
                    break;
                case 27:
                    zzdk.zzZ(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, zzv(i3));
                    break;
                case 28:
                    zzdk.zzY(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    zzdk.zzR(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 30:
                    zzdk.zzV(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 31:
                    zzdk.zzU(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 32:
                    zzdk.zzP(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 33:
                    zzdk.zzS(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 34:
                    zzdk.zzN(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, false);
                    break;
                case 35:
                    zzdk.zzJ(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 36:
                    zzdk.zzK(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 37:
                    zzdk.zzL(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 38:
                    zzdk.zzM(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 39:
                    zzdk.zzQ(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 40:
                    zzdk.zzO(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 41:
                    zzdk.zzT(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 42:
                    zzdk.zzW(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 43:
                    zzdk.zzR(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 44:
                    zzdk.zzV(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 45:
                    zzdk.zzU(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 46:
                    zzdk.zzP(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 47:
                    zzdk.zzS(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 48:
                    zzdk.zzN(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, true);
                    break;
                case 49:
                    zzdk.zzaa(this.zzc[i3], (List) unsafe.getObject(t, j), zzbcVar, zzv(i3));
                    break;
                case 50:
                    zzS(zzbcVar, i6, unsafe.getObject(t, j), i3);
                    break;
                case 51:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzf(i6, zzD(t, j));
                        break;
                    }
                case 52:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zze(i6, zzE(t, j));
                        break;
                    }
                case 53:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzc(i6, zzG(t, j));
                        break;
                    }
                case 54:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzh(i6, zzG(t, j));
                        break;
                    }
                case 55:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzi(i6, zzF(t, j));
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzj(i6, zzG(t, j));
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzk(i6, zzF(t, j));
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzl(i6, zzH(t, j));
                        break;
                    }
                case 59:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzT(i6, unsafe.getObject(t, j), zzbcVar);
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzr(i6, unsafe.getObject(t, j), zzv(i3));
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzn(i6, (zzau) unsafe.getObject(t, j));
                        break;
                    }
                case 62:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzo(i6, zzF(t, j));
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzg(i6, zzF(t, j));
                        break;
                    }
                case 64:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzb(i6, zzF(t, j));
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzd(i6, zzG(t, j));
                        break;
                    }
                case 66:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzp(i6, zzF(t, j));
                        break;
                    }
                case 67:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzq(i6, zzG(t, j));
                        break;
                    }
                case 68:
                    if (!zzM(t, i6, i3)) {
                        break;
                    } else {
                        zzbcVar.zzs(i6, unsafe.getObject(t, j), zzv(i3));
                        break;
                    }
            }
            i3 += 3;
            i2 = 1048575;
        }
        zzdw<?, ?> zzdwVar = this.zzn;
        zzdwVar.zzi(zzdwVar.zzd(t), zzbcVar);
    }

    private static final void zzT(int i, Object obj, zzbc zzbcVar) throws IOException {
        if (obj instanceof String) {
            zzbcVar.zzm(i, (String) obj);
        } else {
            zzbcVar.zzn(i, (zzau) obj);
        }
    }

    static zzdx zzf(Object obj) {
        zzbs zzbsVar = (zzbs) obj;
        zzdx zzdxVar = zzbsVar.zzc;
        if (zzdxVar == zzdx.zza()) {
            zzdx zzb2 = zzdx.zzb();
            zzbsVar.zzc = zzb2;
            return zzb2;
        }
        return zzdxVar;
    }

    public static <T> zzda<T> zzk(Class<T> cls, zzcu zzcuVar, zzdc zzdcVar, zzcl zzclVar, zzdw<?, ?> zzdwVar, zzbh<?> zzbhVar, zzcs zzcsVar) {
        if (zzcuVar instanceof zzdh) {
            return zzl((zzdh) zzcuVar, zzdcVar, zzclVar, zzdwVar, zzbhVar, zzcsVar);
        }
        zzdt zzdtVar = (zzdt) zzcuVar;
        throw null;
    }

    static <T> zzda<T> zzl(zzdh zzdhVar, zzdc zzdcVar, zzcl zzclVar, zzdw<?, ?> zzdwVar, zzbh<?> zzbhVar, zzcs zzcsVar) {
        int i;
        int i2;
        int i3;
        int[] iArr;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int[] iArr2;
        String str;
        Object[] objArr;
        int i13;
        int i14;
        int i15;
        Class<?> cls;
        int i16;
        int i17;
        int i18;
        Field field;
        char charAt;
        int i19;
        Field field2;
        Field field3;
        int i20;
        char charAt2;
        int i21;
        char charAt3;
        int i22;
        char charAt4;
        int i23;
        char charAt5;
        int i24;
        char charAt6;
        int i25;
        char charAt7;
        int i26;
        char charAt8;
        int i27;
        char charAt9;
        int i28;
        char charAt10;
        int i29;
        char charAt11;
        int i30;
        char charAt12;
        int i31;
        char charAt13;
        boolean z = zzdhVar.zzc() == 2;
        String zzd = zzdhVar.zzd();
        int length = zzd.length();
        char charAt14 = zzd.charAt(0);
        char c = CharacterCompat.MIN_HIGH_SURROGATE;
        if (charAt14 >= 55296) {
            int i32 = 1;
            while (true) {
                i = i32 + 1;
                if (zzd.charAt(i32) < 55296) {
                    break;
                }
                i32 = i;
            }
        } else {
            i = 1;
        }
        int i33 = i + 1;
        int charAt15 = zzd.charAt(i);
        if (charAt15 >= 55296) {
            int i34 = charAt15 & 8191;
            int i35 = 13;
            while (true) {
                i31 = i33 + 1;
                charAt13 = zzd.charAt(i33);
                if (charAt13 < 55296) {
                    break;
                }
                i34 |= (charAt13 & 8191) << i35;
                i35 += 13;
                i33 = i31;
            }
            charAt15 = i34 | (charAt13 << i35);
            i33 = i31;
        }
        if (charAt15 == 0) {
            iArr = zza;
            i8 = 0;
            i7 = 0;
            i6 = 0;
            i5 = 0;
            i4 = 0;
            i3 = 0;
            i2 = 0;
        } else {
            int i36 = i33 + 1;
            int charAt16 = zzd.charAt(i33);
            if (charAt16 >= 55296) {
                int i37 = charAt16 & 8191;
                int i38 = 13;
                while (true) {
                    i30 = i36 + 1;
                    charAt12 = zzd.charAt(i36);
                    if (charAt12 < 55296) {
                        break;
                    }
                    i37 |= (charAt12 & 8191) << i38;
                    i38 += 13;
                    i36 = i30;
                }
                charAt16 = i37 | (charAt12 << i38);
                i36 = i30;
            }
            int i39 = i36 + 1;
            int charAt17 = zzd.charAt(i36);
            if (charAt17 >= 55296) {
                int i40 = charAt17 & 8191;
                int i41 = 13;
                while (true) {
                    i29 = i39 + 1;
                    charAt11 = zzd.charAt(i39);
                    if (charAt11 < 55296) {
                        break;
                    }
                    i40 |= (charAt11 & 8191) << i41;
                    i41 += 13;
                    i39 = i29;
                }
                charAt17 = i40 | (charAt11 << i41);
                i39 = i29;
            }
            int i42 = i39 + 1;
            i7 = zzd.charAt(i39);
            if (i7 >= 55296) {
                int i43 = i7 & 8191;
                int i44 = 13;
                while (true) {
                    i28 = i42 + 1;
                    charAt10 = zzd.charAt(i42);
                    if (charAt10 < 55296) {
                        break;
                    }
                    i43 |= (charAt10 & 8191) << i44;
                    i44 += 13;
                    i42 = i28;
                }
                i7 = i43 | (charAt10 << i44);
                i42 = i28;
            }
            int i45 = i42 + 1;
            int charAt18 = zzd.charAt(i42);
            if (charAt18 >= 55296) {
                int i46 = charAt18 & 8191;
                int i47 = 13;
                while (true) {
                    i27 = i45 + 1;
                    charAt9 = zzd.charAt(i45);
                    if (charAt9 < 55296) {
                        break;
                    }
                    i46 |= (charAt9 & 8191) << i47;
                    i47 += 13;
                    i45 = i27;
                }
                charAt18 = i46 | (charAt9 << i47);
                i45 = i27;
            }
            int i48 = i45 + 1;
            i5 = zzd.charAt(i45);
            if (i5 >= 55296) {
                int i49 = i5 & 8191;
                int i50 = 13;
                while (true) {
                    i26 = i48 + 1;
                    charAt8 = zzd.charAt(i48);
                    if (charAt8 < 55296) {
                        break;
                    }
                    i49 |= (charAt8 & 8191) << i50;
                    i50 += 13;
                    i48 = i26;
                }
                i5 = i49 | (charAt8 << i50);
                i48 = i26;
            }
            int i51 = i48 + 1;
            int charAt19 = zzd.charAt(i48);
            if (charAt19 >= 55296) {
                int i52 = charAt19 & 8191;
                int i53 = 13;
                while (true) {
                    i25 = i51 + 1;
                    charAt7 = zzd.charAt(i51);
                    if (charAt7 < 55296) {
                        break;
                    }
                    i52 |= (charAt7 & 8191) << i53;
                    i53 += 13;
                    i51 = i25;
                }
                charAt19 = i52 | (charAt7 << i53);
                i51 = i25;
            }
            int i54 = i51 + 1;
            int charAt20 = zzd.charAt(i51);
            if (charAt20 >= 55296) {
                int i55 = charAt20 & 8191;
                int i56 = 13;
                while (true) {
                    i24 = i54 + 1;
                    charAt6 = zzd.charAt(i54);
                    if (charAt6 < 55296) {
                        break;
                    }
                    i55 |= (charAt6 & 8191) << i56;
                    i56 += 13;
                    i54 = i24;
                }
                charAt20 = i55 | (charAt6 << i56);
                i54 = i24;
            }
            int i57 = i54 + 1;
            i3 = zzd.charAt(i54);
            if (i3 >= 55296) {
                int i58 = i3 & 8191;
                int i59 = 13;
                while (true) {
                    i23 = i57 + 1;
                    charAt5 = zzd.charAt(i57);
                    if (charAt5 < 55296) {
                        break;
                    }
                    i58 |= (charAt5 & 8191) << i59;
                    i59 += 13;
                    i57 = i23;
                }
                i3 = i58 | (charAt5 << i59);
                i57 = i23;
            }
            iArr = new int[i3 + charAt19 + charAt20];
            i2 = charAt16 + charAt16 + charAt17;
            i8 = charAt16;
            i33 = i57;
            int i60 = charAt19;
            i4 = charAt18;
            i6 = i60;
        }
        Unsafe unsafe = zzb;
        Object[] zze = zzdhVar.zze();
        Class<?> cls2 = zzdhVar.zzb().getClass();
        int[] iArr3 = new int[i5 * 3];
        Object[] objArr2 = new Object[i5 + i5];
        int i61 = i3 + i6;
        int i62 = i3;
        int i63 = i61;
        int i64 = 0;
        int i65 = 0;
        while (i33 < length) {
            int i66 = i33 + 1;
            int charAt21 = zzd.charAt(i33);
            if (charAt21 >= c) {
                int i67 = charAt21 & 8191;
                int i68 = i66;
                int i69 = 13;
                while (true) {
                    i22 = i68 + 1;
                    charAt4 = zzd.charAt(i68);
                    if (charAt4 < c) {
                        break;
                    }
                    i67 |= (charAt4 & 8191) << i69;
                    i69 += 13;
                    i68 = i22;
                }
                charAt21 = i67 | (charAt4 << i69);
                i9 = i22;
            } else {
                i9 = i66;
            }
            int i70 = i9 + 1;
            int charAt22 = zzd.charAt(i9);
            if (charAt22 >= c) {
                int i71 = charAt22 & 8191;
                int i72 = i70;
                int i73 = 13;
                while (true) {
                    i21 = i72 + 1;
                    charAt3 = zzd.charAt(i72);
                    i10 = length;
                    if (charAt3 < 55296) {
                        break;
                    }
                    i71 |= (charAt3 & 8191) << i73;
                    i73 += 13;
                    i72 = i21;
                    length = i10;
                }
                charAt22 = i71 | (charAt3 << i73);
                i11 = i21;
            } else {
                i10 = length;
                i11 = i70;
            }
            int i74 = charAt22 & 255;
            int i75 = i3;
            if ((charAt22 & 1024) != 0) {
                iArr[i65] = i64;
                i65++;
            }
            if (i74 >= 51) {
                int i76 = i11 + 1;
                int charAt23 = zzd.charAt(i11);
                if (charAt23 >= 55296) {
                    int i77 = charAt23 & 8191;
                    int i78 = i76;
                    int i79 = 13;
                    while (true) {
                        i20 = i78 + 1;
                        charAt2 = zzd.charAt(i78);
                        i12 = i4;
                        if (charAt2 < 55296) {
                            break;
                        }
                        i77 |= (charAt2 & 8191) << i79;
                        i79 += 13;
                        i78 = i20;
                        i4 = i12;
                    }
                    charAt23 = i77 | (charAt2 << i79);
                    i19 = i20;
                } else {
                    i12 = i4;
                    i19 = i76;
                }
                int i80 = i74 - 51;
                i13 = i19;
                if (i80 == 9 || i80 == 17) {
                    int i81 = i64 / 3;
                    objArr2[i81 + i81 + 1] = zze[i2];
                    i2++;
                } else if (i80 == 12 && !z) {
                    int i82 = i64 / 3;
                    objArr2[i82 + i82 + 1] = zze[i2];
                    i2++;
                }
                int i83 = charAt23 + charAt23;
                Object obj = zze[i83];
                if (obj instanceof Field) {
                    field2 = (Field) obj;
                } else {
                    field2 = zzn(cls2, (String) obj);
                    zze[i83] = field2;
                }
                iArr2 = iArr3;
                i14 = i7;
                int objectFieldOffset = (int) unsafe.objectFieldOffset(field2);
                int i84 = i83 + 1;
                Object obj2 = zze[i84];
                if (obj2 instanceof Field) {
                    field3 = (Field) obj2;
                } else {
                    field3 = zzn(cls2, (String) obj2);
                    zze[i84] = field3;
                }
                int objectFieldOffset2 = (int) unsafe.objectFieldOffset(field3);
                str = zzd;
                cls = cls2;
                i17 = objectFieldOffset2;
                objArr = objArr2;
                i15 = objectFieldOffset;
                i16 = 0;
            } else {
                iArr2 = iArr3;
                i14 = i7;
                i12 = i4;
                int i85 = i2 + 1;
                Field zzn = zzn(cls2, (String) zze[i2]);
                if (i74 != 9 && i74 != 17) {
                    if (i74 == 27 || i74 == 49) {
                        int i86 = i64 / 3;
                        objArr2[i86 + i86 + 1] = zze[i85];
                        i85++;
                    } else if (i74 == 12 || i74 == 30 || i74 == 44) {
                        if (!z) {
                            int i87 = i64 / 3;
                            objArr2[i87 + i87 + 1] = zze[i85];
                            i85++;
                        }
                    } else if (i74 == 50) {
                        int i88 = i62 + 1;
                        iArr[i62] = i64;
                        int i89 = i64 / 3;
                        int i90 = i89 + i89;
                        int i91 = i85 + 1;
                        objArr2[i90] = zze[i85];
                        if ((charAt22 & 2048) != 0) {
                            i85 = i91 + 1;
                            objArr2[i90 + 1] = zze[i91];
                            i62 = i88;
                        } else {
                            i62 = i88;
                            i85 = i91;
                        }
                    }
                    i18 = i85;
                    i15 = (int) unsafe.objectFieldOffset(zzn);
                    objArr = objArr2;
                    if ((charAt22 & 4096) == 4096 || i74 > 17) {
                        str = zzd;
                        cls = cls2;
                        i13 = i11;
                        i17 = 1048575;
                        i16 = 0;
                    } else {
                        int i92 = i11 + 1;
                        int charAt24 = zzd.charAt(i11);
                        if (charAt24 >= 55296) {
                            int i93 = charAt24 & 8191;
                            int i94 = 13;
                            while (true) {
                                i13 = i92 + 1;
                                charAt = zzd.charAt(i92);
                                if (charAt < 55296) {
                                    break;
                                }
                                i93 |= (charAt & 8191) << i94;
                                i94 += 13;
                                i92 = i13;
                            }
                            charAt24 = i93 | (charAt << i94);
                        } else {
                            i13 = i92;
                        }
                        int i95 = i8 + i8 + (charAt24 / 32);
                        Object obj3 = zze[i95];
                        str = zzd;
                        if (obj3 instanceof Field) {
                            field = (Field) obj3;
                        } else {
                            field = zzn(cls2, (String) obj3);
                            zze[i95] = field;
                        }
                        cls = cls2;
                        i17 = (int) unsafe.objectFieldOffset(field);
                        i16 = charAt24 % 32;
                    }
                    if (i74 >= 18 || i74 > 49) {
                        i2 = i18;
                    } else {
                        iArr[i63] = i15;
                        i63++;
                        i2 = i18;
                    }
                }
                int i96 = i64 / 3;
                objArr2[i96 + i96 + 1] = zzn.getType();
                i18 = i85;
                i15 = (int) unsafe.objectFieldOffset(zzn);
                objArr = objArr2;
                if ((charAt22 & 4096) == 4096) {
                }
                str = zzd;
                cls = cls2;
                i13 = i11;
                i17 = 1048575;
                i16 = 0;
                if (i74 >= 18) {
                }
                i2 = i18;
            }
            int i97 = i64 + 1;
            iArr2[i64] = charAt21;
            int i98 = i97 + 1;
            iArr2[i97] = ((charAt22 & 256) != 0 ? 268435456 : 0) | ((charAt22 & 512) != 0 ? 536870912 : 0) | (i74 << 20) | i15;
            i64 = i98 + 1;
            iArr2[i98] = (i16 << 20) | i17;
            cls2 = cls;
            i7 = i14;
            i3 = i75;
            i33 = i13;
            length = i10;
            objArr2 = objArr;
            zzd = str;
            iArr3 = iArr2;
            i4 = i12;
            c = CharacterCompat.MIN_HIGH_SURROGATE;
        }
        return new zzda<>(iArr3, objArr2, i7, i4, zzdhVar.zzb(), z, false, iArr, i3, i61, zzdcVar, zzclVar, zzdwVar, zzbhVar, zzcsVar, null);
    }

    private static Field zzn(Class<?> cls, String str) {
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

    private final void zzo(T t, T t2, int i) {
        long zzA = zzA(i) & 1048575;
        if (!zzK(t2, i)) {
            return;
        }
        Object zzn = zzeg.zzn(t, zzA);
        Object zzn2 = zzeg.zzn(t2, zzA);
        if (zzn == null || zzn2 == null) {
            if (zzn2 == null) {
                return;
            }
            zzeg.zzo(t, zzA, zzn2);
            zzL(t, i);
            return;
        }
        zzeg.zzo(t, zzA, zzca.zzi(zzn, zzn2));
        zzL(t, i);
    }

    private final void zzp(T t, T t2, int i) {
        Object obj;
        int zzA = zzA(i);
        int i2 = this.zzc[i];
        long j = zzA & 1048575;
        if (!zzM(t2, i2, i)) {
            return;
        }
        if (zzM(t, i2, i)) {
            obj = zzeg.zzn(t, j);
        } else {
            obj = null;
        }
        Object zzn = zzeg.zzn(t2, j);
        if (obj == null || zzn == null) {
            if (zzn == null) {
                return;
            }
            zzeg.zzo(t, j, zzn);
            zzN(t, i2, i);
            return;
        }
        zzeg.zzo(t, j, zzca.zzi(obj, zzn));
        zzN(t, i2, i);
    }

    private final int zzq(T t) {
        int i;
        Unsafe unsafe = zzb;
        int i2 = 0;
        int i3 = 0;
        int i4 = 1048575;
        for (int i5 = 0; i5 < this.zzc.length; i5 += 3) {
            int zzA = zzA(i5);
            int i6 = this.zzc[i5];
            int zzC = zzC(zzA);
            if (zzC <= 17) {
                int i7 = this.zzc[i5 + 2];
                int i8 = i7 & 1048575;
                i = 1 << (i7 >>> 20);
                if (i8 != i4) {
                    i3 = unsafe.getInt(t, i8);
                    i4 = i8;
                }
            } else {
                i = 0;
            }
            long j = zzA & 1048575;
            switch (zzC) {
                case 0:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzx(unsafe.getLong(t, j));
                        break;
                    } else {
                        break;
                    }
                case 3:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzx(unsafe.getLong(t, j));
                        break;
                    } else {
                        break;
                    }
                case 4:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzv(unsafe.getInt(t, j));
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 6:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 7:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + 1;
                        break;
                    } else {
                        break;
                    }
                case 8:
                    if ((i3 & i) != 0) {
                        Object object = unsafe.getObject(t, j);
                        if (object instanceof zzau) {
                            int zzw = zzbb.zzw(i6 << 3);
                            int zzc = ((zzau) object).zzc();
                            i2 += zzw + zzbb.zzw(zzc) + zzc;
                            break;
                        } else {
                            i2 += zzbb.zzw(i6 << 3) + zzbb.zzy((String) object);
                            break;
                        }
                    } else {
                        break;
                    }
                case 9:
                    if ((i3 & i) != 0) {
                        i2 += zzdk.zzw(i6, unsafe.getObject(t, j), zzv(i5));
                        break;
                    } else {
                        break;
                    }
                case 10:
                    if ((i3 & i) != 0) {
                        int zzw2 = zzbb.zzw(i6 << 3);
                        int zzc2 = ((zzau) unsafe.getObject(t, j)).zzc();
                        i2 += zzw2 + zzbb.zzw(zzc2) + zzc2;
                        break;
                    } else {
                        break;
                    }
                case 11:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzw(unsafe.getInt(t, j));
                        break;
                    } else {
                        break;
                    }
                case 12:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzv(unsafe.getInt(t, j));
                        break;
                    } else {
                        break;
                    }
                case 13:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 14:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 15:
                    if ((i3 & i) != 0) {
                        int i9 = unsafe.getInt(t, j);
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzw((i9 >> 31) ^ (i9 + i9));
                        break;
                    } else {
                        break;
                    }
                case 16:
                    if ((i3 & i) != 0) {
                        long j2 = unsafe.getLong(t, j);
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzx((j2 >> 63) ^ (j2 + j2));
                        break;
                    } else {
                        break;
                    }
                case 17:
                    if ((i3 & i) != 0) {
                        i2 += zzbb.zzE(i6, (zzcx) unsafe.getObject(t, j), zzv(i5));
                        break;
                    } else {
                        break;
                    }
                case 18:
                    i2 += zzdk.zzs(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 19:
                    i2 += zzdk.zzq(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 20:
                    i2 += zzdk.zzc(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 21:
                    i2 += zzdk.zze(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 22:
                    i2 += zzdk.zzk(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 23:
                    i2 += zzdk.zzs(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 24:
                    i2 += zzdk.zzq(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 25:
                    i2 += zzdk.zzu(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 26:
                    i2 += zzdk.zzv(i6, (List) unsafe.getObject(t, j));
                    break;
                case 27:
                    i2 += zzdk.zzx(i6, (List) unsafe.getObject(t, j), zzv(i5));
                    break;
                case 28:
                    i2 += zzdk.zzy(i6, (List) unsafe.getObject(t, j));
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    i2 += zzdk.zzm(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 30:
                    i2 += zzdk.zzi(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 31:
                    i2 += zzdk.zzq(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 32:
                    i2 += zzdk.zzs(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 33:
                    i2 += zzdk.zzo(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 34:
                    i2 += zzdk.zzg(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 35:
                    int zzr = zzdk.zzr((List) unsafe.getObject(t, j));
                    if (zzr > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzr) + zzr;
                        break;
                    } else {
                        break;
                    }
                case 36:
                    int zzp = zzdk.zzp((List) unsafe.getObject(t, j));
                    if (zzp > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzp) + zzp;
                        break;
                    } else {
                        break;
                    }
                case 37:
                    int zzb2 = zzdk.zzb((List) unsafe.getObject(t, j));
                    if (zzb2 > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzb2) + zzb2;
                        break;
                    } else {
                        break;
                    }
                case 38:
                    int zzd = zzdk.zzd((List) unsafe.getObject(t, j));
                    if (zzd > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzd) + zzd;
                        break;
                    } else {
                        break;
                    }
                case 39:
                    int zzj = zzdk.zzj((List) unsafe.getObject(t, j));
                    if (zzj > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzj) + zzj;
                        break;
                    } else {
                        break;
                    }
                case 40:
                    int zzr2 = zzdk.zzr((List) unsafe.getObject(t, j));
                    if (zzr2 > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzr2) + zzr2;
                        break;
                    } else {
                        break;
                    }
                case 41:
                    int zzp2 = zzdk.zzp((List) unsafe.getObject(t, j));
                    if (zzp2 > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzp2) + zzp2;
                        break;
                    } else {
                        break;
                    }
                case 42:
                    int zzt = zzdk.zzt((List) unsafe.getObject(t, j));
                    if (zzt > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzt) + zzt;
                        break;
                    } else {
                        break;
                    }
                case 43:
                    int zzl = zzdk.zzl((List) unsafe.getObject(t, j));
                    if (zzl > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzl) + zzl;
                        break;
                    } else {
                        break;
                    }
                case 44:
                    int zzh = zzdk.zzh((List) unsafe.getObject(t, j));
                    if (zzh > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzh) + zzh;
                        break;
                    } else {
                        break;
                    }
                case 45:
                    int zzp3 = zzdk.zzp((List) unsafe.getObject(t, j));
                    if (zzp3 > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzp3) + zzp3;
                        break;
                    } else {
                        break;
                    }
                case 46:
                    int zzr3 = zzdk.zzr((List) unsafe.getObject(t, j));
                    if (zzr3 > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzr3) + zzr3;
                        break;
                    } else {
                        break;
                    }
                case 47:
                    int zzn = zzdk.zzn((List) unsafe.getObject(t, j));
                    if (zzn > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzn) + zzn;
                        break;
                    } else {
                        break;
                    }
                case 48:
                    int zzf = zzdk.zzf((List) unsafe.getObject(t, j));
                    if (zzf > 0) {
                        i2 += zzbb.zzu(i6) + zzbb.zzw(zzf) + zzf;
                        break;
                    } else {
                        break;
                    }
                case 49:
                    i2 += zzdk.zzz(i6, (List) unsafe.getObject(t, j), zzv(i5));
                    break;
                case 50:
                    zzcs.zza(i6, unsafe.getObject(t, j), zzw(i5));
                    break;
                case 51:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzx(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzx(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzv(zzF(t, j));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + 1;
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zzM(t, i6, i5)) {
                        Object object2 = unsafe.getObject(t, j);
                        if (object2 instanceof zzau) {
                            int zzw3 = zzbb.zzw(i6 << 3);
                            int zzc3 = ((zzau) object2).zzc();
                            i2 += zzw3 + zzbb.zzw(zzc3) + zzc3;
                            break;
                        } else {
                            i2 += zzbb.zzw(i6 << 3) + zzbb.zzy((String) object2);
                            break;
                        }
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zzM(t, i6, i5)) {
                        i2 += zzdk.zzw(i6, unsafe.getObject(t, j), zzv(i5));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zzM(t, i6, i5)) {
                        int zzw4 = zzbb.zzw(i6 << 3);
                        int zzc4 = ((zzau) unsafe.getObject(t, j)).zzc();
                        i2 += zzw4 + zzbb.zzw(zzc4) + zzc4;
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzw(zzF(t, j));
                        break;
                    } else {
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzv(zzF(t, j));
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zzM(t, i6, i5)) {
                        int zzF = zzF(t, j);
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzw((zzF >> 31) ^ (zzF + zzF));
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zzM(t, i6, i5)) {
                        long zzG = zzG(t, j);
                        i2 += zzbb.zzw(i6 << 3) + zzbb.zzx((zzG >> 63) ^ (zzG + zzG));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zzM(t, i6, i5)) {
                        i2 += zzbb.zzE(i6, (zzcx) unsafe.getObject(t, j), zzv(i5));
                        break;
                    } else {
                        break;
                    }
            }
        }
        zzdw<?, ?> zzdwVar = this.zzn;
        int zzh2 = i2 + zzdwVar.zzh(zzdwVar.zzd(t));
        if (!this.zzh) {
            return zzh2;
        }
        this.zzo.zzb(t);
        throw null;
    }

    private final int zzr(T t) {
        Unsafe unsafe = zzb;
        int i = 0;
        for (int i2 = 0; i2 < this.zzc.length; i2 += 3) {
            int zzA = zzA(i2);
            int zzC = zzC(zzA);
            int i3 = this.zzc[i2];
            long j = zzA & 1048575;
            if (zzC >= zzbm.DOUBLE_LIST_PACKED.zza() && zzC <= zzbm.SINT64_LIST_PACKED.zza()) {
                int i4 = this.zzc[i2 + 2];
            }
            switch (zzC) {
                case 0:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzx(zzeg.zzf(t, j));
                        break;
                    } else {
                        break;
                    }
                case 3:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzx(zzeg.zzf(t, j));
                        break;
                    } else {
                        break;
                    }
                case 4:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzv(zzeg.zzd(t, j));
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 6:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 7:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + 1;
                        break;
                    } else {
                        break;
                    }
                case 8:
                    if (zzK(t, i2)) {
                        Object zzn = zzeg.zzn(t, j);
                        if (zzn instanceof zzau) {
                            int zzw = zzbb.zzw(i3 << 3);
                            int zzc = ((zzau) zzn).zzc();
                            i += zzw + zzbb.zzw(zzc) + zzc;
                            break;
                        } else {
                            i += zzbb.zzw(i3 << 3) + zzbb.zzy((String) zzn);
                            break;
                        }
                    } else {
                        break;
                    }
                case 9:
                    if (zzK(t, i2)) {
                        i += zzdk.zzw(i3, zzeg.zzn(t, j), zzv(i2));
                        break;
                    } else {
                        break;
                    }
                case 10:
                    if (zzK(t, i2)) {
                        int zzw2 = zzbb.zzw(i3 << 3);
                        int zzc2 = ((zzau) zzeg.zzn(t, j)).zzc();
                        i += zzw2 + zzbb.zzw(zzc2) + zzc2;
                        break;
                    } else {
                        break;
                    }
                case 11:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzw(zzeg.zzd(t, j));
                        break;
                    } else {
                        break;
                    }
                case 12:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzv(zzeg.zzd(t, j));
                        break;
                    } else {
                        break;
                    }
                case 13:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 14:
                    if (zzK(t, i2)) {
                        i += zzbb.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 15:
                    if (zzK(t, i2)) {
                        int zzd = zzeg.zzd(t, j);
                        i += zzbb.zzw(i3 << 3) + zzbb.zzw((zzd >> 31) ^ (zzd + zzd));
                        break;
                    } else {
                        break;
                    }
                case 16:
                    if (zzK(t, i2)) {
                        long zzf = zzeg.zzf(t, j);
                        i += zzbb.zzw(i3 << 3) + zzbb.zzx((zzf >> 63) ^ (zzf + zzf));
                        break;
                    } else {
                        break;
                    }
                case 17:
                    if (zzK(t, i2)) {
                        i += zzbb.zzE(i3, (zzcx) zzeg.zzn(t, j), zzv(i2));
                        break;
                    } else {
                        break;
                    }
                case 18:
                    i += zzdk.zzs(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 19:
                    i += zzdk.zzq(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 20:
                    i += zzdk.zzc(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 21:
                    i += zzdk.zze(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 22:
                    i += zzdk.zzk(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 23:
                    i += zzdk.zzs(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 24:
                    i += zzdk.zzq(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 25:
                    i += zzdk.zzu(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 26:
                    i += zzdk.zzv(i3, (List) zzeg.zzn(t, j));
                    break;
                case 27:
                    i += zzdk.zzx(i3, (List) zzeg.zzn(t, j), zzv(i2));
                    break;
                case 28:
                    i += zzdk.zzy(i3, (List) zzeg.zzn(t, j));
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    i += zzdk.zzm(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 30:
                    i += zzdk.zzi(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 31:
                    i += zzdk.zzq(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 32:
                    i += zzdk.zzs(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 33:
                    i += zzdk.zzo(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 34:
                    i += zzdk.zzg(i3, (List) zzeg.zzn(t, j), false);
                    break;
                case 35:
                    int zzr = zzdk.zzr((List) unsafe.getObject(t, j));
                    if (zzr > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzr) + zzr;
                        break;
                    } else {
                        break;
                    }
                case 36:
                    int zzp = zzdk.zzp((List) unsafe.getObject(t, j));
                    if (zzp > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzp) + zzp;
                        break;
                    } else {
                        break;
                    }
                case 37:
                    int zzb2 = zzdk.zzb((List) unsafe.getObject(t, j));
                    if (zzb2 > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzb2) + zzb2;
                        break;
                    } else {
                        break;
                    }
                case 38:
                    int zzd2 = zzdk.zzd((List) unsafe.getObject(t, j));
                    if (zzd2 > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzd2) + zzd2;
                        break;
                    } else {
                        break;
                    }
                case 39:
                    int zzj = zzdk.zzj((List) unsafe.getObject(t, j));
                    if (zzj > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzj) + zzj;
                        break;
                    } else {
                        break;
                    }
                case 40:
                    int zzr2 = zzdk.zzr((List) unsafe.getObject(t, j));
                    if (zzr2 > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzr2) + zzr2;
                        break;
                    } else {
                        break;
                    }
                case 41:
                    int zzp2 = zzdk.zzp((List) unsafe.getObject(t, j));
                    if (zzp2 > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzp2) + zzp2;
                        break;
                    } else {
                        break;
                    }
                case 42:
                    int zzt = zzdk.zzt((List) unsafe.getObject(t, j));
                    if (zzt > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzt) + zzt;
                        break;
                    } else {
                        break;
                    }
                case 43:
                    int zzl = zzdk.zzl((List) unsafe.getObject(t, j));
                    if (zzl > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzl) + zzl;
                        break;
                    } else {
                        break;
                    }
                case 44:
                    int zzh = zzdk.zzh((List) unsafe.getObject(t, j));
                    if (zzh > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzh) + zzh;
                        break;
                    } else {
                        break;
                    }
                case 45:
                    int zzp3 = zzdk.zzp((List) unsafe.getObject(t, j));
                    if (zzp3 > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzp3) + zzp3;
                        break;
                    } else {
                        break;
                    }
                case 46:
                    int zzr3 = zzdk.zzr((List) unsafe.getObject(t, j));
                    if (zzr3 > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzr3) + zzr3;
                        break;
                    } else {
                        break;
                    }
                case 47:
                    int zzn2 = zzdk.zzn((List) unsafe.getObject(t, j));
                    if (zzn2 > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzn2) + zzn2;
                        break;
                    } else {
                        break;
                    }
                case 48:
                    int zzf2 = zzdk.zzf((List) unsafe.getObject(t, j));
                    if (zzf2 > 0) {
                        i += zzbb.zzu(i3) + zzbb.zzw(zzf2) + zzf2;
                        break;
                    } else {
                        break;
                    }
                case 49:
                    i += zzdk.zzz(i3, (List) zzeg.zzn(t, j), zzv(i2));
                    break;
                case 50:
                    zzcs.zza(i3, zzeg.zzn(t, j), zzw(i2));
                    break;
                case 51:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzx(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzx(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzv(zzF(t, j));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + 1;
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zzM(t, i3, i2)) {
                        Object zzn3 = zzeg.zzn(t, j);
                        if (zzn3 instanceof zzau) {
                            int zzw3 = zzbb.zzw(i3 << 3);
                            int zzc3 = ((zzau) zzn3).zzc();
                            i += zzw3 + zzbb.zzw(zzc3) + zzc3;
                            break;
                        } else {
                            i += zzbb.zzw(i3 << 3) + zzbb.zzy((String) zzn3);
                            break;
                        }
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zzM(t, i3, i2)) {
                        i += zzdk.zzw(i3, zzeg.zzn(t, j), zzv(i2));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zzM(t, i3, i2)) {
                        int zzw4 = zzbb.zzw(i3 << 3);
                        int zzc4 = ((zzau) zzeg.zzn(t, j)).zzc();
                        i += zzw4 + zzbb.zzw(zzc4) + zzc4;
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzw(zzF(t, j));
                        break;
                    } else {
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + zzbb.zzv(zzF(t, j));
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zzM(t, i3, i2)) {
                        int zzF = zzF(t, j);
                        i += zzbb.zzw(i3 << 3) + zzbb.zzw((zzF >> 31) ^ (zzF + zzF));
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zzM(t, i3, i2)) {
                        long zzG = zzG(t, j);
                        i += zzbb.zzw(i3 << 3) + zzbb.zzx((zzG >> 63) ^ (zzG + zzG));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zzM(t, i3, i2)) {
                        i += zzbb.zzE(i3, (zzcx) zzeg.zzn(t, j), zzv(i2));
                        break;
                    } else {
                        break;
                    }
            }
        }
        zzdw<?, ?> zzdwVar = this.zzn;
        return i + zzdwVar.zzh(zzdwVar.zzd(t));
    }

    private final int zzs(T t, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, long j, int i7, long j2, zzai zzaiVar) throws IOException {
        int i8;
        int i9 = i;
        Unsafe unsafe = zzb;
        zzbz zzbzVar = (zzbz) unsafe.getObject(t, j2);
        if (!zzbzVar.zza()) {
            int size = zzbzVar.size();
            zzbzVar = zzbzVar.zze(size == 0 ? 10 : size + size);
            unsafe.putObject(t, j2, zzbzVar);
        }
        switch (i7) {
            case 18:
            case 35:
                if (i5 == 2) {
                    zzbd zzbdVar = (zzbd) zzbzVar;
                    int zza2 = zzaj.zza(bArr, i9, zzaiVar);
                    int i10 = zzaiVar.zza + zza2;
                    while (zza2 < i10) {
                        zzbdVar.zzd(Double.longBitsToDouble(zzaj.zze(bArr, zza2)));
                        zza2 += 8;
                    }
                    if (zza2 != i10) {
                        throw zzcc.zzb();
                    }
                    return zza2;
                } else if (i5 == 1) {
                    zzbd zzbdVar2 = (zzbd) zzbzVar;
                    zzbdVar2.zzd(Double.longBitsToDouble(zzaj.zze(bArr, i)));
                    int i11 = i9 + 8;
                    while (i11 < i2) {
                        int zza3 = zzaj.zza(bArr, i11, zzaiVar);
                        if (i3 != zzaiVar.zza) {
                            return i11;
                        }
                        zzbdVar2.zzd(Double.longBitsToDouble(zzaj.zze(bArr, zza3)));
                        i11 = zza3 + 8;
                    }
                    return i11;
                }
                break;
            case 19:
            case 36:
                if (i5 != 2) {
                    if (i5 == 5) {
                        zzbn zzbnVar = (zzbn) zzbzVar;
                        zzbnVar.zzg(Float.intBitsToFloat(zzaj.zzd(bArr, i)));
                        int i12 = i9 + 4;
                        while (i12 < i2) {
                            int zza4 = zzaj.zza(bArr, i12, zzaiVar);
                            if (i3 != zzaiVar.zza) {
                                return i12;
                            }
                            zzbnVar.zzg(Float.intBitsToFloat(zzaj.zzd(bArr, zza4)));
                            i12 = zza4 + 4;
                        }
                        return i12;
                    }
                } else {
                    zzbn zzbnVar2 = (zzbn) zzbzVar;
                    int zza5 = zzaj.zza(bArr, i9, zzaiVar);
                    int i13 = zzaiVar.zza + zza5;
                    while (zza5 < i13) {
                        zzbnVar2.zzg(Float.intBitsToFloat(zzaj.zzd(bArr, zza5)));
                        zza5 += 4;
                    }
                    if (zza5 != i13) {
                        throw zzcc.zzb();
                    }
                    return zza5;
                }
                break;
            case 20:
            case 21:
            case 37:
            case 38:
                if (i5 == 2) {
                    zzcm zzcmVar = (zzcm) zzbzVar;
                    int zza6 = zzaj.zza(bArr, i9, zzaiVar);
                    int i14 = zzaiVar.zza + zza6;
                    while (zza6 < i14) {
                        zza6 = zzaj.zzc(bArr, zza6, zzaiVar);
                        zzcmVar.zzg(zzaiVar.zzb);
                    }
                    if (zza6 != i14) {
                        throw zzcc.zzb();
                    }
                    return zza6;
                } else if (i5 == 0) {
                    zzcm zzcmVar2 = (zzcm) zzbzVar;
                    int zzc = zzaj.zzc(bArr, i9, zzaiVar);
                    zzcmVar2.zzg(zzaiVar.zzb);
                    while (zzc < i2) {
                        int zza7 = zzaj.zza(bArr, zzc, zzaiVar);
                        if (i3 != zzaiVar.zza) {
                            return zzc;
                        }
                        zzc = zzaj.zzc(bArr, zza7, zzaiVar);
                        zzcmVar2.zzg(zzaiVar.zzb);
                    }
                    return zzc;
                }
                break;
            case 22:
            case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
            case 39:
            case 43:
                if (i5 == 2) {
                    return zzaj.zzl(bArr, i9, zzbzVar, zzaiVar);
                }
                if (i5 == 0) {
                    return zzaj.zzk(i3, bArr, i, i2, zzbzVar, zzaiVar);
                }
                break;
            case 23:
            case 32:
            case 40:
            case 46:
                if (i5 == 2) {
                    zzcm zzcmVar3 = (zzcm) zzbzVar;
                    int zza8 = zzaj.zza(bArr, i9, zzaiVar);
                    int i15 = zzaiVar.zza + zza8;
                    while (zza8 < i15) {
                        zzcmVar3.zzg(zzaj.zze(bArr, zza8));
                        zza8 += 8;
                    }
                    if (zza8 != i15) {
                        throw zzcc.zzb();
                    }
                    return zza8;
                } else if (i5 == 1) {
                    zzcm zzcmVar4 = (zzcm) zzbzVar;
                    zzcmVar4.zzg(zzaj.zze(bArr, i));
                    int i16 = i9 + 8;
                    while (i16 < i2) {
                        int zza9 = zzaj.zza(bArr, i16, zzaiVar);
                        if (i3 != zzaiVar.zza) {
                            return i16;
                        }
                        zzcmVar4.zzg(zzaj.zze(bArr, zza9));
                        i16 = zza9 + 8;
                    }
                    return i16;
                }
                break;
            case 24:
            case 31:
            case 41:
            case 45:
                if (i5 != 2) {
                    if (i5 == 5) {
                        zzbt zzbtVar = (zzbt) zzbzVar;
                        zzbtVar.zzf(zzaj.zzd(bArr, i));
                        int i17 = i9 + 4;
                        while (i17 < i2) {
                            int zza10 = zzaj.zza(bArr, i17, zzaiVar);
                            if (i3 != zzaiVar.zza) {
                                return i17;
                            }
                            zzbtVar.zzf(zzaj.zzd(bArr, zza10));
                            i17 = zza10 + 4;
                        }
                        return i17;
                    }
                } else {
                    zzbt zzbtVar2 = (zzbt) zzbzVar;
                    int zza11 = zzaj.zza(bArr, i9, zzaiVar);
                    int i18 = zzaiVar.zza + zza11;
                    while (zza11 < i18) {
                        zzbtVar2.zzf(zzaj.zzd(bArr, zza11));
                        zza11 += 4;
                    }
                    if (zza11 != i18) {
                        throw zzcc.zzb();
                    }
                    return zza11;
                }
                break;
            case 25:
            case 42:
                if (i5 == 2) {
                    zzak zzakVar = (zzak) zzbzVar;
                    int zza12 = zzaj.zza(bArr, i9, zzaiVar);
                    int i19 = zzaiVar.zza + zza12;
                    while (zza12 < i19) {
                        zza12 = zzaj.zzc(bArr, zza12, zzaiVar);
                        zzakVar.zzd(zzaiVar.zzb != 0);
                    }
                    if (zza12 != i19) {
                        throw zzcc.zzb();
                    }
                    return zza12;
                } else if (i5 == 0) {
                    zzak zzakVar2 = (zzak) zzbzVar;
                    int zzc2 = zzaj.zzc(bArr, i9, zzaiVar);
                    zzakVar2.zzd(zzaiVar.zzb != 0);
                    while (zzc2 < i2) {
                        int zza13 = zzaj.zza(bArr, zzc2, zzaiVar);
                        if (i3 != zzaiVar.zza) {
                            return zzc2;
                        }
                        zzc2 = zzaj.zzc(bArr, zza13, zzaiVar);
                        zzakVar2.zzd(zzaiVar.zzb != 0);
                    }
                    return zzc2;
                }
                break;
            case 26:
                if (i5 == 2) {
                    if ((j & 536870912) != 0) {
                        i9 = zzaj.zza(bArr, i9, zzaiVar);
                        int i20 = zzaiVar.zza;
                        if (i20 >= 0) {
                            if (i20 == 0) {
                                zzbzVar.add("");
                            } else {
                                int i21 = i9 + i20;
                                if (!zzel.zzb(bArr, i9, i21)) {
                                    throw zzcc.zzg();
                                }
                                zzbzVar.add(new String(bArr, i9, i20, zzca.zza));
                                i9 = i21;
                            }
                            while (i9 < i2) {
                                int zza14 = zzaj.zza(bArr, i9, zzaiVar);
                                if (i3 != zzaiVar.zza) {
                                    break;
                                } else {
                                    i9 = zzaj.zza(bArr, zza14, zzaiVar);
                                    int i22 = zzaiVar.zza;
                                    if (i22 >= 0) {
                                        if (i22 == 0) {
                                            zzbzVar.add("");
                                        } else {
                                            int i23 = i9 + i22;
                                            if (zzel.zzb(bArr, i9, i23)) {
                                                zzbzVar.add(new String(bArr, i9, i22, zzca.zza));
                                                i9 = i23;
                                            } else {
                                                throw zzcc.zzg();
                                            }
                                        }
                                    } else {
                                        throw zzcc.zzc();
                                    }
                                }
                            }
                            break;
                        } else {
                            throw zzcc.zzc();
                        }
                    } else {
                        i9 = zzaj.zza(bArr, i9, zzaiVar);
                        int i24 = zzaiVar.zza;
                        if (i24 >= 0) {
                            if (i24 == 0) {
                                zzbzVar.add("");
                            } else {
                                zzbzVar.add(new String(bArr, i9, i24, zzca.zza));
                                i9 += i24;
                            }
                            while (i9 < i2) {
                                int zza15 = zzaj.zza(bArr, i9, zzaiVar);
                                if (i3 != zzaiVar.zza) {
                                    break;
                                } else {
                                    i9 = zzaj.zza(bArr, zza15, zzaiVar);
                                    int i25 = zzaiVar.zza;
                                    if (i25 >= 0) {
                                        if (i25 == 0) {
                                            zzbzVar.add("");
                                        } else {
                                            zzbzVar.add(new String(bArr, i9, i25, zzca.zza));
                                            i9 += i25;
                                        }
                                    } else {
                                        throw zzcc.zzc();
                                    }
                                }
                            }
                            break;
                        } else {
                            throw zzcc.zzc();
                        }
                    }
                }
                break;
            case 27:
                if (i5 == 2) {
                    return zzaj.zzm(zzv(i6), i3, bArr, i, i2, zzbzVar, zzaiVar);
                }
                break;
            case 28:
                if (i5 == 2) {
                    int zza16 = zzaj.zza(bArr, i9, zzaiVar);
                    int i26 = zzaiVar.zza;
                    if (i26 < 0) {
                        throw zzcc.zzc();
                    }
                    if (i26 <= bArr.length - zza16) {
                        if (i26 == 0) {
                            zzbzVar.add(zzau.zzb);
                        } else {
                            zzbzVar.add(zzau.zzk(bArr, zza16, i26));
                            zza16 += i26;
                        }
                        while (zza16 < i2) {
                            int zza17 = zzaj.zza(bArr, zza16, zzaiVar);
                            if (i3 != zzaiVar.zza) {
                                return zza16;
                            }
                            zza16 = zzaj.zza(bArr, zza17, zzaiVar);
                            int i27 = zzaiVar.zza;
                            if (i27 >= 0) {
                                if (i27 <= bArr.length - zza16) {
                                    if (i27 == 0) {
                                        zzbzVar.add(zzau.zzb);
                                    } else {
                                        zzbzVar.add(zzau.zzk(bArr, zza16, i27));
                                        zza16 += i27;
                                    }
                                } else {
                                    throw zzcc.zzb();
                                }
                            } else {
                                throw zzcc.zzc();
                            }
                        }
                        return zza16;
                    }
                    throw zzcc.zzb();
                }
                break;
            case 30:
            case 44:
                if (i5 == 2) {
                    i8 = zzaj.zzl(bArr, i9, zzbzVar, zzaiVar);
                } else if (i5 == 0) {
                    i8 = zzaj.zzk(i3, bArr, i, i2, zzbzVar, zzaiVar);
                }
                zzbs zzbsVar = (zzbs) t;
                zzdx zzdxVar = zzbsVar.zzc;
                if (zzdxVar == zzdx.zza()) {
                    zzdxVar = null;
                }
                Object zzG = zzdk.zzG(i4, zzbzVar, zzx(i6), zzdxVar, this.zzn);
                if (zzG == null) {
                    return i8;
                }
                zzbsVar.zzc = (zzdx) zzG;
                return i8;
            case 33:
            case 47:
                if (i5 == 2) {
                    zzbt zzbtVar3 = (zzbt) zzbzVar;
                    int zza18 = zzaj.zza(bArr, i9, zzaiVar);
                    int i28 = zzaiVar.zza + zza18;
                    while (zza18 < i28) {
                        zza18 = zzaj.zza(bArr, zza18, zzaiVar);
                        zzbtVar3.zzf(zzax.zzb(zzaiVar.zza));
                    }
                    if (zza18 != i28) {
                        throw zzcc.zzb();
                    }
                    return zza18;
                } else if (i5 == 0) {
                    zzbt zzbtVar4 = (zzbt) zzbzVar;
                    int zza19 = zzaj.zza(bArr, i9, zzaiVar);
                    zzbtVar4.zzf(zzax.zzb(zzaiVar.zza));
                    while (zza19 < i2) {
                        int zza20 = zzaj.zza(bArr, zza19, zzaiVar);
                        if (i3 != zzaiVar.zza) {
                            return zza19;
                        }
                        zza19 = zzaj.zza(bArr, zza20, zzaiVar);
                        zzbtVar4.zzf(zzax.zzb(zzaiVar.zza));
                    }
                    return zza19;
                }
                break;
            case 34:
            case 48:
                if (i5 == 2) {
                    zzcm zzcmVar5 = (zzcm) zzbzVar;
                    int zza21 = zzaj.zza(bArr, i9, zzaiVar);
                    int i29 = zzaiVar.zza + zza21;
                    while (zza21 < i29) {
                        zza21 = zzaj.zzc(bArr, zza21, zzaiVar);
                        zzcmVar5.zzg(zzax.zzc(zzaiVar.zzb));
                    }
                    if (zza21 != i29) {
                        throw zzcc.zzb();
                    }
                    return zza21;
                } else if (i5 == 0) {
                    zzcm zzcmVar6 = (zzcm) zzbzVar;
                    int zzc3 = zzaj.zzc(bArr, i9, zzaiVar);
                    zzcmVar6.zzg(zzax.zzc(zzaiVar.zzb));
                    while (zzc3 < i2) {
                        int zza22 = zzaj.zza(bArr, zzc3, zzaiVar);
                        if (i3 != zzaiVar.zza) {
                            return zzc3;
                        }
                        zzc3 = zzaj.zzc(bArr, zza22, zzaiVar);
                        zzcmVar6.zzg(zzax.zzc(zzaiVar.zzb));
                    }
                    return zzc3;
                }
                break;
            default:
                if (i5 == 3) {
                    zzdi zzv = zzv(i6);
                    int i30 = (i3 & (-8)) | 4;
                    int zzj = zzaj.zzj(zzv, bArr, i, i2, i30, zzaiVar);
                    zzbzVar.add(zzaiVar.zzc);
                    while (zzj < i2) {
                        int zza23 = zzaj.zza(bArr, zzj, zzaiVar);
                        if (i3 != zzaiVar.zza) {
                            return zzj;
                        }
                        zzj = zzaj.zzj(zzv, bArr, zza23, i2, i30, zzaiVar);
                        zzbzVar.add(zzaiVar.zzc);
                    }
                    return zzj;
                }
                break;
        }
        return i9;
    }

    private final <K, V> int zzt(T t, byte[] bArr, int i, int i2, int i3, long j, zzai zzaiVar) throws IOException {
        Unsafe unsafe = zzb;
        Object zzw = zzw(i3);
        Object object = unsafe.getObject(t, j);
        if (!((zzcr) object).zze()) {
            zzcr<K, V> zzc = zzcr.zza().zzc();
            zzcs.zzb(zzc, object);
            unsafe.putObject(t, j, zzc);
        }
        zzcq zzcqVar = (zzcq) zzw;
        throw null;
    }

    private final int zzu(T t, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, long j, int i8, zzai zzaiVar) throws IOException {
        Unsafe unsafe = zzb;
        long j2 = this.zzc[i8 + 2] & 1048575;
        switch (i7) {
            case 51:
                if (i5 == 1) {
                    unsafe.putObject(t, j, Double.valueOf(Double.longBitsToDouble(zzaj.zze(bArr, i))));
                    unsafe.putInt(t, j2, i4);
                    return i + 8;
                }
                break;
            case 52:
                if (i5 == 5) {
                    unsafe.putObject(t, j, Float.valueOf(Float.intBitsToFloat(zzaj.zzd(bArr, i))));
                    unsafe.putInt(t, j2, i4);
                    return i + 4;
                }
                break;
            case 53:
            case 54:
                if (i5 == 0) {
                    int zzc = zzaj.zzc(bArr, i, zzaiVar);
                    unsafe.putObject(t, j, Long.valueOf(zzaiVar.zzb));
                    unsafe.putInt(t, j2, i4);
                    return zzc;
                }
                break;
            case 55:
            case 62:
                if (i5 == 0) {
                    int zza2 = zzaj.zza(bArr, i, zzaiVar);
                    unsafe.putObject(t, j, Integer.valueOf(zzaiVar.zza));
                    unsafe.putInt(t, j2, i4);
                    return zza2;
                }
                break;
            case UndoView.ACTION_USERNAME_COPIED /* 56 */:
            case VoIPService.CALL_MIN_LAYER /* 65 */:
                if (i5 == 1) {
                    unsafe.putObject(t, j, Long.valueOf(zzaj.zze(bArr, i)));
                    unsafe.putInt(t, j2, i4);
                    return i + 8;
                }
                break;
            case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
            case 64:
                if (i5 == 5) {
                    unsafe.putObject(t, j, Integer.valueOf(zzaj.zzd(bArr, i)));
                    unsafe.putInt(t, j2, i4);
                    return i + 4;
                }
                break;
            case UndoView.ACTION_TEXT_COPIED /* 58 */:
                if (i5 == 0) {
                    int zzc2 = zzaj.zzc(bArr, i, zzaiVar);
                    unsafe.putObject(t, j, Boolean.valueOf(zzaiVar.zzb != 0));
                    unsafe.putInt(t, j2, i4);
                    return zzc2;
                }
                break;
            case 59:
                if (i5 == 2) {
                    int zza3 = zzaj.zza(bArr, i, zzaiVar);
                    int i9 = zzaiVar.zza;
                    if (i9 == 0) {
                        unsafe.putObject(t, j, "");
                    } else if ((i6 & 536870912) != 0 && !zzel.zzb(bArr, zza3, zza3 + i9)) {
                        throw zzcc.zzg();
                    } else {
                        unsafe.putObject(t, j, new String(bArr, zza3, i9, zzca.zza));
                        zza3 += i9;
                    }
                    unsafe.putInt(t, j2, i4);
                    return zza3;
                }
                break;
            case UndoView.ACTION_PHONE_COPIED /* 60 */:
                if (i5 == 2) {
                    int zzi = zzaj.zzi(zzv(i8), bArr, i, i2, zzaiVar);
                    Object object = unsafe.getInt(t, j2) == i4 ? unsafe.getObject(t, j) : null;
                    if (object == null) {
                        unsafe.putObject(t, j, zzaiVar.zzc);
                    } else {
                        unsafe.putObject(t, j, zzca.zzi(object, zzaiVar.zzc));
                    }
                    unsafe.putInt(t, j2, i4);
                    return zzi;
                }
                break;
            case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                if (i5 == 2) {
                    int zzh = zzaj.zzh(bArr, i, zzaiVar);
                    unsafe.putObject(t, j, zzaiVar.zzc);
                    unsafe.putInt(t, j2, i4);
                    return zzh;
                }
                break;
            case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                if (i5 == 0) {
                    int zza4 = zzaj.zza(bArr, i, zzaiVar);
                    int i10 = zzaiVar.zza;
                    zzbw zzx = zzx(i8);
                    if (zzx == null || zzx.zza(i10)) {
                        unsafe.putObject(t, j, Integer.valueOf(i10));
                        unsafe.putInt(t, j2, i4);
                    } else {
                        zzf(t).zzh(i3, Long.valueOf(i10));
                    }
                    return zza4;
                }
                break;
            case 66:
                if (i5 == 0) {
                    int zza5 = zzaj.zza(bArr, i, zzaiVar);
                    unsafe.putObject(t, j, Integer.valueOf(zzax.zzb(zzaiVar.zza)));
                    unsafe.putInt(t, j2, i4);
                    return zza5;
                }
                break;
            case 67:
                if (i5 == 0) {
                    int zzc3 = zzaj.zzc(bArr, i, zzaiVar);
                    unsafe.putObject(t, j, Long.valueOf(zzax.zzc(zzaiVar.zzb)));
                    unsafe.putInt(t, j2, i4);
                    return zzc3;
                }
                break;
            case 68:
                if (i5 == 3) {
                    int zzj = zzaj.zzj(zzv(i8), bArr, i, i2, (i3 & (-8)) | 4, zzaiVar);
                    Object object2 = unsafe.getInt(t, j2) == i4 ? unsafe.getObject(t, j) : null;
                    if (object2 == null) {
                        unsafe.putObject(t, j, zzaiVar.zzc);
                    } else {
                        unsafe.putObject(t, j, zzca.zzi(object2, zzaiVar.zzc));
                    }
                    unsafe.putInt(t, j2, i4);
                    return zzj;
                }
                break;
        }
        return i;
    }

    private final zzdi zzv(int i) {
        int i2 = i / 3;
        int i3 = i2 + i2;
        zzdi zzdiVar = (zzdi) this.zzd[i3];
        if (zzdiVar != null) {
            return zzdiVar;
        }
        zzdi<T> zzb2 = zzdf.zza().zzb((Class) this.zzd[i3 + 1]);
        this.zzd[i3] = zzb2;
        return zzb2;
    }

    private final Object zzw(int i) {
        int i2 = i / 3;
        return this.zzd[i2 + i2];
    }

    private final zzbw zzx(int i) {
        int i2 = i / 3;
        return (zzbw) this.zzd[i2 + i2 + 1];
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v10, types: [int] */
    private final int zzy(T t, byte[] bArr, int i, int i2, zzai zzaiVar) throws IOException {
        byte b;
        int i3;
        int i4;
        int i5;
        Unsafe unsafe;
        int i6;
        int i7;
        int i8;
        Unsafe unsafe2;
        int i9;
        Unsafe unsafe3;
        int i10;
        int i11;
        int i12;
        zzda<T> zzdaVar = this;
        T t2 = t;
        byte[] bArr2 = bArr;
        int i13 = i2;
        zzai zzaiVar2 = zzaiVar;
        Unsafe unsafe4 = zzb;
        int i14 = -1;
        int i15 = 1048575;
        int i16 = i;
        int i17 = -1;
        int i18 = 0;
        int i19 = 0;
        int i20 = 1048575;
        while (i16 < i13) {
            int i21 = i16 + 1;
            byte b2 = bArr2[i16];
            if (b2 < 0) {
                i3 = zzaj.zzb(b2, bArr2, i21, zzaiVar2);
                b = zzaiVar2.zza;
            } else {
                b = b2;
                i3 = i21;
            }
            int i22 = b >>> 3;
            int i23 = b & 7;
            int zzP = i22 > i17 ? zzdaVar.zzP(i22, i18 / 3) : zzdaVar.zzO(i22);
            if (zzP == i14) {
                i4 = i3;
                i5 = i22;
                unsafe = unsafe4;
                i6 = 0;
            } else {
                int i24 = zzdaVar.zzc[zzP + 1];
                int zzC = zzC(i24);
                Unsafe unsafe5 = unsafe4;
                long j = i24 & i15;
                if (zzC <= 17) {
                    int i25 = zzdaVar.zzc[zzP + 2];
                    boolean z = true;
                    int i26 = 1 << (i25 >>> 20);
                    int i27 = i25 & 1048575;
                    if (i27 != i20) {
                        if (i20 != 1048575) {
                            i7 = i24;
                            i8 = zzP;
                            long j2 = i20;
                            unsafe3 = unsafe5;
                            unsafe3.putInt(t2, j2, i19);
                        } else {
                            i7 = i24;
                            i8 = zzP;
                            unsafe3 = unsafe5;
                        }
                        if (i27 != 1048575) {
                            i19 = unsafe3.getInt(t2, i27);
                        }
                        unsafe2 = unsafe3;
                        i20 = i27;
                    } else {
                        i7 = i24;
                        i8 = zzP;
                        unsafe2 = unsafe5;
                    }
                    switch (zzC) {
                        case 0:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 1) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                zzeg.zzm(t2, j, Double.longBitsToDouble(zzaj.zze(bArr2, i3)));
                                i16 = i3 + 8;
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i9;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        case 1:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 5) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                zzeg.zzk(t2, j, Float.intBitsToFloat(zzaj.zzd(bArr2, i3)));
                                i16 = i3 + 4;
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i9;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        case 2:
                        case 3:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 0) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                int zzc = zzaj.zzc(bArr2, i3, zzaiVar2);
                                unsafe2.putLong(t, j, zzaiVar2.zzb);
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i9;
                                i16 = zzc;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        case 4:
                        case 11:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 0) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                i16 = zzaj.zza(bArr2, i3, zzaiVar2);
                                unsafe2.putInt(t2, j, zzaiVar2.zza);
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i9;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        case 5:
                        case 14:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 1) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                unsafe2.putLong(t, j, zzaj.zze(bArr2, i3));
                                i16 = i3 + 8;
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i9;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        case 6:
                        case 13:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 5) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                unsafe2.putInt(t2, j, zzaj.zzd(bArr2, i3));
                                i16 = i3 + 4;
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                i18 = i9;
                                break;
                            }
                        case 7:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 0) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                i16 = zzaj.zzc(bArr2, i3, zzaiVar2);
                                if (zzaiVar2.zzb == 0) {
                                    z = false;
                                }
                                zzeg.zzi(t2, j, z);
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                i18 = i9;
                                break;
                            }
                        case 8:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 2) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                i16 = (i7 & 536870912) == 0 ? zzaj.zzf(bArr2, i3, zzaiVar2) : zzaj.zzg(bArr2, i3, zzaiVar2);
                                unsafe2.putObject(t2, j, zzaiVar2.zzc);
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                i18 = i9;
                                break;
                            }
                        case 9:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 2) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                i16 = zzaj.zzi(zzdaVar.zzv(i9), bArr2, i3, i2, zzaiVar2);
                                Object object = unsafe2.getObject(t2, j);
                                if (object == null) {
                                    unsafe2.putObject(t2, j, zzaiVar2.zzc);
                                } else {
                                    unsafe2.putObject(t2, j, zzca.zzi(object, zzaiVar2.zzc));
                                }
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                i18 = i9;
                                break;
                            }
                        case 10:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 2) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                i16 = zzaj.zzh(bArr2, i3, zzaiVar2);
                                unsafe2.putObject(t2, j, zzaiVar2.zzc);
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i9;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        case 12:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 0) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                i16 = zzaj.zza(bArr2, i3, zzaiVar2);
                                unsafe2.putInt(t2, j, zzaiVar2.zza);
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i9;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        case 15:
                            i9 = i8;
                            i5 = i22;
                            if (i23 != 0) {
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                i16 = zzaj.zza(bArr2, i3, zzaiVar2);
                                unsafe2.putInt(t2, j, zzax.zzb(zzaiVar2.zza));
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i9;
                                i17 = i5;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        case 16:
                            if (i23 != 0) {
                                i9 = i8;
                                i5 = i22;
                                i4 = i3;
                                unsafe = unsafe2;
                                i6 = i9;
                                break;
                            } else {
                                int zzc2 = zzaj.zzc(bArr2, i3, zzaiVar2);
                                unsafe2.putLong(t, j, zzax.zzc(zzaiVar2.zzb));
                                i19 |= i26;
                                unsafe4 = unsafe2;
                                i18 = i8;
                                i16 = zzc2;
                                i17 = i22;
                                i15 = 1048575;
                                i14 = -1;
                                i13 = i2;
                                break;
                            }
                        default:
                            i9 = i8;
                            i5 = i22;
                            i4 = i3;
                            unsafe = unsafe2;
                            i6 = i9;
                            break;
                    }
                } else {
                    i5 = i22;
                    int i28 = zzP;
                    if (zzC == 27) {
                        if (i23 == 2) {
                            zzbz zzbzVar = (zzbz) unsafe5.getObject(t2, j);
                            if (!zzbzVar.zza()) {
                                int size = zzbzVar.size();
                                zzbzVar = zzbzVar.zze(size == 0 ? 10 : size + size);
                                unsafe5.putObject(t2, j, zzbzVar);
                            }
                            i16 = zzaj.zzm(zzdaVar.zzv(i28), b, bArr, i3, i2, zzbzVar, zzaiVar);
                            i19 = i19;
                            unsafe4 = unsafe5;
                            i18 = i28;
                            i17 = i5;
                            i15 = 1048575;
                            i14 = -1;
                            i13 = i2;
                        } else {
                            i10 = i3;
                            i11 = i20;
                            i12 = i19;
                            unsafe = unsafe5;
                            i6 = i28;
                            i4 = i10;
                            i19 = i12;
                            i20 = i11;
                        }
                    } else if (zzC <= 49) {
                        int i29 = i3;
                        int i30 = i19;
                        int i31 = i20;
                        unsafe = unsafe5;
                        i6 = i28;
                        i16 = zzs(t, bArr, i3, i2, b, i5, i23, i28, i24, zzC, j, zzaiVar);
                        if (i16 != i29) {
                            zzdaVar = this;
                            t2 = t;
                            bArr2 = bArr;
                            i13 = i2;
                            zzaiVar2 = zzaiVar;
                            i18 = i6;
                            i17 = i5;
                            i19 = i30;
                            i20 = i31;
                            unsafe4 = unsafe;
                            i15 = 1048575;
                            i14 = -1;
                        } else {
                            i4 = i16;
                            i19 = i30;
                            i20 = i31;
                        }
                    } else {
                        i10 = i3;
                        i12 = i19;
                        i11 = i20;
                        unsafe = unsafe5;
                        i6 = i28;
                        if (zzC != 50) {
                            i16 = zzu(t, bArr, i10, i2, b, i5, i23, i24, zzC, j, i6, zzaiVar);
                            if (i16 != i10) {
                                zzdaVar = this;
                                t2 = t;
                                bArr2 = bArr;
                                i13 = i2;
                                zzaiVar2 = zzaiVar;
                                i18 = i6;
                                i17 = i5;
                                i19 = i12;
                                i20 = i11;
                                unsafe4 = unsafe;
                                i15 = 1048575;
                                i14 = -1;
                            } else {
                                i4 = i16;
                                i19 = i12;
                                i20 = i11;
                            }
                        } else if (i23 == 2) {
                            i16 = zzt(t, bArr, i10, i2, i6, j, zzaiVar);
                            if (i16 != i10) {
                                zzdaVar = this;
                                t2 = t;
                                bArr2 = bArr;
                                i13 = i2;
                                zzaiVar2 = zzaiVar;
                                i18 = i6;
                                i17 = i5;
                                i19 = i12;
                                i20 = i11;
                                unsafe4 = unsafe;
                                i15 = 1048575;
                                i14 = -1;
                            } else {
                                i4 = i16;
                                i19 = i12;
                                i20 = i11;
                            }
                        } else {
                            i4 = i10;
                            i19 = i12;
                            i20 = i11;
                        }
                    }
                }
            }
            i16 = zzaj.zzn(b, bArr, i4, i2, zzf(t), zzaiVar);
            zzdaVar = this;
            t2 = t;
            bArr2 = bArr;
            i13 = i2;
            zzaiVar2 = zzaiVar;
            i18 = i6;
            i17 = i5;
            unsafe4 = unsafe;
            i15 = 1048575;
            i14 = -1;
        }
        int i32 = i19;
        Unsafe unsafe6 = unsafe4;
        if (i20 != 1048575) {
            unsafe6.putInt(t, i20, i32);
        }
        if (i16 == i2) {
            return i16;
        }
        throw zzcc.zzf();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static boolean zzz(Object obj, int i, zzdi zzdiVar) {
        return zzdiVar.zzj(zzeg.zzn(obj, i & 1048575));
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final T zza() {
        return (T) ((zzbs) this.zzg).zzG(4, null, null);
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final boolean zzb(T t, T t2) {
        boolean z;
        int length = this.zzc.length;
        for (int i = 0; i < length; i += 3) {
            int zzA = zzA(i);
            long j = zzA & 1048575;
            switch (zzC(zzA)) {
                case 0:
                    if (zzI(t, t2, i) && Double.doubleToLongBits(zzeg.zzl(t, j)) == Double.doubleToLongBits(zzeg.zzl(t2, j))) {
                        continue;
                    }
                    return false;
                case 1:
                    if (zzI(t, t2, i) && Float.floatToIntBits(zzeg.zzj(t, j)) == Float.floatToIntBits(zzeg.zzj(t2, j))) {
                        continue;
                    }
                    return false;
                case 2:
                    if (zzI(t, t2, i) && zzeg.zzf(t, j) == zzeg.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 3:
                    if (zzI(t, t2, i) && zzeg.zzf(t, j) == zzeg.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 4:
                    if (zzI(t, t2, i) && zzeg.zzd(t, j) == zzeg.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 5:
                    if (zzI(t, t2, i) && zzeg.zzf(t, j) == zzeg.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 6:
                    if (zzI(t, t2, i) && zzeg.zzd(t, j) == zzeg.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 7:
                    if (zzI(t, t2, i) && zzeg.zzh(t, j) == zzeg.zzh(t2, j)) {
                        continue;
                    }
                    return false;
                case 8:
                    if (zzI(t, t2, i) && zzdk.zzD(zzeg.zzn(t, j), zzeg.zzn(t2, j))) {
                        continue;
                    }
                    return false;
                case 9:
                    if (zzI(t, t2, i) && zzdk.zzD(zzeg.zzn(t, j), zzeg.zzn(t2, j))) {
                        continue;
                    }
                    return false;
                case 10:
                    if (zzI(t, t2, i) && zzdk.zzD(zzeg.zzn(t, j), zzeg.zzn(t2, j))) {
                        continue;
                    }
                    return false;
                case 11:
                    if (zzI(t, t2, i) && zzeg.zzd(t, j) == zzeg.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 12:
                    if (zzI(t, t2, i) && zzeg.zzd(t, j) == zzeg.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 13:
                    if (zzI(t, t2, i) && zzeg.zzd(t, j) == zzeg.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 14:
                    if (zzI(t, t2, i) && zzeg.zzf(t, j) == zzeg.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 15:
                    if (zzI(t, t2, i) && zzeg.zzd(t, j) == zzeg.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 16:
                    if (zzI(t, t2, i) && zzeg.zzf(t, j) == zzeg.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 17:
                    if (zzI(t, t2, i) && zzdk.zzD(zzeg.zzn(t, j), zzeg.zzn(t2, j))) {
                        continue;
                    }
                    return false;
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
                    z = zzdk.zzD(zzeg.zzn(t, j), zzeg.zzn(t2, j));
                    break;
                case 50:
                    z = zzdk.zzD(zzeg.zzn(t, j), zzeg.zzn(t2, j));
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
                    long zzB = zzB(i) & 1048575;
                    if (zzeg.zzd(t, zzB) == zzeg.zzd(t2, zzB) && zzdk.zzD(zzeg.zzn(t, j), zzeg.zzn(t2, j))) {
                        continue;
                    }
                    return false;
                default:
            }
            if (!z) {
                return false;
            }
        }
        if (!this.zzn.zzd(t).equals(this.zzn.zzd(t2))) {
            return false;
        }
        if (this.zzh) {
            this.zzo.zzb(t);
            this.zzo.zzb(t2);
            throw null;
        }
        return true;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final int zzc(T t) {
        int length = this.zzc.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2 += 3) {
            int zzA = zzA(i2);
            int i3 = this.zzc[i2];
            long j = 1048575 & zzA;
            int i4 = 37;
            switch (zzC(zzA)) {
                case 0:
                    i = (i * 53) + zzca.zze(Double.doubleToLongBits(zzeg.zzl(t, j)));
                    break;
                case 1:
                    i = (i * 53) + Float.floatToIntBits(zzeg.zzj(t, j));
                    break;
                case 2:
                    i = (i * 53) + zzca.zze(zzeg.zzf(t, j));
                    break;
                case 3:
                    i = (i * 53) + zzca.zze(zzeg.zzf(t, j));
                    break;
                case 4:
                    i = (i * 53) + zzeg.zzd(t, j);
                    break;
                case 5:
                    i = (i * 53) + zzca.zze(zzeg.zzf(t, j));
                    break;
                case 6:
                    i = (i * 53) + zzeg.zzd(t, j);
                    break;
                case 7:
                    i = (i * 53) + zzca.zzf(zzeg.zzh(t, j));
                    break;
                case 8:
                    i = (i * 53) + ((String) zzeg.zzn(t, j)).hashCode();
                    break;
                case 9:
                    Object zzn = zzeg.zzn(t, j);
                    if (zzn != null) {
                        i4 = zzn.hashCode();
                    }
                    i = (i * 53) + i4;
                    break;
                case 10:
                    i = (i * 53) + zzeg.zzn(t, j).hashCode();
                    break;
                case 11:
                    i = (i * 53) + zzeg.zzd(t, j);
                    break;
                case 12:
                    i = (i * 53) + zzeg.zzd(t, j);
                    break;
                case 13:
                    i = (i * 53) + zzeg.zzd(t, j);
                    break;
                case 14:
                    i = (i * 53) + zzca.zze(zzeg.zzf(t, j));
                    break;
                case 15:
                    i = (i * 53) + zzeg.zzd(t, j);
                    break;
                case 16:
                    i = (i * 53) + zzca.zze(zzeg.zzf(t, j));
                    break;
                case 17:
                    Object zzn2 = zzeg.zzn(t, j);
                    if (zzn2 != null) {
                        i4 = zzn2.hashCode();
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
                    i = (i * 53) + zzeg.zzn(t, j).hashCode();
                    break;
                case 50:
                    i = (i * 53) + zzeg.zzn(t, j).hashCode();
                    break;
                case 51:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzca.zze(Double.doubleToLongBits(zzD(t, j)));
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + Float.floatToIntBits(zzE(t, j));
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzca.zze(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzca.zze(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzF(t, j);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzca.zze(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzF(t, j);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzca.zzf(zzH(t, j));
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + ((String) zzeg.zzn(t, j)).hashCode();
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzeg.zzn(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzeg.zzn(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzF(t, j);
                        break;
                    } else {
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzF(t, j);
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzF(t, j);
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzca.zze(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzF(t, j);
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzca.zze(zzG(t, j));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zzM(t, i3, i2)) {
                        i = (i * 53) + zzeg.zzn(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
            }
        }
        int hashCode = (i * 53) + this.zzn.zzd(t).hashCode();
        if (!this.zzh) {
            return hashCode;
        }
        this.zzo.zzb(t);
        throw null;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final void zzd(T t, T t2) {
        if (t2 != null) {
            for (int i = 0; i < this.zzc.length; i += 3) {
                int zzA = zzA(i);
                long j = 1048575 & zzA;
                int i2 = this.zzc[i];
                switch (zzC(zzA)) {
                    case 0:
                        if (zzK(t2, i)) {
                            zzeg.zzm(t, j, zzeg.zzl(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 1:
                        if (zzK(t2, i)) {
                            zzeg.zzk(t, j, zzeg.zzj(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        if (zzK(t2, i)) {
                            zzeg.zzg(t, j, zzeg.zzf(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 3:
                        if (zzK(t2, i)) {
                            zzeg.zzg(t, j, zzeg.zzf(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 4:
                        if (zzK(t2, i)) {
                            zzeg.zze(t, j, zzeg.zzd(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 5:
                        if (zzK(t2, i)) {
                            zzeg.zzg(t, j, zzeg.zzf(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 6:
                        if (zzK(t2, i)) {
                            zzeg.zze(t, j, zzeg.zzd(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 7:
                        if (zzK(t2, i)) {
                            zzeg.zzi(t, j, zzeg.zzh(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 8:
                        if (zzK(t2, i)) {
                            zzeg.zzo(t, j, zzeg.zzn(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 9:
                        zzo(t, t2, i);
                        break;
                    case 10:
                        if (zzK(t2, i)) {
                            zzeg.zzo(t, j, zzeg.zzn(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 11:
                        if (zzK(t2, i)) {
                            zzeg.zze(t, j, zzeg.zzd(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 12:
                        if (zzK(t2, i)) {
                            zzeg.zze(t, j, zzeg.zzd(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 13:
                        if (zzK(t2, i)) {
                            zzeg.zze(t, j, zzeg.zzd(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 14:
                        if (zzK(t2, i)) {
                            zzeg.zzg(t, j, zzeg.zzf(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 15:
                        if (zzK(t2, i)) {
                            zzeg.zze(t, j, zzeg.zzd(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 16:
                        if (zzK(t2, i)) {
                            zzeg.zzg(t, j, zzeg.zzf(t2, j));
                            zzL(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 17:
                        zzo(t, t2, i);
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
                        this.zzm.zzb(t, t2, j);
                        break;
                    case 50:
                        zzdk.zzI(this.zzq, t, t2, j);
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
                        if (zzM(t2, i2, i)) {
                            zzeg.zzo(t, j, zzeg.zzn(t2, j));
                            zzN(t, i2, i);
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_PHONE_COPIED /* 60 */:
                        zzp(t, t2, i);
                        break;
                    case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    case 62:
                    case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    case 64:
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                    case 66:
                    case 67:
                        if (zzM(t2, i2, i)) {
                            zzeg.zzo(t, j, zzeg.zzn(t2, j));
                            zzN(t, i2, i);
                            break;
                        } else {
                            break;
                        }
                    case 68:
                        zzp(t, t2, i);
                        break;
                }
            }
            zzdk.zzF(this.zzn, t, t2);
            if (this.zzh) {
                zzdk.zzE(this.zzo, t, t2);
                return;
            }
            return;
        }
        throw null;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final int zze(T t) {
        return this.zzi ? zzr(t) : zzq(t);
    }

    /* JADX WARN: Code restructure failed: missing block: B:149:0x052f, code lost:
        if (r6 == 1048575) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x0531, code lost:
        r28.putInt(r12, r6, r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x0537, code lost:
        r3 = r9.zzk;
     */
    /* JADX WARN: Code restructure failed: missing block: B:153:0x053b, code lost:
        if (r3 >= r9.zzl) goto L260;
     */
    /* JADX WARN: Code restructure failed: missing block: B:154:0x053d, code lost:
        r4 = r9.zzj[r3];
        r5 = r9.zzc[r4];
        r5 = com.google.android.gms.internal.wearable.zzeg.zzn(r12, r9.zzA(r4) & 1048575);
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x054f, code lost:
        if (r5 != null) goto L156;
     */
    /* JADX WARN: Code restructure failed: missing block: B:157:0x0556, code lost:
        if (r9.zzx(r4) != null) goto L259;
     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x0558, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x055b, code lost:
        r5 = (com.google.android.gms.internal.wearable.zzcr) r5;
        r0 = (com.google.android.gms.internal.wearable.zzcq) r9.zzw(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x0563, code lost:
        throw null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:161:0x0564, code lost:
        if (r7 != 0) goto L166;
     */
    /* JADX WARN: Code restructure failed: missing block: B:163:0x0568, code lost:
        if (r0 != r34) goto L164;
     */
    /* JADX WARN: Code restructure failed: missing block: B:165:0x056f, code lost:
        throw com.google.android.gms.internal.wearable.zzcc.zzf();
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x0572, code lost:
        if (r0 > r34) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x0574, code lost:
        if (r1 != r7) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:169:0x0576, code lost:
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:171:0x057c, code lost:
        throw com.google.android.gms.internal.wearable.zzcc.zzf();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final int zzg(T r31, byte[] r32, int r33, int r34, int r35, com.google.android.gms.internal.wearable.zzai r36) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1444
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.wearable.zzda.zzg(java.lang.Object, byte[], int, int, int, com.google.android.gms.internal.wearable.zzai):int");
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final void zzh(T t, byte[] bArr, int i, int i2, zzai zzaiVar) throws IOException {
        if (this.zzi) {
            zzy(t, bArr, i, i2, zzaiVar);
        } else {
            zzg(t, bArr, i, i2, 0, zzaiVar);
        }
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final void zzi(T t) {
        int i;
        int i2 = this.zzk;
        while (true) {
            i = this.zzl;
            if (i2 >= i) {
                break;
            }
            long zzA = zzA(this.zzj[i2]) & 1048575;
            Object zzn = zzeg.zzn(t, zzA);
            if (zzn != null) {
                ((zzcr) zzn).zzd();
                zzeg.zzo(t, zzA, zzn);
            }
            i2++;
        }
        int length = this.zzj.length;
        while (i < length) {
            this.zzm.zza(t, this.zzj[i]);
            i++;
        }
        this.zzn.zze(t);
        if (this.zzh) {
            this.zzo.zzc(t);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.wearable.zzdi
    public final boolean zzj(T t) {
        int i;
        int i2;
        int i3 = 1048575;
        int i4 = 0;
        int i5 = 0;
        while (i5 < this.zzk) {
            int i6 = this.zzj[i5];
            int i7 = this.zzc[i6];
            int zzA = zzA(i6);
            int i8 = this.zzc[i6 + 2];
            int i9 = i8 & 1048575;
            int i10 = 1 << (i8 >>> 20);
            if (i9 == i3) {
                i2 = i3;
                i = i4;
            } else if (i9 != 1048575) {
                i = zzb.getInt(t, i9);
                i2 = i9;
            } else {
                i = i4;
                i2 = i9;
            }
            if ((268435456 & zzA) != 0 && !zzJ(t, i6, i2, i, i10)) {
                return false;
            }
            switch (zzC(zzA)) {
                case 9:
                case 17:
                    if (zzJ(t, i6, i2, i, i10) && !zzz(t, zzA, zzv(i6))) {
                        return false;
                    }
                    break;
                case 27:
                case 49:
                    List list = (List) zzeg.zzn(t, zzA & 1048575);
                    if (!list.isEmpty()) {
                        zzdi zzv = zzv(i6);
                        for (int i11 = 0; i11 < list.size(); i11++) {
                            if (!zzv.zzj(list.get(i11))) {
                                return false;
                            }
                        }
                        continue;
                    } else {
                        continue;
                    }
                case 50:
                    if (!((zzcr) zzeg.zzn(t, zzA & 1048575)).isEmpty()) {
                        zzcq zzcqVar = (zzcq) zzw(i6);
                        throw null;
                    }
                    break;
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                case 68:
                    if (zzM(t, i7, i6) && !zzz(t, zzA, zzv(i6))) {
                        return false;
                    }
                    break;
            }
            i5++;
            i3 = i2;
            i4 = i;
        }
        if (!this.zzh) {
            return true;
        }
        this.zzo.zzb(t);
        throw null;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final void zzm(T t, zzbc zzbcVar) throws IOException {
        if (!this.zzi) {
            zzR(t, zzbcVar);
        } else if (!this.zzh) {
            int length = this.zzc.length;
            for (int i = 0; i < length; i += 3) {
                int zzA = zzA(i);
                int i2 = this.zzc[i];
                switch (zzC(zzA)) {
                    case 0:
                        if (zzK(t, i)) {
                            zzbcVar.zzf(i2, zzeg.zzl(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 1:
                        if (zzK(t, i)) {
                            zzbcVar.zze(i2, zzeg.zzj(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        if (zzK(t, i)) {
                            zzbcVar.zzc(i2, zzeg.zzf(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 3:
                        if (zzK(t, i)) {
                            zzbcVar.zzh(i2, zzeg.zzf(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 4:
                        if (zzK(t, i)) {
                            zzbcVar.zzi(i2, zzeg.zzd(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 5:
                        if (zzK(t, i)) {
                            zzbcVar.zzj(i2, zzeg.zzf(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 6:
                        if (zzK(t, i)) {
                            zzbcVar.zzk(i2, zzeg.zzd(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 7:
                        if (zzK(t, i)) {
                            zzbcVar.zzl(i2, zzeg.zzh(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 8:
                        if (zzK(t, i)) {
                            zzT(i2, zzeg.zzn(t, zzA & 1048575), zzbcVar);
                            break;
                        } else {
                            break;
                        }
                    case 9:
                        if (zzK(t, i)) {
                            zzbcVar.zzr(i2, zzeg.zzn(t, zzA & 1048575), zzv(i));
                            break;
                        } else {
                            break;
                        }
                    case 10:
                        if (zzK(t, i)) {
                            zzbcVar.zzn(i2, (zzau) zzeg.zzn(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 11:
                        if (zzK(t, i)) {
                            zzbcVar.zzo(i2, zzeg.zzd(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 12:
                        if (zzK(t, i)) {
                            zzbcVar.zzg(i2, zzeg.zzd(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 13:
                        if (zzK(t, i)) {
                            zzbcVar.zzb(i2, zzeg.zzd(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 14:
                        if (zzK(t, i)) {
                            zzbcVar.zzd(i2, zzeg.zzf(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 15:
                        if (zzK(t, i)) {
                            zzbcVar.zzp(i2, zzeg.zzd(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 16:
                        if (zzK(t, i)) {
                            zzbcVar.zzq(i2, zzeg.zzf(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 17:
                        if (zzK(t, i)) {
                            zzbcVar.zzs(i2, zzeg.zzn(t, zzA & 1048575), zzv(i));
                            break;
                        } else {
                            break;
                        }
                    case 18:
                        zzdk.zzJ(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 19:
                        zzdk.zzK(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 20:
                        zzdk.zzL(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 21:
                        zzdk.zzM(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 22:
                        zzdk.zzQ(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 23:
                        zzdk.zzO(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 24:
                        zzdk.zzT(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 25:
                        zzdk.zzW(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 26:
                        zzdk.zzX(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar);
                        break;
                    case 27:
                        zzdk.zzZ(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, zzv(i));
                        break;
                    case 28:
                        zzdk.zzY(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar);
                        break;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                        zzdk.zzR(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 30:
                        zzdk.zzV(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 31:
                        zzdk.zzU(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 32:
                        zzdk.zzP(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 33:
                        zzdk.zzS(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 34:
                        zzdk.zzN(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, false);
                        break;
                    case 35:
                        zzdk.zzJ(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 36:
                        zzdk.zzK(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 37:
                        zzdk.zzL(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 38:
                        zzdk.zzM(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 39:
                        zzdk.zzQ(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 40:
                        zzdk.zzO(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 41:
                        zzdk.zzT(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 42:
                        zzdk.zzW(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 43:
                        zzdk.zzR(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 44:
                        zzdk.zzV(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 45:
                        zzdk.zzU(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 46:
                        zzdk.zzP(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 47:
                        zzdk.zzS(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 48:
                        zzdk.zzN(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, true);
                        break;
                    case 49:
                        zzdk.zzaa(this.zzc[i], (List) zzeg.zzn(t, zzA & 1048575), zzbcVar, zzv(i));
                        break;
                    case 50:
                        zzS(zzbcVar, i2, zzeg.zzn(t, zzA & 1048575), i);
                        break;
                    case 51:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzf(i2, zzD(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 52:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zze(i2, zzE(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 53:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzc(i2, zzG(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 54:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzh(i2, zzG(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 55:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzi(i2, zzF(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzj(i2, zzG(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzk(i2, zzF(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_TEXT_COPIED /* 58 */:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzl(i2, zzH(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 59:
                        if (zzM(t, i2, i)) {
                            zzT(i2, zzeg.zzn(t, zzA & 1048575), zzbcVar);
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_PHONE_COPIED /* 60 */:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzr(i2, zzeg.zzn(t, zzA & 1048575), zzv(i));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzn(i2, (zzau) zzeg.zzn(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 62:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzo(i2, zzF(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzg(i2, zzF(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 64:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzb(i2, zzF(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzd(i2, zzG(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 66:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzp(i2, zzF(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 67:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzq(i2, zzG(t, zzA & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 68:
                        if (zzM(t, i2, i)) {
                            zzbcVar.zzs(i2, zzeg.zzn(t, zzA & 1048575), zzv(i));
                            break;
                        } else {
                            break;
                        }
                }
            }
            zzdw<?, ?> zzdwVar = this.zzn;
            zzdwVar.zzi(zzdwVar.zzd(t), zzbcVar);
        } else {
            this.zzo.zzb(t);
            throw null;
        }
    }

    private final <K, V> void zzS(zzbc zzbcVar, int i, Object obj, int i2) throws IOException {
        if (obj == null) {
            return;
        }
        zzcq zzcqVar = (zzcq) zzw(i2);
        throw null;
    }
}
