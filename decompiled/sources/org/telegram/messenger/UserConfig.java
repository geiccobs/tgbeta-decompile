package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Base64;
import java.util.Arrays;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes.dex */
public class UserConfig extends BaseController {
    private static volatile UserConfig[] Instance = new UserConfig[4];
    public static final int MAX_ACCOUNT_COUNT = 4;
    public static final int MAX_ACCOUNT_DEFAULT_COUNT = 3;
    public static final int i_dialogsLoadOffsetAccess = 5;
    public static final int i_dialogsLoadOffsetChannelId = 4;
    public static final int i_dialogsLoadOffsetChatId = 3;
    public static final int i_dialogsLoadOffsetDate = 1;
    public static final int i_dialogsLoadOffsetId = 0;
    public static final int i_dialogsLoadOffsetUserId = 2;
    public static int selectedAccount;
    public long autoDownloadConfigLoadTime;
    public int botRatingLoadTime;
    public long clientUserId;
    private boolean configLoaded;
    public boolean contactsReimported;
    public int contactsSavedCount;
    private TLRPC.User currentUser;
    public boolean draftsLoaded;
    public boolean filtersLoaded;
    public boolean hasSecureData;
    public boolean hasValidDialogLoadIds;
    public int lastContactsSyncTime;
    public int lastHintsSyncTime;
    public int lastMyLocationShareTime;
    public int loginTime;
    public boolean notificationsSettingsLoaded;
    public boolean notificationsSignUpSettingsLoaded;
    public int ratingLoadTime;
    public boolean registeredForPush;
    public volatile byte[] savedPasswordHash;
    public volatile long savedPasswordTime;
    public volatile byte[] savedSaltedPassword;
    public int sharingMyLocationUntil;
    public TLRPC.TL_account_tmpPassword tmpPassword;
    public TLRPC.TL_help_termsOfService unacceptedTermsOfService;
    private final Object sync = new Object();
    public int lastSendMessageId = -210000;
    public int lastBroadcastId = -1;
    public boolean unreadDialogsLoaded = true;
    public int migrateOffsetId = -1;
    public int migrateOffsetDate = -1;
    public long migrateOffsetUserId = -1;
    public long migrateOffsetChatId = -1;
    public long migrateOffsetChannelId = -1;
    public long migrateOffsetAccess = -1;
    public boolean syncContacts = true;
    public boolean suggestContacts = true;

