package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class OsExtension implements Model {
    private static final String NAME = "name";
    private static final String VER = "ver";
    private String name;
    private String ver;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVer() {
        return this.ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) {
        setName(object.optString("name", null));
        setVer(object.optString(VER, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, "name", getName());
        JSONUtils.write(writer, VER, getVer());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OsExtension that = (OsExtension) o;
        String str = this.name;
        if (str == null ? that.name != null : !str.equals(that.name)) {
            return false;
        }
        String str2 = this.ver;
        return str2 != null ? str2.equals(that.ver) : that.ver == null;
    }

    public int hashCode() {
        String str = this.name;
        int i = 0;
        int result = str != null ? str.hashCode() : 0;
        int i2 = result * 31;
        String str2 = this.ver;
        if (str2 != null) {
            i = str2.hashCode();
        }
        int result2 = i2 + i;
        return result2;
    }
}
