package com.google.android.exoplayer2.extractor.mp4;

import android.util.Pair;
import android.util.SparseArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.Ac4Util;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mp4.Atom;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.emsg.EventMessageEncoder;
import com.google.android.exoplayer2.text.cea.CeaUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
/* loaded from: classes3.dex */
public class FragmentedMp4Extractor implements Extractor {
    public static final int FLAG_ENABLE_EMSG_TRACK = 4;
    private static final int FLAG_SIDELOADED = 8;
    public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
    public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 16;
    public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
    private static final int SAMPLE_GROUP_TYPE_seig = 1936025959;
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_ENCRYPTION_DATA = 2;
    private static final int STATE_READING_SAMPLE_CONTINUE = 4;
    private static final int STATE_READING_SAMPLE_START = 3;
    private static final String TAG = "FragmentedMp4Extractor";
    private final TrackOutput additionalEmsgTrackOutput;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private TrackOutput[] cea608TrackOutputs;
    private final List<Format> closedCaptionFormats;
    private final ArrayDeque<Atom.ContainerAtom> containerAtoms;
    private TrackBundle currentTrackBundle;
    private long durationUs;
    private TrackOutput[] emsgTrackOutputs;
    private long endOfMdatPosition;
    private final EventMessageEncoder eventMessageEncoder;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private boolean haveOutputSeekMap;
    private final ParsableByteArray nalBuffer;
    private final ParsableByteArray nalPrefix;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int pendingMetadataSampleBytes;
    private final ArrayDeque<MetadataSampleInfo> pendingMetadataSampleInfos;
    private long pendingSeekTimeUs;
    private boolean processSeiNalUnitPayload;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private final ParsableByteArray scratch;
    private final byte[] scratchBytes;
    private long segmentIndexEarliestPresentationTimeUs;
    private final Track sideloadedTrack;
    private final TimestampAdjuster timestampAdjuster;
    private final SparseArray<TrackBundle> trackBundles;
    public static final ExtractorsFactory FACTORY = FragmentedMp4Extractor$$ExternalSyntheticLambda0.INSTANCE;
    private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = {-94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12};
    private static final Format EMSG_FORMAT = Format.createSampleFormat(null, MimeTypes.APPLICATION_EMSG, Long.MAX_VALUE);

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    public static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new FragmentedMp4Extractor()};
    }

    public FragmentedMp4Extractor() {
        this(0);
    }

    public FragmentedMp4Extractor(int flags) {
        this(flags, null);
    }

    public FragmentedMp4Extractor(int flags, TimestampAdjuster timestampAdjuster) {
        this(flags, timestampAdjuster, null, Collections.emptyList());
    }

    public FragmentedMp4Extractor(int flags, TimestampAdjuster timestampAdjuster, Track sideloadedTrack) {
        this(flags, timestampAdjuster, sideloadedTrack, Collections.emptyList());
    }

    public FragmentedMp4Extractor(int flags, TimestampAdjuster timestampAdjuster, Track sideloadedTrack, List<Format> closedCaptionFormats) {
        this(flags, timestampAdjuster, sideloadedTrack, closedCaptionFormats, null);
    }

    public FragmentedMp4Extractor(int flags, TimestampAdjuster timestampAdjuster, Track sideloadedTrack, List<Format> closedCaptionFormats, TrackOutput additionalEmsgTrackOutput) {
        this.flags = (sideloadedTrack != null ? 8 : 0) | flags;
        this.timestampAdjuster = timestampAdjuster;
        this.sideloadedTrack = sideloadedTrack;
        this.closedCaptionFormats = Collections.unmodifiableList(closedCaptionFormats);
        this.additionalEmsgTrackOutput = additionalEmsgTrackOutput;
        this.eventMessageEncoder = new EventMessageEncoder();
        this.atomHeader = new ParsableByteArray(16);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalPrefix = new ParsableByteArray(5);
        this.nalBuffer = new ParsableByteArray();
        byte[] bArr = new byte[16];
        this.scratchBytes = bArr;
        this.scratch = new ParsableByteArray(bArr);
        this.containerAtoms = new ArrayDeque<>();
        this.pendingMetadataSampleInfos = new ArrayDeque<>();
        this.trackBundles = new SparseArray<>();
        this.durationUs = C.TIME_UNSET;
        this.pendingSeekTimeUs = C.TIME_UNSET;
        this.segmentIndexEarliestPresentationTimeUs = C.TIME_UNSET;
        enterReadingAtomHeaderState();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffFragmented(input);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        if (this.sideloadedTrack != null) {
            TrackBundle bundle = new TrackBundle(output.track(0, this.sideloadedTrack.type));
            bundle.init(this.sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
            this.trackBundles.put(0, bundle);
            maybeInitExtraTracks();
            this.extractorOutput.endTracks();
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long position, long timeUs) {
        int trackCount = this.trackBundles.size();
        for (int i = 0; i < trackCount; i++) {
            this.trackBundles.valueAt(i).reset();
        }
        this.pendingMetadataSampleInfos.clear();
        this.pendingMetadataSampleBytes = 0;
        this.pendingSeekTimeUs = timeUs;
        this.containerAtoms.clear();
        enterReadingAtomHeaderState();
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
                    readAtomPayload(input);
                    break;
                case 2:
                    readEncryptionData(input);
                    break;
                default:
                    if (!readSample(input)) {
                        break;
                    } else {
                        return 0;
                    }
            }
        }
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
        long atomPosition = input.getPosition() - this.atomHeaderBytesRead;
        if (this.atomType == 1836019558) {
            int trackCount = this.trackBundles.size();
            for (int i = 0; i < trackCount; i++) {
                TrackFragment fragment = this.trackBundles.valueAt(i).fragment;
                fragment.atomPosition = atomPosition;
                fragment.auxiliaryDataPosition = atomPosition;
                fragment.dataPosition = atomPosition;
            }
        }
        int trackCount2 = this.atomType;
        if (trackCount2 == 1835295092) {
            this.currentTrackBundle = null;
            this.endOfMdatPosition = this.atomSize + atomPosition;
            if (!this.haveOutputSeekMap) {
                this.extractorOutput.seekMap(new SeekMap.Unseekable(this.durationUs, atomPosition));
                this.haveOutputSeekMap = true;
            }
            this.parserState = 2;
            return true;
        }
        if (shouldParseContainerAtom(trackCount2)) {
            long endPosition2 = (input.getPosition() + this.atomSize) - 8;
            this.containerAtoms.push(new Atom.ContainerAtom(this.atomType, endPosition2));
            if (this.atomSize == this.atomHeaderBytesRead) {
                processAtomEnded(endPosition2);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            if (this.atomHeaderBytesRead != 8) {
                throw new ParserException("Leaf atom defines extended atom size (unsupported).");
            }
            if (this.atomSize > 2147483647L) {
                throw new ParserException("Leaf atom with length > 2147483647 (unsupported).");
            }
            this.atomData = new ParsableByteArray((int) this.atomSize);
            System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
            this.parserState = 1;
        } else if (this.atomSize > 2147483647L) {
            throw new ParserException("Skipping atom with length > 2147483647 (unsupported).");
        } else {
            this.atomData = null;
            this.parserState = 1;
        }
        return true;
    }

    private void readAtomPayload(ExtractorInput input) throws IOException, InterruptedException {
        int atomPayloadSize = ((int) this.atomSize) - this.atomHeaderBytesRead;
        ParsableByteArray parsableByteArray = this.atomData;
        if (parsableByteArray != null) {
            input.readFully(parsableByteArray.data, 8, atomPayloadSize);
            onLeafAtomRead(new Atom.LeafAtom(this.atomType, this.atomData), input.getPosition());
        } else {
            input.skipFully(atomPayloadSize);
        }
        processAtomEnded(input.getPosition());
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && this.containerAtoms.peek().endPosition == atomEndPosition) {
            onContainerAtomRead(this.containerAtoms.pop());
        }
        enterReadingAtomHeaderState();
    }

    private void onLeafAtomRead(Atom.LeafAtom leaf, long inputPosition) throws ParserException {
        if (!this.containerAtoms.isEmpty()) {
            this.containerAtoms.peek().add(leaf);
        } else if (leaf.type == 1936286840) {
            Pair<Long, ChunkIndex> result = parseSidx(leaf.data, inputPosition);
            this.segmentIndexEarliestPresentationTimeUs = ((Long) result.first).longValue();
            this.extractorOutput.seekMap((SeekMap) result.second);
            this.haveOutputSeekMap = true;
        } else if (leaf.type == 1701671783) {
            onEmsgLeafAtomRead(leaf.data);
        }
    }

    private void onContainerAtomRead(Atom.ContainerAtom container) throws ParserException {
        if (container.type == 1836019574) {
            onMoovContainerAtomRead(container);
        } else if (container.type == 1836019558) {
            onMoofContainerAtomRead(container);
        } else if (!this.containerAtoms.isEmpty()) {
            this.containerAtoms.peek().add(container);
        }
    }

    private void onMoovContainerAtomRead(Atom.ContainerAtom moov) throws ParserException {
        int moovContainerChildrenSize;
        int i;
        SparseArray<Track> tracks;
        Assertions.checkState(this.sideloadedTrack == null, "Unexpected moov box.");
        DrmInitData drmInitData = getDrmInitDataFromAtoms(moov.leafChildren);
        Atom.ContainerAtom mvex = moov.getContainerAtomOfType(Atom.TYPE_mvex);
        SparseArray<DefaultSampleValues> defaultSampleValuesArray = new SparseArray<>();
        int mvexChildrenSize = mvex.leafChildren.size();
        long duration = -9223372036854775807L;
        for (int i2 = 0; i2 < mvexChildrenSize; i2++) {
            Atom.LeafAtom atom = mvex.leafChildren.get(i2);
            if (atom.type == 1953654136) {
                Pair<Integer, DefaultSampleValues> trexData = parseTrex(atom.data);
                defaultSampleValuesArray.put(((Integer) trexData.first).intValue(), (DefaultSampleValues) trexData.second);
            } else if (atom.type == 1835362404) {
                duration = parseMehd(atom.data);
            }
        }
        SparseArray<Track> tracks2 = new SparseArray<>();
        int moovContainerChildrenSize2 = moov.containerChildren.size();
        int i3 = 0;
        while (i3 < moovContainerChildrenSize2) {
            Atom.ContainerAtom atom2 = moov.containerChildren.get(i3);
            if (atom2.type == 1953653099) {
                i = i3;
                moovContainerChildrenSize = moovContainerChildrenSize2;
                tracks = tracks2;
                Track track = modifyTrack(AtomParsers.parseTrak(atom2, moov.getLeafAtomOfType(Atom.TYPE_mvhd), duration, drmInitData, (this.flags & 16) != 0, false));
                if (track != null) {
                    tracks.put(track.id, track);
                }
            } else {
                i = i3;
                moovContainerChildrenSize = moovContainerChildrenSize2;
                tracks = tracks2;
            }
            i3 = i + 1;
            tracks2 = tracks;
            moovContainerChildrenSize2 = moovContainerChildrenSize;
        }
        SparseArray<Track> tracks3 = tracks2;
        int trackCount = tracks3.size();
        if (this.trackBundles.size() == 0) {
            for (int i4 = 0; i4 < trackCount; i4++) {
                Track track2 = tracks3.valueAt(i4);
                TrackBundle trackBundle = new TrackBundle(this.extractorOutput.track(i4, track2.type));
                trackBundle.init(track2, getDefaultSampleValues(defaultSampleValuesArray, track2.id));
                this.trackBundles.put(track2.id, trackBundle);
                this.durationUs = Math.max(this.durationUs, track2.durationUs);
            }
            maybeInitExtraTracks();
            this.extractorOutput.endTracks();
            return;
        }
        Assertions.checkState(this.trackBundles.size() == trackCount);
        for (int i5 = 0; i5 < trackCount; i5++) {
            Track track3 = tracks3.valueAt(i5);
            this.trackBundles.get(track3.id).init(track3, getDefaultSampleValues(defaultSampleValuesArray, track3.id));
        }
    }

    protected Track modifyTrack(Track track) {
        return track;
    }

    private DefaultSampleValues getDefaultSampleValues(SparseArray<DefaultSampleValues> defaultSampleValuesArray, int trackId) {
        if (defaultSampleValuesArray.size() == 1) {
            return defaultSampleValuesArray.valueAt(0);
        }
        return (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValuesArray.get(trackId));
    }

    private void onMoofContainerAtomRead(Atom.ContainerAtom moof) throws ParserException {
        parseMoof(moof, this.trackBundles, this.flags, this.scratchBytes);
        DrmInitData drmInitData = getDrmInitDataFromAtoms(moof.leafChildren);
        if (drmInitData != null) {
            int trackCount = this.trackBundles.size();
            for (int i = 0; i < trackCount; i++) {
                this.trackBundles.valueAt(i).updateDrmInitData(drmInitData);
            }
        }
        if (this.pendingSeekTimeUs != C.TIME_UNSET) {
            int trackCount2 = this.trackBundles.size();
            for (int i2 = 0; i2 < trackCount2; i2++) {
                this.trackBundles.valueAt(i2).seek(this.pendingSeekTimeUs);
            }
            this.pendingSeekTimeUs = C.TIME_UNSET;
        }
    }

    private void maybeInitExtraTracks() {
        if (this.emsgTrackOutputs == null) {
            TrackOutput[] trackOutputArr = new TrackOutput[2];
            this.emsgTrackOutputs = trackOutputArr;
            int emsgTrackOutputCount = 0;
            TrackOutput trackOutput = this.additionalEmsgTrackOutput;
            if (trackOutput != null) {
                int emsgTrackOutputCount2 = 0 + 1;
                trackOutputArr[0] = trackOutput;
                emsgTrackOutputCount = emsgTrackOutputCount2;
            }
            if ((this.flags & 4) != 0) {
                trackOutputArr[emsgTrackOutputCount] = this.extractorOutput.track(this.trackBundles.size(), 4);
                emsgTrackOutputCount++;
            }
            TrackOutput[] trackOutputArr2 = (TrackOutput[]) Arrays.copyOf(this.emsgTrackOutputs, emsgTrackOutputCount);
            this.emsgTrackOutputs = trackOutputArr2;
            for (TrackOutput eventMessageTrackOutput : trackOutputArr2) {
                eventMessageTrackOutput.format(EMSG_FORMAT);
            }
        }
        if (this.cea608TrackOutputs == null) {
            this.cea608TrackOutputs = new TrackOutput[this.closedCaptionFormats.size()];
            for (int i = 0; i < this.cea608TrackOutputs.length; i++) {
                TrackOutput output = this.extractorOutput.track(this.trackBundles.size() + 1 + i, 3);
                output.format(this.closedCaptionFormats.get(i));
                this.cea608TrackOutputs[i] = output;
            }
        }
    }

    private void onEmsgLeafAtomRead(ParsableByteArray atom) {
        String value;
        String schemeIdUri;
        long id;
        long durationMs;
        TrackOutput[] trackOutputArr;
        TrackOutput[] trackOutputArr2;
        TrackOutput[] trackOutputArr3 = this.emsgTrackOutputs;
        if (trackOutputArr3 == null || trackOutputArr3.length == 0) {
            return;
        }
        atom.setPosition(8);
        int fullAtom = atom.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        long presentationTimeDeltaUs = C.TIME_UNSET;
        long sampleTimeUs = C.TIME_UNSET;
        switch (version) {
            case 0:
                String schemeIdUri2 = (String) Assertions.checkNotNull(atom.readNullTerminatedString());
                String value2 = (String) Assertions.checkNotNull(atom.readNullTerminatedString());
                long timescale = atom.readUnsignedInt();
                presentationTimeDeltaUs = Util.scaleLargeTimestamp(atom.readUnsignedInt(), 1000000L, timescale);
                long j = this.segmentIndexEarliestPresentationTimeUs;
                if (j != C.TIME_UNSET) {
                    sampleTimeUs = j + presentationTimeDeltaUs;
                }
                long durationMs2 = Util.scaleLargeTimestamp(atom.readUnsignedInt(), 1000L, timescale);
                long id2 = atom.readUnsignedInt();
                durationMs = durationMs2;
                schemeIdUri = schemeIdUri2;
                value = value2;
                id = id2;
                break;
            case 1:
                long timescale2 = atom.readUnsignedInt();
                sampleTimeUs = Util.scaleLargeTimestamp(atom.readUnsignedLongToLong(), 1000000L, timescale2);
                long durationMs3 = Util.scaleLargeTimestamp(atom.readUnsignedInt(), 1000L, timescale2);
                long id3 = atom.readUnsignedInt();
                String schemeIdUri3 = (String) Assertions.checkNotNull(atom.readNullTerminatedString());
                String value3 = (String) Assertions.checkNotNull(atom.readNullTerminatedString());
                durationMs = durationMs3;
                id = id3;
                schemeIdUri = schemeIdUri3;
                value = value3;
                break;
            default:
                Log.w(TAG, "Skipping unsupported emsg version: " + version);
                return;
        }
        byte[] messageData = new byte[atom.bytesLeft()];
        atom.readBytes(messageData, 0, atom.bytesLeft());
        EventMessage eventMessage = new EventMessage(schemeIdUri, value, durationMs, id, messageData);
        ParsableByteArray encodedEventMessage = new ParsableByteArray(this.eventMessageEncoder.encode(eventMessage));
        int sampleSize = encodedEventMessage.bytesLeft();
        for (TrackOutput emsgTrackOutput : this.emsgTrackOutputs) {
            encodedEventMessage.setPosition(0);
            emsgTrackOutput.sampleData(encodedEventMessage, sampleSize);
        }
        if (sampleTimeUs == C.TIME_UNSET) {
            this.pendingMetadataSampleInfos.addLast(new MetadataSampleInfo(presentationTimeDeltaUs, sampleSize));
            this.pendingMetadataSampleBytes += sampleSize;
            return;
        }
        TimestampAdjuster timestampAdjuster = this.timestampAdjuster;
        if (timestampAdjuster != null) {
            sampleTimeUs = timestampAdjuster.adjustSampleTimestamp(sampleTimeUs);
        }
        for (TrackOutput emsgTrackOutput2 : this.emsgTrackOutputs) {
            emsgTrackOutput2.sampleMetadata(sampleTimeUs, 1, sampleSize, 0, null);
        }
    }

    private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray trex) {
        trex.setPosition(12);
        int trackId = trex.readInt();
        int defaultSampleDescriptionIndex = trex.readInt() - 1;
        int defaultSampleDuration = trex.readInt();
        int defaultSampleSize = trex.readInt();
        int defaultSampleFlags = trex.readInt();
        return Pair.create(Integer.valueOf(trackId), new DefaultSampleValues(defaultSampleDescriptionIndex, defaultSampleDuration, defaultSampleSize, defaultSampleFlags));
    }

    private static long parseMehd(ParsableByteArray mehd) {
        mehd.setPosition(8);
        int fullAtom = mehd.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        return version == 0 ? mehd.readUnsignedInt() : mehd.readUnsignedLongToLong();
    }

    private static void parseMoof(Atom.ContainerAtom moof, SparseArray<TrackBundle> trackBundleArray, int flags, byte[] extendedTypeScratch) throws ParserException {
        int moofContainerChildrenSize = moof.containerChildren.size();
        for (int i = 0; i < moofContainerChildrenSize; i++) {
            Atom.ContainerAtom child = moof.containerChildren.get(i);
            if (child.type == 1953653094) {
                parseTraf(child, trackBundleArray, flags, extendedTypeScratch);
            }
        }
    }

    private static void parseTraf(Atom.ContainerAtom traf, SparseArray<TrackBundle> trackBundleArray, int flags, byte[] extendedTypeScratch) throws ParserException {
        Atom.ContainerAtom containerAtom = traf;
        Atom.LeafAtom tfhd = containerAtom.getLeafAtomOfType(Atom.TYPE_tfhd);
        TrackBundle trackBundle = parseTfhd(tfhd.data, trackBundleArray);
        if (trackBundle == null) {
            return;
        }
        TrackFragment fragment = trackBundle.fragment;
        long decodeTime = fragment.nextFragmentDecodeTime;
        trackBundle.reset();
        Atom.LeafAtom tfdtAtom = containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt);
        if (tfdtAtom != null && (flags & 2) == 0) {
            decodeTime = parseTfdt(containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt).data);
        }
        parseTruns(containerAtom, trackBundle, decodeTime, flags);
        TrackEncryptionBox encryptionBox = trackBundle.track.getSampleDescriptionEncryptionBox(fragment.header.sampleDescriptionIndex);
        Atom.LeafAtom saiz = containerAtom.getLeafAtomOfType(Atom.TYPE_saiz);
        if (saiz != null) {
            parseSaiz(encryptionBox, saiz.data, fragment);
        }
        Atom.LeafAtom saio = containerAtom.getLeafAtomOfType(Atom.TYPE_saio);
        if (saio != null) {
            parseSaio(saio.data, fragment);
        }
        Atom.LeafAtom senc = containerAtom.getLeafAtomOfType(Atom.TYPE_senc);
        if (senc != null) {
            parseSenc(senc.data, fragment);
        }
        Atom.LeafAtom sbgp = containerAtom.getLeafAtomOfType(Atom.TYPE_sbgp);
        Atom.LeafAtom sgpd = containerAtom.getLeafAtomOfType(Atom.TYPE_sgpd);
        if (sbgp != null && sgpd != null) {
            parseSgpd(sbgp.data, sgpd.data, encryptionBox != null ? encryptionBox.schemeType : null, fragment);
        }
        int leafChildrenSize = containerAtom.leafChildren.size();
        int i = 0;
        while (i < leafChildrenSize) {
            Atom.LeafAtom atom = containerAtom.leafChildren.get(i);
            int leafChildrenSize2 = leafChildrenSize;
            if (atom.type == 1970628964) {
                parseUuid(atom.data, fragment, extendedTypeScratch);
            }
            i++;
            containerAtom = traf;
            leafChildrenSize = leafChildrenSize2;
        }
    }

    private static void parseTruns(Atom.ContainerAtom traf, TrackBundle trackBundle, long decodeTime, int flags) throws ParserException {
        List<Atom.LeafAtom> leafChildren = traf.leafChildren;
        int leafChildrenSize = leafChildren.size();
        int trunCount = 0;
        int totalSampleCount = 0;
        for (int i = 0; i < leafChildrenSize; i++) {
            Atom.LeafAtom atom = leafChildren.get(i);
            if (atom.type == 1953658222) {
                ParsableByteArray trunData = atom.data;
                trunData.setPosition(12);
                int trunSampleCount = trunData.readUnsignedIntToInt();
                if (trunSampleCount > 0) {
                    totalSampleCount += trunSampleCount;
                    trunCount++;
                }
            }
        }
        trackBundle.currentTrackRunIndex = 0;
        trackBundle.currentSampleInTrackRun = 0;
        trackBundle.currentSampleIndex = 0;
        trackBundle.fragment.initTables(trunCount, totalSampleCount);
        int trunStartPosition = 0;
        int trunIndex = 0;
        for (int i2 = 0; i2 < leafChildrenSize; i2++) {
            Atom.LeafAtom trun = leafChildren.get(i2);
            if (trun.type == 1953658222) {
                trunStartPosition = parseTrun(trackBundle, trunIndex, decodeTime, flags, trun.data, trunStartPosition);
                trunIndex++;
            }
        }
    }

    private static void parseSaiz(TrackEncryptionBox encryptionBox, ParsableByteArray saiz, TrackFragment out) throws ParserException {
        int vectorSize = encryptionBox.perSampleIvSize;
        saiz.setPosition(8);
        int fullAtom = saiz.readInt();
        int flags = Atom.parseFullAtomFlags(fullAtom);
        boolean subsampleEncryption = true;
        if ((flags & 1) == 1) {
            saiz.skipBytes(8);
        }
        int defaultSampleInfoSize = saiz.readUnsignedByte();
        int sampleCount = saiz.readUnsignedIntToInt();
        if (sampleCount != out.sampleCount) {
            throw new ParserException("Length mismatch: " + sampleCount + ", " + out.sampleCount);
        }
        int totalSize = 0;
        if (defaultSampleInfoSize == 0) {
            boolean[] sampleHasSubsampleEncryptionTable = out.sampleHasSubsampleEncryptionTable;
            for (int i = 0; i < sampleCount; i++) {
                int sampleInfoSize = saiz.readUnsignedByte();
                totalSize += sampleInfoSize;
                sampleHasSubsampleEncryptionTable[i] = sampleInfoSize > vectorSize;
            }
        } else {
            if (defaultSampleInfoSize <= vectorSize) {
                subsampleEncryption = false;
            }
            totalSize = 0 + (defaultSampleInfoSize * sampleCount);
            Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
        }
        out.initEncryptionData(totalSize);
    }

    private static void parseSaio(ParsableByteArray saio, TrackFragment out) throws ParserException {
        saio.setPosition(8);
        int fullAtom = saio.readInt();
        int flags = Atom.parseFullAtomFlags(fullAtom);
        if ((flags & 1) == 1) {
            saio.skipBytes(8);
        }
        int entryCount = saio.readUnsignedIntToInt();
        if (entryCount != 1) {
            throw new ParserException("Unexpected saio entry count: " + entryCount);
        }
        int version = Atom.parseFullAtomVersion(fullAtom);
        out.auxiliaryDataPosition += version == 0 ? saio.readUnsignedInt() : saio.readUnsignedLongToLong();
    }

    private static TrackBundle parseTfhd(ParsableByteArray tfhd, SparseArray<TrackBundle> trackBundles) {
        int defaultSampleDescriptionIndex;
        int defaultSampleDuration;
        int defaultSampleSize;
        int defaultSampleFlags;
        tfhd.setPosition(8);
        int fullAtom = tfhd.readInt();
        int atomFlags = Atom.parseFullAtomFlags(fullAtom);
        int trackId = tfhd.readInt();
        TrackBundle trackBundle = getTrackBundle(trackBundles, trackId);
        if (trackBundle == null) {
            return null;
        }
        if ((atomFlags & 1) != 0) {
            long baseDataPosition = tfhd.readUnsignedLongToLong();
            trackBundle.fragment.dataPosition = baseDataPosition;
            trackBundle.fragment.auxiliaryDataPosition = baseDataPosition;
        }
        DefaultSampleValues defaultSampleValues = trackBundle.defaultSampleValues;
        if ((atomFlags & 2) != 0) {
            defaultSampleDescriptionIndex = tfhd.readInt() - 1;
        } else {
            defaultSampleDescriptionIndex = defaultSampleValues.sampleDescriptionIndex;
        }
        if ((atomFlags & 8) != 0) {
            defaultSampleDuration = tfhd.readInt();
        } else {
            defaultSampleDuration = defaultSampleValues.duration;
        }
        if ((atomFlags & 16) != 0) {
            defaultSampleSize = tfhd.readInt();
        } else {
            defaultSampleSize = defaultSampleValues.size;
        }
        if ((atomFlags & 32) != 0) {
            defaultSampleFlags = tfhd.readInt();
        } else {
            defaultSampleFlags = defaultSampleValues.flags;
        }
        trackBundle.fragment.header = new DefaultSampleValues(defaultSampleDescriptionIndex, defaultSampleDuration, defaultSampleSize, defaultSampleFlags);
        return trackBundle;
    }

    private static TrackBundle getTrackBundle(SparseArray<TrackBundle> trackBundles, int trackId) {
        if (trackBundles.size() == 1) {
            return trackBundles.valueAt(0);
        }
        return trackBundles.get(trackId);
    }

    private static long parseTfdt(ParsableByteArray tfdt) {
        tfdt.setPosition(8);
        int fullAtom = tfdt.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        return version == 1 ? tfdt.readUnsignedLongToLong() : tfdt.readUnsignedInt();
    }

    private static int parseTrun(TrackBundle trackBundle, int index, long decodeTime, int flags, ParsableByteArray trun, int trackRunStart) throws ParserException {
        int firstSampleFlags;
        boolean sampleDurationsPresent;
        int i;
        boolean sampleSizesPresent;
        int i2;
        boolean firstSampleFlagsPresent;
        int i3;
        boolean sampleCompositionTimeOffsetsPresent;
        boolean sampleFlagsPresent;
        DefaultSampleValues defaultSampleValues;
        trun.setPosition(8);
        int fullAtom = trun.readInt();
        int atomFlags = Atom.parseFullAtomFlags(fullAtom);
        Track track = trackBundle.track;
        TrackFragment fragment = trackBundle.fragment;
        DefaultSampleValues defaultSampleValues2 = fragment.header;
        fragment.trunLength[index] = trun.readUnsignedIntToInt();
        fragment.trunDataPosition[index] = fragment.dataPosition;
        if ((atomFlags & 1) != 0) {
            long[] jArr = fragment.trunDataPosition;
            jArr[index] = jArr[index] + trun.readInt();
        }
        boolean firstSampleFlagsPresent2 = (atomFlags & 4) != 0;
        int firstSampleFlags2 = defaultSampleValues2.flags;
        if (firstSampleFlagsPresent2) {
            firstSampleFlags2 = trun.readInt();
        }
        boolean sampleDurationsPresent2 = (atomFlags & 256) != 0;
        boolean sampleSizesPresent2 = (atomFlags & 512) != 0;
        boolean sampleFlagsPresent2 = (atomFlags & 1024) != 0;
        boolean sampleCompositionTimeOffsetsPresent2 = (atomFlags & 2048) != 0;
        long edtsOffsetUs = 0;
        if (track.editListDurations == null || track.editListDurations.length != 1 || track.editListDurations[0] != 0) {
            firstSampleFlags = firstSampleFlags2;
        } else {
            firstSampleFlags = firstSampleFlags2;
            edtsOffsetUs = Util.scaleLargeTimestamp(track.editListMediaTimes[0], 1000000L, track.timescale);
        }
        int[] sampleSizeTable = fragment.sampleSizeTable;
        int[] sampleCompositionTimeOffsetUsTable = fragment.sampleCompositionTimeOffsetUsTable;
        long[] sampleDecodingTimeUsTable = fragment.sampleDecodingTimeUsTable;
        boolean[] sampleIsSyncFrameTable = fragment.sampleIsSyncFrameTable;
        int fullAtom2 = track.type;
        boolean workaroundEveryVideoFrameIsSyncFrame = fullAtom2 == 2 && (flags & 1) != 0;
        int trackRunEnd = trackRunStart + fragment.trunLength[index];
        boolean workaroundEveryVideoFrameIsSyncFrame2 = workaroundEveryVideoFrameIsSyncFrame;
        long timescale = track.timescale;
        long cumulativeTime = index > 0 ? fragment.nextFragmentDecodeTime : decodeTime;
        int i4 = trackRunStart;
        while (i4 < trackRunEnd) {
            if (sampleDurationsPresent2) {
                i = trun.readInt();
                sampleDurationsPresent = sampleDurationsPresent2;
            } else {
                sampleDurationsPresent = sampleDurationsPresent2;
                i = defaultSampleValues2.duration;
            }
            int sampleDuration = checkNonNegative(i);
            if (sampleSizesPresent2) {
                i2 = trun.readInt();
                sampleSizesPresent = sampleSizesPresent2;
            } else {
                sampleSizesPresent = sampleSizesPresent2;
                i2 = defaultSampleValues2.size;
            }
            int sampleSize = checkNonNegative(i2);
            if (i4 == 0 && firstSampleFlagsPresent2) {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                i3 = firstSampleFlags;
            } else if (sampleFlagsPresent2) {
                i3 = trun.readInt();
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
            } else {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                i3 = defaultSampleValues2.flags;
            }
            int sampleFlags = i3;
            if (sampleCompositionTimeOffsetsPresent2) {
                defaultSampleValues = defaultSampleValues2;
                int sampleOffset = trun.readInt();
                sampleFlagsPresent = sampleFlagsPresent2;
                sampleCompositionTimeOffsetsPresent = sampleCompositionTimeOffsetsPresent2;
                sampleCompositionTimeOffsetUsTable[i4] = (int) ((sampleOffset * 1000000) / timescale);
            } else {
                defaultSampleValues = defaultSampleValues2;
                sampleFlagsPresent = sampleFlagsPresent2;
                sampleCompositionTimeOffsetsPresent = sampleCompositionTimeOffsetsPresent2;
                sampleCompositionTimeOffsetUsTable[i4] = 0;
            }
            sampleDecodingTimeUsTable[i4] = Util.scaleLargeTimestamp(cumulativeTime, 1000000L, timescale) - edtsOffsetUs;
            sampleSizeTable[i4] = sampleSize;
            sampleIsSyncFrameTable[i4] = ((sampleFlags >> 16) & 1) == 0 && (!workaroundEveryVideoFrameIsSyncFrame2 || i4 == 0);
            cumulativeTime += sampleDuration;
            i4++;
            sampleDurationsPresent2 = sampleDurationsPresent;
            sampleSizesPresent2 = sampleSizesPresent;
            firstSampleFlagsPresent2 = firstSampleFlagsPresent;
            defaultSampleValues2 = defaultSampleValues;
            sampleFlagsPresent2 = sampleFlagsPresent;
            sampleCompositionTimeOffsetsPresent2 = sampleCompositionTimeOffsetsPresent;
        }
        fragment.nextFragmentDecodeTime = cumulativeTime;
        return trackRunEnd;
    }

    private static int checkNonNegative(int value) throws ParserException {
        if (value < 0) {
            throw new ParserException("Unexpected negtive value: " + value);
        }
        return value;
    }

    private static void parseUuid(ParsableByteArray uuid, TrackFragment out, byte[] extendedTypeScratch) throws ParserException {
        uuid.setPosition(8);
        uuid.readBytes(extendedTypeScratch, 0, 16);
        if (!Arrays.equals(extendedTypeScratch, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
            return;
        }
        parseSenc(uuid, 16, out);
    }

    private static void parseSenc(ParsableByteArray senc, TrackFragment out) throws ParserException {
        parseSenc(senc, 0, out);
    }

    private static void parseSenc(ParsableByteArray senc, int offset, TrackFragment out) throws ParserException {
        senc.setPosition(offset + 8);
        int fullAtom = senc.readInt();
        int flags = Atom.parseFullAtomFlags(fullAtom);
        if ((flags & 1) != 0) {
            throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
        }
        boolean subsampleEncryption = (flags & 2) != 0;
        int sampleCount = senc.readUnsignedIntToInt();
        if (sampleCount != out.sampleCount) {
            throw new ParserException("Length mismatch: " + sampleCount + ", " + out.sampleCount);
        }
        Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
        out.initEncryptionData(senc.bytesLeft());
        out.fillEncryptionData(senc);
    }

    private static void parseSgpd(ParsableByteArray sbgp, ParsableByteArray sgpd, String schemeType, TrackFragment out) throws ParserException {
        byte[] constantIv;
        sbgp.setPosition(8);
        int sbgpFullAtom = sbgp.readInt();
        if (sbgp.readInt() != SAMPLE_GROUP_TYPE_seig) {
            return;
        }
        if (Atom.parseFullAtomVersion(sbgpFullAtom) == 1) {
            sbgp.skipBytes(4);
        }
        if (sbgp.readInt() != 1) {
            throw new ParserException("Entry count in sbgp != 1 (unsupported).");
        }
        sgpd.setPosition(8);
        int sgpdFullAtom = sgpd.readInt();
        if (sgpd.readInt() != SAMPLE_GROUP_TYPE_seig) {
            return;
        }
        int sgpdVersion = Atom.parseFullAtomVersion(sgpdFullAtom);
        if (sgpdVersion == 1) {
            if (sgpd.readUnsignedInt() == 0) {
                throw new ParserException("Variable length description in sgpd found (unsupported)");
            }
        } else if (sgpdVersion >= 2) {
            sgpd.skipBytes(4);
        }
        if (sgpd.readUnsignedInt() != 1) {
            throw new ParserException("Entry count in sgpd != 1 (unsupported).");
        }
        sgpd.skipBytes(1);
        int patternByte = sgpd.readUnsignedByte();
        int cryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
        int skipByteBlock = patternByte & 15;
        boolean isProtected = sgpd.readUnsignedByte() == 1;
        if (!isProtected) {
            return;
        }
        int perSampleIvSize = sgpd.readUnsignedByte();
        byte[] keyId = new byte[16];
        sgpd.readBytes(keyId, 0, keyId.length);
        if (perSampleIvSize != 0) {
            constantIv = null;
        } else {
            int constantIvSize = sgpd.readUnsignedByte();
            byte[] constantIv2 = new byte[constantIvSize];
            sgpd.readBytes(constantIv2, 0, constantIvSize);
            constantIv = constantIv2;
        }
        out.definesEncryptionData = true;
        out.trackEncryptionBox = new TrackEncryptionBox(isProtected, schemeType, perSampleIvSize, keyId, cryptByteBlock, skipByteBlock, constantIv);
    }

    private static Pair<Long, ChunkIndex> parseSidx(ParsableByteArray atom, long inputPosition) throws ParserException {
        long earliestPresentationTime;
        long offset;
        int[] sizes;
        atom.setPosition(8);
        int fullAtom = atom.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        atom.skipBytes(4);
        long timescale = atom.readUnsignedInt();
        if (version == 0) {
            long earliestPresentationTime2 = atom.readUnsignedInt();
            long offset2 = inputPosition + atom.readUnsignedInt();
            offset = offset2;
            earliestPresentationTime = earliestPresentationTime2;
        } else {
            long earliestPresentationTime3 = atom.readUnsignedLongToLong();
            long offset3 = inputPosition + atom.readUnsignedLongToLong();
            offset = offset3;
            earliestPresentationTime = earliestPresentationTime3;
        }
        long earliestPresentationTimeUs = Util.scaleLargeTimestamp(earliestPresentationTime, 1000000L, timescale);
        atom.skipBytes(2);
        int referenceCount = atom.readUnsignedShort();
        int[] sizes2 = new int[referenceCount];
        long[] offsets = new long[referenceCount];
        long[] durationsUs = new long[referenceCount];
        long[] timesUs = new long[referenceCount];
        long time = earliestPresentationTime;
        long timeUs = earliestPresentationTimeUs;
        long time2 = time;
        long time3 = offset;
        int i = 0;
        while (i < referenceCount) {
            int firstInt = atom.readInt();
            int type = firstInt & Integer.MIN_VALUE;
            if (type != 0) {
                throw new ParserException("Unhandled indirect reference");
            }
            long referenceDuration = atom.readUnsignedInt();
            sizes2[i] = Integer.MAX_VALUE & firstInt;
            offsets[i] = time3;
            timesUs[i] = timeUs;
            time2 += referenceDuration;
            long[] timesUs2 = timesUs;
            int version2 = version;
            long[] durationsUs2 = durationsUs;
            timeUs = Util.scaleLargeTimestamp(time2, 1000000L, timescale);
            durationsUs2[i] = timeUs - timesUs2[i];
            atom.skipBytes(4);
            time3 += sizes[i];
            i++;
            offsets = offsets;
            durationsUs = durationsUs2;
            timesUs = timesUs2;
            sizes2 = sizes2;
            referenceCount = referenceCount;
            fullAtom = fullAtom;
            version = version2;
        }
        return Pair.create(Long.valueOf(earliestPresentationTimeUs), new ChunkIndex(sizes2, offsets, durationsUs, timesUs));
    }

    private void readEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
        TrackBundle nextTrackBundle = null;
        long nextDataOffset = Long.MAX_VALUE;
        int trackBundlesSize = this.trackBundles.size();
        for (int i = 0; i < trackBundlesSize; i++) {
            TrackFragment trackFragment = this.trackBundles.valueAt(i).fragment;
            if (trackFragment.sampleEncryptionDataNeedsFill && trackFragment.auxiliaryDataPosition < nextDataOffset) {
                nextDataOffset = trackFragment.auxiliaryDataPosition;
                TrackBundle nextTrackBundle2 = this.trackBundles.valueAt(i);
                nextTrackBundle = nextTrackBundle2;
            }
        }
        if (nextTrackBundle == null) {
            this.parserState = 3;
            return;
        }
        int bytesToSkip = (int) (nextDataOffset - input.getPosition());
        if (bytesToSkip < 0) {
            throw new ParserException("Offset to encryption data was negative.");
        }
        input.skipFully(bytesToSkip);
        nextTrackBundle.fragment.fillEncryptionData(input);
    }

    private boolean readSample(ExtractorInput input) throws IOException, InterruptedException {
        long sampleTimeUs;
        int writtenBytes;
        int i = 4;
        int i2 = 1;
        int i3 = 0;
        if (this.parserState == 3) {
            if (this.currentTrackBundle == null) {
                TrackBundle currentTrackBundle = getNextFragmentRun(this.trackBundles);
                if (currentTrackBundle == null) {
                    int bytesToSkip = (int) (this.endOfMdatPosition - input.getPosition());
                    if (bytesToSkip < 0) {
                        throw new ParserException("Offset to end of mdat was negative.");
                    }
                    input.skipFully(bytesToSkip);
                    enterReadingAtomHeaderState();
                    return false;
                }
                long nextDataPosition = currentTrackBundle.fragment.trunDataPosition[currentTrackBundle.currentTrackRunIndex];
                int bytesToSkip2 = (int) (nextDataPosition - input.getPosition());
                if (bytesToSkip2 < 0) {
                    Log.w(TAG, "Ignoring negative offset to sample data.");
                    bytesToSkip2 = 0;
                }
                input.skipFully(bytesToSkip2);
                this.currentTrackBundle = currentTrackBundle;
            }
            this.sampleSize = this.currentTrackBundle.fragment.sampleSizeTable[this.currentTrackBundle.currentSampleIndex];
            if (this.currentTrackBundle.currentSampleIndex < this.currentTrackBundle.firstSampleToOutputIndex) {
                input.skipFully(this.sampleSize);
                this.currentTrackBundle.skipSampleEncryptionData();
                if (!this.currentTrackBundle.next()) {
                    this.currentTrackBundle = null;
                }
                this.parserState = 3;
                return true;
            }
            if (this.currentTrackBundle.track.sampleTransformation == 1) {
                this.sampleSize -= 8;
                input.skipFully(8);
            }
            if (MimeTypes.AUDIO_AC4.equals(this.currentTrackBundle.track.format.sampleMimeType)) {
                this.sampleBytesWritten = this.currentTrackBundle.outputSampleEncryptionData(this.sampleSize, 7);
                Ac4Util.getAc4SampleHeader(this.sampleSize, this.scratch);
                this.currentTrackBundle.output.sampleData(this.scratch, 7);
                this.sampleBytesWritten += 7;
            } else {
                this.sampleBytesWritten = this.currentTrackBundle.outputSampleEncryptionData(this.sampleSize, 0);
            }
            this.sampleSize += this.sampleBytesWritten;
            this.parserState = 4;
            this.sampleCurrentNalBytesRemaining = 0;
        }
        TrackFragment fragment = this.currentTrackBundle.fragment;
        Track track = this.currentTrackBundle.track;
        TrackOutput output = this.currentTrackBundle.output;
        int sampleIndex = this.currentTrackBundle.currentSampleIndex;
        long sampleTimeUs2 = fragment.getSamplePresentationTimeUs(sampleIndex);
        TimestampAdjuster timestampAdjuster = this.timestampAdjuster;
        if (timestampAdjuster == null) {
            sampleTimeUs = sampleTimeUs2;
        } else {
            sampleTimeUs = timestampAdjuster.adjustSampleTimestamp(sampleTimeUs2);
        }
        if (track.nalUnitLengthFieldLength == 0) {
            while (true) {
                int i4 = this.sampleBytesWritten;
                int i5 = this.sampleSize;
                if (i4 >= i5) {
                    break;
                }
                this.sampleBytesWritten += output.sampleData(input, i5 - i4, false);
            }
        } else {
            byte[] nalPrefixData = this.nalPrefix.data;
            nalPrefixData[0] = 0;
            nalPrefixData[1] = 0;
            nalPrefixData[2] = 0;
            int nalUnitPrefixLength = track.nalUnitLengthFieldLength + 1;
            int nalUnitLengthFieldLengthDiff = 4 - track.nalUnitLengthFieldLength;
            while (this.sampleBytesWritten < this.sampleSize) {
                int i6 = this.sampleCurrentNalBytesRemaining;
                if (i6 == 0) {
                    input.readFully(nalPrefixData, nalUnitLengthFieldLengthDiff, nalUnitPrefixLength);
                    this.nalPrefix.setPosition(i3);
                    int nalLengthInt = this.nalPrefix.readInt();
                    if (nalLengthInt >= i2) {
                        this.sampleCurrentNalBytesRemaining = nalLengthInt - 1;
                        this.nalStartCode.setPosition(i3);
                        output.sampleData(this.nalStartCode, i);
                        output.sampleData(this.nalPrefix, i2);
                        this.processSeiNalUnitPayload = this.cea608TrackOutputs.length > 0 && NalUnitUtil.isNalUnitSei(track.format.sampleMimeType, nalPrefixData[i]);
                        this.sampleBytesWritten += 5;
                        this.sampleSize += nalUnitLengthFieldLengthDiff;
                        i2 = 1;
                    } else {
                        throw new ParserException("Invalid NAL length");
                    }
                } else {
                    if (this.processSeiNalUnitPayload) {
                        this.nalBuffer.reset(i6);
                        input.readFully(this.nalBuffer.data, i3, this.sampleCurrentNalBytesRemaining);
                        output.sampleData(this.nalBuffer, this.sampleCurrentNalBytesRemaining);
                        writtenBytes = this.sampleCurrentNalBytesRemaining;
                        int unescapedLength = NalUnitUtil.unescapeStream(this.nalBuffer.data, this.nalBuffer.limit());
                        this.nalBuffer.setPosition(MimeTypes.VIDEO_H265.equals(track.format.sampleMimeType) ? 1 : 0);
                        this.nalBuffer.setLimit(unescapedLength);
                        CeaUtil.consume(sampleTimeUs, this.nalBuffer, this.cea608TrackOutputs);
                    } else {
                        writtenBytes = output.sampleData(input, i6, false);
                    }
                    this.sampleBytesWritten += writtenBytes;
                    this.sampleCurrentNalBytesRemaining -= writtenBytes;
                    i = 4;
                    i2 = 1;
                    i3 = 0;
                }
            }
        }
        int sampleFlags = fragment.sampleIsSyncFrameTable[sampleIndex] ? 1 : 0;
        TrackOutput.CryptoData cryptoData = null;
        TrackEncryptionBox encryptionBox = this.currentTrackBundle.getEncryptionBoxIfEncrypted();
        if (encryptionBox != null) {
            sampleFlags |= C.BUFFER_FLAG_ENCRYPTED;
            cryptoData = encryptionBox.cryptoData;
        }
        output.sampleMetadata(sampleTimeUs, sampleFlags, this.sampleSize, 0, cryptoData);
        outputPendingMetadataSamples(sampleTimeUs);
        if (!this.currentTrackBundle.next()) {
            this.currentTrackBundle = null;
        }
        this.parserState = 3;
        return true;
    }

    private void outputPendingMetadataSamples(long sampleTimeUs) {
        TrackOutput[] trackOutputArr;
        while (!this.pendingMetadataSampleInfos.isEmpty()) {
            MetadataSampleInfo sampleInfo = this.pendingMetadataSampleInfos.removeFirst();
            this.pendingMetadataSampleBytes -= sampleInfo.size;
            long metadataTimeUs = sampleTimeUs + sampleInfo.presentationTimeDeltaUs;
            TimestampAdjuster timestampAdjuster = this.timestampAdjuster;
            if (timestampAdjuster != null) {
                metadataTimeUs = timestampAdjuster.adjustSampleTimestamp(metadataTimeUs);
            }
            for (TrackOutput emsgTrackOutput : this.emsgTrackOutputs) {
                emsgTrackOutput.sampleMetadata(metadataTimeUs, 1, sampleInfo.size, this.pendingMetadataSampleBytes, null);
            }
        }
    }

    private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> trackBundles) {
        TrackBundle nextTrackBundle = null;
        long nextTrackRunOffset = Long.MAX_VALUE;
        int trackBundlesSize = trackBundles.size();
        for (int i = 0; i < trackBundlesSize; i++) {
            TrackBundle trackBundle = trackBundles.valueAt(i);
            if (trackBundle.currentTrackRunIndex != trackBundle.fragment.trunCount) {
                long trunOffset = trackBundle.fragment.trunDataPosition[trackBundle.currentTrackRunIndex];
                if (trunOffset < nextTrackRunOffset) {
                    nextTrackBundle = trackBundle;
                    nextTrackRunOffset = trunOffset;
                }
            }
        }
        return nextTrackBundle;
    }

    private static DrmInitData getDrmInitDataFromAtoms(List<Atom.LeafAtom> leafChildren) {
        ArrayList<DrmInitData.SchemeData> schemeDatas = null;
        int leafChildrenSize = leafChildren.size();
        for (int i = 0; i < leafChildrenSize; i++) {
            Atom.LeafAtom child = leafChildren.get(i);
            if (child.type == 1886614376) {
                if (schemeDatas == null) {
                    schemeDatas = new ArrayList<>();
                }
                byte[] psshData = child.data.data;
                UUID uuid = PsshAtomUtil.parseUuid(psshData);
                if (uuid == null) {
                    Log.w(TAG, "Skipped pssh atom (failed to extract uuid)");
                } else {
                    schemeDatas.add(new DrmInitData.SchemeData(uuid, MimeTypes.VIDEO_MP4, psshData));
                }
            }
        }
        if (schemeDatas == null) {
            return null;
        }
        return new DrmInitData(schemeDatas);
    }

    private static boolean shouldParseLeafAtom(int atom) {
        return atom == 1751411826 || atom == 1835296868 || atom == 1836476516 || atom == 1936286840 || atom == 1937011556 || atom == 1952867444 || atom == 1952868452 || atom == 1953196132 || atom == 1953654136 || atom == 1953658222 || atom == 1886614376 || atom == 1935763834 || atom == 1935763823 || atom == 1936027235 || atom == 1970628964 || atom == 1935828848 || atom == 1936158820 || atom == 1701606260 || atom == 1835362404 || atom == 1701671783;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        return atom == 1836019574 || atom == 1953653099 || atom == 1835297121 || atom == 1835626086 || atom == 1937007212 || atom == 1836019558 || atom == 1953653094 || atom == 1836475768 || atom == 1701082227;
    }

    /* loaded from: classes3.dex */
    public static final class MetadataSampleInfo {
        public final long presentationTimeDeltaUs;
        public final int size;

        public MetadataSampleInfo(long presentationTimeDeltaUs, int size) {
            this.presentationTimeDeltaUs = presentationTimeDeltaUs;
            this.size = size;
        }
    }

    /* loaded from: classes3.dex */
    public static final class TrackBundle {
        private static final int SINGLE_SUBSAMPLE_ENCRYPTION_DATA_LENGTH = 8;
        public int currentSampleInTrackRun;
        public int currentSampleIndex;
        public int currentTrackRunIndex;
        public DefaultSampleValues defaultSampleValues;
        public int firstSampleToOutputIndex;
        public final TrackOutput output;
        public Track track;
        public final TrackFragment fragment = new TrackFragment();
        public final ParsableByteArray scratch = new ParsableByteArray();
        private final ParsableByteArray encryptionSignalByte = new ParsableByteArray(1);
        private final ParsableByteArray defaultInitializationVector = new ParsableByteArray();

        public TrackBundle(TrackOutput output) {
            this.output = output;
        }

        public void init(Track track, DefaultSampleValues defaultSampleValues) {
            this.track = (Track) Assertions.checkNotNull(track);
            this.defaultSampleValues = (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValues);
            this.output.format(track.format);
            reset();
        }

        public void updateDrmInitData(DrmInitData drmInitData) {
            TrackEncryptionBox encryptionBox = this.track.getSampleDescriptionEncryptionBox(this.fragment.header.sampleDescriptionIndex);
            String schemeType = encryptionBox != null ? encryptionBox.schemeType : null;
            this.output.format(this.track.format.copyWithDrmInitData(drmInitData.copyWithSchemeType(schemeType)));
        }

        public void reset() {
            this.fragment.reset();
            this.currentSampleIndex = 0;
            this.currentTrackRunIndex = 0;
            this.currentSampleInTrackRun = 0;
            this.firstSampleToOutputIndex = 0;
        }

        public void seek(long timeUs) {
            for (int searchIndex = this.currentSampleIndex; searchIndex < this.fragment.sampleCount && this.fragment.getSamplePresentationTimeUs(searchIndex) < timeUs; searchIndex++) {
                if (this.fragment.sampleIsSyncFrameTable[searchIndex]) {
                    this.firstSampleToOutputIndex = searchIndex;
                }
            }
        }

        public boolean next() {
            this.currentSampleIndex++;
            int i = this.currentSampleInTrackRun + 1;
            this.currentSampleInTrackRun = i;
            int[] iArr = this.fragment.trunLength;
            int i2 = this.currentTrackRunIndex;
            if (i == iArr[i2]) {
                this.currentTrackRunIndex = i2 + 1;
                this.currentSampleInTrackRun = 0;
                return false;
            }
            return true;
        }

        public int outputSampleEncryptionData(int sampleSize, int clearHeaderSize) {
            int vectorSize;
            ParsableByteArray initializationVectorData;
            TrackEncryptionBox encryptionBox = getEncryptionBoxIfEncrypted();
            if (encryptionBox == null) {
                return 0;
            }
            if (encryptionBox.perSampleIvSize != 0) {
                initializationVectorData = this.fragment.sampleEncryptionData;
                vectorSize = encryptionBox.perSampleIvSize;
            } else {
                byte[] initVectorData = encryptionBox.defaultInitializationVector;
                this.defaultInitializationVector.reset(initVectorData, initVectorData.length);
                ParsableByteArray initializationVectorData2 = this.defaultInitializationVector;
                int length = initVectorData.length;
                initializationVectorData = initializationVectorData2;
                vectorSize = length;
            }
            boolean haveSubsampleEncryptionTable = this.fragment.sampleHasSubsampleEncryptionTable(this.currentSampleIndex);
            boolean writeSubsampleEncryptionData = haveSubsampleEncryptionTable || clearHeaderSize != 0;
            this.encryptionSignalByte.data[0] = (byte) ((writeSubsampleEncryptionData ? 128 : 0) | vectorSize);
            this.encryptionSignalByte.setPosition(0);
            this.output.sampleData(this.encryptionSignalByte, 1);
            this.output.sampleData(initializationVectorData, vectorSize);
            if (!writeSubsampleEncryptionData) {
                return vectorSize + 1;
            }
            if (!haveSubsampleEncryptionTable) {
                this.scratch.reset(8);
                this.scratch.data[0] = 0;
                this.scratch.data[1] = 1;
                this.scratch.data[2] = (byte) ((clearHeaderSize >> 8) & 255);
                this.scratch.data[3] = (byte) (clearHeaderSize & 255);
                this.scratch.data[4] = (byte) ((sampleSize >> 24) & 255);
                this.scratch.data[5] = (byte) ((sampleSize >> 16) & 255);
                this.scratch.data[6] = (byte) ((sampleSize >> 8) & 255);
                this.scratch.data[7] = (byte) (sampleSize & 255);
                this.output.sampleData(this.scratch, 8);
                return vectorSize + 1 + 8;
            }
            ParsableByteArray subsampleEncryptionData = this.fragment.sampleEncryptionData;
            int subsampleCount = subsampleEncryptionData.readUnsignedShort();
            subsampleEncryptionData.skipBytes(-2);
            int subsampleDataLength = (subsampleCount * 6) + 2;
            if (clearHeaderSize != 0) {
                this.scratch.reset(subsampleDataLength);
                this.scratch.readBytes(subsampleEncryptionData.data, 0, subsampleDataLength);
                subsampleEncryptionData.skipBytes(subsampleDataLength);
                int clearDataSize = ((this.scratch.data[2] & 255) << 8) | (this.scratch.data[3] & 255);
                int adjustedClearDataSize = clearDataSize + clearHeaderSize;
                this.scratch.data[2] = (byte) ((adjustedClearDataSize >> 8) & 255);
                this.scratch.data[3] = (byte) (adjustedClearDataSize & 255);
                subsampleEncryptionData = this.scratch;
            }
            this.output.sampleData(subsampleEncryptionData, subsampleDataLength);
            return vectorSize + 1 + subsampleDataLength;
        }

        public void skipSampleEncryptionData() {
            TrackEncryptionBox encryptionBox = getEncryptionBoxIfEncrypted();
            if (encryptionBox == null) {
                return;
            }
            ParsableByteArray sampleEncryptionData = this.fragment.sampleEncryptionData;
            if (encryptionBox.perSampleIvSize != 0) {
                sampleEncryptionData.skipBytes(encryptionBox.perSampleIvSize);
            }
            if (this.fragment.sampleHasSubsampleEncryptionTable(this.currentSampleIndex)) {
                sampleEncryptionData.skipBytes(sampleEncryptionData.readUnsignedShort() * 6);
            }
        }

        public TrackEncryptionBox getEncryptionBoxIfEncrypted() {
            TrackEncryptionBox encryptionBox;
            int sampleDescriptionIndex = this.fragment.header.sampleDescriptionIndex;
            if (this.fragment.trackEncryptionBox != null) {
                encryptionBox = this.fragment.trackEncryptionBox;
            } else {
                encryptionBox = this.track.getSampleDescriptionEncryptionBox(sampleDescriptionIndex);
            }
            if (encryptionBox == null || !encryptionBox.isEncrypted) {
                return null;
            }
            return encryptionBox;
        }
    }
}
