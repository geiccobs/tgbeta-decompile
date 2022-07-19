package com.google.firebase.messaging;

import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import org.json.JSONArray;
import org.json.JSONException;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes.dex */
public class NotificationParams {
    private final Bundle data;

    public NotificationParams(Bundle bundle) {
        if (bundle == null) {
            throw new NullPointerException("data");
        }
        this.data = new Bundle(bundle);
    }

    private static int getLightColor(String str) {
        int parseColor = Color.parseColor(str);
        if (parseColor != -16777216) {
            return parseColor;
        }
        throw new IllegalArgumentException("Transparent color is invalid");
    }

    private static boolean isAnalyticsKey(String str) {
        return str.startsWith("google.c.a.") || str.equals("from");
    }

    private static boolean isReservedKey(String str) {
        return str.startsWith("google.c.") || str.startsWith("gcm.n.") || str.startsWith("gcm.notification.");
    }

    private static String keyWithOldPrefix(String str) {
        return !str.startsWith("gcm.n.") ? str : str.replace("gcm.n.", "gcm.notification.");
    }

    private String normalizePrefix(String str) {
        if (!this.data.containsKey(str) && str.startsWith("gcm.n.")) {
            String keyWithOldPrefix = keyWithOldPrefix(str);
            if (this.data.containsKey(keyWithOldPrefix)) {
                return keyWithOldPrefix;
            }
        }
        return str;
    }

    private static String userFriendlyKey(String str) {
        return str.startsWith("gcm.n.") ? str.substring(6) : str;
    }

    public boolean getBoolean(String str) {
        String string = getString(str);
        return "1".equals(string) || Boolean.parseBoolean(string);
    }

    public Integer getInteger(String str) {
        String string = getString(str);
        if (!TextUtils.isEmpty(string)) {
            try {
                return Integer.valueOf(Integer.parseInt(string));
            } catch (NumberFormatException unused) {
                String userFriendlyKey = userFriendlyKey(str);
                StringBuilder sb = new StringBuilder(String.valueOf(userFriendlyKey).length() + 38 + String.valueOf(string).length());
                sb.append("Couldn't parse value of ");
                sb.append(userFriendlyKey);
                sb.append("(");
                sb.append(string);
                sb.append(") into an int");
                Log.w("NotificationParams", sb.toString());
                return null;
            }
        }
        return null;
    }

    public JSONArray getJSONArray(String str) {
        String string = getString(str);
        if (!TextUtils.isEmpty(string)) {
            try {
                return new JSONArray(string);
            } catch (JSONException unused) {
                String userFriendlyKey = userFriendlyKey(str);
                StringBuilder sb = new StringBuilder(String.valueOf(userFriendlyKey).length() + 50 + String.valueOf(string).length());
                sb.append("Malformed JSON for key ");
                sb.append(userFriendlyKey);
                sb.append(": ");
                sb.append(string);
                sb.append(", falling back to default");
                Log.w("NotificationParams", sb.toString());
                return null;
            }
        }
        return null;
    }

    public int[] getLightSettings() {
        JSONArray jSONArray = getJSONArray("gcm.n.light_settings");
        if (jSONArray == null) {
            return null;
        }
        int[] iArr = new int[3];
        try {
            if (jSONArray.length() != 3) {
                throw new JSONException("lightSettings don't have all three fields");
            }
            iArr[0] = getLightColor(jSONArray.optString(0));
            iArr[1] = jSONArray.optInt(1);
            iArr[2] = jSONArray.optInt(2);
            return iArr;
        } catch (IllegalArgumentException e) {
            String valueOf = String.valueOf(jSONArray);
            String message = e.getMessage();
            StringBuilder sb = new StringBuilder(valueOf.length() + 60 + String.valueOf(message).length());
            sb.append("LightSettings is invalid: ");
            sb.append(valueOf);
            sb.append(". ");
            sb.append(message);
            sb.append(". Skipping setting LightSettings");
            Log.w("NotificationParams", sb.toString());
            return null;
        } catch (JSONException unused) {
            String valueOf2 = String.valueOf(jSONArray);
            StringBuilder sb2 = new StringBuilder(valueOf2.length() + 58);
            sb2.append("LightSettings is invalid: ");
            sb2.append(valueOf2);
            sb2.append(". Skipping setting LightSettings");
            Log.w("NotificationParams", sb2.toString());
            return null;
        }
    }

    public Uri getLink() {
        String string = getString("gcm.n.link_android");
        if (TextUtils.isEmpty(string)) {
            string = getString("gcm.n.link");
        }
        if (!TextUtils.isEmpty(string)) {
            return Uri.parse(string);
        }
        return null;
    }

