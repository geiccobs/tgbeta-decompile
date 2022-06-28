package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class NetExtension implements Model {
    private static final String PROVIDER = "provider";
    private String provider;

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) {
        setProvider(object.optString(PROVIDER, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, PROVIDER, getProvider());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetExtension that = (NetExtension) o;
        String str = this.provider;
        return str != null ? str.equals(that.provider) : that.provider == null;
    }

    public int hashCode() {
        String str = this.provider;
        if (str != null) {
            return str.hashCode();
        }
        return 0;
    }
}
