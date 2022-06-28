package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;
/* loaded from: classes3.dex */
public class EditListBox extends AbstractFullBox {
    public static final String TYPE = "elst";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private List<Entry> entries = new LinkedList();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("EditListBox.java", EditListBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getEntries", "com.coremedia.iso.boxes.EditListBox", "", "", "", "java.util.List"), 68);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setEntries", "com.coremedia.iso.boxes.EditListBox", "java.util.List", RemoteConfigConstants.ResponseFieldKey.ENTRIES, "", "void"), 72);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "toString", "com.coremedia.iso.boxes.EditListBox", "", "", "", "java.lang.String"), 108);
    }

    public EditListBox() {
        super(TYPE);
    }

    public List<Entry> getEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.entries;
    }

    public void setEntries(List<Entry> list) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this, list));
        this.entries = list;
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected long getContentSize() {
        if (getVersion() == 1) {
            long contentSize = 8 + (this.entries.size() * 20);
            return contentSize;
        }
        long contentSize2 = 8 + (this.entries.size() * 12);
        return contentSize2;
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        int entryCount = CastUtils.l2i(IsoTypeReader.readUInt32(content));
        this.entries = new LinkedList();
        for (int i = 0; i < entryCount; i++) {
            this.entries.add(new Entry(this, content));
        }
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, this.entries.size());
        for (Entry entry : this.entries) {
            entry.getContent(byteBuffer);
        }
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "EditListBox{entries=" + this.entries + '}';
    }

    /* loaded from: classes3.dex */
    public static class Entry {
        EditListBox editListBox;
        private double mediaRate;
        private long mediaTime;
        private long segmentDuration;

        public Entry(EditListBox editListBox, long segmentDuration, long mediaTime, double mediaRate) {
            this.segmentDuration = segmentDuration;
            this.mediaTime = mediaTime;
            this.mediaRate = mediaRate;
            this.editListBox = editListBox;
        }

        public Entry(EditListBox editListBox, ByteBuffer bb) {
            if (editListBox.getVersion() == 1) {
                this.segmentDuration = IsoTypeReader.readUInt64(bb);
                this.mediaTime = bb.getLong();
                this.mediaRate = IsoTypeReader.readFixedPoint1616(bb);
            } else {
                this.segmentDuration = IsoTypeReader.readUInt32(bb);
                this.mediaTime = bb.getInt();
                this.mediaRate = IsoTypeReader.readFixedPoint1616(bb);
            }
            this.editListBox = editListBox;
        }

        public long getSegmentDuration() {
            return this.segmentDuration;
        }

        public void setSegmentDuration(long segmentDuration) {
            this.segmentDuration = segmentDuration;
        }

        public long getMediaTime() {
            return this.mediaTime;
        }

        public void setMediaTime(long mediaTime) {
            this.mediaTime = mediaTime;
        }

        public double getMediaRate() {
            return this.mediaRate;
        }

        public void setMediaRate(double mediaRate) {
            this.mediaRate = mediaRate;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Entry entry = (Entry) o;
            if (this.mediaTime == entry.mediaTime && this.segmentDuration == entry.segmentDuration) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            long j = this.segmentDuration;
            int result = (int) (j ^ (j >>> 32));
            long j2 = this.mediaTime;
            return (result * 31) + ((int) (j2 ^ (j2 >>> 32)));
        }

        public void getContent(ByteBuffer bb) {
            if (this.editListBox.getVersion() == 1) {
                IsoTypeWriter.writeUInt64(bb, this.segmentDuration);
                bb.putLong(this.mediaTime);
            } else {
                IsoTypeWriter.writeUInt32(bb, CastUtils.l2i(this.segmentDuration));
                bb.putInt(CastUtils.l2i(this.mediaTime));
            }
            IsoTypeWriter.writeFixedPoint1616(bb, this.mediaRate);
        }

        public String toString() {
            return "Entry{segmentDuration=" + this.segmentDuration + ", mediaTime=" + this.mediaTime + ", mediaRate=" + this.mediaRate + '}';
        }
    }
}
