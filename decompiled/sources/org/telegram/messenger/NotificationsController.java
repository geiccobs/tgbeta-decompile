package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.collection.LongSparseArray;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;
import com.huawei.hms.adapter.internal.AvailableCode;
import com.huawei.hms.push.constant.RemoteMessageConst;
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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC$TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC$TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC$TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC$TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp;
import org.telegram.tgnet.TLRPC$TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL;
import org.telegram.tgnet.TLRPC$TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler;
import org.telegram.tgnet.TLRPC$TL_notificationSoundDefault;
import org.telegram.tgnet.TLRPC$TL_notificationSoundLocal;
import org.telegram.tgnet.TLRPC$TL_notificationSoundNone;
import org.telegram.tgnet.TLRPC$TL_notificationSoundRingtone;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.PopupNotificationActivity;
import org.webrtc.MediaStreamTrack;
/* loaded from: classes.dex */
public class NotificationsController extends BaseController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = null;
    public static String OTHER_NOTIFICATIONS_CHANNEL = null;
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

    public static String getGlobalNotificationsKey(int i) {
        return i == 0 ? "EnableGroup2" : i == 1 ? "EnableAll2" : "EnableChannel2";
    }

    public static /* synthetic */ void lambda$updateServerNotificationsSettings$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$updateServerNotificationsSettings$40(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (Build.VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService(RemoteMessageConst.NOTIFICATION);
            checkOtherNotificationsChannel();
        }
        audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(MediaStreamTrack.AUDIO_TRACK_KIND);
        Instance = new NotificationsController[4];
        lockObjects = new Object[4];
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    public static NotificationsController getInstance(int i) {
        NotificationsController notificationsController = Instance[i];
        if (notificationsController == null) {
            synchronized (lockObjects[i]) {
                notificationsController = Instance[i];
                if (notificationsController == null) {
                    NotificationsController[] notificationsControllerArr = Instance;
                    NotificationsController notificationsController2 = new NotificationsController(i);
                    notificationsControllerArr[i] = notificationsController2;
                    notificationsController = notificationsController2;
                }
            }
        }
        return notificationsController;
    }

    public NotificationsController(int i) {
        super(i);
        StringBuilder sb = new StringBuilder();
        sb.append("messages");
        int i2 = this.currentAccount;
        sb.append(i2 == 0 ? "" : Integer.valueOf(i2));
        this.notificationGroup = sb.toString();
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        this.inChatSoundEnabled = notificationsSettings.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = notificationsSettings.getBoolean("badgeNumber", true);
        this.showBadgeMuted = notificationsSettings.getBoolean("badgeNumberMuted", false);
        this.showBadgeMessages = notificationsSettings.getBoolean("badgeNumberMessages", true);
        notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
        systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService(RemoteMessageConst.NOTIFICATION);
        try {
            audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(MediaStreamTrack.AUDIO_TRACK_KIND);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm");
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "telegram:notification_delay_lock");
            this.notificationDelayWakelock = newWakeLock;
            newWakeLock.setReferenceCounted(false);
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        this.notificationDelayRunnable = new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$new$0();
            }
        };
    }

    public /* synthetic */ void lambda$new$0() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("delay reached");
        }
        if (!this.delayedPushMessages.isEmpty()) {
            showOrUpdateNotification(true);
            this.delayedPushMessages.clear();
        }
        try {
            if (!this.notificationDelayWakelock.isHeld()) {
                return;
            }
            this.notificationDelayWakelock.release();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void checkOtherNotificationsChannel() {
        SharedPreferences sharedPreferences;
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        if (OTHER_NOTIFICATIONS_CHANNEL == null) {
            sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            OTHER_NOTIFICATIONS_CHANNEL = sharedPreferences.getString("OtherKey", "Other3");
        } else {
            sharedPreferences = null;
        }
        NotificationChannel notificationChannel = systemNotificationManager.getNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
        if (notificationChannel != null && notificationChannel.getImportance() == 0) {
            systemNotificationManager.deleteNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
            OTHER_NOTIFICATIONS_CHANNEL = null;
            notificationChannel = null;
        }
        if (OTHER_NOTIFICATIONS_CHANNEL == null) {
            if (sharedPreferences == null) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            }
            OTHER_NOTIFICATIONS_CHANNEL = "Other" + Utilities.random.nextLong();
            sharedPreferences.edit().putString("OtherKey", OTHER_NOTIFICATIONS_CHANNEL).commit();
        }
        if (notificationChannel != null) {
            return;
        }
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

    public void muteUntil(long j, int i) {
        long j2 = 0;
        if (j != 0) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            boolean isGlobalNotificationsEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(j);
            if (i != Integer.MAX_VALUE) {
                edit.putInt("notify2_" + j, 3);
                edit.putInt("notifyuntil_" + j, getConnectionsManager().getCurrentTime() + i);
                j2 = (((long) i) << 32) | 1;
            } else if (!isGlobalNotificationsEnabled) {
                edit.remove("notify2_" + j);
            } else {
                edit.putInt("notify2_" + j, 2);
                j2 = 1L;
            }
            getInstance(this.currentAccount).removeNotificationsForDialog(j);
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j, j2);
            edit.commit();
            TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j);
            if (tLRPC$Dialog != null) {
                TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                tLRPC$Dialog.notify_settings = tLRPC$TL_peerNotifySettings;
                if (i != Integer.MAX_VALUE || isGlobalNotificationsEnabled) {
                    tLRPC$TL_peerNotifySettings.mute_until = i;
                }
            }
            getInstance(this.currentAccount).updateServerNotificationsSettings(j);
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        this.channelGroupsCreated = false;
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$cleanup$1();
            }
        });
    }

    public /* synthetic */ void lambda$cleanup$1() {
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
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        edit.clear();
        edit.commit();
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                systemNotificationManager.deleteNotificationChannelGroup("channels" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("groups" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("private" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("other" + this.currentAccount);
                String str = this.currentAccount + "channel";
                List<NotificationChannel> notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                for (int i = 0; i < size; i++) {
                    String id = notificationChannels.get(i).getId();
                    if (id.startsWith(str)) {
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
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    public void setInChatSoundEnabled(boolean z) {
        this.inChatSoundEnabled = z;
    }

    public /* synthetic */ void lambda$setOpenedDialogId$2(long j) {
        this.openedDialogId = j;
    }

    public void setOpenedDialogId(final long j) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$setOpenedDialogId$2(j);
            }
        });
    }

    public void setOpenedInBubble(final long j, final boolean z) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$setOpenedInBubble$3(z, j);
            }
        });
    }

    public /* synthetic */ void lambda$setOpenedInBubble$3(boolean z, long j) {
        if (z) {
            this.openedInBubbleDialogs.add(Long.valueOf(j));
        } else {
            this.openedInBubbleDialogs.remove(Long.valueOf(j));
        }
    }

    public void setLastOnlineFromOtherDevice(final int i) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$setLastOnlineFromOtherDevice$4(i);
            }
        });
    }

    public /* synthetic */ void lambda$setLastOnlineFromOtherDevice$4(int i) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + i);
        }
        this.lastOnlineFromOtherDevice = i;
    }

    public void removeNotificationsForDialog(long j) {
        processReadMessages(null, j, 0, Integer.MAX_VALUE, false);
        LongSparseIntArray longSparseIntArray = new LongSparseIntArray();
        longSparseIntArray.put(j, 0);
        processDialogsUpdateRead(longSparseIntArray);
    }

    public boolean hasMessagesToReply() {
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialogId) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                return true;
            }
        }
        return false;
    }

    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$6();
            }
        });
    }

    public /* synthetic */ void lambda$forceShowPopupForReply$6() {
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialogId) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                arrayList.add(0, messageObject);
            }
        }
        if (arrayList.isEmpty() || AndroidUtilities.needShowPasscode() || SharedConfig.isWaitingForPasscodeEnter) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$5(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$forceShowPopupForReply$5(ArrayList arrayList) {
        this.popupReplyMessages = arrayList;
        Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
        intent.putExtra("force", true);
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setFlags(268763140);
        ApplicationLoader.applicationContext.startActivity(intent);
        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void removeDeletedMessagesFromNotifications(final LongSparseArray<ArrayList<Integer>> longSparseArray) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$9(longSparseArray, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$9(LongSparseArray longSparseArray, final ArrayList arrayList) {
        Integer num;
        ArrayList arrayList2;
        Integer num2;
        LongSparseArray longSparseArray2 = longSparseArray;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        Integer num3 = 0;
        int i2 = 0;
        while (i2 < longSparseArray.size()) {
            long keyAt = longSparseArray2.keyAt(i2);
            SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(keyAt);
            if (sparseArray == null) {
                num = num3;
            } else {
                ArrayList arrayList3 = (ArrayList) longSparseArray2.get(keyAt);
                int size = arrayList3.size();
                int i3 = 0;
                while (i3 < size) {
                    int intValue = ((Integer) arrayList3.get(i3)).intValue();
                    MessageObject messageObject = sparseArray.get(intValue);
                    Integer num4 = num3;
                    if (messageObject != null) {
                        long dialogId = messageObject.getDialogId();
                        Integer num5 = this.pushDialogs.get(dialogId);
                        if (num5 == null) {
                            num5 = num4;
                        }
                        Integer valueOf = Integer.valueOf(num5.intValue() - 1);
                        if (valueOf.intValue() <= 0) {
                            this.smartNotificationsDialogs.remove(dialogId);
                            num2 = num4;
                        } else {
                            num2 = valueOf;
                        }
                        if (!num2.equals(num5)) {
                            arrayList2 = arrayList3;
                            int intValue2 = this.total_unread_count - num5.intValue();
                            this.total_unread_count = intValue2;
                            this.total_unread_count = intValue2 + num2.intValue();
                            this.pushDialogs.put(dialogId, num2);
                        } else {
                            arrayList2 = arrayList3;
                        }
                        if (num2.intValue() == 0) {
                            this.pushDialogs.remove(dialogId);
                            this.pushDialogsOverrideMention.remove(dialogId);
                        }
                        sparseArray.remove(intValue);
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList.add(messageObject);
                    } else {
                        arrayList2 = arrayList3;
                    }
                    i3++;
                    num3 = num4;
                    arrayList3 = arrayList2;
                }
                num = num3;
                if (sparseArray.size() == 0) {
                    this.pushMessagesDict.remove(keyAt);
                }
            }
            i2++;
            longSparseArray2 = longSparseArray;
            num3 = num;
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$7(arrayList);
                }
            });
        }
        if (i != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            }
            final int size2 = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$8(size2);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$7(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$8(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void removeDeletedHisoryFromNotifications(final LongSparseIntArray longSparseIntArray) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$12(longSparseIntArray, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$12(LongSparseIntArray longSparseIntArray, final ArrayList arrayList) {
        boolean z;
        Integer num;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        Integer num2 = 0;
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= longSparseIntArray.size()) {
                break;
            }
            long keyAt = longSparseIntArray.keyAt(i2);
            long j = -keyAt;
            long j2 = longSparseIntArray.get(keyAt);
            Integer num3 = this.pushDialogs.get(j);
            if (num3 == null) {
                num3 = num2;
            }
            Integer num4 = num3;
            int i3 = 0;
            while (i3 < this.pushMessages.size()) {
                MessageObject messageObject = this.pushMessages.get(i3);
                if (messageObject.getDialogId() == j) {
                    num = num2;
                    if (messageObject.getId() <= j2) {
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(j);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(j);
                            }
                        }
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        i3--;
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList.add(messageObject);
                        num4 = Integer.valueOf(num4.intValue() - 1);
                    }
                } else {
                    num = num2;
                }
                i3++;
                num2 = num;
            }
            Integer num5 = num2;
            if (num4.intValue() <= 0) {
                this.smartNotificationsDialogs.remove(j);
                num4 = num5;
            }
            if (!num4.equals(num3)) {
                int intValue = this.total_unread_count - num3.intValue();
                this.total_unread_count = intValue;
                this.total_unread_count = intValue + num4.intValue();
                this.pushDialogs.put(j, num4);
            }
            if (num4.intValue() == 0) {
                this.pushDialogs.remove(j);
                this.pushDialogsOverrideMention.remove(j);
            }
            i2++;
            num2 = num5;
        }
        if (arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$10(arrayList);
                }
            });
        }
        if (i != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            final int size = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$11(size);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$10(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$11(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processReadMessages(final LongSparseIntArray longSparseIntArray, final long j, final int i, final int i2, final boolean z) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processReadMessages$14(longSparseIntArray, arrayList, j, i2, i, z);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:52:0x00d7, code lost:
        r8 = false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processReadMessages$14(org.telegram.messenger.support.LongSparseIntArray r19, final java.util.ArrayList r20, long r21, int r23, int r24, boolean r25) {
        /*
            Method dump skipped, instructions count: 304
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processReadMessages$14(org.telegram.messenger.support.LongSparseIntArray, java.util.ArrayList, long, int, int, boolean):void");
    }

    public /* synthetic */ void lambda$processReadMessages$13(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0056, code lost:
        if (r0 == 2) goto L21;
     */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0070  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int addToPopupMessages(java.util.ArrayList<org.telegram.messenger.MessageObject> r4, org.telegram.messenger.MessageObject r5, long r6, boolean r8, android.content.SharedPreferences r9) {
        /*
            r3 = this;
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            r1 = 0
            if (r0 != 0) goto L58
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "custom_"
            r0.append(r2)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            boolean r0 = r9.getBoolean(r0, r1)
            if (r0 == 0) goto L34
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "popup_"
            r0.append(r2)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            int r0 = r9.getInt(r0, r1)
            goto L35
        L34:
            r0 = 0
        L35:
            if (r0 != 0) goto L50
            if (r8 == 0) goto L40
            java.lang.String r6 = "popupChannel"
            int r0 = r9.getInt(r6, r1)
            goto L59
        L40:
            boolean r6 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r6 == 0) goto L49
            java.lang.String r6 = "popupGroup"
            goto L4b
        L49:
            java.lang.String r6 = "popupAll"
        L4b:
            int r0 = r9.getInt(r6, r1)
            goto L59
        L50:
            r6 = 1
            if (r0 != r6) goto L55
            r0 = 3
            goto L59
        L55:
            r6 = 2
            if (r0 != r6) goto L59
        L58:
            r0 = 0
        L59:
            if (r0 == 0) goto L6e
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r6 = r6.channel_id
            r8 = 0
            int r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r2 == 0) goto L6e
            boolean r6 = r5.isSupergroup()
            if (r6 != 0) goto L6e
            r0 = 0
        L6e:
            if (r0 == 0) goto L73
            r4.add(r1, r5)
        L73:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.addToPopupMessages(java.util.ArrayList, org.telegram.messenger.MessageObject, long, boolean, android.content.SharedPreferences):int");
    }

    public void processEditedMessages(final LongSparseArray<ArrayList<MessageObject>> longSparseArray) {
        if (longSparseArray.size() == 0) {
            return;
        }
        new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processEditedMessages$15(longSparseArray);
            }
        });
    }

    public /* synthetic */ void lambda$processEditedMessages$15(LongSparseArray longSparseArray) {
        int size = longSparseArray.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            if (this.pushDialogs.indexOfKey(longSparseArray.keyAt(i)) >= 0) {
                ArrayList arrayList = (ArrayList) longSparseArray.valueAt(i);
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    MessageObject messageObject = (MessageObject) arrayList.get(i2);
                    long j = messageObject.messageOwner.peer_id.channel_id;
                    long j2 = 0;
                    if (j != 0) {
                        j2 = -j;
                    }
                    SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(j2);
                    if (sparseArray == null) {
                        break;
                    }
                    MessageObject messageObject2 = sparseArray.get(messageObject.getId());
                    if (messageObject2 != null && messageObject2.isReactionPush) {
                        messageObject2 = null;
                    }
                    if (messageObject2 != null) {
                        sparseArray.put(messageObject.getId(), messageObject);
                        int indexOf = this.pushMessages.indexOf(messageObject2);
                        if (indexOf >= 0) {
                            this.pushMessages.set(indexOf, messageObject);
                        }
                        int indexOf2 = this.delayedPushMessages.indexOf(messageObject2);
                        if (indexOf2 >= 0) {
                            this.delayedPushMessages.set(indexOf2, messageObject);
                        }
                        z = true;
                    }
                }
            }
        }
        if (z) {
            showOrUpdateNotification(false);
        }
    }

    public void processNewMessages(final ArrayList<MessageObject> arrayList, final boolean z, final boolean z2, final CountDownLatch countDownLatch) {
        if (!arrayList.isEmpty()) {
            final ArrayList arrayList2 = new ArrayList(0);
            notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$processNewMessages$18(arrayList, arrayList2, z2, z, countDownLatch);
                }
            });
        } else if (countDownLatch == null) {
        } else {
            countDownLatch.countDown();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0048, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L18;
     */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00f6  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x013d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processNewMessages$18(java.util.ArrayList r30, final java.util.ArrayList r31, boolean r32, boolean r33, java.util.concurrent.CountDownLatch r34) {
        /*
            Method dump skipped, instructions count: 812
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processNewMessages$18(java.util.ArrayList, java.util.ArrayList, boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    public /* synthetic */ void lambda$processNewMessages$16(ArrayList arrayList, int i) {
        this.popupMessages.addAll(0, arrayList);
        if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
            if (i != 3 && ((i != 1 || !ApplicationLoader.isScreenOn) && (i != 2 || ApplicationLoader.isScreenOn))) {
                return;
            }
            Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
            intent.setFlags(268763140);
            try {
                ApplicationLoader.applicationContext.startActivity(intent);
            } catch (Throwable unused) {
            }
        }
    }

    public /* synthetic */ void lambda$processNewMessages$17(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(final LongSparseIntArray longSparseIntArray) {
        final ArrayList arrayList = new ArrayList();
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processDialogsUpdateRead$21(longSparseIntArray, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$21(LongSparseIntArray longSparseIntArray, final ArrayList arrayList) {
        boolean z;
        boolean z2;
        Integer num;
        TLRPC$Chat chat;
        int i = this.total_unread_count;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= longSparseIntArray.size()) {
                break;
            }
            long keyAt = longSparseIntArray.keyAt(i2);
            Integer num2 = this.pushDialogs.get(keyAt);
            int i3 = longSparseIntArray.get(keyAt);
            if (DialogObject.isChatDialog(keyAt) && ((chat = getMessagesController().getChat(Long.valueOf(-keyAt))) == null || chat.min || ChatObject.isNotInChat(chat))) {
                i3 = 0;
            }
            int notifyOverride = getNotifyOverride(notificationsSettings, keyAt);
            if (notifyOverride == -1) {
                z2 = isGlobalNotificationsEnabled(keyAt);
            } else {
                z2 = notifyOverride != 2;
            }
            if (this.notifyCheck && !z2 && (num = this.pushDialogsOverrideMention.get(keyAt)) != null && num.intValue() != 0) {
                i3 = num.intValue();
                z2 = true;
            }
            if (i3 == 0) {
                this.smartNotificationsDialogs.remove(keyAt);
            }
            if (i3 < 0) {
                if (num2 == null) {
                    i2++;
                } else {
                    i3 += num2.intValue();
                }
            }
            if ((z2 || i3 == 0) && num2 != null) {
                this.total_unread_count -= num2.intValue();
            }
            if (i3 == 0) {
                this.pushDialogs.remove(keyAt);
                this.pushDialogsOverrideMention.remove(keyAt);
                int i4 = 0;
                while (i4 < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(i4);
                    if (!messageObject.messageOwner.from_scheduled && messageObject.getDialogId() == keyAt) {
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        this.pushMessages.remove(i4);
                        i4--;
                        this.delayedPushMessages.remove(messageObject);
                        long j = messageObject.messageOwner.peer_id.channel_id;
                        long j2 = 0;
                        if (j != 0) {
                            j2 = -j;
                        }
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(j2);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(j2);
                            }
                        }
                        arrayList.add(messageObject);
                    }
                    i4++;
                }
            } else if (z2) {
                this.total_unread_count += i3;
                this.pushDialogs.put(keyAt, Integer.valueOf(i3));
            }
            i2++;
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$processDialogsUpdateRead$19(arrayList);
                }
            });
        }
        if (i != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            final int size = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$processDialogsUpdateRead$20(size);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$19(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$20(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processLoadedUnreadMessages(final LongSparseArray<Integer> longSparseArray, final ArrayList<TLRPC$Message> arrayList, final ArrayList<MessageObject> arrayList2, ArrayList<TLRPC$User> arrayList3, ArrayList<TLRPC$Chat> arrayList4, ArrayList<TLRPC$EncryptedChat> arrayList5) {
        getMessagesController().putUsers(arrayList3, true);
        getMessagesController().putChats(arrayList4, true);
        getMessagesController().putEncryptedChats(arrayList5, true);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$23(arrayList, longSparseArray, arrayList2);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedUnreadMessages$23(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2) {
        LongSparseArray longSparseArray2;
        SharedPreferences sharedPreferences;
        boolean z;
        boolean z2;
        int i;
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader;
        long j;
        SparseArray<MessageObject> sparseArray;
        boolean z3;
        SparseArray<MessageObject> sparseArray2;
        boolean z4;
        ArrayList arrayList3 = arrayList;
        this.pushDialogs.clear();
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        boolean z5 = false;
        this.total_unread_count = 0;
        this.personalCount = 0;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        LongSparseArray longSparseArray3 = new LongSparseArray();
        long j2 = 0;
        int i2 = 1;
        if (arrayList3 != null) {
            int i3 = 0;
            while (i3 < arrayList.size()) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList3.get(i3);
                if (tLRPC$Message != null && ((tLRPC$MessageFwdHeader = tLRPC$Message.fwd_from) == null || !tLRPC$MessageFwdHeader.imported)) {
                    TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
                    if (!(tLRPC$MessageAction instanceof TLRPC$TL_messageActionSetMessagesTTL) && (!tLRPC$Message.silent || (!(tLRPC$MessageAction instanceof TLRPC$TL_messageActionContactSignUp) && !(tLRPC$MessageAction instanceof TLRPC$TL_messageActionUserJoined)))) {
                        long j3 = tLRPC$Message.peer_id.channel_id;
                        long j4 = j3 != j2 ? -j3 : j2;
                        SparseArray<MessageObject> sparseArray3 = this.pushMessagesDict.get(j4);
                        if (sparseArray3 == null || sparseArray3.indexOfKey(tLRPC$Message.id) < 0) {
                            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message, z5, z5);
                            if (isPersonalMessage(messageObject)) {
                                this.personalCount += i2;
                            }
                            i = i3;
                            long dialogId = messageObject.getDialogId();
                            if (messageObject.messageOwner.mentioned) {
                                sparseArray = sparseArray3;
                                j = messageObject.getFromChatId();
                            } else {
                                sparseArray = sparseArray3;
                                j = dialogId;
                            }
                            int indexOfKey = longSparseArray3.indexOfKey(j);
                            if (indexOfKey >= 0) {
                                z3 = ((Boolean) longSparseArray3.valueAt(indexOfKey)).booleanValue();
                            } else {
                                int notifyOverride = getNotifyOverride(notificationsSettings, j);
                                if (notifyOverride == -1) {
                                    z4 = isGlobalNotificationsEnabled(j);
                                } else {
                                    z4 = notifyOverride != 2;
                                }
                                z3 = z4;
                                longSparseArray3.put(j, Boolean.valueOf(z3));
                            }
                            if (z3 && (j != this.openedDialogId || !ApplicationLoader.isScreenOn)) {
                                if (sparseArray == null) {
                                    sparseArray2 = new SparseArray<>();
                                    this.pushMessagesDict.put(j4, sparseArray2);
                                } else {
                                    sparseArray2 = sparseArray;
                                }
                                sparseArray2.put(tLRPC$Message.id, messageObject);
                                this.pushMessages.add(0, messageObject);
                                if (dialogId != j) {
                                    Integer num = this.pushDialogsOverrideMention.get(dialogId);
                                    this.pushDialogsOverrideMention.put(dialogId, Integer.valueOf(num == null ? 1 : num.intValue() + 1));
                                }
                            }
                            i3 = i + 1;
                            arrayList3 = arrayList;
                            z5 = false;
                            j2 = 0;
                            i2 = 1;
                        }
                    }
                }
                i = i3;
                i3 = i + 1;
                arrayList3 = arrayList;
                z5 = false;
                j2 = 0;
                i2 = 1;
            }
        }
        for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
            long keyAt = longSparseArray.keyAt(i4);
            int indexOfKey2 = longSparseArray3.indexOfKey(keyAt);
            if (indexOfKey2 >= 0) {
                z2 = ((Boolean) longSparseArray3.valueAt(indexOfKey2)).booleanValue();
            } else {
                int notifyOverride2 = getNotifyOverride(notificationsSettings, keyAt);
                if (notifyOverride2 == -1) {
                    z2 = isGlobalNotificationsEnabled(keyAt);
                } else {
                    z2 = notifyOverride2 != 2;
                }
                longSparseArray3.put(keyAt, Boolean.valueOf(z2));
            }
            if (z2) {
                int intValue = ((Integer) longSparseArray.valueAt(i4)).intValue();
                this.pushDialogs.put(keyAt, Integer.valueOf(intValue));
                this.total_unread_count += intValue;
            }
        }
        if (arrayList2 != null) {
            int i5 = 0;
            while (i5 < arrayList2.size()) {
                MessageObject messageObject2 = (MessageObject) arrayList2.get(i5);
                int id = messageObject2.getId();
                if (this.pushMessagesDict.indexOfKey(id) >= 0) {
                    sharedPreferences = notificationsSettings;
                    longSparseArray2 = longSparseArray3;
                } else {
                    if (isPersonalMessage(messageObject2)) {
                        this.personalCount++;
                    }
                    long dialogId2 = messageObject2.getDialogId();
                    TLRPC$Message tLRPC$Message2 = messageObject2.messageOwner;
                    long j5 = tLRPC$Message2.random_id;
                    long fromChatId = tLRPC$Message2.mentioned ? messageObject2.getFromChatId() : dialogId2;
                    int indexOfKey3 = longSparseArray3.indexOfKey(fromChatId);
                    if (indexOfKey3 >= 0) {
                        z = ((Boolean) longSparseArray3.valueAt(indexOfKey3)).booleanValue();
                    } else {
                        int notifyOverride3 = getNotifyOverride(notificationsSettings, fromChatId);
                        if (notifyOverride3 == -1) {
                            z = isGlobalNotificationsEnabled(fromChatId);
                        } else {
                            z = notifyOverride3 != 2;
                        }
                        longSparseArray3.put(fromChatId, Boolean.valueOf(z));
                    }
                    sharedPreferences = notificationsSettings;
                    if (z) {
                        longSparseArray2 = longSparseArray3;
                        if (fromChatId != this.openedDialogId || !ApplicationLoader.isScreenOn) {
                            if (id != 0) {
                                long j6 = messageObject2.messageOwner.peer_id.channel_id;
                                long j7 = j6 != 0 ? -j6 : 0L;
                                SparseArray<MessageObject> sparseArray4 = this.pushMessagesDict.get(j7);
                                if (sparseArray4 == null) {
                                    sparseArray4 = new SparseArray<>();
                                    this.pushMessagesDict.put(j7, sparseArray4);
                                }
                                sparseArray4.put(id, messageObject2);
                            } else if (j5 != 0) {
                                this.fcmRandomMessagesDict.put(j5, messageObject2);
                            }
                            this.pushMessages.add(0, messageObject2);
                            if (dialogId2 != fromChatId) {
                                Integer num2 = this.pushDialogsOverrideMention.get(dialogId2);
                                this.pushDialogsOverrideMention.put(dialogId2, Integer.valueOf(num2 == null ? 1 : num2.intValue() + 1));
                            }
                            Integer num3 = this.pushDialogs.get(fromChatId);
                            int intValue2 = num3 != null ? num3.intValue() + 1 : 1;
                            if (num3 != null) {
                                this.total_unread_count -= num3.intValue();
                            }
                            this.total_unread_count += intValue2;
                            this.pushDialogs.put(fromChatId, Integer.valueOf(intValue2));
                            i5++;
                            notificationsSettings = sharedPreferences;
                            longSparseArray3 = longSparseArray2;
                        }
                    } else {
                        longSparseArray2 = longSparseArray3;
                    }
                }
                i5++;
                notificationsSettings = sharedPreferences;
                longSparseArray3 = longSparseArray2;
            }
        }
        final int size = this.pushDialogs.size();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$22(size);
            }
        });
        showOrUpdateNotification(SystemClock.elapsedRealtime() / 1000 < 60);
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$processLoadedUnreadMessages$22(int i) {
        if (this.total_unread_count == 0) {
            this.popupMessages.clear();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    private int getTotalAllUnreadCount() {
        int i;
        int i2;
        int i3 = 0;
        for (int i4 = 0; i4 < 4; i4++) {
            if (UserConfig.getInstance(i4).isClientActivated()) {
                NotificationsController notificationsController = getInstance(i4);
                if (notificationsController.showBadgeNumber) {
                    if (notificationsController.showBadgeMessages) {
                        if (notificationsController.showBadgeMuted) {
                            try {
                                ArrayList arrayList = new ArrayList(MessagesController.getInstance(i4).allDialogs);
                                int size = arrayList.size();
                                for (int i5 = 0; i5 < size; i5++) {
                                    TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) arrayList.get(i5);
                                    if ((tLRPC$Dialog == null || !DialogObject.isChatDialog(tLRPC$Dialog.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog.id)))) && tLRPC$Dialog != null && (i2 = tLRPC$Dialog.unread_count) != 0) {
                                        i3 += i2;
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else {
                            i = notificationsController.total_unread_count;
                        }
                    } else if (notificationsController.showBadgeMuted) {
                        try {
                            int size2 = MessagesController.getInstance(i4).allDialogs.size();
                            for (int i6 = 0; i6 < size2; i6++) {
                                TLRPC$Dialog tLRPC$Dialog2 = MessagesController.getInstance(i4).allDialogs.get(i6);
                                if ((!DialogObject.isChatDialog(tLRPC$Dialog2.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog2.id)))) && tLRPC$Dialog2.unread_count != 0) {
                                    i3++;
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    } else {
                        i = notificationsController.pushDialogs.size();
                    }
                    i3 += i;
                }
            }
        }
        return i3;
    }

    public /* synthetic */ void lambda$updateBadge$24() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$updateBadge$24();
            }
        });
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount == i) {
            return;
        }
        this.lastBadgeCount = i;
        NotificationBadge.applyCount(i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:147:0x0224, code lost:
        if (r11.getBoolean("EnablePreviewAll", true) == false) goto L149;
     */
    /* JADX WARN: Code restructure failed: missing block: B:153:0x0234, code lost:
        if (r11.getBoolean("EnablePreviewGroup", r10) != false) goto L157;
     */
    /* JADX WARN: Code restructure failed: missing block: B:156:0x023e, code lost:
        if (r11.getBoolean("EnablePreviewChannel", r10) != false) goto L157;
     */
    /* JADX WARN: Code restructure failed: missing block: B:157:0x0240, code lost:
        r4 = r23.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x0250, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L676;
     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x0252, code lost:
        r24[0] = null;
        r5 = r4.action;
     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x025a, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) == false) goto L163;
     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x0262, code lost:
        return r23.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:164:0x0265, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L674;
     */
    /* JADX WARN: Code restructure failed: missing block: B:166:0x0269, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L167;
     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x026f, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L171;
     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x0280, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", org.telegram.messenger.beta.R.string.NotificationContactNewPhoto, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x0284, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L175;
     */
    /* JADX WARN: Code restructure failed: missing block: B:173:0x0286, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", org.telegram.messenger.beta.R.string.formatDateAtTime, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(r23.messageOwner.date * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(r23.messageOwner.date * 1000));
        r0 = r23.messageOwner.action;
     */
    /* JADX WARN: Code restructure failed: missing block: B:174:0x02e4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", org.telegram.messenger.beta.R.string.NotificationUnrecognizedDevice, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARN: Code restructure failed: missing block: B:176:0x02e7, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L672;
     */
    /* JADX WARN: Code restructure failed: missing block: B:178:0x02eb, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L179;
     */
    /* JADX WARN: Code restructure failed: missing block: B:180:0x02f1, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L187;
     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x02f5, code lost:
        if (r5.video == false) goto L185;
     */
    /* JADX WARN: Code restructure failed: missing block: B:184:0x0300, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", org.telegram.messenger.beta.R.string.CallMessageVideoIncomingMissed);
     */
    /* JADX WARN: Code restructure failed: missing block: B:186:0x030a, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", org.telegram.messenger.beta.R.string.CallMessageIncomingMissed);
     */
    /* JADX WARN: Code restructure failed: missing block: B:188:0x030d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L231;
     */
    /* JADX WARN: Code restructure failed: missing block: B:189:0x030f, code lost:
        r2 = r5.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:190:0x0315, code lost:
        if (r2 != 0) goto L194;
     */
    /* JADX WARN: Code restructure failed: missing block: B:192:0x031e, code lost:
        if (r5.users.size() != 1) goto L194;
     */
    /* JADX WARN: Code restructure failed: missing block: B:193:0x0320, code lost:
        r2 = r23.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:195:0x0335, code lost:
        if (r2 == 0) goto L219;
     */
    /* JADX WARN: Code restructure failed: missing block: B:197:0x033f, code lost:
        if (r23.messageOwner.peer_id.channel_id == 0) goto L202;
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x0343, code lost:
        if (r6.megagroup != false) goto L202;
     */
    /* JADX WARN: Code restructure failed: missing block: B:201:0x0359, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", org.telegram.messenger.beta.R.string.ChannelAddedByNotification, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:203:0x035c, code lost:
        if (r2 != r17) goto L206;
     */
    /* JADX WARN: Code restructure failed: missing block: B:205:0x0372, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", org.telegram.messenger.beta.R.string.NotificationInvitedToGroup, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:206:0x0373, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r2));
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x037f, code lost:
        if (r0 != null) goto L209;
     */
    /* JADX WARN: Code restructure failed: missing block: B:208:0x0381, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:210:0x0387, code lost:
        if (r8 != r0.id) goto L217;
     */
    /* JADX WARN: Code restructure failed: missing block: B:212:0x038b, code lost:
        if (r6.megagroup == false) goto L215;
     */
    /* JADX WARN: Code restructure failed: missing block: B:214:0x03a1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", org.telegram.messenger.beta.R.string.NotificationGroupAddSelfMega, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:216:0x03b6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", org.telegram.messenger.beta.R.string.NotificationGroupAddSelf, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:218:0x03d1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.beta.R.string.NotificationGroupAddMember, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:219:0x03d2, code lost:
        r2 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x03e2, code lost:
        if (r3 >= r23.messageOwner.action.users.size()) goto L793;
     */
    /* JADX WARN: Code restructure failed: missing block: B:222:0x03e4, code lost:
        r4 = getMessagesController().getUser(r23.messageOwner.action.users.get(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x03f8, code lost:
        if (r4 == null) goto L795;
     */
    /* JADX WARN: Code restructure failed: missing block: B:224:0x03fa, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x0402, code lost:
        if (r2.length() == 0) goto L227;
     */
    /* JADX WARN: Code restructure failed: missing block: B:226:0x0404, code lost:
        r2.append(", ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:227:0x0409, code lost:
        r2.append(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:228:0x040c, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:230:0x0429, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.beta.R.string.NotificationGroupAddMember, r1, r6.title, r2.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:232:0x042d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L235;
     */
    /* JADX WARN: Code restructure failed: missing block: B:234:0x0442, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", org.telegram.messenger.beta.R.string.NotificationGroupCreatedCall, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:236:0x0445, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L239;
     */
    /* JADX WARN: Code restructure failed: missing block: B:238:0x044d, code lost:
        return r23.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:240:0x0450, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L269;
     */
    /* JADX WARN: Code restructure failed: missing block: B:241:0x0452, code lost:
        r2 = r5.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:242:0x0458, code lost:
        if (r2 != 0) goto L246;
     */
    /* JADX WARN: Code restructure failed: missing block: B:244:0x0461, code lost:
        if (r5.users.size() != 1) goto L246;
     */
    /* JADX WARN: Code restructure failed: missing block: B:245:0x0463, code lost:
        r2 = r23.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:247:0x0478, code lost:
        if (r2 == 0) goto L257;
     */
    /* JADX WARN: Code restructure failed: missing block: B:249:0x047c, code lost:
        if (r2 != r17) goto L252;
     */
    /* JADX WARN: Code restructure failed: missing block: B:251:0x0492, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", org.telegram.messenger.beta.R.string.NotificationGroupInvitedYouToCall, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:252:0x0493, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r2));
     */
    /* JADX WARN: Code restructure failed: missing block: B:253:0x049f, code lost:
        if (r0 != null) goto L255;
     */
    /* JADX WARN: Code restructure failed: missing block: B:254:0x04a1, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:0x04bd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.beta.R.string.NotificationGroupInvitedToCall, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:257:0x04be, code lost:
        r2 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:259:0x04ce, code lost:
        if (r3 >= r23.messageOwner.action.users.size()) goto L796;
     */
    /* JADX WARN: Code restructure failed: missing block: B:260:0x04d0, code lost:
        r4 = getMessagesController().getUser(r23.messageOwner.action.users.get(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:261:0x04e4, code lost:
        if (r4 == null) goto L798;
     */
    /* JADX WARN: Code restructure failed: missing block: B:262:0x04e6, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:263:0x04ee, code lost:
        if (r2.length() == 0) goto L265;
     */
    /* JADX WARN: Code restructure failed: missing block: B:264:0x04f0, code lost:
        r2.append(", ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:265:0x04f5, code lost:
        r2.append(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:266:0x04f8, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x0515, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.beta.R.string.NotificationGroupInvitedToCall, r1, r6.title, r2.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:270:0x0519, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L273;
     */
    /* JADX WARN: Code restructure failed: missing block: B:272:0x052f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", org.telegram.messenger.beta.R.string.NotificationInvitedToGroupByLink, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:274:0x0535, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L277;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0548, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", org.telegram.messenger.beta.R.string.NotificationEditedGroupName, r1, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:278:0x054b, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L656;
     */
    /* JADX WARN: Code restructure failed: missing block: B:280:0x054f, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L281;
     */
    /* JADX WARN: Code restructure failed: missing block: B:282:0x0555, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L296;
     */
    /* JADX WARN: Code restructure failed: missing block: B:283:0x0557, code lost:
        r2 = r5.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:284:0x055b, code lost:
        if (r2 != r17) goto L287;
     */
    /* JADX WARN: Code restructure failed: missing block: B:286:0x0571, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", org.telegram.messenger.beta.R.string.NotificationGroupKickYou, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x0577, code lost:
        if (r2 != r8) goto L291;
     */
    /* JADX WARN: Code restructure failed: missing block: B:290:0x058a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", org.telegram.messenger.beta.R.string.NotificationGroupLeftMember, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:291:0x058b, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r23.messageOwner.action.user_id));
     */
    /* JADX WARN: Code restructure failed: missing block: B:292:0x059d, code lost:
        if (r0 != null) goto L294;
     */
    /* JADX WARN: Code restructure failed: missing block: B:293:0x059f, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:295:0x05bc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", org.telegram.messenger.beta.R.string.NotificationGroupKickMember, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:297:0x05bf, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L300;
     */
    /* JADX WARN: Code restructure failed: missing block: B:299:0x05c7, code lost:
        return r23.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:301:0x05ca, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L304;
     */
    /* JADX WARN: Code restructure failed: missing block: B:303:0x05d2, code lost:
        return r23.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:305:0x05d5, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L308;
     */
    /* JADX WARN: Code restructure failed: missing block: B:307:0x05e8, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.beta.R.string.ActionMigrateFromGroupNotify, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:309:0x05ed, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L312;
     */
    /* JADX WARN: Code restructure failed: missing block: B:311:0x05fe, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.beta.R.string.ActionMigrateFromGroupNotify, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:313:0x0601, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L316;
     */
    /* JADX WARN: Code restructure failed: missing block: B:315:0x0609, code lost:
        return r23.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:317:0x060c, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L638;
     */
    /* JADX WARN: Code restructure failed: missing block: B:319:0x0612, code lost:
        if (r6 == null) goto L428;
     */
    /* JADX WARN: Code restructure failed: missing block: B:321:0x0618, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r6) == false) goto L324;
     */
    /* JADX WARN: Code restructure failed: missing block: B:323:0x061c, code lost:
        if (r6.megagroup == false) goto L428;
     */
    /* JADX WARN: Code restructure failed: missing block: B:324:0x061e, code lost:
        r0 = r23.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:325:0x0620, code lost:
        if (r0 != null) goto L328;
     */
    /* JADX WARN: Code restructure failed: missing block: B:327:0x0636, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoText, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:329:0x063e, code lost:
        if (r0.isMusic() == false) goto L332;
     */
    /* JADX WARN: Code restructure failed: missing block: B:331:0x0651, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", org.telegram.messenger.beta.R.string.NotificationActionPinnedMusic, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:333:0x065b, code lost:
        if (r0.isVideo() == false) goto L342;
     */
    /* JADX WARN: Code restructure failed: missing block: B:335:0x0661, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L340;
     */
    /* JADX WARN: Code restructure failed: missing block: B:337:0x066b, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L340;
     */
    /* JADX WARN: Code restructure failed: missing block: B:339:0x0692, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r1, "📹 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:341:0x06a7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", org.telegram.messenger.beta.R.string.NotificationActionPinnedVideo, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:343:0x06ac, code lost:
        if (r0.isGif() == false) goto L352;
     */
    /* JADX WARN: Code restructure failed: missing block: B:345:0x06b2, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L350;
     */
    /* JADX WARN: Code restructure failed: missing block: B:347:0x06bc, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L350;
     */
    /* JADX WARN: Code restructure failed: missing block: B:349:0x06e3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r1, "🎬 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:351:0x06f8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", org.telegram.messenger.beta.R.string.NotificationActionPinnedGif, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:353:0x0700, code lost:
        if (r0.isVoice() == false) goto L356;
     */
    /* JADX WARN: Code restructure failed: missing block: B:355:0x0713, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", org.telegram.messenger.beta.R.string.NotificationActionPinnedVoice, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:357:0x0718, code lost:
        if (r0.isRoundVideo() == false) goto L360;
     */
    /* JADX WARN: Code restructure failed: missing block: B:359:0x072b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", org.telegram.messenger.beta.R.string.NotificationActionPinnedRound, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:361:0x0730, code lost:
        if (r0.isSticker() != false) goto L422;
     */
    /* JADX WARN: Code restructure failed: missing block: B:363:0x0736, code lost:
        if (r0.isAnimatedSticker() == false) goto L364;
     */
    /* JADX WARN: Code restructure failed: missing block: B:364:0x073a, code lost:
        r4 = r0.messageOwner;
        r7 = r4.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:365:0x0740, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L374;
     */
    /* JADX WARN: Code restructure failed: missing block: B:367:0x0746, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L372;
     */
    /* JADX WARN: Code restructure failed: missing block: B:369:0x074e, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L372;
     */
    /* JADX WARN: Code restructure failed: missing block: B:371:0x0775, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r1, "📎 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:373:0x078a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", org.telegram.messenger.beta.R.string.NotificationActionPinnedFile, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:375:0x078d, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L420;
     */
    /* JADX WARN: Code restructure failed: missing block: B:377:0x0791, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L378;
     */
    /* JADX WARN: Code restructure failed: missing block: B:379:0x0797, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L382;
     */
    /* JADX WARN: Code restructure failed: missing block: B:381:0x07ad, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeoLive, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:383:0x07b2, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L386;
     */
    /* JADX WARN: Code restructure failed: missing block: B:384:0x07b4, code lost:
        r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:385:0x07d3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", org.telegram.messenger.beta.R.string.NotificationActionPinnedContact2, r1, r6.title, org.telegram.messenger.ContactsController.formatName(r7.first_name, r7.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:387:0x07d6, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L394;
     */
    /* JADX WARN: Code restructure failed: missing block: B:388:0x07d8, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:389:0x07de, code lost:
        if (r0.quiz == false) goto L392;
     */
    /* JADX WARN: Code restructure failed: missing block: B:391:0x07f9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", org.telegram.messenger.beta.R.string.NotificationActionPinnedQuiz2, r1, r6.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:393:0x0813, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", org.telegram.messenger.beta.R.string.NotificationActionPinnedPoll2, r1, r6.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:395:0x0816, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L404;
     */
    /* JADX WARN: Code restructure failed: missing block: B:397:0x081c, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L402;
     */
    /* JADX WARN: Code restructure failed: missing block: B:399:0x0824, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L402;
     */
    /* JADX WARN: Code restructure failed: missing block: B:401:0x084b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r1, "🖼 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:403:0x0860, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", org.telegram.messenger.beta.R.string.NotificationActionPinnedPhoto, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:405:0x0866, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L408;
     */
    /* JADX WARN: Code restructure failed: missing block: B:407:0x0879, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", org.telegram.messenger.beta.R.string.NotificationActionPinnedGame, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:408:0x087a, code lost:
        r4 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:409:0x087c, code lost:
        if (r4 == null) goto L418;
     */
    /* JADX WARN: Code restructure failed: missing block: B:411:0x0882, code lost:
        if (r4.length() <= 0) goto L418;
     */
    /* JADX WARN: Code restructure failed: missing block: B:412:0x0884, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:413:0x088a, code lost:
        if (r0.length() <= 20) goto L415;
     */
    /* JADX WARN: Code restructure failed: missing block: B:414:0x088c, code lost:
        r4 = new java.lang.StringBuilder();
        r7 = 0;
        r4.append((java.lang.Object) r0.subSequence(0, 20));
        r4.append("...");
        r0 = r4.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:415:0x08a1, code lost:
        r7 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:416:0x08a2, code lost:
        r2 = new java.lang.Object[3];
        r2[r7] = r1;
        r2[1] = r0;
        r2[2] = r6.title;
     */
    /* JADX WARN: Code restructure failed: missing block: B:417:0x08b3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:419:0x08c8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoText, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:421:0x08dd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeo, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:422:0x08de, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:423:0x08e4, code lost:
        if (r0 == null) goto L426;
     */
    /* JADX WARN: Code restructure failed: missing block: B:425:0x08fb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", org.telegram.messenger.beta.R.string.NotificationActionPinnedStickerEmoji, r1, r6.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:427:0x090e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", org.telegram.messenger.beta.R.string.NotificationActionPinnedSticker, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:429:0x0910, code lost:
        if (r6 == null) goto L534;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00a5, code lost:
        if (r11.getBoolean("EnablePreviewGroup", true) != false) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:430:0x0912, code lost:
        r0 = r23.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:431:0x0914, code lost:
        if (r0 != null) goto L434;
     */
    /* JADX WARN: Code restructure failed: missing block: B:433:0x0926, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoTextChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:435:0x092c, code lost:
        if (r0.isMusic() == false) goto L438;
     */
    /* JADX WARN: Code restructure failed: missing block: B:437:0x093d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedMusicChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:439:0x0947, code lost:
        if (r0.isVideo() == false) goto L448;
     */
    /* JADX WARN: Code restructure failed: missing block: B:441:0x094d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L446;
     */
    /* JADX WARN: Code restructure failed: missing block: B:443:0x0957, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L446;
     */
    /* JADX WARN: Code restructure failed: missing block: B:445:0x097b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r6.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:447:0x098d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedVideoChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:449:0x0992, code lost:
        if (r0.isGif() == false) goto L458;
     */
    /* JADX WARN: Code restructure failed: missing block: B:451:0x0998, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L456;
     */
    /* JADX WARN: Code restructure failed: missing block: B:453:0x09a2, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L456;
     */
    /* JADX WARN: Code restructure failed: missing block: B:455:0x09c6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r6.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:457:0x09d8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedGifChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:459:0x09df, code lost:
        if (r0.isVoice() == false) goto L462;
     */
    /* JADX WARN: Code restructure failed: missing block: B:461:0x09f0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedVoiceChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:463:0x09f5, code lost:
        if (r0.isRoundVideo() == false) goto L466;
     */
    /* JADX WARN: Code restructure failed: missing block: B:465:0x0a06, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedRoundChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:467:0x0a0b, code lost:
        if (r0.isSticker() != false) goto L528;
     */
    /* JADX WARN: Code restructure failed: missing block: B:469:0x0a11, code lost:
        if (r0.isAnimatedSticker() == false) goto L470;
     */
    /* JADX WARN: Code restructure failed: missing block: B:470:0x0a15, code lost:
        r1 = r0.messageOwner;
        r7 = r1.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:471:0x0a1b, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L480;
     */
    /* JADX WARN: Code restructure failed: missing block: B:473:0x0a21, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L478;
     */
    /* JADX WARN: Code restructure failed: missing block: B:475:0x0a29, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L478;
     */
    /* JADX WARN: Code restructure failed: missing block: B:477:0x0a4d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r6.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:479:0x0a5f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedFileChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00b3, code lost:
        if (r11.getBoolean("EnablePreviewChannel", r3) == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:481:0x0a62, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L526;
     */
    /* JADX WARN: Code restructure failed: missing block: B:483:0x0a66, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L484;
     */
    /* JADX WARN: Code restructure failed: missing block: B:485:0x0a6c, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L488;
     */
    /* JADX WARN: Code restructure failed: missing block: B:487:0x0a7f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeoLiveChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:489:0x0a83, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L492;
     */
    /* JADX WARN: Code restructure failed: missing block: B:490:0x0a85, code lost:
        r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:491:0x0aa2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", org.telegram.messenger.beta.R.string.NotificationActionPinnedContactChannel2, r6.title, org.telegram.messenger.ContactsController.formatName(r7.first_name, r7.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:493:0x0aa5, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L500;
     */
    /* JADX WARN: Code restructure failed: missing block: B:494:0x0aa7, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:495:0x0aad, code lost:
        if (r0.quiz == false) goto L498;
     */
    /* JADX WARN: Code restructure failed: missing block: B:497:0x0ac5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", org.telegram.messenger.beta.R.string.NotificationActionPinnedQuizChannel2, r6.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:499:0x0adc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", org.telegram.messenger.beta.R.string.NotificationActionPinnedPollChannel2, r6.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:501:0x0adf, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L510;
     */
    /* JADX WARN: Code restructure failed: missing block: B:503:0x0ae5, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L508;
     */
    /* JADX WARN: Code restructure failed: missing block: B:505:0x0aed, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L508;
     */
    /* JADX WARN: Code restructure failed: missing block: B:507:0x0b11, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r6.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:509:0x0b23, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedPhotoChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:511:0x0b28, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L514;
     */
    /* JADX WARN: Code restructure failed: missing block: B:513:0x0b39, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedGameChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:514:0x0b3a, code lost:
        r1 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:515:0x0b3c, code lost:
        if (r1 == null) goto L524;
     */
    /* JADX WARN: Code restructure failed: missing block: B:517:0x0b42, code lost:
        if (r1.length() <= 0) goto L524;
     */
    /* JADX WARN: Code restructure failed: missing block: B:518:0x0b44, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:519:0x0b4a, code lost:
        if (r0.length() <= 20) goto L521;
     */
    /* JADX WARN: Code restructure failed: missing block: B:520:0x0b4c, code lost:
        r1 = new java.lang.StringBuilder();
        r8 = 0;
        r1.append((java.lang.Object) r0.subSequence(0, 20));
        r1.append("...");
        r0 = r1.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:521:0x0b61, code lost:
        r8 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:522:0x0b62, code lost:
        r1 = new java.lang.Object[2];
        r1[r8] = r6.title;
        r1[1] = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:523:0x0b70, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:525:0x0b82, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoTextChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:527:0x0b94, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeoChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:528:0x0b95, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:529:0x0b9a, code lost:
        if (r0 == null) goto L532;
     */
    /* JADX WARN: Code restructure failed: missing block: B:531:0x0baf, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedStickerEmojiChannel, r6.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:533:0x0bc0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedStickerChannel, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:534:0x0bc1, code lost:
        r0 = r23.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:535:0x0bc4, code lost:
        if (r0 != null) goto L538;
     */
    /* JADX WARN: Code restructure failed: missing block: B:537:0x0bd3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoTextUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:539:0x0bd8, code lost:
        if (r0.isMusic() == false) goto L542;
     */
    /* JADX WARN: Code restructure failed: missing block: B:541:0x0be7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedMusicUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:543:0x0bf1, code lost:
        if (r0.isVideo() == false) goto L552;
     */
    /* JADX WARN: Code restructure failed: missing block: B:545:0x0bf7, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L550;
     */
    /* JADX WARN: Code restructure failed: missing block: B:547:0x0c01, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L550;
     */
    /* JADX WARN: Code restructure failed: missing block: B:549:0x0c23, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextUser, r1, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:551:0x0c33, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedVideoUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:553:0x0c38, code lost:
        if (r0.isGif() == false) goto L562;
     */
    /* JADX WARN: Code restructure failed: missing block: B:555:0x0c3e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L560;
     */
    /* JADX WARN: Code restructure failed: missing block: B:557:0x0c48, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L560;
     */
    /* JADX WARN: Code restructure failed: missing block: B:559:0x0c6a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextUser, r1, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:561:0x0c7a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedGifUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:563:0x0c81, code lost:
        if (r0.isVoice() == false) goto L566;
     */
    /* JADX WARN: Code restructure failed: missing block: B:565:0x0c90, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedVoiceUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:567:0x0c95, code lost:
        if (r0.isRoundVideo() == false) goto L570;
     */
    /* JADX WARN: Code restructure failed: missing block: B:569:0x0ca4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedRoundUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:571:0x0ca9, code lost:
        if (r0.isSticker() != false) goto L632;
     */
    /* JADX WARN: Code restructure failed: missing block: B:573:0x0caf, code lost:
        if (r0.isAnimatedSticker() == false) goto L574;
     */
    /* JADX WARN: Code restructure failed: missing block: B:574:0x0cb3, code lost:
        r4 = r0.messageOwner;
        r7 = r4.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:575:0x0cb9, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L584;
     */
    /* JADX WARN: Code restructure failed: missing block: B:577:0x0cbf, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L582;
     */
    /* JADX WARN: Code restructure failed: missing block: B:579:0x0cc7, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L582;
     */
    /* JADX WARN: Code restructure failed: missing block: B:581:0x0ce9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextUser, r1, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:583:0x0cf9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedFileUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:585:0x0cfc, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L630;
     */
    /* JADX WARN: Code restructure failed: missing block: B:587:0x0d00, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L588;
     */
    /* JADX WARN: Code restructure failed: missing block: B:589:0x0d06, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L592;
     */
    /* JADX WARN: Code restructure failed: missing block: B:591:0x0d17, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeoLiveUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:593:0x0d1b, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L596;
     */
    /* JADX WARN: Code restructure failed: missing block: B:594:0x0d1d, code lost:
        r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:595:0x0d38, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedContactUser, r1, org.telegram.messenger.ContactsController.formatName(r7.first_name, r7.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:597:0x0d3b, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L604;
     */
    /* JADX WARN: Code restructure failed: missing block: B:598:0x0d3d, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:599:0x0d43, code lost:
        if (r0.quiz == false) goto L602;
     */
    /* JADX WARN: Code restructure failed: missing block: B:601:0x0d59, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedQuizUser, r1, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:603:0x0d6e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedPollUser, r1, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:605:0x0d71, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L614;
     */
    /* JADX WARN: Code restructure failed: missing block: B:607:0x0d77, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L612;
     */
    /* JADX WARN: Code restructure failed: missing block: B:609:0x0d7f, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L612;
     */
    /* JADX WARN: Code restructure failed: missing block: B:611:0x0da1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextUser, r1, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:613:0x0db1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedPhotoUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:615:0x0db6, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L618;
     */
    /* JADX WARN: Code restructure failed: missing block: B:617:0x0dc5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedGameUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:618:0x0dc6, code lost:
        r4 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:619:0x0dc8, code lost:
        if (r4 == null) goto L628;
     */
    /* JADX WARN: Code restructure failed: missing block: B:621:0x0dce, code lost:
        if (r4.length() <= 0) goto L628;
     */
    /* JADX WARN: Code restructure failed: missing block: B:622:0x0dd0, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:623:0x0dd6, code lost:
        if (r0.length() <= 20) goto L625;
     */
    /* JADX WARN: Code restructure failed: missing block: B:624:0x0dd8, code lost:
        r4 = new java.lang.StringBuilder();
        r7 = 0;
        r4.append((java.lang.Object) r0.subSequence(0, 20));
        r4.append("...");
        r0 = r4.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:625:0x0ded, code lost:
        r7 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:626:0x0dee, code lost:
        r2 = new java.lang.Object[2];
        r2[r7] = r1;
        r2[1] = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:627:0x0dfa, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextUser, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:629:0x0e0a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoTextUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:631:0x0e1a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeoUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:632:0x0e1b, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:633:0x0e21, code lost:
        if (r0 == null) goto L636;
     */
    /* JADX WARN: Code restructure failed: missing block: B:635:0x0e33, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedStickerEmojiUser, r1, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:637:0x0e41, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", org.telegram.messenger.beta.R.string.NotificationActionPinnedStickerUser, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:639:0x0e44, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) == false) goto L651;
     */
    /* JADX WARN: Code restructure failed: missing block: B:640:0x0e46, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r5).emoticon;
     */
    /* JADX WARN: Code restructure failed: missing block: B:641:0x0e4e, code lost:
        if (android.text.TextUtils.isEmpty(r0) == false) goto L646;
     */
    /* JADX WARN: Code restructure failed: missing block: B:643:0x0e52, code lost:
        if (r2 != r17) goto L645;
     */
    /* JADX WARN: Code restructure failed: missing block: B:647:0x0e78, code lost:
        if (r2 != r17) goto L649;
     */
    /* JADX WARN: Code restructure failed: missing block: B:650:0x0e98, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChangedChatThemeTo", org.telegram.messenger.beta.R.string.ChatThemeChangedTo, r1, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:652:0x0e9b, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest) == false) goto L655;
     */
    /* JADX WARN: Code restructure failed: missing block: B:654:0x0ea3, code lost:
        return r23.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:655:0x0ea4, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:657:0x0eae, code lost:
        if (r4.peer_id.channel_id == 0) goto L666;
     */
    /* JADX WARN: Code restructure failed: missing block: B:659:0x0eb2, code lost:
        if (r6.megagroup != false) goto L666;
     */
    /* JADX WARN: Code restructure failed: missing block: B:661:0x0eb8, code lost:
        if (r23.isVideoAvatar() == false) goto L664;
     */
    /* JADX WARN: Code restructure failed: missing block: B:663:0x0ecb, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", org.telegram.messenger.beta.R.string.ChannelVideoEditNotification, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:665:0x0edd, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", org.telegram.messenger.beta.R.string.ChannelPhotoEditNotification, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:667:0x0ee3, code lost:
        if (r23.isVideoAvatar() == false) goto L670;
     */
    /* JADX WARN: Code restructure failed: missing block: B:669:0x0ef8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", org.telegram.messenger.beta.R.string.NotificationEditedGroupVideo, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:671:0x0f0c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", org.telegram.messenger.beta.R.string.NotificationEditedGroupPhoto, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:673:0x0f13, code lost:
        return r23.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:675:0x0f23, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", org.telegram.messenger.beta.R.string.NotificationContactJoined, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:677:0x0f28, code lost:
        if (r23.isMediaEmpty() == false) goto L684;
     */
    /* JADX WARN: Code restructure failed: missing block: B:679:0x0f32, code lost:
        if (android.text.TextUtils.isEmpty(r23.messageOwner.message) != false) goto L682;
     */
    /* JADX WARN: Code restructure failed: missing block: B:681:0x0f38, code lost:
        return replaceSpoilers(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:683:0x0f41, code lost:
        return org.telegram.messenger.LocaleController.getString(r13, org.telegram.messenger.beta.R.string.Message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:684:0x0f42, code lost:
        r1 = r13;
        r2 = r23.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:685:0x0f49, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L698;
     */
    /* JADX WARN: Code restructure failed: missing block: B:687:0x0f4f, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L692;
     */
    /* JADX WARN: Code restructure failed: missing block: B:689:0x0f57, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L692;
     */
    /* JADX WARN: Code restructure failed: missing block: B:691:0x0f6c, code lost:
        return "🖼 " + replaceSpoilers(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:693:0x0f73, code lost:
        if (r23.messageOwner.media.ttl_seconds == 0) goto L696;
     */
    /* JADX WARN: Code restructure failed: missing block: B:695:0x0f7e, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", org.telegram.messenger.beta.R.string.AttachDestructingPhoto);
     */
    /* JADX WARN: Code restructure failed: missing block: B:697:0x0f88, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.beta.R.string.AttachPhoto);
     */
    /* JADX WARN: Code restructure failed: missing block: B:699:0x0f8d, code lost:
        if (r23.isVideo() == false) goto L712;
     */
    /* JADX WARN: Code restructure failed: missing block: B:701:0x0f93, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L706;
     */
    /* JADX WARN: Code restructure failed: missing block: B:703:0x0f9d, code lost:
        if (android.text.TextUtils.isEmpty(r23.messageOwner.message) != false) goto L706;
     */
    /* JADX WARN: Code restructure failed: missing block: B:705:0x0fb2, code lost:
        return "📹 " + replaceSpoilers(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:707:0x0fb9, code lost:
        if (r23.messageOwner.media.ttl_seconds == 0) goto L710;
     */
    /* JADX WARN: Code restructure failed: missing block: B:709:0x0fc4, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", org.telegram.messenger.beta.R.string.AttachDestructingVideo);
     */
    /* JADX WARN: Code restructure failed: missing block: B:711:0x0fce, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.beta.R.string.AttachVideo);
     */
    /* JADX WARN: Code restructure failed: missing block: B:713:0x0fd3, code lost:
        if (r23.isGame() == false) goto L716;
     */
    /* JADX WARN: Code restructure failed: missing block: B:715:0x0fde, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.beta.R.string.AttachGame);
     */
    /* JADX WARN: Code restructure failed: missing block: B:717:0x0fe3, code lost:
        if (r23.isVoice() == false) goto L720;
     */
    /* JADX WARN: Code restructure failed: missing block: B:719:0x0fee, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.beta.R.string.AttachAudio);
     */
    /* JADX WARN: Code restructure failed: missing block: B:721:0x0ff3, code lost:
        if (r23.isRoundVideo() == false) goto L724;
     */
    /* JADX WARN: Code restructure failed: missing block: B:723:0x0ffe, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.beta.R.string.AttachRound);
     */
    /* JADX WARN: Code restructure failed: missing block: B:725:0x1003, code lost:
        if (r23.isMusic() == false) goto L728;
     */
    /* JADX WARN: Code restructure failed: missing block: B:727:0x100e, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", org.telegram.messenger.beta.R.string.AttachMusic);
     */
    /* JADX WARN: Code restructure failed: missing block: B:728:0x100f, code lost:
        r2 = r23.messageOwner.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:729:0x1015, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L732;
     */
    /* JADX WARN: Code restructure failed: missing block: B:731:0x1020, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.beta.R.string.AttachContact);
     */
    /* JADX WARN: Code restructure failed: missing block: B:733:0x1023, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L740;
     */
    /* JADX WARN: Code restructure failed: missing block: B:735:0x102b, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll.quiz == false) goto L738;
     */
    /* JADX WARN: Code restructure failed: missing block: B:737:0x1036, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.beta.R.string.QuizPoll);
     */
    /* JADX WARN: Code restructure failed: missing block: B:739:0x1040, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.beta.R.string.Poll);
     */
    /* JADX WARN: Code restructure failed: missing block: B:741:0x1043, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L784;
     */
    /* JADX WARN: Code restructure failed: missing block: B:743:0x1047, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L744;
     */
    /* JADX WARN: Code restructure failed: missing block: B:745:0x104d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L748;
     */
    /* JADX WARN: Code restructure failed: missing block: B:747:0x1058, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.beta.R.string.AttachLiveLocation);
     */
    /* JADX WARN: Code restructure failed: missing block: B:749:0x105b, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L778;
     */
    /* JADX WARN: Code restructure failed: missing block: B:751:0x1061, code lost:
        if (r23.isSticker() != false) goto L772;
     */
    /* JADX WARN: Code restructure failed: missing block: B:753:0x1067, code lost:
        if (r23.isAnimatedSticker() == false) goto L754;
     */
    /* JADX WARN: Code restructure failed: missing block: B:755:0x106e, code lost:
        if (r23.isGif() == false) goto L764;
     */
    /* JADX WARN: Code restructure failed: missing block: B:757:0x1074, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L762;
     */
    /* JADX WARN: Code restructure failed: missing block: B:759:0x107e, code lost:
        if (android.text.TextUtils.isEmpty(r23.messageOwner.message) != false) goto L762;
     */
    /* JADX WARN: Code restructure failed: missing block: B:761:0x1093, code lost:
        return "🎬 " + replaceSpoilers(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:763:0x109d, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.beta.R.string.AttachGif);
     */
    /* JADX WARN: Code restructure failed: missing block: B:765:0x10a2, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L770;
     */
    /* JADX WARN: Code restructure failed: missing block: B:767:0x10ac, code lost:
        if (android.text.TextUtils.isEmpty(r23.messageOwner.message) != false) goto L770;
     */
    /* JADX WARN: Code restructure failed: missing block: B:769:0x10c1, code lost:
        return "📎 " + replaceSpoilers(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:771:0x10cb, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.beta.R.string.AttachDocument);
     */
    /* JADX WARN: Code restructure failed: missing block: B:772:0x10cc, code lost:
        r0 = r23.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:773:0x10d0, code lost:
        if (r0 == null) goto L776;
     */
    /* JADX WARN: Code restructure failed: missing block: B:775:0x10ef, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.beta.R.string.AttachSticker);
     */
    /* JADX WARN: Code restructure failed: missing block: B:777:0x10f9, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.beta.R.string.AttachSticker);
     */
    /* JADX WARN: Code restructure failed: missing block: B:779:0x1100, code lost:
        if (android.text.TextUtils.isEmpty(r23.messageText) != false) goto L782;
     */
    /* JADX WARN: Code restructure failed: missing block: B:781:0x1106, code lost:
        return replaceSpoilers(r23);
     */
    /* JADX WARN: Code restructure failed: missing block: B:783:0x110e, code lost:
        return org.telegram.messenger.LocaleController.getString(r1, org.telegram.messenger.beta.R.string.Message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:785:0x1118, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.beta.R.string.AttachLocation);
     */
    /* JADX WARN: Code restructure failed: missing block: B:799:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChatThemeDisabledYou", org.telegram.messenger.beta.R.string.ChatThemeDisabledYou, new java.lang.Object[0]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:800:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChatThemeDisabled", org.telegram.messenger.beta.R.string.ChatThemeDisabled, r1, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:801:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChangedChatThemeYou", org.telegram.messenger.beta.R.string.ChatThemeChangedYou, r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r23, java.lang.String[] r24, boolean[] r25) {
        /*
            Method dump skipped, instructions count: 4401
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    private String replaceSpoilers(MessageObject messageObject) {
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        String str = tLRPC$Message.message;
        if (str == null || tLRPC$Message == null || tLRPC$Message.entities == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < messageObject.messageOwner.entities.size(); i++) {
            if (messageObject.messageOwner.entities.get(i) instanceof TLRPC$TL_messageEntitySpoiler) {
                TLRPC$TL_messageEntitySpoiler tLRPC$TL_messageEntitySpoiler = (TLRPC$TL_messageEntitySpoiler) messageObject.messageOwner.entities.get(i);
                for (int i2 = 0; i2 < tLRPC$TL_messageEntitySpoiler.length; i2++) {
                    char[] cArr = this.spoilerChars;
                    sb.setCharAt(tLRPC$TL_messageEntitySpoiler.offset + i2, cArr[i2 % cArr.length]);
                }
            }
        }
        return sb.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:248:0x0614, code lost:
        if (r11.getBoolean("EnablePreviewGroup", true) == false) goto L250;
     */
    /* JADX WARN: Code restructure failed: missing block: B:252:0x0620, code lost:
        if (r11.getBoolean("EnablePreviewChannel", r10) != false) goto L253;
     */
    /* JADX WARN: Code restructure failed: missing block: B:253:0x0622, code lost:
        r5 = r27.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:254:0x0626, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L563;
     */
    /* JADX WARN: Code restructure failed: missing block: B:255:0x0628, code lost:
        r6 = r5.action;
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:0x062c, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L293;
     */
    /* JADX WARN: Code restructure failed: missing block: B:257:0x062e, code lost:
        r2 = r6.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:258:0x0634, code lost:
        if (r2 != 0) goto L262;
     */
    /* JADX WARN: Code restructure failed: missing block: B:260:0x063d, code lost:
        if (r6.users.size() != 1) goto L262;
     */
    /* JADX WARN: Code restructure failed: missing block: B:261:0x063f, code lost:
        r2 = r27.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:263:0x0654, code lost:
        if (r2 == 0) goto L282;
     */
    /* JADX WARN: Code restructure failed: missing block: B:265:0x065e, code lost:
        if (r27.messageOwner.peer_id.channel_id == 0) goto L269;
     */
    /* JADX WARN: Code restructure failed: missing block: B:267:0x0662, code lost:
        if (r4.megagroup != false) goto L269;
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x0664, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", org.telegram.messenger.beta.R.string.ChannelAddedByNotification, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:270:0x067c, code lost:
        if (r2 != r17) goto L272;
     */
    /* JADX WARN: Code restructure failed: missing block: B:271:0x067e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", org.telegram.messenger.beta.R.string.NotificationInvitedToGroup, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:272:0x0694, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r2));
     */
    /* JADX WARN: Code restructure failed: missing block: B:273:0x06a0, code lost:
        if (r0 != null) goto L275;
     */
    /* JADX WARN: Code restructure failed: missing block: B:274:0x06a2, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x06a8, code lost:
        if (r8 != r0.id) goto L281;
     */
    /* JADX WARN: Code restructure failed: missing block: B:278:0x06ac, code lost:
        if (r4.megagroup == false) goto L280;
     */
    /* JADX WARN: Code restructure failed: missing block: B:279:0x06ae, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", org.telegram.messenger.beta.R.string.NotificationGroupAddSelfMega, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:280:0x06c4, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", org.telegram.messenger.beta.R.string.NotificationGroupAddSelf, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:281:0x06da, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.beta.R.string.NotificationGroupAddMember, r1, r4.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:282:0x06f7, code lost:
        r2 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:284:0x0707, code lost:
        if (r3 >= r27.messageOwner.action.users.size()) goto L749;
     */
    /* JADX WARN: Code restructure failed: missing block: B:285:0x0709, code lost:
        r5 = getMessagesController().getUser(r27.messageOwner.action.users.get(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:286:0x071d, code lost:
        if (r5 == null) goto L751;
     */
    /* JADX WARN: Code restructure failed: missing block: B:287:0x071f, code lost:
        r5 = org.telegram.messenger.UserObject.getUserName(r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x0727, code lost:
        if (r2.length() == 0) goto L290;
     */
    /* JADX WARN: Code restructure failed: missing block: B:289:0x0729, code lost:
        r2.append(", ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:290:0x072e, code lost:
        r2.append(r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:291:0x0731, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:292:0x0734, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.beta.R.string.NotificationGroupAddMember, r1, r4.title, r2.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:294:0x0754, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L296;
     */
    /* JADX WARN: Code restructure failed: missing block: B:297:0x076d, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L299;
     */
    /* JADX WARN: Code restructure failed: missing block: B:300:0x0779, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L326;
     */
    /* JADX WARN: Code restructure failed: missing block: B:301:0x077b, code lost:
        r2 = r6.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:302:0x0781, code lost:
        if (r2 != 0) goto L306;
     */
    /* JADX WARN: Code restructure failed: missing block: B:304:0x078a, code lost:
        if (r6.users.size() != 1) goto L306;
     */
    /* JADX WARN: Code restructure failed: missing block: B:305:0x078c, code lost:
        r2 = r27.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:307:0x07a1, code lost:
        if (r2 == 0) goto L315;
     */
    /* JADX WARN: Code restructure failed: missing block: B:309:0x07a5, code lost:
        if (r2 != r17) goto L311;
     */
    /* JADX WARN: Code restructure failed: missing block: B:310:0x07a7, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", org.telegram.messenger.beta.R.string.NotificationGroupInvitedYouToCall, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:311:0x07bd, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r2));
     */
    /* JADX WARN: Code restructure failed: missing block: B:312:0x07c9, code lost:
        if (r0 != null) goto L314;
     */
    /* JADX WARN: Code restructure failed: missing block: B:313:0x07cb, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:314:0x07cd, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.beta.R.string.NotificationGroupInvitedToCall, r1, r4.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:315:0x07ea, code lost:
        r2 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:317:0x07fa, code lost:
        if (r3 >= r27.messageOwner.action.users.size()) goto L752;
     */
    /* JADX WARN: Code restructure failed: missing block: B:318:0x07fc, code lost:
        r5 = getMessagesController().getUser(r27.messageOwner.action.users.get(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:319:0x0810, code lost:
        if (r5 == null) goto L754;
     */
    /* JADX WARN: Code restructure failed: missing block: B:320:0x0812, code lost:
        r5 = org.telegram.messenger.UserObject.getUserName(r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:321:0x081a, code lost:
        if (r2.length() == 0) goto L323;
     */
    /* JADX WARN: Code restructure failed: missing block: B:322:0x081c, code lost:
        r2.append(", ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:323:0x0821, code lost:
        r2.append(r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:324:0x0824, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:325:0x0827, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.beta.R.string.NotificationGroupInvitedToCall, r1, r4.title, r2.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:327:0x0847, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L329;
     */
    /* JADX WARN: Code restructure failed: missing block: B:330:0x0861, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L332;
     */
    /* JADX WARN: Code restructure failed: missing block: B:333:0x0879, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L551;
     */
    /* JADX WARN: Code restructure failed: missing block: B:335:0x087d, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L336;
     */
    /* JADX WARN: Code restructure failed: missing block: B:337:0x0883, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L348;
     */
    /* JADX WARN: Code restructure failed: missing block: B:338:0x0885, code lost:
        r2 = r6.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:339:0x0889, code lost:
        if (r2 != r17) goto L341;
     */
    /* JADX WARN: Code restructure failed: missing block: B:342:0x08a6, code lost:
        if (r2 != r8) goto L344;
     */
    /* JADX WARN: Code restructure failed: missing block: B:344:0x08bb, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r27.messageOwner.action.user_id));
     */
    /* JADX WARN: Code restructure failed: missing block: B:345:0x08cd, code lost:
        if (r0 != null) goto L347;
     */
    /* JADX WARN: Code restructure failed: missing block: B:346:0x08cf, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:348:0x08ee, code lost:
        r8 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:349:0x08f1, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L351;
     */
    /* JADX WARN: Code restructure failed: missing block: B:352:0x08fd, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L354;
     */
    /* JADX WARN: Code restructure failed: missing block: B:355:0x0909, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L357;
     */
    /* JADX WARN: Code restructure failed: missing block: B:358:0x0922, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L360;
     */
    /* JADX WARN: Code restructure failed: missing block: B:361:0x0937, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L363;
     */
    /* JADX WARN: Code restructure failed: missing block: B:364:0x0943, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L533;
     */
    /* JADX WARN: Code restructure failed: missing block: B:366:0x0949, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r4) == false) goto L451;
     */
    /* JADX WARN: Code restructure failed: missing block: B:368:0x094d, code lost:
        if (r4.megagroup == false) goto L369;
     */
    /* JADX WARN: Code restructure failed: missing block: B:369:0x0951, code lost:
        r1 = r27.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:370:0x0953, code lost:
        if (r1 != null) goto L372;
     */
    /* JADX WARN: Code restructure failed: missing block: B:373:0x096e, code lost:
        if (r1.isMusic() == false) goto L375;
     */
    /* JADX WARN: Code restructure failed: missing block: B:374:0x0970, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedMusicChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:376:0x098a, code lost:
        if (r1.isVideo() == false) goto L383;
     */
    /* JADX WARN: Code restructure failed: missing block: B:378:0x0990, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L382;
     */
    /* JADX WARN: Code restructure failed: missing block: B:380:0x099a, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L382;
     */
    /* JADX WARN: Code restructure failed: missing block: B:381:0x099c, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r4.title, "📹 " + r1.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:382:0x09c0, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedVideoChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:384:0x09d7, code lost:
        if (r1.isGif() == false) goto L391;
     */
    /* JADX WARN: Code restructure failed: missing block: B:386:0x09dd, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L390;
     */
    /* JADX WARN: Code restructure failed: missing block: B:388:0x09e7, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L390;
     */
    /* JADX WARN: Code restructure failed: missing block: B:389:0x09e9, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r4.title, "🎬 " + r1.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:390:0x0a0d, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedGifChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:392:0x0a26, code lost:
        if (r1.isVoice() == false) goto L394;
     */
    /* JADX WARN: Code restructure failed: missing block: B:393:0x0a28, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedVoiceChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:395:0x0a3d, code lost:
        if (r1.isRoundVideo() == false) goto L397;
     */
    /* JADX WARN: Code restructure failed: missing block: B:396:0x0a3f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedRoundChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:398:0x0a54, code lost:
        if (r1.isSticker() != false) goto L447;
     */
    /* JADX WARN: Code restructure failed: missing block: B:400:0x0a5a, code lost:
        if (r1.isAnimatedSticker() == false) goto L401;
     */
    /* JADX WARN: Code restructure failed: missing block: B:401:0x0a5e, code lost:
        r2 = r1.messageOwner;
        r6 = r2.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:402:0x0a64, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L409;
     */
    /* JADX WARN: Code restructure failed: missing block: B:404:0x0a6a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L408;
     */
    /* JADX WARN: Code restructure failed: missing block: B:406:0x0a72, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L408;
     */
    /* JADX WARN: Code restructure failed: missing block: B:407:0x0a74, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r4.title, "📎 " + r1.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:408:0x0a98, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedFileChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:410:0x0aad, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L446;
     */
    /* JADX WARN: Code restructure failed: missing block: B:412:0x0ab1, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L413;
     */
    /* JADX WARN: Code restructure failed: missing block: B:414:0x0ab7, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L416;
     */
    /* JADX WARN: Code restructure failed: missing block: B:415:0x0ab9, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeoLiveChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:417:0x0ace, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L419;
     */
    /* JADX WARN: Code restructure failed: missing block: B:418:0x0ad0, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r27.messageOwner.media;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", org.telegram.messenger.beta.R.string.NotificationActionPinnedContactChannel2, r4.title, org.telegram.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:420:0x0af6, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L425;
     */
    /* JADX WARN: Code restructure failed: missing block: B:421:0x0af8, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:422:0x0afe, code lost:
        if (r0.quiz == false) goto L424;
     */
    /* JADX WARN: Code restructure failed: missing block: B:423:0x0b00, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", org.telegram.messenger.beta.R.string.NotificationActionPinnedQuizChannel2, r4.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:424:0x0b18, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", org.telegram.messenger.beta.R.string.NotificationActionPinnedPollChannel2, r4.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:426:0x0b32, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L433;
     */
    /* JADX WARN: Code restructure failed: missing block: B:428:0x0b38, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L432;
     */
    /* JADX WARN: Code restructure failed: missing block: B:430:0x0b40, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L432;
     */
    /* JADX WARN: Code restructure failed: missing block: B:431:0x0b42, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r4.title, "🖼 " + r1.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:432:0x0b66, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedPhotoChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:434:0x0b7d, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L436;
     */
    /* JADX WARN: Code restructure failed: missing block: B:435:0x0b7f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedGameChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:436:0x0b90, code lost:
        r0 = r1.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:437:0x0b92, code lost:
        if (r0 == null) goto L445;
     */
    /* JADX WARN: Code restructure failed: missing block: B:439:0x0b98, code lost:
        if (r0.length() <= 0) goto L445;
     */
    /* JADX WARN: Code restructure failed: missing block: B:440:0x0b9a, code lost:
        r0 = r1.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:441:0x0ba2, code lost:
        if (r0.length() <= 20) goto L443;
     */
    /* JADX WARN: Code restructure failed: missing block: B:442:0x0ba4, code lost:
        r1 = new java.lang.StringBuilder();
        r6 = 0;
        r1.append((java.lang.Object) r0.subSequence(0, 20));
        r1.append("...");
        r0 = r1.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:443:0x0bbd, code lost:
        r6 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:444:0x0bbe, code lost:
        r1 = new java.lang.Object[2];
        r1[r6] = r4.title;
        r1[1] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedTextChannel, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:445:0x0bce, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoTextChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:446:0x0be1, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeoChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:447:0x0bf4, code lost:
        r0 = r1.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:448:0x0bf9, code lost:
        if (r0 == null) goto L450;
     */
    /* JADX WARN: Code restructure failed: missing block: B:449:0x0bfb, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedStickerEmojiChannel, r4.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:450:0x0c10, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedStickerChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:451:0x0c22, code lost:
        r2 = r27.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:452:0x0c25, code lost:
        if (r2 != null) goto L454;
     */
    /* JADX WARN: Code restructure failed: missing block: B:455:0x0c42, code lost:
        if (r2.isMusic() == false) goto L457;
     */
    /* JADX WARN: Code restructure failed: missing block: B:456:0x0c44, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", org.telegram.messenger.beta.R.string.NotificationActionPinnedMusic, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:458:0x0c60, code lost:
        if (r2.isVideo() == false) goto L465;
     */
    /* JADX WARN: Code restructure failed: missing block: B:460:0x0c66, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L464;
     */
    /* JADX WARN: Code restructure failed: missing block: B:462:0x0c70, code lost:
        if (android.text.TextUtils.isEmpty(r2.messageOwner.message) != false) goto L464;
     */
    /* JADX WARN: Code restructure failed: missing block: B:463:0x0c72, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r1, "📹 " + r2.messageOwner.message, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:464:0x0c99, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", org.telegram.messenger.beta.R.string.NotificationActionPinnedVideo, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:466:0x0cb3, code lost:
        if (r2.isGif() == false) goto L473;
     */
    /* JADX WARN: Code restructure failed: missing block: B:468:0x0cb9, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L472;
     */
    /* JADX WARN: Code restructure failed: missing block: B:470:0x0cc3, code lost:
        if (android.text.TextUtils.isEmpty(r2.messageOwner.message) != false) goto L472;
     */
    /* JADX WARN: Code restructure failed: missing block: B:471:0x0cc5, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r1, "🎬 " + r2.messageOwner.message, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:472:0x0cec, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", org.telegram.messenger.beta.R.string.NotificationActionPinnedGif, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:474:0x0d09, code lost:
        if (r2.isVoice() == false) goto L476;
     */
    /* JADX WARN: Code restructure failed: missing block: B:475:0x0d0b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", org.telegram.messenger.beta.R.string.NotificationActionPinnedVoice, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:477:0x0d22, code lost:
        if (r2.isRoundVideo() == false) goto L479;
     */
    /* JADX WARN: Code restructure failed: missing block: B:478:0x0d24, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", org.telegram.messenger.beta.R.string.NotificationActionPinnedRound, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:480:0x0d3b, code lost:
        if (r2.isSticker() != false) goto L529;
     */
    /* JADX WARN: Code restructure failed: missing block: B:482:0x0d41, code lost:
        if (r2.isAnimatedSticker() == false) goto L483;
     */
    /* JADX WARN: Code restructure failed: missing block: B:483:0x0d45, code lost:
        r3 = r2.messageOwner;
        r7 = r3.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:484:0x0d4b, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L491;
     */
    /* JADX WARN: Code restructure failed: missing block: B:486:0x0d51, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L490;
     */
    /* JADX WARN: Code restructure failed: missing block: B:488:0x0d59, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L490;
     */
    /* JADX WARN: Code restructure failed: missing block: B:489:0x0d5b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r1, "📎 " + r2.messageOwner.message, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:490:0x0d82, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", org.telegram.messenger.beta.R.string.NotificationActionPinnedFile, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:492:0x0d9a, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L528;
     */
    /* JADX WARN: Code restructure failed: missing block: B:494:0x0d9e, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L495;
     */
    /* JADX WARN: Code restructure failed: missing block: B:496:0x0da4, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L498;
     */
    /* JADX WARN: Code restructure failed: missing block: B:497:0x0da6, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeoLive, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:499:0x0dbe, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L501;
     */
    /* JADX WARN: Code restructure failed: missing block: B:500:0x0dc0, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r27.messageOwner.media;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", org.telegram.messenger.beta.R.string.NotificationActionPinnedContact2, r1, r4.title, org.telegram.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:502:0x0de9, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L507;
     */
    /* JADX WARN: Code restructure failed: missing block: B:503:0x0deb, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:504:0x0df1, code lost:
        if (r0.quiz == false) goto L506;
     */
    /* JADX WARN: Code restructure failed: missing block: B:505:0x0df3, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", org.telegram.messenger.beta.R.string.NotificationActionPinnedQuiz2, r1, r4.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:506:0x0e0e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", org.telegram.messenger.beta.R.string.NotificationActionPinnedPoll2, r1, r4.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:508:0x0e2b, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L515;
     */
    /* JADX WARN: Code restructure failed: missing block: B:510:0x0e31, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L514;
     */
    /* JADX WARN: Code restructure failed: missing block: B:512:0x0e39, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L514;
     */
    /* JADX WARN: Code restructure failed: missing block: B:513:0x0e3b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r1, "🖼 " + r2.messageOwner.message, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:514:0x0e62, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", org.telegram.messenger.beta.R.string.NotificationActionPinnedPhoto, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:516:0x0e7d, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L518;
     */
    /* JADX WARN: Code restructure failed: missing block: B:517:0x0e7f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", org.telegram.messenger.beta.R.string.NotificationActionPinnedGame, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:518:0x0e92, code lost:
        r0 = r2.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:519:0x0e94, code lost:
        if (r0 == null) goto L527;
     */
    /* JADX WARN: Code restructure failed: missing block: B:521:0x0e9a, code lost:
        if (r0.length() <= 0) goto L527;
     */
    /* JADX WARN: Code restructure failed: missing block: B:522:0x0e9c, code lost:
        r0 = r2.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:523:0x0ea4, code lost:
        if (r0.length() <= 20) goto L525;
     */
    /* JADX WARN: Code restructure failed: missing block: B:524:0x0ea6, code lost:
        r2 = new java.lang.StringBuilder();
        r7 = 0;
        r2.append((java.lang.Object) r0.subSequence(0, 20));
        r2.append("...");
        r0 = r2.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:525:0x0ebf, code lost:
        r7 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:526:0x0ec0, code lost:
        r2 = new java.lang.Object[3];
        r2[r7] = r1;
        r2[1] = r0;
        r2[2] = r4.title;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.beta.R.string.NotificationActionPinnedText, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:527:0x0ed3, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoText, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:528:0x0ee9, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", org.telegram.messenger.beta.R.string.NotificationActionPinnedGeo, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:529:0x0eff, code lost:
        r0 = r2.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:530:0x0f05, code lost:
        if (r0 == null) goto L532;
     */
    /* JADX WARN: Code restructure failed: missing block: B:531:0x0f07, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", org.telegram.messenger.beta.R.string.NotificationActionPinnedStickerEmoji, r1, r4.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:532:0x0f1e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", org.telegram.messenger.beta.R.string.NotificationActionPinnedSticker, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:534:0x0f34, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) == false) goto L536;
     */
    /* JADX WARN: Code restructure failed: missing block: B:537:0x0f40, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) == false) goto L548;
     */
    /* JADX WARN: Code restructure failed: missing block: B:538:0x0f42, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r6).emoticon;
     */
    /* JADX WARN: Code restructure failed: missing block: B:539:0x0f4a, code lost:
        if (android.text.TextUtils.isEmpty(r0) == false) goto L544;
     */
    /* JADX WARN: Code restructure failed: missing block: B:541:0x0f4e, code lost:
        if (r2 != r17) goto L543;
     */
    /* JADX WARN: Code restructure failed: missing block: B:542:0x0f50, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChatThemeDisabledYou", org.telegram.messenger.beta.R.string.ChatThemeDisabledYou, new java.lang.Object[0]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:543:0x0f5e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChatThemeDisabled", org.telegram.messenger.beta.R.string.ChatThemeDisabled, r1, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:545:0x0f76, code lost:
        if (r2 != r17) goto L547;
     */
    /* JADX WARN: Code restructure failed: missing block: B:546:0x0f78, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChangedChatThemeYou", org.telegram.messenger.beta.R.string.ChatThemeChangedYou, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:547:0x0f87, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChangedChatThemeTo", org.telegram.messenger.beta.R.string.ChatThemeChangedTo, r1, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:549:0x0f9b, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest) == false) goto L745;
     */
    /* JADX WARN: Code restructure failed: missing block: B:552:0x0fad, code lost:
        if (r5.peer_id.channel_id == 0) goto L559;
     */
    /* JADX WARN: Code restructure failed: missing block: B:554:0x0fb1, code lost:
        if (r4.megagroup != false) goto L559;
     */
    /* JADX WARN: Code restructure failed: missing block: B:556:0x0fb7, code lost:
        if (r27.isVideoAvatar() == false) goto L558;
     */
    /* JADX WARN: Code restructure failed: missing block: B:560:0x0fe4, code lost:
        if (r27.isVideoAvatar() == false) goto L562;
     */
    /* JADX WARN: Code restructure failed: missing block: B:564:0x1014, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r4) == false) goto L650;
     */
    /* JADX WARN: Code restructure failed: missing block: B:566:0x1018, code lost:
        if (r4.megagroup != false) goto L650;
     */
    /* JADX WARN: Code restructure failed: missing block: B:568:0x101e, code lost:
        if (r27.isMediaEmpty() == false) goto L574;
     */
    /* JADX WARN: Code restructure failed: missing block: B:569:0x1020, code lost:
        if (r28 != false) goto L573;
     */
    /* JADX WARN: Code restructure failed: missing block: B:571:0x102a, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageOwner.message) != false) goto L573;
     */
    /* JADX WARN: Code restructure failed: missing block: B:572:0x102c, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.beta.R.string.NotificationMessageText, r1, r27.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:574:0x1055, code lost:
        r2 = r27.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:575:0x105b, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L583;
     */
    /* JADX WARN: Code restructure failed: missing block: B:576:0x105d, code lost:
        if (r28 != false) goto L582;
     */
    /* JADX WARN: Code restructure failed: missing block: B:578:0x1063, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L582;
     */
    /* JADX WARN: Code restructure failed: missing block: B:580:0x106b, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L582;
     */
    /* JADX WARN: Code restructure failed: missing block: B:581:0x106d, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.beta.R.string.NotificationMessageText, r1, "🖼 " + r27.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:584:0x10a9, code lost:
        if (r27.isVideo() == false) goto L592;
     */
    /* JADX WARN: Code restructure failed: missing block: B:585:0x10ab, code lost:
        if (r28 != false) goto L591;
     */
    /* JADX WARN: Code restructure failed: missing block: B:587:0x10b1, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L591;
     */
    /* JADX WARN: Code restructure failed: missing block: B:589:0x10bb, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageOwner.message) != false) goto L591;
     */
    /* JADX WARN: Code restructure failed: missing block: B:590:0x10bd, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.beta.R.string.NotificationMessageText, r1, "📹 " + r27.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:593:0x10fb, code lost:
        if (r27.isVoice() == false) goto L595;
     */
    /* JADX WARN: Code restructure failed: missing block: B:596:0x1110, code lost:
        if (r27.isRoundVideo() == false) goto L598;
     */
    /* JADX WARN: Code restructure failed: missing block: B:599:0x1125, code lost:
        if (r27.isMusic() == false) goto L601;
     */
    /* JADX WARN: Code restructure failed: missing block: B:601:0x1136, code lost:
        r2 = r27.messageOwner.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:602:0x113c, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L604;
     */
    /* JADX WARN: Code restructure failed: missing block: B:603:0x113e, code lost:
        r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:605:0x115d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L610;
     */
    /* JADX WARN: Code restructure failed: missing block: B:606:0x115f, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:607:0x1165, code lost:
        if (r0.quiz == false) goto L609;
     */
    /* JADX WARN: Code restructure failed: missing block: B:608:0x1167, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", org.telegram.messenger.beta.R.string.ChannelMessageQuiz2, r1, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:609:0x117d, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", org.telegram.messenger.beta.R.string.ChannelMessagePoll2, r1, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:611:0x1195, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L649;
     */
    /* JADX WARN: Code restructure failed: missing block: B:613:0x1199, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L614;
     */
    /* JADX WARN: Code restructure failed: missing block: B:615:0x119f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L617;
     */
    /* JADX WARN: Code restructure failed: missing block: B:618:0x11b4, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L643;
     */
    /* JADX WARN: Code restructure failed: missing block: B:620:0x11ba, code lost:
        if (r27.isSticker() != false) goto L639;
     */
    /* JADX WARN: Code restructure failed: missing block: B:622:0x11c0, code lost:
        if (r27.isAnimatedSticker() == false) goto L623;
     */
    /* JADX WARN: Code restructure failed: missing block: B:624:0x11c8, code lost:
        if (r27.isGif() == false) goto L632;
     */
    /* JADX WARN: Code restructure failed: missing block: B:625:0x11ca, code lost:
        if (r28 != false) goto L631;
     */
    /* JADX WARN: Code restructure failed: missing block: B:627:0x11d0, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L631;
     */
    /* JADX WARN: Code restructure failed: missing block: B:629:0x11da, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageOwner.message) != false) goto L631;
     */
    /* JADX WARN: Code restructure failed: missing block: B:630:0x11dc, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.beta.R.string.NotificationMessageText, r1, "🎬 " + r27.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:632:0x1214, code lost:
        if (r28 != false) goto L638;
     */
    /* JADX WARN: Code restructure failed: missing block: B:634:0x121a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L638;
     */
    /* JADX WARN: Code restructure failed: missing block: B:636:0x1224, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageOwner.message) != false) goto L638;
     */
    /* JADX WARN: Code restructure failed: missing block: B:637:0x1226, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.beta.R.string.NotificationMessageText, r1, "📎 " + r27.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:639:0x125e, code lost:
        r0 = r27.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:640:0x1264, code lost:
        if (r0 == null) goto L642;
     */
    /* JADX WARN: Code restructure failed: missing block: B:641:0x1266, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", org.telegram.messenger.beta.R.string.ChannelMessageStickerEmoji, r1, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:642:0x1278, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", org.telegram.messenger.beta.R.string.ChannelMessageSticker, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:644:0x1288, code lost:
        if (r28 != false) goto L648;
     */
    /* JADX WARN: Code restructure failed: missing block: B:646:0x1290, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageText) != false) goto L648;
     */
    /* JADX WARN: Code restructure failed: missing block: B:647:0x1292, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.beta.R.string.NotificationMessageText, r1, r27.messageText);
        r29[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:651:0x12d1, code lost:
        if (r27.isMediaEmpty() == false) goto L657;
     */
    /* JADX WARN: Code restructure failed: missing block: B:652:0x12d3, code lost:
        if (r28 != false) goto L656;
     */
    /* JADX WARN: Code restructure failed: missing block: B:654:0x12dd, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageOwner.message) != false) goto L656;
     */
    /* JADX WARN: Code restructure failed: missing block: B:657:0x130d, code lost:
        r2 = r27.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:658:0x1315, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L666;
     */
    /* JADX WARN: Code restructure failed: missing block: B:659:0x1317, code lost:
        if (r28 != false) goto L665;
     */
    /* JADX WARN: Code restructure failed: missing block: B:661:0x131d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L665;
     */
    /* JADX WARN: Code restructure failed: missing block: B:663:0x1325, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L665;
     */
    /* JADX WARN: Code restructure failed: missing block: B:667:0x1368, code lost:
        if (r27.isVideo() == false) goto L675;
     */
    /* JADX WARN: Code restructure failed: missing block: B:668:0x136a, code lost:
        if (r28 != false) goto L674;
     */
    /* JADX WARN: Code restructure failed: missing block: B:670:0x1370, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L674;
     */
    /* JADX WARN: Code restructure failed: missing block: B:672:0x137a, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageOwner.message) != false) goto L674;
     */
    /* JADX WARN: Code restructure failed: missing block: B:676:0x13c0, code lost:
        if (r27.isVoice() == false) goto L678;
     */
    /* JADX WARN: Code restructure failed: missing block: B:679:0x13d9, code lost:
        if (r27.isRoundVideo() == false) goto L681;
     */
    /* JADX WARN: Code restructure failed: missing block: B:682:0x13f2, code lost:
        if (r27.isMusic() == false) goto L684;
     */
    /* JADX WARN: Code restructure failed: missing block: B:684:0x1407, code lost:
        r2 = r27.messageOwner.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:685:0x140d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L687;
     */
    /* JADX WARN: Code restructure failed: missing block: B:686:0x140f, code lost:
        r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:688:0x1434, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L693;
     */
    /* JADX WARN: Code restructure failed: missing block: B:689:0x1436, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:690:0x143c, code lost:
        if (r0.quiz == false) goto L692;
     */
    /* JADX WARN: Code restructure failed: missing block: B:691:0x143e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", org.telegram.messenger.beta.R.string.NotificationMessageGroupQuiz2, r1, r4.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:692:0x1459, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", org.telegram.messenger.beta.R.string.NotificationMessageGroupPoll2, r1, r4.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:694:0x1476, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L696;
     */
    /* JADX WARN: Code restructure failed: missing block: B:697:0x1497, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L734;
     */
    /* JADX WARN: Code restructure failed: missing block: B:699:0x149b, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L700;
     */
    /* JADX WARN: Code restructure failed: missing block: B:701:0x14a1, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L703;
     */
    /* JADX WARN: Code restructure failed: missing block: B:704:0x14bb, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L729;
     */
    /* JADX WARN: Code restructure failed: missing block: B:706:0x14c1, code lost:
        if (r27.isSticker() != false) goto L725;
     */
    /* JADX WARN: Code restructure failed: missing block: B:708:0x14c7, code lost:
        if (r27.isAnimatedSticker() == false) goto L709;
     */
    /* JADX WARN: Code restructure failed: missing block: B:710:0x14cf, code lost:
        if (r27.isGif() == false) goto L718;
     */
    /* JADX WARN: Code restructure failed: missing block: B:711:0x14d1, code lost:
        if (r28 != false) goto L717;
     */
    /* JADX WARN: Code restructure failed: missing block: B:713:0x14d7, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L717;
     */
    /* JADX WARN: Code restructure failed: missing block: B:715:0x14e1, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageOwner.message) != false) goto L717;
     */
    /* JADX WARN: Code restructure failed: missing block: B:718:0x1520, code lost:
        if (r28 != false) goto L724;
     */
    /* JADX WARN: Code restructure failed: missing block: B:720:0x1526, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L724;
     */
    /* JADX WARN: Code restructure failed: missing block: B:722:0x1530, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageOwner.message) != false) goto L724;
     */
    /* JADX WARN: Code restructure failed: missing block: B:725:0x156f, code lost:
        r0 = r27.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:726:0x1575, code lost:
        if (r0 == null) goto L728;
     */
    /* JADX WARN: Code restructure failed: missing block: B:727:0x1577, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", org.telegram.messenger.beta.R.string.NotificationMessageGroupStickerEmoji, r1, r4.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:728:0x158e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", org.telegram.messenger.beta.R.string.NotificationMessageGroupSticker, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:729:0x15a2, code lost:
        if (r28 != false) goto L733;
     */
    /* JADX WARN: Code restructure failed: missing block: B:731:0x15aa, code lost:
        if (android.text.TextUtils.isEmpty(r27.messageText) != false) goto L733;
     */
    /* JADX WARN: Code restructure failed: missing block: B:788:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", org.telegram.messenger.beta.R.string.NotificationGroupCreatedCall, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:789:?, code lost:
        return r27.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:790:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", org.telegram.messenger.beta.R.string.NotificationInvitedToGroupByLink, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:791:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", org.telegram.messenger.beta.R.string.NotificationEditedGroupName, r1, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:792:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", org.telegram.messenger.beta.R.string.NotificationGroupKickYou, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:793:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", org.telegram.messenger.beta.R.string.NotificationGroupLeftMember, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:794:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", org.telegram.messenger.beta.R.string.NotificationGroupKickMember, r1, r4.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:795:?, code lost:
        return r27.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:796:?, code lost:
        return r27.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:797:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.beta.R.string.ActionMigrateFromGroupNotify, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:798:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.beta.R.string.ActionMigrateFromGroupNotify, r6.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:799:?, code lost:
        return r27.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:800:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoTextChannel, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:801:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.beta.R.string.NotificationActionPinnedNoText, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:802:?, code lost:
        return r27.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:803:?, code lost:
        return r27.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:804:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", org.telegram.messenger.beta.R.string.ChannelVideoEditNotification, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:805:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", org.telegram.messenger.beta.R.string.ChannelPhotoEditNotification, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:806:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", org.telegram.messenger.beta.R.string.NotificationEditedGroupVideo, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:807:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", org.telegram.messenger.beta.R.string.NotificationEditedGroupPhoto, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:808:?, code lost:
        return r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:809:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", org.telegram.messenger.beta.R.string.ChannelMessageNoText, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:810:?, code lost:
        return r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:811:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", org.telegram.messenger.beta.R.string.ChannelMessagePhoto, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:812:?, code lost:
        return r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:813:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", org.telegram.messenger.beta.R.string.ChannelMessageVideo, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:814:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", org.telegram.messenger.beta.R.string.ChannelMessageAudio, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:815:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", org.telegram.messenger.beta.R.string.ChannelMessageRound, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:816:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageMusic", org.telegram.messenger.beta.R.string.ChannelMessageMusic, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:817:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", org.telegram.messenger.beta.R.string.ChannelMessageContact2, r1, org.telegram.messenger.ContactsController.formatName(r2.first_name, r2.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:818:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", org.telegram.messenger.beta.R.string.ChannelMessageLiveLocation, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:819:?, code lost:
        return r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:820:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", org.telegram.messenger.beta.R.string.ChannelMessageGIF, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:821:?, code lost:
        return r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:822:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", org.telegram.messenger.beta.R.string.ChannelMessageDocument, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:823:?, code lost:
        return r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:824:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", org.telegram.messenger.beta.R.string.ChannelMessageNoText, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:825:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", org.telegram.messenger.beta.R.string.ChannelMessageMap, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:826:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.beta.R.string.NotificationMessageGroupText, r1, r4.title, r27.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:827:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", org.telegram.messenger.beta.R.string.NotificationMessageGroupNoText, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:828:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.beta.R.string.NotificationMessageGroupText, r1, r4.title, "🖼 " + r27.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:829:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", org.telegram.messenger.beta.R.string.NotificationMessageGroupPhoto, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:830:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.beta.R.string.NotificationMessageGroupText, r1, r4.title, "📹 " + r27.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:831:?, code lost:
        return org.telegram.messenger.LocaleController.formatString(" ", org.telegram.messenger.beta.R.string.NotificationMessageGroupVideo, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:832:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", org.telegram.messenger.beta.R.string.NotificationMessageGroupAudio, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:833:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", org.telegram.messenger.beta.R.string.NotificationMessageGroupRound, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:834:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMusic", org.telegram.messenger.beta.R.string.NotificationMessageGroupMusic, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:835:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", org.telegram.messenger.beta.R.string.NotificationMessageGroupContact2, r1, r4.title, org.telegram.messenger.ContactsController.formatName(r2.first_name, r2.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:836:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", org.telegram.messenger.beta.R.string.NotificationMessageGroupGame, r1, r4.title, r2.game.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:837:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", org.telegram.messenger.beta.R.string.NotificationMessageGroupLiveLocation, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:838:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.beta.R.string.NotificationMessageGroupText, r1, r4.title, "🎬 " + r27.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:839:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", org.telegram.messenger.beta.R.string.NotificationMessageGroupGif, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:840:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.beta.R.string.NotificationMessageGroupText, r1, r4.title, "📎 " + r27.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:841:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", org.telegram.messenger.beta.R.string.NotificationMessageGroupDocument, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:842:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.beta.R.string.NotificationMessageGroupText, r1, r4.title, r27.messageText);
     */
    /* JADX WARN: Code restructure failed: missing block: B:843:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", org.telegram.messenger.beta.R.string.NotificationMessageGroupNoText, r1, r4.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:844:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", org.telegram.messenger.beta.R.string.NotificationMessageGroupMap, r1, r4.title);
     */
    /* JADX WARN: Removed duplicated region for block: B:246:0x060b  */
    /* JADX WARN: Removed duplicated region for block: B:737:0x15ee  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x014b A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x014c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r27, boolean r28, boolean[] r29, boolean[] r30) {
        /*
            Method dump skipped, instructions count: 5672
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getStringForMessage(org.telegram.messenger.MessageObject, boolean, boolean[], boolean[]):java.lang.String");
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            int i = getAccountInstance().getNotificationsSettings().getInt("repeat_messages", 60);
            if (i > 0 && this.personalCount > 0) {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + (i * 60 * 1000), service);
            } else {
                this.alarmManager.cancel(service);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        TLRPC$MessageAction tLRPC$MessageAction;
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        TLRPC$Peer tLRPC$Peer = tLRPC$Message.peer_id;
        return tLRPC$Peer != null && tLRPC$Peer.chat_id == 0 && tLRPC$Peer.channel_id == 0 && ((tLRPC$MessageAction = tLRPC$Message.action) == null || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionEmpty));
    }

    private int getNotifyOverride(SharedPreferences sharedPreferences, long j) {
        int i = sharedPreferences.getInt("notify2_" + j, -1);
        if (i == 3) {
            if (sharedPreferences.getInt("notifyuntil_" + j, 0) < getConnectionsManager().getCurrentTime()) {
                return i;
            }
            return 2;
        }
        return i;
    }

    public /* synthetic */ void lambda$showNotifications$25() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$showNotifications$25();
            }
        });
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$hideNotifications$26();
            }
        });
    }

    public /* synthetic */ void lambda$hideNotifications$26() {
        notificationManager.cancel(this.notificationId);
        this.lastWearNotifiedMessageId.clear();
        for (int i = 0; i < this.wearNotificationsIds.size(); i++) {
            notificationManager.cancel(this.wearNotificationsIds.valueAt(i).intValue());
        }
        this.wearNotificationsIds.clear();
    }

    private void dismissNotification() {
        try {
            notificationManager.cancel(this.notificationId);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            this.lastWearNotifiedMessageId.clear();
            for (int i = 0; i < this.wearNotificationsIds.size(); i++) {
                if (!this.openedInBubbleDialogs.contains(Long.valueOf(this.wearNotificationsIds.keyAt(i)))) {
                    notificationManager.cancel(this.wearNotificationsIds.valueAt(i).intValue());
                }
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(NotificationsController$$ExternalSyntheticLambda38.INSTANCE);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void lambda$dismissNotification$27() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
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
            if (getNotifyOverride(getAccountInstance().getNotificationsSettings(), this.openedDialogId) == 2) {
                return;
            }
            notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$playInChatSound$29();
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public /* synthetic */ void lambda$playInChatSound$29() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundPlay) <= 500) {
            return;
        }
        try {
            if (this.soundPool == null) {
                SoundPool soundPool = new SoundPool(3, 1, 0);
                this.soundPool = soundPool;
                soundPool.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda3.INSTANCE);
            }
            if (this.soundIn == 0 && !this.soundInLoaded) {
                this.soundInLoaded = true;
                this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, org.telegram.messenger.beta.R.raw.sound_in, 1);
            }
            int i = this.soundIn;
            if (i == 0) {
                return;
            }
            try {
                this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public static /* synthetic */ void lambda$playInChatSound$28(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void scheduleNotificationDelay(boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay notification start, onlineReason = " + z);
            }
            this.notificationDelayWakelock.acquire(10000L);
            notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
            notificationsQueue.postRunnable(this.notificationDelayRunnable, z ? 3000 : 1000);
        } catch (Exception e) {
            FileLog.e(e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$repeatNotificationMaybe$30();
            }
        });
    }

    public /* synthetic */ void lambda$repeatNotificationMaybe$30() {
        int i = Calendar.getInstance().get(11);
        if (i >= 11 && i <= 22) {
            notificationManager.cancel(this.notificationId);
            showOrUpdateNotification(true);
            return;
        }
        scheduleNotificationRepeat();
    }

    private boolean isEmptyVibration(long[] jArr) {
        if (jArr == null || jArr.length == 0) {
            return false;
        }
        for (long j : jArr) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    public void deleteNotificationChannel(long j) {
        deleteNotificationChannel(j, -1);
    }

    /* renamed from: deleteNotificationChannelInternal */
    public void lambda$deleteNotificationChannel$31(long j, int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            if (i == 0 || i == -1) {
                String str = "org.telegram.key" + j;
                String string = notificationsSettings.getString(str, null);
                if (string != null) {
                    edit.remove(str).remove(str + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel internal " + string);
                    }
                }
            }
            if (i == 1 || i == -1) {
                String str2 = "org.telegram.keyia" + j;
                String string2 = notificationsSettings.getString(str2, null);
                if (string2 != null) {
                    edit.remove(str2).remove(str2 + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string2);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel internal " + string2);
                    }
                }
            }
            edit.commit();
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public void deleteNotificationChannel(final long j, final int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannel$31(j, i);
            }
        });
    }

    public void deleteNotificationChannelGlobal(int i) {
        deleteNotificationChannelGlobal(i, -1);
    }

    /* renamed from: deleteNotificationChannelGlobalInternal */
    public void lambda$deleteNotificationChannelGlobal$32(int i, int i2) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            if (i2 == 0 || i2 == -1) {
                String str = i == 2 ? "channels" : i == 0 ? "groups" : "private";
                String string = notificationsSettings.getString(str, null);
                if (string != null) {
                    SharedPreferences.Editor remove = edit.remove(str);
                    remove.remove(str + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel global internal " + string);
                    }
                }
            }
            if (i2 == 1 || i2 == -1) {
                String str2 = i == 2 ? "channels_ia" : i == 0 ? "groups_ia" : "private_ia";
                String string2 = notificationsSettings.getString(str2, null);
                if (string2 != null) {
                    SharedPreferences.Editor remove2 = edit.remove(str2);
                    remove2.remove(str2 + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string2);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel global internal " + string2);
                    }
                }
            }
            edit.remove(i == 2 ? "overwrite_channel" : i == 0 ? "overwrite_group" : "overwrite_private");
            edit.commit();
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public void deleteNotificationChannelGlobal(final int i, final int i2) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannelGlobal$32(i, i2);
            }
        });
    }

    public void deleteAllNotificationChannels() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$deleteAllNotificationChannels$33();
            }
        });
    }

    public /* synthetic */ void lambda$deleteAllNotificationChannels$33() {
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            Map<String, ?> all = notificationsSettings.getAll();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("org.telegram.key")) {
                    if (!key.endsWith("_s")) {
                        String str = (String) entry.getValue();
                        systemNotificationManager.deleteNotificationChannel(str);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete all channel " + str);
                        }
                    }
                    edit.remove(key);
                }
            }
            edit.commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean unsupportedNotificationShortcut() {
        return Build.VERSION.SDK_INT < 29 || !SharedConfig.chatBubbles;
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x00ef A[Catch: Exception -> 0x0150, TryCatch #0 {Exception -> 0x0150, blocks: (B:8:0x0020, B:11:0x0060, B:12:0x0064, B:13:0x0068, B:16:0x0074, B:17:0x0078, B:19:0x00a1, B:21:0x00b1, B:23:0x00bb, B:25:0x00ef, B:26:0x00f3, B:27:0x00f7, B:29:0x0100, B:31:0x0107, B:35:0x0114, B:36:0x0119, B:37:0x0122, B:41:0x0139), top: B:47:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00f3 A[Catch: Exception -> 0x0150, TryCatch #0 {Exception -> 0x0150, blocks: (B:8:0x0020, B:11:0x0060, B:12:0x0064, B:13:0x0068, B:16:0x0074, B:17:0x0078, B:19:0x00a1, B:21:0x00b1, B:23:0x00bb, B:25:0x00ef, B:26:0x00f3, B:27:0x00f7, B:29:0x0100, B:31:0x0107, B:35:0x0114, B:36:0x0119, B:37:0x0122, B:41:0x0139), top: B:47:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0100 A[Catch: Exception -> 0x0150, TryCatch #0 {Exception -> 0x0150, blocks: (B:8:0x0020, B:11:0x0060, B:12:0x0064, B:13:0x0068, B:16:0x0074, B:17:0x0078, B:19:0x00a1, B:21:0x00b1, B:23:0x00bb, B:25:0x00ef, B:26:0x00f3, B:27:0x00f7, B:29:0x0100, B:31:0x0107, B:35:0x0114, B:36:0x0119, B:37:0x0122, B:41:0x0139), top: B:47:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0105  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0136  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0138  */
    @android.annotation.SuppressLint({"RestrictedApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String createNotificationShortcut(androidx.core.app.NotificationCompat.Builder r18, long r19, java.lang.String r21, org.telegram.tgnet.TLRPC$User r22, org.telegram.tgnet.TLRPC$Chat r23, androidx.core.app.Person r24) {
        /*
            Method dump skipped, instructions count: 344
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.createNotificationShortcut(androidx.core.app.NotificationCompat$Builder, long, java.lang.String, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, androidx.core.app.Person):java.lang.String");
    }

    @TargetApi(AvailableCode.ERROR_NO_ACTIVITY)
    protected void ensureGroupsCreated() {
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        if (this.groupsCreated == null) {
            this.groupsCreated = Boolean.valueOf(notificationsSettings.getBoolean("groupsCreated4", false));
        }
        if (!this.groupsCreated.booleanValue()) {
            try {
                String str = this.currentAccount + "channel";
                List<NotificationChannel> notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                SharedPreferences.Editor editor = null;
                for (int i = 0; i < size; i++) {
                    NotificationChannel notificationChannel = notificationChannels.get(i);
                    String id = notificationChannel.getId();
                    if (id.startsWith(str)) {
                        int importance = notificationChannel.getImportance();
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
                                long longValue = Utilities.parseLong(id.substring(9, id.indexOf(95, 9))).longValue();
                                if (longValue != 0) {
                                    if (editor == null) {
                                        editor = getAccountInstance().getNotificationsSettings().edit();
                                    }
                                    editor.remove("priority_" + longValue).remove("vibrate_" + longValue).remove("sound_path_" + longValue).remove("sound_" + longValue);
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
            notificationsSettings.edit().putBoolean("groupsCreated4", true).commit();
            this.groupsCreated = Boolean.TRUE;
        }
        if (!this.channelGroupsCreated) {
            List<NotificationChannelGroup> notificationChannelGroups = systemNotificationManager.getNotificationChannelGroups();
            String str2 = "channels" + this.currentAccount;
            String str3 = "groups" + this.currentAccount;
            int size2 = notificationChannelGroups.size();
            String str4 = "other" + this.currentAccount;
            String str5 = "private" + this.currentAccount;
            for (int i2 = 0; i2 < size2; i2++) {
                String id2 = notificationChannelGroups.get(i2).getId();
                if (str2 != null && str2.equals(id2)) {
                    str2 = null;
                } else if (str3 != null && str3.equals(id2)) {
                    str3 = null;
                } else if (str5 != null && str5.equals(id2)) {
                    str5 = null;
                } else if (str4 != null && str4.equals(id2)) {
                    str4 = null;
                }
                if (str2 == null && str3 == null && str5 == null && str4 == null) {
                    break;
                }
            }
            if (str2 != null || str3 != null || str5 != null || str4 != null) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
                if (user == null) {
                    getUserConfig().getCurrentUser();
                }
                String str6 = user != null ? " (" + ContactsController.formatName(user.first_name, user.last_name) + ")" : "";
                ArrayList arrayList = new ArrayList();
                if (str2 != null) {
                    arrayList.add(new NotificationChannelGroup(str2, LocaleController.getString("NotificationsChannels", org.telegram.messenger.beta.R.string.NotificationsChannels) + str6));
                }
                if (str3 != null) {
                    arrayList.add(new NotificationChannelGroup(str3, LocaleController.getString("NotificationsGroups", org.telegram.messenger.beta.R.string.NotificationsGroups) + str6));
                }
                if (str5 != null) {
                    arrayList.add(new NotificationChannelGroup(str5, LocaleController.getString("NotificationsPrivateChats", org.telegram.messenger.beta.R.string.NotificationsPrivateChats) + str6));
                }
                if (str4 != null) {
                    arrayList.add(new NotificationChannelGroup(str4, LocaleController.getString("NotificationsOther", org.telegram.messenger.beta.R.string.NotificationsOther) + str6));
                }
                systemNotificationManager.createNotificationChannelGroups(arrayList);
            }
            this.channelGroupsCreated = true;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:189:0x0402 A[LOOP:1: B:187:0x03ff->B:189:0x0402, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:192:0x0417  */
    /* JADX WARN: Removed duplicated region for block: B:212:0x0465  */
    @android.annotation.TargetApi(com.huawei.hms.adapter.internal.AvailableCode.ERROR_NO_ACTIVITY)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String validateChannelId(long r27, java.lang.String r29, long[] r30, int r31, android.net.Uri r32, int r33, boolean r34, boolean r35, boolean r36, int r37) {
        /*
            Method dump skipped, instructions count: 1364
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARN: Code restructure failed: missing block: B:364:0x0883, code lost:
        if (android.os.Build.VERSION.SDK_INT < 26) goto L366;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0133, code lost:
        if (r11 == 0) goto L79;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0135, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("NotificationHiddenChatName", org.telegram.messenger.beta.R.string.NotificationHiddenChatName);
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x013f, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("NotificationHiddenName", org.telegram.messenger.beta.R.string.NotificationHiddenName);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:100:0x01b7 A[Catch: Exception -> 0x0b0c, TRY_ENTER, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:101:0x01d2 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:105:0x0223 A[Catch: Exception -> 0x0b0c, TRY_ENTER, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:117:0x0299 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:141:0x0355 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:150:0x036c  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x042f A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0453  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0456  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x046f A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:190:0x0515  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x0523  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x05a6  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x0603  */
    /* JADX WARN: Removed duplicated region for block: B:216:0x0607  */
    /* JADX WARN: Removed duplicated region for block: B:219:0x060f A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:225:0x061e  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x0624  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x0629 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:235:0x0636  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x0641 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:254:0x0665  */
    /* JADX WARN: Removed duplicated region for block: B:265:0x067c  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x0681  */
    /* JADX WARN: Removed duplicated region for block: B:269:0x06b8 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:307:0x072a A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:315:0x07a4 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:323:0x07eb A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:336:0x0839  */
    /* JADX WARN: Removed duplicated region for block: B:339:0x0841  */
    /* JADX WARN: Removed duplicated region for block: B:405:0x0961 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:406:0x096b  */
    /* JADX WARN: Removed duplicated region for block: B:409:0x0972 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:410:0x0980  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0119 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x012b  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x0160 A[Catch: Exception -> 0x0b0c, TRY_ENTER, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01a0 A[Catch: Exception -> 0x0b0c, TryCatch #1 {Exception -> 0x0b0c, blocks: (B:10:0x0022, B:12:0x0046, B:14:0x004a, B:16:0x0054, B:18:0x005a, B:21:0x006a, B:22:0x006c, B:26:0x007a, B:28:0x0086, B:29:0x008c, B:31:0x009e, B:33:0x00ac, B:35:0x00b2, B:36:0x00b5, B:38:0x00bb, B:44:0x00c9, B:46:0x00d9, B:54:0x00f1, B:56:0x00f7, B:58:0x00fc, B:59:0x00ff, B:60:0x0103, B:62:0x010b, B:67:0x0113, B:69:0x0119, B:78:0x0135, B:79:0x013f, B:80:0x0149, B:82:0x0153, B:85:0x0160, B:87:0x0168, B:88:0x0175, B:90:0x0195, B:92:0x01a0, B:97:0x01ac, B:100:0x01b7, B:101:0x01d2, B:102:0x020d, B:105:0x0223, B:110:0x0240, B:111:0x0254, B:113:0x0259, B:114:0x026d, B:116:0x0282, B:117:0x0299, B:119:0x02bd, B:121:0x02d5, B:126:0x02df, B:127:0x02e5, B:131:0x02f2, B:132:0x0306, B:134:0x030b, B:135:0x031f, B:136:0x0332, B:138:0x033a, B:139:0x0343, B:142:0x0357, B:153:0x0372, B:155:0x038c, B:158:0x03c3, B:160:0x03cd, B:161:0x03e7, B:163:0x03fc, B:164:0x0408, B:166:0x040c, B:172:0x042f, B:175:0x0449, B:179:0x0458, B:181:0x046f, B:183:0x04b7, B:184:0x04c3, B:185:0x04da, B:187:0x04f1, B:194:0x0525, B:196:0x0535, B:197:0x0541, B:198:0x0548, B:199:0x0565, B:201:0x0577, B:202:0x0583, B:203:0x058a, B:206:0x05b1, B:208:0x05bb, B:209:0x05c7, B:210:0x05ce, B:217:0x0609, B:219:0x060f, B:231:0x0629, B:233:0x062f, B:241:0x0641, B:244:0x064b, B:247:0x0654, B:263:0x0677, B:267:0x0682, B:269:0x06b8, B:273:0x06c7, B:276:0x06d3, B:277:0x06da, B:279:0x06e0, B:282:0x06e5, B:284:0x06ee, B:287:0x06f6, B:289:0x06fa, B:291:0x06fe, B:293:0x0706, B:297:0x0710, B:299:0x0716, B:301:0x071a, B:303:0x0722, B:307:0x072a, B:309:0x0735, B:311:0x073b, B:313:0x0745, B:316:0x07a6, B:318:0x07aa, B:320:0x07b0, B:321:0x07c6, B:323:0x07eb, B:325:0x07f8, B:343:0x0848, B:353:0x085c, B:357:0x0869, B:360:0x0872, B:363:0x087d, B:370:0x0890, B:372:0x0898, B:374:0x08a0, B:376:0x08c8, B:378:0x08cd, B:380:0x08d5, B:382:0x08d9, B:384:0x08e1, B:388:0x08ec, B:389:0x0902, B:390:0x0907, B:391:0x090a, B:393:0x0912, B:396:0x091c, B:398:0x0924, B:401:0x094f, B:402:0x0957, B:405:0x0961, B:409:0x0972, B:412:0x0983, B:419:0x09a4, B:422:0x09b8, B:424:0x09c1, B:425:0x09d3, B:427:0x09d9, B:429:0x09dd, B:431:0x09e8, B:433:0x09ee, B:435:0x09f8, B:437:0x0a07, B:439:0x0a17, B:441:0x0a36, B:442:0x0a3b, B:444:0x0a67, B:445:0x0a78, B:449:0x0a9b, B:451:0x0aa1, B:453:0x0aa9, B:455:0x0aaf, B:457:0x0ac1, B:458:0x0ada, B:459:0x0af2, B:400:0x092e, B:256:0x0668), top: B:467:0x0022, inners: #0, #3 }] */
    /* JADX WARN: Type inference failed for: r1v51 */
    /* JADX WARN: Type inference failed for: r6v86 */
    /* JADX WARN: Type inference failed for: r6v87 */
    /* JADX WARN: Type inference failed for: r6v88 */
    /* JADX WARN: Type inference failed for: r6v89 */
    /* JADX WARN: Type inference failed for: r6v94 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showOrUpdateNotification(boolean r48) {
        /*
            Method dump skipped, instructions count: 2838
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    private boolean isSilentMessage(MessageObject messageObject) {
        return messageObject.messageOwner.silent || messageObject.isReactionPush;
    }

    @SuppressLint({"NewApi"})
    private void setNotificationChannel(Notification notification, NotificationCompat.Builder builder, boolean z) {
        if (z) {
            builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
        } else {
            builder.setChannelId(notification.getChannelId());
        }
    }

    public void resetNotificationSound(NotificationCompat.Builder builder, long j, String str, long[] jArr, int i, Uri uri, int i2, boolean z, boolean z2, boolean z3, int i3) {
        Uri uri2 = Settings.System.DEFAULT_RINGTONE_URI;
        if (uri2 == null || uri == null || TextUtils.equals(uri2.toString(), uri.toString())) {
            return;
        }
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        String uri3 = uri2.toString();
        String string = LocaleController.getString("DefaultRingtone", org.telegram.messenger.beta.R.string.DefaultRingtone);
        if (z) {
            if (i3 == 2) {
                edit.putString("ChannelSound", string);
            } else if (i3 == 0) {
                edit.putString("GroupSound", string);
            } else {
                edit.putString("GlobalSound", string);
            }
            if (i3 == 2) {
                edit.putString("ChannelSoundPath", uri3);
            } else if (i3 == 0) {
                edit.putString("GroupSoundPath", uri3);
            } else {
                edit.putString("GlobalSoundPath", uri3);
            }
            getNotificationsController().lambda$deleteNotificationChannelGlobal$32(i3, -1);
        } else {
            edit.putString("sound_" + j, string);
            edit.putString("sound_path_" + j, uri3);
            lambda$deleteNotificationChannel$31(j, -1);
        }
        edit.commit();
        builder.setChannelId(validateChannelId(j, str, jArr, i, Settings.System.DEFAULT_RINGTONE_URI, i2, z, z2, z3, i3));
        notificationManager.notify(this.notificationId, builder.build());
    }

    /* JADX WARN: Removed duplicated region for block: B:135:0x0347  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x0365  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x036c  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x03c4  */
    /* JADX WARN: Removed duplicated region for block: B:160:0x03cf  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x03f5  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x0400 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:181:0x0456  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x0468  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x04ac  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x04c1  */
    /* JADX WARN: Removed duplicated region for block: B:196:0x04ea A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:199:0x04fa  */
    /* JADX WARN: Removed duplicated region for block: B:222:0x0565 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:227:0x0579  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x058e A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:245:0x05bd  */
    /* JADX WARN: Removed duplicated region for block: B:255:0x05f7  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x0633  */
    /* JADX WARN: Removed duplicated region for block: B:284:0x06ad  */
    /* JADX WARN: Removed duplicated region for block: B:300:0x070a  */
    /* JADX WARN: Removed duplicated region for block: B:304:0x071f  */
    /* JADX WARN: Removed duplicated region for block: B:310:0x0740  */
    /* JADX WARN: Removed duplicated region for block: B:340:0x07c2  */
    /* JADX WARN: Removed duplicated region for block: B:373:0x08ae  */
    /* JADX WARN: Removed duplicated region for block: B:381:0x08cf  */
    /* JADX WARN: Removed duplicated region for block: B:388:0x0902  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x0912  */
    /* JADX WARN: Removed duplicated region for block: B:397:0x0975  */
    /* JADX WARN: Removed duplicated region for block: B:398:0x097f  */
    /* JADX WARN: Removed duplicated region for block: B:404:0x09aa  */
    /* JADX WARN: Removed duplicated region for block: B:407:0x0a04  */
    /* JADX WARN: Removed duplicated region for block: B:411:0x0a3b  */
    /* JADX WARN: Removed duplicated region for block: B:416:0x0a60  */
    /* JADX WARN: Removed duplicated region for block: B:417:0x0a82  */
    /* JADX WARN: Removed duplicated region for block: B:420:0x0b34  */
    /* JADX WARN: Removed duplicated region for block: B:422:0x0b3f  */
    /* JADX WARN: Removed duplicated region for block: B:424:0x0b44  */
    /* JADX WARN: Removed duplicated region for block: B:427:0x0b4e  */
    /* JADX WARN: Removed duplicated region for block: B:433:0x0b62  */
    /* JADX WARN: Removed duplicated region for block: B:435:0x0b67  */
    /* JADX WARN: Removed duplicated region for block: B:438:0x0b73  */
    /* JADX WARN: Removed duplicated region for block: B:444:0x0b82  */
    /* JADX WARN: Removed duplicated region for block: B:457:0x0c09 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:466:0x0c3a  */
    /* JADX WARN: Removed duplicated region for block: B:520:0x0504 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:536:0x0922 A[ADDED_TO_REGION, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0206  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0210  */
    @android.annotation.SuppressLint({"InlinedApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r73, java.lang.String r74, long r75, java.lang.String r77, long[] r78, int r79, android.net.Uri r80, int r81, boolean r82, boolean r83, boolean r84, int r85) {
        /*
            Method dump skipped, instructions count: 3523
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, java.lang.String, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):void");
    }

    /* renamed from: org.telegram.messenger.NotificationsController$1NotificationHolder */
    /* loaded from: classes.dex */
    public class C1NotificationHolder {
        TLRPC$Chat chat;
        long dialogId;
        int id;
        String name;
        NotificationCompat.Builder notification;
        TLRPC$User user;
        final /* synthetic */ String val$chatName;
        final /* synthetic */ int val$chatType;
        final /* synthetic */ int val$importance;
        final /* synthetic */ boolean val$isDefault;
        final /* synthetic */ boolean val$isInApp;
        final /* synthetic */ boolean val$isSilent;
        final /* synthetic */ int val$ledColor;
        final /* synthetic */ Uri val$sound;
        final /* synthetic */ long[] val$vibrationPattern;

        C1NotificationHolder(int i, long j, String str, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, NotificationCompat.Builder builder, String str2, long[] jArr, int i2, Uri uri, int i3, boolean z, boolean z2, boolean z3, int i4) {
            NotificationsController.this = r4;
            this.val$chatName = str2;
            this.val$vibrationPattern = jArr;
            this.val$ledColor = i2;
            this.val$sound = uri;
            this.val$importance = i3;
            this.val$isDefault = z;
            this.val$isInApp = z2;
            this.val$isSilent = z3;
            this.val$chatType = i4;
            this.id = i;
            this.name = str;
            this.user = tLRPC$User;
            this.chat = tLRPC$Chat;
            this.notification = builder;
            this.dialogId = j;
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

    public static /* synthetic */ void lambda$showExtraNotifications$34(Uri uri) {
        ApplicationLoader.applicationContext.revokeUriPermission(uri, 1);
    }

    public static /* synthetic */ void lambda$loadRoundAvatar$36(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setPostProcessor(NotificationsController$$ExternalSyntheticLambda1.INSTANCE);
    }

    @TargetApi(AvailableCode.APP_IS_BACKGROUND_OR_LOCKED)
    private void loadRoundAvatar(File file, Person.Builder builder) {
        if (file != null) {
            try {
                builder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(file), NotificationsController$$ExternalSyntheticLambda0.INSTANCE)));
            } catch (Throwable unused) {
            }
        }
    }

    public static /* synthetic */ int lambda$loadRoundAvatar$35(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        float f = width / 2;
        path.addRoundRect(0.0f, 0.0f, width, canvas.getHeight(), f, f, Path.Direction.CW);
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
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$playOutChatSound$38();
            }
        });
    }

    public /* synthetic */ void lambda$playOutChatSound$38() {
        try {
            if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundOutPlay) <= 100) {
                return;
            }
            this.lastSoundOutPlay = SystemClock.elapsedRealtime();
            if (this.soundPool == null) {
                SoundPool soundPool = new SoundPool(3, 1, 0);
                this.soundPool = soundPool;
                soundPool.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda2.INSTANCE);
            }
            if (this.soundOut == 0 && !this.soundOutLoaded) {
                this.soundOutLoaded = true;
                this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, org.telegram.messenger.beta.R.raw.sound_out, 1);
            }
            int i = this.soundOut;
            if (i == 0) {
                return;
            }
            try {
                this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public static /* synthetic */ void lambda$playOutChatSound$37(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void clearDialogNotificationsSettings(long j) {
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        SharedPreferences.Editor remove = edit.remove("notify2_" + j);
        remove.remove("custom_" + j);
        getMessagesStorage().setDialogFlags(j, 0L);
        TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j);
        if (tLRPC$Dialog != null) {
            tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
        }
        edit.commit();
        getNotificationsController().updateServerNotificationsSettings(j, true);
    }

    public void setDialogNotificationsSettings(long j, int i) {
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(j);
        if (i == 4) {
            if (isGlobalNotificationsEnabled(j)) {
                edit.remove("notify2_" + j);
            } else {
                edit.putInt("notify2_" + j, 0);
            }
            getMessagesStorage().setDialogFlags(j, 0L);
            if (tLRPC$Dialog != null) {
                tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
            }
        } else {
            int currentTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
            if (i == 0) {
                currentTime += 3600;
            } else if (i == 1) {
                currentTime += 28800;
            } else if (i == 2) {
                currentTime += 172800;
            } else if (i == 3) {
                currentTime = Integer.MAX_VALUE;
            }
            long j2 = 1;
            if (i == 3) {
                edit.putInt("notify2_" + j, 2);
            } else {
                edit.putInt("notify2_" + j, 3);
                edit.putInt("notifyuntil_" + j, currentTime);
                j2 = 1 | (((long) currentTime) << 32);
            }
            getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(j);
            MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(j, j2);
            if (tLRPC$Dialog != null) {
                TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                tLRPC$Dialog.notify_settings = tLRPC$TL_peerNotifySettings;
                tLRPC$TL_peerNotifySettings.mute_until = currentTime;
            }
        }
        edit.commit();
        updateServerNotificationsSettings(j);
    }

    public void updateServerNotificationsSettings(long j) {
        updateServerNotificationsSettings(j, true);
    }

    public void updateServerNotificationsSettings(long j, boolean z) {
        int i = 0;
        if (z) {
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
        if (DialogObject.isEncryptedDialog(j)) {
            return;
        }
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        TLRPC$TL_account_updateNotifySettings tLRPC$TL_account_updateNotifySettings = new TLRPC$TL_account_updateNotifySettings();
        TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings = new TLRPC$TL_inputPeerNotifySettings();
        tLRPC$TL_account_updateNotifySettings.settings = tLRPC$TL_inputPeerNotifySettings;
        tLRPC$TL_inputPeerNotifySettings.flags |= 1;
        tLRPC$TL_inputPeerNotifySettings.show_previews = notificationsSettings.getBoolean("content_preview_" + j, true);
        TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings2 = tLRPC$TL_account_updateNotifySettings.settings;
        tLRPC$TL_inputPeerNotifySettings2.flags = tLRPC$TL_inputPeerNotifySettings2.flags | 2;
        tLRPC$TL_inputPeerNotifySettings2.silent = notificationsSettings.getBoolean("silent_" + j, false);
        int i2 = notificationsSettings.getInt("notify2_" + j, -1);
        if (i2 != -1) {
            TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings3 = tLRPC$TL_account_updateNotifySettings.settings;
            tLRPC$TL_inputPeerNotifySettings3.flags |= 4;
            if (i2 == 3) {
                tLRPC$TL_inputPeerNotifySettings3.mute_until = notificationsSettings.getInt("notifyuntil_" + j, 0);
            } else {
                if (i2 == 2) {
                    i = Integer.MAX_VALUE;
                }
                tLRPC$TL_inputPeerNotifySettings3.mute_until = i;
            }
        }
        long j2 = notificationsSettings.getLong("sound_document_id_" + j, 0L);
        String string = notificationsSettings.getString("sound_path_" + j, null);
        TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings4 = tLRPC$TL_account_updateNotifySettings.settings;
        tLRPC$TL_inputPeerNotifySettings4.flags = tLRPC$TL_inputPeerNotifySettings4.flags | 8;
        if (j2 != 0) {
            TLRPC$TL_notificationSoundRingtone tLRPC$TL_notificationSoundRingtone = new TLRPC$TL_notificationSoundRingtone();
            tLRPC$TL_notificationSoundRingtone.id = j2;
            tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundRingtone;
        } else if (string != null) {
            if (string.equals("NoSound")) {
                tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundNone();
            } else {
                TLRPC$TL_notificationSoundLocal tLRPC$TL_notificationSoundLocal = new TLRPC$TL_notificationSoundLocal();
                tLRPC$TL_notificationSoundLocal.title = notificationsSettings.getString("sound_" + j, null);
                tLRPC$TL_notificationSoundLocal.data = string;
                tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundLocal;
            }
        } else {
            tLRPC$TL_inputPeerNotifySettings4.sound = new TLRPC$TL_notificationSoundDefault();
        }
        TLRPC$TL_inputNotifyPeer tLRPC$TL_inputNotifyPeer = new TLRPC$TL_inputNotifyPeer();
        tLRPC$TL_account_updateNotifySettings.peer = tLRPC$TL_inputNotifyPeer;
        tLRPC$TL_inputNotifyPeer.peer = getMessagesController().getInputPeer(j);
        getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, NotificationsController$$ExternalSyntheticLambda40.INSTANCE);
    }

    public void updateServerNotificationsSettings(int i) {
        String str;
        String str2;
        String str3;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        TLRPC$TL_account_updateNotifySettings tLRPC$TL_account_updateNotifySettings = new TLRPC$TL_account_updateNotifySettings();
        TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings = new TLRPC$TL_inputPeerNotifySettings();
        tLRPC$TL_account_updateNotifySettings.settings = tLRPC$TL_inputPeerNotifySettings;
        tLRPC$TL_inputPeerNotifySettings.flags = 5;
        if (i == 0) {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyChats();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableGroup2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewGroup", true);
            str = "GroupSound";
            str3 = "GroupSoundDocId";
            str2 = "GroupSoundPath";
        } else if (i == 1) {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyUsers();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableAll2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewAll", true);
            str = "GlobalSound";
            str3 = "GlobalSoundDocId";
            str2 = "GlobalSoundPath";
        } else {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyBroadcasts();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableChannel2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewChannel", true);
            str = "ChannelSound";
            str3 = "ChannelSoundDocId";
            str2 = "ChannelSoundPath";
        }
        tLRPC$TL_account_updateNotifySettings.settings.flags |= 8;
        long j = notificationsSettings.getLong(str3, 0L);
        String string = notificationsSettings.getString(str2, "NoSound");
        if (j != 0) {
            TLRPC$TL_notificationSoundRingtone tLRPC$TL_notificationSoundRingtone = new TLRPC$TL_notificationSoundRingtone();
            tLRPC$TL_notificationSoundRingtone.id = j;
            tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundRingtone;
        } else if (string != null) {
            if (string.equals("NoSound")) {
                tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundNone();
            } else {
                TLRPC$TL_notificationSoundLocal tLRPC$TL_notificationSoundLocal = new TLRPC$TL_notificationSoundLocal();
                tLRPC$TL_notificationSoundLocal.title = notificationsSettings.getString(str, null);
                tLRPC$TL_notificationSoundLocal.data = string;
                tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundLocal;
            }
        } else {
            tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundDefault();
        }
        getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, NotificationsController$$ExternalSyntheticLambda39.INSTANCE);
    }

    public boolean isGlobalNotificationsEnabled(long j) {
        return isGlobalNotificationsEnabled(j, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0028, code lost:
        if (r4.megagroup == false) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x000e, code lost:
        if (r6.booleanValue() != false) goto L13;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isGlobalNotificationsEnabled(long r4, java.lang.Boolean r6) {
        /*
            r3 = this;
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            r1 = 2
            r2 = 0
            if (r0 == 0) goto L2b
            if (r6 == 0) goto L13
            boolean r4 = r6.booleanValue()
            if (r4 == 0) goto L11
            goto L2c
        L11:
            r1 = 0
            goto L2c
        L13:
            org.telegram.messenger.MessagesController r6 = r3.getMessagesController()
            long r4 = -r4
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r6.getChat(r4)
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r5 == 0) goto L11
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L11
            goto L2c
        L2b:
            r1 = 1
        L2c:
            boolean r4 = r3.isGlobalNotificationsEnabled(r1)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.isGlobalNotificationsEnabled(long, java.lang.Boolean):boolean");
    }

    public boolean isGlobalNotificationsEnabled(int i) {
        return getAccountInstance().getNotificationsSettings().getInt(getGlobalNotificationsKey(i), 0) < getConnectionsManager().getCurrentTime();
    }

    public void setGlobalNotificationsEnabled(int i, int i2) {
        getAccountInstance().getNotificationsSettings().edit().putInt(getGlobalNotificationsKey(i), i2).commit();
        updateServerNotificationsSettings(i);
        getMessagesStorage().updateMutedDialogsFiltersCounters();
        deleteNotificationChannelGlobal(i);
    }

    public void muteDialog(long j, boolean z) {
        if (z) {
            getInstance(this.currentAccount).muteUntil(j, Integer.MAX_VALUE);
            return;
        }
        boolean isGlobalNotificationsEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(j);
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        if (isGlobalNotificationsEnabled) {
            edit.remove("notify2_" + j);
        } else {
            edit.putInt("notify2_" + j, 0);
        }
        getMessagesStorage().setDialogFlags(j, 0L);
        edit.apply();
        TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j);
        if (tLRPC$Dialog != null) {
            tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
        }
        updateServerNotificationsSettings(j);
    }
}
