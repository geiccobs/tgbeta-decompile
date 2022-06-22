package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class DeviceExtension implements Model {
    private String localId;

    public String getLocalId() {
        return this.localId;
    }

    public void setLocalId(String str) {
        this.localId = str;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) {
        setLocalId(jSONObject.optString("localId", null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, "localId", getLocalId());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || DeviceExtension.class != obj.getClass()) {
            return false;
        }
        String str = this.localId;
        String str2 = ((DeviceExtension) obj).localId;
        return str != null ? str.equals(str2) : str2 == null;
    }

    public int hashCode() {
        String str = this.localId;
        if (str != null) {
            return str.hashCode();
        }
        return 0;
    }
}
