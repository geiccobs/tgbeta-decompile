package com.google.android.exoplayer2.extractor.ts;

import android.util.Pair;
import androidx.core.app.FrameMetricsAggregator;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;
import java.util.Collections;
/* loaded from: classes3.dex */
public final class AdtsReader implements ElementaryStreamReader {
    private static final int CRC_SIZE = 2;
    private static final int HEADER_SIZE = 5;
    private static final int ID3_HEADER_SIZE = 10;
    private static final byte[] ID3_IDENTIFIER = {73, 68, 51};
    private static final int ID3_SIZE_OFFSET = 6;
    private static final int MATCH_STATE_FF = 512;
    private static final int MATCH_STATE_I = 768;
    private static final int MATCH_STATE_ID = 1024;
    private static final int MATCH_STATE_START = 256;
    private static final int MATCH_STATE_VALUE_SHIFT = 8;
    private static final int STATE_CHECKING_ADTS_HEADER = 1;
    private static final int STATE_FINDING_SAMPLE = 0;
    private static final int STATE_READING_ADTS_HEADER = 3;
    private static final int STATE_READING_ID3_HEADER = 2;
    private static final int STATE_READING_SAMPLE = 4;
    private static final String TAG = "AdtsReader";
    private static final int VERSION_UNSET = -1;
    private final ParsableBitArray adtsScratch;
    private int bytesRead;
    private int currentFrameVersion;
    private TrackOutput currentOutput;
    private long currentSampleDuration;
    private final boolean exposeId3;
    private int firstFrameSampleRateIndex;
    private int firstFrameVersion;
    private String formatId;
    private boolean foundFirstFrame;
    private boolean hasCrc;
    private boolean hasOutputFormat;
    private final ParsableByteArray id3HeaderBuffer;
    private TrackOutput id3Output;
    private final String language;
    private int matchState;
    private TrackOutput output;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;

    public AdtsReader(boolean exposeId3) {
        this(exposeId3, null);
    }

    public AdtsReader(boolean exposeId3, String language) {
        this.adtsScratch = new ParsableBitArray(new byte[7]);
        this.id3HeaderBuffer = new ParsableByteArray(Arrays.copyOf(ID3_IDENTIFIER, 10));
        setFindingSampleState();
        this.firstFrameVersion = -1;
        this.firstFrameSampleRateIndex = -1;
        this.sampleDurationUs = C.TIME_UNSET;
        this.exposeId3 = exposeId3;
        this.language = language;
    }

