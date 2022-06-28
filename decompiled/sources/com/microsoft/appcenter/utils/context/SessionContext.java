package com.microsoft.appcenter.utils.context;

import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
/* loaded from: classes3.dex */
public class SessionContext {
    private static final String STORAGE_KEY = "sessions";
    private static final String STORAGE_KEY_VALUE_SEPARATOR = "/";
    private static final int STORAGE_MAX_SESSIONS = 10;
    private static SessionContext sInstance;
    private final NavigableMap<Long, SessionInfo> mSessions = new TreeMap();
    private final long mAppLaunchTimestamp = System.currentTimeMillis();

    private SessionContext() {
        long appLaunchTimestamp;
        Set<String> storedSessions = SharedPreferencesManager.getStringSet(STORAGE_KEY);
        if (storedSessions != null) {
            for (String session : storedSessions) {
                String[] split = session.split(STORAGE_KEY_VALUE_SEPARATOR, -1);
                try {
                    long time = Long.parseLong(split[0]);
                    String rawSid = split[1];
                    UUID sid = rawSid.isEmpty() ? null : UUID.fromString(rawSid);
                    if (split.length > 2) {
                        appLaunchTimestamp = Long.parseLong(split[2]);
                    } else {
                        appLaunchTimestamp = time;
                    }
                    this.mSessions.put(Long.valueOf(time), new SessionInfo(time, sid, appLaunchTimestamp));
                } catch (RuntimeException e) {
                    AppCenterLog.warn("AppCenter", "Ignore invalid session in store: " + session, e);
                }
            }
        }
        AppCenterLog.debug("AppCenter", "Loaded stored sessions: " + this.mSessions);
        addSession(null);
    }

    public static synchronized SessionContext getInstance() {
        SessionContext sessionContext;
        synchronized (SessionContext.class) {
            if (sInstance == null) {
                sInstance = new SessionContext();
            }
            sessionContext = sInstance;
        }
        return sessionContext;
    }

    public static synchronized void unsetInstance() {
        synchronized (SessionContext.class) {
            sInstance = null;
        }
    }

    public synchronized void addSession(UUID sessionId) {
        long now = System.currentTimeMillis();
        this.mSessions.put(Long.valueOf(now), new SessionInfo(now, sessionId, this.mAppLaunchTimestamp));
        if (this.mSessions.size() > 10) {
            this.mSessions.pollFirstEntry();
        }
        Set<String> sessionStorage = new LinkedHashSet<>();
        for (SessionInfo session : this.mSessions.values()) {
            sessionStorage.add(session.toString());
        }
        SharedPreferencesManager.putStringSet(STORAGE_KEY, sessionStorage);
    }

    public synchronized SessionInfo getSessionAt(long timestamp) {
        Map.Entry<Long, SessionInfo> pastEntry = this.mSessions.floorEntry(Long.valueOf(timestamp));
        if (pastEntry != null) {
            return pastEntry.getValue();
        }
        return null;
    }

    public synchronized void clearSessions() {
        this.mSessions.clear();
        SharedPreferencesManager.remove(STORAGE_KEY);
    }

    /* loaded from: classes3.dex */
    public static class SessionInfo {
        private final long mAppLaunchTimestamp;
        private final UUID mSessionId;
        private final long mTimestamp;

        SessionInfo(long timestamp, UUID sessionId, long appLaunchTimestamp) {
            this.mTimestamp = timestamp;
            this.mSessionId = sessionId;
            this.mAppLaunchTimestamp = appLaunchTimestamp;
        }

        long getTimestamp() {
            return this.mTimestamp;
        }

        public UUID getSessionId() {
            return this.mSessionId;
        }

        public long getAppLaunchTimestamp() {
            return this.mAppLaunchTimestamp;
        }

        public String toString() {
            String rawSession = getTimestamp() + SessionContext.STORAGE_KEY_VALUE_SEPARATOR;
            if (getSessionId() != null) {
                rawSession = rawSession + getSessionId();
            }
            return rawSession + SessionContext.STORAGE_KEY_VALUE_SEPARATOR + getAppLaunchTimestamp();
        }
    }
}
