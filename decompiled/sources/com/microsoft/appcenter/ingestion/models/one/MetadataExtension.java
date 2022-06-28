package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class MetadataExtension implements Model {
    private JSONObject mMetadata = new JSONObject();

    public JSONObject getMetadata() {
        return this.mMetadata;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) {
        this.mMetadata = object;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        Iterator<String> iterator = this.mMetadata.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            writer.key(key).value(this.mMetadata.get(key));
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetadataExtension metadataExtension = (MetadataExtension) o;
        return this.mMetadata.toString().equals(metadataExtension.mMetadata.toString());
    }

    public int hashCode() {
        return this.mMetadata.toString().hashCode();
    }
}
