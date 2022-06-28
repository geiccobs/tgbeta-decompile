package com.mp4parser.iso23001.part7;

import com.coremedia.iso.Hex;
import java.math.BigInteger;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class CencSampleAuxiliaryDataFormat {
    public byte[] iv = new byte[0];
    public Pair[] pairs = null;

    /* loaded from: classes3.dex */
    public interface Pair {
        int clear();

        long encrypted();
    }

    public int getSize() {
        int size = this.iv.length;
        Pair[] pairArr = this.pairs;
        if (pairArr != null && pairArr.length > 0) {
            return size + 2 + (pairArr.length * 6);
        }
        return size;
    }

    public Pair createPair(int clear, long encrypted) {
        if (clear <= 127) {
            if (encrypted <= 127) {
                return new ByteBytePair(clear, encrypted);
            }
            if (encrypted <= 32767) {
                return new ByteShortPair(clear, encrypted);
            }
            if (encrypted <= 2147483647L) {
                return new ByteIntPair(clear, encrypted);
            }
            return new ByteLongPair(clear, encrypted);
        } else if (clear <= 32767) {
            if (encrypted <= 127) {
                return new ShortBytePair(clear, encrypted);
            }
            if (encrypted <= 32767) {
                return new ShortShortPair(clear, encrypted);
            }
            if (encrypted <= 2147483647L) {
                return new ShortIntPair(clear, encrypted);
            }
            return new ShortLongPair(clear, encrypted);
        } else if (encrypted <= 127) {
            return new IntBytePair(clear, encrypted);
        } else {
            if (encrypted <= 32767) {
                return new IntShortPair(clear, encrypted);
            }
            if (encrypted <= 2147483647L) {
                return new IntIntPair(clear, encrypted);
            }
            return new IntLongPair(clear, encrypted);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CencSampleAuxiliaryDataFormat entry = (CencSampleAuxiliaryDataFormat) o;
        if (!new BigInteger(this.iv).equals(new BigInteger(entry.iv))) {
            return false;
        }
        Pair[] pairArr = this.pairs;
        if (pairArr == null ? entry.pairs == null : Arrays.equals(pairArr, entry.pairs)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        byte[] bArr = this.iv;
        int i = 0;
        int result = bArr != null ? Arrays.hashCode(bArr) : 0;
        int i2 = result * 31;
        Pair[] pairArr = this.pairs;
        if (pairArr != null) {
            i = Arrays.hashCode(pairArr);
        }
        int result2 = i2 + i;
        return result2;
    }

    public String toString() {
        return "Entry{iv=" + Hex.encodeHex(this.iv) + ", pairs=" + Arrays.toString(this.pairs) + '}';
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ByteBytePair extends AbstractPair {
        private byte clear;
        private byte encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ByteBytePair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = (byte) clear;
            this.encrypted = (byte) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ByteShortPair extends AbstractPair {
        private byte clear;
        private short encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ByteShortPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = (byte) clear;
            this.encrypted = (short) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ByteIntPair extends AbstractPair {
        private byte clear;
        private int encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ByteIntPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = (byte) clear;
            this.encrypted = (int) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ByteLongPair extends AbstractPair {
        private byte clear;
        private long encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ByteLongPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = (byte) clear;
            this.encrypted = encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ShortBytePair extends AbstractPair {
        private short clear;
        private byte encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ShortBytePair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = (short) clear;
            this.encrypted = (byte) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ShortShortPair extends AbstractPair {
        private short clear;
        private short encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ShortShortPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = (short) clear;
            this.encrypted = (short) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ShortIntPair extends AbstractPair {
        private short clear;
        private int encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ShortIntPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = (short) clear;
            this.encrypted = (int) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ShortLongPair extends AbstractPair {
        private short clear;
        private long encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ShortLongPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = (short) clear;
            this.encrypted = encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class IntBytePair extends AbstractPair {
        private int clear;
        private byte encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public IntBytePair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = clear;
            this.encrypted = (byte) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class IntShortPair extends AbstractPair {
        private int clear;
        private short encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public IntShortPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = clear;
            this.encrypted = (short) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class IntIntPair extends AbstractPair {
        private int clear;
        private int encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public IntIntPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = clear;
            this.encrypted = (int) encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class IntLongPair extends AbstractPair {
        private int clear;
        private long encrypted;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public IntLongPair(int clear, long encrypted) {
            super(r2, null);
            CencSampleAuxiliaryDataFormat.this = r2;
            this.clear = clear;
            this.encrypted = encrypted;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public int clear() {
            return this.clear;
        }

        @Override // com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair
        public long encrypted() {
            return this.encrypted;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public abstract class AbstractPair implements Pair {
        private AbstractPair() {
            CencSampleAuxiliaryDataFormat.this = r1;
        }

        /* synthetic */ AbstractPair(CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat, AbstractPair abstractPair) {
            this();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Pair pair = (Pair) o;
            if (clear() == pair.clear() && encrypted() == pair.encrypted()) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "P(" + clear() + "|" + encrypted() + ")";
        }
    }
}
