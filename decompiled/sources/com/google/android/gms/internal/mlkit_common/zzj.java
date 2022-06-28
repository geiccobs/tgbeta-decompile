package com.google.android.gms.internal.mlkit_common;

import android.os.Build;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.Callable;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzj {
    final long zza;
    final long zzb;
    final boolean zzc;

    /* JADX INFO: Access modifiers changed from: private */
    public zzj(long j, long j2, boolean z) {
        this.zza = j;
        this.zzb = j2;
        this.zzc = z;
    }

    public static zzj zza(FileDescriptor fileDescriptor) throws IOException {
        if (Build.VERSION.SDK_INT >= 21) {
            StructStat structStat = (StructStat) zza(new Callable(fileDescriptor) { // from class: com.google.android.gms.internal.mlkit_common.zzi
                private final FileDescriptor zza;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.zza = fileDescriptor;
                }

                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return Os.fstat(this.zza);
                }
            });
            return new zzj(structStat.st_dev, structStat.st_ino, OsConstants.S_ISLNK(structStat.st_mode));
        }
        return zzn.zza(fileDescriptor);
    }

    public static zzj zza(String str) throws IOException {
        if (Build.VERSION.SDK_INT >= 21) {
            StructStat structStat = (StructStat) zza(new Callable(str) { // from class: com.google.android.gms.internal.mlkit_common.zzl
                private final String zza;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.zza = str;
                }

                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return Os.lstat(this.zza);
                }
            });
            return new zzj(structStat.st_dev, structStat.st_ino, OsConstants.S_ISLNK(structStat.st_mode));
        }
        return zzn.zza(str);
    }

    private static <T> T zza(Callable<T> callable) throws IOException {
        try {
            return callable.call();
        } catch (Throwable th) {
            throw new IOException(th);
        }
    }
}
