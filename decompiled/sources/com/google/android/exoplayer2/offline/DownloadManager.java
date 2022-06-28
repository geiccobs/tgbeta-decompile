package com.google.android.exoplayer2.offline;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.offline.Downloader;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.RequirementsWatcher;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes3.dex */
public final class DownloadManager {
    public static final int DEFAULT_MAX_PARALLEL_DOWNLOADS = 3;
    public static final int DEFAULT_MIN_RETRY_COUNT = 5;
    public static final Requirements DEFAULT_REQUIREMENTS = new Requirements(1);
    private static final int MSG_ADD_DOWNLOAD = 6;
    private static final int MSG_CONTENT_LENGTH_CHANGED = 10;
    private static final int MSG_DOWNLOAD_UPDATE = 2;
    private static final int MSG_INITIALIZE = 0;
    private static final int MSG_INITIALIZED = 0;
    private static final int MSG_PROCESSED = 1;
    private static final int MSG_RELEASE = 12;
    private static final int MSG_REMOVE_ALL_DOWNLOADS = 8;
    private static final int MSG_REMOVE_DOWNLOAD = 7;
    private static final int MSG_SET_DOWNLOADS_PAUSED = 1;
    private static final int MSG_SET_MAX_PARALLEL_DOWNLOADS = 4;
    private static final int MSG_SET_MIN_RETRY_COUNT = 5;
    private static final int MSG_SET_NOT_MET_REQUIREMENTS = 2;
    private static final int MSG_SET_STOP_REASON = 3;
    private static final int MSG_TASK_STOPPED = 9;
    private static final int MSG_UPDATE_PROGRESS = 11;
    private static final String TAG = "DownloadManager";
    private int activeTaskCount;
    private final Context context;
    private final WritableDownloadIndex downloadIndex;
    private List<Download> downloads;
    private boolean downloadsPaused;
    private boolean initialized;
    private final InternalHandler internalHandler;
    private final CopyOnWriteArraySet<Listener> listeners;
    private final Handler mainHandler;
    private int maxParallelDownloads;
    private int minRetryCount;
    private int notMetRequirements;
    private int pendingMessages;
    private final RequirementsWatcher.Listener requirementsListener;
    private RequirementsWatcher requirementsWatcher;
    private boolean waitingForRequirements;

    /* loaded from: classes3.dex */
    public interface Listener {
        void onDownloadChanged(DownloadManager downloadManager, Download download);

        void onDownloadRemoved(DownloadManager downloadManager, Download download);

        void onDownloadsPausedChanged(DownloadManager downloadManager, boolean z);

        void onIdle(DownloadManager downloadManager);

        void onInitialized(DownloadManager downloadManager);

        void onRequirementsStateChanged(DownloadManager downloadManager, Requirements requirements, int i);

        void onWaitingForRequirementsChanged(DownloadManager downloadManager, boolean z);

