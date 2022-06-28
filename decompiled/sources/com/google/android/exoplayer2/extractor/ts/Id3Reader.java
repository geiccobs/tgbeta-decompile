package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
/* loaded from: classes3.dex */
public final class Id3Reader implements ElementaryStreamReader {
    private static final String TAG = "Id3Reader";
    private final ParsableByteArray id3Header = new ParsableByteArray(10);
    private TrackOutput output;
    private int sampleBytesRead;
    private int sampleSize;
    private long sampleTimeUs;
    private boolean writingSample;

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void seek() {
        this.writingSample = false;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
        idGenerator.generateNewId();
        TrackOutput track = extractorOutput.track(idGenerator.getTrackId(), 4);
        this.output = track;
        track.format(Format.createSampleFormat(idGenerator.getFormatId(), MimeTypes.APPLICATION_ID3, null, -1, null));
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetStarted(long pesTimeUs, int flags) {
        if ((flags & 4) == 0) {
            return;
        }
        this.writingSample = true;
        this.sampleTimeUs = pesTimeUs;
        this.sampleSize = 0;
        this.sampleBytesRead = 0;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void consume(ParsableByteArray data) {
        if (!this.writingSample) {
            return;
        }
        int bytesAvailable = data.bytesLeft();
        int i = this.sampleBytesRead;
        if (i < 10) {
            int headerBytesAvailable = Math.min(bytesAvailable, 10 - i);
            System.arraycopy(data.data, data.getPosition(), this.id3Header.data, this.sampleBytesRead, headerBytesAvailable);
            if (this.sampleBytesRead + headerBytesAvailable == 10) {
                this.id3Header.setPosition(0);
                if (73 != this.id3Header.readUnsignedByte() || 68 != this.id3Header.readUnsignedByte() || 51 != this.id3Header.readUnsignedByte()) {
                    Log.w(TAG, "Discarding invalid ID3 tag");
                    this.writingSample = false;
                    return;
                }
                this.id3Header.skipBytes(3);
                this.sampleSize = this.id3Header.readSynchSafeInt() + 10;
            }
        }
        int bytesToWrite = Math.min(bytesAvailable, this.sampleSize - this.sampleBytesRead);
        this.output.sampleData(data, bytesToWrite);
        this.sampleBytesRead += bytesToWrite;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetFinished() {
        int i;
        if (!this.writingSample || (i = this.sampleSize) == 0 || this.sampleBytesRead != i) {
            return;
        }
        this.output.sampleMetadata(this.sampleTimeUs, 1, i, 0, null);
        this.writingSample = false;
    }
}
