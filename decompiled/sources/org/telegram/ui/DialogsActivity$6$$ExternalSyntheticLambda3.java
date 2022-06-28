package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class DialogsActivity$6$$ExternalSyntheticLambda3 implements RequestDelegate {
    public static final /* synthetic */ DialogsActivity$6$$ExternalSyntheticLambda3 INSTANCE = new DialogsActivity$6$$ExternalSyntheticLambda3();

    private /* synthetic */ DialogsActivity$6$$ExternalSyntheticLambda3() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(DialogsActivity$6$$ExternalSyntheticLambda2.INSTANCE);
    }
}
