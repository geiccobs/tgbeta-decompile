package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messageMediaEmpty extends TLRPC$MessageMedia {
    public static int constructor = 1038967584;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
