package com.google.android.gms.internal.clearcut;

import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes3.dex */
public abstract class zzbn extends zzba {
    private static final Logger logger = Logger.getLogger(zzbn.class.getName());
    private static final boolean zzfy = zzfd.zzed();
    zzbp zzfz;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class zza extends zzbn {
        private final byte[] buffer;
        private final int limit;
        private final int offset;
        private int position;

        zza(byte[] bArr, int i, int i2) {
            super();
            if (bArr != null) {
                int i3 = i + i2;
                if ((i | i2 | (bArr.length - i3)) < 0) {
                    throw new IllegalArgumentException(String.format("Array range is invalid. Buffer.length=%d, offset=%d, length=%d", Integer.valueOf(bArr.length), Integer.valueOf(i), Integer.valueOf(i2)));
                }
                this.buffer = bArr;
                this.offset = i;
                this.position = i;
                this.limit = i3;
                return;
            }
            throw new NullPointerException("buffer");
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public void flush() {
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void write(byte[] bArr, int i, int i2) throws IOException {
            try {
                System.arraycopy(bArr, i, this.buffer, this.position, i2);
                this.position += i2;
            } catch (IndexOutOfBoundsException e) {
                throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(i2)), e);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(byte b) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i = this.position;
                this.position = i + 1;
                bArr[i] = b;
            } catch (IndexOutOfBoundsException e) {
                throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, long j) throws IOException {
            zzb(i, 0);
            zzb(j);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzbb zzbbVar) throws IOException {
            zzb(i, 2);
            zza(zzbbVar);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzdo zzdoVar) throws IOException {
            zzb(i, 2);
            zzb(zzdoVar);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzdo zzdoVar, zzef zzefVar) throws IOException {
            zzb(i, 2);
            zzas zzasVar = (zzas) zzdoVar;
            int zzs = zzasVar.zzs();
            if (zzs == -1) {
                zzs = zzefVar.zzm(zzasVar);
                zzasVar.zzf(zzs);
            }
            zzo(zzs);
            zzefVar.zza(zzdoVar, this.zzfz);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, String str) throws IOException {
            zzb(i, 2);
            zzg(str);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(zzbb zzbbVar) throws IOException {
            zzo(zzbbVar.size());
            zzbbVar.zza(this);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        final void zza(zzdo zzdoVar, zzef zzefVar) throws IOException {
            zzas zzasVar = (zzas) zzdoVar;
            int zzs = zzasVar.zzs();
            if (zzs == -1) {
                zzs = zzefVar.zzm(zzasVar);
                zzasVar.zzf(zzs);
            }
            zzo(zzs);
            zzefVar.zza(zzdoVar, this.zzfz);
        }

        @Override // com.google.android.gms.internal.clearcut.zzba
        public final void zza(byte[] bArr, int i, int i2) throws IOException {
            write(bArr, i, i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final int zzag() {
            return this.limit - this.position;
        }

        public final int zzai() {
            return this.position - this.offset;
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, int i2) throws IOException {
            zzo((i << 3) | i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, zzbb zzbbVar) throws IOException {
            zzb(1, 3);
            zzd(2, i);
            zza(3, zzbbVar);
            zzb(1, 4);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, zzdo zzdoVar) throws IOException {
            zzb(1, 3);
            zzd(2, i);
            zza(3, zzdoVar);
            zzb(1, 4);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, boolean z) throws IOException {
            zzb(i, 0);
            zza(z ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(long j) throws IOException {
            if (zzbn.zzfy && zzag() >= 10) {
                while ((j & (-128)) != 0) {
                    byte[] bArr = this.buffer;
                    int i = this.position;
                    this.position = i + 1;
                    zzfd.zza(bArr, i, (byte) ((((int) j) & 127) | 128));
                    j >>>= 7;
                }
                byte[] bArr2 = this.buffer;
                int i2 = this.position;
                this.position = i2 + 1;
                zzfd.zza(bArr2, i2, (byte) j);
                return;
            }
            while ((j & (-128)) != 0) {
                try {
                    byte[] bArr3 = this.buffer;
                    int i3 = this.position;
                    this.position = i3 + 1;
                    bArr3[i3] = (byte) ((((int) j) & 127) | 128);
                    j >>>= 7;
                } catch (IndexOutOfBoundsException e) {
                    throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
                }
            }
            byte[] bArr4 = this.buffer;
            int i4 = this.position;
            this.position = i4 + 1;
            bArr4[i4] = (byte) j;
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(zzdo zzdoVar) throws IOException {
            zzo(zzdoVar.zzas());
            zzdoVar.zzb(this);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzc(int i, int i2) throws IOException {
            zzb(i, 0);
            zzn(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzc(int i, long j) throws IOException {
            zzb(i, 1);
            zzd(j);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(int i, int i2) throws IOException {
            zzb(i, 0);
            zzo(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(long j) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i = this.position;
                int i2 = i + 1;
                this.position = i2;
                bArr[i] = (byte) j;
                int i3 = i2 + 1;
                this.position = i3;
                bArr[i2] = (byte) (j >> 8);
                int i4 = i3 + 1;
                this.position = i4;
                bArr[i3] = (byte) (j >> 16);
                int i5 = i4 + 1;
                this.position = i5;
                bArr[i4] = (byte) (j >> 24);
                int i6 = i5 + 1;
                this.position = i6;
                bArr[i5] = (byte) (j >> 32);
                int i7 = i6 + 1;
                this.position = i7;
                bArr[i6] = (byte) (j >> 40);
                int i8 = i7 + 1;
                this.position = i8;
                bArr[i7] = (byte) (j >> 48);
                this.position = i8 + 1;
                bArr[i8] = (byte) (j >> 56);
            } catch (IndexOutOfBoundsException e) {
                throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(byte[] bArr, int i, int i2) throws IOException {
            zzo(i2);
            write(bArr, 0, i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzf(int i, int i2) throws IOException {
            zzb(i, 5);
            zzq(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzg(String str) throws IOException {
            int i = this.position;
            try {
                int zzt = zzt(str.length() * 3);
                int zzt2 = zzt(str.length());
                if (zzt2 != zzt) {
                    zzo(zzff.zza(str));
                    this.position = zzff.zza(str, this.buffer, this.position, zzag());
                    return;
                }
                int i2 = i + zzt2;
                this.position = i2;
                int zza = zzff.zza(str, this.buffer, i2, zzag());
                this.position = i;
                zzo((zza - i) - zzt2);
                this.position = zza;
            } catch (zzfi e) {
                this.position = i;
                zza(str, e);
            } catch (IndexOutOfBoundsException e2) {
                throw new zzc(e2);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzn(int i) throws IOException {
            if (i >= 0) {
                zzo(i);
            } else {
                zzb(i);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzo(int i) throws IOException {
            if (zzbn.zzfy && zzag() >= 10) {
                while ((i & (-128)) != 0) {
                    byte[] bArr = this.buffer;
                    int i2 = this.position;
                    this.position = i2 + 1;
                    zzfd.zza(bArr, i2, (byte) ((i & 127) | 128));
                    i >>>= 7;
                }
                byte[] bArr2 = this.buffer;
                int i3 = this.position;
                this.position = i3 + 1;
                zzfd.zza(bArr2, i3, (byte) i);
                return;
            }
            while ((i & (-128)) != 0) {
                try {
                    byte[] bArr3 = this.buffer;
                    int i4 = this.position;
                    this.position = i4 + 1;
                    bArr3[i4] = (byte) ((i & 127) | 128);
                    i >>>= 7;
                } catch (IndexOutOfBoundsException e) {
                    throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
                }
            }
            byte[] bArr4 = this.buffer;
            int i5 = this.position;
            this.position = i5 + 1;
            bArr4[i5] = (byte) i;
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzq(int i) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i2 = this.position;
                int i3 = i2 + 1;
                this.position = i3;
                bArr[i2] = (byte) i;
                int i4 = i3 + 1;
                this.position = i4;
                bArr[i3] = (byte) (i >> 8);
                int i5 = i4 + 1;
                this.position = i5;
                bArr[i4] = (byte) (i >> 16);
                this.position = i5 + 1;
                bArr[i5] = i >> 24;
            } catch (IndexOutOfBoundsException e) {
                throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class zzb extends zza {
        private final ByteBuffer zzga;
        private int zzgb;

        zzb(ByteBuffer byteBuffer) {
            super(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
            this.zzga = byteBuffer;
            this.zzgb = byteBuffer.position();
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn.zza, com.google.android.gms.internal.clearcut.zzbn
        public final void flush() {
            this.zzga.position(this.zzgb + zzai());
        }
    }

    /* loaded from: classes3.dex */
    public static class zzc extends IOException {
        zzc() {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.");
        }

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        zzc(java.lang.String r3) {
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
                r2.<init>(r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.clearcut.zzbn.zzc.<init>(java.lang.String):void");
        }

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        zzc(java.lang.String r3, java.lang.Throwable r4) {
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
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.clearcut.zzbn.zzc.<init>(java.lang.String, java.lang.Throwable):void");
        }

        zzc(Throwable th) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.", th);
        }
    }

    /* loaded from: classes3.dex */
    public static final class zzd extends zzbn {
        private final int zzgb;
        private final ByteBuffer zzgc;
        private final ByteBuffer zzgd;

        zzd(ByteBuffer byteBuffer) {
            super();
            this.zzgc = byteBuffer;
            this.zzgd = byteBuffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
            this.zzgb = byteBuffer.position();
        }

        private final void zzi(String str) throws IOException {
            try {
                zzff.zza(str, this.zzgd);
            } catch (IndexOutOfBoundsException e) {
                throw new zzc(e);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void flush() {
            this.zzgc.position(this.zzgd.position());
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void write(byte[] bArr, int i, int i2) throws IOException {
            try {
                this.zzgd.put(bArr, i, i2);
            } catch (IndexOutOfBoundsException e) {
                throw new zzc(e);
            } catch (BufferOverflowException e2) {
                throw new zzc(e2);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(byte b) throws IOException {
            try {
                this.zzgd.put(b);
            } catch (BufferOverflowException e) {
                throw new zzc(e);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, long j) throws IOException {
            zzb(i, 0);
            zzb(j);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzbb zzbbVar) throws IOException {
            zzb(i, 2);
            zza(zzbbVar);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzdo zzdoVar) throws IOException {
            zzb(i, 2);
            zzb(zzdoVar);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzdo zzdoVar, zzef zzefVar) throws IOException {
            zzb(i, 2);
            zza(zzdoVar, zzefVar);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, String str) throws IOException {
            zzb(i, 2);
            zzg(str);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(zzbb zzbbVar) throws IOException {
            zzo(zzbbVar.size());
            zzbbVar.zza(this);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        final void zza(zzdo zzdoVar, zzef zzefVar) throws IOException {
            zzas zzasVar = (zzas) zzdoVar;
            int zzs = zzasVar.zzs();
            if (zzs == -1) {
                zzs = zzefVar.zzm(zzasVar);
                zzasVar.zzf(zzs);
            }
            zzo(zzs);
            zzefVar.zza(zzdoVar, this.zzfz);
        }

        @Override // com.google.android.gms.internal.clearcut.zzba
        public final void zza(byte[] bArr, int i, int i2) throws IOException {
            write(bArr, i, i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final int zzag() {
            return this.zzgd.remaining();
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, int i2) throws IOException {
            zzo((i << 3) | i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, zzbb zzbbVar) throws IOException {
            zzb(1, 3);
            zzd(2, i);
            zza(3, zzbbVar);
            zzb(1, 4);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, zzdo zzdoVar) throws IOException {
            zzb(1, 3);
            zzd(2, i);
            zza(3, zzdoVar);
            zzb(1, 4);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, boolean z) throws IOException {
            zzb(i, 0);
            zza(z ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(long j) throws IOException {
            while (((-128) & j) != 0) {
                try {
                    this.zzgd.put((byte) ((((int) j) & 127) | 128));
                    j >>>= 7;
                } catch (BufferOverflowException e) {
                    throw new zzc(e);
                }
            }
            this.zzgd.put((byte) j);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(zzdo zzdoVar) throws IOException {
            zzo(zzdoVar.zzas());
            zzdoVar.zzb(this);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzc(int i, int i2) throws IOException {
            zzb(i, 0);
            zzn(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzc(int i, long j) throws IOException {
            zzb(i, 1);
            zzd(j);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(int i, int i2) throws IOException {
            zzb(i, 0);
            zzo(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(long j) throws IOException {
            try {
                this.zzgd.putLong(j);
            } catch (BufferOverflowException e) {
                throw new zzc(e);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(byte[] bArr, int i, int i2) throws IOException {
            zzo(i2);
            write(bArr, 0, i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzf(int i, int i2) throws IOException {
            zzb(i, 5);
            zzq(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzg(String str) throws IOException {
            int position = this.zzgd.position();
            try {
                int zzt = zzt(str.length() * 3);
                int zzt2 = zzt(str.length());
                if (zzt2 != zzt) {
                    zzo(zzff.zza(str));
                    zzi(str);
                    return;
                }
                int position2 = this.zzgd.position() + zzt2;
                this.zzgd.position(position2);
                zzi(str);
                int position3 = this.zzgd.position();
                this.zzgd.position(position);
                zzo(position3 - position2);
                this.zzgd.position(position3);
            } catch (zzfi e) {
                this.zzgd.position(position);
                zza(str, e);
            } catch (IllegalArgumentException e2) {
                throw new zzc(e2);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzn(int i) throws IOException {
            if (i >= 0) {
                zzo(i);
            } else {
                zzb(i);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzo(int i) throws IOException {
            while ((i & (-128)) != 0) {
                try {
                    this.zzgd.put((byte) ((i & 127) | 128));
                    i >>>= 7;
                } catch (BufferOverflowException e) {
                    throw new zzc(e);
                }
            }
            this.zzgd.put((byte) i);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzq(int i) throws IOException {
            try {
                this.zzgd.putInt(i);
            } catch (BufferOverflowException e) {
                throw new zzc(e);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class zze extends zzbn {
        private final ByteBuffer zzgc;
        private final ByteBuffer zzgd;
        private final long zzge;
        private final long zzgf;
        private final long zzgg;
        private final long zzgh;
        private long zzgi;

        zze(ByteBuffer byteBuffer) {
            super();
            this.zzgc = byteBuffer;
            this.zzgd = byteBuffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
            long zzb = zzfd.zzb(byteBuffer);
            this.zzge = zzb;
            long position = byteBuffer.position() + zzb;
            this.zzgf = position;
            long limit = zzb + byteBuffer.limit();
            this.zzgg = limit;
            this.zzgh = limit - 10;
            this.zzgi = position;
        }

        private final void zzk(long j) {
            this.zzgd.position((int) (j - this.zzge));
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void flush() {
            this.zzgc.position((int) (this.zzgi - this.zzge));
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void write(byte[] bArr, int i, int i2) throws IOException {
            if (bArr != null && i >= 0 && i2 >= 0 && bArr.length - i2 >= i) {
                long j = i2;
                long j2 = this.zzgi;
                if (this.zzgg - j >= j2) {
                    zzfd.zza(bArr, i, j2, j);
                    this.zzgi += j;
                    return;
                }
            }
            if (bArr == null) {
                throw new NullPointerException(CommonProperties.VALUE);
            }
            throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Long.valueOf(this.zzgi), Long.valueOf(this.zzgg), Integer.valueOf(i2)));
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(byte b) throws IOException {
            long j = this.zzgi;
            if (j < this.zzgg) {
                this.zzgi = 1 + j;
                zzfd.zza(j, b);
                return;
            }
            throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Long.valueOf(this.zzgi), Long.valueOf(this.zzgg), 1));
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, long j) throws IOException {
            zzb(i, 0);
            zzb(j);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzbb zzbbVar) throws IOException {
            zzb(i, 2);
            zza(zzbbVar);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzdo zzdoVar) throws IOException {
            zzb(i, 2);
            zzb(zzdoVar);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, zzdo zzdoVar, zzef zzefVar) throws IOException {
            zzb(i, 2);
            zza(zzdoVar, zzefVar);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(int i, String str) throws IOException {
            zzb(i, 2);
            zzg(str);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zza(zzbb zzbbVar) throws IOException {
            zzo(zzbbVar.size());
            zzbbVar.zza(this);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        final void zza(zzdo zzdoVar, zzef zzefVar) throws IOException {
            zzas zzasVar = (zzas) zzdoVar;
            int zzs = zzasVar.zzs();
            if (zzs == -1) {
                zzs = zzefVar.zzm(zzasVar);
                zzasVar.zzf(zzs);
            }
            zzo(zzs);
            zzefVar.zza(zzdoVar, this.zzfz);
        }

        @Override // com.google.android.gms.internal.clearcut.zzba
        public final void zza(byte[] bArr, int i, int i2) throws IOException {
            write(bArr, i, i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final int zzag() {
            return (int) (this.zzgg - this.zzgi);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, int i2) throws IOException {
            zzo((i << 3) | i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, zzbb zzbbVar) throws IOException {
            zzb(1, 3);
            zzd(2, i);
            zza(3, zzbbVar);
            zzb(1, 4);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, zzdo zzdoVar) throws IOException {
            zzb(1, 3);
            zzd(2, i);
            zza(3, zzdoVar);
            zzb(1, 4);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(int i, boolean z) throws IOException {
            zzb(i, 0);
            zza(z ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(long j) throws IOException {
            long j2;
            if (this.zzgi <= this.zzgh) {
                while (true) {
                    int i = ((j & (-128)) > 0L ? 1 : ((j & (-128)) == 0L ? 0 : -1));
                    j2 = this.zzgi;
                    if (i == 0) {
                        break;
                    }
                    this.zzgi = j2 + 1;
                    zzfd.zza(j2, (byte) ((((int) j) & 127) | 128));
                    j >>>= 7;
                }
            } else {
                while (true) {
                    j2 = this.zzgi;
                    if (j2 >= this.zzgg) {
                        throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Long.valueOf(this.zzgi), Long.valueOf(this.zzgg), 1));
                    }
                    if ((j & (-128)) == 0) {
                        break;
                    }
                    this.zzgi = j2 + 1;
                    zzfd.zza(j2, (byte) ((((int) j) & 127) | 128));
                    j >>>= 7;
                }
            }
            this.zzgi = 1 + j2;
            zzfd.zza(j2, (byte) j);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzb(zzdo zzdoVar) throws IOException {
            zzo(zzdoVar.zzas());
            zzdoVar.zzb(this);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzc(int i, int i2) throws IOException {
            zzb(i, 0);
            zzn(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzc(int i, long j) throws IOException {
            zzb(i, 1);
            zzd(j);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(int i, int i2) throws IOException {
            zzb(i, 0);
            zzo(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(long j) throws IOException {
            this.zzgd.putLong((int) (this.zzgi - this.zzge), j);
            this.zzgi += 8;
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzd(byte[] bArr, int i, int i2) throws IOException {
            zzo(i2);
            write(bArr, 0, i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzf(int i, int i2) throws IOException {
            zzb(i, 5);
            zzq(i2);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzg(String str) throws IOException {
            long j = this.zzgi;
            try {
                int zzt = zzt(str.length() * 3);
                int zzt2 = zzt(str.length());
                if (zzt2 != zzt) {
                    int zza = zzff.zza(str);
                    zzo(zza);
                    zzk(this.zzgi);
                    zzff.zza(str, this.zzgd);
                    this.zzgi += zza;
                    return;
                }
                int i = ((int) (this.zzgi - this.zzge)) + zzt2;
                this.zzgd.position(i);
                zzff.zza(str, this.zzgd);
                int position = this.zzgd.position() - i;
                zzo(position);
                this.zzgi += position;
            } catch (zzfi e) {
                this.zzgi = j;
                zzk(j);
                zza(str, e);
            } catch (IllegalArgumentException e2) {
                throw new zzc(e2);
            } catch (IndexOutOfBoundsException e3) {
                throw new zzc(e3);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzn(int i) throws IOException {
            if (i >= 0) {
                zzo(i);
            } else {
                zzb(i);
            }
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzo(int i) throws IOException {
            long j;
            if (this.zzgi <= this.zzgh) {
                while ((i & (-128)) != 0) {
                    long j2 = this.zzgi;
                    this.zzgi = j2 + 1;
                    zzfd.zza(j2, (byte) ((i & 127) | 128));
                    i >>>= 7;
                }
                j = this.zzgi;
            } else {
                while (true) {
                    j = this.zzgi;
                    if (j >= this.zzgg) {
                        throw new zzc(String.format("Pos: %d, limit: %d, len: %d", Long.valueOf(this.zzgi), Long.valueOf(this.zzgg), 1));
                    }
                    if ((i & (-128)) == 0) {
                        break;
                    }
                    this.zzgi = j + 1;
                    zzfd.zza(j, (byte) ((i & 127) | 128));
                    i >>>= 7;
                }
            }
            this.zzgi = 1 + j;
            zzfd.zza(j, (byte) i);
        }

        @Override // com.google.android.gms.internal.clearcut.zzbn
        public final void zzq(int i) throws IOException {
            this.zzgd.putInt((int) (this.zzgi - this.zzge), i);
            this.zzgi += 4;
        }
    }

    private zzbn() {
    }

    public static int zza(int i, zzcv zzcvVar) {
        int zzr = zzr(i);
        int zzas = zzcvVar.zzas();
        return zzr + zzt(zzas) + zzas;
    }

    public static int zza(zzcv zzcvVar) {
        int zzas = zzcvVar.zzas();
        return zzt(zzas) + zzas;
    }

    public static zzbn zza(ByteBuffer byteBuffer) {
        if (byteBuffer.hasArray()) {
            return new zzb(byteBuffer);
        }
        if (!byteBuffer.isDirect() || byteBuffer.isReadOnly()) {
            throw new IllegalArgumentException("ByteBuffer is read-only");
        }
        return zzfd.zzee() ? new zze(byteBuffer) : new zzd(byteBuffer);
    }

    public static int zzb(double d) {
        return 8;
    }

    public static int zzb(float f) {
        return 4;
    }

    public static int zzb(int i, double d) {
        return zzr(i) + 8;
    }

    public static int zzb(int i, float f) {
        return zzr(i) + 4;
    }

    public static int zzb(int i, zzcv zzcvVar) {
        return (zzr(1) << 1) + zzh(2, i) + zza(3, zzcvVar);
    }

    public static int zzb(int i, zzdo zzdoVar, zzef zzefVar) {
        return zzr(i) + zzb(zzdoVar, zzefVar);
    }

    public static int zzb(int i, String str) {
        return zzr(i) + zzh(str);
    }

    public static int zzb(zzbb zzbbVar) {
        int size = zzbbVar.size();
        return zzt(size) + size;
    }

    public static int zzb(zzdo zzdoVar, zzef zzefVar) {
        zzas zzasVar = (zzas) zzdoVar;
        int zzs = zzasVar.zzs();
        if (zzs == -1) {
            zzs = zzefVar.zzm(zzasVar);
            zzasVar.zzf(zzs);
        }
        return zzt(zzs) + zzs;
    }

    public static int zzb(boolean z) {
        return 1;
    }

    public static int zzc(int i, zzbb zzbbVar) {
        int zzr = zzr(i);
        int size = zzbbVar.size();
        return zzr + zzt(size) + size;
    }

    public static int zzc(int i, zzdo zzdoVar) {
        return zzr(i) + zzc(zzdoVar);
    }

    @Deprecated
    public static int zzc(int i, zzdo zzdoVar, zzef zzefVar) {
        int zzr = zzr(i) << 1;
        zzas zzasVar = (zzas) zzdoVar;
        int zzs = zzasVar.zzs();
        if (zzs == -1) {
            zzs = zzefVar.zzm(zzasVar);
            zzasVar.zzf(zzs);
        }
        return zzr + zzs;
    }

    public static int zzc(int i, boolean z) {
        return zzr(i) + 1;
    }

    public static int zzc(zzdo zzdoVar) {
        int zzas = zzdoVar.zzas();
        return zzt(zzas) + zzas;
    }

    public static zzbn zzc(byte[] bArr) {
        return new zza(bArr, 0, bArr.length);
    }

    public static int zzd(int i, long j) {
        return zzr(i) + zzf(j);
    }

    public static int zzd(int i, zzbb zzbbVar) {
        return (zzr(1) << 1) + zzh(2, i) + zzc(3, zzbbVar);
    }

    public static int zzd(int i, zzdo zzdoVar) {
        return (zzr(1) << 1) + zzh(2, i) + zzc(3, zzdoVar);
    }

    @Deprecated
    public static int zzd(zzdo zzdoVar) {
        return zzdoVar.zzas();
    }

    public static int zzd(byte[] bArr) {
        int length = bArr.length;
        return zzt(length) + length;
    }

    public static int zze(int i, long j) {
        return zzr(i) + zzf(j);
    }

    public static int zze(long j) {
        return zzf(j);
    }

    public static int zzf(int i, long j) {
        return zzr(i) + zzf(zzj(j));
    }

    public static int zzf(long j) {
        int i;
        if (((-128) & j) == 0) {
            return 1;
        }
        if (j < 0) {
            return 10;
        }
        if (((-34359738368L) & j) != 0) {
            i = 6;
            j >>>= 28;
        } else {
            i = 2;
        }
        if (((-2097152) & j) != 0) {
            i += 2;
            j >>>= 14;
        }
        return (j & (-16384)) != 0 ? i + 1 : i;
    }

    public static int zzg(int i, int i2) {
        return zzr(i) + zzs(i2);
    }

    public static int zzg(int i, long j) {
        return zzr(i) + 8;
    }

    public static int zzg(long j) {
        return zzf(zzj(j));
    }

    public static int zzh(int i, int i2) {
        return zzr(i) + zzt(i2);
    }

    public static int zzh(int i, long j) {
        return zzr(i) + 8;
    }

    public static int zzh(long j) {
        return 8;
    }

    public static int zzh(String str) {
        int i;
        try {
            i = zzff.zza(str);
        } catch (zzfi e) {
            i = str.getBytes(zzci.UTF_8).length;
        }
        return zzt(i) + i;
    }

    public static int zzi(int i, int i2) {
        return zzr(i) + zzt(zzy(i2));
    }

    public static int zzi(long j) {
        return 8;
    }

    public static int zzj(int i, int i2) {
        return zzr(i) + 4;
    }

    private static long zzj(long j) {
        return (j >> 63) ^ (j << 1);
    }

    public static int zzk(int i, int i2) {
        return zzr(i) + 4;
    }

    public static int zzl(int i, int i2) {
        return zzr(i) + zzs(i2);
    }

    public static int zzr(int i) {
        return zzt(i << 3);
    }

    public static int zzs(int i) {
        if (i >= 0) {
            return zzt(i);
        }
        return 10;
    }

    public static int zzt(int i) {
        if ((i & (-128)) == 0) {
            return 1;
        }
        if ((i & (-16384)) == 0) {
            return 2;
        }
        if (((-2097152) & i) == 0) {
            return 3;
        }
        return (i & (-268435456)) == 0 ? 4 : 5;
    }

    public static int zzu(int i) {
        return zzt(zzy(i));
    }

    public static int zzv(int i) {
        return 4;
    }

    public static int zzw(int i) {
        return 4;
    }

    public static int zzx(int i) {
        return zzs(i);
    }

    private static int zzy(int i) {
        return (i >> 31) ^ (i << 1);
    }

    @Deprecated
    public static int zzz(int i) {
        return zzt(i);
    }

    public abstract void flush() throws IOException;

    public abstract void write(byte[] bArr, int i, int i2) throws IOException;

    public abstract void zza(byte b) throws IOException;

    public final void zza(double d) throws IOException {
        zzd(Double.doubleToRawLongBits(d));
    }

    public final void zza(float f) throws IOException {
        zzq(Float.floatToRawIntBits(f));
    }

    public final void zza(int i, double d) throws IOException {
        zzc(i, Double.doubleToRawLongBits(d));
    }

    public final void zza(int i, float f) throws IOException {
        zzf(i, Float.floatToRawIntBits(f));
    }

    public abstract void zza(int i, long j) throws IOException;

    public abstract void zza(int i, zzbb zzbbVar) throws IOException;

    public abstract void zza(int i, zzdo zzdoVar) throws IOException;

    public abstract void zza(int i, zzdo zzdoVar, zzef zzefVar) throws IOException;

    public abstract void zza(int i, String str) throws IOException;

    public abstract void zza(zzbb zzbbVar) throws IOException;

    abstract void zza(zzdo zzdoVar, zzef zzefVar) throws IOException;

    final void zza(String str, zzfi zzfiVar) throws IOException {
        logger.logp(Level.WARNING, "com.google.protobuf.CodedOutputStream", "inefficientWriteStringNoTag", "Converting ill-formed UTF-16. Your Protocol Buffer will not round trip correctly!", (Throwable) zzfiVar);
        byte[] bytes = str.getBytes(zzci.UTF_8);
        try {
            zzo(bytes.length);
            zza(bytes, 0, bytes.length);
        } catch (zzc e) {
            throw e;
        } catch (IndexOutOfBoundsException e2) {
            throw new zzc(e2);
        }
    }

    public final void zza(boolean z) throws IOException {
        zza(z ? (byte) 1 : (byte) 0);
    }

    public abstract int zzag();

    public abstract void zzb(int i, int i2) throws IOException;

    public final void zzb(int i, long j) throws IOException {
        zza(i, zzj(j));
    }

    public abstract void zzb(int i, zzbb zzbbVar) throws IOException;

    public abstract void zzb(int i, zzdo zzdoVar) throws IOException;

    public abstract void zzb(int i, boolean z) throws IOException;

    public abstract void zzb(long j) throws IOException;

    public abstract void zzb(zzdo zzdoVar) throws IOException;

    public abstract void zzc(int i, int i2) throws IOException;

    public abstract void zzc(int i, long j) throws IOException;

    public final void zzc(long j) throws IOException {
        zzb(zzj(j));
    }

    public abstract void zzd(int i, int i2) throws IOException;

    public abstract void zzd(long j) throws IOException;

    public abstract void zzd(byte[] bArr, int i, int i2) throws IOException;

    public final void zze(int i, int i2) throws IOException {
        zzd(i, zzy(i2));
    }

    public abstract void zzf(int i, int i2) throws IOException;

    public abstract void zzg(String str) throws IOException;

    public abstract void zzn(int i) throws IOException;

    public abstract void zzo(int i) throws IOException;

    public final void zzp(int i) throws IOException {
        zzo(zzy(i));
    }

    public abstract void zzq(int i) throws IOException;
}
