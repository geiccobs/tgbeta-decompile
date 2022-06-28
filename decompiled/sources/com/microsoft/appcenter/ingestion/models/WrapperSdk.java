package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class WrapperSdk implements Model {
    private static final String LIVE_UPDATE_DEPLOYMENT_KEY = "liveUpdateDeploymentKey";
    private static final String LIVE_UPDATE_PACKAGE_HASH = "liveUpdatePackageHash";
    private static final String LIVE_UPDATE_RELEASE_LABEL = "liveUpdateReleaseLabel";
    private static final String WRAPPER_RUNTIME_VERSION = "wrapperRuntimeVersion";
    private static final String WRAPPER_SDK_NAME = "wrapperSdkName";
    private static final String WRAPPER_SDK_VERSION = "wrapperSdkVersion";
    private String liveUpdateDeploymentKey;
    private String liveUpdatePackageHash;
    private String liveUpdateReleaseLabel;
    private String wrapperRuntimeVersion;
    private String wrapperSdkName;
    private String wrapperSdkVersion;

    public String getWrapperSdkVersion() {
        return this.wrapperSdkVersion;
    }

    public void setWrapperSdkVersion(String wrapperSdkVersion) {
        this.wrapperSdkVersion = wrapperSdkVersion;
    }

    public String getWrapperSdkName() {
        return this.wrapperSdkName;
    }

    public void setWrapperSdkName(String wrapperSdkName) {
        this.wrapperSdkName = wrapperSdkName;
    }

    public String getWrapperRuntimeVersion() {
        return this.wrapperRuntimeVersion;
    }

    public void setWrapperRuntimeVersion(String wrapperRuntimeVersion) {
        this.wrapperRuntimeVersion = wrapperRuntimeVersion;
    }

    public String getLiveUpdateReleaseLabel() {
        return this.liveUpdateReleaseLabel;
    }

    public void setLiveUpdateReleaseLabel(String liveUpdateReleaseLabel) {
        this.liveUpdateReleaseLabel = liveUpdateReleaseLabel;
    }

    public String getLiveUpdateDeploymentKey() {
        return this.liveUpdateDeploymentKey;
    }

    public void setLiveUpdateDeploymentKey(String liveUpdateDeploymentKey) {
        this.liveUpdateDeploymentKey = liveUpdateDeploymentKey;
    }

    public String getLiveUpdatePackageHash() {
        return this.liveUpdatePackageHash;
    }

    public void setLiveUpdatePackageHash(String liveUpdatePackageHash) {
        this.liveUpdatePackageHash = liveUpdatePackageHash;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        setWrapperSdkVersion(object.optString(WRAPPER_SDK_VERSION, null));
        setWrapperSdkName(object.optString(WRAPPER_SDK_NAME, null));
        setWrapperRuntimeVersion(object.optString(WRAPPER_RUNTIME_VERSION, null));
        setLiveUpdateReleaseLabel(object.optString(LIVE_UPDATE_RELEASE_LABEL, null));
        setLiveUpdateDeploymentKey(object.optString(LIVE_UPDATE_DEPLOYMENT_KEY, null));
        setLiveUpdatePackageHash(object.optString(LIVE_UPDATE_PACKAGE_HASH, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        JSONUtils.write(writer, WRAPPER_SDK_VERSION, getWrapperSdkVersion());
        JSONUtils.write(writer, WRAPPER_SDK_NAME, getWrapperSdkName());
        JSONUtils.write(writer, WRAPPER_RUNTIME_VERSION, getWrapperRuntimeVersion());
        JSONUtils.write(writer, LIVE_UPDATE_RELEASE_LABEL, getLiveUpdateReleaseLabel());
        JSONUtils.write(writer, LIVE_UPDATE_DEPLOYMENT_KEY, getLiveUpdateDeploymentKey());
        JSONUtils.write(writer, LIVE_UPDATE_PACKAGE_HASH, getLiveUpdatePackageHash());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WrapperSdk that = (WrapperSdk) o;
        String str = this.wrapperSdkVersion;
        if (str == null ? that.wrapperSdkVersion != null : !str.equals(that.wrapperSdkVersion)) {
            return false;
        }
        String str2 = this.wrapperSdkName;
        if (str2 == null ? that.wrapperSdkName != null : !str2.equals(that.wrapperSdkName)) {
            return false;
        }
        String str3 = this.wrapperRuntimeVersion;
        if (str3 == null ? that.wrapperRuntimeVersion != null : !str3.equals(that.wrapperRuntimeVersion)) {
            return false;
        }
        String str4 = this.liveUpdateReleaseLabel;
        if (str4 == null ? that.liveUpdateReleaseLabel != null : !str4.equals(that.liveUpdateReleaseLabel)) {
            return false;
        }
        String str5 = this.liveUpdateDeploymentKey;
        if (str5 == null ? that.liveUpdateDeploymentKey != null : !str5.equals(that.liveUpdateDeploymentKey)) {
            return false;
        }
        String str6 = this.liveUpdatePackageHash;
        return str6 != null ? str6.equals(that.liveUpdatePackageHash) : that.liveUpdatePackageHash == null;
    }

    public int hashCode() {
        String str = this.wrapperSdkVersion;
        int i = 0;
        int result = str != null ? str.hashCode() : 0;
        int i2 = result * 31;
        String str2 = this.wrapperSdkName;
        int result2 = i2 + (str2 != null ? str2.hashCode() : 0);
        int result3 = result2 * 31;
        String str3 = this.wrapperRuntimeVersion;
        int result4 = (result3 + (str3 != null ? str3.hashCode() : 0)) * 31;
        String str4 = this.liveUpdateReleaseLabel;
        int result5 = (result4 + (str4 != null ? str4.hashCode() : 0)) * 31;
        String str5 = this.liveUpdateDeploymentKey;
        int result6 = (result5 + (str5 != null ? str5.hashCode() : 0)) * 31;
        String str6 = this.liveUpdatePackageHash;
        if (str6 != null) {
            i = str6.hashCode();
        }
        return result6 + i;
    }
}
