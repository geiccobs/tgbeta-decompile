package com.google.android.exoplayer2.source.hls.playlist;

import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
/* loaded from: classes.dex */
public final /* synthetic */ class DefaultHlsPlaylistTracker$$ExternalSyntheticLambda0 implements HlsPlaylistTracker.Factory {
    public static final /* synthetic */ DefaultHlsPlaylistTracker$$ExternalSyntheticLambda0 INSTANCE = new DefaultHlsPlaylistTracker$$ExternalSyntheticLambda0();

    private /* synthetic */ DefaultHlsPlaylistTracker$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.Factory
    public final HlsPlaylistTracker createTracker(HlsDataSourceFactory hlsDataSourceFactory, LoadErrorHandlingPolicy loadErrorHandlingPolicy, HlsPlaylistParserFactory hlsPlaylistParserFactory) {
        return new DefaultHlsPlaylistTracker(hlsDataSourceFactory, loadErrorHandlingPolicy, hlsPlaylistParserFactory);
    }
}
