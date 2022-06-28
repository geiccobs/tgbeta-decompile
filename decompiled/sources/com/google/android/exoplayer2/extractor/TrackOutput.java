package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.util.Arrays;
/* loaded from: classes3.dex */
public interface TrackOutput {
    void format(Format format);

    int sampleData(ExtractorInput extractorInput, int i, boolean z) throws IOException, InterruptedException;

    void sampleData(ParsableByteArray parsableByteArray, int i);

    void sampleMetadata(long j, int i, int i2, int i3, CryptoData cryptoData);

    /* loaded from: classes3.dex */
    public static final class CryptoData {
        public final int clearBlocks;
        public final int cryptoMode;
        public final int encryptedBlocks;
        public final byte[] encryptionKey;

        public CryptoData(int cryptoMode, byte[] encryptionKey, int encryptedBlocks, int clearBlocks) {
            this.cryptoMode = cryptoMode;
            this.encryptionKey = encryptionKey;
            this.encryptedBlocks = encryptedBlocks;
            this.clearBlocks = clearBlocks;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            CryptoData other = (CryptoData) obj;
            return this.cryptoMode == other.cryptoMode && this.encryptedBlocks == other.encryptedBlocks && this.clearBlocks == other.clearBlocks && Arrays.equals(this.encryptionKey, other.encryptionKey);
        }

        public int hashCode() {
            int result = this.cryptoMode;
            return (((((result * 31) + Arrays.hashCode(this.encryptionKey)) * 31) + this.encryptedBlocks) * 31) + this.clearBlocks;
        }
    }
}
