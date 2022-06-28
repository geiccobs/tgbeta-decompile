package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.ingestion.models.LogWithProperties;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class HandledErrorLog extends LogWithProperties {
    private static final String EXCEPTION = "exception";
    public static final String TYPE = "handledError";
    private Exception exception;
    private UUID id;

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return TYPE;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override // com.microsoft.appcenter.ingestion.models.LogWithProperties, com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        super.read(object);
        setId(UUID.fromString(object.getString("id")));
        if (object.has(EXCEPTION)) {
            JSONObject jException = object.getJSONObject(EXCEPTION);
            Exception exception = new Exception();
            exception.read(jException);
            setException(exception);
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.LogWithProperties, com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        super.write(writer);
        writer.key("id").value(getId());
        if (getException() != null) {
            writer.key(EXCEPTION).object();
            this.exception.write(writer);
            writer.endObject();
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.LogWithProperties, com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        HandledErrorLog errorLog = (HandledErrorLog) o;
        UUID uuid = this.id;
        if (uuid == null ? errorLog.id != null : !uuid.equals(errorLog.id)) {
            return false;
        }
        Exception exception = this.exception;
        return exception != null ? exception.equals(errorLog.exception) : errorLog.exception == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.LogWithProperties, com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int result = super.hashCode();
        int i = result * 31;
        UUID uuid = this.id;
        int i2 = 0;
        int result2 = i + (uuid != null ? uuid.hashCode() : 0);
        int result3 = result2 * 31;
        Exception exception = this.exception;
        if (exception != null) {
            i2 = exception.hashCode();
        }
        return result3 + i2;
    }
}
