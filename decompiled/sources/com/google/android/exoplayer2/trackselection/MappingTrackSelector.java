package com.google.android.exoplayer2.trackselection;

import android.util.Pair;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes.dex */
public abstract class MappingTrackSelector extends TrackSelector {
    protected abstract Pair<RendererConfiguration[], TrackSelection[]> selectTracks(MappedTrackInfo mappedTrackInfo, int[][][] iArr, int[] iArr2) throws ExoPlaybackException;

    /* loaded from: classes.dex */
    public static final class MappedTrackInfo {
        private final int rendererCount;
        private final TrackGroupArray[] rendererTrackGroups;
        private final int[] rendererTrackTypes;

        MappedTrackInfo(int[] iArr, TrackGroupArray[] trackGroupArrayArr, int[] iArr2, int[][][] iArr3, TrackGroupArray trackGroupArray) {
            this.rendererTrackTypes = iArr;
            this.rendererTrackGroups = trackGroupArrayArr;
            this.rendererCount = iArr.length;
        }

        public int getRendererCount() {
            return this.rendererCount;
        }

        public int getRendererType(int i) {
            return this.rendererTrackTypes[i];
        }

        public TrackGroupArray getTrackGroups(int i) {
            return this.rendererTrackGroups[i];
        }
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelector
    public final void onSelectionActivated(Object obj) {
        MappedTrackInfo mappedTrackInfo = (MappedTrackInfo) obj;
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelector
    public final TrackSelectorResult selectTracks(RendererCapabilities[] rendererCapabilitiesArr, TrackGroupArray trackGroupArray, MediaSource.MediaPeriodId mediaPeriodId, Timeline timeline) throws ExoPlaybackException {
        int[] iArr;
        int[] iArr2 = new int[rendererCapabilitiesArr.length + 1];
        int length = rendererCapabilitiesArr.length + 1;
        TrackGroup[][] trackGroupArr = new TrackGroup[length];
        int[][][] iArr3 = new int[rendererCapabilitiesArr.length + 1][];
        for (int i = 0; i < length; i++) {
            int i2 = trackGroupArray.length;
            trackGroupArr[i] = new TrackGroup[i2];
            iArr3[i] = new int[i2];
        }
        int[] mixedMimeTypeAdaptationSupports = getMixedMimeTypeAdaptationSupports(rendererCapabilitiesArr);
        for (int i3 = 0; i3 < trackGroupArray.length; i3++) {
            TrackGroup trackGroup = trackGroupArray.get(i3);
            int findRenderer = findRenderer(rendererCapabilitiesArr, trackGroup, iArr2, MimeTypes.getTrackType(trackGroup.getFormat(0).sampleMimeType) == 4);
            if (findRenderer == rendererCapabilitiesArr.length) {
                iArr = new int[trackGroup.length];
            } else {
                iArr = getFormatSupport(rendererCapabilitiesArr[findRenderer], trackGroup);
            }
            int i4 = iArr2[findRenderer];
            trackGroupArr[findRenderer][i4] = trackGroup;
            iArr3[findRenderer][i4] = iArr;
            iArr2[findRenderer] = iArr2[findRenderer] + 1;
        }
        TrackGroupArray[] trackGroupArrayArr = new TrackGroupArray[rendererCapabilitiesArr.length];
        int[] iArr4 = new int[rendererCapabilitiesArr.length];
        for (int i5 = 0; i5 < rendererCapabilitiesArr.length; i5++) {
            int i6 = iArr2[i5];
            trackGroupArrayArr[i5] = new TrackGroupArray((TrackGroup[]) Util.nullSafeArrayCopy(trackGroupArr[i5], i6));
            iArr3[i5] = (int[][]) Util.nullSafeArrayCopy(iArr3[i5], i6);
            iArr4[i5] = rendererCapabilitiesArr[i5].getTrackType();
        }
        MappedTrackInfo mappedTrackInfo = new MappedTrackInfo(iArr4, trackGroupArrayArr, mixedMimeTypeAdaptationSupports, iArr3, new TrackGroupArray((TrackGroup[]) Util.nullSafeArrayCopy(trackGroupArr[rendererCapabilitiesArr.length], iArr2[rendererCapabilitiesArr.length])));
        Pair<RendererConfiguration[], TrackSelection[]> selectTracks = selectTracks(mappedTrackInfo, iArr3, mixedMimeTypeAdaptationSupports);
        return new TrackSelectorResult((RendererConfiguration[]) selectTracks.first, (TrackSelection[]) selectTracks.second, mappedTrackInfo);
    }

    private static int findRenderer(RendererCapabilities[] rendererCapabilitiesArr, TrackGroup trackGroup, int[] iArr, boolean z) throws ExoPlaybackException {
        int length = rendererCapabilitiesArr.length;
        int i = 0;
        boolean z2 = true;
        for (int i2 = 0; i2 < rendererCapabilitiesArr.length; i2++) {
            RendererCapabilities rendererCapabilities = rendererCapabilitiesArr[i2];
            int i3 = 0;
            for (int i4 = 0; i4 < trackGroup.length; i4++) {
                i3 = Math.max(i3, RendererCapabilities.CC.getFormatSupport(rendererCapabilities.supportsFormat(trackGroup.getFormat(i4))));
            }
            boolean z3 = iArr[i2] == 0;
            if (i3 > i || (i3 == i && z && !z2 && z3)) {
                length = i2;
                z2 = z3;
                i = i3;
            }
        }
        return length;
    }

    private static int[] getFormatSupport(RendererCapabilities rendererCapabilities, TrackGroup trackGroup) throws ExoPlaybackException {
        int[] iArr = new int[trackGroup.length];
        for (int i = 0; i < trackGroup.length; i++) {
            iArr[i] = rendererCapabilities.supportsFormat(trackGroup.getFormat(i));
        }
        return iArr;
    }

    private static int[] getMixedMimeTypeAdaptationSupports(RendererCapabilities[] rendererCapabilitiesArr) throws ExoPlaybackException {
        int length = rendererCapabilitiesArr.length;
        int[] iArr = new int[length];
        for (int i = 0; i < length; i++) {
            iArr[i] = rendererCapabilitiesArr[i].supportsMixedMimeTypeAdaptation();
        }
        return iArr;
    }
}
