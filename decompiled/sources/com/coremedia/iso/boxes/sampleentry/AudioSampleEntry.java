package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes.dex */
public final class AudioSampleEntry extends AbstractSampleEntry {
    private long bytesPerFrame;
    private long bytesPerPacket;
    private long bytesPerSample;
    private int channelCount;
    private int compressionId;
    private int packetSize;
    private int reserved1;
    private long reserved2;
    private long sampleRate;
    private int sampleSize;
    private long samplesPerPacket;
    private int soundVersion;
    private byte[] soundVersion2Data;

    public AudioSampleEntry(String str) {
        super(str);
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public long getSampleRate() {
        return this.sampleRate;
    }

    public void setChannelCount(int i) {
        this.channelCount = i;
    }

    public void setSampleSize(int i) {
        this.sampleSize = i;
    }

    public void setSampleRate(long j) {
        this.sampleRate = j;
    }

    @Override // com.googlecode.mp4parser.AbstractContainerBox, com.coremedia.iso.boxes.Box
    public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        writableByteChannel.write(getHeader());
        int i = this.soundVersion;
        int i2 = 0;
        int i3 = (i == 1 ? 16 : 0) + 28;
        if (i == 2) {
            i2 = 36;
        }
        ByteBuffer allocate = ByteBuffer.allocate(i3 + i2);
        allocate.position(6);
        IsoTypeWriter.writeUInt16(allocate, this.dataReferenceIndex);
        IsoTypeWriter.writeUInt16(allocate, this.soundVersion);
        IsoTypeWriter.writeUInt16(allocate, this.reserved1);
        IsoTypeWriter.writeUInt32(allocate, this.reserved2);
        IsoTypeWriter.writeUInt16(allocate, this.channelCount);
        IsoTypeWriter.writeUInt16(allocate, this.sampleSize);
        IsoTypeWriter.writeUInt16(allocate, this.compressionId);
        IsoTypeWriter.writeUInt16(allocate, this.packetSize);
        if (this.type.equals("mlpa")) {
            IsoTypeWriter.writeUInt32(allocate, getSampleRate());
        } else {
            IsoTypeWriter.writeUInt32(allocate, getSampleRate() << 16);
        }
        if (this.soundVersion == 1) {
            IsoTypeWriter.writeUInt32(allocate, this.samplesPerPacket);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerPacket);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerFrame);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerSample);
        }
        if (this.soundVersion == 2) {
            IsoTypeWriter.writeUInt32(allocate, this.samplesPerPacket);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerPacket);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerFrame);
            IsoTypeWriter.writeUInt32(allocate, this.bytesPerSample);
            allocate.put(this.soundVersion2Data);
        }
        writableByteChannel.write((ByteBuffer) allocate.rewind());
        writeContainer(writableByteChannel);
    }

    @Override // com.googlecode.mp4parser.AbstractContainerBox, com.coremedia.iso.boxes.Box
    public long getSize() {
        int i = this.soundVersion;
        int i2 = 16;
        int i3 = 0;
        int i4 = (i == 1 ? 16 : 0) + 28;
        if (i == 2) {
            i3 = 36;
        }
        long containerSize = i4 + i3 + getContainerSize();
        if (!this.largeBox && 8 + containerSize < 4294967296L) {
            i2 = 8;
        }
        return containerSize + i2;
    }

    @Override // com.googlecode.mp4parser.BasicContainer
    public String toString() {
        return "AudioSampleEntry{bytesPerSample=" + this.bytesPerSample + ", bytesPerFrame=" + this.bytesPerFrame + ", bytesPerPacket=" + this.bytesPerPacket + ", samplesPerPacket=" + this.samplesPerPacket + ", packetSize=" + this.packetSize + ", compressionId=" + this.compressionId + ", soundVersion=" + this.soundVersion + ", sampleRate=" + this.sampleRate + ", sampleSize=" + this.sampleSize + ", channelCount=" + this.channelCount + ", boxes=" + getBoxes() + '}';
    }
}
