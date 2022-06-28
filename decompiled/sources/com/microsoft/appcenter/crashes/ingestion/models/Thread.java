package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.crashes.ingestion.models.json.StackFrameFactory;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class Thread implements Model {
    private List<StackFrame> frames;
    private long id;
    private String name;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StackFrame> getFrames() {
        return this.frames;
    }

    public void setFrames(List<StackFrame> frames) {
        this.frames = frames;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        setId(object.getLong("id"));
        setName(object.optString(CommonProperties.NAME, null));
        setFrames(JSONUtils.readArray(object, CommonProperties.FRAMES, StackFrameFactory.getInstance()));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, "id", Long.valueOf(getId()));
        JSONUtils.write(writer, CommonProperties.NAME, getName());
        JSONUtils.writeArray(writer, CommonProperties.FRAMES, getFrames());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Thread that = (Thread) o;
        if (this.id != that.id) {
            return false;
        }
        String str = this.name;
        if (str == null ? that.name != null : !str.equals(that.name)) {
            return false;
        }
        List<StackFrame> list = this.frames;
        return list != null ? list.equals(that.frames) : that.frames == null;
    }

    public int hashCode() {
        long j = this.id;
        int result = (int) (j ^ (j >>> 32));
        int i = result * 31;
        String str = this.name;
        int i2 = 0;
        int result2 = i + (str != null ? str.hashCode() : 0);
        int result3 = result2 * 31;
        List<StackFrame> list = this.frames;
        if (list != null) {
            i2 = list.hashCode();
        }
        return result3 + i2;
    }
}
