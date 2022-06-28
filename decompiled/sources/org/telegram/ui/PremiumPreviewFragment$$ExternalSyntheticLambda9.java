package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda9 implements RequestDelegate {
    public static final /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda9 INSTANCE = new PremiumPreviewFragment$$ExternalSyntheticLambda9();

    private /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda9() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PremiumPreviewFragment.lambda$sentPremiumBuyCanceled$9(tLObject, tL_error);
    }
}
