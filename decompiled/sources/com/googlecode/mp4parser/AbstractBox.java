package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.UserBox;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Logger;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes3.dex */
public abstract class AbstractBox implements Box {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static Logger LOG = Logger.getLogger(AbstractBox.class);
    private ByteBuffer content;
    long contentStartPosition;
    DataSource dataSource;
    long offset;
    private Container parent;
    protected String type;
    private byte[] userType;
    long memMapSize = -1;
    private ByteBuffer deadBytes = null;
    boolean isRead = true;
    boolean isParsed = true;

    protected abstract void _parseDetails(ByteBuffer byteBuffer);

    protected abstract void getContent(ByteBuffer byteBuffer);

    protected abstract long getContentSize();

    private synchronized void readContent() {
        if (!this.isRead) {
            try {
                Logger logger = LOG;
                logger.logDebug("mem mapping " + getType());
                this.content = this.dataSource.map(this.contentStartPosition, this.memMapSize);
                this.isRead = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override // com.coremedia.iso.boxes.Box
    public long getOffset() {
        return this.offset;
    }

    public AbstractBox(String type) {
        this.type = type;
    }

    public AbstractBox(String type, byte[] userType) {
        this.type = type;
        this.userType = userType;
    }

    @Override // com.coremedia.iso.boxes.Box
    @DoNotParseDetail
    public void parse(DataSource dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        long position = dataSource.position();
        this.contentStartPosition = position;
        this.offset = position - header.remaining();
        this.memMapSize = contentSize;
        this.dataSource = dataSource;
        dataSource.position(dataSource.position() + contentSize);
        this.isRead = false;
        this.isParsed = false;
    }

    @Override // com.coremedia.iso.boxes.Box
    public void getBox(WritableByteChannel os) throws IOException {
        int i = 8;
        int i2 = 0;
        int i3 = 16;
        if (this.isRead) {
            if (this.isParsed) {
                ByteBuffer bb = ByteBuffer.allocate(CastUtils.l2i(getSize()));
                getHeader(bb);
                getContent(bb);
                ByteBuffer byteBuffer = this.deadBytes;
                if (byteBuffer != null) {
                    byteBuffer.rewind();
                    while (this.deadBytes.remaining() > 0) {
                        bb.put(this.deadBytes);
                    }
                }
                os.write((ByteBuffer) bb.rewind());
                return;
            }
            if (!isSmallBox()) {
                i = 16;
            }
            if (!UserBox.TYPE.equals(getType())) {
                i3 = 0;
            }
            ByteBuffer header = ByteBuffer.allocate(i + i3);
            getHeader(header);
            os.write((ByteBuffer) header.rewind());
            os.write((ByteBuffer) this.content.position(0));
            return;
        }
        if (!isSmallBox()) {
            i = 16;
        }
        if (UserBox.TYPE.equals(getType())) {
            i2 = 16;
        }
        ByteBuffer header2 = ByteBuffer.allocate(i + i2);
        getHeader(header2);
        os.write((ByteBuffer) header2.rewind());
        this.dataSource.transferTo(this.contentStartPosition, this.memMapSize, os);
    }

    public final synchronized void parseDetails() {
        readContent();
        Logger logger = LOG;
        logger.logDebug("parsing details of " + getType());
        ByteBuffer content = this.content;
        if (content != null) {
            this.isParsed = true;
            content.rewind();
            _parseDetails(content);
            if (content.remaining() > 0) {
                this.deadBytes = content.slice();
            }
            this.content = null;
            if (!verify(content)) {
                throw new AssertionError();
            }
        }
    }

    protected void setDeadBytes(ByteBuffer newDeadBytes) {
        this.deadBytes = newDeadBytes;
    }

    @Override // com.coremedia.iso.boxes.Box
    public long getSize() {
        long size;
        int i = 0;
        if (!this.isRead) {
            size = this.memMapSize;
        } else if (this.isParsed) {
            size = getContentSize();
        } else {
            ByteBuffer byteBuffer = this.content;
            size = byteBuffer != null ? byteBuffer.limit() : 0;
        }
        long size2 = size + (size >= 4294967288L ? 8 : 0) + 8 + (UserBox.TYPE.equals(getType()) ? 16 : 0);
        ByteBuffer byteBuffer2 = this.deadBytes;
        if (byteBuffer2 != null) {
            i = byteBuffer2.limit();
        }
        return size2 + i;
    }

    @Override // com.coremedia.iso.boxes.Box
    @DoNotParseDetail
    public String getType() {
        return this.type;
    }

    @DoNotParseDetail
    public byte[] getUserType() {
        return this.userType;
    }

    @Override // com.coremedia.iso.boxes.Box
    @DoNotParseDetail
    public Container getParent() {
        return this.parent;
    }

    @Override // com.coremedia.iso.boxes.Box
    @DoNotParseDetail
    public void setParent(Container parent) {
        this.parent = parent;
    }

    public boolean isParsed() {
        return this.isParsed;
    }

    private boolean verify(ByteBuffer content) {
        ByteBuffer byteBuffer;
        ByteBuffer bb = ByteBuffer.allocate(CastUtils.l2i(getContentSize() + (this.deadBytes != null ? byteBuffer.limit() : 0)));
        getContent(bb);
        ByteBuffer byteBuffer2 = this.deadBytes;
        if (byteBuffer2 != null) {
            byteBuffer2.rewind();
            while (this.deadBytes.remaining() > 0) {
                bb.put(this.deadBytes);
            }
        }
        content.rewind();
        bb.rewind();
        if (content.remaining() != bb.remaining()) {
            System.err.print(String.valueOf(getType()) + ": remaining differs " + content.remaining() + " vs. " + bb.remaining());
            LOG.logError(String.valueOf(getType()) + ": remaining differs " + content.remaining() + " vs. " + bb.remaining());
            return false;
        }
        int p = content.position();
        int i = content.limit() - 1;
        int j = bb.limit() - 1;
        while (i >= p) {
            byte v1 = content.get(i);
            byte v2 = bb.get(j);
            if (v1 == v2) {
                i--;
                j--;
            } else {
                LOG.logError(String.format("%s: buffers differ at %d: %2X/%2X", getType(), Integer.valueOf(i), Byte.valueOf(v1), Byte.valueOf(v2)));
                byte[] b1 = new byte[content.remaining()];
                byte[] b2 = new byte[bb.remaining()];
                content.get(b1);
                bb.get(b2);
                System.err.println("original      : " + Hex.encodeHex(b1, 4));
                System.err.println("reconstructed : " + Hex.encodeHex(b2, 4));
                return false;
            }
        }
        return true;
    }

    private boolean isSmallBox() {
        int baseSize = 8;
        if (UserBox.TYPE.equals(getType())) {
            baseSize = 8 + 16;
        }
        if (!this.isRead) {
            return this.memMapSize + ((long) baseSize) < 4294967296L;
        } else if (!this.isParsed) {
            return ((long) (this.content.limit() + baseSize)) < 4294967296L;
        } else {
            long contentSize = getContentSize();
            ByteBuffer byteBuffer = this.deadBytes;
            return (contentSize + ((long) (byteBuffer != null ? byteBuffer.limit() : 0))) + ((long) baseSize) < 4294967296L;
        }
    }

    private void getHeader(ByteBuffer byteBuffer) {
        if (isSmallBox()) {
            IsoTypeWriter.writeUInt32(byteBuffer, getSize());
            byteBuffer.put(IsoFile.fourCCtoBytes(getType()));
        } else {
            IsoTypeWriter.writeUInt32(byteBuffer, 1L);
            byteBuffer.put(IsoFile.fourCCtoBytes(getType()));
            IsoTypeWriter.writeUInt64(byteBuffer, getSize());
        }
        if (UserBox.TYPE.equals(getType())) {
            byteBuffer.put(getUserType());
        }
    }

    @DoNotParseDetail
    public String getPath() {
        return Path.createPath(this);
    }
}
