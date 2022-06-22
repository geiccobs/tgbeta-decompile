package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputSecureFile extends TLObject {
    public static TLRPC$InputSecureFile TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputSecureFile tLRPC$InputSecureFile;
        if (i == 859091184) {
            tLRPC$InputSecureFile = new TLRPC$TL_inputSecureFileUploaded();
        } else {
            tLRPC$InputSecureFile = i != 1399317950 ? null : new TLRPC$TL_inputSecureFile();
        }
        if (tLRPC$InputSecureFile != null || !z) {
            if (tLRPC$InputSecureFile != null) {
                tLRPC$InputSecureFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputSecureFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputSecureFile", Integer.valueOf(i)));
    }
}
