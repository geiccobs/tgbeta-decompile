package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.core.content.pm.ShortcutManagerCompat;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import j$.util.concurrent.ConcurrentHashMap;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationBadge;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.messenger.ringtone.RingtoneUploader;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.StickerSetBulletinLayout;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
/* loaded from: classes4.dex */
public class MediaDataController extends BaseController {
    public static final String ATTACH_MENU_BOT_ANIMATED_ICON_KEY = "android_animated";
    public static final String ATTACH_MENU_BOT_COLOR_DARK_ICON = "dark_icon";
    public static final String ATTACH_MENU_BOT_COLOR_DARK_TEXT = "dark_text";
    public static final String ATTACH_MENU_BOT_COLOR_LIGHT_ICON = "light_icon";
    public static final String ATTACH_MENU_BOT_COLOR_LIGHT_TEXT = "light_text";
    public static final String ATTACH_MENU_BOT_PLACEHOLDER_STATIC_KEY = "placeholder_static";
    public static final String ATTACH_MENU_BOT_STATIC_ICON_KEY = "default_static";
    public static final int MEDIA_AUDIO = 2;
    public static final int MEDIA_FILE = 1;
    public static final int MEDIA_GIF = 5;
    public static final int MEDIA_MUSIC = 4;
    public static final int MEDIA_PHOTOS_ONLY = 6;
    public static final int MEDIA_PHOTOVIDEO = 0;
    public static final int MEDIA_TYPES_COUNT = 8;
    public static final int MEDIA_URL = 3;
    public static final int MEDIA_VIDEOS_ONLY = 7;
    public static final int TYPE_EMOJI = 4;
    public static final int TYPE_FAVE = 2;
    public static final int TYPE_FEATURED = 3;
    public static final int TYPE_GREETINGS = 3;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static RectF bitmapRect;
    private static Comparator<TLRPC.MessageEntity> entityComparator;
    private static Paint erasePaint;
    private static Paint roundPaint;
    private static Path roundPath;
    private String doubleTapReaction;
    private SharedPreferences draftPreferences;
    private boolean featuredStickersLoaded;
    private TLRPC.Document greetingsSticker;
    private boolean inTransaction;
    private boolean isLoadingMenuBots;
    private boolean isLoadingPremiumPromo;
    private boolean isLoadingReactions;
    private long lastDialogId;
    private int lastGuid;
    private long lastMergeDialogId;
    private int lastReplyMessageId;
    private int lastReqId;
    private int lastReturnedNum;
    private TLRPC.Chat lastSearchChat;
    private String lastSearchQuery;
    private TLRPC.User lastSearchUser;
    private int loadFeaturedDate;
    private long loadFeaturedHash;
    public boolean loadFeaturedPremium;
    boolean loaded;
    boolean loading;
    private boolean loadingDrafts;
    private boolean loadingFeaturedStickers;
    private boolean loadingMoreSearchMessages;
    private boolean loadingRecentGifs;
    private int menuBotsUpdateDate;
    private long menuBotsUpdateHash;
    private int mergeReqId;
    private TLRPC.TL_help_premiumPromo premiumPromo;
    private int premiumPromoUpdateDate;
    boolean previewStickersLoading;
    private int reactionsUpdateDate;
    private int reactionsUpdateHash;
    private boolean recentGifsLoaded;
    private int reqId;
    public final RingtoneDataStore ringtoneDataStore;
    private static Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static Pattern ITALIC_PATTERN = Pattern.compile("__(.+?)__");
    private static Pattern SPOILER_PATTERN = Pattern.compile("\\|\\|(.+?)\\|\\|");
    private static Pattern STRIKE_PATTERN = Pattern.compile("~~(.+?)~~");
    public static String SHORTCUT_CATEGORY = "org.telegram.messenger.SHORTCUT_SHARE";
    private static volatile MediaDataController[] Instance = new MediaDataController[4];
    private static final Object[] lockObjects = new Object[4];
    private TLRPC.TL_attachMenuBots attachMenuBots = new TLRPC.TL_attachMenuBots();
    private List<TLRPC.TL_availableReaction> reactionsList = new ArrayList();
    private List<TLRPC.TL_availableReaction> enabledReactionsList = new ArrayList();
    private HashMap<String, TLRPC.TL_availableReaction> reactionsMap = new HashMap<>();
    private ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(0), new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC.Document>[] stickersByIds = {new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>()};
    private LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsById = new LongSparseArray<>();
    private LongSparseArray<TLRPC.TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray<>();
    private LongSparseArray<TLRPC.TL_messages_stickerSet> groupStickerSets = new LongSparseArray<>();
    private ConcurrentHashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByName = new ConcurrentHashMap<>(100, 1.0f, 1);
    private HashMap<String, TLRPC.TL_messages_stickerSet> diceStickerSetsByEmoji = new HashMap<>();
    private LongSparseArray<String> diceEmojiStickerSetsById = new LongSparseArray<>();
    private HashSet<String> loadingDiceStickerSets = new HashSet<>();
    private LongSparseArray<Runnable> removingStickerSetsUndos = new LongSparseArray<>();
    private Runnable[] scheduledLoadStickers = new Runnable[5];
    private boolean[] loadingStickers = new boolean[5];
    private boolean[] stickersLoaded = new boolean[5];
    private long[] loadHash = new long[5];
    private int[] loadDate = new int[5];
    public HashMap<String, RingtoneUploader> ringtoneUploaderHashMap = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC.Message>> verifyingMessages = new HashMap<>();
    private int[] archivedStickersCount = new int[2];
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray<>();
    private HashMap<String, ArrayList<TLRPC.Document>> allStickers = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC.Document>> allStickersFeatured = new HashMap<>();
    private ArrayList<TLRPC.Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private boolean[] loadingRecentStickers = new boolean[4];
    private boolean[] recentStickersLoaded = new boolean[4];
    private ArrayList<TLRPC.Document> recentGifs = new ArrayList<>();
    private ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = new ArrayList<>();
    private LongSparseArray<TLRPC.StickerSetCovered> featuredStickerSetsById = new LongSparseArray<>();
    private ArrayList<Long> unreadStickerSets = new ArrayList<>();
    private ArrayList<Long> readingStickerSets = new ArrayList<>();
    public final ArrayList<ChatThemeBottomSheet.ChatThemeItem> defaultEmojiThemes = new ArrayList<>();
    public final ArrayList<TLRPC.Document> premiumPreviewStickers = new ArrayList<>();
    private int[] messagesSearchCount = {0, 0};
    private boolean[] messagesSearchEndReached = {false, false};
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private SparseArray<MessageObject>[] searchResultMessagesMap = {new SparseArray<>(), new SparseArray<>()};
    public ArrayList<TLRPC.TL_topPeer> hints = new ArrayList<>();
    public ArrayList<TLRPC.TL_topPeer> inlineBots = new ArrayList<>();
    private LongSparseArray<Boolean> loadingPinnedMessages = new LongSparseArray<>();
    private LongSparseArray<Integer> draftsFolderIds = new LongSparseArray<>();
    private LongSparseArray<SparseArray<TLRPC.DraftMessage>> drafts = new LongSparseArray<>();
    private LongSparseArray<SparseArray<TLRPC.Message>> draftMessages = new LongSparseArray<>();
    private HashMap<String, TLRPC.BotInfo> botInfos = new HashMap<>();
    private LongSparseArray<TLRPC.Message> botKeyboards = new LongSparseArray<>();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private HashMap<String, Boolean> currentFetchingEmoji = new HashMap<>();

    /* loaded from: classes4.dex */
    public static class KeywordResult {
        public String emoji;
        public String keyword;
    }

    /* loaded from: classes4.dex */
    public interface KeywordResultCallback {
        void run(ArrayList<KeywordResult> arrayList, String str);
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
        entityComparator = MediaDataController$$ExternalSyntheticLambda29.INSTANCE;
    }

