package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public class ProtocolExtension implements Model {
    private String devMake;
    private String devModel;
    private List<String> ticketKeys;

    public List<String> getTicketKeys() {
        return this.ticketKeys;
    }

    public void setTicketKeys(List<String> list) {
        this.ticketKeys = list;
    }

    public String getDevMake() {
        return this.devMake;
    }

    public void setDevMake(String str) {
        this.devMake = str;
    }

    public String getDevModel() {
        return this.devModel;
    }

    public void setDevModel(String str) {
        this.devModel = str;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        setTicketKeys(JSONUtils.readStringArray(jSONObject, "ticketKeys"));
        setDevMake(jSONObject.optString("devMake", null));
        setDevModel(jSONObject.optString("devModel", null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.writeStringArray(jSONStringer, "ticketKeys", getTicketKeys());
        JSONUtils.write(jSONStringer, "devMake", getDevMake());
        JSONUtils.write(jSONStringer, "devModel", getDevModel());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || ProtocolExtension.class != obj.getClass()) {
            return false;
        }
        ProtocolExtension protocolExtension = (ProtocolExtension) obj;
        List<String> list = this.ticketKeys;
        if (list == null ? protocolExtension.ticketKeys != null : !list.equals(protocolExtension.ticketKeys)) {
            return false;
        }
        String str = this.devMake;
        if (str == null ? protocolExtension.devMake != null : !str.equals(protocolExtension.devMake)) {
            return false;
        }
        String str2 = this.devModel;
        String str3 = protocolExtension.devModel;
        return str2 != null ? str2.equals(str3) : str3 == null;
    }

    public int hashCode() {
        List<String> list = this.ticketKeys;
        int i = 0;
        int hashCode = (list != null ? list.hashCode() : 0) * 31;
        String str = this.devMake;
        int hashCode2 = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.devModel;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return hashCode2 + i;
    }
}