        /* renamed from: com.google.android.exoplayer2.offline.DownloadManager$Listener$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static void $default$onInitialized(Listener _this, DownloadManager downloadManager) {
            }

            public static void $default$onDownloadsPausedChanged(Listener _this, DownloadManager downloadManager, boolean downloadsPaused) {
            }

            public static void $default$onDownloadChanged(Listener _this, DownloadManager downloadManager, Download download) {
            }

            public static void $default$onDownloadRemoved(Listener _this, DownloadManager downloadManager, Download download) {
            }

            public static void $default$onIdle(Listener _this, DownloadManager downloadManager) {
            }

            public static void $default$onRequirementsStateChanged(Listener _this, DownloadManager downloadManager, Requirements requirements, int notMetRequirements) {
            }

            public static void $default$onWaitingForRequirementsChanged(Listener _this, DownloadManager downloadManager, boolean waitingForRequirements) {
            }
        }
    }

    public DownloadManager(Context context, DatabaseProvider databaseProvider, Cache cache, DataSource.Factory upstreamFactory) {
        this(context, new DefaultDownloadIndex(databaseProvider), new DefaultDownloaderFactory(new DownloaderConstructorHelper(cache, upstreamFactory)));
    }

    public DownloadManager(Context context, WritableDownloadIndex downloadIndex, DownloaderFactory downloaderFactory) {
        this.context = context.getApplicationContext();
        this.downloadIndex = downloadIndex;
        this.maxParallelDownloads = 3;
        this.minRetryCount = 5;
        this.downloadsPaused = true;
        this.downloads = Collections.emptyList();
        this.listeners = new CopyOnWriteArraySet<>();
        Handler mainHandler = Util.createHandler(new Handler.Callback() { // from class: com.google.android.exoplayer2.offline.DownloadManager$$ExternalSyntheticLambda0
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                boolean handleMainMessage;
                handleMainMessage = DownloadManager.this.handleMainMessage(message);
                return handleMainMessage;
            }
        });
        this.mainHandler = mainHandler;
        HandlerThread internalThread = new HandlerThread("DownloadManager file i/o");
        internalThread.start();
        InternalHandler internalHandler = new InternalHandler(internalThread, downloadIndex, downloaderFactory, mainHandler, this.maxParallelDownloads, this.minRetryCount, this.downloadsPaused);
        this.internalHandler = internalHandler;
        RequirementsWatcher.Listener requirementsListener = new RequirementsWatcher.Listener() { // from class: com.google.android.exoplayer2.offline.DownloadManager$$ExternalSyntheticLambda1
            @Override // com.google.android.exoplayer2.scheduler.RequirementsWatcher.Listener
            public final void onRequirementsStateChanged(RequirementsWatcher requirementsWatcher, int i) {
                DownloadManager.this.onRequirementsStateChanged(requirementsWatcher, i);
            }
        };
        this.requirementsListener = requirementsListener;
        RequirementsWatcher requirementsWatcher = new RequirementsWatcher(context, requirementsListener, DEFAULT_REQUIREMENTS);
        this.requirementsWatcher = requirementsWatcher;
        int start = requirementsWatcher.start();
        this.notMetRequirements = start;
        this.pendingMessages = 1;
        internalHandler.obtainMessage(0, start, 0).sendToTarget();
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean isIdle() {
        return this.activeTaskCount == 0 && this.pendingMessages == 0;
    }

    public boolean isWaitingForRequirements() {
        return this.waitingForRequirements;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public Requirements getRequirements() {
        return this.requirementsWatcher.getRequirements();
    }

    public int getNotMetRequirements() {
        return this.notMetRequirements;
    }

    public void setRequirements(Requirements requirements) {
        if (requirements.equals(this.requirementsWatcher.getRequirements())) {
            return;
        }
        this.requirementsWatcher.stop();
        RequirementsWatcher requirementsWatcher = new RequirementsWatcher(this.context, this.requirementsListener, requirements);
        this.requirementsWatcher = requirementsWatcher;
        int notMetRequirements = requirementsWatcher.start();
        onRequirementsStateChanged(this.requirementsWatcher, notMetRequirements);
    }

    public int getMaxParallelDownloads() {
        return this.maxParallelDownloads;
    }

    public void setMaxParallelDownloads(int maxParallelDownloads) {
        Assertions.checkArgument(maxParallelDownloads > 0);
        if (this.maxParallelDownloads == maxParallelDownloads) {
            return;
        }
        this.maxParallelDownloads = maxParallelDownloads;
        this.pendingMessages++;
        this.internalHandler.obtainMessage(4, maxParallelDownloads, 0).sendToTarget();
    }

    public int getMinRetryCount() {
        return this.minRetryCount;
    }

    public void setMinRetryCount(int minRetryCount) {
        Assertions.checkArgument(minRetryCount >= 0);
        if (this.minRetryCount == minRetryCount) {
            return;
        }
        this.minRetryCount = minRetryCount;
        this.pendingMessages++;
        this.internalHandler.obtainMessage(5, minRetryCount, 0).sendToTarget();
    }

    public DownloadIndex getDownloadIndex() {
        return this.downloadIndex;
    }

    public List<Download> getCurrentDownloads() {
        return this.downloads;
    }

    public boolean getDownloadsPaused() {
        return this.downloadsPaused;
    }

    public void resumeDownloads() {
        setDownloadsPaused(false);
    }

    public void pauseDownloads() {
        setDownloadsPaused(true);
    }

    public void setStopReason(String id, int stopReason) {
        this.pendingMessages++;
        this.internalHandler.obtainMessage(3, stopReason, 0, id).sendToTarget();
    }

    public void addDownload(DownloadRequest request) {
        addDownload(request, 0);
    }

    public void addDownload(DownloadRequest request, int stopReason) {
        this.pendingMessages++;
        this.internalHandler.obtainMessage(6, stopReason, 0, request).sendToTarget();
    }

    public void removeDownload(String id) {
        this.pendingMessages++;
        this.internalHandler.obtainMessage(7, id).sendToTarget();
    }

    public void removeAllDownloads() {
        this.pendingMessages++;
        this.internalHandler.obtainMessage(8).sendToTarget();
    }

    public void release() {
        synchronized (this.internalHandler) {
            if (this.internalHandler.released) {
                return;
            }
            this.internalHandler.sendEmptyMessage(12);
            boolean wasInterrupted = false;
            while (!this.internalHandler.released) {
                try {
                    this.internalHandler.wait();
                } catch (InterruptedException e) {
                    wasInterrupted = true;
                }
            }
            if (wasInterrupted) {
                Thread.currentThread().interrupt();
            }
            this.mainHandler.removeCallbacksAndMessages(null);
            this.downloads = Collections.emptyList();
            this.pendingMessages = 0;
            this.activeTaskCount = 0;
            this.initialized = false;
            this.notMetRequirements = 0;
            this.waitingForRequirements = false;
        }
    }

    private void setDownloadsPaused(boolean downloadsPaused) {
        if (this.downloadsPaused == downloadsPaused) {
            return;
        }
        this.downloadsPaused = downloadsPaused;
        this.pendingMessages++;
        this.internalHandler.obtainMessage(1, downloadsPaused ? 1 : 0, 0).sendToTarget();
        boolean waitingForRequirementsChanged = updateWaitingForRequirements();
        Iterator<Listener> it = this.listeners.iterator();
        while (it.hasNext()) {
            Listener listener = it.next();
            listener.onDownloadsPausedChanged(this, downloadsPaused);
        }
        if (waitingForRequirementsChanged) {
            notifyWaitingForRequirementsChanged();
        }
    }

    public void onRequirementsStateChanged(RequirementsWatcher requirementsWatcher, int notMetRequirements) {
        Requirements requirements = requirementsWatcher.getRequirements();
        if (this.notMetRequirements != notMetRequirements) {
            this.notMetRequirements = notMetRequirements;
            this.pendingMessages++;
            this.internalHandler.obtainMessage(2, notMetRequirements, 0).sendToTarget();
        }
        boolean waitingForRequirementsChanged = updateWaitingForRequirements();
        Iterator<Listener> it = this.listeners.iterator();
        while (it.hasNext()) {
            Listener listener = it.next();
            listener.onRequirementsStateChanged(this, requirements, notMetRequirements);
        }
        if (waitingForRequirementsChanged) {
            notifyWaitingForRequirementsChanged();
        }
    }

    private boolean updateWaitingForRequirements() {
        boolean waitingForRequirements = false;
        if (!this.downloadsPaused && this.notMetRequirements != 0) {
            int i = 0;
            while (true) {
                if (i >= this.downloads.size()) {
                    break;
                } else if (this.downloads.get(i).state != 0) {
                    i++;
                } else {
                    waitingForRequirements = true;
                    break;
                }
            }
        }
        boolean waitingForRequirementsChanged = this.waitingForRequirements != waitingForRequirements;
        this.waitingForRequirements = waitingForRequirements;
        return waitingForRequirementsChanged;
    }

    private void notifyWaitingForRequirementsChanged() {
        Iterator<Listener> it = this.listeners.iterator();
        while (it.hasNext()) {
            Listener listener = it.next();
            listener.onWaitingForRequirementsChanged(this, this.waitingForRequirements);
        }
    }

    public boolean handleMainMessage(Message message) {
        switch (message.what) {
            case 0:
                List<Download> downloads = (List) message.obj;
                onInitialized(downloads);
                return true;
            case 1:
                int processedMessageCount = message.arg1;
                int activeTaskCount = message.arg2;
                onMessageProcessed(processedMessageCount, activeTaskCount);
                return true;
            case 2:
                DownloadUpdate update = (DownloadUpdate) message.obj;
                onDownloadUpdate(update);
                return true;
            default:
                throw new IllegalStateException();
        }
    }

    private void onInitialized(List<Download> downloads) {
        this.initialized = true;
        this.downloads = Collections.unmodifiableList(downloads);
        boolean waitingForRequirementsChanged = updateWaitingForRequirements();
        Iterator<Listener> it = this.listeners.iterator();
        while (it.hasNext()) {
            Listener listener = it.next();
            listener.onInitialized(this);
        }
        if (waitingForRequirementsChanged) {
            notifyWaitingForRequirementsChanged();
        }
    }

    private void onDownloadUpdate(DownloadUpdate update) {
        this.downloads = Collections.unmodifiableList(update.downloads);
        Download updatedDownload = update.download;
        boolean waitingForRequirementsChanged = updateWaitingForRequirements();
        if (update.isRemove) {
            Iterator<Listener> it = this.listeners.iterator();
            while (it.hasNext()) {
                Listener listener = it.next();
                listener.onDownloadRemoved(this, updatedDownload);
            }
        } else {
            Iterator<Listener> it2 = this.listeners.iterator();
            while (it2.hasNext()) {
                Listener listener2 = it2.next();
                listener2.onDownloadChanged(this, updatedDownload);
            }
        }
        if (waitingForRequirementsChanged) {
            notifyWaitingForRequirementsChanged();
        }
    }

    private void onMessageProcessed(int processedMessageCount, int activeTaskCount) {
        this.pendingMessages -= processedMessageCount;
        this.activeTaskCount = activeTaskCount;
        if (isIdle()) {
            Iterator<Listener> it = this.listeners.iterator();
            while (it.hasNext()) {
                Listener listener = it.next();
                listener.onIdle(this);
            }
        }
    }

    public static Download mergeRequest(Download download, DownloadRequest request, int stopReason, long nowMs) {
        int state;
        int state2 = download.state;
        long startTimeMs = (state2 == 5 || download.isTerminalState()) ? nowMs : download.startTimeMs;
        if (state2 == 5 || state2 == 7) {
            state = 7;
        } else if (stopReason != 0) {
            state = 1;
        } else {
            state = 0;
        }
        return new Download(download.request.copyWithMergedRequest(request), state, startTimeMs, nowMs, -1L, stopReason, 0);
    }

    /* loaded from: classes3.dex */
    public static final class InternalHandler extends Handler {
        private static final int UPDATE_PROGRESS_INTERVAL_MS = 5000;
        private int activeDownloadTaskCount;
        private final WritableDownloadIndex downloadIndex;
        private final DownloaderFactory downloaderFactory;
        private boolean downloadsPaused;
        private final Handler mainHandler;
        private int maxParallelDownloads;
        private int minRetryCount;
        private int notMetRequirements;
        public boolean released;
        private final HandlerThread thread;
        private final ArrayList<Download> downloads = new ArrayList<>();
        private final HashMap<String, Task> activeTasks = new HashMap<>();

