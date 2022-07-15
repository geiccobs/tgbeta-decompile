package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputStickerSetPremiumGifts extends TLRPC$InputStickerSet {
    public static int constructor = -930399486;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
