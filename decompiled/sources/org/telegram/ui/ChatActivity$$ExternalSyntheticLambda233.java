package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda233 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda233 INSTANCE = new ChatActivity$$ExternalSyntheticLambda233();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda233() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$246(tLObject, tLRPC$TL_error);
    }
}
