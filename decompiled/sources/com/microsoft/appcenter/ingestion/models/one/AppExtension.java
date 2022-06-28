package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class AppExtension implements Model {
    private static final String ID = "id";
    private static final String LOCALE = "locale";
    private static final String NAME = "name";
    private static final String USER_ID = "userId";
    private static final String VER = "ver";
    private String id;
    private String locale;
    private String name;
    private String userId;
    private String ver;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) {
        setId(object.optString("id", null));
        setVer(object.optString(VER, null));
        setName(object.optString("name", null));
        setLocale(object.optString(LOCALE, null));
        setUserId(object.optString(USER_ID, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, "id", getId());
        JSONUtils.write(writer, VER, getVer());
        JSONUtils.write(writer, "name", getName());
        JSONUtils.write(writer, LOCALE, getLocale());
        JSONUtils.write(writer, USER_ID, getUserId());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppExtension that = (AppExtension) o;
        String str = this.id;
        if (str == null ? that.id != null : !str.equals(that.id)) {
            return false;
        }
        String str2 = this.ver;
        if (str2 == null ? that.ver != null : !str2.equals(that.ver)) {
            return false;
        }
        String str3 = this.name;
        if (str3 == null ? that.name != null : !str3.equals(that.name)) {
            return false;
        }
        String str4 = this.locale;
        if (str4 == null ? that.locale != null : !str4.equals(that.locale)) {
            return false;
        }
        String str5 = this.userId;
        return str5 != null ? str5.equals(that.userId) : that.userId == null;
    }

    public int hashCode() {
        String str = this.id;
        int i = 0;
        int result = str != null ? str.hashCode() : 0;
        int i2 = result * 31;
        String str2 = this.ver;
        int result2 = i2 + (str2 != null ? str2.hashCode() : 0);
        int result3 = result2 * 31;
        String str3 = this.name;
        int result4 = (result3 + (str3 != null ? str3.hashCode() : 0)) * 31;
        String str4 = this.locale;
        int result5 = (result4 + (str4 != null ? str4.hashCode() : 0)) * 31;
        String str5 = this.userId;
        if (str5 != null) {
            i = str5.hashCode();
        }
        return result5 + i;
    }
}
