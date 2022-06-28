package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import com.microsoft.appcenter.http.DefaultHttpClient;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public final class DataSpec {
    public static final int FLAG_ALLOW_CACHE_FRAGMENTATION = 4;
    public static final int FLAG_ALLOW_GZIP = 1;
    public static final int FLAG_DONT_CACHE_IF_LENGTH_UNKNOWN = 2;
    public static final int HTTP_METHOD_GET = 1;
    public static final int HTTP_METHOD_HEAD = 3;
    public static final int HTTP_METHOD_POST = 2;
    public final long absoluteStreamPosition;
    public final int flags;
    public final byte[] httpBody;
    public final int httpMethod;
    public final Map<String, String> httpRequestHeaders;
    public final String key;
    public final long length;
    public final long position;
    public final Uri uri;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface HttpMethod {
    }

    public DataSpec(Uri uri) {
        this(uri, 0);
    }

    public DataSpec(Uri uri, int flags) {
        this(uri, 0L, -1L, null, flags);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long length, String key) {
        this(uri, absoluteStreamPosition, absoluteStreamPosition, length, key, 0);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long length, String key, int flags) {
        this(uri, absoluteStreamPosition, absoluteStreamPosition, length, key, flags);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long length, String key, int flags, Map<String, String> httpRequestHeaders) {
        this(uri, inferHttpMethod(null), null, absoluteStreamPosition, absoluteStreamPosition, length, key, flags, httpRequestHeaders);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long position, long length, String key, int flags) {
        this(uri, null, absoluteStreamPosition, position, length, key, flags);
    }

    public DataSpec(Uri uri, byte[] postBody, long absoluteStreamPosition, long position, long length, String key, int flags) {
        this(uri, inferHttpMethod(postBody), postBody, absoluteStreamPosition, position, length, key, flags);
    }

    public DataSpec(Uri uri, int httpMethod, byte[] httpBody, long absoluteStreamPosition, long position, long length, String key, int flags) {
        this(uri, httpMethod, httpBody, absoluteStreamPosition, position, length, key, flags, Collections.emptyMap());
    }

    public DataSpec(Uri uri, int httpMethod, byte[] httpBody, long absoluteStreamPosition, long position, long length, String key, int flags, Map<String, String> httpRequestHeaders) {
        boolean z = true;
        Assertions.checkArgument(absoluteStreamPosition >= 0);
        Assertions.checkArgument(position >= 0);
        if (length <= 0 && length != -1) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.httpBody = (httpBody == null || httpBody.length == 0) ? null : httpBody;
        this.absoluteStreamPosition = absoluteStreamPosition;
        this.position = position;
        this.length = length;
        this.key = key;
        this.flags = flags;
        this.httpRequestHeaders = Collections.unmodifiableMap(new HashMap(httpRequestHeaders));
    }

    public boolean isFlagSet(int flag) {
        return (this.flags & flag) == flag;
    }

    public String toString() {
        return "DataSpec[" + getHttpMethodString() + " " + this.uri + ", " + Arrays.toString(this.httpBody) + ", " + this.absoluteStreamPosition + ", " + this.position + ", " + this.length + ", " + this.key + ", " + this.flags + "]";
    }

    public final String getHttpMethodString() {
        return getStringForHttpMethod(this.httpMethod);
    }

    public static String getStringForHttpMethod(int httpMethod) {
        switch (httpMethod) {
            case 1:
                return DefaultHttpClient.METHOD_GET;
            case 2:
                return DefaultHttpClient.METHOD_POST;
            case 3:
                return "HEAD";
            default:
                throw new AssertionError(httpMethod);
        }
    }

    public DataSpec subrange(long offset) {
        long j = this.length;
        long j2 = -1;
        if (j != -1) {
            j2 = j - offset;
        }
        return subrange(offset, j2);
    }

    public DataSpec subrange(long offset, long length) {
        if (offset == 0 && this.length == length) {
            return this;
        }
        return new DataSpec(this.uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition + offset, this.position + offset, length, this.key, this.flags, this.httpRequestHeaders);
    }

    public DataSpec withUri(Uri uri) {
        return new DataSpec(uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition, this.position, this.length, this.key, this.flags, this.httpRequestHeaders);
    }

    public DataSpec withRequestHeaders(Map<String, String> requestHeaders) {
        return new DataSpec(this.uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition, this.position, this.length, this.key, this.flags, requestHeaders);
    }

    public DataSpec withAdditionalHeaders(Map<String, String> requestHeaders) {
        Map<String, String> totalHeaders = new HashMap<>(this.httpRequestHeaders);
        totalHeaders.putAll(requestHeaders);
        return new DataSpec(this.uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition, this.position, this.length, this.key, this.flags, totalHeaders);
    }

    private static int inferHttpMethod(byte[] postBody) {
        return postBody != null ? 2 : 1;
    }
}
