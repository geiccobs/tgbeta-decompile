package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda10 implements RequestDelegate {
    public static final /* synthetic */ SessionsActivity$$ExternalSyntheticLambda10 INSTANCE = new SessionsActivity$$ExternalSyntheticLambda10();

    private /* synthetic */ SessionsActivity$$ExternalSyntheticLambda10() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        SessionsActivity.lambda$createView$0(tLObject, tL_error);
    }
}