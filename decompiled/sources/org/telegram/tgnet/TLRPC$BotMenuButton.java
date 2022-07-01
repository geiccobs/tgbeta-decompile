package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$BotMenuButton extends TLObject {
    public static TLRPC$BotMenuButton TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$BotMenuButton tLRPC$BotMenuButton;
        if (i == -944407322) {
            tLRPC$BotMenuButton = new TLRPC$TL_botMenuButton();
        } else if (i != 1113113093) {
            tLRPC$BotMenuButton = i != 1966318984 ? null : new TLRPC$BotMenuButton() { // from class: org.telegram.tgnet.TLRPC$TL_botMenuButtonDefault
                public static int constructor = 1966318984;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$BotMenuButton = new TLRPC$BotMenuButton() { // from class: org.telegram.tgnet.TLRPC$TL_botMenuButtonCommands
                public static int constructor = 1113113093;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$BotMenuButton != null || !z) {
            if (tLRPC$BotMenuButton != null) {
                tLRPC$BotMenuButton.readParams(abstractSerializedData, z);
            }
            return tLRPC$BotMenuButton;
        }
        throw new RuntimeException(String.format("can't parse magic %x in BotMenuButton", Integer.valueOf(i)));
    }
}
