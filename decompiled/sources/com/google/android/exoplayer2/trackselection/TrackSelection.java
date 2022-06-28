package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import java.util.List;
/* loaded from: classes3.dex */
public interface TrackSelection {

    /* loaded from: classes3.dex */
    public interface Factory {
        TrackSelection[] createTrackSelections(Definition[] definitionArr, BandwidthMeter bandwidthMeter);
    }

    boolean blacklist(int i, long j);

    void disable();

    void enable();

    int evaluateQueueSize(long j, List<? extends MediaChunk> list);

    Format getFormat(int i);

    int getIndexInTrackGroup(int i);

    Format getSelectedFormat();

    int getSelectedIndex();

    int getSelectedIndexInTrackGroup();

    Object getSelectionData();

    int getSelectionReason();

    TrackGroup getTrackGroup();

    int indexOf(int i);

    int indexOf(Format format);

    int length();

    void onDiscontinuity();

    void onPlaybackSpeed(float f);

    void updateSelectedTrack(long j, long j2, long j3, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIteratorArr);

    /* loaded from: classes3.dex */
    public static final class Definition {
        public final Object data;
        public final TrackGroup group;
        public final int reason;
        public final int[] tracks;

        public Definition(TrackGroup group, int... tracks) {
            this(group, tracks, 0, null);
        }

        public Definition(TrackGroup group, int[] tracks, int reason, Object data) {
            this.group = group;
            this.tracks = tracks;
            this.reason = reason;
            this.data = data;
        }
    }

    /* renamed from: com.google.android.exoplayer2.trackselection.TrackSelection$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static void $default$onDiscontinuity(TrackSelection _this) {
        }
    }
}
