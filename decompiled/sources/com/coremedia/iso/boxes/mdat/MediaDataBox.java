package com.coremedia.iso.boxes.mdat;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Logger;
/* loaded from: classes3.dex */
public final class MediaDataBox implements Box {
    private static Logger LOG = Logger.getLogger(MediaDataBox.class.getName());
    public static final String TYPE = "mdat";
    private DataSource dataSource;
    boolean largeBox = false;
    private long offset;
    Container parent;
    private long size;

    @Override // com.coremedia.iso.boxes.Box
    public Container getParent() {
        return this.parent;
    }

    @Override // com.coremedia.iso.boxes.Box
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override // com.coremedia.iso.boxes.Box
    public String getType() {
        return TYPE;
    }

    private static void transfer(DataSource from, long position, long count, WritableByteChannel to) throws IOException {
        long offset = 0;
        while (offset < count) {
            offset += from.transferTo(position + offset, Math.min(67076096L, count - offset), to);
        }
    }

    @Override // com.coremedia.iso.boxes.Box
    public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        transfer(this.dataSource, this.offset, this.size, writableByteChannel);
    }

    @Override // com.coremedia.iso.boxes.Box
    public long getSize() {
        return this.size;
    }

    @Override // com.coremedia.iso.boxes.Box
    public long getOffset() {
        return this.offset;
    }

    @Override // com.coremedia.iso.boxes.Box
    public void parse(DataSource dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        this.offset = dataSource.position() - header.remaining();
        this.dataSource = dataSource;
        this.size = header.remaining() + contentSize;
        dataSource.position(dataSource.position() + contentSize);
    }

    public String toString() {
        return "MediaDataBox{size=" + this.size + '}';
    }
}
