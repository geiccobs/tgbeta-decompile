package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
@Descriptor(tags = {4})
/* loaded from: classes3.dex */
public class DecoderConfigDescriptor extends BaseDescriptor {
    private static Logger log = Logger.getLogger(DecoderConfigDescriptor.class.getName());
    AudioSpecificConfig audioSpecificInfo;
    long avgBitRate;
    int bufferSizeDB;
    byte[] configDescriptorDeadBytes;
    DecoderSpecificInfo decoderSpecificInfo;
    long maxBitRate;
    int objectTypeIndication;
    List<ProfileLevelIndicationDescriptor> profileLevelIndicationDescriptors = new ArrayList();
    int streamType;
    int upStream;

    /* JADX WARN: Incorrect condition in loop: B:20:0x0091 */
    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void parseDetail(java.nio.ByteBuffer r13) throws java.io.IOException {
        /*
            r12 = this;
            int r0 = com.coremedia.iso.IsoTypeReader.readUInt8(r13)
            r12.objectTypeIndication = r0
            int r0 = com.coremedia.iso.IsoTypeReader.readUInt8(r13)
            int r1 = r0 >>> 2
            r12.streamType = r1
            int r1 = r0 >> 1
            r1 = r1 & 1
            r12.upStream = r1
            int r1 = com.coremedia.iso.IsoTypeReader.readUInt24(r13)
            r12.bufferSizeDB = r1
            long r1 = com.coremedia.iso.IsoTypeReader.readUInt32(r13)
            r12.maxBitRate = r1
            long r1 = com.coremedia.iso.IsoTypeReader.readUInt32(r13)
            r12.avgBitRate = r1
            int r1 = r13.remaining()
            r2 = 0
            java.lang.String r3 = ", size: "
            r4 = 2
            if (r1 <= r4) goto L8d
            int r1 = r13.position()
            int r5 = r12.objectTypeIndication
            com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor r5 = com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory.createFrom(r5, r13)
            int r6 = r13.position()
            int r6 = r6 - r1
            java.util.logging.Logger r7 = com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor.log
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            java.lang.String r9 = " - DecoderConfigDescr1 read: "
            r8.append(r9)
            r8.append(r6)
            r8.append(r3)
            if (r5 == 0) goto L5f
            int r9 = r5.getSize()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            goto L60
        L5f:
            r9 = r2
        L60:
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r7.finer(r8)
            if (r5 == 0) goto L7b
            int r7 = r5.getSize()
            if (r6 >= r7) goto L7b
            int r8 = r7 - r6
            byte[] r8 = new byte[r8]
            r12.configDescriptorDeadBytes = r8
            r13.get(r8)
        L7b:
            boolean r7 = r5 instanceof com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderSpecificInfo
            if (r7 == 0) goto L84
            r7 = r5
            com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderSpecificInfo r7 = (com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderSpecificInfo) r7
            r12.decoderSpecificInfo = r7
        L84:
            boolean r7 = r5 instanceof com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig
            if (r7 == 0) goto L8d
            r7 = r5
            com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig r7 = (com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig) r7
            r12.audioSpecificInfo = r7
        L8d:
            int r1 = r13.remaining()
            if (r1 > r4) goto L94
            return
        L94:
            int r1 = r13.position()
            long r5 = (long) r1
            int r1 = r12.objectTypeIndication
            com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor r1 = com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory.createFrom(r1, r13)
            int r7 = r13.position()
            long r7 = (long) r7
            long r7 = r7 - r5
            java.util.logging.Logger r9 = com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor.log
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r1)
            java.lang.String r11 = " - DecoderConfigDescr2 read: "
            r10.append(r11)
            r10.append(r7)
            r10.append(r3)
            if (r1 == 0) goto Lc5
            int r11 = r1.getSize()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            goto Lc6
        Lc5:
            r11 = r2
        Lc6:
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r9.finer(r10)
            boolean r9 = r1 instanceof com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ProfileLevelIndicationDescriptor
            if (r9 == 0) goto Ldd
            java.util.List<com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ProfileLevelIndicationDescriptor> r9 = r12.profileLevelIndicationDescriptors
            r10 = r1
            com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ProfileLevelIndicationDescriptor r10 = (com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ProfileLevelIndicationDescriptor) r10
            r9.add(r10)
            goto L8d
        Ldd:
            goto L8d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor.parseDetail(java.nio.ByteBuffer):void");
    }

    public int serializedSize() {
        AudioSpecificConfig audioSpecificConfig = this.audioSpecificInfo;
        return (audioSpecificConfig == null ? 0 : audioSpecificConfig.serializedSize()) + 15;
    }

    public ByteBuffer serialize() {
        ByteBuffer out = ByteBuffer.allocate(serializedSize());
        IsoTypeWriter.writeUInt8(out, 4);
        IsoTypeWriter.writeUInt8(out, serializedSize() - 2);
        IsoTypeWriter.writeUInt8(out, this.objectTypeIndication);
        int flags = (this.streamType << 2) | (this.upStream << 1) | 1;
        IsoTypeWriter.writeUInt8(out, flags);
        IsoTypeWriter.writeUInt24(out, this.bufferSizeDB);
        IsoTypeWriter.writeUInt32(out, this.maxBitRate);
        IsoTypeWriter.writeUInt32(out, this.avgBitRate);
        AudioSpecificConfig audioSpecificConfig = this.audioSpecificInfo;
        if (audioSpecificConfig != null) {
            out.put(audioSpecificConfig.serialize().array());
        }
        return out;
    }

    public DecoderSpecificInfo getDecoderSpecificInfo() {
        return this.decoderSpecificInfo;
    }

    public AudioSpecificConfig getAudioSpecificInfo() {
        return this.audioSpecificInfo;
    }

    public void setAudioSpecificInfo(AudioSpecificConfig audioSpecificInfo) {
        this.audioSpecificInfo = audioSpecificInfo;
    }

    public List<ProfileLevelIndicationDescriptor> getProfileLevelIndicationDescriptors() {
        return this.profileLevelIndicationDescriptors;
    }

    public int getObjectTypeIndication() {
        return this.objectTypeIndication;
    }

    public void setObjectTypeIndication(int objectTypeIndication) {
        this.objectTypeIndication = objectTypeIndication;
    }

    public int getStreamType() {
        return this.streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public int getUpStream() {
        return this.upStream;
    }

    public void setUpStream(int upStream) {
        this.upStream = upStream;
    }

    public int getBufferSizeDB() {
        return this.bufferSizeDB;
    }

    public void setBufferSizeDB(int bufferSizeDB) {
        this.bufferSizeDB = bufferSizeDB;
    }

    public long getMaxBitRate() {
        return this.maxBitRate;
    }

    public void setMaxBitRate(long maxBitRate) {
        this.maxBitRate = maxBitRate;
    }

    public long getAvgBitRate() {
        return this.avgBitRate;
    }

    public void setAvgBitRate(long avgBitRate) {
        this.avgBitRate = avgBitRate;
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DecoderConfigDescriptor");
        sb.append("{objectTypeIndication=");
        sb.append(this.objectTypeIndication);
        sb.append(", streamType=");
        sb.append(this.streamType);
        sb.append(", upStream=");
        sb.append(this.upStream);
        sb.append(", bufferSizeDB=");
        sb.append(this.bufferSizeDB);
        sb.append(", maxBitRate=");
        sb.append(this.maxBitRate);
        sb.append(", avgBitRate=");
        sb.append(this.avgBitRate);
        sb.append(", decoderSpecificInfo=");
        sb.append(this.decoderSpecificInfo);
        sb.append(", audioSpecificInfo=");
        sb.append(this.audioSpecificInfo);
        sb.append(", configDescriptorDeadBytes=");
        byte[] bArr = this.configDescriptorDeadBytes;
        if (bArr == null) {
            bArr = new byte[0];
        }
        sb.append(Hex.encodeHex(bArr));
        sb.append(", profileLevelIndicationDescriptors=");
        List<ProfileLevelIndicationDescriptor> list = this.profileLevelIndicationDescriptors;
        sb.append(list == null ? "null" : Arrays.asList(list).toString());
        sb.append('}');
        return sb.toString();
    }
}
