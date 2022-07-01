package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class NetExtension implements Model {
    private String provider;

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String str) {
        this.provider = str;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) {
        setProvider(jSONObject.optString("provider", null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, "provider", getProvider());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || NetExtension.class != obj.getClass()) {
            return false;
        }
        String str = this.provider;
        String str2 = ((NetExtension) obj).provider;
        return str != null ? str.equals(str2) : str2 == null;
    }

    public int hashCode() {
        String str = this.provider;
        if (str != null) {
            return str.hashCode();
        }
        return 0;
    }
}
