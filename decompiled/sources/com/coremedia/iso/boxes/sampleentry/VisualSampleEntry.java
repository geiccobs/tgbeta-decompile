package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes3.dex */
public final class VisualSampleEntry extends AbstractSampleEntry implements Container {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String TYPE1 = "mp4v";
    public static final String TYPE2 = "s263";
    public static final String TYPE3 = "avc1";
    public static final String TYPE4 = "avc3";
    public static final String TYPE5 = "drmi";
    public static final String TYPE6 = "hvc1";
    public static final String TYPE7 = "hev1";
    public static final String TYPE_ENCRYPTED = "encv";
    private String compressorname;
    private int depth;
    private int frameCount;
    private int height;
    private double horizresolution;
    private long[] predefined;
    private double vertresolution;
    private int width;

    public VisualSampleEntry() {
        super(TYPE3);
        this.horizresolution = 72.0d;
        this.vertresolution = 72.0d;
        this.frameCount = 1;
        this.compressorname = "";
        this.depth = 24;
        this.predefined = new long[3];
    }

    public VisualSampleEntry(String type) {
        super(type);
        this.horizresolution = 72.0d;
        this.vertresolution = 72.0d;
        this.frameCount = 1;
        this.compressorname = "";
        this.depth = 24;
        this.predefined = new long[3];
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getHorizresolution() {
        return this.horizresolution;
    }

    public void setHorizresolution(double horizresolution) {
        this.horizresolution = horizresolution;
    }

    public double getVertresolution() {
        return this.vertresolution;
    }

    public void setVertresolution(double vertresolution) {
        this.vertresolution = vertresolution;
    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public String getCompressorname() {
        return this.compressorname;
    }

    public void setCompressorname(String compressorname) {
        this.compressorname = compressorname;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override // com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry, com.googlecode.mp4parser.AbstractContainerBox, com.coremedia.iso.boxes.Box
    public void parse(final DataSource dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        final long endPosition = dataSource.position() + contentSize;
        ByteBuffer content = ByteBuffer.allocate(78);
        dataSource.read(content);
        content.position(6);
        this.dataReferenceIndex = IsoTypeReader.readUInt16(content);
        long tmp = IsoTypeReader.readUInt16(content);
        if (0 != tmp) {
            throw new AssertionError("reserved byte not 0");
        }
        long tmp2 = IsoTypeReader.readUInt16(content);
        if (0 != tmp2) {
            throw new AssertionError("reserved byte not 0");
        }
        this.predefined[0] = IsoTypeReader.readUInt32(content);
        this.predefined[1] = IsoTypeReader.readUInt32(content);
        this.predefined[2] = IsoTypeReader.readUInt32(content);
        this.width = IsoTypeReader.readUInt16(content);
        this.height = IsoTypeReader.readUInt16(content);
        this.horizresolution = IsoTypeReader.readFixedPoint1616(content);
        this.vertresolution = IsoTypeReader.readFixedPoint1616(content);
        long tmp3 = IsoTypeReader.readUInt32(content);
        if (0 == tmp3) {
            this.frameCount = IsoTypeReader.readUInt16(content);
            int compressornameDisplayAbleData = IsoTypeReader.readUInt8(content);
            if (compressornameDisplayAbleData > 31) {
                compressornameDisplayAbleData = 31;
            }
            byte[] bytes = new byte[compressornameDisplayAbleData];
            content.get(bytes);
            this.compressorname = Utf8.convert(bytes);
            if (compressornameDisplayAbleData < 31) {
                byte[] zeros = new byte[31 - compressornameDisplayAbleData];
                content.get(zeros);
            }
            this.depth = IsoTypeReader.readUInt16(content);
            long tmp4 = IsoTypeReader.readUInt16(content);
            if (65535 != tmp4) {
                throw new AssertionError();
            }
            DataSource dsLimited = new DataSource() { // from class: com.coremedia.iso.boxes.sampleentry.VisualSampleEntry.1
                @Override // com.googlecode.mp4parser.DataSource
                public int read(ByteBuffer byteBuffer) throws IOException {
                    if (endPosition == dataSource.position()) {
                        return -1;
                    }
                    if (byteBuffer.remaining() > endPosition - dataSource.position()) {
                        ByteBuffer bb = ByteBuffer.allocate(CastUtils.l2i(endPosition - dataSource.position()));
                        dataSource.read(bb);
                        byteBuffer.put((ByteBuffer) bb.rewind());
                        return bb.capacity();
                    }
                    return dataSource.read(byteBuffer);
                }

                @Override // com.googlecode.mp4parser.DataSource
                public long size() throws IOException {
                    return endPosition;
                }

                @Override // com.googlecode.mp4parser.DataSource
                public long position() throws IOException {
                    return dataSource.position();
                }

                @Override // com.googlecode.mp4parser.DataSource
                public void position(long nuPos) throws IOException {
                    dataSource.position(nuPos);
                }

                @Override // com.googlecode.mp4parser.DataSource
                public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
                    return dataSource.transferTo(position, count, target);
                }

                @Override // com.googlecode.mp4parser.DataSource
                public ByteBuffer map(long startPosition, long size) throws IOException {
                    return dataSource.map(startPosition, size);
                }

                @Override // com.googlecode.mp4parser.DataSource, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    dataSource.close();
                }
            };
            initContainer(dsLimited, contentSize - 78, boxParser);
            return;
        }
        throw new AssertionError("reserved byte not 0");
    }

    @Override // com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry, com.googlecode.mp4parser.AbstractContainerBox, com.coremedia.iso.boxes.Box
    public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        writableByteChannel.write(getHeader());
        ByteBuffer byteBuffer = ByteBuffer.allocate(78);
        byteBuffer.position(6);
        IsoTypeWriter.writeUInt16(byteBuffer, this.dataReferenceIndex);
        IsoTypeWriter.writeUInt16(byteBuffer, 0);
        IsoTypeWriter.writeUInt16(byteBuffer, 0);
        IsoTypeWriter.writeUInt32(byteBuffer, this.predefined[0]);
        IsoTypeWriter.writeUInt32(byteBuffer, this.predefined[1]);
        IsoTypeWriter.writeUInt32(byteBuffer, this.predefined[2]);
        IsoTypeWriter.writeUInt16(byteBuffer, getWidth());
        IsoTypeWriter.writeUInt16(byteBuffer, getHeight());
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, getHorizresolution());
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, getVertresolution());
        IsoTypeWriter.writeUInt32(byteBuffer, 0L);
        IsoTypeWriter.writeUInt16(byteBuffer, getFrameCount());
        IsoTypeWriter.writeUInt8(byteBuffer, Utf8.utf8StringLengthInBytes(getCompressorname()));
        byteBuffer.put(Utf8.convert(getCompressorname()));
        int a = Utf8.utf8StringLengthInBytes(getCompressorname());
        while (a < 31) {
            a++;
            byteBuffer.put((byte) 0);
        }
        IsoTypeWriter.writeUInt16(byteBuffer, getDepth());
        IsoTypeWriter.writeUInt16(byteBuffer, 65535);
        writableByteChannel.write((ByteBuffer) byteBuffer.rewind());
        writeContainer(writableByteChannel);
    }

    @Override // com.googlecode.mp4parser.AbstractContainerBox, com.coremedia.iso.boxes.Box
    public long getSize() {
        long s = getContainerSize();
        return s + 78 + ((this.largeBox || (s + 78) + 8 >= 4294967296L) ? 16 : 8);
    }
}
