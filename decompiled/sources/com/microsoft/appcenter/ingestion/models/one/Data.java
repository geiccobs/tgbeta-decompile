package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class Data implements Model {
    static final String BASE_DATA = "baseData";
    static final String BASE_TYPE = "baseType";
    private final JSONObject mProperties = new JSONObject();

    public JSONObject getProperties() {
        return this.mProperties;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        JSONArray names = object.names();
        if (names != null) {
            for (int i = 0; i < names.length(); i++) {
                String name = names.getString(i);
                this.mProperties.put(name, object.get(name));
            }
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, BASE_TYPE, this.mProperties.optString(BASE_TYPE, null));
        JSONUtils.write(writer, BASE_DATA, this.mProperties.optJSONObject(BASE_DATA));
        JSONArray names = this.mProperties.names();
        if (names != null) {
            for (int i = 0; i < names.length(); i++) {
                String name = names.getString(i);
                if (!name.equals(BASE_TYPE) && !name.equals(BASE_DATA)) {
                    writer.key(name).value(this.mProperties.get(name));
                }
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Data data = (Data) o;
        return this.mProperties.toString().equals(data.mProperties.toString());
    }

    public int hashCode() {
        return this.mProperties.toString().hashCode();
    }
}
