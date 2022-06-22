package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class UserExtension implements Model {
    private String localId;
    private String locale;

    public String getLocalId() {
        return this.localId;
    }

    public void setLocalId(String str) {
        this.localId = str;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String str) {
        this.locale = str;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) {
        setLocalId(jSONObject.optString("localId", null));
        setLocale(jSONObject.optString("locale", null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, "localId", getLocalId());
        JSONUtils.write(jSONStringer, "locale", getLocale());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || UserExtension.class != obj.getClass()) {
            return false;
        }
        UserExtension userExtension = (UserExtension) obj;
        String str = this.localId;
        if (str == null ? userExtension.localId != null : !str.equals(userExtension.localId)) {
            return false;
        }
        String str2 = this.locale;
        String str3 = userExtension.locale;
        return str2 != null ? str2.equals(str3) : str3 == null;
    }

    public int hashCode() {
        String str = this.localId;
        int i = 0;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.locale;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return hashCode + i;
    }
}
