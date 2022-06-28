package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class ChangeNameActivity$$ExternalSyntheticLambda4 implements RequestDelegate {
    public static final /* synthetic */ ChangeNameActivity$$ExternalSyntheticLambda4 INSTANCE = new ChangeNameActivity$$ExternalSyntheticLambda4();

    private /* synthetic */ ChangeNameActivity$$ExternalSyntheticLambda4() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ChangeNameActivity.lambda$saveName$3(tLObject, tL_error);
    }
}
