package com.microsoft.appcenter.persistence;

import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import java.io.Closeable;
import java.util.Collection;
import java.util.List;
/* loaded from: classes3.dex */
public abstract class Persistence implements Closeable {
    private LogSerializer mLogSerializer;

    public abstract void clearPendingLogState();

    public abstract int countLogs(String str);

    public abstract void deleteLogs(String str);

    public abstract void deleteLogs(String str, String str2);

    public abstract String getLogs(String str, Collection<String> collection, int i, List<Log> list);

    public abstract long putLog(Log log, String str, int i) throws PersistenceException;

    public abstract boolean setMaxStorageSize(long j);

    public LogSerializer getLogSerializer() {
        LogSerializer logSerializer = this.mLogSerializer;
        if (logSerializer == null) {
            throw new IllegalStateException("logSerializer not configured");
        }
        return logSerializer;
    }

    public void setLogSerializer(LogSerializer logSerializer) {
        this.mLogSerializer = logSerializer;
    }

    /* loaded from: classes3.dex */
    public static class PersistenceException extends Exception {
        public PersistenceException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public PersistenceException(String detailMessage) {
            super(detailMessage);
        }
    }
}
