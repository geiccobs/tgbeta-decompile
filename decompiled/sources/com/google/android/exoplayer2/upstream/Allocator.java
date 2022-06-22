package com.google.android.exoplayer2.upstream;
/* loaded from: classes.dex */
public interface Allocator {
    Allocation allocate();

    int getIndividualAllocationLength();

    void release(Allocation allocation);

    void release(Allocation[] allocationArr);

    void trim();
}
