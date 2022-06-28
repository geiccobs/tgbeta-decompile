package org.telegram.messenger;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import j$.util.function.Consumer;
import j$.util.stream.Stream;
import j$.wrappers.C$r8$wrapper$java$util$stream$Stream$VWRP;
import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import org.telegram.messenger.FilesMigrationService;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;
/* loaded from: classes4.dex */
public class FilesMigrationService extends Service {
    public static FilesMigrationBottomSheet filesMigrationBottomSheet;
    public static boolean hasOldFolder;
    public static boolean isRunning;
    private static boolean wasShown = false;
    long lastUpdateTime;
    private int movedFilesCount;
    private int totalFilesCount;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start() {
        Intent intent = new Intent(ApplicationLoader.applicationContext, FilesMigrationService.class);
        ApplicationLoader.applicationContext.startService(intent);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationsController.checkOtherNotificationsChannel();
        Notification notification = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(getText(org.telegram.messenger.beta.R.string.MigratingFiles)).setAutoCancel(false).setSmallIcon(org.telegram.messenger.beta.R.drawable.notification).build();
        isRunning = true;
        new AnonymousClass1().start();
        startForeground(301, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    /* renamed from: org.telegram.messenger.FilesMigrationService$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends Thread {
        AnonymousClass1() {
            FilesMigrationService.this = this$0;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            FilesMigrationService.this.migrateOldFolder();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FilesMigrationService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FilesMigrationService.AnonymousClass1.this.m278lambda$run$0$orgtelegrammessengerFilesMigrationService$1();
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-FilesMigrationService$1 */
        public /* synthetic */ void m278lambda$run$0$orgtelegrammessengerFilesMigrationService$1() {
            FilesMigrationService.isRunning = false;
            FilesMigrationService.this.stopForeground(true);
            FilesMigrationService.this.stopSelf();
        }
    }

    public void migrateOldFolder() {
        ArrayList<File> dirs;
        File path = Environment.getExternalStorageDirectory();
        if (Build.VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(SharedConfig.storageCacheDir) && (dirs = AndroidUtilities.getRootDirs()) != null) {
            int a = 0;
            int N = dirs.size();
            while (true) {
                if (a >= N) {
                    break;
                }
                File dir = dirs.get(a);
                if (!dir.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                    a++;
                } else {
                    path = dir;
                    break;
                }
            }
        }
        File newPath = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        File telegramPath = new File(newPath, "Telegram");
        File oldPath = new File(path, "Telegram");
        this.totalFilesCount = getFilesCount(oldPath);
        long moveStart = System.currentTimeMillis();
        if (oldPath.canRead() && oldPath.canWrite()) {
            moveDirectory(oldPath, telegramPath);
        }
        long dt = System.currentTimeMillis() - moveStart;
        FileLog.d("move time = " + dt);
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
        sharedPreferences.edit().putBoolean("migration_to_scoped_storage_finished", true).apply();
    }

    private int getFilesCount(File source) {
        if (!source.exists()) {
            return 0;
        }
        int count = 0;
        File[] fileList = source.listFiles();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    count += getFilesCount(fileList[i]);
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.Stream != java.util.stream.Stream<java.nio.file.Path> */
    private void moveDirectory(File source, final File target) {
        if (source.exists()) {
            if (!target.exists() && !target.mkdir()) {
                return;
            }
            try {
                Stream convert = C$r8$wrapper$java$util$stream$Stream$VWRP.convert(Files.list(source.toPath()));
                convert.forEach(new Consumer() { // from class: org.telegram.messenger.FilesMigrationService$$ExternalSyntheticLambda1
                    @Override // j$.util.function.Consumer
                    public final void accept(Object obj) {
                        FilesMigrationService.this.m276x55fd53fa(target, (Path) obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return consumer.getClass();
                    }
                });
                if (convert != null) {
                    convert.close();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                source.delete();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    /* renamed from: lambda$moveDirectory$0$org-telegram-messenger-FilesMigrationService */
    public /* synthetic */ void m276x55fd53fa(File target, Path path) {
        File dest = new File(target, path.getFileName().toString());
        if (!Files.isDirectory(path, new LinkOption[0])) {
            try {
                Files.move(path, dest.toPath(), new CopyOption[0]);
            } catch (Exception e) {
                FileLog.e((Throwable) e, false);
                try {
                    path.toFile().delete();
                } catch (Exception e1) {
                    FileLog.e(e1);
                }
            }
            this.movedFilesCount++;
            updateProgress();
            return;
        }
        moveDirectory(path.toFile(), dest);
    }

    private void updateProgress() {
        long time = System.currentTimeMillis();
        if (time - this.lastUpdateTime > 20 || this.movedFilesCount >= this.totalFilesCount - 1) {
            final int currentCount = this.movedFilesCount;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FilesMigrationService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FilesMigrationService.this.m277x13efee05(currentCount);
                }
            });
        }
    }

    /* renamed from: lambda$updateProgress$1$org-telegram-messenger-FilesMigrationService */
    public /* synthetic */ void m277x13efee05(int currentCount) {
        Notification notification = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(getText(org.telegram.messenger.beta.R.string.MigratingFiles)).setContentText(String.format("%s/%s", Integer.valueOf(currentCount), Integer.valueOf(this.totalFilesCount))).setSmallIcon(org.telegram.messenger.beta.R.drawable.notification).setAutoCancel(false).setProgress(this.totalFilesCount, currentCount, false).build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService("notification");
        mNotificationManager.notify(301, notification);
    }

    public static void checkBottomSheet(BaseFragment fragment) {
        ArrayList<File> dirs;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
        if (!Environment.isExternalStorageLegacy() || sharedPreferences.getBoolean("migration_to_scoped_storage_finished", false) || sharedPreferences.getInt("migration_to_scoped_storage_count", 0) >= 5 || wasShown || filesMigrationBottomSheet != null || isRunning) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 30) {
            File path = Environment.getExternalStorageDirectory();
            if (!TextUtils.isEmpty(SharedConfig.storageCacheDir) && (dirs = AndroidUtilities.getRootDirs()) != null) {
                int a = 0;
                int N = dirs.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    File dir = dirs.get(a);
                    if (!dir.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                        a++;
                    } else {
                        path = dir;
                        break;
                    }
                }
            }
            File oldDirectory = new File(path, "Telegram");
            hasOldFolder = oldDirectory.exists();
        }
        if (hasOldFolder) {
            FilesMigrationBottomSheet filesMigrationBottomSheet2 = new FilesMigrationBottomSheet(fragment);
            filesMigrationBottomSheet = filesMigrationBottomSheet2;
            filesMigrationBottomSheet2.show();
            wasShown = true;
            sharedPreferences.edit().putInt("migration_to_scoped_storage_count", sharedPreferences.getInt("migration_to_scoped_storage_count", 0) + 1).apply();
            return;
        }
        sharedPreferences.edit().putBoolean("migration_to_scoped_storage_finished", true).apply();
    }

    /* loaded from: classes4.dex */
    public static class FilesMigrationBottomSheet extends BottomSheet {
        BaseFragment fragment;

        @Override // org.telegram.ui.ActionBar.BottomSheet
        protected boolean canDismissWithSwipe() {
            return false;
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet
        protected boolean canDismissWithTouchOutside() {
            return false;
        }

        public FilesMigrationBottomSheet(BaseFragment fragment) {
            super(fragment.getParentActivity(), false);
            this.fragment = fragment;
            setCanceledOnTouchOutside(false);
            Context context = fragment.getParentActivity();
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            StickerImageView imageView = new StickerImageView(context, this.currentAccount);
            imageView.setStickerNum(7);
            imageView.getImageReceiver().setAutoRepeat(1);
            linearLayout.addView(imageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
            TextView title = new TextView(context);
            title.setGravity(GravityCompat.START);
            title.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            title.setTextSize(1, 20.0f);
            title.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            title.setText(LocaleController.getString("MigrateOldFolderTitle", org.telegram.messenger.beta.R.string.MigrateOldFolderTitle));
            linearLayout.addView(title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
            TextView description = new TextView(context);
            description.setGravity(GravityCompat.START);
            description.setTextSize(1, 15.0f);
            description.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            description.setText(AndroidUtilities.replaceTags(LocaleController.getString("MigrateOldFolderDescription", org.telegram.messenger.beta.R.string.MigrateOldFolderDescription)));
            linearLayout.addView(description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
            TextView buttonTextView = new TextView(context);
            buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView.setGravity(17);
            buttonTextView.setTextSize(1, 14.0f);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            buttonTextView.setText(LocaleController.getString("MigrateOldFolderButton", org.telegram.messenger.beta.R.string.MigrateOldFolderButton));
            buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 6.0f));
            linearLayout.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.messenger.FilesMigrationService$FilesMigrationBottomSheet$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FilesMigrationService.FilesMigrationBottomSheet.this.m279xaf48eacf(view);
                }
            });
            ScrollView scrollView = new ScrollView(context);
            scrollView.addView(linearLayout);
            setCustomView(scrollView);
        }

        /* renamed from: lambda$new$0$org-telegram-messenger-FilesMigrationService$FilesMigrationBottomSheet */
        public /* synthetic */ void m279xaf48eacf(View view) {
            migrateOldFolder();
        }

        public void migrateOldFolder() {
            Activity activity = this.fragment.getParentActivity();
            boolean canRead = true;
            boolean canWrite = activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
            if (activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                canRead = false;
            }
            if (!canRead || !canWrite) {
                ArrayList<String> permissions = new ArrayList<>();
                if (!canRead) {
                    permissions.add("android.permission.READ_EXTERNAL_STORAGE");
                }
                if (!canWrite) {
                    permissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
                }
                String[] string = new String[permissions.size()];
                activity.requestPermissions((String[]) permissions.toArray(string), 4);
                return;
            }
            FilesMigrationService.start();
            dismiss();
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
        public void dismiss() {
            super.dismiss();
            FilesMigrationService.filesMigrationBottomSheet = null;
        }
    }
}
