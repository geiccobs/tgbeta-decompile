package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class ProtocolExtension implements Model {
    private static final String DEV_MAKE = "devMake";
    private static final String DEV_MODEL = "devModel";
    private static final String TICKET_KEYS = "ticketKeys";
    private String devMake;
    private String devModel;
    private List<String> ticketKeys;

    public List<String> getTicketKeys() {
        return this.ticketKeys;
    }

    public void setTicketKeys(List<String> ticketKeys) {
        this.ticketKeys = ticketKeys;
    }

    public String getDevMake() {
        return this.devMake;
    }

    public void setDevMake(String devMake) {
        this.devMake = devMake;
    }

    public String getDevModel() {
        return this.devModel;
    }

    public void setDevModel(String devModel) {
        this.devModel = devModel;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        setTicketKeys(JSONUtils.readStringArray(object, TICKET_KEYS));
        setDevMake(object.optString(DEV_MAKE, null));
        setDevModel(object.optString(DEV_MODEL, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.writeStringArray(writer, TICKET_KEYS, getTicketKeys());
        JSONUtils.write(writer, DEV_MAKE, getDevMake());
        JSONUtils.write(writer, DEV_MODEL, getDevModel());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProtocolExtension that = (ProtocolExtension) o;
        List<String> list = this.ticketKeys;
        if (list == null ? that.ticketKeys != null : !list.equals(that.ticketKeys)) {
            return false;
        }
        String str = this.devMake;
        if (str == null ? that.devMake != null : !str.equals(that.devMake)) {
            return false;
        }
        String str2 = this.devModel;
        return str2 != null ? str2.equals(that.devModel) : that.devModel == null;
    }

    public int hashCode() {
        List<String> list = this.ticketKeys;
        int i = 0;
        int result = list != null ? list.hashCode() : 0;
        int i2 = result * 31;
        String str = this.devMake;
        int result2 = i2 + (str != null ? str.hashCode() : 0);
        int result3 = result2 * 31;
        String str2 = this.devModel;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return result3 + i;
    }
}
