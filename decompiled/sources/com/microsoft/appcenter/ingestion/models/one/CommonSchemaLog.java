package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.AbstractLog;
import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public abstract class CommonSchemaLog extends AbstractLog {
    private static final String CV = "cV";
    private static final String DATA = "data";
    private static final String EXT = "ext";
    private static final String FLAGS = "flags";
    private static final String IKEY = "iKey";
    private static final String NAME = "name";
    private static final String POP_SAMPLE = "popSample";
    private static final String TIME = "time";
    private static final String VER = "ver";
    private String cV;
    private Data data;
    private Extensions ext;
    private Long flags;
    private String iKey;
    private String name;
    private Double popSample;
    private String ver;

    public String getVer() {
        return this.ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPopSample() {
        return this.popSample;
    }

    public void setPopSample(Double popSample) {
        this.popSample = popSample;
    }

    public String getIKey() {
        return this.iKey;
    }

    public void setIKey(String iKey) {
        this.iKey = iKey;
    }

    public Long getFlags() {
        return this.flags;
    }

    public void setFlags(Long flags) {
        this.flags = flags;
    }

    public String getCV() {
        return this.cV;
    }

    public void setCV(String cV) {
        this.cV = cV;
    }

    public Extensions getExt() {
        return this.ext;
    }

    public void setExt(Extensions ext) {
        this.ext = ext;
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        setVer(object.getString(VER));
        setName(object.getString("name"));
        setTimestamp(JSONDateUtils.toDate(object.getString(TIME)));
        if (object.has(POP_SAMPLE)) {
            setPopSample(Double.valueOf(object.getDouble(POP_SAMPLE)));
        }
        setIKey(object.optString(IKEY, null));
        setFlags(JSONUtils.readLong(object, FLAGS));
        setCV(object.optString(CV, null));
        if (object.has(EXT)) {
            Extensions extensions = new Extensions();
            extensions.read(object.getJSONObject(EXT));
            setExt(extensions);
        }
        if (object.has("data")) {
            Data data = new Data();
            data.read(object.getJSONObject("data"));
            setData(data);
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        writer.key(VER).value(getVer());
        writer.key("name").value(getName());
        writer.key(TIME).value(JSONDateUtils.toString(getTimestamp()));
        JSONUtils.write(writer, POP_SAMPLE, getPopSample());
        JSONUtils.write(writer, IKEY, getIKey());
        JSONUtils.write(writer, FLAGS, getFlags());
        JSONUtils.write(writer, CV, getCV());
        if (getExt() != null) {
            writer.key(EXT).object();
            getExt().write(writer);
            writer.endObject();
        }
        if (getData() != null) {
            writer.key("data").object();
            getData().write(writer);
            writer.endObject();
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        CommonSchemaLog that = (CommonSchemaLog) o;
        String str = this.ver;
        if (str == null ? that.ver != null : !str.equals(that.ver)) {
            return false;
        }
        String str2 = this.name;
        if (str2 == null ? that.name != null : !str2.equals(that.name)) {
            return false;
        }
        Double d = this.popSample;
        if (d == null ? that.popSample != null : !d.equals(that.popSample)) {
            return false;
        }
        String str3 = this.iKey;
        if (str3 == null ? that.iKey != null : !str3.equals(that.iKey)) {
            return false;
        }
        Long l = this.flags;
        if (l == null ? that.flags != null : !l.equals(that.flags)) {
            return false;
        }
        String str4 = this.cV;
        if (str4 == null ? that.cV != null : !str4.equals(that.cV)) {
            return false;
        }
        Extensions extensions = this.ext;
        if (extensions == null ? that.ext != null : !extensions.equals(that.ext)) {
            return false;
        }
        Data data = this.data;
        return data != null ? data.equals(that.data) : that.data == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.AbstractLog
    public int hashCode() {
        int result = super.hashCode();
        int i = result * 31;
        String str = this.ver;
        int i2 = 0;
        int result2 = i + (str != null ? str.hashCode() : 0);
        int result3 = result2 * 31;
        String str2 = this.name;
        int result4 = (result3 + (str2 != null ? str2.hashCode() : 0)) * 31;
        Double d = this.popSample;
        int result5 = (result4 + (d != null ? d.hashCode() : 0)) * 31;
        String str3 = this.iKey;
        int result6 = (result5 + (str3 != null ? str3.hashCode() : 0)) * 31;
        Long l = this.flags;
        int result7 = (result6 + (l != null ? l.hashCode() : 0)) * 31;
        String str4 = this.cV;
        int result8 = (result7 + (str4 != null ? str4.hashCode() : 0)) * 31;
        Extensions extensions = this.ext;
        int result9 = (result8 + (extensions != null ? extensions.hashCode() : 0)) * 31;
        Data data = this.data;
        if (data != null) {
            i2 = data.hashCode();
        }
        return result9 + i2;
    }
}
