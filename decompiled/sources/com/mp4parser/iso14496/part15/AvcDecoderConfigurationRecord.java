package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AvcDecoderConfigurationRecord {
    public int avcLevelIndication;
    public int avcProfileIndication;
    public int bitDepthChromaMinus8;
    public int bitDepthChromaMinus8PaddingBits;
    public int bitDepthLumaMinus8;
    public int bitDepthLumaMinus8PaddingBits;
    public int chromaFormat;
    public int chromaFormatPaddingBits;
    public int configurationVersion;
    public boolean hasExts;
    public int lengthSizeMinusOne;
    public int lengthSizeMinusOnePaddingBits;
    public int numberOfSequenceParameterSetsPaddingBits;
    public List<byte[]> pictureParameterSets;
    public int profileCompatibility;
    public List<byte[]> sequenceParameterSetExts;
    public List<byte[]> sequenceParameterSets;

    public AvcDecoderConfigurationRecord() {
        this.sequenceParameterSets = new ArrayList();
        this.pictureParameterSets = new ArrayList();
        this.hasExts = true;
        this.chromaFormat = 1;
        this.bitDepthLumaMinus8 = 0;
        this.bitDepthChromaMinus8 = 0;
        this.sequenceParameterSetExts = new ArrayList();
        this.lengthSizeMinusOnePaddingBits = 63;
        this.numberOfSequenceParameterSetsPaddingBits = 7;
        this.chromaFormatPaddingBits = 31;
        this.bitDepthLumaMinus8PaddingBits = 31;
        this.bitDepthChromaMinus8PaddingBits = 31;
    }

    public AvcDecoderConfigurationRecord(ByteBuffer byteBuffer) {
        int i;
        this.sequenceParameterSets = new ArrayList();
        this.pictureParameterSets = new ArrayList();
        this.hasExts = true;
        this.chromaFormat = 1;
        this.bitDepthLumaMinus8 = 0;
        this.bitDepthChromaMinus8 = 0;
        this.sequenceParameterSetExts = new ArrayList();
        this.lengthSizeMinusOnePaddingBits = 63;
        this.numberOfSequenceParameterSetsPaddingBits = 7;
        this.chromaFormatPaddingBits = 31;
        this.bitDepthLumaMinus8PaddingBits = 31;
        this.bitDepthChromaMinus8PaddingBits = 31;
        this.configurationVersion = IsoTypeReader.readUInt8(byteBuffer);
        this.avcProfileIndication = IsoTypeReader.readUInt8(byteBuffer);
        this.profileCompatibility = IsoTypeReader.readUInt8(byteBuffer);
        this.avcLevelIndication = IsoTypeReader.readUInt8(byteBuffer);
        BitReaderBuffer bitReaderBuffer = new BitReaderBuffer(byteBuffer);
        this.lengthSizeMinusOnePaddingBits = bitReaderBuffer.readBits(6);
        this.lengthSizeMinusOne = bitReaderBuffer.readBits(2);
        this.numberOfSequenceParameterSetsPaddingBits = bitReaderBuffer.readBits(3);
        int readBits = bitReaderBuffer.readBits(5);
        for (int i2 = 0; i2 < readBits; i2++) {
            byte[] bArr = new byte[IsoTypeReader.readUInt16(byteBuffer)];
            byteBuffer.get(bArr);
            this.sequenceParameterSets.add(bArr);
        }
        long readUInt8 = IsoTypeReader.readUInt8(byteBuffer);
        for (int i3 = 0; i3 < readUInt8; i3++) {
            byte[] bArr2 = new byte[IsoTypeReader.readUInt16(byteBuffer)];
            byteBuffer.get(bArr2);
            this.pictureParameterSets.add(bArr2);
        }
        if (byteBuffer.remaining() < 4) {
            this.hasExts = false;
        }
        if (this.hasExts && ((i = this.avcProfileIndication) == 100 || i == 110 || i == 122 || i == 144)) {
            BitReaderBuffer bitReaderBuffer2 = new BitReaderBuffer(byteBuffer);
            this.chromaFormatPaddingBits = bitReaderBuffer2.readBits(6);
            this.chromaFormat = bitReaderBuffer2.readBits(2);
            this.bitDepthLumaMinus8PaddingBits = bitReaderBuffer2.readBits(5);
            this.bitDepthLumaMinus8 = bitReaderBuffer2.readBits(3);
            this.bitDepthChromaMinus8PaddingBits = bitReaderBuffer2.readBits(5);
            this.bitDepthChromaMinus8 = bitReaderBuffer2.readBits(3);
            long readUInt82 = IsoTypeReader.readUInt8(byteBuffer);
            for (int i4 = 0; i4 < readUInt82; i4++) {
                byte[] bArr3 = new byte[IsoTypeReader.readUInt16(byteBuffer)];
                byteBuffer.get(bArr3);
                this.sequenceParameterSetExts.add(bArr3);
            }
            return;
        }
        this.chromaFormat = -1;
        this.bitDepthLumaMinus8 = -1;
        this.bitDepthChromaMinus8 = -1;
    }

    public void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt8(byteBuffer, this.configurationVersion);
        IsoTypeWriter.writeUInt8(byteBuffer, this.avcProfileIndication);
        IsoTypeWriter.writeUInt8(byteBuffer, this.profileCompatibility);
        IsoTypeWriter.writeUInt8(byteBuffer, this.avcLevelIndication);
        BitWriterBuffer bitWriterBuffer = new BitWriterBuffer(byteBuffer);
        bitWriterBuffer.writeBits(this.lengthSizeMinusOnePaddingBits, 6);
        bitWriterBuffer.writeBits(this.lengthSizeMinusOne, 2);
        bitWriterBuffer.writeBits(this.numberOfSequenceParameterSetsPaddingBits, 3);
        bitWriterBuffer.writeBits(this.pictureParameterSets.size(), 5);
        for (byte[] bArr : this.sequenceParameterSets) {
            IsoTypeWriter.writeUInt16(byteBuffer, bArr.length);
            byteBuffer.put(bArr);
        }
        IsoTypeWriter.writeUInt8(byteBuffer, this.pictureParameterSets.size());
        for (byte[] bArr2 : this.pictureParameterSets) {
            IsoTypeWriter.writeUInt16(byteBuffer, bArr2.length);
            byteBuffer.put(bArr2);
        }
        if (this.hasExts) {
            int i = this.avcProfileIndication;
            if (i != 100 && i != 110 && i != 122 && i != 144) {
                return;
            }
            BitWriterBuffer bitWriterBuffer2 = new BitWriterBuffer(byteBuffer);
            bitWriterBuffer2.writeBits(this.chromaFormatPaddingBits, 6);
            bitWriterBuffer2.writeBits(this.chromaFormat, 2);
            bitWriterBuffer2.writeBits(this.bitDepthLumaMinus8PaddingBits, 5);
            bitWriterBuffer2.writeBits(this.bitDepthLumaMinus8, 3);
            bitWriterBuffer2.writeBits(this.bitDepthChromaMinus8PaddingBits, 5);
            bitWriterBuffer2.writeBits(this.bitDepthChromaMinus8, 3);
            for (byte[] bArr3 : this.sequenceParameterSetExts) {
                IsoTypeWriter.writeUInt16(byteBuffer, bArr3.length);
                byteBuffer.put(bArr3);
            }
        }
    }

    public long getContentSize() {
        int i;
        long j = 6;
        for (byte[] bArr : this.sequenceParameterSets) {
            j = j + 2 + bArr.length;
        }
        long j2 = j + 1;
        for (byte[] bArr2 : this.pictureParameterSets) {
            j2 = j2 + 2 + bArr2.length;
        }
        if (this.hasExts && ((i = this.avcProfileIndication) == 100 || i == 110 || i == 122 || i == 144)) {
            j2 += 4;
            for (byte[] bArr3 : this.sequenceParameterSetExts) {
                j2 = j2 + 2 + bArr3.length;
            }
        }
        return j2;
    }

    public String toString() {
        return "AvcDecoderConfigurationRecord{configurationVersion=" + this.configurationVersion + ", avcProfileIndication=" + this.avcProfileIndication + ", profileCompatibility=" + this.profileCompatibility + ", avcLevelIndication=" + this.avcLevelIndication + ", lengthSizeMinusOne=" + this.lengthSizeMinusOne + ", hasExts=" + this.hasExts + ", chromaFormat=" + this.chromaFormat + ", bitDepthLumaMinus8=" + this.bitDepthLumaMinus8 + ", bitDepthChromaMinus8=" + this.bitDepthChromaMinus8 + ", lengthSizeMinusOnePaddingBits=" + this.lengthSizeMinusOnePaddingBits + ", numberOfSequenceParameterSetsPaddingBits=" + this.numberOfSequenceParameterSetsPaddingBits + ", chromaFormatPaddingBits=" + this.chromaFormatPaddingBits + ", bitDepthLumaMinus8PaddingBits=" + this.bitDepthLumaMinus8PaddingBits + ", bitDepthChromaMinus8PaddingBits=" + this.bitDepthChromaMinus8PaddingBits + '}';
    }
}
