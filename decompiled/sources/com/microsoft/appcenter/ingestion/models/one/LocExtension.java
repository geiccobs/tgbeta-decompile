package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class LocExtension implements Model {
    private String tz;

    public String getTz() {
        return this.tz;
    }

    public void setTz(String str) {
        this.tz = str;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) {
        setTz(jSONObject.optString("tz", null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, "tz", getTz());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || LocExtension.class != obj.getClass()) {
            return false;
        }
        String str = this.tz;
        String str2 = ((LocExtension) obj).tz;
        return str != null ? str.equals(str2) : str2 == null;
    }

    public int hashCode() {
        String str = this.tz;
        if (str != null) {
            return str.hashCode();
        }
        return 0;
    }
}
