package com.carrotsearch.randomizedtesting;
/* loaded from: classes.dex */
final class MurmurHash3 {
    public static long hash(long j) {
        long j2 = (j ^ (j >>> 33)) * (-49064778989728563L);
        long j3 = (j2 ^ (j2 >>> 33)) * (-4265267296055464877L);
        return j3 ^ (j3 >>> 33);
    }
}