    public static MediaDataController getInstance(int num) {
        MediaDataController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (lockObjects) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    MediaDataController[] mediaDataControllerArr = Instance;
                    MediaDataController mediaDataController = new MediaDataController(num);
                    localInstance = mediaDataController;
                    mediaDataControllerArr[num] = mediaDataController;
                }
            }
        }
        return localInstance;
    }

    public MediaDataController(int num) {
        super(num);
        String key;
        long did;
        SerializedData serializedData;
        boolean isThread;
        if (this.currentAccount == 0) {
            this.draftPreferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.draftPreferences = context.getSharedPreferences("drafts" + this.currentAccount, 0);
        }
        Map<String, ?> values = this.draftPreferences.getAll();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            try {
                key = entry.getKey();
                did = Utilities.parseLong(key).longValue();
                byte[] bytes = Utilities.hexToBytes((String) entry.getValue());
                serializedData = new SerializedData(bytes);
                isThread = false;
            } catch (Exception e) {
            }
            if (!key.startsWith("r_")) {
                boolean startsWith = key.startsWith("rt_");
                isThread = startsWith;
                if (!startsWith) {
                    TLRPC.DraftMessage draftMessage = TLRPC.DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (draftMessage != null) {
                        SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(did);
                        if (threads == null) {
                            threads = new SparseArray<>();
                            this.drafts.put(did, threads);
                        }
                        int threadId = key.startsWith("t_") ? Utilities.parseInt((CharSequence) key.substring(key.lastIndexOf(95) + 1)).intValue() : 0;
                        threads.put(threadId, draftMessage);
                    }
                    serializedData.cleanup();
                }
            }
            TLRPC.Message message = TLRPC.Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
            if (message != null) {
                message.readAttachPath(serializedData, getUserConfig().clientUserId);
                SparseArray<TLRPC.Message> threads2 = this.draftMessages.get(did);
                if (threads2 == null) {
                    threads2 = new SparseArray<>();
                    this.draftMessages.put(did, threads2);
                }
                int threadId2 = isThread ? Utilities.parseInt((CharSequence) key.substring(key.lastIndexOf(95) + 1)).intValue() : 0;
                threads2.put(threadId2, message);
            }
            serializedData.cleanup();
        }
        loadStickersByEmojiOrName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME, false, true);
        loadEmojiThemes();
        this.ringtoneDataStore = new RingtoneDataStore(this.currentAccount);
    }

    public void cleanup() {
        int a = 0;
        while (true) {
            ArrayList<TLRPC.Document>[] arrayListArr = this.recentStickers;
            if (a >= arrayListArr.length) {
                break;
            }
            arrayListArr[a].clear();
            this.loadingRecentStickers[a] = false;
            this.recentStickersLoaded[a] = false;
            a++;
        }
        for (int a2 = 0; a2 < 4; a2++) {
            this.loadHash[a2] = 0;
            this.loadDate[a2] = 0;
            this.stickerSets[a2].clear();
            this.loadingStickers[a2] = false;
            this.stickersLoaded[a2] = false;
        }
        this.loadingPinnedMessages.clear();
        this.loadFeaturedDate = 0;
        this.loadFeaturedHash = 0L;
        this.allStickers.clear();
        this.allStickersFeatured.clear();
        this.stickersByEmoji.clear();
        this.featuredStickerSetsById.clear();
        this.featuredStickerSets.clear();
        this.unreadStickerSets.clear();
        this.recentGifs.clear();
        this.stickerSetsById.clear();
        this.installedStickerSetsById.clear();
        this.stickerSetsByName.clear();
        this.diceStickerSetsByEmoji.clear();
        this.diceEmojiStickerSetsById.clear();
        this.loadingDiceStickerSets.clear();
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = false;
        this.loadingRecentGifs = false;
        this.recentGifsLoaded = false;
        this.currentFetchingEmoji.clear();
        if (Build.VERSION.SDK_INT >= 25) {
            Utilities.globalQueue.postRunnable(MediaDataController$$ExternalSyntheticLambda25.INSTANCE);
        }
        this.verifyingMessages.clear();
        this.loading = false;
        this.loaded = false;
        this.hints.clear();
        this.inlineBots.clear();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda109
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m424lambda$cleanup$1$orgtelegrammessengerMediaDataController();
            }
        });
        this.drafts.clear();
        this.draftMessages.clear();
        this.draftPreferences.edit().clear().apply();
        this.botInfos.clear();
        this.botKeyboards.clear();
        this.botKeyboardsByMids.clear();
    }

    public static /* synthetic */ void lambda$cleanup$0() {
        try {
            ShortcutManagerCompat.removeAllDynamicShortcuts(ApplicationLoader.applicationContext);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$cleanup$1$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m424lambda$cleanup$1$orgtelegrammessengerMediaDataController() {
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public void checkStickers(int type) {
        if (!this.loadingStickers[type]) {
            if (!this.stickersLoaded[type] || Math.abs((System.currentTimeMillis() / 1000) - this.loadDate[type]) >= 3600) {
                loadStickers(type, true, false);
            }
        }
    }

    public void checkReactions() {
        if (!this.isLoadingReactions && Math.abs((System.currentTimeMillis() / 1000) - this.reactionsUpdateDate) >= 3600) {
            loadReactions(true, false);
        }
    }

    public void checkMenuBots() {
        if (!this.isLoadingMenuBots && Math.abs((System.currentTimeMillis() / 1000) - this.menuBotsUpdateDate) >= 3600) {
            loadAttachMenuBots(true, false);
        }
    }

    public void checkPremiumPromo() {
        if (!this.isLoadingPremiumPromo && Math.abs((System.currentTimeMillis() / 1000) - this.premiumPromoUpdateDate) >= 3600) {
            loadPremiumPromo(true);
        }
    }

    public TLRPC.TL_help_premiumPromo getPremiumPromo() {
        return this.premiumPromo;
    }

    public TLRPC.TL_attachMenuBots getAttachMenuBots() {
        return this.attachMenuBots;
    }

    public void loadAttachMenuBots(boolean cache, boolean force) {
        this.isLoadingMenuBots = true;
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda142
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m450xd5c0a490();
                }
            });
            return;
        }
        TLRPC.TL_messages_getAttachMenuBots req = new TLRPC.TL_messages_getAttachMenuBots();
        req.hash = force ? 0L : this.menuBotsUpdateHash;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda38
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m451x98ad0def(tLObject, tL_error);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0042, code lost:
        if (r0 != null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x004b, code lost:
        if (r0 == null) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x004d, code lost:
        r0.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0050, code lost:
        processLoadedMenuBots(r4, r1, r3, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x005b, code lost:
        return;
     */
    /* renamed from: lambda$loadAttachMenuBots$2$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m450xd5c0a490() {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            org.telegram.messenger.MessagesStorage r6 = r11.getMessagesStorage()     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            org.telegram.SQLite.SQLiteDatabase r6 = r6.getDatabase()     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            java.lang.String r7 = "SELECT data, hash, date FROM attach_menu_bots"
            java.lang.Object[] r8 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            org.telegram.SQLite.SQLiteCursor r6 = r6.queryFinalized(r7, r8)     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            r0 = r6
            boolean r6 = r0.next()     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            if (r6 == 0) goto L42
            org.telegram.tgnet.NativeByteBuffer r6 = r0.byteBufferValue(r5)     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            r7 = 1
            if (r6 == 0) goto L37
            int r8 = r6.readInt32(r5)     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            org.telegram.tgnet.TLRPC$AttachMenuBots r8 = org.telegram.tgnet.TLRPC.TL_attachMenuBots.TLdeserialize(r6, r8, r7)     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_attachMenuBots     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            if (r9 == 0) goto L34
            r9 = r8
            org.telegram.tgnet.TLRPC$TL_attachMenuBots r9 = (org.telegram.tgnet.TLRPC.TL_attachMenuBots) r9     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            r4 = r9
        L34:
            r6.reuse()     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
        L37:
            long r7 = r0.longValue(r7)     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            r1 = r7
            r7 = 2
            int r5 = r0.intValue(r7)     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L47
            r3 = r5
        L42:
            if (r0 == 0) goto L50
            goto L4d
        L45:
            r5 = move-exception
            goto L5c
        L47:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6, r5)     // Catch: java.lang.Throwable -> L45
            if (r0 == 0) goto L50
        L4d:
            r0.dispose()
        L50:
            r7 = r1
            r9 = r3
            r10 = r4
            r6 = 1
            r1 = r11
            r2 = r10
            r3 = r7
            r5 = r9
            r1.processLoadedMenuBots(r2, r3, r5, r6)
            return
        L5c:
            if (r0 == 0) goto L61
            r0.dispose()
        L61:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m450xd5c0a490():void");
    }

    /* renamed from: lambda$loadAttachMenuBots$3$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m451x98ad0def(TLObject response, TLRPC.TL_error error) {
        int date = (int) (System.currentTimeMillis() / 1000);
        if (response instanceof TLRPC.TL_attachMenuBotsNotModified) {
            processLoadedMenuBots(null, 0L, date, false);
        } else if (response instanceof TLRPC.TL_attachMenuBots) {
            TLRPC.TL_attachMenuBots r = (TLRPC.TL_attachMenuBots) response;
            processLoadedMenuBots(r, r.hash, date, false);
        }
    }

    public void processLoadedMenuBots(TLRPC.TL_attachMenuBots bots, long hash, int date, boolean cache) {
        if (bots != null && date != 0) {
            this.attachMenuBots = bots;
            this.menuBotsUpdateHash = hash;
        }
        this.menuBotsUpdateDate = date;
        if (bots != null) {
            getMessagesController().putUsers(bots.users, cache);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda77
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m520xdb24e53f();
                }
            });
        }
        if (!cache) {
            putMenuBotsToCache(bots, hash, date);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - date) >= 3600) {
            loadAttachMenuBots(false, true);
        }
    }

    /* renamed from: lambda$processLoadedMenuBots$4$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m520xdb24e53f() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.attachMenuBotsDidLoad, new Object[0]);
    }

    private void putMenuBotsToCache(final TLRPC.TL_attachMenuBots bots, final long hash, final int date) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda170
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m537x7981acd6(bots, hash, date);
            }
        });
    }

    /* renamed from: lambda$putMenuBotsToCache$5$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m537x7981acd6(TLRPC.TL_attachMenuBots bots, long hash, int date) {
        try {
            if (bots != null) {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM attach_menu_bots").stepThis().dispose();
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO attach_menu_bots VALUES(?, ?, ?)");
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(bots.getObjectSize());
                bots.serializeToStream(data);
                state.bindByteBuffer(1, data);
                state.bindLong(2, hash);
                state.bindInteger(3, date);
                state.step();
                data.reuse();
                state.dispose();
            } else {
                SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE attach_menu_bots SET date = ?");
                state2.requery();
                state2.bindLong(1, date);
                state2.step();
                state2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void loadPremiumPromo(boolean cache) {
        this.isLoadingPremiumPromo = true;
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda44
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m481xd1823c44();
                }
            });
            return;
        }
        TLRPC.TL_help_getPremiumPromo req = new TLRPC.TL_help_getPremiumPromo();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda42
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m482x946ea5a3(tLObject, tL_error);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x003f, code lost:
        if (r0 == null) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0042, code lost:
        if (r2 == null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0044, code lost:
        processLoadedPremiumPromo(r2, r1, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0047, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:?, code lost:
        return;
     */
    /* renamed from: lambda$loadPremiumPromo$6$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m481xd1823c44() {
        /*
            r8 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 1
            r4 = 0
            org.telegram.messenger.MessagesStorage r5 = r8.getMessagesStorage()     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            org.telegram.SQLite.SQLiteDatabase r5 = r5.getDatabase()     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            java.lang.String r6 = "SELECT data, date FROM premium_promo"
            java.lang.Object[] r7 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r6, r7)     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            r0 = r5
            boolean r5 = r0.next()     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            if (r5 == 0) goto L33
            org.telegram.tgnet.NativeByteBuffer r5 = r0.byteBufferValue(r4)     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            if (r5 == 0) goto L2e
            int r6 = r5.readInt32(r4)     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            org.telegram.tgnet.TLRPC$TL_help_premiumPromo r6 = org.telegram.tgnet.TLRPC.TL_help_premiumPromo.TLdeserialize(r5, r6, r3)     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            r2 = r6
            r5.reuse()     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
        L2e:
            int r4 = r0.intValue(r3)     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3b
            r1 = r4
        L33:
            if (r0 == 0) goto L42
        L35:
            r0.dispose()
            goto L42
        L39:
            r3 = move-exception
            goto L48
        L3b:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5, r4)     // Catch: java.lang.Throwable -> L39
            if (r0 == 0) goto L42
            goto L35
        L42:
            if (r2 == 0) goto L47
            r8.processLoadedPremiumPromo(r2, r1, r3)
        L47:
            return
        L48:
            if (r0 == 0) goto L4d
            r0.dispose()
        L4d:
            goto L4f
        L4e:
            throw r3
        L4f:
            goto L4e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m481xd1823c44():void");
    }

    /* renamed from: lambda$loadPremiumPromo$7$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m482x946ea5a3(TLObject response, TLRPC.TL_error error) {
        int date = (int) (System.currentTimeMillis() / 1000);
        if (response instanceof TLRPC.TL_help_premiumPromo) {
            TLRPC.TL_help_premiumPromo r = (TLRPC.TL_help_premiumPromo) response;
            processLoadedPremiumPromo(r, date, false);
        }
    }

    private void processLoadedPremiumPromo(TLRPC.TL_help_premiumPromo premiumPromo, int date, boolean cache) {
        this.premiumPromo = premiumPromo;
        this.premiumPromoUpdateDate = date;
        getMessagesController().putUsers(premiumPromo.users, cache);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda84
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m521x81199fee();
            }
        });
        if (!cache) {
            putPremiumPromoToCache(premiumPromo, date);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - date) >= 86400 || BuildVars.DEBUG_PRIVATE_VERSION) {
            loadPremiumPromo(false);
        }
    }

    /* renamed from: lambda$processLoadedPremiumPromo$8$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m521x81199fee() {
        getNotificationCenter().postNotificationName(NotificationCenter.premiumPromoUpdated, new Object[0]);
    }

    private void putPremiumPromoToCache(final TLRPC.TL_help_premiumPromo premiumPromo, final int date) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m538x4721983f(premiumPromo, date);
            }
        });
    }

    /* renamed from: lambda$putPremiumPromoToCache$9$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m538x4721983f(TLRPC.TL_help_premiumPromo premiumPromo, int date) {
        try {
            if (premiumPromo != null) {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM premium_promo").stepThis().dispose();
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO premium_promo VALUES(?, ?)");
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(premiumPromo.getObjectSize());
                premiumPromo.serializeToStream(data);
                state.bindByteBuffer(1, data);
                state.bindInteger(2, date);
                state.step();
                data.reuse();
                state.dispose();
            } else {
                SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE premium_promo SET date = ?");
                state2.requery();
                state2.bindInteger(1, date);
                state2.step();
                state2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public List<TLRPC.TL_availableReaction> getReactionsList() {
        return this.reactionsList;
    }

    public void loadReactions(boolean cache, boolean force) {
        this.isLoadingReactions = true;
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda55
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m483x21618a15();
                }
            });
            return;
        }
        TLRPC.TL_messages_getAvailableReactions req = new TLRPC.TL_messages_getAvailableReactions();
        req.hash = force ? 0 : this.reactionsUpdateHash;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda43
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m484xe44df374(tLObject, tL_error);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0059, code lost:
        if (r0 == null) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x005c, code lost:
        processLoadedReactions(r3, r1, r2, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x005f, code lost:
        return;
     */
    /* renamed from: lambda$loadReactions$10$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m483x21618a15() {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 1
            r5 = 0
            org.telegram.messenger.MessagesStorage r6 = r10.getMessagesStorage()     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            org.telegram.SQLite.SQLiteDatabase r6 = r6.getDatabase()     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            java.lang.String r7 = "SELECT data, hash, date FROM reactions"
            java.lang.Object[] r8 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            org.telegram.SQLite.SQLiteCursor r6 = r6.queryFinalized(r7, r8)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            r0 = r6
            boolean r6 = r0.next()     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            if (r6 == 0) goto L4d
            org.telegram.tgnet.NativeByteBuffer r6 = r0.byteBufferValue(r5)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            if (r6 == 0) goto L42
            int r7 = r6.readInt32(r5)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            r8.<init>(r7)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            r3 = r8
            r8 = 0
        L2e:
            if (r8 >= r7) goto L3f
            int r9 = r6.readInt32(r5)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            org.telegram.tgnet.TLRPC$TL_availableReaction r9 = org.telegram.tgnet.TLRPC.TL_availableReaction.TLdeserialize(r6, r9, r4)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            r3.add(r9)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            int r8 = r8 + 1
            goto L2e
        L3f:
            r6.reuse()     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
        L42:
            int r7 = r0.intValue(r4)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            r1 = r7
            r7 = 2
            int r5 = r0.intValue(r7)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L55
            r2 = r5
        L4d:
            if (r0 == 0) goto L5c
        L4f:
            r0.dispose()
            goto L5c
        L53:
            r4 = move-exception
            goto L60
        L55:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6, r5)     // Catch: java.lang.Throwable -> L53
            if (r0 == 0) goto L5c
            goto L4f
        L5c:
            r10.processLoadedReactions(r3, r1, r2, r4)
            return
        L60:
            if (r0 == 0) goto L65
            r0.dispose()
        L65:
            goto L67
        L66:
            throw r4
        L67:
            goto L66
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m483x21618a15():void");
    }

    /* renamed from: lambda$loadReactions$11$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m484xe44df374(TLObject response, TLRPC.TL_error error) {
        int date = (int) (System.currentTimeMillis() / 1000);
        if (response instanceof TLRPC.TL_messages_availableReactionsNotModified) {
            processLoadedReactions(null, 0, date, false);
        } else if (response instanceof TLRPC.TL_messages_availableReactions) {
            TLRPC.TL_messages_availableReactions r = (TLRPC.TL_messages_availableReactions) response;
            processLoadedReactions(r.reactions, r.hash, date, false);
        }
    }

    public void processLoadedReactions(final List<TLRPC.TL_availableReaction> reactions, int hash, int date, boolean cache) {
        if (reactions != null && date != 0) {
            this.reactionsList.clear();
            this.reactionsMap.clear();
            this.enabledReactionsList.clear();
            this.reactionsList.addAll(reactions);
            for (int i = 0; i < this.reactionsList.size(); i++) {
                this.reactionsList.get(i).positionInList = i;
                this.reactionsMap.put(this.reactionsList.get(i).reaction, this.reactionsList.get(i));
                if (!this.reactionsList.get(i).inactive) {
                    this.enabledReactionsList.add(this.reactionsList.get(i));
                }
            }
            this.reactionsUpdateHash = hash;
        }
        this.reactionsUpdateDate = date;
        if (reactions != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.lambda$processLoadedReactions$12(reactions);
                }
            });
        }
        this.isLoadingReactions = false;
        if (!cache) {
            putReactionsToCache(reactions, hash, date);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - date) >= 3600) {
            loadReactions(false, true);
        }
    }

    public static /* synthetic */ void lambda$processLoadedReactions$12(List reactions) {
        for (int i = 0; i < reactions.size(); i++) {
            ImageReceiver imageReceiver = new ImageReceiver();
            TLRPC.TL_availableReaction reaction = (TLRPC.TL_availableReaction) reactions.get(i);
            imageReceiver.setImage(ImageLocation.getForDocument(reaction.activate_animation), null, null, null, 0, 11);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
            ImageReceiver imageReceiver2 = new ImageReceiver();
            imageReceiver2.setImage(ImageLocation.getForDocument(reaction.appear_animation), "60_60_nolimit", null, null, 0, 11);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver2);
            int size = ReactionsEffectOverlay.sizeForBigReaction();
            ImageReceiver imageReceiver3 = new ImageReceiver();
            ImageLocation forDocument = ImageLocation.getForDocument(reaction.around_animation);
            imageReceiver3.setImage(forDocument, size + "_" + size, null, null, 0, 11);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver3);
            ImageReceiver imageReceiver4 = new ImageReceiver();
            imageReceiver4.setImage(ImageLocation.getForDocument(reaction.center_icon), null, null, null, 0, 11);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver4);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reactionsDidLoad, new Object[0]);
    }

    private void putReactionsToCache(List<TLRPC.TL_availableReaction> reactions, final int hash, final int date) {
        final ArrayList<TLRPC.TL_availableReaction> reactionsFinal = reactions != null ? new ArrayList<>(reactions) : null;
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda138
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m539x31e068fa(reactionsFinal, hash, date);
            }
        });
    }

    /* renamed from: lambda$putReactionsToCache$13$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m539x31e068fa(ArrayList reactionsFinal, int hash, int date) {
        try {
            if (reactionsFinal != null) {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM reactions").stepThis().dispose();
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO reactions VALUES(?, ?, ?)");
                state.requery();
                int size = 4;
                for (int a = 0; a < reactionsFinal.size(); a++) {
                    size += ((TLRPC.TL_availableReaction) reactionsFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                data.writeInt32(reactionsFinal.size());
                for (int a2 = 0; a2 < reactionsFinal.size(); a2++) {
                    ((TLRPC.TL_availableReaction) reactionsFinal.get(a2)).serializeToStream(data);
                }
                state.bindByteBuffer(1, data);
                state.bindInteger(2, hash);
                state.bindInteger(3, date);
                state.step();
                data.reuse();
                state.dispose();
                return;
            }
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE reactions SET date = ?");
            state2.requery();
            state2.bindLong(1, date);
            state2.step();
            state2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void checkFeaturedStickers() {
        if (!this.loadingFeaturedStickers) {
            if (!this.featuredStickersLoaded || Math.abs((System.currentTimeMillis() / 1000) - this.loadFeaturedDate) >= 3600) {
                loadFeaturedStickers(true, false);
            }
        }
    }

    public ArrayList<TLRPC.Document> getRecentStickers(int type) {
        ArrayList<TLRPC.Document> arrayList = this.recentStickers[type];
        return new ArrayList<>(arrayList.subList(0, Math.min(arrayList.size(), 20)));
    }

    public ArrayList<TLRPC.Document> getRecentStickersNoCopy(int type) {
        return this.recentStickers[type];
    }

    public boolean isStickerInFavorites(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < this.recentStickers[2].size(); a++) {
            TLRPC.Document d = this.recentStickers[2].get(a);
            if (d.id == document.id && d.dc_id == document.dc_id) {
                return true;
            }
        }
        return false;
    }

    public void clearRecentStickers() {
        TLRPC.TL_messages_clearRecentStickers req = new TLRPC.TL_messages_clearRecentStickers();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda37
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m428xa5269b69(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$clearRecentStickers$16$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m428xa5269b69(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda152
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m427xe23a320a(response);
            }
        });
    }

    /* renamed from: lambda$clearRecentStickers$15$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m427xe23a320a(TLObject response) {
        if (response instanceof TLRPC.TL_boolTrue) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda120
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m426x1f4dc8ab();
                }
            });
            this.recentStickers[0].clear();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoad, false, 0);
        }
    }

    /* renamed from: lambda$clearRecentStickers$14$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m426x1f4dc8ab() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE type = 3").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void addRecentSticker(final int type, final Object parentObject, TLRPC.Document document, int date, boolean remove) {
        boolean found;
        int maxCount;
        final TLRPC.Document old;
        if (type != 3) {
            if (!MessageObject.isStickerDocument(document) && !MessageObject.isAnimatedStickerDocument(document, true)) {
                return;
            }
            int a = 0;
            while (true) {
                if (a >= this.recentStickers[type].size()) {
                    found = false;
                    break;
                }
                TLRPC.Document image = this.recentStickers[type].get(a);
                if (image.id != document.id) {
                    a++;
                } else {
                    this.recentStickers[type].remove(a);
                    if (!remove) {
                        this.recentStickers[type].add(0, image);
                    }
                    found = true;
                }
            }
            if (!found && !remove) {
                this.recentStickers[type].add(0, document);
            }
            if (type == 2) {
                if (remove) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, document, 4);
                } else {
                    boolean replace = this.recentStickers[type].size() > getMessagesController().maxFaveStickersCount;
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i = NotificationCenter.showBulletin;
                    Object[] objArr = new Object[3];
                    objArr[0] = 0;
                    objArr[1] = document;
                    objArr[2] = Integer.valueOf(replace ? 6 : 5);
                    globalInstance.postNotificationName(i, objArr);
                }
                final TLRPC.TL_messages_faveSticker req = new TLRPC.TL_messages_faveSticker();
                req.id = new TLRPC.TL_inputDocument();
                req.id.id = document.id;
                req.id.access_hash = document.access_hash;
                req.id.file_reference = document.file_reference;
                if (req.id.file_reference == null) {
                    req.id.file_reference = new byte[0];
                }
                req.unfave = remove;
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda65
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.m416x16db8c44(parentObject, req, tLObject, tL_error);
                    }
                });
                int maxCount2 = getMessagesController().maxFaveStickersCount;
                maxCount = maxCount2;
            } else {
                if (type == 0 && remove) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, document, 3);
                    final TLRPC.TL_messages_saveRecentSticker req2 = new TLRPC.TL_messages_saveRecentSticker();
                    req2.id = new TLRPC.TL_inputDocument();
                    req2.id.id = document.id;
                    req2.id.access_hash = document.access_hash;
                    req2.id.file_reference = document.file_reference;
                    if (req2.id.file_reference == null) {
                        req2.id.file_reference = new byte[0];
                    }
                    req2.unsave = true;
                    getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda67
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MediaDataController.this.m417xd9c7f5a3(parentObject, req2, tLObject, tL_error);
                        }
                    });
                }
                maxCount = getMessagesController().maxRecentStickersCount;
            }
            if (this.recentStickers[type].size() > maxCount || remove) {
                if (remove) {
                    old = document;
                } else {
                    ArrayList<TLRPC.Document>[] arrayListArr = this.recentStickers;
                    old = arrayListArr[type].remove(arrayListArr[type].size() - 1);
                }
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda94
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m418x9a1903cd(type, old);
                    }
                });
            }
            if (!remove) {
                ArrayList<TLRPC.Document> arrayList = new ArrayList<>();
                arrayList.add(document);
                processLoadedRecentDocuments(type, arrayList, false, date, false);
            }
            if (type == 2 || (type == 0 && remove)) {
                getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, false, Integer.valueOf(type));
            }
        }
    }

    /* renamed from: lambda$addRecentSticker$18$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m416x16db8c44(Object parentObject, TLRPC.TL_messages_faveSticker req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text) && parentObject != null) {
            getFileRefController().requestReference(parentObject, req);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda98
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m415x53ef22e5();
                }
            });
        }
    }

    /* renamed from: lambda$addRecentSticker$17$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m415x53ef22e5() {
        getMediaDataController().loadRecents(2, false, false, true);
    }

    /* renamed from: lambda$addRecentSticker$19$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m417xd9c7f5a3(Object parentObject, TLRPC.TL_messages_saveRecentSticker req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text) && parentObject != null) {
            getFileRefController().requestReference(parentObject, req);
        }
    }

    /* renamed from: lambda$addRecentSticker$20$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m418x9a1903cd(int type, TLRPC.Document old) {
        int cacheType;
        if (type == 0) {
            cacheType = 3;
        } else if (type == 1) {
            cacheType = 4;
        } else {
            cacheType = 5;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "' AND type = " + cacheType).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public ArrayList<TLRPC.Document> getRecentGifs() {
        return new ArrayList<>(this.recentGifs);
    }

    public void removeRecentGif(final TLRPC.Document document) {
        int i = 0;
        int N = this.recentGifs.size();
        while (true) {
            if (i >= N) {
                break;
            } else if (this.recentGifs.get(i).id != document.id) {
                i++;
            } else {
                this.recentGifs.remove(i);
                break;
            }
        }
        final TLRPC.TL_messages_saveGif req = new TLRPC.TL_messages_saveGif();
        req.id = new TLRPC.TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.id.file_reference = document.file_reference;
        if (req.id.file_reference == null) {
            req.id.file_reference = new byte[0];
        }
        req.unsave = true;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda76
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m542x525b8996(req, tLObject, tL_error);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda162
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m543x1547f2f5(document);
            }
        });
    }

    /* renamed from: lambda$removeRecentGif$21$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m542x525b8996(TLRPC.TL_messages_saveGif req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text)) {
            getFileRefController().requestReference("gif", req);
        }
    }

    /* renamed from: lambda$removeRecentGif$22$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m543x1547f2f5(TLRPC.Document document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean hasRecentGif(TLRPC.Document document) {
        for (int a = 0; a < this.recentGifs.size(); a++) {
            TLRPC.Document image = this.recentGifs.get(a);
            if (image.id == document.id) {
                this.recentGifs.remove(a);
                this.recentGifs.add(0, image);
                return true;
            }
        }
        return false;
    }

    public void addRecentGif(final TLRPC.Document document, int date, boolean showReplaceBulletin) {
        if (document == null) {
            return;
        }
        boolean found = false;
        int a = 0;
        while (true) {
            if (a >= this.recentGifs.size()) {
                break;
            }
            TLRPC.Document image = this.recentGifs.get(a);
            if (image.id != document.id) {
                a++;
            } else {
                this.recentGifs.remove(a);
                this.recentGifs.add(0, image);
                found = true;
                break;
            }
        }
        if (!found) {
            this.recentGifs.add(0, document);
        }
        if ((this.recentGifs.size() > getMessagesController().savedGifsLimitDefault && !UserConfig.getInstance(this.currentAccount).isPremium()) || this.recentGifs.size() > getMessagesController().savedGifsLimitPremium) {
            ArrayList<TLRPC.Document> arrayList = this.recentGifs;
            final TLRPC.Document old = arrayList.remove(arrayList.size() - 1);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda161
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m414xd6865ab1(old);
                }
            });
            if (showReplaceBulletin) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda23
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, TLRPC.Document.this, 7);
                    }
                });
            }
        }
        ArrayList<TLRPC.Document> arrayList2 = new ArrayList<>();
        arrayList2.add(document);
        processLoadedRecentDocuments(0, arrayList2, true, date, false);
    }

    /* renamed from: lambda$addRecentGif$23$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m414xd6865ab1(TLRPC.Document old) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean isLoadingStickers(int type) {
        return this.loadingStickers[type];
    }

    /* JADX WARN: Type inference failed for: r4v4, types: [boolean] */
    public void replaceStickerSet(final TLRPC.TL_messages_stickerSet set) {
        TLRPC.TL_messages_stickerSet existingSet = this.stickerSetsById.get(set.set.id);
        String emoji = this.diceEmojiStickerSetsById.get(set.set.id);
        if (emoji != null) {
            this.diceStickerSetsByEmoji.put(emoji, set);
            putDiceStickersToCache(emoji, set, (int) (System.currentTimeMillis() / 1000));
        }
        boolean isGroupSet = false;
        if (existingSet == null) {
            existingSet = this.stickerSetsByName.get(set.set.short_name);
        }
        if (existingSet == null && (existingSet = this.groupStickerSets.get(set.set.id)) != null) {
            isGroupSet = true;
        }
        if (existingSet == null) {
            return;
        }
        boolean changed = false;
        if ("AnimatedEmojies".equals(set.set.short_name)) {
            changed = true;
            existingSet.documents = set.documents;
            existingSet.packs = set.packs;
            existingSet.set = set.set;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m544x65dd3fc6(set);
                }
            });
        } else {
            LongSparseArray<TLRPC.Document> documents = new LongSparseArray<>();
            int size = set.documents.size();
            for (int a = 0; a < size; a++) {
                TLRPC.Document document = set.documents.get(a);
                documents.put(document.id, document);
            }
            int size2 = existingSet.documents.size();
            for (int a2 = 0; a2 < size2; a2++) {
                TLRPC.Document newDocument = documents.get(existingSet.documents.get(a2).id);
                if (newDocument != null) {
                    existingSet.documents.set(a2, newDocument);
                    changed = true;
                }
            }
        }
        if (changed) {
            if (isGroupSet) {
                putSetToCache(existingSet);
                return;
            }
            ?? r4 = set.set.masks;
            ArrayList<TLRPC.TL_messages_stickerSet>[] arrayListArr = this.stickerSets;
            int type = r4 == true ? 1 : 0;
            ArrayList<TLRPC.TL_messages_stickerSet> arrayList = arrayListArr[type];
            int i = this.loadDate[r4];
            long j = this.loadHash[r4];
            int type2 = r4 == true ? 1 : 0;
            putStickersToCache(type2, arrayList, i, j);
            if ("AnimatedEmojies".equals(set.set.short_name)) {
                putStickersToCache(4, this.stickerSets[4], this.loadDate[4], this.loadHash[4]);
            }
        }
    }

    /* renamed from: lambda$replaceStickerSet$25$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m544x65dd3fc6(TLRPC.TL_messages_stickerSet set) {
        LongSparseArray<TLRPC.Document> stickersById = getStickerByIds(4);
        for (int b = 0; b < set.documents.size(); b++) {
            TLRPC.Document document = set.documents.get(b);
            stickersById.put(document.id, document);
        }
    }

    public TLRPC.TL_messages_stickerSet getStickerSetByName(String name) {
        return this.stickerSetsByName.get(name);
    }

    public TLRPC.TL_messages_stickerSet getStickerSetByEmojiOrName(String emoji) {
        return this.diceStickerSetsByEmoji.get(emoji);
    }

    public TLRPC.TL_messages_stickerSet getStickerSetById(long id) {
        return this.stickerSetsById.get(id);
    }

    public TLRPC.TL_messages_stickerSet getGroupStickerSetById(TLRPC.StickerSet stickerSet) {
        TLRPC.TL_messages_stickerSet set = this.stickerSetsById.get(stickerSet.id);
        if (set == null) {
            set = this.groupStickerSets.get(stickerSet.id);
            if (set == null || set.set == null) {
                loadGroupStickerSet(stickerSet, true);
            } else if (set.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
        }
        return set;
    }

    public void putGroupStickerSet(TLRPC.TL_messages_stickerSet stickerSet) {
        this.groupStickerSets.put(stickerSet.set.id, stickerSet);
    }

    private void loadGroupStickerSet(final TLRPC.StickerSet stickerSet, boolean cache) {
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda166
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m463x8d933149(stickerSet);
                }
            });
            return;
        }
        TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
        req.stickerset = new TLRPC.TL_inputStickerSetID();
        req.stickerset.id = stickerSet.id;
        req.stickerset.access_hash = stickerSet.access_hash;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda40
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m465x136c0407(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadGroupStickerSet$27$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m463x8d933149(TLRPC.StickerSet stickerSet) {
        final TLRPC.TL_messages_stickerSet set;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + stickerSet.id + "'", new Object[0]);
            if (cursor.next() && !cursor.isNull(0)) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    set = TLRPC.TL_messages_stickerSet.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                } else {
                    set = null;
                }
            } else {
                set = null;
            }
            cursor.dispose();
            if (set == null || set.set == null || set.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
            if (set != null && set.set != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m462xcaa6c7ea(set);
                    }
                });
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadGroupStickerSet$26$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m462xcaa6c7ea(TLRPC.TL_messages_stickerSet set) {
        this.groupStickerSets.put(set.set.id, set);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(set.set.id));
    }

    /* renamed from: lambda$loadGroupStickerSet$29$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m465x136c0407(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            final TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) response;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m464x507f9aa8(set);
                }
            });
        }
    }

    /* renamed from: lambda$loadGroupStickerSet$28$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m464x507f9aa8(TLRPC.TL_messages_stickerSet set) {
        this.groupStickerSets.put(set.set.id, set);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(set.set.id));
    }

    private void putSetToCache(final TLRPC.TL_messages_stickerSet set) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m540xf2a5f167(set);
            }
        });
    }

    /* renamed from: lambda$putSetToCache$30$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m540xf2a5f167(TLRPC.TL_messages_stickerSet set) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLitePreparedStatement state = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            state.requery();
            state.bindString(1, "s_" + set.set.id);
            state.bindInteger(2, 6);
            state.bindString(3, "");
            state.bindString(4, "");
            state.bindString(5, "");
            state.bindInteger(6, 0);
            state.bindInteger(7, 0);
            state.bindInteger(8, 0);
            state.bindInteger(9, 0);
            NativeByteBuffer data = new NativeByteBuffer(set.getObjectSize());
            set.serializeToStream(data);
            state.bindByteBuffer(10, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public HashMap<String, ArrayList<TLRPC.Document>> getAllStickers() {
        return this.allStickers;
    }

    public HashMap<String, ArrayList<TLRPC.Document>> getAllStickersFeatured() {
        return this.allStickersFeatured;
    }

    public TLRPC.Document getEmojiAnimatedSticker(CharSequence message) {
        if (message == null) {
            return null;
        }
        String emoji = message.toString().replace("️", "");
        ArrayList<TLRPC.TL_messages_stickerSet> arrayList = getStickerSets(4);
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            TLRPC.TL_messages_stickerSet set = arrayList.get(a);
            int N2 = set.packs.size();
            for (int b = 0; b < N2; b++) {
                TLRPC.TL_stickerPack pack = set.packs.get(b);
                if (!pack.documents.isEmpty() && TextUtils.equals(pack.emoticon, emoji)) {
                    LongSparseArray<TLRPC.Document> stickerByIds = getStickerByIds(4);
                    return stickerByIds.get(pack.documents.get(0).longValue());
                }
            }
        }
        return null;
    }

    public boolean canAddStickerToFavorites() {
        return !this.stickersLoaded[0] || this.stickerSets[0].size() >= 5 || !this.recentStickers[2].isEmpty();
    }

    public ArrayList<TLRPC.TL_messages_stickerSet> getStickerSets(int type) {
        if (type == 3) {
            return this.stickerSets[2];
        }
        return this.stickerSets[type];
    }

    public LongSparseArray<TLRPC.Document> getStickerByIds(int type) {
        return this.stickersByIds[type];
    }

    public ArrayList<TLRPC.StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets;
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets;
    }

    public boolean areAllTrendingStickerSetsUnread() {
        int N = this.featuredStickerSets.size();
        for (int a = 0; a < N; a++) {
            TLRPC.StickerSetCovered pack = this.featuredStickerSets.get(a);
            if (!isStickerPackInstalled(pack.set.id) && ((!pack.covers.isEmpty() || pack.cover != null) && !this.unreadStickerSets.contains(Long.valueOf(pack.set.id)))) {
                return false;
            }
        }
        return true;
    }

    public boolean isStickerPackInstalled(long id) {
        return this.installedStickerSetsById.indexOfKey(id) >= 0;
    }

    public boolean isStickerPackUnread(long id) {
        return this.unreadStickerSets.contains(Long.valueOf(id));
    }

    public boolean isStickerPackInstalled(String name) {
        return this.stickerSetsByName.containsKey(name);
    }

    public String getEmojiForSticker(long id) {
        String value = this.stickersByEmoji.get(id);
        return value != null ? value : "";
    }

    public static boolean canShowAttachMenuBotForTarget(TLRPC.TL_attachMenuBot bot, String target) {
        Iterator<TLRPC.AttachMenuPeerType> it = bot.peer_types.iterator();
        while (it.hasNext()) {
            TLRPC.AttachMenuPeerType peerType = it.next();
            if ((!(peerType instanceof TLRPC.TL_attachMenuPeerTypeSameBotPM) && !(peerType instanceof TLRPC.TL_attachMenuPeerTypeBotPM)) || !target.equals("bots")) {
                if (!(peerType instanceof TLRPC.TL_attachMenuPeerTypeBroadcast) || !target.equals("channels")) {
                    if (!(peerType instanceof TLRPC.TL_attachMenuPeerTypeChat) || !target.equals("groups")) {
                        if ((peerType instanceof TLRPC.TL_attachMenuPeerTypePM) && target.equals("users")) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean canShowAttachMenuBot(TLRPC.TL_attachMenuBot bot, TLObject peer) {
        TLRPC.Chat chat = null;
        TLRPC.User user = peer instanceof TLRPC.User ? (TLRPC.User) peer : null;
        if (peer instanceof TLRPC.Chat) {
            chat = (TLRPC.Chat) peer;
        }
        Iterator<TLRPC.AttachMenuPeerType> it = bot.peer_types.iterator();
        while (it.hasNext()) {
            TLRPC.AttachMenuPeerType peerType = it.next();
            if ((peerType instanceof TLRPC.TL_attachMenuPeerTypeSameBotPM) && user != null && user.bot && user.id == bot.bot_id) {
                return true;
            }
            if ((peerType instanceof TLRPC.TL_attachMenuPeerTypeBotPM) && user != null && user.bot && user.id != bot.bot_id) {
                return true;
            }
            if (!(peerType instanceof TLRPC.TL_attachMenuPeerTypePM) || user == null || user.bot) {
                if (!(peerType instanceof TLRPC.TL_attachMenuPeerTypeChat) || chat == null || ChatObject.isChannelAndNotMegaGroup(chat)) {
                    if ((peerType instanceof TLRPC.TL_attachMenuPeerTypeBroadcast) && chat != null && ChatObject.isChannelAndNotMegaGroup(chat)) {
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static TLRPC.TL_attachMenuBotIcon getAnimatedAttachMenuBotIcon(TLRPC.TL_attachMenuBot bot) {
        Iterator<TLRPC.TL_attachMenuBotIcon> it = bot.icons.iterator();
        while (it.hasNext()) {
            TLRPC.TL_attachMenuBotIcon icon = it.next();
            if (icon.name.equals(ATTACH_MENU_BOT_ANIMATED_ICON_KEY)) {
                return icon;
            }
        }
        return null;
    }

    public static TLRPC.TL_attachMenuBotIcon getStaticAttachMenuBotIcon(TLRPC.TL_attachMenuBot bot) {
        Iterator<TLRPC.TL_attachMenuBotIcon> it = bot.icons.iterator();
        while (it.hasNext()) {
            TLRPC.TL_attachMenuBotIcon icon = it.next();
            if (icon.name.equals(ATTACH_MENU_BOT_STATIC_ICON_KEY)) {
                return icon;
            }
        }
        return null;
    }

    public static TLRPC.TL_attachMenuBotIcon getPlaceholderStaticAttachMenuBotIcon(TLRPC.TL_attachMenuBot bot) {
        Iterator<TLRPC.TL_attachMenuBotIcon> it = bot.icons.iterator();
        while (it.hasNext()) {
            TLRPC.TL_attachMenuBotIcon icon = it.next();
            if (icon.name.equals(ATTACH_MENU_BOT_PLACEHOLDER_STATIC_KEY)) {
                return icon;
            }
        }
        return null;
    }

    public static long calcDocumentsHash(ArrayList<TLRPC.Document> arrayList) {
        return calcDocumentsHash(arrayList, 200);
    }

    public static long calcDocumentsHash(ArrayList<TLRPC.Document> arrayList, int maxCount) {
        if (arrayList == null) {
            return 0L;
        }
        long acc = 0;
        int N = Math.min(maxCount, arrayList.size());
        for (int a = 0; a < N; a++) {
            TLRPC.Document document = arrayList.get(a);
            if (document != null) {
                acc = calcHash(acc, document.id);
            }
        }
        return acc;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void loadRecents(final int type, final boolean gif, boolean cache, boolean force) {
        TLObject request;
        long lastLoadTime;
        boolean z = true;
        if (gif) {
            if (this.loadingRecentGifs) {
                return;
            }
            this.loadingRecentGifs = true;
            if (this.recentGifsLoaded) {
                cache = false;
            }
        } else {
            boolean[] zArr = this.loadingRecentStickers;
            if (zArr[type]) {
                return;
            }
            zArr[type] = true;
            if (this.recentStickersLoaded[type]) {
                cache = false;
            }
        }
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m486lambda$loadRecents$32$orgtelegrammessengerMediaDataController(gif, type);
                }
            });
            return;
        }
        SharedPreferences preferences = MessagesController.getEmojiSettings(this.currentAccount);
        if (!force) {
            if (gif) {
                lastLoadTime = preferences.getLong("lastGifLoadTime", 0L);
            } else {
                lastLoadTime = type == 0 ? preferences.getLong("lastStickersLoadTime", 0L) : type == 1 ? preferences.getLong("lastStickersLoadTimeMask", 0L) : type == 3 ? preferences.getLong("lastStickersLoadTimeGreet", 0L) : preferences.getLong("lastStickersLoadTimeFavs", 0L);
            }
            if (Math.abs(System.currentTimeMillis() - lastLoadTime) < 3600000) {
                if (gif) {
                    this.loadingRecentGifs = false;
                    return;
                } else {
                    this.loadingRecentStickers[type] = false;
                    return;
                }
            }
        }
        if (gif) {
            TLRPC.TL_messages_getSavedGifs req = new TLRPC.TL_messages_getSavedGifs();
            req.hash = calcDocumentsHash(this.recentGifs);
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda47
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.m487lambda$loadRecents$33$orgtelegrammessengerMediaDataController(type, tLObject, tL_error);
                }
            });
            return;
        }
        if (type != 2) {
            if (type == 3) {
                TLRPC.TL_messages_getStickers req2 = new TLRPC.TL_messages_getStickers();
                req2.emoticon = "👋" + Emoji.fixEmoji("⭐");
                req2.hash = calcDocumentsHash(this.recentStickers[type]);
                request = req2;
            } else {
                TLRPC.TL_messages_getRecentStickers req3 = new TLRPC.TL_messages_getRecentStickers();
                req3.hash = calcDocumentsHash(this.recentStickers[type]);
                if (type != 1) {
                    z = false;
                }
                req3.attached = z;
                request = req3;
            }
        } else {
            TLRPC.TL_messages_getFavedStickers req4 = new TLRPC.TL_messages_getFavedStickers();
            req4.hash = calcDocumentsHash(this.recentStickers[type]);
            request = req4;
        }
        getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda48
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m488lambda$loadRecents$34$orgtelegrammessengerMediaDataController(type, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadRecents$32$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m486lambda$loadRecents$32$orgtelegrammessengerMediaDataController(final boolean gif, final int type) {
        int cacheType;
        if (gif) {
            cacheType = 2;
        } else if (type == 0) {
            cacheType = 3;
        } else if (type == 1) {
            cacheType = 4;
        } else if (type == 3) {
            cacheType = 6;
        } else {
            cacheType = 5;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor cursor = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + cacheType + " ORDER BY date DESC", new Object[0]);
            final ArrayList<TLRPC.Document> arrayList = new ArrayList<>();
            while (cursor.next()) {
                if (!cursor.isNull(0)) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.Document document = TLRPC.Document.TLdeserialize(data, data.readInt32(false), false);
                        if (document != null) {
                            arrayList.add(document);
                        }
                        data.reuse();
                    }
                }
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m485lambda$loadRecents$31$orgtelegrammessengerMediaDataController(gif, arrayList, type);
                }
            });
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$loadRecents$31$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m485lambda$loadRecents$31$orgtelegrammessengerMediaDataController(boolean gif, ArrayList arrayList, int type) {
        if (gif) {
            this.recentGifs = arrayList;
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
        } else {
            this.recentStickers[type] = arrayList;
            this.loadingRecentStickers[type] = false;
            this.recentStickersLoaded[type] = true;
        }
        if (type == 3) {
            preloadNextGreetingsSticker();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(gif), Integer.valueOf(type));
        loadRecents(type, gif, false, false);
    }

    /* renamed from: lambda$loadRecents$33$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m487lambda$loadRecents$33$orgtelegrammessengerMediaDataController(int type, TLObject response, TLRPC.TL_error error) {
        ArrayList<TLRPC.Document> arrayList = null;
        if (response instanceof TLRPC.TL_messages_savedGifs) {
            TLRPC.TL_messages_savedGifs res = (TLRPC.TL_messages_savedGifs) response;
            arrayList = res.gifs;
        }
        processLoadedRecentDocuments(type, arrayList, true, 0, true);
    }

    /* renamed from: lambda$loadRecents$34$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m488lambda$loadRecents$34$orgtelegrammessengerMediaDataController(int type, TLObject response, TLRPC.TL_error error) {
        ArrayList<TLRPC.Document> arrayList = null;
        if (type == 3) {
            if (response instanceof TLRPC.TL_messages_stickers) {
                TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
                arrayList = res.stickers;
            }
        } else if (type == 2) {
            if (response instanceof TLRPC.TL_messages_favedStickers) {
                TLRPC.TL_messages_favedStickers res2 = (TLRPC.TL_messages_favedStickers) response;
                arrayList = res2.stickers;
            }
        } else if (response instanceof TLRPC.TL_messages_recentStickers) {
            TLRPC.TL_messages_recentStickers res3 = (TLRPC.TL_messages_recentStickers) response;
            arrayList = res3.stickers;
        }
        processLoadedRecentDocuments(type, arrayList, false, 0, true);
    }

    private void preloadNextGreetingsSticker() {
        if (this.recentStickers[3].isEmpty()) {
            return;
        }
        this.greetingsSticker = this.recentStickers[3].get(Utilities.random.nextInt(this.recentStickers[3].size()));
        getFileLoader().loadFile(ImageLocation.getForDocument(this.greetingsSticker), this.greetingsSticker, null, 0, 1);
    }

    public TLRPC.Document getGreetingsSticker() {
        TLRPC.Document result = this.greetingsSticker;
        preloadNextGreetingsSticker();
        return result;
    }

    public void processLoadedRecentDocuments(final int type, final ArrayList<TLRPC.Document> documents, final boolean gif, final int date, final boolean replace) {
        if (documents != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m522x109c9511(gif, type, documents, replace, date);
                }
            });
        }
        if (date == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m523xd388fe70(gif, type, documents);
                }
            });
        }
    }

    /* renamed from: lambda$processLoadedRecentDocuments$35$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m522x109c9511(boolean gif, int type, ArrayList documents, boolean replace, int date) {
        int maxCount;
        int cacheType;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            if (gif) {
                maxCount = getMessagesController().maxRecentGifsCount;
            } else if (type == 3) {
                maxCount = 200;
            } else if (type == 2) {
                maxCount = getMessagesController().maxFaveStickersCount;
            } else {
                maxCount = getMessagesController().maxRecentStickersCount;
            }
            database.beginTransaction();
            SQLitePreparedStatement state = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int count = documents.size();
            if (gif) {
                cacheType = 2;
            } else if (type == 0) {
                cacheType = 3;
            } else if (type == 1) {
                cacheType = 4;
            } else if (type == 3) {
                cacheType = 6;
            } else {
                cacheType = 5;
            }
            if (replace) {
                database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + cacheType).stepThis().dispose();
            }
            for (int a = 0; a < count && a != maxCount; a++) {
                TLRPC.Document document = (TLRPC.Document) documents.get(a);
                state.requery();
                state.bindString(1, "" + document.id);
                state.bindInteger(2, cacheType);
                state.bindString(3, "");
                state.bindString(4, "");
                state.bindString(5, "");
                state.bindInteger(6, 0);
                state.bindInteger(7, 0);
                state.bindInteger(8, 0);
                state.bindInteger(9, date != 0 ? date : count - a);
                NativeByteBuffer data = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(data);
                state.bindByteBuffer(10, data);
                state.step();
                data.reuse();
            }
            state.dispose();
            database.commitTransaction();
            if (documents.size() >= maxCount) {
                database.beginTransaction();
                for (int a2 = maxCount; a2 < documents.size(); a2++) {
                    database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC.Document) documents.get(a2)).id + "' AND type = " + cacheType).stepThis().dispose();
                }
                database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$processLoadedRecentDocuments$36$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m523xd388fe70(boolean gif, int type, ArrayList documents) {
        SharedPreferences.Editor editor = MessagesController.getEmojiSettings(this.currentAccount).edit();
        if (gif) {
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
            editor.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
        } else {
            this.loadingRecentStickers[type] = false;
            this.recentStickersLoaded[type] = true;
            if (type == 0) {
                editor.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            } else if (type == 1) {
                editor.putLong("lastStickersLoadTimeMask", System.currentTimeMillis()).commit();
            } else if (type == 3) {
                editor.putLong("lastStickersLoadTimeGreet", System.currentTimeMillis()).commit();
            } else {
                editor.putLong("lastStickersLoadTimeFavs", System.currentTimeMillis()).commit();
            }
        }
        if (documents != null) {
            if (gif) {
                this.recentGifs = documents;
            } else {
                this.recentStickers[type] = documents;
            }
            if (type == 3) {
                preloadNextGreetingsSticker();
            }
            getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(gif), Integer.valueOf(type));
        }
    }

    public void reorderStickers(int type, final ArrayList<Long> order) {
        Collections.sort(this.stickerSets[type], new Comparator() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda27
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return MediaDataController.lambda$reorderStickers$37(order, (TLRPC.TL_messages_stickerSet) obj, (TLRPC.TL_messages_stickerSet) obj2);
            }
        });
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
        loadStickers(type, false, true);
    }

    public static /* synthetic */ int lambda$reorderStickers$37(ArrayList order, TLRPC.TL_messages_stickerSet lhs, TLRPC.TL_messages_stickerSet rhs) {
        int index1 = order.indexOf(Long.valueOf(lhs.set.id));
        int index2 = order.indexOf(Long.valueOf(rhs.set.id));
        if (index1 > index2) {
            return 1;
        }
        if (index1 < index2) {
            return -1;
        }
        return 0;
    }

    public void calcNewHash(int type) {
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
    }

    public void storeTempStickerSet(TLRPC.TL_messages_stickerSet set) {
        this.stickerSetsById.put(set.set.id, set);
        this.stickerSetsByName.put(set.set.short_name, set);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [int, boolean] */
    public void addNewStickerSet(TLRPC.TL_messages_stickerSet set) {
        if (this.stickerSetsById.indexOfKey(set.set.id) >= 0 || this.stickerSetsByName.containsKey(set.set.short_name)) {
            return;
        }
        ?? r0 = set.set.masks;
        ArrayList<TLRPC.TL_messages_stickerSet>[] arrayListArr = this.stickerSets;
        int type = r0 == true ? 1 : 0;
        arrayListArr[type].add(0, set);
        this.stickerSetsById.put(set.set.id, set);
        this.installedStickerSetsById.put(set.set.id, set);
        this.stickerSetsByName.put(set.set.short_name, set);
        LongSparseArray<TLRPC.Document> stickersById = new LongSparseArray<>();
        for (int a = 0; a < set.documents.size(); a++) {
            TLRPC.Document document = set.documents.get(a);
            stickersById.put(document.id, document);
        }
        for (int a2 = 0; a2 < set.packs.size(); a2++) {
            TLRPC.TL_stickerPack stickerPack = set.packs.get(a2);
            stickerPack.emoticon = stickerPack.emoticon.replace("️", "");
            ArrayList<TLRPC.Document> arrayList = this.allStickers.get(stickerPack.emoticon);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.allStickers.put(stickerPack.emoticon, arrayList);
            }
            for (int c = 0; c < stickerPack.documents.size(); c++) {
                Long id = stickerPack.documents.get(c);
                if (this.stickersByEmoji.indexOfKey(id.longValue()) < 0) {
                    this.stickersByEmoji.put(id.longValue(), stickerPack.emoticon);
                }
                TLRPC.Document sticker = stickersById.get(id.longValue());
                if (sticker != null) {
                    arrayList.add(sticker);
                }
            }
        }
        this.loadHash[r0] = calcStickersHash(this.stickerSets[r0]);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.stickersDidLoad;
        int type2 = r0 == true ? 1 : 0;
        notificationCenter.postNotificationName(i, Integer.valueOf(type2));
        loadStickers(r0, false, true);
    }

    public void loadFeaturedStickers(boolean cache, boolean force) {
        if (this.loadingFeaturedStickers) {
            return;
        }
        this.loadingFeaturedStickers = true;
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda175
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m459x30a79def();
                }
            });
            return;
        }
        final TLRPC.TL_messages_getFeaturedStickers req = new TLRPC.TL_messages_getFeaturedStickers();
        req.hash = force ? 0L : this.loadFeaturedHash;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda75
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m461xb3e51578(req, tLObject, tL_error);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x007c, code lost:
        if (r6 != null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0088, code lost:
        r11 = r3;
        r3 = r0;
        processLoadedFeaturedStickers(r3, r1, r5, true, r2, r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0096, code lost:
        return;
     */
    /* renamed from: lambda$loadFeaturedStickers$38$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m459x30a79def() {
        /*
            r15 = this;
            r0 = 0
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2 = 0
            r3 = 0
            r5 = 0
            r6 = 0
            org.telegram.messenger.MessagesStorage r7 = r15.getMessagesStorage()     // Catch: java.lang.Throwable -> L7f
            org.telegram.SQLite.SQLiteDatabase r7 = r7.getDatabase()     // Catch: java.lang.Throwable -> L7f
            java.lang.String r8 = "SELECT data, unread, date, hash, premium FROM stickers_featured WHERE 1"
            r9 = 0
            java.lang.Object[] r10 = new java.lang.Object[r9]     // Catch: java.lang.Throwable -> L7f
            org.telegram.SQLite.SQLiteCursor r7 = r7.queryFinalized(r8, r10)     // Catch: java.lang.Throwable -> L7f
            r6 = r7
            boolean r7 = r6.next()     // Catch: java.lang.Throwable -> L7f
            if (r7 == 0) goto L7c
            org.telegram.tgnet.NativeByteBuffer r7 = r6.byteBufferValue(r9)     // Catch: java.lang.Throwable -> L7f
            if (r7 == 0) goto L48
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L7f
            r8.<init>()     // Catch: java.lang.Throwable -> L7f
            r0 = r8
            int r8 = r7.readInt32(r9)     // Catch: java.lang.Throwable -> L7f
            r10 = 0
        L34:
            if (r10 >= r8) goto L45
            int r11 = r7.readInt32(r9)     // Catch: java.lang.Throwable -> L7f
            org.telegram.tgnet.TLRPC$StickerSetCovered r11 = org.telegram.tgnet.TLRPC.StickerSetCovered.TLdeserialize(r7, r11, r9)     // Catch: java.lang.Throwable -> L7f
            r0.add(r11)     // Catch: java.lang.Throwable -> L7f
            int r10 = r10 + 1
            goto L34
        L45:
            r7.reuse()     // Catch: java.lang.Throwable -> L7f
        L48:
            r8 = 1
            org.telegram.tgnet.NativeByteBuffer r10 = r6.byteBufferValue(r8)     // Catch: java.lang.Throwable -> L7f
            r7 = r10
            if (r7 == 0) goto L68
            int r10 = r7.readInt32(r9)     // Catch: java.lang.Throwable -> L7f
            r11 = 0
        L55:
            if (r11 >= r10) goto L65
            long r12 = r7.readInt64(r9)     // Catch: java.lang.Throwable -> L7f
            java.lang.Long r12 = java.lang.Long.valueOf(r12)     // Catch: java.lang.Throwable -> L7f
            r1.add(r12)     // Catch: java.lang.Throwable -> L7f
            int r11 = r11 + 1
            goto L55
        L65:
            r7.reuse()     // Catch: java.lang.Throwable -> L7f
        L68:
            r10 = 2
            int r10 = r6.intValue(r10)     // Catch: java.lang.Throwable -> L7f
            r2 = r10
            long r10 = r15.calcFeaturedStickersHash(r0)     // Catch: java.lang.Throwable -> L7f
            r3 = r10
            r10 = 4
            int r10 = r6.intValue(r10)     // Catch: java.lang.Throwable -> L7f
            if (r10 != r8) goto L7b
            r9 = 1
        L7b:
            r5 = r9
        L7c:
            if (r6 == 0) goto L88
            goto L85
        L7f:
            r7 = move-exception
            org.telegram.messenger.FileLog.e(r7)     // Catch: java.lang.Throwable -> L97
            if (r6 == 0) goto L88
        L85:
            r6.dispose()
        L88:
            r10 = r2
            r11 = r3
            r13 = r5
            r14 = r6
            r6 = 1
            r2 = r15
            r3 = r0
            r4 = r1
            r5 = r13
            r7 = r10
            r8 = r11
            r2.processLoadedFeaturedStickers(r3, r4, r5, r6, r7, r8)
            return
        L97:
            r7 = move-exception
            if (r6 == 0) goto L9d
            r6.dispose()
        L9d:
            goto L9f
        L9e:
            throw r7
        L9f:
            goto L9e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m459x30a79def():void");
    }

    /* renamed from: lambda$loadFeaturedStickers$40$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m461xb3e51578(final TLRPC.TL_messages_getFeaturedStickers req, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda158
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m460xf394074e(response, req);
            }
        });
    }

    /* renamed from: lambda$loadFeaturedStickers$39$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m460xf394074e(TLObject response, TLRPC.TL_messages_getFeaturedStickers req) {
        if (response instanceof TLRPC.TL_messages_featuredStickers) {
            TLRPC.TL_messages_featuredStickers res = (TLRPC.TL_messages_featuredStickers) response;
            processLoadedFeaturedStickers(res.sets, res.unread, res.premium, false, (int) (System.currentTimeMillis() / 1000), res.hash);
            return;
        }
        processLoadedFeaturedStickers(null, null, false, false, (int) (System.currentTimeMillis() / 1000), req.hash);
    }

    private void processLoadedFeaturedStickers(final ArrayList<TLRPC.StickerSetCovered> res, final ArrayList<Long> unreadStickers, final boolean premium, final boolean cache, final int date, final long hash) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m512x64a16b();
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m516xc1646e7(cache, res, date, hash, unreadStickers, premium);
            }
        });
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$41$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m512x64a16b() {
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = true;
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$45$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m516xc1646e7(boolean cache, final ArrayList res, final int date, final long hash, final ArrayList unreadStickers, final boolean premium) {
        long j = 0;
        if ((cache && (res == null || Math.abs((System.currentTimeMillis() / 1000) - date) >= 3600)) || (!cache && res == null && hash == 0)) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda141
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m513xc3510aca(res, hash);
                }
            };
            if (res == null && !cache) {
                j = 1000;
            }
            AndroidUtilities.runOnUIThread(runnable, j);
            if (res == null) {
                return;
            }
        }
        if (res == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda86
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m515x4929dd88(date);
                }
            });
            putFeaturedStickersToCache(null, null, date, 0L, premium);
            return;
        }
        try {
            final ArrayList<TLRPC.StickerSetCovered> stickerSetsNew = new ArrayList<>();
            final LongSparseArray<TLRPC.StickerSetCovered> stickerSetsByIdNew = new LongSparseArray<>();
            for (int a = 0; a < res.size(); a++) {
                TLRPC.StickerSetCovered stickerSet = (TLRPC.StickerSetCovered) res.get(a);
                stickerSetsNew.add(stickerSet);
                stickerSetsByIdNew.put(stickerSet.set.id, stickerSet);
            }
            if (!cache) {
                putFeaturedStickersToCache(stickerSetsNew, unreadStickers, date, hash, premium);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda146
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m514x863d7429(unreadStickers, stickerSetsByIdNew, stickerSetsNew, hash, date, premium);
                }
            });
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$42$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m513xc3510aca(ArrayList res, long hash) {
        if (res != null && hash != 0) {
            this.loadFeaturedHash = hash;
        }
        loadFeaturedStickers(false, false);
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$43$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m514x863d7429(ArrayList unreadStickers, LongSparseArray stickerSetsByIdNew, ArrayList stickerSetsNew, long hash, int date, boolean premium) {
        this.unreadStickerSets = unreadStickers;
        this.featuredStickerSetsById = stickerSetsByIdNew;
        this.featuredStickerSets = stickerSetsNew;
        this.loadFeaturedHash = hash;
        this.loadFeaturedDate = date;
        this.loadFeaturedPremium = premium;
        loadStickers(3, true, false);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$44$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m515x4929dd88(int date) {
        this.loadFeaturedDate = date;
    }

    private void putFeaturedStickersToCache(ArrayList<TLRPC.StickerSetCovered> stickers, final ArrayList<Long> unreadStickers, final int date, final long hash, final boolean premium) {
        final ArrayList<TLRPC.StickerSetCovered> stickersFinal = stickers != null ? new ArrayList<>(stickers) : null;
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda147
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m534xd1210c3c(stickersFinal, unreadStickers, date, hash, premium);
            }
        });
    }

    /* renamed from: lambda$putFeaturedStickersToCache$46$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m534xd1210c3c(ArrayList stickersFinal, ArrayList unreadStickers, int date, long hash, boolean premium) {
        int i = 1;
        try {
            if (stickersFinal != null) {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?, ?)");
                state.requery();
                int size = 4;
                for (int a = 0; a < stickersFinal.size(); a++) {
                    size += ((TLRPC.StickerSetCovered) stickersFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                NativeByteBuffer data2 = new NativeByteBuffer((unreadStickers.size() * 8) + 4);
                data.writeInt32(stickersFinal.size());
                for (int a2 = 0; a2 < stickersFinal.size(); a2++) {
                    ((TLRPC.StickerSetCovered) stickersFinal.get(a2)).serializeToStream(data);
                }
                int a3 = unreadStickers.size();
                data2.writeInt32(a3);
                for (int a4 = 0; a4 < unreadStickers.size(); a4++) {
                    data2.writeInt64(((Long) unreadStickers.get(a4)).longValue());
                }
                state.bindInteger(1, 1);
                state.bindByteBuffer(2, data);
                state.bindByteBuffer(3, data2);
                state.bindInteger(4, date);
                state.bindLong(5, hash);
                if (!premium) {
                    i = 0;
                }
                state.bindInteger(6, i);
                state.step();
                data.reuse();
                data2.reuse();
                state.dispose();
                return;
            }
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            state2.requery();
            state2.bindInteger(1, date);
            state2.step();
            state2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private long calcFeaturedStickersHash(ArrayList<TLRPC.StickerSetCovered> sets) {
        if (sets == null || sets.isEmpty()) {
            return 0L;
        }
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            TLRPC.StickerSet set = sets.get(a).set;
            if (!set.archived) {
                acc = calcHash(acc, set.id);
                if (this.unreadStickerSets.contains(Long.valueOf(set.id))) {
                    acc = calcHash(acc, 1L);
                }
            }
        }
        return acc;
    }

    public static long calcHash(long hash, long id) {
        return (((hash ^ (id >> 21)) ^ (id << 35)) ^ (id >> 4)) + id;
    }

    public void markFaturedStickersAsRead(boolean query) {
        if (this.unreadStickerSets.isEmpty()) {
            return;
        }
        this.unreadStickerSets.clear();
        this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash, this.loadFeaturedPremium);
        if (query) {
            TLRPC.TL_messages_readFeaturedStickers req = new TLRPC.TL_messages_readFeaturedStickers();
            getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda79.INSTANCE);
        }
    }

    public static /* synthetic */ void lambda$markFaturedStickersAsRead$47(TLObject response, TLRPC.TL_error error) {
    }

    public long getFeaturesStickersHashWithoutUnread() {
        long acc = 0;
        for (int a = 0; a < this.featuredStickerSets.size(); a++) {
            TLRPC.StickerSet set = this.featuredStickerSets.get(a).set;
            if (!set.archived) {
                acc = calcHash(acc, set.id);
            }
        }
        return acc;
    }

    public void markFaturedStickersByIdAsRead(final long id) {
        if (!this.unreadStickerSets.contains(Long.valueOf(id)) || this.readingStickerSets.contains(Long.valueOf(id))) {
            return;
        }
        this.readingStickerSets.add(Long.valueOf(id));
        TLRPC.TL_messages_readFeaturedStickers req = new TLRPC.TL_messages_readFeaturedStickers();
        req.id.add(Long.valueOf(id));
        getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda80.INSTANCE);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda101
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m503xdf1127c3(id);
            }
        }, 1000L);
    }

    public static /* synthetic */ void lambda$markFaturedStickersByIdAsRead$48(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$markFaturedStickersByIdAsRead$49$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m503xdf1127c3(long id) {
        this.unreadStickerSets.remove(Long.valueOf(id));
        this.readingStickerSets.remove(Long.valueOf(id));
        this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash, this.loadFeaturedPremium);
    }

    public int getArchivedStickersCount(int type) {
        return this.archivedStickersCount[type];
    }

    public void verifyAnimatedStickerMessage(TLRPC.Message message) {
        verifyAnimatedStickerMessage(message, false);
    }

    public void verifyAnimatedStickerMessage(final TLRPC.Message message, boolean safe) {
        if (message == null) {
            return;
        }
        TLRPC.Document document = MessageObject.getDocument(message);
        final String name = MessageObject.getStickerSetName(document);
        if (TextUtils.isEmpty(name)) {
            return;
        }
        TLRPC.TL_messages_stickerSet stickerSet = this.stickerSetsByName.get(name);
        if (stickerSet != null) {
            int N = stickerSet.documents.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Document sticker = stickerSet.documents.get(a);
                if (sticker.id == document.id && sticker.dc_id == document.dc_id) {
                    message.stickerVerified = 1;
                    return;
                }
            }
        } else if (safe) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda165
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m567x4d46cc35(message, name);
                }
            });
        } else {
            m567x4d46cc35(message, name);
        }
    }

    /* renamed from: verifyAnimatedStickerMessageInternal */
    public void m567x4d46cc35(TLRPC.Message message, final String name) {
        ArrayList<TLRPC.Message> messages = this.verifyingMessages.get(name);
        if (messages == null) {
            messages = new ArrayList<>();
            this.verifyingMessages.put(name, messages);
        }
        messages.add(message);
        TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
        req.stickerset = MessageObject.getInputStickerSet(message);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda68
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m569xebcfbc10(name, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$verifyAnimatedStickerMessageInternal$52$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m569xebcfbc10(final String name, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda132
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m568x28e352b1(name, response);
            }
        });
    }

    /* renamed from: lambda$verifyAnimatedStickerMessageInternal$51$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m568x28e352b1(String name, TLObject response) {
        ArrayList<TLRPC.Message> arrayList = this.verifyingMessages.get(name);
        if (response != null) {
            TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) response;
            storeTempStickerSet(set);
            int N2 = arrayList.size();
            for (int b = 0; b < N2; b++) {
                TLRPC.Message m = arrayList.get(b);
                TLRPC.Document d = MessageObject.getDocument(m);
                int a = 0;
                int N = set.documents.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.Document sticker = set.documents.get(a);
                    if (sticker.id != d.id || sticker.dc_id != d.dc_id) {
                        a++;
                    } else {
                        m.stickerVerified = 1;
                        break;
                    }
                }
                if (m.stickerVerified == 0) {
                    m.stickerVerified = 2;
                }
            }
        } else {
            int N22 = arrayList.size();
            for (int b2 = 0; b2 < N22; b2++) {
                arrayList.get(b2).stickerVerified = 2;
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.didVerifyMessagesStickers, arrayList);
        getMessagesStorage().updateMessageVerifyFlags(arrayList);
    }

    public void loadArchivedStickersCount(final int type, boolean cache) {
        boolean z = true;
        if (cache) {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            int count = preferences.getInt("archivedStickersCount" + type, -1);
            if (count == -1) {
                loadArchivedStickersCount(type, false);
                return;
            }
            this.archivedStickersCount[type] = count;
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(type));
            return;
        }
        TLRPC.TL_messages_getArchivedStickers req = new TLRPC.TL_messages_getArchivedStickers();
        req.limit = 0;
        if (type != 1) {
            z = false;
        }
        req.masks = z;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda46
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m449x299e7302(type, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadArchivedStickersCount$54$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m449x299e7302(final int type, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda174
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m448x66b209a3(error, response, type);
            }
        });
    }

    /* renamed from: lambda$loadArchivedStickersCount$53$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m448x66b209a3(TLRPC.TL_error error, TLObject response, int type) {
        if (error == null) {
            TLRPC.TL_messages_archivedStickers res = (TLRPC.TL_messages_archivedStickers) response;
            this.archivedStickersCount[type] = res.count;
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putInt("archivedStickersCount" + type, res.count).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(type));
        }
    }

    private void processLoadStickersResponse(final int type, final TLRPC.TL_messages_allStickers res) {
        TLRPC.TL_messages_allStickers tL_messages_allStickers = res;
        final ArrayList<TLRPC.TL_messages_stickerSet> newStickerArray = new ArrayList<>();
        long j = 1000;
        if (tL_messages_allStickers.sets.isEmpty()) {
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
            return;
        }
        final LongSparseArray<TLRPC.TL_messages_stickerSet> newStickerSets = new LongSparseArray<>();
        int a = 0;
        while (a < tL_messages_allStickers.sets.size()) {
            final TLRPC.StickerSet stickerSet = tL_messages_allStickers.sets.get(a);
            TLRPC.TL_messages_stickerSet oldSet = this.stickerSetsById.get(stickerSet.id);
            if (oldSet != null && oldSet.set.hash == stickerSet.hash) {
                oldSet.set.archived = stickerSet.archived;
                oldSet.set.installed = stickerSet.installed;
                oldSet.set.official = stickerSet.official;
                newStickerSets.put(oldSet.set.id, oldSet);
                newStickerArray.add(oldSet);
                if (newStickerSets.size() == tL_messages_allStickers.sets.size()) {
                    processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / j), tL_messages_allStickers.hash);
                }
            } else {
                newStickerArray.add(null);
                final int index = a;
                TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
                req.stickerset = new TLRPC.TL_inputStickerSetID();
                req.stickerset.id = stickerSet.id;
                req.stickerset.access_hash = stickerSet.access_hash;
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda71
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.m507x8b92e73b(newStickerArray, index, newStickerSets, stickerSet, res, type, tLObject, tL_error);
                    }
                });
            }
            a++;
            tL_messages_allStickers = res;
            j = 1000;
        }
    }

    /* renamed from: lambda$processLoadStickersResponse$56$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m507x8b92e73b(final ArrayList newStickerArray, final int index, final LongSparseArray newStickerSets, final TLRPC.StickerSet stickerSet, final TLRPC.TL_messages_allStickers res, final int type, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda156
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m506xc8a67ddc(response, newStickerArray, index, newStickerSets, stickerSet, res, type);
            }
        });
    }

    /* renamed from: lambda$processLoadStickersResponse$55$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m506xc8a67ddc(TLObject response, ArrayList newStickerArray, int index, LongSparseArray newStickerSets, TLRPC.StickerSet stickerSet, TLRPC.TL_messages_allStickers res, int type) {
        TLRPC.TL_messages_stickerSet res1 = (TLRPC.TL_messages_stickerSet) response;
        newStickerArray.set(index, res1);
        newStickerSets.put(stickerSet.id, res1);
        if (newStickerSets.size() == res.sets.size()) {
            int a1 = 0;
            while (a1 < newStickerArray.size()) {
                if (newStickerArray.get(a1) == null) {
                    newStickerArray.remove(a1);
                    a1--;
                }
                a1++;
            }
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
        }
    }

    public void loadStickersByEmojiOrName(final String name, final boolean isEmoji, boolean cache) {
        if (!this.loadingDiceStickerSets.contains(name)) {
            if (isEmoji && this.diceStickerSetsByEmoji.get(name) != null) {
                return;
            }
            this.loadingDiceStickerSets.add(name);
            if (cache) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda134
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m500x494bb2ef(name, isEmoji);
                    }
                });
                return;
            }
            TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
            if (isEmoji) {
                TLRPC.TL_inputStickerSetDice inputStickerSetDice = new TLRPC.TL_inputStickerSetDice();
                inputStickerSetDice.emoticon = name;
                req.stickerset = inputStickerSetDice;
            } else {
                TLRPC.TL_inputStickerSetShortName inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
                inputStickerSetShortName.short_name = name;
                req.stickerset = inputStickerSetShortName;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda70
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.m502xcf2485ad(name, isEmoji, tLObject, tL_error);
                }
            });
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x003f, code lost:
        if (r2 == null) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0042, code lost:
        processLoadedDiceStickers(r11, r12, r0, true, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x004b, code lost:
        return;
     */
    /* renamed from: lambda$loadStickersByEmojiOrName$57$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m500x494bb2ef(java.lang.String r11, boolean r12) {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            org.telegram.messenger.MessagesStorage r3 = r10.getMessagesStorage()     // Catch: java.lang.Throwable -> L3b
            org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch: java.lang.Throwable -> L3b
            java.lang.String r4 = "SELECT data, date FROM stickers_dice WHERE emoji = ?"
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L3b
            r7 = 0
            r6[r7] = r11     // Catch: java.lang.Throwable -> L3b
            org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r6)     // Catch: java.lang.Throwable -> L3b
            r2 = r3
            boolean r3 = r2.next()     // Catch: java.lang.Throwable -> L3b
            if (r3 == 0) goto L35
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r7)     // Catch: java.lang.Throwable -> L3b
            if (r3 == 0) goto L30
            int r4 = r3.readInt32(r7)     // Catch: java.lang.Throwable -> L3b
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = org.telegram.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r3, r4, r7)     // Catch: java.lang.Throwable -> L3b
            r0 = r4
            r3.reuse()     // Catch: java.lang.Throwable -> L3b
        L30:
            int r4 = r2.intValue(r5)     // Catch: java.lang.Throwable -> L3b
            r1 = r4
        L35:
            if (r2 == 0) goto L42
        L37:
            r2.dispose()
            goto L42
        L3b:
            r3 = move-exception
            org.telegram.messenger.FileLog.e(r3)     // Catch: java.lang.Throwable -> L4c
            if (r2 == 0) goto L42
            goto L37
        L42:
            r8 = 1
            r4 = r10
            r5 = r11
            r6 = r12
            r7 = r0
            r9 = r1
            r4.processLoadedDiceStickers(r5, r6, r7, r8, r9)
            return
        L4c:
            r3 = move-exception
            if (r2 == 0) goto L52
            r2.dispose()
        L52:
            goto L54
        L53:
            throw r3
        L54:
            goto L53
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m500x494bb2ef(java.lang.String, boolean):void");
    }

    /* renamed from: lambda$loadStickersByEmojiOrName$59$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m502xcf2485ad(final String name, final boolean isEmoji, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m501xc381c4e(error, response, name, isEmoji);
            }
        });
    }

    /* renamed from: lambda$loadStickersByEmojiOrName$58$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m501xc381c4e(TLRPC.TL_error error, TLObject response, String name, boolean isEmoji) {
        if (BuildConfig.DEBUG && error != null) {
            return;
        }
        if (response instanceof TLRPC.TL_messages_stickerSet) {
            processLoadedDiceStickers(name, isEmoji, (TLRPC.TL_messages_stickerSet) response, false, (int) (System.currentTimeMillis() / 1000));
        } else {
            processLoadedDiceStickers(name, isEmoji, null, false, (int) (System.currentTimeMillis() / 1000));
        }
    }

    private void processLoadedDiceStickers(final String name, final boolean isEmoji, final TLRPC.TL_messages_stickerSet res, final boolean cache, final int date) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda129
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m508xdfa1347(name);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m511x56bf4f64(cache, res, date, name, isEmoji);
            }
        });
    }

    /* renamed from: lambda$processLoadedDiceStickers$60$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m508xdfa1347(String name) {
        this.loadingDiceStickerSets.remove(name);
    }

    /* renamed from: lambda$processLoadedDiceStickers$63$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m511x56bf4f64(boolean cache, final TLRPC.TL_messages_stickerSet res, int date, final String name, final boolean isEmoji) {
        long j = 1000;
        if ((cache && (res == null || Math.abs((System.currentTimeMillis() / 1000) - date) >= 86400)) || (!cache && res == null)) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda135
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m509xd0e67ca6(name, isEmoji);
                }
            };
            if (res != null || cache) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(runnable, j);
            if (res == null) {
                return;
            }
        }
        if (res != null) {
            if (!cache) {
                putDiceStickersToCache(name, res, date);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda133
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m510x93d2e605(name, res);
                }
            });
        } else if (!cache) {
            putDiceStickersToCache(name, null, date);
        }
    }

    /* renamed from: lambda$processLoadedDiceStickers$61$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m509xd0e67ca6(String name, boolean isEmoji) {
        loadStickersByEmojiOrName(name, isEmoji, false);
    }

    /* renamed from: lambda$processLoadedDiceStickers$62$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m510x93d2e605(String name, TLRPC.TL_messages_stickerSet res) {
        this.diceStickerSetsByEmoji.put(name, res);
        this.diceEmojiStickerSetsById.put(res.set.id, name);
        getNotificationCenter().postNotificationName(NotificationCenter.diceStickersDidLoad, name);
    }

    private void putDiceStickersToCache(final String emoji, final TLRPC.TL_messages_stickerSet stickers, final int date) {
        if (TextUtils.isEmpty(emoji)) {
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m531x7f4420a7(stickers, emoji, date);
            }
        });
    }

    /* renamed from: lambda$putDiceStickersToCache$64$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m531x7f4420a7(TLRPC.TL_messages_stickerSet stickers, String emoji, int date) {
        try {
            if (stickers != null) {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_dice VALUES(?, ?, ?)");
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(stickers.getObjectSize());
                stickers.serializeToStream(data);
                state.bindString(1, emoji);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, date);
                state.step();
                data.reuse();
                state.dispose();
            } else {
                SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_dice SET date = ?");
                state2.requery();
                state2.bindInteger(1, date);
                state2.step();
                state2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void loadStickers(int type, boolean cache, boolean useHash) {
        loadStickers(type, cache, useHash, false);
    }

    public void loadStickers(final int type, boolean cache, final boolean force, boolean scheduleIfLoading) {
        TLObject req;
        if (this.loadingStickers[type]) {
            if (scheduleIfLoading) {
                this.scheduledLoadStickers[type] = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda95
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m495x24957a87(type, force);
                    }
                };
                return;
            }
            return;
        }
        if (type == 3) {
            if (this.featuredStickerSets.isEmpty() || !getMessagesController().preloadFeaturedStickers) {
                return;
            }
        } else if (type != 4) {
            loadArchivedStickersCount(type, cache);
        }
        this.loadingStickers[type] = true;
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda85
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m496xe781e3e6(type);
                }
            });
        } else if (type != 3) {
            if (type == 4) {
                TLRPC.TL_messages_getStickerSet req2 = new TLRPC.TL_messages_getStickerSet();
                req2.stickerset = new TLRPC.TL_inputStickerSetAnimatedEmoji();
                getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda49
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.m497xaa6e4d45(type, tLObject, tL_error);
                    }
                });
                return;
            }
            final long hash = 0;
            if (type == 0) {
                req = new TLRPC.TL_messages_getAllStickers();
                TLRPC.TL_messages_getAllStickers tL_messages_getAllStickers = (TLRPC.TL_messages_getAllStickers) req;
                if (!force) {
                    hash = this.loadHash[type];
                }
                tL_messages_getAllStickers.hash = hash;
            } else {
                req = new TLRPC.TL_messages_getMaskStickers();
                TLRPC.TL_messages_getMaskStickers tL_messages_getMaskStickers = (TLRPC.TL_messages_getMaskStickers) req;
                if (!force) {
                    hash = this.loadHash[type];
                }
                tL_messages_getMaskStickers.hash = hash;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda50
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.m499x30472003(type, hash, tLObject, tL_error);
                }
            });
        } else {
            TLRPC.TL_messages_allStickers response = new TLRPC.TL_messages_allStickers();
            response.hash = this.loadFeaturedHash;
            int size = this.featuredStickerSets.size();
            for (int a = 0; a < size; a++) {
                response.sets.add(this.featuredStickerSets.get(a).set);
            }
            processLoadStickersResponse(type, response);
        }
    }

    /* renamed from: lambda$loadStickers$65$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m495x24957a87(int type, boolean force) {
        loadStickers(type, false, force, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0068, code lost:
        if (r4 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x006b, code lost:
        processLoadedStickers(r14, r0, true, r1, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0074, code lost:
        return;
     */
    /* renamed from: lambda$loadStickers$66$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m496xe781e3e6(int r14) {
        /*
            r13 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            org.telegram.messenger.MessagesStorage r5 = r13.getMessagesStorage()     // Catch: java.lang.Throwable -> L64
            org.telegram.SQLite.SQLiteDatabase r5 = r5.getDatabase()     // Catch: java.lang.Throwable -> L64
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L64
            r6.<init>()     // Catch: java.lang.Throwable -> L64
            java.lang.String r7 = "SELECT data, date, hash FROM stickers_v2 WHERE id = "
            r6.append(r7)     // Catch: java.lang.Throwable -> L64
            int r7 = r14 + 1
            r6.append(r7)     // Catch: java.lang.Throwable -> L64
            java.lang.String r6 = r6.toString()     // Catch: java.lang.Throwable -> L64
            r7 = 0
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch: java.lang.Throwable -> L64
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r6, r8)     // Catch: java.lang.Throwable -> L64
            r4 = r5
            boolean r5 = r4.next()     // Catch: java.lang.Throwable -> L64
            if (r5 == 0) goto L5e
            org.telegram.tgnet.NativeByteBuffer r5 = r4.byteBufferValue(r7)     // Catch: java.lang.Throwable -> L64
            if (r5 == 0) goto L53
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L64
            r6.<init>()     // Catch: java.lang.Throwable -> L64
            r0 = r6
            int r6 = r5.readInt32(r7)     // Catch: java.lang.Throwable -> L64
            r8 = 0
        L3f:
            if (r8 >= r6) goto L50
            int r9 = r5.readInt32(r7)     // Catch: java.lang.Throwable -> L64
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r9 = org.telegram.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r5, r9, r7)     // Catch: java.lang.Throwable -> L64
            r0.add(r9)     // Catch: java.lang.Throwable -> L64
            int r8 = r8 + 1
            goto L3f
        L50:
            r5.reuse()     // Catch: java.lang.Throwable -> L64
        L53:
            r6 = 1
            int r6 = r4.intValue(r6)     // Catch: java.lang.Throwable -> L64
            r1 = r6
            long r6 = calcStickersHash(r0)     // Catch: java.lang.Throwable -> L64
            r2 = r6
        L5e:
            if (r4 == 0) goto L6b
        L60:
            r4.dispose()
            goto L6b
        L64:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5)     // Catch: java.lang.Throwable -> L75
            if (r4 == 0) goto L6b
            goto L60
        L6b:
            r9 = 1
            r6 = r13
            r7 = r14
            r8 = r0
            r10 = r1
            r11 = r2
            r6.processLoadedStickers(r7, r8, r9, r10, r11)
            return
        L75:
            r5 = move-exception
            if (r4 == 0) goto L7b
            r4.dispose()
        L7b:
            goto L7d
        L7c:
            throw r5
        L7d:
            goto L7c
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m496xe781e3e6(int):void");
    }

    /* renamed from: lambda$loadStickers$67$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m497xaa6e4d45(int type, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_messages_stickerSet) {
            ArrayList<TLRPC.TL_messages_stickerSet> newStickerArray = new ArrayList<>();
            newStickerArray.add((TLRPC.TL_messages_stickerSet) response);
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), calcStickersHash(newStickerArray));
            return;
        }
        processLoadedStickers(type, null, false, (int) (System.currentTimeMillis() / 1000), 0L);
    }

    /* renamed from: lambda$loadStickers$69$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m499x30472003(final int type, final long hash, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda155
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m498x6d5ab6a4(response, type, hash);
            }
        });
    }

    /* renamed from: lambda$loadStickers$68$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m498x6d5ab6a4(TLObject response, int type, long hash) {
        if (response instanceof TLRPC.TL_messages_allStickers) {
            processLoadStickersResponse(type, (TLRPC.TL_messages_allStickers) response);
        } else {
            processLoadedStickers(type, null, false, (int) (System.currentTimeMillis() / 1000), hash);
        }
    }

    private void putStickersToCache(final int type, ArrayList<TLRPC.TL_messages_stickerSet> stickers, final int date, final long hash) {
        final ArrayList<TLRPC.TL_messages_stickerSet> stickersFinal = stickers != null ? new ArrayList<>(stickers) : null;
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda139
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m541x9f38f13(stickersFinal, type, date, hash);
            }
        });
    }

    /* renamed from: lambda$putStickersToCache$70$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m541x9f38f13(ArrayList stickersFinal, int type, int date, long hash) {
        try {
            if (stickersFinal != null) {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                state.requery();
                int size = 4;
                for (int a = 0; a < stickersFinal.size(); a++) {
                    size += ((TLRPC.TL_messages_stickerSet) stickersFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                data.writeInt32(stickersFinal.size());
                for (int a2 = 0; a2 < stickersFinal.size(); a2++) {
                    ((TLRPC.TL_messages_stickerSet) stickersFinal.get(a2)).serializeToStream(data);
                }
                int a3 = type + 1;
                state.bindInteger(1, a3);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, date);
                state.bindLong(4, hash);
                state.step();
                data.reuse();
                state.dispose();
                return;
            }
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
            state2.requery();
            state2.bindLong(1, date);
            state2.step();
            state2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public String getStickerSetName(long setId) {
        TLRPC.TL_messages_stickerSet stickerSet = this.stickerSetsById.get(setId);
        if (stickerSet != null) {
            return stickerSet.set.short_name;
        }
        TLRPC.StickerSetCovered stickerSetCovered = this.featuredStickerSetsById.get(setId);
        if (stickerSetCovered != null) {
            return stickerSetCovered.set.short_name;
        }
        return null;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetID) {
                    return attribute.stickerset.id;
                } else {
                    return -1L;
                }
            }
        }
        return -1L;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return null;
                } else {
                    return attribute.stickerset;
                }
            }
        }
        return null;
    }

    private static long calcStickersHash(ArrayList<TLRPC.TL_messages_stickerSet> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            TLRPC.StickerSet set = sets.get(a).set;
            if (!set.archived) {
                acc = calcHash(acc, set.hash);
            }
        }
        return acc;
    }

    private void processLoadedStickers(final int type, final ArrayList<TLRPC.TL_messages_stickerSet> res, final boolean cache, final int date, final long hash) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda88
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m524x9add8e20(type);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m528xa68f339c(cache, res, date, hash, type);
            }
        });
    }

    /* renamed from: lambda$processLoadedStickers$71$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m524x9add8e20(int type) {
        this.loadingStickers[type] = false;
        this.stickersLoaded[type] = true;
        Runnable[] runnableArr = this.scheduledLoadStickers;
        if (runnableArr[type] != null) {
            runnableArr[type].run();
            this.scheduledLoadStickers[type] = null;
        }
    }

    /* renamed from: lambda$processLoadedStickers$75$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m528xa68f339c(boolean cache, final ArrayList res, final int date, final long hash, final int type) {
        TLRPC.TL_messages_stickerSet stickerSet;
        TLRPC.TL_messages_stickerSet stickerSet2;
        MediaDataController mediaDataController = this;
        ArrayList arrayList = res;
        long j = 1000;
        if ((cache && (arrayList == null || BuildVars.DEBUG_PRIVATE_VERSION || Math.abs((System.currentTimeMillis() / 1000) - date) >= 3600)) || (!cache && arrayList == null && hash == 0)) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda144
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m525x5dc9f77f(res, hash, type);
                }
            };
            if (arrayList != null || cache) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(runnable, j);
            if (arrayList == null) {
                return;
            }
        }
        if (arrayList != null) {
            try {
                final ArrayList<TLRPC.TL_messages_stickerSet> stickerSetsNew = new ArrayList<>();
                final LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsByIdNew = new LongSparseArray<>();
                final HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByNameNew = new HashMap<>();
                final LongSparseArray<String> stickersByEmojiNew = new LongSparseArray<>();
                final LongSparseArray<TLRPC.Document> stickersByIdNew = new LongSparseArray<>();
                final HashMap<String, ArrayList<TLRPC.Document>> allStickersNew = new HashMap<>();
                int a = 0;
                while (a < res.size()) {
                    TLRPC.TL_messages_stickerSet stickerSet3 = (TLRPC.TL_messages_stickerSet) arrayList.get(a);
                    if (stickerSet3 != null && mediaDataController.removingStickerSetsUndos.indexOfKey(stickerSet3.set.id) < 0) {
                        stickerSetsNew.add(stickerSet3);
                        stickerSetsByIdNew.put(stickerSet3.set.id, stickerSet3);
                        stickerSetsByNameNew.put(stickerSet3.set.short_name, stickerSet3);
                        for (int b = 0; b < stickerSet3.documents.size(); b++) {
                            TLRPC.Document document = stickerSet3.documents.get(b);
                            if (document != null && !(document instanceof TLRPC.TL_documentEmpty)) {
                                stickersByIdNew.put(document.id, document);
                            }
                        }
                        if (!stickerSet3.set.archived) {
                            int b2 = 0;
                            while (b2 < stickerSet3.packs.size()) {
                                TLRPC.TL_stickerPack stickerPack = stickerSet3.packs.get(b2);
                                if (stickerPack == null) {
                                    stickerSet = stickerSet3;
                                } else if (stickerPack.emoticon == null) {
                                    stickerSet = stickerSet3;
                                } else {
                                    stickerPack.emoticon = stickerPack.emoticon.replace("️", "");
                                    ArrayList<TLRPC.Document> arrayList2 = allStickersNew.get(stickerPack.emoticon);
                                    if (arrayList2 == null) {
                                        arrayList2 = new ArrayList<>();
                                        allStickersNew.put(stickerPack.emoticon, arrayList2);
                                    }
                                    int c = 0;
                                    while (c < stickerPack.documents.size()) {
                                        Long id = stickerPack.documents.get(c);
                                        if (stickersByEmojiNew.indexOfKey(id.longValue()) >= 0) {
                                            stickerSet2 = stickerSet3;
                                        } else {
                                            stickerSet2 = stickerSet3;
                                            stickersByEmojiNew.put(id.longValue(), stickerPack.emoticon);
                                        }
                                        TLRPC.Document sticker = stickersByIdNew.get(id.longValue());
                                        if (sticker != null) {
                                            arrayList2.add(sticker);
                                        }
                                        c++;
                                        stickerSet3 = stickerSet2;
                                    }
                                    stickerSet = stickerSet3;
                                }
                                b2++;
                                stickerSet3 = stickerSet;
                            }
                        }
                    }
                    a++;
                    mediaDataController = this;
                    arrayList = res;
                }
                if (!cache) {
                    putStickersToCache(type, stickerSetsNew, date, hash);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda91
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m526x20b660de(type, stickerSetsByIdNew, stickerSetsByNameNew, stickerSetsNew, hash, date, stickersByIdNew, allStickersNew, stickersByEmojiNew);
                    }
                });
            } catch (Throwable e) {
                FileLog.e(e);
            }
        } else if (!cache) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda89
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m527xe3a2ca3d(type, date);
                }
            });
            putStickersToCache(type, null, date, 0L);
        }
    }

    /* renamed from: lambda$processLoadedStickers$72$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m525x5dc9f77f(ArrayList res, long hash, int type) {
        if (res != null && hash != 0) {
            this.loadHash[type] = hash;
        }
        loadStickers(type, false, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$processLoadedStickers$73$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m526x20b660de(int type, LongSparseArray stickerSetsByIdNew, HashMap stickerSetsByNameNew, ArrayList stickerSetsNew, long hash, int date, LongSparseArray stickersByIdNew, HashMap allStickersNew, LongSparseArray stickersByEmojiNew) {
        for (int a = 0; a < this.stickerSets[type].size(); a++) {
            TLRPC.StickerSet set = this.stickerSets[type].get(a).set;
            this.stickerSetsById.remove(set.id);
            this.stickerSetsByName.remove(set.short_name);
            if (type != 3 && type != 4) {
                this.installedStickerSetsById.remove(set.id);
            }
        }
        for (int a2 = 0; a2 < stickerSetsByIdNew.size(); a2++) {
            this.stickerSetsById.put(stickerSetsByIdNew.keyAt(a2), (TLRPC.TL_messages_stickerSet) stickerSetsByIdNew.valueAt(a2));
            if (type != 3 && type != 4) {
                this.installedStickerSetsById.put(stickerSetsByIdNew.keyAt(a2), (TLRPC.TL_messages_stickerSet) stickerSetsByIdNew.valueAt(a2));
            }
        }
        this.stickerSetsByName.putAll(stickerSetsByNameNew);
        this.stickerSets[type] = stickerSetsNew;
        this.loadHash[type] = hash;
        this.loadDate[type] = date;
        this.stickersByIds[type] = stickersByIdNew;
        if (type == 0) {
            this.allStickers = allStickersNew;
            this.stickersByEmoji = stickersByEmojiNew;
        } else if (type == 3) {
            this.allStickersFeatured = allStickersNew;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
    }

    /* renamed from: lambda$processLoadedStickers$74$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m527xe3a2ca3d(int type, int date) {
        this.loadDate[type] = date;
    }

    public boolean cancelRemovingStickerSet(long id) {
        Runnable undoAction = this.removingStickerSetsUndos.get(id);
        if (undoAction != null) {
            undoAction.run();
            return true;
        }
        return false;
    }

    public void preloadStickerSetThumb(TLRPC.TL_messages_stickerSet stickerSet) {
        ArrayList<TLRPC.Document> documents;
        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(stickerSet.set.thumbs, 90);
        if (thumb != null && (documents = stickerSet.documents) != null && !documents.isEmpty()) {
            loadStickerSetThumbInternal(thumb, stickerSet, documents.get(0), stickerSet.set.thumb_version);
        }
    }

    public void preloadStickerSetThumb(TLRPC.StickerSetCovered stickerSet) {
        TLRPC.Document sticker;
        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(stickerSet.set.thumbs, 90);
        if (thumb != null) {
            if (stickerSet.cover != null) {
                sticker = stickerSet.cover;
            } else if (!stickerSet.covers.isEmpty()) {
                sticker = stickerSet.covers.get(0);
            } else {
                return;
            }
            loadStickerSetThumbInternal(thumb, stickerSet, sticker, stickerSet.set.thumb_version);
        }
    }

    private void loadStickerSetThumbInternal(TLRPC.PhotoSize thumb, Object parentObject, TLRPC.Document sticker, int thumbVersion) {
        ImageLocation imageLocation = ImageLocation.getForSticker(thumb, sticker, thumbVersion);
        if (imageLocation != null) {
            String ext = imageLocation.imageType == 1 ? "tgs" : "webp";
            getFileLoader().loadFile(imageLocation, parentObject, ext, 2, 1);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [int, boolean] */
    public void toggleStickerSet(final Context context, final TLObject stickerSetObject, final int toggle, final BaseFragment baseFragment, final boolean showSettings, boolean showTooltip) {
        TLRPC.TL_messages_stickerSet messages_stickerSet;
        TLRPC.StickerSet stickerSet;
        int currentIndex;
        TLRPC.StickerSet stickerSet2;
        if (stickerSetObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet messages_stickerSet2 = (TLRPC.TL_messages_stickerSet) stickerSetObject;
            messages_stickerSet = messages_stickerSet2;
            stickerSet = messages_stickerSet2.set;
        } else if (!(stickerSetObject instanceof TLRPC.StickerSetCovered)) {
            throw new IllegalArgumentException("Invalid type of the given stickerSetObject: " + stickerSetObject.getClass());
        } else {
            TLRPC.StickerSet stickerSet3 = ((TLRPC.StickerSetCovered) stickerSetObject).set;
            if (toggle != 2) {
                TLRPC.TL_messages_stickerSet messages_stickerSet3 = this.stickerSetsById.get(stickerSet3.id);
                if (messages_stickerSet3 != null) {
                    messages_stickerSet = messages_stickerSet3;
                    stickerSet = stickerSet3;
                } else {
                    return;
                }
            } else {
                messages_stickerSet = null;
                stickerSet = stickerSet3;
            }
        }
        final ?? r0 = stickerSet.masks;
        stickerSet.archived = toggle == 1;
        int a = 0;
        while (true) {
            ArrayList<TLRPC.TL_messages_stickerSet>[] arrayListArr = this.stickerSets;
            int type = r0 == true ? 1 : 0;
            if (a >= arrayListArr[type].size()) {
                currentIndex = 0;
                break;
            }
            TLRPC.TL_messages_stickerSet set = this.stickerSets[r0].get(a);
            if (set.set.id != stickerSet.id) {
                a++;
            } else {
                int currentIndex2 = a;
                this.stickerSets[r0].remove(a);
                if (toggle == 2) {
                    this.stickerSets[r0].add(0, set);
                } else {
                    this.stickerSetsById.remove(set.set.id);
                    this.installedStickerSetsById.remove(set.set.id);
                    this.stickerSetsByName.remove(set.set.short_name);
                }
                currentIndex = currentIndex2;
            }
        }
        this.loadHash[r0] = calcStickersHash(this.stickerSets[r0]);
        putStickersToCache(r0, this.stickerSets[r0], this.loadDate[r0], this.loadHash[r0]);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.stickersDidLoad;
        int type2 = r0 == true ? 1 : 0;
        notificationCenter.postNotificationName(i, Integer.valueOf(type2));
        if (toggle == 2) {
            if (!cancelRemovingStickerSet(stickerSet.id)) {
                toggleStickerSetInternal(context, toggle, baseFragment, showSettings, stickerSetObject, stickerSet, r0, showTooltip);
                return;
            }
            return;
        }
        if (!showTooltip) {
            stickerSet2 = stickerSet;
        } else if (baseFragment != null) {
            StickerSetBulletinLayout bulletinLayout = new StickerSetBulletinLayout(context, stickerSetObject, toggle);
            final int finalCurrentIndex = currentIndex;
            final TLRPC.StickerSet stickerSet4 = stickerSet;
            final TLRPC.TL_messages_stickerSet tL_messages_stickerSet = messages_stickerSet;
            final TLRPC.StickerSet stickerSet5 = stickerSet;
            final Bulletin.UndoButton undoButton = new Bulletin.UndoButton(context, false).setUndoAction(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda168
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m558xd5e9dfa4(stickerSet4, r0, finalCurrentIndex, tL_messages_stickerSet);
                }
            }).setDelayedAction(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda122
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m559x98d64903(context, toggle, baseFragment, showSettings, stickerSetObject, stickerSet5, r0);
                }
            });
            bulletinLayout.setButton(undoButton);
            LongSparseArray<Runnable> longSparseArray = this.removingStickerSetsUndos;
            long j = stickerSet5.id;
            undoButton.getClass();
            longSparseArray.put(j, new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    Bulletin.UndoButton.this.undo();
                }
            });
            Bulletin.make(baseFragment, bulletinLayout, (int) Bulletin.DURATION_LONG).show();
            return;
        } else {
            stickerSet2 = stickerSet;
        }
        toggleStickerSetInternal(context, toggle, baseFragment, showSettings, stickerSetObject, stickerSet2, r0, false);
    }

    /* renamed from: lambda$toggleStickerSet$76$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m558xd5e9dfa4(TLRPC.StickerSet stickerSet, int type, int finalCurrentIndex, TLRPC.TL_messages_stickerSet messages_stickerSet) {
        stickerSet.archived = false;
        this.stickerSets[type].add(finalCurrentIndex, messages_stickerSet);
        this.stickerSetsById.put(stickerSet.id, messages_stickerSet);
        this.installedStickerSetsById.put(stickerSet.id, messages_stickerSet);
        this.stickerSetsByName.put(stickerSet.short_name, messages_stickerSet);
        this.removingStickerSetsUndos.remove(stickerSet.id);
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
    }

    /* renamed from: lambda$toggleStickerSet$77$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m559x98d64903(Context context, int toggle, BaseFragment baseFragment, boolean showSettings, TLObject stickerSetObject, TLRPC.StickerSet stickerSet, int type) {
        toggleStickerSetInternal(context, toggle, baseFragment, showSettings, stickerSetObject, stickerSet, type, false);
    }

    private void toggleStickerSetInternal(final Context context, int toggle, final BaseFragment baseFragment, final boolean showSettings, final TLObject stickerSetObject, final TLRPC.StickerSet stickerSet, final int type, final boolean showTooltip) {
        TLRPC.TL_inputStickerSetID stickerSetID = new TLRPC.TL_inputStickerSetID();
        stickerSetID.access_hash = stickerSet.access_hash;
        stickerSetID.id = stickerSet.id;
        if (toggle != 0) {
            TLRPC.TL_messages_installStickerSet req = new TLRPC.TL_messages_installStickerSet();
            req.stickerset = stickerSetID;
            boolean z = true;
            if (toggle != 1) {
                z = false;
            }
            req.archived = z;
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda74
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.m561xdb8b55de(stickerSet, baseFragment, showSettings, type, showTooltip, context, stickerSetObject, tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_messages_uninstallStickerSet req2 = new TLRPC.TL_messages_uninstallStickerSet();
        req2.stickerset = stickerSetID;
        getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda73
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m563x5ec8cd67(stickerSet, type, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$toggleStickerSetInternal$79$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m561xdb8b55de(final TLRPC.StickerSet stickerSet, final BaseFragment baseFragment, final boolean showSettings, final int type, final boolean showTooltip, final Context context, final TLObject stickerSetObject, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda169
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m560x189eec7f(stickerSet, response, baseFragment, showSettings, type, error, showTooltip, context, stickerSetObject);
            }
        });
    }

    /* renamed from: lambda$toggleStickerSetInternal$78$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m560x189eec7f(TLRPC.StickerSet stickerSet, TLObject response, BaseFragment baseFragment, boolean showSettings, int type, TLRPC.TL_error error, boolean showTooltip, Context context, TLObject stickerSetObject) {
        this.removingStickerSetsUndos.remove(stickerSet.id);
        if (response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
            processStickerSetInstallResultArchive(baseFragment, showSettings, type, (TLRPC.TL_messages_stickerSetInstallResultArchive) response);
        }
        loadStickers(type, false, false, true);
        if (error == null && showTooltip && baseFragment != null) {
            Bulletin.make(baseFragment, new StickerSetBulletinLayout(context, stickerSetObject, 2), 1500).show();
        }
    }

    /* renamed from: lambda$toggleStickerSetInternal$81$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m563x5ec8cd67(final TLRPC.StickerSet stickerSet, final int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda167
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m562x9bdc6408(stickerSet, type);
            }
        });
    }

    /* renamed from: lambda$toggleStickerSetInternal$80$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m562x9bdc6408(TLRPC.StickerSet stickerSet, int type) {
        this.removingStickerSetsUndos.remove(stickerSet.id);
        loadStickers(type, false, true);
    }

    public void toggleStickerSets(ArrayList<TLRPC.StickerSet> stickerSetList, final int type, final int toggle, final BaseFragment baseFragment, final boolean showSettings) {
        int stickerSetListSize = stickerSetList.size();
        ArrayList<TLRPC.InputStickerSet> inputStickerSets = new ArrayList<>(stickerSetListSize);
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= stickerSetListSize) {
                break;
            }
            TLRPC.StickerSet stickerSet = stickerSetList.get(i);
            TLRPC.InputStickerSet inputStickerSet = new TLRPC.TL_inputStickerSetID();
            inputStickerSet.access_hash = stickerSet.access_hash;
            inputStickerSet.id = stickerSet.id;
            inputStickerSets.add(inputStickerSet);
            if (toggle != 0) {
                if (toggle != 1) {
                    z = false;
                }
                stickerSet.archived = z;
            }
            int a = 0;
            int size = this.stickerSets[type].size();
            while (true) {
                if (a < size) {
                    TLRPC.TL_messages_stickerSet set = this.stickerSets[type].get(a);
                    if (set.set.id != inputStickerSet.id) {
                        a++;
                    } else {
                        this.stickerSets[type].remove(a);
                        if (toggle == 2) {
                            this.stickerSets[type].add(0, set);
                        } else {
                            this.stickerSetsById.remove(set.set.id);
                            this.installedStickerSetsById.remove(set.set.id);
                            this.stickerSetsByName.remove(set.set.short_name);
                        }
                    }
                }
            }
            i++;
        }
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
        TLRPC.TL_messages_toggleStickerSets req = new TLRPC.TL_messages_toggleStickerSets();
        req.stickersets = inputStickerSets;
        switch (toggle) {
            case 0:
                req.uninstall = true;
                break;
            case 1:
                req.archive = true;
                break;
            case 2:
                req.unarchive = true;
                break;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda53
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m565xdd79a5cf(toggle, baseFragment, showSettings, type, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$toggleStickerSets$83$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m565xdd79a5cf(final int toggle, final BaseFragment baseFragment, final boolean showSettings, final int type, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda93
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m564x1a8d3c70(toggle, response, baseFragment, showSettings, type);
            }
        });
    }

    /* renamed from: lambda$toggleStickerSets$82$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m564x1a8d3c70(int toggle, TLObject response, BaseFragment baseFragment, boolean showSettings, int type) {
        if (toggle != 0) {
            if (response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
                processStickerSetInstallResultArchive(baseFragment, showSettings, type, (TLRPC.TL_messages_stickerSetInstallResultArchive) response);
            }
            loadStickers(type, false, false, true);
            return;
        }
        loadStickers(type, false, true);
    }

    public void processStickerSetInstallResultArchive(BaseFragment baseFragment, boolean showSettings, int type, TLRPC.TL_messages_stickerSetInstallResultArchive response) {
        int size = response.sets.size();
        for (int i = 0; i < size; i++) {
            this.installedStickerSetsById.remove(response.sets.get(i).set.id);
        }
        loadArchivedStickersCount(type, false);
        getNotificationCenter().postNotificationName(NotificationCenter.needAddArchivedStickers, response.sets);
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            StickersArchiveAlert alert = new StickersArchiveAlert(baseFragment.getParentActivity(), showSettings ? baseFragment : null, response.sets);
            baseFragment.showDialog(alert.create());
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x0016, code lost:
        if (r1[1] != false) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int getMask() {
        /*
            r4 = this;
            r0 = 0
            int r1 = r4.lastReturnedNum
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r4.searchResultMessages
            int r2 = r2.size()
            r3 = 1
            int r2 = r2 - r3
            if (r1 < r2) goto L18
            boolean[] r1 = r4.messagesSearchEndReached
            r2 = 0
            boolean r2 = r1[r2]
            if (r2 == 0) goto L18
            boolean r1 = r1[r3]
            if (r1 != 0) goto L1a
        L18:
            r0 = r0 | 1
        L1a:
            int r1 = r4.lastReturnedNum
            if (r1 <= 0) goto L20
            r0 = r0 | 2
        L20:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getMask():int");
    }

    public ArrayList<MessageObject> getFoundMessageObjects() {
        return this.searchResultMessages;
    }

    public void clearFoundMessageObjects() {
        this.searchResultMessages.clear();
    }

    public boolean isMessageFound(int messageId, boolean mergeDialog) {
        return this.searchResultMessagesMap[mergeDialog ? 1 : 0].indexOfKey(messageId) >= 0;
    }

    public void searchMessagesInChat(String query, long dialogId, long mergeDialogId, int guid, int direction, int replyMessageId, TLRPC.User user, TLRPC.Chat chat) {
        searchMessagesInChat(query, dialogId, mergeDialogId, guid, direction, replyMessageId, false, user, chat, true);
    }

    public void jumpToSearchedMessage(int guid, int index) {
        if (index < 0 || index >= this.searchResultMessages.size()) {
            return;
        }
        this.lastReturnedNum = index;
        MessageObject messageObject = this.searchResultMessages.get(index);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatSearchResultsAvailable;
        int[] iArr = this.messagesSearchCount;
        notificationCenter.postNotificationName(i, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), true);
    }

    public void loadMoreSearchMessages() {
        if (!this.loadingMoreSearchMessages) {
            boolean[] zArr = this.messagesSearchEndReached;
            if (zArr[0] && this.lastMergeDialogId == 0 && zArr[1]) {
                return;
            }
            int temp = this.searchResultMessages.size();
            this.lastReturnedNum = this.searchResultMessages.size();
            searchMessagesInChat(null, this.lastDialogId, this.lastMergeDialogId, this.lastGuid, 1, this.lastReplyMessageId, false, this.lastSearchUser, this.lastSearchChat, false);
            this.lastReturnedNum = temp;
            this.loadingMoreSearchMessages = true;
        }
    }

    private void searchMessagesInChat(String query, final long dialogId, final long mergeDialogId, final int guid, final int direction, final int replyMessageId, boolean internal, final TLRPC.User user, final TLRPC.Chat chat, final boolean jumpToMessage) {
        boolean firstQuery;
        int max_id;
        String query2;
        char c;
        long queryWithDialog;
        int max_id2;
        String query3;
        long queryWithDialog2;
        int i;
        long queryWithDialog3 = dialogId;
        boolean firstQuery2 = !internal;
        if (this.reqId != 0) {
            getConnectionsManager().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            getConnectionsManager().cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        if (query == null) {
            if (this.searchResultMessages.isEmpty()) {
                return;
            }
            if (direction != 1) {
                if (direction == 2) {
                    int i2 = this.lastReturnedNum - 1;
                    this.lastReturnedNum = i2;
                    if (i2 < 0) {
                        this.lastReturnedNum = 0;
                        return;
                    }
                    if (i2 >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    MessageObject messageObject = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i3 = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i3, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), Boolean.valueOf(jumpToMessage));
                    return;
                }
                return;
            }
            int i4 = this.lastReturnedNum + 1;
            this.lastReturnedNum = i4;
            if (i4 < this.searchResultMessages.size()) {
                MessageObject messageObject2 = this.searchResultMessages.get(this.lastReturnedNum);
                NotificationCenter notificationCenter2 = getNotificationCenter();
                int i5 = NotificationCenter.chatSearchResultsAvailable;
                int[] iArr2 = this.messagesSearchCount;
                notificationCenter2.postNotificationName(i5, Integer.valueOf(guid), Integer.valueOf(messageObject2.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject2.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr2[0] + iArr2[1]), Boolean.valueOf(jumpToMessage));
                return;
            }
            boolean[] zArr = this.messagesSearchEndReached;
            if (zArr[0] && mergeDialogId == 0 && zArr[1]) {
                this.lastReturnedNum--;
                return;
            }
            String query4 = this.lastSearchQuery;
            ArrayList<MessageObject> arrayList = this.searchResultMessages;
            MessageObject messageObject3 = arrayList.get(arrayList.size() - 1);
            if (messageObject3.getDialogId() == dialogId && !this.messagesSearchEndReached[0]) {
                max_id = messageObject3.getId();
                queryWithDialog3 = dialogId;
            } else {
                if (messageObject3.getDialogId() != mergeDialogId) {
                    max_id = 0;
                } else {
                    max_id = messageObject3.getId();
                }
                queryWithDialog3 = mergeDialogId;
                this.messagesSearchEndReached[1] = false;
            }
            query2 = query4;
            firstQuery = false;
            c = 0;
        } else {
            if (!firstQuery2) {
                c = 0;
            } else {
                boolean[] zArr2 = this.messagesSearchEndReached;
                c = 0;
                zArr2[1] = false;
                zArr2[0] = false;
                int[] iArr3 = this.messagesSearchCount;
                iArr3[1] = 0;
                iArr3[0] = 0;
                this.searchResultMessages.clear();
                this.searchResultMessagesMap[0].clear();
                this.searchResultMessagesMap[1].clear();
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(guid));
            }
            query2 = query;
            firstQuery = firstQuery2;
            max_id = 0;
        }
        boolean[] zArr3 = this.messagesSearchEndReached;
        if (zArr3[c] && !zArr3[1] && mergeDialogId != 0) {
            queryWithDialog = mergeDialogId;
        } else {
            queryWithDialog = queryWithDialog3;
        }
        if (queryWithDialog != dialogId || !firstQuery) {
            queryWithDialog2 = queryWithDialog;
            query3 = query2;
            max_id2 = max_id;
            i = 2;
        } else if (mergeDialogId != 0) {
            TLRPC.InputPeer inputPeer = getMessagesController().getInputPeer(mergeDialogId);
            if (inputPeer == null) {
                return;
            }
            final TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.peer = inputPeer;
            this.lastMergeDialogId = mergeDialogId;
            req.limit = 1;
            req.q = query2;
            if (user != null) {
                req.from_id = MessagesController.getInputPeer(user);
                req.flags = 1 | req.flags;
            } else if (chat != null) {
                req.from_id = MessagesController.getInputPeer(chat);
                req.flags = 1 | req.flags;
            }
            req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            this.mergeReqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda64
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.m555x76cb163e(mergeDialogId, req, dialogId, guid, direction, replyMessageId, user, chat, jumpToMessage, tLObject, tL_error);
                }
            }, 2);
            return;
        } else {
            queryWithDialog2 = queryWithDialog;
            query3 = query2;
            max_id2 = max_id;
            i = 2;
            this.lastMergeDialogId = 0L;
            zArr3[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        final TLRPC.TL_messages_search req2 = new TLRPC.TL_messages_search();
        final long queryWithDialog4 = queryWithDialog2;
        req2.peer = getMessagesController().getInputPeer(queryWithDialog4);
        if (req2.peer != null) {
            this.lastGuid = guid;
            this.lastDialogId = dialogId;
            this.lastSearchUser = user;
            this.lastSearchChat = chat;
            this.lastReplyMessageId = replyMessageId;
            req2.limit = 21;
            final String query5 = query3;
            req2.q = query5 != null ? query5 : "";
            req2.offset_id = max_id2;
            if (user == null) {
                if (chat != null) {
                    req2.from_id = MessagesController.getInputPeer(chat);
                    req2.flags |= 1;
                }
            } else {
                req2.from_id = MessagesController.getInputPeer(user);
                req2.flags |= 1;
            }
            int i6 = this.lastReplyMessageId;
            if (i6 != 0) {
                req2.top_msg_id = i6;
                req2.flags |= i;
            }
            req2.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            final int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.lastSearchQuery = query5;
            this.reqId = getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda69
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.m557xfca3e8fc(query5, currentReqId, jumpToMessage, req2, queryWithDialog4, dialogId, guid, mergeDialogId, replyMessageId, user, chat, tLObject, tL_error);
                }
            }, 2);
        }
    }

    /* renamed from: lambda$searchMessagesInChat$85$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m555x76cb163e(final long mergeDialogId, final TLRPC.TL_messages_search req, final long dialogId, final int guid, final int direction, final int replyMessageId, final TLRPC.User user, final TLRPC.Chat chat, final boolean jumpToMessage, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda115
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m554xb3deacdf(mergeDialogId, response, req, dialogId, guid, direction, replyMessageId, user, chat, jumpToMessage);
            }
        });
    }

    /* renamed from: lambda$searchMessagesInChat$84$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m554xb3deacdf(long mergeDialogId, TLObject response, TLRPC.TL_messages_search req, long dialogId, int guid, int direction, int replyMessageId, TLRPC.User user, TLRPC.Chat chat, boolean jumpToMessage) {
        if (this.lastMergeDialogId == mergeDialogId) {
            this.mergeReqId = 0;
            if (response == null) {
                this.messagesSearchEndReached[1] = true;
                this.messagesSearchCount[1] = 0;
                searchMessagesInChat(req.q, dialogId, mergeDialogId, guid, direction, replyMessageId, true, user, chat, jumpToMessage);
                return;
            }
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            this.messagesSearchEndReached[1] = res.messages.isEmpty();
            this.messagesSearchCount[1] = res instanceof TLRPC.TL_messages_messagesSlice ? res.count : res.messages.size();
            searchMessagesInChat(req.q, dialogId, mergeDialogId, guid, direction, replyMessageId, true, user, chat, jumpToMessage);
        }
    }

    /* renamed from: lambda$searchMessagesInChat$87$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m557xfca3e8fc(String finalQuery, final int currentReqId, final boolean jumpToMessage, final TLRPC.TL_messages_search req, final long queryWithDialogFinal, final long dialogId, final int guid, final long mergeDialogId, final int replyMessageId, final TLRPC.User user, final TLRPC.Chat chat, final TLObject response, TLRPC.TL_error error) {
        final ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            int N = Math.min(res.messages.size(), 20);
            for (int a = 0; a < N; a++) {
                TLRPC.Message message = res.messages.get(a);
                MessageObject messageObject = new MessageObject(this.currentAccount, message, false, false);
                messageObject.setQuery(finalQuery);
                messageObjects.add(messageObject);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda96
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m556x39b77f9d(currentReqId, jumpToMessage, response, req, queryWithDialogFinal, dialogId, guid, messageObjects, mergeDialogId, replyMessageId, user, chat);
            }
        });
    }

    /* renamed from: lambda$searchMessagesInChat$86$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m556x39b77f9d(int currentReqId, boolean jumpToMessage, TLObject response, TLRPC.TL_messages_search req, long queryWithDialogFinal, long dialogId, int guid, ArrayList messageObjects, long mergeDialogId, int replyMessageId, TLRPC.User user, TLRPC.Chat chat) {
        if (currentReqId == this.lastReqId) {
            this.reqId = 0;
            if (!jumpToMessage) {
                this.loadingMoreSearchMessages = false;
            }
            if (response != null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                int a = 0;
                while (a < res.messages.size()) {
                    TLRPC.Message message = res.messages.get(a);
                    if ((message instanceof TLRPC.TL_messageEmpty) || (message.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                        res.messages.remove(a);
                        a--;
                    }
                    a++;
                }
                getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                getMessagesController().putUsers(res.users, false);
                getMessagesController().putChats(res.chats, false);
                if (req.offset_id == 0 && queryWithDialogFinal == dialogId) {
                    this.lastReturnedNum = 0;
                    this.searchResultMessages.clear();
                    this.searchResultMessagesMap[0].clear();
                    this.searchResultMessagesMap[1].clear();
                    this.messagesSearchCount[0] = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(guid));
                }
                int N = Math.min(res.messages.size(), 20);
                boolean added = false;
                for (int a2 = 0; a2 < N; a2++) {
                    res.messages.get(a2);
                    added = true;
                    MessageObject messageObject = (MessageObject) messageObjects.get(a2);
                    this.searchResultMessages.add(messageObject);
                    this.searchResultMessagesMap[queryWithDialogFinal == dialogId ? (char) 0 : (char) 1].put(messageObject.getId(), messageObject);
                }
                this.messagesSearchEndReached[queryWithDialogFinal == dialogId ? (char) 0 : (char) 1] = res.messages.size() < 21;
                this.messagesSearchCount[queryWithDialogFinal == dialogId ? (char) 0 : (char) 1] = ((res instanceof TLRPC.TL_messages_messagesSlice) || (res instanceof TLRPC.TL_messages_channelMessages)) ? res.count : res.messages.size();
                if (this.searchResultMessages.isEmpty()) {
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), 0, Integer.valueOf(getMask()), 0L, 0, 0, Boolean.valueOf(jumpToMessage));
                } else if (added) {
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    MessageObject messageObject2 = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i, Integer.valueOf(guid), Integer.valueOf(messageObject2.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject2.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), Boolean.valueOf(jumpToMessage));
                }
                if (queryWithDialogFinal == dialogId) {
                    boolean[] zArr = this.messagesSearchEndReached;
                    if (zArr[0] && mergeDialogId != 0 && !zArr[1]) {
                        searchMessagesInChat(this.lastSearchQuery, dialogId, mergeDialogId, guid, 0, replyMessageId, true, user, chat, jumpToMessage);
                    }
                }
            }
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x002a  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0085  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x008d  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00ea A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00eb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadMedia(final long r22, final int r24, final int r25, final int r26, final int r27, int r28, final int r29, final int r30) {
        /*
            Method dump skipped, instructions count: 302
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadMedia(long, int, int, int, int, int, int, int):void");
    }

    /* renamed from: lambda$loadMedia$88$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m473lambda$loadMedia$88$orgtelegrammessengerMediaDataController(long dialogId, int min_id, int count, int max_id, int type, int classGuid, boolean isChannel, int requestIndex, TLObject response, TLRPC.TL_error error) {
        boolean topReached;
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            getMessagesController().removeDeletedMessagesFromArray(dialogId, res.messages);
            boolean z = false;
            if (min_id != 0) {
                if (res.messages.size() <= 1) {
                    z = true;
                }
                topReached = z;
            } else {
                if (res.messages.size() == 0) {
                    z = true;
                }
                topReached = z;
            }
            processLoadedMedia(res, dialogId, count, max_id, min_id, type, 0, classGuid, isChannel, topReached, requestIndex);
        }
    }

    public void getMediaCounts(final long dialogId, final int classGuid) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda104
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m445x3af4ecc2(dialogId, classGuid);
            }
        });
    }

    /* renamed from: lambda$getMediaCounts$93$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m445x3af4ecc2(final long dialogId, int classGuid) {
        Exception e;
        try {
            final int[] counts = new int[8];
            int i = 0;
            counts[0] = -1;
            counts[1] = -1;
            counts[2] = -1;
            counts[3] = -1;
            counts[4] = -1;
            counts[5] = -1;
            counts[6] = -1;
            counts[7] = -1;
            final int[] countsFinal = new int[8];
            countsFinal[0] = -1;
            countsFinal[1] = -1;
            countsFinal[2] = -1;
            countsFinal[3] = -1;
            countsFinal[4] = -1;
            countsFinal[5] = -1;
            countsFinal[6] = -1;
            countsFinal[7] = -1;
            int[] old = new int[8];
            old[0] = 0;
            old[1] = 0;
            old[2] = 0;
            old[3] = 0;
            old[4] = 0;
            old[5] = 0;
            old[6] = 0;
            old[7] = 0;
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d", Long.valueOf(dialogId)), new Object[0]);
            while (cursor.next()) {
                int type = cursor.intValue(0);
                if (type >= 0 && type < 8) {
                    int intValue = cursor.intValue(1);
                    counts[type] = intValue;
                    countsFinal[type] = intValue;
                    old[type] = cursor.intValue(2);
                }
            }
            cursor.dispose();
            if (DialogObject.isEncryptedDialog(dialogId)) {
                for (int a = 0; a < counts.length; a++) {
                    if (counts[a] == -1) {
                        SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v4 WHERE uid = %d AND type = %d LIMIT 1", Long.valueOf(dialogId), Integer.valueOf(a)), new Object[0]);
                        if (cursor2.next()) {
                            counts[a] = cursor2.intValue(0);
                        } else {
                            counts[a] = 0;
                        }
                        cursor2.dispose();
                        putMediaCountDatabase(dialogId, a, counts[a]);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda118
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m441x31dea27b(dialogId, counts);
                    }
                });
                return;
            }
            boolean missing = false;
            TLRPC.TL_messages_getSearchCounters req = new TLRPC.TL_messages_getSearchCounters();
            req.peer = getMessagesController().getInputPeer(dialogId);
            int a2 = 0;
            while (a2 < counts.length) {
                if (req.peer == null) {
                    counts[a2] = i;
                } else if (counts[a2] == -1 || old[a2] == 1) {
                    if (a2 == 0) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterPhotoVideo());
                    } else if (a2 == 1) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterDocument());
                    } else if (a2 == 2) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterRoundVoice());
                    } else if (a2 == 3) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterUrl());
                    } else if (a2 == 4) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterMusic());
                    } else if (a2 == 6) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterPhotos());
                    } else if (a2 == 7) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterVideo());
                    } else {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterGif());
                    }
                    if (counts[a2] == -1) {
                        missing = true;
                    } else if (old[a2] == 1) {
                        counts[a2] = -1;
                    }
                }
                a2++;
                i = 0;
            }
            if (!req.filters.isEmpty()) {
                int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda78
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.m443xb51c1a04(counts, dialogId, tLObject, tL_error);
                    }
                });
                try {
                    getConnectionsManager().bindRequestToGuid(reqId, classGuid);
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    return;
                }
            }
            if (!missing) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda121
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m444x78088363(dialogId, countsFinal);
                    }
                });
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* renamed from: lambda$getMediaCounts$89$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m441x31dea27b(long dialogId, int[] counts) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(dialogId), counts);
    }

    /* renamed from: lambda$getMediaCounts$91$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m443xb51c1a04(final int[] counts, final long dialogId, TLObject response, TLRPC.TL_error error) {
        int type;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] < 0) {
                counts[i] = 0;
            }
        }
        if (response != null) {
            TLRPC.Vector res = (TLRPC.Vector) response;
            int N = res.objects.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_messages_searchCounter searchCounter = (TLRPC.TL_messages_searchCounter) res.objects.get(a);
                if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterPhotoVideo) {
                    type = 0;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterDocument) {
                    type = 1;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterRoundVoice) {
                    type = 2;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterUrl) {
                    type = 3;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterMusic) {
                    type = 4;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterGif) {
                    type = 5;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterPhotos) {
                    type = 6;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterVideo) {
                    type = 7;
                }
                counts[type] = searchCounter.count;
                putMediaCountDatabase(dialogId, type, counts[type]);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda119
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m442xf22fb0a5(dialogId, counts);
            }
        });
    }

    /* renamed from: lambda$getMediaCounts$90$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m442xf22fb0a5(long dialogId, int[] counts) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(dialogId), counts);
    }

    /* renamed from: lambda$getMediaCounts$92$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m444x78088363(long dialogId, int[] countsFinal) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(dialogId), countsFinal);
    }

    public void getMediaCount(final long dialogId, final int type, final int classGuid, boolean fromCache) {
        if (fromCache || DialogObject.isEncryptedDialog(dialogId)) {
            getMediaCountDatabase(dialogId, type, classGuid);
            return;
        }
        TLRPC.TL_messages_getSearchCounters req = new TLRPC.TL_messages_getSearchCounters();
        if (type == 0) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterPhotoVideo());
        } else if (type == 1) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterDocument());
        } else if (type == 2) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterRoundVoice());
        } else if (type == 3) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterUrl());
        } else if (type == 4) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterMusic());
        } else if (type == 5) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterGif());
        }
        req.peer = getMessagesController().getInputPeer(dialogId);
        if (req.peer == null) {
            return;
        }
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda57
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m439xfbcb736(dialogId, type, classGuid, tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(reqId, classGuid);
    }

    /* renamed from: lambda$getMediaCount$94$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m439xfbcb736(long dialogId, int type, int classGuid, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Vector res = (TLRPC.Vector) response;
            if (!res.objects.isEmpty()) {
                TLRPC.TL_messages_searchCounter counter = (TLRPC.TL_messages_searchCounter) res.objects.get(0);
                processLoadedMediaCount(counter.count, dialogId, type, classGuid, false, 0);
            }
        }
    }

    public static int getMediaType(TLRPC.Message message) {
        if (message == null) {
            return -1;
        }
        if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
            return 0;
        }
        if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.Document document = message.media.document;
            if (document == null) {
                return -1;
            }
            boolean isAnimated = false;
            boolean isVideo = false;
            boolean isVoice = false;
            boolean isMusic = false;
            boolean isSticker = false;
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                    isVoice = attribute.round_message;
                    isVideo = true ^ attribute.round_message;
                } else if (attribute instanceof TLRPC.TL_documentAttributeAnimated) {
                    isAnimated = true;
                } else if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    isVoice = attribute.voice;
                    isMusic = true ^ attribute.voice;
                } else if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                    isSticker = true;
                }
            }
            if (isVoice) {
                return 2;
            }
            if (isVideo && !isAnimated && !isSticker) {
                return 0;
            }
            if (isSticker) {
                return -1;
            }
            if (isAnimated) {
                return 5;
            }
            return isMusic ? 4 : 1;
        }
        if (!message.entities.isEmpty()) {
            for (int a2 = 0; a2 < message.entities.size(); a2++) {
                TLRPC.MessageEntity entity = message.entities.get(a2);
                if ((entity instanceof TLRPC.TL_messageEntityUrl) || (entity instanceof TLRPC.TL_messageEntityTextUrl) || (entity instanceof TLRPC.TL_messageEntityEmail)) {
                    return 3;
                }
            }
        }
        return -1;
    }

    public static boolean canAddMessageToMedia(TLRPC.Message message) {
        if (!(message instanceof TLRPC.TL_message_secret) || ((!(message.media instanceof TLRPC.TL_messageMediaPhoto) && !MessageObject.isVideoMessage(message) && !MessageObject.isGifMessage(message)) || message.media.ttl_seconds == 0 || message.media.ttl_seconds > 60)) {
            return ((message instanceof TLRPC.TL_message_secret) || !(message instanceof TLRPC.TL_message) || ((!(message.media instanceof TLRPC.TL_messageMediaPhoto) && !(message.media instanceof TLRPC.TL_messageMediaDocument)) || message.media.ttl_seconds == 0)) && getMediaType(message) != -1;
        }
        return false;
    }

    public void processLoadedMedia(final TLRPC.messages_Messages res, final long dialogId, int count, int max_id, final int min_id, final int type, final int fromCache, final int classGuid, boolean isChannel, final boolean topReached, final int requestIndex) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("process load media did " + dialogId + " count = " + count + " max_id=" + max_id + " min_id=" + min_id + " type = " + type + " cache = " + fromCache + " classGuid = " + classGuid);
        }
        if (fromCache != 0 && (((res.messages.isEmpty() && min_id == 0) || (res.messages.size() <= 1 && min_id != 0)) && !DialogObject.isEncryptedDialog(dialogId))) {
            if (fromCache == 2) {
                return;
            }
            loadMedia(dialogId, count, max_id, min_id, type, 0, classGuid, requestIndex);
            return;
        }
        if (fromCache == 0) {
            ImageLoader.saveMessagesThumbs(res.messages);
            getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            putMediaDatabase(dialogId, type, res.messages, max_id, min_id, topReached);
        }
        Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m518x42f9fdbd(res, fromCache, dialogId, classGuid, type, topReached, min_id, requestIndex);
            }
        });
    }

    /* renamed from: lambda$processLoadedMedia$96$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m518x42f9fdbd(final TLRPC.messages_Messages res, final int fromCache, final long dialogId, final int classGuid, final int type, final boolean topReached, final int min_id, final int requestIndex) {
        LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        for (int a = 0; a < res.users.size(); a++) {
            TLRPC.User u = res.users.get(a);
            usersDict.put(u.id, u);
        }
        final ArrayList<MessageObject> objects = new ArrayList<>();
        for (int a2 = 0; a2 < res.messages.size(); a2++) {
            TLRPC.Message message = res.messages.get(a2);
            MessageObject messageObject = new MessageObject(this.currentAccount, message, usersDict, true, false);
            messageObject.createStrippedThumb();
            objects.add(messageObject);
        }
        getFileLoader().checkMediaExistance(objects);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m517x800d945e(res, fromCache, dialogId, objects, classGuid, type, topReached, min_id, requestIndex);
            }
        });
    }

    /* renamed from: lambda$processLoadedMedia$95$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m517x800d945e(TLRPC.messages_Messages res, int fromCache, long dialogId, ArrayList objects, int classGuid, int type, boolean topReached, int min_id, int requestIndex) {
        int totalCount = res.count;
        boolean z = true;
        getMessagesController().putUsers(res.users, fromCache != 0);
        getMessagesController().putChats(res.chats, fromCache != 0);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.mediaDidLoad;
        Object[] objArr = new Object[8];
        objArr[0] = Long.valueOf(dialogId);
        objArr[1] = Integer.valueOf(totalCount);
        objArr[2] = objects;
        objArr[3] = Integer.valueOf(classGuid);
        objArr[4] = Integer.valueOf(type);
        objArr[5] = Boolean.valueOf(topReached);
        if (min_id == 0) {
            z = false;
        }
        objArr[6] = Boolean.valueOf(z);
        objArr[7] = Integer.valueOf(requestIndex);
        notificationCenter.postNotificationName(i, objArr);
    }

    private void processLoadedMediaCount(final int count, final long dialogId, final int type, final int classGuid, final boolean fromCache, final int old) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda117
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m519x90028d91(dialogId, fromCache, count, type, old, classGuid);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0020, code lost:
        if (r8 != false) goto L17;
     */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0033  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x006d  */
    /* renamed from: lambda$processLoadedMediaCount$97$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m519x90028d91(long r17, boolean r19, int r20, int r21, int r22, int r23) {
        /*
            r16 = this;
            r0 = r20
            r7 = r21
            boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r17)
            r9 = 2
            r10 = -1
            r11 = 1
            r12 = 0
            if (r19 == 0) goto L18
            if (r0 == r10) goto L14
            if (r0 != 0) goto L18
            if (r7 != r9) goto L18
        L14:
            if (r8 != 0) goto L18
            r1 = 1
            goto L19
        L18:
            r1 = 0
        L19:
            r13 = r1
            if (r13 != 0) goto L23
            r14 = r22
            if (r14 != r11) goto L31
            if (r8 != 0) goto L31
            goto L25
        L23:
            r14 = r22
        L25:
            r6 = 0
            r1 = r16
            r2 = r17
            r4 = r21
            r5 = r23
            r1.getMediaCount(r2, r4, r5, r6)
        L31:
            if (r13 != 0) goto L6d
            if (r19 != 0) goto L3d
            r1 = r16
            r2 = r17
            r1.putMediaCountDatabase(r2, r7, r0)
            goto L41
        L3d:
            r1 = r16
            r2 = r17
        L41:
            org.telegram.messenger.NotificationCenter r4 = r16.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Long r15 = java.lang.Long.valueOf(r17)
            r6[r12] = r15
            if (r19 == 0) goto L55
            if (r0 != r10) goto L55
            goto L56
        L55:
            r12 = r0
        L56:
            java.lang.Integer r10 = java.lang.Integer.valueOf(r12)
            r6[r11] = r10
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r19)
            r6[r9] = r10
            r9 = 3
            java.lang.Integer r10 = java.lang.Integer.valueOf(r21)
            r6[r9] = r10
            r4.postNotificationName(r5, r6)
            goto L71
        L6d:
            r1 = r16
            r2 = r17
        L71:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m519x90028d91(long, boolean, int, int, int, int):void");
    }

    private void putMediaCountDatabase(final long uid, final int type, final int count) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda107
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m535x449572c6(uid, type, count);
            }
        });
    }

    /* renamed from: lambda$putMediaCountDatabase$98$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m535x449572c6(long uid, int type, int count) {
        try {
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
            state2.requery();
            state2.bindLong(1, uid);
            state2.bindInteger(2, type);
            state2.bindInteger(3, count);
            state2.bindInteger(4, 0);
            state2.step();
            state2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void getMediaCountDatabase(final long dialogId, final int type, final int classGuid) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda106
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m440xaabc006c(dialogId, type, classGuid);
            }
        });
    }

    /* renamed from: lambda$getMediaCountDatabase$99$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m440xaabc006c(long dialogId, int type, int classGuid) {
        Exception e;
        int count = -1;
        int old = 0;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", Long.valueOf(dialogId), Integer.valueOf(type)), new Object[0]);
            if (cursor.next()) {
                count = cursor.intValue(0);
                old = cursor.intValue(1);
            }
            cursor.dispose();
            if (count == -1 && DialogObject.isEncryptedDialog(dialogId)) {
                SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v4 WHERE uid = %d AND type = %d LIMIT 1", Long.valueOf(dialogId), Integer.valueOf(type)), new Object[0]);
                if (cursor2.next()) {
                    count = cursor2.intValue(0);
                }
                cursor2.dispose();
                if (count != -1) {
                    try {
                        putMediaCountDatabase(dialogId, type, count);
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        return;
                    }
                }
            }
            processLoadedMediaCount(count, dialogId, type, classGuid, true, old);
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* renamed from: org.telegram.messenger.MediaDataController$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$fromCache;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ int val$min_id;
        final /* synthetic */ int val$requestIndex;
        final /* synthetic */ int val$type;
        final /* synthetic */ long val$uid;

        AnonymousClass1(int i, long j, int i2, int i3, int i4, int i5, int i6, boolean z, int i7) {
            MediaDataController.this = this$0;
            this.val$count = i;
            this.val$uid = j;
            this.val$min_id = i2;
            this.val$type = i3;
            this.val$max_id = i4;
            this.val$classGuid = i5;
            this.val$fromCache = i6;
            this.val$isChannel = z;
            this.val$requestIndex = i7;
        }

        @Override // java.lang.Runnable
        public void run() {
            TLRPC.TL_messages_messages res;
            Throwable th;
            SQLiteCursor cursor;
            SQLiteDatabase database;
            int holeMessageId;
            int holeMessageId2;
            int startHole;
            int mid;
            boolean topReached = false;
            TLRPC.TL_messages_messages res2 = new TLRPC.TL_messages_messages();
            try {
                ArrayList<Long> usersToLoad = new ArrayList<>();
                ArrayList<Long> chatsToLoad = new ArrayList<>();
                int countToLoad = this.val$count + 1;
                SQLiteDatabase database2 = MediaDataController.this.getMessagesStorage().getDatabase();
                boolean isEnd = false;
                boolean reverseMessages = false;
                if (!DialogObject.isEncryptedDialog(this.val$uid)) {
                    if (this.val$min_id == 0) {
                        database = database2;
                        SQLiteCursor cursor2 = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", Long.valueOf(this.val$uid), Integer.valueOf(this.val$type)), new Object[0]);
                        if (cursor2.next()) {
                            isEnd = cursor2.intValue(0) == 1;
                        } else {
                            cursor2.dispose();
                            cursor2 = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v4 WHERE uid = %d AND type = %d AND mid > 0", Long.valueOf(this.val$uid), Integer.valueOf(this.val$type)), new Object[0]);
                            if (cursor2.next() && (mid = cursor2.intValue(0)) != 0) {
                                SQLitePreparedStatement state = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                state.requery();
                                state.bindLong(1, this.val$uid);
                                state.bindInteger(2, this.val$type);
                                state.bindInteger(3, 0);
                                state.bindInteger(4, mid);
                                state.step();
                                state.dispose();
                            }
                        }
                        cursor2.dispose();
                    } else {
                        database = database2;
                    }
                    if (this.val$max_id != 0) {
                        SQLiteCursor cursor3 = database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND start <= %d ORDER BY end DESC LIMIT 1", Long.valueOf(this.val$uid), Integer.valueOf(this.val$type), Integer.valueOf(this.val$max_id)), new Object[0]);
                        if (cursor3.next()) {
                            startHole = cursor3.intValue(0);
                            holeMessageId2 = cursor3.intValue(1);
                        } else {
                            holeMessageId2 = 0;
                            startHole = 0;
                        }
                        cursor3.dispose();
                        if (holeMessageId2 > 1) {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$max_id), Integer.valueOf(holeMessageId2), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                            isEnd = false;
                        } else {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$max_id), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                        }
                    } else if (this.val$min_id != 0) {
                        int startHole2 = 0;
                        boolean isEnd2 = isEnd;
                        SQLiteCursor cursor4 = database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end >= %d ORDER BY end ASC LIMIT 1", Long.valueOf(this.val$uid), Integer.valueOf(this.val$type), Integer.valueOf(this.val$min_id)), new Object[0]);
                        if (cursor4.next()) {
                            startHole2 = cursor4.intValue(0);
                            holeMessageId = cursor4.intValue(1);
                        } else {
                            holeMessageId = 0;
                        }
                        cursor4.dispose();
                        reverseMessages = true;
                        if (startHole2 > 1) {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid >= %d AND mid <= %d AND type = %d ORDER BY date ASC, mid ASC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$min_id), Integer.valueOf(startHole2), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                            isEnd = isEnd2;
                        } else {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid >= %d AND type = %d ORDER BY date ASC, mid ASC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$min_id), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                            isEnd = true;
                        }
                    } else {
                        boolean isEnd3 = isEnd;
                        SQLiteCursor cursor5 = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$type)), new Object[0]);
                        int holeMessageId3 = cursor5.next() ? cursor5.intValue(0) : 0;
                        cursor5.dispose();
                        if (holeMessageId3 > 1) {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(holeMessageId3), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                            reverseMessages = false;
                            isEnd = isEnd3;
                        } else {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                            reverseMessages = false;
                            isEnd = isEnd3;
                        }
                    }
                } else {
                    isEnd = true;
                    if (this.val$max_id != 0) {
                        cursor = database2.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$max_id), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                        reverseMessages = false;
                    } else if (this.val$min_id != 0) {
                        cursor = database2.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d AND type = %d ORDER BY m.mid DESC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$min_id), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                        reverseMessages = false;
                    } else {
                        cursor = database2.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", Long.valueOf(this.val$uid), Integer.valueOf(this.val$type), Integer.valueOf(countToLoad)), new Object[0]);
                        reverseMessages = false;
                    }
                }
                while (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, MediaDataController.this.getUserConfig().clientUserId);
                        data.reuse();
                        message.id = cursor.intValue(1);
                        message.dialog_id = this.val$uid;
                        if (DialogObject.isEncryptedDialog(this.val$uid)) {
                            message.random_id = cursor.longValue(2);
                        }
                        if (reverseMessages) {
                            res2.messages.add(0, message);
                        } else {
                            res2.messages.add(message);
                        }
                        MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                    }
                }
                cursor.dispose();
                if (!usersToLoad.isEmpty()) {
                    MediaDataController.this.getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), res2.users);
                }
                if (!chatsToLoad.isEmpty()) {
                    MediaDataController.this.getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), res2.chats);
                }
                if (res2.messages.size() <= this.val$count || this.val$min_id != 0) {
                    topReached = this.val$min_id != 0 ? false : isEnd;
                } else {
                    res2.messages.remove(res2.messages.size() - 1);
                }
                final int i = this.val$classGuid;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.AnonymousClass1.this.m570lambda$run$0$orgtelegrammessengerMediaDataController$1(this, i);
                    }
                });
                MediaDataController.this.processLoadedMedia(res2, this.val$uid, this.val$count, this.val$max_id, this.val$min_id, this.val$type, this.val$fromCache, this.val$classGuid, this.val$isChannel, topReached, this.val$requestIndex);
            } catch (Exception e) {
                try {
                    res2.messages.clear();
                    res2.chats.clear();
                    res2.users.clear();
                    FileLog.e(e);
                    final int i2 = this.val$classGuid;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaDataController.AnonymousClass1.this.m570lambda$run$0$orgtelegrammessengerMediaDataController$1(this, i2);
                        }
                    });
                    MediaDataController.this.processLoadedMedia(res2, this.val$uid, this.val$count, this.val$max_id, this.val$min_id, this.val$type, this.val$fromCache, this.val$classGuid, this.val$isChannel, false, this.val$requestIndex);
                } catch (Throwable th2) {
                    res = res2;
                    th = th2;
                    final int i3 = this.val$classGuid;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaDataController.AnonymousClass1.this.m570lambda$run$0$orgtelegrammessengerMediaDataController$1(this, i3);
                        }
                    });
                    MediaDataController.this.processLoadedMedia(res, this.val$uid, this.val$count, this.val$max_id, this.val$min_id, this.val$type, this.val$fromCache, this.val$classGuid, this.val$isChannel, false, this.val$requestIndex);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                res = res2;
                final int i32 = this.val$classGuid;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.AnonymousClass1.this.m570lambda$run$0$orgtelegrammessengerMediaDataController$1(this, i32);
                    }
                });
                MediaDataController.this.processLoadedMedia(res, this.val$uid, this.val$count, this.val$max_id, this.val$min_id, this.val$type, this.val$fromCache, this.val$classGuid, this.val$isChannel, false, this.val$requestIndex);
                throw th;
            }
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-MediaDataController$1 */
        public /* synthetic */ void m570lambda$run$0$orgtelegrammessengerMediaDataController$1(Runnable task, int classGuid) {
            MediaDataController.this.getMessagesStorage().completeTaskForGuid(task, classGuid);
        }
    }

    private void loadMediaDatabase(long uid, int count, int max_id, int min_id, int type, int classGuid, boolean isChannel, int fromCache, int requestIndex) {
        Runnable runnable = new AnonymousClass1(count, uid, min_id, type, max_id, classGuid, fromCache, isChannel, requestIndex);
        MessagesStorage messagesStorage = getMessagesStorage();
        messagesStorage.getStorageQueue().postRunnable(runnable);
        messagesStorage.bindTaskToGuid(runnable, classGuid);
    }

    private void putMediaDatabase(final long uid, final int type, final ArrayList<TLRPC.Message> messages, final int max_id, final int min_id, final boolean topReached) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda92
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m536x3a2b6177(min_id, messages, topReached, uid, max_id, type);
            }
        });
    }

    /* renamed from: lambda$putMediaDatabase$100$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m536x3a2b6177(int min_id, ArrayList messages, boolean topReached, long uid, int max_id, int type) {
        if (min_id == 0) {
            try {
                if (messages.isEmpty() || topReached) {
                    getMessagesStorage().doneHolesInMedia(uid, max_id, type);
                    if (messages.isEmpty()) {
                        return;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        getMessagesStorage().getDatabase().beginTransaction();
        SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_v4 VALUES(?, ?, ?, ?, ?)");
        Iterator it = messages.iterator();
        while (it.hasNext()) {
            TLRPC.Message message = (TLRPC.Message) it.next();
            if (canAddMessageToMedia(message)) {
                state2.requery();
                NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                message.serializeToStream(data);
                state2.bindInteger(1, message.id);
                state2.bindLong(2, uid);
                state2.bindInteger(3, message.date);
                state2.bindInteger(4, type);
                state2.bindByteBuffer(5, data);
                state2.step();
                data.reuse();
            }
        }
        state2.dispose();
        if (!topReached || max_id != 0 || min_id != 0) {
            int minId = (!topReached || min_id != 0) ? ((TLRPC.Message) messages.get(messages.size() - 1)).id : 1;
            if (min_id != 0) {
                getMessagesStorage().closeHolesInMedia(uid, minId, ((TLRPC.Message) messages.get(0)).id, type);
            } else if (max_id != 0) {
                getMessagesStorage().closeHolesInMedia(uid, minId, max_id, type);
            } else {
                getMessagesStorage().closeHolesInMedia(uid, minId, Integer.MAX_VALUE, type);
            }
        }
        getMessagesStorage().getDatabase().commitTransaction();
    }

    public void loadMusic(final long dialogId, final long maxId, final long minId) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda111
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m475lambda$loadMusic$102$orgtelegrammessengerMediaDataController(dialogId, maxId, minId);
            }
        });
    }

    /* renamed from: lambda$loadMusic$102$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m475lambda$loadMusic$102$orgtelegrammessengerMediaDataController(final long dialogId, long maxId, long minId) {
        SQLiteCursor cursor;
        final ArrayList<MessageObject> arrayListBegin = new ArrayList<>();
        final ArrayList<MessageObject> arrayListEnd = new ArrayList<>();
        int a = 0;
        while (a < 2) {
            ArrayList<MessageObject> arrayList = a == 0 ? arrayListBegin : arrayListEnd;
            if (a == 0) {
                try {
                    cursor = !DialogObject.isEncryptedDialog(dialogId) ? getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", Long.valueOf(dialogId), Long.valueOf(maxId), 4), new Object[0]) : getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", Long.valueOf(dialogId), Long.valueOf(maxId), 4), new Object[0]);
                } catch (Exception e) {
                    e = e;
                    FileLog.e(e);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda114
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaDataController.this.m474lambda$loadMusic$101$orgtelegrammessengerMediaDataController(dialogId, arrayListBegin, arrayListEnd);
                        }
                    });
                }
            } else if (!DialogObject.isEncryptedDialog(dialogId)) {
                cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", Long.valueOf(dialogId), Long.valueOf(minId), 4), new Object[0]);
            } else {
                cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", Long.valueOf(dialogId), Long.valueOf(minId), 4), new Object[0]);
            }
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, getUserConfig().clientUserId);
                    data.reuse();
                    if (MessageObject.isMusicMessage(message)) {
                        message.id = cursor.intValue(1);
                        try {
                            message.dialog_id = dialogId;
                            try {
                                arrayList.add(0, new MessageObject(this.currentAccount, message, false, true));
                            } catch (Exception e2) {
                                e = e2;
                                FileLog.e(e);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda114
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MediaDataController.this.m474lambda$loadMusic$101$orgtelegrammessengerMediaDataController(dialogId, arrayListBegin, arrayListEnd);
                                    }
                                });
                            }
                        } catch (Exception e3) {
                            e = e3;
                        }
                    }
                }
            }
            cursor.dispose();
            a++;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda114
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m474lambda$loadMusic$101$orgtelegrammessengerMediaDataController(dialogId, arrayListBegin, arrayListEnd);
            }
        });
    }

    /* renamed from: lambda$loadMusic$101$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m474lambda$loadMusic$101$orgtelegrammessengerMediaDataController(long dialogId, ArrayList arrayListBegin, ArrayList arrayListEnd) {
        getNotificationCenter().postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(dialogId), arrayListBegin, arrayListEnd);
    }

    public void buildShortcuts() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        int maxShortcuts = ShortcutManagerCompat.getMaxShortcutCountPerActivity(ApplicationLoader.applicationContext) - 2;
        if (maxShortcuts <= 0) {
            maxShortcuts = 5;
        }
        final ArrayList<TLRPC.TL_topPeer> hintsFinal = new ArrayList<>();
        if (SharedConfig.passcodeHash.length() <= 0) {
            for (int a = 0; a < this.hints.size(); a++) {
                hintsFinal.add(this.hints.get(a));
                if (hintsFinal.size() == maxShortcuts - 2) {
                    break;
                }
            }
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda137
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m423x99ce6705(hintsFinal);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:101:0x0338 A[Catch: all -> 0x035c, TryCatch #4 {all -> 0x035c, blocks: (B:3:0x0004, B:5:0x0008, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0078, B:18:0x007e, B:20:0x008e, B:21:0x0091, B:22:0x0098, B:24:0x009e, B:27:0x00a5, B:29:0x00f0, B:30:0x00f6, B:31:0x00fb, B:33:0x0104, B:34:0x0109, B:35:0x0117, B:37:0x011d, B:39:0x013e, B:40:0x0158, B:42:0x0177, B:48:0x0188, B:50:0x0196, B:52:0x01a2, B:54:0x01aa, B:56:0x01b5, B:87:0x02d3, B:89:0x02df, B:93:0x02fb, B:95:0x0312, B:97:0x0317, B:98:0x031f, B:99:0x032b, B:101:0x0338, B:102:0x033e, B:103:0x0343), top: B:116:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:102:0x033e A[Catch: all -> 0x035c, TryCatch #4 {all -> 0x035c, blocks: (B:3:0x0004, B:5:0x0008, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0078, B:18:0x007e, B:20:0x008e, B:21:0x0091, B:22:0x0098, B:24:0x009e, B:27:0x00a5, B:29:0x00f0, B:30:0x00f6, B:31:0x00fb, B:33:0x0104, B:34:0x0109, B:35:0x0117, B:37:0x011d, B:39:0x013e, B:40:0x0158, B:42:0x0177, B:48:0x0188, B:50:0x0196, B:52:0x01a2, B:54:0x01aa, B:56:0x01b5, B:87:0x02d3, B:89:0x02df, B:93:0x02fb, B:95:0x0312, B:97:0x0317, B:98:0x031f, B:99:0x032b, B:101:0x0338, B:102:0x033e, B:103:0x0343), top: B:116:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:91:0x02f4  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x02f9  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0312 A[Catch: all -> 0x035c, TryCatch #4 {all -> 0x035c, blocks: (B:3:0x0004, B:5:0x0008, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0078, B:18:0x007e, B:20:0x008e, B:21:0x0091, B:22:0x0098, B:24:0x009e, B:27:0x00a5, B:29:0x00f0, B:30:0x00f6, B:31:0x00fb, B:33:0x0104, B:34:0x0109, B:35:0x0117, B:37:0x011d, B:39:0x013e, B:40:0x0158, B:42:0x0177, B:48:0x0188, B:50:0x0196, B:52:0x01a2, B:54:0x01aa, B:56:0x01b5, B:87:0x02d3, B:89:0x02df, B:93:0x02fb, B:95:0x0312, B:97:0x0317, B:98:0x031f, B:99:0x032b, B:101:0x0338, B:102:0x033e, B:103:0x0343), top: B:116:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0317 A[Catch: all -> 0x035c, TryCatch #4 {all -> 0x035c, blocks: (B:3:0x0004, B:5:0x0008, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0078, B:18:0x007e, B:20:0x008e, B:21:0x0091, B:22:0x0098, B:24:0x009e, B:27:0x00a5, B:29:0x00f0, B:30:0x00f6, B:31:0x00fb, B:33:0x0104, B:34:0x0109, B:35:0x0117, B:37:0x011d, B:39:0x013e, B:40:0x0158, B:42:0x0177, B:48:0x0188, B:50:0x0196, B:52:0x01a2, B:54:0x01aa, B:56:0x01b5, B:87:0x02d3, B:89:0x02df, B:93:0x02fb, B:95:0x0312, B:97:0x0317, B:98:0x031f, B:99:0x032b, B:101:0x0338, B:102:0x033e, B:103:0x0343), top: B:116:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x031f A[Catch: all -> 0x035c, TryCatch #4 {all -> 0x035c, blocks: (B:3:0x0004, B:5:0x0008, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0078, B:18:0x007e, B:20:0x008e, B:21:0x0091, B:22:0x0098, B:24:0x009e, B:27:0x00a5, B:29:0x00f0, B:30:0x00f6, B:31:0x00fb, B:33:0x0104, B:34:0x0109, B:35:0x0117, B:37:0x011d, B:39:0x013e, B:40:0x0158, B:42:0x0177, B:48:0x0188, B:50:0x0196, B:52:0x01a2, B:54:0x01aa, B:56:0x01b5, B:87:0x02d3, B:89:0x02df, B:93:0x02fb, B:95:0x0312, B:97:0x0317, B:98:0x031f, B:99:0x032b, B:101:0x0338, B:102:0x033e, B:103:0x0343), top: B:116:0x0004 }] */
    /* renamed from: lambda$buildShortcuts$103$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m423x99ce6705(java.util.ArrayList r30) {
        /*
            Method dump skipped, instructions count: 862
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m423x99ce6705(java.util.ArrayList):void");
    }

    public void loadHints(boolean cache) {
        if (this.loading || !getUserConfig().suggestContacts) {
            return;
        }
        if (cache) {
            if (this.loaded) {
                return;
            }
            this.loading = true;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m467lambda$loadHints$105$orgtelegrammessengerMediaDataController();
                }
            });
            this.loaded = true;
            return;
        }
        this.loading = true;
        TLRPC.TL_contacts_getTopPeers req = new TLRPC.TL_contacts_getTopPeers();
        req.hash = 0L;
        req.bots_pm = false;
        req.correspondents = true;
        req.groups = false;
        req.channels = false;
        req.bots_inline = true;
        req.offset = 0;
        req.limit = 20;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda41
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m472lambda$loadHints$110$orgtelegrammessengerMediaDataController(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadHints$105$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m467lambda$loadHints$105$orgtelegrammessengerMediaDataController() {
        Exception e;
        long selfUserId;
        final ArrayList<TLRPC.TL_topPeer> hintsNew = new ArrayList<>();
        final ArrayList<TLRPC.TL_topPeer> inlineBotsNew = new ArrayList<>();
        final ArrayList<TLRPC.User> users = new ArrayList<>();
        final ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        long selfUserId2 = getUserConfig().getClientUserId();
        try {
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            int i = 0;
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            while (cursor.next()) {
                long did = cursor.longValue(i);
                if (did != selfUserId2) {
                    int type = cursor.intValue(1);
                    TLRPC.TL_topPeer peer = new TLRPC.TL_topPeer();
                    peer.rating = cursor.doubleValue(2);
                    if (did > 0) {
                        try {
                            peer.peer = new TLRPC.TL_peerUser();
                            peer.peer.user_id = did;
                            usersToLoad.add(Long.valueOf(did));
                            selfUserId = selfUserId2;
                        } catch (Exception e2) {
                            e = e2;
                            FileLog.e(e);
                            return;
                        }
                    } else {
                        peer.peer = new TLRPC.TL_peerChat();
                        selfUserId = selfUserId2;
                        try {
                            peer.peer.chat_id = -did;
                            chatsToLoad.add(Long.valueOf(-did));
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.e(e);
                            return;
                        }
                    }
                    if (type == 0) {
                        hintsNew.add(peer);
                    } else if (type == 1) {
                        inlineBotsNew.add(peer);
                    }
                    selfUserId2 = selfUserId;
                    i = 0;
                }
            }
            cursor.dispose();
            if (!usersToLoad.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (!chatsToLoad.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda148
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m466lambda$loadHints$104$orgtelegrammessengerMediaDataController(users, chats, hintsNew, inlineBotsNew);
                }
            });
        } catch (Exception e4) {
            e = e4;
        }
    }

    /* renamed from: lambda$loadHints$104$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m466lambda$loadHints$104$orgtelegrammessengerMediaDataController(ArrayList users, ArrayList chats, ArrayList hintsNew, ArrayList inlineBotsNew) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        this.loading = false;
        this.loaded = true;
        this.hints = hintsNew;
        this.inlineBots = inlineBotsNew;
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        if (Math.abs(getUserConfig().lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
            loadHints(false);
        }
    }

    /* renamed from: lambda$loadHints$110$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m472lambda$loadHints$110$orgtelegrammessengerMediaDataController(final TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda154
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m470lambda$loadHints$108$orgtelegrammessengerMediaDataController(response);
                }
            });
        } else if (response instanceof TLRPC.TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m471lambda$loadHints$109$orgtelegrammessengerMediaDataController();
                }
            });
        }
    }

    /* renamed from: lambda$loadHints$108$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m470lambda$loadHints$108$orgtelegrammessengerMediaDataController(TLObject response) {
        final TLRPC.TL_contacts_topPeers topPeers = (TLRPC.TL_contacts_topPeers) response;
        getMessagesController().putUsers(topPeers.users, false);
        getMessagesController().putChats(topPeers.chats, false);
        for (int a = 0; a < topPeers.categories.size(); a++) {
            TLRPC.TL_topPeerCategoryPeers category = topPeers.categories.get(a);
            if (category.category instanceof TLRPC.TL_topPeerCategoryBotsInline) {
                this.inlineBots = category.peers;
                getUserConfig().botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = category.peers;
                long selfUserId = getUserConfig().getClientUserId();
                int b = 0;
                while (true) {
                    if (b >= this.hints.size()) {
                        break;
                    }
                    TLRPC.TL_topPeer topPeer = this.hints.get(b);
                    if (topPeer.peer.user_id != selfUserId) {
                        b++;
                    } else {
                        this.hints.remove(b);
                        break;
                    }
                }
                getUserConfig().ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
            }
        }
        getUserConfig().saveConfig(false);
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda171
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m469lambda$loadHints$107$orgtelegrammessengerMediaDataController(topPeers);
            }
        });
    }

    /* renamed from: lambda$loadHints$107$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m469lambda$loadHints$107$orgtelegrammessengerMediaDataController(TLRPC.TL_contacts_topPeers topPeers) {
        int type;
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
            getMessagesStorage().getDatabase().beginTransaction();
            getMessagesStorage().putUsersAndChats(topPeers.users, topPeers.chats, false, false);
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            for (int a = 0; a < topPeers.categories.size(); a++) {
                TLRPC.TL_topPeerCategoryPeers category = topPeers.categories.get(a);
                if (category.category instanceof TLRPC.TL_topPeerCategoryBotsInline) {
                    type = 1;
                } else {
                    type = 0;
                }
                for (int b = 0; b < category.peers.size(); b++) {
                    TLRPC.TL_topPeer peer = category.peers.get(b);
                    state.requery();
                    state.bindLong(1, MessageObject.getPeerId(peer.peer));
                    state.bindInteger(2, type);
                    state.bindDouble(3, peer.rating);
                    state.bindInteger(4, 0);
                    state.step();
                }
            }
            state.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m468lambda$loadHints$106$orgtelegrammessengerMediaDataController();
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadHints$106$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m468lambda$loadHints$106$orgtelegrammessengerMediaDataController() {
        getUserConfig().suggestContacts = true;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
    }

    /* renamed from: lambda$loadHints$109$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m471lambda$loadHints$109$orgtelegrammessengerMediaDataController() {
        getUserConfig().suggestContacts = false;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
        clearTopPeers();
    }

    public void clearTopPeers() {
        this.hints.clear();
        this.inlineBots.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda131
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m429xccf07f18();
            }
        });
        buildShortcuts();
    }

    /* renamed from: lambda$clearTopPeers$111$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m429xccf07f18() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception e) {
        }
    }

    public void increaseInlineRaiting(long uid) {
        int dt;
        if (!getUserConfig().suggestContacts) {
            return;
        }
        if (getUserConfig().botRatingLoadTime != 0) {
            dt = Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - getUserConfig().botRatingLoadTime);
        } else {
            dt = 60;
        }
        TLRPC.TL_topPeer peer = null;
        int a = 0;
        while (true) {
            if (a >= this.inlineBots.size()) {
                break;
            }
            TLRPC.TL_topPeer p = this.inlineBots.get(a);
            if (p.peer.user_id != uid) {
                a++;
            } else {
                peer = p;
                break;
            }
        }
        if (peer == null) {
            peer = new TLRPC.TL_topPeer();
            peer.peer = new TLRPC.TL_peerUser();
            peer.peer.user_id = uid;
            this.inlineBots.add(peer);
        }
        peer.rating += Math.exp(dt / getMessagesController().ratingDecay);
        Collections.sort(this.inlineBots, MediaDataController$$ExternalSyntheticLambda30.INSTANCE);
        if (this.inlineBots.size() > 20) {
            ArrayList<TLRPC.TL_topPeer> arrayList = this.inlineBots;
            arrayList.remove(arrayList.size() - 1);
        }
        savePeer(uid, 1, peer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public static /* synthetic */ int lambda$increaseInlineRaiting$112(TLRPC.TL_topPeer lhs, TLRPC.TL_topPeer rhs) {
        if (lhs.rating > rhs.rating) {
            return -1;
        }
        if (lhs.rating < rhs.rating) {
            return 1;
        }
        return 0;
    }

    public void removeInline(long dialogId) {
        for (int a = 0; a < this.inlineBots.size(); a++) {
            if (this.inlineBots.get(a).peer.user_id == dialogId) {
                this.inlineBots.remove(a);
                TLRPC.TL_contacts_resetTopPeerRating req = new TLRPC.TL_contacts_resetTopPeerRating();
                req.category = new TLRPC.TL_topPeerCategoryBotsInline();
                req.peer = getMessagesController().getInputPeer(dialogId);
                getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda81.INSTANCE);
                deletePeer(dialogId, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public static /* synthetic */ void lambda$removeInline$113(TLObject response, TLRPC.TL_error error) {
    }

    public void removePeer(long uid) {
        for (int a = 0; a < this.hints.size(); a++) {
            if (this.hints.get(a).peer.user_id == uid) {
                this.hints.remove(a);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TLRPC.TL_contacts_resetTopPeerRating req = new TLRPC.TL_contacts_resetTopPeerRating();
                req.category = new TLRPC.TL_topPeerCategoryCorrespondents();
                req.peer = getMessagesController().getInputPeer(uid);
                deletePeer(uid, 0);
                getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda82.INSTANCE);
                return;
            }
        }
    }

    public static /* synthetic */ void lambda$removePeer$114(TLObject response, TLRPC.TL_error error) {
    }

    public void increasePeerRaiting(final long dialogId) {
        TLRPC.User user;
        if (!getUserConfig().suggestContacts || !DialogObject.isUserDialog(dialogId) || (user = getMessagesController().getUser(Long.valueOf(dialogId))) == null || user.bot || user.self) {
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda97
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m447x1fe5e9f7(dialogId);
            }
        });
    }

    /* renamed from: lambda$increasePeerRaiting$117$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m447x1fe5e9f7(final long dialogId) {
        double dt = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        int lastTime = 0;
        int lastMid = 0;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages_v2 WHERE uid = %d AND out = 1", Long.valueOf(dialogId)), new Object[0]);
            if (cursor.next()) {
                lastMid = cursor.intValue(0);
                lastTime = cursor.intValue(1);
            }
            cursor.dispose();
            if (lastMid > 0 && getUserConfig().ratingLoadTime != 0) {
                dt = lastTime - getUserConfig().ratingLoadTime;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        final double dtFinal = dt;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda102
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m446x5cf98098(dialogId, dtFinal);
            }
        });
    }

    /* renamed from: lambda$increasePeerRaiting$116$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m446x5cf98098(long dialogId, double dtFinal) {
        TLRPC.TL_topPeer peer = null;
        int a = 0;
        while (true) {
            if (a >= this.hints.size()) {
                break;
            }
            TLRPC.TL_topPeer p = this.hints.get(a);
            if (p.peer.user_id != dialogId) {
                a++;
            } else {
                peer = p;
                break;
            }
        }
        if (peer == null) {
            peer = new TLRPC.TL_topPeer();
            peer.peer = new TLRPC.TL_peerUser();
            peer.peer.user_id = dialogId;
            this.hints.add(peer);
        }
        double d = peer.rating;
        double d2 = getMessagesController().ratingDecay;
        Double.isNaN(d2);
        peer.rating = d + Math.exp(dtFinal / d2);
        Collections.sort(this.hints, MediaDataController$$ExternalSyntheticLambda31.INSTANCE);
        savePeer(dialogId, 0, peer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    public static /* synthetic */ int lambda$increasePeerRaiting$115(TLRPC.TL_topPeer lhs, TLRPC.TL_topPeer rhs) {
        if (lhs.rating > rhs.rating) {
            return -1;
        }
        if (lhs.rating < rhs.rating) {
            return 1;
        }
        return 0;
    }

    private void savePeer(final long did, final int type, final double rating) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda105
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m549lambda$savePeer$118$orgtelegrammessengerMediaDataController(did, type, rating);
            }
        });
    }

    /* renamed from: lambda$savePeer$118$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m549lambda$savePeer$118$orgtelegrammessengerMediaDataController(long did, int type, double rating) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            state.requery();
            state.bindLong(1, did);
            state.bindInteger(2, type);
            state.bindDouble(3, rating);
            state.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void deletePeer(final long dialogId, final int type) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda103
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m430lambda$deletePeer$119$orgtelegrammessengerMediaDataController(dialogId, type);
            }
        });
    }

    /* renamed from: lambda$deletePeer$119$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m430lambda$deletePeer$119$orgtelegrammessengerMediaDataController(long dialogId, int type) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", Long.valueOf(dialogId), Integer.valueOf(type))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private Intent createIntrnalShortcutIntent(long dialogId) {
        Intent shortcutIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        if (DialogObject.isEncryptedDialog(dialogId)) {
            int encryptedChatId = DialogObject.getEncryptedChatId(dialogId);
            shortcutIntent.putExtra("encId", encryptedChatId);
            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(encryptedChatId));
            if (encryptedChat == null) {
                return null;
            }
        } else if (DialogObject.isUserDialog(dialogId)) {
            shortcutIntent.putExtra("userId", dialogId);
        } else if (!DialogObject.isChatDialog(dialogId)) {
            return null;
        } else {
            shortcutIntent.putExtra("chatId", -dialogId);
        }
        shortcutIntent.putExtra("currentAccount", this.currentAccount);
        shortcutIntent.setAction("com.tmessages.openchat" + dialogId);
        shortcutIntent.addFlags(ConnectionsManager.FileTypeFile);
        return shortcutIntent;
    }

    /* JADX WARN: Removed duplicated region for block: B:79:0x01d4 A[Catch: Exception -> 0x02a3, TryCatch #5 {Exception -> 0x02a3, blocks: (B:3:0x0002, B:5:0x000f, B:8:0x0022, B:9:0x0034, B:11:0x003a, B:12:0x004a, B:14:0x0050, B:20:0x0068, B:22:0x006e, B:23:0x007c, B:25:0x0082, B:26:0x0090, B:28:0x009c, B:30:0x00a9, B:32:0x00af, B:76:0x01bf, B:77:0x01c2, B:79:0x01d4, B:81:0x01f6, B:83:0x0200, B:85:0x0204, B:86:0x020e, B:87:0x0218, B:89:0x021e, B:91:0x0222, B:92:0x022c, B:93:0x0235, B:94:0x0241, B:96:0x0248, B:99:0x0252, B:101:0x0256, B:102:0x0260, B:103:0x026a, B:105:0x0270, B:107:0x0274, B:108:0x027e, B:109:0x0287), top: B:122:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0241 A[Catch: Exception -> 0x02a3, TryCatch #5 {Exception -> 0x02a3, blocks: (B:3:0x0002, B:5:0x000f, B:8:0x0022, B:9:0x0034, B:11:0x003a, B:12:0x004a, B:14:0x0050, B:20:0x0068, B:22:0x006e, B:23:0x007c, B:25:0x0082, B:26:0x0090, B:28:0x009c, B:30:0x00a9, B:32:0x00af, B:76:0x01bf, B:77:0x01c2, B:79:0x01d4, B:81:0x01f6, B:83:0x0200, B:85:0x0204, B:86:0x020e, B:87:0x0218, B:89:0x021e, B:91:0x0222, B:92:0x022c, B:93:0x0235, B:94:0x0241, B:96:0x0248, B:99:0x0252, B:101:0x0256, B:102:0x0260, B:103:0x026a, B:105:0x0270, B:107:0x0274, B:108:0x027e, B:109:0x0287), top: B:122:0x0002 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void installShortcut(long r20) {
        /*
            Method dump skipped, instructions count: 680
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.installShortcut(long):void");
    }

    public void uninstallShortcut(long dialogId) {
        String name;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("sdid_" + dialogId);
                arrayList.add("ndid_" + dialogId);
                ShortcutManagerCompat.removeDynamicShortcuts(ApplicationLoader.applicationContext, arrayList);
                if (Build.VERSION.SDK_INT >= 30) {
                    ShortcutManager shortcutManager = (ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
                    shortcutManager.removeLongLivedShortcuts(arrayList);
                }
                return;
            }
            TLRPC.User user = null;
            TLRPC.Chat chat = null;
            if (DialogObject.isEncryptedDialog(dialogId)) {
                int encryptedChatId = DialogObject.getEncryptedChatId(dialogId);
                TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(encryptedChatId));
                if (encryptedChat == null) {
                    return;
                }
                user = getMessagesController().getUser(Long.valueOf(encryptedChat.user_id));
            } else if (DialogObject.isUserDialog(dialogId)) {
                user = getMessagesController().getUser(Long.valueOf(dialogId));
            } else if (DialogObject.isChatDialog(dialogId)) {
                chat = getMessagesController().getChat(Long.valueOf(-dialogId));
            } else {
                return;
            }
            if (user == null && chat == null) {
                return;
            }
            if (user != null) {
                name = ContactsController.formatName(user.first_name, user.last_name);
            } else {
                name = chat.title;
            }
            Intent addIntent = new Intent();
            addIntent.putExtra("android.intent.extra.shortcut.INTENT", createIntrnalShortcutIntent(dialogId));
            addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
            addIntent.putExtra("duplicate", false);
            addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ int lambda$static$120(TLRPC.MessageEntity entity1, TLRPC.MessageEntity entity2) {
        if (entity1.offset > entity2.offset) {
            return 1;
        }
        if (entity1.offset < entity2.offset) {
            return -1;
        }
        return 0;
    }

    public void loadPinnedMessages(final long dialogId, final int maxId, final int fallback) {
        if (this.loadingPinnedMessages.indexOfKey(dialogId) >= 0) {
            return;
        }
        this.loadingPinnedMessages.put(dialogId, true);
        final TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
        req.peer = getMessagesController().getInputPeer(dialogId);
        req.limit = 40;
        req.offset_id = maxId;
        req.q = "";
        req.filter = new TLRPC.TL_inputMessagesFilterPinned();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda52
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m479x965d941d(fallback, req, dialogId, maxId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadPinnedMessages$122$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m479x965d941d(int fallback, TLRPC.TL_messages_search req, final long dialogId, int maxId, TLObject response, TLRPC.TL_error error) {
        boolean endReached;
        int totalCount;
        ArrayList<Integer> ids = new ArrayList<>();
        HashMap<Integer, MessageObject> messages = new HashMap<>();
        int totalCount2 = 0;
        if (response instanceof TLRPC.messages_Messages) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
            for (int a = 0; a < res.users.size(); a++) {
                TLRPC.User user = res.users.get(a);
                usersDict.put(user.id, user);
            }
            LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
            for (int a2 = 0; a2 < res.chats.size(); a2++) {
                TLRPC.Chat chat = res.chats.get(a2);
                chatsDict.put(chat.id, chat);
            }
            getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            getMessagesController().putUsers(res.users, false);
            getMessagesController().putChats(res.chats, false);
            int N = res.messages.size();
            for (int a3 = 0; a3 < N; a3++) {
                TLRPC.Message message = res.messages.get(a3);
                if (!(message instanceof TLRPC.TL_messageService) && !(message instanceof TLRPC.TL_messageEmpty)) {
                    ids.add(Integer.valueOf(message.id));
                    messages.put(Integer.valueOf(message.id), new MessageObject(this.currentAccount, message, usersDict, chatsDict, false, false));
                }
            }
            if (fallback != 0 && ids.isEmpty()) {
                ids.add(Integer.valueOf(fallback));
            }
            boolean endReached2 = res.messages.size() < req.limit;
            int totalCount3 = Math.max(res.count, res.messages.size());
            totalCount = totalCount3;
            endReached = endReached2;
        } else {
            if (fallback != 0) {
                ids.add(Integer.valueOf(fallback));
                totalCount2 = 1;
            }
            totalCount = totalCount2;
            endReached = false;
        }
        getMessagesStorage().updatePinnedMessages(dialogId, ids, true, totalCount, maxId, endReached, messages);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda100
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m478xd3712abe(dialogId);
            }
        });
    }

    /* renamed from: lambda$loadPinnedMessages$121$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m478xd3712abe(long dialogId) {
        this.loadingPinnedMessages.remove(dialogId);
    }

    public ArrayList<MessageObject> loadPinnedMessages(final long dialogId, final long channelId, final ArrayList<Integer> mids, boolean useQueue) {
        if (useQueue) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda112
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m480x5949fd7c(dialogId, channelId, mids);
                }
            });
            return null;
        }
        return loadPinnedMessageInternal(dialogId, channelId, mids, true);
    }

    /* renamed from: lambda$loadPinnedMessages$123$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m480x5949fd7c(long dialogId, long channelId, ArrayList mids) {
        loadPinnedMessageInternal(dialogId, channelId, mids, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private ArrayList<MessageObject> loadPinnedMessageInternal(final long dialogId, final long channelId, ArrayList<Integer> mids, boolean returnValue) {
        Exception e;
        String str;
        SQLiteCursor cursor;
        ArrayList<TLRPC.Chat> chats;
        ArrayList<Long> chatsToLoad;
        ArrayList<Long> usersToLoad;
        ArrayList<TLRPC.User> users;
        ArrayList<TLRPC.Chat> chats2;
        try {
            ArrayList<Integer> midsCopy = new ArrayList<>(mids);
            if (channelId != 0) {
                StringBuilder builder = new StringBuilder();
                int N = mids.size();
                for (int a = 0; a < N; a++) {
                    Integer messageId = mids.get(a);
                    if (builder.length() != 0) {
                        builder.append(",");
                    }
                    builder.append(messageId);
                }
                str = builder;
            } else {
                str = TextUtils.join(",", mids);
            }
            ArrayList<TLRPC.Message> results = new ArrayList<>();
            ArrayList<TLRPC.User> users2 = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats3 = new ArrayList<>();
            ArrayList<Long> usersToLoad2 = new ArrayList<>();
            ArrayList<Long> chatsToLoad2 = new ArrayList<>();
            long selfUserId = getUserConfig().clientUserId;
            int i = 1;
            SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages_v2 WHERE mid IN (%s) AND uid = %d", str, Long.valueOf(dialogId)), new Object[0]);
            while (cursor2.next()) {
                NativeByteBuffer data = cursor2.byteBufferValue(0);
                if (data != null) {
                    TLRPC.Message result = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    if (!(result.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                        result.readAttachPath(data, selfUserId);
                        result.id = cursor2.intValue(i);
                        result.date = cursor2.intValue(2);
                        result.dialog_id = dialogId;
                        MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad2, chatsToLoad2);
                        results.add(result);
                        midsCopy.remove(Integer.valueOf(result.id));
                    }
                    data.reuse();
                }
                i = 1;
            }
            cursor2.dispose();
            if (midsCopy.isEmpty()) {
                cursor = cursor2;
            } else {
                SQLiteCursor cursor3 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned_v2 WHERE uid = %d AND mid IN (%s)", Long.valueOf(dialogId), TextUtils.join(",", midsCopy)), new Object[0]);
                while (cursor3.next()) {
                    NativeByteBuffer data2 = cursor3.byteBufferValue(0);
                    if (data2 != null) {
                        TLRPC.Message result2 = TLRPC.Message.TLdeserialize(data2, data2.readInt32(false), false);
                        if (!(result2.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                            result2.readAttachPath(data2, selfUserId);
                            result2.dialog_id = dialogId;
                            MessagesStorage.addUsersAndChatsFromMessage(result2, usersToLoad2, chatsToLoad2);
                            results.add(result2);
                            midsCopy.remove(Integer.valueOf(result2.id));
                        }
                        data2.reuse();
                    }
                }
                cursor3.dispose();
                cursor = cursor3;
            }
            if (midsCopy.isEmpty()) {
                chatsToLoad = chatsToLoad2;
                usersToLoad = usersToLoad2;
                chats = chats3;
                users = users2;
            } else if (channelId != 0) {
                final TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
                req.channel = getMessagesController().getInputChannel(channelId);
                req.id = midsCopy;
                chatsToLoad = chatsToLoad2;
                usersToLoad = usersToLoad2;
                chats = chats3;
                users = users2;
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda61
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.m476x78a52d59(channelId, dialogId, req, tLObject, tL_error);
                    }
                });
            } else {
                chatsToLoad = chatsToLoad2;
                usersToLoad = usersToLoad2;
                chats = chats3;
                users = users2;
                final TLRPC.TL_messages_getMessages req2 = new TLRPC.TL_messages_getMessages();
                req2.id = midsCopy;
                try {
                    getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda63
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MediaDataController.this.m477x3b9196b8(dialogId, req2, tLObject, tL_error);
                        }
                    });
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    return null;
                }
            }
            if (!results.isEmpty()) {
                if (!usersToLoad.isEmpty()) {
                    getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
                if (!chatsToLoad.isEmpty()) {
                    chats2 = chats;
                    getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), chats2);
                } else {
                    chats2 = chats;
                }
                if (returnValue) {
                    return broadcastPinnedMessage(results, users, chats2, true, true);
                }
                broadcastPinnedMessage(results, users, chats2, true, false);
                return null;
            }
            return null;
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* renamed from: lambda$loadPinnedMessageInternal$124$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m476x78a52d59(long channelId, long dialogId, TLRPC.TL_channels_getMessages req, TLObject response, TLRPC.TL_error error) {
        boolean ok = false;
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            if (!messagesRes.messages.isEmpty()) {
                getMessagesController().getChat(Long.valueOf(channelId));
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                broadcastPinnedMessage(messagesRes.messages, messagesRes.users, messagesRes.chats, false, false);
                getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                savePinnedMessages(dialogId, messagesRes.messages);
                ok = true;
            }
        }
        if (!ok) {
            getMessagesStorage().updatePinnedMessages(dialogId, req.id, false, -1, 0, false, null);
        }
    }

    /* renamed from: lambda$loadPinnedMessageInternal$125$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m477x3b9196b8(long dialogId, TLRPC.TL_messages_getMessages req, TLObject response, TLRPC.TL_error error) {
        boolean ok = false;
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            if (!messagesRes.messages.isEmpty()) {
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                broadcastPinnedMessage(messagesRes.messages, messagesRes.users, messagesRes.chats, false, false);
                getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                savePinnedMessages(dialogId, messagesRes.messages);
                ok = true;
            }
        }
        if (!ok) {
            getMessagesStorage().updatePinnedMessages(dialogId, req.id, false, -1, 0, false, null);
        }
    }

    private void savePinnedMessages(final long dialogId, final ArrayList<TLRPC.Message> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda143
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m550x68019462(arrayList, dialogId);
            }
        });
    }

    /* renamed from: lambda$savePinnedMessages$126$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m550x68019462(ArrayList arrayList, long dialogId) {
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned_v2 VALUES(?, ?, ?)");
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Message message = (TLRPC.Message) arrayList.get(a);
                NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                message.serializeToStream(data);
                state.requery();
                state.bindLong(1, dialogId);
                state.bindInteger(2, message.id);
                state.bindByteBuffer(3, data);
                state.step();
                data.reuse();
            }
            state.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private ArrayList<MessageObject> broadcastPinnedMessage(final ArrayList<TLRPC.Message> results, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final boolean isCache, boolean returnValue) {
        if (results.isEmpty()) {
            return null;
        }
        final LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        for (int a = 0; a < users.size(); a++) {
            TLRPC.User user = users.get(a);
            usersDict.put(user.id, user);
        }
        final LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
        for (int a2 = 0; a2 < chats.size(); a2++) {
            TLRPC.Chat chat = chats.get(a2);
            chatsDict.put(chat.id, chat);
        }
        final ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (!returnValue) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda151
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m421xd99db1b2(users, isCache, chats, results, messageObjects, usersDict, chatsDict);
                }
            });
            return null;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda149
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m419x53c4def4(users, isCache, chats);
            }
        });
        int checkedCount = 0;
        int N = results.size();
        for (int a3 = 0; a3 < N; a3++) {
            TLRPC.Message message = results.get(a3);
            if ((message.media instanceof TLRPC.TL_messageMediaDocument) || (message.media instanceof TLRPC.TL_messageMediaPhoto)) {
                checkedCount++;
            }
            messageObjects.add(new MessageObject(this.currentAccount, message, usersDict, chatsDict, false, checkedCount < 30));
        }
        return messageObjects;
    }

    /* renamed from: lambda$broadcastPinnedMessage$127$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m419x53c4def4(ArrayList users, boolean isCache, ArrayList chats) {
        getMessagesController().putUsers(users, isCache);
        getMessagesController().putChats(chats, isCache);
    }

    /* renamed from: lambda$broadcastPinnedMessage$129$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m421xd99db1b2(ArrayList users, boolean isCache, ArrayList chats, ArrayList results, final ArrayList messageObjects, LongSparseArray usersDict, LongSparseArray chatsDict) {
        getMessagesController().putUsers(users, isCache);
        getMessagesController().putChats(chats, isCache);
        int checkedCount = 0;
        int N = results.size();
        for (int a = 0; a < N; a++) {
            TLRPC.Message message = (TLRPC.Message) results.get(a);
            if ((message.media instanceof TLRPC.TL_messageMediaDocument) || (message.media instanceof TLRPC.TL_messageMediaPhoto)) {
                checkedCount++;
            }
            messageObjects.add(new MessageObject(this.currentAccount, message, (LongSparseArray<TLRPC.User>) usersDict, (LongSparseArray<TLRPC.Chat>) chatsDict, false, checkedCount < 30));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda136
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m420x16b14853(messageObjects);
            }
        });
    }

    /* renamed from: lambda$broadcastPinnedMessage$128$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m420x16b14853(ArrayList messageObjects) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(((MessageObject) messageObjects.get(0)).getDialogId()), null, true, messageObjects, 0, 0, -1, false);
    }

    private static void removeEmptyMessages(ArrayList<TLRPC.Message> messages) {
        int a = 0;
        while (a < messages.size()) {
            TLRPC.Message message = messages.get(a);
            if (message == null || (message instanceof TLRPC.TL_messageEmpty) || (message.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                messages.remove(a);
                a--;
            }
            a++;
        }
    }

    public void loadReplyMessagesForMessages(ArrayList<MessageObject> messages, final long dialogId, final boolean scheduled, final Runnable callback) {
        if (DialogObject.isEncryptedDialog(dialogId)) {
            final ArrayList<Long> replyMessages = new ArrayList<>();
            final LongSparseArray<ArrayList<MessageObject>> replyMessageRandomOwners = new LongSparseArray<>();
            for (int a = 0; a < messages.size(); a++) {
                MessageObject messageObject = messages.get(a);
                if (messageObject != null && messageObject.isReply() && messageObject.replyMessageObject == null) {
                    long id = messageObject.messageOwner.reply_to.reply_to_random_id;
                    ArrayList<MessageObject> messageObjects = replyMessageRandomOwners.get(id);
                    if (messageObjects == null) {
                        messageObjects = new ArrayList<>();
                        replyMessageRandomOwners.put(id, messageObjects);
                    }
                    messageObjects.add(messageObject);
                    if (!replyMessages.contains(Long.valueOf(id))) {
                        replyMessages.add(Long.valueOf(id));
                    }
                }
            }
            if (replyMessages.isEmpty()) {
                if (callback != null) {
                    callback.run();
                    return;
                }
                return;
            }
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda145
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m490xc0504be4(replyMessages, dialogId, replyMessageRandomOwners, callback);
                }
            });
            return;
        }
        final LongSparseArray<SparseArray<ArrayList<MessageObject>>> replyMessageOwners = new LongSparseArray<>();
        final LongSparseArray<ArrayList<Integer>> dialogReplyMessagesIds = new LongSparseArray<>();
        for (int a2 = 0; a2 < messages.size(); a2++) {
            MessageObject messageObject2 = messages.get(a2);
            if (messageObject2 != null && messageObject2.getId() > 0 && messageObject2.isReply()) {
                int messageId = messageObject2.messageOwner.reply_to.reply_to_msg_id;
                long channelId = 0;
                if (messageObject2.messageOwner.reply_to.reply_to_peer_id != null) {
                    if (messageObject2.messageOwner.reply_to.reply_to_peer_id.channel_id != 0) {
                        channelId = messageObject2.messageOwner.reply_to.reply_to_peer_id.channel_id;
                    }
                } else if (messageObject2.messageOwner.peer_id.channel_id != 0) {
                    channelId = messageObject2.messageOwner.peer_id.channel_id;
                }
                if (messageObject2.replyMessageObject == null || (messageObject2.replyMessageObject.messageOwner != null && messageObject2.replyMessageObject.messageOwner.peer_id != null && !(messageObject2.messageOwner instanceof TLRPC.TL_messageEmpty) && messageObject2.replyMessageObject.messageOwner.peer_id.channel_id != channelId)) {
                    SparseArray<ArrayList<MessageObject>> sparseArray = replyMessageOwners.get(dialogId);
                    ArrayList<Integer> ids = dialogReplyMessagesIds.get(channelId);
                    if (sparseArray == null) {
                        sparseArray = new SparseArray<>();
                        replyMessageOwners.put(dialogId, sparseArray);
                    }
                    if (ids == null) {
                        ids = new ArrayList<>();
                        dialogReplyMessagesIds.put(channelId, ids);
                    }
                    ArrayList<MessageObject> arrayList = sparseArray.get(messageId);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        sparseArray.put(messageId, arrayList);
                        if (!ids.contains(Integer.valueOf(messageId))) {
                            ids.add(Integer.valueOf(messageId));
                        }
                    }
                    arrayList.add(messageObject2);
                }
            }
        }
        if (replyMessageOwners.isEmpty()) {
            if (callback != null) {
                callback.run();
                return;
            }
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda123
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m494xcc01f160(replyMessageOwners, dialogReplyMessagesIds, scheduled, dialogId, callback);
            }
        });
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$131$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m490xc0504be4(ArrayList replyMessages, final long dialogId, LongSparseArray replyMessageRandomOwners, Runnable callback) {
        Exception e;
        final ArrayList<MessageObject> loadedMessages;
        SQLiteDatabase database;
        Locale locale;
        int i;
        Object[] objArr;
        try {
            loadedMessages = new ArrayList<>();
            database = getMessagesStorage().getDatabase();
            locale = Locale.US;
            i = 1;
            objArr = new Object[1];
        } catch (Exception e2) {
            e = e2;
        }
        try {
            boolean z = false;
            objArr[0] = TextUtils.join(",", replyMessages);
            SQLiteCursor cursor = database.queryFinalized(String.format(locale, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms_v2 as r INNER JOIN messages_v2 as m ON r.mid = m.mid AND r.uid = m.uid WHERE r.random_id IN(%s)", objArr), new Object[0]);
            while (cursor.next()) {
                int i2 = z ? 1 : 0;
                int i3 = z ? 1 : 0;
                NativeByteBuffer data = cursor.byteBufferValue(i2);
                if (data != null) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                    message.readAttachPath(data, getUserConfig().clientUserId);
                    data.reuse();
                    message.id = cursor.intValue(i);
                    message.date = cursor.intValue(2);
                    message.dialog_id = dialogId;
                    long value = cursor.longValue(3);
                    ArrayList<MessageObject> arrayList = (ArrayList) replyMessageRandomOwners.get(value);
                    replyMessageRandomOwners.remove(value);
                    if (arrayList != null) {
                        MessageObject messageObject = new MessageObject(this.currentAccount, message, z, z);
                        loadedMessages.add(messageObject);
                        int b = 0;
                        while (b < arrayList.size()) {
                            MessageObject object = arrayList.get(b);
                            object.replyMessageObject = messageObject;
                            NativeByteBuffer data2 = data;
                            object.messageOwner.reply_to = new TLRPC.TL_messageReplyHeader();
                            object.messageOwner.reply_to.reply_to_msg_id = messageObject.getId();
                            b++;
                            data = data2;
                        }
                    }
                }
                i = 1;
                z = false;
            }
            cursor.dispose();
            if (replyMessageRandomOwners.size() != 0) {
                for (int b2 = 0; b2 < replyMessageRandomOwners.size(); b2++) {
                    ArrayList<MessageObject> arrayList2 = (ArrayList) replyMessageRandomOwners.valueAt(b2);
                    for (int a = 0; a < arrayList2.size(); a++) {
                        TLRPC.Message message2 = arrayList2.get(a).messageOwner;
                        if (message2.reply_to != null) {
                            message2.reply_to.reply_to_random_id = 0L;
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda113
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m489xfd63e285(dialogId, loadedMessages);
                }
            });
            if (callback != null) {
                callback.run();
            }
        } catch (Exception e3) {
            e = e3;
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$130$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m489xfd63e285(long dialogId, ArrayList loadedMessages) {
        getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(dialogId), loadedMessages, 0);
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$135$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m494xcc01f160(final LongSparseArray replyMessageOwners, LongSparseArray dialogReplyMessagesIds, final boolean scheduled, final long dialogId, final Runnable callback) {
        ArrayList<Long> usersToLoad;
        int N;
        ArrayList<TLRPC.Message> result;
        int a;
        int N2;
        LongSparseArray longSparseArray = replyMessageOwners;
        long j = dialogId;
        try {
            ArrayList<TLRPC.Message> result2 = new ArrayList<>();
            ArrayList<TLRPC.User> users = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            ArrayList<Long> usersToLoad2 = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            int b = 0;
            int N22 = replyMessageOwners.size();
            while (b < N22) {
                long did = longSparseArray.keyAt(b);
                SparseArray sparseArray = (SparseArray) longSparseArray.valueAt(b);
                ArrayList<Integer> ids = (ArrayList) dialogReplyMessagesIds.get(did);
                if (ids == null) {
                    N2 = N22;
                } else {
                    N2 = N22;
                    SQLiteCursor cursor = scheduled ? getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM scheduled_messages_v2 WHERE mid IN(%s) AND uid = %d", TextUtils.join(",", ids), Long.valueOf(dialogId)), new Object[0]) : getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages_v2 WHERE mid IN(%s) AND uid = %d", TextUtils.join(",", ids), Long.valueOf(dialogId)), new Object[0]);
                    while (cursor.next()) {
                        NativeByteBuffer data = cursor.byteBufferValue(0);
                        if (data != null) {
                            TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                            message.readAttachPath(data, getUserConfig().clientUserId);
                            data.reuse();
                            message.id = cursor.intValue(1);
                            message.date = cursor.intValue(2);
                            message.dialog_id = j;
                            MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad2, chatsToLoad);
                            result2.add(message);
                            long channelId = message.peer_id != null ? message.peer_id.channel_id : 0L;
                            ArrayList<Integer> mids = (ArrayList) dialogReplyMessagesIds.get(channelId);
                            if (mids != null) {
                                mids.remove(Integer.valueOf(message.id));
                                if (mids.isEmpty()) {
                                    dialogReplyMessagesIds.remove(channelId);
                                }
                            }
                        }
                    }
                    cursor.dispose();
                }
                b++;
                longSparseArray = replyMessageOwners;
                N22 = N2;
            }
            if (!usersToLoad2.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad2), users);
            }
            if (!chatsToLoad.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            broadcastReplyMessages(result2, replyMessageOwners, users, chats, dialogId, true);
            if (dialogReplyMessagesIds.isEmpty()) {
                if (callback != null) {
                    AndroidUtilities.runOnUIThread(callback);
                    return;
                }
                return;
            }
            int N3 = dialogReplyMessagesIds.size();
            int a2 = 0;
            while (a2 < N3) {
                final long channelId2 = dialogReplyMessagesIds.keyAt(a2);
                if (scheduled) {
                    TLRPC.TL_messages_getScheduledMessages req = new TLRPC.TL_messages_getScheduledMessages();
                    req.peer = getMessagesController().getInputPeer(j);
                    req.id = (ArrayList) dialogReplyMessagesIds.valueAt(a2);
                    result = result2;
                    N = N3;
                    a = a2;
                    usersToLoad = usersToLoad2;
                    getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda59
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MediaDataController.this.m491x833cb543(dialogId, channelId2, replyMessageOwners, scheduled, callback, tLObject, tL_error);
                        }
                    });
                } else {
                    result = result2;
                    a = a2;
                    usersToLoad = usersToLoad2;
                    N = N3;
                    if (channelId2 != 0) {
                        TLRPC.TL_channels_getMessages req2 = new TLRPC.TL_channels_getMessages();
                        req2.channel = getMessagesController().getInputChannel(channelId2);
                        req2.id = (ArrayList) dialogReplyMessagesIds.valueAt(a);
                        getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda60
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MediaDataController.this.m492x46291ea2(dialogId, channelId2, replyMessageOwners, scheduled, callback, tLObject, tL_error);
                            }
                        });
                    } else {
                        TLRPC.TL_messages_getMessages req3 = new TLRPC.TL_messages_getMessages();
                        req3.id = (ArrayList) dialogReplyMessagesIds.valueAt(a);
                        getConnectionsManager().sendRequest(req3, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda62
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MediaDataController.this.m493x9158801(dialogId, replyMessageOwners, scheduled, callback, tLObject, tL_error);
                            }
                        });
                    }
                }
                a2 = a + 1;
                j = dialogId;
                result2 = result;
                N3 = N;
                usersToLoad2 = usersToLoad;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$132$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m491x833cb543(long dialogId, long channelId, LongSparseArray replyMessageOwners, boolean scheduled, Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            for (int i = 0; i < messagesRes.messages.size(); i++) {
                TLRPC.Message message = messagesRes.messages.get(i);
                if (message.dialog_id == 0) {
                    message.dialog_id = dialogId;
                }
            }
            MessageObject.fixMessagePeer(messagesRes.messages, channelId);
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            saveReplyMessages(replyMessageOwners, messagesRes.messages, scheduled);
        }
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$133$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m492x46291ea2(long dialogId, long channelId, LongSparseArray replyMessageOwners, boolean scheduled, Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            for (int i = 0; i < messagesRes.messages.size(); i++) {
                TLRPC.Message message = messagesRes.messages.get(i);
                if (message.dialog_id == 0) {
                    message.dialog_id = dialogId;
                }
            }
            MessageObject.fixMessagePeer(messagesRes.messages, channelId);
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            saveReplyMessages(replyMessageOwners, messagesRes.messages, scheduled);
        }
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$134$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m493x9158801(long dialogId, LongSparseArray replyMessageOwners, boolean scheduled, Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            for (int i = 0; i < messagesRes.messages.size(); i++) {
                TLRPC.Message message = messagesRes.messages.get(i);
                if (message.dialog_id == 0) {
                    message.dialog_id = dialogId;
                }
            }
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            saveReplyMessages(replyMessageOwners, messagesRes.messages, scheduled);
        }
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    private void saveReplyMessages(final LongSparseArray<SparseArray<ArrayList<MessageObject>>> replyMessageOwners, final ArrayList<TLRPC.Message> result, final boolean scheduled) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m551x51190305(scheduled, result, replyMessageOwners);
            }
        });
    }

    /* renamed from: lambda$saveReplyMessages$136$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m551x51190305(boolean scheduled, ArrayList result, LongSparseArray replyMessageOwners) {
        Exception e;
        SQLitePreparedStatement state;
        ArrayList<MessageObject> messageObjects;
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            if (scheduled) {
                state = getMessagesStorage().getDatabase().executeFast("UPDATE scheduled_messages_v2 SET replydata = ?, reply_to_message_id = ? WHERE mid = ? AND uid = ?");
            } else {
                state = getMessagesStorage().getDatabase().executeFast("UPDATE messages_v2 SET replydata = ?, reply_to_message_id = ? WHERE mid = ? AND uid = ?");
            }
            for (int a = 0; a < result.size(); a++) {
                try {
                    TLRPC.Message message = (TLRPC.Message) result.get(a);
                    long dialogId = MessageObject.getDialogId(message);
                    try {
                        SparseArray<ArrayList<MessageObject>> sparseArray = (SparseArray) replyMessageOwners.get(dialogId);
                        if (sparseArray != null && (messageObjects = sparseArray.get(message.id)) != null) {
                            NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                            message.serializeToStream(data);
                            for (int b = 0; b < messageObjects.size(); b++) {
                                MessageObject messageObject = messageObjects.get(b);
                                state.requery();
                                state.bindByteBuffer(1, data);
                                state.bindInteger(2, message.id);
                                state.bindInteger(3, messageObject.getId());
                                state.bindLong(4, messageObject.getDialogId());
                                state.step();
                            }
                            data.reuse();
                        }
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
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e4) {
            e = e4;
        }
    }

    private void broadcastReplyMessages(ArrayList<TLRPC.Message> result, final LongSparseArray<SparseArray<ArrayList<MessageObject>>> replyMessageOwners, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final long dialog_id, final boolean isCache) {
        LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        for (int a = 0; a < users.size(); a++) {
            TLRPC.User user = users.get(a);
            usersDict.put(user.id, user);
        }
        LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
        for (int a2 = 0; a2 < chats.size(); a2++) {
            TLRPC.Chat chat = chats.get(a2);
            chatsDict.put(chat.id, chat);
        }
        final ArrayList<MessageObject> messageObjects = new ArrayList<>();
        int N = result.size();
        for (int a3 = 0; a3 < N; a3++) {
            messageObjects.add(new MessageObject(this.currentAccount, result.get(a3), usersDict, chatsDict, false, false));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda150
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m422x27eff94e(users, isCache, chats, messageObjects, replyMessageOwners, dialog_id);
            }
        });
    }

    /* renamed from: lambda$broadcastReplyMessages$137$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m422x27eff94e(ArrayList users, boolean isCache, ArrayList chats, ArrayList messageObjects, LongSparseArray replyMessageOwners, long dialog_id) {
        ArrayList<MessageObject> arrayList;
        getMessagesController().putUsers(users, isCache);
        getMessagesController().putChats(chats, isCache);
        boolean changed = false;
        int N = messageObjects.size();
        for (int a = 0; a < N; a++) {
            MessageObject messageObject = (MessageObject) messageObjects.get(a);
            long dialogId = messageObject.getDialogId();
            SparseArray<ArrayList<MessageObject>> sparseArray = (SparseArray) replyMessageOwners.get(dialogId);
            if (sparseArray != null && (arrayList = sparseArray.get(messageObject.getId())) != null) {
                for (int b = 0; b < arrayList.size(); b++) {
                    MessageObject m = arrayList.get(b);
                    m.replyMessageObject = messageObject;
                    if (m.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage) {
                        m.generatePinMessageText(null, null);
                    } else if (m.messageOwner.action instanceof TLRPC.TL_messageActionGameScore) {
                        m.generateGameMessageText(null);
                    } else if (m.messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent) {
                        m.generatePaymentSentMessageText(null);
                    }
                }
                changed = true;
            }
        }
        if (changed) {
            getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(dialog_id), messageObjects, replyMessageOwners);
        }
    }

    public static void sortEntities(ArrayList<TLRPC.MessageEntity> entities) {
        Collections.sort(entities, entityComparator);
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0027 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0029 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean checkInclusion(int r6, java.util.List<org.telegram.tgnet.TLRPC.MessageEntity> r7, boolean r8) {
        /*
            r0 = 0
            if (r7 == 0) goto L2d
            boolean r1 = r7.isEmpty()
            if (r1 == 0) goto La
            goto L2d
        La:
            int r1 = r7.size()
            r2 = 0
        Lf:
            if (r2 >= r1) goto L2c
            java.lang.Object r3 = r7.get(r2)
            org.telegram.tgnet.TLRPC$MessageEntity r3 = (org.telegram.tgnet.TLRPC.MessageEntity) r3
            int r4 = r3.offset
            if (r8 == 0) goto L1e
            if (r4 >= r6) goto L29
            goto L20
        L1e:
            if (r4 > r6) goto L29
        L20:
            int r4 = r3.offset
            int r5 = r3.length
            int r4 = r4 + r5
            if (r4 <= r6) goto L29
            r0 = 1
            return r0
        L29:
            int r2 = r2 + 1
            goto Lf
        L2c:
            return r0
        L2d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.checkInclusion(int, java.util.List, boolean):boolean");
    }

    private static boolean checkIntersection(int start, int end, List<TLRPC.MessageEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            if (entity.offset > start && entity.offset + entity.length <= end) {
                return true;
            }
        }
        return false;
    }

    public CharSequence substring(CharSequence source, int start, int end) {
        if (source instanceof SpannableStringBuilder) {
            return source.subSequence(start, end);
        }
        if (source instanceof SpannedString) {
            return source.subSequence(start, end);
        }
        return TextUtils.substring(source, start, end);
    }

    private static CharacterStyle createNewSpan(CharacterStyle baseSpan, TextStyleSpan.TextStyleRun textStyleRun, TextStyleSpan.TextStyleRun newStyleRun, boolean allowIntersection) {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun(textStyleRun);
        if (newStyleRun != null) {
            if (allowIntersection) {
                run.merge(newStyleRun);
            } else {
                run.replace(newStyleRun);
            }
        }
        if (baseSpan instanceof TextStyleSpan) {
            return new TextStyleSpan(run);
        }
        if (baseSpan instanceof URLSpanReplacement) {
            URLSpanReplacement span = (URLSpanReplacement) baseSpan;
            return new URLSpanReplacement(span.getURL(), run);
        }
        return null;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:58:0x00d2
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:92)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:44)
        */
    public static void addStyleToText(org.telegram.ui.Components.TextStyleSpan r16, int r17, int r18, android.text.Spannable r19, boolean r20) {
        /*
            Method dump skipped, instructions count: 219
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.addStyleToText(org.telegram.ui.Components.TextStyleSpan, int, int, android.text.Spannable, boolean):void");
    }

    public static void addTextStyleRuns(MessageObject msg, Spannable text) {
        addTextStyleRuns(msg.messageOwner.entities, msg.messageText, text, -1);
    }

    public static void addTextStyleRuns(TLRPC.DraftMessage msg, Spannable text, int allowedFlags) {
        addTextStyleRuns(msg.entities, msg.message, text, allowedFlags);
    }

    public static void addTextStyleRuns(MessageObject msg, Spannable text, int allowedFlags) {
        addTextStyleRuns(msg.messageOwner.entities, msg.messageText, text, allowedFlags);
    }

    public static void addTextStyleRuns(ArrayList<TLRPC.MessageEntity> entities, CharSequence messageText, Spannable text) {
        addTextStyleRuns(entities, messageText, text, -1);
    }

    public static void addTextStyleRuns(ArrayList<TLRPC.MessageEntity> entities, CharSequence messageText, Spannable text, int allowedFlags) {
        TextStyleSpan[] textStyleSpanArr;
        for (TextStyleSpan prevSpan : (TextStyleSpan[]) text.getSpans(0, text.length(), TextStyleSpan.class)) {
            text.removeSpan(prevSpan);
        }
        Iterator<TextStyleSpan.TextStyleRun> it = getTextStyleRuns(entities, messageText, allowedFlags).iterator();
        while (it.hasNext()) {
            TextStyleSpan.TextStyleRun run = it.next();
            addStyleToText(new TextStyleSpan(run), run.start, run.end, text, true);
        }
    }

    public static ArrayList<TextStyleSpan.TextStyleRun> getTextStyleRuns(ArrayList<TLRPC.MessageEntity> entities, CharSequence text, int allowedFlags) {
        ArrayList<TextStyleSpan.TextStyleRun> runs = new ArrayList<>();
        ArrayList<TLRPC.MessageEntity> entitiesCopy = new ArrayList<>(entities);
        Collections.sort(entitiesCopy, MediaDataController$$ExternalSyntheticLambda28.INSTANCE);
        int N = entitiesCopy.size();
        for (int a = 0; a < N; a++) {
            TLRPC.MessageEntity entity = entitiesCopy.get(a);
            if (entity != null && entity.length > 0 && entity.offset >= 0 && entity.offset < text.length()) {
                if (entity.offset + entity.length > text.length()) {
                    entity.length = text.length() - entity.offset;
                }
                TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
                newRun.start = entity.offset;
                newRun.end = newRun.start + entity.length;
                if (entity instanceof TLRPC.TL_messageEntitySpoiler) {
                    newRun.flags = 256;
                } else if (entity instanceof TLRPC.TL_messageEntityStrike) {
                    newRun.flags = 8;
                } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                    newRun.flags = 16;
                } else if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                    newRun.flags = 32;
                } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                    newRun.flags = 1;
                } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                    newRun.flags = 2;
                } else if ((entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre)) {
                    newRun.flags = 4;
                } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                    newRun.flags = 64;
                    newRun.urlEntity = entity;
                } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    newRun.flags = 64;
                    newRun.urlEntity = entity;
                } else {
                    newRun.flags = 128;
                    newRun.urlEntity = entity;
                }
                newRun.flags &= allowedFlags;
                int b = 0;
                int N2 = runs.size();
                while (b < N2) {
                    TextStyleSpan.TextStyleRun run = runs.get(b);
                    if (newRun.start > run.start) {
                        if (newRun.start < run.end) {
                            if (newRun.end < run.end) {
                                TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                                r.merge(run);
                                int b2 = b + 1;
                                runs.add(b2, r);
                                TextStyleSpan.TextStyleRun r2 = new TextStyleSpan.TextStyleRun(run);
                                r2.start = newRun.end;
                                b = b2 + 1;
                                N2 = N2 + 1 + 1;
                                runs.add(b, r2);
                            } else {
                                TextStyleSpan.TextStyleRun r3 = new TextStyleSpan.TextStyleRun(newRun);
                                r3.merge(run);
                                r3.end = run.end;
                                b++;
                                N2++;
                                runs.add(b, r3);
                            }
                            int temp = newRun.start;
                            newRun.start = run.end;
                            run.end = temp;
                        }
                    } else if (run.start < newRun.end) {
                        int temp2 = run.start;
                        if (newRun.end == run.end) {
                            run.merge(newRun);
                        } else if (newRun.end < run.end) {
                            TextStyleSpan.TextStyleRun r4 = new TextStyleSpan.TextStyleRun(run);
                            r4.merge(newRun);
                            r4.end = newRun.end;
                            b++;
                            N2++;
                            runs.add(b, r4);
                            run.start = newRun.end;
                        } else {
                            TextStyleSpan.TextStyleRun r5 = new TextStyleSpan.TextStyleRun(newRun);
                            r5.start = run.end;
                            b++;
                            N2++;
                            runs.add(b, r5);
                            run.merge(newRun);
                        }
                        newRun.end = temp2;
                    }
                    b++;
                }
                int b3 = newRun.start;
                if (b3 < newRun.end) {
                    runs.add(newRun);
                }
            }
        }
        return runs;
    }

    public static /* synthetic */ int lambda$getTextStyleRuns$138(TLRPC.MessageEntity o1, TLRPC.MessageEntity o2) {
        if (o1.offset > o2.offset) {
            return 1;
        }
        if (o1.offset < o2.offset) {
            return -1;
        }
        return 0;
    }

    public void addStyle(int flags, int spanStart, int spanEnd, ArrayList<TLRPC.MessageEntity> entities) {
        if ((flags & 256) != 0) {
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntitySpoiler(), spanStart, spanEnd));
        }
        if ((flags & 1) != 0) {
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityBold(), spanStart, spanEnd));
        }
        if ((flags & 2) != 0) {
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityItalic(), spanStart, spanEnd));
        }
        if ((flags & 4) != 0) {
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityCode(), spanStart, spanEnd));
        }
        if ((flags & 8) != 0) {
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityStrike(), spanStart, spanEnd));
        }
        if ((flags & 16) != 0) {
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityUnderline(), spanStart, spanEnd));
        }
        if ((flags & 32) != 0) {
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityBlockquote(), spanStart, spanEnd));
        }
    }

    private TLRPC.MessageEntity setEntityStartEnd(TLRPC.MessageEntity entity, int spanStart, int spanEnd) {
        entity.offset = spanStart;
        entity.length = spanEnd - spanStart;
        return entity;
    }

    public ArrayList<TLRPC.MessageEntity> getEntities(CharSequence[] message, boolean allowStrike) {
        int lastIndex;
        int start;
        URLSpanReplacement[] spansUrlReplacement;
        ArrayList<TLRPC.MessageEntity> entities;
        char c;
        if (message == null || message[0] == null) {
            return null;
        }
        ArrayList<TLRPC.MessageEntity> entities2 = null;
        int start2 = -1;
        int lastIndex2 = 0;
        boolean isPre = false;
        while (true) {
            int indexOf = TextUtils.indexOf(message[0], !isPre ? "`" : "```", lastIndex2);
            int index = indexOf;
            if (indexOf == -1) {
                break;
            } else if (start2 == -1) {
                isPre = message[0].length() - index > 2 && message[0].charAt(index + 1) == '`' && message[0].charAt(index + 2) == '`';
                start2 = index;
                lastIndex2 = index + (isPre ? 3 : 1);
            } else {
                if (entities2 == null) {
                    entities2 = new ArrayList<>();
                }
                for (int a = (isPre ? 3 : 1) + index; a < message[0].length() && message[0].charAt(a) == '`'; a++) {
                    index++;
                }
                int lastIndex3 = (isPre ? 3 : 1) + index;
                if (isPre) {
                    int firstChar = start2 > 0 ? message[0].charAt(start2 - 1) : 0;
                    boolean replacedFirst = firstChar == 32 || firstChar == 10;
                    CharSequence startMessage = substring(message[0], 0, start2 - (replacedFirst ? 1 : 0));
                    CharSequence content = substring(message[0], start2 + 3, index);
                    int firstChar2 = index + 3 < message[0].length() ? message[0].charAt(index + 3) : 0;
                    CharSequence endMessage = substring(message[0], index + 3 + ((firstChar2 == 32 || firstChar2 == 10) ? 1 : 0), message[0].length());
                    if (startMessage.length() != 0) {
                        startMessage = AndroidUtilities.concat(startMessage, "\n");
                    } else {
                        replacedFirst = true;
                    }
                    if (endMessage.length() != 0) {
                        c = 1;
                        endMessage = AndroidUtilities.concat("\n", endMessage);
                    } else {
                        c = 1;
                    }
                    if (!TextUtils.isEmpty(content)) {
                        CharSequence[] charSequenceArr = new CharSequence[3];
                        charSequenceArr[0] = startMessage;
                        charSequenceArr[c] = content;
                        charSequenceArr[2] = endMessage;
                        message[0] = AndroidUtilities.concat(charSequenceArr);
                        TLRPC.TL_messageEntityPre entity = new TLRPC.TL_messageEntityPre();
                        entity.offset = (replacedFirst ? 0 : 1) + start2;
                        entity.length = ((index - start2) - 3) + (replacedFirst ? 0 : 1);
                        entity.language = "";
                        entities2.add(entity);
                        lastIndex3 -= 6;
                    }
                } else if (start2 + 1 != index) {
                    message[0] = AndroidUtilities.concat(substring(message[0], 0, start2), substring(message[0], start2 + 1, index), substring(message[0], index + 1, message[0].length()));
                    TLRPC.TL_messageEntityCode entity2 = new TLRPC.TL_messageEntityCode();
                    entity2.offset = start2;
                    entity2.length = (index - start2) - 1;
                    entities2.add(entity2);
                    lastIndex3 -= 2;
                }
                lastIndex2 = lastIndex3;
                start2 = -1;
                isPre = false;
            }
        }
        if (start2 != -1 && isPre) {
            message[0] = AndroidUtilities.concat(substring(message[0], 0, start2), substring(message[0], start2 + 2, message[0].length()));
            if (entities2 == null) {
                entities2 = new ArrayList<>();
            }
            TLRPC.TL_messageEntityCode entity3 = new TLRPC.TL_messageEntityCode();
            entity3.offset = start2;
            entity3.length = 1;
            entities2.add(entity3);
        }
        if (message[0] instanceof Spanned) {
            Spanned spannable = (Spanned) message[0];
            TextStyleSpan[] spans = (TextStyleSpan[]) spannable.getSpans(0, message[0].length(), TextStyleSpan.class);
            if (spans != null && spans.length > 0) {
                for (TextStyleSpan span : spans) {
                    int spanStart = spannable.getSpanStart(span);
                    int spanEnd = spannable.getSpanEnd(span);
                    if (!checkInclusion(spanStart, entities2, false) && !checkInclusion(spanEnd, entities2, true) && !checkIntersection(spanStart, spanEnd, entities2)) {
                        if (entities2 == null) {
                            entities2 = new ArrayList<>();
                        }
                        addStyle(span.getStyleFlags(), spanStart, spanEnd, entities2);
                    }
                }
            }
            URLSpanUserMention[] spansMentions = (URLSpanUserMention[]) spannable.getSpans(0, message[0].length(), URLSpanUserMention.class);
            if (spansMentions != null && spansMentions.length > 0) {
                if (entities2 == null) {
                    entities2 = new ArrayList<>();
                }
                int b = 0;
                while (b < spansMentions.length) {
                    TLRPC.TL_inputMessageEntityMentionName entity4 = new TLRPC.TL_inputMessageEntityMentionName();
                    ArrayList<TLRPC.MessageEntity> entities3 = entities2;
                    entity4.user_id = getMessagesController().getInputUser(Utilities.parseLong(spansMentions[b].getURL()).longValue());
                    if (entity4.user_id == null) {
                        entities = entities3;
                    } else {
                        entity4.offset = spannable.getSpanStart(spansMentions[b]);
                        entity4.length = Math.min(spannable.getSpanEnd(spansMentions[b]), message[0].length()) - entity4.offset;
                        if (message[0].charAt((entity4.offset + entity4.length) - 1) == ' ') {
                            entity4.length--;
                        }
                        entities = entities3;
                        entities.add(entity4);
                    }
                    b++;
                    entities2 = entities;
                }
            }
            URLSpanReplacement[] spansUrlReplacement2 = (URLSpanReplacement[]) spannable.getSpans(0, message[0].length(), URLSpanReplacement.class);
            if (spansUrlReplacement2 != null && spansUrlReplacement2.length > 0) {
                if (entities2 == null) {
                    entities2 = new ArrayList<>();
                }
                int b2 = 0;
                while (b2 < spansUrlReplacement2.length) {
                    TLRPC.TL_messageEntityTextUrl entity5 = new TLRPC.TL_messageEntityTextUrl();
                    entity5.offset = spannable.getSpanStart(spansUrlReplacement2[b2]);
                    entity5.length = Math.min(spannable.getSpanEnd(spansUrlReplacement2[b2]), message[0].length()) - entity5.offset;
                    entity5.url = spansUrlReplacement2[b2].getURL();
                    entities2.add(entity5);
                    TextStyleSpan.TextStyleRun style = spansUrlReplacement2[b2].getTextStyleRun();
                    if (style == null) {
                        spansUrlReplacement = spansUrlReplacement2;
                        start = start2;
                        lastIndex = lastIndex2;
                    } else {
                        int i = style.flags;
                        spansUrlReplacement = spansUrlReplacement2;
                        int i2 = entity5.offset;
                        start = start2;
                        int start3 = entity5.offset;
                        lastIndex = lastIndex2;
                        int lastIndex4 = entity5.length;
                        addStyle(i, i2, start3 + lastIndex4, entities2);
                    }
                    b2++;
                    spansUrlReplacement2 = spansUrlReplacement;
                    start2 = start;
                    lastIndex2 = lastIndex;
                }
            }
            if (spannable instanceof Spannable) {
                AndroidUtilities.addLinks((Spannable) spannable, 1);
                URLSpan[] spansUrl = (URLSpan[]) spannable.getSpans(0, message[0].length(), URLSpan.class);
                if (spansUrl != null && spansUrl.length > 0) {
                    if (entities2 == null) {
                        entities2 = new ArrayList<>();
                    }
                    for (int b3 = 0; b3 < spansUrl.length; b3++) {
                        if (!(spansUrl[b3] instanceof URLSpanReplacement) && !(spansUrl[b3] instanceof URLSpanUserMention)) {
                            TLRPC.TL_messageEntityUrl entity6 = new TLRPC.TL_messageEntityUrl();
                            entity6.offset = spannable.getSpanStart(spansUrl[b3]);
                            entity6.length = Math.min(spannable.getSpanEnd(spansUrl[b3]), message[0].length()) - entity6.offset;
                            entity6.url = spansUrl[b3].getURL();
                            entities2.add(entity6);
                        }
                    }
                }
            }
        }
        CharSequence cs = message[0];
        if (entities2 == null) {
            entities2 = new ArrayList<>();
        }
        CharSequence cs2 = parsePattern(parsePattern(parsePattern(cs, BOLD_PATTERN, entities2, MediaDataController$$ExternalSyntheticLambda32.INSTANCE), ITALIC_PATTERN, entities2, MediaDataController$$ExternalSyntheticLambda34.INSTANCE), SPOILER_PATTERN, entities2, MediaDataController$$ExternalSyntheticLambda35.INSTANCE);
        if (allowStrike) {
            cs2 = parsePattern(cs2, STRIKE_PATTERN, entities2, MediaDataController$$ExternalSyntheticLambda36.INSTANCE);
        }
        message[0] = cs2;
        return entities2;
    }

    public static /* synthetic */ TLRPC.MessageEntity lambda$getEntities$139(Void obj) {
        return new TLRPC.TL_messageEntityBold();
    }

    public static /* synthetic */ TLRPC.MessageEntity lambda$getEntities$140(Void obj) {
        return new TLRPC.TL_messageEntityItalic();
    }

    public static /* synthetic */ TLRPC.MessageEntity lambda$getEntities$141(Void obj) {
        return new TLRPC.TL_messageEntitySpoiler();
    }

    public static /* synthetic */ TLRPC.MessageEntity lambda$getEntities$142(Void obj) {
        return new TLRPC.TL_messageEntityStrike();
    }

    private CharSequence parsePattern(CharSequence cs, Pattern pattern, List<TLRPC.MessageEntity> entities, GenericProvider<Void, TLRPC.MessageEntity> entityProvider) {
        URLSpan[] spansUrl;
        Matcher m = pattern.matcher(cs);
        int offset = 0;
        while (m.find()) {
            String gr = m.group(1);
            boolean allowEntity = true;
            if ((cs instanceof Spannable) && (spansUrl = (URLSpan[]) ((Spannable) cs).getSpans(m.start() - offset, m.end() - offset, URLSpan.class)) != null && spansUrl.length > 0) {
                allowEntity = false;
            }
            if (allowEntity) {
                cs = ((Object) cs.subSequence(0, m.start() - offset)) + gr + ((Object) cs.subSequence(m.end() - offset, cs.length()));
                TLRPC.MessageEntity entity = entityProvider.provide(null);
                entity.offset = m.start() - offset;
                entity.length = gr.length();
                entities.add(entity);
            }
            offset += (m.end() - m.start()) - gr.length();
        }
        return cs;
    }

    public void loadDraftsIfNeed() {
        if (getUserConfig().draftsLoaded || this.loadingDrafts) {
            return;
        }
        this.loadingDrafts = true;
        getConnectionsManager().sendRequest(new TLRPC.TL_messages_getAllDrafts(), new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda39
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m458x54c0aabb(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadDraftsIfNeed$145$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m458x54c0aabb(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda153
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m456xcee7d7fd();
                }
            });
            return;
        }
        getMessagesController().processUpdates((TLRPC.Updates) response, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda164
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m457x91d4415c();
            }
        });
    }

    /* renamed from: lambda$loadDraftsIfNeed$143$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m456xcee7d7fd() {
        this.loadingDrafts = false;
    }

    /* renamed from: lambda$loadDraftsIfNeed$144$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m457x91d4415c() {
        this.loadingDrafts = false;
        UserConfig userConfig = getUserConfig();
        userConfig.draftsLoaded = true;
        userConfig.saveConfig(false);
    }

    public int getDraftFolderId(long dialogId) {
        return this.draftsFolderIds.get(dialogId, 0).intValue();
    }

    public void setDraftFolderId(long dialogId, int folderId) {
        this.draftsFolderIds.put(dialogId, Integer.valueOf(folderId));
    }

    public void clearDraftsFolderIds() {
        this.draftsFolderIds.clear();
    }

    public LongSparseArray<SparseArray<TLRPC.DraftMessage>> getDrafts() {
        return this.drafts;
    }

    public TLRPC.DraftMessage getDraft(long dialogId, int threadId) {
        SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(dialogId);
        if (threads == null) {
            return null;
        }
        return threads.get(threadId);
    }

    public TLRPC.Message getDraftMessage(long dialogId, int threadId) {
        SparseArray<TLRPC.Message> threads = this.draftMessages.get(dialogId);
        if (threads == null) {
            return null;
        }
        return threads.get(threadId);
    }

    public void saveDraft(long dialogId, int threadId, CharSequence message, ArrayList<TLRPC.MessageEntity> entities, TLRPC.Message replyToMessage, boolean noWebpage) {
        saveDraft(dialogId, threadId, message, entities, replyToMessage, noWebpage, false);
    }

    public void saveDraft(long dialogId, int threadId, CharSequence message, ArrayList<TLRPC.MessageEntity> entities, TLRPC.Message replyToMessage, boolean noWebpage, boolean clean) {
        TLRPC.DraftMessage draftMessage;
        if (!TextUtils.isEmpty(message) || replyToMessage != null) {
            draftMessage = new TLRPC.TL_draftMessage();
        } else {
            draftMessage = new TLRPC.TL_draftMessageEmpty();
        }
        draftMessage.date = (int) (System.currentTimeMillis() / 1000);
        draftMessage.message = message == null ? "" : message.toString();
        draftMessage.no_webpage = noWebpage;
        if (replyToMessage != null) {
            draftMessage.reply_to_msg_id = replyToMessage.id;
            draftMessage.flags |= 1;
        }
        if (entities != null && !entities.isEmpty()) {
            draftMessage.entities = entities;
            draftMessage.flags |= 8;
        }
        SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(dialogId);
        TLRPC.DraftMessage currentDraft = threads == null ? null : threads.get(threadId);
        if (!clean) {
            if (currentDraft == null || !currentDraft.message.equals(draftMessage.message) || currentDraft.reply_to_msg_id != draftMessage.reply_to_msg_id || currentDraft.no_webpage != draftMessage.no_webpage) {
                if (currentDraft == null && TextUtils.isEmpty(draftMessage.message) && draftMessage.reply_to_msg_id == 0) {
                    return;
                }
            } else {
                return;
            }
        }
        saveDraft(dialogId, threadId, draftMessage, replyToMessage, false);
        if (threadId == 0) {
            if (!DialogObject.isEncryptedDialog(dialogId)) {
                TLRPC.TL_messages_saveDraft req = new TLRPC.TL_messages_saveDraft();
                req.peer = getMessagesController().getInputPeer(dialogId);
                if (req.peer == null) {
                    return;
                }
                req.message = draftMessage.message;
                req.no_webpage = draftMessage.no_webpage;
                req.reply_to_msg_id = draftMessage.reply_to_msg_id;
                req.entities = draftMessage.entities;
                req.flags = draftMessage.flags;
                getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda83.INSTANCE);
            }
            getMessagesController().sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public static /* synthetic */ void lambda$saveDraft$146(TLObject response, TLRPC.TL_error error) {
    }

    public void saveDraft(final long dialogId, final int threadId, TLRPC.DraftMessage draft, TLRPC.Message replyToMessage, boolean fromServer) {
        TLRPC.User user;
        TLRPC.Chat chat;
        String str;
        String str2;
        SharedPreferences.Editor editor = this.draftPreferences.edit();
        MessagesController messagesController = getMessagesController();
        if (draft == null || (draft instanceof TLRPC.TL_draftMessageEmpty)) {
            SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(dialogId);
            if (threads != null) {
                threads.remove(threadId);
                if (threads.size() == 0) {
                    this.drafts.remove(dialogId);
                }
            }
            SparseArray<TLRPC.Message> threads2 = this.draftMessages.get(dialogId);
            if (threads2 != null) {
                threads2.remove(threadId);
                if (threads2.size() == 0) {
                    this.draftMessages.remove(dialogId);
                }
            }
            if (threadId == 0) {
                this.draftPreferences.edit().remove("" + dialogId).remove("r_" + dialogId).commit();
            } else {
                this.draftPreferences.edit().remove("t_" + dialogId + "_" + threadId).remove("rt_" + dialogId + "_" + threadId).commit();
            }
            messagesController.removeDraftDialogIfNeed(dialogId);
        } else {
            SparseArray<TLRPC.DraftMessage> threads3 = this.drafts.get(dialogId);
            if (threads3 == null) {
                threads3 = new SparseArray<>();
                this.drafts.put(dialogId, threads3);
            }
            threads3.put(threadId, draft);
            if (threadId == 0) {
                messagesController.putDraftDialogIfNeed(dialogId, draft);
            }
            try {
                SerializedData serializedData = new SerializedData(draft.getObjectSize());
                draft.serializeToStream(serializedData);
                if (threadId == 0) {
                    str2 = "" + dialogId;
                } else {
                    str2 = "t_" + dialogId + "_" + threadId;
                }
                editor.putString(str2, Utilities.bytesToHex(serializedData.toByteArray()));
                serializedData.cleanup();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        SparseArray<TLRPC.Message> threads4 = this.draftMessages.get(dialogId);
        if (replyToMessage == null) {
            if (threads4 != null) {
                threads4.remove(threadId);
                if (threads4.size() == 0) {
                    this.draftMessages.remove(dialogId);
                }
            }
            if (threadId == 0) {
                editor.remove("r_" + dialogId);
            } else {
                editor.remove("rt_" + dialogId + "_" + threadId);
            }
        } else {
            if (threads4 == null) {
                threads4 = new SparseArray<>();
                this.draftMessages.put(dialogId, threads4);
            }
            threads4.put(threadId, replyToMessage);
            SerializedData serializedData2 = new SerializedData(replyToMessage.getObjectSize());
            replyToMessage.serializeToStream(serializedData2);
            if (threadId == 0) {
                str = "r_" + dialogId;
            } else {
                str = "rt_" + dialogId + "_" + threadId;
            }
            editor.putString(str, Utilities.bytesToHex(serializedData2.toByteArray()));
            serializedData2.cleanup();
        }
        editor.commit();
        if (fromServer && threadId == 0) {
            if (draft != null && draft.reply_to_msg_id != 0 && replyToMessage == null) {
                if (!DialogObject.isUserDialog(dialogId)) {
                    user = null;
                    chat = getMessagesController().getChat(Long.valueOf(-dialogId));
                } else {
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(dialogId));
                    user = user2;
                    chat = null;
                }
                if (user != null || chat != null) {
                    final long channelId = ChatObject.isChannel(chat) ? chat.id : 0L;
                    final int messageId = draft.reply_to_msg_id;
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda90
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaDataController.this.m547lambda$saveDraft$149$orgtelegrammessengerMediaDataController(messageId, dialogId, channelId, threadId);
                        }
                    });
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(dialogId));
        }
    }

    /* renamed from: lambda$saveDraft$149$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m547lambda$saveDraft$149$orgtelegrammessengerMediaDataController(int messageId, final long dialogId, long channelId, final int threadId) {
        NativeByteBuffer data;
        TLRPC.Message message = null;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE mid = %d and uid = %d", Integer.valueOf(messageId), Long.valueOf(dialogId)), new Object[0]);
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                message.readAttachPath(data, getUserConfig().clientUserId);
                data.reuse();
            }
            cursor.dispose();
            if (message == null) {
                if (channelId != 0) {
                    TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
                    req.channel = getMessagesController().getInputChannel(channelId);
                    req.id.add(Integer.valueOf(messageId));
                    getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda54
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MediaDataController.this.m545lambda$saveDraft$147$orgtelegrammessengerMediaDataController(dialogId, threadId, tLObject, tL_error);
                        }
                    });
                    return;
                }
                TLRPC.TL_messages_getMessages req2 = new TLRPC.TL_messages_getMessages();
                req2.id.add(Integer.valueOf(messageId));
                getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda56
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.m546lambda$saveDraft$148$orgtelegrammessengerMediaDataController(dialogId, threadId, tLObject, tL_error);
                    }
                });
                return;
            }
            saveDraftReplyMessage(dialogId, threadId, message);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$saveDraft$147$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m545lambda$saveDraft$147$orgtelegrammessengerMediaDataController(long dialogId, int threadId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            if (!messagesRes.messages.isEmpty()) {
                saveDraftReplyMessage(dialogId, threadId, messagesRes.messages.get(0));
            }
        }
    }

    /* renamed from: lambda$saveDraft$148$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m546lambda$saveDraft$148$orgtelegrammessengerMediaDataController(long dialogId, int threadId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            if (!messagesRes.messages.isEmpty()) {
                saveDraftReplyMessage(dialogId, threadId, messagesRes.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(final long dialogId, final int threadId, final TLRPC.Message message) {
        if (message == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda108
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m548x2da16e25(dialogId, threadId, message);
            }
        });
    }

    /* renamed from: lambda$saveDraftReplyMessage$150$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m548x2da16e25(long dialogId, int threadId, TLRPC.Message message) {
        StringBuilder sb;
        SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(dialogId);
        TLRPC.DraftMessage draftMessage = threads != null ? threads.get(threadId) : null;
        if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
            SparseArray<TLRPC.Message> threads2 = this.draftMessages.get(dialogId);
            if (threads2 == null) {
                threads2 = new SparseArray<>();
                this.draftMessages.put(dialogId, threads2);
            }
            threads2.put(threadId, message);
            SerializedData serializedData = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData);
            SharedPreferences.Editor edit = this.draftPreferences.edit();
            if (threadId == 0) {
                sb = new StringBuilder();
                sb.append("r_");
                sb.append(dialogId);
            } else {
                sb = new StringBuilder();
                sb.append("rt_");
                sb.append(dialogId);
                sb.append("_");
                sb.append(threadId);
            }
            edit.putString(sb.toString(), Utilities.bytesToHex(serializedData.toByteArray())).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(dialogId));
            serializedData.cleanup();
        }
    }

    public void clearAllDrafts(boolean notify) {
        this.drafts.clear();
        this.draftMessages.clear();
        this.draftsFolderIds.clear();
        this.draftPreferences.edit().clear().commit();
        if (notify) {
            getMessagesController().sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void cleanDraft(long dialogId, int threadId, boolean replyOnly) {
        SparseArray<TLRPC.DraftMessage> threads2 = this.drafts.get(dialogId);
        TLRPC.DraftMessage draftMessage = threads2 != null ? threads2.get(threadId) : null;
        if (draftMessage == null) {
            return;
        }
        if (!replyOnly) {
            SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(dialogId);
            if (threads != null) {
                threads.remove(threadId);
                if (threads.size() == 0) {
                    this.drafts.remove(dialogId);
                }
            }
            SparseArray<TLRPC.Message> threads3 = this.draftMessages.get(dialogId);
            if (threads3 != null) {
                threads3.remove(threadId);
                if (threads3.size() == 0) {
                    this.draftMessages.remove(dialogId);
                }
            }
            if (threadId == 0) {
                this.draftPreferences.edit().remove("" + dialogId).remove("r_" + dialogId).commit();
                getMessagesController().sortDialogs(null);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                return;
            }
            this.draftPreferences.edit().remove("t_" + dialogId + "_" + threadId).remove("rt_" + dialogId + "_" + threadId).commit();
        } else if (draftMessage.reply_to_msg_id != 0) {
            draftMessage.reply_to_msg_id = 0;
            draftMessage.flags &= -2;
            saveDraft(dialogId, threadId, draftMessage.message, draftMessage.entities, null, draftMessage.no_webpage, true);
        }
    }

    public void beginTransaction() {
        this.inTransaction = true;
    }

    public void endTransaction() {
        this.inTransaction = false;
    }

    public void clearBotKeyboard(final long dialogId, final ArrayList<Integer> messages) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda140
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m425xc1dcc7aa(messages, dialogId);
            }
        });
    }

    /* renamed from: lambda$clearBotKeyboard$151$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m425xc1dcc7aa(ArrayList messages, long dialogId) {
        if (messages != null) {
            for (int a = 0; a < messages.size(); a++) {
                long did1 = this.botKeyboardsByMids.get(((Integer) messages.get(a)).intValue());
                if (did1 != 0) {
                    this.botKeyboards.remove(did1);
                    this.botKeyboardsByMids.delete(((Integer) messages.get(a)).intValue());
                    getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(did1));
                }
            }
            return;
        }
        this.botKeyboards.remove(dialogId);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(dialogId));
    }

    public void loadBotKeyboard(final long dialogId) {
        TLRPC.Message keyboard = this.botKeyboards.get(dialogId);
        if (keyboard != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, keyboard, Long.valueOf(dialogId));
        } else {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda99
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m455x6409491b(dialogId);
                }
            });
        }
    }

    /* renamed from: lambda$loadBotKeyboard$153$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m455x6409491b(final long dialogId) {
        NativeByteBuffer data;
        TLRPC.Message botKeyboard = null;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", Long.valueOf(dialogId)), new Object[0]);
            if (cursor.next() && !cursor.isNull(0) && (data = cursor.byteBufferValue(0)) != null) {
                botKeyboard = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
            }
            cursor.dispose();
            if (botKeyboard != null) {
                final TLRPC.Message botKeyboardFinal = botKeyboard;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda163
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m454xa11cdfbc(botKeyboardFinal, dialogId);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadBotKeyboard$152$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m454xa11cdfbc(TLRPC.Message botKeyboardFinal, long dialogId) {
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, botKeyboardFinal, Long.valueOf(dialogId));
    }

    private TLRPC.BotInfo loadBotInfoInternal(long uid, long dialogId) throws SQLiteException {
        NativeByteBuffer data;
        TLRPC.BotInfo botInfo = null;
        SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info_v2 WHERE uid = %d AND dialogId = %d", Long.valueOf(uid), Long.valueOf(dialogId)), new Object[0]);
        if (cursor.next() && !cursor.isNull(0) && (data = cursor.byteBufferValue(0)) != null) {
            botInfo = TLRPC.BotInfo.TLdeserialize(data, data.readInt32(false), false);
            data.reuse();
        }
        cursor.dispose();
        return botInfo;
    }

    public void loadBotInfo(final long uid, final long dialogId, boolean cache, final int classGuid) {
        if (cache) {
            HashMap<String, TLRPC.BotInfo> hashMap = this.botInfos;
            TLRPC.BotInfo botInfo = hashMap.get(uid + "_" + dialogId);
            if (botInfo != null) {
                getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(classGuid));
                return;
            }
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda110
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m453x79b07e32(uid, dialogId, classGuid);
            }
        });
    }

    /* renamed from: lambda$loadBotInfo$155$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m453x79b07e32(long uid, long dialogId, final int classGuid) {
        try {
            final TLRPC.BotInfo botInfo = loadBotInfoInternal(uid, dialogId);
            if (botInfo != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda159
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m452xb6c414d3(botInfo, classGuid);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadBotInfo$154$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m452xb6c414d3(TLRPC.BotInfo botInfo, int classGuid) {
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(classGuid));
    }

    public void putBotKeyboard(final long dialogId, final TLRPC.Message message) {
        if (message == null) {
            return;
        }
        int mid = 0;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", Long.valueOf(dialogId)), new Object[0]);
            if (cursor.next()) {
                mid = cursor.intValue(0);
            }
            cursor.dispose();
            if (mid >= message.id) {
                return;
            }
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
            message.serializeToStream(data);
            state.bindLong(1, dialogId);
            state.bindInteger(2, message.id);
            state.bindByteBuffer(3, data);
            state.step();
            data.reuse();
            state.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda116
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m530x4f7e6987(dialogId, message);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$putBotKeyboard$156$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m530x4f7e6987(long dialogId, TLRPC.Message message) {
        TLRPC.Message old = this.botKeyboards.get(dialogId);
        this.botKeyboards.put(dialogId, message);
        long channelId = MessageObject.getChannelId(message);
        if (channelId == 0) {
            if (old != null) {
                this.botKeyboardsByMids.delete(old.id);
            }
            this.botKeyboardsByMids.put(message.id, dialogId);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(dialogId));
    }

    public void putBotInfo(final long dialogId, final TLRPC.BotInfo botInfo) {
        if (botInfo == null) {
            return;
        }
        HashMap<String, TLRPC.BotInfo> hashMap = this.botInfos;
        hashMap.put(botInfo.user_id + "_" + dialogId, botInfo);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda160
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m529lambda$putBotInfo$157$orgtelegrammessengerMediaDataController(botInfo, dialogId);
            }
        });
    }

    /* renamed from: lambda$putBotInfo$157$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m529lambda$putBotInfo$157$orgtelegrammessengerMediaDataController(TLRPC.BotInfo botInfo, long dialogId) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info_v2 VALUES(?, ?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(botInfo.getObjectSize());
            botInfo.serializeToStream(data);
            state.bindLong(1, botInfo.user_id);
            state.bindLong(2, dialogId);
            state.bindByteBuffer(3, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateBotInfo(final long dialogId, final TLRPC.TL_updateBotCommands update) {
        HashMap<String, TLRPC.BotInfo> hashMap = this.botInfos;
        TLRPC.BotInfo botInfo = hashMap.get(update.bot_id + "_" + dialogId);
        if (botInfo != null) {
            botInfo.commands = update.commands;
            getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, 0);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m566xf7a6bab2(update, dialogId);
            }
        });
    }

    /* renamed from: lambda$updateBotInfo$158$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m566xf7a6bab2(TLRPC.TL_updateBotCommands update, long dialogId) {
        try {
            TLRPC.BotInfo info = loadBotInfoInternal(update.bot_id, dialogId);
            if (info != null) {
                info.commands = update.commands;
            }
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info_v2 VALUES(?, ?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(info.getObjectSize());
            info.serializeToStream(data);
            state.bindLong(1, info.user_id);
            state.bindLong(2, dialogId);
            state.bindByteBuffer(3, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public HashMap<String, TLRPC.TL_availableReaction> getReactionsMap() {
        return this.reactionsMap;
    }

    public String getDoubleTapReaction() {
        String str = this.doubleTapReaction;
        if (str != null) {
            return str;
        }
        if (getReactionsList().isEmpty()) {
            return null;
        }
        String savedReaction = MessagesController.getEmojiSettings(this.currentAccount).getString("reaction_on_double_tap", null);
        if (savedReaction != null && getReactionsMap().get(savedReaction) != null) {
            this.doubleTapReaction = savedReaction;
            return savedReaction;
        }
        return getReactionsList().get(0).reaction;
    }

    public void setDoubleTapReaction(String reaction) {
        MessagesController.getEmojiSettings(this.currentAccount).edit().putString("reaction_on_double_tap", reaction).apply();
        this.doubleTapReaction = reaction;
    }

    public List<TLRPC.TL_availableReaction> getEnabledReactionsList() {
        return this.enabledReactionsList;
    }

    public void uploadRingtone(String filePath) {
        if (this.ringtoneUploaderHashMap.containsKey(filePath)) {
            return;
        }
        this.ringtoneUploaderHashMap.put(filePath, new RingtoneUploader(filePath, this.currentAccount));
        this.ringtoneDataStore.addUploadingTone(filePath);
    }

    public void onRingtoneUploaded(String filePath, TLRPC.Document document, boolean error) {
        this.ringtoneUploaderHashMap.remove(filePath);
        this.ringtoneDataStore.onRingtoneUploaded(filePath, document, error);
    }

    public void checkRingtones() {
        this.ringtoneDataStore.m1261lambda$new$0$orgtelegrammessengerringtoneRingtoneDataStore();
    }

    public boolean saveToRingtones(final TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        if (this.ringtoneDataStore.contains(document.id)) {
            return true;
        }
        if (document.size > MessagesController.getInstance(this.currentAccount).ringtoneSizeMax) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLargeError", org.telegram.messenger.beta.R.string.TooLargeError, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", org.telegram.messenger.beta.R.string.ErrorRingtoneSizeTooBig, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax / 1024)));
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if ((attribute instanceof TLRPC.TL_documentAttributeAudio) && attribute.duration > MessagesController.getInstance(this.currentAccount).ringtoneDurationMax) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLongError", org.telegram.messenger.beta.R.string.TooLongError, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", org.telegram.messenger.beta.R.string.ErrorRingtoneDurationTooLong, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax)));
                return false;
            }
        }
        TLRPC.TL_account_saveRingtone saveRingtone = new TLRPC.TL_account_saveRingtone();
        saveRingtone.id = new TLRPC.TL_inputDocument();
        saveRingtone.id.id = document.id;
        saveRingtone.id.file_reference = document.file_reference;
        saveRingtone.id.access_hash = document.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(saveRingtone, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda72
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m553x9c191a4e(document, tLObject, tL_error);
            }
        });
        return true;
    }

    /* renamed from: lambda$saveToRingtones$160$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m553x9c191a4e(final TLRPC.Document document, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda157
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m552xdbc80c24(response, document);
            }
        });
    }

    /* renamed from: lambda$saveToRingtones$159$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m552xdbc80c24(TLObject response, TLRPC.Document document) {
        if (response != null) {
            if (response instanceof TLRPC.TL_account_savedRingtoneConverted) {
                this.ringtoneDataStore.addTone(((TLRPC.TL_account_savedRingtoneConverted) response).document);
            } else {
                this.ringtoneDataStore.addTone(document);
            }
        }
    }

    public void preloadPremiumPreviewStickers() {
        if (this.previewStickersLoading || !this.premiumPreviewStickers.isEmpty()) {
            int i = 0;
            while (i < Math.min(this.premiumPreviewStickers.size(), 3)) {
                ArrayList<TLRPC.Document> arrayList = this.premiumPreviewStickers;
                TLRPC.Document document = arrayList.get(i == 2 ? arrayList.size() - 1 : i);
                if (MessageObject.isPremiumSticker(document)) {
                    ImageReceiver imageReceiver = new ImageReceiver();
                    imageReceiver.setImage(ImageLocation.getForDocument(document), null, null, "webp", null, 1);
                    ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
                    ImageReceiver imageReceiver2 = new ImageReceiver();
                    imageReceiver2.setImage(ImageLocation.getForDocument(MessageObject.getPremiumStickerAnimation(document), document), (String) null, (ImageLocation) null, (String) null, "tgs", (Object) null, 1);
                    ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver2);
                }
                i++;
            }
            return;
        }
        TLRPC.TL_messages_getStickers req2 = new TLRPC.TL_messages_getStickers();
        req2.emoticon = Emoji.fixEmoji("⭐") + Emoji.fixEmoji("⭐");
        req2.hash = 0L;
        this.previewStickersLoading = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda45
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m505xb33623b5(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$preloadPremiumPreviewStickers$162$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m505xb33623b5(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda173
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m504xf049ba56(error, response);
            }
        });
    }

    /* renamed from: lambda$preloadPremiumPreviewStickers$161$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m504xf049ba56(TLRPC.TL_error error, TLObject response) {
        if (error != null) {
            return;
        }
        this.previewStickersLoading = false;
        TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
        this.premiumPreviewStickers.clear();
        this.premiumPreviewStickers.addAll(res.stickers);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.premiumStickersPreviewLoaded, new Object[0]);
    }

    public void chekAllMedia(boolean force) {
        if (force) {
            this.reactionsUpdateDate = 0;
            this.loadFeaturedDate = 0;
        }
        loadRecents(2, false, true, false);
        loadRecents(3, false, true, false);
        checkFeaturedStickers();
        checkReactions();
        checkMenuBots();
        checkPremiumPromo();
    }

    public void fetchNewEmojiKeywords(String[] langCodes) {
        if (langCodes == null) {
            return;
        }
        for (final String langCode : langCodes) {
            if (TextUtils.isEmpty(langCode) || this.currentFetchingEmoji.get(langCode) != null) {
                return;
            }
            this.currentFetchingEmoji.put(langCode, true);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda128
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m436x580d5f55(langCode);
                }
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$fetchNewEmojiKeywords$168$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m436x580d5f55(final String langCode) {
        TLObject request;
        int version = -1;
        String alias = null;
        long date = 0;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT alias, version, date FROM emoji_keywords_info_v2 WHERE lang = ?", langCode);
            if (cursor.next()) {
                alias = cursor.stringValue(0);
                version = cursor.intValue(1);
                date = cursor.longValue(2);
            }
            cursor.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!BuildVars.DEBUG_VERSION && Math.abs(System.currentTimeMillis() - date) < 3600000) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda124
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m431x896f507a(langCode);
                }
            });
            return;
        }
        if (version == -1) {
            TLRPC.TL_messages_getEmojiKeywords req = new TLRPC.TL_messages_getEmojiKeywords();
            req.lang_code = langCode;
            request = req;
        } else {
            TLRPC.TL_messages_getEmojiKeywordsDifference req2 = new TLRPC.TL_messages_getEmojiKeywordsDifference();
            req2.lang_code = langCode;
            req2.from_version = version;
            request = req2;
        }
        final String aliasFinal = alias;
        final int versionFinal = version;
        getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda51
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.m435x9520f5f6(versionFinal, aliasFinal, langCode, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$163$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m431x896f507a(String langCode) {
        this.currentFetchingEmoji.remove(langCode);
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$167$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m435x9520f5f6(int versionFinal, String aliasFinal, final String langCode, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_emojiKeywordsDifference res = (TLRPC.TL_emojiKeywordsDifference) response;
            if (versionFinal != -1 && !res.lang_code.equals(aliasFinal)) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda126
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.m433xf482338(langCode);
                    }
                });
                return;
            } else {
                putEmojiKeywords(langCode, res);
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda127
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m434xd2348c97(langCode);
            }
        });
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$165$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m433xf482338(final String langCode) {
        try {
            SQLitePreparedStatement deleteState = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            deleteState.bindString(1, langCode);
            deleteState.step();
            deleteState.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda125
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m432x4c5bb9d9(langCode);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$164$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m432x4c5bb9d9(String langCode) {
        this.currentFetchingEmoji.remove(langCode);
        fetchNewEmojiKeywords(new String[]{langCode});
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$166$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m434xd2348c97(String langCode) {
        this.currentFetchingEmoji.remove(langCode);
    }

    private void putEmojiKeywords(final String lang, final TLRPC.TL_emojiKeywordsDifference res) {
        if (res == null) {
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda172
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m533xee6fc64d(res, lang);
            }
        });
    }

    /* renamed from: lambda$putEmojiKeywords$170$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m533xee6fc64d(TLRPC.TL_emojiKeywordsDifference res, final String lang) {
        try {
            if (!res.keywords.isEmpty()) {
                SQLitePreparedStatement insertState = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_v2 VALUES(?, ?, ?)");
                SQLitePreparedStatement deleteState = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_v2 WHERE lang = ? AND keyword = ? AND emoji = ?");
                getMessagesStorage().getDatabase().beginTransaction();
                int N = res.keywords.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.EmojiKeyword keyword = res.keywords.get(a);
                    if (keyword instanceof TLRPC.TL_emojiKeyword) {
                        TLRPC.TL_emojiKeyword emojiKeyword = (TLRPC.TL_emojiKeyword) keyword;
                        String key = emojiKeyword.keyword.toLowerCase();
                        int N2 = emojiKeyword.emoticons.size();
                        for (int b = 0; b < N2; b++) {
                            insertState.requery();
                            insertState.bindString(1, res.lang_code);
                            insertState.bindString(2, key);
                            insertState.bindString(3, emojiKeyword.emoticons.get(b));
                            insertState.step();
                        }
                    } else if (keyword instanceof TLRPC.TL_emojiKeywordDeleted) {
                        TLRPC.TL_emojiKeywordDeleted keywordDeleted = (TLRPC.TL_emojiKeywordDeleted) keyword;
                        String key2 = keywordDeleted.keyword.toLowerCase();
                        int N22 = keywordDeleted.emoticons.size();
                        for (int b2 = 0; b2 < N22; b2++) {
                            deleteState.requery();
                            deleteState.bindString(1, res.lang_code);
                            deleteState.bindString(2, key2);
                            deleteState.bindString(3, keywordDeleted.emoticons.get(b2));
                            deleteState.step();
                        }
                    }
                }
                getMessagesStorage().getDatabase().commitTransaction();
                insertState.dispose();
                deleteState.dispose();
            }
            SQLitePreparedStatement infoState = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_info_v2 VALUES(?, ?, ?, ?)");
            infoState.bindString(1, lang);
            infoState.bindString(2, res.lang_code);
            infoState.bindInteger(3, res.version);
            infoState.bindLong(4, System.currentTimeMillis());
            infoState.step();
            infoState.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda130
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.m532x2e1eb823(lang);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$putEmojiKeywords$169$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m532x2e1eb823(String lang) {
        this.currentFetchingEmoji.remove(lang);
        getNotificationCenter().postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, lang);
    }

    public void getEmojiSuggestions(String[] langCodes, String keyword, boolean fullMatch, KeywordResultCallback callback) {
        getEmojiSuggestions(langCodes, keyword, fullMatch, callback, null);
    }

    public void getEmojiSuggestions(final String[] langCodes, final String keyword, final boolean fullMatch, final KeywordResultCallback callback, final CountDownLatch sync) {
        if (callback == null) {
            return;
        }
        if (TextUtils.isEmpty(keyword) || langCodes == null) {
            callback.run(new ArrayList<>(), null);
            return;
        }
        final ArrayList<String> recentEmoji = new ArrayList<>(Emoji.recentEmoji);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.m438xe6e6cfe5(langCodes, callback, keyword, fullMatch, recentEmoji, sync);
            }
        });
        if (sync != null) {
            try {
                sync.await();
            } catch (Throwable th) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x014f  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0156  */
    /* renamed from: lambda$getEmojiSuggestions$174$org-telegram-messenger-MediaDataController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m438xe6e6cfe5(final java.lang.String[] r19, final org.telegram.messenger.MediaDataController.KeywordResultCallback r20, java.lang.String r21, boolean r22, final java.util.ArrayList r23, java.util.concurrent.CountDownLatch r24) {
        /*
            Method dump skipped, instructions count: 351
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m438xe6e6cfe5(java.lang.String[], org.telegram.messenger.MediaDataController$KeywordResultCallback, java.lang.String, boolean, java.util.ArrayList, java.util.concurrent.CountDownLatch):void");
    }

    /* renamed from: lambda$getEmojiSuggestions$171$org-telegram-messenger-MediaDataController */
    public /* synthetic */ void m437x9e2193c8(String[] langCodes, KeywordResultCallback callback, ArrayList result) {
        for (String str : langCodes) {
            if (this.currentFetchingEmoji.get(str) != null) {
                return;
            }
        }
        callback.run(result, null);
    }

    public static /* synthetic */ int lambda$getEmojiSuggestions$172(ArrayList recentEmoji, KeywordResult o1, KeywordResult o2) {
        int idx1 = recentEmoji.indexOf(o1.emoji);
        if (idx1 < 0) {
            idx1 = Integer.MAX_VALUE;
        }
        int idx2 = recentEmoji.indexOf(o2.emoji);
        if (idx2 < 0) {
            idx2 = Integer.MAX_VALUE;
        }
        if (idx1 < idx2) {
            return -1;
        }
        if (idx1 > idx2) {
            return 1;
        }
        int len1 = o1.keyword.length();
        int len2 = o2.keyword.length();
        if (len1 < len2) {
            return -1;
        }
        if (len1 > len2) {
            return 1;
        }
        return 0;
    }

    public void loadEmojiThemes() {
        Context context = ApplicationLoader.applicationContext;
        SharedPreferences preferences = context.getSharedPreferences("emojithemes_config_" + this.currentAccount, 0);
        int count = preferences.getInt(NotificationBadge.NewHtcHomeBadger.COUNT, 0);
        ArrayList<ChatThemeBottomSheet.ChatThemeItem> previewItems = new ArrayList<>();
        previewItems.add(new ChatThemeBottomSheet.ChatThemeItem(EmojiThemes.createHomePreviewTheme()));
        for (int i = 0; i < count; i++) {
            String value = preferences.getString("theme_" + i, "");
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(value));
            try {
                TLRPC.TL_theme theme = TLRPC.Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                EmojiThemes fullTheme = EmojiThemes.createPreviewFullTheme(theme);
                if (fullTheme.items.size() >= 4) {
                    previewItems.add(new ChatThemeBottomSheet.ChatThemeItem(fullTheme));
                }
                ChatThemeController.chatThemeQueue.postRunnable(new AnonymousClass2(previewItems));
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaDataController$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 implements Runnable {
        final /* synthetic */ ArrayList val$previewItems;

        AnonymousClass2(ArrayList arrayList) {
            MediaDataController.this = this$0;
            this.val$previewItems = arrayList;
        }

        @Override // java.lang.Runnable
        public void run() {
            for (int i = 0; i < this.val$previewItems.size(); i++) {
                ((ChatThemeBottomSheet.ChatThemeItem) this.val$previewItems.get(i)).chatTheme.loadPreviewColors(0);
            }
            final ArrayList arrayList = this.val$previewItems;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.AnonymousClass2.this.m571lambda$run$0$orgtelegrammessengerMediaDataController$2(arrayList);
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-MediaDataController$2 */
        public /* synthetic */ void m571lambda$run$0$orgtelegrammessengerMediaDataController$2(ArrayList previewItems) {
            MediaDataController.this.defaultEmojiThemes.clear();
            MediaDataController.this.defaultEmojiThemes.addAll(previewItems);
        }
    }

    public void generateEmojiPreviewThemes(ArrayList<TLRPC.TL_theme> emojiPreviewThemes, int currentAccount) {
        Context context = ApplicationLoader.applicationContext;
        SharedPreferences preferences = context.getSharedPreferences("emojithemes_config_" + currentAccount, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(NotificationBadge.NewHtcHomeBadger.COUNT, emojiPreviewThemes.size());
        for (int i = 0; i < emojiPreviewThemes.size(); i++) {
            TLRPC.TL_theme tlChatTheme = emojiPreviewThemes.get(i);
            SerializedData data = new SerializedData(tlChatTheme.getObjectSize());
            tlChatTheme.serializeToStream(data);
            editor.putString("theme_" + i, Utilities.bytesToHex(data.toByteArray()));
        }
        editor.apply();
        if (!emojiPreviewThemes.isEmpty()) {
            ArrayList<ChatThemeBottomSheet.ChatThemeItem> previewItems = new ArrayList<>();
            previewItems.add(new ChatThemeBottomSheet.ChatThemeItem(EmojiThemes.createHomePreviewTheme()));
            for (int i2 = 0; i2 < emojiPreviewThemes.size(); i2++) {
                TLRPC.TL_theme theme = emojiPreviewThemes.get(i2);
                EmojiThemes chatTheme = EmojiThemes.createPreviewFullTheme(theme);
                ChatThemeBottomSheet.ChatThemeItem item = new ChatThemeBottomSheet.ChatThemeItem(chatTheme);
                if (chatTheme.items.size() >= 4) {
                    previewItems.add(item);
                }
            }
            ChatThemeController.chatThemeQueue.postRunnable(new AnonymousClass3(previewItems, currentAccount));
            return;
        }
        this.defaultEmojiThemes.clear();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiPreviewThemesChanged, new Object[0]);
    }

    /* renamed from: org.telegram.messenger.MediaDataController$3 */
    /* loaded from: classes4.dex */
    public class AnonymousClass3 implements Runnable {
        final /* synthetic */ int val$currentAccount;
        final /* synthetic */ ArrayList val$previewItems;

        AnonymousClass3(ArrayList arrayList, int i) {
            MediaDataController.this = this$0;
            this.val$previewItems = arrayList;
            this.val$currentAccount = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            for (int i = 0; i < this.val$previewItems.size(); i++) {
                ((ChatThemeBottomSheet.ChatThemeItem) this.val$previewItems.get(i)).chatTheme.loadPreviewColors(this.val$currentAccount);
            }
            final ArrayList arrayList = this.val$previewItems;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.AnonymousClass3.this.m572lambda$run$0$orgtelegrammessengerMediaDataController$3(arrayList);
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-messenger-MediaDataController$3 */
        public /* synthetic */ void m572lambda$run$0$orgtelegrammessengerMediaDataController$3(ArrayList previewItems) {
            MediaDataController.this.defaultEmojiThemes.clear();
            MediaDataController.this.defaultEmojiThemes.addAll(previewItems);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiPreviewThemesChanged, new Object[0]);
        }
    }
}
