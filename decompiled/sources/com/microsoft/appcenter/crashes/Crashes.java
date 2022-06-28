package com.microsoft.appcenter.crashes;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import com.microsoft.appcenter.AbstractAppCenterService;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import com.microsoft.appcenter.crashes.ingestion.models.Exception;
import com.microsoft.appcenter.crashes.ingestion.models.HandledErrorLog;
import com.microsoft.appcenter.crashes.ingestion.models.ManagedErrorLog;
import com.microsoft.appcenter.crashes.ingestion.models.json.ErrorAttachmentLogFactory;
import com.microsoft.appcenter.crashes.ingestion.models.json.HandledErrorLogFactory;
import com.microsoft.appcenter.crashes.ingestion.models.json.ManagedErrorLogFactory;
import com.microsoft.appcenter.crashes.model.ErrorReport;
import com.microsoft.appcenter.crashes.model.NativeException;
import com.microsoft.appcenter.crashes.model.TestCrashException;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.json.DefaultLogSerializer;
import com.microsoft.appcenter.ingestion.models.json.LogFactory;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.HandlerUtils;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
import com.microsoft.appcenter.utils.async.DefaultAppCenterFuture;
import com.microsoft.appcenter.utils.context.SessionContext;
import com.microsoft.appcenter.utils.context.UserIdContext;
import com.microsoft.appcenter.utils.storage.FileManager;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
/* loaded from: classes3.dex */
public class Crashes extends AbstractAppCenterService {
    public static final int ALWAYS_SEND = 2;
    public static final int DONT_SEND = 1;
    static final String ERROR_GROUP = "groupErrors";
    public static final String LOG_TAG = "AppCenterCrashes";
    private static final int MAX_ATTACHMENT_SIZE = 7340032;
    public static final String PREF_KEY_ALWAYS_SEND = "com.microsoft.appcenter.crashes.always.send";
    static final String PREF_KEY_MEMORY_RUNNING_LEVEL = "com.microsoft.appcenter.crashes.memory";
    public static final int SEND = 0;
    private static final String SERVICE_NAME = "Crashes";
    private Context mContext;
    private Device mDevice;
    private final Map<String, LogFactory> mFactories;
    private boolean mHasReceivedMemoryWarningInLastSession;
    private long mInitializeTimestamp;
    private ErrorReport mLastSessionErrorReport;
    private LogSerializer mLogSerializer;
    private ComponentCallbacks2 mMemoryWarningListener;
    private boolean mSavedUncaughtException;
    private UncaughtExceptionHandler mUncaughtExceptionHandler;
    private static final CrashesListener DEFAULT_ERROR_REPORTING_LISTENER = new DefaultCrashesListener();
    private static Crashes sInstance = null;
    private boolean mAutomaticProcessing = true;
    private CrashesListener mCrashesListener = DEFAULT_ERROR_REPORTING_LISTENER;
    private final Map<UUID, ErrorLogReport> mUnprocessedErrorReports = new LinkedHashMap();
    private final Map<UUID, ErrorLogReport> mErrorReportCache = new LinkedHashMap();

    /* loaded from: classes3.dex */
    public interface CallbackProcessor {
        void onCallBack(ErrorReport errorReport);

        boolean shouldDeleteThrowable();
    }

    /* loaded from: classes3.dex */
    public interface ExceptionModelBuilder {
        Exception buildExceptionModel();
    }

    private Crashes() {
        HashMap hashMap = new HashMap();
        this.mFactories = hashMap;
        hashMap.put(ManagedErrorLog.TYPE, ManagedErrorLogFactory.getInstance());
        hashMap.put(HandledErrorLog.TYPE, HandledErrorLogFactory.getInstance());
        hashMap.put(ErrorAttachmentLog.TYPE, ErrorAttachmentLogFactory.getInstance());
        DefaultLogSerializer defaultLogSerializer = new DefaultLogSerializer();
        this.mLogSerializer = defaultLogSerializer;
        defaultLogSerializer.addLogFactory(ManagedErrorLog.TYPE, ManagedErrorLogFactory.getInstance());
        this.mLogSerializer.addLogFactory(ErrorAttachmentLog.TYPE, ErrorAttachmentLogFactory.getInstance());
    }

    public static synchronized Crashes getInstance() {
        Crashes crashes;
        synchronized (Crashes.class) {
            if (sInstance == null) {
                sInstance = new Crashes();
            }
            crashes = sInstance;
        }
        return crashes;
    }

    static synchronized void unsetInstance() {
        synchronized (Crashes.class) {
            sInstance = null;
        }
    }

