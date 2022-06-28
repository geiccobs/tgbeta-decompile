package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.crashes.ingestion.models.json.ExceptionFactory;
import com.microsoft.appcenter.crashes.ingestion.models.json.StackFrameFactory;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class Exception implements Model {
    private static final String INNER_EXCEPTIONS = "innerExceptions";
    private static final String MESSAGE = "message";
    private static final String MINIDUMP_FILE_PATH = "minidumpFilePath";
    private static final String STACK_TRACE = "stackTrace";
    private static final String WRAPPER_SDK_NAME = "wrapperSdkName";
    private List<StackFrame> frames;
    private List<Exception> innerExceptions;
    private String message;
    private String minidumpFilePath;
    private String stackTrace;
    private String type;
    private String wrapperSdkName;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public List<StackFrame> getFrames() {
        return this.frames;
    }

    public void setFrames(List<StackFrame> frames) {
        this.frames = frames;
    }

    public List<Exception> getInnerExceptions() {
        return this.innerExceptions;
    }

    public void setInnerExceptions(List<Exception> innerExceptions) {
        this.innerExceptions = innerExceptions;
    }

    public String getWrapperSdkName() {
        return this.wrapperSdkName;
    }

    public void setWrapperSdkName(String wrapperSdkName) {
        this.wrapperSdkName = wrapperSdkName;
    }

    public String getMinidumpFilePath() {
        return this.minidumpFilePath;
    }

    public void setMinidumpFilePath(String minidumpFilePath) {
        this.minidumpFilePath = minidumpFilePath;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        setType(object.optString(CommonProperties.TYPE, null));
        setMessage(object.optString(MESSAGE, null));
        setStackTrace(object.optString(STACK_TRACE, null));
        setFrames(JSONUtils.readArray(object, CommonProperties.FRAMES, StackFrameFactory.getInstance()));
        setInnerExceptions(JSONUtils.readArray(object, INNER_EXCEPTIONS, ExceptionFactory.getInstance()));
        setWrapperSdkName(object.optString(WRAPPER_SDK_NAME, null));
        setMinidumpFilePath(object.optString(MINIDUMP_FILE_PATH, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, CommonProperties.TYPE, getType());
        JSONUtils.write(writer, MESSAGE, getMessage());
        JSONUtils.write(writer, STACK_TRACE, getStackTrace());
        JSONUtils.writeArray(writer, CommonProperties.FRAMES, getFrames());
        JSONUtils.writeArray(writer, INNER_EXCEPTIONS, getInnerExceptions());
        JSONUtils.write(writer, WRAPPER_SDK_NAME, getWrapperSdkName());
        JSONUtils.write(writer, MINIDUMP_FILE_PATH, getMinidumpFilePath());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Exception exception = (Exception) o;
        String str = this.type;
        if (str == null ? exception.type != null : !str.equals(exception.type)) {
            return false;
        }
        String str2 = this.message;
        if (str2 == null ? exception.message != null : !str2.equals(exception.message)) {
            return false;
        }
        String str3 = this.stackTrace;
        if (str3 == null ? exception.stackTrace != null : !str3.equals(exception.stackTrace)) {
            return false;
        }
        List<StackFrame> list = this.frames;
        if (list == null ? exception.frames != null : !list.equals(exception.frames)) {
            return false;
        }
        List<Exception> list2 = this.innerExceptions;
        if (list2 == null ? exception.innerExceptions != null : !list2.equals(exception.innerExceptions)) {
            return false;
        }
        String str4 = this.wrapperSdkName;
        if (str4 == null ? exception.wrapperSdkName != null : !str4.equals(exception.wrapperSdkName)) {
            return false;
        }
        String str5 = this.minidumpFilePath;
        return str5 != null ? str5.equals(exception.minidumpFilePath) : exception.minidumpFilePath == null;
    }

    public int hashCode() {
        String str = this.type;
        int i = 0;
        int result = str != null ? str.hashCode() : 0;
        int i2 = result * 31;
        String str2 = this.message;
        int result2 = i2 + (str2 != null ? str2.hashCode() : 0);
        int result3 = result2 * 31;
        String str3 = this.stackTrace;
        int result4 = (result3 + (str3 != null ? str3.hashCode() : 0)) * 31;
        List<StackFrame> list = this.frames;
        int result5 = (result4 + (list != null ? list.hashCode() : 0)) * 31;
        List<Exception> list2 = this.innerExceptions;
        int result6 = (result5 + (list2 != null ? list2.hashCode() : 0)) * 31;
        String str4 = this.wrapperSdkName;
        int result7 = (result6 + (str4 != null ? str4.hashCode() : 0)) * 31;
        String str5 = this.minidumpFilePath;
        if (str5 != null) {
            i = str5.hashCode();
        }
        return result7 + i;
    }
}
