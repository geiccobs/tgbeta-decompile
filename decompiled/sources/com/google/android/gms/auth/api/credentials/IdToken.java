package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class IdToken extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<IdToken> CREATOR = new zbf();
    private final String zba;
    private final String zbb;

    public IdToken(String accountType, String idToken) {
        Preconditions.checkArgument(!TextUtils.isEmpty(accountType), "account type string cannot be null or empty");
        Preconditions.checkArgument(!TextUtils.isEmpty(idToken), "id token string cannot be null or empty");
        this.zba = accountType;
        this.zbb = idToken;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IdToken)) {
            return false;
        }
        IdToken idToken = (IdToken) obj;
        return Objects.equal(this.zba, idToken.zba) && Objects.equal(this.zbb, idToken.zbb);
    }

    public String getAccountType() {
        return this.zba;
    }

    public String getIdToken() {
        return this.zbb;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeString(out, 1, getAccountType(), false);
        SafeParcelWriter.writeString(out, 2, getIdToken(), false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
