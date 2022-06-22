package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda200 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda200 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda200();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda200() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getMessagesInternal$125;
        lambda$getMessagesInternal$125 = MessagesStorage.lambda$getMessagesInternal$125((TLRPC$Message) obj, (TLRPC$Message) obj2);
        return lambda$getMessagesInternal$125;
    }
}