    public static AppCenterFuture<Boolean> isEnabled() {
        return getInstance().isInstanceEnabledAsync();
    }

    public static AppCenterFuture<Void> setEnabled(boolean enabled) {
        return getInstance().setInstanceEnabledAsync(enabled);
    }

    public static void trackError(Throwable throwable) {
        trackError(throwable, null, null);
    }

    public static void trackError(Throwable throwable, Map<String, String> properties, Iterable<ErrorAttachmentLog> attachments) {
        getInstance().queueException(throwable, properties, attachments);
    }

    public static void generateTestCrash() {
        if (Constants.APPLICATION_DEBUGGABLE) {
            throw new TestCrashException();
        }
        AppCenterLog.warn(LOG_TAG, "The application is not debuggable so SDK won't generate test crash");
    }

    public static void setListener(CrashesListener listener) {
        getInstance().setInstanceListener(listener);
    }

    public static AppCenterFuture<String> getMinidumpDirectory() {
        return getInstance().getNewMinidumpDirectoryAsync();
    }

    public static void notifyUserConfirmation(int userConfirmation) {
        getInstance().handleUserConfirmation(userConfirmation);
    }

    public static AppCenterFuture<Boolean> hasCrashedInLastSession() {
        return getInstance().hasInstanceCrashedInLastSession();
    }

    public static AppCenterFuture<ErrorReport> getLastSessionCrashReport() {
        return getInstance().getInstanceLastSessionCrashReport();
    }

    public static AppCenterFuture<Boolean> hasReceivedMemoryWarningInLastSession() {
        return getInstance().hasInstanceReceivedMemoryWarningInLastSession();
    }

