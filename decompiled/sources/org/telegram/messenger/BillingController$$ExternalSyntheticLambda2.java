package org.telegram.messenger;
/* loaded from: classes4.dex */
public final /* synthetic */ class BillingController$$ExternalSyntheticLambda2 implements Runnable {
    public static final /* synthetic */ BillingController$$ExternalSyntheticLambda2 INSTANCE = new BillingController$$ExternalSyntheticLambda2();

    private /* synthetic */ BillingController$$ExternalSyntheticLambda2() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.billingProductDetailsUpdated, new Object[0]);
    }
}
