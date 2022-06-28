package com.microsoft.appcenter.utils.context;

import android.text.TextUtils;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.utils.AppCenterLog;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.Set;
/* loaded from: classes3.dex */
public class UserIdContext {
    private static final String CUSTOM_PREFIX = "c";
    public static final int USER_ID_APP_CENTER_MAX_LENGTH = 256;
    private static UserIdContext sInstance;
    private final Set<Listener> mListeners = Collections.newSetFromMap(new ConcurrentHashMap());
    private String mUserId;

    /* loaded from: classes3.dex */
    public interface Listener {
        void onNewUserId(String str);
    }

    public static synchronized UserIdContext getInstance() {
        UserIdContext userIdContext;
        synchronized (UserIdContext.class) {
            if (sInstance == null) {
                sInstance = new UserIdContext();
            }
            userIdContext = sInstance;
        }
        return userIdContext;
    }

    public static synchronized void unsetInstance() {
        synchronized (UserIdContext.class) {
            sInstance = null;
        }
    }

    public static boolean checkUserIdValidForOneCollector(String userId) {
        if (userId == null) {
            return true;
        }
        if (userId.isEmpty()) {
            AppCenterLog.error("AppCenter", "userId must not be empty.");
            return false;
        }
        int prefixIndex = userId.indexOf(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        if (prefixIndex >= 0) {
            String prefix = userId.substring(0, prefixIndex);
            if (!prefix.equals("c")) {
                AppCenterLog.error("AppCenter", String.format("userId prefix must be '%s%s', '%s%s' is not supported.", "c", Constants.COMMON_SCHEMA_PREFIX_SEPARATOR, prefix, Constants.COMMON_SCHEMA_PREFIX_SEPARATOR));
                return false;
            } else if (prefixIndex == userId.length() - 1) {
                AppCenterLog.error("AppCenter", "userId must not be empty.");
                return false;
            }
        }
        return true;
    }

    public static boolean checkUserIdValidForAppCenter(String userId) {
        if (userId != null && userId.length() > 256) {
            AppCenterLog.error("AppCenter", "userId is limited to 256 characters.");
            return false;
        }
        return true;
    }

    public static String getPrefixedUserId(String userId) {
        if (userId != null && !userId.contains(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR)) {
            return "c:" + userId;
        }
        return userId;
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    public synchronized String getUserId() {
        return this.mUserId;
    }

    public void setUserId(String userId) {
        if (!updateUserId(userId)) {
            return;
        }
        for (Listener listener : this.mListeners) {
            listener.onNewUserId(this.mUserId);
        }
    }

    private synchronized boolean updateUserId(String userId) {
        if (TextUtils.equals(this.mUserId, userId)) {
            return false;
        }
        this.mUserId = userId;
        return true;
    }
}
