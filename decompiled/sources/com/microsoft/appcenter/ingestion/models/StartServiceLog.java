package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class StartServiceLog extends AbstractLog {
    private List<String> services;

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return "startService";
    }

    public List<String> getServices() {
        return this.services;
    }

    public void setServices(List<String> list) {
        this.services = list;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setServices(JSONUtils.readStringArray(jSONObject, "services"));
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        JSONUtils.writeStringArray(jSONStringer, "services", getServices());
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || StartServiceLog.class != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        List<String> list = this.services;
        List<String> list2 = ((StartServiceLog) obj).services;
        return list != null ? list.equals(list2) : list2 == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int hashCode = super.hashCode() * 31;
        List<String> list = this.services;
        return hashCode + (list != null ? list.hashCode() : 0);
    }
}
