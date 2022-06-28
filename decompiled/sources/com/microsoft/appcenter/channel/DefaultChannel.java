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
/* loaded from: classes3.dex */
public class DefaultChannel implements Channel {
    static final int CLEAR_BATCH_SIZE = 100;
    private static final long MINIMUM_TRANSMISSION_INTERVAL = 3000;
    static final String START_TIMER_PREFIX = "startTimerPrefix.";
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

    public DefaultChannel(Context context, String appSecret, LogSerializer logSerializer, HttpClient httpClient, Handler appCenterHandler) {
        this(context, appSecret, buildDefaultPersistence(context, logSerializer), new AppCenterIngestion(httpClient, logSerializer), appCenterHandler);
    }

    DefaultChannel(Context context, String appSecret, Persistence persistence, Ingestion ingestion, Handler appCenterHandler) {
        this.mContext = context;
        this.mAppSecret = appSecret;
        this.mInstallId = IdHelper.getInstallId();
        this.mGroupStates = new HashMap();
        this.mListeners = new LinkedHashSet();
        this.mPersistence = persistence;
        this.mIngestion = ingestion;
        HashSet hashSet = new HashSet();
        this.mIngestions = hashSet;
        hashSet.add(ingestion);
        this.mAppCenterHandler = appCenterHandler;
        this.mEnabled = true;
    }

