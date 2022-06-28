package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class InitialObjectDescriptor extends ObjectDescriptorBase {
    int audioProfileLevelIndication;
    int graphicsProfileLevelIndication;
    int includeInlineProfileLevelFlag;
    int oDProfileLevelIndication;
    private int objectDescriptorId;
    int sceneProfileLevelIndication;
    int urlFlag;
    int urlLength;
    String urlString;
    int visualProfileLevelIndication;
    List<ESDescriptor> esDescriptors = new ArrayList();
    List<ExtensionDescriptor> extensionDescriptors = new ArrayList();
    List<BaseDescriptor> unknownDescriptors = new ArrayList();

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public void parseDetail(ByteBuffer bb) throws IOException {
        int sizeLeft;
        int data = IsoTypeReader.readUInt16(bb);
        this.objectDescriptorId = (65472 & data) >> 6;
        this.urlFlag = (data & 63) >> 5;
        this.includeInlineProfileLevelFlag = (data & 31) >> 4;
        int sizeLeft2 = getSize() - 2;
        if (this.urlFlag == 1) {
            int readUInt8 = IsoTypeReader.readUInt8(bb);
            this.urlLength = readUInt8;
            this.urlString = IsoTypeReader.readString(bb, readUInt8);
            sizeLeft = sizeLeft2 - (this.urlLength + 1);
        } else {
            this.oDProfileLevelIndication = IsoTypeReader.readUInt8(bb);
            this.sceneProfileLevelIndication = IsoTypeReader.readUInt8(bb);
            this.audioProfileLevelIndication = IsoTypeReader.readUInt8(bb);
            this.visualProfileLevelIndication = IsoTypeReader.readUInt8(bb);
            this.graphicsProfileLevelIndication = IsoTypeReader.readUInt8(bb);
            sizeLeft = sizeLeft2 - 5;
            if (sizeLeft > 2) {
                BaseDescriptor descriptor = ObjectDescriptorFactory.createFrom(-1, bb);
                sizeLeft -= descriptor.getSize();
                if (descriptor instanceof ESDescriptor) {
                    this.esDescriptors.add((ESDescriptor) descriptor);
                } else {
                    this.unknownDescriptors.add(descriptor);
                }
            }
        }
        if (sizeLeft > 2) {
            BaseDescriptor descriptor2 = ObjectDescriptorFactory.createFrom(-1, bb);
            if (descriptor2 instanceof ExtensionDescriptor) {
                this.extensionDescriptors.add((ExtensionDescriptor) descriptor2);
            } else {
                this.unknownDescriptors.add(descriptor2);
            }
        }
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public String toString() {
        return "InitialObjectDescriptor{objectDescriptorId=" + this.objectDescriptorId + ", urlFlag=" + this.urlFlag + ", includeInlineProfileLevelFlag=" + this.includeInlineProfileLevelFlag + ", urlLength=" + this.urlLength + ", urlString='" + this.urlString + "', oDProfileLevelIndication=" + this.oDProfileLevelIndication + ", sceneProfileLevelIndication=" + this.sceneProfileLevelIndication + ", audioProfileLevelIndication=" + this.audioProfileLevelIndication + ", visualProfileLevelIndication=" + this.visualProfileLevelIndication + ", graphicsProfileLevelIndication=" + this.graphicsProfileLevelIndication + ", esDescriptors=" + this.esDescriptors + ", extensionDescriptors=" + this.extensionDescriptors + ", unknownDescriptors=" + this.unknownDescriptors + '}';
    }
}
