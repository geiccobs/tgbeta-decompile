package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
final class zzho extends zzhm {
    private final boolean zza = true;
    private final byte[] zzb;
    private int zzc;
    private final int zzd;
    private int zze;
    private int zzf;
    private int zzg;

    public zzho(ByteBuffer byteBuffer, boolean z) {
        super(null);
        this.zzb = byteBuffer.array();
        int arrayOffset = byteBuffer.arrayOffset() + byteBuffer.position();
        this.zzc = arrayOffset;
        this.zzd = arrayOffset;
        this.zze = byteBuffer.arrayOffset() + byteBuffer.limit();
    }

    private final boolean zzu() {
        return this.zzc == this.zze;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final int zza() throws IOException {
        if (zzu()) {
            return Integer.MAX_VALUE;
        }
        int zzv = zzv();
        this.zzf = zzv;
        if (zzv != this.zzg) {
            return zzv >>> 3;
        }
        return Integer.MAX_VALUE;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final int zzb() {
        return this.zzf;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final boolean zzc() throws IOException {
        int i;
        int i2;
        if (zzu() || (i = this.zzf) == (i2 = this.zzg)) {
            return false;
        }
        switch (i & 7) {
            case 0:
                int i3 = this.zze;
                int i4 = this.zzc;
                if (i3 - i4 >= 10) {
                    byte[] bArr = this.zzb;
                    int i5 = 0;
                    while (i5 < 10) {
                        int i6 = i4 + 1;
                        if (bArr[i4] >= 0) {
                            this.zzc = i6;
                            return true;
                        }
                        i5++;
                        i4 = i6;
                    }
                }
                for (int i7 = 0; i7 < 10; i7++) {
                    if (zzy() >= 0) {
                        return true;
                    }
                }
                throw zzjk.zzc();
            case 1:
                zza(8);
                return true;
            case 2:
                zza(zzv());
                return true;
            case 3:
                this.zzg = ((i >>> 3) << 3) | 4;
                while (zza() != Integer.MAX_VALUE && zzc()) {
                }
                if (this.zzf != this.zzg) {
                    throw zzjk.zzg();
                }
                this.zzg = i2;
                return true;
            case 4:
            default:
                throw zzjk.zzf();
            case 5:
                zza(4);
                return true;
        }
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final double zzd() throws IOException {
        zzc(1);
        return Double.longBitsToDouble(zzaa());
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final float zze() throws IOException {
        zzc(5);
        return Float.intBitsToFloat(zzz());
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final long zzf() throws IOException {
        zzc(0);
        return zzw();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final long zzg() throws IOException {
        zzc(0);
        return zzw();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final int zzh() throws IOException {
        zzc(0);
        return zzv();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final long zzi() throws IOException {
        zzc(1);
        return zzaa();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final int zzj() throws IOException {
        zzc(5);
        return zzz();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final boolean zzk() throws IOException {
        zzc(0);
        return zzv() != 0;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final String zzl() throws IOException {
        return zza(false);
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final String zzm() throws IOException {
        return zza(true);
    }

    private final String zza(boolean z) throws IOException {
        zzc(2);
        int zzv = zzv();
        if (zzv == 0) {
            return "";
        }
        zzb(zzv);
        if (z) {
            byte[] bArr = this.zzb;
            int i = this.zzc;
            if (!zzmd.zza(bArr, i, i + zzv)) {
                throw zzjk.zzh();
            }
        }
        String str = new String(this.zzb, this.zzc, zzv, zzjf.zza);
        this.zzc += zzv;
        return str;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final <T> T zza(Class<T> cls, zzio zzioVar) throws IOException {
        zzc(2);
        return (T) zzc(zzky.zza().zza((Class) cls), zzioVar);
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final <T> T zza(zzlc<T> zzlcVar, zzio zzioVar) throws IOException {
        zzc(2);
        return (T) zzc(zzlcVar, zzioVar);
    }

    private final <T> T zzc(zzlc<T> zzlcVar, zzio zzioVar) throws IOException {
        int zzv = zzv();
        zzb(zzv);
        int i = this.zze;
        int i2 = this.zzc + zzv;
        this.zze = i2;
        try {
            T zza = zzlcVar.zza();
            zzlcVar.zza(zza, this, zzioVar);
            zzlcVar.zzc(zza);
            if (this.zzc != i2) {
                throw zzjk.zzg();
            }
            return zza;
        } finally {
            this.zze = i;
        }
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final <T> T zzb(Class<T> cls, zzio zzioVar) throws IOException {
        zzc(3);
        return (T) zzd(zzky.zza().zza((Class) cls), zzioVar);
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final <T> T zzb(zzlc<T> zzlcVar, zzio zzioVar) throws IOException {
        zzc(3);
        return (T) zzd(zzlcVar, zzioVar);
    }

    private final <T> T zzd(zzlc<T> zzlcVar, zzio zzioVar) throws IOException {
        int i = this.zzg;
        this.zzg = ((this.zzf >>> 3) << 3) | 4;
        try {
            T zza = zzlcVar.zza();
            zzlcVar.zza(zza, this, zzioVar);
            zzlcVar.zzc(zza);
            if (this.zzf != this.zzg) {
                throw zzjk.zzg();
            }
            return zza;
        } finally {
            this.zzg = i;
        }
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final zzht zzn() throws IOException {
        zzht zzhtVar;
        zzc(2);
        int zzv = zzv();
        if (zzv == 0) {
            return zzht.zza;
        }
        zzb(zzv);
        if (this.zza) {
            zzhtVar = zzht.zzb(this.zzb, this.zzc, zzv);
        } else {
            zzhtVar = zzht.zza(this.zzb, this.zzc, zzv);
        }
        this.zzc += zzv;
        return zzhtVar;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final int zzo() throws IOException {
        zzc(0);
        return zzv();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final int zzp() throws IOException {
        zzc(0);
        return zzv();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final int zzq() throws IOException {
        zzc(5);
        return zzz();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final long zzr() throws IOException {
        zzc(1);
        return zzaa();
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final int zzs() throws IOException {
        zzc(0);
        return zzif.zze(zzv());
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final long zzt() throws IOException {
        zzc(0);
        return zzif.zza(zzw());
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zza(List<Double> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzin) {
            zzin zzinVar = (zzin) list;
            switch (this.zzf & 7) {
                case 1:
                    break;
                case 2:
                    int zzv = zzv();
                    zzd(zzv);
                    int i3 = this.zzc + zzv;
                    while (this.zzc < i3) {
                        zzinVar.zza(Double.longBitsToDouble(zzac()));
                    }
                    return;
                default:
                    throw zzjk.zzf();
            }
            do {
                zzinVar.zza(zzd());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 1:
                break;
            case 2:
                int zzv2 = zzv();
                zzd(zzv2);
                int i4 = this.zzc + zzv2;
                while (this.zzc < i4) {
                    list.add(Double.valueOf(Double.longBitsToDouble(zzac())));
                }
                return;
            default:
                throw zzjk.zzf();
        }
        do {
            list.add(Double.valueOf(zzd()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzb(List<Float> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzja) {
            zzja zzjaVar = (zzja) list;
            switch (this.zzf & 7) {
                case 2:
                    int zzv = zzv();
                    zze(zzv);
                    int i3 = this.zzc + zzv;
                    while (this.zzc < i3) {
                        zzjaVar.zza(Float.intBitsToFloat(zzab()));
                    }
                    return;
                case 5:
                    break;
                default:
                    throw zzjk.zzf();
            }
            do {
                zzjaVar.zza(zze());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 2:
                int zzv2 = zzv();
                zze(zzv2);
                int i4 = this.zzc + zzv2;
                while (this.zzc < i4) {
                    list.add(Float.valueOf(Float.intBitsToFloat(zzab())));
                }
                return;
            case 5:
                break;
            default:
                throw zzjk.zzf();
        }
        do {
            list.add(Float.valueOf(zze()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzc(List<Long> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjy) {
            zzjy zzjyVar = (zzjy) list;
            switch (this.zzf & 7) {
                case 0:
                    break;
                case 1:
                default:
                    throw zzjk.zzf();
                case 2:
                    int zzv = this.zzc + zzv();
                    while (this.zzc < zzv) {
                        zzjyVar.zza(zzw());
                    }
                    zzf(zzv);
                    return;
            }
            do {
                zzjyVar.zza(zzf());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 0:
                break;
            case 1:
            default:
                throw zzjk.zzf();
            case 2:
                int zzv2 = this.zzc + zzv();
                while (this.zzc < zzv2) {
                    list.add(Long.valueOf(zzw()));
                }
                zzf(zzv2);
                return;
        }
        do {
            list.add(Long.valueOf(zzf()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzd(List<Long> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjy) {
            zzjy zzjyVar = (zzjy) list;
            switch (this.zzf & 7) {
                case 0:
                    break;
                case 1:
                default:
                    throw zzjk.zzf();
                case 2:
                    int zzv = this.zzc + zzv();
                    while (this.zzc < zzv) {
                        zzjyVar.zza(zzw());
                    }
                    zzf(zzv);
                    return;
            }
            do {
                zzjyVar.zza(zzg());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 0:
                break;
            case 1:
            default:
                throw zzjk.zzf();
            case 2:
                int zzv2 = this.zzc + zzv();
                while (this.zzc < zzv2) {
                    list.add(Long.valueOf(zzw()));
                }
                zzf(zzv2);
                return;
        }
        do {
            list.add(Long.valueOf(zzg()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zze(List<Integer> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjd) {
            zzjd zzjdVar = (zzjd) list;
            switch (this.zzf & 7) {
                case 0:
                    break;
                case 1:
                default:
                    throw zzjk.zzf();
                case 2:
                    int zzv = this.zzc + zzv();
                    while (this.zzc < zzv) {
                        zzjdVar.zzc(zzv());
                    }
                    zzf(zzv);
                    return;
            }
            do {
                zzjdVar.zzc(zzh());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 0:
                break;
            case 1:
            default:
                throw zzjk.zzf();
            case 2:
                int zzv2 = this.zzc + zzv();
                while (this.zzc < zzv2) {
                    list.add(Integer.valueOf(zzv()));
                }
                zzf(zzv2);
                return;
        }
        do {
            list.add(Integer.valueOf(zzh()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzf(List<Long> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjy) {
            zzjy zzjyVar = (zzjy) list;
            switch (this.zzf & 7) {
                case 1:
                    break;
                case 2:
                    int zzv = zzv();
                    zzd(zzv);
                    int i3 = this.zzc + zzv;
                    while (this.zzc < i3) {
                        zzjyVar.zza(zzac());
                    }
                    return;
                default:
                    throw zzjk.zzf();
            }
            do {
                zzjyVar.zza(zzi());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 1:
                break;
            case 2:
                int zzv2 = zzv();
                zzd(zzv2);
                int i4 = this.zzc + zzv2;
                while (this.zzc < i4) {
                    list.add(Long.valueOf(zzac()));
                }
                return;
            default:
                throw zzjk.zzf();
        }
        do {
            list.add(Long.valueOf(zzi()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzg(List<Integer> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjd) {
            zzjd zzjdVar = (zzjd) list;
            switch (this.zzf & 7) {
                case 2:
                    int zzv = zzv();
                    zze(zzv);
                    int i3 = this.zzc + zzv;
                    while (this.zzc < i3) {
                        zzjdVar.zzc(zzab());
                    }
                    return;
                case 5:
                    break;
                default:
                    throw zzjk.zzf();
            }
            do {
                zzjdVar.zzc(zzj());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 2:
                int zzv2 = zzv();
                zze(zzv2);
                int i4 = this.zzc + zzv2;
                while (this.zzc < i4) {
                    list.add(Integer.valueOf(zzab()));
                }
                return;
            case 5:
                break;
            default:
                throw zzjk.zzf();
        }
        do {
            list.add(Integer.valueOf(zzj()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzh(List<Boolean> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzhr) {
            zzhr zzhrVar = (zzhr) list;
            switch (this.zzf & 7) {
                case 0:
                    break;
                case 1:
                default:
                    throw zzjk.zzf();
                case 2:
                    int zzv = this.zzc + zzv();
                    while (this.zzc < zzv) {
                        zzhrVar.zza(zzv() != 0);
                    }
                    zzf(zzv);
                    return;
            }
            do {
                zzhrVar.zza(zzk());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 0:
                break;
            case 1:
            default:
                throw zzjk.zzf();
            case 2:
                int zzv2 = this.zzc + zzv();
                while (this.zzc < zzv2) {
                    list.add(Boolean.valueOf(zzv() != 0));
                }
                zzf(zzv2);
                return;
        }
        do {
            list.add(Boolean.valueOf(zzk()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzi(List<String> list) throws IOException {
        zza(list, false);
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzj(List<String> list) throws IOException {
        zza(list, true);
    }

    private final void zza(List<String> list, boolean z) throws IOException {
        int i;
        int i2;
        if ((this.zzf & 7) != 2) {
            throw zzjk.zzf();
        }
        if ((list instanceof zzjv) && !z) {
            zzjv zzjvVar = (zzjv) list;
            do {
                zzjvVar.zza(zzn());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        do {
            list.add(zza(z));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.vision.zzld
    public final <T> void zza(List<T> list, zzlc<T> zzlcVar, zzio zzioVar) throws IOException {
        int i;
        int i2 = this.zzf;
        if ((i2 & 7) != 2) {
            throw zzjk.zzf();
        }
        do {
            list.add(zzc(zzlcVar, zzioVar));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == i2);
        this.zzc = i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.vision.zzld
    public final <T> void zzb(List<T> list, zzlc<T> zzlcVar, zzio zzioVar) throws IOException {
        int i;
        int i2 = this.zzf;
        if ((i2 & 7) != 3) {
            throw zzjk.zzf();
        }
        do {
            list.add(zzd(zzlcVar, zzioVar));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == i2);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzk(List<zzht> list) throws IOException {
        int i;
        if ((this.zzf & 7) != 2) {
            throw zzjk.zzf();
        }
        do {
            list.add(zzn());
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzl(List<Integer> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjd) {
            zzjd zzjdVar = (zzjd) list;
            switch (this.zzf & 7) {
                case 0:
                    break;
                case 1:
                default:
                    throw zzjk.zzf();
                case 2:
                    int zzv = this.zzc + zzv();
                    while (this.zzc < zzv) {
                        zzjdVar.zzc(zzv());
                    }
                    return;
            }
            do {
                zzjdVar.zzc(zzo());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 0:
                break;
            case 1:
            default:
                throw zzjk.zzf();
            case 2:
                int zzv2 = this.zzc + zzv();
                while (this.zzc < zzv2) {
                    list.add(Integer.valueOf(zzv()));
                }
                return;
        }
        do {
            list.add(Integer.valueOf(zzo()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzm(List<Integer> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjd) {
            zzjd zzjdVar = (zzjd) list;
            switch (this.zzf & 7) {
                case 0:
                    break;
                case 1:
                default:
                    throw zzjk.zzf();
                case 2:
                    int zzv = this.zzc + zzv();
                    while (this.zzc < zzv) {
                        zzjdVar.zzc(zzv());
                    }
                    return;
            }
            do {
                zzjdVar.zzc(zzp());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 0:
                break;
            case 1:
            default:
                throw zzjk.zzf();
            case 2:
                int zzv2 = this.zzc + zzv();
                while (this.zzc < zzv2) {
                    list.add(Integer.valueOf(zzv()));
                }
                return;
        }
        do {
            list.add(Integer.valueOf(zzp()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzn(List<Integer> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjd) {
            zzjd zzjdVar = (zzjd) list;
            switch (this.zzf & 7) {
                case 2:
                    int zzv = zzv();
                    zze(zzv);
                    int i3 = this.zzc + zzv;
                    while (this.zzc < i3) {
                        zzjdVar.zzc(zzab());
                    }
                    return;
                case 5:
                    break;
                default:
                    throw zzjk.zzf();
            }
            do {
                zzjdVar.zzc(zzq());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 2:
                int zzv2 = zzv();
                zze(zzv2);
                int i4 = this.zzc + zzv2;
                while (this.zzc < i4) {
                    list.add(Integer.valueOf(zzab()));
                }
                return;
            case 5:
                break;
            default:
                throw zzjk.zzf();
        }
        do {
            list.add(Integer.valueOf(zzq()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzo(List<Long> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjy) {
            zzjy zzjyVar = (zzjy) list;
            switch (this.zzf & 7) {
                case 1:
                    break;
                case 2:
                    int zzv = zzv();
                    zzd(zzv);
                    int i3 = this.zzc + zzv;
                    while (this.zzc < i3) {
                        zzjyVar.zza(zzac());
                    }
                    return;
                default:
                    throw zzjk.zzf();
            }
            do {
                zzjyVar.zza(zzr());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 1:
                break;
            case 2:
                int zzv2 = zzv();
                zzd(zzv2);
                int i4 = this.zzc + zzv2;
                while (this.zzc < i4) {
                    list.add(Long.valueOf(zzac()));
                }
                return;
            default:
                throw zzjk.zzf();
        }
        do {
            list.add(Long.valueOf(zzr()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzp(List<Integer> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjd) {
            zzjd zzjdVar = (zzjd) list;
            switch (this.zzf & 7) {
                case 0:
                    break;
                case 1:
                default:
                    throw zzjk.zzf();
                case 2:
                    int zzv = this.zzc + zzv();
                    while (this.zzc < zzv) {
                        zzjdVar.zzc(zzif.zze(zzv()));
                    }
                    return;
            }
            do {
                zzjdVar.zzc(zzs());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 0:
                break;
            case 1:
            default:
                throw zzjk.zzf();
            case 2:
                int zzv2 = this.zzc + zzv();
                while (this.zzc < zzv2) {
                    list.add(Integer.valueOf(zzif.zze(zzv())));
                }
                return;
        }
        do {
            list.add(Integer.valueOf(zzs()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    @Override // com.google.android.gms.internal.vision.zzld
    public final void zzq(List<Long> list) throws IOException {
        int i;
        int i2;
        if (list instanceof zzjy) {
            zzjy zzjyVar = (zzjy) list;
            switch (this.zzf & 7) {
                case 0:
                    break;
                case 1:
                default:
                    throw zzjk.zzf();
                case 2:
                    int zzv = this.zzc + zzv();
                    while (this.zzc < zzv) {
                        zzjyVar.zza(zzif.zza(zzw()));
                    }
                    return;
            }
            do {
                zzjyVar.zza(zzt());
                if (zzu()) {
                    return;
                }
                i2 = this.zzc;
            } while (zzv() == this.zzf);
            this.zzc = i2;
            return;
        }
        switch (this.zzf & 7) {
            case 0:
                break;
            case 1:
            default:
                throw zzjk.zzf();
            case 2:
                int zzv2 = this.zzc + zzv();
                while (this.zzc < zzv2) {
                    list.add(Long.valueOf(zzif.zza(zzw())));
                }
                return;
        }
        do {
            list.add(Long.valueOf(zzt()));
            if (zzu()) {
                return;
            }
            i = this.zzc;
        } while (zzv() == this.zzf);
        this.zzc = i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.vision.zzld
    public final <K, V> void zza(Map<K, V> map, zzkf<K, V> zzkfVar, zzio zzioVar) throws IOException {
        zzc(2);
        int zzv = zzv();
        zzb(zzv);
        int i = this.zze;
        this.zze = this.zzc + zzv;
        try {
            Object obj = zzkfVar.zzb;
            Object obj2 = zzkfVar.zzd;
            while (true) {
                int zza = zza();
                if (zza != Integer.MAX_VALUE) {
                    switch (zza) {
                        case 1:
                            obj = zza(zzkfVar.zza, (Class<?>) null, (zzio) null);
                            break;
                        case 2:
                            obj2 = zza(zzkfVar.zzc, zzkfVar.zzd.getClass(), zzioVar);
                            break;
                        default:
                            try {
                                if (!zzc()) {
                                    throw new zzjk("Unable to parse map entry.");
                                    break;
                                } else {
                                    break;
                                }
                            } catch (zzjn e) {
                                if (!zzc()) {
                                    throw new zzjk("Unable to parse map entry.");
                                }
                                break;
                            }
                    }
                } else {
                    map.put(obj, obj2);
                    return;
                }
            }
        } finally {
            this.zze = i;
        }
    }

    private final Object zza(zzml zzmlVar, Class<?> cls, zzio zzioVar) throws IOException {
        switch (zzhp.zza[zzmlVar.ordinal()]) {
            case 1:
                return Boolean.valueOf(zzk());
            case 2:
                return zzn();
            case 3:
                return Double.valueOf(zzd());
            case 4:
                return Integer.valueOf(zzp());
            case 5:
                return Integer.valueOf(zzj());
            case 6:
                return Long.valueOf(zzi());
            case 7:
                return Float.valueOf(zze());
            case 8:
                return Integer.valueOf(zzh());
            case 9:
                return Long.valueOf(zzg());
            case 10:
                return zza(cls, zzioVar);
            case 11:
                return Integer.valueOf(zzq());
            case 12:
                return Long.valueOf(zzr());
            case 13:
                return Integer.valueOf(zzs());
            case 14:
                return Long.valueOf(zzt());
            case 15:
                return zza(true);
            case 16:
                return Integer.valueOf(zzo());
            case 17:
                return Long.valueOf(zzf());
            default:
                throw new RuntimeException("unsupported field type.");
        }
    }

    private final int zzv() throws IOException {
        int i;
        int i2 = this.zzc;
        int i3 = this.zze;
        if (i3 == i2) {
            throw zzjk.zza();
        }
        byte[] bArr = this.zzb;
        int i4 = i2 + 1;
        byte b = bArr[i2];
        if (b >= 0) {
            this.zzc = i4;
            return b;
        } else if (i3 - i4 < 9) {
            return (int) zzx();
        } else {
            int i5 = i4 + 1;
            int i6 = b ^ (bArr[i4] << 7);
            if (i6 < 0) {
                i = i6 ^ (-128);
            } else {
                int i7 = i5 + 1;
                int i8 = i6 ^ (bArr[i5] << 14);
                if (i8 >= 0) {
                    i = i8 ^ 16256;
                    i5 = i7;
                } else {
                    i5 = i7 + 1;
                    int i9 = i8 ^ (bArr[i7] << 21);
                    if (i9 < 0) {
                        i = i9 ^ (-2080896);
                    } else {
                        int i10 = i5 + 1;
                        byte b2 = bArr[i5];
                        i = (i9 ^ (b2 << 28)) ^ 266354560;
                        if (b2 < 0) {
                            i5 = i10 + 1;
                            if (bArr[i10] < 0) {
                                i10 = i5 + 1;
                                if (bArr[i5] < 0) {
                                    i5 = i10 + 1;
                                    if (bArr[i10] < 0) {
                                        i10 = i5 + 1;
                                        if (bArr[i5] < 0) {
                                            i5 = i10 + 1;
                                            if (bArr[i10] < 0) {
                                                throw zzjk.zzc();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        i5 = i10;
                    }
                }
            }
            this.zzc = i5;
            return i;
        }
    }

    private final long zzw() throws IOException {
        long j;
        int i = this.zzc;
        int i2 = this.zze;
        if (i2 == i) {
            throw zzjk.zza();
        }
        byte[] bArr = this.zzb;
        int i3 = i + 1;
        byte b = bArr[i];
        if (b >= 0) {
            this.zzc = i3;
            return b;
        } else if (i2 - i3 < 9) {
            return zzx();
        } else {
            int i4 = i3 + 1;
            int i5 = b ^ (bArr[i3] << 7);
            if (i5 < 0) {
                j = i5 ^ (-128);
            } else {
                int i6 = i4 + 1;
                int i7 = i5 ^ (bArr[i4] << 14);
                if (i7 >= 0) {
                    i4 = i6;
                    j = i7 ^ 16256;
                } else {
                    i4 = i6 + 1;
                    int i8 = i7 ^ (bArr[i6] << 21);
                    if (i8 < 0) {
                        j = i8 ^ (-2080896);
                    } else {
                        long j2 = i8;
                        int i9 = i4 + 1;
                        long j3 = j2 ^ (bArr[i4] << 28);
                        if (j3 >= 0) {
                            j = 266354560 ^ j3;
                            i4 = i9;
                        } else {
                            i4 = i9 + 1;
                            long j4 = j3 ^ (bArr[i9] << 35);
                            if (j4 < 0) {
                                j = j4 ^ (-34093383808L);
                            } else {
                                int i10 = i4 + 1;
                                long j5 = j4 ^ (bArr[i4] << 42);
                                if (j5 >= 0) {
                                    j = 4363953127296L ^ j5;
                                    i4 = i10;
                                } else {
                                    i4 = i10 + 1;
                                    long j6 = j5 ^ (bArr[i10] << 49);
                                    if (j6 < 0) {
                                        j = j6 ^ (-558586000294016L);
                                    } else {
                                        int i11 = i4 + 1;
                                        long j7 = (j6 ^ (bArr[i4] << 56)) ^ 71499008037633920L;
                                        if (j7 >= 0) {
                                            i4 = i11;
                                            j = j7;
                                        } else {
                                            i4 = i11 + 1;
                                            if (bArr[i11] < 0) {
                                                throw zzjk.zzc();
                                            }
                                            j = j7;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.zzc = i4;
            return j;
        }
    }

    private final long zzx() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte zzy = zzy();
            j |= (zzy & Byte.MAX_VALUE) << i;
            if ((zzy & 128) == 0) {
                return j;
            }
        }
        throw zzjk.zzc();
    }

    private final byte zzy() throws IOException {
        int i = this.zzc;
        if (i == this.zze) {
            throw zzjk.zza();
        }
        byte[] bArr = this.zzb;
        this.zzc = i + 1;
        return bArr[i];
    }

    private final int zzz() throws IOException {
        zzb(4);
        return zzab();
    }

    private final long zzaa() throws IOException {
        zzb(8);
        return zzac();
    }

    private final int zzab() {
        int i = this.zzc;
        byte[] bArr = this.zzb;
        this.zzc = i + 4;
        return ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16);
    }

    private final long zzac() {
        int i = this.zzc;
        byte[] bArr = this.zzb;
        this.zzc = i + 8;
        return ((bArr[i + 7] & 255) << 56) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16) | ((bArr[i + 3] & 255) << 24) | ((bArr[i + 4] & 255) << 32) | ((bArr[i + 5] & 255) << 40) | ((bArr[i + 6] & 255) << 48);
    }

    private final void zza(int i) throws IOException {
        zzb(i);
        this.zzc += i;
    }

    private final void zzb(int i) throws IOException {
        if (i < 0 || i > this.zze - this.zzc) {
            throw zzjk.zza();
        }
    }

    private final void zzc(int i) throws IOException {
        if ((this.zzf & 7) != i) {
            throw zzjk.zzf();
        }
    }

    private final void zzd(int i) throws IOException {
        zzb(i);
        if ((i & 7) != 0) {
            throw zzjk.zzg();
        }
    }

    private final void zze(int i) throws IOException {
        zzb(i);
        if ((i & 3) != 0) {
            throw zzjk.zzg();
        }
    }

    private final void zzf(int i) throws IOException {
        if (this.zzc != i) {
            throw zzjk.zza();
        }
    }
}
