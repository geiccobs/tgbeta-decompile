package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class FiltersSetupActivity$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ FiltersSetupActivity$$ExternalSyntheticLambda0 INSTANCE = new FiltersSetupActivity$$ExternalSyntheticLambda0();

    private /* synthetic */ FiltersSetupActivity$$ExternalSyntheticLambda0() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        FiltersSetupActivity.lambda$onFragmentDestroy$0(tLObject, tL_error);
    }
}