    public static boolean isAdtsSyncWord(int candidateSyncWord) {
        return (65526 & candidateSyncWord) == 65520;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void seek() {
        resetSync();
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        this.formatId = idGenerator.getFormatId();
        this.output = extractorOutput.track(idGenerator.getTrackId(), 1);
        if (this.exposeId3) {
            idGenerator.generateNewId();
            TrackOutput track = extractorOutput.track(idGenerator.getTrackId(), 4);
            this.id3Output = track;
            track.format(Format.createSampleFormat(idGenerator.getFormatId(), MimeTypes.APPLICATION_ID3, null, -1, null));
            return;
        }
        this.id3Output = new DummyTrackOutput();
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetStarted(long pesTimeUs, int flags) {
        this.timeUs = pesTimeUs;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void consume(ParsableByteArray data) throws ParserException {
        while (data.bytesLeft() > 0) {
            switch (this.state) {
                case 0:
                    findNextSample(data);
                    break;
                case 1:
                    checkAdtsHeader(data);
                    break;
                case 2:
                    if (!continueRead(data, this.id3HeaderBuffer.data, 10)) {
                        break;
                    } else {
                        parseId3Header();
                        break;
                    }
                case 3:
                    int targetLength = this.hasCrc ? 7 : 5;
                    if (!continueRead(data, this.adtsScratch.data, targetLength)) {
                        break;
                    } else {
                        parseAdtsHeader();
                        break;
                    }
                case 4:
                    readSample(data);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetFinished() {
    }

    public long getSampleDurationUs() {
        return this.sampleDurationUs;
    }

    private void resetSync() {
        this.foundFirstFrame = false;
        setFindingSampleState();
    }

    private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
        int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
        source.readBytes(target, this.bytesRead, bytesToRead);
        int i = this.bytesRead + bytesToRead;
        this.bytesRead = i;
        return i == targetLength;
    }

    private void setFindingSampleState() {
        this.state = 0;
        this.bytesRead = 0;
        this.matchState = 256;
    }

    private void setReadingId3HeaderState() {
        this.state = 2;
        this.bytesRead = ID3_IDENTIFIER.length;
        this.sampleSize = 0;
        this.id3HeaderBuffer.setPosition(0);
    }

    private void setReadingSampleState(TrackOutput outputToUse, long currentSampleDuration, int priorReadBytes, int sampleSize) {
        this.state = 4;
        this.bytesRead = priorReadBytes;
        this.currentOutput = outputToUse;
        this.currentSampleDuration = currentSampleDuration;
        this.sampleSize = sampleSize;
    }

    private void setReadingAdtsHeaderState() {
        this.state = 3;
        this.bytesRead = 0;
    }

    private void setCheckingAdtsHeaderState() {
        this.state = 1;
        this.bytesRead = 0;
    }

    private void findNextSample(ParsableByteArray pesBuffer) {
        byte[] adtsData = pesBuffer.data;
        int data = pesBuffer.getPosition();
        int endOffset = pesBuffer.limit();
        while (data < endOffset) {
            int position = data + 1;
            int data2 = adtsData[data] & 255;
            if (this.matchState == 512 && isAdtsSyncBytes((byte) -1, (byte) data2) && (this.foundFirstFrame || checkSyncPositionValid(pesBuffer, position - 2))) {
                this.currentFrameVersion = (data2 & 8) >> 3;
                this.hasCrc = (data2 & 1) == 0;
                if (!this.foundFirstFrame) {
                    setCheckingAdtsHeaderState();
                } else {
                    setReadingAdtsHeaderState();
                }
                pesBuffer.setPosition(position);
                return;
            }
            int i = this.matchState;
            switch (i | data2) {
                case 329:
                    this.matchState = MATCH_STATE_I;
                    break;
                case FrameMetricsAggregator.EVERY_DURATION /* 511 */:
                    this.matchState = 512;
                    break;
                case 836:
                    this.matchState = 1024;
                    break;
                case 1075:
                    setReadingId3HeaderState();
                    pesBuffer.setPosition(position);
                    return;
                default:
                    if (i == 256) {
                        break;
                    } else {
                        this.matchState = 256;
                        data = position - 1;
                        continue;
                    }
            }
            data = position;
        }
        pesBuffer.setPosition(data);
    }

    private void checkAdtsHeader(ParsableByteArray buffer) {
        if (buffer.bytesLeft() == 0) {
            return;
        }
        this.adtsScratch.data[0] = buffer.data[buffer.getPosition()];
        this.adtsScratch.setPosition(2);
        int currentFrameSampleRateIndex = this.adtsScratch.readBits(4);
        int i = this.firstFrameSampleRateIndex;
        if (i != -1 && currentFrameSampleRateIndex != i) {
            resetSync();
            return;
        }
        if (!this.foundFirstFrame) {
            this.foundFirstFrame = true;
            this.firstFrameVersion = this.currentFrameVersion;
            this.firstFrameSampleRateIndex = currentFrameSampleRateIndex;
        }
        setReadingAdtsHeaderState();
    }

    private boolean checkSyncPositionValid(ParsableByteArray pesBuffer, int syncPositionCandidate) {
        pesBuffer.setPosition(syncPositionCandidate + 1);
        if (!tryRead(pesBuffer, this.adtsScratch.data, 1)) {
            return false;
        }
        this.adtsScratch.setPosition(4);
        int currentFrameVersion = this.adtsScratch.readBits(1);
        int i = this.firstFrameVersion;
        if (i != -1 && currentFrameVersion != i) {
            return false;
        }
        if (this.firstFrameSampleRateIndex != -1) {
            if (!tryRead(pesBuffer, this.adtsScratch.data, 1)) {
                return true;
            }
            this.adtsScratch.setPosition(2);
            int currentFrameSampleRateIndex = this.adtsScratch.readBits(4);
            if (currentFrameSampleRateIndex != this.firstFrameSampleRateIndex) {
                return false;
            }
            pesBuffer.setPosition(syncPositionCandidate + 2);
        }
        if (!tryRead(pesBuffer, this.adtsScratch.data, 4)) {
            return true;
        }
        this.adtsScratch.setPosition(14);
        int frameSize = this.adtsScratch.readBits(13);
        if (frameSize < 7) {
            return false;
        }
        byte[] data = pesBuffer.data;
        int dataLimit = pesBuffer.limit();
        int nextSyncPosition = syncPositionCandidate + frameSize;
        if (nextSyncPosition >= dataLimit) {
            return true;
        }
        if (data[nextSyncPosition] == -1) {
            if (nextSyncPosition + 1 == dataLimit) {
                return true;
            }
            return isAdtsSyncBytes((byte) -1, data[nextSyncPosition + 1]) && ((data[nextSyncPosition + 1] & 8) >> 3) == currentFrameVersion;
        } else if (data[nextSyncPosition] != 73) {
            return false;
        } else {
            if (nextSyncPosition + 1 == dataLimit) {
                return true;
            }
            if (data[nextSyncPosition + 1] != 68) {
                return false;
            }
            return nextSyncPosition + 2 == dataLimit || data[nextSyncPosition + 2] == 51;
        }
    }

    private boolean isAdtsSyncBytes(byte firstByte, byte secondByte) {
        int syncWord = ((firstByte & 255) << 8) | (secondByte & 255);
        return isAdtsSyncWord(syncWord);
    }

    private boolean tryRead(ParsableByteArray source, byte[] target, int targetLength) {
        if (source.bytesLeft() < targetLength) {
            return false;
        }
        source.readBytes(target, 0, targetLength);
        return true;
    }

    private void parseId3Header() {
        this.id3Output.sampleData(this.id3HeaderBuffer, 10);
        this.id3HeaderBuffer.setPosition(6);
        setReadingSampleState(this.id3Output, 0L, 10, this.id3HeaderBuffer.readSynchSafeInt() + 10);
    }

    private void parseAdtsHeader() throws ParserException {
        int sampleSize;
        this.adtsScratch.setPosition(0);
        if (!this.hasOutputFormat) {
            int audioObjectType = this.adtsScratch.readBits(2) + 1;
            if (audioObjectType != 2) {
                Log.w(TAG, "Detected audio object type: " + audioObjectType + ", but assuming AAC LC.");
                audioObjectType = 2;
            }
            this.adtsScratch.skipBits(5);
            int channelConfig = this.adtsScratch.readBits(3);
            byte[] audioSpecificConfig = CodecSpecificDataUtil.buildAacAudioSpecificConfig(audioObjectType, this.firstFrameSampleRateIndex, channelConfig);
            Pair<Integer, Integer> audioParams = CodecSpecificDataUtil.parseAacAudioSpecificConfig(audioSpecificConfig);
            Format format = Format.createAudioSampleFormat(this.formatId, "audio/mp4a-latm", null, -1, -1, ((Integer) audioParams.second).intValue(), ((Integer) audioParams.first).intValue(), Collections.singletonList(audioSpecificConfig), null, 0, this.language);
            this.sampleDurationUs = 1024000000 / format.sampleRate;
            this.output.format(format);
            this.hasOutputFormat = true;
        } else {
            this.adtsScratch.skipBits(10);
        }
        this.adtsScratch.skipBits(4);
        int sampleSize2 = (this.adtsScratch.readBits(13) - 2) - 5;
        if (!this.hasCrc) {
            sampleSize = sampleSize2;
        } else {
            sampleSize = sampleSize2 - 2;
        }
        setReadingSampleState(this.output, this.sampleDurationUs, 0, sampleSize);
    }

    private void readSample(ParsableByteArray data) {
        int bytesToRead = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
        this.currentOutput.sampleData(data, bytesToRead);
        int i = this.bytesRead + bytesToRead;
        this.bytesRead = i;
        int i2 = this.sampleSize;
        if (i == i2) {
            this.currentOutput.sampleMetadata(this.timeUs, 1, i2, 0, null);
            this.timeUs += this.currentSampleDuration;
            setFindingSampleState();
        }
    }
}
