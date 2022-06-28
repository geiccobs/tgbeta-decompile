package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.Ac4Util;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mp4.Atom;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public final class Mp4Extractor implements Extractor, SeekMap {
    private static final int BRAND_QUICKTIME = 1903435808;
    public static final ExtractorsFactory FACTORY = Mp4Extractor$$ExternalSyntheticLambda0.INSTANCE;
    public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 1;
    private static final long MAXIMUM_READ_AHEAD_BYTES_STREAM = 524288;
    private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144;
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private long[][] accumulatedSampleSizes;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final ArrayDeque<Atom.ContainerAtom> containerAtoms;
    private long durationUs;
    private ExtractorOutput extractorOutput;
    private int firstVideoTrackIndex;
    private final int flags;
    private boolean isQuickTime;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesRead;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleTrackIndex;
    private final ParsableByteArray scratch;
    private Mp4Track[] tracks;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface State {
    }

    public static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new Mp4Extractor()};
    }

    public Mp4Extractor() {
        this(0);
    }

    public Mp4Extractor(int flags) {
        this.flags = flags;
        this.atomHeader = new ParsableByteArray(16);
        this.containerAtoms = new ArrayDeque<>();
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.scratch = new ParsableByteArray();
        this.sampleTrackIndex = -1;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffUnfragmented(input);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long position, long timeUs) {
        this.containerAtoms.clear();
        this.atomHeaderBytesRead = 0;
        this.sampleTrackIndex = -1;
        this.sampleBytesRead = 0;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        if (position == 0) {
            enterReadingAtomHeaderState();
        } else if (this.tracks != null) {
            updateSampleIndices(timeUs);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        while (true) {
            switch (this.parserState) {
                case 0:
                    if (readAtomHeader(input)) {
                        break;
                    } else {
                        return -1;
                    }
                case 1:
                    if (!readAtomPayload(input, seekPosition)) {
                        break;
                    } else {
                        return 1;
                    }
                case 2:
                    return readSample(input, seekPosition);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public boolean isSeekable() {
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public long getDurationUs() {
        return this.durationUs;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        long firstOffset;
        long firstTimeUs;
        int secondSampleIndex;
        Mp4Track[] mp4TrackArr = this.tracks;
        if (mp4TrackArr.length == 0) {
            return new SeekMap.SeekPoints(SeekPoint.START);
        }
        long secondTimeUs = C.TIME_UNSET;
        long secondOffset = -1;
        int i = this.firstVideoTrackIndex;
        if (i != -1) {
            TrackSampleTable sampleTable = mp4TrackArr[i].sampleTable;
            int sampleIndex = getSynchronizationSampleIndex(sampleTable, timeUs);
            if (sampleIndex == -1) {
                return new SeekMap.SeekPoints(SeekPoint.START);
            }
            long sampleTimeUs = sampleTable.timestampsUs[sampleIndex];
            firstTimeUs = sampleTimeUs;
            firstOffset = sampleTable.offsets[sampleIndex];
            if (sampleTimeUs < timeUs && sampleIndex < sampleTable.sampleCount - 1 && (secondSampleIndex = sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs)) != -1 && secondSampleIndex != sampleIndex) {
                secondTimeUs = sampleTable.timestampsUs[secondSampleIndex];
                secondOffset = sampleTable.offsets[secondSampleIndex];
            }
        } else {
            firstTimeUs = timeUs;
            firstOffset = Long.MAX_VALUE;
        }
        int i2 = 0;
        long firstOffset2 = firstOffset;
        while (true) {
            Mp4Track[] mp4TrackArr2 = this.tracks;
            if (i2 >= mp4TrackArr2.length) {
                break;
            }
            if (i2 != this.firstVideoTrackIndex) {
                TrackSampleTable sampleTable2 = mp4TrackArr2[i2].sampleTable;
                firstOffset2 = maybeAdjustSeekOffset(sampleTable2, firstTimeUs, firstOffset2);
                if (secondTimeUs != C.TIME_UNSET) {
                    secondOffset = maybeAdjustSeekOffset(sampleTable2, secondTimeUs, secondOffset);
                }
            }
            i2++;
        }
        SeekPoint firstSeekPoint = new SeekPoint(firstTimeUs, firstOffset2);
        if (secondTimeUs == C.TIME_UNSET) {
            return new SeekMap.SeekPoints(firstSeekPoint);
        }
        SeekPoint secondSeekPoint = new SeekPoint(secondTimeUs, secondOffset);
        return new SeekMap.SeekPoints(firstSeekPoint, secondSeekPoint);
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = 0;
        this.atomHeaderBytesRead = 0;
    }

    private boolean readAtomHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (this.atomHeaderBytesRead == 0) {
            if (!input.readFully(this.atomHeader.data, 0, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(0);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        long j = this.atomSize;
        if (j == 1) {
            input.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        } else if (j == 0) {
            long endPosition = input.getLength();
            if (endPosition == -1 && !this.containerAtoms.isEmpty()) {
                endPosition = this.containerAtoms.peek().endPosition;
            }
            if (endPosition != -1) {
                this.atomSize = (endPosition - input.getPosition()) + this.atomHeaderBytesRead;
            }
        }
        if (this.atomSize < this.atomHeaderBytesRead) {
            throw new ParserException("Atom size less than header length (unsupported).");
        }
        if (shouldParseContainerAtom(this.atomType)) {
            long position = input.getPosition();
            long j2 = this.atomSize;
            int i = this.atomHeaderBytesRead;
            long endPosition2 = (position + j2) - i;
            if (j2 != i && this.atomType == 1835365473) {
                maybeSkipRemainingMetaAtomHeaderBytes(input);
            }
            this.containerAtoms.push(new Atom.ContainerAtom(this.atomType, endPosition2));
            if (this.atomSize == this.atomHeaderBytesRead) {
                processAtomEnded(endPosition2);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            Assertions.checkState(this.atomHeaderBytesRead == 8);
            Assertions.checkState(this.atomSize <= 2147483647L);
            this.atomData = new ParsableByteArray((int) this.atomSize);
            System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
            this.parserState = 1;
        } else {
            this.atomData = null;
            this.parserState = 1;
        }
        return true;
    }

    private boolean readAtomPayload(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        long atomPayloadSize = this.atomSize - this.atomHeaderBytesRead;
        long atomEndPosition = input.getPosition() + atomPayloadSize;
        boolean seekRequired = false;
        ParsableByteArray parsableByteArray = this.atomData;
        if (parsableByteArray != null) {
            input.readFully(parsableByteArray.data, this.atomHeaderBytesRead, (int) atomPayloadSize);
            if (this.atomType == 1718909296) {
                this.isQuickTime = processFtypAtom(this.atomData);
            } else if (!this.containerAtoms.isEmpty()) {
                this.containerAtoms.peek().add(new Atom.LeafAtom(this.atomType, this.atomData));
            }
        } else if (atomPayloadSize < RELOAD_MINIMUM_SEEK_DISTANCE) {
            input.skipFully((int) atomPayloadSize);
        } else {
            positionHolder.position = input.getPosition() + atomPayloadSize;
            seekRequired = true;
        }
        processAtomEnded(atomEndPosition);
        return seekRequired && this.parserState != 2;
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && this.containerAtoms.peek().endPosition == atomEndPosition) {
            Atom.ContainerAtom containerAtom = this.containerAtoms.pop();
            if (containerAtom.type == 1836019574) {
                processMoovAtom(containerAtom);
                this.containerAtoms.clear();
                this.parserState = 2;
            } else if (!this.containerAtoms.isEmpty()) {
                this.containerAtoms.peek().add(containerAtom);
            }
        }
        if (this.parserState != 2) {
            enterReadingAtomHeaderState();
        }
    }

    private void processMoovAtom(Atom.ContainerAtom moov) throws ParserException {
        int firstVideoTrackIndex = -1;
        long durationUs = C.TIME_UNSET;
        List<Mp4Track> tracks = new ArrayList<>();
        Metadata udtaMetadata = null;
        GaplessInfoHolder gaplessInfoHolder = new GaplessInfoHolder();
        Atom.LeafAtom udta = moov.getLeafAtomOfType(Atom.TYPE_udta);
        if (udta != null && (udtaMetadata = AtomParsers.parseUdta(udta, this.isQuickTime)) != null) {
            gaplessInfoHolder.setFromMetadata(udtaMetadata);
        }
        Metadata mdtaMetadata = null;
        Atom.ContainerAtom meta = moov.getContainerAtomOfType(Atom.TYPE_meta);
        if (meta != null) {
            mdtaMetadata = AtomParsers.parseMdtaFromMeta(meta);
        }
        boolean ignoreEditLists = (this.flags & 1) != 0;
        ArrayList<TrackSampleTable> trackSampleTables = getTrackSampleTables(moov, gaplessInfoHolder, ignoreEditLists);
        int trackCount = trackSampleTables.size();
        int i = 0;
        while (i < trackCount) {
            TrackSampleTable trackSampleTable = trackSampleTables.get(i);
            Track track = trackSampleTable.track;
            Atom.ContainerAtom meta2 = meta;
            boolean ignoreEditLists2 = ignoreEditLists;
            long trackDurationUs = track.durationUs != C.TIME_UNSET ? track.durationUs : trackSampleTable.durationUs;
            long durationUs2 = Math.max(durationUs, trackDurationUs);
            Atom.LeafAtom udta2 = udta;
            Mp4Track mp4Track = new Mp4Track(track, trackSampleTable, this.extractorOutput.track(i, track.type));
            int maxInputSize = trackSampleTable.maximumSize + 30;
            Format format = track.format.copyWithMaxInputSize(maxInputSize);
            int maxInputSize2 = track.type;
            ArrayList<TrackSampleTable> trackSampleTables2 = trackSampleTables;
            if (maxInputSize2 == 2 && trackDurationUs > 0 && trackSampleTable.sampleCount > 1) {
                float frameRate = trackSampleTable.sampleCount / (((float) trackDurationUs) / 1000000.0f);
                format = format.copyWithFrameRate(frameRate);
            }
            mp4Track.trackOutput.format(MetadataUtil.getFormatWithMetadata(track.type, format, udtaMetadata, mdtaMetadata, gaplessInfoHolder));
            if (track.type == 2 && firstVideoTrackIndex == -1) {
                firstVideoTrackIndex = tracks.size();
            }
            tracks.add(mp4Track);
            i++;
            meta = meta2;
            ignoreEditLists = ignoreEditLists2;
            durationUs = durationUs2;
            udta = udta2;
            trackSampleTables = trackSampleTables2;
        }
        this.firstVideoTrackIndex = firstVideoTrackIndex;
        this.durationUs = durationUs;
        Mp4Track[] mp4TrackArr = (Mp4Track[]) tracks.toArray(new Mp4Track[0]);
        this.tracks = mp4TrackArr;
        this.accumulatedSampleSizes = calculateAccumulatedSampleSizes(mp4TrackArr);
        this.extractorOutput.endTracks();
        this.extractorOutput.seekMap(this);
    }

    private ArrayList<TrackSampleTable> getTrackSampleTables(Atom.ContainerAtom moov, GaplessInfoHolder gaplessInfoHolder, boolean ignoreEditLists) throws ParserException {
        Track track;
        ArrayList<TrackSampleTable> trackSampleTables = new ArrayList<>();
        for (int i = 0; i < moov.containerChildren.size(); i++) {
            Atom.ContainerAtom atom = moov.containerChildren.get(i);
            if (atom.type == 1953653099 && (track = AtomParsers.parseTrak(atom, moov.getLeafAtomOfType(Atom.TYPE_mvhd), C.TIME_UNSET, null, ignoreEditLists, this.isQuickTime)) != null) {
                Atom.ContainerAtom stblAtom = atom.getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
                TrackSampleTable trackSampleTable = AtomParsers.parseStbl(track, stblAtom, gaplessInfoHolder);
                if (trackSampleTable.sampleCount != 0) {
                    trackSampleTables.add(trackSampleTable);
                }
            }
        }
        return trackSampleTables;
    }

    private int readSample(ExtractorInput input, PositionHolder positionHolder) throws IOException, InterruptedException {
        long position;
        long skipAmount;
        int i;
        int sampleSize;
        long inputPosition = input.getPosition();
        if (this.sampleTrackIndex == -1) {
            int trackIndexOfNextReadSample = getTrackIndexOfNextReadSample(inputPosition);
            this.sampleTrackIndex = trackIndexOfNextReadSample;
            if (trackIndexOfNextReadSample == -1) {
                return -1;
            }
        }
        Mp4Track track = this.tracks[this.sampleTrackIndex];
        TrackOutput trackOutput = track.trackOutput;
        int sampleIndex = track.sampleIndex;
        long position2 = track.sampleTable.offsets[sampleIndex];
        int sampleSize2 = track.sampleTable.sizes[sampleIndex];
        long skipAmount2 = (position2 - inputPosition) + this.sampleBytesRead;
        if (skipAmount2 < 0) {
            position = position2;
        } else if (skipAmount2 < RELOAD_MINIMUM_SEEK_DISTANCE) {
            if (track.track.sampleTransformation != 1) {
                skipAmount = skipAmount2;
            } else {
                sampleSize2 -= 8;
                skipAmount = skipAmount2 + 8;
            }
            input.skipFully((int) skipAmount);
            if (track.track.nalUnitLengthFieldLength != 0) {
                byte[] nalLengthData = this.nalLength.data;
                nalLengthData[0] = 0;
                nalLengthData[1] = 0;
                nalLengthData[2] = 0;
                int nalUnitLengthFieldLength = track.track.nalUnitLengthFieldLength;
                int nalUnitLengthFieldLengthDiff = 4 - track.track.nalUnitLengthFieldLength;
                while (this.sampleBytesWritten < sampleSize2) {
                    int i2 = this.sampleCurrentNalBytesRemaining;
                    if (i2 != 0) {
                        int writtenBytes = trackOutput.sampleData(input, i2, false);
                        this.sampleBytesRead += writtenBytes;
                        this.sampleBytesWritten += writtenBytes;
                        this.sampleCurrentNalBytesRemaining -= writtenBytes;
                        inputPosition = inputPosition;
                    } else {
                        input.readFully(nalLengthData, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
                        this.sampleBytesRead += nalUnitLengthFieldLength;
                        long inputPosition2 = inputPosition;
                        this.nalLength.setPosition(0);
                        int nalLengthInt = this.nalLength.readInt();
                        if (nalLengthInt < 0) {
                            throw new ParserException("Invalid NAL length");
                        }
                        this.sampleCurrentNalBytesRemaining = nalLengthInt;
                        this.nalStartCode.setPosition(0);
                        trackOutput.sampleData(this.nalStartCode, 4);
                        this.sampleBytesWritten += 4;
                        sampleSize2 += nalUnitLengthFieldLengthDiff;
                        inputPosition = inputPosition2;
                    }
                }
                sampleSize = sampleSize2;
                i = 0;
            } else {
                if (MimeTypes.AUDIO_AC4.equals(track.track.format.sampleMimeType)) {
                    if (this.sampleBytesWritten == 0) {
                        Ac4Util.getAc4SampleHeader(sampleSize2, this.scratch);
                        trackOutput.sampleData(this.scratch, 7);
                        this.sampleBytesWritten += 7;
                    }
                    sampleSize2 += 7;
                }
                while (true) {
                    int i3 = this.sampleBytesWritten;
                    if (i3 >= sampleSize2) {
                        break;
                    }
                    int writtenBytes2 = trackOutput.sampleData(input, sampleSize2 - i3, false);
                    this.sampleBytesRead += writtenBytes2;
                    this.sampleBytesWritten += writtenBytes2;
                    this.sampleCurrentNalBytesRemaining -= writtenBytes2;
                }
                i = 0;
                sampleSize = sampleSize2;
            }
            trackOutput.sampleMetadata(track.sampleTable.timestampsUs[sampleIndex], track.sampleTable.flags[sampleIndex], sampleSize, 0, null);
            track.sampleIndex++;
            this.sampleTrackIndex = -1;
            this.sampleBytesRead = i;
            this.sampleBytesWritten = i;
            this.sampleCurrentNalBytesRemaining = i;
            return i;
        } else {
            position = position2;
        }
        positionHolder.position = position;
        return 1;
    }

    private int getTrackIndexOfNextReadSample(long inputPosition) {
        long preferredSkipAmount = Long.MAX_VALUE;
        boolean preferredRequiresReload = true;
        int preferredTrackIndex = -1;
        long preferredAccumulatedBytes = Long.MAX_VALUE;
        long minAccumulatedBytes = Long.MAX_VALUE;
        boolean minAccumulatedBytesRequiresReload = true;
        int minAccumulatedBytesTrackIndex = -1;
        int trackIndex = 0;
        while (true) {
            Mp4Track[] mp4TrackArr = this.tracks;
            if (trackIndex >= mp4TrackArr.length) {
                break;
            }
            Mp4Track track = mp4TrackArr[trackIndex];
            int sampleIndex = track.sampleIndex;
            if (sampleIndex != track.sampleTable.sampleCount) {
                long sampleOffset = track.sampleTable.offsets[sampleIndex];
                long sampleAccumulatedBytes = this.accumulatedSampleSizes[trackIndex][sampleIndex];
                long skipAmount = sampleOffset - inputPosition;
                boolean requiresReload = skipAmount < 0 || skipAmount >= RELOAD_MINIMUM_SEEK_DISTANCE;
                if ((!requiresReload && preferredRequiresReload) || (requiresReload == preferredRequiresReload && skipAmount < preferredSkipAmount)) {
                    preferredRequiresReload = requiresReload;
                    preferredSkipAmount = skipAmount;
                    preferredTrackIndex = trackIndex;
                    preferredAccumulatedBytes = sampleAccumulatedBytes;
                }
                if (sampleAccumulatedBytes < minAccumulatedBytes) {
                    minAccumulatedBytes = sampleAccumulatedBytes;
                    minAccumulatedBytesRequiresReload = requiresReload;
                    minAccumulatedBytesTrackIndex = trackIndex;
                }
            }
            trackIndex++;
        }
        if (minAccumulatedBytes == Long.MAX_VALUE || !minAccumulatedBytesRequiresReload || preferredAccumulatedBytes < 524288 + minAccumulatedBytes) {
            return preferredTrackIndex;
        }
        return minAccumulatedBytesTrackIndex;
    }

    private void updateSampleIndices(long timeUs) {
        Mp4Track[] mp4TrackArr;
        for (Mp4Track track : this.tracks) {
            TrackSampleTable sampleTable = track.sampleTable;
            int sampleIndex = sampleTable.getIndexOfEarlierOrEqualSynchronizationSample(timeUs);
            if (sampleIndex == -1) {
                sampleIndex = sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs);
            }
            track.sampleIndex = sampleIndex;
        }
    }

    private void maybeSkipRemainingMetaAtomHeaderBytes(ExtractorInput input) throws IOException, InterruptedException {
        this.scratch.reset(8);
        input.peekFully(this.scratch.data, 0, 8);
        this.scratch.skipBytes(4);
        if (this.scratch.readInt() == 1751411826) {
            input.resetPeekPosition();
        } else {
            input.skipFully(4);
        }
    }

    private static long[][] calculateAccumulatedSampleSizes(Mp4Track[] tracks) {
        long[][] accumulatedSampleSizes = new long[tracks.length];
        int[] nextSampleIndex = new int[tracks.length];
        long[] nextSampleTimesUs = new long[tracks.length];
        boolean[] tracksFinished = new boolean[tracks.length];
        for (int i = 0; i < tracks.length; i++) {
            accumulatedSampleSizes[i] = new long[tracks[i].sampleTable.sampleCount];
            nextSampleTimesUs[i] = tracks[i].sampleTable.timestampsUs[0];
        }
        long accumulatedSampleSize = 0;
        int finishedTracks = 0;
        while (finishedTracks < tracks.length) {
            long minTimeUs = Long.MAX_VALUE;
            int minTimeTrackIndex = -1;
            for (int i2 = 0; i2 < tracks.length; i2++) {
                if (!tracksFinished[i2] && nextSampleTimesUs[i2] <= minTimeUs) {
                    minTimeTrackIndex = i2;
                    minTimeUs = nextSampleTimesUs[i2];
                }
            }
            int i3 = nextSampleIndex[minTimeTrackIndex];
            accumulatedSampleSizes[minTimeTrackIndex][i3] = accumulatedSampleSize;
            accumulatedSampleSize += tracks[minTimeTrackIndex].sampleTable.sizes[i3];
            int trackSampleIndex = i3 + 1;
            nextSampleIndex[minTimeTrackIndex] = trackSampleIndex;
            if (trackSampleIndex < accumulatedSampleSizes[minTimeTrackIndex].length) {
                nextSampleTimesUs[minTimeTrackIndex] = tracks[minTimeTrackIndex].sampleTable.timestampsUs[trackSampleIndex];
            } else {
                tracksFinished[minTimeTrackIndex] = true;
                finishedTracks++;
            }
        }
        return accumulatedSampleSizes;
    }

    private static long maybeAdjustSeekOffset(TrackSampleTable sampleTable, long seekTimeUs, long offset) {
        int sampleIndex = getSynchronizationSampleIndex(sampleTable, seekTimeUs);
        if (sampleIndex == -1) {
            return offset;
        }
        long sampleOffset = sampleTable.offsets[sampleIndex];
        return Math.min(sampleOffset, offset);
    }

    private static int getSynchronizationSampleIndex(TrackSampleTable sampleTable, long timeUs) {
        int sampleIndex = sampleTable.getIndexOfEarlierOrEqualSynchronizationSample(timeUs);
        if (sampleIndex == -1) {
            return sampleTable.getIndexOfLaterOrEqualSynchronizationSample(timeUs);
        }
        return sampleIndex;
    }

    private static boolean processFtypAtom(ParsableByteArray atomData) {
        atomData.setPosition(8);
        int majorBrand = atomData.readInt();
        if (majorBrand == BRAND_QUICKTIME) {
            return true;
        }
        atomData.skipBytes(4);
        while (atomData.bytesLeft() > 0) {
            if (atomData.readInt() == BRAND_QUICKTIME) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldParseLeafAtom(int atom) {
        return atom == 1835296868 || atom == 1836476516 || atom == 1751411826 || atom == 1937011556 || atom == 1937011827 || atom == 1937011571 || atom == 1668576371 || atom == 1701606260 || atom == 1937011555 || atom == 1937011578 || atom == 1937013298 || atom == 1937007471 || atom == 1668232756 || atom == 1953196132 || atom == 1718909296 || atom == 1969517665 || atom == 1801812339 || atom == 1768715124;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        return atom == 1836019574 || atom == 1953653099 || atom == 1835297121 || atom == 1835626086 || atom == 1937007212 || atom == 1701082227 || atom == 1835365473;
    }

    /* loaded from: classes3.dex */
    public static final class Mp4Track {
        public int sampleIndex;
        public final TrackSampleTable sampleTable;
        public final Track track;
        public final TrackOutput trackOutput;

        public Mp4Track(Track track, TrackSampleTable sampleTable, TrackOutput trackOutput) {
            this.track = track;
            this.sampleTable = sampleTable;
            this.trackOutput = trackOutput;
        }
    }
}
