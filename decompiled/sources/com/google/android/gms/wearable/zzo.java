package com.google.android.gms.wearable;

import com.google.android.gms.common.data.DataHolder;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzo implements Runnable {
    final /* synthetic */ DataHolder zza;
    final /* synthetic */ zzx zzb;

    public zzo(zzx zzxVar, DataHolder dataHolder) {
        this.zzb = zzxVar;
        this.zza = dataHolder;
    }

    @Override // java.lang.Runnable
    public final void run() {
        DataEventBuffer dataEventBuffer = new DataEventBuffer(this.zza);
        try {
            this.zzb.zza.onDataChanged(dataEventBuffer);
        } finally {
            dataEventBuffer.release();
        }
    }
}
