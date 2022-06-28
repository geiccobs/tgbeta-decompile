package com.microsoft.appcenter;

import com.microsoft.appcenter.utils.AppCenterLog;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public class CustomProperties {
    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
    static final int MAX_PROPERTIES_COUNT = 60;
    static final int MAX_PROPERTY_KEY_LENGTH = 128;
    private static final int MAX_PROPERTY_VALUE_LENGTH = 128;
    private static final String VALUE_NULL_ERROR_MESSAGE = "Custom property value cannot be null, did you mean to call clear?";
    private final Map<String, Object> mProperties = new HashMap();

    public synchronized Map<String, Object> getProperties() {
        return new HashMap(this.mProperties);
    }

    public synchronized CustomProperties set(String key, String value) {
        if (isValidKey(key) && isValidStringValue(key, value)) {
            addProperty(key, value);
        }
        return this;
    }

    public synchronized CustomProperties set(String key, Date value) {
        if (isValidKey(key)) {
            if (value != null) {
                addProperty(key, value);
            } else {
                AppCenterLog.error("AppCenter", VALUE_NULL_ERROR_MESSAGE);
            }
        }
        return this;
    }

    public synchronized CustomProperties set(String key, Number value) {
        if (isValidKey(key) && isValidNumberValue(key, value)) {
            addProperty(key, value);
        }
        return this;
    }

    public synchronized CustomProperties set(String key, boolean value) {
        if (isValidKey(key)) {
            addProperty(key, Boolean.valueOf(value));
        }
        return this;
    }

    public synchronized CustomProperties clear(String key) {
        if (isValidKey(key)) {
            addProperty(key, null);
        }
        return this;
    }

    private void addProperty(String key, Object value) {
        if (this.mProperties.containsKey(key) || this.mProperties.size() < 60) {
            this.mProperties.put(key, value);
        } else {
            AppCenterLog.error("AppCenter", "Custom properties cannot contain more than 60 items");
        }
    }

    private boolean isValidKey(String key) {
        if (key == null || !KEY_PATTERN.matcher(key).matches()) {
            AppCenterLog.error("AppCenter", "Custom property \"" + key + "\" must match \"" + KEY_PATTERN + "\"");
            return false;
        } else if (key.length() > 128) {
            AppCenterLog.error("AppCenter", "Custom property \"" + key + "\" length cannot be longer than 128 characters.");
            return false;
        } else if (this.mProperties.containsKey(key)) {
            AppCenterLog.warn("AppCenter", "Custom property \"" + key + "\" is already set or cleared and will be overridden.");
            return true;
        } else {
            return true;
        }
    }

    private boolean isValidStringValue(String key, String value) {
        if (value == null) {
            AppCenterLog.error("AppCenter", VALUE_NULL_ERROR_MESSAGE);
            return false;
        } else if (value.length() > 128) {
            AppCenterLog.error("AppCenter", "Custom property \"" + key + "\" value length cannot be longer than 128 characters.");
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidNumberValue(String key, Number value) {
        if (value == null) {
            AppCenterLog.error("AppCenter", VALUE_NULL_ERROR_MESSAGE);
            return false;
        }
        double doubleValue = value.doubleValue();
        if (Double.isInfinite(doubleValue) || Double.isNaN(doubleValue)) {
            AppCenterLog.error("AppCenter", "Custom property \"" + key + "\" value cannot be NaN or infinite.");
            return false;
        }
        return true;
    }
}
