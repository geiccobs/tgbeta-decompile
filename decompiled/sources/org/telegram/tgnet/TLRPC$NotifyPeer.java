package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$NotifyPeer extends TLObject {
    public static TLRPC$NotifyPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$NotifyPeer tLRPC$NotifyPeer;
        switch (i) {
            case -1613493288:
                tLRPC$NotifyPeer = new TLRPC$TL_notifyPeer();
                break;
            case -1261946036:
                tLRPC$NotifyPeer = new TLRPC$NotifyPeer() { // from class: org.telegram.tgnet.TLRPC$TL_notifyUsers
                    public static int constructor = -1261946036;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1073230141:
                tLRPC$NotifyPeer = new TLRPC$NotifyPeer() { // from class: org.telegram.tgnet.TLRPC$TL_notifyChats
                    public static int constructor = -1073230141;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -703403793:
                tLRPC$NotifyPeer = new TLRPC$NotifyPeer() { // from class: org.telegram.tgnet.TLRPC$TL_notifyBroadcasts
                    public static int constructor = -703403793;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            default:
                tLRPC$NotifyPeer = null;
                break;
        }
        if (tLRPC$NotifyPeer != null || !z) {
            if (tLRPC$NotifyPeer != null) {
                tLRPC$NotifyPeer.readParams(abstractSerializedData, z);
            }
            return tLRPC$NotifyPeer;
        }
        throw new RuntimeException(String.format("can't parse magic %x in NotifyPeer", Integer.valueOf(i)));
    }
}
