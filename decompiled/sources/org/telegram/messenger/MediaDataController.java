package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.core.content.pm.ShortcutManagerCompat;
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
import org.telegram.messenger.Utilities;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.messenger.ringtone.RingtoneUploader;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$AttachMenuBots;
import org.telegram.tgnet.TLRPC$AttachMenuPeerType;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EmojiKeyword;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$MessagesFilter;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_account_saveRingtone;
import org.telegram.tgnet.TLRPC$TL_account_savedRingtoneConverted;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon;
import org.telegram.tgnet.TLRPC$TL_attachMenuBots;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotsNotModified;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeBotPM;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeBroadcast;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeChat;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypePM;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeSameBotPM;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channels_getMessages;
import org.telegram.tgnet.TLRPC$TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC$TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC$TL_contacts_topPeers;
import org.telegram.tgnet.TLRPC$TL_contacts_topPeersDisabled;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_draftMessage;
import org.telegram.tgnet.TLRPC$TL_draftMessageEmpty;
import org.telegram.tgnet.TLRPC$TL_emojiKeyword;
import org.telegram.tgnet.TLRPC$TL_emojiKeywordDeleted;
import org.telegram.tgnet.TLRPC$TL_emojiKeywordsDifference;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_premiumPromo;
import org.telegram.tgnet.TLRPC$TL_inputDocument;
import org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterGif;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPinned;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetAnimatedEmoji;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetDice;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetPremiumGifts;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC$TL_messageEmpty;
import org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote;
import org.telegram.tgnet.TLRPC$TL_messageEntityBold;
import org.telegram.tgnet.TLRPC$TL_messageEntityCode;
import org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageEntityPre;
import org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler;
import org.telegram.tgnet.TLRPC$TL_messageEntityStrike;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageEntityUnderline;
import org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_message_secret;
import org.telegram.tgnet.TLRPC$TL_messages_allStickers;
import org.telegram.tgnet.TLRPC$TL_messages_archivedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_availableReactions;
import org.telegram.tgnet.TLRPC$TL_messages_availableReactionsNotModified;
import org.telegram.tgnet.TLRPC$TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC$TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC$TL_messages_favedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_featuredStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getAllStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getArchivedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBots;
import org.telegram.tgnet.TLRPC$TL_messages_getAvailableReactions;
import org.telegram.tgnet.TLRPC$TL_messages_getEmojiStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getFeaturedEmojiStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getFeaturedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getSearchCounters;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC$TL_messages_readFeaturedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_recentStickers;
import org.telegram.tgnet.TLRPC$TL_messages_saveDraft;
import org.telegram.tgnet.TLRPC$TL_messages_saveGif;
import org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC$TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_messages_searchCounter;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.tgnet.TLRPC$TL_messages_toggleStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_uninstallStickerSet;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_topPeer;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC$TL_updateBotCommands;
import org.telegram.tgnet.TLRPC$Theme;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.TLRPC$messages_StickerSet;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.StickerSetBulletinLayout;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.URLSpanReplacement;
/* loaded from: classes.dex */
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
    public static final int TYPE_EMOJIPACKS = 5;
    public static final int TYPE_FAVE = 2;
    public static final int TYPE_FEATURED = 3;
    public static final int TYPE_FEATURED_EMOJIPACKS = 6;
    public static final int TYPE_GREETINGS = 3;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static RectF bitmapRect;
    private static Comparator<TLRPC$MessageEntity> entityComparator;
    private static Paint erasePaint;
    private static Paint roundPaint;
    private static Path roundPath;
    private String doubleTapReaction;
    private SharedPreferences draftPreferences;
    private TLRPC$Document greetingsSticker;
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
    private TLRPC$Chat lastSearchChat;
    private String lastSearchQuery;
    private TLRPC$User lastSearchUser;
    public boolean loadFeaturedPremium;
    boolean loaded;
    boolean loading;
    private boolean loadingDrafts;
    private boolean loadingMoreSearchMessages;
    private boolean loadingPremiumGiftStickers;
    private boolean loadingRecentGifs;
    private int menuBotsUpdateDate;
    private long menuBotsUpdateHash;
    private int mergeReqId;
    private TLRPC$TL_help_premiumPromo premiumPromo;
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
    private TLRPC$TL_attachMenuBots attachMenuBots = new TLRPC$TL_attachMenuBots();
    private List<TLRPC$TL_availableReaction> reactionsList = new ArrayList();
    private List<TLRPC$TL_availableReaction> enabledReactionsList = new ArrayList();
    private HashMap<String, TLRPC$TL_availableReaction> reactionsMap = new HashMap<>();
    private ArrayList<TLRPC$TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(0), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC$Document>[] stickersByIds = {new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>()};
    private LongSparseArray<TLRPC$TL_messages_stickerSet> stickerSetsById = new LongSparseArray<>();
    private LongSparseArray<TLRPC$TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray<>();
    private LongSparseArray<TLRPC$TL_messages_stickerSet> groupStickerSets = new LongSparseArray<>();
    private ConcurrentHashMap<String, TLRPC$TL_messages_stickerSet> stickerSetsByName = new ConcurrentHashMap<>(100, 1.0f, 1);
    private HashMap<String, TLRPC$TL_messages_stickerSet> diceStickerSetsByEmoji = new HashMap<>();
    private LongSparseArray<String> diceEmojiStickerSetsById = new LongSparseArray<>();
    private HashSet<String> loadingDiceStickerSets = new HashSet<>();
    private LongSparseArray<Runnable> removingStickerSetsUndos = new LongSparseArray<>();
    private Runnable[] scheduledLoadStickers = new Runnable[7];
    private boolean[] loadingStickers = new boolean[7];
    private boolean[] stickersLoaded = new boolean[7];
    private long[] loadHash = new long[7];
    private int[] loadDate = new int[7];
    public HashMap<String, RingtoneUploader> ringtoneUploaderHashMap = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC$Message>> verifyingMessages = new HashMap<>();
    private int[] archivedStickersCount = new int[7];
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray<>();
    private HashMap<String, ArrayList<TLRPC$Document>> allStickers = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC$Document>> allStickersFeatured = new HashMap<>();
    private ArrayList<TLRPC$Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private boolean[] loadingRecentStickers = new boolean[7];
    private boolean[] recentStickersLoaded = new boolean[7];
    private ArrayList<TLRPC$Document> recentGifs = new ArrayList<>();
    private long[] loadFeaturedHash = new long[2];
    private int[] loadFeaturedDate = new int[2];
    private ArrayList<TLRPC$StickerSetCovered>[] featuredStickerSets = {new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC$StickerSetCovered>[] featuredStickerSetsById = {new LongSparseArray<>(), new LongSparseArray<>()};
    private ArrayList<Long>[] unreadStickerSets = {new ArrayList<>(), new ArrayList<>()};
    private ArrayList<Long>[] readingStickerSets = {new ArrayList<>(), new ArrayList<>()};
    private boolean[] loadingFeaturedStickers = new boolean[2];
    private boolean[] featuredStickersLoaded = new boolean[2];
    public final ArrayList<ChatThemeBottomSheet.ChatThemeItem> defaultEmojiThemes = new ArrayList<>();
    public final ArrayList<TLRPC$Document> premiumPreviewStickers = new ArrayList<>();
    private int[] messagesSearchCount = {0, 0};
    private boolean[] messagesSearchEndReached = {false, false};
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private SparseArray<MessageObject>[] searchResultMessagesMap = {new SparseArray<>(), new SparseArray<>()};
    public ArrayList<TLRPC$TL_topPeer> hints = new ArrayList<>();
    public ArrayList<TLRPC$TL_topPeer> inlineBots = new ArrayList<>();
    private LongSparseArray<Boolean> loadingPinnedMessages = new LongSparseArray<>();
    private LongSparseArray<Integer> draftsFolderIds = new LongSparseArray<>();
    private LongSparseArray<SparseArray<TLRPC$DraftMessage>> drafts = new LongSparseArray<>();
    private LongSparseArray<SparseArray<TLRPC$Message>> draftMessages = new LongSparseArray<>();
    private HashMap<String, TLRPC$BotInfo> botInfos = new HashMap<>();
    private LongSparseArray<TLRPC$Message> botKeyboards = new LongSparseArray<>();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private HashMap<String, Boolean> currentFetchingEmoji = new HashMap<>();
    private boolean triedLoadingEmojipacks = false;

    /* loaded from: classes.dex */
    public static class KeywordResult {
        public String emoji;
        public String keyword;
    }

    /* loaded from: classes.dex */
    public interface KeywordResultCallback {
        void run(ArrayList<KeywordResult> arrayList, String str);
    }

    public static long calcHash(long j, long j2) {
        return (((j ^ (j2 >> 21)) ^ (j2 << 35)) ^ (j2 >> 4)) + j2;
    }

    public static /* synthetic */ void lambda$markFeaturedStickersAsRead$49(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$markFeaturedStickersByIdAsRead$50(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$removeInline$117(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$removePeer$118(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static /* synthetic */ void lambda$saveDraft$150(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
        entityComparator = MediaDataController$$ExternalSyntheticLambda133.INSTANCE;
    }

    public static MediaDataController getInstance(int i) {
        MediaDataController mediaDataController = Instance[i];
        if (mediaDataController == null) {
            synchronized (lockObjects) {
                mediaDataController = Instance[i];
                if (mediaDataController == null) {
                    MediaDataController[] mediaDataControllerArr = Instance;
                    MediaDataController mediaDataController2 = new MediaDataController(i);
                    mediaDataControllerArr[i] = mediaDataController2;
                    mediaDataController = mediaDataController2;
                }
            }
        }
        return mediaDataController;
    }

    public MediaDataController(int i) {
        super(i);
        String key;
        long longValue;
        SerializedData serializedData;
        boolean z;
        if (this.currentAccount == 0) {
            this.draftPreferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.draftPreferences = context.getSharedPreferences("drafts" + this.currentAccount, 0);
        }
        for (Map.Entry<String, ?> entry : this.draftPreferences.getAll().entrySet()) {
            try {
                key = entry.getKey();
                longValue = Utilities.parseLong(key).longValue();
                serializedData = new SerializedData(Utilities.hexToBytes((String) entry.getValue()));
            } catch (Exception unused) {
            }
            if (!key.startsWith("r_")) {
                z = key.startsWith("rt_");
                if (!z) {
                    TLRPC$DraftMessage TLdeserialize = TLRPC$DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (TLdeserialize != null) {
                        SparseArray<TLRPC$DraftMessage> sparseArray = this.drafts.get(longValue);
                        if (sparseArray == null) {
                            sparseArray = new SparseArray<>();
                            this.drafts.put(longValue, sparseArray);
                        }
                        sparseArray.put(key.startsWith("t_") ? Utilities.parseInt((CharSequence) key.substring(key.lastIndexOf(95) + 1)).intValue() : 0, TLdeserialize);
                    }
                    serializedData.cleanup();
                }
            } else {
                z = false;
            }
            TLRPC$Message TLdeserialize2 = TLRPC$Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
            if (TLdeserialize2 != null) {
                TLdeserialize2.readAttachPath(serializedData, getUserConfig().clientUserId);
                SparseArray<TLRPC$Message> sparseArray2 = this.draftMessages.get(longValue);
                if (sparseArray2 == null) {
                    sparseArray2 = new SparseArray<>();
                    this.draftMessages.put(longValue, sparseArray2);
                }
                sparseArray2.put(z ? Utilities.parseInt((CharSequence) key.substring(key.lastIndexOf(95) + 1)).intValue() : 0, TLdeserialize2);
            }
            serializedData.cleanup();
        }
        loadStickersByEmojiOrName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME, false, true);
        loadEmojiThemes();
        this.ringtoneDataStore = new RingtoneDataStore(this.currentAccount);
    }

    public void cleanup() {
        int i = 0;
        while (true) {
            ArrayList<TLRPC$Document>[] arrayListArr = this.recentStickers;
            if (i >= arrayListArr.length) {
                break;
            }
            arrayListArr[i].clear();
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = false;
            i++;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            this.loadHash[i2] = 0;
            this.loadDate[i2] = 0;
            this.stickerSets[i2].clear();
            this.loadingStickers[i2] = false;
            this.stickersLoaded[i2] = false;
        }
        this.loadingPinnedMessages.clear();
        int[] iArr = this.loadFeaturedDate;
        iArr[0] = 0;
        long[] jArr = this.loadFeaturedHash;
        jArr[0] = 0;
        iArr[1] = 0;
        jArr[1] = 0;
        this.allStickers.clear();
        this.allStickersFeatured.clear();
        this.stickersByEmoji.clear();
        this.featuredStickerSetsById[0].clear();
        this.featuredStickerSets[0].clear();
        this.featuredStickerSetsById[1].clear();
        this.featuredStickerSets[1].clear();
        this.unreadStickerSets[0].clear();
        this.unreadStickerSets[1].clear();
        this.recentGifs.clear();
        this.stickerSetsById.clear();
        this.installedStickerSetsById.clear();
        this.stickerSetsByName.clear();
        this.diceStickerSetsByEmoji.clear();
        this.diceEmojiStickerSetsById.clear();
        this.loadingDiceStickerSets.clear();
        boolean[] zArr = this.loadingFeaturedStickers;
        zArr[0] = false;
        boolean[] zArr2 = this.featuredStickersLoaded;
        zArr2[0] = false;
        zArr[1] = false;
        zArr2[1] = false;
        this.loadingRecentGifs = false;
        this.recentGifsLoaded = false;
        this.currentFetchingEmoji.clear();
        if (Build.VERSION.SDK_INT >= 25) {
            Utilities.globalQueue.postRunnable(MediaDataController$$ExternalSyntheticLambda129.INSTANCE);
        }
        this.verifyingMessages.clear();
        this.loading = false;
        this.loaded = false;
        this.hints.clear();
        this.inlineBots.clear();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$cleanup$1();
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

    public /* synthetic */ void lambda$cleanup$1() {
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public boolean areStickersLoaded(int i) {
        return this.stickersLoaded[i];
    }

    public void checkStickers(int i) {
        if (!this.loadingStickers[i]) {
            if (this.stickersLoaded[i] && Math.abs((System.currentTimeMillis() / 1000) - this.loadDate[i]) < 3600) {
                return;
            }
            loadStickers(i, true, false);
        }
    }

    public void checkReactions() {
        if (this.isLoadingReactions || Math.abs((System.currentTimeMillis() / 1000) - this.reactionsUpdateDate) < 3600) {
            return;
        }
        loadReactions(true, false);
    }

    public void checkMenuBots() {
        if (this.isLoadingMenuBots || Math.abs((System.currentTimeMillis() / 1000) - this.menuBotsUpdateDate) < 3600) {
            return;
        }
        loadAttachMenuBots(true, false);
    }

    public void checkPremiumPromo() {
        if (this.isLoadingPremiumPromo || Math.abs((System.currentTimeMillis() / 1000) - this.premiumPromoUpdateDate) < 3600) {
            return;
        }
        loadPremiumPromo(true);
    }

    public TLRPC$TL_help_premiumPromo getPremiumPromo() {
        return this.premiumPromo;
    }

    public TLRPC$TL_attachMenuBots getAttachMenuBots() {
        return this.attachMenuBots;
    }

    public void loadAttachMenuBots(boolean z, boolean z2) {
        this.isLoadingMenuBots = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadAttachMenuBots$2();
                }
            });
            return;
        }
        TLRPC$TL_messages_getAttachMenuBots tLRPC$TL_messages_getAttachMenuBots = new TLRPC$TL_messages_getAttachMenuBots();
        tLRPC$TL_messages_getAttachMenuBots.hash = z2 ? 0L : this.menuBotsUpdateHash;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getAttachMenuBots, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda144
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadAttachMenuBots$3(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v5, types: [org.telegram.tgnet.TLRPC$TL_attachMenuBots] */
    public /* synthetic */ void lambda$loadAttachMenuBots$2() {
        SQLiteCursor sQLiteCursor;
        Throwable th;
        int i;
        long j;
        TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots;
        Exception e;
        long j2;
        SQLiteCursor sQLiteCursor2;
        SQLiteCursor sQLiteCursor3 = null;
        int i2 = 0;
        long j3 = 0;
        try {
            try {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized("SELECT data, hash, date FROM attach_menu_bots", new Object[0]);
            } catch (Exception e2) {
                e = e2;
                j2 = 0;
                sQLiteCursor2 = null;
            }
        } catch (Throwable th2) {
            sQLiteCursor = sQLiteCursor3;
            th = th2;
        }
        try {
            if (sQLiteCursor.next()) {
                NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$AttachMenuBots TLdeserialize = TLRPC$AttachMenuBots.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), true);
                    if (TLdeserialize instanceof TLRPC$TL_attachMenuBots) {
                        sQLiteCursor3 = (TLRPC$TL_attachMenuBots) TLdeserialize;
                    }
                    byteBufferValue.reuse();
                }
                j3 = sQLiteCursor.longValue(1);
                i2 = sQLiteCursor.intValue(2);
            }
            sQLiteCursor.dispose();
            tLRPC$TL_attachMenuBots = sQLiteCursor3;
            i = i2;
            j = j3;
        } catch (Exception e3) {
            e = e3;
            long j4 = j3;
            sQLiteCursor2 = sQLiteCursor3;
            sQLiteCursor3 = sQLiteCursor;
            j2 = j4;
            FileLog.e((Throwable) e, false);
            if (sQLiteCursor3 != null) {
                sQLiteCursor3.dispose();
            }
            tLRPC$TL_attachMenuBots = sQLiteCursor2;
            j = j2;
            i = 0;
            processLoadedMenuBots(tLRPC$TL_attachMenuBots, j, i, true);
        } catch (Throwable th3) {
            th = th3;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
        processLoadedMenuBots(tLRPC$TL_attachMenuBots, j, i, true);
    }

    public /* synthetic */ void lambda$loadAttachMenuBots$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC$TL_attachMenuBotsNotModified) {
            processLoadedMenuBots(null, 0L, currentTimeMillis, false);
        } else if (!(tLObject instanceof TLRPC$TL_attachMenuBots)) {
        } else {
            TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots = (TLRPC$TL_attachMenuBots) tLObject;
            processLoadedMenuBots(tLRPC$TL_attachMenuBots, tLRPC$TL_attachMenuBots.hash, currentTimeMillis, false);
        }
    }

    public void processLoadedMenuBots(TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots, long j, int i, boolean z) {
        if (tLRPC$TL_attachMenuBots != null && i != 0) {
            this.attachMenuBots = tLRPC$TL_attachMenuBots;
            this.menuBotsUpdateHash = j;
        }
        this.menuBotsUpdateDate = i;
        if (tLRPC$TL_attachMenuBots != null) {
            getMessagesController().putUsers(tLRPC$TL_attachMenuBots.users, z);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$processLoadedMenuBots$4();
                }
            });
        }
        if (!z) {
            putMenuBotsToCache(tLRPC$TL_attachMenuBots, j, i);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - i) < 3600) {
        } else {
            loadAttachMenuBots(false, true);
        }
    }

    public /* synthetic */ void lambda$processLoadedMenuBots$4() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.attachMenuBotsDidLoad, new Object[0]);
    }

    private void putMenuBotsToCache(final TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots, final long j, final int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda94
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putMenuBotsToCache$5(tLRPC$TL_attachMenuBots, j, i);
            }
        });
    }

    public /* synthetic */ void lambda$putMenuBotsToCache$5(TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots, long j, int i) {
        try {
            if (tLRPC$TL_attachMenuBots != null) {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM attach_menu_bots").stepThis().dispose();
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO attach_menu_bots VALUES(?, ?, ?)");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_attachMenuBots.getObjectSize());
                tLRPC$TL_attachMenuBots.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindLong(2, j);
                executeFast.bindInteger(3, i);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } else {
                SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE attach_menu_bots SET date = ?");
                executeFast2.requery();
                executeFast2.bindLong(1, i);
                executeFast2.step();
                executeFast2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void loadPremiumPromo(boolean z) {
        this.isLoadingPremiumPromo = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadPremiumPromo$6();
                }
            });
            return;
        }
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_help_getPremiumPromo
            public static int constructor = -1206152236;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z2) {
                return TLRPC$TL_help_premiumPromo.TLdeserialize(abstractSerializedData, i, z2);
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda143
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadPremiumPromo$7(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadPremiumPromo$6() {
        /*
            r7 = this;
            r0 = 0
            r1 = 1
            r2 = 0
            org.telegram.messenger.MessagesStorage r3 = r7.getMessagesStorage()     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3d
            org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3d
            java.lang.String r4 = "SELECT data, date FROM premium_promo"
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3d
            org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r5)     // Catch: java.lang.Throwable -> L39 java.lang.Exception -> L3d
            boolean r4 = r3.next()     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            if (r4 == 0) goto L2e
            org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r2)     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            if (r4 == 0) goto L2a
            int r5 = r4.readInt32(r2)     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            org.telegram.tgnet.TLRPC$TL_help_premiumPromo r0 = org.telegram.tgnet.TLRPC$TL_help_premiumPromo.TLdeserialize(r4, r5, r1)     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            r4.reuse()     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
        L2a:
            int r2 = r3.intValue(r1)     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
        L2e:
            r3.dispose()
            goto L48
        L32:
            r0 = move-exception
            goto L4e
        L34:
            r4 = move-exception
            r6 = r3
            r3 = r0
            r0 = r6
            goto L3f
        L39:
            r1 = move-exception
            r3 = r0
            r0 = r1
            goto L4e
        L3d:
            r4 = move-exception
            r3 = r0
        L3f:
            org.telegram.messenger.FileLog.e(r4, r2)     // Catch: java.lang.Throwable -> L39
            if (r0 == 0) goto L47
            r0.dispose()
        L47:
            r0 = r3
        L48:
            if (r0 == 0) goto L4d
            r7.processLoadedPremiumPromo(r0, r2, r1)
        L4d:
            return
        L4e:
            if (r3 == 0) goto L53
            r3.dispose()
        L53:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPremiumPromo$6():void");
    }

    public /* synthetic */ void lambda$loadPremiumPromo$7(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC$TL_help_premiumPromo) {
            processLoadedPremiumPromo((TLRPC$TL_help_premiumPromo) tLObject, currentTimeMillis, false);
        }
    }

    private void processLoadedPremiumPromo(TLRPC$TL_help_premiumPromo tLRPC$TL_help_premiumPromo, int i, boolean z) {
        this.premiumPromo = tLRPC$TL_help_premiumPromo;
        this.premiumPromoUpdateDate = i;
        getMessagesController().putUsers(tLRPC$TL_help_premiumPromo.users, z);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedPremiumPromo$8();
            }
        });
        if (!z) {
            putPremiumPromoToCache(tLRPC$TL_help_premiumPromo, i);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - i) < 86400 && !BuildVars.DEBUG_PRIVATE_VERSION) {
        } else {
            loadPremiumPromo(false);
        }
    }

    public /* synthetic */ void lambda$processLoadedPremiumPromo$8() {
        getNotificationCenter().postNotificationName(NotificationCenter.premiumPromoUpdated, new Object[0]);
    }

    private void putPremiumPromoToCache(final TLRPC$TL_help_premiumPromo tLRPC$TL_help_premiumPromo, final int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda100
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putPremiumPromoToCache$9(tLRPC$TL_help_premiumPromo, i);
            }
        });
    }

    public /* synthetic */ void lambda$putPremiumPromoToCache$9(TLRPC$TL_help_premiumPromo tLRPC$TL_help_premiumPromo, int i) {
        try {
            if (tLRPC$TL_help_premiumPromo != null) {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM premium_promo").stepThis().dispose();
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO premium_promo VALUES(?, ?)");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_help_premiumPromo.getObjectSize());
                tLRPC$TL_help_premiumPromo.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindInteger(2, i);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } else {
                SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE premium_promo SET date = ?");
                executeFast2.requery();
                executeFast2.bindInteger(1, i);
                executeFast2.step();
                executeFast2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public List<TLRPC$TL_availableReaction> getReactionsList() {
        return this.reactionsList;
    }

    public void loadReactions(boolean z, boolean z2) {
        this.isLoadingReactions = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadReactions$10();
                }
            });
            return;
        }
        TLRPC$TL_messages_getAvailableReactions tLRPC$TL_messages_getAvailableReactions = new TLRPC$TL_messages_getAvailableReactions();
        tLRPC$TL_messages_getAvailableReactions.hash = z2 ? 0 : this.reactionsUpdateHash;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getAvailableReactions, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda148
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadReactions$11(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0069  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0076  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadReactions$10() {
        /*
            r9 = this;
            r0 = 0
            r1 = 1
            r2 = 0
            org.telegram.messenger.MessagesStorage r3 = r9.getMessagesStorage()     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5f
            org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5f
            java.lang.String r4 = "SELECT data, hash, date FROM reactions"
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5f
            org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r5)     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5f
            boolean r4 = r3.next()     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L73
            if (r4 == 0) goto L51
            org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r2)     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L73
            if (r4 == 0) goto L40
            int r5 = r4.readInt32(r2)     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L73
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L73
            r6.<init>(r5)     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L73
            r0 = 0
        L29:
            if (r0 >= r5) goto L39
            int r7 = r4.readInt32(r2)     // Catch: java.lang.Exception -> L3e java.lang.Throwable -> L73
            org.telegram.tgnet.TLRPC$TL_availableReaction r7 = org.telegram.tgnet.TLRPC$TL_availableReaction.TLdeserialize(r4, r7, r1)     // Catch: java.lang.Exception -> L3e java.lang.Throwable -> L73
            r6.add(r7)     // Catch: java.lang.Exception -> L3e java.lang.Throwable -> L73
            int r0 = r0 + 1
            goto L29
        L39:
            r4.reuse()     // Catch: java.lang.Exception -> L3e java.lang.Throwable -> L73
            r0 = r6
            goto L40
        L3e:
            r0 = move-exception
            goto L59
        L40:
            int r4 = r3.intValue(r1)     // Catch: java.lang.Exception -> L56 java.lang.Throwable -> L73
            r5 = 2
            int r2 = r3.intValue(r5)     // Catch: java.lang.Exception -> L4d java.lang.Throwable -> L73
            r8 = r4
            r4 = r2
            r2 = r8
            goto L52
        L4d:
            r5 = move-exception
            r6 = r0
            r0 = r5
            goto L64
        L51:
            r4 = 0
        L52:
            r3.dispose()
            goto L6f
        L56:
            r4 = move-exception
            r6 = r0
            r0 = r4
        L59:
            r4 = 0
            goto L64
        L5b:
            r1 = move-exception
            r3 = r0
            r0 = r1
            goto L74
        L5f:
            r3 = move-exception
            r6 = r0
            r4 = 0
            r0 = r3
            r3 = r6
        L64:
            org.telegram.messenger.FileLog.e(r0, r2)     // Catch: java.lang.Throwable -> L73
            if (r3 == 0) goto L6c
            r3.dispose()
        L6c:
            r2 = r4
            r0 = r6
            r4 = 0
        L6f:
            r9.processLoadedReactions(r0, r2, r4, r1)
            return
        L73:
            r0 = move-exception
        L74:
            if (r3 == 0) goto L79
            r3.dispose()
        L79:
            goto L7b
        L7a:
            throw r0
        L7b:
            goto L7a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadReactions$10():void");
    }

    public /* synthetic */ void lambda$loadReactions$11(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC$TL_messages_availableReactionsNotModified) {
            processLoadedReactions(null, 0, currentTimeMillis, false);
        } else if (!(tLObject instanceof TLRPC$TL_messages_availableReactions)) {
        } else {
            TLRPC$TL_messages_availableReactions tLRPC$TL_messages_availableReactions = (TLRPC$TL_messages_availableReactions) tLObject;
            processLoadedReactions(tLRPC$TL_messages_availableReactions.reactions, tLRPC$TL_messages_availableReactions.hash, currentTimeMillis, false);
        }
    }

    public void processLoadedReactions(final List<TLRPC$TL_availableReaction> list, int i, int i2, boolean z) {
        if (list != null && i2 != 0) {
            this.reactionsList.clear();
            this.reactionsMap.clear();
            this.enabledReactionsList.clear();
            this.reactionsList.addAll(list);
            for (int i3 = 0; i3 < this.reactionsList.size(); i3++) {
                this.reactionsList.get(i3).positionInList = i3;
                this.reactionsMap.put(this.reactionsList.get(i3).reaction, this.reactionsList.get(i3));
                if (!this.reactionsList.get(i3).inactive) {
                    this.enabledReactionsList.add(this.reactionsList.get(i3));
                }
            }
            this.reactionsUpdateHash = i;
        }
        this.reactionsUpdateDate = i2;
        if (list != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.lambda$processLoadedReactions$12(list);
                }
            });
        }
        this.isLoadingReactions = false;
        if (!z) {
            putReactionsToCache(list, i, i2);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - i2) >= 3600) {
            loadReactions(false, true);
        }
    }

    public static /* synthetic */ void lambda$processLoadedReactions$12(List list) {
        for (int i = 0; i < list.size(); i++) {
            ImageReceiver imageReceiver = new ImageReceiver();
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) list.get(i);
            imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.activate_animation), null, null, null, 0, 11);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
            ImageReceiver imageReceiver2 = new ImageReceiver();
            imageReceiver2.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.appear_animation), "60_60_nolimit", null, null, 0, 11);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver2);
            int sizeForBigReaction = ReactionsEffectOverlay.sizeForBigReaction();
            ImageReceiver imageReceiver3 = new ImageReceiver();
            ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$TL_availableReaction.around_animation);
            imageReceiver3.setImage(forDocument, sizeForBigReaction + "_" + sizeForBigReaction, null, null, 0, 11);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver3);
            ImageReceiver imageReceiver4 = new ImageReceiver();
            imageReceiver4.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.center_icon), null, null, null, 0, 11);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver4);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reactionsDidLoad, new Object[0]);
    }

    private void putReactionsToCache(List<TLRPC$TL_availableReaction> list, final int i, final int i2) {
        final ArrayList arrayList = list != null ? new ArrayList(list) : null;
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda64
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putReactionsToCache$13(arrayList, i, i2);
            }
        });
    }

    public /* synthetic */ void lambda$putReactionsToCache$13(ArrayList arrayList, int i, int i2) {
        try {
            if (arrayList != null) {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM reactions").stepThis().dispose();
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO reactions VALUES(?, ?, ?)");
                executeFast.requery();
                int i3 = 4;
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    i3 += ((TLRPC$TL_availableReaction) arrayList.get(i4)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((TLRPC$TL_availableReaction) arrayList.get(i5)).serializeToStream(nativeByteBuffer);
                }
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindInteger(2, i);
                executeFast.bindInteger(3, i2);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
                return;
            }
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE reactions SET date = ?");
            executeFast2.requery();
            executeFast2.bindLong(1, i2);
            executeFast2.step();
            executeFast2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void checkFeaturedStickers() {
        if (!this.loadingFeaturedStickers[0]) {
            if (this.featuredStickersLoaded[0] && Math.abs((System.currentTimeMillis() / 1000) - this.loadFeaturedDate[0]) < 3600) {
                return;
            }
            loadFeaturedStickers(false, true, false);
        }
    }

    public void checkFeaturedEmoji() {
        if (!this.loadingFeaturedStickers[1]) {
            if (this.featuredStickersLoaded[1] && Math.abs((System.currentTimeMillis() / 1000) - this.loadFeaturedDate[1]) < 3600) {
                return;
            }
            loadFeaturedStickers(true, true, false);
        }
    }

    public ArrayList<TLRPC$Document> getRecentStickers(int i) {
        ArrayList<TLRPC$Document> arrayList = this.recentStickers[i];
        return new ArrayList<>(arrayList.subList(0, Math.min(arrayList.size(), 20)));
    }

    public ArrayList<TLRPC$Document> getRecentStickersNoCopy(int i) {
        return this.recentStickers[i];
    }

    public boolean isStickerInFavorites(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return false;
        }
        for (int i = 0; i < this.recentStickers[2].size(); i++) {
            TLRPC$Document tLRPC$Document2 = this.recentStickers[2].get(i);
            if (tLRPC$Document2.id == tLRPC$Document.id && tLRPC$Document2.dc_id == tLRPC$Document.dc_id) {
                return true;
            }
        }
        return false;
    }

    public void clearRecentStickers() {
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_messages_clearRecentStickers
            public static int constructor = -1986437075;
            public boolean attached;
            public int flags;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
                int i = this.attached ? this.flags | 1 : this.flags & (-2);
                this.flags = i;
                abstractSerializedData.writeInt32(i);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda141
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$clearRecentStickers$16(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$clearRecentStickers$16(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda80
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$clearRecentStickers$15(tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$clearRecentStickers$15(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$clearRecentStickers$14();
                }
            });
            this.recentStickers[0].clear();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.FALSE, 0);
        }
    }

    public /* synthetic */ void lambda$clearRecentStickers$14() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE type = 3").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void addRecentSticker(final int i, final Object obj, TLRPC$Document tLRPC$Document, int i2, boolean z) {
        boolean z2;
        int i3;
        final TLRPC$Document tLRPC$Document2;
        if (i != 3) {
            if (!MessageObject.isStickerDocument(tLRPC$Document) && !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                return;
            }
            int i4 = 0;
            while (true) {
                if (i4 >= this.recentStickers[i].size()) {
                    z2 = false;
                    break;
                }
                TLRPC$Document tLRPC$Document3 = this.recentStickers[i].get(i4);
                if (tLRPC$Document3.id == tLRPC$Document.id) {
                    this.recentStickers[i].remove(i4);
                    if (!z) {
                        this.recentStickers[i].add(0, tLRPC$Document3);
                    }
                    z2 = true;
                } else {
                    i4++;
                }
            }
            if (!z2 && !z) {
                this.recentStickers[i].add(0, tLRPC$Document);
            }
            if (i == 2) {
                if (z) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, tLRPC$Document, 4);
                } else {
                    boolean z3 = this.recentStickers[i].size() > getMessagesController().maxFaveStickersCount;
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i5 = NotificationCenter.showBulletin;
                    Object[] objArr = new Object[3];
                    objArr[0] = 0;
                    objArr[1] = tLRPC$Document;
                    objArr[2] = Integer.valueOf(z3 ? 6 : 5);
                    globalInstance.postNotificationName(i5, objArr);
                }
                final TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker = new TLRPC$TL_messages_faveSticker();
                TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
                tLRPC$TL_messages_faveSticker.id = tLRPC$TL_inputDocument;
                tLRPC$TL_inputDocument.id = tLRPC$Document.id;
                tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
                byte[] bArr = tLRPC$Document.file_reference;
                tLRPC$TL_inputDocument.file_reference = bArr;
                if (bArr == null) {
                    tLRPC$TL_inputDocument.file_reference = new byte[0];
                }
                tLRPC$TL_messages_faveSticker.unfave = z;
                getConnectionsManager().sendRequest(tLRPC$TL_messages_faveSticker, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda166
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MediaDataController.this.lambda$addRecentSticker$18(obj, tLRPC$TL_messages_faveSticker, tLObject, tLRPC$TL_error);
                    }
                });
                i3 = getMessagesController().maxFaveStickersCount;
            } else {
                if (i == 0 && z) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, tLRPC$Document, 3);
                    final TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker = new TLRPC$TL_messages_saveRecentSticker();
                    TLRPC$TL_inputDocument tLRPC$TL_inputDocument2 = new TLRPC$TL_inputDocument();
                    tLRPC$TL_messages_saveRecentSticker.id = tLRPC$TL_inputDocument2;
                    tLRPC$TL_inputDocument2.id = tLRPC$Document.id;
                    tLRPC$TL_inputDocument2.access_hash = tLRPC$Document.access_hash;
                    byte[] bArr2 = tLRPC$Document.file_reference;
                    tLRPC$TL_inputDocument2.file_reference = bArr2;
                    if (bArr2 == null) {
                        tLRPC$TL_inputDocument2.file_reference = new byte[0];
                    }
                    tLRPC$TL_messages_saveRecentSticker.unsave = true;
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_saveRecentSticker, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda167
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MediaDataController.this.lambda$addRecentSticker$19(obj, tLRPC$TL_messages_saveRecentSticker, tLObject, tLRPC$TL_error);
                        }
                    });
                }
                i3 = getMessagesController().maxRecentStickersCount;
            }
            if (this.recentStickers[i].size() > i3 || z) {
                if (z) {
                    tLRPC$Document2 = tLRPC$Document;
                } else {
                    ArrayList<TLRPC$Document>[] arrayListArr = this.recentStickers;
                    tLRPC$Document2 = arrayListArr[i].remove(arrayListArr[i].size() - 1);
                }
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda25
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.lambda$addRecentSticker$20(i, tLRPC$Document2);
                    }
                });
            }
            if (!z) {
                ArrayList<TLRPC$Document> arrayList = new ArrayList<>();
                arrayList.add(tLRPC$Document);
                processLoadedRecentDocuments(i, arrayList, false, i2, false);
            }
            if (i != 2 && (i != 0 || !z)) {
                return;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.FALSE, Integer.valueOf(i));
        }
    }

    public /* synthetic */ void lambda$addRecentSticker$18(Object obj, TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && FileRefController.isFileRefError(tLRPC$TL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tLRPC$TL_messages_faveSticker);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$addRecentSticker$17();
                }
            });
        }
    }

    public /* synthetic */ void lambda$addRecentSticker$17() {
        getMediaDataController().loadRecents(2, false, false, true);
    }

    public /* synthetic */ void lambda$addRecentSticker$19(Object obj, TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text) || obj == null) {
            return;
        }
        getFileRefController().requestReference(obj, tLRPC$TL_messages_saveRecentSticker);
    }

    public /* synthetic */ void lambda$addRecentSticker$20(int i, TLRPC$Document tLRPC$Document) {
        int i2 = 5;
        if (i == 0) {
            i2 = 3;
        } else if (i == 1) {
            i2 = 4;
        } else if (i == 5) {
            i2 = 7;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + tLRPC$Document.id + "' AND type = " + i2).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public ArrayList<TLRPC$Document> getRecentGifs() {
        return new ArrayList<>(this.recentGifs);
    }

    public void removeRecentGif(final TLRPC$Document tLRPC$Document) {
        int size = this.recentGifs.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            } else if (this.recentGifs.get(i).id == tLRPC$Document.id) {
                this.recentGifs.remove(i);
                break;
            } else {
                i++;
            }
        }
        final TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif = new TLRPC$TL_messages_saveGif();
        TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
        tLRPC$TL_messages_saveGif.id = tLRPC$TL_inputDocument;
        tLRPC$TL_inputDocument.id = tLRPC$Document.id;
        tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
        byte[] bArr = tLRPC$Document.file_reference;
        tLRPC$TL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tLRPC$TL_inputDocument.file_reference = new byte[0];
        }
        tLRPC$TL_messages_saveGif.unsave = true;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_saveGif, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda178
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$removeRecentGif$21(tLRPC$TL_messages_saveGif, tLObject, tLRPC$TL_error);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda87
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$removeRecentGif$22(tLRPC$Document);
            }
        });
    }

    public /* synthetic */ void lambda$removeRecentGif$21(TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text)) {
            return;
        }
        getFileRefController().requestReference("gif", tLRPC$TL_messages_saveGif);
    }

    public /* synthetic */ void lambda$removeRecentGif$22(TLRPC$Document tLRPC$Document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + tLRPC$Document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean hasRecentGif(TLRPC$Document tLRPC$Document) {
        for (int i = 0; i < this.recentGifs.size(); i++) {
            TLRPC$Document tLRPC$Document2 = this.recentGifs.get(i);
            if (tLRPC$Document2.id == tLRPC$Document.id) {
                this.recentGifs.remove(i);
                this.recentGifs.add(0, tLRPC$Document2);
                return true;
            }
        }
        return false;
    }

    public void addRecentGif(final TLRPC$Document tLRPC$Document, int i, boolean z) {
        boolean z2;
        if (tLRPC$Document == null) {
            return;
        }
        int i2 = 0;
        while (true) {
            if (i2 >= this.recentGifs.size()) {
                z2 = false;
                break;
            }
            TLRPC$Document tLRPC$Document2 = this.recentGifs.get(i2);
            if (tLRPC$Document2.id == tLRPC$Document.id) {
                this.recentGifs.remove(i2);
                this.recentGifs.add(0, tLRPC$Document2);
                z2 = true;
                break;
            }
            i2++;
        }
        if (!z2) {
            this.recentGifs.add(0, tLRPC$Document);
        }
        if ((this.recentGifs.size() > getMessagesController().savedGifsLimitDefault && !UserConfig.getInstance(this.currentAccount).isPremium()) || this.recentGifs.size() > getMessagesController().savedGifsLimitPremium) {
            ArrayList<TLRPC$Document> arrayList = this.recentGifs;
            final TLRPC$Document remove = arrayList.remove(arrayList.size() - 1);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda86
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$addRecentGif$23(remove);
                }
            });
            if (z) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda126
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.lambda$addRecentGif$24(TLRPC$Document.this);
                    }
                });
            }
        }
        ArrayList<TLRPC$Document> arrayList2 = new ArrayList<>();
        arrayList2.add(tLRPC$Document);
        processLoadedRecentDocuments(0, arrayList2, true, i, false);
    }

    public /* synthetic */ void lambda$addRecentGif$23(TLRPC$Document tLRPC$Document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + tLRPC$Document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void lambda$addRecentGif$24(TLRPC$Document tLRPC$Document) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, tLRPC$Document, 7);
    }

    public boolean isLoadingStickers(int i) {
        return this.loadingStickers[i];
    }

    public void replaceStickerSet(final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        boolean z;
        int i;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = this.stickerSetsById.get(tLRPC$TL_messages_stickerSet.set.id);
        String str = this.diceEmojiStickerSetsById.get(tLRPC$TL_messages_stickerSet.set.id);
        if (str != null) {
            this.diceStickerSetsByEmoji.put(str, tLRPC$TL_messages_stickerSet);
            putDiceStickersToCache(str, tLRPC$TL_messages_stickerSet, (int) (System.currentTimeMillis() / 1000));
        }
        if (tLRPC$TL_messages_stickerSet2 == null) {
            tLRPC$TL_messages_stickerSet2 = this.stickerSetsByName.get(tLRPC$TL_messages_stickerSet.set.short_name);
        }
        boolean z2 = tLRPC$TL_messages_stickerSet2 == null && (tLRPC$TL_messages_stickerSet2 = this.groupStickerSets.get(tLRPC$TL_messages_stickerSet.set.id)) != null;
        if (tLRPC$TL_messages_stickerSet2 == null) {
            return;
        }
        if ("AnimatedEmojies".equals(tLRPC$TL_messages_stickerSet.set.short_name)) {
            tLRPC$TL_messages_stickerSet2.documents = tLRPC$TL_messages_stickerSet.documents;
            tLRPC$TL_messages_stickerSet2.packs = tLRPC$TL_messages_stickerSet.packs;
            tLRPC$TL_messages_stickerSet2.set = tLRPC$TL_messages_stickerSet.set;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda105
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$replaceStickerSet$25(tLRPC$TL_messages_stickerSet);
                }
            });
            z = true;
        } else {
            LongSparseArray longSparseArray = new LongSparseArray();
            int size = tLRPC$TL_messages_stickerSet.documents.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
                longSparseArray.put(tLRPC$Document.id, tLRPC$Document);
            }
            int size2 = tLRPC$TL_messages_stickerSet2.documents.size();
            z = false;
            for (int i3 = 0; i3 < size2; i3++) {
                TLRPC$Document tLRPC$Document2 = (TLRPC$Document) longSparseArray.get(tLRPC$TL_messages_stickerSet2.documents.get(i3).id);
                if (tLRPC$Document2 != null) {
                    tLRPC$TL_messages_stickerSet2.documents.set(i3, tLRPC$Document2);
                    z = true;
                }
            }
        }
        if (!z) {
            return;
        }
        if (z2) {
            putSetToCache(tLRPC$TL_messages_stickerSet2);
            return;
        }
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        if (tLRPC$StickerSet.masks) {
            i = 1;
        } else {
            i = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        putStickersToCache(i, this.stickerSets[i], this.loadDate[i], this.loadHash[i]);
        if (!"AnimatedEmojies".equals(tLRPC$TL_messages_stickerSet.set.short_name)) {
            return;
        }
        putStickersToCache(4, this.stickerSets[4], this.loadDate[4], this.loadHash[4]);
    }

    public /* synthetic */ void lambda$replaceStickerSet$25(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        LongSparseArray<TLRPC$Document> stickerByIds = getStickerByIds(4);
        for (int i = 0; i < tLRPC$TL_messages_stickerSet.documents.size(); i++) {
            TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i);
            stickerByIds.put(tLRPC$Document.id, tLRPC$Document);
        }
    }

    public TLRPC$TL_messages_stickerSet getStickerSetByName(String str) {
        return this.stickerSetsByName.get(str);
    }

    public TLRPC$TL_messages_stickerSet getStickerSetByEmojiOrName(String str) {
        return this.diceStickerSetsByEmoji.get(str);
    }

    public TLRPC$TL_messages_stickerSet getStickerSetById(long j) {
        return this.stickerSetsById.get(j);
    }

    public TLRPC$TL_messages_stickerSet getGroupStickerSetById(TLRPC$StickerSet tLRPC$StickerSet) {
        TLRPC$StickerSet tLRPC$StickerSet2;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSetsById.get(tLRPC$StickerSet.id);
        if (tLRPC$TL_messages_stickerSet == null) {
            tLRPC$TL_messages_stickerSet = this.groupStickerSets.get(tLRPC$StickerSet.id);
            if (tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set) == null) {
                loadGroupStickerSet(tLRPC$StickerSet, true);
            } else if (tLRPC$StickerSet2.hash != tLRPC$StickerSet.hash) {
                loadGroupStickerSet(tLRPC$StickerSet, false);
            }
        }
        return tLRPC$TL_messages_stickerSet;
    }

    public void putGroupStickerSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.groupStickerSets.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
    }

    public TLRPC$TL_messages_stickerSet getStickerSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
        return getStickerSet(tLRPC$InputStickerSet, z, null);
    }

    public TLRPC$TL_messages_stickerSet getStickerSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z, final Runnable runnable) {
        String str;
        if ((tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetID) && this.stickerSetsById.containsKey(tLRPC$InputStickerSet.id)) {
            return this.stickerSetsById.get(tLRPC$InputStickerSet.id);
        }
        if ((tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetShortName) && (str = tLRPC$InputStickerSet.short_name) != null && this.stickerSetsByName.containsKey(str.toLowerCase())) {
            return this.stickerSetsByName.get(tLRPC$InputStickerSet.short_name.toLowerCase());
        }
        if (z) {
            return null;
        }
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$InputStickerSet;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda168
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$getStickerSet$27(runnable, tLObject, tLRPC$TL_error);
            }
        });
        return null;
    }

    public /* synthetic */ void lambda$getStickerSet$27(Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda102
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$getStickerSet$26(tLRPC$TL_messages_stickerSet);
                }
            });
        } else if (runnable == null) {
        } else {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$getStickerSet$26(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        TLRPC$StickerSet tLRPC$StickerSet;
        if (tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) == null) {
            return;
        }
        this.stickerSetsById.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
        this.stickerSetsByName.put(tLRPC$TL_messages_stickerSet.set.short_name.toLowerCase(), tLRPC$TL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
    }

    private void loadGroupStickerSet(final TLRPC$StickerSet tLRPC$StickerSet, boolean z) {
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda90
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadGroupStickerSet$29(tLRPC$StickerSet);
                }
            });
            return;
        }
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda149
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadGroupStickerSet$31(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$29(TLRPC$StickerSet tLRPC$StickerSet) {
        TLRPC$StickerSet tLRPC$StickerSet2;
        NativeByteBuffer byteBufferValue;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor queryFinalized = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + tLRPC$StickerSet.id + "'", new Object[0]);
            final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
            if (queryFinalized.next() && !queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$TL_messages_stickerSet = TLRPC$messages_StickerSet.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set) == null || tLRPC$StickerSet2.hash != tLRPC$StickerSet.hash) {
                loadGroupStickerSet(tLRPC$StickerSet, false);
            }
            if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.set == null) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda101
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadGroupStickerSet$28(tLRPC$TL_messages_stickerSet);
                }
            });
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$28(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.groupStickerSets.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$31(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda103
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadGroupStickerSet$30(tLRPC$TL_messages_stickerSet);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$30(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.groupStickerSets.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
    }

    private void putSetToCache(final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda104
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putSetToCache$32(tLRPC$TL_messages_stickerSet);
            }
        });
    }

    public /* synthetic */ void lambda$putSetToCache$32(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindString(1, "s_" + tLRPC$TL_messages_stickerSet.set.id);
            executeFast.bindInteger(2, 6);
            executeFast.bindString(3, "");
            executeFast.bindString(4, "");
            executeFast.bindString(5, "");
            executeFast.bindInteger(6, 0);
            executeFast.bindInteger(7, 0);
            executeFast.bindInteger(8, 0);
            executeFast.bindInteger(9, 0);
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_messages_stickerSet.getObjectSize());
            tLRPC$TL_messages_stickerSet.serializeToStream(nativeByteBuffer);
            executeFast.bindByteBuffer(10, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public HashMap<String, ArrayList<TLRPC$Document>> getAllStickers() {
        return this.allStickers;
    }

    public HashMap<String, ArrayList<TLRPC$Document>> getAllStickersFeatured() {
        return this.allStickersFeatured;
    }

    public TLRPC$Document getEmojiAnimatedSticker(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        String replace = charSequence.toString().replace("️", "");
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = getStickerSets(4);
        int size = stickerSets.size();
        for (int i = 0; i < size; i++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSets.get(i);
            int size2 = tLRPC$TL_messages_stickerSet.packs.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i2);
                if (!tLRPC$TL_stickerPack.documents.isEmpty() && TextUtils.equals(tLRPC$TL_stickerPack.emoticon, replace)) {
                    return getStickerByIds(4).get(tLRPC$TL_stickerPack.documents.get(0).longValue());
                }
            }
        }
        return null;
    }

    public boolean canAddStickerToFavorites() {
        return !this.stickersLoaded[0] || this.stickerSets[0].size() >= 5 || !this.recentStickers[2].isEmpty();
    }

    public ArrayList<TLRPC$TL_messages_stickerSet> getStickerSets(int i) {
        if (i == 3) {
            return this.stickerSets[2];
        }
        return this.stickerSets[i];
    }

    public LongSparseArray<TLRPC$Document> getStickerByIds(int i) {
        return this.stickersByIds[i];
    }

    public ArrayList<TLRPC$StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets[0];
    }

    public ArrayList<TLRPC$StickerSetCovered> getFeaturedEmojiSets() {
        return this.featuredStickerSets[1];
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets[0];
    }

    public ArrayList<Long> getUnreadEmojiSets() {
        return this.unreadStickerSets[1];
    }

    public boolean areAllTrendingStickerSetsUnread(boolean z) {
        int size = this.featuredStickerSets[z ? 1 : 0].size();
        for (int i = 0; i < size; i++) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = this.featuredStickerSets[z].get(i);
            if (!isStickerPackInstalled(tLRPC$StickerSetCovered.set.id) && ((!tLRPC$StickerSetCovered.covers.isEmpty() || tLRPC$StickerSetCovered.cover != null) && !this.unreadStickerSets[z].contains(Long.valueOf(tLRPC$StickerSetCovered.set.id)))) {
                return false;
            }
        }
        return true;
    }

    public boolean isStickerPackInstalled(long j) {
        return this.installedStickerSetsById.indexOfKey(j) >= 0;
    }

    public boolean isStickerPackUnread(boolean z, long j) {
        return this.unreadStickerSets[z ? 1 : 0].contains(Long.valueOf(j));
    }

    public boolean isStickerPackInstalled(String str) {
        return this.stickerSetsByName.containsKey(str);
    }

    public String getEmojiForSticker(long j) {
        String str = this.stickersByEmoji.get(j);
        return str != null ? str : "";
    }

    public static boolean canShowAttachMenuBotForTarget(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, String str) {
        Iterator<TLRPC$AttachMenuPeerType> it = tLRPC$TL_attachMenuBot.peer_types.iterator();
        while (it.hasNext()) {
            TLRPC$AttachMenuPeerType next = it.next();
            if (((next instanceof TLRPC$TL_attachMenuPeerTypeSameBotPM) || (next instanceof TLRPC$TL_attachMenuPeerTypeBotPM)) && str.equals("bots")) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeBroadcast) && str.equals("channels")) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeChat) && str.equals("groups")) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypePM) && str.equals("users")) {
                return true;
            }
        }
        return false;
    }

    public static boolean canShowAttachMenuBot(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLObject tLObject) {
        TLRPC$Chat tLRPC$Chat = null;
        TLRPC$User tLRPC$User = tLObject instanceof TLRPC$User ? (TLRPC$User) tLObject : null;
        if (tLObject instanceof TLRPC$Chat) {
            tLRPC$Chat = (TLRPC$Chat) tLObject;
        }
        Iterator<TLRPC$AttachMenuPeerType> it = tLRPC$TL_attachMenuBot.peer_types.iterator();
        while (it.hasNext()) {
            TLRPC$AttachMenuPeerType next = it.next();
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeSameBotPM) && tLRPC$User != null && tLRPC$User.bot && tLRPC$User.id == tLRPC$TL_attachMenuBot.bot_id) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeBotPM) && tLRPC$User != null && tLRPC$User.bot && tLRPC$User.id != tLRPC$TL_attachMenuBot.bot_id) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypePM) && tLRPC$User != null && !tLRPC$User.bot) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeChat) && tLRPC$Chat != null && !ChatObject.isChannelAndNotMegaGroup(tLRPC$Chat)) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeBroadcast) && tLRPC$Chat != null && ChatObject.isChannelAndNotMegaGroup(tLRPC$Chat)) {
                return true;
            }
        }
        return false;
    }

    public static TLRPC$TL_attachMenuBotIcon getAnimatedAttachMenuBotIcon(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        Iterator<TLRPC$TL_attachMenuBotIcon> it = tLRPC$TL_attachMenuBot.icons.iterator();
        while (it.hasNext()) {
            TLRPC$TL_attachMenuBotIcon next = it.next();
            if (next.name.equals(ATTACH_MENU_BOT_ANIMATED_ICON_KEY)) {
                return next;
            }
        }
        return null;
    }

    public static TLRPC$TL_attachMenuBotIcon getStaticAttachMenuBotIcon(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        Iterator<TLRPC$TL_attachMenuBotIcon> it = tLRPC$TL_attachMenuBot.icons.iterator();
        while (it.hasNext()) {
            TLRPC$TL_attachMenuBotIcon next = it.next();
            if (next.name.equals(ATTACH_MENU_BOT_STATIC_ICON_KEY)) {
                return next;
            }
        }
        return null;
    }

    public static TLRPC$TL_attachMenuBotIcon getPlaceholderStaticAttachMenuBotIcon(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        Iterator<TLRPC$TL_attachMenuBotIcon> it = tLRPC$TL_attachMenuBot.icons.iterator();
        while (it.hasNext()) {
            TLRPC$TL_attachMenuBotIcon next = it.next();
            if (next.name.equals(ATTACH_MENU_BOT_PLACEHOLDER_STATIC_KEY)) {
                return next;
            }
        }
        return null;
    }

    public static long calcDocumentsHash(ArrayList<TLRPC$Document> arrayList) {
        return calcDocumentsHash(arrayList, 200);
    }

    public static long calcDocumentsHash(ArrayList<TLRPC$Document> arrayList, int i) {
        long j = 0;
        if (arrayList == null) {
            return 0L;
        }
        int min = Math.min(i, arrayList.size());
        for (int i2 = 0; i2 < min; i2++) {
            TLRPC$Document tLRPC$Document = arrayList.get(i2);
            if (tLRPC$Document != null) {
                j = calcHash(j, tLRPC$Document.id);
            }
        }
        return j;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x001d, code lost:
        if (r6.recentStickersLoaded[r7] != false) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x001f, code lost:
        r9 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0020, code lost:
        if (r9 == false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0022, code lost:
        getMessagesStorage().getStorageQueue().postRunnable(new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda112());
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0034, code lost:
        r9 = org.telegram.messenger.MessagesController.getEmojiSettings(r6.currentAccount);
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x003b, code lost:
        if (r10 != false) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x003f, code lost:
        if (r8 == false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0041, code lost:
        r9 = r9.getLong("lastGifLoadTime", 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0048, code lost:
        if (r7 != 0) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x004a, code lost:
        r9 = r9.getLong("lastStickersLoadTime", 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0051, code lost:
        if (r7 != 1) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0053, code lost:
        r9 = r9.getLong("lastStickersLoadTimeMask", 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x005a, code lost:
        if (r7 != 3) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x005c, code lost:
        r9 = r9.getLong("lastStickersLoadTimeGreet", 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0063, code lost:
        r9 = r9.getLong("lastStickersLoadTimeFavs", 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0077, code lost:
        if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r9) >= 3600000) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0079, code lost:
        if (r8 == false) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x007b, code lost:
        r6.loadingRecentGifs = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x007e, code lost:
        r6.loadingRecentStickers[r7] = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0082, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0083, code lost:
        if (r8 == false) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0085, code lost:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs();
        r8.hash = calcDocumentsHash(r6.recentGifs);
        getConnectionsManager().sendRequest(r8, new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda152());
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00a0, code lost:
        if (r7 != 2) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00a2, code lost:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers();
        r8.hash = calcDocumentsHash(r6.recentStickers[r7]);
        r8 = r8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00b2, code lost:
        if (r7 != 3) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00b4, code lost:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers();
        r8.emoticon = "👋" + org.telegram.messenger.Emoji.fixEmoji("⭐");
        r8.hash = calcDocumentsHash(r6.recentStickers[r7]);
        r8 = r8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00df, code lost:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers();
        r8.hash = calcDocumentsHash(r6.recentStickers[r7]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00ee, code lost:
        if (r7 != 1) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00f0, code lost:
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00f1, code lost:
        r8.attached = r0;
        r8 = r8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00f3, code lost:
        getConnectionsManager().sendRequest(r8, new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda151());
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00ff, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x000d, code lost:
        if (r6.recentGifsLoaded != false) goto L14;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadRecents(final int r7, final boolean r8, boolean r9, boolean r10) {
        /*
            Method dump skipped, instructions count: 256
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadRecents(int, boolean, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$loadRecents$34(final boolean z, final int i) {
        NativeByteBuffer byteBufferValue;
        int i2 = 5;
        if (z) {
            i2 = 2;
        } else if (i == 0) {
            i2 = 3;
        } else if (i == 1) {
            i2 = 4;
        } else if (i == 3) {
            i2 = 6;
        } else if (i == 5) {
            i2 = 7;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor queryFinalized = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + i2 + " ORDER BY date DESC", new Object[0]);
            final ArrayList arrayList = new ArrayList();
            while (queryFinalized.next()) {
                if (!queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                    TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (TLdeserialize != null) {
                        arrayList.add(TLdeserialize);
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda117
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadRecents$33(z, arrayList, i);
                }
            });
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$loadRecents$33(boolean z, ArrayList arrayList, int i) {
        if (z) {
            this.recentGifs = arrayList;
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
        } else {
            this.recentStickers[i] = arrayList;
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = true;
        }
        if (i == 3) {
            preloadNextGreetingsSticker();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(z), Integer.valueOf(i));
        loadRecents(i, z, false, false);
    }

    public /* synthetic */ void lambda$loadRecents$35(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        processLoadedRecentDocuments(i, tLObject instanceof TLRPC$TL_messages_savedGifs ? ((TLRPC$TL_messages_savedGifs) tLObject).gifs : null, true, 0, true);
    }

    public /* synthetic */ void lambda$loadRecents$36(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList<TLRPC$Document> arrayList;
        if (i == 3) {
            if (tLObject instanceof TLRPC$TL_messages_stickers) {
                arrayList = ((TLRPC$TL_messages_stickers) tLObject).stickers;
            }
            arrayList = null;
        } else if (i == 2) {
            if (tLObject instanceof TLRPC$TL_messages_favedStickers) {
                arrayList = ((TLRPC$TL_messages_favedStickers) tLObject).stickers;
            }
            arrayList = null;
        } else {
            if (tLObject instanceof TLRPC$TL_messages_recentStickers) {
                arrayList = ((TLRPC$TL_messages_recentStickers) tLObject).stickers;
            }
            arrayList = null;
        }
        processLoadedRecentDocuments(i, arrayList, false, 0, true);
    }

    private void preloadNextGreetingsSticker() {
        if (this.recentStickers[3].isEmpty()) {
            return;
        }
        ArrayList<TLRPC$Document>[] arrayListArr = this.recentStickers;
        this.greetingsSticker = arrayListArr[3].get(Utilities.random.nextInt(arrayListArr[3].size()));
        getFileLoader().loadFile(ImageLocation.getForDocument(this.greetingsSticker), this.greetingsSticker, null, 0, 1);
    }

    public TLRPC$Document getGreetingsSticker() {
        TLRPC$Document tLRPC$Document = this.greetingsSticker;
        preloadNextGreetingsSticker();
        return tLRPC$Document;
    }

    public void processLoadedRecentDocuments(final int i, final ArrayList<TLRPC$Document> arrayList, final boolean z, final int i2, final boolean z2) {
        if (arrayList != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda115
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$processLoadedRecentDocuments$37(z, i, arrayList, z2, i2);
                }
            });
        }
        if (i2 == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda114
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$processLoadedRecentDocuments$38(z, i, arrayList);
                }
            });
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$37(boolean z, int i, ArrayList arrayList, boolean z2, int i2) {
        int i3;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            int i4 = 2;
            int i5 = 3;
            if (z) {
                i3 = getMessagesController().maxRecentGifsCount;
            } else if (i == 3) {
                i3 = 200;
            } else if (i == 2) {
                i3 = getMessagesController().maxFaveStickersCount;
            } else {
                i3 = getMessagesController().maxRecentStickersCount;
            }
            database.beginTransaction();
            SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int size = arrayList.size();
            int i6 = z ? 2 : i == 0 ? 3 : i == 1 ? 4 : i == 3 ? 6 : i == 5 ? 7 : 5;
            if (z2) {
                database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + i6).stepThis().dispose();
            }
            int i7 = 0;
            while (i7 < size && i7 != i3) {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) arrayList.get(i7);
                executeFast.requery();
                StringBuilder sb = new StringBuilder();
                sb.append("");
                int i8 = i7;
                sb.append(tLRPC$Document.id);
                executeFast.bindString(1, sb.toString());
                executeFast.bindInteger(i4, i6);
                executeFast.bindString(i5, "");
                executeFast.bindString(4, "");
                executeFast.bindString(5, "");
                executeFast.bindInteger(6, 0);
                executeFast.bindInteger(7, 0);
                executeFast.bindInteger(8, 0);
                executeFast.bindInteger(9, i2 != 0 ? i2 : size - i8);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Document.getObjectSize());
                tLRPC$Document.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(10, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                i7 = i8 + 1;
                i4 = 2;
                i5 = 3;
            }
            executeFast.dispose();
            database.commitTransaction();
            if (arrayList.size() < i3) {
                return;
            }
            database.beginTransaction();
            while (i3 < arrayList.size()) {
                database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC$Document) arrayList.get(i3)).id + "' AND type = " + i6).stepThis().dispose();
                i3++;
            }
            database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$processLoadedRecentDocuments$38(boolean z, int i, ArrayList arrayList) {
        SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
        if (z) {
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
            edit.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
        } else {
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = true;
            if (i == 0) {
                edit.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            } else if (i == 1) {
                edit.putLong("lastStickersLoadTimeMask", System.currentTimeMillis()).commit();
            } else if (i == 3) {
                edit.putLong("lastStickersLoadTimeGreet", System.currentTimeMillis()).commit();
            } else {
                edit.putLong("lastStickersLoadTimeFavs", System.currentTimeMillis()).commit();
            }
        }
        if (arrayList != null) {
            if (z) {
                this.recentGifs = arrayList;
            } else {
                this.recentStickers[i] = arrayList;
            }
            if (i == 3) {
                preloadNextGreetingsSticker();
            }
            getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(z), Integer.valueOf(i));
        }
    }

    public void reorderStickers(int i, final ArrayList<Long> arrayList) {
        Collections.sort(this.stickerSets[i], new Comparator() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda131
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$reorderStickers$39;
                lambda$reorderStickers$39 = MediaDataController.lambda$reorderStickers$39(arrayList, (TLRPC$TL_messages_stickerSet) obj, (TLRPC$TL_messages_stickerSet) obj2);
                return lambda$reorderStickers$39;
            }
        });
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
        loadStickers(i, false, true);
    }

    public static /* synthetic */ int lambda$reorderStickers$39(ArrayList arrayList, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2) {
        int indexOf = arrayList.indexOf(Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
        int indexOf2 = arrayList.indexOf(Long.valueOf(tLRPC$TL_messages_stickerSet2.set.id));
        if (indexOf > indexOf2) {
            return 1;
        }
        return indexOf < indexOf2 ? -1 : 0;
    }

    public void calcNewHash(int i) {
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
    }

    public void storeTempStickerSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.stickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        this.stickerSetsByName.put(tLRPC$TL_messages_stickerSet.set.short_name, tLRPC$TL_messages_stickerSet);
    }

    public void addNewStickerSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        int i;
        if (this.stickerSetsById.indexOfKey(tLRPC$TL_messages_stickerSet.set.id) >= 0 || this.stickerSetsByName.containsKey(tLRPC$TL_messages_stickerSet.set.short_name)) {
            return;
        }
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        if (tLRPC$StickerSet.masks) {
            i = 1;
        } else {
            i = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        this.stickerSets[i].add(0, tLRPC$TL_messages_stickerSet);
        this.stickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        this.installedStickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        this.stickerSetsByName.put(tLRPC$TL_messages_stickerSet.set.short_name, tLRPC$TL_messages_stickerSet);
        LongSparseArray longSparseArray = new LongSparseArray();
        for (int i2 = 0; i2 < tLRPC$TL_messages_stickerSet.documents.size(); i2++) {
            TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
            longSparseArray.put(tLRPC$Document.id, tLRPC$Document);
        }
        for (int i3 = 0; i3 < tLRPC$TL_messages_stickerSet.packs.size(); i3++) {
            TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i3);
            String replace = tLRPC$TL_stickerPack.emoticon.replace("️", "");
            tLRPC$TL_stickerPack.emoticon = replace;
            ArrayList<TLRPC$Document> arrayList = this.allStickers.get(replace);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.allStickers.put(tLRPC$TL_stickerPack.emoticon, arrayList);
            }
            for (int i4 = 0; i4 < tLRPC$TL_stickerPack.documents.size(); i4++) {
                Long l = tLRPC$TL_stickerPack.documents.get(i4);
                if (this.stickersByEmoji.indexOfKey(l.longValue()) < 0) {
                    this.stickersByEmoji.put(l.longValue(), tLRPC$TL_stickerPack.emoticon);
                }
                TLRPC$Document tLRPC$Document2 = (TLRPC$Document) longSparseArray.get(l.longValue());
                if (tLRPC$Document2 != null) {
                    arrayList.add(tLRPC$Document2);
                }
            }
        }
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
        loadStickers(i, false, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void loadFeaturedStickers(final boolean z, boolean z2, boolean z3) {
        TLRPC$TL_messages_getFeaturedStickers tLRPC$TL_messages_getFeaturedStickers;
        boolean[] zArr = this.loadingFeaturedStickers;
        if (zArr[z ? 1 : 0]) {
            return;
        }
        zArr[z] = true;
        if (z2) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda111
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadFeaturedStickers$40(z);
                }
            });
            return;
        }
        final long j = 0;
        if (z != 0) {
            TLRPC$TL_messages_getFeaturedEmojiStickers tLRPC$TL_messages_getFeaturedEmojiStickers = new TLRPC$TL_messages_getFeaturedEmojiStickers();
            if (!z3) {
                j = this.loadFeaturedHash[1];
            }
            tLRPC$TL_messages_getFeaturedEmojiStickers.hash = j;
            tLRPC$TL_messages_getFeaturedStickers = tLRPC$TL_messages_getFeaturedEmojiStickers;
        } else {
            TLRPC$TL_messages_getFeaturedStickers tLRPC$TL_messages_getFeaturedStickers2 = new TLRPC$TL_messages_getFeaturedStickers();
            if (!z3) {
                j = this.loadFeaturedHash[0];
            }
            tLRPC$TL_messages_getFeaturedStickers2.hash = j;
            tLRPC$TL_messages_getFeaturedStickers = tLRPC$TL_messages_getFeaturedStickers2;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getFeaturedStickers, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda179
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadFeaturedStickers$42(z, j, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x00ab A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadFeaturedStickers$40(boolean r15) {
        /*
            r14 = this;
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0 = 0
            r1 = 0
            r4 = 0
            org.telegram.messenger.MessagesStorage r2 = r14.getMessagesStorage()     // Catch: java.lang.Throwable -> La1
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch: java.lang.Throwable -> La1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La1
            r6.<init>()     // Catch: java.lang.Throwable -> La1
            java.lang.String r7 = "SELECT data, unread, date, hash, premium FROM stickers_featured WHERE emoji = "
            r6.append(r7)     // Catch: java.lang.Throwable -> La1
            r7 = 1
            if (r15 == 0) goto L20
            r8 = 1
            goto L21
        L20:
            r8 = 0
        L21:
            r6.append(r8)     // Catch: java.lang.Throwable -> La1
            java.lang.String r6 = r6.toString()     // Catch: java.lang.Throwable -> La1
            java.lang.Object[] r8 = new java.lang.Object[r1]     // Catch: java.lang.Throwable -> La1
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r6, r8)     // Catch: java.lang.Throwable -> La1
            boolean r6 = r2.next()     // Catch: java.lang.Throwable -> L9c
            if (r6 == 0) goto L93
            org.telegram.tgnet.NativeByteBuffer r6 = r2.byteBufferValue(r1)     // Catch: java.lang.Throwable -> L9c
            if (r6 == 0) goto L5b
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L9c
            r8.<init>()     // Catch: java.lang.Throwable -> L9c
            int r0 = r6.readInt32(r1)     // Catch: java.lang.Throwable -> L59
            r9 = 0
        L44:
            if (r9 >= r0) goto L54
            int r10 = r6.readInt32(r1)     // Catch: java.lang.Throwable -> L59
            org.telegram.tgnet.TLRPC$StickerSetCovered r10 = org.telegram.tgnet.TLRPC$StickerSetCovered.TLdeserialize(r6, r10, r1)     // Catch: java.lang.Throwable -> L59
            r8.add(r10)     // Catch: java.lang.Throwable -> L59
            int r9 = r9 + 1
            goto L44
        L54:
            r6.reuse()     // Catch: java.lang.Throwable -> L59
            r0 = r8
            goto L5b
        L59:
            r0 = move-exception
            goto L9f
        L5b:
            org.telegram.tgnet.NativeByteBuffer r6 = r2.byteBufferValue(r7)     // Catch: java.lang.Throwable -> L9c
            if (r6 == 0) goto L79
            int r8 = r6.readInt32(r1)     // Catch: java.lang.Throwable -> L9c
            r9 = 0
        L66:
            if (r9 >= r8) goto L76
            long r10 = r6.readInt64(r1)     // Catch: java.lang.Throwable -> L9c
            java.lang.Long r10 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L9c
            r3.add(r10)     // Catch: java.lang.Throwable -> L9c
            int r9 = r9 + 1
            goto L66
        L76:
            r6.reuse()     // Catch: java.lang.Throwable -> L9c
        L79:
            r6 = 2
            int r6 = r2.intValue(r6)     // Catch: java.lang.Throwable -> L9c
            long r4 = r14.calcFeaturedStickersHash(r15, r0)     // Catch: java.lang.Throwable -> L8f
            r8 = 4
            int r8 = r2.intValue(r8)     // Catch: java.lang.Throwable -> L8f
            if (r8 != r7) goto L8a
            r1 = 1
        L8a:
            r12 = r4
            r4 = r1
            r1 = r6
            r5 = r12
            goto L95
        L8f:
            r7 = move-exception
            r8 = r0
            r0 = r7
            goto La6
        L93:
            r5 = r4
            r4 = 0
        L95:
            r2.dispose()
            r2 = r0
            r7 = r5
            r6 = r1
            goto Lb1
        L9c:
            r6 = move-exception
            r8 = r0
            r0 = r6
        L9f:
            r6 = 0
            goto La6
        La1:
            r2 = move-exception
            r8 = r0
            r6 = 0
            r0 = r2
            r2 = r8
        La6:
            org.telegram.messenger.FileLog.e(r0)     // Catch: java.lang.Throwable -> Lb8
            if (r2 == 0) goto Lae
            r2.dispose()
        Lae:
            r2 = r8
            r7 = r4
            r4 = 0
        Lb1:
            r5 = 1
            r0 = r14
            r1 = r15
            r0.processLoadedFeaturedStickers(r1, r2, r3, r4, r5, r6, r7)
            return
        Lb8:
            r15 = move-exception
            if (r2 == 0) goto Lbe
            r2.dispose()
        Lbe:
            goto Lc0
        Lbf:
            throw r15
        Lc0:
            goto Lbf
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadFeaturedStickers$40(boolean):void");
    }

    public /* synthetic */ void lambda$loadFeaturedStickers$42(final boolean z, final long j, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda83
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadFeaturedStickers$41(tLObject, z, j);
            }
        });
    }

    public /* synthetic */ void lambda$loadFeaturedStickers$41(TLObject tLObject, boolean z, long j) {
        if (tLObject instanceof TLRPC$TL_messages_featuredStickers) {
            TLRPC$TL_messages_featuredStickers tLRPC$TL_messages_featuredStickers = (TLRPC$TL_messages_featuredStickers) tLObject;
            processLoadedFeaturedStickers(z, tLRPC$TL_messages_featuredStickers.sets, tLRPC$TL_messages_featuredStickers.unread, tLRPC$TL_messages_featuredStickers.premium, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_featuredStickers.hash);
            return;
        }
        processLoadedFeaturedStickers(z, null, null, false, false, (int) (System.currentTimeMillis() / 1000), j);
    }

    private void processLoadedFeaturedStickers(final boolean z, final ArrayList<TLRPC$StickerSetCovered> arrayList, final ArrayList<Long> arrayList2, final boolean z2, final boolean z3, final int i, final long j) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda110
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedFeaturedStickers$43(z);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda119
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedFeaturedStickers$47(z3, arrayList, i, j, z, arrayList2, z2);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$43(boolean z) {
        this.loadingFeaturedStickers[z ? 1 : 0] = false;
        this.featuredStickersLoaded[z] = true;
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$47(boolean z, final ArrayList arrayList, final int i, final long j, final boolean z2, final ArrayList arrayList2, final boolean z3) {
        long j2 = 0;
        if ((z && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - i) >= 3600)) || (!z && arrayList == null && j == 0)) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda70
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$processLoadedFeaturedStickers$44(arrayList, j, z2);
                }
            };
            if (arrayList == null && !z) {
                j2 = 1000;
            }
            AndroidUtilities.runOnUIThread(runnable, j2);
            if (arrayList == null) {
                return;
            }
        }
        if (arrayList != null) {
            try {
                final ArrayList<TLRPC$StickerSetCovered> arrayList3 = new ArrayList<>();
                final LongSparseArray longSparseArray = new LongSparseArray();
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) arrayList.get(i2);
                    arrayList3.add(tLRPC$StickerSetCovered);
                    longSparseArray.put(tLRPC$StickerSetCovered.set.id, tLRPC$StickerSetCovered);
                }
                if (!z) {
                    putFeaturedStickersToCache(z2, arrayList3, arrayList2, i, j, z3);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda121
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.lambda$processLoadedFeaturedStickers$45(z2, arrayList2, longSparseArray, arrayList3, j, i, z3);
                    }
                });
            } catch (Throwable th) {
                FileLog.e(th);
            }
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda113
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedFeaturedStickers$46(z2, i);
            }
        });
        putFeaturedStickersToCache(z2, null, null, i, 0L, z3);
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$44(ArrayList arrayList, long j, boolean z) {
        if (arrayList != null && j != 0) {
            this.loadFeaturedHash[z ? 1 : 0] = j;
        }
        loadFeaturedStickers(z, false, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$45(boolean z, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, long j, int i, boolean z2) {
        this.unreadStickerSets[z ? 1 : 0] = arrayList;
        this.featuredStickerSetsById[z] = longSparseArray;
        this.featuredStickerSets[z] = arrayList2;
        this.loadFeaturedHash[z] = j;
        this.loadFeaturedDate[z] = i;
        this.loadFeaturedPremium = z2;
        loadStickers(z != 0 ? 6 : 3, true, false);
        getNotificationCenter().postNotificationName(z != 0 ? NotificationCenter.featuredEmojiDidLoad : NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$46(boolean z, int i) {
        this.loadFeaturedDate[z ? 1 : 0] = i;
    }

    private void putFeaturedStickersToCache(final boolean z, ArrayList<TLRPC$StickerSetCovered> arrayList, final ArrayList<Long> arrayList2, final int i, final long j, final boolean z2) {
        final ArrayList arrayList3 = arrayList != null ? new ArrayList(arrayList) : null;
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda71
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putFeaturedStickersToCache$48(arrayList3, arrayList2, i, j, z2, z);
            }
        });
    }

    public /* synthetic */ void lambda$putFeaturedStickersToCache$48(ArrayList arrayList, ArrayList arrayList2, int i, long j, boolean z, boolean z2) {
        int i2 = 1;
        try {
            if (arrayList != null) {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?, ?, ?)");
                executeFast.requery();
                int i3 = 4;
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    i3 += ((TLRPC$StickerSetCovered) arrayList.get(i4)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer((arrayList2.size() * 8) + 4);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((TLRPC$StickerSetCovered) arrayList.get(i5)).serializeToStream(nativeByteBuffer);
                }
                nativeByteBuffer2.writeInt32(arrayList2.size());
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    nativeByteBuffer2.writeInt64(((Long) arrayList2.get(i6)).longValue());
                }
                executeFast.bindInteger(1, 1);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindByteBuffer(3, nativeByteBuffer2);
                executeFast.bindInteger(4, i);
                executeFast.bindLong(5, j);
                executeFast.bindInteger(6, z ? 1 : 0);
                if (!z2) {
                    i2 = 0;
                }
                executeFast.bindInteger(7, i2);
                executeFast.step();
                nativeByteBuffer.reuse();
                nativeByteBuffer2.reuse();
                executeFast.dispose();
                return;
            }
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            executeFast2.requery();
            executeFast2.bindInteger(1, i);
            executeFast2.step();
            executeFast2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private long calcFeaturedStickersHash(boolean z, ArrayList<TLRPC$StickerSetCovered> arrayList) {
        long j = 0;
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$StickerSet tLRPC$StickerSet = arrayList.get(i).set;
                if (!tLRPC$StickerSet.archived) {
                    j = calcHash(j, tLRPC$StickerSet.id);
                    if (this.unreadStickerSets[z ? 1 : 0].contains(Long.valueOf(tLRPC$StickerSet.id))) {
                        j = calcHash(j, 1L);
                    }
                }
            }
        }
        return j;
    }

    public void markFeaturedStickersAsRead(boolean z, boolean z2) {
        if (this.unreadStickerSets[z ? 1 : 0].isEmpty()) {
            return;
        }
        this.unreadStickerSets[z].clear();
        this.loadFeaturedHash[z] = calcFeaturedStickersHash(z, this.featuredStickerSets[z]);
        getNotificationCenter().postNotificationName(z != 0 ? NotificationCenter.featuredEmojiDidLoad : NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(z, this.featuredStickerSets[z], this.unreadStickerSets[z], this.loadFeaturedDate[z], this.loadFeaturedHash[z], this.loadFeaturedPremium);
        if (!z2) {
            return;
        }
        getConnectionsManager().sendRequest(new TLRPC$TL_messages_readFeaturedStickers(), MediaDataController$$ExternalSyntheticLambda181.INSTANCE);
    }

    public long getFeaturedStickersHashWithoutUnread(boolean z) {
        long j = 0;
        for (int i = 0; i < this.featuredStickerSets[z ? 1 : 0].size(); i++) {
            TLRPC$StickerSet tLRPC$StickerSet = this.featuredStickerSets[z].get(i).set;
            if (!tLRPC$StickerSet.archived) {
                j = calcHash(j, tLRPC$StickerSet.id);
            }
        }
        return j;
    }

    public void markFeaturedStickersByIdAsRead(final boolean z, final long j) {
        if (!this.unreadStickerSets[z ? 1 : 0].contains(Long.valueOf(j)) || this.readingStickerSets[z].contains(Long.valueOf(j))) {
            return;
        }
        this.readingStickerSets[z].add(Long.valueOf(j));
        TLRPC$TL_messages_readFeaturedStickers tLRPC$TL_messages_readFeaturedStickers = new TLRPC$TL_messages_readFeaturedStickers();
        tLRPC$TL_messages_readFeaturedStickers.id.add(Long.valueOf(j));
        getConnectionsManager().sendRequest(tLRPC$TL_messages_readFeaturedStickers, MediaDataController$$ExternalSyntheticLambda182.INSTANCE);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda116
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$markFeaturedStickersByIdAsRead$51(z, j);
            }
        }, 1000L);
    }

    public /* synthetic */ void lambda$markFeaturedStickersByIdAsRead$51(boolean z, long j) {
        this.unreadStickerSets[z ? 1 : 0].remove(Long.valueOf(j));
        this.readingStickerSets[z].remove(Long.valueOf(j));
        this.loadFeaturedHash[z] = calcFeaturedStickersHash(z, this.featuredStickerSets[z]);
        getNotificationCenter().postNotificationName(z != 0 ? NotificationCenter.featuredEmojiDidLoad : NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(z, this.featuredStickerSets[z], this.unreadStickerSets[z], this.loadFeaturedDate[z], this.loadFeaturedHash[z], this.loadFeaturedPremium);
    }

    public int getArchivedStickersCount(int i) {
        return this.archivedStickersCount[i];
    }

    public void verifyAnimatedStickerMessage(TLRPC$Message tLRPC$Message) {
        verifyAnimatedStickerMessage(tLRPC$Message, false);
    }

    public void verifyAnimatedStickerMessage(final TLRPC$Message tLRPC$Message, boolean z) {
        if (tLRPC$Message == null) {
            return;
        }
        TLRPC$Document document = MessageObject.getDocument(tLRPC$Message);
        final String stickerSetName = MessageObject.getStickerSetName(document);
        if (TextUtils.isEmpty(stickerSetName)) {
            return;
        }
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSetsByName.get(stickerSetName);
        if (tLRPC$TL_messages_stickerSet == null) {
            if (z) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda89
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.lambda$verifyAnimatedStickerMessage$52(tLRPC$Message, stickerSetName);
                    }
                });
                return;
            } else {
                lambda$verifyAnimatedStickerMessage$52(tLRPC$Message, stickerSetName);
                return;
            }
        }
        int size = tLRPC$TL_messages_stickerSet.documents.size();
        for (int i = 0; i < size; i++) {
            TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i);
            if (tLRPC$Document.id == document.id && tLRPC$Document.dc_id == document.dc_id) {
                tLRPC$Message.stickerVerified = 1;
                return;
            }
        }
    }

    /* renamed from: verifyAnimatedStickerMessageInternal */
    public void lambda$verifyAnimatedStickerMessage$52(TLRPC$Message tLRPC$Message, final String str) {
        ArrayList<TLRPC$Message> arrayList = this.verifyingMessages.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.verifyingMessages.put(str, arrayList);
        }
        arrayList.add(tLRPC$Message);
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        tLRPC$TL_messages_getStickerSet.stickerset = MessageObject.getInputStickerSet(tLRPC$Message);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda169
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$verifyAnimatedStickerMessageInternal$54(str, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$verifyAnimatedStickerMessageInternal$54(final String str, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda58
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$verifyAnimatedStickerMessageInternal$53(str, tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$verifyAnimatedStickerMessageInternal$53(String str, TLObject tLObject) {
        ArrayList<TLRPC$Message> arrayList = this.verifyingMessages.get(str);
        if (tLObject != null) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            storeTempStickerSet(tLRPC$TL_messages_stickerSet);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Message tLRPC$Message = arrayList.get(i);
                TLRPC$Document document = MessageObject.getDocument(tLRPC$Message);
                int size2 = tLRPC$TL_messages_stickerSet.documents.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size2) {
                        break;
                    }
                    TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
                    if (tLRPC$Document.id == document.id && tLRPC$Document.dc_id == document.dc_id) {
                        tLRPC$Message.stickerVerified = 1;
                        break;
                    }
                    i2++;
                }
                if (tLRPC$Message.stickerVerified == 0) {
                    tLRPC$Message.stickerVerified = 2;
                }
            }
        } else {
            int size3 = arrayList.size();
            for (int i3 = 0; i3 < size3; i3++) {
                arrayList.get(i3).stickerVerified = 2;
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.didVerifyMessagesStickers, arrayList);
        getMessagesStorage().updateMessageVerifyFlags(arrayList);
    }

    public void loadArchivedStickersCount(final int i, boolean z) {
        boolean z2 = true;
        if (z) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            int i2 = notificationsSettings.getInt("archivedStickersCount" + i, -1);
            if (i2 == -1) {
                loadArchivedStickersCount(i, false);
                return;
            }
            this.archivedStickersCount[i] = i2;
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
            return;
        }
        TLRPC$TL_messages_getArchivedStickers tLRPC$TL_messages_getArchivedStickers = new TLRPC$TL_messages_getArchivedStickers();
        tLRPC$TL_messages_getArchivedStickers.limit = 0;
        tLRPC$TL_messages_getArchivedStickers.masks = i == 1;
        if (i != 5) {
            z2 = false;
        }
        tLRPC$TL_messages_getArchivedStickers.emojis = z2;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getArchivedStickers, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda150
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadArchivedStickersCount$56(i, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadArchivedStickersCount$56(final int i, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda98
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadArchivedStickersCount$55(tLRPC$TL_error, tLObject, i);
            }
        });
    }

    public /* synthetic */ void lambda$loadArchivedStickersCount$55(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_archivedStickers tLRPC$TL_messages_archivedStickers = (TLRPC$TL_messages_archivedStickers) tLObject;
            this.archivedStickersCount[i] = tLRPC$TL_messages_archivedStickers.count;
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putInt("archivedStickersCount" + i, tLRPC$TL_messages_archivedStickers.count).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
        }
    }

    private void processLoadStickersResponse(final int i, final TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers) {
        final ArrayList<TLRPC$TL_messages_stickerSet> arrayList = new ArrayList<>();
        long j = 1000;
        if (tLRPC$TL_messages_allStickers.sets.isEmpty()) {
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_allStickers.hash);
        } else {
            final LongSparseArray longSparseArray = new LongSparseArray();
            int i2 = 0;
            while (i2 < tLRPC$TL_messages_allStickers.sets.size()) {
                final TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_allStickers.sets.get(i2);
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSetsById.get(tLRPC$StickerSet.id);
                if (tLRPC$TL_messages_stickerSet != null) {
                    TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set;
                    if (tLRPC$StickerSet2.hash == tLRPC$StickerSet.hash) {
                        tLRPC$StickerSet2.archived = tLRPC$StickerSet.archived;
                        tLRPC$StickerSet2.installed = tLRPC$StickerSet.installed;
                        tLRPC$StickerSet2.official = tLRPC$StickerSet.official;
                        longSparseArray.put(tLRPC$StickerSet2.id, tLRPC$TL_messages_stickerSet);
                        arrayList.add(tLRPC$TL_messages_stickerSet);
                        if (longSparseArray.size() == tLRPC$TL_messages_allStickers.sets.size()) {
                            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / j), tLRPC$TL_messages_allStickers.hash);
                        }
                        i2++;
                        j = 1000;
                    }
                }
                arrayList.add(null);
                TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                final int i3 = i2;
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda172
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MediaDataController.this.lambda$processLoadStickersResponse$58(arrayList, i3, longSparseArray, tLRPC$StickerSet, tLRPC$TL_messages_allStickers, i, tLObject, tLRPC$TL_error);
                    }
                });
                i2++;
                j = 1000;
            }
        }
    }

    public /* synthetic */ void lambda$processLoadStickersResponse$58(final ArrayList arrayList, final int i, final LongSparseArray longSparseArray, final TLRPC$StickerSet tLRPC$StickerSet, final TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers, final int i2, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda81
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadStickersResponse$57(tLObject, arrayList, i, longSparseArray, tLRPC$StickerSet, tLRPC$TL_messages_allStickers, i2);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadStickersResponse$57(TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers, int i2) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
        arrayList.set(i, tLRPC$TL_messages_stickerSet);
        longSparseArray.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
        if (longSparseArray.size() == tLRPC$TL_messages_allStickers.sets.size()) {
            int i3 = 0;
            while (i3 < arrayList.size()) {
                if (arrayList.get(i3) == null) {
                    arrayList.remove(i3);
                    i3--;
                }
                i3++;
            }
            processLoadedStickers(i2, arrayList, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_allStickers.hash);
        }
    }

    public void checkPremiumGiftStickers() {
        if (this.loadingPremiumGiftStickers || getUserConfig().premiumGiftsStickerPack != null || System.currentTimeMillis() - getUserConfig().lastUpdatedPremiumGiftsStickerPack < 86400000) {
            return;
        }
        this.loadingPremiumGiftStickers = true;
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        tLRPC$TL_messages_getStickerSet.stickerset = new TLRPC$TL_inputStickerSetPremiumGifts();
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda145
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$checkPremiumGiftStickers$60(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$checkPremiumGiftStickers$60(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda78
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$checkPremiumGiftStickers$59(tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$checkPremiumGiftStickers$59(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            getUserConfig().premiumGiftsStickerPack = ((TLRPC$TL_messages_stickerSet) tLObject).set.short_name;
            getUserConfig().lastUpdatedPremiumGiftsStickerPack = System.currentTimeMillis();
            getUserConfig().saveConfig(false);
            getNotificationCenter().postNotificationName(NotificationCenter.didUpdatePremiumGiftStickers, new Object[0]);
        }
    }

    public void loadStickersByEmojiOrName(final String str, final boolean z, boolean z2) {
        if (!this.loadingDiceStickerSets.contains(str)) {
            if (z && this.diceStickerSetsByEmoji.get(str) != null) {
                return;
            }
            this.loadingDiceStickerSets.add(str);
            if (z2) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda60
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.lambda$loadStickersByEmojiOrName$61(str, z);
                    }
                });
                return;
            }
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            if (z) {
                TLRPC$TL_inputStickerSetDice tLRPC$TL_inputStickerSetDice = new TLRPC$TL_inputStickerSetDice();
                tLRPC$TL_inputStickerSetDice.emoticon = str;
                tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetDice;
            } else {
                TLRPC$TL_inputStickerSetShortName tLRPC$TL_inputStickerSetShortName = new TLRPC$TL_inputStickerSetShortName();
                tLRPC$TL_inputStickerSetShortName.short_name = str;
                tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetShortName;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda171
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MediaDataController.this.lambda$loadStickersByEmojiOrName$63(str, z, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadStickersByEmojiOrName$61(String str, boolean z) {
        Throwable th;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2;
        int i;
        SQLiteCursor sQLiteCursor = null;
        r0 = null;
        r0 = null;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet3 = null;
        int i2 = 0;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT data, date FROM stickers_dice WHERE emoji = ?", str);
            try {
                if (queryFinalized.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        tLRPC$TL_messages_stickerSet3 = TLRPC$messages_StickerSet.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                    }
                    i2 = queryFinalized.intValue(1);
                }
                queryFinalized.dispose();
                tLRPC$TL_messages_stickerSet2 = tLRPC$TL_messages_stickerSet3;
                i = i2;
            } catch (Throwable th2) {
                th = th2;
                tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet3;
                sQLiteCursor = queryFinalized;
                try {
                    FileLog.e(th);
                    tLRPC$TL_messages_stickerSet2 = tLRPC$TL_messages_stickerSet;
                    i = 0;
                    processLoadedDiceStickers(str, z, tLRPC$TL_messages_stickerSet2, true, i);
                } finally {
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                }
            }
        } catch (Throwable th3) {
            th = th3;
            tLRPC$TL_messages_stickerSet = null;
        }
        processLoadedDiceStickers(str, z, tLRPC$TL_messages_stickerSet2, true, i);
    }

    public /* synthetic */ void lambda$loadStickersByEmojiOrName$63(final String str, final boolean z, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda99
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadStickersByEmojiOrName$62(tLRPC$TL_error, tLObject, str, z);
            }
        });
    }

    public /* synthetic */ void lambda$loadStickersByEmojiOrName$62(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, boolean z) {
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            processLoadedDiceStickers(str, z, (TLRPC$TL_messages_stickerSet) tLObject, false, (int) (System.currentTimeMillis() / 1000));
        } else {
            processLoadedDiceStickers(str, z, null, false, (int) (System.currentTimeMillis() / 1000));
        }
    }

    public /* synthetic */ void lambda$processLoadedDiceStickers$64(String str) {
        this.loadingDiceStickerSets.remove(str);
    }

    private void processLoadedDiceStickers(final String str, final boolean z, final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, final boolean z2, final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda55
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedDiceStickers$64(str);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda122
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedDiceStickers$67(z2, tLRPC$TL_messages_stickerSet, i, str, z);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedDiceStickers$67(boolean z, final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i, final String str, final boolean z2) {
        long j = 1000;
        if ((z && (tLRPC$TL_messages_stickerSet == null || Math.abs((System.currentTimeMillis() / 1000) - i) >= 86400)) || (!z && tLRPC$TL_messages_stickerSet == null)) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda61
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$processLoadedDiceStickers$65(str, z2);
                }
            };
            if (tLRPC$TL_messages_stickerSet != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(runnable, j);
            if (tLRPC$TL_messages_stickerSet == null) {
                return;
            }
        }
        if (tLRPC$TL_messages_stickerSet != null) {
            if (!z) {
                putDiceStickersToCache(str, tLRPC$TL_messages_stickerSet, i);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda59
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$processLoadedDiceStickers$66(str, tLRPC$TL_messages_stickerSet);
                }
            });
        } else if (z) {
        } else {
            putDiceStickersToCache(str, null, i);
        }
    }

    public /* synthetic */ void lambda$processLoadedDiceStickers$65(String str, boolean z) {
        loadStickersByEmojiOrName(str, z, false);
    }

    public /* synthetic */ void lambda$processLoadedDiceStickers$66(String str, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.diceStickerSetsByEmoji.put(str, tLRPC$TL_messages_stickerSet);
        this.diceEmojiStickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, str);
        getNotificationCenter().postNotificationName(NotificationCenter.diceStickersDidLoad, str);
    }

    private void putDiceStickersToCache(final String str, final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, final int i) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda106
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putDiceStickersToCache$68(tLRPC$TL_messages_stickerSet, str, i);
            }
        });
    }

    public /* synthetic */ void lambda$putDiceStickersToCache$68(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, String str, int i) {
        try {
            if (tLRPC$TL_messages_stickerSet != null) {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_dice VALUES(?, ?, ?)");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_messages_stickerSet.getObjectSize());
                tLRPC$TL_messages_stickerSet.serializeToStream(nativeByteBuffer);
                executeFast.bindString(1, str);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, i);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } else {
                SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_dice SET date = ?");
                executeFast2.requery();
                executeFast2.bindInteger(1, i);
                executeFast2.step();
                executeFast2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void loadStickers(int i, boolean z, boolean z2) {
        loadStickers(i, z, z2, false, null);
    }

    public void loadStickers(int i, boolean z, boolean z2, boolean z3) {
        loadStickers(i, z, z2, z3, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void loadStickers(final int i, boolean z, final boolean z2, boolean z3, final Utilities.Callback<ArrayList<TLRPC$TL_messages_stickerSet>> callback) {
        TLRPC$TL_messages_getMaskStickers tLRPC$TL_messages_getMaskStickers;
        if (this.loadingStickers[i]) {
            if (!z3) {
                return;
            }
            this.scheduledLoadStickers[i] = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadStickers$69(i, z2, callback);
                }
            };
            return;
        }
        char c = 1;
        if (i == 3) {
            if (this.featuredStickerSets[0].isEmpty() || !getMessagesController().preloadFeaturedStickers) {
                return;
            }
        } else if (i == 6) {
            if (this.featuredStickerSets[1].isEmpty() || !getMessagesController().preloadFeaturedStickers) {
                return;
            }
        } else if (i != 4) {
            loadArchivedStickersCount(i, z);
        }
        this.loadingStickers[i] = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadStickers$70(i, callback);
                }
            });
        } else if (i == 3 || i == 6) {
            if (i != 6) {
                c = 0;
            }
            TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers = new TLRPC$TL_messages_allStickers();
            tLRPC$TL_messages_allStickers.hash = this.loadFeaturedHash[c];
            int size = this.featuredStickerSets[c].size();
            for (int i2 = 0; i2 < size; i2++) {
                tLRPC$TL_messages_allStickers.sets.add(this.featuredStickerSets[c].get(i2).set);
            }
            processLoadStickersResponse(i, tLRPC$TL_messages_allStickers);
            if (callback == null) {
                return;
            }
            callback.run(null);
        } else if (i == 4) {
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            tLRPC$TL_messages_getStickerSet.stickerset = new TLRPC$TL_inputStickerSetAnimatedEmoji();
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda173
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MediaDataController.this.lambda$loadStickers$71(callback, i, tLObject, tLRPC$TL_error);
                }
            });
        } else {
            long j = 0;
            if (i == 0) {
                TLRPC$TL_messages_getAllStickers tLRPC$TL_messages_getAllStickers = new TLRPC$TL_messages_getAllStickers();
                if (!z2) {
                    j = this.loadHash[i];
                }
                tLRPC$TL_messages_getAllStickers.hash = j;
                tLRPC$TL_messages_getMaskStickers = tLRPC$TL_messages_getAllStickers;
            } else if (i == 5) {
                TLRPC$TL_messages_getEmojiStickers tLRPC$TL_messages_getEmojiStickers = new TLRPC$TL_messages_getEmojiStickers();
                if (!z2) {
                    j = this.loadHash[i];
                }
                tLRPC$TL_messages_getEmojiStickers.hash = j;
                tLRPC$TL_messages_getMaskStickers = tLRPC$TL_messages_getEmojiStickers;
            } else {
                TLRPC$TL_messages_getMaskStickers tLRPC$TL_messages_getMaskStickers2 = new TLRPC$TL_messages_getMaskStickers();
                if (!z2) {
                    j = this.loadHash[i];
                }
                tLRPC$TL_messages_getMaskStickers2.hash = j;
                tLRPC$TL_messages_getMaskStickers = tLRPC$TL_messages_getMaskStickers2;
            }
            final long j2 = j;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getMaskStickers, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda174
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MediaDataController.this.lambda$loadStickers$73(callback, i, j2, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadStickers$69(int i, boolean z, Utilities.Callback callback) {
        loadStickers(i, false, z, false, callback);
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0077  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadStickers$70(int r16, org.telegram.messenger.Utilities.Callback r17) {
        /*
            r15 = this;
            r1 = r17
            r2 = 0
            r3 = 0
            r4 = 0
            org.telegram.messenger.MessagesStorage r0 = r15.getMessagesStorage()     // Catch: java.lang.Throwable -> L68
            org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch: java.lang.Throwable -> L68
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L68
            r6.<init>()     // Catch: java.lang.Throwable -> L68
            java.lang.String r7 = "SELECT data, date, hash FROM stickers_v2 WHERE id = "
            r6.append(r7)     // Catch: java.lang.Throwable -> L68
            int r7 = r16 + 1
            r6.append(r7)     // Catch: java.lang.Throwable -> L68
            java.lang.String r6 = r6.toString()     // Catch: java.lang.Throwable -> L68
            java.lang.Object[] r7 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L68
            org.telegram.SQLite.SQLiteCursor r6 = r0.queryFinalized(r6, r7)     // Catch: java.lang.Throwable -> L68
            boolean r0 = r6.next()     // Catch: java.lang.Throwable -> L64
            if (r0 == 0) goto L5d
            org.telegram.tgnet.NativeByteBuffer r0 = r6.byteBufferValue(r3)     // Catch: java.lang.Throwable -> L64
            if (r0 == 0) goto L54
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L64
            r7.<init>()     // Catch: java.lang.Throwable -> L64
            int r2 = r0.readInt32(r3)     // Catch: java.lang.Throwable -> L52
            r8 = 0
        L3d:
            if (r8 >= r2) goto L4d
            int r9 = r0.readInt32(r3)     // Catch: java.lang.Throwable -> L52
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r9 = org.telegram.tgnet.TLRPC$messages_StickerSet.TLdeserialize(r0, r9, r3)     // Catch: java.lang.Throwable -> L52
            r7.add(r9)     // Catch: java.lang.Throwable -> L52
            int r8 = r8 + 1
            goto L3d
        L4d:
            r0.reuse()     // Catch: java.lang.Throwable -> L52
            r2 = r7
            goto L54
        L52:
            r0 = move-exception
            goto L66
        L54:
            r0 = 1
            int r3 = r6.intValue(r0)     // Catch: java.lang.Throwable -> L64
            long r4 = calcStickersHash(r2)     // Catch: java.lang.Throwable -> L64
        L5d:
            r6.dispose()
            r10 = r2
            r12 = r3
            r13 = r4
            goto L75
        L64:
            r0 = move-exception
            r7 = r2
        L66:
            r2 = r6
            goto L6a
        L68:
            r0 = move-exception
            r7 = r2
        L6a:
            org.telegram.messenger.FileLog.e(r0)     // Catch: java.lang.Throwable -> L82
            if (r2 == 0) goto L72
            r2.dispose()
        L72:
            r12 = r3
            r13 = r4
            r10 = r7
        L75:
            if (r1 == 0) goto L7a
            r1.run(r10)
        L7a:
            r11 = 1
            r8 = r15
            r9 = r16
            r8.processLoadedStickers(r9, r10, r11, r12, r13)
            return
        L82:
            r0 = move-exception
            r1 = r0
            if (r2 == 0) goto L89
            r2.dispose()
        L89:
            goto L8b
        L8a:
            throw r1
        L8b:
            goto L8a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadStickers$70(int, org.telegram.messenger.Utilities$Callback):void");
    }

    public /* synthetic */ void lambda$loadStickers$71(Utilities.Callback callback, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList = new ArrayList<>();
            arrayList.add((TLRPC$TL_messages_stickerSet) tLObject);
            if (callback != null) {
                callback.run(arrayList);
            }
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), calcStickersHash(arrayList));
            return;
        }
        if (callback != null) {
            callback.run(null);
        }
        processLoadedStickers(i, null, false, (int) (System.currentTimeMillis() / 1000), 0L);
    }

    public /* synthetic */ void lambda$loadStickers$73(final Utilities.Callback callback, final int i, final long j, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda77
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadStickers$72(callback, tLObject, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$loadStickers$72(Utilities.Callback callback, TLObject tLObject, int i, long j) {
        if (callback != null) {
            callback.run(null);
        }
        if (tLObject instanceof TLRPC$TL_messages_allStickers) {
            processLoadStickersResponse(i, (TLRPC$TL_messages_allStickers) tLObject);
        } else {
            processLoadedStickers(i, null, false, (int) (System.currentTimeMillis() / 1000), j);
        }
    }

    private void putStickersToCache(final int i, ArrayList<TLRPC$TL_messages_stickerSet> arrayList, final int i2, final long j) {
        final ArrayList arrayList2 = arrayList != null ? new ArrayList(arrayList) : null;
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda65
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putStickersToCache$74(arrayList2, i, i2, j);
            }
        });
    }

    public /* synthetic */ void lambda$putStickersToCache$74(ArrayList arrayList, int i, int i2, long j) {
        try {
            if (arrayList != null) {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                executeFast.requery();
                int i3 = 4;
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    i3 += ((TLRPC$TL_messages_stickerSet) arrayList.get(i4)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((TLRPC$TL_messages_stickerSet) arrayList.get(i5)).serializeToStream(nativeByteBuffer);
                }
                executeFast.bindInteger(1, i + 1);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, i2);
                executeFast.bindLong(4, j);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
                return;
            }
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
            executeFast2.requery();
            executeFast2.bindLong(1, i2);
            executeFast2.step();
            executeFast2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public String getStickerSetName(long j) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSetsById.get(j);
        if (tLRPC$TL_messages_stickerSet != null) {
            return tLRPC$TL_messages_stickerSet.set.short_name;
        }
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered = this.featuredStickerSetsById[0].get(j);
        if (tLRPC$StickerSetCovered != null) {
            return tLRPC$StickerSetCovered.set.short_name;
        }
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = this.featuredStickerSetsById[1].get(j);
        if (tLRPC$StickerSetCovered2 == null) {
            return null;
        }
        return tLRPC$StickerSetCovered2.set.short_name;
    }

    public static long getStickerSetId(TLRPC$Document tLRPC$Document) {
        for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset;
                if (!(tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetID)) {
                    return -1L;
                }
                return tLRPC$InputStickerSet.id;
            }
        }
        return -1L;
    }

    public static TLRPC$InputStickerSet getInputStickerSet(TLRPC$Document tLRPC$Document) {
        for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset;
                if (!(tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty)) {
                    return tLRPC$InputStickerSet;
                }
                return null;
            }
        }
        return null;
    }

    private static long calcStickersHash(ArrayList<TLRPC$TL_messages_stickerSet> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$StickerSet tLRPC$StickerSet = arrayList.get(i).set;
            if (!tLRPC$StickerSet.archived) {
                j = calcHash(j, tLRPC$StickerSet.hash);
            }
        }
        return j;
    }

    private void processLoadedStickers(final int i, final ArrayList<TLRPC$TL_messages_stickerSet> arrayList, final boolean z, final int i2, final long j) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedStickers$75(i);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda118
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedStickers$79(z, arrayList, i2, j, i);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedStickers$75(int i) {
        this.loadingStickers[i] = false;
        this.stickersLoaded[i] = true;
        Runnable[] runnableArr = this.scheduledLoadStickers;
        if (runnableArr[i] != null) {
            runnableArr[i].run();
            this.scheduledLoadStickers[i] = null;
        }
    }

    public /* synthetic */ void lambda$processLoadedStickers$79(boolean z, final ArrayList arrayList, final int i, final long j, final int i2) {
        int i3;
        String str;
        int i4;
        MediaDataController mediaDataController = this;
        ArrayList arrayList2 = arrayList;
        long j2 = 1000;
        if ((z && (arrayList2 == null || BuildVars.DEBUG_PRIVATE_VERSION || Math.abs((System.currentTimeMillis() / 1000) - i) >= 3600)) || (!z && arrayList2 == null && j == 0)) {
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda68
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$processLoadedStickers$76(arrayList, j, i2);
                }
            };
            if (arrayList2 != null || z) {
                j2 = 0;
            }
            AndroidUtilities.runOnUIThread(runnable, j2);
            if (arrayList2 == null) {
                return;
            }
        }
        if (arrayList2 != null) {
            try {
                final ArrayList<TLRPC$TL_messages_stickerSet> arrayList3 = new ArrayList<>();
                final LongSparseArray longSparseArray = new LongSparseArray();
                final HashMap hashMap = new HashMap();
                final LongSparseArray longSparseArray2 = new LongSparseArray();
                final LongSparseArray longSparseArray3 = new LongSparseArray();
                HashMap hashMap2 = new HashMap();
                int i5 = 0;
                while (i5 < arrayList.size()) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) arrayList2.get(i5);
                    if (tLRPC$TL_messages_stickerSet != null && mediaDataController.removingStickerSetsUndos.indexOfKey(tLRPC$TL_messages_stickerSet.set.id) < 0) {
                        arrayList3.add(tLRPC$TL_messages_stickerSet);
                        longSparseArray.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
                        hashMap.put(tLRPC$TL_messages_stickerSet.set.short_name, tLRPC$TL_messages_stickerSet);
                        int i6 = 0;
                        while (i6 < tLRPC$TL_messages_stickerSet.documents.size()) {
                            TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i6);
                            if (tLRPC$Document != null && !(tLRPC$Document instanceof TLRPC$TL_documentEmpty)) {
                                i4 = i5;
                                longSparseArray3.put(tLRPC$Document.id, tLRPC$Document);
                                i6++;
                                i5 = i4;
                            }
                            i4 = i5;
                            i6++;
                            i5 = i4;
                        }
                        i3 = i5;
                        if (!tLRPC$TL_messages_stickerSet.set.archived) {
                            int i7 = 0;
                            while (i7 < tLRPC$TL_messages_stickerSet.packs.size()) {
                                TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i7);
                                if (tLRPC$TL_stickerPack != null && (str = tLRPC$TL_stickerPack.emoticon) != null) {
                                    String replace = str.replace("️", "");
                                    tLRPC$TL_stickerPack.emoticon = replace;
                                    ArrayList arrayList4 = (ArrayList) hashMap2.get(replace);
                                    if (arrayList4 == null) {
                                        arrayList4 = new ArrayList();
                                        hashMap2.put(tLRPC$TL_stickerPack.emoticon, arrayList4);
                                    }
                                    int i8 = 0;
                                    while (i8 < tLRPC$TL_stickerPack.documents.size()) {
                                        Long l = tLRPC$TL_stickerPack.documents.get(i8);
                                        HashMap hashMap3 = hashMap2;
                                        if (longSparseArray2.indexOfKey(l.longValue()) < 0) {
                                            longSparseArray2.put(l.longValue(), tLRPC$TL_stickerPack.emoticon);
                                        }
                                        TLRPC$Document tLRPC$Document2 = (TLRPC$Document) longSparseArray3.get(l.longValue());
                                        if (tLRPC$Document2 != null) {
                                            arrayList4.add(tLRPC$Document2);
                                        }
                                        i8++;
                                        hashMap2 = hashMap3;
                                    }
                                }
                                i7++;
                                hashMap2 = hashMap2;
                            }
                        }
                        i5 = i3 + 1;
                        mediaDataController = this;
                        arrayList2 = arrayList;
                        hashMap2 = hashMap2;
                    }
                    i3 = i5;
                    i5 = i3 + 1;
                    mediaDataController = this;
                    arrayList2 = arrayList;
                    hashMap2 = hashMap2;
                }
                final HashMap hashMap4 = hashMap2;
                if (!z) {
                    putStickersToCache(i2, arrayList3, i, j);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda21
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.lambda$processLoadedStickers$77(i2, longSparseArray, hashMap, arrayList3, j, i, longSparseArray3, hashMap4, longSparseArray2);
                    }
                });
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (!z) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$processLoadedStickers$78(i2, i);
                }
            });
            putStickersToCache(i2, null, i, 0L);
        }
    }

    public /* synthetic */ void lambda$processLoadedStickers$76(ArrayList arrayList, long j, int i) {
        if (arrayList != null && j != 0) {
            this.loadHash[i] = j;
        }
        loadStickers(i, false, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$processLoadedStickers$77(int i, LongSparseArray longSparseArray, HashMap hashMap, ArrayList arrayList, long j, int i2, LongSparseArray longSparseArray2, HashMap hashMap2, LongSparseArray longSparseArray3) {
        for (int i3 = 0; i3 < this.stickerSets[i].size(); i3++) {
            TLRPC$StickerSet tLRPC$StickerSet = this.stickerSets[i].get(i3).set;
            this.stickerSetsById.remove(tLRPC$StickerSet.id);
            this.stickerSetsByName.remove(tLRPC$StickerSet.short_name);
            if (i != 3 && i != 6 && i != 4) {
                this.installedStickerSetsById.remove(tLRPC$StickerSet.id);
            }
        }
        for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
            this.stickerSetsById.put(longSparseArray.keyAt(i4), (TLRPC$TL_messages_stickerSet) longSparseArray.valueAt(i4));
            if (i != 3 && i != 6 && i != 4) {
                this.installedStickerSetsById.put(longSparseArray.keyAt(i4), (TLRPC$TL_messages_stickerSet) longSparseArray.valueAt(i4));
            }
        }
        this.stickerSetsByName.putAll(hashMap);
        this.stickerSets[i] = arrayList;
        this.loadHash[i] = j;
        this.loadDate[i] = i2;
        this.stickersByIds[i] = longSparseArray2;
        if (i == 0) {
            this.allStickers = hashMap2;
            this.stickersByEmoji = longSparseArray3;
        } else if (i == 3) {
            this.allStickersFeatured = hashMap2;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
    }

    public /* synthetic */ void lambda$processLoadedStickers$78(int i, int i2) {
        this.loadDate[i] = i2;
    }

    public boolean cancelRemovingStickerSet(long j) {
        Runnable runnable = this.removingStickerSetsUndos.get(j);
        if (runnable != null) {
            runnable.run();
            return true;
        }
        return false;
    }

    public void preloadStickerSetThumb(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        ArrayList<TLRPC$Document> arrayList;
        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_messages_stickerSet.set.thumbs, 90);
        if (closestPhotoSizeWithSize == null || (arrayList = tLRPC$TL_messages_stickerSet.documents) == null || arrayList.isEmpty()) {
            return;
        }
        loadStickerSetThumbInternal(closestPhotoSizeWithSize, tLRPC$TL_messages_stickerSet, arrayList.get(0), tLRPC$TL_messages_stickerSet.set.thumb_version);
    }

    public void preloadStickerSetThumb(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$StickerSetCovered.set.thumbs, 90);
        if (closestPhotoSizeWithSize != null) {
            TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered.cover;
            if (tLRPC$Document == null) {
                if (tLRPC$StickerSetCovered.covers.isEmpty()) {
                    return;
                }
                tLRPC$Document = tLRPC$StickerSetCovered.covers.get(0);
            }
            loadStickerSetThumbInternal(closestPhotoSizeWithSize, tLRPC$StickerSetCovered, tLRPC$Document, tLRPC$StickerSetCovered.set.thumb_version);
        }
    }

    private void loadStickerSetThumbInternal(TLRPC$PhotoSize tLRPC$PhotoSize, Object obj, TLRPC$Document tLRPC$Document, int i) {
        ImageLocation forSticker = ImageLocation.getForSticker(tLRPC$PhotoSize, tLRPC$Document, i);
        if (forSticker != null) {
            getFileLoader().loadFile(forSticker, obj, forSticker.imageType == 1 ? "tgs" : "webp", 2, 1);
        }
    }

    public void toggleStickerSet(Context context, TLObject tLObject, int i, BaseFragment baseFragment, boolean z, boolean z2) {
        toggleStickerSet(context, tLObject, i, baseFragment, z, z2, null);
    }

    public void toggleStickerSet(final Context context, final TLObject tLObject, final int i, final BaseFragment baseFragment, final boolean z, boolean z2, final Runnable runnable) {
        TLRPC$StickerSet tLRPC$StickerSet;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        int i2;
        int i3;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2;
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet3 = (TLRPC$TL_messages_stickerSet) tLObject;
            tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet3;
            tLRPC$StickerSet = tLRPC$TL_messages_stickerSet3.set;
        } else if (tLObject instanceof TLRPC$StickerSetCovered) {
            TLRPC$StickerSet tLRPC$StickerSet2 = ((TLRPC$StickerSetCovered) tLObject).set;
            if (i != 2) {
                tLRPC$TL_messages_stickerSet2 = this.stickerSetsById.get(tLRPC$StickerSet2.id);
                if (tLRPC$TL_messages_stickerSet2 == null) {
                    return;
                }
            } else {
                tLRPC$TL_messages_stickerSet2 = null;
            }
            tLRPC$StickerSet = tLRPC$StickerSet2;
            tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet2;
        } else {
            throw new IllegalArgumentException("Invalid type of the given stickerSetObject: " + tLObject.getClass());
        }
        if (tLRPC$StickerSet.masks) {
            i2 = 1;
        } else {
            i2 = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        tLRPC$StickerSet.archived = i == 1;
        int i4 = 0;
        while (true) {
            if (i4 >= this.stickerSets[i2].size()) {
                i3 = 0;
                break;
            }
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet4 = this.stickerSets[i2].get(i4);
            if (tLRPC$TL_messages_stickerSet4.set.id == tLRPC$StickerSet.id) {
                this.stickerSets[i2].remove(i4);
                if (i == 2) {
                    this.stickerSets[i2].add(0, tLRPC$TL_messages_stickerSet4);
                } else {
                    this.stickerSetsById.remove(tLRPC$TL_messages_stickerSet4.set.id);
                    this.installedStickerSetsById.remove(tLRPC$TL_messages_stickerSet4.set.id);
                    this.stickerSetsByName.remove(tLRPC$TL_messages_stickerSet4.set.short_name);
                }
                i3 = i4;
            } else {
                i4++;
            }
        }
        this.loadHash[i2] = calcStickersHash(this.stickerSets[i2]);
        putStickersToCache(i2, this.stickerSets[i2], this.loadDate[i2], this.loadHash[i2]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i2));
        if (i == 2) {
            if (cancelRemovingStickerSet(tLRPC$StickerSet.id)) {
                return;
            }
            toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i2, z2);
        } else if (!z2 || baseFragment == null) {
            toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i2, false);
        } else {
            StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(context, tLObject, i);
            final TLRPC$StickerSet tLRPC$StickerSet3 = tLRPC$StickerSet;
            final int i5 = i2;
            final int i6 = i3;
            final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet5 = tLRPC$TL_messages_stickerSet;
            final TLRPC$StickerSet tLRPC$StickerSet4 = tLRPC$StickerSet;
            final int i7 = i2;
            final Bulletin.UndoButton delayedAction = new Bulletin.UndoButton(context, false).setUndoAction(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda92
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$toggleStickerSet$80(tLRPC$StickerSet3, i5, i6, tLRPC$TL_messages_stickerSet5, runnable);
                }
            }).setDelayedAction(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda49
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$toggleStickerSet$81(context, i, baseFragment, z, tLObject, tLRPC$StickerSet4, i7);
                }
            });
            stickerSetBulletinLayout.setButton(delayedAction);
            LongSparseArray<Runnable> longSparseArray = this.removingStickerSetsUndos;
            long j = tLRPC$StickerSet.id;
            delayedAction.getClass();
            longSparseArray.put(j, new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda127
                @Override // java.lang.Runnable
                public final void run() {
                    Bulletin.UndoButton.this.undo();
                }
            });
            Bulletin.make(baseFragment, stickerSetBulletinLayout, 2750).show();
        }
    }

    public /* synthetic */ void lambda$toggleStickerSet$80(TLRPC$StickerSet tLRPC$StickerSet, int i, int i2, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, Runnable runnable) {
        tLRPC$StickerSet.archived = false;
        this.stickerSets[i].add(i2, tLRPC$TL_messages_stickerSet);
        this.stickerSetsById.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
        this.installedStickerSetsById.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
        this.stickerSetsByName.put(tLRPC$StickerSet.short_name, tLRPC$TL_messages_stickerSet);
        this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        putStickersToCache(i, this.stickerSets[i], this.loadDate[i], this.loadHash[i]);
        if (runnable != null) {
            runnable.run();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
    }

    public /* synthetic */ void lambda$toggleStickerSet$81(Context context, int i, BaseFragment baseFragment, boolean z, TLObject tLObject, TLRPC$StickerSet tLRPC$StickerSet, int i2) {
        toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i2, false);
    }

    private void toggleStickerSetInternal(final Context context, int i, final BaseFragment baseFragment, final boolean z, final TLObject tLObject, final TLRPC$StickerSet tLRPC$StickerSet, final int i2, final boolean z2) {
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
        if (i != 0) {
            TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
            tLRPC$TL_messages_installStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
            boolean z3 = true;
            if (i != 1) {
                z3 = false;
            }
            tLRPC$TL_messages_installStickerSet.archived = z3;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_installStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda177
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                    MediaDataController.this.lambda$toggleStickerSetInternal$83(tLRPC$StickerSet, baseFragment, z, i2, z2, context, tLObject, tLObject2, tLRPC$TL_error);
                }
            });
            return;
        }
        TLRPC$TL_messages_uninstallStickerSet tLRPC$TL_messages_uninstallStickerSet = new TLRPC$TL_messages_uninstallStickerSet();
        tLRPC$TL_messages_uninstallStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_uninstallStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda176
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$toggleStickerSetInternal$85(tLRPC$StickerSet, i2, tLObject2, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$toggleStickerSetInternal$83(final TLRPC$StickerSet tLRPC$StickerSet, final BaseFragment baseFragment, final boolean z, final int i, final boolean z2, final Context context, final TLObject tLObject, final TLObject tLObject2, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda93
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$toggleStickerSetInternal$82(tLRPC$StickerSet, tLObject2, baseFragment, z, i, tLRPC$TL_error, z2, context, tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$toggleStickerSetInternal$82(TLRPC$StickerSet tLRPC$StickerSet, TLObject tLObject, BaseFragment baseFragment, boolean z, int i, TLRPC$TL_error tLRPC$TL_error, boolean z2, Context context, TLObject tLObject2) {
        this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
        if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
            processStickerSetInstallResultArchive(baseFragment, z, i, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
        }
        loadStickers(i, false, false, true);
        if (tLRPC$TL_error != null || !z2 || baseFragment == null) {
            return;
        }
        Bulletin.make(baseFragment, new StickerSetBulletinLayout(context, tLObject2, 2), 1500).show();
    }

    public /* synthetic */ void lambda$toggleStickerSetInternal$85(final TLRPC$StickerSet tLRPC$StickerSet, final int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda91
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$toggleStickerSetInternal$84(tLRPC$StickerSet, i);
            }
        });
    }

    public /* synthetic */ void lambda$toggleStickerSetInternal$84(TLRPC$StickerSet tLRPC$StickerSet, int i) {
        this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
        loadStickers(i, false, true);
    }

    public void toggleStickerSets(ArrayList<TLRPC$StickerSet> arrayList, final int i, final int i2, final BaseFragment baseFragment, final boolean z) {
        int size = arrayList.size();
        ArrayList<TLRPC$InputStickerSet> arrayList2 = new ArrayList<>(size);
        int i3 = 0;
        while (true) {
            boolean z2 = true;
            if (i3 >= size) {
                break;
            }
            TLRPC$StickerSet tLRPC$StickerSet = arrayList.get(i3);
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            arrayList2.add(tLRPC$TL_inputStickerSetID);
            if (i2 != 0) {
                if (i2 != 1) {
                    z2 = false;
                }
                tLRPC$StickerSet.archived = z2;
            }
            int size2 = this.stickerSets[i].size();
            int i4 = 0;
            while (true) {
                if (i4 < size2) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSets[i].get(i4);
                    if (tLRPC$TL_messages_stickerSet.set.id == tLRPC$TL_inputStickerSetID.id) {
                        this.stickerSets[i].remove(i4);
                        if (i2 == 2) {
                            this.stickerSets[i].add(0, tLRPC$TL_messages_stickerSet);
                        } else {
                            this.stickerSetsById.remove(tLRPC$TL_messages_stickerSet.set.id);
                            this.installedStickerSetsById.remove(tLRPC$TL_messages_stickerSet.set.id);
                            this.stickerSetsByName.remove(tLRPC$TL_messages_stickerSet.set.short_name);
                        }
                    } else {
                        i4++;
                    }
                }
            }
            i3++;
        }
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        putStickersToCache(i, this.stickerSets[i], this.loadDate[i], this.loadHash[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
        TLRPC$TL_messages_toggleStickerSets tLRPC$TL_messages_toggleStickerSets = new TLRPC$TL_messages_toggleStickerSets();
        tLRPC$TL_messages_toggleStickerSets.stickersets = arrayList2;
        if (i2 == 0) {
            tLRPC$TL_messages_toggleStickerSets.uninstall = true;
        } else if (i2 == 1) {
            tLRPC$TL_messages_toggleStickerSets.archive = true;
        } else if (i2 == 2) {
            tLRPC$TL_messages_toggleStickerSets.unarchive = true;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_toggleStickerSets, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda155
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$toggleStickerSets$87(i2, baseFragment, z, i, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$toggleStickerSets$87(final int i, final BaseFragment baseFragment, final boolean z, final int i2, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$toggleStickerSets$86(i, tLObject, baseFragment, z, i2);
            }
        });
    }

    public /* synthetic */ void lambda$toggleStickerSets$86(int i, TLObject tLObject, BaseFragment baseFragment, boolean z, int i2) {
        if (i != 0) {
            if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                processStickerSetInstallResultArchive(baseFragment, z, i2, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
            }
            loadStickers(i2, false, false, true);
            return;
        }
        loadStickers(i2, false, true);
    }

    public void processStickerSetInstallResultArchive(BaseFragment baseFragment, boolean z, int i, TLRPC$TL_messages_stickerSetInstallResultArchive tLRPC$TL_messages_stickerSetInstallResultArchive) {
        int size = tLRPC$TL_messages_stickerSetInstallResultArchive.sets.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.installedStickerSetsById.remove(tLRPC$TL_messages_stickerSetInstallResultArchive.sets.get(i2).set.id);
        }
        loadArchivedStickersCount(i, false);
        getNotificationCenter().postNotificationName(NotificationCenter.needAddArchivedStickers, tLRPC$TL_messages_stickerSetInstallResultArchive.sets);
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        baseFragment.showDialog(new StickersArchiveAlert(baseFragment.getParentActivity(), z ? baseFragment : null, tLRPC$TL_messages_stickerSetInstallResultArchive.sets).create());
    }

    private int getMask() {
        int i = 1;
        if (this.lastReturnedNum >= this.searchResultMessages.size() - 1) {
            boolean[] zArr = this.messagesSearchEndReached;
            if (zArr[0] && zArr[1]) {
                i = 0;
            }
        }
        return this.lastReturnedNum > 0 ? i | 2 : i;
    }

    public ArrayList<MessageObject> getFoundMessageObjects() {
        return this.searchResultMessages;
    }

    public void clearFoundMessageObjects() {
        this.searchResultMessages.clear();
    }

    public boolean isMessageFound(int i, boolean z) {
        return this.searchResultMessagesMap[z ? 1 : 0].indexOfKey(i) >= 0;
    }

    public void searchMessagesInChat(String str, long j, long j2, int i, int i2, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat) {
        searchMessagesInChat(str, j, j2, i, i2, i3, false, tLRPC$User, tLRPC$Chat, true);
    }

    public void jumpToSearchedMessage(int i, int i2) {
        if (i2 < 0 || i2 >= this.searchResultMessages.size()) {
            return;
        }
        this.lastReturnedNum = i2;
        MessageObject messageObject = this.searchResultMessages.get(i2);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i3 = NotificationCenter.chatSearchResultsAvailable;
        int[] iArr = this.messagesSearchCount;
        notificationCenter.postNotificationName(i3, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), Boolean.TRUE);
    }

    public void loadMoreSearchMessages() {
        if (!this.loadingMoreSearchMessages) {
            boolean[] zArr = this.messagesSearchEndReached;
            if (zArr[0] && this.lastMergeDialogId == 0 && zArr[1]) {
                return;
            }
            int size = this.searchResultMessages.size();
            this.lastReturnedNum = this.searchResultMessages.size();
            searchMessagesInChat(null, this.lastDialogId, this.lastMergeDialogId, this.lastGuid, 1, this.lastReplyMessageId, false, this.lastSearchUser, this.lastSearchChat, false);
            this.lastReturnedNum = size;
            this.loadingMoreSearchMessages = true;
        }
    }

    private void searchMessagesInChat(String str, final long j, final long j2, final int i, final int i2, final int i3, boolean z, final TLRPC$User tLRPC$User, final TLRPC$Chat tLRPC$Chat, final boolean z2) {
        long j3;
        int i4;
        String str2;
        long j4;
        String str3;
        long j5;
        int i5;
        boolean z3 = !z;
        if (this.reqId != 0) {
            getConnectionsManager().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            getConnectionsManager().cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        if (str != null) {
            if (z3) {
                boolean[] zArr = this.messagesSearchEndReached;
                zArr[1] = false;
                zArr[0] = false;
                int[] iArr = this.messagesSearchCount;
                iArr[1] = 0;
                iArr[0] = 0;
                this.searchResultMessages.clear();
                this.searchResultMessagesMap[0].clear();
                this.searchResultMessagesMap[1].clear();
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i));
            }
            str2 = str;
            j3 = j;
            i4 = 0;
        } else if (this.searchResultMessages.isEmpty()) {
            return;
        } else {
            if (i2 != 1) {
                if (i2 != 2) {
                    return;
                }
                int i6 = this.lastReturnedNum - 1;
                this.lastReturnedNum = i6;
                if (i6 < 0) {
                    this.lastReturnedNum = 0;
                    return;
                }
                if (i6 >= this.searchResultMessages.size()) {
                    this.lastReturnedNum = this.searchResultMessages.size() - 1;
                }
                MessageObject messageObject = this.searchResultMessages.get(this.lastReturnedNum);
                NotificationCenter notificationCenter = getNotificationCenter();
                int i7 = NotificationCenter.chatSearchResultsAvailable;
                int[] iArr2 = this.messagesSearchCount;
                notificationCenter.postNotificationName(i7, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr2[0] + iArr2[1]), Boolean.valueOf(z2));
                return;
            }
            int i8 = this.lastReturnedNum + 1;
            this.lastReturnedNum = i8;
            if (i8 < this.searchResultMessages.size()) {
                MessageObject messageObject2 = this.searchResultMessages.get(this.lastReturnedNum);
                NotificationCenter notificationCenter2 = getNotificationCenter();
                int i9 = NotificationCenter.chatSearchResultsAvailable;
                int[] iArr3 = this.messagesSearchCount;
                notificationCenter2.postNotificationName(i9, Integer.valueOf(i), Integer.valueOf(messageObject2.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject2.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr3[0] + iArr3[1]), Boolean.valueOf(z2));
                return;
            }
            boolean[] zArr2 = this.messagesSearchEndReached;
            if (zArr2[0] && j2 == 0 && zArr2[1]) {
                this.lastReturnedNum--;
                return;
            }
            String str4 = this.lastSearchQuery;
            ArrayList<MessageObject> arrayList = this.searchResultMessages;
            MessageObject messageObject3 = arrayList.get(arrayList.size() - 1);
            if (messageObject3.getDialogId() == j && !this.messagesSearchEndReached[0]) {
                i5 = messageObject3.getId();
                j5 = j;
            } else {
                i5 = messageObject3.getDialogId() == j2 ? messageObject3.getId() : 0;
                this.messagesSearchEndReached[1] = false;
                j5 = j2;
            }
            j3 = j5;
            i4 = i5;
            str2 = str4;
            z3 = false;
        }
        boolean[] zArr3 = this.messagesSearchEndReached;
        if (!zArr3[0] || zArr3[1]) {
            j4 = 0;
        } else {
            j4 = 0;
            if (j2 != 0) {
                j3 = j2;
            }
        }
        if (j3 != j || !z3) {
            str3 = str2;
        } else if (j2 != j4) {
            TLRPC$InputPeer inputPeer = getMessagesController().getInputPeer(j2);
            if (inputPeer == null) {
                return;
            }
            final TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
            tLRPC$TL_messages_search.peer = inputPeer;
            this.lastMergeDialogId = j2;
            tLRPC$TL_messages_search.limit = 1;
            tLRPC$TL_messages_search.q = str2;
            if (tLRPC$User != null) {
                tLRPC$TL_messages_search.from_id = MessagesController.getInputPeer(tLRPC$User);
                tLRPC$TL_messages_search.flags = 1 | tLRPC$TL_messages_search.flags;
            } else if (tLRPC$Chat != null) {
                tLRPC$TL_messages_search.from_id = MessagesController.getInputPeer(tLRPC$Chat);
                tLRPC$TL_messages_search.flags = 1 | tLRPC$TL_messages_search.flags;
            }
            tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterEmpty();
            this.mergeReqId = getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda165
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    MediaDataController.this.lambda$searchMessagesInChat$89(j2, tLRPC$TL_messages_search, j, i, i2, i3, tLRPC$User, tLRPC$Chat, z2, tLObject, tLRPC$TL_error);
                }
            }, 2);
            return;
        } else {
            str3 = str2;
            this.lastMergeDialogId = 0L;
            zArr3[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        final TLRPC$TL_messages_search tLRPC$TL_messages_search2 = new TLRPC$TL_messages_search();
        TLRPC$InputPeer inputPeer2 = getMessagesController().getInputPeer(j3);
        tLRPC$TL_messages_search2.peer = inputPeer2;
        if (inputPeer2 == null) {
            return;
        }
        this.lastGuid = i;
        this.lastDialogId = j;
        this.lastSearchUser = tLRPC$User;
        this.lastSearchChat = tLRPC$Chat;
        this.lastReplyMessageId = i3;
        tLRPC$TL_messages_search2.limit = 21;
        tLRPC$TL_messages_search2.q = str3 != null ? str3 : "";
        tLRPC$TL_messages_search2.offset_id = i4;
        if (tLRPC$User != null) {
            tLRPC$TL_messages_search2.from_id = MessagesController.getInputPeer(tLRPC$User);
            tLRPC$TL_messages_search2.flags |= 1;
        } else if (tLRPC$Chat != null) {
            tLRPC$TL_messages_search2.from_id = MessagesController.getInputPeer(tLRPC$Chat);
            tLRPC$TL_messages_search2.flags |= 1;
        }
        int i10 = this.lastReplyMessageId;
        if (i10 != 0) {
            tLRPC$TL_messages_search2.top_msg_id = i10;
            tLRPC$TL_messages_search2.flags |= 2;
        }
        tLRPC$TL_messages_search2.filter = new TLRPC$TL_inputMessagesFilterEmpty();
        final int i11 = this.lastReqId + 1;
        this.lastReqId = i11;
        this.lastSearchQuery = str3;
        final String str5 = str3;
        final long j6 = j3;
        this.reqId = getConnectionsManager().sendRequest(tLRPC$TL_messages_search2, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda170
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$searchMessagesInChat$91(str5, i11, z2, tLRPC$TL_messages_search2, j6, j, i, j2, i3, tLRPC$User, tLRPC$Chat, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$searchMessagesInChat$89(final long j, final TLRPC$TL_messages_search tLRPC$TL_messages_search, final long j2, final int i, final int i2, final int i3, final TLRPC$User tLRPC$User, final TLRPC$Chat tLRPC$Chat, final boolean z, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$searchMessagesInChat$88(j, tLObject, tLRPC$TL_messages_search, j2, i, i2, i3, tLRPC$User, tLRPC$Chat, z);
            }
        });
    }

    public /* synthetic */ void lambda$searchMessagesInChat$88(long j, TLObject tLObject, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j2, int i, int i2, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z) {
        if (this.lastMergeDialogId == j) {
            this.mergeReqId = 0;
            if (tLObject != null) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                this.messagesSearchEndReached[1] = tLRPC$messages_Messages.messages.isEmpty();
                this.messagesSearchCount[1] = tLRPC$messages_Messages instanceof TLRPC$TL_messages_messagesSlice ? tLRPC$messages_Messages.count : tLRPC$messages_Messages.messages.size();
                searchMessagesInChat(tLRPC$TL_messages_search.q, j2, j, i, i2, i3, true, tLRPC$User, tLRPC$Chat, z);
                return;
            }
            this.messagesSearchEndReached[1] = true;
            this.messagesSearchCount[1] = 0;
            searchMessagesInChat(tLRPC$TL_messages_search.q, j2, j, i, i2, i3, true, tLRPC$User, tLRPC$Chat, z);
        }
    }

    public /* synthetic */ void lambda$searchMessagesInChat$91(String str, final int i, final boolean z, final TLRPC$TL_messages_search tLRPC$TL_messages_search, final long j, final long j2, final int i2, final long j3, final int i3, final TLRPC$User tLRPC$User, final TLRPC$Chat tLRPC$Chat, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        final ArrayList arrayList = new ArrayList();
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            int min = Math.min(tLRPC$messages_Messages.messages.size(), 20);
            for (int i4 = 0; i4 < min; i4++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages.messages.get(i4), false, false);
                messageObject.setQuery(str);
                arrayList.add(messageObject);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$searchMessagesInChat$90(i, z, tLObject, tLRPC$TL_messages_search, j, j2, i2, arrayList, j3, i3, tLRPC$User, tLRPC$Chat);
            }
        });
    }

    public /* synthetic */ void lambda$searchMessagesInChat$90(int i, boolean z, TLObject tLObject, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j, long j2, int i2, ArrayList arrayList, long j3, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat) {
        if (i == this.lastReqId) {
            this.reqId = 0;
            if (!z) {
                this.loadingMoreSearchMessages = false;
            }
            if (tLObject == null) {
                return;
            }
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            int i4 = 0;
            while (i4 < tLRPC$messages_Messages.messages.size()) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i4);
                if ((tLRPC$Message instanceof TLRPC$TL_messageEmpty) || (tLRPC$Message.action instanceof TLRPC$TL_messageActionHistoryClear)) {
                    tLRPC$messages_Messages.messages.remove(i4);
                    i4--;
                }
                i4++;
            }
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            getMessagesController().putUsers(tLRPC$messages_Messages.users, false);
            getMessagesController().putChats(tLRPC$messages_Messages.chats, false);
            if (tLRPC$TL_messages_search.offset_id == 0 && j == j2) {
                this.lastReturnedNum = 0;
                this.searchResultMessages.clear();
                this.searchResultMessagesMap[0].clear();
                this.searchResultMessagesMap[1].clear();
                this.messagesSearchCount[0] = 0;
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i2));
            }
            int min = Math.min(tLRPC$messages_Messages.messages.size(), 20);
            int i5 = 0;
            boolean z2 = false;
            while (i5 < min) {
                tLRPC$messages_Messages.messages.get(i5);
                MessageObject messageObject = (MessageObject) arrayList.get(i5);
                this.searchResultMessages.add(messageObject);
                this.searchResultMessagesMap[j == j2 ? (char) 0 : (char) 1].put(messageObject.getId(), messageObject);
                i5++;
                z2 = true;
            }
            this.messagesSearchEndReached[j == j2 ? (char) 0 : (char) 1] = tLRPC$messages_Messages.messages.size() < 21;
            this.messagesSearchCount[j == j2 ? (char) 0 : (char) 1] = ((tLRPC$messages_Messages instanceof TLRPC$TL_messages_messagesSlice) || (tLRPC$messages_Messages instanceof TLRPC$TL_messages_channelMessages)) ? tLRPC$messages_Messages.count : tLRPC$messages_Messages.messages.size();
            if (this.searchResultMessages.isEmpty()) {
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i2), 0, Integer.valueOf(getMask()), 0L, 0, 0, Boolean.valueOf(z));
            } else if (z2) {
                if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                    this.lastReturnedNum = this.searchResultMessages.size() - 1;
                }
                MessageObject messageObject2 = this.searchResultMessages.get(this.lastReturnedNum);
                NotificationCenter notificationCenter = getNotificationCenter();
                int i6 = NotificationCenter.chatSearchResultsAvailable;
                int[] iArr = this.messagesSearchCount;
                notificationCenter.postNotificationName(i6, Integer.valueOf(i2), Integer.valueOf(messageObject2.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject2.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), Boolean.valueOf(z));
            }
            if (j != j2) {
                return;
            }
            boolean[] zArr = this.messagesSearchEndReached;
            if (!zArr[0] || j3 == 0 || zArr[1]) {
                return;
            }
            searchMessagesInChat(this.lastSearchQuery, j2, j3, i2, 0, i3, true, tLRPC$User, tLRPC$Chat, z);
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x002a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadMedia(final long r17, final int r19, final int r20, final int r21, final int r22, int r23, final int r24, final int r25) {
        /*
            Method dump skipped, instructions count: 293
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadMedia(long, int, int, int, int, int, int, int):void");
    }

    public /* synthetic */ void lambda$loadMedia$92(long j, int i, int i2, int i3, int i4, int i5, boolean z, int i6, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            getMessagesController().removeDeletedMessagesFromArray(j, tLRPC$messages_Messages.messages);
            boolean z2 = false;
            if (i == 0 ? tLRPC$messages_Messages.messages.size() == 0 : tLRPC$messages_Messages.messages.size() <= 1) {
                z2 = true;
            }
            processLoadedMedia(tLRPC$messages_Messages, j, i2, i3, i, i4, 0, i5, z, z2, i6);
        }
    }

    public void getMediaCounts(final long j, final int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$getMediaCounts$97(j, i);
            }
        });
    }

    public /* synthetic */ void lambda$getMediaCounts$97(final long j, int i) {
        try {
            final int[] iArr = new int[8];
            iArr[0] = -1;
            iArr[1] = -1;
            iArr[2] = -1;
            iArr[3] = -1;
            iArr[4] = -1;
            iArr[5] = -1;
            iArr[6] = -1;
            iArr[7] = -1;
            final int[] iArr2 = new int[8];
            iArr2[0] = -1;
            iArr2[1] = -1;
            iArr2[2] = -1;
            iArr2[3] = -1;
            iArr2[4] = -1;
            iArr2[5] = -1;
            iArr2[6] = -1;
            iArr2[7] = -1;
            int[] iArr3 = new int[8];
            iArr3[0] = 0;
            iArr3[1] = 0;
            iArr3[2] = 0;
            iArr3[3] = 0;
            iArr3[4] = 0;
            iArr3[5] = 0;
            iArr3[6] = 0;
            iArr3[7] = 0;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d", Long.valueOf(j)), new Object[0]);
            while (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                if (intValue >= 0 && intValue < 8) {
                    int intValue2 = queryFinalized.intValue(1);
                    iArr[intValue] = intValue2;
                    iArr2[intValue] = intValue2;
                    iArr3[intValue] = queryFinalized.intValue(2);
                }
            }
            queryFinalized.dispose();
            if (DialogObject.isEncryptedDialog(j)) {
                for (int i2 = 0; i2 < 8; i2++) {
                    if (iArr[i2] == -1) {
                        SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v4 WHERE uid = %d AND type = %d LIMIT 1", Long.valueOf(j), Integer.valueOf(i2)), new Object[0]);
                        if (queryFinalized2.next()) {
                            iArr[i2] = queryFinalized2.intValue(0);
                        } else {
                            iArr[i2] = 0;
                        }
                        queryFinalized2.dispose();
                        putMediaCountDatabase(j, i2, iArr[i2]);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda47
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.lambda$getMediaCounts$93(j, iArr);
                    }
                });
                return;
            }
            TLRPC$TL_messages_getSearchCounters tLRPC$TL_messages_getSearchCounters = new TLRPC$TL_messages_getSearchCounters();
            tLRPC$TL_messages_getSearchCounters.peer = getMessagesController().getInputPeer(j);
            int i3 = 0;
            boolean z = false;
            for (int i4 = 8; i3 < i4; i4 = 8) {
                if (tLRPC$TL_messages_getSearchCounters.peer == null) {
                    iArr[i3] = 0;
                } else if (iArr[i3] == -1 || iArr3[i3] == 1) {
                    if (i3 == 0) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterPhotoVideo());
                    } else if (i3 == 1) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterDocument());
                    } else if (i3 == 2) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterRoundVoice());
                    } else if (i3 == 3) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterUrl());
                    } else if (i3 == 4) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterMusic());
                    } else if (i3 == 6) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterPhotos());
                    } else if (i3 == 7) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterVideo());
                    } else {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterGif());
                    }
                    if (iArr[i3] == -1) {
                        z = true;
                    } else if (iArr3[i3] == 1) {
                        iArr[i3] = -1;
                    }
                }
                i3++;
            }
            if (!tLRPC$TL_messages_getSearchCounters.filters.isEmpty()) {
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchCounters, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda180
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MediaDataController.this.lambda$getMediaCounts$95(iArr, j, tLObject, tLRPC$TL_error);
                    }
                }), i);
            }
            if (z) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda48
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$getMediaCounts$96(j, iArr2);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$getMediaCounts$93(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public /* synthetic */ void lambda$getMediaCounts$95(final int[] iArr, final long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int i;
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (iArr[i2] < 0) {
                iArr[i2] = 0;
            }
        }
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            int size = tLRPC$Vector.objects.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$TL_messages_searchCounter tLRPC$TL_messages_searchCounter = (TLRPC$TL_messages_searchCounter) tLRPC$Vector.objects.get(i3);
                TLRPC$MessagesFilter tLRPC$MessagesFilter = tLRPC$TL_messages_searchCounter.filter;
                if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterPhotoVideo) {
                    i = 0;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterDocument) {
                    i = 1;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterRoundVoice) {
                    i = 2;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterUrl) {
                    i = 3;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterMusic) {
                    i = 4;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterGif) {
                    i = 5;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterPhotos) {
                    i = 6;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterVideo) {
                    i = 7;
                }
                iArr[i] = tLRPC$TL_messages_searchCounter.count;
                putMediaCountDatabase(j, i, iArr[i]);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$getMediaCounts$94(j, iArr);
            }
        });
    }

    public /* synthetic */ void lambda$getMediaCounts$94(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public /* synthetic */ void lambda$getMediaCounts$96(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public void getMediaCount(final long j, final int i, final int i2, boolean z) {
        if (z || DialogObject.isEncryptedDialog(j)) {
            getMediaCountDatabase(j, i, i2);
            return;
        }
        TLRPC$TL_messages_getSearchCounters tLRPC$TL_messages_getSearchCounters = new TLRPC$TL_messages_getSearchCounters();
        if (i == 0) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterPhotoVideo());
        } else if (i == 1) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterDocument());
        } else if (i == 2) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterRoundVoice());
        } else if (i == 3) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterUrl());
        } else if (i == 4) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterMusic());
        } else if (i == 5) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterGif());
        }
        TLRPC$InputPeer inputPeer = getMessagesController().getInputPeer(j);
        tLRPC$TL_messages_getSearchCounters.peer = inputPeer;
        if (inputPeer == null) {
            return;
        }
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchCounters, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda158
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$getMediaCount$98(j, i, i2, tLObject, tLRPC$TL_error);
            }
        }), i2);
    }

    public /* synthetic */ void lambda$getMediaCount$98(long j, int i, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            if (tLRPC$Vector.objects.isEmpty()) {
                return;
            }
            processLoadedMediaCount(((TLRPC$TL_messages_searchCounter) tLRPC$Vector.objects.get(0)).count, j, i, i2, false, 0);
        }
    }

    public static int getMediaType(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message == null) {
            return -1;
        }
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            return 0;
        }
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            TLRPC$Document tLRPC$Document = tLRPC$MessageMedia.document;
            if (tLRPC$Document == null) {
                return -1;
            }
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = false;
            boolean z5 = false;
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                    z = tLRPC$DocumentAttribute.round_message;
                    z2 = !z;
                } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAnimated) {
                    z3 = true;
                } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    z = tLRPC$DocumentAttribute.voice;
                    z5 = !z;
                } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                    z4 = true;
                }
            }
            if (z) {
                return 2;
            }
            if (z2 && !z3 && !z4) {
                return 0;
            }
            if (z4) {
                return -1;
            }
            if (z3) {
                return 5;
            }
            return z5 ? 4 : 1;
        }
        if (!tLRPC$Message.entities.isEmpty()) {
            for (int i2 = 0; i2 < tLRPC$Message.entities.size(); i2++) {
                TLRPC$MessageEntity tLRPC$MessageEntity = tLRPC$Message.entities.get(i2);
                if ((tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityUrl) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityTextUrl) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityEmail)) {
                    return 3;
                }
            }
        }
        return -1;
    }

    public static boolean canAddMessageToMedia(TLRPC$Message tLRPC$Message) {
        int i;
        boolean z = tLRPC$Message instanceof TLRPC$TL_message_secret;
        if (!z || ((!(tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) && !MessageObject.isVideoMessage(tLRPC$Message) && !MessageObject.isGifMessage(tLRPC$Message)) || (i = tLRPC$Message.media.ttl_seconds) == 0 || i > 60)) {
            if (!z && (tLRPC$Message instanceof TLRPC$TL_message)) {
                TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$MessageMedia.ttl_seconds != 0) {
                    return false;
                }
            }
            return getMediaType(tLRPC$Message) != -1;
        }
        return false;
    }

    public void processLoadedMedia(final TLRPC$messages_Messages tLRPC$messages_Messages, final long j, int i, int i2, final int i3, final int i4, final int i5, final int i6, boolean z, final boolean z2, final int i7) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("process load media did " + j + " count = " + i + " max_id=" + i2 + " min_id=" + i3 + " type = " + i4 + " cache = " + i5 + " classGuid = " + i6);
        }
        if (i5 != 0 && (((tLRPC$messages_Messages.messages.isEmpty() && i3 == 0) || (tLRPC$messages_Messages.messages.size() <= 1 && i3 != 0)) && !DialogObject.isEncryptedDialog(j))) {
            if (i5 == 2) {
                return;
            }
            loadMedia(j, i, i2, i3, i4, 0, i6, i7);
            return;
        }
        if (i5 == 0) {
            ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages.messages);
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            putMediaDatabase(j, i4, tLRPC$messages_Messages.messages, i2, i3, z2);
        }
        Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda108
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedMedia$100(tLRPC$messages_Messages, i5, j, i6, i4, z2, i3, i7);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedMedia$100(final TLRPC$messages_Messages tLRPC$messages_Messages, final int i, final long j, final int i2, final int i3, final boolean z, final int i4, final int i5) {
        LongSparseArray longSparseArray = new LongSparseArray();
        for (int i6 = 0; i6 < tLRPC$messages_Messages.users.size(); i6++) {
            TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i6);
            longSparseArray.put(tLRPC$User.id, tLRPC$User);
        }
        final ArrayList<MessageObject> arrayList = new ArrayList<>();
        for (int i7 = 0; i7 < tLRPC$messages_Messages.messages.size(); i7++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages.messages.get(i7), (LongSparseArray<TLRPC$User>) longSparseArray, true, false);
            messageObject.createStrippedThumb();
            arrayList.add(messageObject);
        }
        getFileLoader().checkMediaExistance(arrayList);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda109
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedMedia$99(tLRPC$messages_Messages, i, j, arrayList, i2, i3, z, i4, i5);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedMedia$99(TLRPC$messages_Messages tLRPC$messages_Messages, int i, long j, ArrayList arrayList, int i2, int i3, boolean z, int i4, int i5) {
        int i6 = tLRPC$messages_Messages.count;
        boolean z2 = true;
        getMessagesController().putUsers(tLRPC$messages_Messages.users, i != 0);
        getMessagesController().putChats(tLRPC$messages_Messages.chats, i != 0);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i7 = NotificationCenter.mediaDidLoad;
        Object[] objArr = new Object[8];
        objArr[0] = Long.valueOf(j);
        objArr[1] = Integer.valueOf(i6);
        objArr[2] = arrayList;
        objArr[3] = Integer.valueOf(i2);
        objArr[4] = Integer.valueOf(i3);
        objArr[5] = Boolean.valueOf(z);
        if (i4 == 0) {
            z2 = false;
        }
        objArr[6] = Boolean.valueOf(z2);
        objArr[7] = Integer.valueOf(i5);
        notificationCenter.postNotificationName(i7, objArr);
    }

    private void processLoadedMediaCount(final int i, final long j, final int i2, final int i3, final boolean z, final int i4) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda45
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$processLoadedMediaCount$101(j, z, i, i2, i4, i3);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedMediaCount$101(long j, boolean z, int i, int i2, int i3, int i4) {
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(j);
        int i5 = 0;
        boolean z2 = z && (i == -1 || (i == 0 && i2 == 2)) && !isEncryptedDialog;
        if (z2 || (i3 == 1 && !isEncryptedDialog)) {
            getMediaCount(j, i2, i4, false);
        }
        if (!z2) {
            if (!z) {
                putMediaCountDatabase(j, i2, i);
            }
            NotificationCenter notificationCenter = getNotificationCenter();
            int i6 = NotificationCenter.mediaCountDidLoad;
            Object[] objArr = new Object[4];
            objArr[0] = Long.valueOf(j);
            if (!z || i != -1) {
                i5 = i;
            }
            objArr[1] = Integer.valueOf(i5);
            objArr[2] = Boolean.valueOf(z);
            objArr[3] = Integer.valueOf(i2);
            notificationCenter.postNotificationName(i6, objArr);
        }
    }

    private void putMediaCountDatabase(final long j, final int i, final int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putMediaCountDatabase$102(j, i, i2);
            }
        });
    }

    public /* synthetic */ void lambda$putMediaCountDatabase$102(long j, int i, int i2) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, i);
            executeFast.bindInteger(3, i2);
            executeFast.bindInteger(4, 0);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void getMediaCountDatabase(final long j, final int i, final int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$getMediaCountDatabase$103(j, i, i2);
            }
        });
    }

    public /* synthetic */ void lambda$getMediaCountDatabase$103(long j, int i, int i2) {
        Exception e;
        int i3;
        int i4;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            Locale locale = Locale.US;
            SQLiteCursor queryFinalized = database.queryFinalized(String.format(locale, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", Long.valueOf(j), Integer.valueOf(i)), new Object[0]);
            if (queryFinalized.next()) {
                i4 = queryFinalized.intValue(0);
                i3 = queryFinalized.intValue(1);
            } else {
                i4 = -1;
                i3 = 0;
            }
            queryFinalized.dispose();
            if (i4 == -1 && DialogObject.isEncryptedDialog(j)) {
                SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized(String.format(locale, "SELECT COUNT(mid) FROM media_v4 WHERE uid = %d AND type = %d LIMIT 1", Long.valueOf(j), Integer.valueOf(i)), new Object[0]);
                if (queryFinalized2.next()) {
                    i4 = queryFinalized2.intValue(0);
                }
                queryFinalized2.dispose();
                if (i4 != -1) {
                    try {
                        putMediaCountDatabase(j, i, i4);
                        processLoadedMediaCount(i4, j, i, i2, true, i3);
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        return;
                    }
                }
            }
            processLoadedMediaCount(i4, j, i, i2, true, i3);
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* renamed from: org.telegram.messenger.MediaDataController$1 */
    /* loaded from: classes.dex */
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
            MediaDataController.this = r1;
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

        /* JADX WARN: Removed duplicated region for block: B:62:0x034d A[Catch: all -> 0x040c, Exception -> 0x040f, TryCatch #0 {Exception -> 0x040f, blocks: (B:3:0x0007, B:5:0x0029, B:7:0x002d, B:9:0x0053, B:12:0x005b, B:14:0x0082, B:16:0x0088, B:18:0x00aa, B:20:0x00af, B:22:0x00b3, B:24:0x00e3, B:26:0x00ec, B:28:0x00f1, B:29:0x012a, B:30:0x015b, B:32:0x015f, B:34:0x0191, B:36:0x019b, B:38:0x01a1, B:39:0x01db, B:41:0x0211, B:43:0x023b, B:45:0x0241, B:47:0x0247, B:48:0x0278, B:51:0x02a4, B:53:0x02a9, B:56:0x02e1, B:58:0x02e5, B:59:0x031a, B:60:0x0347, B:62:0x034d, B:64:0x0353, B:66:0x037a, B:69:0x0385, B:70:0x038c, B:71:0x0392, B:73:0x039c, B:76:0x03a8, B:77:0x03b7, B:79:0x03bd, B:80:0x03cc, B:82:0x03d6, B:84:0x03da, B:85:0x03e6), top: B:100:0x0007, outer: #1 }] */
        /* JADX WARN: Removed duplicated region for block: B:73:0x039c A[Catch: all -> 0x040c, Exception -> 0x040f, TRY_LEAVE, TryCatch #0 {Exception -> 0x040f, blocks: (B:3:0x0007, B:5:0x0029, B:7:0x002d, B:9:0x0053, B:12:0x005b, B:14:0x0082, B:16:0x0088, B:18:0x00aa, B:20:0x00af, B:22:0x00b3, B:24:0x00e3, B:26:0x00ec, B:28:0x00f1, B:29:0x012a, B:30:0x015b, B:32:0x015f, B:34:0x0191, B:36:0x019b, B:38:0x01a1, B:39:0x01db, B:41:0x0211, B:43:0x023b, B:45:0x0241, B:47:0x0247, B:48:0x0278, B:51:0x02a4, B:53:0x02a9, B:56:0x02e1, B:58:0x02e5, B:59:0x031a, B:60:0x0347, B:62:0x034d, B:64:0x0353, B:66:0x037a, B:69:0x0385, B:70:0x038c, B:71:0x0392, B:73:0x039c, B:76:0x03a8, B:77:0x03b7, B:79:0x03bd, B:80:0x03cc, B:82:0x03d6, B:84:0x03da, B:85:0x03e6), top: B:100:0x0007, outer: #1 }] */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:71:0x0392 -> B:55:0x02df). Please submit an issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:72:0x0398 -> B:55:0x02df). Please submit an issue!!! */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 1131
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.AnonymousClass1.run():void");
        }

        public /* synthetic */ void lambda$run$0(Runnable runnable, int i) {
            MediaDataController.this.getMessagesStorage().completeTaskForGuid(runnable, i);
        }
    }

    private void loadMediaDatabase(long j, int i, int i2, int i3, int i4, int i5, boolean z, int i6, int i7) {
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(i, j, i3, i4, i2, i5, i6, z, i7);
        MessagesStorage messagesStorage = getMessagesStorage();
        messagesStorage.getStorageQueue().postRunnable(anonymousClass1);
        messagesStorage.bindTaskToGuid(anonymousClass1, i5);
    }

    private void putMediaDatabase(final long j, final int i, final ArrayList<TLRPC$Message> arrayList, final int i2, final int i3, final boolean z) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putMediaDatabase$104(i3, arrayList, z, j, i2, i);
            }
        });
    }

    public /* synthetic */ void lambda$putMediaDatabase$104(int i, ArrayList arrayList, boolean z, long j, int i2, int i3) {
        if (i == 0) {
            try {
                if (arrayList.isEmpty() || z) {
                    getMessagesStorage().doneHolesInMedia(j, i2, i3);
                    if (arrayList.isEmpty()) {
                        return;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        getMessagesStorage().getDatabase().beginTransaction();
        SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_v4 VALUES(?, ?, ?, ?, ?)");
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            TLRPC$Message tLRPC$Message = (TLRPC$Message) it.next();
            if (canAddMessageToMedia(tLRPC$Message)) {
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                tLRPC$Message.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, tLRPC$Message.id);
                executeFast.bindLong(2, j);
                executeFast.bindInteger(3, tLRPC$Message.date);
                executeFast.bindInteger(4, i3);
                executeFast.bindByteBuffer(5, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
        }
        executeFast.dispose();
        if (!z || i2 != 0 || i != 0) {
            int i4 = (!z || i != 0) ? ((TLRPC$Message) arrayList.get(arrayList.size() - 1)).id : 1;
            if (i != 0) {
                getMessagesStorage().closeHolesInMedia(j, i4, ((TLRPC$Message) arrayList.get(0)).id, i3);
            } else if (i2 != 0) {
                getMessagesStorage().closeHolesInMedia(j, i4, i2, i3);
            } else {
                getMessagesStorage().closeHolesInMedia(j, i4, Integer.MAX_VALUE, i3);
            }
        }
        getMessagesStorage().getDatabase().commitTransaction();
    }

    public void loadMusic(final long j, final long j2, final long j3) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadMusic$106(j, j2, j3);
            }
        });
    }

    public /* synthetic */ void lambda$loadMusic$106(final long j, long j2, long j3) {
        SQLiteCursor sQLiteCursor;
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        int i = 0;
        while (i < 2) {
            ArrayList arrayList3 = i == 0 ? arrayList : arrayList2;
            if (i == 0) {
                try {
                    if (!DialogObject.isEncryptedDialog(j)) {
                        sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", Long.valueOf(j), Long.valueOf(j2), 4), new Object[0]);
                    } else {
                        sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", Long.valueOf(j), Long.valueOf(j2), 4), new Object[0]);
                    }
                } catch (Exception e) {
                    e = e;
                    FileLog.e(e);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda42
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaDataController.this.lambda$loadMusic$105(j, arrayList, arrayList2);
                        }
                    });
                }
            } else if (!DialogObject.isEncryptedDialog(j)) {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", Long.valueOf(j), Long.valueOf(j3), 4), new Object[0]);
            } else {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", Long.valueOf(j), Long.valueOf(j3), 4), new Object[0]);
            }
            while (sQLiteCursor.next()) {
                NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    if (MessageObject.isMusicMessage(TLdeserialize)) {
                        TLdeserialize.id = sQLiteCursor.intValue(1);
                        try {
                            TLdeserialize.dialog_id = j;
                        } catch (Exception e2) {
                            e = e2;
                        }
                        try {
                            arrayList3.add(0, new MessageObject(this.currentAccount, TLdeserialize, false, true));
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.e(e);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda42
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MediaDataController.this.lambda$loadMusic$105(j, arrayList, arrayList2);
                                }
                            });
                        }
                    }
                }
            }
            sQLiteCursor.dispose();
            i++;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadMusic$105(j, arrayList, arrayList2);
            }
        });
    }

    public /* synthetic */ void lambda$loadMusic$105(long j, ArrayList arrayList, ArrayList arrayList2) {
        getNotificationCenter().postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(j), arrayList, arrayList2);
    }

    public void buildShortcuts() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        int maxShortcutCountPerActivity = ShortcutManagerCompat.getMaxShortcutCountPerActivity(ApplicationLoader.applicationContext) - 2;
        if (maxShortcutCountPerActivity <= 0) {
            maxShortcutCountPerActivity = 5;
        }
        final ArrayList arrayList = new ArrayList();
        if (SharedConfig.passcodeHash.length() <= 0) {
            for (int i = 0; i < this.hints.size(); i++) {
                arrayList.add(this.hints.get(i));
                if (arrayList.size() == maxShortcutCountPerActivity - 2) {
                    break;
                }
            }
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$buildShortcuts$107(arrayList);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:73:0x0262  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0279  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0292 A[Catch: all -> 0x02cd, TryCatch #3 {all -> 0x02cd, blocks: (B:3:0x0004, B:5:0x0009, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0077, B:18:0x007d, B:20:0x008d, B:21:0x0090, B:22:0x0096, B:24:0x009c, B:27:0x00a3, B:29:0x00ee, B:30:0x00f4, B:31:0x00f9, B:33:0x0102, B:34:0x0107, B:35:0x0112, B:37:0x0118, B:39:0x0133, B:40:0x0146, B:42:0x015c, B:47:0x0168, B:49:0x0174, B:50:0x0177, B:52:0x017d, B:55:0x0187, B:72:0x025e, B:74:0x0264, B:77:0x027b, B:79:0x0292, B:81:0x0297, B:82:0x029f, B:83:0x02ab, B:85:0x02b8, B:86:0x02be, B:87:0x02c3), top: B:98:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:81:0x0297 A[Catch: all -> 0x02cd, TryCatch #3 {all -> 0x02cd, blocks: (B:3:0x0004, B:5:0x0009, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0077, B:18:0x007d, B:20:0x008d, B:21:0x0090, B:22:0x0096, B:24:0x009c, B:27:0x00a3, B:29:0x00ee, B:30:0x00f4, B:31:0x00f9, B:33:0x0102, B:34:0x0107, B:35:0x0112, B:37:0x0118, B:39:0x0133, B:40:0x0146, B:42:0x015c, B:47:0x0168, B:49:0x0174, B:50:0x0177, B:52:0x017d, B:55:0x0187, B:72:0x025e, B:74:0x0264, B:77:0x027b, B:79:0x0292, B:81:0x0297, B:82:0x029f, B:83:0x02ab, B:85:0x02b8, B:86:0x02be, B:87:0x02c3), top: B:98:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x029f A[Catch: all -> 0x02cd, TryCatch #3 {all -> 0x02cd, blocks: (B:3:0x0004, B:5:0x0009, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0077, B:18:0x007d, B:20:0x008d, B:21:0x0090, B:22:0x0096, B:24:0x009c, B:27:0x00a3, B:29:0x00ee, B:30:0x00f4, B:31:0x00f9, B:33:0x0102, B:34:0x0107, B:35:0x0112, B:37:0x0118, B:39:0x0133, B:40:0x0146, B:42:0x015c, B:47:0x0168, B:49:0x0174, B:50:0x0177, B:52:0x017d, B:55:0x0187, B:72:0x025e, B:74:0x0264, B:77:0x027b, B:79:0x0292, B:81:0x0297, B:82:0x029f, B:83:0x02ab, B:85:0x02b8, B:86:0x02be, B:87:0x02c3), top: B:98:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x02b8 A[Catch: all -> 0x02cd, TryCatch #3 {all -> 0x02cd, blocks: (B:3:0x0004, B:5:0x0009, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0077, B:18:0x007d, B:20:0x008d, B:21:0x0090, B:22:0x0096, B:24:0x009c, B:27:0x00a3, B:29:0x00ee, B:30:0x00f4, B:31:0x00f9, B:33:0x0102, B:34:0x0107, B:35:0x0112, B:37:0x0118, B:39:0x0133, B:40:0x0146, B:42:0x015c, B:47:0x0168, B:49:0x0174, B:50:0x0177, B:52:0x017d, B:55:0x0187, B:72:0x025e, B:74:0x0264, B:77:0x027b, B:79:0x0292, B:81:0x0297, B:82:0x029f, B:83:0x02ab, B:85:0x02b8, B:86:0x02be, B:87:0x02c3), top: B:98:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x02be A[Catch: all -> 0x02cd, TryCatch #3 {all -> 0x02cd, blocks: (B:3:0x0004, B:5:0x0009, B:6:0x002a, B:9:0x0045, B:11:0x004b, B:12:0x004f, B:14:0x0055, B:16:0x0077, B:18:0x007d, B:20:0x008d, B:21:0x0090, B:22:0x0096, B:24:0x009c, B:27:0x00a3, B:29:0x00ee, B:30:0x00f4, B:31:0x00f9, B:33:0x0102, B:34:0x0107, B:35:0x0112, B:37:0x0118, B:39:0x0133, B:40:0x0146, B:42:0x015c, B:47:0x0168, B:49:0x0174, B:50:0x0177, B:52:0x017d, B:55:0x0187, B:72:0x025e, B:74:0x0264, B:77:0x027b, B:79:0x0292, B:81:0x0297, B:82:0x029f, B:83:0x02ab, B:85:0x02b8, B:86:0x02be, B:87:0x02c3), top: B:98:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:94:0x01b7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$buildShortcuts$107(java.util.ArrayList r21) {
        /*
            Method dump skipped, instructions count: 718
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$buildShortcuts$107(java.util.ArrayList):void");
    }

    public void loadHints(boolean z) {
        if (this.loading || !getUserConfig().suggestContacts) {
            return;
        }
        if (z) {
            if (this.loaded) {
                return;
            }
            this.loading = true;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadHints$109();
                }
            });
            this.loaded = true;
            return;
        }
        this.loading = true;
        TLRPC$TL_contacts_getTopPeers tLRPC$TL_contacts_getTopPeers = new TLRPC$TL_contacts_getTopPeers();
        tLRPC$TL_contacts_getTopPeers.hash = 0L;
        tLRPC$TL_contacts_getTopPeers.bots_pm = false;
        tLRPC$TL_contacts_getTopPeers.correspondents = true;
        tLRPC$TL_contacts_getTopPeers.groups = false;
        tLRPC$TL_contacts_getTopPeers.channels = false;
        tLRPC$TL_contacts_getTopPeers.bots_inline = true;
        tLRPC$TL_contacts_getTopPeers.offset = 0;
        tLRPC$TL_contacts_getTopPeers.limit = 20;
        getConnectionsManager().sendRequest(tLRPC$TL_contacts_getTopPeers, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda142
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadHints$114(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadHints$109() {
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList<TLRPC$User> arrayList3 = new ArrayList<>();
        final ArrayList<TLRPC$Chat> arrayList4 = new ArrayList<>();
        long clientUserId = getUserConfig().getClientUserId();
        try {
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            int i = 0;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(i);
                if (longValue != clientUserId) {
                    int intValue = queryFinalized.intValue(1);
                    TLRPC$TL_topPeer tLRPC$TL_topPeer = new TLRPC$TL_topPeer();
                    tLRPC$TL_topPeer.rating = queryFinalized.doubleValue(2);
                    if (longValue > 0) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        tLRPC$TL_topPeer.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = longValue;
                        arrayList5.add(Long.valueOf(longValue));
                    } else {
                        TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                        tLRPC$TL_topPeer.peer = tLRPC$TL_peerChat;
                        long j = -longValue;
                        tLRPC$TL_peerChat.chat_id = j;
                        arrayList6.add(Long.valueOf(j));
                    }
                    if (intValue == 0) {
                        arrayList.add(tLRPC$TL_topPeer);
                    } else if (intValue == 1) {
                        arrayList2.add(tLRPC$TL_topPeer);
                    }
                    i = 0;
                }
            }
            queryFinalized.dispose();
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList5), arrayList3);
            }
            if (!arrayList6.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList6), arrayList4);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda72
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadHints$108(arrayList3, arrayList4, arrayList, arrayList2);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$loadHints$108(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        this.loading = false;
        this.loaded = true;
        this.hints = arrayList3;
        this.inlineBots = arrayList4;
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        if (Math.abs(getUserConfig().lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
            loadHints(false);
        }
    }

    public /* synthetic */ void lambda$loadHints$114(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda79
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadHints$112(tLObject);
                }
            });
        } else if (!(tLObject instanceof TLRPC$TL_contacts_topPeersDisabled)) {
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadHints$113();
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadHints$112(TLObject tLObject) {
        final TLRPC$TL_contacts_topPeers tLRPC$TL_contacts_topPeers = (TLRPC$TL_contacts_topPeers) tLObject;
        getMessagesController().putUsers(tLRPC$TL_contacts_topPeers.users, false);
        getMessagesController().putChats(tLRPC$TL_contacts_topPeers.chats, false);
        for (int i = 0; i < tLRPC$TL_contacts_topPeers.categories.size(); i++) {
            TLRPC$TL_topPeerCategoryPeers tLRPC$TL_topPeerCategoryPeers = tLRPC$TL_contacts_topPeers.categories.get(i);
            if (tLRPC$TL_topPeerCategoryPeers.category instanceof TLRPC$TL_topPeerCategoryBotsInline) {
                this.inlineBots = tLRPC$TL_topPeerCategoryPeers.peers;
                getUserConfig().botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = tLRPC$TL_topPeerCategoryPeers.peers;
                long clientUserId = getUserConfig().getClientUserId();
                int i2 = 0;
                while (true) {
                    if (i2 >= this.hints.size()) {
                        break;
                    } else if (this.hints.get(i2).peer.user_id == clientUserId) {
                        this.hints.remove(i2);
                        break;
                    } else {
                        i2++;
                    }
                }
                getUserConfig().ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
            }
        }
        getUserConfig().saveConfig(false);
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda95
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadHints$111(tLRPC$TL_contacts_topPeers);
            }
        });
    }

    public /* synthetic */ void lambda$loadHints$111(TLRPC$TL_contacts_topPeers tLRPC$TL_contacts_topPeers) {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
            getMessagesStorage().getDatabase().beginTransaction();
            getMessagesStorage().putUsersAndChats(tLRPC$TL_contacts_topPeers.users, tLRPC$TL_contacts_topPeers.chats, false, false);
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            for (int i = 0; i < tLRPC$TL_contacts_topPeers.categories.size(); i++) {
                TLRPC$TL_topPeerCategoryPeers tLRPC$TL_topPeerCategoryPeers = tLRPC$TL_contacts_topPeers.categories.get(i);
                int i2 = tLRPC$TL_topPeerCategoryPeers.category instanceof TLRPC$TL_topPeerCategoryBotsInline ? 1 : 0;
                for (int i3 = 0; i3 < tLRPC$TL_topPeerCategoryPeers.peers.size(); i3++) {
                    TLRPC$TL_topPeer tLRPC$TL_topPeer = tLRPC$TL_topPeerCategoryPeers.peers.get(i3);
                    executeFast.requery();
                    executeFast.bindLong(1, MessageObject.getPeerId(tLRPC$TL_topPeer.peer));
                    executeFast.bindInteger(2, i2);
                    executeFast.bindDouble(3, tLRPC$TL_topPeer.rating);
                    executeFast.bindInteger(4, 0);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadHints$110();
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$loadHints$110() {
        getUserConfig().suggestContacts = true;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
    }

    public /* synthetic */ void lambda$loadHints$113() {
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
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$clearTopPeers$115();
            }
        });
        buildShortcuts();
    }

    public /* synthetic */ void lambda$clearTopPeers$115() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception unused) {
        }
    }

    public void increaseInlineRaiting(long j) {
        if (!getUserConfig().suggestContacts) {
            return;
        }
        int max = getUserConfig().botRatingLoadTime != 0 ? Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - getUserConfig().botRatingLoadTime) : 60;
        TLRPC$TL_topPeer tLRPC$TL_topPeer = null;
        int i = 0;
        while (true) {
            if (i >= this.inlineBots.size()) {
                break;
            }
            TLRPC$TL_topPeer tLRPC$TL_topPeer2 = this.inlineBots.get(i);
            if (tLRPC$TL_topPeer2.peer.user_id == j) {
                tLRPC$TL_topPeer = tLRPC$TL_topPeer2;
                break;
            }
            i++;
        }
        if (tLRPC$TL_topPeer == null) {
            tLRPC$TL_topPeer = new TLRPC$TL_topPeer();
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$TL_topPeer.peer = tLRPC$TL_peerUser;
            tLRPC$TL_peerUser.user_id = j;
            this.inlineBots.add(tLRPC$TL_topPeer);
        }
        tLRPC$TL_topPeer.rating += Math.exp(max / getMessagesController().ratingDecay);
        Collections.sort(this.inlineBots, MediaDataController$$ExternalSyntheticLambda134.INSTANCE);
        if (this.inlineBots.size() > 20) {
            ArrayList<TLRPC$TL_topPeer> arrayList = this.inlineBots;
            arrayList.remove(arrayList.size() - 1);
        }
        savePeer(j, 1, tLRPC$TL_topPeer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public static /* synthetic */ int lambda$increaseInlineRaiting$116(TLRPC$TL_topPeer tLRPC$TL_topPeer, TLRPC$TL_topPeer tLRPC$TL_topPeer2) {
        double d = tLRPC$TL_topPeer.rating;
        double d2 = tLRPC$TL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    public void removeInline(long j) {
        for (int i = 0; i < this.inlineBots.size(); i++) {
            if (this.inlineBots.get(i).peer.user_id == j) {
                this.inlineBots.remove(i);
                TLRPC$TL_contacts_resetTopPeerRating tLRPC$TL_contacts_resetTopPeerRating = new TLRPC$TL_contacts_resetTopPeerRating();
                tLRPC$TL_contacts_resetTopPeerRating.category = new TLRPC$TL_topPeerCategoryBotsInline();
                tLRPC$TL_contacts_resetTopPeerRating.peer = getMessagesController().getInputPeer(j);
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_resetTopPeerRating, MediaDataController$$ExternalSyntheticLambda185.INSTANCE);
                deletePeer(j, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public void removePeer(long j) {
        for (int i = 0; i < this.hints.size(); i++) {
            if (this.hints.get(i).peer.user_id == j) {
                this.hints.remove(i);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TLRPC$TL_contacts_resetTopPeerRating tLRPC$TL_contacts_resetTopPeerRating = new TLRPC$TL_contacts_resetTopPeerRating();
                tLRPC$TL_contacts_resetTopPeerRating.category = new TLRPC$TL_topPeerCategoryCorrespondents();
                tLRPC$TL_contacts_resetTopPeerRating.peer = getMessagesController().getInputPeer(j);
                deletePeer(j, 0);
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_resetTopPeerRating, MediaDataController$$ExternalSyntheticLambda184.INSTANCE);
                return;
            }
        }
    }

    public void increasePeerRaiting(final long j) {
        TLRPC$User user;
        if (getUserConfig().suggestContacts && DialogObject.isUserDialog(j) && (user = getMessagesController().getUser(Long.valueOf(j))) != null && !user.bot && !user.self) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$increasePeerRaiting$121(j);
                }
            });
        }
    }

    public /* synthetic */ void lambda$increasePeerRaiting$121(final long j) {
        int i;
        double d = 0.0d;
        try {
            int i2 = 0;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages_v2 WHERE uid = %d AND out = 1", Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next()) {
                i2 = queryFinalized.intValue(0);
                i = queryFinalized.intValue(1);
            } else {
                i = 0;
            }
            queryFinalized.dispose();
            if (i2 > 0 && getUserConfig().ratingLoadTime != 0) {
                d = i - getUserConfig().ratingLoadTime;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        final double d2 = d;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$increasePeerRaiting$120(j, d2);
            }
        });
    }

    public /* synthetic */ void lambda$increasePeerRaiting$120(long j, double d) {
        TLRPC$TL_topPeer tLRPC$TL_topPeer;
        int i = 0;
        while (true) {
            if (i >= this.hints.size()) {
                tLRPC$TL_topPeer = null;
                break;
            }
            tLRPC$TL_topPeer = this.hints.get(i);
            if (tLRPC$TL_topPeer.peer.user_id == j) {
                break;
            }
            i++;
        }
        if (tLRPC$TL_topPeer == null) {
            tLRPC$TL_topPeer = new TLRPC$TL_topPeer();
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$TL_topPeer.peer = tLRPC$TL_peerUser;
            tLRPC$TL_peerUser.user_id = j;
            this.hints.add(tLRPC$TL_topPeer);
        }
        double d2 = tLRPC$TL_topPeer.rating;
        double d3 = getMessagesController().ratingDecay;
        Double.isNaN(d3);
        tLRPC$TL_topPeer.rating = d2 + Math.exp(d / d3);
        Collections.sort(this.hints, MediaDataController$$ExternalSyntheticLambda135.INSTANCE);
        savePeer(j, 0, tLRPC$TL_topPeer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    public static /* synthetic */ int lambda$increasePeerRaiting$119(TLRPC$TL_topPeer tLRPC$TL_topPeer, TLRPC$TL_topPeer tLRPC$TL_topPeer2) {
        double d = tLRPC$TL_topPeer.rating;
        double d2 = tLRPC$TL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    private void savePeer(final long j, final int i, final double d) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$savePeer$122(j, i, d);
            }
        });
    }

    public /* synthetic */ void lambda$savePeer$122(long j, int i, double d) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, i);
            executeFast.bindDouble(3, d);
            executeFast.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void deletePeer(final long j, final int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$deletePeer$123(j, i);
            }
        });
    }

    public /* synthetic */ void lambda$deletePeer$123(long j, int i) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private Intent createIntrnalShortcutIntent(long j) {
        Intent intent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        if (DialogObject.isEncryptedDialog(j)) {
            int encryptedChatId = DialogObject.getEncryptedChatId(j);
            intent.putExtra("encId", encryptedChatId);
            if (getMessagesController().getEncryptedChat(Integer.valueOf(encryptedChatId)) == null) {
                return null;
            }
        } else if (DialogObject.isUserDialog(j)) {
            intent.putExtra("userId", j);
        } else if (!DialogObject.isChatDialog(j)) {
            return null;
        } else {
            intent.putExtra("chatId", -j);
        }
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setAction("com.tmessages.openchat" + j);
        intent.addFlags(ConnectionsManager.FileTypeFile);
        return intent;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(20:106|3|(2:5|(1:7)(1:8))(2:9|(1:11)(2:13|(2:15|(5:19|(3:21|(1:23)(2:25|(1:27)(4:28|(2:30|35)|34|35))|24)(4:31|(2:33|35)|34|35)|(2:(2:104|40)(1:43)|(8:46|103|47|(3:49|(1:51)(1:52)|53)(3:54|(1:56)|57)|58|101|59|60))(1:38)|63|(4:65|(1:67)(1:(2:69|(1:71)(1:72))(2:73|(1:78)(1:77)))|79|107)(4:80|(1:82)(2:83|(2:85|(1:87)(1:88))(2:89|(1:94)(1:93)))|95|108))(1:18))(1:96)))|12|(0)|19|(0)(0)|(0)|(0)(0)|(0)|46|103|47|(0)(0)|58|101|59|60|63|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0171, code lost:
        r0 = th;
     */
    /* JADX WARN: Removed duplicated region for block: B:104:0x00a8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0062 A[Catch: Exception -> 0x0253, TryCatch #3 {Exception -> 0x0253, blocks: (B:3:0x0002, B:5:0x000d, B:8:0x0020, B:9:0x002f, B:11:0x0035, B:13:0x0044, B:15:0x004a, B:21:0x0062, B:23:0x0068, B:25:0x0074, B:27:0x007a, B:28:0x0084, B:30:0x0090, B:31:0x0093, B:33:0x0099, B:62:0x0172, B:63:0x0175, B:65:0x0187, B:67:0x01a9, B:69:0x01b3, B:71:0x01b7, B:72:0x01c1, B:73:0x01cb, B:75:0x01d1, B:77:0x01d5, B:78:0x01df, B:79:0x01e8, B:80:0x01f2, B:82:0x01f9, B:85:0x0203, B:87:0x0207, B:88:0x0211, B:89:0x021b, B:91:0x0221, B:93:0x0225, B:94:0x022f, B:95:0x0238), top: B:106:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0093 A[Catch: Exception -> 0x0253, TryCatch #3 {Exception -> 0x0253, blocks: (B:3:0x0002, B:5:0x000d, B:8:0x0020, B:9:0x002f, B:11:0x0035, B:13:0x0044, B:15:0x004a, B:21:0x0062, B:23:0x0068, B:25:0x0074, B:27:0x007a, B:28:0x0084, B:30:0x0090, B:31:0x0093, B:33:0x0099, B:62:0x0172, B:63:0x0175, B:65:0x0187, B:67:0x01a9, B:69:0x01b3, B:71:0x01b7, B:72:0x01c1, B:73:0x01cb, B:75:0x01d1, B:77:0x01d5, B:78:0x01df, B:79:0x01e8, B:80:0x01f2, B:82:0x01f9, B:85:0x0203, B:87:0x0207, B:88:0x0211, B:89:0x021b, B:91:0x0221, B:93:0x0225, B:94:0x022f, B:95:0x0238), top: B:106:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00d8 A[Catch: all -> 0x0171, TryCatch #1 {all -> 0x0171, blocks: (B:47:0x00c4, B:49:0x00d8, B:51:0x00e3, B:52:0x00e9, B:53:0x00ec, B:54:0x00f3, B:56:0x00fe, B:57:0x010c, B:58:0x0142, B:59:0x016c), top: B:103:0x00c4 }] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00f3 A[Catch: all -> 0x0171, TryCatch #1 {all -> 0x0171, blocks: (B:47:0x00c4, B:49:0x00d8, B:51:0x00e3, B:52:0x00e9, B:53:0x00ec, B:54:0x00f3, B:56:0x00fe, B:57:0x010c, B:58:0x0142, B:59:0x016c), top: B:103:0x00c4 }] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0187 A[Catch: Exception -> 0x0253, TryCatch #3 {Exception -> 0x0253, blocks: (B:3:0x0002, B:5:0x000d, B:8:0x0020, B:9:0x002f, B:11:0x0035, B:13:0x0044, B:15:0x004a, B:21:0x0062, B:23:0x0068, B:25:0x0074, B:27:0x007a, B:28:0x0084, B:30:0x0090, B:31:0x0093, B:33:0x0099, B:62:0x0172, B:63:0x0175, B:65:0x0187, B:67:0x01a9, B:69:0x01b3, B:71:0x01b7, B:72:0x01c1, B:73:0x01cb, B:75:0x01d1, B:77:0x01d5, B:78:0x01df, B:79:0x01e8, B:80:0x01f2, B:82:0x01f9, B:85:0x0203, B:87:0x0207, B:88:0x0211, B:89:0x021b, B:91:0x0221, B:93:0x0225, B:94:0x022f, B:95:0x0238), top: B:106:0x0002 }] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x01f2 A[Catch: Exception -> 0x0253, TryCatch #3 {Exception -> 0x0253, blocks: (B:3:0x0002, B:5:0x000d, B:8:0x0020, B:9:0x002f, B:11:0x0035, B:13:0x0044, B:15:0x004a, B:21:0x0062, B:23:0x0068, B:25:0x0074, B:27:0x007a, B:28:0x0084, B:30:0x0090, B:31:0x0093, B:33:0x0099, B:62:0x0172, B:63:0x0175, B:65:0x0187, B:67:0x01a9, B:69:0x01b3, B:71:0x01b7, B:72:0x01c1, B:73:0x01cb, B:75:0x01d1, B:77:0x01d5, B:78:0x01df, B:79:0x01e8, B:80:0x01f2, B:82:0x01f9, B:85:0x0203, B:87:0x0207, B:88:0x0211, B:89:0x021b, B:91:0x0221, B:93:0x0225, B:94:0x022f, B:95:0x0238), top: B:106:0x0002 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void installShortcut(long r17) {
        /*
            Method dump skipped, instructions count: 600
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.installShortcut(long):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x00a4 A[Catch: Exception -> 0x00d4, TryCatch #0 {Exception -> 0x00d4, blocks: (B:2:0x0000, B:4:0x0006, B:6:0x003c, B:7:0x004b, B:9:0x0052, B:12:0x0065, B:13:0x0074, B:15:0x007a, B:17:0x008a, B:19:0x0090, B:24:0x00a4, B:25:0x00ad, B:26:0x00af), top: B:31:0x0000 }] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00ad A[Catch: Exception -> 0x00d4, TryCatch #0 {Exception -> 0x00d4, blocks: (B:2:0x0000, B:4:0x0006, B:6:0x003c, B:7:0x004b, B:9:0x0052, B:12:0x0065, B:13:0x0074, B:15:0x007a, B:17:0x008a, B:19:0x0090, B:24:0x00a4, B:25:0x00ad, B:26:0x00af), top: B:31:0x0000 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void uninstallShortcut(long r7) {
        /*
            r6 = this;
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Exception -> Ld4
            r1 = 26
            if (r0 < r1) goto L4b
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch: java.lang.Exception -> Ld4
            r1.<init>()     // Catch: java.lang.Exception -> Ld4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> Ld4
            r2.<init>()     // Catch: java.lang.Exception -> Ld4
            java.lang.String r3 = "sdid_"
            r2.append(r3)     // Catch: java.lang.Exception -> Ld4
            r2.append(r7)     // Catch: java.lang.Exception -> Ld4
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Exception -> Ld4
            r1.add(r2)     // Catch: java.lang.Exception -> Ld4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> Ld4
            r2.<init>()     // Catch: java.lang.Exception -> Ld4
            java.lang.String r3 = "ndid_"
            r2.append(r3)     // Catch: java.lang.Exception -> Ld4
            r2.append(r7)     // Catch: java.lang.Exception -> Ld4
            java.lang.String r7 = r2.toString()     // Catch: java.lang.Exception -> Ld4
            r1.add(r7)     // Catch: java.lang.Exception -> Ld4
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch: java.lang.Exception -> Ld4
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r7, r1)     // Catch: java.lang.Exception -> Ld4
            r7 = 30
            if (r0 < r7) goto Ld8
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch: java.lang.Exception -> Ld4
            java.lang.Class<android.content.pm.ShortcutManager> r8 = android.content.pm.ShortcutManager.class
            java.lang.Object r7 = r7.getSystemService(r8)     // Catch: java.lang.Exception -> Ld4
            android.content.pm.ShortcutManager r7 = (android.content.pm.ShortcutManager) r7     // Catch: java.lang.Exception -> Ld4
            r7.removeLongLivedShortcuts(r1)     // Catch: java.lang.Exception -> Ld4
            goto Ld8
        L4b:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)     // Catch: java.lang.Exception -> Ld4
            r1 = 0
            if (r0 == 0) goto L74
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r7)     // Catch: java.lang.Exception -> Ld4
            org.telegram.messenger.MessagesController r2 = r6.getMessagesController()     // Catch: java.lang.Exception -> Ld4
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch: java.lang.Exception -> Ld4
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r2.getEncryptedChat(r0)     // Catch: java.lang.Exception -> Ld4
            if (r0 != 0) goto L65
            return
        L65:
            org.telegram.messenger.MessagesController r2 = r6.getMessagesController()     // Catch: java.lang.Exception -> Ld4
            long r3 = r0.user_id     // Catch: java.lang.Exception -> Ld4
            java.lang.Long r0 = java.lang.Long.valueOf(r3)     // Catch: java.lang.Exception -> Ld4
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)     // Catch: java.lang.Exception -> Ld4
            goto L86
        L74:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r7)     // Catch: java.lang.Exception -> Ld4
            if (r0 == 0) goto L8a
            org.telegram.messenger.MessagesController r0 = r6.getMessagesController()     // Catch: java.lang.Exception -> Ld4
            java.lang.Long r2 = java.lang.Long.valueOf(r7)     // Catch: java.lang.Exception -> Ld4
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)     // Catch: java.lang.Exception -> Ld4
        L86:
            r5 = r1
            r1 = r0
            r0 = r5
            goto L9d
        L8a:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r7)     // Catch: java.lang.Exception -> Ld4
            if (r0 == 0) goto Ld3
            org.telegram.messenger.MessagesController r0 = r6.getMessagesController()     // Catch: java.lang.Exception -> Ld4
            long r2 = -r7
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch: java.lang.Exception -> Ld4
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)     // Catch: java.lang.Exception -> Ld4
        L9d:
            if (r1 != 0) goto La2
            if (r0 != 0) goto La2
            return
        La2:
            if (r1 == 0) goto Lad
            java.lang.String r0 = r1.first_name     // Catch: java.lang.Exception -> Ld4
            java.lang.String r1 = r1.last_name     // Catch: java.lang.Exception -> Ld4
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r1)     // Catch: java.lang.Exception -> Ld4
            goto Laf
        Lad:
            java.lang.String r0 = r0.title     // Catch: java.lang.Exception -> Ld4
        Laf:
            android.content.Intent r1 = new android.content.Intent     // Catch: java.lang.Exception -> Ld4
            r1.<init>()     // Catch: java.lang.Exception -> Ld4
            java.lang.String r2 = "android.intent.extra.shortcut.INTENT"
            android.content.Intent r7 = r6.createIntrnalShortcutIntent(r7)     // Catch: java.lang.Exception -> Ld4
            r1.putExtra(r2, r7)     // Catch: java.lang.Exception -> Ld4
            java.lang.String r7 = "android.intent.extra.shortcut.NAME"
            r1.putExtra(r7, r0)     // Catch: java.lang.Exception -> Ld4
            java.lang.String r7 = "duplicate"
            r8 = 0
            r1.putExtra(r7, r8)     // Catch: java.lang.Exception -> Ld4
            java.lang.String r7 = "com.android.launcher.action.UNINSTALL_SHORTCUT"
            r1.setAction(r7)     // Catch: java.lang.Exception -> Ld4
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch: java.lang.Exception -> Ld4
            r7.sendBroadcast(r1)     // Catch: java.lang.Exception -> Ld4
            goto Ld8
        Ld3:
            return
        Ld4:
            r7 = move-exception
            org.telegram.messenger.FileLog.e(r7)
        Ld8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.uninstallShortcut(long):void");
    }

    public static /* synthetic */ int lambda$static$124(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
        int i = tLRPC$MessageEntity.offset;
        int i2 = tLRPC$MessageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void loadPinnedMessages(final long j, final int i, final int i2) {
        if (this.loadingPinnedMessages.indexOfKey(j) >= 0) {
            return;
        }
        this.loadingPinnedMessages.put(j, Boolean.TRUE);
        final TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
        tLRPC$TL_messages_search.peer = getMessagesController().getInputPeer(j);
        tLRPC$TL_messages_search.limit = 40;
        tLRPC$TL_messages_search.offset_id = i;
        tLRPC$TL_messages_search.q = "";
        tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterPinned();
        getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda154
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadPinnedMessages$126(i2, tLRPC$TL_messages_search, j, i, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadPinnedMessages$126(int i, TLRPC$TL_messages_search tLRPC$TL_messages_search, final long j, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        boolean z;
        int i3;
        int i4;
        ArrayList<Integer> arrayList = new ArrayList<>();
        HashMap<Integer, MessageObject> hashMap = new HashMap<>();
        if (tLObject instanceof TLRPC$messages_Messages) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i5 = 0; i5 < tLRPC$messages_Messages.users.size(); i5++) {
                TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i5);
                longSparseArray.put(tLRPC$User.id, tLRPC$User);
            }
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i6 = 0; i6 < tLRPC$messages_Messages.chats.size(); i6++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$messages_Messages.chats.get(i6);
                longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
            }
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            getMessagesController().putUsers(tLRPC$messages_Messages.users, false);
            getMessagesController().putChats(tLRPC$messages_Messages.chats, false);
            int size = tLRPC$messages_Messages.messages.size();
            for (int i7 = 0; i7 < size; i7++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i7);
                if (!(tLRPC$Message instanceof TLRPC$TL_messageService) && !(tLRPC$Message instanceof TLRPC$TL_messageEmpty)) {
                    arrayList.add(Integer.valueOf(tLRPC$Message.id));
                    hashMap.put(Integer.valueOf(tLRPC$Message.id), new MessageObject(this.currentAccount, tLRPC$Message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, false, false));
                }
            }
            if (i != 0 && arrayList.isEmpty()) {
                arrayList.add(Integer.valueOf(i));
            }
            boolean z2 = tLRPC$messages_Messages.messages.size() < tLRPC$TL_messages_search.limit;
            i3 = Math.max(tLRPC$messages_Messages.count, tLRPC$messages_Messages.messages.size());
            z = z2;
        } else {
            if (i != 0) {
                arrayList.add(Integer.valueOf(i));
                i4 = 1;
            } else {
                i4 = 0;
            }
            i3 = i4;
            z = false;
        }
        getMessagesStorage().updatePinnedMessages(j, arrayList, true, i3, i2, z, hashMap);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadPinnedMessages$125(j);
            }
        });
    }

    public /* synthetic */ void lambda$loadPinnedMessages$125(long j) {
        this.loadingPinnedMessages.remove(j);
    }

    public /* synthetic */ void lambda$loadPinnedMessages$127(long j, long j2, ArrayList arrayList) {
        loadPinnedMessageInternal(j, j2, arrayList, false);
    }

    public ArrayList<MessageObject> loadPinnedMessages(final long j, final long j2, final ArrayList<Integer> arrayList, boolean z) {
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadPinnedMessages$127(j, j2, arrayList);
                }
            });
            return null;
        }
        return loadPinnedMessageInternal(j, j2, arrayList, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0176 A[Catch: Exception -> 0x01c9, TryCatch #1 {Exception -> 0x01c9, blocks: (B:42:0x0167, B:44:0x0170, B:46:0x0176, B:48:0x017c, B:50:0x018c, B:52:0x0192, B:55:0x01a4, B:57:0x01b7), top: B:64:0x0167 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r18v0, types: [org.telegram.messenger.MediaDataController, org.telegram.messenger.BaseController] */
    /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v12, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v13, types: [java.lang.StringBuilder] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.ArrayList<org.telegram.messenger.MessageObject> loadPinnedMessageInternal(final long r19, final long r21, java.util.ArrayList<java.lang.Integer> r23, boolean r24) {
        /*
            Method dump skipped, instructions count: 467
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadPinnedMessageInternal(long, long, java.util.ArrayList, boolean):java.util.ArrayList");
    }

    /* JADX WARN: Removed duplicated region for block: B:11:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0048  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$128(long r13, long r15, org.telegram.tgnet.TLRPC$TL_channels_getMessages r17, org.telegram.tgnet.TLObject r18, org.telegram.tgnet.TLRPC$TL_error r19) {
        /*
            r12 = this;
            r0 = 1
            if (r19 != 0) goto L43
            r1 = r18
            org.telegram.tgnet.TLRPC$messages_Messages r1 = (org.telegram.tgnet.TLRPC$messages_Messages) r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r1.messages
            removeEmptyMessages(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r1.messages
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L43
            org.telegram.messenger.MessagesController r2 = r12.getMessagesController()
            java.lang.Long r3 = java.lang.Long.valueOf(r13)
            r2.getChat(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r1.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r1.messages
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r1.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r1.chats
            r7 = 0
            r8 = 0
            r3 = r12
            r3.broadcastPinnedMessage(r4, r5, r6, r7, r8)
            org.telegram.messenger.MessagesStorage r2 = r12.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r1.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r1.chats
            r2.putUsersAndChats(r3, r4, r0, r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r1.messages
            r2 = r12
            r4 = r15
            r12.savePinnedMessages(r4, r1)
            goto L46
        L43:
            r2 = r12
            r4 = r15
            r0 = 0
        L46:
            if (r0 != 0) goto L59
            org.telegram.messenger.MessagesStorage r3 = r12.getMessagesStorage()
            r0 = r17
            java.util.ArrayList<java.lang.Integer> r6 = r0.id
            r7 = 0
            r8 = -1
            r9 = 0
            r10 = 0
            r11 = 0
            r4 = r15
            r3.updatePinnedMessages(r4, r6, r7, r8, r9, r10, r11)
        L59:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$128(long, long, org.telegram.tgnet.TLRPC$TL_channels_getMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:11:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0037  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$129(long r11, org.telegram.tgnet.TLRPC$TL_messages_getMessages r13, org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC$TL_error r15) {
        /*
            r10 = this;
            r0 = 1
            if (r15 != 0) goto L34
            org.telegram.tgnet.TLRPC$messages_Messages r14 = (org.telegram.tgnet.TLRPC$messages_Messages) r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            removeEmptyMessages(r15)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            boolean r15 = r15.isEmpty()
            if (r15 != 0) goto L34
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r15)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r14.messages
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r14.chats
            r5 = 0
            r6 = 0
            r1 = r10
            r1.broadcastPinnedMessage(r2, r3, r4, r5, r6)
            org.telegram.messenger.MessagesStorage r15 = r10.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r14.chats
            r15.putUsersAndChats(r1, r2, r0, r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r14 = r14.messages
            r10.savePinnedMessages(r11, r14)
            goto L35
        L34:
            r0 = 0
        L35:
            if (r0 != 0) goto L46
            org.telegram.messenger.MessagesStorage r1 = r10.getMessagesStorage()
            java.util.ArrayList<java.lang.Integer> r4 = r13.id
            r5 = 0
            r6 = -1
            r7 = 0
            r8 = 0
            r9 = 0
            r2 = r11
            r1.updatePinnedMessages(r2, r4, r5, r6, r7, r8, r9)
        L46:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$129(long, org.telegram.tgnet.TLRPC$TL_messages_getMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    private void savePinnedMessages(final long j, final ArrayList<TLRPC$Message> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$savePinnedMessages$130(arrayList, j);
            }
        });
    }

    public /* synthetic */ void lambda$savePinnedMessages$130(ArrayList arrayList, long j) {
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned_v2 VALUES(?, ?, ?)");
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                tLRPC$Message.serializeToStream(nativeByteBuffer);
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, tLRPC$Message.id);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private ArrayList<MessageObject> broadcastPinnedMessage(final ArrayList<TLRPC$Message> arrayList, final ArrayList<TLRPC$User> arrayList2, final ArrayList<TLRPC$Chat> arrayList3, final boolean z, boolean z2) {
        if (arrayList.isEmpty()) {
            return null;
        }
        final LongSparseArray longSparseArray = new LongSparseArray();
        for (int i = 0; i < arrayList2.size(); i++) {
            TLRPC$User tLRPC$User = arrayList2.get(i);
            longSparseArray.put(tLRPC$User.id, tLRPC$User);
        }
        final LongSparseArray longSparseArray2 = new LongSparseArray();
        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
            TLRPC$Chat tLRPC$Chat = arrayList3.get(i2);
            longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
        }
        final ArrayList<MessageObject> arrayList4 = new ArrayList<>();
        if (z2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda73
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$broadcastPinnedMessage$131(arrayList2, z, arrayList3);
                }
            });
            int size = arrayList.size();
            int i3 = 0;
            int i4 = 0;
            while (i4 < size) {
                TLRPC$Message tLRPC$Message = arrayList.get(i4);
                TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto)) {
                    i3++;
                }
                int i5 = i3;
                arrayList4.add(new MessageObject(this.currentAccount, tLRPC$Message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, false, i5 < 30));
                i4++;
                i3 = i5;
            }
            return arrayList4;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda75
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$broadcastPinnedMessage$133(arrayList2, z, arrayList3, arrayList, arrayList4, longSparseArray, longSparseArray2);
            }
        });
        return null;
    }

    public /* synthetic */ void lambda$broadcastPinnedMessage$131(ArrayList arrayList, boolean z, ArrayList arrayList2) {
        getMessagesController().putUsers(arrayList, z);
        getMessagesController().putChats(arrayList2, z);
    }

    public /* synthetic */ void lambda$broadcastPinnedMessage$133(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, final ArrayList arrayList4, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        getMessagesController().putUsers(arrayList, z);
        getMessagesController().putChats(arrayList2, z);
        int size = arrayList3.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList3.get(i2);
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto)) {
                i++;
            }
            arrayList4.add(new MessageObject(this.currentAccount, tLRPC$Message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, false, i < 30));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda63
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$broadcastPinnedMessage$132(arrayList4);
            }
        });
    }

    public /* synthetic */ void lambda$broadcastPinnedMessage$132(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(((MessageObject) arrayList.get(0)).getDialogId()), null, Boolean.TRUE, arrayList, 0, 0, -1, Boolean.FALSE);
    }

    private static void removeEmptyMessages(ArrayList<TLRPC$Message> arrayList) {
        int i = 0;
        while (i < arrayList.size()) {
            TLRPC$Message tLRPC$Message = arrayList.get(i);
            if (tLRPC$Message == null || (tLRPC$Message instanceof TLRPC$TL_messageEmpty) || (tLRPC$Message.action instanceof TLRPC$TL_messageActionHistoryClear)) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:38:0x00b6, code lost:
        if (r12 != 0) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00bf, code lost:
        if (r12 != 0) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00c1, code lost:
        r10 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00c2, code lost:
        r8 = r1.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00c4, code lost:
        if (r8 == null) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00c6, code lost:
        r8 = r8.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00c8, code lost:
        if (r8 == null) goto L81;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00ca, code lost:
        r8 = r8.peer_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00cc, code lost:
        if (r8 == null) goto L82;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x00d0, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty) == false) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00d7, code lost:
        if (r8.channel_id != r10) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00da, code lost:
        r7 = (android.util.SparseArray) r3.get(r16);
        r8 = (java.util.ArrayList) r4.get(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00e6, code lost:
        if (r7 != null) goto L55;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00e8, code lost:
        r7 = new android.util.SparseArray();
        r3.put(r16, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00f0, code lost:
        if (r8 != null) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00f2, code lost:
        r8 = new java.util.ArrayList();
        r4.put(r10, r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00fa, code lost:
        r10 = (java.util.ArrayList) r7.get(r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0100, code lost:
        if (r10 != null) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0102, code lost:
        r10 = new java.util.ArrayList();
        r7.put(r9, r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0112, code lost:
        if (r8.contains(java.lang.Integer.valueOf(r9)) != false) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0114, code lost:
        r8.add(java.lang.Integer.valueOf(r9));
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x011b, code lost:
        r10.add(r1);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadReplyMessagesForMessages(java.util.ArrayList<org.telegram.messenger.MessageObject> r15, final long r16, final boolean r18, final java.lang.Runnable r19) {
        /*
            Method dump skipped, instructions count: 329
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadReplyMessagesForMessages(java.util.ArrayList, long, boolean, java.lang.Runnable):void");
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$135(ArrayList arrayList, final long j, LongSparseArray longSparseArray, Runnable runnable) {
        try {
            final ArrayList arrayList2 = new ArrayList();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms_v2 as r INNER JOIN messages_v2 as m ON r.mid = m.mid AND r.uid = m.uid WHERE r.random_id IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.date = queryFinalized.intValue(2);
                    TLdeserialize.dialog_id = j;
                    long longValue = queryFinalized.longValue(3);
                    ArrayList arrayList3 = (ArrayList) longSparseArray.get(longValue);
                    longSparseArray.remove(longValue);
                    if (arrayList3 != null) {
                        MessageObject messageObject = new MessageObject(this.currentAccount, TLdeserialize, false, false);
                        arrayList2.add(messageObject);
                        for (int i = 0; i < arrayList3.size(); i++) {
                            MessageObject messageObject2 = (MessageObject) arrayList3.get(i);
                            messageObject2.replyMessageObject = messageObject;
                            messageObject2.messageOwner.reply_to = new TLRPC$TL_messageReplyHeader();
                            messageObject2.messageOwner.reply_to.reply_to_msg_id = messageObject.getId();
                        }
                    }
                }
            }
            queryFinalized.dispose();
            if (longSparseArray.size() != 0) {
                for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                    ArrayList arrayList4 = (ArrayList) longSparseArray.valueAt(i2);
                    for (int i3 = 0; i3 < arrayList4.size(); i3++) {
                        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = ((MessageObject) arrayList4.get(i3)).messageOwner.reply_to;
                        if (tLRPC$TL_messageReplyHeader != null) {
                            tLRPC$TL_messageReplyHeader.reply_to_random_id = 0L;
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadReplyMessagesForMessages$134(j, arrayList2);
                }
            });
            if (runnable == null) {
                return;
            }
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$134(long j, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j), arrayList, 0);
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$139(final LongSparseArray longSparseArray, LongSparseArray longSparseArray2, final boolean z, final long j, final Runnable runnable) {
        int i;
        int i2;
        boolean z2;
        SQLiteCursor sQLiteCursor;
        LongSparseArray longSparseArray3 = longSparseArray;
        try {
            ArrayList<TLRPC$Message> arrayList = new ArrayList<>();
            ArrayList<TLRPC$User> arrayList2 = new ArrayList<>();
            ArrayList<TLRPC$Chat> arrayList3 = new ArrayList<>();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            int i3 = 0;
            for (int size = longSparseArray.size(); i3 < size; size = i2) {
                long keyAt = longSparseArray3.keyAt(i3);
                SparseArray sparseArray = (SparseArray) longSparseArray3.valueAt(i3);
                ArrayList arrayList6 = (ArrayList) longSparseArray2.get(keyAt);
                if (arrayList6 == null) {
                    i2 = size;
                } else {
                    if (z) {
                        i2 = size;
                        sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM scheduled_messages_v2 WHERE mid IN(%s) AND uid = %d", TextUtils.join(",", arrayList6), Long.valueOf(j)), new Object[0]);
                        z2 = false;
                    } else {
                        i2 = size;
                        SQLiteDatabase database = getMessagesStorage().getDatabase();
                        Locale locale = Locale.US;
                        String join = TextUtils.join(",", arrayList6);
                        z2 = false;
                        sQLiteCursor = database.queryFinalized(String.format(locale, "SELECT data, mid, date, uid FROM messages_v2 WHERE mid IN(%s) AND uid = %d", join, Long.valueOf(j)), new Object[0]);
                    }
                    while (sQLiteCursor.next()) {
                        int i4 = z2 ? 1 : 0;
                        int i5 = z2 ? 1 : 0;
                        int i6 = z2 ? 1 : 0;
                        int i7 = z2 ? 1 : 0;
                        NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(i4);
                        if (byteBufferValue != null) {
                            TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z2), z2);
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            TLdeserialize.id = sQLiteCursor.intValue(1);
                            TLdeserialize.date = sQLiteCursor.intValue(2);
                            TLdeserialize.dialog_id = j;
                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList4, arrayList5, null);
                            arrayList.add(TLdeserialize);
                            TLRPC$Peer tLRPC$Peer = TLdeserialize.peer_id;
                            long j2 = tLRPC$Peer != null ? tLRPC$Peer.channel_id : 0L;
                            ArrayList arrayList7 = (ArrayList) longSparseArray2.get(j2);
                            if (arrayList7 != null) {
                                arrayList7.remove(Integer.valueOf(TLdeserialize.id));
                                if (arrayList7.isEmpty()) {
                                    longSparseArray2.remove(j2);
                                }
                            }
                        }
                        z2 = false;
                    }
                    sQLiteCursor.dispose();
                }
                i3++;
                longSparseArray3 = longSparseArray;
            }
            if (!arrayList4.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList4), arrayList2);
            }
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
            }
            broadcastReplyMessages(arrayList, longSparseArray, arrayList2, arrayList3, j, true);
            if (longSparseArray2.isEmpty()) {
                if (runnable == null) {
                    return;
                }
                AndroidUtilities.runOnUIThread(runnable);
                return;
            }
            int size2 = longSparseArray2.size();
            int i8 = 0;
            while (i8 < size2) {
                final long keyAt2 = longSparseArray2.keyAt(i8);
                if (z) {
                    TLRPC$TL_messages_getScheduledMessages tLRPC$TL_messages_getScheduledMessages = new TLRPC$TL_messages_getScheduledMessages();
                    tLRPC$TL_messages_getScheduledMessages.peer = getMessagesController().getInputPeer(j);
                    tLRPC$TL_messages_getScheduledMessages.id = (ArrayList) longSparseArray2.valueAt(i8);
                    i = size2;
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_getScheduledMessages, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda160
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MediaDataController.this.lambda$loadReplyMessagesForMessages$136(j, keyAt2, longSparseArray, z, runnable, tLObject, tLRPC$TL_error);
                        }
                    });
                } else {
                    i = size2;
                    if (keyAt2 != 0) {
                        TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
                        tLRPC$TL_channels_getMessages.channel = getMessagesController().getInputChannel(keyAt2);
                        tLRPC$TL_channels_getMessages.id = (ArrayList) longSparseArray2.valueAt(i8);
                        getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda161
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                MediaDataController.this.lambda$loadReplyMessagesForMessages$137(j, keyAt2, longSparseArray, z, runnable, tLObject, tLRPC$TL_error);
                            }
                        });
                    } else {
                        TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages = new TLRPC$TL_messages_getMessages();
                        tLRPC$TL_messages_getMessages.id = (ArrayList) longSparseArray2.valueAt(i8);
                        getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda163
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                MediaDataController.this.lambda$loadReplyMessagesForMessages$138(j, longSparseArray, z, runnable, tLObject, tLRPC$TL_error);
                            }
                        });
                    }
                }
                i8++;
                size2 = i;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$136(long j, long j2, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            for (int i = 0; i < tLRPC$messages_Messages.messages.size(); i++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i);
                if (tLRPC$Message.dialog_id == 0) {
                    tLRPC$Message.dialog_id = j;
                }
            }
            MessageObject.fixMessagePeer(tLRPC$messages_Messages.messages, j2);
            ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages.messages);
            broadcastReplyMessages(tLRPC$messages_Messages.messages, longSparseArray, tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            saveReplyMessages(longSparseArray, tLRPC$messages_Messages.messages, z);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$137(long j, long j2, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            for (int i = 0; i < tLRPC$messages_Messages.messages.size(); i++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i);
                if (tLRPC$Message.dialog_id == 0) {
                    tLRPC$Message.dialog_id = j;
                }
            }
            MessageObject.fixMessagePeer(tLRPC$messages_Messages.messages, j2);
            ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages.messages);
            broadcastReplyMessages(tLRPC$messages_Messages.messages, longSparseArray, tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            saveReplyMessages(longSparseArray, tLRPC$messages_Messages.messages, z);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$138(long j, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            for (int i = 0; i < tLRPC$messages_Messages.messages.size(); i++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i);
                if (tLRPC$Message.dialog_id == 0) {
                    tLRPC$Message.dialog_id = j;
                }
            }
            ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages.messages);
            broadcastReplyMessages(tLRPC$messages_Messages.messages, longSparseArray, tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            saveReplyMessages(longSparseArray, tLRPC$messages_Messages.messages, z);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    private void saveReplyMessages(final LongSparseArray<SparseArray<ArrayList<MessageObject>>> longSparseArray, final ArrayList<TLRPC$Message> arrayList, final boolean z) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda120
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$saveReplyMessages$140(z, arrayList, longSparseArray);
            }
        });
    }

    public /* synthetic */ void lambda$saveReplyMessages$140(boolean z, ArrayList arrayList, LongSparseArray longSparseArray) {
        SQLitePreparedStatement sQLitePreparedStatement;
        ArrayList arrayList2;
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            if (z) {
                sQLitePreparedStatement = getMessagesStorage().getDatabase().executeFast("UPDATE scheduled_messages_v2 SET replydata = ?, reply_to_message_id = ? WHERE mid = ? AND uid = ?");
            } else {
                sQLitePreparedStatement = getMessagesStorage().getDatabase().executeFast("UPDATE messages_v2 SET replydata = ?, reply_to_message_id = ? WHERE mid = ? AND uid = ?");
            }
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i);
                SparseArray sparseArray = (SparseArray) longSparseArray.get(MessageObject.getDialogId(tLRPC$Message));
                if (sparseArray != null && (arrayList2 = (ArrayList) sparseArray.get(tLRPC$Message.id)) != null) {
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                    tLRPC$Message.serializeToStream(nativeByteBuffer);
                    for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                        MessageObject messageObject = (MessageObject) arrayList2.get(i2);
                        sQLitePreparedStatement.requery();
                        sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                        sQLitePreparedStatement.bindInteger(2, tLRPC$Message.id);
                        sQLitePreparedStatement.bindInteger(3, messageObject.getId());
                        sQLitePreparedStatement.bindLong(4, messageObject.getDialogId());
                        sQLitePreparedStatement.step();
                    }
                    nativeByteBuffer.reuse();
                }
            }
            sQLitePreparedStatement.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void broadcastReplyMessages(ArrayList<TLRPC$Message> arrayList, final LongSparseArray<SparseArray<ArrayList<MessageObject>>> longSparseArray, final ArrayList<TLRPC$User> arrayList2, final ArrayList<TLRPC$Chat> arrayList3, final long j, final boolean z) {
        LongSparseArray longSparseArray2 = new LongSparseArray();
        for (int i = 0; i < arrayList2.size(); i++) {
            TLRPC$User tLRPC$User = arrayList2.get(i);
            longSparseArray2.put(tLRPC$User.id, tLRPC$User);
        }
        LongSparseArray longSparseArray3 = new LongSparseArray();
        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
            TLRPC$Chat tLRPC$Chat = arrayList3.get(i2);
            longSparseArray3.put(tLRPC$Chat.id, tLRPC$Chat);
        }
        final ArrayList arrayList4 = new ArrayList();
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            arrayList4.add(new MessageObject(this.currentAccount, arrayList.get(i3), (LongSparseArray<TLRPC$User>) longSparseArray2, (LongSparseArray<TLRPC$Chat>) longSparseArray3, false, false));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda74
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$broadcastReplyMessages$141(arrayList2, z, arrayList3, arrayList4, longSparseArray, j);
            }
        });
    }

    public /* synthetic */ void lambda$broadcastReplyMessages$141(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray, long j) {
        ArrayList arrayList4;
        getMessagesController().putUsers(arrayList, z);
        getMessagesController().putChats(arrayList2, z);
        int size = arrayList3.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            MessageObject messageObject = (MessageObject) arrayList3.get(i);
            SparseArray sparseArray = (SparseArray) longSparseArray.get(messageObject.getDialogId());
            if (sparseArray != null && (arrayList4 = (ArrayList) sparseArray.get(messageObject.getId())) != null) {
                for (int i2 = 0; i2 < arrayList4.size(); i2++) {
                    MessageObject messageObject2 = (MessageObject) arrayList4.get(i2);
                    messageObject2.replyMessageObject = messageObject;
                    TLRPC$MessageAction tLRPC$MessageAction = messageObject2.messageOwner.action;
                    if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPinMessage) {
                        messageObject2.generatePinMessageText(null, null);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionGameScore) {
                        messageObject2.generateGameMessageText(null);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPaymentSent) {
                        messageObject2.generatePaymentSentMessageText(null);
                    }
                }
                z2 = true;
            }
        }
        if (z2) {
            getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j), arrayList3, longSparseArray);
        }
    }

    public static void sortEntities(ArrayList<TLRPC$MessageEntity> arrayList) {
        Collections.sort(arrayList, entityComparator);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0027 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0029 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean checkInclusion(int r5, java.util.List<org.telegram.tgnet.TLRPC$MessageEntity> r6, boolean r7) {
        /*
            r0 = 0
            if (r6 == 0) goto L2c
            boolean r1 = r6.isEmpty()
            if (r1 == 0) goto La
            goto L2c
        La:
            int r1 = r6.size()
            r2 = 0
        Lf:
            if (r2 >= r1) goto L2c
            java.lang.Object r3 = r6.get(r2)
            org.telegram.tgnet.TLRPC$MessageEntity r3 = (org.telegram.tgnet.TLRPC$MessageEntity) r3
            int r4 = r3.offset
            if (r7 == 0) goto L1e
            if (r4 >= r5) goto L29
            goto L20
        L1e:
            if (r4 > r5) goto L29
        L20:
            int r4 = r3.offset
            int r3 = r3.length
            int r4 = r4 + r3
            if (r4 <= r5) goto L29
            r5 = 1
            return r5
        L29:
            int r2 = r2 + 1
            goto Lf
        L2c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.checkInclusion(int, java.util.List, boolean):boolean");
    }

    private static boolean checkIntersection(int i, int i2, List<TLRPC$MessageEntity> list) {
        if (list != null && !list.isEmpty()) {
            int size = list.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$MessageEntity tLRPC$MessageEntity = list.get(i3);
                int i4 = tLRPC$MessageEntity.offset;
                if (i4 > i && i4 + tLRPC$MessageEntity.length <= i2) {
                    return true;
                }
            }
        }
        return false;
    }

    public CharSequence substring(CharSequence charSequence, int i, int i2) {
        if (charSequence instanceof SpannableStringBuilder) {
            return charSequence.subSequence(i, i2);
        }
        if (charSequence instanceof SpannedString) {
            return charSequence.subSequence(i, i2);
        }
        return TextUtils.substring(charSequence, i, i2);
    }

    private static CharacterStyle createNewSpan(CharacterStyle characterStyle, TextStyleSpan.TextStyleRun textStyleRun, TextStyleSpan.TextStyleRun textStyleRun2, boolean z) {
        TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun(textStyleRun);
        if (textStyleRun2 != null) {
            if (z) {
                textStyleRun3.merge(textStyleRun2);
            } else {
                textStyleRun3.replace(textStyleRun2);
            }
        }
        if (characterStyle instanceof TextStyleSpan) {
            return new TextStyleSpan(textStyleRun3);
        }
        if (!(characterStyle instanceof URLSpanReplacement)) {
            return null;
        }
        return new URLSpanReplacement(((URLSpanReplacement) characterStyle).getURL(), textStyleRun3);
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x008a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void addStyleToText(org.telegram.ui.Components.TextStyleSpan r11, int r12, int r13, android.text.Spannable r14, boolean r15) {
        /*
            java.lang.Class<android.text.style.CharacterStyle> r0 = android.text.style.CharacterStyle.class
            java.lang.Object[] r0 = r14.getSpans(r12, r13, r0)     // Catch: java.lang.Exception -> Lc0
            android.text.style.CharacterStyle[] r0 = (android.text.style.CharacterStyle[]) r0     // Catch: java.lang.Exception -> Lc0
            r1 = 33
            if (r0 == 0) goto Laa
            int r2 = r0.length     // Catch: java.lang.Exception -> Lc0
            if (r2 <= 0) goto Laa
            r2 = 0
        L10:
            int r3 = r0.length     // Catch: java.lang.Exception -> Lc0
            if (r2 >= r3) goto Laa
            r3 = r0[r2]     // Catch: java.lang.Exception -> Lc0
            if (r11 == 0) goto L1c
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = r11.getTextStyleRun()     // Catch: java.lang.Exception -> Lc0
            goto L21
        L1c:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch: java.lang.Exception -> Lc0
            r4.<init>()     // Catch: java.lang.Exception -> Lc0
        L21:
            boolean r5 = r3 instanceof org.telegram.ui.Components.TextStyleSpan     // Catch: java.lang.Exception -> Lc0
            if (r5 == 0) goto L2d
            r5 = r3
            org.telegram.ui.Components.TextStyleSpan r5 = (org.telegram.ui.Components.TextStyleSpan) r5     // Catch: java.lang.Exception -> Lc0
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = r5.getTextStyleRun()     // Catch: java.lang.Exception -> Lc0
            goto L3f
        L2d:
            boolean r5 = r3 instanceof org.telegram.ui.Components.URLSpanReplacement     // Catch: java.lang.Exception -> Lc0
            if (r5 == 0) goto La6
            r5 = r3
            org.telegram.ui.Components.URLSpanReplacement r5 = (org.telegram.ui.Components.URLSpanReplacement) r5     // Catch: java.lang.Exception -> Lc0
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = r5.getTextStyleRun()     // Catch: java.lang.Exception -> Lc0
            if (r5 != 0) goto L3f
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch: java.lang.Exception -> Lc0
            r5.<init>()     // Catch: java.lang.Exception -> Lc0
        L3f:
            if (r5 != 0) goto L43
            goto La6
        L43:
            int r6 = r14.getSpanStart(r3)     // Catch: java.lang.Exception -> Lc0
            int r7 = r14.getSpanEnd(r3)     // Catch: java.lang.Exception -> Lc0
            r14.removeSpan(r3)     // Catch: java.lang.Exception -> Lc0
            if (r6 <= r12) goto L6a
            if (r13 <= r7) goto L6a
            android.text.style.CharacterStyle r3 = createNewSpan(r3, r5, r4, r15)     // Catch: java.lang.Exception -> Lc0
            r14.setSpan(r3, r6, r7, r1)     // Catch: java.lang.Exception -> Lc0
            if (r11 == 0) goto L68
            org.telegram.ui.Components.TextStyleSpan r3 = new org.telegram.ui.Components.TextStyleSpan     // Catch: java.lang.Exception -> Lc0
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch: java.lang.Exception -> Lc0
            r5.<init>(r4)     // Catch: java.lang.Exception -> Lc0
            r3.<init>(r5)     // Catch: java.lang.Exception -> Lc0
            r14.setSpan(r3, r7, r13, r1)     // Catch: java.lang.Exception -> Lc0
        L68:
            r13 = r6
            goto La6
        L6a:
            r8 = 0
            if (r6 > r12) goto L87
            if (r6 == r12) goto L76
            android.text.style.CharacterStyle r9 = createNewSpan(r3, r5, r8, r15)     // Catch: java.lang.Exception -> Lc0
            r14.setSpan(r9, r6, r12, r1)     // Catch: java.lang.Exception -> Lc0
        L76:
            if (r7 <= r12) goto L87
            if (r11 == 0) goto L85
            android.text.style.CharacterStyle r9 = createNewSpan(r3, r5, r4, r15)     // Catch: java.lang.Exception -> Lc0
            int r10 = java.lang.Math.min(r7, r13)     // Catch: java.lang.Exception -> Lc0
            r14.setSpan(r9, r12, r10, r1)     // Catch: java.lang.Exception -> Lc0
        L85:
            r9 = r7
            goto L88
        L87:
            r9 = r12
        L88:
            if (r7 < r13) goto La5
            if (r7 == r13) goto L93
            android.text.style.CharacterStyle r8 = createNewSpan(r3, r5, r8, r15)     // Catch: java.lang.Exception -> Lc0
            r14.setSpan(r8, r13, r7, r1)     // Catch: java.lang.Exception -> Lc0
        L93:
            if (r13 <= r6) goto La5
            if (r7 > r12) goto La5
            if (r11 == 0) goto La4
            android.text.style.CharacterStyle r12 = createNewSpan(r3, r5, r4, r15)     // Catch: java.lang.Exception -> Lc0
            int r13 = java.lang.Math.min(r7, r13)     // Catch: java.lang.Exception -> Lc0
            r14.setSpan(r12, r6, r13, r1)     // Catch: java.lang.Exception -> Lc0
        La4:
            r13 = r6
        La5:
            r12 = r9
        La6:
            int r2 = r2 + 1
            goto L10
        Laa:
            if (r11 == 0) goto Lc4
            if (r12 >= r13) goto Lc4
            int r15 = r14.length()     // Catch: java.lang.Exception -> Lc0
            if (r12 >= r15) goto Lc4
            int r15 = r14.length()     // Catch: java.lang.Exception -> Lc0
            int r13 = java.lang.Math.min(r15, r13)     // Catch: java.lang.Exception -> Lc0
            r14.setSpan(r11, r12, r13, r1)     // Catch: java.lang.Exception -> Lc0
            goto Lc4
        Lc0:
            r11 = move-exception
            org.telegram.messenger.FileLog.e(r11)
        Lc4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.addStyleToText(org.telegram.ui.Components.TextStyleSpan, int, int, android.text.Spannable, boolean):void");
    }

    public static void addTextStyleRuns(MessageObject messageObject, Spannable spannable) {
        addTextStyleRuns(messageObject.messageOwner.entities, messageObject.messageText, spannable, -1);
    }

    public static void addTextStyleRuns(TLRPC$DraftMessage tLRPC$DraftMessage, Spannable spannable, int i) {
        addTextStyleRuns(tLRPC$DraftMessage.entities, tLRPC$DraftMessage.message, spannable, i);
    }

    public static void addTextStyleRuns(MessageObject messageObject, Spannable spannable, int i) {
        addTextStyleRuns(messageObject.messageOwner.entities, messageObject.messageText, spannable, i);
    }

    public static void addTextStyleRuns(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, Spannable spannable) {
        addTextStyleRuns(arrayList, charSequence, spannable, -1);
    }

    public static void addTextStyleRuns(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, Spannable spannable, int i) {
        for (TextStyleSpan textStyleSpan : (TextStyleSpan[]) spannable.getSpans(0, spannable.length(), TextStyleSpan.class)) {
            spannable.removeSpan(textStyleSpan);
        }
        Iterator<TextStyleSpan.TextStyleRun> it = getTextStyleRuns(arrayList, charSequence, i).iterator();
        while (it.hasNext()) {
            TextStyleSpan.TextStyleRun next = it.next();
            addStyleToText(new TextStyleSpan(next), next.start, next.end, spannable, true);
        }
    }

    public static void addAnimatedEmojiSpans(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt) {
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        if (!(charSequence instanceof Spannable) || arrayList == null) {
            return;
        }
        Spannable spannable = (Spannable) charSequence;
        for (AnimatedEmojiSpan animatedEmojiSpan : (AnimatedEmojiSpan[]) spannable.getSpans(0, spannable.length(), AnimatedEmojiSpan.class)) {
            if (animatedEmojiSpan != null) {
                spannable.removeSpan(animatedEmojiSpan);
            }
        }
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$MessageEntity tLRPC$MessageEntity = arrayList.get(i);
            if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCustomEmoji) {
                TLRPC$TL_messageEntityCustomEmoji tLRPC$TL_messageEntityCustomEmoji = (TLRPC$TL_messageEntityCustomEmoji) tLRPC$MessageEntity;
                int i2 = tLRPC$MessageEntity.offset;
                int i3 = tLRPC$MessageEntity.length + i2;
                if (i2 < i3 && i3 <= spannable.length()) {
                    spannable.setSpan(new AnimatedEmojiSpan(tLRPC$TL_messageEntityCustomEmoji.document_id, fontMetricsInt), i2, i3, 33);
                }
            }
        }
    }

    public static ArrayList<TextStyleSpan.TextStyleRun> getTextStyleRuns(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, int i) {
        int i2;
        ArrayList<TextStyleSpan.TextStyleRun> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList(arrayList);
        Collections.sort(arrayList3, MediaDataController$$ExternalSyntheticLambda132.INSTANCE);
        int size = arrayList3.size();
        for (int i3 = 0; i3 < size; i3++) {
            TLRPC$MessageEntity tLRPC$MessageEntity = (TLRPC$MessageEntity) arrayList3.get(i3);
            if (tLRPC$MessageEntity != null && tLRPC$MessageEntity.length > 0 && (i2 = tLRPC$MessageEntity.offset) >= 0 && i2 < charSequence.length()) {
                if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length > charSequence.length()) {
                    tLRPC$MessageEntity.length = charSequence.length() - tLRPC$MessageEntity.offset;
                }
                if (!(tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCustomEmoji)) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    int i4 = tLRPC$MessageEntity.offset;
                    textStyleRun.start = i4;
                    textStyleRun.end = i4 + tLRPC$MessageEntity.length;
                    if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntitySpoiler) {
                        textStyleRun.flags = 256;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityStrike) {
                        textStyleRun.flags = 8;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityUnderline) {
                        textStyleRun.flags = 16;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBlockquote) {
                        textStyleRun.flags = 32;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBold) {
                        textStyleRun.flags = 1;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityItalic) {
                        textStyleRun.flags = 2;
                    } else if ((tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCode) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityPre)) {
                        textStyleRun.flags = 4;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityMentionName) {
                        textStyleRun.flags = 64;
                        textStyleRun.urlEntity = tLRPC$MessageEntity;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_inputMessageEntityMentionName) {
                        textStyleRun.flags = 64;
                        textStyleRun.urlEntity = tLRPC$MessageEntity;
                    } else {
                        textStyleRun.flags = ConnectionsManager.RequestFlagNeedQuickAck;
                        textStyleRun.urlEntity = tLRPC$MessageEntity;
                    }
                    textStyleRun.flags &= i;
                    int size2 = arrayList2.size();
                    int i5 = 0;
                    while (i5 < size2) {
                        TextStyleSpan.TextStyleRun textStyleRun2 = arrayList2.get(i5);
                        int i6 = textStyleRun.start;
                        int i7 = textStyleRun2.start;
                        if (i6 > i7) {
                            int i8 = textStyleRun2.end;
                            if (i6 < i8) {
                                if (textStyleRun.end < i8) {
                                    TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                    textStyleRun3.merge(textStyleRun2);
                                    int i9 = i5 + 1;
                                    arrayList2.add(i9, textStyleRun3);
                                    TextStyleSpan.TextStyleRun textStyleRun4 = new TextStyleSpan.TextStyleRun(textStyleRun2);
                                    textStyleRun4.start = textStyleRun.end;
                                    i5 = i9 + 1;
                                    size2 = size2 + 1 + 1;
                                    arrayList2.add(i5, textStyleRun4);
                                } else {
                                    TextStyleSpan.TextStyleRun textStyleRun5 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                    textStyleRun5.merge(textStyleRun2);
                                    textStyleRun5.end = textStyleRun2.end;
                                    i5++;
                                    size2++;
                                    arrayList2.add(i5, textStyleRun5);
                                }
                                int i10 = textStyleRun.start;
                                textStyleRun.start = textStyleRun2.end;
                                textStyleRun2.end = i10;
                            }
                        } else {
                            int i11 = textStyleRun.end;
                            if (i7 < i11) {
                                int i12 = textStyleRun2.end;
                                if (i11 == i12) {
                                    textStyleRun2.merge(textStyleRun);
                                } else if (i11 < i12) {
                                    TextStyleSpan.TextStyleRun textStyleRun6 = new TextStyleSpan.TextStyleRun(textStyleRun2);
                                    textStyleRun6.merge(textStyleRun);
                                    textStyleRun6.end = textStyleRun.end;
                                    i5++;
                                    size2++;
                                    arrayList2.add(i5, textStyleRun6);
                                    textStyleRun2.start = textStyleRun.end;
                                } else {
                                    TextStyleSpan.TextStyleRun textStyleRun7 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                    textStyleRun7.start = textStyleRun2.end;
                                    i5++;
                                    size2++;
                                    arrayList2.add(i5, textStyleRun7);
                                    textStyleRun2.merge(textStyleRun);
                                }
                                textStyleRun.end = i7;
                            }
                        }
                        i5++;
                    }
                    if (textStyleRun.start < textStyleRun.end) {
                        arrayList2.add(textStyleRun);
                    }
                }
            }
        }
        return arrayList2;
    }

    public static /* synthetic */ int lambda$getTextStyleRuns$142(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
        int i = tLRPC$MessageEntity.offset;
        int i2 = tLRPC$MessageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void addStyle(int i, int i2, int i3, ArrayList<TLRPC$MessageEntity> arrayList) {
        if ((i & 256) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntitySpoiler(), i2, i3));
        }
        if ((i & 1) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityBold(), i2, i3));
        }
        if ((i & 2) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityItalic(), i2, i3));
        }
        if ((i & 4) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityCode(), i2, i3));
        }
        if ((i & 8) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityStrike(), i2, i3));
        }
        if ((i & 16) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityUnderline(), i2, i3));
        }
        if ((i & 32) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityBlockquote(), i2, i3));
        }
    }

    private TLRPC$MessageEntity setEntityStartEnd(TLRPC$MessageEntity tLRPC$MessageEntity, int i, int i2) {
        tLRPC$MessageEntity.offset = i;
        tLRPC$MessageEntity.length = i2 - i;
        return tLRPC$MessageEntity;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0052, code lost:
        if (r0 != null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0054, code lost:
        r0 = new java.util.ArrayList<>();
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0059, code lost:
        if (r4 == false) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x005b, code lost:
        r12 = 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x005d, code lost:
        r12 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x005e, code lost:
        r12 = r12 + r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0065, code lost:
        if (r12 >= r19[0].length()) goto L179;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x006d, code lost:
        if (r19[0].charAt(r12) != '`') goto L180;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x006f, code lost:
        r5 = r5 + 1;
        r12 = r12 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0074, code lost:
        if (r4 == false) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0076, code lost:
        r10 = 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0078, code lost:
        r10 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0079, code lost:
        r10 = r10 + r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x007a, code lost:
        if (r4 == false) goto L70;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x007c, code lost:
        if (r6 <= 0) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x007e, code lost:
        r4 = r19[0].charAt(r6 - 1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0087, code lost:
        r4 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x008a, code lost:
        if (r4 == ' ') goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x008c, code lost:
        if (r4 != '\n') goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x008f, code lost:
        r4 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0091, code lost:
        r4 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0092, code lost:
        r13 = substring(r19[0], 0, r6 - r4);
        r14 = substring(r19[0], r6 + 3, r5);
        r15 = r5 + 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00aa, code lost:
        if (r15 >= r19[0].length()) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00ac, code lost:
        r3 = r19[0].charAt(r15);
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00b3, code lost:
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00b4, code lost:
        r11 = r19[0];
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00b6, code lost:
        if (r3 == ' ') goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00b8, code lost:
        if (r3 != '\n') goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00bb, code lost:
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00bd, code lost:
        r3 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00be, code lost:
        r3 = substring(r11, r15 + r3, r19[0].length());
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00cf, code lost:
        if (r13.length() == 0) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x00d1, code lost:
        r13 = org.telegram.messenger.AndroidUtilities.concat(r13, "\n");
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00dc, code lost:
        r4 = 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x00e1, code lost:
        if (r3.length() == 0) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00e3, code lost:
        r3 = org.telegram.messenger.AndroidUtilities.concat("\n", r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x00f1, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L173;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x00f3, code lost:
        r19[0] = org.telegram.messenger.AndroidUtilities.concat(r13, r14, r3);
        r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityPre();
        r3.offset = (r4 ^ 1) + r6;
        r3.length = ((r5 - r6) - 3) + (r4 ^ 1);
        r3.language = "";
        r0.add(r3);
        r10 = r10 - 6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x011e, code lost:
        r3 = r6 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0120, code lost:
        if (r3 == r5) goto L175;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0122, code lost:
        r19[0] = org.telegram.messenger.AndroidUtilities.concat(substring(r19[0], 0, r6), substring(r19[0], r3, r5), substring(r19[0], r5 + 1, r19[0].length()));
        r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode();
        r3.offset = r6;
        r3.length = (r5 - r6) - 1;
        r0.add(r3);
        r10 = r10 - 2;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> getEntities(java.lang.CharSequence[] r19, boolean r20) {
        /*
            Method dump skipped, instructions count: 923
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getEntities(java.lang.CharSequence[], boolean):java.util.ArrayList");
    }

    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$143(Void r0) {
        return new TLRPC$TL_messageEntityBold();
    }

    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$144(Void r0) {
        return new TLRPC$TL_messageEntityItalic();
    }

    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$145(Void r0) {
        return new TLRPC$TL_messageEntitySpoiler();
    }

    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$146(Void r0) {
        return new TLRPC$TL_messageEntityStrike();
    }

    private CharSequence parsePattern(CharSequence charSequence, Pattern pattern, List<TLRPC$MessageEntity> list, GenericProvider<Void, TLRPC$MessageEntity> genericProvider) {
        URLSpan[] uRLSpanArr;
        Matcher matcher = pattern.matcher(charSequence);
        int i = 0;
        while (matcher.find()) {
            boolean z = true;
            String group = matcher.group(1);
            if ((charSequence instanceof Spannable) && (uRLSpanArr = (URLSpan[]) ((Spannable) charSequence).getSpans(matcher.start() - i, matcher.end() - i, URLSpan.class)) != null && uRLSpanArr.length > 0) {
                z = false;
            }
            if (z) {
                charSequence = ((Object) charSequence.subSequence(0, matcher.start() - i)) + group + ((Object) charSequence.subSequence(matcher.end() - i, charSequence.length()));
                TLRPC$MessageEntity provide = genericProvider.provide(null);
                provide.offset = matcher.start() - i;
                provide.length = group.length();
                list.add(provide);
            }
            i += (matcher.end() - matcher.start()) - group.length();
        }
        return charSequence;
    }

    public void loadDraftsIfNeed() {
        if (getUserConfig().draftsLoaded || this.loadingDrafts) {
            return;
        }
        this.loadingDrafts = true;
        getConnectionsManager().sendRequest(new TLObject() { // from class: org.telegram.tgnet.TLRPC$TL_messages_getAllDrafts
            public static int constructor = 1782549861;

            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
                return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                abstractSerializedData.writeInt32(constructor);
            }
        }, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda146
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$loadDraftsIfNeed$149(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadDraftsIfNeed$147() {
        this.loadingDrafts = false;
    }

    public /* synthetic */ void lambda$loadDraftsIfNeed$149(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadDraftsIfNeed$147();
                }
            });
            return;
        }
        getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadDraftsIfNeed$148();
            }
        });
    }

    public /* synthetic */ void lambda$loadDraftsIfNeed$148() {
        this.loadingDrafts = false;
        UserConfig userConfig = getUserConfig();
        userConfig.draftsLoaded = true;
        userConfig.saveConfig(false);
    }

    public int getDraftFolderId(long j) {
        return this.draftsFolderIds.get(j, 0).intValue();
    }

    public void setDraftFolderId(long j, int i) {
        this.draftsFolderIds.put(j, Integer.valueOf(i));
    }

    public void clearDraftsFolderIds() {
        this.draftsFolderIds.clear();
    }

    public LongSparseArray<SparseArray<TLRPC$DraftMessage>> getDrafts() {
        return this.drafts;
    }

    public TLRPC$DraftMessage getDraft(long j, int i) {
        SparseArray<TLRPC$DraftMessage> sparseArray = this.drafts.get(j);
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(i);
    }

    public TLRPC$Message getDraftMessage(long j, int i) {
        SparseArray<TLRPC$Message> sparseArray = this.draftMessages.get(j);
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(i);
    }

    public void saveDraft(long j, int i, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$Message tLRPC$Message, boolean z) {
        saveDraft(j, i, charSequence, arrayList, tLRPC$Message, z, false);
    }

    public void saveDraft(long j, int i, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$Message tLRPC$Message, boolean z, boolean z2) {
        TLRPC$DraftMessage tLRPC$DraftMessage;
        if (!TextUtils.isEmpty(charSequence) || tLRPC$Message != null) {
            tLRPC$DraftMessage = new TLRPC$TL_draftMessage();
        } else {
            tLRPC$DraftMessage = new TLRPC$TL_draftMessageEmpty();
        }
        TLRPC$DraftMessage tLRPC$DraftMessage2 = tLRPC$DraftMessage;
        tLRPC$DraftMessage2.date = (int) (System.currentTimeMillis() / 1000);
        tLRPC$DraftMessage2.message = charSequence == null ? "" : charSequence.toString();
        tLRPC$DraftMessage2.no_webpage = z;
        if (tLRPC$Message != null) {
            tLRPC$DraftMessage2.reply_to_msg_id = tLRPC$Message.id;
            tLRPC$DraftMessage2.flags |= 1;
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            tLRPC$DraftMessage2.entities = arrayList;
            tLRPC$DraftMessage2.flags |= 8;
        }
        SparseArray<TLRPC$DraftMessage> sparseArray = this.drafts.get(j);
        TLRPC$DraftMessage tLRPC$DraftMessage3 = sparseArray == null ? null : sparseArray.get(i);
        if (!z2) {
            if (tLRPC$DraftMessage3 != null && tLRPC$DraftMessage3.message.equals(tLRPC$DraftMessage2.message) && tLRPC$DraftMessage3.reply_to_msg_id == tLRPC$DraftMessage2.reply_to_msg_id && tLRPC$DraftMessage3.no_webpage == tLRPC$DraftMessage2.no_webpage) {
                return;
            }
            if (tLRPC$DraftMessage3 == null && TextUtils.isEmpty(tLRPC$DraftMessage2.message) && tLRPC$DraftMessage2.reply_to_msg_id == 0) {
                return;
            }
        }
        saveDraft(j, i, tLRPC$DraftMessage2, tLRPC$Message, false);
        if (i == 0) {
            if (!DialogObject.isEncryptedDialog(j)) {
                TLRPC$TL_messages_saveDraft tLRPC$TL_messages_saveDraft = new TLRPC$TL_messages_saveDraft();
                TLRPC$InputPeer inputPeer = getMessagesController().getInputPeer(j);
                tLRPC$TL_messages_saveDraft.peer = inputPeer;
                if (inputPeer == null) {
                    return;
                }
                tLRPC$TL_messages_saveDraft.message = tLRPC$DraftMessage2.message;
                tLRPC$TL_messages_saveDraft.no_webpage = tLRPC$DraftMessage2.no_webpage;
                tLRPC$TL_messages_saveDraft.reply_to_msg_id = tLRPC$DraftMessage2.reply_to_msg_id;
                tLRPC$TL_messages_saveDraft.entities = tLRPC$DraftMessage2.entities;
                tLRPC$TL_messages_saveDraft.flags = tLRPC$DraftMessage2.flags;
                getConnectionsManager().sendRequest(tLRPC$TL_messages_saveDraft, MediaDataController$$ExternalSyntheticLambda183.INSTANCE);
            }
            getMessagesController().sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void saveDraft(final long j, final int i, TLRPC$DraftMessage tLRPC$DraftMessage, TLRPC$Message tLRPC$Message, boolean z) {
        TLRPC$Chat tLRPC$Chat;
        StringBuilder sb;
        String str;
        SharedPreferences.Editor edit = this.draftPreferences.edit();
        MessagesController messagesController = getMessagesController();
        if (tLRPC$DraftMessage == null || (tLRPC$DraftMessage instanceof TLRPC$TL_draftMessageEmpty)) {
            SparseArray<TLRPC$DraftMessage> sparseArray = this.drafts.get(j);
            if (sparseArray != null) {
                sparseArray.remove(i);
                if (sparseArray.size() == 0) {
                    this.drafts.remove(j);
                }
            }
            SparseArray<TLRPC$Message> sparseArray2 = this.draftMessages.get(j);
            if (sparseArray2 != null) {
                sparseArray2.remove(i);
                if (sparseArray2.size() == 0) {
                    this.draftMessages.remove(j);
                }
            }
            if (i == 0) {
                this.draftPreferences.edit().remove("" + j).remove("r_" + j).commit();
            } else {
                this.draftPreferences.edit().remove("t_" + j + "_" + i).remove("rt_" + j + "_" + i).commit();
            }
            messagesController.removeDraftDialogIfNeed(j);
        } else {
            SparseArray<TLRPC$DraftMessage> sparseArray3 = this.drafts.get(j);
            if (sparseArray3 == null) {
                sparseArray3 = new SparseArray<>();
                this.drafts.put(j, sparseArray3);
            }
            sparseArray3.put(i, tLRPC$DraftMessage);
            if (i == 0) {
                messagesController.putDraftDialogIfNeed(j, tLRPC$DraftMessage);
            }
            try {
                SerializedData serializedData = new SerializedData(tLRPC$DraftMessage.getObjectSize());
                tLRPC$DraftMessage.serializeToStream(serializedData);
                if (i == 0) {
                    str = "" + j;
                } else {
                    str = "t_" + j + "_" + i;
                }
                edit.putString(str, Utilities.bytesToHex(serializedData.toByteArray()));
                serializedData.cleanup();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        SparseArray<TLRPC$Message> sparseArray4 = this.draftMessages.get(j);
        if (tLRPC$Message == null) {
            if (sparseArray4 != null) {
                sparseArray4.remove(i);
                if (sparseArray4.size() == 0) {
                    this.draftMessages.remove(j);
                }
            }
            if (i == 0) {
                edit.remove("r_" + j);
            } else {
                edit.remove("rt_" + j + "_" + i);
            }
        } else {
            if (sparseArray4 == null) {
                sparseArray4 = new SparseArray<>();
                this.draftMessages.put(j, sparseArray4);
            }
            sparseArray4.put(i, tLRPC$Message);
            SerializedData serializedData2 = new SerializedData(tLRPC$Message.getObjectSize());
            tLRPC$Message.serializeToStream(serializedData2);
            if (i == 0) {
                sb = new StringBuilder();
                sb.append("r_");
                sb.append(j);
            } else {
                sb = new StringBuilder();
                sb.append("rt_");
                sb.append(j);
                sb.append("_");
                sb.append(i);
            }
            edit.putString(sb.toString(), Utilities.bytesToHex(serializedData2.toByteArray()));
            serializedData2.cleanup();
        }
        edit.commit();
        if (!z || i != 0) {
            return;
        }
        if (tLRPC$DraftMessage != null && tLRPC$DraftMessage.reply_to_msg_id != 0 && tLRPC$Message == null) {
            TLRPC$Chat tLRPC$Chat2 = null;
            if (DialogObject.isUserDialog(j)) {
                tLRPC$Chat2 = getMessagesController().getUser(Long.valueOf(j));
                tLRPC$Chat = tLRPC$Chat2;
            } else {
                tLRPC$Chat = getMessagesController().getChat(Long.valueOf(-j));
            }
            if (tLRPC$Chat2 != null || tLRPC$Chat != null) {
                final long j2 = ChatObject.isChannel(tLRPC$Chat) ? tLRPC$Chat.id : 0L;
                final int i2 = tLRPC$DraftMessage.reply_to_msg_id;
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda20
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.lambda$saveDraft$153(i2, j, j2, i);
                    }
                });
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
    }

    public /* synthetic */ void lambda$saveDraft$153(int i, final long j, long j2, final int i2) {
        NativeByteBuffer byteBufferValue;
        TLRPC$Message tLRPC$Message = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE mid = %d and uid = %d", Integer.valueOf(i), Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$Message = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                tLRPC$Message.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$Message != null) {
                saveDraftReplyMessage(j, i2, tLRPC$Message);
            } else if (j2 != 0) {
                TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
                tLRPC$TL_channels_getMessages.channel = getMessagesController().getInputChannel(j2);
                tLRPC$TL_channels_getMessages.id.add(Integer.valueOf(i));
                getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda156
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MediaDataController.this.lambda$saveDraft$151(j, i2, tLObject, tLRPC$TL_error);
                    }
                });
            } else {
                TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages = new TLRPC$TL_messages_getMessages();
                tLRPC$TL_messages_getMessages.id.add(Integer.valueOf(i));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda157
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        MediaDataController.this.lambda$saveDraft$152(j, i2, tLObject, tLRPC$TL_error);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$saveDraft$151(long j, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if (tLRPC$messages_Messages.messages.isEmpty()) {
                return;
            }
            saveDraftReplyMessage(j, i, tLRPC$messages_Messages.messages.get(0));
        }
    }

    public /* synthetic */ void lambda$saveDraft$152(long j, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if (tLRPC$messages_Messages.messages.isEmpty()) {
                return;
            }
            saveDraftReplyMessage(j, i, tLRPC$messages_Messages.messages.get(0));
        }
    }

    private void saveDraftReplyMessage(final long j, final int i, final TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$saveDraftReplyMessage$154(j, i, tLRPC$Message);
            }
        });
    }

    public /* synthetic */ void lambda$saveDraftReplyMessage$154(long j, int i, TLRPC$Message tLRPC$Message) {
        String str;
        SparseArray<TLRPC$DraftMessage> sparseArray = this.drafts.get(j);
        TLRPC$DraftMessage tLRPC$DraftMessage = sparseArray != null ? sparseArray.get(i) : null;
        if (tLRPC$DraftMessage == null || tLRPC$DraftMessage.reply_to_msg_id != tLRPC$Message.id) {
            return;
        }
        SparseArray<TLRPC$Message> sparseArray2 = this.draftMessages.get(j);
        if (sparseArray2 == null) {
            sparseArray2 = new SparseArray<>();
            this.draftMessages.put(j, sparseArray2);
        }
        sparseArray2.put(i, tLRPC$Message);
        SerializedData serializedData = new SerializedData(tLRPC$Message.getObjectSize());
        tLRPC$Message.serializeToStream(serializedData);
        SharedPreferences.Editor edit = this.draftPreferences.edit();
        if (i == 0) {
            str = "r_" + j;
        } else {
            str = "rt_" + j + "_" + i;
        }
        edit.putString(str, Utilities.bytesToHex(serializedData.toByteArray())).commit();
        getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
        serializedData.cleanup();
    }

    public void clearAllDrafts(boolean z) {
        this.drafts.clear();
        this.draftMessages.clear();
        this.draftsFolderIds.clear();
        this.draftPreferences.edit().clear().commit();
        if (z) {
            getMessagesController().sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void cleanDraft(long j, int i, boolean z) {
        SparseArray<TLRPC$DraftMessage> sparseArray = this.drafts.get(j);
        TLRPC$DraftMessage tLRPC$DraftMessage = sparseArray != null ? sparseArray.get(i) : null;
        if (tLRPC$DraftMessage == null) {
            return;
        }
        if (!z) {
            SparseArray<TLRPC$DraftMessage> sparseArray2 = this.drafts.get(j);
            if (sparseArray2 != null) {
                sparseArray2.remove(i);
                if (sparseArray2.size() == 0) {
                    this.drafts.remove(j);
                }
            }
            SparseArray<TLRPC$Message> sparseArray3 = this.draftMessages.get(j);
            if (sparseArray3 != null) {
                sparseArray3.remove(i);
                if (sparseArray3.size() == 0) {
                    this.draftMessages.remove(j);
                }
            }
            if (i == 0) {
                this.draftPreferences.edit().remove("" + j).remove("r_" + j).commit();
                getMessagesController().sortDialogs(null);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                return;
            }
            this.draftPreferences.edit().remove("t_" + j + "_" + i).remove("rt_" + j + "_" + i).commit();
        } else if (tLRPC$DraftMessage.reply_to_msg_id == 0) {
        } else {
            tLRPC$DraftMessage.reply_to_msg_id = 0;
            tLRPC$DraftMessage.flags &= -2;
            saveDraft(j, i, tLRPC$DraftMessage.message, tLRPC$DraftMessage.entities, null, tLRPC$DraftMessage.no_webpage, true);
        }
    }

    public void beginTransaction() {
        this.inTransaction = true;
    }

    public void endTransaction() {
        this.inTransaction = false;
    }

    public void clearBotKeyboard(final long j, final ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda67
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$clearBotKeyboard$155(arrayList, j);
            }
        });
    }

    public /* synthetic */ void lambda$clearBotKeyboard$155(ArrayList arrayList, long j) {
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                long j2 = this.botKeyboardsByMids.get(((Integer) arrayList.get(i)).intValue());
                if (j2 != 0) {
                    this.botKeyboards.remove(j2);
                    this.botKeyboardsByMids.delete(((Integer) arrayList.get(i)).intValue());
                    getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(j2));
                }
            }
            return;
        }
        this.botKeyboards.remove(j);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(j));
    }

    public void loadBotKeyboard(final long j) {
        TLRPC$Message tLRPC$Message = this.botKeyboards.get(j);
        if (tLRPC$Message != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, tLRPC$Message, Long.valueOf(j));
        } else {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadBotKeyboard$157(j);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadBotKeyboard$157(final long j) {
        NativeByteBuffer byteBufferValue;
        final TLRPC$Message tLRPC$Message = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next() && !queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$Message = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$Message == null) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda88
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadBotKeyboard$156(tLRPC$Message, j);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$loadBotKeyboard$156(TLRPC$Message tLRPC$Message, long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, tLRPC$Message, Long.valueOf(j));
    }

    private TLRPC$BotInfo loadBotInfoInternal(long j, long j2) throws SQLiteException {
        TLRPC$BotInfo tLRPC$BotInfo;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info_v2 WHERE uid = %d AND dialogId = %d", Long.valueOf(j), Long.valueOf(j2)), new Object[0]);
        if (!queryFinalized.next() || queryFinalized.isNull(0) || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
            tLRPC$BotInfo = null;
        } else {
            tLRPC$BotInfo = TLRPC$BotInfo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
            byteBufferValue.reuse();
        }
        queryFinalized.dispose();
        return tLRPC$BotInfo;
    }

    public void loadBotInfo(final long j, final long j2, boolean z, final int i) {
        if (z) {
            HashMap<String, TLRPC$BotInfo> hashMap = this.botInfos;
            TLRPC$BotInfo tLRPC$BotInfo = hashMap.get(j + "_" + j2);
            if (tLRPC$BotInfo != null) {
                getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, tLRPC$BotInfo, Integer.valueOf(i));
                return;
            }
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$loadBotInfo$159(j, j2, i);
            }
        });
    }

    public /* synthetic */ void lambda$loadBotInfo$159(long j, long j2, final int i) {
        try {
            final TLRPC$BotInfo loadBotInfoInternal = loadBotInfoInternal(j, j2);
            if (loadBotInfoInternal == null) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda84
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$loadBotInfo$158(loadBotInfoInternal, i);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$loadBotInfo$158(TLRPC$BotInfo tLRPC$BotInfo, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, tLRPC$BotInfo, Integer.valueOf(i));
    }

    public void putBotKeyboard(final long j, final TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message == null) {
            return;
        }
        try {
            int i = 0;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", Long.valueOf(j)), new Object[0]);
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            if (i >= tLRPC$Message.id) {
                return;
            }
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
            tLRPC$Message.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, tLRPC$Message.id);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda44
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$putBotKeyboard$160(j, tLRPC$Message);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$putBotKeyboard$160(long j, TLRPC$Message tLRPC$Message) {
        TLRPC$Message tLRPC$Message2 = this.botKeyboards.get(j);
        this.botKeyboards.put(j, tLRPC$Message);
        if (MessageObject.getChannelId(tLRPC$Message) == 0) {
            if (tLRPC$Message2 != null) {
                this.botKeyboardsByMids.delete(tLRPC$Message2.id);
            }
            this.botKeyboardsByMids.put(tLRPC$Message.id, j);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, tLRPC$Message, Long.valueOf(j));
    }

    public void putBotInfo(final long j, final TLRPC$BotInfo tLRPC$BotInfo) {
        if (tLRPC$BotInfo == null) {
            return;
        }
        HashMap<String, TLRPC$BotInfo> hashMap = this.botInfos;
        hashMap.put(tLRPC$BotInfo.user_id + "_" + j, tLRPC$BotInfo);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda85
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putBotInfo$161(tLRPC$BotInfo, j);
            }
        });
    }

    public /* synthetic */ void lambda$putBotInfo$161(TLRPC$BotInfo tLRPC$BotInfo, long j) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info_v2 VALUES(?, ?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$BotInfo.getObjectSize());
            tLRPC$BotInfo.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$BotInfo.user_id);
            executeFast.bindLong(2, j);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateBotInfo(final long j, final TLRPC$TL_updateBotCommands tLRPC$TL_updateBotCommands) {
        HashMap<String, TLRPC$BotInfo> hashMap = this.botInfos;
        TLRPC$BotInfo tLRPC$BotInfo = hashMap.get(tLRPC$TL_updateBotCommands.bot_id + "_" + j);
        if (tLRPC$BotInfo != null) {
            tLRPC$BotInfo.commands = tLRPC$TL_updateBotCommands.commands;
            getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, tLRPC$BotInfo, 0);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda107
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$updateBotInfo$162(tLRPC$TL_updateBotCommands, j);
            }
        });
    }

    public /* synthetic */ void lambda$updateBotInfo$162(TLRPC$TL_updateBotCommands tLRPC$TL_updateBotCommands, long j) {
        try {
            TLRPC$BotInfo loadBotInfoInternal = loadBotInfoInternal(tLRPC$TL_updateBotCommands.bot_id, j);
            if (loadBotInfoInternal != null) {
                loadBotInfoInternal.commands = tLRPC$TL_updateBotCommands.commands;
            }
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info_v2 VALUES(?, ?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(loadBotInfoInternal.getObjectSize());
            loadBotInfoInternal.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, loadBotInfoInternal.user_id);
            executeFast.bindLong(2, j);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public HashMap<String, TLRPC$TL_availableReaction> getReactionsMap() {
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
        String string = MessagesController.getEmojiSettings(this.currentAccount).getString("reaction_on_double_tap", null);
        if (string != null && getReactionsMap().get(string) != null) {
            this.doubleTapReaction = string;
            return string;
        }
        return getReactionsList().get(0).reaction;
    }

    public void setDoubleTapReaction(String str) {
        MessagesController.getEmojiSettings(this.currentAccount).edit().putString("reaction_on_double_tap", str).apply();
        this.doubleTapReaction = str;
    }

    public List<TLRPC$TL_availableReaction> getEnabledReactionsList() {
        return this.enabledReactionsList;
    }

    public void uploadRingtone(String str) {
        if (this.ringtoneUploaderHashMap.containsKey(str)) {
            return;
        }
        this.ringtoneUploaderHashMap.put(str, new RingtoneUploader(str, this.currentAccount));
        this.ringtoneDataStore.addUploadingTone(str);
    }

    public void onRingtoneUploaded(String str, TLRPC$Document tLRPC$Document, boolean z) {
        this.ringtoneUploaderHashMap.remove(str);
        this.ringtoneDataStore.onRingtoneUploaded(str, tLRPC$Document, z);
    }

    public void checkRingtones() {
        this.ringtoneDataStore.lambda$new$0();
    }

    public boolean saveToRingtones(final TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return false;
        }
        if (this.ringtoneDataStore.contains(tLRPC$Document.id)) {
            return true;
        }
        if (tLRPC$Document.size > MessagesController.getInstance(this.currentAccount).ringtoneSizeMax) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLargeError", org.telegram.messenger.beta.R.string.TooLargeError, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", org.telegram.messenger.beta.R.string.ErrorRingtoneSizeTooBig, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax / 1024)));
            return false;
        }
        for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) && tLRPC$DocumentAttribute.duration > MessagesController.getInstance(this.currentAccount).ringtoneDurationMax) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLongError", org.telegram.messenger.beta.R.string.TooLongError, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", org.telegram.messenger.beta.R.string.ErrorRingtoneDurationTooLong, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax)));
                return false;
            }
        }
        TLRPC$TL_account_saveRingtone tLRPC$TL_account_saveRingtone = new TLRPC$TL_account_saveRingtone();
        TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
        tLRPC$TL_account_saveRingtone.id = tLRPC$TL_inputDocument;
        tLRPC$TL_inputDocument.id = tLRPC$Document.id;
        tLRPC$TL_inputDocument.file_reference = tLRPC$Document.file_reference;
        tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_saveRingtone, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda175
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$saveToRingtones$164(tLRPC$Document, tLObject, tLRPC$TL_error);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$saveToRingtones$164(final TLRPC$Document tLRPC$Document, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda82
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$saveToRingtones$163(tLObject, tLRPC$Document);
            }
        });
    }

    public /* synthetic */ void lambda$saveToRingtones$163(TLObject tLObject, TLRPC$Document tLRPC$Document) {
        if (tLObject != null) {
            if (tLObject instanceof TLRPC$TL_account_savedRingtoneConverted) {
                this.ringtoneDataStore.addTone(((TLRPC$TL_account_savedRingtoneConverted) tLObject).document);
            } else {
                this.ringtoneDataStore.addTone(tLRPC$Document);
            }
        }
    }

    public void preloadPremiumPreviewStickers() {
        if (this.previewStickersLoading || !this.premiumPreviewStickers.isEmpty()) {
            int i = 0;
            while (i < Math.min(this.premiumPreviewStickers.size(), 3)) {
                ArrayList<TLRPC$Document> arrayList = this.premiumPreviewStickers;
                TLRPC$Document tLRPC$Document = arrayList.get(i == 2 ? arrayList.size() - 1 : i);
                if (MessageObject.isPremiumSticker(tLRPC$Document)) {
                    ImageReceiver imageReceiver = new ImageReceiver();
                    imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), null, null, "webp", null, 1);
                    ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
                    ImageReceiver imageReceiver2 = new ImageReceiver();
                    imageReceiver2.setImage(ImageLocation.getForDocument(MessageObject.getPremiumStickerAnimation(tLRPC$Document), tLRPC$Document), (String) null, (ImageLocation) null, (String) null, "tgs", (Object) null, 1);
                    ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver2);
                }
                i++;
            }
            return;
        }
        TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers = new TLRPC$TL_messages_getStickers();
        tLRPC$TL_messages_getStickers.emoticon = Emoji.fixEmoji("⭐") + Emoji.fixEmoji("⭐");
        tLRPC$TL_messages_getStickers.hash = 0L;
        this.previewStickersLoading = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getStickers, new RequestDelegate() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda147
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MediaDataController.this.lambda$preloadPremiumPreviewStickers$166(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$preloadPremiumPreviewStickers$166(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda97
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$preloadPremiumPreviewStickers$165(tLRPC$TL_error, tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$preloadPremiumPreviewStickers$165(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error != null) {
            return;
        }
        this.previewStickersLoading = false;
        this.premiumPreviewStickers.clear();
        this.premiumPreviewStickers.addAll(((TLRPC$TL_messages_stickers) tLObject).stickers);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.premiumStickersPreviewLoaded, new Object[0]);
    }

    public void chekAllMedia(boolean z) {
        if (z) {
            this.reactionsUpdateDate = 0;
            int[] iArr = this.loadFeaturedDate;
            iArr[0] = 0;
            iArr[1] = 0;
        }
        loadRecents(2, false, true, false);
        loadRecents(3, false, true, false);
        checkFeaturedStickers();
        checkFeaturedEmoji();
        checkReactions();
        checkMenuBots();
        checkPremiumPromo();
        checkPremiumGiftStickers();
    }

    public void fetchNewEmojiKeywords(String[] strArr) {
        if (strArr == null) {
            return;
        }
        for (final String str : strArr) {
            if (TextUtils.isEmpty(str) || this.currentFetchingEmoji.get(str) != null) {
                return;
            }
            this.currentFetchingEmoji.put(str, Boolean.TRUE);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda56
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$fetchNewEmojiKeywords$172(str);
                }
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0057  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x005f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$172(final java.lang.String r10) {
        /*
            r9 = this;
            r0 = -1
            r1 = 0
            r2 = 0
            org.telegram.messenger.MessagesStorage r4 = r9.getMessagesStorage()     // Catch: java.lang.Exception -> L33
            org.telegram.SQLite.SQLiteDatabase r4 = r4.getDatabase()     // Catch: java.lang.Exception -> L33
            java.lang.String r5 = "SELECT alias, version, date FROM emoji_keywords_info_v2 WHERE lang = ?"
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch: java.lang.Exception -> L33
            r8 = 0
            r7[r8] = r10     // Catch: java.lang.Exception -> L33
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r5, r7)     // Catch: java.lang.Exception -> L33
            boolean r5 = r4.next()     // Catch: java.lang.Exception -> L33
            if (r5 == 0) goto L2c
            java.lang.String r1 = r4.stringValue(r8)     // Catch: java.lang.Exception -> L33
            int r5 = r4.intValue(r6)     // Catch: java.lang.Exception -> L33
            r6 = 2
            long r2 = r4.longValue(r6)     // Catch: java.lang.Exception -> L31
            goto L2d
        L2c:
            r5 = -1
        L2d:
            r4.dispose()     // Catch: java.lang.Exception -> L31
            goto L38
        L31:
            r4 = move-exception
            goto L35
        L33:
            r4 = move-exception
            r5 = -1
        L35:
            org.telegram.messenger.FileLog.e(r4)
        L38:
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 != 0) goto L55
            long r6 = java.lang.System.currentTimeMillis()
            long r6 = r6 - r2
            long r2 = java.lang.Math.abs(r6)
            r6 = 3600000(0x36ee80, double:1.7786363E-317)
            int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r4 >= 0) goto L55
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda52 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda52
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        L55:
            if (r5 != r0) goto L5f
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords
            r0.<init>()
            r0.lang_code = r10
            goto L68
        L5f:
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference
            r0.<init>()
            r0.lang_code = r10
            r0.from_version = r5
        L68:
            org.telegram.tgnet.ConnectionsManager r2 = r9.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda153 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda153
            r3.<init>()
            r2.sendRequest(r0, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$fetchNewEmojiKeywords$172(java.lang.String):void");
    }

    public /* synthetic */ void lambda$fetchNewEmojiKeywords$167(String str) {
        this.currentFetchingEmoji.remove(str);
    }

    public /* synthetic */ void lambda$fetchNewEmojiKeywords$171(int i, String str, final String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference = (TLRPC$TL_emojiKeywordsDifference) tLObject;
            if (i != -1 && !tLRPC$TL_emojiKeywordsDifference.lang_code.equals(str)) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda57
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaDataController.this.lambda$fetchNewEmojiKeywords$169(str2);
                    }
                });
                return;
            } else {
                putEmojiKeywords(str2, tLRPC$TL_emojiKeywordsDifference);
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda53
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$fetchNewEmojiKeywords$170(str2);
            }
        });
    }

    public /* synthetic */ void lambda$fetchNewEmojiKeywords$169(final String str) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            executeFast.bindString(1, str);
            executeFast.step();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda54
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$fetchNewEmojiKeywords$168(str);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$fetchNewEmojiKeywords$168(String str) {
        this.currentFetchingEmoji.remove(str);
        fetchNewEmojiKeywords(new String[]{str});
    }

    public /* synthetic */ void lambda$fetchNewEmojiKeywords$170(String str) {
        this.currentFetchingEmoji.remove(str);
    }

    private void putEmojiKeywords(final String str, final TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference) {
        if (tLRPC$TL_emojiKeywordsDifference == null) {
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda96
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$putEmojiKeywords$174(tLRPC$TL_emojiKeywordsDifference, str);
            }
        });
    }

    public /* synthetic */ void lambda$putEmojiKeywords$174(TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference, final String str) {
        try {
            if (!tLRPC$TL_emojiKeywordsDifference.keywords.isEmpty()) {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_v2 VALUES(?, ?, ?)");
                SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_v2 WHERE lang = ? AND keyword = ? AND emoji = ?");
                getMessagesStorage().getDatabase().beginTransaction();
                int size = tLRPC$TL_emojiKeywordsDifference.keywords.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$EmojiKeyword tLRPC$EmojiKeyword = tLRPC$TL_emojiKeywordsDifference.keywords.get(i);
                    if (tLRPC$EmojiKeyword instanceof TLRPC$TL_emojiKeyword) {
                        TLRPC$TL_emojiKeyword tLRPC$TL_emojiKeyword = (TLRPC$TL_emojiKeyword) tLRPC$EmojiKeyword;
                        String lowerCase = tLRPC$TL_emojiKeyword.keyword.toLowerCase();
                        int size2 = tLRPC$TL_emojiKeyword.emoticons.size();
                        for (int i2 = 0; i2 < size2; i2++) {
                            executeFast.requery();
                            executeFast.bindString(1, tLRPC$TL_emojiKeywordsDifference.lang_code);
                            executeFast.bindString(2, lowerCase);
                            executeFast.bindString(3, tLRPC$TL_emojiKeyword.emoticons.get(i2));
                            executeFast.step();
                        }
                    } else if (tLRPC$EmojiKeyword instanceof TLRPC$TL_emojiKeywordDeleted) {
                        TLRPC$TL_emojiKeywordDeleted tLRPC$TL_emojiKeywordDeleted = (TLRPC$TL_emojiKeywordDeleted) tLRPC$EmojiKeyword;
                        String lowerCase2 = tLRPC$TL_emojiKeywordDeleted.keyword.toLowerCase();
                        int size3 = tLRPC$TL_emojiKeywordDeleted.emoticons.size();
                        for (int i3 = 0; i3 < size3; i3++) {
                            executeFast2.requery();
                            executeFast2.bindString(1, tLRPC$TL_emojiKeywordsDifference.lang_code);
                            executeFast2.bindString(2, lowerCase2);
                            executeFast2.bindString(3, tLRPC$TL_emojiKeywordDeleted.emoticons.get(i3));
                            executeFast2.step();
                        }
                    }
                }
                getMessagesStorage().getDatabase().commitTransaction();
                executeFast.dispose();
                executeFast2.dispose();
            }
            SQLitePreparedStatement executeFast3 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_info_v2 VALUES(?, ?, ?, ?)");
            executeFast3.bindString(1, str);
            executeFast3.bindString(2, tLRPC$TL_emojiKeywordsDifference.lang_code);
            executeFast3.bindInteger(3, tLRPC$TL_emojiKeywordsDifference.version);
            executeFast3.bindLong(4, System.currentTimeMillis());
            executeFast3.step();
            executeFast3.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda51
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$putEmojiKeywords$173(str);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$putEmojiKeywords$173(String str) {
        this.currentFetchingEmoji.remove(str);
        getNotificationCenter().postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, str);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback, boolean z2) {
        getEmojiSuggestions(strArr, str, z, keywordResultCallback, null, z2);
    }

    public void getEmojiSuggestions(final String[] strArr, final String str, final boolean z, final KeywordResultCallback keywordResultCallback, final CountDownLatch countDownLatch, final boolean z2) {
        if (keywordResultCallback == null) {
            return;
        }
        if (TextUtils.isEmpty(str) || strArr == null) {
            keywordResultCallback.run(new ArrayList<>(), null);
            return;
        }
        final ArrayList arrayList = new ArrayList(Emoji.recentEmoji);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda123
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$getEmojiSuggestions$180(strArr, keywordResultCallback, str, z, arrayList, z2, countDownLatch);
            }
        });
        if (countDownLatch == null) {
            return;
        }
        try {
            countDownLatch.await();
        } catch (Throwable unused) {
        }
    }

    public /* synthetic */ void lambda$getEmojiSuggestions$180(final String[] strArr, final KeywordResultCallback keywordResultCallback, String str, boolean z, final ArrayList arrayList, boolean z2, final CountDownLatch countDownLatch) {
        String str2;
        SQLiteCursor sQLiteCursor;
        final ArrayList<KeywordResult> arrayList2 = new ArrayList<>();
        HashMap hashMap = new HashMap();
        final String str3 = null;
        boolean z3 = false;
        for (int i = 0; i < strArr.length; i++) {
            try {
                SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT alias FROM emoji_keywords_info_v2 WHERE lang = ?", strArr[i]);
                if (queryFinalized.next()) {
                    str3 = queryFinalized.stringValue(0);
                }
                queryFinalized.dispose();
                if (str3 != null) {
                    z3 = true;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (!z3) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda124
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$getEmojiSuggestions$175(strArr, keywordResultCallback, arrayList2);
                }
            });
            return;
        }
        String lowerCase = str.toLowerCase();
        for (int i2 = 0; i2 < 2; i2++) {
            if (i2 == 1) {
                String translitString = LocaleController.getInstance().getTranslitString(lowerCase, false, false);
                if (!translitString.equals(lowerCase)) {
                    lowerCase = translitString;
                }
            }
            StringBuilder sb = new StringBuilder(lowerCase);
            int length = sb.length();
            while (true) {
                if (length <= 0) {
                    str2 = null;
                    break;
                }
                length--;
                char charAt = (char) (sb.charAt(length) + 1);
                sb.setCharAt(length, charAt);
                if (charAt != 0) {
                    str2 = sb.toString();
                    break;
                }
            }
            if (z) {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword = ?", lowerCase);
            } else if (str2 != null) {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword >= ? AND keyword < ?", lowerCase, str2);
            } else {
                lowerCase = lowerCase + "%";
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword LIKE ?", lowerCase);
            }
            while (sQLiteCursor.next()) {
                String replace = sQLiteCursor.stringValue(0).replace("️", "");
                if (hashMap.get(replace) == null) {
                    hashMap.put(replace, Boolean.TRUE);
                    KeywordResult keywordResult = new KeywordResult();
                    keywordResult.emoji = replace;
                    keywordResult.keyword = sQLiteCursor.stringValue(1);
                    arrayList2.add(keywordResult);
                }
            }
            sQLiteCursor.dispose();
        }
        Collections.sort(arrayList2, new Comparator() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda130
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$getEmojiSuggestions$176;
                lambda$getEmojiSuggestions$176 = MediaDataController.lambda$getEmojiSuggestions$176(arrayList, (MediaDataController.KeywordResult) obj, (MediaDataController.KeywordResult) obj2);
                return lambda$getEmojiSuggestions$176;
            }
        });
        if (z2) {
            fillWithAnimatedEmoji(arrayList2, new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.lambda$getEmojiSuggestions$178(countDownLatch, keywordResultCallback, arrayList2, str3);
                }
            });
        } else if (countDownLatch != null) {
            keywordResultCallback.run(arrayList2, str3);
            countDownLatch.countDown();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.KeywordResultCallback.this.run(arrayList2, str3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$getEmojiSuggestions$175(String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
        for (String str : strArr) {
            if (this.currentFetchingEmoji.get(str) != null) {
                return;
            }
        }
        keywordResultCallback.run(arrayList, null);
    }

    public static /* synthetic */ int lambda$getEmojiSuggestions$176(ArrayList arrayList, KeywordResult keywordResult, KeywordResult keywordResult2) {
        int indexOf = arrayList.indexOf(keywordResult.emoji);
        int i = Integer.MAX_VALUE;
        if (indexOf < 0) {
            indexOf = Integer.MAX_VALUE;
        }
        int indexOf2 = arrayList.indexOf(keywordResult2.emoji);
        if (indexOf2 >= 0) {
            i = indexOf2;
        }
        if (indexOf < i) {
            return -1;
        }
        if (indexOf > i) {
            return 1;
        }
        int length = keywordResult.keyword.length();
        int length2 = keywordResult2.keyword.length();
        if (length < length2) {
            return -1;
        }
        return length > length2 ? 1 : 0;
    }

    public static /* synthetic */ void lambda$getEmojiSuggestions$178(CountDownLatch countDownLatch, final KeywordResultCallback keywordResultCallback, final ArrayList arrayList, final String str) {
        if (countDownLatch != null) {
            keywordResultCallback.run(arrayList, str);
            countDownLatch.countDown();
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.KeywordResultCallback.this.run(arrayList, str);
            }
        });
    }

    public void fillWithAnimatedEmoji(final ArrayList<KeywordResult> arrayList, final Runnable runnable) {
        if (arrayList == null || arrayList.isEmpty()) {
            if (runnable == null) {
                return;
            }
            runnable.run();
            return;
        }
        final ArrayList[] arrayListArr = {getStickerSets(5)};
        final Runnable runnable2 = new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda76
            @Override // java.lang.Runnable
            public final void run() {
                MediaDataController.this.lambda$fillWithAnimatedEmoji$181(arrayList, arrayListArr, runnable);
            }
        };
        if ((arrayListArr[0] == null || arrayListArr[0].isEmpty()) && !this.triedLoadingEmojipacks) {
            this.triedLoadingEmojipacks = true;
            final boolean[] zArr = new boolean[1];
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda125
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.this.lambda$fillWithAnimatedEmoji$183(zArr, arrayListArr, runnable2);
                }
            });
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda128
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.lambda$fillWithAnimatedEmoji$184(zArr, runnable2);
                }
            }, 900L);
            return;
        }
        runnable2.run();
    }

    public /* synthetic */ void lambda$fillWithAnimatedEmoji$181(ArrayList arrayList, ArrayList[] arrayListArr, Runnable runnable) {
        String str;
        String str2;
        int i;
        boolean z;
        TLRPC$TL_documentAttributeCustomEmoji tLRPC$TL_documentAttributeCustomEmoji;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        int i2 = 2;
        if (arrayList.size() > 5) {
            i2 = 1;
        } else if (arrayList.size() <= 2) {
            i2 = 3;
        }
        int i3 = 0;
        int i4 = 0;
        while (i4 < Math.min(15, arrayList.size())) {
            String str3 = ((KeywordResult) arrayList.get(i4)).emoji;
            if (str3 != null) {
                arrayList3.clear();
                boolean isPremium = UserConfig.getInstance(this.currentAccount).isPremium();
                String str4 = "animated_";
                if (Emoji.recentEmoji != null) {
                    for (int i5 = 0; i5 < Emoji.recentEmoji.size(); i5++) {
                        if (Emoji.recentEmoji.get(i5).startsWith(str4)) {
                            try {
                                TLRPC$Document findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, Long.parseLong(Emoji.recentEmoji.get(i5).substring(9)));
                                if (findDocument != null && ((isPremium || MessageObject.isFreeEmoji(findDocument)) && str3.equals(MessageObject.findAnimatedEmojiEmoticon(findDocument, null)))) {
                                    arrayList3.add(findDocument);
                                }
                            } catch (Exception unused) {
                            }
                        }
                        if (arrayList3.size() >= i2) {
                            break;
                        }
                    }
                }
                if (arrayList3.size() < i2 && arrayListArr[i3] != null) {
                    int i6 = 0;
                    while (i6 < arrayListArr[i3].size()) {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) arrayListArr[i3].get(i6);
                        if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.documents != null) {
                            int i7 = 0;
                            while (i7 < tLRPC$TL_messages_stickerSet.documents.size()) {
                                TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i7);
                                if (tLRPC$Document == null || tLRPC$Document.attributes == null || arrayList3.contains(tLRPC$Document)) {
                                    str = str4;
                                } else {
                                    int i8 = 0;
                                    while (true) {
                                        if (i8 >= arrayList3.size()) {
                                            i = i2;
                                            str = str4;
                                            z = false;
                                            break;
                                        }
                                        i = i2;
                                        str = str4;
                                        if (((TLRPC$Document) arrayList3.get(i8)).id == tLRPC$Document.id) {
                                            z = true;
                                            break;
                                        }
                                        i8++;
                                        i2 = i;
                                        str4 = str;
                                    }
                                    if (!z) {
                                        int i9 = 0;
                                        while (true) {
                                            if (i9 >= tLRPC$Document.attributes.size()) {
                                                tLRPC$TL_documentAttributeCustomEmoji = null;
                                                break;
                                            }
                                            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i9);
                                            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeCustomEmoji) {
                                                tLRPC$TL_documentAttributeCustomEmoji = (TLRPC$TL_documentAttributeCustomEmoji) tLRPC$DocumentAttribute;
                                                break;
                                            }
                                            i9++;
                                        }
                                        if (tLRPC$TL_documentAttributeCustomEmoji != null && str3.equals(tLRPC$TL_documentAttributeCustomEmoji.alt) && (isPremium || tLRPC$TL_documentAttributeCustomEmoji.free)) {
                                            arrayList3.add(tLRPC$Document);
                                            i2 = i;
                                            if (arrayList3.size() >= i2) {
                                                break;
                                            }
                                        }
                                    }
                                    i2 = i;
                                }
                                i7++;
                                str4 = str;
                            }
                        }
                        str = str4;
                        if (arrayList3.size() >= i2) {
                            break;
                        }
                        i6++;
                        str4 = str;
                        i3 = 0;
                    }
                }
                str = str4;
                if (!arrayList3.isEmpty()) {
                    String str5 = ((KeywordResult) arrayList.get(i4)).keyword;
                    int i10 = 0;
                    while (i10 < arrayList3.size()) {
                        TLRPC$Document tLRPC$Document2 = (TLRPC$Document) arrayList3.get(i10);
                        if (tLRPC$Document2 != null) {
                            KeywordResult keywordResult = new KeywordResult();
                            StringBuilder sb = new StringBuilder();
                            str2 = str;
                            sb.append(str2);
                            sb.append(tLRPC$Document2.id);
                            keywordResult.emoji = sb.toString();
                            keywordResult.keyword = str5;
                            arrayList2.add(keywordResult);
                        } else {
                            str2 = str;
                        }
                        i10++;
                        str = str2;
                    }
                }
            }
            i4++;
            i3 = 0;
        }
        arrayList.addAll(i3, arrayList2);
        if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$fillWithAnimatedEmoji$183(final boolean[] zArr, final ArrayList[] arrayListArr, final Runnable runnable) {
        loadStickers(5, true, false, false, new Utilities.Callback() { // from class: org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda140
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                MediaDataController.lambda$fillWithAnimatedEmoji$182(zArr, arrayListArr, runnable, (ArrayList) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$fillWithAnimatedEmoji$182(boolean[] zArr, ArrayList[] arrayListArr, Runnable runnable, ArrayList arrayList) {
        if (!zArr[0]) {
            arrayListArr[0] = arrayList;
            runnable.run();
            zArr[0] = true;
        }
    }

    public static /* synthetic */ void lambda$fillWithAnimatedEmoji$184(boolean[] zArr, Runnable runnable) {
        if (!zArr[0]) {
            runnable.run();
            zArr[0] = true;
        }
    }

    public void loadEmojiThemes() {
        Context context = ApplicationLoader.applicationContext;
        SharedPreferences sharedPreferences = context.getSharedPreferences("emojithemes_config_" + this.currentAccount, 0);
        int i = sharedPreferences.getInt(NotificationBadge.NewHtcHomeBadger.COUNT, 0);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ChatThemeBottomSheet.ChatThemeItem(EmojiThemes.createHomePreviewTheme()));
        for (int i2 = 0; i2 < i; i2++) {
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(sharedPreferences.getString("theme_" + i2, "")));
            try {
                EmojiThemes createPreviewFullTheme = EmojiThemes.createPreviewFullTheme(TLRPC$Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true));
                if (createPreviewFullTheme.items.size() >= 4) {
                    arrayList.add(new ChatThemeBottomSheet.ChatThemeItem(createPreviewFullTheme));
                }
                ChatThemeController.chatThemeQueue.postRunnable(new AnonymousClass2(arrayList));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MediaDataController$2 */
    /* loaded from: classes.dex */
    public class AnonymousClass2 implements Runnable {
        final /* synthetic */ ArrayList val$previewItems;

        AnonymousClass2(ArrayList arrayList) {
            MediaDataController.this = r1;
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
                    MediaDataController.AnonymousClass2.this.lambda$run$0(arrayList);
                }
            });
        }

        public /* synthetic */ void lambda$run$0(ArrayList arrayList) {
            MediaDataController.this.defaultEmojiThemes.clear();
            MediaDataController.this.defaultEmojiThemes.addAll(arrayList);
        }
    }

    public void generateEmojiPreviewThemes(ArrayList<TLRPC$TL_theme> arrayList, int i) {
        Context context = ApplicationLoader.applicationContext;
        SharedPreferences.Editor edit = context.getSharedPreferences("emojithemes_config_" + i, 0).edit();
        edit.putInt(NotificationBadge.NewHtcHomeBadger.COUNT, arrayList.size());
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            TLRPC$TL_theme tLRPC$TL_theme = arrayList.get(i2);
            SerializedData serializedData = new SerializedData(tLRPC$TL_theme.getObjectSize());
            tLRPC$TL_theme.serializeToStream(serializedData);
            edit.putString("theme_" + i2, Utilities.bytesToHex(serializedData.toByteArray()));
        }
        edit.apply();
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(new ChatThemeBottomSheet.ChatThemeItem(EmojiThemes.createHomePreviewTheme()));
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                EmojiThemes createPreviewFullTheme = EmojiThemes.createPreviewFullTheme(arrayList.get(i3));
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem = new ChatThemeBottomSheet.ChatThemeItem(createPreviewFullTheme);
                if (createPreviewFullTheme.items.size() >= 4) {
                    arrayList2.add(chatThemeItem);
                }
            }
            ChatThemeController.chatThemeQueue.postRunnable(new AnonymousClass3(arrayList2, i));
            return;
        }
        this.defaultEmojiThemes.clear();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiPreviewThemesChanged, new Object[0]);
    }

    /* renamed from: org.telegram.messenger.MediaDataController$3 */
    /* loaded from: classes.dex */
    public class AnonymousClass3 implements Runnable {
        final /* synthetic */ int val$currentAccount;
        final /* synthetic */ ArrayList val$previewItems;

        AnonymousClass3(ArrayList arrayList, int i) {
            MediaDataController.this = r1;
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
                    MediaDataController.AnonymousClass3.this.lambda$run$0(arrayList);
                }
            });
        }

        public /* synthetic */ void lambda$run$0(ArrayList arrayList) {
            MediaDataController.this.defaultEmojiThemes.clear();
            MediaDataController.this.defaultEmojiThemes.addAll(arrayList);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiPreviewThemesChanged, new Object[0]);
        }
    }
}
