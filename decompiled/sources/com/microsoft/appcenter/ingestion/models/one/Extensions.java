package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
/* loaded from: classes3.dex */
public class Extensions implements Model {
    private static final String APP = "app";
    private static final String DEVICE = "device";
    private static final String LOC = "loc";
    private static final String METADATA = "metadata";
    private static final String NET = "net";
    private static final String OS = "os";
    private static final String PROTOCOL = "protocol";
    private static final String SDK = "sdk";
    private static final String USER = "user";
    private AppExtension app;
    private DeviceExtension device;
    private LocExtension loc;
    private MetadataExtension metadata;
    private NetExtension net;
    private OsExtension os;
    private ProtocolExtension protocol;
    private SdkExtension sdk;
    private UserExtension user;

    public MetadataExtension getMetadata() {
        return this.metadata;
    }

    public void setMetadata(MetadataExtension metadata) {
        this.metadata = metadata;
    }

    public ProtocolExtension getProtocol() {
        return this.protocol;
    }

    public void setProtocol(ProtocolExtension protocol) {
        this.protocol = protocol;
    }

    public UserExtension getUser() {
        return this.user;
    }

    public void setUser(UserExtension user) {
        this.user = user;
    }

    public DeviceExtension getDevice() {
        return this.device;
    }

    public void setDevice(DeviceExtension device) {
        this.device = device;
    }

    public OsExtension getOs() {
        return this.os;
    }

    public void setOs(OsExtension os) {
        this.os = os;
    }

    public AppExtension getApp() {
        return this.app;
    }

    public void setApp(AppExtension app) {
        this.app = app;
    }

    public NetExtension getNet() {
        return this.net;
    }

    public void setNet(NetExtension net) {
        this.net = net;
    }

    public SdkExtension getSdk() {
        return this.sdk;
    }

    public void setSdk(SdkExtension sdk) {
        this.sdk = sdk;
    }

    public LocExtension getLoc() {
        return this.loc;
    }

    public void setLoc(LocExtension loc) {
        this.loc = loc;
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void read(JSONObject object) throws JSONException {
        if (object.has("metadata")) {
            MetadataExtension metadata = new MetadataExtension();
            metadata.read(object.getJSONObject("metadata"));
            setMetadata(metadata);
        }
        if (object.has(PROTOCOL)) {
            ProtocolExtension protocol = new ProtocolExtension();
            protocol.read(object.getJSONObject(PROTOCOL));
            setProtocol(protocol);
        }
        if (object.has(USER)) {
            UserExtension user = new UserExtension();
            user.read(object.getJSONObject(USER));
            setUser(user);
        }
        if (object.has(DEVICE)) {
            DeviceExtension device = new DeviceExtension();
            device.read(object.getJSONObject(DEVICE));
            setDevice(device);
        }
        if (object.has(OS)) {
            OsExtension os = new OsExtension();
            os.read(object.getJSONObject(OS));
            setOs(os);
        }
        if (object.has(APP)) {
            AppExtension app = new AppExtension();
            app.read(object.getJSONObject(APP));
            setApp(app);
        }
        if (object.has(NET)) {
            NetExtension net = new NetExtension();
            net.read(object.getJSONObject(NET));
            setNet(net);
        }
        if (object.has(SDK)) {
            SdkExtension sdk = new SdkExtension();
            sdk.read(object.getJSONObject(SDK));
            setSdk(sdk);
        }
        if (object.has(LOC)) {
            LocExtension loc = new LocExtension();
            loc.read(object.getJSONObject(LOC));
            setLoc(loc);
        }
    }

    @Override // com.microsoft.appcenter.ingestion.models.Model
    public void write(JSONStringer writer) throws JSONException {
        if (getMetadata() != null) {
            writer.key("metadata").object();
            getMetadata().write(writer);
            writer.endObject();
        }
        if (getProtocol() != null) {
            writer.key(PROTOCOL).object();
            getProtocol().write(writer);
            writer.endObject();
        }
        if (getUser() != null) {
            writer.key(USER).object();
            getUser().write(writer);
            writer.endObject();
        }
        if (getDevice() != null) {
            writer.key(DEVICE).object();
            getDevice().write(writer);
            writer.endObject();
        }
        if (getOs() != null) {
            writer.key(OS).object();
            getOs().write(writer);
            writer.endObject();
        }
        if (getApp() != null) {
            writer.key(APP).object();
            getApp().write(writer);
            writer.endObject();
        }
        if (getNet() != null) {
            writer.key(NET).object();
            getNet().write(writer);
            writer.endObject();
        }
        if (getSdk() != null) {
            writer.key(SDK).object();
            getSdk().write(writer);
            writer.endObject();
        }
        if (getLoc() != null) {
            writer.key(LOC).object();
            getLoc().write(writer);
            writer.endObject();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Extensions that = (Extensions) o;
        MetadataExtension metadataExtension = this.metadata;
        if (metadataExtension == null ? that.metadata != null : !metadataExtension.equals(that.metadata)) {
            return false;
        }
        ProtocolExtension protocolExtension = this.protocol;
        if (protocolExtension == null ? that.protocol != null : !protocolExtension.equals(that.protocol)) {
            return false;
        }
        UserExtension userExtension = this.user;
        if (userExtension == null ? that.user != null : !userExtension.equals(that.user)) {
            return false;
        }
        DeviceExtension deviceExtension = this.device;
        if (deviceExtension == null ? that.device != null : !deviceExtension.equals(that.device)) {
            return false;
        }
        OsExtension osExtension = this.os;
        if (osExtension == null ? that.os != null : !osExtension.equals(that.os)) {
            return false;
        }
        AppExtension appExtension = this.app;
        if (appExtension == null ? that.app != null : !appExtension.equals(that.app)) {
            return false;
        }
        NetExtension netExtension = this.net;
        if (netExtension == null ? that.net != null : !netExtension.equals(that.net)) {
            return false;
        }
        SdkExtension sdkExtension = this.sdk;
        if (sdkExtension == null ? that.sdk != null : !sdkExtension.equals(that.sdk)) {
            return false;
        }
        LocExtension locExtension = this.loc;
        return locExtension != null ? locExtension.equals(that.loc) : that.loc == null;
    }

    public int hashCode() {
        MetadataExtension metadataExtension = this.metadata;
        int i = 0;
        int result = metadataExtension != null ? metadataExtension.hashCode() : 0;
        int i2 = result * 31;
        ProtocolExtension protocolExtension = this.protocol;
        int result2 = i2 + (protocolExtension != null ? protocolExtension.hashCode() : 0);
        int result3 = result2 * 31;
        UserExtension userExtension = this.user;
        int result4 = (result3 + (userExtension != null ? userExtension.hashCode() : 0)) * 31;
        DeviceExtension deviceExtension = this.device;
        int result5 = (result4 + (deviceExtension != null ? deviceExtension.hashCode() : 0)) * 31;
        OsExtension osExtension = this.os;
        int result6 = (result5 + (osExtension != null ? osExtension.hashCode() : 0)) * 31;
        AppExtension appExtension = this.app;
        int result7 = (result6 + (appExtension != null ? appExtension.hashCode() : 0)) * 31;
        NetExtension netExtension = this.net;
        int result8 = (result7 + (netExtension != null ? netExtension.hashCode() : 0)) * 31;
        SdkExtension sdkExtension = this.sdk;
        int result9 = (result8 + (sdkExtension != null ? sdkExtension.hashCode() : 0)) * 31;
        LocExtension locExtension = this.loc;
        if (locExtension != null) {
            i = locExtension.hashCode();
        }
        return result9 + i;
    }
}
