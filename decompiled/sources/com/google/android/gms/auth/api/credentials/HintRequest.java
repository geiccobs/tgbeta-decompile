package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class HintRequest extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<HintRequest> CREATOR = new zbe();
    final int zba;
    private final CredentialPickerConfig zbb;
    private final boolean zbc;
    private final boolean zbd;
    private final String[] zbe;
    private final boolean zbf;
    private final String zbg;
    private final String zbh;

    /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private boolean zba;
        private boolean zbb;
        private String[] zbc;
        private CredentialPickerConfig zbd = new CredentialPickerConfig.Builder().build();
        private boolean zbe = false;
        private String zbf;
        private String zbg;

        public HintRequest build() {
            if (this.zbc == null) {
                this.zbc = new String[0];
            }
            boolean z = this.zba;
            if (z || this.zbb || this.zbc.length != 0) {
                return new HintRequest(2, this.zbd, z, this.zbb, this.zbc, this.zbe, this.zbf, this.zbg);
            }
            throw new IllegalStateException("At least one authentication method must be specified");
        }

        public Builder setAccountTypes(String... strArr) {
            if (strArr == null) {
                strArr = new String[0];
            }
            this.zbc = strArr;
            return this;
        }

        public Builder setEmailAddressIdentifierSupported(boolean z) {
            this.zba = z;
            return this;
        }

        public Builder setHintPickerConfig(CredentialPickerConfig hintPickerConfig) {
            this.zbd = (CredentialPickerConfig) Preconditions.checkNotNull(hintPickerConfig);
            return this;
        }

        public Builder setIdTokenNonce(String str) {
            this.zbg = str;
            return this;
        }

        public Builder setIdTokenRequested(boolean z) {
            this.zbe = z;
            return this;
        }

        public Builder setPhoneNumberIdentifierSupported(boolean z) {
            this.zbb = z;
            return this;
        }

        public Builder setServerClientId(String str) {
            this.zbf = str;
            return this;
        }
    }

    public HintRequest(int i, CredentialPickerConfig credentialPickerConfig, boolean z, boolean z2, String[] strArr, boolean z3, String str, String str2) {
        this.zba = i;
        this.zbb = (CredentialPickerConfig) Preconditions.checkNotNull(credentialPickerConfig);
        this.zbc = z;
        this.zbd = z2;
        this.zbe = (String[]) Preconditions.checkNotNull(strArr);
        if (i < 2) {
            this.zbf = true;
            this.zbg = null;
            this.zbh = null;
            return;
        }
        this.zbf = z3;
        this.zbg = str;
        this.zbh = str2;
    }

    public String[] getAccountTypes() {
        return this.zbe;
    }

    public CredentialPickerConfig getHintPickerConfig() {
        return this.zbb;
    }

    public String getIdTokenNonce() {
        return this.zbh;
    }

    public String getServerClientId() {
        return this.zbg;
    }

    public boolean isEmailAddressIdentifierSupported() {
        return this.zbc;
    }

    public boolean isIdTokenRequested() {
        return this.zbf;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeParcelable(out, 1, getHintPickerConfig(), flags, false);
        SafeParcelWriter.writeBoolean(out, 2, isEmailAddressIdentifierSupported());
        SafeParcelWriter.writeBoolean(out, 3, this.zbd);
        SafeParcelWriter.writeStringArray(out, 4, getAccountTypes(), false);
        SafeParcelWriter.writeBoolean(out, 5, isIdTokenRequested());
        SafeParcelWriter.writeString(out, 6, getServerClientId(), false);
        SafeParcelWriter.writeString(out, 7, getIdTokenNonce(), false);
        SafeParcelWriter.writeInt(out, 1000, this.zba);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
