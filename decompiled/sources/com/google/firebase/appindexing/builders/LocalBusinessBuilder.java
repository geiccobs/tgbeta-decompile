package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class LocalBusinessBuilder extends IndexableBuilder<LocalBusinessBuilder> {
    public LocalBusinessBuilder() {
        super("LocalBusiness");
    }

    public LocalBusinessBuilder setAddress(PostalAddressBuilder address) {
        put("address", address);
        return this;
    }

    public LocalBusinessBuilder setAggregateRating(AggregateRatingBuilder aggregateRating) {
        put("aggregateRating", aggregateRating);
        return this;
    }

    public LocalBusinessBuilder setGeo(GeoShapeBuilder geoShape) {
        put("geo", geoShape);
        return this;
    }

    public LocalBusinessBuilder setPriceRange(String priceRange) {
        put("priceRange", priceRange);
        return this;
    }

    public LocalBusinessBuilder setTelephone(String telephone) {
        put("telephone", telephone);
        return this;
    }

    public LocalBusinessBuilder(String str) {
        super("Restaurant");
    }
}
