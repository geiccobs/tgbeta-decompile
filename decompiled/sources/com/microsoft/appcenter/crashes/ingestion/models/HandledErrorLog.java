package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.ingestion.models.LogWithProperties;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class HandledErrorLog extends LogWithProperties {
    private Exception exception;
    private UUID id;

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return "handledError";
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override // com.microsoft.appcenter.ingestion.models.LogWithProperties, com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setId(UUID.fromString(jSONObject.getString("id")));
        if (jSONObject.has("exception")) {
            JSONObject jSONObject2 = jSONObject.getJSONObject("exception");
            Exception exception = new Exception();
            exception.read(jSONObject2);
            setException(exception);
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.LogWithProperties, com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        jSONStringer.key("id").value(getId());
        if (getException() != null) {
            jSONStringer.key("exception").object();
            this.exception.write(jSONStringer);
            jSONStringer.endObject();
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.LogWithProperties, com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || HandledErrorLog.class != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        HandledErrorLog handledErrorLog = (HandledErrorLog) obj;
        UUID uuid = this.id;
        if (uuid == null ? handledErrorLog.id != null : !uuid.equals(handledErrorLog.id)) {
            return false;
        }
        Exception exception = this.exception;
        Exception exception2 = handledErrorLog.exception;
        return exception != null ? exception.equals(exception2) : exception2 == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.LogWithProperties, com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int hashCode = super.hashCode() * 31;
        UUID uuid = this.id;
        int i = 0;
        int hashCode2 = (hashCode + (uuid != null ? uuid.hashCode() : 0)) * 31;
        Exception exception = this.exception;
        if (exception != null) {
            i = exception.hashCode();
        }
        return hashCode2 + i;
    }
}
