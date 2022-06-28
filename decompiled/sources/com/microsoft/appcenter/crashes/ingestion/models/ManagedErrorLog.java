package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.crashes.ingestion.models.json.ThreadFactory;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class ManagedErrorLog extends AbstractErrorLog {
    private static final String EXCEPTION = "exception";
    private static final String THREADS = "threads";
    public static final String TYPE = "managedError";
    private Exception exception;
    private List<Thread> threads;

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return TYPE;
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

    public void setThreads(List<Thread> threads) {
        this.threads = threads;
    }

    @Override // com.microsoft.appcenter.crashes.ingestion.models.AbstractErrorLog, com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        super.read(object);
        if (object.has(EXCEPTION)) {
            JSONObject jException = object.getJSONObject(EXCEPTION);
            Exception exception = new Exception();
            exception.read(jException);
            setException(exception);
        }
        setThreads(JSONUtils.readArray(object, THREADS, ThreadFactory.getInstance()));
    }

    @Override // com.microsoft.appcenter.crashes.ingestion.models.AbstractErrorLog, com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        super.write(writer);
        if (getException() != null) {
            writer.key(EXCEPTION).object();
            this.exception.write(writer);
            writer.endObject();
        }
        JSONUtils.writeArray(writer, THREADS, getThreads());
    }

    @Override // com.microsoft.appcenter.crashes.ingestion.models.AbstractErrorLog, com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        ManagedErrorLog that = (ManagedErrorLog) o;
        Exception exception = this.exception;
        if (exception == null ? that.exception != null : !exception.equals(that.exception)) {
            return false;
        }
        List<Thread> list = this.threads;
        return list != null ? list.equals(that.threads) : that.threads == null;
    }

    @Override // com.microsoft.appcenter.crashes.ingestion.models.AbstractErrorLog, com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int result = super.hashCode();
        int i = result * 31;
        Exception exception = this.exception;
        int i2 = 0;
        int result2 = i + (exception != null ? exception.hashCode() : 0);
        int result3 = result2 * 31;
        List<Thread> list = this.threads;
        if (list != null) {
            i2 = list.hashCode();
        }
        return result3 + i2;
    }
}
