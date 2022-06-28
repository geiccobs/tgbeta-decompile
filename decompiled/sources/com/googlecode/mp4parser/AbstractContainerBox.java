package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes3.dex */
public class AbstractContainerBox extends BasicContainer implements Box {
    protected boolean largeBox;
    private long offset;
    Container parent;
    protected String type;

    public AbstractContainerBox(String type) {
        this.type = type;
    }

    @Override // com.coremedia.iso.boxes.Box
    public Container getParent() {
        return this.parent;
    }

    @Override // com.coremedia.iso.boxes.Box
    public long getOffset() {
        return this.offset;
    }

    @Override // com.coremedia.iso.boxes.Box
    public void setParent(Container parent) {
        this.parent = parent;
    }

    public long getSize() {
        long s = getContainerSize();
        return ((this.largeBox || 8 + s >= 4294967296L) ? 16 : 8) + s;
    }

    @Override // com.coremedia.iso.boxes.Box
    public String getType() {
        return this.type;
    }

    public ByteBuffer getHeader() {
        ByteBuffer header;
        if (this.largeBox || getSize() >= 4294967296L) {
            byte[] bArr = new byte[16];
            bArr[3] = 1;
            bArr[4] = this.type.getBytes()[0];
            bArr[5] = this.type.getBytes()[1];
            bArr[6] = this.type.getBytes()[2];
            bArr[7] = this.type.getBytes()[3];
            header = ByteBuffer.wrap(bArr);
            header.position(8);
            IsoTypeWriter.writeUInt64(header, getSize());
        } else {
            header = ByteBuffer.wrap(new byte[]{0, 0, 0, 0, this.type.getBytes()[0], this.type.getBytes()[1], this.type.getBytes()[2], this.type.getBytes()[3]});
            IsoTypeWriter.writeUInt32(header, getSize());
        }
        header.rewind();
        return header;
    }

    public void parse(DataSource dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        this.offset = dataSource.position() - header.remaining();
        this.largeBox = header.remaining() == 16;
        initContainer(dataSource, contentSize, boxParser);
    }

    public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        writableByteChannel.write(getHeader());
        writeContainer(writableByteChannel);
    }

    @Override // com.googlecode.mp4parser.BasicContainer
    public void initContainer(DataSource dataSource, long containerSize, BoxParser boxParser) throws IOException {
        this.dataSource = dataSource;
        this.parsePosition = dataSource.position();
        this.startPosition = this.parsePosition - ((this.largeBox || 8 + containerSize >= 4294967296L) ? 16 : 8);
        dataSource.position(dataSource.position() + containerSize);
        this.endPosition = dataSource.position();
        this.boxParser = boxParser;
    }
}
