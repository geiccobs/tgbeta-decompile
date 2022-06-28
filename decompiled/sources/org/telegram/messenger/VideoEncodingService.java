package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes4.dex */
public class VideoEncodingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;
    private int currentAccount;
    private int currentProgress;
    private String path;

    public VideoEncodingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopEncodingService);
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
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(4);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopEncodingService);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy video service");
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        String str;
        boolean z = true;
        if (id == NotificationCenter.fileUploadProgressChanged) {
            String fileName = (String) args[0];
            if (account == this.currentAccount && (str = this.path) != null && str.equals(fileName)) {
                Long loadedSize = (Long) args[1];
                Long totalSize = (Long) args[2];
                float progress = Math.min(1.0f, ((float) loadedSize.longValue()) / ((float) totalSize.longValue()));
                Boolean bool = (Boolean) args[3];
                int i = (int) (100.0f * progress);
                this.currentProgress = i;
                NotificationCompat.Builder builder = this.builder;
                if (i != 0) {
                    z = false;
                }
                builder.setProgress(100, i, z);
                try {
                    NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        } else if (id == NotificationCenter.stopEncodingService) {
            String filepath = (String) args[0];
            int account2 = ((Integer) args[1]).intValue();
            if (account2 != this.currentAccount) {
                return;
            }
            if (filepath == null || filepath.equals(this.path)) {
                stopSelf();
            }
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.path = intent.getStringExtra("path");
        int oldAccount = this.currentAccount;
        int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        this.currentAccount = intExtra;
        if (!UserConfig.isValidAccount(intExtra)) {
            stopSelf();
            return 2;
        }
        if (oldAccount != this.currentAccount) {
            NotificationCenter.getInstance(oldAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadProgressChanged);
        }
        boolean isGif = intent.getBooleanExtra("gif", false);
        if (this.path == null) {
            stopSelf();
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start video service");
        }
        if (this.builder == null) {
            NotificationsController.checkOtherNotificationsChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder;
            builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", org.telegram.messenger.beta.R.string.AppName));
            if (isGif) {
                this.builder.setTicker(LocaleController.getString("SendingGif", org.telegram.messenger.beta.R.string.SendingGif));
                this.builder.setContentText(LocaleController.getString("SendingGif", org.telegram.messenger.beta.R.string.SendingGif));
            } else {
                this.builder.setTicker(LocaleController.getString("SendingVideo", org.telegram.messenger.beta.R.string.SendingVideo));
                this.builder.setContentText(LocaleController.getString("SendingVideo", org.telegram.messenger.beta.R.string.SendingVideo));
            }
        }
        this.currentProgress = 0;
        this.builder.setProgress(100, 0, true);
        startForeground(4, this.builder.build());
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        return 2;
    }
}
