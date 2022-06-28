package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.microsoft.appcenter.ingestion.models.properties.LongTypedProperty;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;
/* loaded from: classes3.dex */
public class TierBitRateBox extends AbstractBox {
    public static final String TYPE = "tibr";
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
    long avgBitRate;
    long baseBitRate;
    long maxBitRate;
    long tierAvgBitRate;
    long tierBaseBitRate;
    long tierMaxBitRate;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("TierBitRateBox.java", TierBitRateBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getBaseBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", "", "", "", LongTypedProperty.TYPE), 52);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setBaseBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", LongTypedProperty.TYPE, "baseBitRate", "", "void"), 56);
        ajc$tjp_10 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getTierAvgBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", "", "", "", LongTypedProperty.TYPE), 92);
        ajc$tjp_11 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setTierAvgBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", LongTypedProperty.TYPE, "tierAvgBitRate", "", "void"), 96);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getMaxBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", "", "", "", LongTypedProperty.TYPE), 60);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setMaxBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", LongTypedProperty.TYPE, "maxBitRate", "", "void"), 64);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getAvgBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", "", "", "", LongTypedProperty.TYPE), 68);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setAvgBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", LongTypedProperty.TYPE, "avgBitRate", "", "void"), 72);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getTierBaseBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", "", "", "", LongTypedProperty.TYPE), 76);
        ajc$tjp_7 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setTierBaseBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", LongTypedProperty.TYPE, "tierBaseBitRate", "", "void"), 80);
        ajc$tjp_8 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getTierMaxBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", "", "", "", LongTypedProperty.TYPE), 84);
        ajc$tjp_9 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setTierMaxBitRate", "com.mp4parser.iso14496.part15.TierBitRateBox", LongTypedProperty.TYPE, "tierMaxBitRate", "", "void"), 88);
    }

    public TierBitRateBox() {
        super(TYPE);
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected long getContentSize() {
        return 24L;
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt32(byteBuffer, this.baseBitRate);
        IsoTypeWriter.writeUInt32(byteBuffer, this.maxBitRate);
        IsoTypeWriter.writeUInt32(byteBuffer, this.avgBitRate);
        IsoTypeWriter.writeUInt32(byteBuffer, this.tierBaseBitRate);
        IsoTypeWriter.writeUInt32(byteBuffer, this.tierMaxBitRate);
        IsoTypeWriter.writeUInt32(byteBuffer, this.tierAvgBitRate);
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected void _parseDetails(ByteBuffer content) {
        this.baseBitRate = IsoTypeReader.readUInt32(content);
        this.maxBitRate = IsoTypeReader.readUInt32(content);
        this.avgBitRate = IsoTypeReader.readUInt32(content);
        this.tierBaseBitRate = IsoTypeReader.readUInt32(content);
        this.tierMaxBitRate = IsoTypeReader.readUInt32(content);
        this.tierAvgBitRate = IsoTypeReader.readUInt32(content);
    }

    public long getBaseBitRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.baseBitRate;
    }

    public void setBaseBitRate(long baseBitRate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this, Conversions.longObject(baseBitRate)));
        this.baseBitRate = baseBitRate;
    }

    public long getMaxBitRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.maxBitRate;
    }

    public void setMaxBitRate(long maxBitRate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this, Conversions.longObject(maxBitRate)));
        this.maxBitRate = maxBitRate;
    }

    public long getAvgBitRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return this.avgBitRate;
    }

    public void setAvgBitRate(long avgBitRate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this, Conversions.longObject(avgBitRate)));
        this.avgBitRate = avgBitRate;
    }

    public long getTierBaseBitRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        return this.tierBaseBitRate;
    }

    public void setTierBaseBitRate(long tierBaseBitRate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this, Conversions.longObject(tierBaseBitRate)));
        this.tierBaseBitRate = tierBaseBitRate;
    }

    public long getTierMaxBitRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this));
        return this.tierMaxBitRate;
    }

    public void setTierMaxBitRate(long tierMaxBitRate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, this, this, Conversions.longObject(tierMaxBitRate)));
        this.tierMaxBitRate = tierMaxBitRate;
    }

    public long getTierAvgBitRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, this, this));
        return this.tierAvgBitRate;
    }

    public void setTierAvgBitRate(long tierAvgBitRate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, this, this, Conversions.longObject(tierAvgBitRate)));
        this.tierAvgBitRate = tierAvgBitRate;
    }
}
