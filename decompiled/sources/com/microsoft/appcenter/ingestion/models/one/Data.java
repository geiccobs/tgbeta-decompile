package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class Data implements Model {
    private final JSONObject mProperties = new JSONObject();

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        JSONArray names = jSONObject.names();
        if (names != null) {
            for (int i = 0; i < names.length(); i++) {
                String string = names.getString(i);
                this.mProperties.put(string, jSONObject.get(string));
            }
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, "baseType", this.mProperties.optString("baseType", null));
        JSONUtils.write(jSONStringer, "baseData", this.mProperties.optJSONObject("baseData"));
        JSONArray names = this.mProperties.names();
        if (names != null) {
            for (int i = 0; i < names.length(); i++) {
                String string = names.getString(i);
                if (!string.equals("baseType") && !string.equals("baseData")) {
                    jSONStringer.key(string).value(this.mProperties.get(string));
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && Data.class == obj.getClass()) {
            return this.mProperties.toString().equals(((Data) obj).mProperties.toString());
        }
        return false;
    }

    public int hashCode() {
        return this.mProperties.toString().hashCode();
    }
}
