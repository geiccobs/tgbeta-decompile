package com.google.firebase.appindexing.builders;

import java.util.Date;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class VideoObjectBuilder extends IndexableBuilder<VideoObjectBuilder> {
    VideoObjectBuilder() {
        super("VideoObject");
    }

    public VideoObjectBuilder setAuthor(PersonBuilder author) {
        put("author", author);
        return this;
    }

    public VideoObjectBuilder setDuration(long durationInSeconds) {
        put("duration", durationInSeconds);
        return this;
    }

    public VideoObjectBuilder setDurationWatched(long durationWatchedInSeconds) {
        put("durationWatched", durationWatchedInSeconds);
        return this;
    }

    public VideoObjectBuilder setLocationCreated(PlaceBuilder place) {
        put("locationCreated", place);
        return this;
    }

    public VideoObjectBuilder setSeriesName(String seriesName) {
        put("seriesName", seriesName);
        return this;
    }

    public VideoObjectBuilder setUploadDate(Date uploadDate) {
        put("uploadDate", uploadDate.getTime());
        return this;
    }
}
