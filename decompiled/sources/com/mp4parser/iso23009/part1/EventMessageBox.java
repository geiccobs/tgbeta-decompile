package com.mp4parser.iso23009.part1;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.properties.LongTypedProperty;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;
/* loaded from: classes3.dex */
public class EventMessageBox extends AbstractFullBox {
    public static final String TYPE = "emsg";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_11 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_12 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_13 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_9 = null;
    long eventDuration;
    long id;
    byte[] messageData;
    long presentationTimeDelta;
    String schemeIdUri;
    long timescale;
    String value;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("EventMessageBox.java", EventMessageBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getSchemeIdUri", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "java.lang.String"), 59);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setSchemeIdUri", "com.mp4parser.iso23009.part1.EventMessageBox", "java.lang.String", "schemeIdUri", "", "void"), 63);
        ajc$tjp_10 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getId", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", LongTypedProperty.TYPE), 99);
        ajc$tjp_11 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setId", "com.mp4parser.iso23009.part1.EventMessageBox", LongTypedProperty.TYPE, "id", "", "void"), 103);
        ajc$tjp_12 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getMessageData", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "[B"), 107);
        ajc$tjp_13 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setMessageData", "com.mp4parser.iso23009.part1.EventMessageBox", "[B", "messageData", "", "void"), 111);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getValue", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "java.lang.String"), 67);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setValue", "com.mp4parser.iso23009.part1.EventMessageBox", "java.lang.String", CommonProperties.VALUE, "", "void"), 71);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getTimescale", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", LongTypedProperty.TYPE), 75);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setTimescale", "com.mp4parser.iso23009.part1.EventMessageBox", LongTypedProperty.TYPE, "timescale", "", "void"), 79);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getPresentationTimeDelta", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", LongTypedProperty.TYPE), 83);
        ajc$tjp_7 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setPresentationTimeDelta", "com.mp4parser.iso23009.part1.EventMessageBox", LongTypedProperty.TYPE, "presentationTimeDelta", "", "void"), 87);
        ajc$tjp_8 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "getEventDuration", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", LongTypedProperty.TYPE), 91);
        ajc$tjp_9 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, factory.makeMethodSig(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, "setEventDuration", "com.mp4parser.iso23009.part1.EventMessageBox", LongTypedProperty.TYPE, "eventDuration", "", "void"), 95);
    }

    public EventMessageBox() {
        super(TYPE);
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.schemeIdUri = IsoTypeReader.readString(content);
        this.value = IsoTypeReader.readString(content);
        this.timescale = IsoTypeReader.readUInt32(content);
        this.presentationTimeDelta = IsoTypeReader.readUInt32(content);
        this.eventDuration = IsoTypeReader.readUInt32(content);
        this.id = IsoTypeReader.readUInt32(content);
        byte[] bArr = new byte[content.remaining()];
        this.messageData = bArr;
        content.get(bArr);
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUtf8String(byteBuffer, this.schemeIdUri);
        IsoTypeWriter.writeUtf8String(byteBuffer, this.value);
        IsoTypeWriter.writeUInt32(byteBuffer, this.timescale);
        IsoTypeWriter.writeUInt32(byteBuffer, this.presentationTimeDelta);
        IsoTypeWriter.writeUInt32(byteBuffer, this.eventDuration);
        IsoTypeWriter.writeUInt32(byteBuffer, this.id);
        byteBuffer.put(this.messageData);
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    protected long getContentSize() {
        return Utf8.utf8StringLengthInBytes(this.schemeIdUri) + 22 + Utf8.utf8StringLengthInBytes(this.value) + this.messageData.length;
    }

    public String getSchemeIdUri() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.schemeIdUri;
    }

    public void setSchemeIdUri(String schemeIdUri) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this, schemeIdUri));
        this.schemeIdUri = schemeIdUri;
    }

    public String getValue() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.value;
    }

    public void setValue(String value) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this, value));
        this.value = value;
    }

    public long getTimescale() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return this.timescale;
    }

    public void setTimescale(long timescale) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this, Conversions.longObject(timescale)));
        this.timescale = timescale;
    }

    public long getPresentationTimeDelta() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        return this.presentationTimeDelta;
    }

    public void setPresentationTimeDelta(long presentationTimeDelta) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this, Conversions.longObject(presentationTimeDelta)));
        this.presentationTimeDelta = presentationTimeDelta;
    }

    public long getEventDuration() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this));
        return this.eventDuration;
    }

    public void setEventDuration(long eventDuration) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, this, this, Conversions.longObject(eventDuration)));
        this.eventDuration = eventDuration;
    }

    public long getId() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, this, this));
        return this.id;
    }

    public void setId(long id) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, this, this, Conversions.longObject(id)));
        this.id = id;
    }

    public byte[] getMessageData() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, this, this));
        return this.messageData;
    }

    public void setMessageData(byte[] messageData) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, this, this, messageData));
        this.messageData = messageData;
    }
}
