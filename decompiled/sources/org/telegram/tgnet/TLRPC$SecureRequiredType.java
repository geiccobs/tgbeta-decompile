package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$SecureRequiredType extends TLObject {
    public static TLRPC$SecureRequiredType TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$SecureRequiredType tLRPC$SecureRequiredType;
        if (i == -2103600678) {
            tLRPC$SecureRequiredType = new TLRPC$TL_secureRequiredType();
        } else {
            tLRPC$SecureRequiredType = i != 41187252 ? null : new TLRPC$TL_secureRequiredTypeOneOf();
        }
        if (tLRPC$SecureRequiredType != null || !z) {
            if (tLRPC$SecureRequiredType != null) {
                tLRPC$SecureRequiredType.readParams(abstractSerializedData, z);
            }
            return tLRPC$SecureRequiredType;
        }
        throw new RuntimeException(String.format("can't parse magic %x in SecureRequiredType", Integer.valueOf(i)));
    }
}
