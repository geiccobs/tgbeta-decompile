package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_FeaturedStickers extends TLObject {
    public static TLRPC$messages_FeaturedStickers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_FeaturedStickers tLRPC$messages_FeaturedStickers;
        if (i == -1103615738) {
            tLRPC$messages_FeaturedStickers = new TLRPC$TL_messages_featuredStickers();
        } else {
            tLRPC$messages_FeaturedStickers = i != -958657434 ? null : new TLRPC$messages_FeaturedStickers() { // from class: org.telegram.tgnet.TLRPC$TL_messages_featuredStickersNotModified
                public static int constructor = -958657434;
                public int count;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.count = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt32(this.count);
                }
            };
        }
        if (tLRPC$messages_FeaturedStickers != null || !z) {
            if (tLRPC$messages_FeaturedStickers != null) {
                tLRPC$messages_FeaturedStickers.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_FeaturedStickers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_FeaturedStickers", Integer.valueOf(i)));
    }
}
