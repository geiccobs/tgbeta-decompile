package j$.util;

import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
/* loaded from: classes2.dex */
public interface PrimitiveIterator<T, T_CONS> extends java.util.Iterator<T> {
    void forEachRemaining(T_CONS t_cons);

    /* loaded from: classes2.dex */
    public interface OfInt extends PrimitiveIterator<Integer, IntConsumer> {
        void forEachRemaining(Consumer<? super Integer> consumer);

        void forEachRemaining(IntConsumer intConsumer);

        @Override // java.util.Iterator
        Integer next();

        int nextInt();

        /* renamed from: j$.util.PrimitiveIterator$OfInt$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfInt _this, IntConsumer action) {
                action.getClass();
                while (_this.hasNext()) {
                    action.accept(_this.nextInt());
                }
            }

            public static Integer $default$next(OfInt _this) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfInt.nextInt()");
                }
                return Integer.valueOf(_this.nextInt());
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super java.lang.Integer> */
            public static void $default$forEachRemaining(OfInt _this, Consumer consumer) {
                if (consumer instanceof IntConsumer) {
                    _this.forEachRemaining((IntConsumer) consumer);
                    return;
                }
                consumer.getClass();
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((IntConsumer) new PrimitiveIterator$OfInt$$ExternalSyntheticLambda0(consumer));
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface OfLong extends PrimitiveIterator<Long, LongConsumer> {
        void forEachRemaining(Consumer<? super Long> consumer);

        void forEachRemaining(LongConsumer longConsumer);

        @Override // java.util.Iterator
        Long next();

        long nextLong();

        /* renamed from: j$.util.PrimitiveIterator$OfLong$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfLong _this, LongConsumer action) {
                action.getClass();
                while (_this.hasNext()) {
                    action.accept(_this.nextLong());
                }
            }

            public static Long $default$next(OfLong _this) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfLong.nextLong()");
                }
                return Long.valueOf(_this.nextLong());
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super java.lang.Long> */
            public static void $default$forEachRemaining(OfLong _this, Consumer consumer) {
                if (consumer instanceof LongConsumer) {
                    _this.forEachRemaining((LongConsumer) consumer);
                    return;
                }
                consumer.getClass();
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((LongConsumer) new PrimitiveIterator$OfLong$$ExternalSyntheticLambda0(consumer));
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface OfDouble extends PrimitiveIterator<Double, DoubleConsumer> {
        void forEachRemaining(Consumer<? super Double> consumer);

        void forEachRemaining(DoubleConsumer doubleConsumer);

        @Override // java.util.Iterator
        Double next();

        double nextDouble();

        /* renamed from: j$.util.PrimitiveIterator$OfDouble$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static void $default$forEachRemaining(OfDouble _this, DoubleConsumer action) {
                action.getClass();
                while (_this.hasNext()) {
                    action.accept(_this.nextDouble());
                }
            }

            public static Double $default$next(OfDouble _this) {
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfDouble.nextLong()");
                }
                return Double.valueOf(_this.nextDouble());
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super java.lang.Double> */
            public static void $default$forEachRemaining(OfDouble _this, Consumer consumer) {
                if (consumer instanceof DoubleConsumer) {
                    _this.forEachRemaining((DoubleConsumer) consumer);
                    return;
                }
                consumer.getClass();
                if (Tripwire.ENABLED) {
                    Tripwire.trip(_this.getClass(), "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
                }
                consumer.getClass();
                _this.forEachRemaining((DoubleConsumer) new PrimitiveIterator$OfDouble$$ExternalSyntheticLambda0(consumer));
            }
        }
    }
}
