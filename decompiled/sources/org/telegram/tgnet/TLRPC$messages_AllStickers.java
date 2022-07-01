package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_AllStickers extends TLObject {
    public ArrayList<TLRPC$StickerSet> sets = new ArrayList<>();

    public TLRPC$messages_AllStickers() {
        new ArrayList();
        new ArrayList();
    }

    public static TLRPC$messages_AllStickers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_AllStickers tLRPC$messages_AllStickers;
        if (i == -843329861) {
            tLRPC$messages_AllStickers = new TLRPC$TL_messages_allStickers();
        } else {
            tLRPC$messages_AllStickers = i != -395967805 ? null : new TLRPC$messages_AllStickers() { // from class: org.telegram.tgnet.TLRPC$TL_messages_allStickersNotModified
                public static int constructor = -395967805;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$messages_AllStickers != null || !z) {
            if (tLRPC$messages_AllStickers != null) {
                tLRPC$messages_AllStickers.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_AllStickers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_AllStickers", Integer.valueOf(i)));
    }
}
