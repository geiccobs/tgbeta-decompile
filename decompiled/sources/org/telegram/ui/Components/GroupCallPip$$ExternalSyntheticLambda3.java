package org.telegram.ui.Components;

import org.telegram.messenger.NotificationCenter;
/* loaded from: classes5.dex */
public final /* synthetic */ class GroupCallPip$$ExternalSyntheticLambda3 implements Runnable {
    public static final /* synthetic */ GroupCallPip$$ExternalSyntheticLambda3 INSTANCE = new GroupCallPip$$ExternalSyntheticLambda3();

    private /* synthetic */ GroupCallPip$$ExternalSyntheticLambda3() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
    }
}
