package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class CustomPropertiesLog extends AbstractLog {
    private Map<String, Object> properties;

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return "customProperties";
    }

    private static Map<String, Object> readProperties(JSONObject jSONObject) throws JSONException {
        JSONArray jSONArray = jSONObject.getJSONArray("properties");
        HashMap hashMap = new HashMap();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
            hashMap.put(jSONObject2.getString("name"), readPropertyValue(jSONObject2));
        }
        return hashMap;
    }

    private static Object readPropertyValue(JSONObject jSONObject) throws JSONException {
        String string = jSONObject.getString("type");
        if (string.equals("clear")) {
            return null;
        }
        if (string.equals("boolean")) {
            return Boolean.valueOf(jSONObject.getBoolean("value"));
        }
        if (string.equals("number")) {
            Object obj = jSONObject.get("value");
            if (!(obj instanceof Number)) {
                throw new JSONException("Invalid value type");
            }
            return obj;
        } else if (string.equals("dateTime")) {
            return JSONDateUtils.toDate(jSONObject.getString("value"));
        } else {
            if (string.equals("string")) {
                return jSONObject.getString("value");
            }
            throw new JSONException("Invalid value type");
        }
    }

    private static void writeProperties(JSONStringer jSONStringer, Map<String, Object> map) throws JSONException {
        if (map != null) {
            jSONStringer.key("properties").array();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jSONStringer.object();
                JSONUtils.write(jSONStringer, "name", entry.getKey());
                writePropertyValue(jSONStringer, entry.getValue());
                jSONStringer.endObject();
            }
            jSONStringer.endArray();
            return;
        }
        throw new JSONException("Properties cannot be null");
    }

    private static void writePropertyValue(JSONStringer jSONStringer, Object obj) throws JSONException {
        if (obj == null) {
            JSONUtils.write(jSONStringer, "type", "clear");
        } else if (obj instanceof Boolean) {
            JSONUtils.write(jSONStringer, "type", "boolean");
            JSONUtils.write(jSONStringer, "value", obj);
        } else if (obj instanceof Number) {
            JSONUtils.write(jSONStringer, "type", "number");
            JSONUtils.write(jSONStringer, "value", obj);
        } else if (obj instanceof Date) {
            JSONUtils.write(jSONStringer, "type", "dateTime");
            JSONUtils.write(jSONStringer, "value", JSONDateUtils.toString((Date) obj));
        } else if (obj instanceof String) {
            JSONUtils.write(jSONStringer, "type", "string");
            JSONUtils.write(jSONStringer, "value", obj);
        } else {
            throw new JSONException("Invalid value type");
        }
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, Object> map) {
        this.properties = map;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setProperties(readProperties(jSONObject));
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        writeProperties(jSONStringer, getProperties());
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || CustomPropertiesLog.class != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        Map<String, Object> map = this.properties;
        Map<String, Object> map2 = ((CustomPropertiesLog) obj).properties;
        return map != null ? map.equals(map2) : map2 == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int hashCode = super.hashCode() * 31;
        Map<String, Object> map = this.properties;
        return hashCode + (map != null ? map.hashCode() : 0);
    }
}
