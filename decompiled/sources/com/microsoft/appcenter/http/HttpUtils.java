package com.microsoft.appcenter.http;

import android.content.Context;
import android.os.Build;
import com.microsoft.appcenter.utils.NetworkStateHelper;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
/* loaded from: classes3.dex */
public class HttpUtils {
    public static final int CONNECT_TIMEOUT = 10000;
    static final int MAX_CHARACTERS_DISPLAYED_FOR_SECRET = 8;
    public static final int READ_BUFFER_SIZE = 1024;
    public static final int READ_TIMEOUT = 10000;
    public static final int THREAD_STATS_TAG = -667034599;
    public static final int WRITE_BUFFER_SIZE = 1024;
    private static final Class[] RECOVERABLE_EXCEPTIONS = {EOFException.class, InterruptedIOException.class, SocketException.class, UnknownHostException.class, RejectedExecutionException.class};
    private static final Pattern CONNECTION_ISSUE_PATTERN = Pattern.compile("connection (time|reset|abort)|failure in ssl library, usually a protocol error|anchor for certification path not found");
    private static final Pattern TOKEN_VALUE_PATTERN = Pattern.compile(":[^\"]+");
    private static final Pattern API_KEY_PATTERN = Pattern.compile("-[^,]+(,|$)");

    HttpUtils() {
    }

    public static boolean isRecoverableError(Throwable t) {
        Class<?>[] clsArr;
        String message;
        Class<?>[] clsArr2;
        if (t instanceof HttpException) {
            HttpException exception = (HttpException) t;
            int code = exception.getHttpResponse().getStatusCode();
            return code >= 500 || code == 408 || code == 429;
        }
        for (Class<?> type : RECOVERABLE_EXCEPTIONS) {
            if (type.isAssignableFrom(t.getClass())) {
                return true;
            }
        }
        Throwable cause = t.getCause();
        if (cause != null) {
            for (Class<?> type2 : RECOVERABLE_EXCEPTIONS) {
                if (type2.isAssignableFrom(cause.getClass())) {
                    return true;
                }
            }
        }
        return (t instanceof SSLException) && (message = t.getMessage()) != null && CONNECTION_ISSUE_PATTERN.matcher(message.toLowerCase(Locale.US)).find();
    }

    public static String hideSecret(String secret) {
        int length = secret.length();
        int i = 8;
        if (secret.length() < 8) {
            i = 0;
        }
        int hidingEndIndex = length - i;
        char[] fill = new char[hidingEndIndex];
        Arrays.fill(fill, '*');
        return new String(fill) + secret.substring(hidingEndIndex);
    }

    public static String hideApiKeys(String apiKeys) {
        StringBuilder buffer = new StringBuilder();
        Matcher matcher = API_KEY_PATTERN.matcher(apiKeys);
        int lastEnd = 0;
        while (matcher.find()) {
            buffer.append(apiKeys.substring(lastEnd, matcher.start()));
            buffer.append("-***");
            buffer.append(matcher.group(1));
            lastEnd = matcher.end();
        }
        if (lastEnd < apiKeys.length()) {
            buffer.append(apiKeys.substring(lastEnd));
        }
        return buffer.toString();
    }

    public static String hideTickets(String tickets) {
        return TOKEN_VALUE_PATTERN.matcher(tickets).replaceAll(":***");
    }

    public static HttpClient createHttpClient(Context context) {
        return createHttpClient(context, true);
    }

    public static HttpClient createHttpClient(Context context, boolean compressionEnabled) {
        return new HttpClientRetryer(createHttpClientWithoutRetryer(context, compressionEnabled));
    }

    public static HttpClient createHttpClientWithoutRetryer(Context context, boolean compressionEnabled) {
        HttpClient httpClient = new DefaultHttpClient(compressionEnabled);
        NetworkStateHelper networkStateHelper = NetworkStateHelper.getSharedInstance(context);
        return new HttpClientNetworkStateHandler(httpClient, networkStateHelper);
    }

    public static HttpsURLConnection createHttpsConnection(URL url) throws IOException {
        if (!"https".equals(url.getProtocol())) {
            throw new IOException("App Center support only HTTPS connection.");
        }
        URLConnection urlConnection = url.openConnection();
        if (urlConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
            if (Build.VERSION.SDK_INT <= 21) {
                httpsURLConnection.setSSLSocketFactory(new TLS1_2SocketFactory());
            }
            httpsURLConnection.setConnectTimeout(10000);
            httpsURLConnection.setReadTimeout(10000);
            return httpsURLConnection;
        }
        throw new IOException("App Center supports only HTTPS connection.");
    }
}
