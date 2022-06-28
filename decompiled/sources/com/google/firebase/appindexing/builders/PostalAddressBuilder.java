package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class PostalAddressBuilder extends IndexableBuilder<PostalAddressBuilder> {
    public PostalAddressBuilder() {
        super("PostalAddress");
    }

    public PostalAddressBuilder setAddressCountry(String country) {
        put("addressCountry", country);
        return this;
    }

    public PostalAddressBuilder setAddressLocality(String addressLocality) {
        put("addressLocality", addressLocality);
        return this;
    }

    public PostalAddressBuilder setPostalCode(String postalCode) {
        put("postalCode", postalCode);
        return this;
    }

    public PostalAddressBuilder setStreetAddress(String streetAddress) {
        put("streetAddress", streetAddress);
        return this;
    }
}
