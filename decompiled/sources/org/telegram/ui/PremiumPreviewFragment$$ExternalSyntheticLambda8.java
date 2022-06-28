package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda8 implements RequestDelegate {
    public static final /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda8 INSTANCE = new PremiumPreviewFragment$$ExternalSyntheticLambda8();

    private /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda8() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PremiumPreviewFragment.lambda$sentPremiumButtonClick$8(tLObject, tL_error);
    }
}
