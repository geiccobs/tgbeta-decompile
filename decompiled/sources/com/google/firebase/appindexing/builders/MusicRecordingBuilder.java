package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class MusicRecordingBuilder extends IndexableBuilder<MusicRecordingBuilder> {
    public MusicRecordingBuilder() {
        super("MusicRecording");
    }

    public MusicRecordingBuilder setByArtist(MusicGroupBuilder artist) {
        put("byArtist", artist);
        return this;
    }

    public MusicRecordingBuilder setDuration(int durationInSeconds) {
        put("duration", durationInSeconds);
        return this;
    }

    public MusicRecordingBuilder setInAlbum(MusicAlbumBuilder musicAlbum) {
        put("inAlbum", musicAlbum);
        return this;
    }

    public MusicRecordingBuilder setInPlaylist(MusicPlaylistBuilder... musicPlaylists) {
        put("inPlaylist", musicPlaylists);
        return this;
    }
}