    private synchronized AppCenterFuture<String> getNewMinidumpDirectoryAsync() {
        final DefaultAppCenterFuture<String> future;
        future = new DefaultAppCenterFuture<>();
        postAsyncGetter(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.1
            @Override // java.lang.Runnable
            public void run() {
                future.complete(ErrorLogHelper.getNewMinidumpSubfolderWithContextData(Crashes.this.mContext).getAbsolutePath());
            }
        }, future, null);
        return future;
    }

    private synchronized AppCenterFuture<Boolean> hasInstanceCrashedInLastSession() {
        final DefaultAppCenterFuture<Boolean> future;
        future = new DefaultAppCenterFuture<>();
        postAsyncGetter(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.2
            @Override // java.lang.Runnable
            public void run() {
                future.complete(Boolean.valueOf(Crashes.this.mLastSessionErrorReport != null));
            }
        }, future, false);
        return future;
    }

    private synchronized AppCenterFuture<Boolean> hasInstanceReceivedMemoryWarningInLastSession() {
        final DefaultAppCenterFuture<Boolean> future;
        future = new DefaultAppCenterFuture<>();
        postAsyncGetter(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.3
            @Override // java.lang.Runnable
            public void run() {
                future.complete(Boolean.valueOf(Crashes.this.mHasReceivedMemoryWarningInLastSession));
            }
        }, future, false);
        return future;
    }

    private synchronized AppCenterFuture<ErrorReport> getInstanceLastSessionCrashReport() {
        final DefaultAppCenterFuture<ErrorReport> future;
        future = new DefaultAppCenterFuture<>();
        postAsyncGetter(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.4
            @Override // java.lang.Runnable
            public void run() {
                future.complete(Crashes.this.mLastSessionErrorReport);
            }
        }, future, null);
        return future;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected synchronized void applyEnabledState(boolean enabled) {
        initialize();
        if (enabled) {
            ComponentCallbacks2 componentCallbacks2 = new ComponentCallbacks2() { // from class: com.microsoft.appcenter.crashes.Crashes.5
                @Override // android.content.ComponentCallbacks2
                public void onTrimMemory(int level) {
                    Crashes.saveMemoryRunningLevel(level);
                }

                @Override // android.content.ComponentCallbacks
                public void onConfigurationChanged(Configuration newConfig) {
                }

                @Override // android.content.ComponentCallbacks
                public void onLowMemory() {
                    Crashes.saveMemoryRunningLevel(80);
                }
            };
            this.mMemoryWarningListener = componentCallbacks2;
            this.mContext.registerComponentCallbacks(componentCallbacks2);
        } else {
            File[] files = ErrorLogHelper.getErrorStorageDirectory().listFiles();
            if (files != null) {
                for (File file : files) {
                    AppCenterLog.debug(LOG_TAG, "Deleting file " + file);
                    if (!file.delete()) {
                        AppCenterLog.warn(LOG_TAG, "Failed to delete file " + file);
                    }
                }
            }
            AppCenterLog.info(LOG_TAG, "Deleted crashes local files");
            this.mErrorReportCache.clear();
            this.mLastSessionErrorReport = null;
            this.mContext.unregisterComponentCallbacks(this.mMemoryWarningListener);
            this.mMemoryWarningListener = null;
            SharedPreferencesManager.remove(PREF_KEY_MEMORY_RUNNING_LEVEL);
        }
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService, com.microsoft.appcenter.AppCenterService
    public synchronized void onStarted(Context context, Channel channel, String appSecret, String transmissionTargetToken, boolean startedFromApp) {
        this.mContext = context;
        if (!isInstanceEnabled()) {
            ErrorLogHelper.removeMinidumpFolder();
            AppCenterLog.debug(LOG_TAG, "Clean up minidump folder.");
        }
        super.onStarted(context, channel, appSecret, transmissionTargetToken, startedFromApp);
        if (isInstanceEnabled()) {
            processPendingErrors();
        }
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService, com.microsoft.appcenter.AppCenterService
    public Map<String, LogFactory> getLogFactories() {
        return this.mFactories;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected String getGroupName() {
        return ERROR_GROUP;
    }

    @Override // com.microsoft.appcenter.AppCenterService
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected String getLoggerTag() {
        return LOG_TAG;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected int getTriggerCount() {
        return 1;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected Channel.GroupListener getChannelListener() {
        return new Channel.GroupListener() { // from class: com.microsoft.appcenter.crashes.Crashes.6
            private void processCallback(final Log log, final CallbackProcessor callbackProcessor) {
                Crashes.this.post(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.6.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Log log2 = log;
                        if (log2 instanceof ManagedErrorLog) {
                            ManagedErrorLog errorLog = (ManagedErrorLog) log2;
                            final ErrorReport report = Crashes.this.buildErrorReport(errorLog);
                            UUID id = errorLog.getId();
                            if (report != null) {
                                if (callbackProcessor.shouldDeleteThrowable()) {
                                    Crashes.this.removeStoredThrowable(id);
                                }
                                HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.6.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        callbackProcessor.onCallBack(report);
                                    }
                                });
                                return;
                            }
                            AppCenterLog.warn(Crashes.LOG_TAG, "Cannot find crash report for the error log: " + id);
                        } else if (!(log2 instanceof ErrorAttachmentLog) && !(log2 instanceof HandledErrorLog)) {
                            AppCenterLog.warn(Crashes.LOG_TAG, "A different type of log comes to crashes: " + log.getClass().getName());
                        }
                    }
                });
            }

            @Override // com.microsoft.appcenter.channel.Channel.GroupListener
            public void onBeforeSending(Log log) {
                processCallback(log, new CallbackProcessor() { // from class: com.microsoft.appcenter.crashes.Crashes.6.2
                    @Override // com.microsoft.appcenter.crashes.Crashes.CallbackProcessor
                    public boolean shouldDeleteThrowable() {
                        return false;
                    }

                    @Override // com.microsoft.appcenter.crashes.Crashes.CallbackProcessor
                    public void onCallBack(ErrorReport report) {
                        Crashes.this.mCrashesListener.onBeforeSending(report);
                    }
                });
            }

            @Override // com.microsoft.appcenter.channel.Channel.GroupListener
            public void onSuccess(Log log) {
                processCallback(log, new CallbackProcessor() { // from class: com.microsoft.appcenter.crashes.Crashes.6.3
                    @Override // com.microsoft.appcenter.crashes.Crashes.CallbackProcessor
                    public boolean shouldDeleteThrowable() {
                        return true;
                    }

                    @Override // com.microsoft.appcenter.crashes.Crashes.CallbackProcessor
                    public void onCallBack(ErrorReport report) {
                        Crashes.this.mCrashesListener.onSendingSucceeded(report);
                    }
                });
            }

            @Override // com.microsoft.appcenter.channel.Channel.GroupListener
            public void onFailure(Log log, final Exception e) {
                processCallback(log, new CallbackProcessor() { // from class: com.microsoft.appcenter.crashes.Crashes.6.4
                    @Override // com.microsoft.appcenter.crashes.Crashes.CallbackProcessor
                    public boolean shouldDeleteThrowable() {
                        return true;
                    }

                    @Override // com.microsoft.appcenter.crashes.Crashes.CallbackProcessor
                    public void onCallBack(ErrorReport report) {
                        Crashes.this.mCrashesListener.onSendingFailed(report, e);
                    }
                });
            }
        };
    }

    public synchronized Device getDeviceInfo(Context context) throws DeviceInfoHelper.DeviceInfoException {
        if (this.mDevice == null) {
            this.mDevice = DeviceInfoHelper.getDeviceInfo(context);
        }
        return this.mDevice;
    }

    public synchronized long getInitializeTimestamp() {
        return this.mInitializeTimestamp;
    }

    private synchronized void queueException(final Throwable throwable, Map<String, String> properties, Iterable<ErrorAttachmentLog> attachments) {
        queueException(new ExceptionModelBuilder() { // from class: com.microsoft.appcenter.crashes.Crashes.7
            @Override // com.microsoft.appcenter.crashes.Crashes.ExceptionModelBuilder
            public Exception buildExceptionModel() {
                return ErrorLogHelper.getModelExceptionFromThrowable(throwable);
            }
        }, properties, attachments);
    }

    public synchronized UUID queueException(final Exception modelException, Map<String, String> properties, Iterable<ErrorAttachmentLog> attachments) {
        return queueException(new ExceptionModelBuilder() { // from class: com.microsoft.appcenter.crashes.Crashes.8
            @Override // com.microsoft.appcenter.crashes.Crashes.ExceptionModelBuilder
            public Exception buildExceptionModel() {
                return modelException;
            }
        }, properties, attachments);
    }

    private synchronized UUID queueException(final ExceptionModelBuilder exceptionModelBuilder, Map<String, String> properties, final Iterable<ErrorAttachmentLog> attachments) {
        final UUID errorId;
        final String userId = UserIdContext.getInstance().getUserId();
        errorId = UUID.randomUUID();
        final Map<String, String> validatedProperties = ErrorLogHelper.validateProperties(properties, "HandledError");
        post(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.9
            @Override // java.lang.Runnable
            public void run() {
                HandledErrorLog errorLog = new HandledErrorLog();
                errorLog.setId(errorId);
                errorLog.setUserId(userId);
                errorLog.setException(exceptionModelBuilder.buildExceptionModel());
                errorLog.setProperties(validatedProperties);
                Crashes.this.mChannel.enqueue(errorLog, Crashes.ERROR_GROUP, 1);
                Crashes.this.sendErrorAttachment(errorId, attachments);
            }
        });
        return errorId;
    }

    private void initialize() {
        boolean enabled = isInstanceEnabled();
        this.mInitializeTimestamp = enabled ? System.currentTimeMillis() : -1L;
        if (!enabled) {
            UncaughtExceptionHandler uncaughtExceptionHandler = this.mUncaughtExceptionHandler;
            if (uncaughtExceptionHandler != null) {
                uncaughtExceptionHandler.unregister();
                this.mUncaughtExceptionHandler = null;
                return;
            }
            return;
        }
        UncaughtExceptionHandler uncaughtExceptionHandler2 = new UncaughtExceptionHandler();
        this.mUncaughtExceptionHandler = uncaughtExceptionHandler2;
        uncaughtExceptionHandler2.register();
        processMinidumpFiles();
    }

    private void processMinidumpFiles() {
        File[] newMinidumpFiles;
        for (File minidumpSubfolder : ErrorLogHelper.getNewMinidumpFiles()) {
            if (!minidumpSubfolder.isDirectory()) {
                AppCenterLog.debug(LOG_TAG, "Found a minidump from a previous SDK version.");
                processSingleMinidump(minidumpSubfolder, minidumpSubfolder);
            } else {
                File[] minidumpSubfolderFiles = minidumpSubfolder.listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.Crashes.10
                    @Override // java.io.FilenameFilter
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(ErrorLogHelper.MINIDUMP_FILE_EXTENSION);
                    }
                });
                if (minidumpSubfolderFiles != null && minidumpSubfolderFiles.length != 0) {
                    for (File minidumpFile : minidumpSubfolderFiles) {
                        processSingleMinidump(minidumpFile, minidumpSubfolder);
                    }
                }
            }
        }
        File logFile = ErrorLogHelper.getLastErrorLogFile();
        while (logFile != null && logFile.length() == 0) {
            AppCenterLog.warn(LOG_TAG, "Deleting empty error file: " + logFile);
            logFile.delete();
            logFile = ErrorLogHelper.getLastErrorLogFile();
        }
        if (logFile != null) {
            AppCenterLog.debug(LOG_TAG, "Processing crash report for the last session.");
            String logFileContents = FileManager.read(logFile);
            if (logFileContents == null) {
                AppCenterLog.error(LOG_TAG, "Error reading last session error log.");
            } else {
                try {
                    ManagedErrorLog log = (ManagedErrorLog) this.mLogSerializer.deserializeLog(logFileContents, null);
                    this.mLastSessionErrorReport = buildErrorReport(log);
                    AppCenterLog.debug(LOG_TAG, "Processed crash report for the last session.");
                } catch (JSONException e) {
                    AppCenterLog.error(LOG_TAG, "Error parsing last session error log.", e);
                }
            }
        }
        ErrorLogHelper.removeStaleMinidumpSubfolders();
    }

    private void processSingleMinidump(File minidumpFile, File minidumpFolder) {
        AppCenterLog.debug(LOG_TAG, "Process pending minidump file: " + minidumpFile);
        long minidumpDate = minidumpFile.lastModified();
        File dest = new File(ErrorLogHelper.getPendingMinidumpDirectory(), minidumpFile.getName());
        Exception modelException = new Exception();
        modelException.setType("minidump");
        modelException.setWrapperSdkName(Constants.WRAPPER_SDK_NAME_NDK);
        modelException.setMinidumpFilePath(dest.getPath());
        ManagedErrorLog errorLog = new ManagedErrorLog();
        errorLog.setException(modelException);
        errorLog.setTimestamp(new Date(minidumpDate));
        errorLog.setFatal(true);
        errorLog.setId(ErrorLogHelper.parseLogFolderUuid(minidumpFolder));
        SessionContext.SessionInfo session = SessionContext.getInstance().getSessionAt(minidumpDate);
        if (session != null && session.getAppLaunchTimestamp() <= minidumpDate) {
            errorLog.setAppLaunchTimestamp(new Date(session.getAppLaunchTimestamp()));
        } else {
            errorLog.setAppLaunchTimestamp(errorLog.getTimestamp());
        }
        errorLog.setProcessId(0);
        errorLog.setProcessName("");
        errorLog.setUserId(UserIdContext.getInstance().getUserId());
        try {
            Device savedDeviceInfo = ErrorLogHelper.getStoredDeviceInfo(minidumpFolder);
            if (savedDeviceInfo == null) {
                savedDeviceInfo = getDeviceInfo(this.mContext);
                savedDeviceInfo.setWrapperSdkName(Constants.WRAPPER_SDK_NAME_NDK);
            }
            errorLog.setDevice(savedDeviceInfo);
            saveErrorLogFiles(new NativeException(), errorLog);
            if (!minidumpFile.renameTo(dest)) {
                throw new IOException("Failed to move file");
            }
        } catch (Exception e) {
            minidumpFile.delete();
            removeAllStoredErrorLogFiles(errorLog.getId());
            AppCenterLog.error(LOG_TAG, "Failed to process new minidump file: " + minidumpFile, e);
        }
    }

    private void processPendingErrors() {
        File[] storedErrorLogFiles;
        for (File logFile : ErrorLogHelper.getStoredErrorLogFiles()) {
            AppCenterLog.debug(LOG_TAG, "Process pending error file: " + logFile);
            String logfileContents = FileManager.read(logFile);
            if (logfileContents != null) {
                try {
                    ManagedErrorLog log = (ManagedErrorLog) this.mLogSerializer.deserializeLog(logfileContents, null);
                    UUID id = log.getId();
                    ErrorReport report = buildErrorReport(log);
                    if (report == null) {
                        removeAllStoredErrorLogFiles(id);
                    } else {
                        if (this.mAutomaticProcessing && !this.mCrashesListener.shouldProcess(report)) {
                            AppCenterLog.debug(LOG_TAG, "CrashesListener.shouldProcess returned false, clean up and ignore log: " + id.toString());
                            removeAllStoredErrorLogFiles(id);
                        }
                        if (!this.mAutomaticProcessing) {
                            AppCenterLog.debug(LOG_TAG, "CrashesListener.shouldProcess returned true, continue processing log: " + id.toString());
                        }
                        this.mUnprocessedErrorReports.put(id, this.mErrorReportCache.get(id));
                    }
                } catch (JSONException e) {
                    AppCenterLog.error(LOG_TAG, "Error parsing error log. Deleting invalid file: " + logFile, e);
                    logFile.delete();
                }
            }
        }
        boolean isMemoryRunningLevelWasReceived = isMemoryRunningLevelWasReceived(SharedPreferencesManager.getInt(PREF_KEY_MEMORY_RUNNING_LEVEL, -1));
        this.mHasReceivedMemoryWarningInLastSession = isMemoryRunningLevelWasReceived;
        if (isMemoryRunningLevelWasReceived) {
            AppCenterLog.debug(LOG_TAG, "The application received a low memory warning in the last session.");
        }
        SharedPreferencesManager.remove(PREF_KEY_MEMORY_RUNNING_LEVEL);
        if (this.mAutomaticProcessing) {
            sendCrashReportsOrAwaitUserConfirmation();
        }
    }

    private static boolean isMemoryRunningLevelWasReceived(int memoryLevel) {
        return memoryLevel == 5 || memoryLevel == 10 || memoryLevel == 15 || memoryLevel == 80;
    }

    public boolean sendCrashReportsOrAwaitUserConfirmation() {
        final boolean alwaysSend = SharedPreferencesManager.getBoolean(PREF_KEY_ALWAYS_SEND, false);
        HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.11
            @Override // java.lang.Runnable
            public void run() {
                if (Crashes.this.mUnprocessedErrorReports.size() > 0) {
                    if (!alwaysSend) {
                        if (Crashes.this.mAutomaticProcessing) {
                            if (!Crashes.this.mCrashesListener.shouldAwaitUserConfirmation()) {
                                AppCenterLog.debug(Crashes.LOG_TAG, "CrashesListener.shouldAwaitUserConfirmation returned false, will send logs.");
                                Crashes.this.handleUserConfirmation(0);
                                return;
                            }
                            AppCenterLog.debug(Crashes.LOG_TAG, "CrashesListener.shouldAwaitUserConfirmation returned true, wait sending logs.");
                            return;
                        }
                        AppCenterLog.debug(Crashes.LOG_TAG, "Automatic processing disabled, will wait for explicit user confirmation.");
                        return;
                    }
                    AppCenterLog.debug(Crashes.LOG_TAG, "The flag for user confirmation is set to ALWAYS_SEND, will send logs.");
                    Crashes.this.handleUserConfirmation(0);
                }
            }
        });
        return alwaysSend;
    }

    public void removeAllStoredErrorLogFiles(UUID id) {
        ErrorLogHelper.removeStoredErrorLogFile(id);
        removeStoredThrowable(id);
    }

    public void removeStoredThrowable(UUID id) {
        this.mErrorReportCache.remove(id);
        WrapperSdkExceptionManager.deleteWrapperExceptionData(id);
        ErrorLogHelper.removeStoredThrowableFile(id);
    }

    UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.mUncaughtExceptionHandler;
    }

    void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        this.mUncaughtExceptionHandler = handler;
    }

    ErrorReport buildErrorReport(ManagedErrorLog log) {
        UUID id = log.getId();
        if (!this.mErrorReportCache.containsKey(id)) {
            File file = ErrorLogHelper.getStoredThrowableFile(id);
            if (file == null) {
                return null;
            }
            String stackTrace = null;
            if (file.length() > 0) {
                stackTrace = FileManager.read(file);
            }
            ErrorReport report = ErrorLogHelper.getErrorReportFromErrorLog(log, stackTrace);
            this.mErrorReportCache.put(id, new ErrorLogReport(log, report));
            return report;
        }
        ErrorReport report2 = this.mErrorReportCache.get(id).report;
        report2.setDevice(log.getDevice());
        return report2;
    }

    CrashesListener getInstanceListener() {
        return this.mCrashesListener;
    }

    synchronized void setInstanceListener(CrashesListener listener) {
        if (listener == null) {
            listener = DEFAULT_ERROR_REPORTING_LISTENER;
        }
        this.mCrashesListener = listener;
    }

    public synchronized void handleUserConfirmation(final int userConfirmation) {
        post(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.12
            @Override // java.lang.Runnable
            public void run() {
                int i = userConfirmation;
                if (i == 1) {
                    Iterator<UUID> iterator = Crashes.this.mUnprocessedErrorReports.keySet().iterator();
                    while (iterator.hasNext()) {
                        UUID id = iterator.next();
                        iterator.remove();
                        Crashes.this.removeAllStoredErrorLogFiles(id);
                    }
                    ErrorLogHelper.cleanPendingMinidumps();
                    return;
                }
                if (i == 2) {
                    SharedPreferencesManager.putBoolean(Crashes.PREF_KEY_ALWAYS_SEND, true);
                }
                Iterator<Map.Entry<UUID, ErrorLogReport>> unprocessedIterator = Crashes.this.mUnprocessedErrorReports.entrySet().iterator();
                while (unprocessedIterator.hasNext()) {
                    File dumpFile = null;
                    ErrorAttachmentLog dumpAttachment = null;
                    Map.Entry<UUID, ErrorLogReport> unprocessedEntry = unprocessedIterator.next();
                    ErrorLogReport errorLogReport = unprocessedEntry.getValue();
                    if (errorLogReport.report.getDevice() != null && Constants.WRAPPER_SDK_NAME_NDK.equals(errorLogReport.report.getDevice().getWrapperSdkName())) {
                        Exception exception = errorLogReport.log.getException();
                        String minidumpFilePath = exception.getMinidumpFilePath();
                        exception.setMinidumpFilePath(null);
                        if (minidumpFilePath == null) {
                            minidumpFilePath = exception.getStackTrace();
                            exception.setStackTrace(null);
                        }
                        if (minidumpFilePath != null) {
                            dumpFile = new File(minidumpFilePath);
                            byte[] logfileContents = FileManager.readBytes(dumpFile);
                            dumpAttachment = ErrorAttachmentLog.attachmentWithBinary(logfileContents, "minidump.dmp", "application/octet-stream");
                        } else {
                            AppCenterLog.warn(Crashes.LOG_TAG, "NativeException found without minidump.");
                        }
                    }
                    Crashes.this.mChannel.enqueue(errorLogReport.log, Crashes.ERROR_GROUP, 2);
                    if (dumpAttachment != null) {
                        Crashes.this.sendErrorAttachment(errorLogReport.log.getId(), Collections.singleton(dumpAttachment));
                        dumpFile.delete();
                    }
                    if (Crashes.this.mAutomaticProcessing) {
                        Iterable<ErrorAttachmentLog> attachments = Crashes.this.mCrashesListener.getErrorAttachments(errorLogReport.report);
                        Crashes.this.sendErrorAttachment(errorLogReport.log.getId(), attachments);
                    }
                    unprocessedIterator.remove();
                    ErrorLogHelper.removeStoredErrorLogFile(unprocessedEntry.getKey());
                }
            }
        });
    }

    public void sendErrorAttachment(UUID errorId, Iterable<ErrorAttachmentLog> attachments) {
        if (attachments == null) {
            AppCenterLog.debug(LOG_TAG, "Error report: " + errorId.toString() + " does not have any attachment.");
            return;
        }
        for (ErrorAttachmentLog attachment : attachments) {
            if (attachment == null) {
                AppCenterLog.warn(LOG_TAG, "Skipping null ErrorAttachmentLog.");
            } else {
                attachment.setId(UUID.randomUUID());
                attachment.setErrorId(errorId);
                if (!attachment.isValid()) {
                    AppCenterLog.error(LOG_TAG, "Not all required fields are present in ErrorAttachmentLog.");
                } else if (attachment.getData().length > MAX_ATTACHMENT_SIZE) {
                    AppCenterLog.error(LOG_TAG, String.format(Locale.ENGLISH, "Discarding attachment with size above %d bytes: size=%d, fileName=%s.", Integer.valueOf((int) MAX_ATTACHMENT_SIZE), Integer.valueOf(attachment.getData().length), attachment.getFileName()));
                } else {
                    this.mChannel.enqueue(attachment, ERROR_GROUP, 1);
                }
            }
        }
    }

    void setLogSerializer(LogSerializer logSerializer) {
        this.mLogSerializer = logSerializer;
    }

    public void saveUncaughtException(Thread thread, Throwable throwable) {
        try {
            saveUncaughtException(thread, throwable, ErrorLogHelper.getModelExceptionFromThrowable(throwable));
        } catch (IOException e) {
            AppCenterLog.error(LOG_TAG, "Error writing error log to file", e);
        } catch (JSONException e2) {
            AppCenterLog.error(LOG_TAG, "Error serializing error log to JSON", e2);
        }
    }

    public UUID saveUncaughtException(Thread thread, Throwable throwable, Exception modelException) throws JSONException, IOException {
        if (isEnabled().get().booleanValue() && !this.mSavedUncaughtException) {
            this.mSavedUncaughtException = true;
            ManagedErrorLog errorLog = ErrorLogHelper.createErrorLog(this.mContext, thread, modelException, Thread.getAllStackTraces(), this.mInitializeTimestamp, true);
            return saveErrorLogFiles(throwable, errorLog);
        }
        return null;
    }

    private UUID saveErrorLogFiles(Throwable throwable, ManagedErrorLog errorLog) throws JSONException, IOException {
        File errorStorageDirectory = ErrorLogHelper.getErrorStorageDirectory();
        UUID errorLogId = errorLog.getId();
        String filename = errorLogId.toString();
        AppCenterLog.debug(LOG_TAG, "Saving uncaught exception.");
        File errorLogFile = new File(errorStorageDirectory, filename + ErrorLogHelper.ERROR_LOG_FILE_EXTENSION);
        String errorLogString = this.mLogSerializer.serializeLog(errorLog);
        FileManager.write(errorLogFile, errorLogString);
        AppCenterLog.debug(LOG_TAG, "Saved JSON content for ingestion into " + errorLogFile);
        File throwableFile = new File(errorStorageDirectory, filename + ErrorLogHelper.THROWABLE_FILE_EXTENSION);
        if (throwable != null) {
            try {
                String stackTrace = android.util.Log.getStackTraceString(throwable);
                FileManager.write(throwableFile, stackTrace);
                AppCenterLog.debug(LOG_TAG, "Saved stack trace as is for client side inspection in " + throwableFile + " stack trace:" + stackTrace);
            } catch (StackOverflowError e) {
                AppCenterLog.error(LOG_TAG, "Failed to store stack trace.", e);
                throwable = null;
                throwableFile.delete();
            }
        }
        if (throwable == null) {
            if (!throwableFile.createNewFile()) {
                throw new IOException(throwableFile.getName());
            }
            AppCenterLog.debug(LOG_TAG, "Saved empty Throwable file in " + throwableFile);
        }
        return errorLogId;
    }

    public void setAutomaticProcessing(boolean automaticProcessing) {
        this.mAutomaticProcessing = automaticProcessing;
    }

    public AppCenterFuture<Collection<ErrorReport>> getUnprocessedErrorReports() {
        final DefaultAppCenterFuture<Collection<ErrorReport>> future = new DefaultAppCenterFuture<>();
        postAsyncGetter(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.13
            @Override // java.lang.Runnable
            public void run() {
                ArrayList arrayList = new ArrayList(Crashes.this.mUnprocessedErrorReports.size());
                for (ErrorLogReport entry : Crashes.this.mUnprocessedErrorReports.values()) {
                    arrayList.add(entry.report);
                }
                future.complete(arrayList);
            }
        }, future, Collections.emptyList());
        return future;
    }

    public AppCenterFuture<Boolean> sendCrashReportsOrAwaitUserConfirmation(final Collection<String> filteredReportIds) {
        final DefaultAppCenterFuture<Boolean> future = new DefaultAppCenterFuture<>();
        postAsyncGetter(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.14
            @Override // java.lang.Runnable
            public void run() {
                Iterator<Map.Entry<UUID, ErrorLogReport>> iterator = Crashes.this.mUnprocessedErrorReports.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<UUID, ErrorLogReport> entry = iterator.next();
                    UUID id = entry.getKey();
                    String idString = entry.getValue().report.getId();
                    Collection collection = filteredReportIds;
                    if (collection != null && collection.contains(idString)) {
                        AppCenterLog.debug(Crashes.LOG_TAG, "CrashesListener.shouldProcess returned true, continue processing log: " + idString);
                    } else {
                        AppCenterLog.debug(Crashes.LOG_TAG, "CrashesListener.shouldProcess returned false, clean up and ignore log: " + idString);
                        Crashes.this.removeAllStoredErrorLogFiles(id);
                        iterator.remove();
                    }
                }
                future.complete(Boolean.valueOf(Crashes.this.sendCrashReportsOrAwaitUserConfirmation()));
            }
        }, future, false);
        return future;
    }

    public void sendErrorAttachments(final String errorReportId, final Iterable<ErrorAttachmentLog> attachments) {
        post(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.15
            @Override // java.lang.Runnable
            public void run() {
                try {
                    UUID errorId = UUID.fromString(errorReportId);
                    Crashes.this.sendErrorAttachment(errorId, attachments);
                } catch (RuntimeException e) {
                    AppCenterLog.error(Crashes.LOG_TAG, "Error report identifier has an invalid format for sending attachments.");
                }
            }
        });
    }

    public static void saveMemoryRunningLevel(int level) {
        SharedPreferencesManager.putInt(PREF_KEY_MEMORY_RUNNING_LEVEL, level);
        AppCenterLog.debug(LOG_TAG, String.format("The memory running level (%s) was saved.", Integer.valueOf(level)));
    }

    /* loaded from: classes3.dex */
    private static class DefaultCrashesListener extends AbstractCrashesListener {
        private DefaultCrashesListener() {
        }
    }

    /* loaded from: classes3.dex */
    public static class ErrorLogReport {
        private final ManagedErrorLog log;
        private final ErrorReport report;

        private ErrorLogReport(ManagedErrorLog log, ErrorReport report) {
            this.log = log;
            this.report = report;
        }
    }
}
