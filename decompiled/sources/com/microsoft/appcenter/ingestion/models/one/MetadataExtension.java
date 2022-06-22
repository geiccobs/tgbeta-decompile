package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class MetadataExtension implements Model {
    private JSONObject mMetadata = new JSONObject();

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) {
        this.mMetadata = jSONObject;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        Iterator<String> keys = this.mMetadata.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            jSONStringer.key(next).value(this.mMetadata.get(next));
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && MetadataExtension.class == obj.getClass()) {
            return this.mMetadata.toString().equals(((MetadataExtension) obj).mMetadata.toString());
        }
        return false;
    }

    public int hashCode() {
        return this.mMetadata.toString().hashCode();
    }
}
