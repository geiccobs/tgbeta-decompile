package com.google.android.exoplayer2.extractor.ts;

import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Collections;
import org.telegram.messenger.MediaController;
/* loaded from: classes.dex */
public final class LatmReader implements ElementaryStreamReader {
    private int audioMuxVersionA;
    private int bytesRead;
    private int channelCount;
    private Format format;
    private String formatId;
    private int frameLengthType;
    private final String language;
    private int numSubframes;
    private long otherDataLenBits;
    private boolean otherDataPresent;
    private TrackOutput output;
    private final ParsableBitArray sampleBitArray;
    private final ParsableByteArray sampleDataBuffer;
    private long sampleDurationUs;
    private int sampleRateHz;
    private int sampleSize;
    private int secondHeaderByte;
    private int state;
    private boolean streamMuxRead;
    private long timeUs;

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetFinished() {
    }

    public LatmReader(String str) {
        this.language = str;
        ParsableByteArray parsableByteArray = new ParsableByteArray(1024);
        this.sampleDataBuffer = parsableByteArray;
        this.sampleBitArray = new ParsableBitArray(parsableByteArray.data);
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void seek() {
        this.state = 0;
        this.streamMuxRead = false;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator trackIdGenerator) {
        trackIdGenerator.generateNewId();
        this.output = extractorOutput.track(trackIdGenerator.getTrackId(), 1);
        this.formatId = trackIdGenerator.getFormatId();
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetStarted(long j, int i) {
        this.timeUs = j;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void consume(ParsableByteArray parsableByteArray) throws ParserException {
        while (parsableByteArray.bytesLeft() > 0) {
            int i = this.state;
            if (i != 0) {
                if (i == 1) {
                    int readUnsignedByte = parsableByteArray.readUnsignedByte();
                    if ((readUnsignedByte & 224) == 224) {
                        this.secondHeaderByte = readUnsignedByte;
                        this.state = 2;
                    } else if (readUnsignedByte != 86) {
                        this.state = 0;
                    }
                } else if (i == 2) {
                    int readUnsignedByte2 = ((this.secondHeaderByte & (-225)) << 8) | parsableByteArray.readUnsignedByte();
                    this.sampleSize = readUnsignedByte2;
                    if (readUnsignedByte2 > this.sampleDataBuffer.data.length) {
                        resetBufferForSize(readUnsignedByte2);
                    }
                    this.bytesRead = 0;
                    this.state = 3;
                } else if (i == 3) {
                    int min = Math.min(parsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
                    parsableByteArray.readBytes(this.sampleBitArray.data, this.bytesRead, min);
                    int i2 = this.bytesRead + min;
                    this.bytesRead = i2;
                    if (i2 == this.sampleSize) {
                        this.sampleBitArray.setPosition(0);
                        parseAudioMuxElement(this.sampleBitArray);
                        this.state = 0;
                    }
                } else {
                    throw new IllegalStateException();
                }
            } else if (parsableByteArray.readUnsignedByte() == 86) {
                this.state = 1;
            }
        }
    }

    private void parseAudioMuxElement(ParsableBitArray parsableBitArray) throws ParserException {
        if (!parsableBitArray.readBit()) {
            this.streamMuxRead = true;
            parseStreamMuxConfig(parsableBitArray);
        } else if (!this.streamMuxRead) {
            return;
        }
        if (this.audioMuxVersionA == 0) {
            if (this.numSubframes != 0) {
                throw new ParserException();
            }
            parsePayloadMux(parsableBitArray, parsePayloadLengthInfo(parsableBitArray));
            if (!this.otherDataPresent) {
                return;
            }
            parsableBitArray.skipBits((int) this.otherDataLenBits);
            return;
        }
        throw new ParserException();
    }

    private void parseStreamMuxConfig(ParsableBitArray parsableBitArray) throws ParserException {
        boolean readBit;
        int readBits = parsableBitArray.readBits(1);
        int readBits2 = readBits == 1 ? parsableBitArray.readBits(1) : 0;
        this.audioMuxVersionA = readBits2;
        if (readBits2 == 0) {
            if (readBits == 1) {
                latmGetValue(parsableBitArray);
            }
            if (!parsableBitArray.readBit()) {
                throw new ParserException();
            }
            this.numSubframes = parsableBitArray.readBits(6);
            int readBits3 = parsableBitArray.readBits(4);
            int readBits4 = parsableBitArray.readBits(3);
            if (readBits3 != 0 || readBits4 != 0) {
                throw new ParserException();
            }
            if (readBits == 0) {
                int position = parsableBitArray.getPosition();
                int parseAudioSpecificConfig = parseAudioSpecificConfig(parsableBitArray);
                parsableBitArray.setPosition(position);
                byte[] bArr = new byte[(parseAudioSpecificConfig + 7) / 8];
                parsableBitArray.readBits(bArr, 0, parseAudioSpecificConfig);
                Format createAudioSampleFormat = Format.createAudioSampleFormat(this.formatId, MediaController.AUIDO_MIME_TYPE, null, -1, -1, this.channelCount, this.sampleRateHz, Collections.singletonList(bArr), null, 0, this.language);
                if (!createAudioSampleFormat.equals(this.format)) {
                    this.format = createAudioSampleFormat;
                    this.sampleDurationUs = 1024000000 / createAudioSampleFormat.sampleRate;
                    this.output.format(createAudioSampleFormat);
                }
            } else {
                parsableBitArray.skipBits(((int) latmGetValue(parsableBitArray)) - parseAudioSpecificConfig(parsableBitArray));
            }
            parseFrameLength(parsableBitArray);
            boolean readBit2 = parsableBitArray.readBit();
            this.otherDataPresent = readBit2;
            this.otherDataLenBits = 0L;
            if (readBit2) {
                if (readBits == 1) {
                    this.otherDataLenBits = latmGetValue(parsableBitArray);
                } else {
                    do {
                        readBit = parsableBitArray.readBit();
                        this.otherDataLenBits = (this.otherDataLenBits << 8) + parsableBitArray.readBits(8);
                    } while (readBit);
                }
            }
            if (!parsableBitArray.readBit()) {
                return;
            }
            parsableBitArray.skipBits(8);
            return;
        }
        throw new ParserException();
    }

    private void parseFrameLength(ParsableBitArray parsableBitArray) {
        int readBits = parsableBitArray.readBits(3);
        this.frameLengthType = readBits;
        if (readBits == 0) {
            parsableBitArray.skipBits(8);
        } else if (readBits == 1) {
            parsableBitArray.skipBits(9);
        } else if (readBits == 3 || readBits == 4 || readBits == 5) {
            parsableBitArray.skipBits(6);
        } else if (readBits == 6 || readBits == 7) {
            parsableBitArray.skipBits(1);
        } else {
            throw new IllegalStateException();
        }
    }

    private int parseAudioSpecificConfig(ParsableBitArray parsableBitArray) throws ParserException {
        int bitsLeft = parsableBitArray.bitsLeft();
        Pair<Integer, Integer> parseAacAudioSpecificConfig = CodecSpecificDataUtil.parseAacAudioSpecificConfig(parsableBitArray, true);
        this.sampleRateHz = ((Integer) parseAacAudioSpecificConfig.first).intValue();
        this.channelCount = ((Integer) parseAacAudioSpecificConfig.second).intValue();
        return bitsLeft - parsableBitArray.bitsLeft();
    }

    private int parsePayloadLengthInfo(ParsableBitArray parsableBitArray) throws ParserException {
        int readBits;
        if (this.frameLengthType == 0) {
            int i = 0;
            do {
                readBits = parsableBitArray.readBits(8);
                i += readBits;
            } while (readBits == 255);
            return i;
        }
        throw new ParserException();
    }

    private void parsePayloadMux(ParsableBitArray parsableBitArray, int i) {
        int position = parsableBitArray.getPosition();
        if ((position & 7) == 0) {
            this.sampleDataBuffer.setPosition(position >> 3);
        } else {
            parsableBitArray.readBits(this.sampleDataBuffer.data, 0, i * 8);
            this.sampleDataBuffer.setPosition(0);
        }
        this.output.sampleData(this.sampleDataBuffer, i);
        this.output.sampleMetadata(this.timeUs, 1, i, 0, null);
        this.timeUs += this.sampleDurationUs;
    }

    private void resetBufferForSize(int i) {
        this.sampleDataBuffer.reset(i);
        this.sampleBitArray.reset(this.sampleDataBuffer.data);
    }

    private static long latmGetValue(ParsableBitArray parsableBitArray) {
        return parsableBitArray.readBits((parsableBitArray.readBits(2) + 1) * 8);
    }
}
