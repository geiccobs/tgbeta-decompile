package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputReportReasonSpam extends TLRPC$ReportReason {
    public static int constructor = 1490799288;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
