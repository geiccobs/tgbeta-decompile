package com.carrotsearch.randomizedtesting;

import java.util.Random;
/* loaded from: classes3.dex */
public class Xoroshiro128PlusRandom extends Random {
    private static final double DOUBLE_UNIT = 1.1102230246251565E-16d;
    private static final float FLOAT_UNIT = 5.9604645E-8f;
    private long s0;
    private long s1;

    public Xoroshiro128PlusRandom(long seed) {
        super(0L);
        long hash = MurmurHash3.hash(seed);
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
    public void setSeed(long seed) {
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
    public void nextBytes(byte[] bytes) {
        int i = 0;
        int len = bytes.length;
        while (i < len) {
            long rnd = nextInt();
            int i2 = Math.min(len - i, 8);
            while (true) {
                int n = i2 - 1;
                if (i2 > 0) {
                    bytes[i] = (byte) rnd;
                    rnd >>>= 8;
                    i++;
                    i2 = n;
                }
            }
        }
    }

    @Override // java.util.Random
    public double nextDouble() {
        double nextLong = nextLong() >>> 11;
        Double.isNaN(nextLong);
        return nextLong * DOUBLE_UNIT;
    }

    @Override // java.util.Random
    public float nextFloat() {
        return (nextInt() >>> 8) * FLOAT_UNIT;
    }

    @Override // java.util.Random
    public int nextInt() {
        return (int) nextLong();
    }

    @Override // java.util.Random
    public int nextInt(int n) {
        return super.nextInt(n);
    }

    @Override // java.util.Random
    public double nextGaussian() {
        return super.nextGaussian();
    }

    @Override // java.util.Random
    public long nextLong() {
        long s0 = this.s0;
        long s1 = this.s1;
        long result = s0 + s1;
        long s12 = s1 ^ s0;
        this.s0 = (Long.rotateLeft(s0, 55) ^ s12) ^ (s12 << 14);
        this.s1 = Long.rotateLeft(s12, 36);
        return result;
    }

    @Override // java.util.Random
    protected int next(int bits) {
        return ((int) nextLong()) >>> (32 - bits);
    }
}
