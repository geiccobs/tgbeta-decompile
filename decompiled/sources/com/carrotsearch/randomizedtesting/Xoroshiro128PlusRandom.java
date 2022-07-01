package com.carrotsearch.randomizedtesting;

import java.util.Random;
/* loaded from: classes.dex */
public class Xoroshiro128PlusRandom extends Random {
    private long s0;
    private long s1;

    public Xoroshiro128PlusRandom(long j) {
        super(0L);
        long hash = MurmurHash3.hash(j);
        this.s0 = hash;
        long hash2 = MurmurHash3.hash(hash);
        this.s1 = hash2;
        if (this.s0 == 0 && hash2 == 0) {
            long hash3 = MurmurHash3.hash(3735928559L);
            this.s0 = hash3;
            this.s1 = MurmurHash3.hash(hash3);
        }
    }

    @Override // java.util.Random
    public void setSeed(long j) {
        if (this.s0 == 0 && this.s1 == 0) {
            return;
        }
        throw new RuntimeException("No seed set");
    }

    @Override // java.util.Random
    public boolean nextBoolean() {
        return nextLong() >= 0;
    }

    @Override // java.util.Random
    public void nextBytes(byte[] bArr) {
        int length = bArr.length;
        int i = 0;
        while (i < length) {
            long nextInt = nextInt();
            int min = Math.min(length - i, 8);
            while (true) {
                int i2 = min - 1;
                if (min > 0) {
                    bArr[i] = (byte) nextInt;
                    nextInt >>>= 8;
                    i++;
                    min = i2;
                }
            }
        }
    }

    @Override // java.util.Random
    public double nextDouble() {
        double nextLong = nextLong() >>> 11;
        Double.isNaN(nextLong);
        return nextLong * 1.1102230246251565E-16d;
    }

    @Override // java.util.Random
    public float nextFloat() {
        return (nextInt() >>> 8) * 5.9604645E-8f;
    }

    @Override // java.util.Random
    public int nextInt() {
        return (int) nextLong();
    }

    @Override // java.util.Random
    public int nextInt(int i) {
        return super.nextInt(i);
    }

    @Override // java.util.Random
    public double nextGaussian() {
        return super.nextGaussian();
    }

    @Override // java.util.Random
    public long nextLong() {
        long j = this.s0;
        long j2 = this.s1;
        long j3 = j + j2;
        long j4 = j2 ^ j;
        this.s0 = (Long.rotateLeft(j, 55) ^ j4) ^ (j4 << 14);
        this.s1 = Long.rotateLeft(j4, 36);
        return j3;
    }

    @Override // java.util.Random
    protected int next(int i) {
        return ((int) nextLong()) >>> (32 - i);
    }
}
