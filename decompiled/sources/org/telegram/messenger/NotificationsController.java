package org.telegram.messenger;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import androidx.collection.LongSparseArray;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.content.LocusIdCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.PopupNotificationActivity;
/* loaded from: classes4.dex */
public class NotificationsController extends BaseController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = null;
    public static final int SETTING_MUTE_2_DAYS = 2;
    public static final int SETTING_MUTE_8_HOURS = 1;
    public static final int SETTING_MUTE_CUSTOM = 5;
    public static final int SETTING_MUTE_FOREVER = 3;
    public static final int SETTING_MUTE_HOUR = 0;
    public static final int SETTING_MUTE_UNMUTE = 4;
    public static final int SETTING_SOUND_OFF = 1;
    public static final int SETTING_SOUND_ON = 0;
    public static final int TYPE_CHANNEL = 2;
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_PRIVATE = 1;
    protected static AudioManager audioManager;
    private static final Object[] lockObjects;
    private static NotificationManagerCompat notificationManager;
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private boolean channelGroupsCreated;
    private Boolean groupsCreated;
    private boolean inChatSoundEnabled;
    public long lastNotificationChannelCreateTime;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private PowerManager.WakeLock notificationDelayWakelock;
    private String notificationGroup;
    public boolean showBadgeMessages;
    public boolean showBadgeMuted;
    public boolean showBadgeNumber;
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    public static String OTHER_NOTIFICATIONS_CHANNEL = null;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    public static long globalSecretChatId = DialogObject.makeEncryptedDialogId(1);
    private ArrayList<MessageObject> pushMessages = new ArrayList<>();
    private ArrayList<MessageObject> delayedPushMessages = new ArrayList<>();
    private LongSparseArray<SparseArray<MessageObject>> pushMessagesDict = new LongSparseArray<>();
    private LongSparseArray<MessageObject> fcmRandomMessagesDict = new LongSparseArray<>();
    private LongSparseArray<Point> smartNotificationsDialogs = new LongSparseArray<>();
    private LongSparseArray<Integer> pushDialogs = new LongSparseArray<>();
    private LongSparseArray<Integer> wearNotificationsIds = new LongSparseArray<>();
    private LongSparseArray<Integer> lastWearNotifiedMessageId = new LongSparseArray<>();
    private LongSparseArray<Integer> pushDialogsOverrideMention = new LongSparseArray<>();
    public ArrayList<MessageObject> popupMessages = new ArrayList<>();
    public ArrayList<MessageObject> popupReplyMessages = new ArrayList<>();
    private HashSet<Long> openedInBubbleDialogs = new HashSet<>();
    private long openedDialogId = 0;
    private int lastButtonId = 5000;
    private int total_unread_count = 0;
    private int personalCount = 0;
    private boolean notifyCheck = false;
    private int lastOnlineFromOtherDevice = 0;
    private int lastBadgeCount = -1;
    char[] spoilerChars = {10252, 10338, 10385, 10280};
    private int notificationId = this.currentAccount + 1;

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (Build.VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
            checkOtherNotificationsChannel();
        }
        audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
        Instance = new NotificationsController[4];
        lockObjects = new Object[4];
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    public static NotificationsController getInstance(int num) {
        NotificationsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (lockObjects[num]) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    NotificationsController[] notificationsControllerArr = Instance;
                    NotificationsController notificationsController = new NotificationsController(num);
                    localInstance = notificationsController;
                    notificationsControllerArr[num] = notificationsController;
                }
            }
        }
        return localInstance;
    }

    public NotificationsController(int instance) {
        super(instance);
        StringBuilder sb = new StringBuilder();
        sb.append("messages");
        sb.append(this.currentAccount == 0 ? "" : Integer.valueOf(this.currentAccount));
        this.notificationGroup = sb.toString();
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        this.inChatSoundEnabled = preferences.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = preferences.getBoolean("badgeNumber", true);
        this.showBadgeMuted = preferences.getBoolean("badgeNumberMuted", false);
        this.showBadgeMessages = preferences.getBoolean("badgeNumberMessages", true);
        notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
        systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
        try {
            audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            PowerManager pm = (PowerManager) ApplicationLoader.applicationContext.getSystemService("power");
            PowerManager.WakeLock newWakeLock = pm.newWakeLock(1, "telegram:notification_delay_lock");
            this.notificationDelayWakelock = newWakeLock;
            newWakeLock.setReferenceCounted(false);
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        this.notificationDelayRunnable = new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1101lambda$new$0$orgtelegrammessengerNotificationsController();
            }
        };
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1101lambda$new$0$orgtelegrammessengerNotificationsController() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("delay reached");
        }
        if (!this.delayedPushMessages.isEmpty()) {
            showOrUpdateNotification(true);
            this.delayedPushMessages.clear();
        }
        try {
            if (this.notificationDelayWakelock.isHeld()) {
                this.notificationDelayWakelock.release();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void checkOtherNotificationsChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        SharedPreferences preferences = null;
        if (OTHER_NOTIFICATIONS_CHANNEL == null) {
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            OTHER_NOTIFICATIONS_CHANNEL = preferences.getString("OtherKey", "Other3");
        }
        NotificationChannel notificationChannel = systemNotificationManager.getNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
        if (notificationChannel != null && notificationChannel.getImportance() == 0) {
            systemNotificationManager.deleteNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
            OTHER_NOTIFICATIONS_CHANNEL = null;
            notificationChannel = null;
        }
        if (OTHER_NOTIFICATIONS_CHANNEL == null) {
            if (preferences == null) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            }
            OTHER_NOTIFICATIONS_CHANNEL = "Other" + Utilities.random.nextLong();
            preferences.edit().putString("OtherKey", OTHER_NOTIFICATIONS_CHANNEL).commit();
        }
        if (notificationChannel == null) {
            NotificationChannel notificationChannel2 = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Internal notifications", 3);
            notificationChannel2.enableLights(false);
            notificationChannel2.enableVibration(false);
            notificationChannel2.setSound(null, null);
            try {
                systemNotificationManager.createNotificationChannel(notificationChannel2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void muteUntil(long did, int selectedTimeInSeconds) {
        long flags;
        if (did != 0) {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            SharedPreferences.Editor editor = preferences.edit();
            boolean defaultEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(did);
            if (selectedTimeInSeconds == Integer.MAX_VALUE) {
                if (!defaultEnabled) {
                    editor.remove("notify2_" + did);
                    flags = 0;
                } else {
                    editor.putInt("notify2_" + did, 2);
                    flags = 1;
                }
            } else {
                editor.putInt("notify2_" + did, 3);
                editor.putInt("notifyuntil_" + did, getConnectionsManager().getCurrentTime() + selectedTimeInSeconds);
                flags = (((long) selectedTimeInSeconds) << 32) | 1;
            }
            getInstance(this.currentAccount).removeNotificationsForDialog(did);
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(did, flags);
            editor.commit();
            TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                if (selectedTimeInSeconds != Integer.MAX_VALUE || defaultEnabled) {
                    dialog.notify_settings.mute_until = selectedTimeInSeconds;
                }
            }
            getInstance(this.currentAccount).updateServerNotificationsSettings(did);
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        this.channelGroupsCreated = false;
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1094lambda$cleanup$1$orgtelegrammessengerNotificationsController();
            }
        });
    }

    /* renamed from: lambda$cleanup$1$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1094lambda$cleanup$1$orgtelegrammessengerNotificationsController() {
        this.openedDialogId = 0L;
        this.total_unread_count = 0;
        this.personalCount = 0;
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        this.fcmRandomMessagesDict.clear();
        this.pushDialogs.clear();
        this.wearNotificationsIds.clear();
        this.lastWearNotifiedMessageId.clear();
        this.openedInBubbleDialogs.clear();
        this.delayedPushMessages.clear();
        this.notifyCheck = false;
        this.lastBadgeCount = 0;
        try {
            if (this.notificationDelayWakelock.isHeld()) {
                this.notificationDelayWakelock.release();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        dismissNotification();
        setBadge(getTotalAllUnreadCount());
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                systemNotificationManager.deleteNotificationChannelGroup("channels" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("groups" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("private" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("other" + this.currentAccount);
                String keyStart = this.currentAccount + "channel";
                List<NotificationChannel> list = systemNotificationManager.getNotificationChannels();
                int count = list.size();
                for (int a = 0; a < count; a++) {
                    NotificationChannel channel = list.get(a);
                    String id = channel.getId();
                    if (id.startsWith(keyStart)) {
                        try {
                            systemNotificationManager.deleteNotificationChannel(id);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel cleanup " + id);
                        }
                    }
                }
            } catch (Throwable e3) {
                FileLog.e(e3);
            }
        }
    }

    public void setInChatSoundEnabled(boolean value) {
        this.inChatSoundEnabled = value;
    }

    /* renamed from: lambda$setOpenedDialogId$2$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1123x8a2b000c(long dialog_id) {
        this.openedDialogId = dialog_id;
    }

    public void setOpenedDialogId(final long dialog_id) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1123x8a2b000c(dialog_id);
            }
        });
    }

    public void setOpenedInBubble(final long dialogId, final boolean opened) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1124x8c233e1d(opened, dialogId);
            }
        });
    }

    /* renamed from: lambda$setOpenedInBubble$3$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1124x8c233e1d(boolean opened, long dialogId) {
        if (opened) {
            this.openedInBubbleDialogs.add(Long.valueOf(dialogId));
        } else {
            this.openedInBubbleDialogs.remove(Long.valueOf(dialogId));
        }
    }

    public void setLastOnlineFromOtherDevice(final int time) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1122xfb3acdad(time);
            }
        });
    }

    /* renamed from: lambda$setLastOnlineFromOtherDevice$4$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1122xfb3acdad(int time) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + time);
        }
        this.lastOnlineFromOtherDevice = time;
    }

    public void removeNotificationsForDialog(long did) {
        processReadMessages(null, did, 0, Integer.MAX_VALUE, false);
        LongSparseIntArray dialogsToUpdate = new LongSparseIntArray();
        dialogsToUpdate.put(did, 0);
        processDialogsUpdateRead(dialogsToUpdate);
    }

    public boolean hasMessagesToReply() {
        for (int a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            if ((!messageObject.messageOwner.mentioned || !(messageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialog_id) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                return true;
            }
        }
        return false;
    }

    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1099x16c2e2d7();
            }
        });
    }

    /* renamed from: lambda$forceShowPopupForReply$6$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1099x16c2e2d7() {
        final ArrayList<MessageObject> popupArray = new ArrayList<>();
        for (int a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            if ((!messageObject.messageOwner.mentioned || !(messageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialog_id) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                popupArray.add(0, messageObject);
            }
        }
        if (!popupArray.isEmpty() && !AndroidUtilities.needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1098xdcf840f8(popupArray);
                }
            });
        }
    }

    /* renamed from: lambda$forceShowPopupForReply$5$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1098xdcf840f8(ArrayList popupArray) {
        this.popupReplyMessages = popupArray;
        Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
        popupIntent.putExtra("force", true);
        popupIntent.putExtra("currentAccount", this.currentAccount);
        popupIntent.setFlags(268763140);
        ApplicationLoader.applicationContext.startActivity(popupIntent);
        Intent it = new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        ApplicationLoader.applicationContext.sendBroadcast(it);
    }

    public void removeDeletedMessagesFromNotifications(final LongSparseArray<ArrayList<Integer>> deletedMessages) {
        final ArrayList<MessageObject> popupArrayRemove = new ArrayList<>(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1120x6483a23c(deletedMessages, popupArrayRemove);
            }
        });
    }

    /* renamed from: lambda$removeDeletedMessagesFromNotifications$9$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1120x6483a23c(LongSparseArray deletedMessages, final ArrayList popupArrayRemove) {
        long key;
        Integer newCount;
        LongSparseArray longSparseArray = deletedMessages;
        int old_unread_count = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        int a = 0;
        while (true) {
            int i = 0;
            if (a >= deletedMessages.size()) {
                break;
            }
            long key2 = longSparseArray.keyAt(a);
            SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(key2);
            if (sparseArray != null) {
                ArrayList<Integer> mids = (ArrayList) longSparseArray.get(key2);
                int b = 0;
                int N = mids.size();
                while (b < N) {
                    int mid = mids.get(b).intValue();
                    MessageObject messageObject = sparseArray.get(mid);
                    if (messageObject == null) {
                        key = key2;
                    } else {
                        key = key2;
                        long dialogId = messageObject.getDialogId();
                        Integer currentCount = this.pushDialogs.get(dialogId);
                        if (currentCount == null) {
                            currentCount = Integer.valueOf(i);
                        }
                        Integer newCount2 = Integer.valueOf(currentCount.intValue() - 1);
                        if (newCount2.intValue() > 0) {
                            newCount = newCount2;
                        } else {
                            Integer newCount3 = Integer.valueOf(i);
                            this.smartNotificationsDialogs.remove(dialogId);
                            newCount = newCount3;
                        }
                        if (!newCount.equals(currentCount)) {
                            int intValue = this.total_unread_count - currentCount.intValue();
                            this.total_unread_count = intValue;
                            this.total_unread_count = intValue + newCount.intValue();
                            this.pushDialogs.put(dialogId, newCount);
                        }
                        if (newCount.intValue() == 0) {
                            this.pushDialogs.remove(dialogId);
                            this.pushDialogsOverrideMention.remove(dialogId);
                        }
                        sparseArray.remove(mid);
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        popupArrayRemove.add(messageObject);
                    }
                    b++;
                    key2 = key;
                    i = 0;
                }
                long key3 = key2;
                if (sparseArray.size() == 0) {
                    this.pushMessagesDict.remove(key3);
                }
            }
            a++;
            longSparseArray = deletedMessages;
        }
        if (!popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1118xf0ee5e7e(popupArrayRemove);
                }
            });
        }
        if (old_unread_count != this.total_unread_count) {
            if (this.notifyCheck) {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            final int pushDialogsCount = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1119x2ab9005d(pushDialogsCount);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* renamed from: lambda$removeDeletedMessagesFromNotifications$7$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1118xf0ee5e7e(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$removeDeletedMessagesFromNotifications$8$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1119x2ab9005d(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void removeDeletedHisoryFromNotifications(final LongSparseIntArray deletedMessages) {
        final ArrayList<MessageObject> popupArrayRemove = new ArrayList<>(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1117xc147f6e8(deletedMessages, popupArrayRemove);
            }
        });
    }

    /* renamed from: lambda$removeDeletedHisoryFromNotifications$12$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1117xc147f6e8(LongSparseIntArray deletedMessages, final ArrayList popupArrayRemove) {
        long key;
        int i;
        LongSparseIntArray longSparseIntArray = deletedMessages;
        int old_unread_count = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        int a = 0;
        while (a < deletedMessages.size()) {
            long key2 = longSparseIntArray.keyAt(a);
            long dialogId = -key2;
            long id = longSparseIntArray.get(key2);
            Integer currentCount = this.pushDialogs.get(dialogId);
            if (currentCount == null) {
                currentCount = 0;
            }
            Integer newCount = currentCount;
            int c = 0;
            while (c < this.pushMessages.size()) {
                MessageObject messageObject = this.pushMessages.get(c);
                if (messageObject.getDialogId() == dialogId) {
                    key = key2;
                    if (messageObject.getId() > id) {
                        i = 1;
                    } else {
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(dialogId);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(dialogId);
                            }
                        }
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        c--;
                        if (isPersonalMessage(messageObject)) {
                            i = 1;
                            this.personalCount--;
                        } else {
                            i = 1;
                        }
                        popupArrayRemove.add(messageObject);
                        newCount = Integer.valueOf(newCount.intValue() - i);
                    }
                } else {
                    key = key2;
                    i = 1;
                }
                c += i;
                key2 = key;
            }
            if (newCount.intValue() <= 0) {
                newCount = 0;
                this.smartNotificationsDialogs.remove(dialogId);
            }
            if (!newCount.equals(currentCount)) {
                int intValue = this.total_unread_count - currentCount.intValue();
                this.total_unread_count = intValue;
                this.total_unread_count = intValue + newCount.intValue();
                this.pushDialogs.put(dialogId, newCount);
            }
            if (newCount.intValue() == 0) {
                this.pushDialogs.remove(dialogId);
                this.pushDialogsOverrideMention.remove(dialogId);
            }
            a++;
            longSparseIntArray = deletedMessages;
        }
        if (popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1115x4db2b32a(popupArrayRemove);
                }
            });
        }
        if (old_unread_count != this.total_unread_count) {
            if (this.notifyCheck) {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            final int pushDialogsCount = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1116x877d5509(pushDialogsCount);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* renamed from: lambda$removeDeletedHisoryFromNotifications$10$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1115x4db2b32a(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$removeDeletedHisoryFromNotifications$11$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1116x877d5509(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processReadMessages(final LongSparseIntArray inbox, final long dialogId, final int maxDate, final int maxId, final boolean isPopup) {
        final ArrayList<MessageObject> popupArrayRemove = new ArrayList<>(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1114x6297df38(inbox, popupArrayRemove, dialogId, maxId, maxDate, isPopup);
            }
        });
    }

    /* renamed from: lambda$processReadMessages$14$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1114x6297df38(LongSparseIntArray inbox, final ArrayList popupArrayRemove, long dialogId, int maxId, int maxDate, boolean isPopup) {
        long did;
        long did2;
        long j = 0;
        if (inbox != null) {
            int b = 0;
            while (b < inbox.size()) {
                long key = inbox.keyAt(b);
                int messageId = inbox.get(key);
                int a = 0;
                while (a < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(a);
                    if (!messageObject.messageOwner.from_scheduled && messageObject.getDialogId() == key && messageObject.getId() <= messageId) {
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        popupArrayRemove.add(messageObject);
                        if (messageObject.messageOwner.peer_id.channel_id != j) {
                            did2 = -messageObject.messageOwner.peer_id.channel_id;
                        } else {
                            did2 = 0;
                        }
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(did2);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(did2);
                            }
                        }
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(a);
                        a--;
                    }
                    a++;
                    j = 0;
                }
                b++;
                j = 0;
            }
        }
        if (dialogId != 0 && (maxId != 0 || maxDate != 0)) {
            int a2 = 0;
            while (a2 < this.pushMessages.size()) {
                MessageObject messageObject2 = this.pushMessages.get(a2);
                if (messageObject2.getDialogId() == dialogId) {
                    boolean remove = false;
                    if (maxDate != 0) {
                        if (messageObject2.messageOwner.date <= maxDate) {
                            remove = true;
                        }
                    } else if (!isPopup) {
                        if (messageObject2.getId() <= maxId || maxId < 0) {
                            remove = true;
                        }
                    } else if (messageObject2.getId() == maxId || maxId < 0) {
                        remove = true;
                    }
                    if (remove) {
                        if (isPersonalMessage(messageObject2)) {
                            this.personalCount--;
                        }
                        if (messageObject2.messageOwner.peer_id.channel_id != 0) {
                            did = -messageObject2.messageOwner.peer_id.channel_id;
                        } else {
                            did = 0;
                        }
                        SparseArray<MessageObject> sparseArray2 = this.pushMessagesDict.get(did);
                        if (sparseArray2 != null) {
                            sparseArray2.remove(messageObject2.getId());
                            if (sparseArray2.size() == 0) {
                                this.pushMessagesDict.remove(did);
                            }
                        }
                        this.pushMessages.remove(a2);
                        this.delayedPushMessages.remove(messageObject2);
                        popupArrayRemove.add(messageObject2);
                        a2--;
                    }
                }
                a2++;
            }
        }
        if (!popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1113x28cd3d59(popupArrayRemove);
                }
            });
        }
    }

    /* renamed from: lambda$processReadMessages$13$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1113x28cd3d59(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    private int addToPopupMessages(ArrayList<MessageObject> popupArrayAdd, MessageObject messageObject, long dialogId, boolean isChannel, SharedPreferences preferences) {
        int popup = 0;
        if (!DialogObject.isEncryptedDialog(dialogId)) {
            if (preferences.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + dialogId, false)) {
                popup = preferences.getInt("popup_" + dialogId, 0);
            }
            if (popup == 0) {
                if (isChannel) {
                    popup = preferences.getInt("popupChannel", 0);
                } else {
                    popup = preferences.getInt(DialogObject.isChatDialog(dialogId) ? "popupGroup" : "popupAll", 0);
                }
            } else if (popup == 1) {
                popup = 3;
            } else if (popup == 2) {
                popup = 0;
            }
        }
        if (popup != 0 && messageObject.messageOwner.peer_id.channel_id != 0 && !messageObject.isSupergroup()) {
            popup = 0;
        }
        if (popup != 0) {
            popupArrayAdd.add(0, messageObject);
        }
        return popup;
    }

    public void processEditedMessages(final LongSparseArray<ArrayList<MessageObject>> editedMessages) {
        if (editedMessages.size() == 0) {
            return;
        }
        new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1107xd706556a(editedMessages);
            }
        });
    }

    /* renamed from: lambda$processEditedMessages$15$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1107xd706556a(LongSparseArray editedMessages) {
        long did;
        LongSparseArray longSparseArray = editedMessages;
        boolean updated = false;
        int a = 0;
        int N = editedMessages.size();
        while (a < N) {
            long dialogId = longSparseArray.keyAt(a);
            if (this.pushDialogs.indexOfKey(dialogId) >= 0) {
                ArrayList<MessageObject> messages = (ArrayList) longSparseArray.valueAt(a);
                int N2 = messages.size();
                for (int b = 0; b < N2; b++) {
                    MessageObject messageObject = messages.get(b);
                    if (messageObject.messageOwner.peer_id.channel_id != 0) {
                        did = -messageObject.messageOwner.peer_id.channel_id;
                    } else {
                        did = 0;
                    }
                    SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(did);
                    if (sparseArray == null) {
                        break;
                    }
                    MessageObject oldMessage = sparseArray.get(messageObject.getId());
                    if (oldMessage != null && oldMessage.isReactionPush) {
                        oldMessage = null;
                    }
                    if (oldMessage != null) {
                        updated = true;
                        sparseArray.put(messageObject.getId(), messageObject);
                        int idx = this.pushMessages.indexOf(oldMessage);
                        if (idx >= 0) {
                            this.pushMessages.set(idx, messageObject);
                        }
                        int idx2 = this.delayedPushMessages.indexOf(oldMessage);
                        if (idx2 >= 0) {
                            this.delayedPushMessages.set(idx2, messageObject);
                        }
                    }
                }
            }
            a++;
            longSparseArray = editedMessages;
        }
        if (updated) {
            showOrUpdateNotification(false);
        }
    }

    public void processNewMessages(final ArrayList<MessageObject> messageObjects, final boolean isLast, final boolean isFcm, final CountDownLatch countDownLatch) {
        if (messageObjects.isEmpty()) {
            if (countDownLatch != null) {
                countDownLatch.countDown();
                return;
            }
            return;
        }
        final ArrayList<MessageObject> popupArrayAdd = new ArrayList<>(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1112xffba819a(messageObjects, popupArrayAdd, isFcm, isLast, countDownLatch);
            }
        });
    }

    /* renamed from: lambda$processNewMessages$18$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1112xffba819a(ArrayList messageObjects, final ArrayList popupArrayAdd, boolean isFcm, boolean isLast, CountDownLatch countDownLatch) {
        Boolean isChannel;
        boolean canAddValue;
        Integer override;
        LongSparseArray<Boolean> settingsCache;
        boolean allowPinned;
        int a;
        boolean added;
        int popup;
        long randomId;
        boolean isChannel2;
        long did;
        long randomId2;
        boolean value;
        SparseArray<MessageObject> sparseArray;
        long dialogId;
        boolean edited;
        int i;
        long dialogId2;
        SparseArray<MessageObject> sparseArray2;
        boolean value2;
        SparseArray<MessageObject> sparseArray3;
        boolean added2;
        int popup2;
        MessageObject messageObject;
        ArrayList arrayList = messageObjects;
        LongSparseArray<Boolean> settingsCache2 = new LongSparseArray<>();
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        boolean allowPinned2 = preferences.getBoolean("PinnedMessages", true);
        boolean messageObject2 = false;
        boolean edited2 = false;
        int mid = 0;
        boolean hasScheduled = false;
        int a2 = 0;
        while (a2 < messageObjects.size()) {
            MessageObject messageObject3 = (MessageObject) arrayList.get(a2);
            if (messageObject3.messageOwner != null) {
                if (!messageObject3.isImportedForward() && !(messageObject3.messageOwner.action instanceof TLRPC.TL_messageActionSetMessagesTTL)) {
                    if (messageObject3.messageOwner.silent) {
                        if (!(messageObject3.messageOwner.action instanceof TLRPC.TL_messageActionContactSignUp)) {
                            if (messageObject3.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined) {
                                a = a2;
                                settingsCache = settingsCache2;
                                allowPinned = allowPinned2;
                                added = messageObject2;
                                popup = mid;
                                mid = popup;
                                messageObject2 = added;
                                a2 = a + 1;
                                arrayList = messageObjects;
                                allowPinned2 = allowPinned;
                                settingsCache2 = settingsCache;
                            }
                        }
                    }
                }
                a = a2;
                settingsCache = settingsCache2;
                allowPinned = allowPinned2;
                added = messageObject2;
                popup = mid;
                mid = popup;
                messageObject2 = added;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache2 = settingsCache;
            }
            int mid2 = messageObject3.getId();
            if (messageObject3.isFcmMessage()) {
                a = a2;
                randomId = messageObject3.messageOwner.random_id;
            } else {
                a = a2;
                randomId = 0;
            }
            allowPinned = allowPinned2;
            long dialogId3 = messageObject3.getDialogId();
            if (messageObject3.isFcmMessage()) {
                isChannel2 = messageObject3.localChannel;
            } else {
                boolean isChannel3 = DialogObject.isChatDialog(dialogId3);
                if (isChannel3) {
                    TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-dialogId3));
                    boolean isChannel4 = ChatObject.isChannel(chat) && !chat.megagroup;
                    isChannel2 = isChannel4;
                } else {
                    isChannel2 = false;
                }
            }
            if (messageObject3.messageOwner.peer_id.channel_id != 0) {
                did = -messageObject3.messageOwner.peer_id.channel_id;
            } else {
                did = 0;
            }
            SparseArray<MessageObject> sparseArray4 = this.pushMessagesDict.get(did);
            MessageObject oldMessageObject = sparseArray4 != null ? sparseArray4.get(mid2) : null;
            if (oldMessageObject == null) {
                randomId2 = randomId;
                if (messageObject3.messageOwner.random_id == 0) {
                    settingsCache = settingsCache2;
                } else {
                    settingsCache = settingsCache2;
                    oldMessageObject = this.fcmRandomMessagesDict.get(messageObject3.messageOwner.random_id);
                    if (oldMessageObject != null) {
                        this.fcmRandomMessagesDict.remove(messageObject3.messageOwner.random_id);
                    }
                }
            } else {
                randomId2 = randomId;
                settingsCache = settingsCache2;
            }
            MessageObject oldMessageObject2 = oldMessageObject;
            if (oldMessageObject2 == null) {
                added = messageObject2;
                popup = mid;
                long randomId3 = randomId2;
                long did2 = did;
                if (!edited2) {
                    if (isFcm) {
                        getMessagesStorage().putPushMessage(messageObject3);
                    }
                    if (dialogId3 == this.openedDialogId && ApplicationLoader.isScreenOn) {
                        if (!isFcm) {
                            playInChatSound();
                        }
                    } else {
                        if (messageObject3.messageOwner.mentioned) {
                            if (allowPinned || !(messageObject3.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) {
                                dialogId3 = messageObject3.getFromChatId();
                            }
                        }
                        if (isPersonalMessage(messageObject3)) {
                            this.personalCount++;
                        }
                        DialogObject.isChatDialog(dialogId3);
                        LongSparseArray<Boolean> settingsCache3 = settingsCache;
                        int index = settingsCache3.indexOfKey(dialogId3);
                        if (index >= 0) {
                            sparseArray = sparseArray4;
                            value = settingsCache3.valueAt(index).booleanValue();
                        } else {
                            int notifyOverride = getNotifyOverride(preferences, dialogId3);
                            if (notifyOverride == -1) {
                                value2 = isGlobalNotificationsEnabled(dialogId3, Boolean.valueOf(isChannel2));
                            } else {
                                value2 = notifyOverride != 2;
                            }
                            sparseArray = sparseArray4;
                            settingsCache3.put(dialogId3, Boolean.valueOf(value2));
                            value = value2;
                        }
                        if (!value) {
                            settingsCache = settingsCache3;
                            dialogId = dialogId3;
                            edited = edited2;
                        } else {
                            if (!isFcm) {
                                settingsCache = settingsCache3;
                                long j = dialogId3;
                                dialogId = dialogId3;
                                dialogId2 = dialogId3;
                                edited = edited2;
                                i = 0;
                                popup = addToPopupMessages(popupArrayAdd, messageObject3, j, isChannel2, preferences);
                            } else {
                                settingsCache = settingsCache3;
                                dialogId = dialogId3;
                                edited = edited2;
                                i = 0;
                                dialogId2 = dialogId3;
                            }
                            if (!hasScheduled) {
                                hasScheduled = messageObject3.messageOwner.from_scheduled;
                            }
                            this.delayedPushMessages.add(messageObject3);
                            this.pushMessages.add(i, messageObject3);
                            if (mid2 != 0) {
                                if (sparseArray != null) {
                                    sparseArray2 = sparseArray;
                                } else {
                                    sparseArray2 = new SparseArray<>();
                                    this.pushMessagesDict.put(did2, sparseArray2);
                                }
                                sparseArray2.put(mid2, messageObject3);
                            } else if (randomId3 != 0) {
                                this.fcmRandomMessagesDict.put(randomId3, messageObject3);
                            }
                            if (dialogId2 != dialogId) {
                                Integer current = this.pushDialogsOverrideMention.get(dialogId2);
                                this.pushDialogsOverrideMention.put(dialogId2, Integer.valueOf(current == null ? 1 : current.intValue() + 1));
                            }
                        }
                        if (messageObject3.isReactionPush) {
                            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
                            sparseBooleanArray.put(mid2, true);
                            getMessagesController().checkUnreadReactions(dialogId, sparseBooleanArray);
                        }
                        edited2 = edited;
                        messageObject2 = true;
                        mid = popup;
                        a2 = a + 1;
                        arrayList = messageObjects;
                        allowPinned2 = allowPinned;
                        settingsCache2 = settingsCache;
                    }
                }
                mid = popup;
                messageObject2 = added;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache2 = settingsCache;
            } else if (!oldMessageObject2.isFcmMessage()) {
                added = messageObject2;
                popup = mid;
                mid = popup;
                messageObject2 = added;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache2 = settingsCache;
            } else {
                if (sparseArray4 != null) {
                    sparseArray3 = sparseArray4;
                } else {
                    SparseArray<MessageObject> sparseArray5 = new SparseArray<>();
                    this.pushMessagesDict.put(did, sparseArray5);
                    sparseArray3 = sparseArray5;
                }
                sparseArray3.put(mid2, messageObject3);
                int idxOld = this.pushMessages.indexOf(oldMessageObject2);
                if (idxOld >= 0) {
                    this.pushMessages.set(idxOld, messageObject3);
                    added2 = messageObject2;
                    messageObject = messageObject3;
                    popup2 = addToPopupMessages(popupArrayAdd, messageObject3, dialogId3, isChannel2, preferences);
                } else {
                    added2 = messageObject2;
                    popup2 = mid;
                    messageObject = messageObject3;
                }
                if (isFcm) {
                    boolean z = messageObject.localEdit;
                    edited2 = z;
                    if (z) {
                        getMessagesStorage().putPushMessage(messageObject);
                    }
                }
                mid = popup2;
                messageObject2 = added2;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache2 = settingsCache;
            }
        }
        boolean added3 = messageObject2;
        boolean edited3 = edited2;
        final int popup3 = mid;
        if (added3) {
            this.notifyCheck = isLast;
        }
        if (!popupArrayAdd.isEmpty() && !AndroidUtilities.needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1110x8c253ddc(popupArrayAdd, popup3);
                }
            });
        }
        if (isFcm || hasScheduled) {
            if (edited3) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else if (added3) {
                MessageObject messageObject4 = (MessageObject) messageObjects.get(0);
                long dialog_id = messageObject4.getDialogId();
                if (messageObject4.isFcmMessage()) {
                    isChannel = Boolean.valueOf(messageObject4.localChannel);
                } else {
                    isChannel = null;
                }
                int old_unread_count = this.total_unread_count;
                int notifyOverride2 = getNotifyOverride(preferences, dialog_id);
                if (notifyOverride2 == -1) {
                    canAddValue = isGlobalNotificationsEnabled(dialog_id, isChannel);
                } else {
                    canAddValue = notifyOverride2 != 2;
                }
                Integer currentCount = this.pushDialogs.get(dialog_id);
                int newCount = currentCount != null ? currentCount.intValue() + 1 : 1;
                if (this.notifyCheck && !canAddValue && (override = this.pushDialogsOverrideMention.get(dialog_id)) != null && override.intValue() != 0) {
                    canAddValue = true;
                    newCount = override.intValue();
                }
                if (canAddValue) {
                    if (currentCount != null) {
                        this.total_unread_count -= currentCount.intValue();
                    }
                    this.total_unread_count += newCount;
                    this.pushDialogs.put(dialog_id, Integer.valueOf(newCount));
                }
                if (old_unread_count != this.total_unread_count) {
                    this.delayedPushMessages.clear();
                    showOrUpdateNotification(this.notifyCheck);
                    final int pushDialogsCount = this.pushDialogs.size();
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            NotificationsController.this.m1111xc5efdfbb(pushDialogsCount);
                        }
                    });
                }
                this.notifyCheck = false;
                if (this.showBadgeNumber) {
                    setBadge(getTotalAllUnreadCount());
                }
            }
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* renamed from: lambda$processNewMessages$16$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1110x8c253ddc(ArrayList popupArrayAdd, int popupFinal) {
        this.popupMessages.addAll(0, popupArrayAdd);
        if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
            if (popupFinal == 3 || ((popupFinal == 1 && ApplicationLoader.isScreenOn) || (popupFinal == 2 && !ApplicationLoader.isScreenOn))) {
                Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                popupIntent.setFlags(268763140);
                try {
                    ApplicationLoader.applicationContext.startActivity(popupIntent);
                } catch (Throwable th) {
                }
            }
        }
    }

    /* renamed from: lambda$processNewMessages$17$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1111xc5efdfbb(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(final LongSparseIntArray dialogsToUpdate) {
        final ArrayList<MessageObject> popupArrayToRemove = new ArrayList<>();
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1106xc2d50a00(dialogsToUpdate, popupArrayToRemove);
            }
        });
    }

    /* renamed from: lambda$processDialogsUpdateRead$21$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1106xc2d50a00(LongSparseIntArray dialogsToUpdate, final ArrayList popupArrayToRemove) {
        boolean z;
        long dialogId;
        long did;
        Integer override;
        TLRPC.Chat chat;
        int old_unread_count = this.total_unread_count;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        int b = 0;
        while (true) {
            boolean canAddValue = false;
            z = true;
            int i = 1;
            if (b >= dialogsToUpdate.size()) {
                break;
            }
            long dialogId2 = dialogsToUpdate.keyAt(b);
            Integer currentCount = this.pushDialogs.get(dialogId2);
            int newCount = dialogsToUpdate.get(dialogId2);
            if (DialogObject.isChatDialog(dialogId2) && ((chat = getMessagesController().getChat(Long.valueOf(-dialogId2))) == null || chat.min || ChatObject.isNotInChat(chat))) {
                newCount = 0;
            }
            int notifyOverride = getNotifyOverride(preferences, dialogId2);
            if (notifyOverride == -1) {
                canAddValue = isGlobalNotificationsEnabled(dialogId2);
            } else if (notifyOverride != 2) {
                canAddValue = true;
            }
            if (this.notifyCheck && !canAddValue && (override = this.pushDialogsOverrideMention.get(dialogId2)) != null && override.intValue() != 0) {
                canAddValue = true;
                newCount = override.intValue();
            }
            if (newCount == 0) {
                this.smartNotificationsDialogs.remove(dialogId2);
            }
            if (newCount < 0) {
                if (currentCount == null) {
                    b++;
                } else {
                    newCount += currentCount.intValue();
                }
            }
            if ((canAddValue || newCount == 0) && currentCount != null) {
                this.total_unread_count -= currentCount.intValue();
            }
            if (newCount == 0) {
                this.pushDialogs.remove(dialogId2);
                this.pushDialogsOverrideMention.remove(dialogId2);
                int a = 0;
                while (a < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(a);
                    if (messageObject.messageOwner.from_scheduled || messageObject.getDialogId() != dialogId2) {
                        dialogId = dialogId2;
                    } else {
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount -= i;
                        }
                        this.pushMessages.remove(a);
                        a--;
                        this.delayedPushMessages.remove(messageObject);
                        dialogId = dialogId2;
                        if (messageObject.messageOwner.peer_id.channel_id != 0) {
                            did = -messageObject.messageOwner.peer_id.channel_id;
                        } else {
                            did = 0;
                        }
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(did);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(did);
                            }
                        }
                        popupArrayToRemove.add(messageObject);
                    }
                    i = 1;
                    a++;
                    dialogId2 = dialogId;
                }
            } else if (canAddValue) {
                this.total_unread_count += newCount;
                this.pushDialogs.put(dialogId2, Integer.valueOf(newCount));
            }
            b++;
        }
        if (!popupArrayToRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1104x91a07ef7(popupArrayToRemove);
                }
            });
        }
        if (old_unread_count != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            final int pushDialogsCount = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1105x890a6821(pushDialogsCount);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* renamed from: lambda$processDialogsUpdateRead$19$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1104x91a07ef7(ArrayList popupArrayToRemove) {
        int size = popupArrayToRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayToRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$processDialogsUpdateRead$20$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1105x890a6821(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processLoadedUnreadMessages(final LongSparseArray<Integer> dialogs, final ArrayList<TLRPC.Message> messages, final ArrayList<MessageObject> push, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, ArrayList<TLRPC.EncryptedChat> encryptedChats) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1109xf8b52a58(messages, dialogs, push);
            }
        });
    }

    /* renamed from: lambda$processLoadedUnreadMessages$23$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1109xf8b52a58(ArrayList messages, LongSparseArray dialogs, ArrayList push) {
        SharedPreferences preferences;
        boolean value;
        long did;
        boolean value2;
        boolean value3;
        int a;
        long did2;
        long dialog_id;
        boolean value4;
        ArrayList arrayList = messages;
        ArrayList arrayList2 = push;
        this.pushDialogs.clear();
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        boolean z = false;
        this.total_unread_count = 0;
        this.personalCount = 0;
        SharedPreferences preferences2 = getAccountInstance().getNotificationsSettings();
        LongSparseArray<Boolean> settingsCache = new LongSparseArray<>();
        long j = 0;
        int i = -1;
        int i2 = 1;
        if (arrayList != null) {
            int a2 = 0;
            while (a2 < messages.size()) {
                TLRPC.Message message = (TLRPC.Message) arrayList.get(a2);
                if (message == null) {
                    a = a2;
                } else {
                    if (message.fwd_from == null || !message.fwd_from.imported) {
                        if (message.action instanceof TLRPC.TL_messageActionSetMessagesTTL) {
                            a = a2;
                        } else {
                            if (message.silent) {
                                if (!(message.action instanceof TLRPC.TL_messageActionContactSignUp)) {
                                    if (message.action instanceof TLRPC.TL_messageActionUserJoined) {
                                        a = a2;
                                    }
                                }
                            }
                            if (message.peer_id.channel_id != j) {
                                did2 = -message.peer_id.channel_id;
                            } else {
                                did2 = 0;
                            }
                            SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(did2);
                            if (sparseArray != null && sparseArray.indexOfKey(message.id) >= 0) {
                                a = a2;
                            } else {
                                MessageObject messageObject = new MessageObject(this.currentAccount, message, z, z);
                                if (isPersonalMessage(messageObject)) {
                                    this.personalCount += i2;
                                }
                                long dialog_id2 = messageObject.getDialogId();
                                if (!messageObject.messageOwner.mentioned) {
                                    a = a2;
                                    dialog_id = dialog_id2;
                                } else {
                                    a = a2;
                                    dialog_id = messageObject.getFromChatId();
                                }
                                int index = settingsCache.indexOfKey(dialog_id);
                                if (index >= 0) {
                                    value4 = settingsCache.valueAt(index).booleanValue();
                                } else {
                                    int notifyOverride = getNotifyOverride(preferences2, dialog_id);
                                    if (notifyOverride == i) {
                                        value4 = isGlobalNotificationsEnabled(dialog_id);
                                    } else {
                                        value4 = notifyOverride != 2;
                                    }
                                    settingsCache.put(dialog_id, Boolean.valueOf(value4));
                                }
                                if (value4 && (dialog_id != this.openedDialogId || !ApplicationLoader.isScreenOn)) {
                                    if (sparseArray == null) {
                                        sparseArray = new SparseArray<>();
                                        this.pushMessagesDict.put(did2, sparseArray);
                                    }
                                    sparseArray.put(message.id, messageObject);
                                    this.pushMessages.add(0, messageObject);
                                    if (dialog_id2 != dialog_id) {
                                        Integer current = this.pushDialogsOverrideMention.get(dialog_id2);
                                        this.pushDialogsOverrideMention.put(dialog_id2, Integer.valueOf(current == null ? 1 : current.intValue() + 1));
                                    }
                                }
                            }
                        }
                    }
                    a = a2;
                }
                a2 = a + 1;
                arrayList = messages;
                z = false;
                j = 0;
                i = -1;
                i2 = 1;
            }
        }
        for (int a3 = 0; a3 < dialogs.size(); a3++) {
            long dialog_id3 = dialogs.keyAt(a3);
            int index2 = settingsCache.indexOfKey(dialog_id3);
            if (index2 >= 0) {
                value2 = settingsCache.valueAt(index2).booleanValue();
            } else {
                int notifyOverride2 = getNotifyOverride(preferences2, dialog_id3);
                if (notifyOverride2 == -1) {
                    value3 = isGlobalNotificationsEnabled(dialog_id3);
                } else {
                    value3 = notifyOverride2 != 2;
                }
                settingsCache.put(dialog_id3, Boolean.valueOf(value3));
                value2 = value3;
            }
            if (value2) {
                int count = ((Integer) dialogs.valueAt(a3)).intValue();
                this.pushDialogs.put(dialog_id3, Integer.valueOf(count));
                this.total_unread_count += count;
            }
        }
        if (arrayList2 != null) {
            int a4 = 0;
            while (a4 < push.size()) {
                MessageObject messageObject2 = (MessageObject) arrayList2.get(a4);
                int mid = messageObject2.getId();
                if (this.pushMessagesDict.indexOfKey(mid) >= 0) {
                    preferences = preferences2;
                } else {
                    if (isPersonalMessage(messageObject2)) {
                        this.personalCount++;
                    }
                    long dialogId = messageObject2.getDialogId();
                    long randomId = messageObject2.messageOwner.random_id;
                    if (messageObject2.messageOwner.mentioned) {
                        dialogId = messageObject2.getFromChatId();
                    }
                    int index3 = settingsCache.indexOfKey(dialogId);
                    if (index3 >= 0) {
                        value = settingsCache.valueAt(index3).booleanValue();
                    } else {
                        int notifyOverride3 = getNotifyOverride(preferences2, dialogId);
                        if (notifyOverride3 == -1) {
                            value = isGlobalNotificationsEnabled(dialogId);
                        } else {
                            value = notifyOverride3 != 2;
                        }
                        settingsCache.put(dialogId, Boolean.valueOf(value));
                    }
                    if (value) {
                        if (dialogId == this.openedDialogId && ApplicationLoader.isScreenOn) {
                            preferences = preferences2;
                        } else {
                            if (mid != 0) {
                                if (messageObject2.messageOwner.peer_id.channel_id != 0) {
                                    did = -messageObject2.messageOwner.peer_id.channel_id;
                                } else {
                                    did = 0;
                                }
                                SparseArray<MessageObject> sparseArray2 = this.pushMessagesDict.get(did);
                                if (sparseArray2 != null) {
                                    preferences = preferences2;
                                } else {
                                    sparseArray2 = new SparseArray<>();
                                    preferences = preferences2;
                                    this.pushMessagesDict.put(did, sparseArray2);
                                }
                                sparseArray2.put(mid, messageObject2);
                            } else {
                                preferences = preferences2;
                                if (randomId != 0) {
                                    this.fcmRandomMessagesDict.put(randomId, messageObject2);
                                }
                            }
                            this.pushMessages.add(0, messageObject2);
                            if (dialogId != dialogId) {
                                Integer current2 = this.pushDialogsOverrideMention.get(dialogId);
                                this.pushDialogsOverrideMention.put(dialogId, Integer.valueOf(current2 == null ? 1 : current2.intValue() + 1));
                            }
                            Integer currentCount = this.pushDialogs.get(dialogId);
                            int newCount = currentCount != null ? currentCount.intValue() + 1 : 1;
                            if (currentCount != null) {
                                this.total_unread_count -= currentCount.intValue();
                            }
                            this.total_unread_count += newCount;
                            this.pushDialogs.put(dialogId, Integer.valueOf(newCount));
                        }
                    } else {
                        preferences = preferences2;
                    }
                }
                a4++;
                arrayList2 = push;
                preferences2 = preferences;
            }
        }
        final int pushDialogsCount = this.pushDialogs.size();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1108xbeea8879(pushDialogsCount);
            }
        });
        showOrUpdateNotification(SystemClock.elapsedRealtime() / 1000 < 60);
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* renamed from: lambda$processLoadedUnreadMessages$22$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1108xbeea8879(int pushDialogsCount) {
        if (this.total_unread_count == 0) {
            this.popupMessages.clear();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    private int getTotalAllUnreadCount() {
        int count = 0;
        for (int a = 0; a < 4; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                NotificationsController controller = getInstance(a);
                if (controller.showBadgeNumber) {
                    if (controller.showBadgeMessages) {
                        if (controller.showBadgeMuted) {
                            try {
                                ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>(MessagesController.getInstance(a).allDialogs);
                                int N = dialogs.size();
                                for (int i = 0; i < N; i++) {
                                    TLRPC.Dialog dialog = dialogs.get(i);
                                    if (dialog != null && DialogObject.isChatDialog(dialog.id)) {
                                        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-dialog.id));
                                        if (ChatObject.isNotInChat(chat)) {
                                        }
                                    }
                                    if (dialog != null && dialog.unread_count != 0) {
                                        count += dialog.unread_count;
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else {
                            count += controller.total_unread_count;
                        }
                    } else if (controller.showBadgeMuted) {
                        try {
                            int N2 = MessagesController.getInstance(a).allDialogs.size();
                            for (int i2 = 0; i2 < N2; i2++) {
                                TLRPC.Dialog dialog2 = MessagesController.getInstance(a).allDialogs.get(i2);
                                if (DialogObject.isChatDialog(dialog2.id)) {
                                    TLRPC.Chat chat2 = getMessagesController().getChat(Long.valueOf(-dialog2.id));
                                    if (ChatObject.isNotInChat(chat2)) {
                                    }
                                }
                                if (dialog2.unread_count != 0) {
                                    count++;
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    } else {
                        count += controller.pushDialogs.size();
                    }
                }
            }
        }
        return count;
    }

    /* renamed from: lambda$updateBadge$24$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1126x8d3d4342() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1126x8d3d4342();
            }
        });
    }

    private void setBadge(int count) {
        if (this.lastBadgeCount == count) {
            return;
        }
        this.lastBadgeCount = count;
        NotificationBadge.applyCount(count);
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x00c6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r29, java.lang.String[] r30, boolean[] r31) {
        /*
            Method dump skipped, instructions count: 4842
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    private String replaceSpoilers(MessageObject messageObject) {
        String text = messageObject.messageOwner.message;
        if (text == null || messageObject == null || messageObject.messageOwner == null || messageObject.messageOwner.entities == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(text);
        for (int i = 0; i < messageObject.messageOwner.entities.size(); i++) {
            if (messageObject.messageOwner.entities.get(i) instanceof TLRPC.TL_messageEntitySpoiler) {
                TLRPC.TL_messageEntitySpoiler spoiler = (TLRPC.TL_messageEntitySpoiler) messageObject.messageOwner.entities.get(i);
                for (int j = 0; j < spoiler.length; j++) {
                    char[] cArr = this.spoilerChars;
                    stringBuilder.setCharAt(spoiler.offset + j, cArr[j % cArr.length]);
                }
            }
        }
        return stringBuilder.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:258:0x07cc, code lost:
        if (r11.getBoolean("EnablePreviewGroup", true) != false) goto L263;
     */
    /* JADX WARN: Removed duplicated region for block: B:256:0x07c1  */
    /* JADX WARN: Removed duplicated region for block: B:758:0x18dd  */
    /* JADX WARN: Removed duplicated region for block: B:760:0x18e5  */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r31, boolean r32, boolean[] r33, boolean[] r34) {
        /*
            Method dump skipped, instructions count: 6442
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getStringForMessage(org.telegram.messenger.MessageObject, boolean, boolean[], boolean[]):java.lang.String");
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent pintent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
            int minutes = preferences.getInt("repeat_messages", 60);
            if (minutes > 0 && this.personalCount > 0) {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + (minutes * 60 * 1000), pintent);
            } else {
                this.alarmManager.cancel(pintent);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        return messageObject.messageOwner.peer_id != null && messageObject.messageOwner.peer_id.chat_id == 0 && messageObject.messageOwner.peer_id.channel_id == 0 && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty));
    }

    private int getNotifyOverride(SharedPreferences preferences, long dialog_id) {
        int notifyOverride = preferences.getInt("notify2_" + dialog_id, -1);
        if (notifyOverride == 3) {
            int muteUntil = preferences.getInt("notifyuntil_" + dialog_id, 0);
            if (muteUntil >= getConnectionsManager().getCurrentTime()) {
                return 2;
            }
            return notifyOverride;
        }
        return notifyOverride;
    }

    /* renamed from: lambda$showNotifications$25$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1125x8a90ed32() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1125x8a90ed32();
            }
        });
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1100x832e582c();
            }
        });
    }

    /* renamed from: lambda$hideNotifications$26$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1100x832e582c() {
        notificationManager.cancel(this.notificationId);
        this.lastWearNotifiedMessageId.clear();
        for (int a = 0; a < this.wearNotificationsIds.size(); a++) {
            notificationManager.cancel(this.wearNotificationsIds.valueAt(a).intValue());
        }
        this.wearNotificationsIds.clear();
    }

    private void dismissNotification() {
        try {
            notificationManager.cancel(this.notificationId);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            this.lastWearNotifiedMessageId.clear();
            for (int a = 0; a < this.wearNotificationsIds.size(); a++) {
                long did = this.wearNotificationsIds.keyAt(a);
                if (!this.openedInBubbleDialogs.contains(Long.valueOf(did))) {
                    notificationManager.cancel(this.wearNotificationsIds.valueAt(a).intValue());
                }
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(NotificationsController$$ExternalSyntheticLambda31.INSTANCE);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void playInChatSound() {
        if (!this.inChatSoundEnabled || MediaController.getInstance().isRecordingAudio()) {
            return;
        }
        try {
            if (audioManager.getRingerMode() == 0) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
            int notifyOverride = getNotifyOverride(preferences, this.openedDialogId);
            if (notifyOverride == 2) {
                return;
            }
            notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.m1102xa67ee1();
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* renamed from: lambda$playInChatSound$29$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1102xa67ee1() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundPlay) <= 500) {
            return;
        }
        try {
            if (this.soundPool == null) {
                SoundPool soundPool = new SoundPool(3, 1, 0);
                this.soundPool = soundPool;
                soundPool.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda22.INSTANCE);
            }
            if (this.soundIn == 0 && !this.soundInLoaded) {
                this.soundInLoaded = true;
                this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, org.telegram.messenger.beta.R.raw.sound_in, 1);
            }
            int i = this.soundIn;
            if (i != 0) {
                try {
                    this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public static /* synthetic */ void lambda$playInChatSound$28(SoundPool soundPool, int sampleId, int status) {
        if (status == 0) {
            try {
                soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void scheduleNotificationDelay(boolean onlineReason) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay notification start, onlineReason = " + onlineReason);
            }
            this.notificationDelayWakelock.acquire(10000L);
            notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
            notificationsQueue.postRunnable(this.notificationDelayRunnable, onlineReason ? 3000 : 1000);
        } catch (Exception e) {
            FileLog.e(e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1121x309788cf();
            }
        });
    }

    /* renamed from: lambda$repeatNotificationMaybe$30$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1121x309788cf() {
        int hour = Calendar.getInstance().get(11);
        if (hour >= 11 && hour <= 22) {
            notificationManager.cancel(this.notificationId);
            showOrUpdateNotification(true);
            return;
        }
        scheduleNotificationRepeat();
    }

    private boolean isEmptyVibration(long[] pattern) {
        if (pattern == null || pattern.length == 0) {
            return false;
        }
        for (int a = 0; a < pattern.length; a++) {
            if (pattern[a] != 0) {
                return false;
            }
        }
        return true;
    }

    public void deleteNotificationChannel(long dialogId) {
        deleteNotificationChannel(dialogId, -1);
    }

    /* renamed from: deleteNotificationChannelInternal */
    public void m1096xab324d39(long dialogId, int what) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        try {
            SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor editor = preferences.edit();
            if (what == 0 || what == -1) {
                String key = "org.telegram.key" + dialogId;
                String channelId = preferences.getString(key, null);
                if (channelId != null) {
                    editor.remove(key).remove(key + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(channelId);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel internal " + channelId);
                    }
                }
            }
            if (what == 1 || what == -1) {
                String key2 = "org.telegram.keyia" + dialogId;
                String channelId2 = preferences.getString(key2, null);
                if (channelId2 != null) {
                    editor.remove(key2).remove(key2 + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(channelId2);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel internal " + channelId2);
                    }
                }
            }
            editor.commit();
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public void deleteNotificationChannel(final long dialogId, final int what) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1096xab324d39(dialogId, what);
            }
        });
    }

    public void deleteNotificationChannelGlobal(int type) {
        deleteNotificationChannelGlobal(type, -1);
    }

    /* renamed from: deleteNotificationChannelGlobalInternal */
    public void m1097xb6f20c1b(int type, int what) {
        String overwriteKey;
        String key;
        String key2;
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        try {
            SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor editor = preferences.edit();
            if (what == 0 || what == -1) {
                if (type == 2) {
                    key2 = "channels";
                } else if (type == 0) {
                    key2 = "groups";
                } else {
                    key2 = "private";
                }
                String channelId = preferences.getString(key2, null);
                if (channelId != null) {
                    SharedPreferences.Editor remove = editor.remove(key2);
                    remove.remove(key2 + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(channelId);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel global internal " + channelId);
                    }
                }
            }
            if (what == 1 || what == -1) {
                if (type == 2) {
                    key = "channels_ia";
                } else if (type == 0) {
                    key = "groups_ia";
                } else {
                    key = "private_ia";
                }
                String channelId2 = preferences.getString(key, null);
                if (channelId2 != null) {
                    SharedPreferences.Editor remove2 = editor.remove(key);
                    remove2.remove(key + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(channelId2);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel global internal " + channelId2);
                    }
                }
            }
            if (type == 2) {
                overwriteKey = "overwrite_channel";
            } else if (type == 0) {
                overwriteKey = "overwrite_group";
            } else {
                overwriteKey = "overwrite_private";
            }
            editor.remove(overwriteKey);
            editor.commit();
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public void deleteNotificationChannelGlobal(final int type, final int what) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1097xb6f20c1b(type, what);
            }
        });
    }

    public void deleteAllNotificationChannels() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1095xdfb4577b();
            }
        });
    }

    /* renamed from: lambda$deleteAllNotificationChannels$33$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1095xdfb4577b() {
        try {
            SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
            Map<String, ?> values = preferences.getAll();
            SharedPreferences.Editor editor = preferences.edit();
            for (Map.Entry<String, ?> entry : values.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("org.telegram.key")) {
                    if (!key.endsWith("_s")) {
                        String id = (String) entry.getValue();
                        systemNotificationManager.deleteNotificationChannel(id);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete all channel " + id);
                        }
                    }
                    editor.remove(key);
                }
            }
            editor.commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean unsupportedNotificationShortcut() {
        return Build.VERSION.SDK_INT < 29 || !SharedConfig.chatBubbles;
    }

    private String createNotificationShortcut(NotificationCompat.Builder builder, long did, String name, TLRPC.User user, TLRPC.Chat chat, Person person) {
        Exception e;
        String id;
        Intent shortcutIntent;
        IconCompat icon;
        if (!unsupportedNotificationShortcut()) {
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return null;
            }
            try {
                id = "ndid_" + did;
                shortcutIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
                shortcutIntent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
                if (did > 0) {
                    shortcutIntent.putExtra("userId", did);
                } else {
                    shortcutIntent.putExtra("chatId", -did);
                }
            } catch (Exception e2) {
                e = e2;
            }
            try {
                ShortcutInfoCompat.Builder shortcutBuilder = new ShortcutInfoCompat.Builder(ApplicationLoader.applicationContext, id).setShortLabel(chat != null ? name : UserObject.getFirstName(user)).setLongLabel(name).setIntent(new Intent("android.intent.action.VIEW")).setIntent(shortcutIntent).setLongLived(true).setLocusId(new LocusIdCompat(id));
                Bitmap avatar = null;
                if (person != null) {
                    shortcutBuilder.setPerson(person);
                    shortcutBuilder.setIcon(person.getIcon());
                    if (person.getIcon() != null) {
                        avatar = person.getIcon().getBitmap();
                    }
                }
                ShortcutInfoCompat shortcut = shortcutBuilder.build();
                ShortcutManagerCompat.pushDynamicShortcut(ApplicationLoader.applicationContext, shortcut);
                builder.setShortcutInfo(shortcut);
                Intent intent = new Intent(ApplicationLoader.applicationContext, BubbleActivity.class);
                intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
                if (DialogObject.isUserDialog(did)) {
                    intent.putExtra("userId", did);
                } else {
                    intent.putExtra("chatId", -did);
                }
                intent.putExtra("currentAccount", this.currentAccount);
                if (avatar != null) {
                    icon = IconCompat.createWithAdaptiveBitmap(avatar);
                } else if (user != null) {
                    icon = IconCompat.createWithResource(ApplicationLoader.applicationContext, user.bot ? org.telegram.messenger.beta.R.drawable.book_bot : org.telegram.messenger.beta.R.drawable.book_user);
                } else {
                    icon = IconCompat.createWithResource(ApplicationLoader.applicationContext, org.telegram.messenger.beta.R.drawable.book_group);
                }
                NotificationCompat.BubbleMetadata.Builder bubbleBuilder = new NotificationCompat.BubbleMetadata.Builder(PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 134217728), icon);
                bubbleBuilder.setSuppressNotification(this.openedDialogId == did);
                bubbleBuilder.setAutoExpandBubble(false);
                bubbleBuilder.setDesiredHeight(AndroidUtilities.dp(640.0f));
                builder.setBubbleMetadata(bubbleBuilder.build());
                return id;
            } catch (Exception e3) {
                e = e3;
                FileLog.e(e);
                return null;
            }
        }
        return null;
    }

    protected void ensureGroupsCreated() {
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        if (this.groupsCreated == null) {
            this.groupsCreated = Boolean.valueOf(preferences.getBoolean("groupsCreated4", false));
        }
        if (!this.groupsCreated.booleanValue()) {
            try {
                String keyStart = this.currentAccount + "channel";
                List<NotificationChannel> list = systemNotificationManager.getNotificationChannels();
                int count = list.size();
                SharedPreferences.Editor editor = null;
                for (int a = 0; a < count; a++) {
                    NotificationChannel channel = list.get(a);
                    String id = channel.getId();
                    if (id.startsWith(keyStart)) {
                        int importance = channel.getImportance();
                        if (importance != 4 && importance != 5 && !id.contains("_ia_")) {
                            if (id.contains("_channels_")) {
                                if (editor == null) {
                                    editor = getAccountInstance().getNotificationsSettings().edit();
                                }
                                editor.remove("priority_channel").remove("vibrate_channel").remove("ChannelSoundPath").remove("ChannelSound");
                            } else if (id.contains("_groups_")) {
                                if (editor == null) {
                                    editor = getAccountInstance().getNotificationsSettings().edit();
                                }
                                editor.remove("priority_group").remove("vibrate_group").remove("GroupSoundPath").remove("GroupSound");
                            } else if (id.contains("_private_")) {
                                if (editor == null) {
                                    editor = getAccountInstance().getNotificationsSettings().edit();
                                }
                                editor.remove("priority_messages");
                                editor.remove("priority_group").remove("vibrate_messages").remove("GlobalSoundPath").remove("GlobalSound");
                            } else {
                                long dialogId = Utilities.parseLong(id.substring(9, id.indexOf(95, 9))).longValue();
                                if (dialogId != 0) {
                                    if (editor == null) {
                                        editor = getAccountInstance().getNotificationsSettings().edit();
                                    }
                                    editor.remove("priority_" + dialogId).remove("vibrate_" + dialogId).remove("sound_path_" + dialogId).remove("sound_" + dialogId);
                                }
                            }
                        }
                        systemNotificationManager.deleteNotificationChannel(id);
                    }
                }
                if (editor != null) {
                    editor.commit();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            preferences.edit().putBoolean("groupsCreated4", true).commit();
            this.groupsCreated = true;
        }
        if (!this.channelGroupsCreated) {
            List<NotificationChannelGroup> list2 = systemNotificationManager.getNotificationChannelGroups();
            String channelsId = "channels" + this.currentAccount;
            String groupsId = "groups" + this.currentAccount;
            String privateId = "private" + this.currentAccount;
            String otherId = "other" + this.currentAccount;
            int N = list2.size();
            for (int a2 = 0; a2 < N; a2++) {
                String id2 = list2.get(a2).getId();
                if (channelsId != null && channelsId.equals(id2)) {
                    channelsId = null;
                } else if (groupsId != null && groupsId.equals(id2)) {
                    groupsId = null;
                } else if (privateId != null && privateId.equals(id2)) {
                    privateId = null;
                } else if (otherId != null && otherId.equals(id2)) {
                    otherId = null;
                }
                if (channelsId == null && groupsId == null && privateId == null && otherId == null) {
                    break;
                }
            }
            if (channelsId != null || groupsId != null || privateId != null || otherId != null) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
                if (user == null) {
                    getUserConfig().getCurrentUser();
                }
                String userName = user != null ? " (" + ContactsController.formatName(user.first_name, user.last_name) + ")" : "";
                ArrayList<NotificationChannelGroup> channelGroups = new ArrayList<>();
                if (channelsId != null) {
                    channelGroups.add(new NotificationChannelGroup(channelsId, LocaleController.getString("NotificationsChannels", org.telegram.messenger.beta.R.string.NotificationsChannels) + userName));
                }
                if (groupsId != null) {
                    channelGroups.add(new NotificationChannelGroup(groupsId, LocaleController.getString("NotificationsGroups", org.telegram.messenger.beta.R.string.NotificationsGroups) + userName));
                }
                if (privateId != null) {
                    channelGroups.add(new NotificationChannelGroup(privateId, LocaleController.getString("NotificationsPrivateChats", org.telegram.messenger.beta.R.string.NotificationsPrivateChats) + userName));
                }
                if (otherId != null) {
                    channelGroups.add(new NotificationChannelGroup(otherId, LocaleController.getString("NotificationsOther", org.telegram.messenger.beta.R.string.NotificationsOther) + userName));
                }
                systemNotificationManager.createNotificationChannelGroups(channelGroups);
            }
            this.channelGroupsCreated = true;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:193:0x0483 A[LOOP:1: B:191:0x047e->B:193:0x0483, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:196:0x049c  */
    /* JADX WARN: Removed duplicated region for block: B:216:0x04e9  */
    /* JADX WARN: Removed duplicated region for block: B:243:0x05d8  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x0493 A[EDGE_INSN: B:248:0x0493->B:194:0x0493 ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String validateChannelId(long r34, java.lang.String r36, long[] r37, int r38, android.net.Uri r39, int r40, boolean r41, boolean r42, boolean r43, int r44) {
        /*
            Method dump skipped, instructions count: 1518
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARN: Code restructure failed: missing block: B:84:0x0185, code lost:
        if (r5 == 0) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x0187, code lost:
        r2 = org.telegram.messenger.LocaleController.getString("NotificationHiddenChatName", org.telegram.messenger.beta.R.string.NotificationHiddenChatName);
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0191, code lost:
        r2 = org.telegram.messenger.LocaleController.getString("NotificationHiddenName", org.telegram.messenger.beta.R.string.NotificationHiddenName);
     */
    /* JADX WARN: Removed duplicated region for block: B:106:0x020f A[Catch: Exception -> 0x0dce, TRY_ENTER, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0230 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:111:0x028f A[Catch: Exception -> 0x0dce, TRY_ENTER, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:122:0x0307 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:146:0x03cb A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:154:0x03e1  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x04bc A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:180:0x04e2  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x04e4  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x04ff A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:193:0x05b4  */
    /* JADX WARN: Removed duplicated region for block: B:196:0x05ce  */
    /* JADX WARN: Removed duplicated region for block: B:207:0x0680  */
    /* JADX WARN: Removed duplicated region for block: B:217:0x06e2  */
    /* JADX WARN: Removed duplicated region for block: B:218:0x06e6  */
    /* JADX WARN: Removed duplicated region for block: B:221:0x06ee A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:224:0x06fb  */
    /* JADX WARN: Removed duplicated region for block: B:227:0x0702 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:231:0x070b A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:234:0x071a  */
    /* JADX WARN: Removed duplicated region for block: B:237:0x0720  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x072c  */
    /* JADX WARN: Removed duplicated region for block: B:243:0x0730 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:257:0x0762  */
    /* JADX WARN: Removed duplicated region for block: B:259:0x0766  */
    /* JADX WARN: Removed duplicated region for block: B:274:0x078b  */
    /* JADX WARN: Removed duplicated region for block: B:275:0x079a  */
    /* JADX WARN: Removed duplicated region for block: B:278:0x07de A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:325:0x08bc A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:333:0x0942 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:342:0x0995 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00ce A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:362:0x09f7  */
    /* JADX WARN: Removed duplicated region for block: B:388:0x0a4a  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x0a58  */
    /* JADX WARN: Removed duplicated region for block: B:394:0x0a5e A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:441:0x0b7d A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:444:0x0b89 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:445:0x0b9d  */
    /* JADX WARN: Removed duplicated region for block: B:460:0x0c10 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00fb  */
    /* JADX WARN: Removed duplicated region for block: B:485:0x0d26 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:493:0x0d4e A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:494:0x0d67 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x011b A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0126  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x014a A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x014f A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x016b A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:81:0x017d  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01b5 A[Catch: Exception -> 0x0dce, TRY_ENTER, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x01e8  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x01f4 A[Catch: Exception -> 0x0dce, TryCatch #1 {Exception -> 0x0dce, blocks: (B:10:0x0024, B:12:0x004b, B:14:0x004f, B:16:0x005c, B:18:0x0064, B:20:0x0079, B:21:0x0080, B:22:0x0086, B:26:0x0098, B:30:0x00a8, B:33:0x00b6, B:35:0x00ce, B:37:0x00de, B:39:0x00e4, B:40:0x00ea, B:42:0x00f0, B:48:0x00ff, B:50:0x011b, B:60:0x0139, B:62:0x013f, B:65:0x014a, B:66:0x014f, B:67:0x0155, B:69:0x015b, B:74:0x0163, B:76:0x016b, B:85:0x0187, B:86:0x0191, B:87:0x019b, B:89:0x01a8, B:92:0x01b5, B:94:0x01bd, B:95:0x01ca, B:97:0x01e9, B:99:0x01f4, B:103:0x0204, B:106:0x020f, B:107:0x0230, B:108:0x0272, B:111:0x028f, B:116:0x02b2, B:117:0x02c7, B:119:0x02cc, B:120:0x02e1, B:121:0x02f5, B:122:0x0307, B:124:0x032c, B:126:0x0344, B:131:0x034e, B:132:0x0355, B:136:0x0364, B:137:0x0379, B:139:0x037e, B:140:0x0393, B:141:0x03a7, B:143:0x03ad, B:144:0x03b7, B:147:0x03cd, B:157:0x03e7, B:159:0x0401, B:162:0x0439, B:164:0x0445, B:165:0x0461, B:167:0x0479, B:168:0x0485, B:170:0x0489, B:175:0x04bc, B:178:0x04d6, B:182:0x04e5, B:184:0x04ff, B:186:0x0552, B:187:0x055e, B:188:0x0577, B:190:0x0592, B:197:0x05d0, B:199:0x05e5, B:200:0x05f4, B:201:0x05fc, B:202:0x0629, B:204:0x0640, B:205:0x064f, B:206:0x0657, B:209:0x068d, B:211:0x0697, B:212:0x06a6, B:213:0x06ae, B:219:0x06e8, B:221:0x06ee, B:231:0x070b, B:233:0x0713, B:243:0x0730, B:246:0x073c, B:250:0x0749, B:270:0x0781, B:276:0x07a4, B:278:0x07de, B:282:0x07ed, B:285:0x0801, B:288:0x0812, B:290:0x0818, B:293:0x0828, B:295:0x0833, B:298:0x083b, B:300:0x0841, B:302:0x0847, B:304:0x0855, B:306:0x085d, B:311:0x0876, B:313:0x087c, B:315:0x0882, B:317:0x0890, B:319:0x0898, B:325:0x08bc, B:327:0x08cf, B:329:0x08d5, B:331:0x08e0, B:334:0x0944, B:336:0x0948, B:338:0x0950, B:340:0x096b, B:342:0x0995, B:344:0x09a2, B:367:0x0a02, B:377:0x0a1d, B:382:0x0a2e, B:385:0x0a3c, B:389:0x0a4e, B:396:0x0a62, B:400:0x0a74, B:402:0x0a7c, B:404:0x0ab2, B:406:0x0ab7, B:408:0x0abf, B:410:0x0ac5, B:412:0x0acd, B:416:0x0ada, B:417:0x0af6, B:419:0x0b05, B:420:0x0b0c, B:422:0x0b16, B:423:0x0b1f, B:425:0x0b25, B:427:0x0b2d, B:436:0x0b60, B:437:0x0b6a, B:441:0x0b7d, B:444:0x0b89, B:447:0x0ba0, B:454:0x0bc6, B:456:0x0bdf, B:457:0x0bec, B:458:0x0c09, B:460:0x0c10, B:462:0x0c14, B:464:0x0c1f, B:466:0x0c25, B:468:0x0c32, B:470:0x0c50, B:472:0x0c60, B:474:0x0c7f, B:476:0x0c8b, B:478:0x0cbf, B:479:0x0cd3, B:485:0x0d26, B:487:0x0d2c, B:489:0x0d34, B:491:0x0d3a, B:493:0x0d4e, B:494:0x0d67, B:495:0x0d7f, B:261:0x0769), top: B:503:0x0024, inners: #2 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showOrUpdateNotification(boolean r73) {
        /*
            Method dump skipped, instructions count: 3544
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    private boolean isSilentMessage(MessageObject messageObject) {
        return messageObject.messageOwner.silent || messageObject.isReactionPush;
    }

    private void setNotificationChannel(Notification mainNotification, NotificationCompat.Builder builder, boolean useSummaryNotification) {
        if (useSummaryNotification) {
            builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
        } else {
            builder.setChannelId(mainNotification.getChannelId());
        }
    }

    public void resetNotificationSound(NotificationCompat.Builder notificationBuilder, long dialogId, String chatName, long[] vibrationPattern, int ledColor, Uri sound, int importance, boolean isDefault, boolean isInApp, boolean isSilent, int chatType) {
        Uri defaultSound = Settings.System.DEFAULT_RINGTONE_URI;
        if (defaultSound == null || sound == null || TextUtils.equals(defaultSound.toString(), sound.toString())) {
            return;
        }
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        SharedPreferences.Editor editor = preferences.edit();
        String newSound = defaultSound.toString();
        String ringtoneName = LocaleController.getString("DefaultRingtone", org.telegram.messenger.beta.R.string.DefaultRingtone);
        if (isDefault) {
            if (chatType == 2) {
                editor.putString("ChannelSound", ringtoneName);
            } else if (chatType == 0) {
                editor.putString("GroupSound", ringtoneName);
            } else {
                editor.putString("GlobalSound", ringtoneName);
            }
            if (chatType == 2) {
                editor.putString("ChannelSoundPath", newSound);
            } else if (chatType == 0) {
                editor.putString("GroupSoundPath", newSound);
            } else {
                editor.putString("GlobalSoundPath", newSound);
            }
            getNotificationsController().m1097xb6f20c1b(chatType, -1);
        } else {
            editor.putString("sound_" + dialogId, ringtoneName);
            editor.putString("sound_path_" + dialogId, newSound);
            m1096xab324d39(dialogId, -1);
        }
        editor.commit();
        notificationBuilder.setChannelId(validateChannelId(dialogId, chatName, vibrationPattern, ledColor, Settings.System.DEFAULT_RINGTONE_URI, importance, isDefault, isInApp, isSilent, chatType));
        notificationManager.notify(this.notificationId, notificationBuilder.build());
    }

    /* JADX WARN: Removed duplicated region for block: B:136:0x0426  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x044b  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x0458  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x04c2  */
    /* JADX WARN: Removed duplicated region for block: B:167:0x04ca  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x04fb  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x050b A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:182:0x051c  */
    /* JADX WARN: Removed duplicated region for block: B:197:0x05cd  */
    /* JADX WARN: Removed duplicated region for block: B:198:0x05d5  */
    /* JADX WARN: Removed duplicated region for block: B:201:0x05e7  */
    /* JADX WARN: Removed duplicated region for block: B:207:0x0613 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:242:0x06b8 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:247:0x06d3  */
    /* JADX WARN: Removed duplicated region for block: B:255:0x06ea A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:265:0x0723  */
    /* JADX WARN: Removed duplicated region for block: B:362:0x097a  */
    /* JADX WARN: Removed duplicated region for block: B:415:0x0af0  */
    /* JADX WARN: Removed duplicated region for block: B:424:0x0b79  */
    /* JADX WARN: Removed duplicated region for block: B:425:0x0b83  */
    /* JADX WARN: Removed duplicated region for block: B:431:0x0bad  */
    /* JADX WARN: Removed duplicated region for block: B:432:0x0bb3  */
    /* JADX WARN: Removed duplicated region for block: B:435:0x0c0f  */
    /* JADX WARN: Removed duplicated region for block: B:439:0x0c4c  */
    /* JADX WARN: Removed duplicated region for block: B:444:0x0c75  */
    /* JADX WARN: Removed duplicated region for block: B:445:0x0c9d  */
    /* JADX WARN: Removed duplicated region for block: B:448:0x0d5c  */
    /* JADX WARN: Removed duplicated region for block: B:450:0x0d67  */
    /* JADX WARN: Removed duplicated region for block: B:451:0x0d6d  */
    /* JADX WARN: Removed duplicated region for block: B:453:0x0d71  */
    /* JADX WARN: Removed duplicated region for block: B:456:0x0d7b  */
    /* JADX WARN: Removed duplicated region for block: B:462:0x0d8f  */
    /* JADX WARN: Removed duplicated region for block: B:463:0x0d94  */
    /* JADX WARN: Removed duplicated region for block: B:465:0x0d97  */
    /* JADX WARN: Removed duplicated region for block: B:466:0x0d9f  */
    /* JADX WARN: Removed duplicated region for block: B:469:0x0dab  */
    /* JADX WARN: Removed duplicated region for block: B:489:0x0e9f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:498:0x0ece  */
    /* JADX WARN: Removed duplicated region for block: B:499:0x0ed6  */
    /* JADX WARN: Removed duplicated region for block: B:521:0x1002  */
    /* JADX WARN: Removed duplicated region for block: B:530:0x1054  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r87, java.lang.String r88, long r89, java.lang.String r91, long[] r92, int r93, android.net.Uri r94, int r95, boolean r96, boolean r97, boolean r98, int r99) {
        /*
            Method dump skipped, instructions count: 4271
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, java.lang.String, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):void");
    }

    /* renamed from: org.telegram.messenger.NotificationsController$1NotificationHolder */
    /* loaded from: classes4.dex */
    public class C1NotificationHolder {
        TLRPC.Chat chat;
        long dialogId;
        int id;
        String name;
        NotificationCompat.Builder notification;
        TLRPC.User user;
        final /* synthetic */ String val$chatName;
        final /* synthetic */ int val$chatType;
        final /* synthetic */ int val$importance;
        final /* synthetic */ boolean val$isDefault;
        final /* synthetic */ boolean val$isInApp;
        final /* synthetic */ boolean val$isSilent;
        final /* synthetic */ int val$ledColor;
        final /* synthetic */ Uri val$sound;
        final /* synthetic */ long[] val$vibrationPattern;

        C1NotificationHolder(int i, long li, String n, TLRPC.User u, TLRPC.Chat c, NotificationCompat.Builder builder, String str, long[] jArr, int i2, Uri uri, int i3, boolean z, boolean z2, boolean z3, int i4) {
            NotificationsController.this = this$0;
            this.val$chatName = str;
            this.val$vibrationPattern = jArr;
            this.val$ledColor = i2;
            this.val$sound = uri;
            this.val$importance = i3;
            this.val$isDefault = z;
            this.val$isInApp = z2;
            this.val$isSilent = z3;
            this.val$chatType = i4;
            this.id = i;
            this.name = n;
            this.user = u;
            this.chat = c;
            this.notification = builder;
            this.dialogId = li;
        }

        void call() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("show dialog notification with id " + this.id + " " + this.dialogId + " user=" + this.user + " chat=" + this.chat);
            }
            try {
                NotificationsController.notificationManager.notify(this.id, this.notification.build());
            } catch (SecurityException e) {
                FileLog.e(e);
                NotificationsController.this.resetNotificationSound(this.notification, this.dialogId, this.val$chatName, this.val$vibrationPattern, this.val$ledColor, this.val$sound, this.val$importance, this.val$isDefault, this.val$isInApp, this.val$isSilent, this.val$chatType);
            }
        }
    }

    private void loadRoundAvatar(File avatar, Person.Builder personBuilder) {
        if (avatar != null) {
            try {
                Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(avatar), NotificationsController$$ExternalSyntheticLambda0.INSTANCE);
                IconCompat icon = IconCompat.createWithBitmap(bitmap);
                personBuilder.setIcon(icon);
            } catch (Throwable th) {
            }
        }
    }

    public static /* synthetic */ int lambda$loadRoundAvatar$35(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        path.addRoundRect(0.0f, 0.0f, width, height, width / 2, width / 2, Path.Direction.CW);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawPath(path, paint);
        return -3;
    }

    public void playOutChatSound() {
        if (!this.inChatSoundEnabled || MediaController.getInstance().isRecordingAudio()) {
            return;
        }
        try {
            if (audioManager.getRingerMode() == 0) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.m1103x9f53e1fc();
            }
        });
    }

    /* renamed from: lambda$playOutChatSound$38$org-telegram-messenger-NotificationsController */
    public /* synthetic */ void m1103x9f53e1fc() {
        try {
            if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundOutPlay) <= 100) {
                return;
            }
            this.lastSoundOutPlay = SystemClock.elapsedRealtime();
            if (this.soundPool == null) {
                SoundPool soundPool = new SoundPool(3, 1, 0);
                this.soundPool = soundPool;
                soundPool.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda33.INSTANCE);
            }
            if (this.soundOut == 0 && !this.soundOutLoaded) {
                this.soundOutLoaded = true;
                this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, org.telegram.messenger.beta.R.raw.sound_out, 1);
            }
            int i = this.soundOut;
            if (i != 0) {
                try {
                    this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public static /* synthetic */ void lambda$playOutChatSound$37(SoundPool soundPool, int sampleId, int status) {
        if (status == 0) {
            try {
                soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void clearDialogNotificationsSettings(long did) {
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        SharedPreferences.Editor editor = preferences.edit();
        SharedPreferences.Editor remove = editor.remove("notify2_" + did);
        remove.remove(ContentMetadata.KEY_CUSTOM_PREFIX + did);
        getMessagesStorage().setDialogFlags(did, 0L);
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
        if (dialog != null) {
            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
        }
        editor.commit();
        getNotificationsController().updateServerNotificationsSettings(did, true);
    }

    public void setDialogNotificationsSettings(long dialog_id, int setting) {
        long flags;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        SharedPreferences.Editor editor = preferences.edit();
        TLRPC.Dialog dialog = MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(dialog_id);
        if (setting == 4) {
            boolean defaultEnabled = isGlobalNotificationsEnabled(dialog_id);
            if (defaultEnabled) {
                editor.remove("notify2_" + dialog_id);
            } else {
                editor.putInt("notify2_" + dialog_id, 0);
            }
            getMessagesStorage().setDialogFlags(dialog_id, 0L);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        } else {
            int untilTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
            if (setting == 0) {
                untilTime += 3600;
            } else if (setting == 1) {
                untilTime += 28800;
            } else if (setting == 2) {
                untilTime += 172800;
            } else if (setting == 3) {
                untilTime = Integer.MAX_VALUE;
            }
            if (setting == 3) {
                editor.putInt("notify2_" + dialog_id, 2);
                flags = 1;
            } else {
                editor.putInt("notify2_" + dialog_id, 3);
                editor.putInt("notifyuntil_" + dialog_id, untilTime);
                flags = (((long) untilTime) << 32) | 1;
            }
            getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(dialog_id);
            MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(dialog_id, flags);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                dialog.notify_settings.mute_until = untilTime;
            }
        }
        editor.commit();
        updateServerNotificationsSettings(dialog_id);
    }

    public void updateServerNotificationsSettings(long dialog_id) {
        updateServerNotificationsSettings(dialog_id, true);
    }

    public void updateServerNotificationsSettings(long dialogId, boolean post) {
        int i = 0;
        if (post) {
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
        if (DialogObject.isEncryptedDialog(dialogId)) {
            return;
        }
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        TLRPC.TL_account_updateNotifySettings req = new TLRPC.TL_account_updateNotifySettings();
        req.settings = new TLRPC.TL_inputPeerNotifySettings();
        req.settings.flags |= 1;
        req.settings.show_previews = preferences.getBoolean("content_preview_" + dialogId, true);
        TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings = req.settings;
        tL_inputPeerNotifySettings.flags = tL_inputPeerNotifySettings.flags | 2;
        req.settings.silent = preferences.getBoolean("silent_" + dialogId, false);
        int mute_type = preferences.getInt("notify2_" + dialogId, -1);
        if (mute_type != -1) {
            req.settings.flags |= 4;
            if (mute_type == 3) {
                req.settings.mute_until = preferences.getInt("notifyuntil_" + dialogId, 0);
            } else {
                TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings2 = req.settings;
                if (mute_type == 2) {
                    i = Integer.MAX_VALUE;
                }
                tL_inputPeerNotifySettings2.mute_until = i;
            }
        }
        long soundDocumentId = preferences.getLong("sound_document_id_" + dialogId, 0L);
        String soundPath = preferences.getString("sound_path_" + dialogId, null);
        TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings3 = req.settings;
        tL_inputPeerNotifySettings3.flags = tL_inputPeerNotifySettings3.flags | 8;
        if (soundDocumentId != 0) {
            TLRPC.TL_notificationSoundRingtone ringtoneSound = new TLRPC.TL_notificationSoundRingtone();
            ringtoneSound.id = soundDocumentId;
            req.settings.sound = ringtoneSound;
        } else if (soundPath != null) {
            if (soundPath.equals("NoSound")) {
                req.settings.sound = new TLRPC.TL_notificationSoundNone();
            } else {
                TLRPC.TL_notificationSoundLocal localSound = new TLRPC.TL_notificationSoundLocal();
                localSound.title = preferences.getString("sound_" + dialogId, null);
                localSound.data = soundPath;
                req.settings.sound = localSound;
            }
        } else {
            req.settings.sound = new TLRPC.TL_notificationSoundDefault();
        }
        req.peer = new TLRPC.TL_inputNotifyPeer();
        ((TLRPC.TL_inputNotifyPeer) req.peer).peer = getMessagesController().getInputPeer(dialogId);
        getConnectionsManager().sendRequest(req, NotificationsController$$ExternalSyntheticLambda32.INSTANCE);
    }

    public static /* synthetic */ void lambda$updateServerNotificationsSettings$39(TLObject response, TLRPC.TL_error error) {
    }

    public void updateServerNotificationsSettings(int type) {
        String soundPathPref;
        String soundDocumentIdPref;
        String soundNamePref;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        TLRPC.TL_account_updateNotifySettings req = new TLRPC.TL_account_updateNotifySettings();
        req.settings = new TLRPC.TL_inputPeerNotifySettings();
        req.settings.flags = 5;
        if (type == 0) {
            req.peer = new TLRPC.TL_inputNotifyChats();
            req.settings.mute_until = preferences.getInt("EnableGroup2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewGroup", true);
            soundNamePref = "GroupSound";
            soundDocumentIdPref = "GroupSoundDocId";
            soundPathPref = "GroupSoundPath";
        } else if (type == 1) {
            req.peer = new TLRPC.TL_inputNotifyUsers();
            req.settings.mute_until = preferences.getInt("EnableAll2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewAll", true);
            soundNamePref = "GlobalSound";
            soundDocumentIdPref = "GlobalSoundDocId";
            soundPathPref = "GlobalSoundPath";
        } else {
            req.peer = new TLRPC.TL_inputNotifyBroadcasts();
            req.settings.mute_until = preferences.getInt("EnableChannel2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewChannel", true);
            soundNamePref = "ChannelSound";
            soundDocumentIdPref = "ChannelSoundDocId";
            soundPathPref = "ChannelSoundPath";
        }
        req.settings.flags |= 8;
        long soundDocumentId = preferences.getLong(soundDocumentIdPref, 0L);
        String soundPath = preferences.getString(soundPathPref, "NoSound");
        if (soundDocumentId != 0) {
            TLRPC.TL_notificationSoundRingtone ringtoneSound = new TLRPC.TL_notificationSoundRingtone();
            ringtoneSound.id = soundDocumentId;
            req.settings.sound = ringtoneSound;
        } else if (soundPath != null) {
            if (soundPath.equals("NoSound")) {
                req.settings.sound = new TLRPC.TL_notificationSoundNone();
            } else {
                TLRPC.TL_notificationSoundLocal localSound = new TLRPC.TL_notificationSoundLocal();
                localSound.title = preferences.getString(soundNamePref, null);
                localSound.data = soundPath;
                req.settings.sound = localSound;
            }
        } else {
            req.settings.sound = new TLRPC.TL_notificationSoundDefault();
        }
        getConnectionsManager().sendRequest(req, NotificationsController$$ExternalSyntheticLambda34.INSTANCE);
    }

    public static /* synthetic */ void lambda$updateServerNotificationsSettings$40(TLObject response, TLRPC.TL_error error) {
    }

    public boolean isGlobalNotificationsEnabled(long dialogId) {
        return isGlobalNotificationsEnabled(dialogId, null);
    }

    public boolean isGlobalNotificationsEnabled(long dialogId, Boolean forceChannel) {
        int type;
        if (DialogObject.isChatDialog(dialogId)) {
            if (forceChannel != null) {
                if (forceChannel.booleanValue()) {
                    type = 2;
                } else {
                    type = 0;
                }
            } else {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-dialogId));
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    type = 2;
                } else {
                    type = 0;
                }
            }
        } else {
            type = 1;
        }
        return isGlobalNotificationsEnabled(type);
    }

    public boolean isGlobalNotificationsEnabled(int type) {
        return getAccountInstance().getNotificationsSettings().getInt(getGlobalNotificationsKey(type), 0) < getConnectionsManager().getCurrentTime();
    }

    public void setGlobalNotificationsEnabled(int type, int time) {
        getAccountInstance().getNotificationsSettings().edit().putInt(getGlobalNotificationsKey(type), time).commit();
        updateServerNotificationsSettings(type);
        getMessagesStorage().updateMutedDialogsFiltersCounters();
        deleteNotificationChannelGlobal(type);
    }

    public static String getGlobalNotificationsKey(int type) {
        if (type == 0) {
            return "EnableGroup2";
        }
        if (type == 1) {
            return "EnableAll2";
        }
        return "EnableChannel2";
    }

    public void muteDialog(long dialog_id, boolean mute) {
        if (mute) {
            getInstance(this.currentAccount).muteUntil(dialog_id, Integer.MAX_VALUE);
            return;
        }
        boolean defaultEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(dialog_id);
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        SharedPreferences.Editor editor = preferences.edit();
        if (defaultEnabled) {
            editor.remove("notify2_" + dialog_id);
        } else {
            editor.putInt("notify2_" + dialog_id, 0);
        }
        getMessagesStorage().setDialogFlags(dialog_id, 0L);
        editor.apply();
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialog_id);
        if (dialog != null) {
            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
        }
        updateServerNotificationsSettings(dialog_id);
    }
}
