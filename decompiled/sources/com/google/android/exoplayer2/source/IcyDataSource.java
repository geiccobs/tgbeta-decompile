package com.google.android.exoplayer2.source;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
final class IcyDataSource implements DataSource {
    private int bytesUntilMetadata;
    private final Listener listener;
    private final int metadataIntervalBytes;
    private final byte[] metadataLengthByteHolder;
    private final DataSource upstream;

    /* loaded from: classes3.dex */
    public interface Listener {
        void onIcyMetadata(ParsableByteArray parsableByteArray);
    }

    public IcyDataSource(DataSource upstream, int metadataIntervalBytes, Listener listener) {
        Assertions.checkArgument(metadataIntervalBytes > 0);
        this.upstream = upstream;
        this.metadataIntervalBytes = metadataIntervalBytes;
        this.listener = listener;
        this.metadataLengthByteHolder = new byte[1];
        this.bytesUntilMetadata = metadataIntervalBytes;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void addTransferListener(TransferListener transferListener) {
        this.upstream.addTransferListener(transferListener);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public long open(DataSpec dataSpec) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (this.bytesUntilMetadata == 0) {
            if (!readMetadata()) {
                return -1;
            }
            this.bytesUntilMetadata = this.metadataIntervalBytes;
        }
        int bytesRead = this.upstream.read(buffer, offset, Math.min(this.bytesUntilMetadata, readLength));
        if (bytesRead != -1) {
            this.bytesUntilMetadata -= bytesRead;
        }
        return bytesRead;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Uri getUri() {
        return this.upstream.getUri();
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Map<String, List<String>> getResponseHeaders() {
        return this.upstream.getResponseHeaders();
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }

    private boolean readMetadata() throws IOException {
        if (this.upstream.read(this.metadataLengthByteHolder, 0, 1) == -1) {
            return false;
        }
        int metadataLength = (this.metadataLengthByteHolder[0] & 255) << 4;
        if (metadataLength == 0) {
            return true;
        }
        int offset = 0;
        int lengthRemaining = metadataLength;
        byte[] metadata = new byte[metadataLength];
        while (lengthRemaining > 0) {
            int bytesRead = this.upstream.read(metadata, offset, lengthRemaining);
            if (bytesRead == -1) {
                return false;
            }
            offset += bytesRead;
            lengthRemaining -= bytesRead;
        }
        while (metadataLength > 0 && metadata[metadataLength - 1] == 0) {
            metadataLength--;
        }
        if (metadataLength > 0) {
            this.listener.onIcyMetadata(new ParsableByteArray(metadata, metadataLength));
        }
        return true;
    }
}
