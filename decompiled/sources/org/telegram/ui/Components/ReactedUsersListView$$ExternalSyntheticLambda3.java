package org.telegram.ui.Components;

import j$.util.function.ToIntFunction;
import org.telegram.tgnet.TLRPC$TL_messagePeerReaction;
/* loaded from: classes3.dex */
public final /* synthetic */ class ReactedUsersListView$$ExternalSyntheticLambda3 implements ToIntFunction {
    public static final /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda3 INSTANCE = new ReactedUsersListView$$ExternalSyntheticLambda3();

    private /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda3() {
    }

    @Override // j$.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int lambda$load$1;
        lambda$load$1 = ReactedUsersListView.lambda$load$1((TLRPC$TL_messagePeerReaction) obj);
        return lambda$load$1;
    }
}
