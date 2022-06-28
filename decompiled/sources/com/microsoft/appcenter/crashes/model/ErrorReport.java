package com.microsoft.appcenter.crashes.model;

import com.microsoft.appcenter.ingestion.models.Device;
import java.util.Date;
/* loaded from: classes3.dex */
public class ErrorReport {
    private Date appErrorTime;
    private Date appStartTime;
    private Device device;
    private String id;
    private String stackTrace;
    private String threadName;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Deprecated
    public Throwable getThrowable() {
        return null;
    }

    public Date getAppStartTime() {
        return this.appStartTime;
    }

    public void setAppStartTime(Date appStartTime) {
        this.appStartTime = appStartTime;
    }

    public Date getAppErrorTime() {
        return this.appErrorTime;
    }

    public void setAppErrorTime(Date appErrorTime) {
        this.appErrorTime = appErrorTime;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
