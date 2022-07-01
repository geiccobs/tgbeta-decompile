package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputGame extends TLObject {
    public long access_hash;
    public TLRPC$InputUser bot_id;
    public long id;
    public String short_name;

    public static TLRPC$InputGame TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputGame tLRPC$InputGame;
        if (i == -1020139510) {
            tLRPC$InputGame = new TLRPC$TL_inputGameShortName();
        } else {
            tLRPC$InputGame = i != 53231223 ? null : new TLRPC$InputGame() { // from class: org.telegram.tgnet.TLRPC$TL_inputGameID
                public static int constructor = 53231223;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.id = abstractSerializedData2.readInt64(z2);
                    this.access_hash = abstractSerializedData2.readInt64(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.id);
                    abstractSerializedData2.writeInt64(this.access_hash);
                }
            };
        }
        if (tLRPC$InputGame != null || !z) {
            if (tLRPC$InputGame != null) {
                tLRPC$InputGame.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputGame;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputGame", Integer.valueOf(i)));
    }
}
