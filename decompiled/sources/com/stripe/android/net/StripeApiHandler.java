package com.stripe.android.net;

import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.appindexing.Indexable;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.http.DefaultHttpClient;
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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Security;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class StripeApiHandler {
    public static final String CHARSET = "UTF-8";
    private static final String DNS_CACHE_TTL_PROPERTY_NAME = "networkaddress.cache.ttl";
    static final String GET = "GET";
    public static final String LIVE_API_BASE = "https://api.stripe.com";
    static final String POST = "POST";
    private static final SSLSocketFactory SSL_SOCKET_FACTORY = new StripeSSLSocketFactory();
    public static final String TOKENS = "tokens";
    public static final String VERSION = "3.5.0";

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface RestMethod {
    }

    public static Token createToken(Map<String, Object> cardParams, RequestOptions options) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        return requestToken("POST", getApiUrl(), cardParams, options);
    }

    public static Token retrieveToken(RequestOptions options, String tokenId) throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException {
        try {
            return requestToken("GET", getRetrieveTokenApiUrl(tokenId), null, options);
        } catch (CardException cardException) {
            throw new APIException(cardException.getMessage(), cardException.getRequestId(), cardException.getStatusCode(), cardException);
        }
    }

    static String createQuery(Map<String, Object> params) throws UnsupportedEncodingException, InvalidRequestException {
        StringBuilder queryStringBuffer = new StringBuilder();
        List<Parameter> flatParams = flattenParams(params);
        for (Parameter param : flatParams) {
            if (queryStringBuffer.length() > 0) {
                queryStringBuffer.append("&");
            }
            queryStringBuffer.append(urlEncodePair(param.key, param.value));
        }
        return queryStringBuffer.toString();
    }

    static Map<String, String> getHeaders(RequestOptions options) {
        Map<String, String> headers = new HashMap<>();
        String apiVersion = options.getApiVersion();
        headers.put("Accept-Charset", "UTF-8");
        headers.put("Accept", "application/json");
        headers.put("User-Agent", String.format("Stripe/v1 JavaBindings/%s", VERSION));
        headers.put(Constants.AUTHORIZATION_HEADER, String.format("Bearer %s", options.getPublishableApiKey()));
        String[] propertyNames = {"os.name", "os.version", "os.arch", "java.version", "java.vendor", "java.vm.version", "java.vm.vendor"};
        Map<String, String> propertyMap = new HashMap<>();
        for (String propertyName : propertyNames) {
            propertyMap.put(propertyName, System.getProperty(propertyName));
        }
        propertyMap.put("bindings.version", VERSION);
        propertyMap.put("lang", "Java");
        propertyMap.put("publisher", "Stripe");
        JSONObject headerMappingObject = new JSONObject(propertyMap);
        headers.put("X-Stripe-Client-User-Agent", headerMappingObject.toString());
        if (apiVersion != null) {
            headers.put("Stripe-Version", apiVersion);
        }
        if (options.getIdempotencyKey() != null) {
            headers.put("Idempotency-Key", options.getIdempotencyKey());
        }
        return headers;
    }

    static String getApiUrl() {
        return String.format("%s/v1/%s", LIVE_API_BASE, TOKENS);
    }

    static String getRetrieveTokenApiUrl(String tokenId) {
        return String.format("%s/%s", getApiUrl(), tokenId);
    }

    private static String formatURL(String url, String query) {
        if (query == null || query.isEmpty()) {
            return url;
        }
        String separator = "?";
        if (url.contains(separator)) {
            separator = "&";
        }
        return String.format("%s%s%s", url, separator, query);
    }

    private static HttpURLConnection createGetConnection(String url, String query, RequestOptions options) throws IOException {
        String getURL = formatURL(url, query);
        HttpURLConnection conn = createStripeConnection(getURL, options);
        conn.setRequestMethod("GET");
        return conn;
    }

    private static HttpURLConnection createPostConnection(String url, String query, RequestOptions options) throws IOException {
        HttpURLConnection conn = createStripeConnection(url, options);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty(DefaultHttpClient.CONTENT_TYPE_KEY, String.format("application/x-www-form-urlencoded;charset=%s", "UTF-8"));
        OutputStream output = null;
        try {
            output = conn.getOutputStream();
            output.write(query.getBytes("UTF-8"));
            return conn;
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private static HttpURLConnection createStripeConnection(String url, RequestOptions options) throws IOException {
        URL stripeURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) stripeURL.openConnection();
        conn.setConnectTimeout(Indexable.MAX_BYTE_SIZE);
        conn.setReadTimeout(80000);
        conn.setUseCaches(false);
        for (Map.Entry<String, String> header : getHeaders(options).entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(SSL_SOCKET_FACTORY);
        }
        return conn;
    }

    private static Token requestToken(String method, String url, Map<String, Object> params, RequestOptions options) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        if (options == null) {
            return null;
        }
        String originalDNSCacheTTL = null;
        Boolean allowedToSetTTL = true;
        try {
            originalDNSCacheTTL = Security.getProperty(DNS_CACHE_TTL_PROPERTY_NAME);
            Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "0");
        } catch (SecurityException e) {
            allowedToSetTTL = false;
        }
        String apiKey = options.getPublishableApiKey();
        if (apiKey.trim().isEmpty()) {
            throw new AuthenticationException("No API key provided. (HINT: set your API key using 'Stripe.apiKey = <API-KEY>'. You can generate API keys from the Stripe web interface. See https://stripe.com/api for details or email support@stripe.com if you have questions.", null, 0);
        }
        try {
            StripeResponse response = getStripeResponse(method, url, params, options);
            int rCode = response.getResponseCode();
            String rBody = response.getResponseBody();
            String requestId = null;
            Map<String, List<String>> headers = response.getResponseHeaders();
            List<String> requestIdList = headers == null ? null : headers.get("Request-Id");
            if (requestIdList != null && requestIdList.size() > 0) {
                requestId = requestIdList.get(0);
            }
            if (rCode < 200 || rCode >= 300) {
                handleAPIError(rBody, rCode, requestId);
            }
            Token parseToken = TokenParser.parseToken(rBody);
            if (allowedToSetTTL.booleanValue()) {
                if (originalDNSCacheTTL == null) {
                    Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "-1");
                } else {
                    Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
                }
            }
            return parseToken;
        } catch (JSONException e2) {
            if (allowedToSetTTL.booleanValue()) {
                if (originalDNSCacheTTL == null) {
                    Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "-1");
                } else {
                    Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
                }
            }
            return null;
        } catch (Throwable th) {
            if (allowedToSetTTL.booleanValue()) {
                if (originalDNSCacheTTL == null) {
                    Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "-1");
                } else {
                    Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
                }
            }
            throw th;
        }
    }

    private static StripeResponse getStripeResponse(String method, String url, Map<String, Object> params, RequestOptions options) throws InvalidRequestException, APIConnectionException, APIException {
        try {
            String query = createQuery(params);
            return makeURLConnectionRequest(method, url, query, options);
        } catch (UnsupportedEncodingException e) {
            throw new InvalidRequestException("Unable to encode parameters to UTF-8. Please contact support@stripe.com for assistance.", null, null, 0, e);
        }
    }

    private static List<Parameter> flattenParams(Map<String, Object> params) throws InvalidRequestException {
        return flattenParamsMap(params, null);
    }

    private static List<Parameter> flattenParamsList(List<Object> params, String keyPrefix) throws InvalidRequestException {
        List<Parameter> flatParams = new LinkedList<>();
        String newPrefix = String.format("%s[]", keyPrefix);
        if (params.isEmpty()) {
            flatParams.add(new Parameter(keyPrefix, ""));
        } else {
            for (Object obj : params) {
                flatParams.addAll(flattenParamsValue(obj, newPrefix));
            }
        }
        return flatParams;
    }

    private static List<Parameter> flattenParamsMap(Map<String, Object> params, String keyPrefix) throws InvalidRequestException {
        List<Parameter> flatParams = new LinkedList<>();
        if (params == null) {
            return flatParams;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String newPrefix = key;
            if (keyPrefix != null) {
                newPrefix = String.format("%s[%s]", keyPrefix, key);
            }
            flatParams.addAll(flattenParamsValue(value, newPrefix));
        }
        return flatParams;
    }

    private static List<Parameter> flattenParamsValue(Object value, String keyPrefix) throws InvalidRequestException {
        if (value instanceof Map) {
            return flattenParamsMap((Map) value, keyPrefix);
        }
        if (value instanceof List) {
            return flattenParamsList((List) value, keyPrefix);
        }
        if ("".equals(value)) {
            throw new InvalidRequestException("You cannot set '" + keyPrefix + "' to an empty string. We interpret empty strings as null in requests. You may set '" + keyPrefix + "' to null to delete the property.", keyPrefix, null, 0, null);
        } else if (value == null) {
            List<Parameter> flatParams = new LinkedList<>();
            flatParams.add(new Parameter(keyPrefix, ""));
            return flatParams;
        } else {
            List<Parameter> flatParams2 = new LinkedList<>();
            flatParams2.add(new Parameter(keyPrefix, value.toString()));
            return flatParams2;
        }
    }

    private static void handleAPIError(String rBody, int rCode, String requestId) throws InvalidRequestException, AuthenticationException, CardException, APIException {
        ErrorParser.StripeError stripeError = ErrorParser.parseError(rBody);
        switch (rCode) {
            case 400:
                throw new InvalidRequestException(stripeError.message, stripeError.param, requestId, Integer.valueOf(rCode), null);
            case 401:
                throw new AuthenticationException(stripeError.message, requestId, Integer.valueOf(rCode));
            case WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE /* 402 */:
                throw new CardException(stripeError.message, requestId, stripeError.code, stripeError.param, stripeError.decline_code, stripeError.charge, Integer.valueOf(rCode), null);
            case 403:
                throw new PermissionException(stripeError.message, requestId, Integer.valueOf(rCode));
            case WalletConstants.ERROR_CODE_INVALID_PARAMETERS /* 404 */:
                throw new InvalidRequestException(stripeError.message, stripeError.param, requestId, Integer.valueOf(rCode), null);
            case 429:
                throw new RateLimitException(stripeError.message, stripeError.param, requestId, Integer.valueOf(rCode), null);
            default:
                throw new APIException(stripeError.message, requestId, Integer.valueOf(rCode), null);
        }
    }

    private static String urlEncodePair(String k, String v) throws UnsupportedEncodingException {
        return String.format("%s=%s", urlEncode(k), urlEncode(v));
    }

    private static String urlEncode(String str) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return URLEncoder.encode(str, "UTF-8");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static StripeResponse makeURLConnectionRequest(String method, String url, String query, RequestOptions options) throws APIConnectionException {
        String rBody;
        HttpURLConnection conn;
        HttpURLConnection conn2 = null;
        char c = 65535;
        try {
            try {
                switch (method.hashCode()) {
                    case 70454:
                        if (method.equals("GET")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 2461856:
                        if (method.equals("POST")) {
                            c = 1;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        conn = createGetConnection(url, query, options);
                        break;
                    case 1:
                        conn = createPostConnection(url, query, options);
                        break;
                    default:
                        throw new APIConnectionException(String.format("Unrecognized HTTP method %s. This indicates a bug in the Stripe bindings. Please contact support@stripe.com for assistance.", method));
                }
                int rCode = conn2.getResponseCode();
                if (rCode >= 200 && rCode < 300) {
                    rBody = getResponseBody(conn2.getInputStream());
                } else {
                    rBody = getResponseBody(conn2.getErrorStream());
                }
                Map<String, List<String>> headers = conn2.getHeaderFields();
                return new StripeResponse(rCode, rBody, headers);
            } catch (IOException e) {
                throw new APIConnectionException(String.format("IOException during API request to Stripe (%s): %s Please check your internet connection and try again. If this problem persists, you should check Stripe's service status at https://twitter.com/stripestatus, or let us know at support@stripe.com.", getApiUrl(), e.getMessage()), e);
            }
        } finally {
            if (conn2 != null) {
                conn2.disconnect();
            }
        }
    }

    private static String getResponseBody(InputStream responseStream) throws IOException {
        String rBody = new Scanner(responseStream, "UTF-8").useDelimiter("\\A").next();
        responseStream.close();
        return rBody;
    }

    /* loaded from: classes3.dex */
    public static final class Parameter {
        public final String key;
        public final String value;

        public Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
