package com.microsoft.appcenter.channel;

import android.content.Context;
import android.os.Handler;
import com.microsoft.appcenter.CancellationException;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.http.HttpResponse;
import com.microsoft.appcenter.http.HttpUtils;
import com.microsoft.appcenter.http.ServiceCallback;
import com.microsoft.appcenter.ingestion.AppCenterIngestion;
import com.microsoft.appcenter.ingestion.Ingestion;
import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.LogContainer;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import com.microsoft.appcenter.ingestion.models.one.PartAUtils;
import com.microsoft.appcenter.persistence.DatabasePersistence;
import com.microsoft.appcenter.persistence.Persistence;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.IdHelper;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
/* loaded from: classes.dex */
public class DefaultChannel implements Channel {
    private final Handler mAppCenterHandler;
    private String mAppSecret;
    private final Context mContext;
    private int mCurrentState;
    private Device mDevice;
    private boolean mDiscardLogs;
    private boolean mEnabled;
    private final Map<String, GroupState> mGroupStates;
    private final Ingestion mIngestion;
    private final Set<Ingestion> mIngestions;
    private final UUID mInstallId;
    private final Collection<Channel.Listener> mListeners;
    private final Persistence mPersistence;

    public DefaultChannel(Context context, String str, LogSerializer logSerializer, HttpClient httpClient, Handler handler) {
        this(context, str, buildDefaultPersistence(context, logSerializer), new AppCenterIngestion(httpClient, logSerializer), handler);
    }

    DefaultChannel(Context context, String str, Persistence persistence, Ingestion ingestion, Handler handler) {
        this.mContext = context;
        this.mAppSecret = str;
        this.mInstallId = IdHelper.getInstallId();
        this.mGroupStates = new HashMap();
        this.mListeners = new LinkedHashSet();
        this.mPersistence = persistence;
        this.mIngestion = ingestion;
        HashSet hashSet = new HashSet();
        this.mIngestions = hashSet;
        hashSet.add(ingestion);
        this.mAppCenterHandler = handler;
        this.mEnabled = true;
    }

