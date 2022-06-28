package com.google.android.gms.auth.api.identity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public class SignInPassword extends AbstractSafeParcelable {
    public static final Parcelable.Creator<SignInPassword> CREATOR = new zbm();
    private final String zba;
    private final String zbb;

    public SignInPassword(String id, String password) {
        this.zba = Preconditions.checkNotEmpty(((String) Preconditions.checkNotNull(id, "Account identifier cannot be null")).trim(), "Account identifier cannot be empty");
        this.zbb = Preconditions.checkNotEmpty(password);
    }

    public boolean equals(Object o) {
        if (!(o instanceof SignInPassword)) {
            return false;
        }
        SignInPassword signInPassword = (SignInPassword) o;
        return Objects.equal(this.zba, signInPassword.zba) && Objects.equal(this.zbb, signInPassword.zbb);
    }

    public String getId() {
        return this.zba;
    }

    public String getPassword() {
        return this.zbb;
    }

    public int hashCode() {
        return Objects.hashCode(this.zba, this.zbb);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeString(dest, 1, getId(), false);
        SafeParcelWriter.writeString(dest, 2, getPassword(), false);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }
}
