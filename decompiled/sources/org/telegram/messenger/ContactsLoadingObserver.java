package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes4.dex */
public final class ContactsLoadingObserver {
    private final Callback callback;
    private final ContactsController contactsController;
    private final int currentAccount;
    private final NotificationCenter notificationCenter;
    private boolean released;
    private final NotificationCenter.NotificationCenterDelegate observer = new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.messenger.ContactsLoadingObserver$$ExternalSyntheticLambda1
        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public final void didReceivedNotification(int i, int i2, Object[] objArr) {
            ContactsLoadingObserver.this.m182lambda$new$0$orgtelegrammessengerContactsLoadingObserver(i, i2, objArr);
        }
    };
    private final Runnable releaseRunnable = new Runnable() { // from class: org.telegram.messenger.ContactsLoadingObserver$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            ContactsLoadingObserver.this.m183lambda$new$1$orgtelegrammessengerContactsLoadingObserver();
        }
    };
    private final Handler handler = new Handler(Looper.myLooper());

    /* loaded from: classes4.dex */
    public interface Callback {
        void onResult(boolean z);
    }

    public static void observe(Callback callback, long expirationTime) {
        new ContactsLoadingObserver(callback).start(expirationTime);
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-ContactsLoadingObserver */
    public /* synthetic */ void m182lambda$new$0$orgtelegrammessengerContactsLoadingObserver(int id, int account, Object[] args) {
        if (id == NotificationCenter.contactsDidLoad) {
            onContactsLoadingStateUpdated(account, false);
        }
    }

    private ContactsLoadingObserver(Callback callback) {
        this.callback = callback;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.contactsController = ContactsController.getInstance(i);
        this.notificationCenter = NotificationCenter.getInstance(i);
    }

    /* renamed from: lambda$new$1$org-telegram-messenger-ContactsLoadingObserver */
    public /* synthetic */ void m183lambda$new$1$orgtelegrammessengerContactsLoadingObserver() {
        onContactsLoadingStateUpdated(this.currentAccount, true);
    }

    public void start(long expirationTime) {
        if (!onContactsLoadingStateUpdated(this.currentAccount, false)) {
            this.notificationCenter.addObserver(this.observer, NotificationCenter.contactsDidLoad);
            this.handler.postDelayed(this.releaseRunnable, expirationTime);
        }
    }

    public void release() {
        if (!this.released) {
            NotificationCenter notificationCenter = this.notificationCenter;
            if (notificationCenter != null) {
                notificationCenter.removeObserver(this.observer, NotificationCenter.contactsDidLoad);
            }
            Handler handler = this.handler;
            if (handler != null) {
                handler.removeCallbacks(this.releaseRunnable);
            }
            this.released = true;
        }
    }

    private boolean onContactsLoadingStateUpdated(int account, boolean force) {
        if (!this.released) {
            boolean contactsLoaded = this.contactsController.contactsLoaded;
            if (contactsLoaded || force) {
                release();
                this.callback.onResult(contactsLoaded);
                return true;
            }
            return false;
        }
        return false;
    }
}
