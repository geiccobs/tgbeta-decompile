package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_speakingInGroupCallAction extends TLRPC$SendMessageAction {
    public static int constructor = -651419003;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
