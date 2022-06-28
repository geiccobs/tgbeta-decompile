package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public static final /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda7 INSTANCE = new PrivacySettingsActivity$$ExternalSyntheticLambda7();

    private /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda7() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PrivacySettingsActivity.lambda$onFragmentDestroy$1(tLObject, tL_error);
    }
}
