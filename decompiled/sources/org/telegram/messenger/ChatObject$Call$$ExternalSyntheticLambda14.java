package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda14 implements Comparator {
    public static final /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda14 INSTANCE = new ChatObject$Call$$ExternalSyntheticLambda14();

    private /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda14() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = AndroidUtilities.compare(((TLRPC.TL_updateGroupCallParticipants) obj).version, ((TLRPC.TL_updateGroupCallParticipants) obj2).version);
        return compare;
    }
}
