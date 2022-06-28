package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.util.UriUtil;
/* loaded from: classes3.dex */
public final class RangedUri {
    private int hashCode;
    public final long length;
    private final String referenceUri;
    public final long start;

    public RangedUri(String referenceUri, long start, long length) {
        this.referenceUri = referenceUri == null ? "" : referenceUri;
        this.start = start;
        this.length = length;
    }

    public Uri resolveUri(String baseUri) {
        return UriUtil.resolveToUri(baseUri, this.referenceUri);
    }

    public String resolveUriString(String baseUri) {
        return UriUtil.resolve(baseUri, this.referenceUri);
    }

    public RangedUri attemptMerge(RangedUri other, String baseUri) {
        String resolvedUri = resolveUriString(baseUri);
        if (other == null || !resolvedUri.equals(other.resolveUriString(baseUri))) {
            return null;
        }
        long j = this.length;
        if (j != -1) {
            long j2 = this.start;
            if (j2 + j == other.start) {
                long j3 = other.length;
                return new RangedUri(resolvedUri, j2, j3 == -1 ? -1L : j + j3);
            }
        }
        long j4 = other.length;
        if (j4 != -1) {
            long j5 = other.start;
            if (j5 + j4 == this.start) {
                return new RangedUri(resolvedUri, j5, j == -1 ? -1L : j4 + j);
            }
            return null;
        }
        return null;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = (17 * 31) + ((int) this.start);
            this.hashCode = (((result * 31) + ((int) this.length)) * 31) + this.referenceUri.hashCode();
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RangedUri other = (RangedUri) obj;
        return this.start == other.start && this.length == other.length && this.referenceUri.equals(other.referenceUri);
    }

    public String toString() {
        return "RangedUri(referenceUri=" + this.referenceUri + ", start=" + this.start + ", length=" + this.length + ")";
    }
}
