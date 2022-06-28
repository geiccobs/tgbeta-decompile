package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class UserExtension implements Model {
    private static final String LOCALE = "locale";
    private static final String LOCAL_ID = "localId";
    private String localId;
    private String locale;

    public String getLocalId() {
        return this.localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) {
        setLocalId(object.optString(LOCAL_ID, null));
        setLocale(object.optString(LOCALE, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, LOCAL_ID, getLocalId());
        JSONUtils.write(writer, LOCALE, getLocale());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserExtension that = (UserExtension) o;
        String str = this.localId;
        if (str == null ? that.localId != null : !str.equals(that.localId)) {
            return false;
        }
        String str2 = this.locale;
        return str2 != null ? str2.equals(that.locale) : that.locale == null;
    }

    public int hashCode() {
        String str = this.localId;
        int i = 0;
        int result = str != null ? str.hashCode() : 0;
        int i2 = result * 31;
        String str2 = this.locale;
        if (str2 != null) {
            i = str2.hashCode();
        }
        int result2 = i2 + i;
        return result2;
    }
}
