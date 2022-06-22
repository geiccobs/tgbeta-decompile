package com.microsoft.appcenter.crashes;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import com.microsoft.appcenter.AbstractAppCenterService;
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
import com.microsoft.appcenter.utils.context.SessionContext;
import com.microsoft.appcenter.utils.context.UserIdContext;
import com.microsoft.appcenter.utils.storage.FileManager;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
/* loaded from: classes.dex */
public class Crashes extends AbstractAppCenterService {
    private static final CrashesListener DEFAULT_ERROR_REPORTING_LISTENER = new DefaultCrashesListener(null);
    @SuppressLint({"StaticFieldLeak"})
    private static Crashes sInstance = null;
    private Context mContext;
    private Device mDevice;
    private final Map<String, LogFactory> mFactories;
    private boolean mHasReceivedMemoryWarningInLastSession;
    private long mInitializeTimestamp;
    private LogSerializer mLogSerializer;
    private ComponentCallbacks2 mMemoryWarningListener;
    private boolean mSavedUncaughtException;
    private UncaughtExceptionHandler mUncaughtExceptionHandler;
    private boolean mAutomaticProcessing = true;
    private CrashesListener mCrashesListener = DEFAULT_ERROR_REPORTING_LISTENER;
    private final Map<UUID, ErrorLogReport> mUnprocessedErrorReports = new LinkedHashMap();
    private final Map<UUID, ErrorLogReport> mErrorReportCache = new LinkedHashMap();

    /* renamed from: com.microsoft.appcenter.crashes.Crashes$1 */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements Runnable {
    }

    /* loaded from: classes.dex */
    public interface CallbackProcessor {
        void onCallBack(ErrorReport errorReport);

        boolean shouldDeleteThrowable();
    }

    /* loaded from: classes.dex */
    public interface ExceptionModelBuilder {
        Exception buildExceptionModel();
    }

