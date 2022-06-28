package com.google.android.exoplayer2.trackselection;

import android.os.SystemClock;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import java.util.List;
import java.util.Random;
/* loaded from: classes3.dex */
public final class RandomTrackSelection extends BaseTrackSelection {
    private final Random random;
    private int selectedIndex;

    /* loaded from: classes3.dex */
    public static final class Factory implements TrackSelection.Factory {
        private final Random random;

        public Factory() {
            this.random = new Random();
        }

        public Factory(int seed) {
            this.random = new Random(seed);
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection.Factory
        public TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitions, BandwidthMeter bandwidthMeter) {
            return TrackSelectionUtil.createTrackSelectionsForDefinitions(definitions, new TrackSelectionUtil.AdaptiveTrackSelectionFactory() { // from class: com.google.android.exoplayer2.trackselection.RandomTrackSelection$Factory$$ExternalSyntheticLambda0
                @Override // com.google.android.exoplayer2.trackselection.TrackSelectionUtil.AdaptiveTrackSelectionFactory
                public final TrackSelection createAdaptiveTrackSelection(TrackSelection.Definition definition) {
                    return RandomTrackSelection.Factory.this.m75x1ff1e6e5(definition);
                }
            });
        }

        /* renamed from: lambda$createTrackSelections$0$com-google-android-exoplayer2-trackselection-RandomTrackSelection$Factory */
        public /* synthetic */ TrackSelection m75x1ff1e6e5(TrackSelection.Definition definition) {
            return new RandomTrackSelection(definition.group, definition.tracks, this.random);
        }
    }

    public RandomTrackSelection(TrackGroup group, int... tracks) {
        super(group, tracks);
        Random random = new Random();
        this.random = random;
        this.selectedIndex = random.nextInt(this.length);
    }

    public RandomTrackSelection(TrackGroup group, int[] tracks, long seed) {
        this(group, tracks, new Random(seed));
    }

    public RandomTrackSelection(TrackGroup group, int[] tracks, Random random) {
        super(group, tracks);
        this.random = random;
        this.selectedIndex = random.nextInt(this.length);
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> queue, MediaChunkIterator[] mediaChunkIterators) {
        long nowMs = SystemClock.elapsedRealtime();
        int nonBlacklistedFormatCount = 0;
        for (int i = 0; i < this.length; i++) {
            if (!isBlacklisted(i, nowMs)) {
                nonBlacklistedFormatCount++;
            }
        }
        this.selectedIndex = this.random.nextInt(nonBlacklistedFormatCount);
        if (nonBlacklistedFormatCount != this.length) {
            int nonBlacklistedFormatCount2 = 0;
            for (int i2 = 0; i2 < this.length; i2++) {
                if (!isBlacklisted(i2, nowMs)) {
                    int nonBlacklistedFormatCount3 = nonBlacklistedFormatCount2 + 1;
                    if (this.selectedIndex != nonBlacklistedFormatCount2) {
                        nonBlacklistedFormatCount2 = nonBlacklistedFormatCount3;
                    } else {
                        this.selectedIndex = i2;
                        return;
                    }
                }
            }
        }
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public int getSelectionReason() {
        return 3;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public Object getSelectionData() {
        return null;
    }
}
