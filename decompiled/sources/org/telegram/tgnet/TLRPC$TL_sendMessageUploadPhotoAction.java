package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_sendMessageUploadPhotoAction extends TLRPC$SendMessageAction {
    public static int constructor = -774682074;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.progress = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.progress);
    }
}
