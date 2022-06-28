package com.google.android.gms.wearable.internal;

import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.ChannelIOException;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
public final class zzbn extends OutputStream {
    private final OutputStream zza;
    @Nullable
    private volatile zzaw zzb;

    public zzbn(OutputStream outputStream) {
        this.zza = (OutputStream) Preconditions.checkNotNull(outputStream);
    }

    private final IOException zzb(IOException iOException) {
        zzaw zzawVar = this.zzb;
        if (zzawVar != null) {
            if (Log.isLoggable("ChannelOutputStream", 2)) {
                Log.v("ChannelOutputStream", "Caught IOException, but channel has been closed. Translating to ChannelIOException.", iOException);
            }
            return new ChannelIOException("Channel closed unexpectedly before stream was finished", zzawVar.zza, zzawVar.zzb);
        }
        return iOException;
    }

    public final void zza(zzaw zzawVar) {
        this.zzb = zzawVar;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        try {
            this.zza.close();
        } catch (IOException e) {
            throw zzb(e);
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public final void flush() throws IOException {
        try {
            this.zza.flush();
        } catch (IOException e) {
            throw zzb(e);
        }
    }

    @Override // java.io.OutputStream
    public final void write(int i) throws IOException {
        try {
            this.zza.write(i);
        } catch (IOException e) {
            throw zzb(e);
        }
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr) throws IOException {
        try {
            this.zza.write(bArr);
        } catch (IOException e) {
            throw zzb(e);
        }
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr, int i, int i2) throws IOException {
        try {
            this.zza.write(bArr, i, i2);
        } catch (IOException e) {
            throw zzb(e);
        }
    }
}
