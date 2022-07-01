package com.microsoft.appcenter.crashes.model;

import com.microsoft.appcenter.ingestion.models.Device;
import java.util.Date;
/* loaded from: classes.dex */
public class ErrorReport {
    private Device device;

    public void setAppErrorTime(Date date) {
    }

    public void setAppStartTime(Date date) {
    }

    public void setId(String str) {
    }

    public void setStackTrace(String str) {
    }

    public void setThreadName(String str) {
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
