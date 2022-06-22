package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_sendMessageRecordAudioAction extends TLRPC$SendMessageAction {
    public static int constructor = -718310409;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
