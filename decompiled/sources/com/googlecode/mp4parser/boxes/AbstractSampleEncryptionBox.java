package com.googlecode.mp4parser.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.microsoft.appcenter.ingestion.models.properties.BooleanTypedProperty;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;
/* loaded from: classes3.dex */
public abstract class AbstractSampleEncryptionBox extends AbstractFullBox {
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    protected int algorithmId = -1;
    protected int ivSize = -1;
    protected byte[] kid = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    List<CencSampleAuxiliaryDataFormat> entries = Collections.emptyList();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AbstractSampleEncryptionBox.java", AbstractSampleEncryptionBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getOffsetToFirstIV", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "", "", "", "int"), 29);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getEntries", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "", "", "", "java.util.List"), 89);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setEntries", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "java.util.List", RemoteConfigConstants.ResponseFieldKey.ENTRIES, "", "void"), 93);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "equals", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "java.lang.Object", "o", "", BooleanTypedProperty.TYPE), 162);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "hashCode", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "", "", "", "int"), PsExtractor.PRIVATE_STREAM_1);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getEntrySizes", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "", "", "", "java.util.List"), 197);
    }

    public AbstractSampleEncryptionBox(String type) {
        super(type);
    }

    public int getOffsetToFirstIV() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        int offset = getSize() > 4294967296L ? 16 : 8;
        return offset + (isOverrideTrackEncryptionBoxParameters() ? this.kid.length + 4 : 0) + 4;
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        if ((getFlags() & 1) > 0) {
            this.algorithmId = IsoTypeReader.readUInt24(content);
            this.ivSize = IsoTypeReader.readUInt8(content);
            byte[] bArr = new byte[16];
            this.kid = bArr;
            content.get(bArr);
        }
        long numOfEntries = IsoTypeReader.readUInt32(content);
        ByteBuffer parseEight = content.duplicate();
        ByteBuffer parseSixteen = content.duplicate();
        List<CencSampleAuxiliaryDataFormat> parseEntries = parseEntries(parseEight, numOfEntries, 8);
        this.entries = parseEntries;
        if (parseEntries == null) {
            this.entries = parseEntries(parseSixteen, numOfEntries, 16);
            content.position((content.position() + content.remaining()) - parseSixteen.remaining());
        } else {
            content.position((content.position() + content.remaining()) - parseEight.remaining());
        }
        if (this.entries == null) {
            throw new RuntimeException("Cannot parse SampleEncryptionBox");
        }
    }

    private List<CencSampleAuxiliaryDataFormat> parseEntries(ByteBuffer content, long numOfEntries, int ivSize) {
        List _entries = new ArrayList();
        long remainingNumOfEntries = numOfEntries;
        while (true) {
            long remainingNumOfEntries2 = remainingNumOfEntries - 1;
            if (remainingNumOfEntries > 0) {
                try {
                    CencSampleAuxiliaryDataFormat e = new CencSampleAuxiliaryDataFormat();
                    e.iv = new byte[ivSize];
                    content.get(e.iv);
                    if ((getFlags() & 2) > 0) {
                        int numOfPairs = IsoTypeReader.readUInt16(content);
                        e.pairs = new CencSampleAuxiliaryDataFormat.Pair[numOfPairs];
                        for (int i = 0; i < e.pairs.length; i++) {
                            e.pairs[i] = e.createPair(IsoTypeReader.readUInt16(content), IsoTypeReader.readUInt32(content));
                        }
                    }
                    _entries.add(e);
                    remainingNumOfEntries = remainingNumOfEntries2;
                } catch (BufferUnderflowException e2) {
                    return null;
                }
            } else {
                return _entries;
            }
        }
    }

    public List<CencSampleAuxiliaryDataFormat> getEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.entries;
    }

    public void setEntries(List<CencSampleAuxiliaryDataFormat> list) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this, list));
        this.entries = list;
    }

    @DoNotParseDetail
    public boolean isSubSampleEncryption() {
        return (getFlags() & 2) > 0;
    }

    @DoNotParseDetail
    public void setSubSampleEncryption(boolean b) {
        if (b) {
            setFlags(getFlags() | 2);
        } else {
            setFlags(getFlags() & 16777213);
        }
    }

    @DoNotParseDetail
    protected boolean isOverrideTrackEncryptionBoxParameters() {
        return (getFlags() & 1) > 0;
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected void getContent(ByteBuffer byteBuffer) {
        CencSampleAuxiliaryDataFormat.Pair[] pairArr;
        writeVersionAndFlags(byteBuffer);
        if (isOverrideTrackEncryptionBoxParameters()) {
            IsoTypeWriter.writeUInt24(byteBuffer, this.algorithmId);
            IsoTypeWriter.writeUInt8(byteBuffer, this.ivSize);
            byteBuffer.put(this.kid);
        }
        IsoTypeWriter.writeUInt32(byteBuffer, this.entries.size());
        for (CencSampleAuxiliaryDataFormat entry : this.entries) {
            if (entry.getSize() > 0) {
                if (entry.iv.length != 8 && entry.iv.length != 16) {
                    throw new RuntimeException("IV must be either 8 or 16 bytes");
                }
                byteBuffer.put(entry.iv);
                if (isSubSampleEncryption()) {
                    IsoTypeWriter.writeUInt16(byteBuffer, entry.pairs.length);
                    for (CencSampleAuxiliaryDataFormat.Pair pair : entry.pairs) {
                        IsoTypeWriter.writeUInt16(byteBuffer, pair.clear());
                        IsoTypeWriter.writeUInt32(byteBuffer, pair.encrypted());
                    }
                }
            }
        }
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected long getContentSize() {
        long contentSize = 4;
        if (isOverrideTrackEncryptionBoxParameters()) {
            long contentSize2 = 4 + 4;
            contentSize = contentSize2 + this.kid.length;
        }
        long contentSize3 = contentSize + 4;
        for (CencSampleAuxiliaryDataFormat entry : this.entries) {
            contentSize3 += entry.getSize();
        }
        return contentSize3;
    }

    @Override // com.googlecode.mp4parser.AbstractBox, com.coremedia.iso.boxes.Box
    public void getBox(WritableByteChannel os) throws IOException {
        super.getBox(os);
    }

    public boolean equals(Object o) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this, o));
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractSampleEncryptionBox that = (AbstractSampleEncryptionBox) o;
        if (this.algorithmId != that.algorithmId || this.ivSize != that.ivSize) {
            return false;
        }
        List<CencSampleAuxiliaryDataFormat> list = this.entries;
        if (list == null ? that.entries != null : !list.equals(that.entries)) {
            return false;
        }
        return Arrays.equals(this.kid, that.kid);
    }

    public int hashCode() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        int result = this.algorithmId;
        int result2 = ((result * 31) + this.ivSize) * 31;
        byte[] bArr = this.kid;
        int i = 0;
        int result3 = (result2 + (bArr != null ? Arrays.hashCode(bArr) : 0)) * 31;
        List<CencSampleAuxiliaryDataFormat> list = this.entries;
        if (list != null) {
            i = list.hashCode();
        }
        return result3 + i;
    }

    public List<Short> getEntrySizes() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
        List entrySizes = new ArrayList(this.entries.size());
        for (CencSampleAuxiliaryDataFormat entry : this.entries) {
            short size = (short) entry.iv.length;
            if (isSubSampleEncryption()) {
                size = (short) ((entry.pairs.length * 6) + ((short) (size + 2)));
            }
            entrySizes.add(Short.valueOf(size));
        }
        return entrySizes;
    }
}
