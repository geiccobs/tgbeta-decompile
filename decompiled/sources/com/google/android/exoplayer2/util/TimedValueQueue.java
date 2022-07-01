package com.google.android.exoplayer2.util;

import java.util.Arrays;
/* loaded from: classes.dex */
public final class TimedValueQueue<V> {
    private int first;
    private int size;
    private long[] timestamps;
    private V[] values;

    public TimedValueQueue() {
        this(10);
    }

    public TimedValueQueue(int i) {
        this.timestamps = new long[i];
        this.values = (V[]) newArray(i);
    }

    public synchronized void add(long j, V v) {
        clearBufferOnTimeDiscontinuity(j);
        doubleCapacityIfFull();
        addUnchecked(j, v);
    }

    public synchronized void clear() {
        this.first = 0;
        this.size = 0;
        Arrays.fill(this.values, (Object) null);
    }

    public synchronized V pollFloor(long j) {
        return poll(j, true);
    }

    private V poll(long j, boolean z) {
        long j2 = Long.MAX_VALUE;
        V v = null;
        while (true) {
            int i = this.size;
            if (i <= 0) {
                break;
            }
            long[] jArr = this.timestamps;
            int i2 = this.first;
            long j3 = j - jArr[i2];
            if (j3 < 0 && (z || (-j3) >= j2)) {
                break;
            }
            V[] vArr = this.values;
            v = vArr[i2];
            vArr[i2] = null;
            this.first = (i2 + 1) % vArr.length;
            this.size = i - 1;
            j2 = j3;
        }
        return v;
    }

    private void clearBufferOnTimeDiscontinuity(long j) {
        int i = this.size;
        if (i > 0) {
            if (j > this.timestamps[((this.first + i) - 1) % this.values.length]) {
                return;
            }
            clear();
        }
    }

    private void doubleCapacityIfFull() {
        int length = this.values.length;
        if (this.size < length) {
            return;
        }
        int i = length * 2;
        long[] jArr = new long[i];
        V[] vArr = (V[]) newArray(i);
        int i2 = this.first;
        int i3 = length - i2;
        System.arraycopy(this.timestamps, i2, jArr, 0, i3);
        System.arraycopy(this.values, this.first, vArr, 0, i3);
        int i4 = this.first;
        if (i4 > 0) {
            System.arraycopy(this.timestamps, 0, jArr, i3, i4);
            System.arraycopy(this.values, 0, vArr, i3, this.first);
        }
        this.timestamps = jArr;
        this.values = vArr;
        this.first = 0;
    }

    private void addUnchecked(long j, V v) {
        int i = this.first;
        int i2 = this.size;
        V[] vArr = this.values;
        int length = (i + i2) % vArr.length;
        this.timestamps[length] = j;
        vArr[length] = v;
        this.size = i2 + 1;
    }

    private static <V> V[] newArray(int i) {
        return (V[]) new Object[i];
    }
}
