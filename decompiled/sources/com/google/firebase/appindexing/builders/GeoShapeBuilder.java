package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class GeoShapeBuilder extends IndexableBuilder<GeoShapeBuilder> {
    public GeoShapeBuilder() {
        super("GeoShape");
    }

    @Deprecated
    public GeoShapeBuilder setBox(String box) {
        put("box", box);
        return this;
    }

    public GeoShapeBuilder setBox(String... box) {
        put("box", box);
        return this;
    }
}
