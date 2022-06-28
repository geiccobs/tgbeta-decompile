package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;
/* loaded from: classes4.dex */
public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda54 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda54 INSTANCE = new VoIPService$$ExternalSyntheticLambda54();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda54() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
