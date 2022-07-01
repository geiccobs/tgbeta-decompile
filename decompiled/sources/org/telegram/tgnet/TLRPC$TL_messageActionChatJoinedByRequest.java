package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messageActionChatJoinedByRequest extends TLRPC$MessageAction {
    public static int constructor = -339958837;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