        public InternalHandler(HandlerThread thread, WritableDownloadIndex downloadIndex, DownloaderFactory downloaderFactory, Handler mainHandler, int maxParallelDownloads, int minRetryCount, boolean downloadsPaused) {
            super(thread.getLooper());
            this.thread = thread;
            this.downloadIndex = downloadIndex;
            this.downloaderFactory = downloaderFactory;
            this.mainHandler = mainHandler;
            this.maxParallelDownloads = maxParallelDownloads;
            this.minRetryCount = minRetryCount;
            this.downloadsPaused = downloadsPaused;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = 1;
            switch (message.what) {
                case 0:
                    int notMetRequirements = message.arg1;
                    initialize(notMetRequirements);
                    break;
                case 1:
                    int notMetRequirements2 = message.arg1;
                    boolean downloadsPaused = notMetRequirements2 != 0;
                    setDownloadsPaused(downloadsPaused);
                    break;
                case 2:
                    int notMetRequirements3 = message.arg1;
                    setNotMetRequirements(notMetRequirements3);
                    break;
                case 3:
                    String id = (String) message.obj;
                    int stopReason = message.arg1;
                    setStopReason(id, stopReason);
                    break;
                case 4:
                    int minRetryCount = message.arg1;
                    setMaxParallelDownloads(minRetryCount);
                    break;
                case 5:
                    int minRetryCount2 = message.arg1;
                    setMinRetryCount(minRetryCount2);
                    break;
                case 6:
                    DownloadRequest request = (DownloadRequest) message.obj;
                    int stopReason2 = message.arg1;
                    addDownload(request, stopReason2);
                    break;
                case 7:
                    String id2 = (String) message.obj;
                    removeDownload(id2);
                    break;
                case 8:
                    removeAllDownloads();
                    break;
                case 9:
                    Task task = (Task) message.obj;
                    onTaskStopped(task);
                    i = 0;
                    break;
                case 10:
                    Task task2 = (Task) message.obj;
                    onContentLengthChanged(task2);
                    return;
                case 11:
                    updateProgress();
                    return;
                case 12:
                    release();
                    return;
                default:
                    throw new IllegalStateException();
            }
            this.mainHandler.obtainMessage(1, i, this.activeTasks.size()).sendToTarget();
        }

