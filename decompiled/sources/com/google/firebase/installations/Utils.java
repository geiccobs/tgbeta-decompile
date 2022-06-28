package com.google.firebase.installations;

import android.text.TextUtils;
import com.google.firebase.installations.local.PersistedInstallationEntry;
import com.google.firebase.installations.time.Clock;
import com.google.firebase.installations.time.SystemClock;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class Utils {
    private static final String APP_ID_IDENTIFICATION_SUBSTRING = ":";
    private static Utils singleton;
    private final Clock clock;
    public static final long AUTH_TOKEN_EXPIRATION_BUFFER_IN_SECS = TimeUnit.HOURS.toSeconds(1);
    private static final Pattern API_KEY_FORMAT = Pattern.compile("\\AA[\\w-]{38}\\z");

    private Utils(Clock clock) {
        this.clock = clock;
    }

    public static Utils getInstance() {
        return getInstance(SystemClock.getInstance());
    }

    public static Utils getInstance(Clock clock) {
        if (singleton == null) {
            singleton = new Utils(clock);
        }
        return singleton;
    }

    public boolean isAuthTokenExpired(PersistedInstallationEntry entry) {
        return TextUtils.isEmpty(entry.getAuthToken()) || entry.getTokenCreationEpochInSecs() + entry.getExpiresInSecs() < currentTimeInSecs() + AUTH_TOKEN_EXPIRATION_BUFFER_IN_SECS;
    }

    public long currentTimeInSecs() {
        return TimeUnit.MILLISECONDS.toSeconds(currentTimeInMillis());
    }

    public long currentTimeInMillis() {
        return this.clock.currentTimeMillis();
    }

    public static boolean isValidAppIdFormat(String appId) {
        return appId.contains(":");
    }

    public static boolean isValidApiKeyFormat(String apiKey) {
        return API_KEY_FORMAT.matcher(apiKey).matches();
    }

    public long getRandomDelayForSyncPrevention() {
        return (long) (Math.random() * 1000.0d);
    }
}
