package j$.util.concurrent;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.StreamSupport;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes2.dex */
public class ThreadLocalRandom extends Random {
    static final String BadBound = "bound must be positive";
    static final String BadRange = "bound must be greater than origin";
    static final String BadSize = "size must be non-negative";
    private static final double DOUBLE_UNIT = 1.1102230246251565E-16d;
    private static final float FLOAT_UNIT = 5.9604645E-8f;
    private static final long GAMMA = -7046029254386353131L;
    private static final int PROBE_INCREMENT = -1640531527;
    private static final long SEEDER_INCREMENT = -4942790177534073029L;
    private static final long serialVersionUID = -5851777807851030925L;
    boolean initialized;
    int threadLocalRandomProbe;
    int threadLocalRandomSecondarySeed;
    long threadLocalRandomSeed;
    private static final AtomicInteger probeGenerator = new AtomicInteger();
    private static final AtomicLong seeder = new AtomicLong(initialSeed());
    private static final ThreadLocal<Double> nextLocalGaussian = new ThreadLocal<>();
    private static final ThreadLocal<ThreadLocalRandom> instances = new ThreadLocal<ThreadLocalRandom>() { // from class: j$.util.concurrent.ThreadLocalRandom.2
        @Override // java.lang.ThreadLocal
        public ThreadLocalRandom initialValue() {
            return new ThreadLocalRandom();
        }
    };
    private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("rnd", Long.TYPE), new ObjectStreamField("initialized", Boolean.TYPE)};

    private static long initialSeed() {
        if (((Boolean) AccessController.doPrivileged(new PrivilegedAction<Boolean>() { // from class: j$.util.concurrent.ThreadLocalRandom.1
            @Override // java.security.PrivilegedAction
            public Boolean run() {
                return Boolean.valueOf(Boolean.getBoolean("java.util.secureRandomSeed"));
            }
        })).booleanValue()) {
            byte[] seedBytes = SecureRandom.getSeed(8);
            long s = seedBytes[0] & 255;
            for (int i = 1; i < 8; i++) {
                s = (s << 8) | (seedBytes[i] & 255);
            }
            return s;
        }
        return mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime());
    }

    private static long mix64(long z) {
        long z2 = ((z >>> 33) ^ z) * (-49064778989728563L);
        long z3 = ((z2 >>> 33) ^ z2) * (-4265267296055464877L);
        return (z3 >>> 33) ^ z3;
    }

    private static int mix32(long z) {
        long z2 = ((z >>> 33) ^ z) * (-49064778989728563L);
        return (int) ((((z2 >>> 33) ^ z2) * (-4265267296055464877L)) >>> 32);
    }

    private ThreadLocalRandom() {
        this.initialized = true;
    }

    public static final void localInit() {
        int p = probeGenerator.addAndGet(PROBE_INCREMENT);
        int probe = p == 0 ? 1 : p;
        long seed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
        ThreadLocalRandom t = instances.get();
        t.threadLocalRandomSeed = seed;
        t.threadLocalRandomProbe = probe;
    }

    public static ThreadLocalRandom current() {
        ThreadLocalRandom t = instances.get();
        if (t.threadLocalRandomProbe == 0) {
            localInit();
        }
        return t;
    }

    @Override // java.util.Random
    public void setSeed(long seed) {
        if (this.initialized) {
            throw new UnsupportedOperationException();
        }
    }

    final long nextSeed() {
        long r = this.threadLocalRandomSeed + GAMMA;
        this.threadLocalRandomSeed = r;
        return r;
    }

    @Override // java.util.Random
    protected int next(int bits) {
        return (int) (mix64(nextSeed()) >>> (64 - bits));
    }

    final long internalNextLong(long origin, long bound) {
        long r = mix64(nextSeed());
        if (origin < bound) {
            long n = bound - origin;
            long m = n - 1;
            if ((n & m) == 0) {
                return (r & m) + origin;
            }
            if (n > 0) {
                long u = r >>> 1;
                while (true) {
                    long r2 = u % n;
                    if ((u + m) - r2 < 0) {
                        u = mix64(nextSeed()) >>> 1;
                    } else {
                        return r2 + origin;
                    }
                }
            } else {
                while (true) {
                    if (r < origin || r >= bound) {
                        r = mix64(nextSeed());
                    } else {
                        return r;
                    }
                }
            }
        } else {
            return r;
        }
    }

    final int internalNextInt(int origin, int bound) {
        int r = mix32(nextSeed());
        if (origin < bound) {
            int n = bound - origin;
            int m = n - 1;
            if ((n & m) == 0) {
                return (r & m) + origin;
            }
            if (n > 0) {
                int u = r >>> 1;
                while (true) {
                    int r2 = u % n;
                    if ((u + m) - r2 < 0) {
                        u = mix32(nextSeed()) >>> 1;
                    } else {
                        return r2 + origin;
                    }
                }
            } else {
                while (true) {
                    if (r < origin || r >= bound) {
                        r = mix32(nextSeed());
                    } else {
                        return r;
                    }
                }
            }
        } else {
            return r;
        }
    }

    final double internalNextDouble(double origin, double bound) {
        double nextLong = nextLong() >>> 11;
        Double.isNaN(nextLong);
        double r = nextLong * DOUBLE_UNIT;
        if (origin < bound) {
            double r2 = ((bound - origin) * r) + origin;
            if (r2 >= bound) {
                return Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1);
            }
            return r2;
        }
        return r;
    }

    @Override // java.util.Random
    public int nextInt() {
        return mix32(nextSeed());
    }

    @Override // java.util.Random
    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException(BadBound);
        }
        int r = mix32(nextSeed());
        int m = bound - 1;
        if ((bound & m) == 0) {
            return r & m;
        }
        int u = r >>> 1;
        while (true) {
            int r2 = u % bound;
            if ((u + m) - r2 >= 0) {
                return r2;
            }
            u = mix32(nextSeed()) >>> 1;
        }
    }

    public int nextInt(int origin, int bound) {
        if (origin >= bound) {
            throw new IllegalArgumentException(BadRange);
        }
        return internalNextInt(origin, bound);
    }

    @Override // java.util.Random
    public long nextLong() {
        return mix64(nextSeed());
    }

    public long nextLong(long bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException(BadBound);
        }
        long r = mix64(nextSeed());
        long m = bound - 1;
        if ((bound & m) == 0) {
            return r & m;
        }
        long u = r >>> 1;
        while (true) {
            long r2 = u % bound;
            if ((u + m) - r2 < 0) {
                u = mix64(nextSeed()) >>> 1;
            } else {
                return r2;
            }
        }
    }

    public long nextLong(long origin, long bound) {
        if (origin >= bound) {
            throw new IllegalArgumentException(BadRange);
        }
        return internalNextLong(origin, bound);
    }

    @Override // java.util.Random
    public double nextDouble() {
        double mix64 = mix64(nextSeed()) >>> 11;
        Double.isNaN(mix64);
        return mix64 * DOUBLE_UNIT;
    }

    public double nextDouble(double bound) {
        if (bound <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            throw new IllegalArgumentException(BadBound);
        }
        double mix64 = mix64(nextSeed()) >>> 11;
        Double.isNaN(mix64);
        double result = mix64 * DOUBLE_UNIT * bound;
        return result < bound ? result : Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1);
    }

    public double nextDouble(double origin, double bound) {
        if (origin >= bound) {
            throw new IllegalArgumentException(BadRange);
        }
        return internalNextDouble(origin, bound);
    }

    @Override // java.util.Random
    public boolean nextBoolean() {
        return mix32(nextSeed()) < 0;
    }

    @Override // java.util.Random
    public float nextFloat() {
        return (mix32(nextSeed()) >>> 8) * FLOAT_UNIT;
    }

    @Override // java.util.Random
    public double nextGaussian() {
        ThreadLocal<Double> threadLocal = nextLocalGaussian;
        Double d = threadLocal.get();
        if (d != null) {
            threadLocal.set(null);
            return d.doubleValue();
        }
        while (true) {
            double v1 = (nextDouble() * 2.0d) - 1.0d;
            double v2 = (nextDouble() * 2.0d) - 1.0d;
            double s = (v1 * v1) + (v2 * v2);
            if (s < 1.0d && s != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                double multiplier = StrictMath.sqrt((StrictMath.log(s) * (-2.0d)) / s);
                nextLocalGaussian.set(new Double(v2 * multiplier));
                return v1 * multiplier;
            }
        }
    }

    @Override // java.util.Random
    public IntStream ints(long streamSize) {
        if (streamSize < 0) {
            throw new IllegalArgumentException(BadSize);
        }
        return StreamSupport.intStream(new RandomIntsSpliterator(0L, streamSize, Integer.MAX_VALUE, 0), false);
    }

    @Override // java.util.Random
    public IntStream ints() {
        return StreamSupport.intStream(new RandomIntsSpliterator(0L, Long.MAX_VALUE, Integer.MAX_VALUE, 0), false);
    }

    @Override // java.util.Random
    public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
        if (streamSize < 0) {
            throw new IllegalArgumentException(BadSize);
        }
        if (randomNumberOrigin >= randomNumberBound) {
            throw new IllegalArgumentException(BadRange);
        }
        return StreamSupport.intStream(new RandomIntsSpliterator(0L, streamSize, randomNumberOrigin, randomNumberBound), false);
    }

    @Override // java.util.Random
    public IntStream ints(int randomNumberOrigin, int randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound) {
            throw new IllegalArgumentException(BadRange);
        }
        return StreamSupport.intStream(new RandomIntsSpliterator(0L, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound), false);
    }

    @Override // java.util.Random
    public LongStream longs(long streamSize) {
        if (streamSize < 0) {
            throw new IllegalArgumentException(BadSize);
        }
        return StreamSupport.longStream(new RandomLongsSpliterator(0L, streamSize, Long.MAX_VALUE, 0L), false);
    }

    @Override // java.util.Random
    public LongStream longs() {
        return StreamSupport.longStream(new RandomLongsSpliterator(0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L), false);
    }

    @Override // java.util.Random
    public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
        if (streamSize < 0) {
            throw new IllegalArgumentException(BadSize);
        }
        if (randomNumberOrigin >= randomNumberBound) {
            throw new IllegalArgumentException(BadRange);
        }
        return StreamSupport.longStream(new RandomLongsSpliterator(0L, streamSize, randomNumberOrigin, randomNumberBound), false);
    }

    @Override // java.util.Random
    public LongStream longs(long randomNumberOrigin, long randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound) {
            throw new IllegalArgumentException(BadRange);
        }
        return StreamSupport.longStream(new RandomLongsSpliterator(0L, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound), false);
    }

    @Override // java.util.Random
    public DoubleStream doubles(long streamSize) {
        if (streamSize < 0) {
            throw new IllegalArgumentException(BadSize);
        }
        return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, streamSize, Double.MAX_VALUE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE), false);
    }

    @Override // java.util.Random
    public DoubleStream doubles() {
        return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, Long.MAX_VALUE, Double.MAX_VALUE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE), false);
    }

    @Override // java.util.Random
    public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
        if (streamSize < 0) {
            throw new IllegalArgumentException(BadSize);
        }
        if (randomNumberOrigin >= randomNumberBound) {
            throw new IllegalArgumentException(BadRange);
        }
        return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, streamSize, randomNumberOrigin, randomNumberBound), false);
    }

    @Override // java.util.Random
    public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound) {
            throw new IllegalArgumentException(BadRange);
        }
        return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound), false);
    }

    /* loaded from: classes2.dex */
    public static final class RandomIntsSpliterator implements Spliterator.OfInt {
        final int bound;
        final long fence;
        long index;
        final int origin;

        @Override // j$.util.Spliterator.OfInt, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        @Override // j$.util.Spliterator.OfInt, j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
        }

        RandomIntsSpliterator(long index, long fence, int origin, int bound) {
            this.index = index;
            this.fence = fence;
            this.origin = origin;
            this.bound = bound;
        }

        @Override // j$.util.Spliterator.OfInt, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
        public RandomIntsSpliterator trySplit() {
            long i = this.index;
            long m = (this.fence + i) >>> 1;
            if (m <= i) {
                return null;
            }
            this.index = m;
            return new RandomIntsSpliterator(i, m, this.origin, this.bound);
        }

        @Override // j$.util.Spliterator
        public long estimateSize() {
            return this.fence - this.index;
        }

        @Override // j$.util.Spliterator
        public int characteristics() {
            return 17728;
        }

        @Override // j$.util.Spliterator.OfInt
        public boolean tryAdvance(IntConsumer consumer) {
            if (consumer == null) {
                throw new NullPointerException();
            }
            long i = this.index;
            long f = this.fence;
            if (i < f) {
                consumer.accept(ThreadLocalRandom.current().internalNextInt(this.origin, this.bound));
                this.index = 1 + i;
                return true;
            }
            return false;
        }

        @Override // j$.util.Spliterator.OfInt
        public void forEachRemaining(IntConsumer consumer) {
            long j;
            if (consumer == null) {
                throw new NullPointerException();
            }
            long i = this.index;
            long f = this.fence;
            if (i < f) {
                this.index = f;
                int o = this.origin;
                int b = this.bound;
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                do {
                    consumer.accept(rng.internalNextInt(o, b));
                    j = 1 + i;
                    i = j;
                } while (j < f);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class RandomLongsSpliterator implements Spliterator.OfLong {
        final long bound;
        final long fence;
        long index;
        final long origin;

        @Override // j$.util.Spliterator.OfLong, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        @Override // j$.util.Spliterator.OfLong, j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
        }

        RandomLongsSpliterator(long index, long fence, long origin, long bound) {
            this.index = index;
            this.fence = fence;
            this.origin = origin;
            this.bound = bound;
        }

        @Override // j$.util.Spliterator.OfLong, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
        public RandomLongsSpliterator trySplit() {
            long i = this.index;
            long m = (this.fence + i) >>> 1;
            if (m <= i) {
                return null;
            }
            this.index = m;
            return new RandomLongsSpliterator(i, m, this.origin, this.bound);
        }

        @Override // j$.util.Spliterator
        public long estimateSize() {
            return this.fence - this.index;
        }

        @Override // j$.util.Spliterator
        public int characteristics() {
            return 17728;
        }

        @Override // j$.util.Spliterator.OfLong
        public boolean tryAdvance(LongConsumer consumer) {
            if (consumer == null) {
                throw new NullPointerException();
            }
            long i = this.index;
            long f = this.fence;
            if (i < f) {
                consumer.accept(ThreadLocalRandom.current().internalNextLong(this.origin, this.bound));
                this.index = 1 + i;
                return true;
            }
            return false;
        }

        @Override // j$.util.Spliterator.OfLong
        public void forEachRemaining(LongConsumer consumer) {
            long j;
            if (consumer == null) {
                throw new NullPointerException();
            }
            long i = this.index;
            long f = this.fence;
            if (i < f) {
                this.index = f;
                long o = this.origin;
                long b = this.bound;
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                do {
                    consumer.accept(rng.internalNextLong(o, b));
                    j = 1 + i;
                    i = j;
                } while (j < f);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class RandomDoublesSpliterator implements Spliterator.OfDouble {
        final double bound;
        final long fence;
        long index;
        final double origin;

        @Override // j$.util.Spliterator.OfDouble, j$.util.Spliterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        @Override // j$.util.Spliterator
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        @Override // j$.util.Spliterator.OfDouble, j$.util.Spliterator
        public /* synthetic */ boolean tryAdvance(Consumer consumer) {
            return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
        }

        RandomDoublesSpliterator(long index, long fence, double origin, double bound) {
            this.index = index;
            this.fence = fence;
            this.origin = origin;
            this.bound = bound;
        }

        @Override // j$.util.Spliterator.OfDouble, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
        public RandomDoublesSpliterator trySplit() {
            long i = this.index;
            long m = (this.fence + i) >>> 1;
            if (m <= i) {
                return null;
            }
            this.index = m;
            return new RandomDoublesSpliterator(i, m, this.origin, this.bound);
        }

        @Override // j$.util.Spliterator
        public long estimateSize() {
            return this.fence - this.index;
        }

        @Override // j$.util.Spliterator
        public int characteristics() {
            return 17728;
        }

        @Override // j$.util.Spliterator.OfDouble
        public boolean tryAdvance(DoubleConsumer consumer) {
            if (consumer == null) {
                throw new NullPointerException();
            }
            long i = this.index;
            long f = this.fence;
            if (i < f) {
                consumer.accept(ThreadLocalRandom.current().internalNextDouble(this.origin, this.bound));
                this.index = 1 + i;
                return true;
            }
            return false;
        }

        @Override // j$.util.Spliterator.OfDouble
        public void forEachRemaining(DoubleConsumer consumer) {
            long j;
            if (consumer == null) {
                throw new NullPointerException();
            }
            long i = this.index;
            long f = this.fence;
            if (i < f) {
                this.index = f;
                double o = this.origin;
                double b = this.bound;
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                do {
                    consumer.accept(rng.internalNextDouble(o, b));
                    j = 1 + i;
                    i = j;
                } while (j < f);
            }
        }
    }

    public static final int getProbe() {
        return instances.get().threadLocalRandomProbe;
    }

    public static final int advanceProbe(int probe) {
        int probe2 = probe ^ (probe << 13);
        int probe3 = probe2 ^ (probe2 >>> 17);
        int probe4 = probe3 ^ (probe3 << 5);
        instances.get().threadLocalRandomProbe = probe4;
        return probe4;
    }

    static final int nextSecondarySeed() {
        int r;
        ThreadLocalRandom t = instances.get();
        int r2 = t.threadLocalRandomSecondarySeed;
        if (r2 != 0) {
            int r3 = (r2 << 13) ^ r2;
            int r4 = r3 ^ (r3 >>> 17);
            r = r4 ^ (r4 << 5);
        } else {
            localInit();
            int r5 = (int) t.threadLocalRandomSeed;
            if (r5 != 0) {
                r = r5;
            } else {
                r = 1;
            }
        }
        t.threadLocalRandomSecondarySeed = r;
        return r;
    }

    private void writeObject(ObjectOutputStream s) {
        ObjectOutputStream.PutField fields = s.putFields();
        fields.put("rnd", this.threadLocalRandomSeed);
        fields.put("initialized", true);
        s.writeFields();
    }

    private Object readResolve() {
        return current();
    }
}
