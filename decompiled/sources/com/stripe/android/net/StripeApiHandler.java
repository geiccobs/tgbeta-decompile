package com.stripe.android.net;

import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.CardException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.exception.PermissionException;
import com.stripe.android.exception.RateLimitException;
import com.stripe.android.model.Token;
import com.stripe.android.net.ErrorParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class StripeApiHandler {
    private static final SSLSocketFactory SSL_SOCKET_FACTORY = new StripeSSLSocketFactory();

    public static Token createToken(Map<String, Object> map, RequestOptions requestOptions) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        return requestToken("POST", getApiUrl(), map, requestOptions);
    }

    static String createQuery(Map<String, Object> map) throws UnsupportedEncodingException, InvalidRequestException {
        StringBuilder sb = new StringBuilder();
        for (Parameter parameter : flattenParams(map)) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(urlEncodePair(parameter.key, parameter.value));
        }
        return sb.toString();
    }

    static Map<String, String> getHeaders(RequestOptions requestOptions) {
        HashMap hashMap = new HashMap();
        String apiVersion = requestOptions.getApiVersion();
        hashMap.put("Accept-Charset", "UTF-8");
        hashMap.put("Accept", "application/json");
        hashMap.put("User-Agent", String.format("Stripe/v1 JavaBindings/%s", "3.5.0"));
        hashMap.put("Authorization", String.format("Bearer %s", requestOptions.getPublishableApiKey()));
        String[] strArr = {"os.name", "os.version", "os.arch", "java.version", "java.vendor", "java.vm.version", "java.vm.vendor"};
        HashMap hashMap2 = new HashMap();
        for (int i = 0; i < 7; i++) {
            String str = strArr[i];
            hashMap2.put(str, System.getProperty(str));
        }
        hashMap2.put("bindings.version", "3.5.0");
        hashMap2.put("lang", "Java");
        hashMap2.put("publisher", "Stripe");
        hashMap.put("X-Stripe-Client-User-Agent", new JSONObject(hashMap2).toString());
        if (apiVersion != null) {
            hashMap.put("Stripe-Version", apiVersion);
        }
        if (requestOptions.getIdempotencyKey() != null) {
            hashMap.put("Idempotency-Key", requestOptions.getIdempotencyKey());
        }
        return hashMap;
    }

    static String getApiUrl() {
        return String.format("%s/v1/%s", "https://api.stripe.com", "tokens");
    }

    private static String formatURL(String str, String str2) {
        if (str2 == null || str2.isEmpty()) {
            return str;
        }
        String str3 = "?";
        if (str.contains(str3)) {
            str3 = "&";
        }
        return String.format("%s%s%s", str, str3, str2);
    }

    private static HttpURLConnection createGetConnection(String str, String str2, RequestOptions requestOptions) throws IOException {
        HttpURLConnection createStripeConnection = createStripeConnection(formatURL(str, str2), requestOptions);
        createStripeConnection.setRequestMethod("GET");
        return createStripeConnection;
    }

    private static HttpURLConnection createPostConnection(String str, String str2, RequestOptions requestOptions) throws IOException {
        OutputStream outputStream;
        Throwable th;
        HttpURLConnection createStripeConnection = createStripeConnection(str, requestOptions);
        createStripeConnection.setDoOutput(true);
        createStripeConnection.setRequestMethod("POST");
        createStripeConnection.setRequestProperty("Content-Type", String.format("application/x-www-form-urlencoded;charset=%s", "UTF-8"));
        try {
            outputStream = createStripeConnection.getOutputStream();
        } catch (Throwable th2) {
            th = th2;
            outputStream = null;
        }
        try {
            outputStream.write(str2.getBytes("UTF-8"));
            outputStream.close();
            return createStripeConnection;
        } catch (Throwable th3) {
            th = th3;
            if (outputStream != null) {
                outputStream.close();
            }
            throw th;
        }
    }

    private static HttpURLConnection createStripeConnection(String str, RequestOptions requestOptions) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(30000);
        httpURLConnection.setReadTimeout(80000);
        httpURLConnection.setUseCaches(false);
        for (Map.Entry<String, String> entry : getHeaders(requestOptions).entrySet()) {
            httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        if (httpURLConnection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(SSL_SOCKET_FACTORY);
        }
        return httpURLConnection;
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x0093  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0026 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.stripe.android.model.Token requestToken(java.lang.String r7, java.lang.String r8, java.util.Map<java.lang.String, java.lang.Object> r9, com.stripe.android.net.RequestOptions r10) throws com.stripe.android.exception.AuthenticationException, com.stripe.android.exception.InvalidRequestException, com.stripe.android.exception.APIConnectionException, com.stripe.android.exception.CardException, com.stripe.android.exception.APIException {
        /*
            java.lang.String r0 = "-1"
            java.lang.String r1 = "networkaddress.cache.ttl"
            r2 = 0
            if (r10 != 0) goto L8
            return r2
        L8:
            java.lang.Boolean r3 = java.lang.Boolean.TRUE
            java.lang.String r4 = java.security.Security.getProperty(r1)     // Catch: java.lang.SecurityException -> L14
            java.lang.String r5 = "0"
            java.security.Security.setProperty(r1, r5)     // Catch: java.lang.SecurityException -> L15
            goto L17
        L14:
            r4 = r2
        L15:
            java.lang.Boolean r3 = java.lang.Boolean.FALSE
        L17:
            java.lang.String r5 = r10.getPublishableApiKey()
            java.lang.String r5 = r5.trim()
            boolean r5 = r5.isEmpty()
            r6 = 0
            if (r5 != 0) goto L93
            com.stripe.android.net.StripeResponse r7 = getStripeResponse(r7, r8, r9, r10)     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            int r8 = r7.getResponseCode()     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            java.lang.String r9 = r7.getResponseBody()     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            java.util.Map r7 = r7.getResponseHeaders()     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            if (r7 != 0) goto L3a
            r7 = r2
            goto L42
        L3a:
            java.lang.String r10 = "Request-Id"
            java.lang.Object r7 = r7.get(r10)     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            java.util.List r7 = (java.util.List) r7     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
        L42:
            if (r7 == 0) goto L51
            int r10 = r7.size()     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            if (r10 <= 0) goto L51
            java.lang.Object r7 = r7.get(r6)     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            java.lang.String r7 = (java.lang.String) r7     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            goto L52
        L51:
            r7 = r2
        L52:
            r10 = 200(0xc8, float:2.8E-43)
            if (r8 < r10) goto L5a
            r10 = 300(0x12c, float:4.2E-43)
            if (r8 < r10) goto L5d
        L5a:
            handleAPIError(r9, r8, r7)     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
        L5d:
            com.stripe.android.model.Token r7 = com.stripe.android.net.TokenParser.parseToken(r9)     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L82
            boolean r8 = r3.booleanValue()
            if (r8 == 0) goto L70
            if (r4 != 0) goto L6d
            java.security.Security.setProperty(r1, r0)
            goto L70
        L6d:
            java.security.Security.setProperty(r1, r4)
        L70:
            return r7
        L71:
            r7 = move-exception
            boolean r8 = r3.booleanValue()
            if (r8 == 0) goto L81
            if (r4 != 0) goto L7e
            java.security.Security.setProperty(r1, r0)
            goto L81
        L7e:
            java.security.Security.setProperty(r1, r4)
        L81:
            throw r7
        L82:
            boolean r7 = r3.booleanValue()
            if (r7 == 0) goto L92
            if (r4 != 0) goto L8f
            java.security.Security.setProperty(r1, r0)
            goto L92
        L8f:
            java.security.Security.setProperty(r1, r4)
        L92:
            return r2
        L93:
            com.stripe.android.exception.AuthenticationException r7 = new com.stripe.android.exception.AuthenticationException
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
            java.lang.String r9 = "No API key provided. (HINT: set your API key using 'Stripe.apiKey = <API-KEY>'. You can generate API keys from the Stripe web interface. See https://stripe.com/api for details or email support@stripe.com if you have questions."
            r7.<init>(r9, r2, r8)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.stripe.android.net.StripeApiHandler.requestToken(java.lang.String, java.lang.String, java.util.Map, com.stripe.android.net.RequestOptions):com.stripe.android.model.Token");
    }

    private static StripeResponse getStripeResponse(String str, String str2, Map<String, Object> map, RequestOptions requestOptions) throws InvalidRequestException, APIConnectionException, APIException {
        try {
            return makeURLConnectionRequest(str, str2, createQuery(map), requestOptions);
        } catch (UnsupportedEncodingException e) {
            throw new InvalidRequestException("Unable to encode parameters to UTF-8. Please contact support@stripe.com for assistance.", null, null, 0, e);
        }
    }

    private static List<Parameter> flattenParams(Map<String, Object> map) throws InvalidRequestException {
        return flattenParamsMap(map, null);
    }

    private static List<Parameter> flattenParamsList(List<Object> list, String str) throws InvalidRequestException {
        LinkedList linkedList = new LinkedList();
        String format = String.format("%s[]", str);
        if (list.isEmpty()) {
            linkedList.add(new Parameter(str, ""));
        } else {
            for (Object obj : list) {
                linkedList.addAll(flattenParamsValue(obj, format));
            }
        }
        return linkedList;
    }

    private static List<Parameter> flattenParamsMap(Map<String, Object> map, String str) throws InvalidRequestException {
        LinkedList linkedList = new LinkedList();
        if (map == null) {
            return linkedList;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (str != null) {
                key = String.format("%s[%s]", str, key);
            }
            linkedList.addAll(flattenParamsValue(value, key));
        }
        return linkedList;
    }

    private static List<Parameter> flattenParamsValue(Object obj, String str) throws InvalidRequestException {
        if (obj instanceof Map) {
            return flattenParamsMap((Map) obj, str);
        }
        if (obj instanceof List) {
            return flattenParamsList((List) obj, str);
        }
        if (!"".equals(obj)) {
            if (obj == null) {
                LinkedList linkedList = new LinkedList();
                linkedList.add(new Parameter(str, ""));
                return linkedList;
            }
            LinkedList linkedList2 = new LinkedList();
            linkedList2.add(new Parameter(str, obj.toString()));
            return linkedList2;
        }
        throw new InvalidRequestException("You cannot set '" + str + "' to an empty string. We interpret empty strings as null in requests. You may set '" + str + "' to null to delete the property.", str, null, 0, null);
    }

    private static void handleAPIError(String str, int i, String str2) throws InvalidRequestException, AuthenticationException, CardException, APIException {
        ErrorParser.StripeError parseError = ErrorParser.parseError(str);
        if (i != 429) {
            switch (i) {
                case 400:
                    throw new InvalidRequestException(parseError.message, parseError.param, str2, Integer.valueOf(i), null);
                case 401:
                    throw new AuthenticationException(parseError.message, str2, Integer.valueOf(i));
                case 402:
                    throw new CardException(parseError.message, str2, parseError.code, parseError.param, parseError.decline_code, parseError.charge, Integer.valueOf(i), null);
                case 403:
                    throw new PermissionException(parseError.message, str2, Integer.valueOf(i));
                case 404:
                    throw new InvalidRequestException(parseError.message, parseError.param, str2, Integer.valueOf(i), null);
                default:
                    throw new APIException(parseError.message, str2, Integer.valueOf(i), null);
            }
        }
        throw new RateLimitException(parseError.message, parseError.param, str2, Integer.valueOf(i), null);
    }

    private static String urlEncodePair(String str, String str2) throws UnsupportedEncodingException {
        return String.format("%s=%s", urlEncode(str), urlEncode(str2));
    }

    private static String urlEncode(String str) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return URLEncoder.encode(str, "UTF-8");
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0028, code lost:
        if (r0 != 1) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x002a, code lost:
        r6 = createPostConnection(r7, r8, r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x003e, code lost:
        throw new com.stripe.android.exception.APIConnectionException(java.lang.String.format("Unrecognized HTTP method %s. This indicates a bug in the Stripe bindings. Please contact support@stripe.com for assistance.", r6));
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.stripe.android.net.StripeResponse makeURLConnectionRequest(java.lang.String r6, java.lang.String r7, java.lang.String r8, com.stripe.android.net.RequestOptions r9) throws com.stripe.android.exception.APIConnectionException {
        /*
            r0 = -1
            r1 = 0
            r2 = 1
            r3 = 0
            int r4 = r6.hashCode()     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            r5 = 70454(0x11336, float:9.8727E-41)
            if (r4 == r5) goto L1d
            r5 = 2461856(0x2590a0, float:3.449795E-39)
            if (r4 == r5) goto L13
            goto L26
        L13:
            java.lang.String r4 = "POST"
            boolean r4 = r6.equals(r4)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            if (r4 == 0) goto L26
            r0 = 1
            goto L26
        L1d:
            java.lang.String r4 = "GET"
            boolean r4 = r6.equals(r4)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            if (r4 == 0) goto L26
            r0 = 0
        L26:
            if (r0 == 0) goto L3f
            if (r0 != r2) goto L2f
            java.net.HttpURLConnection r6 = createPostConnection(r7, r8, r9)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            goto L43
        L2f:
            com.stripe.android.exception.APIConnectionException r7 = new com.stripe.android.exception.APIConnectionException     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            java.lang.String r8 = "Unrecognized HTTP method %s. This indicates a bug in the Stripe bindings. Please contact support@stripe.com for assistance."
            java.lang.Object[] r9 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            r9[r1] = r6     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            java.lang.String r6 = java.lang.String.format(r8, r9)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            r7.<init>(r6)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            throw r7     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
        L3f:
            java.net.HttpURLConnection r6 = createGetConnection(r7, r8, r9)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
        L43:
            r3 = r6
            int r6 = r3.getResponseCode()     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            r7 = 200(0xc8, float:2.8E-43)
            if (r6 < r7) goto L59
            r7 = 300(0x12c, float:4.2E-43)
            if (r6 >= r7) goto L59
            java.io.InputStream r7 = r3.getInputStream()     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            java.lang.String r7 = getResponseBody(r7)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            goto L61
        L59:
            java.io.InputStream r7 = r3.getErrorStream()     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            java.lang.String r7 = getResponseBody(r7)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
        L61:
            java.util.Map r8 = r3.getHeaderFields()     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            com.stripe.android.net.StripeResponse r9 = new com.stripe.android.net.StripeResponse     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            r9.<init>(r6, r7, r8)     // Catch: java.lang.Throwable -> L6e java.io.IOException -> L70
            r3.disconnect()
            return r9
        L6e:
            r6 = move-exception
            goto L8c
        L70:
            r6 = move-exception
            com.stripe.android.exception.APIConnectionException r7 = new com.stripe.android.exception.APIConnectionException     // Catch: java.lang.Throwable -> L6e
            java.lang.String r8 = "IOException during API request to Stripe (%s): %s Please check your internet connection and try again. If this problem persists, you should check Stripe's service status at https://twitter.com/stripestatus, or let us know at support@stripe.com."
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch: java.lang.Throwable -> L6e
            java.lang.String r0 = getApiUrl()     // Catch: java.lang.Throwable -> L6e
            r9[r1] = r0     // Catch: java.lang.Throwable -> L6e
            java.lang.String r0 = r6.getMessage()     // Catch: java.lang.Throwable -> L6e
            r9[r2] = r0     // Catch: java.lang.Throwable -> L6e
            java.lang.String r8 = java.lang.String.format(r8, r9)     // Catch: java.lang.Throwable -> L6e
            r7.<init>(r8, r6)     // Catch: java.lang.Throwable -> L6e
            throw r7     // Catch: java.lang.Throwable -> L6e
        L8c:
            if (r3 == 0) goto L91
            r3.disconnect()
        L91:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.stripe.android.net.StripeApiHandler.makeURLConnectionRequest(java.lang.String, java.lang.String, java.lang.String, com.stripe.android.net.RequestOptions):com.stripe.android.net.StripeResponse");
    }

    private static String getResponseBody(InputStream inputStream) throws IOException {
        String next = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
        inputStream.close();
        return next;
    }

    /* loaded from: classes.dex */
    public static final class Parameter {
        public final String key;
        public final String value;

        public Parameter(String str, String str2) {
            this.key = str;
            this.value = str2;
        }
    }
}
