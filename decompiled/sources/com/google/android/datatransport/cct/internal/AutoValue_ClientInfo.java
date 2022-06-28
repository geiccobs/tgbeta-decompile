package com.google.android.datatransport.cct.internal;

import com.google.android.datatransport.cct.internal.ClientInfo;
/* loaded from: classes3.dex */
final class AutoValue_ClientInfo extends ClientInfo {
    private final AndroidClientInfo androidClientInfo;
    private final ClientInfo.ClientType clientType;

    private AutoValue_ClientInfo(ClientInfo.ClientType clientType, AndroidClientInfo androidClientInfo) {
        this.clientType = clientType;
        this.androidClientInfo = androidClientInfo;
    }

    @Override // com.google.android.datatransport.cct.internal.ClientInfo
    public ClientInfo.ClientType getClientType() {
        return this.clientType;
    }

    @Override // com.google.android.datatransport.cct.internal.ClientInfo
    public AndroidClientInfo getAndroidClientInfo() {
        return this.androidClientInfo;
    }

    public String toString() {
        return "ClientInfo{clientType=" + this.clientType + ", androidClientInfo=" + this.androidClientInfo + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientInfo)) {
            return false;
        }
        ClientInfo that = (ClientInfo) o;
        ClientInfo.ClientType clientType = this.clientType;
        if (clientType != null ? clientType.equals(that.getClientType()) : that.getClientType() == null) {
            AndroidClientInfo androidClientInfo = this.androidClientInfo;
            if (androidClientInfo == null) {
                if (that.getAndroidClientInfo() == null) {
                    return true;
                }
            } else if (androidClientInfo.equals(that.getAndroidClientInfo())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        ClientInfo.ClientType clientType = this.clientType;
        int i = 0;
        int h$2 = (h$ ^ (clientType == null ? 0 : clientType.hashCode())) * 1000003;
        AndroidClientInfo androidClientInfo = this.androidClientInfo;
        if (androidClientInfo != null) {
            i = androidClientInfo.hashCode();
        }
        return h$2 ^ i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class Builder extends ClientInfo.Builder {
        private AndroidClientInfo androidClientInfo;
        private ClientInfo.ClientType clientType;

        @Override // com.google.android.datatransport.cct.internal.ClientInfo.Builder
        public ClientInfo.Builder setClientType(ClientInfo.ClientType clientType) {
            this.clientType = clientType;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.ClientInfo.Builder
        public ClientInfo.Builder setAndroidClientInfo(AndroidClientInfo androidClientInfo) {
            this.androidClientInfo = androidClientInfo;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.ClientInfo.Builder
        public ClientInfo build() {
            return new AutoValue_ClientInfo(this.clientType, this.androidClientInfo);
        }
    }
}
