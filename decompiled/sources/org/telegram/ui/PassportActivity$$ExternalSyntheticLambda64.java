package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda64 implements RequestDelegate {
    public static final /* synthetic */ PassportActivity$$ExternalSyntheticLambda64 INSTANCE = new PassportActivity$$ExternalSyntheticLambda64();

    private /* synthetic */ PassportActivity$$ExternalSyntheticLambda64() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PassportActivity$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                PassportActivity.lambda$new$0(TLObject.this);
            }
        });
    }
}
