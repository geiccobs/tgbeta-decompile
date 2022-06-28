package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.DTSSpecificBox;
import com.microsoft.appcenter.distribute.DistributeConstants;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.telegram.ui.GroupCallActivity;
/* loaded from: classes3.dex */
public class DTSTrackImpl extends AbstractTrack {
    private static final int BUFFER = 67108864;
    int bcCoreBitRate;
    int bcCoreChannelMask;
    int bcCoreMaxSampleRate;
    int bitrate;
    int channelCount;
    int channelMask;
    int codecDelayAtMaxFs;
    int coreBitRate;
    int coreChannelMask;
    int coreFramePayloadInBytes;
    int coreMaxSampleRate;
    boolean coreSubStreamPresent;
    private int dataOffset;
    private DataSource dataSource;
    DTSSpecificBox ddts;
    int extAvgBitrate;
    int extFramePayloadInBytes;
    int extPeakBitrate;
    int extSmoothBuffSize;
    boolean extensionSubStreamPresent;
    int frameSize;
    boolean isVBR;
    private String lang;
    int lbrCodingPresent;
    int lsbTrimPercent;
    int maxSampleRate;
    int numExtSubStreams;
    int numFramesTotal;
    int numSamplesOrigAudioAtMaxFs;
    SampleDescriptionBox sampleDescriptionBox;
    private long[] sampleDurations;
    int sampleSize;
    int samplerate;
    private List<Sample> samples;
    int samplesPerFrame;
    int samplesPerFrameAtMaxFs;
    TrackMetaData trackMetaData;
    String type;

    public DTSTrackImpl(DataSource dataSource, String lang) throws IOException {
        super(dataSource.toString());
        this.trackMetaData = new TrackMetaData();
        this.frameSize = 0;
        this.dataOffset = 0;
        this.ddts = new DTSSpecificBox();
        this.isVBR = false;
        this.coreSubStreamPresent = false;
        this.extensionSubStreamPresent = false;
        this.numExtSubStreams = 0;
        this.coreMaxSampleRate = 0;
        this.coreBitRate = 0;
        this.coreChannelMask = 0;
        this.coreFramePayloadInBytes = 0;
        this.extAvgBitrate = 0;
        this.extPeakBitrate = 0;
        this.extSmoothBuffSize = 0;
        this.extFramePayloadInBytes = 0;
        this.maxSampleRate = 0;
        this.lbrCodingPresent = 0;
        this.numFramesTotal = 0;
        this.samplesPerFrameAtMaxFs = 0;
        this.numSamplesOrigAudioAtMaxFs = 0;
        this.channelMask = 0;
        this.codecDelayAtMaxFs = 0;
        this.bcCoreMaxSampleRate = 0;
        this.bcCoreBitRate = 0;
        this.bcCoreChannelMask = 0;
        this.lsbTrimPercent = 0;
        this.type = "none";
        this.lang = "eng";
        this.lang = lang;
        this.dataSource = dataSource;
        parse();
    }

    public DTSTrackImpl(DataSource dataSource) throws IOException {
        super(dataSource.toString());
        this.trackMetaData = new TrackMetaData();
        this.frameSize = 0;
        this.dataOffset = 0;
        this.ddts = new DTSSpecificBox();
        this.isVBR = false;
        this.coreSubStreamPresent = false;
        this.extensionSubStreamPresent = false;
        this.numExtSubStreams = 0;
        this.coreMaxSampleRate = 0;
        this.coreBitRate = 0;
        this.coreChannelMask = 0;
        this.coreFramePayloadInBytes = 0;
        this.extAvgBitrate = 0;
        this.extPeakBitrate = 0;
        this.extSmoothBuffSize = 0;
        this.extFramePayloadInBytes = 0;
        this.maxSampleRate = 0;
        this.lbrCodingPresent = 0;
        this.numFramesTotal = 0;
        this.samplesPerFrameAtMaxFs = 0;
        this.numSamplesOrigAudioAtMaxFs = 0;
        this.channelMask = 0;
        this.codecDelayAtMaxFs = 0;
        this.bcCoreMaxSampleRate = 0;
        this.bcCoreBitRate = 0;
        this.bcCoreChannelMask = 0;
        this.lsbTrimPercent = 0;
        this.type = "none";
        this.lang = "eng";
        this.dataSource = dataSource;
        parse();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.dataSource.close();
    }

