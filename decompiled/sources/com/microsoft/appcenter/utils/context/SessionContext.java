package com.microsoft.appcenter.utils.context;

import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
/* loaded from: classes.dex */
public class SessionContext {
    private static SessionContext sInstance;
    private final NavigableMap<Long, SessionInfo> mSessions = new TreeMap();
    private final long mAppLaunchTimestamp = System.currentTimeMillis();

    private SessionContext() {
        Set<String> stringSet = SharedPreferencesManager.getStringSet("sessions");
        if (stringSet != null) {
            for (String str : stringSet) {
                String[] split = str.split("/", -1);
                try {
                    long parseLong = Long.parseLong(split[0]);
                    String str2 = split[1];
                    this.mSessions.put(Long.valueOf(parseLong), new SessionInfo(parseLong, str2.isEmpty() ? null : UUID.fromString(str2), split.length > 2 ? Long.parseLong(split[2]) : parseLong));
                } catch (RuntimeException e) {
                    AppCenterLog.warn("AppCenter", "Ignore invalid session in store: " + str, e);
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

    public synchronized void addSession(UUID uuid) {
        long currentTimeMillis = System.currentTimeMillis();
        this.mSessions.put(Long.valueOf(currentTimeMillis), new SessionInfo(currentTimeMillis, uuid, this.mAppLaunchTimestamp));
        if (this.mSessions.size() > 10) {
            this.mSessions.pollFirstEntry();
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (SessionInfo sessionInfo : this.mSessions.values()) {
            linkedHashSet.add(sessionInfo.toString());
        }
        SharedPreferencesManager.putStringSet("sessions", linkedHashSet);
    }

    public synchronized SessionInfo getSessionAt(long j) {
        Map.Entry<Long, SessionInfo> floorEntry = this.mSessions.floorEntry(Long.valueOf(j));
        if (floorEntry != null) {
            return floorEntry.getValue();
        }
        return null;
    }

    /* loaded from: classes.dex */
    public static class SessionInfo {
        private final long mAppLaunchTimestamp;
        private final UUID mSessionId;
        private final long mTimestamp;

        SessionInfo(long j, UUID uuid, long j2) {
            this.mTimestamp = j;
            this.mSessionId = uuid;
            this.mAppLaunchTimestamp = j2;
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
            String str = getTimestamp() + "/";
            if (getSessionId() != null) {
                str = str + getSessionId();
            }
            return str + "/" + getAppLaunchTimestamp();
        }
    }
}
