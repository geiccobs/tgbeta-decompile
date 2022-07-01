package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class AppExtension implements Model {
    private String id;
    private String locale;
    private String name;
    private String userId;
    private String ver;

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getVer() {
        return this.ver;
    }

    public void setVer(String str) {
        this.ver = str;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String str) {
        this.locale = str;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String str) {
        this.userId = str;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) {
        setId(jSONObject.optString("id", null));
        setVer(jSONObject.optString("ver", null));
        setName(jSONObject.optString("name", null));
        setLocale(jSONObject.optString("locale", null));
        setUserId(jSONObject.optString("userId", null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, "id", getId());
        JSONUtils.write(jSONStringer, "ver", getVer());
        JSONUtils.write(jSONStringer, "name", getName());
        JSONUtils.write(jSONStringer, "locale", getLocale());
        JSONUtils.write(jSONStringer, "userId", getUserId());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || AppExtension.class != obj.getClass()) {
            return false;
        }
        AppExtension appExtension = (AppExtension) obj;
        String str = this.id;
        if (str == null ? appExtension.id != null : !str.equals(appExtension.id)) {
            return false;
        }
        String str2 = this.ver;
        if (str2 == null ? appExtension.ver != null : !str2.equals(appExtension.ver)) {
            return false;
        }
        String str3 = this.name;
        if (str3 == null ? appExtension.name != null : !str3.equals(appExtension.name)) {
            return false;
        }
        String str4 = this.locale;
        if (str4 == null ? appExtension.locale != null : !str4.equals(appExtension.locale)) {
            return false;
        }
        String str5 = this.userId;
        String str6 = appExtension.userId;
        return str5 != null ? str5.equals(str6) : str6 == null;
    }

    public int hashCode() {
        String str = this.id;
        int i = 0;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.ver;
        int hashCode2 = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.name;
        int hashCode3 = (hashCode2 + (str3 != null ? str3.hashCode() : 0)) * 31;
        String str4 = this.locale;
        int hashCode4 = (hashCode3 + (str4 != null ? str4.hashCode() : 0)) * 31;
        String str5 = this.userId;
        if (str5 != null) {
            i = str5.hashCode();
        }
        return hashCode4 + i;
    }
}
