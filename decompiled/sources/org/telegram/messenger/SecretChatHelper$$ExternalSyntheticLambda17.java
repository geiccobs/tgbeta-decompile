package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class SecretChatHelper$$ExternalSyntheticLambda17 implements Comparator {
    public static final /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda17 INSTANCE = new SecretChatHelper$$ExternalSyntheticLambda17();

    private /* synthetic */ SecretChatHelper$$ExternalSyntheticLambda17() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = AndroidUtilities.compare(((TLRPC.Message) obj).seq_out, ((TLRPC.Message) obj2).seq_out);
        return compare;
    }
}
