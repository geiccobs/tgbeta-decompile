package com.google.android.gms.auth.api.identity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public class GetSignInIntentRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<GetSignInIntentRequest> CREATOR = new zbd();
    private final String zba;
    private final String zbb;
    private String zbc;
    private final String zbd;

    /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private String zba;
        private String zbb;
        private String zbc;
        private String zbd;

        public GetSignInIntentRequest build() {
            return new GetSignInIntentRequest(this.zba, this.zbb, this.zbc, this.zbd);
        }

        public Builder filterByHostedDomain(String str) {
            this.zbb = str;
            return this;
        }

        public Builder setNonce(String str) {
            this.zbd = str;
            return this;
        }

        public Builder setServerClientId(String serverClientId) {
            Preconditions.checkNotNull(serverClientId);
            this.zba = serverClientId;
            return this;
        }

        public final Builder zba(String str) {
            this.zbc = str;
            return this;
        }
    }

    public GetSignInIntentRequest(String str, String str2, String str3, String str4) {
        Preconditions.checkNotNull(str);
        this.zba = str;
        this.zbb = str2;
        this.zbc = str3;
        this.zbd = str4;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder zba(GetSignInIntentRequest getSignInIntentRequest) {
        Preconditions.checkNotNull(getSignInIntentRequest);
        Builder builder = builder();
        builder.setServerClientId(getSignInIntentRequest.getServerClientId());
        builder.setNonce(getSignInIntentRequest.getNonce());
        builder.filterByHostedDomain(getSignInIntentRequest.getHostedDomainFilter());
        String str = getSignInIntentRequest.zbc;
        if (str != null) {
            builder.zba(str);
        }
        return builder;
    }

    public boolean equals(Object o) {
        if (!(o instanceof GetSignInIntentRequest)) {
            return false;
        }
        GetSignInIntentRequest getSignInIntentRequest = (GetSignInIntentRequest) o;
        return Objects.equal(this.zba, getSignInIntentRequest.zba) && Objects.equal(this.zbd, getSignInIntentRequest.zbd) && Objects.equal(this.zbb, getSignInIntentRequest.zbb);
    }

    public String getHostedDomainFilter() {
        return this.zbb;
    }

    public String getNonce() {
        return this.zbd;
    }

    public String getServerClientId() {
        return this.zba;
    }

    public int hashCode() {
        return Objects.hashCode(this.zba, this.zbb);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeString(dest, 1, getServerClientId(), false);
        SafeParcelWriter.writeString(dest, 2, getHostedDomainFilter(), false);
        SafeParcelWriter.writeString(dest, 3, this.zbc, false);
        SafeParcelWriter.writeString(dest, 4, getNonce(), false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }
}
