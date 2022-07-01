package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.crashes.ingestion.models.json.ThreadFactory;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class ManagedErrorLog extends AbstractErrorLog {
    private Exception exception;
    private List<Thread> threads;

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return "managedError";
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public List<Thread> getThreads() {
        return this.threads;
    }

    public void setThreads(List<Thread> list) {
        this.threads = list;
    }

    @Override // com.microsoft.appcenter.crashes.ingestion.models.AbstractErrorLog, com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        if (jSONObject.has("exception")) {
            JSONObject jSONObject2 = jSONObject.getJSONObject("exception");
            Exception exception = new Exception();
            exception.read(jSONObject2);
            setException(exception);
        }
        setThreads(JSONUtils.readArray(jSONObject, "threads", ThreadFactory.getInstance()));
    }

    @Override // com.microsoft.appcenter.crashes.ingestion.models.AbstractErrorLog, com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        if (getException() != null) {
            jSONStringer.key("exception").object();
            this.exception.write(jSONStringer);
            jSONStringer.endObject();
        }
        JSONUtils.writeArray(jSONStringer, "threads", getThreads());
    }

    @Override // com.microsoft.appcenter.crashes.ingestion.models.AbstractErrorLog, com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || ManagedErrorLog.class != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        ManagedErrorLog managedErrorLog = (ManagedErrorLog) obj;
        Exception exception = this.exception;
        if (exception == null ? managedErrorLog.exception != null : !exception.equals(managedErrorLog.exception)) {
            return false;
        }
        List<Thread> list = this.threads;
        List<Thread> list2 = managedErrorLog.threads;
        return list != null ? list.equals(list2) : list2 == null;
    }

    @Override // com.microsoft.appcenter.crashes.ingestion.models.AbstractErrorLog, com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int hashCode = super.hashCode() * 31;
        Exception exception = this.exception;
        int i = 0;
        int hashCode2 = (hashCode + (exception != null ? exception.hashCode() : 0)) * 31;
        List<Thread> list = this.threads;
        if (list != null) {
            i = list.hashCode();
        }
        return hashCode2 + i;
    }
}
