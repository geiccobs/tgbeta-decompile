package org.telegram.messenger;
/* loaded from: classes4.dex */
public final /* synthetic */ class Emoji$$ExternalSyntheticLambda1 implements Runnable {
    public static final /* synthetic */ Emoji$$ExternalSyntheticLambda1 INSTANCE = new Emoji$$ExternalSyntheticLambda1();

    private /* synthetic */ Emoji$$ExternalSyntheticLambda1() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiLoaded, new Object[0]);
    }
}
