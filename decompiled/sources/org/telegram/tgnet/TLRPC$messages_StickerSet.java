package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_StickerSet extends TLObject {
    public TLRPC$StickerSet set;
    public ArrayList<TLRPC$TL_stickerPack> packs = new ArrayList<>();
    public ArrayList<TLRPC$Document> documents = new ArrayList<>();

    public static TLRPC$TL_messages_stickerSet TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        if (i == -1240849242) {
            tLRPC$TL_messages_stickerSet = new TLRPC$TL_messages_stickerSet();
        } else {
            tLRPC$TL_messages_stickerSet = i != -738646805 ? null : new TLRPC$TL_messages_stickerSet() { // from class: org.telegram.tgnet.TLRPC$TL_messages_stickerSetNotModified
                public static int constructor = -738646805;

                @Override // org.telegram.tgnet.TLRPC$TL_messages_stickerSet, org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$TL_messages_stickerSet != null || !z) {
            if (tLRPC$TL_messages_stickerSet != null) {
                tLRPC$TL_messages_stickerSet.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messages_stickerSet;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_StickerSet", Integer.valueOf(i)));
    }
}
