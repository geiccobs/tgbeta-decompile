package com.carrotsearch.randomizedtesting;
/* loaded from: classes3.dex */
final class MurmurHash3 {
    private MurmurHash3() {
    }

    public static int hash(int k) {
        int k2 = (k ^ (k >>> 16)) * (-2048144789);
        int k3 = (k2 ^ (k2 >>> 13)) * (-1028477387);
        return k3 ^ (k3 >>> 16);
    }

    public static long hash(long k) {
        long k2 = (k ^ (k >>> 33)) * (-49064778989728563L);
        long k3 = (k2 ^ (k2 >>> 33)) * (-4265267296055464877L);
        return k3 ^ (k3 >>> 33);
    }
}
