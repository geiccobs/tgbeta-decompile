package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class MusicAlbumBuilder extends IndexableBuilder<MusicAlbumBuilder> {
    public MusicAlbumBuilder() {
        super("MusicAlbum");
    }

    public MusicAlbumBuilder setByArtist(MusicGroupBuilder artist) {
        put("byArtist", artist);
        return this;
    }

    public MusicAlbumBuilder setNumTracks(int numTracks) {
        put("numTracks", numTracks);
        return this;
    }

    public MusicAlbumBuilder setTrack(MusicRecordingBuilder... tracks) {
        put("track", tracks);
        return this;
    }
}
