package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class StackFrame implements Model {
    private static final String CLASS_NAME = "className";
    private static final String FILE_NAME = "fileName";
    private static final String LINE_NUMBER = "lineNumber";
    private static final String METHOD_NAME = "methodName";
    private String className;
    private String fileName;
    private Integer lineNumber;
    private String methodName;

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        setClassName(object.optString(CLASS_NAME, null));
        setMethodName(object.optString(METHOD_NAME, null));
        setLineNumber(JSONUtils.readInteger(object, LINE_NUMBER));
        setFileName(object.optString(FILE_NAME, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, CLASS_NAME, getClassName());
        JSONUtils.write(writer, METHOD_NAME, getMethodName());
        JSONUtils.write(writer, LINE_NUMBER, getLineNumber());
        JSONUtils.write(writer, FILE_NAME, getFileName());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StackFrame that = (StackFrame) o;
        String str = this.className;
        if (str == null ? that.className != null : !str.equals(that.className)) {
            return false;
        }
        String str2 = this.methodName;
        if (str2 == null ? that.methodName != null : !str2.equals(that.methodName)) {
            return false;
        }
        Integer num = this.lineNumber;
        if (num == null ? that.lineNumber != null : !num.equals(that.lineNumber)) {
            return false;
        }
        String str3 = this.fileName;
        return str3 != null ? str3.equals(that.fileName) : that.fileName == null;
    }

    public int hashCode() {
        String str = this.className;
        int i = 0;
        int result = str != null ? str.hashCode() : 0;
        int i2 = result * 31;
        String str2 = this.methodName;
        int result2 = i2 + (str2 != null ? str2.hashCode() : 0);
        int result3 = result2 * 31;
        Integer num = this.lineNumber;
        int result4 = (result3 + (num != null ? num.hashCode() : 0)) * 31;
        String str3 = this.fileName;
        if (str3 != null) {
            i = str3.hashCode();
        }
        return result4 + i;
    }
}
