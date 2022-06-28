package com.google.android.exoplayer2.offline;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes3.dex */
public abstract class DownloadService extends Service {
    public static final String ACTION_ADD_DOWNLOAD = "com.google.android.exoplayer.downloadService.action.ADD_DOWNLOAD";
    public static final String ACTION_INIT = "com.google.android.exoplayer.downloadService.action.INIT";
    public static final String ACTION_PAUSE_DOWNLOADS = "com.google.android.exoplayer.downloadService.action.PAUSE_DOWNLOADS";
    public static final String ACTION_REMOVE_ALL_DOWNLOADS = "com.google.android.exoplayer.downloadService.action.REMOVE_ALL_DOWNLOADS";
    public static final String ACTION_REMOVE_DOWNLOAD = "com.google.android.exoplayer.downloadService.action.REMOVE_DOWNLOAD";
    private static final String ACTION_RESTART = "com.google.android.exoplayer.downloadService.action.RESTART";
    public static final String ACTION_RESUME_DOWNLOADS = "com.google.android.exoplayer.downloadService.action.RESUME_DOWNLOADS";
    public static final String ACTION_SET_REQUIREMENTS = "com.google.android.exoplayer.downloadService.action.SET_REQUIREMENTS";
    public static final String ACTION_SET_STOP_REASON = "com.google.android.exoplayer.downloadService.action.SET_STOP_REASON";
    public static final long DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000;
    public static final int FOREGROUND_NOTIFICATION_ID_NONE = 0;
    public static final String KEY_CONTENT_ID = "content_id";
    public static final String KEY_DOWNLOAD_REQUEST = "download_request";
    public static final String KEY_FOREGROUND = "foreground";
    public static final String KEY_REQUIREMENTS = "requirements";
    public static final String KEY_STOP_REASON = "stop_reason";
    private static final String TAG = "DownloadService";
    private static final HashMap<Class<? extends DownloadService>, DownloadManagerHelper> downloadManagerHelpers = new HashMap<>();
    private final int channelDescriptionResourceId;
    private final String channelId;
    private final int channelNameResourceId;
    private DownloadManager downloadManager;
    private final ForegroundNotificationUpdater foregroundNotificationUpdater;
    private boolean isDestroyed;
    private boolean isStopped;
    private int lastStartId;
    private boolean startedInForeground;
    private boolean taskRemoved;

    protected abstract DownloadManager getDownloadManager();

    protected abstract Notification getForegroundNotification(List<Download> list);

    protected abstract Scheduler getScheduler();

