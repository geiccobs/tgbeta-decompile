package org.telegram.messenger.voip;

import org.telegram.messenger.NotificationCenter;
/* loaded from: classes4.dex */
public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda59 implements Runnable {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda59 INSTANCE = new VoIPService$$ExternalSyntheticLambda59();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda59() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }
}
