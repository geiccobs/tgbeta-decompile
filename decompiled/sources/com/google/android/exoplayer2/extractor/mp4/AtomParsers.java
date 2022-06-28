package com.google.android.exoplayer2.extractor.mp4;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.audio.Ac4Util;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.mp4.Atom;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class AtomParsers {
    private static final int MAX_GAPLESS_TRIM_SIZE_SAMPLES = 4;
    private static final String TAG = "AtomParsers";
    private static final int TYPE_clcp = 1668047728;
    private static final int TYPE_mdta = 1835299937;
    private static final int TYPE_meta = 1835365473;
    private static final int TYPE_sbtl = 1935832172;
    private static final int TYPE_soun = 1936684398;
    private static final int TYPE_subt = 1937072756;
    private static final int TYPE_text = 1952807028;
    private static final int TYPE_vide = 1986618469;
    private static final byte[] opusMagic = Util.getUtf8Bytes("OpusHead");

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public interface SampleSizeBox {
        int getSampleCount();

        boolean isFixedSampleSize();

        int readNextSampleSize();
    }

    public static Track parseTrak(Atom.ContainerAtom trak, Atom.LeafAtom mvhd, long duration, DrmInitData drmInitData, boolean ignoreEditLists, boolean isQuickTime) throws ParserException {
        long duration2;
        long durationUs;
        long[] editListMediaTimes;
        long[] editListDurations;
        Atom.ContainerAtom mdia = trak.getContainerAtomOfType(Atom.TYPE_mdia);
        int trackType = getTrackTypeForHdlr(parseHdlr(mdia.getLeafAtomOfType(Atom.TYPE_hdlr).data));
        if (trackType != -1) {
            TkhdData tkhdData = parseTkhd(trak.getLeafAtomOfType(Atom.TYPE_tkhd).data);
            if (duration != C.TIME_UNSET) {
                duration2 = duration;
            } else {
                duration2 = tkhdData.duration;
            }
            long movieTimescale = parseMvhd(mvhd.data);
            if (duration2 == C.TIME_UNSET) {
                durationUs = -9223372036854775807L;
            } else {
                durationUs = Util.scaleLargeTimestamp(duration2, 1000000L, movieTimescale);
            }
            Atom.ContainerAtom stbl = mdia.getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
            Pair<Long, String> mdhdData = parseMdhd(mdia.getLeafAtomOfType(Atom.TYPE_mdhd).data);
            StsdData stsdData = parseStsd(stbl.getLeafAtomOfType(Atom.TYPE_stsd).data, tkhdData.id, tkhdData.rotationDegrees, (String) mdhdData.second, drmInitData, isQuickTime);
            if (!ignoreEditLists) {
                Pair<long[], long[]> edtsData = parseEdts(trak.getContainerAtomOfType(Atom.TYPE_edts));
                long[] editListDurations2 = (long[]) edtsData.first;
                long[] editListMediaTimes2 = (long[]) edtsData.second;
                editListDurations = editListDurations2;
                editListMediaTimes = editListMediaTimes2;
            } else {
                editListDurations = null;
                editListMediaTimes = null;
            }
            if (stsdData.format == null) {
                return null;
            }
            return new Track(tkhdData.id, trackType, ((Long) mdhdData.first).longValue(), movieTimescale, durationUs, stsdData.format, stsdData.requiredSampleTransformation, stsdData.trackEncryptionBoxes, stsdData.nalUnitLengthFieldLength, editListDurations, editListMediaTimes);
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x0296  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0305  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x0326  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0106  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.google.android.exoplayer2.extractor.mp4.TrackSampleTable parseStbl(com.google.android.exoplayer2.extractor.mp4.Track r71, com.google.android.exoplayer2.extractor.mp4.Atom.ContainerAtom r72, com.google.android.exoplayer2.extractor.GaplessInfoHolder r73) throws com.google.android.exoplayer2.ParserException {
        /*
            Method dump skipped, instructions count: 1503
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.AtomParsers.parseStbl(com.google.android.exoplayer2.extractor.mp4.Track, com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom, com.google.android.exoplayer2.extractor.GaplessInfoHolder):com.google.android.exoplayer2.extractor.mp4.TrackSampleTable");
    }

    public static Metadata parseUdta(Atom.LeafAtom udtaAtom, boolean isQuickTime) {
        if (isQuickTime) {
            return null;
        }
        ParsableByteArray udtaData = udtaAtom.data;
        udtaData.setPosition(8);
        while (udtaData.bytesLeft() >= 8) {
            int atomPosition = udtaData.getPosition();
            int atomSize = udtaData.readInt();
            int atomType = udtaData.readInt();
            if (atomType == 1835365473) {
                udtaData.setPosition(atomPosition);
                return parseUdtaMeta(udtaData, atomPosition + atomSize);
            }
            udtaData.setPosition(atomPosition + atomSize);
        }
        return null;
    }

    public static Metadata parseMdtaFromMeta(Atom.ContainerAtom meta) {
        Atom.LeafAtom hdlrAtom = meta.getLeafAtomOfType(Atom.TYPE_hdlr);
        Atom.LeafAtom keysAtom = meta.getLeafAtomOfType(Atom.TYPE_keys);
        Atom.LeafAtom ilstAtom = meta.getLeafAtomOfType(Atom.TYPE_ilst);
        if (hdlrAtom == null || keysAtom == null || ilstAtom == null || parseHdlr(hdlrAtom.data) != TYPE_mdta) {
            return null;
        }
        ParsableByteArray keys = keysAtom.data;
        keys.setPosition(12);
        int entryCount = keys.readInt();
        String[] keyNames = new String[entryCount];
        for (int i = 0; i < entryCount; i++) {
            int entrySize = keys.readInt();
            keys.skipBytes(4);
            int keySize = entrySize - 8;
            keyNames[i] = keys.readString(keySize);
        }
        ParsableByteArray ilst = ilstAtom.data;
        ilst.setPosition(8);
        ArrayList<Metadata.Entry> entries = new ArrayList<>();
        while (ilst.bytesLeft() > 8) {
            int atomPosition = ilst.getPosition();
            int atomSize = ilst.readInt();
            int keyIndex = ilst.readInt() - 1;
            if (keyIndex >= 0 && keyIndex < keyNames.length) {
                String key = keyNames[keyIndex];
                Metadata.Entry entry = MetadataUtil.parseMdtaMetadataEntryFromIlst(ilst, atomPosition + atomSize, key);
                if (entry != null) {
                    entries.add(entry);
                }
            } else {
                Log.w(TAG, "Skipped metadata with unknown key index: " + keyIndex);
            }
            ilst.setPosition(atomPosition + atomSize);
        }
        if (!entries.isEmpty()) {
            return new Metadata(entries);
        }
        return null;
    }

    private static Metadata parseUdtaMeta(ParsableByteArray meta, int limit) {
        meta.skipBytes(12);
        while (meta.getPosition() < limit) {
            int atomPosition = meta.getPosition();
            int atomSize = meta.readInt();
            int atomType = meta.readInt();
            if (atomType == 1768715124) {
                meta.setPosition(atomPosition);
                return parseIlst(meta, atomPosition + atomSize);
            }
            meta.setPosition(atomPosition + atomSize);
        }
        return null;
    }

    private static Metadata parseIlst(ParsableByteArray ilst, int limit) {
        ilst.skipBytes(8);
        ArrayList<Metadata.Entry> entries = new ArrayList<>();
        while (ilst.getPosition() < limit) {
            Metadata.Entry entry = MetadataUtil.parseIlstElement(ilst);
            if (entry != null) {
                entries.add(entry);
            }
        }
        if (entries.isEmpty()) {
            return null;
        }
        return new Metadata(entries);
    }

    private static long parseMvhd(ParsableByteArray mvhd) {
        int i = 8;
        mvhd.setPosition(8);
        int fullAtom = mvhd.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        if (version != 0) {
            i = 16;
        }
        mvhd.skipBytes(i);
        return mvhd.readUnsignedInt();
    }

    private static TkhdData parseTkhd(ParsableByteArray tkhd) {
        long duration;
        int rotationDegrees;
        int durationByteCount = 8;
        tkhd.setPosition(8);
        int fullAtom = tkhd.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        tkhd.skipBytes(version == 0 ? 8 : 16);
        int trackId = tkhd.readInt();
        tkhd.skipBytes(4);
        boolean durationUnknown = true;
        int durationPosition = tkhd.getPosition();
        if (version == 0) {
            durationByteCount = 4;
        }
        int i = 0;
        while (true) {
            if (i >= durationByteCount) {
                break;
            } else if (tkhd.data[durationPosition + i] == -1) {
                i++;
            } else {
                durationUnknown = false;
                break;
            }
        }
        if (durationUnknown) {
            tkhd.skipBytes(durationByteCount);
            duration = C.TIME_UNSET;
        } else {
            duration = version == 0 ? tkhd.readUnsignedInt() : tkhd.readUnsignedLongToLong();
            if (duration == 0) {
                duration = C.TIME_UNSET;
            }
        }
        tkhd.skipBytes(16);
        int a00 = tkhd.readInt();
        int a01 = tkhd.readInt();
        tkhd.skipBytes(4);
        int a10 = tkhd.readInt();
        int a11 = tkhd.readInt();
        if (a00 == 0 && a01 == 65536 && a10 == (-65536) && a11 == 0) {
            rotationDegrees = 90;
        } else if (a00 == 0 && a01 == (-65536) && a10 == 65536 && a11 == 0) {
            rotationDegrees = 270;
        } else {
            int rotationDegrees2 = -65536;
            if (a00 == rotationDegrees2 && a01 == 0 && a10 == 0 && a11 == (-65536)) {
                rotationDegrees = 180;
            } else {
                rotationDegrees = 0;
            }
        }
        return new TkhdData(trackId, duration, rotationDegrees);
    }

    private static int parseHdlr(ParsableByteArray hdlr) {
        hdlr.setPosition(16);
        return hdlr.readInt();
    }

    private static int getTrackTypeForHdlr(int hdlr) {
        if (hdlr == TYPE_soun) {
            return 1;
        }
        if (hdlr == TYPE_vide) {
            return 2;
        }
        if (hdlr == TYPE_text || hdlr == TYPE_sbtl || hdlr == TYPE_subt || hdlr == TYPE_clcp) {
            return 3;
        }
        if (hdlr == 1835365473) {
            return 4;
        }
        return -1;
    }

    private static Pair<Long, String> parseMdhd(ParsableByteArray mdhd) {
        int i = 8;
        mdhd.setPosition(8);
        int fullAtom = mdhd.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        mdhd.skipBytes(version == 0 ? 8 : 16);
        long timescale = mdhd.readUnsignedInt();
        if (version == 0) {
            i = 4;
        }
        mdhd.skipBytes(i);
        int languageCode = mdhd.readUnsignedShort();
        String language = "" + ((char) (((languageCode >> 10) & 31) + 96)) + ((char) (((languageCode >> 5) & 31) + 96)) + ((char) ((languageCode & 31) + 96));
        return Pair.create(Long.valueOf(timescale), language);
    }

    private static StsdData parseStsd(ParsableByteArray stsd, int trackId, int rotationDegrees, String language, DrmInitData drmInitData, boolean isQuickTime) throws ParserException {
        int childAtomType;
        stsd.setPosition(12);
        int numberOfEntries = stsd.readInt();
        StsdData out = new StsdData(numberOfEntries);
        for (int i = 0; i < numberOfEntries; i++) {
            int childStartPosition = stsd.getPosition();
            int childAtomSize = stsd.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType2 = stsd.readInt();
            if (childAtomType2 == 1635148593 || childAtomType2 == 1635148595 || childAtomType2 == 1701733238 || childAtomType2 == 1836070006 || childAtomType2 == 1752589105 || childAtomType2 == 1751479857 || childAtomType2 == 1932670515 || childAtomType2 == 1987063864 || childAtomType2 == 1987063865 || childAtomType2 == 1635135537 || childAtomType2 == 1685479798 || childAtomType2 == 1685479729 || childAtomType2 == 1685481573) {
                childAtomType = childAtomType2;
            } else if (childAtomType2 == 1685481521) {
                childAtomType = childAtomType2;
            } else {
                if (childAtomType2 == 1836069985 || childAtomType2 == 1701733217 || childAtomType2 == 1633889587 || childAtomType2 == 1700998451 || childAtomType2 == 1633889588 || childAtomType2 == 1685353315 || childAtomType2 == 1685353317 || childAtomType2 == 1685353320 || childAtomType2 == 1685353324 || childAtomType2 == 1935764850 || childAtomType2 == 1935767394 || childAtomType2 == 1819304813 || childAtomType2 == 1936684916 || childAtomType2 == 1953984371 || childAtomType2 == 778924083 || childAtomType2 == 1634492771 || childAtomType2 == 1634492791 || childAtomType2 == 1970037111 || childAtomType2 == 1332770163 || childAtomType2 == 1716281667) {
                    parseAudioSampleEntry(stsd, childAtomType2, childStartPosition, childAtomSize, trackId, language, isQuickTime, drmInitData, out, i);
                } else if (childAtomType2 == 1414810956 || childAtomType2 == 1954034535 || childAtomType2 == 2004251764 || childAtomType2 == 1937010800 || childAtomType2 == 1664495672) {
                    parseTextSampleEntry(stsd, childAtomType2, childStartPosition, childAtomSize, trackId, language, out);
                } else if (childAtomType2 == 1667329389) {
                    out.format = Format.createSampleFormat(Integer.toString(trackId), MimeTypes.APPLICATION_CAMERA_MOTION, null, -1, null);
                }
                stsd.setPosition(childStartPosition + childAtomSize);
            }
            parseVideoSampleEntry(stsd, childAtomType, childStartPosition, childAtomSize, trackId, rotationDegrees, drmInitData, out, i);
            stsd.setPosition(childStartPosition + childAtomSize);
        }
        return out;
    }

    private static void parseTextSampleEntry(ParsableByteArray parent, int atomType, int position, int atomSize, int trackId, String language, StsdData out) throws ParserException {
        String mimeType;
        parent.setPosition(position + 8 + 8);
        List<byte[]> initializationData = null;
        long subsampleOffsetUs = Long.MAX_VALUE;
        if (atomType == 1414810956) {
            mimeType = MimeTypes.APPLICATION_TTML;
        } else if (atomType == 1954034535) {
            mimeType = MimeTypes.APPLICATION_TX3G;
            int sampleDescriptionLength = (atomSize - 8) - 8;
            byte[] sampleDescriptionData = new byte[sampleDescriptionLength];
            parent.readBytes(sampleDescriptionData, 0, sampleDescriptionLength);
            initializationData = Collections.singletonList(sampleDescriptionData);
        } else if (atomType == 2004251764) {
            mimeType = MimeTypes.APPLICATION_MP4VTT;
        } else if (atomType == 1937010800) {
            mimeType = MimeTypes.APPLICATION_TTML;
            subsampleOffsetUs = 0;
        } else if (atomType == 1664495672) {
            mimeType = MimeTypes.APPLICATION_MP4CEA608;
            out.requiredSampleTransformation = 1;
        } else {
            throw new IllegalStateException();
        }
        out.format = Format.createTextSampleFormat(Integer.toString(trackId), mimeType, null, -1, 0, language, -1, null, subsampleOffsetUs, initializationData);
    }

    /* JADX WARN: Removed duplicated region for block: B:86:0x01aa A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:87:0x01ab  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void parseVideoSampleEntry(com.google.android.exoplayer2.util.ParsableByteArray r31, int r32, int r33, int r34, int r35, int r36, com.google.android.exoplayer2.drm.DrmInitData r37, com.google.android.exoplayer2.extractor.mp4.AtomParsers.StsdData r38, int r39) throws com.google.android.exoplayer2.ParserException {
        /*
            Method dump skipped, instructions count: 478
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.AtomParsers.parseVideoSampleEntry(com.google.android.exoplayer2.util.ParsableByteArray, int, int, int, int, int, com.google.android.exoplayer2.drm.DrmInitData, com.google.android.exoplayer2.extractor.mp4.AtomParsers$StsdData, int):void");
    }

    private static Pair<long[], long[]> parseEdts(Atom.ContainerAtom edtsAtom) {
        Atom.LeafAtom elst;
        if (edtsAtom == null || (elst = edtsAtom.getLeafAtomOfType(Atom.TYPE_elst)) == null) {
            return Pair.create(null, null);
        }
        ParsableByteArray elstData = elst.data;
        elstData.setPosition(8);
        int fullAtom = elstData.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        int entryCount = elstData.readUnsignedIntToInt();
        long[] editListDurations = new long[entryCount];
        long[] editListMediaTimes = new long[entryCount];
        for (int i = 0; i < entryCount; i++) {
            editListDurations[i] = version == 1 ? elstData.readUnsignedLongToLong() : elstData.readUnsignedInt();
            editListMediaTimes[i] = version == 1 ? elstData.readLong() : elstData.readInt();
            int mediaRateInteger = elstData.readShort();
            if (mediaRateInteger != 1) {
                throw new IllegalArgumentException("Unsupported media rate.");
            }
            elstData.skipBytes(2);
        }
        return Pair.create(editListDurations, editListMediaTimes);
    }

    private static float parsePaspFromParent(ParsableByteArray parent, int position) {
        parent.setPosition(position + 8);
        int hSpacing = parent.readUnsignedIntToInt();
        int vSpacing = parent.readUnsignedIntToInt();
        return hSpacing / vSpacing;
    }

    private static void parseAudioSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, String language, boolean isQuickTime, DrmInitData drmInitData, StsdData out, int entryIndex) throws ParserException {
        int quickTimeSoundDescriptionVersion;
        int sampleRate;
        int channelCount;
        int atomType2;
        DrmInitData drmInitData2;
        int pcmEncoding;
        int quickTimeSoundDescriptionVersion2;
        DrmInitData drmInitData3;
        int atomType3;
        int quickTimeSoundDescriptionVersion3;
        int childAtomType;
        int childAtomType2;
        DrmInitData drmInitData4 = drmInitData;
        parent.setPosition(position + 8 + 8);
        if (isQuickTime) {
            int quickTimeSoundDescriptionVersion4 = parent.readUnsignedShort();
            parent.skipBytes(6);
            quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion4;
        } else {
            parent.skipBytes(8);
            quickTimeSoundDescriptionVersion = 0;
        }
        if (quickTimeSoundDescriptionVersion == 0 || quickTimeSoundDescriptionVersion == 1) {
            int channelCount2 = parent.readUnsignedShort();
            parent.skipBytes(6);
            sampleRate = parent.readUnsignedFixedPoint1616();
            if (quickTimeSoundDescriptionVersion == 1) {
                parent.skipBytes(16);
            }
            channelCount = channelCount2;
        } else if (quickTimeSoundDescriptionVersion == 2) {
            parent.skipBytes(16);
            sampleRate = (int) Math.round(parent.readDouble());
            channelCount = parent.readUnsignedIntToInt();
            parent.skipBytes(20);
        } else {
            return;
        }
        int childPosition = parent.getPosition();
        int atomType4 = atomType;
        if (atomType4 != 1701733217) {
            drmInitData2 = drmInitData4;
            atomType2 = atomType4;
        } else {
            Pair<Integer, TrackEncryptionBox> sampleEntryEncryptionData = parseSampleEntryEncryptionData(parent, position, size);
            if (sampleEntryEncryptionData != null) {
                atomType4 = ((Integer) sampleEntryEncryptionData.first).intValue();
                drmInitData4 = drmInitData4 == null ? null : drmInitData4.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                out.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
            }
            parent.setPosition(childPosition);
            drmInitData2 = drmInitData4;
            atomType2 = atomType4;
        }
        String mimeType = null;
        if (atomType2 == 1633889587) {
            mimeType = MimeTypes.AUDIO_AC3;
            pcmEncoding = -1;
        } else if (atomType2 == 1700998451) {
            mimeType = MimeTypes.AUDIO_E_AC3;
            pcmEncoding = -1;
        } else if (atomType2 == 1633889588) {
            mimeType = MimeTypes.AUDIO_AC4;
            pcmEncoding = -1;
        } else if (atomType2 == 1685353315) {
            mimeType = MimeTypes.AUDIO_DTS;
            pcmEncoding = -1;
        } else if (atomType2 == 1685353320 || atomType2 == 1685353324) {
            mimeType = MimeTypes.AUDIO_DTS_HD;
            pcmEncoding = -1;
        } else if (atomType2 == 1685353317) {
            mimeType = MimeTypes.AUDIO_DTS_EXPRESS;
            pcmEncoding = -1;
        } else if (atomType2 == 1935764850) {
            mimeType = MimeTypes.AUDIO_AMR_NB;
            pcmEncoding = -1;
        } else if (atomType2 == 1935767394) {
            mimeType = MimeTypes.AUDIO_AMR_WB;
            pcmEncoding = -1;
        } else if (atomType2 == 1819304813 || atomType2 == 1936684916) {
            mimeType = MimeTypes.AUDIO_RAW;
            pcmEncoding = 2;
        } else if (atomType2 == 1953984371) {
            mimeType = MimeTypes.AUDIO_RAW;
            pcmEncoding = 268435456;
        } else if (atomType2 == 778924083) {
            mimeType = MimeTypes.AUDIO_MPEG;
            pcmEncoding = -1;
        } else if (atomType2 == 1634492771) {
            mimeType = MimeTypes.AUDIO_ALAC;
            pcmEncoding = -1;
        } else if (atomType2 == 1634492791) {
            mimeType = MimeTypes.AUDIO_ALAW;
            pcmEncoding = -1;
        } else if (atomType2 == 1970037111) {
            mimeType = MimeTypes.AUDIO_MLAW;
            pcmEncoding = -1;
        } else if (atomType2 == 1332770163) {
            mimeType = MimeTypes.AUDIO_OPUS;
            pcmEncoding = -1;
        } else if (atomType2 != 1716281667) {
            pcmEncoding = -1;
        } else {
            mimeType = MimeTypes.AUDIO_FLAC;
            pcmEncoding = -1;
        }
        String mimeType2 = mimeType;
        byte[] initializationData = null;
        int channelCount3 = channelCount;
        int sampleRate2 = sampleRate;
        int childPosition2 = childPosition;
        while (childPosition2 - position < size) {
            parent.setPosition(childPosition2);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType3 = parent.readInt();
            if (childAtomType3 == 1702061171) {
                childAtomType2 = childAtomType3;
                childAtomType = childAtomSize;
                drmInitData3 = drmInitData2;
                atomType3 = atomType2;
                quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion;
                quickTimeSoundDescriptionVersion3 = childPosition2;
            } else if (!isQuickTime || childAtomType3 != 2002876005) {
                if (childAtomType3 == 1684103987) {
                    parent.setPosition(childPosition2 + 8);
                    out.format = Ac3Util.parseAc3AnnexFFormat(parent, Integer.toString(trackId), language, drmInitData2);
                    childAtomType = childAtomSize;
                    drmInitData3 = drmInitData2;
                    atomType3 = atomType2;
                    quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion;
                    quickTimeSoundDescriptionVersion3 = childPosition2;
                } else if (childAtomType3 == 1684366131) {
                    parent.setPosition(childPosition2 + 8);
                    out.format = Ac3Util.parseEAc3AnnexFFormat(parent, Integer.toString(trackId), language, drmInitData2);
                    childAtomType = childAtomSize;
                    drmInitData3 = drmInitData2;
                    atomType3 = atomType2;
                    quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion;
                    quickTimeSoundDescriptionVersion3 = childPosition2;
                } else if (childAtomType3 == 1684103988) {
                    parent.setPosition(childPosition2 + 8);
                    out.format = Ac4Util.parseAc4AnnexEFormat(parent, Integer.toString(trackId), language, drmInitData2);
                    childAtomType = childAtomSize;
                    drmInitData3 = drmInitData2;
                    atomType3 = atomType2;
                    quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion;
                    quickTimeSoundDescriptionVersion3 = childPosition2;
                } else if (childAtomType3 == 1684305011) {
                    drmInitData3 = drmInitData2;
                    atomType3 = atomType2;
                    quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion;
                    out.format = Format.createAudioSampleFormat(Integer.toString(trackId), mimeType2, null, -1, -1, channelCount3, sampleRate2, null, drmInitData3, 0, language);
                    childAtomType = childAtomSize;
                    quickTimeSoundDescriptionVersion3 = childPosition2;
                } else {
                    int childPosition3 = childPosition2;
                    drmInitData3 = drmInitData2;
                    atomType3 = atomType2;
                    quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion;
                    if (childAtomType3 != 1682927731) {
                        childAtomType = childAtomSize;
                        quickTimeSoundDescriptionVersion3 = childPosition3;
                        if (childAtomType3 != 1684425825) {
                            if (childAtomType3 == 1634492771) {
                                int childAtomBodySize = childAtomType - 12;
                                byte[] initializationData2 = new byte[childAtomBodySize];
                                parent.setPosition(quickTimeSoundDescriptionVersion3 + 12);
                                parent.readBytes(initializationData2, 0, childAtomBodySize);
                                Pair<Integer, Integer> audioSpecificConfig = CodecSpecificDataUtil.parseAlacAudioSpecificConfig(initializationData2);
                                int sampleRate3 = ((Integer) audioSpecificConfig.first).intValue();
                                initializationData = initializationData2;
                                sampleRate2 = sampleRate3;
                                channelCount3 = ((Integer) audioSpecificConfig.second).intValue();
                            }
                        } else {
                            int childAtomBodySize2 = childAtomType - 12;
                            byte[] initializationData3 = new byte[childAtomBodySize2 + 4];
                            initializationData3[0] = 102;
                            initializationData3[1] = 76;
                            initializationData3[2] = 97;
                            initializationData3[3] = 67;
                            parent.setPosition(quickTimeSoundDescriptionVersion3 + 12);
                            parent.readBytes(initializationData3, 4, childAtomBodySize2);
                            initializationData = initializationData3;
                        }
                    } else {
                        childAtomType = childAtomSize;
                        int childAtomBodySize3 = childAtomType - 8;
                        byte[] bArr = opusMagic;
                        byte[] initializationData4 = new byte[bArr.length + childAtomBodySize3];
                        System.arraycopy(bArr, 0, initializationData4, 0, bArr.length);
                        quickTimeSoundDescriptionVersion3 = childPosition3;
                        parent.setPosition(quickTimeSoundDescriptionVersion3 + 8);
                        parent.readBytes(initializationData4, bArr.length, childAtomBodySize3);
                        initializationData = initializationData4;
                    }
                }
                childPosition2 = quickTimeSoundDescriptionVersion3 + childAtomType;
                atomType2 = atomType3;
                drmInitData2 = drmInitData3;
                quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion2;
            } else {
                childAtomType2 = childAtomType3;
                childAtomType = childAtomSize;
                drmInitData3 = drmInitData2;
                atomType3 = atomType2;
                quickTimeSoundDescriptionVersion2 = quickTimeSoundDescriptionVersion;
                quickTimeSoundDescriptionVersion3 = childPosition2;
            }
            int esdsAtomPosition = childAtomType2 == 1702061171 ? quickTimeSoundDescriptionVersion3 : findEsdsPosition(parent, quickTimeSoundDescriptionVersion3, childAtomType);
            if (esdsAtomPosition != -1) {
                Pair<String, byte[]> mimeTypeAndInitializationData = parseEsdsFromParent(parent, esdsAtomPosition);
                String mimeType3 = (String) mimeTypeAndInitializationData.first;
                initializationData = (byte[]) mimeTypeAndInitializationData.second;
                if (!"audio/mp4a-latm".equals(mimeType3)) {
                    mimeType2 = mimeType3;
                } else {
                    Pair<Integer, Integer> audioSpecificConfig2 = CodecSpecificDataUtil.parseAacAudioSpecificConfig(initializationData);
                    sampleRate2 = ((Integer) audioSpecificConfig2.first).intValue();
                    channelCount3 = ((Integer) audioSpecificConfig2.second).intValue();
                    mimeType2 = mimeType3;
                }
            }
            childPosition2 = quickTimeSoundDescriptionVersion3 + childAtomType;
            atomType2 = atomType3;
            drmInitData2 = drmInitData3;
            quickTimeSoundDescriptionVersion = quickTimeSoundDescriptionVersion2;
        }
        DrmInitData drmInitData5 = drmInitData2;
        if (out.format == null && mimeType2 != null) {
            out.format = Format.createAudioSampleFormat(Integer.toString(trackId), mimeType2, null, -1, -1, channelCount3, sampleRate2, pcmEncoding, initializationData == null ? null : Collections.singletonList(initializationData), drmInitData5, 0, language);
        }
    }

    private static int findEsdsPosition(ParsableByteArray parent, int position, int size) {
        int childAtomPosition = parent.getPosition();
        while (childAtomPosition - position < size) {
            parent.setPosition(childAtomPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childType = parent.readInt();
            if (childType == 1702061171) {
                return childAtomPosition;
            }
            childAtomPosition += childAtomSize;
        }
        return -1;
    }

    private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray parent, int position) {
        parent.setPosition(position + 8 + 4);
        parent.skipBytes(1);
        parseExpandableClassSize(parent);
        parent.skipBytes(2);
        int flags = parent.readUnsignedByte();
        if ((flags & 128) != 0) {
            parent.skipBytes(2);
        }
        if ((flags & 64) != 0) {
            parent.skipBytes(parent.readUnsignedShort());
        }
        if ((flags & 32) != 0) {
            parent.skipBytes(2);
        }
        parent.skipBytes(1);
        parseExpandableClassSize(parent);
        int objectTypeIndication = parent.readUnsignedByte();
        String mimeType = MimeTypes.getMimeTypeFromMp4ObjectType(objectTypeIndication);
        if (MimeTypes.AUDIO_MPEG.equals(mimeType) || MimeTypes.AUDIO_DTS.equals(mimeType) || MimeTypes.AUDIO_DTS_HD.equals(mimeType)) {
            return Pair.create(mimeType, null);
        }
        parent.skipBytes(12);
        parent.skipBytes(1);
        int initializationDataSize = parseExpandableClassSize(parent);
        byte[] initializationData = new byte[initializationDataSize];
        parent.readBytes(initializationData, 0, initializationDataSize);
        return Pair.create(mimeType, initializationData);
    }

    private static Pair<Integer, TrackEncryptionBox> parseSampleEntryEncryptionData(ParsableByteArray parent, int position, int size) {
        Pair<Integer, TrackEncryptionBox> result;
        int childPosition = parent.getPosition();
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType = parent.readInt();
            if (childAtomType == 1936289382 && (result = parseCommonEncryptionSinfFromParent(parent, childPosition, childAtomSize)) != null) {
                return result;
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    static Pair<Integer, TrackEncryptionBox> parseCommonEncryptionSinfFromParent(ParsableByteArray parent, int position, int size) {
        int childPosition = position + 8;
        int schemeInformationBoxPosition = -1;
        int schemeInformationBoxSize = 0;
        String schemeType = null;
        Integer dataFormat = null;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            int childAtomType = parent.readInt();
            if (childAtomType == 1718775137) {
                dataFormat = Integer.valueOf(parent.readInt());
            } else if (childAtomType == 1935894637) {
                parent.skipBytes(4);
                schemeType = parent.readString(4);
            } else if (childAtomType == 1935894633) {
                schemeInformationBoxPosition = childPosition;
                schemeInformationBoxSize = childAtomSize;
            }
            childPosition += childAtomSize;
        }
        if (C.CENC_TYPE_cenc.equals(schemeType) || C.CENC_TYPE_cbc1.equals(schemeType) || C.CENC_TYPE_cens.equals(schemeType) || C.CENC_TYPE_cbcs.equals(schemeType)) {
            boolean z = true;
            Assertions.checkArgument(dataFormat != null, "frma atom is mandatory");
            Assertions.checkArgument(schemeInformationBoxPosition != -1, "schi atom is mandatory");
            TrackEncryptionBox encryptionBox = parseSchiFromParent(parent, schemeInformationBoxPosition, schemeInformationBoxSize, schemeType);
            if (encryptionBox == null) {
                z = false;
            }
            Assertions.checkArgument(z, "tenc atom is mandatory");
            return Pair.create(dataFormat, encryptionBox);
        }
        return null;
    }

    private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray parent, int position, int size, String schemeType) {
        byte[] constantIv;
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            int childAtomType = parent.readInt();
            if (childAtomType == 1952804451) {
                int fullAtom = parent.readInt();
                int version = Atom.parseFullAtomVersion(fullAtom);
                boolean defaultIsProtected = true;
                parent.skipBytes(1);
                int defaultCryptByteBlock = 0;
                int defaultSkipByteBlock = 0;
                if (version == 0) {
                    parent.skipBytes(1);
                } else {
                    int patternByte = parent.readUnsignedByte();
                    defaultCryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
                    defaultSkipByteBlock = patternByte & 15;
                }
                if (parent.readUnsignedByte() != 1) {
                    defaultIsProtected = false;
                }
                int defaultPerSampleIvSize = parent.readUnsignedByte();
                byte[] defaultKeyId = new byte[16];
                parent.readBytes(defaultKeyId, 0, defaultKeyId.length);
                if (defaultIsProtected && defaultPerSampleIvSize == 0) {
                    int constantIvSize = parent.readUnsignedByte();
                    byte[] constantIv2 = new byte[constantIvSize];
                    parent.readBytes(constantIv2, 0, constantIvSize);
                    constantIv = constantIv2;
                } else {
                    constantIv = null;
                }
                return new TrackEncryptionBox(defaultIsProtected, schemeType, defaultPerSampleIvSize, defaultKeyId, defaultCryptByteBlock, defaultSkipByteBlock, constantIv);
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    private static byte[] parseProjFromParent(ParsableByteArray parent, int position, int size) {
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            int childAtomType = parent.readInt();
            if (childAtomType == 1886547818) {
                return Arrays.copyOfRange(parent.data, childPosition, childPosition + childAtomSize);
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    private static int parseExpandableClassSize(ParsableByteArray data) {
        int currentByte = data.readUnsignedByte();
        int size = currentByte & 127;
        while ((currentByte & 128) == 128) {
            currentByte = data.readUnsignedByte();
            size = (size << 7) | (currentByte & 127);
        }
        return size;
    }

    private static boolean canApplyEditWithGaplessInfo(long[] timestamps, long duration, long editStartTime, long editEndTime) {
        int lastIndex = timestamps.length - 1;
        int latestDelayIndex = Util.constrainValue(4, 0, lastIndex);
        int earliestPaddingIndex = Util.constrainValue(timestamps.length - 4, 0, lastIndex);
        return timestamps[0] <= editStartTime && editStartTime < timestamps[latestDelayIndex] && timestamps[earliestPaddingIndex] < editEndTime && editEndTime <= duration;
    }

    private AtomParsers() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class ChunkIterator {
        private final ParsableByteArray chunkOffsets;
        private final boolean chunkOffsetsAreLongs;
        public int index;
        public final int length;
        private int nextSamplesPerChunkChangeIndex;
        public int numSamples;
        public long offset;
        private int remainingSamplesPerChunkChanges;
        private final ParsableByteArray stsc;

        public ChunkIterator(ParsableByteArray stsc, ParsableByteArray chunkOffsets, boolean chunkOffsetsAreLongs) {
            this.stsc = stsc;
            this.chunkOffsets = chunkOffsets;
            this.chunkOffsetsAreLongs = chunkOffsetsAreLongs;
            chunkOffsets.setPosition(12);
            this.length = chunkOffsets.readUnsignedIntToInt();
            stsc.setPosition(12);
            this.remainingSamplesPerChunkChanges = stsc.readUnsignedIntToInt();
            Assertions.checkState(stsc.readInt() != 1 ? false : true, "first_chunk must be 1");
            this.index = -1;
        }

        public boolean moveNext() {
            int i = this.index + 1;
            this.index = i;
            if (i == this.length) {
                return false;
            }
            this.offset = this.chunkOffsetsAreLongs ? this.chunkOffsets.readUnsignedLongToLong() : this.chunkOffsets.readUnsignedInt();
            if (this.index == this.nextSamplesPerChunkChangeIndex) {
                this.numSamples = this.stsc.readUnsignedIntToInt();
                this.stsc.skipBytes(4);
                int i2 = this.remainingSamplesPerChunkChanges - 1;
                this.remainingSamplesPerChunkChanges = i2;
                this.nextSamplesPerChunkChangeIndex = i2 > 0 ? this.stsc.readUnsignedIntToInt() - 1 : -1;
            }
            return true;
        }
    }

    /* loaded from: classes3.dex */
    public static final class TkhdData {
        private final long duration;
        private final int id;
        private final int rotationDegrees;

        public TkhdData(int id, long duration, int rotationDegrees) {
            this.id = id;
            this.duration = duration;
            this.rotationDegrees = rotationDegrees;
        }
    }

    /* loaded from: classes3.dex */
    public static final class StsdData {
        public static final int STSD_HEADER_SIZE = 8;
        public Format format;
        public int nalUnitLengthFieldLength;
        public int requiredSampleTransformation = 0;
        public final TrackEncryptionBox[] trackEncryptionBoxes;

        public StsdData(int numberOfEntries) {
            this.trackEncryptionBoxes = new TrackEncryptionBox[numberOfEntries];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class StszSampleSizeBox implements SampleSizeBox {
        private final ParsableByteArray data;
        private final int fixedSampleSize;
        private final int sampleCount;

        public StszSampleSizeBox(Atom.LeafAtom stszAtom) {
            ParsableByteArray parsableByteArray = stszAtom.data;
            this.data = parsableByteArray;
            parsableByteArray.setPosition(12);
            this.fixedSampleSize = parsableByteArray.readUnsignedIntToInt();
            this.sampleCount = parsableByteArray.readUnsignedIntToInt();
        }

        @Override // com.google.android.exoplayer2.extractor.mp4.AtomParsers.SampleSizeBox
        public int getSampleCount() {
            return this.sampleCount;
        }

        @Override // com.google.android.exoplayer2.extractor.mp4.AtomParsers.SampleSizeBox
        public int readNextSampleSize() {
            int i = this.fixedSampleSize;
            return i == 0 ? this.data.readUnsignedIntToInt() : i;
        }

        @Override // com.google.android.exoplayer2.extractor.mp4.AtomParsers.SampleSizeBox
        public boolean isFixedSampleSize() {
            return this.fixedSampleSize != 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class Stz2SampleSizeBox implements SampleSizeBox {
        private int currentByte;
        private final ParsableByteArray data;
        private final int fieldSize;
        private final int sampleCount;
        private int sampleIndex;

        public Stz2SampleSizeBox(Atom.LeafAtom stz2Atom) {
            ParsableByteArray parsableByteArray = stz2Atom.data;
            this.data = parsableByteArray;
            parsableByteArray.setPosition(12);
            this.fieldSize = parsableByteArray.readUnsignedIntToInt() & 255;
            this.sampleCount = parsableByteArray.readUnsignedIntToInt();
        }

        @Override // com.google.android.exoplayer2.extractor.mp4.AtomParsers.SampleSizeBox
        public int getSampleCount() {
            return this.sampleCount;
        }

        @Override // com.google.android.exoplayer2.extractor.mp4.AtomParsers.SampleSizeBox
        public int readNextSampleSize() {
            int i = this.fieldSize;
            if (i == 8) {
                return this.data.readUnsignedByte();
            }
            if (i == 16) {
                return this.data.readUnsignedShort();
            }
            int i2 = this.sampleIndex;
            this.sampleIndex = i2 + 1;
            if (i2 % 2 == 0) {
                int readUnsignedByte = this.data.readUnsignedByte();
                this.currentByte = readUnsignedByte;
                return (readUnsignedByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
            }
            return this.currentByte & 15;
        }

        @Override // com.google.android.exoplayer2.extractor.mp4.AtomParsers.SampleSizeBox
        public boolean isFixedSampleSize() {
            return false;
        }
    }
}
