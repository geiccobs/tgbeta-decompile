package com.mp4parser.iso14496.part30;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.googlecode.mp4parser.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes3.dex */
public class XMLSubtitleSampleEntry extends AbstractSampleEntry {
    public static final String TYPE = "stpp";
    private String namespace = "";
    private String schemaLocation = "";
    private String auxiliaryMimeTypes = "";

    public XMLSubtitleSampleEntry() {
        super(TYPE);
    }

    @Override // com.googlecode.mp4parser.AbstractContainerBox, com.coremedia.iso.boxes.Box
    public long getSize() {
        long s = getContainerSize();
        int i = 8;
        long t = this.namespace.length() + 8 + this.schemaLocation.length() + this.auxiliaryMimeTypes.length() + 3;
        long j = s + t;
        if (this.largeBox || s + t + 8 >= 4294967296L) {
            i = 16;
        }
        return j + i;
    }

    @Override // com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry, com.googlecode.mp4parser.AbstractContainerBox, com.coremedia.iso.boxes.Box
    public void parse(DataSource dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        dataSource.read((ByteBuffer) byteBuffer.rewind());
        byteBuffer.position(6);
        this.dataReferenceIndex = IsoTypeReader.readUInt16(byteBuffer);
        long start = dataSource.position();
        ByteBuffer content = ByteBuffer.allocate(1024);
        dataSource.read((ByteBuffer) content.rewind());
        String readString = IsoTypeReader.readString((ByteBuffer) content.rewind());
        this.namespace = readString;
        dataSource.position(readString.length() + start + 1);
        dataSource.read((ByteBuffer) content.rewind());
        this.schemaLocation = IsoTypeReader.readString((ByteBuffer) content.rewind());
        dataSource.position(this.namespace.length() + start + this.schemaLocation.length() + 2);
        dataSource.read((ByteBuffer) content.rewind());
        this.auxiliaryMimeTypes = IsoTypeReader.readString((ByteBuffer) content.rewind());
        dataSource.position(this.namespace.length() + start + this.schemaLocation.length() + this.auxiliaryMimeTypes.length() + 3);
        initContainer(dataSource, contentSize - ((((header.remaining() + this.namespace.length()) + this.schemaLocation.length()) + this.auxiliaryMimeTypes.length()) + 3), boxParser);
    }

    @Override // com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry, com.googlecode.mp4parser.AbstractContainerBox, com.coremedia.iso.boxes.Box
    public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        writableByteChannel.write(getHeader());
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.namespace.length() + 8 + this.schemaLocation.length() + this.auxiliaryMimeTypes.length() + 3);
        byteBuffer.position(6);
        IsoTypeWriter.writeUInt16(byteBuffer, this.dataReferenceIndex);
        IsoTypeWriter.writeZeroTermUtf8String(byteBuffer, this.namespace);
        IsoTypeWriter.writeZeroTermUtf8String(byteBuffer, this.schemaLocation);
        IsoTypeWriter.writeZeroTermUtf8String(byteBuffer, this.auxiliaryMimeTypes);
        writableByteChannel.write((ByteBuffer) byteBuffer.rewind());
        writeContainer(writableByteChannel);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getAuxiliaryMimeTypes() {
        return this.auxiliaryMimeTypes;
    }

    public void setAuxiliaryMimeTypes(String auxiliaryMimeTypes) {
        this.auxiliaryMimeTypes = auxiliaryMimeTypes;
    }
}
