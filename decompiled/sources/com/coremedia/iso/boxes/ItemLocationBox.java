package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.BasePermissionsActivity;
/* loaded from: classes3.dex */
public class ItemLocationBox extends AbstractFullBox {
    public static final String TYPE = "iloc";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_11 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_9 = null;
    public int offsetSize = 8;
    public int lengthSize = 8;
    public int baseOffsetSize = 8;
    public int indexSize = 0;
    public List<Item> items = new LinkedList();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("ItemLocationBox.java", ItemLocationBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getOffsetSize", "com.coremedia.iso.boxes.ItemLocationBox", "", "", "", "int"), 119);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setOffsetSize", "com.coremedia.iso.boxes.ItemLocationBox", "int", "offsetSize", "", "void"), 123);
        ajc$tjp_10 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "createItem", "com.coremedia.iso.boxes.ItemLocationBox", "int:int:int:long:java.util.List", "itemId:constructionMethod:dataReferenceIndex:baseOffset:extents", "", "com.coremedia.iso.boxes.ItemLocationBox$Item"), 160);
        ajc$tjp_11 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "createExtent", "com.coremedia.iso.boxes.ItemLocationBox", "long:long:long", "extentOffset:extentLength:extentIndex", "", "com.coremedia.iso.boxes.ItemLocationBox$Extent"), 285);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getLengthSize", "com.coremedia.iso.boxes.ItemLocationBox", "", "", "", "int"), 127);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setLengthSize", "com.coremedia.iso.boxes.ItemLocationBox", "int", "lengthSize", "", "void"), 131);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getBaseOffsetSize", "com.coremedia.iso.boxes.ItemLocationBox", "", "", "", "int"), TsExtractor.TS_STREAM_TYPE_E_AC3);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setBaseOffsetSize", "com.coremedia.iso.boxes.ItemLocationBox", "int", "baseOffsetSize", "", "void"), 139);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getIndexSize", "com.coremedia.iso.boxes.ItemLocationBox", "", "", "", "int"), TLRPC.LAYER);
        ajc$tjp_7 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setIndexSize", "com.coremedia.iso.boxes.ItemLocationBox", "int", "indexSize", "", "void"), 147);
        ajc$tjp_8 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getItems", "com.coremedia.iso.boxes.ItemLocationBox", "", "", "", "java.util.List"), BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR);
        ajc$tjp_9 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setItems", "com.coremedia.iso.boxes.ItemLocationBox", "java.util.List", "items", "", "void"), 155);
    }

    public ItemLocationBox() {
        super(TYPE);
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected long getContentSize() {
        long size = 8;
        for (Item item : this.items) {
            size += item.getSize();
        }
        return size;
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt8(byteBuffer, (this.offsetSize << 4) | this.lengthSize);
        if (getVersion() == 1) {
            IsoTypeWriter.writeUInt8(byteBuffer, (this.baseOffsetSize << 4) | this.indexSize);
        } else {
            IsoTypeWriter.writeUInt8(byteBuffer, this.baseOffsetSize << 4);
        }
        IsoTypeWriter.writeUInt16(byteBuffer, this.items.size());
        for (Item item : this.items) {
            item.getContent(byteBuffer);
        }
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        int tmp = IsoTypeReader.readUInt8(content);
        this.offsetSize = tmp >>> 4;
        this.lengthSize = tmp & 15;
        int tmp2 = IsoTypeReader.readUInt8(content);
        this.baseOffsetSize = tmp2 >>> 4;
        if (getVersion() == 1) {
            this.indexSize = tmp2 & 15;
        }
        int itemCount = IsoTypeReader.readUInt16(content);
        for (int i = 0; i < itemCount; i++) {
            this.items.add(new Item(content));
        }
    }

    public int getOffsetSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.offsetSize;
    }

    public void setOffsetSize(int offsetSize) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(offsetSize)));
        this.offsetSize = offsetSize;
    }

    public int getLengthSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.lengthSize;
    }

    public void setLengthSize(int lengthSize) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(lengthSize)));
        this.lengthSize = lengthSize;
    }

    public int getBaseOffsetSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return this.baseOffsetSize;
    }

    public void setBaseOffsetSize(int baseOffsetSize) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(baseOffsetSize)));
        this.baseOffsetSize = baseOffsetSize;
    }

    public int getIndexSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        return this.indexSize;
    }

    public void setIndexSize(int indexSize) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(indexSize)));
        this.indexSize = indexSize;
    }

    public List<Item> getItems() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this));
        return this.items;
    }

    public void setItems(List<Item> list) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, this, this, list));
        this.items = list;
    }

    public Item createItem(int itemId, int constructionMethod, int dataReferenceIndex, long baseOffset, List<Extent> list) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, (Object) this, (Object) this, new Object[]{Conversions.intObject(itemId), Conversions.intObject(constructionMethod), Conversions.intObject(dataReferenceIndex), Conversions.longObject(baseOffset), list}));
        return new Item(itemId, constructionMethod, dataReferenceIndex, baseOffset, list);
    }

    Item createItem(ByteBuffer bb) {
        return new Item(bb);
    }

    /* loaded from: classes3.dex */
    public class Item {
        public long baseOffset;
        public int constructionMethod;
        public int dataReferenceIndex;
        public List<Extent> extents;
        public int itemId;

        public Item(ByteBuffer in) {
            ItemLocationBox.this = r5;
            this.extents = new LinkedList();
            this.itemId = IsoTypeReader.readUInt16(in);
            if (r5.getVersion() == 1) {
                int tmp = IsoTypeReader.readUInt16(in);
                this.constructionMethod = tmp & 15;
            }
            int tmp2 = IsoTypeReader.readUInt16(in);
            this.dataReferenceIndex = tmp2;
            if (r5.baseOffsetSize > 0) {
                this.baseOffset = IsoTypeReaderVariable.read(in, r5.baseOffsetSize);
            } else {
                this.baseOffset = 0L;
            }
            int extentCount = IsoTypeReader.readUInt16(in);
            for (int i = 0; i < extentCount; i++) {
                this.extents.add(new Extent(in));
            }
        }

        public Item(int itemId, int constructionMethod, int dataReferenceIndex, long baseOffset, List<Extent> extents) {
            ItemLocationBox.this = r1;
            this.extents = new LinkedList();
            this.itemId = itemId;
            this.constructionMethod = constructionMethod;
            this.dataReferenceIndex = dataReferenceIndex;
            this.baseOffset = baseOffset;
            this.extents = extents;
        }

        public int getSize() {
            int size = 2;
            if (ItemLocationBox.this.getVersion() == 1) {
                size = 2 + 2;
            }
            int size2 = size + 2 + ItemLocationBox.this.baseOffsetSize + 2;
            for (Extent extent : this.extents) {
                size2 += extent.getSize();
            }
            return size2;
        }

        public void setBaseOffset(long baseOffset) {
            this.baseOffset = baseOffset;
        }

        public void getContent(ByteBuffer bb) {
            IsoTypeWriter.writeUInt16(bb, this.itemId);
            if (ItemLocationBox.this.getVersion() == 1) {
                IsoTypeWriter.writeUInt16(bb, this.constructionMethod);
            }
            IsoTypeWriter.writeUInt16(bb, this.dataReferenceIndex);
            if (ItemLocationBox.this.baseOffsetSize > 0) {
                IsoTypeWriterVariable.write(this.baseOffset, bb, ItemLocationBox.this.baseOffsetSize);
            }
            IsoTypeWriter.writeUInt16(bb, this.extents.size());
            for (Extent extent : this.extents) {
                extent.getContent(bb);
            }
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Item item = (Item) o;
            if (this.baseOffset != item.baseOffset || this.constructionMethod != item.constructionMethod || this.dataReferenceIndex != item.dataReferenceIndex || this.itemId != item.itemId) {
                return false;
            }
            List<Extent> list = this.extents;
            if (list == null ? item.extents == null : list.equals(item.extents)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.itemId;
            int result2 = (result * 31) + this.constructionMethod;
            long j = this.baseOffset;
            int result3 = ((((result2 * 31) + this.dataReferenceIndex) * 31) + ((int) (j ^ (j >>> 32)))) * 31;
            List<Extent> list = this.extents;
            return result3 + (list != null ? list.hashCode() : 0);
        }

        public String toString() {
            return "Item{baseOffset=" + this.baseOffset + ", itemId=" + this.itemId + ", constructionMethod=" + this.constructionMethod + ", dataReferenceIndex=" + this.dataReferenceIndex + ", extents=" + this.extents + '}';
        }
    }

    public Extent createExtent(long extentOffset, long extentLength, long extentIndex) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, (Object) this, (Object) this, new Object[]{Conversions.longObject(extentOffset), Conversions.longObject(extentLength), Conversions.longObject(extentIndex)}));
        return new Extent(extentOffset, extentLength, extentIndex);
    }

    Extent createExtent(ByteBuffer bb) {
        return new Extent(bb);
    }

    /* loaded from: classes3.dex */
    public class Extent {
        public long extentIndex;
        public long extentLength;
        public long extentOffset;

        public Extent(long extentOffset, long extentLength, long extentIndex) {
            ItemLocationBox.this = r1;
            this.extentOffset = extentOffset;
            this.extentLength = extentLength;
            this.extentIndex = extentIndex;
        }

        public Extent(ByteBuffer in) {
            ItemLocationBox.this = r3;
            if (r3.getVersion() == 1 && r3.indexSize > 0) {
                this.extentIndex = IsoTypeReaderVariable.read(in, r3.indexSize);
            }
            this.extentOffset = IsoTypeReaderVariable.read(in, r3.offsetSize);
            this.extentLength = IsoTypeReaderVariable.read(in, r3.lengthSize);
        }

        public void getContent(ByteBuffer os) {
            if (ItemLocationBox.this.getVersion() == 1 && ItemLocationBox.this.indexSize > 0) {
                IsoTypeWriterVariable.write(this.extentIndex, os, ItemLocationBox.this.indexSize);
            }
            IsoTypeWriterVariable.write(this.extentOffset, os, ItemLocationBox.this.offsetSize);
            IsoTypeWriterVariable.write(this.extentLength, os, ItemLocationBox.this.lengthSize);
        }

        public int getSize() {
            return (ItemLocationBox.this.indexSize > 0 ? ItemLocationBox.this.indexSize : 0) + ItemLocationBox.this.offsetSize + ItemLocationBox.this.lengthSize;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Extent extent = (Extent) o;
            if (this.extentIndex == extent.extentIndex && this.extentLength == extent.extentLength && this.extentOffset == extent.extentOffset) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            long j = this.extentOffset;
            int result = (int) (j ^ (j >>> 32));
            long j2 = this.extentLength;
            int result2 = (result * 31) + ((int) (j2 ^ (j2 >>> 32)));
            long j3 = this.extentIndex;
            return (result2 * 31) + ((int) (j3 ^ (j3 >>> 32)));
        }

        public String toString() {
            return "Extent{extentOffset=" + this.extentOffset + ", extentLength=" + this.extentLength + ", extentIndex=" + this.extentIndex + '}';
        }
    }
}
