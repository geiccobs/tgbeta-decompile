package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$account_SavedRingtones extends TLObject {
    public static TLRPC$account_SavedRingtones TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_SavedRingtones tLRPC$account_SavedRingtones;
        if (i != -1041683259) {
            tLRPC$account_SavedRingtones = i != -67704655 ? null : new TLRPC$account_SavedRingtones() { // from class: org.telegram.tgnet.TLRPC$TL_account_savedRingtonesNotModified
                public static int constructor = -67704655;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$account_SavedRingtones = new TLRPC$TL_account_savedRingtones();
        }
        if (tLRPC$account_SavedRingtones != null || !z) {
            if (tLRPC$account_SavedRingtones != null) {
                tLRPC$account_SavedRingtones.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_SavedRingtones;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_SavedRingtones", Integer.valueOf(i)));
    }
}
