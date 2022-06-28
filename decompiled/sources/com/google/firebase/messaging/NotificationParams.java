package com.google.firebase.messaging;

import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;
import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import org.json.JSONArray;
import org.json.JSONException;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes3.dex */
public class NotificationParams {
    private final Bundle data;

    public NotificationParams(Bundle data) {
        if (data == null) {
            throw new NullPointerException("data");
        }
        this.data = new Bundle(data);
    }

    private static int getLightColor(String str) {
        int parseColor = Color.parseColor(str);
        if (parseColor == -16777216) {
            throw new IllegalArgumentException("Transparent color is invalid");
        }
        return parseColor;
    }

    private static boolean isAnalyticsKey(String str) {
        return str.startsWith(Constants.AnalyticsKeys.PREFIX) || str.equals(Constants.MessagePayloadKeys.FROM);
    }

    private static boolean isReservedKey(String str) {
        return str.startsWith(Constants.MessagePayloadKeys.RESERVED_CLIENT_LIB_PREFIX) || str.startsWith(Constants.MessageNotificationKeys.NOTIFICATION_PREFIX) || str.startsWith(Constants.MessageNotificationKeys.NOTIFICATION_PREFIX_OLD);
    }

    private static String keyWithOldPrefix(String str) {
        return !str.startsWith(Constants.MessageNotificationKeys.NOTIFICATION_PREFIX) ? str : str.replace(Constants.MessageNotificationKeys.NOTIFICATION_PREFIX, Constants.MessageNotificationKeys.NOTIFICATION_PREFIX_OLD);
    }

    private String normalizePrefix(String str) {
        if (!this.data.containsKey(str) && str.startsWith(Constants.MessageNotificationKeys.NOTIFICATION_PREFIX)) {
            String keyWithOldPrefix = keyWithOldPrefix(str);
            if (this.data.containsKey(keyWithOldPrefix)) {
                return keyWithOldPrefix;
            }
        }
        return str;
    }

    private static String userFriendlyKey(String str) {
        return str.startsWith(Constants.MessageNotificationKeys.NOTIFICATION_PREFIX) ? str.substring(6) : str;
    }

    public boolean getBoolean(String key) {
        String key2 = getString(key);
        return IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE.equals(key2) || Boolean.parseBoolean(key2);
    }

