package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.ChannelIOException;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
public final class zzbl extends InputStream {
    private final InputStream zza;
    @Nullable
    private volatile zzaw zzb;

    public zzbl(InputStream inputStream) {
        this.zza = (InputStream) Preconditions.checkNotNull(inputStream);
    }

    private final int zzb(int i) throws ChannelIOException {
        if (i == -1) {
            zzaw zzawVar = this.zzb;
            if (zzawVar != null) {
                throw new ChannelIOException("Channel closed unexpectedly before stream was finished", zzawVar.zza, zzawVar.zzb);
            }
            return -1;
        }
        return i;
    }

    @Override // java.io.InputStream
    public final int available() throws IOException {
        return this.zza.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        this.zza.close();
    }

    @Override // java.io.InputStream
    public final void mark(int i) {
        this.zza.mark(i);
    }

    @Override // java.io.InputStream
    public final boolean markSupported() {
        return this.zza.markSupported();
    }

    @Override // java.io.InputStream
    public final int read() throws IOException {
        int read = this.zza.read();
        zzb(read);
        return read;
    }

    @Override // java.io.InputStream
    public final void reset() throws IOException {
        this.zza.reset();
    }

    @Override // java.io.InputStream
    public final long skip(long j) throws IOException {
        return this.zza.skip(j);
    }

    public final void zza(zzaw zzawVar) {
        this.zzb = (zzaw) Preconditions.checkNotNull(zzawVar);
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr) throws IOException {
        int read = this.zza.read(bArr);
        zzb(read);
        return read;
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr, int i, int i2) throws IOException {
        int read = this.zza.read(bArr, i, i2);
        zzb(read);
        return read;
    }
}