    public static UserConfig getInstance(int num) {
        UserConfig localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (UserConfig.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    UserConfig[] userConfigArr = Instance;
                    UserConfig userConfig = new UserConfig(num);
                    localInstance = userConfig;
                    userConfigArr[num] = userConfig;
                }
            }
        }
        return localInstance;
    }

    public static int getActivatedAccountsCount() {
        int count = 0;
        for (int a = 0; a < 4; a++) {
            if (AccountInstance.getInstance(a).getUserConfig().isClientActivated()) {
                count++;
            }
        }
        return count;
    }

    public UserConfig(int instance) {
        super(instance);
    }

    public static boolean hasPremiumOnAccounts() {
        for (int a = 0; a < 4; a++) {
            if (AccountInstance.getInstance(a).getUserConfig().isClientActivated() && AccountInstance.getInstance(a).getUserConfig().getUserConfig().isPremium()) {
                return true;
            }
        }
        return false;
    }

    public static int getMaxAccountCount() {
        return hasPremiumOnAccounts() ? 5 : 3;
    }

    public int getNewMessageId() {
        int id;
        synchronized (this.sync) {
            id = this.lastSendMessageId;
            this.lastSendMessageId = id - 1;
        }
        return id;
    }

    public void saveConfig(final boolean withFile) {
        NotificationCenter.getInstance(this.currentAccount).doOnIdle(new Runnable() { // from class: org.telegram.messenger.UserConfig$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                UserConfig.this.m1231lambda$saveConfig$0$orgtelegrammessengerUserConfig(withFile);
            }
        });
    }

    /* renamed from: lambda$saveConfig$0$org-telegram-messenger-UserConfig */
    public /* synthetic */ void m1231lambda$saveConfig$0$orgtelegrammessengerUserConfig(boolean withFile) {
        synchronized (this.sync) {
            try {
                SharedPreferences.Editor editor = getPreferences().edit();
                if (this.currentAccount == 0) {
                    editor.putInt("selectedAccount", selectedAccount);
                }
                editor.putBoolean("registeredForPush", this.registeredForPush);
                editor.putInt("lastSendMessageId", this.lastSendMessageId);
                editor.putInt("contactsSavedCount", this.contactsSavedCount);
                editor.putInt("lastBroadcastId", this.lastBroadcastId);
                editor.putInt("lastContactsSyncTime", this.lastContactsSyncTime);
                editor.putInt("lastHintsSyncTime", this.lastHintsSyncTime);
                editor.putBoolean("draftsLoaded", this.draftsLoaded);
                editor.putBoolean("unreadDialogsLoaded", this.unreadDialogsLoaded);
                editor.putInt("ratingLoadTime", this.ratingLoadTime);
                editor.putInt("botRatingLoadTime", this.botRatingLoadTime);
                editor.putBoolean("contactsReimported", this.contactsReimported);
                editor.putInt("loginTime", this.loginTime);
                editor.putBoolean("syncContacts", this.syncContacts);
                editor.putBoolean("suggestContacts", this.suggestContacts);
                editor.putBoolean("hasSecureData", this.hasSecureData);
                editor.putBoolean("notificationsSettingsLoaded3", this.notificationsSettingsLoaded);
                editor.putBoolean("notificationsSignUpSettingsLoaded", this.notificationsSignUpSettingsLoaded);
                editor.putLong("autoDownloadConfigLoadTime", this.autoDownloadConfigLoadTime);
                editor.putBoolean("hasValidDialogLoadIds", this.hasValidDialogLoadIds);
                editor.putInt("sharingMyLocationUntil", this.sharingMyLocationUntil);
                editor.putInt("lastMyLocationShareTime", this.lastMyLocationShareTime);
                editor.putBoolean("filtersLoaded", this.filtersLoaded);
                editor.putInt("6migrateOffsetId", this.migrateOffsetId);
                if (this.migrateOffsetId != -1) {
                    editor.putInt("6migrateOffsetDate", this.migrateOffsetDate);
                    editor.putLong("6migrateOffsetUserId", this.migrateOffsetUserId);
                    editor.putLong("6migrateOffsetChatId", this.migrateOffsetChatId);
                    editor.putLong("6migrateOffsetChannelId", this.migrateOffsetChannelId);
                    editor.putLong("6migrateOffsetAccess", this.migrateOffsetAccess);
                }
                if (this.unacceptedTermsOfService != null) {
                    try {
                        SerializedData data = new SerializedData(this.unacceptedTermsOfService.getObjectSize());
                        this.unacceptedTermsOfService.serializeToStream(data);
                        editor.putString("terms", Base64.encodeToString(data.toByteArray(), 0));
                        data.cleanup();
                    } catch (Exception e) {
                    }
                } else {
                    editor.remove("terms");
                }
                SharedConfig.saveConfig();
                if (this.tmpPassword != null) {
                    SerializedData data2 = new SerializedData();
                    this.tmpPassword.serializeToStream(data2);
                    String string = Base64.encodeToString(data2.toByteArray(), 0);
                    editor.putString("tmpPassword", string);
                    data2.cleanup();
                } else {
                    editor.remove("tmpPassword");
                }
                if (this.currentUser != null) {
                    if (withFile) {
                        SerializedData data3 = new SerializedData();
                        this.currentUser.serializeToStream(data3);
                        String string2 = Base64.encodeToString(data3.toByteArray(), 0);
                        editor.putString("user", string2);
                        data3.cleanup();
                    }
                } else {
                    editor.remove("user");
                }
                editor.commit();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public static boolean isValidAccount(int num) {
        return num >= 0 && num < 4 && getInstance(num).isClientActivated();
    }

    public boolean isClientActivated() {
        boolean z;
        synchronized (this.sync) {
            z = this.currentUser != null;
        }
        return z;
    }

    public long getClientUserId() {
        long j;
        synchronized (this.sync) {
            TLRPC.User user = this.currentUser;
            j = user != null ? user.id : 0L;
        }
        return j;
    }

    public String getClientPhone() {
        String str;
        synchronized (this.sync) {
            TLRPC.User user = this.currentUser;
            str = (user == null || user.phone == null) ? "" : this.currentUser.phone;
        }
        return str;
    }

    public TLRPC.User getCurrentUser() {
        TLRPC.User user;
        synchronized (this.sync) {
            user = this.currentUser;
        }
        return user;
    }

    public void setCurrentUser(TLRPC.User user) {
        synchronized (this.sync) {
            TLRPC.User oldUser = this.currentUser;
            this.currentUser = user;
            this.clientUserId = user.id;
            checkPremium(oldUser, user);
        }
    }

    private void checkPremium(TLRPC.User oldUser, final TLRPC.User newUser) {
        if (oldUser == null || (newUser != null && oldUser.premium != newUser.premium)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.UserConfig$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UserConfig.this.m1230lambda$checkPremium$1$orgtelegrammessengerUserConfig(newUser);
                }
            });
        }
    }

    /* renamed from: lambda$checkPremium$1$org-telegram-messenger-UserConfig */
    public /* synthetic */ void m1230lambda$checkPremium$1$orgtelegrammessengerUserConfig(TLRPC.User newUser) {
        getMessagesController().updatePremium(newUser.premium);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.currentUserPremiumStatusChanged, new Object[0]);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.premiumStatusChangedGlobal, new Object[0]);
        getMediaDataController().loadPremiumPromo(false);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(21:8|(1:10)|11|(17:16|18|19|50|20|(1:24)|27|(1:29)|30|(1:34)|35|(1:39)|40|(1:42)|43|44|45)|17|18|19|50|20|(2:22|24)|27|(0)|30|(2:32|34)|35|(2:37|39)|40|(0)|43|44|45) */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0119, code lost:
        r8 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x011a, code lost:
        org.telegram.messenger.FileLog.e(r8);
     */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0127 A[Catch: all -> 0x01a1, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0007, B:8:0x0009, B:10:0x0012, B:11:0x001a, B:13:0x00d1, B:18:0x00dd, B:20:0x00f8, B:22:0x0100, B:24:0x0106, B:26:0x011a, B:27:0x011d, B:29:0x0127, B:30:0x014f, B:32:0x0157, B:34:0x015d, B:35:0x016f, B:37:0x0178, B:39:0x017e, B:40:0x0190, B:42:0x0194, B:43:0x019d, B:44:0x019f), top: B:49:0x0003, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0194 A[Catch: all -> 0x01a1, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0007, B:8:0x0009, B:10:0x0012, B:11:0x001a, B:13:0x00d1, B:18:0x00dd, B:20:0x00f8, B:22:0x0100, B:24:0x0106, B:26:0x011a, B:27:0x011d, B:29:0x0127, B:30:0x014f, B:32:0x0157, B:34:0x015d, B:35:0x016f, B:37:0x0178, B:39:0x017e, B:40:0x0190, B:42:0x0194, B:43:0x019d, B:44:0x019f), top: B:49:0x0003, inners: #1 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadConfig() {
        /*
            Method dump skipped, instructions count: 420
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UserConfig.loadConfig():void");
    }

    public boolean isConfigLoaded() {
        return this.configLoaded;
    }

    public void savePassword(byte[] hash, byte[] salted) {
        this.savedPasswordTime = SystemClock.elapsedRealtime();
        this.savedPasswordHash = hash;
        this.savedSaltedPassword = salted;
    }

    public void checkSavedPassword() {
        if ((this.savedSaltedPassword == null && this.savedPasswordHash == null) || Math.abs(SystemClock.elapsedRealtime() - this.savedPasswordTime) < 1800000) {
            return;
        }
        resetSavedPassword();
    }

    public void resetSavedPassword() {
        this.savedPasswordTime = 0L;
        if (this.savedPasswordHash != null) {
            Arrays.fill(this.savedPasswordHash, (byte) 0);
            this.savedPasswordHash = null;
        }
        if (this.savedSaltedPassword != null) {
            Arrays.fill(this.savedSaltedPassword, (byte) 0);
            this.savedSaltedPassword = null;
        }
    }

    private SharedPreferences getPreferences() {
        if (this.currentAccount == 0) {
            return ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
        }
        Context context = ApplicationLoader.applicationContext;
        return context.getSharedPreferences("userconfig" + this.currentAccount, 0);
    }

    public void clearConfig() {
        getPreferences().edit().clear().apply();
        this.sharingMyLocationUntil = 0;
        this.lastMyLocationShareTime = 0;
        this.currentUser = null;
        this.clientUserId = 0L;
        this.registeredForPush = false;
        this.contactsSavedCount = 0;
        this.lastSendMessageId = -210000;
        this.lastBroadcastId = -1;
        this.notificationsSettingsLoaded = false;
        this.notificationsSignUpSettingsLoaded = false;
        this.migrateOffsetId = -1;
        this.migrateOffsetDate = -1;
        this.migrateOffsetUserId = -1L;
        this.migrateOffsetChatId = -1L;
        this.migrateOffsetChannelId = -1L;
        this.migrateOffsetAccess = -1L;
        this.ratingLoadTime = 0;
        this.botRatingLoadTime = 0;
        this.draftsLoaded = false;
        this.contactsReimported = true;
        this.syncContacts = true;
        this.suggestContacts = true;
        this.unreadDialogsLoaded = true;
        this.hasValidDialogLoadIds = true;
        this.unacceptedTermsOfService = null;
        this.filtersLoaded = false;
        this.hasSecureData = false;
        this.loginTime = (int) (System.currentTimeMillis() / 1000);
        this.lastContactsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 82800;
        this.lastHintsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 90000;
        resetSavedPassword();
        boolean hasActivated = false;
        int a = 0;
        while (true) {
            if (a >= 4) {
                break;
            } else if (!AccountInstance.getInstance(a).getUserConfig().isClientActivated()) {
                a++;
            } else {
                hasActivated = true;
                break;
            }
        }
        if (!hasActivated) {
            SharedConfig.clearConfig();
        }
        saveConfig(true);
    }

    public boolean isPinnedDialogsLoaded(int folderId) {
        SharedPreferences preferences = getPreferences();
        return preferences.getBoolean("2pinnedDialogsLoaded" + folderId, false);
    }

    public void setPinnedDialogsLoaded(int folderId, boolean loaded) {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.putBoolean("2pinnedDialogsLoaded" + folderId, loaded).commit();
    }

    public int getTotalDialogsCount(int folderId) {
        SharedPreferences preferences = getPreferences();
        StringBuilder sb = new StringBuilder();
        sb.append("2totalDialogsLoadCount");
        sb.append(folderId == 0 ? "" : Integer.valueOf(folderId));
        return preferences.getInt(sb.toString(), 0);
    }

    public void setTotalDialogsCount(int folderId, int totalDialogsLoadCount) {
        SharedPreferences.Editor edit = getPreferences().edit();
        StringBuilder sb = new StringBuilder();
        sb.append("2totalDialogsLoadCount");
        sb.append(folderId == 0 ? "" : Integer.valueOf(folderId));
        edit.putInt(sb.toString(), totalDialogsLoadCount).commit();
    }

    public long[] getDialogLoadOffsets(int folderId) {
        SharedPreferences preferences = getPreferences();
        StringBuilder sb = new StringBuilder();
        sb.append("2dialogsLoadOffsetId");
        Object obj = "";
        sb.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        int i = -1;
        int dialogsLoadOffsetId = preferences.getInt(sb.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("2dialogsLoadOffsetDate");
        sb2.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        String sb3 = sb2.toString();
        if (this.hasValidDialogLoadIds) {
            i = 0;
        }
        int dialogsLoadOffsetDate = preferences.getInt(sb3, i);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("2dialogsLoadOffsetUserId");
        sb4.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        long j = -1;
        long dialogsLoadOffsetUserId = AndroidUtilities.getPrefIntOrLong(preferences, sb4.toString(), this.hasValidDialogLoadIds ? 0L : -1L);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("2dialogsLoadOffsetChatId");
        sb5.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        long dialogsLoadOffsetChatId = AndroidUtilities.getPrefIntOrLong(preferences, sb5.toString(), this.hasValidDialogLoadIds ? 0L : -1L);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("2dialogsLoadOffsetChannelId");
        sb6.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        long dialogsLoadOffsetChannelId = AndroidUtilities.getPrefIntOrLong(preferences, sb6.toString(), this.hasValidDialogLoadIds ? 0L : -1L);
        StringBuilder sb7 = new StringBuilder();
        sb7.append("2dialogsLoadOffsetAccess");
        if (folderId != 0) {
            obj = Integer.valueOf(folderId);
        }
        sb7.append(obj);
        String sb8 = sb7.toString();
        if (this.hasValidDialogLoadIds) {
            j = 0;
        }
        long dialogsLoadOffsetAccess = preferences.getLong(sb8, j);
        return new long[]{dialogsLoadOffsetId, dialogsLoadOffsetDate, dialogsLoadOffsetUserId, dialogsLoadOffsetChatId, dialogsLoadOffsetChannelId, dialogsLoadOffsetAccess};
    }

    public void setDialogsLoadOffset(int folderId, int dialogsLoadOffsetId, int dialogsLoadOffsetDate, long dialogsLoadOffsetUserId, long dialogsLoadOffsetChatId, long dialogsLoadOffsetChannelId, long dialogsLoadOffsetAccess) {
        SharedPreferences.Editor editor = getPreferences().edit();
        StringBuilder sb = new StringBuilder();
        sb.append("2dialogsLoadOffsetId");
        Object obj = "";
        sb.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putInt(sb.toString(), dialogsLoadOffsetId);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("2dialogsLoadOffsetDate");
        sb2.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putInt(sb2.toString(), dialogsLoadOffsetDate);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("2dialogsLoadOffsetUserId");
        sb3.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putLong(sb3.toString(), dialogsLoadOffsetUserId);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("2dialogsLoadOffsetChatId");
        sb4.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putLong(sb4.toString(), dialogsLoadOffsetChatId);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("2dialogsLoadOffsetChannelId");
        sb5.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putLong(sb5.toString(), dialogsLoadOffsetChannelId);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("2dialogsLoadOffsetAccess");
        if (folderId != 0) {
            obj = Integer.valueOf(folderId);
        }
        sb6.append(obj);
        editor.putLong(sb6.toString(), dialogsLoadOffsetAccess);
        editor.putBoolean("hasValidDialogLoadIds", true);
        editor.commit();
    }

    public boolean isPremium() {
        TLRPC.User user = this.currentUser;
        if (user == null) {
            return false;
        }
        return user.premium;
    }
}
