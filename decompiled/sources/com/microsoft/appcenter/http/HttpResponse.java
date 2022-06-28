package com.microsoft.appcenter.http;

import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public class HttpResponse {
    private final Map<String, String> headers;
    private final String payload;
    private final int statusCode;

    public HttpResponse(int status) {
        this(status, "");
    }

    public HttpResponse(int status, String payload) {
        this(status, payload, new HashMap());
    }

    public HttpResponse(int status, String payload, Map<String, String> headers) {
        this.payload = payload;
        this.statusCode = status;
        this.headers = headers;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getPayload() {
        return this.payload;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpResponse that = (HttpResponse) o;
        return this.statusCode == that.statusCode && this.payload.equals(that.payload) && this.headers.equals(that.headers);
    }

    public int hashCode() {
        int result = this.statusCode;
        return (((result * 31) + this.payload.hashCode()) * 31) + this.headers.hashCode();
    }
}
