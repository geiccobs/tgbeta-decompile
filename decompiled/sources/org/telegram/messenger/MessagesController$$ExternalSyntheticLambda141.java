package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda141 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda141 INSTANCE = new MessagesController$$ExternalSyntheticLambda141();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda141() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
        return compare;
    }
}
