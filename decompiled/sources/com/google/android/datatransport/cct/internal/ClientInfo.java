package com.google.android.datatransport.cct.internal;

import com.google.android.datatransport.cct.internal.AutoValue_ClientInfo;
/* loaded from: classes3.dex */
public abstract class ClientInfo {

    /* loaded from: classes3.dex */
    public static abstract class Builder {
        public abstract ClientInfo build();

        public abstract Builder setAndroidClientInfo(AndroidClientInfo androidClientInfo);

        public abstract Builder setClientType(ClientType clientType);
    }

    public abstract AndroidClientInfo getAndroidClientInfo();

    public abstract ClientType getClientType();

    /* loaded from: classes3.dex */
    public enum ClientType {
        UNKNOWN(0),
        ANDROID_FIREBASE(23);
        
        private final int value;

        ClientType(int value) {
            this.value = value;
        }
    }

    public static Builder builder() {
        return new AutoValue_ClientInfo.Builder();
    }
}