    private static boolean isMemoryRunningLevelWasReceived(int i) {
        return i == 5 || i == 10 || i == 15 || i == 80;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected String getGroupName() {
        return "groupErrors";
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected String getLoggerTag() {
        return "AppCenterCrashes";
    }

    @Override // com.microsoft.appcenter.AppCenterService
    public String getServiceName() {
        return "Crashes";
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected int getTriggerCount() {
        return 1;
    }

    private Crashes() {
        HashMap hashMap = new HashMap();
        this.mFactories = hashMap;
        hashMap.put("managedError", ManagedErrorLogFactory.getInstance());
        hashMap.put("handledError", HandledErrorLogFactory.getInstance());
        hashMap.put("errorAttachment", ErrorAttachmentLogFactory.getInstance());
        DefaultLogSerializer defaultLogSerializer = new DefaultLogSerializer();
        this.mLogSerializer = defaultLogSerializer;
        defaultLogSerializer.addLogFactory("managedError", ManagedErrorLogFactory.getInstance());
        this.mLogSerializer.addLogFactory("errorAttachment", ErrorAttachmentLogFactory.getInstance());
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

    public static AppCenterFuture<Boolean> isEnabled() {
        return getInstance().isInstanceEnabledAsync();
    }

    public static void trackError(Throwable th) {
        trackError(th, null, null);
    }

    public static void trackError(Throwable th, Map<String, String> map, Iterable<ErrorAttachmentLog> iterable) {
        getInstance().queueException(th, map, iterable);
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected synchronized void applyEnabledState(boolean z) {
        initialize();
        if (z) {
            ComponentCallbacks2 componentCallbacks2 = new ComponentCallbacks2(this) { // from class: com.microsoft.appcenter.crashes.Crashes.5
                @Override // android.content.ComponentCallbacks
                public void onConfigurationChanged(Configuration configuration) {
                }

                @Override // android.content.ComponentCallbacks2
                public void onTrimMemory(int i) {
                    Crashes.saveMemoryRunningLevel(i);
                }

                @Override // android.content.ComponentCallbacks
                public void onLowMemory() {
                    Crashes.saveMemoryRunningLevel(80);
                }
            };
            this.mMemoryWarningListener = componentCallbacks2;
            this.mContext.registerComponentCallbacks(componentCallbacks2);
        } else {
            File[] listFiles = ErrorLogHelper.getErrorStorageDirectory().listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    AppCenterLog.debug("AppCenterCrashes", "Deleting file " + file);
                    if (!file.delete()) {
                        AppCenterLog.warn("AppCenterCrashes", "Failed to delete file " + file);
                    }
                }
            }
            AppCenterLog.info("AppCenterCrashes", "Deleted crashes local files");
            this.mErrorReportCache.clear();
            this.mContext.unregisterComponentCallbacks(this.mMemoryWarningListener);
            this.mMemoryWarningListener = null;
            SharedPreferencesManager.remove("com.microsoft.appcenter.crashes.memory");
        }
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService, com.microsoft.appcenter.AppCenterService
    public synchronized void onStarted(Context context, Channel channel, String str, String str2, boolean z) {
        this.mContext = context;
        if (!isInstanceEnabled()) {
            ErrorLogHelper.removeMinidumpFolder();
            AppCenterLog.debug("AppCenterCrashes", "Clean up minidump folder.");
        }
        super.onStarted(context, channel, str, str2, z);
        if (isInstanceEnabled()) {
            processPendingErrors();
        }
    }

    @Override // com.microsoft.appcenter.AppCenterService
    public Map<String, LogFactory> getLogFactories() {
        return this.mFactories;
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
                            ManagedErrorLog managedErrorLog = (ManagedErrorLog) log2;
                            final ErrorReport buildErrorReport = Crashes.this.buildErrorReport(managedErrorLog);
                            UUID id = managedErrorLog.getId();
                            if (buildErrorReport != null) {
                                if (callbackProcessor.shouldDeleteThrowable()) {
                                    Crashes.this.removeStoredThrowable(id);
                                }
                                HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.6.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        callbackProcessor.onCallBack(buildErrorReport);
                                    }
                                });
                                return;
                            }
                            AppCenterLog.warn("AppCenterCrashes", "Cannot find crash report for the error log: " + id);
                        } else if ((log2 instanceof ErrorAttachmentLog) || (log2 instanceof HandledErrorLog)) {
                        } else {
                            AppCenterLog.warn("AppCenterCrashes", "A different type of log comes to crashes: " + log.getClass().getName());
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
                    public void onCallBack(ErrorReport errorReport) {
                        Crashes.this.mCrashesListener.onBeforeSending(errorReport);
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
                    public void onCallBack(ErrorReport errorReport) {
                        Crashes.this.mCrashesListener.onSendingSucceeded(errorReport);
                    }
                });
            }

