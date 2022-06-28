package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzhu implements Runnable {
    final /* synthetic */ Uri zza;
    final /* synthetic */ BaseImplementation.ResultHolder zzb;
    final /* synthetic */ String zzc;
    final /* synthetic */ long zzd;
    final /* synthetic */ long zze;
    final /* synthetic */ zzhv zzf;

    public zzhu(zzhv zzhvVar, Uri uri, BaseImplementation.ResultHolder resultHolder, String str, long j, long j2) {
        this.zzf = zzhvVar;
        this.zza = uri;
        this.zzb = resultHolder;
        this.zzc = str;
        this.zzd = j;
        this.zze = j2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v12, types: [android.os.ParcelFileDescriptor] */
    /* JADX WARN: Type inference failed for: r2v15, types: [java.io.IOException] */
    /* JADX WARN: Type inference failed for: r2v16, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r2v17, types: [java.io.IOException] */
    /* JADX WARN: Type inference failed for: r2v5, types: [java.lang.Object, java.io.File] */
    @Override // java.lang.Runnable
    public final void run() {
        if (Log.isLoggable("WearableClient", 2)) {
            Log.v("WearableClient", "Executing sendFileToChannelTask");
        }
        if (!"file".equals(this.zza.getScheme())) {
            Log.w("WearableClient", "Channel.sendFile used with non-file URI");
            this.zzb.setFailedResult(new Status(10, "Channel.sendFile used with non-file URI"));
            return;
        }
        ?? e = new File(this.zza.getPath());
        try {
            try {
                ParcelFileDescriptor open = ParcelFileDescriptor.open(e, 268435456);
                try {
                    ((zzeu) this.zzf.getService()).zzA(new zzho(this.zzb), this.zzc, open, this.zzd, this.zze);
                    try {
                        open.close();
                    } catch (IOException e2) {
                        e = e2;
                        Log.w("WearableClient", "Failed to close sourceFd", e);
                    }
                } catch (RemoteException e3) {
                    Log.w("WearableClient", "Channel.sendFile failed.", e3);
                    this.zzb.setFailedResult(new Status(8));
                    try {
                        open.close();
                    } catch (IOException e4) {
                        e = e4;
                        Log.w("WearableClient", "Failed to close sourceFd", e);
                    }
                }
            } catch (Throwable th) {
                try {
                    e.close();
                } catch (IOException e5) {
                    Log.w("WearableClient", "Failed to close sourceFd", e5);
                }
                throw th;
            }
        } catch (FileNotFoundException e6) {
            String valueOf = String.valueOf((Object) e);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 46);
            sb.append("File couldn't be opened for Channel.sendFile: ");
            sb.append(valueOf);
            Log.w("WearableClient", sb.toString());
            this.zzb.setFailedResult(new Status(13));
        }
    }
}
