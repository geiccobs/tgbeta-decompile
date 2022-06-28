package j$.util.stream;

import j$.lang.Iterable;
import j$.util.DesugarArrays;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.LongConsumer;
import j$.wrappers.C$r8$wrapper$java$util$function$Consumer$VWRP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes2.dex */
public class SpinedBuffer<E> extends AbstractSpinedBuffer implements Consumer<E>, Iterable<E>, Iterable<E> {
    private static final int SPLITERATOR_CHARACTERISTICS = 16464;
    protected E[] curChunk;
    protected E[][] spine;

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // java.lang.Iterable
    public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
        forEach(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    SpinedBuffer(int initialCapacity) {
        super(initialCapacity);
        this.curChunk = (E[]) new Object[1 << this.initialChunkPower];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    public SpinedBuffer() {
        this.curChunk = (E[]) new Object[1 << this.initialChunkPower];
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    protected long capacity() {
        if (this.spineIndex == 0) {
            return this.curChunk.length;
        }
        return this.priorElementCount[this.spineIndex] + this.spine[this.spineIndex].length;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    private void inflateSpine() {
        if (this.spine == null) {
            this.spine = (E[][]) new Object[8];
            this.priorElementCount = new long[8];
            this.spine[0] = this.curChunk;
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    /* JADX WARN: Multi-variable type inference failed */
    public final void ensureCapacity(long targetSize) {
        long capacity = capacity();
        if (targetSize > capacity) {
            inflateSpine();
            int i = this.spineIndex;
            while (true) {
                i++;
                if (targetSize > capacity) {
                    E[][] eArr = this.spine;
                    if (i >= eArr.length) {
                        int newSpineSize = eArr.length * 2;
                        this.spine = (E[][]) ((Object[][]) Arrays.copyOf(eArr, newSpineSize));
                        this.priorElementCount = Arrays.copyOf(this.priorElementCount, newSpineSize);
                    }
                    int nextChunkSize = chunkSize(i);
                    this.spine[i] = new Object[nextChunkSize];
                    this.priorElementCount[i] = this.priorElementCount[i - 1] + this.spine[i - 1].length;
                    capacity += nextChunkSize;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    protected void increaseCapacity() {
        ensureCapacity(capacity() + 1);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    public E get(long index) {
        if (this.spineIndex == 0) {
            if (index < this.elementIndex) {
                return this.curChunk[(int) index];
            }
            throw new IndexOutOfBoundsException(Long.toString(index));
        } else if (index >= count()) {
            throw new IndexOutOfBoundsException(Long.toString(index));
        } else {
            for (int j = 0; j <= this.spineIndex; j++) {
                long j2 = this.priorElementCount[j];
                E[][] eArr = this.spine;
                if (index < j2 + eArr[j].length) {
                    return eArr[j][(int) (index - this.priorElementCount[j])];
                }
            }
            throw new IndexOutOfBoundsException(Long.toString(index));
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    public void copyInto(E[] array, int offset) {
        long finalOffset = offset + count();
        if (finalOffset > array.length || finalOffset < offset) {
            throw new IndexOutOfBoundsException("does not fit");
        }
        if (this.spineIndex == 0) {
            System.arraycopy(this.curChunk, 0, array, offset, this.elementIndex);
            return;
        }
        for (int i = 0; i < this.spineIndex; i++) {
            E[][] eArr = this.spine;
            System.arraycopy(eArr[i], 0, array, offset, eArr[i].length);
            offset += this.spine[i].length;
        }
        int i2 = this.elementIndex;
        if (i2 > 0) {
            System.arraycopy(this.curChunk, 0, array, offset, this.elementIndex);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<E[]> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    public E[] asArray(IntFunction<E[]> intFunction) {
        long size = count();
        if (size >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        E[] result = intFunction.apply((int) size);
        copyInto(result, 0);
        return result;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    @Override // j$.util.stream.AbstractSpinedBuffer
    public void clear() {
        E[][] eArr = this.spine;
        if (eArr != null) {
            this.curChunk = eArr[0];
            int i = 0;
            while (true) {
                E[] eArr2 = this.curChunk;
                if (i >= eArr2.length) {
                    break;
                }
                eArr2[i] = null;
                i++;
            }
            this.spine = null;
            this.priorElementCount = null;
        } else {
            for (int i2 = 0; i2 < this.elementIndex; i2++) {
                this.curChunk[i2] = null;
            }
        }
        this.elementIndex = 0;
        this.spineIndex = 0;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    @Override // java.lang.Iterable, j$.lang.Iterable
    public Iterator<E> iterator() {
        return Spliterators.iterator(Iterable.EL.spliterator(this));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super E> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    public void forEach(Consumer<? super E> consumer) {
        for (int j = 0; j < this.spineIndex; j++) {
            for (E e : this.spine[j]) {
                consumer.accept((Object) e);
            }
        }
        for (int i = 0; i < this.elementIndex; i++) {
            consumer.accept((Object) this.curChunk[i]);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    public void accept(E e) {
        if (this.elementIndex == this.curChunk.length) {
            inflateSpine();
            int i = this.spineIndex + 1;
            E[][] eArr = this.spine;
            if (i >= eArr.length || eArr[this.spineIndex + 1] == null) {
                increaseCapacity();
            }
            this.elementIndex = 0;
            this.spineIndex++;
            this.curChunk = this.spine[this.spineIndex];
        }
        E[] eArr2 = this.curChunk;
        int i2 = this.elementIndex;
        this.elementIndex = i2 + 1;
        eArr2[i2] = e;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    public String toString() {
        final List<E> list = new ArrayList<>();
        list.getClass();
        Iterable.EL.forEach(this, new Consumer() { // from class: j$.util.stream.SpinedBuffer$$ExternalSyntheticLambda0
            @Override // j$.util.function.Consumer
            public final void accept(Object obj) {
                list.add(obj);
            }

            @Override // j$.util.function.Consumer
            public /* synthetic */ Consumer andThen(Consumer consumer) {
                return consumer.getClass();
            }
        });
        return "SpinedBuffer:" + list.toString();
    }

    /* renamed from: j$.util.stream.SpinedBuffer$1Splitr */
    /* loaded from: classes2.dex */
    public class C1Splitr implements Spliterator<E> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        final int lastSpineElementFence;
        final int lastSpineIndex;
        E[] splChunk;
        int splElementIndex;
        int splSpineIndex;

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

        C1Splitr(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
            SpinedBuffer.this = this$0;
            this.splSpineIndex = firstSpineIndex;
            this.lastSpineIndex = lastSpineIndex;
            this.splElementIndex = firstSpineElementIndex;
            this.lastSpineElementFence = lastSpineElementFence;
            if ($assertionsDisabled || this$0.spine != null || (firstSpineIndex == 0 && lastSpineIndex == 0)) {
                this.splChunk = this$0.spine == null ? this$0.curChunk : this$0.spine[firstSpineIndex];
                return;
            }
            throw new AssertionError();
        }

        @Override // j$.util.Spliterator
        public long estimateSize() {
            if (this.splSpineIndex == this.lastSpineIndex) {
                return this.lastSpineElementFence - this.splElementIndex;
            }
            return ((SpinedBuffer.this.priorElementCount[this.lastSpineIndex] + this.lastSpineElementFence) - SpinedBuffer.this.priorElementCount[this.splSpineIndex]) - this.splElementIndex;
        }

        @Override // j$.util.Spliterator
        public int characteristics() {
            return SpinedBuffer.SPLITERATOR_CHARACTERISTICS;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super E> */
        @Override // j$.util.Spliterator
        public boolean tryAdvance(Consumer<? super E> consumer) {
            consumer.getClass();
            int i = this.splSpineIndex;
            int i2 = this.lastSpineIndex;
            if (i < i2 || (i == i2 && this.splElementIndex < this.lastSpineElementFence)) {
                int i3 = this.splElementIndex;
                this.splElementIndex = i3 + 1;
                consumer.accept((Object) this.splChunk[i3]);
                if (this.splElementIndex == this.splChunk.length) {
                    this.splElementIndex = 0;
                    this.splSpineIndex++;
                    if (SpinedBuffer.this.spine != null && this.splSpineIndex <= this.lastSpineIndex) {
                        this.splChunk = SpinedBuffer.this.spine[this.splSpineIndex];
                    }
                }
                return true;
            }
            return false;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super E> */
        @Override // j$.util.Spliterator
        public void forEachRemaining(Consumer<? super E> consumer) {
            int i;
            consumer.getClass();
            int i2 = this.splSpineIndex;
            int i3 = this.lastSpineIndex;
            if (i2 < i3 || (i2 == i3 && this.splElementIndex < this.lastSpineElementFence)) {
                int i4 = this.splElementIndex;
                int sp = this.splSpineIndex;
                while (true) {
                    i = this.lastSpineIndex;
                    if (sp >= i) {
                        break;
                    }
                    E[] chunk = SpinedBuffer.this.spine[sp];
                    while (i4 < chunk.length) {
                        consumer.accept((Object) chunk[i4]);
                        i4++;
                    }
                    i4 = 0;
                    sp++;
                }
                int sp2 = this.splSpineIndex;
                E[] chunk2 = sp2 == i ? this.splChunk : SpinedBuffer.this.spine[this.lastSpineIndex];
                int hElementIndex = this.lastSpineElementFence;
                while (i4 < hElementIndex) {
                    consumer.accept((Object) chunk2[i4]);
                    i4++;
                }
                this.splSpineIndex = this.lastSpineIndex;
                this.splElementIndex = this.lastSpineElementFence;
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<E> */
        @Override // j$.util.Spliterator
        public Spliterator<E> trySplit() {
            int i = this.splSpineIndex;
            int i2 = this.lastSpineIndex;
            if (i < i2) {
                SpinedBuffer spinedBuffer = SpinedBuffer.this;
                C1Splitr c1Splitr = new C1Splitr(this.splSpineIndex, this.lastSpineIndex - 1, this.splElementIndex, spinedBuffer.spine[this.lastSpineIndex - 1].length);
                this.splSpineIndex = this.lastSpineIndex;
                this.splElementIndex = 0;
                this.splChunk = SpinedBuffer.this.spine[this.splSpineIndex];
                return c1Splitr;
            } else if (i != i2) {
                return null;
            } else {
                int i3 = this.lastSpineElementFence;
                int i4 = this.splElementIndex;
                int t = (i3 - i4) / 2;
                if (t == 0) {
                    return null;
                }
                Spliterator<E> spliterator = DesugarArrays.spliterator(this.splChunk, i4, i4 + t);
                this.splElementIndex += t;
                return spliterator;
            }
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer != java.util.stream.SpinedBuffer<E> */
    public Spliterator<E> spliterator() {
        return new C1Splitr(0, this.spineIndex, 0, this.elementIndex);
    }

    /* loaded from: classes2.dex */
    public static abstract class OfPrimitive<E, T_ARR, T_CONS> extends AbstractSpinedBuffer implements Iterable<E>, Iterable<E> {
        T_ARR curChunk = newArray(1 << this.initialChunkPower);
        T_ARR[] spine;

        protected abstract void arrayForEach(T_ARR t_arr, int i, int i2, T_CONS t_cons);

        protected abstract int arrayLength(T_ARR t_arr);

        public abstract void forEach(Consumer<? super E> consumer);

        public abstract Iterator<E> iterator();

        public abstract T_ARR newArray(int i);

        protected abstract T_ARR[] newArrayArray(int i);

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        OfPrimitive(int initialCapacity) {
            super(initialCapacity);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        OfPrimitive() {
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        public Spliterator<E> spliterator() {
            return Spliterators.spliteratorUnknownSize(iterator(), 0);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        protected long capacity() {
            if (this.spineIndex == 0) {
                return arrayLength(this.curChunk);
            }
            return this.priorElementCount[this.spineIndex] + arrayLength(this.spine[this.spineIndex]);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        private void inflateSpine() {
            if (this.spine == null) {
                this.spine = newArrayArray(8);
                this.priorElementCount = new long[8];
                this.spine[0] = this.curChunk;
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        public final void ensureCapacity(long targetSize) {
            long capacity = capacity();
            if (targetSize > capacity) {
                inflateSpine();
                int i = this.spineIndex;
                while (true) {
                    i++;
                    if (targetSize > capacity) {
                        T_ARR[] t_arrArr = this.spine;
                        if (i >= t_arrArr.length) {
                            int newSpineSize = t_arrArr.length * 2;
                            this.spine = (T_ARR[]) Arrays.copyOf(t_arrArr, newSpineSize);
                            this.priorElementCount = Arrays.copyOf(this.priorElementCount, newSpineSize);
                        }
                        int nextChunkSize = chunkSize(i);
                        this.spine[i] = newArray(nextChunkSize);
                        this.priorElementCount[i] = this.priorElementCount[i - 1] + arrayLength(this.spine[i - 1]);
                        capacity += nextChunkSize;
                    } else {
                        return;
                    }
                }
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        protected void increaseCapacity() {
            ensureCapacity(capacity() + 1);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        protected int chunkFor(long index) {
            if (this.spineIndex == 0) {
                if (index < this.elementIndex) {
                    return 0;
                }
                throw new IndexOutOfBoundsException(Long.toString(index));
            } else if (index >= count()) {
                throw new IndexOutOfBoundsException(Long.toString(index));
            } else {
                for (int j = 0; j <= this.spineIndex; j++) {
                    if (index < this.priorElementCount[j] + arrayLength(this.spine[j])) {
                        return j;
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(index));
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        public void copyInto(T_ARR array, int offset) {
            long finalOffset = offset + count();
            if (finalOffset > arrayLength(array) || finalOffset < offset) {
                throw new IndexOutOfBoundsException("does not fit");
            }
            if (this.spineIndex == 0) {
                System.arraycopy(this.curChunk, 0, array, offset, this.elementIndex);
                return;
            }
            for (int i = 0; i < this.spineIndex; i++) {
                T_ARR[] t_arrArr = this.spine;
                System.arraycopy(t_arrArr[i], 0, array, offset, arrayLength(t_arrArr[i]));
                offset += arrayLength(this.spine[i]);
            }
            int i2 = this.elementIndex;
            if (i2 > 0) {
                System.arraycopy(this.curChunk, 0, array, offset, this.elementIndex);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        public T_ARR asPrimitiveArray() {
            long size = count();
            if (size >= 2147483639) {
                throw new IllegalArgumentException("Stream size exceeds max array size");
            }
            T_ARR result = newArray((int) size);
            copyInto(result, 0);
            return result;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        protected void preAccept() {
            if (this.elementIndex == arrayLength(this.curChunk)) {
                inflateSpine();
                int i = this.spineIndex + 1;
                T_ARR[] t_arrArr = this.spine;
                if (i >= t_arrArr.length || t_arrArr[this.spineIndex + 1] == null) {
                    increaseCapacity();
                }
                this.elementIndex = 0;
                this.spineIndex++;
                this.curChunk = this.spine[this.spineIndex];
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        @Override // j$.util.stream.AbstractSpinedBuffer
        public void clear() {
            T_ARR[] t_arrArr = this.spine;
            if (t_arrArr != null) {
                this.curChunk = t_arrArr[0];
                this.spine = null;
                this.priorElementCount = null;
            }
            this.elementIndex = 0;
            this.spineIndex = 0;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS> */
        public void forEach(T_CONS consumer) {
            for (int j = 0; j < this.spineIndex; j++) {
                T_ARR[] t_arrArr = this.spine;
                arrayForEach(t_arrArr[j], 0, arrayLength(t_arrArr[j]), consumer);
            }
            arrayForEach(this.curChunk, 0, this.elementIndex, consumer);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public abstract class BaseSpliterator<T_SPLITR extends Spliterator.OfPrimitive<E, T_CONS, T_SPLITR>> implements Spliterator.OfPrimitive<E, T_CONS, T_SPLITR> {
            static final /* synthetic */ boolean $assertionsDisabled = true;
            final int lastSpineElementFence;
            final int lastSpineIndex;
            T_ARR splChunk;
            int splElementIndex;
            int splSpineIndex;

            abstract void arrayForOne(T_ARR t_arr, int i, T_CONS t_cons);

            abstract T_SPLITR arraySpliterator(T_ARR t_arr, int i, int i2);

            @Override // j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.CC.$default$forEachRemaining(this, consumer);
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

            abstract T_SPLITR newSpliterator(int i, int i2, int i3, int i4);

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive$BaseSpliterator != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS>$BaseSpliterator<T_SPLITR extends j$.util.Spliterator$OfPrimitive<E, T_CONS, T_SPLITR>> */
            BaseSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                OfPrimitive.this = this$0;
                this.splSpineIndex = firstSpineIndex;
                this.lastSpineIndex = lastSpineIndex;
                this.splElementIndex = firstSpineElementIndex;
                this.lastSpineElementFence = lastSpineElementFence;
                if ($assertionsDisabled || this$0.spine != null || (firstSpineIndex == 0 && lastSpineIndex == 0)) {
                    this.splChunk = this$0.spine == null ? this$0.curChunk : this$0.spine[firstSpineIndex];
                    return;
                }
                throw new AssertionError();
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive$BaseSpliterator != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS>$BaseSpliterator<T_SPLITR extends j$.util.Spliterator$OfPrimitive<E, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator
            public long estimateSize() {
                if (this.splSpineIndex == this.lastSpineIndex) {
                    return this.lastSpineElementFence - this.splElementIndex;
                }
                return ((OfPrimitive.this.priorElementCount[this.lastSpineIndex] + this.lastSpineElementFence) - OfPrimitive.this.priorElementCount[this.splSpineIndex]) - this.splElementIndex;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive$BaseSpliterator != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS>$BaseSpliterator<T_SPLITR extends j$.util.Spliterator$OfPrimitive<E, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator
            public int characteristics() {
                return SpinedBuffer.SPLITERATOR_CHARACTERISTICS;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive$BaseSpliterator != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS>$BaseSpliterator<T_SPLITR extends j$.util.Spliterator$OfPrimitive<E, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive
            public boolean tryAdvance(T_CONS consumer) {
                consumer.getClass();
                int i = this.splSpineIndex;
                int i2 = this.lastSpineIndex;
                if (i < i2 || (i == i2 && this.splElementIndex < this.lastSpineElementFence)) {
                    T_ARR t_arr = this.splChunk;
                    int i3 = this.splElementIndex;
                    this.splElementIndex = i3 + 1;
                    arrayForOne(t_arr, i3, consumer);
                    if (this.splElementIndex == OfPrimitive.this.arrayLength(this.splChunk)) {
                        this.splElementIndex = 0;
                        this.splSpineIndex++;
                        if (OfPrimitive.this.spine != null && this.splSpineIndex <= this.lastSpineIndex) {
                            this.splChunk = OfPrimitive.this.spine[this.splSpineIndex];
                        }
                    }
                    return true;
                }
                return false;
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive$BaseSpliterator != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS>$BaseSpliterator<T_SPLITR extends j$.util.Spliterator$OfPrimitive<E, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive
            public void forEachRemaining(T_CONS consumer) {
                int i;
                consumer.getClass();
                int i2 = this.splSpineIndex;
                int i3 = this.lastSpineIndex;
                if (i2 < i3 || (i2 == i3 && this.splElementIndex < this.lastSpineElementFence)) {
                    int i4 = this.splElementIndex;
                    int sp = this.splSpineIndex;
                    while (true) {
                        i = this.lastSpineIndex;
                        if (sp >= i) {
                            break;
                        }
                        T_ARR chunk = OfPrimitive.this.spine[sp];
                        OfPrimitive ofPrimitive = OfPrimitive.this;
                        ofPrimitive.arrayForEach(chunk, i4, ofPrimitive.arrayLength(chunk), consumer);
                        i4 = 0;
                        sp++;
                    }
                    int sp2 = this.splSpineIndex;
                    OfPrimitive.this.arrayForEach(sp2 == i ? this.splChunk : OfPrimitive.this.spine[this.lastSpineIndex], i4, this.lastSpineElementFence, consumer);
                    this.splSpineIndex = this.lastSpineIndex;
                    this.splElementIndex = this.lastSpineElementFence;
                }
            }

            /* JADX WARN: Generic types in debug info not equals: j$.util.stream.SpinedBuffer$OfPrimitive$BaseSpliterator != java.util.stream.SpinedBuffer$OfPrimitive<E, T_ARR, T_CONS>$BaseSpliterator<T_SPLITR extends j$.util.Spliterator$OfPrimitive<E, T_CONS, T_SPLITR>> */
            @Override // j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public T_SPLITR trySplit() {
                int i = this.splSpineIndex;
                int i2 = this.lastSpineIndex;
                if (i < i2) {
                    int i3 = this.splElementIndex;
                    OfPrimitive ofPrimitive = OfPrimitive.this;
                    T_SPLITR ret = newSpliterator(i, i2 - 1, i3, ofPrimitive.arrayLength(ofPrimitive.spine[this.lastSpineIndex - 1]));
                    this.splSpineIndex = this.lastSpineIndex;
                    this.splElementIndex = 0;
                    this.splChunk = OfPrimitive.this.spine[this.splSpineIndex];
                    return ret;
                } else if (i != i2) {
                    return null;
                } else {
                    int i4 = this.lastSpineElementFence;
                    int i5 = this.splElementIndex;
                    int t = (i4 - i5) / 2;
                    if (t == 0) {
                        return null;
                    }
                    T_SPLITR ret2 = arraySpliterator(this.splChunk, i5, t);
                    this.splElementIndex += t;
                    return ret2;
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class OfInt extends OfPrimitive<Integer, int[], IntConsumer> implements IntConsumer {
        @Override // j$.util.function.IntConsumer
        public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
            return intConsumer.getClass();
        }

        public OfInt() {
        }

        public OfInt(int initialCapacity) {
            super(initialCapacity);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super java.lang.Integer> */
        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, j$.lang.Iterable
        public void forEach(Consumer<? super Integer> consumer) {
            if (consumer instanceof IntConsumer) {
                forEach((OfInt) consumer);
                return;
            }
            if (Tripwire.ENABLED) {
                Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
            }
            spliterator().forEachRemaining(consumer);
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive
        public int[][] newArrayArray(int size) {
            return new int[size];
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive
        public int[] newArray(int size) {
            return new int[size];
        }

        public int arrayLength(int[] array) {
            return array.length;
        }

        public void arrayForEach(int[] array, int from, int to, IntConsumer consumer) {
            for (int i = from; i < to; i++) {
                consumer.accept(array[i]);
            }
        }

        public void accept(int i) {
            preAccept();
            int i2 = this.elementIndex;
            this.elementIndex = i2 + 1;
            ((int[]) this.curChunk)[i2] = i;
        }

        public int get(long index) {
            int ch = chunkFor(index);
            if (this.spineIndex == 0 && ch == 0) {
                return ((int[]) this.curChunk)[(int) index];
            }
            return ((int[][]) this.spine)[ch][(int) (index - this.priorElementCount[ch])];
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, java.lang.Iterable, j$.lang.Iterable
        public PrimitiveIterator.OfInt iterator() {
            return Spliterators.iterator(spliterator());
        }

        /* renamed from: j$.util.stream.SpinedBuffer$OfInt$1Splitr */
        /* loaded from: classes2.dex */
        public class C1Splitr extends OfPrimitive<Integer, int[], IntConsumer>.BaseSpliterator<Spliterator.OfInt> implements Spliterator.OfInt {
            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfInt.CC.$default$forEachRemaining((Spliterator.OfInt) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfInt.CC.$default$tryAdvance((Spliterator.OfInt) this, consumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
                super.forEachRemaining((C1Splitr) intConsumer);
            }

            @Override // j$.util.Spliterator.OfInt
            public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
                return super.tryAdvance((C1Splitr) intConsumer);
            }

            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfInt trySplit() {
                return (Spliterator.OfInt) super.trySplit();
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            C1Splitr(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                super(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
                OfInt.this = this$0;
            }

            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator
            public Spliterator.OfInt newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                return new C1Splitr(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
            }

            public void arrayForOne(int[] array, int index, IntConsumer consumer) {
                consumer.accept(array[index]);
            }

            public Spliterator.OfInt arraySpliterator(int[] array, int offset, int len) {
                return DesugarArrays.spliterator(array, offset, offset + len);
            }
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, java.lang.Iterable, j$.lang.Iterable
        public Spliterator.OfInt spliterator() {
            return new C1Splitr(0, this.spineIndex, 0, this.elementIndex);
        }

        public String toString() {
            int[] array = asPrimitiveArray();
            if (array.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.spineIndex), Arrays.toString(array));
            }
            int[] array2 = Arrays.copyOf(array, 200);
            return String.format("%s[length=%d, chunks=%d]%s...", getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.spineIndex), Arrays.toString(array2));
        }
    }

    /* loaded from: classes2.dex */
    public static class OfLong extends OfPrimitive<Long, long[], LongConsumer> implements LongConsumer {
        @Override // j$.util.function.LongConsumer
        public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
            return longConsumer.getClass();
        }

        public OfLong() {
        }

        public OfLong(int initialCapacity) {
            super(initialCapacity);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super java.lang.Long> */
        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, j$.lang.Iterable
        public void forEach(Consumer<? super Long> consumer) {
            if (consumer instanceof LongConsumer) {
                forEach((OfLong) consumer);
                return;
            }
            if (Tripwire.ENABLED) {
                Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
            }
            spliterator().forEachRemaining(consumer);
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive
        public long[][] newArrayArray(int size) {
            return new long[size];
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive
        public long[] newArray(int size) {
            return new long[size];
        }

        public int arrayLength(long[] array) {
            return array.length;
        }

        public void arrayForEach(long[] array, int from, int to, LongConsumer consumer) {
            for (int i = from; i < to; i++) {
                consumer.accept(array[i]);
            }
        }

        public void accept(long i) {
            preAccept();
            int i2 = this.elementIndex;
            this.elementIndex = i2 + 1;
            ((long[]) this.curChunk)[i2] = i;
        }

        public long get(long index) {
            int ch = chunkFor(index);
            if (this.spineIndex == 0 && ch == 0) {
                return ((long[]) this.curChunk)[(int) index];
            }
            return ((long[][]) this.spine)[ch][(int) (index - this.priorElementCount[ch])];
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, java.lang.Iterable, j$.lang.Iterable
        public PrimitiveIterator.OfLong iterator() {
            return Spliterators.iterator(spliterator());
        }

        /* renamed from: j$.util.stream.SpinedBuffer$OfLong$1Splitr */
        /* loaded from: classes2.dex */
        public class C1Splitr extends OfPrimitive<Long, long[], LongConsumer>.BaseSpliterator<Spliterator.OfLong> implements Spliterator.OfLong {
            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfLong.CC.$default$forEachRemaining((Spliterator.OfLong) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfLong.CC.$default$tryAdvance((Spliterator.OfLong) this, consumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
                super.forEachRemaining((C1Splitr) longConsumer);
            }

            @Override // j$.util.Spliterator.OfLong
            public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
                return super.tryAdvance((C1Splitr) longConsumer);
            }

            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfLong trySplit() {
                return (Spliterator.OfLong) super.trySplit();
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            C1Splitr(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                super(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
                OfLong.this = this$0;
            }

            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator
            public Spliterator.OfLong newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                return new C1Splitr(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
            }

            public void arrayForOne(long[] array, int index, LongConsumer consumer) {
                consumer.accept(array[index]);
            }

            public Spliterator.OfLong arraySpliterator(long[] array, int offset, int len) {
                return DesugarArrays.spliterator(array, offset, offset + len);
            }
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, java.lang.Iterable, j$.lang.Iterable
        public Spliterator.OfLong spliterator() {
            return new C1Splitr(0, this.spineIndex, 0, this.elementIndex);
        }

        public String toString() {
            long[] array = asPrimitiveArray();
            if (array.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.spineIndex), Arrays.toString(array));
            }
            long[] array2 = Arrays.copyOf(array, 200);
            return String.format("%s[length=%d, chunks=%d]%s...", getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.spineIndex), Arrays.toString(array2));
        }
    }

    /* loaded from: classes2.dex */
    public static class OfDouble extends OfPrimitive<Double, double[], DoubleConsumer> implements DoubleConsumer {
        @Override // j$.util.function.DoubleConsumer
        public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
            return doubleConsumer.getClass();
        }

        public OfDouble() {
        }

        public OfDouble(int initialCapacity) {
            super(initialCapacity);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super java.lang.Double> */
        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, j$.lang.Iterable
        public void forEach(Consumer<? super Double> consumer) {
            if (consumer instanceof DoubleConsumer) {
                forEach((OfDouble) consumer);
                return;
            }
            if (Tripwire.ENABLED) {
                Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
            }
            spliterator().forEachRemaining(consumer);
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive
        public double[][] newArrayArray(int size) {
            return new double[size];
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive
        public double[] newArray(int size) {
            return new double[size];
        }

        public int arrayLength(double[] array) {
            return array.length;
        }

        public void arrayForEach(double[] array, int from, int to, DoubleConsumer consumer) {
            for (int i = from; i < to; i++) {
                consumer.accept(array[i]);
            }
        }

        public void accept(double i) {
            preAccept();
            int i2 = this.elementIndex;
            this.elementIndex = i2 + 1;
            ((double[]) this.curChunk)[i2] = i;
        }

        public double get(long index) {
            int ch = chunkFor(index);
            if (this.spineIndex == 0 && ch == 0) {
                return ((double[]) this.curChunk)[(int) index];
            }
            return ((double[][]) this.spine)[ch][(int) (index - this.priorElementCount[ch])];
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, java.lang.Iterable, j$.lang.Iterable
        public PrimitiveIterator.OfDouble iterator() {
            return Spliterators.iterator(spliterator());
        }

        /* renamed from: j$.util.stream.SpinedBuffer$OfDouble$1Splitr */
        /* loaded from: classes2.dex */
        public class C1Splitr extends OfPrimitive<Double, double[], DoubleConsumer>.BaseSpliterator<Spliterator.OfDouble> implements Spliterator.OfDouble {
            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator, j$.util.Spliterator
            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                Spliterator.OfDouble.CC.$default$forEachRemaining((Spliterator.OfDouble) this, consumer);
            }

            @Override // j$.util.Spliterator
            public /* synthetic */ boolean tryAdvance(Consumer consumer) {
                return Spliterator.OfDouble.CC.$default$tryAdvance((Spliterator.OfDouble) this, consumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
                super.forEachRemaining((C1Splitr) doubleConsumer);
            }

            @Override // j$.util.Spliterator.OfDouble
            public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
                return super.tryAdvance((C1Splitr) doubleConsumer);
            }

            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator, j$.util.Spliterator.OfPrimitive, j$.util.Spliterator
            public /* bridge */ /* synthetic */ Spliterator.OfDouble trySplit() {
                return (Spliterator.OfDouble) super.trySplit();
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            C1Splitr(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                super(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
                OfDouble.this = this$0;
            }

            @Override // j$.util.stream.SpinedBuffer.OfPrimitive.BaseSpliterator
            public Spliterator.OfDouble newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                return new C1Splitr(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
            }

            public void arrayForOne(double[] array, int index, DoubleConsumer consumer) {
                consumer.accept(array[index]);
            }

            public Spliterator.OfDouble arraySpliterator(double[] array, int offset, int len) {
                return DesugarArrays.spliterator(array, offset, offset + len);
            }
        }

        @Override // j$.util.stream.SpinedBuffer.OfPrimitive, java.lang.Iterable, j$.lang.Iterable
        public Spliterator.OfDouble spliterator() {
            return new C1Splitr(0, this.spineIndex, 0, this.elementIndex);
        }

        public String toString() {
            double[] array = asPrimitiveArray();
            if (array.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s", getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.spineIndex), Arrays.toString(array));
            }
            double[] array2 = Arrays.copyOf(array, 200);
            return String.format("%s[length=%d, chunks=%d]%s...", getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.spineIndex), Arrays.toString(array2));
        }
    }
}