            @Override // com.microsoft.appcenter.channel.Channel.GroupListener
            public void onFailure(Log log, final Exception exc) {
                processCallback(log, new CallbackProcessor() { // from class: com.microsoft.appcenter.crashes.Crashes.6.4
                    @Override // com.microsoft.appcenter.crashes.Crashes.CallbackProcessor
                    public boolean shouldDeleteThrowable() {
                        return true;
                    }

                    @Override // com.microsoft.appcenter.crashes.Crashes.CallbackProcessor
                    public void onCallBack(ErrorReport errorReport) {
                        Crashes.this.mCrashesListener.onSendingFailed(errorReport, exc);
                    }
                });
            }
        };
    }

    synchronized Device getDeviceInfo(Context context) throws DeviceInfoHelper.DeviceInfoException {
        if (this.mDevice == null) {
            this.mDevice = DeviceInfoHelper.getDeviceInfo(context);
        }
        return this.mDevice;
    }

    private synchronized void queueException(final Throwable th, Map<String, String> map, Iterable<ErrorAttachmentLog> iterable) {
        queueException(new ExceptionModelBuilder(this) { // from class: com.microsoft.appcenter.crashes.Crashes.7
            @Override // com.microsoft.appcenter.crashes.Crashes.ExceptionModelBuilder
            public Exception buildExceptionModel() {
                return ErrorLogHelper.getModelExceptionFromThrowable(th);
            }
        }, map, iterable);
    }

    private synchronized UUID queueException(final ExceptionModelBuilder exceptionModelBuilder, Map<String, String> map, final Iterable<ErrorAttachmentLog> iterable) {
        final UUID randomUUID;
        final String userId = UserIdContext.getInstance().getUserId();
        randomUUID = UUID.randomUUID();
        final Map<String, String> validateProperties = ErrorLogHelper.validateProperties(map, "HandledError");
        post(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.9
            @Override // java.lang.Runnable
            public void run() {
                HandledErrorLog handledErrorLog = new HandledErrorLog();
                handledErrorLog.setId(randomUUID);
                handledErrorLog.setUserId(userId);
                handledErrorLog.setException(exceptionModelBuilder.buildExceptionModel());
                handledErrorLog.setProperties(validateProperties);
                ((AbstractAppCenterService) Crashes.this).mChannel.enqueue(handledErrorLog, "groupErrors", 1);
                Crashes.this.sendErrorAttachment(randomUUID, iterable);
            }
        });
        return randomUUID;
    }

    private void initialize() {
        boolean isInstanceEnabled = isInstanceEnabled();
        this.mInitializeTimestamp = isInstanceEnabled ? System.currentTimeMillis() : -1L;
        if (!isInstanceEnabled) {
            UncaughtExceptionHandler uncaughtExceptionHandler = this.mUncaughtExceptionHandler;
            if (uncaughtExceptionHandler == null) {
                return;
            }
            uncaughtExceptionHandler.unregister();
            this.mUncaughtExceptionHandler = null;
            return;
        }
        UncaughtExceptionHandler uncaughtExceptionHandler2 = new UncaughtExceptionHandler();
        this.mUncaughtExceptionHandler = uncaughtExceptionHandler2;
        uncaughtExceptionHandler2.register();
        processMinidumpFiles();
    }

    private void processMinidumpFiles() {
        File[] newMinidumpFiles;
        for (File file : ErrorLogHelper.getNewMinidumpFiles()) {
            if (!file.isDirectory()) {
                AppCenterLog.debug("AppCenterCrashes", "Found a minidump from a previous SDK version.");
                processSingleMinidump(file, file);
            } else {
                File[] listFiles = file.listFiles(new FilenameFilter(this) { // from class: com.microsoft.appcenter.crashes.Crashes.10
                    @Override // java.io.FilenameFilter
                    public boolean accept(File file2, String str) {
                        return str.endsWith(".dmp");
                    }
                });
                if (listFiles != null && listFiles.length != 0) {
                    for (File file2 : listFiles) {
                        processSingleMinidump(file2, file);
                    }
                }
            }
        }
        File lastErrorLogFile = ErrorLogHelper.getLastErrorLogFile();
        while (lastErrorLogFile != null && lastErrorLogFile.length() == 0) {
            AppCenterLog.warn("AppCenterCrashes", "Deleting empty error file: " + lastErrorLogFile);
            lastErrorLogFile.delete();
            lastErrorLogFile = ErrorLogHelper.getLastErrorLogFile();
        }
        if (lastErrorLogFile != null) {
            AppCenterLog.debug("AppCenterCrashes", "Processing crash report for the last session.");
            String read = FileManager.read(lastErrorLogFile);
            if (read == null) {
                AppCenterLog.error("AppCenterCrashes", "Error reading last session error log.");
            } else {
                try {
                    buildErrorReport((ManagedErrorLog) this.mLogSerializer.deserializeLog(read, null));
                    AppCenterLog.debug("AppCenterCrashes", "Processed crash report for the last session.");
                } catch (JSONException e) {
                    AppCenterLog.error("AppCenterCrashes", "Error parsing last session error log.", e);
                }
            }
        }
        ErrorLogHelper.removeStaleMinidumpSubfolders();
    }

    private void processSingleMinidump(File file, File file2) {
        AppCenterLog.debug("AppCenterCrashes", "Process pending minidump file: " + file);
        long lastModified = file.lastModified();
        File file3 = new File(ErrorLogHelper.getPendingMinidumpDirectory(), file.getName());
        Exception exception = new Exception();
        exception.setType("minidump");
        exception.setWrapperSdkName("appcenter.ndk");
        exception.setMinidumpFilePath(file3.getPath());
        ManagedErrorLog managedErrorLog = new ManagedErrorLog();
        managedErrorLog.setException(exception);
        managedErrorLog.setTimestamp(new Date(lastModified));
        managedErrorLog.setFatal(Boolean.TRUE);
        managedErrorLog.setId(ErrorLogHelper.parseLogFolderUuid(file2));
        SessionContext.SessionInfo sessionAt = SessionContext.getInstance().getSessionAt(lastModified);
        if (sessionAt != null && sessionAt.getAppLaunchTimestamp() <= lastModified) {
            managedErrorLog.setAppLaunchTimestamp(new Date(sessionAt.getAppLaunchTimestamp()));
        } else {
            managedErrorLog.setAppLaunchTimestamp(managedErrorLog.getTimestamp());
        }
        managedErrorLog.setProcessId(0);
        managedErrorLog.setProcessName("");
        managedErrorLog.setUserId(UserIdContext.getInstance().getUserId());
        try {
            Device storedDeviceInfo = ErrorLogHelper.getStoredDeviceInfo(file2);
            if (storedDeviceInfo == null) {
                storedDeviceInfo = getDeviceInfo(this.mContext);
                storedDeviceInfo.setWrapperSdkName("appcenter.ndk");
            }
            managedErrorLog.setDevice(storedDeviceInfo);
            saveErrorLogFiles(new NativeException(), managedErrorLog);
            if (file.renameTo(file3)) {
                return;
            }
            throw new IOException("Failed to move file");
        } catch (Exception e) {
            file.delete();
            removeAllStoredErrorLogFiles(managedErrorLog.getId());
            AppCenterLog.error("AppCenterCrashes", "Failed to process new minidump file: " + file, e);
        }
    }

    private void processPendingErrors() {
        File[] storedErrorLogFiles;
        for (File file : ErrorLogHelper.getStoredErrorLogFiles()) {
            AppCenterLog.debug("AppCenterCrashes", "Process pending error file: " + file);
            String read = FileManager.read(file);
            if (read != null) {
                try {
                    ManagedErrorLog managedErrorLog = (ManagedErrorLog) this.mLogSerializer.deserializeLog(read, null);
                    UUID id = managedErrorLog.getId();
                    ErrorReport buildErrorReport = buildErrorReport(managedErrorLog);
                    if (buildErrorReport == null) {
                        removeAllStoredErrorLogFiles(id);
                    } else {
                        if (this.mAutomaticProcessing && !this.mCrashesListener.shouldProcess(buildErrorReport)) {
                            AppCenterLog.debug("AppCenterCrashes", "CrashesListener.shouldProcess returned false, clean up and ignore log: " + id.toString());
                            removeAllStoredErrorLogFiles(id);
                        }
                        if (!this.mAutomaticProcessing) {
                            AppCenterLog.debug("AppCenterCrashes", "CrashesListener.shouldProcess returned true, continue processing log: " + id.toString());
                        }
                        this.mUnprocessedErrorReports.put(id, this.mErrorReportCache.get(id));
                    }
                } catch (JSONException e) {
                    AppCenterLog.error("AppCenterCrashes", "Error parsing error log. Deleting invalid file: " + file, e);
                    file.delete();
                }
            }
        }
        boolean isMemoryRunningLevelWasReceived = isMemoryRunningLevelWasReceived(SharedPreferencesManager.getInt("com.microsoft.appcenter.crashes.memory", -1));
        this.mHasReceivedMemoryWarningInLastSession = isMemoryRunningLevelWasReceived;
        if (isMemoryRunningLevelWasReceived) {
            AppCenterLog.debug("AppCenterCrashes", "The application received a low memory warning in the last session.");
        }
        SharedPreferencesManager.remove("com.microsoft.appcenter.crashes.memory");
        if (this.mAutomaticProcessing) {
            sendCrashReportsOrAwaitUserConfirmation();
        }
    }

    private boolean sendCrashReportsOrAwaitUserConfirmation() {
        final boolean z = SharedPreferencesManager.getBoolean("com.microsoft.appcenter.crashes.always.send", false);
        HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.11
            @Override // java.lang.Runnable
            public void run() {
                if (Crashes.this.mUnprocessedErrorReports.size() > 0) {
                    if (!z) {
                        if (Crashes.this.mAutomaticProcessing) {
                            if (!Crashes.this.mCrashesListener.shouldAwaitUserConfirmation()) {
                                AppCenterLog.debug("AppCenterCrashes", "CrashesListener.shouldAwaitUserConfirmation returned false, will send logs.");
                                Crashes.this.handleUserConfirmation(0);
                                return;
                            }
                            AppCenterLog.debug("AppCenterCrashes", "CrashesListener.shouldAwaitUserConfirmation returned true, wait sending logs.");
                            return;
                        }
                        AppCenterLog.debug("AppCenterCrashes", "Automatic processing disabled, will wait for explicit user confirmation.");
                        return;
                    }
                    AppCenterLog.debug("AppCenterCrashes", "The flag for user confirmation is set to ALWAYS_SEND, will send logs.");
                    Crashes.this.handleUserConfirmation(0);
                }
            }
        });
        return z;
    }

    public void removeAllStoredErrorLogFiles(UUID uuid) {
        ErrorLogHelper.removeStoredErrorLogFile(uuid);
        removeStoredThrowable(uuid);
    }

    public void removeStoredThrowable(UUID uuid) {
        this.mErrorReportCache.remove(uuid);
        WrapperSdkExceptionManager.deleteWrapperExceptionData(uuid);
        ErrorLogHelper.removeStoredThrowableFile(uuid);
    }

    ErrorReport buildErrorReport(ManagedErrorLog managedErrorLog) {
        UUID id = managedErrorLog.getId();
        if (!this.mErrorReportCache.containsKey(id)) {
            File storedThrowableFile = ErrorLogHelper.getStoredThrowableFile(id);
            if (storedThrowableFile == null) {
                return null;
            }
            ErrorReport errorReportFromErrorLog = ErrorLogHelper.getErrorReportFromErrorLog(managedErrorLog, storedThrowableFile.length() > 0 ? FileManager.read(storedThrowableFile) : null);
            this.mErrorReportCache.put(id, new ErrorLogReport(managedErrorLog, errorReportFromErrorLog, null));
            return errorReportFromErrorLog;
        }
        ErrorReport errorReport = this.mErrorReportCache.get(id).report;
        errorReport.setDevice(managedErrorLog.getDevice());
        return errorReport;
    }

    public synchronized void handleUserConfirmation(final int i) {
        post(new Runnable() { // from class: com.microsoft.appcenter.crashes.Crashes.12
            /* JADX WARN: Removed duplicated region for block: B:28:0x00bc  */
            /* JADX WARN: Removed duplicated region for block: B:31:0x00d8  */
            /* JADX WARN: Removed duplicated region for block: B:37:0x00f3 A[SYNTHETIC] */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void run() {
                /*
                    r10 = this;
                    int r0 = r2
                    r1 = 1
                    if (r0 != r1) goto L2d
                    com.microsoft.appcenter.crashes.Crashes r0 = com.microsoft.appcenter.crashes.Crashes.this
                    java.util.Map r0 = com.microsoft.appcenter.crashes.Crashes.access$1000(r0)
                    java.util.Set r0 = r0.keySet()
                    java.util.Iterator r0 = r0.iterator()
                L13:
                    boolean r1 = r0.hasNext()
                    if (r1 == 0) goto L28
                    java.lang.Object r1 = r0.next()
                    java.util.UUID r1 = (java.util.UUID) r1
                    r0.remove()
                    com.microsoft.appcenter.crashes.Crashes r2 = com.microsoft.appcenter.crashes.Crashes.this
                    com.microsoft.appcenter.crashes.Crashes.access$1500(r2, r1)
                    goto L13
                L28:
                    com.microsoft.appcenter.crashes.utils.ErrorLogHelper.cleanPendingMinidumps()
                    goto L101
                L2d:
                    r2 = 2
                    if (r0 != r2) goto L35
                    java.lang.String r0 = "com.microsoft.appcenter.crashes.always.send"
                    com.microsoft.appcenter.utils.storage.SharedPreferencesManager.putBoolean(r0, r1)
                L35:
                    com.microsoft.appcenter.crashes.Crashes r0 = com.microsoft.appcenter.crashes.Crashes.this
                    java.util.Map r0 = com.microsoft.appcenter.crashes.Crashes.access$1000(r0)
                    java.util.Set r0 = r0.entrySet()
                    java.util.Iterator r0 = r0.iterator()
                L43:
                    boolean r1 = r0.hasNext()
                    if (r1 == 0) goto L101
                    java.lang.Object r1 = r0.next()
                    java.util.Map$Entry r1 = (java.util.Map.Entry) r1
                    java.lang.Object r3 = r1.getValue()
                    com.microsoft.appcenter.crashes.Crashes$ErrorLogReport r3 = (com.microsoft.appcenter.crashes.Crashes.ErrorLogReport) r3
                    com.microsoft.appcenter.crashes.model.ErrorReport r4 = com.microsoft.appcenter.crashes.Crashes.ErrorLogReport.access$1300(r3)
                    com.microsoft.appcenter.ingestion.models.Device r4 = r4.getDevice()
                    r5 = 0
                    if (r4 == 0) goto Laa
                    com.microsoft.appcenter.crashes.model.ErrorReport r4 = com.microsoft.appcenter.crashes.Crashes.ErrorLogReport.access$1300(r3)
                    com.microsoft.appcenter.ingestion.models.Device r4 = r4.getDevice()
                    java.lang.String r4 = r4.getWrapperSdkName()
                    java.lang.String r6 = "appcenter.ndk"
                    boolean r4 = r6.equals(r4)
                    if (r4 == 0) goto Laa
                    com.microsoft.appcenter.crashes.ingestion.models.ManagedErrorLog r4 = com.microsoft.appcenter.crashes.Crashes.ErrorLogReport.access$1600(r3)
                    com.microsoft.appcenter.crashes.ingestion.models.Exception r4 = r4.getException()
                    java.lang.String r6 = r4.getMinidumpFilePath()
                    r4.setMinidumpFilePath(r5)
                    if (r6 != 0) goto L8c
                    java.lang.String r6 = r4.getStackTrace()
                    r4.setStackTrace(r5)
                L8c:
                    if (r6 == 0) goto La3
                    java.io.File r5 = new java.io.File
                    r5.<init>(r6)
                    byte[] r4 = com.microsoft.appcenter.utils.storage.FileManager.readBytes(r5)
                    java.lang.String r6 = "minidump.dmp"
                    java.lang.String r7 = "application/octet-stream"
                    com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog r4 = com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog.attachmentWithBinary(r4, r6, r7)
                    r9 = r5
                    r5 = r4
                    r4 = r9
                    goto Lab
                La3:
                    java.lang.String r4 = "AppCenterCrashes"
                    java.lang.String r6 = "NativeException found without minidump."
                    com.microsoft.appcenter.utils.AppCenterLog.warn(r4, r6)
                Laa:
                    r4 = r5
                Lab:
                    com.microsoft.appcenter.crashes.Crashes r6 = com.microsoft.appcenter.crashes.Crashes.this
                    com.microsoft.appcenter.channel.Channel r6 = com.microsoft.appcenter.crashes.Crashes.access$1700(r6)
                    com.microsoft.appcenter.crashes.ingestion.models.ManagedErrorLog r7 = com.microsoft.appcenter.crashes.Crashes.ErrorLogReport.access$1600(r3)
                    java.lang.String r8 = "groupErrors"
                    r6.enqueue(r7, r8, r2)
                    if (r5 == 0) goto Ld0
                    com.microsoft.appcenter.crashes.Crashes r6 = com.microsoft.appcenter.crashes.Crashes.this
                    com.microsoft.appcenter.crashes.ingestion.models.ManagedErrorLog r7 = com.microsoft.appcenter.crashes.Crashes.ErrorLogReport.access$1600(r3)
                    java.util.UUID r7 = r7.getId()
                    java.util.Set r5 = java.util.Collections.singleton(r5)
                    com.microsoft.appcenter.crashes.Crashes.access$900(r6, r7, r5)
                    r4.delete()
                Ld0:
                    com.microsoft.appcenter.crashes.Crashes r4 = com.microsoft.appcenter.crashes.Crashes.this
                    boolean r4 = com.microsoft.appcenter.crashes.Crashes.access$1200(r4)
                    if (r4 == 0) goto Lf3
                    com.microsoft.appcenter.crashes.Crashes r4 = com.microsoft.appcenter.crashes.Crashes.this
                    com.microsoft.appcenter.crashes.CrashesListener r4 = com.microsoft.appcenter.crashes.Crashes.access$700(r4)
                    com.microsoft.appcenter.crashes.model.ErrorReport r5 = com.microsoft.appcenter.crashes.Crashes.ErrorLogReport.access$1300(r3)
                    java.lang.Iterable r4 = r4.getErrorAttachments(r5)
                    com.microsoft.appcenter.crashes.Crashes r5 = com.microsoft.appcenter.crashes.Crashes.this
                    com.microsoft.appcenter.crashes.ingestion.models.ManagedErrorLog r3 = com.microsoft.appcenter.crashes.Crashes.ErrorLogReport.access$1600(r3)
                    java.util.UUID r3 = r3.getId()
                    com.microsoft.appcenter.crashes.Crashes.access$900(r5, r3, r4)
                Lf3:
                    r0.remove()
                    java.lang.Object r1 = r1.getKey()
                    java.util.UUID r1 = (java.util.UUID) r1
                    com.microsoft.appcenter.crashes.utils.ErrorLogHelper.removeStoredErrorLogFile(r1)
                    goto L43
                L101:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.crashes.Crashes.AnonymousClass12.run():void");
            }
        });
    }

    public void sendErrorAttachment(UUID uuid, Iterable<ErrorAttachmentLog> iterable) {
        if (iterable == null) {
            AppCenterLog.debug("AppCenterCrashes", "Error report: " + uuid.toString() + " does not have any attachment.");
            return;
        }
        for (ErrorAttachmentLog errorAttachmentLog : iterable) {
            if (errorAttachmentLog != null) {
                errorAttachmentLog.setId(UUID.randomUUID());
                errorAttachmentLog.setErrorId(uuid);
                if (!errorAttachmentLog.isValid()) {
                    AppCenterLog.error("AppCenterCrashes", "Not all required fields are present in ErrorAttachmentLog.");
                } else if (errorAttachmentLog.getData().length > 7340032) {
                    AppCenterLog.error("AppCenterCrashes", String.format(Locale.ENGLISH, "Discarding attachment with size above %d bytes: size=%d, fileName=%s.", 7340032, Integer.valueOf(errorAttachmentLog.getData().length), errorAttachmentLog.getFileName()));
                } else {
                    this.mChannel.enqueue(errorAttachmentLog, "groupErrors", 1);
                }
            } else {
                AppCenterLog.warn("AppCenterCrashes", "Skipping null ErrorAttachmentLog.");
            }
        }
    }

    public void saveUncaughtException(Thread thread, Throwable th) {
        try {
            saveUncaughtException(thread, th, ErrorLogHelper.getModelExceptionFromThrowable(th));
        } catch (IOException e) {
            AppCenterLog.error("AppCenterCrashes", "Error writing error log to file", e);
        } catch (JSONException e2) {
            AppCenterLog.error("AppCenterCrashes", "Error serializing error log to JSON", e2);
        }
    }

    UUID saveUncaughtException(Thread thread, Throwable th, Exception exception) throws JSONException, IOException {
        if (isEnabled().get().booleanValue() && !this.mSavedUncaughtException) {
            this.mSavedUncaughtException = true;
            return saveErrorLogFiles(th, ErrorLogHelper.createErrorLog(this.mContext, thread, exception, Thread.getAllStackTraces(), this.mInitializeTimestamp, true));
        }
        return null;
    }

    private UUID saveErrorLogFiles(Throwable th, ManagedErrorLog managedErrorLog) throws JSONException, IOException {
        File errorStorageDirectory = ErrorLogHelper.getErrorStorageDirectory();
        UUID id = managedErrorLog.getId();
        String uuid = id.toString();
        AppCenterLog.debug("AppCenterCrashes", "Saving uncaught exception.");
        File file = new File(errorStorageDirectory, uuid + ".json");
        FileManager.write(file, this.mLogSerializer.serializeLog(managedErrorLog));
        AppCenterLog.debug("AppCenterCrashes", "Saved JSON content for ingestion into " + file);
        File file2 = new File(errorStorageDirectory, uuid + ".throwable");
        if (th != null) {
            try {
                String stackTraceString = android.util.Log.getStackTraceString(th);
                FileManager.write(file2, stackTraceString);
                AppCenterLog.debug("AppCenterCrashes", "Saved stack trace as is for client side inspection in " + file2 + " stack trace:" + stackTraceString);
            } catch (StackOverflowError e) {
                AppCenterLog.error("AppCenterCrashes", "Failed to store stack trace.", e);
                th = null;
                file2.delete();
            }
        }
        if (th == null) {
            if (!file2.createNewFile()) {
                throw new IOException(file2.getName());
            }
            AppCenterLog.debug("AppCenterCrashes", "Saved empty Throwable file in " + file2);
        }
        return id;
    }

    public static void saveMemoryRunningLevel(int i) {
        SharedPreferencesManager.putInt("com.microsoft.appcenter.crashes.memory", i);
        AppCenterLog.debug("AppCenterCrashes", String.format("The memory running level (%s) was saved.", Integer.valueOf(i)));
    }

    /* loaded from: classes.dex */
    private static class DefaultCrashesListener extends AbstractCrashesListener {
        private DefaultCrashesListener() {
        }

        /* synthetic */ DefaultCrashesListener(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    /* loaded from: classes.dex */
    public static class ErrorLogReport {
        private final ManagedErrorLog log;
        private final ErrorReport report;

        /* synthetic */ ErrorLogReport(ManagedErrorLog managedErrorLog, ErrorReport errorReport, AnonymousClass1 anonymousClass1) {
            this(managedErrorLog, errorReport);
        }

        private ErrorLogReport(ManagedErrorLog managedErrorLog, ErrorReport errorReport) {
            this.log = managedErrorLog;
            this.report = errorReport;
        }
    }
}
