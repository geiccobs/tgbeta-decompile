package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_Stickers extends TLObject {
    public static TLRPC$messages_Stickers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Stickers tLRPC$messages_Stickers;
        if (i == -244016606) {
            tLRPC$messages_Stickers = new TLRPC$messages_Stickers() { // from class: org.telegram.tgnet.TLRPC$TL_messages_stickersNotModified
                public static int constructor = -244016606;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$messages_Stickers = i != 816245886 ? null : new TLRPC$TL_messages_stickers();
        }
        if (tLRPC$messages_Stickers != null || !z) {
            if (tLRPC$messages_Stickers != null) {
                tLRPC$messages_Stickers.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_Stickers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Stickers", Integer.valueOf(i)));
    }
}
