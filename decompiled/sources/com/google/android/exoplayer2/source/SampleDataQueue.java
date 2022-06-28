package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.upstream.Allocation;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class SampleDataQueue {
    private static final int INITIAL_SCRATCH_SIZE = 32;
    private final int allocationLength;
    private final Allocator allocator;
    private AllocationNode firstAllocationNode;
    private AllocationNode readAllocationNode;
    private final ParsableByteArray scratch = new ParsableByteArray(32);
    private long totalBytesWritten;
    private AllocationNode writeAllocationNode;

    public SampleDataQueue(Allocator allocator) {
        this.allocator = allocator;
        int individualAllocationLength = allocator.getIndividualAllocationLength();
        this.allocationLength = individualAllocationLength;
        AllocationNode allocationNode = new AllocationNode(0L, individualAllocationLength);
        this.firstAllocationNode = allocationNode;
        this.readAllocationNode = allocationNode;
        this.writeAllocationNode = allocationNode;
    }

    public void reset() {
        clearAllocationNodes(this.firstAllocationNode);
        AllocationNode allocationNode = new AllocationNode(0L, this.allocationLength);
        this.firstAllocationNode = allocationNode;
        this.readAllocationNode = allocationNode;
        this.writeAllocationNode = allocationNode;
        this.totalBytesWritten = 0L;
        this.allocator.trim();
    }

    public void discardUpstreamSampleBytes(long totalBytesWritten) {
        AllocationNode allocationNode;
        this.totalBytesWritten = totalBytesWritten;
        if (totalBytesWritten == 0 || totalBytesWritten == this.firstAllocationNode.startPosition) {
            AllocationNode lastNodeToKeep = this.firstAllocationNode;
            clearAllocationNodes(lastNodeToKeep);
            AllocationNode allocationNode2 = new AllocationNode(this.totalBytesWritten, this.allocationLength);
            this.firstAllocationNode = allocationNode2;
            this.readAllocationNode = allocationNode2;
            this.writeAllocationNode = allocationNode2;
            return;
        }
        AllocationNode lastNodeToKeep2 = this.firstAllocationNode;
        while (this.totalBytesWritten > lastNodeToKeep2.endPosition) {
            lastNodeToKeep2 = lastNodeToKeep2.next;
        }
        AllocationNode firstNodeToDiscard = lastNodeToKeep2.next;
        clearAllocationNodes(firstNodeToDiscard);
        lastNodeToKeep2.next = new AllocationNode(lastNodeToKeep2.endPosition, this.allocationLength);
        if (this.totalBytesWritten == lastNodeToKeep2.endPosition) {
            allocationNode = lastNodeToKeep2.next;
        } else {
            allocationNode = lastNodeToKeep2;
        }
        this.writeAllocationNode = allocationNode;
        if (this.readAllocationNode == firstNodeToDiscard) {
            this.readAllocationNode = lastNodeToKeep2.next;
        }
    }

    public void rewind() {
        this.readAllocationNode = this.firstAllocationNode;
    }

    public void readToBuffer(DecoderInputBuffer buffer, SampleQueue.SampleExtrasHolder extrasHolder) {
        if (buffer.isEncrypted()) {
            readEncryptionData(buffer, extrasHolder);
        }
        if (buffer.hasSupplementalData()) {
            this.scratch.reset(4);
            readData(extrasHolder.offset, this.scratch.data, 4);
            int sampleSize = this.scratch.readUnsignedIntToInt();
            extrasHolder.offset += 4;
            extrasHolder.size -= 4;
            buffer.ensureSpaceForWrite(sampleSize);
            readData(extrasHolder.offset, buffer.data, sampleSize);
            extrasHolder.offset += sampleSize;
            extrasHolder.size -= sampleSize;
            buffer.resetSupplementalData(extrasHolder.size);
            readData(extrasHolder.offset, buffer.supplementalData, extrasHolder.size);
            return;
        }
        buffer.ensureSpaceForWrite(extrasHolder.size);
        readData(extrasHolder.offset, buffer.data, extrasHolder.size);
    }

    public void discardDownstreamTo(long absolutePosition) {
        if (absolutePosition == -1) {
            return;
        }
        while (absolutePosition >= this.firstAllocationNode.endPosition) {
            this.allocator.release(this.firstAllocationNode.allocation);
            this.firstAllocationNode = this.firstAllocationNode.clear();
        }
        if (this.readAllocationNode.startPosition < this.firstAllocationNode.startPosition) {
            this.readAllocationNode = this.firstAllocationNode;
        }
    }

    public long getTotalBytesWritten() {
        return this.totalBytesWritten;
    }

    public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        int bytesAppended = input.read(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), preAppend(length));
        if (bytesAppended == -1) {
            if (allowEndOfInput) {
                return -1;
            }
            throw new EOFException();
        }
        postAppend(bytesAppended);
        return bytesAppended;
    }

    public void sampleData(ParsableByteArray buffer, int length) {
        while (length > 0) {
            int bytesAppended = preAppend(length);
            buffer.readBytes(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), bytesAppended);
            length -= bytesAppended;
            postAppend(bytesAppended);
        }
    }

    private void readEncryptionData(DecoderInputBuffer buffer, SampleQueue.SampleExtrasHolder extrasHolder) {
        int subsampleCount;
        long offset = extrasHolder.offset;
        boolean subsampleEncryption = true;
        this.scratch.reset(1);
        readData(offset, this.scratch.data, 1);
        long offset2 = offset + 1;
        byte signalByte = this.scratch.data[0];
        if ((signalByte & 128) == 0) {
            subsampleEncryption = false;
        }
        int ivSize = signalByte & Byte.MAX_VALUE;
        CryptoInfo cryptoInfo = buffer.cryptoInfo;
        if (cryptoInfo.iv == null) {
            cryptoInfo.iv = new byte[16];
        } else {
            Arrays.fill(cryptoInfo.iv, (byte) 0);
        }
        readData(offset2, cryptoInfo.iv, ivSize);
        long offset3 = offset2 + ivSize;
        if (subsampleEncryption) {
            this.scratch.reset(2);
            readData(offset3, this.scratch.data, 2);
            offset3 += 2;
            subsampleCount = this.scratch.readUnsignedShort();
        } else {
            subsampleCount = 1;
        }
        int[] clearDataSizes = cryptoInfo.numBytesOfClearData;
        int[] clearDataSizes2 = (clearDataSizes == null || clearDataSizes.length < subsampleCount) ? new int[subsampleCount] : clearDataSizes;
        int[] clearDataSizes3 = cryptoInfo.numBytesOfEncryptedData;
        int[] encryptedDataSizes = (clearDataSizes3 == null || clearDataSizes3.length < subsampleCount) ? new int[subsampleCount] : clearDataSizes3;
        if (subsampleEncryption) {
            int subsampleDataLength = subsampleCount * 6;
            this.scratch.reset(subsampleDataLength);
            readData(offset3, this.scratch.data, subsampleDataLength);
            offset3 += subsampleDataLength;
            this.scratch.setPosition(0);
            for (int i = 0; i < subsampleCount; i++) {
                clearDataSizes2[i] = this.scratch.readUnsignedShort();
                encryptedDataSizes[i] = this.scratch.readUnsignedIntToInt();
            }
        } else {
            clearDataSizes2[0] = 0;
            encryptedDataSizes[0] = extrasHolder.size - ((int) (offset3 - extrasHolder.offset));
        }
        TrackOutput.CryptoData cryptoData = extrasHolder.cryptoData;
        cryptoInfo.set(subsampleCount, clearDataSizes2, encryptedDataSizes, cryptoData.encryptionKey, cryptoInfo.iv, cryptoData.cryptoMode, cryptoData.encryptedBlocks, cryptoData.clearBlocks);
        int bytesRead = (int) (offset3 - extrasHolder.offset);
        extrasHolder.offset += bytesRead;
        extrasHolder.size -= bytesRead;
    }

    private void readData(long absolutePosition, ByteBuffer target, int length) {
        advanceReadTo(absolutePosition);
        int remaining = length;
        while (remaining > 0) {
            int toCopy = Math.min(remaining, (int) (this.readAllocationNode.endPosition - absolutePosition));
            Allocation allocation = this.readAllocationNode.allocation;
            target.put(allocation.data, this.readAllocationNode.translateOffset(absolutePosition), toCopy);
            remaining -= toCopy;
            absolutePosition += toCopy;
            if (absolutePosition == this.readAllocationNode.endPosition) {
                this.readAllocationNode = this.readAllocationNode.next;
            }
        }
    }

    private void readData(long absolutePosition, byte[] target, int length) {
        advanceReadTo(absolutePosition);
        int remaining = length;
        while (remaining > 0) {
            int toCopy = Math.min(remaining, (int) (this.readAllocationNode.endPosition - absolutePosition));
            Allocation allocation = this.readAllocationNode.allocation;
            System.arraycopy(allocation.data, this.readAllocationNode.translateOffset(absolutePosition), target, length - remaining, toCopy);
            remaining -= toCopy;
            absolutePosition += toCopy;
            if (absolutePosition == this.readAllocationNode.endPosition) {
                this.readAllocationNode = this.readAllocationNode.next;
            }
        }
    }

    private void advanceReadTo(long absolutePosition) {
        while (absolutePosition >= this.readAllocationNode.endPosition) {
            this.readAllocationNode = this.readAllocationNode.next;
        }
    }

    private void clearAllocationNodes(AllocationNode fromNode) {
        if (!fromNode.wasInitialized) {
            return;
        }
        int allocationCount = (this.writeAllocationNode.wasInitialized ? 1 : 0) + (((int) (this.writeAllocationNode.startPosition - fromNode.startPosition)) / this.allocationLength);
        Allocation[] allocationsToRelease = new Allocation[allocationCount];
        AllocationNode currentNode = fromNode;
        for (int i = 0; i < allocationsToRelease.length; i++) {
            allocationsToRelease[i] = currentNode.allocation;
            currentNode = currentNode.clear();
        }
        this.allocator.release(allocationsToRelease);
    }

    private int preAppend(int length) {
        if (!this.writeAllocationNode.wasInitialized) {
            this.writeAllocationNode.initialize(this.allocator.allocate(), new AllocationNode(this.writeAllocationNode.endPosition, this.allocationLength));
        }
        return Math.min(length, (int) (this.writeAllocationNode.endPosition - this.totalBytesWritten));
    }

    private void postAppend(int length) {
        long j = this.totalBytesWritten + length;
        this.totalBytesWritten = j;
        if (j == this.writeAllocationNode.endPosition) {
            this.writeAllocationNode = this.writeAllocationNode.next;
        }
    }

    /* loaded from: classes3.dex */
    public static final class AllocationNode {
        public Allocation allocation;
        public final long endPosition;
        public AllocationNode next;
        public final long startPosition;
        public boolean wasInitialized;

        public AllocationNode(long startPosition, int allocationLength) {
            this.startPosition = startPosition;
            this.endPosition = allocationLength + startPosition;
        }

        public void initialize(Allocation allocation, AllocationNode next) {
            this.allocation = allocation;
            this.next = next;
            this.wasInitialized = true;
        }

        public int translateOffset(long absolutePosition) {
            return ((int) (absolutePosition - this.startPosition)) + this.allocation.offset;
        }

        public AllocationNode clear() {
            this.allocation = null;
            AllocationNode temp = this.next;
            this.next = null;
            return temp;
        }
    }
}
