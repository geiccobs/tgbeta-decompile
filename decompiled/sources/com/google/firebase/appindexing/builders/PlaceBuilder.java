package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class PlaceBuilder extends IndexableBuilder<PlaceBuilder> {
    public PlaceBuilder() {
        super("Place");
    }

    public PlaceBuilder setGeo(GeoShapeBuilder geoShape) {
        put("geo", geoShape);
        return this;
    }
}
