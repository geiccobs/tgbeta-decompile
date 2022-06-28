package com.google.android.exoplayer2.source.dash;

import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.EmptySampleStream;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Descriptor;
import com.google.android.exoplayer2.source.dash.manifest.EventStream;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.microsoft.appcenter.Constants;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class DashMediaPeriod implements MediaPeriod, SequenceableLoader.Callback<ChunkSampleStream<DashChunkSource>>, ChunkSampleStream.ReleaseCallback<DashChunkSource> {
    private static final Pattern CEA608_SERVICE_DESCRIPTOR_REGEX = Pattern.compile("CC([1-4])=(.+)");
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final DashChunkSource.Factory chunkSourceFactory;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final DrmSessionManager<?> drmSessionManager;
    private final long elapsedRealtimeOffsetMs;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private List<EventStream> eventStreams;
    final int id;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private boolean notifiedReadingStarted;
    private int periodIndex;
    private final PlayerEmsgHandler playerEmsgHandler;
    private final TrackGroupInfo[] trackGroupInfos;
    private final TrackGroupArray trackGroups;
    private final TransferListener transferListener;
    private ChunkSampleStream<DashChunkSource>[] sampleStreams = newSampleStreamArray(0);
    private EventSampleStream[] eventSampleStreams = new EventSampleStream[0];
    private final IdentityHashMap<ChunkSampleStream<DashChunkSource>, PlayerEmsgHandler.PlayerTrackEmsgHandler> trackEmsgHandlerBySampleStream = new IdentityHashMap<>();

    public DashMediaPeriod(int id, DashManifest manifest, int periodIndex, DashChunkSource.Factory chunkSourceFactory, TransferListener transferListener, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher, long elapsedRealtimeOffsetMs, LoaderErrorThrower manifestLoaderErrorThrower, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, PlayerEmsgHandler.PlayerEmsgCallback playerEmsgCallback) {
        this.id = id;
        this.manifest = manifest;
        this.periodIndex = periodIndex;
        this.chunkSourceFactory = chunkSourceFactory;
        this.transferListener = transferListener;
        this.drmSessionManager = drmSessionManager;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.playerEmsgHandler = new PlayerEmsgHandler(manifest, playerEmsgCallback, allocator);
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        Period period = manifest.getPeriod(periodIndex);
        this.eventStreams = period.eventStreams;
        Pair<TrackGroupArray, TrackGroupInfo[]> result = buildTrackGroups(drmSessionManager, period.adaptationSets, this.eventStreams);
        this.trackGroups = (TrackGroupArray) result.first;
        this.trackGroupInfos = (TrackGroupInfo[]) result.second;
        eventDispatcher.mediaPeriodCreated();
    }

    public void updateManifest(DashManifest manifest, int periodIndex) {
        EventSampleStream[] eventSampleStreamArr;
        this.manifest = manifest;
        this.periodIndex = periodIndex;
        this.playerEmsgHandler.updateManifest(manifest);
        ChunkSampleStream<DashChunkSource>[] chunkSampleStreamArr = this.sampleStreams;
        if (chunkSampleStreamArr != null) {
            for (ChunkSampleStream<DashChunkSource> sampleStream : chunkSampleStreamArr) {
                sampleStream.getChunkSource().updateManifest(manifest, periodIndex);
            }
            this.callback.onContinueLoadingRequested(this);
        }
        this.eventStreams = manifest.getPeriod(periodIndex).eventStreams;
        for (EventSampleStream eventSampleStream : this.eventSampleStreams) {
            Iterator<EventStream> it = this.eventStreams.iterator();
            while (true) {
                if (it.hasNext()) {
                    EventStream eventStream = it.next();
                    if (eventStream.id().equals(eventSampleStream.eventStreamId())) {
                        boolean z = true;
                        int lastPeriodIndex = manifest.getPeriodCount() - 1;
                        if (!manifest.dynamic || periodIndex != lastPeriodIndex) {
                            z = false;
                        }
                        eventSampleStream.updateEventStream(eventStream, z);
                    }
                }
            }
        }
    }

    public void release() {
        ChunkSampleStream<DashChunkSource>[] chunkSampleStreamArr;
        this.playerEmsgHandler.release();
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.release(this);
        }
        this.callback = null;
        this.eventDispatcher.mediaPeriodReleased();
    }

    @Override // com.google.android.exoplayer2.source.chunk.ChunkSampleStream.ReleaseCallback
    public synchronized void onSampleStreamReleased(ChunkSampleStream<DashChunkSource> stream) {
        PlayerEmsgHandler.PlayerTrackEmsgHandler trackEmsgHandler = this.trackEmsgHandlerBySampleStream.remove(stream);
        if (trackEmsgHandler != null) {
            trackEmsgHandler.release();
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        callback.onPrepared(this);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void maybeThrowPrepareError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public List<StreamKey> getStreamKeys(List<TrackSelection> trackSelections) {
        DashMediaPeriod dashMediaPeriod = this;
        List<AdaptationSet> manifestAdaptationSets = dashMediaPeriod.manifest.getPeriod(dashMediaPeriod.periodIndex).adaptationSets;
        List<StreamKey> streamKeys = new ArrayList<>();
        Iterator<TrackSelection> it = trackSelections.iterator();
        while (it.hasNext()) {
            TrackSelection trackSelection = it.next();
            int trackGroupIndex = dashMediaPeriod.trackGroups.indexOf(trackSelection.getTrackGroup());
            TrackGroupInfo trackGroupInfo = dashMediaPeriod.trackGroupInfos[trackGroupIndex];
            if (trackGroupInfo.trackGroupCategory == 0) {
                int[] adaptationSetIndices = trackGroupInfo.adaptationSetIndices;
                int[] trackIndices = new int[trackSelection.length()];
                for (int i = 0; i < trackSelection.length(); i++) {
                    trackIndices[i] = trackSelection.getIndexInTrackGroup(i);
                }
                Arrays.sort(trackIndices);
                int currentAdaptationSetIndex = 0;
                int totalTracksInPreviousAdaptationSets = 0;
                int i2 = 0;
                int tracksInCurrentAdaptationSet = manifestAdaptationSets.get(adaptationSetIndices[0]).representations.size();
                int length = trackIndices.length;
                while (i2 < length) {
                    int trackIndex = trackIndices[i2];
                    while (trackIndex >= totalTracksInPreviousAdaptationSets + tracksInCurrentAdaptationSet) {
                        currentAdaptationSetIndex++;
                        totalTracksInPreviousAdaptationSets += tracksInCurrentAdaptationSet;
                        tracksInCurrentAdaptationSet = manifestAdaptationSets.get(adaptationSetIndices[currentAdaptationSetIndex]).representations.size();
                    }
                    streamKeys.add(new StreamKey(dashMediaPeriod.periodIndex, adaptationSetIndices[currentAdaptationSetIndex], trackIndex - totalTracksInPreviousAdaptationSets));
                    i2++;
                    dashMediaPeriod = this;
                    manifestAdaptationSets = manifestAdaptationSets;
                    it = it;
                }
                dashMediaPeriod = this;
            }
        }
        return streamKeys;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int[] streamIndexToTrackGroupIndex = getStreamIndexToTrackGroupIndex(selections);
        releaseDisabledStreams(selections, mayRetainStreamFlags, streams);
        releaseOrphanEmbeddedStreams(selections, streams, streamIndexToTrackGroupIndex);
        selectNewStreams(selections, streams, streamResetFlags, positionUs, streamIndexToTrackGroupIndex);
        ArrayList<ChunkSampleStream<DashChunkSource>> sampleStreamList = new ArrayList<>();
        ArrayList<EventSampleStream> eventSampleStreamList = new ArrayList<>();
        for (SampleStream sampleStream : streams) {
            if (sampleStream instanceof ChunkSampleStream) {
                ChunkSampleStream<DashChunkSource> stream = (ChunkSampleStream) sampleStream;
                sampleStreamList.add(stream);
            } else if (sampleStream instanceof EventSampleStream) {
                eventSampleStreamList.add((EventSampleStream) sampleStream);
            }
        }
        ChunkSampleStream<DashChunkSource>[] newSampleStreamArray = newSampleStreamArray(sampleStreamList.size());
        this.sampleStreams = newSampleStreamArray;
        sampleStreamList.toArray(newSampleStreamArray);
        EventSampleStream[] eventSampleStreamArr = new EventSampleStream[eventSampleStreamList.size()];
        this.eventSampleStreams = eventSampleStreamArr;
        eventSampleStreamList.toArray(eventSampleStreamArr);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void discardBuffer(long positionUs, boolean toKeyframe) {
        ChunkSampleStream<DashChunkSource>[] chunkSampleStreamArr;
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.discardBuffer(positionUs, toKeyframe);
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long positionUs) {
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
        ChunkSampleStream<DashChunkSource>[] chunkSampleStreamArr;
        EventSampleStream[] eventSampleStreamArr;
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.seekToUs(positionUs);
        }
        for (EventSampleStream sampleStream2 : this.eventSampleStreams) {
            sampleStream2.seekToUs(positionUs);
        }
        return positionUs;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        ChunkSampleStream<DashChunkSource>[] chunkSampleStreamArr;
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            if (sampleStream.primaryTrackType == 2) {
                return sampleStream.getAdjustedSeekPositionUs(positionUs, seekParameters);
            }
        }
        return positionUs;
    }

    public void onContinueLoadingRequested(ChunkSampleStream<DashChunkSource> sampleStream) {
        this.callback.onContinueLoadingRequested(this);
    }

    private int[] getStreamIndexToTrackGroupIndex(TrackSelection[] selections) {
        int[] streamIndexToTrackGroupIndex = new int[selections.length];
        for (int i = 0; i < selections.length; i++) {
            if (selections[i] != null) {
                streamIndexToTrackGroupIndex[i] = this.trackGroups.indexOf(selections[i].getTrackGroup());
            } else {
                streamIndexToTrackGroupIndex[i] = -1;
            }
        }
        return streamIndexToTrackGroupIndex;
    }

    private void releaseDisabledStreams(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams) {
        for (int i = 0; i < selections.length; i++) {
            if (selections[i] == null || !mayRetainStreamFlags[i]) {
                if (streams[i] instanceof ChunkSampleStream) {
                    ChunkSampleStream<DashChunkSource> stream = (ChunkSampleStream) streams[i];
                    stream.release(this);
                } else if (streams[i] instanceof ChunkSampleStream.EmbeddedSampleStream) {
                    ((ChunkSampleStream.EmbeddedSampleStream) streams[i]).release();
                }
                streams[i] = null;
            }
        }
    }

    private void releaseOrphanEmbeddedStreams(TrackSelection[] selections, SampleStream[] streams, int[] streamIndexToTrackGroupIndex) {
        boolean mayRetainStream;
        for (int i = 0; i < selections.length; i++) {
            if ((streams[i] instanceof EmptySampleStream) || (streams[i] instanceof ChunkSampleStream.EmbeddedSampleStream)) {
                int primaryStreamIndex = getPrimaryStreamIndex(i, streamIndexToTrackGroupIndex);
                if (primaryStreamIndex == -1) {
                    mayRetainStream = streams[i] instanceof EmptySampleStream;
                } else {
                    mayRetainStream = (streams[i] instanceof ChunkSampleStream.EmbeddedSampleStream) && ((ChunkSampleStream.EmbeddedSampleStream) streams[i]).parent == streams[primaryStreamIndex];
                }
                if (!mayRetainStream) {
                    if (streams[i] instanceof ChunkSampleStream.EmbeddedSampleStream) {
                        ((ChunkSampleStream.EmbeddedSampleStream) streams[i]).release();
                    }
                    streams[i] = null;
                }
            }
        }
    }

    private void selectNewStreams(TrackSelection[] selections, SampleStream[] streams, boolean[] streamResetFlags, long positionUs, int[] streamIndexToTrackGroupIndex) {
        for (int i = 0; i < selections.length; i++) {
            TrackSelection selection = selections[i];
            if (selection != null) {
                if (streams[i] == null) {
                    streamResetFlags[i] = true;
                    int trackGroupIndex = streamIndexToTrackGroupIndex[i];
                    TrackGroupInfo trackGroupInfo = this.trackGroupInfos[trackGroupIndex];
                    if (trackGroupInfo.trackGroupCategory == 0) {
                        streams[i] = buildSampleStream(trackGroupInfo, selection, positionUs);
                    } else if (trackGroupInfo.trackGroupCategory == 2) {
                        EventStream eventStream = this.eventStreams.get(trackGroupInfo.eventStreamGroupIndex);
                        Format format = selection.getTrackGroup().getFormat(0);
                        streams[i] = new EventSampleStream(eventStream, format, this.manifest.dynamic);
                    }
                } else if (streams[i] instanceof ChunkSampleStream) {
                    ChunkSampleStream<DashChunkSource> stream = (ChunkSampleStream) streams[i];
                    stream.getChunkSource().updateTrackSelection(selection);
                }
            }
        }
        for (int i2 = 0; i2 < selections.length; i2++) {
            if (streams[i2] == null && selections[i2] != null) {
                int trackGroupIndex2 = streamIndexToTrackGroupIndex[i2];
                TrackGroupInfo trackGroupInfo2 = this.trackGroupInfos[trackGroupIndex2];
                if (trackGroupInfo2.trackGroupCategory == 1) {
                    int primaryStreamIndex = getPrimaryStreamIndex(i2, streamIndexToTrackGroupIndex);
                    if (primaryStreamIndex == -1) {
                        streams[i2] = new EmptySampleStream();
                    } else {
                        streams[i2] = ((ChunkSampleStream) streams[primaryStreamIndex]).selectEmbeddedTrack(positionUs, trackGroupInfo2.trackType);
                    }
                }
            }
        }
    }

    private int getPrimaryStreamIndex(int embeddedStreamIndex, int[] streamIndexToTrackGroupIndex) {
        int embeddedTrackGroupIndex = streamIndexToTrackGroupIndex[embeddedStreamIndex];
        if (embeddedTrackGroupIndex == -1) {
            return -1;
        }
        int primaryTrackGroupIndex = this.trackGroupInfos[embeddedTrackGroupIndex].primaryTrackGroupIndex;
        for (int i = 0; i < streamIndexToTrackGroupIndex.length; i++) {
            int trackGroupIndex = streamIndexToTrackGroupIndex[i];
            if (trackGroupIndex == primaryTrackGroupIndex && this.trackGroupInfos[trackGroupIndex].trackGroupCategory == 0) {
                return i;
            }
        }
        return -1;
    }

    private static Pair<TrackGroupArray, TrackGroupInfo[]> buildTrackGroups(DrmSessionManager<?> drmSessionManager, List<AdaptationSet> adaptationSets, List<EventStream> eventStreams) {
        int[][] groupedAdaptationSetIndices = getGroupedAdaptationSetIndices(adaptationSets);
        int primaryGroupCount = groupedAdaptationSetIndices.length;
        boolean[] primaryGroupHasEventMessageTrackFlags = new boolean[primaryGroupCount];
        Format[][] primaryGroupCea608TrackFormats = new Format[primaryGroupCount];
        int totalEmbeddedTrackGroupCount = identifyEmbeddedTracks(primaryGroupCount, adaptationSets, groupedAdaptationSetIndices, primaryGroupHasEventMessageTrackFlags, primaryGroupCea608TrackFormats);
        int totalGroupCount = primaryGroupCount + totalEmbeddedTrackGroupCount + eventStreams.size();
        TrackGroup[] trackGroups = new TrackGroup[totalGroupCount];
        TrackGroupInfo[] trackGroupInfos = new TrackGroupInfo[totalGroupCount];
        int trackGroupCount = buildPrimaryAndEmbeddedTrackGroupInfos(drmSessionManager, adaptationSets, groupedAdaptationSetIndices, primaryGroupCount, primaryGroupHasEventMessageTrackFlags, primaryGroupCea608TrackFormats, trackGroups, trackGroupInfos);
        buildManifestEventTrackGroupInfos(eventStreams, trackGroups, trackGroupInfos, trackGroupCount);
        return Pair.create(new TrackGroupArray(trackGroups), trackGroupInfos);
    }

    private static int[][] getGroupedAdaptationSetIndices(List<AdaptationSet> adaptationSets) {
        Descriptor adaptationSetSwitchingProperty;
        int adaptationSetCount = adaptationSets.size();
        SparseIntArray adaptationSetIdToIndex = new SparseIntArray(adaptationSetCount);
        List<List<Integer>> adaptationSetGroupedIndices = new ArrayList<>(adaptationSetCount);
        SparseArray<List<Integer>> adaptationSetIndexToGroupedIndices = new SparseArray<>(adaptationSetCount);
        for (int i = 0; i < adaptationSetCount; i++) {
            adaptationSetIdToIndex.put(adaptationSets.get(i).id, i);
            List<Integer> initialGroup = new ArrayList<>();
            initialGroup.add(Integer.valueOf(i));
            adaptationSetGroupedIndices.add(initialGroup);
            adaptationSetIndexToGroupedIndices.put(i, initialGroup);
        }
        for (int i2 = 0; i2 < adaptationSetCount; i2++) {
            int mergedGroupIndex = i2;
            AdaptationSet adaptationSet = adaptationSets.get(i2);
            Descriptor trickPlayProperty = findTrickPlayProperty(adaptationSet.essentialProperties);
            if (trickPlayProperty == null) {
                trickPlayProperty = findTrickPlayProperty(adaptationSet.supplementalProperties);
            }
            if (trickPlayProperty != null) {
                int mainAdaptationSetId = Integer.parseInt(trickPlayProperty.value);
                int mainAdaptationSetIndex = adaptationSetIdToIndex.get(mainAdaptationSetId, -1);
                if (mainAdaptationSetIndex != -1) {
                    mergedGroupIndex = mainAdaptationSetIndex;
                }
            }
            if (mergedGroupIndex == i2 && (adaptationSetSwitchingProperty = findAdaptationSetSwitchingProperty(adaptationSet.supplementalProperties)) != null) {
                String[] otherAdaptationSetIds = Util.split(adaptationSetSwitchingProperty.value, ",");
                for (String adaptationSetId : otherAdaptationSetIds) {
                    int otherAdaptationSetId = adaptationSetIdToIndex.get(Integer.parseInt(adaptationSetId), -1);
                    if (otherAdaptationSetId != -1) {
                        mergedGroupIndex = Math.min(mergedGroupIndex, otherAdaptationSetId);
                    }
                }
            }
            if (mergedGroupIndex != i2) {
                List<Integer> thisGroup = adaptationSetIndexToGroupedIndices.get(i2);
                List<Integer> mergedGroup = adaptationSetIndexToGroupedIndices.get(mergedGroupIndex);
                mergedGroup.addAll(thisGroup);
                adaptationSetIndexToGroupedIndices.put(i2, mergedGroup);
                adaptationSetGroupedIndices.remove(thisGroup);
            }
        }
        int i3 = adaptationSetGroupedIndices.size();
        int[][] groupedAdaptationSetIndices = new int[i3];
        for (int i4 = 0; i4 < groupedAdaptationSetIndices.length; i4++) {
            groupedAdaptationSetIndices[i4] = Util.toArray(adaptationSetGroupedIndices.get(i4));
            Arrays.sort(groupedAdaptationSetIndices[i4]);
        }
        return groupedAdaptationSetIndices;
    }

    private static int identifyEmbeddedTracks(int primaryGroupCount, List<AdaptationSet> adaptationSets, int[][] groupedAdaptationSetIndices, boolean[] primaryGroupHasEventMessageTrackFlags, Format[][] primaryGroupCea608TrackFormats) {
        int numEmbeddedTrackGroups = 0;
        for (int i = 0; i < primaryGroupCount; i++) {
            if (hasEventMessageTrack(adaptationSets, groupedAdaptationSetIndices[i])) {
                primaryGroupHasEventMessageTrackFlags[i] = true;
                numEmbeddedTrackGroups++;
            }
            primaryGroupCea608TrackFormats[i] = getCea608TrackFormats(adaptationSets, groupedAdaptationSetIndices[i]);
            if (primaryGroupCea608TrackFormats[i].length != 0) {
                numEmbeddedTrackGroups++;
            }
        }
        return numEmbeddedTrackGroups;
    }

    private static int buildPrimaryAndEmbeddedTrackGroupInfos(DrmSessionManager<?> drmSessionManager, List<AdaptationSet> adaptationSets, int[][] groupedAdaptationSetIndices, int primaryGroupCount, boolean[] primaryGroupHasEventMessageTrackFlags, Format[][] primaryGroupCea608TrackFormats, TrackGroup[] trackGroups, TrackGroupInfo[] trackGroupInfos) {
        int trackGroupCount;
        int cea608TrackGroupIndex;
        int trackGroupCount2 = 0;
        int i = 0;
        while (i < primaryGroupCount) {
            int[] adaptationSetIndices = groupedAdaptationSetIndices[i];
            List<Representation> representations = new ArrayList<>();
            for (int adaptationSetIndex : adaptationSetIndices) {
                representations.addAll(adaptationSets.get(adaptationSetIndex).representations);
            }
            Format[] formats = new Format[representations.size()];
            for (int j = 0; j < formats.length; j++) {
                Format format = representations.get(j).format;
                DrmInitData drmInitData = format.drmInitData;
                if (drmInitData != null) {
                    format = format.copyWithExoMediaCryptoType(drmSessionManager.getExoMediaCryptoType(drmInitData));
                }
                formats[j] = format;
            }
            int j2 = adaptationSetIndices[0];
            AdaptationSet firstAdaptationSet = adaptationSets.get(j2);
            int trackGroupCount3 = trackGroupCount2 + 1;
            if (primaryGroupHasEventMessageTrackFlags[i]) {
                trackGroupCount = trackGroupCount3 + 1;
            } else {
                trackGroupCount = trackGroupCount3;
                trackGroupCount3 = -1;
            }
            if (primaryGroupCea608TrackFormats[i].length != 0) {
                cea608TrackGroupIndex = trackGroupCount;
                trackGroupCount++;
            } else {
                cea608TrackGroupIndex = -1;
            }
            trackGroups[trackGroupCount2] = new TrackGroup(formats);
            trackGroupInfos[trackGroupCount2] = TrackGroupInfo.primaryTrack(firstAdaptationSet.type, adaptationSetIndices, trackGroupCount2, trackGroupCount3, cea608TrackGroupIndex);
            if (trackGroupCount3 != -1) {
                trackGroups[trackGroupCount3] = new TrackGroup(Format.createSampleFormat(firstAdaptationSet.id + ":emsg", MimeTypes.APPLICATION_EMSG, null, -1, null));
                trackGroupInfos[trackGroupCount3] = TrackGroupInfo.embeddedEmsgTrack(adaptationSetIndices, trackGroupCount2);
            }
            if (cea608TrackGroupIndex != -1) {
                trackGroups[cea608TrackGroupIndex] = new TrackGroup(primaryGroupCea608TrackFormats[i]);
                trackGroupInfos[cea608TrackGroupIndex] = TrackGroupInfo.embeddedCea608Track(adaptationSetIndices, trackGroupCount2);
            }
            i++;
            trackGroupCount2 = trackGroupCount;
        }
        return trackGroupCount2;
    }

    private static void buildManifestEventTrackGroupInfos(List<EventStream> eventStreams, TrackGroup[] trackGroups, TrackGroupInfo[] trackGroupInfos, int existingTrackGroupCount) {
        int i = 0;
        while (i < eventStreams.size()) {
            EventStream eventStream = eventStreams.get(i);
            Format format = Format.createSampleFormat(eventStream.id(), MimeTypes.APPLICATION_EMSG, null, -1, null);
            trackGroups[existingTrackGroupCount] = new TrackGroup(format);
            trackGroupInfos[existingTrackGroupCount] = TrackGroupInfo.mpdEventTrack(i);
            i++;
            existingTrackGroupCount++;
        }
    }

    private ChunkSampleStream<DashChunkSource> buildSampleStream(TrackGroupInfo trackGroupInfo, TrackSelection selection, long positionUs) {
        TrackGroup embeddedEventMessageTrackGroup;
        TrackGroup embeddedCea608TrackGroup;
        PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler;
        int embeddedTrackCount = 0;
        boolean z = true;
        boolean enableEventMessageTrack = trackGroupInfo.embeddedEventMessageTrackGroupIndex != -1;
        if (!enableEventMessageTrack) {
            embeddedEventMessageTrackGroup = null;
        } else {
            TrackGroup embeddedEventMessageTrackGroup2 = this.trackGroups.get(trackGroupInfo.embeddedEventMessageTrackGroupIndex);
            embeddedTrackCount = 0 + 1;
            embeddedEventMessageTrackGroup = embeddedEventMessageTrackGroup2;
        }
        if (trackGroupInfo.embeddedCea608TrackGroupIndex == -1) {
            z = false;
        }
        boolean enableCea608Tracks = z;
        if (!enableCea608Tracks) {
            embeddedCea608TrackGroup = null;
        } else {
            TrackGroup embeddedCea608TrackGroup2 = this.trackGroups.get(trackGroupInfo.embeddedCea608TrackGroupIndex);
            embeddedTrackCount += embeddedCea608TrackGroup2.length;
            embeddedCea608TrackGroup = embeddedCea608TrackGroup2;
        }
        Format[] embeddedTrackFormats = new Format[embeddedTrackCount];
        int[] embeddedTrackTypes = new int[embeddedTrackCount];
        int embeddedTrackCount2 = 0;
        if (enableEventMessageTrack) {
            embeddedTrackFormats[0] = embeddedEventMessageTrackGroup.getFormat(0);
            embeddedTrackTypes[0] = 4;
            embeddedTrackCount2 = 0 + 1;
        }
        List<Format> embeddedCea608TrackFormats = new ArrayList<>();
        if (enableCea608Tracks) {
            for (int i = 0; i < embeddedCea608TrackGroup.length; i++) {
                embeddedTrackFormats[embeddedTrackCount2] = embeddedCea608TrackGroup.getFormat(i);
                embeddedTrackTypes[embeddedTrackCount2] = 3;
                embeddedCea608TrackFormats.add(embeddedTrackFormats[embeddedTrackCount2]);
                embeddedTrackCount2++;
            }
        }
        if (this.manifest.dynamic && enableEventMessageTrack) {
            playerTrackEmsgHandler = this.playerEmsgHandler.newPlayerTrackEmsgHandler();
        } else {
            playerTrackEmsgHandler = null;
        }
        PlayerEmsgHandler.PlayerTrackEmsgHandler trackPlayerEmsgHandler = playerTrackEmsgHandler;
        DashChunkSource chunkSource = this.chunkSourceFactory.createDashChunkSource(this.manifestLoaderErrorThrower, this.manifest, this.periodIndex, trackGroupInfo.adaptationSetIndices, selection, trackGroupInfo.trackType, this.elapsedRealtimeOffsetMs, enableEventMessageTrack, embeddedCea608TrackFormats, trackPlayerEmsgHandler, this.transferListener);
        ChunkSampleStream<DashChunkSource> stream = new ChunkSampleStream<>(trackGroupInfo.trackType, embeddedTrackTypes, embeddedTrackFormats, chunkSource, this, this.allocator, positionUs, this.drmSessionManager, this.loadErrorHandlingPolicy, this.eventDispatcher);
        synchronized (this) {
            this.trackEmsgHandlerBySampleStream.put(stream, trackPlayerEmsgHandler);
        }
        return stream;
    }

    private static Descriptor findAdaptationSetSwitchingProperty(List<Descriptor> descriptors) {
        return findDescriptor(descriptors, "urn:mpeg:dash:adaptation-set-switching:2016");
    }

    private static Descriptor findTrickPlayProperty(List<Descriptor> descriptors) {
        return findDescriptor(descriptors, "http://dashif.org/guidelines/trickmode");
    }

    private static Descriptor findDescriptor(List<Descriptor> descriptors, String schemeIdUri) {
        for (int i = 0; i < descriptors.size(); i++) {
            Descriptor descriptor = descriptors.get(i);
            if (schemeIdUri.equals(descriptor.schemeIdUri)) {
                return descriptor;
            }
        }
        return null;
    }

    private static boolean hasEventMessageTrack(List<AdaptationSet> adaptationSets, int[] adaptationSetIndices) {
        for (int i : adaptationSetIndices) {
            List<Representation> representations = adaptationSets.get(i).representations;
            for (int j = 0; j < representations.size(); j++) {
                Representation representation = representations.get(j);
                if (!representation.inbandEventStreams.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Format[] getCea608TrackFormats(List<AdaptationSet> adaptationSets, int[] adaptationSetIndices) {
        for (int i : adaptationSetIndices) {
            AdaptationSet adaptationSet = adaptationSets.get(i);
            List<Descriptor> descriptors = adaptationSets.get(i).accessibilityDescriptors;
            for (int j = 0; j < descriptors.size(); j++) {
                Descriptor descriptor = descriptors.get(j);
                if ("urn:scte:dash:cc:cea-608:2015".equals(descriptor.schemeIdUri)) {
                    String value = descriptor.value;
                    int i2 = 1;
                    if (value == null) {
                        return new Format[]{buildCea608TrackFormat(adaptationSet.id)};
                    }
                    String[] services = Util.split(value, ";");
                    Format[] formats = new Format[services.length];
                    int k = 0;
                    while (k < services.length) {
                        Matcher matcher = CEA608_SERVICE_DESCRIPTOR_REGEX.matcher(services[k]);
                        if (!matcher.matches()) {
                            Format[] formatArr = new Format[i2];
                            formatArr[0] = buildCea608TrackFormat(adaptationSet.id);
                            return formatArr;
                        }
                        formats[k] = buildCea608TrackFormat(adaptationSet.id, matcher.group(2), Integer.parseInt(matcher.group(i2)));
                        k++;
                        i2 = 1;
                    }
                    return formats;
                }
            }
        }
        return new Format[0];
    }

    private static Format buildCea608TrackFormat(int adaptationSetId) {
        return buildCea608TrackFormat(adaptationSetId, null, -1);
    }

    private static Format buildCea608TrackFormat(int adaptationSetId, String language, int accessibilityChannel) {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append(adaptationSetId);
        sb.append(":cea608");
        if (accessibilityChannel != -1) {
            str = Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + accessibilityChannel;
        } else {
            str = "";
        }
        sb.append(str);
        return Format.createTextSampleFormat(sb.toString(), MimeTypes.APPLICATION_CEA608, null, -1, 0, language, accessibilityChannel, null, Long.MAX_VALUE, null);
    }

    private static ChunkSampleStream<DashChunkSource>[] newSampleStreamArray(int length) {
        return new ChunkSampleStream[length];
    }

    /* loaded from: classes3.dex */
    public static final class TrackGroupInfo {
        private static final int CATEGORY_EMBEDDED = 1;
        private static final int CATEGORY_MANIFEST_EVENTS = 2;
        private static final int CATEGORY_PRIMARY = 0;
        public final int[] adaptationSetIndices;
        public final int embeddedCea608TrackGroupIndex;
        public final int embeddedEventMessageTrackGroupIndex;
        public final int eventStreamGroupIndex;
        public final int primaryTrackGroupIndex;
        public final int trackGroupCategory;
        public final int trackType;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface TrackGroupCategory {
        }

        public static TrackGroupInfo primaryTrack(int trackType, int[] adaptationSetIndices, int primaryTrackGroupIndex, int embeddedEventMessageTrackGroupIndex, int embeddedCea608TrackGroupIndex) {
            return new TrackGroupInfo(trackType, 0, adaptationSetIndices, primaryTrackGroupIndex, embeddedEventMessageTrackGroupIndex, embeddedCea608TrackGroupIndex, -1);
        }

        public static TrackGroupInfo embeddedEmsgTrack(int[] adaptationSetIndices, int primaryTrackGroupIndex) {
            return new TrackGroupInfo(4, 1, adaptationSetIndices, primaryTrackGroupIndex, -1, -1, -1);
        }

        public static TrackGroupInfo embeddedCea608Track(int[] adaptationSetIndices, int primaryTrackGroupIndex) {
            return new TrackGroupInfo(3, 1, adaptationSetIndices, primaryTrackGroupIndex, -1, -1, -1);
        }

        public static TrackGroupInfo mpdEventTrack(int eventStreamIndex) {
            return new TrackGroupInfo(4, 2, new int[0], -1, -1, -1, eventStreamIndex);
        }

        private TrackGroupInfo(int trackType, int trackGroupCategory, int[] adaptationSetIndices, int primaryTrackGroupIndex, int embeddedEventMessageTrackGroupIndex, int embeddedCea608TrackGroupIndex, int eventStreamGroupIndex) {
            this.trackType = trackType;
            this.adaptationSetIndices = adaptationSetIndices;
            this.trackGroupCategory = trackGroupCategory;
            this.primaryTrackGroupIndex = primaryTrackGroupIndex;
            this.embeddedEventMessageTrackGroupIndex = embeddedEventMessageTrackGroupIndex;
            this.embeddedCea608TrackGroupIndex = embeddedCea608TrackGroupIndex;
            this.eventStreamGroupIndex = eventStreamGroupIndex;
        }
    }
}
