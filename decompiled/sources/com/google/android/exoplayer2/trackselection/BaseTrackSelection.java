package com.google.android.exoplayer2.trackselection;

import android.os.SystemClock;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes3.dex */
public abstract class BaseTrackSelection implements TrackSelection {
    private final long[] blacklistUntilTimes;
    private final Format[] formats;
    protected final TrackGroup group;
    private int hashCode;
    protected final int length;
    protected final int[] tracks;

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public /* synthetic */ void onDiscontinuity() {
        TrackSelection.CC.$default$onDiscontinuity(this);
    }

    public BaseTrackSelection(TrackGroup group, int... tracks) {
        Assertions.checkState(tracks.length > 0);
        this.group = (TrackGroup) Assertions.checkNotNull(group);
        int length = tracks.length;
        this.length = length;
        this.formats = new Format[length];
        for (int i = 0; i < tracks.length; i++) {
            this.formats[i] = group.getFormat(tracks[i]);
        }
        Arrays.sort(this.formats, new DecreasingBandwidthComparator());
        this.tracks = new int[this.length];
        int i2 = 0;
        while (true) {
            int i3 = this.length;
            if (i2 < i3) {
                this.tracks[i2] = group.indexOf(this.formats[i2]);
                i2++;
            } else {
                this.blacklistUntilTimes = new long[i3];
                return;
            }
        }
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public void enable() {
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public void disable() {
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final TrackGroup getTrackGroup() {
        return this.group;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final int length() {
        return this.tracks.length;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final Format getFormat(int index) {
        return this.formats[index];
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final int getIndexInTrackGroup(int index) {
        return this.tracks[index];
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final int indexOf(Format format) {
        for (int i = 0; i < this.length; i++) {
            if (this.formats[i] == format) {
                return i;
            }
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final int indexOf(int indexInTrackGroup) {
        for (int i = 0; i < this.length; i++) {
            if (this.tracks[i] == indexInTrackGroup) {
                return i;
            }
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final Format getSelectedFormat() {
        return this.formats[getSelectedIndex()];
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final int getSelectedIndexInTrackGroup() {
        return this.tracks[getSelectedIndex()];
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public void onPlaybackSpeed(float playbackSpeed) {
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public int evaluateQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        return queue.size();
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelection
    public final boolean blacklist(int index, long blacklistDurationMs) {
        long nowMs = SystemClock.elapsedRealtime();
        boolean canBlacklist = isBlacklisted(index, nowMs);
        int i = 0;
        boolean canBlacklist2 = canBlacklist;
        while (true) {
            boolean z = false;
            if (i >= this.length || canBlacklist2) {
                break;
            }
            if (i != index && !isBlacklisted(i, nowMs)) {
                z = true;
            }
            canBlacklist2 = z;
            i++;
        }
        if (!canBlacklist2) {
            return false;
        }
        long[] jArr = this.blacklistUntilTimes;
        jArr[index] = Math.max(jArr[index], Util.addWithOverflowDefault(nowMs, blacklistDurationMs, Long.MAX_VALUE));
        return true;
    }

    public final boolean isBlacklisted(int index, long nowMs) {
        return this.blacklistUntilTimes[index] > nowMs;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (System.identityHashCode(this.group) * 31) + Arrays.hashCode(this.tracks);
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BaseTrackSelection other = (BaseTrackSelection) obj;
        return this.group == other.group && Arrays.equals(this.tracks, other.tracks);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class DecreasingBandwidthComparator implements Comparator<Format> {
        private DecreasingBandwidthComparator() {
        }

        public int compare(Format a, Format b) {
            return b.bitrate - a.bitrate;
        }
    }
}
