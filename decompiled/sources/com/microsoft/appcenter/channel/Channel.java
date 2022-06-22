package com.microsoft.appcenter.channel;

import com.microsoft.appcenter.ingestion.Ingestion;
import com.microsoft.appcenter.ingestion.models.Log;
/* loaded from: classes.dex */
public interface Channel {

    /* loaded from: classes.dex */
    public interface GroupListener {
        void onBeforeSending(Log log);

        void onFailure(Log log, Exception exc);

        void onSuccess(Log log);
    }

    /* loaded from: classes.dex */
    public interface Listener {
        void onClear(String str);

        void onGloballyEnabled(boolean z);

        void onGroupAdded(String str, GroupListener groupListener, long j);

        void onGroupRemoved(String str);

        void onPreparedLog(Log log, String str, int i);

        void onPreparingLog(Log log, String str);

        boolean shouldFilter(Log log);
    }

    void addGroup(String str, int i, long j, int i2, Ingestion ingestion, GroupListener groupListener);

    void addListener(Listener listener);

    void clear(String str);

    void enqueue(Log log, String str, int i);

    void removeGroup(String str);

    void removeListener(Listener listener);

    void setAppSecret(String str);

    void setEnabled(boolean z);

    void setLogUrl(String str);

    boolean setMaxStorageSize(long j);

    void shutdown();
}
