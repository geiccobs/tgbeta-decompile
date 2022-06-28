package com.microsoft.appcenter.ingestion.models;

import java.util.List;
/* loaded from: classes3.dex */
public class LogContainer {
    private List<Log> logs;

    public List<Log> getLogs() {
        return this.logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LogContainer container = (LogContainer) o;
        List<Log> list = this.logs;
        return list != null ? list.equals(container.logs) : container.logs == null;
    }

    public int hashCode() {
        List<Log> list = this.logs;
        if (list != null) {
            return list.hashCode();
        }
        return 0;
    }
}
