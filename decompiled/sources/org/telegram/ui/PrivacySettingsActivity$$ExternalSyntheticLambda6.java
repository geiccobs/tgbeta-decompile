package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda6 implements RequestDelegate {
    public static final /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda6 INSTANCE = new PrivacySettingsActivity$$ExternalSyntheticLambda6();

    private /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda6() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PrivacySettingsActivity.lambda$onFragmentDestroy$0(tLObject, tL_error);
    }
}
