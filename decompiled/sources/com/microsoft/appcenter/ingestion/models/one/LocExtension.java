package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class LocExtension implements Model {
    private static final String TZ = "tz";
    private String tz;

    public String getTz() {
        return this.tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) {
        setTz(object.optString(TZ, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, TZ, getTz());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocExtension that = (LocExtension) o;
        String str = this.tz;
        return str != null ? str.equals(that.tz) : that.tz == null;
    }

    public int hashCode() {
        String str = this.tz;
        if (str != null) {
            return str.hashCode();
        }
        return 0;
    }
}
