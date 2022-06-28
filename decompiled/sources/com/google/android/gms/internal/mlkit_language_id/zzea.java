package com.google.android.gms.internal.mlkit_language_id;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public abstract class zzea extends zzdk {
    private static final Logger zzb = Logger.getLogger(zzea.class.getName());
    private static final boolean zzc = zzhn.zza();
    zzed zza;

    public static zzea zza(byte[] bArr) {
        return new zza(bArr, 0, bArr.length);
    }

    public abstract int zza();

    public abstract void zza(byte b) throws IOException;

    public abstract void zza(int i) throws IOException;

    public abstract void zza(int i, int i2) throws IOException;

    public abstract void zza(int i, long j) throws IOException;

    public abstract void zza(int i, zzdn zzdnVar) throws IOException;

    public abstract void zza(int i, zzfz zzfzVar) throws IOException;

    public abstract void zza(int i, zzfz zzfzVar, zzgp zzgpVar) throws IOException;

    public abstract void zza(int i, String str) throws IOException;

    public abstract void zza(int i, boolean z) throws IOException;

    public abstract void zza(long j) throws IOException;

    public abstract void zza(zzdn zzdnVar) throws IOException;

    public abstract void zza(zzfz zzfzVar) throws IOException;

    public abstract void zza(String str) throws IOException;

    public abstract void zzb(int i) throws IOException;

    public abstract void zzb(int i, int i2) throws IOException;

    public abstract void zzb(int i, zzdn zzdnVar) throws IOException;

    public abstract void zzb(byte[] bArr, int i, int i2) throws IOException;

    public abstract void zzc(int i, int i2) throws IOException;

    public abstract void zzc(int i, long j) throws IOException;

    public abstract void zzc(long j) throws IOException;

    public abstract void zzd(int i) throws IOException;

    public abstract void zze(int i, int i2) throws IOException;

    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static class zzb extends IOException {
        zzb() {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.");
        }

        zzb(Throwable th) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.", th);
        }

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        zzb(java.lang.String r3, java.lang.Throwable r4) {
            /*
                r2 = this;
                java.lang.String r0 = "CodedOutputStream was writing to a flat byte array and ran out of space.: "
                java.lang.String r0 = java.lang.String.valueOf(r0)
                java.lang.String r3 = java.lang.String.valueOf(r3)
                int r1 = r3.length()
                if (r1 == 0) goto L15
                java.lang.String r3 = r0.concat(r3)
                goto L1a
            L15:
                java.lang.String r3 = new java.lang.String
                r3.<init>(r0)
            L1a:
                r2.<init>(r3, r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.mlkit_language_id.zzea.zzb.<init>(java.lang.String, java.lang.Throwable):void");
        }
    }

    private zzea() {
    }

    public final void zzd(int i, int i2) throws IOException {
        zzc(i, zzm(i2));
    }

    public final void zzb(int i, long j) throws IOException {
        zza(i, zzi(j));
    }

    public final void zza(int i, float f) throws IOException {
        zze(i, Float.floatToRawIntBits(f));
    }

    public final void zza(int i, double d) throws IOException {
        zzc(i, Double.doubleToRawLongBits(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.mlkit:language-id@@16.1.1 */
    /* loaded from: classes3.dex */
    public static class zza extends zzea {
        private final byte[] zzb;
        private final int zzc;
        private final int zzd;
        private int zze;

        zza(byte[] bArr, int i, int i2) {
            super();
            if (bArr == null) {
                throw new NullPointerException("buffer");
            }
            if ((i2 | 0 | (bArr.length - i2)) < 0) {
                throw new IllegalArgumentException(String.format("Array range is invalid. Buffer.length=%d, offset=%d, length=%d", Integer.valueOf(bArr.length), 0, Integer.valueOf(i2)));
            }
            this.zzb = bArr;
            this.zzc = 0;
            this.zze = 0;
            this.zzd = i2;
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(int i, int i2) throws IOException {
            zzb((i << 3) | i2);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zzb(int i, int i2) throws IOException {
            zza(i, 0);
            zza(i2);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zzc(int i, int i2) throws IOException {
            zza(i, 0);
            zzb(i2);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zze(int i, int i2) throws IOException {
            zza(i, 5);
            zzd(i2);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(int i, long j) throws IOException {
            zza(i, 0);
            zza(j);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zzc(int i, long j) throws IOException {
            zza(i, 1);
            zzc(j);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(int i, boolean z) throws IOException {
            zza(i, 0);
            zza(z ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(int i, String str) throws IOException {
            zza(i, 2);
            zza(str);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(int i, zzdn zzdnVar) throws IOException {
            zza(i, 2);
            zza(zzdnVar);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(zzdn zzdnVar) throws IOException {
            zzb(zzdnVar.zza());
            zzdnVar.zza(this);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zzb(byte[] bArr, int i, int i2) throws IOException {
            zzb(i2);
            zzc(bArr, 0, i2);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(int i, zzfz zzfzVar, zzgp zzgpVar) throws IOException {
            zza(i, 2);
            zzde zzdeVar = (zzde) zzfzVar;
            int zzg = zzdeVar.zzg();
            if (zzg == -1) {
                zzg = zzgpVar.zzd(zzdeVar);
                zzdeVar.zza(zzg);
            }
            zzb(zzg);
            zzgpVar.zza((zzgp) zzfzVar, (zzib) this.zza);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(int i, zzfz zzfzVar) throws IOException {
            zza(1, 3);
            zzc(2, i);
            zza(3, 2);
            zza(zzfzVar);
            zza(1, 4);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zzb(int i, zzdn zzdnVar) throws IOException {
            zza(1, 3);
            zzc(2, i);
            zza(3, zzdnVar);
            zza(1, 4);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(zzfz zzfzVar) throws IOException {
            zzb(zzfzVar.zzj());
            zzfzVar.zza(this);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(byte b) throws IOException {
            try {
                byte[] bArr = this.zzb;
                int i = this.zze;
                this.zze = i + 1;
                bArr[i] = b;
            } catch (IndexOutOfBoundsException e) {
                throw new zzb(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.zze), Integer.valueOf(this.zzd), 1), e);
            }
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(int i) throws IOException {
            if (i >= 0) {
                zzb(i);
            } else {
                zza(i);
            }
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zzb(int i) throws IOException {
            if (zzea.zzc && !zzdl.zza() && zza() >= 5) {
                if ((i & (-128)) == 0) {
                    byte[] bArr = this.zzb;
                    int i2 = this.zze;
                    this.zze = i2 + 1;
                    zzhn.zza(bArr, i2, (byte) i);
                    return;
                }
                byte[] bArr2 = this.zzb;
                int i3 = this.zze;
                this.zze = i3 + 1;
                zzhn.zza(bArr2, i3, (byte) (i | 128));
                int i4 = i >>> 7;
                if ((i4 & (-128)) == 0) {
                    byte[] bArr3 = this.zzb;
                    int i5 = this.zze;
                    this.zze = i5 + 1;
                    zzhn.zza(bArr3, i5, (byte) i4);
                    return;
                }
                byte[] bArr4 = this.zzb;
                int i6 = this.zze;
                this.zze = i6 + 1;
                zzhn.zza(bArr4, i6, (byte) (i4 | 128));
                int i7 = i4 >>> 7;
                if ((i7 & (-128)) == 0) {
                    byte[] bArr5 = this.zzb;
                    int i8 = this.zze;
                    this.zze = i8 + 1;
                    zzhn.zza(bArr5, i8, (byte) i7);
                    return;
                }
                byte[] bArr6 = this.zzb;
                int i9 = this.zze;
                this.zze = i9 + 1;
                zzhn.zza(bArr6, i9, (byte) (i7 | 128));
                int i10 = i7 >>> 7;
                if ((i10 & (-128)) == 0) {
                    byte[] bArr7 = this.zzb;
                    int i11 = this.zze;
                    this.zze = i11 + 1;
                    zzhn.zza(bArr7, i11, (byte) i10);
                    return;
                }
                byte[] bArr8 = this.zzb;
                int i12 = this.zze;
                this.zze = i12 + 1;
                zzhn.zza(bArr8, i12, (byte) (i10 | 128));
                byte[] bArr9 = this.zzb;
                int i13 = this.zze;
                this.zze = i13 + 1;
                zzhn.zza(bArr9, i13, (byte) (i10 >>> 7));
                return;
            }
            while ((i & (-128)) != 0) {
                try {
                    byte[] bArr10 = this.zzb;
                    int i14 = this.zze;
                    this.zze = i14 + 1;
                    bArr10[i14] = (byte) ((i & 127) | 128);
                    i >>>= 7;
                } catch (IndexOutOfBoundsException e) {
                    throw new zzb(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.zze), Integer.valueOf(this.zzd), 1), e);
                }
            }
            byte[] bArr11 = this.zzb;
            int i15 = this.zze;
            this.zze = i15 + 1;
            bArr11[i15] = (byte) i;
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zzd(int i) throws IOException {
            try {
                byte[] bArr = this.zzb;
                int i2 = this.zze;
                int i3 = i2 + 1;
                this.zze = i3;
                bArr[i2] = (byte) i;
                int i4 = i3 + 1;
                this.zze = i4;
                bArr[i3] = (byte) (i >> 8);
                int i5 = i4 + 1;
                this.zze = i5;
                bArr[i4] = (byte) (i >> 16);
                this.zze = i5 + 1;
                bArr[i5] = (byte) (i >>> 24);
            } catch (IndexOutOfBoundsException e) {
                throw new zzb(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.zze), Integer.valueOf(this.zzd), 1), e);
            }
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(long j) throws IOException {
            if (zzea.zzc && zza() >= 10) {
                while ((j & (-128)) != 0) {
                    byte[] bArr = this.zzb;
                    int i = this.zze;
                    this.zze = i + 1;
                    zzhn.zza(bArr, i, (byte) ((((int) j) & 127) | 128));
                    j >>>= 7;
                }
                byte[] bArr2 = this.zzb;
                int i2 = this.zze;
                this.zze = i2 + 1;
                zzhn.zza(bArr2, i2, (byte) j);
                return;
            }
            while ((j & (-128)) != 0) {
                try {
                    byte[] bArr3 = this.zzb;
                    int i3 = this.zze;
                    this.zze = i3 + 1;
                    bArr3[i3] = (byte) ((((int) j) & 127) | 128);
                    j >>>= 7;
                } catch (IndexOutOfBoundsException e) {
                    throw new zzb(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.zze), Integer.valueOf(this.zzd), 1), e);
                }
            }
            byte[] bArr4 = this.zzb;
            int i4 = this.zze;
            this.zze = i4 + 1;
            bArr4[i4] = (byte) j;
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zzc(long j) throws IOException {
            try {
                byte[] bArr = this.zzb;
                int i = this.zze;
                int i2 = i + 1;
                this.zze = i2;
                bArr[i] = (byte) j;
                int i3 = i2 + 1;
                this.zze = i3;
                bArr[i2] = (byte) (j >> 8);
                int i4 = i3 + 1;
                this.zze = i4;
                bArr[i3] = (byte) (j >> 16);
                int i5 = i4 + 1;
                this.zze = i5;
                bArr[i4] = (byte) (j >> 24);
                int i6 = i5 + 1;
                this.zze = i6;
                bArr[i5] = (byte) (j >> 32);
                int i7 = i6 + 1;
                this.zze = i7;
                bArr[i6] = (byte) (j >> 40);
                int i8 = i7 + 1;
                this.zze = i8;
                bArr[i7] = (byte) (j >> 48);
                this.zze = i8 + 1;
                bArr[i8] = (byte) (j >> 56);
            } catch (IndexOutOfBoundsException e) {
                throw new zzb(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.zze), Integer.valueOf(this.zzd), 1), e);
            }
        }

        private final void zzc(byte[] bArr, int i, int i2) throws IOException {
            try {
                System.arraycopy(bArr, i, this.zzb, this.zze, i2);
                this.zze += i2;
            } catch (IndexOutOfBoundsException e) {
                throw new zzb(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.zze), Integer.valueOf(this.zzd), Integer.valueOf(i2)), e);
            }
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzdk
        public final void zza(byte[] bArr, int i, int i2) throws IOException {
            zzc(bArr, i, i2);
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final void zza(String str) throws IOException {
            int i = this.zze;
            try {
                int zzg = zzg(str.length() * 3);
                int zzg2 = zzg(str.length());
                if (zzg2 == zzg) {
                    int i2 = i + zzg2;
                    this.zze = i2;
                    int zza = zzhp.zza(str, this.zzb, i2, zza());
                    this.zze = i;
                    zzb((zza - i) - zzg2);
                    this.zze = zza;
                    return;
                }
                zzb(zzhp.zza(str));
                this.zze = zzhp.zza(str, this.zzb, this.zze, zza());
            } catch (zzhq e) {
                this.zze = i;
                zza(str, e);
            } catch (IndexOutOfBoundsException e2) {
                throw new zzb(e2);
            }
        }

        @Override // com.google.android.gms.internal.mlkit_language_id.zzea
        public final int zza() {
            return this.zzd - this.zze;
        }
    }

    public final void zzc(int i) throws IOException {
        zzb(zzm(i));
    }

    public final void zzb(long j) throws IOException {
        zza(zzi(j));
    }

    public final void zza(float f) throws IOException {
        zzd(Float.floatToRawIntBits(f));
    }

    public final void zza(double d) throws IOException {
        zzc(Double.doubleToRawLongBits(d));
    }

    public final void zza(boolean z) throws IOException {
        zza(z ? (byte) 1 : (byte) 0);
    }

    public static int zzf(int i, int i2) {
        return zzg(i << 3) + zzf(i2);
    }

    public static int zzg(int i, int i2) {
        return zzg(i << 3) + zzg(i2);
    }

    public static int zzh(int i, int i2) {
        return zzg(i << 3) + zzg(zzm(i2));
    }

    public static int zzi(int i, int i2) {
        return zzg(i << 3) + 4;
    }

    public static int zzj(int i, int i2) {
        return zzg(i << 3) + 4;
    }

    public static int zzd(int i, long j) {
        return zzg(i << 3) + zze(j);
    }

    public static int zze(int i, long j) {
        return zzg(i << 3) + zze(j);
    }

    public static int zzf(int i, long j) {
        return zzg(i << 3) + zze(zzi(j));
    }

    public static int zzg(int i, long j) {
        return zzg(i << 3) + 8;
    }

    public static int zzh(int i, long j) {
        return zzg(i << 3) + 8;
    }

    public static int zzb(int i, float f) {
        return zzg(i << 3) + 4;
    }

    public static int zzb(int i, double d) {
        return zzg(i << 3) + 8;
    }

    public static int zzb(int i, boolean z) {
        return zzg(i << 3) + 1;
    }

    public static int zzk(int i, int i2) {
        return zzg(i << 3) + zzf(i2);
    }

    public static int zzb(int i, String str) {
        return zzg(i << 3) + zzb(str);
    }

    public static int zzc(int i, zzdn zzdnVar) {
        int zzg = zzg(i << 3);
        int zza2 = zzdnVar.zza();
        return zzg + zzg(zza2) + zza2;
    }

    public static int zza(int i, zzfe zzfeVar) {
        int zzg = zzg(i << 3);
        int zzb2 = zzfeVar.zzb();
        return zzg + zzg(zzb2) + zzb2;
    }

    public static int zzb(int i, zzfz zzfzVar, zzgp zzgpVar) {
        return zzg(i << 3) + zza(zzfzVar, zzgpVar);
    }

    public static int zzb(int i, zzfz zzfzVar) {
        return (zzg(8) << 1) + zzg(2, i) + zzg(24) + zzb(zzfzVar);
    }

    public static int zzd(int i, zzdn zzdnVar) {
        return (zzg(8) << 1) + zzg(2, i) + zzc(3, zzdnVar);
    }

    public static int zzb(int i, zzfe zzfeVar) {
        return (zzg(8) << 1) + zzg(2, i) + zza(3, zzfeVar);
    }

    public static int zze(int i) {
        return zzg(i << 3);
    }

    public static int zzf(int i) {
        if (i >= 0) {
            return zzg(i);
        }
        return 10;
    }

    public static int zzg(int i) {
        if ((i & (-128)) == 0) {
            return 1;
        }
        if ((i & (-16384)) == 0) {
            return 2;
        }
        if (((-2097152) & i) == 0) {
            return 3;
        }
        if ((i & (-268435456)) == 0) {
            return 4;
        }
        return 5;
    }

    public static int zzh(int i) {
        return zzg(zzm(i));
    }

    public static int zzi(int i) {
        return 4;
    }

    public static int zzj(int i) {
        return 4;
    }

    public static int zzd(long j) {
        return zze(j);
    }

    public static int zze(long j) {
        int i;
        if (((-128) & j) == 0) {
            return 1;
        }
        if (j < 0) {
            return 10;
        }
        if (((-34359738368L) & j) == 0) {
            i = 2;
        } else {
            i = 6;
            j >>>= 28;
        }
        if (((-2097152) & j) != 0) {
            i += 2;
            j >>>= 14;
        }
        if ((j & (-16384)) != 0) {
            return i + 1;
        }
        return i;
    }

    public static int zzf(long j) {
        return zze(zzi(j));
    }

    public static int zzg(long j) {
        return 8;
    }

    public static int zzh(long j) {
        return 8;
    }

    public static int zzb(float f) {
        return 4;
    }

    public static int zzb(double d) {
        return 8;
    }

    public static int zzb(boolean z) {
        return 1;
    }

    public static int zzk(int i) {
        return zzf(i);
    }

    public static int zzb(String str) {
        int i;
        try {
            i = zzhp.zza(str);
        } catch (zzhq e) {
            i = str.getBytes(zzeq.zza).length;
        }
        return zzg(i) + i;
    }

    public static int zza(zzfe zzfeVar) {
        int zzb2 = zzfeVar.zzb();
        return zzg(zzb2) + zzb2;
    }

    public static int zzb(zzdn zzdnVar) {
        int zza2 = zzdnVar.zza();
        return zzg(zza2) + zza2;
    }

    public static int zzb(byte[] bArr) {
        int length = bArr.length;
        return zzg(length) + length;
    }

    public static int zzb(zzfz zzfzVar) {
        int zzj = zzfzVar.zzj();
        return zzg(zzj) + zzj;
    }

    public static int zza(zzfz zzfzVar, zzgp zzgpVar) {
        zzde zzdeVar = (zzde) zzfzVar;
        int zzg = zzdeVar.zzg();
        if (zzg == -1) {
            zzg = zzgpVar.zzd(zzdeVar);
            zzdeVar.zza(zzg);
        }
        return zzg(zzg) + zzg;
    }

    private static int zzm(int i) {
        return (i >> 31) ^ (i << 1);
    }

    private static long zzi(long j) {
        return (j >> 63) ^ (j << 1);
    }

    public final void zzb() {
        if (zza() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    final void zza(String str, zzhq zzhqVar) throws IOException {
        zzb.logp(Level.WARNING, "com.google.protobuf.CodedOutputStream", "inefficientWriteStringNoTag", "Converting ill-formed UTF-16. Your Protocol Buffer will not round trip correctly!", (Throwable) zzhqVar);
        byte[] bytes = str.getBytes(zzeq.zza);
        try {
            zzb(bytes.length);
            zza(bytes, 0, bytes.length);
        } catch (zzb e) {
            throw e;
        } catch (IndexOutOfBoundsException e2) {
            throw new zzb(e2);
        }
    }

    @Deprecated
    public static int zzc(int i, zzfz zzfzVar, zzgp zzgpVar) {
        int zzg = zzg(i << 3) << 1;
        zzde zzdeVar = (zzde) zzfzVar;
        int zzg2 = zzdeVar.zzg();
        if (zzg2 == -1) {
            zzg2 = zzgpVar.zzd(zzdeVar);
            zzdeVar.zza(zzg2);
        }
        return zzg + zzg2;
    }

    @Deprecated
    public static int zzc(zzfz zzfzVar) {
        return zzfzVar.zzj();
    }

    @Deprecated
    public static int zzl(int i) {
        return zzg(i);
    }
}
