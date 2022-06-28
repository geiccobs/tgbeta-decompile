package com.microsoft.appcenter.ingestion;

import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.http.DefaultHttpClient;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.http.HttpUtils;
import com.microsoft.appcenter.http.ServiceCall;
import com.microsoft.appcenter.http.ServiceCallback;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.LogContainer;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.TicketCache;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class OneCollectorIngestion implements Ingestion {
    static final String API_KEY = "apikey";
    private static final String CLIENT_VERSION_FORMAT = "ACS-Android-Java-no-%s-no";
    static final String CLIENT_VERSION_KEY = "Client-Version";
    private static final String CONTENT_TYPE_VALUE = "application/x-json-stream; charset=utf-8";
    private static final String DEFAULT_LOG_URL = "https://mobile.events.data.microsoft.com/OneCollector/1.0";
    static final String STRICT = "Strict";
    static final String TICKETS = "Tickets";
    static final String UPLOAD_TIME_KEY = "Upload-Time";
    private final HttpClient mHttpClient;
    private final LogSerializer mLogSerializer;
    private String mLogUrl = DEFAULT_LOG_URL;

    public OneCollectorIngestion(HttpClient httpClient, LogSerializer logSerializer) {
        this.mLogSerializer = logSerializer;
        this.mHttpClient = httpClient;
    }

    @Override // com.microsoft.appcenter.ingestion.Ingestion
    public ServiceCall sendAsync(String appSecret, UUID installId, LogContainer logContainer, ServiceCallback serviceCallback) throws IllegalArgumentException {
        Map<String, String> headers = new HashMap<>();
        Set<String> apiKeys = new LinkedHashSet<>();
        for (Log log : logContainer.getLogs()) {
            apiKeys.addAll(log.getTransmissionTargetTokens());
        }
        StringBuilder apiKey = new StringBuilder();
        for (String targetToken : apiKeys) {
            apiKey.append(targetToken);
            apiKey.append(",");
        }
        if (!apiKeys.isEmpty()) {
            apiKey.deleteCharAt(apiKey.length() - 1);
        }
        headers.put(API_KEY, apiKey.toString());
        JSONObject tickets = new JSONObject();
        for (Log log2 : logContainer.getLogs()) {
            List<String> ticketKeys = ((CommonSchemaLog) log2).getExt().getProtocol().getTicketKeys();
            if (ticketKeys != null) {
                for (String ticketKey : ticketKeys) {
                    String token = TicketCache.getTicket(ticketKey);
                    if (token != null) {
                        try {
                            tickets.put(ticketKey, token);
                        } catch (JSONException e) {
                            AppCenterLog.error("AppCenter", "Cannot serialize tickets, sending log anonymously", e);
                        }
                    }
                }
            }
        }
        if (tickets.length() > 0) {
            headers.put(TICKETS, tickets.toString());
            if (Constants.APPLICATION_DEBUGGABLE) {
                headers.put(STRICT, Boolean.TRUE.toString());
            }
        }
        headers.put(DefaultHttpClient.CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
        headers.put(CLIENT_VERSION_KEY, String.format(CLIENT_VERSION_FORMAT, "3.3.1"));
        headers.put(UPLOAD_TIME_KEY, String.valueOf(System.currentTimeMillis()));
        HttpClient.CallTemplate callTemplate = new IngestionCallTemplate(this.mLogSerializer, logContainer);
        return this.mHttpClient.callAsync(this.mLogUrl, DefaultHttpClient.METHOD_POST, headers, callTemplate, serviceCallback);
    }

    @Override // com.microsoft.appcenter.ingestion.Ingestion
    public void setLogUrl(String logUrl) {
        this.mLogUrl = logUrl;
    }

    @Override // com.microsoft.appcenter.ingestion.Ingestion
    public void reopen() {
        this.mHttpClient.reopen();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.mHttpClient.close();
    }

    /* loaded from: classes3.dex */
    private static class IngestionCallTemplate implements HttpClient.CallTemplate {
        private final LogContainer mLogContainer;
        private final LogSerializer mLogSerializer;

        IngestionCallTemplate(LogSerializer logSerializer, LogContainer logContainer) {
            this.mLogSerializer = logSerializer;
            this.mLogContainer = logContainer;
        }

        @Override // com.microsoft.appcenter.http.HttpClient.CallTemplate
        public String buildRequestBody() throws JSONException {
            StringBuilder jsonStream = new StringBuilder();
            for (Log log : this.mLogContainer.getLogs()) {
                jsonStream.append(this.mLogSerializer.serializeLog(log));
                jsonStream.append('\n');
            }
            return jsonStream.toString();
        }

        @Override // com.microsoft.appcenter.http.HttpClient.CallTemplate
        public void onBeforeCalling(URL url, Map<String, String> headers) {
            if (AppCenterLog.getLogLevel() <= 2) {
                AppCenterLog.verbose("AppCenter", "Calling " + url + "...");
                Map<String, String> logHeaders = new HashMap<>(headers);
                String apiKeys = logHeaders.get(OneCollectorIngestion.API_KEY);
                if (apiKeys != null) {
                    logHeaders.put(OneCollectorIngestion.API_KEY, HttpUtils.hideApiKeys(apiKeys));
                }
                String tickets = logHeaders.get(OneCollectorIngestion.TICKETS);
                if (tickets != null) {
                    logHeaders.put(OneCollectorIngestion.TICKETS, HttpUtils.hideTickets(tickets));
                }
                AppCenterLog.verbose("AppCenter", "Headers: " + logHeaders);
            }
        }
    }
}
