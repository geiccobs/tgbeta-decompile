package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public final class HlsMediaPeriod implements MediaPeriod, HlsSampleStreamWrapper.Callback, HlsPlaylistTracker.PlaylistEventListener {
    private final Allocator allocator;
    private final boolean allowChunklessPreparation;
    private MediaPeriod.Callback callback;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private final DrmSessionManager<?> drmSessionManager;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private final HlsExtractorFactory extractorFactory;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private final TransferListener mediaTransferListener;
    private final int metadataType;
    private boolean notifiedReadingStarted;
    private int pendingPrepareCount;
    private final HlsPlaylistTracker playlistTracker;
    private TrackGroupArray trackGroups;
    private final boolean useSessionKeys;
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap<>();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private HlsSampleStreamWrapper[] sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private int[][] manifestUrlIndicesPerWrapper = new int[0];

    public HlsMediaPeriod(HlsExtractorFactory extractorFactory, HlsPlaylistTracker playlistTracker, HlsDataSourceFactory dataSourceFactory, TransferListener mediaTransferListener, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, boolean allowChunklessPreparation, int metadataType, boolean useSessionKeys) {
        this.extractorFactory = extractorFactory;
        this.playlistTracker = playlistTracker;
        this.dataSourceFactory = dataSourceFactory;
        this.mediaTransferListener = mediaTransferListener;
        this.drmSessionManager = drmSessionManager;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.allowChunklessPreparation = allowChunklessPreparation;
        this.metadataType = metadataType;
        this.useSessionKeys = useSessionKeys;
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(new SequenceableLoader[0]);
        eventDispatcher.mediaPeriodCreated();
    }

    public void release() {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr;
        this.playlistTracker.removeListener(this);
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.release();
        }
        this.callback = null;
        this.eventDispatcher.mediaPeriodReleased();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        this.playlistTracker.addListener(this);
        buildAndPrepareSampleStreamWrappers(positionUs);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void maybeThrowPrepareError() throws IOException {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr;
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.maybeThrowPrepareError();
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public TrackGroupArray getTrackGroups() {
        return (TrackGroupArray) Assertions.checkNotNull(this.trackGroups);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public List<StreamKey> getStreamKeys(List<TrackSelection> trackSelections) {
        TrackGroupArray mainWrapperTrackGroups;
        int[] mainWrapperVariantIndices;
        int mainWrapperPrimaryGroupIndex;
        int audioWrapperOffset;
        boolean hasVariants;
        int groupIndexType;
        HlsMediaPeriod hlsMediaPeriod = this;
        HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) Assertions.checkNotNull(hlsMediaPeriod.playlistTracker.getMasterPlaylist());
        boolean hasVariants2 = !masterPlaylist.variants.isEmpty();
        int audioWrapperOffset2 = hasVariants2 ? 1 : 0;
        int subtitleWrapperOffset = hlsMediaPeriod.sampleStreamWrappers.length - masterPlaylist.subtitles.size();
        if (hasVariants2) {
            HlsSampleStreamWrapper mainWrapper = hlsMediaPeriod.sampleStreamWrappers[0];
            mainWrapperVariantIndices = hlsMediaPeriod.manifestUrlIndicesPerWrapper[0];
            mainWrapperTrackGroups = mainWrapper.getTrackGroups();
            mainWrapperPrimaryGroupIndex = mainWrapper.getPrimaryTrackGroupIndex();
        } else {
            mainWrapperVariantIndices = new int[0];
            mainWrapperTrackGroups = TrackGroupArray.EMPTY;
            mainWrapperPrimaryGroupIndex = 0;
        }
        List<StreamKey> streamKeys = new ArrayList<>();
        boolean needsPrimaryTrackGroupSelection = false;
        boolean i = false;
        for (TrackSelection trackSelection : trackSelections) {
            TrackGroup trackSelectionGroup = trackSelection.getTrackGroup();
            int mainWrapperTrackGroupIndex = mainWrapperTrackGroups.indexOf(trackSelectionGroup);
            if (mainWrapperTrackGroupIndex != -1) {
                if (mainWrapperTrackGroupIndex == mainWrapperPrimaryGroupIndex) {
                    boolean hasPrimaryTrackGroupSelection = true;
                    int i2 = 0;
                    while (true) {
                        hasVariants = hasVariants2;
                        if (i2 >= trackSelection.length()) {
                            break;
                        }
                        int variantIndex = mainWrapperVariantIndices[trackSelection.getIndexInTrackGroup(i2)];
                        streamKeys.add(new StreamKey(0, variantIndex));
                        i2++;
                        hasVariants2 = hasVariants;
                        mainWrapperTrackGroupIndex = mainWrapperTrackGroupIndex;
                        hasPrimaryTrackGroupSelection = hasPrimaryTrackGroupSelection;
                    }
                    audioWrapperOffset = audioWrapperOffset2;
                    i = hasPrimaryTrackGroupSelection;
                } else {
                    hasVariants = hasVariants2;
                    needsPrimaryTrackGroupSelection = true;
                    audioWrapperOffset = audioWrapperOffset2;
                }
            } else {
                hasVariants = hasVariants2;
                int i3 = audioWrapperOffset2;
                while (true) {
                    HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = hlsMediaPeriod.sampleStreamWrappers;
                    if (i3 >= hlsSampleStreamWrapperArr.length) {
                        audioWrapperOffset = audioWrapperOffset2;
                        break;
                    }
                    TrackGroupArray wrapperTrackGroups = hlsSampleStreamWrapperArr[i3].getTrackGroups();
                    int selectedTrackGroupIndex = wrapperTrackGroups.indexOf(trackSelectionGroup);
                    if (selectedTrackGroupIndex == -1) {
                        i3++;
                        hlsMediaPeriod = this;
                    } else {
                        if (i3 < subtitleWrapperOffset) {
                            groupIndexType = 1;
                        } else {
                            groupIndexType = 2;
                        }
                        int[] selectedWrapperUrlIndices = hlsMediaPeriod.manifestUrlIndicesPerWrapper[i3];
                        int trackIndex = 0;
                        while (true) {
                            audioWrapperOffset = audioWrapperOffset2;
                            int audioWrapperOffset3 = trackSelection.length();
                            if (trackIndex < audioWrapperOffset3) {
                                int renditionIndex = selectedWrapperUrlIndices[trackSelection.getIndexInTrackGroup(trackIndex)];
                                streamKeys.add(new StreamKey(groupIndexType, renditionIndex));
                                trackIndex++;
                                audioWrapperOffset2 = audioWrapperOffset;
                                selectedWrapperUrlIndices = selectedWrapperUrlIndices;
                            }
                        }
                    }
                }
            }
            hlsMediaPeriod = this;
            hasVariants2 = hasVariants;
            audioWrapperOffset2 = audioWrapperOffset;
        }
        if (needsPrimaryTrackGroupSelection && !i) {
            int lowestBitrateIndex = mainWrapperVariantIndices[0];
            int lowestBitrate = masterPlaylist.variants.get(mainWrapperVariantIndices[0]).format.bitrate;
            for (int i4 = 1; i4 < mainWrapperVariantIndices.length; i4++) {
                int variantBitrate = masterPlaylist.variants.get(mainWrapperVariantIndices[i4]).format.bitrate;
                if (variantBitrate < lowestBitrate) {
                    lowestBitrate = variantBitrate;
                    lowestBitrateIndex = mainWrapperVariantIndices[i4];
                }
            }
            streamKeys.add(new StreamKey(0, lowestBitrateIndex));
        }
        return streamKeys;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers;
        SampleStream[] sampleStreamArr = streams;
        int[] streamChildIndices = new int[selections.length];
        int[] selectionChildIndices = new int[selections.length];
        for (int i = 0; i < selections.length; i++) {
            streamChildIndices[i] = sampleStreamArr[i] == null ? -1 : this.streamWrapperIndices.get(sampleStreamArr[i]).intValue();
            selectionChildIndices[i] = -1;
            if (selections[i] != null) {
                TrackGroup trackGroup = selections[i].getTrackGroup();
                int j = 0;
                while (true) {
                    HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
                    if (j < hlsSampleStreamWrapperArr.length) {
                        if (hlsSampleStreamWrapperArr[j].getTrackGroups().indexOf(trackGroup) == -1) {
                            j++;
                        } else {
                            selectionChildIndices[i] = j;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        boolean forceReset = false;
        this.streamWrapperIndices.clear();
        SampleStream[] newStreams = new SampleStream[selections.length];
        SampleStream[] childStreams = new SampleStream[selections.length];
        TrackSelection[] childSelections = new TrackSelection[selections.length];
        HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers2 = new HlsSampleStreamWrapper[this.sampleStreamWrappers.length];
        int newEnabledSampleStreamWrapperCount = 0;
        int i2 = 0;
        while (i2 < this.sampleStreamWrappers.length) {
            for (int j2 = 0; j2 < selections.length; j2++) {
                TrackSelection trackSelection = null;
                childStreams[j2] = streamChildIndices[j2] == i2 ? sampleStreamArr[j2] : null;
                if (selectionChildIndices[j2] == i2) {
                    trackSelection = selections[j2];
                }
                childSelections[j2] = trackSelection;
            }
            HlsSampleStreamWrapper sampleStreamWrapper = this.sampleStreamWrappers[i2];
            int i3 = i2;
            HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers3 = newEnabledSampleStreamWrappers2;
            int newEnabledSampleStreamWrapperCount2 = newEnabledSampleStreamWrapperCount;
            TrackSelection[] childSelections2 = childSelections;
            boolean wasReset = sampleStreamWrapper.selectTracks(childSelections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs, forceReset);
            boolean wrapperEnabled = false;
            int j3 = 0;
            while (true) {
                boolean z = true;
                if (j3 >= selections.length) {
                    break;
                }
                SampleStream childStream = childStreams[j3];
                if (selectionChildIndices[j3] == i3) {
                    Assertions.checkNotNull(childStream);
                    newStreams[j3] = childStream;
                    wrapperEnabled = true;
                    this.streamWrapperIndices.put(childStream, Integer.valueOf(i3));
                } else if (streamChildIndices[j3] == i3) {
                    if (childStream != null) {
                        z = false;
                    }
                    Assertions.checkState(z);
                }
                j3++;
            }
            if (wrapperEnabled) {
                newEnabledSampleStreamWrappers = newEnabledSampleStreamWrappers3;
                newEnabledSampleStreamWrappers[newEnabledSampleStreamWrapperCount2] = sampleStreamWrapper;
                newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2 + 1;
                if (newEnabledSampleStreamWrapperCount2 == 0) {
                    sampleStreamWrapper.setIsTimestampMaster(true);
                    if (!wasReset) {
                        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = this.enabledSampleStreamWrappers;
                        if (hlsSampleStreamWrapperArr2.length != 0 && sampleStreamWrapper == hlsSampleStreamWrapperArr2[0]) {
                        }
                    }
                    this.timestampAdjusterProvider.reset();
                    forceReset = true;
                } else {
                    sampleStreamWrapper.setIsTimestampMaster(false);
                }
            } else {
                newEnabledSampleStreamWrappers = newEnabledSampleStreamWrappers3;
                newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2;
            }
            i2 = i3 + 1;
            sampleStreamArr = streams;
            newEnabledSampleStreamWrappers2 = newEnabledSampleStreamWrappers;
            childSelections = childSelections2;
        }
        System.arraycopy(newStreams, 0, streams, 0, newStreams.length);
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr3 = (HlsSampleStreamWrapper[]) Util.nullSafeArrayCopy(newEnabledSampleStreamWrappers2, newEnabledSampleStreamWrapperCount);
        this.enabledSampleStreamWrappers = hlsSampleStreamWrapperArr3;
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(hlsSampleStreamWrapperArr3);
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void discardBuffer(long positionUs, boolean toKeyframe) {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr;
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            sampleStreamWrapper.discardBuffer(positionUs, toKeyframe);
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long positionUs) {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr;
        if (this.trackGroups == null) {
            for (HlsSampleStreamWrapper wrapper : this.sampleStreamWrappers) {
                wrapper.continuePreparing();
            }
            return false;
        }
        return this.compositeSequenceableLoader.continueLoading(positionUs);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.compositeSequenceableLoader.isLoading();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long readDiscontinuity() {
        if (!this.notifiedReadingStarted) {
            this.eventDispatcher.readingStarted();
            this.notifiedReadingStarted = true;
            return C.TIME_UNSET;
        }
        return C.TIME_UNSET;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long seekToUs(long positionUs) {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.enabledSampleStreamWrappers;
        if (hlsSampleStreamWrapperArr.length > 0) {
            boolean forceReset = hlsSampleStreamWrapperArr[0].seekToUs(positionUs, false);
            int i = 1;
            while (true) {
                HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = this.enabledSampleStreamWrappers;
                if (i >= hlsSampleStreamWrapperArr2.length) {
                    break;
                }
                hlsSampleStreamWrapperArr2[i].seekToUs(positionUs, forceReset);
                i++;
            }
            if (forceReset) {
                this.timestampAdjusterProvider.reset();
            }
        }
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.Callback
    public void onPrepared() {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr;
        int i = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i;
        if (i > 0) {
            return;
        }
        int totalTrackGroupCount = 0;
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            totalTrackGroupCount += sampleStreamWrapper.getTrackGroups().length;
        }
        TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
        int trackGroupIndex = 0;
        for (HlsSampleStreamWrapper sampleStreamWrapper2 : this.sampleStreamWrappers) {
            int wrapperTrackGroupCount = sampleStreamWrapper2.getTrackGroups().length;
            int j = 0;
            while (j < wrapperTrackGroupCount) {
                trackGroupArray[trackGroupIndex] = sampleStreamWrapper2.getTrackGroups().get(j);
                j++;
                trackGroupIndex++;
            }
        }
        this.trackGroups = new TrackGroupArray(trackGroupArray);
        this.callback.onPrepared(this);
    }

    @Override // com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.Callback
    public void onPlaylistRefreshRequired(Uri url) {
        this.playlistTracker.refreshPlaylist(url);
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper sampleStreamWrapper) {
        this.callback.onContinueLoadingRequested(this);
    }

    @Override // com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener
    public void onPlaylistChanged() {
        this.callback.onContinueLoadingRequested(this);
    }

    @Override // com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener
    public boolean onPlaylistError(Uri url, long blacklistDurationMs) {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr;
        boolean noBlacklistingFailure = true;
        for (HlsSampleStreamWrapper streamWrapper : this.sampleStreamWrappers) {
            noBlacklistingFailure &= streamWrapper.onPlaylistError(url, blacklistDurationMs);
        }
        this.callback.onContinueLoadingRequested(this);
        return noBlacklistingFailure;
    }

    private void buildAndPrepareSampleStreamWrappers(long positionUs) {
        Map<String, DrmInitData> overridingDrmInitData;
        HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) Assertions.checkNotNull(this.playlistTracker.getMasterPlaylist());
        if (this.useSessionKeys) {
            overridingDrmInitData = deriveOverridingDrmInitData(masterPlaylist.sessionKeyDrmInitData);
        } else {
            overridingDrmInitData = Collections.emptyMap();
        }
        boolean hasVariants = !masterPlaylist.variants.isEmpty();
        List<HlsMasterPlaylist.Rendition> audioRenditions = masterPlaylist.audios;
        List<HlsMasterPlaylist.Rendition> subtitleRenditions = masterPlaylist.subtitles;
        this.pendingPrepareCount = 0;
        ArrayList<HlsSampleStreamWrapper> sampleStreamWrappers = new ArrayList<>();
        ArrayList<int[]> manifestUrlIndicesPerWrapper = new ArrayList<>();
        if (hasVariants) {
            buildAndPrepareMainSampleStreamWrapper(masterPlaylist, positionUs, sampleStreamWrappers, manifestUrlIndicesPerWrapper, overridingDrmInitData);
        }
        buildAndPrepareAudioSampleStreamWrappers(positionUs, audioRenditions, sampleStreamWrappers, manifestUrlIndicesPerWrapper, overridingDrmInitData);
        int i = 0;
        while (i < subtitleRenditions.size()) {
            HlsMasterPlaylist.Rendition subtitleRendition = subtitleRenditions.get(i);
            int i2 = i;
            HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(3, new Uri[]{subtitleRendition.url}, new Format[]{subtitleRendition.format}, null, Collections.emptyList(), overridingDrmInitData, positionUs);
            manifestUrlIndicesPerWrapper.add(new int[]{i2});
            sampleStreamWrappers.add(sampleStreamWrapper);
            sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroup[]{new TrackGroup(subtitleRendition.format)}, 0, new int[0]);
            i = i2 + 1;
            masterPlaylist = masterPlaylist;
        }
        this.sampleStreamWrappers = (HlsSampleStreamWrapper[]) sampleStreamWrappers.toArray(new HlsSampleStreamWrapper[0]);
        this.manifestUrlIndicesPerWrapper = (int[][]) manifestUrlIndicesPerWrapper.toArray(new int[0]);
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
        this.pendingPrepareCount = hlsSampleStreamWrapperArr.length;
        hlsSampleStreamWrapperArr[0].setIsTimestampMaster(true);
        for (HlsSampleStreamWrapper sampleStreamWrapper2 : this.sampleStreamWrappers) {
            sampleStreamWrapper2.continuePreparing();
        }
        this.enabledSampleStreamWrappers = this.sampleStreamWrappers;
    }

    private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist masterPlaylist, long positionUs, List<HlsSampleStreamWrapper> sampleStreamWrappers, List<int[]> manifestUrlIndicesPerWrapper, Map<String, DrmInitData> overridingDrmInitData) {
        int[] variantTypes = new int[masterPlaylist.variants.size()];
        int videoVariantCount = 0;
        int audioVariantCount = 0;
        for (int i = 0; i < masterPlaylist.variants.size(); i++) {
            Format format = masterPlaylist.variants.get(i).format;
            if (format.height > 0 || Util.getCodecsOfType(format.codecs, 2) != null) {
                variantTypes[i] = 2;
                videoVariantCount++;
            } else if (Util.getCodecsOfType(format.codecs, 1) != null) {
                variantTypes[i] = 1;
                audioVariantCount++;
            } else {
                variantTypes[i] = -1;
            }
        }
        boolean useVideoVariantsOnly = false;
        boolean useNonAudioVariantsOnly = false;
        int selectedVariantsCount = variantTypes.length;
        if (videoVariantCount > 0) {
            useVideoVariantsOnly = true;
            selectedVariantsCount = videoVariantCount;
        } else if (audioVariantCount < variantTypes.length) {
            useNonAudioVariantsOnly = true;
            selectedVariantsCount = variantTypes.length - audioVariantCount;
        }
        Uri[] selectedPlaylistUrls = new Uri[selectedVariantsCount];
        Format[] selectedPlaylistFormats = new Format[selectedVariantsCount];
        int[] selectedVariantIndices = new int[selectedVariantsCount];
        int outIndex = 0;
        for (int i2 = 0; i2 < masterPlaylist.variants.size(); i2++) {
            if ((!useVideoVariantsOnly || variantTypes[i2] == 2) && (!useNonAudioVariantsOnly || variantTypes[i2] != 1)) {
                HlsMasterPlaylist.Variant variant = masterPlaylist.variants.get(i2);
                selectedPlaylistUrls[outIndex] = variant.url;
                selectedPlaylistFormats[outIndex] = variant.format;
                selectedVariantIndices[outIndex] = i2;
                outIndex++;
            }
        }
        String codecs = selectedPlaylistFormats[0].codecs;
        HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(0, selectedPlaylistUrls, selectedPlaylistFormats, masterPlaylist.muxedAudioFormat, masterPlaylist.muxedCaptionFormats, overridingDrmInitData, positionUs);
        sampleStreamWrappers.add(sampleStreamWrapper);
        manifestUrlIndicesPerWrapper.add(selectedVariantIndices);
        if (this.allowChunklessPreparation && codecs != null) {
            boolean variantsContainVideoCodecs = Util.getCodecsOfType(codecs, 2) != null;
            boolean variantsContainAudioCodecs = Util.getCodecsOfType(codecs, 1) != null;
            List<TrackGroup> muxedTrackGroups = new ArrayList<>();
            if (variantsContainVideoCodecs) {
                Format[] videoFormats = new Format[selectedVariantsCount];
                int videoVariantCount2 = 0;
                while (true) {
                    int audioVariantCount2 = audioVariantCount;
                    int audioVariantCount3 = videoFormats.length;
                    if (videoVariantCount2 >= audioVariantCount3) {
                        break;
                    }
                    videoFormats[videoVariantCount2] = deriveVideoFormat(selectedPlaylistFormats[videoVariantCount2]);
                    videoVariantCount2++;
                    audioVariantCount = audioVariantCount2;
                }
                muxedTrackGroups.add(new TrackGroup(videoFormats));
                if (variantsContainAudioCodecs && (masterPlaylist.muxedAudioFormat != null || masterPlaylist.audios.isEmpty())) {
                    muxedTrackGroups.add(new TrackGroup(deriveAudioFormat(selectedPlaylistFormats[0], masterPlaylist.muxedAudioFormat, false)));
                }
                List<Format> ccFormats = masterPlaylist.muxedCaptionFormats;
                if (ccFormats != null) {
                    for (int i3 = 0; i3 < ccFormats.size(); i3++) {
                        muxedTrackGroups.add(new TrackGroup(ccFormats.get(i3)));
                    }
                }
            } else if (variantsContainAudioCodecs) {
                Format[] audioFormats = new Format[selectedVariantsCount];
                for (int i4 = 0; i4 < audioFormats.length; i4++) {
                    audioFormats[i4] = deriveAudioFormat(selectedPlaylistFormats[i4], masterPlaylist.muxedAudioFormat, true);
                }
                muxedTrackGroups.add(new TrackGroup(audioFormats));
            } else {
                throw new IllegalArgumentException("Unexpected codecs attribute: " + codecs);
            }
            TrackGroup id3TrackGroup = new TrackGroup(Format.createSampleFormat("ID3", MimeTypes.APPLICATION_ID3, null, -1, null));
            muxedTrackGroups.add(id3TrackGroup);
            sampleStreamWrapper.prepareWithMasterPlaylistInfo((TrackGroup[]) muxedTrackGroups.toArray(new TrackGroup[0]), 0, muxedTrackGroups.indexOf(id3TrackGroup));
        }
    }

    private void buildAndPrepareAudioSampleStreamWrappers(long positionUs, List<HlsMasterPlaylist.Rendition> audioRenditions, List<HlsSampleStreamWrapper> sampleStreamWrappers, List<int[]> manifestUrlsIndicesPerWrapper, Map<String, DrmInitData> overridingDrmInitData) {
        ArrayList<Uri> scratchPlaylistUrls = new ArrayList<>(audioRenditions.size());
        ArrayList<Format> scratchPlaylistFormats = new ArrayList<>(audioRenditions.size());
        ArrayList<Integer> scratchIndicesList = new ArrayList<>(audioRenditions.size());
        HashSet<String> alreadyGroupedNames = new HashSet<>();
        for (int renditionByNameIndex = 0; renditionByNameIndex < audioRenditions.size(); renditionByNameIndex++) {
            String name = audioRenditions.get(renditionByNameIndex).name;
            if (alreadyGroupedNames.add(name)) {
                boolean renditionsHaveCodecs = true;
                scratchPlaylistUrls.clear();
                scratchPlaylistFormats.clear();
                scratchIndicesList.clear();
                int renditionIndex = 0;
                while (true) {
                    boolean z = true;
                    if (renditionIndex >= audioRenditions.size()) {
                        break;
                    }
                    if (Util.areEqual(name, audioRenditions.get(renditionIndex).name)) {
                        HlsMasterPlaylist.Rendition rendition = audioRenditions.get(renditionIndex);
                        scratchIndicesList.add(Integer.valueOf(renditionIndex));
                        scratchPlaylistUrls.add(rendition.url);
                        scratchPlaylistFormats.add(rendition.format);
                        if (rendition.format.codecs == null) {
                            z = false;
                        }
                        renditionsHaveCodecs &= z;
                    }
                    renditionIndex++;
                }
                HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(1, (Uri[]) scratchPlaylistUrls.toArray((Uri[]) Util.castNonNullTypeArray(new Uri[0])), (Format[]) scratchPlaylistFormats.toArray(new Format[0]), null, Collections.emptyList(), overridingDrmInitData, positionUs);
                manifestUrlsIndicesPerWrapper.add(Util.toArray(scratchIndicesList));
                sampleStreamWrappers.add(sampleStreamWrapper);
                if (this.allowChunklessPreparation && renditionsHaveCodecs) {
                    Format[] renditionFormats = (Format[]) scratchPlaylistFormats.toArray(new Format[0]);
                    sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroup[]{new TrackGroup(renditionFormats)}, 0, new int[0]);
                }
            }
        }
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int trackType, Uri[] playlistUrls, Format[] playlistFormats, Format muxedAudioFormat, List<Format> muxedCaptionFormats, Map<String, DrmInitData> overridingDrmInitData, long positionUs) {
        HlsChunkSource defaultChunkSource = new HlsChunkSource(this.extractorFactory, this.playlistTracker, playlistUrls, playlistFormats, this.dataSourceFactory, this.mediaTransferListener, this.timestampAdjusterProvider, muxedCaptionFormats);
        return new HlsSampleStreamWrapper(trackType, this, defaultChunkSource, overridingDrmInitData, this.allocator, positionUs, muxedAudioFormat, this.drmSessionManager, this.loadErrorHandlingPolicy, this.eventDispatcher, this.metadataType);
    }

    private static Map<String, DrmInitData> deriveOverridingDrmInitData(List<DrmInitData> sessionKeyDrmInitData) {
        ArrayList<DrmInitData> mutableSessionKeyDrmInitData = new ArrayList<>(sessionKeyDrmInitData);
        HashMap<String, DrmInitData> drmInitDataBySchemeType = new HashMap<>();
        for (int i = 0; i < mutableSessionKeyDrmInitData.size(); i++) {
            DrmInitData drmInitData = sessionKeyDrmInitData.get(i);
            String scheme = drmInitData.schemeType;
            int j = i + 1;
            while (j < mutableSessionKeyDrmInitData.size()) {
                DrmInitData nextDrmInitData = mutableSessionKeyDrmInitData.get(j);
                if (TextUtils.equals(nextDrmInitData.schemeType, scheme)) {
                    drmInitData = drmInitData.merge(nextDrmInitData);
                    mutableSessionKeyDrmInitData.remove(j);
                } else {
                    j++;
                }
            }
            drmInitDataBySchemeType.put(scheme, drmInitData);
        }
        return drmInitDataBySchemeType;
    }

    private static Format deriveVideoFormat(Format variantFormat) {
        String codecs = Util.getCodecsOfType(variantFormat.codecs, 2);
        String sampleMimeType = MimeTypes.getMediaMimeType(codecs);
        return Format.createVideoContainerFormat(variantFormat.id, variantFormat.label, variantFormat.containerMimeType, sampleMimeType, codecs, variantFormat.metadata, variantFormat.bitrate, variantFormat.width, variantFormat.height, variantFormat.frameRate, null, variantFormat.selectionFlags, variantFormat.roleFlags);
    }

    private static Format deriveAudioFormat(Format variantFormat, Format mediaTagFormat, boolean isPrimaryTrackInVariant) {
        Metadata metadata;
        String codecs;
        int channelCount = -1;
        int selectionFlags = 0;
        int roleFlags = 0;
        String language = null;
        String label = null;
        if (mediaTagFormat != null) {
            codecs = mediaTagFormat.codecs;
            metadata = mediaTagFormat.metadata;
            channelCount = mediaTagFormat.channelCount;
            selectionFlags = mediaTagFormat.selectionFlags;
            roleFlags = mediaTagFormat.roleFlags;
            language = mediaTagFormat.language;
            label = mediaTagFormat.label;
        } else {
            codecs = Util.getCodecsOfType(variantFormat.codecs, 1);
            metadata = variantFormat.metadata;
            if (isPrimaryTrackInVariant) {
                channelCount = variantFormat.channelCount;
                selectionFlags = variantFormat.selectionFlags;
                roleFlags = variantFormat.roleFlags;
                language = variantFormat.language;
                label = variantFormat.label;
            }
        }
        String sampleMimeType = MimeTypes.getMediaMimeType(codecs);
        int bitrate = isPrimaryTrackInVariant ? variantFormat.bitrate : -1;
        return Format.createAudioContainerFormat(variantFormat.id, label, variantFormat.containerMimeType, sampleMimeType, codecs, metadata, bitrate, channelCount, -1, null, selectionFlags, roleFlags, language);
    }
}