        private void initialize(int notMetRequirements) {
            this.notMetRequirements = notMetRequirements;
            DownloadCursor cursor = null;
            try {
                try {
                    this.downloadIndex.setDownloadingStatesToQueued();
                    cursor = this.downloadIndex.getDownloads(0, 1, 2, 5, 7);
                    while (cursor.moveToNext()) {
                        this.downloads.add(cursor.getDownload());
                    }
                } catch (IOException e) {
                    Log.e(DownloadManager.TAG, "Failed to load index.", e);
                    this.downloads.clear();
                }
                Util.closeQuietly(cursor);
                ArrayList<Download> downloadsForMessage = new ArrayList<>(this.downloads);
                this.mainHandler.obtainMessage(0, downloadsForMessage).sendToTarget();
                syncTasks();
            } catch (Throwable th) {
                Util.closeQuietly(cursor);
                throw th;
            }
        }

        private void setDownloadsPaused(boolean downloadsPaused) {
            this.downloadsPaused = downloadsPaused;
            syncTasks();
        }

        private void setNotMetRequirements(int notMetRequirements) {
            this.notMetRequirements = notMetRequirements;
            syncTasks();
        }

        private void setStopReason(String id, int stopReason) {
            if (id == null) {
                for (int i = 0; i < this.downloads.size(); i++) {
                    setStopReason(this.downloads.get(i), stopReason);
                }
                try {
                    this.downloadIndex.setStopReason(stopReason);
                } catch (IOException e) {
                    Log.e(DownloadManager.TAG, "Failed to set manual stop reason", e);
                }
            } else {
                Download download = getDownload(id, false);
                if (download != null) {
                    setStopReason(download, stopReason);
                } else {
                    try {
                        this.downloadIndex.setStopReason(id, stopReason);
                    } catch (IOException e2) {
                        Log.e(DownloadManager.TAG, "Failed to set manual stop reason: " + id, e2);
                    }
                }
            }
            syncTasks();
        }

