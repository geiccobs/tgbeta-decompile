package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes4.dex */
public class ImportingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;

    public ImportingService() {
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.stickersImportProgressChanged);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent arg2) {
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        try {
            stopForeground(true);
        } catch (Throwable th) {
        }
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(5);
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.stickersImportProgressChanged);
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy import service");
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if ((id == NotificationCenter.historyImportProgressChanged || id == NotificationCenter.stickersImportProgressChanged) && !hasImportingStickers() && !hasImportingStickers()) {
            stopSelf();
        }
    }

    private boolean hasImportingHistory() {
        for (int a = 0; a < 4; a++) {
            if (SendMessagesHelper.getInstance(a).isImportingHistory()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasImportingStickers() {
        for (int a = 0; a < 4; a++) {
            if (SendMessagesHelper.getInstance(a).isImportingStickers()) {
                return true;
            }
        }
        return false;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!hasImportingStickers() && !hasImportingHistory()) {
            stopSelf();
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start import service");
        }
        if (this.builder == null) {
            NotificationsController.checkOtherNotificationsChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder;
            builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", org.telegram.messenger.beta.R.string.AppName));
            if (hasImportingHistory()) {
                this.builder.setTicker(LocaleController.getString("ImporImportingService", org.telegram.messenger.beta.R.string.ImporImportingService));
                this.builder.setContentText(LocaleController.getString("ImporImportingService", org.telegram.messenger.beta.R.string.ImporImportingService));
            } else {
                this.builder.setTicker(LocaleController.getString("ImporImportingStickersService", org.telegram.messenger.beta.R.string.ImporImportingStickersService));
                this.builder.setContentText(LocaleController.getString("ImporImportingStickersService", org.telegram.messenger.beta.R.string.ImporImportingStickersService));
            }
        }
        this.builder.setProgress(100, 0, true);
        startForeground(5, this.builder.build());
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(5, this.builder.build());
        return 2;
    }
}
