package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda226 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda226 INSTANCE = new MessagesController$$ExternalSyntheticLambda226();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda226() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processUpdatesQueue$261;
        lambda$processUpdatesQueue$261 = MessagesController.lambda$processUpdatesQueue$261((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processUpdatesQueue$261;
    }
}
