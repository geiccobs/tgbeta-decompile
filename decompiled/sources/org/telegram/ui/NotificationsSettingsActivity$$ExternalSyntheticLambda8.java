package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class NotificationsSettingsActivity$$ExternalSyntheticLambda8 implements RequestDelegate {
    public static final /* synthetic */ NotificationsSettingsActivity$$ExternalSyntheticLambda8 INSTANCE = new NotificationsSettingsActivity$$ExternalSyntheticLambda8();

    private /* synthetic */ NotificationsSettingsActivity$$ExternalSyntheticLambda8() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        NotificationsSettingsActivity.lambda$createView$5(tLObject, tL_error);
    }
}
