package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputStorePaymentPurpose extends TLObject {
    public static TLRPC$InputStorePaymentPurpose TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose;
        if (i == -1502273946) {
            tLRPC$InputStorePaymentPurpose = new TLRPC$TL_inputStorePaymentPremiumSubscription();
        } else {
            tLRPC$InputStorePaymentPurpose = i != 1147243133 ? null : new TLRPC$TL_inputStorePaymentGiftPremium();
        }
        if (tLRPC$InputStorePaymentPurpose != null || !z) {
            if (tLRPC$InputStorePaymentPurpose != null) {
                tLRPC$InputStorePaymentPurpose.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputStorePaymentPurpose;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputStorePaymentPurpose", Integer.valueOf(i)));
    }
}
