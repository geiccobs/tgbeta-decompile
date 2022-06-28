package com.google.android.gms.internal.icing;

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
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzeh<T> implements zzep<T> {
    private static final int[] zza = new int[0];
    private static final Unsafe zzb = zzfn.zzq();
    private final int[] zzc;
    private final Object[] zzd;
    private final zzee zze;
    private final boolean zzf;
    private final boolean zzg;
    private final int[] zzh;
    private final int zzi;
    private final int zzj;
    private final zzds zzk;
    private final zzfd<?, ?> zzl;
    private final zzcq<?> zzm;
    private final zzej zzn;
    private final zzdz zzo;

    /* JADX WARN: Multi-variable type inference failed */
    private zzeh(int[] iArr, int[] iArr2, Object[] objArr, int i, int i2, zzee zzeeVar, boolean z, boolean z2, int[] iArr3, int i3, int i4, zzej zzejVar, zzds zzdsVar, zzfd<?, ?> zzfdVar, zzcq<?> zzcqVar, zzdz zzdzVar) {
        this.zzc = iArr;
        this.zzd = iArr2;
        this.zzg = zzeeVar;
        boolean z3 = false;
        if (zzfdVar != 0 && zzfdVar.zza(i2)) {
            z3 = true;
        }
        this.zzf = z3;
        this.zzh = z2;
        this.zzi = iArr3;
        this.zzj = i3;
        this.zzn = i4;
        this.zzk = zzejVar;
        this.zzl = zzdsVar;
        this.zzm = zzfdVar;
        this.zze = i2;
        this.zzo = zzcqVar;
    }

    private final boolean zzA(T t, int i, int i2, int i3, int i4) {
        if (i2 == 1048575) {
            return zzB(t, i);
        }
        return (i3 & i4) != 0;
    }

    private final boolean zzB(T t, int i) {
        int zzs = zzs(i);
        long j = zzs & 1048575;
        if (j != 1048575) {
            return (zzfn.zzd(t, j) & (1 << (zzs >>> 20))) != 0;
        }
        int zzr = zzr(i);
        long j2 = zzr & 1048575;
        switch (zzt(zzr)) {
            case 0:
                return zzfn.zzl(t, j2) != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            case 1:
                return zzfn.zzj(t, j2) != 0.0f;
            case 2:
                return zzfn.zzf(t, j2) != 0;
            case 3:
                return zzfn.zzf(t, j2) != 0;
            case 4:
                return zzfn.zzd(t, j2) != 0;
            case 5:
                return zzfn.zzf(t, j2) != 0;
            case 6:
                return zzfn.zzd(t, j2) != 0;
            case 7:
                return zzfn.zzh(t, j2);
            case 8:
                Object zzn = zzfn.zzn(t, j2);
                if (zzn instanceof String) {
                    return !((String) zzn).isEmpty();
                } else if (!(zzn instanceof zzcf)) {
                    throw new IllegalArgumentException();
                } else {
                    return !zzcf.zzb.equals(zzn);
                }
            case 9:
                return zzfn.zzn(t, j2) != null;
            case 10:
                return !zzcf.zzb.equals(zzfn.zzn(t, j2));
            case 11:
                return zzfn.zzd(t, j2) != 0;
            case 12:
                return zzfn.zzd(t, j2) != 0;
            case 13:
                return zzfn.zzd(t, j2) != 0;
            case 14:
                return zzfn.zzf(t, j2) != 0;
            case 15:
                return zzfn.zzd(t, j2) != 0;
            case 16:
                return zzfn.zzf(t, j2) != 0;
            case 17:
                return zzfn.zzn(t, j2) != null;
            default:
                throw new IllegalArgumentException();
        }
    }

    private final void zzC(T t, int i) {
        int zzs = zzs(i);
        long j = 1048575 & zzs;
        if (j == 1048575) {
            return;
        }
        zzfn.zze(t, j, (1 << (zzs >>> 20)) | zzfn.zzd(t, j));
    }

    private final boolean zzD(T t, int i, int i2) {
        return zzfn.zzd(t, (long) (zzs(i2) & 1048575)) == i;
    }

    private final void zzE(T t, int i, int i2) {
        zzfn.zze(t, zzs(i2) & 1048575, i);
    }

    private final void zzF(T t, zzcn zzcnVar) throws IOException {
        int i;
        if (this.zzf) {
            this.zzm.zzb(t);
            throw null;
        }
        int length = this.zzc.length;
        Unsafe unsafe = zzb;
        int i2 = 1048575;
        int i3 = 0;
        int i4 = 0;
        int i5 = 1048575;
        while (i3 < length) {
            int zzr = zzr(i3);
            int i6 = this.zzc[i3];
            int zzt = zzt(zzr);
            if (zzt <= 17) {
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
            long j = zzr & i2;
            switch (zzt) {
                case 0:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzf(i6, zzfn.zzl(t, j));
                        break;
                    }
                case 1:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zze(i6, zzfn.zzj(t, j));
                        break;
                    }
                case 2:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzc(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 3:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzh(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 4:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzi(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 5:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzj(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 6:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzk(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 7:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzl(i6, zzfn.zzh(t, j));
                        break;
                    }
                case 8:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzH(i6, unsafe.getObject(t, j), zzcnVar);
                        break;
                    }
                case 9:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzr(i6, unsafe.getObject(t, j), zzo(i3));
                        break;
                    }
                case 10:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzn(i6, (zzcf) unsafe.getObject(t, j));
                        break;
                    }
                case 11:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzo(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 12:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzg(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 13:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzb(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 14:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzd(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 15:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzp(i6, unsafe.getInt(t, j));
                        break;
                    }
                case 16:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzq(i6, unsafe.getLong(t, j));
                        break;
                    }
                case 17:
                    if ((i4 & i) == 0) {
                        break;
                    } else {
                        zzcnVar.zzs(i6, unsafe.getObject(t, j), zzo(i3));
                        break;
                    }
                case 18:
                    zzer.zzH(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 19:
                    zzer.zzI(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 20:
                    zzer.zzJ(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 21:
                    zzer.zzK(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 22:
                    zzer.zzO(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 23:
                    zzer.zzM(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 24:
                    zzer.zzR(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 25:
                    zzer.zzU(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 26:
                    zzer.zzV(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar);
                    break;
                case 27:
                    zzer.zzX(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, zzo(i3));
                    break;
                case 28:
                    zzer.zzW(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    zzer.zzP(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 30:
                    zzer.zzT(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 31:
                    zzer.zzS(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 32:
                    zzer.zzN(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 33:
                    zzer.zzQ(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 34:
                    zzer.zzL(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, false);
                    break;
                case 35:
                    zzer.zzH(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 36:
                    zzer.zzI(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 37:
                    zzer.zzJ(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 38:
                    zzer.zzK(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 39:
                    zzer.zzO(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 40:
                    zzer.zzM(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 41:
                    zzer.zzR(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 42:
                    zzer.zzU(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 43:
                    zzer.zzP(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 44:
                    zzer.zzT(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 45:
                    zzer.zzS(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 46:
                    zzer.zzN(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 47:
                    zzer.zzQ(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 48:
                    zzer.zzL(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, true);
                    break;
                case 49:
                    zzer.zzY(this.zzc[i3], (List) unsafe.getObject(t, j), zzcnVar, zzo(i3));
                    break;
                case 50:
                    zzG(zzcnVar, i6, unsafe.getObject(t, j), i3);
                    break;
                case 51:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzf(i6, zzu(t, j));
                        break;
                    }
                case 52:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zze(i6, zzv(t, j));
                        break;
                    }
                case 53:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzc(i6, zzx(t, j));
                        break;
                    }
                case 54:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzh(i6, zzx(t, j));
                        break;
                    }
                case 55:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzi(i6, zzw(t, j));
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzj(i6, zzx(t, j));
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzk(i6, zzw(t, j));
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzl(i6, zzy(t, j));
                        break;
                    }
                case 59:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzH(i6, unsafe.getObject(t, j), zzcnVar);
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzr(i6, unsafe.getObject(t, j), zzo(i3));
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzn(i6, (zzcf) unsafe.getObject(t, j));
                        break;
                    }
                case 62:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzo(i6, zzw(t, j));
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzg(i6, zzw(t, j));
                        break;
                    }
                case 64:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzb(i6, zzw(t, j));
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzd(i6, zzx(t, j));
                        break;
                    }
                case 66:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzp(i6, zzw(t, j));
                        break;
                    }
                case 67:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzq(i6, zzx(t, j));
                        break;
                    }
                case 68:
                    if (!zzD(t, i6, i3)) {
                        break;
                    } else {
                        zzcnVar.zzs(i6, unsafe.getObject(t, j), zzo(i3));
                        break;
                    }
            }
            i3 += 3;
            i2 = 1048575;
        }
        zzfd<?, ?> zzfdVar = this.zzl;
        zzfdVar.zzg(zzfdVar.zzb(t), zzcnVar);
    }

    private static final void zzH(int i, Object obj, zzcn zzcnVar) throws IOException {
        if (obj instanceof String) {
            zzcnVar.zzm(i, (String) obj);
        } else {
            zzcnVar.zzn(i, (zzcf) obj);
        }
    }

    public static <T> zzeh<T> zzg(Class<T> cls, zzeb zzebVar, zzej zzejVar, zzds zzdsVar, zzfd<?, ?> zzfdVar, zzcq<?> zzcqVar, zzdz zzdzVar) {
        if (zzebVar instanceof zzeo) {
            return zzh((zzeo) zzebVar, zzejVar, zzdsVar, zzfdVar, zzcqVar, zzdzVar);
        }
        zzfa zzfaVar = (zzfa) zzebVar;
        throw null;
    }

    static <T> zzeh<T> zzh(zzeo zzeoVar, zzej zzejVar, zzds zzdsVar, zzfd<?, ?> zzfdVar, zzcq<?> zzcqVar, zzdz zzdzVar) {
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
        boolean z = zzeoVar.zzc() == 2;
        String zzd = zzeoVar.zzd();
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
        Object[] zze = zzeoVar.zze();
        Class<?> cls2 = zzeoVar.zzb().getClass();
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
                    field2 = zzj(cls2, (String) obj);
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
                    field3 = zzj(cls2, (String) obj2);
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
                Field zzj = zzj(cls2, (String) zze[i2]);
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
                    i15 = (int) unsafe.objectFieldOffset(zzj);
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
                            field = zzj(cls2, (String) obj3);
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
                objArr2[i96 + i96 + 1] = zzj.getType();
                i18 = i85;
                i15 = (int) unsafe.objectFieldOffset(zzj);
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
        return new zzeh<>(iArr3, objArr2, i7, i4, zzeoVar.zzb(), z, false, iArr, i3, i61, zzejVar, zzdsVar, zzfdVar, zzcqVar, zzdzVar, null);
    }

    private static Field zzj(Class<?> cls, String str) {
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

    private final void zzk(T t, T t2, int i) {
        long zzr = zzr(i) & 1048575;
        if (!zzB(t2, i)) {
            return;
        }
        Object zzn = zzfn.zzn(t, zzr);
        Object zzn2 = zzfn.zzn(t2, zzr);
        if (zzn == null || zzn2 == null) {
            if (zzn2 == null) {
                return;
            }
            zzfn.zzo(t, zzr, zzn2);
            zzC(t, i);
            return;
        }
        zzfn.zzo(t, zzr, zzdh.zzi(zzn, zzn2));
        zzC(t, i);
    }

    private final void zzl(T t, T t2, int i) {
        Object obj;
        int zzr = zzr(i);
        int i2 = this.zzc[i];
        long j = zzr & 1048575;
        if (!zzD(t2, i2, i)) {
            return;
        }
        if (zzD(t, i2, i)) {
            obj = zzfn.zzn(t, j);
        } else {
            obj = null;
        }
        Object zzn = zzfn.zzn(t2, j);
        if (obj == null || zzn == null) {
            if (zzn == null) {
                return;
            }
            zzfn.zzo(t, j, zzn);
            zzE(t, i2, i);
            return;
        }
        zzfn.zzo(t, j, zzdh.zzi(obj, zzn));
        zzE(t, i2, i);
    }

    private final int zzm(T t) {
        int i;
        Unsafe unsafe = zzb;
        int i2 = 0;
        int i3 = 0;
        int i4 = 1048575;
        for (int i5 = 0; i5 < this.zzc.length; i5 += 3) {
            int zzr = zzr(i5);
            int i6 = this.zzc[i5];
            int zzt = zzt(zzr);
            if (zzt <= 17) {
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
            long j = zzr & 1048575;
            switch (zzt) {
                case 0:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzx(unsafe.getLong(t, j));
                        break;
                    } else {
                        break;
                    }
                case 3:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzx(unsafe.getLong(t, j));
                        break;
                    } else {
                        break;
                    }
                case 4:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzv(unsafe.getInt(t, j));
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 6:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 7:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + 1;
                        break;
                    } else {
                        break;
                    }
                case 8:
                    if ((i3 & i) != 0) {
                        Object object = unsafe.getObject(t, j);
                        if (object instanceof zzcf) {
                            int zzw = zzcm.zzw(i6 << 3);
                            int zzc = ((zzcf) object).zzc();
                            i2 += zzw + zzcm.zzw(zzc) + zzc;
                            break;
                        } else {
                            i2 += zzcm.zzw(i6 << 3) + zzcm.zzy((String) object);
                            break;
                        }
                    } else {
                        break;
                    }
                case 9:
                    if ((i3 & i) != 0) {
                        i2 += zzer.zzw(i6, unsafe.getObject(t, j), zzo(i5));
                        break;
                    } else {
                        break;
                    }
                case 10:
                    if ((i3 & i) != 0) {
                        int zzw2 = zzcm.zzw(i6 << 3);
                        int zzc2 = ((zzcf) unsafe.getObject(t, j)).zzc();
                        i2 += zzw2 + zzcm.zzw(zzc2) + zzc2;
                        break;
                    } else {
                        break;
                    }
                case 11:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzw(unsafe.getInt(t, j));
                        break;
                    } else {
                        break;
                    }
                case 12:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzv(unsafe.getInt(t, j));
                        break;
                    } else {
                        break;
                    }
                case 13:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 14:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 15:
                    if ((i3 & i) != 0) {
                        int i9 = unsafe.getInt(t, j);
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzw((i9 >> 31) ^ (i9 + i9));
                        break;
                    } else {
                        break;
                    }
                case 16:
                    if ((i3 & i) != 0) {
                        long j2 = unsafe.getLong(t, j);
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzx((j2 >> 63) ^ (j2 + j2));
                        break;
                    } else {
                        break;
                    }
                case 17:
                    if ((i3 & i) != 0) {
                        i2 += zzcm.zzE(i6, (zzee) unsafe.getObject(t, j), zzo(i5));
                        break;
                    } else {
                        break;
                    }
                case 18:
                    i2 += zzer.zzs(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 19:
                    i2 += zzer.zzq(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 20:
                    i2 += zzer.zzc(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 21:
                    i2 += zzer.zze(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 22:
                    i2 += zzer.zzk(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 23:
                    i2 += zzer.zzs(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 24:
                    i2 += zzer.zzq(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 25:
                    i2 += zzer.zzu(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 26:
                    i2 += zzer.zzv(i6, (List) unsafe.getObject(t, j));
                    break;
                case 27:
                    i2 += zzer.zzx(i6, (List) unsafe.getObject(t, j), zzo(i5));
                    break;
                case 28:
                    i2 += zzer.zzy(i6, (List) unsafe.getObject(t, j));
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    i2 += zzer.zzm(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 30:
                    i2 += zzer.zzi(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 31:
                    i2 += zzer.zzq(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 32:
                    i2 += zzer.zzs(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 33:
                    i2 += zzer.zzo(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 34:
                    i2 += zzer.zzg(i6, (List) unsafe.getObject(t, j), false);
                    break;
                case 35:
                    int zzr2 = zzer.zzr((List) unsafe.getObject(t, j));
                    if (zzr2 > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzr2) + zzr2;
                        break;
                    } else {
                        break;
                    }
                case 36:
                    int zzp = zzer.zzp((List) unsafe.getObject(t, j));
                    if (zzp > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzp) + zzp;
                        break;
                    } else {
                        break;
                    }
                case 37:
                    int zzb2 = zzer.zzb((List) unsafe.getObject(t, j));
                    if (zzb2 > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzb2) + zzb2;
                        break;
                    } else {
                        break;
                    }
                case 38:
                    int zzd = zzer.zzd((List) unsafe.getObject(t, j));
                    if (zzd > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzd) + zzd;
                        break;
                    } else {
                        break;
                    }
                case 39:
                    int zzj = zzer.zzj((List) unsafe.getObject(t, j));
                    if (zzj > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzj) + zzj;
                        break;
                    } else {
                        break;
                    }
                case 40:
                    int zzr3 = zzer.zzr((List) unsafe.getObject(t, j));
                    if (zzr3 > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzr3) + zzr3;
                        break;
                    } else {
                        break;
                    }
                case 41:
                    int zzp2 = zzer.zzp((List) unsafe.getObject(t, j));
                    if (zzp2 > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzp2) + zzp2;
                        break;
                    } else {
                        break;
                    }
                case 42:
                    int zzt2 = zzer.zzt((List) unsafe.getObject(t, j));
                    if (zzt2 > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzt2) + zzt2;
                        break;
                    } else {
                        break;
                    }
                case 43:
                    int zzl = zzer.zzl((List) unsafe.getObject(t, j));
                    if (zzl > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzl) + zzl;
                        break;
                    } else {
                        break;
                    }
                case 44:
                    int zzh = zzer.zzh((List) unsafe.getObject(t, j));
                    if (zzh > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzh) + zzh;
                        break;
                    } else {
                        break;
                    }
                case 45:
                    int zzp3 = zzer.zzp((List) unsafe.getObject(t, j));
                    if (zzp3 > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzp3) + zzp3;
                        break;
                    } else {
                        break;
                    }
                case 46:
                    int zzr4 = zzer.zzr((List) unsafe.getObject(t, j));
                    if (zzr4 > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzr4) + zzr4;
                        break;
                    } else {
                        break;
                    }
                case 47:
                    int zzn = zzer.zzn((List) unsafe.getObject(t, j));
                    if (zzn > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzn) + zzn;
                        break;
                    } else {
                        break;
                    }
                case 48:
                    int zzf = zzer.zzf((List) unsafe.getObject(t, j));
                    if (zzf > 0) {
                        i2 += zzcm.zzu(i6) + zzcm.zzw(zzf) + zzf;
                        break;
                    } else {
                        break;
                    }
                case 49:
                    i2 += zzer.zzz(i6, (List) unsafe.getObject(t, j), zzo(i5));
                    break;
                case 50:
                    zzdz.zza(i6, unsafe.getObject(t, j), zzp(i5));
                    break;
                case 51:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzx(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzx(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzv(zzw(t, j));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + 1;
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zzD(t, i6, i5)) {
                        Object object2 = unsafe.getObject(t, j);
                        if (object2 instanceof zzcf) {
                            int zzw3 = zzcm.zzw(i6 << 3);
                            int zzc3 = ((zzcf) object2).zzc();
                            i2 += zzw3 + zzcm.zzw(zzc3) + zzc3;
                            break;
                        } else {
                            i2 += zzcm.zzw(i6 << 3) + zzcm.zzy((String) object2);
                            break;
                        }
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zzD(t, i6, i5)) {
                        i2 += zzer.zzw(i6, unsafe.getObject(t, j), zzo(i5));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zzD(t, i6, i5)) {
                        int zzw4 = zzcm.zzw(i6 << 3);
                        int zzc4 = ((zzcf) unsafe.getObject(t, j)).zzc();
                        i2 += zzw4 + zzcm.zzw(zzc4) + zzc4;
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzw(zzw(t, j));
                        break;
                    } else {
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzv(zzw(t, j));
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzw(i6 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zzD(t, i6, i5)) {
                        int zzw5 = zzw(t, j);
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzw((zzw5 >> 31) ^ (zzw5 + zzw5));
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zzD(t, i6, i5)) {
                        long zzx = zzx(t, j);
                        i2 += zzcm.zzw(i6 << 3) + zzcm.zzx((zzx >> 63) ^ (zzx + zzx));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zzD(t, i6, i5)) {
                        i2 += zzcm.zzE(i6, (zzee) unsafe.getObject(t, j), zzo(i5));
                        break;
                    } else {
                        break;
                    }
            }
        }
        zzfd<?, ?> zzfdVar = this.zzl;
        int zzf2 = i2 + zzfdVar.zzf(zzfdVar.zzb(t));
        if (!this.zzf) {
            return zzf2;
        }
        this.zzm.zzb(t);
        throw null;
    }

    private final int zzn(T t) {
        Unsafe unsafe = zzb;
        int i = 0;
        for (int i2 = 0; i2 < this.zzc.length; i2 += 3) {
            int zzr = zzr(i2);
            int zzt = zzt(zzr);
            int i3 = this.zzc[i2];
            long j = zzr & 1048575;
            if (zzt >= zzcv.DOUBLE_LIST_PACKED.zza() && zzt <= zzcv.SINT64_LIST_PACKED.zza()) {
                int i4 = this.zzc[i2 + 2];
            }
            switch (zzt) {
                case 0:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzx(zzfn.zzf(t, j));
                        break;
                    } else {
                        break;
                    }
                case 3:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzx(zzfn.zzf(t, j));
                        break;
                    } else {
                        break;
                    }
                case 4:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzv(zzfn.zzd(t, j));
                        break;
                    } else {
                        break;
                    }
                case 5:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 6:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 7:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + 1;
                        break;
                    } else {
                        break;
                    }
                case 8:
                    if (zzB(t, i2)) {
                        Object zzn = zzfn.zzn(t, j);
                        if (zzn instanceof zzcf) {
                            int zzw = zzcm.zzw(i3 << 3);
                            int zzc = ((zzcf) zzn).zzc();
                            i += zzw + zzcm.zzw(zzc) + zzc;
                            break;
                        } else {
                            i += zzcm.zzw(i3 << 3) + zzcm.zzy((String) zzn);
                            break;
                        }
                    } else {
                        break;
                    }
                case 9:
                    if (zzB(t, i2)) {
                        i += zzer.zzw(i3, zzfn.zzn(t, j), zzo(i2));
                        break;
                    } else {
                        break;
                    }
                case 10:
                    if (zzB(t, i2)) {
                        int zzw2 = zzcm.zzw(i3 << 3);
                        int zzc2 = ((zzcf) zzfn.zzn(t, j)).zzc();
                        i += zzw2 + zzcm.zzw(zzc2) + zzc2;
                        break;
                    } else {
                        break;
                    }
                case 11:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzw(zzfn.zzd(t, j));
                        break;
                    } else {
                        break;
                    }
                case 12:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzv(zzfn.zzd(t, j));
                        break;
                    } else {
                        break;
                    }
                case 13:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 14:
                    if (zzB(t, i2)) {
                        i += zzcm.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 15:
                    if (zzB(t, i2)) {
                        int zzd = zzfn.zzd(t, j);
                        i += zzcm.zzw(i3 << 3) + zzcm.zzw((zzd >> 31) ^ (zzd + zzd));
                        break;
                    } else {
                        break;
                    }
                case 16:
                    if (zzB(t, i2)) {
                        long zzf = zzfn.zzf(t, j);
                        i += zzcm.zzw(i3 << 3) + zzcm.zzx((zzf >> 63) ^ (zzf + zzf));
                        break;
                    } else {
                        break;
                    }
                case 17:
                    if (zzB(t, i2)) {
                        i += zzcm.zzE(i3, (zzee) zzfn.zzn(t, j), zzo(i2));
                        break;
                    } else {
                        break;
                    }
                case 18:
                    i += zzer.zzs(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 19:
                    i += zzer.zzq(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 20:
                    i += zzer.zzc(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 21:
                    i += zzer.zze(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 22:
                    i += zzer.zzk(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 23:
                    i += zzer.zzs(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 24:
                    i += zzer.zzq(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 25:
                    i += zzer.zzu(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 26:
                    i += zzer.zzv(i3, (List) zzfn.zzn(t, j));
                    break;
                case 27:
                    i += zzer.zzx(i3, (List) zzfn.zzn(t, j), zzo(i2));
                    break;
                case 28:
                    i += zzer.zzy(i3, (List) zzfn.zzn(t, j));
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                    i += zzer.zzm(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 30:
                    i += zzer.zzi(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 31:
                    i += zzer.zzq(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 32:
                    i += zzer.zzs(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 33:
                    i += zzer.zzo(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 34:
                    i += zzer.zzg(i3, (List) zzfn.zzn(t, j), false);
                    break;
                case 35:
                    int zzr2 = zzer.zzr((List) unsafe.getObject(t, j));
                    if (zzr2 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzr2) + zzr2;
                        break;
                    } else {
                        break;
                    }
                case 36:
                    int zzp = zzer.zzp((List) unsafe.getObject(t, j));
                    if (zzp > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzp) + zzp;
                        break;
                    } else {
                        break;
                    }
                case 37:
                    int zzb2 = zzer.zzb((List) unsafe.getObject(t, j));
                    if (zzb2 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzb2) + zzb2;
                        break;
                    } else {
                        break;
                    }
                case 38:
                    int zzd2 = zzer.zzd((List) unsafe.getObject(t, j));
                    if (zzd2 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzd2) + zzd2;
                        break;
                    } else {
                        break;
                    }
                case 39:
                    int zzj = zzer.zzj((List) unsafe.getObject(t, j));
                    if (zzj > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzj) + zzj;
                        break;
                    } else {
                        break;
                    }
                case 40:
                    int zzr3 = zzer.zzr((List) unsafe.getObject(t, j));
                    if (zzr3 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzr3) + zzr3;
                        break;
                    } else {
                        break;
                    }
                case 41:
                    int zzp2 = zzer.zzp((List) unsafe.getObject(t, j));
                    if (zzp2 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzp2) + zzp2;
                        break;
                    } else {
                        break;
                    }
                case 42:
                    int zzt2 = zzer.zzt((List) unsafe.getObject(t, j));
                    if (zzt2 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzt2) + zzt2;
                        break;
                    } else {
                        break;
                    }
                case 43:
                    int zzl = zzer.zzl((List) unsafe.getObject(t, j));
                    if (zzl > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzl) + zzl;
                        break;
                    } else {
                        break;
                    }
                case 44:
                    int zzh = zzer.zzh((List) unsafe.getObject(t, j));
                    if (zzh > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzh) + zzh;
                        break;
                    } else {
                        break;
                    }
                case 45:
                    int zzp3 = zzer.zzp((List) unsafe.getObject(t, j));
                    if (zzp3 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzp3) + zzp3;
                        break;
                    } else {
                        break;
                    }
                case 46:
                    int zzr4 = zzer.zzr((List) unsafe.getObject(t, j));
                    if (zzr4 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzr4) + zzr4;
                        break;
                    } else {
                        break;
                    }
                case 47:
                    int zzn2 = zzer.zzn((List) unsafe.getObject(t, j));
                    if (zzn2 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzn2) + zzn2;
                        break;
                    } else {
                        break;
                    }
                case 48:
                    int zzf2 = zzer.zzf((List) unsafe.getObject(t, j));
                    if (zzf2 > 0) {
                        i += zzcm.zzu(i3) + zzcm.zzw(zzf2) + zzf2;
                        break;
                    } else {
                        break;
                    }
                case 49:
                    i += zzer.zzz(i3, (List) zzfn.zzn(t, j), zzo(i2));
                    break;
                case 50:
                    zzdz.zza(i3, zzfn.zzn(t, j), zzp(i2));
                    break;
                case 51:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzx(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzx(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzv(zzw(t, j));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + 1;
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zzD(t, i3, i2)) {
                        Object zzn3 = zzfn.zzn(t, j);
                        if (zzn3 instanceof zzcf) {
                            int zzw3 = zzcm.zzw(i3 << 3);
                            int zzc3 = ((zzcf) zzn3).zzc();
                            i += zzw3 + zzcm.zzw(zzc3) + zzc3;
                            break;
                        } else {
                            i += zzcm.zzw(i3 << 3) + zzcm.zzy((String) zzn3);
                            break;
                        }
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zzD(t, i3, i2)) {
                        i += zzer.zzw(i3, zzfn.zzn(t, j), zzo(i2));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zzD(t, i3, i2)) {
                        int zzw4 = zzcm.zzw(i3 << 3);
                        int zzc4 = ((zzcf) zzfn.zzn(t, j)).zzc();
                        i += zzw4 + zzcm.zzw(zzc4) + zzc4;
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzw(zzw(t, j));
                        break;
                    } else {
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + zzcm.zzv(zzw(t, j));
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + 4;
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzw(i3 << 3) + 8;
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zzD(t, i3, i2)) {
                        int zzw5 = zzw(t, j);
                        i += zzcm.zzw(i3 << 3) + zzcm.zzw((zzw5 >> 31) ^ (zzw5 + zzw5));
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zzD(t, i3, i2)) {
                        long zzx = zzx(t, j);
                        i += zzcm.zzw(i3 << 3) + zzcm.zzx((zzx >> 63) ^ (zzx + zzx));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zzD(t, i3, i2)) {
                        i += zzcm.zzE(i3, (zzee) zzfn.zzn(t, j), zzo(i2));
                        break;
                    } else {
                        break;
                    }
            }
        }
        zzfd<?, ?> zzfdVar = this.zzl;
        return i + zzfdVar.zzf(zzfdVar.zzb(t));
    }

    private final zzep zzo(int i) {
        int i2 = i / 3;
        int i3 = i2 + i2;
        zzep zzepVar = (zzep) this.zzd[i3];
        if (zzepVar != null) {
            return zzepVar;
        }
        zzep<T> zzb2 = zzem.zza().zzb((Class) this.zzd[i3 + 1]);
        this.zzd[i3] = zzb2;
        return zzb2;
    }

    private final Object zzp(int i) {
        int i2 = i / 3;
        return this.zzd[i2 + i2];
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static boolean zzq(Object obj, int i, zzep zzepVar) {
        return zzepVar.zzf(zzfn.zzn(obj, i & 1048575));
    }

    private final int zzr(int i) {
        return this.zzc[i + 1];
    }

    private final int zzs(int i) {
        return this.zzc[i + 2];
    }

    private static int zzt(int i) {
        return (i >>> 20) & 255;
    }

    private static <T> double zzu(T t, long j) {
        return ((Double) zzfn.zzn(t, j)).doubleValue();
    }

    private static <T> float zzv(T t, long j) {
        return ((Float) zzfn.zzn(t, j)).floatValue();
    }

    private static <T> int zzw(T t, long j) {
        return ((Integer) zzfn.zzn(t, j)).intValue();
    }

    private static <T> long zzx(T t, long j) {
        return ((Long) zzfn.zzn(t, j)).longValue();
    }

    private static <T> boolean zzy(T t, long j) {
        return ((Boolean) zzfn.zzn(t, j)).booleanValue();
    }

    private final boolean zzz(T t, T t2, int i) {
        return zzB(t, i) == zzB(t2, i);
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final boolean zza(T t, T t2) {
        boolean z;
        int length = this.zzc.length;
        for (int i = 0; i < length; i += 3) {
            int zzr = zzr(i);
            long j = zzr & 1048575;
            switch (zzt(zzr)) {
                case 0:
                    if (zzz(t, t2, i) && Double.doubleToLongBits(zzfn.zzl(t, j)) == Double.doubleToLongBits(zzfn.zzl(t2, j))) {
                        continue;
                    }
                    return false;
                case 1:
                    if (zzz(t, t2, i) && Float.floatToIntBits(zzfn.zzj(t, j)) == Float.floatToIntBits(zzfn.zzj(t2, j))) {
                        continue;
                    }
                    return false;
                case 2:
                    if (zzz(t, t2, i) && zzfn.zzf(t, j) == zzfn.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 3:
                    if (zzz(t, t2, i) && zzfn.zzf(t, j) == zzfn.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 4:
                    if (zzz(t, t2, i) && zzfn.zzd(t, j) == zzfn.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 5:
                    if (zzz(t, t2, i) && zzfn.zzf(t, j) == zzfn.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 6:
                    if (zzz(t, t2, i) && zzfn.zzd(t, j) == zzfn.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 7:
                    if (zzz(t, t2, i) && zzfn.zzh(t, j) == zzfn.zzh(t2, j)) {
                        continue;
                    }
                    return false;
                case 8:
                    if (zzz(t, t2, i) && zzer.zzD(zzfn.zzn(t, j), zzfn.zzn(t2, j))) {
                        continue;
                    }
                    return false;
                case 9:
                    if (zzz(t, t2, i) && zzer.zzD(zzfn.zzn(t, j), zzfn.zzn(t2, j))) {
                        continue;
                    }
                    return false;
                case 10:
                    if (zzz(t, t2, i) && zzer.zzD(zzfn.zzn(t, j), zzfn.zzn(t2, j))) {
                        continue;
                    }
                    return false;
                case 11:
                    if (zzz(t, t2, i) && zzfn.zzd(t, j) == zzfn.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 12:
                    if (zzz(t, t2, i) && zzfn.zzd(t, j) == zzfn.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 13:
                    if (zzz(t, t2, i) && zzfn.zzd(t, j) == zzfn.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 14:
                    if (zzz(t, t2, i) && zzfn.zzf(t, j) == zzfn.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 15:
                    if (zzz(t, t2, i) && zzfn.zzd(t, j) == zzfn.zzd(t2, j)) {
                        continue;
                    }
                    return false;
                case 16:
                    if (zzz(t, t2, i) && zzfn.zzf(t, j) == zzfn.zzf(t2, j)) {
                        continue;
                    }
                    return false;
                case 17:
                    if (zzz(t, t2, i) && zzer.zzD(zzfn.zzn(t, j), zzfn.zzn(t2, j))) {
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
                    z = zzer.zzD(zzfn.zzn(t, j), zzfn.zzn(t2, j));
                    break;
                case 50:
                    z = zzer.zzD(zzfn.zzn(t, j), zzfn.zzn(t2, j));
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
                    long zzs = zzs(i) & 1048575;
                    if (zzfn.zzd(t, zzs) == zzfn.zzd(t2, zzs) && zzer.zzD(zzfn.zzn(t, j), zzfn.zzn(t2, j))) {
                        continue;
                    }
                    return false;
                default:
            }
            if (!z) {
                return false;
            }
        }
        if (!this.zzl.zzb(t).equals(this.zzl.zzb(t2))) {
            return false;
        }
        if (this.zzf) {
            this.zzm.zzb(t);
            this.zzm.zzb(t2);
            throw null;
        }
        return true;
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final int zzb(T t) {
        int length = this.zzc.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2 += 3) {
            int zzr = zzr(i2);
            int i3 = this.zzc[i2];
            long j = 1048575 & zzr;
            int i4 = 37;
            switch (zzt(zzr)) {
                case 0:
                    i = (i * 53) + zzdh.zze(Double.doubleToLongBits(zzfn.zzl(t, j)));
                    break;
                case 1:
                    i = (i * 53) + Float.floatToIntBits(zzfn.zzj(t, j));
                    break;
                case 2:
                    i = (i * 53) + zzdh.zze(zzfn.zzf(t, j));
                    break;
                case 3:
                    i = (i * 53) + zzdh.zze(zzfn.zzf(t, j));
                    break;
                case 4:
                    i = (i * 53) + zzfn.zzd(t, j);
                    break;
                case 5:
                    i = (i * 53) + zzdh.zze(zzfn.zzf(t, j));
                    break;
                case 6:
                    i = (i * 53) + zzfn.zzd(t, j);
                    break;
                case 7:
                    i = (i * 53) + zzdh.zzf(zzfn.zzh(t, j));
                    break;
                case 8:
                    i = (i * 53) + ((String) zzfn.zzn(t, j)).hashCode();
                    break;
                case 9:
                    Object zzn = zzfn.zzn(t, j);
                    if (zzn != null) {
                        i4 = zzn.hashCode();
                    }
                    i = (i * 53) + i4;
                    break;
                case 10:
                    i = (i * 53) + zzfn.zzn(t, j).hashCode();
                    break;
                case 11:
                    i = (i * 53) + zzfn.zzd(t, j);
                    break;
                case 12:
                    i = (i * 53) + zzfn.zzd(t, j);
                    break;
                case 13:
                    i = (i * 53) + zzfn.zzd(t, j);
                    break;
                case 14:
                    i = (i * 53) + zzdh.zze(zzfn.zzf(t, j));
                    break;
                case 15:
                    i = (i * 53) + zzfn.zzd(t, j);
                    break;
                case 16:
                    i = (i * 53) + zzdh.zze(zzfn.zzf(t, j));
                    break;
                case 17:
                    Object zzn2 = zzfn.zzn(t, j);
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
                    i = (i * 53) + zzfn.zzn(t, j).hashCode();
                    break;
                case 50:
                    i = (i * 53) + zzfn.zzn(t, j).hashCode();
                    break;
                case 51:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzdh.zze(Double.doubleToLongBits(zzu(t, j)));
                        break;
                    } else {
                        break;
                    }
                case 52:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + Float.floatToIntBits(zzv(t, j));
                        break;
                    } else {
                        break;
                    }
                case 53:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzdh.zze(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case 54:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzdh.zze(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case 55:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzw(t, j);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzdh.zze(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzw(t, j);
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_TEXT_COPIED /* 58 */:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzdh.zzf(zzy(t, j));
                        break;
                    } else {
                        break;
                    }
                case 59:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + ((String) zzfn.zzn(t, j)).hashCode();
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzfn.zzn(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
                case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzfn.zzn(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
                case 62:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzw(t, j);
                        break;
                    } else {
                        break;
                    }
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzw(t, j);
                        break;
                    } else {
                        break;
                    }
                case 64:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzw(t, j);
                        break;
                    } else {
                        break;
                    }
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzdh.zze(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case 66:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzw(t, j);
                        break;
                    } else {
                        break;
                    }
                case 67:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzdh.zze(zzx(t, j));
                        break;
                    } else {
                        break;
                    }
                case 68:
                    if (zzD(t, i3, i2)) {
                        i = (i * 53) + zzfn.zzn(t, j).hashCode();
                        break;
                    } else {
                        break;
                    }
            }
        }
        int hashCode = (i * 53) + this.zzl.zzb(t).hashCode();
        if (!this.zzf) {
            return hashCode;
        }
        this.zzm.zzb(t);
        throw null;
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final void zzc(T t, T t2) {
        if (t2 != null) {
            for (int i = 0; i < this.zzc.length; i += 3) {
                int zzr = zzr(i);
                long j = 1048575 & zzr;
                int i2 = this.zzc[i];
                switch (zzt(zzr)) {
                    case 0:
                        if (zzB(t2, i)) {
                            zzfn.zzm(t, j, zzfn.zzl(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 1:
                        if (zzB(t2, i)) {
                            zzfn.zzk(t, j, zzfn.zzj(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        if (zzB(t2, i)) {
                            zzfn.zzg(t, j, zzfn.zzf(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 3:
                        if (zzB(t2, i)) {
                            zzfn.zzg(t, j, zzfn.zzf(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 4:
                        if (zzB(t2, i)) {
                            zzfn.zze(t, j, zzfn.zzd(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 5:
                        if (zzB(t2, i)) {
                            zzfn.zzg(t, j, zzfn.zzf(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 6:
                        if (zzB(t2, i)) {
                            zzfn.zze(t, j, zzfn.zzd(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 7:
                        if (zzB(t2, i)) {
                            zzfn.zzi(t, j, zzfn.zzh(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 8:
                        if (zzB(t2, i)) {
                            zzfn.zzo(t, j, zzfn.zzn(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 9:
                        zzk(t, t2, i);
                        break;
                    case 10:
                        if (zzB(t2, i)) {
                            zzfn.zzo(t, j, zzfn.zzn(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 11:
                        if (zzB(t2, i)) {
                            zzfn.zze(t, j, zzfn.zzd(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 12:
                        if (zzB(t2, i)) {
                            zzfn.zze(t, j, zzfn.zzd(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 13:
                        if (zzB(t2, i)) {
                            zzfn.zze(t, j, zzfn.zzd(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 14:
                        if (zzB(t2, i)) {
                            zzfn.zzg(t, j, zzfn.zzf(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 15:
                        if (zzB(t2, i)) {
                            zzfn.zze(t, j, zzfn.zzd(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 16:
                        if (zzB(t2, i)) {
                            zzfn.zzg(t, j, zzfn.zzf(t2, j));
                            zzC(t, i);
                            break;
                        } else {
                            break;
                        }
                    case 17:
                        zzk(t, t2, i);
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
                        this.zzk.zzb(t, t2, j);
                        break;
                    case 50:
                        zzer.zzG(this.zzo, t, t2, j);
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
                        if (zzD(t2, i2, i)) {
                            zzfn.zzo(t, j, zzfn.zzn(t2, j));
                            zzE(t, i2, i);
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_PHONE_COPIED /* 60 */:
                        zzl(t, t2, i);
                        break;
                    case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                    case 62:
                    case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    case 64:
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                    case 66:
                    case 67:
                        if (zzD(t2, i2, i)) {
                            zzfn.zzo(t, j, zzfn.zzn(t2, j));
                            zzE(t, i2, i);
                            break;
                        } else {
                            break;
                        }
                    case 68:
                        zzl(t, t2, i);
                        break;
                }
            }
            zzer.zzF(this.zzl, t, t2);
            if (this.zzf) {
                zzer.zzE(this.zzm, t, t2);
                return;
            }
            return;
        }
        throw null;
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final int zzd(T t) {
        return this.zzg ? zzn(t) : zzm(t);
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final void zze(T t) {
        int i;
        int i2 = this.zzi;
        while (true) {
            i = this.zzj;
            if (i2 >= i) {
                break;
            }
            long zzr = zzr(this.zzh[i2]) & 1048575;
            Object zzn = zzfn.zzn(t, zzr);
            if (zzn != null) {
                ((zzdy) zzn).zzc();
                zzfn.zzo(t, zzr, zzn);
            }
            i2++;
        }
        int length = this.zzh.length;
        while (i < length) {
            this.zzk.zza(t, this.zzh[i]);
            i++;
        }
        this.zzl.zzc(t);
        if (this.zzf) {
            this.zzm.zzc(t);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.icing.zzep
    public final boolean zzf(T t) {
        int i;
        int i2;
        int i3 = 1048575;
        int i4 = 0;
        int i5 = 0;
        while (i5 < this.zzi) {
            int i6 = this.zzh[i5];
            int i7 = this.zzc[i6];
            int zzr = zzr(i6);
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
            if ((268435456 & zzr) != 0 && !zzA(t, i6, i2, i, i10)) {
                return false;
            }
            switch (zzt(zzr)) {
                case 9:
                case 17:
                    if (zzA(t, i6, i2, i, i10) && !zzq(t, zzr, zzo(i6))) {
                        return false;
                    }
                    break;
                case 27:
                case 49:
                    List list = (List) zzfn.zzn(t, zzr & 1048575);
                    if (!list.isEmpty()) {
                        zzep zzo = zzo(i6);
                        for (int i11 = 0; i11 < list.size(); i11++) {
                            if (!zzo.zzf(list.get(i11))) {
                                return false;
                            }
                        }
                        continue;
                    } else {
                        continue;
                    }
                case 50:
                    if (!((zzdy) zzfn.zzn(t, zzr & 1048575)).isEmpty()) {
                        zzdx zzdxVar = (zzdx) zzp(i6);
                        throw null;
                    }
                    break;
                case UndoView.ACTION_PHONE_COPIED /* 60 */:
                case 68:
                    if (zzD(t, i7, i6) && !zzq(t, zzr, zzo(i6))) {
                        return false;
                    }
                    break;
            }
            i5++;
            i3 = i2;
            i4 = i;
        }
        if (!this.zzf) {
            return true;
        }
        this.zzm.zzb(t);
        throw null;
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final void zzi(T t, zzcn zzcnVar) throws IOException {
        if (!this.zzg) {
            zzF(t, zzcnVar);
        } else if (!this.zzf) {
            int length = this.zzc.length;
            for (int i = 0; i < length; i += 3) {
                int zzr = zzr(i);
                int i2 = this.zzc[i];
                switch (zzt(zzr)) {
                    case 0:
                        if (zzB(t, i)) {
                            zzcnVar.zzf(i2, zzfn.zzl(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 1:
                        if (zzB(t, i)) {
                            zzcnVar.zze(i2, zzfn.zzj(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        if (zzB(t, i)) {
                            zzcnVar.zzc(i2, zzfn.zzf(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 3:
                        if (zzB(t, i)) {
                            zzcnVar.zzh(i2, zzfn.zzf(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 4:
                        if (zzB(t, i)) {
                            zzcnVar.zzi(i2, zzfn.zzd(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 5:
                        if (zzB(t, i)) {
                            zzcnVar.zzj(i2, zzfn.zzf(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 6:
                        if (zzB(t, i)) {
                            zzcnVar.zzk(i2, zzfn.zzd(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 7:
                        if (zzB(t, i)) {
                            zzcnVar.zzl(i2, zzfn.zzh(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 8:
                        if (zzB(t, i)) {
                            zzH(i2, zzfn.zzn(t, zzr & 1048575), zzcnVar);
                            break;
                        } else {
                            break;
                        }
                    case 9:
                        if (zzB(t, i)) {
                            zzcnVar.zzr(i2, zzfn.zzn(t, zzr & 1048575), zzo(i));
                            break;
                        } else {
                            break;
                        }
                    case 10:
                        if (zzB(t, i)) {
                            zzcnVar.zzn(i2, (zzcf) zzfn.zzn(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 11:
                        if (zzB(t, i)) {
                            zzcnVar.zzo(i2, zzfn.zzd(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 12:
                        if (zzB(t, i)) {
                            zzcnVar.zzg(i2, zzfn.zzd(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 13:
                        if (zzB(t, i)) {
                            zzcnVar.zzb(i2, zzfn.zzd(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 14:
                        if (zzB(t, i)) {
                            zzcnVar.zzd(i2, zzfn.zzf(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 15:
                        if (zzB(t, i)) {
                            zzcnVar.zzp(i2, zzfn.zzd(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 16:
                        if (zzB(t, i)) {
                            zzcnVar.zzq(i2, zzfn.zzf(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 17:
                        if (zzB(t, i)) {
                            zzcnVar.zzs(i2, zzfn.zzn(t, zzr & 1048575), zzo(i));
                            break;
                        } else {
                            break;
                        }
                    case 18:
                        zzer.zzH(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 19:
                        zzer.zzI(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 20:
                        zzer.zzJ(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 21:
                        zzer.zzK(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 22:
                        zzer.zzO(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 23:
                        zzer.zzM(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 24:
                        zzer.zzR(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 25:
                        zzer.zzU(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 26:
                        zzer.zzV(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar);
                        break;
                    case 27:
                        zzer.zzX(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, zzo(i));
                        break;
                    case 28:
                        zzer.zzW(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar);
                        break;
                    case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                        zzer.zzP(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 30:
                        zzer.zzT(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 31:
                        zzer.zzS(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 32:
                        zzer.zzN(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 33:
                        zzer.zzQ(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 34:
                        zzer.zzL(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, false);
                        break;
                    case 35:
                        zzer.zzH(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 36:
                        zzer.zzI(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 37:
                        zzer.zzJ(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 38:
                        zzer.zzK(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 39:
                        zzer.zzO(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 40:
                        zzer.zzM(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 41:
                        zzer.zzR(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 42:
                        zzer.zzU(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 43:
                        zzer.zzP(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 44:
                        zzer.zzT(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 45:
                        zzer.zzS(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 46:
                        zzer.zzN(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 47:
                        zzer.zzQ(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 48:
                        zzer.zzL(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, true);
                        break;
                    case 49:
                        zzer.zzY(this.zzc[i], (List) zzfn.zzn(t, zzr & 1048575), zzcnVar, zzo(i));
                        break;
                    case 50:
                        zzG(zzcnVar, i2, zzfn.zzn(t, zzr & 1048575), i);
                        break;
                    case 51:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzf(i2, zzu(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 52:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zze(i2, zzv(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 53:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzc(i2, zzx(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 54:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzh(i2, zzx(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 55:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzi(i2, zzw(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzj(i2, zzx(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzk(i2, zzw(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_TEXT_COPIED /* 58 */:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzl(i2, zzy(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 59:
                        if (zzD(t, i2, i)) {
                            zzH(i2, zzfn.zzn(t, zzr & 1048575), zzcnVar);
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_PHONE_COPIED /* 60 */:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzr(i2, zzfn.zzn(t, zzr & 1048575), zzo(i));
                            break;
                        } else {
                            break;
                        }
                    case UndoView.ACTION_SHARE_BACKGROUND /* 61 */:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzn(i2, (zzcf) zzfn.zzn(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 62:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzo(i2, zzw(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzg(i2, zzw(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 64:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzb(i2, zzw(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzd(i2, zzx(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 66:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzp(i2, zzw(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 67:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzq(i2, zzx(t, zzr & 1048575));
                            break;
                        } else {
                            break;
                        }
                    case 68:
                        if (zzD(t, i2, i)) {
                            zzcnVar.zzs(i2, zzfn.zzn(t, zzr & 1048575), zzo(i));
                            break;
                        } else {
                            break;
                        }
                }
            }
            zzfd<?, ?> zzfdVar = this.zzl;
            zzfdVar.zzg(zzfdVar.zzb(t), zzcnVar);
        } else {
            this.zzm.zzb(t);
            throw null;
        }
    }

    private final <K, V> void zzG(zzcn zzcnVar, int i, Object obj, int i2) throws IOException {
        if (obj == null) {
            return;
        }
        zzdx zzdxVar = (zzdx) zzp(i2);
        throw null;
    }
}
