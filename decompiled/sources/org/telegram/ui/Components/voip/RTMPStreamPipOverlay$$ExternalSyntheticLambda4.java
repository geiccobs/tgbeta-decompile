package org.telegram.ui.Components.voip;

import org.telegram.messenger.NotificationCenter;
/* loaded from: classes5.dex */
public final /* synthetic */ class RTMPStreamPipOverlay$$ExternalSyntheticLambda4 implements Runnable {
    public static final /* synthetic */ RTMPStreamPipOverlay$$ExternalSyntheticLambda4 INSTANCE = new RTMPStreamPipOverlay$$ExternalSyntheticLambda4();

    private /* synthetic */ RTMPStreamPipOverlay$$ExternalSyntheticLambda4() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
    }
}
