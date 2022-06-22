package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputWallPaper extends TLObject {
    public static TLRPC$InputWallPaper TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputWallPaper tLRPC$InputWallPaper;
        if (i == -1770371538) {
            tLRPC$InputWallPaper = new TLRPC$TL_inputWallPaperNoFile();
        } else if (i == -433014407) {
            tLRPC$InputWallPaper = new TLRPC$TL_inputWallPaper();
        } else {
            tLRPC$InputWallPaper = i != 1913199744 ? null : new TLRPC$TL_inputWallPaperSlug();
        }
        if (tLRPC$InputWallPaper != null || !z) {
            if (tLRPC$InputWallPaper != null) {
                tLRPC$InputWallPaper.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputWallPaper;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputWallPaper", Integer.valueOf(i)));
    }
}
