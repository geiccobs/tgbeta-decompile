package com.google.android.gms.auth.api.identity;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public class SaveAccountLinkingTokenRequest extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<SaveAccountLinkingTokenRequest> CREATOR = new zbg();
    public static final String EXTRA_TOKEN = "extra_token";
    public static final String TOKEN_TYPE_AUTH_CODE = "auth_code";
    private final PendingIntent zba;
    private final String zbb;
    private final String zbc;
    private final List<String> zbd;
    private final String zbe;

    public SaveAccountLinkingTokenRequest(PendingIntent pendingIntent, String str, String str2, List<String> list, String str3) {
        this.zba = pendingIntent;
        this.zbb = str;
        this.zbc = str2;
        this.zbd = list;
        this.zbe = str3;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder zba(SaveAccountLinkingTokenRequest saveAccountLinkingTokenRequest) {
        Preconditions.checkNotNull(saveAccountLinkingTokenRequest);
        Builder builder = builder();
        builder.setScopes(saveAccountLinkingTokenRequest.getScopes());
        builder.setServiceId(saveAccountLinkingTokenRequest.getServiceId());
        builder.setConsentPendingIntent(saveAccountLinkingTokenRequest.getConsentPendingIntent());
        builder.setTokenType(saveAccountLinkingTokenRequest.getTokenType());
        String str = saveAccountLinkingTokenRequest.zbe;
        if (!TextUtils.isEmpty(str)) {
            builder.zba(str);
        }
        return builder;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SaveAccountLinkingTokenRequest)) {
            return false;
        }
        SaveAccountLinkingTokenRequest saveAccountLinkingTokenRequest = (SaveAccountLinkingTokenRequest) o;
        return this.zbd.size() == saveAccountLinkingTokenRequest.zbd.size() && this.zbd.containsAll(saveAccountLinkingTokenRequest.zbd) && Objects.equal(this.zba, saveAccountLinkingTokenRequest.zba) && Objects.equal(this.zbb, saveAccountLinkingTokenRequest.zbb) && Objects.equal(this.zbc, saveAccountLinkingTokenRequest.zbc) && Objects.equal(this.zbe, saveAccountLinkingTokenRequest.zbe);
    }

    public PendingIntent getConsentPendingIntent() {
        return this.zba;
    }

    public List<String> getScopes() {
        return this.zbd;
    }

    public String getServiceId() {
        return this.zbc;
    }

    public String getTokenType() {
        return this.zbb;
    }

    public int hashCode() {
        return Objects.hashCode(this.zba, this.zbb, this.zbc, this.zbd, this.zbe);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeParcelable(dest, 1, getConsentPendingIntent(), flags, false);
        SafeParcelWriter.writeString(dest, 2, getTokenType(), false);
        SafeParcelWriter.writeString(dest, 3, getServiceId(), false);
        SafeParcelWriter.writeStringList(dest, 4, getScopes(), false);
        SafeParcelWriter.writeString(dest, 5, this.zbe, false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private PendingIntent zba;
        private String zbb;
        private String zbc;
        private List<String> zbd = new ArrayList();
        private String zbe;

        public Builder setConsentPendingIntent(PendingIntent pendingIntent) {
            this.zba = pendingIntent;
            return this;
        }

        public Builder setScopes(List<String> list) {
            this.zbd = list;
            return this;
        }

        public Builder setServiceId(String str) {
            this.zbc = str;
            return this;
        }

        public Builder setTokenType(String str) {
            this.zbb = str;
            return this;
        }

        public final Builder zba(String str) {
            this.zbe = str;
            return this;
        }

        public SaveAccountLinkingTokenRequest build() {
            boolean z = false;
            Preconditions.checkArgument(this.zba != null, "Consent PendingIntent cannot be null");
            Preconditions.checkArgument(SaveAccountLinkingTokenRequest.TOKEN_TYPE_AUTH_CODE.equals(this.zbb), "Invalid tokenType");
            Preconditions.checkArgument(!TextUtils.isEmpty(this.zbc), "serviceId cannot be null or empty");
            if (this.zbd != null) {
                z = true;
            }
            Preconditions.checkArgument(z, "scopes cannot be null");
            return new SaveAccountLinkingTokenRequest(this.zba, this.zbb, this.zbc, this.zbd, this.zbe);
        }
    }
}
