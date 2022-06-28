package com.google.firebase.remoteconfig.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.content.pm.PackageInfoCompat;
import com.google.android.gms.common.util.AndroidUtilsLight;
import com.google.android.gms.common.util.Hex;
import com.google.firebase.remoteconfig.BuildConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigClientException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigServerException;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.google.firebase.remoteconfig.internal.ConfigContainer;
import com.google.firebase.remoteconfig.internal.ConfigFetchHandler;
import com.microsoft.appcenter.http.DefaultHttpClient;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class ConfigFetchHttpClient {
    private static final String API_KEY_HEADER = "X-Goog-Api-Key";
    private static final String ETAG_HEADER = "ETag";
    private static final Pattern GMP_APP_ID_PATTERN = Pattern.compile("^[^:]+:([0-9]+):(android|ios|web):([0-9a-f]+)");
    private static final String IF_NONE_MATCH_HEADER = "If-None-Match";
    private static final String INSTALLATIONS_AUTH_TOKEN_HEADER = "X-Goog-Firebase-Installations-Auth";
    private static final String X_ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String X_ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final String X_GOOGLE_GFE_CAN_RETRY = "X-Google-GFE-Can-Retry";
    private final String apiKey;
    private final String appId;
    private final long connectTimeoutInSeconds;
    private final Context context;
    private final String namespace;
    private final String projectNumber;
    private final long readTimeoutInSeconds;

    public ConfigFetchHttpClient(Context context, String appId, String apiKey, String namespace, long connectTimeoutInSeconds, long readTimeoutInSeconds) {
        this.context = context;
        this.appId = appId;
        this.apiKey = apiKey;
        this.projectNumber = extractProjectNumberFromAppId(appId);
        this.namespace = namespace;
        this.connectTimeoutInSeconds = connectTimeoutInSeconds;
        this.readTimeoutInSeconds = readTimeoutInSeconds;
    }

    public long getConnectTimeoutInSeconds() {
        return this.connectTimeoutInSeconds;
    }

    public long getReadTimeoutInSeconds() {
        return this.readTimeoutInSeconds;
    }

    private static String extractProjectNumberFromAppId(String gmpAppId) {
        Matcher matcher = GMP_APP_ID_PATTERN.matcher(gmpAppId);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public HttpURLConnection createHttpURLConnection() throws FirebaseRemoteConfigException {
        try {
            URL url = new URL(getFetchUrl(this.projectNumber, this.namespace));
            return (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new FirebaseRemoteConfigException(e.getMessage());
        }
    }

    public ConfigFetchHandler.FetchResponse fetch(HttpURLConnection urlConnection, String installationId, String installationAuthToken, Map<String, String> analyticsUserProperties, String lastFetchETag, Map<String, String> customHeaders, Date currentTime) throws FirebaseRemoteConfigException {
        setUpUrlConnection(urlConnection, lastFetchETag, installationAuthToken, customHeaders);
        try {
            try {
                byte[] requestBody = createFetchRequestBody(installationId, installationAuthToken, analyticsUserProperties).toString().getBytes("utf-8");
                setFetchRequestBody(urlConnection, requestBody);
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    throw new FirebaseRemoteConfigServerException(responseCode, urlConnection.getResponseMessage());
                }
                String fetchResponseETag = urlConnection.getHeaderField(ETAG_HEADER);
                JSONObject fetchResponse = getFetchResponseBody(urlConnection);
                if (!backendHasUpdates(fetchResponse)) {
                    return ConfigFetchHandler.FetchResponse.forBackendHasNoUpdates(currentTime);
                }
                ConfigContainer fetchedConfigs = extractConfigs(fetchResponse, currentTime);
                return ConfigFetchHandler.FetchResponse.forBackendUpdatesFetched(fetchedConfigs, fetchResponseETag);
            } finally {
                urlConnection.disconnect();
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                }
            }
        } catch (IOException | JSONException e2) {
            throw new FirebaseRemoteConfigClientException("The client had an error while calling the backend!", e2);
        }
    }

    private void setUpUrlConnection(HttpURLConnection urlConnection, String lastFetchEtag, String installationAuthToken, Map<String, String> customHeaders) {
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(this.connectTimeoutInSeconds));
        urlConnection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(this.readTimeoutInSeconds));
        urlConnection.setRequestProperty(IF_NONE_MATCH_HEADER, lastFetchEtag);
        setCommonRequestHeaders(urlConnection, installationAuthToken);
        setCustomRequestHeaders(urlConnection, customHeaders);
    }

    private String getFetchUrl(String projectNumber, String namespace) {
        return String.format(RemoteConfigConstants.FETCH_REGEX_URL, projectNumber, namespace);
    }

    private void setCommonRequestHeaders(HttpURLConnection urlConnection, String installationAuthToken) {
        urlConnection.setRequestProperty(API_KEY_HEADER, this.apiKey);
        urlConnection.setRequestProperty(X_ANDROID_PACKAGE_HEADER, this.context.getPackageName());
        urlConnection.setRequestProperty(X_ANDROID_CERT_HEADER, getFingerprintHashForPackage());
        urlConnection.setRequestProperty(X_GOOGLE_GFE_CAN_RETRY, "yes");
        urlConnection.setRequestProperty(INSTALLATIONS_AUTH_TOKEN_HEADER, installationAuthToken);
        urlConnection.setRequestProperty(DefaultHttpClient.CONTENT_TYPE_KEY, "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
    }

    private void setCustomRequestHeaders(HttpURLConnection urlConnection, Map<String, String> customHeaders) {
        for (Map.Entry<String, String> customHeaderEntry : customHeaders.entrySet()) {
            urlConnection.setRequestProperty(customHeaderEntry.getKey(), customHeaderEntry.getValue());
        }
    }

    private String getFingerprintHashForPackage() {
        try {
            Context context = this.context;
            byte[] hash = AndroidUtilsLight.getPackageCertificateHashBytes(context, context.getPackageName());
            if (hash == null) {
                Log.e(FirebaseRemoteConfig.TAG, "Could not get fingerprint hash for package: " + this.context.getPackageName());
                return null;
            }
            return Hex.bytesToStringUppercase(hash, false);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(FirebaseRemoteConfig.TAG, "No such package: " + this.context.getPackageName(), e);
            return null;
        }
    }

    private JSONObject createFetchRequestBody(String installationId, String installationAuthToken, Map<String, String> analyticsUserProperties) throws FirebaseRemoteConfigClientException {
        String languageCode;
        Map<String, Object> requestBodyMap = new HashMap<>();
        if (installationId == null) {
            throw new FirebaseRemoteConfigClientException("Fetch failed: Firebase installation id is null.");
        }
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.INSTANCE_ID, installationId);
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.INSTANCE_ID_TOKEN, installationAuthToken);
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.APP_ID, this.appId);
        Locale locale = this.context.getResources().getConfiguration().locale;
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.COUNTRY_CODE, locale.getCountry());
        if (Build.VERSION.SDK_INT >= 21) {
            languageCode = locale.toLanguageTag();
        } else {
            languageCode = locale.toString();
        }
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.LANGUAGE_CODE, languageCode);
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.PLATFORM_VERSION, Integer.toString(Build.VERSION.SDK_INT));
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.TIME_ZONE, TimeZone.getDefault().getID());
        try {
            PackageInfo packageInfo = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
            if (packageInfo != null) {
                requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.APP_VERSION, packageInfo.versionName);
                requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.APP_BUILD, Long.toString(PackageInfoCompat.getLongVersionCode(packageInfo)));
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.PACKAGE_NAME, this.context.getPackageName());
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.SDK_VERSION, BuildConfig.VERSION_NAME);
        requestBodyMap.put(RemoteConfigConstants.RequestFieldKey.ANALYTICS_USER_PROPERTIES, new JSONObject(analyticsUserProperties));
        return new JSONObject(requestBodyMap);
    }

    private void setFetchRequestBody(HttpURLConnection urlConnection, byte[] requestBody) throws IOException {
        urlConnection.setFixedLengthStreamingMode(requestBody.length);
        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
        out.write(requestBody);
        out.flush();
        out.close();
    }

    private JSONObject getFetchResponseBody(URLConnection urlConnection) throws IOException, JSONException {
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
        StringBuilder responseStringBuilder = new StringBuilder();
        while (true) {
            int current = br.read();
            if (current != -1) {
                responseStringBuilder.append((char) current);
            } else {
                return new JSONObject(responseStringBuilder.toString());
            }
        }
    }

    private boolean backendHasUpdates(JSONObject response) {
        try {
            return true ^ response.get(RemoteConfigConstants.ResponseFieldKey.STATE).equals("NO_CHANGE");
        } catch (JSONException e) {
            return true;
        }
    }

    private static ConfigContainer extractConfigs(JSONObject fetchResponse, Date fetchTime) throws FirebaseRemoteConfigClientException {
        try {
            ConfigContainer.Builder containerBuilder = ConfigContainer.newBuilder().withFetchTime(fetchTime);
            JSONObject entries = null;
            try {
                entries = fetchResponse.getJSONObject(RemoteConfigConstants.ResponseFieldKey.ENTRIES);
            } catch (JSONException e) {
            }
            if (entries != null) {
                containerBuilder.replaceConfigsWith(entries);
            }
            JSONArray experimentDescriptions = null;
            try {
                experimentDescriptions = fetchResponse.getJSONArray(RemoteConfigConstants.ResponseFieldKey.EXPERIMENT_DESCRIPTIONS);
            } catch (JSONException e2) {
            }
            if (experimentDescriptions != null) {
                containerBuilder.withAbtExperiments(experimentDescriptions);
            }
            JSONObject personalizationMetadata = null;
            try {
                personalizationMetadata = fetchResponse.getJSONObject(RemoteConfigConstants.ResponseFieldKey.PERSONALIZATION_METADATA);
            } catch (JSONException e3) {
            }
            if (personalizationMetadata != null) {
                containerBuilder.withPersonalizationMetadata(personalizationMetadata);
            }
            return containerBuilder.build();
        } catch (JSONException e4) {
            throw new FirebaseRemoteConfigClientException("Fetch failed: fetch response could not be parsed.", e4);
        }
    }
}
