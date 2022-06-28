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
import org.telegram.tgnet.ConnectionsManager;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzht implements Runnable {
    final /* synthetic */ Uri zza;
    final /* synthetic */ BaseImplementation.ResultHolder zzb;
    final /* synthetic */ boolean zzc;
    final /* synthetic */ String zzd;
    final /* synthetic */ zzhv zze;

    public zzht(zzhv zzhvVar, Uri uri, BaseImplementation.ResultHolder resultHolder, boolean z, String str) {
        this.zze = zzhvVar;
        this.zza = uri;
        this.zzb = resultHolder;
        this.zzc = z;
        this.zzd = str;
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
            Log.v("WearableClient", "Executing receiveFileFromChannelTask");
        }
        if (!"file".equals(this.zza.getScheme())) {
            Log.w("WearableClient", "Channel.receiveFile used with non-file URI");
            this.zzb.setFailedResult(new Status(10, "Channel.receiveFile used with non-file URI"));
            return;
        }
        ?? e = new File(this.zza.getPath());
        try {
            try {
                ParcelFileDescriptor open = ParcelFileDescriptor.open(e, (true != this.zzc ? 0 : ConnectionsManager.FileTypeVideo) | 671088640);
                try {
                    ((zzeu) this.zze.getService()).zzz(new zzhr(this.zzb), this.zzd, open);
                    try {
                        open.close();
                    } catch (IOException e2) {
                        e = e2;
                        Log.w("WearableClient", "Failed to close targetFd", e);
                    }
                } catch (RemoteException e3) {
                    Log.w("WearableClient", "Channel.receiveFile failed.", e3);
                    this.zzb.setFailedResult(new Status(8));
                    try {
                        open.close();
                    } catch (IOException e4) {
                        e = e4;
                        Log.w("WearableClient", "Failed to close targetFd", e);
                    }
                }
            } catch (Throwable th) {
                try {
                    e.close();
                } catch (IOException e5) {
                    Log.w("WearableClient", "Failed to close targetFd", e5);
                }
                throw th;
            }
        } catch (FileNotFoundException e6) {
            String valueOf = String.valueOf((Object) e);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 49);
            sb.append("File couldn't be opened for Channel.receiveFile: ");
            sb.append(valueOf);
            Log.w("WearableClient", sb.toString());
            this.zzb.setFailedResult(new Status(13));
        }
    }
}
