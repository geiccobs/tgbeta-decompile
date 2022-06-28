package org.telegram.messenger;
/* loaded from: classes4.dex */
public final /* synthetic */ class NotificationCenter$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ NotificationCenter f$0;

    public /* synthetic */ NotificationCenter$$ExternalSyntheticLambda1(NotificationCenter notificationCenter) {
        this.f$0 = notificationCenter;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.checkForExpiredNotifications();
    }
}
