package com.microsoft.appcenter.ingestion;

import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.http.AbstractAppCallTemplate;
import com.microsoft.appcenter.http.DefaultHttpClient;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.http.ServiceCall;
import com.microsoft.appcenter.http.ServiceCallback;
import com.microsoft.appcenter.ingestion.models.LogContainer;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
/* loaded from: classes3.dex */
public class AppCenterIngestion implements Ingestion {
    static final String API_PATH = "/logs?api-version=1.0.0";
    public static final String DEFAULT_LOG_URL = "https://in.appcenter.ms";
    static final String INSTALL_ID = "Install-ID";
    private final HttpClient mHttpClient;
    private final LogSerializer mLogSerializer;
    private String mLogUrl = DEFAULT_LOG_URL;

    public AppCenterIngestion(HttpClient httpClient, LogSerializer logSerializer) {
        this.mLogSerializer = logSerializer;
        this.mHttpClient = httpClient;
    }

    @Override // com.microsoft.appcenter.ingestion.Ingestion
    public void setLogUrl(String logUrl) {
        this.mLogUrl = logUrl;
    }

    @Override // com.microsoft.appcenter.ingestion.Ingestion
    public ServiceCall sendAsync(String appSecret, UUID installId, LogContainer logContainer, ServiceCallback serviceCallback) throws IllegalArgumentException {
        Map<String, String> headers = new HashMap<>();
        headers.put(INSTALL_ID, installId.toString());
        headers.put(Constants.APP_SECRET, appSecret);
        HttpClient.CallTemplate callTemplate = new IngestionCallTemplate(this.mLogSerializer, logContainer);
        HttpClient httpClient = this.mHttpClient;
        return httpClient.callAsync(this.mLogUrl + API_PATH, DefaultHttpClient.METHOD_POST, headers, callTemplate, serviceCallback);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.mHttpClient.close();
    }

    @Override // com.microsoft.appcenter.ingestion.Ingestion
    public void reopen() {
        this.mHttpClient.reopen();
    }

    /* loaded from: classes3.dex */
    private static class IngestionCallTemplate extends AbstractAppCallTemplate {
        private final LogContainer mLogContainer;
        private final LogSerializer mLogSerializer;

        IngestionCallTemplate(LogSerializer logSerializer, LogContainer logContainer) {
            this.mLogSerializer = logSerializer;
            this.mLogContainer = logContainer;
        }

        @Override // com.microsoft.appcenter.http.HttpClient.CallTemplate
        public String buildRequestBody() throws JSONException {
            return this.mLogSerializer.serializeContainer(this.mLogContainer);
        }
    }
}
