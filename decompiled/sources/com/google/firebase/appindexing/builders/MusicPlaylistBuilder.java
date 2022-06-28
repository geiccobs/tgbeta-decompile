package com.google.firebase.appindexing.builders;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public class MusicPlaylistBuilder extends IndexableBuilder<MusicPlaylistBuilder> {
    public MusicPlaylistBuilder() {
        super("MusicPlaylist");
    }

    public MusicPlaylistBuilder setNumTracks(int numTracks) {
        return put("numTracks", numTracks);
    }

    public MusicPlaylistBuilder setTrack(MusicRecordingBuilder... tracks) {
        return put("track", tracks);
    }
}