        private void setStopReason(Download download, int stopReason) {
            if (stopReason == 0) {
                if (download.state == 1) {
                    putDownloadWithState(download, 0);
                }
            } else if (stopReason != download.stopReason) {
                int state = download.state;
                putDownload(new Download(download.request, (state == 0 || state == 2) ? 1 : state, download.startTimeMs, System.currentTimeMillis(), download.contentLength, stopReason, 0, download.progress));
            }
        }

        private void setMaxParallelDownloads(int maxParallelDownloads) {
            this.maxParallelDownloads = maxParallelDownloads;
            syncTasks();
        }

        private void setMinRetryCount(int minRetryCount) {
            this.minRetryCount = minRetryCount;
        }

        private void addDownload(DownloadRequest request, int stopReason) {
            Download download = getDownload(request.id, true);
            long nowMs = System.currentTimeMillis();
            if (download != null) {
                putDownload(DownloadManager.mergeRequest(download, request, stopReason, nowMs));
            } else {
                putDownload(new Download(request, stopReason != 0 ? 1 : 0, nowMs, nowMs, -1L, stopReason, 0));
            }
            syncTasks();
        }

        private void removeDownload(String id) {
            Download download = getDownload(id, true);
            if (download == null) {
                Log.e(DownloadManager.TAG, "Failed to remove nonexistent download: " + id);
                return;
            }
            putDownloadWithState(download, 5);
            syncTasks();
        }