    private static Persistence buildDefaultPersistence(Context context, LogSerializer logSerializer) {
        DatabasePersistence databasePersistence = new DatabasePersistence(context);
        databasePersistence.setLogSerializer(logSerializer);
        return databasePersistence;
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public boolean setMaxStorageSize(long j) {
        return this.mPersistence.setMaxStorageSize(j);
    }

    private boolean checkStateDidNotChange(GroupState groupState, int i) {
        return i == this.mCurrentState && groupState == this.mGroupStates.get(groupState.mName);
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void setAppSecret(String str) {
        this.mAppSecret = str;
        if (this.mEnabled) {
            for (GroupState groupState : this.mGroupStates.values()) {
                if (groupState.mIngestion == this.mIngestion) {
                    checkPendingLogs(groupState);
                }
            }
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void addGroup(String str, int i, long j, int i2, Ingestion ingestion, Channel.GroupListener groupListener) {
        AppCenterLog.debug("AppCenter", "addGroup(" + str + ")");
        Ingestion ingestion2 = ingestion == null ? this.mIngestion : ingestion;
        this.mIngestions.add(ingestion2);
        GroupState groupState = new GroupState(str, i, j, i2, ingestion2, groupListener);
        this.mGroupStates.put(str, groupState);
        groupState.mPendingLogCount = this.mPersistence.countLogs(str);
        if (this.mAppSecret != null || this.mIngestion != ingestion2) {
            checkPendingLogs(groupState);
        }
        for (Channel.Listener listener : this.mListeners) {
            listener.onGroupAdded(str, groupListener, j);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void removeGroup(String str) {
        AppCenterLog.debug("AppCenter", "removeGroup(" + str + ")");
        GroupState remove = this.mGroupStates.remove(str);
        if (remove != null) {
            cancelTimer(remove);
        }
        for (Channel.Listener listener : this.mListeners) {
            listener.onGroupRemoved(str);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void setEnabled(boolean z) {
        if (this.mEnabled == z) {
            return;
        }
        if (z) {
            this.mEnabled = true;
            this.mDiscardLogs = false;
            this.mCurrentState++;
            for (Ingestion ingestion : this.mIngestions) {
                ingestion.reopen();
            }
            for (GroupState groupState : this.mGroupStates.values()) {
                checkPendingLogs(groupState);
            }
        } else {
            suspend(true, new CancellationException());
        }
        for (Channel.Listener listener : this.mListeners) {
            listener.onGloballyEnabled(z);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void setLogUrl(String str) {
        this.mIngestion.setLogUrl(str);
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void clear(String str) {
        if (!this.mGroupStates.containsKey(str)) {
            return;
        }
        AppCenterLog.debug("AppCenter", "clear(" + str + ")");
        this.mPersistence.deleteLogs(str);
        for (Channel.Listener listener : this.mListeners) {
            listener.onClear(str);
        }
    }

    private void suspend(boolean z, Exception exc) {
        Channel.GroupListener groupListener;
        this.mEnabled = false;
        this.mDiscardLogs = z;
        this.mCurrentState++;
        for (GroupState groupState : this.mGroupStates.values()) {
            cancelTimer(groupState);
            Iterator<Map.Entry<String, List<Log>>> it = groupState.mSendingBatches.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, List<Log>> next = it.next();
                it.remove();
                if (z && (groupListener = groupState.mListener) != null) {
                    for (Log log : next.getValue()) {
                        groupListener.onFailure(log, exc);
                    }
                }
            }
        }
        for (Ingestion ingestion : this.mIngestions) {
            try {
                ingestion.close();
            } catch (IOException e) {
                AppCenterLog.error("AppCenter", "Failed to close ingestion: " + ingestion, e);
            }
        }
        if (z) {
            for (GroupState groupState2 : this.mGroupStates.values()) {
                deleteLogsOnSuspended(groupState2);
            }
            return;
        }
        this.mPersistence.clearPendingLogState();
    }

    private void deleteLogsOnSuspended(GroupState groupState) {
        ArrayList<Log> arrayList = new ArrayList();
        this.mPersistence.getLogs(groupState.mName, Collections.emptyList(), 100, arrayList);
        if (arrayList.size() > 0 && groupState.mListener != null) {
            for (Log log : arrayList) {
                groupState.mListener.onBeforeSending(log);
                groupState.mListener.onFailure(log, new CancellationException());
            }
        }
        if (arrayList.size() >= 100 && groupState.mListener != null) {
            deleteLogsOnSuspended(groupState);
        } else {
            this.mPersistence.deleteLogs(groupState.mName);
        }
    }

    void cancelTimer(GroupState groupState) {
        if (groupState.mScheduled) {
            groupState.mScheduled = false;
            this.mAppCenterHandler.removeCallbacks(groupState.mRunnable);
            SharedPreferencesManager.remove("startTimerPrefix." + groupState.mName);
        }
    }

    public void triggerIngestion(GroupState groupState) {
        if (!this.mEnabled) {
            return;
        }
        int i = groupState.mPendingLogCount;
        int min = Math.min(i, groupState.mMaxLogsPerBatch);
        AppCenterLog.debug("AppCenter", "triggerIngestion(" + groupState.mName + ") pendingLogCount=" + i);
        cancelTimer(groupState);
        if (groupState.mSendingBatches.size() == groupState.mMaxParallelBatches) {
            AppCenterLog.debug("AppCenter", "Already sending " + groupState.mMaxParallelBatches + " batches of analytics data to the server.");
            return;
        }
        ArrayList<Log> arrayList = new ArrayList(min);
        String logs = this.mPersistence.getLogs(groupState.mName, groupState.mPausedTargetKeys, min, arrayList);
        groupState.mPendingLogCount -= min;
        if (logs == null) {
            return;
        }
        AppCenterLog.debug("AppCenter", "ingestLogs(" + groupState.mName + "," + logs + ") pendingLogCount=" + groupState.mPendingLogCount);
        if (groupState.mListener != null) {
            for (Log log : arrayList) {
                groupState.mListener.onBeforeSending(log);
            }
        }
        groupState.mSendingBatches.put(logs, arrayList);
        sendLogs(groupState, this.mCurrentState, arrayList, logs);
    }

    private void sendLogs(final GroupState groupState, final int i, List<Log> list, final String str) {
        LogContainer logContainer = new LogContainer();
        logContainer.setLogs(list);
        groupState.mIngestion.sendAsync(this.mAppSecret, this.mInstallId, logContainer, new ServiceCallback() { // from class: com.microsoft.appcenter.channel.DefaultChannel.1
            @Override // com.microsoft.appcenter.http.ServiceCallback
            public void onCallSucceeded(HttpResponse httpResponse) {
                DefaultChannel.this.mAppCenterHandler.post(new Runnable() { // from class: com.microsoft.appcenter.channel.DefaultChannel.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                        DefaultChannel.this.handleSendingSuccess(groupState, str);
                    }
                });
            }

            @Override // com.microsoft.appcenter.http.ServiceCallback
            public void onCallFailed(final Exception exc) {
                DefaultChannel.this.mAppCenterHandler.post(new Runnable() { // from class: com.microsoft.appcenter.channel.DefaultChannel.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                        DefaultChannel.this.handleSendingFailure(groupState, str, exc);
                    }
                });
            }
        });
        this.mAppCenterHandler.post(new Runnable() { // from class: com.microsoft.appcenter.channel.DefaultChannel.2
            @Override // java.lang.Runnable
            public void run() {
                DefaultChannel.this.checkPendingLogsAfterPost(groupState, i);
            }
        });
    }

    public void checkPendingLogsAfterPost(GroupState groupState, int i) {
        if (checkStateDidNotChange(groupState, i)) {
            checkPendingLogs(groupState);
        }
    }

    public void handleSendingSuccess(GroupState groupState, String str) {
        List<Log> remove = groupState.mSendingBatches.remove(str);
        if (remove != null) {
            this.mPersistence.deleteLogs(groupState.mName, str);
            Channel.GroupListener groupListener = groupState.mListener;
            if (groupListener != null) {
                for (Log log : remove) {
                    groupListener.onSuccess(log);
                }
            }
            checkPendingLogs(groupState);
        }
    }

    public void handleSendingFailure(GroupState groupState, String str, Exception exc) {
        String str2 = groupState.mName;
        List<Log> remove = groupState.mSendingBatches.remove(str);
        if (remove != null) {
            AppCenterLog.error("AppCenter", "Sending logs groupName=" + str2 + " id=" + str + " failed", exc);
            boolean isRecoverableError = HttpUtils.isRecoverableError(exc);
            if (isRecoverableError) {
                groupState.mPendingLogCount += remove.size();
            } else {
                Channel.GroupListener groupListener = groupState.mListener;
                if (groupListener != null) {
                    for (Log log : remove) {
                        groupListener.onFailure(log, exc);
                    }
                }
            }
            suspend(!isRecoverableError, exc);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void enqueue(Log log, String str, int i) {
        boolean z;
        GroupState groupState = this.mGroupStates.get(str);
        if (groupState == null) {
            AppCenterLog.error("AppCenter", "Invalid group name:" + str);
        } else if (this.mDiscardLogs) {
            AppCenterLog.warn("AppCenter", "Channel is disabled, the log is discarded.");
            Channel.GroupListener groupListener = groupState.mListener;
            if (groupListener == null) {
                return;
            }
            groupListener.onBeforeSending(log);
            groupState.mListener.onFailure(log, new CancellationException());
        } else {
            for (Channel.Listener listener : this.mListeners) {
                listener.onPreparingLog(log, str);
            }
            if (log.getDevice() == null) {
                if (this.mDevice == null) {
                    try {
                        this.mDevice = DeviceInfoHelper.getDeviceInfo(this.mContext);
                    } catch (DeviceInfoHelper.DeviceInfoException e) {
                        AppCenterLog.error("AppCenter", "Device log cannot be generated", e);
                        return;
                    }
                }
                log.setDevice(this.mDevice);
            }
            if (log.getTimestamp() == null) {
                log.setTimestamp(new Date());
            }
            for (Channel.Listener listener2 : this.mListeners) {
                listener2.onPreparedLog(log, str, i);
            }
            loop2: while (true) {
                z = false;
                for (Channel.Listener listener3 : this.mListeners) {
                    if (z || listener3.shouldFilter(log)) {
                        z = true;
                    }
                }
            }
            if (z) {
                AppCenterLog.debug("AppCenter", "Log of type '" + log.getType() + "' was filtered out by listener(s)");
            } else if (this.mAppSecret == null && groupState.mIngestion == this.mIngestion) {
                AppCenterLog.debug("AppCenter", "Log of type '" + log.getType() + "' was not filtered out by listener(s) but no app secret was provided. Not persisting/sending the log.");
            } else {
                try {
                    this.mPersistence.putLog(log, str, i);
                    Iterator<String> it = log.getTransmissionTargetTokens().iterator();
                    String targetKey = it.hasNext() ? PartAUtils.getTargetKey(it.next()) : null;
                    if (groupState.mPausedTargetKeys.contains(targetKey)) {
                        AppCenterLog.debug("AppCenter", "Transmission target ikey=" + targetKey + " is paused.");
                        return;
                    }
                    groupState.mPendingLogCount++;
                    AppCenterLog.debug("AppCenter", "enqueue(" + groupState.mName + ") pendingLogCount=" + groupState.mPendingLogCount);
                    if (this.mEnabled) {
                        checkPendingLogs(groupState);
                    } else {
                        AppCenterLog.debug("AppCenter", "Channel is temporarily disabled, log was saved to disk.");
                    }
                } catch (Persistence.PersistenceException e2) {
                    AppCenterLog.error("AppCenter", "Error persisting log", e2);
                    Channel.GroupListener groupListener2 = groupState.mListener;
                    if (groupListener2 == null) {
                        return;
                    }
                    groupListener2.onBeforeSending(log);
                    groupState.mListener.onFailure(log, e2);
                }
            }
        }
    }

    void checkPendingLogs(GroupState groupState) {
        AppCenterLog.debug("AppCenter", String.format("checkPendingLogs(%s) pendingLogCount=%s batchTimeInterval=%s", groupState.mName, Integer.valueOf(groupState.mPendingLogCount), Long.valueOf(groupState.mBatchTimeInterval)));
        Long resolveTriggerInterval = resolveTriggerInterval(groupState);
        if (resolveTriggerInterval == null || groupState.mPaused) {
            return;
        }
        if (resolveTriggerInterval.longValue() == 0) {
            triggerIngestion(groupState);
        } else if (groupState.mScheduled) {
        } else {
            groupState.mScheduled = true;
            this.mAppCenterHandler.postDelayed(groupState.mRunnable, resolveTriggerInterval.longValue());
        }
    }

    private Long resolveTriggerInterval(GroupState groupState) {
        if (groupState.mBatchTimeInterval > 3000) {
            return resolveCustomTriggerInterval(groupState);
        }
        return resolveDefaultTriggerInterval(groupState);
    }

    private Long resolveCustomTriggerInterval(GroupState groupState) {
        long currentTimeMillis = System.currentTimeMillis();
        long j = SharedPreferencesManager.getLong("startTimerPrefix." + groupState.mName);
        if (groupState.mPendingLogCount <= 0) {
            if (j + groupState.mBatchTimeInterval >= currentTimeMillis) {
                return null;
            }
            SharedPreferencesManager.remove("startTimerPrefix." + groupState.mName);
            AppCenterLog.debug("AppCenter", "The timer for " + groupState.mName + " channel finished.");
            return null;
        } else if (j == 0 || j > currentTimeMillis) {
            SharedPreferencesManager.putLong("startTimerPrefix." + groupState.mName, currentTimeMillis);
            AppCenterLog.debug("AppCenter", "The timer value for " + groupState.mName + " has been saved.");
            return Long.valueOf(groupState.mBatchTimeInterval);
        } else {
            return Long.valueOf(Math.max(groupState.mBatchTimeInterval - (currentTimeMillis - j), 0L));
        }
    }

    private Long resolveDefaultTriggerInterval(GroupState groupState) {
        int i = groupState.mPendingLogCount;
        if (i >= groupState.mMaxLogsPerBatch) {
            return 0L;
        }
        if (i <= 0) {
            return null;
        }
        return Long.valueOf(groupState.mBatchTimeInterval);
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void addListener(Channel.Listener listener) {
        this.mListeners.add(listener);
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void removeListener(Channel.Listener listener) {
        this.mListeners.remove(listener);
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void shutdown() {
        suspend(false, new CancellationException());
    }

    /* loaded from: classes.dex */
    public class GroupState {
        final long mBatchTimeInterval;
        final Ingestion mIngestion;
        final Channel.GroupListener mListener;
        final int mMaxLogsPerBatch;
        final int mMaxParallelBatches;
        final String mName;
        boolean mPaused;
        int mPendingLogCount;
        boolean mScheduled;
        final Map<String, List<Log>> mSendingBatches = new HashMap();
        final Collection<String> mPausedTargetKeys = new HashSet();
        final Runnable mRunnable = new Runnable() { // from class: com.microsoft.appcenter.channel.DefaultChannel.GroupState.1
            @Override // java.lang.Runnable
            public void run() {
                GroupState groupState = GroupState.this;
                groupState.mScheduled = false;
                DefaultChannel.this.triggerIngestion(groupState);
            }
        };

        GroupState(String str, int i, long j, int i2, Ingestion ingestion, Channel.GroupListener groupListener) {
            DefaultChannel.this = r1;
            this.mName = str;
            this.mMaxLogsPerBatch = i;
            this.mBatchTimeInterval = j;
            this.mMaxParallelBatches = i2;
            this.mIngestion = ingestion;
            this.mListener = groupListener;
        }
    }
}
