package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_notificationSoundDefault extends TLRPC$NotificationSound {
    public static int constructor = -1746354498;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
