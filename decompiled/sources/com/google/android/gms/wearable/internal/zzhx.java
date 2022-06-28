package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhx implements ListenerHolder.Notifier<DataApi.DataListener> {
    final /* synthetic */ DataHolder zza;

    public zzhx(DataHolder dataHolder) {
        this.zza = dataHolder;
    }

    @Override // com.google.android.gms.common.api.internal.ListenerHolder.Notifier
    public final /* bridge */ /* synthetic */ void notifyListener(DataApi.DataListener dataListener) {
        try {
            dataListener.onDataChanged(new DataEventBuffer(this.zza));
        } finally {
            this.zza.close();
        }
    }

    @Override // com.google.android.gms.common.api.internal.ListenerHolder.Notifier
    public final void onNotifyListenerFailed() {
        this.zza.close();
    }
}
