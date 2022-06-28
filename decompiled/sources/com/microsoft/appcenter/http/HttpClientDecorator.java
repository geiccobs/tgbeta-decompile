package com.microsoft.appcenter.http;

import java.io.IOException;
/* loaded from: classes3.dex */
public abstract class HttpClientDecorator implements HttpClient {
    final HttpClient mDecoratedApi;

    public HttpClientDecorator(HttpClient decoratedApi) {
        this.mDecoratedApi = decoratedApi;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.mDecoratedApi.close();
    }

    @Override // com.microsoft.appcenter.http.HttpClient
    public void reopen() {
        this.mDecoratedApi.reopen();
    }

    HttpClient getDecoratedApi() {
        return this.mDecoratedApi;
    }
}
