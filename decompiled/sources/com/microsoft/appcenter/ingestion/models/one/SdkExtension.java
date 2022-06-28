package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class SdkExtension implements Model {
    private static final String EPOCH = "epoch";
    private static final String INSTALL_ID = "installId";
    private static final String LIB_VER = "libVer";
    private static final String SEQ = "seq";
    private String epoch;
    private UUID installId;
    private String libVer;
    private Long seq;

    public String getLibVer() {
        return this.libVer;
    }

    public void setLibVer(String libVer) {
        this.libVer = libVer;
    }

    public String getEpoch() {
        return this.epoch;
    }

    public void setEpoch(String epoch) {
        this.epoch = epoch;
    }

    public Long getSeq() {
        return this.seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public UUID getInstallId() {
        return this.installId;
    }

    public void setInstallId(UUID installId) {
        this.installId = installId;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        setLibVer(object.optString(LIB_VER, null));
        setEpoch(object.optString(EPOCH, null));
        setSeq(JSONUtils.readLong(object, SEQ));
        if (object.has("installId")) {
            setInstallId(UUID.fromString(object.getString("installId")));
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, LIB_VER, getLibVer());
        JSONUtils.write(writer, EPOCH, getEpoch());
        JSONUtils.write(writer, SEQ, getSeq());
        JSONUtils.write(writer, "installId", getInstallId());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SdkExtension that = (SdkExtension) o;
        String str = this.libVer;
        if (str == null ? that.libVer != null : !str.equals(that.libVer)) {
            return false;
        }
        String str2 = this.epoch;
        if (str2 == null ? that.epoch != null : !str2.equals(that.epoch)) {
            return false;
        }
        Long l = this.seq;
        if (l == null ? that.seq != null : !l.equals(that.seq)) {
            return false;
        }
        UUID uuid = this.installId;
        return uuid != null ? uuid.equals(that.installId) : that.installId == null;
    }

    public int hashCode() {
        String str = this.libVer;
        int i = 0;
        int result = str != null ? str.hashCode() : 0;
        int i2 = result * 31;
        String str2 = this.epoch;
        int result2 = i2 + (str2 != null ? str2.hashCode() : 0);
        int result3 = result2 * 31;
        Long l = this.seq;
        int result4 = (result3 + (l != null ? l.hashCode() : 0)) * 31;
        UUID uuid = this.installId;
        if (uuid != null) {
            i = uuid.hashCode();
        }
        return result4 + i;
    }
}
