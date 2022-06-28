package org.telegram.ui;

import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
/* loaded from: classes4.dex */
public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda41 implements Runnable {
    public static final /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda41 INSTANCE = new GroupCallActivity$$ExternalSyntheticLambda41();

    private /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda41() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        RTMPStreamPipOverlay.show();
    }
}
