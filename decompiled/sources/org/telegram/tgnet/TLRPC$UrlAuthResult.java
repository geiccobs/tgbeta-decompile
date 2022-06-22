package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$UrlAuthResult extends TLObject {
    public static TLRPC$UrlAuthResult TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$UrlAuthResult tLRPC$UrlAuthResult;
        if (i == -1886646706) {
            tLRPC$UrlAuthResult = new TLRPC$TL_urlAuthResultAccepted();
        } else if (i != -1831650802) {
            tLRPC$UrlAuthResult = i != -1445536993 ? null : new TLRPC$UrlAuthResult() { // from class: org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault
                public static int constructor = -1445536993;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$UrlAuthResult = new TLRPC$TL_urlAuthResultRequest();
        }
        if (tLRPC$UrlAuthResult != null || !z) {
            if (tLRPC$UrlAuthResult != null) {
                tLRPC$UrlAuthResult.readParams(abstractSerializedData, z);
            }
            return tLRPC$UrlAuthResult;
        }
        throw new RuntimeException(String.format("can't parse magic %x in UrlAuthResult", Integer.valueOf(i)));
    }
}
