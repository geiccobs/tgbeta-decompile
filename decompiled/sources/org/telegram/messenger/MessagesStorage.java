package org.telegram.messenger;

import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
/* loaded from: classes4.dex */
public class MessagesStorage extends BaseController {
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
    private static volatile MessagesStorage[] Instance = new MessagesStorage[4];
    private static final Object[] lockObjects = new Object[4];
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

    /* loaded from: classes4.dex */
    public interface BooleanCallback {
        void run(boolean z);
    }

    /* loaded from: classes4.dex */
    public interface IntCallback {
        void run(int i);
    }

    /* loaded from: classes4.dex */
    public interface LongCallback {
        void run(long j);
    }

    /* loaded from: classes4.dex */
    public interface StringCallback {
        void run(String str);
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    public static MessagesStorage getInstance(int num) {
        MessagesStorage localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (lockObjects[num]) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    MessagesStorage[] messagesStorageArr = Instance;
                    MessagesStorage messagesStorage = new MessagesStorage(num);
                    localInstance = messagesStorage;
                    messagesStorageArr[num] = messagesStorage;
                }
            }
        }
        return localInstance;
    }

    private void ensureOpened() {
        try {
            this.openSync.await();
        } catch (Throwable th) {
        }
    }

    public int getLastDateValue() {
        ensureOpened();
        return this.lastDateValue;
    }

    public void setLastDateValue(int value) {
        ensureOpened();
        this.lastDateValue = value;
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

    public void setLastPtsValue(int value) {
        ensureOpened();
        this.lastPtsValue = value;
    }

    public int getLastQtsValue() {
        ensureOpened();
        return this.lastQtsValue;
    }

    public void setLastQtsValue(int value) {
        ensureOpened();
        this.lastQtsValue = value;
    }

    public int getLastSeqValue() {
        ensureOpened();
        return this.lastSeqValue;
    }

    public void setLastSeqValue(int value) {
        ensureOpened();
        this.lastSeqValue = value;
    }

    public int getLastSecretVersion() {
        ensureOpened();
        return this.lastSecretVersion;
    }

    public void setLastSecretVersion(int value) {
        ensureOpened();
        this.lastSecretVersion = value;
    }

    public byte[] getSecretPBytes() {
        ensureOpened();
        return this.secretPBytes;
    }

    public void setSecretPBytes(byte[] value) {
        ensureOpened();
        this.secretPBytes = value;
    }

    public int getSecretG() {
        ensureOpened();
        return this.secretG;
    }

    public void setSecretG(int value) {
        ensureOpened();
        this.secretG = value;
    }

    public MessagesStorage(int instance) {
        super(instance);
        DispatchQueue dispatchQueue = new DispatchQueue("storageQueue_" + instance);
        this.storageQueue = dispatchQueue;
        dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda77
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1002lambda$new$0$orgtelegrammessengerMessagesStorage();
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1002lambda$new$0$orgtelegrammessengerMessagesStorage() {
        openDatabase(1);
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public void bindTaskToGuid(Runnable task, int guid) {
        ArrayList<Runnable> arrayList = this.tasks.get(guid);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.tasks.put(guid, arrayList);
        }
        arrayList.add(task);
    }

    public void cancelTasksForGuid(int guid) {
        ArrayList<Runnable> arrayList = this.tasks.get(guid);
        if (arrayList == null) {
            return;
        }
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            this.storageQueue.cancelRunnable(arrayList.get(a));
        }
        this.tasks.remove(guid);
    }

    public void completeTaskForGuid(Runnable runnable, int guid) {
        ArrayList<Runnable> arrayList = this.tasks.get(guid);
        if (arrayList == null) {
            return;
        }
        arrayList.remove(runnable);
        if (arrayList.isEmpty()) {
            this.tasks.remove(guid);
        }
    }

    public long getDatabaseSize() {
        long size = 0;
        File file = this.cacheFile;
        if (file != null) {
            size = 0 + file.length();
        }
        File file2 = this.shmCacheFile;
        if (file2 != null) {
            return size + file2.length();
        }
        return size;
    }

    public void openDatabase(int openTries) {
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            filesDir = new File(filesDir, "account" + this.currentAccount + "/");
            filesDir.mkdirs();
        }
        this.cacheFile = new File(filesDir, "cache4.db");
        this.walCacheFile = new File(filesDir, "cache4.db-wal");
        this.shmCacheFile = new File(filesDir, "cache4.db-shm");
        boolean createTable = false;
        if (!this.cacheFile.exists()) {
            createTable = true;
        }
        int i = 3;
        try {
            SQLiteDatabase sQLiteDatabase = new SQLiteDatabase(this.cacheFile.getPath());
            this.database = sQLiteDatabase;
            sQLiteDatabase.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_size_limit = 10485760").stepThis().dispose();
            if (createTable) {
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
                int version = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current db version = " + version);
                }
                if (version == 0) {
                    throw new Exception("malformed");
                }
                try {
                    SQLiteCursor cursor = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
                    if (cursor.next()) {
                        this.lastSeqValue = cursor.intValue(0);
                        this.lastPtsValue = cursor.intValue(1);
                        this.lastDateValue = cursor.intValue(2);
                        this.lastQtsValue = cursor.intValue(3);
                        this.lastSecretVersion = cursor.intValue(4);
                        this.secretG = cursor.intValue(5);
                        if (cursor.isNull(6)) {
                            this.secretPBytes = null;
                        } else {
                            byte[] byteArrayValue = cursor.byteArrayValue(6);
                            this.secretPBytes = byteArrayValue;
                            if (byteArrayValue != null && byteArrayValue.length == 1) {
                                this.secretPBytes = null;
                            }
                        }
                    }
                    cursor.dispose();
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
                if (version < LAST_DB_VERSION) {
                    try {
                        updateDbToLastVersion(version);
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
            if (openTries < 3 && e4.getMessage() != null && e4.getMessage().contains("malformed")) {
                if (openTries == 2) {
                    cleanupInternal(true);
                    for (int a = 0; a < 2; a++) {
                        getUserConfig().setDialogsLoadOffset(a, 0, 0, 0L, 0L, 0L, 0L);
                        getUserConfig().setTotalDialogsCount(a, 0);
                    }
                    getUserConfig().saveConfig(false);
                } else {
                    cleanupInternal(false);
                }
                if (openTries == 1) {
                    i = 2;
                }
                openDatabase(i);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda88
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1004lambda$openDatabase$1$orgtelegrammessengerMessagesStorage();
            }
        });
        loadDialogFilters();
        loadUnreadMessages();
        loadPendingTasks();
        try {
            this.openSync.countDown();
        } catch (Throwable th) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda99
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1005lambda$openDatabase$2$orgtelegrammessengerMessagesStorage();
            }
        });
    }

    /* renamed from: lambda$openDatabase$1$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1004lambda$openDatabase$1$orgtelegrammessengerMessagesStorage() {
        if (this.databaseMigrationInProgress) {
            this.databaseMigrationInProgress = false;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, false);
        }
    }

    /* renamed from: lambda$openDatabase$2$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1005lambda$openDatabase$2$orgtelegrammessengerMessagesStorage() {
        this.showClearDatabaseAlert = false;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseOpened, new Object[0]);
    }

    public boolean isDatabaseMigrationInProgress() {
        return this.databaseMigrationInProgress;
    }

    private void updateDbToLastVersion(int currentVersion) throws Exception {
        int version;
        MessagesStorage messagesStorage;
        SQLiteCursor cursor;
        SQLiteCursor cursor2;
        int channelId;
        int sendState;
        NativeByteBuffer repliesdata;
        SQLiteCursor cursor3;
        SQLiteCursor cursor4;
        SQLiteCursor cursor5;
        SQLiteCursor cursor6;
        SQLiteCursor cursor7;
        NativeByteBuffer data;
        SQLiteCursor cursor8;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda115
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1061x9f8670a4();
            }
        });
        int version2 = currentVersion;
        FileLog.d("MessagesStorage start db migration from " + version2 + " to " + LAST_DB_VERSION);
        int i = 4;
        if (version2 < 4) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
            this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
            fixNotificationSettings();
            this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
            version2 = 4;
        }
        int i2 = 2;
        int i3 = 0;
        int i4 = 1;
        if (version2 == 4) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            this.database.beginTransaction();
            SQLiteCursor cursor9 = this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (cursor9.next()) {
                int date = cursor9.intValue(0);
                NativeByteBuffer data2 = cursor9.byteBufferValue(1);
                if (data2 != null) {
                    int length = data2.limit();
                    for (int a = 0; a < length / 4; a++) {
                        state.requery();
                        state.bindInteger(1, data2.readInt32(false));
                        state.bindInteger(2, date);
                        state.step();
                    }
                    data2.reuse();
                }
            }
            state.dispose();
            cursor9.dispose();
            this.database.commitTransaction();
            this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            version2 = 6;
        }
        if (version2 == 6) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            version2 = 7;
        }
        if (version2 == 7 || version2 == 8 || version2 == 9) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
            version2 = 10;
        }
        if (version2 == 10) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
            version2 = 11;
        }
        if (version2 == 11 || version2 == 12) {
            this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
            version2 = 13;
        }
        if (version2 == 13) {
            this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            version2 = 14;
        }
        if (version2 == 14) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            version2 = 15;
        }
        if (version2 == 15) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            version2 = 16;
        }
        if (version2 == 16) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            version2 = 17;
        }
        if (version2 == 17) {
            this.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            version2 = 18;
        }
        if (version2 == 18) {
            this.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            version2 = 19;
        }
        if (version2 == 19) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            version2 = 20;
        }
        if (version2 == 20) {
            this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            version2 = 21;
        }
        if (version2 == 21) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            SQLiteCursor cursor10 = this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (cursor10.next()) {
                long chatId = cursor10.intValue(0);
                NativeByteBuffer data3 = cursor10.byteBufferValue(1);
                if (data3 != null) {
                    TLRPC.ChatParticipants participants = TLRPC.ChatParticipants.TLdeserialize(data3, data3.readInt32(false), false);
                    data3.reuse();
                    if (participants != null) {
                        TLRPC.TL_chatFull chatFull = new TLRPC.TL_chatFull();
                        chatFull.id = chatId;
                        chatFull.chat_photo = new TLRPC.TL_photoEmpty();
                        chatFull.notify_settings = new TLRPC.TL_peerNotifySettingsEmpty_layer77();
                        chatFull.exported_invite = null;
                        chatFull.participants = participants;
                        NativeByteBuffer data22 = new NativeByteBuffer(chatFull.getObjectSize());
                        chatFull.serializeToStream(data22);
                        state2.requery();
                        state2.bindLong(1, chatId);
                        state2.bindByteBuffer(2, data22);
                        state2.step();
                        data22.reuse();
                    }
                }
            }
            state2.dispose();
            cursor10.dispose();
            this.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
            version2 = 22;
        }
        if (version2 == 22) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            version2 = 23;
        }
        if (version2 == 23 || version2 == 24) {
            this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
            version2 = 25;
        }
        if (version2 == 25 || version2 == 26) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
            version2 = 27;
        }
        if (version2 == 27) {
            this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
            version2 = 28;
        }
        if (version2 == 28 || version2 == 29) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
            version2 = 30;
        }
        if (version2 == 30) {
            this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
            version2 = 31;
        }
        if (version2 == 31) {
            this.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            version2 = 32;
        }
        if (version2 == 32) {
            this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
            version2 = 33;
        }
        if (version2 == 33) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
            version2 = 34;
        }
        if (version2 == 34) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
            version2 = 35;
        }
        if (version2 == 35) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            version2 = 36;
        }
        if (version2 == 36) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            version2 = 37;
        }
        if (version2 == 37) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
            version2 = 38;
        }
        if (version2 == 38) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
            version2 = 39;
        }
        if (version2 == 39) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
            version2 = 40;
        }
        if (version2 == 40) {
            fixNotificationSettings();
            this.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            version2 = 41;
        }
        if (version2 == 41) {
            this.database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
            version2 = 42;
        }
        if (version2 == 42) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
            version2 = 43;
        }
        if (version2 == 43) {
            this.database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
            version2 = 44;
        }
        if (version2 == 44) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
            version2 = 45;
        }
        if (version2 == 45) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
            version2 = 46;
        }
        if (version2 == 46) {
            this.database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
            version2 = 47;
        }
        if (version2 == 47) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 48").stepThis().dispose();
            version2 = 48;
        }
        if (version2 == 48) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 49").stepThis().dispose();
            version2 = 49;
        }
        if (version2 == 49) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 50").stepThis().dispose();
            version2 = 50;
        }
        if (version2 == 50) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("ALTER TABLE sent_files_v2 ADD COLUMN parent TEXT").stepThis().dispose();
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("ALTER TABLE download_queue ADD COLUMN parent TEXT").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 51").stepThis().dispose();
            version2 = 51;
        }
        if (version2 == 51) {
            this.database.executeFast("ALTER TABLE media_counts_v2 ADD COLUMN old INTEGER").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 52").stepThis().dispose();
            version2 = 52;
        }
        if (version2 == 52) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id ON polls_v2(id);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 53").stepThis().dispose();
            version2 = 53;
        }
        if (version2 == 53) {
            this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN online INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 54").stepThis().dispose();
            version2 = 54;
        }
        if (version2 == 54) {
            this.database.executeFast("DROP TABLE IF EXISTS wallpapers;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 55").stepThis().dispose();
            version2 = 55;
        }
        if (version2 == 55) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 56").stepThis().dispose();
            version2 = 56;
        }
        if (version2 == 56 || version2 == 57) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 58").stepThis().dispose();
            version2 = 58;
        }
        if (version2 == 58) {
            this.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE emoji_keywords_info_v2 ADD COLUMN date INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 59").stepThis().dispose();
            version2 = 59;
        }
        if (version2 == 59) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN folder_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN data BLOB default NULL").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 60").stepThis().dispose();
            version2 = 60;
        }
        if (version2 == 60) {
            this.database.executeFast("DROP TABLE IF EXISTS channel_admins;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS blocked_users;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 61").stepThis().dispose();
            version2 = 61;
        }
        if (version2 == 61) {
            this.database.executeFast("DROP INDEX IF EXISTS send_state_idx_messages;").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages2 ON messages(mid, send_state, date);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 62").stepThis().dispose();
            version2 = 62;
        }
        if (version2 == 62) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages(mid INTEGER PRIMARY KEY, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages ON scheduled_messages(mid, send_state, date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages ON scheduled_messages(uid, date);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 63").stepThis().dispose();
            version2 = 63;
        }
        if (version2 == 63) {
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 64").stepThis().dispose();
            version2 = 64;
        }
        if (version2 == 64) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_filter(id INTEGER PRIMARY KEY, ord INTEGER, unread_count INTEGER, flags INTEGER, title TEXT)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_filter_ep(id INTEGER, peer INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 65").stepThis().dispose();
            version2 = 65;
        }
        if (version2 == 65) {
            this.database.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON dialogs(flags);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 66").stepThis().dispose();
            version2 = 66;
        }
        if (version2 == 66) {
            this.database.executeFast("CREATE TABLE dialog_filter_pin_v2(id INTEGER, peer INTEGER, pin INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 67").stepThis().dispose();
            version2 = 67;
        }
        if (version2 == 67) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_dice(emoji TEXT PRIMARY KEY, data BLOB, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 68").stepThis().dispose();
            version2 = 68;
        }
        if (version2 == 68) {
            executeNoException("ALTER TABLE messages ADD COLUMN forwards INTEGER default 0");
            this.database.executeFast("PRAGMA user_version = 69").stepThis().dispose();
            version2 = 69;
        }
        if (version2 == 69) {
            executeNoException("ALTER TABLE messages ADD COLUMN replies_data BLOB default NULL");
            executeNoException("ALTER TABLE messages ADD COLUMN thread_reply_id INTEGER default 0");
            this.database.executeFast("PRAGMA user_version = 70").stepThis().dispose();
            version2 = 70;
        }
        if (version2 == 70) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned_v2(uid INTEGER, mid INTEGER, data BLOB, PRIMARY KEY (uid, mid));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 71").stepThis().dispose();
            version2 = 71;
        }
        if (version2 == 71) {
            executeNoException("ALTER TABLE sharing_locations ADD COLUMN proximity INTEGER default 0");
            this.database.executeFast("PRAGMA user_version = 72").stepThis().dispose();
            version2 = 72;
        }
        if (version2 == 72) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned_count(uid INTEGER PRIMARY KEY, count INTEGER, end INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 73").stepThis().dispose();
            version2 = 73;
        }
        if (version2 == 73) {
            executeNoException("ALTER TABLE chat_settings_v2 ADD COLUMN inviter INTEGER default 0");
            this.database.executeFast("PRAGMA user_version = 74").stepThis().dispose();
            version2 = 74;
        }
        if (version2 == 74) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS shortcut_widget(id INTEGER, did INTEGER, ord INTEGER, PRIMARY KEY (id, did));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS shortcut_widget_did ON shortcut_widget(did);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 75").stepThis().dispose();
            version2 = 75;
        }
        if (version2 == 75) {
            executeNoException("ALTER TABLE chat_settings_v2 ADD COLUMN links INTEGER default 0");
            this.database.executeFast("PRAGMA user_version = 76").stepThis().dispose();
            version2 = 76;
        }
        if (version2 == 76) {
            executeNoException("ALTER TABLE enc_tasks_v2 ADD COLUMN media INTEGER default -1");
            this.database.executeFast("PRAGMA user_version = 77").stepThis().dispose();
            version2 = 77;
        }
        if (version2 == 77) {
            this.database.executeFast("DROP TABLE IF EXISTS channel_admins_v2;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins_v3(did INTEGER, uid INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 78").stepThis().dispose();
            version2 = 78;
        }
        if (version2 == 78) {
            this.database.executeFast("DROP TABLE IF EXISTS bot_info;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_info_v2(uid INTEGER, dialogId INTEGER, info BLOB, PRIMARY KEY(uid, dialogId))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 79").stepThis().dispose();
            version2 = 79;
        }
        int i5 = 3;
        if (version2 != 79) {
            version = version2;
        } else {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v3(mid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, media))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v3 ON enc_tasks_v3(date);").stepThis().dispose();
            this.database.beginTransaction();
            SQLiteCursor cursor11 = this.database.queryFinalized("SELECT mid, date, media FROM enc_tasks_v2 WHERE 1", new Object[0]);
            SQLitePreparedStatement state3 = this.database.executeFast("REPLACE INTO enc_tasks_v3 VALUES(?, ?, ?)");
            if (cursor11.next()) {
                long mid = cursor11.longValue(0);
                int date2 = cursor11.intValue(1);
                int media = cursor11.intValue(2);
                state3.requery();
                state3.bindLong(1, mid);
                state3.bindInteger(2, date2);
                state3.bindInteger(3, media);
                state3.step();
            }
            state3.dispose();
            cursor11.dispose();
            this.database.commitTransaction();
            this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks_v2;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS enc_tasks_v2;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 80").stepThis().dispose();
            version = 80;
        }
        int i6 = 5;
        if (version == 80) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages_v2(mid INTEGER, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, send_state, date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages_v2 ON scheduled_messages_v2(uid, date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid_v2 ON bot_keyboard(mid, uid);").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS bot_keyboard_idx_mid;").stepThis().dispose();
            this.database.beginTransaction();
            try {
                cursor8 = this.database.queryFinalized("SELECT mid, uid, send_state, date, data, ttl, replydata FROM scheduled_messages_v2 WHERE 1", new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
                cursor8 = null;
            }
            if (cursor8 != null) {
                SQLitePreparedStatement statement = this.database.executeFast("REPLACE INTO scheduled_messages_v2 VALUES(?, ?, ?, ?, ?, ?, ?)");
                while (cursor8.next()) {
                    NativeByteBuffer data4 = cursor8.byteBufferValue(i);
                    if (data4 != null) {
                        int mid2 = cursor8.intValue(i3);
                        int version3 = version;
                        long uid = cursor8.longValue(1);
                        int sendState2 = cursor8.intValue(2);
                        int date3 = cursor8.intValue(i5);
                        int ttl = cursor8.intValue(i6);
                        NativeByteBuffer replydata = cursor8.byteBufferValue(6);
                        statement.requery();
                        statement.bindInteger(1, mid2);
                        statement.bindLong(2, uid);
                        statement.bindInteger(i5, sendState2);
                        statement.bindByteBuffer(4, data4);
                        statement.bindInteger(5, date3);
                        statement.bindInteger(6, ttl);
                        if (replydata != null) {
                            statement.bindByteBuffer(7, replydata);
                        } else {
                            statement.bindNull(7);
                        }
                        statement.step();
                        if (replydata != null) {
                            replydata.reuse();
                        }
                        data4.reuse();
                        version = version3;
                        i = 4;
                        i3 = 0;
                        i5 = 3;
                        i6 = 5;
                    }
                }
                cursor8.dispose();
                statement.dispose();
            }
            this.database.executeFast("DROP INDEX IF EXISTS send_state_idx_scheduled_messages;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS uid_date_idx_scheduled_messages;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS scheduled_messages;").stepThis().dispose();
            this.database.commitTransaction();
            this.database.executeFast("PRAGMA user_version = 81").stepThis().dispose();
            version = 81;
        }
        if (version == 81) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_v3(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v3 ON media_v3(uid, mid, type, date);").stepThis().dispose();
            this.database.beginTransaction();
            try {
                cursor7 = this.database.queryFinalized("SELECT mid, uid, date, type, data FROM media_v2 WHERE 1", new Object[0]);
            } catch (Exception e2) {
                FileLog.e(e2);
                cursor7 = null;
            }
            if (cursor7 != null) {
                SQLitePreparedStatement statement2 = this.database.executeFast("REPLACE INTO media_v3 VALUES(?, ?, ?, ?, ?)");
                while (cursor7.next()) {
                    NativeByteBuffer data5 = cursor7.byteBufferValue(4);
                    if (data5 != null) {
                        int mid3 = cursor7.intValue(0);
                        long uid2 = cursor7.longValue(1);
                        if (((int) uid2) != 0) {
                            data = data5;
                        } else {
                            data = data5;
                            uid2 = DialogObject.makeEncryptedDialogId((int) (uid2 >> 32));
                        }
                        int date4 = cursor7.intValue(2);
                        int type = cursor7.intValue(3);
                        statement2.requery();
                        statement2.bindInteger(1, mid3);
                        statement2.bindLong(2, uid2);
                        statement2.bindInteger(3, date4);
                        statement2.bindInteger(4, type);
                        NativeByteBuffer data6 = data;
                        statement2.bindByteBuffer(5, data6);
                        statement2.step();
                        data6.reuse();
                    }
                }
                cursor7.dispose();
                statement2.dispose();
            }
            this.database.executeFast("DROP INDEX IF EXISTS uid_mid_type_date_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS media_v2;").stepThis().dispose();
            this.database.commitTransaction();
            this.database.executeFast("PRAGMA user_version = 82").stepThis().dispose();
            version = 82;
        }
        if (version == 82) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS randoms_v2(random_id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (random_id, mid, uid))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms_v2 ON randoms_v2(mid, uid);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v4(mid INTEGER, uid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, uid, media))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v4 ON enc_tasks_v4(date);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id_v2 ON polls_v2(id);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending_v2(id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (id, mid, uid));").stepThis().dispose();
            this.database.beginTransaction();
            try {
                cursor3 = this.database.queryFinalized("SELECT r.random_id, r.mid, m.uid FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e3) {
                cursor3 = null;
                FileLog.e(e3);
            }
            if (cursor3 != null) {
                SQLitePreparedStatement statement3 = this.database.executeFast("REPLACE INTO randoms_v2 VALUES(?, ?, ?)");
                while (cursor3.next()) {
                    long randomId = cursor3.longValue(0);
                    int mid4 = cursor3.intValue(1);
                    long uid3 = cursor3.longValue(2);
                    if (((int) uid3) == 0) {
                        uid3 = DialogObject.makeEncryptedDialogId((int) (uid3 >> 32));
                    }
                    statement3.requery();
                    statement3.bindLong(1, randomId);
                    statement3.bindInteger(2, mid4);
                    statement3.bindLong(3, uid3);
                    statement3.step();
                }
                cursor3.dispose();
                statement3.dispose();
            }
            try {
                cursor4 = this.database.queryFinalized("SELECT p.mid, m.uid, p.id FROM polls as p INNER JOIN messages as m ON p.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e4) {
                cursor4 = null;
                FileLog.e(e4);
            }
            if (cursor4 != null) {
                SQLitePreparedStatement statement4 = this.database.executeFast("REPLACE INTO polls_v2 VALUES(?, ?, ?)");
                while (cursor4.next()) {
                    int mid5 = cursor4.intValue(0);
                    long uid4 = cursor4.longValue(1);
                    long id = cursor4.longValue(2);
                    if (((int) uid4) == 0) {
                        uid4 = DialogObject.makeEncryptedDialogId((int) (uid4 >> 32));
                    }
                    statement4.requery();
                    statement4.bindInteger(1, mid5);
                    statement4.bindLong(2, uid4);
                    statement4.bindLong(3, id);
                    statement4.step();
                }
                cursor4.dispose();
                statement4.dispose();
            }
            try {
                cursor5 = this.database.queryFinalized("SELECT wp.id, wp.mid, m.uid FROM webpage_pending as wp INNER JOIN messages as m ON wp.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e5) {
                cursor5 = null;
                FileLog.e(e5);
            }
            if (cursor5 != null) {
                SQLitePreparedStatement statement5 = this.database.executeFast("REPLACE INTO webpage_pending_v2 VALUES(?, ?, ?)");
                while (cursor5.next()) {
                    long id2 = cursor5.longValue(0);
                    int mid6 = cursor5.intValue(1);
                    long uid5 = cursor5.longValue(2);
                    if (((int) uid5) == 0) {
                        uid5 = DialogObject.makeEncryptedDialogId((int) (uid5 >> 32));
                    }
                    statement5.requery();
                    statement5.bindLong(1, id2);
                    statement5.bindInteger(2, mid6);
                    statement5.bindLong(3, uid5);
                    statement5.step();
                }
                cursor5.dispose();
                statement5.dispose();
            }
            try {
                cursor6 = this.database.queryFinalized("SELECT et.mid, m.uid, et.date, et.media FROM enc_tasks_v3 as et INNER JOIN messages as m ON et.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e6) {
                FileLog.e(e6);
                cursor6 = null;
            }
            if (cursor6 != null) {
                SQLitePreparedStatement statement6 = this.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
                while (cursor6.next()) {
                    int mid7 = cursor6.intValue(0);
                    long uid6 = cursor6.longValue(1);
                    int date5 = cursor6.intValue(2);
                    int media2 = cursor6.intValue(3);
                    if (((int) uid6) == 0) {
                        uid6 = DialogObject.makeEncryptedDialogId((int) (uid6 >> 32));
                    }
                    statement6.requery();
                    statement6.bindInteger(1, mid7);
                    statement6.bindLong(2, uid6);
                    statement6.bindInteger(3, date5);
                    statement6.bindInteger(4, media2);
                    statement6.step();
                }
                cursor6.dispose();
                statement6.dispose();
            }
            this.database.executeFast("DROP INDEX IF EXISTS mid_idx_randoms;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS randoms;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks_v3;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS enc_tasks_v3;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS polls_id;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS polls;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS webpage_pending;").stepThis().dispose();
            this.database.commitTransaction();
            this.database.executeFast("PRAGMA user_version = 83").stepThis().dispose();
            version = 83;
        }
        if (version != 83) {
            messagesStorage = this;
        } else {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_v2(mid INTEGER, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_v2 ON messages_v2(uid, mid, read_state, out);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_v2 ON messages_v2(uid, date, mid);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_v2 ON messages_v2(mid, out);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_v2 ON messages_v2(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_v2 ON messages_v2(mid, send_state, date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_v2 ON messages_v2(uid, mention, read_state);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_v2 ON messages_v2(mid, is_channel);").stepThis().dispose();
            this.database.beginTransaction();
            try {
                cursor2 = this.database.queryFinalized("SELECT mid, uid, read_state, send_state, date, data, out, ttl, media, replydata, imp, mention, forwards, replies_data, thread_reply_id FROM messages WHERE 1", new Object[0]);
            } catch (Exception e7) {
                FileLog.e(e7);
                cursor2 = null;
            }
            if (cursor2 != null) {
                SQLitePreparedStatement statement7 = this.database.executeFast("REPLACE INTO messages_v2 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                int num = 0;
                while (cursor2.next()) {
                    NativeByteBuffer data7 = cursor2.byteBufferValue(5);
                    if (data7 != null) {
                        int num2 = num + 1;
                        long mid8 = cursor2.intValue(0);
                        long uid7 = cursor2.longValue(i4);
                        if (((int) uid7) == 0) {
                            uid7 = DialogObject.makeEncryptedDialogId((int) (uid7 >> 32));
                        }
                        int readState = cursor2.intValue(i2);
                        int sendState3 = cursor2.intValue(3);
                        int date6 = cursor2.intValue(4);
                        int out = cursor2.intValue(6);
                        int ttl2 = cursor2.intValue(7);
                        int media3 = cursor2.intValue(8);
                        int version4 = version;
                        NativeByteBuffer replydata2 = cursor2.byteBufferValue(9);
                        int imp = cursor2.intValue(10);
                        int mention = cursor2.intValue(11);
                        int forwards = cursor2.intValue(12);
                        NativeByteBuffer repliesdata2 = cursor2.byteBufferValue(13);
                        int thread_reply_id = cursor2.intValue(14);
                        SQLiteCursor cursor12 = cursor2;
                        int channelId2 = (int) (uid7 >> 32);
                        if (ttl2 >= 0) {
                            channelId = channelId2;
                            sendState = sendState3;
                        } else {
                            TLRPC.Message message = TLRPC.Message.TLdeserialize(data7, data7.readInt32(false), false);
                            if (message == null) {
                                channelId = channelId2;
                                sendState = sendState3;
                            } else {
                                sendState = sendState3;
                                message.readAttachPath(data7, getUserConfig().clientUserId);
                                if (message.params != null) {
                                    channelId = channelId2;
                                } else {
                                    message.params = new HashMap<>();
                                    HashMap<String, String> hashMap = message.params;
                                    StringBuilder sb = new StringBuilder();
                                    channelId = channelId2;
                                    sb.append("");
                                    sb.append(ttl2);
                                    hashMap.put("fwd_peer", sb.toString());
                                }
                                data7.reuse();
                                NativeByteBuffer data8 = new NativeByteBuffer(message.getObjectSize());
                                message.serializeToStream(data8);
                                data7 = data8;
                            }
                            ttl2 = 0;
                        }
                        statement7.requery();
                        statement7.bindInteger(1, (int) mid8);
                        statement7.bindLong(2, uid7);
                        statement7.bindInteger(3, readState);
                        statement7.bindInteger(4, sendState);
                        statement7.bindInteger(5, date6);
                        statement7.bindByteBuffer(6, data7);
                        statement7.bindInteger(7, out);
                        statement7.bindInteger(8, ttl2);
                        statement7.bindInteger(9, media3);
                        if (replydata2 != null) {
                            statement7.bindByteBuffer(10, replydata2);
                        } else {
                            statement7.bindNull(10);
                        }
                        statement7.bindInteger(11, imp);
                        statement7.bindInteger(12, mention);
                        statement7.bindInteger(13, forwards);
                        if (repliesdata2 != null) {
                            repliesdata = repliesdata2;
                            statement7.bindByteBuffer(14, repliesdata);
                        } else {
                            repliesdata = repliesdata2;
                            statement7.bindNull(14);
                        }
                        statement7.bindInteger(15, thread_reply_id);
                        statement7.bindInteger(16, channelId > 0 ? 1 : 0);
                        statement7.step();
                        if (replydata2 != null) {
                            replydata2.reuse();
                        }
                        if (repliesdata != null) {
                            repliesdata.reuse();
                        }
                        data7.reuse();
                        cursor2 = cursor12;
                        num = num2;
                        version = version4;
                        i2 = 2;
                        i4 = 1;
                    }
                }
                cursor2.dispose();
                statement7.dispose();
            }
            ArrayList<Integer> secretChatsToUpdate = null;
            ArrayList<Integer> foldersToUpdate = null;
            messagesStorage = this;
            SQLiteCursor cursor13 = messagesStorage.database.queryFinalized("SELECT did, last_mid, last_mid_i FROM dialogs WHERE 1", new Object[0]);
            SQLitePreparedStatement statement42 = messagesStorage.database.executeFast("UPDATE dialogs SET last_mid = ?, last_mid_i = ? WHERE did = ?");
            while (cursor13.next()) {
                long did = cursor13.longValue(0);
                int lowerId = (int) did;
                int highId = (int) (did >> 32);
                if (lowerId == 0) {
                    if (secretChatsToUpdate == null) {
                        secretChatsToUpdate = new ArrayList<>();
                    }
                    secretChatsToUpdate.add(Integer.valueOf(highId));
                } else if (highId == 2) {
                    if (foldersToUpdate == null) {
                        foldersToUpdate = new ArrayList<>();
                    }
                    foldersToUpdate.add(Integer.valueOf(lowerId));
                }
                statement42.requery();
                statement42.bindInteger(1, cursor13.intValue(1));
                statement42.bindInteger(2, cursor13.intValue(2));
                statement42.bindLong(3, did);
                statement42.step();
            }
            statement42.dispose();
            cursor13.dispose();
            SQLiteCursor cursor14 = messagesStorage.database.queryFinalized("SELECT uid, mid FROM unread_push_messages WHERE 1", new Object[0]);
            SQLitePreparedStatement statement43 = messagesStorage.database.executeFast("UPDATE unread_push_messages SET mid = ? WHERE uid = ? AND mid = ?");
            while (cursor14.next()) {
                long did2 = cursor14.longValue(0);
                int mid9 = cursor14.intValue(1);
                statement43.requery();
                statement43.bindInteger(1, mid9);
                statement43.bindLong(2, did2);
                statement43.bindInteger(3, mid9);
                statement43.step();
            }
            statement43.dispose();
            cursor14.dispose();
            if (secretChatsToUpdate != null) {
                SQLitePreparedStatement statement8 = messagesStorage.database.executeFast("UPDATE dialogs SET did = ? WHERE did = ?");
                SQLitePreparedStatement statement22 = messagesStorage.database.executeFast("UPDATE dialog_filter_pin_v2 SET peer = ? WHERE peer = ?");
                SQLitePreparedStatement statement32 = messagesStorage.database.executeFast("UPDATE dialog_filter_ep SET peer = ? WHERE peer = ?");
                int a2 = 0;
                int N = secretChatsToUpdate.size();
                while (a2 < N) {
                    int sid = secretChatsToUpdate.get(a2).intValue();
                    long newId = DialogObject.makeEncryptedDialogId(sid);
                    SQLiteCursor cursor15 = cursor14;
                    long oldId = sid << 32;
                    statement8.requery();
                    statement8.bindLong(1, newId);
                    statement8.bindLong(2, oldId);
                    statement8.step();
                    statement22.requery();
                    statement22.bindLong(1, newId);
                    statement22.bindLong(2, oldId);
                    statement22.step();
                    statement32.requery();
                    statement32.bindLong(1, newId);
                    statement32.bindLong(2, oldId);
                    statement32.step();
                    a2++;
                    cursor14 = cursor15;
                    statement43 = statement43;
                }
                statement8.dispose();
                statement22.dispose();
                statement32.dispose();
            }
            if (foldersToUpdate != null) {
                SQLitePreparedStatement statement9 = messagesStorage.database.executeFast("UPDATE dialogs SET did = ? WHERE did = ?");
                int N2 = foldersToUpdate.size();
                for (int a3 = 0; a3 < N2; a3++) {
                    int fid = foldersToUpdate.get(a3).intValue();
                    long newId2 = DialogObject.makeFolderDialogId(fid);
                    statement9.requery();
                    statement9.bindLong(1, newId2);
                    statement9.bindLong(2, 8589934592L | fid);
                    statement9.step();
                }
                statement9.dispose();
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
            version = 84;
        }
        if (version == 84) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_v4(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, type))").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                cursor = messagesStorage.database.queryFinalized("SELECT mid, uid, date, type, data FROM media_v3 WHERE 1", new Object[0]);
            } catch (Exception e8) {
                FileLog.e(e8);
                cursor = null;
            }
            if (cursor != null) {
                SQLitePreparedStatement statement10 = messagesStorage.database.executeFast("REPLACE INTO media_v4 VALUES(?, ?, ?, ?, ?)");
                while (cursor.next()) {
                    NativeByteBuffer data9 = cursor.byteBufferValue(4);
                    if (data9 != null) {
                        int mid10 = cursor.intValue(0);
                        long uid8 = cursor.longValue(1);
                        if (((int) uid8) == 0) {
                            uid8 = DialogObject.makeEncryptedDialogId((int) (uid8 >> 32));
                        }
                        int date7 = cursor.intValue(2);
                        int type2 = cursor.intValue(3);
                        statement10.requery();
                        statement10.bindInteger(1, mid10);
                        statement10.bindLong(2, uid8);
                        statement10.bindInteger(3, date7);
                        statement10.bindInteger(4, type2);
                        statement10.bindByteBuffer(5, data9);
                        statement10.step();
                        data9.reuse();
                    }
                }
                cursor.dispose();
                statement10.dispose();
            }
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media_v3;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 85").stepThis().dispose();
            version = 85;
        }
        if (version == 85) {
            messagesStorage.executeNoException("ALTER TABLE messages_v2 ADD COLUMN reply_to_message_id INTEGER default 0");
            messagesStorage.executeNoException("ALTER TABLE scheduled_messages_v2 ADD COLUMN reply_to_message_id INTEGER default 0");
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_v2 ON messages_v2(mid, reply_to_message_id);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, reply_to_message_id);").stepThis().dispose();
            messagesStorage.executeNoException("UPDATE messages_v2 SET replydata = NULL");
            messagesStorage.executeNoException("UPDATE scheduled_messages_v2 SET replydata = NULL");
            messagesStorage.database.executeFast("PRAGMA user_version = 86").stepThis().dispose();
            version = 86;
        }
        if (version == 86) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS reactions(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 87").stepThis().dispose();
            version = 87;
        }
        if (version == 87) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_reactions INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE reaction_mentions(message_id INTEGER PRIMARY KEY, state INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 88").stepThis().dispose();
            version = 88;
        }
        if (version == 88 || version == 89) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS reaction_mentions;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS reaction_mentions(message_id INTEGER, state INTEGER, dialog_id INTEGER, PRIMARY KEY(dialog_id, message_id));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_did ON reaction_mentions(dialog_id);").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_type_date_idx_media_v3").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v4 ON media_v4(uid, mid, type, date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 90").stepThis().dispose();
            version = 90;
        }
        if (version == 90 || version == 91) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS downloading_documents;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE downloading_documents(data BLOB, hash INTEGER, id INTEGER, state INTEGER, date INTEGER, PRIMARY KEY(hash, id));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 92").stepThis().dispose();
            version = 92;
        }
        if (version == 92) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS attach_menu_bots(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 93").stepThis().dispose();
            version = 95;
        }
        if (version == 95 || version == 93) {
            messagesStorage.executeNoException("ALTER TABLE messages_v2 ADD COLUMN custom_params BLOB default NULL");
            messagesStorage.database.executeFast("PRAGMA user_version = 96").stepThis().dispose();
            version = 96;
        }
        if (version == 96) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS premium_promo(data BLOB, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("UPDATE stickers_v2 SET date = 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 97").stepThis().dispose();
            version = 97;
        }
        if (version == 97) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS stickers_featured;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER, premium INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 98").stepThis().dispose();
        }
        FileLog.d("MessagesStorage db migration finished");
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda116
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1062xf6a46183();
            }
        });
    }

    /* renamed from: lambda$updateDbToLastVersion$3$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1061x9f8670a4() {
        this.databaseMigrationInProgress = true;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, true);
    }

    /* renamed from: lambda$updateDbToLastVersion$4$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1062xf6a46183() {
        this.databaseMigrationInProgress = false;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, false);
    }

    private void executeNoException(String query) {
        try {
            this.database.executeFast(query).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void cleanupInternal(boolean deleteFiles) {
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
        if (deleteFiles) {
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
            if (file3 != null) {
                file3.delete();
                this.shmCacheFile = null;
            }
        }
    }

    public void cleanup(final boolean isLogin) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda92
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m907lambda$cleanup$6$orgtelegrammessengerMessagesStorage(isLogin);
            }
        });
    }

    /* renamed from: lambda$cleanup$6$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m907lambda$cleanup$6$orgtelegrammessengerMessagesStorage(boolean isLogin) {
        cleanupInternal(true);
        openDatabase(1);
        if (isLogin) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda157
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m906lambda$cleanup$5$orgtelegrammessengerMessagesStorage();
                }
            });
        }
    }

    /* renamed from: lambda$cleanup$5$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m906lambda$cleanup$5$orgtelegrammessengerMessagesStorage() {
        getMessagesController().getDifference();
    }

    public void saveSecretParams(final int lsv, final int sg, final byte[] pbytes) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda132
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1045lambda$saveSecretParams$7$orgtelegrammessengerMessagesStorage(lsv, sg, pbytes);
            }
        });
    }

    /* renamed from: lambda$saveSecretParams$7$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1045lambda$saveSecretParams$7$orgtelegrammessengerMessagesStorage(int lsv, int sg, byte[] pbytes) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
            int i = 1;
            state.bindInteger(1, lsv);
            state.bindInteger(2, sg);
            if (pbytes != null) {
                i = pbytes.length;
            }
            NativeByteBuffer data = new NativeByteBuffer(i);
            if (pbytes != null) {
                data.writeBytes(pbytes);
            }
            state.bindByteBuffer(3, data);
            state.step();
            state.dispose();
            data.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void fixNotificationSettings() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m933x7b3dd33c();
            }
        });
    }

    /* renamed from: lambda$fixNotificationSettings$8$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m933x7b3dd33c() {
        long flags;
        try {
            LongSparseArray<Long> ids = new LongSparseArray<>();
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            Map<String, ?> values = preferences.getAll();
            for (Map.Entry<String, ?> entry : values.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("notify2_")) {
                    Integer value = (Integer) entry.getValue();
                    if (value.intValue() == 2 || value.intValue() == 3) {
                        String key2 = key.replace("notify2_", "");
                        if (value.intValue() == 2) {
                            flags = 1;
                        } else {
                            Integer time = (Integer) values.get("notifyuntil_" + key2);
                            if (time != null) {
                                flags = (time.intValue() << 32) | 1;
                            } else {
                                flags = 1;
                            }
                        }
                        try {
                            ids.put(Long.parseLong(key2), Long.valueOf(flags));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                this.database.beginTransaction();
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                for (int a = 0; a < ids.size(); a++) {
                    state.requery();
                    state.bindLong(1, ids.keyAt(a));
                    state.bindLong(2, ids.valueAt(a).longValue());
                    state.step();
                }
                state.dispose();
                this.database.commitTransaction();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        } catch (Throwable e3) {
            FileLog.e(e3);
        }
    }

    public long createPendingTask(final NativeByteBuffer data) {
        if (data == null) {
            return 0L;
        }
        final long id = this.lastTaskId.getAndAdd(1L);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m915xf5604f3e(id, data);
            }
        });
        return id;
    }

    /* renamed from: lambda$createPendingTask$9$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m915xf5604f3e(long id, NativeByteBuffer data) {
        try {
            try {
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
                state.bindLong(1, id);
                state.bindByteBuffer(2, data);
                state.step();
                state.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            data.reuse();
        }
    }

    public void removePendingTask(final long id) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda165
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1031x73f8e42e(id);
            }
        });
    }

    /* renamed from: lambda$removePendingTask$10$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1031x73f8e42e(long id) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM pending_tasks WHERE id = " + id).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda55
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m986x5f532def();
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$loadPendingTasks$30$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m986x5f532def() {
        SQLiteCursor cursor;
        NativeByteBuffer data;
        TLObject request;
        TLRPC.InputChannel inputChannel;
        TLRPC.InputChannel inputChannel2;
        TLObject request2;
        try {
            boolean z = false;
            SQLiteCursor cursor2 = this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
            while (cursor2.next()) {
                int i = z ? 1 : 0;
                int i2 = z ? 1 : 0;
                final long taskId = cursor2.longValue(i);
                NativeByteBuffer data2 = cursor2.byteBufferValue(1);
                if (data2 == null) {
                    cursor = cursor2;
                } else {
                    int type = data2.readInt32(z);
                    switch (type) {
                        case 0:
                            cursor = cursor2;
                            data = data2;
                            final TLRPC.Chat chat = TLRPC.Chat.TLdeserialize(data, data.readInt32(false), false);
                            if (chat != null) {
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda63
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.m967x9d30c8cc(chat, taskId);
                                    }
                                });
                                break;
                            }
                            break;
                        case 1:
                            cursor = cursor2;
                            data = data2;
                            final long channelId = data.readInt32(false);
                            final int newDialogType = data.readInt32(false);
                            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda171
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m968xf44eb9ab(channelId, newDialogType, taskId);
                                }
                            });
                            break;
                        case 2:
                        case 5:
                        case 8:
                        case 10:
                        case 14:
                            cursor = cursor2;
                            data = data2;
                            final TLRPC.Dialog dialog = new TLRPC.TL_dialog();
                            dialog.id = data.readInt64(false);
                            dialog.top_message = data.readInt32(false);
                            dialog.read_inbox_max_id = data.readInt32(false);
                            dialog.read_outbox_max_id = data.readInt32(false);
                            dialog.unread_count = data.readInt32(false);
                            dialog.last_message_date = data.readInt32(false);
                            dialog.pts = data.readInt32(false);
                            dialog.flags = data.readInt32(false);
                            if (type >= 5) {
                                dialog.pinned = data.readBool(false);
                                dialog.pinnedNum = data.readInt32(false);
                            }
                            if (type >= 8) {
                                dialog.unread_mentions_count = data.readInt32(false);
                            }
                            if (type >= 10) {
                                dialog.unread_mark = data.readBool(false);
                            }
                            if (type >= 14) {
                                dialog.folder_id = data.readInt32(false);
                            }
                            final TLRPC.InputPeer peer = TLRPC.InputPeer.TLdeserialize(data, data.readInt32(false), false);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda69
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m969x4b6caa8a(dialog, peer, taskId);
                                }
                            });
                            break;
                        case 3:
                            cursor = cursor2;
                            data = data2;
                            long random_id = data.readInt64(false);
                            TLRPC.InputPeer peer2 = TLRPC.InputPeer.TLdeserialize(data, data.readInt32(false), false);
                            TLRPC.TL_inputMediaGame game = (TLRPC.TL_inputMediaGame) TLRPC.InputMedia.TLdeserialize(data, data.readInt32(false), false);
                            getSendMessagesHelper().sendGame(peer2, game, random_id, taskId);
                            break;
                        case 4:
                            cursor = cursor2;
                            data = data2;
                            final long did = data.readInt64(false);
                            final boolean pin = data.readBool(false);
                            final TLRPC.InputPeer peer3 = TLRPC.InputPeer.TLdeserialize(data, data.readInt32(false), false);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda14
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m970xa28a9b69(did, pin, peer3, taskId);
                                }
                            });
                            break;
                        case 6:
                            cursor = cursor2;
                            data = data2;
                            final long channelId2 = data.readInt32(false);
                            final int newDialogType2 = data.readInt32(false);
                            final TLRPC.InputChannel inputChannel3 = TLRPC.InputChannel.TLdeserialize(data, data.readInt32(false), false);
                            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda173
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m971xf9a88c48(channelId2, newDialogType2, taskId, inputChannel3);
                                }
                            });
                            break;
                        case 7:
                            cursor = cursor2;
                            data = data2;
                            final long channelId3 = data.readInt32(false);
                            int constructor = data.readInt32(false);
                            TLObject request3 = TLRPC.TL_messages_deleteMessages.TLdeserialize(data, constructor, false);
                            if (request3 != null) {
                                request = request3;
                            } else {
                                request = TLRPC.TL_channels_deleteMessages.TLdeserialize(data, constructor, false);
                            }
                            if (request == null) {
                                removePendingTask(taskId);
                                break;
                            } else {
                                final TLObject finalRequest = request;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda189
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.m973xa7e46e06(channelId3, taskId, finalRequest);
                                    }
                                });
                                break;
                            }
                        case 9:
                            cursor = cursor2;
                            data = data2;
                            final long did2 = data.readInt64(false);
                            final TLRPC.InputPeer peer4 = TLRPC.InputPeer.TLdeserialize(data, data.readInt32(false), false);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda7
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m975x56204fc4(did2, peer4, taskId);
                                }
                            });
                            break;
                        case 11:
                            cursor = cursor2;
                            data = data2;
                            final int mid = data.readInt32(false);
                            final long channelId4 = data.readInt32(false);
                            final int ttl = data.readInt32(false);
                            if (channelId4 != 0) {
                                inputChannel = TLRPC.InputChannel.TLdeserialize(data, data.readInt32(false), false);
                            } else {
                                inputChannel = null;
                            }
                            final TLRPC.InputChannel inputChannel4 = inputChannel;
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda175
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m976xd2b302ee(channelId4, mid, inputChannel4, ttl, taskId);
                                }
                            });
                            break;
                        case 12:
                        case 19:
                        case 20:
                            cursor = cursor2;
                            data = data2;
                            removePendingTask(taskId);
                            break;
                        case 13:
                            final long did3 = data2.readInt64(false);
                            final boolean first = data2.readBool(false);
                            final int onlyHistory = data2.readInt32(false);
                            final int maxIdDelete = data2.readInt32(false);
                            final boolean revoke = data2.readBool(false);
                            final TLRPC.InputPeer inputPeer = TLRPC.InputPeer.TLdeserialize(data2, data2.readInt32(false), false);
                            cursor = cursor2;
                            data = data2;
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda13
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m979xd80cd58b(did3, first, onlyHistory, maxIdDelete, revoke, inputPeer, taskId);
                                }
                            });
                            break;
                        case 15:
                            final TLRPC.InputPeer inputPeer2 = TLRPC.InputPeer.TLdeserialize(data2, data2.readInt32(false), false);
                            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda76
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m980x2f2ac66a(inputPeer2, taskId);
                                }
                            });
                            cursor = cursor2;
                            data = data2;
                            break;
                        case 16:
                            final int folderId = data2.readInt32(false);
                            int count = data2.readInt32(false);
                            final ArrayList<TLRPC.InputDialogPeer> peers = new ArrayList<>();
                            for (int a = 0; a < count; a++) {
                                TLRPC.InputDialogPeer inputPeer3 = TLRPC.InputDialogPeer.TLdeserialize(data2, data2.readInt32(false), false);
                                peers.add(inputPeer3);
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda155
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m981x8648b749(folderId, peers, taskId);
                                }
                            });
                            cursor = cursor2;
                            data = data2;
                            break;
                        case 17:
                            final int folderId2 = data2.readInt32(false);
                            int count2 = data2.readInt32(false);
                            final ArrayList<TLRPC.TL_inputFolderPeer> peers2 = new ArrayList<>();
                            for (int a2 = 0; a2 < count2; a2++) {
                                TLRPC.TL_inputFolderPeer inputPeer4 = TLRPC.TL_inputFolderPeer.TLdeserialize(data2, data2.readInt32(false), false);
                                peers2.add(inputPeer4);
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda156
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m982xdd66a828(folderId2, peers2, taskId);
                                }
                            });
                            cursor = cursor2;
                            data = data2;
                            break;
                        case 18:
                            final long dialogId = data2.readInt64(false);
                            data2.readInt32(false);
                            final TLObject request4 = TLRPC.TL_messages_deleteScheduledMessages.TLdeserialize(data2, data2.readInt32(false), false);
                            if (request4 == null) {
                                removePendingTask(taskId);
                                cursor = cursor2;
                                data = data2;
                                break;
                            } else {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda192
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.m983x34849907(dialogId, taskId, request4);
                                    }
                                });
                                cursor = cursor2;
                                data = data2;
                                break;
                            }
                        case 21:
                            final Theme.OverrideWallpaperInfo info = new Theme.OverrideWallpaperInfo();
                            data2.readInt64(false);
                            info.isBlurred = data2.readBool(false);
                            info.isMotion = data2.readBool(false);
                            info.color = data2.readInt32(false);
                            info.gradientColor1 = data2.readInt32(false);
                            info.rotation = data2.readInt32(false);
                            info.intensity = (float) data2.readDouble(false);
                            final boolean install = data2.readBool(false);
                            info.slug = data2.readString(false);
                            info.originalFileName = data2.readString(false);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda91
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m978x80eee4ac(info, install, taskId);
                                }
                            });
                            cursor = cursor2;
                            data = data2;
                            break;
                        case 22:
                            final TLRPC.InputPeer inputPeer5 = TLRPC.InputPeer.TLdeserialize(data2, data2.readInt32(false), false);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda78
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m984x8ba289e6(inputPeer5, taskId);
                                }
                            });
                            cursor = cursor2;
                            data = data2;
                            break;
                        case 23:
                            final long dialogId2 = data2.readInt64(false);
                            final int mid2 = data2.readInt32(false);
                            final int ttl2 = data2.readInt32(false);
                            if (!DialogObject.isEncryptedDialog(dialogId2) && DialogObject.isChatDialog(dialogId2) && data2.hasRemaining()) {
                                inputChannel2 = TLRPC.InputChannel.TLdeserialize(data2, data2.readInt32(false), false);
                            } else {
                                inputChannel2 = null;
                            }
                            final TLRPC.InputChannel inputChannel5 = inputChannel2;
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda176
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m977x29d0f3cd(dialogId2, mid2, inputChannel5, ttl2, taskId);
                                }
                            });
                            cursor = cursor2;
                            data = data2;
                            break;
                        case 24:
                            final long dialogId3 = data2.readInt64(false);
                            int constructor2 = data2.readInt32(false);
                            TLObject request5 = TLRPC.TL_messages_deleteMessages.TLdeserialize(data2, constructor2, false);
                            if (request5 != null) {
                                request2 = request5;
                            } else {
                                request2 = TLRPC.TL_channels_deleteMessages.TLdeserialize(data2, constructor2, false);
                            }
                            if (request2 == null) {
                                removePendingTask(taskId);
                                cursor = cursor2;
                                data = data2;
                                break;
                            } else {
                                final TLObject finalRequest2 = request2;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda191
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.m974xff025ee5(dialogId3, taskId, finalRequest2);
                                    }
                                });
                                cursor = cursor2;
                                data = data2;
                                break;
                            }
                        case 25:
                            final long channelId5 = data2.readInt64(z);
                            final int newDialogType3 = data2.readInt32(z);
                            final TLRPC.InputChannel inputChannel6 = TLRPC.InputChannel.TLdeserialize(data2, data2.readInt32(z), z);
                            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda174
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m972x50c67d27(channelId5, newDialogType3, taskId, inputChannel6);
                                }
                            });
                            cursor = cursor2;
                            data = data2;
                            break;
                        case 100:
                            final int chatId = data2.readInt32(z);
                            final boolean revoke2 = data2.readBool(z);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda159
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MessagesStorage.this.m985xe2c07ac5(chatId, revoke2, taskId);
                                }
                            });
                            cursor = cursor2;
                            data = data2;
                            break;
                        default:
                            cursor = cursor2;
                            data = data2;
                            break;
                    }
                    data.reuse();
                }
                cursor2 = cursor;
                z = false;
            }
            cursor2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadPendingTasks$11$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m967x9d30c8cc(TLRPC.Chat chat, long taskId) {
        getMessagesController().loadUnknownChannel(chat, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$12$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m968xf44eb9ab(long channelId, int newDialogType, long taskId) {
        getMessagesController().getChannelDifference(channelId, newDialogType, taskId, null);
    }

    /* renamed from: lambda$loadPendingTasks$13$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m969x4b6caa8a(TLRPC.Dialog dialog, TLRPC.InputPeer peer, long taskId) {
        getMessagesController().checkLastDialogMessage(dialog, peer, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$14$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m970xa28a9b69(long did, boolean pin, TLRPC.InputPeer peer, long taskId) {
        getMessagesController().pinDialog(did, pin, peer, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$15$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m971xf9a88c48(long channelId, int newDialogType, long taskId, TLRPC.InputChannel inputChannel) {
        getMessagesController().getChannelDifference(channelId, newDialogType, taskId, inputChannel);
    }

    /* renamed from: lambda$loadPendingTasks$16$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m972x50c67d27(long channelId, int newDialogType, long taskId, TLRPC.InputChannel inputChannel) {
        getMessagesController().getChannelDifference(channelId, newDialogType, taskId, inputChannel);
    }

    /* renamed from: lambda$loadPendingTasks$17$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m973xa7e46e06(long channelId, long taskId, TLObject finalRequest) {
        getMessagesController().deleteMessages(null, null, null, -channelId, true, false, false, taskId, finalRequest);
    }

    /* renamed from: lambda$loadPendingTasks$18$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m974xff025ee5(long dialogId, long taskId, TLObject finalRequest) {
        getMessagesController().deleteMessages(null, null, null, dialogId, true, false, false, taskId, finalRequest);
    }

    /* renamed from: lambda$loadPendingTasks$19$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m975x56204fc4(long did, TLRPC.InputPeer peer, long taskId) {
        getMessagesController().markDialogAsUnread(did, peer, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$20$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m976xd2b302ee(long channelId, int mid, TLRPC.InputChannel inputChannel, int ttl, long taskId) {
        getMessagesController().markMessageAsRead2(-channelId, mid, inputChannel, ttl, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$21$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m977x29d0f3cd(long dialogId, int mid, TLRPC.InputChannel inputChannel, int ttl, long taskId) {
        getMessagesController().markMessageAsRead2(dialogId, mid, inputChannel, ttl, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$22$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m978x80eee4ac(Theme.OverrideWallpaperInfo info, boolean install, long taskId) {
        getMessagesController().saveWallpaperToServer(null, info, install, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$23$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m979xd80cd58b(long did, boolean first, int onlyHistory, int maxIdDelete, boolean revoke, TLRPC.InputPeer inputPeer, long taskId) {
        getMessagesController().deleteDialog(did, first ? 1 : 0, onlyHistory, maxIdDelete, revoke, inputPeer, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$24$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m980x2f2ac66a(TLRPC.InputPeer inputPeer, long taskId) {
        getMessagesController().loadUnknownDialog(inputPeer, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$25$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m981x8648b749(int folderId, ArrayList peers, long taskId) {
        getMessagesController().reorderPinnedDialogs(folderId, peers, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$26$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m982xdd66a828(int folderId, ArrayList peers, long taskId) {
        getMessagesController().addDialogToFolder(null, folderId, -1, peers, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$27$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m983x34849907(long dialogId, long taskId, TLObject request) {
        getMessagesController().deleteMessages(null, null, null, dialogId, true, true, false, taskId, request);
    }

    /* renamed from: lambda$loadPendingTasks$28$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m984x8ba289e6(TLRPC.InputPeer inputPeer, long taskId) {
        getMessagesController().reloadMentionsCountForChannel(inputPeer, taskId);
    }

    /* renamed from: lambda$loadPendingTasks$29$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m985xe2c07ac5(int chatId, boolean revoke, long taskId) {
        getSecretChatHelper().declineSecretChat(chatId, revoke, taskId);
    }

    public void saveChannelPts(final long channelId, final int pts) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda136
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1038lambda$saveChannelPts$31$orgtelegrammessengerMessagesStorage(pts, channelId);
            }
        });
    }

    /* renamed from: lambda$saveChannelPts$31$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1038lambda$saveChannelPts$31$orgtelegrammessengerMessagesStorage(int pts, long channelId) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
            state.bindInteger(1, pts);
            state.bindLong(2, -channelId);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: saveDiffParamsInternal */
    public void m1044lambda$saveDiffParams$32$orgtelegrammessengerMessagesStorage(int seq, int pts, int date, int qts) {
        try {
            if (this.lastSavedSeq == seq && this.lastSavedPts == pts && this.lastSavedDate == date && this.lastQtsValue == qts) {
                return;
            }
            SQLitePreparedStatement state = this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
            state.bindInteger(1, seq);
            state.bindInteger(2, pts);
            state.bindInteger(3, date);
            state.bindInteger(4, qts);
            state.step();
            state.dispose();
            this.lastSavedSeq = seq;
            this.lastSavedPts = pts;
            this.lastSavedDate = date;
            this.lastSavedQts = qts;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveDiffParams(final int seq, final int pts, final int date, final int qts) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda129
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1044lambda$saveDiffParams$32$orgtelegrammessengerMessagesStorage(seq, pts, date, qts);
            }
        });
    }

    /* renamed from: lambda$updateMutedDialogsFiltersCounters$33$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1080xc13b591e() {
        resetAllUnreadCounters(true);
    }

    public void updateMutedDialogsFiltersCounters() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda118
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1080xc13b591e();
            }
        });
    }

    public void setDialogFlags(final long did, final long flags) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda185
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1046lambda$setDialogFlags$34$orgtelegrammessengerMessagesStorage(did, flags);
            }
        });
    }

    /* renamed from: lambda$setDialogFlags$34$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1046lambda$setDialogFlags$34$orgtelegrammessengerMessagesStorage(long did, long flags) {
        int oldFlags = 0;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT flags FROM dialog_settings WHERE did = " + did, new Object[0]);
            if (cursor.next()) {
                oldFlags = cursor.intValue(0);
            }
            cursor.dispose();
            if (flags == oldFlags) {
                return;
            }
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", Long.valueOf(did), Long.valueOf(flags))).stepThis().dispose();
            resetAllUnreadCounters(true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putPushMessage(final MessageObject message) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda56
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1021lambda$putPushMessage$35$orgtelegrammessengerMessagesStorage(message);
            }
        });
    }

    /* renamed from: lambda$putPushMessage$35$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1021lambda$putPushMessage$35$orgtelegrammessengerMessagesStorage(MessageObject message) {
        try {
            NativeByteBuffer data = new NativeByteBuffer(message.messageOwner.getObjectSize());
            message.messageOwner.serializeToStream(data);
            int flags = 0;
            if (message.localType == 2) {
                flags = 0 | 1;
            }
            if (message.localChannel) {
                flags |= 2;
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO unread_push_messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            state.requery();
            state.bindLong(1, message.getDialogId());
            state.bindInteger(2, message.getId());
            state.bindLong(3, message.messageOwner.random_id);
            state.bindInteger(4, message.messageOwner.date);
            state.bindByteBuffer(5, data);
            if (message.messageText == null) {
                state.bindNull(6);
            } else {
                state.bindString(6, message.messageText.toString());
            }
            if (message.localName == null) {
                state.bindNull(7);
            } else {
                state.bindString(7, message.localName);
            }
            if (message.localUserName == null) {
                state.bindNull(8);
            } else {
                state.bindString(8, message.localUserName);
            }
            state.bindInteger(9, flags);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearLocalDatabase() {
        getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda179
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m910x6abde44();
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x0239 A[Catch: all -> 0x0299, Exception -> 0x029b, TryCatch #0 {Exception -> 0x029b, blocks: (B:3:0x0004, B:4:0x004a, B:6:0x0050, B:8:0x005a, B:10:0x0062, B:11:0x0075, B:13:0x007b, B:15:0x00a0, B:16:0x00a5, B:19:0x00b1, B:21:0x00cf, B:43:0x016a, B:44:0x016f, B:46:0x0239, B:49:0x0252, B:50:0x0256, B:51:0x025f), top: B:61:0x0004, outer: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0245  */
    /* renamed from: lambda$clearLocalDatabase$37$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m910x6abde44() {
        /*
            Method dump skipped, instructions count: 692
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m910x6abde44():void");
    }

    /* renamed from: lambda$clearLocalDatabase$36$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m909xaf8ded65() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didClearDatabase, new Object[0]);
        getMediaDataController().loadAttachMenuBots(false, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ReadDialog {
        public int date;
        public int lastMid;
        public int unreadCount;

        private ReadDialog() {
        }
    }

    public void readAllDialogs(final int folderId) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda127
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1029lambda$readAllDialogs$39$orgtelegrammessengerMessagesStorage(folderId);
            }
        });
    }

    /* renamed from: lambda$readAllDialogs$39$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1029lambda$readAllDialogs$39$orgtelegrammessengerMessagesStorage(int folderId) {
        SQLiteCursor cursor;
        try {
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            ArrayList<Integer> encryptedChatIds = new ArrayList<>();
            final LongSparseArray<ReadDialog> dialogs = new LongSparseArray<>();
            if (folderId >= 0) {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count > 0 AND folder_id = %1$d", Integer.valueOf(folderId)), new Object[0]);
            } else {
                cursor = this.database.queryFinalized("SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count > 0", new Object[0]);
            }
            while (cursor.next()) {
                long did = cursor.longValue(0);
                if (!DialogObject.isFolderDialogId(did)) {
                    ReadDialog dialog = new ReadDialog();
                    dialog.lastMid = cursor.intValue(1);
                    dialog.unreadCount = cursor.intValue(2);
                    dialog.date = cursor.intValue(3);
                    dialogs.put(did, dialog);
                    if (!DialogObject.isEncryptedDialog(did)) {
                        if (DialogObject.isChatDialog(did)) {
                            if (!chatsToLoad.contains(Long.valueOf(-did))) {
                                chatsToLoad.add(Long.valueOf(-did));
                            }
                        } else if (!usersToLoad.contains(Long.valueOf(did))) {
                            usersToLoad.add(Long.valueOf(did));
                        }
                    } else {
                        int encryptedChatId = DialogObject.getEncryptedChatId(did);
                        if (!encryptedChatIds.contains(Integer.valueOf(encryptedChatId))) {
                            encryptedChatIds.add(Integer.valueOf(encryptedChatId));
                        }
                    }
                }
            }
            cursor.dispose();
            final ArrayList<TLRPC.User> users = new ArrayList<>();
            final ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            final ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
            if (!encryptedChatIds.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", encryptedChatIds), encryptedChats, usersToLoad);
            }
            if (!usersToLoad.isEmpty()) {
                getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (!chatsToLoad.isEmpty()) {
                getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda50
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1028lambda$readAllDialogs$38$orgtelegrammessengerMessagesStorage(users, chats, encryptedChats, dialogs);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$readAllDialogs$38$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1028lambda$readAllDialogs$38$orgtelegrammessengerMessagesStorage(ArrayList users, ArrayList chats, ArrayList encryptedChats, LongSparseArray dialogs) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        for (int a = 0; a < dialogs.size(); a++) {
            long did = dialogs.keyAt(a);
            ReadDialog dialog = (ReadDialog) dialogs.valueAt(a);
            getMessagesController().markDialogAsRead(did, dialog.lastMid, dialog.lastMid, dialog.date, false, 0, dialog.unreadCount, true, 0);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:70:0x01b8  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01d0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private org.telegram.tgnet.TLRPC.messages_Dialogs loadDialogsByIds(java.lang.String r24, java.util.ArrayList<java.lang.Long> r25, java.util.ArrayList<java.lang.Long> r26, java.util.ArrayList<java.lang.Integer> r27) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 666
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadDialogsByIds(java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList):org.telegram.tgnet.TLRPC$messages_Dialogs");
    }

    private void loadDialogFilters() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m966x9322d85();
            }
        });
    }

    /* renamed from: lambda$loadDialogFilters$41$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m966x9322d85() {
        TLRPC.messages_Dialogs dialogs;
        SQLiteCursor cursor2;
        boolean hasDefaultFilter;
        long did;
        try {
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            ArrayList<Integer> encryptedToLoad = new ArrayList<>();
            ArrayList<Long> dialogsToLoad = new ArrayList<>();
            SparseArray<MessagesController.DialogFilter> filtersById = new SparseArray<>();
            usersToLoad.add(Long.valueOf(getUserConfig().getClientUserId()));
            int i = 0;
            SQLiteCursor filtersCursor = this.database.queryFinalized("SELECT id, ord, unread_count, flags, title FROM dialog_filter WHERE 1", new Object[0]);
            boolean updateCounters = false;
            boolean hasDefaultFilter2 = false;
            while (true) {
                int i2 = 2;
                int i3 = 1;
                if (!filtersCursor.next()) {
                    break;
                }
                MessagesController.DialogFilter filter = new MessagesController.DialogFilter();
                filter.id = filtersCursor.intValue(i);
                filter.order = filtersCursor.intValue(1);
                filter.unreadCount = -1;
                filter.pendingUnreadCount = -1;
                filter.flags = filtersCursor.intValue(3);
                filter.name = filtersCursor.stringValue(4);
                this.dialogFilters.add(filter);
                this.dialogFiltersMap.put(filter.id, filter);
                filtersById.put(filter.id, filter);
                if (filter.pendingUnreadCount < 0) {
                    updateCounters = true;
                }
                int a = 0;
                while (a < i2) {
                    if (a == 0) {
                        cursor2 = this.database.queryFinalized("SELECT peer, pin FROM dialog_filter_pin_v2 WHERE id = " + filter.id, new Object[i]);
                    } else {
                        cursor2 = this.database.queryFinalized("SELECT peer FROM dialog_filter_ep WHERE id = " + filter.id, new Object[i]);
                    }
                    while (cursor2.next()) {
                        long did2 = cursor2.longValue(i);
                        if (a == 0) {
                            if (!DialogObject.isEncryptedDialog(did2)) {
                                filter.alwaysShow.add(Long.valueOf(did2));
                            }
                            int pin = cursor2.intValue(i3);
                            if (pin == Integer.MIN_VALUE) {
                                hasDefaultFilter = hasDefaultFilter2;
                                did = did2;
                            } else {
                                hasDefaultFilter = hasDefaultFilter2;
                                did = did2;
                                filter.pinnedDialogs.put(did, pin);
                                if (!dialogsToLoad.contains(Long.valueOf(did))) {
                                    dialogsToLoad.add(Long.valueOf(did));
                                }
                            }
                        } else {
                            hasDefaultFilter = hasDefaultFilter2;
                            did = did2;
                            if (!DialogObject.isEncryptedDialog(did)) {
                                filter.neverShow.add(Long.valueOf(did));
                            }
                        }
                        if (DialogObject.isChatDialog(did)) {
                            if (!chatsToLoad.contains(Long.valueOf(-did))) {
                                chatsToLoad.add(Long.valueOf(-did));
                            }
                        } else if (DialogObject.isUserDialog(did)) {
                            if (!usersToLoad.contains(Long.valueOf(did))) {
                                usersToLoad.add(Long.valueOf(did));
                            }
                        } else {
                            int encryptedChatId = DialogObject.getEncryptedChatId(did);
                            if (!encryptedToLoad.contains(Integer.valueOf(encryptedChatId))) {
                                encryptedToLoad.add(Integer.valueOf(encryptedChatId));
                            }
                        }
                        hasDefaultFilter2 = hasDefaultFilter;
                        i = 0;
                        i3 = 1;
                    }
                    cursor2.dispose();
                    a++;
                    hasDefaultFilter2 = hasDefaultFilter2;
                    i = 0;
                    i2 = 2;
                    i3 = 1;
                }
                boolean hasDefaultFilter3 = hasDefaultFilter2;
                if (filter.id != 0) {
                    hasDefaultFilter2 = hasDefaultFilter3;
                } else {
                    hasDefaultFilter2 = true;
                }
                i = 0;
            }
            boolean hasDefaultFilter4 = hasDefaultFilter2;
            filtersCursor.dispose();
            if (!hasDefaultFilter4) {
                MessagesController.DialogFilter filter2 = new MessagesController.DialogFilter();
                filter2.id = 0;
                filter2.order = 0;
                filter2.name = "ALL_CHATS";
                for (int i4 = 0; i4 < this.dialogFilters.size(); i4++) {
                    this.dialogFilters.get(i4).order++;
                }
                this.dialogFilters.add(filter2);
                this.dialogFiltersMap.put(filter2.id, filter2);
                filtersById.put(filter2.id, filter2);
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO dialog_filter VALUES(?, ?, ?, ?, ?)");
                state.bindInteger(1, filter2.id);
                state.bindInteger(2, filter2.order);
                state.bindInteger(3, filter2.unreadCount);
                state.bindInteger(4, filter2.flags);
                state.bindString(5, filter2.name);
                state.stepThis().dispose();
            }
            Collections.sort(this.dialogFilters, MessagesStorage$$ExternalSyntheticLambda108.INSTANCE);
            if (updateCounters) {
                calcUnreadCounters(true);
            }
            if (!dialogsToLoad.isEmpty()) {
                dialogs = loadDialogsByIds(TextUtils.join(",", dialogsToLoad), usersToLoad, chatsToLoad, encryptedToLoad);
            } else {
                dialogs = new TLRPC.TL_messages_dialogs();
            }
            ArrayList<TLRPC.User> users = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
            if (!encryptedToLoad.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad), encryptedChats, usersToLoad);
            }
            if (!usersToLoad.isEmpty()) {
                getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (!chatsToLoad.isEmpty()) {
                getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            getMessagesController().processLoadedDialogFilters(new ArrayList<>(this.dialogFilters), dialogs, null, users, chats, encryptedChats, 0);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ int lambda$loadDialogFilters$40(MessagesController.DialogFilter o1, MessagesController.DialogFilter o2) {
        if (o1.order > o2.order) {
            return 1;
        }
        if (o1.order < o2.order) {
            return -1;
        }
        return 0;
    }

    private void calcUnreadCounters(boolean apply) {
        int i;
        int i2;
        ArrayList<TLRPC.Chat> chats;
        String str;
        LongSparseArray<TLRPC.User> encUsersDict;
        LongSparseArray<TLRPC.Chat> chatsDict;
        ArrayList<TLRPC.User> encUsers;
        LongSparseArray<TLRPC.User> encUsersDict2;
        LongSparseArray<TLRPC.User> encUsersDict3;
        ArrayList<Long> chatsToLoad;
        String str2;
        LongSparseArray<TLRPC.Chat> chatsDict2;
        LongSparseIntArray dialogsByFolders;
        ArrayList<Long> chatsToLoad2;
        ArrayList<Integer> encryptedToLoad;
        int N;
        int a;
        int flags;
        MessagesController.DialogFilter filter;
        MessagesController.DialogFilter filter2;
        int N2;
        int flag;
        MessagesController.DialogFilter filter3;
        int N22;
        int count;
        int flag2;
        MessagesController.DialogFilter filter4;
        int N3;
        int a2;
        int N23;
        LongSparseIntArray dialogsByFolders2;
        ArrayList<Long> chatsToLoad3;
        ArrayList<Integer> encryptedToLoad2;
        int flag3;
        MessagesController.DialogFilter filter5;
        int N4;
        int a3;
        LongSparseIntArray dialogsByFolders3;
        int count2;
        int flag4;
        ArrayList<TLRPC.Chat> chats2;
        int N5;
        LongSparseArray<TLRPC.Chat> chatsDict3;
        LongSparseArray<TLRPC.User> encUsersDict4;
        int N6;
        ArrayList<TLRPC.EncryptedChat> encryptedChats;
        LongSparseArray<TLRPC.User> usersDict;
        String str3;
        LongSparseArray<TLRPC.User> encUsersDict5;
        ArrayList<TLRPC.Chat> chats3;
        int a4 = 0;
        while (true) {
            i = 2;
            i2 = 0;
            if (a4 >= 2) {
                break;
            }
            for (int b = 0; b < 2; b++) {
                try {
                    int[] iArr = this.contacts[a4];
                    int[] iArr2 = this.nonContacts[a4];
                    int[] iArr3 = this.bots[a4];
                    int[] iArr4 = this.channels[a4];
                    this.groups[a4][b] = 0;
                    iArr4[b] = 0;
                    iArr3[b] = 0;
                    iArr2[b] = 0;
                    iArr[b] = 0;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
            a4++;
            FileLog.e(e);
            return;
        }
        this.dialogsWithMentions.clear();
        this.dialogsWithUnread.clear();
        ArrayList<TLRPC.User> users = new ArrayList<>();
        ArrayList<TLRPC.User> encUsers2 = new ArrayList<>();
        ArrayList<TLRPC.Chat> chats4 = new ArrayList<>();
        ArrayList<Long> usersToLoad = new ArrayList<>();
        ArrayList<Long> chatsToLoad4 = new ArrayList<>();
        ArrayList<Integer> encryptedToLoad3 = new ArrayList<>();
        LongSparseIntArray dialogsByFolders4 = new LongSparseIntArray();
        SQLiteCursor cursor = this.database.queryFinalized("SELECT did, folder_id, unread_count, unread_count_i FROM dialogs WHERE unread_count > 0 OR flags > 0 UNION ALL SELECT did, folder_id, unread_count, unread_count_i FROM dialogs WHERE unread_count_i > 0", new Object[0]);
        while (cursor.next()) {
            int folderId = cursor.intValue(1);
            long did = cursor.longValue(i2);
            int unread = cursor.intValue(i);
            int mentions = cursor.intValue(3);
            if (unread > 0) {
                this.dialogsWithUnread.put(did, Integer.valueOf(unread));
            }
            if (mentions > 0) {
                this.dialogsWithMentions.put(did, Integer.valueOf(mentions));
            }
            dialogsByFolders4.put(did, folderId);
            if (DialogObject.isEncryptedDialog(did)) {
                int encryptedChatId = DialogObject.getEncryptedChatId(did);
                if (!encryptedToLoad3.contains(Integer.valueOf(encryptedChatId))) {
                    encryptedToLoad3.add(Integer.valueOf(encryptedChatId));
                }
            } else if (DialogObject.isUserDialog(did)) {
                if (!usersToLoad.contains(Long.valueOf(did))) {
                    usersToLoad.add(Long.valueOf(did));
                }
            } else if (!chatsToLoad4.contains(Long.valueOf(-did))) {
                chatsToLoad4.add(Long.valueOf(-did));
            }
            i = 2;
            i2 = 0;
        }
        cursor.dispose();
        LongSparseArray<TLRPC.User> usersDict2 = new LongSparseArray<>();
        LongSparseArray<TLRPC.Chat> chatsDict4 = new LongSparseArray<>();
        LongSparseArray<TLRPC.User> encUsersDict6 = new LongSparseArray<>();
        LongSparseIntArray encryptedChatsByUsersCount = new LongSparseIntArray();
        LongSparseArray<Boolean> mutedDialogs = new LongSparseArray<>();
        LongSparseArray<Boolean> archivedDialogs = new LongSparseArray<>();
        String str4 = ",";
        if (!usersToLoad.isEmpty()) {
            getUsersInternal(TextUtils.join(str4, usersToLoad), users);
            int a5 = 0;
            int N7 = users.size();
            while (true) {
                ArrayList<Long> usersToLoad2 = usersToLoad;
                int N8 = N7;
                if (a5 >= N8) {
                    break;
                }
                TLRPC.User user = users.get(a5);
                ArrayList<TLRPC.User> users2 = users;
                LongSparseArray<TLRPC.Chat> chatsDict5 = chatsDict4;
                ArrayList<TLRPC.User> encUsers3 = encUsers2;
                boolean muted = getMessagesController().isDialogMuted(user.id);
                int idx1 = dialogsByFolders4.get(user.id);
                int idx2 = muted ? 1 : 0;
                if (muted) {
                    encUsersDict5 = encUsersDict6;
                    str3 = str4;
                    mutedDialogs.put(user.id, true);
                } else {
                    encUsersDict5 = encUsersDict6;
                    str3 = str4;
                }
                if (idx1 == 1) {
                    chats3 = chats4;
                    archivedDialogs.put(user.id, true);
                } else {
                    chats3 = chats4;
                }
                if (user.bot) {
                    int[] iArr5 = this.bots[idx1];
                    iArr5[idx2] = iArr5[idx2] + 1;
                } else {
                    if (!user.self && !user.contact) {
                        int[] iArr6 = this.nonContacts[idx1];
                        iArr6[idx2] = iArr6[idx2] + 1;
                    }
                    int[] iArr7 = this.contacts[idx1];
                    iArr7[idx2] = iArr7[idx2] + 1;
                }
                usersDict2.put(user.id, user);
                a5++;
                usersToLoad = usersToLoad2;
                users = users2;
                encUsers2 = encUsers3;
                N7 = N8;
                chatsDict4 = chatsDict5;
                encUsersDict6 = encUsersDict5;
                str4 = str3;
                chats4 = chats3;
            }
            chatsDict = chatsDict4;
            encUsers = encUsers2;
            chats = chats4;
            encUsersDict = encUsersDict6;
            str = str4;
        } else {
            chatsDict = chatsDict4;
            encUsers = encUsers2;
            chats = chats4;
            encUsersDict = encUsersDict6;
            str = str4;
        }
        if (encryptedToLoad3.isEmpty()) {
            chatsToLoad = chatsToLoad4;
            encUsersDict2 = encUsersDict;
            str2 = str;
            encUsersDict3 = usersDict2;
        } else {
            ArrayList<Long> encUsersToLoad = new ArrayList<>();
            ArrayList<TLRPC.EncryptedChat> encryptedChats2 = new ArrayList<>();
            str2 = str;
            getEncryptedChatsInternal(TextUtils.join(str2, encryptedToLoad3), encryptedChats2, encUsersToLoad);
            if (!encUsersToLoad.isEmpty()) {
                ArrayList<TLRPC.User> encUsers4 = encUsers;
                getUsersInternal(TextUtils.join(str2, encUsersToLoad), encUsers4);
                int a6 = 0;
                int N9 = encUsers4.size();
                while (a6 < N9) {
                    TLRPC.User user2 = encUsers4.get(a6);
                    ArrayList<Long> encUsersToLoad2 = encUsersToLoad;
                    LongSparseArray<TLRPC.User> encUsersDict7 = encUsersDict;
                    encUsersDict7.put(user2.id, user2);
                    a6++;
                    encUsersDict = encUsersDict7;
                    chatsToLoad4 = chatsToLoad4;
                    encUsers4 = encUsers4;
                    encUsersToLoad = encUsersToLoad2;
                }
                chatsToLoad = chatsToLoad4;
                LongSparseArray<TLRPC.User> encUsersDict8 = encUsersDict;
                int a7 = 0;
                int N10 = encryptedChats2.size();
                while (a7 < N10) {
                    TLRPC.EncryptedChat encryptedChat = encryptedChats2.get(a7);
                    TLRPC.User user3 = encUsersDict8.get(encryptedChat.user_id);
                    if (user3 == null) {
                        encUsersDict4 = encUsersDict8;
                        usersDict = usersDict2;
                        encryptedChats = encryptedChats2;
                        N6 = N10;
                    } else {
                        usersDict = usersDict2;
                        encryptedChats = encryptedChats2;
                        long did2 = DialogObject.makeEncryptedDialogId(encryptedChat.id);
                        boolean muted2 = getMessagesController().isDialogMuted(did2);
                        int idx12 = dialogsByFolders4.get(did2);
                        int idx22 = muted2 ? 1 : 0;
                        if (muted2) {
                            encUsersDict4 = encUsersDict8;
                            mutedDialogs.put(user3.id, true);
                        } else {
                            encUsersDict4 = encUsersDict8;
                        }
                        if (idx12 != 1) {
                            N6 = N10;
                        } else {
                            N6 = N10;
                            archivedDialogs.put(user3.id, true);
                        }
                        if (!user3.self && !user3.contact) {
                            int[] iArr8 = this.nonContacts[idx12];
                            iArr8[idx22] = iArr8[idx22] + 1;
                            int count3 = encryptedChatsByUsersCount.get(user3.id, 0);
                            encryptedChatsByUsersCount.put(user3.id, count3 + 1);
                        }
                        int[] iArr9 = this.contacts[idx12];
                        iArr9[idx22] = iArr9[idx22] + 1;
                        int count32 = encryptedChatsByUsersCount.get(user3.id, 0);
                        encryptedChatsByUsersCount.put(user3.id, count32 + 1);
                    }
                    a7++;
                    usersDict2 = usersDict;
                    encryptedChats2 = encryptedChats;
                    N10 = N6;
                    encUsersDict8 = encUsersDict4;
                }
                encUsersDict2 = encUsersDict8;
                encUsersDict3 = usersDict2;
            } else {
                chatsToLoad = chatsToLoad4;
                encUsersDict2 = encUsersDict;
                encUsersDict3 = usersDict2;
            }
        }
        if (chatsToLoad.isEmpty()) {
            chatsDict2 = chatsDict;
        } else {
            ArrayList<TLRPC.Chat> chats5 = chats;
            getChatsInternal(TextUtils.join(str2, chatsToLoad), chats5);
            int a8 = 0;
            int N11 = chats5.size();
            while (a8 < N11) {
                TLRPC.Chat chat = chats5.get(a8);
                if (chat.migrated_to instanceof TLRPC.TL_inputChannel) {
                    chats2 = chats5;
                    N5 = N11;
                    chatsDict3 = chatsDict;
                } else if (ChatObject.isNotInChat(chat)) {
                    chats2 = chats5;
                    N5 = N11;
                    chatsDict3 = chatsDict;
                } else {
                    boolean muted3 = getMessagesController().isDialogMuted(-chat.id, chat);
                    int idx13 = dialogsByFolders4.get(-chat.id);
                    int idx23 = (!muted3 || this.dialogsWithMentions.indexOfKey(-chat.id) >= 0) ? 0 : 1;
                    if (!muted3) {
                        chats2 = chats5;
                    } else {
                        chats2 = chats5;
                        mutedDialogs.put(-chat.id, true);
                    }
                    if (idx13 == 1) {
                        N5 = N11;
                        archivedDialogs.put(-chat.id, true);
                    } else {
                        N5 = N11;
                    }
                    if (ChatObject.isChannel(chat) && !chat.megagroup) {
                        int[] iArr10 = this.channels[idx13];
                        iArr10[idx23] = iArr10[idx23] + 1;
                    } else {
                        int[] iArr11 = this.groups[idx13];
                        iArr11[idx23] = iArr11[idx23] + 1;
                    }
                    chatsDict3 = chatsDict;
                    chatsDict3.put(chat.id, chat);
                    a8++;
                    chatsDict = chatsDict3;
                    N11 = N5;
                    chats5 = chats2;
                }
                this.dialogsWithUnread.remove(-chat.id);
                this.dialogsWithMentions.remove(-chat.id);
                a8++;
                chatsDict = chatsDict3;
                N11 = N5;
                chats5 = chats2;
            }
            chatsDict2 = chatsDict;
        }
        int a9 = 0;
        int N12 = this.dialogFilters.size();
        while (a9 < N12 + 2) {
            if (a9 < N12) {
                filter = this.dialogFilters.get(a9);
                if (filter.pendingUnreadCount >= 0) {
                    encryptedToLoad = encryptedToLoad3;
                    dialogsByFolders = dialogsByFolders4;
                    chatsToLoad2 = chatsToLoad;
                    int i3 = N12;
                    N = a9;
                    a = i3;
                    int a10 = N + 1;
                    encryptedToLoad3 = encryptedToLoad;
                    chatsToLoad = chatsToLoad2;
                    dialogsByFolders4 = dialogsByFolders;
                    N12 = a;
                    a9 = a10;
                } else {
                    flags = filter.flags;
                }
            } else {
                filter = null;
                int flags2 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
                if (a9 == N12) {
                    if (!getNotificationsController().showBadgeMuted) {
                        flags2 |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
                    }
                    flags = flags2 | MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
                } else {
                    flags = flags2 | MessagesController.DIALOG_FILTER_FLAG_ONLY_ARCHIVED;
                }
            }
            int unreadCount = 0;
            if ((MessagesController.DIALOG_FILTER_FLAG_CONTACTS & flags) != 0) {
                if ((MessagesController.DIALOG_FILTER_FLAG_ONLY_ARCHIVED & flags) == 0) {
                    unreadCount = 0 + this.contacts[0][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.contacts[0][1];
                    }
                }
                if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED & flags) == 0) {
                    unreadCount += this.contacts[1][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.contacts[1][1];
                    }
                }
            }
            if ((MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS & flags) != 0) {
                if ((MessagesController.DIALOG_FILTER_FLAG_ONLY_ARCHIVED & flags) == 0) {
                    unreadCount += this.nonContacts[0][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.nonContacts[0][1];
                    }
                }
                if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED & flags) == 0) {
                    unreadCount += this.nonContacts[1][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.nonContacts[1][1];
                    }
                }
            }
            if ((MessagesController.DIALOG_FILTER_FLAG_GROUPS & flags) != 0) {
                if ((MessagesController.DIALOG_FILTER_FLAG_ONLY_ARCHIVED & flags) == 0) {
                    unreadCount += this.groups[0][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.groups[0][1];
                    }
                }
                if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED & flags) == 0) {
                    unreadCount += this.groups[1][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.groups[1][1];
                    }
                }
            }
            if ((MessagesController.DIALOG_FILTER_FLAG_CHANNELS & flags) != 0) {
                if ((MessagesController.DIALOG_FILTER_FLAG_ONLY_ARCHIVED & flags) == 0) {
                    unreadCount += this.channels[0][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.channels[0][1];
                    }
                }
                if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED & flags) == 0) {
                    unreadCount += this.channels[1][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.channels[1][1];
                    }
                }
            }
            if ((MessagesController.DIALOG_FILTER_FLAG_BOTS & flags) != 0) {
                if ((MessagesController.DIALOG_FILTER_FLAG_ONLY_ARCHIVED & flags) == 0) {
                    unreadCount += this.bots[0][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.bots[0][1];
                    }
                }
                if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED & flags) == 0) {
                    unreadCount += this.bots[1][0];
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0) {
                        unreadCount += this.bots[1][1];
                    }
                }
            }
            if (filter != null) {
                int b2 = 0;
                int N24 = filter.alwaysShow.size();
                while (b2 < N24) {
                    long did3 = filter.alwaysShow.get(b2).longValue();
                    if (DialogObject.isUserDialog(did3)) {
                        int i4 = 0;
                        while (true) {
                            encryptedToLoad2 = encryptedToLoad3;
                            if (i4 >= 2) {
                                break;
                            }
                            LongSparseArray<TLRPC.User> dict = i4 == 0 ? encUsersDict3 : encUsersDict2;
                            int N25 = N24;
                            ArrayList<Long> chatsToLoad5 = chatsToLoad;
                            long did4 = did3;
                            TLRPC.User user4 = dict.get(did4);
                            if (user4 == null) {
                                a3 = a9;
                                N4 = N12;
                                filter5 = filter;
                                dialogsByFolders3 = dialogsByFolders4;
                            } else {
                                if (i4 == 0) {
                                    dialogsByFolders3 = dialogsByFolders4;
                                    count2 = 1;
                                } else {
                                    dialogsByFolders3 = dialogsByFolders4;
                                    count2 = encryptedChatsByUsersCount.get(did4, 0);
                                    if (count2 == 0) {
                                        a3 = a9;
                                        N4 = N12;
                                        filter5 = filter;
                                    }
                                }
                                a3 = a9;
                                if (user4.bot) {
                                    flag4 = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                                } else {
                                    if (!user4.self && !user4.contact) {
                                        flag4 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                                    }
                                    flag4 = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                                }
                                if ((flags & flag4) == 0) {
                                    unreadCount += count2;
                                    N4 = N12;
                                    filter5 = filter;
                                } else {
                                    if ((flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                                        N4 = N12;
                                        filter5 = filter;
                                        if (mutedDialogs.indexOfKey(user4.id) >= 0) {
                                            unreadCount += count2;
                                        }
                                    } else {
                                        N4 = N12;
                                        filter5 = filter;
                                    }
                                    int N13 = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
                                    if ((N13 & flags) != 0 && archivedDialogs.indexOfKey(user4.id) >= 0) {
                                        unreadCount += count2;
                                    }
                                }
                            }
                            i4++;
                            encryptedToLoad3 = encryptedToLoad2;
                            dialogsByFolders4 = dialogsByFolders3;
                            a9 = a3;
                            N12 = N4;
                            filter = filter5;
                            did3 = did4;
                            chatsToLoad = chatsToLoad5;
                            N24 = N25;
                        }
                        a2 = a9;
                        N3 = N12;
                        filter4 = filter;
                        N23 = N24;
                        chatsToLoad3 = chatsToLoad;
                        dialogsByFolders2 = dialogsByFolders4;
                    } else {
                        a2 = a9;
                        N3 = N12;
                        filter4 = filter;
                        encryptedToLoad2 = encryptedToLoad3;
                        N23 = N24;
                        chatsToLoad3 = chatsToLoad;
                        dialogsByFolders2 = dialogsByFolders4;
                        TLRPC.Chat chat2 = chatsDict2.get(-did3);
                        if (chat2 != null) {
                            if (ChatObject.isChannel(chat2) && !chat2.megagroup) {
                                flag3 = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                            } else {
                                flag3 = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                            }
                            if ((flags & flag3) == 0) {
                                unreadCount++;
                            } else if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) != 0 && mutedDialogs.indexOfKey(-chat2.id) >= 0 && this.dialogsWithMentions.indexOfKey(-chat2.id) < 0) {
                                unreadCount++;
                            } else if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED & flags) != 0 && archivedDialogs.indexOfKey(-chat2.id) >= 0) {
                                unreadCount++;
                            }
                        }
                    }
                    b2++;
                    encryptedToLoad3 = encryptedToLoad2;
                    chatsToLoad = chatsToLoad3;
                    dialogsByFolders4 = dialogsByFolders2;
                    N24 = N23;
                    a9 = a2;
                    N12 = N3;
                    filter = filter4;
                }
                int a11 = a9;
                int N14 = N12;
                encryptedToLoad = encryptedToLoad3;
                dialogsByFolders = dialogsByFolders4;
                chatsToLoad2 = chatsToLoad;
                int b3 = 0;
                int N26 = filter.neverShow.size();
                while (b3 < N26) {
                    long did5 = filter.neverShow.get(b3).longValue();
                    if (DialogObject.isUserDialog(did5)) {
                        int i5 = 0;
                        while (i5 < 2) {
                            LongSparseArray<TLRPC.User> dict2 = i5 == 0 ? encUsersDict3 : encUsersDict2;
                            TLRPC.User user5 = dict2.get(did5);
                            if (user5 == null) {
                                N22 = N26;
                                filter3 = filter;
                            } else {
                                if (i5 == 0) {
                                    count = 1;
                                } else {
                                    count = encryptedChatsByUsersCount.get(did5, 0);
                                    if (count == 0) {
                                        N22 = N26;
                                        filter3 = filter;
                                    }
                                }
                                if (user5.bot) {
                                    flag2 = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                                } else {
                                    if (!user5.self && !user5.contact) {
                                        flag2 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                                    }
                                    flag2 = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                                }
                                if ((flags & flag2) == 0) {
                                    N22 = N26;
                                    filter3 = filter;
                                } else {
                                    if ((flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
                                        N22 = N26;
                                        filter3 = filter;
                                        if (archivedDialogs.indexOfKey(user5.id) < 0) {
                                        }
                                    } else {
                                        N22 = N26;
                                        filter3 = filter;
                                    }
                                    int N27 = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
                                    if ((N27 & flags) == 0 || mutedDialogs.indexOfKey(user5.id) < 0) {
                                        unreadCount -= count;
                                    }
                                }
                            }
                            i5++;
                            N26 = N22;
                            filter = filter3;
                        }
                        N2 = N26;
                        filter2 = filter;
                    } else {
                        N2 = N26;
                        filter2 = filter;
                        TLRPC.Chat chat3 = chatsDict2.get(-did5);
                        if (chat3 != null) {
                            if (ChatObject.isChannel(chat3) && !chat3.megagroup) {
                                flag = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                            } else {
                                flag = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                            }
                            if ((flags & flag) != 0 && (((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED & flags) == 0 || archivedDialogs.indexOfKey(-chat3.id) < 0) && ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & flags) == 0 || mutedDialogs.indexOfKey(-chat3.id) < 0 || this.dialogsWithMentions.indexOfKey(-chat3.id) >= 0))) {
                                unreadCount--;
                            }
                        }
                    }
                    b3++;
                    N26 = N2;
                    filter = filter2;
                }
                filter.pendingUnreadCount = unreadCount;
                if (!apply) {
                    N = a11;
                    a = N14;
                } else {
                    filter.unreadCount = unreadCount;
                    N = a11;
                    a = N14;
                }
            } else {
                int N15 = N12;
                encryptedToLoad = encryptedToLoad3;
                dialogsByFolders = dialogsByFolders4;
                chatsToLoad2 = chatsToLoad;
                N = a9;
                a = N15;
                if (N == a) {
                    this.pendingMainUnreadCount = unreadCount;
                    if (apply) {
                        this.mainUnreadCount = unreadCount;
                    }
                } else if (N == a + 1) {
                    this.pendingArchiveUnreadCount = unreadCount;
                    if (apply) {
                        this.archiveUnreadCount = unreadCount;
                    }
                }
            }
            int a102 = N + 1;
            encryptedToLoad3 = encryptedToLoad;
            chatsToLoad = chatsToLoad2;
            dialogsByFolders4 = dialogsByFolders;
            N12 = a;
            a9 = a102;
        }
    }

    private void saveDialogFilterInternal(MessagesController.DialogFilter filter, boolean atBegin, boolean peers) {
        try {
            if (!this.dialogFilters.contains(filter)) {
                if (atBegin) {
                    this.dialogFilters.add(0, filter);
                } else {
                    this.dialogFilters.add(filter);
                }
                this.dialogFiltersMap.put(filter.id, filter);
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO dialog_filter VALUES(?, ?, ?, ?, ?)");
            state.bindInteger(1, filter.id);
            state.bindInteger(2, filter.order);
            state.bindInteger(3, filter.unreadCount);
            state.bindInteger(4, filter.flags);
            state.bindString(5, filter.id == 0 ? "ALL_CHATS" : filter.name);
            state.step();
            state.dispose();
            if (peers) {
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteDatabase.executeFast("DELETE FROM dialog_filter_ep WHERE id = " + filter.id).stepThis().dispose();
                SQLiteDatabase sQLiteDatabase2 = this.database;
                sQLiteDatabase2.executeFast("DELETE FROM dialog_filter_pin_v2 WHERE id = " + filter.id).stepThis().dispose();
                this.database.beginTransaction();
                SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO dialog_filter_pin_v2 VALUES(?, ?, ?)");
                int N = filter.alwaysShow.size();
                for (int a = 0; a < N; a++) {
                    long key = filter.alwaysShow.get(a).longValue();
                    state2.requery();
                    state2.bindInteger(1, filter.id);
                    state2.bindLong(2, key);
                    state2.bindInteger(3, filter.pinnedDialogs.get(key, Integer.MIN_VALUE));
                    state2.step();
                }
                int N2 = filter.pinnedDialogs.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    long key2 = filter.pinnedDialogs.keyAt(a2);
                    if (DialogObject.isEncryptedDialog(key2)) {
                        state2.requery();
                        state2.bindInteger(1, filter.id);
                        state2.bindLong(2, key2);
                        state2.bindInteger(3, filter.pinnedDialogs.valueAt(a2));
                        state2.step();
                    }
                }
                state2.dispose();
                SQLitePreparedStatement state3 = this.database.executeFast("REPLACE INTO dialog_filter_ep VALUES(?, ?)");
                int N3 = filter.neverShow.size();
                for (int a3 = 0; a3 < N3; a3++) {
                    state3.requery();
                    state3.bindInteger(1, filter.id);
                    state3.bindLong(2, filter.neverShow.get(a3).longValue());
                    state3.step();
                }
                state3.dispose();
                this.database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void checkLoadedRemoteFilters(final TLRPC.Vector vector) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda85
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m903x2a713f38(vector);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:155:0x03aa A[Catch: Exception -> 0x0684, TryCatch #1 {Exception -> 0x0684, blocks: (B:9:0x006f, B:13:0x008a, B:17:0x0091, B:21:0x0098, B:25:0x009f, B:29:0x00a6, B:33:0x00ad, B:37:0x00b4, B:41:0x00bb, B:43:0x00c9, B:45:0x00df, B:47:0x00e7, B:49:0x00eb, B:50:0x00f0, B:52:0x010f, B:54:0x0128, B:57:0x0139, B:60:0x014a, B:63:0x0168, B:66:0x017d, B:67:0x018c, B:72:0x01a7, B:74:0x01b0, B:77:0x01bd, B:78:0x01cb, B:83:0x01d4, B:84:0x01d7, B:86:0x01db, B:87:0x01de, B:89:0x01e2, B:91:0x01f8, B:93:0x020c, B:94:0x0213, B:96:0x0219, B:97:0x021c, B:98:0x021e, B:99:0x0223, B:101:0x022b, B:102:0x0230, B:104:0x0238, B:106:0x0248, B:109:0x0269, B:111:0x027b, B:113:0x0283, B:114:0x0289, B:117:0x02a7, B:118:0x02af, B:120:0x02b5, B:122:0x02df, B:124:0x02e6, B:126:0x02f2, B:128:0x02fe, B:130:0x0308, B:131:0x030b, B:133:0x0311, B:137:0x032a, B:139:0x0336, B:140:0x0339, B:141:0x033b, B:143:0x0351, B:145:0x0359, B:146:0x035c, B:148:0x0362, B:151:0x0372, B:152:0x0380, B:153:0x0398, B:155:0x03aa, B:157:0x03bc, B:159:0x03c2, B:162:0x03d4, B:165:0x03de, B:168:0x03f5, B:172:0x041f, B:174:0x0428, B:176:0x043c, B:177:0x0443, B:179:0x0449, B:180:0x044c, B:181:0x044e, B:182:0x0453, B:184:0x045b, B:185:0x0460, B:187:0x047d, B:188:0x0483, B:192:0x04ab, B:193:0x04ae, B:195:0x04b2, B:196:0x04b5, B:197:0x04b7, B:199:0x04be, B:201:0x04ca, B:203:0x04d6, B:204:0x04d9, B:206:0x04df, B:208:0x04eb, B:210:0x04f1, B:211:0x04f4, B:212:0x04f6, B:214:0x050c, B:215:0x050f, B:217:0x0515, B:219:0x0520, B:220:0x0528, B:221:0x053a, B:222:0x0548, B:223:0x055f, B:226:0x0578), top: B:259:0x006f }] */
    /* JADX WARN: Removed duplicated region for block: B:156:0x03ba  */
    /* JADX WARN: Removed duplicated region for block: B:159:0x03c2 A[Catch: Exception -> 0x0684, TryCatch #1 {Exception -> 0x0684, blocks: (B:9:0x006f, B:13:0x008a, B:17:0x0091, B:21:0x0098, B:25:0x009f, B:29:0x00a6, B:33:0x00ad, B:37:0x00b4, B:41:0x00bb, B:43:0x00c9, B:45:0x00df, B:47:0x00e7, B:49:0x00eb, B:50:0x00f0, B:52:0x010f, B:54:0x0128, B:57:0x0139, B:60:0x014a, B:63:0x0168, B:66:0x017d, B:67:0x018c, B:72:0x01a7, B:74:0x01b0, B:77:0x01bd, B:78:0x01cb, B:83:0x01d4, B:84:0x01d7, B:86:0x01db, B:87:0x01de, B:89:0x01e2, B:91:0x01f8, B:93:0x020c, B:94:0x0213, B:96:0x0219, B:97:0x021c, B:98:0x021e, B:99:0x0223, B:101:0x022b, B:102:0x0230, B:104:0x0238, B:106:0x0248, B:109:0x0269, B:111:0x027b, B:113:0x0283, B:114:0x0289, B:117:0x02a7, B:118:0x02af, B:120:0x02b5, B:122:0x02df, B:124:0x02e6, B:126:0x02f2, B:128:0x02fe, B:130:0x0308, B:131:0x030b, B:133:0x0311, B:137:0x032a, B:139:0x0336, B:140:0x0339, B:141:0x033b, B:143:0x0351, B:145:0x0359, B:146:0x035c, B:148:0x0362, B:151:0x0372, B:152:0x0380, B:153:0x0398, B:155:0x03aa, B:157:0x03bc, B:159:0x03c2, B:162:0x03d4, B:165:0x03de, B:168:0x03f5, B:172:0x041f, B:174:0x0428, B:176:0x043c, B:177:0x0443, B:179:0x0449, B:180:0x044c, B:181:0x044e, B:182:0x0453, B:184:0x045b, B:185:0x0460, B:187:0x047d, B:188:0x0483, B:192:0x04ab, B:193:0x04ae, B:195:0x04b2, B:196:0x04b5, B:197:0x04b7, B:199:0x04be, B:201:0x04ca, B:203:0x04d6, B:204:0x04d9, B:206:0x04df, B:208:0x04eb, B:210:0x04f1, B:211:0x04f4, B:212:0x04f6, B:214:0x050c, B:215:0x050f, B:217:0x0515, B:219:0x0520, B:220:0x0528, B:221:0x053a, B:222:0x0548, B:223:0x055f, B:226:0x0578), top: B:259:0x006f }] */
    /* JADX WARN: Removed duplicated region for block: B:160:0x03d0  */
    /* JADX WARN: Removed duplicated region for block: B:162:0x03d4 A[Catch: Exception -> 0x0684, TryCatch #1 {Exception -> 0x0684, blocks: (B:9:0x006f, B:13:0x008a, B:17:0x0091, B:21:0x0098, B:25:0x009f, B:29:0x00a6, B:33:0x00ad, B:37:0x00b4, B:41:0x00bb, B:43:0x00c9, B:45:0x00df, B:47:0x00e7, B:49:0x00eb, B:50:0x00f0, B:52:0x010f, B:54:0x0128, B:57:0x0139, B:60:0x014a, B:63:0x0168, B:66:0x017d, B:67:0x018c, B:72:0x01a7, B:74:0x01b0, B:77:0x01bd, B:78:0x01cb, B:83:0x01d4, B:84:0x01d7, B:86:0x01db, B:87:0x01de, B:89:0x01e2, B:91:0x01f8, B:93:0x020c, B:94:0x0213, B:96:0x0219, B:97:0x021c, B:98:0x021e, B:99:0x0223, B:101:0x022b, B:102:0x0230, B:104:0x0238, B:106:0x0248, B:109:0x0269, B:111:0x027b, B:113:0x0283, B:114:0x0289, B:117:0x02a7, B:118:0x02af, B:120:0x02b5, B:122:0x02df, B:124:0x02e6, B:126:0x02f2, B:128:0x02fe, B:130:0x0308, B:131:0x030b, B:133:0x0311, B:137:0x032a, B:139:0x0336, B:140:0x0339, B:141:0x033b, B:143:0x0351, B:145:0x0359, B:146:0x035c, B:148:0x0362, B:151:0x0372, B:152:0x0380, B:153:0x0398, B:155:0x03aa, B:157:0x03bc, B:159:0x03c2, B:162:0x03d4, B:165:0x03de, B:168:0x03f5, B:172:0x041f, B:174:0x0428, B:176:0x043c, B:177:0x0443, B:179:0x0449, B:180:0x044c, B:181:0x044e, B:182:0x0453, B:184:0x045b, B:185:0x0460, B:187:0x047d, B:188:0x0483, B:192:0x04ab, B:193:0x04ae, B:195:0x04b2, B:196:0x04b5, B:197:0x04b7, B:199:0x04be, B:201:0x04ca, B:203:0x04d6, B:204:0x04d9, B:206:0x04df, B:208:0x04eb, B:210:0x04f1, B:211:0x04f4, B:212:0x04f6, B:214:0x050c, B:215:0x050f, B:217:0x0515, B:219:0x0520, B:220:0x0528, B:221:0x053a, B:222:0x0548, B:223:0x055f, B:226:0x0578), top: B:259:0x006f }] */
    /* JADX WARN: Removed duplicated region for block: B:163:0x03da  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x03de A[Catch: Exception -> 0x0684, TryCatch #1 {Exception -> 0x0684, blocks: (B:9:0x006f, B:13:0x008a, B:17:0x0091, B:21:0x0098, B:25:0x009f, B:29:0x00a6, B:33:0x00ad, B:37:0x00b4, B:41:0x00bb, B:43:0x00c9, B:45:0x00df, B:47:0x00e7, B:49:0x00eb, B:50:0x00f0, B:52:0x010f, B:54:0x0128, B:57:0x0139, B:60:0x014a, B:63:0x0168, B:66:0x017d, B:67:0x018c, B:72:0x01a7, B:74:0x01b0, B:77:0x01bd, B:78:0x01cb, B:83:0x01d4, B:84:0x01d7, B:86:0x01db, B:87:0x01de, B:89:0x01e2, B:91:0x01f8, B:93:0x020c, B:94:0x0213, B:96:0x0219, B:97:0x021c, B:98:0x021e, B:99:0x0223, B:101:0x022b, B:102:0x0230, B:104:0x0238, B:106:0x0248, B:109:0x0269, B:111:0x027b, B:113:0x0283, B:114:0x0289, B:117:0x02a7, B:118:0x02af, B:120:0x02b5, B:122:0x02df, B:124:0x02e6, B:126:0x02f2, B:128:0x02fe, B:130:0x0308, B:131:0x030b, B:133:0x0311, B:137:0x032a, B:139:0x0336, B:140:0x0339, B:141:0x033b, B:143:0x0351, B:145:0x0359, B:146:0x035c, B:148:0x0362, B:151:0x0372, B:152:0x0380, B:153:0x0398, B:155:0x03aa, B:157:0x03bc, B:159:0x03c2, B:162:0x03d4, B:165:0x03de, B:168:0x03f5, B:172:0x041f, B:174:0x0428, B:176:0x043c, B:177:0x0443, B:179:0x0449, B:180:0x044c, B:181:0x044e, B:182:0x0453, B:184:0x045b, B:185:0x0460, B:187:0x047d, B:188:0x0483, B:192:0x04ab, B:193:0x04ae, B:195:0x04b2, B:196:0x04b5, B:197:0x04b7, B:199:0x04be, B:201:0x04ca, B:203:0x04d6, B:204:0x04d9, B:206:0x04df, B:208:0x04eb, B:210:0x04f1, B:211:0x04f4, B:212:0x04f6, B:214:0x050c, B:215:0x050f, B:217:0x0515, B:219:0x0520, B:220:0x0528, B:221:0x053a, B:222:0x0548, B:223:0x055f, B:226:0x0578), top: B:259:0x006f }] */
    /* JADX WARN: Removed duplicated region for block: B:166:0x03ea  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x01b0 A[Catch: Exception -> 0x0684, TryCatch #1 {Exception -> 0x0684, blocks: (B:9:0x006f, B:13:0x008a, B:17:0x0091, B:21:0x0098, B:25:0x009f, B:29:0x00a6, B:33:0x00ad, B:37:0x00b4, B:41:0x00bb, B:43:0x00c9, B:45:0x00df, B:47:0x00e7, B:49:0x00eb, B:50:0x00f0, B:52:0x010f, B:54:0x0128, B:57:0x0139, B:60:0x014a, B:63:0x0168, B:66:0x017d, B:67:0x018c, B:72:0x01a7, B:74:0x01b0, B:77:0x01bd, B:78:0x01cb, B:83:0x01d4, B:84:0x01d7, B:86:0x01db, B:87:0x01de, B:89:0x01e2, B:91:0x01f8, B:93:0x020c, B:94:0x0213, B:96:0x0219, B:97:0x021c, B:98:0x021e, B:99:0x0223, B:101:0x022b, B:102:0x0230, B:104:0x0238, B:106:0x0248, B:109:0x0269, B:111:0x027b, B:113:0x0283, B:114:0x0289, B:117:0x02a7, B:118:0x02af, B:120:0x02b5, B:122:0x02df, B:124:0x02e6, B:126:0x02f2, B:128:0x02fe, B:130:0x0308, B:131:0x030b, B:133:0x0311, B:137:0x032a, B:139:0x0336, B:140:0x0339, B:141:0x033b, B:143:0x0351, B:145:0x0359, B:146:0x035c, B:148:0x0362, B:151:0x0372, B:152:0x0380, B:153:0x0398, B:155:0x03aa, B:157:0x03bc, B:159:0x03c2, B:162:0x03d4, B:165:0x03de, B:168:0x03f5, B:172:0x041f, B:174:0x0428, B:176:0x043c, B:177:0x0443, B:179:0x0449, B:180:0x044c, B:181:0x044e, B:182:0x0453, B:184:0x045b, B:185:0x0460, B:187:0x047d, B:188:0x0483, B:192:0x04ab, B:193:0x04ae, B:195:0x04b2, B:196:0x04b5, B:197:0x04b7, B:199:0x04be, B:201:0x04ca, B:203:0x04d6, B:204:0x04d9, B:206:0x04df, B:208:0x04eb, B:210:0x04f1, B:211:0x04f4, B:212:0x04f6, B:214:0x050c, B:215:0x050f, B:217:0x0515, B:219:0x0520, B:220:0x0528, B:221:0x053a, B:222:0x0548, B:223:0x055f, B:226:0x0578), top: B:259:0x006f }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01d2  */
    /* renamed from: lambda$checkLoadedRemoteFilters$43$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m903x2a713f38(org.telegram.tgnet.TLRPC.Vector r41) {
        /*
            Method dump skipped, instructions count: 1677
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m903x2a713f38(org.telegram.tgnet.TLRPC$Vector):void");
    }

    public static /* synthetic */ int lambda$checkLoadedRemoteFilters$42(LongSparseIntArray pinnedDialogs, Long o1, Long o2) {
        int idx1 = pinnedDialogs.get(o1.longValue());
        int idx2 = pinnedDialogs.get(o2.longValue());
        if (idx1 > idx2) {
            return 1;
        }
        if (idx1 < idx2) {
            return -1;
        }
        return 0;
    }

    /* renamed from: processLoadedFilterPeersInternal */
    public void m1008x4fbc8d3(TLRPC.messages_Dialogs pinnedDialogs, TLRPC.messages_Dialogs pinnedRemoteDialogs, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, ArrayList<MessagesController.DialogFilter> filtersToSave, SparseArray<MessagesController.DialogFilter> filtersToDelete, ArrayList<Integer> filtersOrder, HashMap<Integer, HashSet<Long>> filterDialogRemovals, HashMap<Integer, HashSet<Long>> filterUserRemovals, HashSet<Integer> filtersUnreadCounterReset) {
        boolean anythingChanged = false;
        putUsersAndChats(users, chats, true, false);
        int N = filtersToDelete.size();
        for (int a = 0; a < N; a++) {
            m924xc88f6158(filtersToDelete.valueAt(a));
            anythingChanged = true;
        }
        Iterator<Integer> it = filtersUnreadCounterReset.iterator();
        while (it.hasNext()) {
            Integer id = it.next();
            MessagesController.DialogFilter filter = this.dialogFiltersMap.get(id.intValue());
            if (filter != null) {
                filter.pendingUnreadCount = -1;
            }
        }
        for (Map.Entry<Integer, HashSet<Long>> entry : filterUserRemovals.entrySet()) {
            MessagesController.DialogFilter filter2 = this.dialogFiltersMap.get(entry.getKey().intValue());
            if (filter2 != null) {
                HashSet<Long> set = entry.getValue();
                filter2.alwaysShow.removeAll(set);
                filter2.neverShow.removeAll(set);
                anythingChanged = true;
            }
        }
        for (Map.Entry<Integer, HashSet<Long>> entry2 : filterDialogRemovals.entrySet()) {
            MessagesController.DialogFilter filter3 = this.dialogFiltersMap.get(entry2.getKey().intValue());
            if (filter3 != null) {
                Iterator<Long> it2 = entry2.getValue().iterator();
                while (it2.hasNext()) {
                    Long id2 = it2.next();
                    filter3.pinnedDialogs.delete(id2.longValue());
                }
                anythingChanged = true;
            }
        }
        int N2 = filtersToSave.size();
        for (int a2 = 0; a2 < N2; a2++) {
            saveDialogFilterInternal(filtersToSave.get(a2), false, true);
            anythingChanged = true;
        }
        boolean orderChanged = false;
        int N3 = this.dialogFilters.size();
        for (int a3 = 0; a3 < N3; a3++) {
            MessagesController.DialogFilter filter4 = this.dialogFilters.get(a3);
            int order = filtersOrder.indexOf(Integer.valueOf(filter4.id));
            if (filter4.order != order) {
                filter4.order = order;
                anythingChanged = true;
                orderChanged = true;
            }
        }
        if (orderChanged) {
            Collections.sort(this.dialogFilters, MessagesStorage$$ExternalSyntheticLambda109.INSTANCE);
            saveDialogFiltersOrderInternal();
        }
        int remote = anythingChanged ? 1 : 2;
        calcUnreadCounters(true);
        getMessagesController().processLoadedDialogFilters(new ArrayList<>(this.dialogFilters), pinnedDialogs, pinnedRemoteDialogs, users, chats, null, remote);
    }

    public static /* synthetic */ int lambda$processLoadedFilterPeersInternal$44(MessagesController.DialogFilter o1, MessagesController.DialogFilter o2) {
        if (o1.order > o2.order) {
            return 1;
        }
        if (o1.order < o2.order) {
            return -1;
        }
        return 0;
    }

    public void processLoadedFilterPeers(final TLRPC.messages_Dialogs pinnedDialogs, final TLRPC.messages_Dialogs pinnedRemoteDialogs, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final ArrayList<MessagesController.DialogFilter> filtersToSave, final SparseArray<MessagesController.DialogFilter> filtersToDelete, final ArrayList<Integer> filtersOrder, final HashMap<Integer, HashSet<Long>> filterDialogRemovals, final HashMap<Integer, HashSet<Long>> filterUserRemovals, final HashSet<Integer> filtersUnreadCounterReset) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda89
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1008x4fbc8d3(pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
            }
        });
    }

    /* renamed from: deleteDialogFilterInternal */
    public void m924xc88f6158(MessagesController.DialogFilter filter) {
        try {
            this.dialogFilters.remove(filter);
            this.dialogFiltersMap.remove(filter.id);
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM dialog_filter WHERE id = " + filter.id).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase2 = this.database;
            sQLiteDatabase2.executeFast("DELETE FROM dialog_filter_ep WHERE id = " + filter.id).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase3 = this.database;
            sQLiteDatabase3.executeFast("DELETE FROM dialog_filter_pin_v2 WHERE id = " + filter.id).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteDialogFilter(final MessagesController.DialogFilter filter) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda58
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m924xc88f6158(filter);
            }
        });
    }

    public void saveDialogFilter(final MessagesController.DialogFilter filter, final boolean atBegin, final boolean peers) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda59
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1042x711df8a8(filter, atBegin, peers);
            }
        });
    }

    /* renamed from: lambda$saveDialogFilter$48$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1042x711df8a8(MessagesController.DialogFilter filter, boolean atBegin, boolean peers) {
        saveDialogFilterInternal(filter, atBegin, peers);
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda114
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1041x1a0007c9();
            }
        });
    }

    /* renamed from: lambda$saveDialogFilter$47$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1041x1a0007c9() {
        ArrayList<MessagesController.DialogFilter> filters = getMessagesController().dialogFilters;
        int N = filters.size();
        for (int a = 0; a < N; a++) {
            filters.get(a).unreadCount = filters.get(a).pendingUnreadCount;
        }
        int a2 = this.pendingMainUnreadCount;
        this.mainUnreadCount = a2;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public void saveDialogFiltersOrderInternal() {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE dialog_filter SET ord = ?, flags = ? WHERE id = ?");
            int N = this.dialogFilters.size();
            for (int a = 0; a < N; a++) {
                MessagesController.DialogFilter filter = this.dialogFilters.get(a);
                state.requery();
                state.bindInteger(1, filter.order);
                state.bindInteger(2, filter.flags);
                state.bindInteger(3, filter.id);
                state.step();
            }
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveDialogFiltersOrder() {
        final ArrayList<MessagesController.DialogFilter> filtersFinal = new ArrayList<>(getMessagesController().dialogFilters);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1043x583f2ae2(filtersFinal);
            }
        });
    }

    /* renamed from: lambda$saveDialogFiltersOrder$49$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1043x583f2ae2(ArrayList filtersFinal) {
        this.dialogFilters.clear();
        this.dialogFiltersMap.clear();
        this.dialogFilters.addAll(filtersFinal);
        for (int i = 0; i < filtersFinal.size(); i++) {
            ((MessagesController.DialogFilter) filtersFinal.get(i)).order = i;
            this.dialogFiltersMap.put(((MessagesController.DialogFilter) filtersFinal.get(i)).id, (MessagesController.DialogFilter) filtersFinal.get(i));
        }
        saveDialogFiltersOrderInternal();
    }

    protected static void addReplyMessages(TLRPC.Message message, LongSparseArray<SparseArray<ArrayList<TLRPC.Message>>> replyMessageOwners, LongSparseArray<ArrayList<Integer>> dialogReplyMessagesIds) {
        int i = message.reply_to.reply_to_msg_id;
        long dialogId = MessageObject.getReplyToDialogId(message);
        SparseArray<ArrayList<TLRPC.Message>> sparseArray = replyMessageOwners.get(dialogId);
        ArrayList<Integer> ids = dialogReplyMessagesIds.get(dialogId);
        if (sparseArray == null) {
            sparseArray = new SparseArray<>();
            replyMessageOwners.put(dialogId, sparseArray);
        }
        if (ids == null) {
            ids = new ArrayList<>();
            dialogReplyMessagesIds.put(dialogId, ids);
        }
        ArrayList<TLRPC.Message> arrayList = sparseArray.get(message.reply_to.reply_to_msg_id);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            sparseArray.put(message.reply_to.reply_to_msg_id, arrayList);
            if (!ids.contains(Integer.valueOf(message.reply_to.reply_to_msg_id))) {
                ids.add(Integer.valueOf(message.reply_to.reply_to_msg_id));
            }
        }
        arrayList.add(message);
    }

    protected void loadReplyMessages(LongSparseArray<SparseArray<ArrayList<TLRPC.Message>>> replyMessageOwners, LongSparseArray<ArrayList<Integer>> dialogReplyMessagesIds, ArrayList<Long> usersToLoad, ArrayList<Long> chatsToLoad, boolean scheduled) throws SQLiteException {
        SQLiteCursor cursor;
        MessagesStorage messagesStorage = this;
        LongSparseArray<SparseArray<ArrayList<TLRPC.Message>>> longSparseArray = replyMessageOwners;
        if (replyMessageOwners.isEmpty()) {
            return;
        }
        int b = 0;
        int N2 = replyMessageOwners.size();
        while (b < N2) {
            long dialogId = longSparseArray.keyAt(b);
            SparseArray<ArrayList<TLRPC.Message>> owners = longSparseArray.valueAt(b);
            ArrayList<Integer> ids = dialogReplyMessagesIds.get(dialogId);
            if (ids != null) {
                boolean z = false;
                if (scheduled) {
                    cursor = messagesStorage.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM scheduled_messages_v2 WHERE mid IN(%s) AND uid = %d", TextUtils.join(",", ids), Long.valueOf(dialogId)), new Object[0]);
                } else {
                    cursor = messagesStorage.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages_v2 WHERE mid IN(%s) AND uid = %d", TextUtils.join(",", ids), Long.valueOf(dialogId)), new Object[0]);
                }
                while (cursor.next()) {
                    int i = z ? 1 : 0;
                    int i2 = z ? 1 : 0;
                    NativeByteBuffer data = cursor.byteBufferValue(i);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                        message.readAttachPath(data, getUserConfig().clientUserId);
                        data.reuse();
                        message.id = cursor.intValue(1);
                        message.date = cursor.intValue(2);
                        message.dialog_id = cursor.longValue(3);
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        ArrayList<TLRPC.Message> arrayList = owners.get(message.id);
                        if (arrayList != null) {
                            int N = arrayList.size();
                            for (int a = 0; a < N; a++) {
                                TLRPC.Message m = arrayList.get(a);
                                m.replyMessage = message;
                                MessageObject.getDialogId(message);
                            }
                        }
                    }
                    z = false;
                }
                cursor.dispose();
            }
            b++;
            messagesStorage = this;
            longSparseArray = replyMessageOwners;
        }
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m988xaddb54f4();
            }
        });
    }

    /* renamed from: lambda$loadUnreadMessages$51$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m988xaddb54f4() {
        String str;
        LongSparseArray<SparseArray<ArrayList<TLRPC.Message>>> replyMessageOwners;
        ArrayList<TLRPC.EncryptedChat> encryptedChats;
        ArrayList<TLRPC.User> users;
        ArrayList<MessageObject> pushMessages;
        ArrayList<TLRPC.Message> messages;
        ArrayList<TLRPC.Chat> chats;
        ArrayList<TLRPC.EncryptedChat> encryptedChats2;
        String str2;
        ArrayList<TLRPC.User> users2;
        ArrayList<TLRPC.EncryptedChat> encryptedChats3;
        ArrayList<TLRPC.User> users3;
        ArrayList<MessageObject> pushMessages2;
        int maxDate;
        String str3;
        LongSparseArray<SparseArray<ArrayList<TLRPC.Message>>> replyMessageOwners2;
        LongSparseArray<ArrayList<Integer>> dialogReplyMessagesIds;
        ArrayList<MessageObject> pushMessages3;
        int currentTime;
        StringBuilder ids;
        NativeByteBuffer data;
        try {
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            ArrayList<Integer> encryptedChatIds = new ArrayList<>();
            final LongSparseArray<Integer> pushDialogs = new LongSparseArray<>();
            int i = 0;
            SQLiteCursor cursor = this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count > 0", new Object[0]);
            StringBuilder ids2 = new StringBuilder();
            int currentTime2 = getConnectionsManager().getCurrentTime();
            while (true) {
                str = ",";
                if (!cursor.next()) {
                    break;
                }
                long flags = cursor.longValue(2);
                boolean muted = (flags & 1) != 0;
                int mutedUntil = (int) (flags >> 32);
                if (cursor.isNull(2) || !muted || (mutedUntil != 0 && mutedUntil < currentTime2)) {
                    long did = cursor.longValue(i);
                    if (!DialogObject.isFolderDialogId(did)) {
                        int count = cursor.intValue(1);
                        pushDialogs.put(did, Integer.valueOf(count));
                        if (ids2.length() != 0) {
                            ids2.append(str);
                        }
                        ids2.append(did);
                        if (DialogObject.isEncryptedDialog(did)) {
                            int encryptedChatId = DialogObject.getEncryptedChatId(did);
                            if (!encryptedChatIds.contains(Integer.valueOf(encryptedChatId))) {
                                encryptedChatIds.add(Integer.valueOf(encryptedChatId));
                            }
                        } else if (DialogObject.isUserDialog(did)) {
                            if (!usersToLoad.contains(Long.valueOf(did))) {
                                usersToLoad.add(Long.valueOf(did));
                            }
                        } else if (!chatsToLoad.contains(Long.valueOf(-did))) {
                            chatsToLoad.add(Long.valueOf(-did));
                        }
                    }
                }
                i = 0;
            }
            cursor.dispose();
            LongSparseArray<SparseArray<ArrayList<TLRPC.Message>>> replyMessageOwners3 = new LongSparseArray<>();
            LongSparseArray<ArrayList<Integer>> dialogReplyMessagesIds2 = new LongSparseArray<>();
            ArrayList<TLRPC.Message> messages2 = new ArrayList<>();
            ArrayList<MessageObject> pushMessages4 = new ArrayList<>();
            ArrayList<TLRPC.User> users4 = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats2 = new ArrayList<>();
            ArrayList<TLRPC.EncryptedChat> encryptedChats4 = new ArrayList<>();
            if (ids2.length() <= 0) {
                replyMessageOwners = replyMessageOwners3;
                chats = chats2;
                encryptedChats = encryptedChats4;
                messages = messages2;
                users = users4;
                pushMessages = pushMessages4;
            } else {
                SQLiteCursor cursor2 = this.database.queryFinalized("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages_v2 WHERE uid IN (" + ids2.toString() + ") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50", new Object[0]);
                int maxDate2 = 0;
                while (cursor2.next()) {
                    NativeByteBuffer data2 = cursor2.byteBufferValue(1);
                    if (data2 == null) {
                        ids = ids2;
                        currentTime = currentTime2;
                    } else {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data2, data2.readInt32(false), false);
                        ids = ids2;
                        currentTime = currentTime2;
                        message.readAttachPath(data2, getUserConfig().clientUserId);
                        data2.reuse();
                        MessageObject.setUnreadFlags(message, cursor2.intValue(0));
                        message.id = cursor2.intValue(3);
                        message.date = cursor2.intValue(4);
                        message.dialog_id = cursor2.longValue(5);
                        messages2.add(message);
                        int maxDate3 = Math.max(maxDate2, message.date);
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        message.send_state = cursor2.intValue(2);
                        if ((message.peer_id.channel_id == 0 && !MessageObject.isUnread(message) && !DialogObject.isEncryptedDialog(message.dialog_id)) || message.id > 0) {
                            message.send_state = 0;
                        }
                        if (DialogObject.isEncryptedDialog(message.dialog_id) && !cursor2.isNull(5)) {
                            message.random_id = cursor2.longValue(5);
                        }
                        try {
                            if (message.reply_to != null && message.reply_to.reply_to_msg_id != 0 && ((message.action instanceof TLRPC.TL_messageActionPinMessage) || (message.action instanceof TLRPC.TL_messageActionPaymentSent) || (message.action instanceof TLRPC.TL_messageActionGameScore))) {
                                if (!cursor2.isNull(6) && (data = cursor2.byteBufferValue(6)) != null) {
                                    message.replyMessage = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                                    message.replyMessage.readAttachPath(data, getUserConfig().clientUserId);
                                    data.reuse();
                                    if (message.replyMessage != null) {
                                        addUsersAndChatsFromMessage(message.replyMessage, usersToLoad, chatsToLoad);
                                    }
                                }
                                if (message.replyMessage == null) {
                                    addReplyMessages(message, replyMessageOwners3, dialogReplyMessagesIds2);
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        maxDate2 = maxDate3;
                    }
                    ids2 = ids;
                    currentTime2 = currentTime;
                }
                cursor2.dispose();
                this.database.executeFast("DELETE FROM unread_push_messages WHERE date <= " + maxDate2).stepThis().dispose();
                SQLiteCursor cursor3 = this.database.queryFinalized("SELECT data, mid, date, uid, random, fm, name, uname, flags FROM unread_push_messages WHERE 1 ORDER BY date DESC LIMIT 50", new Object[0]);
                while (cursor3.next()) {
                    NativeByteBuffer data3 = cursor3.byteBufferValue(0);
                    if (data3 != null) {
                        TLRPC.Message message2 = TLRPC.Message.TLdeserialize(data3, data3.readInt32(false), false);
                        data3.reuse();
                        message2.id = cursor3.intValue(1);
                        message2.date = cursor3.intValue(2);
                        dialogReplyMessagesIds = dialogReplyMessagesIds2;
                        message2.dialog_id = cursor3.longValue(3);
                        replyMessageOwners2 = replyMessageOwners3;
                        message2.random_id = cursor3.longValue(4);
                        String messageText = cursor3.isNull(5) ? null : cursor3.stringValue(5);
                        String name = cursor3.isNull(6) ? null : cursor3.stringValue(6);
                        String userName = cursor3.isNull(7) ? null : cursor3.stringValue(7);
                        int flags2 = cursor3.intValue(8);
                        if (MessageObject.getFromChatId(message2) != 0) {
                            str3 = str;
                        } else if (!DialogObject.isUserDialog(message2.dialog_id)) {
                            str3 = str;
                        } else {
                            message2.from_id = new TLRPC.TL_peerUser();
                            str3 = str;
                            message2.from_id.user_id = message2.dialog_id;
                        }
                        if (DialogObject.isUserDialog(message2.dialog_id)) {
                            if (!usersToLoad.contains(Long.valueOf(message2.dialog_id))) {
                                usersToLoad.add(Long.valueOf(message2.dialog_id));
                            }
                        } else if (DialogObject.isChatDialog(message2.dialog_id) && !chatsToLoad.contains(Long.valueOf(-message2.dialog_id))) {
                            chatsToLoad.add(Long.valueOf(-message2.dialog_id));
                        }
                        pushMessages3 = pushMessages4;
                        pushMessages3.add(new MessageObject(this.currentAccount, message2, messageText, name, userName, (flags2 & 1) != 0, (flags2 & 2) != 0, (message2.flags & Integer.MIN_VALUE) != 0, false));
                        addUsersAndChatsFromMessage(message2, usersToLoad, chatsToLoad);
                    } else {
                        dialogReplyMessagesIds = dialogReplyMessagesIds2;
                        replyMessageOwners2 = replyMessageOwners3;
                        str3 = str;
                        pushMessages3 = pushMessages4;
                    }
                    pushMessages4 = pushMessages3;
                    dialogReplyMessagesIds2 = dialogReplyMessagesIds;
                    replyMessageOwners3 = replyMessageOwners2;
                    str = str3;
                }
                replyMessageOwners = replyMessageOwners3;
                String str4 = str;
                ArrayList<MessageObject> pushMessages5 = pushMessages4;
                cursor3.dispose();
                messages = messages2;
                loadReplyMessages(replyMessageOwners, dialogReplyMessagesIds2, usersToLoad, chatsToLoad, false);
                if (encryptedChatIds.isEmpty()) {
                    encryptedChats2 = encryptedChats4;
                    str2 = str4;
                } else {
                    str2 = str4;
                    encryptedChats2 = encryptedChats4;
                    getEncryptedChatsInternal(TextUtils.join(str2, encryptedChatIds), encryptedChats2, usersToLoad);
                }
                if (usersToLoad.isEmpty()) {
                    users2 = users4;
                } else {
                    users2 = users4;
                    getUsersInternal(TextUtils.join(str2, usersToLoad), users2);
                }
                if (!chatsToLoad.isEmpty()) {
                    chats = chats2;
                    getChatsInternal(TextUtils.join(str2, chatsToLoad), chats);
                    int a = 0;
                    while (a < chats.size()) {
                        TLRPC.Chat chat = chats.get(a);
                        if (chat == null) {
                            maxDate = maxDate2;
                            users3 = users2;
                            encryptedChats3 = encryptedChats2;
                            pushMessages2 = pushMessages5;
                        } else {
                            if (!ChatObject.isNotInChat(chat) && !chat.min && chat.migrated_to == null) {
                                maxDate = maxDate2;
                                users3 = users2;
                                encryptedChats3 = encryptedChats2;
                                pushMessages2 = pushMessages5;
                            }
                            pushMessages2 = pushMessages5;
                            long did2 = -chat.id;
                            maxDate = maxDate2;
                            this.database.executeFast("UPDATE dialogs SET unread_count = 0 WHERE did = " + did2).stepThis().dispose();
                            users3 = users2;
                            encryptedChats3 = encryptedChats2;
                            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", Long.valueOf(did2))).stepThis().dispose();
                            chats.remove(a);
                            a--;
                            pushDialogs.remove(did2);
                            int b = 0;
                            while (b < messages.size()) {
                                if (messages.get(b).dialog_id == did2) {
                                    messages.remove(b);
                                    b--;
                                }
                                b++;
                            }
                        }
                        a++;
                        maxDate2 = maxDate;
                        pushMessages5 = pushMessages2;
                        users2 = users3;
                        encryptedChats2 = encryptedChats3;
                    }
                    users = users2;
                    encryptedChats = encryptedChats2;
                    pushMessages = pushMessages5;
                } else {
                    users = users2;
                    encryptedChats = encryptedChats2;
                    pushMessages = pushMessages5;
                    chats = chats2;
                }
            }
            Collections.reverse(messages);
            final ArrayList<TLRPC.Chat> chats3 = chats;
            final ArrayList<TLRPC.Message> arrayList = messages;
            final ArrayList<TLRPC.User> users5 = users;
            final ArrayList<MessageObject> arrayList2 = pushMessages;
            final ArrayList<TLRPC.EncryptedChat> encryptedChats5 = encryptedChats;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m987x56bd6415(pushDialogs, arrayList, arrayList2, users5, chats3, encryptedChats5);
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* renamed from: lambda$loadUnreadMessages$50$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m987x56bd6415(LongSparseArray pushDialogs, ArrayList messages, ArrayList pushMessages, ArrayList users, ArrayList chats, ArrayList encryptedChats) {
        getNotificationsController().processLoadedUnreadMessages(pushDialogs, messages, pushMessages, users, chats, encryptedChats);
    }

    public void putWallpapers(final ArrayList<TLRPC.WallPaper> wallPapers, final int action) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda151
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1024lambda$putWallpapers$52$orgtelegrammessengerMessagesStorage(action, wallPapers);
            }
        });
    }

    /* renamed from: lambda$putWallpapers$52$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1024lambda$putWallpapers$52$orgtelegrammessengerMessagesStorage(int action, ArrayList wallPapers) {
        SQLitePreparedStatement state;
        if (action == 1) {
            try {
                this.database.executeFast("DELETE FROM wallpapers2 WHERE num >= -1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        if (action != 0) {
            state = this.database.executeFast("REPLACE INTO wallpapers2 VALUES(?, ?, ?)");
        } else {
            state = this.database.executeFast("UPDATE wallpapers2 SET data = ? WHERE uid = ?");
        }
        int N = wallPapers.size();
        for (int a = 0; a < N; a++) {
            TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) wallPapers.get(a);
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(wallPaper.getObjectSize());
            wallPaper.serializeToStream(data);
            if (action != 0) {
                state.bindLong(1, wallPaper.id);
                state.bindByteBuffer(2, data);
                if (action >= 0) {
                    state.bindInteger(3, action == 2 ? -1 : a);
                } else {
                    state.bindInteger(3, action);
                }
            } else {
                state.bindByteBuffer(1, data);
                state.bindLong(2, wallPaper.id);
            }
            state.step();
            data.reuse();
        }
        state.dispose();
        this.database.commitTransaction();
    }

    public void deleteWallpaper(final long id) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda162
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m929lambda$deleteWallpaper$53$orgtelegrammessengerMessagesStorage(id);
            }
        });
    }

    /* renamed from: lambda$deleteWallpaper$53$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m929lambda$deleteWallpaper$53$orgtelegrammessengerMessagesStorage(long id) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM wallpapers2 WHERE uid = " + id).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m957lambda$getWallpapers$55$orgtelegrammessengerMessagesStorage();
            }
        });
    }

    /* renamed from: lambda$getWallpapers$55$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m957lambda$getWallpapers$55$orgtelegrammessengerMessagesStorage() {
        SQLiteCursor cursor = null;
        try {
            try {
                cursor = this.database.queryFinalized("SELECT data FROM wallpapers2 WHERE 1 ORDER BY num ASC", new Object[0]);
                final ArrayList<TLRPC.WallPaper> wallPapers = new ArrayList<>();
                while (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.WallPaper wallPaper = TLRPC.WallPaper.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (wallPaper != null) {
                            wallPapers.add(wallPaper);
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersDidLoad, wallPapers);
                    }
                });
                if (cursor == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (cursor == null) {
                    return;
                }
            }
            cursor.dispose();
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.dispose();
            }
            throw th;
        }
    }

    public void addRecentLocalFile(final String imageUrl, final String localUrl, final TLRPC.Document document) {
        if (imageUrl == null || imageUrl.length() == 0) {
            return;
        }
        if ((localUrl == null || localUrl.length() == 0) && document == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda70
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m898xafb5f8b9(document, imageUrl, localUrl);
            }
        });
    }

    /* renamed from: lambda$addRecentLocalFile$56$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m898xafb5f8b9(TLRPC.Document document, String imageUrl, String localUrl) {
        try {
            if (document != null) {
                SQLitePreparedStatement state = this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(data);
                state.bindByteBuffer(1, data);
                state.bindString(2, imageUrl);
                state.step();
                state.dispose();
                data.reuse();
            } else {
                SQLitePreparedStatement state2 = this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
                state2.requery();
                state2.bindString(1, localUrl);
                state2.bindString(2, imageUrl);
                state2.step();
                state2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteUserChatHistory(final long dialogId, final long fromId) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda183
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m928xa3ece497(dialogId, fromId);
            }
        });
    }

    /* renamed from: lambda$deleteUserChatHistory$59$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m928xa3ece497(final long dialogId, long fromId) {
        try {
            final ArrayList<Integer> mids = new ArrayList<>();
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + dialogId, new Object[0]);
            ArrayList<File> filesToDelete = new ArrayList<>();
            final ArrayList<String> namesToDelete = new ArrayList<>();
            ArrayList<Pair<Long, Integer>> idsToDelete = new ArrayList<>();
            while (cursor.next()) {
                try {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                        if (message != null) {
                            message.readAttachPath(data, getUserConfig().clientUserId);
                            if ((UserObject.isReplyUser(dialogId) && MessageObject.getPeerId(message.fwd_from.from_id) == fromId) || (MessageObject.getFromChatId(message) == fromId && message.id != 1)) {
                                mids.add(Integer.valueOf(message.id));
                                addFilesToDelete(message, filesToDelete, idsToDelete, namesToDelete, false);
                            }
                        }
                        data.reuse();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            cursor.dispose();
            deleteFromDownloadQueue(idsToDelete, true);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda47
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m926xf5b102d9(namesToDelete, dialogId, mids);
                }
            });
            m994x707d8e9d(dialogId, mids, false, false);
            m1064xf68cfb5(dialogId, DialogObject.isChatDialog(dialogId) ? -dialogId : 0L, mids, null);
            getFileLoader().deleteFiles(filesToDelete, 0);
            if (!mids.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda45
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.m927x4ccef3b8(mids, dialogId);
                    }
                });
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* renamed from: lambda$deleteUserChatHistory$57$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m926xf5b102d9(ArrayList namesToDelete, long dialogId, ArrayList mids) {
        getFileLoader().cancelLoadFiles(namesToDelete);
        getMessagesController().markDialogMessageAsDeleted(dialogId, mids);
    }

    /* renamed from: lambda$deleteUserChatHistory$58$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m927x4ccef3b8(ArrayList mids, long dialogId) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.messagesDeleted;
        Object[] objArr = new Object[3];
        objArr[0] = mids;
        objArr[1] = Long.valueOf(DialogObject.isChatDialog(dialogId) ? -dialogId : 0L);
        objArr[2] = false;
        notificationCenter.postNotificationName(i, objArr);
    }

    private boolean addFilesToDelete(TLRPC.Message message, ArrayList<File> filesToDelete, ArrayList<Pair<Long, Integer>> ids, ArrayList<String> namesToDelete, boolean forceCache) {
        if (message == null) {
            return false;
        }
        int type = 0;
        long id = 0;
        TLRPC.Document document = MessageObject.getDocument(message);
        TLRPC.Photo photo = MessageObject.getPhoto(message);
        if (MessageObject.isVoiceMessage(message)) {
            if (document == null || getMediaDataController().ringtoneDataStore.contains(document.id)) {
                return false;
            }
            id = document.id;
            type = 2;
        } else if (MessageObject.isStickerMessage(message) || MessageObject.isAnimatedStickerMessage(message)) {
            if (document == null) {
                return false;
            }
            id = document.id;
            type = 1;
        } else if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isGifMessage(message)) {
            if (document == null) {
                return false;
            }
            id = document.id;
            type = 4;
        } else if (document != null) {
            if (getMediaDataController().ringtoneDataStore.contains(document.id)) {
                return false;
            }
            id = document.id;
            type = 8;
        } else if (photo != null) {
            if (FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize()) != null) {
                id = photo.id;
                type = 1;
            }
        }
        if (id != 0) {
            ids.add(new Pair<>(Long.valueOf(id), Integer.valueOf(type)));
        }
        if (photo != null) {
            int N = photo.sizes.size();
            for (int a = 0; a < N; a++) {
                TLRPC.PhotoSize photoSize = photo.sizes.get(a);
                String name = FileLoader.getAttachFileName(photoSize);
                if (!TextUtils.isEmpty(name)) {
                    namesToDelete.add(name);
                }
                File file = getFileLoader().getPathToAttach(photoSize, forceCache);
                if (file.toString().length() > 0) {
                    filesToDelete.add(file);
                }
            }
            return true;
        } else if (document == null) {
            return false;
        } else {
            String name2 = FileLoader.getAttachFileName(document);
            if (!TextUtils.isEmpty(name2)) {
                namesToDelete.add(name2);
            }
            File file2 = getFileLoader().getPathToAttach(document, forceCache);
            if (file2.toString().length() > 0) {
                filesToDelete.add(file2);
            }
            int N2 = document.thumbs.size();
            for (int a2 = 0; a2 < N2; a2++) {
                File file3 = getFileLoader().getPathToAttach(document.thumbs.get(a2));
                if (file3.toString().length() > 0) {
                    filesToDelete.add(file3);
                }
            }
            return true;
        }
    }

    public void deleteDialog(final long did, final int messagesOnly) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda133
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m923lambda$deleteDialog$62$orgtelegrammessengerMessagesStorage(messagesOnly, did);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x029f A[Catch: Exception -> 0x0487, TryCatch #4 {Exception -> 0x0487, blocks: (B:5:0x000d, B:7:0x002c, B:8:0x0031, B:11:0x0037, B:15:0x0043, B:30:0x00c5, B:31:0x00c8, B:39:0x00fe, B:41:0x0120, B:67:0x01c0, B:68:0x01c3, B:70:0x029f, B:71:0x02a2, B:73:0x02b1, B:77:0x02c5, B:79:0x0361, B:81:0x0367, B:82:0x0387, B:83:0x03a9), top: B:95:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x02be  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0361 A[Catch: Exception -> 0x0487, TryCatch #4 {Exception -> 0x0487, blocks: (B:5:0x000d, B:7:0x002c, B:8:0x0031, B:11:0x0037, B:15:0x0043, B:30:0x00c5, B:31:0x00c8, B:39:0x00fe, B:41:0x0120, B:67:0x01c0, B:68:0x01c3, B:70:0x029f, B:71:0x02a2, B:73:0x02b1, B:77:0x02c5, B:79:0x0361, B:81:0x0367, B:82:0x0387, B:83:0x03a9), top: B:95:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0387 A[Catch: Exception -> 0x0487, TryCatch #4 {Exception -> 0x0487, blocks: (B:5:0x000d, B:7:0x002c, B:8:0x0031, B:11:0x0037, B:15:0x0043, B:30:0x00c5, B:31:0x00c8, B:39:0x00fe, B:41:0x0120, B:67:0x01c0, B:68:0x01c3, B:70:0x029f, B:71:0x02a2, B:73:0x02b1, B:77:0x02c5, B:79:0x0361, B:81:0x0367, B:82:0x0387, B:83:0x03a9), top: B:95:0x000d }] */
    /* renamed from: lambda$deleteDialog$62$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m923lambda$deleteDialog$62$orgtelegrammessengerMessagesStorage(int r24, long r25) {
        /*
            Method dump skipped, instructions count: 1164
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m923lambda$deleteDialog$62$orgtelegrammessengerMessagesStorage(int, long):void");
    }

    /* renamed from: lambda$deleteDialog$60$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m921lambda$deleteDialog$60$orgtelegrammessengerMessagesStorage(ArrayList namesToDelete) {
        getFileLoader().cancelLoadFiles(namesToDelete);
    }

    /* renamed from: lambda$deleteDialog$61$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m922lambda$deleteDialog$61$orgtelegrammessengerMessagesStorage() {
        getNotificationCenter().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
    }

    public void onDeleteQueryComplete(final long did) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda164
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1003x961536cf(did);
            }
        });
    }

    /* renamed from: lambda$onDeleteQueryComplete$63$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1003x961536cf(long did) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + did).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDialogPhotos(final long did, final int count, final int maxId, final int classGuid) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda141
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m942lambda$getDialogPhotos$65$orgtelegrammessengerMessagesStorage(maxId, did, count, classGuid);
            }
        });
    }

    /* renamed from: lambda$getDialogPhotos$65$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m942lambda$getDialogPhotos$65$orgtelegrammessengerMessagesStorage(final int maxId, final long did, final int count, final int classGuid) {
        SQLiteCursor cursor;
        try {
            if (maxId != 0) {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY rowid ASC LIMIT %d", Long.valueOf(did), Integer.valueOf(maxId), Integer.valueOf(count)), new Object[0]);
            } else {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY rowid ASC LIMIT %d", Long.valueOf(did), Integer.valueOf(count)), new Object[0]);
            }
            final TLRPC.photos_Photos res = new TLRPC.TL_photos_photos();
            final ArrayList<TLRPC.Message> messages = new ArrayList<>();
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    TLRPC.Photo photo = TLRPC.Photo.TLdeserialize(data, data.readInt32(false), false);
                    if (data.remaining() > 0) {
                        messages.add(TLRPC.Message.TLdeserialize(data, data.readInt32(false), false));
                    } else {
                        messages.add(null);
                    }
                    data.reuse();
                    res.photos.add(photo);
                }
            }
            cursor.dispose();
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda90
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m941lambda$getDialogPhotos$64$orgtelegrammessengerMessagesStorage(res, messages, did, count, maxId, classGuid);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$getDialogPhotos$64$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m941lambda$getDialogPhotos$64$orgtelegrammessengerMessagesStorage(TLRPC.photos_Photos res, ArrayList messages, long did, int count, int maxId, int classGuid) {
        getMessagesController().processLoadedUserPhotos(res, messages, did, count, maxId, true, classGuid);
    }

    public void clearUserPhotos(final long dialogId) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda161
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m913lambda$clearUserPhotos$66$orgtelegrammessengerMessagesStorage(dialogId);
            }
        });
    }

    /* renamed from: lambda$clearUserPhotos$66$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m913lambda$clearUserPhotos$66$orgtelegrammessengerMessagesStorage(long dialogId) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + dialogId).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearUserPhoto(final long dialogId, final long pid) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda182
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m912lambda$clearUserPhoto$67$orgtelegrammessengerMessagesStorage(dialogId, pid);
            }
        });
    }

    /* renamed from: lambda$clearUserPhoto$67$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m912lambda$clearUserPhoto$67$orgtelegrammessengerMessagesStorage(long dialogId, long pid) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + dialogId + " AND id = " + pid).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetDialogs(final TLRPC.messages_Dialogs dialogsRes, final int messagesCount, final int seq, final int newPts, final int date, final int qts, final LongSparseArray<TLRPC.Dialog> new_dialogs_dict, final LongSparseArray<MessageObject> new_dialogMessage, final TLRPC.Message lastMessage, final int dialogsCount) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda87
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1035lambda$resetDialogs$69$orgtelegrammessengerMessagesStorage(dialogsRes, dialogsCount, seq, newPts, date, qts, lastMessage, messagesCount, new_dialogs_dict, new_dialogMessage);
            }
        });
    }

    /* renamed from: lambda$resetDialogs$69$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1035lambda$resetDialogs$69$orgtelegrammessengerMessagesStorage(TLRPC.messages_Dialogs dialogsRes, int dialogsCount, int seq, int newPts, int date, int qts, TLRPC.Message lastMessage, int messagesCount, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage) {
        String ids;
        int maxPinnedNum = 0;
        try {
            ArrayList<Long> dids = new ArrayList<>();
            int totalPinnedCount = dialogsRes.dialogs.size() - dialogsCount;
            final LongSparseIntArray oldPinnedDialogNums = new LongSparseIntArray();
            ArrayList<Long> oldPinnedOrder = new ArrayList<>();
            ArrayList<Long> orderArrayList = new ArrayList<>();
            for (int a = dialogsCount; a < dialogsRes.dialogs.size(); a++) {
                orderArrayList.add(Long.valueOf(dialogsRes.dialogs.get(a).id));
            }
            SQLiteCursor cursor = this.database.queryFinalized("SELECT did, pinned FROM dialogs WHERE 1", new Object[0]);
            while (cursor.next()) {
                long did = cursor.longValue(0);
                int pinnedNum = cursor.intValue(1);
                if (!DialogObject.isEncryptedDialog(did)) {
                    dids.add(Long.valueOf(did));
                    if (pinnedNum > 0) {
                        maxPinnedNum = Math.max(pinnedNum, maxPinnedNum);
                        oldPinnedDialogNums.put(did, pinnedNum);
                        oldPinnedOrder.add(Long.valueOf(did));
                    }
                }
            }
            Collections.sort(oldPinnedOrder, new Comparator() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda107
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return MessagesStorage.lambda$resetDialogs$68(LongSparseIntArray.this, (Long) obj, (Long) obj2);
                }
            });
            while (oldPinnedOrder.size() < totalPinnedCount) {
                oldPinnedOrder.add(0, 0L);
            }
            cursor.dispose();
            String ids2 = "(" + TextUtils.join(",", dids) + ")";
            this.database.beginTransaction();
            this.database.executeFast("DELETE FROM chat_pinned_count WHERE uid IN " + ids2).stepThis().dispose();
            this.database.executeFast("DELETE FROM chat_pinned_v2 WHERE uid IN " + ids2).stepThis().dispose();
            this.database.executeFast("DELETE FROM dialogs WHERE did IN " + ids2).stepThis().dispose();
            this.database.executeFast("DELETE FROM messages_v2 WHERE uid IN " + ids2).stepThis().dispose();
            this.database.executeFast("DELETE FROM polls_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM bot_keyboard WHERE uid IN " + ids2).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_v4 WHERE uid IN " + ids2).stepThis().dispose();
            this.database.executeFast("DELETE FROM messages_holes WHERE uid IN " + ids2).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid IN " + ids2).stepThis().dispose();
            this.database.commitTransaction();
            int a2 = 0;
            while (a2 < totalPinnedCount) {
                TLRPC.Dialog dialog = dialogsRes.dialogs.get(dialogsCount + a2);
                if ((dialog instanceof TLRPC.TL_dialog) && !dialog.pinned) {
                    ids = ids2;
                } else {
                    int oldIdx = oldPinnedOrder.indexOf(Long.valueOf(dialog.id));
                    int newIdx = orderArrayList.indexOf(Long.valueOf(dialog.id));
                    if (oldIdx == -1 || newIdx == -1) {
                        ids = ids2;
                    } else if (oldIdx == newIdx) {
                        ids = ids2;
                        int oldNum = oldPinnedDialogNums.get(dialog.id, -1);
                        if (oldNum != -1) {
                            dialog.pinnedNum = oldNum;
                        }
                    } else {
                        ids = ids2;
                        long oldDid = oldPinnedOrder.get(newIdx).longValue();
                        int oldNum2 = oldPinnedDialogNums.get(oldDid, -1);
                        if (oldNum2 != -1) {
                            dialog.pinnedNum = oldNum2;
                        }
                    }
                    if (dialog.pinnedNum == 0) {
                        dialog.pinnedNum = (totalPinnedCount - a2) + maxPinnedNum;
                    }
                }
                a2++;
                ids2 = ids;
            }
            putDialogsInternal(dialogsRes, 0);
            m1044lambda$saveDiffParams$32$orgtelegrammessengerMessagesStorage(seq, newPts, date, qts);
            int totalDialogsLoadCount = getUserConfig().getTotalDialogsCount(0);
            long dialogsLoadOffsetChannelId = 0;
            long dialogsLoadOffsetChatId = 0;
            long dialogsLoadOffsetUserId = 0;
            long dialogsLoadOffsetAccess = 0;
            int totalDialogsLoadCount2 = dialogsRes.dialogs.size() + totalDialogsLoadCount;
            int totalDialogsLoadCount3 = lastMessage.id;
            int dialogsLoadOffsetDate = lastMessage.date;
            if (lastMessage.peer_id.channel_id != 0) {
                dialogsLoadOffsetChannelId = lastMessage.peer_id.channel_id;
                dialogsLoadOffsetChatId = 0;
                dialogsLoadOffsetUserId = 0;
                int a3 = 0;
                while (true) {
                    if (a3 >= dialogsRes.chats.size()) {
                        break;
                    }
                    TLRPC.Chat chat = dialogsRes.chats.get(a3);
                    if (chat.id != dialogsLoadOffsetChannelId) {
                        a3++;
                    } else {
                        dialogsLoadOffsetAccess = chat.access_hash;
                        break;
                    }
                }
            } else if (lastMessage.peer_id.chat_id != 0) {
                dialogsLoadOffsetChatId = lastMessage.peer_id.chat_id;
                dialogsLoadOffsetChannelId = 0;
                dialogsLoadOffsetUserId = 0;
                int a4 = 0;
                while (true) {
                    if (a4 >= dialogsRes.chats.size()) {
                        break;
                    }
                    TLRPC.Chat chat2 = dialogsRes.chats.get(a4);
                    if (chat2.id != dialogsLoadOffsetChatId) {
                        a4++;
                    } else {
                        dialogsLoadOffsetAccess = chat2.access_hash;
                        break;
                    }
                }
            } else if (lastMessage.peer_id.user_id != 0) {
                dialogsLoadOffsetUserId = lastMessage.peer_id.user_id;
                dialogsLoadOffsetChatId = 0;
                dialogsLoadOffsetChannelId = 0;
                int a5 = 0;
                while (true) {
                    if (a5 >= dialogsRes.users.size()) {
                        break;
                    }
                    TLRPC.User user = dialogsRes.users.get(a5);
                    if (user.id != dialogsLoadOffsetUserId) {
                        a5++;
                    } else {
                        dialogsLoadOffsetAccess = user.access_hash;
                        break;
                    }
                }
            }
            for (int a6 = 0; a6 < 2; a6++) {
                getUserConfig().setDialogsLoadOffset(a6, totalDialogsLoadCount3, dialogsLoadOffsetDate, dialogsLoadOffsetUserId, dialogsLoadOffsetChatId, dialogsLoadOffsetChannelId, dialogsLoadOffsetAccess);
                getUserConfig().setTotalDialogsCount(a6, totalDialogsLoadCount2);
            }
            getUserConfig().draftsLoaded = false;
            getUserConfig().saveConfig(false);
            getMessagesController().completeDialogsReset(dialogsRes, messagesCount, seq, newPts, date, qts, new_dialogs_dict, new_dialogMessage, lastMessage);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ int lambda$resetDialogs$68(LongSparseIntArray oldPinnedDialogNums, Long o1, Long o2) {
        int val1 = oldPinnedDialogNums.get(o1.longValue());
        int val2 = oldPinnedDialogNums.get(o2.longValue());
        if (val1 < val2) {
            return 1;
        }
        if (val1 > val2) {
            return -1;
        }
        return 0;
    }

    public void putDialogPhotos(final long did, final TLRPC.photos_Photos photos, final ArrayList<TLRPC.Message> messages) {
        if (photos == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1014lambda$putDialogPhotos$70$orgtelegrammessengerMessagesStorage(did, photos, messages);
            }
        });
    }

    /* renamed from: lambda$putDialogPhotos$70$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1014lambda$putDialogPhotos$70$orgtelegrammessengerMessagesStorage(long did, TLRPC.photos_Photos photos, ArrayList messages) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + did).stepThis().dispose();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
            int N = photos.photos.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Photo photo = photos.photos.get(a);
                if (!(photo instanceof TLRPC.TL_photoEmpty)) {
                    state.requery();
                    int size = photo.getObjectSize();
                    if (messages != null) {
                        size += ((TLRPC.Message) messages.get(a)).getObjectSize();
                    }
                    NativeByteBuffer data = new NativeByteBuffer(size);
                    photo.serializeToStream(data);
                    if (messages != null) {
                        ((TLRPC.Message) messages.get(a)).serializeToStream(data);
                    }
                    state.bindLong(1, did);
                    state.bindLong(2, photo.id);
                    state.bindByteBuffer(3, data);
                    state.step();
                    data.reuse();
                }
            }
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void emptyMessagesMedia(final long dialogId, final ArrayList<Integer> mids) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m932x8f51bd3e(mids, dialogId);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x0147 A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x014d A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0159 A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x015c A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x016d  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x016f  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0181 A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0196 A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x019f A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01b2 A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01c8 A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x01cc A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x01d7 A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x01dc A[Catch: Exception -> 0x020a, TryCatch #1 {Exception -> 0x020a, blocks: (B:5:0x0026, B:6:0x003f, B:8:0x0047, B:10:0x004e, B:12:0x0067, B:15:0x007c, B:17:0x0082, B:18:0x008c, B:20:0x0092, B:21:0x009b, B:23:0x00bf, B:24:0x00c5, B:28:0x00d2, B:30:0x00e0, B:31:0x00e9, B:33:0x00ef, B:35:0x012d, B:40:0x0135, B:42:0x0147, B:43:0x014d, B:44:0x0154, B:46:0x0159, B:47:0x015c, B:49:0x0160, B:50:0x0162, B:54:0x0170, B:56:0x0181, B:57:0x0196, B:58:0x0199, B:60:0x019f, B:62:0x01a5, B:63:0x01aa, B:64:0x01ae, B:65:0x01b2, B:66:0x01b6, B:68:0x01c8, B:69:0x01cc, B:70:0x01cf, B:72:0x01d7, B:74:0x01dc, B:75:0x01df, B:76:0x01e9, B:78:0x01f9), top: B:86:0x0026 }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x01df A[SYNTHETIC] */
    /* renamed from: lambda$emptyMessagesMedia$73$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m932x8f51bd3e(java.util.ArrayList r19, long r20) {
        /*
            Method dump skipped, instructions count: 531
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m932x8f51bd3e(java.util.ArrayList, long):void");
    }

    /* renamed from: lambda$emptyMessagesMedia$71$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m930xe115db80(ArrayList messages) {
        for (int a = 0; a < messages.size(); a++) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, messages.get(a));
        }
    }

    /* renamed from: lambda$emptyMessagesMedia$72$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m931x3833cc5f(ArrayList namesToDelete) {
        getFileLoader().cancelLoadFiles(namesToDelete);
    }

    public void updateMessagePollResults(final long pollId, final TLRPC.Poll poll, final TLRPC.PollResults results) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1072x7c868e8b(pollId, poll, results);
            }
        });
    }

    /* renamed from: lambda$updateMessagePollResults$74$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1072x7c868e8b(long pollId, TLRPC.Poll poll, TLRPC.PollResults results) {
        SQLitePreparedStatement state;
        TLRPC.Poll poll2 = poll;
        LongSparseArray<ArrayList<Integer>> dialogs = null;
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, mid FROM polls_v2 WHERE id = %d", Long.valueOf(pollId)), new Object[0]);
            while (cursor.next()) {
                long dialogId = cursor.longValue(0);
                if (dialogs == null) {
                    dialogs = new LongSparseArray<>();
                }
                ArrayList<Integer> mids = dialogs.get(dialogId);
                if (mids == null) {
                    mids = new ArrayList<>();
                    dialogs.put(dialogId, mids);
                }
                mids.add(Integer.valueOf(cursor.intValue(1)));
            }
            cursor.dispose();
            if (dialogs != null) {
                this.database.beginTransaction();
                SQLitePreparedStatement state2 = this.database.executeFast("UPDATE messages_v2 SET data = ? WHERE mid = ? AND uid = ?");
                int b = 0;
                int N2 = dialogs.size();
                while (b < N2) {
                    long dialogId2 = dialogs.keyAt(b);
                    ArrayList<Integer> mids2 = dialogs.valueAt(b);
                    int a = 0;
                    int N = mids2.size();
                    while (a < N) {
                        Integer mid = mids2.get(a);
                        LongSparseArray<ArrayList<Integer>> dialogs2 = dialogs;
                        int N22 = N2;
                        SQLiteCursor cursor2 = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE mid = %d AND uid = %d", mid, Long.valueOf(dialogId2)), new Object[0]);
                        if (cursor2.next()) {
                            NativeByteBuffer data = cursor2.byteBufferValue(0);
                            if (data != null) {
                                TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                                SQLitePreparedStatement state3 = state2;
                                message.readAttachPath(data, getUserConfig().clientUserId);
                                data.reuse();
                                if (!(message.media instanceof TLRPC.TL_messageMediaPoll)) {
                                    state = state3;
                                } else {
                                    TLRPC.TL_messageMediaPoll media = (TLRPC.TL_messageMediaPoll) message.media;
                                    if (poll2 != null) {
                                        media.poll = poll2;
                                    }
                                    if (results != null) {
                                        MessageObject.updatePollResults(media, results);
                                    }
                                    NativeByteBuffer data2 = new NativeByteBuffer(message.getObjectSize());
                                    message.serializeToStream(data2);
                                    state3.requery();
                                    state = state3;
                                    state.bindByteBuffer(1, data2);
                                    state.bindInteger(2, mid.intValue());
                                    state.bindLong(3, dialogId2);
                                    state.step();
                                    data2.reuse();
                                }
                            } else {
                                state = state2;
                            }
                        } else {
                            state = state2;
                            this.database.executeFast(String.format(Locale.US, "DELETE FROM polls_v2 WHERE mid = %d AND uid = %d", mid, Long.valueOf(dialogId2))).stepThis().dispose();
                        }
                        cursor2.dispose();
                        a++;
                        poll2 = poll;
                        cursor = cursor2;
                        state2 = state;
                        dialogs = dialogs2;
                        N2 = N22;
                    }
                    b++;
                    poll2 = poll;
                    N2 = N2;
                }
                state2.dispose();
                this.database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageReactions(final long dialogId, final int msgId, final TLRPC.TL_messageReactions reactions) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda147
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1073x8dcff9d(msgId, dialogId, reactions);
            }
        });
    }

    /* renamed from: lambda$updateMessageReactions$75$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1073x8dcff9d(int msgId, long dialogId, TLRPC.TL_messageReactions reactions) {
        NativeByteBuffer data;
        try {
            this.database.beginTransaction();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE mid = %d AND uid = %d", Integer.valueOf(msgId), Long.valueOf(dialogId)), new Object[0]);
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                if (message != null) {
                    message.readAttachPath(data, getUserConfig().clientUserId);
                    data.reuse();
                    MessageObject.updateReactions(message, reactions);
                    SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET data = ? WHERE mid = ? AND uid = ?");
                    NativeByteBuffer data2 = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data2);
                    state.requery();
                    state.bindByteBuffer(1, data2);
                    state.bindInteger(2, msgId);
                    state.bindLong(3, dialogId);
                    state.step();
                    data2.reuse();
                    state.dispose();
                } else {
                    data.reuse();
                }
            }
            cursor.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageVoiceTranscriptionOpen(final long dialogId, final int msgId, final TLRPC.Message saveFromMessage) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda144
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1079xf31f139e(msgId, dialogId, saveFromMessage);
            }
        });
    }

    /* renamed from: lambda$updateMessageVoiceTranscriptionOpen$76$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1079xf31f139e(int msgId, long dialogId, TLRPC.Message saveFromMessage) {
        try {
            this.database.beginTransaction();
            TLRPC.Message message = getMessageWithCustomParamsOnly(msgId, dialogId);
            message.voiceTranscriptionOpen = saveFromMessage.voiceTranscriptionOpen;
            message.voiceTranscriptionRated = saveFromMessage.voiceTranscriptionRated;
            message.voiceTranscriptionFinal = saveFromMessage.voiceTranscriptionFinal;
            message.voiceTranscriptionId = saveFromMessage.voiceTranscriptionId;
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            state.requery();
            NativeByteBuffer nativeByteBuffer = MessageCustomParamsHelper.writeLocalParams(message);
            if (nativeByteBuffer != null) {
                state.bindByteBuffer(1, nativeByteBuffer);
            } else {
                state.bindNull(1);
            }
            state.bindInteger(2, msgId);
            state.bindLong(3, dialogId);
            state.step();
            state.dispose();
            if (nativeByteBuffer != null) {
                nativeByteBuffer.reuse();
            }
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageVoiceTranscription(final long dialogId, final int messageId, final String text, final long transcriptionId, final boolean isFinal) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda148
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1077xb5daf713(messageId, dialogId, isFinal, transcriptionId, text);
            }
        });
    }

    /* renamed from: lambda$updateMessageVoiceTranscription$77$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1077xb5daf713(int messageId, long dialogId, boolean isFinal, long transcriptionId, String text) {
        try {
            this.database.beginTransaction();
            TLRPC.Message message = getMessageWithCustomParamsOnly(messageId, dialogId);
            message.voiceTranscriptionFinal = isFinal;
            message.voiceTranscriptionId = transcriptionId;
            message.voiceTranscription = text;
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            state.requery();
            NativeByteBuffer nativeByteBuffer = MessageCustomParamsHelper.writeLocalParams(message);
            if (nativeByteBuffer != null) {
                state.bindByteBuffer(1, nativeByteBuffer);
            } else {
                state.bindNull(1);
            }
            state.bindInteger(2, messageId);
            state.bindLong(3, dialogId);
            state.step();
            state.dispose();
            this.database.commitTransaction();
            if (nativeByteBuffer != null) {
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageVoiceTranscription(final long dialogId, final int messageId, final String text, final TLRPC.Message saveFromMessage) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda145
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1078xcf8e7f2(messageId, dialogId, saveFromMessage, text);
            }
        });
    }

    /* renamed from: lambda$updateMessageVoiceTranscription$78$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1078xcf8e7f2(int messageId, long dialogId, TLRPC.Message saveFromMessage, String text) {
        try {
            this.database.beginTransaction();
            TLRPC.Message message = getMessageWithCustomParamsOnly(messageId, dialogId);
            message.voiceTranscriptionOpen = saveFromMessage.voiceTranscriptionOpen;
            message.voiceTranscriptionRated = saveFromMessage.voiceTranscriptionRated;
            message.voiceTranscriptionFinal = saveFromMessage.voiceTranscriptionFinal;
            message.voiceTranscriptionId = saveFromMessage.voiceTranscriptionId;
            message.voiceTranscription = text;
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            state.requery();
            NativeByteBuffer nativeByteBuffer = MessageCustomParamsHelper.writeLocalParams(message);
            if (nativeByteBuffer != null) {
                state.bindByteBuffer(1, nativeByteBuffer);
            } else {
                state.bindNull(1);
            }
            state.bindInteger(2, messageId);
            state.bindLong(3, dialogId);
            state.step();
            state.dispose();
            this.database.commitTransaction();
            if (nativeByteBuffer != null) {
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateMessageCustomParams(final long dialogId, final TLRPC.Message saveFromMessage) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda79
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1071xab339ca8(saveFromMessage, dialogId);
            }
        });
    }

    /* renamed from: lambda$updateMessageCustomParams$79$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1071xab339ca8(TLRPC.Message saveFromMessage, long dialogId) {
        try {
            this.database.beginTransaction();
            TLRPC.Message message = getMessageWithCustomParamsOnly(saveFromMessage.id, dialogId);
            MessageCustomParamsHelper.copyParams(saveFromMessage, message);
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            state.requery();
            NativeByteBuffer nativeByteBuffer = MessageCustomParamsHelper.writeLocalParams(message);
            if (nativeByteBuffer != null) {
                state.bindByteBuffer(1, nativeByteBuffer);
            } else {
                state.bindNull(1);
            }
            state.bindInteger(2, saveFromMessage.id);
            state.bindLong(3, dialogId);
            state.step();
            state.dispose();
            this.database.commitTransaction();
            if (nativeByteBuffer != null) {
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private TLRPC.Message getMessageWithCustomParamsOnly(int messageId, long dialogId) {
        TLRPC.Message message = new TLRPC.TL_message();
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT custom_params FROM messages_v2 WHERE mid = " + messageId + " AND uid = " + dialogId, new Object[0]);
            if (cursor.next()) {
                MessageCustomParamsHelper.readLocalParams(message, cursor.byteBufferValue(0));
            }
            cursor.dispose();
        } catch (SQLiteException e) {
            FileLog.e(e);
        }
        return message;
    }

    public void getNewTask(final LongSparseArray<ArrayList<Integer>> oldTask, final LongSparseArray<ArrayList<Integer>> oldTaskMedia) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m952lambda$getNewTask$80$orgtelegrammessengerMessagesStorage(oldTask, oldTaskMedia);
            }
        });
    }

    /* renamed from: lambda$getNewTask$80$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m952lambda$getNewTask$80$orgtelegrammessengerMessagesStorage(LongSparseArray oldTask, LongSparseArray oldTaskMedia) {
        boolean media;
        LongSparseArray<ArrayList<Integer>> task;
        ArrayList<Integer> arr;
        int i = 2;
        int i2 = 0;
        if (oldTask != null) {
            try {
                int N = oldTask.size();
                for (int a = 0; a < N; a++) {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v4 WHERE mid IN(%s) AND uid = %d AND media = 0", TextUtils.join(",", (Iterable) oldTask.valueAt(a)), Long.valueOf(oldTask.keyAt(a)))).stepThis().dispose();
                }
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        if (oldTaskMedia != null) {
            int N2 = oldTaskMedia.size();
            for (int a2 = 0; a2 < N2; a2++) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v4 WHERE mid IN(%s) AND uid = %d AND media = 1", TextUtils.join(",", (Iterable) oldTaskMedia.valueAt(a2)), Long.valueOf(oldTaskMedia.keyAt(a2)))).stepThis().dispose();
            }
        }
        int date = 0;
        LongSparseArray<ArrayList<Integer>> newTask = null;
        LongSparseArray<ArrayList<Integer>> newTaskMedia = null;
        SQLiteCursor cursor = this.database.queryFinalized("SELECT mid, date, media, uid FROM enc_tasks_v4 WHERE date = (SELECT min(date) FROM enc_tasks_v4)", new Object[0]);
        while (cursor.next()) {
            int mid = cursor.intValue(i2);
            date = cursor.intValue(1);
            int isMedia = cursor.intValue(i);
            long uid = cursor.longValue(3);
            if (isMedia == -1) {
                media = mid > 0;
            } else {
                media = isMedia != 0;
            }
            if (media) {
                if (newTaskMedia == null) {
                    newTaskMedia = new LongSparseArray<>();
                }
                task = newTaskMedia;
            } else {
                if (newTask == null) {
                    newTask = new LongSparseArray<>();
                }
                task = newTask;
            }
            ArrayList<Integer> arr2 = task.get(uid);
            if (arr2 != null) {
                arr = arr2;
            } else {
                arr = new ArrayList<>();
                task.put(uid, arr);
            }
            arr.add(Integer.valueOf(mid));
            i = 2;
            i2 = 0;
        }
        cursor.dispose();
        getMessagesController().processLoadedDeleteTask(date, newTask, newTaskMedia);
    }

    public void markMentionMessageAsRead(final long dialogId, final int messageId, final long did) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda143
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m990x43f88ac8(messageId, dialogId, did);
            }
        });
    }

    /* renamed from: lambda$markMentionMessageAsRead$81$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m990x43f88ac8(int messageId, long dialogId, long did) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid = %d AND uid = %d", Integer.valueOf(messageId), Long.valueOf(dialogId))).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + did, new Object[0]);
            int old_mentions_count = 0;
            if (cursor.next()) {
                old_mentions_count = Math.max(0, cursor.intValue(0) - 1);
            }
            cursor.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(old_mentions_count), Long.valueOf(did))).stepThis().dispose();
            LongSparseIntArray sparseArray = new LongSparseIntArray(1);
            sparseArray.put(did, old_mentions_count);
            if (old_mentions_count == 0) {
                updateFiltersReadCounter(null, sparseArray, true);
            }
            getMessagesController().processDialogsUpdateRead(null, sparseArray);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void markMessageAsMention(final long dialogId, final int mid) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda134
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m991xab0e3cb3(mid, dialogId);
            }
        });
    }

    /* renamed from: lambda$markMessageAsMention$82$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m991xab0e3cb3(int mid, long dialogId) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET mention = 1, read_state = read_state & ~2 WHERE mid = %d AND uid = %d", Integer.valueOf(mid), Long.valueOf(dialogId))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetMentionsCount(final long did, final int count) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda167
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1036x21bbdd4b(did, count);
            }
        });
    }

    /* renamed from: lambda$resetMentionsCount$83$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1036x21bbdd4b(long did, int count) {
        int prevUnreadCount = 0;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + did, new Object[0]);
            if (cursor.next()) {
                prevUnreadCount = cursor.intValue(0);
            }
            cursor.dispose();
            if (prevUnreadCount != 0 || count != 0) {
                if (count == 0) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(did))).stepThis().dispose();
                }
                this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(count), Long.valueOf(did))).stepThis().dispose();
                LongSparseIntArray sparseArray = new LongSparseIntArray(1);
                sparseArray.put(did, count);
                getMessagesController().processDialogsUpdateRead(null, sparseArray);
                if (count == 0) {
                    updateFiltersReadCounter(null, sparseArray, true);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void createTaskForMid(final long dialogId, final int messageId, final int time, final int readTime, final int ttl, final boolean inner) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda130
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m917xdbb52a12(time, readTime, ttl, messageId, inner, dialogId);
            }
        });
    }

    /* renamed from: lambda$createTaskForMid$85$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m917xdbb52a12(int time, int readTime, int ttl, int messageId, final boolean inner, final long dialogId) {
        try {
            int minDate = Math.max(time, readTime) + ttl;
            SparseArray<ArrayList<Integer>> messages = new SparseArray<>();
            final ArrayList<Integer> midsArray = new ArrayList<>();
            midsArray.add(Integer.valueOf(messageId));
            messages.put(minDate, midsArray);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda96
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m916x84973933(inner, dialogId, midsArray);
                }
            });
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
            for (int a = 0; a < messages.size(); a++) {
                int key = messages.keyAt(a);
                ArrayList<Integer> arr = messages.get(key);
                for (int b = 0; b < arr.size(); b++) {
                    state.requery();
                    state.bindInteger(1, arr.get(b).intValue());
                    state.bindLong(2, dialogId);
                    state.bindInteger(3, key);
                    state.bindInteger(4, 1);
                    state.step();
                }
            }
            state.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET ttl = 0 WHERE mid = %d AND uid = %d", Integer.valueOf(messageId), Long.valueOf(dialogId))).stepThis().dispose();
            getMessagesController().didAddedNewTask(minDate, dialogId, messages);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$createTaskForMid$84$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m916x84973933(boolean inner, long dialogId, ArrayList midsArray) {
        if (!inner) {
            markMessagesContentAsRead(dialogId, midsArray, 0);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(dialogId), midsArray);
    }

    public void createTaskForSecretChat(final int chatId, final int time, final int readTime, final int isOut, final ArrayList<Long> random_ids) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda153
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m919xc1e18f26(chatId, random_ids, isOut, time, readTime);
            }
        });
    }

    /* renamed from: lambda$createTaskForSecretChat$87$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m919xc1e18f26(int chatId, ArrayList random_ids, int isOut, int time, int readTime) {
        SQLiteCursor cursor;
        try {
            final long dialogId = DialogObject.makeEncryptedDialogId(chatId);
            int minDate = Integer.MAX_VALUE;
            SparseArray<ArrayList<Integer>> messages = new SparseArray<>();
            final ArrayList<Integer> midsArray = new ArrayList<>();
            StringBuilder mids = new StringBuilder();
            if (random_ids == null) {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages_v2 WHERE uid = %d AND out = %d AND read_state > 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", Long.valueOf(dialogId), Integer.valueOf(isOut), Integer.valueOf(time)), new Object[0]);
            } else {
                String ids = TextUtils.join(",", random_ids);
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages_v2 as m INNER JOIN randoms_v2 as r ON m.mid = r.mid AND m.uid = r.uid WHERE r.random_id IN (%s)", ids), new Object[0]);
            }
            while (cursor.next()) {
                int ttl = cursor.intValue(1);
                int mid = cursor.intValue(0);
                if (random_ids != null) {
                    midsArray.add(Integer.valueOf(mid));
                }
                if (ttl > 0) {
                    int date = Math.max(time, readTime) + ttl;
                    minDate = Math.min(minDate, date);
                    ArrayList<Integer> arr = messages.get(date);
                    if (arr == null) {
                        arr = new ArrayList<>();
                        messages.put(date, arr);
                    }
                    if (mids.length() != 0) {
                        mids.append(",");
                    }
                    mids.append(mid);
                    arr.add(Integer.valueOf(mid));
                }
            }
            cursor.dispose();
            if (random_ids != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda195
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.m918x6ac39e47(dialogId, midsArray);
                    }
                });
            }
            if (messages.size() != 0) {
                this.database.beginTransaction();
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
                for (int a = 0; a < messages.size(); a++) {
                    int key = messages.keyAt(a);
                    ArrayList<Integer> arr2 = messages.get(key);
                    for (int b = 0; b < arr2.size(); b++) {
                        state.requery();
                        state.bindInteger(1, arr2.get(b).intValue());
                        state.bindLong(2, dialogId);
                        state.bindInteger(3, key);
                        state.bindInteger(4, 0);
                        state.step();
                    }
                }
                state.dispose();
                this.database.commitTransaction();
                this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET ttl = 0 WHERE mid IN(%s) AND uid = %d", mids.toString(), Long.valueOf(dialogId))).stepThis().dispose();
                getMessagesController().didAddedNewTask(minDate, dialogId, messages);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$createTaskForSecretChat$86$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m918x6ac39e47(long dialogId, ArrayList midsArray) {
        markMessagesContentAsRead(dialogId, midsArray, 0);
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(dialogId), midsArray);
    }

    /* JADX WARN: Code restructure failed: missing block: B:170:0x0483, code lost:
        if (r7 != false) goto L173;
     */
    /* JADX WARN: Code restructure failed: missing block: B:171:0x0485, code lost:
        if (r10 == false) goto L172;
     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x0488, code lost:
        r11 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:175:0x0496, code lost:
        if (r11.indexOfKey(-r8.id) >= 0) goto L176;
     */
    /* JADX WARN: Code restructure failed: missing block: B:177:0x049c, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r8) == false) goto L181;
     */
    /* JADX WARN: Code restructure failed: missing block: B:179:0x04a0, code lost:
        if (r8.megagroup != false) goto L181;
     */
    /* JADX WARN: Code restructure failed: missing block: B:180:0x04a2, code lost:
        r4 = r36.channels[r14];
        r4[r6] = r4[r6] + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x04af, code lost:
        r4 = r36.groups[r14];
        r4[r6] = r4[r6 == true ? 1 : 0] + 1;
     */
    /* JADX WARN: Type inference failed for: r11v56, types: [boolean] */
    /* JADX WARN: Type inference failed for: r4v92, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v13, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateFiltersReadCounter(org.telegram.messenger.support.LongSparseIntArray r37, org.telegram.messenger.support.LongSparseIntArray r38, boolean r39) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 3196
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateFiltersReadCounter(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, boolean):void");
    }

    /* renamed from: lambda$updateFiltersReadCounter$88$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1070x83997f23() {
        ArrayList<MessagesController.DialogFilter> filters = getMessagesController().dialogFilters;
        int N = filters.size();
        for (int a = 0; a < N; a++) {
            filters.get(a).unreadCount = filters.get(a).pendingUnreadCount;
        }
        int a2 = this.pendingMainUnreadCount;
        this.mainUnreadCount = a2;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
    }

    /* JADX WARN: Removed duplicated region for block: B:101:0x02ef A[Catch: Exception -> 0x02f7, TRY_LEAVE, TryCatch #0 {Exception -> 0x02f7, blocks: (B:3:0x0006, B:7:0x0022, B:8:0x003a, B:10:0x0040, B:13:0x0047, B:16:0x004e, B:18:0x0058, B:19:0x005c, B:21:0x0063, B:22:0x006a, B:25:0x0073, B:27:0x0079, B:29:0x00aa, B:30:0x00b2, B:31:0x00d7, B:33:0x00dd, B:35:0x00e4, B:36:0x0111, B:38:0x0117, B:40:0x0131, B:42:0x0137, B:44:0x013f, B:46:0x0146, B:48:0x016d, B:49:0x0173, B:50:0x0181, B:52:0x0198, B:53:0x01a6, B:57:0x01bb, B:59:0x01c5, B:60:0x01cc, B:63:0x01e0, B:66:0x01e7, B:68:0x01ed, B:69:0x0215, B:71:0x021b, B:75:0x0226, B:77:0x0231, B:78:0x023f, B:80:0x0245, B:82:0x0270, B:84:0x0278, B:86:0x027d, B:87:0x0283, B:88:0x0298, B:89:0x029d, B:91:0x02a8, B:93:0x02ae, B:94:0x02b7, B:96:0x02bd, B:97:0x02d6, B:98:0x02d9, B:99:0x02de, B:101:0x02ef), top: B:105:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:134:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateDialogsWithReadMessagesInternal(java.util.ArrayList<java.lang.Integer> r26, org.telegram.messenger.support.LongSparseIntArray r27, org.telegram.messenger.support.LongSparseIntArray r28, androidx.collection.LongSparseArray<java.util.ArrayList<java.lang.Integer>> r29) {
        /*
            Method dump skipped, instructions count: 764
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateDialogsWithReadMessagesInternal(java.util.ArrayList, org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, androidx.collection.LongSparseArray):void");
    }

    private static boolean isEmpty(SparseArray<?> array) {
        return array == null || array.size() == 0;
    }

    private static boolean isEmpty(LongSparseIntArray array) {
        return array == null || array.size() == 0;
    }

    private static boolean isEmpty(List<?> array) {
        return array == null || array.isEmpty();
    }

    private static boolean isEmpty(SparseIntArray array) {
        return array == null || array.size() == 0;
    }

    private static boolean isEmpty(LongSparseArray<?> array) {
        return array == null || array.size() == 0;
    }

    public void updateDialogsWithReadMessages(final LongSparseIntArray inbox, final LongSparseIntArray outbox, final LongSparseArray<ArrayList<Integer>> mentions, boolean useQueue) {
        if (isEmpty(inbox) && isEmpty(outbox) && isEmpty(mentions)) {
            return;
        }
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda61
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1065x1aa06d9e(inbox, outbox, mentions);
                }
            });
        } else {
            updateDialogsWithReadMessagesInternal(null, inbox, outbox, mentions);
        }
    }

    /* renamed from: lambda$updateDialogsWithReadMessages$89$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1065x1aa06d9e(LongSparseIntArray inbox, LongSparseIntArray outbox, LongSparseArray mentions) {
        updateDialogsWithReadMessagesInternal(null, inbox, outbox, mentions);
    }

    public void updateChatParticipants(final TLRPC.ChatParticipants participants) {
        if (participants == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda68
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1060x7fa5c7d8(participants);
            }
        });
    }

    /* renamed from: lambda$updateChatParticipants$91$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1060x7fa5c7d8(TLRPC.ChatParticipants participants) {
        NativeByteBuffer data;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + participants.chat_id, new Object[0]);
            TLRPC.ChatFull info = null;
            new ArrayList();
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                info = TLRPC.ChatFull.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
                info.pinned_msg_id = cursor.intValue(1);
                info.online_count = cursor.intValue(2);
                info.inviterId = cursor.longValue(3);
            }
            cursor.dispose();
            if (info instanceof TLRPC.TL_chatFull) {
                info.participants = participants;
                final TLRPC.ChatFull finalInfo = info;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda65
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.m1059x2887d6f9(finalInfo);
                    }
                });
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?)");
                NativeByteBuffer data2 = new NativeByteBuffer(info.getObjectSize());
                info.serializeToStream(data2);
                state.bindLong(1, info.id);
                state.bindByteBuffer(2, data2);
                state.bindInteger(3, info.pinned_msg_id);
                state.bindInteger(4, info.online_count);
                state.bindLong(5, info.inviterId);
                state.bindInteger(6, info.invitesCount);
                state.step();
                state.dispose();
                data2.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$updateChatParticipants$90$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1059x2887d6f9(TLRPC.ChatFull finalInfo) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, finalInfo, 0, false, false);
    }

    public void loadChannelAdmins(final long chatId) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda163
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m964x9852d0bd(chatId);
            }
        });
    }

    /* renamed from: lambda$loadChannelAdmins$92$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m964x9852d0bd(long chatId) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT uid, data FROM channel_admins_v3 WHERE did = " + chatId, new Object[0]);
            LongSparseArray<TLRPC.ChannelParticipant> ids = new LongSparseArray<>();
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(1);
                if (data != null) {
                    TLRPC.ChannelParticipant participant = TLRPC.ChannelParticipant.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    if (participant != null) {
                        ids.put(cursor.longValue(0), participant);
                    }
                }
            }
            cursor.dispose();
            getMessagesController().processLoadedChannelAdmins(ids, chatId, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putChannelAdmins(final long chatId, final LongSparseArray<TLRPC.ChannelParticipant> ids) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda193
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1011x57c8d7ad(chatId, ids);
            }
        });
    }

    /* renamed from: lambda$putChannelAdmins$93$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1011x57c8d7ad(long chatId, LongSparseArray ids) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM channel_admins_v3 WHERE did = " + chatId).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO channel_admins_v3 VALUES(?, ?, ?)");
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            for (int a = 0; a < ids.size(); a++) {
                state.requery();
                state.bindLong(1, chatId);
                state.bindLong(2, ids.keyAt(a));
                TLRPC.ChannelParticipant participant = (TLRPC.ChannelParticipant) ids.valueAt(a);
                NativeByteBuffer data = new NativeByteBuffer(participant.getObjectSize());
                participant.serializeToStream(data);
                state.bindByteBuffer(3, data);
                state.step();
                data.reuse();
            }
            state.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChannelUsers(final long channelId, final ArrayList<TLRPC.ChannelParticipant> participants) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda196
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1053x1d7444e2(channelId, participants);
            }
        });
    }

    /* renamed from: lambda$updateChannelUsers$94$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1053x1d7444e2(long channelId, ArrayList participants) {
        long did = -channelId;
        try {
            this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + did).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
            int date = (int) (System.currentTimeMillis() / 1000);
            for (int a = 0; a < participants.size(); a++) {
                TLRPC.ChannelParticipant participant = (TLRPC.ChannelParticipant) participants.get(a);
                state.requery();
                state.bindLong(1, did);
                state.bindLong(2, MessageObject.getPeerId(participant.peer));
                state.bindInteger(3, date);
                NativeByteBuffer data = new NativeByteBuffer(participant.getObjectSize());
                participant.serializeToStream(data);
                state.bindByteBuffer(4, data);
                state.step();
                data.reuse();
                date--;
            }
            state.dispose();
            this.database.commitTransaction();
            loadChatInfo(channelId, true, null, false, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveBotCache(final String key, final TLObject result) {
        if (result == null || TextUtils.isEmpty(key)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1037lambda$saveBotCache$95$orgtelegrammessengerMessagesStorage(result, key);
            }
        });
    }

    /* renamed from: lambda$saveBotCache$95$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1037lambda$saveBotCache$95$orgtelegrammessengerMessagesStorage(TLObject result, String key) {
        try {
            int currentDate = getConnectionsManager().getCurrentTime();
            if (result instanceof TLRPC.TL_messages_botCallbackAnswer) {
                currentDate += ((TLRPC.TL_messages_botCallbackAnswer) result).cache_time;
            } else if (result instanceof TLRPC.TL_messages_botResults) {
                currentDate += ((TLRPC.TL_messages_botResults) result).cache_time;
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
            NativeByteBuffer data = new NativeByteBuffer(result.getObjectSize());
            result.serializeToStream(data);
            state.bindString(1, key);
            state.bindInteger(2, currentDate);
            state.bindByteBuffer(3, data);
            state.step();
            state.dispose();
            data.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getBotCache(final String key, final RequestDelegate requestDelegate) {
        if (key == null || requestDelegate == null) {
            return;
        }
        final int currentDate = getConnectionsManager().getCurrentTime();
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda149
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m934lambda$getBotCache$96$orgtelegrammessengerMessagesStorage(currentDate, key, requestDelegate);
            }
        });
    }

    /* renamed from: lambda$getBotCache$96$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m934lambda$getBotCache$96$orgtelegrammessengerMessagesStorage(int currentDate, String key, RequestDelegate requestDelegate) {
        TLObject result = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteDatabase.executeFast("DELETE FROM botcache WHERE date < " + currentDate).stepThis().dispose();
                SQLiteCursor cursor = this.database.queryFinalized("SELECT data FROM botcache WHERE id = ?", key);
                if (cursor.next()) {
                    try {
                        NativeByteBuffer data = cursor.byteBufferValue(0);
                        if (data != null) {
                            int constructor = data.readInt32(false);
                            if (constructor == TLRPC.TL_messages_botCallbackAnswer.constructor) {
                                result = TLRPC.TL_messages_botCallbackAnswer.TLdeserialize(data, constructor, false);
                            } else {
                                result = TLRPC.messages_BotResults.TLdeserialize(data, constructor, false);
                            }
                            data.reuse();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                cursor.dispose();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        } finally {
            requestDelegate.run(result, null);
        }
    }

    public void loadUserInfo(final TLRPC.User user, final boolean force, final int classGuid, int fromMessageId) {
        if (user == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda84
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m989lambda$loadUserInfo$97$orgtelegrammessengerMessagesStorage(user, force, classGuid);
            }
        });
    }

    /* renamed from: lambda$loadUserInfo$97$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m989lambda$loadUserInfo$97$orgtelegrammessengerMessagesStorage(TLRPC.User user, boolean force, int classGuid) {
        boolean pinnedEndReached;
        int totalPinnedCount;
        TLRPC.UserFull info;
        Throwable th;
        Exception e;
        ArrayList<MessageObject> messageObjects;
        NativeByteBuffer data;
        HashMap<Integer, MessageObject> pinnedMessagesMap = new HashMap<>();
        ArrayList<Integer> pinnedMessages = new ArrayList<>();
        int totalPinnedCount2 = 0;
        TLRPC.UserFull info2 = null;
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT info, pinned FROM user_settings WHERE uid = " + user.id, new Object[0]);
            boolean z = true;
            if (!cursor.next() || (data = cursor.byteBufferValue(0)) == null) {
                info = null;
            } else {
                info2 = TLRPC.UserFull.TLdeserialize(data, data.readInt32(false), false);
                info2.pinned_msg_id = cursor.intValue(1);
                data.reuse();
                info = info2;
            }
            try {
                cursor.dispose();
                SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM chat_pinned_v2 WHERE uid = %d ORDER BY mid DESC", Long.valueOf(user.id)), new Object[0]);
                while (cursor2.next()) {
                    int id = cursor2.intValue(0);
                    pinnedMessages.add(Integer.valueOf(id));
                    pinnedMessagesMap.put(Integer.valueOf(id), null);
                }
                cursor2.dispose();
                SQLiteCursor cursor3 = this.database.queryFinalized("SELECT count, end FROM chat_pinned_count WHERE uid = " + user.id, new Object[0]);
                if (cursor3.next()) {
                    totalPinnedCount2 = cursor3.intValue(0);
                    if (cursor3.intValue(1) == 0) {
                        z = false;
                    }
                    boolean pinnedEndReached2 = z;
                    totalPinnedCount = totalPinnedCount2;
                    pinnedEndReached = pinnedEndReached2;
                } else {
                    totalPinnedCount = 0;
                    pinnedEndReached = false;
                }
                try {
                    try {
                        cursor3.dispose();
                        if (info != null && info.pinned_msg_id != 0 && (pinnedMessages.isEmpty() || info.pinned_msg_id > pinnedMessages.get(0).intValue())) {
                            pinnedMessages.clear();
                            pinnedMessages.add(Integer.valueOf(info.pinned_msg_id));
                            pinnedMessagesMap.put(Integer.valueOf(info.pinned_msg_id), null);
                        }
                        if (!pinnedMessages.isEmpty() && (messageObjects = getMediaDataController().loadPinnedMessages(user.id, 0L, pinnedMessages, false)) != null) {
                            int N = messageObjects.size();
                            for (int a = 0; a < N; a++) {
                                MessageObject messageObject = messageObjects.get(a);
                                pinnedMessagesMap.put(Integer.valueOf(messageObject.getId()), messageObject);
                            }
                        }
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        getMessagesController().processUserInfo(user, info, true, force, classGuid, pinnedMessages, pinnedMessagesMap, totalPinnedCount, pinnedEndReached);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    getMessagesController().processUserInfo(user, info, true, force, classGuid, pinnedMessages, pinnedMessagesMap, totalPinnedCount, pinnedEndReached);
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                totalPinnedCount = totalPinnedCount2;
                pinnedEndReached = false;
            } catch (Throwable th3) {
                th = th3;
                totalPinnedCount = totalPinnedCount2;
                pinnedEndReached = false;
                getMessagesController().processUserInfo(user, info, true, force, classGuid, pinnedMessages, pinnedMessagesMap, totalPinnedCount, pinnedEndReached);
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            totalPinnedCount = 0;
            pinnedEndReached = false;
            info = info2;
        } catch (Throwable th4) {
            th = th4;
            totalPinnedCount = 0;
            pinnedEndReached = false;
            info = info2;
        }
        getMessagesController().processUserInfo(user, info, true, force, classGuid, pinnedMessages, pinnedMessagesMap, totalPinnedCount, pinnedEndReached);
    }

    public void updateUserInfo(final TLRPC.UserFull info, final boolean ifExist) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda102
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1087lambda$updateUserInfo$98$orgtelegrammessengerMessagesStorage(ifExist, info);
            }
        });
    }

    /* renamed from: lambda$updateUserInfo$98$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1087lambda$updateUserInfo$98$orgtelegrammessengerMessagesStorage(boolean ifExist, TLRPC.UserFull info) {
        if (ifExist) {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT uid FROM user_settings WHERE uid = " + info.user.id, new Object[0]);
                boolean exist = cursor.next();
                cursor.dispose();
                if (!exist) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
        NativeByteBuffer data = new NativeByteBuffer(info.getObjectSize());
        info.serializeToStream(data);
        state.bindLong(1, info.user.id);
        state.bindByteBuffer(2, data);
        state.bindInteger(3, info.pinned_msg_id);
        state.step();
        state.dispose();
        data.reuse();
        if ((info.flags & 2048) != 0) {
            SQLitePreparedStatement state2 = this.database.executeFast("UPDATE dialogs SET folder_id = ? WHERE did = ?");
            state2.bindInteger(1, info.folder_id);
            state2.bindLong(2, info.user.id);
            state2.step();
            state2.dispose();
            this.unknownDialogsIds.remove(info.user.id);
        }
    }

    public void saveChatInviter(final long chatId, final long inviterId) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda184
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1039lambda$saveChatInviter$99$orgtelegrammessengerMessagesStorage(inviterId, chatId);
            }
        });
    }

    /* renamed from: lambda$saveChatInviter$99$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1039lambda$saveChatInviter$99$orgtelegrammessengerMessagesStorage(long inviterId, long chatId) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE chat_settings_v2 SET inviter = ? WHERE uid = ?");
            state.requery();
            state.bindLong(1, inviterId);
            state.bindLong(2, chatId);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveChatLinksCount(final long chatId, final int linksCount) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda137
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1040x8d19e181(linksCount, chatId);
            }
        });
    }

    /* renamed from: lambda$saveChatLinksCount$100$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1040x8d19e181(int linksCount, long chatId) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE chat_settings_v2 SET links = ? WHERE uid = ?");
            state.requery();
            state.bindInteger(1, linksCount);
            state.bindLong(2, chatId);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChatInfo(final TLRPC.ChatFull info, final boolean ifExist) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda67
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1055lambda$updateChatInfo$101$orgtelegrammessengerMessagesStorage(info, ifExist);
            }
        });
    }

    /* renamed from: lambda$updateChatInfo$101$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1055lambda$updateChatInfo$101$orgtelegrammessengerMessagesStorage(TLRPC.ChatFull info, boolean ifExist) {
        int currentOnline = -1;
        int links = 0;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT online, inviter, links FROM chat_settings_v2 WHERE uid = " + info.id, new Object[0]);
            if (cursor.next()) {
                currentOnline = cursor.intValue(0);
                info.inviterId = cursor.longValue(1);
                links = cursor.intValue(2);
            }
            cursor.dispose();
            if (ifExist && currentOnline == -1) {
                return;
            }
            if (currentOnline >= 0 && (info.flags & 8192) == 0) {
                info.online_count = currentOnline;
            }
            if (links >= 0) {
                info.invitesCount = links;
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?)");
            NativeByteBuffer data = new NativeByteBuffer(info.getObjectSize());
            info.serializeToStream(data);
            state.bindLong(1, info.id);
            state.bindByteBuffer(2, data);
            state.bindInteger(3, info.pinned_msg_id);
            state.bindInteger(4, info.online_count);
            state.bindLong(5, info.inviterId);
            state.bindInteger(6, info.invitesCount);
            state.step();
            state.dispose();
            data.reuse();
            if (info instanceof TLRPC.TL_channelFull) {
                SQLiteDatabase sQLiteDatabase2 = this.database;
                SQLiteCursor cursor2 = sQLiteDatabase2.queryFinalized("SELECT inbox_max, outbox_max FROM dialogs WHERE did = " + (-info.id), new Object[0]);
                if (cursor2.next()) {
                    int inbox_max = cursor2.intValue(0);
                    if (inbox_max < info.read_inbox_max_id) {
                        int outbox_max = cursor2.intValue(1);
                        SQLitePreparedStatement state2 = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ?, outbox_max = ? WHERE did = ?");
                        state2.bindInteger(1, info.unread_count);
                        state2.bindInteger(2, info.read_inbox_max_id);
                        state2.bindInteger(3, Math.max(outbox_max, info.read_outbox_max_id));
                        state2.bindLong(4, -info.id);
                        state2.step();
                        state2.dispose();
                    }
                }
                cursor2.dispose();
            }
            if ((info.flags & 2048) != 0) {
                SQLitePreparedStatement state3 = this.database.executeFast("UPDATE dialogs SET folder_id = ? WHERE did = ?");
                state3.bindInteger(1, info.folder_id);
                state3.bindLong(2, -info.id);
                state3.step();
                state3.dispose();
                this.unknownDialogsIds.remove(-info.id);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChatOnlineCount(final long channelId, final int onlineCount) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda139
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1058xc912afc9(onlineCount, channelId);
            }
        });
    }

    /* renamed from: lambda$updateChatOnlineCount$102$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1058xc912afc9(int onlineCount, long channelId) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE chat_settings_v2 SET online = ? WHERE uid = ?");
            state.requery();
            state.bindInteger(1, onlineCount);
            state.bindLong(2, channelId);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updatePinnedMessages(final long dialogId, final ArrayList<Integer> ids, final boolean pin, final int totalCount, final int maxId, final boolean end, final HashMap<Integer, MessageObject> messages) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda101
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1083x805b6e9a(pin, messages, maxId, dialogId, ids, totalCount, end);
            }
        });
    }

    /* renamed from: lambda$updatePinnedMessages$105$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1083x805b6e9a(boolean pin, final HashMap messages, final int maxId, final long dialogId, final ArrayList ids, int totalCount, boolean end) {
        boolean endReached;
        int newCount;
        boolean endReached2;
        int newCount2;
        int alreadyAdded;
        boolean endReached3;
        int newCount3;
        boolean endReached4;
        int newCount22;
        int i = 2;
        try {
            if (pin) {
                this.database.beginTransaction();
                if (messages != null) {
                    if (maxId == 0) {
                        this.database.executeFast("DELETE FROM chat_pinned_v2 WHERE uid = " + dialogId).stepThis().dispose();
                    }
                    alreadyAdded = 0;
                } else {
                    SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM chat_pinned_v2 WHERE uid = %d AND mid IN (%s)", Long.valueOf(dialogId), TextUtils.join(",", ids)), new Object[0]);
                    int alreadyAdded2 = cursor.next() ? cursor.intValue(0) : 0;
                    cursor.dispose();
                    alreadyAdded = alreadyAdded2;
                }
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chat_pinned_v2 VALUES(?, ?, ?)");
                int a = 0;
                int N = ids.size();
                while (a < N) {
                    Integer id = (Integer) ids.get(a);
                    state.requery();
                    state.bindLong(1, dialogId);
                    state.bindInteger(i, id.intValue());
                    MessageObject message = null;
                    if (messages != null) {
                        message = (MessageObject) messages.get(id);
                    }
                    NativeByteBuffer data = null;
                    if (message != null) {
                        NativeByteBuffer data2 = new NativeByteBuffer(message.messageOwner.getObjectSize());
                        message.messageOwner.serializeToStream(data2);
                        state.bindByteBuffer(3, data2);
                        data = data2;
                    } else {
                        state.bindNull(3);
                    }
                    state.step();
                    if (data != null) {
                        data.reuse();
                    }
                    a++;
                    i = 2;
                }
                state.dispose();
                this.database.commitTransaction();
                SQLiteCursor cursor2 = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM chat_pinned_v2 WHERE uid = %d", Long.valueOf(dialogId)), new Object[0]);
                int newCount1 = cursor2.next() ? cursor2.intValue(0) : 0;
                cursor2.dispose();
                if (messages == null) {
                    SQLiteCursor cursor22 = this.database.queryFinalized(String.format(Locale.US, "SELECT count, end FROM chat_pinned_count WHERE uid = %d", Long.valueOf(dialogId)), new Object[0]);
                    if (cursor22.next()) {
                        newCount22 = cursor22.intValue(0);
                        endReached4 = cursor22.intValue(1) != 0;
                    } else {
                        newCount22 = 0;
                        endReached4 = false;
                    }
                    cursor22.dispose();
                    endReached3 = endReached4;
                    newCount3 = Math.max((ids.size() - alreadyAdded) + newCount22, newCount1);
                } else {
                    newCount3 = Math.max(totalCount, newCount1);
                    endReached3 = end;
                }
                SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO chat_pinned_count VALUES(?, ?, ?)");
                state2.requery();
                state2.bindLong(1, dialogId);
                state2.bindInteger(2, newCount3);
                state2.bindInteger(3, endReached3 ? 1 : 0);
                state2.step();
                state2.dispose();
                final int newCount12 = newCount3;
                final boolean z = endReached3;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda198
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.m1081xd21f8cdc(dialogId, ids, messages, maxId, newCount12, z);
                    }
                });
                return;
            }
            if (ids == null) {
                this.database.executeFast("DELETE FROM chat_pinned_v2 WHERE uid = " + dialogId).stepThis().dispose();
                if (DialogObject.isChatDialog(dialogId)) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE chat_settings_v2 SET pinned = 0 WHERE uid = %d", Long.valueOf(-dialogId))).stepThis().dispose();
                } else {
                    this.database.executeFast(String.format(Locale.US, "UPDATE user_settings SET pinned = 0 WHERE uid = %d", Long.valueOf(dialogId))).stepThis().dispose();
                }
                newCount = 0;
                endReached = true;
            } else {
                String idsStr = TextUtils.join(",", ids);
                if (DialogObject.isChatDialog(dialogId)) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE chat_settings_v2 SET pinned = 0 WHERE uid = %d AND pinned IN (%s)", Long.valueOf(-dialogId), idsStr)).stepThis().dispose();
                } else {
                    this.database.executeFast(String.format(Locale.US, "UPDATE user_settings SET pinned = 0 WHERE uid = %d AND pinned IN (%s)", Long.valueOf(dialogId), idsStr)).stepThis().dispose();
                }
                this.database.executeFast(String.format(Locale.US, "DELETE FROM chat_pinned_v2 WHERE uid = %d AND mid IN(%s)", Long.valueOf(dialogId), idsStr)).stepThis().dispose();
                SQLiteCursor cursor3 = this.database.queryFinalized("SELECT changes()", new Object[0]);
                int updatedCount = cursor3.next() ? cursor3.intValue(0) : 0;
                cursor3.dispose();
                SQLiteCursor cursor4 = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM chat_pinned_v2 WHERE uid = %d", Long.valueOf(dialogId)), new Object[0]);
                int newCount13 = cursor4.next() ? cursor4.intValue(0) : 0;
                cursor4.dispose();
                SQLiteCursor cursor5 = this.database.queryFinalized(String.format(Locale.US, "SELECT count, end FROM chat_pinned_count WHERE uid = %d", Long.valueOf(dialogId)), new Object[0]);
                if (cursor5.next()) {
                    newCount2 = Math.max(0, cursor5.intValue(0) - updatedCount);
                    endReached2 = cursor5.intValue(1) != 0;
                } else {
                    newCount2 = 0;
                    endReached2 = false;
                }
                cursor5.dispose();
                endReached = endReached2;
                newCount = Math.max(newCount13, newCount2);
            }
            SQLitePreparedStatement state3 = this.database.executeFast("REPLACE INTO chat_pinned_count VALUES(?, ?, ?)");
            state3.requery();
            state3.bindLong(1, dialogId);
            state3.bindInteger(2, newCount);
            state3.bindInteger(3, endReached ? 1 : 0);
            state3.step();
            state3.dispose();
            final int i2 = newCount;
            final boolean z2 = endReached;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda199
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1082x293d7dbb(dialogId, ids, messages, maxId, i2, z2);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$updatePinnedMessages$103$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1081xd21f8cdc(long dialogId, ArrayList ids, HashMap messages, int maxId, int newCount, boolean endReached) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(dialogId), ids, true, 0, messages, Integer.valueOf(maxId), Integer.valueOf(newCount), Boolean.valueOf(endReached));
    }

    /* renamed from: lambda$updatePinnedMessages$104$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1082x293d7dbb(long dialogId, ArrayList ids, HashMap messages, int maxId, int newCount, boolean endReached) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(dialogId), ids, false, 0, messages, Integer.valueOf(maxId), Integer.valueOf(newCount), Boolean.valueOf(endReached));
    }

    public void updateChatInfo(final long chatId, final long userId, final int what, final long invited_id, final int version) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda172
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1057lambda$updateChatInfo$107$orgtelegrammessengerMessagesStorage(chatId, what, userId, invited_id, version);
            }
        });
    }

    /* renamed from: lambda$updateChatInfo$107$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1057lambda$updateChatInfo$107$orgtelegrammessengerMessagesStorage(long chatId, int what, long userId, long invited_id, int version) {
        Exception e;
        TLRPC.ChatParticipant newParticipant;
        NativeByteBuffer data;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + chatId, new Object[0]);
            TLRPC.ChatFull info = null;
            new ArrayList();
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                info = TLRPC.ChatFull.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
                info.pinned_msg_id = cursor.intValue(1);
                info.online_count = cursor.intValue(2);
                info.inviterId = cursor.longValue(3);
            }
            cursor.dispose();
            if (info instanceof TLRPC.TL_chatFull) {
                if (what == 1) {
                    int a = 0;
                    while (true) {
                        if (a >= info.participants.participants.size()) {
                            break;
                        } else if (info.participants.participants.get(a).user_id != userId) {
                            a++;
                        } else {
                            info.participants.participants.remove(a);
                            break;
                        }
                    }
                } else if (what == 0) {
                    Iterator<TLRPC.ChatParticipant> it = info.participants.participants.iterator();
                    while (it.hasNext()) {
                        TLRPC.ChatParticipant part = it.next();
                        if (part.user_id == userId) {
                            return;
                        }
                    }
                    TLRPC.TL_chatParticipant participant = new TLRPC.TL_chatParticipant();
                    participant.user_id = userId;
                    participant.inviter_id = invited_id;
                    participant.date = getConnectionsManager().getCurrentTime();
                    info.participants.participants.add(participant);
                } else if (what == 2) {
                    int a2 = 0;
                    while (true) {
                        if (a2 >= info.participants.participants.size()) {
                            break;
                        }
                        TLRPC.ChatParticipant participant2 = info.participants.participants.get(a2);
                        if (participant2.user_id != userId) {
                            a2++;
                        } else {
                            if (invited_id == 1) {
                                newParticipant = new TLRPC.TL_chatParticipantAdmin();
                            } else {
                                newParticipant = new TLRPC.TL_chatParticipant();
                            }
                            TLRPC.ChatParticipant newParticipant2 = newParticipant;
                            newParticipant2.user_id = participant2.user_id;
                            newParticipant2.date = participant2.date;
                            newParticipant2.inviter_id = participant2.inviter_id;
                            info.participants.participants.set(a2, newParticipant2);
                        }
                    }
                }
                try {
                    info.participants.version = version;
                    final TLRPC.ChatFull finalInfo = info;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda64
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesStorage.this.m1056lambda$updateChatInfo$106$orgtelegrammessengerMessagesStorage(finalInfo);
                        }
                    });
                    SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?)");
                    NativeByteBuffer data2 = new NativeByteBuffer(info.getObjectSize());
                    info.serializeToStream(data2);
                    state.bindLong(1, chatId);
                    state.bindByteBuffer(2, data2);
                    state.bindInteger(3, info.pinned_msg_id);
                    state.bindInteger(4, info.online_count);
                    state.bindLong(5, info.inviterId);
                    state.bindInteger(6, info.invitesCount);
                    state.step();
                    state.dispose();
                    data2.reuse();
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                }
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* renamed from: lambda$updateChatInfo$106$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1056lambda$updateChatInfo$106$orgtelegrammessengerMessagesStorage(TLRPC.ChatFull finalInfo) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, finalInfo, 0, false, false);
    }

    public boolean isMigratedChat(final long chatId) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] result = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m963lambda$isMigratedChat$108$orgtelegrammessengerMessagesStorage(chatId, result, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return result[0];
    }

    /* renamed from: lambda$isMigratedChat$108$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m963lambda$isMigratedChat$108$orgtelegrammessengerMessagesStorage(long chatId, boolean[] result, CountDownLatch countDownLatch) {
        NativeByteBuffer data;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + chatId, new Object[0]);
                TLRPC.ChatFull info = null;
                new ArrayList();
                if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                    info = TLRPC.ChatFull.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                }
                cursor.dispose();
                result[0] = (info instanceof TLRPC.TL_channelFull) && info.migrated_from_chat_id != 0;
                countDownLatch.countDown();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public boolean hasInviteMeMessage(final long chatId) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] result = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m961x4d912397(chatId, result, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return result[0];
    }

    /* renamed from: lambda$hasInviteMeMessage$109$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m961x4d912397(long chatId, boolean[] result, CountDownLatch countDownLatch) {
        try {
            try {
                long selfId = getUserConfig().getClientUserId();
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + (-chatId) + " AND out = 0 ORDER BY mid DESC LIMIT 100", new Object[0]);
                while (true) {
                    if (!cursor.next()) {
                        break;
                    }
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if ((message.action instanceof TLRPC.TL_messageActionChatAddUser) && message.action.users.contains(Long.valueOf(selfId))) {
                            result[0] = true;
                            break;
                        }
                    }
                }
                cursor.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(27:2|197|3|4|(3:195|6|(22:8|9|199|15|16|192|(7:184|18|(4:21|(2:23|208)(1:209)|24|19)|207|25|(1:27)|28)(2:33|(10:178|35|(12:38|188|39|(2:190|41)|44|45|46|(2:48|49)|(7:51|205|52|53|201|54|(2:56|57))(1:66)|(2:84|212)(11:176|69|(1:71)|72|73|182|74|75|186|76|213)|88|36)|210|89|(4:92|(2:94|215)(1:216)|95|90)|214|96|(1:98)|99)(1:104))|105|(1:109)|203|114|(4:118|119|115|116)|217|120|(7:122|123|124|193|125|(1:127)|128)|133|(1:141)|142|(4:144|(1:146)(1:147)|148|(3:150|(2:152|153)|218))|154|169|170))|14|199|15|16|192|(0)(0)|105|(2:107|109)|203|114|(2:115|116)|217|120|(0)|133|(3:135|137|141)|142|(0)|154|169|170|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x03ba, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:156:0x03bb, code lost:
        r16 = r10;
        r17 = r0;
        r18 = r0;
        r19 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:157:0x03c5, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x03c6, code lost:
        r16 = r10;
        r17 = r0;
        r18 = r0;
        r19 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x03cf, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x03d0, code lost:
        r20 = 0;
        r21 = false;
        r16 = r10;
        r17 = r0;
        r18 = r0;
        r19 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:161:0x03dd, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x03de, code lost:
        r20 = 0;
        r21 = false;
        r16 = r10;
        r17 = r0;
        r18 = r0;
        r19 = r0;
     */
    /* JADX WARN: Removed duplicated region for block: B:118:0x02ce  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x0309  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x0370 A[Catch: all -> 0x028f, Exception -> 0x029a, TRY_ENTER, TryCatch #9 {Exception -> 0x029a, blocks: (B:87:0x0205, B:89:0x020e, B:90:0x021b, B:92:0x0223, B:94:0x0231, B:95:0x0234, B:96:0x023d, B:98:0x0243, B:107:0x0272, B:109:0x0278, B:119:0x02cf, B:123:0x030a, B:135:0x033c, B:137:0x0340, B:139:0x0346, B:141:0x0355, B:144:0x0370, B:148:0x037b, B:150:0x0383, B:152:0x038a), top: B:180:0x0205 }] */
    /* JADX WARN: Removed duplicated region for block: B:184:0x0091 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00f3 A[Catch: all -> 0x03cf, Exception -> 0x03dd, TRY_ENTER, TRY_LEAVE, TryCatch #24 {Exception -> 0x03dd, all -> 0x03cf, blocks: (B:15:0x0088, B:33:0x00f3), top: B:199:0x0088 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private org.telegram.tgnet.TLRPC.ChatFull loadChatInfoInternal(long r23, boolean r25, boolean r26, boolean r27, int r28) {
        /*
            Method dump skipped, instructions count: 1092
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadChatInfoInternal(long, boolean, boolean, boolean, int):org.telegram.tgnet.TLRPC$ChatFull");
    }

    public TLRPC.ChatFull loadChatInfo(long chatId, boolean isChannel, CountDownLatch countDownLatch, boolean force, boolean byChannelUsers) {
        return loadChatInfo(chatId, isChannel, countDownLatch, force, byChannelUsers, 0);
    }

    public TLRPC.ChatFull loadChatInfo(final long chatId, final boolean isChannel, final CountDownLatch countDownLatch, final boolean force, final boolean byChannelUsers, final int fromMessageId) {
        final TLRPC.ChatFull[] result = new TLRPC.ChatFull[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda104
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m965lambda$loadChatInfo$110$orgtelegrammessengerMessagesStorage(result, chatId, isChannel, force, byChannelUsers, fromMessageId, countDownLatch);
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Throwable th) {
            }
        }
        return result[0];
    }

    /* renamed from: lambda$loadChatInfo$110$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m965lambda$loadChatInfo$110$orgtelegrammessengerMessagesStorage(TLRPC.ChatFull[] result, long chatId, boolean isChannel, boolean force, boolean byChannelUsers, int fromMessageId, CountDownLatch countDownLatch) {
        result[0] = loadChatInfoInternal(chatId, isChannel, force, byChannelUsers, fromMessageId);
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void processPendingRead(final long dialogId, final int maxPositiveId, final int maxNegativeId, final int scheduledCount) {
        final int maxDate = this.lastSavedDate;
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda170
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1009xbcb6c6ce(dialogId, maxPositiveId, scheduledCount, maxDate, maxNegativeId);
            }
        });
    }

    /* renamed from: lambda$processPendingRead$111$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1009xbcb6c6ce(long dialogId, int maxPositiveId, int scheduledCount, int maxDate, int maxNegativeId) {
        Exception e;
        int unreadCount;
        int currentMaxId;
        int currentMaxId2 = 0;
        int unreadCount2 = 0;
        long last_mid = 0;
        int prevUnreadCount = 0;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT unread_count, inbox_max, last_mid FROM dialogs WHERE did = " + dialogId, new Object[0]);
            if (cursor.next()) {
                int intValue = cursor.intValue(0);
                unreadCount2 = intValue;
                prevUnreadCount = intValue;
                currentMaxId2 = cursor.intValue(1);
                last_mid = cursor.longValue(2);
            }
            cursor.dispose();
            this.database.beginTransaction();
            try {
                if (!DialogObject.isEncryptedDialog(dialogId)) {
                    try {
                        currentMaxId = Math.max(currentMaxId2, maxPositiveId);
                        SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
                        state.requery();
                        state.bindLong(1, dialogId);
                        state.bindInteger(2, currentMaxId);
                        state.step();
                        state.dispose();
                        if (currentMaxId >= last_mid) {
                            unreadCount = 0;
                        } else {
                            int updatedCount = 0;
                            SQLiteCursor cursor2 = this.database.queryFinalized("SELECT changes()", new Object[0]);
                            if (cursor2.next()) {
                                updatedCount = cursor2.intValue(0) + scheduledCount;
                            }
                            cursor2.dispose();
                            unreadCount = Math.max(0, unreadCount2 - updatedCount);
                        }
                        SQLitePreparedStatement state2 = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                        state2.requery();
                        state2.bindLong(1, dialogId);
                        state2.bindInteger(2, currentMaxId);
                        state2.step();
                        state2.dispose();
                        SQLitePreparedStatement state3 = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND date <= ?");
                        state3.requery();
                        state3.bindLong(1, dialogId);
                        state3.bindInteger(2, maxDate);
                        state3.step();
                        state3.dispose();
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        return;
                    }
                } else {
                    currentMaxId = maxNegativeId;
                    SQLitePreparedStatement state4 = this.database.executeFast("UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = ? AND mid >= ? AND read_state IN(0,2) AND out = 0");
                    state4.requery();
                    state4.bindLong(1, dialogId);
                    state4.bindInteger(2, currentMaxId);
                    state4.step();
                    state4.dispose();
                    if (currentMaxId <= last_mid) {
                        unreadCount = 0;
                    } else {
                        int updatedCount2 = 0;
                        SQLiteCursor cursor3 = this.database.queryFinalized("SELECT changes()", new Object[0]);
                        if (cursor3.next()) {
                            updatedCount2 = cursor3.intValue(0) + scheduledCount;
                        }
                        cursor3.dispose();
                        unreadCount = Math.max(0, unreadCount2 - updatedCount2);
                    }
                }
                SQLitePreparedStatement state5 = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ? WHERE did = ?");
                state5.requery();
                state5.bindInteger(1, unreadCount);
                state5.bindInteger(2, currentMaxId);
                state5.bindLong(3, dialogId);
                state5.step();
                state5.dispose();
                this.database.commitTransaction();
                if (prevUnreadCount != 0 && unreadCount == 0) {
                    LongSparseIntArray dialogsToUpdate = new LongSparseIntArray();
                    dialogsToUpdate.put(dialogId, unreadCount);
                    updateFiltersReadCounter(dialogsToUpdate, null, true);
                }
                updateWidgets(dialogId);
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Exception e4) {
            e = e4;
        }
    }

    public void putContacts(ArrayList<TLRPC.TL_contact> contacts, final boolean deleteAll) {
        if (contacts.isEmpty() && !deleteAll) {
            return;
        }
        final ArrayList<TLRPC.TL_contact> contactsCopy = new ArrayList<>(contacts);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda100
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1013lambda$putContacts$112$orgtelegrammessengerMessagesStorage(deleteAll, contactsCopy);
            }
        });
    }

    /* renamed from: lambda$putContacts$112$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1013lambda$putContacts$112$orgtelegrammessengerMessagesStorage(boolean deleteAll, ArrayList contactsCopy) {
        if (deleteAll) {
            try {
                this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
        for (int a = 0; a < contactsCopy.size(); a++) {
            TLRPC.TL_contact contact = (TLRPC.TL_contact) contactsCopy.get(a);
            state.requery();
            int i = 1;
            state.bindLong(1, contact.user_id);
            if (!contact.mutual) {
                i = 0;
            }
            state.bindInteger(2, i);
            state.step();
        }
        state.dispose();
        this.database.commitTransaction();
    }

    public void deleteContacts(final ArrayList<Long> uids) {
        if (uids == null || uids.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m920lambda$deleteContacts$113$orgtelegrammessengerMessagesStorage(uids);
            }
        });
    }

    /* renamed from: lambda$deleteContacts$113$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m920lambda$deleteContacts$113$orgtelegrammessengerMessagesStorage(ArrayList uids) {
        try {
            String ids = TextUtils.join(",", uids);
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM contacts WHERE uid IN(" + ids + ")").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void applyPhoneBookUpdates(final String adds, final String deletes) {
        if (TextUtils.isEmpty(adds)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m899xa8d435e2(adds, deletes);
            }
        });
    }

    /* renamed from: lambda$applyPhoneBookUpdates$114$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m899xa8d435e2(String adds, String deletes) {
        try {
            if (adds.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", adds)).stepThis().dispose();
            }
            if (deletes.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", deletes)).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putCachedPhoneBook(final HashMap<String, ContactsController.Contact> contactHashMap, final boolean migrate, boolean delete) {
        if (contactHashMap != null) {
            if (contactHashMap.isEmpty() && !migrate && !delete) {
                return;
            }
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda54
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1010x9d4f67c2(contactHashMap, migrate);
                }
            });
        }
    }

    /* renamed from: lambda$putCachedPhoneBook$115$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1010x9d4f67c2(HashMap contactHashMap, boolean migrate) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d(this.currentAccount + " save contacts to db " + contactHashMap.size());
            }
            this.database.executeFast("DELETE FROM user_contacts_v7 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM user_phones_v7 WHERE 1").stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO user_contacts_v7 VALUES(?, ?, ?, ?, ?)");
            SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO user_phones_v7 VALUES(?, ?, ?, ?)");
            for (Map.Entry<String, ContactsController.Contact> entry : contactHashMap.entrySet()) {
                ContactsController.Contact contact = entry.getValue();
                if (!contact.phones.isEmpty() && !contact.shortPhones.isEmpty()) {
                    state.requery();
                    state.bindString(1, contact.key);
                    state.bindInteger(2, contact.contact_id);
                    state.bindString(3, contact.first_name);
                    state.bindString(4, contact.last_name);
                    state.bindInteger(5, contact.imported);
                    state.step();
                    for (int a = 0; a < contact.phones.size(); a++) {
                        state2.requery();
                        state2.bindString(1, contact.key);
                        state2.bindString(2, contact.phones.get(a));
                        state2.bindString(3, contact.shortPhones.get(a));
                        state2.bindInteger(4, contact.phoneDeleted.get(a).intValue());
                        state2.step();
                    }
                }
            }
            state.dispose();
            state2.dispose();
            this.database.commitTransaction();
            if (migrate) {
                this.database.executeFast("DROP TABLE IF EXISTS user_contacts_v6;").stepThis().dispose();
                this.database.executeFast("DROP TABLE IF EXISTS user_phones_v6;").stepThis().dispose();
                getCachedPhoneBook(false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getCachedPhoneBook(final boolean byError) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda93
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m935x60dfe28(byError);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x023b, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:101:0x023c, code lost:
        if (r3 != null) goto L102;
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x023e, code lost:
        r3.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x0241, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x0242, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x0244, code lost:
        if (r3 != null) goto L107;
     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x0246, code lost:
        r3.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x0249, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00eb, code lost:
        if (0 != 0) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00ed, code lost:
        r3.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00f5, code lost:
        if (r3 == null) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00f8, code lost:
        r4 = 16;
        r7 = 0;
        r8 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00fc, code lost:
        r3 = r26.database.queryFinalized("SELECT COUNT(key) FROM user_contacts_v7 WHERE 1", new java.lang.Object[0]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x010b, code lost:
        if (r3.next() == false) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x010d, code lost:
        r7 = r3.intValue(0);
        r4 = java.lang.Math.min(5000, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0119, code lost:
        if (r7 <= 5000) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x011b, code lost:
        r8 = r7 - 5000;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x011f, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0121, code lost:
        org.telegram.messenger.FileLog.d(r26.currentAccount + " current cached contacts count = " + r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x013a, code lost:
        if (r3 == null) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x013c, code lost:
        r3.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0140, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0141, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0144, code lost:
        if (r3 == null) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0147, code lost:
        r0 = new java.util.HashMap<>(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x014d, code lost:
        if (r8 == 0) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x014f, code lost:
        r3 = r26.database.queryFinalized("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1 LIMIT 0," + r7, new java.lang.Object[0]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x016a, code lost:
        r0 = r26.database.queryFinalized("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1", new java.lang.Object[0]);
        r3 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0179, code lost:
        if (r3.next() == false) goto L136;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x017b, code lost:
        r0 = r3.stringValue(0);
        r11 = r0.get(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x0185, code lost:
        if (r11 != null) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0187, code lost:
        r11 = new org.telegram.messenger.ContactsController.Contact();
        r11.contact_id = r3.intValue(r9);
        r11.first_name = r3.stringValue(r6);
        r11.last_name = r3.stringValue(r5);
        r11.imported = r3.intValue(7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x01a8, code lost:
        if (r11.first_name != null) goto L69;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x01aa, code lost:
        r11.first_name = "";
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x01ae, code lost:
        if (r11.last_name != null) goto L72;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x01b0, code lost:
        r11.last_name = "";
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x01b2, code lost:
        r0.put(r0, r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x01b5, code lost:
        r14 = r3.stringValue(4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x01ba, code lost:
        if (r14 != null) goto L76;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x01c0, code lost:
        r11.phones.add(r14);
        r16 = r3.stringValue(5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x01ca, code lost:
        if (r16 != null) goto L79;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x01d5, code lost:
        if (r16.length() != 8) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x01db, code lost:
        if (r14.length() == 8) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x01dd, code lost:
        r5 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r14);
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x01e4, code lost:
        r5 = r16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x01e6, code lost:
        r11.shortPhones.add(r5);
        r11.phoneDeleted.add(java.lang.Integer.valueOf(r3.intValue(6)));
        r11.phoneTypes.add("");
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0204, code lost:
        if (r0.size() != 5000) goto L139;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0208, code lost:
        r5 = 3;
        r6 = 2;
        r9 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x020d, code lost:
        r3.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0210, code lost:
        r0 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0217, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0219, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x021a, code lost:
        r0.clear();
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x0220, code lost:
        if (r3 != null) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x0222, code lost:
        r3.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x0226, code lost:
        getContactsController().performSyncPhoneBook(r0, true, true, false, false, !r27, false);
     */
    /* renamed from: lambda$getCachedPhoneBook$116$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m935x60dfe28(boolean r27) {
        /*
            Method dump skipped, instructions count: 596
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m935x60dfe28(boolean):void");
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m938lambda$getContacts$117$orgtelegrammessengerMessagesStorage();
            }
        });
    }

    /* renamed from: lambda$getContacts$117$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m938lambda$getContacts$117$orgtelegrammessengerMessagesStorage() {
        ArrayList<TLRPC.TL_contact> contacts = new ArrayList<>();
        ArrayList<TLRPC.User> users = new ArrayList<>();
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
            StringBuilder uids = new StringBuilder();
            while (cursor.next()) {
                long userId = cursor.intValue(0);
                TLRPC.TL_contact contact = new TLRPC.TL_contact();
                contact.user_id = userId;
                contact.mutual = cursor.intValue(1) == 1;
                if (uids.length() != 0) {
                    uids.append(",");
                }
                contacts.add(contact);
                uids.append(contact.user_id);
            }
            cursor.dispose();
            if (uids.length() != 0) {
                getUsersInternal(uids.toString(), users);
            }
        } catch (Exception e) {
            contacts.clear();
            users.clear();
            FileLog.e(e);
        }
        getContactsController().processLoadedContacts(contacts, users, 1);
    }

    public void getUnsentMessages(final int count) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda125
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m955xc61346c(count);
            }
        });
    }

    /* renamed from: lambda$getUnsentMessages$118$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m955xc61346c(int count) {
        ArrayList<TLRPC.User> users;
        ArrayList<TLRPC.Chat> chats;
        ArrayList<TLRPC.Chat> chats2;
        ArrayList<TLRPC.User> users2;
        try {
            SparseArray<TLRPC.Message> messageHashMap = new SparseArray<>();
            ArrayList<TLRPC.Message> messages = new ArrayList<>();
            ArrayList<TLRPC.Message> scheduledMessages = new ArrayList<>();
            ArrayList<TLRPC.User> users3 = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats3 = new ArrayList<>();
            ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            ArrayList<Integer> encryptedChatIds = new ArrayList<>();
            SQLiteDatabase sQLiteDatabase = this.database;
            boolean z = false;
            SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages_v2 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid AND r.uid = m.uid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY m.mid DESC LIMIT " + count, new Object[0]);
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(1);
                if (data != null) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                    message.send_state = cursor.intValue(2);
                    message.readAttachPath(data, getUserConfig().clientUserId);
                    data.reuse();
                    if (messageHashMap.indexOfKey(message.id) < 0) {
                        MessageObject.setUnreadFlags(message, cursor.intValue(0));
                        message.id = cursor.intValue(3);
                        message.date = cursor.intValue(4);
                        if (!cursor.isNull(5)) {
                            message.random_id = cursor.longValue(5);
                        }
                        message.dialog_id = cursor.longValue(6);
                        message.seq_in = cursor.intValue(7);
                        message.seq_out = cursor.intValue(8);
                        message.ttl = cursor.intValue(9);
                        messages.add(message);
                        messageHashMap.put(message.id, message);
                        if (DialogObject.isEncryptedDialog(message.dialog_id)) {
                            int encryptedChatId = DialogObject.getEncryptedChatId(message.dialog_id);
                            if (!encryptedChatIds.contains(Integer.valueOf(encryptedChatId))) {
                                encryptedChatIds.add(Integer.valueOf(encryptedChatId));
                            }
                        } else if (DialogObject.isUserDialog(message.dialog_id)) {
                            if (!usersToLoad.contains(Long.valueOf(message.dialog_id))) {
                                usersToLoad.add(Long.valueOf(message.dialog_id));
                            }
                        } else if (!chatsToLoad.contains(Long.valueOf(-message.dialog_id))) {
                            chatsToLoad.add(Long.valueOf(-message.dialog_id));
                        }
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        if (message.send_state != 3 && ((message.peer_id.channel_id == 0 && !MessageObject.isUnread(message) && !DialogObject.isEncryptedDialog(message.dialog_id)) || message.id > 0)) {
                            message.send_state = 0;
                        }
                    }
                }
                z = false;
            }
            cursor.dispose();
            SQLiteCursor cursor2 = this.database.queryFinalized("SELECT m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, m.ttl FROM scheduled_messages_v2 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid AND r.uid = m.uid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY date ASC", new Object[0]);
            while (cursor2.next()) {
                NativeByteBuffer data2 = cursor2.byteBufferValue(0);
                if (data2 != null) {
                    TLRPC.Message message2 = TLRPC.Message.TLdeserialize(data2, data2.readInt32(false), false);
                    message2.send_state = cursor2.intValue(1);
                    message2.readAttachPath(data2, getUserConfig().clientUserId);
                    data2.reuse();
                    if (messageHashMap.indexOfKey(message2.id) >= 0) {
                        users2 = users3;
                        chats2 = chats3;
                    } else {
                        message2.id = cursor2.intValue(2);
                        message2.date = cursor2.intValue(3);
                        if (cursor2.isNull(4)) {
                            users2 = users3;
                            chats2 = chats3;
                        } else {
                            users2 = users3;
                            chats2 = chats3;
                            message2.random_id = cursor2.longValue(4);
                        }
                        message2.dialog_id = cursor2.longValue(5);
                        message2.ttl = cursor2.intValue(6);
                        scheduledMessages.add(message2);
                        messageHashMap.put(message2.id, message2);
                        if (DialogObject.isEncryptedDialog(message2.dialog_id)) {
                            int encryptedChatId2 = DialogObject.getEncryptedChatId(message2.dialog_id);
                            if (!encryptedChatIds.contains(Integer.valueOf(encryptedChatId2))) {
                                encryptedChatIds.add(Integer.valueOf(encryptedChatId2));
                            }
                        } else if (DialogObject.isUserDialog(message2.dialog_id)) {
                            if (!usersToLoad.contains(Long.valueOf(message2.dialog_id))) {
                                usersToLoad.add(Long.valueOf(message2.dialog_id));
                            }
                        } else if (!chatsToLoad.contains(Long.valueOf(-message2.dialog_id))) {
                            chatsToLoad.add(Long.valueOf(-message2.dialog_id));
                        }
                        addUsersAndChatsFromMessage(message2, usersToLoad, chatsToLoad);
                        if (message2.send_state != 3 && ((message2.peer_id.channel_id == 0 && !MessageObject.isUnread(message2) && !DialogObject.isEncryptedDialog(message2.dialog_id)) || message2.id > 0)) {
                            message2.send_state = 0;
                        }
                    }
                } else {
                    users2 = users3;
                    chats2 = chats3;
                }
                users3 = users2;
                chats3 = chats2;
            }
            ArrayList<TLRPC.User> users4 = users3;
            ArrayList<TLRPC.Chat> chats4 = chats3;
            cursor2.dispose();
            if (!encryptedChatIds.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", encryptedChatIds), encryptedChats, usersToLoad);
            }
            if (usersToLoad.isEmpty()) {
                users = users4;
            } else {
                users = users4;
                getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (chatsToLoad.isEmpty()) {
                chats = chats4;
            } else {
                StringBuilder stringToLoad = new StringBuilder();
                for (int a = 0; a < chatsToLoad.size(); a++) {
                    Long cid = chatsToLoad.get(a);
                    if (stringToLoad.length() != 0) {
                        stringToLoad.append(",");
                    }
                    stringToLoad.append(cid);
                }
                chats = chats4;
                getChatsInternal(stringToLoad.toString(), chats);
            }
            getSendMessagesHelper().processUnsentMessages(messages, scheduledMessages, users, chats, encryptedChats);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean checkMessageByRandomId(final long random_id) {
        final boolean[] result = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m904x9b23a670(random_id, result, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return result[0];
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0030, code lost:
        if (r0 == null) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0033, code lost:
        r12.countDown();
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0036, code lost:
        return;
     */
    /* renamed from: lambda$checkMessageByRandomId$119$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m904x9b23a670(long r9, boolean[] r11, java.util.concurrent.CountDownLatch r12) {
        /*
            r8 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r8.database     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            java.util.Locale r2 = java.util.Locale.US     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            java.lang.String r3 = "SELECT random_id FROM randoms_v2 WHERE random_id = %d"
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            java.lang.Long r6 = java.lang.Long.valueOf(r9)     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            r7 = 0
            r5[r7] = r6     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            java.lang.String r2 = java.lang.String.format(r2, r3, r5)     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            java.lang.Object[] r3 = new java.lang.Object[r7]     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            org.telegram.SQLite.SQLiteCursor r1 = r1.queryFinalized(r2, r3)     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            r0 = r1
            boolean r1 = r0.next()     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            if (r1 == 0) goto L24
            r11[r7] = r4     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
        L24:
            if (r0 == 0) goto L33
        L26:
            r0.dispose()
            goto L33
        L2a:
            r1 = move-exception
            goto L37
        L2c:
            r1 = move-exception
            org.telegram.messenger.FileLog.e(r1)     // Catch: java.lang.Throwable -> L2a
            if (r0 == 0) goto L33
            goto L26
        L33:
            r12.countDown()
            return
        L37:
            if (r0 == 0) goto L3c
            r0.dispose()
        L3c:
            goto L3e
        L3d:
            throw r1
        L3e:
            goto L3d
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m904x9b23a670(long, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public boolean checkMessageId(final long dialogId, final int mid) {
        final boolean[] result = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda181
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m905lambda$checkMessageId$120$orgtelegrammessengerMessagesStorage(dialogId, mid, result, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return result[0];
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0037, code lost:
        if (r0 == null) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x003a, code lost:
        r13.countDown();
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x003d, code lost:
        return;
     */
    /* renamed from: lambda$checkMessageId$120$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m905lambda$checkMessageId$120$orgtelegrammessengerMessagesStorage(long r9, int r11, boolean[] r12, java.util.concurrent.CountDownLatch r13) {
        /*
            r8 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r8.database     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.util.Locale r2 = java.util.Locale.US     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.lang.String r3 = "SELECT mid FROM messages_v2 WHERE uid = %d AND mid = %d"
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.lang.Long r5 = java.lang.Long.valueOf(r9)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            r6 = 0
            r4[r6] = r5     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.lang.Integer r5 = java.lang.Integer.valueOf(r11)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            r7 = 1
            r4[r7] = r5     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.lang.String r2 = java.lang.String.format(r2, r3, r4)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.lang.Object[] r3 = new java.lang.Object[r6]     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            org.telegram.SQLite.SQLiteCursor r1 = r1.queryFinalized(r2, r3)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            r0 = r1
            boolean r1 = r0.next()     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            if (r1 == 0) goto L2b
            r12[r6] = r7     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
        L2b:
            if (r0 == 0) goto L3a
        L2d:
            r0.dispose()
            goto L3a
        L31:
            r1 = move-exception
            goto L3e
        L33:
            r1 = move-exception
            org.telegram.messenger.FileLog.e(r1)     // Catch: java.lang.Throwable -> L31
            if (r0 == 0) goto L3a
            goto L2d
        L3a:
            r13.countDown()
            return
        L3e:
            if (r0 == 0) goto L43
            r0.dispose()
        L43:
            goto L45
        L44:
            throw r1
        L45:
            goto L44
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m905lambda$checkMessageId$120$orgtelegrammessengerMessagesStorage(long, int, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void getUnreadMention(final long dialog_id, final IntCallback callback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m954xd178367(dialog_id, callback);
            }
        });
    }

    /* renamed from: lambda$getUnreadMention$122$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m954xd178367(long dialog_id, final IntCallback callback) {
        final int result;
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages_v2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(dialog_id)), new Object[0]);
            if (cursor.next()) {
                result = cursor.intValue(0);
            } else {
                result = 0;
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda135
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.IntCallback.this.run(result);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getMessagesCount(final long dialog_id, final IntCallback callback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m950x2522c43d(dialog_id, callback);
            }
        });
    }

    /* renamed from: lambda$getMessagesCount$124$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m950x2522c43d(long dialog_id, final IntCallback callback) {
        final int result;
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages_v2 WHERE uid = %d", Long.valueOf(dialog_id)), new Object[0]);
            if (cursor.next()) {
                result = cursor.intValue(0);
            } else {
                result = 0;
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda124
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.IntCallback.this.run(result);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:96:0x02ce
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:92)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:44)
        */
    public java.lang.Runnable getMessagesInternal(long r61, long r63, int r65, int r66, int r67, int r68, int r69, int r70, boolean r71, int r72, int r73, boolean r74) {
        /*
            Method dump skipped, instructions count: 7459
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getMessagesInternal(long, long, int, int, int, int, int, int, boolean, int, int, boolean):java.lang.Runnable");
    }

    public static /* synthetic */ int lambda$getMessagesInternal$125(TLRPC.Message lhs, TLRPC.Message rhs) {
        if (lhs.id > 0 && rhs.id > 0) {
            if (lhs.id > rhs.id) {
                return -1;
            }
            return lhs.id < rhs.id ? 1 : 0;
        } else if (lhs.id < 0 && rhs.id < 0) {
            if (lhs.id < rhs.id) {
                return -1;
            }
            return lhs.id > rhs.id ? 1 : 0;
        } else if (lhs.date > rhs.date) {
            return -1;
        } else {
            return lhs.date < rhs.date ? 1 : 0;
        }
    }

    /* renamed from: lambda$getMessagesInternal$126$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m951x6fd3f663(TLRPC.TL_messages_messages res, int finalMessagesCount, long dialogId, long mergeDialogId, int countQueryFinal, int maxIdOverrideFinal, int offset_date, int classGuid, int minUnreadIdFinal, int lastMessageIdFinal, int countUnreadFinal, int maxUnreadDateFinal, int load_type, boolean isEndFinal, boolean scheduled, int replyMessageId, int loadIndex, boolean queryFromServerFinal, int mentionsUnreadFinal, boolean processMessages) {
        getMessagesController().processLoadedMessages(res, finalMessagesCount, dialogId, mergeDialogId, countQueryFinal, maxIdOverrideFinal, offset_date, true, classGuid, minUnreadIdFinal, lastMessageIdFinal, countUnreadFinal, maxUnreadDateFinal, load_type, isEndFinal, scheduled ? 1 : 0, replyMessageId, loadIndex, queryFromServerFinal, mentionsUnreadFinal, processMessages);
    }

    public void getMessages(final long dialogId, final long mergeDialogId, boolean loadInfo, final int count, final int max_id, final int offset_date, final int minDate, final int classGuid, final int load_type, final boolean scheduled, final int replyMessageId, final int loadIndex, final boolean processMessages) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda186
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m949lambda$getMessages$127$orgtelegrammessengerMessagesStorage(dialogId, mergeDialogId, count, max_id, offset_date, minDate, classGuid, load_type, scheduled, replyMessageId, loadIndex, processMessages);
            }
        });
    }

    /* renamed from: lambda$getMessages$127$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m949lambda$getMessages$127$orgtelegrammessengerMessagesStorage(long dialogId, long mergeDialogId, int count, int max_id, int offset_date, int minDate, int classGuid, int load_type, boolean scheduled, int replyMessageId, int loadIndex, boolean processMessages) {
        Utilities.stageQueue.postRunnable(getMessagesInternal(dialogId, mergeDialogId, count, max_id, offset_date, minDate, classGuid, load_type, scheduled, replyMessageId, loadIndex, processMessages));
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda190
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m911lambda$clearSentMedia$128$orgtelegrammessengerMessagesStorage();
            }
        });
    }

    /* renamed from: lambda$clearSentMedia$128$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m911lambda$clearSentMedia$128$orgtelegrammessengerMessagesStorage() {
        try {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public Object[] getSentFile(final String path, final int type) {
        if (path == null || path.toLowerCase().endsWith("attheme")) {
            return null;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Object[] result = new Object[2];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m953lambda$getSentFile$129$orgtelegrammessengerMessagesStorage(path, type, result, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (result[0] == null) {
            return null;
        }
        return result;
    }

    /* renamed from: lambda$getSentFile$129$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m953lambda$getSentFile$129$orgtelegrammessengerMessagesStorage(String path, int type, Object[] result, CountDownLatch countDownLatch) {
        NativeByteBuffer data;
        try {
            try {
                String id = Utilities.MD5(path);
                if (id != null) {
                    SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, parent FROM sent_files_v2 WHERE uid = '%s' AND type = %d", id, Integer.valueOf(type)), new Object[0]);
                    if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                        TLObject file = TLRPC.MessageMedia.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (file instanceof TLRPC.TL_messageMediaDocument) {
                            result[0] = ((TLRPC.TL_messageMediaDocument) file).document;
                        } else if (file instanceof TLRPC.TL_messageMediaPhoto) {
                            result[0] = ((TLRPC.TL_messageMediaPhoto) file).photo;
                        }
                        if (result[0] != null) {
                            result[1] = cursor.stringValue(1);
                        }
                    }
                    cursor.dispose();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    private void updateWidgets(long did) {
        ArrayList<Long> dids = new ArrayList<>();
        dids.add(Long.valueOf(did));
        updateWidgets(dids);
    }

    private void updateWidgets(ArrayList<Long> dids) {
        if (dids.isEmpty()) {
            return;
        }
        AppWidgetManager appWidgetManager = null;
        try {
            TextUtils.join(",", dids);
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT DISTINCT id FROM shortcut_widget WHERE did IN(%s,-1)", TextUtils.join(",", dids)), new Object[0]);
            while (cursor.next()) {
                if (appWidgetManager == null) {
                    appWidgetManager = AppWidgetManager.getInstance(ApplicationLoader.applicationContext);
                }
                appWidgetManager.notifyAppWidgetViewDataChanged(cursor.intValue(0), org.telegram.messenger.beta.R.id.list_view);
            }
            cursor.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putWidgetDialogs(final int widgetId, final ArrayList<Long> dids) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda152
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1027x57fa9bd7(widgetId, dids);
            }
        });
    }

    /* renamed from: lambda$putWidgetDialogs$130$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1027x57fa9bd7(int widgetId, ArrayList dids) {
        try {
            this.database.beginTransaction();
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM shortcut_widget WHERE id = " + widgetId).stepThis().dispose();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO shortcut_widget VALUES(?, ?, ?)");
            if (dids.isEmpty()) {
                state.requery();
                state.bindInteger(1, widgetId);
                state.bindLong(2, -1L);
                state.bindInteger(3, 0);
                state.step();
            } else {
                int N = dids.size();
                for (int a = 0; a < N; a++) {
                    long did = ((Long) dids.get(a)).longValue();
                    state.requery();
                    state.bindInteger(1, widgetId);
                    state.bindLong(2, did);
                    state.bindInteger(3, a);
                    state.step();
                }
            }
            state.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearWidgetDialogs(final int widgetId) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda122
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m914x55a82134(widgetId);
            }
        });
    }

    /* renamed from: lambda$clearWidgetDialogs$131$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m914x55a82134(int widgetId) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM shortcut_widget WHERE id = " + widgetId).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getWidgetDialogIds(final int widgetId, final int type, final ArrayList<Long> dids, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final boolean edit) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda158
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m958xabe11857(widgetId, dids, users, chats, edit, type, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$getWidgetDialogIds$132$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m958xabe11857(int widgetId, ArrayList dids, ArrayList users, ArrayList chats, boolean edit, int type, CountDownLatch countDownLatch) {
        try {
            try {
                ArrayList<Long> usersToLoad = new ArrayList<>();
                ArrayList<Long> chatsToLoad = new ArrayList<>();
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(widgetId)), new Object[0]);
                while (cursor.next()) {
                    long id = cursor.longValue(0);
                    if (id != -1) {
                        dids.add(Long.valueOf(id));
                        if (users != null && chats != null) {
                            if (DialogObject.isUserDialog(id)) {
                                usersToLoad.add(Long.valueOf(id));
                            } else {
                                chatsToLoad.add(Long.valueOf(-id));
                            }
                        }
                    }
                }
                cursor.dispose();
                if (!edit && dids.isEmpty()) {
                    if (type == 0) {
                        SQLiteCursor cursor2 = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = 0 ORDER BY pinned DESC, date DESC LIMIT 0,10", new Object[0]);
                        while (cursor2.next()) {
                            long dialogId = cursor2.longValue(0);
                            if (!DialogObject.isFolderDialogId(dialogId)) {
                                dids.add(Long.valueOf(dialogId));
                                if (users != null && chats != null) {
                                    if (DialogObject.isUserDialog(dialogId)) {
                                        usersToLoad.add(Long.valueOf(dialogId));
                                    } else {
                                        chatsToLoad.add(Long.valueOf(-dialogId));
                                    }
                                }
                            }
                        }
                        cursor2.dispose();
                    } else {
                        SQLiteCursor cursor3 = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                        while (cursor3.next()) {
                            long dialogId2 = cursor3.longValue(0);
                            dids.add(Long.valueOf(dialogId2));
                            if (users != null && chats != null) {
                                if (DialogObject.isUserDialog(dialogId2)) {
                                    usersToLoad.add(Long.valueOf(dialogId2));
                                } else {
                                    chatsToLoad.add(Long.valueOf(-dialogId2));
                                }
                            }
                        }
                        cursor3.dispose();
                    }
                }
                if (users != null && chats != null) {
                    if (!chatsToLoad.isEmpty()) {
                        getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                    }
                    if (!usersToLoad.isEmpty()) {
                        getUsersInternal(TextUtils.join(",", usersToLoad), users);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void getWidgetDialogs(final int widgetId, final int type, final ArrayList<Long> dids, final LongSparseArray<TLRPC.Dialog> dialogs, final LongSparseArray<TLRPC.Message> messages, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda154
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m959xf81ec9bb(widgetId, dids, type, dialogs, messages, chats, users, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$getWidgetDialogs$133$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m959xf81ec9bb(int widgetId, ArrayList dids, int type, LongSparseArray dialogs, LongSparseArray messages, ArrayList chats, ArrayList users, CountDownLatch countDownLatch) {
        Throwable th;
        Exception e;
        ArrayList<Long> usersToLoad;
        SQLiteCursor cursor;
        String str;
        SQLiteCursor cursor2;
        String str2;
        boolean add = false;
        try {
            try {
                usersToLoad = new ArrayList<>();
                ArrayList<Long> chatsToLoad = new ArrayList<>();
                int i = 1;
                boolean z = false;
                SQLiteCursor cursor3 = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(widgetId)), new Object[0]);
                while (cursor3.next()) {
                    long id = cursor3.longValue(0);
                    if (id != -1) {
                        dids.add(Long.valueOf(id));
                        if (DialogObject.isUserDialog(id)) {
                            usersToLoad.add(Long.valueOf(id));
                        } else {
                            chatsToLoad.add(Long.valueOf(-id));
                        }
                    }
                }
                cursor3.dispose();
                if (dids.isEmpty() && type == 1) {
                    SQLiteCursor cursor4 = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                    while (cursor4.next()) {
                        long dialogId = cursor4.longValue(0);
                        dids.add(Long.valueOf(dialogId));
                        if (DialogObject.isUserDialog(dialogId)) {
                            usersToLoad.add(Long.valueOf(dialogId));
                        } else {
                            chatsToLoad.add(Long.valueOf(-dialogId));
                        }
                    }
                    cursor4.dispose();
                }
                String str3 = ",";
                if (dids.isEmpty()) {
                    add = true;
                    cursor = this.database.queryFinalized("SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.folder_id = 0 ORDER BY d.pinned DESC, d.date DESC LIMIT 0,10", new Object[0]);
                } else {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.did IN(%s)", TextUtils.join(str3, dids)), new Object[0]);
                }
                while (cursor.next()) {
                    int i2 = z ? 1 : 0;
                    int i3 = z ? 1 : 0;
                    long dialogId2 = cursor.longValue(i2);
                    if (DialogObject.isFolderDialogId(dialogId2)) {
                        str2 = str3;
                    } else {
                        if (add) {
                            dids.add(Long.valueOf(dialogId2));
                        }
                        TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                        tL_dialog.id = dialogId2;
                        tL_dialog.top_message = cursor.intValue(i);
                        tL_dialog.unread_count = cursor.intValue(2);
                        tL_dialog.last_message_date = cursor.intValue(3);
                        str2 = str3;
                        dialogs.put(tL_dialog.id, tL_dialog);
                        NativeByteBuffer data = cursor.byteBufferValue(4);
                        if (data != null) {
                            TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                            message.readAttachPath(data, getUserConfig().clientUserId);
                            data.reuse();
                            MessageObject.setUnreadFlags(message, cursor.intValue(5));
                            message.id = cursor.intValue(6);
                            message.send_state = cursor.intValue(7);
                            int date = cursor.intValue(8);
                            if (date != 0) {
                                tL_dialog.last_message_date = date;
                            }
                            message.dialog_id = tL_dialog.id;
                            try {
                                messages.put(tL_dialog.id, message);
                                addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                            } catch (Exception e2) {
                                e = e2;
                                FileLog.e(e);
                                countDownLatch.countDown();
                            } catch (Throwable th2) {
                                th = th2;
                                countDownLatch.countDown();
                                throw th;
                            }
                        }
                    }
                    str3 = str2;
                    i = 1;
                    z = false;
                }
                String str4 = str3;
                cursor.dispose();
                if (!add && dids.size() > dialogs.size()) {
                    int a = 0;
                    int N = dids.size();
                    while (a < N) {
                        long did = ((Long) dids.get(a)).longValue();
                        if (dialogs.get(((Long) dids.get(a)).longValue()) == null) {
                            TLRPC.TL_dialog dialog = new TLRPC.TL_dialog();
                            dialog.id = did;
                            cursor2 = cursor;
                            dialogs.put(dialog.id, dialog);
                            if (DialogObject.isChatDialog(did)) {
                                if (chatsToLoad.contains(Long.valueOf(-did))) {
                                    chatsToLoad.add(Long.valueOf(-did));
                                }
                            } else if (usersToLoad.contains(Long.valueOf(did))) {
                                usersToLoad.add(Long.valueOf(did));
                            }
                        } else {
                            cursor2 = cursor;
                        }
                        a++;
                        cursor = cursor2;
                    }
                }
                if (!chatsToLoad.isEmpty()) {
                    str = str4;
                    try {
                        getChatsInternal(TextUtils.join(str, chatsToLoad), chats);
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.e(e);
                        countDownLatch.countDown();
                    } catch (Throwable th3) {
                        th = th3;
                        countDownLatch.countDown();
                        throw th;
                    }
                } else {
                    str = str4;
                }
            } catch (Throwable th4) {
                th = th4;
                countDownLatch.countDown();
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
        } catch (Throwable th5) {
            th = th5;
        }
        if (usersToLoad.isEmpty()) {
            countDownLatch.countDown();
        }
        try {
            getUsersInternal(TextUtils.join(str, usersToLoad), users);
        } catch (Exception e5) {
            e = e5;
            FileLog.e(e);
            countDownLatch.countDown();
        }
        countDownLatch.countDown();
    }

    public void putSentFile(final String path, final TLObject file, final int type, final String parent) {
        if (path == null || file == null || parent == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1022lambda$putSentFile$134$orgtelegrammessengerMessagesStorage(path, file, type, parent);
            }
        });
    }

    /* renamed from: lambda$putSentFile$134$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1022lambda$putSentFile$134$orgtelegrammessengerMessagesStorage(String path, TLObject file, int type, String parent) {
        SQLitePreparedStatement state = null;
        try {
            try {
                String id = Utilities.MD5(path);
                if (id != null) {
                    TLRPC.MessageMedia messageMedia = null;
                    if (file instanceof TLRPC.Photo) {
                        messageMedia = new TLRPC.TL_messageMediaPhoto();
                        messageMedia.photo = (TLRPC.Photo) file;
                        messageMedia.flags |= 1;
                    } else if (file instanceof TLRPC.Document) {
                        messageMedia = new TLRPC.TL_messageMediaDocument();
                        messageMedia.document = (TLRPC.Document) file;
                        messageMedia.flags |= 1;
                    }
                    if (messageMedia == null) {
                        if (0 == 0) {
                            return;
                        }
                        state.dispose();
                        return;
                    }
                    state = this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?, ?)");
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(messageMedia.getObjectSize());
                    messageMedia.serializeToStream(data);
                    state.bindString(1, id);
                    state.bindInteger(2, type);
                    state.bindByteBuffer(3, data);
                    state.bindString(4, parent);
                    state.step();
                    data.reuse();
                }
                if (state == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (state == null) {
                    return;
                }
            }
            state.dispose();
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatSeq(final TLRPC.EncryptedChat chat, final boolean cleanup) {
        if (chat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda75
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1068xb8a5257e(chat, cleanup);
            }
        });
    }

    /* renamed from: lambda$updateEncryptedChatSeq$135$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1068xb8a5257e(TLRPC.EncryptedChat chat, boolean cleanup) {
        SQLitePreparedStatement state = null;
        try {
            try {
                state = this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
                state.bindInteger(1, chat.seq_in);
                state.bindInteger(2, chat.seq_out);
                state.bindInteger(3, (chat.key_use_count_in << 16) | chat.key_use_count_out);
                state.bindInteger(4, chat.in_seq_no);
                state.bindInteger(5, chat.mtproto_seq);
                state.bindInteger(6, chat.id);
                state.step();
                if (cleanup && chat.in_seq_no != 0) {
                    long did = DialogObject.getEncryptedChatId(chat.id);
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_v2 WHERE mid IN (SELECT m.mid FROM messages_v2 as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d) AND uid = %d", Long.valueOf(did), Integer.valueOf(chat.in_seq_no), Long.valueOf(did))).stepThis().dispose();
                }
                if (state == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (state == null) {
                    return;
                }
            }
            state.dispose();
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatTTL(final TLRPC.EncryptedChat chat) {
        if (chat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda73
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1069x58627eb0(chat);
            }
        });
    }

    /* renamed from: lambda$updateEncryptedChatTTL$136$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1069x58627eb0(TLRPC.EncryptedChat chat) {
        SQLitePreparedStatement state = null;
        try {
            try {
                state = this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
                state.bindInteger(1, chat.ttl);
                state.bindInteger(2, chat.id);
                state.step();
                if (state == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (state == null) {
                    return;
                }
            }
            state.dispose();
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatLayer(final TLRPC.EncryptedChat chat) {
        if (chat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda72
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1067xafa5d58a(chat);
            }
        });
    }

    /* renamed from: lambda$updateEncryptedChatLayer$137$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1067xafa5d58a(TLRPC.EncryptedChat chat) {
        SQLitePreparedStatement state = null;
        try {
            try {
                state = this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
                state.bindInteger(1, chat.layer);
                state.bindInteger(2, chat.id);
                state.step();
                if (state == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (state == null) {
                    return;
                }
            }
            state.dispose();
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChat(final TLRPC.EncryptedChat chat) {
        if (chat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda71
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1066x918af94e(chat);
            }
        });
    }

    /* renamed from: lambda$updateEncryptedChat$138$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1066x918af94e(TLRPC.EncryptedChat chat) {
        SQLitePreparedStatement state = null;
        try {
            try {
                if ((chat.key_hash == null || chat.key_hash.length < 16) && chat.auth_key != null) {
                    chat.key_hash = AndroidUtilities.calcAuthKeyHash(chat.auth_key);
                }
                state = this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ?, mtproto_seq = ? WHERE uid = ?");
                NativeByteBuffer data = new NativeByteBuffer(chat.getObjectSize());
                NativeByteBuffer data2 = new NativeByteBuffer(chat.a_or_b != null ? chat.a_or_b.length : 1);
                NativeByteBuffer data3 = new NativeByteBuffer(chat.auth_key != null ? chat.auth_key.length : 1);
                NativeByteBuffer data4 = new NativeByteBuffer(chat.future_auth_key != null ? chat.future_auth_key.length : 1);
                NativeByteBuffer data5 = new NativeByteBuffer(chat.key_hash != null ? chat.key_hash.length : 1);
                chat.serializeToStream(data);
                state.bindByteBuffer(1, data);
                if (chat.a_or_b != null) {
                    data2.writeBytes(chat.a_or_b);
                }
                if (chat.auth_key != null) {
                    data3.writeBytes(chat.auth_key);
                }
                if (chat.future_auth_key != null) {
                    data4.writeBytes(chat.future_auth_key);
                }
                if (chat.key_hash != null) {
                    data5.writeBytes(chat.key_hash);
                }
                state.bindByteBuffer(2, data2);
                state.bindByteBuffer(3, data3);
                state.bindInteger(4, chat.ttl);
                state.bindInteger(5, chat.layer);
                state.bindInteger(6, chat.seq_in);
                state.bindInteger(7, chat.seq_out);
                state.bindInteger(8, (chat.key_use_count_in << 16) | chat.key_use_count_out);
                state.bindLong(9, chat.exchange_id);
                state.bindInteger(10, chat.key_create_date);
                state.bindLong(11, chat.future_key_fingerprint);
                state.bindByteBuffer(12, data4);
                state.bindByteBuffer(13, data5);
                state.bindInteger(14, chat.in_seq_no);
                state.bindLong(15, chat.admin_id);
                state.bindInteger(16, chat.mtproto_seq);
                state.bindInteger(17, chat.id);
                state.step();
                data.reuse();
                data2.reuse();
                data3.reuse();
                data4.reuse();
                data5.reuse();
                if (state == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (state == null) {
                    return;
                }
            }
            state.dispose();
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void isDialogHasTopMessage(final long did, final Runnable onDontExist) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda194
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m962x8c39c46(did, onDontExist);
            }
        });
    }

    /* renamed from: lambda$isDialogHasTopMessage$139$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m962x8c39c46(long did, Runnable onDontExist) {
        boolean exists = false;
        try {
            boolean z = true;
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT last_mid FROM dialogs WHERE did = %d", Long.valueOf(did)), new Object[0]);
            if (cursor.next()) {
                if (cursor.intValue(0) == 0) {
                    z = false;
                }
                exists = z;
            }
            cursor.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!exists) {
            AndroidUtilities.runOnUIThread(onDontExist);
        }
    }

    public boolean hasAuthMessage(final int date) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] result = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda160
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m960lambda$hasAuthMessage$140$orgtelegrammessengerMessagesStorage(date, result, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return result[0];
    }

    /* renamed from: lambda$hasAuthMessage$140$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m960lambda$hasAuthMessage$140$orgtelegrammessengerMessagesStorage(int date, boolean[] result, CountDownLatch countDownLatch) {
        try {
            try {
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages_v2 WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", Integer.valueOf(date)), new Object[0]);
                result[0] = cursor.next();
                cursor.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void getEncryptedChat(final long chatId, final CountDownLatch countDownLatch, final ArrayList<TLObject> result) {
        if (countDownLatch == null || result == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda200
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m948x4cdc21e9(chatId, result, countDownLatch);
            }
        });
    }

    /* renamed from: lambda$getEncryptedChat$141$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m948x4cdc21e9(long chatId, ArrayList result, CountDownLatch countDownLatch) {
        try {
            try {
                ArrayList<Long> usersToLoad = new ArrayList<>();
                ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
                getEncryptedChatsInternal("" + chatId, encryptedChats, usersToLoad);
                if (!encryptedChats.isEmpty() && !usersToLoad.isEmpty()) {
                    ArrayList<TLRPC.User> users = new ArrayList<>();
                    getUsersInternal(TextUtils.join(",", usersToLoad), users);
                    if (!users.isEmpty()) {
                        result.add(encryptedChats.get(0));
                        result.add(users.get(0));
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void putEncryptedChat(final TLRPC.EncryptedChat chat, final TLRPC.User user, final TLRPC.Dialog dialog) {
        if (chat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda74
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1016x92fb781(chat, user, dialog);
            }
        });
    }

    /* renamed from: lambda$putEncryptedChat$142$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1016x92fb781(TLRPC.EncryptedChat chat, TLRPC.User user, TLRPC.Dialog dialog) {
        try {
            if ((chat.key_hash == null || chat.key_hash.length < 16) && chat.auth_key != null) {
                chat.key_hash = AndroidUtilities.calcAuthKeyHash(chat.auth_key);
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            NativeByteBuffer data = new NativeByteBuffer(chat.getObjectSize());
            NativeByteBuffer data2 = new NativeByteBuffer(chat.a_or_b != null ? chat.a_or_b.length : 1);
            NativeByteBuffer data3 = new NativeByteBuffer(chat.auth_key != null ? chat.auth_key.length : 1);
            NativeByteBuffer data4 = new NativeByteBuffer(chat.future_auth_key != null ? chat.future_auth_key.length : 1);
            NativeByteBuffer data5 = new NativeByteBuffer(chat.key_hash != null ? chat.key_hash.length : 1);
            chat.serializeToStream(data);
            state.bindInteger(1, chat.id);
            state.bindLong(2, user.id);
            state.bindString(3, formatUserSearchName(user));
            state.bindByteBuffer(4, data);
            if (chat.a_or_b != null) {
                data2.writeBytes(chat.a_or_b);
            }
            if (chat.auth_key != null) {
                data3.writeBytes(chat.auth_key);
            }
            if (chat.future_auth_key != null) {
                data4.writeBytes(chat.future_auth_key);
            }
            if (chat.key_hash != null) {
                data5.writeBytes(chat.key_hash);
            }
            state.bindByteBuffer(5, data2);
            state.bindByteBuffer(6, data3);
            state.bindInteger(7, chat.ttl);
            state.bindInteger(8, chat.layer);
            state.bindInteger(9, chat.seq_in);
            state.bindInteger(10, chat.seq_out);
            state.bindInteger(11, chat.key_use_count_out | (chat.key_use_count_in << 16));
            state.bindLong(12, chat.exchange_id);
            state.bindInteger(13, chat.key_create_date);
            state.bindLong(14, chat.future_key_fingerprint);
            state.bindByteBuffer(15, data4);
            state.bindByteBuffer(16, data5);
            state.bindInteger(17, chat.in_seq_no);
            state.bindLong(18, chat.admin_id);
            state.bindInteger(19, chat.mtproto_seq);
            state.step();
            state.dispose();
            data.reuse();
            data2.reuse();
            data3.reuse();
            data4.reuse();
            data5.reuse();
            if (dialog != null) {
                SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                state2.bindLong(1, dialog.id);
                state2.bindInteger(2, dialog.last_message_date);
                state2.bindInteger(3, dialog.unread_count);
                state2.bindInteger(4, dialog.top_message);
                state2.bindInteger(5, dialog.read_inbox_max_id);
                state2.bindInteger(6, dialog.read_outbox_max_id);
                state2.bindInteger(7, 0);
                state2.bindInteger(8, dialog.unread_mentions_count);
                state2.bindInteger(9, dialog.pts);
                state2.bindInteger(10, 0);
                state2.bindInteger(11, dialog.pinnedNum);
                state2.bindInteger(12, dialog.flags);
                state2.bindInteger(13, dialog.folder_id);
                state2.bindNull(14);
                state2.bindInteger(15, dialog.unread_reactions_count);
                state2.step();
                state2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private String formatUserSearchName(TLRPC.User user) {
        StringBuilder str = new StringBuilder();
        if (user.first_name != null && user.first_name.length() > 0) {
            str.append(user.first_name);
        }
        if (user.last_name != null && user.last_name.length() > 0) {
            if (str.length() > 0) {
                str.append(" ");
            }
            str.append(user.last_name);
        }
        str.append(";;;");
        if (user.username != null && user.username.length() > 0) {
            str.append(user.username);
        }
        return str.toString().toLowerCase();
    }

    private void putUsersInternal(ArrayList<TLRPC.User> users) throws Exception {
        if (users == null || users.isEmpty()) {
            return;
        }
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
        for (int a = 0; a < users.size(); a++) {
            TLRPC.User user = users.get(a);
            if (user.min) {
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", Long.valueOf(user.id)), new Object[0]);
                if (cursor.next()) {
                    try {
                        NativeByteBuffer data = cursor.byteBufferValue(0);
                        if (data != null) {
                            TLRPC.User oldUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                            if (oldUser != null) {
                                if (user.username != null) {
                                    oldUser.username = user.username;
                                    oldUser.flags |= 8;
                                } else {
                                    oldUser.username = null;
                                    oldUser.flags &= -9;
                                }
                                if (user.apply_min_photo) {
                                    if (user.photo != null) {
                                        oldUser.photo = user.photo;
                                        oldUser.flags |= 32;
                                    } else {
                                        oldUser.photo = null;
                                        oldUser.flags &= -33;
                                    }
                                }
                                user = oldUser;
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                cursor.dispose();
            }
            state.requery();
            NativeByteBuffer data2 = new NativeByteBuffer(user.getObjectSize());
            user.serializeToStream(data2);
            state.bindLong(1, user.id);
            state.bindString(2, formatUserSearchName(user));
            if (user.status != null) {
                if (user.status instanceof TLRPC.TL_userStatusRecently) {
                    user.status.expires = -100;
                } else if (user.status instanceof TLRPC.TL_userStatusLastWeek) {
                    user.status.expires = -101;
                } else if (user.status instanceof TLRPC.TL_userStatusLastMonth) {
                    user.status.expires = -102;
                }
                state.bindInteger(3, user.status.expires);
            } else {
                state.bindInteger(3, 0);
            }
            state.bindByteBuffer(4, data2);
            state.step();
            data2.reuse();
        }
        state.dispose();
    }

    public void updateChatDefaultBannedRights(final long chatId, final TLRPC.TL_chatBannedRights rights, final int version) {
        if (rights == null || chatId == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda177
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1054x1da22d72(chatId, version, rights);
            }
        });
    }

    /* renamed from: lambda$updateChatDefaultBannedRights$143$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1054x1da22d72(long chatId, int version, TLRPC.TL_chatBannedRights rights) {
        NativeByteBuffer data;
        TLRPC.Chat chat = null;
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", Long.valueOf(chatId)), new Object[0]);
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                chat = TLRPC.Chat.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
            }
            cursor.dispose();
            if (chat == null) {
                return;
            }
            if (chat.default_banned_rights != null && version < chat.version) {
                return;
            }
            chat.default_banned_rights = rights;
            chat.flags |= 262144;
            chat.version = version;
            SQLitePreparedStatement state = this.database.executeFast("UPDATE chats SET data = ? WHERE uid = ?");
            NativeByteBuffer data2 = new NativeByteBuffer(chat.getObjectSize());
            chat.serializeToStream(data2);
            state.bindByteBuffer(1, data2);
            state.bindLong(2, chat.id);
            state.step();
            data2.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void putChatsInternal(ArrayList<TLRPC.Chat> chats) throws Exception {
        if (chats == null || chats.isEmpty()) {
            return;
        }
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
        for (int a = 0; a < chats.size(); a++) {
            TLRPC.Chat chat = chats.get(a);
            if (chat.min) {
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", Long.valueOf(chat.id)), new Object[0]);
                if (cursor.next()) {
                    try {
                        NativeByteBuffer data = cursor.byteBufferValue(0);
                        if (data != null) {
                            TLRPC.Chat oldChat = TLRPC.Chat.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                            if (oldChat != null) {
                                oldChat.title = chat.title;
                                oldChat.photo = chat.photo;
                                oldChat.broadcast = chat.broadcast;
                                oldChat.verified = chat.verified;
                                oldChat.megagroup = chat.megagroup;
                                oldChat.call_not_empty = chat.call_not_empty;
                                oldChat.call_active = chat.call_active;
                                if (chat.default_banned_rights != null) {
                                    oldChat.default_banned_rights = chat.default_banned_rights;
                                    oldChat.flags |= 262144;
                                }
                                if (chat.admin_rights != null) {
                                    oldChat.admin_rights = chat.admin_rights;
                                    oldChat.flags |= 16384;
                                }
                                if (chat.banned_rights != null) {
                                    oldChat.banned_rights = chat.banned_rights;
                                    oldChat.flags |= 32768;
                                }
                                if (chat.username != null) {
                                    oldChat.username = chat.username;
                                    oldChat.flags |= 64;
                                } else {
                                    oldChat.username = null;
                                    oldChat.flags &= -65;
                                }
                                chat = oldChat;
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                cursor.dispose();
            }
            state.requery();
            chat.flags |= 131072;
            NativeByteBuffer data2 = new NativeByteBuffer(chat.getObjectSize());
            chat.serializeToStream(data2);
            state.bindLong(1, chat.id);
            if (chat.title != null) {
                String name = chat.title.toLowerCase();
                state.bindString(2, name);
            } else {
                state.bindString(2, "");
            }
            state.bindByteBuffer(3, data2);
            state.step();
            data2.reuse();
        }
        state.dispose();
    }

    public void getUsersInternal(String usersToLoad, ArrayList<TLRPC.User> result) throws Exception {
        if (usersToLoad == null || usersToLoad.length() == 0 || result == null) {
            return;
        }
        SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", usersToLoad), new Object[0]);
        while (cursor.next()) {
            try {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    TLRPC.User user = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    if (user != null) {
                        if (user.status != null) {
                            user.status.expires = cursor.intValue(1);
                        }
                        result.add(user);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        cursor.dispose();
    }

    public void getChatsInternal(String chatsToLoad, ArrayList<TLRPC.Chat> result) throws Exception {
        if (chatsToLoad == null || chatsToLoad.length() == 0 || result == null) {
            return;
        }
        SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", chatsToLoad), new Object[0]);
        while (cursor.next()) {
            try {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    TLRPC.Chat chat = TLRPC.Chat.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    if (chat != null) {
                        result.add(chat);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        cursor.dispose();
    }

    public void getEncryptedChatsInternal(String chatsToLoad, ArrayList<TLRPC.EncryptedChat> result, ArrayList<Long> usersToLoad) throws Exception {
        if (chatsToLoad == null || chatsToLoad.length() == 0 || result == null) {
            return;
        }
        SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", chatsToLoad), new Object[0]);
        while (cursor.next()) {
            try {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    TLRPC.EncryptedChat chat = TLRPC.EncryptedChat.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    if (chat != null) {
                        chat.user_id = cursor.longValue(1);
                        if (usersToLoad != null && !usersToLoad.contains(Long.valueOf(chat.user_id))) {
                            usersToLoad.add(Long.valueOf(chat.user_id));
                        }
                        chat.a_or_b = cursor.byteArrayValue(2);
                        chat.auth_key = cursor.byteArrayValue(3);
                        chat.ttl = cursor.intValue(4);
                        chat.layer = cursor.intValue(5);
                        chat.seq_in = cursor.intValue(6);
                        chat.seq_out = cursor.intValue(7);
                        int use_count = cursor.intValue(8);
                        chat.key_use_count_in = (short) (use_count >> 16);
                        chat.key_use_count_out = (short) use_count;
                        chat.exchange_id = cursor.longValue(9);
                        chat.key_create_date = cursor.intValue(10);
                        chat.future_key_fingerprint = cursor.longValue(11);
                        chat.future_auth_key = cursor.byteArrayValue(12);
                        chat.key_hash = cursor.byteArrayValue(13);
                        chat.in_seq_no = cursor.intValue(14);
                        long admin_id = cursor.longValue(15);
                        if (admin_id != 0) {
                            chat.admin_id = admin_id;
                        }
                        chat.mtproto_seq = cursor.intValue(16);
                        result.add(chat);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        cursor.dispose();
    }

    /* renamed from: putUsersAndChatsInternal */
    public void m1023xd6d1abcf(ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, boolean withTransaction) {
        if (withTransaction) {
            try {
                this.database.beginTransaction();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        putUsersInternal(users);
        putChatsInternal(chats);
        if (withTransaction) {
            this.database.commitTransaction();
        }
    }

    public void putUsersAndChats(final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final boolean withTransaction, boolean useQueue) {
        if (users != null && users.isEmpty() && chats != null && chats.isEmpty()) {
            return;
        }
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda51
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1023xd6d1abcf(users, chats, withTransaction);
                }
            });
        } else {
            m1023xd6d1abcf(users, chats, withTransaction);
        }
    }

    public void removeFromDownloadQueue(final long id, final int type, final boolean move) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda94
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1030xf2d9f16a(move, type, id);
            }
        });
    }

    /* renamed from: lambda$removeFromDownloadQueue$145$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1030xf2d9f16a(boolean move, int type, long id) {
        try {
            if (move) {
                int minDate = -1;
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(date) FROM download_queue WHERE type = %d", Integer.valueOf(type)), new Object[0]);
                if (cursor.next()) {
                    minDate = cursor.intValue(0);
                }
                cursor.dispose();
                if (minDate != -1) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", Integer.valueOf(minDate - 1), Long.valueOf(id), Integer.valueOf(type))).stepThis().dispose();
                }
                return;
            }
            this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", Long.valueOf(id), Integer.valueOf(type))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void deleteFromDownloadQueue(final ArrayList<Pair<Long, Integer>> ids, boolean transaction) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        if (transaction) {
            try {
                this.database.beginTransaction();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLitePreparedStatement state = this.database.executeFast("DELETE FROM download_queue WHERE uid = ? AND type = ?");
        int N = ids.size();
        for (int a = 0; a < N; a++) {
            Pair<Long, Integer> pair = ids.get(a);
            state.requery();
            state.bindLong(1, ((Long) pair.first).longValue());
            state.bindInteger(2, ((Integer) pair.second).intValue());
            state.step();
        }
        state.dispose();
        if (transaction) {
            this.database.commitTransaction();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m925x50e562f0(ids);
            }
        });
    }

    /* renamed from: lambda$deleteFromDownloadQueue$146$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m925x50e562f0(ArrayList ids) {
        getDownloadController().cancelDownloading(ids);
    }

    public void clearDownloadQueue(final int type) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda121
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m908xfe22100d(type);
            }
        });
    }

    /* renamed from: lambda$clearDownloadQueue$147$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m908xfe22100d(int type) {
        try {
            if (type == 0) {
                this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", Integer.valueOf(type))).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDownloadQueue(final int type) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda123
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m947xa098b894(type);
            }
        });
    }

    /* renamed from: lambda$getDownloadQueue$149$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m947xa098b894(final int type) {
        try {
            final ArrayList<DownloadObject> objects = new ArrayList<>();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data, parent FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", Integer.valueOf(type)), new Object[0]);
            while (cursor.next()) {
                DownloadObject downloadObject = new DownloadObject();
                downloadObject.type = cursor.intValue(1);
                downloadObject.id = cursor.longValue(0);
                downloadObject.parent = cursor.stringValue(3);
                NativeByteBuffer data = cursor.byteBufferValue(2);
                if (data != null) {
                    TLRPC.MessageMedia messageMedia = TLRPC.MessageMedia.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    if (messageMedia.document != null) {
                        downloadObject.object = messageMedia.document;
                        downloadObject.secret = MessageObject.isVideoDocument(messageMedia.document) && messageMedia.ttl_seconds > 0 && messageMedia.ttl_seconds <= 60;
                    } else if (messageMedia.photo != null) {
                        downloadObject.object = messageMedia.photo;
                        downloadObject.secret = messageMedia.ttl_seconds > 0 && messageMedia.ttl_seconds <= 60;
                    }
                    downloadObject.forceCache = (messageMedia.flags & Integer.MIN_VALUE) != 0;
                }
                objects.add(downloadObject);
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda150
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m946x497ac7b5(type, objects);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$getDownloadQueue$148$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m946x497ac7b5(int type, ArrayList objects) {
        getDownloadController().processDownloadObjects(type, objects);
    }

    private int getMessageMediaType(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            if (!(message.media instanceof TLRPC.TL_messageMediaPhoto) && !MessageObject.isGifMessage(message) && !MessageObject.isVoiceMessage(message) && !MessageObject.isVideoMessage(message) && !MessageObject.isRoundVideoMessage(message)) {
                return -1;
            }
            return (message.ttl <= 0 || message.ttl > 60) ? 0 : 1;
        } else if ((message instanceof TLRPC.TL_message) && (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (message.media instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0)) {
            return 1;
        } else {
            return ((message.media instanceof TLRPC.TL_messageMediaPhoto) || MessageObject.isVideoMessage(message)) ? 0 : -1;
        }
    }

    public void putWebPages(final LongSparseArray<TLRPC.WebPage> webPages) {
        if (isEmpty(webPages)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1026lambda$putWebPages$151$orgtelegrammessengerMessagesStorage(webPages);
            }
        });
    }

    /* renamed from: lambda$putWebPages$151$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1026lambda$putWebPages$151$orgtelegrammessengerMessagesStorage(LongSparseArray webPages) {
        int N;
        SQLiteCursor cursor;
        try {
            final ArrayList<TLRPC.Message> messages = new ArrayList<>();
            int a = 0;
            int N2 = webPages.size();
            while (true) {
                int i = 2;
                if (a >= N2) {
                    break;
                }
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor cursor2 = sQLiteDatabase.queryFinalized("SELECT mid, uid FROM webpage_pending_v2 WHERE id = " + webPages.keyAt(a), new Object[0]);
                LongSparseArray<ArrayList<Integer>> dialogs = new LongSparseArray<>();
                while (cursor2.next()) {
                    long dialogId = cursor2.longValue(1);
                    ArrayList<Integer> mids = dialogs.get(dialogId);
                    if (mids == null) {
                        mids = new ArrayList<>();
                        dialogs.put(dialogId, mids);
                    }
                    mids.add(Integer.valueOf(cursor2.intValue(0)));
                }
                cursor2.dispose();
                if (dialogs.isEmpty()) {
                    N = N2;
                } else {
                    int b = 0;
                    int N22 = dialogs.size();
                    while (b < N22) {
                        long dialogId2 = dialogs.keyAt(b);
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        Locale locale = Locale.US;
                        int N3 = N2;
                        Object[] objArr = new Object[i];
                        objArr[0] = TextUtils.join(",", dialogs.valueAt(b));
                        objArr[1] = Long.valueOf(dialogId2);
                        SQLiteCursor cursor3 = sQLiteDatabase2.queryFinalized(String.format(locale, "SELECT mid, data FROM messages_v2 WHERE mid IN (%s) AND uid = %d", objArr), new Object[0]);
                        while (cursor3.next()) {
                            int mid = cursor3.intValue(0);
                            NativeByteBuffer data = cursor3.byteBufferValue(1);
                            if (data == null) {
                                cursor = cursor3;
                            } else {
                                TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                                cursor = cursor3;
                                message.readAttachPath(data, getUserConfig().clientUserId);
                                data.reuse();
                                if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
                                    message.id = mid;
                                    message.media.webpage = (TLRPC.WebPage) webPages.valueAt(a);
                                    messages.add(message);
                                }
                            }
                            cursor3 = cursor;
                        }
                        cursor3.dispose();
                        b++;
                        N2 = N3;
                        i = 2;
                    }
                    N = N2;
                }
                a++;
                N2 = N;
            }
            if (messages.isEmpty()) {
                return;
            }
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET data = ? WHERE mid = ? AND uid = ?");
            SQLitePreparedStatement state2 = this.database.executeFast("UPDATE media_v4 SET data = ? WHERE mid = ? AND uid = ?");
            for (int a2 = 0; a2 < messages.size(); a2++) {
                TLRPC.Message message2 = messages.get(a2);
                NativeByteBuffer data2 = new NativeByteBuffer(message2.getObjectSize());
                message2.serializeToStream(data2);
                state.requery();
                state.bindByteBuffer(1, data2);
                state.bindInteger(2, message2.id);
                state.bindLong(3, MessageObject.getDialogId(message2));
                state.step();
                state2.requery();
                state2.bindByteBuffer(1, data2);
                state2.bindInteger(2, message2.id);
                state2.bindLong(3, MessageObject.getDialogId(message2));
                state2.step();
                data2.reuse();
            }
            state.dispose();
            state2.dispose();
            this.database.commitTransaction();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1025lambda$putWebPages$150$orgtelegrammessengerMessagesStorage(messages);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$putWebPages$150$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1025lambda$putWebPages$150$orgtelegrammessengerMessagesStorage(ArrayList messages) {
        getNotificationCenter().postNotificationName(NotificationCenter.didReceivedWebpages, messages);
    }

    public void overwriteChannel(final long channelId, final TLRPC.TL_updates_channelDifferenceTooLong difference, final int newDialogType) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda178
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1007x1b88a3f6(channelId, newDialogType, difference);
            }
        });
    }

    /* renamed from: lambda$overwriteChannel$153$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1007x1b88a3f6(long channelId, int newDialogType, final TLRPC.TL_updates_channelDifferenceTooLong difference) {
        int pinned;
        boolean checkInvite = false;
        final long did = -channelId;
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT pinned FROM dialogs WHERE did = " + did, new Object[0]);
            if (!cursor.next()) {
                if (newDialogType == 0) {
                    pinned = 0;
                } else {
                    checkInvite = true;
                    pinned = 0;
                }
            } else {
                pinned = cursor.intValue(0);
            }
            cursor.dispose();
            this.database.executeFast("DELETE FROM chat_pinned_count WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM chat_pinned_v2 WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM messages_v2 WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("UPDATE media_counts_v2 SET old = 1 WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_v4 WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + did).stepThis().dispose();
            getMediaDataController().clearBotKeyboard(did, null);
            TLRPC.TL_messages_dialogs dialogs = new TLRPC.TL_messages_dialogs();
            dialogs.chats.addAll(difference.chats);
            dialogs.users.addAll(difference.users);
            dialogs.messages.addAll(difference.messages);
            TLRPC.Dialog dialog = difference.dialog;
            dialog.id = did;
            dialog.flags = 1;
            dialog.notify_settings = null;
            dialog.pinned = pinned != 0;
            dialog.pinnedNum = pinned;
            dialogs.dialogs.add(dialog);
            putDialogsInternal(dialogs, 0);
            updateDialogsWithDeletedMessages(-channelId, channelId, new ArrayList<>(), null, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1006xc46ab317(did, difference);
                }
            });
            if (checkInvite) {
                if (newDialogType == 1) {
                    getMessagesController().checkChatInviter(channelId, true);
                } else {
                    getMessagesController().generateJoinMessage(channelId, false);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$overwriteChannel$152$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1006xc46ab317(long did, TLRPC.TL_updates_channelDifferenceTooLong difference) {
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), true, difference);
    }

    public void putChannelViews(final LongSparseArray<SparseIntArray> channelViews, final LongSparseArray<SparseIntArray> channelForwards, final LongSparseArray<SparseArray<TLRPC.MessageReplies>> channelReplies, final boolean addReply) {
        if (isEmpty(channelViews) && isEmpty(channelForwards) && isEmpty(channelReplies)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1012x9e06ed0d(channelViews, channelForwards, channelReplies, addReply);
            }
        });
    }

    /* renamed from: lambda$putChannelViews$154$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1012x9e06ed0d(LongSparseArray channelViews, LongSparseArray channelForwards, LongSparseArray channelReplies, boolean addReply) {
        SparseArray<TLRPC.MessageReplies> messages;
        NativeByteBuffer data;
        LongSparseArray longSparseArray = channelReplies;
        try {
            this.database.beginTransaction();
            int i = 4;
            int i2 = 2;
            if (!isEmpty(channelViews)) {
                SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET media = max((SELECT media FROM messages_v2 WHERE mid = ? AND uid = ?), ?) WHERE mid = ? AND uid = ?");
                int a = 0;
                while (a < channelViews.size()) {
                    long peer = channelViews.keyAt(a);
                    SparseIntArray messages2 = (SparseIntArray) channelViews.valueAt(a);
                    int b = 0;
                    int N = messages2.size();
                    while (b < N) {
                        int views = messages2.valueAt(b);
                        int messageId = messages2.keyAt(b);
                        state.requery();
                        state.bindInteger(1, messageId);
                        state.bindLong(i2, peer);
                        state.bindInteger(3, views);
                        state.bindInteger(i, messageId);
                        state.bindLong(5, peer);
                        state.step();
                        b++;
                        i = 4;
                        i2 = 2;
                    }
                    a++;
                    i = 4;
                    i2 = 2;
                }
                state.dispose();
            }
            if (!isEmpty(channelForwards)) {
                SQLitePreparedStatement state2 = this.database.executeFast("UPDATE messages_v2 SET forwards = max((SELECT forwards FROM messages_v2 WHERE mid = ? AND uid = ?), ?) WHERE mid = ? AND uid = ?");
                for (int a2 = 0; a2 < channelForwards.size(); a2++) {
                    long peer2 = channelForwards.keyAt(a2);
                    SparseIntArray messages3 = (SparseIntArray) channelForwards.valueAt(a2);
                    int N2 = messages3.size();
                    for (int b2 = 0; b2 < N2; b2++) {
                        int forwards = messages3.valueAt(b2);
                        int messageId2 = messages3.keyAt(b2);
                        state2.requery();
                        state2.bindInteger(1, messageId2);
                        state2.bindLong(2, peer2);
                        state2.bindInteger(3, forwards);
                        state2.bindInteger(4, messageId2);
                        state2.bindLong(5, peer2);
                        state2.step();
                    }
                }
                state2.dispose();
            }
            if (!isEmpty(channelReplies)) {
                SQLitePreparedStatement state3 = this.database.executeFast("UPDATE messages_v2 SET replies_data = ? WHERE mid = ? AND uid = ?");
                int a3 = 0;
                while (a3 < channelReplies.size()) {
                    long peer3 = longSparseArray.keyAt(a3);
                    SparseArray<TLRPC.MessageReplies> messages4 = (SparseArray) longSparseArray.valueAt(a3);
                    int b3 = 0;
                    int N3 = messages4.size();
                    while (b3 < N3) {
                        int messageId3 = messages4.keyAt(b3);
                        TLRPC.MessageReplies currentReplies = null;
                        SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT replies_data FROM messages_v2 WHERE mid = %d AND uid = %d", Integer.valueOf(messageId3), Long.valueOf(peer3)), new Object[0]);
                        boolean messageExists = cursor.next();
                        if (messageExists && (data = cursor.byteBufferValue(0)) != null) {
                            currentReplies = TLRPC.MessageReplies.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                        }
                        cursor.dispose();
                        if (!messageExists) {
                            messages = messages4;
                        } else {
                            TLRPC.MessageReplies replies = messages4.get(messages4.keyAt(b3));
                            if (!addReply && currentReplies != null && currentReplies.replies_pts != 0 && replies.replies_pts <= currentReplies.replies_pts && replies.read_max_id <= currentReplies.read_max_id && replies.max_id <= currentReplies.max_id) {
                                messages = messages4;
                            } else {
                                if (addReply) {
                                    if (currentReplies == null) {
                                        currentReplies = new TLRPC.TL_messageReplies();
                                        currentReplies.flags |= 2;
                                    }
                                    currentReplies.replies += replies.replies;
                                    int c = 0;
                                    int N4 = replies.recent_repliers.size();
                                    while (c < N4) {
                                        long id = MessageObject.getPeerId(replies.recent_repliers.get(c));
                                        int d = 0;
                                        SQLiteCursor cursor2 = cursor;
                                        int N22 = currentReplies.recent_repliers.size();
                                        while (d < N22) {
                                            SparseArray<TLRPC.MessageReplies> messages5 = messages4;
                                            long id2 = MessageObject.getPeerId(currentReplies.recent_repliers.get(d));
                                            if (id == id2) {
                                                currentReplies.recent_repliers.remove(d);
                                                d--;
                                                N22--;
                                            }
                                            d++;
                                            messages4 = messages5;
                                        }
                                        c++;
                                        cursor = cursor2;
                                    }
                                    messages = messages4;
                                    currentReplies.recent_repliers.addAll(0, replies.recent_repliers);
                                    while (currentReplies.recent_repliers.size() > 3) {
                                        currentReplies.recent_repliers.remove(0);
                                    }
                                    replies = currentReplies;
                                } else {
                                    messages = messages4;
                                }
                                if (currentReplies != null && currentReplies.read_max_id > replies.read_max_id) {
                                    replies.read_max_id = currentReplies.read_max_id;
                                }
                                state3.requery();
                                NativeByteBuffer data2 = new NativeByteBuffer(replies.getObjectSize());
                                replies.serializeToStream(data2);
                                state3.bindByteBuffer(1, data2);
                                state3.bindInteger(2, messageId3);
                                state3.bindLong(3, peer3);
                                state3.step();
                                data2.reuse();
                            }
                        }
                        b3++;
                        messages4 = messages;
                    }
                    a3++;
                    longSparseArray = channelReplies;
                }
                state3.dispose();
            }
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: updateRepliesMaxReadIdInternal */
    public void m1085x22aed16(long chatId, int mid, int readMaxId) {
        Exception e;
        NativeByteBuffer data;
        long dialogId = -chatId;
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET replies_data = ? WHERE mid = ? AND uid = ?");
            TLRPC.MessageReplies currentReplies = null;
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT replies_data FROM messages_v2 WHERE mid = %d AND uid = %d", Integer.valueOf(mid), Long.valueOf(dialogId)), new Object[0]);
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                currentReplies = TLRPC.MessageReplies.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
            }
            cursor.dispose();
            if (currentReplies != null) {
                try {
                    currentReplies.read_max_id = readMaxId;
                    state.requery();
                    NativeByteBuffer data2 = new NativeByteBuffer(currentReplies.getObjectSize());
                    currentReplies.serializeToStream(data2);
                    state.bindByteBuffer(1, data2);
                    try {
                        state.bindInteger(2, mid);
                        state.bindLong(3, dialogId);
                        state.step();
                        data2.reuse();
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        return;
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e);
                    return;
                }
            }
            state.dispose();
        } catch (Exception e4) {
            e = e4;
        }
    }

    public void updateRepliesMaxReadId(final long chatId, final int mid, final int readMaxId, boolean useQueue) {
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda169
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1085x22aed16(chatId, mid, readMaxId);
                }
            });
        } else {
            m1085x22aed16(chatId, mid, readMaxId);
        }
    }

    public void updateRepliesCount(final long chatId, final int mid, final ArrayList<TLRPC.Peer> repliers, final int maxId, final int count) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda142
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1084x69d4697b(mid, chatId, count, repliers, maxId);
            }
        });
    }

    /* renamed from: lambda$updateRepliesCount$156$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1084x69d4697b(int mid, long chatId, int count, ArrayList repliers, int maxId) {
        Exception e;
        NativeByteBuffer data;
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET replies_data = ? WHERE mid = ? AND uid = ?");
            TLRPC.MessageReplies currentReplies = null;
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.ENGLISH, "SELECT replies_data FROM messages_v2 WHERE mid = %d AND uid = %d", Integer.valueOf(mid), Long.valueOf(-chatId)), new Object[0]);
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                currentReplies = TLRPC.MessageReplies.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
            }
            cursor.dispose();
            if (currentReplies != null) {
                currentReplies.replies += count;
                if (currentReplies.replies < 0) {
                    currentReplies.replies = 0;
                }
                if (repliers != null) {
                    currentReplies.recent_repliers = repliers;
                    currentReplies.flags |= 2;
                }
                if (maxId != 0) {
                    currentReplies.max_id = maxId;
                }
                state.requery();
                NativeByteBuffer data2 = new NativeByteBuffer(currentReplies.getObjectSize());
                currentReplies.serializeToStream(data2);
                state.bindByteBuffer(1, data2);
                try {
                    state.bindInteger(2, mid);
                    state.bindLong(3, -chatId);
                    state.step();
                    data2.reuse();
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    return;
                }
            }
            state.dispose();
        } catch (Exception e3) {
            e = e3;
        }
    }

    private boolean isValidKeyboardToSave(TLRPC.Message message) {
        return message.reply_markup != null && !(message.reply_markup instanceof TLRPC.TL_replyInlineMarkup) && (!message.reply_markup.selective || message.mentioned);
    }

    public void updateMessageVerifyFlags(final ArrayList<TLRPC.Message> messages) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1076xb266a8ba(messages);
            }
        });
    }

    /* renamed from: lambda$updateMessageVerifyFlags$157$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1076xb266a8ba(ArrayList messages) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages_v2 SET imp = ? WHERE mid = ? AND uid = ?");
            int N = messages.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Message message = (TLRPC.Message) messages.get(a);
                state.requery();
                int flags = 0;
                if (message.stickerVerified == 0) {
                    flags = 0 | 1;
                } else if (message.stickerVerified == 2) {
                    flags = 0 | 2;
                }
                state.bindInteger(1, flags);
                state.bindInteger(2, message.id);
                state.bindLong(3, MessageObject.getDialogId(message));
                state.step();
            }
            state.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:248:0x0722 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x072a A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:252:0x0738 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:253:0x073b A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:258:0x074b  */
    /* JADX WARN: Removed duplicated region for block: B:259:0x074d  */
    /* JADX WARN: Removed duplicated region for block: B:262:0x075f A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:263:0x0778 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:266:0x0783 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:271:0x0798 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:274:0x07b2 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:275:0x07b6 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:278:0x07c4 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:279:0x07e1  */
    /* JADX WARN: Removed duplicated region for block: B:282:0x07ed  */
    /* JADX WARN: Removed duplicated region for block: B:286:0x0823  */
    /* JADX WARN: Removed duplicated region for block: B:292:0x082f A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:293:0x083a  */
    /* JADX WARN: Removed duplicated region for block: B:297:0x086c  */
    /* JADX WARN: Removed duplicated region for block: B:301:0x089e A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:306:0x08c7 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:308:0x08cc A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:311:0x08d4 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:423:0x0c61 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:430:0x0c92 A[Catch: Exception -> 0x0112, TryCatch #0 {Exception -> 0x0112, blocks: (B:5:0x000d, B:6:0x0012, B:7:0x0026, B:9:0x002c, B:12:0x0037, B:14:0x0043, B:15:0x0046, B:17:0x0081, B:18:0x0095, B:20:0x00a2, B:21:0x00a9, B:22:0x00af, B:24:0x00b7, B:25:0x00bc, B:27:0x00c3, B:31:0x00d4, B:33:0x00e2, B:34:0x00e5, B:36:0x0107, B:37:0x010c, B:43:0x0118, B:44:0x011d, B:45:0x019f, B:47:0x01a9, B:49:0x01ba, B:51:0x01be, B:53:0x01cf, B:55:0x01e1, B:57:0x01f2, B:60:0x01fa, B:62:0x0200, B:66:0x0213, B:68:0x0217, B:70:0x021d, B:72:0x022a, B:74:0x0251, B:76:0x0259, B:78:0x0269, B:80:0x026d, B:84:0x027c, B:86:0x0289, B:88:0x029b, B:90:0x02a1, B:91:0x02a4, B:93:0x02b5, B:95:0x02c7, B:97:0x02db, B:100:0x02e3, B:102:0x02f8, B:104:0x0302, B:105:0x030d, B:107:0x0313, B:108:0x0316, B:110:0x0323, B:112:0x0333, B:114:0x0344, B:116:0x0354, B:118:0x035f, B:120:0x0365, B:122:0x036f, B:124:0x0375, B:125:0x037a, B:127:0x03ad, B:129:0x03b3, B:131:0x03c9, B:133:0x03d2, B:134:0x041c, B:136:0x0422, B:138:0x0438, B:142:0x044c, B:143:0x0452, B:145:0x045b, B:146:0x0464, B:148:0x0470, B:150:0x047d, B:152:0x0485, B:154:0x048c, B:156:0x04b4, B:157:0x04ca, B:160:0x04da, B:162:0x04e1, B:164:0x04ea, B:166:0x04f4, B:167:0x0504, B:170:0x0511, B:173:0x051c, B:174:0x0528, B:177:0x055f, B:179:0x0565, B:181:0x056c, B:182:0x05ae, B:184:0x05b4, B:186:0x05bf, B:188:0x05c4, B:190:0x05c8, B:193:0x05d7, B:195:0x05e1, B:198:0x05ec, B:200:0x05f7, B:204:0x0616, B:206:0x061c, B:208:0x0634, B:209:0x0637, B:211:0x064a, B:213:0x0652, B:216:0x0663, B:218:0x0667, B:222:0x069f, B:225:0x06ae, B:227:0x06ba, B:229:0x06c0, B:231:0x06c4, B:233:0x06ca, B:235:0x06ce, B:237:0x06d4, B:239:0x06dd, B:241:0x0709, B:246:0x0711, B:248:0x0722, B:249:0x072a, B:250:0x0733, B:252:0x0738, B:253:0x073b, B:255:0x0740, B:256:0x0742, B:260:0x074e, B:262:0x075f, B:263:0x0778, B:264:0x077f, B:266:0x0783, B:268:0x0789, B:269:0x078e, B:270:0x0792, B:271:0x0798, B:272:0x079e, B:274:0x07b2, B:275:0x07b6, B:276:0x07b9, B:278:0x07c4, B:280:0x07e7, B:283:0x07ef, B:285:0x07fc, B:287:0x0825, B:289:0x0829, B:292:0x082f, B:294:0x083c, B:295:0x0866, B:298:0x086e, B:300:0x087b, B:301:0x089e, B:303:0x08a6, B:306:0x08c7, B:308:0x08cc, B:309:0x08cf, B:311:0x08d4, B:313:0x08de, B:317:0x08e9, B:319:0x08f7, B:321:0x0902, B:323:0x0908, B:325:0x090e, B:329:0x091b, B:331:0x0931, B:332:0x0955, B:334:0x0963, B:337:0x0973, B:339:0x0979, B:341:0x097f, B:345:0x0991, B:348:0x09b2, B:350:0x09c0, B:352:0x09dd, B:359:0x0a05, B:361:0x0a21, B:363:0x0a36, B:365:0x0a3c, B:366:0x0a4a, B:368:0x0a83, B:370:0x0a92, B:373:0x0ac5, B:374:0x0ae0, B:376:0x0afb, B:378:0x0b00, B:380:0x0b0f, B:381:0x0b12, B:382:0x0b31, B:384:0x0b37, B:387:0x0b5b, B:389:0x0b96, B:392:0x0bd0, B:395:0x0be3, B:398:0x0c06, B:402:0x0c14, B:405:0x0c1e, B:408:0x0c29, B:410:0x0c2f, B:415:0x0c3a, B:419:0x0c4a, B:421:0x0c56, B:423:0x0c61, B:427:0x0c6a, B:429:0x0c6f, B:430:0x0c92, B:434:0x0ca1, B:436:0x0ca6, B:440:0x0cd5, B:441:0x0d07, B:442:0x0d1f, B:444:0x0d3b, B:446:0x0d4b, B:448:0x0d5e, B:450:0x0d98, B:451:0x0da4, B:453:0x0daa, B:455:0x0dcf, B:456:0x0ddc, B:457:0x0df2, B:460:0x0e04, B:461:0x0e09, B:463:0x0e1e, B:464:0x0e28), top: B:467:0x0009 }] */
    /* renamed from: putMessagesInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m1017lambda$putMessages$159$orgtelegrammessengerMessagesStorage(java.util.ArrayList<org.telegram.tgnet.TLRPC.Message> r68, boolean r69, boolean r70, int r71, boolean r72, boolean r73) {
        /*
            Method dump skipped, instructions count: 3634
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m1017lambda$putMessages$159$orgtelegrammessengerMessagesStorage(java.util.ArrayList, boolean, boolean, int, boolean, boolean):void");
    }

    /* renamed from: lambda$putMessagesInternal$158$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1020xa17c4dcb(int downloadMediaMaskFinal) {
        getDownloadController().newDownloadObjectsAvailable(downloadMediaMaskFinal);
    }

    public void putMessages(ArrayList<TLRPC.Message> messages, boolean withTransaction, boolean useQueue, boolean doNotUpdateDialogDate, int downloadMask, boolean scheduled) {
        putMessages(messages, withTransaction, useQueue, doNotUpdateDialogDate, downloadMask, false, scheduled);
    }

    public void putMessages(final ArrayList<TLRPC.Message> messages, final boolean withTransaction, boolean useQueue, final boolean doNotUpdateDialogDate, final int downloadMask, final boolean ifNoLastMessage, final boolean scheduled) {
        if (messages.size() == 0) {
            return;
        }
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda53
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1017lambda$putMessages$159$orgtelegrammessengerMessagesStorage(messages, withTransaction, doNotUpdateDialogDate, downloadMask, ifNoLastMessage, scheduled);
                }
            });
        } else {
            m1017lambda$putMessages$159$orgtelegrammessengerMessagesStorage(messages, withTransaction, doNotUpdateDialogDate, downloadMask, ifNoLastMessage, scheduled);
        }
    }

    public void markMessageAsSendError(final TLRPC.Message message, final boolean scheduled) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda80
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m992x8cd091e(message, scheduled);
            }
        });
    }

    /* renamed from: lambda$markMessageAsSendError$160$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m992x8cd091e(TLRPC.Message message, boolean scheduled) {
        try {
            long messageId = message.id;
            if (scheduled) {
                this.database.executeFast(String.format(Locale.US, "UPDATE scheduled_messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(messageId), Long.valueOf(MessageObject.getDialogId(message)))).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(messageId), Long.valueOf(MessageObject.getDialogId(message)))).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setMessageSeq(final int mid, final int seq_in, final int seq_out) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda128
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1051lambda$setMessageSeq$161$orgtelegrammessengerMessagesStorage(mid, seq_in, seq_out);
            }
        });
    }

    /* renamed from: lambda$setMessageSeq$161$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1051lambda$setMessageSeq$161$orgtelegrammessengerMessagesStorage(int mid, int seq_in, int seq_out) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
            state.requery();
            state.bindInteger(1, mid);
            state.bindInteger(2, seq_in);
            state.bindInteger(3, seq_out);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:120:0x0200, code lost:
        if (r7 != null) goto L121;
     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x0202, code lost:
        r7.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:126:0x020e, code lost:
        if (r7 == null) goto L127;
     */
    /* JADX WARN: Code restructure failed: missing block: B:128:0x021d, code lost:
        return new long[]{r13, r27};
     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x0243, code lost:
        if (r7 != null) goto L145;
     */
    /* JADX WARN: Code restructure failed: missing block: B:144:0x0298, code lost:
        if (r7 == null) goto L202;
     */
    /* JADX WARN: Code restructure failed: missing block: B:145:0x029a, code lost:
        r7.dispose();
        r7 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x029e, code lost:
        r7 = r21.database.executeFast("UPDATE media_v4 SET mid = ? WHERE mid = ? AND uid = ?");
        r7.bindInteger(1, r27);
        r7.bindInteger(2, r12);
        r7.bindLong(3, r13);
        r7.step();
     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x02b6, code lost:
        if (r7 == null) goto L211;
     */
    /* JADX WARN: Code restructure failed: missing block: B:148:0x02b9, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x02bd, code lost:
        r21.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM media_v4 WHERE mid = %d AND uid = %d", java.lang.Integer.valueOf(r12), java.lang.Long.valueOf(r13))).stepThis().dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:152:0x02e6, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:153:0x02e7, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:154:0x02ea, code lost:
        if (r7 == null) goto L211;
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x02ec, code lost:
        r7.dispose();
        r7 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:156:0x02f0, code lost:
        r7 = r21.database.executeFast("UPDATE dialogs SET last_mid = ? WHERE last_mid = ?");
        r7.bindInteger(1, r27);
        r7.bindInteger(2, r12);
        r7.step();
     */
    /* JADX WARN: Code restructure failed: missing block: B:157:0x0304, code lost:
        if (r7 == null) goto L182;
     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x0306, code lost:
        r7.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x030a, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x030c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:161:0x030d, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x0310, code lost:
        if (r7 == null) goto L182;
     */
    /* JADX WARN: Code restructure failed: missing block: B:163:0x0314, code lost:
        if (r7 != null) goto L164;
     */
    /* JADX WARN: Code restructure failed: missing block: B:164:0x0316, code lost:
        r7.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:165:0x0319, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:166:0x031a, code lost:
        if (r7 != null) goto L167;
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x031c, code lost:
        r7.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x0320, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:173:0x0340, code lost:
        if (r7 != null) goto L174;
     */
    /* JADX WARN: Code restructure failed: missing block: B:174:0x0342, code lost:
        r7.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x0377, code lost:
        if (r7 == null) goto L182;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x008c, code lost:
        if (r15 != null) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x009b, code lost:
        if (r15 == null) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x009d, code lost:
        r15.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00a0, code lost:
        r18 = r5;
     */
    /* JADX WARN: Removed duplicated region for block: B:100:0x01b7  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x01d5 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:113:0x01d7  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x0223  */
    /* JADX WARN: Removed duplicated region for block: B:189:0x0392  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x0399  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x005a A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x005e  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00a7 A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x012e  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x01aa A[Catch: Exception -> 0x01bb, all -> 0x038f, TRY_LEAVE, TryCatch #22 {all -> 0x038f, blocks: (B:94:0x0197, B:96:0x01aa, B:106:0x01c5), top: B:219:0x0187 }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x01b3  */
    /* renamed from: updateMessageStateAndIdInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public long[] m1074x549c500a(long r22, long r24, java.lang.Integer r26, int r27, int r28, int r29) {
        /*
            Method dump skipped, instructions count: 927
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m1074x549c500a(long, long, java.lang.Integer, int, int, int):long[]");
    }

    /* renamed from: lambda$updateMessageStateAndIdInternal$162$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1075x8dec4e2e(TLRPC.TL_updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    public long[] updateMessageStateAndId(final long random_id, final long dialogId, final Integer _oldId, final int newId, final int date, boolean useQueue, final int scheduled) {
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda187
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1074x549c500a(random_id, dialogId, _oldId, newId, date, scheduled);
                }
            });
            return null;
        }
        return m1074x549c500a(random_id, dialogId, _oldId, newId, date, scheduled);
    }

    /* renamed from: updateUsersInternal */
    public void m1088lambda$updateUsers$164$orgtelegrammessengerMessagesStorage(ArrayList<TLRPC.User> users, boolean onlyStatus, boolean withTransaction) {
        try {
            if (onlyStatus) {
                if (withTransaction) {
                    this.database.beginTransaction();
                }
                SQLitePreparedStatement state = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
                int N = users.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.User user = users.get(a);
                    state.requery();
                    if (user.status != null) {
                        state.bindInteger(1, user.status.expires);
                    } else {
                        state.bindInteger(1, 0);
                    }
                    state.bindLong(2, user.id);
                    state.step();
                }
                state.dispose();
                if (withTransaction) {
                    this.database.commitTransaction();
                }
                return;
            }
            StringBuilder ids = new StringBuilder();
            LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
            int N2 = users.size();
            for (int a2 = 0; a2 < N2; a2++) {
                TLRPC.User user2 = users.get(a2);
                if (ids.length() != 0) {
                    ids.append(",");
                }
                ids.append(user2.id);
                usersDict.put(user2.id, user2);
            }
            ArrayList<TLRPC.User> loadedUsers = new ArrayList<>();
            getUsersInternal(ids.toString(), loadedUsers);
            int N3 = loadedUsers.size();
            for (int a3 = 0; a3 < N3; a3++) {
                TLRPC.User user3 = loadedUsers.get(a3);
                TLRPC.User updateUser = usersDict.get(user3.id);
                if (updateUser != null) {
                    if (updateUser.first_name != null && updateUser.last_name != null) {
                        if (!UserObject.isContact(user3)) {
                            user3.first_name = updateUser.first_name;
                            user3.last_name = updateUser.last_name;
                        }
                        user3.username = updateUser.username;
                    } else if (updateUser.photo != null) {
                        user3.photo = updateUser.photo;
                    } else if (updateUser.phone != null) {
                        user3.phone = updateUser.phone;
                    }
                }
            }
            if (!loadedUsers.isEmpty()) {
                if (withTransaction) {
                    this.database.beginTransaction();
                }
                putUsersInternal(loadedUsers);
                if (withTransaction) {
                    this.database.commitTransaction();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateUsers(final ArrayList<TLRPC.User> users, final boolean onlyStatus, final boolean withTransaction, boolean useQueue) {
        if (users == null || users.isEmpty()) {
            return;
        }
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda52
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1088lambda$updateUsers$164$orgtelegrammessengerMessagesStorage(users, onlyStatus, withTransaction);
                }
            });
        } else {
            m1088lambda$updateUsers$164$orgtelegrammessengerMessagesStorage(users, onlyStatus, withTransaction);
        }
    }

    /* renamed from: markMessagesAsReadInternal */
    public void m1000x36e31eab(LongSparseIntArray inbox, LongSparseIntArray outbox, SparseIntArray encryptedMessages) {
        try {
            if (!isEmpty(inbox)) {
                SQLitePreparedStatement state = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                for (int b = 0; b < inbox.size(); b++) {
                    long key = inbox.keyAt(b);
                    int messageId = inbox.get(key);
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", Long.valueOf(key), Integer.valueOf(messageId))).stepThis().dispose();
                    state.requery();
                    state.bindLong(1, key);
                    state.bindInteger(2, messageId);
                    state.step();
                }
                state.dispose();
            }
            if (!isEmpty(outbox)) {
                for (int b2 = 0; b2 < outbox.size(); b2++) {
                    long key2 = outbox.keyAt(b2);
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", Long.valueOf(key2), Integer.valueOf(outbox.get(key2)))).stepThis().dispose();
                }
            }
            if (encryptedMessages != null && !isEmpty(encryptedMessages)) {
                for (int a = 0; a < encryptedMessages.size(); a++) {
                    long dialogId = DialogObject.makeEncryptedDialogId(encryptedMessages.keyAt(a));
                    int max_date = encryptedMessages.valueAt(a);
                    SQLitePreparedStatement state2 = this.database.executeFast("UPDATE messages_v2 SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
                    state2.requery();
                    state2.bindLong(1, dialogId);
                    state2.bindInteger(2, max_date);
                    state2.step();
                    state2.dispose();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void markMessagesContentAsReadInternal(long dialogId, ArrayList<Integer> mids, int date) {
        try {
            String midsStr = TextUtils.join(",", mids);
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid IN (%s) AND uid = %d", midsStr, Long.valueOf(dialogId))).stepThis().dispose();
            if (date != 0) {
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages_v2 WHERE mid IN (%s) AND uid = %d AND ttl > 0", midsStr, Long.valueOf(dialogId)), new Object[0]);
                ArrayList<Integer> arrayList = null;
                while (cursor.next()) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(Integer.valueOf(cursor.intValue(0)));
                }
                if (arrayList != null) {
                    emptyMessagesMedia(dialogId, arrayList);
                }
                cursor.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void markMessagesContentAsRead(final long dialogId, final ArrayList<Integer> mids, final int date) {
        if (isEmpty(mids)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda197
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1001x37aba7bf(dialogId, mids, date);
            }
        });
    }

    /* renamed from: lambda$markMessagesContentAsRead$165$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1001x37aba7bf(long dialogId, ArrayList mids, int date) {
        if (dialogId == 0) {
            try {
                LongSparseArray<ArrayList<Integer>> sparseArray = new LongSparseArray<>();
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, mid FROM messages_v2 WHERE mid IN (%s) AND is_channel = 0", TextUtils.join(",", mids)), new Object[0]);
                while (cursor.next()) {
                    long did = cursor.longValue(0);
                    ArrayList<Integer> arrayList = sparseArray.get(did);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        sparseArray.put(did, arrayList);
                    }
                    arrayList.add(Integer.valueOf(cursor.intValue(1)));
                }
                cursor.dispose();
                int N = sparseArray.size();
                for (int a = 0; a < N; a++) {
                    markMessagesContentAsReadInternal(sparseArray.keyAt(a), sparseArray.valueAt(a), date);
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        markMessagesContentAsReadInternal(dialogId, mids, date);
    }

    public void markMessagesAsRead(final LongSparseIntArray inbox, final LongSparseIntArray outbox, final SparseIntArray encryptedMessages, boolean useQueue) {
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda60
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1000x36e31eab(inbox, outbox, encryptedMessages);
                }
            });
        } else {
            m1000x36e31eab(inbox, outbox, encryptedMessages);
        }
    }

    public void markMessagesAsDeletedByRandoms(final ArrayList<Long> messages) {
        if (messages.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m997xd772581f(messages);
            }
        });
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:20:0x0096
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:92)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:44)
        */
    /* renamed from: lambda$markMessagesAsDeletedByRandoms$168$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m997xd772581f(java.util.ArrayList r19) {
        /*
            r18 = this;
            r11 = r18
            java.lang.String r0 = ","
            r12 = r19
            java.lang.String r0 = android.text.TextUtils.join(r0, r12)     // Catch: java.lang.Exception -> L94
            org.telegram.SQLite.SQLiteDatabase r1 = r11.database     // Catch: java.lang.Exception -> L94
            java.util.Locale r2 = java.util.Locale.US     // Catch: java.lang.Exception -> L94
            java.lang.String r3 = "SELECT mid, uid FROM randoms_v2 WHERE random_id IN(%s)"
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch: java.lang.Exception -> L94
            r6 = 0
            r5[r6] = r0     // Catch: java.lang.Exception -> L94
            java.lang.String r2 = java.lang.String.format(r2, r3, r5)     // Catch: java.lang.Exception -> L94
            java.lang.Object[] r3 = new java.lang.Object[r6]     // Catch: java.lang.Exception -> L94
            org.telegram.SQLite.SQLiteCursor r1 = r1.queryFinalized(r2, r3)     // Catch: java.lang.Exception -> L94
            r13 = r1
            androidx.collection.LongSparseArray r1 = new androidx.collection.LongSparseArray     // Catch: java.lang.Exception -> L94
            r1.<init>()     // Catch: java.lang.Exception -> L94
            r14 = r1
        L27:
            boolean r1 = r13.next()     // Catch: java.lang.Exception -> L94
            if (r1 == 0) goto L4f
            long r1 = r13.longValue(r4)     // Catch: java.lang.Exception -> L94
            java.lang.Object r3 = r14.get(r1)     // Catch: java.lang.Exception -> L94
            java.util.ArrayList r3 = (java.util.ArrayList) r3     // Catch: java.lang.Exception -> L94
            if (r3 != 0) goto L42
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch: java.lang.Exception -> L94
            r5.<init>()     // Catch: java.lang.Exception -> L94
            r3 = r5
            r14.put(r1, r3)     // Catch: java.lang.Exception -> L94
        L42:
            int r5 = r13.intValue(r6)     // Catch: java.lang.Exception -> L94
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch: java.lang.Exception -> L94
            r3.add(r5)     // Catch: java.lang.Exception -> L94
            goto L27
        L4f:
            r13.dispose()     // Catch: java.lang.Exception -> L94
            boolean r1 = r14.isEmpty()     // Catch: java.lang.Exception -> L94
            if (r1 != 0) goto L93
            r1 = 0
            int r2 = r14.size()     // Catch: java.lang.Exception -> L94
            r15 = r2
            r10 = r1
        L5f:
            if (r10 >= r15) goto L91
            long r2 = r14.keyAt(r10)     // Catch: java.lang.Exception -> L94
            java.lang.Object r1 = r14.valueAt(r10)     // Catch: java.lang.Exception -> L94
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch: java.lang.Exception -> L94
            r9 = r1
            org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda35 r1 = new org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda35     // Catch: java.lang.Exception -> L94
            r1.<init>()     // Catch: java.lang.Exception -> L94
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch: java.lang.Exception -> L94
            r1 = 0
            r11.updateDialogsWithReadMessagesInternal(r9, r1, r1, r1)     // Catch: java.lang.Exception -> L94
            r5 = 1
            r6 = 0
            r1 = r18
            r4 = r9
            r1.m994x707d8e9d(r2, r4, r5, r6)     // Catch: java.lang.Exception -> L94
            r7 = 0
            r1 = 0
            r4 = r18
            r5 = r2
            r16 = r9
            r17 = r10
            r10 = r1
            r4.m1064xf68cfb5(r5, r7, r9, r10)     // Catch: java.lang.Exception -> L94
            int r10 = r17 + 1
            goto L5f
        L91:
            r17 = r10
        L93:
            goto L9c
        L94:
            r0 = move-exception
            goto L99
        L96:
            r0 = move-exception
            r12 = r19
        L99:
            org.telegram.messenger.FileLog.e(r0)
        L9c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m997xd772581f(java.util.ArrayList):void");
    }

    /* renamed from: lambda$markMessagesAsDeletedByRandoms$167$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m996x80546740(ArrayList mids) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, mids, 0L, false);
    }

    public void deletePushMessages(long dialogId, ArrayList<Integer> messages) {
        try {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM unread_push_messages WHERE uid = %d AND mid IN(%s)", Long.valueOf(dialogId), TextUtils.join(",", messages))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void broadcastScheduledMessagesChange(final Long did) {
        final int count;
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM scheduled_messages_v2 WHERE uid = %d", did), new Object[0]);
            if (cursor.next()) {
                count = cursor.intValue(0);
            } else {
                count = 0;
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m900xa2c12b01(did, count);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$broadcastScheduledMessagesChange$169$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m900xa2c12b01(Long did, int count) {
        getNotificationCenter().postNotificationName(NotificationCenter.scheduledMessagesUpdated, did, Integer.valueOf(count));
    }

    /* JADX WARN: Removed duplicated region for block: B:106:0x0413  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x074d  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0793 A[Catch: Exception -> 0x079d, TryCatch #1 {Exception -> 0x079d, blocks: (B:172:0x078a, B:174:0x0793, B:175:0x0797), top: B:186:0x078a }] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x02b6  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0364 A[Catch: Exception -> 0x07a3, TryCatch #10 {Exception -> 0x07a3, blocks: (B:3:0x0006, B:6:0x0013, B:14:0x0057, B:15:0x005a, B:17:0x0083, B:19:0x0093, B:21:0x00cb, B:22:0x00ee, B:79:0x02a1, B:80:0x02a4, B:86:0x02be, B:87:0x02cd, B:90:0x02e2, B:91:0x02f1, B:92:0x02ff, B:94:0x0323, B:95:0x0327, B:96:0x0341, B:97:0x035e, B:99:0x0364, B:101:0x039b, B:102:0x03a9, B:103:0x03fe, B:109:0x0429, B:111:0x042f, B:112:0x045b, B:117:0x04c3, B:121:0x04cd, B:123:0x04ee, B:124:0x0514, B:131:0x05aa, B:133:0x05b7, B:134:0x05bd, B:136:0x05c5, B:137:0x05d6, B:139:0x05de, B:140:0x05e6, B:151:0x0669, B:8:0x0039, B:10:0x003f, B:12:0x004d), top: B:204:0x0006, inners: #6 }] */
    /* renamed from: markMessagesAsDeletedInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<java.lang.Long> m994x707d8e9d(long r44, java.util.ArrayList<java.lang.Integer> r46, boolean r47, boolean r48) {
        /*
            Method dump skipped, instructions count: 1962
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m994x707d8e9d(long, java.util.ArrayList, boolean, boolean):java.util.ArrayList");
    }

    /* renamed from: lambda$markMessagesAsDeletedInternal$170$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m998x7a09ae2(ArrayList namesToDelete) {
        getFileLoader().cancelLoadFiles(namesToDelete);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: updateDialogsWithDeletedMessagesInternal */
    public void m1064xf68cfb5(long originalDialogId, long channelId, ArrayList<Integer> messages, ArrayList<Long> additionalDialogsToUpdate) {
        String ids;
        TLRPC.Dialog dialog;
        ArrayList<Integer> encryptedToLoad;
        SQLitePreparedStatement state;
        long j = channelId;
        try {
            ArrayList<Long> dialogsToUpdate = new ArrayList<>();
            if (!messages.isEmpty()) {
                if (j != 0) {
                    dialogsToUpdate.add(Long.valueOf(-j));
                    state = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages_v2 WHERE uid = ? AND date = (SELECT MAX(date) FROM messages_v2 WHERE uid = ?)) WHERE did = ?");
                } else {
                    if (originalDialogId == 0) {
                        SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s) AND flags = 0", TextUtils.join(",", messages)), new Object[0]);
                        while (cursor.next()) {
                            dialogsToUpdate.add(Long.valueOf(cursor.longValue(0)));
                        }
                        cursor.dispose();
                    } else {
                        dialogsToUpdate.add(Long.valueOf(originalDialogId));
                    }
                    state = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages_v2 WHERE uid = ? AND date = (SELECT MAX(date) FROM messages_v2 WHERE uid = ? AND date != 0)) WHERE did = ?");
                }
                this.database.beginTransaction();
                for (int a = 0; a < dialogsToUpdate.size(); a++) {
                    long did = dialogsToUpdate.get(a).longValue();
                    state.requery();
                    state.bindLong(1, did);
                    state.bindLong(2, did);
                    state.bindLong(3, did);
                    state.step();
                }
                state.dispose();
                this.database.commitTransaction();
            } else {
                dialogsToUpdate.add(Long.valueOf(-j));
            }
            if (additionalDialogsToUpdate != null) {
                for (int a2 = 0; a2 < additionalDialogsToUpdate.size(); a2++) {
                    Long did2 = additionalDialogsToUpdate.get(a2);
                    if (!dialogsToUpdate.contains(did2)) {
                        dialogsToUpdate.add(did2);
                    }
                }
            }
            String ids2 = TextUtils.join(",", dialogsToUpdate);
            TLRPC.messages_Dialogs dialogs = new TLRPC.TL_messages_dialogs();
            ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            ArrayList<Integer> encryptedToLoad2 = new ArrayList<>();
            SQLiteCursor cursor2 = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max, d.pinned, d.unread_count_i, d.flags, d.folder_id, d.data, d.unread_reactions FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.did IN(%s)", ids2), new Object[0]);
            while (cursor2.next()) {
                long dialogId = cursor2.longValue(0);
                if (DialogObject.isFolderDialogId(dialogId)) {
                    TLRPC.TL_dialogFolder dialogFolder = new TLRPC.TL_dialogFolder();
                    if (cursor2.isNull(16)) {
                        ids = ids2;
                    } else {
                        NativeByteBuffer data = cursor2.byteBufferValue(16);
                        if (data != null) {
                            dialogFolder.folder = TLRPC.TL_folder.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                            ids = ids2;
                        } else {
                            dialogFolder.folder = new TLRPC.TL_folder();
                            ids = ids2;
                            dialogFolder.folder.id = cursor2.intValue(15);
                        }
                    }
                    dialog = dialogFolder;
                } else {
                    ids = ids2;
                    dialog = new TLRPC.TL_dialog();
                }
                dialog.id = dialogId;
                dialog.top_message = cursor2.intValue(1);
                dialog.read_inbox_max_id = cursor2.intValue(10);
                dialog.read_outbox_max_id = cursor2.intValue(11);
                dialog.unread_count = cursor2.intValue(2);
                dialog.unread_mentions_count = cursor2.intValue(13);
                dialog.last_message_date = cursor2.intValue(3);
                dialog.pts = cursor2.intValue(9);
                dialog.flags = j == 0 ? 0 : 1;
                dialog.pinnedNum = cursor2.intValue(12);
                dialog.pinned = dialog.pinnedNum != 0;
                int dialog_flags = cursor2.intValue(14);
                dialog.unread_mark = (dialog_flags & 1) != 0;
                dialog.folder_id = cursor2.intValue(15);
                dialog.unread_reactions_count = cursor2.intValue(17);
                dialogs.dialogs.add(dialog);
                NativeByteBuffer data2 = cursor2.byteBufferValue(4);
                if (data2 != null) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data2, data2.readInt32(false), false);
                    message.readAttachPath(data2, getUserConfig().clientUserId);
                    data2.reuse();
                    MessageObject.setUnreadFlags(message, cursor2.intValue(5));
                    message.id = cursor2.intValue(6);
                    message.send_state = cursor2.intValue(7);
                    int date = cursor2.intValue(8);
                    if (date != 0) {
                        dialog.last_message_date = date;
                    }
                    message.dialog_id = dialog.id;
                    dialogs.messages.add(message);
                    addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                }
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    int encryptedChatId = DialogObject.getEncryptedChatId(dialogId);
                    encryptedToLoad = encryptedToLoad2;
                    if (!encryptedToLoad.contains(Integer.valueOf(encryptedChatId))) {
                        encryptedToLoad.add(Integer.valueOf(encryptedChatId));
                    }
                } else {
                    encryptedToLoad = encryptedToLoad2;
                    if (DialogObject.isUserDialog(dialogId)) {
                        if (!usersToLoad.contains(Long.valueOf(dialogId))) {
                            usersToLoad.add(Long.valueOf(dialogId));
                        }
                    } else if (!chatsToLoad.contains(Long.valueOf(-dialogId))) {
                        chatsToLoad.add(Long.valueOf(-dialogId));
                    }
                }
                j = channelId;
                encryptedToLoad2 = encryptedToLoad;
                ids2 = ids;
            }
            ArrayList<Integer> encryptedToLoad3 = encryptedToLoad2;
            cursor2.dispose();
            if (!encryptedToLoad3.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad3), encryptedChats, usersToLoad);
            }
            if (!chatsToLoad.isEmpty()) {
                getChatsInternal(TextUtils.join(",", chatsToLoad), dialogs.chats);
            }
            if (!usersToLoad.isEmpty()) {
                getUsersInternal(TextUtils.join(",", usersToLoad), dialogs.users);
            }
            if (!dialogs.dialogs.isEmpty() || !encryptedChats.isEmpty()) {
                getMessagesController().processDialogsUpdate(dialogs, encryptedChats, true);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateDialogsWithDeletedMessages(final long dialogId, final long channelId, final ArrayList<Integer> messages, final ArrayList<Long> additionalDialogsToUpdate, boolean useQueue) {
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda188
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m1064xf68cfb5(dialogId, channelId, messages, additionalDialogsToUpdate);
                }
            });
        } else {
            m1064xf68cfb5(dialogId, channelId, messages, additionalDialogsToUpdate);
        }
    }

    public ArrayList<Long> markMessagesAsDeleted(final long dialogId, final ArrayList<Integer> messages, boolean useQueue, final boolean deleteFiles, final boolean scheduled) {
        if (messages.isEmpty()) {
            return null;
        }
        if (!useQueue) {
            return m994x707d8e9d(dialogId, messages, deleteFiles, scheduled);
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m994x707d8e9d(dialogId, messages, deleteFiles, scheduled);
            }
        });
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x0137 A[Catch: Exception -> 0x02f4, TryCatch #5 {Exception -> 0x02f4, blocks: (B:48:0x011b, B:49:0x0131, B:51:0x0137, B:53:0x0167, B:54:0x0175, B:55:0x01c5, B:57:0x0228, B:58:0x022e, B:60:0x0233, B:62:0x0256, B:63:0x027d, B:65:0x0282), top: B:84:0x011b }] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0228 A[Catch: Exception -> 0x02f4, TryCatch #5 {Exception -> 0x02f4, blocks: (B:48:0x011b, B:49:0x0131, B:51:0x0137, B:53:0x0167, B:54:0x0175, B:55:0x01c5, B:57:0x0228, B:58:0x022e, B:60:0x0233, B:62:0x0256, B:63:0x027d, B:65:0x0282), top: B:84:0x011b }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0233 A[Catch: Exception -> 0x02f4, TryCatch #5 {Exception -> 0x02f4, blocks: (B:48:0x011b, B:49:0x0131, B:51:0x0137, B:53:0x0167, B:54:0x0175, B:55:0x01c5, B:57:0x0228, B:58:0x022e, B:60:0x0233, B:62:0x0256, B:63:0x027d, B:65:0x0282), top: B:84:0x011b }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0281  */
    /* JADX WARN: Type inference failed for: r5v0 */
    /* JADX WARN: Type inference failed for: r5v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r5v18 */
    /* JADX WARN: Type inference failed for: r5v19 */
    /* renamed from: markMessagesAsDeletedInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<java.lang.Long> m995x1eb9705b(long r25, int r27, boolean r28) {
        /*
            Method dump skipped, instructions count: 769
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m995x1eb9705b(long, int, boolean):java.util.ArrayList");
    }

    /* renamed from: lambda$markMessagesAsDeletedInternal$173$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m999xcfa6d7f(ArrayList namesToDelete) {
        getFileLoader().cancelLoadFiles(namesToDelete);
    }

    public ArrayList<Long> markMessagesAsDeleted(final long channelId, final int mid, boolean useQueue, final boolean deleteFiles) {
        if (useQueue) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda180
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m995x1eb9705b(channelId, mid, deleteFiles);
                }
            });
            return null;
        }
        return m995x1eb9705b(channelId, mid, deleteFiles);
    }

    private void fixUnsupportedMedia(TLRPC.Message message) {
        if (message == null) {
            return;
        }
        if (message.media instanceof TLRPC.TL_messageMediaUnsupported_old) {
            if (message.media.bytes.length == 0) {
                message.media.bytes = Utilities.intToBytes(TLRPC.LAYER);
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaUnsupported) {
            message.media = new TLRPC.TL_messageMediaUnsupported_old();
            message.media.bytes = Utilities.intToBytes(TLRPC.LAYER);
            message.flags |= 512;
        }
    }

    private void doneHolesInTable(String table, long did, int max_id) throws Exception {
        if (max_id == 0) {
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM " + table + " WHERE uid = %d", Long.valueOf(did))).stepThis().dispose();
        } else {
            SQLiteDatabase sQLiteDatabase2 = this.database;
            Locale locale2 = Locale.US;
            sQLiteDatabase2.executeFast(String.format(locale2, "DELETE FROM " + table + " WHERE uid = %d AND start = 0", Long.valueOf(did))).stepThis().dispose();
        }
        SQLiteDatabase sQLiteDatabase3 = this.database;
        SQLitePreparedStatement state = sQLiteDatabase3.executeFast("REPLACE INTO " + table + " VALUES(?, ?, ?)");
        state.requery();
        state.bindLong(1, did);
        state.bindInteger(2, 1);
        state.bindInteger(3, 1);
        state.step();
        state.dispose();
    }

    public void doneHolesInMedia(long did, int max_id, int type) throws Exception {
        if (type == -1) {
            if (max_id == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", Long.valueOf(did))).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", Long.valueOf(did))).stepThis().dispose();
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
            for (int a = 0; a < 8; a++) {
                state.requery();
                state.bindLong(1, did);
                state.bindInteger(2, a);
                state.bindInteger(3, 1);
                state.bindInteger(4, 1);
                state.step();
            }
            state.dispose();
            return;
        }
        if (max_id == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", Long.valueOf(did), Integer.valueOf(type))).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", Long.valueOf(did), Integer.valueOf(type))).stepThis().dispose();
        }
        SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        state2.requery();
        state2.bindLong(1, did);
        state2.bindInteger(2, type);
        state2.bindInteger(3, 1);
        state2.bindInteger(4, 1);
        state2.step();
        state2.dispose();
    }

    /* loaded from: classes4.dex */
    public static class Hole {
        public int end;
        public int start;
        public int type;

        public Hole(int s, int e) {
            this.start = s;
            this.end = e;
        }

        public Hole(int t, int s, int e) {
            this.type = t;
            this.start = s;
            this.end = e;
        }
    }

    public void closeHolesInMedia(long did, int minId, int maxId, int type) {
        SQLiteCursor cursor;
        int i = 4;
        int i2 = 1;
        try {
            if (type < 0) {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", Long.valueOf(did), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId)), new Object[0]);
            } else {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", Long.valueOf(did), Integer.valueOf(type), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId)), new Object[0]);
            }
            ArrayList<Hole> holes = null;
            while (cursor.next()) {
                if (holes == null) {
                    holes = new ArrayList<>();
                }
                int holeType = cursor.intValue(0);
                int start = cursor.intValue(1);
                int end = cursor.intValue(2);
                if (start != end || start != 1) {
                    holes.add(new Hole(holeType, start, end));
                }
            }
            cursor.dispose();
            if (holes != null) {
                int a = 0;
                while (a < holes.size()) {
                    Hole hole = holes.get(a);
                    if (maxId >= hole.end - i2 && minId <= hole.start + i2) {
                        SQLiteDatabase sQLiteDatabase = this.database;
                        Locale locale = Locale.US;
                        Object[] objArr = new Object[i];
                        objArr[0] = Long.valueOf(did);
                        objArr[1] = Integer.valueOf(hole.type);
                        objArr[2] = Integer.valueOf(hole.start);
                        objArr[3] = Integer.valueOf(hole.end);
                        sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", objArr)).stepThis().dispose();
                        i = 4;
                    } else if (maxId >= hole.end - 1) {
                        if (hole.end == minId) {
                            i = 4;
                        } else {
                            try {
                                this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", Integer.valueOf(minId), Long.valueOf(did), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                            } catch (Exception e) {
                                FileLog.e((Throwable) e, false);
                            }
                            i = 4;
                        }
                    } else if (minId <= hole.start + 1) {
                        if (hole.start == maxId) {
                            i = 4;
                        } else {
                            try {
                                this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", Integer.valueOf(maxId), Long.valueOf(did), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                            } catch (Exception e2) {
                                FileLog.e((Throwable) e2, false);
                            }
                            i = 4;
                        }
                    } else {
                        this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", Long.valueOf(did), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                        state.requery();
                        state.bindLong(1, did);
                        state.bindInteger(2, hole.type);
                        state.bindInteger(3, hole.start);
                        state.bindInteger(4, minId);
                        state.step();
                        state.requery();
                        state.bindLong(1, did);
                        state.bindInteger(2, hole.type);
                        state.bindInteger(3, maxId);
                        i = 4;
                        state.bindInteger(4, hole.end);
                        state.step();
                        state.dispose();
                    }
                    a++;
                    i2 = 1;
                }
            }
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    private void closeHolesInTable(String table, long did, int minId, int maxId) {
        try {
            int i = 1;
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM " + table + " WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", Long.valueOf(did), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId)), new Object[0]);
            ArrayList<Hole> holes = null;
            while (cursor.next()) {
                if (holes == null) {
                    holes = new ArrayList<>();
                }
                int start = cursor.intValue(0);
                int end = cursor.intValue(1);
                if (start != end || start != 1) {
                    holes.add(new Hole(start, end));
                }
            }
            cursor.dispose();
            if (holes != null) {
                int a = 0;
                while (a < holes.size()) {
                    Hole hole = holes.get(a);
                    if (maxId >= hole.end - i && minId <= hole.start + i) {
                        this.database.executeFast(String.format(Locale.US, "DELETE FROM " + table + " WHERE uid = %d AND start = %d AND end = %d", Long.valueOf(did), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                    } else if (maxId >= hole.end - 1) {
                        if (hole.end != minId) {
                            try {
                                this.database.executeFast(String.format(Locale.US, "UPDATE " + table + " SET end = %d WHERE uid = %d AND start = %d AND end = %d", Integer.valueOf(minId), Long.valueOf(did), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                            } catch (Exception e) {
                                FileLog.e((Throwable) e, false);
                            }
                        }
                    } else if (minId <= hole.start + 1) {
                        if (hole.start != maxId) {
                            try {
                                this.database.executeFast(String.format(Locale.US, "UPDATE " + table + " SET start = %d WHERE uid = %d AND start = %d AND end = %d", Integer.valueOf(maxId), Long.valueOf(did), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                            } catch (Exception e2) {
                                FileLog.e((Throwable) e2, false);
                            }
                        }
                    } else {
                        this.database.executeFast(String.format(Locale.US, "DELETE FROM " + table + " WHERE uid = %d AND start = %d AND end = %d", Long.valueOf(did), Integer.valueOf(hole.start), Integer.valueOf(hole.end))).stepThis().dispose();
                        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO " + table + " VALUES(?, ?, ?)");
                        state.requery();
                        state.bindLong(1, did);
                        state.bindInteger(2, hole.start);
                        state.bindInteger(3, minId);
                        state.step();
                        state.requery();
                        state.bindLong(1, did);
                        state.bindInteger(2, maxId);
                        state.bindInteger(3, hole.end);
                        state.step();
                        state.dispose();
                    }
                    a++;
                    i = 1;
                }
            }
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public void replaceMessageIfExists(final TLRPC.Message message, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final boolean broadcast) {
        if (message == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda81
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1033x42579921(message, broadcast, users, chats);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00ce A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00d4 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00e0 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00e4 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00f8  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00fa  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x010c A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0123 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x012e A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0141 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0151 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0155 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0161 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0185 A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x018a A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x019d A[Catch: Exception -> 0x020f, TryCatch #3 {Exception -> 0x020f, blocks: (B:6:0x0037, B:11:0x0047, B:15:0x0054, B:16:0x0057, B:18:0x0079, B:19:0x007c, B:21:0x00b4, B:26:0x00bc, B:28:0x00ce, B:29:0x00d4, B:30:0x00db, B:32:0x00e0, B:33:0x00e4, B:35:0x00e8, B:37:0x00ed, B:41:0x00fb, B:43:0x010c, B:44:0x0123, B:45:0x0128, B:47:0x012e, B:49:0x0134, B:50:0x0139, B:51:0x013d, B:52:0x0141, B:53:0x0144, B:55:0x0151, B:56:0x0155, B:57:0x0158, B:59:0x0161, B:61:0x0185, B:63:0x018a, B:64:0x018d, B:66:0x019d, B:67:0x01aa, B:69:0x01b0, B:71:0x01c8, B:73:0x01ce, B:74:0x01e3, B:3:0x000a, B:8:0x003b, B:13:0x004f), top: B:80:0x000a }] */
    /* renamed from: lambda$replaceMessageIfExists$176$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m1033x42579921(org.telegram.tgnet.TLRPC.Message r22, boolean r23, java.util.ArrayList r24, java.util.ArrayList r25) {
        /*
            Method dump skipped, instructions count: 540
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m1033x42579921(org.telegram.tgnet.TLRPC$Message, boolean, java.util.ArrayList, java.util.ArrayList):void");
    }

    /* renamed from: lambda$replaceMessageIfExists$175$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1032xeb39a842(MessageObject messageObject, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
    }

    public void putMessages(final TLRPC.messages_Messages messages, final long dialogId, final int load_type, final int max_id, final boolean createDialog, final boolean scheduled) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda97
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1019lambda$putMessages$178$orgtelegrammessengerMessagesStorage(scheduled, dialogId, messages, load_type, max_id, createDialog);
            }
        });
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:194:0x064e
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:92)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:44)
        */
    /* renamed from: lambda$putMessages$178$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1019lambda$putMessages$178$orgtelegrammessengerMessagesStorage(boolean r46, long r47, org.telegram.tgnet.TLRPC.messages_Messages r49, int r50, int r51, boolean r52) {
        /*
            Method dump skipped, instructions count: 2075
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m1019lambda$putMessages$178$orgtelegrammessengerMessagesStorage(boolean, long, org.telegram.tgnet.TLRPC$messages_Messages, int, int, boolean):void");
    }

    /* renamed from: lambda$putMessages$177$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1018lambda$putMessages$177$orgtelegrammessengerMessagesStorage(ArrayList namesToDelete) {
        getFileLoader().cancelLoadFiles(namesToDelete);
    }

    public static void addUsersAndChatsFromMessage(TLRPC.Message message, ArrayList<Long> usersToLoad, ArrayList<Long> chatsToLoad) {
        String peerIdStr;
        long fromId = MessageObject.getFromChatId(message);
        if (DialogObject.isUserDialog(fromId)) {
            if (!usersToLoad.contains(Long.valueOf(fromId))) {
                usersToLoad.add(Long.valueOf(fromId));
            }
        } else if (DialogObject.isChatDialog(fromId) && !chatsToLoad.contains(Long.valueOf(-fromId))) {
            chatsToLoad.add(Long.valueOf(-fromId));
        }
        if (message.via_bot_id != 0 && !usersToLoad.contains(Long.valueOf(message.via_bot_id))) {
            usersToLoad.add(Long.valueOf(message.via_bot_id));
        }
        if (message.action != null) {
            if (message.action.user_id != 0 && !usersToLoad.contains(Long.valueOf(message.action.user_id))) {
                usersToLoad.add(Long.valueOf(message.action.user_id));
            }
            if (message.action.channel_id != 0 && !chatsToLoad.contains(Long.valueOf(message.action.channel_id))) {
                chatsToLoad.add(Long.valueOf(message.action.channel_id));
            }
            if (message.action.chat_id != 0 && !chatsToLoad.contains(Long.valueOf(message.action.chat_id))) {
                chatsToLoad.add(Long.valueOf(message.action.chat_id));
            }
            if (message.action instanceof TLRPC.TL_messageActionGeoProximityReached) {
                TLRPC.TL_messageActionGeoProximityReached action = (TLRPC.TL_messageActionGeoProximityReached) message.action;
                long id = MessageObject.getPeerId(action.from_id);
                if (DialogObject.isUserDialog(id)) {
                    if (!usersToLoad.contains(Long.valueOf(id))) {
                        usersToLoad.add(Long.valueOf(id));
                    }
                } else if (!chatsToLoad.contains(Long.valueOf(-id))) {
                    chatsToLoad.add(Long.valueOf(-id));
                }
                long id2 = MessageObject.getPeerId(action.to_id);
                if (id2 > 0) {
                    if (!usersToLoad.contains(Long.valueOf(id2))) {
                        usersToLoad.add(Long.valueOf(id2));
                    }
                } else if (!chatsToLoad.contains(Long.valueOf(-id2))) {
                    chatsToLoad.add(Long.valueOf(-id2));
                }
            }
            if (!message.action.users.isEmpty()) {
                for (int a = 0; a < message.action.users.size(); a++) {
                    Long uid = message.action.users.get(a);
                    if (!usersToLoad.contains(uid)) {
                        usersToLoad.add(uid);
                    }
                }
            }
        }
        if (!message.entities.isEmpty()) {
            for (int a2 = 0; a2 < message.entities.size(); a2++) {
                TLRPC.MessageEntity entity = message.entities.get(a2);
                if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                    usersToLoad.add(Long.valueOf(((TLRPC.TL_messageEntityMentionName) entity).user_id));
                } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    usersToLoad.add(Long.valueOf(((TLRPC.TL_inputMessageEntityMentionName) entity).user_id.user_id));
                }
            }
        }
        if (message.media != null) {
            if (message.media.user_id != 0 && !usersToLoad.contains(Long.valueOf(message.media.user_id))) {
                usersToLoad.add(Long.valueOf(message.media.user_id));
            }
            if (message.media instanceof TLRPC.TL_messageMediaPoll) {
                TLRPC.TL_messageMediaPoll messageMediaPoll = (TLRPC.TL_messageMediaPoll) message.media;
                if (!messageMediaPoll.results.recent_voters.isEmpty()) {
                    usersToLoad.addAll(messageMediaPoll.results.recent_voters);
                }
            }
        }
        if (message.replies != null) {
            int N = message.replies.recent_repliers.size();
            for (int a3 = 0; a3 < N; a3++) {
                long id3 = MessageObject.getPeerId(message.replies.recent_repliers.get(a3));
                if (DialogObject.isUserDialog(id3)) {
                    if (!usersToLoad.contains(Long.valueOf(id3))) {
                        usersToLoad.add(Long.valueOf(id3));
                    }
                } else if (DialogObject.isChatDialog(id3) && !chatsToLoad.contains(Long.valueOf(-id3))) {
                    chatsToLoad.add(Long.valueOf(-id3));
                }
            }
        }
        if (message.reply_to != null && message.reply_to.reply_to_peer_id != null) {
            long id4 = MessageObject.getPeerId(message.reply_to.reply_to_peer_id);
            if (DialogObject.isUserDialog(id4)) {
                if (!usersToLoad.contains(Long.valueOf(id4))) {
                    usersToLoad.add(Long.valueOf(id4));
                }
            } else if (DialogObject.isChatDialog(id4) && !chatsToLoad.contains(Long.valueOf(-id4))) {
                chatsToLoad.add(Long.valueOf(-id4));
            }
        }
        if (message.fwd_from != null) {
            if (message.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                if (!usersToLoad.contains(Long.valueOf(message.fwd_from.from_id.user_id))) {
                    usersToLoad.add(Long.valueOf(message.fwd_from.from_id.user_id));
                }
            } else if (message.fwd_from.from_id instanceof TLRPC.TL_peerChannel) {
                if (!chatsToLoad.contains(Long.valueOf(message.fwd_from.from_id.channel_id))) {
                    chatsToLoad.add(Long.valueOf(message.fwd_from.from_id.channel_id));
                }
            } else if ((message.fwd_from.from_id instanceof TLRPC.TL_peerChat) && !chatsToLoad.contains(Long.valueOf(message.fwd_from.from_id.chat_id))) {
                chatsToLoad.add(Long.valueOf(message.fwd_from.from_id.chat_id));
            }
            if (message.fwd_from.saved_from_peer != null) {
                if (message.fwd_from.saved_from_peer.user_id != 0) {
                    if (!chatsToLoad.contains(Long.valueOf(message.fwd_from.saved_from_peer.user_id))) {
                        usersToLoad.add(Long.valueOf(message.fwd_from.saved_from_peer.user_id));
                    }
                } else if (message.fwd_from.saved_from_peer.channel_id != 0) {
                    if (!chatsToLoad.contains(Long.valueOf(message.fwd_from.saved_from_peer.channel_id))) {
                        chatsToLoad.add(Long.valueOf(message.fwd_from.saved_from_peer.channel_id));
                    }
                } else if (message.fwd_from.saved_from_peer.chat_id != 0 && !chatsToLoad.contains(Long.valueOf(message.fwd_from.saved_from_peer.chat_id))) {
                    chatsToLoad.add(Long.valueOf(message.fwd_from.saved_from_peer.chat_id));
                }
            }
        }
        if (message.params != null && (peerIdStr = message.params.get("fwd_peer")) != null) {
            long peerId = Utilities.parseLong(peerIdStr).longValue();
            if (peerId < 0 && !chatsToLoad.contains(Long.valueOf(-peerId))) {
                chatsToLoad.add(Long.valueOf(-peerId));
            }
        }
    }

    public void getDialogs(final int folderId, final int offset, final int count, boolean loadDraftsPeersAndFolders) {
        long[] draftsDialogIds;
        if (loadDraftsPeersAndFolders) {
            LongSparseArray<SparseArray<TLRPC.DraftMessage>> drafts = getMediaDataController().getDrafts();
            int draftsCount = drafts.size();
            if (draftsCount > 0) {
                draftsDialogIds = new long[draftsCount];
                for (int i = 0; i < draftsCount; i++) {
                    SparseArray<TLRPC.DraftMessage> threads = drafts.valueAt(i);
                    if (threads.get(0) != null) {
                        draftsDialogIds[i] = drafts.keyAt(i);
                    }
                }
            } else {
                draftsDialogIds = null;
            }
        } else {
            draftsDialogIds = null;
        }
        final long[] jArr = draftsDialogIds;
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda131
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m945lambda$getDialogs$180$orgtelegrammessengerMessagesStorage(folderId, offset, count, jArr);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:119:0x02d1  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x02e0 A[Catch: Exception -> 0x044d, TryCatch #2 {Exception -> 0x044d, blocks: (B:116:0x02c2, B:118:0x02c6, B:120:0x02da, B:122:0x02e0, B:124:0x02ef, B:126:0x02f7, B:128:0x02fe, B:130:0x0308, B:131:0x0310, B:133:0x0316, B:135:0x0321, B:141:0x0348, B:151:0x0390, B:152:0x0396, B:154:0x0399, B:159:0x03aa, B:161:0x03b4, B:162:0x03bc, B:164:0x03c7, B:165:0x03cf, B:167:0x03dd, B:168:0x03e6, B:169:0x03eb, B:171:0x03f3), top: B:220:0x02c2 }] */
    /* JADX WARN: Removed duplicated region for block: B:126:0x02f7 A[Catch: Exception -> 0x044d, TryCatch #2 {Exception -> 0x044d, blocks: (B:116:0x02c2, B:118:0x02c6, B:120:0x02da, B:122:0x02e0, B:124:0x02ef, B:126:0x02f7, B:128:0x02fe, B:130:0x0308, B:131:0x0310, B:133:0x0316, B:135:0x0321, B:141:0x0348, B:151:0x0390, B:152:0x0396, B:154:0x0399, B:159:0x03aa, B:161:0x03b4, B:162:0x03bc, B:164:0x03c7, B:165:0x03cf, B:167:0x03dd, B:168:0x03e6, B:169:0x03eb, B:171:0x03f3), top: B:220:0x02c2 }] */
    /* JADX WARN: Removed duplicated region for block: B:234:0x0199 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:244:0x01d5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0164  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0166  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x017c  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x017e  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x01b8  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01ed A[Catch: Exception -> 0x0337, TryCatch #6 {Exception -> 0x0337, blocks: (B:60:0x01ce, B:66:0x01e6, B:68:0x01ed, B:70:0x01fb, B:75:0x0225), top: B:228:0x01ce }] */
    /* renamed from: lambda$getDialogs$180$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m945lambda$getDialogs$180$orgtelegrammessengerMessagesStorage(int r34, int r35, int r36, long[] r37) {
        /*
            Method dump skipped, instructions count: 1272
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m945lambda$getDialogs$180$orgtelegrammessengerMessagesStorage(int, int, int, long[]):void");
    }

    /* renamed from: lambda$getDialogs$179$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m944lambda$getDialogs$179$orgtelegrammessengerMessagesStorage(LongSparseArray folderIds) {
        MediaDataController mediaDataController = getMediaDataController();
        mediaDataController.clearDraftsFolderIds();
        if (folderIds != null) {
            int size = folderIds.size();
            for (int i = 0; i < size; i++) {
                mediaDataController.setDraftFolderId(folderIds.keyAt(i), ((Integer) folderIds.valueAt(i)).intValue());
            }
        }
    }

    public static void createFirstHoles(long did, SQLitePreparedStatement state5, SQLitePreparedStatement state6, int messageId) throws Exception {
        state5.requery();
        state5.bindLong(1, did);
        state5.bindInteger(2, messageId == 1 ? 1 : 0);
        state5.bindInteger(3, messageId);
        state5.step();
        for (int b = 0; b < 8; b++) {
            state6.requery();
            state6.bindLong(1, did);
            state6.bindInteger(2, b);
            state6.bindInteger(3, messageId == 1 ? 1 : 0);
            state6.bindInteger(4, messageId);
            state6.step();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x0139, code lost:
        if (r6 < 0) goto L35;
     */
    /* JADX WARN: Removed duplicated region for block: B:113:0x0355  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x03ae A[Catch: Exception -> 0x0457, TryCatch #5 {Exception -> 0x0457, blocks: (B:9:0x0039, B:10:0x0076, B:12:0x0080, B:38:0x014b, B:114:0x0360, B:116:0x03ae, B:117:0x03b0, B:119:0x03c0, B:120:0x03d9, B:121:0x03df, B:123:0x03eb, B:124:0x03ee, B:126:0x03f2, B:130:0x0407, B:133:0x0417, B:134:0x0427, B:136:0x0444, B:138:0x0453), top: B:164:0x0039 }] */
    /* JADX WARN: Removed duplicated region for block: B:119:0x03c0 A[Catch: Exception -> 0x0457, TryCatch #5 {Exception -> 0x0457, blocks: (B:9:0x0039, B:10:0x0076, B:12:0x0080, B:38:0x014b, B:114:0x0360, B:116:0x03ae, B:117:0x03b0, B:119:0x03c0, B:120:0x03d9, B:121:0x03df, B:123:0x03eb, B:124:0x03ee, B:126:0x03f2, B:130:0x0407, B:133:0x0417, B:134:0x0427, B:136:0x0444, B:138:0x0453), top: B:164:0x0039 }] */
    /* JADX WARN: Removed duplicated region for block: B:120:0x03d9 A[Catch: Exception -> 0x0457, TryCatch #5 {Exception -> 0x0457, blocks: (B:9:0x0039, B:10:0x0076, B:12:0x0080, B:38:0x014b, B:114:0x0360, B:116:0x03ae, B:117:0x03b0, B:119:0x03c0, B:120:0x03d9, B:121:0x03df, B:123:0x03eb, B:124:0x03ee, B:126:0x03f2, B:130:0x0407, B:133:0x0417, B:134:0x0427, B:136:0x0444, B:138:0x0453), top: B:164:0x0039 }] */
    /* JADX WARN: Removed duplicated region for block: B:123:0x03eb A[Catch: Exception -> 0x0457, TryCatch #5 {Exception -> 0x0457, blocks: (B:9:0x0039, B:10:0x0076, B:12:0x0080, B:38:0x014b, B:114:0x0360, B:116:0x03ae, B:117:0x03b0, B:119:0x03c0, B:120:0x03d9, B:121:0x03df, B:123:0x03eb, B:124:0x03ee, B:126:0x03f2, B:130:0x0407, B:133:0x0417, B:134:0x0427, B:136:0x0444, B:138:0x0453), top: B:164:0x0039 }] */
    /* JADX WARN: Removed duplicated region for block: B:126:0x03f2 A[Catch: Exception -> 0x0457, TryCatch #5 {Exception -> 0x0457, blocks: (B:9:0x0039, B:10:0x0076, B:12:0x0080, B:38:0x014b, B:114:0x0360, B:116:0x03ae, B:117:0x03b0, B:119:0x03c0, B:120:0x03d9, B:121:0x03df, B:123:0x03eb, B:124:0x03ee, B:126:0x03f2, B:130:0x0407, B:133:0x0417, B:134:0x0427, B:136:0x0444, B:138:0x0453), top: B:164:0x0039 }] */
    /* JADX WARN: Removed duplicated region for block: B:131:0x040f  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x0156 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void putDialogsInternal(org.telegram.tgnet.TLRPC.messages_Dialogs r31, int r32) {
        /*
            Method dump skipped, instructions count: 1156
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    public void getDialogFolderId(final long dialogId, final IntCallback callback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m939xe040e45(dialogId, callback);
            }
        });
    }

    /* renamed from: lambda$getDialogFolderId$182$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m939xe040e45(long dialogId, final IntCallback callback) {
        final int folderId;
        int folderId2;
        try {
            if (this.unknownDialogsIds.get(dialogId) != null) {
                folderId = -1;
            } else {
                SQLiteCursor cursor = this.database.queryFinalized("SELECT folder_id FROM dialogs WHERE did = ?", Long.valueOf(dialogId));
                if (cursor.next()) {
                    folderId2 = cursor.intValue(0);
                } else {
                    folderId2 = -1;
                }
                cursor.dispose();
                folderId = folderId2;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda111
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.IntCallback.this.run(folderId);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogsFolderId(final ArrayList<TLRPC.TL_folderPeer> peers, final ArrayList<TLRPC.TL_inputFolderPeer> inputPeers, final long dialogId, final int folderId) {
        if (peers == null && inputPeers == null && dialogId == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1049xa6d9683f(peers, inputPeers, folderId, dialogId);
            }
        });
    }

    /* renamed from: lambda$setDialogsFolderId$183$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1049xa6d9683f(ArrayList peers, ArrayList inputPeers, int folderId, long dialogId) {
        Exception e;
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET folder_id = ?, pinned = ? WHERE did = ?");
            if (peers != null) {
                int N = peers.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.TL_folderPeer folderPeer = (TLRPC.TL_folderPeer) peers.get(a);
                    long did = DialogObject.getPeerDialogId(folderPeer.peer);
                    state.requery();
                    state.bindInteger(1, folderPeer.folder_id);
                    state.bindInteger(2, 0);
                    state.bindLong(3, did);
                    state.step();
                    this.unknownDialogsIds.remove(did);
                }
            } else if (inputPeers != null) {
                int N2 = inputPeers.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    TLRPC.TL_inputFolderPeer folderPeer2 = (TLRPC.TL_inputFolderPeer) inputPeers.get(a2);
                    long did2 = DialogObject.getPeerDialogId(folderPeer2.peer);
                    state.requery();
                    state.bindInteger(1, folderPeer2.folder_id);
                    state.bindInteger(2, 0);
                    state.bindLong(3, did2);
                    state.step();
                    this.unknownDialogsIds.remove(did2);
                }
            } else {
                state.requery();
                try {
                    state.bindInteger(1, folderId);
                    state.bindInteger(2, 0);
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    return;
                }
                try {
                    state.bindLong(3, dialogId);
                    state.step();
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e);
                    return;
                }
            }
            state.dispose();
            this.database.commitTransaction();
            m901xad1233b5(1);
            resetAllUnreadCounters(false);
        } catch (Exception e4) {
            e = e4;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x003c, code lost:
        r1 = false;
     */
    /* renamed from: checkIfFolderEmptyInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m901xad1233b5(final int r8) {
        /*
            r7 = this;
            org.telegram.SQLite.SQLiteDatabase r0 = r7.database     // Catch: java.lang.Exception -> L6d
            java.lang.String r1 = "SELECT did FROM dialogs WHERE folder_id = ?"
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch: java.lang.Exception -> L6d
            java.lang.Integer r3 = java.lang.Integer.valueOf(r8)     // Catch: java.lang.Exception -> L6d
            r4 = 0
            r2[r4] = r3     // Catch: java.lang.Exception -> L6d
            org.telegram.SQLite.SQLiteCursor r0 = r0.queryFinalized(r1, r2)     // Catch: java.lang.Exception -> L6d
            r1 = 1
        L13:
            boolean r2 = r0.next()     // Catch: java.lang.Exception -> L6d
            if (r2 == 0) goto L3d
            long r2 = r0.longValue(r4)     // Catch: java.lang.Exception -> L6d
            boolean r5 = org.telegram.messenger.DialogObject.isUserDialog(r2)     // Catch: java.lang.Exception -> L6d
            if (r5 != 0) goto L3c
            boolean r5 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)     // Catch: java.lang.Exception -> L6d
            if (r5 == 0) goto L2a
            goto L3c
        L2a:
            long r5 = -r2
            org.telegram.tgnet.TLRPC$Chat r5 = r7.getChat(r5)     // Catch: java.lang.Exception -> L6d
            boolean r6 = org.telegram.messenger.ChatObject.isNotInChat(r5)     // Catch: java.lang.Exception -> L6d
            if (r6 != 0) goto L3b
            org.telegram.tgnet.TLRPC$InputChannel r6 = r5.migrated_to     // Catch: java.lang.Exception -> L6d
            if (r6 != 0) goto L3b
            r1 = 0
            goto L3d
        L3b:
            goto L13
        L3c:
            r1 = 0
        L3d:
            r0.dispose()     // Catch: java.lang.Exception -> L6d
            if (r1 == 0) goto L6c
            org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda120 r2 = new org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda120     // Catch: java.lang.Exception -> L6d
            r2.<init>()     // Catch: java.lang.Exception -> L6d
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch: java.lang.Exception -> L6d
            org.telegram.SQLite.SQLiteDatabase r2 = r7.database     // Catch: java.lang.Exception -> L6d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L6d
            r3.<init>()     // Catch: java.lang.Exception -> L6d
            java.lang.String r4 = "DELETE FROM dialogs WHERE did = "
            r3.append(r4)     // Catch: java.lang.Exception -> L6d
            long r4 = org.telegram.messenger.DialogObject.makeFolderDialogId(r8)     // Catch: java.lang.Exception -> L6d
            r3.append(r4)     // Catch: java.lang.Exception -> L6d
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Exception -> L6d
            org.telegram.SQLite.SQLitePreparedStatement r2 = r2.executeFast(r3)     // Catch: java.lang.Exception -> L6d
            org.telegram.SQLite.SQLitePreparedStatement r2 = r2.stepThis()     // Catch: java.lang.Exception -> L6d
            r2.dispose()     // Catch: java.lang.Exception -> L6d
        L6c:
            goto L71
        L6d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e(r0)
        L71:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m901xad1233b5(int):void");
    }

    /* renamed from: lambda$checkIfFolderEmptyInternal$184$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m902xae5916d9(int folderId) {
        getMessagesController().onFolderEmpty(folderId);
    }

    public void checkIfFolderEmpty(final int folderId) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda119
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m901xad1233b5(folderId);
            }
        });
    }

    public void unpinAllDialogsExceptNew(final ArrayList<Long> dids, final int folderId) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1052xceb7ac4d(dids, folderId);
            }
        });
    }

    /* renamed from: lambda$unpinAllDialogsExceptNew$186$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1052xceb7ac4d(ArrayList dids, int folderId) {
        try {
            ArrayList<Long> unpinnedDialogs = new ArrayList<>();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT did, folder_id FROM dialogs WHERE pinned > 0 AND did NOT IN (%s)", TextUtils.join(",", dids)), new Object[0]);
            while (cursor.next()) {
                long did = cursor.longValue(0);
                int fid = cursor.intValue(1);
                if (fid == folderId && !DialogObject.isEncryptedDialog(did) && !DialogObject.isFolderDialogId(did)) {
                    unpinnedDialogs.add(Long.valueOf(cursor.longValue(0)));
                }
            }
            cursor.dispose();
            if (!unpinnedDialogs.isEmpty()) {
                SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                for (int a = 0; a < unpinnedDialogs.size(); a++) {
                    long did2 = unpinnedDialogs.get(a).longValue();
                    state.requery();
                    state.bindInteger(1, 0);
                    state.bindLong(2, did2);
                    state.step();
                }
                state.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogUnread(final long did, final boolean unread) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1048x86a7ca8e(did, unread);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0034, code lost:
        if (r1 == null) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x003a, code lost:
        if (r9 == false) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x003c, code lost:
        r0 = r0 | 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x003e, code lost:
        r0 = r0 & (-2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0040, code lost:
        r4 = r6.database.executeFast("UPDATE dialogs SET flags = ? WHERE did = ?");
        r4.bindInteger(1, r0);
        r4.bindLong(2, r7);
        r4.step();
        r4.dispose();
        resetAllUnreadCounters(false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0028, code lost:
        if (r1 != null) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x002a, code lost:
        r1.dispose();
     */
    /* renamed from: lambda$setDialogUnread$187$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m1048x86a7ca8e(long r7, boolean r9) {
        /*
            r6 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            org.telegram.SQLite.SQLiteDatabase r3 = r6.database     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            r4.<init>()     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            java.lang.String r5 = "SELECT flags FROM dialogs WHERE did = "
            r4.append(r5)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            r4.append(r7)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r5)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            r1 = r3
            boolean r3 = r1.next()     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            if (r3 == 0) goto L28
            int r3 = r1.intValue(r2)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            r0 = r3
        L28:
            if (r1 == 0) goto L39
        L2a:
            r1.dispose()     // Catch: java.lang.Exception -> L37
            goto L39
        L2e:
            r2 = move-exception
            goto L59
        L30:
            r3 = move-exception
            org.telegram.messenger.FileLog.e(r3)     // Catch: java.lang.Throwable -> L2e
            if (r1 == 0) goto L39
            goto L2a
        L37:
            r0 = move-exception
            goto L60
        L39:
            r3 = 1
            if (r9 == 0) goto L3e
            r0 = r0 | r3
            goto L40
        L3e:
            r0 = r0 & (-2)
        L40:
            org.telegram.SQLite.SQLiteDatabase r4 = r6.database     // Catch: java.lang.Exception -> L37
            java.lang.String r5 = "UPDATE dialogs SET flags = ? WHERE did = ?"
            org.telegram.SQLite.SQLitePreparedStatement r4 = r4.executeFast(r5)     // Catch: java.lang.Exception -> L37
            r4.bindInteger(r3, r0)     // Catch: java.lang.Exception -> L37
            r3 = 2
            r4.bindLong(r3, r7)     // Catch: java.lang.Exception -> L37
            r4.step()     // Catch: java.lang.Exception -> L37
            r4.dispose()     // Catch: java.lang.Exception -> L37
            r6.resetAllUnreadCounters(r2)     // Catch: java.lang.Exception -> L37
            goto L63
        L59:
            if (r1 == 0) goto L5e
            r1.dispose()     // Catch: java.lang.Exception -> L37
        L5e:
            throw r2     // Catch: java.lang.Exception -> L37
        L60:
            org.telegram.messenger.FileLog.e(r0)
        L63:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m1048x86a7ca8e(long, boolean):void");
    }

    private void resetAllUnreadCounters(boolean muted) {
        int N = this.dialogFilters.size();
        for (int a = 0; a < N; a++) {
            MessagesController.DialogFilter filter = this.dialogFilters.get(a);
            if (muted) {
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                    filter.pendingUnreadCount = -1;
                }
            } else {
                filter.pendingUnreadCount = -1;
            }
        }
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda110
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1034x80b138d4();
            }
        });
    }

    /* renamed from: lambda$resetAllUnreadCounters$188$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1034x80b138d4() {
        ArrayList<MessagesController.DialogFilter> filters = getMessagesController().dialogFilters;
        int N = filters.size();
        for (int a = 0; a < N; a++) {
            filters.get(a).unreadCount = filters.get(a).pendingUnreadCount;
        }
        int a2 = this.pendingMainUnreadCount;
        this.mainUnreadCount = a2;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public void setDialogPinned(final long did, final int pinned) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda138
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1047x300cec83(pinned, did);
            }
        });
    }

    /* renamed from: lambda$setDialogPinned$189$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1047x300cec83(int pinned, long did) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            state.bindInteger(1, pinned);
            state.bindLong(2, did);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogsPinned(final ArrayList<Long> dids, final ArrayList<Integer> pinned) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1050x88a77e14(dids, pinned);
            }
        });
    }

    /* renamed from: lambda$setDialogsPinned$190$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1050x88a77e14(ArrayList dids, ArrayList pinned) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            int N = dids.size();
            for (int a = 0; a < N; a++) {
                state.requery();
                state.bindInteger(1, ((Integer) pinned.get(a)).intValue());
                state.bindLong(2, ((Long) dids.get(a)).longValue());
                state.step();
            }
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putDialogs(final TLRPC.messages_Dialogs dialogs, final int check) {
        if (dialogs.dialogs.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda86
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1015lambda$putDialogs$191$orgtelegrammessengerMessagesStorage(dialogs, check);
            }
        });
    }

    /* renamed from: lambda$putDialogs$191$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1015lambda$putDialogs$191$orgtelegrammessengerMessagesStorage(TLRPC.messages_Dialogs dialogs, int check) {
        putDialogsInternal(dialogs, check);
        try {
            loadUnreadMessages();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDialogMaxMessageId(final long dialog_id, final IntCallback callback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m940x10799330(dialog_id, callback);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0037, code lost:
        if (r0 == null) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x003a, code lost:
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda146());
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0042, code lost:
        return;
     */
    /* renamed from: lambda$getDialogMaxMessageId$193$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m940x10799330(long r7, final org.telegram.messenger.MessagesStorage.IntCallback r9) {
        /*
            r6 = this;
            r0 = 0
            r1 = 1
            int[] r1 = new int[r1]
            org.telegram.SQLite.SQLiteDatabase r2 = r6.database     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            r3.<init>()     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.lang.String r4 = "SELECT MAX(mid) FROM messages_v2 WHERE uid = "
            r3.append(r4)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            r3.append(r7)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            r4 = 0
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r5)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            r0 = r2
            boolean r2 = r0.next()     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            if (r2 == 0) goto L2b
            int r2 = r0.intValue(r4)     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
            r1[r4] = r2     // Catch: java.lang.Throwable -> L31 java.lang.Exception -> L33
        L2b:
            if (r0 == 0) goto L3a
        L2d:
            r0.dispose()
            goto L3a
        L31:
            r2 = move-exception
            goto L43
        L33:
            r2 = move-exception
            org.telegram.messenger.FileLog.e(r2)     // Catch: java.lang.Throwable -> L31
            if (r0 == 0) goto L3a
            goto L2d
        L3a:
            org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda146 r2 = new org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda146
            r2.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            return
        L43:
            if (r0 == 0) goto L48
            r0.dispose()
        L48:
            goto L4a
        L49:
            throw r2
        L4a:
            goto L49
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m940x10799330(long, org.telegram.messenger.MessagesStorage$IntCallback):void");
    }

    public int getDialogReadMax(final boolean outbox, final long dialog_id) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] max = {0};
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda98
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m943x6e764301(outbox, dialog_id, max, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return max[0].intValue();
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0055, code lost:
        if (r0 == null) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0058, code lost:
        r10.countDown();
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x005b, code lost:
        return;
     */
    /* renamed from: lambda$getDialogReadMax$194$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m943x6e764301(boolean r6, long r7, java.lang.Integer[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
            if (r6 == 0) goto L1f
            org.telegram.SQLite.SQLiteDatabase r2 = r5.database     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            r3.<init>()     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.String r4 = "SELECT outbox_max FROM dialogs WHERE did = "
            r3.append(r4)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            r3.append(r7)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            r0 = r2
            goto L39
        L1f:
            org.telegram.SQLite.SQLiteDatabase r2 = r5.database     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            r3.<init>()     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.String r4 = "SELECT inbox_max FROM dialogs WHERE did = "
            r3.append(r4)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            r3.append(r7)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            r0 = r2
        L39:
            boolean r2 = r0.next()     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            if (r2 == 0) goto L49
            int r2 = r0.intValue(r1)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
            r9[r1] = r2     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L51
        L49:
            if (r0 == 0) goto L58
        L4b:
            r0.dispose()
            goto L58
        L4f:
            r1 = move-exception
            goto L5c
        L51:
            r1 = move-exception
            org.telegram.messenger.FileLog.e(r1)     // Catch: java.lang.Throwable -> L4f
            if (r0 == 0) goto L58
            goto L4b
        L58:
            r10.countDown()
            return
        L5c:
            if (r0 == 0) goto L61
            r0.dispose()
        L61:
            goto L63
        L62:
            throw r1
        L63:
            goto L62
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m943x6e764301(boolean, long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public int getChannelPtsSync(final long channelId) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] pts = {0};
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m936x4cc4420d(channelId, pts, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return pts[0].intValue();
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0039, code lost:
        if (r0 == null) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x003c, code lost:
        r9.countDown();
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0040, code lost:
        r1 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0041, code lost:
        org.telegram.messenger.FileLog.e(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0044, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x002d, code lost:
        if (r0 != null) goto L7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x002f, code lost:
        r0.dispose();
     */
    /* renamed from: lambda$getChannelPtsSync$195$org-telegram-messenger-MessagesStorage */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m936x4cc4420d(long r6, java.lang.Integer[] r8, java.util.concurrent.CountDownLatch r9) {
        /*
            r5 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r5.database     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            r2.<init>()     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            java.lang.String r3 = "SELECT pts FROM dialogs WHERE did = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            long r3 = -r6
            r2.append(r3)     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            r3 = 0
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            org.telegram.SQLite.SQLiteCursor r1 = r1.queryFinalized(r2, r4)     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            r0 = r1
            boolean r1 = r0.next()     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            if (r1 == 0) goto L2d
            int r1 = r0.intValue(r3)     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
            r8[r3] = r1     // Catch: java.lang.Throwable -> L33 java.lang.Exception -> L35
        L2d:
            if (r0 == 0) goto L3c
        L2f:
            r0.dispose()
            goto L3c
        L33:
            r1 = move-exception
            goto L45
        L35:
            r1 = move-exception
            org.telegram.messenger.FileLog.e(r1)     // Catch: java.lang.Throwable -> L33
            if (r0 == 0) goto L3c
            goto L2f
        L3c:
            r9.countDown()     // Catch: java.lang.Exception -> L40
            goto L44
        L40:
            r1 = move-exception
            org.telegram.messenger.FileLog.e(r1)
        L44:
            return
        L45:
            if (r0 == 0) goto L4a
            r0.dispose()
        L4a:
            goto L4c
        L4b:
            throw r1
        L4c:
            goto L4b
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.m936x4cc4420d(long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public TLRPC.User getUserSync(final long userId) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC.User[] user = new TLRPC.User[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda105
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m956lambda$getUserSync$196$orgtelegrammessengerMessagesStorage(user, userId, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return user[0];
    }

    /* renamed from: lambda$getUserSync$196$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m956lambda$getUserSync$196$orgtelegrammessengerMessagesStorage(TLRPC.User[] user, long userId, CountDownLatch countDownLatch) {
        user[0] = getUser(userId);
        countDownLatch.countDown();
    }

    public TLRPC.Chat getChatSync(final long chatId) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC.Chat[] chat = new TLRPC.Chat[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda103
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m937lambda$getChatSync$197$orgtelegrammessengerMessagesStorage(chat, chatId, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return chat[0];
    }

    /* renamed from: lambda$getChatSync$197$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m937lambda$getChatSync$197$orgtelegrammessengerMessagesStorage(TLRPC.Chat[] chat, long chatId, CountDownLatch countDownLatch) {
        chat[0] = getChat(chatId);
        countDownLatch.countDown();
    }

    public TLRPC.User getUser(long userId) {
        try {
            ArrayList<TLRPC.User> users = new ArrayList<>();
            getUsersInternal("" + userId, users);
            if (users.isEmpty()) {
                return null;
            }
            TLRPC.User user = users.get(0);
            return user;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public ArrayList<TLRPC.User> getUsers(ArrayList<Long> uids) {
        ArrayList<TLRPC.User> users = new ArrayList<>();
        try {
            getUsersInternal(TextUtils.join(",", uids), users);
        } catch (Exception e) {
            users.clear();
            FileLog.e(e);
        }
        return users;
    }

    public TLRPC.Chat getChat(long chatId) {
        try {
            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            getChatsInternal("" + chatId, chats);
            if (chats.isEmpty()) {
                return null;
            }
            TLRPC.Chat chat = chats.get(0);
            return chat;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public TLRPC.EncryptedChat getEncryptedChat(long chatId) {
        try {
            ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
            getEncryptedChatsInternal("" + chatId, encryptedChats, null);
            if (encryptedChats.isEmpty()) {
                return null;
            }
            TLRPC.EncryptedChat chat = encryptedChats.get(0);
            return chat;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:86:0x01f4, code lost:
        if (r5.startsWith(r7) != false) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0051, code lost:
        if (r13.length() == 0) goto L13;
     */
    /* JADX WARN: Removed duplicated region for block: B:136:0x0376 A[Catch: Exception -> 0x08a2, LOOP:2: B:106:0x029f->B:136:0x0376, LOOP_END, TryCatch #9 {Exception -> 0x08a2, blocks: (B:46:0x0107, B:48:0x010d, B:55:0x013c, B:57:0x014a, B:65:0x016e, B:67:0x0180, B:71:0x019e, B:73:0x01b3, B:75:0x01b9, B:77:0x01bf, B:81:0x01e7, B:85:0x01f0, B:87:0x01f6, B:89:0x0207, B:91:0x0219, B:92:0x0232, B:95:0x0240, B:96:0x0265, B:98:0x026b, B:101:0x0280, B:103:0x028a, B:105:0x0293, B:107:0x02a1, B:109:0x02af, B:112:0x02c6, B:114:0x02cc, B:118:0x02e4, B:124:0x02f2, B:126:0x02fd, B:128:0x031d, B:132:0x0332, B:133:0x033d, B:134:0x0364, B:136:0x0376, B:139:0x03a1, B:141:0x03bd, B:153:0x0407, B:156:0x041e, B:158:0x0424, B:170:0x045c), top: B:358:0x0107 }] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x069a A[Catch: Exception -> 0x089c, LOOP:6: B:196:0x04e6->B:249:0x069a, LOOP_END, TryCatch #1 {Exception -> 0x089c, blocks: (B:143:0x03c3, B:144:0x03e0, B:146:0x03e6, B:149:0x03fb, B:151:0x03ff, B:162:0x043e, B:164:0x0444, B:166:0x0452, B:168:0x0456, B:174:0x0468, B:180:0x0492, B:181:0x0495, B:193:0x04df, B:200:0x04f1, B:203:0x0508, B:205:0x050e, B:209:0x0526, B:219:0x0546, B:226:0x0560, B:234:0x0609, B:238:0x0617, B:242:0x0628, B:247:0x0686, B:249:0x069a, B:252:0x06ca, B:256:0x06eb, B:257:0x06f1, B:259:0x06f7, B:261:0x0701, B:263:0x0705, B:264:0x0708, B:265:0x070b, B:266:0x0711, B:268:0x0717), top: B:342:0x03c3 }] */
    /* JADX WARN: Removed duplicated region for block: B:322:0x0861 A[Catch: Exception -> 0x0895, LOOP:10: B:291:0x0791->B:322:0x0861, LOOP_END, TryCatch #7 {Exception -> 0x0895, blocks: (B:272:0x0728, B:277:0x073b, B:278:0x0749, B:280:0x074f, B:283:0x075b, B:286:0x076f, B:288:0x077e, B:290:0x0789, B:292:0x0793, B:294:0x07a1, B:297:0x07ba, B:299:0x07c0, B:303:0x07d8, B:310:0x07e8, B:312:0x07f3, B:314:0x0807, B:318:0x081c, B:319:0x0829, B:320:0x0851, B:322:0x0861, B:325:0x088d), top: B:354:0x0728 }] */
    /* JADX WARN: Removed duplicated region for block: B:375:0x02f2 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:393:0x0531 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:405:0x07e8 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void localSearch(int r43, java.lang.String r44, java.util.ArrayList<java.lang.Object> r45, java.util.ArrayList<java.lang.CharSequence> r46, java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r47, int r48) {
        /*
            Method dump skipped, instructions count: 2229
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.localSearch(int, java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    public static /* synthetic */ int lambda$localSearch$198(DialogsSearchAdapter.DialogSearchResult lhs, DialogsSearchAdapter.DialogSearchResult rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public ArrayList<Integer> getCachedMessagesInRange(long dialogId, int minDate, int maxDate) {
        ArrayList<Integer> messageIds = new ArrayList<>();
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages_v2 WHERE uid = %d AND date >= %d AND date <= %d", Long.valueOf(dialogId), Integer.valueOf(minDate), Integer.valueOf(maxDate)), new Object[0]);
            while (cursor.next()) {
                try {
                    int mid = cursor.intValue(0);
                    messageIds.add(Integer.valueOf(mid));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            cursor.dispose();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return messageIds;
    }

    public void updateUnreadReactionsCount(final long dialogId, final int count) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda140
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1086x3c60978f(count, dialogId);
            }
        });
    }

    /* renamed from: lambda$updateUnreadReactionsCount$199$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1086x3c60978f(int count, long dialogId) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET unread_reactions = ? WHERE did = ?");
            state.bindInteger(1, Math.max(count, 0));
            state.bindLong(2, dialogId);
            state.step();
            state.dispose();
            if (count == 0) {
                SQLitePreparedStatement state2 = this.database.executeFast("UPDATE reaction_mentions SET state = 0 WHERE dialog_id = ?");
                state2.bindLong(1, dialogId);
                state2.step();
                state2.dispose();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void markMessageReactionsAsRead(final long dialogId, final int messageId, boolean usequeue) {
        if (usequeue) {
            getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda166
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.m993x9b9bb993(dialogId, messageId);
                }
            });
        } else {
            m993x9b9bb993(dialogId, messageId);
        }
    }

    /* renamed from: markMessageReactionsAsReadInternal */
    public void m993x9b9bb993(long dialogId, int messageId) {
        NativeByteBuffer data;
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("UPDATE reaction_mentions SET state = 0 WHERE message_id = ? AND dialog_id = ?");
            state.bindInteger(1, messageId);
            state.bindLong(2, dialogId);
            state.step();
            state.dispose();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE uid = %d AND mid = %d", Long.valueOf(dialogId), Integer.valueOf(messageId)), new Object[0]);
            TLRPC.Message message = null;
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                message.readAttachPath(data, getUserConfig().clientUserId);
                data.reuse();
                if (message.reactions != null && message.reactions.recent_reactions != null) {
                    for (int i = 0; i < message.reactions.recent_reactions.size(); i++) {
                        message.reactions.recent_reactions.get(i).unread = false;
                    }
                }
            }
            cursor.dispose();
            if (message != null) {
                SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "UPDATE messages_v2 SET data = ? WHERE uid = %d AND mid = %d", Long.valueOf(dialogId), Integer.valueOf(messageId)));
                try {
                    NativeByteBuffer data2 = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data2);
                    state2.bindByteBuffer(1, data2);
                    state2.step();
                    state2.dispose();
                    data2.reuse();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (SQLiteException e2) {
            FileLog.e(e2);
        }
    }

    public void updateDialogUnreadReactions(final long dialogId, final int newUnreadCount, final boolean increment) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda95
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.m1063x2d7e821a(increment, dialogId, newUnreadCount);
            }
        });
    }

    /* renamed from: lambda$updateDialogUnreadReactions$201$org-telegram-messenger-MessagesStorage */
    public /* synthetic */ void m1063x2d7e821a(boolean increment, long dialogId, int newUnreadCount) {
        int oldUnreadRactions = 0;
        if (increment) {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                SQLiteCursor cursor = sQLiteDatabase.queryFinalized("SELECT unread_reactions FROM dialogs WHERE did = " + dialogId, new Object[0]);
                if (cursor.next()) {
                    oldUnreadRactions = Math.max(0, cursor.intValue(0));
                }
                cursor.dispose();
            } catch (SQLiteException e) {
                e.printStackTrace();
                return;
            }
        }
        SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("UPDATE dialogs SET unread_reactions = ? WHERE did = ?");
        state.bindInteger(1, oldUnreadRactions + newUnreadCount);
        state.bindLong(2, dialogId);
        state.step();
        state.dispose();
    }
}
