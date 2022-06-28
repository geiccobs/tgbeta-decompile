package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public class BookBuilder extends IndexableBuilder<BookBuilder> {
    public BookBuilder() {
        super("Book");
    }

    public BookBuilder setAuthor(PersonBuilder... personBuilders) {
        return put("author", personBuilders);
    }
}
