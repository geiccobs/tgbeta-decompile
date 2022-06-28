package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.ingestion.models.AbstractLog;
import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Date;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public abstract class AbstractErrorLog extends AbstractLog {
    private static final String APP_LAUNCH_TIMESTAMP = "appLaunchTimestamp";
    private static final String ARCHITECTURE = "architecture";
    private static final String ERROR_THREAD_ID = "errorThreadId";
    private static final String ERROR_THREAD_NAME = "errorThreadName";
    private static final String FATAL = "fatal";
    private static final String PARENT_PROCESS_ID = "parentProcessId";
    private static final String PARENT_PROCESS_NAME = "parentProcessName";
    private static final String PROCESS_ID = "processId";
    private static final String PROCESS_NAME = "processName";
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

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getProcessId() {
        return this.processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return this.processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Integer getParentProcessId() {
        return this.parentProcessId;
    }

    public void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    public String getParentProcessName() {
        return this.parentProcessName;
    }

    public void setParentProcessName(String parentProcessName) {
        this.parentProcessName = parentProcessName;
    }

    public Long getErrorThreadId() {
        return this.errorThreadId;
    }

    public void setErrorThreadId(Long errorThreadId) {
        this.errorThreadId = errorThreadId;
    }

    public String getErrorThreadName() {
        return this.errorThreadName;
    }

    public void setErrorThreadName(String errorThreadName) {
        this.errorThreadName = errorThreadName;
    }

    public Boolean getFatal() {
        return this.fatal;
    }

    public void setFatal(Boolean fatal) {
        this.fatal = fatal;
    }

    public Date getAppLaunchTimestamp() {
        return this.appLaunchTimestamp;
    }

    public void setAppLaunchTimestamp(Date appLaunchTimestamp) {
        this.appLaunchTimestamp = appLaunchTimestamp;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        super.read(object);
        setId(UUID.fromString(object.getString("id")));
        setProcessId(JSONUtils.readInteger(object, PROCESS_ID));
        setProcessName(object.optString(PROCESS_NAME, null));
        setParentProcessId(JSONUtils.readInteger(object, PARENT_PROCESS_ID));
        setParentProcessName(object.optString(PARENT_PROCESS_NAME, null));
        setErrorThreadId(JSONUtils.readLong(object, ERROR_THREAD_ID));
        setErrorThreadName(object.optString(ERROR_THREAD_NAME, null));
        setFatal(JSONUtils.readBoolean(object, FATAL));
        setAppLaunchTimestamp(JSONDateUtils.toDate(object.getString(APP_LAUNCH_TIMESTAMP)));
        setArchitecture(object.optString(ARCHITECTURE, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        super.write(writer);
        JSONUtils.write(writer, "id", getId());
        JSONUtils.write(writer, PROCESS_ID, getProcessId());
        JSONUtils.write(writer, PROCESS_NAME, getProcessName());
        JSONUtils.write(writer, PARENT_PROCESS_ID, getParentProcessId());
        JSONUtils.write(writer, PARENT_PROCESS_NAME, getParentProcessName());
        JSONUtils.write(writer, ERROR_THREAD_ID, getErrorThreadId());
        JSONUtils.write(writer, ERROR_THREAD_NAME, getErrorThreadName());
        JSONUtils.write(writer, FATAL, getFatal());
        JSONUtils.write(writer, APP_LAUNCH_TIMESTAMP, JSONDateUtils.toString(getAppLaunchTimestamp()));
        JSONUtils.write(writer, ARCHITECTURE, getArchitecture());
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        AbstractErrorLog that = (AbstractErrorLog) o;
        UUID uuid = this.id;
        if (uuid == null ? that.id != null : !uuid.equals(that.id)) {
            return false;
        }
        Integer num = this.processId;
        if (num == null ? that.processId != null : !num.equals(that.processId)) {
            return false;
        }
        String str = this.processName;
        if (str == null ? that.processName != null : !str.equals(that.processName)) {
            return false;
        }
        Integer num2 = this.parentProcessId;
        if (num2 == null ? that.parentProcessId != null : !num2.equals(that.parentProcessId)) {
            return false;
        }
        String str2 = this.parentProcessName;
        if (str2 == null ? that.parentProcessName != null : !str2.equals(that.parentProcessName)) {
            return false;
        }
        Long l = this.errorThreadId;
        if (l == null ? that.errorThreadId != null : !l.equals(that.errorThreadId)) {
            return false;
        }
        String str3 = this.errorThreadName;
        if (str3 == null ? that.errorThreadName != null : !str3.equals(that.errorThreadName)) {
            return false;
        }
        Boolean bool = this.fatal;
        if (bool == null ? that.fatal != null : !bool.equals(that.fatal)) {
            return false;
        }
        Date date = this.appLaunchTimestamp;
        if (date == null ? that.appLaunchTimestamp != null : !date.equals(that.appLaunchTimestamp)) {
            return false;
        }
        String str4 = this.architecture;
        return str4 != null ? str4.equals(that.architecture) : that.architecture == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int result = super.hashCode();
        int i = result * 31;
        UUID uuid = this.id;
        int i2 = 0;
        int result2 = i + (uuid != null ? uuid.hashCode() : 0);
        int result3 = result2 * 31;
        Integer num = this.processId;
        int result4 = (result3 + (num != null ? num.hashCode() : 0)) * 31;
        String str = this.processName;
        int result5 = (result4 + (str != null ? str.hashCode() : 0)) * 31;
        Integer num2 = this.parentProcessId;
        int result6 = (result5 + (num2 != null ? num2.hashCode() : 0)) * 31;
        String str2 = this.parentProcessName;
        int result7 = (result6 + (str2 != null ? str2.hashCode() : 0)) * 31;
        Long l = this.errorThreadId;
        int result8 = (result7 + (l != null ? l.hashCode() : 0)) * 31;
        String str3 = this.errorThreadName;
        int result9 = (result8 + (str3 != null ? str3.hashCode() : 0)) * 31;
        Boolean bool = this.fatal;
        int result10 = (result9 + (bool != null ? bool.hashCode() : 0)) * 31;
        Date date = this.appLaunchTimestamp;
        int result11 = (result10 + (date != null ? date.hashCode() : 0)) * 31;
        String str4 = this.architecture;
        if (str4 != null) {
            i2 = str4.hashCode();
        }
        return result11 + i2;
    }
}
