package com.google.android.exoplayer2.source;
/* loaded from: classes.dex */
public interface SequenceableLoader {

    /* loaded from: classes.dex */
    public interface Callback<T extends SequenceableLoader> {
        void onContinueLoadingRequested(T t);
    }

    boolean continueLoading(long j);

    long getBufferedPositionUs();

    long getNextLoadPositionUs();

    boolean isLoading();

    void reevaluateBuffer(long j);
}
