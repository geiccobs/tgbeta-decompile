package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.LazyList;
import com.googlecode.mp4parser.util.Logger;
import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.wrappers.C$r8$wrapper$java$util$function$Consumer$VWRP;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
/* loaded from: classes3.dex */
public class BasicContainer implements Container, Iterator<Box>, Closeable, j$.util.Iterator {
    private static final Box EOF = new AbstractBox("eof ") { // from class: com.googlecode.mp4parser.BasicContainer.1
        @Override // com.googlecode.mp4parser.AbstractBox
        protected long getContentSize() {
            return 0L;
        }

        @Override // com.googlecode.mp4parser.AbstractBox
        protected void getContent(ByteBuffer byteBuffer) {
        }

        @Override // com.googlecode.mp4parser.AbstractBox
        protected void _parseDetails(ByteBuffer content) {
        }
    };
    private static Logger LOG = Logger.getLogger(BasicContainer.class);
    protected BoxParser boxParser;
    protected DataSource dataSource;
    Box lookahead = null;
    long parsePosition = 0;
    long startPosition = 0;
    long endPosition = 0;
    private List<Box> boxes = new ArrayList();

    @Override // j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    @Override // java.util.Iterator
    public /* synthetic */ void forEachRemaining(java.util.function.Consumer<? super Box> consumer) {
        forEachRemaining(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    @Override // com.coremedia.iso.boxes.Container
    public List<Box> getBoxes() {
        if (this.dataSource != null && this.lookahead != EOF) {
            return new LazyList(this.boxes, this);
        }
        return this.boxes;
    }

    @Override // com.coremedia.iso.boxes.Container
    public void setBoxes(List<Box> boxes) {
        this.boxes = new ArrayList(boxes);
        this.lookahead = EOF;
        this.dataSource = null;
    }

    public long getContainerSize() {
        long contentSize = 0;
        for (int i = 0; i < getBoxes().size(); i++) {
            contentSize += this.boxes.get(i).getSize();
        }
        return contentSize;
    }

    @Override // com.coremedia.iso.boxes.Container
    public <T extends Box> List<T> getBoxes(Class<T> clazz) {
        ArrayList arrayList = null;
        Box box = null;
        List<Box> boxes = getBoxes();
        for (int i = 0; i < boxes.size(); i++) {
            Box boxe = boxes.get(i);
            if (clazz.isInstance(boxe)) {
                if (box == null) {
                    box = boxe;
                } else {
                    if (arrayList == null) {
                        arrayList = new ArrayList(2);
                        arrayList.add(box);
                    }
                    arrayList.add(boxe);
                }
            }
        }
        if (arrayList != null) {
            return arrayList;
        }
        if (box != null) {
            return Collections.singletonList(box);
        }
        return Collections.emptyList();
    }

    @Override // com.coremedia.iso.boxes.Container
    public <T extends Box> List<T> getBoxes(Class<T> clazz, boolean recursive) {
        ArrayList arrayList = new ArrayList(2);
        List<Box> boxes = getBoxes();
        for (int i = 0; i < boxes.size(); i++) {
            Box boxe = boxes.get(i);
            if (clazz.isInstance(boxe)) {
                arrayList.add(boxe);
            }
            if (recursive && (boxe instanceof Container)) {
                arrayList.addAll(((Container) boxe).getBoxes(clazz, recursive));
            }
        }
        return arrayList;
    }

    public void addBox(Box box) {
        if (box != null) {
            this.boxes = new ArrayList(getBoxes());
            box.setParent(this);
            this.boxes.add(box);
        }
    }

    public void initContainer(DataSource dataSource, long containerSize, BoxParser boxParser) throws IOException {
        this.dataSource = dataSource;
        long position = dataSource.position();
        this.startPosition = position;
        this.parsePosition = position;
        dataSource.position(dataSource.position() + containerSize);
        this.endPosition = dataSource.position();
        this.boxParser = boxParser;
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public boolean hasNext() {
        Box box = this.lookahead;
        if (box == EOF) {
            return false;
        }
        if (box != null) {
            return true;
        }
        try {
            this.lookahead = next();
            return true;
        } catch (NoSuchElementException e) {
            this.lookahead = EOF;
            return false;
        }
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public Box next() {
        Box b;
        Box box = this.lookahead;
        if (box != null && box != EOF) {
            Box b2 = this.lookahead;
            this.lookahead = null;
            return b2;
        }
        DataSource dataSource = this.dataSource;
        if (dataSource == null || this.parsePosition >= this.endPosition) {
            this.lookahead = EOF;
            throw new NoSuchElementException();
        }
        try {
            synchronized (dataSource) {
                this.dataSource.position(this.parsePosition);
                b = this.boxParser.parseBox(this.dataSource, this);
                this.parsePosition = this.dataSource.position();
            }
            return b;
        } catch (EOFException e) {
            throw new NoSuchElementException();
        } catch (IOException e2) {
            throw new NoSuchElementException();
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getClass().getSimpleName());
        buffer.append("[");
        for (int i = 0; i < this.boxes.size(); i++) {
            if (i > 0) {
                buffer.append(";");
            }
            buffer.append(this.boxes.get(i).toString());
        }
        buffer.append("]");
        return buffer.toString();
    }

    @Override // com.coremedia.iso.boxes.Container
    public final void writeContainer(WritableByteChannel bb) throws IOException {
        for (Box box : getBoxes()) {
            box.getBox(bb);
        }
    }

    @Override // com.coremedia.iso.boxes.Container
    public ByteBuffer getByteBuffer(long rangeStart, long size) throws IOException {
        ByteBuffer map;
        DataSource dataSource = this.dataSource;
        if (dataSource != null) {
            synchronized (dataSource) {
                map = this.dataSource.map(this.startPosition + rangeStart, size);
            }
            return map;
        }
        ByteBuffer out = ByteBuffer.allocate(CastUtils.l2i(size));
        long rangeEnd = rangeStart + size;
        long boxEnd = 0;
        for (Box box : this.boxes) {
            long boxStart = boxEnd;
            boxEnd = boxStart + box.getSize();
            if (boxEnd > rangeStart && boxStart < rangeEnd) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                WritableByteChannel wbc = Channels.newChannel(baos);
                box.getBox(wbc);
                wbc.close();
                if (boxStart >= rangeStart && boxEnd <= rangeEnd) {
                    out.put(baos.toByteArray());
                } else if (boxStart < rangeStart && boxEnd > rangeEnd) {
                    int length = CastUtils.l2i((box.getSize() - (rangeStart - boxStart)) - (boxEnd - rangeEnd));
                    out.put(baos.toByteArray(), CastUtils.l2i(rangeStart - boxStart), length);
                } else if (boxStart < rangeStart && boxEnd <= rangeEnd) {
                    int length2 = CastUtils.l2i(box.getSize() - (rangeStart - boxStart));
                    out.put(baos.toByteArray(), CastUtils.l2i(rangeStart - boxStart), length2);
                } else if (boxStart >= rangeStart && boxEnd > rangeEnd) {
                    int length3 = CastUtils.l2i(box.getSize() - (boxEnd - rangeEnd));
                    out.put(baos.toByteArray(), 0, length3);
                }
            }
        }
        return (ByteBuffer) out.rewind();
    }

    public void close() throws IOException {
        this.dataSource.close();
    }
}
