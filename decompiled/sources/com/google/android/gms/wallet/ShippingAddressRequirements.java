package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.ArrayList;
import java.util.Collection;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class ShippingAddressRequirements extends AbstractSafeParcelable {
    public static final Parcelable.Creator<ShippingAddressRequirements> CREATOR = new zzag();
    ArrayList<String> zza;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public final class Builder {
        /* synthetic */ Builder(zzaf zzafVar) {
            ShippingAddressRequirements.this = r1;
        }

        public Builder addAllowedCountryCode(String allowedCountryCode) {
            Preconditions.checkNotEmpty(allowedCountryCode, "allowedCountryCode can't be null or empty! If you don't have restrictions, just leave it unset.");
            ShippingAddressRequirements shippingAddressRequirements = ShippingAddressRequirements.this;
            if (shippingAddressRequirements.zza == null) {
                shippingAddressRequirements.zza = new ArrayList<>();
            }
            ShippingAddressRequirements.this.zza.add(allowedCountryCode);
            return this;
        }

        public Builder addAllowedCountryCodes(Collection<String> collection) {
            if (collection == null || collection.isEmpty()) {
                throw new IllegalArgumentException("allowedCountryCodes can't be null or empty! If you don't have restrictions, just leave it unset.");
            }
            ShippingAddressRequirements shippingAddressRequirements = ShippingAddressRequirements.this;
            if (shippingAddressRequirements.zza == null) {
                shippingAddressRequirements.zza = new ArrayList<>();
            }
            ShippingAddressRequirements.this.zza.addAll(collection);
            return this;
        }

        public ShippingAddressRequirements build() {
            return ShippingAddressRequirements.this;
        }
    }

    private ShippingAddressRequirements() {
    }

    public static Builder newBuilder() {
        return new Builder(null);
    }

    public ArrayList<String> getAllowedCountryCodes() {
        return this.zza;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(out);
        SafeParcelWriter.writeStringList(out, 1, this.zza, false);
        SafeParcelWriter.finishObjectHeader(out, beginObjectHeader);
    }

    public ShippingAddressRequirements(ArrayList<String> arrayList) {
        this.zza = arrayList;
    }
}
