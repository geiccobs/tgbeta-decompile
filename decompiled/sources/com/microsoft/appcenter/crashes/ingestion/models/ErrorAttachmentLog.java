package com.microsoft.appcenter.crashes.ingestion.models;

import android.util.Base64;
import com.microsoft.appcenter.ingestion.models.AbstractLog;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class ErrorAttachmentLog extends AbstractLog {
    static final Charset CHARSET = Charset.forName("UTF-8");
    private static final String CONTENT_TYPE = "contentType";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    static final String DATA = "data";
    private static final String ERROR_ID = "errorId";
    private static final String FILE_NAME = "fileName";
    public static final String TYPE = "errorAttachment";
    private String contentType;
    private byte[] data;
    private UUID errorId;
    private String fileName;
    private UUID id;

    public static ErrorAttachmentLog attachmentWithText(String text, String fileName) {
        if (text == null) {
            text = "";
        }
        return attachmentWithBinary(text.getBytes(CHARSET), fileName, CONTENT_TYPE_TEXT_PLAIN);
    }

    public static ErrorAttachmentLog attachmentWithBinary(byte[] data, String fileName, String contentType) {
        ErrorAttachmentLog attachmentLog = new ErrorAttachmentLog();
        attachmentLog.setData(data);
        attachmentLog.setFileName(fileName);
        attachmentLog.setContentType(contentType);
        return attachmentLog;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return TYPE;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getErrorId() {
        return this.errorId;
    }

    public void setErrorId(UUID errorId) {
        this.errorId = errorId;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isValid() {
        return (getId() == null || getErrorId() == null || getContentType() == null || getData() == null) ? false : true;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        super.read(object);
        setId(UUID.fromString(object.getString("id")));
        setErrorId(UUID.fromString(object.getString(ERROR_ID)));
        setContentType(object.getString(CONTENT_TYPE));
        setFileName(object.optString(FILE_NAME, null));
        try {
            setData(Base64.decode(object.getString("data"), 0));
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        super.write(writer);
        JSONUtils.write(writer, "id", getId());
        JSONUtils.write(writer, ERROR_ID, getErrorId());
        JSONUtils.write(writer, CONTENT_TYPE, getContentType());
        JSONUtils.write(writer, FILE_NAME, getFileName());
        JSONUtils.write(writer, "data", Base64.encodeToString(getData(), 2));
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        ErrorAttachmentLog that = (ErrorAttachmentLog) o;
        UUID uuid = this.id;
        if (uuid == null ? that.id != null : !uuid.equals(that.id)) {
            return false;
        }
        UUID uuid2 = this.errorId;
        if (uuid2 == null ? that.errorId != null : !uuid2.equals(that.errorId)) {
            return false;
        }
        String str = this.contentType;
        if (str == null ? that.contentType != null : !str.equals(that.contentType)) {
            return false;
        }
        String str2 = this.fileName;
        if (str2 == null ? that.fileName != null : !str2.equals(that.fileName)) {
            return false;
        }
        return Arrays.equals(this.data, that.data);
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int result = super.hashCode();
        int i = result * 31;
        UUID uuid = this.id;
        int i2 = 0;
        int result2 = i + (uuid != null ? uuid.hashCode() : 0);
        int result3 = result2 * 31;
        UUID uuid2 = this.errorId;
        int result4 = (result3 + (uuid2 != null ? uuid2.hashCode() : 0)) * 31;
        String str = this.contentType;
        int result5 = (result4 + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.fileName;
        if (str2 != null) {
            i2 = str2.hashCode();
        }
        return ((result5 + i2) * 31) + Arrays.hashCode(this.data);
    }
}
