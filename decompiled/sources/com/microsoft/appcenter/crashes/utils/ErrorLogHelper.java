package com.microsoft.appcenter.crashes.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.crashes.Crashes;
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
import java.io.IOException;
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
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class ErrorLogHelper {
    static final int CAUSE_LIMIT = 16;
    private static final int CAUSE_LIMIT_HALF = 8;
    public static final String DEVICE_INFO_FILE = "deviceInfo";
    static final String ERROR_DIRECTORY = "error";
    public static final String ERROR_LOG_FILE_EXTENSION = ".json";
    public static final int FRAME_LIMIT = 256;
    private static final int FRAME_LIMIT_HALF = 128;
    private static final int MAX_PROPERTY_COUNT = 20;
    public static final int MAX_PROPERTY_ITEM_LENGTH = 125;
    private static final String MINIDUMP_DIRECTORY = "minidump";
    public static final String MINIDUMP_FILE_EXTENSION = ".dmp";
    private static final String NEW_MINIDUMP_DIRECTORY = "new";
    private static final String PENDING_MINIDUMP_DIRECTORY = "pending";
    public static final String THROWABLE_FILE_EXTENSION = ".throwable";
    private static File sErrorLogDirectory;
    private static File sNewMinidumpDirectory;
    private static File sPendingMinidumpDirectory;

    public static ManagedErrorLog createErrorLog(Context context, Thread thread, Throwable throwable, Map<Thread, StackTraceElement[]> allStackTraces, long initializeTimestamp) {
        return createErrorLog(context, thread, getModelExceptionFromThrowable(throwable), allStackTraces, initializeTimestamp, true);
    }

    public static ManagedErrorLog createErrorLog(Context context, Thread thread, Exception exception, Map<Thread, StackTraceElement[]> allStackTraces, long initializeTimestamp, boolean fatal) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        ManagedErrorLog errorLog = new ManagedErrorLog();
        errorLog.setId(UUID.randomUUID());
        errorLog.setTimestamp(new Date());
        errorLog.setUserId(UserIdContext.getInstance().getUserId());
        try {
            errorLog.setDevice(DeviceInfoHelper.getDeviceInfo(context));
        } catch (DeviceInfoHelper.DeviceInfoException e) {
            AppCenterLog.error(Crashes.LOG_TAG, "Could not attach device properties snapshot to error log, will attach at sending time", e);
        }
        errorLog.setProcessId(Integer.valueOf(Process.myPid()));
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        if (activityManager != null && (runningAppProcesses = activityManager.getRunningAppProcesses()) != null) {
            for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
                if (info.pid == Process.myPid()) {
                    errorLog.setProcessName(info.processName);
                }
            }
        }
        if (errorLog.getProcessName() == null) {
            errorLog.setProcessName("");
        }
        errorLog.setArchitecture(getArchitecture());
        errorLog.setErrorThreadId(Long.valueOf(thread.getId()));
        errorLog.setErrorThreadName(thread.getName());
        errorLog.setFatal(Boolean.valueOf(fatal));
        errorLog.setAppLaunchTimestamp(new Date(initializeTimestamp));
        errorLog.setException(exception);
        List<Thread> threads = new ArrayList<>(allStackTraces.size());
        for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
            Thread javaThread = new Thread();
            javaThread.setId(entry.getKey().getId());
            javaThread.setName(entry.getKey().getName());
            javaThread.setFrames(getModelFramesFromStackTrace(entry.getValue()));
            threads.add(javaThread);
        }
        errorLog.setThreads(threads);
        return errorLog;
    }

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
            File errorStorageDirectory = getErrorStorageDirectory();
            File minidumpDirectory = new File(errorStorageDirectory.getAbsolutePath(), MINIDUMP_DIRECTORY);
            file = new File(minidumpDirectory, NEW_MINIDUMP_DIRECTORY);
        }
        return file;
    }

    public static synchronized File getNewMinidumpSubfolder() {
        File minidumpDirectory;
        synchronized (ErrorLogHelper.class) {
            if (sNewMinidumpDirectory == null) {
                File minidumpDirectory2 = getNewMinidumpDirectory();
                File file = new File(minidumpDirectory2, UUID.randomUUID().toString());
                sNewMinidumpDirectory = file;
                FileManager.mkdir(file.getPath());
            }
            minidumpDirectory = sNewMinidumpDirectory;
        }
        return minidumpDirectory;
    }

    public static synchronized File getNewMinidumpSubfolderWithContextData(Context context) {
        File directorySubfolder;
        synchronized (ErrorLogHelper.class) {
            directorySubfolder = getNewMinidumpSubfolder();
            File deviceInfoFile = new File(directorySubfolder, DEVICE_INFO_FILE);
            try {
                Device deviceInfo = DeviceInfoHelper.getDeviceInfo(context);
                deviceInfo.setWrapperSdkName(Constants.WRAPPER_SDK_NAME_NDK);
                JSONStringer writer = new JSONStringer();
                writer.object();
                deviceInfo.write(writer);
                writer.endObject();
                String deviceInfoString = writer.toString();
                FileManager.write(deviceInfoFile, deviceInfoString);
            } catch (DeviceInfoHelper.DeviceInfoException | IOException | JSONException e) {
                AppCenterLog.error(Crashes.LOG_TAG, "Failed to store device info in a minidump folder.", e);
                deviceInfoFile.delete();
            }
        }
        return directorySubfolder;
    }

    public static synchronized File getPendingMinidumpDirectory() {
        File errorStorageDirectory;
        synchronized (ErrorLogHelper.class) {
            if (sPendingMinidumpDirectory == null) {
                File errorStorageDirectory2 = getErrorStorageDirectory();
                File minidumpDirectory = new File(errorStorageDirectory2.getAbsolutePath(), MINIDUMP_DIRECTORY);
                File file = new File(minidumpDirectory, PENDING_MINIDUMP_DIRECTORY);
                sPendingMinidumpDirectory = file;
                FileManager.mkdir(file.getPath());
            }
            errorStorageDirectory = sPendingMinidumpDirectory;
        }
        return errorStorageDirectory;
    }

    public static File[] getStoredErrorLogFiles() {
        File[] files = getErrorStorageDirectory().listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.1
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String filename) {
                return filename.endsWith(ErrorLogHelper.ERROR_LOG_FILE_EXTENSION);
            }
        });
        return files != null ? files : new File[0];
    }

    public static File[] getNewMinidumpFiles() {
        File[] files = getNewMinidumpDirectory().listFiles();
        return files != null ? files : new File[0];
    }

    public static Device getStoredDeviceInfo(File logFolder) {
        File[] files = logFolder.listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.2
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String filename) {
                return filename.equals(ErrorLogHelper.DEVICE_INFO_FILE);
            }
        });
        if (files == null || files.length == 0) {
            AppCenterLog.warn(Crashes.LOG_TAG, "No stored deviceinfo file found in a minidump folder.");
            return null;
        }
        File deviceInfoFile = files[0];
        String deviceInfoString = FileManager.read(deviceInfoFile);
        if (deviceInfoString == null) {
            AppCenterLog.error(Crashes.LOG_TAG, "Failed to read stored device info.");
            return null;
        }
        return parseDevice(deviceInfoString);
    }

    static Device parseDevice(String deviceInfoString) {
        try {
            Device device = new Device();
            JSONObject jsonObject = new JSONObject(deviceInfoString);
            device.read(jsonObject);
            return device;
        } catch (JSONException e) {
            AppCenterLog.error(Crashes.LOG_TAG, "Failed to deserialize device info.", e);
            return null;
        }
    }

    public static void removeStaleMinidumpSubfolders() {
        File[] previousSubFolders = getNewMinidumpDirectory().listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.3
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String name) {
                if (ErrorLogHelper.sNewMinidumpDirectory != null) {
                    return !name.equals(ErrorLogHelper.sNewMinidumpDirectory.getName());
                }
                return true;
            }
        });
        if (previousSubFolders == null || previousSubFolders.length == 0) {
            AppCenterLog.debug(Crashes.LOG_TAG, "No previous minidump sub-folders.");
            return;
        }
        for (File file : previousSubFolders) {
            FileManager.deleteDirectory(file);
        }
    }

    public static void removeMinidumpFolder() {
        File errorStorageDirectory = getErrorStorageDirectory();
        File minidumpDirectory = new File(errorStorageDirectory.getAbsolutePath(), MINIDUMP_DIRECTORY);
        FileManager.deleteDirectory(minidumpDirectory);
    }

    public static File getLastErrorLogFile() {
        return FileManager.lastModifiedFile(getErrorStorageDirectory(), new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.4
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String filename) {
                return filename.endsWith(ErrorLogHelper.ERROR_LOG_FILE_EXTENSION);
            }
        });
    }

    public static File getStoredThrowableFile(UUID id) {
        return getStoredFile(id, THROWABLE_FILE_EXTENSION);
    }

    public static void removeStoredThrowableFile(UUID id) {
        File file = getStoredThrowableFile(id);
        if (file != null) {
            AppCenterLog.info(Crashes.LOG_TAG, "Deleting throwable file " + file.getName());
            FileManager.delete(file);
        }
    }

    static File getStoredErrorLogFile(UUID id) {
        return getStoredFile(id, ERROR_LOG_FILE_EXTENSION);
    }

    public static void removeStoredErrorLogFile(UUID id) {
        File file = getStoredErrorLogFile(id);
        if (file != null) {
            AppCenterLog.info(Crashes.LOG_TAG, "Deleting error log file " + file.getName());
            FileManager.delete(file);
        }
    }

    public static ErrorReport getErrorReportFromErrorLog(ManagedErrorLog log, String stackTrace) {
        ErrorReport report = new ErrorReport();
        report.setId(log.getId().toString());
        report.setThreadName(log.getErrorThreadName());
        report.setStackTrace(stackTrace);
        report.setAppStartTime(log.getAppLaunchTimestamp());
        report.setAppErrorTime(log.getTimestamp());
        report.setDevice(log.getDevice());
        return report;
    }

    static void setErrorLogDirectory(File file) {
        sErrorLogDirectory = file;
    }

    private static File getStoredFile(final UUID id, final String extension) {
        File[] files = getErrorStorageDirectory().listFiles(new FilenameFilter() { // from class: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.5
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String filename) {
                return filename.startsWith(id.toString()) && filename.endsWith(extension);
            }
        });
        if (files == null || files.length <= 0) {
            return null;
        }
        return files[0];
    }

    public static Exception getModelExceptionFromThrowable(Throwable t) {
        Exception topException = null;
        Exception parentException = null;
        List<Throwable> causeChain = new LinkedList<>();
        for (Throwable cause = t; cause != null; cause = cause.getCause()) {
            causeChain.add(cause);
        }
        if (causeChain.size() > 16) {
            AppCenterLog.warn(Crashes.LOG_TAG, "Crash causes truncated from " + causeChain.size() + " to 16 causes.");
            causeChain.subList(8, causeChain.size() - 8).clear();
        }
        for (Throwable cause2 : causeChain) {
            Exception exception = new Exception();
            exception.setType(cause2.getClass().getName());
            exception.setMessage(cause2.getMessage());
            exception.setFrames(getModelFramesFromStackTrace(cause2));
            if (topException == null) {
                topException = exception;
            } else {
                parentException.setInnerExceptions(Collections.singletonList(exception));
            }
            parentException = exception;
        }
        return topException;
    }

    private static List<StackFrame> getModelFramesFromStackTrace(Throwable throwable) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace.length > 256) {
            StackTraceElement[] stackTraceTruncated = new StackTraceElement[256];
            System.arraycopy(stackTrace, 0, stackTraceTruncated, 0, 128);
            System.arraycopy(stackTrace, stackTrace.length - 128, stackTraceTruncated, 128, 128);
            throwable.setStackTrace(stackTraceTruncated);
            AppCenterLog.warn(Crashes.LOG_TAG, "Crash frames truncated from " + stackTrace.length + " to " + stackTraceTruncated.length + " frames.");
            stackTrace = stackTraceTruncated;
        }
        return getModelFramesFromStackTrace(stackTrace);
    }

    private static List<StackFrame> getModelFramesFromStackTrace(StackTraceElement[] stackTrace) {
        List<StackFrame> stackFrames = new ArrayList<>();
        for (StackTraceElement stackTraceElement : stackTrace) {
            stackFrames.add(getModelStackFrame(stackTraceElement));
        }
        return stackFrames;
    }

    private static StackFrame getModelStackFrame(StackTraceElement stackTraceElement) {
        StackFrame stackFrame = new StackFrame();
        stackFrame.setClassName(stackTraceElement.getClassName());
        stackFrame.setMethodName(stackTraceElement.getMethodName());
        stackFrame.setLineNumber(Integer.valueOf(stackTraceElement.getLineNumber()));
        stackFrame.setFileName(stackTraceElement.getFileName());
        return stackFrame;
    }

    public static Map<String, String> validateProperties(Map<String, String> properties, String logType) {
        if (properties == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        Iterator<Map.Entry<String, String>> it = properties.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<String, String> property = it.next();
            String key = property.getKey();
            String value = property.getValue();
            if (result.size() >= 20) {
                String message = String.format("%s : properties cannot contain more than %s items. Skipping other properties.", logType, 20);
                AppCenterLog.warn(Crashes.LOG_TAG, message);
                break;
            } else if (key == null || key.isEmpty()) {
                String message2 = String.format("%s : a property key cannot be null or empty. Property will be skipped.", logType);
                AppCenterLog.warn(Crashes.LOG_TAG, message2);
            } else if (value == null) {
                String message3 = String.format("%s : property '%s' : property value cannot be null. Property '%s' will be skipped.", logType, key, key);
                AppCenterLog.warn(Crashes.LOG_TAG, message3);
            } else {
                if (key.length() > 125) {
                    String message4 = String.format("%s : property '%s' : property key length cannot be longer than %s characters. Property key will be truncated.", logType, key, Integer.valueOf((int) MAX_PROPERTY_ITEM_LENGTH));
                    AppCenterLog.warn(Crashes.LOG_TAG, message4);
                    key = key.substring(0, MAX_PROPERTY_ITEM_LENGTH);
                }
                if (value.length() > 125) {
                    String message5 = String.format("%s : property '%s' : property value cannot be longer than %s characters. Property value will be truncated.", logType, key, Integer.valueOf((int) MAX_PROPERTY_ITEM_LENGTH));
                    AppCenterLog.warn(Crashes.LOG_TAG, message5);
                    value = value.substring(0, MAX_PROPERTY_ITEM_LENGTH);
                }
                result.put(key, value);
            }
        }
        return result;
    }

    public static void cleanPendingMinidumps() {
        FileManager.cleanDirectory(getPendingMinidumpDirectory());
    }

    public static UUID parseLogFolderUuid(File logFolder) {
        UUID uuid = null;
        if (logFolder.isDirectory()) {
            try {
                uuid = UUID.fromString(logFolder.getName());
            } catch (IllegalArgumentException e) {
                AppCenterLog.warn(Crashes.LOG_TAG, "Cannot parse minidump folder name to UUID.", e);
            }
        }
        return uuid == null ? UUID.randomUUID() : uuid;
    }

    public static void clearStaticState() {
        sNewMinidumpDirectory = null;
        sErrorLogDirectory = null;
        sPendingMinidumpDirectory = null;
    }
}