    private static Persistence buildDefaultPersistence(Context context, LogSerializer logSerializer) {
        Persistence persistence = new DatabasePersistence(context);
        persistence.setLogSerializer(logSerializer);
        return persistence;
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public boolean setMaxStorageSize(long maxStorageSizeInBytes) {
        return this.mPersistence.setMaxStorageSize(maxStorageSizeInBytes);
    }

    private boolean checkStateDidNotChange(GroupState groupState, int stateSnapshot) {
        return stateSnapshot == this.mCurrentState && groupState == this.mGroupStates.get(groupState.mName);
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void setAppSecret(String appSecret) {
        this.mAppSecret = appSecret;
        if (this.mEnabled) {
            for (GroupState groupState : this.mGroupStates.values()) {
                if (groupState.mIngestion == this.mIngestion) {
                    checkPendingLogs(groupState);
                }
            }
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void addGroup(String groupName, int maxLogsPerBatch, long batchTimeInterval, int maxParallelBatches, Ingestion ingestion, Channel.GroupListener groupListener) {
        AppCenterLog.debug("AppCenter", "addGroup(" + groupName + ")");
        Ingestion ingestion2 = ingestion == null ? this.mIngestion : ingestion;
        this.mIngestions.add(ingestion2);
        GroupState groupState = new GroupState(groupName, maxLogsPerBatch, batchTimeInterval, maxParallelBatches, ingestion2, groupListener);
        this.mGroupStates.put(groupName, groupState);
        groupState.mPendingLogCount = this.mPersistence.countLogs(groupName);
        if (this.mAppSecret != null || this.mIngestion != ingestion2) {
            checkPendingLogs(groupState);
        }
        for (Channel.Listener listener : this.mListeners) {
            listener.onGroupAdded(groupName, groupListener, batchTimeInterval);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void removeGroup(String groupName) {
        AppCenterLog.debug("AppCenter", "removeGroup(" + groupName + ")");
        GroupState groupState = this.mGroupStates.remove(groupName);
        if (groupState != null) {
            cancelTimer(groupState);
        }
        for (Channel.Listener listener : this.mListeners) {
            listener.onGroupRemoved(groupName);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void pauseGroup(String groupName, String targetToken) {
        GroupState groupState = this.mGroupStates.get(groupName);
        if (groupState != null) {
            if (targetToken != null) {
                String targetKey = PartAUtils.getTargetKey(targetToken);
                if (groupState.mPausedTargetKeys.add(targetKey)) {
                    AppCenterLog.debug("AppCenter", "pauseGroup(" + groupName + ", " + targetKey + ")");
                }
            } else if (!groupState.mPaused) {
                AppCenterLog.debug("AppCenter", "pauseGroup(" + groupName + ")");
                groupState.mPaused = true;
                cancelTimer(groupState);
            }
            for (Channel.Listener listener : this.mListeners) {
                listener.onPaused(groupName, targetToken);
            }
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void resumeGroup(String groupName, String targetToken) {
        GroupState groupState = this.mGroupStates.get(groupName);
        if (groupState != null) {
            if (targetToken != null) {
                String targetKey = PartAUtils.getTargetKey(targetToken);
                if (groupState.mPausedTargetKeys.remove(targetKey)) {
                    AppCenterLog.debug("AppCenter", "resumeGroup(" + groupName + ", " + targetKey + ")");
                    groupState.mPendingLogCount = this.mPersistence.countLogs(groupName);
                    checkPendingLogs(groupState);
                }
            } else if (groupState.mPaused) {
                AppCenterLog.debug("AppCenter", "resumeGroup(" + groupName + ")");
                groupState.mPaused = false;
                checkPendingLogs(groupState);
            }
            for (Channel.Listener listener : this.mListeners) {
                listener.onResumed(groupName, targetToken);
            }
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public boolean isEnabled() {
        return this.mEnabled;
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void setEnabled(boolean enabled) {
        if (this.mEnabled == enabled) {
            return;
        }
        if (!enabled) {
            suspend(true, new CancellationException());
        } else {
            this.mEnabled = true;
            this.mDiscardLogs = false;
            this.mCurrentState++;
            for (Ingestion ingestion : this.mIngestions) {
                ingestion.reopen();
            }
            for (GroupState groupState : this.mGroupStates.values()) {
                checkPendingLogs(groupState);
            }
        }
        for (Channel.Listener listener : this.mListeners) {
            listener.onGloballyEnabled(enabled);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void setLogUrl(String logUrl) {
        this.mIngestion.setLogUrl(logUrl);
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void clear(String groupName) {
        if (!this.mGroupStates.containsKey(groupName)) {
            return;
        }
        AppCenterLog.debug("AppCenter", "clear(" + groupName + ")");
        this.mPersistence.deleteLogs(groupName);
        for (Channel.Listener listener : this.mListeners) {
            listener.onClear(groupName);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void invalidateDeviceCache() {
        this.mDevice = null;
    }

    private void suspend(boolean deleteLogs, Exception exception) {
        Channel.GroupListener groupListener;
        this.mEnabled = false;
        this.mDiscardLogs = deleteLogs;
        this.mCurrentState++;
        for (GroupState groupState : this.mGroupStates.values()) {
            cancelTimer(groupState);
            Iterator<Map.Entry<String, List<Log>>> iterator = groupState.mSendingBatches.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<Log>> entry = iterator.next();
                iterator.remove();
                if (deleteLogs && (groupListener = groupState.mListener) != null) {
                    for (Log log : entry.getValue()) {
                        groupListener.onFailure(log, exception);
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
        if (deleteLogs) {
            for (GroupState groupState2 : this.mGroupStates.values()) {
                deleteLogsOnSuspended(groupState2);
            }
            return;
        }
        this.mPersistence.clearPendingLogState();
    }

    private void deleteLogsOnSuspended(GroupState groupState) {
        List<Log> logs = new ArrayList<>();
        this.mPersistence.getLogs(groupState.mName, Collections.emptyList(), 100, logs);
        if (logs.size() > 0 && groupState.mListener != null) {
            for (Log log : logs) {
                groupState.mListener.onBeforeSending(log);
                groupState.mListener.onFailure(log, new CancellationException());
            }
        }
        if (logs.size() >= 100 && groupState.mListener != null) {
            deleteLogsOnSuspended(groupState);
        } else {
            this.mPersistence.deleteLogs(groupState.mName);
        }
    }

    void cancelTimer(GroupState groupState) {
        if (groupState.mScheduled) {
            groupState.mScheduled = false;
            this.mAppCenterHandler.removeCallbacks(groupState.mRunnable);
            SharedPreferencesManager.remove(START_TIMER_PREFIX + groupState.mName);
        }
    }

    public void triggerIngestion(GroupState groupState) {
        if (!this.mEnabled) {
            return;
        }
        int pendingLogCount = groupState.mPendingLogCount;
        int maxFetch = Math.min(pendingLogCount, groupState.mMaxLogsPerBatch);
        AppCenterLog.debug("AppCenter", "triggerIngestion(" + groupState.mName + ") pendingLogCount=" + pendingLogCount);
        cancelTimer(groupState);
        if (groupState.mSendingBatches.size() == groupState.mMaxParallelBatches) {
            AppCenterLog.debug("AppCenter", "Already sending " + groupState.mMaxParallelBatches + " batches of analytics data to the server.");
            return;
        }
        List<Log> batch = new ArrayList<>(maxFetch);
        String batchId = this.mPersistence.getLogs(groupState.mName, groupState.mPausedTargetKeys, maxFetch, batch);
        groupState.mPendingLogCount -= maxFetch;
        if (batchId == null) {
            return;
        }
        AppCenterLog.debug("AppCenter", "ingestLogs(" + groupState.mName + "," + batchId + ") pendingLogCount=" + groupState.mPendingLogCount);
        if (groupState.mListener != null) {
            for (Log log : batch) {
                groupState.mListener.onBeforeSending(log);
            }
        }
        groupState.mSendingBatches.put(batchId, batch);
        sendLogs(groupState, this.mCurrentState, batch, batchId);
    }

    private void sendLogs(final GroupState groupState, final int currentState, List<Log> batch, final String batchId) {
        LogContainer logContainer = new LogContainer();
        logContainer.setLogs(batch);
        groupState.mIngestion.sendAsync(this.mAppSecret, this.mInstallId, logContainer, new ServiceCallback() { // from class: com.microsoft.appcenter.channel.DefaultChannel.1
            @Override // com.microsoft.appcenter.http.ServiceCallback
            public void onCallSucceeded(HttpResponse httpResponse) {
                DefaultChannel.this.mAppCenterHandler.post(new Runnable() { // from class: com.microsoft.appcenter.channel.DefaultChannel.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DefaultChannel.this.handleSendingSuccess(groupState, batchId);
                    }
                });
            }

            @Override // com.microsoft.appcenter.http.ServiceCallback
            public void onCallFailed(final Exception e) {
                DefaultChannel.this.mAppCenterHandler.post(new Runnable() { // from class: com.microsoft.appcenter.channel.DefaultChannel.1.2
                    @Override // java.lang.Runnable
                    public void run() {
                        DefaultChannel.this.handleSendingFailure(groupState, batchId, e);
                    }
                });
            }
        });
        this.mAppCenterHandler.post(new Runnable() { // from class: com.microsoft.appcenter.channel.DefaultChannel.2
            @Override // java.lang.Runnable
            public void run() {
                DefaultChannel.this.checkPendingLogsAfterPost(groupState, currentState);
            }
        });
    }

    public void checkPendingLogsAfterPost(GroupState groupState, int currentState) {
        if (checkStateDidNotChange(groupState, currentState)) {
            checkPendingLogs(groupState);
        }
    }

    public void handleSendingSuccess(GroupState groupState, String batchId) {
        List<Log> removedLogsForBatchId = groupState.mSendingBatches.remove(batchId);
        if (removedLogsForBatchId != null) {
            this.mPersistence.deleteLogs(groupState.mName, batchId);
            Channel.GroupListener groupListener = groupState.mListener;
            if (groupListener != null) {
                for (Log log : removedLogsForBatchId) {
                    groupListener.onSuccess(log);
                }
            }
            checkPendingLogs(groupState);
        }
    }

    public void handleSendingFailure(GroupState groupState, String batchId, Exception e) {
        String groupName = groupState.mName;
        List<Log> removedLogsForBatchId = groupState.mSendingBatches.remove(batchId);
        if (removedLogsForBatchId != null) {
            AppCenterLog.error("AppCenter", "Sending logs groupName=" + groupName + " id=" + batchId + " failed", e);
            boolean recoverableError = HttpUtils.isRecoverableError(e);
            if (recoverableError) {
                groupState.mPendingLogCount += removedLogsForBatchId.size();
            } else {
                Channel.GroupListener groupListener = groupState.mListener;
                if (groupListener != null) {
                    for (Log log : removedLogsForBatchId) {
                        groupListener.onFailure(log, e);
                    }
                }
            }
            suspend(!recoverableError, e);
        }
    }

    @Override // com.microsoft.appcenter.channel.Channel
    public void enqueue(Log log, String groupName, int flags) {
        GroupState groupState = this.mGroupStates.get(groupName);
        if (groupState == null) {
            AppCenterLog.error("AppCenter", "Invalid group name:" + groupName);
        } else if (this.mDiscardLogs) {
            AppCenterLog.warn("AppCenter", "Channel is disabled, the log is discarded.");
            if (groupState.mListener != null) {
                groupState.mListener.onBeforeSending(log);
                groupState.mListener.onFailure(log, new CancellationException());
            }
        } else {
            for (Channel.Listener listener : this.mListeners) {
                listener.onPreparingLog(log, groupName);
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
                listener2.onPreparedLog(log, groupName, flags);
            }
            boolean filteredOut = false;
            Iterator<Channel.Listener> it = this.mListeners.iterator();
            while (true) {
                boolean z = true;
                if (!it.hasNext()) {
                    break;
                }
                Channel.Listener listener3 = it.next();
                if (!filteredOut && !listener3.shouldFilter(log)) {
                    z = false;
                }
                filteredOut = z;
            }
            if (filteredOut) {
                AppCenterLog.debug("AppCenter", "Log of type '" + log.getType() + "' was filtered out by listener(s)");
            } else if (this.mAppSecret == null && groupState.mIngestion == this.mIngestion) {
                AppCenterLog.debug("AppCenter", "Log of type '" + log.getType() + "' was not filtered out by listener(s) but no app secret was provided. Not persisting/sending the log.");
            } else {
                try {
                    this.mPersistence.putLog(log, groupName, flags);
                    Iterator<String> targetKeys = log.getTransmissionTargetTokens().iterator();
                    String targetKey = targetKeys.hasNext() ? PartAUtils.getTargetKey(targetKeys.next()) : null;
                    if (groupState.mPausedTargetKeys.contains(targetKey)) {
                        AppCenterLog.debug("AppCenter", "Transmission target ikey=" + targetKey + " is paused.");
                        return;
                    }
                    groupState.mPendingLogCount++;
                    AppCenterLog.debug("AppCenter", "enqueue(" + groupState.mName + ") pendingLogCount=" + groupState.mPendingLogCount);
                    if (!this.mEnabled) {
                        AppCenterLog.debug("AppCenter", "Channel is temporarily disabled, log was saved to disk.");
                    } else {
                        checkPendingLogs(groupState);
                    }
                } catch (Persistence.PersistenceException e2) {
                    AppCenterLog.error("AppCenter", "Error persisting log", e2);
                    if (groupState.mListener != null) {
                        groupState.mListener.onBeforeSending(log);
                        groupState.mListener.onFailure(log, e2);
                    }
                }
            }
        }
    }

    void checkPendingLogs(GroupState groupState) {
        AppCenterLog.debug("AppCenter", String.format("checkPendingLogs(%s) pendingLogCount=%s batchTimeInterval=%s", groupState.mName, Integer.valueOf(groupState.mPendingLogCount), Long.valueOf(groupState.mBatchTimeInterval)));
        Long batchTimeInterval = resolveTriggerInterval(groupState);
        if (batchTimeInterval == null || groupState.mPaused) {
            return;
        }
        if (batchTimeInterval.longValue() == 0) {
            triggerIngestion(groupState);
        } else if (!groupState.mScheduled) {
            groupState.mScheduled = true;
            this.mAppCenterHandler.postDelayed(groupState.mRunnable, batchTimeInterval.longValue());
        }
    }

    private Long resolveTriggerInterval(GroupState groupState) {
        if (groupState.mBatchTimeInterval > MINIMUM_TRANSMISSION_INTERVAL) {
            return resolveCustomTriggerInterval(groupState);
        }
        return resolveDefaultTriggerInterval(groupState);
    }

    private Long resolveCustomTriggerInterval(GroupState groupState) {
        long now = System.currentTimeMillis();
        long startTimer = SharedPreferencesManager.getLong(START_TIMER_PREFIX + groupState.mName);
        if (groupState.mPendingLogCount > 0) {
            if (startTimer != 0 && startTimer <= now) {
                return Long.valueOf(Math.max(groupState.mBatchTimeInterval - (now - startTimer), 0L));
            }
            SharedPreferencesManager.putLong(START_TIMER_PREFIX + groupState.mName, now);
            AppCenterLog.debug("AppCenter", "The timer value for " + groupState.mName + " has been saved.");
            return Long.valueOf(groupState.mBatchTimeInterval);
        } else if (groupState.mBatchTimeInterval + startTimer < now) {
            SharedPreferencesManager.remove(START_TIMER_PREFIX + groupState.mName);
            AppCenterLog.debug("AppCenter", "The timer for " + groupState.mName + " channel finished.");
            return null;
        } else {
            return null;
        }
    }

    private Long resolveDefaultTriggerInterval(GroupState groupState) {
        if (groupState.mPendingLogCount >= groupState.mMaxLogsPerBatch) {
            return 0L;
        }
        if (groupState.mPendingLogCount <= 0) {
            return null;
        }
        return Long.valueOf(groupState.mBatchTimeInterval);
    }

    GroupState getGroupState(String groupName) {
        return this.mGroupStates.get(groupName);
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

    /* loaded from: classes3.dex */
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
                GroupState.this.mScheduled = false;
                DefaultChannel.this.triggerIngestion(GroupState.this);
            }
        };

        GroupState(String name, int maxLogsPerBatch, long batchTimeInterval, int maxParallelBatches, Ingestion ingestion, Channel.GroupListener listener) {
            DefaultChannel.this = this$0;
            this.mName = name;
            this.mMaxLogsPerBatch = maxLogsPerBatch;
            this.mBatchTimeInterval = batchTimeInterval;
            this.mMaxParallelBatches = maxParallelBatches;
            this.mIngestion = ingestion;
            this.mListener = listener;
        }
    }
}
