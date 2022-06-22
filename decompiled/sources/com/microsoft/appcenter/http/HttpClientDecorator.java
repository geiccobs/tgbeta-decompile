package com.microsoft.appcenter.http;

import java.io.IOException;
/* loaded from: classes.dex */
public abstract class HttpClientDecorator implements HttpClient {
    final HttpClient mDecoratedApi;

    public HttpClientDecorator(HttpClient httpClient) {
        this.mDecoratedApi = httpClient;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.mDecoratedApi.close();
    }

    @Override // com.microsoft.appcenter.http.HttpClient
    public void reopen() {
        this.mDecoratedApi.reopen();
    }
}
