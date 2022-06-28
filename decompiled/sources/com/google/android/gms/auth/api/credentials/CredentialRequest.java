package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class CredentialRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<CredentialRequest> CREATOR = new zbc();
    final int zba;
    private final boolean zbb;
    private final String[] zbc;
    private final CredentialPickerConfig zbd;
    private final CredentialPickerConfig zbe;
    private final boolean zbf;
    private final String zbg;
    private final String zbh;
    private final boolean zbi;

    /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private boolean zba;
        private String[] zbb;
        private CredentialPickerConfig zbc;
        private CredentialPickerConfig zbd;
        private boolean zbe = false;
        private String zbf = null;
        private String zbg;

        public CredentialRequest build() {
            if (this.zbb == null) {
                this.zbb = new String[0];
            }
            boolean z = this.zba;
            if (z || this.zbb.length != 0) {
                return new CredentialRequest(4, z, this.zbb, this.zbc, this.zbd, this.zbe, this.zbf, this.zbg, false);
            }
            throw new IllegalStateException("At least one authentication method must be specified");
        }

        public Builder setAccountTypes(String... strArr) {
            if (strArr == null) {
                strArr = new String[0];
            }
            this.zbb = strArr;
            return this;
        }

        public Builder setCredentialHintPickerConfig(CredentialPickerConfig credentialPickerConfig) {
            this.zbd = credentialPickerConfig;
            return this;
        }

        public Builder setCredentialPickerConfig(CredentialPickerConfig credentialPickerConfig) {
            this.zbc = credentialPickerConfig;
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

        public Builder setPasswordLoginSupported(boolean z) {
            this.zba = z;
            return this;
        }

        public Builder setServerClientId(String str) {
            this.zbf = str;
            return this;
        }

        @Deprecated
        public Builder setSupportsPasswordLogin(boolean supportsPasswordLogin) {
            setPasswordLoginSupported(supportsPasswordLogin);
            return this;
        }
    }

    public CredentialRequest(int i, boolean z, String[] strArr, CredentialPickerConfig credentialPickerConfig, CredentialPickerConfig credentialPickerConfig2, boolean z2, String str, String str2, boolean z3) {
        this.zba = i;
        this.zbb = z;
        this.zbc = (String[]) Preconditions.checkNotNull(strArr);
        this.zbd = credentialPickerConfig == null ? new CredentialPickerConfig.Builder().build() : credentialPickerConfig;
        this.zbe = credentialPickerConfig2 == null ? new CredentialPickerConfig.Builder().build() : credentialPickerConfig2;
        if (i < 3) {
            this.zbf = true;
            this.zbg = null;
            this.zbh = null;
        } else {
            this.zbf = z2;
            this.zbg = str;
            this.zbh = str2;
        }
        this.zbi = z3;
    }

    public String[] getAccountTypes() {
        return this.zbc;
    }

    public Set<String> getAccountTypesSet() {
        return new HashSet(Arrays.asList(this.zbc));
    }

    public CredentialPickerConfig getCredentialHintPickerConfig() {
        return this.zbe;
    }

    public CredentialPickerConfig getCredentialPickerConfig() {
        return this.zbd;
    }

    public String getIdTokenNonce() {
        return this.zbh;
    }

    public String getServerClientId() {
        return this.zbg;
    }

    @Deprecated
    public boolean getSupportsPasswordLogin() {
        return isPasswordLoginSupported();
    }

    public boolean isIdTokenRequested() {
        return this.zbf;
    }

    public boolean isPasswordLoginSupported() {
        return this.zbb;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeBoolean(out, 1, isPasswordLoginSupported());
        SafeParcelWriter.writeStringArray(out, 2, getAccountTypes(), false);
        SafeParcelWriter.writeParcelable(out, 3, getCredentialPickerConfig(), flags, false);
        SafeParcelWriter.writeParcelable(out, 4, getCredentialHintPickerConfig(), flags, false);
        SafeParcelWriter.writeBoolean(out, 5, isIdTokenRequested());
        SafeParcelWriter.writeString(out, 6, getServerClientId(), false);
        SafeParcelWriter.writeString(out, 7, getIdTokenNonce(), false);
        SafeParcelWriter.writeBoolean(out, 8, this.zbi);
        SafeParcelWriter.writeInt(out, 1000, this.zba);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
