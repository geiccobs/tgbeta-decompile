package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.Ac4Util;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class Ac4Reader implements ElementaryStreamReader {
    private static final int STATE_FINDING_SYNC = 0;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private int bytesRead;
    private Format format;
    private boolean hasCRC;
    private final ParsableBitArray headerScratchBits;
    private final ParsableByteArray headerScratchBytes;
    private final String language;
    private boolean lastByteWasAC;
    private TrackOutput output;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;
    private String trackFormatId;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface State {
    }

    public Ac4Reader() {
        this(null);
    }

    public Ac4Reader(String language) {
        ParsableBitArray parsableBitArray = new ParsableBitArray(new byte[16]);
        this.headerScratchBits = parsableBitArray;
        this.headerScratchBytes = new ParsableByteArray(parsableBitArray.data);
        this.state = 0;
        this.bytesRead = 0;
        this.lastByteWasAC = false;
        this.hasCRC = false;
        this.language = language;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void seek() {
        this.state = 0;
        this.bytesRead = 0;
        this.lastByteWasAC = false;
        this.hasCRC = false;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator generator) {
        generator.generateNewId();
        this.trackFormatId = generator.getFormatId();
        this.output = extractorOutput.track(generator.getTrackId(), 1);
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetStarted(long pesTimeUs, int flags) {
        this.timeUs = pesTimeUs;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void consume(ParsableByteArray data) {
        while (data.bytesLeft() > 0) {
            switch (this.state) {
                case 0:
                    if (!skipToNextSync(data)) {
                        break;
                    } else {
                        this.state = 1;
                        this.headerScratchBytes.data[0] = -84;
                        this.headerScratchBytes.data[1] = (byte) (this.hasCRC ? 65 : 64);
                        this.bytesRead = 2;
                        break;
                    }
                case 1:
                    if (!continueRead(data, this.headerScratchBytes.data, 16)) {
                        break;
                    } else {
                        parseHeader();
                        this.headerScratchBytes.setPosition(0);
                        this.output.sampleData(this.headerScratchBytes, 16);
                        this.state = 2;
                        break;
                    }
                case 2:
                    int bytesToRead = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
                    this.output.sampleData(data, bytesToRead);
                    int i = this.bytesRead + bytesToRead;
                    this.bytesRead = i;
                    int i2 = this.sampleSize;
                    if (i != i2) {
                        break;
                    } else {
                        this.output.sampleMetadata(this.timeUs, 1, i2, 0, null);
                        this.timeUs += this.sampleDurationUs;
                        this.state = 0;
                        break;
                    }
            }
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetFinished() {
    }

    private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
        int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
        source.readBytes(target, this.bytesRead, bytesToRead);
        int i = this.bytesRead + bytesToRead;
        this.bytesRead = i;
        return i == targetLength;
    }

    private boolean skipToNextSync(ParsableByteArray pesBuffer) {
        boolean z;
        int secondByte;
        while (true) {
            z = false;
            if (pesBuffer.bytesLeft() <= 0) {
                return false;
            }
            if (!this.lastByteWasAC) {
                if (pesBuffer.readUnsignedByte() == 172) {
                    z = true;
                }
                this.lastByteWasAC = z;
            } else {
                secondByte = pesBuffer.readUnsignedByte();
                this.lastByteWasAC = secondByte == 172;
                if (secondByte == 64 || secondByte == 65) {
                    break;
                }
            }
        }
        if (secondByte == 65) {
            z = true;
        }
        this.hasCRC = z;
        return true;
    }

    private void parseHeader() {
        this.headerScratchBits.setPosition(0);
        Ac4Util.SyncFrameInfo frameInfo = Ac4Util.parseAc4SyncframeInfo(this.headerScratchBits);
        if (this.format == null || frameInfo.channelCount != this.format.channelCount || frameInfo.sampleRate != this.format.sampleRate || !MimeTypes.AUDIO_AC4.equals(this.format.sampleMimeType)) {
            Format createAudioSampleFormat = Format.createAudioSampleFormat(this.trackFormatId, MimeTypes.AUDIO_AC4, null, -1, -1, frameInfo.channelCount, frameInfo.sampleRate, null, null, 0, this.language);
            this.format = createAudioSampleFormat;
            this.output.format(createAudioSampleFormat);
        }
        this.sampleSize = frameInfo.frameSize;
        this.sampleDurationUs = (frameInfo.sampleCount * 1000000) / this.format.sampleRate;
    }
}
