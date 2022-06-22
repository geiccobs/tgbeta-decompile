package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputMessagesFilterPinned extends TLRPC$MessagesFilter {
    public static int constructor = 464520273;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
