package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class MusicGroupBuilder extends IndexableBuilder<MusicGroupBuilder> {
    public MusicGroupBuilder() {
        super("MusicGroup");
    }

    public MusicGroupBuilder setAlbum(MusicAlbumBuilder... musicAlbums) {
        put("album", musicAlbums);
        return this;
    }

    public MusicGroupBuilder setGenre(String genre) {
        put("genre", genre);
        return this;
    }

    public MusicGroupBuilder setTrack(MusicRecordingBuilder... tracks) {
        put("track", tracks);
        return this;
    }
}