        private void removeAllDownloads() {
            List<Download> terminalDownloads = new ArrayList<>();
            try {
                DownloadCursor cursor = this.downloadIndex.getDownloads(3, 4);
                while (cursor.moveToNext()) {
                    terminalDownloads.add(cursor.getDownload());
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (IOException e) {
                Log.e(DownloadManager.TAG, "Failed to load downloads.");
            }
            for (int i = 0; i < this.downloads.size(); i++) {
                ArrayList<Download> arrayList = this.downloads;
                arrayList.set(i, copyDownloadWithState(arrayList.get(i), 5));
            }
            for (int i2 = 0; i2 < terminalDownloads.size(); i2++) {
                this.downloads.add(copyDownloadWithState(terminalDownloads.get(i2), 5));
            }
            Collections.sort(this.downloads, DownloadManager$InternalHandler$$ExternalSyntheticLambda0.INSTANCE);
            try {
                this.downloadIndex.setStatesToRemoving();
            } catch (IOException e2) {
                Log.e(DownloadManager.TAG, "Failed to update index.", e2);
            }
            ArrayList<Download> updateList = new ArrayList<>(this.downloads);
            for (int i3 = 0; i3 < this.downloads.size(); i3++) {
                DownloadUpdate update = new DownloadUpdate(this.downloads.get(i3), false, updateList);
                this.mainHandler.obtainMessage(2, update).sendToTarget();
            }
            syncTasks();
        }

        private void release() {
            for (Task task : this.activeTasks.values()) {
                task.cancel(true);
            }
            try {
                this.downloadIndex.setDownloadingStatesToQueued();
            } catch (IOException e) {
                Log.e(DownloadManager.TAG, "Failed to update index.", e);
            }
            this.downloads.clear();
            this.thread.quit();
            synchronized (this) {
                this.released = true;
                notifyAll();
            }
        }

        private void syncTasks() {
            int accumulatingDownloadTaskCount = 0;
            for (int i = 0; i < this.downloads.size(); i++) {
                Download download = this.downloads.get(i);
                Task activeTask = this.activeTasks.get(download.request.id);
                switch (download.state) {
                    case 0:
                        activeTask = syncQueuedDownload(activeTask, download);
                        break;
                    case 1:
                        syncStoppedDownload(activeTask);
                        break;
                    case 2:
                        Assertions.checkNotNull(activeTask);
                        syncDownloadingDownload(activeTask, download, accumulatingDownloadTaskCount);
                        break;
                    case 3:
                    case 4:
                    case 6:
                    default:
                        throw new IllegalStateException();
                    case 5:
                    case 7:
                        syncRemovingDownload(activeTask, download);
                        break;
                }
                if (activeTask != null && !activeTask.isRemove) {
                    accumulatingDownloadTaskCount++;
                }
            }
        }

        private void syncStoppedDownload(Task activeTask) {
            if (activeTask == null) {
                return;
            }
            Assertions.checkState(!activeTask.isRemove);
            activeTask.cancel(false);
        }

        private Task syncQueuedDownload(Task activeTask, Download download) {
            if (activeTask == null) {
                if (!canDownloadsRun() || this.activeDownloadTaskCount >= this.maxParallelDownloads) {
                    return null;
                }
                Download download2 = putDownloadWithState(download, 2);
                Downloader downloader = this.downloaderFactory.createDownloader(download2.request);
                Task activeTask2 = new Task(download2.request, downloader, download2.progress, false, this.minRetryCount, this);
                this.activeTasks.put(download2.request.id, activeTask2);
                int i = this.activeDownloadTaskCount;
                this.activeDownloadTaskCount = i + 1;
                if (i == 0) {
                    sendEmptyMessageDelayed(11, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                }
                activeTask2.start();
                return activeTask2;
            }
            Assertions.checkState(!activeTask.isRemove);
            activeTask.cancel(false);
            return activeTask;
        }

        private void syncDownloadingDownload(Task activeTask, Download download, int accumulatingDownloadTaskCount) {
            Assertions.checkState(!activeTask.isRemove);
            if (!canDownloadsRun() || accumulatingDownloadTaskCount >= this.maxParallelDownloads) {
                putDownloadWithState(download, 0);
                activeTask.cancel(false);
            }
        }

        private void syncRemovingDownload(Task activeTask, Download download) {
            if (activeTask == null) {
                Downloader downloader = this.downloaderFactory.createDownloader(download.request);
                Task activeTask2 = new Task(download.request, downloader, download.progress, true, this.minRetryCount, this);
                this.activeTasks.put(download.request.id, activeTask2);
                activeTask2.start();
            } else if (!activeTask.isRemove) {
                activeTask.cancel(false);
            }
        }

        private void onContentLengthChanged(Task task) {
            String downloadId = task.request.id;
            long contentLength = task.contentLength;
            Download download = (Download) Assertions.checkNotNull(getDownload(downloadId, false));
            if (contentLength != download.contentLength && contentLength != -1) {
                putDownload(new Download(download.request, download.state, download.startTimeMs, System.currentTimeMillis(), contentLength, download.stopReason, download.failureReason, download.progress));
            }
        }

        private void onTaskStopped(Task task) {
            String downloadId = task.request.id;
            this.activeTasks.remove(downloadId);
            boolean isRemove = task.isRemove;
            if (!isRemove) {
                int i = this.activeDownloadTaskCount - 1;
                this.activeDownloadTaskCount = i;
                if (i == 0) {
                    removeMessages(11);
                }
            }
            if (task.isCanceled) {
                syncTasks();
                return;
            }
            Throwable finalError = task.finalError;
            if (finalError != null) {
                Log.e(DownloadManager.TAG, "Task failed: " + task.request + ", " + isRemove, finalError);
            }
            Download download = (Download) Assertions.checkNotNull(getDownload(downloadId, false));
            switch (download.state) {
                case 2:
                    Assertions.checkState(!isRemove);
                    onDownloadTaskStopped(download, finalError);
                    break;
                case 5:
                case 7:
                    Assertions.checkState(isRemove);
                    onRemoveTaskStopped(download);
                    break;
                default:
                    throw new IllegalStateException();
            }
            syncTasks();
        }

        private void onDownloadTaskStopped(Download download, Throwable finalError) {
            Download download2 = new Download(download.request, finalError == null ? 3 : 4, download.startTimeMs, System.currentTimeMillis(), download.contentLength, download.stopReason, finalError == null ? 0 : 1, download.progress);
            this.downloads.remove(getDownloadIndex(download2.request.id));
            try {
                this.downloadIndex.putDownload(download2);
            } catch (IOException e) {
                Log.e(DownloadManager.TAG, "Failed to update index.", e);
            }
            DownloadUpdate update = new DownloadUpdate(download2, false, new ArrayList(this.downloads));
            this.mainHandler.obtainMessage(2, update).sendToTarget();
        }

        private void onRemoveTaskStopped(Download download) {
            int i = 1;
            if (download.state == 7) {
                if (download.stopReason == 0) {
                    i = 0;
                }
                putDownloadWithState(download, i);
                syncTasks();
                return;
            }
            int removeIndex = getDownloadIndex(download.request.id);
            this.downloads.remove(removeIndex);
            try {
                this.downloadIndex.removeDownload(download.request.id);
            } catch (IOException e) {
                Log.e(DownloadManager.TAG, "Failed to remove from database");
            }
            DownloadUpdate update = new DownloadUpdate(download, true, new ArrayList(this.downloads));
            this.mainHandler.obtainMessage(2, update).sendToTarget();
        }

        private void updateProgress() {
            for (int i = 0; i < this.downloads.size(); i++) {
                Download download = this.downloads.get(i);
                if (download.state == 2) {
                    try {
                        this.downloadIndex.putDownload(download);
                    } catch (IOException e) {
                        Log.e(DownloadManager.TAG, "Failed to update index.", e);
                    }
                }
            }
            sendEmptyMessageDelayed(11, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }

        private boolean canDownloadsRun() {
            return !this.downloadsPaused && this.notMetRequirements == 0;
        }

        private Download putDownloadWithState(Download download, int state) {
            boolean z = true;
            if (state == 3 || state == 4 || state == 1) {
                z = false;
            }
            Assertions.checkState(z);
            return putDownload(copyDownloadWithState(download, state));
        }

        private Download putDownload(Download download) {
            boolean needsSort = true;
            Assertions.checkState((download.state == 3 || download.state == 4) ? false : true);
            int changedIndex = getDownloadIndex(download.request.id);
            if (changedIndex == -1) {
                this.downloads.add(download);
                Collections.sort(this.downloads, DownloadManager$InternalHandler$$ExternalSyntheticLambda0.INSTANCE);
            } else {
                if (download.startTimeMs == this.downloads.get(changedIndex).startTimeMs) {
                    needsSort = false;
                }
                this.downloads.set(changedIndex, download);
                if (needsSort) {
                    Collections.sort(this.downloads, DownloadManager$InternalHandler$$ExternalSyntheticLambda0.INSTANCE);
                }
            }
            try {
                this.downloadIndex.putDownload(download);
            } catch (IOException e) {
                Log.e(DownloadManager.TAG, "Failed to update index.", e);
            }
            DownloadUpdate update = new DownloadUpdate(download, false, new ArrayList(this.downloads));
            this.mainHandler.obtainMessage(2, update).sendToTarget();
            return download;
        }

        private Download getDownload(String id, boolean loadFromIndex) {
            int index = getDownloadIndex(id);
            if (index != -1) {
                return this.downloads.get(index);
            }
            if (loadFromIndex) {
                try {
                    return this.downloadIndex.getDownload(id);
                } catch (IOException e) {
                    Log.e(DownloadManager.TAG, "Failed to load download: " + id, e);
                    return null;
                }
            }
            return null;
        }

        private int getDownloadIndex(String id) {
            for (int i = 0; i < this.downloads.size(); i++) {
                Download download = this.downloads.get(i);
                if (download.request.id.equals(id)) {
                    return i;
                }
            }
            return -1;
        }

        private static Download copyDownloadWithState(Download download, int state) {
            return new Download(download.request, state, download.startTimeMs, System.currentTimeMillis(), download.contentLength, 0, 0, download.progress);
        }

        public static int compareStartTimes(Download first, Download second) {
            return Util.compareLong(first.startTimeMs, second.startTimeMs);
        }
    }

    /* loaded from: classes3.dex */
    public static class Task extends Thread implements Downloader.ProgressListener {
        private long contentLength;
        private final DownloadProgress downloadProgress;
        private final Downloader downloader;
        private Throwable finalError;
        private volatile InternalHandler internalHandler;
        private volatile boolean isCanceled;
        private final boolean isRemove;
        private final int minRetryCount;
        private final DownloadRequest request;

        private Task(DownloadRequest request, Downloader downloader, DownloadProgress downloadProgress, boolean isRemove, int minRetryCount, InternalHandler internalHandler) {
            this.request = request;
            this.downloader = downloader;
            this.downloadProgress = downloadProgress;
            this.isRemove = isRemove;
            this.minRetryCount = minRetryCount;
            this.internalHandler = internalHandler;
            this.contentLength = -1L;
        }

        public void cancel(boolean released) {
            if (released) {
                this.internalHandler = null;
            }
            if (!this.isCanceled) {
                this.isCanceled = true;
                this.downloader.cancel();
                interrupt();
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                if (this.isRemove) {
                    this.downloader.remove();
                } else {
                    int errorCount = 0;
                    long errorPosition = -1;
                    while (!this.isCanceled) {
                        try {
                            this.downloader.download(this);
                            break;
                        } catch (IOException e) {
                            if (!this.isCanceled) {
                                long bytesDownloaded = this.downloadProgress.bytesDownloaded;
                                if (bytesDownloaded != errorPosition) {
                                    errorPosition = bytesDownloaded;
                                    errorCount = 0;
                                }
                                errorCount++;
                                if (errorCount > this.minRetryCount) {
                                    throw e;
                                }
                                Thread.sleep(getRetryDelayMillis(errorCount));
                            }
                        }
                    }
                }
            } catch (Throwable e2) {
                this.finalError = e2;
            }
            Handler internalHandler = this.internalHandler;
            if (internalHandler != null) {
                internalHandler.obtainMessage(9, this).sendToTarget();
            }
        }

        @Override // com.google.android.exoplayer2.offline.Downloader.ProgressListener
        public void onProgress(long contentLength, long bytesDownloaded, float percentDownloaded) {
            this.downloadProgress.bytesDownloaded = bytesDownloaded;
            this.downloadProgress.percentDownloaded = percentDownloaded;
            if (contentLength != this.contentLength) {
                this.contentLength = contentLength;
                Handler internalHandler = this.internalHandler;
                if (internalHandler != null) {
                    internalHandler.obtainMessage(10, this).sendToTarget();
                }
            }
        }

        private static int getRetryDelayMillis(int errorCount) {
            return Math.min((errorCount - 1) * 1000, 5000);
        }
    }

    /* loaded from: classes3.dex */
    public static final class DownloadUpdate {
        public final Download download;
        public final List<Download> downloads;
        public final boolean isRemove;

        public DownloadUpdate(Download download, boolean isRemove, List<Download> downloads) {
            this.download = download;
            this.isRemove = isRemove;
            this.downloads = downloads;
        }
    }
}
