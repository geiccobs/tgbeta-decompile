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
/* loaded from: classes3.dex */
public class CustomPropertiesLog extends AbstractLog {
    private static final String PROPERTIES = "properties";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_TYPE = "type";
    private static final String PROPERTY_TYPE_BOOLEAN = "boolean";
    private static final String PROPERTY_TYPE_CLEAR = "clear";
    private static final String PROPERTY_TYPE_DATETIME = "dateTime";
    private static final String PROPERTY_TYPE_NUMBER = "number";
    private static final String PROPERTY_TYPE_STRING = "string";
    private static final String PROPERTY_VALUE = "value";
    public static final String TYPE = "customProperties";
    private Map<String, Object> properties;

    private static Map<String, Object> readProperties(JSONObject object) throws JSONException {
        JSONArray jArray = object.getJSONArray(PROPERTIES);
        Map<String, Object> properties = new HashMap<>();
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jProperty = jArray.getJSONObject(i);
            String key = jProperty.getString("name");
            Object value = readPropertyValue(jProperty);
            properties.put(key, value);
        }
        return properties;
    }

    private static Object readPropertyValue(JSONObject object) throws JSONException {
        String type = object.getString("type");
        if (type.equals(PROPERTY_TYPE_CLEAR)) {
            return null;
        }
        if (type.equals("boolean")) {
            Object value = Boolean.valueOf(object.getBoolean("value"));
            return value;
        } else if (type.equals(PROPERTY_TYPE_NUMBER)) {
            Object value2 = object.get("value");
            if (!(value2 instanceof Number)) {
                throw new JSONException("Invalid value type");
            }
            return value2;
        } else if (type.equals("dateTime")) {
            Object value3 = JSONDateUtils.toDate(object.getString("value"));
            return value3;
        } else if (type.equals("string")) {
            Object value4 = object.getString("value");
            return value4;
        } else {
            throw new JSONException("Invalid value type");
        }
    }

    private static void writeProperties(JSONStringer writer, Map<String, Object> properties) throws JSONException {
        if (properties != null) {
            writer.key(PROPERTIES).array();
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                writer.object();
                JSONUtils.write(writer, "name", property.getKey());
                writePropertyValue(writer, property.getValue());
                writer.endObject();
            }
            writer.endArray();
            return;
        }
        throw new JSONException("Properties cannot be null");
    }

    private static void writePropertyValue(JSONStringer writer, Object value) throws JSONException {
        if (value == null) {
            JSONUtils.write(writer, "type", PROPERTY_TYPE_CLEAR);
        } else if (value instanceof Boolean) {
            JSONUtils.write(writer, "type", "boolean");
            JSONUtils.write(writer, "value", value);
        } else if (value instanceof Number) {
            JSONUtils.write(writer, "type", PROPERTY_TYPE_NUMBER);
            JSONUtils.write(writer, "value", value);
        } else if (value instanceof Date) {
            JSONUtils.write(writer, "type", "dateTime");
            JSONUtils.write(writer, "value", JSONDateUtils.toString((Date) value));
        } else if (value instanceof String) {
            JSONUtils.write(writer, "type", "string");
            JSONUtils.write(writer, "value", value);
        } else {
            throw new JSONException("Invalid value type");
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return TYPE;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        super.read(object);
        setProperties(readProperties(object));
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        super.write(writer);
        writeProperties(writer, getProperties());
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        CustomPropertiesLog that = (CustomPropertiesLog) o;
        Map<String, Object> map = this.properties;
        return map != null ? map.equals(that.properties) : that.properties == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int result = super.hashCode();
        int i = result * 31;
        Map<String, Object> map = this.properties;
        int result2 = i + (map != null ? map.hashCode() : 0);
        return result2;
    }
}
