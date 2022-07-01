package com.microsoft.appcenter.crashes.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.crashes.ingestion.models.Exception;
import com.microsoft.appcenter.crashes.ingestion.models.ManagedErrorLog;
import com.microsoft.appcenter.crashes.ingestion.models.StackFrame;
import com.microsoft.appcenter.crashes.ingestion.models.Thread;
import com.microsoft.appcenter.crashes.model.ErrorReport;
import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.context.UserIdContext;
import com.microsoft.appcenter.utils.storage.FileManager;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
public class ErrorLogHelper {
    private static File sErrorLogDirectory;
    private static File sNewMinidumpDirectory;
    private static File sPendingMinidumpDirectory;

    public static ManagedErrorLog createErrorLog(Context context, Thread thread, Exception exception, Map<Thread, StackTraceElement[]> map, long j, boolean z) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        ManagedErrorLog managedErrorLog = new ManagedErrorLog();
        managedErrorLog.setId(UUID.randomUUID());
        managedErrorLog.setTimestamp(new Date());
        managedErrorLog.setUserId(UserIdContext.getInstance().getUserId());
        try {
            managedErrorLog.setDevice(DeviceInfoHelper.getDeviceInfo(context));
        } catch (DeviceInfoHelper.DeviceInfoException e) {
            AppCenterLog.error("AppCenterCrashes", "Could not attach device properties snapshot to error log, will attach at sending time", e);
        }
        managedErrorLog.setProcessId(Integer.valueOf(Process.myPid()));
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        if (activityManager != null && (runningAppProcesses = activityManager.getRunningAppProcesses()) != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo.pid == Process.myPid()) {
                    managedErrorLog.setProcessName(runningAppProcessInfo.processName);
                }
            }
        }
        if (managedErrorLog.getProcessName() == null) {
            managedErrorLog.setProcessName("");
        }
        managedErrorLog.setArchitecture(getArchitecture());
        managedErrorLog.setErrorThreadId(Long.valueOf(thread.getId()));
        managedErrorLog.setErrorThreadName(thread.getName());
        managedErrorLog.setFatal(Boolean.valueOf(z));
        managedErrorLog.setAppLaunchTimestamp(new Date(j));
        managedErrorLog.setException(exception);
        ArrayList arrayList = new ArrayList(map.size());
        for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
            Thread thread2 = new Thread();
            thread2.setId(entry.getKey().getId());
            thread2.setName(entry.getKey().getName());
            thread2.setFrames(getModelFramesFromStackTrace(entry.getValue()));
            arrayList.add(thread2);
        }
        managedErrorLog.setThreads(arrayList);
        return managedErrorLog;
    }

    @TargetApi(R.styleable.MapAttrs_uiZoomGestures)
    private static String getArchitecture() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Build.SUPPORTED_ABIS[0];
        }
        return Build.CPU_ABI;
    }

    public static synchronized File getErrorStorageDirectory() {
        File file;
        synchronized (ErrorLogHelper.class) {
            if (sErrorLogDirectory == null) {
                File file2 = new File(Constants.FILES_PATH, "error");
                sErrorLogDirectory = file2;
                FileManager.mkdir(file2.getAbsolutePath());
            }
            file = sErrorLogDirectory;
        }
        return file;
    }

    public static synchronized File getNewMinidumpDirectory() {
        File file;
        synchronized (ErrorLogHelper.class) {
            file = new File(new File(getErrorStorageDirectory().getAbsolutePath(), "minidump"), "new");
        }
        return file;
    }

    public static synchronized File getPendingMinidumpDirectory() {
        File file;
        synchronized (ErrorLogHelper.class) {
            if (sPendingMinidumpDirectory == null) {
                File file2 = new File(new File(getErrorStorageDirectory().getAbsolutePath(), "minidump"), "pending");
                sPendingMinidumpDirectory = file2;
                FileManager.mkdir(file2.getPath());
            }
            file = sPendingMinidumpDirectory;
        }
        return file;
    }

    public static File[] getStoredErrorLogFiles() {
        File[] listFiles = getErrorStorageDirectory().listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file, String str) {
                return str.endsWith(".json");
            }
        });
        return listFiles != null ? listFiles : new File[0];
    }

    public static File[] getNewMinidumpFiles() {
        File[] listFiles = getNewMinidumpDirectory().listFiles();
        return listFiles != null ? listFiles : new File[0];
    }

    public static Device getStoredDeviceInfo(File file) {
        File[] listFiles = file.listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.2
            @Override // java.io.FilenameFilter
            public boolean accept(File file2, String str) {
                return str.equals("deviceInfo");
            }
        });
        if (listFiles == null || listFiles.length == 0) {
            AppCenterLog.warn("AppCenterCrashes", "No stored deviceinfo file found in a minidump folder.");
            return null;
        }
        String read = FileManager.read(listFiles[0]);
        if (read == null) {
            AppCenterLog.error("AppCenterCrashes", "Failed to read stored device info.");
            return null;
        }
        return parseDevice(read);
    }

    static Device parseDevice(String str) {
        try {
            Device device = new Device();
            device.read(new JSONObject(str));
            return device;
        } catch (JSONException e) {
            AppCenterLog.error("AppCenterCrashes", "Failed to deserialize device info.", e);
            return null;
        }
    }

    public static void removeStaleMinidumpSubfolders() {
        File[] listFiles = getNewMinidumpDirectory().listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.3
            @Override // java.io.FilenameFilter
            public boolean accept(File file, String str) {
                if (ErrorLogHelper.sNewMinidumpDirectory != null) {
                    return !str.equals(ErrorLogHelper.sNewMinidumpDirectory.getName());
                }
                return true;
            }
        });
        if (listFiles == null || listFiles.length == 0) {
            AppCenterLog.debug("AppCenterCrashes", "No previous minidump sub-folders.");
            return;
        }
        for (File file : listFiles) {
            FileManager.deleteDirectory(file);
        }
    }

    public static void removeMinidumpFolder() {
        FileManager.deleteDirectory(new File(getErrorStorageDirectory().getAbsolutePath(), "minidump"));
    }

    public static File getLastErrorLogFile() {
        return FileManager.lastModifiedFile(getErrorStorageDirectory(), new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.4
            @Override // java.io.FilenameFilter
            public boolean accept(File file, String str) {
                return str.endsWith(".json");
            }
        });
    }

    public static File getStoredThrowableFile(UUID uuid) {
        return getStoredFile(uuid, ".throwable");
    }

    public static void removeStoredThrowableFile(UUID uuid) {
        File storedThrowableFile = getStoredThrowableFile(uuid);
        if (storedThrowableFile != null) {
            AppCenterLog.info("AppCenterCrashes", "Deleting throwable file " + storedThrowableFile.getName());
            FileManager.delete(storedThrowableFile);
        }
    }

    static File getStoredErrorLogFile(UUID uuid) {
        return getStoredFile(uuid, ".json");
    }

    public static void removeStoredErrorLogFile(UUID uuid) {
        File storedErrorLogFile = getStoredErrorLogFile(uuid);
        if (storedErrorLogFile != null) {
            AppCenterLog.info("AppCenterCrashes", "Deleting error log file " + storedErrorLogFile.getName());
            FileManager.delete(storedErrorLogFile);
        }
    }

    public static ErrorReport getErrorReportFromErrorLog(ManagedErrorLog managedErrorLog, String str) {
        ErrorReport errorReport = new ErrorReport();
        errorReport.setId(managedErrorLog.getId().toString());
        errorReport.setThreadName(managedErrorLog.getErrorThreadName());
        errorReport.setStackTrace(str);
        errorReport.setAppStartTime(managedErrorLog.getAppLaunchTimestamp());
        errorReport.setAppErrorTime(managedErrorLog.getTimestamp());
        errorReport.setDevice(managedErrorLog.getDevice());
        return errorReport;
    }

    private static File getStoredFile(final UUID uuid, final String str) {
        File[] listFiles = getErrorStorageDirectory().listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.5
            @Override // java.io.FilenameFilter
            public boolean accept(File file, String str2) {
                return str2.startsWith(uuid.toString()) && str2.endsWith(str);
            }
        });
        if (listFiles == null || listFiles.length <= 0) {
            return null;
        }
        return listFiles[0];
    }

    public static Exception getModelExceptionFromThrowable(Throwable th) {
        LinkedList<Throwable> linkedList = new LinkedList();
        while (th != null) {
            linkedList.add(th);
            th = th.getCause();
        }
        if (linkedList.size() > 16) {
            AppCenterLog.warn("AppCenterCrashes", "Crash causes truncated from " + linkedList.size() + " to 16 causes.");
            linkedList.subList(8, linkedList.size() - 8).clear();
        }
        Exception exception = null;
        Exception exception2 = null;
        for (Throwable th2 : linkedList) {
            Exception exception3 = new Exception();
            exception3.setType(th2.getClass().getName());
            exception3.setMessage(th2.getMessage());
            exception3.setFrames(getModelFramesFromStackTrace(th2));
            if (exception == null) {
                exception = exception3;
            } else {
                exception2.setInnerExceptions(Collections.singletonList(exception3));
            }
            exception2 = exception3;
        }
        return exception;
    }

    private static List<StackFrame> getModelFramesFromStackTrace(Throwable th) {
        StackTraceElement[] stackTrace = th.getStackTrace();
        if (stackTrace.length > 256) {
            StackTraceElement[] stackTraceElementArr = new StackTraceElement[256];
            System.arraycopy(stackTrace, 0, stackTraceElementArr, 0, ConnectionsManager.RequestFlagNeedQuickAck);
            System.arraycopy(stackTrace, stackTrace.length - ConnectionsManager.RequestFlagNeedQuickAck, stackTraceElementArr, ConnectionsManager.RequestFlagNeedQuickAck, ConnectionsManager.RequestFlagNeedQuickAck);
            th.setStackTrace(stackTraceElementArr);
            AppCenterLog.warn("AppCenterCrashes", "Crash frames truncated from " + stackTrace.length + " to 256 frames.");
            stackTrace = stackTraceElementArr;
        }
        return getModelFramesFromStackTrace(stackTrace);
    }

    private static List<StackFrame> getModelFramesFromStackTrace(StackTraceElement[] stackTraceElementArr) {
        ArrayList arrayList = new ArrayList();
        for (StackTraceElement stackTraceElement : stackTraceElementArr) {
            arrayList.add(getModelStackFrame(stackTraceElement));
        }
        return arrayList;
    }

    private static StackFrame getModelStackFrame(StackTraceElement stackTraceElement) {
        StackFrame stackFrame = new StackFrame();
        stackFrame.setClassName(stackTraceElement.getClassName());
        stackFrame.setMethodName(stackTraceElement.getMethodName());
        stackFrame.setLineNumber(Integer.valueOf(stackTraceElement.getLineNumber()));
        stackFrame.setFileName(stackTraceElement.getFileName());
        return stackFrame;
    }

    public static Map<String, String> validateProperties(Map<String, String> map, String str) {
        if (map == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<String, String> next = it.next();
            String key = next.getKey();
            String value = next.getValue();
            if (hashMap.size() >= 20) {
                AppCenterLog.warn("AppCenterCrashes", String.format("%s : properties cannot contain more than %s items. Skipping other properties.", str, 20));
                break;
            } else if (key == null || key.isEmpty()) {
                AppCenterLog.warn("AppCenterCrashes", String.format("%s : a property key cannot be null or empty. Property will be skipped.", str));
            } else if (value == null) {
                AppCenterLog.warn("AppCenterCrashes", String.format("%s : property '%s' : property value cannot be null. Property '%s' will be skipped.", str, key, key));
            } else {
                if (key.length() > 125) {
                    AppCenterLog.warn("AppCenterCrashes", String.format("%s : property '%s' : property key length cannot be longer than %s characters. Property key will be truncated.", str, key, 125));
                    key = key.substring(0, 125);
                }
                if (value.length() > 125) {
                    AppCenterLog.warn("AppCenterCrashes", String.format("%s : property '%s' : property value cannot be longer than %s characters. Property value will be truncated.", str, key, 125));
                    value = value.substring(0, 125);
                }
                hashMap.put(key, value);
            }
        }
        return hashMap;
    }

    public static void cleanPendingMinidumps() {
        FileManager.cleanDirectory(getPendingMinidumpDirectory());
    }

    /* JADX WARN: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:9:0x001a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.UUID parseLogFolderUuid(java.io.File r2) {
        /*
            boolean r0 = r2.isDirectory()
            if (r0 == 0) goto L17
            java.lang.String r2 = r2.getName()     // Catch: java.lang.IllegalArgumentException -> Lf
            java.util.UUID r2 = java.util.UUID.fromString(r2)     // Catch: java.lang.IllegalArgumentException -> Lf
            goto L18
        Lf:
            r2 = move-exception
            java.lang.String r0 = "AppCenterCrashes"
            java.lang.String r1 = "Cannot parse minidump folder name to UUID."
            com.microsoft.appcenter.utils.AppCenterLog.warn(r0, r1, r2)
        L17:
            r2 = 0
        L18:
            if (r2 != 0) goto L1e
            java.util.UUID r2 = java.util.UUID.randomUUID()
        L1e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.parseLogFolderUuid(java.io.File):java.util.UUID");
    }
}
