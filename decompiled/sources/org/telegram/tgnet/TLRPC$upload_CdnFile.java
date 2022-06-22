package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$upload_CdnFile extends TLObject {
    public NativeByteBuffer bytes;
    public byte[] request_token;

    public static TLRPC$upload_CdnFile TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$upload_CdnFile tLRPC$upload_CdnFile;
        if (i == -1449145777) {
            tLRPC$upload_CdnFile = new TLRPC$TL_upload_cdnFile();
        } else {
            tLRPC$upload_CdnFile = i != -290921362 ? null : new TLRPC$upload_CdnFile() { // from class: org.telegram.tgnet.TLRPC$TL_upload_cdnFileReuploadNeeded
                public static int constructor = -290921362;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.request_token = abstractSerializedData2.readByteArray(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeByteArray(this.request_token);
                }
            };
        }
        if (tLRPC$upload_CdnFile != null || !z) {
            if (tLRPC$upload_CdnFile != null) {
                tLRPC$upload_CdnFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$upload_CdnFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in upload_CdnFile", Integer.valueOf(i)));
    }
}
