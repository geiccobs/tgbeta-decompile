package com.microsoft.appcenter.ingestion.models;

import java.util.List;
/* loaded from: classes.dex */
public class LogContainer {
    private List<Log> logs;

    public List<Log> getLogs() {
        return this.logs;
    }

    public void setLogs(List<Log> list) {
        this.logs = list;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || LogContainer.class != obj.getClass()) {
            return false;
        }
        List<Log> list = this.logs;
        List<Log> list2 = ((LogContainer) obj).logs;
        return list != null ? list.equals(list2) : list2 == null;
    }

    public int hashCode() {
        List<Log> list = this.logs;
        if (list != null) {
            return list.hashCode();
        }
        return 0;
    }
}
