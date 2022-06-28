package com.microsoft.appcenter.ingestion.models.properties;

import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.Model;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public abstract class TypedProperty implements Model {
    private String name;

    public abstract String getType();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        if (!object.getString(CommonProperties.TYPE).equals(getType())) {
            throw new JSONException("Invalid type");
        }
        setName(object.getString(CommonProperties.NAME));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        writer.key(CommonProperties.TYPE).value(getType());
        writer.key(CommonProperties.NAME).value(getName());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypedProperty that = (TypedProperty) o;
        String str = this.name;
        return str != null ? str.equals(that.name) : that.name == null;
    }

    public int hashCode() {
        String str = this.name;
        if (str != null) {
            return str.hashCode();
        }
        return 0;
    }
}