    protected DownloadService(int foregroundNotificationId) {
        this(foregroundNotificationId, 1000L);
    }

    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval) {
        this(foregroundNotificationId, foregroundNotificationUpdateInterval, null, 0, 0);
    }

    @Deprecated
    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval, String channelId, int channelNameResourceId) {
        this(foregroundNotificationId, foregroundNotificationUpdateInterval, channelId, channelNameResourceId, 0);
    }

    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval, String channelId, int channelNameResourceId, int channelDescriptionResourceId) {
        if (foregroundNotificationId == 0) {
            this.foregroundNotificationUpdater = null;
            this.channelId = null;
            this.channelNameResourceId = 0;
            this.channelDescriptionResourceId = 0;
            return;
        }
        this.foregroundNotificationUpdater = new ForegroundNotificationUpdater(foregroundNotificationId, foregroundNotificationUpdateInterval);
        this.channelId = channelId;
        this.channelNameResourceId = channelNameResourceId;
        this.channelDescriptionResourceId = channelDescriptionResourceId;
    }

    public static Intent buildAddDownloadIntent(Context context, Class<? extends DownloadService> clazz, DownloadRequest downloadRequest, boolean foreground) {
        return buildAddDownloadIntent(context, clazz, downloadRequest, 0, foreground);
    }

    public static Intent buildAddDownloadIntent(Context context, Class<? extends DownloadService> clazz, DownloadRequest downloadRequest, int stopReason, boolean foreground) {
        return getIntent(context, clazz, ACTION_ADD_DOWNLOAD, foreground).putExtra(KEY_DOWNLOAD_REQUEST, downloadRequest).putExtra(KEY_STOP_REASON, stopReason);
    }

    public static Intent buildRemoveDownloadIntent(Context context, Class<? extends DownloadService> clazz, String id, boolean foreground) {
        return getIntent(context, clazz, ACTION_REMOVE_DOWNLOAD, foreground).putExtra(KEY_CONTENT_ID, id);
    }

    public static Intent buildRemoveAllDownloadsIntent(Context context, Class<? extends DownloadService> clazz, boolean foreground) {
        return getIntent(context, clazz, ACTION_REMOVE_ALL_DOWNLOADS, foreground);
    }

    public static Intent buildResumeDownloadsIntent(Context context, Class<? extends DownloadService> clazz, boolean foreground) {
        return getIntent(context, clazz, ACTION_RESUME_DOWNLOADS, foreground);
    }

    public static Intent buildPauseDownloadsIntent(Context context, Class<? extends DownloadService> clazz, boolean foreground) {
        return getIntent(context, clazz, ACTION_PAUSE_DOWNLOADS, foreground);
    }

    public static Intent buildSetStopReasonIntent(Context context, Class<? extends DownloadService> clazz, String id, int stopReason, boolean foreground) {
        return getIntent(context, clazz, ACTION_SET_STOP_REASON, foreground).putExtra(KEY_CONTENT_ID, id).putExtra(KEY_STOP_REASON, stopReason);
    }

    public static Intent buildSetRequirementsIntent(Context context, Class<? extends DownloadService> clazz, Requirements requirements, boolean foreground) {
        return getIntent(context, clazz, ACTION_SET_REQUIREMENTS, foreground).putExtra(KEY_REQUIREMENTS, requirements);
    }

    public static void sendAddDownload(Context context, Class<? extends DownloadService> clazz, DownloadRequest downloadRequest, boolean foreground) {
        Intent intent = buildAddDownloadIntent(context, clazz, downloadRequest, foreground);
        startService(context, intent, foreground);
    }

    public static void sendAddDownload(Context context, Class<? extends DownloadService> clazz, DownloadRequest downloadRequest, int stopReason, boolean foreground) {
        Intent intent = buildAddDownloadIntent(context, clazz, downloadRequest, stopReason, foreground);
        startService(context, intent, foreground);
    }

    public static void sendRemoveDownload(Context context, Class<? extends DownloadService> clazz, String id, boolean foreground) {
        Intent intent = buildRemoveDownloadIntent(context, clazz, id, foreground);
        startService(context, intent, foreground);
    }

    public static void sendRemoveAllDownloads(Context context, Class<? extends DownloadService> clazz, boolean foreground) {
        Intent intent = buildRemoveAllDownloadsIntent(context, clazz, foreground);
        startService(context, intent, foreground);
    }

    public static void sendResumeDownloads(Context context, Class<? extends DownloadService> clazz, boolean foreground) {
        Intent intent = buildResumeDownloadsIntent(context, clazz, foreground);
        startService(context, intent, foreground);
    }

    public static void sendPauseDownloads(Context context, Class<? extends DownloadService> clazz, boolean foreground) {
        Intent intent = buildPauseDownloadsIntent(context, clazz, foreground);
        startService(context, intent, foreground);
    }

    public static void sendSetStopReason(Context context, Class<? extends DownloadService> clazz, String id, int stopReason, boolean foreground) {
        Intent intent = buildSetStopReasonIntent(context, clazz, id, stopReason, foreground);
        startService(context, intent, foreground);
    }

    public static void sendSetRequirements(Context context, Class<? extends DownloadService> clazz, Requirements requirements, boolean foreground) {
        Intent intent = buildSetRequirementsIntent(context, clazz, requirements, foreground);
        startService(context, intent, foreground);
    }

    public static void start(Context context, Class<? extends DownloadService> clazz) {
        context.startService(getIntent(context, clazz, ACTION_INIT));
    }

    public static void startForeground(Context context, Class<? extends DownloadService> clazz) {
        Intent intent = getIntent(context, clazz, ACTION_INIT, true);
        Util.startForegroundService(context, intent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Service
    public void onCreate() {
        String str = this.channelId;
        if (str != null) {
            NotificationUtil.createNotificationChannel(this, str, this.channelNameResourceId, this.channelDescriptionResourceId, 2);
        }
        Class<?> cls = getClass();
        HashMap<Class<? extends DownloadService>, DownloadManagerHelper> hashMap = downloadManagerHelpers;
        DownloadManagerHelper downloadManagerHelper = (DownloadManagerHelper) hashMap.get(cls);
        if (downloadManagerHelper == null) {
            boolean foregroundAllowed = this.foregroundNotificationUpdater != null;
            Scheduler scheduler = foregroundAllowed ? getScheduler() : null;
            DownloadManager downloadManager = getDownloadManager();
            this.downloadManager = downloadManager;
            downloadManager.resumeDownloads();
            downloadManagerHelper = new DownloadManagerHelper(getApplicationContext(), this.downloadManager, foregroundAllowed, scheduler, cls);
            hashMap.put(cls, downloadManagerHelper);
        } else {
            this.downloadManager = downloadManagerHelper.downloadManager;
        }
        downloadManagerHelper.attachService(this);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        ForegroundNotificationUpdater foregroundNotificationUpdater;
        this.lastStartId = startId;
        this.taskRemoved = false;
        String intentAction = null;
        String contentId = null;
        if (intent != null) {
            intentAction = intent.getAction();
            contentId = intent.getStringExtra(KEY_CONTENT_ID);
            this.startedInForeground |= intent.getBooleanExtra(KEY_FOREGROUND, false) || ACTION_RESTART.equals(intentAction);
        }
        if (intentAction == null) {
            intentAction = ACTION_INIT;
        }
        DownloadManager downloadManager = (DownloadManager) Assertions.checkNotNull(this.downloadManager);
        char c = 65535;
        switch (intentAction.hashCode()) {
            case -1931239035:
                if (intentAction.equals(ACTION_ADD_DOWNLOAD)) {
                    c = 2;
                    break;
                }
                break;
            case -932047176:
                if (intentAction.equals(ACTION_RESUME_DOWNLOADS)) {
                    c = 5;
                    break;
                }
                break;
            case -871181424:
                if (intentAction.equals(ACTION_RESTART)) {
                    c = 1;
                    break;
                }
                break;
            case -650547439:
                if (intentAction.equals(ACTION_REMOVE_ALL_DOWNLOADS)) {
                    c = 4;
                    break;
                }
                break;
            case -119057172:
                if (intentAction.equals(ACTION_SET_REQUIREMENTS)) {
                    c = '\b';
                    break;
                }
                break;
            case 191112771:
                if (intentAction.equals(ACTION_PAUSE_DOWNLOADS)) {
                    c = 6;
                    break;
                }
                break;
            case 671523141:
                if (intentAction.equals(ACTION_SET_STOP_REASON)) {
                    c = 7;
                    break;
                }
                break;
            case 1015676687:
                if (intentAction.equals(ACTION_INIT)) {
                    c = 0;
                    break;
                }
                break;
            case 1547520644:
                if (intentAction.equals(ACTION_REMOVE_DOWNLOAD)) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
                break;
            case 2:
                DownloadRequest downloadRequest = (DownloadRequest) ((Intent) Assertions.checkNotNull(intent)).getParcelableExtra(KEY_DOWNLOAD_REQUEST);
                if (downloadRequest == null) {
                    Log.e(TAG, "Ignored ADD_DOWNLOAD: Missing download_request extra");
                    break;
                } else {
                    int stopReason = intent.getIntExtra(KEY_STOP_REASON, 0);
                    downloadManager.addDownload(downloadRequest, stopReason);
                    break;
                }
            case 3:
                if (contentId == null) {
                    Log.e(TAG, "Ignored REMOVE_DOWNLOAD: Missing content_id extra");
                    break;
                } else {
                    downloadManager.removeDownload(contentId);
                    break;
                }
            case 4:
                downloadManager.removeAllDownloads();
                break;
            case 5:
                downloadManager.resumeDownloads();
                break;
            case 6:
                downloadManager.pauseDownloads();
                break;
            case 7:
                if (!((Intent) Assertions.checkNotNull(intent)).hasExtra(KEY_STOP_REASON)) {
                    Log.e(TAG, "Ignored SET_STOP_REASON: Missing stop_reason extra");
                    break;
                } else {
                    int stopReason2 = intent.getIntExtra(KEY_STOP_REASON, 0);
                    downloadManager.setStopReason(contentId, stopReason2);
                    break;
                }
            case '\b':
                Requirements requirements = (Requirements) ((Intent) Assertions.checkNotNull(intent)).getParcelableExtra(KEY_REQUIREMENTS);
                if (requirements == null) {
                    Log.e(TAG, "Ignored SET_REQUIREMENTS: Missing requirements extra");
                    break;
                } else {
                    downloadManager.setRequirements(requirements);
                    break;
                }
            default:
                Log.e(TAG, "Ignored unrecognized action: " + intentAction);
                break;
        }
        if (Util.SDK_INT >= 26 && this.startedInForeground && (foregroundNotificationUpdater = this.foregroundNotificationUpdater) != null) {
            foregroundNotificationUpdater.showNotificationIfNotAlready();
        }
        this.isStopped = false;
        if (downloadManager.isIdle()) {
            stop();
        }
        return 1;
    }

    @Override // android.app.Service
    public void onTaskRemoved(Intent rootIntent) {
        this.taskRemoved = true;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.isDestroyed = true;
        DownloadManagerHelper downloadManagerHelper = (DownloadManagerHelper) Assertions.checkNotNull(downloadManagerHelpers.get(getClass()));
        downloadManagerHelper.detachService(this);
        ForegroundNotificationUpdater foregroundNotificationUpdater = this.foregroundNotificationUpdater;
        if (foregroundNotificationUpdater != null) {
            foregroundNotificationUpdater.stopPeriodicUpdates();
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    protected final void invalidateForegroundNotification() {
        ForegroundNotificationUpdater foregroundNotificationUpdater = this.foregroundNotificationUpdater;
        if (foregroundNotificationUpdater != null && !this.isDestroyed) {
            foregroundNotificationUpdater.invalidate();
        }
    }

    @Deprecated
    protected void onDownloadChanged(Download download) {
    }

    @Deprecated
    protected void onDownloadRemoved(Download download) {
    }

    public void notifyDownloads(List<Download> downloads) {
        if (this.foregroundNotificationUpdater != null) {
            for (int i = 0; i < downloads.size(); i++) {
                if (needsStartedService(downloads.get(i).state)) {
                    this.foregroundNotificationUpdater.startPeriodicUpdates();
                    return;
                }
            }
        }
    }

    public void notifyDownloadChanged(Download download) {
        onDownloadChanged(download);
        if (this.foregroundNotificationUpdater != null) {
            if (needsStartedService(download.state)) {
                this.foregroundNotificationUpdater.startPeriodicUpdates();
            } else {
                this.foregroundNotificationUpdater.invalidate();
            }
        }
    }

    public void notifyDownloadRemoved(Download download) {
        onDownloadRemoved(download);
        ForegroundNotificationUpdater foregroundNotificationUpdater = this.foregroundNotificationUpdater;
        if (foregroundNotificationUpdater != null) {
            foregroundNotificationUpdater.invalidate();
        }
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public void stop() {
        ForegroundNotificationUpdater foregroundNotificationUpdater = this.foregroundNotificationUpdater;
        if (foregroundNotificationUpdater != null) {
            foregroundNotificationUpdater.stopPeriodicUpdates();
        }
        if (Util.SDK_INT < 28 && this.taskRemoved) {
            stopSelf();
            this.isStopped = true;
            return;
        }
        this.isStopped |= stopSelfResult(this.lastStartId);
    }

    public static boolean needsStartedService(int state) {
        return state == 2 || state == 5 || state == 7;
    }

    private static Intent getIntent(Context context, Class<? extends DownloadService> clazz, String action, boolean foreground) {
        return getIntent(context, clazz, action).putExtra(KEY_FOREGROUND, foreground);
    }

    public static Intent getIntent(Context context, Class<? extends DownloadService> clazz, String action) {
        return new Intent(context, clazz).setAction(action);
    }

    private static void startService(Context context, Intent intent, boolean foreground) {
        if (foreground) {
            Util.startForegroundService(context, intent);
        } else {
            context.startService(intent);
        }
    }

    /* loaded from: classes3.dex */
    public final class ForegroundNotificationUpdater {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean notificationDisplayed;
        private final int notificationId;
        private boolean periodicUpdatesStarted;
        private final long updateInterval;

        public ForegroundNotificationUpdater(int notificationId, long updateInterval) {
            DownloadService.this = r2;
            this.notificationId = notificationId;
            this.updateInterval = updateInterval;
        }

        public void startPeriodicUpdates() {
            this.periodicUpdatesStarted = true;
            update();
        }

        public void stopPeriodicUpdates() {
            this.periodicUpdatesStarted = false;
            this.handler.removeCallbacksAndMessages(null);
        }

        public void showNotificationIfNotAlready() {
            if (!this.notificationDisplayed) {
                update();
            }
        }

        public void invalidate() {
            if (this.notificationDisplayed) {
                update();
            }
        }

        public void update() {
            List<Download> downloads = ((DownloadManager) Assertions.checkNotNull(DownloadService.this.downloadManager)).getCurrentDownloads();
            DownloadService downloadService = DownloadService.this;
            downloadService.startForeground(this.notificationId, downloadService.getForegroundNotification(downloads));
            this.notificationDisplayed = true;
            if (this.periodicUpdatesStarted) {
                this.handler.removeCallbacksAndMessages(null);
                this.handler.postDelayed(new Runnable() { // from class: com.google.android.exoplayer2.offline.DownloadService$ForegroundNotificationUpdater$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DownloadService.ForegroundNotificationUpdater.this.update();
                    }
                }, this.updateInterval);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class DownloadManagerHelper implements DownloadManager.Listener {
        private final Context context;
        private final DownloadManager downloadManager;
        private DownloadService downloadService;
        private final boolean foregroundAllowed;
        private final Scheduler scheduler;
        private final Class<? extends DownloadService> serviceClass;

        @Override // com.google.android.exoplayer2.offline.DownloadManager.Listener
        public /* synthetic */ void onDownloadsPausedChanged(DownloadManager downloadManager, boolean z) {
            DownloadManager.Listener.CC.$default$onDownloadsPausedChanged(this, downloadManager, z);
        }

        @Override // com.google.android.exoplayer2.offline.DownloadManager.Listener
        public /* synthetic */ void onRequirementsStateChanged(DownloadManager downloadManager, Requirements requirements, int i) {
            DownloadManager.Listener.CC.$default$onRequirementsStateChanged(this, downloadManager, requirements, i);
        }

        private DownloadManagerHelper(Context context, DownloadManager downloadManager, boolean foregroundAllowed, Scheduler scheduler, Class<? extends DownloadService> serviceClass) {
            this.context = context;
            this.downloadManager = downloadManager;
            this.foregroundAllowed = foregroundAllowed;
            this.scheduler = scheduler;
            this.serviceClass = serviceClass;
            downloadManager.addListener(this);
            updateScheduler();
        }

        public void attachService(final DownloadService downloadService) {
            Assertions.checkState(this.downloadService == null);
            this.downloadService = downloadService;
            if (this.downloadManager.isInitialized()) {
                new Handler().postAtFrontOfQueue(new Runnable() { // from class: com.google.android.exoplayer2.offline.DownloadService$DownloadManagerHelper$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DownloadService.DownloadManagerHelper.this.m53x5d17c8bb(downloadService);
                    }
                });
            }
        }

        /* renamed from: lambda$attachService$0$com-google-android-exoplayer2-offline-DownloadService$DownloadManagerHelper */
        public /* synthetic */ void m53x5d17c8bb(DownloadService downloadService) {
            downloadService.notifyDownloads(this.downloadManager.getCurrentDownloads());
        }

        public void detachService(DownloadService downloadService) {
            Assertions.checkState(this.downloadService == downloadService);
            this.downloadService = null;
            if (this.scheduler != null && !this.downloadManager.isWaitingForRequirements()) {
                this.scheduler.cancel();
            }
        }

        @Override // com.google.android.exoplayer2.offline.DownloadManager.Listener
        public void onInitialized(DownloadManager downloadManager) {
            DownloadService downloadService = this.downloadService;
            if (downloadService != null) {
                downloadService.notifyDownloads(downloadManager.getCurrentDownloads());
            }
        }

        @Override // com.google.android.exoplayer2.offline.DownloadManager.Listener
        public void onDownloadChanged(DownloadManager downloadManager, Download download) {
            DownloadService downloadService = this.downloadService;
            if (downloadService != null) {
                downloadService.notifyDownloadChanged(download);
            }
            if (serviceMayNeedRestart() && DownloadService.needsStartedService(download.state)) {
                Log.w(DownloadService.TAG, "DownloadService wasn't running. Restarting.");
                restartService();
            }
        }

        @Override // com.google.android.exoplayer2.offline.DownloadManager.Listener
        public void onDownloadRemoved(DownloadManager downloadManager, Download download) {
            DownloadService downloadService = this.downloadService;
            if (downloadService != null) {
                downloadService.notifyDownloadRemoved(download);
            }
        }

        @Override // com.google.android.exoplayer2.offline.DownloadManager.Listener
        public final void onIdle(DownloadManager downloadManager) {
            DownloadService downloadService = this.downloadService;
            if (downloadService != null) {
                downloadService.stop();
            }
        }

        @Override // com.google.android.exoplayer2.offline.DownloadManager.Listener
        public void onWaitingForRequirementsChanged(DownloadManager downloadManager, boolean waitingForRequirements) {
            if (!waitingForRequirements && !downloadManager.getDownloadsPaused() && serviceMayNeedRestart()) {
                List<Download> downloads = downloadManager.getCurrentDownloads();
                int i = 0;
                while (true) {
                    if (i >= downloads.size()) {
                        break;
                    } else if (downloads.get(i).state != 0) {
                        i++;
                    } else {
                        restartService();
                        break;
                    }
                }
            }
            updateScheduler();
        }

        private boolean serviceMayNeedRestart() {
            DownloadService downloadService = this.downloadService;
            return downloadService == null || downloadService.isStopped();
        }

        private void restartService() {
            if (this.foregroundAllowed) {
                Intent intent = DownloadService.getIntent(this.context, this.serviceClass, DownloadService.ACTION_RESTART);
                Util.startForegroundService(this.context, intent);
                return;
            }
            try {
                Intent intent2 = DownloadService.getIntent(this.context, this.serviceClass, DownloadService.ACTION_INIT);
                this.context.startService(intent2);
            } catch (IllegalStateException e) {
                Log.w(DownloadService.TAG, "Failed to restart DownloadService (process is idle).");
            }
        }

        private void updateScheduler() {
            if (this.scheduler == null) {
                return;
            }
            if (this.downloadManager.isWaitingForRequirements()) {
                String servicePackage = this.context.getPackageName();
                Requirements requirements = this.downloadManager.getRequirements();
                boolean success = this.scheduler.schedule(requirements, servicePackage, DownloadService.ACTION_RESTART);
                if (!success) {
                    Log.e(DownloadService.TAG, "Scheduling downloads failed.");
                    return;
                }
                return;
            }
            this.scheduler.cancel();
        }
    }
}
