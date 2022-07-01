package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.ingestion.models.AbstractLog;
import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Date;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes.dex */
public abstract class AbstractErrorLog extends AbstractLog {
    private Date appLaunchTimestamp;
    private String architecture;
    private Long errorThreadId;
    private String errorThreadName;
    private Boolean fatal;
    private UUID id;
    private Integer parentProcessId;
    private String parentProcessName;
    private Integer processId;
    private String processName;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public Integer getProcessId() {
        return this.processId;
    }

    public void setProcessId(Integer num) {
        this.processId = num;
    }

    public String getProcessName() {
        return this.processName;
    }

    public void setProcessName(String str) {
        this.processName = str;
    }

    public Integer getParentProcessId() {
        return this.parentProcessId;
    }

    public void setParentProcessId(Integer num) {
        this.parentProcessId = num;
    }

    public String getParentProcessName() {
        return this.parentProcessName;
    }

    public void setParentProcessName(String str) {
        this.parentProcessName = str;
    }

    public Long getErrorThreadId() {
        return this.errorThreadId;
    }

    public void setErrorThreadId(Long l) {
        this.errorThreadId = l;
    }

    public String getErrorThreadName() {
        return this.errorThreadName;
    }

    public void setErrorThreadName(String str) {
        this.errorThreadName = str;
    }

    public Boolean getFatal() {
        return this.fatal;
    }

    public void setFatal(Boolean bool) {
        this.fatal = bool;
    }

    public Date getAppLaunchTimestamp() {
        return this.appLaunchTimestamp;
    }

    public void setAppLaunchTimestamp(Date date) {
        this.appLaunchTimestamp = date;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public void setArchitecture(String str) {
        this.architecture = str;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setId(UUID.fromString(jSONObject.getString("id")));
        setProcessId(JSONUtils.readInteger(jSONObject, "processId"));
        setProcessName(jSONObject.optString("processName", null));
        setParentProcessId(JSONUtils.readInteger(jSONObject, "parentProcessId"));
        setParentProcessName(jSONObject.optString("parentProcessName", null));
        setErrorThreadId(JSONUtils.readLong(jSONObject, "errorThreadId"));
        setErrorThreadName(jSONObject.optString("errorThreadName", null));
        setFatal(JSONUtils.readBoolean(jSONObject, "fatal"));
        setAppLaunchTimestamp(JSONDateUtils.toDate(jSONObject.getString("appLaunchTimestamp")));
        setArchitecture(jSONObject.optString("architecture", null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        JSONUtils.write(jSONStringer, "id", getId());
        JSONUtils.write(jSONStringer, "processId", getProcessId());
        JSONUtils.write(jSONStringer, "processName", getProcessName());
        JSONUtils.write(jSONStringer, "parentProcessId", getParentProcessId());
        JSONUtils.write(jSONStringer, "parentProcessName", getParentProcessName());
        JSONUtils.write(jSONStringer, "errorThreadId", getErrorThreadId());
        JSONUtils.write(jSONStringer, "errorThreadName", getErrorThreadName());
        JSONUtils.write(jSONStringer, "fatal", getFatal());
        JSONUtils.write(jSONStringer, "appLaunchTimestamp", JSONDateUtils.toString(getAppLaunchTimestamp()));
        JSONUtils.write(jSONStringer, "architecture", getArchitecture());
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        AbstractErrorLog abstractErrorLog = (AbstractErrorLog) obj;
        UUID uuid = this.id;
        if (uuid == null ? abstractErrorLog.id != null : !uuid.equals(abstractErrorLog.id)) {
            return false;
        }
        Integer num = this.processId;
        if (num == null ? abstractErrorLog.processId != null : !num.equals(abstractErrorLog.processId)) {
            return false;
        }
        String str = this.processName;
        if (str == null ? abstractErrorLog.processName != null : !str.equals(abstractErrorLog.processName)) {
            return false;
        }
        Integer num2 = this.parentProcessId;
        if (num2 == null ? abstractErrorLog.parentProcessId != null : !num2.equals(abstractErrorLog.parentProcessId)) {
            return false;
        }
        String str2 = this.parentProcessName;
        if (str2 == null ? abstractErrorLog.parentProcessName != null : !str2.equals(abstractErrorLog.parentProcessName)) {
            return false;
        }
        Long l = this.errorThreadId;
        if (l == null ? abstractErrorLog.errorThreadId != null : !l.equals(abstractErrorLog.errorThreadId)) {
            return false;
        }
        String str3 = this.errorThreadName;
        if (str3 == null ? abstractErrorLog.errorThreadName != null : !str3.equals(abstractErrorLog.errorThreadName)) {
            return false;
        }
        Boolean bool = this.fatal;
        if (bool == null ? abstractErrorLog.fatal != null : !bool.equals(abstractErrorLog.fatal)) {
            return false;
        }
        Date date = this.appLaunchTimestamp;
        if (date == null ? abstractErrorLog.appLaunchTimestamp != null : !date.equals(abstractErrorLog.appLaunchTimestamp)) {
            return false;
        }
        String str4 = this.architecture;
        String str5 = abstractErrorLog.architecture;
        return str4 != null ? str4.equals(str5) : str5 == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int hashCode = super.hashCode() * 31;
        UUID uuid = this.id;
        int i = 0;
        int hashCode2 = (hashCode + (uuid != null ? uuid.hashCode() : 0)) * 31;
        Integer num = this.processId;
        int hashCode3 = (hashCode2 + (num != null ? num.hashCode() : 0)) * 31;
        String str = this.processName;
        int hashCode4 = (hashCode3 + (str != null ? str.hashCode() : 0)) * 31;
        Integer num2 = this.parentProcessId;
        int hashCode5 = (hashCode4 + (num2 != null ? num2.hashCode() : 0)) * 31;
        String str2 = this.parentProcessName;
        int hashCode6 = (hashCode5 + (str2 != null ? str2.hashCode() : 0)) * 31;
        Long l = this.errorThreadId;
        int hashCode7 = (hashCode6 + (l != null ? l.hashCode() : 0)) * 31;
        String str3 = this.errorThreadName;
        int hashCode8 = (hashCode7 + (str3 != null ? str3.hashCode() : 0)) * 31;
        Boolean bool = this.fatal;
        int hashCode9 = (hashCode8 + (bool != null ? bool.hashCode() : 0)) * 31;
        Date date = this.appLaunchTimestamp;
        int hashCode10 = (hashCode9 + (date != null ? date.hashCode() : 0)) * 31;
        String str4 = this.architecture;
        if (str4 != null) {
            i = str4.hashCode();
        }
        return hashCode10 + i;
    }
}