    public Integer getInteger(String key) {
        String string = getString(key);
        if (!TextUtils.isEmpty(string)) {
            try {
                return Integer.valueOf(Integer.parseInt(string));
            } catch (NumberFormatException e) {
                String userFriendlyKey = userFriendlyKey(key);
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

    public JSONArray getJSONArray(String key) {
        String string = getString(key);
        if (!TextUtils.isEmpty(string)) {
            try {
                return new JSONArray(string);
            } catch (JSONException e) {
                String userFriendlyKey = userFriendlyKey(key);
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
        JSONArray jSONArray = getJSONArray(Constants.MessageNotificationKeys.LIGHT_SETTINGS);
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
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 60 + String.valueOf(message).length());
            sb.append("LightSettings is invalid: ");
            sb.append(valueOf);
            sb.append(". ");
            sb.append(message);
            sb.append(". Skipping setting LightSettings");
            Log.w("NotificationParams", sb.toString());
            return null;
        } catch (JSONException e2) {
            String valueOf2 = String.valueOf(jSONArray);
            StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf2).length() + 58);
            sb2.append("LightSettings is invalid: ");
            sb2.append(valueOf2);
            sb2.append(". Skipping setting LightSettings");
            Log.w("NotificationParams", sb2.toString());
            return null;
        }
    }

    public Uri getLink() {
        String string = getString(Constants.MessageNotificationKeys.LINK_ANDROID);
        if (TextUtils.isEmpty(string)) {
            string = getString(Constants.MessageNotificationKeys.LINK);
        }
        if (!TextUtils.isEmpty(string)) {
            return Uri.parse(string);
        }
        return null;
    }

    public Object[] getLocalizationArgsForKey(String key) {
        JSONArray jSONArray = getJSONArray(String.valueOf(key).concat(Constants.MessageNotificationKeys.TEXT_ARGS_SUFFIX));
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

    public String getLocalizationResourceForKey(String key) {
        return getString(String.valueOf(key).concat(Constants.MessageNotificationKeys.TEXT_RESOURCE_SUFFIX));
    }

    public String getLocalizedString(Resources resources, String packageName, String key) {
        String localizationResourceForKey = getLocalizationResourceForKey(key);
        if (TextUtils.isEmpty(localizationResourceForKey)) {
            return null;
        }
        int identifier = resources.getIdentifier(localizationResourceForKey, StringTypedProperty.TYPE, packageName);
        if (identifier == 0) {
            String userFriendlyKey = userFriendlyKey(String.valueOf(key).concat(Constants.MessageNotificationKeys.TEXT_RESOURCE_SUFFIX));
            StringBuilder sb = new StringBuilder(String.valueOf(userFriendlyKey).length() + 49 + String.valueOf(key).length());
            sb.append(userFriendlyKey);
            sb.append(" resource not found: ");
            sb.append(key);
            sb.append(" Default value will be used.");
            Log.w("NotificationParams", sb.toString());
            return null;
        }
        Object[] localizationArgsForKey = getLocalizationArgsForKey(key);
        if (localizationArgsForKey == null) {
            return resources.getString(identifier);
        }
        try {
            return resources.getString(identifier, localizationArgsForKey);
        } catch (MissingFormatArgumentException e) {
            String userFriendlyKey2 = userFriendlyKey(key);
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

    public Long getLong(String key) {
        String string = getString(key);
        if (!TextUtils.isEmpty(string)) {
            try {
                return Long.valueOf(Long.parseLong(string));
            } catch (NumberFormatException e) {
                String userFriendlyKey = userFriendlyKey(key);
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
        return getString(Constants.MessageNotificationKeys.CHANNEL);
    }

    public Integer getNotificationCount() {
        Integer integer = getInteger(Constants.MessageNotificationKeys.NOTIFICATION_COUNT);
        if (integer == null) {
            return null;
        }
        if (integer.intValue() >= 0) {
            return integer;
        }
        String valueOf = String.valueOf(integer);
        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 67);
        sb.append("notificationCount is invalid: ");
        sb.append(valueOf);
        sb.append(". Skipping setting notificationCount.");
        Log.w(Constants.TAG, sb.toString());
        return null;
    }

    public Integer getNotificationPriority() {
        Integer integer = getInteger(Constants.MessageNotificationKeys.NOTIFICATION_PRIORITY);
        if (integer == null) {
            return null;
        }
        if (integer.intValue() >= -2 && integer.intValue() <= 2) {
            return integer;
        }
        String valueOf = String.valueOf(integer);
        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 72);
        sb.append("notificationPriority is invalid ");
        sb.append(valueOf);
        sb.append(". Skipping setting notificationPriority.");
        Log.w(Constants.TAG, sb.toString());
        return null;
    }

    public String getPossiblyLocalizedString(Resources resources, String packageName, String key) {
        String string = getString(key);
        return !TextUtils.isEmpty(string) ? string : getLocalizedString(resources, packageName, key);
    }

    public String getSoundResourceName() {
        String string = getString(Constants.MessageNotificationKeys.SOUND_2);
        return TextUtils.isEmpty(string) ? getString(Constants.MessageNotificationKeys.SOUND) : string;
    }

    public String getString(String key) {
        return this.data.getString(normalizePrefix(key));
    }

    public long[] getVibrateTimings() {
        JSONArray jSONArray = getJSONArray(Constants.MessageNotificationKeys.VIBRATE_TIMINGS);
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
        } catch (NumberFormatException | JSONException e) {
            String valueOf = String.valueOf(jSONArray);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 74);
            sb.append("User defined vibrateTimings is invalid: ");
            sb.append(valueOf);
            sb.append(". Skipping setting vibrateTimings.");
            Log.w("NotificationParams", sb.toString());
            return null;
        }
    }

    public Integer getVisibility() {
        Integer integer = getInteger(Constants.MessageNotificationKeys.VISIBILITY);
        if (integer == null) {
            return null;
        }
        if (integer.intValue() >= -1 && integer.intValue() <= 1) {
            return integer;
        }
        String valueOf = String.valueOf(integer);
        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 53);
        sb.append("visibility is invalid: ");
        sb.append(valueOf);
        sb.append(". Skipping setting visibility.");
        Log.w("NotificationParams", sb.toString());
        return null;
    }

    public boolean hasImage() {
        return !TextUtils.isEmpty(getString(Constants.MessageNotificationKeys.IMAGE_URL));
    }

    public boolean isNotification() {
        return getBoolean(Constants.MessageNotificationKeys.ENABLE_NOTIFICATION);
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

    public static boolean isNotification(Bundle data) {
        return IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE.equals(data.getString(Constants.MessageNotificationKeys.ENABLE_NOTIFICATION)) || IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE.equals(data.getString(keyWithOldPrefix(Constants.MessageNotificationKeys.ENABLE_NOTIFICATION)));
    }
}