    public Object[] getLocalizationArgsForKey(String str) {
        JSONArray jSONArray = getJSONArray(String.valueOf(str).concat("_loc_args"));
        if (jSONArray == null) {
            return null;
        }
        int length = jSONArray.length();
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = jSONArray.optString(i);
        }
        return strArr;
    }

    public String getLocalizationResourceForKey(String str) {
        return getString(String.valueOf(str).concat("_loc_key"));
    }

    public String getLocalizedString(Resources resources, String str, String str2) {
        String localizationResourceForKey = getLocalizationResourceForKey(str2);
        if (TextUtils.isEmpty(localizationResourceForKey)) {
            return null;
        }
        int identifier = resources.getIdentifier(localizationResourceForKey, "string", str);
        if (identifier == 0) {
            String userFriendlyKey = userFriendlyKey(String.valueOf(str2).concat("_loc_key"));
            StringBuilder sb = new StringBuilder(String.valueOf(userFriendlyKey).length() + 49 + String.valueOf(str2).length());
            sb.append(userFriendlyKey);
            sb.append(" resource not found: ");
            sb.append(str2);
            sb.append(" Default value will be used.");
            Log.w("NotificationParams", sb.toString());
            return null;
        }
        Object[] localizationArgsForKey = getLocalizationArgsForKey(str2);
        if (localizationArgsForKey == null) {
            return resources.getString(identifier);
        }
        try {
            return resources.getString(identifier, localizationArgsForKey);
        } catch (MissingFormatArgumentException e) {
            String userFriendlyKey2 = userFriendlyKey(str2);
            String arrays = Arrays.toString(localizationArgsForKey);
            StringBuilder sb2 = new StringBuilder(String.valueOf(userFriendlyKey2).length() + 58 + String.valueOf(arrays).length());
            sb2.append("Missing format argument for ");
            sb2.append(userFriendlyKey2);
            sb2.append(": ");
            sb2.append(arrays);
            sb2.append(" Default value will be used.");
            Log.w("NotificationParams", sb2.toString(), e);
            return null;
        }
    }

    public Long getLong(String str) {
        String string = getString(str);
        if (!TextUtils.isEmpty(string)) {
            try {
                return Long.valueOf(Long.parseLong(string));
            } catch (NumberFormatException unused) {
                String userFriendlyKey = userFriendlyKey(str);
                StringBuilder sb = new StringBuilder(String.valueOf(userFriendlyKey).length() + 38 + String.valueOf(string).length());
                sb.append("Couldn't parse value of ");
                sb.append(userFriendlyKey);
                sb.append("(");
                sb.append(string);
                sb.append(") into a long");
                Log.w("NotificationParams", sb.toString());
                return null;
            }
        }
        return null;
    }

    public String getNotificationChannelId() {
        return getString("gcm.n.android_channel_id");
    }

    public Integer getNotificationCount() {
        Integer integer = getInteger("gcm.n.notification_count");
        if (integer == null) {
            return null;
        }
        if (integer.intValue() >= 0) {
            return integer;
        }
        String valueOf = String.valueOf(integer);
        StringBuilder sb = new StringBuilder(valueOf.length() + 67);
        sb.append("notificationCount is invalid: ");
        sb.append(valueOf);
        sb.append(". Skipping setting notificationCount.");
        Log.w("FirebaseMessaging", sb.toString());
        return null;
    }

    public Integer getNotificationPriority() {
        Integer integer = getInteger("gcm.n.notification_priority");
        if (integer == null) {
            return null;
        }
        if (integer.intValue() >= -2 && integer.intValue() <= 2) {
            return integer;
        }
        String valueOf = String.valueOf(integer);
        StringBuilder sb = new StringBuilder(valueOf.length() + 72);
        sb.append("notificationPriority is invalid ");
        sb.append(valueOf);
        sb.append(". Skipping setting notificationPriority.");
        Log.w("FirebaseMessaging", sb.toString());
        return null;
    }

    public String getPossiblyLocalizedString(Resources resources, String str, String str2) {
        String string = getString(str2);
        return !TextUtils.isEmpty(string) ? string : getLocalizedString(resources, str, str2);
    }

    public String getSoundResourceName() {
        String string = getString("gcm.n.sound2");
        return TextUtils.isEmpty(string) ? getString("gcm.n.sound") : string;
    }

    public String getString(String str) {
        return this.data.getString(normalizePrefix(str));
    }

    public long[] getVibrateTimings() {
        JSONArray jSONArray = getJSONArray("gcm.n.vibrate_timings");
        if (jSONArray == null) {
            return null;
        }
        try {
            if (jSONArray.length() <= 1) {
                throw new JSONException("vibrateTimings have invalid length");
            }
            int length = jSONArray.length();
            long[] jArr = new long[length];
            for (int i = 0; i < length; i++) {
                jArr[i] = jSONArray.optLong(i);
            }
            return jArr;
        } catch (NumberFormatException | JSONException unused) {
            String valueOf = String.valueOf(jSONArray);
            StringBuilder sb = new StringBuilder(valueOf.length() + 74);
            sb.append("User defined vibrateTimings is invalid: ");
            sb.append(valueOf);
            sb.append(". Skipping setting vibrateTimings.");
            Log.w("NotificationParams", sb.toString());
            return null;
        }
    }

    public Integer getVisibility() {
        Integer integer = getInteger("gcm.n.visibility");
        if (integer == null) {
            return null;
        }
        if (integer.intValue() >= -1 && integer.intValue() <= 1) {
            return integer;
        }
        String valueOf = String.valueOf(integer);
        StringBuilder sb = new StringBuilder(valueOf.length() + 53);
        sb.append("visibility is invalid: ");
        sb.append(valueOf);
        sb.append(". Skipping setting visibility.");
        Log.w("NotificationParams", sb.toString());
        return null;
    }

    public Bundle paramsForAnalyticsIntent() {
        Bundle bundle = new Bundle(this.data);
        for (String str : this.data.keySet()) {
            if (!isAnalyticsKey(str)) {
                bundle.remove(str);
            }
        }
        return bundle;
    }

    public Bundle paramsWithReservedKeysRemoved() {
        Bundle bundle = new Bundle(this.data);
        for (String str : this.data.keySet()) {
            if (isReservedKey(str)) {
                bundle.remove(str);
            }
        }
        return bundle;
    }

    public static boolean isNotification(Bundle bundle) {
        return "1".equals(bundle.getString("gcm.n.e")) || "1".equals(bundle.getString(keyWithOldPrefix("gcm.n.e")));
    }
}
