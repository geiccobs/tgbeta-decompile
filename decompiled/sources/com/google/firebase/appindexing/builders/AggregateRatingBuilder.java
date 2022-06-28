package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class AggregateRatingBuilder extends IndexableBuilder<AggregateRatingBuilder> {
    public AggregateRatingBuilder() {
        super("AggregateRating");
    }

    public AggregateRatingBuilder setRatingCount(long ratingCount) {
        put("ratingCount", ratingCount);
        return this;
    }

    public AggregateRatingBuilder setRatingValue(String ratingValue) {
        put("ratingValue", ratingValue);
        return this;
    }
}
