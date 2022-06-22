package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public abstract class LogWithProperties extends AbstractLog {
    private Map<String, String> properties;

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> map) {
        this.properties = map;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setProperties(JSONUtils.readMap(jSONObject, "properties"));
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        JSONUtils.writeMap(jSONStringer, "properties", getProperties());
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        Map<String, String> map = this.properties;
        Map<String, String> map2 = ((LogWithProperties) obj).properties;
        return map != null ? map.equals(map2) : map2 == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int hashCode = super.hashCode() * 31;
        Map<String, String> map = this.properties;
        return hashCode + (map != null ? map.hashCode() : 0);
    }
}
