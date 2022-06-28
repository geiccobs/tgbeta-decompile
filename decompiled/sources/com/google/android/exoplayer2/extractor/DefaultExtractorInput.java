package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class DefaultExtractorInput implements ExtractorInput {
    private static final int PEEK_MAX_FREE_SPACE = 524288;
    private static final int PEEK_MIN_FREE_SPACE_AFTER_RESIZE = 65536;
    private static final int SCRATCH_SPACE_SIZE = 4096;
    private final DataSource dataSource;
    private int peekBufferLength;
    private int peekBufferPosition;
    private long position;
    private final long streamLength;
    private byte[] peekBuffer = new byte[65536];
    private final byte[] scratchSpace = new byte[4096];

    public DefaultExtractorInput(DataSource dataSource, long position, long length) {
        this.dataSource = dataSource;
        this.position = position;
        this.streamLength = length;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public int read(byte[] target, int offset, int length) throws IOException, InterruptedException {
        int bytesRead = readFromPeekBuffer(target, offset, length);
        if (bytesRead == 0) {
            bytesRead = readFromDataSource(target, offset, length, 0, true);
        }
        commitBytesRead(bytesRead);
        return bytesRead;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public boolean readFully(byte[] target, int offset, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        int bytesRead = readFromPeekBuffer(target, offset, length);
        while (bytesRead < length && bytesRead != -1) {
            bytesRead = readFromDataSource(target, offset, length, bytesRead, allowEndOfInput);
        }
        commitBytesRead(bytesRead);
        return bytesRead != -1;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public void readFully(byte[] target, int offset, int length) throws IOException, InterruptedException {
        readFully(target, offset, length, false);
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public int skip(int length) throws IOException, InterruptedException {
        int bytesSkipped = skipFromPeekBuffer(length);
        if (bytesSkipped == 0) {
            byte[] bArr = this.scratchSpace;
            bytesSkipped = readFromDataSource(bArr, 0, Math.min(length, bArr.length), 0, true);
        }
        commitBytesRead(bytesSkipped);
        return bytesSkipped;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public boolean skipFully(int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        int bytesSkipped = skipFromPeekBuffer(length);
        while (bytesSkipped < length && bytesSkipped != -1) {
            int minLength = Math.min(length, this.scratchSpace.length + bytesSkipped);
            bytesSkipped = readFromDataSource(this.scratchSpace, -bytesSkipped, minLength, bytesSkipped, allowEndOfInput);
        }
        commitBytesRead(bytesSkipped);
        return bytesSkipped != -1;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public void skipFully(int length) throws IOException, InterruptedException {
        skipFully(length, false);
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public int peek(byte[] target, int offset, int length) throws IOException, InterruptedException {
        int bytesPeeked;
        ensureSpaceForPeek(length);
        int i = this.peekBufferLength;
        int i2 = this.peekBufferPosition;
        int peekBufferRemainingBytes = i - i2;
        if (peekBufferRemainingBytes == 0) {
            bytesPeeked = readFromDataSource(this.peekBuffer, i2, length, 0, true);
            if (bytesPeeked == -1) {
                return -1;
            }
            this.peekBufferLength += bytesPeeked;
        } else {
            bytesPeeked = Math.min(length, peekBufferRemainingBytes);
        }
        System.arraycopy(this.peekBuffer, this.peekBufferPosition, target, offset, bytesPeeked);
        this.peekBufferPosition += bytesPeeked;
        return bytesPeeked;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public boolean peekFully(byte[] target, int offset, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        if (!advancePeekPosition(length, allowEndOfInput)) {
            return false;
        }
        System.arraycopy(this.peekBuffer, this.peekBufferPosition - length, target, offset, length);
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public void peekFully(byte[] target, int offset, int length) throws IOException, InterruptedException {
        peekFully(target, offset, length, false);
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public boolean advancePeekPosition(int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        ensureSpaceForPeek(length);
        int bytesPeeked = this.peekBufferLength - this.peekBufferPosition;
        while (bytesPeeked < length) {
            bytesPeeked = readFromDataSource(this.peekBuffer, this.peekBufferPosition, length, bytesPeeked, allowEndOfInput);
            if (bytesPeeked == -1) {
                return false;
            }
            this.peekBufferLength = this.peekBufferPosition + bytesPeeked;
        }
        this.peekBufferPosition += length;
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public void advancePeekPosition(int length) throws IOException, InterruptedException {
        advancePeekPosition(length, false);
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public void resetPeekPosition() {
        this.peekBufferPosition = 0;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public long getPeekPosition() {
        return this.position + this.peekBufferPosition;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public long getPosition() {
        return this.position;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public long getLength() {
        return this.streamLength;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorInput
    public <E extends Throwable> void setRetryPosition(long position, E e) throws Throwable {
        Assertions.checkArgument(position >= 0);
        this.position = position;
        throw e;
    }

    private void ensureSpaceForPeek(int length) {
        int requiredLength = this.peekBufferPosition + length;
        byte[] bArr = this.peekBuffer;
        if (requiredLength > bArr.length) {
            int newPeekCapacity = Util.constrainValue(bArr.length * 2, 65536 + requiredLength, 524288 + requiredLength);
            this.peekBuffer = Arrays.copyOf(this.peekBuffer, newPeekCapacity);
        }
    }

    private int skipFromPeekBuffer(int length) {
        int bytesSkipped = Math.min(this.peekBufferLength, length);
        updatePeekBuffer(bytesSkipped);
        return bytesSkipped;
    }

    private int readFromPeekBuffer(byte[] target, int offset, int length) {
        int i = this.peekBufferLength;
        if (i == 0) {
            return 0;
        }
        int peekBytes = Math.min(i, length);
        System.arraycopy(this.peekBuffer, 0, target, offset, peekBytes);
        updatePeekBuffer(peekBytes);
        return peekBytes;
    }

    private void updatePeekBuffer(int bytesConsumed) {
        int i = this.peekBufferLength - bytesConsumed;
        this.peekBufferLength = i;
        this.peekBufferPosition = 0;
        byte[] newPeekBuffer = this.peekBuffer;
        byte[] bArr = this.peekBuffer;
        if (i < bArr.length - 524288) {
            newPeekBuffer = new byte[65536 + i];
        }
        System.arraycopy(bArr, bytesConsumed, newPeekBuffer, 0, i);
        this.peekBuffer = newPeekBuffer;
    }

    private int readFromDataSource(byte[] target, int offset, int length, int bytesAlreadyRead, boolean allowEndOfInput) throws InterruptedException, IOException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        int bytesRead = this.dataSource.read(target, offset + bytesAlreadyRead, length - bytesAlreadyRead);
        if (bytesRead == -1) {
            if (bytesAlreadyRead == 0 && allowEndOfInput) {
                return -1;
            }
            throw new EOFException();
        }
        return bytesAlreadyRead + bytesRead;
    }

    private void commitBytesRead(int bytesRead) {
        if (bytesRead != -1) {
            this.position += bytesRead;
        }
    }
}
