package com.google.android.gms.auth.api.credentials;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes.dex */
public class Credential extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<Credential> CREATOR = new zba();
    public static final String EXTRA_KEY = "com.google.android.gms.credentials.Credential";
    @Nonnull
    private final String zba;
    private final String zbb;
    private final Uri zbc;
    @Nonnull
    private final List<IdToken> zbd;
    private final String zbe;
    private final String zbf;
    private final String zbg;
    private final String zbh;

    /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private final String zba;
        private String zbb;
        private Uri zbc;
        private List<IdToken> zbd;
        private String zbe;
        private String zbf;
        private String zbg;
        private String zbh;

        public Builder(Credential credential) {
            this.zba = credential.zba;
            this.zbb = credential.zbb;
            this.zbc = credential.zbc;
            this.zbd = credential.zbd;
            this.zbe = credential.zbe;
            this.zbf = credential.zbf;
            this.zbg = credential.zbg;
            this.zbh = credential.zbh;
        }

        public Builder(String str) {
            this.zba = str;
        }

        public Credential build() {
            return new Credential(this.zba, this.zbb, this.zbc, this.zbd, this.zbe, this.zbf, this.zbg, this.zbh);
        }

        public Builder setAccountType(String str) {
            this.zbf = str;
            return this;
        }

        public Builder setName(String str) {
            this.zbb = str;
            return this;
        }

        public Builder setPassword(String str) {
            this.zbe = str;
            return this;
        }

        public Builder setProfilePictureUri(Uri uri) {
            this.zbc = uri;
            return this;
        }
    }

    public Credential(String str, String str2, Uri uri, List<IdToken> list, String str3, String str4, String str5, String str6) {
        Boolean bool;
        String trim = ((String) Preconditions.checkNotNull(str, "credential identifier cannot be null")).trim();
        Preconditions.checkNotEmpty(trim, "credential identifier cannot be empty");
        if (str3 == null || !TextUtils.isEmpty(str3)) {
            if (str4 != null) {
                boolean z = false;
                if (TextUtils.isEmpty(str4)) {
                    bool = false;
                } else {
                    Uri parse = Uri.parse(str4);
                    if (!parse.isAbsolute() || !parse.isHierarchical() || TextUtils.isEmpty(parse.getScheme()) || TextUtils.isEmpty(parse.getAuthority())) {
                        bool = false;
                    } else {
                        if ("http".equalsIgnoreCase(parse.getScheme())) {
                            z = true;
                        } else if ("https".equalsIgnoreCase(parse.getScheme())) {
                            z = true;
                        }
                        bool = Boolean.valueOf(z);
                    }
                }
                if (!bool.booleanValue()) {
                    throw new IllegalArgumentException("Account type must be a valid Http/Https URI");
                }
            }
            if (!TextUtils.isEmpty(str4) && !TextUtils.isEmpty(str3)) {
                throw new IllegalArgumentException("Password and AccountType are mutually exclusive");
            }
            if (str2 != null && TextUtils.isEmpty(str2.trim())) {
                str2 = null;
            }
            this.zbb = str2;
            this.zbc = uri;
            this.zbd = list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
            this.zba = trim;
            this.zbe = str3;
            this.zbf = str4;
            this.zbg = str5;
            this.zbh = str6;
            return;
        }
        throw new IllegalArgumentException("Password must not be empty if set");
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Credential)) {
            return false;
        }
        Credential credential = (Credential) other;
        return TextUtils.equals(this.zba, credential.zba) && TextUtils.equals(this.zbb, credential.zbb) && Objects.equal(this.zbc, credential.zbc) && TextUtils.equals(this.zbe, credential.zbe) && TextUtils.equals(this.zbf, credential.zbf);
    }

    public String getAccountType() {
        return this.zbf;
    }

    public String getFamilyName() {
        return this.zbh;
    }

    public String getGivenName() {
        return this.zbg;
    }

    @Nonnull
    public String getId() {
        return this.zba;
    }

    @Nonnull
    public List<IdToken> getIdTokens() {
        return this.zbd;
    }

    public String getName() {
        return this.zbb;
    }

    public String getPassword() {
        return this.zbe;
    }

    public Uri getProfilePictureUri() {
        return this.zbc;
    }

    public int hashCode() {
        return Objects.hashCode(this.zba, this.zbb, this.zbc, this.zbe, this.zbf);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeString(out, 1, getId(), false);
        SafeParcelWriter.writeString(out, 2, getName(), false);
        SafeParcelWriter.writeParcelable(out, 3, getProfilePictureUri(), flags, false);
        SafeParcelWriter.writeTypedList(out, 4, getIdTokens(), false);
        SafeParcelWriter.writeString(out, 5, getPassword(), false);
        SafeParcelWriter.writeString(out, 6, getAccountType(), false);
        SafeParcelWriter.writeString(out, 9, getGivenName(), false);
        SafeParcelWriter.writeString(out, 10, getFamilyName(), false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
