package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;
/* loaded from: classes.dex */
public final /* synthetic */ class MessageObject$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ MessageObject$$ExternalSyntheticLambda1 INSTANCE = new MessageObject$$ExternalSyntheticLambda1();

    private /* synthetic */ MessageObject$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$addEntitiesToText$0;
        lambda$addEntitiesToText$0 = MessageObject.lambda$addEntitiesToText$0((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
        return lambda$addEntitiesToText$0;
    }
}
