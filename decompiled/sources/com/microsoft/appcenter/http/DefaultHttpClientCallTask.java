package com.microsoft.appcenter.http;

import android.net.TrafficStats;
import android.os.AsyncTask;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class DefaultHttpClientCallTask extends AsyncTask<Void, Void, Object> {
    private static final int DEFAULT_STRING_BUILDER_CAPACITY = 16;
    private static final int MAX_PRETTIFY_LOG_LENGTH = 4096;
    private static final int MIN_GZIP_LENGTH = 1400;
    private final HttpClient.CallTemplate mCallTemplate;
    private final boolean mCompressionEnabled;
    private final Map<String, String> mHeaders;
    private final String mMethod;
    private final ServiceCallback mServiceCallback;
    private final Tracker mTracker;
    private final String mUrl;
    private static final Pattern TOKEN_REGEX_URL_ENCODED = Pattern.compile("token=[^&]+");
    private static final Pattern TOKEN_REGEX_JSON = Pattern.compile("token\":\"[^\"]+\"");
    private static final Pattern REDIRECT_URI_REGEX_JSON = Pattern.compile("redirect_uri\":\"[^\"]+\"");

    /* loaded from: classes3.dex */
    public interface Tracker {
        void onFinish(DefaultHttpClientCallTask defaultHttpClientCallTask);

        void onStart(DefaultHttpClientCallTask defaultHttpClientCallTask);
    }

    public DefaultHttpClientCallTask(String url, String method, Map<String, String> headers, HttpClient.CallTemplate callTemplate, ServiceCallback serviceCallback, Tracker tracker, boolean compressionEnabled) {
        this.mUrl = url;
        this.mMethod = method;
        this.mHeaders = headers;
        this.mCallTemplate = callTemplate;
        this.mServiceCallback = serviceCallback;
        this.mTracker = tracker;
        this.mCompressionEnabled = compressionEnabled;
    }

    private static InputStream getInputStream(HttpsURLConnection httpsURLConnection) throws IOException {
        int status = httpsURLConnection.getResponseCode();
        if (status >= 200 && status < 400) {
            return httpsURLConnection.getInputStream();
        }
        return httpsURLConnection.getErrorStream();
    }

    private void writePayload(OutputStream out, byte[] payload) throws IOException {
        for (int i = 0; i < payload.length; i += 1024) {
            out.write(payload, i, Math.min(payload.length - i, 1024));
            if (isCancelled()) {
                return;
            }
        }
    }

    private String readResponse(HttpsURLConnection httpsURLConnection) throws IOException {
        StringBuilder builder = new StringBuilder(Math.max(httpsURLConnection.getContentLength(), 16));
        InputStream stream = getInputStream(httpsURLConnection);
        try {
            Reader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[1024];
            do {
                int len = reader.read(buffer);
                if (len <= 0) {
                    break;
                }
                builder.append(buffer, 0, len);
            } while (!isCancelled());
            return builder.toString();
        } finally {
            stream.close();
        }
    }

    private HttpResponse doHttpCall() throws Exception {
        String logPayload;
        HttpClient.CallTemplate callTemplate;
        URL url = new URL(this.mUrl);
        HttpsURLConnection httpsURLConnection = HttpUtils.createHttpsConnection(url);
        try {
            httpsURLConnection.setRequestMethod(this.mMethod);
            String payload = null;
            byte[] binaryPayload = null;
            boolean shouldCompress = false;
            boolean isPost = this.mMethod.equals(DefaultHttpClient.METHOD_POST);
            if (isPost && (callTemplate = this.mCallTemplate) != null) {
                payload = callTemplate.buildRequestBody();
                binaryPayload = payload.getBytes("UTF-8");
                shouldCompress = this.mCompressionEnabled && binaryPayload.length >= MIN_GZIP_LENGTH;
                if (!this.mHeaders.containsKey(DefaultHttpClient.CONTENT_TYPE_KEY)) {
                    this.mHeaders.put(DefaultHttpClient.CONTENT_TYPE_KEY, "application/json");
                }
            }
            if (shouldCompress) {
                this.mHeaders.put("Content-Encoding", "gzip");
            }
            for (Map.Entry<String, String> header : this.mHeaders.entrySet()) {
                httpsURLConnection.setRequestProperty(header.getKey(), header.getValue());
            }
            if (isCancelled()) {
                return null;
            }
            HttpClient.CallTemplate callTemplate2 = this.mCallTemplate;
            if (callTemplate2 != null) {
                callTemplate2.onBeforeCalling(url, this.mHeaders);
            }
            if (binaryPayload != null) {
                if (AppCenterLog.getLogLevel() <= 2) {
                    if (payload.length() < 4096) {
                        payload = TOKEN_REGEX_URL_ENCODED.matcher(payload).replaceAll("token=***");
                        if ("application/json".equals(this.mHeaders.get(DefaultHttpClient.CONTENT_TYPE_KEY))) {
                            payload = new JSONObject(payload).toString(2);
                        }
                    }
                    AppCenterLog.verbose("AppCenter", payload);
                }
                if (shouldCompress) {
                    ByteArrayOutputStream gzipBuffer = new ByteArrayOutputStream(binaryPayload.length);
                    GZIPOutputStream gzipStream = new GZIPOutputStream(gzipBuffer);
                    gzipStream.write(binaryPayload);
                    gzipStream.close();
                    binaryPayload = gzipBuffer.toByteArray();
                }
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setFixedLengthStreamingMode(binaryPayload.length);
                OutputStream out = httpsURLConnection.getOutputStream();
                writePayload(out, binaryPayload);
                out.close();
            }
            if (isCancelled()) {
                return null;
            }
            int status = httpsURLConnection.getResponseCode();
            String response = readResponse(httpsURLConnection);
            if (AppCenterLog.getLogLevel() <= 2) {
                String contentType = httpsURLConnection.getHeaderField(DefaultHttpClient.CONTENT_TYPE_KEY);
                if (contentType != null && !contentType.startsWith("text/") && !contentType.startsWith("application/")) {
                    logPayload = "<binary>";
                    AppCenterLog.verbose("AppCenter", "HTTP response status=" + status + " payload=" + logPayload);
                }
                String logPayload2 = TOKEN_REGEX_JSON.matcher(response).replaceAll("token\":\"***\"");
                logPayload = REDIRECT_URI_REGEX_JSON.matcher(logPayload2).replaceAll("redirect_uri\":\"***\"");
                AppCenterLog.verbose("AppCenter", "HTTP response status=" + status + " payload=" + logPayload);
            }
            HashMap hashMap = new HashMap();
            for (Map.Entry<String, List<String>> header2 : httpsURLConnection.getHeaderFields().entrySet()) {
                hashMap.put(header2.getKey(), header2.getValue().iterator().next());
            }
            HttpResponse httpResponse = new HttpResponse(status, response, hashMap);
            if (status >= 200 && status < 300) {
                return httpResponse;
            }
            throw new HttpException(httpResponse);
        } finally {
            httpsURLConnection.disconnect();
        }
    }

    public Object doInBackground(Void... params) {
        TrafficStats.setThreadStatsTag(HttpUtils.THREAD_STATS_TAG);
        try {
            return doHttpCall();
        } catch (Exception e) {
            return e;
        } finally {
            TrafficStats.clearThreadStatsTag();
        }
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        this.mTracker.onStart(this);
    }

    @Override // android.os.AsyncTask
    protected void onPostExecute(Object result) {
        this.mTracker.onFinish(this);
        if (result instanceof Exception) {
            this.mServiceCallback.onCallFailed((Exception) result);
            return;
        }
        HttpResponse response = (HttpResponse) result;
        this.mServiceCallback.onCallSucceeded(response);
    }

    @Override // android.os.AsyncTask
    protected void onCancelled(Object result) {
        if ((result instanceof HttpResponse) || (result instanceof HttpException)) {
            onPostExecute(result);
        } else {
            this.mTracker.onFinish(this);
        }
    }
}
