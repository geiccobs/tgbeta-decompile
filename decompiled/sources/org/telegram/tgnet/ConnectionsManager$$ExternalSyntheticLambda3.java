package org.telegram.tgnet;

import org.telegram.messenger.NotificationCenter;
/* loaded from: classes4.dex */
public final /* synthetic */ class ConnectionsManager$$ExternalSyntheticLambda3 implements Runnable {
    public static final /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda3 INSTANCE = new ConnectionsManager$$ExternalSyntheticLambda3();

    private /* synthetic */ ConnectionsManager$$ExternalSyntheticLambda3() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, 3);
    }
}
