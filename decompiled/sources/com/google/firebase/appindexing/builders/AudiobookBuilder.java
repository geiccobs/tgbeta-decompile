package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public class AudiobookBuilder extends IndexableBuilder<AudiobookBuilder> {
    public AudiobookBuilder() {
        super("Audiobook");
    }

    public AudiobookBuilder setAuthor(PersonBuilder... personBuilders) {
        return put("author", personBuilders);
    }

    public AudiobookBuilder setReadBy(PersonBuilder... personBuilders) {
        return put("readBy", personBuilders);
    }
}
