package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda139 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda139 INSTANCE = new MessagesController$$ExternalSyntheticLambda139();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda139() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
        return compare;
    }
}
