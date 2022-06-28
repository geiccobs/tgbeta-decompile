package com.google.android.exoplayer2.upstream;

import android.os.Handler;
/* loaded from: classes3.dex */
public interface BandwidthMeter {

    /* loaded from: classes3.dex */
    public interface EventListener {
        void onBandwidthSample(int i, long j, long j2);
    }

    void addEventListener(Handler handler, EventListener eventListener);

    long getBitrateEstimate();

    TransferListener getTransferListener();

    void removeEventListener(EventListener eventListener);
}
