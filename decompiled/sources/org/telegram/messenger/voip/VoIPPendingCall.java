package org.telegram.messenger.voip;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.voip.VoIPHelper;
/* loaded from: classes4.dex */
public final class VoIPPendingCall {
    private AccountInstance accountInstance;
    private final Activity activity;
    private Handler handler;
    private NotificationCenter notificationCenter;
    private final NotificationCenter.NotificationCenterDelegate observer;
    private final Runnable releaseRunnable;
    private boolean released;
    private final long userId;
    private final boolean video;

    public static VoIPPendingCall startOrSchedule(Activity activity, long userId, boolean video, AccountInstance accountInstance) {
        return new VoIPPendingCall(activity, userId, video, 1000L, accountInstance);
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-voip-VoIPPendingCall */
    public /* synthetic */ void m1281lambda$new$0$orgtelegrammessengervoipVoIPPendingCall(int id, int account, Object[] args) {
        if (id == NotificationCenter.didUpdateConnectionState) {
            onConnectionStateUpdated(false);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-messenger-voip-VoIPPendingCall */
    public /* synthetic */ void m1282lambda$new$1$orgtelegrammessengervoipVoIPPendingCall() {
        onConnectionStateUpdated(true);
    }

    private VoIPPendingCall(Activity activity, long userId, boolean video, long expirationTime, AccountInstance accountInstance) {
        NotificationCenter.NotificationCenterDelegate notificationCenterDelegate = new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.messenger.voip.VoIPPendingCall$$ExternalSyntheticLambda1
            @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
            public final void didReceivedNotification(int i, int i2, Object[] objArr) {
                VoIPPendingCall.this.m1281lambda$new$0$orgtelegrammessengervoipVoIPPendingCall(i, i2, objArr);
            }
        };
        this.observer = notificationCenterDelegate;
        Runnable runnable = new Runnable() { // from class: org.telegram.messenger.voip.VoIPPendingCall$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VoIPPendingCall.this.m1282lambda$new$1$orgtelegrammessengervoipVoIPPendingCall();
            }
        };
        this.releaseRunnable = runnable;
        this.activity = activity;
        this.userId = userId;
        this.video = video;
        this.accountInstance = accountInstance;
        if (!onConnectionStateUpdated(false)) {
            NotificationCenter notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount);
            this.notificationCenter = notificationCenter;
            notificationCenter.addObserver(notificationCenterDelegate, NotificationCenter.didUpdateConnectionState);
            Handler handler = new Handler(Looper.myLooper());
            this.handler = handler;
            handler.postDelayed(runnable, expirationTime);
        }
    }

    private boolean onConnectionStateUpdated(boolean force) {
        if (this.released || (!force && !isConnected(this.accountInstance) && !isAirplaneMode())) {
            return false;
        }
        MessagesController messagesController = this.accountInstance.getMessagesController();
        TLRPC.User user = messagesController.getUser(Long.valueOf(this.userId));
        if (user != null) {
            TLRPC.UserFull userFull = messagesController.getUserFull(user.id);
            VoIPHelper.startCall(user, this.video, userFull != null && userFull.video_calls_available, this.activity, userFull, this.accountInstance);
        } else if (isAirplaneMode()) {
            VoIPHelper.startCall(null, this.video, false, this.activity, null, this.accountInstance);
        }
        release();
        return true;
    }

    private boolean isConnected(AccountInstance accountInstance) {
        return accountInstance.getConnectionsManager().getConnectionState() == 3;
    }

    private boolean isAirplaneMode() {
        return Settings.System.getInt(this.activity.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public void release() {
        if (!this.released) {
            NotificationCenter notificationCenter = this.notificationCenter;
            if (notificationCenter != null) {
                notificationCenter.removeObserver(this.observer, NotificationCenter.didUpdateConnectionState);
            }
            Handler handler = this.handler;
            if (handler != null) {
                handler.removeCallbacks(this.releaseRunnable);
            }
            this.released = true;
        }
    }
}
