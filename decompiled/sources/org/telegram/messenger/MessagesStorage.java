package org.telegram.messenger;

import android.appwidget.AppWidgetManager;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$ChatParticipants;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputChannel;
import org.telegram.tgnet.TLRPC$InputDialogPeer;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$Poll;
import org.telegram.tgnet.TLRPC$PollResults;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_chatParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;
import org.telegram.tgnet.TLRPC$TL_folder;
import org.telegram.tgnet.TLRPC$TL_folderPeer;
import org.telegram.tgnet.TLRPC$TL_inputFolderPeer;
import org.telegram.tgnet.TLRPC$TL_inputMediaGame;
import org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC$TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached;
import org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported_old;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messageReplies;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_message_secret;
import org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC$TL_messages_botResults;
import org.telegram.tgnet.TLRPC$TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC$TL_messages_deleteScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_dialogs;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettingsEmpty_layer77;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photos_photos;
import org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC$TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC$TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC$TL_userStatusRecently;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_BotResults;
import org.telegram.tgnet.TLRPC$messages_Dialogs;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.TLRPC$photos_Photos;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
/* loaded from: classes.dex */
public class MessagesStorage extends BaseController {
    private static volatile MessagesStorage[] Instance = new MessagesStorage[4];
    private static final int LAST_DB_VERSION = 98;
    private int archiveUnreadCount;
    private File cacheFile;
    private SQLiteDatabase database;
    private boolean databaseMigrationInProgress;
    private int mainUnreadCount;
    private volatile int pendingArchiveUnreadCount;
    private volatile int pendingMainUnreadCount;
    private File shmCacheFile;
    public boolean showClearDatabaseAlert;
    private DispatchQueue storageQueue;
    private File walCacheFile;
    private AtomicLong lastTaskId = new AtomicLong(System.currentTimeMillis());
    private SparseArray<ArrayList<Runnable>> tasks = new SparseArray<>();
    private int lastDateValue = 0;
    private int lastPtsValue = 0;
    private int lastQtsValue = 0;
    private int lastSeqValue = 0;
    private int lastSecretVersion = 0;
    private byte[] secretPBytes = null;
    private int secretG = 0;
    private int lastSavedSeq = 0;
    private int lastSavedPts = 0;
    private int lastSavedDate = 0;
    private int lastSavedQts = 0;
    private ArrayList<MessagesController.DialogFilter> dialogFilters = new ArrayList<>();
    private SparseArray<MessagesController.DialogFilter> dialogFiltersMap = new SparseArray<>();
    private LongSparseArray<Boolean> unknownDialogsIds = new LongSparseArray<>();
    private CountDownLatch openSync = new CountDownLatch(1);
    private int[][] contacts = {new int[2], new int[2]};
    private int[][] nonContacts = {new int[2], new int[2]};
    private int[][] bots = {new int[2], new int[2]};
    private int[][] channels = {new int[2], new int[2]};
    private int[][] groups = {new int[2], new int[2]};
    private int[] mentionChannels = new int[2];
    private int[] mentionGroups = new int[2];
    private LongSparseArray<Integer> dialogsWithMentions = new LongSparseArray<>();
    private LongSparseArray<Integer> dialogsWithUnread = new LongSparseArray<>();

    /* loaded from: classes.dex */
    public interface BooleanCallback {
        void run(boolean z);
    }

    /* loaded from: classes.dex */
    public interface IntCallback {
        void run(int i);
    }

    /* loaded from: classes.dex */
    public interface LongCallback {
        void run(long j);
    }

    /* loaded from: classes.dex */
    public interface StringCallback {
        void run(String str);
    }

    public static MessagesStorage getInstance(int i) {
        MessagesStorage messagesStorage = Instance[i];
        if (messagesStorage == null) {
            synchronized (MessagesStorage.class) {
                messagesStorage = Instance[i];
                if (messagesStorage == null) {
                    MessagesStorage[] messagesStorageArr = Instance;
                    MessagesStorage messagesStorage2 = new MessagesStorage(i);
                    messagesStorageArr[i] = messagesStorage2;
                    messagesStorage = messagesStorage2;
                }
            }
        }
        return messagesStorage;
    }

    private void ensureOpened() {
        try {
            this.openSync.await();
        } catch (Throwable unused) {
        }
    }

    public int getLastDateValue() {
        ensureOpened();
        return this.lastDateValue;
    }

    public void setLastDateValue(int i) {
        ensureOpened();
        this.lastDateValue = i;
    }

    public int getLastPtsValue() {
        ensureOpened();
        return this.lastPtsValue;
    }

    public int getMainUnreadCount() {
        return this.mainUnreadCount;
    }

    public int getArchiveUnreadCount() {
        return this.archiveUnreadCount;
    }

    public void setLastPtsValue(int i) {
        ensureOpened();
        this.lastPtsValue = i;
    }

    public int getLastQtsValue() {
        ensureOpened();
        return this.lastQtsValue;
    }

    public void setLastQtsValue(int i) {
        ensureOpened();
        this.lastQtsValue = i;
    }

    public int getLastSeqValue() {
        ensureOpened();
        return this.lastSeqValue;
    }

    public void setLastSeqValue(int i) {
        ensureOpened();
        this.lastSeqValue = i;
    }

    public int getLastSecretVersion() {
        ensureOpened();
        return this.lastSecretVersion;
    }

    public void setLastSecretVersion(int i) {
        ensureOpened();
        this.lastSecretVersion = i;
    }

    public byte[] getSecretPBytes() {
        ensureOpened();
        return this.secretPBytes;
    }

    public void setSecretPBytes(byte[] bArr) {
        ensureOpened();
        this.secretPBytes = bArr;
    }

    public int getSecretG() {
        ensureOpened();
        return this.secretG;
    }

    public void setSecretG(int i) {
        ensureOpened();
        this.secretG = i;
    }

    public MessagesStorage(int i) {
        super(i);
        DispatchQueue dispatchQueue = new DispatchQueue("storageQueue_" + i);
        this.storageQueue = dispatchQueue;
        dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$new$0();
            }
        });
    }

    public /* synthetic */ void lambda$new$0() {
        openDatabase(1);
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public void bindTaskToGuid(Runnable runnable, int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.tasks.put(i, arrayList);
        }
        arrayList.add(runnable);
    }

    public void cancelTasksForGuid(int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            return;
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.storageQueue.cancelRunnable(arrayList.get(i2));
        }
        this.tasks.remove(i);
    }

    public void completeTaskForGuid(Runnable runnable, int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            return;
        }
        arrayList.remove(runnable);
        if (!arrayList.isEmpty()) {
            return;
        }
        this.tasks.remove(i);
    }

    public long getDatabaseSize() {
        File file = this.cacheFile;
        long j = 0;
        if (file != null) {
            j = 0 + file.length();
        }
        File file2 = this.shmCacheFile;
        return file2 != null ? j + file2.length() : j;
    }

    public void openDatabase(int i) {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File file = new File(filesDirFixed, "account" + this.currentAccount + "/");
            file.mkdirs();
            filesDirFixed = file;
        }
        this.cacheFile = new File(filesDirFixed, "cache4.db");
        this.walCacheFile = new File(filesDirFixed, "cache4.db-wal");
        this.shmCacheFile = new File(filesDirFixed, "cache4.db-shm");
        boolean z = !this.cacheFile.exists();
        int i2 = 3;
        try {
            SQLiteDatabase sQLiteDatabase = new SQLiteDatabase(this.cacheFile.getPath());
            this.database = sQLiteDatabase;
            sQLiteDatabase.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_size_limit = 10485760").stepThis().dispose();
            if (z) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("create new database");
                }
                this.database.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE scheduled_messages_v2(mid INTEGER, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, reply_to_message_id INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, send_state, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages_v2 ON scheduled_messages_v2(uid, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, reply_to_message_id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_v2(mid INTEGER, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, reply_to_message_id INTEGER, custom_params BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_v2 ON messages_v2(uid, mid, read_state, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_v2 ON messages_v2(uid, date, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_v2 ON messages_v2(mid, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_v2 ON messages_v2(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_v2 ON messages_v2(mid, send_state, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_v2 ON messages_v2(uid, mention, read_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_v2 ON messages_v2(mid, is_channel);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_v2 ON messages_v2(mid, reply_to_message_id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER, flags INTEGER, folder_id INTEGER, data BLOB, unread_reactions INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON dialogs(flags);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_filter(id INTEGER PRIMARY KEY, ord INTEGER, unread_count INTEGER, flags INTEGER, title TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_filter_ep(id INTEGER, peer INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_filter_pin_v2(id INTEGER, peer INTEGER, pin INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE randoms_v2(random_id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (random_id, mid, uid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms_v2 ON randoms_v2(mid, uid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_tasks_v4(mid INTEGER, uid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, uid, media))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v4 ON enc_tasks_v4(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_v4(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v4 ON media_v4(uid, mid, type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid_v2 ON bot_keyboard(mid, uid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER, online INTEGER, inviter INTEGER, links INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_pinned_v2(uid INTEGER, mid INTEGER, data BLOB, PRIMARY KEY (uid, mid));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_pinned_count(uid INTEGER PRIMARY KEY, count INTEGER, end INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER, admin_id INTEGER, mtproto_seq INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_admins_v3(did INTEGER, uid INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER, premium INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_dice(emoji TEXT PRIMARY KEY, data BLOB, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE webpage_pending_v2(id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (id, mid, uid));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_info_v2(uid INTEGER, dialogId INTEGER, info BLOB, PRIMARY KEY(uid, dialogId))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB, proximity INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE shortcut_widget(id INTEGER, did INTEGER, ord INTEGER, PRIMARY KEY (id, did));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS shortcut_widget_did ON shortcut_widget(did);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id_v2 ON polls_v2(id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE reactions(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE reaction_mentions(message_id INTEGER, state INTEGER, dialog_id INTEGER, PRIMARY KEY(message_id, dialog_id))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_did ON reaction_mentions(dialog_id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE downloading_documents(data BLOB, hash INTEGER, id INTEGER, state INTEGER, date INTEGER, PRIMARY KEY(hash, id));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE attach_menu_bots(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE premium_promo(data BLOB, date INTEGER);").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 98").stepThis().dispose();
            } else {
                int intValue = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current db version = " + intValue);
                }
                if (intValue == 0) {
                    throw new Exception("malformed");
                }
                try {
                    SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
                    if (queryFinalized.next()) {
                        this.lastSeqValue = queryFinalized.intValue(0);
                        this.lastPtsValue = queryFinalized.intValue(1);
                        this.lastDateValue = queryFinalized.intValue(2);
                        this.lastQtsValue = queryFinalized.intValue(3);
                        this.lastSecretVersion = queryFinalized.intValue(4);
                        this.secretG = queryFinalized.intValue(5);
                        if (queryFinalized.isNull(6)) {
                            this.secretPBytes = null;
                        } else {
                            byte[] byteArrayValue = queryFinalized.byteArrayValue(6);
                            this.secretPBytes = byteArrayValue;
                            if (byteArrayValue != null && byteArrayValue.length == 1) {
                                this.secretPBytes = null;
                            }
                        }
                    }
                    queryFinalized.dispose();
                } catch (Exception e) {
                    if (e.getMessage() != null && e.getMessage().contains("malformed")) {
                        throw new RuntimeException("malformed");
                    }
                    FileLog.e(e);
                    try {
                        this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                        this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                if (intValue < LAST_DB_VERSION) {
                    try {
                        updateDbToLastVersion(intValue);
                    } catch (Exception e3) {
                        if (BuildVars.DEBUG_PRIVATE_VERSION) {
                            throw e3;
                        }
                        FileLog.e(e3);
                        throw new RuntimeException("malformed");
                    }
                }
            }
        } catch (Exception e4) {
            FileLog.e(e4);
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                throw new RuntimeException(e4);
            }
            if (i < 3 && e4.getMessage() != null && e4.getMessage().contains("malformed")) {
                if (i == 2) {
                    cleanupInternal(true);
                    for (int i3 = 0; i3 < 2; i3++) {
                        getUserConfig().setDialogsLoadOffset(i3, 0, 0, 0L, 0L, 0L, 0L);
                        getUserConfig().setTotalDialogsCount(i3, 0);
                    }
                    getUserConfig().saveConfig(false);
                } else {
                    cleanupInternal(false);
                }
                if (i == 1) {
                    i2 = 2;
                }
                openDatabase(i2);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$openDatabase$1();
            }
        });
        loadDialogFilters();
        loadUnreadMessages();
        loadPendingTasks();
        try {
            this.openSync.countDown();
        } catch (Throwable unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$openDatabase$2();
            }
        });
    }

    public /* synthetic */ void lambda$openDatabase$1() {
        if (this.databaseMigrationInProgress) {
            this.databaseMigrationInProgress = false;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, Boolean.FALSE);
        }
    }

    public /* synthetic */ void lambda$openDatabase$2() {
        this.showClearDatabaseAlert = false;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseOpened, new Object[0]);
    }

    public boolean isDatabaseMigrationInProgress() {
        return this.databaseMigrationInProgress;
    }

    private void updateDbToLastVersion(int i) throws Exception {
        SQLiteCursor sQLiteCursor;
        SQLiteCursor sQLiteCursor2;
        int i2;
        int i3;
        NativeByteBuffer nativeByteBuffer;
        SQLiteCursor sQLiteCursor3;
        SQLiteCursor sQLiteCursor4;
        SQLiteCursor sQLiteCursor5;
        SQLiteCursor sQLiteCursor6;
        SQLiteCursor sQLiteCursor7;
        SQLiteCursor sQLiteCursor8;
        MessagesStorage messagesStorage = this;
        int i4 = i;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateDbToLastVersion$3();
            }
        });
        FileLog.d("MessagesStorage start db migration from " + i4 + " to " + LAST_DB_VERSION);
        int i5 = 4;
        if (i4 < 4) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
            messagesStorage.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
            fixNotificationSettings();
            messagesStorage.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
            i4 = 4;
        }
        int i6 = 6;
        int i7 = 2;
        int i8 = 1;
        int i9 = 0;
        if (i4 == 4) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            SQLiteCursor queryFinalized = messagesStorage.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast = messagesStorage.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    int limit = byteBufferValue.limit();
                    for (int i10 = 0; i10 < limit / 4; i10++) {
                        executeFast.requery();
                        executeFast.bindInteger(1, byteBufferValue.readInt32(false));
                        executeFast.bindInteger(2, intValue);
                        executeFast.step();
                    }
                    byteBufferValue.reuse();
                }
            }
            executeFast.dispose();
            queryFinalized.dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            i4 = 6;
        }
        if (i4 == 6) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            i4 = 7;
        }
        if (i4 == 7 || i4 == 8 || i4 == 9) {
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
            i4 = 10;
        }
        if (i4 == 10) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
            i4 = 11;
        }
        if (i4 == 11 || i4 == 12) {
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
            i4 = 13;
        }
        if (i4 == 13) {
            messagesStorage.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            i4 = 14;
        }
        if (i4 == 14) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            i4 = 15;
        }
        if (i4 == 15) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            i4 = 16;
        }
        if (i4 == 16) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            i4 = 17;
        }
        if (i4 == 17) {
            messagesStorage.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            i4 = 18;
        }
        if (i4 == 18) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            i4 = 19;
        }
        if (i4 == 19) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            i4 = 20;
        }
        if (i4 == 20) {
            messagesStorage.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            i4 = 21;
        }
        if (i4 == 21) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            SQLiteCursor queryFinalized2 = messagesStorage.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            SQLitePreparedStatement executeFast2 = messagesStorage.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (queryFinalized2.next()) {
                long intValue2 = queryFinalized2.intValue(0);
                NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(1);
                if (byteBufferValue2 != null) {
                    TLRPC$ChatParticipants TLdeserialize = TLRPC$ChatParticipants.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                    byteBufferValue2.reuse();
                    if (TLdeserialize != null) {
                        TLRPC$TL_chatFull tLRPC$TL_chatFull = new TLRPC$TL_chatFull();
                        tLRPC$TL_chatFull.id = intValue2;
                        tLRPC$TL_chatFull.chat_photo = new TLRPC$TL_photoEmpty();
                        tLRPC$TL_chatFull.notify_settings = new TLRPC$TL_peerNotifySettingsEmpty_layer77();
                        tLRPC$TL_chatFull.exported_invite = null;
                        tLRPC$TL_chatFull.participants = TLdeserialize;
                        NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(tLRPC$TL_chatFull.getObjectSize());
                        tLRPC$TL_chatFull.serializeToStream(nativeByteBuffer2);
                        executeFast2.requery();
                        executeFast2.bindLong(1, intValue2);
                        executeFast2.bindByteBuffer(2, nativeByteBuffer2);
                        executeFast2.step();
                        nativeByteBuffer2.reuse();
                    }
                }
            }
            executeFast2.dispose();
            queryFinalized2.dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
            i4 = 22;
        }
        if (i4 == 22) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            i4 = 23;
        }
        if (i4 == 23 || i4 == 24) {
            messagesStorage.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
            i4 = 25;
        }
        if (i4 == 25 || i4 == 26) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
            i4 = 27;
        }
        if (i4 == 27) {
            messagesStorage.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
            i4 = 28;
        }
        if (i4 == 28 || i4 == 29) {
            messagesStorage.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
            i4 = 30;
        }
        if (i4 == 30) {
            messagesStorage.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
            i4 = 31;
        }
        if (i4 == 31) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            i4 = 32;
        }
        if (i4 == 32) {
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
            i4 = 33;
        }
        if (i4 == 33) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
            i4 = 34;
        }
        if (i4 == 34) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
            i4 = 35;
        }
        if (i4 == 35) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            i4 = 36;
        }
        if (i4 == 36) {
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            i4 = 37;
        }
        if (i4 == 37) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
            i4 = 38;
        }
        if (i4 == 38) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
            i4 = 39;
        }
        if (i4 == 39) {
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
            i4 = 40;
        }
        if (i4 == 40) {
            fixNotificationSettings();
            messagesStorage.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            i4 = 41;
        }
        if (i4 == 41) {
            messagesStorage.database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
            i4 = 42;
        }
        if (i4 == 42) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
            i4 = 43;
        }
        if (i4 == 43) {
            messagesStorage.database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
            i4 = 44;
        }
        if (i4 == 44) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
            i4 = 45;
        }
        if (i4 == 45) {
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
            i4 = 46;
        }
        if (i4 == 46) {
            messagesStorage.database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
            i4 = 47;
        }
        if (i4 == 47) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 48").stepThis().dispose();
            i4 = 48;
        }
        if (i4 == 48) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 49").stepThis().dispose();
            i4 = 49;
        }
        if (i4 == 49) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 50").stepThis().dispose();
            i4 = 50;
        }
        if (i4 == 50) {
            messagesStorage.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE sent_files_v2 ADD COLUMN parent TEXT").stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE download_queue ADD COLUMN parent TEXT").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 51").stepThis().dispose();
            i4 = 51;
        }
        if (i4 == 51) {
            messagesStorage.database.executeFast("ALTER TABLE media_counts_v2 ADD COLUMN old INTEGER").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 52").stepThis().dispose();
            i4 = 52;
        }
        if (i4 == 52) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id ON polls_v2(id);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 53").stepThis().dispose();
            i4 = 53;
        }
        if (i4 == 53) {
            messagesStorage.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN online INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 54").stepThis().dispose();
            i4 = 54;
        }
        if (i4 == 54) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS wallpapers;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 55").stepThis().dispose();
            i4 = 55;
        }
        if (i4 == 55) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 56").stepThis().dispose();
            i4 = 56;
        }
        if (i4 == 56 || i4 == 57) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 58").stepThis().dispose();
            i4 = 58;
        }
        if (i4 == 58) {
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE emoji_keywords_info_v2 ADD COLUMN date INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 59").stepThis().dispose();
            i4 = 59;
        }
        if (i4 == 59) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN folder_id INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN data BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 60").stepThis().dispose();
            i4 = 60;
        }
        if (i4 == 60) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS channel_admins;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS blocked_users;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 61").stepThis().dispose();
            i4 = 61;
        }
        if (i4 == 61) {
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS send_state_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages2 ON messages(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 62").stepThis().dispose();
            i4 = 62;
        }
        if (i4 == 62) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages(mid INTEGER PRIMARY KEY, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages ON scheduled_messages(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages ON scheduled_messages(uid, date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 63").stepThis().dispose();
            i4 = 63;
        }
        if (i4 == 63) {
            messagesStorage.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 64").stepThis().dispose();
            i4 = 64;
        }
        if (i4 == 64) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_filter(id INTEGER PRIMARY KEY, ord INTEGER, unread_count INTEGER, flags INTEGER, title TEXT)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_filter_ep(id INTEGER, peer INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 65").stepThis().dispose();
            i4 = 65;
        }
        if (i4 == 65) {
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON dialogs(flags);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 66").stepThis().dispose();
            i4 = 66;
        }
        if (i4 == 66) {
            messagesStorage.database.executeFast("CREATE TABLE dialog_filter_pin_v2(id INTEGER, peer INTEGER, pin INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 67").stepThis().dispose();
            i4 = 67;
        }
        if (i4 == 67) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_dice(emoji TEXT PRIMARY KEY, data BLOB, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 68").stepThis().dispose();
            i4 = 68;
        }
        if (i4 == 68) {
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN forwards INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 69").stepThis().dispose();
            i4 = 69;
        }
        if (i4 == 69) {
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN replies_data BLOB default NULL");
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN thread_reply_id INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 70").stepThis().dispose();
            i4 = 70;
        }
        if (i4 == 70) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned_v2(uid INTEGER, mid INTEGER, data BLOB, PRIMARY KEY (uid, mid));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 71").stepThis().dispose();
            i4 = 71;
        }
        if (i4 == 71) {
            messagesStorage.executeNoException("ALTER TABLE sharing_locations ADD COLUMN proximity INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 72").stepThis().dispose();
            i4 = 72;
        }
        if (i4 == 72) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned_count(uid INTEGER PRIMARY KEY, count INTEGER, end INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 73").stepThis().dispose();
            i4 = 73;
        }
        if (i4 == 73) {
            messagesStorage.executeNoException("ALTER TABLE chat_settings_v2 ADD COLUMN inviter INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 74").stepThis().dispose();
            i4 = 74;
        }
        if (i4 == 74) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS shortcut_widget(id INTEGER, did INTEGER, ord INTEGER, PRIMARY KEY (id, did));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS shortcut_widget_did ON shortcut_widget(did);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 75").stepThis().dispose();
            i4 = 75;
        }
        if (i4 == 75) {
            messagesStorage.executeNoException("ALTER TABLE chat_settings_v2 ADD COLUMN links INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 76").stepThis().dispose();
            i4 = 76;
        }
        if (i4 == 76) {
            messagesStorage.executeNoException("ALTER TABLE enc_tasks_v2 ADD COLUMN media INTEGER default -1");
            messagesStorage.database.executeFast("PRAGMA user_version = 77").stepThis().dispose();
            i4 = 77;
        }
        if (i4 == 77) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS channel_admins_v2;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins_v3(did INTEGER, uid INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 78").stepThis().dispose();
            i4 = 78;
        }
        if (i4 == 78) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS bot_info;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS bot_info_v2(uid INTEGER, dialogId INTEGER, info BLOB, PRIMARY KEY(uid, dialogId))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 79").stepThis().dispose();
            i4 = 79;
        }
        int i11 = 3;
        if (i4 == 79) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v3(mid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, media))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v3 ON enc_tasks_v3(date);").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            SQLiteCursor queryFinalized3 = messagesStorage.database.queryFinalized("SELECT mid, date, media FROM enc_tasks_v2 WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast3 = messagesStorage.database.executeFast("REPLACE INTO enc_tasks_v3 VALUES(?, ?, ?)");
            if (queryFinalized3.next()) {
                long longValue = queryFinalized3.longValue(0);
                int intValue3 = queryFinalized3.intValue(1);
                int intValue4 = queryFinalized3.intValue(2);
                executeFast3.requery();
                executeFast3.bindLong(1, longValue);
                executeFast3.bindInteger(2, intValue3);
                executeFast3.bindInteger(3, intValue4);
                executeFast3.step();
            }
            executeFast3.dispose();
            queryFinalized3.dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks_v2;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS enc_tasks_v2;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 80").stepThis().dispose();
            i4 = 80;
        }
        int i12 = 5;
        if (i4 == 80) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages_v2(mid INTEGER, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages_v2 ON scheduled_messages_v2(uid, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid_v2 ON bot_keyboard(mid, uid);").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS bot_keyboard_idx_mid;").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor8 = messagesStorage.database.queryFinalized("SELECT mid, uid, send_state, date, data, ttl, replydata FROM scheduled_messages_v2 WHERE 1", new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
                sQLiteCursor8 = null;
            }
            if (sQLiteCursor8 != null) {
                SQLitePreparedStatement executeFast4 = messagesStorage.database.executeFast("REPLACE INTO scheduled_messages_v2 VALUES(?, ?, ?, ?, ?, ?, ?)");
                while (sQLiteCursor8.next()) {
                    NativeByteBuffer byteBufferValue3 = sQLiteCursor8.byteBufferValue(4);
                    if (byteBufferValue3 != null) {
                        int intValue5 = sQLiteCursor8.intValue(i9);
                        long longValue2 = sQLiteCursor8.longValue(1);
                        int intValue6 = sQLiteCursor8.intValue(2);
                        int intValue7 = sQLiteCursor8.intValue(3);
                        int intValue8 = sQLiteCursor8.intValue(i12);
                        NativeByteBuffer byteBufferValue4 = sQLiteCursor8.byteBufferValue(6);
                        executeFast4.requery();
                        executeFast4.bindInteger(1, intValue5);
                        executeFast4.bindLong(2, longValue2);
                        executeFast4.bindInteger(3, intValue6);
                        executeFast4.bindByteBuffer(4, byteBufferValue3);
                        executeFast4.bindInteger(5, intValue7);
                        executeFast4.bindInteger(6, intValue8);
                        if (byteBufferValue4 != null) {
                            executeFast4.bindByteBuffer(7, byteBufferValue4);
                        } else {
                            executeFast4.bindNull(7);
                        }
                        executeFast4.step();
                        if (byteBufferValue4 != null) {
                            byteBufferValue4.reuse();
                        }
                        byteBufferValue3.reuse();
                        i9 = 0;
                        i12 = 5;
                    }
                }
                sQLiteCursor8.dispose();
                executeFast4.dispose();
            }
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS send_state_idx_scheduled_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_date_idx_scheduled_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS scheduled_messages;").stepThis().dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("PRAGMA user_version = 81").stepThis().dispose();
            i4 = 81;
        }
        if (i4 == 81) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_v3(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v3 ON media_v3(uid, mid, type, date);").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor7 = messagesStorage.database.queryFinalized("SELECT mid, uid, date, type, data FROM media_v2 WHERE 1", new Object[0]);
            } catch (Exception e2) {
                FileLog.e(e2);
                sQLiteCursor7 = null;
            }
            if (sQLiteCursor7 != null) {
                SQLitePreparedStatement executeFast5 = messagesStorage.database.executeFast("REPLACE INTO media_v3 VALUES(?, ?, ?, ?, ?)");
                while (sQLiteCursor7.next()) {
                    NativeByteBuffer byteBufferValue5 = sQLiteCursor7.byteBufferValue(4);
                    if (byteBufferValue5 != null) {
                        int intValue9 = sQLiteCursor7.intValue(0);
                        long longValue3 = sQLiteCursor7.longValue(1);
                        if (((int) longValue3) == 0) {
                            longValue3 = DialogObject.makeEncryptedDialogId((int) (longValue3 >> 32));
                        }
                        int intValue10 = sQLiteCursor7.intValue(2);
                        int intValue11 = sQLiteCursor7.intValue(3);
                        executeFast5.requery();
                        executeFast5.bindInteger(1, intValue9);
                        executeFast5.bindLong(2, longValue3);
                        executeFast5.bindInteger(3, intValue10);
                        executeFast5.bindInteger(4, intValue11);
                        executeFast5.bindByteBuffer(5, byteBufferValue5);
                        executeFast5.step();
                        byteBufferValue5.reuse();
                    }
                }
                sQLiteCursor7.dispose();
                executeFast5.dispose();
            }
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_type_date_idx_media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media_v2;").stepThis().dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("PRAGMA user_version = 82").stepThis().dispose();
            i4 = 82;
        }
        if (i4 == 82) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS randoms_v2(random_id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (random_id, mid, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms_v2 ON randoms_v2(mid, uid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v4(mid INTEGER, uid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, uid, media))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v4 ON enc_tasks_v4(date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id_v2 ON polls_v2(id);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending_v2(id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (id, mid, uid));").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor3 = messagesStorage.database.queryFinalized("SELECT r.random_id, r.mid, m.uid FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e3) {
                FileLog.e(e3);
                sQLiteCursor3 = null;
            }
            if (sQLiteCursor3 != null) {
                SQLitePreparedStatement executeFast6 = messagesStorage.database.executeFast("REPLACE INTO randoms_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor3.next()) {
                    long longValue4 = sQLiteCursor3.longValue(0);
                    int intValue12 = sQLiteCursor3.intValue(1);
                    long longValue5 = sQLiteCursor3.longValue(2);
                    if (((int) longValue5) == 0) {
                        longValue5 = DialogObject.makeEncryptedDialogId((int) (longValue5 >> 32));
                    }
                    executeFast6.requery();
                    executeFast6.bindLong(1, longValue4);
                    executeFast6.bindInteger(2, intValue12);
                    executeFast6.bindLong(3, longValue5);
                    executeFast6.step();
                }
                sQLiteCursor3.dispose();
                executeFast6.dispose();
            }
            try {
                sQLiteCursor4 = messagesStorage.database.queryFinalized("SELECT p.mid, m.uid, p.id FROM polls as p INNER JOIN messages as m ON p.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e4) {
                FileLog.e(e4);
                sQLiteCursor4 = null;
            }
            if (sQLiteCursor4 != null) {
                SQLitePreparedStatement executeFast7 = messagesStorage.database.executeFast("REPLACE INTO polls_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor4.next()) {
                    int intValue13 = sQLiteCursor4.intValue(0);
                    long longValue6 = sQLiteCursor4.longValue(1);
                    long longValue7 = sQLiteCursor4.longValue(2);
                    if (((int) longValue6) == 0) {
                        longValue6 = DialogObject.makeEncryptedDialogId((int) (longValue6 >> 32));
                    }
                    executeFast7.requery();
                    executeFast7.bindInteger(1, intValue13);
                    executeFast7.bindLong(2, longValue6);
                    executeFast7.bindLong(3, longValue7);
                    executeFast7.step();
                }
                sQLiteCursor4.dispose();
                executeFast7.dispose();
            }
            try {
                sQLiteCursor5 = messagesStorage.database.queryFinalized("SELECT wp.id, wp.mid, m.uid FROM webpage_pending as wp INNER JOIN messages as m ON wp.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e5) {
                FileLog.e(e5);
                sQLiteCursor5 = null;
            }
            if (sQLiteCursor5 != null) {
                SQLitePreparedStatement executeFast8 = messagesStorage.database.executeFast("REPLACE INTO webpage_pending_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor5.next()) {
                    long longValue8 = sQLiteCursor5.longValue(0);
                    int intValue14 = sQLiteCursor5.intValue(1);
                    long longValue9 = sQLiteCursor5.longValue(2);
                    if (((int) longValue9) == 0) {
                        longValue9 = DialogObject.makeEncryptedDialogId((int) (longValue9 >> 32));
                    }
                    executeFast8.requery();
                    executeFast8.bindLong(1, longValue8);
                    executeFast8.bindInteger(2, intValue14);
                    executeFast8.bindLong(3, longValue9);
                    executeFast8.step();
                }
                sQLiteCursor5.dispose();
                executeFast8.dispose();
            }
            try {
                sQLiteCursor6 = messagesStorage.database.queryFinalized("SELECT et.mid, m.uid, et.date, et.media FROM enc_tasks_v3 as et INNER JOIN messages as m ON et.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e6) {
                FileLog.e(e6);
                sQLiteCursor6 = null;
            }
            if (sQLiteCursor6 != null) {
                SQLitePreparedStatement executeFast9 = messagesStorage.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
                while (sQLiteCursor6.next()) {
                    int intValue15 = sQLiteCursor6.intValue(0);
                    long longValue10 = sQLiteCursor6.longValue(1);
                    int intValue16 = sQLiteCursor6.intValue(2);
                    int intValue17 = sQLiteCursor6.intValue(3);
                    if (((int) longValue10) == 0) {
                        longValue10 = DialogObject.makeEncryptedDialogId((int) (longValue10 >> 32));
                    }
                    executeFast9.requery();
                    executeFast9.bindInteger(1, intValue15);
                    executeFast9.bindLong(2, longValue10);
                    executeFast9.bindInteger(3, intValue16);
                    executeFast9.bindInteger(4, intValue17);
                    executeFast9.step();
                }
                sQLiteCursor6.dispose();
                executeFast9.dispose();
            }
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS mid_idx_randoms;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS randoms;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks_v3;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS enc_tasks_v3;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS polls_id;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS polls;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS webpage_pending;").stepThis().dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("PRAGMA user_version = 83").stepThis().dispose();
            i4 = 83;
        }
        if (i4 == 83) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS messages_v2(mid INTEGER, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_v2 ON messages_v2(uid, mid, read_state, out);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_v2 ON messages_v2(uid, date, mid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_v2 ON messages_v2(mid, out);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_v2 ON messages_v2(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_v2 ON messages_v2(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_v2 ON messages_v2(uid, mention, read_state);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_v2 ON messages_v2(mid, is_channel);").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor2 = messagesStorage.database.queryFinalized("SELECT mid, uid, read_state, send_state, date, data, out, ttl, media, replydata, imp, mention, forwards, replies_data, thread_reply_id FROM messages WHERE 1", new Object[0]);
            } catch (Exception e7) {
                FileLog.e(e7);
                sQLiteCursor2 = null;
            }
            if (sQLiteCursor2 != null) {
                SQLitePreparedStatement executeFast10 = messagesStorage.database.executeFast("REPLACE INTO messages_v2 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                while (sQLiteCursor2.next()) {
                    NativeByteBuffer byteBufferValue6 = sQLiteCursor2.byteBufferValue(5);
                    if (byteBufferValue6 != null) {
                        long intValue18 = sQLiteCursor2.intValue(0);
                        long longValue11 = sQLiteCursor2.longValue(i8);
                        if (((int) longValue11) == 0) {
                            longValue11 = DialogObject.makeEncryptedDialogId((int) (longValue11 >> 32));
                        }
                        int intValue19 = sQLiteCursor2.intValue(i7);
                        int intValue20 = sQLiteCursor2.intValue(i11);
                        int intValue21 = sQLiteCursor2.intValue(i5);
                        int intValue22 = sQLiteCursor2.intValue(i6);
                        int intValue23 = sQLiteCursor2.intValue(7);
                        int intValue24 = sQLiteCursor2.intValue(8);
                        NativeByteBuffer byteBufferValue7 = sQLiteCursor2.byteBufferValue(9);
                        int intValue25 = sQLiteCursor2.intValue(10);
                        int intValue26 = sQLiteCursor2.intValue(11);
                        int intValue27 = sQLiteCursor2.intValue(12);
                        NativeByteBuffer byteBufferValue8 = sQLiteCursor2.byteBufferValue(13);
                        int intValue28 = sQLiteCursor2.intValue(14);
                        SQLiteCursor sQLiteCursor9 = sQLiteCursor2;
                        int i13 = (int) (longValue11 >> 32);
                        if (intValue23 < 0) {
                            TLRPC$Message TLdeserialize2 = TLRPC$Message.TLdeserialize(byteBufferValue6, byteBufferValue6.readInt32(false), false);
                            if (TLdeserialize2 != null) {
                                i3 = intValue24;
                                TLdeserialize2.readAttachPath(byteBufferValue6, getUserConfig().clientUserId);
                                if (TLdeserialize2.params == null) {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    TLdeserialize2.params = hashMap;
                                    StringBuilder sb = new StringBuilder();
                                    i2 = i13;
                                    sb.append("");
                                    sb.append(intValue23);
                                    hashMap.put("fwd_peer", sb.toString());
                                } else {
                                    i2 = i13;
                                }
                                byteBufferValue6.reuse();
                                NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(TLdeserialize2.getObjectSize());
                                TLdeserialize2.serializeToStream(nativeByteBuffer3);
                                byteBufferValue6 = nativeByteBuffer3;
                            } else {
                                i2 = i13;
                                i3 = intValue24;
                            }
                            intValue23 = 0;
                        } else {
                            i2 = i13;
                            i3 = intValue24;
                        }
                        executeFast10.requery();
                        executeFast10.bindInteger(1, (int) intValue18);
                        executeFast10.bindLong(2, longValue11);
                        executeFast10.bindInteger(3, intValue19);
                        executeFast10.bindInteger(4, intValue20);
                        executeFast10.bindInteger(5, intValue21);
                        executeFast10.bindByteBuffer(6, byteBufferValue6);
                        executeFast10.bindInteger(7, intValue22);
                        executeFast10.bindInteger(8, intValue23);
                        executeFast10.bindInteger(9, i3);
                        if (byteBufferValue7 != null) {
                            executeFast10.bindByteBuffer(10, byteBufferValue7);
                        } else {
                            executeFast10.bindNull(10);
                        }
                        executeFast10.bindInteger(11, intValue25);
                        executeFast10.bindInteger(12, intValue26);
                        executeFast10.bindInteger(13, intValue27);
                        if (byteBufferValue8 != null) {
                            nativeByteBuffer = byteBufferValue8;
                            executeFast10.bindByteBuffer(14, nativeByteBuffer);
                        } else {
                            nativeByteBuffer = byteBufferValue8;
                            executeFast10.bindNull(14);
                        }
                        executeFast10.bindInteger(15, intValue28);
                        executeFast10.bindInteger(16, i2 > 0 ? 1 : 0);
                        executeFast10.step();
                        if (byteBufferValue7 != null) {
                            byteBufferValue7.reuse();
                        }
                        if (nativeByteBuffer != null) {
                            nativeByteBuffer.reuse();
                        }
                        byteBufferValue6.reuse();
                        sQLiteCursor2 = sQLiteCursor9;
                        i5 = 4;
                        i6 = 6;
                        i7 = 2;
                        i8 = 1;
                        i11 = 3;
                    }
                }
                sQLiteCursor2.dispose();
                executeFast10.dispose();
            }
            messagesStorage = this;
            int i14 = 0;
            SQLiteCursor queryFinalized4 = messagesStorage.database.queryFinalized("SELECT did, last_mid, last_mid_i FROM dialogs WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast11 = messagesStorage.database.executeFast("UPDATE dialogs SET last_mid = ?, last_mid_i = ? WHERE did = ?");
            ArrayList arrayList = null;
            ArrayList arrayList2 = null;
            while (queryFinalized4.next()) {
                long longValue12 = queryFinalized4.longValue(i14);
                int i15 = (int) longValue12;
                int i16 = (int) (longValue12 >> 32);
                if (i15 == 0) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(i16));
                } else if (i16 == 2) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(Integer.valueOf(i15));
                }
                executeFast11.requery();
                executeFast11.bindInteger(1, queryFinalized4.intValue(1));
                executeFast11.bindInteger(2, queryFinalized4.intValue(2));
                executeFast11.bindLong(3, longValue12);
                executeFast11.step();
                i14 = 0;
            }
            executeFast11.dispose();
            queryFinalized4.dispose();
            int i17 = 0;
            SQLiteCursor queryFinalized5 = messagesStorage.database.queryFinalized("SELECT uid, mid FROM unread_push_messages WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast12 = messagesStorage.database.executeFast("UPDATE unread_push_messages SET mid = ? WHERE uid = ? AND mid = ?");
            while (queryFinalized5.next()) {
                long longValue13 = queryFinalized5.longValue(i17);
                int intValue29 = queryFinalized5.intValue(1);
                executeFast12.requery();
                executeFast12.bindInteger(1, intValue29);
                executeFast12.bindLong(2, longValue13);
                executeFast12.bindInteger(3, intValue29);
                executeFast12.step();
                i17 = 0;
            }
            executeFast12.dispose();
            queryFinalized5.dispose();
            if (arrayList != null) {
                SQLitePreparedStatement executeFast13 = messagesStorage.database.executeFast("UPDATE dialogs SET did = ? WHERE did = ?");
                SQLitePreparedStatement executeFast14 = messagesStorage.database.executeFast("UPDATE dialog_filter_pin_v2 SET peer = ? WHERE peer = ?");
                SQLitePreparedStatement executeFast15 = messagesStorage.database.executeFast("UPDATE dialog_filter_ep SET peer = ? WHERE peer = ?");
                int size = arrayList.size();
                for (int i18 = 0; i18 < size; i18++) {
                    long intValue30 = ((Integer) arrayList.get(i18)).intValue();
                    long makeEncryptedDialogId = DialogObject.makeEncryptedDialogId(intValue30);
                    long j = intValue30 << 32;
                    executeFast13.requery();
                    executeFast13.bindLong(1, makeEncryptedDialogId);
                    executeFast13.bindLong(2, j);
                    executeFast13.step();
                    executeFast14.requery();
                    executeFast14.bindLong(1, makeEncryptedDialogId);
                    executeFast14.bindLong(2, j);
                    executeFast14.step();
                    executeFast15.requery();
                    executeFast15.bindLong(1, makeEncryptedDialogId);
                    executeFast15.bindLong(2, j);
                    executeFast15.step();
                }
                executeFast13.dispose();
                executeFast14.dispose();
                executeFast15.dispose();
            }
            if (arrayList2 != null) {
                SQLitePreparedStatement executeFast16 = messagesStorage.database.executeFast("UPDATE dialogs SET did = ? WHERE did = ?");
                int size2 = arrayList2.size();
                for (int i19 = 0; i19 < size2; i19++) {
                    int intValue31 = ((Integer) arrayList2.get(i19)).intValue();
                    long makeFolderDialogId = DialogObject.makeFolderDialogId(intValue31);
                    executeFast16.requery();
                    executeFast16.bindLong(1, makeFolderDialogId);
                    executeFast16.bindLong(2, 8589934592L | intValue31);
                    executeFast16.step();
                }
                executeFast16.dispose();
            }
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_read_out_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS mid_out_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS task_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS send_state_idx_messages2;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mention_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS messages;").stepThis().dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("PRAGMA user_version = 84").stepThis().dispose();
            i4 = 84;
        }
        if (i4 == 84) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_v4(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, type))").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor = messagesStorage.database.queryFinalized("SELECT mid, uid, date, type, data FROM media_v3 WHERE 1", new Object[0]);
            } catch (Exception e8) {
                FileLog.e(e8);
                sQLiteCursor = null;
            }
            if (sQLiteCursor != null) {
                SQLitePreparedStatement executeFast17 = messagesStorage.database.executeFast("REPLACE INTO media_v4 VALUES(?, ?, ?, ?, ?)");
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue9 = sQLiteCursor.byteBufferValue(4);
                    if (byteBufferValue9 != null) {
                        int intValue32 = sQLiteCursor.intValue(0);
                        long longValue14 = sQLiteCursor.longValue(1);
                        if (((int) longValue14) == 0) {
                            longValue14 = DialogObject.makeEncryptedDialogId((int) (longValue14 >> 32));
                        }
                        int intValue33 = sQLiteCursor.intValue(2);
                        int intValue34 = sQLiteCursor.intValue(3);
                        executeFast17.requery();
                        executeFast17.bindInteger(1, intValue32);
                        executeFast17.bindLong(2, longValue14);
                        executeFast17.bindInteger(3, intValue33);
                        executeFast17.bindInteger(4, intValue34);
                        executeFast17.bindByteBuffer(5, byteBufferValue9);
                        executeFast17.step();
                        byteBufferValue9.reuse();
                    }
                }
                sQLiteCursor.dispose();
                executeFast17.dispose();
            }
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media_v3;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 85").stepThis().dispose();
            i4 = 85;
        }
        if (i4 == 85) {
            messagesStorage.executeNoException("ALTER TABLE messages_v2 ADD COLUMN reply_to_message_id INTEGER default 0");
            messagesStorage.executeNoException("ALTER TABLE scheduled_messages_v2 ADD COLUMN reply_to_message_id INTEGER default 0");
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_v2 ON messages_v2(mid, reply_to_message_id);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, reply_to_message_id);").stepThis().dispose();
            messagesStorage.executeNoException("UPDATE messages_v2 SET replydata = NULL");
            messagesStorage.executeNoException("UPDATE scheduled_messages_v2 SET replydata = NULL");
            messagesStorage.database.executeFast("PRAGMA user_version = 86").stepThis().dispose();
            i4 = 86;
        }
        if (i4 == 86) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS reactions(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 87").stepThis().dispose();
            i4 = 87;
        }
        if (i4 == 87) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_reactions INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE reaction_mentions(message_id INTEGER PRIMARY KEY, state INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 88").stepThis().dispose();
            i4 = 88;
        }
        if (i4 == 88 || i4 == 89) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS reaction_mentions;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS reaction_mentions(message_id INTEGER, state INTEGER, dialog_id INTEGER, PRIMARY KEY(dialog_id, message_id));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_did ON reaction_mentions(dialog_id);").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_type_date_idx_media_v3").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v4 ON media_v4(uid, mid, type, date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 90").stepThis().dispose();
            i4 = 90;
        }
        if (i4 == 90 || i4 == 91) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS downloading_documents;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE downloading_documents(data BLOB, hash INTEGER, id INTEGER, state INTEGER, date INTEGER, PRIMARY KEY(hash, id));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 92").stepThis().dispose();
            i4 = 92;
        }
        if (i4 == 92) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS attach_menu_bots(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 93").stepThis().dispose();
            i4 = 95;
        }
        if (i4 == 95 || i4 == 93) {
            messagesStorage.executeNoException("ALTER TABLE messages_v2 ADD COLUMN custom_params BLOB default NULL");
            messagesStorage.database.executeFast("PRAGMA user_version = 96").stepThis().dispose();
            i4 = 96;
        }
        if (i4 == 96) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS premium_promo(data BLOB, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("UPDATE stickers_v2 SET date = 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 97").stepThis().dispose();
            i4 = 97;
        }
        if (i4 == 97) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS stickers_featured;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER, premium INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 98").stepThis().dispose();
        }
        FileLog.d("MessagesStorage db migration finished");
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateDbToLastVersion$4();
            }
        });
    }

    public /* synthetic */ void lambda$updateDbToLastVersion$3() {
        this.databaseMigrationInProgress = true;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, Boolean.TRUE);
    }

    public /* synthetic */ void lambda$updateDbToLastVersion$4() {
        this.databaseMigrationInProgress = false;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, Boolean.FALSE);
    }

    private void executeNoException(String str) {
        try {
            this.database.executeFast(str).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void cleanupInternal(boolean z) {
        this.lastDateValue = 0;
        this.lastSeqValue = 0;
        this.lastPtsValue = 0;
        this.lastQtsValue = 0;
        this.lastSecretVersion = 0;
        this.mainUnreadCount = 0;
        this.archiveUnreadCount = 0;
        this.pendingMainUnreadCount = 0;
        this.pendingArchiveUnreadCount = 0;
        this.dialogFilters.clear();
        this.dialogFiltersMap.clear();
        this.unknownDialogsIds.clear();
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.secretPBytes = null;
        this.secretG = 0;
        SQLiteDatabase sQLiteDatabase = this.database;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
            this.database = null;
        }
        if (z) {
            File file = this.cacheFile;
            if (file != null) {
                file.delete();
                this.cacheFile = null;
            }
            File file2 = this.walCacheFile;
            if (file2 != null) {
                file2.delete();
                this.walCacheFile = null;
            }
            File file3 = this.shmCacheFile;
            if (file3 == null) {
                return;
            }
            file3.delete();
            this.shmCacheFile = null;
        }
    }

    public void cleanup(final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda184
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$cleanup$6(z);
            }
        });
    }

    public /* synthetic */ void lambda$cleanup$6(boolean z) {
        cleanupInternal(true);
        openDatabase(1);
        if (z) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$cleanup$5();
                }
            });
        }
    }

    public /* synthetic */ void lambda$cleanup$5() {
        getMessagesController().getDifference();
    }

    public void saveSecretParams(final int i, final int i2, final byte[] bArr) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveSecretParams$7(i, i2, bArr);
            }
        });
    }

    public /* synthetic */ void lambda$saveSecretParams$7(int i, int i2, byte[] bArr) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
            int i3 = 1;
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            if (bArr != null) {
                i3 = bArr.length;
            }
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
            if (bArr != null) {
                nativeByteBuffer.writeBytes(bArr);
            }
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void fixNotificationSettings() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$fixNotificationSettings$8();
            }
        });
    }

    public /* synthetic */ void lambda$fixNotificationSettings$8() {
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            Map<String, ?> all = MessagesController.getNotificationsSettings(this.currentAccount).getAll();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("notify2_")) {
                    Integer num = (Integer) entry.getValue();
                    if (num.intValue() == 2 || num.intValue() == 3) {
                        String replace = key.replace("notify2_", "");
                        long j = 1;
                        if (num.intValue() != 2) {
                            Integer num2 = (Integer) all.get("notifyuntil_" + replace);
                            if (num2 != null) {
                                j = 1 | (num2.intValue() << 32);
                            }
                        }
                        try {
                            longSparseArray.put(Long.parseLong(replace), Long.valueOf(j));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                for (int i = 0; i < longSparseArray.size(); i++) {
                    executeFast.requery();
                    executeFast.bindLong(1, longSparseArray.keyAt(i));
                    executeFast.bindLong(2, ((Long) longSparseArray.valueAt(i)).longValue());
                    executeFast.step();
                }
                executeFast.dispose();
                this.database.commitTransaction();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public long createPendingTask(final NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return 0L;
        }
        final long andAdd = this.lastTaskId.getAndAdd(1L);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda105
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$createPendingTask$9(andAdd, nativeByteBuffer);
            }
        });
        return andAdd;
    }

    public /* synthetic */ void lambda$createPendingTask$9(long j, NativeByteBuffer nativeByteBuffer) {
        try {
            try {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
                executeFast.bindLong(1, j);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            nativeByteBuffer.reuse();
        }
    }

    public void removePendingTask(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda63
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$removePendingTask$10(j);
            }
        });
    }

    public /* synthetic */ void lambda$removePendingTask$10(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM pending_tasks WHERE id = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadPendingTasks$30();
            }
        });
    }

    public /* synthetic */ void lambda$loadPendingTasks$30() {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
            while (queryFinalized.next()) {
                final long longValue = queryFinalized.longValue(0);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    int readInt32 = byteBufferValue.readInt32(false);
                    if (readInt32 != 100) {
                        switch (readInt32) {
                            case 0:
                                final TLRPC$Chat TLdeserialize = TLRPC$Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                if (TLdeserialize != null) {
                                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda157
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesStorage.this.lambda$loadPendingTasks$11(TLdeserialize, longValue);
                                        }
                                    });
                                    break;
                                }
                                break;
                            case 1:
                                final long readInt322 = byteBufferValue.readInt32(false);
                                final int readInt323 = byteBufferValue.readInt32(false);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda72
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$12(readInt322, readInt323, longValue);
                                    }
                                });
                                break;
                            case 2:
                            case 5:
                            case 8:
                            case 10:
                            case 14:
                                final TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
                                tLRPC$TL_dialog.id = byteBufferValue.readInt64(false);
                                tLRPC$TL_dialog.top_message = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.read_inbox_max_id = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.read_outbox_max_id = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.unread_count = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.last_message_date = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.pts = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.flags = byteBufferValue.readInt32(false);
                                if (readInt32 >= 5) {
                                    tLRPC$TL_dialog.pinned = byteBufferValue.readBool(false);
                                    tLRPC$TL_dialog.pinnedNum = byteBufferValue.readInt32(false);
                                }
                                if (readInt32 >= 8) {
                                    tLRPC$TL_dialog.unread_mentions_count = byteBufferValue.readInt32(false);
                                }
                                if (readInt32 >= 10) {
                                    tLRPC$TL_dialog.unread_mark = byteBufferValue.readBool(false);
                                }
                                if (readInt32 >= 14) {
                                    tLRPC$TL_dialog.folder_id = byteBufferValue.readInt32(false);
                                }
                                final TLRPC$InputPeer TLdeserialize2 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda162
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$13(tLRPC$TL_dialog, TLdeserialize2, longValue);
                                    }
                                });
                                break;
                            case 3:
                                getSendMessagesHelper().sendGame(TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), (TLRPC$TL_inputMediaGame) TLRPC$InputMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), byteBufferValue.readInt64(false), longValue);
                                break;
                            case 4:
                                final long readInt64 = byteBufferValue.readInt64(false);
                                final boolean readBool = byteBufferValue.readBool(false);
                                final TLRPC$InputPeer TLdeserialize3 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda112
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$14(readInt64, readBool, TLdeserialize3, longValue);
                                    }
                                });
                                break;
                            case 6:
                                final long readInt324 = byteBufferValue.readInt32(false);
                                final int readInt325 = byteBufferValue.readInt32(false);
                                final TLRPC$InputChannel TLdeserialize4 = TLRPC$InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda74
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$15(readInt324, readInt325, longValue, TLdeserialize4);
                                    }
                                });
                                break;
                            case 7:
                                final long readInt326 = byteBufferValue.readInt32(false);
                                int readInt327 = byteBufferValue.readInt32(false);
                                TLObject TLdeserialize5 = TLRPC$TL_messages_deleteMessages.TLdeserialize(byteBufferValue, readInt327, false);
                                final TLObject TLdeserialize6 = TLdeserialize5 == null ? TLRPC$TL_channels_deleteMessages.TLdeserialize(byteBufferValue, readInt327, false) : TLdeserialize5;
                                if (TLdeserialize6 == null) {
                                    removePendingTask(longValue);
                                    break;
                                } else {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda91
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesStorage.this.lambda$loadPendingTasks$17(readInt326, longValue, TLdeserialize6);
                                        }
                                    });
                                    break;
                                }
                            case 9:
                                final long readInt642 = byteBufferValue.readInt64(false);
                                final TLRPC$InputPeer TLdeserialize7 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda106
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$19(readInt642, TLdeserialize7, longValue);
                                    }
                                });
                                break;
                            case 11:
                                final int readInt328 = byteBufferValue.readInt32(false);
                                final long readInt329 = byteBufferValue.readInt32(false);
                                final int readInt3210 = byteBufferValue.readInt32(false);
                                final TLRPC$InputChannel TLdeserialize8 = readInt329 != 0 ? TLRPC$InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false) : null;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda76
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$20(readInt329, readInt328, TLdeserialize8, readInt3210, longValue);
                                    }
                                });
                                break;
                            case 12:
                            case R.styleable.MapAttrs_uiTiltGestures /* 19 */:
                            case R.styleable.MapAttrs_uiZoomControls /* 20 */:
                                removePendingTask(longValue);
                                break;
                            case 13:
                                final long readInt643 = byteBufferValue.readInt64(false);
                                final boolean readBool2 = byteBufferValue.readBool(false);
                                final int readInt3211 = byteBufferValue.readInt32(false);
                                final int readInt3212 = byteBufferValue.readInt32(false);
                                final boolean readBool3 = byteBufferValue.readBool(false);
                                final TLRPC$InputPeer TLdeserialize9 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda111
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$23(readInt643, readBool2, readInt3211, readInt3212, readBool3, TLdeserialize9, longValue);
                                    }
                                });
                                break;
                            case 15:
                                final TLRPC$InputPeer TLdeserialize10 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda169
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$24(TLdeserialize10, longValue);
                                    }
                                });
                                break;
                            case 16:
                                final int readInt3213 = byteBufferValue.readInt32(false);
                                int readInt3214 = byteBufferValue.readInt32(false);
                                final ArrayList arrayList = new ArrayList();
                                for (int i = 0; i < readInt3214; i++) {
                                    arrayList.add(TLRPC$InputDialogPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda59
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$25(readInt3213, arrayList, longValue);
                                    }
                                });
                                break;
                            case 17:
                                final int readInt3215 = byteBufferValue.readInt32(false);
                                int readInt3216 = byteBufferValue.readInt32(false);
                                final ArrayList arrayList2 = new ArrayList();
                                for (int i2 = 0; i2 < readInt3216; i2++) {
                                    arrayList2.add(TLRPC$TL_inputFolderPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda58
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$26(readInt3215, arrayList2, longValue);
                                    }
                                });
                                break;
                            case R.styleable.MapAttrs_uiScrollGesturesDuringRotateOrZoom /* 18 */:
                                final long readInt644 = byteBufferValue.readInt64(false);
                                byteBufferValue.readInt32(false);
                                final TLRPC$TL_messages_deleteScheduledMessages TLdeserialize11 = TLRPC$TL_messages_deleteScheduledMessages.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                if (TLdeserialize11 == null) {
                                    removePendingTask(longValue);
                                    break;
                                } else {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda89
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesStorage.this.lambda$loadPendingTasks$27(readInt644, longValue, TLdeserialize11);
                                        }
                                    });
                                    break;
                                }
                            case R.styleable.MapAttrs_uiZoomGestures /* 21 */:
                                final Theme.OverrideWallpaperInfo overrideWallpaperInfo = new Theme.OverrideWallpaperInfo();
                                byteBufferValue.readInt64(false);
                                overrideWallpaperInfo.isBlurred = byteBufferValue.readBool(false);
                                overrideWallpaperInfo.isMotion = byteBufferValue.readBool(false);
                                overrideWallpaperInfo.color = byteBufferValue.readInt32(false);
                                overrideWallpaperInfo.gradientColor1 = byteBufferValue.readInt32(false);
                                overrideWallpaperInfo.rotation = byteBufferValue.readInt32(false);
                                overrideWallpaperInfo.intensity = (float) byteBufferValue.readDouble(false);
                                final boolean readBool4 = byteBufferValue.readBool(false);
                                overrideWallpaperInfo.slug = byteBufferValue.readString(false);
                                overrideWallpaperInfo.originalFileName = byteBufferValue.readString(false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda182
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$22(overrideWallpaperInfo, readBool4, longValue);
                                    }
                                });
                                break;
                            case R.styleable.MapAttrs_useViewLifecycle /* 22 */:
                                final TLRPC$InputPeer TLdeserialize12 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda170
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$28(TLdeserialize12, longValue);
                                    }
                                });
                                break;
                            case R.styleable.MapAttrs_zOrderOnTop /* 23 */:
                                final long readInt645 = byteBufferValue.readInt64(false);
                                final int readInt3217 = byteBufferValue.readInt32(false);
                                final int readInt3218 = byteBufferValue.readInt32(false);
                                final TLRPC$InputChannel TLdeserialize13 = (DialogObject.isEncryptedDialog(readInt645) || !DialogObject.isChatDialog(readInt645) || !byteBufferValue.hasRemaining()) ? null : TLRPC$InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda77
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$21(readInt645, readInt3217, TLdeserialize13, readInt3218, longValue);
                                    }
                                });
                                break;
                            case 24:
                                final long readInt646 = byteBufferValue.readInt64(false);
                                int readInt3219 = byteBufferValue.readInt32(false);
                                TLObject TLdeserialize14 = TLRPC$TL_messages_deleteMessages.TLdeserialize(byteBufferValue, readInt3219, false);
                                final TLObject TLdeserialize15 = TLdeserialize14 == null ? TLRPC$TL_channels_deleteMessages.TLdeserialize(byteBufferValue, readInt3219, false) : TLdeserialize14;
                                if (TLdeserialize15 == null) {
                                    removePendingTask(longValue);
                                    break;
                                } else {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda90
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesStorage.this.lambda$loadPendingTasks$18(readInt646, longValue, TLdeserialize15);
                                        }
                                    });
                                    break;
                                }
                            case 25:
                                final long readInt647 = byteBufferValue.readInt64(false);
                                final int readInt3220 = byteBufferValue.readInt32(false);
                                final TLRPC$InputChannel TLdeserialize16 = TLRPC$InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda75
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$16(readInt647, readInt3220, longValue, TLdeserialize16);
                                    }
                                });
                                break;
                        }
                    } else {
                        final int readInt3221 = byteBufferValue.readInt32(false);
                        final boolean readBool5 = byteBufferValue.readBool(false);
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda61
                            @Override // java.lang.Runnable
                            public final void run() {
                                MessagesStorage.this.lambda$loadPendingTasks$29(readInt3221, readBool5, longValue);
                            }
                        });
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$loadPendingTasks$11(TLRPC$Chat tLRPC$Chat, long j) {
        getMessagesController().loadUnknownChannel(tLRPC$Chat, j);
    }

    public /* synthetic */ void lambda$loadPendingTasks$12(long j, int i, long j2) {
        getMessagesController().getChannelDifference(j, i, j2, null);
    }

    public /* synthetic */ void lambda$loadPendingTasks$13(TLRPC$Dialog tLRPC$Dialog, TLRPC$InputPeer tLRPC$InputPeer, long j) {
        getMessagesController().checkLastDialogMessage(tLRPC$Dialog, tLRPC$InputPeer, j);
    }

    public /* synthetic */ void lambda$loadPendingTasks$14(long j, boolean z, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        getMessagesController().pinDialog(j, z, tLRPC$InputPeer, j2);
    }

    public /* synthetic */ void lambda$loadPendingTasks$15(long j, int i, long j2, TLRPC$InputChannel tLRPC$InputChannel) {
        getMessagesController().getChannelDifference(j, i, j2, tLRPC$InputChannel);
    }

    public /* synthetic */ void lambda$loadPendingTasks$16(long j, int i, long j2, TLRPC$InputChannel tLRPC$InputChannel) {
        getMessagesController().getChannelDifference(j, i, j2, tLRPC$InputChannel);
    }

    public /* synthetic */ void lambda$loadPendingTasks$17(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, -j, true, false, false, j2, tLObject);
    }

    public /* synthetic */ void lambda$loadPendingTasks$18(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, j, true, false, false, j2, tLObject);
    }

    public /* synthetic */ void lambda$loadPendingTasks$19(long j, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        getMessagesController().markDialogAsUnread(j, tLRPC$InputPeer, j2);
    }

    public /* synthetic */ void lambda$loadPendingTasks$20(long j, int i, TLRPC$InputChannel tLRPC$InputChannel, int i2, long j2) {
        getMessagesController().markMessageAsRead2(-j, i, tLRPC$InputChannel, i2, j2);
    }

    public /* synthetic */ void lambda$loadPendingTasks$21(long j, int i, TLRPC$InputChannel tLRPC$InputChannel, int i2, long j2) {
        getMessagesController().markMessageAsRead2(j, i, tLRPC$InputChannel, i2, j2);
    }

    public /* synthetic */ void lambda$loadPendingTasks$22(Theme.OverrideWallpaperInfo overrideWallpaperInfo, boolean z, long j) {
        getMessagesController().saveWallpaperToServer(null, overrideWallpaperInfo, z, j);
    }

    public /* synthetic */ void lambda$loadPendingTasks$23(long j, boolean z, int i, int i2, boolean z2, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        getMessagesController().deleteDialog(j, z ? 1 : 0, i, i2, z2, tLRPC$InputPeer, j2);
    }

    public /* synthetic */ void lambda$loadPendingTasks$24(TLRPC$InputPeer tLRPC$InputPeer, long j) {
        getMessagesController().loadUnknownDialog(tLRPC$InputPeer, j);
    }

    public /* synthetic */ void lambda$loadPendingTasks$25(int i, ArrayList arrayList, long j) {
        getMessagesController().reorderPinnedDialogs(i, arrayList, j);
    }

    public /* synthetic */ void lambda$loadPendingTasks$26(int i, ArrayList arrayList, long j) {
        getMessagesController().addDialogToFolder(null, i, -1, arrayList, j);
    }

    public /* synthetic */ void lambda$loadPendingTasks$27(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, j, true, true, false, j2, tLObject);
    }

    public /* synthetic */ void lambda$loadPendingTasks$28(TLRPC$InputPeer tLRPC$InputPeer, long j) {
        getMessagesController().reloadMentionsCountForChannel(tLRPC$InputPeer, j);
    }

    public /* synthetic */ void lambda$loadPendingTasks$29(int i, boolean z, long j) {
        getSecretChatHelper().declineSecretChat(i, z, j);
    }

    public void saveChannelPts(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveChannelPts$31(i, j);
            }
        });
    }

    public /* synthetic */ void lambda$saveChannelPts$31(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, -j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: saveDiffParamsInternal */
    public void lambda$saveDiffParams$32(int i, int i2, int i3, int i4) {
        try {
            if (this.lastSavedSeq == i && this.lastSavedPts == i2 && this.lastSavedDate == i3 && this.lastQtsValue == i4) {
                return;
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            executeFast.bindInteger(3, i3);
            executeFast.bindInteger(4, i4);
            executeFast.step();
            executeFast.dispose();
            this.lastSavedSeq = i;
            this.lastSavedPts = i2;
            this.lastSavedDate = i3;
            this.lastSavedQts = i4;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveDiffParams(final int i, final int i2, final int i3, final int i4) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveDiffParams$32(i, i2, i3, i4);
            }
        });
    }

    public /* synthetic */ void lambda$updateMutedDialogsFiltersCounters$33() {
        resetAllUnreadCounters(true);
    }

    public void updateMutedDialogsFiltersCounters() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMutedDialogsFiltersCounters$33();
            }
        });
    }

    public void setDialogFlags(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda85
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogFlags$34(j, j2);
            }
        });
    }

    public /* synthetic */ void lambda$setDialogFlags$34(long j, long j2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT flags FROM dialog_settings WHERE did = " + j, new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
            queryFinalized.dispose();
            if (j2 == intValue) {
                return;
            }
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", Long.valueOf(j), Long.valueOf(j2))).stepThis().dispose();
            resetAllUnreadCounters(true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putPushMessage(final MessageObject messageObject) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda150
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putPushMessage$35(messageObject);
            }
        });
    }

    public /* synthetic */ void lambda$putPushMessage$35(MessageObject messageObject) {
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            int i = 0;
            if (messageObject.localType == 2) {
                i = 1;
            }
            if (messageObject.localChannel) {
                i |= 2;
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO unread_push_messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, messageObject.getDialogId());
            executeFast.bindInteger(2, messageObject.getId());
            executeFast.bindLong(3, messageObject.messageOwner.random_id);
            executeFast.bindInteger(4, messageObject.messageOwner.date);
            executeFast.bindByteBuffer(5, nativeByteBuffer);
            CharSequence charSequence = messageObject.messageText;
            if (charSequence == null) {
                executeFast.bindNull(6);
            } else {
                executeFast.bindString(6, charSequence.toString());
            }
            String str = messageObject.localName;
            if (str == null) {
                executeFast.bindNull(7);
            } else {
                executeFast.bindString(7, str);
            }
            String str2 = messageObject.localUserName;
            if (str2 == null) {
                executeFast.bindNull(8);
            } else {
                executeFast.bindString(8, str2);
            }
            executeFast.bindInteger(9, i);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearLocalDatabase() {
        getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearLocalDatabase$37();
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x0211 A[Catch: all -> 0x0260, Exception -> 0x0262, TryCatch #4 {Exception -> 0x0262, blocks: (B:3:0x0004, B:4:0x0043, B:6:0x0049, B:8:0x0053, B:9:0x005b, B:10:0x006e, B:12:0x0074, B:14:0x0098, B:16:0x009e, B:19:0x00a8, B:21:0x00c5, B:43:0x0144, B:44:0x0147, B:46:0x0211, B:49:0x0220, B:50:0x0223, B:51:0x022a), top: B:61:0x0004, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x021b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$clearLocalDatabase$37() {
        /*
            Method dump skipped, instructions count: 634
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$clearLocalDatabase$37():void");
    }

    public /* synthetic */ void lambda$clearLocalDatabase$36() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didClearDatabase, new Object[0]);
        getMediaDataController().loadAttachMenuBots(false, true);
    }

    /* loaded from: classes.dex */
    public static class ReadDialog {
        public int date;
        public int lastMid;
        public int unreadCount;

        private ReadDialog() {
        }
    }

    public void readAllDialogs(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$readAllDialogs$39(i);
            }
        });
    }

    public /* synthetic */ void lambda$readAllDialogs$39(int i) {
        SQLiteCursor sQLiteCursor;
        try {
            ArrayList<Long> arrayList = new ArrayList<>();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            final LongSparseArray longSparseArray = new LongSparseArray();
            if (i >= 0) {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count > 0 AND folder_id = %1$d", Integer.valueOf(i)), new Object[0]);
            } else {
                sQLiteCursor = this.database.queryFinalized("SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count > 0", new Object[0]);
            }
            while (sQLiteCursor.next()) {
                long longValue = sQLiteCursor.longValue(0);
                if (!DialogObject.isFolderDialogId(longValue)) {
                    ReadDialog readDialog = new ReadDialog();
                    readDialog.lastMid = sQLiteCursor.intValue(1);
                    readDialog.unreadCount = sQLiteCursor.intValue(2);
                    readDialog.date = sQLiteCursor.intValue(3);
                    longSparseArray.put(longValue, readDialog);
                    if (!DialogObject.isEncryptedDialog(longValue)) {
                        if (DialogObject.isChatDialog(longValue)) {
                            long j = -longValue;
                            if (!arrayList2.contains(Long.valueOf(j))) {
                                arrayList2.add(Long.valueOf(j));
                            }
                        } else if (!arrayList.contains(Long.valueOf(longValue))) {
                            arrayList.add(Long.valueOf(longValue));
                        }
                    } else {
                        int encryptedChatId = DialogObject.getEncryptedChatId(longValue);
                        if (!arrayList3.contains(Integer.valueOf(encryptedChatId))) {
                            arrayList3.add(Integer.valueOf(encryptedChatId));
                        }
                    }
                }
            }
            sQLiteCursor.dispose();
            final ArrayList<TLRPC$User> arrayList4 = new ArrayList<>();
            final ArrayList<TLRPC$Chat> arrayList5 = new ArrayList<>();
            final ArrayList<TLRPC$EncryptedChat> arrayList6 = new ArrayList<>();
            if (!arrayList3.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", arrayList3), arrayList6, arrayList);
            }
            if (!arrayList.isEmpty()) {
                getUsersInternal(TextUtils.join(",", arrayList), arrayList4);
            }
            if (!arrayList2.isEmpty()) {
                getChatsInternal(TextUtils.join(",", arrayList2), arrayList5);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda145
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$readAllDialogs$38(arrayList4, arrayList5, arrayList6, longSparseArray);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$readAllDialogs$38(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < longSparseArray.size(); i++) {
            long keyAt = longSparseArray.keyAt(i);
            ReadDialog readDialog = (ReadDialog) longSparseArray.valueAt(i);
            MessagesController messagesController = getMessagesController();
            int i2 = readDialog.lastMid;
            messagesController.markDialogAsRead(keyAt, i2, i2, readDialog.date, false, 0, readDialog.unreadCount, true, 0);
        }
    }

    private TLRPC$messages_Dialogs loadDialogsByIds(String str, ArrayList<Long> arrayList, ArrayList<Long> arrayList2, ArrayList<Integer> arrayList3) throws Exception {
        int i;
        TLRPC$Message tLRPC$Message;
        NativeByteBuffer byteBufferValue;
        TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs = new TLRPC$TL_messages_dialogs();
        LongSparseArray longSparseArray = new LongSparseArray();
        boolean z = false;
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, s.flags, m.date, d.pts, d.inbox_max, d.outbox_max, m.replydata, d.pinned, d.unread_count_i, d.flags, d.folder_id, d.data, d.unread_reactions FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.did IN (%s) ORDER BY d.pinned DESC, d.date DESC", str), new Object[0]);
        while (true) {
            i = 2;
            if (!queryFinalized.next()) {
                break;
            }
            int i2 = z ? 1 : 0;
            int i3 = z ? 1 : 0;
            long longValue = queryFinalized.longValue(i2);
            TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
            tLRPC$TL_dialog.id = longValue;
            tLRPC$TL_dialog.top_message = queryFinalized.intValue(1);
            tLRPC$TL_dialog.unread_count = queryFinalized.intValue(2);
            tLRPC$TL_dialog.last_message_date = queryFinalized.intValue(3);
            int intValue = queryFinalized.intValue(10);
            tLRPC$TL_dialog.pts = intValue;
            tLRPC$TL_dialog.flags = (intValue == 0 || DialogObject.isUserDialog(tLRPC$TL_dialog.id)) ? 0 : 1;
            tLRPC$TL_dialog.read_inbox_max_id = queryFinalized.intValue(11);
            tLRPC$TL_dialog.read_outbox_max_id = queryFinalized.intValue(12);
            int intValue2 = queryFinalized.intValue(14);
            tLRPC$TL_dialog.pinnedNum = intValue2;
            tLRPC$TL_dialog.pinned = intValue2 != 0;
            tLRPC$TL_dialog.unread_mentions_count = queryFinalized.intValue(15);
            tLRPC$TL_dialog.unread_mark = (queryFinalized.intValue(16) & 1) != 0;
            long longValue2 = queryFinalized.longValue(8);
            TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
            tLRPC$TL_dialog.notify_settings = tLRPC$TL_peerNotifySettings;
            if ((((int) longValue2) & 1) != 0) {
                int i4 = (int) (longValue2 >> 32);
                tLRPC$TL_peerNotifySettings.mute_until = i4;
                if (i4 == 0) {
                    tLRPC$TL_peerNotifySettings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                }
            }
            tLRPC$TL_dialog.folder_id = queryFinalized.intValue(17);
            tLRPC$TL_dialog.unread_reactions_count = queryFinalized.intValue(19);
            tLRPC$TL_messages_dialogs.dialogs.add(tLRPC$TL_dialog);
            NativeByteBuffer byteBufferValue2 = queryFinalized.byteBufferValue(4);
            if (byteBufferValue2 != null) {
                TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(z), z);
                if (TLdeserialize != null) {
                    TLdeserialize.readAttachPath(byteBufferValue2, getUserConfig().clientUserId);
                    byteBufferValue2.reuse();
                    MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(5));
                    TLdeserialize.id = queryFinalized.intValue(6);
                    int intValue3 = queryFinalized.intValue(9);
                    if (intValue3 != 0) {
                        tLRPC$TL_dialog.last_message_date = intValue3;
                    }
                    TLdeserialize.send_state = queryFinalized.intValue(7);
                    TLdeserialize.dialog_id = tLRPC$TL_dialog.id;
                    tLRPC$TL_messages_dialogs.messages.add(TLdeserialize);
                    addUsersAndChatsFromMessage(TLdeserialize, arrayList, arrayList2);
                    try {
                        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = TLdeserialize.reply_to;
                        if (tLRPC$TL_messageReplyHeader != null && tLRPC$TL_messageReplyHeader.reply_to_msg_id != 0) {
                            TLRPC$MessageAction tLRPC$MessageAction = TLdeserialize.action;
                            if ((tLRPC$MessageAction instanceof TLRPC$TL_messageActionPinMessage) || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPaymentSent) || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionGameScore)) {
                                if (queryFinalized.isNull(13) || (byteBufferValue = queryFinalized.byteBufferValue(13)) == null) {
                                    tLRPC$Message = TLdeserialize;
                                } else {
                                    TLRPC$Message TLdeserialize2 = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z), z);
                                    TLdeserialize.replyMessage = TLdeserialize2;
                                    TLdeserialize2.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                                    byteBufferValue.reuse();
                                    tLRPC$Message = TLdeserialize;
                                    TLRPC$Message tLRPC$Message2 = tLRPC$Message.replyMessage;
                                    if (tLRPC$Message2 != null) {
                                        addUsersAndChatsFromMessage(tLRPC$Message2, arrayList, arrayList2);
                                    }
                                }
                                if (tLRPC$Message.replyMessage == null) {
                                    longSparseArray.put(tLRPC$TL_dialog.id, tLRPC$Message);
                                }
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else {
                    byteBufferValue2.reuse();
                }
            }
            if (DialogObject.isEncryptedDialog(longValue)) {
                int encryptedChatId = DialogObject.getEncryptedChatId(longValue);
                if (!arrayList3.contains(Integer.valueOf(encryptedChatId))) {
                    arrayList3.add(Integer.valueOf(encryptedChatId));
                }
            } else if (DialogObject.isUserDialog(longValue)) {
                if (!arrayList.contains(Long.valueOf(longValue))) {
                    arrayList.add(Long.valueOf(longValue));
                }
            } else {
                long j = -longValue;
                if (!arrayList2.contains(Long.valueOf(j))) {
                    arrayList2.add(Long.valueOf(j));
                }
            }
            z = false;
        }
        queryFinalized.dispose();
        if (!longSparseArray.isEmpty()) {
            int size = longSparseArray.size();
            int i5 = 0;
            while (i5 < size) {
                long keyAt = longSparseArray.keyAt(i5);
                TLRPC$Message tLRPC$Message3 = (TLRPC$Message) longSparseArray.valueAt(i5);
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                Object[] objArr = new Object[i];
                objArr[0] = Integer.valueOf(tLRPC$Message3.id);
                objArr[1] = Long.valueOf(keyAt);
                SQLiteCursor queryFinalized2 = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT data, mid, date, uid FROM messages_v2 WHERE mid = %d and uid = %d", objArr), new Object[0]);
                while (queryFinalized2.next()) {
                    NativeByteBuffer byteBufferValue3 = queryFinalized2.byteBufferValue(0);
                    if (byteBufferValue3 != null) {
                        TLRPC$Message TLdeserialize3 = TLRPC$Message.TLdeserialize(byteBufferValue3, byteBufferValue3.readInt32(false), false);
                        TLdeserialize3.readAttachPath(byteBufferValue3, getUserConfig().clientUserId);
                        byteBufferValue3.reuse();
                        TLdeserialize3.id = queryFinalized2.intValue(1);
                        TLdeserialize3.date = queryFinalized2.intValue(2);
                        TLdeserialize3.dialog_id = queryFinalized2.longValue(3);
                        addUsersAndChatsFromMessage(TLdeserialize3, arrayList, arrayList2);
                        tLRPC$Message3.replyMessage = TLdeserialize3;
                        TLdeserialize3.dialog_id = tLRPC$Message3.dialog_id;
                    }
                }
                queryFinalized2.dispose();
                i5++;
                i = 2;
            }
        }
        return tLRPC$TL_messages_dialogs;
    }

    private void loadDialogFilters() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadDialogFilters$41();
            }
        });
    }

    public /* synthetic */ void lambda$loadDialogFilters$41() {
        TLRPC$messages_Dialogs tLRPC$messages_Dialogs;
        SQLiteCursor sQLiteCursor;
        try {
            ArrayList<Long> arrayList = new ArrayList<>();
            ArrayList<Long> arrayList2 = new ArrayList<>();
            ArrayList<Integer> arrayList3 = new ArrayList<>();
            ArrayList arrayList4 = new ArrayList();
            SparseArray sparseArray = new SparseArray();
            arrayList.add(Long.valueOf(getUserConfig().getClientUserId()));
            int i = 0;
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT id, ord, unread_count, flags, title FROM dialog_filter WHERE 1", new Object[0]);
            boolean z = false;
            boolean z2 = false;
            while (true) {
                if (!queryFinalized.next()) {
                    break;
                }
                MessagesController.DialogFilter dialogFilter = new MessagesController.DialogFilter();
                dialogFilter.id = queryFinalized.intValue(i);
                dialogFilter.order = queryFinalized.intValue(1);
                dialogFilter.unreadCount = -1;
                dialogFilter.pendingUnreadCount = -1;
                dialogFilter.flags = queryFinalized.intValue(3);
                dialogFilter.name = queryFinalized.stringValue(4);
                this.dialogFilters.add(dialogFilter);
                this.dialogFiltersMap.put(dialogFilter.id, dialogFilter);
                sparseArray.put(dialogFilter.id, dialogFilter);
                if (dialogFilter.pendingUnreadCount < 0) {
                    z2 = true;
                }
                int i2 = 0;
                for (int i3 = 2; i2 < i3; i3 = 2) {
                    if (i2 == 0) {
                        sQLiteCursor = this.database.queryFinalized("SELECT peer, pin FROM dialog_filter_pin_v2 WHERE id = " + dialogFilter.id, new Object[i]);
                    } else {
                        sQLiteCursor = this.database.queryFinalized("SELECT peer FROM dialog_filter_ep WHERE id = " + dialogFilter.id, new Object[i]);
                    }
                    while (sQLiteCursor.next()) {
                        long longValue = sQLiteCursor.longValue(i);
                        if (i2 == 0) {
                            if (!DialogObject.isEncryptedDialog(longValue)) {
                                dialogFilter.alwaysShow.add(Long.valueOf(longValue));
                            }
                            int intValue = sQLiteCursor.intValue(1);
                            if (intValue != Integer.MIN_VALUE) {
                                dialogFilter.pinnedDialogs.put(longValue, intValue);
                                if (!arrayList4.contains(Long.valueOf(longValue))) {
                                    arrayList4.add(Long.valueOf(longValue));
                                }
                            }
                        } else if (!DialogObject.isEncryptedDialog(longValue)) {
                            dialogFilter.neverShow.add(Long.valueOf(longValue));
                        }
                        if (DialogObject.isChatDialog(longValue)) {
                            long j = -longValue;
                            if (!arrayList2.contains(Long.valueOf(j))) {
                                arrayList2.add(Long.valueOf(j));
                            }
                        } else if (DialogObject.isUserDialog(longValue)) {
                            if (!arrayList.contains(Long.valueOf(longValue))) {
                                arrayList.add(Long.valueOf(longValue));
                            }
                        } else {
                            int encryptedChatId = DialogObject.getEncryptedChatId(longValue);
                            if (!arrayList3.contains(Integer.valueOf(encryptedChatId))) {
                                arrayList3.add(Integer.valueOf(encryptedChatId));
                            }
                        }
                        i = 0;
                    }
                    sQLiteCursor.dispose();
                    i2++;
                    i = 0;
                }
                if (dialogFilter.id == 0) {
                    z = true;
                }
                i = 0;
            }
            queryFinalized.dispose();
            if (!z) {
                MessagesController.DialogFilter dialogFilter2 = new MessagesController.DialogFilter();
                dialogFilter2.id = 0;
                dialogFilter2.order = 0;
                dialogFilter2.name = "ALL_CHATS";
                for (int i4 = 0; i4 < this.dialogFilters.size(); i4++) {
                    this.dialogFilters.get(i4).order++;
                }
                this.dialogFilters.add(dialogFilter2);
                this.dialogFiltersMap.put(dialogFilter2.id, dialogFilter2);
                sparseArray.put(dialogFilter2.id, dialogFilter2);
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO dialog_filter VALUES(?, ?, ?, ?, ?)");
                executeFast.bindInteger(1, dialogFilter2.id);
                executeFast.bindInteger(2, dialogFilter2.order);
                executeFast.bindInteger(3, dialogFilter2.unreadCount);
                executeFast.bindInteger(4, dialogFilter2.flags);
                executeFast.bindString(5, dialogFilter2.name);
                executeFast.stepThis().dispose();
            }
            Collections.sort(this.dialogFilters, MessagesStorage$$ExternalSyntheticLambda198.INSTANCE);
            if (z2) {
                calcUnreadCounters(true);
            }
            if (!arrayList4.isEmpty()) {
                tLRPC$messages_Dialogs = loadDialogsByIds(TextUtils.join(",", arrayList4), arrayList, arrayList2, arrayList3);
            } else {
                tLRPC$messages_Dialogs = new TLRPC$TL_messages_dialogs();
            }
            TLRPC$messages_Dialogs tLRPC$messages_Dialogs2 = tLRPC$messages_Dialogs;
            ArrayList<TLRPC$User> arrayList5 = new ArrayList<>();
            ArrayList<TLRPC$Chat> arrayList6 = new ArrayList<>();
            ArrayList<TLRPC$EncryptedChat> arrayList7 = new ArrayList<>();
            if (!arrayList3.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", arrayList3), arrayList7, arrayList);
            }
            if (!arrayList.isEmpty()) {
                getUsersInternal(TextUtils.join(",", arrayList), arrayList5);
            }
            if (!arrayList2.isEmpty()) {
                getChatsInternal(TextUtils.join(",", arrayList2), arrayList6);
            }
            getMessagesController().processLoadedDialogFilters(new ArrayList<>(this.dialogFilters), tLRPC$messages_Dialogs2, null, arrayList5, arrayList6, arrayList7, 0);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ int lambda$loadDialogFilters$40(MessagesController.DialogFilter dialogFilter, MessagesController.DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:226:0x04b4, code lost:
        if (r14.indexOfKey(r3.id) >= 0) goto L227;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0573, code lost:
        if (r17 == 0) goto L277;
     */
    /* JADX WARN: Removed duplicated region for block: B:115:0x0304  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x0445 A[Catch: Exception -> 0x063c, TryCatch #0 {Exception -> 0x063c, blocks: (B:7:0x000a, B:8:0x002b, B:9:0x002e, B:10:0x0065, B:12:0x006c, B:14:0x007f, B:16:0x008a, B:17:0x0093, B:19:0x009c, B:21:0x00aa, B:22:0x00b2, B:24:0x00b8, B:26:0x00c2, B:27:0x00ca, B:29:0x00d5, B:31:0x00de, B:34:0x0107, B:36:0x0115, B:38:0x0137, B:42:0x014c, B:43:0x0153, B:45:0x0157, B:46:0x0162, B:48:0x0166, B:51:0x016b, B:52:0x0176, B:53:0x0180, B:54:0x0196, B:56:0x01a5, B:58:0x01bc, B:60:0x01ca, B:61:0x01dc, B:63:0x01e5, B:66:0x01f8, B:68:0x020d, B:72:0x021c, B:73:0x0223, B:75:0x0227, B:78:0x022c, B:79:0x0237, B:80:0x0241, B:81:0x024f, B:83:0x0256, B:85:0x025c, B:87:0x026e, B:89:0x027a, B:92:0x0281, B:94:0x0295, B:99:0x02a5, B:102:0x02b0, B:103:0x02b8, B:105:0x02be, B:107:0x02c2, B:108:0x02cd, B:109:0x02d7, B:110:0x02df, B:111:0x02f1, B:112:0x02f7, B:113:0x0300, B:116:0x0306, B:119:0x0318, B:120:0x031b, B:122:0x0320, B:124:0x0328, B:125:0x032b, B:126:0x032d, B:127:0x032f, B:128:0x0332, B:130:0x0337, B:132:0x033c, B:134:0x0349, B:137:0x0353, B:139:0x0358, B:141:0x0366, B:143:0x036d, B:145:0x0372, B:147:0x0377, B:149:0x0384, B:150:0x038a, B:152:0x038f, B:154:0x039d, B:155:0x03a2, B:157:0x03a7, B:159:0x03ac, B:161:0x03b9, B:162:0x03bf, B:164:0x03c4, B:166:0x03d2, B:167:0x03d7, B:169:0x03dc, B:171:0x03e1, B:173:0x03ee, B:174:0x03f4, B:176:0x03f9, B:178:0x0407, B:179:0x040c, B:181:0x0411, B:183:0x0416, B:185:0x0423, B:186:0x0429, B:188:0x042e, B:190:0x043c, B:193:0x0445, B:195:0x044f, B:202:0x0471, B:206:0x0480, B:209:0x048a, B:211:0x048e, B:212:0x0491, B:214:0x0495, B:217:0x049a, B:218:0x049d, B:219:0x049f, B:221:0x04a2, B:223:0x04a7, B:225:0x04ac, B:227:0x04b6, B:229:0x04bb, B:231:0x04c0, B:235:0x04cd, B:237:0x04db, B:239:0x04ea, B:241:0x04f0, B:243:0x04f4, B:244:0x04f7, B:245:0x04f9, B:247:0x04fc, B:248:0x04ff, B:250:0x0504, B:252:0x050d, B:255:0x0519, B:257:0x051e, B:260:0x0528, B:261:0x0533, B:263:0x0540, B:271:0x055d, B:275:0x056c, B:278:0x0578, B:280:0x057c, B:281:0x057f, B:283:0x0583, B:286:0x0588, B:287:0x058b, B:288:0x058d, B:290:0x0590, B:292:0x0595, B:296:0x05a2, B:298:0x05a7, B:300:0x05af, B:302:0x05b6, B:304:0x05c2, B:306:0x05cf, B:308:0x05d5, B:310:0x05d9, B:311:0x05dc, B:312:0x05de, B:314:0x05e1, B:316:0x05e6, B:318:0x05ef, B:320:0x05f4, B:322:0x05fd, B:324:0x0608, B:325:0x060a, B:326:0x0612, B:328:0x0618, B:332:0x0623, B:334:0x0627, B:335:0x062a, B:337:0x062e, B:339:0x0632), top: B:344:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:330:0x061d  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x025c A[Catch: Exception -> 0x063c, TryCatch #0 {Exception -> 0x063c, blocks: (B:7:0x000a, B:8:0x002b, B:9:0x002e, B:10:0x0065, B:12:0x006c, B:14:0x007f, B:16:0x008a, B:17:0x0093, B:19:0x009c, B:21:0x00aa, B:22:0x00b2, B:24:0x00b8, B:26:0x00c2, B:27:0x00ca, B:29:0x00d5, B:31:0x00de, B:34:0x0107, B:36:0x0115, B:38:0x0137, B:42:0x014c, B:43:0x0153, B:45:0x0157, B:46:0x0162, B:48:0x0166, B:51:0x016b, B:52:0x0176, B:53:0x0180, B:54:0x0196, B:56:0x01a5, B:58:0x01bc, B:60:0x01ca, B:61:0x01dc, B:63:0x01e5, B:66:0x01f8, B:68:0x020d, B:72:0x021c, B:73:0x0223, B:75:0x0227, B:78:0x022c, B:79:0x0237, B:80:0x0241, B:81:0x024f, B:83:0x0256, B:85:0x025c, B:87:0x026e, B:89:0x027a, B:92:0x0281, B:94:0x0295, B:99:0x02a5, B:102:0x02b0, B:103:0x02b8, B:105:0x02be, B:107:0x02c2, B:108:0x02cd, B:109:0x02d7, B:110:0x02df, B:111:0x02f1, B:112:0x02f7, B:113:0x0300, B:116:0x0306, B:119:0x0318, B:120:0x031b, B:122:0x0320, B:124:0x0328, B:125:0x032b, B:126:0x032d, B:127:0x032f, B:128:0x0332, B:130:0x0337, B:132:0x033c, B:134:0x0349, B:137:0x0353, B:139:0x0358, B:141:0x0366, B:143:0x036d, B:145:0x0372, B:147:0x0377, B:149:0x0384, B:150:0x038a, B:152:0x038f, B:154:0x039d, B:155:0x03a2, B:157:0x03a7, B:159:0x03ac, B:161:0x03b9, B:162:0x03bf, B:164:0x03c4, B:166:0x03d2, B:167:0x03d7, B:169:0x03dc, B:171:0x03e1, B:173:0x03ee, B:174:0x03f4, B:176:0x03f9, B:178:0x0407, B:179:0x040c, B:181:0x0411, B:183:0x0416, B:185:0x0423, B:186:0x0429, B:188:0x042e, B:190:0x043c, B:193:0x0445, B:195:0x044f, B:202:0x0471, B:206:0x0480, B:209:0x048a, B:211:0x048e, B:212:0x0491, B:214:0x0495, B:217:0x049a, B:218:0x049d, B:219:0x049f, B:221:0x04a2, B:223:0x04a7, B:225:0x04ac, B:227:0x04b6, B:229:0x04bb, B:231:0x04c0, B:235:0x04cd, B:237:0x04db, B:239:0x04ea, B:241:0x04f0, B:243:0x04f4, B:244:0x04f7, B:245:0x04f9, B:247:0x04fc, B:248:0x04ff, B:250:0x0504, B:252:0x050d, B:255:0x0519, B:257:0x051e, B:260:0x0528, B:261:0x0533, B:263:0x0540, B:271:0x055d, B:275:0x056c, B:278:0x0578, B:280:0x057c, B:281:0x057f, B:283:0x0583, B:286:0x0588, B:287:0x058b, B:288:0x058d, B:290:0x0590, B:292:0x0595, B:296:0x05a2, B:298:0x05a7, B:300:0x05af, B:302:0x05b6, B:304:0x05c2, B:306:0x05cf, B:308:0x05d5, B:310:0x05d9, B:311:0x05dc, B:312:0x05de, B:314:0x05e1, B:316:0x05e6, B:318:0x05ef, B:320:0x05f4, B:322:0x05fd, B:324:0x0608, B:325:0x060a, B:326:0x0612, B:328:0x0618, B:332:0x0623, B:334:0x0627, B:335:0x062a, B:337:0x062e, B:339:0x0632), top: B:344:0x000a }] */
    /* JADX WARN: Type inference failed for: r6v108, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v99, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void calcUnreadCounters(boolean r25) {
        /*
            Method dump skipped, instructions count: 1601
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.calcUnreadCounters(boolean):void");
    }

    private void saveDialogFilterInternal(MessagesController.DialogFilter dialogFilter, boolean z, boolean z2) {
        try {
            if (!this.dialogFilters.contains(dialogFilter)) {
                if (z) {
                    this.dialogFilters.add(0, dialogFilter);
                } else {
                    this.dialogFilters.add(dialogFilter);
                }
                this.dialogFiltersMap.put(dialogFilter.id, dialogFilter);
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO dialog_filter VALUES(?, ?, ?, ?, ?)");
            executeFast.bindInteger(1, dialogFilter.id);
            executeFast.bindInteger(2, dialogFilter.order);
            executeFast.bindInteger(3, dialogFilter.unreadCount);
            executeFast.bindInteger(4, dialogFilter.flags);
            executeFast.bindString(5, dialogFilter.id == 0 ? "ALL_CHATS" : dialogFilter.name);
            executeFast.step();
            executeFast.dispose();
            if (!z2) {
                return;
            }
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM dialog_filter_ep WHERE id = " + dialogFilter.id).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase2 = this.database;
            sQLiteDatabase2.executeFast("DELETE FROM dialog_filter_pin_v2 WHERE id = " + dialogFilter.id).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO dialog_filter_pin_v2 VALUES(?, ?, ?)");
            int size = dialogFilter.alwaysShow.size();
            for (int i = 0; i < size; i++) {
                long longValue = dialogFilter.alwaysShow.get(i).longValue();
                executeFast2.requery();
                executeFast2.bindInteger(1, dialogFilter.id);
                executeFast2.bindLong(2, longValue);
                executeFast2.bindInteger(3, dialogFilter.pinnedDialogs.get(longValue, Integer.MIN_VALUE));
                executeFast2.step();
            }
            int size2 = dialogFilter.pinnedDialogs.size();
            for (int i2 = 0; i2 < size2; i2++) {
                long keyAt = dialogFilter.pinnedDialogs.keyAt(i2);
                if (DialogObject.isEncryptedDialog(keyAt)) {
                    executeFast2.requery();
                    executeFast2.bindInteger(1, dialogFilter.id);
                    executeFast2.bindLong(2, keyAt);
                    executeFast2.bindInteger(3, dialogFilter.pinnedDialogs.valueAt(i2));
                    executeFast2.step();
                }
            }
            executeFast2.dispose();
            SQLitePreparedStatement executeFast3 = this.database.executeFast("REPLACE INTO dialog_filter_ep VALUES(?, ?)");
            int size3 = dialogFilter.neverShow.size();
            for (int i3 = 0; i3 < size3; i3++) {
                executeFast3.requery();
                executeFast3.bindInteger(1, dialogFilter.id);
                executeFast3.bindLong(2, dialogFilter.neverShow.get(i3).longValue());
                executeFast3.step();
            }
            executeFast3.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void checkLoadedRemoteFilters(final TLRPC$Vector tLRPC$Vector) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda177
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$checkLoadedRemoteFilters$43(tLRPC$Vector);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:158:0x0382 A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:67:0x0168, B:68:0x0177, B:72:0x0189, B:74:0x0192, B:77:0x019f, B:78:0x01ad, B:83:0x01b8, B:84:0x01bb, B:87:0x01c1, B:88:0x01c4, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:95:0x01f3, B:98:0x01fa, B:99:0x01fc, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:141:0x0310, B:144:0x031a, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:154:0x0351, B:155:0x035d, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:180:0x0413, B:183:0x041a, B:184:0x041c, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:194:0x0469, B:195:0x046c, B:197:0x0470, B:198:0x0473, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:210:0x04a9, B:213:0x04b0, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:221:0x04de, B:222:0x04e8, B:223:0x04fa, B:224:0x0503, B:225:0x0519, B:228:0x052e), top: B:258:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:159:0x0391  */
    /* JADX WARN: Removed duplicated region for block: B:162:0x039b A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:67:0x0168, B:68:0x0177, B:72:0x0189, B:74:0x0192, B:77:0x019f, B:78:0x01ad, B:83:0x01b8, B:84:0x01bb, B:87:0x01c1, B:88:0x01c4, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:95:0x01f3, B:98:0x01fa, B:99:0x01fc, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:141:0x0310, B:144:0x031a, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:154:0x0351, B:155:0x035d, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:180:0x0413, B:183:0x041a, B:184:0x041c, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:194:0x0469, B:195:0x046c, B:197:0x0470, B:198:0x0473, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:210:0x04a9, B:213:0x04b0, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:221:0x04de, B:222:0x04e8, B:223:0x04fa, B:224:0x0503, B:225:0x0519, B:228:0x052e), top: B:258:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:163:0x03a7  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x03ad A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:67:0x0168, B:68:0x0177, B:72:0x0189, B:74:0x0192, B:77:0x019f, B:78:0x01ad, B:83:0x01b8, B:84:0x01bb, B:87:0x01c1, B:88:0x01c4, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:95:0x01f3, B:98:0x01fa, B:99:0x01fc, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:141:0x0310, B:144:0x031a, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:154:0x0351, B:155:0x035d, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:180:0x0413, B:183:0x041a, B:184:0x041c, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:194:0x0469, B:195:0x046c, B:197:0x0470, B:198:0x0473, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:210:0x04a9, B:213:0x04b0, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:221:0x04de, B:222:0x04e8, B:223:0x04fa, B:224:0x0503, B:225:0x0519, B:228:0x052e), top: B:258:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:166:0x03b3  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x03b7 A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:67:0x0168, B:68:0x0177, B:72:0x0189, B:74:0x0192, B:77:0x019f, B:78:0x01ad, B:83:0x01b8, B:84:0x01bb, B:87:0x01c1, B:88:0x01c4, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:95:0x01f3, B:98:0x01fa, B:99:0x01fc, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:141:0x0310, B:144:0x031a, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:154:0x0351, B:155:0x035d, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:180:0x0413, B:183:0x041a, B:184:0x041c, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:194:0x0469, B:195:0x046c, B:197:0x0470, B:198:0x0473, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:210:0x04a9, B:213:0x04b0, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:221:0x04de, B:222:0x04e8, B:223:0x04fa, B:224:0x0503, B:225:0x0519, B:228:0x052e), top: B:258:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:169:0x03c3  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0192 A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:67:0x0168, B:68:0x0177, B:72:0x0189, B:74:0x0192, B:77:0x019f, B:78:0x01ad, B:83:0x01b8, B:84:0x01bb, B:87:0x01c1, B:88:0x01c4, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:95:0x01f3, B:98:0x01fa, B:99:0x01fc, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:141:0x0310, B:144:0x031a, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:154:0x0351, B:155:0x035d, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:180:0x0413, B:183:0x041a, B:184:0x041c, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:194:0x0469, B:195:0x046c, B:197:0x0470, B:198:0x0473, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:210:0x04a9, B:213:0x04b0, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:221:0x04de, B:222:0x04e8, B:223:0x04fa, B:224:0x0503, B:225:0x0519, B:228:0x052e), top: B:258:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01b6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$checkLoadedRemoteFilters$43(org.telegram.tgnet.TLRPC$Vector r39) {
        /*
            Method dump skipped, instructions count: 1541
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkLoadedRemoteFilters$43(org.telegram.tgnet.TLRPC$Vector):void");
    }

    public static /* synthetic */ int lambda$checkLoadedRemoteFilters$42(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* renamed from: processLoadedFilterPeersInternal */
    public void lambda$processLoadedFilterPeers$45(TLRPC$messages_Dialogs tLRPC$messages_Dialogs, TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, ArrayList<TLRPC$User> arrayList, ArrayList<TLRPC$Chat> arrayList2, ArrayList<MessagesController.DialogFilter> arrayList3, SparseArray<MessagesController.DialogFilter> sparseArray, ArrayList<Integer> arrayList4, HashMap<Integer, HashSet<Long>> hashMap, HashMap<Integer, HashSet<Long>> hashMap2, HashSet<Integer> hashSet) {
        putUsersAndChats(arrayList, arrayList2, true, false);
        int size = sparseArray.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            lambda$deleteDialogFilter$46(sparseArray.valueAt(i));
            i++;
            z = true;
        }
        Iterator<Integer> it = hashSet.iterator();
        while (it.hasNext()) {
            MessagesController.DialogFilter dialogFilter = this.dialogFiltersMap.get(it.next().intValue());
            if (dialogFilter != null) {
                dialogFilter.pendingUnreadCount = -1;
            }
        }
        for (Map.Entry<Integer, HashSet<Long>> entry : hashMap2.entrySet()) {
            MessagesController.DialogFilter dialogFilter2 = this.dialogFiltersMap.get(entry.getKey().intValue());
            if (dialogFilter2 != null) {
                HashSet<Long> value = entry.getValue();
                dialogFilter2.alwaysShow.removeAll(value);
                dialogFilter2.neverShow.removeAll(value);
                z = true;
            }
        }
        for (Map.Entry<Integer, HashSet<Long>> entry2 : hashMap.entrySet()) {
            MessagesController.DialogFilter dialogFilter3 = this.dialogFiltersMap.get(entry2.getKey().intValue());
            if (dialogFilter3 != null) {
                Iterator<Long> it2 = entry2.getValue().iterator();
                while (it2.hasNext()) {
                    dialogFilter3.pinnedDialogs.delete(it2.next().longValue());
                }
                z = true;
            }
        }
        int size2 = arrayList3.size();
        int i2 = 0;
        while (i2 < size2) {
            saveDialogFilterInternal(arrayList3.get(i2), false, true);
            i2++;
            z = true;
        }
        int size3 = this.dialogFilters.size();
        boolean z2 = false;
        for (int i3 = 0; i3 < size3; i3++) {
            MessagesController.DialogFilter dialogFilter4 = this.dialogFilters.get(i3);
            int indexOf = arrayList4.indexOf(Integer.valueOf(dialogFilter4.id));
            if (dialogFilter4.order != indexOf) {
                dialogFilter4.order = indexOf;
                z2 = true;
                z = true;
            }
        }
        if (z2) {
            Collections.sort(this.dialogFilters, MessagesStorage$$ExternalSyntheticLambda199.INSTANCE);
            saveDialogFiltersOrderInternal();
        }
        int i4 = z ? 1 : 2;
        calcUnreadCounters(true);
        getMessagesController().processLoadedDialogFilters(new ArrayList<>(this.dialogFilters), tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, null, i4);
    }

    public static /* synthetic */ int lambda$processLoadedFilterPeersInternal$44(MessagesController.DialogFilter dialogFilter, MessagesController.DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void processLoadedFilterPeers(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, final ArrayList<TLRPC$User> arrayList, final ArrayList<TLRPC$Chat> arrayList2, final ArrayList<MessagesController.DialogFilter> arrayList3, final SparseArray<MessagesController.DialogFilter> sparseArray, final ArrayList<Integer> arrayList4, final HashMap<Integer, HashSet<Long>> hashMap, final HashMap<Integer, HashSet<Long>> hashMap2, final HashSet<Integer> hashSet) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda180
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$processLoadedFilterPeers$45(tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap, hashMap2, hashSet);
            }
        });
    }

    /* renamed from: deleteDialogFilterInternal */
    public void lambda$deleteDialogFilter$46(MessagesController.DialogFilter dialogFilter) {
        try {
            this.dialogFilters.remove(dialogFilter);
            this.dialogFiltersMap.remove(dialogFilter.id);
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM dialog_filter WHERE id = " + dialogFilter.id).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase2 = this.database;
            sQLiteDatabase2.executeFast("DELETE FROM dialog_filter_ep WHERE id = " + dialogFilter.id).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase3 = this.database;
            sQLiteDatabase3.executeFast("DELETE FROM dialog_filter_pin_v2 WHERE id = " + dialogFilter.id).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteDialogFilter(final MessagesController.DialogFilter dialogFilter) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda152
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteDialogFilter$46(dialogFilter);
            }
        });
    }

    public void saveDialogFilter(final MessagesController.DialogFilter dialogFilter, final boolean z, final boolean z2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda153
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFilter$48(dialogFilter, z, z2);
            }
        });
    }

    public /* synthetic */ void lambda$saveDialogFilter$48(MessagesController.DialogFilter dialogFilter, boolean z, boolean z2) {
        saveDialogFilterInternal(dialogFilter, z, z2);
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFilter$47();
            }
        });
    }

    public /* synthetic */ void lambda$saveDialogFilter$47() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public void saveDialogFiltersOrderInternal() {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialog_filter SET ord = ?, flags = ? WHERE id = ?");
            int size = this.dialogFilters.size();
            for (int i = 0; i < size; i++) {
                MessagesController.DialogFilter dialogFilter = this.dialogFilters.get(i);
                executeFast.requery();
                executeFast.bindInteger(1, dialogFilter.order);
                executeFast.bindInteger(2, dialogFilter.flags);
                executeFast.bindInteger(3, dialogFilter.id);
                executeFast.step();
            }
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveDialogFiltersOrder() {
        final ArrayList arrayList = new ArrayList(getMessagesController().dialogFilters);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda127
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFiltersOrder$49(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$saveDialogFiltersOrder$49(ArrayList arrayList) {
        this.dialogFilters.clear();
        this.dialogFiltersMap.clear();
        this.dialogFilters.addAll(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            ((MessagesController.DialogFilter) arrayList.get(i)).order = i;
            this.dialogFiltersMap.put(((MessagesController.DialogFilter) arrayList.get(i)).id, (MessagesController.DialogFilter) arrayList.get(i));
        }
        saveDialogFiltersOrderInternal();
    }

    protected static void addReplyMessages(TLRPC$Message tLRPC$Message, LongSparseArray<SparseArray<ArrayList<TLRPC$Message>>> longSparseArray, LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        int i = tLRPC$Message.reply_to.reply_to_msg_id;
        long replyToDialogId = MessageObject.getReplyToDialogId(tLRPC$Message);
        SparseArray<ArrayList<TLRPC$Message>> sparseArray = longSparseArray.get(replyToDialogId);
        ArrayList<Integer> arrayList = longSparseArray2.get(replyToDialogId);
        if (sparseArray == null) {
            sparseArray = new SparseArray<>();
            longSparseArray.put(replyToDialogId, sparseArray);
        }
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            longSparseArray2.put(replyToDialogId, arrayList);
        }
        ArrayList<TLRPC$Message> arrayList2 = sparseArray.get(tLRPC$Message.reply_to.reply_to_msg_id);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList<>();
            sparseArray.put(tLRPC$Message.reply_to.reply_to_msg_id, arrayList2);
            if (!arrayList.contains(Integer.valueOf(tLRPC$Message.reply_to.reply_to_msg_id))) {
                arrayList.add(Integer.valueOf(tLRPC$Message.reply_to.reply_to_msg_id));
            }
        }
        arrayList2.add(tLRPC$Message);
    }

    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r3v4 */
    protected void loadReplyMessages(LongSparseArray<SparseArray<ArrayList<TLRPC$Message>>> longSparseArray, LongSparseArray<ArrayList<Integer>> longSparseArray2, ArrayList<Long> arrayList, ArrayList<Long> arrayList2, boolean z) throws SQLiteException {
        SQLiteCursor sQLiteCursor;
        if (longSparseArray.isEmpty()) {
            return;
        }
        int size = longSparseArray.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            long keyAt = longSparseArray.keyAt(i2);
            SparseArray<ArrayList<TLRPC$Message>> valueAt = longSparseArray.valueAt(i2);
            ArrayList<Integer> arrayList3 = longSparseArray2.get(keyAt);
            if (arrayList3 != null) {
                if (z) {
                    SQLiteDatabase sQLiteDatabase = this.database;
                    Locale locale = Locale.US;
                    Object[] objArr = new Object[2];
                    objArr[i] = TextUtils.join(",", arrayList3);
                    objArr[1] = Long.valueOf(keyAt);
                    sQLiteCursor = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT data, mid, date, uid FROM scheduled_messages_v2 WHERE mid IN(%s) AND uid = %d", objArr), new Object[i]);
                } else {
                    SQLiteDatabase sQLiteDatabase2 = this.database;
                    Locale locale2 = Locale.US;
                    Object[] objArr2 = new Object[2];
                    objArr2[i] = TextUtils.join(",", arrayList3);
                    objArr2[1] = Long.valueOf(keyAt);
                    sQLiteCursor = sQLiteDatabase2.queryFinalized(String.format(locale2, "SELECT data, mid, date, uid FROM messages_v2 WHERE mid IN(%s) AND uid = %d", objArr2), new Object[i]);
                }
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(i);
                    if (byteBufferValue != null) {
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(i), i);
                        TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                        byteBufferValue.reuse();
                        TLdeserialize.id = sQLiteCursor.intValue(1);
                        TLdeserialize.date = sQLiteCursor.intValue(2);
                        TLdeserialize.dialog_id = sQLiteCursor.longValue(3);
                        addUsersAndChatsFromMessage(TLdeserialize, arrayList, arrayList2);
                        ArrayList<TLRPC$Message> arrayList4 = valueAt.get(TLdeserialize.id);
                        if (arrayList4 != null) {
                            int size2 = arrayList4.size();
                            for (int i3 = 0; i3 < size2; i3++) {
                                arrayList4.get(i3).replyMessage = TLdeserialize;
                                MessageObject.getDialogId(TLdeserialize);
                            }
                        }
                    }
                    i = 0;
                }
                sQLiteCursor.dispose();
            }
            i2++;
            i = 0;
        }
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadUnreadMessages$51();
            }
        });
    }

    public /* synthetic */ void lambda$loadUnreadMessages$51() {
        String str;
        ArrayList arrayList;
        ArrayList<TLRPC$Chat> arrayList2;
        ArrayList<TLRPC$User> arrayList3;
        ArrayList<TLRPC$EncryptedChat> arrayList4;
        final LongSparseArray longSparseArray;
        LongSparseArray longSparseArray2;
        ArrayList<TLRPC$EncryptedChat> arrayList5;
        LongSparseArray longSparseArray3;
        ArrayList<TLRPC$User> arrayList6;
        String str2;
        int i;
        Exception e;
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader;
        NativeByteBuffer byteBufferValue;
        try {
            ArrayList<Long> arrayList7 = new ArrayList<>();
            ArrayList<Long> arrayList8 = new ArrayList<>();
            ArrayList arrayList9 = new ArrayList();
            LongSparseArray longSparseArray4 = new LongSparseArray();
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count > 0", new Object[0]);
            StringBuilder sb = new StringBuilder();
            int currentTime = getConnectionsManager().getCurrentTime();
            while (true) {
                str = ",";
                if (!queryFinalized.next()) {
                    break;
                }
                long longValue = queryFinalized.longValue(2);
                boolean z = (longValue & 1) != 0;
                int i2 = (int) (longValue >> 32);
                if (queryFinalized.isNull(2) || !z || (i2 != 0 && i2 < currentTime)) {
                    long longValue2 = queryFinalized.longValue(0);
                    if (!DialogObject.isFolderDialogId(longValue2)) {
                        longSparseArray4.put(longValue2, Integer.valueOf(queryFinalized.intValue(1)));
                        if (sb.length() != 0) {
                            sb.append(str);
                        }
                        sb.append(longValue2);
                        if (DialogObject.isEncryptedDialog(longValue2)) {
                            int encryptedChatId = DialogObject.getEncryptedChatId(longValue2);
                            if (!arrayList9.contains(Integer.valueOf(encryptedChatId))) {
                                arrayList9.add(Integer.valueOf(encryptedChatId));
                            }
                        } else if (DialogObject.isUserDialog(longValue2)) {
                            if (!arrayList7.contains(Long.valueOf(longValue2))) {
                                arrayList7.add(Long.valueOf(longValue2));
                            }
                        } else {
                            long j = -longValue2;
                            if (!arrayList8.contains(Long.valueOf(j))) {
                                arrayList8.add(Long.valueOf(j));
                            }
                        }
                    }
                }
            }
            queryFinalized.dispose();
            LongSparseArray<SparseArray<ArrayList<TLRPC$Message>>> longSparseArray5 = new LongSparseArray<>();
            LongSparseArray<ArrayList<Integer>> longSparseArray6 = new LongSparseArray<>();
            final ArrayList arrayList10 = new ArrayList();
            ArrayList arrayList11 = new ArrayList();
            ArrayList<TLRPC$User> arrayList12 = new ArrayList<>();
            ArrayList<TLRPC$Chat> arrayList13 = new ArrayList<>();
            ArrayList<TLRPC$EncryptedChat> arrayList14 = new ArrayList<>();
            if (sb.length() > 0) {
                SQLiteCursor queryFinalized2 = this.database.queryFinalized("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages_v2 WHERE uid IN (" + sb.toString() + ") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50", new Object[0]);
                int i3 = 0;
                while (queryFinalized2.next()) {
                    NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(1);
                    if (byteBufferValue2 != null) {
                        arrayList6 = arrayList12;
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                        longSparseArray3 = longSparseArray4;
                        TLdeserialize.readAttachPath(byteBufferValue2, getUserConfig().clientUserId);
                        byteBufferValue2.reuse();
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(0));
                        TLdeserialize.id = queryFinalized2.intValue(3);
                        TLdeserialize.date = queryFinalized2.intValue(4);
                        str2 = str;
                        TLdeserialize.dialog_id = queryFinalized2.longValue(5);
                        arrayList10.add(TLdeserialize);
                        int max = Math.max(i3, TLdeserialize.date);
                        addUsersAndChatsFromMessage(TLdeserialize, arrayList7, arrayList8);
                        TLdeserialize.send_state = queryFinalized2.intValue(2);
                        if ((TLdeserialize.peer_id.channel_id == 0 && !MessageObject.isUnread(TLdeserialize) && !DialogObject.isEncryptedDialog(TLdeserialize.dialog_id)) || TLdeserialize.id > 0) {
                            TLdeserialize.send_state = 0;
                        }
                        if (DialogObject.isEncryptedDialog(TLdeserialize.dialog_id) && !queryFinalized2.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized2.longValue(5);
                        }
                        try {
                            tLRPC$TL_messageReplyHeader = TLdeserialize.reply_to;
                        } catch (Exception e2) {
                            e = e2;
                            i = max;
                        }
                        if (tLRPC$TL_messageReplyHeader != null && tLRPC$TL_messageReplyHeader.reply_to_msg_id != 0) {
                            TLRPC$MessageAction tLRPC$MessageAction = TLdeserialize.action;
                            if ((tLRPC$MessageAction instanceof TLRPC$TL_messageActionPinMessage) || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPaymentSent) || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionGameScore)) {
                                if (queryFinalized2.isNull(6) || (byteBufferValue = queryFinalized2.byteBufferValue(6)) == null) {
                                    i = max;
                                } else {
                                    TLRPC$Message TLdeserialize2 = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    TLdeserialize.replyMessage = TLdeserialize2;
                                    i = max;
                                    try {
                                        TLdeserialize2.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                                        byteBufferValue.reuse();
                                        TLRPC$Message tLRPC$Message = TLdeserialize.replyMessage;
                                        if (tLRPC$Message != null) {
                                            addUsersAndChatsFromMessage(tLRPC$Message, arrayList7, arrayList8);
                                        }
                                    } catch (Exception e3) {
                                        e = e3;
                                        FileLog.e(e);
                                        i3 = i;
                                        str = str2;
                                        arrayList12 = arrayList6;
                                        longSparseArray4 = longSparseArray3;
                                    }
                                }
                                if (TLdeserialize.replyMessage == null) {
                                    addReplyMessages(TLdeserialize, longSparseArray5, longSparseArray6);
                                }
                                i3 = i;
                            }
                        }
                        i = max;
                        i3 = i;
                    } else {
                        arrayList6 = arrayList12;
                        longSparseArray3 = longSparseArray4;
                        str2 = str;
                    }
                    str = str2;
                    arrayList12 = arrayList6;
                    longSparseArray4 = longSparseArray3;
                }
                ArrayList<TLRPC$User> arrayList15 = arrayList12;
                LongSparseArray longSparseArray7 = longSparseArray4;
                String str3 = str;
                queryFinalized2.dispose();
                this.database.executeFast("DELETE FROM unread_push_messages WHERE date <= " + i3).stepThis().dispose();
                boolean z2 = false;
                SQLiteCursor queryFinalized3 = this.database.queryFinalized("SELECT data, mid, date, uid, random, fm, name, uname, flags FROM unread_push_messages WHERE 1 ORDER BY date DESC LIMIT 50", new Object[0]);
                while (queryFinalized3.next()) {
                    int i4 = z2 ? 1 : 0;
                    int i5 = z2 ? 1 : 0;
                    NativeByteBuffer byteBufferValue3 = queryFinalized3.byteBufferValue(i4);
                    if (byteBufferValue3 != null) {
                        TLRPC$Message TLdeserialize3 = TLRPC$Message.TLdeserialize(byteBufferValue3, byteBufferValue3.readInt32(z2), z2);
                        byteBufferValue3.reuse();
                        TLdeserialize3.id = queryFinalized3.intValue(1);
                        TLdeserialize3.date = queryFinalized3.intValue(2);
                        TLdeserialize3.dialog_id = queryFinalized3.longValue(3);
                        ArrayList<TLRPC$EncryptedChat> arrayList16 = arrayList14;
                        TLdeserialize3.random_id = queryFinalized3.longValue(4);
                        String stringValue = queryFinalized3.isNull(5) ? null : queryFinalized3.stringValue(5);
                        String stringValue2 = queryFinalized3.isNull(6) ? null : queryFinalized3.stringValue(6);
                        String stringValue3 = queryFinalized3.isNull(7) ? null : queryFinalized3.stringValue(7);
                        int intValue = queryFinalized3.intValue(8);
                        if (MessageObject.getFromChatId(TLdeserialize3) != 0 || !DialogObject.isUserDialog(TLdeserialize3.dialog_id)) {
                            arrayList5 = arrayList16;
                        } else {
                            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                            TLdeserialize3.from_id = tLRPC$TL_peerUser;
                            arrayList5 = arrayList16;
                            tLRPC$TL_peerUser.user_id = TLdeserialize3.dialog_id;
                        }
                        if (DialogObject.isUserDialog(TLdeserialize3.dialog_id)) {
                            if (!arrayList7.contains(Long.valueOf(TLdeserialize3.dialog_id))) {
                                arrayList7.add(Long.valueOf(TLdeserialize3.dialog_id));
                            }
                        } else if (DialogObject.isChatDialog(TLdeserialize3.dialog_id) && !arrayList8.contains(Long.valueOf(-TLdeserialize3.dialog_id))) {
                            arrayList8.add(Long.valueOf(-TLdeserialize3.dialog_id));
                        }
                        arrayList11.add(new MessageObject(this.currentAccount, TLdeserialize3, stringValue, stringValue2, stringValue3, (intValue & 1) != 0, (intValue & 2) != 0, (TLdeserialize3.flags & Integer.MIN_VALUE) != 0, false));
                        addUsersAndChatsFromMessage(TLdeserialize3, arrayList7, arrayList8);
                    } else {
                        arrayList5 = arrayList14;
                    }
                    arrayList14 = arrayList5;
                    z2 = false;
                }
                queryFinalized3.dispose();
                arrayList3 = arrayList15;
                arrayList = arrayList11;
                arrayList4 = arrayList14;
                arrayList2 = arrayList13;
                loadReplyMessages(longSparseArray5, longSparseArray6, arrayList7, arrayList8, false);
                if (!arrayList9.isEmpty()) {
                    getEncryptedChatsInternal(TextUtils.join(str3, arrayList9), arrayList4, arrayList7);
                }
                if (!arrayList7.isEmpty()) {
                    getUsersInternal(TextUtils.join(str3, arrayList7), arrayList3);
                }
                if (!arrayList8.isEmpty()) {
                    getChatsInternal(TextUtils.join(str3, arrayList8), arrayList2);
                    int i6 = 0;
                    while (i6 < arrayList2.size()) {
                        TLRPC$Chat tLRPC$Chat = arrayList2.get(i6);
                        if (tLRPC$Chat == null || (!ChatObject.isNotInChat(tLRPC$Chat) && !tLRPC$Chat.min && tLRPC$Chat.migrated_to == null)) {
                            longSparseArray2 = longSparseArray7;
                        } else {
                            long j2 = -tLRPC$Chat.id;
                            this.database.executeFast("UPDATE dialogs SET unread_count = 0 WHERE did = " + j2).stepThis().dispose();
                            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", Long.valueOf(j2))).stepThis().dispose();
                            arrayList2.remove(i6);
                            i6 += -1;
                            longSparseArray2 = longSparseArray7;
                            longSparseArray2.remove(j2);
                            int i7 = 0;
                            while (i7 < arrayList10.size()) {
                                if (((TLRPC$Message) arrayList10.get(i7)).dialog_id == j2) {
                                    arrayList10.remove(i7);
                                    i7--;
                                }
                                i7++;
                            }
                        }
                        i6++;
                        longSparseArray7 = longSparseArray2;
                    }
                }
                longSparseArray = longSparseArray7;
            } else {
                arrayList = arrayList11;
                arrayList3 = arrayList12;
                longSparseArray = longSparseArray4;
                arrayList2 = arrayList13;
                arrayList4 = arrayList14;
            }
            Collections.reverse(arrayList10);
            final ArrayList arrayList17 = arrayList;
            final ArrayList<TLRPC$User> arrayList18 = arrayList3;
            final ArrayList<TLRPC$Chat> arrayList19 = arrayList2;
            final ArrayList<TLRPC$EncryptedChat> arrayList20 = arrayList4;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda121
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$loadUnreadMessages$50(longSparseArray, arrayList10, arrayList17, arrayList18, arrayList19, arrayList20);
                }
            });
        } catch (Exception e4) {
            FileLog.e(e4);
        }
    }

    public /* synthetic */ void lambda$loadUnreadMessages$50(LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        getNotificationsController().processLoadedUnreadMessages(longSparseArray, arrayList, arrayList2, arrayList3, arrayList4, arrayList5);
    }

    public void putWallpapers(final ArrayList<TLRPC$WallPaper> arrayList, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda54
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putWallpapers$52(i, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$putWallpapers$52(int i, ArrayList arrayList) {
        SQLitePreparedStatement sQLitePreparedStatement;
        if (i == 1) {
            try {
                this.database.executeFast("DELETE FROM wallpapers2 WHERE num >= -1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        if (i != 0) {
            sQLitePreparedStatement = this.database.executeFast("REPLACE INTO wallpapers2 VALUES(?, ?, ?)");
        } else {
            sQLitePreparedStatement = this.database.executeFast("UPDATE wallpapers2 SET data = ? WHERE uid = ?");
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$WallPaper tLRPC$WallPaper = (TLRPC$WallPaper) arrayList.get(i2);
            sQLitePreparedStatement.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$WallPaper.getObjectSize());
            tLRPC$WallPaper.serializeToStream(nativeByteBuffer);
            if (i != 0) {
                sQLitePreparedStatement.bindLong(1, tLRPC$WallPaper.id);
                sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer);
                if (i < 0) {
                    sQLitePreparedStatement.bindInteger(3, i);
                } else {
                    sQLitePreparedStatement.bindInteger(3, i == 2 ? -1 : i2);
                }
            } else {
                sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                sQLitePreparedStatement.bindLong(2, tLRPC$WallPaper.id);
            }
            sQLitePreparedStatement.step();
            nativeByteBuffer.reuse();
        }
        sQLitePreparedStatement.dispose();
        this.database.commitTransaction();
    }

    public void deleteWallpaper(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda64
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteWallpaper$53(j);
            }
        });
    }

    public /* synthetic */ void lambda$deleteWallpaper$53(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM wallpapers2 WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getWallpapers$55();
            }
        });
    }

    public /* synthetic */ void lambda$getWallpapers$55() {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized("SELECT data FROM wallpapers2 WHERE 1 ORDER BY num ASC", new Object[0]);
                final ArrayList arrayList = new ArrayList();
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC$WallPaper TLdeserialize = TLRPC$WallPaper.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            arrayList.add(TLdeserialize);
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.lambda$getWallpapers$54(arrayList);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public static /* synthetic */ void lambda$getWallpapers$54(ArrayList arrayList) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersDidLoad, arrayList);
    }

    public void addRecentLocalFile(final String str, final String str2, final TLRPC$Document tLRPC$Document) {
        if (str == null || str.length() == 0) {
            return;
        }
        if ((str2 == null || str2.length() == 0) && tLRPC$Document == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda163
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$addRecentLocalFile$56(tLRPC$Document, str, str2);
            }
        });
    }

    public /* synthetic */ void lambda$addRecentLocalFile$56(TLRPC$Document tLRPC$Document, String str, String str2) {
        try {
            if (tLRPC$Document != null) {
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Document.getObjectSize());
                tLRPC$Document.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindString(2, str);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            } else {
                SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
                executeFast2.requery();
                executeFast2.bindString(1, str2);
                executeFast2.bindString(2, str);
                executeFast2.step();
                executeFast2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteUserChatHistory(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda82
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteUserChatHistory$59(j, j2);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x00bc  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00be  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00d8 A[Catch: Exception -> 0x00e1, TRY_LEAVE, TryCatch #2 {Exception -> 0x00e1, blocks: (B:3:0x0004, B:29:0x0094, B:30:0x0097, B:34:0x00c0, B:36:0x00d8), top: B:44:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:49:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$deleteUserChatHistory$59(final long r18, long r20) {
        /*
            Method dump skipped, instructions count: 230
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteUserChatHistory$59(long, long):void");
    }

    public /* synthetic */ void lambda$deleteUserChatHistory$57(ArrayList arrayList, long j, ArrayList arrayList2) {
        getFileLoader().cancelLoadFiles(arrayList);
        getMessagesController().markDialogMessageAsDeleted(j, arrayList2);
    }

    public /* synthetic */ void lambda$deleteUserChatHistory$58(ArrayList arrayList, long j) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.messagesDeleted;
        Object[] objArr = new Object[3];
        objArr[0] = arrayList;
        objArr[1] = Long.valueOf(DialogObject.isChatDialog(j) ? -j : 0L);
        objArr[2] = Boolean.FALSE;
        notificationCenter.postNotificationName(i, objArr);
    }

    private boolean addFilesToDelete(TLRPC$Message tLRPC$Message, ArrayList<File> arrayList, ArrayList<Pair<Long, Integer>> arrayList2, ArrayList<String> arrayList3, boolean z) {
        int i;
        long j;
        int i2 = 0;
        if (tLRPC$Message == null) {
            return false;
        }
        TLRPC$Document document = MessageObject.getDocument(tLRPC$Message);
        TLRPC$Photo photo = MessageObject.getPhoto(tLRPC$Message);
        if (MessageObject.isVoiceMessage(tLRPC$Message)) {
            if (document == null || getMediaDataController().ringtoneDataStore.contains(document.id)) {
                return false;
            }
            j = document.id;
            i = 2;
        } else {
            if (MessageObject.isStickerMessage(tLRPC$Message) || MessageObject.isAnimatedStickerMessage(tLRPC$Message)) {
                if (document == null) {
                    return false;
                }
                j = document.id;
            } else if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isGifMessage(tLRPC$Message)) {
                if (document == null) {
                    return false;
                }
                j = document.id;
                i = 4;
            } else if (document != null) {
                if (getMediaDataController().ringtoneDataStore.contains(document.id)) {
                    return false;
                }
                j = document.id;
                i = 8;
            } else if (photo == null || FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize()) == null) {
                j = 0;
                i = 0;
            } else {
                j = photo.id;
            }
            i = 1;
        }
        if (j != 0) {
            arrayList2.add(new Pair<>(Long.valueOf(j), Integer.valueOf(i)));
        }
        if (photo != null) {
            int size = photo.sizes.size();
            while (i2 < size) {
                TLRPC$PhotoSize tLRPC$PhotoSize = photo.sizes.get(i2);
                String attachFileName = FileLoader.getAttachFileName(tLRPC$PhotoSize);
                if (!TextUtils.isEmpty(attachFileName)) {
                    arrayList3.add(attachFileName);
                }
                File pathToAttach = getFileLoader().getPathToAttach(tLRPC$PhotoSize, z);
                if (pathToAttach.toString().length() > 0) {
                    arrayList.add(pathToAttach);
                }
                i2++;
            }
            return true;
        } else if (document == null) {
            return false;
        } else {
            String attachFileName2 = FileLoader.getAttachFileName(document);
            if (!TextUtils.isEmpty(attachFileName2)) {
                arrayList3.add(attachFileName2);
            }
            File pathToAttach2 = getFileLoader().getPathToAttach(document, z);
            if (pathToAttach2.toString().length() > 0) {
                arrayList.add(pathToAttach2);
            }
            int size2 = document.thumbs.size();
            while (i2 < size2) {
                File pathToAttach3 = getFileLoader().getPathToAttach(document.thumbs.get(i2));
                if (pathToAttach3.toString().length() > 0) {
                    arrayList.add(pathToAttach3);
                }
                i2++;
            }
            return true;
        }
    }

    public void deleteDialog(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteDialog$62(i, j);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:68:0x0286 A[Catch: Exception -> 0x0471, TryCatch #5 {Exception -> 0x0471, blocks: (B:4:0x000c, B:6:0x002b, B:8:0x0031, B:11:0x0037, B:16:0x0048, B:30:0x00c5, B:31:0x00c8, B:38:0x00f3, B:40:0x0113, B:65:0x01a7, B:66:0x01aa, B:68:0x0286, B:69:0x0289, B:71:0x0295, B:74:0x02a5, B:76:0x034b, B:78:0x0351, B:79:0x0371, B:80:0x0393), top: B:94:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x034b A[Catch: Exception -> 0x0471, TryCatch #5 {Exception -> 0x0471, blocks: (B:4:0x000c, B:6:0x002b, B:8:0x0031, B:11:0x0037, B:16:0x0048, B:30:0x00c5, B:31:0x00c8, B:38:0x00f3, B:40:0x0113, B:65:0x01a7, B:66:0x01aa, B:68:0x0286, B:69:0x0289, B:71:0x0295, B:74:0x02a5, B:76:0x034b, B:78:0x0351, B:79:0x0371, B:80:0x0393), top: B:94:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0371 A[Catch: Exception -> 0x0471, TryCatch #5 {Exception -> 0x0471, blocks: (B:4:0x000c, B:6:0x002b, B:8:0x0031, B:11:0x0037, B:16:0x0048, B:30:0x00c5, B:31:0x00c8, B:38:0x00f3, B:40:0x0113, B:65:0x01a7, B:66:0x01aa, B:68:0x0286, B:69:0x0289, B:71:0x0295, B:74:0x02a5, B:76:0x034b, B:78:0x0351, B:79:0x0371, B:80:0x0393), top: B:94:0x000c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$deleteDialog$62(int r25, long r26) {
        /*
            Method dump skipped, instructions count: 1142
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteDialog$62(int, long):void");
    }

    public /* synthetic */ void lambda$deleteDialog$60(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public /* synthetic */ void lambda$deleteDialog$61() {
        getNotificationCenter().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
    }

    public void onDeleteQueryComplete(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda67
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$onDeleteQueryComplete$63(j);
            }
        });
    }

    public /* synthetic */ void lambda$onDeleteQueryComplete$63(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDialogPhotos(final long j, final int i, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda45
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogPhotos$65(i2, j, i, i3);
            }
        });
    }

    public /* synthetic */ void lambda$getDialogPhotos$65(final int i, final long j, final int i2, final int i3) {
        SQLiteCursor sQLiteCursor;
        try {
            if (i != 0) {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY rowid ASC LIMIT %d", Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2)), new Object[0]);
            } else {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY rowid ASC LIMIT %d", Long.valueOf(j), Integer.valueOf(i2)), new Object[0]);
            }
            final TLRPC$TL_photos_photos tLRPC$TL_photos_photos = new TLRPC$TL_photos_photos();
            final ArrayList arrayList = new ArrayList();
            while (sQLiteCursor.next()) {
                NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$Photo TLdeserialize = TLRPC$Photo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (byteBufferValue.remaining() > 0) {
                        arrayList.add(TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                    } else {
                        arrayList.add(null);
                    }
                    byteBufferValue.reuse();
                    tLRPC$TL_photos_photos.photos.add(TLdeserialize);
                }
            }
            sQLiteCursor.dispose();
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda181
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$getDialogPhotos$64(tLRPC$TL_photos_photos, arrayList, j, i2, i, i3);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$getDialogPhotos$64(TLRPC$photos_Photos tLRPC$photos_Photos, ArrayList arrayList, long j, int i, int i2, int i3) {
        getMessagesController().processLoadedUserPhotos(tLRPC$photos_Photos, arrayList, j, i, i2, true, i3);
    }

    public void clearUserPhotos(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearUserPhotos$66(j);
            }
        });
    }

    public /* synthetic */ void lambda$clearUserPhotos$66(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearUserPhoto(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda84
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearUserPhoto$67(j, j2);
            }
        });
    }

    public /* synthetic */ void lambda$clearUserPhoto$67(long j, long j2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + j + " AND id = " + j2).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetDialogs(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final int i, final int i2, final int i3, final int i4, final int i5, final LongSparseArray<TLRPC$Dialog> longSparseArray, final LongSparseArray<MessageObject> longSparseArray2, final TLRPC$Message tLRPC$Message, final int i6) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda179
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$resetDialogs$69(tLRPC$messages_Dialogs, i6, i2, i3, i4, i5, tLRPC$Message, i, longSparseArray, longSparseArray2);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:78:0x02cb A[Catch: Exception -> 0x0312, LOOP:7: B:76:0x02c8->B:78:0x02cb, LOOP_END, TryCatch #0 {Exception -> 0x0312, blocks: (B:3:0x0006, B:4:0x0024, B:6:0x002c, B:7:0x0040, B:8:0x004c, B:10:0x0052, B:12:0x0061, B:14:0x006a, B:15:0x0079, B:16:0x0081, B:18:0x0089, B:19:0x0091, B:21:0x01bc, B:23:0x01ca, B:26:0x01cf, B:30:0x01ea, B:32:0x01f2, B:33:0x01f5, B:35:0x0205, B:36:0x0207, B:38:0x020b, B:39:0x0210, B:40:0x0215, B:43:0x0241, B:45:0x0249, B:47:0x0259, B:48:0x025c, B:51:0x0265, B:54:0x026e, B:56:0x0276, B:58:0x0284, B:59:0x0287, B:62:0x0298, B:65:0x02a1, B:67:0x02a9, B:69:0x02b7, B:70:0x02ba, B:78:0x02cb, B:79:0x02ea), top: B:83:0x0006 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$resetDialogs$69(org.telegram.tgnet.TLRPC$messages_Dialogs r31, int r32, int r33, int r34, int r35, int r36, org.telegram.tgnet.TLRPC$Message r37, int r38, androidx.collection.LongSparseArray r39, androidx.collection.LongSparseArray r40) {
        /*
            Method dump skipped, instructions count: 791
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$resetDialogs$69(org.telegram.tgnet.TLRPC$messages_Dialogs, int, int, int, int, int, org.telegram.tgnet.TLRPC$Message, int, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public static /* synthetic */ int lambda$resetDialogs$68(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public void putDialogPhotos(final long j, final TLRPC$photos_Photos tLRPC$photos_Photos, final ArrayList<TLRPC$Message> arrayList) {
        if (tLRPC$photos_Photos == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda109
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putDialogPhotos$70(j, tLRPC$photos_Photos, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$putDialogPhotos$70(long j, TLRPC$photos_Photos tLRPC$photos_Photos, ArrayList arrayList) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + j).stepThis().dispose();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
            int size = tLRPC$photos_Photos.photos.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Photo tLRPC$Photo = tLRPC$photos_Photos.photos.get(i);
                if (!(tLRPC$Photo instanceof TLRPC$TL_photoEmpty)) {
                    executeFast.requery();
                    int objectSize = tLRPC$Photo.getObjectSize();
                    if (arrayList != null) {
                        objectSize += ((TLRPC$Message) arrayList.get(i)).getObjectSize();
                    }
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(objectSize);
                    tLRPC$Photo.serializeToStream(nativeByteBuffer);
                    if (arrayList != null) {
                        ((TLRPC$Message) arrayList.get(i)).serializeToStream(nativeByteBuffer);
                    }
                    executeFast.bindLong(1, j);
                    executeFast.bindLong(2, tLRPC$Photo.id);
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                }
            }
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void emptyMessagesMedia(final long j, final ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda140
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$emptyMessagesMedia$73(arrayList, j);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x012b A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0131 A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x013c  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x013e  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x014e  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0150  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0162 A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0176 A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x017f A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x018a A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x019f A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01a3 A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01ae A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:70:0x01b3 A[Catch: Exception -> 0x01d7, TryCatch #0 {Exception -> 0x01d7, blocks: (B:3:0x0002, B:4:0x003b, B:6:0x0042, B:8:0x0048, B:10:0x0060, B:13:0x0073, B:15:0x0079, B:16:0x0081, B:18:0x0085, B:19:0x008c, B:21:0x00ad, B:22:0x00b3, B:24:0x00b8, B:26:0x00c5, B:27:0x00ce, B:29:0x00d4, B:31:0x0111, B:36:0x0119, B:38:0x012b, B:39:0x0131, B:40:0x0138, B:46:0x0143, B:50:0x0151, B:52:0x0162, B:53:0x0176, B:54:0x0179, B:56:0x017f, B:59:0x0184, B:60:0x0186, B:61:0x018a, B:62:0x018e, B:64:0x019f, B:65:0x01a3, B:66:0x01a6, B:68:0x01ae, B:70:0x01b3, B:71:0x01b6, B:72:0x01bb, B:73:0x01c6), top: B:77:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x01b6 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$emptyMessagesMedia$73(java.util.ArrayList r18, long r19) {
        /*
            Method dump skipped, instructions count: 476
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$emptyMessagesMedia$73(java.util.ArrayList, long):void");
    }

    public /* synthetic */ void lambda$emptyMessagesMedia$71(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, arrayList.get(i));
        }
    }

    public /* synthetic */ void lambda$emptyMessagesMedia$72(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public void updateMessagePollResults(final long j, final TLRPC$Poll tLRPC$Poll, final TLRPC$PollResults tLRPC$PollResults) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda107
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessagePollResults$74(j, tLRPC$Poll, tLRPC$PollResults);
            }
        });
    }

    public /* synthetic */ void lambda$updateMessagePollResults$74(long j, TLRPC$Poll tLRPC$Poll, TLRPC$PollResults tLRPC$PollResults) {
        int i;
        ArrayList arrayList;
        LongSparseArray longSparseArray = null;
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, mid FROM polls_v2 WHERE id = %d", Long.valueOf(j)), new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                if (longSparseArray == null) {
                    longSparseArray = new LongSparseArray();
                }
                ArrayList arrayList2 = (ArrayList) longSparseArray.get(longValue);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    longSparseArray.put(longValue, arrayList2);
                }
                arrayList2.add(Integer.valueOf(queryFinalized.intValue(1)));
            }
            queryFinalized.dispose();
            if (longSparseArray == null) {
                return;
            }
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET data = ? WHERE mid = ? AND uid = ?");
            int size = longSparseArray.size();
            for (int i2 = 0; i2 < size; i2++) {
                long keyAt = longSparseArray.keyAt(i2);
                ArrayList arrayList3 = (ArrayList) longSparseArray.valueAt(i2);
                int i3 = 0;
                for (int size2 = arrayList3.size(); i3 < size2; size2 = i) {
                    Integer num = (Integer) arrayList3.get(i3);
                    SQLiteDatabase sQLiteDatabase = this.database;
                    Locale locale = Locale.US;
                    LongSparseArray longSparseArray2 = longSparseArray;
                    int i4 = size;
                    SQLiteCursor queryFinalized2 = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT data FROM messages_v2 WHERE mid = %d AND uid = %d", num, Long.valueOf(keyAt)), new Object[0]);
                    if (queryFinalized2.next()) {
                        NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            arrayList = arrayList3;
                            i = size2;
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            TLRPC$MessageMedia tLRPC$MessageMedia = TLdeserialize.media;
                            if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPoll) {
                                TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = (TLRPC$TL_messageMediaPoll) tLRPC$MessageMedia;
                                if (tLRPC$Poll != null) {
                                    tLRPC$TL_messageMediaPoll.poll = tLRPC$Poll;
                                }
                                if (tLRPC$PollResults != null) {
                                    MessageObject.updatePollResults(tLRPC$TL_messageMediaPoll, tLRPC$PollResults);
                                }
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                                TLdeserialize.serializeToStream(nativeByteBuffer);
                                executeFast.requery();
                                executeFast.bindByteBuffer(1, nativeByteBuffer);
                                executeFast.bindInteger(2, num.intValue());
                                executeFast.bindLong(3, keyAt);
                                executeFast.step();
                                nativeByteBuffer.reuse();
                            }
                        } else {
                            arrayList = arrayList3;
                            i = size2;
                        }
                    } else {
                        arrayList = arrayList3;
                        i = size2;
                        this.database.executeFast(String.format(locale, "DELETE FROM polls_v2 WHERE mid = %d AND uid = %d", num, Long.valueOf(keyAt))).stepThis().dispose();
                    }
                    queryFinalized2.dispose();
                    i3++;
                    longSparseArray = longSparseArray2;
                    arrayList3 = arrayList;
                    size = i4;
                }
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageReactions(final long j, final int i, final TLRPC$TL_messageReactions tLRPC$TL_messageReactions) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageReactions$75(i, j, tLRPC$TL_messageReactions);
            }
        });
    }

    public /* synthetic */ void lambda$updateMessageReactions$75(int i, long j, TLRPC$TL_messageReactions tLRPC$TL_messageReactions) {
        NativeByteBuffer byteBufferValue;
        try {
            this.database.beginTransaction();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                if (TLdeserialize != null) {
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    MessageObject.updateReactions(TLdeserialize, tLRPC$TL_messageReactions);
                    SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET data = ? WHERE mid = ? AND uid = ?");
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                    TLdeserialize.serializeToStream(nativeByteBuffer);
                    executeFast.requery();
                    executeFast.bindByteBuffer(1, nativeByteBuffer);
                    executeFast.bindInteger(2, i);
                    executeFast.bindLong(3, j);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                } else {
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageVoiceTranscriptionOpen(final long j, final int i, final TLRPC$Message tLRPC$Message) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscriptionOpen$76(i, j, tLRPC$Message);
            }
        });
    }

    public /* synthetic */ void lambda$updateMessageVoiceTranscriptionOpen$76(int i, long j, TLRPC$Message tLRPC$Message) {
        try {
            this.database.beginTransaction();
            TLRPC$Message messageWithCustomParamsOnly = getMessageWithCustomParamsOnly(i, j);
            messageWithCustomParamsOnly.voiceTranscriptionOpen = tLRPC$Message.voiceTranscriptionOpen;
            messageWithCustomParamsOnly.voiceTranscriptionRated = tLRPC$Message.voiceTranscriptionRated;
            messageWithCustomParamsOnly.voiceTranscriptionFinal = tLRPC$Message.voiceTranscriptionFinal;
            messageWithCustomParamsOnly.voiceTranscriptionId = tLRPC$Message.voiceTranscriptionId;
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            executeFast.requery();
            NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnly);
            if (writeLocalParams != null) {
                executeFast.bindByteBuffer(1, writeLocalParams);
            } else {
                executeFast.bindNull(1);
            }
            executeFast.bindInteger(2, i);
            executeFast.bindLong(3, j);
            executeFast.step();
            executeFast.dispose();
            if (writeLocalParams != null) {
                writeLocalParams.reuse();
            }
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageVoiceTranscription(final long j, final int i, final String str, final long j2, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda51
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscription$77(i, j, z, j2, str);
            }
        });
    }

    public /* synthetic */ void lambda$updateMessageVoiceTranscription$77(int i, long j, boolean z, long j2, String str) {
        try {
            this.database.beginTransaction();
            TLRPC$Message messageWithCustomParamsOnly = getMessageWithCustomParamsOnly(i, j);
            messageWithCustomParamsOnly.voiceTranscriptionFinal = z;
            messageWithCustomParamsOnly.voiceTranscriptionId = j2;
            messageWithCustomParamsOnly.voiceTranscription = str;
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            executeFast.requery();
            NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnly);
            if (writeLocalParams != null) {
                executeFast.bindByteBuffer(1, writeLocalParams);
            } else {
                executeFast.bindNull(1);
            }
            executeFast.bindInteger(2, i);
            executeFast.bindLong(3, j);
            executeFast.step();
            executeFast.dispose();
            this.database.commitTransaction();
            if (writeLocalParams == null) {
                return;
            }
            writeLocalParams.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageVoiceTranscription(final long j, final int i, final String str, final TLRPC$Message tLRPC$Message) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscription$78(i, j, tLRPC$Message, str);
            }
        });
    }

    public /* synthetic */ void lambda$updateMessageVoiceTranscription$78(int i, long j, TLRPC$Message tLRPC$Message, String str) {
        try {
            this.database.beginTransaction();
            TLRPC$Message messageWithCustomParamsOnly = getMessageWithCustomParamsOnly(i, j);
            messageWithCustomParamsOnly.voiceTranscriptionOpen = tLRPC$Message.voiceTranscriptionOpen;
            messageWithCustomParamsOnly.voiceTranscriptionRated = tLRPC$Message.voiceTranscriptionRated;
            messageWithCustomParamsOnly.voiceTranscriptionFinal = tLRPC$Message.voiceTranscriptionFinal;
            messageWithCustomParamsOnly.voiceTranscriptionId = tLRPC$Message.voiceTranscriptionId;
            messageWithCustomParamsOnly.voiceTranscription = str;
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            executeFast.requery();
            NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnly);
            if (writeLocalParams != null) {
                executeFast.bindByteBuffer(1, writeLocalParams);
            } else {
                executeFast.bindNull(1);
            }
            executeFast.bindInteger(2, i);
            executeFast.bindLong(3, j);
            executeFast.step();
            executeFast.dispose();
            this.database.commitTransaction();
            if (writeLocalParams == null) {
                return;
            }
            writeLocalParams.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageCustomParams(final long j, final TLRPC$Message tLRPC$Message) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda171
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageCustomParams$79(tLRPC$Message, j);
            }
        });
    }

    public /* synthetic */ void lambda$updateMessageCustomParams$79(TLRPC$Message tLRPC$Message, long j) {
        try {
            this.database.beginTransaction();
            TLRPC$Message messageWithCustomParamsOnly = getMessageWithCustomParamsOnly(tLRPC$Message.id, j);
            MessageCustomParamsHelper.copyParams(tLRPC$Message, messageWithCustomParamsOnly);
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            executeFast.requery();
            NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnly);
            if (writeLocalParams != null) {
                executeFast.bindByteBuffer(1, writeLocalParams);
            } else {
                executeFast.bindNull(1);
            }
            executeFast.bindInteger(2, tLRPC$Message.id);
            executeFast.bindLong(3, j);
            executeFast.step();
            executeFast.dispose();
            this.database.commitTransaction();
            if (writeLocalParams == null) {
                return;
            }
            writeLocalParams.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private TLRPC$Message getMessageWithCustomParamsOnly(int i, long j) {
        TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT custom_params FROM messages_v2 WHERE mid = " + i + " AND uid = " + j, new Object[0]);
            if (queryFinalized.next()) {
                MessageCustomParamsHelper.readLocalParams(tLRPC$TL_message, queryFinalized.byteBufferValue(0));
            }
            queryFinalized.dispose();
        } catch (SQLiteException e) {
            FileLog.e(e);
        }
        return tLRPC$TL_message;
    }

    public void getNewTask(final LongSparseArray<ArrayList<Integer>> longSparseArray, final LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda119
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getNewTask$80(longSparseArray, longSparseArray2);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x00a1, code lost:
        if (r4 > 0) goto L19;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getNewTask$80(androidx.collection.LongSparseArray r13, androidx.collection.LongSparseArray r14) {
        /*
            r12 = this;
            java.lang.String r0 = ","
            r1 = 2
            r2 = 1
            r3 = 0
            if (r13 == 0) goto L41
            int r4 = r13.size()     // Catch: java.lang.Exception -> L3e
            r5 = 0
        Lc:
            if (r5 >= r4) goto L41
            org.telegram.SQLite.SQLiteDatabase r6 = r12.database     // Catch: java.lang.Exception -> L3e
            java.util.Locale r7 = java.util.Locale.US     // Catch: java.lang.Exception -> L3e
            java.lang.String r8 = "DELETE FROM enc_tasks_v4 WHERE mid IN(%s) AND uid = %d AND media = 0"
            java.lang.Object[] r9 = new java.lang.Object[r1]     // Catch: java.lang.Exception -> L3e
            java.lang.Object r10 = r13.valueAt(r5)     // Catch: java.lang.Exception -> L3e
            java.lang.Iterable r10 = (java.lang.Iterable) r10     // Catch: java.lang.Exception -> L3e
            java.lang.String r10 = android.text.TextUtils.join(r0, r10)     // Catch: java.lang.Exception -> L3e
            r9[r3] = r10     // Catch: java.lang.Exception -> L3e
            long r10 = r13.keyAt(r5)     // Catch: java.lang.Exception -> L3e
            java.lang.Long r10 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Exception -> L3e
            r9[r2] = r10     // Catch: java.lang.Exception -> L3e
            java.lang.String r7 = java.lang.String.format(r7, r8, r9)     // Catch: java.lang.Exception -> L3e
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.executeFast(r7)     // Catch: java.lang.Exception -> L3e
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch: java.lang.Exception -> L3e
            r6.dispose()     // Catch: java.lang.Exception -> L3e
            int r5 = r5 + 1
            goto Lc
        L3e:
            r13 = move-exception
            goto Le3
        L41:
            if (r14 == 0) goto L7a
            int r13 = r14.size()     // Catch: java.lang.Exception -> L3e
            r4 = 0
        L48:
            if (r4 >= r13) goto L7a
            org.telegram.SQLite.SQLiteDatabase r5 = r12.database     // Catch: java.lang.Exception -> L3e
            java.util.Locale r6 = java.util.Locale.US     // Catch: java.lang.Exception -> L3e
            java.lang.String r7 = "DELETE FROM enc_tasks_v4 WHERE mid IN(%s) AND uid = %d AND media = 1"
            java.lang.Object[] r8 = new java.lang.Object[r1]     // Catch: java.lang.Exception -> L3e
            java.lang.Object r9 = r14.valueAt(r4)     // Catch: java.lang.Exception -> L3e
            java.lang.Iterable r9 = (java.lang.Iterable) r9     // Catch: java.lang.Exception -> L3e
            java.lang.String r9 = android.text.TextUtils.join(r0, r9)     // Catch: java.lang.Exception -> L3e
            r8[r3] = r9     // Catch: java.lang.Exception -> L3e
            long r9 = r14.keyAt(r4)     // Catch: java.lang.Exception -> L3e
            java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch: java.lang.Exception -> L3e
            r8[r2] = r9     // Catch: java.lang.Exception -> L3e
            java.lang.String r6 = java.lang.String.format(r6, r7, r8)     // Catch: java.lang.Exception -> L3e
            org.telegram.SQLite.SQLitePreparedStatement r5 = r5.executeFast(r6)     // Catch: java.lang.Exception -> L3e
            org.telegram.SQLite.SQLitePreparedStatement r5 = r5.stepThis()     // Catch: java.lang.Exception -> L3e
            r5.dispose()     // Catch: java.lang.Exception -> L3e
            int r4 = r4 + 1
            goto L48
        L7a:
            org.telegram.SQLite.SQLiteDatabase r13 = r12.database     // Catch: java.lang.Exception -> L3e
            java.lang.String r14 = "SELECT mid, date, media, uid FROM enc_tasks_v4 WHERE date = (SELECT min(date) FROM enc_tasks_v4)"
            java.lang.Object[] r0 = new java.lang.Object[r3]     // Catch: java.lang.Exception -> L3e
            org.telegram.SQLite.SQLiteCursor r13 = r13.queryFinalized(r14, r0)     // Catch: java.lang.Exception -> L3e
            r14 = 0
            r0 = r14
            r4 = 0
        L87:
            boolean r5 = r13.next()     // Catch: java.lang.Exception -> L3e
            if (r5 == 0) goto Ld8
            int r4 = r13.intValue(r3)     // Catch: java.lang.Exception -> L3e
            int r5 = r13.intValue(r2)     // Catch: java.lang.Exception -> L3e
            int r6 = r13.intValue(r1)     // Catch: java.lang.Exception -> L3e
            r7 = 3
            long r7 = r13.longValue(r7)     // Catch: java.lang.Exception -> L3e
            r9 = -1
            if (r6 != r9) goto La7
            if (r4 <= 0) goto La5
        La3:
            r6 = 1
            goto Laa
        La5:
            r6 = 0
            goto Laa
        La7:
            if (r6 == 0) goto La5
            goto La3
        Laa:
            if (r6 == 0) goto Lb5
            if (r0 != 0) goto Lb3
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray     // Catch: java.lang.Exception -> L3e
            r0.<init>()     // Catch: java.lang.Exception -> L3e
        Lb3:
            r6 = r0
            goto Lbe
        Lb5:
            if (r14 != 0) goto Lbc
            androidx.collection.LongSparseArray r14 = new androidx.collection.LongSparseArray     // Catch: java.lang.Exception -> L3e
            r14.<init>()     // Catch: java.lang.Exception -> L3e
        Lbc:
            r6 = r0
            r0 = r14
        Lbe:
            java.lang.Object r9 = r0.get(r7)     // Catch: java.lang.Exception -> L3e
            java.util.ArrayList r9 = (java.util.ArrayList) r9     // Catch: java.lang.Exception -> L3e
            if (r9 != 0) goto Lce
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch: java.lang.Exception -> L3e
            r9.<init>()     // Catch: java.lang.Exception -> L3e
            r0.put(r7, r9)     // Catch: java.lang.Exception -> L3e
        Lce:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r4)     // Catch: java.lang.Exception -> L3e
            r9.add(r0)     // Catch: java.lang.Exception -> L3e
            r4 = r5
            r0 = r6
            goto L87
        Ld8:
            r13.dispose()     // Catch: java.lang.Exception -> L3e
            org.telegram.messenger.MessagesController r13 = r12.getMessagesController()     // Catch: java.lang.Exception -> L3e
            r13.processLoadedDeleteTask(r4, r14, r0)     // Catch: java.lang.Exception -> L3e
            goto Le6
        Le3:
            org.telegram.messenger.FileLog.e(r13)
        Le6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getNewTask$80(androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public void markMentionMessageAsRead(final long j, final int i, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMentionMessageAsRead$81(i, j, j2);
            }
        });
    }

    public /* synthetic */ void lambda$markMentionMessageAsRead$81(int i, long j, long j2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase2 = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase2.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + j2, new Object[0]);
            int max = queryFinalized.next() ? Math.max(0, queryFinalized.intValue(0) - 1) : 0;
            queryFinalized.dispose();
            this.database.executeFast(String.format(locale, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(max), Long.valueOf(j2))).stepThis().dispose();
            LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
            longSparseIntArray.put(j2, max);
            if (max == 0) {
                updateFiltersReadCounter(null, longSparseIntArray, true);
            }
            getMessagesController().processDialogsUpdateRead(null, longSparseIntArray);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void markMessageAsMention(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMessageAsMention$82(i, j);
            }
        });
    }

    public /* synthetic */ void lambda$markMessageAsMention$82(int i, long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET mention = 1, read_state = read_state & ~2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetMentionsCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda68
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$resetMentionsCount$83(j, i);
            }
        });
    }

    public /* synthetic */ void lambda$resetMentionsCount$83(long j, int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + j, new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
            queryFinalized.dispose();
            if (intValue == 0 && i == 0) {
                return;
            }
            if (i == 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j))).stepThis().dispose();
            }
            this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
            LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
            longSparseIntArray.put(j, i);
            getMessagesController().processDialogsUpdateRead(null, longSparseIntArray);
            if (i != 0) {
                return;
            }
            updateFiltersReadCounter(null, longSparseIntArray, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void createTaskForMid(final long j, final int i, final int i2, final int i3, final int i4, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$createTaskForMid$85(i2, i3, i4, i, z, j);
            }
        });
    }

    public /* synthetic */ void lambda$createTaskForMid$85(int i, int i2, int i3, int i4, final boolean z, final long j) {
        try {
            int max = Math.max(i, i2) + i3;
            SparseArray<ArrayList<Integer>> sparseArray = new SparseArray<>();
            final ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.add(Integer.valueOf(i4));
            sparseArray.put(max, arrayList);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda187
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$createTaskForMid$84(z, j, arrayList);
                }
            });
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
            for (int i5 = 0; i5 < sparseArray.size(); i5++) {
                int keyAt = sparseArray.keyAt(i5);
                ArrayList<Integer> arrayList2 = sparseArray.get(keyAt);
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    executeFast.requery();
                    executeFast.bindInteger(1, arrayList2.get(i6).intValue());
                    executeFast.bindLong(2, j);
                    executeFast.bindInteger(3, keyAt);
                    executeFast.bindInteger(4, 1);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET ttl = 0 WHERE mid = %d AND uid = %d", Integer.valueOf(i4), Long.valueOf(j))).stepThis().dispose();
            getMessagesController().didAddedNewTask(max, j, sparseArray);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$createTaskForMid$84(boolean z, long j, ArrayList arrayList) {
        if (!z) {
            markMessagesContentAsRead(j, arrayList, 0);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(j), arrayList);
    }

    public void createTaskForSecretChat(final int i, final int i2, final int i3, final int i4, final ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda56
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$createTaskForSecretChat$87(i, arrayList, i4, i2, i3);
            }
        });
    }

    public /* synthetic */ void lambda$createTaskForSecretChat$87(int i, ArrayList arrayList, int i2, int i3, int i4) {
        SQLiteCursor sQLiteCursor;
        try {
            final long makeEncryptedDialogId = DialogObject.makeEncryptedDialogId(i);
            SparseArray<ArrayList<Integer>> sparseArray = new SparseArray<>();
            final ArrayList arrayList2 = new ArrayList();
            StringBuilder sb = new StringBuilder();
            if (arrayList == null) {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages_v2 WHERE uid = %d AND out = %d AND read_state > 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", Long.valueOf(makeEncryptedDialogId), Integer.valueOf(i2), Integer.valueOf(i3)), new Object[0]);
            } else {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages_v2 as m INNER JOIN randoms_v2 as r ON m.mid = r.mid AND m.uid = r.uid WHERE r.random_id IN (%s)", TextUtils.join(",", arrayList)), new Object[0]);
            }
            int i5 = ConnectionsManager.DEFAULT_DATACENTER_ID;
            while (sQLiteCursor.next()) {
                int intValue = sQLiteCursor.intValue(1);
                int intValue2 = sQLiteCursor.intValue(0);
                if (arrayList != null) {
                    arrayList2.add(Integer.valueOf(intValue2));
                }
                if (intValue > 0) {
                    int max = Math.max(i3, i4) + intValue;
                    i5 = Math.min(i5, max);
                    ArrayList<Integer> arrayList3 = sparseArray.get(max);
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList<>();
                        sparseArray.put(max, arrayList3);
                    }
                    if (sb.length() != 0) {
                        sb.append(",");
                    }
                    sb.append(intValue2);
                    arrayList3.add(Integer.valueOf(intValue2));
                }
            }
            sQLiteCursor.dispose();
            if (arrayList != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda94
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$createTaskForSecretChat$86(makeEncryptedDialogId, arrayList2);
                    }
                });
            }
            if (sparseArray.size() == 0) {
                return;
            }
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
            for (int i6 = 0; i6 < sparseArray.size(); i6++) {
                int keyAt = sparseArray.keyAt(i6);
                ArrayList<Integer> arrayList4 = sparseArray.get(keyAt);
                for (int i7 = 0; i7 < arrayList4.size(); i7++) {
                    executeFast.requery();
                    executeFast.bindInteger(1, arrayList4.get(i7).intValue());
                    executeFast.bindLong(2, makeEncryptedDialogId);
                    executeFast.bindInteger(3, keyAt);
                    executeFast.bindInteger(4, 0);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            this.database.commitTransaction();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET ttl = 0 WHERE mid IN(%s) AND uid = %d", sb.toString(), Long.valueOf(makeEncryptedDialogId))).stepThis().dispose();
            getMessagesController().didAddedNewTask(i5, makeEncryptedDialogId, sparseArray);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$createTaskForSecretChat$86(long j, ArrayList arrayList) {
        markMessagesContentAsRead(j, arrayList, 0);
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(j), arrayList);
    }

    /* JADX WARN: Code restructure failed: missing block: B:157:0x03bf, code lost:
        if (r9.indexOfKey(-r4.id) >= 0) goto L158;
     */
    /* JADX WARN: Code restructure failed: missing block: B:318:0x0670, code lost:
        if (r1.dialogsWithMentions.indexOfKey(-r0.id) < 0) goto L324;
     */
    /* JADX WARN: Removed duplicated region for block: B:196:0x047f  */
    /* JADX WARN: Removed duplicated region for block: B:210:0x04ba  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x057f  */
    /* JADX WARN: Removed duplicated region for block: B:411:0x07e3  */
    /* JADX WARN: Removed duplicated region for block: B:425:0x0819  */
    /* JADX WARN: Removed duplicated region for block: B:473:0x08cf  */
    /* JADX WARN: Removed duplicated region for block: B:581:0x0a9e  */
    /* JADX WARN: Removed duplicated region for block: B:582:0x0aa5  */
    /* JADX WARN: Type inference failed for: r3v83, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v106, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v114, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateFiltersReadCounter(org.telegram.messenger.support.LongSparseIntArray r26, org.telegram.messenger.support.LongSparseIntArray r27, boolean r28) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 2765
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateFiltersReadCounter(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, boolean):void");
    }

    public /* synthetic */ void lambda$updateFiltersReadCounter$88() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x02b7 A[Catch: Exception -> 0x02bf, TRY_LEAVE, TryCatch #0 {Exception -> 0x02bf, blocks: (B:3:0x0006, B:7:0x0022, B:8:0x003a, B:10:0x0040, B:13:0x0047, B:16:0x004e, B:18:0x0058, B:19:0x005c, B:20:0x0062, B:21:0x0067, B:24:0x006e, B:26:0x0074, B:28:0x00a3, B:29:0x00aa, B:30:0x00d0, B:32:0x00d6, B:34:0x00dd, B:35:0x0106, B:37:0x010c, B:39:0x0124, B:41:0x012a, B:43:0x0131, B:45:0x0138, B:47:0x015a, B:49:0x0161, B:50:0x016f, B:52:0x017e, B:53:0x0188, B:57:0x0199, B:59:0x01a3, B:60:0x01aa, B:61:0x01b0, B:64:0x01b7, B:66:0x01bd, B:67:0x01e5, B:69:0x01eb, B:73:0x01f5, B:75:0x0200, B:76:0x020e, B:78:0x0214, B:80:0x023c, B:82:0x0242, B:84:0x0247, B:86:0x024e, B:87:0x0264, B:88:0x0266, B:90:0x026f, B:92:0x0275, B:93:0x027e, B:95:0x0284, B:96:0x029d, B:97:0x02a0, B:98:0x02a7, B:100:0x02b7), top: B:104:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:133:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateDialogsWithReadMessagesInternal(java.util.ArrayList<java.lang.Integer> r22, org.telegram.messenger.support.LongSparseIntArray r23, org.telegram.messenger.support.LongSparseIntArray r24, androidx.collection.LongSparseArray<java.util.ArrayList<java.lang.Integer>> r25) {
        /*
            Method dump skipped, instructions count: 708
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateDialogsWithReadMessagesInternal(java.util.ArrayList, org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, androidx.collection.LongSparseArray):void");
    }

    private static boolean isEmpty(SparseArray<?> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    private static boolean isEmpty(LongSparseIntArray longSparseIntArray) {
        return longSparseIntArray == null || longSparseIntArray.size() == 0;
    }

    private static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private static boolean isEmpty(SparseIntArray sparseIntArray) {
        return sparseIntArray == null || sparseIntArray.size() == 0;
    }

    private static boolean isEmpty(LongSparseArray<?> longSparseArray) {
        return longSparseArray == null || longSparseArray.size() == 0;
    }

    public void updateDialogsWithReadMessages(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final LongSparseArray<ArrayList<Integer>> longSparseArray, boolean z) {
        if (!isEmpty(longSparseIntArray) || !isEmpty(longSparseIntArray2) || !isEmpty(longSparseArray)) {
            if (z) {
                this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda155
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$updateDialogsWithReadMessages$89(longSparseIntArray, longSparseIntArray2, longSparseArray);
                    }
                });
            } else {
                updateDialogsWithReadMessagesInternal(null, longSparseIntArray, longSparseIntArray2, longSparseArray);
            }
        }
    }

    public /* synthetic */ void lambda$updateDialogsWithReadMessages$89(LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2, LongSparseArray longSparseArray) {
        updateDialogsWithReadMessagesInternal(null, longSparseIntArray, longSparseIntArray2, longSparseArray);
    }

    public void updateChatParticipants(final TLRPC$ChatParticipants tLRPC$ChatParticipants) {
        if (tLRPC$ChatParticipants == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda161
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatParticipants$91(tLRPC$ChatParticipants);
            }
        });
    }

    public /* synthetic */ void lambda$updateChatParticipants$91(TLRPC$ChatParticipants tLRPC$ChatParticipants) {
        NativeByteBuffer byteBufferValue;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + tLRPC$ChatParticipants.chat_id, new Object[0]);
            final TLRPC$ChatFull tLRPC$ChatFull = null;
            new ArrayList();
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$ChatFull = TLRPC$ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
                tLRPC$ChatFull.pinned_msg_id = queryFinalized.intValue(1);
                tLRPC$ChatFull.online_count = queryFinalized.intValue(2);
                tLRPC$ChatFull.inviterId = queryFinalized.longValue(3);
            }
            queryFinalized.dispose();
            if (!(tLRPC$ChatFull instanceof TLRPC$TL_chatFull)) {
                return;
            }
            tLRPC$ChatFull.participants = tLRPC$ChatParticipants;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda159
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateChatParticipants$90(tLRPC$ChatFull);
                }
            });
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?)");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChatFull.getObjectSize());
            tLRPC$ChatFull.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$ChatFull.id);
            executeFast.bindByteBuffer(2, nativeByteBuffer);
            executeFast.bindInteger(3, tLRPC$ChatFull.pinned_msg_id);
            executeFast.bindInteger(4, tLRPC$ChatFull.online_count);
            executeFast.bindLong(5, tLRPC$ChatFull.inviterId);
            executeFast.bindInteger(6, tLRPC$ChatFull.invitesCount);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$updateChatParticipants$90(TLRPC$ChatFull tLRPC$ChatFull) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.postNotificationName(i, tLRPC$ChatFull, 0, bool, bool);
    }

    public void loadChannelAdmins(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda65
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadChannelAdmins$92(j);
            }
        });
    }

    public /* synthetic */ void lambda$loadChannelAdmins$92(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT uid, data FROM channel_admins_v3 WHERE did = " + j, new Object[0]);
            LongSparseArray<TLRPC$ChannelParticipant> longSparseArray = new LongSparseArray<>();
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    TLRPC$ChannelParticipant TLdeserialize = TLRPC$ChannelParticipant.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        longSparseArray.put(queryFinalized.longValue(0), TLdeserialize);
                    }
                }
            }
            queryFinalized.dispose();
            getMessagesController().processLoadedChannelAdmins(longSparseArray, j, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putChannelAdmins(final long j, final LongSparseArray<TLRPC$ChannelParticipant> longSparseArray) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda92
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putChannelAdmins$93(j, longSparseArray);
            }
        });
    }

    public /* synthetic */ void lambda$putChannelAdmins$93(long j, LongSparseArray longSparseArray) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM channel_admins_v3 WHERE did = " + j).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO channel_admins_v3 VALUES(?, ?, ?)");
            long currentTimeMillis = System.currentTimeMillis() / 1000;
            for (int i = 0; i < longSparseArray.size(); i++) {
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindLong(2, longSparseArray.keyAt(i));
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) longSparseArray.valueAt(i);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChannelParticipant.getObjectSize());
                tLRPC$ChannelParticipant.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChannelUsers(final long j, final ArrayList<TLRPC$ChannelParticipant> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda95
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChannelUsers$94(j, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$updateChannelUsers$94(long j, ArrayList arrayList) {
        long j2 = -j;
        try {
            this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + j2).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) arrayList.get(i);
                executeFast.requery();
                executeFast.bindLong(1, j2);
                executeFast.bindLong(2, MessageObject.getPeerId(tLRPC$ChannelParticipant.peer));
                executeFast.bindInteger(3, currentTimeMillis);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChannelParticipant.getObjectSize());
                tLRPC$ChannelParticipant.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(4, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                currentTimeMillis--;
            }
            executeFast.dispose();
            this.database.commitTransaction();
            loadChatInfo(j, true, null, false, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveBotCache(final String str, final TLObject tLObject) {
        if (tLObject == null || TextUtils.isEmpty(str)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda156
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveBotCache$95(tLObject, str);
            }
        });
    }

    public /* synthetic */ void lambda$saveBotCache$95(TLObject tLObject, String str) {
        int i;
        try {
            int currentTime = getConnectionsManager().getCurrentTime();
            if (tLObject instanceof TLRPC$TL_messages_botCallbackAnswer) {
                i = ((TLRPC$TL_messages_botCallbackAnswer) tLObject).cache_time;
            } else {
                if (tLObject instanceof TLRPC$TL_messages_botResults) {
                    i = ((TLRPC$TL_messages_botResults) tLObject).cache_time;
                }
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindString(1, str);
                executeFast.bindInteger(2, currentTime);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
            currentTime += i;
            SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(tLObject.getObjectSize());
            tLObject.serializeToStream(nativeByteBuffer2);
            executeFast2.bindString(1, str);
            executeFast2.bindInteger(2, currentTime);
            executeFast2.bindByteBuffer(3, nativeByteBuffer2);
            executeFast2.step();
            executeFast2.dispose();
            nativeByteBuffer2.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getBotCache(final String str, final RequestDelegate requestDelegate) {
        if (str == null || requestDelegate == null) {
            return;
        }
        final int currentTime = getConnectionsManager().getCurrentTime();
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda52
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getBotCache$96(currentTime, str, requestDelegate);
            }
        });
    }

    public /* synthetic */ void lambda$getBotCache$96(int i, String str, RequestDelegate requestDelegate) {
        Throwable th;
        TLObject tLObject;
        Exception e;
        SQLiteCursor queryFinalized;
        Exception e2;
        NativeByteBuffer byteBufferValue;
        try {
            try {
                this.database.executeFast("DELETE FROM botcache WHERE date < " + i).stepThis().dispose();
                queryFinalized = this.database.queryFinalized("SELECT data FROM botcache WHERE id = ?", str);
            } catch (Exception e3) {
                e = e3;
                tLObject = null;
            }
            if (queryFinalized.next()) {
                try {
                    byteBufferValue = queryFinalized.byteBufferValue(0);
                } catch (Exception e4) {
                    e2 = e4;
                    tLObject = null;
                }
                if (byteBufferValue != null) {
                    int readInt32 = byteBufferValue.readInt32(false);
                    if (readInt32 == TLRPC$TL_messages_botCallbackAnswer.constructor) {
                        tLObject = TLRPC$TL_messages_botCallbackAnswer.TLdeserialize(byteBufferValue, readInt32, false);
                    } else {
                        tLObject = TLRPC$messages_BotResults.TLdeserialize(byteBufferValue, readInt32, false);
                    }
                    try {
                        try {
                            byteBufferValue.reuse();
                        } catch (Exception e5) {
                            e2 = e5;
                            try {
                                FileLog.e(e2);
                                queryFinalized.dispose();
                            } catch (Exception e6) {
                                e = e6;
                                FileLog.e(e);
                                requestDelegate.run(tLObject, null);
                            }
                            requestDelegate.run(tLObject, null);
                        }
                        queryFinalized.dispose();
                        requestDelegate.run(tLObject, null);
                    } catch (Throwable th2) {
                        th = th2;
                        requestDelegate.run(tLObject, null);
                        throw th;
                    }
                }
            }
            tLObject = null;
            queryFinalized.dispose();
            requestDelegate.run(tLObject, null);
        } catch (Throwable th3) {
            th = th3;
            tLObject = null;
            requestDelegate.run(tLObject, null);
            throw th;
        }
    }

    public void loadUserInfo(final TLRPC$User tLRPC$User, final boolean z, final int i, int i2) {
        if (tLRPC$User == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda176
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadUserInfo$97(tLRPC$User, z, i);
            }
        });
    }

    public /* synthetic */ void lambda$loadUserInfo$97(TLRPC$User tLRPC$User, boolean z, int i) {
        boolean z2;
        int i2;
        TLRPC$UserFull tLRPC$UserFull;
        Exception e;
        MessagesController messagesController;
        boolean z3;
        TLRPC$User tLRPC$User2;
        boolean z4;
        int i3;
        TLRPC$UserFull tLRPC$UserFull2;
        SQLiteCursor queryFinalized;
        boolean z5;
        int i4;
        ArrayList<MessageObject> loadPinnedMessages;
        NativeByteBuffer byteBufferValue;
        HashMap<Integer, MessageObject> hashMap = new HashMap<>();
        ArrayList<Integer> arrayList = new ArrayList<>();
        try {
            SQLiteCursor queryFinalized2 = this.database.queryFinalized("SELECT info, pinned FROM user_settings WHERE uid = " + tLRPC$User.id, new Object[0]);
            boolean z6 = true;
            if (!queryFinalized2.next() || (byteBufferValue = queryFinalized2.byteBufferValue(0)) == null) {
                tLRPC$UserFull2 = null;
            } else {
                tLRPC$UserFull2 = TLRPC$UserFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                try {
                    tLRPC$UserFull2.pinned_msg_id = queryFinalized2.intValue(1);
                    byteBufferValue.reuse();
                } catch (Exception e2) {
                    e = e2;
                    tLRPC$UserFull = tLRPC$UserFull2;
                    i2 = 0;
                    z2 = false;
                    try {
                        FileLog.e(e);
                        messagesController = getMessagesController();
                        z3 = true;
                        tLRPC$User2 = tLRPC$User;
                        z4 = z;
                        i3 = i;
                        messagesController.processUserInfo(tLRPC$User2, tLRPC$UserFull, z3, z4, i3, arrayList, hashMap, i2, z2);
                    } catch (Throwable th) {
                        th = th;
                        getMessagesController().processUserInfo(tLRPC$User, tLRPC$UserFull, true, z, i, arrayList, hashMap, i2, z2);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    tLRPC$UserFull = tLRPC$UserFull2;
                    i2 = 0;
                    z2 = false;
                    getMessagesController().processUserInfo(tLRPC$User, tLRPC$UserFull, true, z, i, arrayList, hashMap, i2, z2);
                    throw th;
                }
            }
            queryFinalized2.dispose();
            SQLiteCursor queryFinalized3 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM chat_pinned_v2 WHERE uid = %d ORDER BY mid DESC", Long.valueOf(tLRPC$User.id)), new Object[0]);
            while (queryFinalized3.next()) {
                int intValue = queryFinalized3.intValue(0);
                arrayList.add(Integer.valueOf(intValue));
                hashMap.put(Integer.valueOf(intValue), null);
            }
            queryFinalized3.dispose();
            queryFinalized = this.database.queryFinalized("SELECT count, end FROM chat_pinned_count WHERE uid = " + tLRPC$User.id, new Object[0]);
            if (queryFinalized.next()) {
                int intValue2 = queryFinalized.intValue(0);
                try {
                    if (queryFinalized.intValue(1) == 0) {
                        z6 = false;
                    }
                    i4 = intValue2;
                    z5 = z6;
                } catch (Exception e3) {
                    e = e3;
                    i2 = intValue2;
                    tLRPC$UserFull = tLRPC$UserFull2;
                    z2 = false;
                    FileLog.e(e);
                    messagesController = getMessagesController();
                    z3 = true;
                    tLRPC$User2 = tLRPC$User;
                    z4 = z;
                    i3 = i;
                    messagesController.processUserInfo(tLRPC$User2, tLRPC$UserFull, z3, z4, i3, arrayList, hashMap, i2, z2);
                } catch (Throwable th3) {
                    th = th3;
                    i2 = intValue2;
                    tLRPC$UserFull = tLRPC$UserFull2;
                    z2 = false;
                    getMessagesController().processUserInfo(tLRPC$User, tLRPC$UserFull, true, z, i, arrayList, hashMap, i2, z2);
                    throw th;
                }
            } else {
                i4 = 0;
                z5 = false;
            }
        } catch (Exception e4) {
            e = e4;
            tLRPC$UserFull = null;
        } catch (Throwable th4) {
            th = th4;
            tLRPC$UserFull = null;
        }
        try {
            queryFinalized.dispose();
            if (tLRPC$UserFull2 != null && tLRPC$UserFull2.pinned_msg_id != 0 && (arrayList.isEmpty() || tLRPC$UserFull2.pinned_msg_id > arrayList.get(0).intValue())) {
                arrayList.clear();
                arrayList.add(Integer.valueOf(tLRPC$UserFull2.pinned_msg_id));
                hashMap.put(Integer.valueOf(tLRPC$UserFull2.pinned_msg_id), null);
            }
            if (!arrayList.isEmpty() && (loadPinnedMessages = getMediaDataController().loadPinnedMessages(tLRPC$User.id, 0L, arrayList, false)) != null) {
                int size = loadPinnedMessages.size();
                for (int i5 = 0; i5 < size; i5++) {
                    MessageObject messageObject = loadPinnedMessages.get(i5);
                    hashMap.put(Integer.valueOf(messageObject.getId()), messageObject);
                }
            }
            messagesController = getMessagesController();
            z3 = true;
            tLRPC$User2 = tLRPC$User;
            tLRPC$UserFull = tLRPC$UserFull2;
            z4 = z;
            i3 = i;
            i2 = i4;
            z2 = z5;
        } catch (Exception e5) {
            e = e5;
            tLRPC$UserFull = tLRPC$UserFull2;
            i2 = i4;
            z2 = z5;
            FileLog.e(e);
            messagesController = getMessagesController();
            z3 = true;
            tLRPC$User2 = tLRPC$User;
            z4 = z;
            i3 = i;
            messagesController.processUserInfo(tLRPC$User2, tLRPC$UserFull, z3, z4, i3, arrayList, hashMap, i2, z2);
        } catch (Throwable th5) {
            th = th5;
            tLRPC$UserFull = tLRPC$UserFull2;
            i2 = i4;
            z2 = z5;
            getMessagesController().processUserInfo(tLRPC$User, tLRPC$UserFull, true, z, i, arrayList, hashMap, i2, z2);
            throw th;
        }
        messagesController.processUserInfo(tLRPC$User2, tLRPC$UserFull, z3, z4, i3, arrayList, hashMap, i2, z2);
    }

    public void updateUserInfo(final TLRPC$UserFull tLRPC$UserFull, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda192
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateUserInfo$98(z, tLRPC$UserFull);
            }
        });
    }

    public /* synthetic */ void lambda$updateUserInfo$98(boolean z, TLRPC$UserFull tLRPC$UserFull) {
        if (z) {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT uid FROM user_settings WHERE uid = " + tLRPC$UserFull.user.id, new Object[0]);
                boolean next = queryFinalized.next();
                queryFinalized.dispose();
                if (!next) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$UserFull.getObjectSize());
        tLRPC$UserFull.serializeToStream(nativeByteBuffer);
        executeFast.bindLong(1, tLRPC$UserFull.user.id);
        executeFast.bindByteBuffer(2, nativeByteBuffer);
        executeFast.bindInteger(3, tLRPC$UserFull.pinned_msg_id);
        executeFast.step();
        executeFast.dispose();
        nativeByteBuffer.reuse();
        if ((tLRPC$UserFull.flags & 2048) != 0) {
            SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE dialogs SET folder_id = ? WHERE did = ?");
            executeFast2.bindInteger(1, tLRPC$UserFull.folder_id);
            executeFast2.bindLong(2, tLRPC$UserFull.user.id);
            executeFast2.step();
            executeFast2.dispose();
            this.unknownDialogsIds.remove(tLRPC$UserFull.user.id);
        }
    }

    public void saveChatInviter(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda83
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveChatInviter$99(j2, j);
            }
        });
    }

    public /* synthetic */ void lambda$saveChatInviter$99(long j, long j2) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE chat_settings_v2 SET inviter = ? WHERE uid = ?");
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindLong(2, j2);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveChatLinksCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveChatLinksCount$100(i, j);
            }
        });
    }

    public /* synthetic */ void lambda$saveChatLinksCount$100(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE chat_settings_v2 SET links = ? WHERE uid = ?");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChatInfo(final TLRPC$ChatFull tLRPC$ChatFull, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda160
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatInfo$101(tLRPC$ChatFull, z);
            }
        });
    }

    public /* synthetic */ void lambda$updateChatInfo$101(TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
        int i;
        int i2;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT online, inviter, links FROM chat_settings_v2 WHERE uid = " + tLRPC$ChatFull.id, new Object[0]);
            if (queryFinalized.next()) {
                i2 = queryFinalized.intValue(0);
                tLRPC$ChatFull.inviterId = queryFinalized.longValue(1);
                i = queryFinalized.intValue(2);
            } else {
                i2 = -1;
                i = 0;
            }
            queryFinalized.dispose();
            if (z && i2 == -1) {
                return;
            }
            if (i2 >= 0 && (tLRPC$ChatFull.flags & 8192) == 0) {
                tLRPC$ChatFull.online_count = i2;
            }
            if (i >= 0) {
                tLRPC$ChatFull.invitesCount = i;
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?)");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChatFull.getObjectSize());
            tLRPC$ChatFull.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$ChatFull.id);
            executeFast.bindByteBuffer(2, nativeByteBuffer);
            executeFast.bindInteger(3, tLRPC$ChatFull.pinned_msg_id);
            executeFast.bindInteger(4, tLRPC$ChatFull.online_count);
            executeFast.bindLong(5, tLRPC$ChatFull.inviterId);
            executeFast.bindInteger(6, tLRPC$ChatFull.invitesCount);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
            if (tLRPC$ChatFull instanceof TLRPC$TL_channelFull) {
                SQLiteDatabase sQLiteDatabase2 = this.database;
                SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized("SELECT inbox_max, outbox_max FROM dialogs WHERE did = " + (-tLRPC$ChatFull.id), new Object[0]);
                if (queryFinalized2.next() && queryFinalized2.intValue(0) < tLRPC$ChatFull.read_inbox_max_id) {
                    int intValue = queryFinalized2.intValue(1);
                    SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ?, outbox_max = ? WHERE did = ?");
                    executeFast2.bindInteger(1, tLRPC$ChatFull.unread_count);
                    executeFast2.bindInteger(2, tLRPC$ChatFull.read_inbox_max_id);
                    executeFast2.bindInteger(3, Math.max(intValue, tLRPC$ChatFull.read_outbox_max_id));
                    executeFast2.bindLong(4, -tLRPC$ChatFull.id);
                    executeFast2.step();
                    executeFast2.dispose();
                }
                queryFinalized2.dispose();
            }
            if ((tLRPC$ChatFull.flags & 2048) == 0) {
                return;
            }
            SQLitePreparedStatement executeFast3 = this.database.executeFast("UPDATE dialogs SET folder_id = ? WHERE did = ?");
            executeFast3.bindInteger(1, tLRPC$ChatFull.folder_id);
            executeFast3.bindLong(2, -tLRPC$ChatFull.id);
            executeFast3.step();
            executeFast3.dispose();
            this.unknownDialogsIds.remove(-tLRPC$ChatFull.id);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChatOnlineCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatOnlineCount$102(i, j);
            }
        });
    }

    public /* synthetic */ void lambda$updateChatOnlineCount$102(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE chat_settings_v2 SET online = ? WHERE uid = ?");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updatePinnedMessages(final long j, final ArrayList<Integer> arrayList, final boolean z, final int i, final int i2, final boolean z2, final HashMap<Integer, MessageObject> hashMap) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda191
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updatePinnedMessages$105(z, hashMap, i2, j, arrayList, i, z2);
            }
        });
    }

    public /* synthetic */ void lambda$updatePinnedMessages$105(boolean z, final HashMap hashMap, final int i, final long j, final ArrayList arrayList, int i2, boolean z2) {
        int i3;
        final boolean z3;
        final int i4;
        int i5;
        boolean z4;
        int i6;
        final boolean z5;
        int i7;
        int max;
        int i8 = 2;
        int i9 = 1;
        try {
            if (z) {
                this.database.beginTransaction();
                if (hashMap != null) {
                    if (i == 0) {
                        SQLiteDatabase sQLiteDatabase = this.database;
                        sQLiteDatabase.executeFast("DELETE FROM chat_pinned_v2 WHERE uid = " + j).stepThis().dispose();
                    }
                    i6 = 0;
                } else {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM chat_pinned_v2 WHERE uid = %d AND mid IN (%s)", Long.valueOf(j), TextUtils.join(",", arrayList)), new Object[0]);
                    i6 = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                    queryFinalized.dispose();
                }
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_pinned_v2 VALUES(?, ?, ?)");
                int size = arrayList.size();
                int i10 = 0;
                while (i10 < size) {
                    Integer num = (Integer) arrayList.get(i10);
                    executeFast.requery();
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(i8, num.intValue());
                    NativeByteBuffer nativeByteBuffer = null;
                    MessageObject messageObject = hashMap != null ? (MessageObject) hashMap.get(num) : null;
                    if (messageObject != null) {
                        nativeByteBuffer = new NativeByteBuffer(messageObject.messageOwner.getObjectSize());
                        messageObject.messageOwner.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(3, nativeByteBuffer);
                    } else {
                        executeFast.bindNull(3);
                    }
                    executeFast.step();
                    if (nativeByteBuffer != null) {
                        nativeByteBuffer.reuse();
                    }
                    i10++;
                    i8 = 2;
                }
                executeFast.dispose();
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase2 = this.database;
                Locale locale = Locale.US;
                SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(String.format(locale, "SELECT COUNT(mid) FROM chat_pinned_v2 WHERE uid = %d", Long.valueOf(j)), new Object[0]);
                int intValue = queryFinalized2.next() ? queryFinalized2.intValue(0) : 0;
                queryFinalized2.dispose();
                if (hashMap != null) {
                    max = Math.max(i2, intValue);
                    z5 = z2;
                } else {
                    SQLiteCursor queryFinalized3 = this.database.queryFinalized(String.format(locale, "SELECT count, end FROM chat_pinned_count WHERE uid = %d", Long.valueOf(j)), new Object[0]);
                    if (queryFinalized3.next()) {
                        i7 = queryFinalized3.intValue(0);
                        if (queryFinalized3.intValue(1) != 0) {
                            z5 = true;
                            queryFinalized3.dispose();
                            max = Math.max(i7 + (arrayList.size() - i6), intValue);
                        }
                    } else {
                        i7 = 0;
                    }
                    z5 = false;
                    queryFinalized3.dispose();
                    max = Math.max(i7 + (arrayList.size() - i6), intValue);
                }
                final int i11 = max;
                SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO chat_pinned_count VALUES(?, ?, ?)");
                executeFast2.requery();
                executeFast2.bindLong(1, j);
                executeFast2.bindInteger(2, i11);
                if (!z5) {
                    i9 = 0;
                }
                executeFast2.bindInteger(3, i9);
                executeFast2.step();
                executeFast2.dispose();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda98
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$updatePinnedMessages$103(j, arrayList, hashMap, i, i11, z5);
                    }
                });
                return;
            }
            if (arrayList == null) {
                SQLiteDatabase sQLiteDatabase3 = this.database;
                sQLiteDatabase3.executeFast("DELETE FROM chat_pinned_v2 WHERE uid = " + j).stepThis().dispose();
                if (DialogObject.isChatDialog(j)) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE chat_settings_v2 SET pinned = 0 WHERE uid = %d", Long.valueOf(-j))).stepThis().dispose();
                } else {
                    this.database.executeFast(String.format(Locale.US, "UPDATE user_settings SET pinned = 0 WHERE uid = %d", Long.valueOf(j))).stepThis().dispose();
                }
                i4 = 0;
                z3 = true;
                i3 = 0;
            } else {
                String join = TextUtils.join(",", arrayList);
                if (DialogObject.isChatDialog(j)) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE chat_settings_v2 SET pinned = 0 WHERE uid = %d AND pinned IN (%s)", Long.valueOf(-j), join)).stepThis().dispose();
                } else {
                    this.database.executeFast(String.format(Locale.US, "UPDATE user_settings SET pinned = 0 WHERE uid = %d AND pinned IN (%s)", Long.valueOf(j), join)).stepThis().dispose();
                }
                SQLiteDatabase sQLiteDatabase4 = this.database;
                Locale locale2 = Locale.US;
                sQLiteDatabase4.executeFast(String.format(locale2, "DELETE FROM chat_pinned_v2 WHERE uid = %d AND mid IN(%s)", Long.valueOf(j), join)).stepThis().dispose();
                SQLiteCursor queryFinalized4 = this.database.queryFinalized("SELECT changes()", new Object[0]);
                int intValue2 = queryFinalized4.next() ? queryFinalized4.intValue(0) : 0;
                queryFinalized4.dispose();
                SQLiteCursor queryFinalized5 = this.database.queryFinalized(String.format(locale2, "SELECT COUNT(mid) FROM chat_pinned_v2 WHERE uid = %d", Long.valueOf(j)), new Object[0]);
                int intValue3 = queryFinalized5.next() ? queryFinalized5.intValue(0) : 0;
                queryFinalized5.dispose();
                i3 = 0;
                SQLiteCursor queryFinalized6 = this.database.queryFinalized(String.format(locale2, "SELECT count, end FROM chat_pinned_count WHERE uid = %d", Long.valueOf(j)), new Object[0]);
                if (queryFinalized6.next()) {
                    i5 = Math.max(0, queryFinalized6.intValue(0) - intValue2);
                    z4 = queryFinalized6.intValue(1) != 0;
                } else {
                    z4 = false;
                    i5 = 0;
                }
                queryFinalized6.dispose();
                i4 = Math.max(intValue3, i5);
                z3 = z4;
            }
            SQLitePreparedStatement executeFast3 = this.database.executeFast("REPLACE INTO chat_pinned_count VALUES(?, ?, ?)");
            executeFast3.requery();
            executeFast3.bindLong(1, j);
            executeFast3.bindInteger(2, i4);
            if (z3) {
                i3 = 1;
            }
            executeFast3.bindInteger(3, i3);
            executeFast3.step();
            executeFast3.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda97
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updatePinnedMessages$104(j, arrayList, hashMap, i, i4, z3);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$updatePinnedMessages$103(long j, ArrayList arrayList, HashMap hashMap, int i, int i2, boolean z) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(j), arrayList, Boolean.TRUE, 0, hashMap, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z));
    }

    public /* synthetic */ void lambda$updatePinnedMessages$104(long j, ArrayList arrayList, HashMap hashMap, int i, int i2, boolean z) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(j), arrayList, Boolean.FALSE, 0, hashMap, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z));
    }

    public void updateChatInfo(final long j, final long j2, final int i, final long j3, final int i2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda73
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatInfo$107(j, i, j2, j3, i2);
            }
        });
    }

    public /* synthetic */ void lambda$updateChatInfo$107(long j, int i, long j2, long j3, int i2) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        NativeByteBuffer byteBufferValue;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + j, new Object[0]);
            final TLRPC$ChatFull tLRPC$ChatFull = null;
            new ArrayList();
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$ChatFull = TLRPC$ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
                tLRPC$ChatFull.pinned_msg_id = queryFinalized.intValue(1);
                tLRPC$ChatFull.online_count = queryFinalized.intValue(2);
                tLRPC$ChatFull.inviterId = queryFinalized.longValue(3);
            }
            queryFinalized.dispose();
            if (!(tLRPC$ChatFull instanceof TLRPC$TL_chatFull)) {
                return;
            }
            if (i == 1) {
                int i3 = 0;
                while (true) {
                    if (i3 >= tLRPC$ChatFull.participants.participants.size()) {
                        break;
                    } else if (tLRPC$ChatFull.participants.participants.get(i3).user_id == j2) {
                        tLRPC$ChatFull.participants.participants.remove(i3);
                        break;
                    } else {
                        i3++;
                    }
                }
            } else if (i == 0) {
                Iterator<TLRPC$ChatParticipant> it = tLRPC$ChatFull.participants.participants.iterator();
                while (it.hasNext()) {
                    if (it.next().user_id == j2) {
                        return;
                    }
                }
                TLRPC$TL_chatParticipant tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipant();
                tLRPC$TL_chatParticipant.user_id = j2;
                tLRPC$TL_chatParticipant.inviter_id = j3;
                tLRPC$TL_chatParticipant.date = getConnectionsManager().getCurrentTime();
                tLRPC$ChatFull.participants.participants.add(tLRPC$TL_chatParticipant);
            } else if (i == 2) {
                int i4 = 0;
                while (true) {
                    if (i4 >= tLRPC$ChatFull.participants.participants.size()) {
                        break;
                    }
                    TLRPC$ChatParticipant tLRPC$ChatParticipant2 = tLRPC$ChatFull.participants.participants.get(i4);
                    if (tLRPC$ChatParticipant2.user_id == j2) {
                        if (j3 == 1) {
                            tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantAdmin();
                        } else {
                            tLRPC$ChatParticipant = new TLRPC$TL_chatParticipant();
                        }
                        tLRPC$ChatParticipant.user_id = tLRPC$ChatParticipant2.user_id;
                        tLRPC$ChatParticipant.date = tLRPC$ChatParticipant2.date;
                        tLRPC$ChatParticipant.inviter_id = tLRPC$ChatParticipant2.inviter_id;
                        tLRPC$ChatFull.participants.participants.set(i4, tLRPC$ChatParticipant);
                    } else {
                        i4++;
                    }
                }
            }
            tLRPC$ChatFull.participants.version = i2;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda158
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateChatInfo$106(tLRPC$ChatFull);
                }
            });
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?)");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChatFull.getObjectSize());
            tLRPC$ChatFull.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, j);
            executeFast.bindByteBuffer(2, nativeByteBuffer);
            executeFast.bindInteger(3, tLRPC$ChatFull.pinned_msg_id);
            executeFast.bindInteger(4, tLRPC$ChatFull.online_count);
            executeFast.bindLong(5, tLRPC$ChatFull.inviterId);
            executeFast.bindInteger(6, tLRPC$ChatFull.invitesCount);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$updateChatInfo$106(TLRPC$ChatFull tLRPC$ChatFull) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.postNotificationName(i, tLRPC$ChatFull, 0, bool, bool);
    }

    public boolean isMigratedChat(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda115
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$isMigratedChat$108(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$isMigratedChat$108(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        NativeByteBuffer byteBufferValue;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + j, new Object[0]);
                TLRPC$ChatFull tLRPC$ChatFull = null;
                new ArrayList();
                if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                    tLRPC$ChatFull = TLRPC$ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
                queryFinalized.dispose();
                zArr[0] = (tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.migrated_from_chat_id != 0;
                countDownLatch.countDown();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public boolean hasInviteMeMessage(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda116
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$hasInviteMeMessage$109(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$hasInviteMeMessage$109(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            try {
                long clientUserId = getUserConfig().getClientUserId();
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + (-j) + " AND out = 0 ORDER BY mid DESC LIMIT 100", new Object[0]);
                while (true) {
                    if (!queryFinalized.next()) {
                        break;
                    }
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLRPC$MessageAction tLRPC$MessageAction = TLdeserialize.action;
                        if ((tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatAddUser) && tLRPC$MessageAction.users.contains(Long.valueOf(clientUserId))) {
                            zArr[0] = true;
                            break;
                        }
                    }
                }
                queryFinalized.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(16:(3:187|6|(27:8|185|9|10|165|20|21|178|(5:23|(4:26|(2:28|194)(1:195)|29|24)|193|30|(1:32))(2:35|(25:37|(9:180|40|(1:42)(1:43)|44|(1:46)(1:47)|(4:49|167|50|(1:52))(1:55)|(4:58|(1:60)|61|200)|65|38)|196|66|(4:69|(2:71|203)(2:72|202)|73|67)|201|74|(1:76)|78|(1:82)|177|85|(6:189|88|89|90|91|86)|204|96|97|(6:99|100|191|101|(1:103)(1:104)|105)(1:110)|171|111|(3:182|113|(1:119))|122|(4:124|(1:126)(1:127)|128|(3:130|(2:132|133)|205))|134|159|160))|77|78|(2:80|82)|177|85|(1:86)|204|96|97|(0)(0)|171|111|(0)|122|(0)|134|159|160))|177|85|(1:86)|204|96|97|(0)(0)|171|111|(0)|122|(0)|134|159|160) */
    /* JADX WARN: Can't wrap try/catch for region: R(28:2|(3:183|3|4)|(3:187|6|(27:8|185|9|10|165|20|21|178|(5:23|(4:26|(2:28|194)(1:195)|29|24)|193|30|(1:32))(2:35|(25:37|(9:180|40|(1:42)(1:43)|44|(1:46)(1:47)|(4:49|167|50|(1:52))(1:55)|(4:58|(1:60)|61|200)|65|38)|196|66|(4:69|(2:71|203)(2:72|202)|73|67)|201|74|(1:76)|78|(1:82)|177|85|(6:189|88|89|90|91|86)|204|96|97|(6:99|100|191|101|(1:103)(1:104)|105)(1:110)|171|111|(3:182|113|(1:119))|122|(4:124|(1:126)(1:127)|128|(3:130|(2:132|133)|205))|134|159|160))|77|78|(2:80|82)|177|85|(1:86)|204|96|97|(0)(0)|171|111|(0)|122|(0)|134|159|160))|19|165|20|21|178|(0)(0)|77|78|(0)|177|85|(1:86)|204|96|97|(0)(0)|171|111|(0)|122|(0)|134|159|160|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x02f1, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:139:0x02f2, code lost:
        r14 = r7;
        r12 = r19;
        r13 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x0306, code lost:
        r0 = e;
     */
    /* JADX WARN: Removed duplicated region for block: B:110:0x0266  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x02a7 A[Catch: all -> 0x029e, Exception -> 0x02f1, TRY_ENTER, TryCatch #9 {Exception -> 0x02f1, blocks: (B:111:0x026a, B:113:0x026f, B:115:0x0273, B:117:0x0279, B:119:0x0287, B:122:0x02a1, B:124:0x02a7, B:128:0x02b1, B:130:0x02bc, B:132:0x02c2), top: B:171:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:182:0x026f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:189:0x0203 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x007e A[Catch: all -> 0x00b8, Exception -> 0x0306, TRY_ENTER, TryCatch #14 {all -> 0x00b8, blocks: (B:23:0x007e, B:24:0x0084, B:26:0x008e, B:28:0x009e, B:29:0x00a1, B:30:0x00a9, B:32:0x00af, B:37:0x00c0, B:38:0x00e7, B:40:0x00ed, B:42:0x00f3, B:44:0x0100, B:46:0x0106, B:90:0x020d, B:99:0x0249), top: B:178:0x007c }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00bc A[Catch: all -> 0x0302, Exception -> 0x0306, TRY_ENTER, TRY_LEAVE, TryCatch #1 {all -> 0x0302, blocks: (B:20:0x0075, B:35:0x00bc, B:97:0x0227), top: B:165:0x0075 }] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x01b8 A[Catch: all -> 0x01d5, Exception -> 0x02fd, TryCatch #11 {Exception -> 0x02fd, blocks: (B:64:0x016a, B:66:0x0174, B:67:0x017f, B:69:0x0187, B:71:0x0195, B:73:0x019b, B:74:0x01a4, B:76:0x01aa, B:80:0x01b8, B:82:0x01be, B:85:0x01db, B:86:0x01fd), top: B:174:0x016a }] */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0249 A[Catch: all -> 0x00b8, Exception -> 0x0306, TRY_ENTER, TRY_LEAVE, TryCatch #14 {all -> 0x00b8, blocks: (B:23:0x007e, B:24:0x0084, B:26:0x008e, B:28:0x009e, B:29:0x00a1, B:30:0x00a9, B:32:0x00af, B:37:0x00c0, B:38:0x00e7, B:40:0x00ed, B:42:0x00f3, B:44:0x0100, B:46:0x0106, B:90:0x020d, B:99:0x0249), top: B:178:0x007c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private org.telegram.tgnet.TLRPC$ChatFull loadChatInfoInternal(long r22, boolean r24, boolean r25, boolean r26, int r27) {
        /*
            Method dump skipped, instructions count: 827
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadChatInfoInternal(long, boolean, boolean, boolean, int):org.telegram.tgnet.TLRPC$ChatFull");
    }

    public TLRPC$ChatFull loadChatInfo(long j, boolean z, CountDownLatch countDownLatch, boolean z2, boolean z3) {
        return loadChatInfo(j, z, countDownLatch, z2, z3, 0);
    }

    public TLRPC$ChatFull loadChatInfo(final long j, final boolean z, final CountDownLatch countDownLatch, final boolean z2, final boolean z3, final int i) {
        final TLRPC$ChatFull[] tLRPC$ChatFullArr = new TLRPC$ChatFull[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda194
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadChatInfo$110(tLRPC$ChatFullArr, j, z, z2, z3, i, countDownLatch);
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Throwable unused) {
            }
        }
        return tLRPC$ChatFullArr[0];
    }

    public /* synthetic */ void lambda$loadChatInfo$110(TLRPC$ChatFull[] tLRPC$ChatFullArr, long j, boolean z, boolean z2, boolean z3, int i, CountDownLatch countDownLatch) {
        tLRPC$ChatFullArr[0] = loadChatInfoInternal(j, z, z2, z3, i);
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void processPendingRead(final long j, final int i, final int i2, final int i3) {
        final int i4 = this.lastSavedDate;
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda71
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$processPendingRead$111(j, i, i3, i4, i2);
            }
        });
    }

    public /* synthetic */ void lambda$processPendingRead$111(long j, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8 = i4;
        long j2 = 0;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            int i9 = 0;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT unread_count, inbox_max, last_mid FROM dialogs WHERE did = " + j, new Object[0]);
            if (queryFinalized.next()) {
                i7 = queryFinalized.intValue(0);
                i6 = i7;
                i5 = queryFinalized.intValue(1);
                j2 = queryFinalized.longValue(2);
            } else {
                i7 = 0;
                i6 = 0;
                i5 = 0;
            }
            queryFinalized.dispose();
            this.database.beginTransaction();
            if (!DialogObject.isEncryptedDialog(j)) {
                i8 = Math.max(i5, i);
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, i8);
                executeFast.step();
                executeFast.dispose();
                if (i8 < j2) {
                    SQLiteCursor queryFinalized2 = this.database.queryFinalized("SELECT changes()", new Object[0]);
                    int intValue = queryFinalized2.next() ? queryFinalized2.intValue(0) + i2 : 0;
                    queryFinalized2.dispose();
                    i9 = Math.max(0, i7 - intValue);
                }
                SQLitePreparedStatement executeFast2 = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                executeFast2.requery();
                executeFast2.bindLong(1, j);
                executeFast2.bindInteger(2, i8);
                executeFast2.step();
                executeFast2.dispose();
                SQLitePreparedStatement executeFast3 = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND date <= ?");
                executeFast3.requery();
                executeFast3.bindLong(1, j);
                executeFast3.bindInteger(2, i3);
                executeFast3.step();
                executeFast3.dispose();
            } else {
                SQLitePreparedStatement executeFast4 = this.database.executeFast("UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = ? AND mid >= ? AND read_state IN(0,2) AND out = 0");
                executeFast4.requery();
                executeFast4.bindLong(1, j);
                executeFast4.bindInteger(2, i8);
                executeFast4.step();
                executeFast4.dispose();
                if (i8 > j2) {
                    SQLiteCursor queryFinalized3 = this.database.queryFinalized("SELECT changes()", new Object[0]);
                    int intValue2 = queryFinalized3.next() ? queryFinalized3.intValue(0) + i2 : 0;
                    queryFinalized3.dispose();
                    i9 = Math.max(0, i7 - intValue2);
                }
            }
            SQLitePreparedStatement executeFast5 = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ? WHERE did = ?");
            executeFast5.requery();
            executeFast5.bindInteger(1, i9);
            executeFast5.bindInteger(2, i8);
            executeFast5.bindLong(3, j);
            executeFast5.step();
            executeFast5.dispose();
            this.database.commitTransaction();
            if (i6 != 0 && i9 == 0) {
                LongSparseIntArray longSparseIntArray = new LongSparseIntArray();
                longSparseIntArray.put(j, i9);
                updateFiltersReadCounter(longSparseIntArray, null, true);
            }
            updateWidgets(j);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putContacts(ArrayList<TLRPC$TL_contact> arrayList, final boolean z) {
        if (!arrayList.isEmpty() || z) {
            final ArrayList arrayList2 = new ArrayList(arrayList);
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda190
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putContacts$112(z, arrayList2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$putContacts$112(boolean z, ArrayList arrayList) {
        if (z) {
            try {
                this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$TL_contact tLRPC$TL_contact = (TLRPC$TL_contact) arrayList.get(i);
            executeFast.requery();
            int i2 = 1;
            executeFast.bindLong(1, tLRPC$TL_contact.user_id);
            if (!tLRPC$TL_contact.mutual) {
                i2 = 0;
            }
            executeFast.bindInteger(2, i2);
            executeFast.step();
        }
        executeFast.dispose();
        this.database.commitTransaction();
    }

    public void deleteContacts(final ArrayList<Long> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda137
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteContacts$113(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$deleteContacts$113(ArrayList arrayList) {
        try {
            String join = TextUtils.join(",", arrayList);
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM contacts WHERE uid IN(" + join + ")").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void applyPhoneBookUpdates(final String str, final String str2) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda124
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$applyPhoneBookUpdates$114(str, str2);
            }
        });
    }

    public /* synthetic */ void lambda$applyPhoneBookUpdates$114(String str, String str2) {
        try {
            if (str.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", str)).stepThis().dispose();
            }
            if (str2.length() == 0) {
                return;
            }
            this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", str2)).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putCachedPhoneBook(final HashMap<String, ContactsController.Contact> hashMap, final boolean z, boolean z2) {
        if (hashMap != null) {
            if (hashMap.isEmpty() && !z && !z2) {
                return;
            }
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda149
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putCachedPhoneBook$115(hashMap, z);
                }
            });
        }
    }

    public /* synthetic */ void lambda$putCachedPhoneBook$115(HashMap hashMap, boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d(this.currentAccount + " save contacts to db " + hashMap.size());
            }
            this.database.executeFast("DELETE FROM user_contacts_v7 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM user_phones_v7 WHERE 1").stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_contacts_v7 VALUES(?, ?, ?, ?, ?)");
            SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO user_phones_v7 VALUES(?, ?, ?, ?)");
            Iterator it = hashMap.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ContactsController.Contact contact = (ContactsController.Contact) ((Map.Entry) it.next()).getValue();
                if (!contact.phones.isEmpty() && !contact.shortPhones.isEmpty()) {
                    executeFast.requery();
                    executeFast.bindString(1, contact.key);
                    executeFast.bindInteger(2, contact.contact_id);
                    executeFast.bindString(3, contact.first_name);
                    executeFast.bindString(4, contact.last_name);
                    executeFast.bindInteger(5, contact.imported);
                    executeFast.step();
                    for (int i = 0; i < contact.phones.size(); i++) {
                        executeFast2.requery();
                        executeFast2.bindString(1, contact.key);
                        executeFast2.bindString(2, contact.phones.get(i));
                        executeFast2.bindString(3, contact.shortPhones.get(i));
                        executeFast2.bindInteger(4, contact.phoneDeleted.get(i).intValue());
                        executeFast2.step();
                    }
                }
            }
            executeFast.dispose();
            executeFast2.dispose();
            this.database.commitTransaction();
            if (!z) {
                return;
            }
            this.database.executeFast("DROP TABLE IF EXISTS user_contacts_v6;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS user_phones_v6;").stepThis().dispose();
            getCachedPhoneBook(false);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getCachedPhoneBook(final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda183
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getCachedPhoneBook$116(z);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:69:0x013a, code lost:
        if (r10 != null) goto L64;
     */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00f9 A[Catch: all -> 0x0132, TRY_LEAVE, TryCatch #2 {all -> 0x0132, blocks: (B:51:0x00e9, B:53:0x00f9), top: B:124:0x00e9 }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x012a  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0144 A[Catch: all -> 0x01ee, Exception -> 0x01f0, TRY_ENTER, TryCatch #0 {Exception -> 0x01f0, blocks: (B:72:0x0144, B:73:0x015e, B:75:0x0169, B:77:0x016f, B:79:0x017b, B:81:0x019d, B:82:0x019f, B:84:0x01a3, B:85:0x01a5, B:86:0x01a8, B:89:0x01b0, B:92:0x01bc, B:94:0x01c2, B:96:0x01c8, B:97:0x01cc, B:99:0x01ea), top: B:120:0x0142, outer: #8 }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x015e A[Catch: all -> 0x01ee, Exception -> 0x01f0, TryCatch #0 {Exception -> 0x01f0, blocks: (B:72:0x0144, B:73:0x015e, B:75:0x0169, B:77:0x016f, B:79:0x017b, B:81:0x019d, B:82:0x019f, B:84:0x01a3, B:85:0x01a5, B:86:0x01a8, B:89:0x01b0, B:92:0x01bc, B:94:0x01c2, B:96:0x01c8, B:97:0x01cc, B:99:0x01ea), top: B:120:0x0142, outer: #8 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x016f A[Catch: all -> 0x01ee, Exception -> 0x01f0, TryCatch #0 {Exception -> 0x01f0, blocks: (B:72:0x0144, B:73:0x015e, B:75:0x0169, B:77:0x016f, B:79:0x017b, B:81:0x019d, B:82:0x019f, B:84:0x01a3, B:85:0x01a5, B:86:0x01a8, B:89:0x01b0, B:92:0x01bc, B:94:0x01c2, B:96:0x01c8, B:97:0x01cc, B:99:0x01ea), top: B:120:0x0142, outer: #8 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getCachedPhoneBook$116(boolean r25) {
        /*
            Method dump skipped, instructions count: 554
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getCachedPhoneBook$116(boolean):void");
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getContacts$117();
            }
        });
    }

    public /* synthetic */ void lambda$getContacts$117() {
        ArrayList<TLRPC$TL_contact> arrayList = new ArrayList<>();
        ArrayList<TLRPC$User> arrayList2 = new ArrayList<>();
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
            StringBuilder sb = new StringBuilder();
            while (queryFinalized.next()) {
                TLRPC$TL_contact tLRPC$TL_contact = new TLRPC$TL_contact();
                tLRPC$TL_contact.user_id = queryFinalized.intValue(0);
                tLRPC$TL_contact.mutual = queryFinalized.intValue(1) == 1;
                if (sb.length() != 0) {
                    sb.append(",");
                }
                arrayList.add(tLRPC$TL_contact);
                sb.append(tLRPC$TL_contact.user_id);
            }
            queryFinalized.dispose();
            if (sb.length() != 0) {
                getUsersInternal(sb.toString(), arrayList2);
            }
        } catch (Exception e) {
            arrayList.clear();
            arrayList2.clear();
            FileLog.e(e);
        }
        getContactsController().processLoadedContacts(arrayList, arrayList2, 1);
    }

    public void getUnsentMessages(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getUnsentMessages$118(i);
            }
        });
    }

    public /* synthetic */ void lambda$getUnsentMessages$118(int i) {
        int i2;
        try {
            SparseArray sparseArray = new SparseArray();
            ArrayList<TLRPC$Message> arrayList = new ArrayList<>();
            ArrayList arrayList2 = new ArrayList();
            ArrayList<TLRPC$User> arrayList3 = new ArrayList<>();
            ArrayList<TLRPC$Chat> arrayList4 = new ArrayList<>();
            ArrayList<TLRPC$EncryptedChat> arrayList5 = new ArrayList<>();
            ArrayList<Long> arrayList6 = new ArrayList<>();
            ArrayList arrayList7 = new ArrayList();
            ArrayList arrayList8 = new ArrayList();
            SQLiteDatabase sQLiteDatabase = this.database;
            boolean z = false;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages_v2 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid AND r.uid = m.uid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY m.mid DESC LIMIT " + i, new Object[0]);
            while (true) {
                i2 = 1;
                if (!queryFinalized.next()) {
                    break;
                }
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z), z);
                    TLdeserialize.send_state = queryFinalized.intValue(2);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    if (sparseArray.indexOfKey(TLdeserialize.id) < 0) {
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(0));
                        TLdeserialize.id = queryFinalized.intValue(3);
                        TLdeserialize.date = queryFinalized.intValue(4);
                        if (!queryFinalized.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized.longValue(5);
                        }
                        TLdeserialize.dialog_id = queryFinalized.longValue(6);
                        TLdeserialize.seq_in = queryFinalized.intValue(7);
                        TLdeserialize.seq_out = queryFinalized.intValue(8);
                        TLdeserialize.ttl = queryFinalized.intValue(9);
                        arrayList.add(TLdeserialize);
                        sparseArray.put(TLdeserialize.id, TLdeserialize);
                        if (DialogObject.isEncryptedDialog(TLdeserialize.dialog_id)) {
                            int encryptedChatId = DialogObject.getEncryptedChatId(TLdeserialize.dialog_id);
                            if (!arrayList8.contains(Integer.valueOf(encryptedChatId))) {
                                arrayList8.add(Integer.valueOf(encryptedChatId));
                            }
                        } else if (DialogObject.isUserDialog(TLdeserialize.dialog_id)) {
                            if (!arrayList6.contains(Long.valueOf(TLdeserialize.dialog_id))) {
                                arrayList6.add(Long.valueOf(TLdeserialize.dialog_id));
                            }
                        } else if (!arrayList7.contains(Long.valueOf(-TLdeserialize.dialog_id))) {
                            arrayList7.add(Long.valueOf(-TLdeserialize.dialog_id));
                        }
                        addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7);
                        if (TLdeserialize.send_state != 3 && ((TLdeserialize.peer_id.channel_id == 0 && !MessageObject.isUnread(TLdeserialize) && !DialogObject.isEncryptedDialog(TLdeserialize.dialog_id)) || TLdeserialize.id > 0)) {
                            TLdeserialize.send_state = 0;
                        }
                    }
                }
                z = false;
            }
            queryFinalized.dispose();
            boolean z2 = false;
            SQLiteCursor queryFinalized2 = this.database.queryFinalized("SELECT m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, m.ttl FROM scheduled_messages_v2 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid AND r.uid = m.uid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY date ASC", new Object[0]);
            while (queryFinalized2.next()) {
                int i3 = z2 ? 1 : 0;
                int i4 = z2 ? 1 : 0;
                NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(i3);
                if (byteBufferValue2 != null) {
                    TLRPC$Message TLdeserialize2 = TLRPC$Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(z2), z2);
                    TLdeserialize2.send_state = queryFinalized2.intValue(i2);
                    TLdeserialize2.readAttachPath(byteBufferValue2, getUserConfig().clientUserId);
                    byteBufferValue2.reuse();
                    if (sparseArray.indexOfKey(TLdeserialize2.id) < 0) {
                        TLdeserialize2.id = queryFinalized2.intValue(2);
                        TLdeserialize2.date = queryFinalized2.intValue(3);
                        if (!queryFinalized2.isNull(4)) {
                            TLdeserialize2.random_id = queryFinalized2.longValue(4);
                        }
                        TLdeserialize2.dialog_id = queryFinalized2.longValue(5);
                        TLdeserialize2.ttl = queryFinalized2.intValue(6);
                        arrayList2.add(TLdeserialize2);
                        sparseArray.put(TLdeserialize2.id, TLdeserialize2);
                        if (DialogObject.isEncryptedDialog(TLdeserialize2.dialog_id)) {
                            int encryptedChatId2 = DialogObject.getEncryptedChatId(TLdeserialize2.dialog_id);
                            if (!arrayList8.contains(Integer.valueOf(encryptedChatId2))) {
                                arrayList8.add(Integer.valueOf(encryptedChatId2));
                            }
                        } else if (DialogObject.isUserDialog(TLdeserialize2.dialog_id)) {
                            if (!arrayList6.contains(Long.valueOf(TLdeserialize2.dialog_id))) {
                                arrayList6.add(Long.valueOf(TLdeserialize2.dialog_id));
                            }
                        } else if (!arrayList7.contains(Long.valueOf(-TLdeserialize2.dialog_id))) {
                            arrayList7.add(Long.valueOf(-TLdeserialize2.dialog_id));
                        }
                        addUsersAndChatsFromMessage(TLdeserialize2, arrayList6, arrayList7);
                        if (TLdeserialize2.send_state != 3) {
                            if ((TLdeserialize2.peer_id.channel_id == 0 && !MessageObject.isUnread(TLdeserialize2) && !DialogObject.isEncryptedDialog(TLdeserialize2.dialog_id)) || TLdeserialize2.id > 0) {
                                TLdeserialize2.send_state = 0;
                            }
                            z2 = false;
                            i2 = 1;
                        }
                    }
                }
                z2 = false;
                i2 = 1;
            }
            queryFinalized2.dispose();
            if (!arrayList8.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", arrayList8), arrayList5, arrayList6);
            }
            if (!arrayList6.isEmpty()) {
                getUsersInternal(TextUtils.join(",", arrayList6), arrayList3);
            }
            if (!arrayList7.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i5 = 0; i5 < arrayList7.size(); i5++) {
                    Long l = (Long) arrayList7.get(i5);
                    if (sb.length() != 0) {
                        sb.append(",");
                    }
                    sb.append(l);
                }
                getChatsInternal(sb.toString(), arrayList4);
            }
            getSendMessagesHelper().processUnsentMessages(arrayList, arrayList2, arrayList3, arrayList4, arrayList5);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean checkMessageByRandomId(final long j) {
        final boolean[] zArr = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda114
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$checkMessageByRandomId$119(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x002a, code lost:
        if (r0 == null) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$checkMessageByRandomId$119(long r7, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
            r6 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r6.database     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.util.Locale r2 = java.util.Locale.US     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.lang.String r3 = "SELECT random_id FROM randoms_v2 WHERE random_id = %d"
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            r8 = 0
            r5[r8] = r7     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.lang.String r7 = java.lang.String.format(r2, r3, r5)     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.lang.Object[] r2 = new java.lang.Object[r8]     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r7, r2)     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            boolean r7 = r0.next()     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            if (r7 == 0) goto L2c
            r9[r8] = r4     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            goto L2c
        L24:
            r7 = move-exception
            goto L33
        L26:
            r7 = move-exception
            org.telegram.messenger.FileLog.e(r7)     // Catch: java.lang.Throwable -> L24
            if (r0 == 0) goto L2f
        L2c:
            r0.dispose()
        L2f:
            r10.countDown()
            return
        L33:
            if (r0 == 0) goto L38
            r0.dispose()
        L38:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageByRandomId$119(long, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public boolean checkMessageId(final long j, final int i) {
        final boolean[] zArr = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda81
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$checkMessageId$120(j, i, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0031, code lost:
        if (r0 == null) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$checkMessageId$120(long r6, int r8, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
            r5 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r5.database     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.util.Locale r2 = java.util.Locale.US     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.String r3 = "SELECT mid FROM messages_v2 WHERE uid = %d AND mid = %d"
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r7 = 0
            r4[r7] = r6     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.Integer r6 = java.lang.Integer.valueOf(r8)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r8 = 1
            r4[r8] = r6     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.String r6 = java.lang.String.format(r2, r3, r4)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.Object[] r2 = new java.lang.Object[r7]     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r6, r2)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            boolean r6 = r0.next()     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            if (r6 == 0) goto L33
            r9[r7] = r8     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            goto L33
        L2b:
            r6 = move-exception
            goto L3a
        L2d:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> L2b
            if (r0 == 0) goto L36
        L33:
            r0.dispose()
        L36:
            r10.countDown()
            return
        L3a:
            if (r0 == 0) goto L3f
            r0.dispose()
        L3f:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageId$120(long, int, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void getUnreadMention(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda104
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getUnreadMention$122(j, intCallback);
            }
        });
    }

    public /* synthetic */ void lambda$getUnreadMention$122(long j, final IntCallback intCallback) {
        try {
            final int i = 0;
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages_v2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.IntCallback.this.run(i);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getMessagesCount(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda102
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getMessagesCount$124(j, intCallback);
            }
        });
    }

    public /* synthetic */ void lambda$getMessagesCount$124(long j, final IntCallback intCallback) {
        try {
            final int i = 0;
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages_v2 WHERE uid = %d", Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.IntCallback.this.run(i);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(8:68|(7:800|(3:96|(1:188)(4:103|(6:105|(10:107|108|109|110|111|879|112|113|(1:115)(1:116)|117)(1:120)|790|121|868|(10:123|124|829|125|126|127|(4:129|130|766|131)(1:132)|133|(5:135|(1:137)(1:138)|788|139|140)(1:143)|144)(2:149|(5:151|(1:153)(1:154)|155|(3:157|(1:159)|160)|161)(5:162|(1:164)(1:165)|166|(3:168|(1:170)|171)|172)))(1:178)|179|(4:183|775|184|(1:186)(1:187))(1:182))|189)(6:71|72|(8:74|75|76|77|78|79|794|80)(1:88)|862|89|90)|190|191|768|192|780)|(5:(12:(3:194|195|(6:197|777|205|206|820|(12:257|(1:259)(1:260)|860|261|262|(1:297)(10:802|265|(2:864|267)(1:269)|270|271|804|272|(1:274)(1:275)|276|(6:(1:280)(4:281|(1:283)|284|(5:286|(1:288)|289|290|(1:292)))|(1:300)(1:301)|(7:786|303|304|851|305|(1:307)|308)(1:313)|(10:315|(1:317)(1:318)|319|(1:321)(1:322)|323|(2:328|333)|329|(1:331)|332|333)(3:336|(6:839|338|(1:340)(1:341)|342|343|(4:835|345|346|354))(1:351)|352)|353|354))|298|(0)(0)|(0)(0)|(0)(0)|353|354)(4:213|(4:215|(1:217)(1:218)|219|(1:221)(1:222))(1:(1:(4:227|(1:229)(1:230)|231|(1:233)(1:234))(1:235))(12:238|(1:240)(1:241)|770|242|243|774|244|(1:246)(1:247)|248|(1:250)(1:251)|252|237))|236|237)))(21:199|(2:201|(1:203))|204|777|205|206|820|(1:208)|257|(0)(0)|860|261|262|(0)|297|298|(0)(0)|(0)(0)|(0)(0)|353|354)|860|261|262|(0)|297|298|(0)(0)|(0)(0)|(0)(0)|353|354)|820|(0)|257|(0)(0))|779|204|777|205|206) */
    /* JADX WARN: Code restructure failed: missing block: B:359:0x0b5e, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:557:0x109f, code lost:
        if (r1.reply_to_random_id != 0) goto L560;
     */
    /* JADX WARN: Removed duplicated region for block: B:208:0x0530  */
    /* JADX WARN: Removed duplicated region for block: B:259:0x081d A[Catch: Exception -> 0x0607, TRY_ENTER, TRY_LEAVE, TryCatch #31 {Exception -> 0x0607, blocks: (B:215:0x053d, B:217:0x0571, B:219:0x0577, B:221:0x057c, B:222:0x05c5, B:227:0x060e, B:229:0x0632, B:231:0x0638, B:233:0x063d, B:234:0x0686, B:235:0x06c7, B:238:0x0718, B:240:0x0734, B:259:0x081d), top: B:820:0x052e }] */
    /* JADX WARN: Removed duplicated region for block: B:260:0x0822  */
    /* JADX WARN: Removed duplicated region for block: B:264:0x0829 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:300:0x091e  */
    /* JADX WARN: Removed duplicated region for block: B:301:0x0920  */
    /* JADX WARN: Removed duplicated region for block: B:313:0x0966  */
    /* JADX WARN: Removed duplicated region for block: B:315:0x096a A[Catch: Exception -> 0x0a9d, TryCatch #47 {Exception -> 0x0a9d, blocks: (B:305:0x092a, B:308:0x0956, B:315:0x096a, B:317:0x0990, B:319:0x0996, B:321:0x09bd, B:323:0x09c3, B:328:0x09cc, B:332:0x0a30, B:340:0x0ac5), top: B:851:0x092a }] */
    /* JADX WARN: Removed duplicated region for block: B:336:0x0aa2  */
    /* JADX WARN: Removed duplicated region for block: B:611:0x11a4  */
    /* JADX WARN: Removed duplicated region for block: B:758:0x1525  */
    /* JADX WARN: Removed duplicated region for block: B:760:0x1549  */
    /* JADX WARN: Removed duplicated region for block: B:761:0x1551  */
    /* JADX WARN: Removed duplicated region for block: B:786:0x0923 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:852:0x115a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.Runnable getMessagesInternal(final long r46, final long r48, int r50, int r51, final int r52, int r53, final int r54, final int r55, final boolean r56, final int r57, final int r58, final boolean r59) {
        /*
            Method dump skipped, instructions count: 5489
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getMessagesInternal(long, long, int, int, int, int, int, int, boolean, int, int, boolean):java.lang.Runnable");
    }

    public static /* synthetic */ int lambda$getMessagesInternal$125(TLRPC$Message tLRPC$Message, TLRPC$Message tLRPC$Message2) {
        int i;
        int i2;
        int i3 = tLRPC$Message.id;
        if (i3 > 0 && (i2 = tLRPC$Message2.id) > 0) {
            if (i3 > i2) {
                return -1;
            }
            return i3 < i2 ? 1 : 0;
        } else if (i3 < 0 && (i = tLRPC$Message2.id) < 0) {
            if (i3 < i) {
                return -1;
            }
            return i3 > i ? 1 : 0;
        } else {
            int i4 = tLRPC$Message.date;
            int i5 = tLRPC$Message2.date;
            if (i4 > i5) {
                return -1;
            }
            return i4 < i5 ? 1 : 0;
        }
    }

    public /* synthetic */ void lambda$getMessagesInternal$126(TLRPC$TL_messages_messages tLRPC$TL_messages_messages, int i, long j, long j2, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, boolean z, boolean z2, int i11, int i12, boolean z3, int i13, boolean z4) {
        getMessagesController().processLoadedMessages(tLRPC$TL_messages_messages, i, j, j2, i2, i3, i4, true, i5, i6, i7, i8, i9, i10, z, z2 ? 1 : 0, i11, i12, z3, i13, z4);
    }

    public void getMessages(final long j, final long j2, boolean z, final int i, final int i2, final int i3, final int i4, final int i5, final int i6, final boolean z2, final int i7, final int i8, final boolean z3) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda86
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getMessages$127(j, j2, i, i2, i3, i4, i5, i6, z2, i7, i8, z3);
            }
        });
    }

    public /* synthetic */ void lambda$getMessages$127(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7, int i8, boolean z2) {
        Utilities.stageQueue.postRunnable(getMessagesInternal(j, j2, i, i2, i3, i4, i5, i6, z, i7, i8, z2));
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearSentMedia$128();
            }
        });
    }

    public /* synthetic */ void lambda$clearSentMedia$128() {
        try {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public Object[] getSentFile(final String str, final int i) {
        if (str == null || str.toLowerCase().endsWith("attheme")) {
            return null;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Object[] objArr = new Object[2];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda123
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getSentFile$129(str, i, objArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (objArr[0] == null) {
            return null;
        }
        return objArr;
    }

    public /* synthetic */ void lambda$getSentFile$129(String str, int i, Object[] objArr, CountDownLatch countDownLatch) {
        NativeByteBuffer byteBufferValue;
        try {
            try {
                String MD5 = Utilities.MD5(str);
                if (MD5 != null) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, parent FROM sent_files_v2 WHERE uid = '%s' AND type = %d", MD5, Integer.valueOf(i)), new Object[0]);
                    if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                        TLRPC$MessageMedia TLdeserialize = TLRPC$MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize instanceof TLRPC$TL_messageMediaDocument) {
                            objArr[0] = ((TLRPC$TL_messageMediaDocument) TLdeserialize).document;
                        } else if (TLdeserialize instanceof TLRPC$TL_messageMediaPhoto) {
                            objArr[0] = ((TLRPC$TL_messageMediaPhoto) TLdeserialize).photo;
                        }
                        if (objArr[0] != null) {
                            objArr[1] = queryFinalized.stringValue(1);
                        }
                    }
                    queryFinalized.dispose();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    private void updateWidgets(long j) {
        ArrayList<Long> arrayList = new ArrayList<>();
        arrayList.add(Long.valueOf(j));
        updateWidgets(arrayList);
    }

    private void updateWidgets(ArrayList<Long> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        AppWidgetManager appWidgetManager = null;
        try {
            TextUtils.join(",", arrayList);
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT DISTINCT id FROM shortcut_widget WHERE did IN(%s,-1)", TextUtils.join(",", arrayList)), new Object[0]);
            while (queryFinalized.next()) {
                if (appWidgetManager == null) {
                    appWidgetManager = AppWidgetManager.getInstance(ApplicationLoader.applicationContext);
                }
                appWidgetManager.notifyAppWidgetViewDataChanged(queryFinalized.intValue(0), R.id.list_view);
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putWidgetDialogs(final int i, final ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda55
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putWidgetDialogs$130(i, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$putWidgetDialogs$130(int i, ArrayList arrayList) {
        try {
            this.database.beginTransaction();
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM shortcut_widget WHERE id = " + i).stepThis().dispose();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO shortcut_widget VALUES(?, ?, ?)");
            if (arrayList.isEmpty()) {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindLong(2, -1L);
                executeFast.bindInteger(3, 0);
                executeFast.step();
            } else {
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    long longValue = ((Long) arrayList.get(i2)).longValue();
                    executeFast.requery();
                    executeFast.bindInteger(1, i);
                    executeFast.bindLong(2, longValue);
                    executeFast.bindInteger(3, i2);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearWidgetDialogs(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearWidgetDialogs$131(i);
            }
        });
    }

    public /* synthetic */ void lambda$clearWidgetDialogs$131(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM shortcut_widget WHERE id = " + i).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getWidgetDialogIds(final int i, final int i2, final ArrayList<Long> arrayList, final ArrayList<TLRPC$User> arrayList2, final ArrayList<TLRPC$Chat> arrayList3, final boolean z) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda60
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getWidgetDialogIds$132(i, arrayList, arrayList2, arrayList3, z, i2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$getWidgetDialogIds$132(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, boolean z, int i2, CountDownLatch countDownLatch) {
        try {
            try {
                ArrayList arrayList4 = new ArrayList();
                ArrayList arrayList5 = new ArrayList();
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(i)), new Object[0]);
                while (queryFinalized.next()) {
                    long longValue = queryFinalized.longValue(0);
                    if (longValue != -1) {
                        arrayList.add(Long.valueOf(longValue));
                        if (arrayList2 != null && arrayList3 != null) {
                            if (DialogObject.isUserDialog(longValue)) {
                                arrayList4.add(Long.valueOf(longValue));
                            } else {
                                arrayList5.add(Long.valueOf(-longValue));
                            }
                        }
                    }
                }
                queryFinalized.dispose();
                if (!z && arrayList.isEmpty()) {
                    if (i2 == 0) {
                        SQLiteCursor queryFinalized2 = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = 0 ORDER BY pinned DESC, date DESC LIMIT 0,10", new Object[0]);
                        while (queryFinalized2.next()) {
                            long longValue2 = queryFinalized2.longValue(0);
                            if (!DialogObject.isFolderDialogId(longValue2)) {
                                arrayList.add(Long.valueOf(longValue2));
                                if (arrayList2 != null && arrayList3 != null) {
                                    if (DialogObject.isUserDialog(longValue2)) {
                                        arrayList4.add(Long.valueOf(longValue2));
                                    } else {
                                        arrayList5.add(Long.valueOf(-longValue2));
                                    }
                                }
                            }
                        }
                        queryFinalized2.dispose();
                    } else {
                        SQLiteCursor queryFinalized3 = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                        while (queryFinalized3.next()) {
                            long longValue3 = queryFinalized3.longValue(0);
                            arrayList.add(Long.valueOf(longValue3));
                            if (arrayList2 != null && arrayList3 != null) {
                                if (DialogObject.isUserDialog(longValue3)) {
                                    arrayList4.add(Long.valueOf(longValue3));
                                } else {
                                    arrayList5.add(Long.valueOf(-longValue3));
                                }
                            }
                        }
                        queryFinalized3.dispose();
                    }
                }
                if (arrayList2 != null && arrayList3 != null) {
                    if (!arrayList5.isEmpty()) {
                        getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
                    }
                    if (!arrayList4.isEmpty()) {
                        getUsersInternal(TextUtils.join(",", arrayList4), arrayList2);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void getWidgetDialogs(final int i, final int i2, final ArrayList<Long> arrayList, final LongSparseArray<TLRPC$Dialog> longSparseArray, final LongSparseArray<TLRPC$Message> longSparseArray2, final ArrayList<TLRPC$User> arrayList2, final ArrayList<TLRPC$Chat> arrayList3) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda57
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getWidgetDialogs$133(i, arrayList, i2, longSparseArray, longSparseArray2, arrayList3, arrayList2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$getWidgetDialogs$133(int i, ArrayList arrayList, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, ArrayList arrayList2, ArrayList arrayList3, CountDownLatch countDownLatch) {
        boolean z;
        SQLiteCursor sQLiteCursor;
        try {
            try {
                ArrayList arrayList4 = new ArrayList();
                ArrayList arrayList5 = new ArrayList();
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(i)), new Object[0]);
                while (queryFinalized.next()) {
                    long longValue = queryFinalized.longValue(0);
                    if (longValue != -1) {
                        arrayList.add(Long.valueOf(longValue));
                        if (DialogObject.isUserDialog(longValue)) {
                            arrayList4.add(Long.valueOf(longValue));
                        } else {
                            arrayList5.add(Long.valueOf(-longValue));
                        }
                    }
                }
                queryFinalized.dispose();
                if (arrayList.isEmpty() && i2 == 1) {
                    SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                    while (queryFinalized2.next()) {
                        long longValue2 = queryFinalized2.longValue(0);
                        arrayList.add(Long.valueOf(longValue2));
                        if (DialogObject.isUserDialog(longValue2)) {
                            arrayList4.add(Long.valueOf(longValue2));
                        } else {
                            arrayList5.add(Long.valueOf(-longValue2));
                        }
                    }
                    queryFinalized2.dispose();
                }
                if (arrayList.isEmpty()) {
                    sQLiteCursor = this.database.queryFinalized("SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.folder_id = 0 ORDER BY d.pinned DESC, d.date DESC LIMIT 0,10", new Object[0]);
                    z = true;
                } else {
                    sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.did IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
                    z = false;
                }
                while (sQLiteCursor.next()) {
                    long longValue3 = sQLiteCursor.longValue(0);
                    if (!DialogObject.isFolderDialogId(longValue3)) {
                        if (z) {
                            arrayList.add(Long.valueOf(longValue3));
                        }
                        TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
                        tLRPC$TL_dialog.id = longValue3;
                        tLRPC$TL_dialog.top_message = sQLiteCursor.intValue(1);
                        tLRPC$TL_dialog.unread_count = sQLiteCursor.intValue(2);
                        tLRPC$TL_dialog.last_message_date = sQLiteCursor.intValue(3);
                        longSparseArray.put(tLRPC$TL_dialog.id, tLRPC$TL_dialog);
                        NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(4);
                        if (byteBufferValue != null) {
                            TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            MessageObject.setUnreadFlags(TLdeserialize, sQLiteCursor.intValue(5));
                            TLdeserialize.id = sQLiteCursor.intValue(6);
                            TLdeserialize.send_state = sQLiteCursor.intValue(7);
                            int intValue = sQLiteCursor.intValue(8);
                            if (intValue != 0) {
                                tLRPC$TL_dialog.last_message_date = intValue;
                            }
                            long j = tLRPC$TL_dialog.id;
                            TLdeserialize.dialog_id = j;
                            longSparseArray2.put(j, TLdeserialize);
                            addUsersAndChatsFromMessage(TLdeserialize, arrayList4, arrayList5);
                        }
                    }
                }
                sQLiteCursor.dispose();
                if (!z && arrayList.size() > longSparseArray.size()) {
                    int size = arrayList.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        long longValue4 = ((Long) arrayList.get(i3)).longValue();
                        if (longSparseArray.get(((Long) arrayList.get(i3)).longValue()) == null) {
                            TLRPC$TL_dialog tLRPC$TL_dialog2 = new TLRPC$TL_dialog();
                            tLRPC$TL_dialog2.id = longValue4;
                            longSparseArray.put(longValue4, tLRPC$TL_dialog2);
                            if (DialogObject.isChatDialog(longValue4)) {
                                long j2 = -longValue4;
                                if (arrayList5.contains(Long.valueOf(j2))) {
                                    arrayList5.add(Long.valueOf(j2));
                                }
                            } else if (arrayList4.contains(Long.valueOf(longValue4))) {
                                arrayList4.add(Long.valueOf(longValue4));
                            }
                        }
                    }
                }
                if (!arrayList5.isEmpty()) {
                    getChatsInternal(TextUtils.join(",", arrayList5), arrayList2);
                }
                if (!arrayList4.isEmpty()) {
                    getUsersInternal(TextUtils.join(",", arrayList4), arrayList3);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void putSentFile(final String str, final TLObject tLObject, final int i, final String str2) {
        if (str == null || tLObject == null || str2 == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda125
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putSentFile$134(str, tLObject, i, str2);
            }
        });
    }

    public /* synthetic */ void lambda$putSentFile$134(String str, TLObject tLObject, int i, String str2) {
        TLRPC$MessageMedia tLRPC$MessageMedia;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                String MD5 = Utilities.MD5(str);
                if (MD5 != null) {
                    if (tLObject instanceof TLRPC$Photo) {
                        tLRPC$MessageMedia = new TLRPC$TL_messageMediaPhoto();
                        tLRPC$MessageMedia.photo = (TLRPC$Photo) tLObject;
                        tLRPC$MessageMedia.flags |= 1;
                    } else if (tLObject instanceof TLRPC$Document) {
                        tLRPC$MessageMedia = new TLRPC$TL_messageMediaDocument();
                        tLRPC$MessageMedia.document = (TLRPC$Document) tLObject;
                        tLRPC$MessageMedia.flags |= 1;
                    } else {
                        tLRPC$MessageMedia = null;
                    }
                    if (tLRPC$MessageMedia == null) {
                        return;
                    }
                    sQLitePreparedStatement = this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?, ?)");
                    sQLitePreparedStatement.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$MessageMedia.getObjectSize());
                    tLRPC$MessageMedia.serializeToStream(nativeByteBuffer);
                    sQLitePreparedStatement.bindString(1, MD5);
                    sQLitePreparedStatement.bindInteger(2, i);
                    sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer);
                    sQLitePreparedStatement.bindString(4, str2);
                    sQLitePreparedStatement.step();
                    nativeByteBuffer.reuse();
                }
                if (sQLitePreparedStatement == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatSeq(final TLRPC$EncryptedChat tLRPC$EncryptedChat, final boolean z) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda168
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatSeq$135(tLRPC$EncryptedChat, z);
            }
        });
    }

    public /* synthetic */ void lambda$updateEncryptedChatSeq$135(TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, tLRPC$EncryptedChat.seq_in);
                sQLitePreparedStatement.bindInteger(2, tLRPC$EncryptedChat.seq_out);
                sQLitePreparedStatement.bindInteger(3, (tLRPC$EncryptedChat.key_use_count_in << 16) | tLRPC$EncryptedChat.key_use_count_out);
                sQLitePreparedStatement.bindInteger(4, tLRPC$EncryptedChat.in_seq_no);
                sQLitePreparedStatement.bindInteger(5, tLRPC$EncryptedChat.mtproto_seq);
                sQLitePreparedStatement.bindInteger(6, tLRPC$EncryptedChat.id);
                sQLitePreparedStatement.step();
                if (z && tLRPC$EncryptedChat.in_seq_no != 0) {
                    long encryptedChatId = DialogObject.getEncryptedChatId(tLRPC$EncryptedChat.id);
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_v2 WHERE mid IN (SELECT m.mid FROM messages_v2 as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d) AND uid = %d", Long.valueOf(encryptedChatId), Integer.valueOf(tLRPC$EncryptedChat.in_seq_no), Long.valueOf(encryptedChatId))).stepThis().dispose();
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatTTL(final TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda165
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatTTL$136(tLRPC$EncryptedChat);
            }
        });
    }

    public /* synthetic */ void lambda$updateEncryptedChatTTL$136(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, tLRPC$EncryptedChat.ttl);
                sQLitePreparedStatement.bindInteger(2, tLRPC$EncryptedChat.id);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatLayer(final TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda164
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatLayer$137(tLRPC$EncryptedChat);
            }
        });
    }

    public /* synthetic */ void lambda$updateEncryptedChatLayer$137(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, tLRPC$EncryptedChat.layer);
                sQLitePreparedStatement.bindInteger(2, tLRPC$EncryptedChat.id);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChat(final TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda166
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChat$138(tLRPC$EncryptedChat);
            }
        });
    }

    public /* synthetic */ void lambda$updateEncryptedChat$138(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        byte[] bArr;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                byte[] bArr2 = tLRPC$EncryptedChat.key_hash;
                if ((bArr2 == null || bArr2.length < 16) && (bArr = tLRPC$EncryptedChat.auth_key) != null) {
                    tLRPC$EncryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(bArr);
                }
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ?, mtproto_seq = ? WHERE uid = ?");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$EncryptedChat.getObjectSize());
                byte[] bArr3 = tLRPC$EncryptedChat.a_or_b;
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(bArr3 != null ? bArr3.length : 1);
                byte[] bArr4 = tLRPC$EncryptedChat.auth_key;
                NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(bArr4 != null ? bArr4.length : 1);
                byte[] bArr5 = tLRPC$EncryptedChat.future_auth_key;
                NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(bArr5 != null ? bArr5.length : 1);
                byte[] bArr6 = tLRPC$EncryptedChat.key_hash;
                NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(bArr6 != null ? bArr6.length : 1);
                tLRPC$EncryptedChat.serializeToStream(nativeByteBuffer);
                sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                byte[] bArr7 = tLRPC$EncryptedChat.a_or_b;
                if (bArr7 != null) {
                    nativeByteBuffer2.writeBytes(bArr7);
                }
                byte[] bArr8 = tLRPC$EncryptedChat.auth_key;
                if (bArr8 != null) {
                    nativeByteBuffer3.writeBytes(bArr8);
                }
                byte[] bArr9 = tLRPC$EncryptedChat.future_auth_key;
                if (bArr9 != null) {
                    nativeByteBuffer4.writeBytes(bArr9);
                }
                byte[] bArr10 = tLRPC$EncryptedChat.key_hash;
                if (bArr10 != null) {
                    nativeByteBuffer5.writeBytes(bArr10);
                }
                sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer2);
                sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer3);
                sQLitePreparedStatement.bindInteger(4, tLRPC$EncryptedChat.ttl);
                sQLitePreparedStatement.bindInteger(5, tLRPC$EncryptedChat.layer);
                sQLitePreparedStatement.bindInteger(6, tLRPC$EncryptedChat.seq_in);
                sQLitePreparedStatement.bindInteger(7, tLRPC$EncryptedChat.seq_out);
                sQLitePreparedStatement.bindInteger(8, (tLRPC$EncryptedChat.key_use_count_in << 16) | tLRPC$EncryptedChat.key_use_count_out);
                sQLitePreparedStatement.bindLong(9, tLRPC$EncryptedChat.exchange_id);
                sQLitePreparedStatement.bindInteger(10, tLRPC$EncryptedChat.key_create_date);
                sQLitePreparedStatement.bindLong(11, tLRPC$EncryptedChat.future_key_fingerprint);
                sQLitePreparedStatement.bindByteBuffer(12, nativeByteBuffer4);
                sQLitePreparedStatement.bindByteBuffer(13, nativeByteBuffer5);
                sQLitePreparedStatement.bindInteger(14, tLRPC$EncryptedChat.in_seq_no);
                sQLitePreparedStatement.bindLong(15, tLRPC$EncryptedChat.admin_id);
                sQLitePreparedStatement.bindInteger(16, tLRPC$EncryptedChat.mtproto_seq);
                sQLitePreparedStatement.bindInteger(17, tLRPC$EncryptedChat.id);
                sQLitePreparedStatement.step();
                nativeByteBuffer.reuse();
                nativeByteBuffer2.reuse();
                nativeByteBuffer3.reuse();
                nativeByteBuffer4.reuse();
                nativeByteBuffer5.reuse();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void isDialogHasTopMessage(final long j, final Runnable runnable) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda93
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$isDialogHasTopMessage$139(j, runnable);
            }
        });
    }

    public /* synthetic */ void lambda$isDialogHasTopMessage$139(long j, Runnable runnable) {
        boolean z = false;
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT last_mid FROM dialogs WHERE did = %d", Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next() && queryFinalized.intValue(0) != 0) {
                z = true;
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!z) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public boolean hasAuthMessage(final int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$hasAuthMessage$140(i, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$hasAuthMessage$140(int i, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            try {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages_v2 WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", Integer.valueOf(i)), new Object[0]);
                zArr[0] = queryFinalized.next();
                queryFinalized.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void getEncryptedChat(final long j, final CountDownLatch countDownLatch, final ArrayList<TLObject> arrayList) {
        if (countDownLatch == null || arrayList == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda99
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getEncryptedChat$141(j, arrayList, countDownLatch);
            }
        });
    }

    public /* synthetic */ void lambda$getEncryptedChat$141(long j, ArrayList arrayList, CountDownLatch countDownLatch) {
        try {
            try {
                ArrayList<Long> arrayList2 = new ArrayList<>();
                ArrayList<TLRPC$EncryptedChat> arrayList3 = new ArrayList<>();
                getEncryptedChatsInternal("" + j, arrayList3, arrayList2);
                if (!arrayList3.isEmpty() && !arrayList2.isEmpty()) {
                    ArrayList<TLRPC$User> arrayList4 = new ArrayList<>();
                    getUsersInternal(TextUtils.join(",", arrayList2), arrayList4);
                    if (!arrayList4.isEmpty()) {
                        arrayList.add(arrayList3.get(0));
                        arrayList.add(arrayList4.get(0));
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void putEncryptedChat(final TLRPC$EncryptedChat tLRPC$EncryptedChat, final TLRPC$User tLRPC$User, final TLRPC$Dialog tLRPC$Dialog) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda167
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putEncryptedChat$142(tLRPC$EncryptedChat, tLRPC$User, tLRPC$Dialog);
            }
        });
    }

    public /* synthetic */ void lambda$putEncryptedChat$142(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$User tLRPC$User, TLRPC$Dialog tLRPC$Dialog) {
        byte[] bArr;
        try {
            byte[] bArr2 = tLRPC$EncryptedChat.key_hash;
            if ((bArr2 == null || bArr2.length < 16) && (bArr = tLRPC$EncryptedChat.auth_key) != null) {
                tLRPC$EncryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(bArr);
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$EncryptedChat.getObjectSize());
            byte[] bArr3 = tLRPC$EncryptedChat.a_or_b;
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(bArr3 != null ? bArr3.length : 1);
            byte[] bArr4 = tLRPC$EncryptedChat.auth_key;
            NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(bArr4 != null ? bArr4.length : 1);
            byte[] bArr5 = tLRPC$EncryptedChat.future_auth_key;
            NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(bArr5 != null ? bArr5.length : 1);
            byte[] bArr6 = tLRPC$EncryptedChat.key_hash;
            NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(bArr6 != null ? bArr6.length : 1);
            tLRPC$EncryptedChat.serializeToStream(nativeByteBuffer);
            executeFast.bindInteger(1, tLRPC$EncryptedChat.id);
            executeFast.bindLong(2, tLRPC$User.id);
            executeFast.bindString(3, formatUserSearchName(tLRPC$User));
            executeFast.bindByteBuffer(4, nativeByteBuffer);
            byte[] bArr7 = tLRPC$EncryptedChat.a_or_b;
            if (bArr7 != null) {
                nativeByteBuffer2.writeBytes(bArr7);
            }
            byte[] bArr8 = tLRPC$EncryptedChat.auth_key;
            if (bArr8 != null) {
                nativeByteBuffer3.writeBytes(bArr8);
            }
            byte[] bArr9 = tLRPC$EncryptedChat.future_auth_key;
            if (bArr9 != null) {
                nativeByteBuffer4.writeBytes(bArr9);
            }
            byte[] bArr10 = tLRPC$EncryptedChat.key_hash;
            if (bArr10 != null) {
                nativeByteBuffer5.writeBytes(bArr10);
            }
            executeFast.bindByteBuffer(5, nativeByteBuffer2);
            executeFast.bindByteBuffer(6, nativeByteBuffer3);
            executeFast.bindInteger(7, tLRPC$EncryptedChat.ttl);
            executeFast.bindInteger(8, tLRPC$EncryptedChat.layer);
            executeFast.bindInteger(9, tLRPC$EncryptedChat.seq_in);
            executeFast.bindInteger(10, tLRPC$EncryptedChat.seq_out);
            executeFast.bindInteger(11, tLRPC$EncryptedChat.key_use_count_out | (tLRPC$EncryptedChat.key_use_count_in << 16));
            executeFast.bindLong(12, tLRPC$EncryptedChat.exchange_id);
            executeFast.bindInteger(13, tLRPC$EncryptedChat.key_create_date);
            executeFast.bindLong(14, tLRPC$EncryptedChat.future_key_fingerprint);
            executeFast.bindByteBuffer(15, nativeByteBuffer4);
            executeFast.bindByteBuffer(16, nativeByteBuffer5);
            executeFast.bindInteger(17, tLRPC$EncryptedChat.in_seq_no);
            executeFast.bindLong(18, tLRPC$EncryptedChat.admin_id);
            executeFast.bindInteger(19, tLRPC$EncryptedChat.mtproto_seq);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
            nativeByteBuffer2.reuse();
            nativeByteBuffer3.reuse();
            nativeByteBuffer4.reuse();
            nativeByteBuffer5.reuse();
            if (tLRPC$Dialog == null) {
                return;
            }
            SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast2.bindLong(1, tLRPC$Dialog.id);
            executeFast2.bindInteger(2, tLRPC$Dialog.last_message_date);
            executeFast2.bindInteger(3, tLRPC$Dialog.unread_count);
            executeFast2.bindInteger(4, tLRPC$Dialog.top_message);
            executeFast2.bindInteger(5, tLRPC$Dialog.read_inbox_max_id);
            executeFast2.bindInteger(6, tLRPC$Dialog.read_outbox_max_id);
            executeFast2.bindInteger(7, 0);
            executeFast2.bindInteger(8, tLRPC$Dialog.unread_mentions_count);
            executeFast2.bindInteger(9, tLRPC$Dialog.pts);
            executeFast2.bindInteger(10, 0);
            executeFast2.bindInteger(11, tLRPC$Dialog.pinnedNum);
            executeFast2.bindInteger(12, tLRPC$Dialog.flags);
            executeFast2.bindInteger(13, tLRPC$Dialog.folder_id);
            executeFast2.bindNull(14);
            executeFast2.bindInteger(15, tLRPC$Dialog.unread_reactions_count);
            executeFast2.step();
            executeFast2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private String formatUserSearchName(TLRPC$User tLRPC$User) {
        StringBuilder sb = new StringBuilder();
        String str = tLRPC$User.first_name;
        if (str != null && str.length() > 0) {
            sb.append(tLRPC$User.first_name);
        }
        String str2 = tLRPC$User.last_name;
        if (str2 != null && str2.length() > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(tLRPC$User.last_name);
        }
        sb.append(";;;");
        String str3 = tLRPC$User.username;
        if (str3 != null && str3.length() > 0) {
            sb.append(tLRPC$User.username);
        }
        return sb.toString().toLowerCase();
    }

    private void putUsersInternal(ArrayList<TLRPC$User> arrayList) throws Exception {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$User tLRPC$User = arrayList.get(i);
            if (tLRPC$User.min) {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", Long.valueOf(tLRPC$User.id)), new Object[0]);
                if (queryFinalized.next()) {
                    try {
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize != null) {
                                String str = tLRPC$User.username;
                                if (str != null) {
                                    TLdeserialize.username = str;
                                    TLdeserialize.flags |= 8;
                                } else {
                                    TLdeserialize.username = null;
                                    TLdeserialize.flags &= -9;
                                }
                                if (tLRPC$User.apply_min_photo) {
                                    TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User.photo;
                                    if (tLRPC$UserProfilePhoto != null) {
                                        TLdeserialize.photo = tLRPC$UserProfilePhoto;
                                        TLdeserialize.flags |= 32;
                                    } else {
                                        TLdeserialize.photo = null;
                                        TLdeserialize.flags &= -33;
                                    }
                                }
                                tLRPC$User = TLdeserialize;
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                queryFinalized.dispose();
            }
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$User.getObjectSize());
            tLRPC$User.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$User.id);
            executeFast.bindString(2, formatUserSearchName(tLRPC$User));
            TLRPC$UserStatus tLRPC$UserStatus = tLRPC$User.status;
            if (tLRPC$UserStatus != null) {
                if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusRecently) {
                    tLRPC$UserStatus.expires = -100;
                } else if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusLastWeek) {
                    tLRPC$UserStatus.expires = -101;
                } else if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusLastMonth) {
                    tLRPC$UserStatus.expires = -102;
                }
                executeFast.bindInteger(3, tLRPC$UserStatus.expires);
            } else {
                executeFast.bindInteger(3, 0);
            }
            executeFast.bindByteBuffer(4, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
        }
        executeFast.dispose();
    }

    public void updateChatDefaultBannedRights(final long j, final TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, final int i) {
        if (tLRPC$TL_chatBannedRights == null || j == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda78
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatDefaultBannedRights$143(j, i, tLRPC$TL_chatBannedRights);
            }
        });
    }

    public /* synthetic */ void lambda$updateChatDefaultBannedRights$143(long j, int i, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights) {
        NativeByteBuffer byteBufferValue;
        TLRPC$Chat tLRPC$Chat = null;
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$Chat = TLRPC$Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$Chat == null) {
                return;
            }
            if (tLRPC$Chat.default_banned_rights != null && i < tLRPC$Chat.version) {
                return;
            }
            tLRPC$Chat.default_banned_rights = tLRPC$TL_chatBannedRights;
            tLRPC$Chat.flags |= 262144;
            tLRPC$Chat.version = i;
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE chats SET data = ? WHERE uid = ?");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Chat.getObjectSize());
            tLRPC$Chat.serializeToStream(nativeByteBuffer);
            executeFast.bindByteBuffer(1, nativeByteBuffer);
            executeFast.bindLong(2, tLRPC$Chat.id);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void putChatsInternal(ArrayList<TLRPC$Chat> arrayList) throws Exception {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$Chat tLRPC$Chat = arrayList.get(i);
            if (tLRPC$Chat.min) {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", Long.valueOf(tLRPC$Chat.id)), new Object[0]);
                if (queryFinalized.next()) {
                    try {
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLRPC$Chat TLdeserialize = TLRPC$Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize != null) {
                                TLdeserialize.title = tLRPC$Chat.title;
                                TLdeserialize.photo = tLRPC$Chat.photo;
                                TLdeserialize.broadcast = tLRPC$Chat.broadcast;
                                TLdeserialize.verified = tLRPC$Chat.verified;
                                TLdeserialize.megagroup = tLRPC$Chat.megagroup;
                                TLdeserialize.call_not_empty = tLRPC$Chat.call_not_empty;
                                TLdeserialize.call_active = tLRPC$Chat.call_active;
                                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights;
                                if (tLRPC$TL_chatBannedRights != null) {
                                    TLdeserialize.default_banned_rights = tLRPC$TL_chatBannedRights;
                                    TLdeserialize.flags |= 262144;
                                }
                                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
                                if (tLRPC$TL_chatAdminRights != null) {
                                    TLdeserialize.admin_rights = tLRPC$TL_chatAdminRights;
                                    TLdeserialize.flags |= 16384;
                                }
                                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = tLRPC$Chat.banned_rights;
                                if (tLRPC$TL_chatBannedRights2 != null) {
                                    TLdeserialize.banned_rights = tLRPC$TL_chatBannedRights2;
                                    TLdeserialize.flags |= 32768;
                                }
                                String str = tLRPC$Chat.username;
                                if (str != null) {
                                    TLdeserialize.username = str;
                                    TLdeserialize.flags |= 64;
                                } else {
                                    TLdeserialize.username = null;
                                    TLdeserialize.flags &= -65;
                                }
                                tLRPC$Chat = TLdeserialize;
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                queryFinalized.dispose();
            }
            executeFast.requery();
            tLRPC$Chat.flags |= 131072;
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Chat.getObjectSize());
            tLRPC$Chat.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$Chat.id);
            String str2 = tLRPC$Chat.title;
            if (str2 != null) {
                executeFast.bindString(2, str2.toLowerCase());
            } else {
                executeFast.bindString(2, "");
            }
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
        }
        executeFast.dispose();
    }

    public void getUsersInternal(String str, ArrayList<TLRPC$User> arrayList) throws Exception {
        if (str == null || str.length() == 0 || arrayList == null) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", str), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        TLRPC$UserStatus tLRPC$UserStatus = TLdeserialize.status;
                        if (tLRPC$UserStatus != null) {
                            tLRPC$UserStatus.expires = queryFinalized.intValue(1);
                        }
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        queryFinalized.dispose();
    }

    public void getChatsInternal(String str, ArrayList<TLRPC$Chat> arrayList) throws Exception {
        if (str == null || str.length() == 0 || arrayList == null) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", str), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$Chat TLdeserialize = TLRPC$Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        queryFinalized.dispose();
    }

    public void getEncryptedChatsInternal(String str, ArrayList<TLRPC$EncryptedChat> arrayList, ArrayList<Long> arrayList2) throws Exception {
        if (str == null || str.length() == 0 || arrayList == null) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", str), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$EncryptedChat TLdeserialize = TLRPC$EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        long longValue = queryFinalized.longValue(1);
                        TLdeserialize.user_id = longValue;
                        if (arrayList2 != null && !arrayList2.contains(Long.valueOf(longValue))) {
                            arrayList2.add(Long.valueOf(TLdeserialize.user_id));
                        }
                        TLdeserialize.a_or_b = queryFinalized.byteArrayValue(2);
                        TLdeserialize.auth_key = queryFinalized.byteArrayValue(3);
                        TLdeserialize.ttl = queryFinalized.intValue(4);
                        TLdeserialize.layer = queryFinalized.intValue(5);
                        TLdeserialize.seq_in = queryFinalized.intValue(6);
                        TLdeserialize.seq_out = queryFinalized.intValue(7);
                        int intValue = queryFinalized.intValue(8);
                        TLdeserialize.key_use_count_in = (short) (intValue >> 16);
                        TLdeserialize.key_use_count_out = (short) intValue;
                        TLdeserialize.exchange_id = queryFinalized.longValue(9);
                        TLdeserialize.key_create_date = queryFinalized.intValue(10);
                        TLdeserialize.future_key_fingerprint = queryFinalized.longValue(11);
                        TLdeserialize.future_auth_key = queryFinalized.byteArrayValue(12);
                        TLdeserialize.key_hash = queryFinalized.byteArrayValue(13);
                        TLdeserialize.in_seq_no = queryFinalized.intValue(14);
                        long longValue2 = queryFinalized.longValue(15);
                        if (longValue2 != 0) {
                            TLdeserialize.admin_id = longValue2;
                        }
                        TLdeserialize.mtproto_seq = queryFinalized.intValue(16);
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        queryFinalized.dispose();
    }

    /* renamed from: putUsersAndChatsInternal */
    public void lambda$putUsersAndChats$144(ArrayList<TLRPC$User> arrayList, ArrayList<TLRPC$Chat> arrayList2, boolean z) {
        if (z) {
            try {
                this.database.beginTransaction();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        putUsersInternal(arrayList);
        putChatsInternal(arrayList2);
        if (z) {
            this.database.commitTransaction();
        }
    }

    public void putUsersAndChats(final ArrayList<TLRPC$User> arrayList, final ArrayList<TLRPC$Chat> arrayList2, final boolean z, boolean z2) {
        if (arrayList == null || !arrayList.isEmpty() || arrayList2 == null || !arrayList2.isEmpty()) {
            if (z2) {
                this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda146
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$putUsersAndChats$144(arrayList, arrayList2, z);
                    }
                });
            } else {
                lambda$putUsersAndChats$144(arrayList, arrayList2, z);
            }
        }
    }

    public void removeFromDownloadQueue(final long j, final int i, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda185
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$removeFromDownloadQueue$145(z, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$removeFromDownloadQueue$145(boolean z, int i, long j) {
        try {
            if (!z) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
                return;
            }
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT min(date) FROM download_queue WHERE type = %d", Integer.valueOf(i)), new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
            queryFinalized.dispose();
            if (intValue == -1) {
                return;
            }
            this.database.executeFast(String.format(locale, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", Integer.valueOf(intValue - 1), Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void deleteFromDownloadQueue(final ArrayList<Pair<Long, Integer>> arrayList, boolean z) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        if (z) {
            try {
                this.database.beginTransaction();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("DELETE FROM download_queue WHERE uid = ? AND type = ?");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Pair<Long, Integer> pair = arrayList.get(i);
            executeFast.requery();
            executeFast.bindLong(1, ((Long) pair.first).longValue());
            executeFast.bindInteger(2, ((Integer) pair.second).intValue());
            executeFast.step();
        }
        executeFast.dispose();
        if (z) {
            this.database.commitTransaction();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda138
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteFromDownloadQueue$146(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$deleteFromDownloadQueue$146(ArrayList arrayList) {
        getDownloadController().cancelDownloading(arrayList);
    }

    public void clearDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearDownloadQueue$147(i);
            }
        });
    }

    public /* synthetic */ void lambda$clearDownloadQueue$147(int i) {
        try {
            if (i == 0) {
                this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", Integer.valueOf(i))).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDownloadQueue$149(i);
            }
        });
    }

    public /* synthetic */ void lambda$getDownloadQueue$149(final int i) {
        int i2;
        try {
            final ArrayList arrayList = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data, parent FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", Integer.valueOf(i)), new Object[0]);
            while (queryFinalized.next()) {
                DownloadObject downloadObject = new DownloadObject();
                downloadObject.type = queryFinalized.intValue(1);
                downloadObject.id = queryFinalized.longValue(0);
                downloadObject.parent = queryFinalized.stringValue(3);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(2);
                if (byteBufferValue != null) {
                    TLRPC$MessageMedia TLdeserialize = TLRPC$MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    TLRPC$Document tLRPC$Document = TLdeserialize.document;
                    if (tLRPC$Document != null) {
                        downloadObject.object = tLRPC$Document;
                        downloadObject.secret = MessageObject.isVideoDocument(tLRPC$Document) && (i2 = TLdeserialize.ttl_seconds) > 0 && i2 <= 60;
                    } else {
                        TLRPC$Photo tLRPC$Photo = TLdeserialize.photo;
                        if (tLRPC$Photo != null) {
                            downloadObject.object = tLRPC$Photo;
                            int i3 = TLdeserialize.ttl_seconds;
                            downloadObject.secret = i3 > 0 && i3 <= 60;
                        }
                    }
                    downloadObject.forceCache = (TLdeserialize.flags & Integer.MIN_VALUE) != 0;
                }
                arrayList.add(downloadObject);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda53
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$getDownloadQueue$148(i, arrayList);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$getDownloadQueue$148(int i, ArrayList arrayList) {
        getDownloadController().processDownloadObjects(i, arrayList);
    }

    private int getMessageMediaType(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if (!(tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) && !MessageObject.isGifMessage(tLRPC$Message) && !MessageObject.isVoiceMessage(tLRPC$Message) && !MessageObject.isVideoMessage(tLRPC$Message) && !MessageObject.isRoundVideoMessage(tLRPC$Message)) {
                return -1;
            }
            int i = tLRPC$Message.ttl;
            return (i <= 0 || i > 60) ? 0 : 1;
        }
        if (tLRPC$Message instanceof TLRPC$TL_message) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$MessageMedia.ttl_seconds != 0) {
                return 1;
            }
        }
        return ((tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) || MessageObject.isVideoMessage(tLRPC$Message)) ? 0 : -1;
    }

    public void putWebPages(final LongSparseArray<TLRPC$WebPage> longSparseArray) {
        if (isEmpty(longSparseArray)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda118
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putWebPages$151(longSparseArray);
            }
        });
    }

    public /* synthetic */ void lambda$putWebPages$151(LongSparseArray longSparseArray) {
        try {
            final ArrayList arrayList = new ArrayList();
            int size = longSparseArray.size();
            int i = 0;
            int i2 = 0;
            while (true) {
                int i3 = 2;
                if (i2 >= size) {
                    break;
                }
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT mid, uid FROM webpage_pending_v2 WHERE id = " + longSparseArray.keyAt(i2), new Object[i]);
                LongSparseArray longSparseArray2 = new LongSparseArray();
                while (queryFinalized.next()) {
                    long longValue = queryFinalized.longValue(1);
                    ArrayList arrayList2 = (ArrayList) longSparseArray2.get(longValue);
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                        longSparseArray2.put(longValue, arrayList2);
                    }
                    arrayList2.add(Integer.valueOf(queryFinalized.intValue(i)));
                }
                queryFinalized.dispose();
                if (!longSparseArray2.isEmpty()) {
                    int size2 = longSparseArray2.size();
                    int i4 = 0;
                    while (i4 < size2) {
                        long keyAt = longSparseArray2.keyAt(i4);
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        Locale locale = Locale.US;
                        Object[] objArr = new Object[i3];
                        objArr[0] = TextUtils.join(",", (ArrayList) longSparseArray2.valueAt(i4));
                        objArr[1] = Long.valueOf(keyAt);
                        SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(String.format(locale, "SELECT mid, data FROM messages_v2 WHERE mid IN (%s) AND uid = %d", objArr), new Object[0]);
                        while (queryFinalized2.next()) {
                            int intValue = queryFinalized2.intValue(0);
                            NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(1);
                            if (byteBufferValue != null) {
                                TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                                byteBufferValue.reuse();
                                TLRPC$MessageMedia tLRPC$MessageMedia = TLdeserialize.media;
                                if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
                                    TLdeserialize.id = intValue;
                                    tLRPC$MessageMedia.webpage = (TLRPC$WebPage) longSparseArray.valueAt(i2);
                                    arrayList.add(TLdeserialize);
                                }
                            }
                        }
                        queryFinalized2.dispose();
                        i4++;
                        i3 = 2;
                    }
                }
                i2++;
                i = 0;
            }
            if (arrayList.isEmpty()) {
                return;
            }
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET data = ? WHERE mid = ? AND uid = ?");
            SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE media_v4 SET data = ? WHERE mid = ? AND uid = ?");
            for (int i5 = 0; i5 < arrayList.size(); i5++) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i5);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                tLRPC$Message.serializeToStream(nativeByteBuffer);
                executeFast.requery();
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindInteger(2, tLRPC$Message.id);
                executeFast.bindLong(3, MessageObject.getDialogId(tLRPC$Message));
                executeFast.step();
                executeFast2.requery();
                executeFast2.bindByteBuffer(1, nativeByteBuffer);
                executeFast2.bindInteger(2, tLRPC$Message.id);
                executeFast2.bindLong(3, MessageObject.getDialogId(tLRPC$Message));
                executeFast2.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
            executeFast2.dispose();
            this.database.commitTransaction();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda131
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putWebPages$150(arrayList);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$putWebPages$150(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.didReceivedWebpages, arrayList);
    }

    public void overwriteChannel(final long j, final TLRPC$TL_updates_channelDifferenceTooLong tLRPC$TL_updates_channelDifferenceTooLong, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda79
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$overwriteChannel$153(j, i, tLRPC$TL_updates_channelDifferenceTooLong);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0157  */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0159  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0183  */
    /* JADX WARN: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$overwriteChannel$153(long r19, int r21, final org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong r22) {
        /*
            Method dump skipped, instructions count: 411
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$overwriteChannel$153(long, int, org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong):void");
    }

    public /* synthetic */ void lambda$overwriteChannel$152(long j, TLRPC$TL_updates_channelDifferenceTooLong tLRPC$TL_updates_channelDifferenceTooLong) {
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.TRUE, tLRPC$TL_updates_channelDifferenceTooLong);
    }

    public void putChannelViews(final LongSparseArray<SparseIntArray> longSparseArray, final LongSparseArray<SparseIntArray> longSparseArray2, final LongSparseArray<SparseArray<TLRPC$MessageReplies>> longSparseArray3, final boolean z) {
        if (!isEmpty(longSparseArray) || !isEmpty(longSparseArray2) || !isEmpty(longSparseArray3)) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda120
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putChannelViews$154(longSparseArray, longSparseArray2, longSparseArray3, z);
                }
            });
        }
    }

    public /* synthetic */ void lambda$putChannelViews$154(LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3, boolean z) {
        TLRPC$TL_messageReplies tLRPC$TL_messageReplies;
        int i;
        int i2;
        NativeByteBuffer byteBufferValue;
        LongSparseArray longSparseArray4 = longSparseArray3;
        try {
            this.database.beginTransaction();
            int i3 = 2;
            char c = 1;
            if (!isEmpty(longSparseArray)) {
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET media = max((SELECT media FROM messages_v2 WHERE mid = ? AND uid = ?), ?) WHERE mid = ? AND uid = ?");
                for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
                    long keyAt = longSparseArray.keyAt(i4);
                    SparseIntArray sparseIntArray = (SparseIntArray) longSparseArray.valueAt(i4);
                    int size = sparseIntArray.size();
                    for (int i5 = 0; i5 < size; i5++) {
                        int valueAt = sparseIntArray.valueAt(i5);
                        int keyAt2 = sparseIntArray.keyAt(i5);
                        executeFast.requery();
                        executeFast.bindInteger(1, keyAt2);
                        executeFast.bindLong(2, keyAt);
                        executeFast.bindInteger(3, valueAt);
                        executeFast.bindInteger(4, keyAt2);
                        executeFast.bindLong(5, keyAt);
                        executeFast.step();
                    }
                }
                executeFast.dispose();
            }
            if (!isEmpty(longSparseArray2)) {
                SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE messages_v2 SET forwards = max((SELECT forwards FROM messages_v2 WHERE mid = ? AND uid = ?), ?) WHERE mid = ? AND uid = ?");
                for (int i6 = 0; i6 < longSparseArray2.size(); i6++) {
                    long keyAt3 = longSparseArray2.keyAt(i6);
                    SparseIntArray sparseIntArray2 = (SparseIntArray) longSparseArray2.valueAt(i6);
                    int size2 = sparseIntArray2.size();
                    for (int i7 = 0; i7 < size2; i7++) {
                        int valueAt2 = sparseIntArray2.valueAt(i7);
                        int keyAt4 = sparseIntArray2.keyAt(i7);
                        executeFast2.requery();
                        executeFast2.bindInteger(1, keyAt4);
                        executeFast2.bindLong(2, keyAt3);
                        executeFast2.bindInteger(3, valueAt2);
                        executeFast2.bindInteger(4, keyAt4);
                        executeFast2.bindLong(5, keyAt3);
                        executeFast2.step();
                    }
                }
                executeFast2.dispose();
            }
            if (!isEmpty(longSparseArray3)) {
                SQLitePreparedStatement executeFast3 = this.database.executeFast("UPDATE messages_v2 SET replies_data = ? WHERE mid = ? AND uid = ?");
                int i8 = 0;
                while (i8 < longSparseArray3.size()) {
                    long keyAt5 = longSparseArray4.keyAt(i8);
                    SparseArray sparseArray = (SparseArray) longSparseArray4.valueAt(i8);
                    int size3 = sparseArray.size();
                    int i9 = 0;
                    while (i9 < size3) {
                        int keyAt6 = sparseArray.keyAt(i9);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        Locale locale = Locale.US;
                        Object[] objArr = new Object[i3];
                        objArr[0] = Integer.valueOf(keyAt6);
                        objArr[c] = Long.valueOf(keyAt5);
                        SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT replies_data FROM messages_v2 WHERE mid = %d AND uid = %d", objArr), new Object[0]);
                        boolean next = queryFinalized.next();
                        if (!next || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
                            tLRPC$TL_messageReplies = null;
                        } else {
                            tLRPC$TL_messageReplies = TLRPC$MessageReplies.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                        }
                        queryFinalized.dispose();
                        if (next) {
                            TLRPC$MessageReplies tLRPC$MessageReplies = (TLRPC$MessageReplies) sparseArray.get(sparseArray.keyAt(i9));
                            if (z || tLRPC$TL_messageReplies == null || (i2 = tLRPC$TL_messageReplies.replies_pts) == 0 || tLRPC$MessageReplies.replies_pts > i2 || tLRPC$MessageReplies.read_max_id > tLRPC$TL_messageReplies.read_max_id || tLRPC$MessageReplies.max_id > tLRPC$TL_messageReplies.max_id) {
                                if (z) {
                                    if (tLRPC$TL_messageReplies == null) {
                                        TLRPC$TL_messageReplies tLRPC$TL_messageReplies2 = new TLRPC$TL_messageReplies();
                                        tLRPC$TL_messageReplies2.flags |= 2;
                                        tLRPC$TL_messageReplies = tLRPC$TL_messageReplies2;
                                    }
                                    tLRPC$TL_messageReplies.replies += tLRPC$MessageReplies.replies;
                                    int size4 = tLRPC$MessageReplies.recent_repliers.size();
                                    for (int i10 = 0; i10 < size4; i10++) {
                                        long peerId = MessageObject.getPeerId(tLRPC$MessageReplies.recent_repliers.get(i10));
                                        int size5 = tLRPC$TL_messageReplies.recent_repliers.size();
                                        int i11 = 0;
                                        while (i11 < size5) {
                                            if (peerId == MessageObject.getPeerId(tLRPC$TL_messageReplies.recent_repliers.get(i11))) {
                                                tLRPC$TL_messageReplies.recent_repliers.remove(i11);
                                                i11--;
                                                size5--;
                                            }
                                            i11++;
                                        }
                                    }
                                    tLRPC$TL_messageReplies.recent_repliers.addAll(0, tLRPC$MessageReplies.recent_repliers);
                                    while (tLRPC$TL_messageReplies.recent_repliers.size() > 3) {
                                        tLRPC$TL_messageReplies.recent_repliers.remove(0);
                                    }
                                    tLRPC$MessageReplies = tLRPC$TL_messageReplies;
                                }
                                if (tLRPC$TL_messageReplies != null && (i = tLRPC$TL_messageReplies.read_max_id) > tLRPC$MessageReplies.read_max_id) {
                                    tLRPC$MessageReplies.read_max_id = i;
                                }
                                executeFast3.requery();
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$MessageReplies.getObjectSize());
                                tLRPC$MessageReplies.serializeToStream(nativeByteBuffer);
                                executeFast3.bindByteBuffer(1, nativeByteBuffer);
                                executeFast3.bindInteger(2, keyAt6);
                                executeFast3.bindLong(3, keyAt5);
                                executeFast3.step();
                                nativeByteBuffer.reuse();
                                i9++;
                                i3 = 2;
                                c = 1;
                            }
                        }
                        i9++;
                        i3 = 2;
                        c = 1;
                    }
                    i8++;
                    longSparseArray4 = longSparseArray3;
                    i3 = 2;
                    c = 1;
                }
                executeFast3.dispose();
            }
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: updateRepliesMaxReadIdInternal */
    public void lambda$updateRepliesMaxReadId$155(long j, int i, int i2) {
        NativeByteBuffer byteBufferValue;
        long j2 = -j;
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET replies_data = ? WHERE mid = ? AND uid = ?");
            TLRPC$MessageReplies tLRPC$MessageReplies = null;
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT replies_data FROM messages_v2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j2)), new Object[0]);
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$MessageReplies = TLRPC$MessageReplies.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$MessageReplies != null) {
                tLRPC$MessageReplies.read_max_id = i2;
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$MessageReplies.getObjectSize());
                tLRPC$MessageReplies.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindInteger(2, i);
                executeFast.bindLong(3, j2);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateRepliesMaxReadId(final long j, final int i, final int i2, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda70
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateRepliesMaxReadId$155(j, i, i2);
                }
            });
        } else {
            lambda$updateRepliesMaxReadId$155(j, i, i2);
        }
    }

    public void updateRepliesCount(final long j, final int i, final ArrayList<TLRPC$Peer> arrayList, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateRepliesCount$156(i, j, i3, arrayList, i2);
            }
        });
    }

    public /* synthetic */ void lambda$updateRepliesCount$156(int i, long j, int i2, ArrayList arrayList, int i3) {
        NativeByteBuffer byteBufferValue;
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET replies_data = ? WHERE mid = ? AND uid = ?");
            TLRPC$MessageReplies tLRPC$MessageReplies = null;
            long j2 = -j;
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.ENGLISH, "SELECT replies_data FROM messages_v2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j2)), new Object[0]);
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$MessageReplies = TLRPC$MessageReplies.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$MessageReplies != null) {
                int i4 = tLRPC$MessageReplies.replies + i2;
                tLRPC$MessageReplies.replies = i4;
                if (i4 < 0) {
                    tLRPC$MessageReplies.replies = 0;
                }
                if (arrayList != null) {
                    tLRPC$MessageReplies.recent_repliers = arrayList;
                    tLRPC$MessageReplies.flags |= 2;
                }
                if (i3 != 0) {
                    tLRPC$MessageReplies.max_id = i3;
                }
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$MessageReplies.getObjectSize());
                tLRPC$MessageReplies.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindInteger(2, i);
                executeFast.bindLong(3, j2);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean isValidKeyboardToSave(TLRPC$Message tLRPC$Message) {
        TLRPC$ReplyMarkup tLRPC$ReplyMarkup = tLRPC$Message.reply_markup;
        return tLRPC$ReplyMarkup != null && !(tLRPC$ReplyMarkup instanceof TLRPC$TL_replyInlineMarkup) && (!tLRPC$ReplyMarkup.selective || tLRPC$Message.mentioned);
    }

    public void updateMessageVerifyFlags(final ArrayList<TLRPC$Message> arrayList) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda130
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVerifyFlags$157(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$updateMessageVerifyFlags$157(ArrayList arrayList) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET imp = ? WHERE mid = ? AND uid = ?");
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i);
                executeFast.requery();
                int i2 = tLRPC$Message.stickerVerified;
                executeFast.bindInteger(1, i2 == 0 ? 1 : i2 == 2 ? 2 : 0);
                executeFast.bindInteger(2, tLRPC$Message.id);
                executeFast.bindLong(3, MessageObject.getDialogId(tLRPC$Message));
                executeFast.step();
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:207:0x0594 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:219:0x05ba  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x05ff A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:230:0x0607 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:233:0x0614  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x0618  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x0626  */
    /* JADX WARN: Removed duplicated region for block: B:242:0x0628  */
    /* JADX WARN: Removed duplicated region for block: B:245:0x0639 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:246:0x064f A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x0659 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:254:0x0666 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:257:0x067c A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:258:0x0682 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:261:0x0694 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:262:0x06b0  */
    /* JADX WARN: Removed duplicated region for block: B:265:0x06ba  */
    /* JADX WARN: Removed duplicated region for block: B:270:0x06ea A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:273:0x06f0 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:277:0x0726  */
    /* JADX WARN: Removed duplicated region for block: B:282:0x0754 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:286:0x0778 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:288:0x077d A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:291:0x0785 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:307:0x07c8 A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:309:0x07dc A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:336:0x087e A[Catch: Exception -> 0x0110, TryCatch #0 {Exception -> 0x0110, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:15:0x0044, B:17:0x007a, B:18:0x0090, B:20:0x009d, B:21:0x00a4, B:22:0x00ab, B:24:0x00b3, B:25:0x00b8, B:27:0x00bf, B:30:0x00d0, B:32:0x00df, B:33:0x00e2, B:35:0x0104, B:37:0x010a, B:44:0x0117, B:45:0x011c, B:46:0x017f, B:48:0x0185, B:50:0x0196, B:52:0x019a, B:54:0x01a6, B:56:0x01b7, B:58:0x01c4, B:61:0x01cc, B:63:0x01d2, B:65:0x01d6, B:67:0x01da, B:69:0x01e0, B:71:0x01ec, B:73:0x0211, B:75:0x0217, B:77:0x0224, B:80:0x022a, B:82:0x0234, B:83:0x023e, B:85:0x0244, B:86:0x0247, B:88:0x0254, B:89:0x025e, B:91:0x026b, B:94:0x0273, B:96:0x028e, B:98:0x0298, B:99:0x02a2, B:101:0x02a8, B:102:0x02ab, B:104:0x02b8, B:105:0x02c2, B:107:0x02d3, B:108:0x02dd, B:110:0x02ec, B:112:0x02f2, B:114:0x02fc, B:116:0x0302, B:117:0x0307, B:119:0x0323, B:121:0x0329, B:123:0x033f, B:125:0x0348, B:126:0x0398, B:128:0x039e, B:130:0x03ad, B:133:0x03b9, B:134:0x03be, B:136:0x03c6, B:137:0x03ce, B:139:0x03d3, B:141:0x03d8, B:142:0x03de, B:144:0x03e5, B:146:0x03ff, B:147:0x040d, B:150:0x041a, B:152:0x0421, B:154:0x0428, B:156:0x0430, B:157:0x043c, B:161:0x0448, B:162:0x044c, B:163:0x0453, B:165:0x0460, B:167:0x0466, B:169:0x046d, B:170:0x04ae, B:172:0x04b4, B:174:0x04be, B:176:0x04c3, B:178:0x04c8, B:181:0x04d5, B:183:0x04df, B:186:0x04e9, B:188:0x04f4, B:190:0x050a, B:192:0x0510, B:195:0x0527, B:197:0x0539, B:199:0x053f, B:202:0x054d, B:204:0x0551, B:207:0x0594, B:209:0x05a0, B:211:0x05a6, B:213:0x05aa, B:216:0x05b0, B:218:0x05b4, B:220:0x05bc, B:222:0x05e6, B:227:0x05ee, B:229:0x05ff, B:230:0x0607, B:231:0x0610, B:239:0x061f, B:243:0x0629, B:245:0x0639, B:246:0x064f, B:247:0x0655, B:249:0x0659, B:252:0x065e, B:253:0x0660, B:254:0x0666, B:255:0x066c, B:257:0x067c, B:258:0x0682, B:259:0x0687, B:261:0x0694, B:263:0x06b4, B:266:0x06bc, B:267:0x06c4, B:268:0x06e6, B:270:0x06ea, B:273:0x06f0, B:274:0x06f8, B:275:0x0720, B:278:0x0728, B:280:0x0732, B:282:0x0754, B:284:0x075a, B:286:0x0778, B:288:0x077d, B:289:0x0780, B:291:0x0785, B:293:0x078f, B:295:0x0793, B:297:0x07a1, B:299:0x07ac, B:301:0x07b2, B:303:0x07b6, B:305:0x07ba, B:307:0x07c8, B:309:0x07dc, B:311:0x07e2, B:314:0x07ea, B:316:0x07f0, B:318:0x07f6, B:322:0x07ff, B:324:0x0814, B:326:0x0820, B:328:0x0835, B:332:0x084d, B:333:0x0862, B:336:0x087e, B:338:0x0884, B:339:0x088e, B:341:0x08c2, B:343:0x08c7, B:346:0x08eb, B:347:0x08fd, B:349:0x0911, B:351:0x0916, B:353:0x0925, B:354:0x0928, B:355:0x0947, B:357:0x094d, B:360:0x096d, B:362:0x0997, B:365:0x09c5, B:367:0x09d2), top: B:440:0x0009 }] */
    /* renamed from: putMessagesInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void lambda$putMessages$159(java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r46, boolean r47, boolean r48, int r49, boolean r50, boolean r51) {
        /*
            Method dump skipped, instructions count: 3019
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$159(java.util.ArrayList, boolean, boolean, int, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$putMessagesInternal$158(int i) {
        getDownloadController().newDownloadObjectsAvailable(i);
    }

    public void putMessages(ArrayList<TLRPC$Message> arrayList, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        putMessages(arrayList, z, z2, z3, i, false, z4);
    }

    public void putMessages(final ArrayList<TLRPC$Message> arrayList, final boolean z, boolean z2, final boolean z3, final int i, final boolean z4, final boolean z5) {
        if (arrayList.size() == 0) {
            return;
        }
        if (z2) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda148
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putMessages$159(arrayList, z, z3, i, z4, z5);
                }
            });
        } else {
            lambda$putMessages$159(arrayList, z, z3, i, z4, z5);
        }
    }

    public void markMessageAsSendError(final TLRPC$Message tLRPC$Message, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda172
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMessageAsSendError$160(tLRPC$Message, z);
            }
        });
    }

    public /* synthetic */ void lambda$markMessageAsSendError$160(TLRPC$Message tLRPC$Message, boolean z) {
        try {
            long j = tLRPC$Message.id;
            if (z) {
                this.database.executeFast(String.format(Locale.US, "UPDATE scheduled_messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(tLRPC$Message)))).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(tLRPC$Message)))).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setMessageSeq(final int i, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setMessageSeq$161(i, i2, i3);
            }
        });
    }

    public /* synthetic */ void lambda$setMessageSeq$161(int i, int i2, int i3) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            executeFast.bindInteger(3, i3);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(15:2|(7:209|4|203|5|(3:7|171|8)(1:10)|11|(1:22))(1:28)|29|(8:(2:169|(18:211|32|184|33|34|186|35|44|61|(13:183|66|193|67|(2:69|70)(1:71)|72|(1:82)(1:84)|179|85|(2:87|88)|93|94|(1:96)(1:(4:177|(12:207|114|115|188|116|125|173|126|132|181|134|139)(3:175|149|155)|156|157)(6:195|(1:100)(1:101)|102|107|108|109)))(1:64)|65|(0)(0)|179|85|(0)|93|94|(0)(0)))|179|85|(0)|93|94|(0)(0)|(13:(0)|(1:190)|(1:202)|(1:199)|(1:200)|(1:197)|(0)|(1:182)|(0)|(0)|(0)|(0)|(0)))|50|(6:52|(1:54)(1:55)|56|191|57|60)|61|(0)|183|66|193|67|(0)(0)|72|(0)(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(22:2|(7:209|4|203|5|(3:7|171|8)(1:10)|11|(1:22))(1:28)|29|(2:169|(18:211|32|184|33|34|186|35|44|61|(13:183|66|193|67|(2:69|70)(1:71)|72|(1:82)(1:84)|179|85|(2:87|88)|93|94|(1:96)(1:(4:177|(12:207|114|115|188|116|125|173|126|132|181|134|139)(3:175|149|155)|156|157)(6:195|(1:100)(1:101)|102|107|108|109)))(1:64)|65|(0)(0)|179|85|(0)|93|94|(0)(0)))|50|(6:52|(1:54)(1:55)|56|191|57|60)|61|(0)|183|66|193|67|(0)(0)|72|(0)(0)|179|85|(0)|93|94|(0)(0)|(13:(0)|(1:190)|(1:202)|(1:199)|(1:200)|(1:197)|(0)|(1:182)|(0)|(0)|(0)|(0)|(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x01d8, code lost:
        if (r5 == null) goto L108;
     */
    /* JADX WARN: Code restructure failed: missing block: B:124:0x0255, code lost:
        if (r3 != null) goto L125;
     */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x02c0, code lost:
        if (r5 == null) goto L156;
     */
    /* JADX WARN: Code restructure failed: missing block: B:154:0x031c, code lost:
        if (r5 != null) goto L155;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x009c, code lost:
        if (r10 == null) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x0156, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x0158, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0159, code lost:
        r8 = r22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x015d, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x015e, code lost:
        r8 = r22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0160, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0163, code lost:
        if (r8 != null) goto L80;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x0165, code lost:
        r8.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x016c, code lost:
        if (r4 == 1) goto L179;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x019e, code lost:
        if (r8 == null) goto L94;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:133:0x02a5  */
    /* JADX WARN: Removed duplicated region for block: B:147:0x02d4  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x033e  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x0072 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0058 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x005d  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00b5  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0122 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0149 A[Catch: Exception -> 0x0156, all -> 0x033b, TRY_LEAVE, TryCatch #10 {all -> 0x033b, blocks: (B:67:0x0143, B:69:0x0149, B:78:0x0160), top: B:183:0x012a }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x014f  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x016b  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x016f  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x018f A[Catch: all -> 0x0197, Exception -> 0x019a, TRY_LEAVE, TryCatch #8 {Exception -> 0x019a, blocks: (B:85:0x0170, B:87:0x018f), top: B:179:0x0170, outer: #27 }] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x01a9 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01aa  */
    /* JADX WARN: Type inference failed for: r10v0 */
    /* JADX WARN: Type inference failed for: r10v19 */
    /* JADX WARN: Type inference failed for: r10v21 */
    /* JADX WARN: Type inference failed for: r10v22 */
    /* JADX WARN: Type inference failed for: r10v6, types: [long] */
    /* JADX WARN: Type inference failed for: r10v7 */
    /* JADX WARN: Type inference failed for: r10v8, types: [org.telegram.SQLite.SQLitePreparedStatement] */
    /* renamed from: updateMessageStateAndIdInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public long[] lambda$updateMessageStateAndId$163(long r20, long r22, java.lang.Integer r24, int r25, int r26, int r27) {
        /*
            Method dump skipped, instructions count: 836
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateMessageStateAndId$163(long, long, java.lang.Integer, int, int, int):long[]");
    }

    public /* synthetic */ void lambda$updateMessageStateAndIdInternal$162(TLRPC$TL_updates tLRPC$TL_updates) {
        getMessagesController().processUpdates(tLRPC$TL_updates, false);
    }

    public long[] updateMessageStateAndId(final long j, final long j2, final Integer num, final int i, final int i2, boolean z, final int i3) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda87
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateMessageStateAndId$163(j, j2, num, i, i2, i3);
                }
            });
            return null;
        }
        return lambda$updateMessageStateAndId$163(j, j2, num, i, i2, i3);
    }

    /* renamed from: updateUsersInternal */
    public void lambda$updateUsers$164(ArrayList<TLRPC$User> arrayList, boolean z, boolean z2) {
        try {
            if (z) {
                if (z2) {
                    this.database.beginTransaction();
                }
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$User tLRPC$User = arrayList.get(i);
                    executeFast.requery();
                    TLRPC$UserStatus tLRPC$UserStatus = tLRPC$User.status;
                    if (tLRPC$UserStatus != null) {
                        executeFast.bindInteger(1, tLRPC$UserStatus.expires);
                    } else {
                        executeFast.bindInteger(1, 0);
                    }
                    executeFast.bindLong(2, tLRPC$User.id);
                    executeFast.step();
                }
                executeFast.dispose();
                if (!z2) {
                    return;
                }
                this.database.commitTransaction();
                return;
            }
            StringBuilder sb = new StringBuilder();
            LongSparseArray longSparseArray = new LongSparseArray();
            int size2 = arrayList.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC$User tLRPC$User2 = arrayList.get(i2);
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(tLRPC$User2.id);
                longSparseArray.put(tLRPC$User2.id, tLRPC$User2);
            }
            ArrayList<TLRPC$User> arrayList2 = new ArrayList<>();
            getUsersInternal(sb.toString(), arrayList2);
            int size3 = arrayList2.size();
            for (int i3 = 0; i3 < size3; i3++) {
                TLRPC$User tLRPC$User3 = arrayList2.get(i3);
                TLRPC$User tLRPC$User4 = (TLRPC$User) longSparseArray.get(tLRPC$User3.id);
                if (tLRPC$User4 != null) {
                    if (tLRPC$User4.first_name != null && tLRPC$User4.last_name != null) {
                        if (!UserObject.isContact(tLRPC$User3)) {
                            tLRPC$User3.first_name = tLRPC$User4.first_name;
                            tLRPC$User3.last_name = tLRPC$User4.last_name;
                        }
                        tLRPC$User3.username = tLRPC$User4.username;
                    } else {
                        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User4.photo;
                        if (tLRPC$UserProfilePhoto != null) {
                            tLRPC$User3.photo = tLRPC$UserProfilePhoto;
                        } else {
                            String str = tLRPC$User4.phone;
                            if (str != null) {
                                tLRPC$User3.phone = str;
                            }
                        }
                    }
                }
            }
            if (arrayList2.isEmpty()) {
                return;
            }
            if (z2) {
                this.database.beginTransaction();
            }
            putUsersInternal(arrayList2);
            if (!z2) {
                return;
            }
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateUsers(final ArrayList<TLRPC$User> arrayList, final boolean z, final boolean z2, boolean z3) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        if (z3) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda147
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateUsers$164(arrayList, z, z2);
                }
            });
        } else {
            lambda$updateUsers$164(arrayList, z, z2);
        }
    }

    /* renamed from: markMessagesAsReadInternal */
    public void lambda$markMessagesAsRead$166(LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2, SparseIntArray sparseIntArray) {
        try {
            if (!isEmpty(longSparseIntArray)) {
                SQLitePreparedStatement executeFast = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                for (int i = 0; i < longSparseIntArray.size(); i++) {
                    long keyAt = longSparseIntArray.keyAt(i);
                    int i2 = longSparseIntArray.get(keyAt);
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", Long.valueOf(keyAt), Integer.valueOf(i2))).stepThis().dispose();
                    executeFast.requery();
                    executeFast.bindLong(1, keyAt);
                    executeFast.bindInteger(2, i2);
                    executeFast.step();
                }
                executeFast.dispose();
            }
            if (!isEmpty(longSparseIntArray2)) {
                for (int i3 = 0; i3 < longSparseIntArray2.size(); i3++) {
                    long keyAt2 = longSparseIntArray2.keyAt(i3);
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", Long.valueOf(keyAt2), Integer.valueOf(longSparseIntArray2.get(keyAt2)))).stepThis().dispose();
                }
            }
            if (sparseIntArray == null || isEmpty(sparseIntArray)) {
                return;
            }
            for (int i4 = 0; i4 < sparseIntArray.size(); i4++) {
                long makeEncryptedDialogId = DialogObject.makeEncryptedDialogId(sparseIntArray.keyAt(i4));
                int valueAt = sparseIntArray.valueAt(i4);
                SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
                executeFast2.requery();
                executeFast2.bindLong(1, makeEncryptedDialogId);
                executeFast2.bindInteger(2, valueAt);
                executeFast2.step();
                executeFast2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void markMessagesContentAsReadInternal(long j, ArrayList<Integer> arrayList, int i) {
        try {
            String join = TextUtils.join(",", arrayList);
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid IN (%s) AND uid = %d", join, Long.valueOf(j))).stepThis().dispose();
            if (i == 0) {
                return;
            }
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(locale, "SELECT mid, ttl FROM messages_v2 WHERE mid IN (%s) AND uid = %d AND ttl > 0", join, Long.valueOf(j)), new Object[0]);
            ArrayList<Integer> arrayList2 = null;
            while (queryFinalized.next()) {
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList<>();
                }
                arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            if (arrayList2 != null) {
                emptyMessagesMedia(j, arrayList2);
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void markMessagesContentAsRead(final long j, final ArrayList<Integer> arrayList, final int i) {
        if (isEmpty(arrayList)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda96
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMessagesContentAsRead$165(j, arrayList, i);
            }
        });
    }

    public /* synthetic */ void lambda$markMessagesContentAsRead$165(long j, ArrayList arrayList, int i) {
        if (j == 0) {
            try {
                LongSparseArray longSparseArray = new LongSparseArray();
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, mid FROM messages_v2 WHERE mid IN (%s) AND is_channel = 0", TextUtils.join(",", arrayList)), new Object[0]);
                while (queryFinalized.next()) {
                    long longValue = queryFinalized.longValue(0);
                    ArrayList arrayList2 = (ArrayList) longSparseArray.get(longValue);
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                        longSparseArray.put(longValue, arrayList2);
                    }
                    arrayList2.add(Integer.valueOf(queryFinalized.intValue(1)));
                }
                queryFinalized.dispose();
                int size = longSparseArray.size();
                for (int i2 = 0; i2 < size; i2++) {
                    markMessagesContentAsReadInternal(longSparseArray.keyAt(i2), (ArrayList) longSparseArray.valueAt(i2), i);
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        markMessagesContentAsReadInternal(j, arrayList, i);
    }

    public void markMessagesAsRead(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final SparseIntArray sparseIntArray, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda154
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$markMessagesAsRead$166(longSparseIntArray, longSparseIntArray2, sparseIntArray);
                }
            });
        } else {
            lambda$markMessagesAsRead$166(longSparseIntArray, longSparseIntArray2, sparseIntArray);
        }
    }

    public void markMessagesAsDeletedByRandoms(final ArrayList<Long> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda126
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMessagesAsDeletedByRandoms$168(arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$markMessagesAsDeletedByRandoms$168(ArrayList arrayList) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, uid FROM randoms_v2 WHERE random_id IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
            LongSparseArray longSparseArray = new LongSparseArray();
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(1);
                ArrayList arrayList2 = (ArrayList) longSparseArray.get(longValue);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    longSparseArray.put(longValue, arrayList2);
                }
                arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            queryFinalized.dispose();
            if (longSparseArray.isEmpty()) {
                return;
            }
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                long keyAt = longSparseArray.keyAt(i);
                final ArrayList<Integer> arrayList3 = (ArrayList) longSparseArray.valueAt(i);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda128
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$markMessagesAsDeletedByRandoms$167(arrayList3);
                    }
                });
                updateDialogsWithReadMessagesInternal(arrayList3, null, null, null);
                lambda$markMessagesAsDeleted$172(keyAt, arrayList3, true, false);
                lambda$updateDialogsWithDeletedMessages$171(keyAt, 0L, arrayList3, null);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$markMessagesAsDeletedByRandoms$167(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, 0L, Boolean.FALSE);
    }

    public void deletePushMessages(long j, ArrayList<Integer> arrayList) {
        try {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM unread_push_messages WHERE uid = %d AND mid IN(%s)", Long.valueOf(j), TextUtils.join(",", arrayList))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void broadcastScheduledMessagesChange(final Long l) {
        try {
            final int i = 0;
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM scheduled_messages_v2 WHERE uid = %d", l), new Object[0]);
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda122
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$broadcastScheduledMessagesChange$169(l, i);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$broadcastScheduledMessagesChange$169(Long l, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.scheduledMessagesUpdated, l, Integer.valueOf(i));
    }

    /* JADX WARN: Removed duplicated region for block: B:142:0x0633  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x067a A[Catch: Exception -> 0x0683, TryCatch #5 {Exception -> 0x0683, blocks: (B:79:0x029f, B:80:0x02b3, B:82:0x02b9, B:84:0x02e5, B:86:0x02f1, B:87:0x0337, B:89:0x0340, B:91:0x0356, B:93:0x035c, B:94:0x0387, B:96:0x03b8, B:98:0x03eb, B:100:0x03f1, B:102:0x03f6, B:104:0x0414, B:105:0x0437, B:106:0x043a, B:108:0x04a0, B:109:0x04bc, B:111:0x04c2, B:113:0x04cd, B:114:0x04d2, B:116:0x04da, B:117:0x04e8, B:119:0x04f2, B:120:0x04f7, B:121:0x0506, B:123:0x050b, B:125:0x051a, B:127:0x052d, B:129:0x0565, B:131:0x0571, B:133:0x0576, B:135:0x059e, B:136:0x05b0, B:137:0x05c5, B:139:0x05db, B:140:0x060e, B:144:0x0639, B:145:0x0649, B:147:0x0668, B:149:0x067a, B:150:0x067d), top: B:168:0x029f }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0216  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x02b9 A[Catch: Exception -> 0x0683, TryCatch #5 {Exception -> 0x0683, blocks: (B:79:0x029f, B:80:0x02b3, B:82:0x02b9, B:84:0x02e5, B:86:0x02f1, B:87:0x0337, B:89:0x0340, B:91:0x0356, B:93:0x035c, B:94:0x0387, B:96:0x03b8, B:98:0x03eb, B:100:0x03f1, B:102:0x03f6, B:104:0x0414, B:105:0x0437, B:106:0x043a, B:108:0x04a0, B:109:0x04bc, B:111:0x04c2, B:113:0x04cd, B:114:0x04d2, B:116:0x04da, B:117:0x04e8, B:119:0x04f2, B:120:0x04f7, B:121:0x0506, B:123:0x050b, B:125:0x051a, B:127:0x052d, B:129:0x0565, B:131:0x0571, B:133:0x0576, B:135:0x059e, B:136:0x05b0, B:137:0x05c5, B:139:0x05db, B:140:0x060e, B:144:0x0639, B:145:0x0649, B:147:0x0668, B:149:0x067a, B:150:0x067d), top: B:168:0x029f }] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0340 A[Catch: Exception -> 0x0683, TryCatch #5 {Exception -> 0x0683, blocks: (B:79:0x029f, B:80:0x02b3, B:82:0x02b9, B:84:0x02e5, B:86:0x02f1, B:87:0x0337, B:89:0x0340, B:91:0x0356, B:93:0x035c, B:94:0x0387, B:96:0x03b8, B:98:0x03eb, B:100:0x03f1, B:102:0x03f6, B:104:0x0414, B:105:0x0437, B:106:0x043a, B:108:0x04a0, B:109:0x04bc, B:111:0x04c2, B:113:0x04cd, B:114:0x04d2, B:116:0x04da, B:117:0x04e8, B:119:0x04f2, B:120:0x04f7, B:121:0x0506, B:123:0x050b, B:125:0x051a, B:127:0x052d, B:129:0x0565, B:131:0x0571, B:133:0x0576, B:135:0x059e, B:136:0x05b0, B:137:0x05c5, B:139:0x05db, B:140:0x060e, B:144:0x0639, B:145:0x0649, B:147:0x0668, B:149:0x067a, B:150:0x067d), top: B:168:0x029f }] */
    /* renamed from: markMessagesAsDeletedInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<java.lang.Long> lambda$markMessagesAsDeleted$172(long r30, java.util.ArrayList<java.lang.Integer> r32, boolean r33, boolean r34) {
        /*
            Method dump skipped, instructions count: 1680
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesAsDeleted$172(long, java.util.ArrayList, boolean, boolean):java.util.ArrayList");
    }

    public /* synthetic */ void lambda$markMessagesAsDeletedInternal$170(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: updateDialogsWithDeletedMessagesInternal */
    public void lambda$updateDialogsWithDeletedMessages$171(long j, long j2, ArrayList<Integer> arrayList, ArrayList<Long> arrayList2) {
        TLRPC$TL_dialog tLRPC$TL_dialog;
        SQLitePreparedStatement sQLitePreparedStatement;
        long j3 = j2;
        try {
            ArrayList arrayList3 = new ArrayList();
            boolean z = false;
            if (!arrayList.isEmpty()) {
                if (j3 != 0) {
                    arrayList3.add(Long.valueOf(-j3));
                    sQLitePreparedStatement = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages_v2 WHERE uid = ? AND date = (SELECT MAX(date) FROM messages_v2 WHERE uid = ?)) WHERE did = ?");
                } else {
                    if (j == 0) {
                        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s) AND flags = 0", TextUtils.join(",", arrayList)), new Object[0]);
                        while (queryFinalized.next()) {
                            arrayList3.add(Long.valueOf(queryFinalized.longValue(0)));
                        }
                        queryFinalized.dispose();
                    } else {
                        arrayList3.add(Long.valueOf(j));
                    }
                    sQLitePreparedStatement = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages_v2 WHERE uid = ? AND date = (SELECT MAX(date) FROM messages_v2 WHERE uid = ? AND date != 0)) WHERE did = ?");
                }
                this.database.beginTransaction();
                for (int i = 0; i < arrayList3.size(); i++) {
                    long longValue = ((Long) arrayList3.get(i)).longValue();
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindLong(1, longValue);
                    sQLitePreparedStatement.bindLong(2, longValue);
                    sQLitePreparedStatement.bindLong(3, longValue);
                    sQLitePreparedStatement.step();
                }
                sQLitePreparedStatement.dispose();
                this.database.commitTransaction();
            } else {
                arrayList3.add(Long.valueOf(-j3));
            }
            if (arrayList2 != null) {
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    Long l = arrayList2.get(i2);
                    if (!arrayList3.contains(l)) {
                        arrayList3.add(l);
                    }
                }
            }
            String join = TextUtils.join(",", arrayList3);
            TLRPC$TL_messages_dialogs tLRPC$TL_messages_dialogs = new TLRPC$TL_messages_dialogs();
            ArrayList<TLRPC$EncryptedChat> arrayList4 = new ArrayList<>();
            ArrayList<Long> arrayList5 = new ArrayList<>();
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            SQLiteCursor queryFinalized2 = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max, d.pinned, d.unread_count_i, d.flags, d.folder_id, d.data, d.unread_reactions FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.did IN(%s)", join), new Object[0]);
            while (queryFinalized2.next()) {
                int i3 = z ? 1 : 0;
                int i4 = z ? 1 : 0;
                long longValue2 = queryFinalized2.longValue(i3);
                if (DialogObject.isFolderDialogId(longValue2)) {
                    TLRPC$TL_dialogFolder tLRPC$TL_dialogFolder = new TLRPC$TL_dialogFolder();
                    tLRPC$TL_dialog = tLRPC$TL_dialogFolder;
                    if (!queryFinalized2.isNull(16)) {
                        NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(16);
                        if (byteBufferValue != null) {
                            tLRPC$TL_dialogFolder.folder = TLRPC$TL_folder.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z), z);
                            byteBufferValue.reuse();
                            tLRPC$TL_dialog = tLRPC$TL_dialogFolder;
                        } else {
                            TLRPC$TL_folder tLRPC$TL_folder = new TLRPC$TL_folder();
                            tLRPC$TL_dialogFolder.folder = tLRPC$TL_folder;
                            tLRPC$TL_folder.id = queryFinalized2.intValue(15);
                            tLRPC$TL_dialog = tLRPC$TL_dialogFolder;
                        }
                    }
                } else {
                    tLRPC$TL_dialog = new TLRPC$TL_dialog();
                }
                tLRPC$TL_dialog.id = longValue2;
                tLRPC$TL_dialog.top_message = queryFinalized2.intValue(1);
                tLRPC$TL_dialog.read_inbox_max_id = queryFinalized2.intValue(10);
                tLRPC$TL_dialog.read_outbox_max_id = queryFinalized2.intValue(11);
                tLRPC$TL_dialog.unread_count = queryFinalized2.intValue(2);
                tLRPC$TL_dialog.unread_mentions_count = queryFinalized2.intValue(13);
                tLRPC$TL_dialog.last_message_date = queryFinalized2.intValue(3);
                tLRPC$TL_dialog.pts = queryFinalized2.intValue(9);
                tLRPC$TL_dialog.flags = j3 == 0 ? 0 : 1;
                int intValue = queryFinalized2.intValue(12);
                tLRPC$TL_dialog.pinnedNum = intValue;
                tLRPC$TL_dialog.pinned = intValue != 0;
                tLRPC$TL_dialog.unread_mark = (queryFinalized2.intValue(14) & 1) != 0;
                tLRPC$TL_dialog.folder_id = queryFinalized2.intValue(15);
                tLRPC$TL_dialog.unread_reactions_count = queryFinalized2.intValue(17);
                tLRPC$TL_messages_dialogs.dialogs.add(tLRPC$TL_dialog);
                NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(4);
                if (byteBufferValue2 != null) {
                    TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue2, getUserConfig().clientUserId);
                    byteBufferValue2.reuse();
                    MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(5));
                    TLdeserialize.id = queryFinalized2.intValue(6);
                    TLdeserialize.send_state = queryFinalized2.intValue(7);
                    int intValue2 = queryFinalized2.intValue(8);
                    if (intValue2 != 0) {
                        tLRPC$TL_dialog.last_message_date = intValue2;
                    }
                    TLdeserialize.dialog_id = tLRPC$TL_dialog.id;
                    tLRPC$TL_messages_dialogs.messages.add(TLdeserialize);
                    addUsersAndChatsFromMessage(TLdeserialize, arrayList5, arrayList6);
                }
                if (DialogObject.isEncryptedDialog(longValue2)) {
                    int encryptedChatId = DialogObject.getEncryptedChatId(longValue2);
                    if (!arrayList7.contains(Integer.valueOf(encryptedChatId))) {
                        arrayList7.add(Integer.valueOf(encryptedChatId));
                    }
                } else if (DialogObject.isUserDialog(longValue2)) {
                    if (!arrayList5.contains(Long.valueOf(longValue2))) {
                        arrayList5.add(Long.valueOf(longValue2));
                    }
                } else {
                    long j4 = -longValue2;
                    if (!arrayList6.contains(Long.valueOf(j4))) {
                        arrayList6.add(Long.valueOf(j4));
                    }
                }
                j3 = j2;
                z = false;
            }
            queryFinalized2.dispose();
            if (!arrayList7.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", arrayList7), arrayList4, arrayList5);
            }
            if (!arrayList6.isEmpty()) {
                getChatsInternal(TextUtils.join(",", arrayList6), tLRPC$TL_messages_dialogs.chats);
            }
            if (!arrayList5.isEmpty()) {
                getUsersInternal(TextUtils.join(",", arrayList5), tLRPC$TL_messages_dialogs.users);
            }
            if (tLRPC$TL_messages_dialogs.dialogs.isEmpty() && arrayList4.isEmpty()) {
                return;
            }
            getMessagesController().processDialogsUpdate(tLRPC$TL_messages_dialogs, arrayList4, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateDialogsWithDeletedMessages(final long j, final long j2, final ArrayList<Integer> arrayList, final ArrayList<Long> arrayList2, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda88
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateDialogsWithDeletedMessages$171(j, j2, arrayList, arrayList2);
                }
            });
        } else {
            lambda$updateDialogsWithDeletedMessages$171(j, j2, arrayList, arrayList2);
        }
    }

    public ArrayList<Long> markMessagesAsDeleted(final long j, final ArrayList<Integer> arrayList, boolean z, final boolean z2, final boolean z3) {
        if (arrayList.isEmpty()) {
            return null;
        }
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda100
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$markMessagesAsDeleted$172(j, arrayList, z2, z3);
                }
            });
            return null;
        }
        return lambda$markMessagesAsDeleted$172(j, arrayList, z2, z3);
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x0109 A[Catch: Exception -> 0x0286, TryCatch #2 {Exception -> 0x0286, blocks: (B:39:0x00ef, B:40:0x0103, B:42:0x0109, B:44:0x0132, B:46:0x013d, B:47:0x0180, B:49:0x01d8, B:51:0x01de, B:53:0x01e3, B:55:0x01ff, B:56:0x0220, B:57:0x0223), top: B:70:0x00ef }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x01d8 A[Catch: Exception -> 0x0286, TryCatch #2 {Exception -> 0x0286, blocks: (B:39:0x00ef, B:40:0x0103, B:42:0x0109, B:44:0x0132, B:46:0x013d, B:47:0x0180, B:49:0x01d8, B:51:0x01de, B:53:0x01e3, B:55:0x01ff, B:56:0x0220, B:57:0x0223), top: B:70:0x00ef }] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x01dd  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x01e3 A[Catch: Exception -> 0x0286, TryCatch #2 {Exception -> 0x0286, blocks: (B:39:0x00ef, B:40:0x0103, B:42:0x0109, B:44:0x0132, B:46:0x013d, B:47:0x0180, B:49:0x01d8, B:51:0x01de, B:53:0x01e3, B:55:0x01ff, B:56:0x0220, B:57:0x0223), top: B:70:0x00ef }] */
    /* JADX WARN: Type inference failed for: r15v10 */
    /* JADX WARN: Type inference failed for: r15v5 */
    /* JADX WARN: Type inference failed for: r6v0 */
    /* JADX WARN: Type inference failed for: r6v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r6v21 */
    /* JADX WARN: Type inference failed for: r6v22 */
    /* renamed from: markMessagesAsDeletedInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<java.lang.Long> lambda$markMessagesAsDeleted$174(long r20, int r22, boolean r23) {
        /*
            Method dump skipped, instructions count: 659
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesAsDeleted$174(long, int, boolean):java.util.ArrayList");
    }

    public /* synthetic */ void lambda$markMessagesAsDeletedInternal$173(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public ArrayList<Long> markMessagesAsDeleted(final long j, final int i, boolean z, final boolean z2) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda80
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$markMessagesAsDeleted$174(j, i, z2);
                }
            });
            return null;
        }
        return lambda$markMessagesAsDeleted$174(j, i, z2);
    }

    private void fixUnsupportedMedia(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message == null) {
            return;
        }
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaUnsupported_old) {
            if (tLRPC$MessageMedia.bytes.length != 0) {
                return;
            }
            tLRPC$MessageMedia.bytes = Utilities.intToBytes(143);
        } else if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaUnsupported)) {
        } else {
            TLRPC$TL_messageMediaUnsupported_old tLRPC$TL_messageMediaUnsupported_old = new TLRPC$TL_messageMediaUnsupported_old();
            tLRPC$Message.media = tLRPC$TL_messageMediaUnsupported_old;
            tLRPC$TL_messageMediaUnsupported_old.bytes = Utilities.intToBytes(143);
            tLRPC$Message.flags |= 512;
        }
    }

    private void doneHolesInTable(String str, long j, int i) throws Exception {
        if (i == 0) {
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM " + str + " WHERE uid = %d", Long.valueOf(j))).stepThis().dispose();
        } else {
            SQLiteDatabase sQLiteDatabase2 = this.database;
            Locale locale2 = Locale.US;
            sQLiteDatabase2.executeFast(String.format(locale2, "DELETE FROM " + str + " WHERE uid = %d AND start = 0", Long.valueOf(j))).stepThis().dispose();
        }
        SQLiteDatabase sQLiteDatabase3 = this.database;
        SQLitePreparedStatement executeFast = sQLiteDatabase3.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?)");
        executeFast.requery();
        executeFast.bindLong(1, j);
        executeFast.bindInteger(2, 1);
        executeFast.bindInteger(3, 1);
        executeFast.step();
        executeFast.dispose();
    }

    public void doneHolesInMedia(long j, int i, int i2) throws Exception {
        if (i2 == -1) {
            if (i == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", Long.valueOf(j))).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", Long.valueOf(j))).stepThis().dispose();
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
            for (int i3 = 0; i3 < 8; i3++) {
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, i3);
                executeFast.bindInteger(3, 1);
                executeFast.bindInteger(4, 1);
                executeFast.step();
            }
            executeFast.dispose();
            return;
        }
        if (i == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", Long.valueOf(j), Integer.valueOf(i2))).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", Long.valueOf(j), Integer.valueOf(i2))).stepThis().dispose();
        }
        SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        executeFast2.requery();
        executeFast2.bindLong(1, j);
        executeFast2.bindInteger(2, i2);
        executeFast2.bindInteger(3, 1);
        executeFast2.bindInteger(4, 1);
        executeFast2.step();
        executeFast2.dispose();
    }

    /* loaded from: classes.dex */
    public static class Hole {
        public int end;
        public int start;
        public int type;

        public Hole(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public Hole(int i, int i2, int i3) {
            this.type = i;
            this.start = i2;
            this.end = i3;
        }
    }

    public void closeHolesInMedia(long j, int i, int i2, int i3) {
        SQLiteCursor sQLiteCursor;
        ArrayList arrayList;
        int i4 = 4;
        try {
            if (i3 < 0) {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)), new Object[0]);
            } else {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)), new Object[0]);
            }
            arrayList = null;
            while (sQLiteCursor.next()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                int intValue = sQLiteCursor.intValue(0);
                int intValue2 = sQLiteCursor.intValue(1);
                int intValue3 = sQLiteCursor.intValue(2);
                if (intValue2 != intValue3 || intValue2 != 1) {
                    arrayList.add(new Hole(intValue, intValue2, intValue3));
                }
            }
            sQLiteCursor.dispose();
        } catch (Exception e) {
            FileLog.e(e);
            return;
        }
        if (arrayList != null) {
            for (int i5 = 0; i5 < arrayList.size(); i5++) {
                Hole hole = (Hole) arrayList.get(i5);
                int i6 = hole.end;
                if (i2 >= i6 - 1 && i <= hole.start + 1) {
                    SQLiteDatabase sQLiteDatabase = this.database;
                    Locale locale = Locale.US;
                    Object[] objArr = new Object[i4];
                    objArr[0] = Long.valueOf(j);
                    objArr[1] = Integer.valueOf(hole.type);
                    objArr[2] = Integer.valueOf(hole.start);
                    objArr[3] = Integer.valueOf(hole.end);
                    sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", objArr)).stepThis().dispose();
                } else {
                    if (i2 < i6 - 1) {
                        int i7 = hole.start;
                        if (i > i7 + 1) {
                            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                            executeFast.requery();
                            executeFast.bindLong(1, j);
                            executeFast.bindInteger(2, hole.type);
                            executeFast.bindInteger(3, hole.start);
                            executeFast.bindInteger(4, i);
                            executeFast.step();
                            executeFast.requery();
                            executeFast.bindLong(1, j);
                            executeFast.bindInteger(2, hole.type);
                            executeFast.bindInteger(3, i2);
                            i4 = 4;
                            executeFast.bindInteger(4, hole.end);
                            executeFast.step();
                            executeFast.dispose();
                        } else if (i7 != i2) {
                            try {
                                this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                            } catch (Exception e2) {
                                FileLog.e((Throwable) e2, false);
                            }
                        }
                    } else if (i6 != i) {
                        try {
                            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3, false);
                        }
                    }
                    FileLog.e(e);
                    return;
                }
                i4 = 4;
            }
        }
    }

    private void closeHolesInTable(String str, long j, int i, int i2) {
        int i3;
        ArrayList arrayList;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            i3 = 1;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT start, end FROM " + str + " WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)), new Object[0]);
            arrayList = null;
            while (queryFinalized.next()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                int intValue = queryFinalized.intValue(0);
                int intValue2 = queryFinalized.intValue(1);
                if (intValue != intValue2 || intValue != 1) {
                    arrayList.add(new Hole(intValue, intValue2));
                }
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
            return;
        }
        if (arrayList != null) {
            int i4 = 0;
            while (i4 < arrayList.size()) {
                Hole hole = (Hole) arrayList.get(i4);
                int i5 = hole.end;
                if (i2 >= i5 - 1 && i <= hole.start + i3) {
                    SQLiteDatabase sQLiteDatabase2 = this.database;
                    Locale locale2 = Locale.US;
                    sQLiteDatabase2.executeFast(String.format(locale2, "DELETE FROM " + str + " WHERE uid = %d AND start = %d AND end = %d", Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                } else {
                    if (i2 < i5 - 1) {
                        int i6 = hole.start;
                        if (i > i6 + 1) {
                            SQLiteDatabase sQLiteDatabase3 = this.database;
                            Locale locale3 = Locale.US;
                            sQLiteDatabase3.executeFast(String.format(locale3, "DELETE FROM " + str + " WHERE uid = %d AND start = %d AND end = %d", Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                            SQLiteDatabase sQLiteDatabase4 = this.database;
                            SQLitePreparedStatement executeFast = sQLiteDatabase4.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?)");
                            executeFast.requery();
                            executeFast.bindLong(1, j);
                            executeFast.bindInteger(2, hole.start);
                            executeFast.bindInteger(3, i);
                            executeFast.step();
                            executeFast.requery();
                            executeFast.bindLong(1, j);
                            executeFast.bindInteger(2, i2);
                            executeFast.bindInteger(3, hole.end);
                            executeFast.step();
                            executeFast.dispose();
                            i4++;
                            i3 = 1;
                        } else if (i6 != i2) {
                            try {
                                SQLiteDatabase sQLiteDatabase5 = this.database;
                                Locale locale4 = Locale.US;
                                sQLiteDatabase5.executeFast(String.format(locale4, "UPDATE " + str + " SET start = %d WHERE uid = %d AND start = %d AND end = %d", Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                            } catch (Exception e2) {
                                FileLog.e((Throwable) e2, false);
                            }
                        }
                    } else if (i5 != i) {
                        try {
                            SQLiteDatabase sQLiteDatabase6 = this.database;
                            Locale locale5 = Locale.US;
                            sQLiteDatabase6.executeFast(String.format(locale5, "UPDATE " + str + " SET end = %d WHERE uid = %d AND start = %d AND end = %d", Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3, false);
                        }
                    }
                    FileLog.e(e);
                    return;
                }
                i4++;
                i3 = 1;
            }
        }
    }

    public void replaceMessageIfExists(final TLRPC$Message tLRPC$Message, final ArrayList<TLRPC$User> arrayList, final ArrayList<TLRPC$Chat> arrayList2, final boolean z) {
        if (tLRPC$Message == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda173
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$replaceMessageIfExists$176(tLRPC$Message, z, arrayList, arrayList2);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0051 A[Catch: Exception -> 0x01da, TRY_ENTER, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0072 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00ac A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00c6 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00cc A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00d7  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00d9  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00e9  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00eb  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00fc A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0110 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0119 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0124 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0134 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0138 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0144 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0165 A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x016a A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:70:0x017d A[Catch: Exception -> 0x01da, TryCatch #0 {Exception -> 0x01da, blocks: (B:6:0x0032, B:10:0x003e, B:19:0x0051, B:21:0x0055, B:23:0x0072, B:24:0x0075, B:26:0x00ac, B:31:0x00b4, B:33:0x00c6, B:34:0x00cc, B:35:0x00d3, B:41:0x00de, B:45:0x00ec, B:47:0x00fc, B:48:0x0110, B:49:0x0113, B:51:0x0119, B:54:0x011e, B:55:0x0120, B:56:0x0124, B:57:0x0127, B:59:0x0134, B:60:0x0138, B:61:0x013b, B:63:0x0144, B:65:0x0165, B:67:0x016a, B:68:0x016d, B:70:0x017d, B:71:0x0188, B:73:0x018e, B:74:0x01a2, B:76:0x01a8, B:77:0x01bc, B:83:0x01e0, B:84:0x01e3), top: B:87:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:97:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$replaceMessageIfExists$176(org.telegram.tgnet.TLRPC$Message r17, boolean r18, java.util.ArrayList r19, java.util.ArrayList r20) {
        /*
            Method dump skipped, instructions count: 488
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$replaceMessageIfExists$176(org.telegram.tgnet.TLRPC$Message, boolean, java.util.ArrayList, java.util.ArrayList):void");
    }

    public /* synthetic */ void lambda$replaceMessageIfExists$175(MessageObject messageObject, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
    }

    public void putMessages(final TLRPC$messages_Messages tLRPC$messages_Messages, final long j, final int i, final int i2, final boolean z, final boolean z2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda188
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putMessages$178(z2, j, tLRPC$messages_Messages, i, i2, z);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:61:0x0238, code lost:
        if (r4.id == r2.id) goto L62;
     */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0439 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0441 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:132:0x044e  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x0452  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0463  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x0465  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0476 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:144:0x048d A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:147:0x0497 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x04a4 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:155:0x04bb A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:156:0x04bf A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:159:0x04cb A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:160:0x04ee A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:173:0x0551 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:175:0x0556 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0560 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:187:0x05a4  */
    /* JADX WARN: Removed duplicated region for block: B:191:0x05d2 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:199:0x0605 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x025a A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0271  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0285 A[Catch: Exception -> 0x06dd, TryCatch #0 {Exception -> 0x06dd, blocks: (B:4:0x0013, B:6:0x0040, B:9:0x004d, B:10:0x007f, B:11:0x0082, B:12:0x009d, B:15:0x00a8, B:17:0x00b1, B:19:0x00b8, B:22:0x00ef, B:32:0x0124, B:33:0x012e, B:34:0x0155, B:36:0x018d, B:38:0x019b, B:42:0x01ad, B:44:0x01e1, B:46:0x01e8, B:48:0x0205, B:50:0x0213, B:51:0x0217, B:52:0x021e, B:54:0x0224, B:56:0x022a, B:58:0x022e, B:60:0x0232, B:63:0x023c, B:65:0x0240, B:67:0x0246, B:69:0x024a, B:71:0x024e, B:76:0x025a, B:78:0x027b, B:80:0x0285, B:82:0x0293, B:86:0x029e, B:90:0x02ab, B:92:0x02cb, B:93:0x02cf, B:97:0x02d7, B:98:0x02da, B:100:0x02de, B:102:0x02eb, B:109:0x0306, B:111:0x0326, B:113:0x033d, B:115:0x0342, B:116:0x0375, B:117:0x03dc, B:119:0x03e6, B:121:0x0420, B:126:0x0428, B:128:0x0439, B:129:0x0441, B:130:0x044a, B:137:0x045c, B:141:0x0466, B:143:0x0476, B:144:0x048d, B:145:0x0493, B:147:0x0497, B:150:0x049c, B:151:0x049e, B:152:0x04a4, B:153:0x04aa, B:155:0x04bb, B:156:0x04bf, B:157:0x04c2, B:159:0x04cb, B:160:0x04ee, B:162:0x04f3, B:170:0x0549, B:173:0x0551, B:175:0x0556, B:176:0x0559, B:178:0x0560, B:181:0x0566, B:183:0x0571, B:185:0x059e, B:188:0x05a6, B:190:0x05b1, B:191:0x05d2, B:194:0x05d8, B:196:0x05e3, B:199:0x0605, B:203:0x060f, B:209:0x061e, B:210:0x0630, B:212:0x0642, B:214:0x0647, B:216:0x0656, B:218:0x065b, B:219:0x0662, B:221:0x068c, B:222:0x06c3, B:224:0x06ca), top: B:228:0x0011 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putMessages$178(boolean r38, long r39, org.telegram.tgnet.TLRPC$messages_Messages r41, int r42, int r43, boolean r44) {
        /*
            Method dump skipped, instructions count: 1762
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$178(boolean, long, org.telegram.tgnet.TLRPC$messages_Messages, int, int, boolean):void");
    }

    public /* synthetic */ void lambda$putMessages$177(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public static void addUsersAndChatsFromMessage(TLRPC$Message tLRPC$Message, ArrayList<Long> arrayList, ArrayList<Long> arrayList2) {
        String str;
        TLRPC$Peer tLRPC$Peer;
        long fromChatId = MessageObject.getFromChatId(tLRPC$Message);
        if (DialogObject.isUserDialog(fromChatId)) {
            if (!arrayList.contains(Long.valueOf(fromChatId))) {
                arrayList.add(Long.valueOf(fromChatId));
            }
        } else if (DialogObject.isChatDialog(fromChatId)) {
            long j = -fromChatId;
            if (!arrayList2.contains(Long.valueOf(j))) {
                arrayList2.add(Long.valueOf(j));
            }
        }
        long j2 = tLRPC$Message.via_bot_id;
        if (j2 != 0 && !arrayList.contains(Long.valueOf(j2))) {
            arrayList.add(Long.valueOf(tLRPC$Message.via_bot_id));
        }
        TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
        if (tLRPC$MessageAction != null) {
            long j3 = tLRPC$MessageAction.user_id;
            if (j3 != 0 && !arrayList.contains(Long.valueOf(j3))) {
                arrayList.add(Long.valueOf(tLRPC$Message.action.user_id));
            }
            long j4 = tLRPC$Message.action.channel_id;
            if (j4 != 0 && !arrayList2.contains(Long.valueOf(j4))) {
                arrayList2.add(Long.valueOf(tLRPC$Message.action.channel_id));
            }
            long j5 = tLRPC$Message.action.chat_id;
            if (j5 != 0 && !arrayList2.contains(Long.valueOf(j5))) {
                arrayList2.add(Long.valueOf(tLRPC$Message.action.chat_id));
            }
            TLRPC$MessageAction tLRPC$MessageAction2 = tLRPC$Message.action;
            if (tLRPC$MessageAction2 instanceof TLRPC$TL_messageActionGeoProximityReached) {
                TLRPC$TL_messageActionGeoProximityReached tLRPC$TL_messageActionGeoProximityReached = (TLRPC$TL_messageActionGeoProximityReached) tLRPC$MessageAction2;
                long peerId = MessageObject.getPeerId(tLRPC$TL_messageActionGeoProximityReached.from_id);
                if (DialogObject.isUserDialog(peerId)) {
                    if (!arrayList.contains(Long.valueOf(peerId))) {
                        arrayList.add(Long.valueOf(peerId));
                    }
                } else {
                    long j6 = -peerId;
                    if (!arrayList2.contains(Long.valueOf(j6))) {
                        arrayList2.add(Long.valueOf(j6));
                    }
                }
                long peerId2 = MessageObject.getPeerId(tLRPC$TL_messageActionGeoProximityReached.to_id);
                if (peerId2 > 0) {
                    if (!arrayList.contains(Long.valueOf(peerId2))) {
                        arrayList.add(Long.valueOf(peerId2));
                    }
                } else {
                    long j7 = -peerId2;
                    if (!arrayList2.contains(Long.valueOf(j7))) {
                        arrayList2.add(Long.valueOf(j7));
                    }
                }
            }
            if (!tLRPC$Message.action.users.isEmpty()) {
                for (int i = 0; i < tLRPC$Message.action.users.size(); i++) {
                    Long l = tLRPC$Message.action.users.get(i);
                    if (!arrayList.contains(l)) {
                        arrayList.add(l);
                    }
                }
            }
        }
        if (!tLRPC$Message.entities.isEmpty()) {
            for (int i2 = 0; i2 < tLRPC$Message.entities.size(); i2++) {
                TLRPC$MessageEntity tLRPC$MessageEntity = tLRPC$Message.entities.get(i2);
                if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityMentionName) {
                    arrayList.add(Long.valueOf(((TLRPC$TL_messageEntityMentionName) tLRPC$MessageEntity).user_id));
                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_inputMessageEntityMentionName) {
                    arrayList.add(Long.valueOf(((TLRPC$TL_inputMessageEntityMentionName) tLRPC$MessageEntity).user_id.user_id));
                }
            }
        }
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia != null) {
            long j8 = tLRPC$MessageMedia.user_id;
            if (j8 != 0 && !arrayList.contains(Long.valueOf(j8))) {
                arrayList.add(Long.valueOf(tLRPC$Message.media.user_id));
            }
            TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message.media;
            if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaPoll) {
                TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = (TLRPC$TL_messageMediaPoll) tLRPC$MessageMedia2;
                if (!tLRPC$TL_messageMediaPoll.results.recent_voters.isEmpty()) {
                    arrayList.addAll(tLRPC$TL_messageMediaPoll.results.recent_voters);
                }
            }
        }
        TLRPC$MessageReplies tLRPC$MessageReplies = tLRPC$Message.replies;
        if (tLRPC$MessageReplies != null) {
            int size = tLRPC$MessageReplies.recent_repliers.size();
            for (int i3 = 0; i3 < size; i3++) {
                long peerId3 = MessageObject.getPeerId(tLRPC$Message.replies.recent_repliers.get(i3));
                if (DialogObject.isUserDialog(peerId3)) {
                    if (!arrayList.contains(Long.valueOf(peerId3))) {
                        arrayList.add(Long.valueOf(peerId3));
                    }
                } else if (DialogObject.isChatDialog(peerId3)) {
                    long j9 = -peerId3;
                    if (!arrayList2.contains(Long.valueOf(j9))) {
                        arrayList2.add(Long.valueOf(j9));
                    }
                }
            }
        }
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = tLRPC$Message.reply_to;
        if (tLRPC$TL_messageReplyHeader != null && (tLRPC$Peer = tLRPC$TL_messageReplyHeader.reply_to_peer_id) != null) {
            long peerId4 = MessageObject.getPeerId(tLRPC$Peer);
            if (DialogObject.isUserDialog(peerId4)) {
                if (!arrayList.contains(Long.valueOf(peerId4))) {
                    arrayList.add(Long.valueOf(peerId4));
                }
            } else if (DialogObject.isChatDialog(peerId4)) {
                long j10 = -peerId4;
                if (!arrayList2.contains(Long.valueOf(j10))) {
                    arrayList2.add(Long.valueOf(j10));
                }
            }
        }
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = tLRPC$Message.fwd_from;
        if (tLRPC$MessageFwdHeader != null) {
            TLRPC$Peer tLRPC$Peer2 = tLRPC$MessageFwdHeader.from_id;
            if (tLRPC$Peer2 instanceof TLRPC$TL_peerUser) {
                if (!arrayList.contains(Long.valueOf(tLRPC$Peer2.user_id))) {
                    arrayList.add(Long.valueOf(tLRPC$Message.fwd_from.from_id.user_id));
                }
            } else if (tLRPC$Peer2 instanceof TLRPC$TL_peerChannel) {
                if (!arrayList2.contains(Long.valueOf(tLRPC$Peer2.channel_id))) {
                    arrayList2.add(Long.valueOf(tLRPC$Message.fwd_from.from_id.channel_id));
                }
            } else if ((tLRPC$Peer2 instanceof TLRPC$TL_peerChat) && !arrayList2.contains(Long.valueOf(tLRPC$Peer2.chat_id))) {
                arrayList2.add(Long.valueOf(tLRPC$Message.fwd_from.from_id.chat_id));
            }
            TLRPC$Peer tLRPC$Peer3 = tLRPC$Message.fwd_from.saved_from_peer;
            if (tLRPC$Peer3 != null) {
                long j11 = tLRPC$Peer3.user_id;
                if (j11 != 0) {
                    if (!arrayList2.contains(Long.valueOf(j11))) {
                        arrayList.add(Long.valueOf(tLRPC$Message.fwd_from.saved_from_peer.user_id));
                    }
                } else {
                    long j12 = tLRPC$Peer3.channel_id;
                    if (j12 != 0) {
                        if (!arrayList2.contains(Long.valueOf(j12))) {
                            arrayList2.add(Long.valueOf(tLRPC$Message.fwd_from.saved_from_peer.channel_id));
                        }
                    } else {
                        long j13 = tLRPC$Peer3.chat_id;
                        if (j13 != 0 && !arrayList2.contains(Long.valueOf(j13))) {
                            arrayList2.add(Long.valueOf(tLRPC$Message.fwd_from.saved_from_peer.chat_id));
                        }
                    }
                }
            }
        }
        HashMap<String, String> hashMap = tLRPC$Message.params;
        if (hashMap == null || (str = hashMap.get("fwd_peer")) == null) {
            return;
        }
        long longValue = Utilities.parseLong(str).longValue();
        if (longValue >= 0) {
            return;
        }
        long j14 = -longValue;
        if (arrayList2.contains(Long.valueOf(j14))) {
            return;
        }
        arrayList2.add(Long.valueOf(j14));
    }

    public void getDialogs(final int i, final int i2, final int i3, boolean z) {
        LongSparseArray<SparseArray<TLRPC$DraftMessage>> drafts;
        int size;
        long[] jArr = null;
        if (z && (size = (drafts = getMediaDataController().getDrafts()).size()) > 0) {
            jArr = new long[size];
            for (int i4 = 0; i4 < size; i4++) {
                if (drafts.valueAt(i4).get(0) != null) {
                    jArr[i4] = drafts.keyAt(i4);
                }
            }
        }
        final long[] jArr2 = jArr;
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogs$180(i, i2, i3, jArr2);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0139  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x013b  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0150  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0152  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x016b A[Catch: Exception -> 0x02a1, TryCatch #10 {Exception -> 0x02a1, blocks: (B:16:0x0099, B:18:0x00a3, B:20:0x00b0, B:22:0x00b6, B:23:0x00c7, B:26:0x00db, B:27:0x00e7, B:28:0x00ee, B:30:0x010f, B:36:0x011d, B:40:0x013c, B:44:0x0153, B:46:0x016b, B:48:0x0173, B:49:0x0178, B:51:0x018f, B:53:0x0199, B:55:0x01a0, B:57:0x01ab, B:59:0x01cf, B:60:0x01d1), top: B:201:0x0099 }] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x018f A[Catch: Exception -> 0x02a1, TryCatch #10 {Exception -> 0x02a1, blocks: (B:16:0x0099, B:18:0x00a3, B:20:0x00b0, B:22:0x00b6, B:23:0x00c7, B:26:0x00db, B:27:0x00e7, B:28:0x00ee, B:30:0x010f, B:36:0x011d, B:40:0x013c, B:44:0x0153, B:46:0x016b, B:48:0x0173, B:49:0x0178, B:51:0x018f, B:53:0x0199, B:55:0x01a0, B:57:0x01ab, B:59:0x01cf, B:60:0x01d1), top: B:201:0x0099 }] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0198  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x01a0 A[Catch: Exception -> 0x02a1, TryCatch #10 {Exception -> 0x02a1, blocks: (B:16:0x0099, B:18:0x00a3, B:20:0x00b0, B:22:0x00b6, B:23:0x00c7, B:26:0x00db, B:27:0x00e7, B:28:0x00ee, B:30:0x010f, B:36:0x011d, B:40:0x013c, B:44:0x0153, B:46:0x016b, B:48:0x0173, B:49:0x0178, B:51:0x018f, B:53:0x0199, B:55:0x01a0, B:57:0x01ab, B:59:0x01cf, B:60:0x01d1), top: B:201:0x0099 }] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0243  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x024c A[Catch: Exception -> 0x03df, TryCatch #0 {Exception -> 0x03df, blocks: (B:87:0x0238, B:88:0x023c, B:91:0x0246, B:93:0x024c, B:95:0x025c, B:96:0x0264, B:98:0x026c, B:100:0x0276, B:101:0x027e, B:103:0x0284, B:105:0x028f, B:109:0x02a6, B:115:0x02cc, B:118:0x02e1, B:119:0x02e7, B:121:0x02ea, B:126:0x02f9, B:128:0x0303, B:129:0x030b, B:131:0x0316, B:132:0x031d, B:134:0x0329, B:135:0x0332, B:136:0x0335, B:138:0x033b), top: B:181:0x0238 }] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0264 A[Catch: Exception -> 0x03df, TryCatch #0 {Exception -> 0x03df, blocks: (B:87:0x0238, B:88:0x023c, B:91:0x0246, B:93:0x024c, B:95:0x025c, B:96:0x0264, B:98:0x026c, B:100:0x0276, B:101:0x027e, B:103:0x0284, B:105:0x028f, B:109:0x02a6, B:115:0x02cc, B:118:0x02e1, B:119:0x02e7, B:121:0x02ea, B:126:0x02f9, B:128:0x0303, B:129:0x030b, B:131:0x0316, B:132:0x031d, B:134:0x0329, B:135:0x0332, B:136:0x0335, B:138:0x033b), top: B:181:0x0238 }] */
    /* JADX WARN: Type inference failed for: r0v27, types: [java.lang.Object, org.telegram.tgnet.TLRPC$Dialog] */
    /* JADX WARN: Type inference failed for: r0v51, types: [org.telegram.tgnet.TLRPC$TL_dialog] */
    /* JADX WARN: Type inference failed for: r0v52, types: [org.telegram.tgnet.TLRPC$TL_dialogFolder] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getDialogs$180(int r24, int r25, int r26, long[] r27) {
        /*
            Method dump skipped, instructions count: 1045
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogs$180(int, int, int, long[]):void");
    }

    public /* synthetic */ void lambda$getDialogs$179(LongSparseArray longSparseArray) {
        MediaDataController mediaDataController = getMediaDataController();
        mediaDataController.clearDraftsFolderIds();
        if (longSparseArray != null) {
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                mediaDataController.setDraftFolderId(longSparseArray.keyAt(i), ((Integer) longSparseArray.valueAt(i)).intValue());
            }
        }
    }

    public static void createFirstHoles(long j, SQLitePreparedStatement sQLitePreparedStatement, SQLitePreparedStatement sQLitePreparedStatement2, int i) throws Exception {
        sQLitePreparedStatement.requery();
        sQLitePreparedStatement.bindLong(1, j);
        sQLitePreparedStatement.bindInteger(2, i == 1 ? 1 : 0);
        sQLitePreparedStatement.bindInteger(3, i);
        sQLitePreparedStatement.step();
        for (int i2 = 0; i2 < 8; i2++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            sQLitePreparedStatement2.bindInteger(2, i2);
            sQLitePreparedStatement2.bindInteger(3, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(4, i);
            sQLitePreparedStatement2.step();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x0121, code lost:
        if (r12 < 0) goto L35;
     */
    /* JADX WARN: Removed duplicated region for block: B:101:0x0305  */
    /* JADX WARN: Removed duplicated region for block: B:104:0x036e A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:105:0x0387 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:108:0x0399 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:111:0x03a0 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x013b A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x01b2 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x01b5  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x01bf  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x01c3  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x01d1  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01d3  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01e4 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x01fa A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0204 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0211 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x022b A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0251 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0261 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:92:0x029a  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x02c4  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x02c8 A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:99:0x02eb A[Catch: Exception -> 0x040a, TryCatch #0 {Exception -> 0x040a, blocks: (B:3:0x0006, B:4:0x0017, B:6:0x001f, B:7:0x0031, B:9:0x0039, B:10:0x006f, B:12:0x0079, B:14:0x0090, B:19:0x00bf, B:21:0x00e1, B:23:0x00e5, B:26:0x00ef, B:29:0x00f6, B:31:0x0118, B:33:0x011e, B:37:0x0131, B:39:0x013b, B:41:0x0148, B:43:0x0156, B:45:0x019a, B:50:0x01a2, B:52:0x01b2, B:54:0x01b6, B:62:0x01ca, B:66:0x01d4, B:68:0x01e4, B:69:0x01fa, B:70:0x0200, B:72:0x0204, B:75:0x0209, B:76:0x020b, B:77:0x0211, B:78:0x0217, B:80:0x022b, B:82:0x0251, B:83:0x0254, B:85:0x025b, B:88:0x0261, B:89:0x0269, B:90:0x0292, B:93:0x029c, B:94:0x02a4, B:98:0x02c8, B:99:0x02eb, B:102:0x0310, B:104:0x036e, B:105:0x0387, B:106:0x038d, B:108:0x0399, B:109:0x039c, B:111:0x03a0, B:115:0x03b3, B:117:0x03bd, B:118:0x03ca, B:120:0x03e4, B:122:0x03f3, B:123:0x03f6), top: B:127:0x0006 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs r24, int r25) {
        /*
            Method dump skipped, instructions count: 1039
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    public void getDialogFolderId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda103
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogFolderId$182(j, intCallback);
            }
        });
    }

    public /* synthetic */ void lambda$getDialogFolderId$182(long j, final IntCallback intCallback) {
        try {
            final int i = -1;
            if (this.unknownDialogsIds.get(j) == null) {
                SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT folder_id FROM dialogs WHERE did = ?", Long.valueOf(j));
                if (queryFinalized.next()) {
                    i = queryFinalized.intValue(0);
                }
                queryFinalized.dispose();
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.IntCallback.this.run(i);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogsFolderId(final ArrayList<TLRPC$TL_folderPeer> arrayList, final ArrayList<TLRPC$TL_inputFolderPeer> arrayList2, final long j, final int i) {
        if (arrayList == null && arrayList2 == null && j == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda144
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogsFolderId$183(arrayList, arrayList2, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$setDialogsFolderId$183(ArrayList arrayList, ArrayList arrayList2, int i, long j) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET folder_id = ?, pinned = ? WHERE did = ?");
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$TL_folderPeer tLRPC$TL_folderPeer = (TLRPC$TL_folderPeer) arrayList.get(i2);
                    long peerDialogId = DialogObject.getPeerDialogId(tLRPC$TL_folderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tLRPC$TL_folderPeer.folder_id);
                    executeFast.bindInteger(2, 0);
                    executeFast.bindLong(3, peerDialogId);
                    executeFast.step();
                    this.unknownDialogsIds.remove(peerDialogId);
                }
            } else if (arrayList2 != null) {
                int size2 = arrayList2.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    TLRPC$TL_inputFolderPeer tLRPC$TL_inputFolderPeer = (TLRPC$TL_inputFolderPeer) arrayList2.get(i3);
                    long peerDialogId2 = DialogObject.getPeerDialogId(tLRPC$TL_inputFolderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tLRPC$TL_inputFolderPeer.folder_id);
                    executeFast.bindInteger(2, 0);
                    executeFast.bindLong(3, peerDialogId2);
                    executeFast.step();
                    this.unknownDialogsIds.remove(peerDialogId2);
                }
            } else {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindInteger(2, 0);
                executeFast.bindLong(3, j);
                executeFast.step();
            }
            executeFast.dispose();
            this.database.commitTransaction();
            lambda$checkIfFolderEmpty$185(1);
            resetAllUnreadCounters(false);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: checkIfFolderEmptyInternal */
    public void lambda$checkIfFolderEmpty$185(final int i) {
        try {
            boolean z = true;
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = ?", Integer.valueOf(i));
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                if (!DialogObject.isUserDialog(longValue) && !DialogObject.isEncryptedDialog(longValue)) {
                    TLRPC$Chat chat = getChat(-longValue);
                    if (ChatObject.isNotInChat(chat) || chat.migrated_to != null) {
                    }
                }
                z = false;
            }
            queryFinalized.dispose();
            if (!z) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$checkIfFolderEmptyInternal$184(i);
                }
            });
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM dialogs WHERE did = " + DialogObject.makeFolderDialogId(i)).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$checkIfFolderEmptyInternal$184(int i) {
        getMessagesController().onFolderEmpty(i);
    }

    public void checkIfFolderEmpty(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$checkIfFolderEmpty$185(i);
            }
        });
    }

    public void unpinAllDialogsExceptNew(final ArrayList<Long> arrayList, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda139
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$unpinAllDialogsExceptNew$186(arrayList, i);
            }
        });
    }

    public /* synthetic */ void lambda$unpinAllDialogsExceptNew$186(ArrayList arrayList, int i) {
        try {
            ArrayList arrayList2 = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did, folder_id FROM dialogs WHERE pinned > 0 AND did NOT IN (%s)", TextUtils.join(",", arrayList)), new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                if (queryFinalized.intValue(1) == i && !DialogObject.isEncryptedDialog(longValue) && !DialogObject.isFolderDialogId(longValue)) {
                    arrayList2.add(Long.valueOf(queryFinalized.longValue(0)));
                }
            }
            queryFinalized.dispose();
            if (arrayList2.isEmpty()) {
                return;
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                long longValue2 = ((Long) arrayList2.get(i2)).longValue();
                executeFast.requery();
                executeFast.bindInteger(1, 0);
                executeFast.bindLong(2, longValue2);
                executeFast.step();
            }
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogUnread(final long j, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda110
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogUnread$187(j, z);
            }
        });
    }

    public /* synthetic */ void lambda$setDialogUnread$187(long j, boolean z) {
        int i;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteCursor = sQLiteDatabase.queryFinalized("SELECT flags FROM dialogs WHERE did = " + j, new Object[0]);
                i = sQLiteCursor.next() ? sQLiteCursor.intValue(0) : 0;
                sQLiteCursor.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
                i = 0;
            }
            int i2 = z ? i | 1 : i & (-2);
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET flags = ? WHERE did = ?");
            executeFast.bindInteger(1, i2);
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
            resetAllUnreadCounters(false);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    private void resetAllUnreadCounters(boolean z) {
        int size = this.dialogFilters.size();
        for (int i = 0; i < size; i++) {
            MessagesController.DialogFilter dialogFilter = this.dialogFilters.get(i);
            if (z) {
                if ((dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                    dialogFilter.pendingUnreadCount = -1;
                }
            } else {
                dialogFilter.pendingUnreadCount = -1;
            }
        }
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$resetAllUnreadCounters$188();
            }
        });
    }

    public /* synthetic */ void lambda$resetAllUnreadCounters$188() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public void setDialogPinned(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogPinned$189(i, j);
            }
        });
    }

    public /* synthetic */ void lambda$setDialogPinned$189(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogsPinned(final ArrayList<Long> arrayList, final ArrayList<Integer> arrayList2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda143
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogsPinned$190(arrayList, arrayList2);
            }
        });
    }

    public /* synthetic */ void lambda$setDialogsPinned$190(ArrayList arrayList, ArrayList arrayList2) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                executeFast.requery();
                executeFast.bindInteger(1, ((Integer) arrayList2.get(i)).intValue());
                executeFast.bindLong(2, ((Long) arrayList.get(i)).longValue());
                executeFast.step();
            }
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putDialogs(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final int i) {
        if (tLRPC$messages_Dialogs.dialogs.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda178
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putDialogs$191(tLRPC$messages_Dialogs, i);
            }
        });
    }

    public /* synthetic */ void lambda$putDialogs$191(TLRPC$messages_Dialogs tLRPC$messages_Dialogs, int i) {
        putDialogsInternal(tLRPC$messages_Dialogs, i);
        try {
            loadUnreadMessages();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDialogMaxMessageId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda101
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogMaxMessageId$193(j, intCallback);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0031, code lost:
        if (r1 == null) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getDialogMaxMessageId$193(long r6, final org.telegram.messenger.MessagesStorage.IntCallback r8) {
        /*
            r5 = this;
            r0 = 1
            int[] r0 = new int[r0]
            r1 = 0
            org.telegram.SQLite.SQLiteDatabase r2 = r5.database     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r3.<init>()     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.String r4 = "SELECT MAX(mid) FROM messages_v2 WHERE uid = "
            r3.append(r4)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r3.append(r6)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.String r6 = r3.toString()     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r7 = 0
            java.lang.Object[] r3 = new java.lang.Object[r7]     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            org.telegram.SQLite.SQLiteCursor r1 = r2.queryFinalized(r6, r3)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            boolean r6 = r1.next()     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            if (r6 == 0) goto L33
            int r6 = r1.intValue(r7)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r0[r7] = r6     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            goto L33
        L2b:
            r6 = move-exception
            goto L3f
        L2d:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> L2b
            if (r1 == 0) goto L36
        L33:
            r1.dispose()
        L36:
            org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda4 r6 = new org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda4
            r6.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)
            return
        L3f:
            if (r1 == 0) goto L44
            r1.dispose()
        L44:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogMaxMessageId$193(long, org.telegram.messenger.MessagesStorage$IntCallback):void");
    }

    public static /* synthetic */ void lambda$getDialogMaxMessageId$192(IntCallback intCallback, int[] iArr) {
        intCallback.run(iArr[0]);
    }

    public int getDialogReadMax(final boolean z, final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] numArr = {0};
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda189
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogReadMax$194(z, j, numArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return numArr[0].intValue();
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x004f, code lost:
        if (r1 == null) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getDialogReadMax$194(boolean r5, long r6, java.lang.Integer[] r8, java.util.concurrent.CountDownLatch r9) {
        /*
            r4 = this;
            r0 = 0
            r1 = 0
            if (r5 == 0) goto L1e
            org.telegram.SQLite.SQLiteDatabase r5 = r4.database     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            r2.<init>()     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.String r3 = "SELECT outbox_max FROM dialogs WHERE did = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            r2.append(r6)     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.String r6 = r2.toString()     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.Object[] r7 = new java.lang.Object[r0]     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r6, r7)     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            goto L37
        L1e:
            org.telegram.SQLite.SQLiteDatabase r5 = r4.database     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            r2.<init>()     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.String r3 = "SELECT inbox_max FROM dialogs WHERE did = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            r2.append(r6)     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.String r6 = r2.toString()     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.Object[] r7 = new java.lang.Object[r0]     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r6, r7)     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
        L37:
            r1 = r5
            boolean r5 = r1.next()     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            if (r5 == 0) goto L51
            int r5 = r1.intValue(r0)     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            r8[r0] = r5     // Catch: java.lang.Throwable -> L49 java.lang.Exception -> L4b
            goto L51
        L49:
            r5 = move-exception
            goto L58
        L4b:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5)     // Catch: java.lang.Throwable -> L49
            if (r1 == 0) goto L54
        L51:
            r1.dispose()
        L54:
            r9.countDown()
            return
        L58:
            if (r1 == 0) goto L5d
            r1.dispose()
        L5d:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogReadMax$194(boolean, long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public int getChannelPtsSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] numArr = {0};
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda113
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getChannelPtsSync$195(j, numArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return numArr[0].intValue();
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0033, code lost:
        if (r0 == null) goto L20;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getChannelPtsSync$195(long r5, java.lang.Integer[] r7, java.util.concurrent.CountDownLatch r8) {
        /*
            r4 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r4.database     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            r2.<init>()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            java.lang.String r3 = "SELECT pts FROM dialogs WHERE did = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            long r5 = -r5
            r2.append(r5)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            java.lang.String r5 = r2.toString()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            r6 = 0
            java.lang.Object[] r2 = new java.lang.Object[r6]     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r5, r2)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            boolean r5 = r0.next()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            if (r5 == 0) goto L35
            int r5 = r0.intValue(r6)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            r7[r6] = r5     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            goto L35
        L2d:
            r5 = move-exception
            goto L41
        L2f:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5)     // Catch: java.lang.Throwable -> L2d
            if (r0 == 0) goto L38
        L35:
            r0.dispose()
        L38:
            r8.countDown()     // Catch: java.lang.Exception -> L3c
            goto L40
        L3c:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5)
        L40:
            return
        L41:
            if (r0 == 0) goto L46
            r0.dispose()
        L46:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getChannelPtsSync$195(long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public TLRPC$User getUserSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC$User[] tLRPC$UserArr = new TLRPC$User[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda195
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getUserSync$196(tLRPC$UserArr, j, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return tLRPC$UserArr[0];
    }

    public /* synthetic */ void lambda$getUserSync$196(TLRPC$User[] tLRPC$UserArr, long j, CountDownLatch countDownLatch) {
        tLRPC$UserArr[0] = getUser(j);
        countDownLatch.countDown();
    }

    public TLRPC$Chat getChatSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC$Chat[] tLRPC$ChatArr = new TLRPC$Chat[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda193
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getChatSync$197(tLRPC$ChatArr, j, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return tLRPC$ChatArr[0];
    }

    public /* synthetic */ void lambda$getChatSync$197(TLRPC$Chat[] tLRPC$ChatArr, long j, CountDownLatch countDownLatch) {
        tLRPC$ChatArr[0] = getChat(j);
        countDownLatch.countDown();
    }

    public TLRPC$User getUser(long j) {
        try {
            ArrayList<TLRPC$User> arrayList = new ArrayList<>();
            getUsersInternal("" + j, arrayList);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public ArrayList<TLRPC$User> getUsers(ArrayList<Long> arrayList) {
        ArrayList<TLRPC$User> arrayList2 = new ArrayList<>();
        try {
            getUsersInternal(TextUtils.join(",", arrayList), arrayList2);
        } catch (Exception e) {
            arrayList2.clear();
            FileLog.e(e);
        }
        return arrayList2;
    }

    public TLRPC$Chat getChat(long j) {
        try {
            ArrayList<TLRPC$Chat> arrayList = new ArrayList<>();
            getChatsInternal("" + j, arrayList);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public TLRPC$EncryptedChat getEncryptedChat(long j) {
        try {
            ArrayList<TLRPC$EncryptedChat> arrayList = new ArrayList<>();
            getEncryptedChatsInternal("" + j, arrayList, null);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:112:0x02c2 A[Catch: Exception -> 0x06c9, LOOP:2: B:85:0x0213->B:112:0x02c2, LOOP_END, TryCatch #0 {Exception -> 0x06c9, blocks: (B:3:0x0014, B:6:0x0023, B:8:0x004d, B:15:0x005d, B:17:0x0065, B:18:0x0069, B:20:0x007f, B:21:0x009d, B:22:0x00b2, B:24:0x00b8, B:26:0x00d6, B:35:0x00e9, B:37:0x00f5, B:41:0x0103, B:43:0x010e, B:48:0x011d, B:50:0x012b, B:52:0x0137, B:54:0x0143, B:56:0x0149, B:58:0x014f, B:62:0x016f, B:64:0x0177, B:66:0x017f, B:68:0x0190, B:70:0x01a1, B:71:0x01bc, B:74:0x01ca, B:75:0x01e6, B:77:0x01ec, B:80:0x0200, B:82:0x0207, B:86:0x0215, B:88:0x021f, B:91:0x0236, B:93:0x023c, B:97:0x0254, B:103:0x0262, B:105:0x0269, B:107:0x0283, B:109:0x028b, B:110:0x0296, B:111:0x02bd, B:112:0x02c2, B:115:0x02d9, B:117:0x02e5, B:119:0x02eb, B:120:0x0307, B:122:0x030d, B:127:0x0324, B:129:0x032c, B:132:0x0343, B:134:0x0349, B:137:0x035f, B:138:0x0362, B:140:0x0369, B:142:0x0376, B:144:0x037a, B:146:0x0380, B:148:0x0386, B:149:0x039e, B:150:0x03a1, B:152:0x03a7, B:153:0x03c3, B:155:0x03c9, B:159:0x03df, B:161:0x03e8, B:165:0x03f4, B:167:0x03fc, B:170:0x0413, B:172:0x0419, B:176:0x0431, B:181:0x043c, B:183:0x0443, B:185:0x0451, B:187:0x0458, B:191:0x046a, B:193:0x04f5, B:194:0x04f7, B:196:0x0503, B:199:0x050d, B:200:0x0536, B:201:0x055d, B:202:0x0567, B:205:0x0575, B:207:0x057d, B:208:0x0583, B:210:0x0589, B:212:0x0593, B:214:0x0597, B:215:0x059a, B:216:0x059d, B:217:0x05a3, B:219:0x05a9, B:222:0x05cc, B:223:0x05d9, B:225:0x05df, B:228:0x05eb, B:231:0x05ff, B:233:0x0606, B:237:0x0612, B:239:0x061a, B:242:0x0631, B:244:0x0637, B:248:0x064f, B:253:0x065a, B:255:0x0661, B:257:0x0671, B:259:0x0679, B:260:0x0686, B:261:0x06ae, B:263:0x06b5, B:266:0x06c5), top: B:270:0x0014 }] */
    /* JADX WARN: Removed duplicated region for block: B:202:0x0567 A[Catch: Exception -> 0x06c9, LOOP:6: B:164:0x03f2->B:202:0x0567, LOOP_END, TryCatch #0 {Exception -> 0x06c9, blocks: (B:3:0x0014, B:6:0x0023, B:8:0x004d, B:15:0x005d, B:17:0x0065, B:18:0x0069, B:20:0x007f, B:21:0x009d, B:22:0x00b2, B:24:0x00b8, B:26:0x00d6, B:35:0x00e9, B:37:0x00f5, B:41:0x0103, B:43:0x010e, B:48:0x011d, B:50:0x012b, B:52:0x0137, B:54:0x0143, B:56:0x0149, B:58:0x014f, B:62:0x016f, B:64:0x0177, B:66:0x017f, B:68:0x0190, B:70:0x01a1, B:71:0x01bc, B:74:0x01ca, B:75:0x01e6, B:77:0x01ec, B:80:0x0200, B:82:0x0207, B:86:0x0215, B:88:0x021f, B:91:0x0236, B:93:0x023c, B:97:0x0254, B:103:0x0262, B:105:0x0269, B:107:0x0283, B:109:0x028b, B:110:0x0296, B:111:0x02bd, B:112:0x02c2, B:115:0x02d9, B:117:0x02e5, B:119:0x02eb, B:120:0x0307, B:122:0x030d, B:127:0x0324, B:129:0x032c, B:132:0x0343, B:134:0x0349, B:137:0x035f, B:138:0x0362, B:140:0x0369, B:142:0x0376, B:144:0x037a, B:146:0x0380, B:148:0x0386, B:149:0x039e, B:150:0x03a1, B:152:0x03a7, B:153:0x03c3, B:155:0x03c9, B:159:0x03df, B:161:0x03e8, B:165:0x03f4, B:167:0x03fc, B:170:0x0413, B:172:0x0419, B:176:0x0431, B:181:0x043c, B:183:0x0443, B:185:0x0451, B:187:0x0458, B:191:0x046a, B:193:0x04f5, B:194:0x04f7, B:196:0x0503, B:199:0x050d, B:200:0x0536, B:201:0x055d, B:202:0x0567, B:205:0x0575, B:207:0x057d, B:208:0x0583, B:210:0x0589, B:212:0x0593, B:214:0x0597, B:215:0x059a, B:216:0x059d, B:217:0x05a3, B:219:0x05a9, B:222:0x05cc, B:223:0x05d9, B:225:0x05df, B:228:0x05eb, B:231:0x05ff, B:233:0x0606, B:237:0x0612, B:239:0x061a, B:242:0x0631, B:244:0x0637, B:248:0x064f, B:253:0x065a, B:255:0x0661, B:257:0x0671, B:259:0x0679, B:260:0x0686, B:261:0x06ae, B:263:0x06b5, B:266:0x06c5), top: B:270:0x0014 }] */
    /* JADX WARN: Removed duplicated region for block: B:263:0x06b5 A[Catch: Exception -> 0x06c9, LOOP:10: B:236:0x0610->B:263:0x06b5, LOOP_END, TryCatch #0 {Exception -> 0x06c9, blocks: (B:3:0x0014, B:6:0x0023, B:8:0x004d, B:15:0x005d, B:17:0x0065, B:18:0x0069, B:20:0x007f, B:21:0x009d, B:22:0x00b2, B:24:0x00b8, B:26:0x00d6, B:35:0x00e9, B:37:0x00f5, B:41:0x0103, B:43:0x010e, B:48:0x011d, B:50:0x012b, B:52:0x0137, B:54:0x0143, B:56:0x0149, B:58:0x014f, B:62:0x016f, B:64:0x0177, B:66:0x017f, B:68:0x0190, B:70:0x01a1, B:71:0x01bc, B:74:0x01ca, B:75:0x01e6, B:77:0x01ec, B:80:0x0200, B:82:0x0207, B:86:0x0215, B:88:0x021f, B:91:0x0236, B:93:0x023c, B:97:0x0254, B:103:0x0262, B:105:0x0269, B:107:0x0283, B:109:0x028b, B:110:0x0296, B:111:0x02bd, B:112:0x02c2, B:115:0x02d9, B:117:0x02e5, B:119:0x02eb, B:120:0x0307, B:122:0x030d, B:127:0x0324, B:129:0x032c, B:132:0x0343, B:134:0x0349, B:137:0x035f, B:138:0x0362, B:140:0x0369, B:142:0x0376, B:144:0x037a, B:146:0x0380, B:148:0x0386, B:149:0x039e, B:150:0x03a1, B:152:0x03a7, B:153:0x03c3, B:155:0x03c9, B:159:0x03df, B:161:0x03e8, B:165:0x03f4, B:167:0x03fc, B:170:0x0413, B:172:0x0419, B:176:0x0431, B:181:0x043c, B:183:0x0443, B:185:0x0451, B:187:0x0458, B:191:0x046a, B:193:0x04f5, B:194:0x04f7, B:196:0x0503, B:199:0x050d, B:200:0x0536, B:201:0x055d, B:202:0x0567, B:205:0x0575, B:207:0x057d, B:208:0x0583, B:210:0x0589, B:212:0x0593, B:214:0x0597, B:215:0x059a, B:216:0x059d, B:217:0x05a3, B:219:0x05a9, B:222:0x05cc, B:223:0x05d9, B:225:0x05df, B:228:0x05eb, B:231:0x05ff, B:233:0x0606, B:237:0x0612, B:239:0x061a, B:242:0x0631, B:244:0x0637, B:248:0x064f, B:253:0x065a, B:255:0x0661, B:257:0x0671, B:259:0x0679, B:260:0x0686, B:261:0x06ae, B:263:0x06b5, B:266:0x06c5), top: B:270:0x0014 }] */
    /* JADX WARN: Removed duplicated region for block: B:286:0x0262 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:311:0x043c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:324:0x065a A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void localSearch(int r26, java.lang.String r27, java.util.ArrayList<java.lang.Object> r28, java.util.ArrayList<java.lang.CharSequence> r29, java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r30, int r31) {
        /*
            Method dump skipped, instructions count: 1742
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.localSearch(int, java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    public static /* synthetic */ int lambda$localSearch$198(DialogsSearchAdapter.DialogSearchResult dialogSearchResult, DialogsSearchAdapter.DialogSearchResult dialogSearchResult2) {
        int i = dialogSearchResult.date;
        int i2 = dialogSearchResult2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public ArrayList<Integer> getCachedMessagesInRange(long j, int i, int i2) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages_v2 WHERE uid = %d AND date >= %d AND date <= %d", Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2)), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    arrayList.add(Integer.valueOf(queryFinalized.intValue(0)));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return arrayList;
    }

    public void updateUnreadReactionsCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateUnreadReactionsCount$199(i, j);
            }
        });
    }

    public /* synthetic */ void lambda$updateUnreadReactionsCount$199(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET unread_reactions = ? WHERE did = ?");
            executeFast.bindInteger(1, Math.max(i, 0));
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
            if (i != 0) {
                return;
            }
            SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE reaction_mentions SET state = 0 WHERE dialog_id = ?");
            executeFast2.bindLong(1, j);
            executeFast2.step();
            executeFast2.dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void markMessageReactionsAsRead(final long j, final int i, boolean z) {
        if (z) {
            getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda69
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$markMessageReactionsAsRead$200(j, i);
                }
            });
        } else {
            lambda$markMessageReactionsAsRead$200(j, i);
        }
    }

    /* renamed from: markMessageReactionsAsReadInternal */
    public void lambda$markMessageReactionsAsRead$200(long j, int i) {
        NativeByteBuffer byteBufferValue;
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("UPDATE reaction_mentions SET state = 0 WHERE message_id = ? AND dialog_id = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE uid = %d AND mid = %d", Long.valueOf(j), Integer.valueOf(i)), new Object[0]);
            TLRPC$Message tLRPC$Message = null;
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$Message = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                tLRPC$Message.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                byteBufferValue.reuse();
                TLRPC$TL_messageReactions tLRPC$TL_messageReactions = tLRPC$Message.reactions;
                if (tLRPC$TL_messageReactions != null && tLRPC$TL_messageReactions.recent_reactions != null) {
                    for (int i2 = 0; i2 < tLRPC$Message.reactions.recent_reactions.size(); i2++) {
                        tLRPC$Message.reactions.recent_reactions.get(i2).unread = false;
                    }
                }
            }
            queryFinalized.dispose();
            if (tLRPC$Message == null) {
                return;
            }
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "UPDATE messages_v2 SET data = ? WHERE uid = %d AND mid = %d", Long.valueOf(j), Integer.valueOf(i)));
            try {
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                tLRPC$Message.serializeToStream(nativeByteBuffer);
                executeFast2.bindByteBuffer(1, nativeByteBuffer);
                executeFast2.step();
                executeFast2.dispose();
                nativeByteBuffer.reuse();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (SQLiteException e2) {
            FileLog.e(e2);
        }
    }

    public void updateDialogUnreadReactions(final long j, final int i, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda186
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateDialogUnreadReactions$201(z, j, i);
            }
        });
    }

    public /* synthetic */ void lambda$updateDialogUnreadReactions$201(boolean z, long j, int i) {
        int i2 = 0;
        if (z) {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT unread_reactions FROM dialogs WHERE did = " + j, new Object[0]);
                if (queryFinalized.next()) {
                    i2 = Math.max(0, queryFinalized.intValue(0));
                }
                queryFinalized.dispose();
            } catch (SQLiteException e) {
                e.printStackTrace();
                return;
            }
        }
        SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("UPDATE dialogs SET unread_reactions = ? WHERE did = ?");
        executeFast.bindInteger(1, i2 + i);
        executeFast.bindLong(2, j);
        executeFast.step();
        executeFast.dispose();
    }
}
