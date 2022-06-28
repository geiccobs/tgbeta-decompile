package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class Device extends WrapperSdk {
    private static final String APP_BUILD = "appBuild";
    private static final String APP_NAMESPACE = "appNamespace";
    private static final String APP_VERSION = "appVersion";
    private static final String CARRIER_COUNTRY = "carrierCountry";
    private static final String CARRIER_NAME = "carrierName";
    private static final String LOCALE = "locale";
    private static final String MODEL = "model";
    private static final String OEM_NAME = "oemName";
    private static final String OS_API_LEVEL = "osApiLevel";
    private static final String OS_BUILD = "osBuild";
    private static final String OS_NAME = "osName";
    private static final String OS_VERSION = "osVersion";
    private static final String SCREEN_SIZE = "screenSize";
    private static final String SDK_NAME = "sdkName";
    private static final String SDK_VERSION = "sdkVersion";
    private static final String TIME_ZONE_OFFSET = "timeZoneOffset";
    private String appBuild;
    private String appNamespace;
    private String appVersion;
    private String carrierCountry;
    private String carrierName;
    private String locale;
    private String model;
    private String oemName;
    private Integer osApiLevel;
    private String osBuild;
    private String osName;
    private String osVersion;
    private String screenSize;
    private String sdkName;
    private String sdkVersion;
    private Integer timeZoneOffset;

    public String getSdkName() {
        return this.sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOemName() {
        return this.oemName;
    }

    public void setOemName(String oemName) {
        this.oemName = oemName;
    }

    public String getOsName() {
        return this.osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsBuild() {
        return this.osBuild;
    }

    public void setOsBuild(String osBuild) {
        this.osBuild = osBuild;
    }

    public Integer getOsApiLevel() {
        return this.osApiLevel;
    }

    public void setOsApiLevel(Integer osApiLevel) {
        this.osApiLevel = osApiLevel;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Integer getTimeZoneOffset() {
        return this.timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public String getScreenSize() {
        return this.screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCarrierName() {
        return this.carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCarrierCountry() {
        return this.carrierCountry;
    }

    public void setCarrierCountry(String carrierCountry) {
        this.carrierCountry = carrierCountry;
    }

    public String getAppBuild() {
        return this.appBuild;
    }

    public void setAppBuild(String appBuild) {
        this.appBuild = appBuild;
    }

    public String getAppNamespace() {
        return this.appNamespace;
    }

    public void setAppNamespace(String appNamespace) {
        this.appNamespace = appNamespace;
    }

    @Override // com.microsoft.appcenter.ingestion.models.WrapperSdk, com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        super.read(object);
        setSdkName(object.getString(SDK_NAME));
        setSdkVersion(object.getString("sdkVersion"));
        setModel(object.getString(MODEL));
        setOemName(object.getString(OEM_NAME));
        setOsName(object.getString(OS_NAME));
        setOsVersion(object.getString(OS_VERSION));
        setOsBuild(object.optString(OS_BUILD, null));
        setOsApiLevel(JSONUtils.readInteger(object, OS_API_LEVEL));
        setLocale(object.getString(LOCALE));
        setTimeZoneOffset(Integer.valueOf(object.getInt(TIME_ZONE_OFFSET)));
        setScreenSize(object.getString(SCREEN_SIZE));
        setAppVersion(object.getString("appVersion"));
        setCarrierName(object.optString(CARRIER_NAME, null));
        setCarrierCountry(object.optString(CARRIER_COUNTRY, null));
        setAppBuild(object.getString("appBuild"));
        setAppNamespace(object.optString(APP_NAMESPACE, null));
    }

    @Override // com.microsoft.appcenter.ingestion.models.WrapperSdk, com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        super.write(writer);
        writer.key(SDK_NAME).value(getSdkName());
        writer.key("sdkVersion").value(getSdkVersion());
        writer.key(MODEL).value(getModel());
        writer.key(OEM_NAME).value(getOemName());
        writer.key(OS_NAME).value(getOsName());
        writer.key(OS_VERSION).value(getOsVersion());
        JSONUtils.write(writer, OS_BUILD, getOsBuild());
        JSONUtils.write(writer, OS_API_LEVEL, getOsApiLevel());
        writer.key(LOCALE).value(getLocale());
        writer.key(TIME_ZONE_OFFSET).value(getTimeZoneOffset());
        writer.key(SCREEN_SIZE).value(getScreenSize());
        writer.key("appVersion").value(getAppVersion());
        JSONUtils.write(writer, CARRIER_NAME, getCarrierName());
        JSONUtils.write(writer, CARRIER_COUNTRY, getCarrierCountry());
        writer.key("appBuild").value(getAppBuild());
        JSONUtils.write(writer, APP_NAMESPACE, getAppNamespace());
    }

    @Override // com.microsoft.appcenter.ingestion.models.WrapperSdk
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        Device device = (Device) o;
        String str = this.sdkName;
        if (str == null ? device.sdkName != null : !str.equals(device.sdkName)) {
            return false;
        }
        String str2 = this.sdkVersion;
        if (str2 == null ? device.sdkVersion != null : !str2.equals(device.sdkVersion)) {
            return false;
        }
        String str3 = this.model;
        if (str3 == null ? device.model != null : !str3.equals(device.model)) {
            return false;
        }
        String str4 = this.oemName;
        if (str4 == null ? device.oemName != null : !str4.equals(device.oemName)) {
            return false;
        }
        String str5 = this.osName;
        if (str5 == null ? device.osName != null : !str5.equals(device.osName)) {
            return false;
        }
        String str6 = this.osVersion;
        if (str6 == null ? device.osVersion != null : !str6.equals(device.osVersion)) {
            return false;
        }
        String str7 = this.osBuild;
        if (str7 == null ? device.osBuild != null : !str7.equals(device.osBuild)) {
            return false;
        }
        Integer num = this.osApiLevel;
        if (num == null ? device.osApiLevel != null : !num.equals(device.osApiLevel)) {
            return false;
        }
        String str8 = this.locale;
        if (str8 == null ? device.locale != null : !str8.equals(device.locale)) {
            return false;
        }
        Integer num2 = this.timeZoneOffset;
        if (num2 == null ? device.timeZoneOffset != null : !num2.equals(device.timeZoneOffset)) {
            return false;
        }
        String str9 = this.screenSize;
        if (str9 == null ? device.screenSize != null : !str9.equals(device.screenSize)) {
            return false;
        }
        String str10 = this.appVersion;
        if (str10 == null ? device.appVersion != null : !str10.equals(device.appVersion)) {
            return false;
        }
        String str11 = this.carrierName;
        if (str11 == null ? device.carrierName != null : !str11.equals(device.carrierName)) {
            return false;
        }
        String str12 = this.carrierCountry;
        if (str12 == null ? device.carrierCountry != null : !str12.equals(device.carrierCountry)) {
            return false;
        }
        String str13 = this.appBuild;
        if (str13 == null ? device.appBuild != null : !str13.equals(device.appBuild)) {
            return false;
        }
        String str14 = this.appNamespace;
        return str14 != null ? str14.equals(device.appNamespace) : device.appNamespace == null;
    }

    @Override // com.microsoft.appcenter.ingestion.models.WrapperSdk
    public int hashCode() {
        int result = super.hashCode();
        int i = result * 31;
        String str = this.sdkName;
        int i2 = 0;
        int result2 = i + (str != null ? str.hashCode() : 0);
        int result3 = result2 * 31;
        String str2 = this.sdkVersion;
        int result4 = (result3 + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.model;
        int result5 = (result4 + (str3 != null ? str3.hashCode() : 0)) * 31;
        String str4 = this.oemName;
        int result6 = (result5 + (str4 != null ? str4.hashCode() : 0)) * 31;
        String str5 = this.osName;
        int result7 = (result6 + (str5 != null ? str5.hashCode() : 0)) * 31;
        String str6 = this.osVersion;
        int result8 = (result7 + (str6 != null ? str6.hashCode() : 0)) * 31;
        String str7 = this.osBuild;
        int result9 = (result8 + (str7 != null ? str7.hashCode() : 0)) * 31;
        Integer num = this.osApiLevel;
        int result10 = (result9 + (num != null ? num.hashCode() : 0)) * 31;
        String str8 = this.locale;
        int result11 = (result10 + (str8 != null ? str8.hashCode() : 0)) * 31;
        Integer num2 = this.timeZoneOffset;
        int result12 = (result11 + (num2 != null ? num2.hashCode() : 0)) * 31;
        String str9 = this.screenSize;
        int result13 = (result12 + (str9 != null ? str9.hashCode() : 0)) * 31;
        String str10 = this.appVersion;
        int result14 = (result13 + (str10 != null ? str10.hashCode() : 0)) * 31;
        String str11 = this.carrierName;
        int result15 = (result14 + (str11 != null ? str11.hashCode() : 0)) * 31;
        String str12 = this.carrierCountry;
        int result16 = (result15 + (str12 != null ? str12.hashCode() : 0)) * 31;
        String str13 = this.appBuild;
        int result17 = (result16 + (str13 != null ? str13.hashCode() : 0)) * 31;
        String str14 = this.appNamespace;
        if (str14 != null) {
            i2 = str14.hashCode();
        }
        return result17 + i2;
    }
}
