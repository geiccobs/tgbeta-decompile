package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda46 implements RequestDelegate {
    public static final /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda46 INSTANCE = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda46();

    private /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda46() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        TwoStepVerificationSetupActivity.lambda$createView$19(tLObject, tL_error);
    }
}