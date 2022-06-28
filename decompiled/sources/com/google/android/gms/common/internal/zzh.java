package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.internal.GmsClientSupervisor;
import java.util.HashMap;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public final class zzh implements Handler.Callback {
    private final /* synthetic */ zzg zza;

    /* JADX INFO: Access modifiers changed from: private */
    public zzh(zzg zzgVar) {
        this.zza = zzgVar;
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        HashMap hashMap;
        HashMap hashMap2;
        HashMap hashMap3;
        HashMap hashMap4;
        HashMap hashMap5;
        switch (message.what) {
            case 0:
                hashMap = this.zza.zza;
                synchronized (hashMap) {
                    GmsClientSupervisor.zza zzaVar = (GmsClientSupervisor.zza) message.obj;
                    hashMap2 = this.zza.zza;
                    zzi zziVar = (zzi) hashMap2.get(zzaVar);
                    if (zziVar != null && zziVar.zzc()) {
                        if (zziVar.zza()) {
                            zziVar.zzb("GmsClientSupervisor");
                        }
                        hashMap3 = this.zza.zza;
                        hashMap3.remove(zzaVar);
                    }
                }
                return true;
            case 1:
                hashMap4 = this.zza.zza;
                synchronized (hashMap4) {
                    GmsClientSupervisor.zza zzaVar2 = (GmsClientSupervisor.zza) message.obj;
                    hashMap5 = this.zza.zza;
                    zzi zziVar2 = (zzi) hashMap5.get(zzaVar2);
                    if (zziVar2 != null && zziVar2.zzb() == 3) {
                        String valueOf = String.valueOf(zzaVar2);
                        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 47);
                        sb.append("Timeout waiting for ServiceConnection callback ");
                        sb.append(valueOf);
                        Log.e("GmsClientSupervisor", sb.toString(), new Exception());
                        ComponentName zze = zziVar2.zze();
                        if (zze == null) {
                            zze = zzaVar2.zzb();
                        }
                        if (zze == null) {
                            zze = new ComponentName((String) Preconditions.checkNotNull(zzaVar2.zza()), "unknown");
                        }
                        zziVar2.onServiceDisconnected(zze);
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
