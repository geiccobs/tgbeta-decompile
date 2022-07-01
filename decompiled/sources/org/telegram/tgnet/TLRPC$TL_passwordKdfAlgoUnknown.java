package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_passwordKdfAlgoUnknown extends TLRPC$PasswordKdfAlgo {
    public static int constructor = -732254058;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
