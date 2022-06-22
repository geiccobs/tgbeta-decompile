package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messageActionScreenshotTaken extends TLRPC$MessageAction {
    public static int constructor = 1200788123;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
