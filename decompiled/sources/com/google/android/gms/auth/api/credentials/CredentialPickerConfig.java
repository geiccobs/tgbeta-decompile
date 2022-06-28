package com.google.android.gms.auth.api.credentials;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class CredentialPickerConfig extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Parcelable.Creator<CredentialPickerConfig> CREATOR = new zbb();
    final int zba;
    private final boolean zbb;
    private final boolean zbc;
    private final int zbd;

    /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private boolean zba = false;
        private boolean zbb = true;
        private int zbc = 1;

        public CredentialPickerConfig build() {
            return new CredentialPickerConfig(2, this.zba, this.zbb, false, this.zbc);
        }

        @Deprecated
        public Builder setForNewAccount(boolean z) {
            int i = 1;
            if (true == z) {
                i = 3;
            }
            this.zbc = i;
            return this;
        }

        public Builder setPrompt(int i) {
            this.zbc = i;
            return this;
        }

        public Builder setShowAddAccountButton(boolean z) {
            this.zba = z;
            return this;
        }

        public Builder setShowCancelButton(boolean z) {
            this.zbb = z;
            return this;
        }
    }

    /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Prompt {
        public static final int CONTINUE = 1;
        public static final int SIGN_IN = 2;
        public static final int SIGN_UP = 3;
    }

    public CredentialPickerConfig(int i, boolean z, boolean z2, boolean z3, int i2) {
        this.zba = i;
        this.zbb = z;
        this.zbc = z2;
        if (i < 2) {
            this.zbd = true == z3 ? 3 : 1;
        } else {
            this.zbd = i2;
        }
    }

    @Deprecated
    public boolean isForNewAccount() {
        return this.zbd == 3;
    }

    public boolean shouldShowAddAccountButton() {
        return this.zbb;
    }

    public boolean shouldShowCancelButton() {
        return this.zbc;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeBoolean(out, 1, shouldShowAddAccountButton());
        SafeParcelWriter.writeBoolean(out, 2, shouldShowCancelButton());
        SafeParcelWriter.writeBoolean(out, 3, isForNewAccount());
        SafeParcelWriter.writeInt(out, 4, this.zbd);
        SafeParcelWriter.writeInt(out, 1000, this.zba);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }
}
