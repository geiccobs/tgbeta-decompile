package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_jsonNull extends TLRPC$JSONValue {
    public static int constructor = 1064139624;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
