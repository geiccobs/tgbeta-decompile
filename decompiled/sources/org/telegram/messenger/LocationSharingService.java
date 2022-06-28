package org.telegram.messenger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.ArrayList;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes4.dex */
public class LocationSharingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;
    private Handler handler;
    private Runnable runnable;

    public LocationSharingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.handler = new Handler();
        Runnable runnable = new Runnable() { // from class: org.telegram.messenger.LocationSharingService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                LocationSharingService.this.m365lambda$onCreate$1$orgtelegrammessengerLocationSharingService();
            }
        };
        this.runnable = runnable;
        this.handler.postDelayed(runnable, 1000L);
    }

    /* renamed from: lambda$onCreate$1$org-telegram-messenger-LocationSharingService */
    public /* synthetic */ void m365lambda$onCreate$1$orgtelegrammessengerLocationSharingService() {
        this.handler.postDelayed(this.runnable, 1000L);
        Utilities.stageQueue.postRunnable(LocationSharingService$$ExternalSyntheticLambda2.INSTANCE);
    }

    public static /* synthetic */ void lambda$onCreate$0() {
        for (int a = 0; a < 4; a++) {
            LocationController.getInstance(a).update();
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent arg2) {
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacks(this.runnable);
        }
        stopForeground(true);
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(6);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        Handler handler;
        if (id == NotificationCenter.liveLocationsChanged && (handler = this.handler) != null) {
            handler.post(new Runnable() { // from class: org.telegram.messenger.LocationSharingService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LocationSharingService.this.m364x7be92e23();
                }
            });
        }
    }

    /* renamed from: lambda$didReceivedNotification$2$org-telegram-messenger-LocationSharingService */
    public /* synthetic */ void m364x7be92e23() {
        ArrayList<LocationController.SharingLocationInfo> infos = getInfos();
        if (infos.isEmpty()) {
            stopSelf();
        } else {
            updateNotification(true);
        }
    }

    private ArrayList<LocationController.SharingLocationInfo> getInfos() {
        ArrayList<LocationController.SharingLocationInfo> infos = new ArrayList<>();
        for (int a = 0; a < 4; a++) {
            ArrayList<LocationController.SharingLocationInfo> arrayList = LocationController.getInstance(a).sharingLocationsUI;
            if (!arrayList.isEmpty()) {
                infos.addAll(arrayList);
            }
        }
        return infos;
    }

    private void updateNotification(boolean post) {
        String param;
        String str;
        if (this.builder == null) {
            return;
        }
        ArrayList<LocationController.SharingLocationInfo> infos = getInfos();
        if (infos.size() == 1) {
            LocationController.SharingLocationInfo info = infos.get(0);
            long dialogId = info.messageObject.getDialogId();
            int currentAccount = info.messageObject.currentAccount;
            if (DialogObject.isUserDialog(dialogId)) {
                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(dialogId));
                param = UserObject.getFirstName(user);
                str = LocaleController.getString("AttachLiveLocationIsSharing", org.telegram.messenger.beta.R.string.AttachLiveLocationIsSharing);
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-dialogId));
                if (chat != null) {
                    param = chat.title;
                } else {
                    param = "";
                }
                str = LocaleController.getString("AttachLiveLocationIsSharingChat", org.telegram.messenger.beta.R.string.AttachLiveLocationIsSharingChat);
            }
        } else {
            param = LocaleController.formatPluralString("Chats", infos.size(), new Object[0]);
            str = LocaleController.getString("AttachLiveLocationIsSharingChats", org.telegram.messenger.beta.R.string.AttachLiveLocationIsSharingChats);
        }
        String text = String.format(str, LocaleController.getString("AttachLiveLocation", org.telegram.messenger.beta.R.string.AttachLiveLocation), param);
        this.builder.setTicker(text);
        this.builder.setContentText(text);
        if (post) {
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(6, this.builder.build());
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (getInfos().isEmpty()) {
            stopSelf();
        }
        if (this.builder == null) {
            Intent intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent2.setAction("org.tmessages.openlocations");
            intent2.addCategory("android.intent.category.LAUNCHER");
            PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder;
            builder.setWhen(System.currentTimeMillis());
            this.builder.setSmallIcon(org.telegram.messenger.beta.R.drawable.live_loc);
            this.builder.setContentIntent(contentIntent);
            NotificationsController.checkOtherNotificationsChannel();
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", org.telegram.messenger.beta.R.string.AppName));
            Intent stopIntent = new Intent(ApplicationLoader.applicationContext, StopLiveLocationReceiver.class);
            this.builder.addAction(0, LocaleController.getString("StopLiveLocation", org.telegram.messenger.beta.R.string.StopLiveLocation), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, stopIntent, 134217728));
        }
        updateNotification(false);
        startForeground(6, this.builder.build());
        return 2;
    }
}
