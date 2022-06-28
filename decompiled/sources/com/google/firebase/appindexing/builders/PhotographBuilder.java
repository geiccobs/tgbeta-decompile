package com.google.firebase.appindexing.builders;

import java.util.Date;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class PhotographBuilder extends IndexableBuilder<PhotographBuilder> {
    PhotographBuilder() {
        super("Photograph");
    }

    public PhotographBuilder setDateCreated(Date dateCreated) {
        put("dateCreated", dateCreated.getTime());
        return this;
    }

    public PhotographBuilder setLocationCreated(PlaceBuilder place) {
        put("locationCreated", place);
        return this;
    }
}
