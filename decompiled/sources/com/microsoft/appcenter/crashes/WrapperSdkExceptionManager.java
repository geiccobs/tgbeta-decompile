package com.microsoft.appcenter.crashes;

import android.content.Context;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import com.microsoft.appcenter.crashes.ingestion.models.Exception;
import com.microsoft.appcenter.crashes.model.ErrorReport;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
import com.microsoft.appcenter.utils.storage.FileManager;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/* loaded from: classes3.dex */
public class WrapperSdkExceptionManager {
    private static final String DATA_FILE_EXTENSION = ".dat";
    static final Map<String, String> sWrapperExceptionDataContainer = new HashMap();

    WrapperSdkExceptionManager() {
    }

    public static UUID saveWrapperException(Thread thread, Throwable throwable, Exception modelException, String rawSerializedException) {
        try {
            UUID errorId = Crashes.getInstance().saveUncaughtException(thread, throwable, modelException);
            if (errorId != null && rawSerializedException != null) {
                sWrapperExceptionDataContainer.put(errorId.toString(), rawSerializedException);
                File dataFile = getFile(errorId);
                FileManager.write(dataFile, rawSerializedException);
                AppCenterLog.debug(Crashes.LOG_TAG, "Saved raw wrapper exception data into " + dataFile);
            }
            return errorId;
        } catch (Exception e) {
            AppCenterLog.error(Crashes.LOG_TAG, "Failed to save wrapper exception data to file", e);
            return null;
        }
    }

    public static void deleteWrapperExceptionData(UUID errorId) {
        if (errorId == null) {
            AppCenterLog.error(Crashes.LOG_TAG, "Failed to delete wrapper exception data: null errorId");
            return;
        }
        File dataFile = getFile(errorId);
        if (dataFile.exists()) {
            String loadResult = loadWrapperExceptionData(errorId);
            if (loadResult == null) {
                AppCenterLog.error(Crashes.LOG_TAG, "Failed to load wrapper exception data.");
            }
            FileManager.delete(dataFile);
        }
    }

    public static String loadWrapperExceptionData(UUID errorId) {
        if (errorId == null) {
            AppCenterLog.error(Crashes.LOG_TAG, "Failed to load wrapper exception data: null errorId");
            return null;
        }
        Map<String, String> map = sWrapperExceptionDataContainer;
        String data = map.get(errorId.toString());
        if (data != null) {
            return data;
        }
        File dataFile = getFile(errorId);
        if (!dataFile.exists()) {
            return null;
        }
        String data2 = FileManager.read(dataFile);
        if (data2 != null) {
            map.put(errorId.toString(), data2);
        }
        return data2;
    }

    private static File getFile(UUID errorId) {
        File errorStorageDirectory = ErrorLogHelper.getErrorStorageDirectory();
        String filename = errorId.toString() + DATA_FILE_EXTENSION;
        return new File(errorStorageDirectory, filename);
    }

    public static String trackException(Exception modelException, Map<String, String> properties, Iterable<ErrorAttachmentLog> attachments) {
        return Crashes.getInstance().queueException(modelException, properties, attachments).toString();
    }

    public static void setAutomaticProcessing(boolean automaticProcessing) {
        Crashes.getInstance().setAutomaticProcessing(automaticProcessing);
    }

    public static AppCenterFuture<Collection<ErrorReport>> getUnprocessedErrorReports() {
        return Crashes.getInstance().getUnprocessedErrorReports();
    }

    public static AppCenterFuture<Boolean> sendCrashReportsOrAwaitUserConfirmation(Collection<String> filteredReportIds) {
        return Crashes.getInstance().sendCrashReportsOrAwaitUserConfirmation(filteredReportIds);
    }

    public static ErrorReport buildHandledErrorReport(Context context, String errorReportId) {
        ErrorReport report = new ErrorReport();
        report.setId(errorReportId);
        report.setAppErrorTime(new Date());
        report.setAppStartTime(new Date(Crashes.getInstance().getInitializeTimestamp()));
        try {
            report.setDevice(Crashes.getInstance().getDeviceInfo(context));
        } catch (DeviceInfoHelper.DeviceInfoException e) {
            AppCenterLog.warn(Crashes.LOG_TAG, "Handled error report cannot get device info, errorReportId=" + errorReportId);
        }
        return report;
    }

    public static void sendErrorAttachments(String errorReportId, Iterable<ErrorAttachmentLog> attachments) {
        Crashes.getInstance().sendErrorAttachments(errorReportId, attachments);
    }
}