    private void parse() throws IOException {
        if (!readVariables()) {
            throw new IOException();
        }
        this.sampleDescriptionBox = new SampleDescriptionBox();
        AudioSampleEntry audioSampleEntry = new AudioSampleEntry(this.type);
        audioSampleEntry.setChannelCount(this.channelCount);
        audioSampleEntry.setSampleRate(this.samplerate);
        audioSampleEntry.setDataReferenceIndex(1);
        audioSampleEntry.setSampleSize(16);
        audioSampleEntry.addBox(this.ddts);
        this.sampleDescriptionBox.addBox(audioSampleEntry);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setLanguage(this.lang);
        this.trackMetaData.setTimescale(this.samplerate);
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public List<Sample> getSamples() {
        return this.samples;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public long[] getSampleDurations() {
        return this.sampleDurations;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public long[] getSyncSamples() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.AbstractTrack, com.googlecode.mp4parser.authoring.Track
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    @Override // com.googlecode.mp4parser.authoring.Track
    public String getHandler() {
        return "soun";
    }

    private void parseDtshdhdr(int size, ByteBuffer bb) {
        bb.getInt();
        bb.get();
        bb.getInt();
        bb.get();
        int bitwStreamMetadata = bb.getShort();
        bb.get();
        byte b = bb.get();
        this.numExtSubStreams = b;
        if ((bitwStreamMetadata & 1) == 1) {
            this.isVBR = true;
        }
        if ((bitwStreamMetadata & 8) == 8) {
            this.coreSubStreamPresent = true;
        }
        if ((bitwStreamMetadata & 16) == 16) {
            this.extensionSubStreamPresent = true;
            this.numExtSubStreams = b + 1;
        } else {
            this.numExtSubStreams = 0;
        }
        for (int i = 14; i < size; i++) {
            bb.get();
        }
    }

    private boolean parseCoressmd(int size, ByteBuffer bb) {
        int cmsr_1 = bb.get();
        int cmsr_2 = bb.getShort();
        this.coreMaxSampleRate = (cmsr_1 << 16) | (65535 & cmsr_2);
        this.coreBitRate = bb.getShort();
        this.coreChannelMask = bb.getShort();
        this.coreFramePayloadInBytes = bb.getInt();
        for (int i = 11; i < size; i++) {
            bb.get();
        }
        return true;
    }

    private boolean parseAuprhdr(int size, ByteBuffer bb) {
        bb.get();
        int bitwAupresData = bb.getShort();
        int a = bb.get();
        int b = bb.getShort();
        this.maxSampleRate = (a << 16) | (b & 65535);
        this.numFramesTotal = bb.getInt();
        this.samplesPerFrameAtMaxFs = bb.getShort();
        int a2 = bb.get();
        int b2 = bb.getInt();
        this.numSamplesOrigAudioAtMaxFs = (a2 << 32) | (b2 & 65535);
        this.channelMask = bb.getShort();
        this.codecDelayAtMaxFs = bb.getShort();
        int c = 21;
        if ((bitwAupresData & 3) == 3) {
            int a3 = bb.get();
            int b3 = bb.getShort();
            this.bcCoreMaxSampleRate = (65535 & b3) | (a3 << 16);
            this.bcCoreBitRate = bb.getShort();
            this.bcCoreChannelMask = bb.getShort();
            c = 21 + 7;
        }
        if ((bitwAupresData & 4) > 0) {
            this.lsbTrimPercent = bb.get();
            c++;
        }
        if ((bitwAupresData & 8) > 0) {
            this.lbrCodingPresent = 1;
        }
        while (c < size) {
            bb.get();
            c++;
        }
        return true;
    }

    private boolean parseExtssmd(int size, ByteBuffer bb) {
        int i;
        int a = bb.get();
        int b = bb.getShort();
        this.extAvgBitrate = (a << 16) | (b & 65535);
        if (this.isVBR) {
            int a2 = bb.get();
            int b2 = bb.getShort();
            this.extPeakBitrate = (65535 & b2) | (a2 << 16);
            this.extSmoothBuffSize = bb.getShort();
            i = 3 + 5;
        } else {
            this.extFramePayloadInBytes = bb.getInt();
            i = 3 + 4;
        }
        while (i < size) {
            bb.get();
            i++;
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x0185, code lost:
        if (r2 != true) goto L105;
     */
    /* JADX WARN: Code restructure failed: missing block: B:101:0x0187, code lost:
        if (r0 != false) goto L105;
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x0189, code lost:
        if (r9 != 0) goto L105;
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x018b, code lost:
        if (r7 != false) goto L105;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x018d, code lost:
        r12 = 13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x0193, code lost:
        if (r8 != 0) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x0195, code lost:
        if (r4 != false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x0197, code lost:
        if (r2 != false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x0199, code lost:
        if (r0 != false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x019c, code lost:
        if (r9 != 1) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x019e, code lost:
        if (r7 != false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x01a0, code lost:
        r12 = 14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x01a6, code lost:
        if (r13 != 0) goto L122;
     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x01a8, code lost:
        if (r8 != 0) goto L122;
     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x01aa, code lost:
        if (r4 != false) goto L122;
     */
    /* JADX WARN: Code restructure failed: missing block: B:116:0x01ac, code lost:
        if (r2 != false) goto L122;
     */
    /* JADX WARN: Code restructure failed: missing block: B:118:0x01af, code lost:
        if (r0 != true) goto L122;
     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x01b1, code lost:
        if (r9 != 0) goto L122;
     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x01b3, code lost:
        if (r7 != false) goto L122;
     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x01b5, code lost:
        r12 = 7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x01bc, code lost:
        if (r13 != 6) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:124:0x01be, code lost:
        if (r8 != 0) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:125:0x01c0, code lost:
        if (r4 != false) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:126:0x01c2, code lost:
        if (r2 != false) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:128:0x01c5, code lost:
        if (r0 != true) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:129:0x01c7, code lost:
        if (r9 != 0) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:130:0x01c9, code lost:
        if (r7 != false) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x01cb, code lost:
        r12 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:132:0x01d1, code lost:
        if (r13 != 0) goto L141;
     */
    /* JADX WARN: Code restructure failed: missing block: B:133:0x01d3, code lost:
        if (r8 != 0) goto L141;
     */
    /* JADX WARN: Code restructure failed: missing block: B:134:0x01d5, code lost:
        if (r4 != false) goto L141;
     */
    /* JADX WARN: Code restructure failed: missing block: B:136:0x01d8, code lost:
        if (r2 != true) goto L141;
     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x01da, code lost:
        if (r0 != false) goto L141;
     */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x01dc, code lost:
        if (r9 != 0) goto L141;
     */
    /* JADX WARN: Code restructure failed: missing block: B:139:0x01de, code lost:
        if (r7 != false) goto L141;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0032, code lost:
        r10 = r7.getLong();
        r39.dataOffset = r7.position();
        r5 = -1;
        r19 = false;
        r20 = false;
        r21 = 0;
        r22 = 0;
        r12 = -1;
        r0 = false;
        r15 = -1;
        r4 = false;
        r13 = 0;
        r2 = false;
        r14 = 0;
        r3 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:140:0x01e0, code lost:
        r12 = 11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:142:0x01e6, code lost:
        if (r13 != 6) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:143:0x01e8, code lost:
        if (r8 != 0) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:144:0x01ea, code lost:
        if (r4 != false) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x01ed, code lost:
        if (r2 != true) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x01ef, code lost:
        if (r0 != false) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:148:0x01f1, code lost:
        if (r9 != 0) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x01f3, code lost:
        if (r7 != false) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x005e, code lost:
        if (r19 == false) goto L198;
     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x01f5, code lost:
        r12 = 12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x01fa, code lost:
        if (r13 != 0) goto L160;
     */
    /* JADX WARN: Code restructure failed: missing block: B:152:0x01fc, code lost:
        if (r8 != 0) goto L160;
     */
    /* JADX WARN: Code restructure failed: missing block: B:153:0x01fe, code lost:
        if (r4 != false) goto L160;
     */
    /* JADX WARN: Code restructure failed: missing block: B:154:0x0200, code lost:
        if (r2 != false) goto L160;
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x0202, code lost:
        if (r0 != false) goto L160;
     */
    /* JADX WARN: Code restructure failed: missing block: B:157:0x0205, code lost:
        if (r9 != 1) goto L160;
     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x0207, code lost:
        if (r7 != false) goto L160;
     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x0209, code lost:
        r12 = 15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:161:0x020f, code lost:
        if (r13 != 2) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x0211, code lost:
        if (r8 != 0) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:163:0x0213, code lost:
        if (r4 != false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:164:0x0215, code lost:
        if (r2 != false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:165:0x0217, code lost:
        if (r0 != false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x021a, code lost:
        if (r9 != 1) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x021c, code lost:
        if (r7 != false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:169:0x021e, code lost:
        r12 = 16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0064, code lost:
        switch(r39.samplesPerFrame) {
            case 512: goto L21;
            case 1024: goto L20;
            case 2048: goto L19;
            case 4096: goto L18;
            default: goto L17;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x0223, code lost:
        r12 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:171:0x0225, code lost:
        r15 = r39.ddts;
        r17 = r0;
        r0 = r39.maxSampleRate;
        r24 = r4;
        r18 = r5;
        r15.setDTSSamplingFrequency(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x0235, code lost:
        if (r39.isVBR == false) goto L174;
     */
    /* JADX WARN: Code restructure failed: missing block: B:173:0x0237, code lost:
        r39.ddts.setMaxBitRate((r39.coreBitRate + r39.extPeakBitrate) * 1000);
     */
    /* JADX WARN: Code restructure failed: missing block: B:174:0x0245, code lost:
        r39.ddts.setMaxBitRate((r39.coreBitRate + r39.extAvgBitrate) * 1000);
     */
    /* JADX WARN: Code restructure failed: missing block: B:175:0x0252, code lost:
        r39.ddts.setAvgBitRate((r39.coreBitRate + r39.extAvgBitrate) * 1000);
        r39.ddts.setPcmSampleDepth(r39.sampleSize);
        r39.ddts.setFrameDuration(r1);
        r39.ddts.setStreamConstruction(r12);
        r0 = r39.coreChannelMask;
     */
    /* JADX WARN: Code restructure failed: missing block: B:176:0x0274, code lost:
        if ((r0 & 8) > 0) goto L180;
     */
    /* JADX WARN: Code restructure failed: missing block: B:178:0x0278, code lost:
        if ((r0 & 4096) <= 0) goto L179;
     */
    /* JADX WARN: Code restructure failed: missing block: B:179:0x027b, code lost:
        r39.ddts.setCoreLFEPresent(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0067, code lost:
        r1 = -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:180:0x0282, code lost:
        r39.ddts.setCoreLFEPresent(1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x0289, code lost:
        r39.ddts.setCoreLayout(r3);
        r39.ddts.setCoreSize(r39.coreFramePayloadInBytes);
        r39.ddts.setStereoDownmix(0);
        r39.ddts.setRepresentationType(4);
        r39.ddts.setChannelLayout(r39.channelMask);
     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x02aa, code lost:
        if (r39.coreMaxSampleRate <= 0) goto L186;
     */
    /* JADX WARN: Code restructure failed: missing block: B:184:0x02ae, code lost:
        if (r39.extAvgBitrate <= 0) goto L186;
     */
    /* JADX WARN: Code restructure failed: missing block: B:185:0x02b0, code lost:
        r39.ddts.setMultiAssetFlag(1);
        r5 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:186:0x02b8, code lost:
        r5 = 0;
        r39.ddts.setMultiAssetFlag(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:187:0x02bf, code lost:
        r39.ddts.setLBRDurationMod(r39.lbrCodingPresent);
        r39.ddts.setReservedBoxPresent(r5);
        r39.channelCount = r5;
        r0 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:189:0x02d0, code lost:
        if (r0 < 16) goto L192;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x006a, code lost:
        r1 = 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:190:0x02d2, code lost:
        r5 = r39.dataSource;
        r15 = r39.dataOffset;
        r5 = r20;
        r0 = generateSamples(r5, r15, r10, r5);
        r39.samples = r0;
        r0 = new long[r0.size()];
        r39.sampleDurations = r0;
        java.util.Arrays.fill(r0, r39.samplesPerFrame);
     */
    /* JADX WARN: Code restructure failed: missing block: B:191:0x0307, code lost:
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:192:0x0308, code lost:
        r31 = r1;
        r35 = r2;
        r32 = r3;
        r28 = r12;
        r33 = r14;
        r12 = r17;
        r34 = r18;
        r15 = r27;
        r27 = r13;
        r13 = r24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:193:0x0321, code lost:
        if (((r39.channelMask >> r0) & 1) != 1) goto L347;
     */
    /* JADX WARN: Code restructure failed: missing block: B:194:0x0323, code lost:
        switch(r0) {
            case 0: goto L196;
            case 3: goto L196;
            case 4: goto L196;
            case 7: goto L196;
            case 8: goto L196;
            case 12: goto L196;
            case 14: goto L196;
            default: goto L195;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:195:0x0326, code lost:
        r39.channelCount += 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x032d, code lost:
        r39.channelCount++;
     */
    /* JADX WARN: Code restructure failed: missing block: B:197:0x0332, code lost:
        r0 = r0 + 1;
        r17 = r12;
        r24 = r13;
        r13 = r27;
        r12 = r28;
        r1 = r31;
        r3 = r32;
        r14 = r33;
        r18 = r34;
        r2 = r35;
        r27 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:198:0x034a, code lost:
        r35 = r2;
        r34 = r5;
        r30 = r9;
        r29 = r12;
        r27 = r13;
        r33 = r14;
        r9 = r21;
        r12 = r0;
        r13 = r4;
        r21 = r8;
        r8 = r22;
        r22 = r7;
        r7 = r20;
        r20 = r15;
        r15 = r3;
        r0 = r22.position();
        r1 = r22.getInt();
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x0371, code lost:
        if (r1 != 2147385345) goto L330;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x006f, code lost:
        r1 = 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:201:0x0375, code lost:
        if (r20 != 1) goto L331;
     */
    /* JADX WARN: Code restructure failed: missing block: B:202:0x0377, code lost:
        r19 = true;
        r20 = r7;
        r0 = r12;
        r3 = r15;
        r7 = r22;
        r12 = r29;
        r14 = r33;
        r5 = r34;
        r2 = r35;
        r15 = r20;
        r22 = r8;
        r4 = r13;
        r8 = r21;
        r13 = r27;
        r21 = r9;
        r9 = r30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:203:0x0395, code lost:
        r3 = new com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer(r22);
        r4 = r3.readBits(1);
        r2 = r3.readBits(5);
        r31 = r10;
        r10 = r3.readBits(1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:204:0x03af, code lost:
        if (r4 != 1) goto L333;
     */
    /* JADX WARN: Code restructure failed: missing block: B:206:0x03b3, code lost:
        if (r2 != 31) goto L334;
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x03b5, code lost:
        if (r10 == 0) goto L209;
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x03be, code lost:
        r11 = r3.readBits(7);
        r39.samplesPerFrame = (r11 + 1) * 32;
        r14 = r3.readBits(14);
        r2 = r39.frameSize;
        r39.frameSize = r2 + (r14 + 1);
        r2 = r3.readBits(6);
        r4 = r3.readBits(4);
        r39.samplerate = getSampleRate(r4);
        r2 = r3.readBits(5);
        r4 = getBitRate(r2);
        r39.bitrate = r4;
        r29 = r3.readBits(1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0074, code lost:
        r1 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:210:0x03ff, code lost:
        if (r29 == 0) goto L212;
     */
    /* JADX WARN: Code restructure failed: missing block: B:211:0x0401, code lost:
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:212:0x0403, code lost:
        r3.readBits(1);
        r3.readBits(1);
        r3.readBits(1);
        r3.readBits(1);
        r27 = r3.readBits(3);
        r33 = r3.readBits(1);
        r3.readBits(1);
        r3.readBits(2);
        r3.readBits(1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x0425, code lost:
        if (r10 != 1) goto L215;
     */
    /* JADX WARN: Code restructure failed: missing block: B:214:0x0427, code lost:
        r3.readBits(16);
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x042c, code lost:
        r3.readBits(1);
        r37 = r3.readBits(4);
        r3.readBits(2);
        r4 = r3.readBits(3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:216:0x043e, code lost:
        switch(r4) {
            case 0: goto L221;
            case 1: goto L221;
            case 2: goto L220;
            case 3: goto L220;
            case 4: goto L337;
            case 5: goto L219;
            case 6: goto L219;
            default: goto L337;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:218:0x0444, code lost:
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:219:0x0445, code lost:
        r39.sampleSize = 24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0079, code lost:
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:220:0x044c, code lost:
        r39.sampleSize = 20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x0453, code lost:
        r39.sampleSize = 16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:222:0x045a, code lost:
        r3.readBits(1);
        r3.readBits(1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x0463, code lost:
        switch(r37) {
            case 6: goto L226;
            case 7: goto L225;
            default: goto L224;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:224:0x0466, code lost:
        r3.readBits(4);
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x046f, code lost:
        r2 = r3.readBits(4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:226:0x0478, code lost:
        r2 = r3.readBits(4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:227:0x0481, code lost:
        r2 = (r0 + r14) + 1;
        r22.position(r2);
        r20 = r7;
        r22 = r8;
        r0 = r12;
        r4 = r13;
        r3 = r15;
        r15 = 1;
        r8 = r21;
        r12 = r2;
        r13 = r27;
        r10 = r31;
        r14 = r33;
        r2 = r35;
        r7 = r22;
        r21 = r9;
        r9 = r30;
        r5 = r34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:229:0x04af, code lost:
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x007d, code lost:
        r27 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:230:0x04b0, code lost:
        r31 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:231:0x04b9, code lost:
        if (r1 != 1683496997) goto L339;
     */
    /* JADX WARN: Code restructure failed: missing block: B:233:0x04bc, code lost:
        if (r20 != (-1)) goto L235;
     */
    /* JADX WARN: Code restructure failed: missing block: B:234:0x04be, code lost:
        r2 = 0;
        r39.samplesPerFrame = r39.samplesPerFrameAtMaxFs;
     */
    /* JADX WARN: Code restructure failed: missing block: B:235:0x04c4, code lost:
        r2 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:236:0x04c5, code lost:
        r4 = new com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer(r22);
        r4.readBits(8);
        r4.readBits(2);
        r11 = r4.readBits(1);
        r10 = 12;
        r14 = 20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:237:0x04dd, code lost:
        if (r11 != 0) goto L239;
     */
    /* JADX WARN: Code restructure failed: missing block: B:238:0x04df, code lost:
        r10 = 8;
        r14 = 16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:239:0x04e3, code lost:
        r17 = r4.readBits(r10) + 1;
        r20 = r4.readBits(r14) + 1;
        r22.position(r0 + r17);
        r1 = r22.getInt();
        r22 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0080, code lost:
        if (r1 != (-1)) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:240:0x0500, code lost:
        if (r1 != 1515870810) goto L245;
     */
    /* JADX WARN: Code restructure failed: missing block: B:242:0x0503, code lost:
        if (r15 != true) goto L244;
     */
    /* JADX WARN: Code restructure failed: missing block: B:243:0x0505, code lost:
        r19 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:244:0x0507, code lost:
        r23 = 1;
        r3 = true;
        r2 = r35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:246:0x0512, code lost:
        if (r1 != 1191201283) goto L251;
     */
    /* JADX WARN: Code restructure failed: missing block: B:248:0x0515, code lost:
        if (r13 != true) goto L250;
     */
    /* JADX WARN: Code restructure failed: missing block: B:249:0x0517, code lost:
        r19 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0082, code lost:
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:250:0x0519, code lost:
        r13 = true;
        r23 = 1;
        r3 = r15;
        r2 = r35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:252:0x0524, code lost:
        if (r1 != 496366178) goto L257;
     */
    /* JADX WARN: Code restructure failed: missing block: B:253:0x0526, code lost:
        r23 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:254:0x052b, code lost:
        if (r35 != true) goto L256;
     */
    /* JADX WARN: Code restructure failed: missing block: B:255:0x052d, code lost:
        r19 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:0x052f, code lost:
        r2 = true;
        r3 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:257:0x0532, code lost:
        r23 = 1;
        r2 = r35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:258:0x0539, code lost:
        if (r1 != 1700671838) goto L263;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0084, code lost:
        r3 = 31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:260:0x053c, code lost:
        if (r12 != true) goto L262;
     */
    /* JADX WARN: Code restructure failed: missing block: B:261:0x053e, code lost:
        r19 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:262:0x0540, code lost:
        r12 = true;
        r3 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:264:0x0547, code lost:
        if (r1 != 176167201) goto L269;
     */
    /* JADX WARN: Code restructure failed: missing block: B:266:0x054a, code lost:
        if (r7 != true) goto L268;
     */
    /* JADX WARN: Code restructure failed: missing block: B:267:0x054c, code lost:
        r19 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x054e, code lost:
        r7 = true;
        r3 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0086, code lost:
        switch(r12) {
            case 0: goto L27;
            case 1: goto L28;
            case 2: goto L27;
            case 3: goto L28;
            case 4: goto L27;
            case 5: goto L27;
            case 6: goto L27;
            case 7: goto L27;
            case 8: goto L27;
            case 9: goto L27;
            default: goto L28;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:270:0x0555, code lost:
        if (r1 != 1101174087) goto L275;
     */
    /* JADX WARN: Code restructure failed: missing block: B:272:0x0558, code lost:
        if (r9 != 1) goto L274;
     */
    /* JADX WARN: Code restructure failed: missing block: B:273:0x055a, code lost:
        r19 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:274:0x055c, code lost:
        r9 = 1;
        r3 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0563, code lost:
        if (r1 != 45126241) goto L281;
     */
    /* JADX WARN: Code restructure failed: missing block: B:278:0x0566, code lost:
        if (r8 != 1) goto L280;
     */
    /* JADX WARN: Code restructure failed: missing block: B:279:0x0568, code lost:
        r19 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x008a, code lost:
        r3 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:280:0x056a, code lost:
        r8 = 1;
        r3 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:281:0x056e, code lost:
        r3 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:282:0x056f, code lost:
        if (r19 != false) goto L284;
     */
    /* JADX WARN: Code restructure failed: missing block: B:283:0x0571, code lost:
        r39.frameSize += r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:284:0x0577, code lost:
        r22.position(r0 + r20);
        r20 = r7;
        r0 = r12;
        r4 = r13;
        r15 = r22;
        r13 = r27;
        r12 = r29;
        r10 = r31;
        r14 = r33;
        r7 = r22;
        r22 = r8;
        r8 = r21;
        r5 = r23;
        r21 = r9;
        r9 = r30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:286:0x05b0, code lost:
        throw new java.io.IOException("No DTS_SYNCWORD_* found at " + r22.position());
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x008b, code lost:
        r18 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0091, code lost:
        if (r15 != 0) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0093, code lost:
        r9 = r21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x009a, code lost:
        if (r9 != 1) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x009c, code lost:
        r8 = r22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x009e, code lost:
        if (r8 != 0) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:349:?, code lost:
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00a0, code lost:
        r18 = 17;
        r39.type = com.coremedia.iso.boxes.sampleentry.AudioSampleEntry.TYPE11;
        r20 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00ad, code lost:
        r18 = 21;
        r39.type = com.coremedia.iso.boxes.sampleentry.AudioSampleEntry.TYPE12;
        r20 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00b8, code lost:
        r8 = r22;
        r7 = r20;
        r20 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00c1, code lost:
        if (r7 != true) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00c3, code lost:
        r18 = 18;
        r39.type = com.coremedia.iso.boxes.sampleentry.AudioSampleEntry.TYPE13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00ca, code lost:
        if (r8 != 1) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00cc, code lost:
        r39.type = com.coremedia.iso.boxes.sampleentry.AudioSampleEntry.TYPE12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00ce, code lost:
        if (r4 != false) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00d0, code lost:
        if (r9 != 0) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00d2, code lost:
        r18 = 19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00d6, code lost:
        if (r4 != true) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00d8, code lost:
        if (r9 != 0) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00da, code lost:
        r18 = 20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00dd, code lost:
        if (r4 != false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00e0, code lost:
        if (r9 != 1) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00e2, code lost:
        r18 = 21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00e4, code lost:
        r39.samplerate = r39.maxSampleRate;
        r39.sampleSize = 24;
        r12 = r18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00f0, code lost:
        r9 = r21;
        r8 = r22;
        r7 = r20;
        r20 = r15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00ff, code lost:
        if (r5 >= 1) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0103, code lost:
        if (r14 <= 0) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0105, code lost:
        switch(r13) {
            case 0: goto L61;
            case 2: goto L60;
            case 6: goto L59;
            default: goto L58;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0108, code lost:
        r39.type = com.coremedia.iso.boxes.sampleentry.AudioSampleEntry.TYPE12;
        r12 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0110, code lost:
        r39.type = com.coremedia.iso.boxes.sampleentry.AudioSampleEntry.TYPE12;
        r12 = 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0118, code lost:
        r39.type = "dtsc";
        r12 = 4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0120, code lost:
        r39.type = "dtsc";
        r12 = 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x0128, code lost:
        r39.type = "dtsc";
        r12 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0130, code lost:
        r39.type = com.coremedia.iso.boxes.sampleentry.AudioSampleEntry.TYPE12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0132, code lost:
        if (r14 != 0) goto L113;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x0134, code lost:
        if (r8 != 0) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0137, code lost:
        if (r4 != true) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x0139, code lost:
        if (r2 != false) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x013b, code lost:
        if (r0 != false) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x013d, code lost:
        if (r9 != 0) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x013f, code lost:
        if (r7 != false) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0141, code lost:
        r12 = 5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x0147, code lost:
        if (r8 != 0) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x0149, code lost:
        if (r4 != false) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x014b, code lost:
        if (r2 != false) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x014e, code lost:
        if (r0 != true) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0150, code lost:
        if (r9 != 0) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0152, code lost:
        if (r7 != false) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x0154, code lost:
        r12 = 6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x015a, code lost:
        if (r8 != 0) goto L89;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x015d, code lost:
        if (r4 != true) goto L89;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x015f, code lost:
        if (r2 != false) goto L89;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x0161, code lost:
        if (r0 != true) goto L89;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0163, code lost:
        if (r9 != 0) goto L89;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0165, code lost:
        if (r7 != false) goto L89;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0167, code lost:
        r12 = 9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x016d, code lost:
        if (r8 != 0) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x016f, code lost:
        if (r4 != false) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x0172, code lost:
        if (r2 != true) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0174, code lost:
        if (r0 != false) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0176, code lost:
        if (r9 != 0) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x0178, code lost:
        if (r7 != false) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x017a, code lost:
        r12 = 10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x0180, code lost:
        if (r8 != 0) goto L105;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x0183, code lost:
        if (r4 != true) goto L105;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean readVariables() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1680
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.DTSTrackImpl.readVariables():boolean");
    }

    private List<Sample> generateSamples(DataSource dataSource, int dataOffset, long dataSize, int corePresent) throws IOException {
        LookAhead la = new LookAhead(dataSource, dataOffset, dataSize, corePresent);
        List<Sample> mySamples = new ArrayList<>();
        while (true) {
            final ByteBuffer sample = la.findNextStart();
            if (sample != null) {
                mySamples.add(new Sample() { // from class: com.googlecode.mp4parser.authoring.tracks.DTSTrackImpl.1
                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public void writeTo(WritableByteChannel channel) throws IOException {
                        channel.write((ByteBuffer) sample.rewind());
                    }

                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public long getSize() {
                        return sample.rewind().remaining();
                    }

                    @Override // com.googlecode.mp4parser.authoring.Sample
                    public ByteBuffer asByteBuffer() {
                        return sample;
                    }
                });
            } else {
                System.err.println("all samples found");
                return mySamples;
            }
        }
    }

    private int getBitRate(int rate) throws IOException {
        switch (rate) {
            case 0:
                return 32;
            case 1:
                return 56;
            case 2:
                return 64;
            case 3:
                return 96;
            case 4:
                return 112;
            case 5:
                return 128;
            case 6:
                return PsExtractor.AUDIO_STREAM;
            case 7:
                return 224;
            case 8:
                return 256;
            case 9:
                return GroupCallActivity.TABLET_LIST_SIZE;
            case 10:
                return 384;
            case 11:
                return 448;
            case 12:
                return 512;
            case 13:
                return 576;
            case 14:
                return 640;
            case 15:
                return 768;
            case 16:
                return 960;
            case 17:
                return 1024;
            case 18:
                return 1152;
            case 19:
                return 1280;
            case 20:
                return 1344;
            case 21:
                return 1408;
            case 22:
                return 1411;
            case 23:
                return 1472;
            case 24:
                return 1536;
            case 25:
                return -1;
            default:
                throw new IOException("Unknown bitrate value");
        }
    }

    private int getSampleRate(int sfreq) throws IOException {
        switch (sfreq) {
            case 1:
                return 8000;
            case 2:
                return 16000;
            case 3:
                return 32000;
            case 4:
            case 5:
            case 9:
            case 10:
            default:
                throw new IOException("Unknown Sample Rate");
            case 6:
                return 11025;
            case 7:
                return 22050;
            case 8:
                return 44100;
            case 11:
                return 12000;
            case 12:
                return 24000;
            case 13:
                return 48000;
        }
    }

    /* loaded from: classes3.dex */
    public class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos;
        private final int corePresent;
        long dataEnd;
        DataSource dataSource;
        int inBufferPos = 0;
        long start;

        LookAhead(DataSource dataSource, long bufferStartPos, long dataSize, int corePresent) throws IOException {
            DTSTrackImpl.this = r3;
            this.dataSource = dataSource;
            this.bufferStartPos = bufferStartPos;
            this.dataEnd = dataSize + bufferStartPos;
            this.corePresent = corePresent;
            fillBuffer();
        }

        public ByteBuffer findNextStart() throws IOException {
            while (true) {
                try {
                    if (this.corePresent == 1) {
                        if (nextFourEquals0x7FFE8001()) {
                            break;
                        }
                        discardByte();
                    } else if (nextFourEquals0x64582025()) {
                        break;
                    } else {
                        discardByte();
                    }
                } catch (EOFException e) {
                    return null;
                }
                return null;
            }
            discardNext4AndMarkStart();
            while (true) {
                if (this.corePresent == 1) {
                    if (nextFourEquals0x7FFE8001orEof()) {
                        break;
                    }
                    discardQWord();
                } else if (nextFourEquals0x64582025orEof()) {
                    break;
                } else {
                    discardQWord();
                }
                return null;
            }
            return getSample();
        }

        private void fillBuffer() throws IOException {
            System.err.println("Fill Buffer");
            DataSource dataSource = this.dataSource;
            long j = this.bufferStartPos;
            this.buffer = dataSource.map(j, Math.min(this.dataEnd - j, 67108864L));
        }

        private boolean nextFourEquals0x64582025() throws IOException {
            return nextFourEquals((byte) 100, (byte) 88, (byte) 32, (byte) 37);
        }

        private boolean nextFourEquals0x7FFE8001() throws IOException {
            return nextFourEquals(Byte.MAX_VALUE, (byte) -2, Byte.MIN_VALUE, (byte) 1);
        }

        private boolean nextFourEquals(byte a, byte b, byte c, byte d) throws IOException {
            int limit = this.buffer.limit();
            int i = this.inBufferPos;
            if (limit - i >= 4) {
                return this.buffer.get(i) == a && this.buffer.get(this.inBufferPos + 1) == b && this.buffer.get(this.inBufferPos + 2) == c && this.buffer.get(this.inBufferPos + 3) == d;
            } else if (this.bufferStartPos + i + 4 >= this.dataSource.size()) {
                throw new EOFException();
            } else {
                return false;
            }
        }

        private boolean nextFourEquals0x64582025orEof() throws IOException {
            return nextFourEqualsOrEof((byte) 100, (byte) 88, (byte) 32, (byte) 37);
        }

        private boolean nextFourEquals0x7FFE8001orEof() throws IOException {
            return nextFourEqualsOrEof(Byte.MAX_VALUE, (byte) -2, Byte.MIN_VALUE, (byte) 1);
        }

        private boolean nextFourEqualsOrEof(byte a, byte b, byte c, byte d) throws IOException {
            int limit = this.buffer.limit();
            int i = this.inBufferPos;
            if (limit - i >= 4) {
                if ((this.bufferStartPos + i) % 1048576 == 0) {
                    PrintStream printStream = System.err;
                    StringBuilder sb = new StringBuilder();
                    sb.append(((this.bufferStartPos + this.inBufferPos) / DistributeConstants.KIBIBYTE_IN_BYTES) / DistributeConstants.KIBIBYTE_IN_BYTES);
                    printStream.println(sb.toString());
                }
                return this.buffer.get(this.inBufferPos) == a && this.buffer.get(this.inBufferPos + 1) == b && this.buffer.get(this.inBufferPos + 2) == c && this.buffer.get(this.inBufferPos + 3) == d;
            }
            long j = this.bufferStartPos;
            long j2 = this.dataEnd;
            if (i + j + 4 > j2) {
                return j + ((long) i) == j2;
            }
            this.bufferStartPos = this.start;
            this.inBufferPos = 0;
            fillBuffer();
            return nextFourEquals0x7FFE8001();
        }

        private void discardByte() {
            this.inBufferPos++;
        }

        private void discardQWord() {
            this.inBufferPos += 4;
        }

        private void discardNext4AndMarkStart() {
            long j = this.bufferStartPos;
            int i = this.inBufferPos;
            this.start = j + i;
            this.inBufferPos = i + 4;
        }

        private ByteBuffer getSample() {
            long j = this.start;
            long j2 = this.bufferStartPos;
            if (j >= j2) {
                this.buffer.position((int) (j - j2));
                Buffer sample = this.buffer.slice();
                sample.limit((int) (this.inBufferPos - (this.start - this.bufferStartPos)));
                return (ByteBuffer) sample;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }
    }
}
