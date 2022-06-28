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
/* loaded from: classes3.dex */
public abstract class AbstractLog implements Log {
    static final String DEVICE = "device";
    private static final String DISTRIBUTION_GROUP_ID = "distributionGroupId";
    private static final String SID = "sid";
    private static final String TIMESTAMP = "timestamp";
    private static final String USER_ID = "userId";
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
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public UUID getSid() {
        return this.sid;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public void setSid(UUID sid) {
        this.sid = sid;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getDistributionGroupId() {
        return this.distributionGroupId;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public void setDistributionGroupId(String distributionGroupId) {
        this.distributionGroupId = distributionGroupId;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getUserId() {
        return this.userId;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public void setUserId(String userId) {
        this.userId = userId;
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
    public Object getTag() {
        return this.tag;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public synchronized void addTransmissionTarget(String transmissionTargetToken) {
        this.transmissionTargetTokens.add(transmissionTargetToken);
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public synchronized Set<String> getTransmissionTargetTokens() {
        return Collections.unmodifiableSet(this.transmissionTargetTokens);
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, CommonProperties.TYPE, getType());
        writer.key(TIMESTAMP).value(JSONDateUtils.toString(getTimestamp()));
        JSONUtils.write(writer, SID, getSid());
        JSONUtils.write(writer, DISTRIBUTION_GROUP_ID, getDistributionGroupId());
        JSONUtils.write(writer, USER_ID, getUserId());
        if (getDevice() != null) {
            writer.key(DEVICE).object();
            getDevice().write(writer);
            writer.endObject();
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        if (!object.getString(CommonProperties.TYPE).equals(getType())) {
            throw new JSONException("Invalid type");
        }
        setTimestamp(JSONDateUtils.toDate(object.getString(TIMESTAMP)));
        if (object.has(SID)) {
            setSid(UUID.fromString(object.getString(SID)));
        }
        setDistributionGroupId(object.optString(DISTRIBUTION_GROUP_ID, null));
        setUserId(object.optString(USER_ID, null));
        if (object.has(DEVICE)) {
            Device device = new Device();
            device.read(object.getJSONObject(DEVICE));
            setDevice(device);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractLog that = (AbstractLog) o;
        if (!this.transmissionTargetTokens.equals(that.transmissionTargetTokens)) {
            return false;
        }
        Date date = this.timestamp;
        if (date == null ? that.timestamp != null : !date.equals(that.timestamp)) {
            return false;
        }
        UUID uuid = this.sid;
        if (uuid == null ? that.sid != null : !uuid.equals(that.sid)) {
            return false;
        }
        String str = this.distributionGroupId;
        if (str == null ? that.distributionGroupId != null : !str.equals(that.distributionGroupId)) {
            return false;
        }
        String str2 = this.userId;
        if (str2 == null ? that.userId != null : !str2.equals(that.userId)) {
            return false;
        }
        Device device = this.device;
        if (device == null ? that.device != null : !device.equals(that.device)) {
            return false;
        }
        Object obj = this.tag;
        return obj != null ? obj.equals(that.tag) : that.tag == null;
    }

    public int hashCode() {
        int result = this.transmissionTargetTokens.hashCode();
        int i = result * 31;
        Date date = this.timestamp;
        int i2 = 0;
        int result2 = i + (date != null ? date.hashCode() : 0);
        int result3 = result2 * 31;
        UUID uuid = this.sid;
        int result4 = (result3 + (uuid != null ? uuid.hashCode() : 0)) * 31;
        String str = this.distributionGroupId;
        int result5 = (result4 + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.userId;
        int result6 = (result5 + (str2 != null ? str2.hashCode() : 0)) * 31;
        Device device = this.device;
        int result7 = (result6 + (device != null ? device.hashCode() : 0)) * 31;
        Object obj = this.tag;
        if (obj != null) {
            i2 = obj.hashCode();
        }
        return result7 + i2;
    }
}
