package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda147 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda147 INSTANCE = new ChatActivity$$ExternalSyntheticLambda147();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda147() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ChatActivity.lambda$markSponsoredAsRead$245(tLObject, tL_error);
    }
}
