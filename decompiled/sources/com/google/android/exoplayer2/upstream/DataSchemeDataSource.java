package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import android.util.Base64;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.net.URLDecoder;
/* loaded from: classes3.dex */
public final class DataSchemeDataSource extends BaseDataSource {
    public static final String SCHEME_DATA = "data";
    private byte[] data;
    private DataSpec dataSpec;
    private int endPosition;
    private int readPosition;

    public DataSchemeDataSource() {
        super(false);
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public long open(DataSpec dataSpec) throws IOException {
        transferInitializing(dataSpec);
        this.dataSpec = dataSpec;
        this.readPosition = (int) dataSpec.position;
        Uri uri = dataSpec.uri;
        String scheme = uri.getScheme();
        if (!"data".equals(scheme)) {
            throw new ParserException("Unsupported scheme: " + scheme);
        }
        String[] uriParts = Util.split(uri.getSchemeSpecificPart(), ",");
        if (uriParts.length != 2) {
            throw new ParserException("Unexpected URI format: " + uri);
        }
        String dataString = uriParts[1];
        if (uriParts[0].contains(";base64")) {
            try {
                this.data = Base64.decode(dataString, 0);
            } catch (IllegalArgumentException e) {
                throw new ParserException("Error while parsing Base64 encoded string: " + dataString, e);
            }
        } else {
            this.data = Util.getUtf8Bytes(URLDecoder.decode(dataString, C.ASCII_NAME));
        }
        int length = dataSpec.length != -1 ? ((int) dataSpec.length) + this.readPosition : this.data.length;
        this.endPosition = length;
        if (length > this.data.length || this.readPosition > length) {
            this.data = null;
            throw new DataSourceException(0);
        }
        transferStarted(dataSpec);
        return this.endPosition - this.readPosition;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public int read(byte[] buffer, int offset, int readLength) {
        if (readLength == 0) {
            return 0;
        }
        int remainingBytes = this.endPosition - this.readPosition;
        if (remainingBytes == 0) {
            return -1;
        }
        int readLength2 = Math.min(readLength, remainingBytes);
        System.arraycopy(Util.castNonNull(this.data), this.readPosition, buffer, offset, readLength2);
        this.readPosition += readLength2;
        bytesTransferred(readLength2);
        return readLength2;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public Uri getUri() {
        DataSpec dataSpec = this.dataSpec;
        if (dataSpec != null) {
            return dataSpec.uri;
        }
        return null;
    }

    @Override // com.google.android.exoplayer2.upstream.DataSource
    public void close() {
        if (this.data != null) {
            this.data = null;
            transferEnded();
        }
        this.dataSpec = null;
    }
}
