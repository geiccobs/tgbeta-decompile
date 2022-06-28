package org.telegram.messenger;

import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.NotificationBadge;
/* loaded from: classes4.dex */
public class NotificationBadge {
    private static final List<Class<? extends Badger>> BADGERS;
    private static Badger badger;
    private static ComponentName componentName;
    private static boolean initied;

    /* loaded from: classes4.dex */
    public interface Badger {
        void executeBadge(int i);

        List<String> getSupportLaunchers();
    }

    static {
        LinkedList linkedList = new LinkedList();
        BADGERS = linkedList;
        linkedList.add(AdwHomeBadger.class);
        linkedList.add(ApexHomeBadger.class);
        linkedList.add(NewHtcHomeBadger.class);
        linkedList.add(NovaHomeBadger.class);
        linkedList.add(SonyHomeBadger.class);
        linkedList.add(XiaomiHomeBadger.class);
        linkedList.add(AsusHomeBadger.class);
        linkedList.add(HuaweiHomeBadger.class);
        linkedList.add(OPPOHomeBader.class);
        linkedList.add(SamsungHomeBadger.class);
        linkedList.add(ZukHomeBadger.class);
        linkedList.add(VivoHomeBadger.class);
    }

    /* loaded from: classes4.dex */
    public static class AdwHomeBadger implements Badger {
        public static final String CLASSNAME = "CNAME";
        public static final String COUNT = "COUNT";
        public static final String INTENT_UPDATE_COUNTER = "org.adw.launcher.counter.SEND";
        public static final String PACKAGENAME = "PNAME";

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            final Intent intent = new Intent(INTENT_UPDATE_COUNTER);
            intent.putExtra(PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(CLASSNAME, NotificationBadge.componentName.getClassName());
            intent.putExtra(COUNT, badgeCount);
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge$AdwHomeBadger$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ApplicationLoader.applicationContext.sendBroadcast(intent);
                    }
                });
            }
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("org.adw.launcher", "org.adwfreak.launcher");
        }
    }

    /* loaded from: classes4.dex */
    public static class ApexHomeBadger implements Badger {
        private static final String CLASS = "class";
        private static final String COUNT = "count";
        private static final String INTENT_UPDATE_COUNTER = "com.anddoes.launcher.COUNTER_CHANGED";
        private static final String PACKAGENAME = "package";

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            final Intent intent = new Intent(INTENT_UPDATE_COUNTER);
            intent.putExtra(PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra("count", badgeCount);
            intent.putExtra(CLASS, NotificationBadge.componentName.getClassName());
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge$ApexHomeBadger$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ApplicationLoader.applicationContext.sendBroadcast(intent);
                    }
                });
            }
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.anddoes.launcher");
        }
    }

    /* loaded from: classes4.dex */
    public static class AsusHomeBadger implements Badger {
        private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";
        private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
        private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
            intent.putExtra(INTENT_EXTRA_PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, NotificationBadge.componentName.getClassName());
            intent.putExtra("badge_vip_count", 0);
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge$AsusHomeBadger$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ApplicationLoader.applicationContext.sendBroadcast(intent);
                    }
                });
            }
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.asus.launcher");
        }
    }

    /* loaded from: classes4.dex */
    public static class DefaultBadger implements Badger {
        private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";
        private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
        private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
            intent.putExtra(INTENT_EXTRA_PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, NotificationBadge.componentName.getClassName());
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge$DefaultBadger$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ApplicationLoader.applicationContext.sendBroadcast(intent);
                }
            });
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("fr.neamar.kiss", "com.quaap.launchtime", "com.quaap.launchtime_official");
        }
    }

    /* loaded from: classes4.dex */
    public static class HuaweiHomeBadger implements Badger {
        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            final Bundle localBundle = new Bundle();
            localBundle.putString("package", ApplicationLoader.applicationContext.getPackageName());
            localBundle.putString("class", NotificationBadge.componentName.getClassName());
            localBundle.putInt("badgenumber", badgeCount);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge$HuaweiHomeBadger$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationBadge.HuaweiHomeBadger.lambda$executeBadge$0(localBundle);
                }
            });
        }

        public static /* synthetic */ void lambda$executeBadge$0(Bundle localBundle) {
            try {
                ApplicationLoader.applicationContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", (String) null, localBundle);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.huawei.android.launcher");
        }
    }

    /* loaded from: classes4.dex */
    public static class NewHtcHomeBadger implements Badger {
        public static final String COUNT = "count";
        public static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";
        public static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";
        public static final String INTENT_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";
        public static final String INTENT_UPDATE_SHORTCUT = "com.htc.launcher.action.UPDATE_SHORTCUT";
        public static final String PACKAGENAME = "packagename";

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            final Intent intent1 = new Intent(INTENT_SET_NOTIFICATION);
            intent1.putExtra(EXTRA_COMPONENT, NotificationBadge.componentName.flattenToShortString());
            intent1.putExtra(EXTRA_COUNT, badgeCount);
            final Intent intent = new Intent(INTENT_UPDATE_SHORTCUT);
            intent.putExtra(PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(COUNT, badgeCount);
            if (NotificationBadge.canResolveBroadcast(intent1) || NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge$NewHtcHomeBadger$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationBadge.NewHtcHomeBadger.lambda$executeBadge$0(intent1, intent);
                    }
                });
            }
        }

        public static /* synthetic */ void lambda$executeBadge$0(Intent intent1, Intent intent) {
            ApplicationLoader.applicationContext.sendBroadcast(intent1);
            ApplicationLoader.applicationContext.sendBroadcast(intent);
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.htc.launcher");
        }
    }

    /* loaded from: classes4.dex */
    public static class NovaHomeBadger implements Badger {
        private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
        private static final String COUNT = "count";
        private static final String TAG = "tag";

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG, NotificationBadge.componentName.getPackageName() + "/" + NotificationBadge.componentName.getClassName());
            contentValues.put("count", Integer.valueOf(badgeCount));
            ApplicationLoader.applicationContext.getContentResolver().insert(Uri.parse(CONTENT_URI), contentValues);
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.teslacoilsw.launcher");
        }
    }

    /* loaded from: classes4.dex */
    public static class OPPOHomeBader implements Badger {
        private static final String INTENT_ACTION = "com.oppo.unsettledevent";
        private static final String INTENT_EXTRA_BADGEUPGRADE_COUNT = "app_badge_count";
        private static final String INTENT_EXTRA_BADGE_COUNT = "number";
        private static final String INTENT_EXTRA_BADGE_UPGRADENUMBER = "upgradeNumber";
        private static final String INTENT_EXTRA_PACKAGENAME = "pakeageName";
        private static final String PROVIDER_CONTENT_URI = "content://com.android.badge/badge";
        private int mCurrentTotalCount = -1;

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            if (this.mCurrentTotalCount == badgeCount) {
                return;
            }
            this.mCurrentTotalCount = badgeCount;
            executeBadgeByContentProvider(badgeCount);
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.oppo.launcher");
        }

        private void executeBadgeByContentProvider(int badgeCount) {
            try {
                Bundle extras = new Bundle();
                extras.putInt(INTENT_EXTRA_BADGEUPGRADE_COUNT, badgeCount);
                ApplicationLoader.applicationContext.getContentResolver().call(Uri.parse(PROVIDER_CONTENT_URI), "setAppBadgeCount", (String) null, extras);
            } catch (Throwable th) {
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class SamsungHomeBadger implements Badger {
        private static final String[] CONTENT_PROJECTION = {"_id", "class"};
        private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
        private static DefaultBadger defaultBadger;

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            try {
                if (defaultBadger == null) {
                    defaultBadger = new DefaultBadger();
                }
                defaultBadger.executeBadge(badgeCount);
            } catch (Exception e) {
            }
            Uri mUri = Uri.parse(CONTENT_URI);
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(mUri, CONTENT_PROJECTION, "package=?", new String[]{NotificationBadge.componentName.getPackageName()}, null);
                if (cursor != null) {
                    String entryActivityName = NotificationBadge.componentName.getClassName();
                    boolean entryActivityExist = false;
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        ContentValues contentValues = getContentValues(NotificationBadge.componentName, badgeCount, false);
                        contentResolver.update(mUri, contentValues, "_id=?", new String[]{String.valueOf(id)});
                        if (entryActivityName.equals(cursor.getString(cursor.getColumnIndex("class")))) {
                            entryActivityExist = true;
                        }
                    }
                    if (!entryActivityExist) {
                        ContentValues contentValues2 = getContentValues(NotificationBadge.componentName, badgeCount, true);
                        contentResolver.insert(mUri, contentValues2);
                    }
                }
            } finally {
                NotificationBadge.close(cursor);
            }
        }

        private ContentValues getContentValues(ComponentName componentName, int badgeCount, boolean isInsert) {
            ContentValues contentValues = new ContentValues();
            if (isInsert) {
                contentValues.put("package", componentName.getPackageName());
                contentValues.put("class", componentName.getClassName());
            }
            contentValues.put("badgecount", Integer.valueOf(badgeCount));
            return contentValues;
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.sec.android.app.launcher", "com.sec.android.app.twlauncher");
        }
    }

    /* loaded from: classes4.dex */
    public static class SonyHomeBadger implements Badger {
        private static final String INTENT_ACTION = "com.sonyericsson.home.action.UPDATE_BADGE";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME";
        private static final String INTENT_EXTRA_MESSAGE = "com.sonyericsson.home.intent.extra.badge.MESSAGE";
        private static final String INTENT_EXTRA_PACKAGE_NAME = "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME";
        private static final String INTENT_EXTRA_SHOW_MESSAGE = "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE";
        private static final String PROVIDER_COLUMNS_ACTIVITY_NAME = "activity_name";
        private static final String PROVIDER_COLUMNS_BADGE_COUNT = "badge_count";
        private static final String PROVIDER_COLUMNS_PACKAGE_NAME = "package_name";
        private static final String PROVIDER_CONTENT_URI = "content://com.sonymobile.home.resourceprovider/badge";
        private static final String SONY_HOME_PROVIDER_NAME = "com.sonymobile.home.resourceprovider";
        private static AsyncQueryHandler mQueryHandler;
        private final Uri BADGE_CONTENT_URI = Uri.parse(PROVIDER_CONTENT_URI);

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            if (sonyBadgeContentProviderExists()) {
                executeBadgeByContentProvider(badgeCount);
            } else {
                executeBadgeByBroadcast(badgeCount);
            }
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.sonyericsson.home", "com.sonymobile.home");
        }

        private static void executeBadgeByBroadcast(int badgeCount) {
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_PACKAGE_NAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, NotificationBadge.componentName.getClassName());
            intent.putExtra(INTENT_EXTRA_MESSAGE, String.valueOf(badgeCount));
            intent.putExtra(INTENT_EXTRA_SHOW_MESSAGE, badgeCount > 0);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge$SonyHomeBadger$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ApplicationLoader.applicationContext.sendBroadcast(intent);
                }
            });
        }

        private void executeBadgeByContentProvider(int badgeCount) {
            if (badgeCount < 0) {
                return;
            }
            if (mQueryHandler == null) {
                mQueryHandler = new AsyncQueryHandler(ApplicationLoader.applicationContext.getApplicationContext().getContentResolver()) { // from class: org.telegram.messenger.NotificationBadge.SonyHomeBadger.1
                    @Override // android.content.AsyncQueryHandler, android.os.Handler
                    public void handleMessage(Message msg) {
                        try {
                            super.handleMessage(msg);
                        } catch (Throwable th) {
                        }
                    }
                };
            }
            insertBadgeAsync(badgeCount, NotificationBadge.componentName.getPackageName(), NotificationBadge.componentName.getClassName());
        }

        private void insertBadgeAsync(int badgeCount, String packageName, String activityName) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PROVIDER_COLUMNS_BADGE_COUNT, Integer.valueOf(badgeCount));
            contentValues.put(PROVIDER_COLUMNS_PACKAGE_NAME, packageName);
            contentValues.put(PROVIDER_COLUMNS_ACTIVITY_NAME, activityName);
            mQueryHandler.startInsert(0, null, this.BADGE_CONTENT_URI, contentValues);
        }

        private static boolean sonyBadgeContentProviderExists() {
            ProviderInfo info = ApplicationLoader.applicationContext.getPackageManager().resolveContentProvider(SONY_HOME_PROVIDER_NAME, 0);
            if (info == null) {
                return false;
            }
            return true;
        }
    }

    /* loaded from: classes4.dex */
    public static class XiaomiHomeBadger implements Badger {
        public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
        public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
        public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            Object obj = "";
            try {
                Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
                Object miuiNotification = miuiNotificationClass.newInstance();
                Field field = miuiNotification.getClass().getDeclaredField("messageCount");
                field.setAccessible(true);
                field.set(miuiNotification, String.valueOf(badgeCount == 0 ? obj : Integer.valueOf(badgeCount)));
            } catch (Throwable th) {
                final Intent localIntent = new Intent(INTENT_ACTION);
                localIntent.putExtra(EXTRA_UPDATE_APP_COMPONENT_NAME, NotificationBadge.componentName.getPackageName() + "/" + NotificationBadge.componentName.getClassName());
                if (badgeCount != 0) {
                    obj = Integer.valueOf(badgeCount);
                }
                localIntent.putExtra(EXTRA_UPDATE_APP_MSG_TEXT, String.valueOf(obj));
                if (NotificationBadge.canResolveBroadcast(localIntent)) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge.XiaomiHomeBadger.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ApplicationLoader.applicationContext.sendBroadcast(localIntent);
                        }
                    });
                }
            }
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.miui.miuilite", "com.miui.home", "com.miui.miuihome", "com.miui.miuihome2", "com.miui.mihome", "com.miui.mihome2");
        }
    }

    /* loaded from: classes4.dex */
    public static class ZukHomeBadger implements Badger {
        private final Uri CONTENT_URI = Uri.parse("content://com.android.badge/badge");

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            final Bundle extra = new Bundle();
            extra.putInt("app_badge_count", badgeCount);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationBadge$ZukHomeBadger$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationBadge.ZukHomeBadger.this.m1092x7f3f44c2(extra);
                }
            });
        }

        /* renamed from: lambda$executeBadge$0$org-telegram-messenger-NotificationBadge$ZukHomeBadger */
        public /* synthetic */ void m1092x7f3f44c2(Bundle extra) {
            try {
                ApplicationLoader.applicationContext.getContentResolver().call(this.CONTENT_URI, "setAppBadgeCount", (String) null, extra);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.zui.launcher");
        }
    }

    /* loaded from: classes4.dex */
    public static class VivoHomeBadger implements Badger {
        @Override // org.telegram.messenger.NotificationBadge.Badger
        public void executeBadge(int badgeCount) {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra(RemoteConfigConstants.RequestFieldKey.PACKAGE_NAME, ApplicationLoader.applicationContext.getPackageName());
            intent.putExtra("className", NotificationBadge.componentName.getClassName());
            intent.putExtra("notificationNum", badgeCount);
            ApplicationLoader.applicationContext.sendBroadcast(intent);
        }

        @Override // org.telegram.messenger.NotificationBadge.Badger
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.vivo.launcher");
        }
    }

    public static boolean applyCount(int badgeCount) {
        try {
            if (badger == null && !initied) {
                initBadger();
                initied = true;
            }
            Badger badger2 = badger;
            if (badger2 == null) {
                return false;
            }
            badger2.executeBadge(badgeCount);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    private static boolean initBadger() {
        Context context = ApplicationLoader.applicationContext;
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            return false;
        }
        componentName = launchIntent.getComponent();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 65536);
        if (resolveInfo != null) {
            String currentHomePackage = resolveInfo.activityInfo.packageName;
            Iterator<Class<? extends Badger>> it = BADGERS.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Class<? extends Badger> b = it.next();
                Badger shortcutBadger = null;
                try {
                    shortcutBadger = b.newInstance();
                } catch (Exception e) {
                }
                if (shortcutBadger != null && shortcutBadger.getSupportLaunchers().contains(currentHomePackage)) {
                    badger = shortcutBadger;
                    break;
                }
            }
            if (badger != null) {
                return true;
            }
        }
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 65536);
        if (resolveInfos != null) {
            for (int a = 0; a < resolveInfos.size(); a++) {
                String currentHomePackage2 = resolveInfos.get(a).activityInfo.packageName;
                Iterator<Class<? extends Badger>> it2 = BADGERS.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    Class<? extends Badger> b2 = it2.next();
                    Badger shortcutBadger2 = null;
                    try {
                        shortcutBadger2 = b2.newInstance();
                    } catch (Exception e2) {
                    }
                    if (shortcutBadger2 != null && shortcutBadger2.getSupportLaunchers().contains(currentHomePackage2)) {
                        badger = shortcutBadger2;
                        break;
                    }
                }
                if (badger != null) {
                    break;
                }
            }
        }
        if (badger == null) {
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                badger = new XiaomiHomeBadger();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("ZUK")) {
                badger = new ZukHomeBadger();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
                badger = new OPPOHomeBader();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("VIVO")) {
                badger = new VivoHomeBadger();
            } else {
                badger = new DefaultBadger();
            }
        }
        return true;
    }

    public static boolean canResolveBroadcast(Intent intent) {
        PackageManager packageManager = ApplicationLoader.applicationContext.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }

    public static void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable th) {
            }
        }
    }
}
