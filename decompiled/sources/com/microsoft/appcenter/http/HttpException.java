package com.microsoft.appcenter.http;

import android.text.TextUtils;
import java.io.IOException;
/* loaded from: classes3.dex */
public class HttpException extends IOException {
    private final HttpResponse mHttpResponse;

    public HttpException(HttpResponse httpResponse) {
        super(getDetailMessage(httpResponse.getStatusCode(), httpResponse.getPayload()));
        this.mHttpResponse = httpResponse;
    }

    private static String getDetailMessage(int status, String payload) {
        if (TextUtils.isEmpty(payload)) {
            return String.valueOf(status);
        }
        return status + " - " + payload;
    }

    public HttpResponse getHttpResponse() {
        return this.mHttpResponse;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpException that = (HttpException) o;
        return this.mHttpResponse.equals(that.mHttpResponse);
    }

    public int hashCode() {
        return this.mHttpResponse.hashCode();
    }
}
