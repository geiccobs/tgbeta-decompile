package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public abstract class AbstractLog implements Log {
    private Device device;
    private String distributionGroupId;
    private UUID sid;
    private Object tag;
    private Date timestamp;
    private final Set<String> transmissionTargetTokens = new LinkedHashSet();
    private String userId;

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public UUID getSid() {
        return this.sid;
    }

    public void setSid(UUID uuid) {
        this.sid = uuid;
    }

    public String getDistributionGroupId() {
        return this.distributionGroupId;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public void setDistributionGroupId(String str) {
        this.distributionGroupId = str;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String str) {
        this.userId = str;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public Device getDevice() {
        return this.device;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public void setDevice(Device device) {
        this.device = device;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public synchronized void addTransmissionTarget(String str) {
        this.transmissionTargetTokens.add(str);
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public synchronized Set<String> getTransmissionTargetTokens() {
        return Collections.unmodifiableSet(this.transmissionTargetTokens);
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, "type", getType());
        jSONStringer.key("timestamp").value(JSONDateUtils.toString(getTimestamp()));
        JSONUtils.write(jSONStringer, "sid", getSid());
        JSONUtils.write(jSONStringer, "distributionGroupId", getDistributionGroupId());
        JSONUtils.write(jSONStringer, "userId", getUserId());
        if (getDevice() != null) {
            jSONStringer.key("device").object();
            getDevice().write(jSONStringer);
            jSONStringer.endObject();
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        if (!jSONObject.getString("type").equals(getType())) {
            throw new JSONException("Invalid type");
        }
        setTimestamp(JSONDateUtils.toDate(jSONObject.getString("timestamp")));
        if (jSONObject.has("sid")) {
            setSid(UUID.fromString(jSONObject.getString("sid")));
        }
        setDistributionGroupId(jSONObject.optString("distributionGroupId", null));
        setUserId(jSONObject.optString("userId", null));
        if (!jSONObject.has("device")) {
            return;
        }
        Device device = new Device();
        device.read(jSONObject.getJSONObject("device"));
        setDevice(device);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractLog abstractLog = (AbstractLog) obj;
        if (!this.transmissionTargetTokens.equals(abstractLog.transmissionTargetTokens)) {
            return false;
        }
        Date date = this.timestamp;
        if (date == null ? abstractLog.timestamp != null : !date.equals(abstractLog.timestamp)) {
            return false;
        }
        UUID uuid = this.sid;
        if (uuid == null ? abstractLog.sid != null : !uuid.equals(abstractLog.sid)) {
            return false;
        }
        String str = this.distributionGroupId;
        if (str == null ? abstractLog.distributionGroupId != null : !str.equals(abstractLog.distributionGroupId)) {
            return false;
        }
        String str2 = this.userId;
        if (str2 == null ? abstractLog.userId != null : !str2.equals(abstractLog.userId)) {
            return false;
        }
        Device device = this.device;
        if (device == null ? abstractLog.device != null : !device.equals(abstractLog.device)) {
            return false;
        }
        Object obj2 = this.tag;
        Object obj3 = abstractLog.tag;
        return obj2 != null ? obj2.equals(obj3) : obj3 == null;
    }

    public int hashCode() {
        int hashCode = this.transmissionTargetTokens.hashCode() * 31;
        Date date = this.timestamp;
        int i = 0;
        int hashCode2 = (hashCode + (date != null ? date.hashCode() : 0)) * 31;
        UUID uuid = this.sid;
        int hashCode3 = (hashCode2 + (uuid != null ? uuid.hashCode() : 0)) * 31;
        String str = this.distributionGroupId;
        int hashCode4 = (hashCode3 + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.userId;
        int hashCode5 = (hashCode4 + (str2 != null ? str2.hashCode() : 0)) * 31;
        Device device = this.device;
        int hashCode6 = (hashCode5 + (device != null ? device.hashCode() : 0)) * 31;
        Object obj = this.tag;
        if (obj != null) {
            i = obj.hashCode();
        }
        return hashCode6 + i;
    }
}
