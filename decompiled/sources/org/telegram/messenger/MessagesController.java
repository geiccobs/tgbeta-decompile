package org.telegram.messenger;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import androidx.core.util.Consumer;
import androidx.core.view.ViewCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.gms.wearable.WearableStatusCodes;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.messaging.Constants;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationBadge;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.support.LongSparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatReactionsEditActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes.dex */
public class MessagesController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    private static volatile long lastPasswordCheckTime;
    private static volatile long lastThemeCheckTime;
    public int aboutLengthLimitDefault;
    public int aboutLengthLimitPremium;
    public float animatedEmojisZoom;
    public Set<String> authDomains;
    public boolean autoarchiveAvailable;
    public Set<String> autologinDomains;
    public String autologinToken;
    public int availableMapProviders;
    public boolean backgroundConnection;
    public boolean blockedCountry;
    public boolean blockedEndReached;
    public int callConnectTimeout;
    public int callPacketTimeout;
    public int callReceiveTimeout;
    public int callRingTimeout;
    public boolean canRevokePmInbox;
    public int captionLengthLimitDefault;
    public int captionLengthLimitPremium;
    public int channelsLimitDefault;
    public int channelsLimitPremium;
    public int chatReadMarkExpirePeriod;
    public int chatReadMarkSizeThreshold;
    private boolean checkingPromoInfo;
    private int checkingPromoInfoRequestId;
    private boolean checkingTosUpdate;
    private Runnable currentDeleteTaskRunnable;
    private LongSparseArray<ArrayList<Integer>> currentDeletingTaskMediaMids;
    private LongSparseArray<ArrayList<Integer>> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public String dcDomainName;
    public int dialogFiltersChatsLimitDefault;
    public int dialogFiltersChatsLimitPremium;
    public int dialogFiltersLimitDefault;
    public int dialogFiltersLimitPremium;
    public boolean dialogFiltersLoaded;
    public int dialogFiltersPinnedLimitDefault;
    public int dialogFiltersPinnedLimitPremium;
    private boolean dialogsInTransaction;
    public boolean dialogsLoaded;
    public HashSet<String> diceEmojies;
    private SharedPreferences emojiPreferences;
    public boolean enableJoined;
    public Set<String> exportGroupUri;
    public Set<String> exportPrivateUri;
    public Set<String> exportUri;
    public TLRPC.WebPage faqWebPage;
    public boolean filtersEnabled;
    public boolean firstGettingTask;
    public boolean getfileExperimentalParams;
    private boolean gettingAppChangelog;
    public boolean gettingDifference;
    private boolean gettingNewDeleteTask;
    public String gifSearchBot;
    public int groupCallVideoMaxParticipants;
    public volatile boolean ignoreSetOnline;
    public String imageSearchBot;
    private String installReferer;
    private boolean isLeftPromoChannel;
    public boolean keepAliveService;
    private int lastCheckPromoId;
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix;
    private boolean loadingAppConfig;
    private int loadingNotificationSettings;
    private boolean loadingNotificationSignUpSettings;
    private boolean loadingRemoteFilters;
    private boolean loadingSuggestedFilters;
    private boolean loadingUnreadDialogs;
    private SharedPreferences mainPreferences;
    public String mapKey;
    public int mapProvider;
    public int maxCaptionLength;
    public int maxEditTime;
    public int maxFaveStickersCount;
    public int maxFolderPinnedDialogsCount;
    public int maxGroupCount;
    public int maxMegagroupCount;
    public int maxMessageLength;
    public int maxPinnedDialogsCount;
    public int maxRecentGifsCount;
    public int maxRecentStickersCount;
    private boolean migratingDialogs;
    private int nextPromoInfoCheckTime;
    private int nextTosCheckTime;
    private SharedPreferences notificationsPreferences;
    private boolean offlineSent;
    public Set<String> pendingSuggestions;
    private int pollsToCheckSize;
    public boolean preloadFeaturedStickers;
    public String premiumBotUsername;
    public String premiumInvoiceSlug;
    public boolean premiumLocked;
    private TLRPC.Dialog promoDialog;
    private long promoDialogId;
    public int promoDialogType;
    public String promoPsaMessage;
    public String promoPsaType;
    private String proxyDialogAddress;
    public int publicLinksLimitDefault;
    public int publicLinksLimitPremium;
    public boolean qrLoginCamera;
    public int ratingDecay;
    public boolean registeringForPush;
    public boolean remoteConfigLoaded;
    private TLRPC.messages_Dialogs resetDialogsAll;
    private TLRPC.TL_messages_peerDialogs resetDialogsPinned;
    private boolean resetingDialogs;
    public int revokeTimeLimit;
    public int revokeTimePmLimit;
    public int ringtoneDurationMax;
    public int ringtoneSizeMax;
    public int roundAudioBitrate;
    public int roundVideoBitrate;
    public int roundVideoSize;
    public boolean saveGifsWithStickers;
    public int savedGifsLimitDefault;
    public int savedGifsLimitPremium;
    public int secretWebpagePreview;
    public boolean showFiltersTooltip;
    private DialogFilter sortingDialogFilter;
    private int statusRequest;
    private int statusSettingState;
    public int stickersFavedLimitDefault;
    public int stickersFavedLimitPremium;
    public boolean suggestStickersApiOnly;
    public String suggestedLangCode;
    public int unreadUnmutedDialogs;
    public int updateCheckDelay;
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    public int uploadMaxFileParts;
    public int uploadMaxFilePartsPremium;
    private String uploadingAvatar;
    private String uploadingWallpaper;
    private Theme.OverrideWallpaperInfo uploadingWallpaperInfo;
    public String venueSearchBot;
    public int webFileDatacenterId;
    public String youtubePipType;
    public static int UPDATE_MASK_NAME = 1;
    public static int UPDATE_MASK_AVATAR = 2;
    public static int UPDATE_MASK_STATUS = 4;
    public static int UPDATE_MASK_CHAT_AVATAR = 8;
    public static int UPDATE_MASK_CHAT_NAME = 16;
    public static int UPDATE_MASK_CHAT_MEMBERS = 32;
    public static int UPDATE_MASK_USER_PRINT = 64;
    public static int UPDATE_MASK_USER_PHONE = 128;
    public static int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
    public static int UPDATE_MASK_SELECT_DIALOG = 512;
    public static int UPDATE_MASK_PHONE = 1024;
    public static int UPDATE_MASK_NEW_MESSAGE = 2048;
    public static int UPDATE_MASK_SEND_STATE = 4096;
    public static int UPDATE_MASK_CHAT = 8192;
    public static int UPDATE_MASK_MESSAGE_TEXT = 32768;
    public static int UPDATE_MASK_CHECK = 65536;
    public static int UPDATE_MASK_REORDER = 131072;
    public static int UPDATE_MASK_EMOJI_INTERACTIONS = 262144;
    public static int UPDATE_MASK_ALL = 1024 | ((((((((2 | 4) | 1) | 8) | 16) | 32) | 64) | 128) | 256);
    public static int PROMO_TYPE_PROXY = 0;
    public static int PROMO_TYPE_PSA = 1;
    public static int PROMO_TYPE_OTHER = 2;
    public static int DIALOG_FILTER_FLAG_CONTACTS = 1;
    public static int DIALOG_FILTER_FLAG_NON_CONTACTS = 2;
    public static int DIALOG_FILTER_FLAG_GROUPS = 4;
    public static int DIALOG_FILTER_FLAG_CHANNELS = 8;
    public static int DIALOG_FILTER_FLAG_BOTS = 16;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_MUTED = 32;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_READ = 64;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED = 128;
    public static int DIALOG_FILTER_FLAG_ONLY_ARCHIVED = 256;
    public static int DIALOG_FILTER_FLAG_ALL_CHATS = (((1 | 2) | 4) | 8) | 16;
    private static volatile MessagesController[] Instance = new MessagesController[4];
    private static final Object[] lockObjects = new Object[4];
    private ConcurrentHashMap<Long, TLRPC.Chat> chats = new ConcurrentHashMap<>(100, 1.0f, 2);
    private ConcurrentHashMap<Integer, TLRPC.EncryptedChat> encryptedChats = new ConcurrentHashMap<>(10, 1.0f, 2);
    private ConcurrentHashMap<Long, TLRPC.User> users = new ConcurrentHashMap<>(100, 1.0f, 2);
    private ConcurrentHashMap<String, TLObject> objectsByUsernames = new ConcurrentHashMap<>(100, 1.0f, 2);
    private HashMap<Long, TLRPC.Chat> activeVoiceChatsMap = new HashMap<>();
    private ArrayList<Long> joiningToChannels = new ArrayList<>();
    private LongSparseArray<TLRPC.TL_chatInviteExported> exportedChats = new LongSparseArray<>();
    public ArrayList<TLRPC.RecentMeUrl> hintDialogs = new ArrayList<>();
    public SparseArray<ArrayList<TLRPC.Dialog>> dialogsByFolder = new SparseArray<>();
    protected ArrayList<TLRPC.Dialog> allDialogs = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsForward = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsServerOnly = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsCanAddUsers = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsMyChannels = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsMyGroups = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsChannelsOnly = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsUsersOnly = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsForBlock = new ArrayList<>();
    public ArrayList<TLRPC.Dialog> dialogsGroupsOnly = new ArrayList<>();
    public DialogFilter[] selectedDialogFilter = new DialogFilter[2];
    private int dialogsLoadedTillDate = Integer.MAX_VALUE;
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap<>(100, 1.0f, 2);
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap<>(100, 1.0f, 2);
    public LongSparseArray<TLRPC.Dialog> dialogs_dict = new LongSparseArray<>();
    public LongSparseArray<MessageObject> dialogMessage = new LongSparseArray<>();
    public LongSparseArray<MessageObject> dialogMessagesByRandomIds = new LongSparseArray<>();
    public LongSparseIntArray deletedHistory = new LongSparseIntArray();
    public SparseArray<MessageObject> dialogMessagesByIds = new SparseArray<>();
    public ConcurrentHashMap<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>> printingUsers = new ConcurrentHashMap<>(20, 1.0f, 2);
    public LongSparseArray<SparseArray<CharSequence>> printingStrings = new LongSparseArray<>();
    public LongSparseArray<SparseArray<Integer>> printingStringsTypes = new LongSparseArray<>();
    public LongSparseArray<SparseArray<Boolean>>[] sendingTypings = new LongSparseArray[12];
    public ConcurrentHashMap<Long, Integer> onlinePrivacy = new ConcurrentHashMap<>(20, 1.0f, 2);
    private LongSparseArray<Boolean> loadingPeerSettings = new LongSparseArray<>();
    private ArrayList<Long> createdDialogIds = new ArrayList<>();
    private ArrayList<Long> createdScheduledDialogIds = new ArrayList<>();
    private ArrayList<Long> createdDialogMainThreadIds = new ArrayList<>();
    private ArrayList<Long> visibleDialogMainThreadIds = new ArrayList<>();
    private ArrayList<Long> visibleScheduledDialogMainThreadIds = new ArrayList<>();
    private LongSparseIntArray shortPollChannels = new LongSparseIntArray();
    private LongSparseArray<ArrayList<Integer>> needShortPollChannels = new LongSparseArray<>();
    private LongSparseIntArray shortPollOnlines = new LongSparseIntArray();
    private LongSparseArray<ArrayList<Integer>> needShortPollOnlines = new LongSparseArray<>();
    private LongSparseArray<TLRPC.Dialog> deletingDialogs = new LongSparseArray<>();
    private LongSparseArray<TLRPC.Dialog> clearingHistoryDialogs = new LongSparseArray<>();
    public boolean loadingBlockedPeers = false;
    public LongSparseIntArray blockePeers = new LongSparseIntArray();
    public int totalBlockedCount = -1;
    private LongSparseArray<ArrayList<Integer>> channelViewsToSend = new LongSparseArray<>();
    private LongSparseArray<SparseArray<MessageObject>> pollsToCheck = new LongSparseArray<>();
    public SparseIntArray premiumFeaturesTypesToPosition = new SparseIntArray();
    public ArrayList<DialogFilter> dialogFilters = new ArrayList<>();
    public SparseArray<DialogFilter> dialogFiltersById = new SparseArray<>();
    public ArrayList<TLRPC.TL_dialogFilterSuggested> suggestedFilters = new ArrayList<>();
    private LongSparseArray<ArrayList<TLRPC.Updates>> updatesQueueChannels = new LongSparseArray<>();
    private LongSparseLongArray updatesStartWaitTimeChannels = new LongSparseLongArray();
    private LongSparseIntArray channelsPts = new LongSparseIntArray();
    private LongSparseArray<Boolean> gettingDifferenceChannels = new LongSparseArray<>();
    private LongSparseArray<Boolean> gettingChatInviters = new LongSparseArray<>();
    private LongSparseArray<Boolean> gettingUnknownChannels = new LongSparseArray<>();
    private LongSparseArray<Boolean> gettingUnknownDialogs = new LongSparseArray<>();
    private LongSparseArray<Boolean> checkingLastMessagesDialogs = new LongSparseArray<>();
    private ArrayList<TLRPC.Updates> updatesQueueSeq = new ArrayList<>();
    private ArrayList<TLRPC.Updates> updatesQueuePts = new ArrayList<>();
    private ArrayList<TLRPC.Updates> updatesQueueQts = new ArrayList<>();
    private LongSparseArray<TLRPC.UserFull> fullUsers = new LongSparseArray<>();
    private LongSparseArray<TLRPC.ChatFull> fullChats = new LongSparseArray<>();
    private LongSparseArray<ChatObject.Call> groupCalls = new LongSparseArray<>();
    private LongSparseArray<ChatObject.Call> groupCallsByChatId = new LongSparseArray<>();
    private ArrayList<Long> loadingFullUsers = new ArrayList<>();
    private ArrayList<Long> loadedFullUsers = new ArrayList<>();
    private ArrayList<Long> loadingFullChats = new ArrayList<>();
    private ArrayList<Long> loadingGroupCalls = new ArrayList<>();
    private ArrayList<Long> loadingFullParticipants = new ArrayList<>();
    private ArrayList<Long> loadedFullParticipants = new ArrayList<>();
    private ArrayList<Long> loadedFullChats = new ArrayList<>();
    private LongSparseArray<LongSparseArray<TLRPC.ChannelParticipant>> channelAdmins = new LongSparseArray<>();
    private LongSparseIntArray loadingChannelAdmins = new LongSparseIntArray();
    private SparseIntArray migratedChats = new SparseIntArray();
    private LongSparseArray<SponsoredMessagesInfo> sponsoredMessages = new LongSparseArray<>();
    private LongSparseArray<SendAsPeersInfo> sendAsPeers = new LongSparseArray<>();
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages = new HashMap<>();
    private LongSparseArray<ArrayList<MessageObject>> reloadingWebpagesPending = new LongSparseArray<>();
    private HashMap<String, ArrayList<MessageObject>> reloadingScheduledWebpages = new HashMap<>();
    private LongSparseArray<ArrayList<MessageObject>> reloadingScheduledWebpagesPending = new LongSparseArray<>();
    private LongSparseArray<Long> lastScheduledServerQueryTime = new LongSparseArray<>();
    private LongSparseArray<Long> lastServerQueryTime = new LongSparseArray<>();
    private LongSparseArray<ArrayList<Integer>> reloadingMessages = new LongSparseArray<>();
    private ArrayList<ReadTask> readTasks = new ArrayList<>();
    private LongSparseArray<ReadTask> readTasksMap = new LongSparseArray<>();
    private ArrayList<ReadTask> repliesReadTasks = new ArrayList<>();
    private HashMap<String, ReadTask> threadsReadTasksMap = new HashMap<>();
    private SparseIntArray nextDialogsCacheOffset = new SparseIntArray();
    private SparseBooleanArray loadingDialogs = new SparseBooleanArray();
    private SparseBooleanArray dialogsEndReached = new SparseBooleanArray();
    private SparseBooleanArray serverDialogsEndReached = new SparseBooleanArray();
    private boolean getDifferenceFirstSync = true;
    private SparseIntArray loadingPinnedDialogs = new SparseIntArray();
    public ArrayList<FaqSearchResult> faqSearchArray = new ArrayList<>();
    public boolean suggestContacts = true;
    private Runnable themeCheckRunnable = MessagesController$$ExternalSyntheticLambda132.INSTANCE;
    private Runnable passwordCheckRunnable = new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda133
        @Override // java.lang.Runnable
        public final void run() {
            MessagesController.this.m759lambda$new$0$orgtelegrammessengerMessagesController();
        }
    };
    private HashMap<String, Object> uploadingThemes = new HashMap<>();
    public int maxBroadcastCount = 100;
    public int minGroupConvertSize = 200;
    public ArrayList<String> gifSearchEmojies = new ArrayList<>();
    public HashMap<String, DiceFrameSuccess> diceSuccess = new HashMap<>();
    public HashMap<String, EmojiSound> emojiSounds = new HashMap<>();
    public HashMap<Long, ArrayList<TLRPC.TL_sendMessageEmojiInteraction>> emojiInteractions = new HashMap<>();
    private Comparator<TLRPC.Dialog> dialogDateComparator = new Comparator() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda134
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return MessagesController.this.m761lambda$new$6$orgtelegrammessengerMessagesController((TLRPC.Dialog) obj, (TLRPC.Dialog) obj2);
        }
    };
    private Comparator<TLRPC.Dialog> dialogComparator = new Comparator() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda135
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return MessagesController.this.m762lambda$new$7$orgtelegrammessengerMessagesController((TLRPC.Dialog) obj, (TLRPC.Dialog) obj2);
        }
    };
    private Comparator<TLRPC.Update> updatesComparator = new Comparator() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda136
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return MessagesController.this.m763lambda$new$8$orgtelegrammessengerMessagesController((TLRPC.Update) obj, (TLRPC.Update) obj2);
        }
    };
    private int DIALOGS_LOAD_TYPE_CACHE = 1;
    private int DIALOGS_LOAD_TYPE_CHANNEL = 2;
    private int DIALOGS_LOAD_TYPE_UNKNOWN = 3;

    /* loaded from: classes4.dex */
    public interface ErrorDelegate {
        boolean run(TLRPC.TL_error tL_error);
    }

    /* loaded from: classes4.dex */
    public interface IsInChatCheckedCallback {
        void run(boolean z, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str);
    }

    /* loaded from: classes4.dex */
    public interface MessagesLoadedCallback {
        void onError();

        void onMessagesLoaded(boolean z);
    }

    /* loaded from: classes4.dex */
    public static class PrintingUser {
        public TLRPC.SendMessageAction action;
        public long lastTime;
        public long userId;
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m759lambda$new$0$orgtelegrammessengerMessagesController() {
        getUserConfig().checkSavedPassword();
    }

    public void getNextReactionMention(final long dialogId, final int count, final Consumer<Integer> callback) {
        MessagesStorage messagesStorage = getMessagesStorage();
        messagesStorage.getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda336
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m693xf58aaac7(dialogId, callback, count);
            }
        });
    }

    /* renamed from: lambda$getNextReactionMention$5$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m693xf58aaac7(long dialogId, final Consumer callback, int count) {
        boolean needRequest = true;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT message_id FROM reaction_mentions WHERE state = 1 AND dialog_id = %d LIMIT 1", Long.valueOf(dialogId)), new Object[0]);
            int messageId = 0;
            if (cursor.next()) {
                messageId = cursor.intValue(0);
                needRequest = false;
            }
            cursor.dispose();
            if (messageId != 0) {
                getMessagesStorage().markMessageReactionsAsRead(dialogId, messageId, false);
                final int finalMessageId = messageId;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda328
                    @Override // java.lang.Runnable
                    public final void run() {
                        Consumer.this.accept(Integer.valueOf(finalMessageId));
                    }
                });
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (needRequest) {
            TLRPC.TL_messages_getUnreadReactions req = new TLRPC.TL_messages_getUnreadReactions();
            req.peer = getMessagesController().getInputPeer(dialogId);
            req.limit = 1;
            req.add_offset = count - 1;
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda145
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda131
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesController.lambda$getNextReactionMention$3(TLObject.this, tL_error, r3);
                        }
                    });
                }
            });
        }
    }

    public static /* synthetic */ void lambda$getNextReactionMention$3(TLObject response, TLRPC.TL_error error, final Consumer callback) {
        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
        int messageId = 0;
        if (error != null && res != null && res.messages != null && !res.messages.isEmpty()) {
            messageId = res.messages.get(0).id;
        }
        final int finalMessageId = messageId;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda339
            @Override // java.lang.Runnable
            public final void run() {
                Consumer.this.accept(Integer.valueOf(finalMessageId));
            }
        });
    }

    public void updatePremium(boolean premium) {
        if (this.dialogFilters.isEmpty()) {
            return;
        }
        if (!premium) {
            if (!this.dialogFilters.get(0).isDefault()) {
                int i = 1;
                while (true) {
                    if (i < this.dialogFilters.size()) {
                        if (!this.dialogFilters.get(i).isDefault()) {
                            i++;
                        } else {
                            DialogFilter defaultFilter = this.dialogFilters.remove(i);
                            this.dialogFilters.add(0, defaultFilter);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            lockFiltersInternal();
        } else {
            for (int i2 = 0; i2 < this.dialogFilters.size(); i2++) {
                this.dialogFilters.get(i2).locked = false;
            }
        }
        getMessagesStorage().saveDialogFiltersOrder();
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
    }

    public void lockFiltersInternal() {
        boolean changed = false;
        if (!getUserConfig().isPremium() && this.dialogFilters.size() - 1 > this.dialogFiltersLimitDefault) {
            int n = (this.dialogFilters.size() - 1) - this.dialogFiltersLimitDefault;
            ArrayList<DialogFilter> filtersSortedById = new ArrayList<>(this.dialogFilters);
            Collections.reverse(filtersSortedById);
            for (int i = 0; i < filtersSortedById.size(); i++) {
                if (i < n) {
                    if (!filtersSortedById.get(i).locked) {
                        changed = true;
                    }
                    filtersSortedById.get(i).locked = true;
                } else {
                    if (filtersSortedById.get(i).locked) {
                        changed = true;
                    }
                    filtersSortedById.get(i).locked = false;
                }
            }
        }
        if (changed) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        }
    }

    public int getCaptionMaxLengthLimit() {
        return getUserConfig().isPremium() ? this.captionLengthLimitPremium : this.captionLengthLimitDefault;
    }

    public int getAboutLimit() {
        return getUserConfig().isPremium() ? this.aboutLengthLimitPremium : this.aboutLengthLimitDefault;
    }

    public boolean isPremiumUser(TLRPC.User currentUser) {
        return !this.premiumLocked && currentUser.premium;
    }

    public ArrayList<TLRPC.TL_messages_stickerSet> filterPremiumStickers(ArrayList<TLRPC.TL_messages_stickerSet> stickerSets) {
        if (!this.premiumLocked) {
            return stickerSets;
        }
        int i = 0;
        while (i < stickerSets.size()) {
            TLRPC.TL_messages_stickerSet newSet = getInstance(this.currentAccount).filterPremiumStickers(stickerSets.get(i));
            if (newSet == null) {
                stickerSets.remove(i);
                i--;
            } else {
                stickerSets.set(i, newSet);
            }
            i++;
        }
        return stickerSets;
    }

    public TLRPC.TL_messages_stickerSet filterPremiumStickers(TLRPC.TL_messages_stickerSet stickerSet) {
        if (!this.premiumLocked || stickerSet == null) {
            return stickerSet;
        }
        boolean hasPremiumSticker = false;
        int i = 0;
        while (true) {
            try {
                if (i >= stickerSet.documents.size()) {
                    break;
                } else if (!MessageObject.isPremiumSticker(stickerSet.documents.get(i))) {
                    i++;
                } else {
                    hasPremiumSticker = true;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (hasPremiumSticker) {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(stickerSet.getObjectSize());
            stickerSet.serializeToStream(nativeByteBuffer);
            nativeByteBuffer.position(0);
            TLRPC.TL_messages_stickerSet newStickersSet = new TLRPC.TL_messages_stickerSet();
            nativeByteBuffer.readInt32(true);
            newStickersSet.readParams(nativeByteBuffer, true);
            nativeByteBuffer.reuse();
            stickerSet = newStickersSet;
            int i2 = 0;
            while (i2 < stickerSet.documents.size()) {
                if (MessageObject.isPremiumSticker(stickerSet.documents.get(i2))) {
                    stickerSet.documents.remove(i2);
                    stickerSet.packs.remove(i2);
                    i2--;
                    if (stickerSet.documents.isEmpty()) {
                        return null;
                    }
                }
                i2++;
            }
        }
        return stickerSet;
    }

    /* loaded from: classes4.dex */
    public class SponsoredMessagesInfo {
        private long loadTime;
        private boolean loading;
        private ArrayList<MessageObject> messages;

        private SponsoredMessagesInfo() {
            MessagesController.this = r1;
        }
    }

    /* loaded from: classes4.dex */
    public class SendAsPeersInfo {
        private long loadTime;
        private boolean loading;
        private TLRPC.TL_channels_sendAsPeers sendAsPeers;

        private SendAsPeersInfo() {
            MessagesController.this = r1;
        }
    }

    /* loaded from: classes4.dex */
    public static class FaqSearchResult {
        public int num;
        public String[] path;
        public String title;
        public String url;

        public FaqSearchResult(String t, String[] p, String u) {
            this.title = t;
            this.path = p;
            this.url = u;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof FaqSearchResult)) {
                return false;
            }
            FaqSearchResult result = (FaqSearchResult) obj;
            return this.title.equals(result.title);
        }

        public String toString() {
            SerializedData data = new SerializedData();
            data.writeInt32(this.num);
            int i = 0;
            data.writeInt32(0);
            data.writeString(this.title);
            String[] strArr = this.path;
            if (strArr != null) {
                i = strArr.length;
            }
            data.writeInt32(i);
            if (this.path != null) {
                int a = 0;
                while (true) {
                    String[] strArr2 = this.path;
                    if (a >= strArr2.length) {
                        break;
                    }
                    data.writeString(strArr2[a]);
                    a++;
                }
            }
            data.writeString(this.url);
            return Utilities.bytesToHex(data.toByteArray());
        }
    }

    /* loaded from: classes4.dex */
    public static class EmojiSound {
        public long accessHash;
        public byte[] fileReference;
        public long id;

        public EmojiSound(long i, long ah, String fr) {
            this.id = i;
            this.accessHash = ah;
            this.fileReference = Base64.decode(fr, 8);
        }

        public EmojiSound(long i, long ah, byte[] fr) {
            this.id = i;
            this.accessHash = ah;
            this.fileReference = fr;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof EmojiSound)) {
                return false;
            }
            EmojiSound emojiSound = (EmojiSound) obj;
            return this.id == emojiSound.id && this.accessHash == emojiSound.accessHash && Arrays.equals(this.fileReference, emojiSound.fileReference);
        }
    }

    public void clearQueryTime() {
        this.lastServerQueryTime.clear();
        this.lastScheduledServerQueryTime.clear();
    }

    /* loaded from: classes4.dex */
    public static class DiceFrameSuccess {
        public int frame;
        public int num;

        public DiceFrameSuccess(int f, int n) {
            this.frame = f;
            this.num = n;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof DiceFrameSuccess)) {
                return false;
            }
            DiceFrameSuccess frameSuccess = (DiceFrameSuccess) obj;
            return this.frame == frameSuccess.frame && this.num == frameSuccess.num;
        }
    }

    /* loaded from: classes4.dex */
    public static class UserActionUpdatesSeq extends TLRPC.Updates {
        private UserActionUpdatesSeq() {
        }
    }

    /* loaded from: classes4.dex */
    public static class UserActionUpdatesPts extends TLRPC.Updates {
        private UserActionUpdatesPts() {
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    /* loaded from: classes4.dex */
    public static class ReadTask {
        public long dialogId;
        public int maxDate;
        public int maxId;
        public long replyId;
        public long sendRequestTime;

        private ReadTask() {
        }
    }

    /* loaded from: classes4.dex */
    public static class DialogFilter {
        private static int dialogFilterPointer = 10;
        public int flags;
        public int id;
        public int localId;
        public boolean locked;
        public String name;
        public int order;
        public volatile int pendingUnreadCount;
        public int unreadCount;
        public ArrayList<Long> alwaysShow = new ArrayList<>();
        public ArrayList<Long> neverShow = new ArrayList<>();
        public LongSparseIntArray pinnedDialogs = new LongSparseIntArray();
        public ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();

        public DialogFilter() {
            int i = dialogFilterPointer;
            dialogFilterPointer = i + 1;
            this.localId = i;
        }

        public boolean includesDialog(AccountInstance accountInstance, long dialogId) {
            MessagesController messagesController = accountInstance.getMessagesController();
            TLRPC.Dialog dialog = messagesController.dialogs_dict.get(dialogId);
            if (dialog == null) {
                return false;
            }
            return includesDialog(accountInstance, dialogId, dialog);
        }

        public boolean includesDialog(AccountInstance accountInstance, long dialogId, TLRPC.Dialog d) {
            TLRPC.Chat chat;
            if (this.neverShow.contains(Long.valueOf(dialogId))) {
                return false;
            }
            if (this.alwaysShow.contains(Long.valueOf(dialogId))) {
                return true;
            }
            if (d.folder_id != 0 && (this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
                return false;
            }
            MessagesController messagesController = accountInstance.getMessagesController();
            ContactsController contactsController = accountInstance.getContactsController();
            if (((this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0 && messagesController.isDialogMuted(d.id) && d.unread_mentions_count == 0) || ((this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0 && d.unread_count == 0 && !d.unread_mark && d.unread_mentions_count == 0)) {
                return false;
            }
            if (dialogId > 0) {
                TLRPC.User user = messagesController.getUser(Long.valueOf(dialogId));
                if (user != null) {
                    if (!user.bot) {
                        if (user.self || user.contact || contactsController.isContact(dialogId)) {
                            if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
                                return true;
                            }
                        } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
                            return true;
                        }
                    } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
                        return true;
                    }
                }
            } else if (dialogId < 0 && (chat = messagesController.getChat(Long.valueOf(-dialogId))) != null) {
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
                        return true;
                    }
                } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
                    return true;
                }
            }
            return false;
        }

        public boolean alwaysShow(int currentAccount, TLRPC.Dialog dialog) {
            TLRPC.EncryptedChat encryptedChat;
            if (dialog == null) {
                return false;
            }
            long dialogId = dialog.id;
            if (DialogObject.isEncryptedDialog(dialog.id) && (encryptedChat = MessagesController.getInstance(currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)))) != null) {
                dialogId = encryptedChat.user_id;
            }
            return this.alwaysShow.contains(Long.valueOf(dialogId));
        }

        public boolean isDefault() {
            return this.id == 0;
        }
    }

    /* renamed from: lambda$new$6$org-telegram-messenger-MessagesController */
    public /* synthetic */ int m761lambda$new$6$orgtelegrammessengerMessagesController(TLRPC.Dialog dialog1, TLRPC.Dialog dialog2) {
        int pinnedNum1 = this.sortingDialogFilter.pinnedDialogs.get(dialog1.id, Integer.MIN_VALUE);
        int pinnedNum2 = this.sortingDialogFilter.pinnedDialogs.get(dialog2.id, Integer.MIN_VALUE);
        if (!(dialog1 instanceof TLRPC.TL_dialogFolder) || (dialog2 instanceof TLRPC.TL_dialogFolder)) {
            if (!(dialog1 instanceof TLRPC.TL_dialogFolder) && (dialog2 instanceof TLRPC.TL_dialogFolder)) {
                return 1;
            }
            if (pinnedNum1 == Integer.MIN_VALUE && pinnedNum2 != Integer.MIN_VALUE) {
                return 1;
            }
            if (pinnedNum1 != Integer.MIN_VALUE && pinnedNum2 == Integer.MIN_VALUE) {
                return -1;
            }
            if (pinnedNum1 != Integer.MIN_VALUE) {
                if (pinnedNum1 > pinnedNum2) {
                    return 1;
                }
                return pinnedNum1 < pinnedNum2 ? -1 : 0;
            }
            MediaDataController mediaDataController = getMediaDataController();
            long date1 = DialogObject.getLastMessageOrDraftDate(dialog1, mediaDataController.getDraft(dialog1.id, 0));
            long date2 = DialogObject.getLastMessageOrDraftDate(dialog2, mediaDataController.getDraft(dialog2.id, 0));
            if (date1 < date2) {
                return 1;
            }
            return date1 > date2 ? -1 : 0;
        }
        return -1;
    }

    /* renamed from: lambda$new$7$org-telegram-messenger-MessagesController */
    public /* synthetic */ int m762lambda$new$7$orgtelegrammessengerMessagesController(TLRPC.Dialog dialog1, TLRPC.Dialog dialog2) {
        if (!(dialog1 instanceof TLRPC.TL_dialogFolder) || (dialog2 instanceof TLRPC.TL_dialogFolder)) {
            if (!(dialog1 instanceof TLRPC.TL_dialogFolder) && (dialog2 instanceof TLRPC.TL_dialogFolder)) {
                return 1;
            }
            if (!dialog1.pinned && dialog2.pinned) {
                return 1;
            }
            if (dialog1.pinned && !dialog2.pinned) {
                return -1;
            }
            if (dialog1.pinned) {
                if (dialog1.pinnedNum < dialog2.pinnedNum) {
                    return 1;
                }
                return dialog1.pinnedNum > dialog2.pinnedNum ? -1 : 0;
            }
            MediaDataController mediaDataController = getMediaDataController();
            long date1 = DialogObject.getLastMessageOrDraftDate(dialog1, mediaDataController.getDraft(dialog1.id, 0));
            long date2 = DialogObject.getLastMessageOrDraftDate(dialog2, mediaDataController.getDraft(dialog2.id, 0));
            if (date1 < date2) {
                return 1;
            }
            return date1 > date2 ? -1 : 0;
        }
        return -1;
    }

    /* renamed from: lambda$new$8$org-telegram-messenger-MessagesController */
    public /* synthetic */ int m763lambda$new$8$orgtelegrammessengerMessagesController(TLRPC.Update lhs, TLRPC.Update rhs) {
        int ltype = getUpdateType(lhs);
        int rtype = getUpdateType(rhs);
        if (ltype != rtype) {
            return AndroidUtilities.compare(ltype, rtype);
        }
        if (ltype == 0) {
            return AndroidUtilities.compare(getUpdatePts(lhs), getUpdatePts(rhs));
        }
        if (ltype == 1) {
            return AndroidUtilities.compare(getUpdateQts(lhs), getUpdateQts(rhs));
        }
        if (ltype == 2) {
            long lChannel = getUpdateChannelId(lhs);
            long rChannel = getUpdateChannelId(rhs);
            if (lChannel == rChannel) {
                return AndroidUtilities.compare(getUpdatePts(lhs), getUpdatePts(rhs));
            }
            return AndroidUtilities.compare(lChannel, rChannel);
        }
        return 0;
    }

    public static MessagesController getInstance(int num) {
        MessagesController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (lockObjects[num]) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    MessagesController[] messagesControllerArr = Instance;
                    MessagesController messagesController = new MessagesController(num);
                    localInstance = messagesController;
                    messagesControllerArr[num] = messagesController;
                }
            }
        }
        return localInstance;
    }

    public static SharedPreferences getNotificationsSettings(int account) {
        return getInstance(account).notificationsPreferences;
    }

    public static SharedPreferences getGlobalNotificationsSettings() {
        return getInstance(0).notificationsPreferences;
    }

    public static SharedPreferences getMainSettings(int account) {
        return getInstance(account).mainPreferences;
    }

    public static SharedPreferences getGlobalMainSettings() {
        return getInstance(0).mainPreferences;
    }

    public static SharedPreferences getEmojiSettings(int account) {
        return getInstance(account).emojiPreferences;
    }

    public static SharedPreferences getGlobalEmojiSettings() {
        return getInstance(0).emojiPreferences;
    }

    public MessagesController(int num) {
        super(num);
        boolean z = true;
        ImageLoader.getInstance();
        getMessagesStorage();
        getLocationController();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda155
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m764lambda$new$9$orgtelegrammessengerMessagesController();
            }
        });
        addSupportUser();
        if (this.currentAccount == 0) {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.notificationsPreferences = context.getSharedPreferences("Notifications" + this.currentAccount, 0);
            Context context2 = ApplicationLoader.applicationContext;
            this.mainPreferences = context2.getSharedPreferences("mainconfig" + this.currentAccount, 0);
            Context context3 = ApplicationLoader.applicationContext;
            this.emojiPreferences = context3.getSharedPreferences("emoji" + this.currentAccount, 0);
        }
        this.enableJoined = this.notificationsPreferences.getBoolean("EnableContactJoined", true);
        this.remoteConfigLoaded = this.mainPreferences.getBoolean("remoteConfigLoaded", false);
        this.secretWebpagePreview = this.mainPreferences.getInt("secretWebpage2", 2);
        this.maxGroupCount = this.mainPreferences.getInt("maxGroupCount", 200);
        this.maxMegagroupCount = this.mainPreferences.getInt("maxMegagroupCount", 10000);
        this.maxRecentGifsCount = this.mainPreferences.getInt("maxRecentGifsCount", 200);
        this.maxRecentStickersCount = this.mainPreferences.getInt("maxRecentStickersCount", 30);
        this.maxFaveStickersCount = this.mainPreferences.getInt("maxFaveStickersCount", 5);
        this.maxEditTime = this.mainPreferences.getInt("maxEditTime", 3600);
        this.ratingDecay = this.mainPreferences.getInt("ratingDecay", 2419200);
        this.linkPrefix = this.mainPreferences.getString("linkPrefix", "t.me");
        this.callReceiveTimeout = this.mainPreferences.getInt("callReceiveTimeout", Indexable.MAX_STRING_LENGTH);
        this.callRingTimeout = this.mainPreferences.getInt("callRingTimeout", 90000);
        this.callConnectTimeout = this.mainPreferences.getInt("callConnectTimeout", Indexable.MAX_BYTE_SIZE);
        this.callPacketTimeout = this.mainPreferences.getInt("callPacketTimeout", 10000);
        this.updateCheckDelay = this.mainPreferences.getInt("updateCheckDelay", 86400);
        this.maxPinnedDialogsCount = this.mainPreferences.getInt("maxPinnedDialogsCount", 5);
        this.maxFolderPinnedDialogsCount = this.mainPreferences.getInt("maxFolderPinnedDialogsCount", 100);
        this.maxMessageLength = this.mainPreferences.getInt("maxMessageLength", 4096);
        this.maxCaptionLength = this.mainPreferences.getInt("maxCaptionLength", 1024);
        this.mapProvider = this.mainPreferences.getInt("mapProvider", 0);
        this.availableMapProviders = this.mainPreferences.getInt("availableMapProviders", 3);
        this.mapKey = this.mainPreferences.getString("pk", null);
        this.installReferer = this.mainPreferences.getString("installReferer", null);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
        this.youtubePipType = this.mainPreferences.getString("youtubePipType", "disabled");
        this.keepAliveService = this.mainPreferences.getBoolean("keepAliveService", false);
        this.backgroundConnection = this.mainPreferences.getBoolean("keepAliveService", false);
        this.promoDialogId = this.mainPreferences.getLong("proxy_dialog", 0L);
        this.nextPromoInfoCheckTime = this.mainPreferences.getInt("nextPromoInfoCheckTime", 0);
        this.promoDialogType = this.mainPreferences.getInt("promo_dialog_type", 0);
        this.promoPsaMessage = this.mainPreferences.getString("promo_psa_message", null);
        this.promoPsaType = this.mainPreferences.getString("promo_psa_type", null);
        this.proxyDialogAddress = this.mainPreferences.getString("proxyDialogAddress", null);
        this.nextTosCheckTime = this.notificationsPreferences.getInt("nextTosCheckTime", 0);
        this.venueSearchBot = this.mainPreferences.getString("venueSearchBot", "foursquare");
        this.gifSearchBot = this.mainPreferences.getString("gifSearchBot", "gif");
        this.imageSearchBot = this.mainPreferences.getString("imageSearchBot", "pic");
        this.blockedCountry = this.mainPreferences.getBoolean("blockedCountry", false);
        this.dcDomainName = this.mainPreferences.getString("dcDomainName2", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv3.stel.com" : "apv3.stel.com");
        this.webFileDatacenterId = this.mainPreferences.getInt("webFileDatacenterId", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? 2 : 4);
        this.suggestedLangCode = this.mainPreferences.getString("suggestedLangCode", "en");
        this.animatedEmojisZoom = this.mainPreferences.getFloat("animatedEmojisZoom", 0.625f);
        this.qrLoginCamera = this.mainPreferences.getBoolean("qrLoginCamera", false);
        this.saveGifsWithStickers = this.mainPreferences.getBoolean("saveGifsWithStickers", false);
        this.filtersEnabled = this.mainPreferences.getBoolean("filtersEnabled", false);
        this.getfileExperimentalParams = this.mainPreferences.getBoolean("getfileExperimentalParams", false);
        this.showFiltersTooltip = this.mainPreferences.getBoolean("showFiltersTooltip", false);
        this.autoarchiveAvailable = this.mainPreferences.getBoolean("autoarchiveAvailable", false);
        this.groupCallVideoMaxParticipants = this.mainPreferences.getInt("groipCallVideoMaxParticipants", 30);
        this.chatReadMarkSizeThreshold = this.mainPreferences.getInt("chatReadMarkSizeThreshold", 100);
        this.chatReadMarkExpirePeriod = this.mainPreferences.getInt("chatReadMarkExpirePeriod", 604800);
        this.ringtoneDurationMax = this.mainPreferences.getInt("ringtoneDurationMax", 5);
        this.ringtoneSizeMax = this.mainPreferences.getInt("ringtoneSizeMax", 102400);
        this.chatReadMarkExpirePeriod = this.mainPreferences.getInt("chatReadMarkExpirePeriod", 604800);
        this.suggestStickersApiOnly = this.mainPreferences.getBoolean("suggestStickersApiOnly", false);
        this.roundVideoSize = this.mainPreferences.getInt("roundVideoSize", 384);
        this.roundVideoBitrate = this.mainPreferences.getInt("roundVideoBitrate", 1000);
        this.roundAudioBitrate = this.mainPreferences.getInt("roundAudioBitrate", 64);
        this.pendingSuggestions = this.mainPreferences.getStringSet("pendingSuggestions", null);
        int i = this.mainPreferences.getInt("channelsLimitDefault", 500);
        this.channelsLimitDefault = i;
        this.channelsLimitPremium = this.mainPreferences.getInt("channelsLimitPremium", i * 2);
        this.savedGifsLimitDefault = this.mainPreferences.getInt("savedGifsLimitDefault", 200);
        this.savedGifsLimitPremium = this.mainPreferences.getInt("savedGifsLimitPremium", 400);
        this.stickersFavedLimitDefault = this.mainPreferences.getInt("stickersFavedLimitDefault", 5);
        this.stickersFavedLimitPremium = this.mainPreferences.getInt("stickersFavedLimitPremium", 200);
        this.dialogFiltersLimitDefault = this.mainPreferences.getInt("dialogFiltersLimitDefault", 10);
        this.dialogFiltersLimitPremium = this.mainPreferences.getInt("dialogFiltersLimitPremium", 20);
        this.dialogFiltersChatsLimitDefault = this.mainPreferences.getInt("dialogFiltersChatsLimitDefault", 100);
        this.dialogFiltersChatsLimitPremium = this.mainPreferences.getInt("dialogFiltersChatsLimitPremium", 200);
        this.dialogFiltersPinnedLimitDefault = this.mainPreferences.getInt("dialogFiltersPinnedLimitDefault", 5);
        this.dialogFiltersPinnedLimitPremium = this.mainPreferences.getInt("dialogFiltersPinnedLimitPremium", 10);
        this.publicLinksLimitDefault = this.mainPreferences.getInt("publicLinksLimitDefault", 10);
        this.publicLinksLimitPremium = this.mainPreferences.getInt("publicLinksLimitPremium", 20);
        this.captionLengthLimitDefault = this.mainPreferences.getInt("captionLengthLimitDefault", 1024);
        this.captionLengthLimitPremium = this.mainPreferences.getInt("captionLengthLimitPremium", 4096);
        this.aboutLengthLimitDefault = this.mainPreferences.getInt("aboutLengthLimitDefault", 70);
        this.aboutLengthLimitPremium = this.mainPreferences.getInt("aboutLengthLimitPremium", 140);
        int i2 = this.mainPreferences.getInt("uploadMaxFileParts", WearableStatusCodes.TARGET_NODE_NOT_CONNECTED);
        this.uploadMaxFileParts = i2;
        this.uploadMaxFilePartsPremium = this.mainPreferences.getInt("uploadMaxFilePartsPremium", i2 * 2);
        this.premiumInvoiceSlug = this.mainPreferences.getString("premiumInvoiceSlug", null);
        this.premiumBotUsername = this.mainPreferences.getString("premiumBotUsername", null);
        this.premiumLocked = this.mainPreferences.getBoolean("premiumLocked", false);
        loadPremiumFeaturesPreviewOrder(this.mainPreferences.getString("premiumFeaturesTypesToPosition", null));
        if (this.pendingSuggestions != null) {
            this.pendingSuggestions = new HashSet(this.pendingSuggestions);
        } else {
            this.pendingSuggestions = new HashSet();
        }
        Set<String> stringSet = this.mainPreferences.getStringSet("exportUri2", null);
        this.exportUri = stringSet;
        if (stringSet != null) {
            this.exportUri = new HashSet(this.exportUri);
        } else {
            HashSet hashSet = new HashSet();
            this.exportUri = hashSet;
            hashSet.add("content://(\\d+@)?com\\.whatsapp\\.provider\\.media/export_chat/");
            this.exportUri.add("content://(\\d+@)?com\\.whatsapp\\.w4b\\.provider\\.media/export_chat/");
            this.exportUri.add("content://jp\\.naver\\.line\\.android\\.line\\.common\\.FileProvider/export-chat/");
            this.exportUri.add(".*WhatsApp.*\\.txt$");
        }
        Set<String> stringSet2 = this.mainPreferences.getStringSet("exportGroupUri", null);
        this.exportGroupUri = stringSet2;
        if (stringSet2 != null) {
            this.exportGroupUri = new HashSet(this.exportGroupUri);
        } else {
            HashSet hashSet2 = new HashSet();
            this.exportGroupUri = hashSet2;
            hashSet2.add("@g.us/");
        }
        Set<String> stringSet3 = this.mainPreferences.getStringSet("exportPrivateUri", null);
        this.exportPrivateUri = stringSet3;
        if (stringSet3 != null) {
            this.exportPrivateUri = new HashSet(this.exportPrivateUri);
        } else {
            HashSet hashSet3 = new HashSet();
            this.exportPrivateUri = hashSet3;
            hashSet3.add("@s.whatsapp.net/");
        }
        Set<String> stringSet4 = this.mainPreferences.getStringSet("autologinDomains", null);
        this.autologinDomains = stringSet4;
        if (stringSet4 != null) {
            this.autologinDomains = new HashSet(this.autologinDomains);
        } else {
            this.autologinDomains = new HashSet();
        }
        Set<String> stringSet5 = this.mainPreferences.getStringSet("authDomains", null);
        this.authDomains = stringSet5;
        if (stringSet5 != null) {
            this.authDomains = new HashSet(this.authDomains);
        } else {
            this.authDomains = new HashSet();
        }
        this.autologinToken = this.mainPreferences.getString("autologinToken", null);
        Set<String> emojies = this.mainPreferences.getStringSet("diceEmojies", null);
        if (emojies != null) {
            this.diceEmojies = new HashSet<>(emojies);
        } else {
            HashSet<String> hashSet4 = new HashSet<>();
            this.diceEmojies = hashSet4;
            hashSet4.add("🎲");
            this.diceEmojies.add("🎯");
        }
        String text = this.mainPreferences.getString("diceSuccess", null);
        if (text == null) {
            this.diceSuccess.put("🎯", new DiceFrameSuccess(62, 6));
        } else {
            try {
                byte[] bytes = Base64.decode(text, 0);
                if (bytes != null) {
                    SerializedData data = new SerializedData(bytes);
                    int count = data.readInt32(true);
                    for (int a = 0; a < count; a++) {
                        this.diceSuccess.put(data.readString(true), new DiceFrameSuccess(data.readInt32(true), data.readInt32(true)));
                    }
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        String text2 = this.mainPreferences.getString("emojiSounds", null);
        if (text2 != null) {
            try {
                byte[] bytes2 = Base64.decode(text2, 0);
                if (bytes2 != null) {
                    SerializedData data2 = new SerializedData(bytes2);
                    int count2 = data2.readInt32(true);
                    int a2 = 0;
                    while (a2 < count2) {
                        this.emojiSounds.put(data2.readString(z), new EmojiSound(data2.readInt64(z), data2.readInt64(z), data2.readByteArray(z)));
                        a2++;
                        z = true;
                    }
                    data2.cleanup();
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        String text3 = this.mainPreferences.getString("gifSearchEmojies", null);
        if (text3 == null) {
            this.gifSearchEmojies.add("👍");
            this.gifSearchEmojies.add("👎");
            this.gifSearchEmojies.add("😍");
            this.gifSearchEmojies.add("😂");
            this.gifSearchEmojies.add("😮");
            this.gifSearchEmojies.add("🙄");
            this.gifSearchEmojies.add("😥");
            this.gifSearchEmojies.add("😡");
            this.gifSearchEmojies.add("🥳");
            this.gifSearchEmojies.add("😎");
        } else {
            try {
                byte[] bytes3 = Base64.decode(text3, 0);
                if (bytes3 != null) {
                    SerializedData data3 = new SerializedData(bytes3);
                    int count3 = data3.readInt32(true);
                    for (int a3 = 0; a3 < count3; a3++) {
                        this.gifSearchEmojies.add(data3.readString(true));
                    }
                    data3.cleanup();
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
        }
        if (BuildVars.DEBUG_VERSION) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda144
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m760lambda$new$10$orgtelegrammessengerMessagesController();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    /* renamed from: lambda$new$9$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m764lambda$new$9$orgtelegrammessengerMessagesController() {
        MessagesController messagesController = getMessagesController();
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploaded);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploadFailed);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileLoadFailed);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.messageReceivedByServer);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.updateMessageMedia);
    }

    private void sendLoadPeersRequest(final TLObject req, final ArrayList<TLObject> requests, final TLRPC.messages_Dialogs pinnedDialogs, final TLRPC.messages_Dialogs pinnedRemoteDialogs, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final ArrayList<DialogFilter> filtersToSave, final SparseArray<DialogFilter> filtersToDelete, final ArrayList<Integer> filtersOrder, final HashMap<Integer, HashSet<Long>> filterDialogRemovals, final HashMap<Integer, HashSet<Long>> filterUserRemovals, final HashSet<Integer> filtersUnreadCounterReset) {
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda241
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m843x2be7911c(chats, users, pinnedDialogs, pinnedRemoteDialogs, requests, req, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$sendLoadPeersRequest$11$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m843x2be7911c(ArrayList chats, ArrayList users, TLRPC.messages_Dialogs pinnedDialogs, TLRPC.messages_Dialogs pinnedRemoteDialogs, ArrayList requests, TLObject req, ArrayList filtersToSave, SparseArray filtersToDelete, ArrayList filtersOrder, HashMap filterDialogRemovals, HashMap filterUserRemovals, HashSet filtersUnreadCounterReset, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_messages_chats) {
            chats.addAll(((TLRPC.TL_messages_chats) response).chats);
        } else if (response instanceof TLRPC.Vector) {
            TLRPC.Vector res = (TLRPC.Vector) response;
            int N = res.objects.size();
            for (int a = 0; a < N; a++) {
                TLRPC.User user = (TLRPC.User) res.objects.get(a);
                users.add(user);
            }
        } else if (response instanceof TLRPC.TL_messages_peerDialogs) {
            TLRPC.TL_messages_peerDialogs peerDialogs = (TLRPC.TL_messages_peerDialogs) response;
            pinnedDialogs.dialogs.addAll(peerDialogs.dialogs);
            pinnedDialogs.messages.addAll(peerDialogs.messages);
            pinnedRemoteDialogs.dialogs.addAll(peerDialogs.dialogs);
            pinnedRemoteDialogs.messages.addAll(peerDialogs.messages);
            users.addAll(peerDialogs.users);
            chats.addAll(peerDialogs.chats);
        }
        requests.remove(req);
        if (requests.isEmpty()) {
            getMessagesStorage().processLoadedFilterPeers(pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
        }
    }

    public void loadFilterPeers(final HashMap<Long, TLRPC.InputPeer> dialogsToLoadMap, final HashMap<Long, TLRPC.InputPeer> usersToLoadMap, final HashMap<Long, TLRPC.InputPeer> chatsToLoadMap, final TLRPC.messages_Dialogs pinnedDialogs, final TLRPC.messages_Dialogs pinnedRemoteDialogs, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final ArrayList<DialogFilter> filtersToSave, final SparseArray<DialogFilter> filtersToDelete, final ArrayList<Integer> filtersOrder, final HashMap<Integer, HashSet<Long>> filterDialogRemovals, final HashMap<Integer, HashSet<Long>> filterUserRemovals, final HashSet<Integer> filtersUnreadCounterReset) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m710x7865f450(usersToLoadMap, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset, chatsToLoadMap, dialogsToLoadMap);
            }
        });
    }

    /* renamed from: lambda$loadFilterPeers$12$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m710x7865f450(HashMap usersToLoadMap, TLRPC.messages_Dialogs pinnedDialogs, TLRPC.messages_Dialogs pinnedRemoteDialogs, ArrayList users, ArrayList chats, ArrayList filtersToSave, SparseArray filtersToDelete, ArrayList filtersOrder, HashMap filterDialogRemovals, HashMap filterUserRemovals, HashSet filtersUnreadCounterReset, HashMap chatsToLoadMap, HashMap dialogsToLoadMap) {
        int i;
        TLRPC.TL_messages_getPeerDialogs req4;
        TLRPC.TL_channels_getChannels req3;
        TLRPC.TL_messages_getChats req2;
        ArrayList<TLObject> requests = new ArrayList<>();
        TLRPC.TL_users_getUsers req = null;
        for (Map.Entry<Long, TLRPC.InputPeer> entry : usersToLoadMap.entrySet()) {
            if (req == null) {
                req = new TLRPC.TL_users_getUsers();
                requests.add(req);
            }
            req.id.add(getInputUser(entry.getValue()));
            if (req.id.size() == 100) {
                sendLoadPeersRequest(req, requests, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
                req = null;
            }
        }
        if (req == null) {
            i = 100;
        } else {
            i = 100;
            sendLoadPeersRequest(req, requests, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
        }
        TLRPC.TL_messages_getChats req22 = null;
        TLRPC.TL_channels_getChannels req32 = null;
        for (Map.Entry<Long, TLRPC.InputPeer> entry2 : chatsToLoadMap.entrySet()) {
            TLRPC.InputPeer inputPeer = entry2.getValue();
            if (inputPeer.chat_id != 0) {
                if (req22 != null) {
                    req2 = req22;
                } else {
                    TLRPC.TL_messages_getChats req23 = new TLRPC.TL_messages_getChats();
                    requests.add(req23);
                    req2 = req23;
                }
                req2.id.add(entry2.getKey());
                if (req2.id.size() != i) {
                    req22 = req2;
                } else {
                    sendLoadPeersRequest(req2, requests, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
                    req22 = null;
                }
            } else if (inputPeer.channel_id != 0) {
                if (req32 != null) {
                    req3 = req32;
                } else {
                    TLRPC.TL_channels_getChannels req33 = new TLRPC.TL_channels_getChannels();
                    requests.add(req33);
                    req3 = req33;
                }
                req3.id.add(getInputChannel(inputPeer));
                if (req3.id.size() != 100) {
                    req32 = req3;
                } else {
                    sendLoadPeersRequest(req3, requests, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
                    req32 = null;
                }
            }
            i = 100;
        }
        if (req22 != null) {
            sendLoadPeersRequest(req22, requests, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
        }
        if (req32 != null) {
            sendLoadPeersRequest(req32, requests, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
        }
        TLRPC.TL_messages_getPeerDialogs req42 = null;
        for (Map.Entry<Long, TLRPC.InputPeer> entry3 : dialogsToLoadMap.entrySet()) {
            if (req42 != null) {
                req4 = req42;
            } else {
                TLRPC.TL_messages_getPeerDialogs req43 = new TLRPC.TL_messages_getPeerDialogs();
                requests.add(req43);
                req4 = req43;
            }
            TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            inputDialogPeer.peer = entry3.getValue();
            req4.peers.add(inputDialogPeer);
            if (req4.peers.size() == 100) {
                sendLoadPeersRequest(req4, requests, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
                req42 = null;
            } else {
                req42 = req4;
            }
        }
        if (req42 != null) {
            sendLoadPeersRequest(req42, requests, pinnedDialogs, pinnedRemoteDialogs, users, chats, filtersToSave, filtersToDelete, filtersOrder, filterDialogRemovals, filterUserRemovals, filtersUnreadCounterReset);
        }
    }

    public void processLoadedDialogFilters(final ArrayList<DialogFilter> filters, final TLRPC.messages_Dialogs pinnedDialogs, final TLRPC.messages_Dialogs pinnedRemoteDialogs, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final ArrayList<TLRPC.EncryptedChat> encryptedChats, final int remote) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda104
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m781x76092fcd(pinnedDialogs, encryptedChats, pinnedRemoteDialogs, remote, filters, users, chats);
            }
        });
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> */
    /* JADX WARN: Removed duplicated region for block: B:81:0x01c3  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x01f1  */
    /* renamed from: lambda$processLoadedDialogFilters$15$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m781x76092fcd(org.telegram.tgnet.TLRPC.messages_Dialogs r21, final java.util.ArrayList r22, final org.telegram.tgnet.TLRPC.messages_Dialogs r23, final int r24, final java.util.ArrayList r25, final java.util.ArrayList r26, final java.util.ArrayList r27) {
        /*
            Method dump skipped, instructions count: 740
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m781x76092fcd(org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, org.telegram.tgnet.TLRPC$messages_Dialogs, int, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList):void");
    }

    /* renamed from: lambda$processLoadedDialogFilters$14$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m780x90c7c10c(int remote, ArrayList filters, ArrayList users, ArrayList chats, TLRPC.messages_Dialogs pinnedRemoteDialogs, ArrayList encryptedChats, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage) {
        TLRPC.messages_Dialogs messages_dialogs = pinnedRemoteDialogs;
        LongSparseArray longSparseArray = new_dialogs_dict;
        if (remote != 2) {
            this.dialogFilters = filters;
            this.dialogFiltersById.clear();
            int N = this.dialogFilters.size();
            for (int a = 0; a < N; a++) {
                DialogFilter filter = this.dialogFilters.get(a);
                this.dialogFiltersById.put(filter.id, filter);
            }
            Collections.sort(this.dialogFilters, MessagesController$$ExternalSyntheticLambda138.INSTANCE);
            putUsers(users, true);
            putChats(chats, true);
            this.dialogFiltersLoaded = true;
            getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            if (remote == 0) {
                loadRemoteFilters(false);
            }
            if (messages_dialogs != null && !messages_dialogs.dialogs.isEmpty()) {
                applyDialogsNotificationsSettings(messages_dialogs.dialogs);
            }
            if (encryptedChats != null) {
                for (int a2 = 0; a2 < encryptedChats.size(); a2++) {
                    TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) encryptedChats.get(a2);
                    if ((encryptedChat instanceof TLRPC.TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < SecretChatHelper.CURRENT_SECRET_CHAT_LAYER) {
                        getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, null);
                    }
                    putEncryptedChat(encryptedChat, true);
                }
            }
            int a3 = 0;
            while (a3 < new_dialogs_dict.size()) {
                long key = longSparseArray.keyAt(a3);
                TLRPC.Dialog value = (TLRPC.Dialog) longSparseArray.valueAt(a3);
                TLRPC.Dialog currentDialog = this.dialogs_dict.get(key);
                if (messages_dialogs != null && messages_dialogs.dialogs.contains(value)) {
                    if (value.draft instanceof TLRPC.TL_draftMessage) {
                        getMediaDataController().saveDraft(value.id, 0, value.draft, null, false);
                    }
                    if (currentDialog != null) {
                        currentDialog.notify_settings = value.notify_settings;
                    }
                }
                MessageObject newMsg = (MessageObject) new_dialogMessage.get(value.id);
                if (currentDialog == null) {
                    this.dialogs_dict.put(key, value);
                    this.dialogMessage.put(key, newMsg);
                    if (newMsg != null && newMsg.messageOwner.peer_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                        if (newMsg.messageOwner.random_id != 0) {
                            this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                        }
                    }
                } else {
                    currentDialog.pinned = value.pinned;
                    currentDialog.pinnedNum = value.pinnedNum;
                    MessageObject oldMsg = this.dialogMessage.get(key);
                    if ((oldMsg != null && oldMsg.deleted) || oldMsg == null || currentDialog.top_message > 0) {
                        if (value.top_message >= currentDialog.top_message) {
                            this.dialogs_dict.put(key, value);
                            this.dialogMessage.put(key, newMsg);
                            if (oldMsg != null) {
                                if (oldMsg.messageOwner.peer_id.channel_id == 0) {
                                    this.dialogMessagesByIds.remove(oldMsg.getId());
                                }
                                if (oldMsg.messageOwner.random_id != 0) {
                                    this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                }
                            }
                            if (newMsg != null && newMsg.messageOwner.peer_id.channel_id == 0) {
                                if (oldMsg != null && oldMsg.getId() == newMsg.getId()) {
                                    newMsg.deleted = oldMsg.deleted;
                                }
                                this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                                if (newMsg.messageOwner.random_id != 0) {
                                    this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                                }
                            }
                        }
                    } else if (newMsg == null || newMsg.messageOwner.date > oldMsg.messageOwner.date) {
                        this.dialogs_dict.put(key, value);
                        this.dialogMessage.put(key, newMsg);
                        if (oldMsg.messageOwner.peer_id.channel_id == 0) {
                            this.dialogMessagesByIds.remove(oldMsg.getId());
                        }
                        if (newMsg != null) {
                            if (oldMsg.getId() == newMsg.getId()) {
                                newMsg.deleted = oldMsg.deleted;
                            }
                            if (newMsg.messageOwner.peer_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                                if (newMsg.messageOwner.random_id != 0) {
                                    this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                                }
                            }
                        }
                        if (oldMsg.messageOwner.random_id != 0) {
                            this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                        }
                    }
                }
                a3++;
                messages_dialogs = pinnedRemoteDialogs;
                longSparseArray = new_dialogs_dict;
            }
            this.allDialogs.clear();
            int size = this.dialogs_dict.size();
            for (int a4 = 0; a4 < size; a4++) {
                TLRPC.Dialog dialog = this.dialogs_dict.valueAt(a4);
                if (this.deletingDialogs.indexOfKey(dialog.id) < 0) {
                    this.allDialogs.add(dialog);
                }
            }
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        if (remote != 0) {
            getUserConfig().filtersLoaded = true;
            getUserConfig().saveConfig(false);
            this.loadingRemoteFilters = false;
            getNotificationCenter().postNotificationName(NotificationCenter.filterSettingsUpdated, new Object[0]);
        }
        lockFiltersInternal();
    }

    public static /* synthetic */ int lambda$processLoadedDialogFilters$13(DialogFilter o1, DialogFilter o2) {
        if (o1.order > o2.order) {
            return 1;
        }
        if (o1.order < o2.order) {
            return -1;
        }
        return 0;
    }

    public void loadSuggestedFilters() {
        if (this.loadingSuggestedFilters) {
            return;
        }
        this.loadingSuggestedFilters = true;
        TLRPC.TL_messages_getSuggestedDialogFilters req = new TLRPC.TL_messages_getSuggestedDialogFilters();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda160
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m739x3e7ea830(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadSuggestedFilters$17$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m739x3e7ea830(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m738x593d396f(response);
            }
        });
    }

    /* renamed from: lambda$loadSuggestedFilters$16$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m738x593d396f(TLObject response) {
        this.loadingSuggestedFilters = false;
        this.suggestedFilters.clear();
        if (response instanceof TLRPC.Vector) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            int N = vector.objects.size();
            for (int a = 0; a < N; a++) {
                this.suggestedFilters.add((TLRPC.TL_dialogFilterSuggested) vector.objects.get(a));
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.suggestedFiltersLoaded, new Object[0]);
    }

    public void loadRemoteFilters(boolean force) {
        if (this.loadingRemoteFilters || !getUserConfig().isClientActivated()) {
            return;
        }
        if (!force && getUserConfig().filtersLoaded) {
            return;
        }
        if (force) {
            getUserConfig().filtersLoaded = false;
            getUserConfig().saveConfig(false);
        }
        TLRPC.TL_messages_getDialogFilters req = new TLRPC.TL_messages_getDialogFilters();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda158
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m735xe3f763b(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadRemoteFilters$19$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m735xe3f763b(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.Vector) {
            getMessagesStorage().checkLoadedRemoteFilters((TLRPC.Vector) response);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda99
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m734x28fe077a();
                }
            });
        }
    }

    /* renamed from: lambda$loadRemoteFilters$18$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m734x28fe077a() {
        this.loadingRemoteFilters = false;
    }

    public void selectDialogFilter(DialogFilter filter, int index) {
        DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
        if (dialogFilterArr[index] == filter) {
            return;
        }
        DialogFilter prevFilter = dialogFilterArr[index];
        dialogFilterArr[index] = filter;
        char c = 1;
        if (dialogFilterArr[index == 0 ? (char) 1 : (char) 0] == filter) {
            if (index != 0) {
                c = 0;
            }
            dialogFilterArr[c] = null;
        }
        if (dialogFilterArr[index] == null) {
            if (prevFilter != null) {
                prevFilter.dialogs.clear();
                return;
            }
            return;
        }
        sortDialogs(null);
    }

    public void onFilterUpdate(DialogFilter filter) {
        for (int a = 0; a < 2; a++) {
            if (this.selectedDialogFilter[a] == filter) {
                sortDialogs(null);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
                return;
            }
        }
    }

    public void addFilter(DialogFilter filter, boolean atBegin) {
        if (atBegin) {
            int order = 254;
            int N = this.dialogFilters.size();
            for (int a = 0; a < N; a++) {
                order = Math.min(order, this.dialogFilters.get(a).order);
            }
            int a2 = order - 1;
            filter.order = a2;
            this.dialogFilters.add(0, filter);
        } else {
            int order2 = 0;
            int N2 = this.dialogFilters.size();
            for (int a3 = 0; a3 < N2; a3++) {
                order2 = Math.max(order2, this.dialogFilters.get(a3).order);
            }
            int a4 = order2 + 1;
            filter.order = a4;
            this.dialogFilters.add(filter);
        }
        this.dialogFiltersById.put(filter.id, filter);
        if (this.dialogFilters.size() == 1 && SharedConfig.getChatSwipeAction(this.currentAccount) != 5) {
            SharedConfig.updateChatListSwipeSetting(5);
        }
        lockFiltersInternal();
    }

    public void removeFilter(DialogFilter filter) {
        this.dialogFilters.remove(filter);
        this.dialogFiltersById.remove(filter.id);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
    }

    /* renamed from: loadAppConfig */
    public void m760lambda$new$10$orgtelegrammessengerMessagesController() {
        if (this.loadingAppConfig) {
            return;
        }
        this.loadingAppConfig = true;
        TLRPC.TL_help_getAppConfig req = new TLRPC.TL_help_getAppConfig();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda153
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m702xd0b5e9e4(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadAppConfig$21$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m702xd0b5e9e4(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m701xeb747b23(response);
            }
        });
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: lambda$loadAppConfig$20$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m701xeb747b23(TLObject response) {
        char c;
        TLRPC.TL_jsonObject object;
        int N;
        Exception e;
        boolean keelAliveChanged;
        int a;
        TLRPC.TL_jsonObject jsonObject;
        TLRPC.TL_jsonObject jsonObject2;
        Exception e2;
        boolean keelAliveChanged2;
        TLRPC.TL_jsonObjectValue value;
        boolean keelAliveChanged3;
        TLRPC.TL_jsonObject jsonObject3;
        int c2;
        int a2;
        TLRPC.TL_jsonObjectValue value2;
        char c3;
        if (response instanceof TLRPC.TL_jsonObject) {
            SharedPreferences.Editor editor = this.mainPreferences.edit();
            TLRPC.TL_jsonObject object2 = (TLRPC.TL_jsonObject) response;
            int N2 = object2.value.size();
            int a3 = 0;
            boolean keelAliveChanged4 = false;
            boolean changed = false;
            while (a3 < N2) {
                TLRPC.TL_jsonObjectValue value3 = object2.value.get(a3);
                String str = value3.key;
                switch (str.hashCode()) {
                    case -2086426873:
                        if (str.equals("dialog_filters_pinned_limit_premium")) {
                            c = ',';
                            break;
                        }
                        c = 65535;
                        break;
                    case -1906216435:
                        if (str.equals("upload_max_fileparts_default")) {
                            c = '-';
                            break;
                        }
                        c = 65535;
                        break;
                    case -1688620344:
                        if (str.equals("dialog_filters_tooltip")) {
                            c = 7;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1683918311:
                        if (str.equals("qr_login_camera")) {
                            c = 11;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1391086521:
                        if (str.equals("pending_suggestions")) {
                            c = 29;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1385240692:
                        if (str.equals("channels_public_limit_premium")) {
                            c = '0';
                            break;
                        }
                        c = 65535;
                        break;
                    case -1287877531:
                        if (str.equals("stickers_faved_limit_premium")) {
                            c = '&';
                            break;
                        }
                        c = 65535;
                        break;
                    case -1056002991:
                        if (str.equals("chat_read_mark_expire_period")) {
                            c = 22;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1032177933:
                        if (str.equals("emojies_send_dice")) {
                            c = 16;
                            break;
                        }
                        c = 65535;
                        break;
                    case -980397720:
                        if (str.equals("url_auth_domains")) {
                            c = '\r';
                            break;
                        }
                        c = 65535;
                        break;
                    case -896467099:
                        if (str.equals("saved_gifs_limit_default")) {
                            c = '#';
                            break;
                        }
                        c = 65535;
                        break;
                    case -581904190:
                        if (str.equals("dialog_filters_limit_default")) {
                            c = '\'';
                            break;
                        }
                        c = 65535;
                        break;
                    case -561040027:
                        if (str.equals("premium_invoice_slug")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case -515715076:
                        if (str.equals("export_regex")) {
                            c = 26;
                            break;
                        }
                        c = 65535;
                        break;
                    case -488472170:
                        if (str.equals("about_length_limit_default")) {
                            c = '3';
                            break;
                        }
                        c = 65535;
                        break;
                    case -416504589:
                        if (str.equals("caption_length_limit_premium")) {
                            c = '2';
                            break;
                        }
                        c = 65535;
                        break;
                    case -404170231:
                        if (str.equals("keep_alive_service")) {
                            c = '\n';
                            break;
                        }
                        c = 65535;
                        break;
                    case -381432266:
                        if (str.equals("premium_promo_order")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case -377047005:
                        if (str.equals("ringtone_size_max")) {
                            c = 31;
                            break;
                        }
                        c = 65535;
                        break;
                    case -350971916:
                        if (str.equals("dialog_filters_chats_limit_premium")) {
                            c = '*';
                            break;
                        }
                        c = 65535;
                        break;
                    case -256319580:
                        if (str.equals("channels_limit_premium")) {
                            c = '\"';
                            break;
                        }
                        c = 65535;
                        break;
                    case -253815153:
                        if (str.equals("background_connection")) {
                            c = '\t';
                            break;
                        }
                        c = 65535;
                        break;
                    case -232883529:
                        if (str.equals("emojies_send_dice_success")) {
                            c = 18;
                            break;
                        }
                        c = 65535;
                        break;
                    case -223170831:
                        if (str.equals("dialog_filters_pinned_limit_default")) {
                            c = '+';
                            break;
                        }
                        c = 65535;
                        break;
                    case -111779186:
                        if (str.equals("autoarchive_setting_available")) {
                            c = 19;
                            break;
                        }
                        c = 65535;
                        break;
                    case -76561797:
                        if (str.equals("youtube_pip")) {
                            c = '\b';
                            break;
                        }
                        c = 65535;
                        break;
                    case -24016028:
                        if (str.equals("emojies_animated_zoom")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 169095108:
                        if (str.equals("stickers_emoji_suggest_only_api")) {
                            c = 25;
                            break;
                        }
                        c = 65535;
                        break;
                    case 222975416:
                        if (str.equals("gif_search_emojies")) {
                            c = 17;
                            break;
                        }
                        c = 65535;
                        break;
                    case 227342346:
                        if (str.equals("autologin_domains")) {
                            c = 14;
                            break;
                        }
                        c = 65535;
                        break;
                    case 246778895:
                        if (str.equals("export_group_urls")) {
                            c = 27;
                            break;
                        }
                        c = 65535;
                        break;
                    case 314452116:
                        if (str.equals("autologin_token")) {
                            c = 15;
                            break;
                        }
                        c = 65535;
                        break;
                    case 396402384:
                        if (str.equals("getfile_experimental_params")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 478015350:
                        if (str.equals("channels_public_limit_default")) {
                            c = '/';
                            break;
                        }
                        c = 65535;
                        break;
                    case 525494819:
                        if (str.equals("upload_max_fileparts_premium")) {
                            c = '.';
                            break;
                        }
                        c = 65535;
                        break;
                    case 575378511:
                        if (str.equals("stickers_faved_limit_default")) {
                            c = '%';
                            break;
                        }
                        c = 65535;
                        break;
                    case 676199595:
                        if (str.equals("groupcall_video_participants_max")) {
                            c = 20;
                            break;
                        }
                        c = 65535;
                        break;
                    case 684764449:
                        if (str.equals("save_gifs_with_stickers")) {
                            c = '\f';
                            break;
                        }
                        c = 65535;
                        break;
                    case 917364150:
                        if (str.equals("ringtone_duration_max")) {
                            c = ' ';
                            break;
                        }
                        c = 65535;
                        break;
                    case 992898905:
                        if (str.equals("inapp_update_check_delay")) {
                            c = 23;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1052355894:
                        if (str.equals("premium_bot_username")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1085221270:
                        if (str.equals("premium_purchase_blocked")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1322701672:
                        if (str.equals("round_video_encoding")) {
                            c = 24;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1446751453:
                        if (str.equals("caption_length_limit_default")) {
                            c = '1';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1508636733:
                        if (str.equals("chat_read_mark_size_threshold")) {
                            c = 21;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512284126:
                        if (str.equals("dialog_filters_chats_limit_default")) {
                            c = ')';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1535244155:
                        if (str.equals("saved_gifs_limit_premium")) {
                            c = '$';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1606936462:
                        if (str.equals("channels_limit_default")) {
                            c = '!';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1849807064:
                        if (str.equals("dialog_filters_limit_premium")) {
                            c = '(';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1850325103:
                        if (str.equals("emojies_sounds")) {
                            c = 30;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1943239084:
                        if (str.equals("about_length_limit_premium")) {
                            c = '4';
                            break;
                        }
                        c = 65535;
                        break;
                    case 2074702027:
                        if (str.equals("export_private_urls")) {
                            c = 28;
                            break;
                        }
                        c = 65535;
                        break;
                    case 2136829446:
                        if (str.equals("dialog_filters_enabled")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if ((value3.value instanceof TLRPC.TL_jsonBool) && this.premiumLocked != ((TLRPC.TL_jsonBool) value3.value).value) {
                            boolean z = ((TLRPC.TL_jsonBool) value3.value).value;
                            this.premiumLocked = z;
                            editor.putBoolean("premiumLocked", z);
                            changed = true;
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 1:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonString) {
                            String string = ((TLRPC.TL_jsonString) value3.value).value;
                            if (!string.equals(this.premiumBotUsername)) {
                                this.premiumBotUsername = string;
                                editor.putString("premiumBotUsername", string);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 2:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonString) {
                            String string2 = ((TLRPC.TL_jsonString) value3.value).value;
                            if (!string2.equals(this.premiumInvoiceSlug)) {
                                this.premiumInvoiceSlug = string2;
                                editor.putString("premiumInvoiceSlug", string2);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 3:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray order = (TLRPC.TL_jsonArray) value3.value;
                            boolean changed2 = savePremiumFeaturesPreviewOrder(editor, order.value);
                            changed = changed2;
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 4:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonNumber) {
                            TLRPC.TL_jsonNumber number = (TLRPC.TL_jsonNumber) value3.value;
                            if (this.animatedEmojisZoom != number.value) {
                                float f = (float) number.value;
                                this.animatedEmojisZoom = f;
                                editor.putFloat("animatedEmojisZoom", f);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 5:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonBool) {
                            TLRPC.TL_jsonBool bool = (TLRPC.TL_jsonBool) value3.value;
                            if (bool.value != this.getfileExperimentalParams) {
                                boolean z2 = bool.value;
                                this.getfileExperimentalParams = z2;
                                editor.putBoolean("getfileExperimentalParams", z2);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 6:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonBool) {
                            TLRPC.TL_jsonBool bool2 = (TLRPC.TL_jsonBool) value3.value;
                            if (bool2.value != this.filtersEnabled) {
                                boolean z3 = bool2.value;
                                this.filtersEnabled = z3;
                                editor.putBoolean("filtersEnabled", z3);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 7:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonBool) {
                            TLRPC.TL_jsonBool bool3 = (TLRPC.TL_jsonBool) value3.value;
                            if (bool3.value != this.showFiltersTooltip) {
                                boolean z4 = bool3.value;
                                this.showFiltersTooltip = z4;
                                editor.putBoolean("showFiltersTooltip", z4);
                                changed = true;
                                getNotificationCenter().postNotificationName(NotificationCenter.filterSettingsUpdated, new Object[0]);
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case '\b':
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonString) {
                            TLRPC.TL_jsonString string3 = (TLRPC.TL_jsonString) value3.value;
                            if (!string3.value.equals(this.youtubePipType)) {
                                String str2 = string3.value;
                                this.youtubePipType = str2;
                                editor.putString("youtubePipType", str2);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case '\t':
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonBool) {
                            TLRPC.TL_jsonBool bool4 = (TLRPC.TL_jsonBool) value3.value;
                            if (bool4.value == this.backgroundConnection) {
                                keelAliveChanged4 = keelAliveChanged;
                                continue;
                            } else {
                                boolean z5 = bool4.value;
                                this.backgroundConnection = z5;
                                editor.putBoolean("backgroundConnection", z5);
                                changed = true;
                                keelAliveChanged4 = true;
                            }
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case '\n':
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonBool) {
                            TLRPC.TL_jsonBool bool5 = (TLRPC.TL_jsonBool) value3.value;
                            if (bool5.value == this.keepAliveService) {
                                keelAliveChanged4 = keelAliveChanged;
                                continue;
                            } else {
                                boolean z6 = bool5.value;
                                this.keepAliveService = z6;
                                editor.putBoolean("keepAliveService", z6);
                                changed = true;
                                keelAliveChanged4 = true;
                            }
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 11:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonBool) {
                            TLRPC.TL_jsonBool bool6 = (TLRPC.TL_jsonBool) value3.value;
                            if (bool6.value != this.qrLoginCamera) {
                                boolean z7 = bool6.value;
                                this.qrLoginCamera = z7;
                                editor.putBoolean("qrLoginCamera", z7);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case '\f':
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonBool) {
                            TLRPC.TL_jsonBool bool7 = (TLRPC.TL_jsonBool) value3.value;
                            if (bool7.value != this.saveGifsWithStickers) {
                                boolean z8 = bool7.value;
                                this.saveGifsWithStickers = z8;
                                editor.putBoolean("saveGifsWithStickers", z8);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case '\r':
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        HashSet<String> newDomains = new HashSet<>();
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray array = (TLRPC.TL_jsonArray) value3.value;
                            int N22 = array.value.size();
                            for (int b = 0; b < N22; b++) {
                                TLRPC.JSONValue val = array.value.get(b);
                                if (val instanceof TLRPC.TL_jsonString) {
                                    newDomains.add(((TLRPC.TL_jsonString) val).value);
                                }
                            }
                        }
                        if (!this.authDomains.equals(newDomains)) {
                            this.authDomains = newDomains;
                            editor.putStringSet("authDomains", newDomains);
                            changed = true;
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 14:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        HashSet<String> newDomains2 = new HashSet<>();
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray array2 = (TLRPC.TL_jsonArray) value3.value;
                            int N23 = array2.value.size();
                            for (int b2 = 0; b2 < N23; b2++) {
                                TLRPC.JSONValue val2 = array2.value.get(b2);
                                if (val2 instanceof TLRPC.TL_jsonString) {
                                    newDomains2.add(((TLRPC.TL_jsonString) val2).value);
                                }
                            }
                        }
                        if (!this.autologinDomains.equals(newDomains2)) {
                            this.autologinDomains = newDomains2;
                            editor.putStringSet("autologinDomains", newDomains2);
                            changed = true;
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 15:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        if (value3.value instanceof TLRPC.TL_jsonString) {
                            TLRPC.TL_jsonString string4 = (TLRPC.TL_jsonString) value3.value;
                            if (!string4.value.equals(this.autologinToken)) {
                                String str3 = string4.value;
                                this.autologinToken = str3;
                                editor.putString("autologinToken", str3);
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 16:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        HashSet<String> newEmojies = new HashSet<>();
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray array3 = (TLRPC.TL_jsonArray) value3.value;
                            int N24 = array3.value.size();
                            for (int b3 = 0; b3 < N24; b3++) {
                                TLRPC.JSONValue val3 = array3.value.get(b3);
                                if (val3 instanceof TLRPC.TL_jsonString) {
                                    newEmojies.add(((TLRPC.TL_jsonString) val3).value.replace("️", ""));
                                }
                            }
                        }
                        if (!this.diceEmojies.equals(newEmojies)) {
                            this.diceEmojies = newEmojies;
                            editor.putStringSet("diceEmojies", newEmojies);
                            changed = true;
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 17:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        ArrayList<String> newEmojies2 = new ArrayList<>();
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray array4 = (TLRPC.TL_jsonArray) value3.value;
                            int N25 = array4.value.size();
                            for (int b4 = 0; b4 < N25; b4++) {
                                TLRPC.JSONValue val4 = array4.value.get(b4);
                                if (val4 instanceof TLRPC.TL_jsonString) {
                                    newEmojies2.add(((TLRPC.TL_jsonString) val4).value.replace("️", ""));
                                }
                            }
                        }
                        if (!this.gifSearchEmojies.equals(newEmojies2)) {
                            this.gifSearchEmojies = newEmojies2;
                            SerializedData serializedData = new SerializedData();
                            serializedData.writeInt32(this.gifSearchEmojies.size());
                            int N26 = this.gifSearchEmojies.size();
                            for (int b5 = 0; b5 < N26; b5++) {
                                serializedData.writeString(this.gifSearchEmojies.get(b5));
                            }
                            editor.putString("gifSearchEmojies", Base64.encodeToString(serializedData.toByteArray(), 0));
                            serializedData.cleanup();
                            changed = true;
                            keelAliveChanged4 = keelAliveChanged;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 18:
                        object = object2;
                        N = N2;
                        try {
                            HashMap<String, DiceFrameSuccess> newEmojies3 = new HashMap<>();
                            if (!(value3.value instanceof TLRPC.TL_jsonObject)) {
                                keelAliveChanged2 = keelAliveChanged4;
                                a = a3;
                            } else {
                                TLRPC.TL_jsonObject jsonObject4 = (TLRPC.TL_jsonObject) value3.value;
                                int b6 = 0;
                                int N27 = jsonObject4.value.size();
                                while (b6 < N27) {
                                    TLRPC.TL_jsonObjectValue val5 = jsonObject4.value.get(b6);
                                    if (!(val5.value instanceof TLRPC.TL_jsonObject)) {
                                        jsonObject3 = jsonObject4;
                                        c2 = N27;
                                        keelAliveChanged3 = keelAliveChanged4;
                                        a2 = a3;
                                        value = value3;
                                    } else {
                                        TLRPC.TL_jsonObject jsonObject22 = (TLRPC.TL_jsonObject) val5.value;
                                        int n = Integer.MAX_VALUE;
                                        int f2 = Integer.MAX_VALUE;
                                        jsonObject3 = jsonObject4;
                                        int N3 = jsonObject22.value.size();
                                        c2 = N27;
                                        int N28 = 0;
                                        while (N28 < N3) {
                                            int N32 = N3;
                                            TLRPC.TL_jsonObjectValue val22 = jsonObject22.value.get(N28);
                                            TLRPC.TL_jsonObject jsonObject23 = jsonObject22;
                                            if (!(val22.value instanceof TLRPC.TL_jsonNumber)) {
                                                keelAliveChanged = keelAliveChanged4;
                                                a = a3;
                                                value2 = value3;
                                            } else {
                                                keelAliveChanged = keelAliveChanged4;
                                                try {
                                                    if (CommonProperties.VALUE.equals(val22.key)) {
                                                        a = a3;
                                                        value2 = value3;
                                                        try {
                                                            n = (int) ((TLRPC.TL_jsonNumber) val22.value).value;
                                                        } catch (Exception e3) {
                                                            e2 = e3;
                                                            FileLog.e(e2);
                                                            keelAliveChanged4 = keelAliveChanged;
                                                            a3 = a + 1;
                                                            N2 = N;
                                                            object2 = object;
                                                        }
                                                    } else {
                                                        a = a3;
                                                        value2 = value3;
                                                        if ("frame_start".equals(val22.key)) {
                                                            f2 = (int) ((TLRPC.TL_jsonNumber) val22.value).value;
                                                        }
                                                    }
                                                } catch (Exception e4) {
                                                    e2 = e4;
                                                    a = a3;
                                                }
                                            }
                                            N28++;
                                            a3 = a;
                                            N3 = N32;
                                            jsonObject22 = jsonObject23;
                                            keelAliveChanged4 = keelAliveChanged;
                                            value3 = value2;
                                        }
                                        keelAliveChanged3 = keelAliveChanged4;
                                        value = value3;
                                        a2 = a3;
                                        if (f2 != Integer.MAX_VALUE && n != Integer.MAX_VALUE) {
                                            newEmojies3.put(val5.key.replace("️", ""), new DiceFrameSuccess(f2, n));
                                        }
                                    }
                                    b6++;
                                    a3 = a2;
                                    N27 = c2;
                                    jsonObject4 = jsonObject3;
                                    keelAliveChanged4 = keelAliveChanged3;
                                    value3 = value;
                                }
                                keelAliveChanged2 = keelAliveChanged4;
                                a = a3;
                            }
                            if (!this.diceSuccess.equals(newEmojies3)) {
                                this.diceSuccess = newEmojies3;
                                SerializedData serializedData2 = new SerializedData();
                                serializedData2.writeInt32(this.diceSuccess.size());
                                for (Map.Entry<String, DiceFrameSuccess> entry : this.diceSuccess.entrySet()) {
                                    serializedData2.writeString(entry.getKey());
                                    DiceFrameSuccess frameSuccess = entry.getValue();
                                    serializedData2.writeInt32(frameSuccess.frame);
                                    serializedData2.writeInt32(frameSuccess.num);
                                }
                                editor.putString("diceSuccess", Base64.encodeToString(serializedData2.toByteArray(), 0));
                                serializedData2.cleanup();
                                changed = true;
                            }
                            keelAliveChanged4 = keelAliveChanged2;
                            continue;
                        } catch (Exception e5) {
                            e2 = e5;
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                        }
                        a3 = a + 1;
                        N2 = N;
                        object2 = object;
                        break;
                    case 19:
                        object = object2;
                        N = N2;
                        if (!(value3.value instanceof TLRPC.TL_jsonBool)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonBool bool8 = (TLRPC.TL_jsonBool) value3.value;
                            if (bool8.value != this.autoarchiveAvailable) {
                                boolean z9 = bool8.value;
                                this.autoarchiveAvailable = z9;
                                editor.putBoolean("autoarchiveAvailable", z9);
                                changed = true;
                            }
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case 20:
                        object = object2;
                        N = N2;
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number2 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number2.value != this.groupCallVideoMaxParticipants) {
                                int i = (int) number2.value;
                                this.groupCallVideoMaxParticipants = i;
                                editor.putInt("groipCallVideoMaxParticipants", i);
                                changed = true;
                            }
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case 21:
                        object = object2;
                        N = N2;
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number3 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number3.value != this.chatReadMarkSizeThreshold) {
                                int i2 = (int) number3.value;
                                this.chatReadMarkSizeThreshold = i2;
                                editor.putInt("chatReadMarkSizeThreshold", i2);
                                changed = true;
                            }
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case 22:
                        object = object2;
                        N = N2;
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number4 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number4.value != this.chatReadMarkExpirePeriod) {
                                int i3 = (int) number4.value;
                                this.chatReadMarkExpirePeriod = i3;
                                editor.putInt("chatReadMarkExpirePeriod", i3);
                                changed = true;
                            }
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case 23:
                        object = object2;
                        N = N2;
                        if (value3.value instanceof TLRPC.TL_jsonNumber) {
                            TLRPC.TL_jsonNumber number5 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number5.value != this.updateCheckDelay) {
                                int i4 = (int) number5.value;
                                this.updateCheckDelay = i4;
                                editor.putInt("updateCheckDelay", i4);
                                changed = true;
                            }
                            a = a3;
                            continue;
                        } else if (!(value3.value instanceof TLRPC.TL_jsonString)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            int delay = Utilities.parseInt((CharSequence) ((TLRPC.TL_jsonString) value3.value).value).intValue();
                            if (delay != this.updateCheckDelay) {
                                this.updateCheckDelay = delay;
                                editor.putInt("updateCheckDelay", delay);
                                changed = true;
                            }
                            a = a3;
                        }
                        a3 = a + 1;
                        N2 = N;
                        object2 = object;
                    case 24:
                        object = object2;
                        N = N2;
                        if (!(value3.value instanceof TLRPC.TL_jsonObject)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonObject jsonObject5 = (TLRPC.TL_jsonObject) value3.value;
                            int N29 = jsonObject5.value.size();
                            for (int b7 = 0; b7 < N29; b7++) {
                                TLRPC.TL_jsonObjectValue value22 = jsonObject5.value.get(b7);
                                String str4 = value22.key;
                                switch (str4.hashCode()) {
                                    case -233204595:
                                        if (str4.equals("diameter")) {
                                            c3 = 0;
                                            break;
                                        }
                                        c3 = 65535;
                                        break;
                                    case 258902020:
                                        if (str4.equals("audio_bitrate")) {
                                            c3 = 2;
                                            break;
                                        }
                                        c3 = 65535;
                                        break;
                                    case 1924434857:
                                        if (str4.equals("video_bitrate")) {
                                            c3 = 1;
                                            break;
                                        }
                                        c3 = 65535;
                                        break;
                                    default:
                                        c3 = 65535;
                                        break;
                                }
                                switch (c3) {
                                    case 0:
                                        if (value22.value instanceof TLRPC.TL_jsonNumber) {
                                            TLRPC.TL_jsonNumber number6 = (TLRPC.TL_jsonNumber) value22.value;
                                            if (number6.value != this.roundVideoSize) {
                                                int i5 = (int) number6.value;
                                                this.roundVideoSize = i5;
                                                editor.putInt("roundVideoSize", i5);
                                                changed = true;
                                                break;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    case 1:
                                        if (value22.value instanceof TLRPC.TL_jsonNumber) {
                                            TLRPC.TL_jsonNumber number7 = (TLRPC.TL_jsonNumber) value22.value;
                                            if (number7.value != this.roundVideoBitrate) {
                                                int i6 = (int) number7.value;
                                                this.roundVideoBitrate = i6;
                                                editor.putInt("roundVideoBitrate", i6);
                                                changed = true;
                                                break;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    case 2:
                                        if (value22.value instanceof TLRPC.TL_jsonNumber) {
                                            TLRPC.TL_jsonNumber number8 = (TLRPC.TL_jsonNumber) value22.value;
                                            if (number8.value != this.roundAudioBitrate) {
                                                int i7 = (int) number8.value;
                                                this.roundAudioBitrate = i7;
                                                editor.putInt("roundAudioBitrate", i7);
                                                changed = true;
                                                break;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                }
                            }
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                        break;
                    case 25:
                        object = object2;
                        N = N2;
                        if (!(value3.value instanceof TLRPC.TL_jsonBool)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonBool bool9 = (TLRPC.TL_jsonBool) value3.value;
                            if (bool9.value != this.suggestStickersApiOnly) {
                                boolean z10 = bool9.value;
                                this.suggestStickersApiOnly = z10;
                                editor.putBoolean("suggestStickersApiOnly", z10);
                                changed = true;
                            }
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case 26:
                        object = object2;
                        N = N2;
                        HashSet<String> newExport = new HashSet<>();
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray array5 = (TLRPC.TL_jsonArray) value3.value;
                            int N210 = array5.value.size();
                            for (int b8 = 0; b8 < N210; b8++) {
                                TLRPC.JSONValue val6 = array5.value.get(b8);
                                if (val6 instanceof TLRPC.TL_jsonString) {
                                    newExport.add(((TLRPC.TL_jsonString) val6).value);
                                }
                            }
                        }
                        if (this.exportUri.equals(newExport)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            this.exportUri = newExport;
                            editor.putStringSet("exportUri2", newExport);
                            changed = true;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case 27:
                        object = object2;
                        N = N2;
                        HashSet<String> newExport2 = new HashSet<>();
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray array6 = (TLRPC.TL_jsonArray) value3.value;
                            int N211 = array6.value.size();
                            for (int b9 = 0; b9 < N211; b9++) {
                                TLRPC.JSONValue val7 = array6.value.get(b9);
                                if (val7 instanceof TLRPC.TL_jsonString) {
                                    newExport2.add(((TLRPC.TL_jsonString) val7).value);
                                }
                            }
                        }
                        if (this.exportGroupUri.equals(newExport2)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            this.exportGroupUri = newExport2;
                            editor.putStringSet("exportGroupUri", newExport2);
                            changed = true;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case 28:
                        object = object2;
                        N = N2;
                        HashSet<String> newExport3 = new HashSet<>();
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray array7 = (TLRPC.TL_jsonArray) value3.value;
                            int N212 = array7.value.size();
                            for (int b10 = 0; b10 < N212; b10++) {
                                TLRPC.JSONValue val8 = array7.value.get(b10);
                                if (val8 instanceof TLRPC.TL_jsonString) {
                                    newExport3.add(((TLRPC.TL_jsonString) val8).value);
                                }
                            }
                        }
                        if (this.exportPrivateUri.equals(newExport3)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            this.exportPrivateUri = newExport3;
                            editor.putStringSet("exportPrivateUri", newExport3);
                            changed = true;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case NalUnitTypes.NAL_TYPE_RSV_VCL29 /* 29 */:
                        object = object2;
                        N = N2;
                        HashSet<String> newSuggestions = new HashSet<>();
                        if (value3.value instanceof TLRPC.TL_jsonArray) {
                            TLRPC.TL_jsonArray array8 = (TLRPC.TL_jsonArray) value3.value;
                            int N213 = array8.value.size();
                            for (int b11 = 0; b11 < N213; b11++) {
                                TLRPC.JSONValue val9 = array8.value.get(b11);
                                if (val9 instanceof TLRPC.TL_jsonString) {
                                    newSuggestions.add(((TLRPC.TL_jsonString) val9).value);
                                }
                            }
                        }
                        if (this.pendingSuggestions.equals(newSuggestions)) {
                            keelAliveChanged = keelAliveChanged4;
                            a = a3;
                            break;
                        } else {
                            this.pendingSuggestions = newSuggestions;
                            editor.putStringSet("pendingSuggestions", newSuggestions);
                            getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable, new Object[0]);
                            changed = true;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case 30:
                        try {
                            HashMap<String, EmojiSound> newEmojies4 = new HashMap<>();
                            if (!(value3.value instanceof TLRPC.TL_jsonObject)) {
                                object = object2;
                                N = N2;
                            } else {
                                TLRPC.TL_jsonObject jsonObject6 = (TLRPC.TL_jsonObject) value3.value;
                                int b12 = 0;
                                int N214 = jsonObject6.value.size();
                                while (b12 < N214) {
                                    TLRPC.TL_jsonObjectValue val10 = jsonObject6.value.get(b12);
                                    if (!(val10.value instanceof TLRPC.TL_jsonObject)) {
                                        object = object2;
                                        N = N2;
                                        jsonObject = jsonObject6;
                                    } else {
                                        TLRPC.TL_jsonObject jsonObject24 = (TLRPC.TL_jsonObject) val10.value;
                                        long i8 = 0;
                                        long ah = 0;
                                        String fr = null;
                                        object = object2;
                                        try {
                                            int N33 = jsonObject24.value.size();
                                            N = N2;
                                            int N4 = 0;
                                            while (N4 < N33) {
                                                int N34 = N33;
                                                try {
                                                    TLRPC.TL_jsonObjectValue val23 = jsonObject24.value.get(N4);
                                                    TLRPC.TL_jsonObject jsonObject25 = jsonObject24;
                                                    if (!(val23.value instanceof TLRPC.TL_jsonString)) {
                                                        jsonObject2 = jsonObject6;
                                                    } else {
                                                        jsonObject2 = jsonObject6;
                                                        if ("id".equals(val23.key)) {
                                                            i8 = Utilities.parseLong(((TLRPC.TL_jsonString) val23.value).value).longValue();
                                                        } else if ("access_hash".equals(val23.key)) {
                                                            ah = Utilities.parseLong(((TLRPC.TL_jsonString) val23.value).value).longValue();
                                                        } else if ("file_reference_base64".equals(val23.key)) {
                                                            fr = ((TLRPC.TL_jsonString) val23.value).value;
                                                        }
                                                    }
                                                    N4++;
                                                    N33 = N34;
                                                    jsonObject24 = jsonObject25;
                                                    jsonObject6 = jsonObject2;
                                                } catch (Exception e6) {
                                                    e = e6;
                                                    FileLog.e(e);
                                                    keelAliveChanged = keelAliveChanged4;
                                                    a = a3;
                                                    keelAliveChanged4 = keelAliveChanged;
                                                    a3 = a + 1;
                                                    N2 = N;
                                                    object2 = object;
                                                }
                                            }
                                            jsonObject = jsonObject6;
                                            if (i8 != 0 && ah != 0 && fr != null) {
                                                newEmojies4.put(val10.key.replace("️", ""), new EmojiSound(i8, ah, fr));
                                            }
                                        } catch (Exception e7) {
                                            e = e7;
                                            N = N2;
                                        }
                                    }
                                    b12++;
                                    N2 = N;
                                    object2 = object;
                                    jsonObject6 = jsonObject;
                                }
                                object = object2;
                                N = N2;
                            }
                            if (!this.emojiSounds.equals(newEmojies4)) {
                                this.emojiSounds = newEmojies4;
                                SerializedData serializedData3 = new SerializedData();
                                serializedData3.writeInt32(this.emojiSounds.size());
                                for (Map.Entry<String, EmojiSound> entry2 : this.emojiSounds.entrySet()) {
                                    serializedData3.writeString(entry2.getKey());
                                    EmojiSound emojiSound = entry2.getValue();
                                    serializedData3.writeInt64(emojiSound.id);
                                    serializedData3.writeInt64(emojiSound.accessHash);
                                    serializedData3.writeByteArray(emojiSound.fileReference);
                                }
                                editor.putString("emojiSounds", Base64.encodeToString(serializedData3.toByteArray(), 0));
                                serializedData3.cleanup();
                                changed = true;
                            }
                            a = a3;
                            continue;
                        } catch (Exception e8) {
                            e = e8;
                            object = object2;
                            N = N2;
                        }
                        a3 = a + 1;
                        N2 = N;
                        object2 = object;
                        break;
                    case 31:
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number9 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number9.value != this.ringtoneSizeMax) {
                                int i9 = (int) number9.value;
                                this.ringtoneSizeMax = i9;
                                editor.putInt("ringtoneSizeMax", i9);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case ' ':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number10 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number10.value != this.ringtoneDurationMax) {
                                int i10 = (int) number10.value;
                                this.ringtoneDurationMax = i10;
                                editor.putInt("ringtoneDurationMax", i10);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '!':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number11 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number11.value != this.channelsLimitDefault) {
                                int i11 = (int) number11.value;
                                this.channelsLimitDefault = i11;
                                editor.putInt("channelsLimitDefault", i11);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '\"':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number12 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number12.value != this.channelsLimitPremium) {
                                int i12 = (int) number12.value;
                                this.channelsLimitPremium = i12;
                                editor.putInt("channelsLimitPremium", i12);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '#':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number13 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number13.value != this.savedGifsLimitDefault) {
                                int i13 = (int) number13.value;
                                this.savedGifsLimitDefault = i13;
                                editor.putInt("savedGifsLimitDefault", i13);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '$':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number14 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number14.value != this.savedGifsLimitPremium) {
                                int i14 = (int) number14.value;
                                this.savedGifsLimitPremium = i14;
                                editor.putInt("savedGifsLimitPremium", i14);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '%':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number15 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number15.value != this.stickersFavedLimitDefault) {
                                int i15 = (int) number15.value;
                                this.stickersFavedLimitDefault = i15;
                                editor.putInt("stickersFavedLimitDefault", i15);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '&':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number16 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number16.value != this.stickersFavedLimitPremium) {
                                int i16 = (int) number16.value;
                                this.stickersFavedLimitPremium = i16;
                                editor.putInt("stickersFavedLimitPremium", i16);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '\'':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number17 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number17.value != this.dialogFiltersLimitDefault) {
                                int i17 = (int) number17.value;
                                this.dialogFiltersLimitDefault = i17;
                                editor.putInt("dialogFiltersLimitDefault", i17);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '(':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number18 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number18.value != this.dialogFiltersLimitPremium) {
                                int i18 = (int) number18.value;
                                this.dialogFiltersLimitPremium = i18;
                                editor.putInt("dialogFiltersLimitPremium", i18);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case ')':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number19 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number19.value != this.dialogFiltersChatsLimitDefault) {
                                int i19 = (int) number19.value;
                                this.dialogFiltersChatsLimitDefault = i19;
                                editor.putInt("dialogFiltersChatsLimitDefault", i19);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '*':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number20 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number20.value != this.dialogFiltersChatsLimitPremium) {
                                int i20 = (int) number20.value;
                                this.dialogFiltersChatsLimitPremium = i20;
                                editor.putInt("dialogFiltersChatsLimitPremium", i20);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '+':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number21 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number21.value != this.dialogFiltersPinnedLimitDefault) {
                                int i21 = (int) number21.value;
                                this.dialogFiltersPinnedLimitDefault = i21;
                                editor.putInt("dialogFiltersPinnedLimitDefault", i21);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case ',':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number22 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number22.value != this.dialogFiltersPinnedLimitPremium) {
                                int i22 = (int) number22.value;
                                this.dialogFiltersPinnedLimitPremium = i22;
                                editor.putInt("dialogFiltersPinnedLimitPremium", i22);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '-':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number23 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number23.value != this.uploadMaxFileParts) {
                                int i23 = (int) number23.value;
                                this.uploadMaxFileParts = i23;
                                editor.putInt("uploadMaxFileParts", i23);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '.':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number24 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number24.value != this.uploadMaxFilePartsPremium) {
                                int i24 = (int) number24.value;
                                this.uploadMaxFilePartsPremium = i24;
                                editor.putInt("uploadMaxFilePartsPremium", i24);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '/':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number25 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number25.value != this.publicLinksLimitDefault) {
                                int i25 = (int) number25.value;
                                this.publicLinksLimitDefault = i25;
                                editor.putInt("publicLinksLimit", i25);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '0':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number26 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number26.value != this.publicLinksLimitPremium) {
                                int i26 = (int) number26.value;
                                this.publicLinksLimitPremium = i26;
                                editor.putInt("publicLinksLimitPremium", i26);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '1':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number27 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number27.value != this.captionLengthLimitDefault) {
                                int i27 = (int) number27.value;
                                this.captionLengthLimitDefault = i27;
                                editor.putInt("captionLengthLimitDefault", i27);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '2':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number28 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number28.value != this.captionLengthLimitPremium) {
                                int i28 = (int) number28.value;
                                this.captionLengthLimitPremium = i28;
                                editor.putInt("captionLengthLimitPremium", i28);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '3':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number29 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number29.value != this.aboutLengthLimitDefault) {
                                int i29 = (int) number29.value;
                                this.aboutLengthLimitDefault = i29;
                                editor.putInt("aboutLengthLimitDefault", i29);
                                changed = true;
                            }
                            object = object2;
                            N = N2;
                            a = a3;
                            continue;
                            a3 = a + 1;
                            N2 = N;
                            object2 = object;
                        }
                    case '4':
                        if (!(value3.value instanceof TLRPC.TL_jsonNumber)) {
                            object = object2;
                            keelAliveChanged = keelAliveChanged4;
                            N = N2;
                            a = a3;
                            break;
                        } else {
                            TLRPC.TL_jsonNumber number30 = (TLRPC.TL_jsonNumber) value3.value;
                            if (number30.value == this.aboutLengthLimitPremium) {
                                object = object2;
                                keelAliveChanged = keelAliveChanged4;
                                N = N2;
                                a = a3;
                                break;
                            } else {
                                int i30 = (int) number30.value;
                                this.aboutLengthLimitPremium = i30;
                                editor.putInt("aboutLengthLimitPremium", i30);
                                changed = true;
                                object = object2;
                                N = N2;
                                a = a3;
                                continue;
                                a3 = a + 1;
                                N2 = N;
                                object2 = object;
                            }
                        }
                    default:
                        object = object2;
                        keelAliveChanged = keelAliveChanged4;
                        N = N2;
                        a = a3;
                        break;
                }
                keelAliveChanged4 = keelAliveChanged;
                a3 = a + 1;
                N2 = N;
                object2 = object;
            }
            boolean keelAliveChanged5 = keelAliveChanged4;
            if (changed) {
                editor.apply();
            }
            if (keelAliveChanged5) {
                ApplicationLoader.startPushService();
                ConnectionsManager connectionsManager = getConnectionsManager();
                connectionsManager.setPushConnectionEnabled(connectionsManager.isPushConnectionEnabled());
            }
        }
        this.loadingAppConfig = false;
    }

    private boolean savePremiumFeaturesPreviewOrder(SharedPreferences.Editor editor, ArrayList<TLRPC.JSONValue> value) {
        int type;
        StringBuilder stringBuilder = new StringBuilder();
        this.premiumFeaturesTypesToPosition.clear();
        for (int i = 0; i < value.size(); i++) {
            String s = null;
            if (value.get(i) instanceof TLRPC.TL_jsonString) {
                s = ((TLRPC.TL_jsonString) value.get(i)).value;
            }
            if (s != null && (type = PremiumPreviewFragment.severStringToFeatureType(s)) >= 0) {
                this.premiumFeaturesTypesToPosition.put(type, i);
                if (stringBuilder.length() > 0) {
                    stringBuilder.append('_');
                }
                stringBuilder.append(type);
            }
        }
        int i2 = stringBuilder.length();
        boolean z = true;
        if (i2 > 0) {
            String string = stringBuilder.toString();
            boolean changed = !string.equals(this.mainPreferences.getString("premiumFeaturesTypesToPosition", null));
            editor.putString("premiumFeaturesTypesToPosition", string);
            return changed;
        }
        editor.remove("premiumFeaturesTypesToPosition");
        if (this.mainPreferences.getString("premiumFeaturesTypesToPosition", null) == null) {
            z = false;
        }
        boolean changed2 = z;
        return changed2;
    }

    private void loadPremiumFeaturesPreviewOrder(String string) {
        this.premiumFeaturesTypesToPosition.clear();
        if (string != null) {
            String[] types = string.split("_");
            for (int i = 0; i < types.length; i++) {
                int type = Integer.parseInt(types[i]);
                this.premiumFeaturesTypesToPosition.put(type, i);
            }
        }
    }

    public void removeSuggestion(long did, String suggestion) {
        if (TextUtils.isEmpty(suggestion)) {
            return;
        }
        if (did == 0) {
            if (this.pendingSuggestions.remove(suggestion)) {
                SharedPreferences.Editor editor = this.mainPreferences.edit();
                editor.putStringSet("pendingSuggestions", this.pendingSuggestions);
                editor.commit();
                getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable, new Object[0]);
            } else {
                return;
            }
        }
        TLRPC.TL_help_dismissSuggestion req = new TLRPC.TL_help_dismissSuggestion();
        req.suggestion = suggestion;
        if (did == 0) {
            req.peer = new TLRPC.TL_inputPeerEmpty();
        } else {
            req.peer = getInputPeer(did);
        }
        getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda283.INSTANCE);
    }

    public static /* synthetic */ void lambda$removeSuggestion$22(TLObject response, TLRPC.TL_error error) {
    }

    public void updateConfig(final TLRPC.TL_config config) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda68
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m885lambda$updateConfig$23$orgtelegrammessengerMessagesController(config);
            }
        });
    }

    /* renamed from: lambda$updateConfig$23$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m885lambda$updateConfig$23$orgtelegrammessengerMessagesController(TLRPC.TL_config config) {
        getDownloadController().loadAutoDownloadConfig(false);
        m760lambda$new$10$orgtelegrammessengerMessagesController();
        this.remoteConfigLoaded = true;
        this.maxMegagroupCount = config.megagroup_size_max;
        this.maxGroupCount = config.chat_size_max;
        this.maxEditTime = config.edit_time_limit;
        this.ratingDecay = config.rating_e_decay;
        this.maxRecentGifsCount = config.saved_gifs_limit;
        this.maxRecentStickersCount = config.stickers_recent_limit;
        this.maxFaveStickersCount = config.stickers_faved_limit;
        this.revokeTimeLimit = config.revoke_time_limit;
        this.revokeTimePmLimit = config.revoke_pm_time_limit;
        this.canRevokePmInbox = config.revoke_pm_inbox;
        this.linkPrefix = config.me_url_prefix;
        boolean forceTryIpV6 = config.force_try_ipv6;
        if (this.linkPrefix.endsWith("/")) {
            String str = this.linkPrefix;
            this.linkPrefix = str.substring(0, str.length() - 1);
        }
        if (this.linkPrefix.startsWith("https://")) {
            this.linkPrefix = this.linkPrefix.substring(8);
        } else if (this.linkPrefix.startsWith("http://")) {
            this.linkPrefix = this.linkPrefix.substring(7);
        }
        this.callReceiveTimeout = config.call_receive_timeout_ms;
        this.callRingTimeout = config.call_ring_timeout_ms;
        this.callConnectTimeout = config.call_connect_timeout_ms;
        this.callPacketTimeout = config.call_packet_timeout_ms;
        this.maxPinnedDialogsCount = config.pinned_dialogs_count_max;
        this.maxFolderPinnedDialogsCount = config.pinned_infolder_count_max;
        this.maxMessageLength = config.message_length_max;
        this.maxCaptionLength = config.caption_length_max;
        this.preloadFeaturedStickers = config.preload_featured_stickers;
        if (config.venue_search_username != null) {
            this.venueSearchBot = config.venue_search_username;
        }
        if (config.gif_search_username != null) {
            this.gifSearchBot = config.gif_search_username;
        }
        if (this.imageSearchBot != null) {
            this.imageSearchBot = config.img_search_username;
        }
        this.blockedCountry = config.blocked_mode;
        this.dcDomainName = config.dc_txt_domain_name;
        this.webFileDatacenterId = config.webfile_dc_id;
        if (config.suggested_lang_code != null) {
            String str2 = this.suggestedLangCode;
            boolean loadRemote = str2 == null || !str2.equals(config.suggested_lang_code);
            this.suggestedLangCode = config.suggested_lang_code;
            if (loadRemote) {
                LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
            }
        }
        Theme.loadRemoteThemes(this.currentAccount, false);
        Theme.checkCurrentRemoteTheme(false);
        if (config.static_maps_provider == null) {
            config.static_maps_provider = "telegram";
        }
        this.mapKey = null;
        this.mapProvider = 2;
        this.availableMapProviders = 0;
        FileLog.d("map providers = " + config.static_maps_provider);
        String[] providers = config.static_maps_provider.split(",");
        for (int a = 0; a < providers.length; a++) {
            String[] mapArgs = providers[a].split("\\+");
            if (mapArgs.length > 0) {
                String[] typeAndKey = mapArgs[0].split(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
                if (typeAndKey.length > 0) {
                    if ("yandex".equals(typeAndKey[0])) {
                        if (a == 0) {
                            if (mapArgs.length > 1) {
                                this.mapProvider = 3;
                            } else {
                                this.mapProvider = 1;
                            }
                        }
                        this.availableMapProviders |= 4;
                    } else if ("google".equals(typeAndKey[0])) {
                        if (a == 0 && mapArgs.length > 1) {
                            this.mapProvider = 4;
                        }
                        this.availableMapProviders |= 1;
                    } else if ("telegram".equals(typeAndKey[0])) {
                        if (a == 0) {
                            this.mapProvider = 2;
                        }
                        this.availableMapProviders |= 2;
                    }
                    if (typeAndKey.length > 1) {
                        this.mapKey = typeAndKey[1];
                    }
                }
            }
        }
        SharedPreferences.Editor editor = this.mainPreferences.edit();
        editor.putBoolean("remoteConfigLoaded", this.remoteConfigLoaded);
        editor.putInt("maxGroupCount", this.maxGroupCount);
        editor.putInt("maxMegagroupCount", this.maxMegagroupCount);
        editor.putInt("maxEditTime", this.maxEditTime);
        editor.putInt("ratingDecay", this.ratingDecay);
        editor.putInt("maxRecentGifsCount", this.maxRecentGifsCount);
        editor.putInt("maxRecentStickersCount", this.maxRecentStickersCount);
        editor.putInt("maxFaveStickersCount", this.maxFaveStickersCount);
        editor.putInt("callReceiveTimeout", this.callReceiveTimeout);
        editor.putInt("callRingTimeout", this.callRingTimeout);
        editor.putInt("callConnectTimeout", this.callConnectTimeout);
        editor.putInt("callPacketTimeout", this.callPacketTimeout);
        editor.putString("linkPrefix", this.linkPrefix);
        editor.putInt("maxPinnedDialogsCount", this.maxPinnedDialogsCount);
        editor.putInt("maxFolderPinnedDialogsCount", this.maxFolderPinnedDialogsCount);
        editor.putInt("maxMessageLength", this.maxMessageLength);
        editor.putInt("maxCaptionLength", this.maxCaptionLength);
        editor.putBoolean("preloadFeaturedStickers", this.preloadFeaturedStickers);
        editor.putInt("revokeTimeLimit", this.revokeTimeLimit);
        editor.putInt("revokeTimePmLimit", this.revokeTimePmLimit);
        editor.putInt("mapProvider", this.mapProvider);
        String str3 = this.mapKey;
        if (str3 != null) {
            editor.putString("pk", str3);
        } else {
            editor.remove("pk");
        }
        editor.putBoolean("canRevokePmInbox", this.canRevokePmInbox);
        editor.putBoolean("blockedCountry", this.blockedCountry);
        editor.putString("venueSearchBot", this.venueSearchBot);
        editor.putString("gifSearchBot", this.gifSearchBot);
        editor.putString("imageSearchBot", this.imageSearchBot);
        editor.putString("dcDomainName2", this.dcDomainName);
        editor.putInt("webFileDatacenterId", this.webFileDatacenterId);
        editor.putString("suggestedLangCode", this.suggestedLangCode);
        editor.putBoolean("forceTryIpV6", forceTryIpV6);
        editor.commit();
        getConnectionsManager().setForceTryIpV6(forceTryIpV6);
        LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(this.currentAccount, config.lang_pack_version, config.base_lang_pack_version);
        getNotificationCenter().postNotificationName(NotificationCenter.configLoaded, new Object[0]);
    }

    public void addSupportUser() {
        TLRPC.TL_userForeign_old2 user = new TLRPC.TL_userForeign_old2();
        user.phone = "333";
        user.id = 333000L;
        user.first_name = "Telegram";
        user.last_name = "";
        user.status = null;
        user.photo = new TLRPC.TL_userProfilePhotoEmpty();
        putUser(user, true);
        TLRPC.TL_userForeign_old2 user2 = new TLRPC.TL_userForeign_old2();
        user2.phone = "42777";
        user2.id = 777000L;
        user2.verified = true;
        user2.first_name = "Telegram";
        user2.last_name = "Notifications";
        user2.status = null;
        user2.photo = new TLRPC.TL_userProfilePhotoEmpty();
        putUser(user2, true);
    }

    public TLRPC.InputUser getInputUser(TLRPC.User user) {
        if (user == null) {
            return new TLRPC.TL_inputUserEmpty();
        }
        if (user.id == getUserConfig().getClientUserId()) {
            return new TLRPC.TL_inputUserSelf();
        }
        TLRPC.InputUser inputUser = new TLRPC.TL_inputUser();
        inputUser.user_id = user.id;
        inputUser.access_hash = user.access_hash;
        return inputUser;
    }

    public TLRPC.InputUser getInputUser(TLRPC.InputPeer peer) {
        if (peer == null) {
            return new TLRPC.TL_inputUserEmpty();
        }
        if (peer instanceof TLRPC.TL_inputPeerSelf) {
            return new TLRPC.TL_inputUserSelf();
        }
        TLRPC.TL_inputUser inputUser = new TLRPC.TL_inputUser();
        inputUser.user_id = peer.user_id;
        inputUser.access_hash = peer.access_hash;
        return inputUser;
    }

    public TLRPC.InputUser getInputUser(long userId) {
        return getInputUser(getUser(Long.valueOf(userId)));
    }

    public static TLRPC.InputChannel getInputChannel(TLRPC.Chat chat) {
        if ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) {
            TLRPC.InputChannel inputChat = new TLRPC.TL_inputChannel();
            inputChat.channel_id = chat.id;
            inputChat.access_hash = chat.access_hash;
            return inputChat;
        }
        return new TLRPC.TL_inputChannelEmpty();
    }

    public static TLRPC.InputChannel getInputChannel(TLRPC.InputPeer peer) {
        TLRPC.TL_inputChannel inputChat = new TLRPC.TL_inputChannel();
        inputChat.channel_id = peer.channel_id;
        inputChat.access_hash = peer.access_hash;
        return inputChat;
    }

    public TLRPC.InputChannel getInputChannel(long chatId) {
        return getInputChannel(getChat(Long.valueOf(chatId)));
    }

    public TLRPC.InputPeer getInputPeer(TLRPC.Peer peer) {
        if (peer instanceof TLRPC.TL_peerChat) {
            TLRPC.InputPeer inputPeer = new TLRPC.TL_inputPeerChat();
            inputPeer.chat_id = peer.chat_id;
            return inputPeer;
        } else if (peer instanceof TLRPC.TL_peerChannel) {
            TLRPC.InputPeer inputPeer2 = new TLRPC.TL_inputPeerChannel();
            inputPeer2.channel_id = peer.channel_id;
            TLRPC.Chat chat = getChat(Long.valueOf(peer.channel_id));
            if (chat != null) {
                inputPeer2.access_hash = chat.access_hash;
                return inputPeer2;
            }
            return inputPeer2;
        } else {
            TLRPC.InputPeer inputPeer3 = new TLRPC.TL_inputPeerUser();
            inputPeer3.user_id = peer.user_id;
            TLRPC.User user = getUser(Long.valueOf(peer.user_id));
            if (user != null) {
                inputPeer3.access_hash = user.access_hash;
                return inputPeer3;
            }
            return inputPeer3;
        }
    }

    public TLRPC.InputPeer getInputPeer(long id) {
        if (id < 0) {
            TLRPC.Chat chat = getChat(Long.valueOf(-id));
            if (ChatObject.isChannel(chat)) {
                TLRPC.InputPeer inputPeer = new TLRPC.TL_inputPeerChannel();
                inputPeer.channel_id = -id;
                inputPeer.access_hash = chat.access_hash;
                return inputPeer;
            }
            TLRPC.InputPeer inputPeer2 = new TLRPC.TL_inputPeerChat();
            inputPeer2.chat_id = -id;
            return inputPeer2;
        }
        TLRPC.User user = getUser(Long.valueOf(id));
        TLRPC.InputPeer inputPeer3 = new TLRPC.TL_inputPeerUser();
        inputPeer3.user_id = id;
        if (user != null) {
            inputPeer3.access_hash = user.access_hash;
            return inputPeer3;
        }
        return inputPeer3;
    }

    public static TLRPC.InputPeer getInputPeer(TLRPC.Chat chat) {
        if (ChatObject.isChannel(chat)) {
            TLRPC.InputPeer inputPeer = new TLRPC.TL_inputPeerChannel();
            inputPeer.channel_id = chat.id;
            inputPeer.access_hash = chat.access_hash;
            return inputPeer;
        }
        TLRPC.InputPeer inputPeer2 = new TLRPC.TL_inputPeerChat();
        inputPeer2.chat_id = chat.id;
        return inputPeer2;
    }

    public static TLRPC.InputPeer getInputPeer(TLRPC.User user) {
        TLRPC.InputPeer inputPeer = new TLRPC.TL_inputPeerUser();
        inputPeer.user_id = user.id;
        inputPeer.access_hash = user.access_hash;
        return inputPeer;
    }

    public TLRPC.Peer getPeer(long id) {
        if (id < 0) {
            TLRPC.Chat chat = getChat(Long.valueOf(-id));
            if ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) {
                TLRPC.Peer inputPeer = new TLRPC.TL_peerChannel();
                inputPeer.channel_id = -id;
                return inputPeer;
            }
            TLRPC.Peer inputPeer2 = new TLRPC.TL_peerChat();
            inputPeer2.chat_id = -id;
            return inputPeer2;
        }
        getUser(Long.valueOf(id));
        TLRPC.Peer inputPeer3 = new TLRPC.TL_peerUser();
        inputPeer3.user_id = id;
        return inputPeer3;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        MessageObject existMessageObject;
        TLRPC.InputFile uploadedFile;
        TLRPC.InputFile uploadedThumb;
        Theme.ThemeAccent accent;
        Theme.ThemeInfo themeInfo;
        TLRPC.TL_account_uploadTheme req;
        TLRPC.TL_inputThemeSettings settings;
        TLRPC.TL_theme info;
        long j;
        if (id != NotificationCenter.fileUploaded) {
            if (id == NotificationCenter.fileUploadFailed) {
                String location = (String) args[0];
                String str = this.uploadingAvatar;
                if (str != null && str.equals(location)) {
                    this.uploadingAvatar = null;
                    return;
                }
                String str2 = this.uploadingWallpaper;
                if (str2 != null && str2.equals(location)) {
                    this.uploadingWallpaper = null;
                    this.uploadingWallpaperInfo = null;
                    return;
                }
                Object object = this.uploadingThemes.remove(location);
                if (object instanceof Theme.ThemeInfo) {
                    Theme.ThemeInfo themeInfo2 = (Theme.ThemeInfo) object;
                    themeInfo2.uploadedFile = null;
                    themeInfo2.uploadedThumb = null;
                    getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo2, 0);
                    return;
                } else if (object instanceof Theme.ThemeAccent) {
                    Theme.ThemeAccent accent2 = (Theme.ThemeAccent) object;
                    accent2.uploadingThumb = null;
                    getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, accent2.parentTheme, accent2);
                    return;
                } else {
                    return;
                }
            } else if (id == NotificationCenter.messageReceivedByServer) {
                Boolean scheduled = (Boolean) args[6];
                if (scheduled.booleanValue()) {
                    return;
                }
                Integer msgId = (Integer) args[0];
                Integer newMsgId = (Integer) args[1];
                Long did = (Long) args[3];
                MessageObject obj = this.dialogMessage.get(did.longValue());
                if (obj != null && (obj.getId() == msgId.intValue() || obj.messageOwner.local_id == msgId.intValue())) {
                    obj.messageOwner.id = newMsgId.intValue();
                    obj.messageOwner.send_state = 0;
                }
                TLRPC.Dialog dialog = this.dialogs_dict.get(did.longValue());
                if (dialog != null && dialog.top_message == msgId.intValue()) {
                    dialog.top_message = newMsgId.intValue();
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
                MessageObject obj2 = this.dialogMessagesByIds.get(msgId.intValue());
                if (obj2 != null) {
                    this.dialogMessagesByIds.remove(msgId.intValue());
                    this.dialogMessagesByIds.put(newMsgId.intValue(), obj2);
                }
                if (DialogObject.isChatDialog(did.longValue())) {
                    TLRPC.ChatFull chatFull = this.fullChats.get(-did.longValue());
                    TLRPC.Chat chat = getChat(Long.valueOf(-did.longValue()));
                    if (chat != null && !ChatObject.hasAdminRights(chat) && chatFull != null && chatFull.slowmode_seconds != 0) {
                        chatFull.slowmode_next_send_date = getConnectionsManager().getCurrentTime() + chatFull.slowmode_seconds;
                        chatFull.flags |= 262144;
                        getMessagesStorage().updateChatInfo(chatFull, false);
                        return;
                    }
                    return;
                }
                return;
            } else if (id == NotificationCenter.updateMessageMedia) {
                TLRPC.Message message = (TLRPC.Message) args[0];
                if (message.peer_id.channel_id == 0 && (existMessageObject = this.dialogMessagesByIds.get(message.id)) != null) {
                    existMessageObject.messageOwner.media = message.media;
                    if (message.media.ttl_seconds == 0) {
                        return;
                    }
                    if ((message.media.photo instanceof TLRPC.TL_photoEmpty) || (message.media.document instanceof TLRPC.TL_documentEmpty)) {
                        existMessageObject.setType();
                        getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                        return;
                    }
                    return;
                }
                return;
            } else {
                return;
            }
        }
        String location2 = (String) args[0];
        TLRPC.InputFile file = (TLRPC.InputFile) args[1];
        String str3 = this.uploadingAvatar;
        if (str3 == null || !str3.equals(location2)) {
            String str4 = this.uploadingWallpaper;
            if (str4 != null && str4.equals(location2)) {
                TLRPC.TL_account_uploadWallPaper req2 = new TLRPC.TL_account_uploadWallPaper();
                req2.file = file;
                req2.mime_type = "image/jpeg";
                final Theme.OverrideWallpaperInfo overrideWallpaperInfo = this.uploadingWallpaperInfo;
                final TLRPC.TL_wallPaperSettings settings2 = new TLRPC.TL_wallPaperSettings();
                settings2.blur = overrideWallpaperInfo.isBlurred;
                settings2.motion = overrideWallpaperInfo.isMotion;
                req2.settings = settings2;
                getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda259
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m656xb04732dc(overrideWallpaperInfo, settings2, tLObject, tL_error);
                    }
                });
                return;
            }
            Object object2 = this.uploadingThemes.get(location2);
            if (object2 instanceof Theme.ThemeInfo) {
                Theme.ThemeInfo themeInfo3 = (Theme.ThemeInfo) object2;
                if (location2.equals(themeInfo3.uploadingThumb)) {
                    themeInfo3.uploadedThumb = file;
                    themeInfo3.uploadingThumb = null;
                } else if (location2.equals(themeInfo3.uploadingFile)) {
                    themeInfo3.uploadedFile = file;
                    themeInfo3.uploadingFile = null;
                }
                uploadedThumb = themeInfo3.uploadedThumb;
                uploadedFile = themeInfo3.uploadedFile;
                themeInfo = themeInfo3;
                accent = null;
            } else if (object2 instanceof Theme.ThemeAccent) {
                Theme.ThemeAccent accent3 = (Theme.ThemeAccent) object2;
                if (location2.equals(accent3.uploadingThumb)) {
                    accent3.uploadedThumb = file;
                    accent3.uploadingThumb = null;
                } else if (location2.equals(accent3.uploadingFile)) {
                    accent3.uploadedFile = file;
                    accent3.uploadingFile = null;
                }
                Theme.ThemeInfo themeInfo4 = accent3.parentTheme;
                uploadedThumb = accent3.uploadedThumb;
                uploadedFile = accent3.uploadedFile;
                themeInfo = themeInfo4;
                accent = accent3;
            } else {
                uploadedThumb = null;
                uploadedFile = null;
                themeInfo = null;
                accent = null;
            }
            this.uploadingThemes.remove(location2);
            if (uploadedFile != null && uploadedThumb != null) {
                new File(location2);
                TLRPC.TL_account_uploadTheme req3 = new TLRPC.TL_account_uploadTheme();
                req3.mime_type = "application/x-tgtheme-android";
                req3.file_name = "theme.attheme";
                req3.file = uploadedFile;
                req3.file.name = "theme.attheme";
                req3.thumb = uploadedThumb;
                req3.thumb.name = "theme-preview.jpg";
                req3.flags |= 1;
                if (accent != null) {
                    accent.uploadedFile = null;
                    accent.uploadedThumb = null;
                    TLRPC.TL_theme info2 = accent.info;
                    TLRPC.TL_inputThemeSettings settings3 = new TLRPC.TL_inputThemeSettings();
                    settings3.base_theme = Theme.getBaseThemeByKey(themeInfo.name);
                    settings3.accent_color = accent.accentColor;
                    if (accent.accentColor2 != 0) {
                        settings3.flags |= 8;
                        settings3.outbox_accent_color = accent.accentColor2;
                    }
                    if (accent.myMessagesAccentColor != 0) {
                        settings3.message_colors.add(Integer.valueOf(accent.myMessagesAccentColor));
                        settings3.flags |= 1;
                        if (accent.myMessagesGradientAccentColor1 != 0) {
                            settings3.message_colors.add(Integer.valueOf(accent.myMessagesGradientAccentColor1));
                            if (accent.myMessagesGradientAccentColor2 != 0) {
                                settings3.message_colors.add(Integer.valueOf(accent.myMessagesGradientAccentColor2));
                                if (accent.myMessagesGradientAccentColor3 != 0) {
                                    settings3.message_colors.add(Integer.valueOf(accent.myMessagesGradientAccentColor3));
                                }
                            }
                        }
                        settings3.message_colors_animated = accent.myMessagesAnimated;
                    }
                    settings3.flags = 2 | settings3.flags;
                    settings3.wallpaper_settings = new TLRPC.TL_wallPaperSettings();
                    if (!TextUtils.isEmpty(accent.patternSlug)) {
                        TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                        inputWallPaperSlug.slug = accent.patternSlug;
                        settings3.wallpaper = inputWallPaperSlug;
                        settings3.wallpaper_settings.intensity = (int) (accent.patternIntensity * 100.0f);
                        settings3.wallpaper_settings.flags |= 8;
                        req = req3;
                        j = 0;
                    } else {
                        TLRPC.TL_inputWallPaperNoFile inputWallPaperNoFile = new TLRPC.TL_inputWallPaperNoFile();
                        req = req3;
                        j = 0;
                        inputWallPaperNoFile.id = 0L;
                        settings3.wallpaper = inputWallPaperNoFile;
                    }
                    settings3.wallpaper_settings.motion = accent.patternMotion;
                    info = info2;
                    if (accent.backgroundOverrideColor != j) {
                        settings3.wallpaper_settings.background_color = (int) accent.backgroundOverrideColor;
                        settings3.wallpaper_settings.flags |= 1;
                    }
                    if (accent.backgroundGradientOverrideColor1 != 0) {
                        settings3.wallpaper_settings.second_background_color = (int) accent.backgroundGradientOverrideColor1;
                        settings3.wallpaper_settings.flags |= 16;
                        settings3.wallpaper_settings.rotation = AndroidUtilities.getWallpaperRotation(accent.backgroundRotation, true);
                    }
                    if (accent.backgroundGradientOverrideColor2 != 0) {
                        settings3.wallpaper_settings.third_background_color = (int) accent.backgroundGradientOverrideColor2;
                        settings3.wallpaper_settings.flags |= 32;
                    }
                    if (accent.backgroundGradientOverrideColor3 != 0) {
                        settings3.wallpaper_settings.fourth_background_color = (int) accent.backgroundGradientOverrideColor3;
                        settings3.wallpaper_settings.flags |= 64;
                    }
                    settings = settings3;
                } else {
                    req = req3;
                    themeInfo.uploadedFile = null;
                    themeInfo.uploadedThumb = null;
                    TLRPC.TL_theme info3 = themeInfo.info;
                    info = info3;
                    settings = null;
                }
                final TLRPC.TL_theme tL_theme = info;
                final Theme.ThemeInfo themeInfo5 = themeInfo;
                final TLRPC.TL_inputThemeSettings tL_inputThemeSettings = settings;
                final Theme.ThemeAccent themeAccent = accent;
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda252
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m662xde2de137(tL_theme, themeInfo5, tL_inputThemeSettings, themeAccent, tLObject, tL_error);
                    }
                });
                return;
            }
            return;
        }
        TLRPC.TL_photos_uploadProfilePhoto req4 = new TLRPC.TL_photos_uploadProfilePhoto();
        req4.file = file;
        req4.flags |= 1;
        getConnectionsManager().sendRequest(req4, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda151
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m654xe5c4555a(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$25$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m654xe5c4555a(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                putUser(user, true);
            } else {
                getUserConfig().setCurrentUser(user);
            }
            if (user == null) {
                return;
            }
            TLRPC.TL_photos_photo photo = (TLRPC.TL_photos_photo) response;
            ArrayList<TLRPC.PhotoSize> sizes = photo.photo.sizes;
            TLRPC.PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 100);
            TLRPC.PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 1000);
            user.photo = new TLRPC.TL_userProfilePhoto();
            user.photo.photo_id = photo.photo.id;
            if (smallSize != null) {
                user.photo.photo_small = smallSize.location;
            }
            if (bigSize != null) {
                user.photo.photo_big = bigSize.location;
            }
            getMessagesStorage().clearUserPhotos(user.id);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            users.add(user);
            getMessagesStorage().putUsersAndChats(users, null, false, true);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda77
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m653x82e699();
                }
            });
        }
    }

    /* renamed from: lambda$didReceivedNotification$24$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m653x82e699() {
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        getUserConfig().saveConfig(true);
    }

    /* renamed from: lambda$didReceivedNotification$27$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m656xb04732dc(final Theme.OverrideWallpaperInfo overrideWallpaperInfo, final TLRPC.TL_wallPaperSettings settings, TLObject response, TLRPC.TL_error error) {
        final TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) response;
        final File path = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.originalFileName);
        if (wallPaper != null) {
            try {
                AndroidUtilities.copyFile(path, getFileLoader().getPathToAttach(wallPaper.document, true));
            } catch (Exception e) {
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda98
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m655xcb05c41b(wallPaper, settings, overrideWallpaperInfo, path);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$26$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m655xcb05c41b(TLRPC.WallPaper wallPaper, TLRPC.TL_wallPaperSettings settings, Theme.OverrideWallpaperInfo overrideWallpaperInfo, File path) {
        if (this.uploadingWallpaper != null && wallPaper != null) {
            wallPaper.settings = settings;
            wallPaper.flags |= 4;
            overrideWallpaperInfo.slug = wallPaper.slug;
            overrideWallpaperInfo.saveOverrideWallpaper();
            ArrayList<TLRPC.WallPaper> wallpapers = new ArrayList<>();
            wallpapers.add(wallPaper);
            getMessagesStorage().putWallpapers(wallpapers, 2);
            TLRPC.PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, GroupCallActivity.TABLET_LIST_SIZE);
            if (image != null) {
                String newKey = image.location.volume_id + "_" + image.location.local_id + "@100_100";
                String oldKey = Utilities.MD5(path.getAbsolutePath()) + "@100_100";
                ImageLoader.getInstance().replaceImageInCache(oldKey, newKey, ImageLocation.getForDocument(image, wallPaper.document), false);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersNeedReload, wallPaper.slug);
        }
    }

    /* renamed from: lambda$didReceivedNotification$33$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m662xde2de137(TLRPC.TL_theme info, final Theme.ThemeInfo themeInfo, TLRPC.TL_inputThemeSettings settings, final Theme.ThemeAccent accent, TLObject response, TLRPC.TL_error error) {
        String title = info != null ? info.title : themeInfo.getName();
        int index = title.lastIndexOf(".attheme");
        String n = index > 0 ? title.substring(0, index) : title;
        if (response != null) {
            TLRPC.Document document = (TLRPC.Document) response;
            TLRPC.TL_inputDocument inputDocument = new TLRPC.TL_inputDocument();
            inputDocument.access_hash = document.access_hash;
            inputDocument.id = document.id;
            inputDocument.file_reference = document.file_reference;
            if (info == null || !info.creator) {
                TLRPC.TL_account_createTheme req2 = new TLRPC.TL_account_createTheme();
                req2.document = inputDocument;
                req2.flags |= 4;
                req2.slug = (info == null || TextUtils.isEmpty(info.slug)) ? "" : info.slug;
                req2.title = n;
                if (settings != null) {
                    req2.settings = settings;
                    req2.flags |= 8;
                }
                getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda260
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m658x7aca105e(themeInfo, accent, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_account_updateTheme req22 = new TLRPC.TL_account_updateTheme();
            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            inputTheme.id = info.id;
            inputTheme.access_hash = info.access_hash;
            req22.theme = inputTheme;
            req22.slug = info.slug;
            req22.flags |= 1;
            req22.title = n;
            req22.flags |= 2;
            req22.document = inputDocument;
            req22.flags |= 4;
            if (settings != null) {
                req22.settings = settings;
                req22.flags |= 8;
            }
            req22.format = "android";
            getConnectionsManager().sendRequest(req22, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda261
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m660x13ab03b5(themeInfo, accent, tLObject, tL_error);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda117
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m661xf8ec7276(themeInfo, accent);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$29$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m658x7aca105e(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent accent, final TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m657x9588a19d(response1, themeInfo, accent);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$28$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m657x9588a19d(TLObject response1, Theme.ThemeInfo themeInfo, Theme.ThemeAccent accent) {
        if (response1 instanceof TLRPC.TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, accent, (TLRPC.TL_theme) response1, this.currentAccount, false);
            installTheme(themeInfo, accent, themeInfo == Theme.getCurrentNightTheme());
            getNotificationCenter().postNotificationName(NotificationCenter.themeUploadedToServer, themeInfo, accent);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, accent);
    }

    /* renamed from: lambda$didReceivedNotification$31$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m660x13ab03b5(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent accent, final TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m659x2e6994f4(response1, themeInfo, accent);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$30$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m659x2e6994f4(TLObject response1, Theme.ThemeInfo themeInfo, Theme.ThemeAccent accent) {
        if (response1 instanceof TLRPC.TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, accent, (TLRPC.TL_theme) response1, this.currentAccount, false);
            getNotificationCenter().postNotificationName(NotificationCenter.themeUploadedToServer, themeInfo, accent);
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, accent);
    }

    /* renamed from: lambda$didReceivedNotification$32$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m661xf8ec7276(Theme.ThemeInfo themeInfo, Theme.ThemeAccent accent) {
        getNotificationCenter().postNotificationName(NotificationCenter.themeUploadError, themeInfo, accent);
    }

    public void cleanup() {
        getContactsController().cleanup();
        MediaController.getInstance().cleanup();
        getNotificationsController().cleanup();
        getSendMessagesHelper().cleanup();
        getSecretChatHelper().cleanup();
        getLocationController().cleanup();
        getMediaDataController().cleanup();
        this.showFiltersTooltip = false;
        DialogsActivity.dialogsLoaded[this.currentAccount] = false;
        SharedPreferences.Editor editor = this.notificationsPreferences.edit();
        editor.clear().commit();
        SharedPreferences.Editor editor2 = this.emojiPreferences.edit();
        editor2.putLong("lastGifLoadTime", 0L).putLong("lastStickersLoadTime", 0L).putLong("lastStickersLoadTimeMask", 0L).putLong("lastStickersLoadTimeFavs", 0L).commit();
        SharedPreferences.Editor editor3 = this.mainPreferences.edit();
        editor3.remove("archivehint").remove("proximityhint").remove("archivehint_l").remove("gifhint").remove("reminderhint").remove("soundHint").remove("dcDomainName2").remove("webFileDatacenterId").remove("themehint").remove("showFiltersTooltip").commit();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("shortcut_widget", 0);
        SharedPreferences.Editor widgetEditor = null;
        AppWidgetManager appWidgetManager = null;
        ArrayList<Integer> chatsWidgets = null;
        ArrayList<Integer> contactsWidgets = null;
        Map<String, ?> values = preferences.getAll();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("account")) {
                Integer value = (Integer) entry.getValue();
                if (value.intValue() == this.currentAccount) {
                    int widgetId = Utilities.parseInt((CharSequence) key).intValue();
                    if (widgetEditor == null) {
                        widgetEditor = preferences.edit();
                        appWidgetManager = AppWidgetManager.getInstance(ApplicationLoader.applicationContext);
                    }
                    widgetEditor.putBoolean("deleted" + widgetId, true);
                    if (preferences.getInt(CommonProperties.TYPE + widgetId, 0) == 0) {
                        if (chatsWidgets == null) {
                            chatsWidgets = new ArrayList<>();
                        }
                        chatsWidgets.add(Integer.valueOf(widgetId));
                    } else {
                        if (contactsWidgets == null) {
                            contactsWidgets = new ArrayList<>();
                        }
                        contactsWidgets.add(Integer.valueOf(widgetId));
                    }
                }
            }
        }
        if (widgetEditor != null) {
            widgetEditor.commit();
        }
        if (chatsWidgets != null) {
            int N = chatsWidgets.size();
            for (int a = 0; a < N; a++) {
                ChatsWidgetProvider.updateWidget(ApplicationLoader.applicationContext, appWidgetManager, chatsWidgets.get(a).intValue());
            }
        }
        if (contactsWidgets != null) {
            int N2 = contactsWidgets.size();
            for (int a2 = 0; a2 < N2; a2++) {
                ContactsWidgetProvider.updateWidget(ApplicationLoader.applicationContext, appWidgetManager, contactsWidgets.get(a2).intValue());
            }
        }
        this.lastScheduledServerQueryTime.clear();
        this.lastServerQueryTime.clear();
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
        this.reloadingScheduledWebpages.clear();
        this.reloadingScheduledWebpagesPending.clear();
        this.sponsoredMessages.clear();
        this.sendAsPeers.clear();
        this.dialogs_dict.clear();
        this.dialogs_read_inbox_max.clear();
        this.loadingPinnedDialogs.clear();
        this.dialogs_read_outbox_max.clear();
        this.exportedChats.clear();
        this.fullUsers.clear();
        this.fullChats.clear();
        this.activeVoiceChatsMap.clear();
        this.loadingGroupCalls.clear();
        this.groupCallsByChatId.clear();
        this.dialogsByFolder.clear();
        this.unreadUnmutedDialogs = 0;
        this.joiningToChannels.clear();
        this.migratedChats.clear();
        this.channelViewsToSend.clear();
        this.pollsToCheck.clear();
        this.pollsToCheckSize = 0;
        this.dialogsServerOnly.clear();
        this.dialogsForward.clear();
        this.allDialogs.clear();
        this.dialogsLoadedTillDate = Integer.MAX_VALUE;
        this.dialogsCanAddUsers.clear();
        this.dialogsMyChannels.clear();
        this.dialogsMyGroups.clear();
        this.dialogsChannelsOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsUsersOnly.clear();
        this.dialogsForBlock.clear();
        this.dialogMessagesByIds.clear();
        this.dialogMessagesByRandomIds.clear();
        this.channelAdmins.clear();
        this.loadingChannelAdmins.clear();
        this.users.clear();
        this.objectsByUsernames.clear();
        this.chats.clear();
        this.dialogMessage.clear();
        this.deletedHistory.clear();
        this.printingUsers.clear();
        this.printingStrings.clear();
        this.printingStringsTypes.clear();
        this.onlinePrivacy.clear();
        this.loadingPeerSettings.clear();
        this.deletingDialogs.clear();
        this.clearingHistoryDialogs.clear();
        this.lastPrintingStringCount = 0;
        DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
        dialogFilterArr[1] = null;
        dialogFilterArr[0] = null;
        this.dialogFilters.clear();
        this.dialogFiltersById.clear();
        this.loadingSuggestedFilters = false;
        this.loadingRemoteFilters = false;
        this.suggestedFilters.clear();
        this.gettingAppChangelog = false;
        this.dialogFiltersLoaded = false;
        this.ignoreSetOnline = false;
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m615lambda$cleanup$34$orgtelegrammessengerMessagesController();
            }
        });
        this.createdDialogMainThreadIds.clear();
        this.visibleDialogMainThreadIds.clear();
        this.visibleScheduledDialogMainThreadIds.clear();
        this.blockePeers.clear();
        int a3 = 0;
        while (true) {
            LongSparseArray<SparseArray<Boolean>>[] longSparseArrayArr = this.sendingTypings;
            if (a3 >= longSparseArrayArr.length) {
                break;
            }
            if (longSparseArrayArr[a3] != null) {
                longSparseArrayArr[a3].clear();
            }
            a3++;
        }
        this.loadingFullUsers.clear();
        this.loadedFullUsers.clear();
        this.reloadingMessages.clear();
        this.loadingFullChats.clear();
        this.loadingFullParticipants.clear();
        this.loadedFullParticipants.clear();
        this.loadedFullChats.clear();
        this.dialogsLoaded = false;
        this.nextDialogsCacheOffset.clear();
        this.loadingDialogs.clear();
        this.dialogsEndReached.clear();
        this.serverDialogsEndReached.clear();
        this.loadingAppConfig = false;
        this.checkingTosUpdate = false;
        this.nextTosCheckTime = 0;
        this.nextPromoInfoCheckTime = 0;
        this.checkingPromoInfo = false;
        this.loadingUnreadDialogs = false;
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
        this.gettingNewDeleteTask = false;
        this.loadingBlockedPeers = false;
        this.totalBlockedCount = -1;
        this.blockedEndReached = false;
        this.firstGettingTask = false;
        this.updatingState = false;
        this.resetingDialogs = false;
        this.lastStatusUpdateTime = 0L;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.getDifferenceFirstSync = true;
        this.uploadingAvatar = null;
        this.uploadingWallpaper = null;
        this.uploadingWallpaperInfo = null;
        this.uploadingThemes.clear();
        this.gettingChatInviters.clear();
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m616lambda$cleanup$35$orgtelegrammessengerMessagesController();
            }
        });
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda55
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m617lambda$cleanup$36$orgtelegrammessengerMessagesController();
            }
        });
    }

    /* renamed from: lambda$cleanup$34$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m615lambda$cleanup$34$orgtelegrammessengerMessagesController() {
        this.readTasks.clear();
        this.readTasksMap.clear();
        this.repliesReadTasks.clear();
        this.threadsReadTasksMap.clear();
        this.updatesQueueSeq.clear();
        this.updatesQueuePts.clear();
        this.updatesQueueQts.clear();
        this.gettingUnknownChannels.clear();
        this.gettingUnknownDialogs.clear();
        this.updatesStartWaitTimeSeq = 0L;
        this.updatesStartWaitTimePts = 0L;
        this.updatesStartWaitTimeQts = 0L;
        this.createdDialogIds.clear();
        this.createdScheduledDialogIds.clear();
        this.gettingDifference = false;
        this.resetDialogsPinned = null;
        this.resetDialogsAll = null;
    }

    /* renamed from: lambda$cleanup$35$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m616lambda$cleanup$35$orgtelegrammessengerMessagesController() {
        getConnectionsManager().setIsUpdating(false);
        this.updatesQueueChannels.clear();
        this.updatesStartWaitTimeChannels.clear();
        this.gettingDifferenceChannels.clear();
        this.channelsPts.clear();
        this.shortPollChannels.clear();
        this.needShortPollChannels.clear();
        this.shortPollOnlines.clear();
        this.needShortPollOnlines.clear();
    }

    /* renamed from: lambda$cleanup$36$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m617lambda$cleanup$36$orgtelegrammessengerMessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.suggestedFiltersLoaded, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public boolean isChatNoForwards(TLRPC.Chat chat) {
        TLRPC.Chat migratedTo;
        if (chat == null) {
            return false;
        }
        if (chat.migrated_to != null && (migratedTo = getChat(Long.valueOf(chat.migrated_to.channel_id))) != null) {
            return migratedTo.noforwards;
        }
        return chat.noforwards;
    }

    public boolean isChatNoForwards(long chatId) {
        return isChatNoForwards(getChat(Long.valueOf(chatId)));
    }

    public TLRPC.User getUser(Long id) {
        if (id.longValue() == 0) {
            return UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        return this.users.get(id);
    }

    public TLObject getUserOrChat(String username) {
        if (username == null || username.length() == 0) {
            return null;
        }
        return this.objectsByUsernames.get(username.toLowerCase());
    }

    public ConcurrentHashMap<Long, TLRPC.User> getUsers() {
        return this.users;
    }

    public ConcurrentHashMap<Long, TLRPC.Chat> getChats() {
        return this.chats;
    }

    public TLRPC.Chat getChat(Long id) {
        return this.chats.get(id);
    }

    public TLRPC.EncryptedChat getEncryptedChat(Integer id) {
        return this.encryptedChats.get(id);
    }

    public TLRPC.EncryptedChat getEncryptedChatDB(int chatId, boolean created) {
        TLRPC.EncryptedChat chat = this.encryptedChats.get(Integer.valueOf(chatId));
        if (chat != null) {
            if (!created) {
                return chat;
            }
            if (!(chat instanceof TLRPC.TL_encryptedChatWaiting) && !(chat instanceof TLRPC.TL_encryptedChatRequested)) {
                return chat;
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ArrayList<TLObject> result = new ArrayList<>();
        getMessagesStorage().getEncryptedChat(chatId, countDownLatch, result);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (result.size() == 2) {
            TLRPC.EncryptedChat chat2 = (TLRPC.EncryptedChat) result.get(0);
            TLRPC.User user = (TLRPC.User) result.get(1);
            putEncryptedChat(chat2, false);
            putUser(user, true);
            return chat2;
        }
        return chat;
    }

    public boolean isDialogVisible(long dialogId, boolean scheduled) {
        return (scheduled ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds).contains(Long.valueOf(dialogId));
    }

    public void setLastVisibleDialogId(long dialogId, boolean scheduled, boolean set) {
        ArrayList<Long> arrayList = scheduled ? this.visibleScheduledDialogMainThreadIds : this.visibleDialogMainThreadIds;
        if (set) {
            if (arrayList.contains(Long.valueOf(dialogId))) {
                return;
            }
            arrayList.add(Long.valueOf(dialogId));
            return;
        }
        arrayList.remove(Long.valueOf(dialogId));
    }

    public void setLastCreatedDialogId(final long dialogId, final boolean scheduled, final boolean set) {
        if (!scheduled) {
            ArrayList<Long> arrayList = this.createdDialogMainThreadIds;
            if (set) {
                if (arrayList.contains(Long.valueOf(dialogId))) {
                    return;
                }
                arrayList.add(Long.valueOf(dialogId));
            } else {
                arrayList.remove(Long.valueOf(dialogId));
                SparseArray<MessageObject> array = this.pollsToCheck.get(dialogId);
                if (array != null) {
                    int N = array.size();
                    for (int a = 0; a < N; a++) {
                        MessageObject object = array.valueAt(a);
                        object.pollVisibleOnScreen = false;
                    }
                }
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda125
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m857x97cf9659(scheduled, set, dialogId);
            }
        });
    }

    /* renamed from: lambda$setLastCreatedDialogId$37$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m857x97cf9659(boolean scheduled, boolean set, long dialogId) {
        ArrayList<Long> arrayList2 = scheduled ? this.createdScheduledDialogIds : this.createdDialogIds;
        if (set) {
            if (arrayList2.contains(Long.valueOf(dialogId))) {
                return;
            }
            arrayList2.add(Long.valueOf(dialogId));
            return;
        }
        arrayList2.remove(Long.valueOf(dialogId));
    }

    public TLRPC.TL_chatInviteExported getExportedInvite(long chatId) {
        return this.exportedChats.get(chatId);
    }

    public boolean putUser(TLRPC.User user, boolean fromCache) {
        if (user == null) {
            return false;
        }
        boolean fromCache2 = (!fromCache || user.id / 1000 == 333 || user.id == 777000) ? false : true;
        TLRPC.User oldUser = this.users.get(Long.valueOf(user.id));
        if (oldUser == user) {
            return false;
        }
        if (oldUser != null && !TextUtils.isEmpty(oldUser.username)) {
            this.objectsByUsernames.remove(oldUser.username.toLowerCase());
        }
        if (!TextUtils.isEmpty(user.username)) {
            this.objectsByUsernames.put(user.username.toLowerCase(), user);
        }
        if (user.min) {
            if (oldUser != null) {
                if (!fromCache2) {
                    if (user.bot) {
                        if (user.username != null) {
                            oldUser.username = user.username;
                            oldUser.flags |= 8;
                        } else {
                            oldUser.flags &= -9;
                            oldUser.username = null;
                        }
                    }
                    if (user.apply_min_photo) {
                        if (user.photo != null) {
                            oldUser.photo = user.photo;
                            oldUser.flags |= 32;
                        } else {
                            oldUser.flags &= -33;
                            oldUser.photo = null;
                        }
                    }
                }
            } else {
                this.users.put(Long.valueOf(user.id), user);
            }
        } else if (!fromCache2) {
            this.users.put(Long.valueOf(user.id), user);
            if (user.id == getUserConfig().getClientUserId()) {
                getUserConfig().setCurrentUser(user);
                getUserConfig().saveConfig(true);
            }
            if (oldUser != null && user.status != null && oldUser.status != null && user.status.expires != oldUser.status.expires) {
                return true;
            }
        } else if (oldUser == null) {
            this.users.put(Long.valueOf(user.id), user);
        } else if (oldUser.min) {
            if (oldUser.bot) {
                if (oldUser.username != null) {
                    user.username = oldUser.username;
                    user.flags |= 8;
                } else {
                    user.flags &= -9;
                    user.username = null;
                }
            }
            if (oldUser.apply_min_photo) {
                if (oldUser.photo != null) {
                    user.photo = oldUser.photo;
                    user.flags |= 32;
                } else {
                    user.flags &= -33;
                    user.photo = null;
                }
            }
            this.users.put(Long.valueOf(user.id), user);
        }
        return false;
    }

    public void putUsers(ArrayList<TLRPC.User> users, boolean fromCache) {
        if (users == null || users.isEmpty()) {
            return;
        }
        boolean updateStatus = false;
        int count = users.size();
        for (int a = 0; a < count; a++) {
            TLRPC.User user = users.get(a);
            if (putUser(user, fromCache)) {
                updateStatus = true;
            }
        }
        if (updateStatus) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda221
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m823lambda$putUsers$38$orgtelegrammessengerMessagesController();
                }
            });
        }
    }

    /* renamed from: lambda$putUsers$38$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m823lambda$putUsers$38$orgtelegrammessengerMessagesController() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    public void putChat(final TLRPC.Chat chat, boolean fromCache) {
        TLRPC.Chat oldChat;
        if (chat == null || (oldChat = this.chats.get(Long.valueOf(chat.id))) == chat) {
            return;
        }
        if (oldChat != null && !TextUtils.isEmpty(oldChat.username)) {
            this.objectsByUsernames.remove(oldChat.username.toLowerCase());
        }
        if (!TextUtils.isEmpty(chat.username)) {
            this.objectsByUsernames.put(chat.username.toLowerCase(), chat);
        }
        if (chat.min) {
            if (oldChat != null) {
                if (!fromCache) {
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
                        oldChat.flags &= -65;
                        oldChat.username = null;
                    }
                    if (chat.participants_count != 0) {
                        oldChat.participants_count = chat.participants_count;
                    }
                    addOrRemoveActiveVoiceChat(oldChat);
                    return;
                }
                return;
            }
            this.chats.put(Long.valueOf(chat.id), chat);
            addOrRemoveActiveVoiceChat(chat);
            return;
        }
        if (!fromCache) {
            if (oldChat != null) {
                if (chat.version != oldChat.version) {
                    this.loadedFullChats.remove(Long.valueOf(chat.id));
                }
                if (oldChat.participants_count != 0 && chat.participants_count == 0) {
                    chat.participants_count = oldChat.participants_count;
                    chat.flags = 131072 | chat.flags;
                }
                int newFlags2 = 0;
                int oldFlags = oldChat.banned_rights != null ? oldChat.banned_rights.flags : 0;
                int newFlags = chat.banned_rights != null ? chat.banned_rights.flags : 0;
                int oldFlags2 = oldChat.default_banned_rights != null ? oldChat.default_banned_rights.flags : 0;
                if (chat.default_banned_rights != null) {
                    newFlags2 = chat.default_banned_rights.flags;
                }
                oldChat.default_banned_rights = chat.default_banned_rights;
                if (oldChat.default_banned_rights == null) {
                    oldChat.flags &= -262145;
                } else {
                    oldChat.flags = 262144 | oldChat.flags;
                }
                oldChat.banned_rights = chat.banned_rights;
                if (oldChat.banned_rights == null) {
                    oldChat.flags &= -32769;
                } else {
                    oldChat.flags = 32768 | oldChat.flags;
                }
                oldChat.admin_rights = chat.admin_rights;
                if (oldChat.admin_rights == null) {
                    oldChat.flags &= -16385;
                } else {
                    oldChat.flags |= 16384;
                }
                if (oldFlags != newFlags || oldFlags2 != newFlags2) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda54
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesController.this.m822lambda$putChat$39$orgtelegrammessengerMessagesController(chat);
                        }
                    });
                }
            }
            this.chats.put(Long.valueOf(chat.id), chat);
        } else if (oldChat == null) {
            this.chats.put(Long.valueOf(chat.id), chat);
        } else if (oldChat.min) {
            chat.title = oldChat.title;
            chat.photo = oldChat.photo;
            chat.broadcast = oldChat.broadcast;
            chat.verified = oldChat.verified;
            chat.megagroup = oldChat.megagroup;
            if (oldChat.default_banned_rights != null) {
                chat.default_banned_rights = oldChat.default_banned_rights;
                chat.flags = 262144 | chat.flags;
            }
            if (oldChat.admin_rights != null) {
                chat.admin_rights = oldChat.admin_rights;
                chat.flags |= 16384;
            }
            if (oldChat.banned_rights != null) {
                chat.banned_rights = oldChat.banned_rights;
                chat.flags = 32768 | chat.flags;
            }
            if (oldChat.username != null) {
                chat.username = oldChat.username;
                chat.flags |= 64;
            } else {
                chat.flags &= -65;
                chat.username = null;
            }
            if (oldChat.participants_count != 0 && chat.participants_count == 0) {
                chat.participants_count = oldChat.participants_count;
                chat.flags = 131072 | chat.flags;
            }
            this.chats.put(Long.valueOf(chat.id), chat);
        }
        addOrRemoveActiveVoiceChat(chat);
    }

    /* renamed from: lambda$putChat$39$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m822lambda$putChat$39$orgtelegrammessengerMessagesController(TLRPC.Chat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    public void putChats(ArrayList<TLRPC.Chat> chats, boolean fromCache) {
        if (chats == null || chats.isEmpty()) {
            return;
        }
        int count = chats.size();
        for (int a = 0; a < count; a++) {
            TLRPC.Chat chat = chats.get(a);
            putChat(chat, fromCache);
        }
    }

    private void addOrRemoveActiveVoiceChat(final TLRPC.Chat chat) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda51
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m577xc487edc8(chat);
                }
            });
        } else {
            m577xc487edc8(chat);
        }
    }

    /* renamed from: addOrRemoveActiveVoiceChatInternal */
    public void m577xc487edc8(TLRPC.Chat chat) {
        TLRPC.Chat currentChat = this.activeVoiceChatsMap.get(Long.valueOf(chat.id));
        if (chat.call_active && chat.call_not_empty && chat.migrated_to == null && !ChatObject.isNotInChat(chat)) {
            if (currentChat != null) {
                return;
            }
            this.activeVoiceChatsMap.put(Long.valueOf(chat.id), chat);
            getNotificationCenter().postNotificationName(NotificationCenter.activeGroupCallsUpdated, new Object[0]);
        } else if (currentChat == null) {
        } else {
            this.activeVoiceChatsMap.remove(Long.valueOf(chat.id));
            getNotificationCenter().postNotificationName(NotificationCenter.activeGroupCallsUpdated, new Object[0]);
        }
    }

    public ArrayList<Long> getActiveGroupCalls() {
        return new ArrayList<>(this.activeVoiceChatsMap.keySet());
    }

    public void setReferer(String referer) {
        if (referer == null) {
            return;
        }
        this.installReferer = referer;
        this.mainPreferences.edit().putString("installReferer", referer).commit();
    }

    public void putEncryptedChat(TLRPC.EncryptedChat encryptedChat, boolean fromCache) {
        if (encryptedChat == null) {
            return;
        }
        if (fromCache) {
            this.encryptedChats.putIfAbsent(Integer.valueOf(encryptedChat.id), encryptedChat);
        } else {
            this.encryptedChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
        }
    }

    public void putEncryptedChats(ArrayList<TLRPC.EncryptedChat> encryptedChats, boolean fromCache) {
        if (encryptedChats == null || encryptedChats.isEmpty()) {
            return;
        }
        int count = encryptedChats.size();
        for (int a = 0; a < count; a++) {
            TLRPC.EncryptedChat encryptedChat = encryptedChats.get(a);
            putEncryptedChat(encryptedChat, fromCache);
        }
    }

    public TLRPC.UserFull getUserFull(long uid) {
        return this.fullUsers.get(uid);
    }

    public TLRPC.ChatFull getChatFull(long chatId) {
        return this.fullChats.get(chatId);
    }

    public void putGroupCall(long chatId, ChatObject.Call call) {
        this.groupCalls.put(call.call.id, call);
        this.groupCallsByChatId.put(chatId, call);
        TLRPC.ChatFull chatFull = getChatFull(chatId);
        if (chatFull != null) {
            chatFull.call = call.getInputGroupCall();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(chatId), Long.valueOf(call.call.id), false);
        loadFullChat(chatId, 0, true);
    }

    public ChatObject.Call getGroupCall(long chatId, boolean load) {
        return getGroupCall(chatId, load, null);
    }

    public ChatObject.Call getGroupCall(final long chatId, boolean load, final Runnable onLoad) {
        TLRPC.ChatFull chatFull = getChatFull(chatId);
        if (chatFull == null || chatFull.call == null) {
            return null;
        }
        ChatObject.Call result = this.groupCalls.get(chatFull.call.id);
        if (result == null && load && !this.loadingGroupCalls.contains(Long.valueOf(chatId))) {
            this.loadingGroupCalls.add(Long.valueOf(chatId));
            if (chatFull.call != null) {
                TLRPC.TL_phone_getGroupCall req = new TLRPC.TL_phone_getGroupCall();
                req.call = chatFull.call;
                req.limit = 20;
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda217
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m691lambda$getGroupCall$42$orgtelegrammessengerMessagesController(chatId, onLoad, tLObject, tL_error);
                    }
                });
            }
        }
        if (result != null && (result.call instanceof TLRPC.TL_groupCallDiscarded)) {
            return null;
        }
        return result;
    }

    /* renamed from: lambda$getGroupCall$42$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m691lambda$getGroupCall$42$orgtelegrammessengerMessagesController(final long chatId, final Runnable onLoad, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m690lambda$getGroupCall$41$orgtelegrammessengerMessagesController(response, chatId, onLoad);
            }
        });
    }

    /* renamed from: lambda$getGroupCall$41$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m690lambda$getGroupCall$41$orgtelegrammessengerMessagesController(TLObject response, long chatId, Runnable onLoad) {
        if (response != null) {
            TLRPC.TL_phone_groupCall groupCall = (TLRPC.TL_phone_groupCall) response;
            putUsers(groupCall.users, false);
            putChats(groupCall.chats, false);
            ChatObject.Call call = new ChatObject.Call();
            call.setCall(getAccountInstance(), chatId, groupCall);
            this.groupCalls.put(groupCall.call.id, call);
            this.groupCallsByChatId.put(chatId, call);
            getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(chatId), Long.valueOf(groupCall.call.id), false);
            if (onLoad != null) {
                onLoad.run();
            }
        }
        this.loadingGroupCalls.remove(Long.valueOf(chatId));
    }

    public void cancelLoadFullUser(long userId) {
        this.loadingFullUsers.remove(Long.valueOf(userId));
    }

    public void cancelLoadFullChat(long chatId) {
        this.loadingFullChats.remove(Long.valueOf(chatId));
    }

    public void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    private void reloadDialogsReadValue(ArrayList<TLRPC.Dialog> dialogs, long did) {
        if (did == 0 && (dialogs == null || dialogs.isEmpty())) {
            return;
        }
        TLRPC.TL_messages_getPeerDialogs req = new TLRPC.TL_messages_getPeerDialogs();
        if (dialogs != null) {
            for (int a = 0; a < dialogs.size(); a++) {
                TLRPC.InputPeer inputPeer = getInputPeer(dialogs.get(a).id);
                if (!(inputPeer instanceof TLRPC.TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                    TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                    inputDialogPeer.peer = inputPeer;
                    req.peers.add(inputDialogPeer);
                }
            }
        } else {
            TLRPC.InputPeer inputPeer2 = getInputPeer(did);
            if ((inputPeer2 instanceof TLRPC.TL_inputPeerChannel) && inputPeer2.access_hash == 0) {
                return;
            }
            TLRPC.TL_inputDialogPeer inputDialogPeer2 = new TLRPC.TL_inputDialogPeer();
            inputDialogPeer2.peer = inputPeer2;
            req.peers.add(inputDialogPeer2);
        }
        if (req.peers.isEmpty()) {
            return;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda167
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m826x941317e(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$reloadDialogsReadValue$43$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m826x941317e(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_messages_peerDialogs res = (TLRPC.TL_messages_peerDialogs) response;
            ArrayList<TLRPC.Update> arrayList = new ArrayList<>();
            for (int a = 0; a < res.dialogs.size(); a++) {
                TLRPC.Dialog dialog = res.dialogs.get(a);
                DialogObject.initDialog(dialog);
                Integer value = this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                if (value == null) {
                    value = 0;
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, value.intValue())));
                if (value.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TLRPC.TL_updateReadChannelInbox update = new TLRPC.TL_updateReadChannelInbox();
                        update.channel_id = dialog.peer.channel_id;
                        update.max_id = dialog.read_inbox_max_id;
                        arrayList.add(update);
                    } else {
                        TLRPC.TL_updateReadHistoryInbox update2 = new TLRPC.TL_updateReadHistoryInbox();
                        update2.peer = dialog.peer;
                        update2.max_id = dialog.read_inbox_max_id;
                        arrayList.add(update2);
                    }
                }
                Integer value2 = this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                if (value2 == null) {
                    value2 = 0;
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, value2.intValue())));
                if (dialog.read_outbox_max_id > value2.intValue()) {
                    if (dialog.peer.channel_id != 0) {
                        TLRPC.TL_updateReadChannelOutbox update3 = new TLRPC.TL_updateReadChannelOutbox();
                        update3.channel_id = dialog.peer.channel_id;
                        update3.max_id = dialog.read_outbox_max_id;
                        arrayList.add(update3);
                    } else {
                        TLRPC.TL_updateReadHistoryOutbox update4 = new TLRPC.TL_updateReadHistoryOutbox();
                        update4.peer = dialog.peer;
                        update4.max_id = dialog.read_outbox_max_id;
                        arrayList.add(update4);
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                processUpdateArray(arrayList, null, null, false, 0);
            }
        }
    }

    public TLRPC.ChannelParticipant getAdminInChannel(long uid, long chatId) {
        LongSparseArray<TLRPC.ChannelParticipant> array = this.channelAdmins.get(chatId);
        if (array == null) {
            return null;
        }
        return array.get(uid);
    }

    public String getAdminRank(long chatId, long uid) {
        TLRPC.ChannelParticipant participant;
        LongSparseArray<TLRPC.ChannelParticipant> array = this.channelAdmins.get(chatId);
        if (array == null || (participant = array.get(uid)) == null) {
            return null;
        }
        return participant.rank != null ? participant.rank : "";
    }

    public boolean isChannelAdminsLoaded(long chatId) {
        return this.channelAdmins.get(chatId) != null;
    }

    public void loadChannelAdmins(final long chatId, boolean cache) {
        int loadTime = this.loadingChannelAdmins.get(chatId);
        if (SystemClock.elapsedRealtime() - loadTime < 60) {
            return;
        }
        this.loadingChannelAdmins.put(chatId, (int) (SystemClock.elapsedRealtime() / 1000));
        if (cache) {
            getMessagesStorage().loadChannelAdmins(chatId);
            return;
        }
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = getInputChannel(chatId);
        req.limit = 100;
        req.filter = new TLRPC.TL_channelParticipantsAdmins();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda195
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m703xfeabe4c1(chatId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadChannelAdmins$44$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m703xfeabe4c1(long chatId, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_channels_channelParticipants) {
            processLoadedAdminsResponse(chatId, (TLRPC.TL_channels_channelParticipants) response);
        }
    }

    public void processLoadedAdminsResponse(long chatId, TLRPC.TL_channels_channelParticipants participants) {
        LongSparseArray<TLRPC.ChannelParticipant> array1 = new LongSparseArray<>(participants.participants.size());
        for (int a = 0; a < participants.participants.size(); a++) {
            TLRPC.ChannelParticipant participant = participants.participants.get(a);
            array1.put(MessageObject.getPeerId(participant.peer), participant);
        }
        processLoadedChannelAdmins(array1, chatId, false);
    }

    public void processLoadedChannelAdmins(final LongSparseArray<TLRPC.ChannelParticipant> array, final long chatId, final boolean cache) {
        if (!cache) {
            getMessagesStorage().putChannelAdmins(chatId, array);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda335
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m777xf200e016(chatId, array, cache);
            }
        });
    }

    /* renamed from: lambda$processLoadedChannelAdmins$45$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m777xf200e016(long chatId, LongSparseArray array, boolean cache) {
        this.channelAdmins.put(chatId, array);
        if (cache) {
            this.loadingChannelAdmins.delete(chatId);
            loadChannelAdmins(chatId, false);
            getNotificationCenter().postNotificationName(NotificationCenter.didLoadChatAdmins, Long.valueOf(chatId));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void loadFullChat(final long chatId, final int classGuid, boolean force) {
        TLObject request;
        boolean loaded = this.loadedFullChats.contains(Long.valueOf(chatId));
        if (!this.loadingFullChats.contains(Long.valueOf(chatId))) {
            if (!force && loaded) {
                return;
            }
            this.loadingFullChats.add(Long.valueOf(chatId));
            final long dialogId = -chatId;
            final TLRPC.Chat chat = getChat(Long.valueOf(chatId));
            if (ChatObject.isChannel(chat)) {
                TLRPC.TL_channels_getFullChannel req = new TLRPC.TL_channels_getFullChannel();
                req.channel = getInputChannel(chat);
                loadChannelAdmins(chatId, !loaded);
                request = req;
            } else {
                TLRPC.TL_messages_getFullChat req2 = new TLRPC.TL_messages_getFullChat();
                req2.chat_id = chatId;
                if (this.dialogs_read_inbox_max.get(Long.valueOf(dialogId)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(dialogId)) == null) {
                    reloadDialogsReadValue(null, dialogId);
                }
                request = req2;
            }
            int reqId = getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda243
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m713lambda$loadFullChat$48$orgtelegrammessengerMessagesController(chat, dialogId, chatId, classGuid, tLObject, tL_error);
                }
            });
            if (classGuid != 0) {
                getConnectionsManager().bindRequestToGuid(reqId, classGuid);
            }
        }
    }

    /* renamed from: lambda$loadFullChat$48$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m713lambda$loadFullChat$48$orgtelegrammessengerMessagesController(TLRPC.Chat chat, long dialogId, final long chatId, final int classGuid, TLObject response, final TLRPC.TL_error error) {
        Integer value;
        Integer value2;
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda70
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m712lambda$loadFullChat$47$orgtelegrammessengerMessagesController(error, chatId);
                }
            });
            return;
        }
        final TLRPC.TL_messages_chatFull res = (TLRPC.TL_messages_chatFull) response;
        getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
        getMessagesStorage().updateChatInfo(res.full_chat, false);
        if (ChatObject.isChannel(chat)) {
            Integer value3 = this.dialogs_read_inbox_max.get(Long.valueOf(dialogId));
            if (value3 != null) {
                value = value3;
            } else {
                value = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, dialogId));
            }
            this.dialogs_read_inbox_max.put(Long.valueOf(dialogId), Integer.valueOf(Math.max(res.full_chat.read_inbox_max_id, value.intValue())));
            if (res.full_chat.read_inbox_max_id > value.intValue()) {
                ArrayList<TLRPC.Update> arrayList = new ArrayList<>();
                TLRPC.TL_updateReadChannelInbox update = new TLRPC.TL_updateReadChannelInbox();
                update.channel_id = chatId;
                update.max_id = res.full_chat.read_inbox_max_id;
                arrayList.add(update);
                processUpdateArray(arrayList, null, null, false, 0);
            }
            Integer value4 = this.dialogs_read_outbox_max.get(Long.valueOf(dialogId));
            if (value4 != null) {
                value2 = value4;
            } else {
                value2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, dialogId));
            }
            this.dialogs_read_outbox_max.put(Long.valueOf(dialogId), Integer.valueOf(Math.max(res.full_chat.read_outbox_max_id, value2.intValue())));
            if (res.full_chat.read_outbox_max_id > value2.intValue()) {
                ArrayList<TLRPC.Update> arrayList2 = new ArrayList<>();
                TLRPC.TL_updateReadChannelOutbox update2 = new TLRPC.TL_updateReadChannelOutbox();
                update2.channel_id = chatId;
                update2.max_id = res.full_chat.read_outbox_max_id;
                arrayList2.add(update2);
                processUpdateArray(arrayList2, null, null, false, 0);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda351
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m711lambda$loadFullChat$46$orgtelegrammessengerMessagesController(chatId, res, classGuid);
            }
        });
    }

    /* renamed from: lambda$loadFullChat$46$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m711lambda$loadFullChat$46$orgtelegrammessengerMessagesController(long chatId, TLRPC.TL_messages_chatFull res, int classGuid) {
        TLRPC.Dialog dialog;
        TLRPC.ChatFull old = this.fullChats.get(chatId);
        if (old != null) {
            res.full_chat.inviterId = old.inviterId;
        }
        this.fullChats.put(chatId, res.full_chat);
        applyDialogNotificationsSettings(-chatId, res.full_chat.notify_settings);
        for (int a = 0; a < res.full_chat.bot_info.size(); a++) {
            TLRPC.BotInfo botInfo = res.full_chat.bot_info.get(a);
            getMediaDataController().putBotInfo(-chatId, botInfo);
        }
        int index = this.blockePeers.indexOfKey(-chatId);
        if (res.full_chat.blocked) {
            if (index < 0) {
                this.blockePeers.put(-chatId, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (index >= 0) {
            this.blockePeers.removeAt(index);
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.exportedChats.put(chatId, res.full_chat.exported_invite);
        this.loadingFullChats.remove(Long.valueOf(chatId));
        this.loadedFullChats.add(Long.valueOf(chatId));
        putUsers(res.users, false);
        putChats(res.chats, false);
        if (res.full_chat.stickerset != null) {
            getMediaDataController().getGroupStickerSetById(res.full_chat.stickerset);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, res.full_chat, Integer.valueOf(classGuid), false, true);
        if ((res.full_chat.flags & 2048) != 0 && (dialog = this.dialogs_dict.get(-chatId)) != null && dialog.folder_id != res.full_chat.folder_id) {
            dialog.folder_id = res.full_chat.folder_id;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: lambda$loadFullChat$47$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m712lambda$loadFullChat$47$orgtelegrammessengerMessagesController(TLRPC.TL_error error, long chatId) {
        checkChannelError(error.text, chatId);
        this.loadingFullChats.remove(Long.valueOf(chatId));
    }

    public void loadFullUser(final TLRPC.User user, final int classGuid, boolean force) {
        if (user == null || this.loadingFullUsers.contains(Long.valueOf(user.id))) {
            return;
        }
        if (!force && this.loadedFullUsers.contains(Long.valueOf(user.id))) {
            return;
        }
        this.loadingFullUsers.add(Long.valueOf(user.id));
        TLRPC.TL_users_getFullUser req = new TLRPC.TL_users_getFullUser();
        req.id = getInputUser(user);
        long dialogId = user.id;
        if (this.dialogs_read_inbox_max.get(Long.valueOf(dialogId)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(dialogId)) == null) {
            reloadDialogsReadValue(null, dialogId);
        }
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda253
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m716lambda$loadFullUser$51$orgtelegrammessengerMessagesController(user, classGuid, tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(reqId, classGuid);
    }

    /* renamed from: lambda$loadFullUser$51$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m716lambda$loadFullUser$51$orgtelegrammessengerMessagesController(final TLRPC.User user, final int classGuid, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_users_userFull res = (TLRPC.TL_users_userFull) response;
            final TLRPC.UserFull userFull = res.full_user;
            putUsers(res.users, false);
            putChats(res.chats, false);
            res.full_user.user = getUser(Long.valueOf(res.full_user.id));
            getMessagesStorage().updateUserInfo(userFull, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda97
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m714lambda$loadFullUser$49$orgtelegrammessengerMessagesController(userFull, user, classGuid);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda95
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m715lambda$loadFullUser$50$orgtelegrammessengerMessagesController(user);
            }
        });
    }

    /* renamed from: lambda$loadFullUser$49$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m714lambda$loadFullUser$49$orgtelegrammessengerMessagesController(TLRPC.UserFull userFull, TLRPC.User user, int classGuid) {
        TLRPC.Dialog dialog;
        savePeerSettings(userFull.user.id, userFull.settings, false);
        applyDialogNotificationsSettings(user.id, userFull.notify_settings);
        if (userFull.bot_info instanceof TLRPC.TL_botInfo) {
            userFull.bot_info.user_id = user.id;
            getMediaDataController().putBotInfo(user.id, userFull.bot_info);
        }
        int index = this.blockePeers.indexOfKey(user.id);
        if (userFull.blocked) {
            if (index < 0) {
                this.blockePeers.put(user.id, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (index >= 0) {
            this.blockePeers.removeAt(index);
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.fullUsers.put(user.id, userFull);
        this.loadingFullUsers.remove(Long.valueOf(user.id));
        this.loadedFullUsers.add(Long.valueOf(user.id));
        String names = user.first_name + user.last_name + user.username;
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(userFull.user);
        putUsers(users, false);
        getMessagesStorage().putUsersAndChats(users, null, false, true);
        if (!names.equals(userFull.user.first_name + userFull.user.last_name + userFull.user.username)) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_NAME));
        }
        if (userFull.user.photo != null && userFull.user.photo.has_video) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        }
        if (userFull.bot_info instanceof TLRPC.TL_botInfo) {
            userFull.bot_info.user_id = userFull.id;
            getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, userFull.bot_info, Integer.valueOf(classGuid));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(user.id), userFull);
        if ((userFull.flags & 2048) != 0 && (dialog = this.dialogs_dict.get(user.id)) != null && dialog.folder_id != userFull.folder_id) {
            dialog.folder_id = userFull.folder_id;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: lambda$loadFullUser$50$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m715lambda$loadFullUser$50$orgtelegrammessengerMessagesController(TLRPC.User user) {
        this.loadingFullUsers.remove(Long.valueOf(user.id));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void reloadMessages(ArrayList<Integer> mids, final long dialogId, final boolean scheduled) {
        TLRPC.Chat chat;
        TLObject request;
        ArrayList<Integer> arrayList;
        if (mids.isEmpty()) {
            return;
        }
        final ArrayList<Integer> result = new ArrayList<>();
        if (DialogObject.isChatDialog(dialogId)) {
            chat = getChat(Long.valueOf(-dialogId));
        } else {
            chat = null;
        }
        if (ChatObject.isChannel(chat)) {
            TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
            req.channel = getInputChannel(chat);
            req.id = result;
            request = req;
        } else {
            TLRPC.TL_messages_getMessages req2 = new TLRPC.TL_messages_getMessages();
            req2.id = result;
            request = req2;
        }
        ArrayList<Integer> arrayList2 = this.reloadingMessages.get(dialogId);
        for (int a = 0; a < mids.size(); a++) {
            Integer mid = mids.get(a);
            if (arrayList2 == null || !arrayList2.contains(mid)) {
                result.add(mid);
            }
        }
        if (result.isEmpty()) {
            return;
        }
        if (arrayList2 != null) {
            arrayList = arrayList2;
        } else {
            ArrayList<Integer> arrayList3 = new ArrayList<>();
            this.reloadingMessages.put(dialogId, arrayList3);
            arrayList = arrayList3;
        }
        arrayList.addAll(result);
        getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda231
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m830x9038eea1(dialogId, scheduled, result, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$reloadMessages$53$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m830x9038eea1(final long dialogId, boolean scheduled, final ArrayList result, TLObject response, TLRPC.TL_error error) {
        Integer inboxValue;
        Integer outboxValue;
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            LongSparseArray<TLRPC.User> usersLocal = new LongSparseArray<>();
            for (int a = 0; a < messagesRes.users.size(); a++) {
                TLRPC.User u = messagesRes.users.get(a);
                usersLocal.put(u.id, u);
            }
            LongSparseArray<TLRPC.Chat> chatsLocal = new LongSparseArray<>();
            for (int a2 = 0; a2 < messagesRes.chats.size(); a2++) {
                TLRPC.Chat c = messagesRes.chats.get(a2);
                chatsLocal.put(c.id, c);
            }
            Integer inboxValue2 = this.dialogs_read_inbox_max.get(Long.valueOf(dialogId));
            if (inboxValue2 == null) {
                Integer inboxValue3 = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, dialogId));
                this.dialogs_read_inbox_max.put(Long.valueOf(dialogId), inboxValue3);
                inboxValue = inboxValue3;
            } else {
                inboxValue = inboxValue2;
            }
            Integer outboxValue2 = this.dialogs_read_outbox_max.get(Long.valueOf(dialogId));
            if (outboxValue2 == null) {
                Integer outboxValue3 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, dialogId));
                this.dialogs_read_outbox_max.put(Long.valueOf(dialogId), outboxValue3);
                outboxValue = outboxValue3;
            } else {
                outboxValue = outboxValue2;
            }
            final ArrayList<MessageObject> objects = new ArrayList<>();
            for (int a3 = 0; a3 < messagesRes.messages.size(); a3++) {
                TLRPC.Message message = messagesRes.messages.get(a3);
                message.dialog_id = dialogId;
                if (!scheduled) {
                    message.unread = (message.out ? outboxValue : inboxValue).intValue() < message.id;
                }
                objects.add(new MessageObject(this.currentAccount, message, usersLocal, chatsLocal, true, true));
            }
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            getMessagesStorage().putMessages(messagesRes, dialogId, -1, 0, false, scheduled);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda345
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m829xaaf77fe0(dialogId, result, objects);
                }
            });
        }
    }

    /* renamed from: lambda$reloadMessages$52$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m829xaaf77fe0(long dialogId, ArrayList result, ArrayList objects) {
        ArrayList<Integer> arrayList1 = this.reloadingMessages.get(dialogId);
        if (arrayList1 != null) {
            arrayList1.removeAll(result);
            if (arrayList1.isEmpty()) {
                this.reloadingMessages.remove(dialogId);
            }
        }
        MessageObject dialogObj = this.dialogMessage.get(dialogId);
        if (dialogObj != null) {
            int a = 0;
            while (true) {
                if (a >= objects.size()) {
                    break;
                }
                MessageObject obj = (MessageObject) objects.get(a);
                if (dialogObj.getId() != obj.getId()) {
                    a++;
                } else {
                    this.dialogMessage.put(dialogId, obj);
                    if (obj.messageOwner.peer_id.channel_id == 0) {
                        MessageObject obj2 = this.dialogMessagesByIds.get(obj.getId());
                        this.dialogMessagesByIds.remove(obj.getId());
                        if (obj2 != null) {
                            this.dialogMessagesByIds.put(obj2.getId(), obj2);
                        }
                    }
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialogId), objects);
    }

    public void hidePeerSettingsBar(long dialogId, TLRPC.User currentUser, TLRPC.Chat currentChat) {
        if (currentUser == null && currentChat == null) {
            return;
        }
        SharedPreferences.Editor editor = this.notificationsPreferences.edit();
        editor.putInt("dialog_bar_vis3" + dialogId, 3);
        editor.remove("dialog_bar_invite" + dialogId);
        editor.commit();
        if (!DialogObject.isEncryptedDialog(dialogId)) {
            TLRPC.TL_messages_hidePeerSettingsBar req = new TLRPC.TL_messages_hidePeerSettingsBar();
            if (currentUser != null) {
                req.peer = getInputPeer(currentUser.id);
            } else {
                req.peer = getInputPeer(-currentChat.id);
            }
            getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda273.INSTANCE);
        }
    }

    public static /* synthetic */ void lambda$hidePeerSettingsBar$54(TLObject response, TLRPC.TL_error error) {
    }

    public void reportSpam(long dialogId, TLRPC.User currentUser, TLRPC.Chat currentChat, TLRPC.EncryptedChat currentEncryptedChat, boolean geo) {
        if (currentUser == null && currentChat == null && currentEncryptedChat == null) {
            return;
        }
        SharedPreferences.Editor editor = this.notificationsPreferences.edit();
        editor.putInt("dialog_bar_vis3" + dialogId, 3);
        editor.commit();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            if (currentEncryptedChat == null || currentEncryptedChat.access_hash == 0) {
                return;
            }
            TLRPC.TL_messages_reportEncryptedSpam req = new TLRPC.TL_messages_reportEncryptedSpam();
            req.peer = new TLRPC.TL_inputEncryptedChat();
            req.peer.chat_id = currentEncryptedChat.id;
            req.peer.access_hash = currentEncryptedChat.access_hash;
            getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda284.INSTANCE, 2);
        } else if (geo) {
            TLRPC.TL_account_reportPeer req2 = new TLRPC.TL_account_reportPeer();
            if (currentChat != null) {
                req2.peer = getInputPeer(-currentChat.id);
            } else if (currentUser != null) {
                req2.peer = getInputPeer(currentUser.id);
            }
            req2.message = "";
            req2.reason = new TLRPC.TL_inputReportReasonGeoIrrelevant();
            getConnectionsManager().sendRequest(req2, MessagesController$$ExternalSyntheticLambda285.INSTANCE, 2);
        } else {
            TLRPC.TL_messages_reportSpam req3 = new TLRPC.TL_messages_reportSpam();
            if (currentChat != null) {
                req3.peer = getInputPeer(-currentChat.id);
            } else if (currentUser != null) {
                req3.peer = getInputPeer(currentUser.id);
            }
            getConnectionsManager().sendRequest(req3, MessagesController$$ExternalSyntheticLambda286.INSTANCE, 2);
        }
    }

    public static /* synthetic */ void lambda$reportSpam$55(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ void lambda$reportSpam$56(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ void lambda$reportSpam$57(TLObject response, TLRPC.TL_error error) {
    }

    private void savePeerSettings(long dialogId, TLRPC.TL_peerSettings settings, boolean update) {
        if (settings != null) {
            SharedPreferences sharedPreferences = this.notificationsPreferences;
            if (sharedPreferences.getInt("dialog_bar_vis3" + dialogId, 0) == 3) {
                return;
            }
            SharedPreferences.Editor editor = this.notificationsPreferences.edit();
            boolean bar_hidden = !settings.report_spam && !settings.add_contact && !settings.block_contact && !settings.share_contact && !settings.report_geo && !settings.invite_members;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("peer settings loaded for " + dialogId + " add = " + settings.add_contact + " block = " + settings.block_contact + " spam = " + settings.report_spam + " share = " + settings.share_contact + " geo = " + settings.report_geo + " hide = " + bar_hidden + " distance = " + settings.geo_distance + " invite = " + settings.invite_members);
            }
            editor.putInt("dialog_bar_vis3" + dialogId, bar_hidden ? 1 : 2);
            editor.putBoolean("dialog_bar_share" + dialogId, settings.share_contact);
            editor.putBoolean("dialog_bar_report" + dialogId, settings.report_spam);
            editor.putBoolean("dialog_bar_add" + dialogId, settings.add_contact);
            editor.putBoolean("dialog_bar_block" + dialogId, settings.block_contact);
            editor.putBoolean("dialog_bar_exception" + dialogId, settings.need_contacts_exception);
            editor.putBoolean("dialog_bar_location" + dialogId, settings.report_geo);
            editor.putBoolean("dialog_bar_archived" + dialogId, settings.autoarchived);
            editor.putBoolean("dialog_bar_invite" + dialogId, settings.invite_members);
            editor.putString("dialog_bar_chat_with_admin_title" + dialogId, settings.request_chat_title);
            editor.putBoolean("dialog_bar_chat_with_channel" + dialogId, settings.request_chat_broadcast);
            editor.putInt("dialog_bar_chat_with_date" + dialogId, settings.request_chat_date);
            SharedPreferences sharedPreferences2 = this.notificationsPreferences;
            if (sharedPreferences2.getInt("dialog_bar_distance" + dialogId, -1) != -2) {
                if ((settings.flags & 64) != 0) {
                    editor.putInt("dialog_bar_distance" + dialogId, settings.geo_distance);
                } else {
                    editor.remove("dialog_bar_distance" + dialogId);
                }
            }
            editor.apply();
            getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(dialogId));
        }
    }

    public void loadPeerSettings(TLRPC.User currentUser, TLRPC.Chat currentChat) {
        final long dialogId;
        if (currentUser == null && currentChat == null) {
            return;
        }
        if (currentUser != null) {
            dialogId = currentUser.id;
        } else {
            dialogId = -currentChat.id;
        }
        if (this.loadingPeerSettings.indexOfKey(dialogId) >= 0) {
            return;
        }
        this.loadingPeerSettings.put(dialogId, true);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("request spam button for " + dialogId);
        }
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        int vis = sharedPreferences.getInt("dialog_bar_vis3" + dialogId, 0);
        if (vis == 1 || vis == 3) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("dialog bar already hidden for " + dialogId);
                return;
            }
            return;
        }
        TLRPC.TL_messages_getPeerSettings req = new TLRPC.TL_messages_getPeerSettings();
        if (currentUser != null) {
            req.peer = getInputPeer(currentUser.id);
        } else {
            req.peer = getInputPeer(-currentChat.id);
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda196
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m729x7ddf9e81(dialogId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadPeerSettings$59$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m729x7ddf9e81(final long dialogId, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda347
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m728x989e2fc0(dialogId, response);
            }
        });
    }

    /* renamed from: lambda$loadPeerSettings$58$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m728x989e2fc0(long dialogId, TLObject response) {
        this.loadingPeerSettings.remove(dialogId);
        if (response != null) {
            TLRPC.TL_messages_peerSettings res = (TLRPC.TL_messages_peerSettings) response;
            TLRPC.TL_peerSettings settings = res.settings;
            putUsers(res.users, false);
            putChats(res.chats, false);
            savePeerSettings(dialogId, settings, false);
        }
    }

    public void processNewChannelDifferenceParams(int pts, int pts_count, long channelId) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewChannelDifferenceParams pts = " + pts + " pts_count = " + pts_count + " channeldId = " + channelId);
        }
        int channelPts = this.channelsPts.get(channelId);
        if (channelPts == 0) {
            channelPts = getMessagesStorage().getChannelPtsSync(channelId);
            if (channelPts == 0) {
                channelPts = 1;
            }
            this.channelsPts.put(channelId, channelPts);
        }
        if (channelPts + pts_count == pts) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("APPLY CHANNEL PTS");
            }
            this.channelsPts.put(channelId, pts);
            getMessagesStorage().saveChannelPts(channelId, pts);
        } else if (channelPts != pts) {
            long updatesStartWaitTime = this.updatesStartWaitTimeChannels.get(channelId);
            boolean gettingDifferenceChannel = this.gettingDifferenceChannels.get(channelId, false).booleanValue();
            if (gettingDifferenceChannel || updatesStartWaitTime == 0 || Math.abs(System.currentTimeMillis() - updatesStartWaitTime) <= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("ADD CHANNEL UPDATE TO QUEUE pts = " + pts + " pts_count = " + pts_count);
                }
                if (updatesStartWaitTime == 0) {
                    this.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
                }
                UserActionUpdatesPts updates = new UserActionUpdatesPts();
                updates.pts = pts;
                updates.pts_count = pts_count;
                updates.chat_id = channelId;
                ArrayList<TLRPC.Updates> arrayList = this.updatesQueueChannels.get(channelId);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.updatesQueueChannels.put(channelId, arrayList);
                }
                arrayList.add(updates);
                return;
            }
            getChannelDifference(channelId);
        }
    }

    public void processNewDifferenceParams(int seq, int pts, int date, int pts_count) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewDifferenceParams seq = " + seq + " pts = " + pts + " date = " + date + " pts_count = " + pts_count);
        }
        if (pts != -1) {
            if (getMessagesStorage().getLastPtsValue() + pts_count == pts) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY PTS");
                }
                getMessagesStorage().setLastPtsValue(pts);
                getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
            } else if (getMessagesStorage().getLastPtsValue() != pts) {
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("ADD UPDATE TO QUEUE pts = " + pts + " pts_count = " + pts_count);
                    }
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts updates = new UserActionUpdatesPts();
                    updates.pts = pts;
                    updates.pts_count = pts_count;
                    this.updatesQueuePts.add(updates);
                } else {
                    getDifference();
                }
            }
        }
        if (seq != -1) {
            if (getMessagesStorage().getLastSeqValue() + 1 == seq) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY SEQ");
                }
                getMessagesStorage().setLastSeqValue(seq);
                if (date != -1) {
                    getMessagesStorage().setLastDateValue(date);
                }
                getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
            } else if (getMessagesStorage().getLastSeqValue() != seq) {
                if (this.gettingDifference || this.updatesStartWaitTimeSeq == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("ADD UPDATE TO QUEUE seq = " + seq);
                    }
                    if (this.updatesStartWaitTimeSeq == 0) {
                        this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                    }
                    UserActionUpdatesSeq updates2 = new UserActionUpdatesSeq();
                    updates2.seq = seq;
                    this.updatesQueueSeq.add(updates2);
                    return;
                }
                getDifference();
            }
        }
    }

    public void didAddedNewTask(final int minDate, final long dialogId, final SparseArray<ArrayList<Integer>> mids) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda294
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m651x45b86af8(minDate);
            }
        });
        if (mids != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda334
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m652x2af9d9b9(dialogId, mids);
                }
            });
        }
    }

    /* renamed from: lambda$didAddedNewTask$60$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m651x45b86af8(int minDate) {
        int i;
        if ((this.currentDeletingTaskMids == null && this.currentDeletingTaskMediaMids == null && !this.gettingNewDeleteTask) || ((i = this.currentDeletingTaskTime) != 0 && minDate < i)) {
            getNewDeleteTask(null, null);
        }
    }

    /* renamed from: lambda$didAddedNewTask$61$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m652x2af9d9b9(long dialogId, SparseArray mids) {
        getNotificationCenter().postNotificationName(NotificationCenter.didCreatedNewDeleteTask, Long.valueOf(dialogId), mids);
    }

    public void getNewDeleteTask(final LongSparseArray<ArrayList<Integer>> oldTask, final LongSparseArray<ArrayList<Integer>> oldTaskMedia) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda360
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m692x329464ca(oldTask, oldTaskMedia);
            }
        });
    }

    /* renamed from: lambda$getNewDeleteTask$62$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m692x329464ca(LongSparseArray oldTask, LongSparseArray oldTaskMedia) {
        this.gettingNewDeleteTask = true;
        getMessagesStorage().getNewTask(oldTask, oldTaskMedia);
    }

    private boolean checkDeletingTask(boolean runnable) {
        int i;
        int currentServerTime = getConnectionsManager().getCurrentTime();
        if (!(this.currentDeletingTaskMids == null && this.currentDeletingTaskMediaMids == null) && (runnable || ((i = this.currentDeletingTaskTime) != 0 && i <= currentServerTime))) {
            this.currentDeletingTaskTime = 0;
            if (this.currentDeleteTaskRunnable != null && !runnable) {
                Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            }
            final LongSparseArray<ArrayList<Integer>> taskMedia = null;
            this.currentDeleteTaskRunnable = null;
            LongSparseArray<ArrayList<Integer>> longSparseArray = this.currentDeletingTaskMids;
            final LongSparseArray<ArrayList<Integer>> task = longSparseArray != null ? longSparseArray.clone() : null;
            LongSparseArray<ArrayList<Integer>> longSparseArray2 = this.currentDeletingTaskMediaMids;
            if (longSparseArray2 != null) {
                taskMedia = longSparseArray2.clone();
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda359
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m598xee214c2b(task, taskMedia);
                }
            });
            return true;
        }
        return false;
    }

    /* renamed from: lambda$checkDeletingTask$64$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m598xee214c2b(final LongSparseArray task, final LongSparseArray taskMedia) {
        if (task != null) {
            int N = task.size();
            for (int a = 0; a < N; a++) {
                ArrayList<Integer> mids = (ArrayList) task.valueAt(a);
                deleteMessages(mids, null, null, task.keyAt(a), true, false, !mids.isEmpty() && mids.get(0).intValue() > 0);
            }
        }
        if (taskMedia != null) {
            int N2 = taskMedia.size();
            for (int a2 = 0; a2 < N2; a2++) {
                getMessagesStorage().emptyMessagesMedia(taskMedia.keyAt(a2), (ArrayList) taskMedia.valueAt(a2));
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda358
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m597x8dfdd6a(task, taskMedia);
            }
        });
    }

    /* renamed from: lambda$checkDeletingTask$63$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m597x8dfdd6a(LongSparseArray task, LongSparseArray taskMedia) {
        getNewDeleteTask(task, taskMedia);
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
    }

    public void processLoadedDeleteTask(final int taskTime, final LongSparseArray<ArrayList<Integer>> task, final LongSparseArray<ArrayList<Integer>> taskMedia) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m779x5720127e(task, taskMedia, taskTime);
            }
        });
    }

    /* renamed from: lambda$processLoadedDeleteTask$66$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m779x5720127e(LongSparseArray task, LongSparseArray taskMedia, int taskTime) {
        this.gettingNewDeleteTask = false;
        if (task != null || taskMedia != null) {
            this.currentDeletingTaskTime = taskTime;
            this.currentDeletingTaskMids = task;
            this.currentDeletingTaskMediaMids = taskMedia;
            if (this.currentDeleteTaskRunnable != null) {
                Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
                this.currentDeleteTaskRunnable = null;
            }
            if (!checkDeletingTask(false)) {
                this.currentDeleteTaskRunnable = new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda166
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m778x71dea3bd();
                    }
                };
                int currentServerTime = getConnectionsManager().getCurrentTime();
                Utilities.stageQueue.postRunnable(this.currentDeleteTaskRunnable, Math.abs(currentServerTime - this.currentDeletingTaskTime) * 1000);
                return;
            }
            return;
        }
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
    }

    /* renamed from: lambda$processLoadedDeleteTask$65$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m778x71dea3bd() {
        checkDeletingTask(true);
    }

    public void loadDialogPhotos(final long did, final int count, final int maxId, boolean fromCache, final int classGuid) {
        if (fromCache) {
            getMessagesStorage().getDialogPhotos(did, count, maxId, classGuid);
        } else if (did > 0) {
            TLRPC.User user = getUser(Long.valueOf(did));
            if (user == null) {
                return;
            }
            TLRPC.TL_photos_getUserPhotos req = new TLRPC.TL_photos_getUserPhotos();
            req.limit = count;
            req.offset = 0;
            req.max_id = maxId;
            req.user_id = getInputUser(user);
            int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda207
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m707x749efc9a(did, count, maxId, classGuid, tLObject, tL_error);
                }
            });
            getConnectionsManager().bindRequestToGuid(reqId, classGuid);
        } else if (did < 0) {
            TLRPC.TL_messages_search req2 = new TLRPC.TL_messages_search();
            req2.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
            req2.limit = count;
            req2.offset_id = maxId;
            req2.q = "";
            req2.peer = getInputPeer(did);
            int reqId2 = getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda208
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m708x59e06b5b(did, count, maxId, classGuid, tLObject, tL_error);
                }
            });
            getConnectionsManager().bindRequestToGuid(reqId2, classGuid);
        }
    }

    /* renamed from: lambda$loadDialogPhotos$67$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m707x749efc9a(long did, int count, int maxId, int classGuid, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.photos_Photos res = (TLRPC.photos_Photos) response;
            processLoadedUserPhotos(res, null, did, count, maxId, false, classGuid);
        }
    }

    /* renamed from: lambda$loadDialogPhotos$68$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m708x59e06b5b(long did, int count, int maxId, int classGuid, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messages = (TLRPC.messages_Messages) response;
            TLRPC.TL_photos_photos res = new TLRPC.TL_photos_photos();
            ArrayList<TLRPC.Message> arrayList = new ArrayList<>();
            res.count = messages.count;
            res.users.addAll(messages.users);
            for (int a = 0; a < messages.messages.size(); a++) {
                TLRPC.Message message = messages.messages.get(a);
                if (message.action != null && message.action.photo != null) {
                    res.photos.add(message.action.photo);
                    arrayList.add(message);
                }
            }
            processLoadedUserPhotos(res, arrayList, did, count, maxId, false, classGuid);
        }
    }

    public void blockPeer(long id) {
        TLRPC.User user = null;
        TLRPC.Chat chat = null;
        if (id > 0) {
            user = getUser(Long.valueOf(id));
            if (user == null) {
                return;
            }
        } else {
            chat = getChat(Long.valueOf(-id));
            if (chat == null) {
                return;
            }
        }
        if (this.blockePeers.indexOfKey(id) >= 0) {
            return;
        }
        this.blockePeers.put(id, 1);
        if (user != null) {
            if (user.bot) {
                getMediaDataController().removeInline(id);
            } else {
                getMediaDataController().removePeer(id);
            }
        }
        int i = this.totalBlockedCount;
        if (i >= 0) {
            this.totalBlockedCount = i + 1;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        TLRPC.TL_contacts_block req = new TLRPC.TL_contacts_block();
        if (user != null) {
            req.id = getInputPeer(user);
        } else {
            req.id = getInputPeer(chat);
        }
        getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda268.INSTANCE);
    }

    public static /* synthetic */ void lambda$blockPeer$69(TLObject response, TLRPC.TL_error error) {
    }

    public void setParticipantBannedRole(final long chatId, TLRPC.User user, TLRPC.Chat chat, TLRPC.TL_chatBannedRights rights, final boolean isChannel, final BaseFragment parentFragment) {
        if ((user == null && chat == null) || rights == null) {
            return;
        }
        final TLRPC.TL_channels_editBanned req = new TLRPC.TL_channels_editBanned();
        req.channel = getInputChannel(chatId);
        if (user != null) {
            req.participant = getInputPeer(user);
        } else {
            req.participant = getInputPeer(chat);
        }
        req.banned_rights = rights;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda229
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m860x45735fde(chatId, parentFragment, req, isChannel, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$setParticipantBannedRole$72$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m860x45735fde(final long chatId, final BaseFragment parentFragment, final TLRPC.TL_channels_editBanned req, final boolean isChannel, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda323
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m858x7af0825c(chatId);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda74
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m859x6031f11d(error, parentFragment, req, isChannel);
            }
        });
    }

    /* renamed from: lambda$setParticipantBannedRole$70$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m858x7af0825c(long chatId) {
        loadFullChat(chatId, 0, true);
    }

    /* renamed from: lambda$setParticipantBannedRole$71$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m859x6031f11d(TLRPC.TL_error error, BaseFragment parentFragment, TLRPC.TL_channels_editBanned req, boolean isChannel) {
        AlertsCreator.processError(this.currentAccount, error, parentFragment, req, Boolean.valueOf(isChannel));
    }

    public void setChannelSlowMode(final long chatId, int seconds) {
        TLRPC.TL_channels_toggleSlowMode req = new TLRPC.TL_channels_toggleSlowMode();
        req.seconds = seconds;
        req.channel = getInputChannel(chatId);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda204
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m849x8741e000(chatId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$setChannelSlowMode$74$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m849x8741e000(final long chatId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda320
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m848xa200713f(chatId);
                }
            }, 1000L);
        }
    }

    /* renamed from: lambda$setChannelSlowMode$73$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m848xa200713f(long chatId) {
        loadFullChat(chatId, 0, true);
    }

    public void setDefaultBannedRole(final long chatId, TLRPC.TL_chatBannedRights rights, final boolean isChannel, final BaseFragment parentFragment) {
        if (rights == null) {
            return;
        }
        final TLRPC.TL_messages_editChatDefaultBannedRights req = new TLRPC.TL_messages_editChatDefaultBannedRights();
        req.peer = getInputPeer(-chatId);
        req.banned_rights = rights;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda230
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m854xab578555(chatId, parentFragment, req, isChannel, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$setDefaultBannedRole$77$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m854xab578555(final long chatId, final BaseFragment parentFragment, final TLRPC.TL_messages_editChatDefaultBannedRights req, final boolean isChannel, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda322
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m852xe0d4a7d3(chatId);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda79
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m853xc6161694(error, parentFragment, req, isChannel);
            }
        });
    }

    /* renamed from: lambda$setDefaultBannedRole$75$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m852xe0d4a7d3(long chatId) {
        loadFullChat(chatId, 0, true);
    }

    /* renamed from: lambda$setDefaultBannedRole$76$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m853xc6161694(TLRPC.TL_error error, BaseFragment parentFragment, TLRPC.TL_messages_editChatDefaultBannedRights req, boolean isChannel) {
        AlertsCreator.processError(this.currentAccount, error, parentFragment, req, Boolean.valueOf(isChannel));
    }

    public void setUserAdminRole(long chatId, TLRPC.User user, TLRPC.TL_chatAdminRights rights, String rank, boolean isChannel, BaseFragment parentFragment, boolean addingNew, boolean forceAdmin, String botHash, Runnable onSuccess) {
        setUserAdminRole(chatId, user, rights, rank, isChannel, parentFragment, addingNew, forceAdmin, botHash, onSuccess, null);
    }

    public void setUserAdminRole(final long chatId, TLRPC.User user, TLRPC.TL_chatAdminRights rights, String rank, final boolean isChannel, final BaseFragment parentFragment, boolean addingNew, boolean forceAdmin, String botHash, final Runnable onSuccess, final ErrorDelegate onError) {
        if (user != null && rights != null) {
            TLRPC.Chat chat = getChat(Long.valueOf(chatId));
            if (ChatObject.isChannel(chat)) {
                final TLRPC.TL_channels_editAdmin req = new TLRPC.TL_channels_editAdmin();
                req.channel = getInputChannel(chat);
                req.user_id = getInputUser(user);
                req.admin_rights = rights;
                req.rank = rank;
                final RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda218
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m863x5c843dc9(chatId, onSuccess, parentFragment, req, isChannel, onError, tLObject, tL_error);
                    }
                };
                if ((chat.megagroup && addingNew) || !TextUtils.isEmpty(botHash)) {
                    addUserToChat(chatId, user, 0, botHash, parentFragment, true, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda64
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesController.this.m864x41c5ac8a(req, requestDelegate);
                        }
                    }, onError);
                    return;
                } else {
                    getConnectionsManager().sendRequest(req, requestDelegate);
                    return;
                }
            }
            final TLRPC.TL_messages_editChatAdmin req2 = new TLRPC.TL_messages_editChatAdmin();
            req2.chat_id = chatId;
            req2.user_id = getInputUser(user);
            req2.is_admin = forceAdmin || rights.change_info || rights.delete_messages || rights.ban_users || rights.invite_users || rights.pin_messages || rights.add_admins || rights.manage_call;
            final RequestDelegate requestDelegate2 = new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda219
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m867xd6cb678e(chatId, onSuccess, parentFragment, req2, onError, tLObject, tL_error);
                }
            };
            if (req2.is_admin || addingNew || !TextUtils.isEmpty(botHash)) {
                addUserToChat(chatId, user, 0, botHash, parentFragment, true, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda82
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m868xbc0cd64f(req2, requestDelegate2);
                    }
                }, onError);
            } else {
                getConnectionsManager().sendRequest(req2, requestDelegate2);
            }
        }
    }

    /* renamed from: lambda$setUserAdminRole$81$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m863x5c843dc9(final long chatId, final Runnable onSuccess, final BaseFragment parentFragment, final TLRPC.TL_channels_editAdmin req, final boolean isChannel, final ErrorDelegate onError, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda337
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m861xde61dbb1(chatId, onSuccess);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda73
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m862xc3a34a72(error, parentFragment, req, isChannel);
            }
        });
        if (onError != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda361
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.ErrorDelegate.this.run(error);
                }
            });
        }
    }

    /* renamed from: lambda$setUserAdminRole$78$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m861xde61dbb1(long chatId, Runnable onSuccess) {
        loadFullChat(chatId, 0, true);
        if (onSuccess != null) {
            onSuccess.run();
        }
    }

    /* renamed from: lambda$setUserAdminRole$79$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m862xc3a34a72(TLRPC.TL_error error, BaseFragment parentFragment, TLRPC.TL_channels_editAdmin req, boolean isChannel) {
        AlertsCreator.processError(this.currentAccount, error, parentFragment, req, Boolean.valueOf(isChannel));
    }

    /* renamed from: lambda$setUserAdminRole$82$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m864x41c5ac8a(TLRPC.TL_channels_editAdmin req, RequestDelegate requestDelegate) {
        getConnectionsManager().sendRequest(req, requestDelegate);
    }

    /* renamed from: lambda$setUserAdminRole$86$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m867xd6cb678e(final long chatId, final Runnable onSuccess, final BaseFragment parentFragment, final TLRPC.TL_messages_editChatAdmin req, final ErrorDelegate onError, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda338
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m865x27071b4b(chatId, onSuccess);
                }
            }, 1000L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda78
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m866xc488a0c(error, parentFragment, req);
            }
        });
        if (onError != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.ErrorDelegate.this.run(error);
                }
            });
        }
    }

    /* renamed from: lambda$setUserAdminRole$83$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m865x27071b4b(long chatId, Runnable onSuccess) {
        loadFullChat(chatId, 0, true);
        if (onSuccess != null) {
            onSuccess.run();
        }
    }

    /* renamed from: lambda$setUserAdminRole$84$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m866xc488a0c(TLRPC.TL_error error, BaseFragment parentFragment, TLRPC.TL_messages_editChatAdmin req) {
        AlertsCreator.processError(this.currentAccount, error, parentFragment, req, false);
    }

    /* renamed from: lambda$setUserAdminRole$87$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m868xbc0cd64f(TLRPC.TL_messages_editChatAdmin req, RequestDelegate requestDelegate) {
        getConnectionsManager().sendRequest(req, requestDelegate);
    }

    public void unblockPeer(long id) {
        TLRPC.TL_contacts_unblock req = new TLRPC.TL_contacts_unblock();
        TLRPC.User user = null;
        TLRPC.Chat chat = null;
        if (id > 0) {
            user = getUser(Long.valueOf(id));
            if (user == null) {
                return;
            }
        } else {
            chat = getChat(Long.valueOf(-id));
            if (chat == null) {
                return;
            }
        }
        this.totalBlockedCount--;
        this.blockePeers.delete(id);
        if (user != null) {
            req.id = getInputPeer(user);
        } else {
            req.id = getInputPeer(chat);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda289.INSTANCE);
    }

    public static /* synthetic */ void lambda$unblockPeer$88(TLObject response, TLRPC.TL_error error) {
    }

    public void getBlockedPeers(final boolean reset) {
        if (!getUserConfig().isClientActivated() || this.loadingBlockedPeers) {
            return;
        }
        this.loadingBlockedPeers = true;
        final TLRPC.TL_contacts_getBlocked req = new TLRPC.TL_contacts_getBlocked();
        req.offset = reset ? 0 : this.blockePeers.size();
        req.limit = reset ? 20 : 100;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda264
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m670xa6fe97e(reset, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$getBlockedPeers$90$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m670xa6fe97e(final boolean reset, final TLRPC.TL_contacts_getBlocked req, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m669x56d064e8(response, reset, req);
            }
        });
    }

    /* renamed from: lambda$getBlockedPeers$89$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m669x56d064e8(TLObject response, boolean reset, TLRPC.TL_contacts_getBlocked req) {
        if (response != null) {
            TLRPC.contacts_Blocked res = (TLRPC.contacts_Blocked) response;
            putUsers(res.users, false);
            putChats(res.chats, false);
            getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            if (reset) {
                this.blockePeers.clear();
            }
            this.totalBlockedCount = Math.max(res.count, res.blocked.size());
            this.blockedEndReached = res.blocked.size() < req.limit;
            int N = res.blocked.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_peerBlocked blocked = res.blocked.get(a);
                this.blockePeers.put(MessageObject.getPeerId(blocked.peer_id), 1);
            }
            this.loadingBlockedPeers = false;
            getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
    }

    public void deleteUserPhoto(TLRPC.InputPhoto photo) {
        if (photo == null) {
            TLRPC.TL_photos_updateProfilePhoto req = new TLRPC.TL_photos_updateProfilePhoto();
            req.id = new TLRPC.TL_inputPhotoEmpty();
            getUserConfig().getCurrentUser().photo = new TLRPC.TL_userProfilePhotoEmpty();
            TLRPC.User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
            }
            if (user == null) {
                return;
            }
            user.photo = getUserConfig().getCurrentUser().photo;
            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda150
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m650xdeb0a01f(tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_photos_deletePhotos req2 = new TLRPC.TL_photos_deletePhotos();
        req2.id.add(photo);
        getConnectionsManager().sendRequest(req2, MessagesController$$ExternalSyntheticLambda272.INSTANCE);
    }

    /* renamed from: lambda$deleteUserPhoto$92$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m650xdeb0a01f(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
            TLRPC.User user1 = getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user1 == null) {
                user1 = getUserConfig().getCurrentUser();
                putUser(user1, false);
            } else {
                getUserConfig().setCurrentUser(user1);
            }
            if (user1 == null) {
                return;
            }
            getMessagesStorage().clearUserPhotos(user1.id);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            users.add(user1);
            getMessagesStorage().putUsersAndChats(users, null, false, true);
            if (photos_photo.photo instanceof TLRPC.TL_photo) {
                user1.photo = new TLRPC.TL_userProfilePhoto();
                user1.photo.has_video = !photos_photo.photo.video_sizes.isEmpty();
                user1.photo.photo_id = photos_photo.photo.id;
                user1.photo.photo_small = FileLoader.getClosestPhotoSizeWithSize(photos_photo.photo.sizes, 150).location;
                user1.photo.photo_big = FileLoader.getClosestPhotoSizeWithSize(photos_photo.photo.sizes, 800).location;
                user1.photo.dc_id = photos_photo.photo.dc_id;
            } else {
                user1.photo = new TLRPC.TL_userProfilePhotoEmpty();
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda66
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m649xf96f315e();
                }
            });
        }
    }

    /* renamed from: lambda$deleteUserPhoto$91$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m649xf96f315e() {
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
        getUserConfig().saveConfig(true);
    }

    public static /* synthetic */ void lambda$deleteUserPhoto$93(TLObject response, TLRPC.TL_error error) {
    }

    public void processLoadedUserPhotos(final TLRPC.photos_Photos res, final ArrayList<TLRPC.Message> messages, final long did, final int count, int maxId, final boolean fromCache, final int classGuid) {
        if (!fromCache) {
            getMessagesStorage().putUsersAndChats(res.users, null, true, true);
            getMessagesStorage().putDialogPhotos(did, res, messages);
        } else if (res == null || res.photos.isEmpty()) {
            loadDialogPhotos(did, count, maxId, false, classGuid);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda107
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m790x1afdc3d(res, fromCache, did, count, classGuid, messages);
            }
        });
    }

    /* renamed from: lambda$processLoadedUserPhotos$94$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m790x1afdc3d(TLRPC.photos_Photos res, boolean fromCache, long did, int count, int classGuid, ArrayList messages) {
        putUsers(res.users, fromCache);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogPhotosLoaded, Long.valueOf(did), Integer.valueOf(count), Boolean.valueOf(fromCache), Integer.valueOf(classGuid), res.photos, messages);
    }

    public void uploadAndApplyUserAvatar(TLRPC.FileLocation location) {
        if (location == null) {
            return;
        }
        this.uploadingAvatar = FileLoader.getDirectory(4) + "/" + location.volume_id + "_" + location.local_id + ".jpg";
        getFileLoader().uploadFile(this.uploadingAvatar, false, true, 16777216);
    }

    public void saveTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent accent, boolean night, boolean unsave) {
        TLRPC.TL_theme info = accent != null ? accent.info : themeInfo.info;
        if (info != null) {
            TLRPC.TL_account_saveTheme req = new TLRPC.TL_account_saveTheme();
            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            inputTheme.id = info.id;
            inputTheme.access_hash = info.access_hash;
            req.theme = inputTheme;
            req.unsave = unsave;
            getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda287.INSTANCE);
            getConnectionsManager().resumeNetworkMaybe();
        }
        if (!unsave) {
            installTheme(themeInfo, accent, night);
        }
    }

    public static /* synthetic */ void lambda$saveTheme$95(TLObject response, TLRPC.TL_error error) {
    }

    public void installTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent accent, boolean night) {
        TLRPC.TL_theme info = accent != null ? accent.info : themeInfo.info;
        String slug = accent != null ? accent.patternSlug : themeInfo.slug;
        boolean isBlured = accent == null && themeInfo.isBlured;
        boolean isMotion = accent != null ? accent.patternMotion : themeInfo.isMotion;
        TLRPC.TL_account_installTheme req = new TLRPC.TL_account_installTheme();
        req.dark = night;
        if (info != null) {
            req.format = "android";
            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            inputTheme.id = info.id;
            inputTheme.access_hash = info.access_hash;
            req.theme = inputTheme;
            req.flags |= 2;
        }
        getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda275.INSTANCE);
        if (!TextUtils.isEmpty(slug)) {
            TLRPC.TL_account_installWallPaper req2 = new TLRPC.TL_account_installWallPaper();
            TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
            inputWallPaperSlug.slug = slug;
            req2.wallpaper = inputWallPaperSlug;
            req2.settings = new TLRPC.TL_wallPaperSettings();
            req2.settings.blur = isBlured;
            req2.settings.motion = isMotion;
            getConnectionsManager().sendRequest(req2, MessagesController$$ExternalSyntheticLambda276.INSTANCE);
        }
    }

    public static /* synthetic */ void lambda$installTheme$96(TLObject response, TLRPC.TL_error error) {
    }

    public static /* synthetic */ void lambda$installTheme$97(TLObject response, TLRPC.TL_error error) {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void saveThemeToServer(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent accent) {
        File pathToWallpaper;
        String key;
        if (themeInfo == null) {
            return;
        }
        if (accent != 0) {
            key = accent.saveToFile().getAbsolutePath();
            pathToWallpaper = accent.getPathToWallpaper();
        } else {
            key = themeInfo.pathToFile;
            pathToWallpaper = null;
        }
        if (key == null || this.uploadingThemes.containsKey(key)) {
            return;
        }
        this.uploadingThemes.put(key, accent != 0 ? accent : themeInfo);
        final String str = key;
        final File file = pathToWallpaper;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m840xea760298(str, file, accent, themeInfo);
            }
        });
    }

    /* renamed from: lambda$saveThemeToServer$99$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m840xea760298(final String key, File pathToWallpaper, final Theme.ThemeAccent accent, final Theme.ThemeInfo themeInfo) {
        final String thumbPath = Theme.createThemePreviewImage(key, pathToWallpaper != null ? pathToWallpaper.getAbsolutePath() : null, accent);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m839x53493d7(thumbPath, key, accent, themeInfo);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$saveThemeToServer$98$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m839x53493d7(String thumbPath, String key, Theme.ThemeAccent accent, Theme.ThemeInfo themeInfo) {
        if (thumbPath == null) {
            this.uploadingThemes.remove(key);
            return;
        }
        this.uploadingThemes.put(thumbPath, accent != null ? accent : themeInfo);
        if (accent == null) {
            themeInfo.uploadingFile = key;
            themeInfo.uploadingThumb = thumbPath;
        } else {
            accent.uploadingFile = key;
            accent.uploadingThumb = thumbPath;
        }
        getFileLoader().uploadFile(key, false, true, ConnectionsManager.FileTypeFile);
        getFileLoader().uploadFile(thumbPath, false, true, 16777216);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void saveWallpaperToServer(File path, Theme.OverrideWallpaperInfo info, boolean install, long taskId) {
        TLRPC.WallPaper wallPaper;
        TLRPC.InputWallPaper inputWallPaper;
        TLObject req;
        final long newTaskId;
        if (this.uploadingWallpaper != null) {
            File finalPath = new File(ApplicationLoader.getFilesDirFixed(), info.originalFileName);
            if (path == null || (!path.getAbsolutePath().equals(this.uploadingWallpaper) && !path.equals(finalPath))) {
                getFileLoader().cancelFileUpload(this.uploadingWallpaper, false);
                this.uploadingWallpaper = null;
                this.uploadingWallpaperInfo = null;
            } else {
                this.uploadingWallpaperInfo = info;
                return;
            }
        }
        if (path != null) {
            this.uploadingWallpaper = path.getAbsolutePath();
            this.uploadingWallpaperInfo = info;
            getFileLoader().uploadFile(this.uploadingWallpaper, false, true, 16777216);
        } else if (!info.isDefault() && !info.isColor() && info.wallpaperId > 0 && !info.isTheme()) {
            if (info.wallpaperId > 0) {
                TLRPC.TL_inputWallPaper inputWallPaperId = new TLRPC.TL_inputWallPaper();
                inputWallPaperId.id = info.wallpaperId;
                inputWallPaperId.access_hash = info.accessHash;
                inputWallPaper = inputWallPaperId;
            } else {
                TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                inputWallPaperSlug.slug = info.slug;
                inputWallPaper = inputWallPaperSlug;
            }
            TLRPC.TL_wallPaperSettings settings = new TLRPC.TL_wallPaperSettings();
            settings.blur = info.isBlurred;
            settings.motion = info.isMotion;
            if (info.color != 0) {
                settings.background_color = info.color & ViewCompat.MEASURED_SIZE_MASK;
                settings.flags |= 1;
                settings.intensity = (int) (info.intensity * 100.0f);
                settings.flags |= 8;
            }
            if (info.gradientColor1 != 0) {
                settings.second_background_color = info.gradientColor1 & ViewCompat.MEASURED_SIZE_MASK;
                settings.rotation = AndroidUtilities.getWallpaperRotation(info.rotation, true);
                settings.flags |= 16;
            }
            if (info.gradientColor2 != 0) {
                settings.third_background_color = info.gradientColor2 & ViewCompat.MEASURED_SIZE_MASK;
                settings.flags |= 32;
            }
            if (info.gradientColor3 != 0) {
                settings.fourth_background_color = info.gradientColor3 & ViewCompat.MEASURED_SIZE_MASK;
                settings.flags |= 64;
            }
            if (install) {
                TLRPC.TL_account_installWallPaper request = new TLRPC.TL_account_installWallPaper();
                request.wallpaper = inputWallPaper;
                request.settings = settings;
                req = request;
            } else {
                TLRPC.TL_account_saveWallPaper request2 = new TLRPC.TL_account_saveWallPaper();
                request2.wallpaper = inputWallPaper;
                request2.settings = settings;
                req = request2;
            }
            if (taskId != 0) {
                newTaskId = taskId;
            } else {
                NativeByteBuffer data = null;
                try {
                    data = new NativeByteBuffer(1024);
                    data.writeInt32(21);
                    data.writeBool(info.isBlurred);
                    data.writeBool(info.isMotion);
                    data.writeInt32(info.color);
                    data.writeInt32(info.gradientColor1);
                    data.writeInt32(info.rotation);
                    data.writeDouble(info.intensity);
                    data.writeBool(install);
                    data.writeString(info.slug);
                    data.writeString(info.originalFileName);
                    data.limit(data.position());
                } catch (Exception e) {
                    FileLog.e(e);
                }
                newTaskId = getMessagesStorage().createPendingTask(data);
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda203
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m841x21c1a1d4(newTaskId, tLObject, tL_error);
                }
            });
        }
        if ((info.isColor() || info.gradientColor2 != 0) && info.wallpaperId <= 0) {
            if (info.isColor()) {
                wallPaper = new TLRPC.TL_wallPaperNoFile();
            } else {
                wallPaper = new TLRPC.TL_wallPaper();
                wallPaper.slug = info.slug;
                wallPaper.document = new TLRPC.TL_documentEmpty();
            }
            if (info.wallpaperId == 0) {
                wallPaper.id = Utilities.random.nextLong();
                if (wallPaper.id > 0) {
                    wallPaper.id = -wallPaper.id;
                }
            } else {
                wallPaper.id = info.wallpaperId;
            }
            wallPaper.dark = MotionBackgroundDrawable.isDark(info.color, info.gradientColor1, info.gradientColor2, info.gradientColor3);
            wallPaper.flags |= 4;
            wallPaper.settings = new TLRPC.TL_wallPaperSettings();
            wallPaper.settings.blur = info.isBlurred;
            wallPaper.settings.motion = info.isMotion;
            if (info.color != 0) {
                wallPaper.settings.background_color = info.color;
                wallPaper.settings.flags |= 1;
                wallPaper.settings.intensity = (int) (info.intensity * 100.0f);
                wallPaper.settings.flags |= 8;
            }
            if (info.gradientColor1 != 0) {
                wallPaper.settings.second_background_color = info.gradientColor1;
                wallPaper.settings.rotation = AndroidUtilities.getWallpaperRotation(info.rotation, true);
                wallPaper.settings.flags |= 16;
            }
            if (info.gradientColor2 != 0) {
                wallPaper.settings.third_background_color = info.gradientColor2;
                wallPaper.settings.flags |= 32;
            }
            if (info.gradientColor3 != 0) {
                wallPaper.settings.fourth_background_color = info.gradientColor3;
                wallPaper.settings.flags |= 64;
            }
            ArrayList<TLRPC.WallPaper> arrayList = new ArrayList<>();
            arrayList.add(wallPaper);
            getMessagesStorage().putWallpapers(arrayList, -3);
            getMessagesStorage().getWallpapers();
        }
    }

    /* renamed from: lambda$saveWallpaperToServer$100$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m841x21c1a1d4(long newTaskId, TLObject response, TLRPC.TL_error error) {
        getMessagesStorage().removePendingTask(newTaskId);
    }

    public void markDialogMessageAsDeleted(long dialogId, ArrayList<Integer> messages) {
        MessageObject obj = this.dialogMessage.get(dialogId);
        if (obj != null) {
            for (int a = 0; a < messages.size(); a++) {
                Integer id = messages.get(a);
                if (obj.getId() == id.intValue()) {
                    obj.deleted = true;
                    return;
                }
            }
        }
    }

    public void deleteMessages(ArrayList<Integer> messages, ArrayList<Long> randoms, TLRPC.EncryptedChat encryptedChat, long dialogId, boolean forAll, boolean scheduled) {
        deleteMessages(messages, randoms, encryptedChat, dialogId, forAll, scheduled, false, 0L, null);
    }

    public void deleteMessages(ArrayList<Integer> messages, ArrayList<Long> randoms, TLRPC.EncryptedChat encryptedChat, long dialogId, boolean forAll, boolean scheduled, boolean cacheOnly) {
        deleteMessages(messages, randoms, encryptedChat, dialogId, forAll, scheduled, cacheOnly, 0L, null);
    }

    public void deleteMessages(ArrayList<Integer> messages, ArrayList<Long> randoms, TLRPC.EncryptedChat encryptedChat, long dialogId, boolean forAll, boolean scheduled, boolean cacheOnly, long taskId, TLObject taskRequest) {
        long channelId;
        ArrayList<Integer> toSend;
        final long newTaskId;
        TLRPC.TL_messages_deleteMessages req;
        Exception e;
        long newTaskId2;
        TLRPC.TL_channels_deleteMessages req2;
        final long newTaskId3;
        TLRPC.TL_messages_deleteScheduledMessages req3;
        long channelId2;
        char c;
        if ((messages == null || messages.isEmpty()) && taskId == 0) {
            return;
        }
        ArrayList<Integer> toSend2 = null;
        if (taskId == 0) {
            if (dialogId != 0 && DialogObject.isChatDialog(dialogId)) {
                TLRPC.Chat chat = getChat(Long.valueOf(-dialogId));
                channelId2 = ChatObject.isChannel(chat) ? chat.id : 0L;
            } else {
                channelId2 = 0;
            }
            if (!cacheOnly) {
                toSend2 = new ArrayList<>();
                int N = messages.size();
                for (int a = 0; a < N; a++) {
                    Integer mid = messages.get(a);
                    if (mid.intValue() > 0) {
                        toSend2.add(mid);
                    }
                }
            }
            if (scheduled) {
                getMessagesStorage().markMessagesAsDeleted(dialogId, messages, true, false, true);
                c = 1;
            } else {
                if (channelId2 == 0) {
                    for (int a2 = 0; a2 < messages.size(); a2++) {
                        Integer id = messages.get(a2);
                        MessageObject obj = this.dialogMessagesByIds.get(id.intValue());
                        if (obj != null) {
                            obj.deleted = true;
                        }
                    }
                } else {
                    markDialogMessageAsDeleted(dialogId, messages);
                }
                getMessagesStorage().markMessagesAsDeleted(dialogId, messages, true, forAll, false);
                c = 1;
                getMessagesStorage().updateDialogsWithDeletedMessages(dialogId, channelId2, messages, null, true);
            }
            NotificationCenter notificationCenter = getNotificationCenter();
            int i = NotificationCenter.messagesDeleted;
            Object[] objArr = new Object[3];
            objArr[0] = messages;
            objArr[c] = Long.valueOf(channelId2);
            objArr[2] = Boolean.valueOf(scheduled);
            notificationCenter.postNotificationName(i, objArr);
            toSend = toSend2;
            channelId = channelId2;
        } else if (!(taskRequest instanceof TLRPC.TL_channels_deleteMessages)) {
            toSend = null;
            channelId = 0;
        } else {
            channelId = ((TLRPC.TL_channels_deleteMessages) taskRequest).channel.channel_id;
            toSend = null;
        }
        if (cacheOnly) {
            return;
        }
        if (scheduled) {
            if (taskRequest instanceof TLRPC.TL_messages_deleteScheduledMessages) {
                req3 = (TLRPC.TL_messages_deleteScheduledMessages) taskRequest;
                newTaskId3 = taskId;
            } else {
                TLRPC.TL_messages_deleteScheduledMessages req4 = new TLRPC.TL_messages_deleteScheduledMessages();
                req4.id = toSend;
                req4.peer = getInputPeer(dialogId);
                NativeByteBuffer data = null;
                try {
                    data = new NativeByteBuffer(req4.getObjectSize() + 12);
                    data.writeInt32(24);
                    data.writeInt64(dialogId);
                    req4.serializeToStream(data);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                req3 = req4;
                newTaskId3 = getMessagesStorage().createPendingTask(data);
            }
            getConnectionsManager().sendRequest(req3, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda193
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m638x33eaba03(newTaskId3, tLObject, tL_error);
                }
            });
        } else if (channelId != 0) {
            if (taskRequest != null) {
                req2 = (TLRPC.TL_channels_deleteMessages) taskRequest;
                newTaskId2 = taskId;
            } else {
                TLRPC.TL_channels_deleteMessages req5 = new TLRPC.TL_channels_deleteMessages();
                req5.id = toSend;
                req5.channel = getInputChannel(channelId);
                NativeByteBuffer data2 = null;
                try {
                    data2 = new NativeByteBuffer(req5.getObjectSize() + 12);
                    data2.writeInt32(24);
                    data2.writeInt64(dialogId);
                    req5.serializeToStream(data2);
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
                req2 = req5;
                newTaskId2 = getMessagesStorage().createPendingTask(data2);
            }
            final long j = channelId;
            final long j2 = newTaskId2;
            getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda213
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m639x192c28c4(j, j2, tLObject, tL_error);
                }
            });
        } else {
            if (randoms != null && encryptedChat != null && !randoms.isEmpty()) {
                getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat, randoms, null);
            }
            if (taskRequest instanceof TLRPC.TL_messages_deleteMessages) {
                req = (TLRPC.TL_messages_deleteMessages) taskRequest;
                newTaskId = taskId;
            } else {
                TLRPC.TL_messages_deleteMessages req6 = new TLRPC.TL_messages_deleteMessages();
                req6.id = toSend;
                req6.revoke = forAll;
                NativeByteBuffer data3 = null;
                try {
                    data3 = new NativeByteBuffer(req6.getObjectSize() + 12);
                    data3.writeInt32(24);
                } catch (Exception e4) {
                    e = e4;
                }
                try {
                    data3.writeInt64(dialogId);
                    req6.serializeToStream(data3);
                } catch (Exception e5) {
                    e = e5;
                    FileLog.e(e);
                    req = req6;
                    newTaskId = getMessagesStorage().createPendingTask(data3);
                    getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda194
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.m640xfe6d9785(newTaskId, tLObject, tL_error);
                        }
                    });
                }
                req = req6;
                newTaskId = getMessagesStorage().createPendingTask(data3);
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda194
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m640xfe6d9785(newTaskId, tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$deleteMessages$101$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m638x33eaba03(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            processUpdates(updates, false);
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    /* renamed from: lambda$deleteMessages$102$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m639x192c28c4(long channelId, long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
            processNewChannelDifferenceParams(res.pts, res.pts_count, channelId);
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    /* renamed from: lambda$deleteMessages$103$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m640xfe6d9785(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void unpinAllMessages(final TLRPC.Chat chat, final TLRPC.User user) {
        if (chat == null && user == null) {
            return;
        }
        TLRPC.TL_messages_unpinAllMessages req = new TLRPC.TL_messages_unpinAllMessages();
        req.peer = getInputPeer(chat != null ? -chat.id : user.id);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda245
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m880xe426980(chat, user, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$unpinAllMessages$104$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m880xe426980(TLRPC.Chat chat, TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_messages_affectedHistory res = (TLRPC.TL_messages_affectedHistory) response;
            if (ChatObject.isChannel(chat)) {
                processNewChannelDifferenceParams(res.pts, res.pts_count, chat.id);
            } else {
                processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
            }
            new ArrayList();
            getMessagesStorage().updatePinnedMessages(chat != null ? -chat.id : user.id, null, false, 0, 0, false, null);
        }
    }

    public void pinMessage(final TLRPC.Chat chat, final TLRPC.User user, final int id, final boolean unpin, boolean oneSide, boolean notify) {
        if (chat == null && user == null) {
            return;
        }
        TLRPC.TL_messages_updatePinnedMessage req = new TLRPC.TL_messages_updatePinnedMessage();
        req.peer = getInputPeer(chat != null ? -chat.id : user.id);
        req.id = id;
        req.unpin = unpin;
        req.silent = !notify;
        req.pm_oneside = oneSide;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda189
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m772lambda$pinMessage$105$orgtelegrammessengerMessagesController(id, chat, user, unpin, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$pinMessage$105$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m772lambda$pinMessage$105$orgtelegrammessengerMessagesController(int id, TLRPC.Chat chat, TLRPC.User user, boolean unpin, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            ArrayList<Integer> ids = new ArrayList<>();
            ids.add(Integer.valueOf(id));
            getMessagesStorage().updatePinnedMessages(chat != null ? -chat.id : user.id, ids, !unpin, -1, 0, false, null);
            TLRPC.Updates updates = (TLRPC.Updates) response;
            processUpdates(updates, false);
        }
    }

    public void deleteUserChannelHistory(final TLRPC.Chat currentChat, final TLRPC.User fromUser, final TLRPC.Chat fromChat, int offset) {
        long fromId = 0;
        if (fromUser != null) {
            fromId = fromUser.id;
        } else if (fromChat != null) {
            fromId = fromChat.id;
        }
        if (offset == 0) {
            getMessagesStorage().deleteUserChatHistory(-currentChat.id, fromId);
        }
        TLRPC.TL_channels_deleteParticipantHistory req = new TLRPC.TL_channels_deleteParticipantHistory();
        req.channel = getInputChannel(currentChat);
        req.participant = fromUser != null ? getInputPeer(fromUser) : getInputPeer(fromChat);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda246
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m648xbd060338(currentChat, fromUser, fromChat, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$deleteUserChannelHistory$106$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m648xbd060338(TLRPC.Chat currentChat, TLRPC.User fromUser, TLRPC.Chat fromChat, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_affectedHistory res = (TLRPC.TL_messages_affectedHistory) response;
            if (res.offset > 0) {
                deleteUserChannelHistory(currentChat, fromUser, fromChat, res.offset);
            }
            processNewChannelDifferenceParams(res.pts, res.pts_count, currentChat.id);
        }
    }

    public ArrayList<TLRPC.Dialog> getAllDialogs() {
        return this.allDialogs;
    }

    public void putDialogsEndReachedAfterRegistration() {
        this.dialogsEndReached.put(0, true);
        this.serverDialogsEndReached.put(0, true);
    }

    public boolean isDialogsEndReached(int folderId) {
        return this.dialogsEndReached.get(folderId);
    }

    public boolean isLoadingDialogs(int folderId) {
        return this.loadingDialogs.get(folderId);
    }

    public boolean isServerDialogsEndReached(int folderId) {
        return this.serverDialogsEndReached.get(folderId);
    }

    public boolean hasHiddenArchive() {
        return SharedConfig.archiveHidden && this.dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
    }

    public ArrayList<TLRPC.Dialog> getDialogs(int folderId) {
        ArrayList<TLRPC.Dialog> dialogs = this.dialogsByFolder.get(folderId);
        if (dialogs == null) {
            return new ArrayList<>();
        }
        return dialogs;
    }

    public int getAllFoldersDialogsCount() {
        int count = 0;
        for (int i = 0; i < this.dialogsByFolder.size(); i++) {
            SparseArray<ArrayList<TLRPC.Dialog>> sparseArray = this.dialogsByFolder;
            List<TLRPC.Dialog> dialogs = sparseArray.get(sparseArray.keyAt(i));
            if (dialogs != null) {
                count += dialogs.size();
            }
        }
        return count;
    }

    public int getTotalDialogsCount() {
        ArrayList<TLRPC.Dialog> dialogs = this.dialogsByFolder.get(0);
        if (dialogs != null) {
            int count = 0 + dialogs.size();
            return count;
        }
        return 0;
    }

    public void putAllNeededDraftDialogs() {
        LongSparseArray<SparseArray<TLRPC.DraftMessage>> drafts = getMediaDataController().getDrafts();
        int size = drafts.size();
        for (int i = 0; i < size; i++) {
            SparseArray<TLRPC.DraftMessage> threads = drafts.valueAt(i);
            TLRPC.DraftMessage draftMessage = threads.get(0);
            if (draftMessage != null) {
                putDraftDialogIfNeed(drafts.keyAt(i), draftMessage);
            }
        }
    }

    public void putDraftDialogIfNeed(long dialogId, TLRPC.DraftMessage draftMessage) {
        if (this.dialogs_dict.indexOfKey(dialogId) < 0) {
            MediaDataController mediaDataController = getMediaDataController();
            int dialogsCount = this.allDialogs.size();
            int i = 0;
            if (dialogsCount > 0) {
                TLRPC.Dialog dialog = this.allDialogs.get(dialogsCount - 1);
                long minDate = DialogObject.getLastMessageOrDraftDate(dialog, mediaDataController.getDraft(dialog.id, 0));
                if (draftMessage.date < minDate) {
                    return;
                }
            }
            TLRPC.TL_dialog dialog2 = new TLRPC.TL_dialog();
            dialog2.id = dialogId;
            dialog2.draft = draftMessage;
            dialog2.folder_id = mediaDataController.getDraftFolderId(dialogId);
            if (dialogId < 0 && ChatObject.isChannel(getChat(Long.valueOf(-dialogId)))) {
                i = 1;
            }
            dialog2.flags = i;
            this.dialogs_dict.put(dialogId, dialog2);
            this.allDialogs.add(dialog2);
            sortDialogs(null);
        }
    }

    public void removeDraftDialogIfNeed(long dialogId) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog != null && dialog.top_message == 0) {
            this.dialogs_dict.remove(dialog.id);
            this.allDialogs.remove(dialog);
        }
    }

    private void removeDialog(TLRPC.Dialog dialog) {
        if (dialog == null) {
            return;
        }
        final long did = dialog.id;
        if (this.dialogsServerOnly.remove(dialog) && DialogObject.isChannel(dialog)) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda319
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m833x79fcfde(did);
                }
            });
        }
        this.allDialogs.remove(dialog);
        this.dialogsMyChannels.remove(dialog);
        this.dialogsMyGroups.remove(dialog);
        this.dialogsCanAddUsers.remove(dialog);
        this.dialogsChannelsOnly.remove(dialog);
        this.dialogsGroupsOnly.remove(dialog);
        this.dialogsUsersOnly.remove(dialog);
        this.dialogsForBlock.remove(dialog);
        this.dialogsForward.remove(dialog);
        int a = 0;
        while (true) {
            DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
            if (a >= dialogFilterArr.length) {
                break;
            }
            if (dialogFilterArr[a] != null) {
                dialogFilterArr[a].dialogs.remove(dialog);
            }
            a++;
        }
        this.dialogs_dict.remove(did);
        ArrayList<TLRPC.Dialog> dialogs = this.dialogsByFolder.get(dialog.folder_id);
        if (dialogs != null) {
            dialogs.remove(dialog);
        }
    }

    /* renamed from: lambda$removeDialog$107$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m833x79fcfde(long did) {
        this.channelsPts.delete(-did);
        this.shortPollChannels.delete(-did);
        this.needShortPollChannels.delete(-did);
        this.shortPollOnlines.delete(-did);
        this.needShortPollOnlines.delete(-did);
    }

    public void hidePromoDialog() {
        if (this.promoDialog == null) {
            return;
        }
        TLRPC.TL_help_hidePromoData req = new TLRPC.TL_help_hidePromoData();
        req.peer = getInputPeer(this.promoDialog.id);
        getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda274.INSTANCE);
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda88
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m700x14815c2f();
            }
        });
        removePromoDialog();
    }

    public static /* synthetic */ void lambda$hidePromoDialog$108(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$hidePromoDialog$109$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m700x14815c2f() {
        this.promoDialogId = 0L;
        this.proxyDialogAddress = null;
        this.nextPromoInfoCheckTime = getConnectionsManager().getCurrentTime() + 3600;
        getGlobalMainSettings().edit().putLong("proxy_dialog", this.promoDialogId).remove("proxyDialogAddress").putInt("nextPromoInfoCheckTime", this.nextPromoInfoCheckTime).commit();
    }

    public void deleteDialog(long did, int onlyHistory) {
        deleteDialog(did, onlyHistory, false);
    }

    public void deleteDialog(long did, int onlyHistory, boolean revoke) {
        deleteDialog(did, 1, onlyHistory, 0, revoke, null, 0L);
    }

    public void setDialogHistoryTTL(long did, int ttl) {
        TLRPC.TL_messages_setHistoryTTL req = new TLRPC.TL_messages_setHistoryTTL();
        req.peer = getInputPeer(did);
        req.period = ttl;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda169
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m856xf4077492(tLObject, tL_error);
            }
        });
        TLRPC.ChatFull chatFull = null;
        TLRPC.UserFull userFull = null;
        if (did > 0) {
            userFull = getUserFull(did);
            if (userFull == null) {
                return;
            }
            userFull.ttl_period = ttl;
            userFull.flags |= 16384;
        } else {
            chatFull = getChatFull(-did);
            if (chatFull == null) {
                return;
            }
            chatFull.ttl_period = ttl;
            if (chatFull instanceof TLRPC.TL_channelFull) {
                chatFull.flags |= 16777216;
            } else {
                chatFull.flags |= 16384;
            }
        }
        if (chatFull != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, 0, false, false);
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(did), userFull);
        }
    }

    /* renamed from: lambda$setDialogHistoryTTL$110$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m856xf4077492(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            processUpdates(updates, false);
        }
    }

    public void setDialogsInTransaction(boolean transaction) {
        this.dialogsInTransaction = transaction;
        if (!transaction) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:148:0x039c  */
    /* JADX WARN: Removed duplicated region for block: B:157:0x03dd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void deleteDialog(final long r30, int r32, final int r33, int r34, final boolean r35, final org.telegram.tgnet.TLRPC.InputPeer r36, final long r37) {
        /*
            Method dump skipped, instructions count: 1091
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteDialog(long, int, int, int, boolean, org.telegram.tgnet.TLRPC$InputPeer, long):void");
    }

    /* renamed from: lambda$deleteDialog$111$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m633xe353b85e(long did, int onlyHistory, boolean revoke, TLRPC.InputPeer peerFinal, long taskId, int param) {
        deleteDialog(did, 2, onlyHistory, Math.max(0, param), revoke, peerFinal, taskId);
        checkIfFolderEmpty(1);
    }

    /* renamed from: lambda$deleteDialog$112$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m634xc895271f(long did) {
        getNotificationsController().removeNotificationsForDialog(did);
    }

    /* renamed from: lambda$deleteDialog$113$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m635xadd695e0(final long did) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda315
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m634xc895271f(did);
            }
        });
    }

    /* renamed from: lambda$deleteDialog$114$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m636x931804a1(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
        if (response != null) {
            processUpdates((TLRPC.Updates) response, false);
        }
    }

    /* renamed from: lambda$deleteDialog$115$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m637x78597362(long newTaskId, long did, int onlyHistory, int max_id_delete_final, boolean revoke, TLRPC.InputPeer peerFinal, TLObject response, TLRPC.TL_error error) {
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
        if (error == null) {
            TLRPC.TL_messages_affectedHistory res = (TLRPC.TL_messages_affectedHistory) response;
            if (res.offset > 0) {
                deleteDialog(did, 0, onlyHistory, max_id_delete_final, revoke, peerFinal, 0L);
            }
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
            getMessagesStorage().onDeleteQueryComplete(did);
        }
    }

    public void saveGif(final Object parentObject, TLRPC.Document document) {
        if (parentObject == null || !MessageObject.isGifDocument(document)) {
            return;
        }
        final TLRPC.TL_messages_saveGif req = new TLRPC.TL_messages_saveGif();
        req.id = new TLRPC.TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.id.file_reference = document.file_reference;
        if (req.id.file_reference == null) {
            req.id.file_reference = new byte[0];
        }
        req.unsave = false;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda236
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m837lambda$saveGif$116$orgtelegrammessengerMessagesController(parentObject, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$saveGif$116$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m837lambda$saveGif$116$orgtelegrammessengerMessagesController(Object parentObject, TLRPC.TL_messages_saveGif req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text)) {
            getFileRefController().requestReference(parentObject, req);
        }
    }

    public void saveRecentSticker(final Object parentObject, TLRPC.Document document, boolean asMask) {
        if (parentObject == null || document == null) {
            return;
        }
        final TLRPC.TL_messages_saveRecentSticker req = new TLRPC.TL_messages_saveRecentSticker();
        req.id = new TLRPC.TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.id.file_reference = document.file_reference;
        if (req.id.file_reference == null) {
            req.id.file_reference = new byte[0];
        }
        req.unsave = false;
        req.attached = asMask;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda237
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m838xce84869c(parentObject, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$saveRecentSticker$117$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m838xce84869c(Object parentObject, TLRPC.TL_messages_saveRecentSticker req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text)) {
            getFileRefController().requestReference(parentObject, req);
        }
    }

    public void loadChannelParticipants(final Long chatId) {
        if (this.loadingFullParticipants.contains(chatId) || this.loadedFullParticipants.contains(chatId)) {
            return;
        }
        this.loadingFullParticipants.add(chatId);
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = getInputChannel(chatId.longValue());
        req.filter = new TLRPC.TL_channelParticipantsRecent();
        req.offset = 0;
        req.limit = 32;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda235
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m705xaaa69d76(chatId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadChannelParticipants$119$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m705xaaa69d76(final Long chatId, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda71
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m704xc5652eb5(error, response, chatId);
            }
        });
    }

    /* renamed from: lambda$loadChannelParticipants$118$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m704xc5652eb5(TLRPC.TL_error error, TLObject response, Long chatId) {
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            putUsers(res.users, false);
            putChats(res.chats, false);
            getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            getMessagesStorage().updateChannelUsers(chatId.longValue(), res.participants);
            this.loadedFullParticipants.add(chatId);
        }
        this.loadingFullParticipants.remove(chatId);
    }

    public void putChatFull(TLRPC.ChatFull chatFull) {
        this.fullChats.put(chatFull.id, chatFull);
    }

    public void processChatInfo(final long chatId, final TLRPC.ChatFull info, final ArrayList<TLRPC.User> usersArr, final boolean fromCache, final boolean force, final boolean byChannelUsers, final ArrayList<Integer> pinnedMessages, final HashMap<Integer, MessageObject> pinnedMessagesMap, final int totalPinnedCount, final boolean pinnedEndReached) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda121
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m773xdcc1bd44(fromCache, chatId, byChannelUsers, force, info, usersArr, pinnedMessages, pinnedMessagesMap, totalPinnedCount, pinnedEndReached);
            }
        });
    }

    /* renamed from: lambda$processChatInfo$120$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m773xdcc1bd44(boolean fromCache, long chatId, boolean byChannelUsers, boolean force, TLRPC.ChatFull info, ArrayList usersArr, ArrayList pinnedMessages, HashMap pinnedMessagesMap, int totalPinnedCount, boolean pinnedEndReached) {
        if (fromCache && chatId > 0 && !byChannelUsers) {
            loadFullChat(chatId, 0, force);
        }
        if (info != null) {
            if (this.fullChats.get(chatId) == null) {
                this.fullChats.put(chatId, info);
            }
            putUsers(usersArr, fromCache);
            if (info.stickerset != null) {
                getMediaDataController().getGroupStickerSetById(info.stickerset);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, info, 0, Boolean.valueOf(byChannelUsers), false);
        }
        if (pinnedMessages != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.pinnedInfoDidLoad, Long.valueOf(-chatId), pinnedMessages, pinnedMessagesMap, Integer.valueOf(totalPinnedCount), Boolean.valueOf(pinnedEndReached));
        }
    }

    public void loadUserInfo(TLRPC.User user, boolean force, int classGuid) {
        loadUserInfo(user, force, classGuid, 0);
    }

    public void loadUserInfo(TLRPC.User user, boolean force, int classGuid, int fromMessageId) {
        getMessagesStorage().loadUserInfo(user, force, classGuid, fromMessageId);
    }

    public void processUserInfo(final TLRPC.User user, final TLRPC.UserFull info, final boolean fromCache, final boolean force, final int classGuid, final ArrayList<Integer> pinnedMessages, final HashMap<Integer, MessageObject> pinnedMessagesMap, final int totalPinnedCount, final boolean pinnedEndReached) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda124
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m821xd9c3e1b8(fromCache, user, classGuid, force, info, pinnedMessages, pinnedMessagesMap, totalPinnedCount, pinnedEndReached);
            }
        });
    }

    /* renamed from: lambda$processUserInfo$121$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m821xd9c3e1b8(boolean fromCache, TLRPC.User user, int classGuid, boolean force, TLRPC.UserFull info, ArrayList pinnedMessages, HashMap pinnedMessagesMap, int totalPinnedCount, boolean pinnedEndReached) {
        if (fromCache) {
            loadFullUser(user, classGuid, force);
        }
        if (info != null) {
            if (this.fullUsers.get(user.id) == null) {
                this.fullUsers.put(user.id, info);
                int index = this.blockePeers.indexOfKey(user.id);
                if (info.blocked) {
                    if (index < 0) {
                        this.blockePeers.put(user.id, 1);
                        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
                    }
                } else if (index >= 0) {
                    this.blockePeers.removeAt(index);
                    getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(user.id), info);
        }
        if (pinnedMessages != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.pinnedInfoDidLoad, Long.valueOf(user.id), pinnedMessages, pinnedMessagesMap, Integer.valueOf(totalPinnedCount), Boolean.valueOf(pinnedEndReached));
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> */
    public void updateTimerProc() {
        Long dialogKey;
        int b;
        int currentServerTime;
        int b2;
        Long dialogKey2;
        int b3;
        int currentServerTime2;
        int timeToRemove;
        long currentTime = System.currentTimeMillis();
        checkDeletingTask(false);
        checkReadTasks();
        if (getUserConfig().isClientActivated()) {
            if (!this.ignoreSetOnline && getConnectionsManager().getPauseTime() == 0 && ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePausedStageQueue) {
                if (ApplicationLoader.mainInterfacePausedStageQueueTime != 0 && Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000 && this.statusSettingState != 1 && (this.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000 || this.offlineSent)) {
                    this.statusSettingState = 1;
                    if (this.statusRequest != 0) {
                        getConnectionsManager().cancelRequest(this.statusRequest, true);
                    }
                    TLRPC.TL_account_updateStatus req = new TLRPC.TL_account_updateStatus();
                    req.offline = false;
                    this.statusRequest = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda173
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.m888x821321c3(tLObject, tL_error);
                        }
                    });
                }
            } else if (this.statusSettingState != 2 && !this.offlineSent && Math.abs(System.currentTimeMillis() - getConnectionsManager().getPauseTime()) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                this.statusSettingState = 2;
                if (this.statusRequest != 0) {
                    getConnectionsManager().cancelRequest(this.statusRequest, true);
                }
                TLRPC.TL_account_updateStatus req2 = new TLRPC.TL_account_updateStatus();
                req2.offline = true;
                this.statusRequest = getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda174
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m889x67549084(tLObject, tL_error);
                    }
                });
            }
            if (this.updatesQueueChannels.size() != 0) {
                for (int a = 0; a < this.updatesQueueChannels.size(); a++) {
                    long key = this.updatesQueueChannels.keyAt(a);
                    long updatesStartWaitTime = this.updatesStartWaitTimeChannels.valueAt(a);
                    if (Math.abs(currentTime - updatesStartWaitTime) >= 1500) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("QUEUE CHANNEL " + key + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
                        }
                        processChannelsUpdatesQueue(key, 0);
                    }
                }
            }
            for (int a2 = 0; a2 < 3; a2++) {
                if (getUpdatesStartTime(a2) != 0 && Math.abs(currentTime - getUpdatesStartTime(a2)) >= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d(a2 + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
                    }
                    processUpdatesQueue(a2, 0);
                }
            }
        }
        final int currentServerTime3 = getConnectionsManager().getCurrentTime();
        if (Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
            this.lastViewsCheckTime = System.currentTimeMillis();
            if (this.channelViewsToSend.size() != 0) {
                int a3 = 0;
                while (a3 < this.channelViewsToSend.size()) {
                    final long key2 = this.channelViewsToSend.keyAt(a3);
                    final TLRPC.TL_messages_getMessagesViews req3 = new TLRPC.TL_messages_getMessagesViews();
                    req3.peer = getInputPeer(key2);
                    req3.id = this.channelViewsToSend.valueAt(a3);
                    req3.increment = a3 == 0;
                    getConnectionsManager().sendRequest(req3, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda228
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.m891x31d76e06(key2, req3, tLObject, tL_error);
                        }
                    });
                    a3++;
                }
                this.channelViewsToSend.clear();
            }
            if (this.pollsToCheckSize > 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda297
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m893xfc5a4b88(currentServerTime3);
                    }
                });
            }
        }
        if (!this.onlinePrivacy.isEmpty()) {
            ArrayList<Long> toRemove = null;
            for (Map.Entry<Long, Integer> entry : this.onlinePrivacy.entrySet()) {
                if (entry.getValue().intValue() < currentServerTime3 - 30) {
                    if (toRemove == null) {
                        toRemove = new ArrayList<>();
                    }
                    toRemove.add(entry.getKey());
                }
            }
            if (toRemove != null) {
                Iterator<Long> it = toRemove.iterator();
                while (it.hasNext()) {
                    Long uid = it.next();
                    this.onlinePrivacy.remove(uid);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda291
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m894xe19bba49();
                    }
                });
            }
        }
        if (this.shortPollChannels.size() != 0) {
            int a4 = 0;
            while (a4 < this.shortPollChannels.size()) {
                long key3 = this.shortPollChannels.keyAt(a4);
                int timeout = this.shortPollChannels.valueAt(a4);
                if (timeout < System.currentTimeMillis() / 1000) {
                    this.shortPollChannels.delete(key3);
                    a4--;
                    if (this.needShortPollChannels.indexOfKey(key3) >= 0) {
                        getChannelDifference(key3);
                    }
                }
                a4++;
            }
        }
        if (this.shortPollOnlines.size() != 0) {
            long time = SystemClock.elapsedRealtime() / 1000;
            int a5 = 0;
            while (a5 < this.shortPollOnlines.size()) {
                final long key4 = this.shortPollOnlines.keyAt(a5);
                int timeout2 = this.shortPollOnlines.valueAt(a5);
                if (timeout2 < time) {
                    if (this.needShortPollChannels.indexOfKey(key4) >= 0) {
                        this.shortPollOnlines.put(key4, (int) (300 + time));
                    } else {
                        this.shortPollOnlines.delete(key4);
                        a5--;
                    }
                    TLRPC.TL_messages_getOnlines req4 = new TLRPC.TL_messages_getOnlines();
                    req4.peer = getInputPeer(-key4);
                    getConnectionsManager().sendRequest(req4, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda206
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.m896x7a7cada0(key4, tLObject, tL_error);
                        }
                    });
                }
                a5++;
            }
        }
        if (!this.printingUsers.isEmpty() || this.lastPrintingStringCount != this.printingUsers.size()) {
            boolean updated = false;
            ArrayList<Long> dialogKeys = new ArrayList<>(this.printingUsers.keySet());
            int b4 = 0;
            while (b4 < dialogKeys.size()) {
                Long dialogKey3 = dialogKeys.get(b4);
                ConcurrentHashMap<Integer, ArrayList<PrintingUser>> concurrentHashMap = this.printingUsers.get(dialogKey3);
                if (concurrentHashMap == null) {
                    currentServerTime = currentServerTime3;
                    b = b4;
                    dialogKey = dialogKey3;
                } else {
                    ArrayList<Integer> threadKeys = new ArrayList<>(concurrentHashMap.keySet());
                    int c = 0;
                    while (c < threadKeys.size()) {
                        Integer threadKey = threadKeys.get(c);
                        ArrayList<PrintingUser> arr = concurrentHashMap.get(threadKey);
                        if (arr == null) {
                            currentServerTime2 = currentServerTime3;
                            b3 = b4;
                            dialogKey2 = dialogKey3;
                        } else {
                            int a6 = 0;
                            while (a6 < arr.size()) {
                                PrintingUser user = arr.get(a6);
                                if (user.action instanceof TLRPC.TL_sendMessageGamePlayAction) {
                                    timeToRemove = Indexable.MAX_BYTE_SIZE;
                                } else {
                                    timeToRemove = 5900;
                                }
                                int currentServerTime4 = currentServerTime3;
                                boolean updated2 = updated;
                                int b5 = b4;
                                Long dialogKey4 = dialogKey3;
                                if (user.lastTime + timeToRemove >= currentTime) {
                                    updated = updated2;
                                } else {
                                    arr.remove(user);
                                    a6--;
                                    updated = true;
                                }
                                a6++;
                                currentServerTime3 = currentServerTime4;
                                b4 = b5;
                                dialogKey3 = dialogKey4;
                            }
                            currentServerTime2 = currentServerTime3;
                            b3 = b4;
                            dialogKey2 = dialogKey3;
                        }
                        if (arr == null || arr.isEmpty()) {
                            concurrentHashMap.remove(threadKey);
                            threadKeys.remove(c);
                            c--;
                        }
                        c++;
                        currentServerTime3 = currentServerTime2;
                        b4 = b3;
                        dialogKey3 = dialogKey2;
                    }
                    currentServerTime = currentServerTime3;
                    b = b4;
                    dialogKey = dialogKey3;
                }
                if (concurrentHashMap == null || concurrentHashMap.isEmpty()) {
                    this.printingUsers.remove(dialogKey);
                    int b6 = b;
                    dialogKeys.remove(b6);
                    b2 = b6 - 1;
                } else {
                    b2 = b;
                }
                b4 = b2 + 1;
                currentServerTime3 = currentServerTime;
            }
            updatePrintingStrings();
            if (updated) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda292
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m897x5fbe1c61();
                    }
                });
            }
        }
        if (Theme.selectedAutoNightType == 1 && Math.abs(currentTime - lastThemeCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.themeCheckRunnable);
            lastThemeCheckTime = currentTime;
        }
        if (getUserConfig().savedPasswordHash != null && Math.abs(currentTime - lastPasswordCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.passwordCheckRunnable);
            lastPasswordCheckTime = currentTime;
        }
        if (this.lastPushRegisterSendTime != 0 && Math.abs(SystemClock.elapsedRealtime() - this.lastPushRegisterSendTime) >= 10800000) {
            GcmPushListenerService.sendRegistrationToServer(SharedConfig.pushString);
        }
        getLocationController().update();
        m603xa5dc1401(false);
        checkTosUpdate();
    }

    /* renamed from: lambda$updateTimerProc$122$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m888x821321c3(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            this.lastStatusUpdateTime = System.currentTimeMillis();
            this.offlineSent = false;
            this.statusSettingState = 0;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
            }
        }
        this.statusRequest = 0;
    }

    /* renamed from: lambda$updateTimerProc$123$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m889x67549084(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            this.offlineSent = true;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
            }
        }
        this.statusRequest = 0;
    }

    /* renamed from: lambda$updateTimerProc$125$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m891x31d76e06(long key, TLRPC.TL_messages_getMessagesViews req, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            final TLRPC.TL_messages_messageViews res = (TLRPC.TL_messages_messageViews) response;
            final LongSparseArray<SparseIntArray> channelViews = new LongSparseArray<>();
            final LongSparseArray<SparseIntArray> channelForwards = new LongSparseArray<>();
            final LongSparseArray<SparseArray<TLRPC.MessageReplies>> channelReplies = new LongSparseArray<>();
            SparseIntArray views = channelViews.get(key);
            SparseIntArray forwards = channelForwards.get(key);
            SparseArray<TLRPC.MessageReplies> replies = channelReplies.get(key);
            SparseIntArray views2 = views;
            SparseIntArray forwards2 = forwards;
            SparseArray<TLRPC.MessageReplies> replies2 = replies;
            for (int a1 = 0; a1 < req.id.size() && a1 < res.views.size(); a1++) {
                TLRPC.TL_messageViews messageViews = res.views.get(a1);
                if ((1 & messageViews.flags) != 0) {
                    if (views2 == null) {
                        views2 = new SparseIntArray();
                        channelViews.put(key, views2);
                    }
                    views2.put(req.id.get(a1).intValue(), messageViews.views);
                }
                if ((messageViews.flags & 2) != 0) {
                    if (forwards2 == null) {
                        forwards2 = new SparseIntArray();
                        channelForwards.put(key, forwards2);
                    }
                    forwards2.put(req.id.get(a1).intValue(), messageViews.forwards);
                }
                if ((messageViews.flags & 4) != 0) {
                    if (replies2 == null) {
                        replies2 = new SparseArray<>();
                        channelReplies.put(key, replies2);
                    }
                    replies2.put(req.id.get(a1).intValue(), messageViews.replies);
                }
            }
            getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            getMessagesStorage().putChannelViews(channelViews, channelForwards, channelReplies, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda83
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m890x4c95ff45(res, channelViews, channelForwards, channelReplies);
                }
            });
        }
    }

    /* renamed from: lambda$updateTimerProc$124$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m890x4c95ff45(TLRPC.TL_messages_messageViews res, LongSparseArray channelViews, LongSparseArray channelForwards, LongSparseArray channelReplies) {
        putUsers(res.users, false);
        putChats(res.chats, false);
        getNotificationCenter().postNotificationName(NotificationCenter.didUpdateMessagesViews, channelViews, channelForwards, channelReplies, false);
    }

    /* renamed from: lambda$updateTimerProc$127$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m893xfc5a4b88(int currentServerTime) {
        int b;
        long time = SystemClock.elapsedRealtime();
        int minExpireTime = Integer.MAX_VALUE;
        int a = 0;
        int N = this.pollsToCheck.size();
        while (a < N) {
            SparseArray<MessageObject> array = this.pollsToCheck.valueAt(a);
            if (array != null) {
                int b2 = 0;
                int N2 = array.size();
                while (b2 < N2) {
                    MessageObject messageObject = array.valueAt(b2);
                    TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) messageObject.messageOwner.media;
                    int timeout = Indexable.MAX_BYTE_SIZE;
                    boolean z = mediaPoll.poll.close_date != 0 && !mediaPoll.poll.closed;
                    final boolean expired = z;
                    if (z) {
                        if (mediaPoll.poll.close_date <= currentServerTime) {
                            timeout = 1000;
                        } else {
                            minExpireTime = Math.min(minExpireTime, mediaPoll.poll.close_date - currentServerTime);
                        }
                    }
                    int b3 = b2;
                    if (Math.abs(time - messageObject.pollLastCheckTime) < timeout) {
                        if (!messageObject.pollVisibleOnScreen && !expired) {
                            array.remove(messageObject.getId());
                            N2--;
                            b = b3 - 1;
                            b2 = b + 1;
                        }
                    } else {
                        messageObject.pollLastCheckTime = time;
                        TLRPC.TL_messages_getPollResults req = new TLRPC.TL_messages_getPollResults();
                        req.peer = getInputPeer(messageObject.getDialogId());
                        req.msg_id = messageObject.getId();
                        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda262
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.m892x1718dcc7(expired, tLObject, tL_error);
                            }
                        });
                    }
                    b = b3;
                    b2 = b + 1;
                }
                if (minExpireTime < 5) {
                    this.lastViewsCheckTime = Math.min(this.lastViewsCheckTime, System.currentTimeMillis() - ((5 - minExpireTime) * 1000));
                }
                if (array.size() == 0) {
                    LongSparseArray<SparseArray<MessageObject>> longSparseArray = this.pollsToCheck;
                    longSparseArray.remove(longSparseArray.keyAt(a));
                    N--;
                    a--;
                }
            }
            a++;
        }
        this.pollsToCheckSize = this.pollsToCheck.size();
    }

    /* renamed from: lambda$updateTimerProc$126$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m892x1718dcc7(boolean expired, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            if (expired) {
                for (int i = 0; i < updates.updates.size(); i++) {
                    TLRPC.Update update = updates.updates.get(i);
                    if (update instanceof TLRPC.TL_updateMessagePoll) {
                        TLRPC.TL_updateMessagePoll messagePoll = (TLRPC.TL_updateMessagePoll) update;
                        if (messagePoll.poll != null && !messagePoll.poll.closed) {
                            this.lastViewsCheckTime = System.currentTimeMillis() - 4000;
                        }
                    }
                }
            }
            processUpdates(updates, false);
        }
    }

    /* renamed from: lambda$updateTimerProc$128$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m894xe19bba49() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    /* renamed from: lambda$updateTimerProc$130$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m896x7a7cada0(final long key, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            final TLRPC.TL_chatOnlines res = (TLRPC.TL_chatOnlines) response;
            getMessagesStorage().updateChatOnlineCount(key, res.onlines);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda348
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m895xc6dd290a(key, res);
                }
            });
        }
    }

    /* renamed from: lambda$updateTimerProc$129$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m895xc6dd290a(long key, TLRPC.TL_chatOnlines res) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatOnlineCountDidLoad, Long.valueOf(key), Integer.valueOf(res.onlines));
    }

    /* renamed from: lambda$updateTimerProc$131$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m897x5fbe1c61() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
    }

    private void checkTosUpdate() {
        if (this.nextTosCheckTime > getConnectionsManager().getCurrentTime() || this.checkingTosUpdate || !getUserConfig().isClientActivated()) {
            return;
        }
        this.checkingTosUpdate = true;
        TLRPC.TL_help_getTermsOfServiceUpdate req = new TLRPC.TL_help_getTermsOfServiceUpdate();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda148
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m610x58f7f824(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$checkTosUpdate$133$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m610x58f7f824(TLObject response, TLRPC.TL_error error) {
        this.checkingTosUpdate = false;
        if (response instanceof TLRPC.TL_help_termsOfServiceUpdateEmpty) {
            this.nextTosCheckTime = ((TLRPC.TL_help_termsOfServiceUpdateEmpty) response).expires;
        } else if (response instanceof TLRPC.TL_help_termsOfServiceUpdate) {
            final TLRPC.TL_help_termsOfServiceUpdate res = (TLRPC.TL_help_termsOfServiceUpdate) response;
            this.nextTosCheckTime = res.expires;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda81
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m609x73b68963(res);
                }
            });
        } else {
            this.nextTosCheckTime = getConnectionsManager().getCurrentTime() + 3600;
        }
        this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).commit();
    }

    /* renamed from: lambda$checkTosUpdate$132$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m609x73b68963(TLRPC.TL_help_termsOfServiceUpdate res) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 4, res.terms_of_service);
    }

    public void checkPromoInfo(final boolean reset) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda118
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m603xa5dc1401(reset);
            }
        });
    }

    /* renamed from: checkPromoInfoInternal */
    public void m603xa5dc1401(boolean reset) {
        String str;
        if (reset && this.checkingPromoInfo) {
            this.checkingPromoInfo = false;
        }
        if ((!reset && this.nextPromoInfoCheckTime > getConnectionsManager().getCurrentTime()) || this.checkingPromoInfo) {
            return;
        }
        if (this.checkingPromoInfoRequestId != 0) {
            getConnectionsManager().cancelRequest(this.checkingPromoInfoRequestId, true);
            this.checkingPromoInfoRequestId = 0;
        }
        SharedPreferences preferences = getGlobalMainSettings();
        preferences.getBoolean("proxy_enabled", false);
        final String proxyAddress = preferences.getString("proxy_ip", "");
        final String proxySecret = preferences.getString("proxy_secret", "");
        int removeCurrent = 0;
        if (this.promoDialogId != 0 && this.promoDialogType == PROMO_TYPE_PROXY && (str = this.proxyDialogAddress) != null) {
            if (!str.equals(proxyAddress + proxySecret)) {
                removeCurrent = 1;
            }
        }
        this.lastCheckPromoId++;
        this.checkingPromoInfo = true;
        final int checkPromoId = this.lastCheckPromoId;
        TLRPC.TL_help_getPromoData req = new TLRPC.TL_help_getPromoData();
        this.checkingPromoInfoRequestId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda187
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m608x528f9be3(checkPromoId, proxyAddress, proxySecret, tLObject, tL_error);
            }
        });
        if (removeCurrent != 0) {
            this.promoDialogId = 0L;
            this.proxyDialogAddress = null;
            this.nextPromoInfoCheckTime = getConnectionsManager().getCurrentTime() + 3600;
            getGlobalMainSettings().edit().putLong("proxy_dialog", this.promoDialogId).remove("proxyDialogAddress").putInt("nextPromoInfoCheckTime", this.nextPromoInfoCheckTime).commit();
            AndroidUtilities.runOnUIThread(new MessagesController$$ExternalSyntheticLambda293(this));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x00ae, code lost:
        r12 = r1;
     */
    /* renamed from: lambda$checkPromoInfoInternal$139$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m608x528f9be3(final int r19, java.lang.String r20, java.lang.String r21, org.telegram.tgnet.TLObject r22, org.telegram.tgnet.TLRPC.TL_error r23) {
        /*
            Method dump skipped, instructions count: 381
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m608x528f9be3(int, java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* renamed from: lambda$checkPromoInfoInternal$138$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m607x6d4e2d22(final long did, final TLRPC.TL_help_promoData res, final int checkPromoId) {
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog != null && did != dialog.id) {
            removePromoDialog();
        }
        TLRPC.Dialog dialog2 = this.dialogs_dict.get(did);
        this.promoDialog = dialog2;
        if (dialog2 != null) {
            this.checkingPromoInfo = false;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
            return;
        }
        LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
        for (int a = 0; a < res.users.size(); a++) {
            TLRPC.User u = res.users.get(a);
            usersDict.put(u.id, u);
        }
        for (int a2 = 0; a2 < res.chats.size(); a2++) {
            TLRPC.Chat c = res.chats.get(a2);
            chatsDict.put(c.id, c);
        }
        TLRPC.TL_messages_getPeerDialogs req1 = new TLRPC.TL_messages_getPeerDialogs();
        TLRPC.TL_inputDialogPeer peer = new TLRPC.TL_inputDialogPeer();
        if (res.peer.user_id != 0) {
            peer.peer = new TLRPC.TL_inputPeerUser();
            peer.peer.user_id = res.peer.user_id;
            TLRPC.User user = usersDict.get(res.peer.user_id);
            if (user != null) {
                peer.peer.access_hash = user.access_hash;
            }
        } else if (res.peer.chat_id != 0) {
            peer.peer = new TLRPC.TL_inputPeerChat();
            peer.peer.chat_id = res.peer.chat_id;
            TLRPC.Chat chat = chatsDict.get(res.peer.chat_id);
            if (chat != null) {
                peer.peer.access_hash = chat.access_hash;
            }
        } else {
            peer.peer = new TLRPC.TL_inputPeerChannel();
            peer.peer.channel_id = res.peer.channel_id;
            TLRPC.Chat chat2 = chatsDict.get(res.peer.channel_id);
            if (chat2 != null) {
                peer.peer.access_hash = chat2.access_hash;
            }
        }
        req1.peers.add(peer);
        this.checkingPromoInfoRequestId = getConnectionsManager().sendRequest(req1, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda190
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m606x880cbe61(checkPromoId, res, did, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$checkPromoInfoInternal$137$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m606x880cbe61(int checkPromoId, final TLRPC.TL_help_promoData res, final long did, TLObject response1, TLRPC.TL_error error1) {
        if (checkPromoId == this.lastCheckPromoId) {
            this.checkingPromoInfoRequestId = 0;
            final TLRPC.TL_messages_peerDialogs res2 = (TLRPC.TL_messages_peerDialogs) response1;
            if (res2 != null && !res2.dialogs.isEmpty()) {
                getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                TLRPC.TL_messages_dialogs dialogs = new TLRPC.TL_messages_dialogs();
                dialogs.chats = res2.chats;
                dialogs.users = res2.users;
                dialogs.dialogs = res2.dialogs;
                dialogs.messages = res2.messages;
                getMessagesStorage().putDialogs(dialogs, 2);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda80
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m604xbd89e0df(res, res2, did);
                    }
                });
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda22
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m605xa2cb4fa0();
                    }
                });
            }
            this.checkingPromoInfo = false;
        }
    }

    /* renamed from: lambda$checkPromoInfoInternal$135$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m604xbd89e0df(TLRPC.TL_help_promoData res, TLRPC.TL_messages_peerDialogs res2, long did) {
        putUsers(res.users, false);
        putChats(res.chats, false);
        putUsers(res2.users, false);
        putChats(res2.chats, false);
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog != null) {
            if (dialog.id < 0) {
                TLRPC.Chat chat = getChat(Long.valueOf(-this.promoDialog.id));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.promoDialog);
                }
            } else {
                removeDialog(this.promoDialog);
            }
        }
        TLRPC.Dialog dialog2 = res2.dialogs.get(0);
        this.promoDialog = dialog2;
        dialog2.id = did;
        this.promoDialog.folder_id = 0;
        if (DialogObject.isChannel(this.promoDialog)) {
            this.channelsPts.put(-this.promoDialog.id, this.promoDialog.pts);
        }
        Integer value = this.dialogs_read_inbox_max.get(Long.valueOf(this.promoDialog.id));
        if (value == null) {
            value = 0;
        }
        this.dialogs_read_inbox_max.put(Long.valueOf(this.promoDialog.id), Integer.valueOf(Math.max(value.intValue(), this.promoDialog.read_inbox_max_id)));
        Integer value2 = this.dialogs_read_outbox_max.get(Long.valueOf(this.promoDialog.id));
        if (value2 == null) {
            value2 = 0;
        }
        this.dialogs_read_outbox_max.put(Long.valueOf(this.promoDialog.id), Integer.valueOf(Math.max(value2.intValue(), this.promoDialog.read_outbox_max_id)));
        this.dialogs_dict.put(did, this.promoDialog);
        if (!res2.messages.isEmpty()) {
            LongSparseArray<TLRPC.User> usersDict1 = new LongSparseArray<>();
            LongSparseArray<TLRPC.Chat> chatsDict1 = new LongSparseArray<>();
            for (int a = 0; a < res2.users.size(); a++) {
                TLRPC.User u = res2.users.get(a);
                usersDict1.put(u.id, u);
            }
            for (int a2 = 0; a2 < res2.chats.size(); a2++) {
                TLRPC.Chat c = res2.chats.get(a2);
                chatsDict1.put(c.id, c);
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, res2.messages.get(0), usersDict1, chatsDict1, false, true);
            this.dialogMessage.put(did, messageObject);
            if (this.promoDialog.last_message_date == 0) {
                this.promoDialog.last_message_date = messageObject.messageOwner.date;
            }
        }
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
    }

    /* renamed from: lambda$checkPromoInfoInternal$136$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m605xa2cb4fa0() {
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog != null) {
            if (dialog.id < 0) {
                TLRPC.Chat chat = getChat(Long.valueOf(-this.promoDialog.id));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.promoDialog);
                }
            } else {
                removeDialog(this.promoDialog);
            }
            this.promoDialog = null;
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void removePromoDialog() {
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog == null) {
            return;
        }
        if (dialog.id < 0) {
            TLRPC.Chat chat = getChat(Long.valueOf(-this.promoDialog.id));
            if (ChatObject.isNotInChat(chat) || chat.restricted) {
                removeDialog(this.promoDialog);
            }
        } else {
            removeDialog(this.promoDialog);
        }
        this.promoDialog = null;
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public boolean isPromoDialog(long did, boolean checkLeft) {
        TLRPC.Dialog dialog = this.promoDialog;
        return dialog != null && dialog.id == did && (!checkLeft || this.isLeftPromoChannel);
    }

    private String getUserNameForTyping(TLRPC.User user) {
        if (user == null) {
            return "";
        }
        if (user.first_name != null && user.first_name.length() > 0) {
            return user.first_name;
        }
        if (user.last_name == null || user.last_name.length() <= 0) {
            return "";
        }
        return user.last_name;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> */
    private void updatePrintingStrings() {
        Iterator<Map.Entry<Integer, ArrayList<PrintingUser>>> it;
        int i;
        String printingString;
        final LongSparseArray<SparseArray<CharSequence>> newStrings = new LongSparseArray<>();
        final LongSparseArray<SparseArray<Integer>> newTypes = new LongSparseArray<>();
        Iterator<Map.Entry<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>>> it2 = this.printingUsers.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>> next = it2.next();
            Long key = next.getKey();
            boolean isEncryptedChat = DialogObject.isEncryptedDialog(key.longValue());
            ConcurrentHashMap<Integer, ArrayList<PrintingUser>> value = next.getValue();
            Iterator<Map.Entry<Integer, ArrayList<PrintingUser>>> it3 = value.entrySet().iterator();
            while (it3.hasNext()) {
                Map.Entry<Integer, ArrayList<PrintingUser>> threadEntry = it3.next();
                Integer threadId = threadEntry.getKey();
                ArrayList<PrintingUser> arr = threadEntry.getValue();
                SparseArray<CharSequence> newPrintingStrings = new SparseArray<>();
                SparseArray<Integer> newPrintingStringsTypes = new SparseArray<>();
                Iterator<Map.Entry<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>>> it4 = it2;
                Map.Entry<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>> entry = next;
                newStrings.put(key.longValue(), newPrintingStrings);
                newTypes.put(key.longValue(), newPrintingStringsTypes);
                ConcurrentHashMap<Integer, ArrayList<PrintingUser>> concurrentHashMap = value;
                if (key.longValue() > 0 || isEncryptedChat) {
                    it = it3;
                    i = 0;
                } else if (arr.size() == 1) {
                    it = it3;
                    i = 0;
                } else {
                    int count = 0;
                    StringBuilder label = new StringBuilder();
                    Iterator<PrintingUser> it5 = arr.iterator();
                    while (true) {
                        if (!it5.hasNext()) {
                            it = it3;
                            break;
                        }
                        it = it3;
                        Map.Entry<Integer, ArrayList<PrintingUser>> threadEntry2 = threadEntry;
                        TLRPC.User user = getUser(Long.valueOf(it5.next().userId));
                        if (user != null) {
                            if (label.length() != 0) {
                                label.append(", ");
                            }
                            label.append(getUserNameForTyping(user));
                            count++;
                        }
                        if (count == 2) {
                            break;
                        }
                        it3 = it;
                        threadEntry = threadEntry2;
                    }
                    if (label.length() != 0) {
                        if (count == 1) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsTypingGroup", org.telegram.messenger.beta.R.string.IsTypingGroup, label.toString()));
                        } else if (arr.size() > 2) {
                            String plural = LocaleController.getPluralString("AndMoreTypingGroup", arr.size() - 2);
                            try {
                                newPrintingStrings.put(threadId.intValue(), String.format(plural, label.toString(), Integer.valueOf(arr.size() - 2)));
                            } catch (Exception e) {
                                newPrintingStrings.put(threadId.intValue(), "LOC_ERR: AndMoreTypingGroup");
                            }
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("AreTypingGroup", org.telegram.messenger.beta.R.string.AreTypingGroup, label.toString()));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 0);
                    }
                    it2 = it4;
                    next = entry;
                    value = concurrentHashMap;
                    it3 = it;
                }
                PrintingUser pu = arr.get(i);
                TLRPC.User user2 = getUser(Long.valueOf(pu.userId));
                if (user2 == null) {
                    it2 = it4;
                    next = entry;
                    value = concurrentHashMap;
                    it3 = it;
                } else {
                    if (pu.action instanceof TLRPC.TL_sendMessageRecordAudioAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsRecordingAudio", org.telegram.messenger.beta.R.string.IsRecordingAudio, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("RecordingAudio", org.telegram.messenger.beta.R.string.RecordingAudio));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 1);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageRecordRoundAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsRecordingRound", org.telegram.messenger.beta.R.string.IsRecordingRound, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("RecordingRound", org.telegram.messenger.beta.R.string.RecordingRound));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 4);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageUploadRoundAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsSendingVideo", org.telegram.messenger.beta.R.string.IsSendingVideo, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("SendingVideoStatus", org.telegram.messenger.beta.R.string.SendingVideoStatus));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 4);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageUploadAudioAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsSendingAudio", org.telegram.messenger.beta.R.string.IsSendingAudio, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("SendingAudio", org.telegram.messenger.beta.R.string.SendingAudio));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 2);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageUploadVideoAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsSendingVideo", org.telegram.messenger.beta.R.string.IsSendingVideo, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("SendingVideoStatus", org.telegram.messenger.beta.R.string.SendingVideoStatus));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 2);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageRecordVideoAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsRecordingVideo", org.telegram.messenger.beta.R.string.IsRecordingVideo, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("RecordingVideoStatus", org.telegram.messenger.beta.R.string.RecordingVideoStatus));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 2);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageUploadDocumentAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsSendingFile", org.telegram.messenger.beta.R.string.IsSendingFile, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("SendingFile", org.telegram.messenger.beta.R.string.SendingFile));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 2);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageUploadPhotoAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsSendingPhoto", org.telegram.messenger.beta.R.string.IsSendingPhoto, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("SendingPhoto", org.telegram.messenger.beta.R.string.SendingPhoto));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 2);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageGamePlayAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsSendingGame", org.telegram.messenger.beta.R.string.IsSendingGame, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("SendingGame", org.telegram.messenger.beta.R.string.SendingGame));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 3);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageGeoLocationAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsSelectingLocation", org.telegram.messenger.beta.R.string.IsSelectingLocation, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("SelectingLocation", org.telegram.messenger.beta.R.string.SelectingLocation));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 0);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageChooseContactAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsSelectingContact", org.telegram.messenger.beta.R.string.IsSelectingContact, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("SelectingContact", org.telegram.messenger.beta.R.string.SelectingContact));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 0);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageEmojiInteractionSeen) {
                        String emoji = ((TLRPC.TL_sendMessageEmojiInteractionSeen) pu.action).emoticon;
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            printingString = LocaleController.formatString("IsEnjoyngAnimations", org.telegram.messenger.beta.R.string.IsEnjoyngAnimations, getUserNameForTyping(user2), emoji);
                        } else {
                            printingString = LocaleController.formatString("EnjoyngAnimations", org.telegram.messenger.beta.R.string.EnjoyngAnimations, emoji);
                        }
                        newPrintingStrings.put(threadId.intValue(), printingString);
                        newPrintingStringsTypes.put(threadId.intValue(), 5);
                    } else if (pu.action instanceof TLRPC.TL_sendMessageChooseStickerAction) {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsChoosingSticker", org.telegram.messenger.beta.R.string.IsChoosingSticker, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("ChoosingSticker", org.telegram.messenger.beta.R.string.ChoosingSticker));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 5);
                    } else {
                        if (key.longValue() < 0 && !isEncryptedChat) {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.formatString("IsTypingGroup", org.telegram.messenger.beta.R.string.IsTypingGroup, getUserNameForTyping(user2)));
                        } else {
                            newPrintingStrings.put(threadId.intValue(), LocaleController.getString("Typing", org.telegram.messenger.beta.R.string.Typing));
                        }
                        newPrintingStringsTypes.put(threadId.intValue(), 0);
                    }
                    it2 = it4;
                    next = entry;
                    value = concurrentHashMap;
                    it3 = it;
                }
            }
        }
        this.lastPrintingStringCount = newStrings.size();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m887x3f8a7e71(newStrings, newTypes);
            }
        });
    }

    /* renamed from: lambda$updatePrintingStrings$140$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m887x3f8a7e71(LongSparseArray newStrings, LongSparseArray newTypes) {
        this.printingStrings = newStrings;
        this.printingStringsTypes = newTypes;
    }

    /* renamed from: cancelTyping */
    public void m846lambda$sendTyping$143$orgtelegrammessengerMessagesController(int action, long dialogId, int threadMsgId) {
        LongSparseArray<SparseArray<Boolean>> dialogs;
        SparseArray<Boolean> threads;
        if (action < 0) {
            return;
        }
        LongSparseArray<SparseArray<Boolean>>[] longSparseArrayArr = this.sendingTypings;
        if (action >= longSparseArrayArr.length || longSparseArrayArr[action] == null || (threads = (dialogs = longSparseArrayArr[action]).get(dialogId)) == null) {
            return;
        }
        threads.remove(threadMsgId);
        if (threads.size() == 0) {
            dialogs.remove(dialogId);
        }
    }

    public boolean sendTyping(long dialogId, int threadMsgId, int action, int classGuid) {
        return sendTyping(dialogId, threadMsgId, action, null, classGuid);
    }

    public boolean sendTyping(final long dialogId, final int threadMsgId, final int action, String emojicon, int classGuid) {
        LongSparseArray<SparseArray<Boolean>> dialogs;
        SparseArray<Boolean> threads;
        TLRPC.Chat chat;
        if (action < 0 || action >= this.sendingTypings.length || dialogId == 0) {
            return false;
        }
        if (dialogId < 0) {
            if (ChatObject.getSendAsPeerId(getChat(Long.valueOf(-dialogId)), getChatFull(-dialogId)) != UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()) {
                return false;
            }
        } else {
            TLRPC.User user = getUser(Long.valueOf(dialogId));
            if (user != null) {
                if (user.id == getUserConfig().getClientUserId()) {
                    return false;
                }
                if (user.status != null && user.status.expires != -100 && !this.onlinePrivacy.containsKey(Long.valueOf(user.id))) {
                    int time = getConnectionsManager().getCurrentTime();
                    if (user.status.expires <= time - 30) {
                        return false;
                    }
                }
            }
        }
        LongSparseArray<SparseArray<Boolean>>[] longSparseArrayArr = this.sendingTypings;
        LongSparseArray<SparseArray<Boolean>> dialogs2 = longSparseArrayArr[action];
        if (dialogs2 != null) {
            dialogs = dialogs2;
        } else {
            LongSparseArray<SparseArray<Boolean>> dialogs3 = new LongSparseArray<>();
            longSparseArrayArr[action] = dialogs3;
            dialogs = dialogs3;
        }
        SparseArray<Boolean> threads2 = dialogs.get(dialogId);
        if (threads2 != null) {
            threads = threads2;
        } else {
            SparseArray<Boolean> threads3 = new SparseArray<>();
            dialogs.put(dialogId, threads3);
            threads = threads3;
        }
        if (threads.get(threadMsgId) != null) {
            return false;
        }
        if (!DialogObject.isEncryptedDialog(dialogId)) {
            TLRPC.TL_messages_setTyping req = new TLRPC.TL_messages_setTyping();
            if (threadMsgId != 0) {
                req.top_msg_id = threadMsgId;
                req.flags |= 1;
            }
            req.peer = getInputPeer(dialogId);
            if (((req.peer instanceof TLRPC.TL_inputPeerChannel) && ((chat = getChat(Long.valueOf(req.peer.channel_id))) == null || !chat.megagroup)) || req.peer == null) {
                return false;
            }
            if (action == 0) {
                req.action = new TLRPC.TL_sendMessageTypingAction();
            } else if (action == 1) {
                req.action = new TLRPC.TL_sendMessageRecordAudioAction();
            } else if (action == 2) {
                req.action = new TLRPC.TL_sendMessageCancelAction();
            } else if (action == 3) {
                req.action = new TLRPC.TL_sendMessageUploadDocumentAction();
            } else if (action == 4) {
                req.action = new TLRPC.TL_sendMessageUploadPhotoAction();
            } else if (action == 5) {
                req.action = new TLRPC.TL_sendMessageUploadVideoAction();
            } else if (action == 6) {
                req.action = new TLRPC.TL_sendMessageGamePlayAction();
            } else if (action == 7) {
                req.action = new TLRPC.TL_sendMessageRecordRoundAction();
            } else if (action == 8) {
                req.action = new TLRPC.TL_sendMessageUploadRoundAction();
            } else if (action == 9) {
                req.action = new TLRPC.TL_sendMessageUploadAudioAction();
            } else if (action == 10) {
                req.action = new TLRPC.TL_sendMessageChooseStickerAction();
            } else if (action == 11) {
                TLRPC.TL_sendMessageEmojiInteractionSeen interactionSeen = new TLRPC.TL_sendMessageEmojiInteractionSeen();
                interactionSeen.emoticon = emojicon;
                req.action = interactionSeen;
            }
            threads.put(threadMsgId, true);
            int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda185
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m845lambda$sendTyping$142$orgtelegrammessengerMessagesController(action, dialogId, threadMsgId, tLObject, tL_error);
                }
            }, 2);
            if (classGuid != 0) {
                getConnectionsManager().bindRequestToGuid(reqId, classGuid);
                return true;
            }
            return true;
        } else if (action != 0) {
            return false;
        } else {
            TLRPC.EncryptedChat chat2 = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)));
            if (chat2.auth_key != null && chat2.auth_key.length > 1 && (chat2 instanceof TLRPC.TL_encryptedChat)) {
                TLRPC.TL_messages_setEncryptedTyping req2 = new TLRPC.TL_messages_setEncryptedTyping();
                req2.peer = new TLRPC.TL_inputEncryptedChat();
                req2.peer.chat_id = chat2.id;
                req2.peer.access_hash = chat2.access_hash;
                req2.typing = true;
                threads.put(threadMsgId, true);
                int reqId2 = getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda186
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m847lambda$sendTyping$144$orgtelegrammessengerMessagesController(action, dialogId, threadMsgId, tLObject, tL_error);
                    }
                }, 2);
                if (classGuid != 0) {
                    getConnectionsManager().bindRequestToGuid(reqId2, classGuid);
                    return true;
                }
                return true;
            }
            return true;
        }
    }

    /* renamed from: lambda$sendTyping$142$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m845lambda$sendTyping$142$orgtelegrammessengerMessagesController(final int action, final long dialogId, final int threadMsgId, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda301
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m844lambda$sendTyping$141$orgtelegrammessengerMessagesController(action, dialogId, threadMsgId);
            }
        });
    }

    /* renamed from: lambda$sendTyping$144$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m847lambda$sendTyping$144$orgtelegrammessengerMessagesController(final int action, final long dialogId, final int threadMsgId, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda302
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m846lambda$sendTyping$143$orgtelegrammessengerMessagesController(action, dialogId, threadMsgId);
            }
        });
    }

    public void removeDeletedMessagesFromArray(long dialogId, ArrayList<TLRPC.Message> messages) {
        int maxDeletedId = this.deletedHistory.get(dialogId, 0);
        if (maxDeletedId == 0) {
            return;
        }
        int a = 0;
        int N = messages.size();
        while (a < N) {
            TLRPC.Message message = messages.get(a);
            if (message.id <= maxDeletedId) {
                messages.remove(a);
                a--;
                N--;
            }
            a++;
        }
    }

    public void loadMessages(long dialogId, long mergeDialogId, boolean loadInfo, int count, int max_id, int offset_date, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, int mode, int threadMessageId, int replyFirstUnread, int loadIndex) {
        loadMessages(dialogId, mergeDialogId, loadInfo, count, max_id, offset_date, fromCache, midDate, classGuid, load_type, last_message_id, mode, threadMessageId, loadIndex, threadMessageId != 0 ? replyFirstUnread : 0, 0, 0, false, 0);
    }

    public void loadMessages(long dialogId, long mergeDialogId, boolean loadInfo, int count, int max_id, int offset_date, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, int mode, int threadMessageId, int loadIndex, int first_unread, int unread_count, int last_date, boolean queryFromServer, int mentionsCount) {
        loadMessagesInternal(dialogId, mergeDialogId, loadInfo, count, max_id, offset_date, fromCache, midDate, classGuid, load_type, last_message_id, mode, threadMessageId, loadIndex, first_unread, unread_count, last_date, queryFromServer, mentionsCount, true, true);
    }

    public void loadMessagesInternal(final long dialogId, final long mergeDialogId, final boolean loadInfo, final int count, final int max_id, final int offset_date, boolean fromCache, final int minDate, final int classGuid, final int load_type, final int last_message_id, final int mode, final int threadMessageId, final int loadIndex, final int first_unread, final int unread_count, final int last_date, final boolean queryFromServer, final int mentionsCount, boolean loadDialog, final boolean processMessages) {
        int i;
        int i2;
        int i3;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load messages in chat " + dialogId + " count " + count + " max_id " + max_id + " cache " + fromCache + " mindate = " + minDate + " guid " + classGuid + " load_type " + load_type + " last_message_id " + last_message_id + " mode " + mode + " index " + loadIndex + " firstUnread " + first_unread + " unread_count " + unread_count + " last_date " + last_date + " queryFromServer " + queryFromServer);
        }
        if (threadMessageId == 0 && mode != 2 && (fromCache || DialogObject.isEncryptedDialog(dialogId))) {
            getMessagesStorage().getMessages(dialogId, mergeDialogId, loadInfo, count, max_id, offset_date, minDate, classGuid, load_type, mode == 1, threadMessageId, loadIndex, processMessages);
            return;
        }
        if (threadMessageId != 0) {
            if (mode != 0) {
                return;
            }
            final TLRPC.TL_messages_getReplies req = new TLRPC.TL_messages_getReplies();
            req.peer = getInputPeer(dialogId);
            req.msg_id = threadMessageId;
            req.offset_date = offset_date;
            if (load_type == 4) {
                req.add_offset = (-count) + 5;
            } else if (load_type == 3) {
                req.add_offset = (-count) / 2;
            } else if (load_type == 1) {
                req.add_offset = (-count) - 1;
            } else if (load_type == 2 && max_id != 0) {
                req.add_offset = (-count) + 10;
            } else if (dialogId < 0 && max_id != 0) {
                TLRPC.Chat chat = getChat(Long.valueOf(-dialogId));
                if (ChatObject.isChannel(chat)) {
                    req.add_offset = -1;
                    req.limit++;
                }
            }
            req.limit = count;
            req.offset_id = max_id;
            int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda182
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m722x63f06a9c(count, max_id, offset_date, first_unread, load_type, dialogId, mergeDialogId, classGuid, last_message_id, unread_count, last_date, threadMessageId, loadIndex, queryFromServer, mentionsCount, processMessages, req, tLObject, tL_error);
                }
            });
            i = classGuid;
            getConnectionsManager().bindRequestToGuid(reqId, i);
        } else {
            i = classGuid;
            if (mode != 2) {
                if (mode == 1) {
                    TLRPC.TL_messages_getScheduledHistory req2 = new TLRPC.TL_messages_getScheduledHistory();
                    req2.peer = getInputPeer(dialogId);
                    req2.hash = minDate;
                    int reqId2 = getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda183
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.m723x4931d95d(max_id, offset_date, dialogId, mergeDialogId, count, classGuid, first_unread, last_message_id, unread_count, last_date, load_type, mode, threadMessageId, loadIndex, queryFromServer, mentionsCount, processMessages, tLObject, tL_error);
                        }
                    });
                    getConnectionsManager().bindRequestToGuid(reqId2, classGuid);
                } else if (loadDialog && ((load_type == 3 || load_type == 2) && last_message_id == 0)) {
                    final TLRPC.TL_messages_getPeerDialogs req3 = new TLRPC.TL_messages_getPeerDialogs();
                    TLRPC.InputPeer inputPeer = getInputPeer(dialogId);
                    TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                    inputDialogPeer.peer = inputPeer;
                    req3.peers.add(inputDialogPeer);
                    getConnectionsManager().sendRequest(req3, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda216
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.m725x13b4b6df(dialogId, mergeDialogId, loadInfo, count, max_id, offset_date, minDate, classGuid, load_type, threadMessageId, loadIndex, first_unread, last_date, queryFromServer, processMessages, req3, tLObject, tL_error);
                        }
                    });
                } else {
                    final TLRPC.TL_messages_getHistory req4 = new TLRPC.TL_messages_getHistory();
                    req4.peer = getInputPeer(dialogId);
                    if (load_type == 4) {
                        i2 = count;
                        req4.add_offset = (-i2) + 5;
                        i3 = max_id;
                    } else {
                        i2 = count;
                        if (load_type != 3) {
                            if (load_type == 1) {
                                req4.add_offset = (-i2) - 1;
                                i3 = max_id;
                            } else {
                                if (load_type == 2) {
                                    i3 = max_id;
                                    if (i3 != 0) {
                                        req4.add_offset = (-i2) + 6;
                                    }
                                } else {
                                    i3 = max_id;
                                }
                                if (dialogId < 0 && i3 != 0) {
                                    TLRPC.Chat chat2 = getChat(Long.valueOf(-dialogId));
                                    if (ChatObject.isChannel(chat2)) {
                                        req4.add_offset = -1;
                                        req4.limit++;
                                    }
                                }
                            }
                        } else {
                            req4.add_offset = (-i2) / 2;
                            i3 = max_id;
                        }
                    }
                    req4.limit = i2;
                    req4.offset_id = i3;
                    req4.offset_date = offset_date;
                    int reqId3 = getConnectionsManager().sendRequest(req4, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda209
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.m727xac95aa36(dialogId, count, max_id, offset_date, mergeDialogId, classGuid, first_unread, last_message_id, unread_count, last_date, load_type, threadMessageId, loadIndex, queryFromServer, mentionsCount, processMessages, req4, tLObject, tL_error);
                        }
                    });
                    getConnectionsManager().bindRequestToGuid(reqId3, classGuid);
                }
            }
        }
    }

    /* renamed from: lambda$loadMessagesInternal$146$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m722x63f06a9c(int count, int max_id, int offset_date, int first_unread, int load_type, long dialogId, long mergeDialogId, final int classGuid, int last_message_id, int unread_count, int last_date, int threadMessageId, int loadIndex, boolean queryFromServer, int mentionsCount, boolean processMessages, final TLRPC.TL_messages_getReplies req, TLObject response, final TLRPC.TL_error error) {
        int fnid;
        int mid;
        if (response != null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            if (res.messages.size() > count) {
                res.messages.remove(0);
            }
            if (!res.messages.isEmpty()) {
                if (offset_date != 0) {
                    int mid2 = res.messages.get(res.messages.size() - 1).id;
                    int a = res.messages.size() - 1;
                    while (true) {
                        if (a < 0) {
                            break;
                        }
                        TLRPC.Message message = res.messages.get(a);
                        if (message.date > offset_date) {
                            mid2 = message.id;
                            break;
                        }
                        a--;
                    }
                    mid = mid2;
                    fnid = 0;
                } else if (first_unread != 0 && load_type == 2 && max_id > 0) {
                    for (int a2 = res.messages.size() - 1; a2 >= 0; a2--) {
                        TLRPC.Message message2 = res.messages.get(a2);
                        if (message2.id > first_unread && !message2.out) {
                            int fnid2 = message2.id;
                            mid = max_id;
                            fnid = fnid2;
                            break;
                        }
                    }
                }
                processLoadedMessages(res, res.messages.size(), dialogId, mergeDialogId, count, mid, offset_date, false, classGuid, fnid, last_message_id, unread_count, last_date, load_type, false, 0, threadMessageId, loadIndex, queryFromServer, mentionsCount, processMessages);
                return;
            }
            mid = max_id;
            fnid = 0;
            processLoadedMessages(res, res.messages.size(), dialogId, mergeDialogId, count, mid, offset_date, false, classGuid, fnid, last_message_id, unread_count, last_date, load_type, false, 0, threadMessageId, loadIndex, queryFromServer, mentionsCount, processMessages);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda311
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m721x7eaefbdb(classGuid, req, error);
            }
        });
    }

    /* renamed from: lambda$loadMessagesInternal$145$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m721x7eaefbdb(int classGuid, TLRPC.TL_messages_getReplies req, TLRPC.TL_error error) {
        getNotificationCenter().postNotificationName(NotificationCenter.loadingMessagesFailed, Integer.valueOf(classGuid), req, error);
    }

    /* renamed from: lambda$loadMessagesInternal$147$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m723x4931d95d(int max_id, int offset_date, long dialogId, long mergeDialogId, int count, int classGuid, int first_unread, int last_message_id, int unread_count, int last_date, int load_type, int mode, int threadMessageId, int loadIndex, boolean queryFromServer, int mentionsCount, boolean processMessages, TLObject response, TLRPC.TL_error error) {
        int mid;
        if (response != null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            if (res instanceof TLRPC.TL_messages_messagesNotModified) {
                return;
            }
            if (offset_date != 0 && !res.messages.isEmpty()) {
                int mid2 = res.messages.get(res.messages.size() - 1).id;
                int a = res.messages.size() - 1;
                while (true) {
                    if (a < 0) {
                        mid = mid2;
                        break;
                    }
                    TLRPC.Message message = res.messages.get(a);
                    if (message.date <= offset_date) {
                        a--;
                    } else {
                        int mid3 = message.id;
                        mid = mid3;
                        break;
                    }
                }
            } else {
                mid = max_id;
            }
            processLoadedMessages(res, res.messages.size(), dialogId, mergeDialogId, count, mid, offset_date, false, classGuid, first_unread, last_message_id, unread_count, last_date, load_type, false, mode, threadMessageId, loadIndex, queryFromServer, mentionsCount, processMessages);
        }
    }

    /* renamed from: lambda$loadMessagesInternal$149$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m725x13b4b6df(long dialogId, long mergeDialogId, boolean loadInfo, int count, int max_id, int offset_date, int minDate, final int classGuid, int load_type, int threadMessageId, int loadIndex, int first_unread, int last_date, boolean queryFromServer, boolean processMessages, final TLRPC.TL_messages_getPeerDialogs req, TLObject response, final TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_messages_peerDialogs res = (TLRPC.TL_messages_peerDialogs) response;
            if (!res.dialogs.isEmpty()) {
                TLRPC.Dialog dialog = res.dialogs.get(0);
                if (dialog.top_message != 0) {
                    TLRPC.TL_messages_dialogs dialogs = new TLRPC.TL_messages_dialogs();
                    dialogs.chats = res.chats;
                    dialogs.users = res.users;
                    dialogs.dialogs = res.dialogs;
                    dialogs.messages = res.messages;
                    getMessagesStorage().putDialogs(dialogs, 2);
                }
                loadMessagesInternal(dialogId, mergeDialogId, loadInfo, count, max_id, offset_date, false, minDate, classGuid, load_type, dialog.top_message, 0, threadMessageId, loadIndex, first_unread, dialog.unread_count, last_date, queryFromServer, dialog.unread_mentions_count, false, processMessages);
            }
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda310
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m724x2e73481e(classGuid, req, error);
            }
        });
    }

    /* renamed from: lambda$loadMessagesInternal$148$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m724x2e73481e(int classGuid, TLRPC.TL_messages_getPeerDialogs req, TLRPC.TL_error error) {
        getNotificationCenter().postNotificationName(NotificationCenter.loadingMessagesFailed, Integer.valueOf(classGuid), req, error);
    }

    /* renamed from: lambda$loadMessagesInternal$151$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m727xac95aa36(long dialogId, int count, int max_id, int offset_date, long mergeDialogId, final int classGuid, int first_unread, int last_message_id, int unread_count, int last_date, int load_type, int threadMessageId, int loadIndex, boolean queryFromServer, int mentionsCount, boolean processMessages, final TLRPC.TL_messages_getHistory req, TLObject response, final TLRPC.TL_error error) {
        int mid;
        if (response != null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            removeDeletedMessagesFromArray(dialogId, res.messages);
            if (res.messages.size() > count) {
                res.messages.remove(0);
            }
            if (offset_date != 0 && !res.messages.isEmpty()) {
                int mid2 = res.messages.get(res.messages.size() - 1).id;
                int a = res.messages.size() - 1;
                while (true) {
                    if (a < 0) {
                        mid = mid2;
                        break;
                    }
                    TLRPC.Message message = res.messages.get(a);
                    if (message.date <= offset_date) {
                        a--;
                    } else {
                        int mid3 = message.id;
                        mid = mid3;
                        break;
                    }
                }
            } else {
                mid = max_id;
            }
            processLoadedMessages(res, res.messages.size(), dialogId, mergeDialogId, count, mid, offset_date, false, classGuid, first_unread, last_message_id, unread_count, last_date, load_type, false, 0, threadMessageId, loadIndex, queryFromServer, mentionsCount, processMessages);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda309
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m726xc7543b75(classGuid, req, error);
            }
        });
    }

    /* renamed from: lambda$loadMessagesInternal$150$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m726xc7543b75(int classGuid, TLRPC.TL_messages_getHistory req, TLRPC.TL_error error) {
        getNotificationCenter().postNotificationName(NotificationCenter.loadingMessagesFailed, Integer.valueOf(classGuid), req, error);
    }

    public void reloadWebPages(final long dialogId, HashMap<String, ArrayList<MessageObject>> webpagesToReload, final boolean scheduled) {
        HashMap<String, ArrayList<MessageObject>> map = scheduled ? this.reloadingScheduledWebpages : this.reloadingWebpages;
        final LongSparseArray<ArrayList<MessageObject>> array = scheduled ? this.reloadingScheduledWebpagesPending : this.reloadingWebpagesPending;
        for (Map.Entry<String, ArrayList<MessageObject>> entry : webpagesToReload.entrySet()) {
            final String url = entry.getKey();
            ArrayList<MessageObject> messages = entry.getValue();
            ArrayList<MessageObject> arrayList = map.get(url);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                map.put(url, arrayList);
            }
            arrayList.addAll(messages);
            TLRPC.TL_messages_getWebPagePreview req = new TLRPC.TL_messages_getWebPagePreview();
            req.message = url;
            final HashMap<String, ArrayList<MessageObject>> hashMap = map;
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda242
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m832x16584452(hashMap, url, array, dialogId, scheduled, tLObject, tL_error);
                }
            });
            map = map;
        }
    }

    /* renamed from: lambda$reloadWebPages$153$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m832x16584452(final HashMap map, final String url, final LongSparseArray array, final long dialogId, final boolean scheduled, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m831x3116d591(map, url, response, array, dialogId, scheduled);
            }
        });
    }

    /* renamed from: lambda$reloadWebPages$152$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m831x3116d591(HashMap map, String url, TLObject response, LongSparseArray array, long dialogId, boolean scheduled) {
        ArrayList<MessageObject> arrayList1 = (ArrayList) map.remove(url);
        if (arrayList1 == null) {
            return;
        }
        TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
        if (!(response instanceof TLRPC.TL_messageMediaWebPage)) {
            for (int a = 0; a < arrayList1.size(); a++) {
                arrayList1.get(a).messageOwner.media.webpage = new TLRPC.TL_webPageEmpty();
                messagesRes.messages.add(arrayList1.get(a).messageOwner);
            }
        } else {
            TLRPC.TL_messageMediaWebPage media = (TLRPC.TL_messageMediaWebPage) response;
            if (!(media.webpage instanceof TLRPC.TL_webPage) && !(media.webpage instanceof TLRPC.TL_webPageEmpty)) {
                array.put(media.webpage.id, arrayList1);
            }
            for (int a2 = 0; a2 < arrayList1.size(); a2++) {
                arrayList1.get(a2).messageOwner.media.webpage = media.webpage;
                if (a2 == 0) {
                    ImageLoader.saveMessageThumbs(arrayList1.get(a2).messageOwner);
                }
                messagesRes.messages.add(arrayList1.get(a2).messageOwner);
            }
        }
        if (!messagesRes.messages.isEmpty()) {
            getMessagesStorage().putMessages((TLRPC.messages_Messages) messagesRes, dialogId, -2, 0, false, scheduled);
            getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialogId), arrayList1);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:157:0x0427, code lost:
        if (r2.media.bytes[0] >= 143) goto L159;
     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x043c, code lost:
        if (org.telegram.messenger.Utilities.bytesToInt(r2.media.bytes) < 143) goto L164;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void processLoadedMessages(final org.telegram.tgnet.TLRPC.messages_Messages r40, final int r41, final long r42, final long r44, final int r46, final int r47, final int r48, final boolean r49, final int r50, final int r51, final int r52, final int r53, final int r54, final int r55, final boolean r56, final int r57, final int r58, final int r59, final boolean r60, final int r61, final boolean r62) {
        /*
            Method dump skipped, instructions count: 1331
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processLoadedMessages(org.telegram.tgnet.TLRPC$messages_Messages, int, long, long, int, int, int, boolean, int, int, int, int, int, int, boolean, int, int, int, boolean, int, boolean):void");
    }

    /* renamed from: lambda$processLoadedMessages$154$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m786x84cc7bf0(long dialogId, long mergeDialogId, int count, int load_type, boolean queryFromServer, int first_unread, int max_id, int offset_date, int hash, int classGuid, int last_message_id, int mode, int threadMessageId, int loadIndex, int unread_count, int last_date, int mentionsCount, boolean needProcess) {
        loadMessagesInternal(dialogId, mergeDialogId, false, count, (load_type != 2 || !queryFromServer) ? max_id : first_unread, offset_date, false, hash, classGuid, load_type, last_message_id, mode, threadMessageId, loadIndex, first_unread, unread_count, last_date, queryFromServer, mentionsCount, true, needProcess);
    }

    /* renamed from: lambda$processLoadedMessages$155$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m787x6a0deab1(int classGuid, TLRPC.messages_Messages messagesRes, boolean isCache, boolean isEnd, int last_message_id) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoadWithoutProcess, Integer.valueOf(classGuid), Integer.valueOf(messagesRes.messages.size()), Boolean.valueOf(isCache), Boolean.valueOf(isEnd), Integer.valueOf(last_message_id));
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0066  */
    /* renamed from: lambda$processLoadedMessages$157$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m789x3490c833(org.telegram.tgnet.TLRPC.messages_Messages r26, final boolean r27, final int r28, boolean r29, final int r30, int r31, final int r32, final long r33, final java.util.ArrayList r35, final boolean r36, final int r37, final int r38, final boolean r39, final int r40, final int r41, final int r42, final int r43, final int r44, final int r45, java.util.ArrayList r46, java.util.HashMap r47) {
        /*
            Method dump skipped, instructions count: 387
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m789x3490c833(org.telegram.tgnet.TLRPC$messages_Messages, boolean, int, boolean, int, int, int, long, java.util.ArrayList, boolean, int, int, boolean, int, int, int, int, int, int, java.util.ArrayList, java.util.HashMap):void");
    }

    /* renamed from: lambda$processLoadedMessages$156$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m788x4f4f5972(boolean needProcess, int classGuid, int resCount, boolean isCache, boolean isEnd, int last_message_id, long dialogId, int count, ArrayList objects, int finalFirst_unread_final, int unread_count, int last_date, int load_type, int loadIndex, int max_id, int mentionsCount, int mode) {
        if (!needProcess) {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoadWithoutProcess, Integer.valueOf(classGuid), Integer.valueOf(resCount), Boolean.valueOf(isCache), Boolean.valueOf(isEnd), Integer.valueOf(last_message_id));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesDidLoad, Long.valueOf(dialogId), Integer.valueOf(count), objects, Boolean.valueOf(isCache), Integer.valueOf(finalFirst_unread_final), Integer.valueOf(last_message_id), Integer.valueOf(unread_count), Integer.valueOf(last_date), Integer.valueOf(load_type), Boolean.valueOf(isEnd), Integer.valueOf(classGuid), Integer.valueOf(loadIndex), Integer.valueOf(max_id), Integer.valueOf(mentionsCount), Integer.valueOf(mode));
        }
    }

    public void loadHintDialogs() {
        if (!this.hintDialogs.isEmpty() || TextUtils.isEmpty(this.installReferer)) {
            return;
        }
        TLRPC.TL_help_getRecentMeUrls req = new TLRPC.TL_help_getRecentMeUrls();
        req.referer = this.installReferer;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda156
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m720x67466193(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadHintDialogs$159$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m720x67466193(final TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m719x8204f2d2(response);
                }
            });
        }
    }

    /* renamed from: lambda$loadHintDialogs$158$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m719x8204f2d2(TLObject response) {
        TLRPC.TL_help_recentMeUrls res = (TLRPC.TL_help_recentMeUrls) response;
        putUsers(res.users, false);
        putChats(res.chats, false);
        this.hintDialogs.clear();
        this.hintDialogs.addAll(res.urls);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private TLRPC.TL_dialogFolder ensureFolderDialogExists(int folderId, boolean[] folderCreated) {
        if (folderId == 0) {
            return null;
        }
        long folderDialogId = DialogObject.makeFolderDialogId(folderId);
        TLRPC.Dialog dialog = this.dialogs_dict.get(folderDialogId);
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            if (folderCreated != null) {
                folderCreated[0] = false;
            }
            return (TLRPC.TL_dialogFolder) dialog;
        }
        if (folderCreated != null) {
            folderCreated[0] = true;
        }
        TLRPC.TL_dialogFolder dialogFolder = new TLRPC.TL_dialogFolder();
        dialogFolder.id = folderDialogId;
        dialogFolder.peer = new TLRPC.TL_peerUser();
        dialogFolder.folder = new TLRPC.TL_folder();
        dialogFolder.folder.id = folderId;
        dialogFolder.folder.title = LocaleController.getString("ArchivedChats", org.telegram.messenger.beta.R.string.ArchivedChats);
        dialogFolder.pinned = true;
        int maxPinnedNum = 0;
        for (int a = 0; a < this.allDialogs.size(); a++) {
            TLRPC.Dialog d = this.allDialogs.get(a);
            if (!d.pinned) {
                if (d.id != this.promoDialogId) {
                    break;
                }
            } else {
                maxPinnedNum = Math.max(d.pinnedNum, maxPinnedNum);
            }
        }
        int a2 = maxPinnedNum + 1;
        dialogFolder.pinnedNum = a2;
        TLRPC.TL_messages_dialogs dialogs = new TLRPC.TL_messages_dialogs();
        dialogs.dialogs.add(dialogFolder);
        getMessagesStorage().putDialogs(dialogs, 1);
        this.dialogs_dict.put(folderDialogId, dialogFolder);
        this.allDialogs.add(0, dialogFolder);
        return dialogFolder;
    }

    /* renamed from: removeFolder */
    public void m765x10ed6eab(int folderId) {
        long dialogId = DialogObject.makeFolderDialogId(folderId);
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog == null) {
            return;
        }
        this.dialogs_dict.remove(dialogId);
        this.allDialogs.remove(dialog);
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.folderBecomeEmpty, Integer.valueOf(folderId));
    }

    public void onFolderEmpty(final int folderId) {
        long[] dialogsLoadOffset = getUserConfig().getDialogLoadOffsets(folderId);
        if (dialogsLoadOffset[0] == 2147483647L) {
            m765x10ed6eab(folderId);
        } else {
            loadDialogs(folderId, 0, 10, false, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda296
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m765x10ed6eab(folderId);
                }
            });
        }
    }

    public void checkIfFolderEmpty(int folderId) {
        if (folderId == 0) {
            return;
        }
        getMessagesStorage().checkIfFolderEmpty(folderId);
    }

    public int addDialogToFolder(long dialogId, int folderId, int pinnedNum, long taskId) {
        ArrayList<Long> arrayList = new ArrayList<>(1);
        arrayList.add(Long.valueOf(dialogId));
        return addDialogToFolder(arrayList, folderId, pinnedNum, null, taskId);
    }

    public int addDialogToFolder(ArrayList<Long> dialogIds, int folderId, int pinnedNum, ArrayList<TLRPC.TL_inputFolderPeer> peers, long taskId) {
        final long newTaskId;
        long newTaskId2;
        TLRPC.Dialog dialog;
        boolean[] folderCreated;
        TLRPC.TL_folders_editPeerFolders req = new TLRPC.TL_folders_editPeerFolders();
        boolean[] folderCreated2 = null;
        if (taskId == 0) {
            long selfUserId = getUserConfig().getClientUserId();
            int N = dialogIds.size();
            int size = 0;
            int size2 = 0;
            boolean[] folderCreated3 = null;
            for (int a = 0; a < N; a++) {
                long dialogId = dialogIds.get(a).longValue();
                if ((DialogObject.isChatDialog(dialogId) || DialogObject.isUserDialog(dialogId) || DialogObject.isEncryptedDialog(dialogId)) && ((folderId != 1 || (dialogId != selfUserId && dialogId != 777000 && !isPromoDialog(dialogId, false))) && (dialog = this.dialogs_dict.get(dialogId)) != null)) {
                    dialog.folder_id = folderId;
                    if (pinnedNum > 0) {
                        dialog.pinned = true;
                        dialog.pinnedNum = pinnedNum;
                    } else {
                        dialog.pinned = false;
                        dialog.pinnedNum = 0;
                    }
                    if (folderCreated3 != null) {
                        folderCreated = folderCreated3;
                    } else {
                        boolean[] folderCreated4 = new boolean[1];
                        ensureFolderDialogExists(folderId, folderCreated4);
                        folderCreated = folderCreated4;
                    }
                    if (DialogObject.isEncryptedDialog(dialogId)) {
                        getMessagesStorage().setDialogsFolderId(null, null, dialogId, folderId);
                        size2 = 1;
                        folderCreated3 = folderCreated;
                    } else {
                        TLRPC.TL_inputFolderPeer folderPeer = new TLRPC.TL_inputFolderPeer();
                        folderPeer.folder_id = folderId;
                        folderPeer.peer = getInputPeer(dialogId);
                        req.folder_peers.add(folderPeer);
                        size += folderPeer.getObjectSize();
                        size2 = 1;
                        folderCreated3 = folderCreated;
                    }
                }
            }
            if (size2 == 0) {
                return 0;
            }
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            if (size != 0) {
                NativeByteBuffer data = null;
                try {
                    data = new NativeByteBuffer(size + 12);
                    data.writeInt32(17);
                    data.writeInt32(folderId);
                    data.writeInt32(req.folder_peers.size());
                    int N2 = req.folder_peers.size();
                    for (int a2 = 0; a2 < N2; a2++) {
                        req.folder_peers.get(a2).serializeToStream(data);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                newTaskId2 = getMessagesStorage().createPendingTask(data);
            } else {
                newTaskId2 = 0;
            }
            folderCreated2 = folderCreated3;
            newTaskId = newTaskId2;
        } else {
            req.folder_peers = peers;
            newTaskId = taskId;
        }
        if (!req.folder_peers.isEmpty()) {
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda191
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m576x8aa7ed7e(newTaskId, tLObject, tL_error);
                }
            });
            getMessagesStorage().setDialogsFolderId(null, req.folder_peers, 0L, folderId);
        }
        if (folderCreated2 == null) {
            return 0;
        }
        return folderCreated2[0] ? 2 : 1;
    }

    /* renamed from: lambda$addDialogToFolder$161$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m576x8aa7ed7e(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            processUpdates((TLRPC.Updates) response, false);
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void loadDialogs(int folderId, int offset, int count, boolean fromCache) {
        loadDialogs(folderId, offset, count, fromCache, null);
    }

    public void loadDialogs(final int folderId, int offset, final int count, boolean fromCache, final Runnable onEmptyCallback) {
        MessageObject message;
        long id;
        if (!this.loadingDialogs.get(folderId) && !this.resetingDialogs) {
            boolean z = true;
            this.loadingDialogs.put(folderId, true);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("folderId = " + folderId + " load cacheOffset = " + offset + " count = " + count + " cache = " + fromCache);
            }
            if (fromCache) {
                MessagesStorage messagesStorage = getMessagesStorage();
                int i = offset == 0 ? 0 : this.nextDialogsCacheOffset.get(folderId, 0);
                if (folderId != 0 || offset != 0) {
                    z = false;
                }
                messagesStorage.getDialogs(folderId, i, count, z);
                return;
            }
            TLRPC.TL_messages_getDialogs req = new TLRPC.TL_messages_getDialogs();
            req.limit = count;
            req.exclude_pinned = true;
            if (folderId != 0) {
                req.flags |= 2;
                req.folder_id = folderId;
            }
            long[] dialogsLoadOffset = getUserConfig().getDialogLoadOffsets(folderId);
            if (dialogsLoadOffset[0] != -1) {
                if (dialogsLoadOffset[0] == 2147483647L) {
                    this.dialogsEndReached.put(folderId, true);
                    this.serverDialogsEndReached.put(folderId, true);
                    this.loadingDialogs.put(folderId, false);
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    return;
                }
                req.offset_id = (int) dialogsLoadOffset[0];
                req.offset_date = (int) dialogsLoadOffset[1];
                if (req.offset_id == 0) {
                    req.offset_peer = new TLRPC.TL_inputPeerEmpty();
                } else {
                    if (dialogsLoadOffset[4] != 0) {
                        req.offset_peer = new TLRPC.TL_inputPeerChannel();
                        req.offset_peer.channel_id = dialogsLoadOffset[4];
                    } else if (dialogsLoadOffset[2] != 0) {
                        req.offset_peer = new TLRPC.TL_inputPeerUser();
                        req.offset_peer.user_id = dialogsLoadOffset[2];
                    } else {
                        req.offset_peer = new TLRPC.TL_inputPeerChat();
                        req.offset_peer.chat_id = dialogsLoadOffset[3];
                    }
                    req.offset_peer.access_hash = dialogsLoadOffset[5];
                }
            } else {
                boolean found = false;
                ArrayList<TLRPC.Dialog> dialogs = getDialogs(folderId);
                int a = dialogs.size() - 1;
                while (true) {
                    if (a < 0) {
                        break;
                    }
                    TLRPC.Dialog dialog = dialogs.get(a);
                    if (dialog.pinned || DialogObject.isEncryptedDialog(dialog.id) || dialog.top_message <= 0 || (message = this.dialogMessage.get(dialog.id)) == null || message.getId() <= 0) {
                        a--;
                    } else {
                        req.offset_date = message.messageOwner.date;
                        req.offset_id = message.messageOwner.id;
                        if (message.messageOwner.peer_id.channel_id != 0) {
                            id = -message.messageOwner.peer_id.channel_id;
                        } else if (message.messageOwner.peer_id.chat_id != 0) {
                            id = -message.messageOwner.peer_id.chat_id;
                        } else {
                            id = message.messageOwner.peer_id.user_id;
                        }
                        req.offset_peer = getInputPeer(id);
                        found = true;
                    }
                }
                if (!found) {
                    req.offset_peer = new TLRPC.TL_inputPeerEmpty();
                }
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda184
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m709lambda$loadDialogs$162$orgtelegrammessengerMessagesController(folderId, count, onEmptyCallback, tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$loadDialogs$162$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m709lambda$loadDialogs$162$orgtelegrammessengerMessagesController(int folderId, int count, Runnable onEmptyCallback, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Dialogs dialogsRes = (TLRPC.messages_Dialogs) response;
            processLoadedDialogs(dialogsRes, null, folderId, 0, count, 0, false, false, false);
            if (onEmptyCallback != null && dialogsRes.dialogs.isEmpty()) {
                AndroidUtilities.runOnUIThread(onEmptyCallback);
            }
        }
    }

    public void loadGlobalNotificationsSettings() {
        if (this.loadingNotificationSettings == 0 && !getUserConfig().notificationsSettingsLoaded) {
            SharedPreferences preferences = getNotificationsSettings(this.currentAccount);
            SharedPreferences.Editor editor1 = null;
            if (preferences.contains("EnableGroup")) {
                boolean enabled = preferences.getBoolean("EnableGroup", true);
                editor1 = preferences.edit();
                if (!enabled) {
                    editor1.putInt("EnableGroup2", Integer.MAX_VALUE);
                    editor1.putInt("EnableChannel2", Integer.MAX_VALUE);
                }
                editor1.remove("EnableGroup").commit();
            }
            if (preferences.contains("EnableAll")) {
                boolean enabled2 = preferences.getBoolean("EnableAll", true);
                if (editor1 == null) {
                    editor1 = preferences.edit();
                }
                if (!enabled2) {
                    editor1.putInt("EnableAll2", Integer.MAX_VALUE);
                }
                editor1.remove("EnableAll").commit();
            }
            if (editor1 != null) {
                editor1.commit();
            }
            this.loadingNotificationSettings = 3;
            for (int a = 0; a < 3; a++) {
                TLRPC.TL_account_getNotifySettings req = new TLRPC.TL_account_getNotifySettings();
                if (a == 0) {
                    req.peer = new TLRPC.TL_inputNotifyChats();
                } else if (a == 1) {
                    req.peer = new TLRPC.TL_inputNotifyUsers();
                } else {
                    req.peer = new TLRPC.TL_inputNotifyBroadcasts();
                }
                final int type = a;
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda175
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m718xe05885d1(type, tLObject, tL_error);
                    }
                });
            }
        }
        if (!getUserConfig().notificationsSignUpSettingsLoaded) {
            loadSignUpNotificationsSettings();
        }
    }

    /* renamed from: lambda$loadGlobalNotificationsSettings$164$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m718xe05885d1(final int type, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m717xfb171710(response, type);
            }
        });
    }

    /* renamed from: lambda$loadGlobalNotificationsSettings$163$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m717xfb171710(TLObject response, int type) {
        if (response != null) {
            this.loadingNotificationSettings--;
            TLRPC.TL_peerNotifySettings notify_settings = (TLRPC.TL_peerNotifySettings) response;
            SharedPreferences.Editor editor = this.notificationsPreferences.edit();
            if (type == 0) {
                if ((notify_settings.flags & 1) != 0) {
                    editor.putBoolean("EnablePreviewGroup", notify_settings.show_previews);
                }
                int i = notify_settings.flags;
                if ((notify_settings.flags & 4) != 0) {
                    editor.putInt("EnableGroup2", notify_settings.mute_until);
                }
            } else if (type == 1) {
                if ((notify_settings.flags & 1) != 0) {
                    editor.putBoolean("EnablePreviewAll", notify_settings.show_previews);
                }
                int i2 = notify_settings.flags;
                if ((notify_settings.flags & 4) != 0) {
                    editor.putInt("EnableAll2", notify_settings.mute_until);
                }
            } else {
                if ((notify_settings.flags & 1) != 0) {
                    editor.putBoolean("EnablePreviewChannel", notify_settings.show_previews);
                }
                int i3 = notify_settings.flags;
                if ((notify_settings.flags & 4) != 0) {
                    editor.putInt("EnableChannel2", notify_settings.mute_until);
                }
            }
            applySoundSettings(notify_settings.android_sound, editor, 0L, type, false);
            editor.commit();
            if (this.loadingNotificationSettings == 0) {
                getUserConfig().notificationsSettingsLoaded = true;
                getUserConfig().saveConfig(false);
            }
        }
    }

    public void loadSignUpNotificationsSettings() {
        if (!this.loadingNotificationSignUpSettings) {
            this.loadingNotificationSignUpSettings = true;
            TLRPC.TL_account_getContactSignUpNotification req = new TLRPC.TL_account_getContactSignUpNotification();
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda159
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m737x2b963d9e(tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$loadSignUpNotificationsSettings$166$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m737x2b963d9e(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m736x4654cedd(response);
            }
        });
    }

    /* renamed from: lambda$loadSignUpNotificationsSettings$165$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m736x4654cedd(TLObject response) {
        this.loadingNotificationSignUpSettings = false;
        SharedPreferences.Editor editor = this.notificationsPreferences.edit();
        boolean z = response instanceof TLRPC.TL_boolFalse;
        this.enableJoined = z;
        editor.putBoolean("EnableContactJoined", z);
        editor.commit();
        getUserConfig().notificationsSignUpSettingsLoaded = true;
        getUserConfig().saveConfig(false);
    }

    public void forceResetDialogs() {
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        getNotificationsController().deleteAllNotificationChannels();
    }

    public void loadUnknownDialog(TLRPC.InputPeer peer, long taskId) {
        long newTaskId;
        if (peer == null) {
            return;
        }
        final long dialogId = DialogObject.getPeerDialogId(peer);
        if (this.gettingUnknownDialogs.indexOfKey(dialogId) >= 0) {
            return;
        }
        this.gettingUnknownDialogs.put(dialogId, true);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load unknown dialog " + dialogId);
        }
        TLRPC.TL_messages_getPeerDialogs req = new TLRPC.TL_messages_getPeerDialogs();
        TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        inputDialogPeer.peer = peer;
        req.peers.add(inputDialogPeer);
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(peer.getObjectSize() + 4);
                data.writeInt32(15);
                peer.serializeToStream(data);
            } catch (Exception e) {
                FileLog.e(e);
            }
            long newTaskId2 = getMessagesStorage().createPendingTask(data);
            newTaskId = newTaskId2;
        } else {
            newTaskId = taskId;
        }
        final long j = newTaskId;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda214
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m741x3212183e(j, dialogId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadUnknownDialog$167$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m741x3212183e(long newTaskId, long dialogId, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_messages_peerDialogs res = (TLRPC.TL_messages_peerDialogs) response;
            if (!res.dialogs.isEmpty()) {
                TLRPC.TL_dialog dialog = (TLRPC.TL_dialog) res.dialogs.get(0);
                TLRPC.TL_messages_dialogs dialogs = new TLRPC.TL_messages_dialogs();
                dialogs.dialogs.addAll(res.dialogs);
                dialogs.messages.addAll(res.messages);
                dialogs.users.addAll(res.users);
                dialogs.chats.addAll(res.chats);
                processLoadedDialogs(dialogs, null, dialog.folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_UNKNOWN, false, false, false);
            }
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
        this.gettingUnknownDialogs.delete(dialogId);
    }

    private void fetchFolderInLoadedPinnedDialogs(TLRPC.TL_messages_peerDialogs res) {
        int N;
        TLRPC.InputPeer inputPeer;
        int a = 0;
        int N2 = res.dialogs.size();
        while (a < N2) {
            TLRPC.Dialog dialog = res.dialogs.get(a);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                N = N2;
            } else {
                TLRPC.TL_dialogFolder dialogFolder = (TLRPC.TL_dialogFolder) dialog;
                long folderTopDialogId = DialogObject.getPeerDialogId(dialog.peer);
                if (dialogFolder.top_message != 0) {
                    long folderTopDialogId2 = 0;
                    if (folderTopDialogId != 0) {
                        int b = 0;
                        int N22 = res.messages.size();
                        while (b < N22) {
                            TLRPC.Message message = res.messages.get(b);
                            long messageDialogId = MessageObject.getDialogId(message);
                            if (folderTopDialogId != messageDialogId || dialog.top_message != message.id) {
                                b++;
                                folderTopDialogId2 = folderTopDialogId2;
                                N2 = N2;
                                folderTopDialogId = folderTopDialogId;
                                N22 = N22;
                            } else {
                                TLRPC.TL_dialog newDialog = new TLRPC.TL_dialog();
                                newDialog.peer = dialog.peer;
                                newDialog.top_message = dialog.top_message;
                                newDialog.folder_id = dialogFolder.folder.id;
                                newDialog.flags |= 16;
                                res.dialogs.add(newDialog);
                                if (!(dialog.peer instanceof TLRPC.TL_peerChannel)) {
                                    if (dialog.peer instanceof TLRPC.TL_peerChat) {
                                        inputPeer = new TLRPC.TL_inputPeerChat();
                                        inputPeer.chat_id = dialog.peer.chat_id;
                                    } else {
                                        inputPeer = new TLRPC.TL_inputPeerUser();
                                        inputPeer.user_id = dialog.peer.user_id;
                                        int c = 0;
                                        int N3 = res.users.size();
                                        while (true) {
                                            if (c >= N3) {
                                                break;
                                            }
                                            TLRPC.User user = res.users.get(c);
                                            if (user.id != inputPeer.user_id) {
                                                c++;
                                            } else {
                                                inputPeer.access_hash = user.access_hash;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    inputPeer = new TLRPC.TL_inputPeerChannel();
                                    inputPeer.channel_id = dialog.peer.channel_id;
                                    int c2 = 0;
                                    int N32 = res.chats.size();
                                    while (true) {
                                        if (c2 >= N32) {
                                            break;
                                        }
                                        int N4 = N2;
                                        TLRPC.Chat chat = res.chats.get(c2);
                                        long folderTopDialogId3 = folderTopDialogId;
                                        int N23 = N22;
                                        TLRPC.Message message2 = message;
                                        if (chat.id != inputPeer.channel_id) {
                                            c2++;
                                            N2 = N4;
                                            folderTopDialogId = folderTopDialogId3;
                                            N22 = N23;
                                            message = message2;
                                        } else {
                                            inputPeer.access_hash = chat.access_hash;
                                            break;
                                        }
                                    }
                                }
                                loadUnknownDialog(inputPeer, 0L);
                                return;
                            }
                        }
                        return;
                    }
                    N = N2;
                } else {
                    N = N2;
                }
                res.dialogs.remove(dialogFolder);
            }
            a++;
            N2 = N;
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> */
    /* JADX WARN: Removed duplicated region for block: B:71:0x01f3  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0221  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void resetDialogs(boolean r22, final int r23, final int r24, final int r25, final int r26) {
        /*
            Method dump skipped, instructions count: 777
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.resetDialogs(boolean, int, int, int, int):void");
    }

    /* renamed from: lambda$resetDialogs$168$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m835x17df83c9(int seq, int newPts, int date, int qts, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            this.resetDialogsPinned = (TLRPC.TL_messages_peerDialogs) response;
            for (int a = 0; a < this.resetDialogsPinned.dialogs.size(); a++) {
                TLRPC.Dialog d = this.resetDialogsPinned.dialogs.get(a);
                d.pinned = true;
            }
            resetDialogs(false, seq, newPts, date, qts);
        }
    }

    /* renamed from: lambda$resetDialogs$169$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m836xfd20f28a(int seq, int newPts, int date, int qts, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            this.resetDialogsAll = (TLRPC.messages_Dialogs) response;
            resetDialogs(false, seq, newPts, date, qts);
        }
    }

    public void completeDialogsReset(final TLRPC.messages_Dialogs dialogsRes, int messagesCount, int seq, final int newPts, final int date, final int qts, final LongSparseArray<TLRPC.Dialog> new_dialogs_dict, final LongSparseArray<MessageObject> new_dialogMessage, TLRPC.Message lastMessage) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda298
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m619x292c7302(newPts, date, qts, dialogsRes, new_dialogs_dict, new_dialogMessage);
            }
        });
    }

    /* renamed from: lambda$completeDialogsReset$171$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m619x292c7302(int newPts, int date, int qts, final TLRPC.messages_Dialogs dialogsRes, final LongSparseArray new_dialogs_dict, final LongSparseArray new_dialogMessage) {
        this.gettingDifference = false;
        getMessagesStorage().setLastPtsValue(newPts);
        getMessagesStorage().setLastDateValue(date);
        getMessagesStorage().setLastQtsValue(qts);
        getDifference();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda102
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m618x43eb0441(dialogsRes, new_dialogs_dict, new_dialogMessage);
            }
        });
    }

    /* renamed from: lambda$completeDialogsReset$170$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m618x43eb0441(TLRPC.messages_Dialogs dialogsRes, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage) {
        MediaDataController mediaDataController;
        long key;
        this.resetingDialogs = false;
        applyDialogsNotificationsSettings(dialogsRes.dialogs);
        MediaDataController mediaDataController2 = getMediaDataController();
        mediaDataController2.clearAllDrafts(false);
        mediaDataController2.loadDraftsIfNeed();
        putUsers(dialogsRes.users, false);
        putChats(dialogsRes.chats, false);
        for (int a = 0; a < this.allDialogs.size(); a++) {
            TLRPC.Dialog oldDialog = this.allDialogs.get(a);
            if (!DialogObject.isEncryptedDialog(oldDialog.id)) {
                this.dialogs_dict.remove(oldDialog.id);
                MessageObject messageObject = this.dialogMessage.get(oldDialog.id);
                this.dialogMessage.remove(oldDialog.id);
                if (messageObject != null) {
                    if (messageObject.messageOwner.peer_id.channel_id == 0) {
                        this.dialogMessagesByIds.remove(messageObject.getId());
                    }
                    if (messageObject.messageOwner.random_id != 0) {
                        this.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                    }
                }
            }
        }
        int a2 = 0;
        while (a2 < new_dialogs_dict.size()) {
            long key2 = new_dialogs_dict.keyAt(a2);
            TLRPC.Dialog value = (TLRPC.Dialog) new_dialogs_dict.valueAt(a2);
            if (!(value.draft instanceof TLRPC.TL_draftMessage)) {
                mediaDataController = mediaDataController2;
                key = key2;
            } else {
                mediaDataController = mediaDataController2;
                key = key2;
                mediaDataController2.saveDraft(value.id, 0, value.draft, null, false);
            }
            this.dialogs_dict.put(key, value);
            MessageObject messageObject2 = (MessageObject) new_dialogMessage.get(value.id);
            this.dialogMessage.put(key, messageObject2);
            if (messageObject2 != null && messageObject2.messageOwner.peer_id.channel_id == 0) {
                this.dialogMessagesByIds.put(messageObject2.getId(), messageObject2);
                this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, messageObject2.messageOwner.date);
                if (messageObject2.messageOwner.random_id != 0) {
                    this.dialogMessagesByRandomIds.put(messageObject2.messageOwner.random_id, messageObject2);
                }
            }
            a2++;
            mediaDataController2 = mediaDataController;
        }
        this.allDialogs.clear();
        int size = this.dialogs_dict.size();
        for (int a3 = 0; a3 < size; a3++) {
            TLRPC.Dialog dialog = this.dialogs_dict.valueAt(a3);
            if (this.deletingDialogs.indexOfKey(dialog.id) < 0) {
                this.allDialogs.add(dialog);
            }
        }
        sortDialogs(null);
        this.dialogsEndReached.put(0, true);
        this.serverDialogsEndReached.put(0, false);
        this.dialogsEndReached.put(1, true);
        this.serverDialogsEndReached.put(1, false);
        int totalDialogsLoadCount = getUserConfig().getTotalDialogsCount(0);
        long[] dialogsLoadOffset = getUserConfig().getDialogLoadOffsets(0);
        if (totalDialogsLoadCount < 400 && dialogsLoadOffset[0] != -1 && dialogsLoadOffset[0] != 2147483647L) {
            loadDialogs(0, 0, 100, false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private void migrateDialogs(final int offset, int offsetDate, long offsetUser, long offsetChat, long offsetChannel, long accessPeer) {
        if (!this.migratingDialogs && offset != -1) {
            this.migratingDialogs = true;
            TLRPC.TL_messages_getDialogs req = new TLRPC.TL_messages_getDialogs();
            req.exclude_pinned = true;
            req.limit = 100;
            req.offset_id = offset;
            req.offset_date = offsetDate;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start migrate with id " + offset + " date " + LocaleController.getInstance().formatterStats.format(offsetDate * 1000));
            }
            if (offset == 0) {
                req.offset_peer = new TLRPC.TL_inputPeerEmpty();
            } else {
                if (offsetChannel != 0) {
                    req.offset_peer = new TLRPC.TL_inputPeerChannel();
                    req.offset_peer.channel_id = offsetChannel;
                } else if (offsetUser != 0) {
                    req.offset_peer = new TLRPC.TL_inputPeerUser();
                    req.offset_peer.user_id = offsetUser;
                } else {
                    req.offset_peer = new TLRPC.TL_inputPeerChat();
                    req.offset_peer.chat_id = offsetChat;
                }
                req.offset_peer.access_hash = accessPeer;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda178
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m758x991d9ec9(offset, tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$migrateDialogs$175$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m758x991d9ec9(final int offset, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            final TLRPC.messages_Dialogs dialogsRes = (TLRPC.messages_Dialogs) response;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda100
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m756xce9ac147(dialogsRes, offset);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda122
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m757xb3dc3008();
            }
        });
    }

    /* renamed from: lambda$migrateDialogs$173$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m756xce9ac147(TLRPC.messages_Dialogs dialogsRes, int offset) {
        Exception e;
        int offsetId;
        int offsetId2;
        SQLiteCursor cursor;
        StringBuilder dids;
        TLRPC.Message lastMessage;
        int totalDialogsLoadCount;
        SQLiteCursor cursor2;
        LongSparseArray<TLRPC.Dialog> dialogHashMap;
        int a;
        LongSparseArray<TLRPC.Dialog> dialogHashMap2;
        int i = offset;
        try {
            int i2 = 0;
            int totalDialogsLoadCount2 = getUserConfig().getTotalDialogsCount(0);
            getUserConfig().setTotalDialogsCount(0, dialogsRes.dialogs.size() + totalDialogsLoadCount2);
            TLRPC.Message lastMessage2 = null;
            for (int a2 = 0; a2 < dialogsRes.messages.size(); a2++) {
                TLRPC.Message message = dialogsRes.messages.get(a2);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("search migrate id " + message.id + " date " + LocaleController.getInstance().formatterStats.format(message.date * 1000));
                }
                if (lastMessage2 == null || message.date < lastMessage2.date) {
                    lastMessage2 = message;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrate step with id " + lastMessage2.id + " date " + LocaleController.getInstance().formatterStats.format(lastMessage2.date * 1000));
            }
            if (dialogsRes.dialogs.size() >= 100) {
                offsetId = lastMessage2.id;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("migrate stop due to not 100 dialogs");
                }
                int i3 = 0;
                for (int i4 = 2; i3 < i4; i4 = 2) {
                    getUserConfig().setDialogsLoadOffset(i3, Integer.MAX_VALUE, getUserConfig().migrateOffsetDate, getUserConfig().migrateOffsetUserId, getUserConfig().migrateOffsetChatId, getUserConfig().migrateOffsetChannelId, getUserConfig().migrateOffsetAccess);
                    i3++;
                }
                offsetId = -1;
            }
            StringBuilder dids2 = new StringBuilder(dialogsRes.dialogs.size() * 12);
            LongSparseArray<TLRPC.Dialog> dialogHashMap3 = new LongSparseArray<>();
            for (int a3 = 0; a3 < dialogsRes.dialogs.size(); a3++) {
                TLRPC.Dialog dialog = dialogsRes.dialogs.get(a3);
                DialogObject.initDialog(dialog);
                if (dids2.length() > 0) {
                    dids2.append(",");
                }
                dids2.append(dialog.id);
                dialogHashMap3.put(dialog.id, dialog);
            }
            int i5 = 1;
            SQLiteCursor cursor3 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did, folder_id FROM dialogs WHERE did IN (%s)", dids2.toString()), new Object[0]);
            while (cursor3.next()) {
                long did = cursor3.longValue(i2);
                int folder_id = cursor3.intValue(i5);
                TLRPC.Dialog dialog2 = dialogHashMap3.get(did);
                if (dialog2 != null) {
                    if (dialog2.folder_id == folder_id) {
                        dialogsRes.dialogs.remove(dialog2);
                        int a4 = 0;
                        while (true) {
                            if (a4 >= dialogsRes.messages.size()) {
                                break;
                            }
                            TLRPC.Message message2 = dialogsRes.messages.get(a4);
                            if (MessageObject.getDialogId(message2) == did) {
                                dialogsRes.messages.remove(a4);
                                a4--;
                                if (message2.id == dialog2.top_message) {
                                    dialog2.top_message = 0;
                                    break;
                                }
                            }
                            a4++;
                        }
                    }
                }
                dialogHashMap3.remove(did);
                i2 = 0;
                i5 = 1;
            }
            cursor3.dispose();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrate found missing dialogs " + dialogsRes.dialogs.size());
            }
            SQLiteCursor cursor4 = getMessagesStorage().getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 NOT IN (536870912, 1073741824)", new Object[0]);
            if (cursor4.next()) {
                try {
                    int date = Math.max(1441062000, cursor4.intValue(0));
                    int a5 = 0;
                    while (a5 < dialogsRes.messages.size()) {
                        TLRPC.Message message3 = dialogsRes.messages.get(a5);
                        if (message3.date >= date) {
                            totalDialogsLoadCount = totalDialogsLoadCount2;
                            cursor2 = cursor4;
                            lastMessage = lastMessage2;
                            dids = dids2;
                            dialogHashMap = dialogHashMap3;
                        } else {
                            if (i == -1) {
                                totalDialogsLoadCount = totalDialogsLoadCount2;
                                a = a5;
                                cursor2 = cursor4;
                                lastMessage = lastMessage2;
                                dids = dids2;
                                dialogHashMap2 = dialogHashMap3;
                            } else {
                                int i6 = 0;
                                while (i6 < 2) {
                                    getUserConfig().setDialogsLoadOffset(i6, getUserConfig().migrateOffsetId, getUserConfig().migrateOffsetDate, getUserConfig().migrateOffsetUserId, getUserConfig().migrateOffsetChatId, getUserConfig().migrateOffsetChannelId, getUserConfig().migrateOffsetAccess);
                                    i6++;
                                    cursor4 = cursor4;
                                    totalDialogsLoadCount2 = totalDialogsLoadCount2;
                                    lastMessage2 = lastMessage2;
                                    dids2 = dids2;
                                    dialogHashMap3 = dialogHashMap3;
                                    offsetId = offsetId;
                                    a5 = a5;
                                }
                                totalDialogsLoadCount = totalDialogsLoadCount2;
                                a = a5;
                                cursor2 = cursor4;
                                lastMessage = lastMessage2;
                                dids = dids2;
                                dialogHashMap2 = dialogHashMap3;
                                offsetId = -1;
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(date * 1000));
                                }
                            }
                            int a6 = a;
                            dialogsRes.messages.remove(a6);
                            a5 = a6 - 1;
                            long did2 = MessageObject.getDialogId(message3);
                            dialogHashMap = dialogHashMap2;
                            TLRPC.Dialog dialog3 = dialogHashMap.get(did2);
                            dialogHashMap.remove(did2);
                            if (dialog3 != null) {
                                dialogsRes.dialogs.remove(dialog3);
                            }
                        }
                        a5++;
                        i = offset;
                        dialogHashMap3 = dialogHashMap;
                        cursor4 = cursor2;
                        totalDialogsLoadCount2 = totalDialogsLoadCount;
                        lastMessage2 = lastMessage;
                        dids2 = dids;
                    }
                    offsetId2 = offsetId;
                    cursor = cursor4;
                    TLRPC.Message lastMessage3 = lastMessage2;
                    if (lastMessage3 != null) {
                        lastMessage2 = lastMessage3;
                        if (lastMessage2.date < date && offset != -1) {
                            for (int i7 = 0; i7 < 2; i7++) {
                                getUserConfig().setDialogsLoadOffset(i7, getUserConfig().migrateOffsetId, getUserConfig().migrateOffsetDate, getUserConfig().migrateOffsetUserId, getUserConfig().migrateOffsetChatId, getUserConfig().migrateOffsetChannelId, getUserConfig().migrateOffsetAccess);
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(date * 1000));
                            }
                            offsetId2 = -1;
                        }
                    } else {
                        lastMessage2 = lastMessage3;
                    }
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda110
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesController.this.m755xe9595286();
                        }
                    });
                    return;
                }
            } else {
                cursor = cursor4;
                offsetId2 = offsetId;
            }
            cursor.dispose();
            getUserConfig().migrateOffsetDate = lastMessage2.date;
            if (lastMessage2.peer_id.channel_id != 0) {
                getUserConfig().migrateOffsetChannelId = lastMessage2.peer_id.channel_id;
                getUserConfig().migrateOffsetChatId = 0L;
                getUserConfig().migrateOffsetUserId = 0L;
                int a7 = 0;
                while (true) {
                    if (a7 >= dialogsRes.chats.size()) {
                        break;
                    }
                    TLRPC.Chat chat = dialogsRes.chats.get(a7);
                    if (chat.id != getUserConfig().migrateOffsetChannelId) {
                        a7++;
                    } else {
                        getUserConfig().migrateOffsetAccess = chat.access_hash;
                        break;
                    }
                }
            } else if (lastMessage2.peer_id.chat_id != 0) {
                getUserConfig().migrateOffsetChatId = lastMessage2.peer_id.chat_id;
                getUserConfig().migrateOffsetChannelId = 0L;
                getUserConfig().migrateOffsetUserId = 0L;
                int a8 = 0;
                while (true) {
                    if (a8 >= dialogsRes.chats.size()) {
                        break;
                    }
                    TLRPC.Chat chat2 = dialogsRes.chats.get(a8);
                    if (chat2.id != getUserConfig().migrateOffsetChatId) {
                        a8++;
                    } else {
                        getUserConfig().migrateOffsetAccess = chat2.access_hash;
                        break;
                    }
                }
            } else if (lastMessage2.peer_id.user_id != 0) {
                getUserConfig().migrateOffsetUserId = lastMessage2.peer_id.user_id;
                getUserConfig().migrateOffsetChatId = 0L;
                getUserConfig().migrateOffsetChannelId = 0L;
                int a9 = 0;
                while (true) {
                    if (a9 >= dialogsRes.users.size()) {
                        break;
                    }
                    TLRPC.User user = dialogsRes.users.get(a9);
                    if (user.id != getUserConfig().migrateOffsetUserId) {
                        a9++;
                    } else {
                        getUserConfig().migrateOffsetAccess = user.access_hash;
                        break;
                    }
                }
            }
            processLoadedDialogs(dialogsRes, null, 0, offsetId2, 0, 0, false, true, false);
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* renamed from: lambda$migrateDialogs$172$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m755xe9595286() {
        this.migratingDialogs = false;
    }

    /* renamed from: lambda$migrateDialogs$174$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m757xb3dc3008() {
        this.migratingDialogs = false;
    }

    public void processLoadedDialogs(final TLRPC.messages_Dialogs dialogsRes, final ArrayList<TLRPC.EncryptedChat> encChats, final int folderId, final int offset, final int count, final int loadType, final boolean resetEnd, final boolean migrate, final boolean fromCache) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda299
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m785x75e8fa04(folderId, loadType, dialogsRes, resetEnd, count, encChats, offset, fromCache, migrate);
            }
        });
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> */
    /* JADX WARN: Removed duplicated region for block: B:164:0x0421  */
    /* JADX WARN: Removed duplicated region for block: B:173:0x0439  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x044d  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x047b  */
    /* renamed from: lambda$processLoadedDialogs$179$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m785x75e8fa04(final int r39, final int r40, final org.telegram.tgnet.TLRPC.messages_Dialogs r41, final boolean r42, final int r43, final java.util.ArrayList r44, final int r45, final boolean r46, final boolean r47) {
        /*
            Method dump skipped, instructions count: 1478
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m785x75e8fa04(int, int, org.telegram.tgnet.TLRPC$messages_Dialogs, boolean, int, java.util.ArrayList, int, boolean, boolean):void");
    }

    /* renamed from: lambda$processLoadedDialogs$176$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m782xc624adc1(TLRPC.messages_Dialogs dialogsRes, int folderId, boolean resetEnd, long[] dialogsLoadOffset, int count) {
        putUsers(dialogsRes.users, true);
        this.loadingDialogs.put(folderId, false);
        if (resetEnd) {
            this.dialogsEndReached.put(folderId, false);
            this.serverDialogsEndReached.put(folderId, false);
        } else if (dialogsLoadOffset[0] == 2147483647L) {
            this.dialogsEndReached.put(folderId, true);
            this.serverDialogsEndReached.put(folderId, true);
        } else {
            loadDialogs(folderId, 0, count, false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* renamed from: lambda$processLoadedDialogs$177$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m783xab661c82(TLRPC.Chat chat) {
        checkChatInviter(chat.id, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:136:0x02b2, code lost:
        if (r33.dialogs.size() != r7) goto L138;
     */
    /* JADX WARN: Incorrect condition in loop: B:42:0x00ae */
    /* JADX WARN: Removed duplicated region for block: B:151:0x02e7  */
    /* renamed from: lambda$processLoadedDialogs$178$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m784x90a78b43(org.telegram.tgnet.TLRPC.Message r31, int r32, org.telegram.tgnet.TLRPC.messages_Dialogs r33, java.util.ArrayList r34, boolean r35, int r36, androidx.collection.LongSparseArray r37, androidx.collection.LongSparseArray r38, androidx.collection.LongSparseArray r39, int r40, boolean r41, int r42, java.util.ArrayList r43) {
        /*
            Method dump skipped, instructions count: 955
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m784x90a78b43(org.telegram.tgnet.TLRPC$Message, int, org.telegram.tgnet.TLRPC$messages_Dialogs, java.util.ArrayList, boolean, int, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, int, boolean, int, java.util.ArrayList):void");
    }

    private void applyDialogNotificationsSettings(long dialogId, TLRPC.PeerNotifySettings notify_settings) {
        boolean updated;
        if (notify_settings == null) {
            return;
        }
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        int currentValue = sharedPreferences.getInt("notify2_" + dialogId, -1);
        SharedPreferences sharedPreferences2 = this.notificationsPreferences;
        int currentValue2 = sharedPreferences2.getInt("notifyuntil_" + dialogId, 0);
        SharedPreferences.Editor editor = this.notificationsPreferences.edit();
        boolean updated2 = false;
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog != null) {
            dialog.notify_settings = notify_settings;
        }
        if ((notify_settings.flags & 2) != 0) {
            editor.putBoolean("silent_" + dialogId, notify_settings.silent);
        } else {
            editor.remove("silent_" + dialogId);
        }
        if ((notify_settings.flags & 4) != 0) {
            if (notify_settings.mute_until > getConnectionsManager().getCurrentTime()) {
                int until = 0;
                if (notify_settings.mute_until > getConnectionsManager().getCurrentTime() + 31536000) {
                    if (currentValue != 2) {
                        updated2 = true;
                        editor.putInt("notify2_" + dialogId, 2);
                        if (dialog != null) {
                            dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                } else {
                    if (currentValue != 3 || currentValue2 != notify_settings.mute_until) {
                        updated2 = true;
                        editor.putInt("notify2_" + dialogId, 3);
                        editor.putInt("notifyuntil_" + dialogId, notify_settings.mute_until);
                        if (dialog != null) {
                            dialog.notify_settings.mute_until = 0;
                        }
                    }
                    until = notify_settings.mute_until;
                }
                getMessagesStorage().setDialogFlags(dialogId, (until << 32) | 1);
                getNotificationsController().removeNotificationsForDialog(dialogId);
                updated = updated2;
            } else {
                if (currentValue != 0 && currentValue != 1) {
                    updated2 = true;
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = 0;
                    }
                    editor.putInt("notify2_" + dialogId, 0);
                }
                getMessagesStorage().setDialogFlags(dialogId, 0L);
                updated = updated2;
            }
        } else {
            if (currentValue != -1) {
                updated2 = true;
                if (dialog != null) {
                    dialog.notify_settings.mute_until = 0;
                }
                editor.remove("notify2_" + dialogId);
            }
            getMessagesStorage().setDialogFlags(dialogId, 0L);
            updated = updated2;
        }
        applySoundSettings(notify_settings.android_sound, editor, dialogId, 0, false);
        editor.commit();
        if (updated) {
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    private void applyDialogsNotificationsSettings(ArrayList<TLRPC.Dialog> dialogs) {
        SharedPreferences.Editor editor = null;
        for (int a = 0; a < dialogs.size(); a++) {
            TLRPC.Dialog dialog = dialogs.get(a);
            if (dialog.peer != null && (dialog.notify_settings instanceof TLRPC.TL_peerNotifySettings)) {
                if (editor == null) {
                    editor = this.notificationsPreferences.edit();
                }
                long dialogId = MessageObject.getPeerId(dialog.peer);
                if ((dialog.notify_settings.flags & 2) != 0) {
                    editor.putBoolean("silent_" + dialogId, dialog.notify_settings.silent);
                } else {
                    editor.remove("silent_" + dialogId);
                }
                if ((dialog.notify_settings.flags & 4) == 0) {
                    editor.remove("notify2_" + dialogId);
                } else if (dialog.notify_settings.mute_until <= getConnectionsManager().getCurrentTime()) {
                    editor.putInt("notify2_" + dialogId, 0);
                } else if (dialog.notify_settings.mute_until > getConnectionsManager().getCurrentTime() + 31536000) {
                    editor.putInt("notify2_" + dialogId, 2);
                    dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                } else {
                    editor.putInt("notify2_" + dialogId, 3);
                    editor.putInt("notifyuntil_" + dialogId, dialog.notify_settings.mute_until);
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
    }

    public void reloadMentionsCountForChannel(final TLRPC.InputPeer peer, long taskId) {
        final long newTaskId;
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(peer.getObjectSize() + 4);
                data.writeInt32(22);
                peer.serializeToStream(data);
            } catch (Exception e) {
                FileLog.e(e);
            }
            newTaskId = getMessagesStorage().createPendingTask(data);
        } else {
            newTaskId = taskId;
        }
        TLRPC.TL_messages_getUnreadMentions req = new TLRPC.TL_messages_getUnreadMentions();
        req.peer = peer;
        req.limit = 1;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda250
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m827xeec5da90(peer, newTaskId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$reloadMentionsCountForChannel$180$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m827xeec5da90(TLRPC.InputPeer peer, long newTaskId, TLObject response, TLRPC.TL_error error) {
        int newCount;
        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
        if (res != null) {
            if (res.count != 0) {
                newCount = res.count;
            } else {
                newCount = res.messages.size();
            }
            getMessagesStorage().resetMentionsCount(-peer.channel_id, newCount);
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void reloadMentionsCountForChannels(final ArrayList<Long> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m828x988225f0(arrayList);
            }
        });
    }

    /* renamed from: lambda$reloadMentionsCountForChannels$181$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m828x988225f0(ArrayList arrayList) {
        for (int a = 0; a < arrayList.size(); a++) {
            long dialogId = -((Long) arrayList.get(a)).longValue();
            reloadMentionsCountForChannel(getInputPeer(dialogId), 0L);
        }
    }

    public void processDialogsUpdateRead(final LongSparseIntArray dialogsToUpdate, final LongSparseIntArray dialogsMentionsToUpdate) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m776x283b1a40(dialogsToUpdate, dialogsMentionsToUpdate);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:65:0x00e7, code lost:
        r0 = true;
     */
    /* renamed from: lambda$processDialogsUpdateRead$182$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m776x283b1a40(org.telegram.messenger.support.LongSparseIntArray r13, org.telegram.messenger.support.LongSparseIntArray r14) {
        /*
            Method dump skipped, instructions count: 285
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m776x283b1a40(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    public void checkLastDialogMessage(final TLRPC.Dialog dialog, TLRPC.InputPeer peer, long taskId) {
        final long newTaskId;
        if (DialogObject.isEncryptedDialog(dialog.id) || this.checkingLastMessagesDialogs.indexOfKey(dialog.id) >= 0) {
            return;
        }
        TLRPC.TL_messages_getHistory req = new TLRPC.TL_messages_getHistory();
        req.peer = peer == null ? getInputPeer(dialog.id) : peer;
        if (req.peer == null) {
            return;
        }
        req.limit = 1;
        this.checkingLastMessagesDialogs.put(dialog.id, true);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("checkLastDialogMessage for " + dialog.id);
        }
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(req.peer.getObjectSize() + 60);
                data.writeInt32(14);
                data.writeInt64(dialog.id);
                data.writeInt32(dialog.top_message);
                data.writeInt32(dialog.read_inbox_max_id);
                data.writeInt32(dialog.read_outbox_max_id);
                data.writeInt32(dialog.unread_count);
                data.writeInt32(dialog.last_message_date);
                data.writeInt32(dialog.pts);
                data.writeInt32(dialog.flags);
                data.writeBool(dialog.pinned);
                data.writeInt32(dialog.pinnedNum);
                data.writeInt32(dialog.unread_mentions_count);
                data.writeBool(dialog.unread_mark);
                data.writeInt32(dialog.folder_id);
                req.peer.serializeToStream(data);
            } catch (Exception e) {
                FileLog.e(e);
            }
            newTaskId = getMessagesStorage().createPendingTask(data);
        } else {
            newTaskId = taskId;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda249
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m602xc57a0eaa(dialog, newTaskId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$checkLastDialogMessage$186$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m602xc57a0eaa(final TLRPC.Dialog dialog, long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            removeDeletedMessagesFromArray(dialog.id, res.messages);
            if (res.messages.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda60
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m600xfaf73128(dialog);
                    }
                });
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("checkLastDialogMessage for " + dialog.id + " has message");
                }
                TLRPC.TL_messages_dialogs dialogs = new TLRPC.TL_messages_dialogs();
                TLRPC.Message newMessage = res.messages.get(0);
                TLRPC.Dialog newDialog = new TLRPC.TL_dialog();
                newDialog.flags = dialog.flags;
                newDialog.top_message = newMessage.id;
                newDialog.last_message_date = newMessage.date;
                newDialog.notify_settings = dialog.notify_settings;
                newDialog.pts = dialog.pts;
                newDialog.unread_count = dialog.unread_count;
                newDialog.unread_mark = dialog.unread_mark;
                newDialog.unread_mentions_count = dialog.unread_mentions_count;
                newDialog.unread_reactions_count = dialog.unread_reactions_count;
                newDialog.read_inbox_max_id = dialog.read_inbox_max_id;
                newDialog.read_outbox_max_id = dialog.read_outbox_max_id;
                newDialog.pinned = dialog.pinned;
                newDialog.pinnedNum = dialog.pinnedNum;
                newDialog.folder_id = dialog.folder_id;
                long j = dialog.id;
                newDialog.id = j;
                newMessage.dialog_id = j;
                dialogs.users.addAll(res.users);
                dialogs.chats.addAll(res.chats);
                dialogs.dialogs.add(newDialog);
                dialogs.messages.addAll(res.messages);
                dialogs.count = 1;
                processDialogsUpdate(dialogs, null, false);
                getMessagesStorage().putMessages(res.messages, true, true, false, getDownloadController().getAutodownloadMask(), true, false);
            }
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda61
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m601xe0389fe9(dialog);
            }
        });
    }

    /* renamed from: lambda$checkLastDialogMessage$184$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m600xfaf73128(final TLRPC.Dialog dialog) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("checkLastDialogMessage for " + dialog.id + " has not message");
        }
        if (getMediaDataController().getDraft(dialog.id, 0) == null) {
            TLRPC.Dialog currentDialog = this.dialogs_dict.get(dialog.id);
            if (currentDialog == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("checkLastDialogMessage for " + dialog.id + " current dialog not found");
                }
                getMessagesStorage().isDialogHasTopMessage(dialog.id, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda59
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m599x15b5c267(dialog);
                    }
                });
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("checkLastDialogMessage for " + dialog.id + " current dialog top message " + currentDialog.top_message);
            }
            if (currentDialog.top_message == 0) {
                deleteDialog(dialog.id, 3);
            }
        }
    }

    /* renamed from: lambda$checkLastDialogMessage$183$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m599x15b5c267(TLRPC.Dialog dialog) {
        deleteDialog(dialog.id, 3);
    }

    /* renamed from: lambda$checkLastDialogMessage$185$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m601xe0389fe9(TLRPC.Dialog dialog) {
        this.checkingLastMessagesDialogs.delete(dialog.id);
    }

    public void processDialogsUpdate(final TLRPC.messages_Dialogs dialogsRes, ArrayList<TLRPC.EncryptedChat> encChats, final boolean fromCache) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda105
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m775xbf713490(dialogsRes, fromCache);
            }
        });
    }

    /* renamed from: lambda$processDialogsUpdate$188$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m775xbf713490(final TLRPC.messages_Dialogs dialogsRes, final boolean fromCache) {
        MessageObject mess;
        final LongSparseArray<TLRPC.Dialog> new_dialogs_dict = new LongSparseArray<>();
        final LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray<>();
        LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>(dialogsRes.users.size());
        LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>(dialogsRes.chats.size());
        final LongSparseIntArray dialogsToUpdate = new LongSparseIntArray();
        for (int a = 0; a < dialogsRes.users.size(); a++) {
            TLRPC.User u = dialogsRes.users.get(a);
            usersDict.put(u.id, u);
        }
        for (int a2 = 0; a2 < dialogsRes.chats.size(); a2++) {
            TLRPC.Chat c = dialogsRes.chats.get(a2);
            chatsDict.put(c.id, c);
        }
        ArrayList<MessageObject> newMessages = new ArrayList<>();
        for (int a3 = 0; a3 < dialogsRes.messages.size(); a3++) {
            TLRPC.Message message = dialogsRes.messages.get(a3);
            long j = this.promoDialogId;
            if (j == 0 || j != message.dialog_id) {
                if (message.peer_id.channel_id != 0) {
                    TLRPC.Chat chat = chatsDict.get(message.peer_id.channel_id);
                    if (chat != null && ChatObject.isNotInChat(chat)) {
                    }
                } else if (message.peer_id.chat_id != 0) {
                    TLRPC.Chat chat2 = chatsDict.get(message.peer_id.chat_id);
                    if (chat2 != null) {
                        if (chat2.migrated_to == null) {
                            if (ChatObject.isNotInChat(chat2)) {
                            }
                        }
                    }
                }
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false, false);
            newMessages.add(messageObject);
            new_dialogMessage.put(messageObject.getDialogId(), messageObject);
        }
        getFileLoader().checkMediaExistance(newMessages);
        for (int a4 = 0; a4 < dialogsRes.dialogs.size(); a4++) {
            TLRPC.Dialog d = dialogsRes.dialogs.get(a4);
            DialogObject.initDialog(d);
            long j2 = this.promoDialogId;
            if (j2 == 0 || j2 != d.id) {
                if (DialogObject.isChannel(d)) {
                    TLRPC.Chat chat3 = chatsDict.get(-d.id);
                    if (chat3 != null && ChatObject.isNotInChat(chat3)) {
                    }
                } else if (DialogObject.isChatDialog(d.id)) {
                    TLRPC.Chat chat4 = chatsDict.get(-d.id);
                    if (chat4 != null) {
                        if (chat4.migrated_to == null) {
                            if (ChatObject.isNotInChat(chat4)) {
                            }
                        }
                    }
                }
            }
            if (d.last_message_date == 0 && (mess = new_dialogMessage.get(d.id)) != null) {
                d.last_message_date = mess.messageOwner.date;
            }
            new_dialogs_dict.put(d.id, d);
            dialogsToUpdate.put(d.id, d.unread_count);
            Integer value = this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
            if (value == null) {
                value = 0;
            }
            this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
            Integer value2 = this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
            if (value2 == null) {
                value2 = 0;
            }
            this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value2.intValue(), d.read_outbox_max_id)));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda103
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m774xda2fc5cf(dialogsRes, new_dialogs_dict, new_dialogMessage, fromCache, dialogsToUpdate);
            }
        });
    }

    /* renamed from: lambda$processDialogsUpdate$187$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m774xda2fc5cf(TLRPC.messages_Dialogs dialogsRes, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage, boolean fromCache, LongSparseIntArray dialogsToUpdate) {
        long key;
        LongSparseArray longSparseArray = new_dialogs_dict;
        int i = 1;
        putUsers(dialogsRes.users, true);
        putChats(dialogsRes.chats, true);
        int a = 0;
        while (a < new_dialogs_dict.size()) {
            long key2 = longSparseArray.keyAt(a);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("processDialogsUpdate " + key2);
            }
            TLRPC.Dialog value = (TLRPC.Dialog) longSparseArray.valueAt(a);
            TLRPC.Dialog currentDialog = this.dialogs_dict.get(key2);
            MessageObject newMsg = (MessageObject) new_dialogMessage.get(value.id);
            if (currentDialog == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("processDialogsUpdate dialog null");
                }
                int offset = this.nextDialogsCacheOffset.get(value.folder_id, 0) + i;
                this.nextDialogsCacheOffset.put(value.folder_id, offset);
                this.dialogs_dict.put(key2, value);
                this.dialogMessage.put(key2, newMsg);
                if (newMsg == null) {
                    if (fromCache) {
                        checkLastDialogMessage(value, null, 0L);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("processDialogsUpdate new message is null");
                    }
                } else if (newMsg.messageOwner.peer_id.channel_id == 0) {
                    this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                    this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, newMsg.messageOwner.date);
                    if (newMsg.messageOwner.random_id != 0) {
                        this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("processDialogsUpdate new message not null");
                    }
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("processDialogsUpdate dialog not null");
                }
                currentDialog.unread_count = value.unread_count;
                if (currentDialog.unread_mentions_count == value.unread_mentions_count) {
                    key = key2;
                } else {
                    currentDialog.unread_mentions_count = value.unread_mentions_count;
                    key = key2;
                    if (this.createdDialogMainThreadIds.contains(Long.valueOf(currentDialog.id))) {
                        getNotificationCenter().postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(currentDialog.id), Integer.valueOf(currentDialog.unread_mentions_count));
                    }
                }
                if (currentDialog.unread_reactions_count != value.unread_reactions_count) {
                    currentDialog.unread_reactions_count = value.unread_reactions_count;
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(currentDialog.id), Integer.valueOf(currentDialog.unread_reactions_count), null);
                }
                long key3 = key;
                MessageObject oldMsg = this.dialogMessage.get(key3);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("processDialogsUpdate oldMsg " + oldMsg + " old top_message = " + currentDialog.top_message + " new top_message = " + value.top_message + "  unread_count =" + currentDialog.unread_count + " fromCache=" + fromCache);
                    StringBuilder sb = new StringBuilder();
                    sb.append("processDialogsUpdate oldMsgDeleted ");
                    sb.append(oldMsg != null && oldMsg.deleted);
                    FileLog.d(sb.toString());
                }
                if (oldMsg == null || currentDialog.top_message > 0) {
                    if ((oldMsg != null && oldMsg.deleted) || value.top_message > currentDialog.top_message) {
                        this.dialogs_dict.put(key3, value);
                        this.dialogMessage.put(key3, newMsg);
                        if (oldMsg != null && oldMsg.messageOwner.peer_id.channel_id == 0) {
                            this.dialogMessagesByIds.remove(oldMsg.getId());
                            if (oldMsg.messageOwner.random_id != 0) {
                                this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                            }
                        }
                        if (newMsg != null) {
                            if (oldMsg != null && oldMsg.getId() == newMsg.getId()) {
                                newMsg.deleted = oldMsg.deleted;
                            }
                            if (newMsg.messageOwner.peer_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                                this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, newMsg.messageOwner.date);
                                if (newMsg.messageOwner.random_id != 0) {
                                    this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                                }
                            }
                        }
                    }
                    if (fromCache && newMsg == null) {
                        checkLastDialogMessage(value, null, 0L);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("processDialogsUpdate new message is null");
                        }
                    }
                } else if (oldMsg.deleted || newMsg == null || newMsg.messageOwner.date > oldMsg.messageOwner.date) {
                    this.dialogs_dict.put(key3, value);
                    this.dialogMessage.put(key3, newMsg);
                    if (oldMsg.messageOwner.peer_id.channel_id == 0) {
                        this.dialogMessagesByIds.remove(oldMsg.getId());
                    }
                    if (newMsg != null) {
                        if (oldMsg.getId() == newMsg.getId()) {
                            newMsg.deleted = oldMsg.deleted;
                        }
                        if (newMsg.messageOwner.peer_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                            this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, newMsg.messageOwner.date);
                            if (newMsg.messageOwner.random_id != 0) {
                                this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                            }
                        }
                    }
                    if (oldMsg.messageOwner.random_id != 0) {
                        this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                    }
                }
            }
            a++;
            longSparseArray = new_dialogs_dict;
            i = 1;
        }
        this.allDialogs.clear();
        int size = this.dialogs_dict.size();
        for (int a2 = 0; a2 < size; a2++) {
            TLRPC.Dialog dialog = this.dialogs_dict.valueAt(a2);
            if (this.deletingDialogs.indexOfKey(dialog.id) < 0) {
                this.allDialogs.add(dialog);
            }
        }
        sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationsController().processDialogsUpdateRead(dialogsToUpdate);
    }

    public void addToViewsQueue(final MessageObject messageObject) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m578xeda3071(messageObject);
            }
        });
    }

    /* renamed from: lambda$addToViewsQueue$189$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m578xeda3071(MessageObject messageObject) {
        long peer = messageObject.getDialogId();
        int id = messageObject.getId();
        ArrayList<Integer> ids = this.channelViewsToSend.get(peer);
        if (ids == null) {
            ids = new ArrayList<>();
            this.channelViewsToSend.put(peer, ids);
        }
        if (!ids.contains(Integer.valueOf(id))) {
            ids.add(Integer.valueOf(id));
        }
    }

    public void loadReactionsForMessages(long dialogId, ArrayList<MessageObject> visibleObjects) {
        if (visibleObjects.isEmpty()) {
            return;
        }
        TLRPC.TL_messages_getMessagesReactions req = new TLRPC.TL_messages_getMessagesReactions();
        req.peer = getInputPeer(dialogId);
        for (int i = 0; i < visibleObjects.size(); i++) {
            MessageObject messageObject = visibleObjects.get(i);
            req.id.add(Integer.valueOf(messageObject.getId()));
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda157
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m733x27fd5333(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadReactionsForMessages$190$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m733x27fd5333(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            for (int i = 0; i < updates.updates.size(); i++) {
                if (updates.updates.get(i) instanceof TLRPC.TL_updateMessageReactions) {
                    ((TLRPC.TL_updateMessageReactions) updates.updates.get(i)).updateUnreadState = false;
                }
            }
            processUpdates(updates, false);
        }
    }

    public void addToPollsQueue(long dialogId, ArrayList<MessageObject> visibleObjects) {
        SparseArray<MessageObject> array = this.pollsToCheck.get(dialogId);
        if (array == null) {
            array = new SparseArray<>();
            this.pollsToCheck.put(dialogId, array);
            this.pollsToCheckSize++;
        }
        int N = array.size();
        for (int a = 0; a < N; a++) {
            array.valueAt(a).pollVisibleOnScreen = false;
        }
        int time = getConnectionsManager().getCurrentTime();
        int minExpireTime = Integer.MAX_VALUE;
        boolean hasExpiredPolls = false;
        int N2 = visibleObjects.size();
        for (int a2 = 0; a2 < N2; a2++) {
            MessageObject messageObject = visibleObjects.get(a2);
            if (messageObject.type == 17) {
                TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) messageObject.messageOwner.media;
                if (!mediaPoll.poll.closed && mediaPoll.poll.close_date != 0) {
                    if (mediaPoll.poll.close_date <= time) {
                        hasExpiredPolls = true;
                    } else {
                        minExpireTime = Math.min(minExpireTime, mediaPoll.poll.close_date - time);
                    }
                }
                int id = messageObject.getId();
                MessageObject object = array.get(id);
                if (object != null) {
                    object.pollVisibleOnScreen = true;
                } else {
                    array.put(id, messageObject);
                }
            }
        }
        if (hasExpiredPolls) {
            this.lastViewsCheckTime = 0L;
        } else if (minExpireTime < 5) {
            this.lastViewsCheckTime = Math.min(this.lastViewsCheckTime, System.currentTimeMillis() - ((5 - minExpireTime) * 1000));
        }
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        if (messageObject.scheduled) {
            return;
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (messageObject.messageOwner.mentioned) {
            getMessagesStorage().markMentionMessageAsRead(-messageObject.messageOwner.peer_id.channel_id, messageObject.getId(), messageObject.getDialogId());
        }
        arrayList.add(Integer.valueOf(messageObject.getId()));
        long dialogId = messageObject.getDialogId();
        getMessagesStorage().markMessagesContentAsRead(dialogId, arrayList, 0);
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(dialogId), arrayList);
        if (messageObject.getId() < 0) {
            markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, Integer.MIN_VALUE);
        } else if (messageObject.messageOwner.peer_id.channel_id != 0) {
            TLRPC.TL_channels_readMessageContents req = new TLRPC.TL_channels_readMessageContents();
            req.channel = getInputChannel(messageObject.messageOwner.peer_id.channel_id);
            if (req.channel == null) {
                return;
            }
            req.id.add(Integer.valueOf(messageObject.getId()));
            getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda280.INSTANCE);
        } else {
            TLRPC.TL_messages_readMessageContents req2 = new TLRPC.TL_messages_readMessageContents();
            req2.id.add(Integer.valueOf(messageObject.getId()));
            getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda163
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m754x3dae112b(tLObject, tL_error);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$markMessageContentAsRead$191(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$markMessageContentAsRead$192$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m754x3dae112b(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
    }

    public void markMentionMessageAsRead(int mid, long channelId, long did) {
        getMessagesStorage().markMentionMessageAsRead(-channelId, mid, did);
        if (channelId != 0) {
            TLRPC.TL_channels_readMessageContents req = new TLRPC.TL_channels_readMessageContents();
            req.channel = getInputChannel(channelId);
            if (req.channel == null) {
                return;
            }
            req.id.add(Integer.valueOf(mid));
            getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda278.INSTANCE);
            return;
        }
        TLRPC.TL_messages_readMessageContents req2 = new TLRPC.TL_messages_readMessageContents();
        req2.id.add(Integer.valueOf(mid));
        getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda162
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m751x6da0c778(tLObject, tL_error);
            }
        });
    }

    public static /* synthetic */ void lambda$markMentionMessageAsRead$193(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$markMentionMessageAsRead$194$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m751x6da0c778(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x009f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void markMessageAsRead2(long r17, int r19, org.telegram.tgnet.TLRPC.InputChannel r20, int r21, long r22) {
        /*
            r16 = this;
            r1 = r16
            r10 = r19
            r11 = r21
            if (r10 == 0) goto Lba
            if (r11 > 0) goto Le
            r13 = r17
            goto Lbc
        Le:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r17)
            if (r0 == 0) goto L1f
            if (r20 != 0) goto L1f
            org.telegram.tgnet.TLRPC$InputChannel r0 = r16.getInputChannel(r17)
            if (r0 != 0) goto L1d
            return
        L1d:
            r12 = r0
            goto L21
        L1f:
            r12 = r20
        L21:
            r2 = 0
            int r0 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r0 != 0) goto L61
            r2 = 0
            org.telegram.tgnet.NativeByteBuffer r0 = new org.telegram.tgnet.NativeByteBuffer     // Catch: java.lang.Exception -> L51
            r3 = 20
            if (r12 == 0) goto L33
            int r4 = r12.getObjectSize()     // Catch: java.lang.Exception -> L51
            goto L34
        L33:
            r4 = 0
        L34:
            int r3 = r3 + r4
            r0.<init>(r3)     // Catch: java.lang.Exception -> L51
            r2 = r0
            r0 = 23
            r2.writeInt32(r0)     // Catch: java.lang.Exception -> L51
            r13 = r17
            r2.writeInt64(r13)     // Catch: java.lang.Exception -> L4f
            r2.writeInt32(r10)     // Catch: java.lang.Exception -> L4f
            r2.writeInt32(r11)     // Catch: java.lang.Exception -> L4f
            if (r12 == 0) goto L4e
            r12.serializeToStream(r2)     // Catch: java.lang.Exception -> L4f
        L4e:
            goto L57
        L4f:
            r0 = move-exception
            goto L54
        L51:
            r0 = move-exception
            r13 = r17
        L54:
            org.telegram.messenger.FileLog.e(r0)
        L57:
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            long r2 = r0.createPendingTask(r2)
            r8 = r2
            goto L66
        L61:
            r13 = r17
            r2 = r22
            r8 = r2
        L66:
            org.telegram.tgnet.ConnectionsManager r0 = r16.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            org.telegram.messenger.MessagesStorage r2 = r16.getMessagesStorage()
            r15 = 0
            r3 = r17
            r5 = r19
            r6 = r0
            r7 = r0
            r10 = r8
            r8 = r21
            r9 = r15
            r2.createTaskForMid(r3, r5, r6, r7, r8, r9)
            if (r12 == 0) goto L9f
            org.telegram.tgnet.TLRPC$TL_channels_readMessageContents r2 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents
            r2.<init>()
            r2.channel = r12
            java.util.ArrayList<java.lang.Integer> r3 = r2.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r19)
            r3.add(r4)
            org.telegram.tgnet.ConnectionsManager r3 = r16.getConnectionsManager()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda198 r4 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda198
            r4.<init>()
            r3.sendRequest(r2, r4)
            goto Lb9
        L9f:
            org.telegram.tgnet.TLRPC$TL_messages_readMessageContents r2 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents
            r2.<init>()
            java.util.ArrayList<java.lang.Integer> r3 = r2.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r19)
            r3.add(r4)
            org.telegram.tgnet.ConnectionsManager r3 = r16.getConnectionsManager()
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda200 r4 = new org.telegram.messenger.MessagesController$$ExternalSyntheticLambda200
            r4.<init>()
            r3.sendRequest(r2, r4)
        Lb9:
            return
        Lba:
            r13 = r17
        Lbc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markMessageAsRead2(long, int, org.telegram.tgnet.TLRPC$InputChannel, int, long):void");
    }

    /* renamed from: lambda$markMessageAsRead2$195$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m752xf805a577(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    /* renamed from: lambda$markMessageAsRead2$196$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m753xdd471438(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void markMessageAsRead(long dialogId, long randomId, int ttl) {
        TLRPC.EncryptedChat chat;
        if (randomId == 0 || dialogId == 0) {
            return;
        }
        if ((ttl <= 0 && ttl != Integer.MIN_VALUE) || !DialogObject.isEncryptedDialog(dialogId) || (chat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)))) == null) {
            return;
        }
        ArrayList<Long> randomIds = new ArrayList<>();
        randomIds.add(Long.valueOf(randomId));
        getSecretChatHelper().sendMessagesReadMessage(chat, randomIds, null);
        if (ttl > 0) {
            int time = getConnectionsManager().getCurrentTime();
            getMessagesStorage().createTaskForSecretChat(chat.id, time, time, 0, randomIds);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void completeReadTask(ReadTask task) {
        TLObject req;
        if (task.replyId != 0) {
            TLRPC.TL_messages_readDiscussion req2 = new TLRPC.TL_messages_readDiscussion();
            req2.msg_id = (int) task.replyId;
            req2.peer = getInputPeer(task.dialogId);
            req2.read_max_id = task.maxId;
            getConnectionsManager().sendRequest(req2, MessagesController$$ExternalSyntheticLambda269.INSTANCE);
        } else if (!DialogObject.isEncryptedDialog(task.dialogId)) {
            TLRPC.InputPeer inputPeer = getInputPeer(task.dialogId);
            if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
                TLRPC.TL_channels_readHistory request = new TLRPC.TL_channels_readHistory();
                request.channel = getInputChannel(-task.dialogId);
                request.max_id = task.maxId;
                req = request;
            } else {
                TLRPC.TL_messages_readHistory request2 = new TLRPC.TL_messages_readHistory();
                request2.peer = inputPeer;
                request2.max_id = task.maxId;
                req = request2;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda149
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m620xd486ac1e(tLObject, tL_error);
                }
            });
        } else {
            TLRPC.EncryptedChat chat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(task.dialogId)));
            if (chat.auth_key != null && chat.auth_key.length > 1 && (chat instanceof TLRPC.TL_encryptedChat)) {
                TLRPC.TL_messages_readEncryptedHistory req3 = new TLRPC.TL_messages_readEncryptedHistory();
                req3.peer = new TLRPC.TL_inputEncryptedChat();
                req3.peer.chat_id = chat.id;
                req3.peer.access_hash = chat.access_hash;
                req3.max_date = task.maxDate;
                getConnectionsManager().sendRequest(req3, MessagesController$$ExternalSyntheticLambda270.INSTANCE);
            }
        }
    }

    public static /* synthetic */ void lambda$completeReadTask$197(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$completeReadTask$198$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m620xd486ac1e(TLObject response, TLRPC.TL_error error) {
        if (error == null && (response instanceof TLRPC.TL_messages_affectedMessages)) {
            TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
    }

    public static /* synthetic */ void lambda$completeReadTask$199(TLObject response, TLRPC.TL_error error) {
    }

    private void checkReadTasks() {
        long time = SystemClock.elapsedRealtime();
        int a = 0;
        int size = this.readTasks.size();
        while (a < size) {
            ReadTask task = this.readTasks.get(a);
            if (task.sendRequestTime <= time) {
                completeReadTask(task);
                this.readTasks.remove(a);
                this.readTasksMap.remove(task.dialogId);
                a--;
                size--;
            }
            a++;
        }
        int a2 = 0;
        int size2 = this.repliesReadTasks.size();
        while (a2 < size2) {
            ReadTask task2 = this.repliesReadTasks.get(a2);
            if (task2.sendRequestTime <= time) {
                completeReadTask(task2);
                this.repliesReadTasks.remove(a2);
                this.threadsReadTasksMap.remove(task2.dialogId + "_" + task2.replyId);
                a2 += -1;
                size2 += -1;
            }
            a2++;
        }
    }

    public void markDialogAsReadNow(final long dialogId, final int replyId) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda300
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m749x64edaaab(replyId, dialogId);
            }
        });
    }

    /* renamed from: lambda$markDialogAsReadNow$200$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m749x64edaaab(int replyId, long dialogId) {
        if (replyId != 0) {
            String key = dialogId + "_" + replyId;
            ReadTask currentReadTask = this.threadsReadTasksMap.get(key);
            if (currentReadTask == null) {
                return;
            }
            completeReadTask(currentReadTask);
            this.repliesReadTasks.remove(currentReadTask);
            this.threadsReadTasksMap.remove(key);
            return;
        }
        ReadTask currentReadTask2 = this.readTasksMap.get(dialogId);
        if (currentReadTask2 == null) {
            return;
        }
        completeReadTask(currentReadTask2);
        this.readTasks.remove(currentReadTask2);
        this.readTasksMap.remove(dialogId);
    }

    public void markMentionsAsRead(long dialogId) {
        if (DialogObject.isEncryptedDialog(dialogId)) {
            return;
        }
        getMessagesStorage().resetMentionsCount(dialogId, 0);
        TLRPC.TL_messages_readMentions req = new TLRPC.TL_messages_readMentions();
        req.peer = getInputPeer(dialogId);
        getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda279.INSTANCE);
    }

    public static /* synthetic */ void lambda$markMentionsAsRead$201(TLObject response, TLRPC.TL_error error) {
    }

    public void markDialogAsRead(final long dialogId, final int maxPositiveId, final int maxNegativeId, final int maxDate, final boolean popup, final int threadId, final int countDiff, final boolean readNow, int scheduledCount) {
        boolean createReadTask;
        Integer value;
        if (threadId != 0) {
            createReadTask = maxPositiveId != Integer.MAX_VALUE;
        } else {
            boolean z = getNotificationsController().showBadgeMessages;
            if (!DialogObject.isEncryptedDialog(dialogId)) {
                if (maxPositiveId == 0) {
                    return;
                }
                Integer value2 = this.dialogs_read_inbox_max.get(Long.valueOf(dialogId));
                if (value2 != null) {
                    value = value2;
                } else {
                    value = 0;
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialogId), Integer.valueOf(Math.max(value.intValue(), maxPositiveId)));
                getMessagesStorage().processPendingRead(dialogId, maxPositiveId, maxNegativeId, scheduledCount);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda327
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m745x3685afec(dialogId, countDiff, maxPositiveId, popup);
                    }
                });
                boolean createReadTask2 = maxPositiveId != Integer.MAX_VALUE;
                createReadTask = createReadTask2;
            } else if (maxDate == 0) {
                return;
            } else {
                createReadTask = true;
                TLRPC.EncryptedChat chat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)));
                getMessagesStorage().processPendingRead(dialogId, maxPositiveId, maxNegativeId, scheduledCount);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda332
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m747x1088d6e(dialogId, maxDate, popup, countDiff, maxNegativeId);
                    }
                });
                if (chat != null && chat.ttl > 0) {
                    int serverTime = Math.max(getConnectionsManager().getCurrentTime(), maxDate);
                    getMessagesStorage().createTaskForSecretChat(chat.id, maxDate, serverTime, 0, null);
                }
            }
        }
        if (createReadTask) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda303
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m748xe649fc2f(threadId, dialogId, readNow, maxDate, maxPositiveId);
                }
            });
        }
    }

    /* renamed from: lambda$markDialogAsRead$203$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m745x3685afec(final long dialogId, final int countDiff, final int maxPositiveId, final boolean popup) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda326
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m744x5144412b(dialogId, countDiff, maxPositiveId, popup);
            }
        });
    }

    /* renamed from: lambda$markDialogAsRead$202$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m744x5144412b(long dialogId, int countDiff, int maxPositiveId, boolean popup) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog != null) {
            int prevCount = dialog.unread_count;
            if (countDiff == 0 || maxPositiveId >= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                dialog.unread_count = Math.max(dialog.unread_count - countDiff, 0);
                if (maxPositiveId != Integer.MIN_VALUE && dialog.unread_count > dialog.top_message - maxPositiveId) {
                    dialog.unread_count = dialog.top_message - maxPositiveId;
                }
            }
            boolean wasUnread = dialog.unread_mark;
            if (wasUnread) {
                dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog.id, false);
            }
            if ((prevCount != 0 || wasUnread) && dialog.unread_count == 0) {
                if (!isDialogMuted(dialogId)) {
                    this.unreadUnmutedDialogs--;
                }
                int b = 0;
                while (true) {
                    DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                    if (b < dialogFilterArr.length) {
                        if (dialogFilterArr[b] == null || (dialogFilterArr[b].flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0) {
                            b++;
                        } else {
                            sortDialogs(null);
                            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
        }
        if (!popup) {
            getNotificationsController().processReadMessages(null, dialogId, 0, maxPositiveId, false);
            LongSparseIntArray dialogsToUpdate = new LongSparseIntArray(1);
            dialogsToUpdate.put(dialogId, 0);
            getNotificationsController().processDialogsUpdateRead(dialogsToUpdate);
            return;
        }
        getNotificationsController().processReadMessages(null, dialogId, 0, maxPositiveId, true);
        LongSparseIntArray dialogsToUpdate2 = new LongSparseIntArray(1);
        dialogsToUpdate2.put(dialogId, -1);
        getNotificationsController().processDialogsUpdateRead(dialogsToUpdate2);
    }

    /* renamed from: lambda$markDialogAsRead$205$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m747x1088d6e(final long dialogId, final int maxDate, final boolean popup, final int countDiff, final int maxNegativeId) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda331
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m746x1bc71ead(dialogId, maxDate, popup, countDiff, maxNegativeId);
            }
        });
    }

    /* renamed from: lambda$markDialogAsRead$204$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m746x1bc71ead(long dialogId, int maxDate, boolean popup, int countDiff, int maxNegativeId) {
        getNotificationsController().processReadMessages(null, dialogId, maxDate, 0, popup);
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog != null) {
            int prevCount = dialog.unread_count;
            if (countDiff == 0 || maxNegativeId <= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                dialog.unread_count = Math.max(dialog.unread_count - countDiff, 0);
                if (maxNegativeId != Integer.MAX_VALUE && dialog.unread_count > maxNegativeId - dialog.top_message) {
                    dialog.unread_count = maxNegativeId - dialog.top_message;
                }
            }
            boolean wasUnread = dialog.unread_mark;
            if (wasUnread) {
                dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog.id, false);
            }
            if ((prevCount != 0 || wasUnread) && dialog.unread_count == 0) {
                if (!isDialogMuted(dialogId)) {
                    this.unreadUnmutedDialogs--;
                }
                int b = 0;
                while (true) {
                    DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                    if (b < dialogFilterArr.length) {
                        if (dialogFilterArr[b] == null || (dialogFilterArr[b].flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0) {
                            b++;
                        } else {
                            sortDialogs(null);
                            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
        }
        LongSparseIntArray dialogsToUpdate = new LongSparseIntArray(1);
        dialogsToUpdate.put(dialogId, 0);
        getNotificationsController().processDialogsUpdateRead(dialogsToUpdate);
    }

    /* renamed from: lambda$markDialogAsRead$206$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m748xe649fc2f(int threadId, long dialogId, boolean readNow, int maxDate, int maxPositiveId) {
        ReadTask currentReadTask;
        if (threadId != 0) {
            HashMap<String, ReadTask> hashMap = this.threadsReadTasksMap;
            currentReadTask = hashMap.get(dialogId + "_" + threadId);
        } else {
            currentReadTask = this.readTasksMap.get(dialogId);
        }
        if (currentReadTask == null) {
            currentReadTask = new ReadTask();
            currentReadTask.dialogId = dialogId;
            currentReadTask.replyId = threadId;
            currentReadTask.sendRequestTime = SystemClock.elapsedRealtime() + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
            if (!readNow) {
                if (threadId != 0) {
                    HashMap<String, ReadTask> hashMap2 = this.threadsReadTasksMap;
                    hashMap2.put(dialogId + "_" + threadId, currentReadTask);
                    this.repliesReadTasks.add(currentReadTask);
                } else {
                    this.readTasksMap.put(dialogId, currentReadTask);
                    this.readTasks.add(currentReadTask);
                }
            }
        }
        currentReadTask.maxDate = maxDate;
        currentReadTask.maxId = maxPositiveId;
        if (readNow) {
            completeReadTask(currentReadTask);
        }
    }

    public int createChat(String title, ArrayList<Long> selectedContacts, String about, int type, boolean forImport, Location location, String locationAddress, final BaseFragment fragment) {
        if (type != 0 || forImport) {
            if (forImport || type == 2 || type == 4) {
                final TLRPC.TL_channels_createChannel req = new TLRPC.TL_channels_createChannel();
                req.title = title;
                req.about = about != null ? about : "";
                req.for_import = forImport;
                if (forImport || type == 4) {
                    req.megagroup = true;
                } else {
                    req.broadcast = true;
                }
                if (location != null) {
                    req.geo_point = new TLRPC.TL_inputGeoPoint();
                    req.geo_point.lat = location.getLatitude();
                    req.geo_point._long = location.getLongitude();
                    req.address = locationAddress;
                    req.flags = 4 | req.flags;
                }
                return getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda256
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessagesController.this.m632lambda$createChat$212$orgtelegrammessengerMessagesController(fragment, req, tLObject, tL_error);
                    }
                }, 2);
            }
            return 0;
        }
        final TLRPC.TL_messages_createChat req2 = new TLRPC.TL_messages_createChat();
        req2.title = title;
        for (int a = 0; a < selectedContacts.size(); a++) {
            TLRPC.User user = getUser(selectedContacts.get(a));
            if (user != null) {
                req2.users.add(getInputUser(user));
            }
        }
        return getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda258
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m629lambda$createChat$209$orgtelegrammessengerMessagesController(fragment, req2, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$createChat$209$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m629lambda$createChat$209$orgtelegrammessengerMessagesController(final BaseFragment fragment, final TLRPC.TL_messages_createChat req, TLObject response, final TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda76
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m627lambda$createChat$207$orgtelegrammessengerMessagesController(error, fragment, req);
                }
            });
            return;
        }
        final TLRPC.Updates updates = (TLRPC.Updates) response;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda93
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m628lambda$createChat$208$orgtelegrammessengerMessagesController(updates);
            }
        });
    }

    /* renamed from: lambda$createChat$207$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m627lambda$createChat$207$orgtelegrammessengerMessagesController(TLRPC.TL_error error, BaseFragment fragment, TLRPC.TL_messages_createChat req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    /* renamed from: lambda$createChat$208$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m628lambda$createChat$208$orgtelegrammessengerMessagesController(TLRPC.Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        if (updates.chats != null && !updates.chats.isEmpty()) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidCreated, Long.valueOf(updates.chats.get(0).id));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
        }
    }

    /* renamed from: lambda$createChat$212$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m632lambda$createChat$212$orgtelegrammessengerMessagesController(final BaseFragment fragment, final TLRPC.TL_channels_createChannel req, TLObject response, final TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda72
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m630lambda$createChat$210$orgtelegrammessengerMessagesController(error, fragment, req);
                }
            });
            return;
        }
        final TLRPC.Updates updates = (TLRPC.Updates) response;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda94
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m631lambda$createChat$211$orgtelegrammessengerMessagesController(updates);
            }
        });
    }

    /* renamed from: lambda$createChat$210$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m630lambda$createChat$210$orgtelegrammessengerMessagesController(TLRPC.TL_error error, BaseFragment fragment, TLRPC.TL_channels_createChannel req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    /* renamed from: lambda$createChat$211$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m631lambda$createChat$211$orgtelegrammessengerMessagesController(TLRPC.Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        if (updates.chats != null && !updates.chats.isEmpty()) {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidCreated, Long.valueOf(updates.chats.get(0).id));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
        }
    }

    public void convertToMegaGroup(Context context, long chatId, BaseFragment fragment, MessagesStorage.LongCallback convertRunnable) {
        convertToMegaGroup(context, chatId, fragment, convertRunnable, null);
    }

    public void convertToMegaGroup(final Context context, long chatId, final BaseFragment fragment, final MessagesStorage.LongCallback convertRunnable, final Runnable errorRunnable) {
        final TLRPC.TL_messages_migrateChat req = new TLRPC.TL_messages_migrateChat();
        req.chat_id = chatId;
        final AlertDialog progressDialog = context != null ? new AlertDialog(context, 3) : null;
        final int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda234
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m625x1c553450(context, progressDialog, convertRunnable, errorRunnable, fragment, req, tLObject, tL_error);
            }
        });
        if (progressDialog != null) {
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda111
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    MessagesController.this.m626x196a311(reqId, dialogInterface);
                }
            });
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: lambda$convertToMegaGroup$216$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m625x1c553450(final Context context, final AlertDialog progressDialog, final MessagesStorage.LongCallback convertRunnable, Runnable errorRunnable, final BaseFragment fragment, final TLRPC.TL_messages_migrateChat req, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            if (context != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda317
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.lambda$convertToMegaGroup$213(context, progressDialog);
                    }
                });
            }
            final TLRPC.Updates updates = (TLRPC.Updates) response;
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda129
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.lambda$convertToMegaGroup$214(MessagesStorage.LongCallback.this, updates);
                }
            });
            return;
        }
        if (errorRunnable != null) {
            errorRunnable.run();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m624x3713c58f(convertRunnable, context, progressDialog, error, fragment, req);
            }
        });
    }

    public static /* synthetic */ void lambda$convertToMegaGroup$213(Context context, AlertDialog progressDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static /* synthetic */ void lambda$convertToMegaGroup$214(MessagesStorage.LongCallback convertRunnable, TLRPC.Updates updates) {
        if (convertRunnable != null) {
            for (int a = 0; a < updates.chats.size(); a++) {
                TLRPC.Chat chat = updates.chats.get(a);
                if (ChatObject.isChannel(chat)) {
                    convertRunnable.run(chat.id);
                    return;
                }
            }
        }
    }

    /* renamed from: lambda$convertToMegaGroup$215$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m624x3713c58f(MessagesStorage.LongCallback convertRunnable, Context context, AlertDialog progressDialog, TLRPC.TL_error error, BaseFragment fragment, TLRPC.TL_messages_migrateChat req) {
        if (convertRunnable != null) {
            convertRunnable.run(0L);
        }
        if (context != null && !((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            AlertsCreator.processError(this.currentAccount, error, fragment, req, false);
        }
    }

    /* renamed from: lambda$convertToMegaGroup$217$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m626x196a311(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    public void convertToGigaGroup(final Context context, TLRPC.Chat chat, final BaseFragment fragment, final MessagesStorage.BooleanCallback convertRunnable) {
        final TLRPC.TL_channels_convertToGigagroup req = new TLRPC.TL_channels_convertToGigagroup();
        req.channel = getInputChannel(chat);
        final AlertDialog progressDialog = context != null ? new AlertDialog(context, 3) : null;
        final int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda232
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m622xd56e6ee0(context, progressDialog, convertRunnable, fragment, req, tLObject, tL_error);
            }
        });
        if (progressDialog != null) {
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    MessagesController.this.m623xbaafdda1(reqId, dialogInterface);
                }
            });
            try {
                progressDialog.showDelayed(400L);
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: lambda$convertToGigaGroup$221$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m622xd56e6ee0(final Context context, final AlertDialog progressDialog, final MessagesStorage.BooleanCallback convertRunnable, final BaseFragment fragment, final TLRPC.TL_channels_convertToGigagroup req, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            if (context != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda306
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.lambda$convertToGigaGroup$218(context, progressDialog);
                    }
                });
            }
            TLRPC.Updates updates = (TLRPC.Updates) response;
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda128
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.lambda$convertToGigaGroup$219(MessagesStorage.BooleanCallback.this);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m621xf02d001f(convertRunnable, context, progressDialog, error, fragment, req);
            }
        });
    }

    public static /* synthetic */ void lambda$convertToGigaGroup$218(Context context, AlertDialog progressDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static /* synthetic */ void lambda$convertToGigaGroup$219(MessagesStorage.BooleanCallback convertRunnable) {
        if (convertRunnable != null) {
            convertRunnable.run(true);
        }
    }

    /* renamed from: lambda$convertToGigaGroup$220$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m621xf02d001f(MessagesStorage.BooleanCallback convertRunnable, Context context, AlertDialog progressDialog, TLRPC.TL_error error, BaseFragment fragment, TLRPC.TL_channels_convertToGigagroup req) {
        if (convertRunnable != null) {
            convertRunnable.run(false);
        }
        if (context != null && !((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            AlertsCreator.processError(this.currentAccount, error, fragment, req, false);
        }
    }

    /* renamed from: lambda$convertToGigaGroup$222$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m623xbaafdda1(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    public void addUsersToChannel(long chatId, ArrayList<TLRPC.InputUser> users, final BaseFragment fragment) {
        if (users == null || users.isEmpty()) {
            return;
        }
        final TLRPC.TL_channels_inviteToChannel req = new TLRPC.TL_channels_inviteToChannel();
        req.channel = getInputChannel(chatId);
        req.users = users;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda257
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m585xdd488535(fragment, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$addUsersToChannel$224$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m585xdd488535(final BaseFragment fragment, final TLRPC.TL_channels_inviteToChannel req, TLObject response, final TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda75
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m584xf8071674(error, fragment, req);
                }
            });
        } else {
            processUpdates((TLRPC.Updates) response, false);
        }
    }

    /* renamed from: lambda$addUsersToChannel$223$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m584xf8071674(TLRPC.TL_error error, BaseFragment fragment, TLRPC.TL_channels_inviteToChannel req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, true);
    }

    public void setDefaultSendAs(final long chatId, long newPeer) {
        TLRPC.ChatFull cachedFull = getChatFull(-chatId);
        if (cachedFull != null) {
            cachedFull.default_send_as = getPeer(newPeer);
            getMessagesStorage().updateChatInfo(cachedFull, false);
            getNotificationCenter().postNotificationName(NotificationCenter.updateDefaultSendAsPeer, Long.valueOf(chatId), cachedFull.default_send_as);
        }
        TLRPC.TL_messages_saveDefaultSendAs req = new TLRPC.TL_messages_saveDefaultSendAs();
        req.peer = getInputPeer(chatId);
        req.send_as = getInputPeer(newPeer);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda205
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m855x156f19a8(chatId, tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$setDefaultSendAs$225$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m855x156f19a8(long chatId, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_boolTrue) {
            TLRPC.ChatFull full = getChatFull(-chatId);
            if (full == null) {
                loadFullChat(-chatId, 0, true);
            }
        } else if (error != null && error.code == 400) {
            loadFullChat(-chatId, 0, true);
        }
    }

    public void toggleChatNoForwards(long chatId, boolean enabled) {
        TLRPC.TL_messages_toggleNoForwards req = new TLRPC.TL_messages_toggleNoForwards();
        req.peer = getInputPeer(-chatId);
        req.enabled = enabled;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda172
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m879x3b5e5c0c(tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$toggleChatNoForwards$227$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m879x3b5e5c0c(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda288
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m878x561ced4b();
                }
            });
        }
    }

    /* renamed from: lambda$toggleChatNoForwards$226$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m878x561ced4b() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChatJoinToSend(long chatId, boolean enabled, final Runnable onSuccess, final Runnable onError) {
        TLRPC.TL_channels_toggleJoinToSend req = new TLRPC.TL_channels_toggleJoinToSend();
        req.channel = getInputChannel(chatId);
        req.enabled = enabled;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda239
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m877xb852b44c(onSuccess, onError, tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$toggleChatJoinToSend$229$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m877xb852b44c(Runnable onSuccess, Runnable onError, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda277
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m876xd311458b();
                }
            });
            if (onSuccess != null) {
                onSuccess.run();
            }
        }
        if (error != null) {
            if (!"CHAT_NOT_MODIFIED".equals(error.text)) {
                if (onError != null) {
                    onError.run();
                }
            } else if (response == null && onSuccess != null) {
                onSuccess.run();
            }
        }
    }

    /* renamed from: lambda$toggleChatJoinToSend$228$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m876xd311458b() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChatJoinRequest(long chatId, boolean enabled, final Runnable onSuccess, final Runnable onError) {
        TLRPC.TL_channels_toggleJoinRequest req = new TLRPC.TL_channels_toggleJoinRequest();
        req.channel = getInputChannel(chatId);
        req.enabled = enabled;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda238
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m875xd5103089(onSuccess, onError, tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$toggleChatJoinRequest$231$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m875xd5103089(Runnable onSuccess, Runnable onError, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda266
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m874xefcec1c8();
                }
            });
            if (onSuccess != null) {
                onSuccess.run();
            }
        }
        if (error != null) {
            if (!"CHAT_NOT_MODIFIED".equals(error.text)) {
                if (onError != null) {
                    onError.run();
                }
            } else if (response == null && onSuccess != null) {
                onSuccess.run();
            }
        }
    }

    /* renamed from: lambda$toggleChatJoinRequest$230$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m874xefcec1c8() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChannelSignatures(long chatId, boolean enabled) {
        TLRPC.TL_channels_toggleSignatures req = new TLRPC.TL_channels_toggleSignatures();
        req.channel = getInputChannel(chatId);
        req.enabled = enabled;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda171
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m873x68e9011c(tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$toggleChannelSignatures$233$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m873x68e9011c(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda255
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m872x83a7925b();
                }
            });
        }
    }

    /* renamed from: lambda$toggleChannelSignatures$232$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m872x83a7925b() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void toggleChannelInvitesHistory(long chatId, boolean enabled) {
        TLRPC.TL_channels_togglePreHistoryHidden req = new TLRPC.TL_channels_togglePreHistoryHidden();
        req.channel = getInputChannel(chatId);
        req.enabled = enabled;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda170
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m871x96a1b74d(tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$toggleChannelInvitesHistory$235$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m871x96a1b74d(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda244
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m870xb160488c();
                }
            });
        }
    }

    /* renamed from: lambda$toggleChannelInvitesHistory$234$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m870xb160488c() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void updateChatAbout(long chatId, final String about, final TLRPC.ChatFull info) {
        TLRPC.TL_messages_editChatAbout req = new TLRPC.TL_messages_editChatAbout();
        req.peer = getInputPeer(-chatId);
        req.about = about;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda248
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m884xcbaf8cc2(info, about, tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$updateChatAbout$237$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m884xcbaf8cc2(final TLRPC.ChatFull info, final String about, TLObject response, TLRPC.TL_error error) {
        if ((response instanceof TLRPC.TL_boolTrue) && info != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda58
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m883xe66e1e01(info, about);
                }
            });
        }
    }

    /* renamed from: lambda$updateChatAbout$236$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m883xe66e1e01(TLRPC.ChatFull info, String about) {
        info.about = about;
        getMessagesStorage().updateChatInfo(info, false);
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, info, 0, false, false);
    }

    public void updateChannelUserName(final long chatId, final String userName) {
        TLRPC.TL_channels_updateUsername req = new TLRPC.TL_channels_updateUsername();
        req.channel = getInputChannel(chatId);
        req.username = userName;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda220
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m882x4caaabc8(chatId, userName, tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$updateChannelUserName$239$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m882x4caaabc8(final long chatId, final String userName, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda340
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m881x67693d07(chatId, userName);
                }
            });
        }
    }

    /* renamed from: lambda$updateChannelUserName$238$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m881x67693d07(long chatId, String userName) {
        TLRPC.Chat chat = getChat(Long.valueOf(chatId));
        if (userName.length() != 0) {
            chat.flags |= 64;
        } else {
            chat.flags &= -65;
        }
        chat.username = userName;
        ArrayList<TLRPC.Chat> arrayList = new ArrayList<>();
        arrayList.add(chat);
        getMessagesStorage().putUsersAndChats(null, arrayList, true, true);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
    }

    public void sendBotStart(TLRPC.User user, String botHash) {
        if (user == null) {
            return;
        }
        TLRPC.TL_messages_startBot req = new TLRPC.TL_messages_startBot();
        req.bot = getInputUser(user);
        req.peer = getInputPeer(user.id);
        req.start_param = botHash;
        req.random_id = Utilities.random.nextLong();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda168
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m842xcb4b17eb(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$sendBotStart$240$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m842xcb4b17eb(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) response, false);
    }

    public boolean isJoiningChannel(long chatId) {
        return this.joiningToChannels.contains(Long.valueOf(chatId));
    }

    public void addUserToChat(long chatId, TLRPC.User user, int forwardCount, String botHash, BaseFragment fragment, Runnable onFinishRunnable) {
        addUserToChat(chatId, user, forwardCount, botHash, fragment, false, onFinishRunnable, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void addUserToChat(final long chatId, TLRPC.User user, int forwardCount, String botHash, final BaseFragment fragment, final boolean ignoreIfAlreadyExists, final Runnable onFinishRunnable, final ErrorDelegate onError) {
        TLObject request;
        if (user == null) {
            if (onError != null) {
                onError.run(null);
                return;
            }
            return;
        }
        final boolean isChannel = ChatObject.isChannel(chatId, this.currentAccount);
        final boolean isMegagroup = isChannel && getChat(Long.valueOf(chatId)).megagroup;
        final TLRPC.InputUser inputUser = getInputUser(user);
        if (botHash == null || (isChannel && !isMegagroup)) {
            if (isChannel) {
                if (inputUser instanceof TLRPC.TL_inputUserSelf) {
                    if (this.joiningToChannels.contains(Long.valueOf(chatId))) {
                        if (onError != null) {
                            onError.run(null);
                            return;
                        }
                        return;
                    }
                    TLRPC.TL_channels_joinChannel req = new TLRPC.TL_channels_joinChannel();
                    req.channel = getInputChannel(chatId);
                    this.joiningToChannels.add(Long.valueOf(chatId));
                    request = req;
                } else {
                    TLRPC.TL_channels_inviteToChannel req2 = new TLRPC.TL_channels_inviteToChannel();
                    req2.channel = getInputChannel(chatId);
                    req2.users.add(inputUser);
                    request = req2;
                }
            } else {
                TLRPC.TL_messages_addChatUser req3 = new TLRPC.TL_messages_addChatUser();
                req3.chat_id = chatId;
                req3.fwd_limit = forwardCount;
                req3.user_id = inputUser;
                request = req3;
            }
        } else {
            TLRPC.TL_messages_startBot req4 = new TLRPC.TL_messages_startBot();
            req4.bot = inputUser;
            if (isChannel) {
                req4.peer = getInputPeer(-chatId);
            } else {
                req4.peer = new TLRPC.TL_inputPeerChat();
                req4.peer.chat_id = chatId;
            }
            req4.start_param = botHash;
            req4.random_id = Utilities.random.nextLong();
            request = req4;
        }
        final TLObject request2 = request;
        getConnectionsManager().sendRequest(request2, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda263
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m583x61e3df72(isChannel, inputUser, chatId, ignoreIfAlreadyExists, onFinishRunnable, onError, fragment, request2, isMegagroup, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$addUserToChat$245$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m583x61e3df72(final boolean isChannel, final TLRPC.InputUser inputUser, final long chatId, boolean ignoreIfAlreadyExists, Runnable onFinishRunnable, final ErrorDelegate onError, final BaseFragment fragment, final TLObject request, final boolean isMegagroup, TLObject response, final TLRPC.TL_error error) {
        boolean hasJoinMessage;
        if (isChannel && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda313
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m579xccde246e(chatId);
                }
            });
        }
        if (error != null) {
            if ("USER_ALREADY_PARTICIPANT".equals(error.text) && ignoreIfAlreadyExists) {
                if (onFinishRunnable != null) {
                    AndroidUtilities.runOnUIThread(onFinishRunnable);
                    return;
                }
                return;
            }
            if (onError != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda31
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m580xb21f932f(onError, error, fragment, request, isChannel, isMegagroup);
                    }
                });
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m581x976101f0(onError, error, fragment, request, isChannel, isMegagroup, inputUser);
                }
            });
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) response;
        int a = 0;
        while (true) {
            if (a >= updates.updates.size()) {
                hasJoinMessage = false;
                break;
            }
            TLRPC.Update update = updates.updates.get(a);
            if (!(update instanceof TLRPC.TL_updateNewChannelMessage) || !(((TLRPC.TL_updateNewChannelMessage) update).message.action instanceof TLRPC.TL_messageActionChatAddUser)) {
                a++;
            } else {
                hasJoinMessage = true;
                break;
            }
        }
        processUpdates(updates, false);
        if (isChannel) {
            if (!hasJoinMessage && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
                generateJoinMessage(chatId, true);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda314
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m582x7ca270b1(chatId);
                }
            }, 1000L);
        }
        if (isChannel && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
            getMessagesStorage().updateDialogsWithDeletedMessages(-chatId, chatId, new ArrayList<>(), null, true);
        }
        if (onFinishRunnable != null) {
            AndroidUtilities.runOnUIThread(onFinishRunnable);
        }
    }

    /* renamed from: lambda$addUserToChat$241$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m579xccde246e(long chatId) {
        this.joiningToChannels.remove(Long.valueOf(chatId));
    }

    /* renamed from: lambda$addUserToChat$242$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m580xb21f932f(ErrorDelegate onError, TLRPC.TL_error error, BaseFragment fragment, TLObject request, boolean isChannel, boolean isMegagroup) {
        boolean handleErrors = onError.run(error);
        if (handleErrors) {
            int i = this.currentAccount;
            boolean z = true;
            Object[] objArr = new Object[1];
            if (!isChannel || isMegagroup) {
                z = false;
            }
            objArr[0] = Boolean.valueOf(z);
            AlertsCreator.processError(i, error, fragment, request, objArr);
        }
    }

    /* renamed from: lambda$addUserToChat$243$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m581x976101f0(ErrorDelegate onError, TLRPC.TL_error error, BaseFragment fragment, TLObject request, boolean isChannel, boolean isMegagroup, TLRPC.InputUser inputUser) {
        if (onError == null) {
            int i = this.currentAccount;
            Object[] objArr = new Object[1];
            objArr[0] = Boolean.valueOf(isChannel && !isMegagroup);
            AlertsCreator.processError(i, error, fragment, request, objArr);
        }
        if (isChannel && (inputUser instanceof TLRPC.TL_inputUserSelf)) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_CHAT));
        }
    }

    /* renamed from: lambda$addUserToChat$244$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m582x7ca270b1(long chatId) {
        loadFullChat(chatId, 0, true);
    }

    public void deleteParticipantFromChat(long chatId, TLRPC.User user, TLRPC.ChatFull info) {
        deleteParticipantFromChat(chatId, user, null, info, false, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void deleteParticipantFromChat(final long chatId, final TLRPC.User user, TLRPC.Chat chat, TLRPC.ChatFull info, boolean forceDelete, boolean revoke) {
        TLRPC.InputPeer inputPeer;
        TLObject request;
        if (user == null && chat == null) {
            return;
        }
        if (user != null) {
            inputPeer = getInputPeer(user);
        } else {
            TLRPC.InputPeer inputPeer2 = getInputPeer(chat);
            inputPeer = inputPeer2;
        }
        TLRPC.Chat ownerChat = getChat(Long.valueOf(chatId));
        final boolean isChannel = ChatObject.isChannel(ownerChat);
        if (isChannel) {
            if (UserObject.isUserSelf(user)) {
                if (ownerChat.creator && forceDelete) {
                    TLRPC.TL_channels_deleteChannel req = new TLRPC.TL_channels_deleteChannel();
                    req.channel = getInputChannel(ownerChat);
                    request = req;
                } else {
                    TLRPC.TL_channels_leaveChannel req2 = new TLRPC.TL_channels_leaveChannel();
                    req2.channel = getInputChannel(ownerChat);
                    request = req2;
                }
            } else {
                TLRPC.TL_channels_editBanned req3 = new TLRPC.TL_channels_editBanned();
                req3.channel = getInputChannel(ownerChat);
                req3.participant = inputPeer;
                req3.banned_rights = new TLRPC.TL_chatBannedRights();
                req3.banned_rights.view_messages = true;
                req3.banned_rights.send_media = true;
                req3.banned_rights.send_messages = true;
                req3.banned_rights.send_stickers = true;
                req3.banned_rights.send_gifs = true;
                req3.banned_rights.send_games = true;
                req3.banned_rights.send_inline = true;
                req3.banned_rights.embed_links = true;
                req3.banned_rights.pin_messages = true;
                req3.banned_rights.send_polls = true;
                req3.banned_rights.invite_users = true;
                req3.banned_rights.change_info = true;
                request = req3;
            }
        } else if (forceDelete) {
            TLRPC.TL_messages_deleteChat req4 = new TLRPC.TL_messages_deleteChat();
            req4.chat_id = chatId;
            getConnectionsManager().sendRequest(req4, MessagesController$$ExternalSyntheticLambda271.INSTANCE);
            return;
        } else {
            TLRPC.TL_messages_deleteChatUser req5 = new TLRPC.TL_messages_deleteChatUser();
            req5.chat_id = chatId;
            req5.user_id = getInputUser(user);
            req5.revoke_history = true;
            request = req5;
        }
        if (UserObject.isUserSelf(user)) {
            deleteDialog(-chatId, 0, revoke);
        }
        getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda265
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m647x1d40c720(isChannel, user, chatId, tLObject, tL_error);
            }
        }, 64);
    }

    public static /* synthetic */ void lambda$deleteParticipantFromChat$246(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$deleteParticipantFromChat$248$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m647x1d40c720(boolean isChannel, TLRPC.User user, final long chatId, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) response;
        processUpdates(updates, false);
        if (isChannel && !UserObject.isUserSelf(user)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda318
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m646x37ff585f(chatId);
                }
            }, 1000L);
        }
    }

    /* renamed from: lambda$deleteParticipantFromChat$247$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m646x37ff585f(long chatId) {
        loadFullChat(chatId, 0, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void changeChatTitle(long chatId, String title) {
        TLObject request;
        if (ChatObject.isChannel(chatId, this.currentAccount)) {
            TLRPC.TL_channels_editTitle req = new TLRPC.TL_channels_editTitle();
            req.channel = getInputChannel(chatId);
            req.title = title;
            request = req;
        } else {
            TLRPC.TL_messages_editChatTitle req2 = new TLRPC.TL_messages_editChatTitle();
            req2.chat_id = chatId;
            req2.title = title;
            request = req2;
        }
        getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda147
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m588xcc7e5467(tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$changeChatTitle$249$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m588xcc7e5467(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) response, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void changeChatAvatar(long chatId, final TLRPC.TL_inputChatPhoto oldPhoto, TLRPC.InputFile inputPhoto, TLRPC.InputFile inputVideo, double videoStartTimestamp, final String videoPath, final TLRPC.FileLocation smallSize, final TLRPC.FileLocation bigSize, final Runnable callback) {
        TLRPC.InputChatPhoto inputChatPhoto;
        TLObject request;
        if (oldPhoto != null) {
            inputChatPhoto = oldPhoto;
        } else if (inputPhoto != null || inputVideo != null) {
            TLRPC.TL_inputChatUploadedPhoto uploadedPhoto = new TLRPC.TL_inputChatUploadedPhoto();
            if (inputPhoto != null) {
                uploadedPhoto.file = inputPhoto;
                uploadedPhoto.flags |= 1;
            }
            if (inputVideo != null) {
                uploadedPhoto.video = inputVideo;
                uploadedPhoto.flags |= 2;
                uploadedPhoto.video_start_ts = videoStartTimestamp;
                uploadedPhoto.flags |= 4;
            }
            inputChatPhoto = uploadedPhoto;
        } else {
            inputChatPhoto = new TLRPC.TL_inputChatPhotoEmpty();
        }
        if (ChatObject.isChannel(chatId, this.currentAccount)) {
            TLRPC.TL_channels_editPhoto req = new TLRPC.TL_channels_editPhoto();
            req.channel = getInputChannel(chatId);
            req.photo = inputChatPhoto;
            request = req;
        } else {
            TLRPC.TL_messages_editChatPhoto req2 = new TLRPC.TL_messages_editChatPhoto();
            req2.chat_id = chatId;
            req2.photo = inputChatPhoto;
            request = req2;
        }
        getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda251
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m587x517e1129(oldPhoto, smallSize, bigSize, videoPath, callback, tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$changeChatAvatar$251$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m587x517e1129(TLRPC.TL_inputChatPhoto oldPhoto, TLRPC.FileLocation smallSize, TLRPC.FileLocation bigSize, String videoPath, final Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) response;
        if (oldPhoto == null) {
            TLRPC.Photo photo = null;
            int a = 0;
            int N = updates.updates.size();
            while (true) {
                if (a >= N) {
                    break;
                }
                TLRPC.Update update = updates.updates.get(a);
                if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                    TLRPC.Message message = ((TLRPC.TL_updateNewChannelMessage) update).message;
                    if ((message.action instanceof TLRPC.TL_messageActionChatEditPhoto) && (message.action.photo instanceof TLRPC.TL_photo)) {
                        photo = message.action.photo;
                        break;
                    }
                    a++;
                } else {
                    if (update instanceof TLRPC.TL_updateNewMessage) {
                        TLRPC.Message message2 = ((TLRPC.TL_updateNewMessage) update).message;
                        if ((message2.action instanceof TLRPC.TL_messageActionChatEditPhoto) && (message2.action.photo instanceof TLRPC.TL_photo)) {
                            photo = message2.action.photo;
                            break;
                        }
                    } else {
                        continue;
                    }
                    a++;
                }
            }
            if (photo != null) {
                TLRPC.PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 150);
                TLRPC.VideoSize videoSize = photo.video_sizes.isEmpty() ? null : photo.video_sizes.get(0);
                if (small != null && smallSize != null) {
                    File destFile = getFileLoader().getPathToAttach(small, true);
                    File src = getFileLoader().getPathToAttach(smallSize, true);
                    src.renameTo(destFile);
                    String oldKey = smallSize.volume_id + "_" + smallSize.local_id + "@50_50";
                    String newKey = small.location.volume_id + "_" + small.location.local_id + "@50_50";
                    ImageLoader.getInstance().replaceImageInCache(oldKey, newKey, ImageLocation.getForPhoto(small, photo), true);
                }
                TLRPC.PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                if (big != null && bigSize != null) {
                    File destFile2 = getFileLoader().getPathToAttach(big, true);
                    File src2 = getFileLoader().getPathToAttach(bigSize, true);
                    src2.renameTo(destFile2);
                }
                if (videoSize != null && videoPath != null) {
                    File destFile3 = getFileLoader().getPathToAttach(videoSize, "mp4", true);
                    File src3 = new File(videoPath);
                    src3.renameTo(destFile3);
                }
            }
        }
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m586x6c3ca268(callback);
            }
        });
    }

    /* renamed from: lambda$changeChatAvatar$250$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m586x6c3ca268(Runnable callback) {
        if (callback != null) {
            callback.run();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
    }

    public void unregistedPush() {
        if (getUserConfig().registeredForPush && SharedConfig.pushString.length() == 0) {
            TLRPC.TL_account_unregisterDevice req = new TLRPC.TL_account_unregisterDevice();
            req.token = SharedConfig.pushString;
            req.token_type = 2;
            for (int a = 0; a < 4; a++) {
                UserConfig userConfig = UserConfig.getInstance(a);
                if (a != this.currentAccount && userConfig.isClientActivated()) {
                    req.other_uids.add(Long.valueOf(userConfig.getClientUserId()));
                }
            }
            getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda290.INSTANCE);
        }
    }

    public static /* synthetic */ void lambda$unregistedPush$252(TLObject response, TLRPC.TL_error error) {
    }

    public void performLogout(int type) {
        boolean z = true;
        if (type == 1) {
            unregistedPush();
            TLRPC.TL_auth_logOut req = new TLRPC.TL_auth_logOut();
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda164
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m770xe373f5bc(tLObject, tL_error);
                }
            });
        } else {
            ConnectionsManager connectionsManager = getConnectionsManager();
            if (type != 2) {
                z = false;
            }
            connectionsManager.cleanup(z);
        }
        getUserConfig().clearConfig();
        SharedPrefsHelper.cleanupAccount(this.currentAccount);
        boolean shouldHandle = true;
        ArrayList<NotificationCenter.NotificationCenterDelegate> observers = getNotificationCenter().getObservers(NotificationCenter.appDidLogout);
        if (observers != null) {
            int a = 0;
            int N = observers.size();
            while (true) {
                if (a >= N) {
                    break;
                } else if (!(observers.get(a) instanceof LaunchActivity)) {
                    a++;
                } else {
                    shouldHandle = false;
                    break;
                }
            }
        }
        if (shouldHandle && UserConfig.selectedAccount == this.currentAccount) {
            int account = -1;
            int a2 = 0;
            while (true) {
                if (a2 >= 4) {
                    break;
                } else if (!UserConfig.getInstance(a2).isClientActivated()) {
                    a2++;
                } else {
                    account = a2;
                    break;
                }
            }
            if (account != -1) {
                UserConfig.selectedAccount = account;
                UserConfig.getInstance(0).saveConfig(false);
                LaunchActivity.clearFragments();
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        getMessagesStorage().cleanup(false);
        cleanup();
        getContactsController().deleteUnknownAppAccounts();
    }

    /* renamed from: lambda$performLogout$254$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m770xe373f5bc(final TLObject response, TLRPC.TL_error error) {
        getConnectionsManager().cleanup(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda130
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.lambda$performLogout$253(TLObject.this);
            }
        });
    }

    public static /* synthetic */ void lambda$performLogout$253(TLObject response) {
        if (response instanceof TLRPC.TL_auth_loggedOut) {
            TLRPC.TL_auth_loggedOut res = (TLRPC.TL_auth_loggedOut) response;
            if (((TLRPC.TL_auth_loggedOut) response).future_auth_token != null) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("saved_tokens", 0);
                int count = preferences.getInt(NotificationBadge.NewHtcHomeBadger.COUNT, 0);
                SerializedData data = new SerializedData(response.getObjectSize());
                res.serializeToStream(data);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("log_out_token_" + count, Utilities.bytesToHex(data.toByteArray())).putInt(NotificationBadge.NewHtcHomeBadger.COUNT, count + 1).apply();
            }
        }
    }

    public static ArrayList<TLRPC.TL_auth_loggedOut> getSavedLogOutTokens() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("saved_tokens", 0);
        int count = preferences.getInt(NotificationBadge.NewHtcHomeBadger.COUNT, 0);
        if (count == 0) {
            return null;
        }
        ArrayList<TLRPC.TL_auth_loggedOut> tokens = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String value = preferences.getString("log_out_token_" + i, "");
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(value));
            TLRPC.TL_auth_loggedOut token = TLRPC.TL_auth_loggedOut.TLdeserialize(serializedData, serializedData.readInt32(true), true);
            if (token != null) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public static void saveLogOutTokens(ArrayList<TLRPC.TL_auth_loggedOut> tokens) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("saved_tokens", 0);
        ArrayList<TLRPC.TL_auth_loggedOut> activeTokens = new ArrayList<>();
        preferences.edit().clear().apply();
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        for (int i = 0; i < Math.min(20, tokens.size()); i++) {
            activeTokens.add(tokens.get(i));
        }
        int i2 = activeTokens.size();
        if (i2 > 0) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(NotificationBadge.NewHtcHomeBadger.COUNT, activeTokens.size());
            for (int i3 = 0; i3 < activeTokens.size(); i3++) {
                SerializedData data = new SerializedData(activeTokens.get(i3).getObjectSize());
                activeTokens.get(i3).serializeToStream(data);
                editor.putString("log_out_token_" + i3, Utilities.bytesToHex(data.toByteArray()));
            }
            editor.apply();
        }
    }

    public void generateUpdateMessage() {
        if (this.gettingAppChangelog || BuildVars.DEBUG_VERSION || SharedConfig.lastUpdateVersion == null || SharedConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING)) {
            return;
        }
        this.gettingAppChangelog = true;
        TLRPC.TL_help_getAppChangelog req = new TLRPC.TL_help_getAppChangelog();
        req.prev_app_version = SharedConfig.lastUpdateVersion;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda152
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m668xb2e3f43b(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$generateUpdateMessage$255$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m668xb2e3f43b(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
            SharedConfig.saveConfig();
        }
        if (response instanceof TLRPC.Updates) {
            processUpdates((TLRPC.Updates) response, false);
        }
    }

    public void registerForPush(final String regid) {
        if (TextUtils.isEmpty(regid) || this.registeringForPush || getUserConfig().getClientUserId() == 0) {
            return;
        }
        if (getUserConfig().registeredForPush && regid.equals(SharedConfig.pushString)) {
            return;
        }
        this.registeringForPush = true;
        this.lastPushRegisterSendTime = SystemClock.elapsedRealtime();
        if (SharedConfig.pushAuthKey == null) {
            SharedConfig.pushAuthKey = new byte[256];
            Utilities.random.nextBytes(SharedConfig.pushAuthKey);
            SharedConfig.saveConfig();
        }
        TLRPC.TL_account_registerDevice req = new TLRPC.TL_account_registerDevice();
        req.token_type = 2;
        req.token = regid;
        req.no_muted = false;
        req.secret = SharedConfig.pushAuthKey;
        for (int a = 0; a < 4; a++) {
            UserConfig userConfig = UserConfig.getInstance(a);
            if (a != this.currentAccount && userConfig.isClientActivated()) {
                long uid = userConfig.getClientUserId();
                req.other_uids.add(Long.valueOf(uid));
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("add other uid = " + uid + " for account " + this.currentAccount);
                }
            }
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda240
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m825x159acb94(regid, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$registerForPush$257$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m825x159acb94(String regid, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_boolTrue) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("account " + this.currentAccount + " registered for push");
            }
            getUserConfig().registeredForPush = true;
            SharedConfig.pushString = regid;
            getUserConfig().saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda233
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m824x30595cd3();
            }
        });
    }

    /* renamed from: lambda$registerForPush$256$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m824x30595cd3() {
        this.registeringForPush = false;
    }

    public void loadCurrentState() {
        if (this.updatingState) {
            return;
        }
        this.updatingState = true;
        TLRPC.TL_updates_getState req = new TLRPC.TL_updates_getState();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda154
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m706xe72bbdad(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadCurrentState$258$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m706xe72bbdad(TLObject response, TLRPC.TL_error error) {
        this.updatingState = false;
        if (error == null) {
            TLRPC.TL_updates_state res = (TLRPC.TL_updates_state) response;
            getMessagesStorage().setLastDateValue(res.date);
            getMessagesStorage().setLastPtsValue(res.pts);
            getMessagesStorage().setLastSeqValue(res.seq);
            getMessagesStorage().setLastQtsValue(res.qts);
            for (int a = 0; a < 3; a++) {
                processUpdatesQueue(a, 2);
            }
            getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        } else if (error.code != 401) {
            loadCurrentState();
        }
    }

    private int getUpdateSeq(TLRPC.Updates updates) {
        if (updates instanceof TLRPC.TL_updatesCombined) {
            return updates.seq_start;
        }
        return updates.seq;
    }

    private void setUpdatesStartTime(int type, long time) {
        if (type == 0) {
            this.updatesStartWaitTimeSeq = time;
        } else if (type == 1) {
            this.updatesStartWaitTimePts = time;
        } else if (type == 2) {
            this.updatesStartWaitTimeQts = time;
        }
    }

    public long getUpdatesStartTime(int type) {
        if (type == 0) {
            return this.updatesStartWaitTimeSeq;
        }
        if (type == 1) {
            return this.updatesStartWaitTimePts;
        }
        if (type == 2) {
            return this.updatesStartWaitTimeQts;
        }
        return 0L;
    }

    private int isValidUpdate(TLRPC.Updates updates, int type) {
        if (type == 0) {
            int seq = getUpdateSeq(updates);
            if (getMessagesStorage().getLastSeqValue() + 1 == seq || getMessagesStorage().getLastSeqValue() == seq) {
                return 0;
            }
            return getMessagesStorage().getLastSeqValue() < seq ? 1 : 2;
        } else if (type == 1) {
            if (updates.pts <= getMessagesStorage().getLastPtsValue()) {
                return 2;
            }
            return getMessagesStorage().getLastPtsValue() + updates.pts_count == updates.pts ? 0 : 1;
        } else if (type != 2) {
            return 0;
        } else {
            if (updates.pts <= getMessagesStorage().getLastQtsValue()) {
                return 2;
            }
            return getMessagesStorage().getLastQtsValue() + updates.updates.size() == updates.pts ? 0 : 1;
        }
    }

    private void processChannelsUpdatesQueue(long channelId, int state) {
        int updateState;
        ArrayList<TLRPC.Updates> updatesQueue = this.updatesQueueChannels.get(channelId);
        if (updatesQueue == null) {
            return;
        }
        int channelPts = this.channelsPts.get(channelId);
        if (!updatesQueue.isEmpty() && channelPts != 0) {
            Collections.sort(updatesQueue, MessagesController$$ExternalSyntheticLambda139.INSTANCE);
            boolean anyProceed = false;
            if (state == 2) {
                this.channelsPts.put(channelId, updatesQueue.get(0).pts);
            }
            for (int a = 0; a < updatesQueue.size(); a = (a - 1) + 1) {
                TLRPC.Updates updates = updatesQueue.get(a);
                if (updates.pts <= channelPts) {
                    updateState = 2;
                } else if (updates.pts_count + channelPts == updates.pts) {
                    updateState = 0;
                } else {
                    updateState = 1;
                }
                if (updateState == 0) {
                    processUpdates(updates, true);
                    anyProceed = true;
                    updatesQueue.remove(a);
                } else if (updateState == 1) {
                    long updatesStartWaitTime = this.updatesStartWaitTimeChannels.get(channelId);
                    if (updatesStartWaitTime != 0 && (anyProceed || Math.abs(System.currentTimeMillis() - updatesStartWaitTime) <= 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN CHANNEL " + channelId + " UPDATES QUEUE - will wait more time");
                        }
                        if (anyProceed) {
                            this.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
                            return;
                        }
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN CHANNEL " + channelId + " UPDATES QUEUE - getChannelDifference ");
                    }
                    this.updatesStartWaitTimeChannels.delete(channelId);
                    this.updatesQueueChannels.remove(channelId);
                    getChannelDifference(channelId);
                    return;
                } else {
                    updatesQueue.remove(a);
                }
            }
            this.updatesQueueChannels.remove(channelId);
            this.updatesStartWaitTimeChannels.delete(channelId);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("UPDATES CHANNEL " + channelId + " QUEUE PROCEED - OK");
                return;
            }
            return;
        }
        this.updatesQueueChannels.remove(channelId);
    }

    private void processUpdatesQueue(int type, int state) {
        ArrayList<TLRPC.Updates> updatesQueue = null;
        if (type == 0) {
            updatesQueue = this.updatesQueueSeq;
            Collections.sort(updatesQueue, new Comparator() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda137
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return MessagesController.this.m820xd533a8c2((TLRPC.Updates) obj, (TLRPC.Updates) obj2);
                }
            });
        } else if (type == 1) {
            updatesQueue = this.updatesQueuePts;
            Collections.sort(updatesQueue, MessagesController$$ExternalSyntheticLambda140.INSTANCE);
        } else if (type == 2) {
            updatesQueue = this.updatesQueueQts;
            Collections.sort(updatesQueue, MessagesController$$ExternalSyntheticLambda141.INSTANCE);
        }
        if (updatesQueue != null && !updatesQueue.isEmpty()) {
            boolean anyProceed = false;
            if (state == 2) {
                TLRPC.Updates updates = updatesQueue.get(0);
                if (type == 0) {
                    getMessagesStorage().setLastSeqValue(getUpdateSeq(updates));
                } else if (type == 1) {
                    getMessagesStorage().setLastPtsValue(updates.pts);
                } else {
                    getMessagesStorage().setLastQtsValue(updates.pts);
                }
            }
            for (int a = 0; a < updatesQueue.size(); a = (a - 1) + 1) {
                TLRPC.Updates updates2 = updatesQueue.get(a);
                int updateState = isValidUpdate(updates2, type);
                if (updateState == 0) {
                    processUpdates(updates2, true);
                    anyProceed = true;
                    updatesQueue.remove(a);
                } else if (updateState == 1) {
                    if (getUpdatesStartTime(type) != 0 && (anyProceed || Math.abs(System.currentTimeMillis() - getUpdatesStartTime(type)) <= 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN UPDATES QUEUE - will wait more time");
                        }
                        if (anyProceed) {
                            setUpdatesStartTime(type, System.currentTimeMillis());
                            return;
                        }
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN UPDATES QUEUE - getDifference");
                    }
                    setUpdatesStartTime(type, 0L);
                    updatesQueue.clear();
                    getDifference();
                    return;
                } else {
                    updatesQueue.remove(a);
                }
            }
            updatesQueue.clear();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("UPDATES QUEUE PROCEED - OK");
            }
        }
        setUpdatesStartTime(type, 0L);
    }

    /* renamed from: lambda$processUpdatesQueue$260$org-telegram-messenger-MessagesController */
    public /* synthetic */ int m820xd533a8c2(TLRPC.Updates updates, TLRPC.Updates updates2) {
        return AndroidUtilities.compare(getUpdateSeq(updates), getUpdateSeq(updates2));
    }

    public void loadUnknownChannel(final TLRPC.Chat channel, long taskId) {
        final long newTaskId;
        if (!(channel instanceof TLRPC.TL_channel) || this.gettingUnknownChannels.indexOfKey(channel.id) >= 0) {
            return;
        }
        if (channel.access_hash == 0) {
            if (taskId != 0) {
                getMessagesStorage().removePendingTask(taskId);
                return;
            }
            return;
        }
        TLRPC.TL_inputPeerChannel inputPeer = new TLRPC.TL_inputPeerChannel();
        inputPeer.channel_id = channel.id;
        inputPeer.access_hash = channel.access_hash;
        this.gettingUnknownChannels.put(channel.id, true);
        TLRPC.TL_messages_getPeerDialogs req = new TLRPC.TL_messages_getPeerDialogs();
        TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        inputDialogPeer.peer = inputPeer;
        req.peers.add(inputDialogPeer);
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(channel.getObjectSize() + 4);
                data.writeInt32(0);
                channel.serializeToStream(data);
            } catch (Exception e) {
                FileLog.e(e);
            }
            newTaskId = getMessagesStorage().createPendingTask(data);
        } else {
            newTaskId = taskId;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda227
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m740xfd9d2588(newTaskId, channel, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadUnknownChannel$263$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m740xfd9d2588(long newTaskId, TLRPC.Chat channel, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_messages_peerDialogs res = (TLRPC.TL_messages_peerDialogs) response;
            if (!res.dialogs.isEmpty() && !res.chats.isEmpty()) {
                TLRPC.TL_dialog dialog = (TLRPC.TL_dialog) res.dialogs.get(0);
                TLRPC.TL_messages_dialogs dialogs = new TLRPC.TL_messages_dialogs();
                dialogs.dialogs.addAll(res.dialogs);
                dialogs.messages.addAll(res.messages);
                dialogs.users.addAll(res.users);
                dialogs.chats.addAll(res.chats);
                processLoadedDialogs(dialogs, null, dialog.folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_CHANNEL, false, false, false);
            }
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
        this.gettingUnknownChannels.delete(channel.id);
    }

    public void startShortPoll(final TLRPC.Chat chat, final int guid, final boolean stop) {
        if (chat == null) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda57
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m869x409983(chat, stop, guid);
            }
        });
    }

    /* renamed from: lambda$startShortPoll$264$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m869x409983(TLRPC.Chat chat, boolean stop, int guid) {
        ArrayList<Integer> guids = this.needShortPollChannels.get(chat.id);
        ArrayList<Integer> onlineGuids = this.needShortPollOnlines.get(chat.id);
        if (stop) {
            if (guids != null) {
                guids.remove(Integer.valueOf(guid));
            }
            if (guids == null || guids.isEmpty()) {
                this.needShortPollChannels.delete(chat.id);
            }
            if (chat.megagroup) {
                if (onlineGuids != null) {
                    onlineGuids.remove(Integer.valueOf(guid));
                }
                if (onlineGuids == null || onlineGuids.isEmpty()) {
                    this.needShortPollOnlines.delete(chat.id);
                    return;
                }
                return;
            }
            return;
        }
        if (guids == null) {
            guids = new ArrayList<>();
            this.needShortPollChannels.put(chat.id, guids);
        }
        if (!guids.contains(Integer.valueOf(guid))) {
            guids.add(Integer.valueOf(guid));
        }
        if (this.shortPollChannels.indexOfKey(chat.id) < 0) {
            getChannelDifference(chat.id, 3, 0L, null);
        }
        if (chat.megagroup) {
            if (onlineGuids == null) {
                onlineGuids = new ArrayList<>();
                this.needShortPollOnlines.put(chat.id, onlineGuids);
            }
            if (!onlineGuids.contains(Integer.valueOf(guid))) {
                onlineGuids.add(Integer.valueOf(guid));
            }
            if (this.shortPollOnlines.indexOfKey(chat.id) < 0) {
                this.shortPollOnlines.put(chat.id, 0);
            }
        }
    }

    private void getChannelDifference(long channelId) {
        getChannelDifference(channelId, 0, 0L, null);
    }

    public static boolean isSupportUser(TLRPC.User user) {
        return user != null && (user.support || user.id == 777000 || user.id == 333000 || user.id == 4240000 || user.id == 4244000 || user.id == 4245000 || user.id == 4246000 || user.id == 410000 || user.id == 420000 || user.id == 431000 || user.id == 431415000 || user.id == 434000 || user.id == 4243000 || user.id == 439000 || user.id == 449000 || user.id == 450000 || user.id == 452000 || user.id == 454000 || user.id == 4254000 || user.id == 455000 || user.id == 460000 || user.id == 470000 || user.id == 479000 || user.id == 796000 || user.id == 482000 || user.id == 490000 || user.id == 496000 || user.id == 497000 || user.id == 498000 || user.id == 4298000);
    }

    public void getChannelDifference(final long channelId, final int newDialogType, long taskId, TLRPC.InputChannel inputChannel) {
        int limit;
        int channelPts;
        TLRPC.InputChannel inputChannel2;
        long newTaskId;
        boolean z = false;
        boolean gettingDifferenceChannel = this.gettingDifferenceChannels.get(channelId, false).booleanValue();
        if (gettingDifferenceChannel) {
            return;
        }
        if (newDialogType == 1) {
            if (this.channelsPts.get(channelId) != 0) {
                return;
            }
            limit = 1;
            channelPts = 1;
        } else {
            int channelPts2 = this.channelsPts.get(channelId);
            if (channelPts2 == 0) {
                channelPts2 = getMessagesStorage().getChannelPtsSync(channelId);
                if (channelPts2 != 0) {
                    this.channelsPts.put(channelId, channelPts2);
                }
                if (channelPts2 == 0 && (newDialogType == 2 || newDialogType == 3)) {
                    return;
                }
            }
            if (channelPts2 != 0) {
                limit = 100;
                channelPts = channelPts2;
            } else {
                return;
            }
        }
        if (inputChannel == null) {
            TLRPC.Chat chat = getChat(Long.valueOf(channelId));
            if (chat == null && (chat = getMessagesStorage().getChatSync(channelId)) != null) {
                putChat(chat, true);
            }
            inputChannel2 = getInputChannel(chat);
        } else {
            inputChannel2 = inputChannel;
        }
        if (inputChannel2.access_hash == 0) {
            if (taskId != 0) {
                getMessagesStorage().removePendingTask(taskId);
                return;
            }
            return;
        }
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(inputChannel2.getObjectSize() + 16);
                data.writeInt32(25);
                data.writeInt64(channelId);
                data.writeInt32(newDialogType);
                inputChannel2.serializeToStream(data);
            } catch (Exception e) {
                FileLog.e(e);
            }
            long newTaskId2 = getMessagesStorage().createPendingTask(data);
            newTaskId = newTaskId2;
        } else {
            newTaskId = taskId;
        }
        this.gettingDifferenceChannels.put(channelId, true);
        TLRPC.TL_updates_getChannelDifference req = new TLRPC.TL_updates_getChannelDifference();
        req.channel = inputChannel2;
        req.filter = new TLRPC.TL_channelMessagesFilterEmpty();
        req.pts = channelPts;
        req.limit = limit;
        if (newDialogType != 3) {
            z = true;
        }
        req.force = z;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start getChannelDifference with pts = " + channelPts + " channelId = " + channelId);
        }
        final long j = newTaskId;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda212
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m679x2990de72(channelId, newDialogType, j, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$getChannelDifference$273$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m679x2990de72(final long channelId, final int newDialogType, final long newTaskId, TLObject response, final TLRPC.TL_error error) {
        TLRPC.Chat channel;
        if (response == null) {
            if (error != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda69
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m678x444f6fb1(error, channelId);
                    }
                });
                this.gettingDifferenceChannels.delete(channelId);
                if (newTaskId != 0) {
                    getMessagesStorage().removePendingTask(newTaskId);
                    return;
                }
                return;
            }
            return;
        }
        final TLRPC.updates_ChannelDifference res = (TLRPC.updates_ChannelDifference) response;
        final LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        for (int a = 0; a < res.users.size(); a++) {
            TLRPC.User user = res.users.get(a);
            usersDict.put(user.id, user);
        }
        int a2 = 0;
        while (true) {
            if (a2 >= res.chats.size()) {
                channel = null;
                break;
            }
            TLRPC.Chat chat = res.chats.get(a2);
            if (chat.id != channelId) {
                a2++;
            } else {
                channel = chat;
                break;
            }
        }
        final TLRPC.Chat channelFinal = channel;
        final ArrayList<TLRPC.TL_updateMessageID> msgUpdates = new ArrayList<>();
        if (!res.other_updates.isEmpty()) {
            int a3 = 0;
            while (a3 < res.other_updates.size()) {
                TLRPC.Update upd = res.other_updates.get(a3);
                if (upd instanceof TLRPC.TL_updateMessageID) {
                    msgUpdates.add((TLRPC.TL_updateMessageID) upd);
                    res.other_updates.remove(a3);
                    a3--;
                }
                a3++;
            }
        }
        getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda108
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m671x31275295(res);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m677x5f0e00f0(msgUpdates, channelId, res, channelFinal, usersDict, newDialogType, newTaskId);
            }
        });
    }

    /* renamed from: lambda$getChannelDifference$265$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m671x31275295(TLRPC.updates_ChannelDifference res) {
        putUsers(res.users, false);
        putChats(res.chats, false);
    }

    /* renamed from: lambda$getChannelDifference$271$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m677x5f0e00f0(ArrayList msgUpdates, final long channelId, final TLRPC.updates_ChannelDifference res, final TLRPC.Chat channelFinal, final LongSparseArray usersDict, final int newDialogType, final long newTaskId) {
        if (!msgUpdates.isEmpty()) {
            final SparseArray<long[]> corrected = new SparseArray<>();
            Iterator it = msgUpdates.iterator();
            while (it.hasNext()) {
                TLRPC.TL_updateMessageID update = (TLRPC.TL_updateMessageID) it.next();
                long[] ids = getMessagesStorage().updateMessageStateAndId(update.random_id, -channelId, null, update.id, 0, false, -1);
                if (ids != null) {
                    corrected.put(update.id, ids);
                }
            }
            if (corrected.size() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda352
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m672x1668c156(corrected);
                    }
                });
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda109
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m676x79cc922f(res, channelId, channelFinal, usersDict, newDialogType, newTaskId);
            }
        });
    }

    /* renamed from: lambda$getChannelDifference$266$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m672x1668c156(SparseArray corrected) {
        for (int a = 0; a < corrected.size(); a++) {
            int newId = corrected.keyAt(a);
            long[] ids = (long[]) corrected.valueAt(a);
            getSendMessagesHelper().processSentMessage((int) ids[1]);
            getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf((int) ids[1]), Integer.valueOf(newId), null, Long.valueOf(ids[0]), 0L, -1, false);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:100:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0197  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01a6  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0209  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x021d  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0224  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x029a  */
    /* renamed from: lambda$getChannelDifference$270$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m676x79cc922f(final org.telegram.tgnet.TLRPC.updates_ChannelDifference r29, long r30, org.telegram.tgnet.TLRPC.Chat r32, androidx.collection.LongSparseArray r33, int r34, long r35) {
        /*
            Method dump skipped, instructions count: 674
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m676x79cc922f(org.telegram.tgnet.TLRPC$updates_ChannelDifference, long, org.telegram.tgnet.TLRPC$Chat, androidx.collection.LongSparseArray, int, long):void");
    }

    /* renamed from: lambda$getChannelDifference$267$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m673xfbaa3017(LongSparseArray messages) {
        for (int a = 0; a < messages.size(); a++) {
            long key = messages.keyAt(a);
            ArrayList<MessageObject> value = (ArrayList) messages.valueAt(a);
            updateInterfaceWithMessages(key, value, false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* renamed from: lambda$getChannelDifference$269$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m675xc62d0d99(final ArrayList pushMessages, TLRPC.updates_ChannelDifference res) {
        if (!pushMessages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m674xe0eb9ed8(pushMessages);
                }
            });
        }
        getMessagesStorage().putMessages(res.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), false);
    }

    /* renamed from: lambda$getChannelDifference$268$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m674xe0eb9ed8(ArrayList pushMessages) {
        getNotificationsController().processNewMessages(pushMessages, true, false, null);
    }

    /* renamed from: lambda$getChannelDifference$272$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m678x444f6fb1(TLRPC.TL_error error, long channelId) {
        checkChannelError(error.text, channelId);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void checkChannelError(String text, long channelId) {
        char c;
        switch (text.hashCode()) {
            case -1809401834:
                if (text.equals("USER_BANNED_IN_CHANNEL")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -795226617:
                if (text.equals("CHANNEL_PRIVATE")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -471086771:
                if (text.equals("CHANNEL_PUBLIC_GROUP_NA")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                getNotificationCenter().postNotificationName(NotificationCenter.chatInfoCantLoad, Long.valueOf(channelId), 0);
                return;
            case 1:
                getNotificationCenter().postNotificationName(NotificationCenter.chatInfoCantLoad, Long.valueOf(channelId), 1);
                return;
            case 2:
                getNotificationCenter().postNotificationName(NotificationCenter.chatInfoCantLoad, Long.valueOf(channelId), 2);
                return;
            default:
                return;
        }
    }

    public void getDifference() {
        getDifference(getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue(), false);
    }

    public void getDifference(int pts, final int date, final int qts, boolean slice) {
        registerForPush(SharedConfig.pushString);
        if (getMessagesStorage().getLastPtsValue() == 0) {
            loadCurrentState();
        } else if (!slice && this.gettingDifference) {
        } else {
            this.gettingDifference = true;
            TLRPC.TL_updates_getDifference req = new TLRPC.TL_updates_getDifference();
            req.pts = pts;
            req.date = date;
            req.qts = qts;
            if (this.getDifferenceFirstSync) {
                req.flags |= 1;
                if (ApplicationLoader.isConnectedOrConnectingToWiFi()) {
                    req.pts_total_limit = 5000;
                } else {
                    req.pts_total_limit = 1000;
                }
                this.getDifferenceFirstSync = false;
            }
            if (req.date == 0) {
                req.date = getConnectionsManager().getCurrentTime();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start getDifference with date = " + date + " pts = " + pts + " qts = " + qts);
            }
            getConnectionsManager().setIsUpdating(true);
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda179
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m689x3429be00(date, qts, tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$getDifference$283$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m689x3429be00(final int date, final int qts, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            this.gettingDifference = false;
            getConnectionsManager().setIsUpdating(false);
            return;
        }
        final TLRPC.updates_Difference res = (TLRPC.updates_Difference) response;
        if (res instanceof TLRPC.TL_updates_differenceTooLong) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda113
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m680x567ec362(res, date, qts);
                }
            });
            return;
        }
        if (res instanceof TLRPC.TL_updates_differenceSlice) {
            getDifference(res.intermediate_state.pts, res.intermediate_state.date, res.intermediate_state.qts, true);
        }
        final LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        final LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
        for (int a = 0; a < res.users.size(); a++) {
            TLRPC.User user = res.users.get(a);
            usersDict.put(user.id, user);
        }
        for (int a2 = 0; a2 < res.chats.size(); a2++) {
            TLRPC.Chat chat = res.chats.get(a2);
            chatsDict.put(chat.id, chat);
        }
        final ArrayList<TLRPC.TL_updateMessageID> msgUpdates = new ArrayList<>();
        if (!res.other_updates.isEmpty()) {
            int a3 = 0;
            while (a3 < res.other_updates.size()) {
                TLRPC.Update upd = res.other_updates.get(a3);
                if (upd instanceof TLRPC.TL_updateMessageID) {
                    msgUpdates.add((TLRPC.TL_updateMessageID) upd);
                    res.other_updates.remove(a3);
                    a3--;
                } else if (getUpdateType(upd) == 2) {
                    long channelId = getUpdateChannelId(upd);
                    int channelPts = this.channelsPts.get(channelId);
                    if (channelPts == 0 && (channelPts = getMessagesStorage().getChannelPtsSync(channelId)) != 0) {
                        this.channelsPts.put(channelId, channelPts);
                    }
                    if (channelPts != 0 && getUpdatePts(upd) <= channelPts) {
                        res.other_updates.remove(a3);
                        a3--;
                    }
                }
                a3++;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda112
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m681x3bc03223(res);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda115
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m688x4ee84f3f(res, msgUpdates, usersDict, chatsDict);
            }
        });
    }

    /* renamed from: lambda$getDifference$274$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m680x567ec362(TLRPC.updates_Difference res, int date, int qts) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), res.pts, date, qts);
    }

    /* renamed from: lambda$getDifference$275$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m681x3bc03223(TLRPC.updates_Difference res) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        putUsers(res.users, false);
        putChats(res.chats, false);
    }

    /* renamed from: lambda$getDifference$282$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m688x4ee84f3f(final TLRPC.updates_Difference res, ArrayList msgUpdates, final LongSparseArray usersDict, final LongSparseArray chatsDict) {
        getMessagesStorage().putUsersAndChats(res.users, res.chats, true, false);
        if (!msgUpdates.isEmpty()) {
            final SparseArray<long[]> corrected = new SparseArray<>();
            for (int a = 0; a < msgUpdates.size(); a++) {
                TLRPC.TL_updateMessageID update = (TLRPC.TL_updateMessageID) msgUpdates.get(a);
                long[] ids = getMessagesStorage().updateMessageStateAndId(update.random_id, 0L, null, update.id, 0, false, -1);
                if (ids != null) {
                    corrected.put(update.id, ids);
                }
            }
            int a2 = corrected.size();
            if (a2 != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda353
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m682x2101a0e4(corrected);
                    }
                });
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda114
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m687x69a6e07e(res, usersDict, chatsDict);
            }
        });
    }

    /* renamed from: lambda$getDifference$276$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m682x2101a0e4(SparseArray corrected) {
        for (int a = 0; a < corrected.size(); a++) {
            int newId = corrected.keyAt(a);
            long[] ids = (long[]) corrected.valueAt(a);
            getSendMessagesHelper().processSentMessage((int) ids[1]);
            getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf((int) ids[1]), Integer.valueOf(newId), null, Long.valueOf(ids[0]), 0L, -1, false);
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> */
    /* renamed from: lambda$getDifference$281$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m687x69a6e07e(final TLRPC.updates_Difference res, LongSparseArray usersDict, LongSparseArray chatsDict) {
        boolean z;
        TLRPC.User user;
        if (!res.new_messages.isEmpty() || !res.new_encrypted_messages.isEmpty()) {
            final LongSparseArray<ArrayList<MessageObject>> messages = new LongSparseArray<>();
            for (int b = 0; b < res.new_encrypted_messages.size(); b++) {
                TLRPC.EncryptedMessage encryptedMessage = res.new_encrypted_messages.get(b);
                ArrayList<TLRPC.Message> decryptedMessages = getSecretChatHelper().decryptMessage(encryptedMessage);
                if (decryptedMessages != null && !decryptedMessages.isEmpty()) {
                    res.new_messages.addAll(decryptedMessages);
                }
            }
            ImageLoader.saveMessagesThumbs(res.new_messages);
            final ArrayList<MessageObject> pushMessages = new ArrayList<>();
            long clientUserId = getUserConfig().getClientUserId();
            for (int a = 0; a < res.new_messages.size(); a++) {
                TLRPC.Message message = res.new_messages.get(a);
                if (!(message instanceof TLRPC.TL_messageEmpty)) {
                    MessageObject.getDialogId(message);
                    if (!DialogObject.isEncryptedDialog(message.dialog_id)) {
                        if ((message.action instanceof TLRPC.TL_messageActionChatDeleteUser) && (user = (TLRPC.User) usersDict.get(message.action.user_id)) != null && user.bot) {
                            message.reply_markup = new TLRPC.TL_replyKeyboardHide();
                            message.flags |= 64;
                        }
                        if ((message.action instanceof TLRPC.TL_messageActionChatMigrateTo) || (message.action instanceof TLRPC.TL_messageActionChannelCreate)) {
                            z = false;
                            message.unread = false;
                            message.media_unread = false;
                        } else {
                            ConcurrentHashMap<Long, Integer> concurrentHashMap = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                            Integer value = concurrentHashMap.get(Long.valueOf(message.dialog_id));
                            if (value == null) {
                                value = Integer.valueOf(getMessagesStorage().getDialogReadMax(message.out, message.dialog_id));
                                concurrentHashMap.put(Long.valueOf(message.dialog_id), value);
                            }
                            message.unread = value.intValue() < message.id;
                            z = false;
                        }
                    } else {
                        z = false;
                    }
                    if (message.dialog_id == clientUserId) {
                        message.unread = z;
                        message.media_unread = z;
                        message.out = true;
                    }
                    boolean isDialogCreated = this.createdDialogIds.contains(Long.valueOf(message.dialog_id));
                    MessageObject obj = new MessageObject(this.currentAccount, message, usersDict, chatsDict, isDialogCreated, isDialogCreated);
                    if ((!obj.isOut() || obj.messageOwner.from_scheduled) && obj.isUnread()) {
                        pushMessages.add(obj);
                    }
                    ArrayList<MessageObject> arr = messages.get(message.dialog_id);
                    if (arr == null) {
                        arr = new ArrayList<>();
                        messages.put(message.dialog_id, arr);
                    }
                    arr.add(obj);
                }
            }
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m686x846571bd(pushMessages, res, messages);
                }
            });
            getSecretChatHelper().processPendingEncMessages();
        }
        if (!res.other_updates.isEmpty()) {
            processUpdateArray(res.other_updates, res.users, res.chats, true, 0);
        }
        if (res instanceof TLRPC.TL_updates_difference) {
            this.gettingDifference = false;
            getMessagesStorage().setLastSeqValue(res.state.seq);
            getMessagesStorage().setLastDateValue(res.state.date);
            getMessagesStorage().setLastPtsValue(res.state.pts);
            getMessagesStorage().setLastQtsValue(res.state.qts);
            getConnectionsManager().setIsUpdating(false);
            for (int a2 = 0; a2 < 3; a2++) {
                processUpdatesQueue(a2, 1);
            }
        } else if (res instanceof TLRPC.TL_updates_differenceSlice) {
            getMessagesStorage().setLastDateValue(res.intermediate_state.date);
            getMessagesStorage().setLastPtsValue(res.intermediate_state.pts);
            getMessagesStorage().setLastQtsValue(res.intermediate_state.qts);
        } else if (res instanceof TLRPC.TL_updates_differenceEmpty) {
            this.gettingDifference = false;
            getMessagesStorage().setLastSeqValue(res.seq);
            getMessagesStorage().setLastDateValue(res.date);
            getConnectionsManager().setIsUpdating(false);
            for (int a3 = 0; a3 < 3; a3++) {
                processUpdatesQueue(a3, 1);
            }
        }
        getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("received difference with date = " + getMessagesStorage().getLastDateValue() + " pts = " + getMessagesStorage().getLastPtsValue() + " seq = " + getMessagesStorage().getLastSeqValue() + " messages = " + res.new_messages.size() + " users = " + res.users.size() + " chats = " + res.chats.size() + " other updates = " + res.other_updates.size());
        }
    }

    /* renamed from: lambda$getDifference$280$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m686x846571bd(final ArrayList pushMessages, final TLRPC.updates_Difference res, LongSparseArray messages) {
        if (!pushMessages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m683x6430fa5(pushMessages, res);
                }
            });
        }
        getMessagesStorage().putMessages(res.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), false);
        for (int a = 0; a < messages.size(); a++) {
            final long dialogId = messages.keyAt(a);
            final ArrayList<MessageObject> arr = (ArrayList) messages.valueAt(a);
            getMediaDataController().loadReplyMessagesForMessages(arr, dialogId, false, new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda343
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m685xd0c5ed27(dialogId, arr);
                }
            });
        }
    }

    /* renamed from: lambda$getDifference$277$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m683x6430fa5(ArrayList pushMessages, TLRPC.updates_Difference res) {
        getNotificationsController().processNewMessages(pushMessages, !(res instanceof TLRPC.TL_updates_differenceSlice), false, null);
    }

    /* renamed from: lambda$getDifference$279$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m685xd0c5ed27(final long dialogId, final ArrayList arr) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda342
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m684xeb847e66(dialogId, arr);
            }
        });
    }

    /* renamed from: lambda$getDifference$278$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m684xeb847e66(long dialogId, ArrayList arr) {
        updateInterfaceWithMessages(dialogId, arr, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void markDialogAsUnread(long dialogId, TLRPC.InputPeer peer, long taskId) {
        final long newTaskId;
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog != null) {
            dialog.unread_mark = true;
            if (dialog.unread_count == 0 && !isDialogMuted(dialogId)) {
                this.unreadUnmutedDialogs++;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
            getMessagesStorage().setDialogUnread(dialogId, true);
            int b = 0;
            while (true) {
                DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                if (b < dialogFilterArr.length) {
                    if (dialogFilterArr[b] == null || (dialogFilterArr[b].flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0) {
                        b++;
                    } else {
                        sortDialogs(null);
                        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (!DialogObject.isEncryptedDialog(dialogId)) {
            TLRPC.TL_messages_markDialogUnread req = new TLRPC.TL_messages_markDialogUnread();
            req.unread = true;
            if (peer == null) {
                peer = getInputPeer(dialogId);
            }
            if (peer instanceof TLRPC.TL_inputPeerEmpty) {
                return;
            }
            TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            inputDialogPeer.peer = peer;
            req.peer = inputDialogPeer;
            if (taskId == 0) {
                NativeByteBuffer data = null;
                try {
                    data = new NativeByteBuffer(peer.getObjectSize() + 12);
                    data.writeInt32(9);
                    data.writeInt64(dialogId);
                    peer.serializeToStream(data);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                newTaskId = getMessagesStorage().createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda197
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m750x8e1bb6fe(newTaskId, tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$markDialogAsUnread$284$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m750x8e1bb6fe(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void loadUnreadDialogs() {
        if (this.loadingUnreadDialogs || getUserConfig().unreadDialogsLoaded) {
            return;
        }
        this.loadingUnreadDialogs = true;
        TLRPC.TL_messages_getDialogUnreadMarks req = new TLRPC.TL_messages_getDialogUnreadMarks();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda161
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m743xa3667486(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadUnreadDialogs$286$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m743xa3667486(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda45
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m742xbe2505c5(response);
            }
        });
    }

    /* renamed from: lambda$loadUnreadDialogs$285$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m742xbe2505c5(TLObject response) {
        long did;
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            int size = vector.objects.size();
            for (int a = 0; a < size; a++) {
                TLRPC.DialogPeer peer = (TLRPC.DialogPeer) vector.objects.get(a);
                if (peer instanceof TLRPC.TL_dialogPeer) {
                    TLRPC.TL_dialogPeer dialogPeer = (TLRPC.TL_dialogPeer) peer;
                    if (dialogPeer.peer.user_id != 0) {
                        did = dialogPeer.peer.user_id;
                    } else if (dialogPeer.peer.chat_id != 0) {
                        did = -dialogPeer.peer.chat_id;
                    } else {
                        did = -dialogPeer.peer.channel_id;
                    }
                    getMessagesStorage().setDialogUnread(did, true);
                    TLRPC.Dialog dialog = this.dialogs_dict.get(did);
                    if (dialog != null && !dialog.unread_mark) {
                        dialog.unread_mark = true;
                        if (dialog.unread_count == 0 && !isDialogMuted(did)) {
                            this.unreadUnmutedDialogs++;
                        }
                    }
                }
            }
            getUserConfig().unreadDialogsLoaded = true;
            getUserConfig().saveConfig(false);
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
            this.loadingUnreadDialogs = false;
        }
    }

    public void reorderPinnedDialogs(int folderId, ArrayList<TLRPC.InputDialogPeer> order, long taskId) {
        final long newTaskId;
        TLRPC.TL_messages_reorderPinnedDialogs req = new TLRPC.TL_messages_reorderPinnedDialogs();
        req.folder_id = folderId;
        req.force = true;
        if (taskId == 0) {
            ArrayList<TLRPC.Dialog> dialogs = getDialogs(folderId);
            if (dialogs.isEmpty()) {
                return;
            }
            ArrayList<Long> dids = new ArrayList<>();
            ArrayList<Integer> pinned = new ArrayList<>();
            int N = dialogs.size();
            int size = 0;
            for (int size2 = 0; size2 < N; size2++) {
                TLRPC.Dialog dialog = dialogs.get(size2);
                if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                    if (!dialog.pinned) {
                        if (dialog.id != this.promoDialogId) {
                            break;
                        }
                    } else {
                        dids.add(Long.valueOf(dialog.id));
                        pinned.add(Integer.valueOf(dialog.pinnedNum));
                        if (!DialogObject.isEncryptedDialog(dialog.id)) {
                            TLRPC.InputPeer inputPeer = getInputPeer(dialog.id);
                            TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                            inputDialogPeer.peer = inputPeer;
                            req.order.add(inputDialogPeer);
                            size += inputDialogPeer.getObjectSize();
                        }
                    }
                }
            }
            getMessagesStorage().setDialogsPinned(dids, pinned);
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(size + 12);
                data.writeInt32(16);
                data.writeInt32(folderId);
                data.writeInt32(req.order.size());
                int N2 = req.order.size();
                for (int a = 0; a < N2; a++) {
                    req.order.get(a).serializeToStream(data);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            long newTaskId2 = getMessagesStorage().createPendingTask(data);
            newTaskId = newTaskId2;
        } else {
            req.order = order;
            newTaskId = taskId;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda202
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m834x4d3d5863(newTaskId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$reorderPinnedDialogs$287$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m834x4d3d5863(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public boolean pinDialog(long dialogId, boolean pin, TLRPC.InputPeer peer, long taskId) {
        TLRPC.InputPeer peer2;
        final long newTaskId;
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog == null || dialog.pinned == pin) {
            return dialog != null;
        }
        int folderId = dialog.folder_id;
        ArrayList<TLRPC.Dialog> dialogs = getDialogs(folderId);
        dialog.pinned = pin;
        if (pin) {
            int maxPinnedNum = 0;
            for (int a = 0; a < dialogs.size(); a++) {
                TLRPC.Dialog d = dialogs.get(a);
                if (!(d instanceof TLRPC.TL_dialogFolder)) {
                    if (!d.pinned) {
                        if (d.id != this.promoDialogId) {
                            break;
                        }
                    } else {
                        maxPinnedNum = Math.max(d.pinnedNum, maxPinnedNum);
                    }
                }
            }
            int a2 = maxPinnedNum + 1;
            dialog.pinnedNum = a2;
        } else {
            dialog.pinnedNum = 0;
        }
        sortDialogs(null);
        if (!pin && !dialogs.isEmpty() && dialogs.get(dialogs.size() - 1) == dialog && !this.dialogsEndReached.get(folderId)) {
            dialogs.remove(dialogs.size() - 1);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        if (!DialogObject.isEncryptedDialog(dialogId) && taskId != -1) {
            TLRPC.TL_messages_toggleDialogPin req = new TLRPC.TL_messages_toggleDialogPin();
            req.pinned = pin;
            if (peer != null) {
                peer2 = peer;
            } else {
                peer2 = getInputPeer(dialogId);
            }
            if (peer2 instanceof TLRPC.TL_inputPeerEmpty) {
                return false;
            }
            TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            inputDialogPeer.peer = peer2;
            req.peer = inputDialogPeer;
            if (taskId == 0) {
                NativeByteBuffer data = null;
                try {
                    data = new NativeByteBuffer(peer2.getObjectSize() + 16);
                    data.writeInt32(4);
                    data.writeInt64(dialogId);
                    data.writeBool(pin);
                    peer2.serializeToStream(data);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                newTaskId = getMessagesStorage().createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda201
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m771lambda$pinDialog$288$orgtelegrammessengerMessagesController(newTaskId, tLObject, tL_error);
                }
            });
        }
        getMessagesStorage().setDialogPinned(dialogId, dialog.pinnedNum);
        return true;
    }

    /* renamed from: lambda$pinDialog$288$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m771lambda$pinDialog$288$orgtelegrammessengerMessagesController(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void loadPinnedDialogs(final int folderId, long newDialogId, ArrayList<Long> order) {
        if (this.loadingPinnedDialogs.indexOfKey(folderId) >= 0 || getUserConfig().isPinnedDialogsLoaded(folderId)) {
            return;
        }
        this.loadingPinnedDialogs.put(folderId, 1);
        TLRPC.TL_messages_getPinnedDialogs req = new TLRPC.TL_messages_getPinnedDialogs();
        req.folder_id = folderId;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda176
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m732xf1c75b97(folderId, tLObject, tL_error);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:58:0x016c  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x019c  */
    /* renamed from: lambda$loadPinnedDialogs$291$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m732xf1c75b97(final int r20, org.telegram.tgnet.TLObject r21, org.telegram.tgnet.TLRPC.TL_error r22) {
        /*
            Method dump skipped, instructions count: 481
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m732xf1c75b97(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* renamed from: lambda$loadPinnedDialogs$290$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m731xc85ecd6(final int folderId, final ArrayList newPinnedDialogs, final boolean firstIsFolder, final TLRPC.TL_messages_peerDialogs res, final LongSparseArray new_dialogMessage, final TLRPC.TL_messages_dialogs toCache) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda307
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m730x58e66840(folderId, newPinnedDialogs, firstIsFolder, res, new_dialogMessage, toCache);
            }
        });
    }

    /* renamed from: lambda$loadPinnedDialogs$289$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m730x58e66840(int folderId, ArrayList newPinnedDialogs, boolean firstIsFolder, TLRPC.TL_messages_peerDialogs res, LongSparseArray new_dialogMessage, TLRPC.TL_messages_dialogs toCache) {
        int maxPinnedNum;
        ArrayList arrayList = newPinnedDialogs;
        this.loadingPinnedDialogs.delete(folderId);
        applyDialogsNotificationsSettings(arrayList);
        boolean changed = false;
        boolean added = false;
        int maxPinnedNum2 = 0;
        ArrayList<TLRPC.Dialog> dialogs = getDialogs(folderId);
        int pinnedNum = firstIsFolder ? 1 : 0;
        for (int a = 0; a < dialogs.size(); a++) {
            TLRPC.Dialog dialog = dialogs.get(a);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                if (DialogObject.isEncryptedDialog(dialog.id)) {
                    if (pinnedNum < newPinnedDialogs.size()) {
                        arrayList.add(pinnedNum, dialog);
                    } else {
                        arrayList.add(dialog);
                    }
                    pinnedNum++;
                } else if (!dialog.pinned) {
                    if (dialog.id != this.promoDialogId) {
                        break;
                    }
                } else {
                    maxPinnedNum2 = Math.max(dialog.pinnedNum, maxPinnedNum2);
                    dialog.pinned = false;
                    dialog.pinnedNum = 0;
                    changed = true;
                    pinnedNum++;
                }
            }
        }
        ArrayList<Long> pinnedDialogs = new ArrayList<>();
        if (!newPinnedDialogs.isEmpty()) {
            putUsers(res.users, false);
            putChats(res.chats, false);
            ArrayList<Long> dids = new ArrayList<>();
            ArrayList<Integer> pinned = new ArrayList<>();
            int a2 = 0;
            int N = newPinnedDialogs.size();
            while (a2 < N) {
                TLRPC.Dialog dialog2 = (TLRPC.Dialog) arrayList.get(a2);
                dialog2.pinnedNum = (N - a2) + maxPinnedNum2;
                pinnedDialogs.add(Long.valueOf(dialog2.id));
                TLRPC.Dialog d = this.dialogs_dict.get(dialog2.id);
                if (d != null) {
                    d.pinned = true;
                    d.pinnedNum = dialog2.pinnedNum;
                    dids.add(Long.valueOf(dialog2.id));
                    pinned.add(Integer.valueOf(dialog2.pinnedNum));
                    maxPinnedNum = maxPinnedNum2;
                } else {
                    this.dialogs_dict.put(dialog2.id, dialog2);
                    MessageObject messageObject = (MessageObject) new_dialogMessage.get(dialog2.id);
                    maxPinnedNum = maxPinnedNum2;
                    this.dialogMessage.put(dialog2.id, messageObject);
                    if (messageObject != null && messageObject.messageOwner.peer_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                        this.dialogsLoadedTillDate = Math.min(this.dialogsLoadedTillDate, messageObject.messageOwner.date);
                        if (messageObject.messageOwner.random_id != 0) {
                            this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                        }
                    }
                    added = true;
                }
                changed = true;
                a2++;
                arrayList = newPinnedDialogs;
                maxPinnedNum2 = maxPinnedNum;
            }
            getMessagesStorage().setDialogsPinned(dids, pinned);
        }
        if (changed) {
            if (added) {
                this.allDialogs.clear();
                int size = this.dialogs_dict.size();
                for (int a3 = 0; a3 < size; a3++) {
                    TLRPC.Dialog dialog3 = this.dialogs_dict.valueAt(a3);
                    if (this.deletingDialogs.indexOfKey(dialog3.id) < 0) {
                        this.allDialogs.add(dialog3);
                    }
                }
            }
            sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getMessagesStorage().unpinAllDialogsExceptNew(pinnedDialogs, folderId);
        getMessagesStorage().putDialogs(toCache, 1);
        getUserConfig().setPinnedDialogsLoaded(folderId, true);
        getUserConfig().saveConfig(false);
    }

    public void generateJoinMessage(final long chatId, boolean ignoreLeft) {
        TLRPC.Chat chat = getChat(Long.valueOf(chatId));
        if (chat == null || !ChatObject.isChannel(chatId, this.currentAccount)) {
            return;
        }
        if ((chat.left || chat.kicked) && !ignoreLeft) {
            return;
        }
        TLRPC.TL_messageService message = new TLRPC.TL_messageService();
        message.flags = 256;
        int newMessageId = getUserConfig().getNewMessageId();
        message.id = newMessageId;
        message.local_id = newMessageId;
        message.date = getConnectionsManager().getCurrentTime();
        message.from_id = new TLRPC.TL_peerUser();
        message.from_id.user_id = getUserConfig().getClientUserId();
        message.peer_id = new TLRPC.TL_peerChannel();
        message.peer_id.channel_id = chatId;
        message.dialog_id = -chatId;
        message.post = true;
        message.action = new TLRPC.TL_messageActionChatAddUser();
        message.action.users.add(Long.valueOf(getUserConfig().getClientUserId()));
        getUserConfig().saveConfig(false);
        final ArrayList<MessageObject> pushMessages = new ArrayList<>();
        ArrayList<TLRPC.Message> messagesArr = new ArrayList<>();
        messagesArr.add(message);
        MessageObject obj = new MessageObject(this.currentAccount, message, true, false);
        pushMessages.add(obj);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m666x1d2a75f4(pushMessages);
            }
        });
        getMessagesStorage().putMessages(messagesArr, true, true, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda341
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m667x26be4b5(chatId, pushMessages);
            }
        });
    }

    /* renamed from: lambda$generateJoinMessage$292$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m665x37e90733(ArrayList pushMessages) {
        getNotificationsController().processNewMessages(pushMessages, true, false, null);
    }

    /* renamed from: lambda$generateJoinMessage$293$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m666x1d2a75f4(final ArrayList pushMessages) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m665x37e90733(pushMessages);
            }
        });
    }

    /* renamed from: lambda$generateJoinMessage$294$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m667x26be4b5(long chatId, ArrayList pushMessages) {
        updateInterfaceWithMessages(-chatId, pushMessages, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void deleteMessagesByPush(final long dialogId, final ArrayList<Integer> ids, final long channelId) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m642x600cc611(ids, channelId, dialogId);
            }
        });
    }

    /* renamed from: lambda$deleteMessagesByPush$296$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m642x600cc611(final ArrayList ids, final long channelId, long dialogId) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m641x7acb5750(ids, channelId);
            }
        });
        getMessagesStorage().deletePushMessages(dialogId, ids);
        ArrayList<Long> dialogIds = getMessagesStorage().markMessagesAsDeleted(dialogId, ids, false, true, false);
        getMessagesStorage().updateDialogsWithDeletedMessages(dialogId, channelId, ids, dialogIds, false);
    }

    /* renamed from: lambda$deleteMessagesByPush$295$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m641x7acb5750(ArrayList ids, long channelId) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, ids, Long.valueOf(channelId), false);
        if (channelId == 0) {
            int size2 = ids.size();
            for (int b = 0; b < size2; b++) {
                Integer id = (Integer) ids.get(b);
                MessageObject obj = this.dialogMessagesByIds.get(id.intValue());
                if (obj != null) {
                    obj.deleted = true;
                }
            }
            return;
        }
        MessageObject obj2 = this.dialogMessage.get(-channelId);
        if (obj2 != null) {
            int size22 = ids.size();
            for (int b2 = 0; b2 < size22; b2++) {
                if (obj2.getId() == ((Integer) ids.get(b2)).intValue()) {
                    obj2.deleted = true;
                    return;
                }
            }
        }
    }

    public void checkChatInviter(final long chatId, final boolean createMessage) {
        final TLRPC.Chat chat = getChat(Long.valueOf(chatId));
        if (!ChatObject.isChannel(chat) || chat.creator || this.gettingChatInviters.indexOfKey(chatId) >= 0) {
            return;
        }
        this.gettingChatInviters.put(chatId, true);
        TLRPC.TL_channels_getParticipant req = new TLRPC.TL_channels_getParticipant();
        req.channel = getInputChannel(chatId);
        req.participant = getInputPeer(getUserConfig().getClientUserId());
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda247
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m596x7a106617(chat, createMessage, chatId, tLObject, tL_error);
            }
        });
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$User> */
    /* renamed from: lambda$checkChatInviter$301$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m596x7a106617(TLRPC.Chat chat, boolean createMessage, final long chatId, TLObject response, TLRPC.TL_error error) {
        ArrayList<MessageObject> pushMessages;
        final TLRPC.TL_channels_channelParticipant res = (TLRPC.TL_channels_channelParticipant) response;
        if (res != null && (res.participant instanceof TLRPC.TL_channelParticipantSelf)) {
            TLRPC.TL_channelParticipantSelf selfParticipant = (TLRPC.TL_channelParticipantSelf) res.participant;
            if (selfParticipant.inviter_id != getUserConfig().getClientUserId() || selfParticipant.via_invite) {
                if (!chat.megagroup || !getMessagesStorage().isMigratedChat(chat.id)) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda63
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesController.this.m592x1947f073(res);
                        }
                    });
                    getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                    if (createMessage && Math.abs(getConnectionsManager().getCurrentTime() - res.participant.date) < 86400 && !getMessagesStorage().hasInviteMeMessage(chatId)) {
                        TLRPC.TL_messageService message = new TLRPC.TL_messageService();
                        message.media_unread = true;
                        message.unread = true;
                        message.flags = 256;
                        message.post = true;
                        int newMessageId = getUserConfig().getNewMessageId();
                        message.id = newMessageId;
                        message.local_id = newMessageId;
                        message.date = res.participant.date;
                        if (selfParticipant.inviter_id != getUserConfig().getClientUserId()) {
                            message.action = new TLRPC.TL_messageActionChatAddUser();
                        } else if (selfParticipant.via_invite) {
                            message.action = new TLRPC.TL_messageActionChatJoinedByRequest();
                        }
                        message.from_id = new TLRPC.TL_peerUser();
                        message.from_id.user_id = res.participant.inviter_id;
                        message.action.users.add(Long.valueOf(getUserConfig().getClientUserId()));
                        message.peer_id = new TLRPC.TL_peerChannel();
                        message.peer_id.channel_id = chatId;
                        message.dialog_id = -chatId;
                        getUserConfig().saveConfig(false);
                        final ArrayList<MessageObject> pushMessages2 = new ArrayList<>();
                        ArrayList<TLRPC.Message> messagesArr = new ArrayList<>();
                        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
                        for (int a = 0; a < res.users.size(); a++) {
                            TLRPC.User user = res.users.get(a);
                            concurrentHashMap.put(Long.valueOf(user.id), user);
                        }
                        messagesArr.add(message);
                        MessageObject obj = new MessageObject(this.currentAccount, (TLRPC.Message) message, (AbstractMap<Long, TLRPC.User>) concurrentHashMap, true, false);
                        pushMessages2.add(obj);
                        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda7
                            @Override // java.lang.Runnable
                            public final void run() {
                                MessagesController.this.m594xe3cacdf5(pushMessages2);
                            }
                        });
                        getMessagesStorage().putMessages(messagesArr, true, true, false, 0, false);
                        pushMessages = pushMessages2;
                    } else {
                        pushMessages = null;
                    }
                    getMessagesStorage().saveChatInviter(chatId, res.participant.inviter_id);
                    final ArrayList<MessageObject> arrayList = pushMessages;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda346
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesController.this.m595x94cef756(chatId, arrayList, res);
                        }
                    });
                }
            }
        }
    }

    /* renamed from: lambda$checkChatInviter$297$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m592x1947f073(TLRPC.TL_channels_channelParticipant res) {
        putUsers(res.users, false);
        putChats(res.chats, false);
    }

    /* renamed from: lambda$checkChatInviter$298$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m593xfe895f34(ArrayList pushMessages) {
        getNotificationsController().processNewMessages(pushMessages, true, false, null);
    }

    /* renamed from: lambda$checkChatInviter$299$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m594xe3cacdf5(final ArrayList pushMessages) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m593xfe895f34(pushMessages);
            }
        });
    }

    /* renamed from: lambda$checkChatInviter$300$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m595x94cef756(long chatId, ArrayList pushMessages, TLRPC.TL_channels_channelParticipant res) {
        this.gettingChatInviters.delete(chatId);
        if (pushMessages != null) {
            updateInterfaceWithMessages(-chatId, pushMessages, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadChatInviter, Long.valueOf(chatId), Long.valueOf(res.participant.inviter_id));
    }

    private int getUpdateType(TLRPC.Update update) {
        if ((update instanceof TLRPC.TL_updateNewMessage) || (update instanceof TLRPC.TL_updateReadMessagesContents) || (update instanceof TLRPC.TL_updateReadHistoryInbox) || (update instanceof TLRPC.TL_updateReadHistoryOutbox) || (update instanceof TLRPC.TL_updateDeleteMessages) || (update instanceof TLRPC.TL_updateWebPage) || (update instanceof TLRPC.TL_updateEditMessage) || (update instanceof TLRPC.TL_updateFolderPeers) || (update instanceof TLRPC.TL_updatePinnedMessages)) {
            return 0;
        }
        if (update instanceof TLRPC.TL_updateNewEncryptedMessage) {
            return 1;
        }
        if ((update instanceof TLRPC.TL_updateNewChannelMessage) || (update instanceof TLRPC.TL_updateDeleteChannelMessages) || (update instanceof TLRPC.TL_updateEditChannelMessage) || (update instanceof TLRPC.TL_updateChannelWebPage) || (update instanceof TLRPC.TL_updatePinnedChannelMessages)) {
            return 2;
        }
        return 3;
    }

    private static int getUpdatePts(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateDeleteMessages) {
            return ((TLRPC.TL_updateDeleteMessages) update).pts;
        }
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryOutbox) {
            return ((TLRPC.TL_updateReadHistoryOutbox) update).pts;
        }
        if (update instanceof TLRPC.TL_updateNewMessage) {
            return ((TLRPC.TL_updateNewMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateEditMessage) {
            return ((TLRPC.TL_updateEditMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateWebPage) {
            return ((TLRPC.TL_updateWebPage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryInbox) {
            return ((TLRPC.TL_updateReadHistoryInbox) update).pts;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).pts;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).pts;
        }
        if (update instanceof TLRPC.TL_updateReadMessagesContents) {
            return ((TLRPC.TL_updateReadMessagesContents) update).pts;
        }
        if (update instanceof TLRPC.TL_updateChannelTooLong) {
            return ((TLRPC.TL_updateChannelTooLong) update).pts;
        }
        if (update instanceof TLRPC.TL_updateFolderPeers) {
            return ((TLRPC.TL_updateFolderPeers) update).pts;
        }
        if (update instanceof TLRPC.TL_updatePinnedChannelMessages) {
            return ((TLRPC.TL_updatePinnedChannelMessages) update).pts;
        }
        if (update instanceof TLRPC.TL_updatePinnedMessages) {
            return ((TLRPC.TL_updatePinnedMessages) update).pts;
        }
        return 0;
    }

    private static int getUpdatePtsCount(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateDeleteMessages) {
            return ((TLRPC.TL_updateDeleteMessages) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryOutbox) {
            return ((TLRPC.TL_updateReadHistoryOutbox) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateNewMessage) {
            return ((TLRPC.TL_updateNewMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateEditMessage) {
            return ((TLRPC.TL_updateEditMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateWebPage) {
            return ((TLRPC.TL_updateWebPage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadHistoryInbox) {
            return ((TLRPC.TL_updateReadHistoryInbox) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateReadMessagesContents) {
            return ((TLRPC.TL_updateReadMessagesContents) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updateFolderPeers) {
            return ((TLRPC.TL_updateFolderPeers) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updatePinnedChannelMessages) {
            return ((TLRPC.TL_updatePinnedChannelMessages) update).pts_count;
        }
        if (update instanceof TLRPC.TL_updatePinnedMessages) {
            return ((TLRPC.TL_updatePinnedMessages) update).pts_count;
        }
        return 0;
    }

    private static int getUpdateQts(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateNewEncryptedMessage) {
            return ((TLRPC.TL_updateNewEncryptedMessage) update).qts;
        }
        return 0;
    }

    public static long getUpdateChannelId(TLRPC.Update update) {
        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
            return ((TLRPC.TL_updateNewChannelMessage) update).message.peer_id.channel_id;
        }
        if (update instanceof TLRPC.TL_updateEditChannelMessage) {
            return ((TLRPC.TL_updateEditChannelMessage) update).message.peer_id.channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelOutbox) {
            return ((TLRPC.TL_updateReadChannelOutbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelMessageViews) {
            return ((TLRPC.TL_updateChannelMessageViews) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelMessageForwards) {
            return ((TLRPC.TL_updateChannelMessageForwards) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelTooLong) {
            return ((TLRPC.TL_updateChannelTooLong) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelReadMessagesContents) {
            return ((TLRPC.TL_updateChannelReadMessagesContents) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelAvailableMessages) {
            return ((TLRPC.TL_updateChannelAvailableMessages) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannel) {
            return ((TLRPC.TL_updateChannel) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelWebPage) {
            return ((TLRPC.TL_updateChannelWebPage) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateDeleteChannelMessages) {
            return ((TLRPC.TL_updateDeleteChannelMessages) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelInbox) {
            return ((TLRPC.TL_updateReadChannelInbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelDiscussionInbox) {
            return ((TLRPC.TL_updateReadChannelDiscussionInbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateReadChannelDiscussionOutbox) {
            return ((TLRPC.TL_updateReadChannelDiscussionOutbox) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updateChannelUserTyping) {
            return ((TLRPC.TL_updateChannelUserTyping) update).channel_id;
        }
        if (update instanceof TLRPC.TL_updatePinnedChannelMessages) {
            return ((TLRPC.TL_updatePinnedChannelMessages) update).channel_id;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("trying to get unknown update channel_id for " + update);
            return 0L;
        }
        return 0L;
    }

    /* JADX WARN: Code restructure failed: missing block: B:180:0x04ab, code lost:
        if ((r0.pts_count + r3) != r0.pts) goto L193;
     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x04c9, code lost:
        if (processUpdateArray(r0.updates, r41.users, r41.chats, false, r41.date) != false) goto L192;
     */
    /* JADX WARN: Code restructure failed: missing block: B:184:0x04cd, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L186;
     */
    /* JADX WARN: Code restructure failed: missing block: B:185:0x04cf, code lost:
        org.telegram.messenger.FileLog.d("need get channel diff inner TL_updates, channel_id = " + r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:186:0x04e3, code lost:
        if (r9 != null) goto L188;
     */
    /* JADX WARN: Code restructure failed: missing block: B:187:0x04e5, code lost:
        r9 = new java.util.ArrayList<>();
        r14 = r19;
        r2 = r13;
        r10 = r26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:189:0x04fb, code lost:
        if (r9.contains(java.lang.Long.valueOf(r4)) != false) goto L191;
     */
    /* JADX WARN: Code restructure failed: missing block: B:190:0x04fd, code lost:
        r9.add(java.lang.Long.valueOf(r4));
        r2 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:191:0x0508, code lost:
        r2 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:192:0x050c, code lost:
        r40.channelsPts.put(r4, r0.pts);
        getMessagesStorage().saveChannelPts(r4, r0.pts);
        r2 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:193:0x0522, code lost:
        r8 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:194:0x0529, code lost:
        if (r8 == r0.pts) goto L222;
     */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x052d, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L198;
     */
    /* JADX WARN: Code restructure failed: missing block: B:197:0x052f, code lost:
        r0 = new java.lang.StringBuilder();
        r0.append(r15);
        r0.append(" need get channel diff, pts: ");
        r0.append(r8);
        r0.append(r11);
        r0.append(r0.pts);
        r2 = r13;
        r0.append(r2);
        r0.append(r0.pts_count);
        r0.append(" channelId = ");
        r0.append(r4);
        org.telegram.messenger.FileLog.d(r0.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:198:0x0561, code lost:
        r2 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x0563, code lost:
        r3 = r40.updatesStartWaitTimeChannels.get(r4);
        r0 = r40.gettingDifferenceChannels.get(r4, false).booleanValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:200:0x057a, code lost:
        if (r0 != false) goto L210;
     */
    /* JADX WARN: Code restructure failed: missing block: B:202:0x0580, code lost:
        if (r3 == 0) goto L210;
     */
    /* JADX WARN: Code restructure failed: missing block: B:204:0x0590, code lost:
        if (java.lang.Math.abs(java.lang.System.currentTimeMillis() - r3) > 1500) goto L205;
     */
    /* JADX WARN: Code restructure failed: missing block: B:205:0x0593, code lost:
        if (r9 != null) goto L207;
     */
    /* JADX WARN: Code restructure failed: missing block: B:206:0x0595, code lost:
        r9 = new java.util.ArrayList<>();
     */
    /* JADX WARN: Code restructure failed: missing block: B:208:0x05a4, code lost:
        if (r9.contains(java.lang.Long.valueOf(r4)) != false) goto L221;
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x05a6, code lost:
        r9.add(java.lang.Long.valueOf(r4));
     */
    /* JADX WARN: Code restructure failed: missing block: B:211:0x05b2, code lost:
        if (r3 != 0) goto L213;
     */
    /* JADX WARN: Code restructure failed: missing block: B:212:0x05b4, code lost:
        r40.updatesStartWaitTimeChannels.put(r4, java.lang.System.currentTimeMillis());
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x05c4, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L217;
     */
    /* JADX WARN: Code restructure failed: missing block: B:216:0x05c6, code lost:
        org.telegram.messenger.FileLog.d(r28);
     */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x05c9, code lost:
        r3 = r40.updatesQueueChannels.get(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:218:0x05d1, code lost:
        if (r3 != null) goto L220;
     */
    /* JADX WARN: Code restructure failed: missing block: B:219:0x05d3, code lost:
        r3 = new java.util.ArrayList<>();
        r40.updatesQueueChannels.put(r4, r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:220:0x05de, code lost:
        r3.add(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x05e2, code lost:
        r14 = r19;
        r10 = r26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:222:0x05e7, code lost:
        r2 = r13;
     */
    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void processUpdates(final org.telegram.tgnet.TLRPC.Updates r41, boolean r42) {
        /*
            Method dump skipped, instructions count: 3067
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdates(org.telegram.tgnet.TLRPC$Updates, boolean):void");
    }

    /* renamed from: lambda$processUpdates$302$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m815x8399960a(boolean printUpdate, long userId, ArrayList objArr) {
        if (printUpdate) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
        }
        updateInterfaceWithMessages(userId, objArr, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* renamed from: lambda$processUpdates$303$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m816x68db04cb(boolean printUpdate, TLRPC.Updates updates, ArrayList objArr) {
        if (printUpdate) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
        }
        updateInterfaceWithMessages(-updates.chat_id, objArr, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* renamed from: lambda$processUpdates$304$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m817x4e1c738c(ArrayList objArr) {
        getNotificationsController().processNewMessages(objArr, true, false, null);
    }

    /* renamed from: lambda$processUpdates$305$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m818x335de24d(final ArrayList objArr) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m817x4e1c738c(objArr);
            }
        });
    }

    public static /* synthetic */ void lambda$processUpdates$306(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$processUpdates$307$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m819xfde0bfcf() {
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    private boolean applyFoldersUpdates(ArrayList<TLRPC.TL_updateFolderPeers> folderUpdates) {
        if (folderUpdates == null) {
            return false;
        }
        boolean updated = false;
        int size = folderUpdates.size();
        for (int a = 0; a < size; a++) {
            TLRPC.TL_updateFolderPeers update = folderUpdates.get(a);
            int size2 = update.folder_peers.size();
            for (int b = 0; b < size2; b++) {
                TLRPC.TL_folderPeer folderPeer = update.folder_peers.get(b);
                long dialogId = DialogObject.getPeerDialogId(folderPeer.peer);
                TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
                if (dialog != null && dialog.folder_id != folderPeer.folder_id) {
                    dialog.pinned = false;
                    dialog.pinnedNum = 0;
                    dialog.folder_id = folderPeer.folder_id;
                    ensureFolderDialogExists(folderPeer.folder_id, null);
                }
            }
            updated = true;
            getMessagesStorage().setDialogsFolderId(folderUpdates.get(a).folder_peers, null, 0L, 0);
        }
        return updated;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$Chat> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$User> */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1160:0x1d82  */
    /* JADX WARN: Removed duplicated region for block: B:1164:0x1dc5  */
    /* JADX WARN: Removed duplicated region for block: B:1167:0x1dd1  */
    /* JADX WARN: Removed duplicated region for block: B:1171:0x1e0c  */
    /* JADX WARN: Removed duplicated region for block: B:1174:0x1e16  */
    /* JADX WARN: Removed duplicated region for block: B:1179:0x1e3f  */
    /* JADX WARN: Removed duplicated region for block: B:1183:0x1e74  */
    /* JADX WARN: Removed duplicated region for block: B:1186:0x1e7c  */
    /* JADX WARN: Removed duplicated region for block: B:1190:0x1eaa  */
    /* JADX WARN: Removed duplicated region for block: B:852:0x1670  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean processUpdateArray(java.util.ArrayList<org.telegram.tgnet.TLRPC.Update> r79, final java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r80, final java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat> r81, boolean r82, final int r83) {
        /*
            Method dump skipped, instructions count: 7854
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdateArray(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, boolean, int):boolean");
    }

    /* renamed from: lambda$processUpdateArray$308$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m791xa492ab56(ArrayList usersArr, ArrayList chatsArr) {
        putUsers(usersArr, false);
        putChats(chatsArr, false);
    }

    /* renamed from: lambda$processUpdateArray$309$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m792x89d41a17(ArrayList usersArr, ArrayList chatsArr) {
        putUsers(usersArr, false);
        putChats(chatsArr, false);
    }

    /* renamed from: lambda$processUpdateArray$310$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m793x3d739ead(TLRPC.TL_updateUserTyping update) {
        getNotificationCenter().postNotificationName(NotificationCenter.onEmojiInteractionsReceived, Long.valueOf(update.user_id), update.action);
    }

    /* renamed from: lambda$processUpdateArray$311$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m794x22b50d6e(TLRPC.TL_updateChatUserTyping update) {
        getNotificationCenter().postNotificationName(NotificationCenter.onEmojiInteractionsReceived, Long.valueOf(-update.chat_id), update.action);
    }

    /* renamed from: lambda$processUpdateArray$313$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m796xed37eaf0(final TLRPC.TL_updatePeerBlocked finalUpdate) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda89
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m795x7f67c2f(finalUpdate);
            }
        });
    }

    /* renamed from: lambda$processUpdateArray$312$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m795x7f67c2f(TLRPC.TL_updatePeerBlocked finalUpdate) {
        long id = MessageObject.getPeerId(finalUpdate.peer_id);
        if (finalUpdate.blocked) {
            if (this.blockePeers.indexOfKey(id) < 0) {
                this.blockePeers.put(id, 1);
            }
        } else {
            this.blockePeers.delete(id);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
    }

    /* renamed from: lambda$processUpdateArray$314$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m797xd27959b1(TLRPC.TL_updateServiceNotification update) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 2, update.message, update.type);
    }

    /* renamed from: lambda$processUpdateArray$315$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m798xb7bac872(TLRPC.TL_updateLangPack update) {
        LocaleController.getInstance().saveRemoteLocaleStringsForCurrentLocale(update.difference, this.currentAccount);
    }

    /* renamed from: lambda$processUpdateArray$316$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m799x9cfc3733(ArrayList pushMessagesFinal) {
        getNotificationsController().processNewMessages(pushMessagesFinal, true, false, null);
    }

    /* renamed from: lambda$processUpdateArray$317$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m800x823da5f4(final ArrayList pushMessagesFinal) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m799x9cfc3733(pushMessagesFinal);
            }
        });
    }

    /* renamed from: lambda$processUpdateArray$318$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m801x677f14b5(LongSparseArray editingMessagesFinal) {
        getNotificationsController().processEditedMessages(editingMessagesFinal);
    }

    /* renamed from: lambda$processUpdateArray$319$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m802x4cc08376(final LongSparseArray editingMessagesFinal) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda356
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m801x677f14b5(editingMessagesFinal);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:580:0x0f6a  */
    /* JADX WARN: Removed duplicated region for block: B:585:0x0fa3  */
    /* renamed from: lambda$processUpdateArray$327$org-telegram-messenger-MessagesController */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m810x452a0f53(int r45, java.util.ArrayList r46, androidx.collection.LongSparseArray r47, int r48, org.telegram.messenger.support.LongSparseIntArray r49, androidx.collection.LongSparseArray r50, androidx.collection.LongSparseArray r51, java.util.ArrayList r52, androidx.collection.LongSparseArray r53, androidx.collection.LongSparseArray r54, boolean r55, java.util.ArrayList r56, java.util.ArrayList r57, androidx.collection.LongSparseArray r58, androidx.collection.LongSparseArray r59, androidx.collection.LongSparseArray r60, java.util.ArrayList r61) {
        /*
            Method dump skipped, instructions count: 4606
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.m810x452a0f53(int, java.util.ArrayList, androidx.collection.LongSparseArray, int, org.telegram.messenger.support.LongSparseIntArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, boolean, java.util.ArrayList, java.util.ArrayList, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList):void");
    }

    /* renamed from: lambda$processUpdateArray$320$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m803x60080c(TLRPC.User currentUser) {
        getContactsController().addContactToPhoneBook(currentUser, true);
    }

    /* renamed from: lambda$processUpdateArray$321$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m804xe5a176cd() {
        getNotificationsController().deleteNotificationChannelGlobal(0);
    }

    /* renamed from: lambda$processUpdateArray$322$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m805xcae2e58e() {
        getNotificationsController().deleteNotificationChannelGlobal(1);
    }

    /* renamed from: lambda$processUpdateArray$323$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m806xb024544f() {
        getNotificationsController().deleteNotificationChannelGlobal(2);
    }

    /* renamed from: lambda$processUpdateArray$324$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m807x9565c310(TLRPC.TL_updateChannel update) {
        getChannelDifference(update.channel_id, 1, 0L, null);
    }

    /* renamed from: lambda$processUpdateArray$325$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m808x7aa731d1(TLRPC.Chat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    /* renamed from: lambda$processUpdateArray$326$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m809x5fe8a092(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates1 = (TLRPC.Updates) response;
            processUpdates(updates1, false);
        }
    }

    /* renamed from: lambda$processUpdateArray$329$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m812xfacecd5(final LongSparseIntArray markAsReadMessagesInboxFinal, final LongSparseIntArray markAsReadMessagesOutboxFinal, final SparseIntArray markAsReadEncryptedFinal, final LongSparseArray markContentAsReadMessagesFinal, final LongSparseArray deletedMessagesFinal, final LongSparseArray scheduledDeletedMessagesFinal, final LongSparseIntArray clearHistoryMessagesFinal) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m811x2a6b7e14(markAsReadMessagesInboxFinal, markAsReadMessagesOutboxFinal, markAsReadEncryptedFinal, markContentAsReadMessagesFinal, deletedMessagesFinal, scheduledDeletedMessagesFinal, clearHistoryMessagesFinal);
            }
        });
    }

    /* renamed from: lambda$processUpdateArray$328$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m811x2a6b7e14(LongSparseIntArray markAsReadMessagesInboxFinal, LongSparseIntArray markAsReadMessagesOutboxFinal, SparseIntArray markAsReadEncryptedFinal, LongSparseArray markContentAsReadMessagesFinal, LongSparseArray deletedMessagesFinal, LongSparseArray scheduledDeletedMessagesFinal, LongSparseIntArray clearHistoryMessagesFinal) {
        int updateMask;
        int updateMask2;
        long j;
        int a;
        int size2;
        MessageObject message;
        MessageObject obj;
        MessageObject obj2;
        int updateMask3 = 0;
        if (markAsReadMessagesInboxFinal != null || markAsReadMessagesOutboxFinal != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.messagesRead, markAsReadMessagesInboxFinal, markAsReadMessagesOutboxFinal);
            if (markAsReadMessagesInboxFinal != null) {
                int updateMask4 = 0;
                getNotificationsController().processReadMessages(markAsReadMessagesInboxFinal, 0L, 0, 0, false);
                SharedPreferences.Editor editor = this.notificationsPreferences.edit();
                int size = markAsReadMessagesInboxFinal.size();
                for (int b = 0; b < size; b++) {
                    long key = markAsReadMessagesInboxFinal.keyAt(b);
                    int messageId = markAsReadMessagesInboxFinal.valueAt(b);
                    TLRPC.Dialog dialog = this.dialogs_dict.get(key);
                    if (dialog != null && dialog.top_message > 0 && dialog.top_message <= messageId && (obj2 = this.dialogMessage.get(dialog.id)) != null && !obj2.isOut()) {
                        obj2.setIsRead();
                        updateMask4 |= UPDATE_MASK_READ_DIALOG_MESSAGE;
                    }
                    if (key != getUserConfig().getClientUserId()) {
                        editor.remove("diditem" + key);
                        editor.remove("diditemo" + key);
                    }
                }
                editor.commit();
                updateMask3 = updateMask4;
            }
            if (markAsReadMessagesOutboxFinal != null) {
                int size3 = markAsReadMessagesOutboxFinal.size();
                for (int b2 = 0; b2 < size3; b2++) {
                    long key2 = markAsReadMessagesOutboxFinal.keyAt(b2);
                    int messageId2 = markAsReadMessagesOutboxFinal.valueAt(b2);
                    TLRPC.Dialog dialog2 = this.dialogs_dict.get(key2);
                    if (dialog2 != null && dialog2.top_message > 0 && dialog2.top_message <= messageId2 && (obj = this.dialogMessage.get(dialog2.id)) != null && obj.isOut()) {
                        obj.setIsRead();
                        updateMask3 |= UPDATE_MASK_READ_DIALOG_MESSAGE;
                    }
                }
            }
        }
        if (markAsReadEncryptedFinal != null) {
            int size4 = markAsReadEncryptedFinal.size();
            for (int a2 = 0; a2 < size4; a2++) {
                int key3 = markAsReadEncryptedFinal.keyAt(a2);
                int value = markAsReadEncryptedFinal.valueAt(a2);
                getNotificationCenter().postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(key3), Integer.valueOf(value));
                long dialogId = DialogObject.makeEncryptedDialogId(key3);
                if (this.dialogs_dict.get(dialogId) != null && (message = this.dialogMessage.get(dialogId)) != null && message.messageOwner.date <= value) {
                    message.setIsRead();
                    updateMask3 = UPDATE_MASK_READ_DIALOG_MESSAGE | updateMask3;
                }
            }
        }
        if (markContentAsReadMessagesFinal != null) {
            int size5 = markContentAsReadMessagesFinal.size();
            for (int a3 = 0; a3 < size5; a3++) {
                long key4 = markContentAsReadMessagesFinal.keyAt(a3);
                getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(key4), (ArrayList) markContentAsReadMessagesFinal.valueAt(a3));
            }
        }
        int i = 3;
        if (deletedMessagesFinal != null) {
            int a4 = 0;
            int size6 = deletedMessagesFinal.size();
            while (a4 < size6) {
                long dialogId2 = deletedMessagesFinal.keyAt(a4);
                ArrayList<Integer> arrayList = (ArrayList) deletedMessagesFinal.valueAt(a4);
                if (arrayList == null) {
                    a = a4;
                } else {
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i2 = NotificationCenter.messagesDeleted;
                    Object[] objArr = new Object[i];
                    objArr[0] = arrayList;
                    a = a4;
                    objArr[1] = Long.valueOf(-dialogId2);
                    objArr[2] = false;
                    notificationCenter.postNotificationName(i2, objArr);
                    if (dialogId2 == 0) {
                        int b3 = 0;
                        int size22 = arrayList.size();
                        while (b3 < size22) {
                            MessageObject obj3 = this.dialogMessagesByIds.get(arrayList.get(b3).intValue());
                            if (obj3 == null) {
                                size2 = size22;
                            } else {
                                if (!BuildVars.LOGS_ENABLED) {
                                    size2 = size22;
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    size2 = size22;
                                    sb.append("mark messages ");
                                    sb.append(obj3.getId());
                                    sb.append(" deleted");
                                    FileLog.d(sb.toString());
                                }
                                obj3.deleted = true;
                            }
                            b3++;
                            size22 = size2;
                        }
                    } else {
                        MessageObject obj4 = this.dialogMessage.get(dialogId2);
                        if (obj4 != null) {
                            int b4 = 0;
                            int size23 = arrayList.size();
                            while (true) {
                                if (b4 < size23) {
                                    if (obj4.getId() != arrayList.get(b4).intValue()) {
                                        b4++;
                                    } else {
                                        obj4.deleted = true;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
                a4 = a + 1;
                i = 3;
            }
            getNotificationsController().removeDeletedMessagesFromNotifications(deletedMessagesFinal);
        }
        LongSparseArray longSparseArray = scheduledDeletedMessagesFinal;
        if (longSparseArray == null) {
            updateMask = updateMask3;
        } else {
            int a5 = 0;
            int size7 = scheduledDeletedMessagesFinal.size();
            while (a5 < size7) {
                long key5 = longSparseArray.keyAt(a5);
                ArrayList<Integer> arrayList2 = (ArrayList) longSparseArray.valueAt(a5);
                if (arrayList2 == null) {
                    updateMask2 = updateMask3;
                } else {
                    NotificationCenter notificationCenter2 = getNotificationCenter();
                    int i3 = NotificationCenter.messagesDeleted;
                    Object[] objArr2 = new Object[3];
                    objArr2[0] = arrayList2;
                    if (DialogObject.isChatDialog(key5)) {
                        updateMask2 = updateMask3;
                        if (ChatObject.isChannel(getChat(Long.valueOf(-key5)))) {
                            j = -key5;
                            objArr2[1] = Long.valueOf(j);
                            objArr2[2] = true;
                            notificationCenter2.postNotificationName(i3, objArr2);
                        }
                    } else {
                        updateMask2 = updateMask3;
                    }
                    j = 0;
                    objArr2[1] = Long.valueOf(j);
                    objArr2[2] = true;
                    notificationCenter2.postNotificationName(i3, objArr2);
                }
                a5++;
                longSparseArray = scheduledDeletedMessagesFinal;
                updateMask3 = updateMask2;
            }
            updateMask = updateMask3;
        }
        if (clearHistoryMessagesFinal != null) {
            int a6 = 0;
            int size8 = clearHistoryMessagesFinal.size();
            while (true) {
                if (a6 >= size8) {
                    break;
                }
                long key6 = clearHistoryMessagesFinal.keyAt(a6);
                int id = clearHistoryMessagesFinal.valueAt(a6);
                long did = -key6;
                int size9 = size8;
                getNotificationCenter().postNotificationName(NotificationCenter.historyCleared, Long.valueOf(did), Integer.valueOf(id));
                MessageObject obj5 = this.dialogMessage.get(did);
                if (obj5 == null || obj5.getId() > id) {
                    a6++;
                    size8 = size9;
                } else {
                    obj5.deleted = true;
                    break;
                }
            }
            getNotificationsController().removeDeletedHisoryFromNotifications(clearHistoryMessagesFinal);
        }
        if (updateMask != 0) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(updateMask));
        }
    }

    /* renamed from: lambda$processUpdateArray$330$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m813xc34c716b(long key, ArrayList arrayList) {
        ArrayList<Long> dialogIds = getMessagesStorage().markMessagesAsDeleted(key, arrayList, false, true, false);
        getMessagesStorage().updateDialogsWithDeletedMessages(key, -key, arrayList, dialogIds, false);
    }

    /* renamed from: lambda$processUpdateArray$331$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m814xa88de02c(long key, int id) {
        ArrayList<Long> dialogIds = getMessagesStorage().markMessagesAsDeleted(key, id, false, true);
        getMessagesStorage().updateDialogsWithDeletedMessages(key, -key, new ArrayList<>(), dialogIds, false);
    }

    public void checkUnreadReactions(final long dialogId, final SparseBooleanArray unreadReactions) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda354
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m614xe06509a2(unreadReactions, dialogId);
            }
        });
    }

    /* renamed from: lambda$checkUnreadReactions$335$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m614xe06509a2(SparseBooleanArray unreadReactions, final long dialogId) {
        int newUnreadCount;
        SQLiteException e;
        final ArrayList<Integer> newUnreadMessages = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < unreadReactions.size(); i++) {
            int messageId = unreadReactions.keyAt(i);
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(messageId);
        }
        SparseBooleanArray reactionsMentionsMessageIds = new SparseBooleanArray();
        int i2 = 1;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT message_id, state FROM reaction_mentions WHERE message_id IN (%s) AND dialog_id = %d", stringBuilder.toString(), Long.valueOf(dialogId)), new Object[0]);
            while (cursor.next()) {
                int messageId2 = cursor.intValue(0);
                boolean hasUnreadReactions = cursor.intValue(1) == 1;
                reactionsMentionsMessageIds.put(messageId2, hasUnreadReactions);
            }
            cursor.dispose();
        } catch (SQLiteException e2) {
            e2.printStackTrace();
        }
        int newUnreadCount2 = 0;
        int i3 = 0;
        boolean needReload = false;
        boolean changed = false;
        while (i3 < unreadReactions.size()) {
            int messageId3 = unreadReactions.keyAt(i3);
            boolean hasUnreadReaction = unreadReactions.valueAt(i3);
            if (reactionsMentionsMessageIds.indexOfKey(messageId3) >= 0) {
                if (reactionsMentionsMessageIds.get(messageId3) == hasUnreadReaction) {
                    newUnreadCount = newUnreadCount2;
                } else {
                    changed = true;
                    newUnreadCount = newUnreadCount2 + (hasUnreadReaction ? 1 : -1);
                }
            } else {
                needReload = true;
                newUnreadCount = newUnreadCount2;
            }
            if (hasUnreadReaction) {
                newUnreadMessages.add(Integer.valueOf(messageId3));
            }
            try {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO reaction_mentions VALUES(?, ?, ?)");
                try {
                    state.requery();
                    state.bindInteger(i2, messageId3);
                    try {
                        state.bindInteger(2, hasUnreadReaction ? 1 : 0);
                        state.bindLong(3, dialogId);
                        state.step();
                        state.dispose();
                    } catch (SQLiteException e3) {
                        e = e3;
                        e.printStackTrace();
                        i3++;
                        newUnreadCount2 = newUnreadCount;
                        i2 = 1;
                    }
                } catch (SQLiteException e4) {
                    e = e4;
                }
            } catch (SQLiteException e5) {
                e = e5;
            }
            i3++;
            newUnreadCount2 = newUnreadCount;
            i2 = 1;
        }
        if (needReload) {
            TLRPC.TL_messages_getPeerDialogs req = new TLRPC.TL_messages_getPeerDialogs();
            TLRPC.TL_inputDialogPeer inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            inputDialogPeer.peer = getInputPeer(dialogId);
            req.peers.add(inputDialogPeer);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda223
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m612x15e22c20(dialogId, newUnreadMessages, tLObject, tL_error);
                }
            });
        } else if (changed) {
            final int finalNewUnreadCount = newUnreadCount2;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda330
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m613xfb239ae1(dialogId, finalNewUnreadCount, newUnreadMessages);
                }
            });
        }
    }

    /* renamed from: lambda$checkUnreadReactions$333$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m612x15e22c20(final long dialogId, final ArrayList newUnreadMessages, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_messages_peerDialogs dialogs = (TLRPC.TL_messages_peerDialogs) response;
            final int count = dialogs.dialogs.size() == 0 ? 0 : dialogs.dialogs.get(0).unread_reactions_count;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda329
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m611x30a0bd5f(dialogId, count, newUnreadMessages);
                }
            });
        }
    }

    /* renamed from: lambda$checkUnreadReactions$332$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m611x30a0bd5f(long dialogId, int count, ArrayList newUnreadMessages) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog == null) {
            getMessagesStorage().updateDialogUnreadReactions(dialogId, count, false);
            return;
        }
        dialog.unread_reactions_count = count;
        getMessagesStorage().updateUnreadReactionsCount(dialogId, count);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(dialogId), Integer.valueOf(count), newUnreadMessages);
    }

    /* renamed from: lambda$checkUnreadReactions$334$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m613xfb239ae1(long dialogId, int finalNewUnreadCount, ArrayList newUnreadMessages) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog == null) {
            getMessagesStorage().updateDialogUnreadReactions(dialogId, finalNewUnreadCount, true);
            return;
        }
        dialog.unread_reactions_count += finalNewUnreadCount;
        if (dialog.unread_reactions_count < 0) {
            dialog.unread_reactions_count = 0;
        }
        getMessagesStorage().updateUnreadReactionsCount(dialogId, dialog.unread_reactions_count);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(dialogId), Integer.valueOf(dialog.unread_reactions_count), newUnreadMessages);
    }

    public boolean isDialogMuted(long dialogId) {
        return isDialogMuted(dialogId, null);
    }

    public boolean isDialogNotificationsSoundEnabled(long dialogId) {
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        return sharedPreferences.getBoolean("sound_enabled_" + dialogId, true);
    }

    public boolean isDialogMuted(long dialogId, TLRPC.Chat chat) {
        Boolean forceChannel;
        SharedPreferences sharedPreferences = this.notificationsPreferences;
        int mute_type = sharedPreferences.getInt("notify2_" + dialogId, -1);
        boolean z = false;
        if (mute_type == -1) {
            if (chat != null) {
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    z = true;
                }
                forceChannel = Boolean.valueOf(z);
            } else {
                forceChannel = null;
            }
            return !getNotificationsController().isGlobalNotificationsEnabled(dialogId, forceChannel);
        } else if (mute_type == 2) {
            return true;
        } else {
            if (mute_type == 3) {
                SharedPreferences sharedPreferences2 = this.notificationsPreferences;
                int mute_until = sharedPreferences2.getInt("notifyuntil_" + dialogId, 0);
                if (mute_until >= getConnectionsManager().getCurrentTime()) {
                    return true;
                }
            }
            return false;
        }
    }

    public void markReactionsAsRead(long dialogId) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(dialogId);
        if (dialog != null) {
            dialog.unread_reactions_count = 0;
        }
        getMessagesStorage().updateUnreadReactionsCount(dialogId, 0);
        TLRPC.TL_messages_readReactions req = new TLRPC.TL_messages_readReactions();
        req.peer = getInputPeer(dialogId);
        getConnectionsManager().sendRequest(req, MessagesController$$ExternalSyntheticLambda281.INSTANCE);
    }

    public static /* synthetic */ void lambda$markReactionsAsRead$336(TLObject response, TLRPC.TL_error error) {
    }

    public ArrayList<MessageObject> getSponsoredMessages(final long dialogId) {
        SponsoredMessagesInfo info = this.sponsoredMessages.get(dialogId);
        if (info != null && (info.loading || Math.abs(SystemClock.elapsedRealtime() - info.loadTime) <= 300000)) {
            return info.messages;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-dialogId));
        if (!ChatObject.isChannel(chat)) {
            return null;
        }
        final SponsoredMessagesInfo info2 = new SponsoredMessagesInfo();
        info2.loading = true;
        this.sponsoredMessages.put(dialogId, info2);
        TLRPC.TL_channels_getSponsoredMessages req = new TLRPC.TL_channels_getSponsoredMessages();
        req.channel = getInputChannel(chat);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda226
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m699xfd594a42(dialogId, info2, tLObject, tL_error);
            }
        });
        return null;
    }

    /* renamed from: lambda$getSponsoredMessages$339$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m699xfd594a42(final long dialogId, final SponsoredMessagesInfo infoFinal, TLObject response, TLRPC.TL_error error) {
        ArrayList<MessageObject> result;
        ArrayList<MessageObject> result2;
        if (response != null) {
            final TLRPC.TL_messages_sponsoredMessages res = (TLRPC.TL_messages_sponsoredMessages) response;
            if (res.messages.isEmpty()) {
                result2 = null;
            } else {
                result2 = new ArrayList<>();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda84
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m697x32d66cc0(res);
                    }
                });
                LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
                LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
                for (int a = 0; a < res.users.size(); a++) {
                    TLRPC.User u = res.users.get(a);
                    usersDict.put(u.id, u);
                }
                for (int a2 = 0; a2 < res.chats.size(); a2++) {
                    TLRPC.Chat c = res.chats.get(a2);
                    chatsDict.put(c.id, c);
                }
                int messageId = -10000000;
                int a3 = 0;
                int N = res.messages.size();
                while (a3 < N) {
                    TLRPC.TL_sponsoredMessage sponsoredMessage = res.messages.get(a3);
                    TLRPC.TL_message message = new TLRPC.TL_message();
                    message.message = sponsoredMessage.message;
                    if (!sponsoredMessage.entities.isEmpty()) {
                        message.entities = sponsoredMessage.entities;
                        message.flags |= 128;
                    }
                    message.peer_id = getPeer(dialogId);
                    message.from_id = sponsoredMessage.from_id;
                    message.flags |= 256;
                    message.date = getConnectionsManager().getCurrentTime();
                    int messageId2 = messageId - 1;
                    message.id = messageId;
                    MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) message, usersDict, chatsDict, true, true);
                    messageObject.sponsoredId = sponsoredMessage.random_id;
                    messageObject.botStartParam = sponsoredMessage.start_param;
                    messageObject.sponsoredChannelPost = sponsoredMessage.channel_post;
                    messageObject.sponsoredChatInvite = sponsoredMessage.chat_invite;
                    messageObject.sponsoredChatInviteHash = sponsoredMessage.chat_invite_hash;
                    result2.add(messageObject);
                    a3++;
                    messageId = messageId2;
                }
            }
            result = result2;
        } else {
            result = null;
        }
        final ArrayList<MessageObject> arrayList = result;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m698x1817db81(arrayList, dialogId, infoFinal);
            }
        });
    }

    /* renamed from: lambda$getSponsoredMessages$337$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m697x32d66cc0(TLRPC.TL_messages_sponsoredMessages res) {
        putUsers(res.users, false);
        putChats(res.chats, false);
    }

    /* renamed from: lambda$getSponsoredMessages$338$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m698x1817db81(ArrayList result, long dialogId, SponsoredMessagesInfo infoFinal) {
        if (result != null) {
            infoFinal.loadTime = SystemClock.elapsedRealtime();
            infoFinal.messages = result;
            getNotificationCenter().postNotificationName(NotificationCenter.didLoadSponsoredMessages, Long.valueOf(dialogId), result);
            return;
        }
        this.sponsoredMessages.remove(dialogId);
    }

    public TLRPC.TL_channels_sendAsPeers getSendAsPeers(final long dialogId) {
        SendAsPeersInfo info = this.sendAsPeers.get(dialogId);
        if (info != null && (info.loading || Math.abs(SystemClock.elapsedRealtime() - info.loadTime) <= 300000)) {
            return info.sendAsPeers;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-dialogId));
        if (chat == null || !ChatObject.canSendAsPeers(chat)) {
            return null;
        }
        final SendAsPeersInfo info2 = new SendAsPeersInfo();
        info2.loading = true;
        this.sendAsPeers.put(dialogId, info2);
        TLRPC.TL_channels_getSendAs req = new TLRPC.TL_channels_getSendAs();
        req.peer = getInputPeer(dialogId);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda225
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m696x8802e60c(dialogId, info2, tLObject, tL_error);
            }
        });
        return null;
    }

    /* renamed from: lambda$getSendAsPeers$342$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m696x8802e60c(final long dialogId, final SendAsPeersInfo infoFinal, TLObject response, TLRPC.TL_error error) {
        TLRPC.TL_channels_sendAsPeers result;
        if (response != null) {
            final TLRPC.TL_channels_sendAsPeers res = (TLRPC.TL_channels_sendAsPeers) response;
            if (res.peers.isEmpty()) {
                result = null;
            } else {
                result = res;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda65
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m694xbd80088a(res);
                    }
                });
                LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
                LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
                for (int a = 0; a < res.users.size(); a++) {
                    TLRPC.User u = res.users.get(a);
                    usersDict.put(u.id, u);
                }
                for (int a2 = 0; a2 < res.chats.size(); a2++) {
                    TLRPC.Chat c = res.chats.get(a2);
                    chatsDict.put(c.id, c);
                }
            }
        } else {
            result = null;
        }
        final TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers = result;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda67
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m695xa2c1774b(tL_channels_sendAsPeers, dialogId, infoFinal);
            }
        });
    }

    /* renamed from: lambda$getSendAsPeers$340$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m694xbd80088a(TLRPC.TL_channels_sendAsPeers res) {
        putUsers(res.users, false);
        putChats(res.chats, false);
    }

    /* renamed from: lambda$getSendAsPeers$341$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m695xa2c1774b(TLRPC.TL_channels_sendAsPeers result, long dialogId, SendAsPeersInfo infoFinal) {
        if (result != null) {
            infoFinal.loadTime = SystemClock.elapsedRealtime();
            infoFinal.sendAsPeers = result;
            getNotificationCenter().postNotificationName(NotificationCenter.didLoadSendAsPeers, Long.valueOf(dialogId), result);
            return;
        }
        this.sendAsPeers.remove(dialogId);
    }

    public CharSequence getPrintingString(long dialogId, int threadId, boolean isDialog) {
        SparseArray<CharSequence> threads;
        TLRPC.User user;
        if ((isDialog && DialogObject.isUserDialog(dialogId) && (user = getUser(Long.valueOf(dialogId))) != null && user.status != null && user.status.expires < 0) || (threads = this.printingStrings.get(dialogId)) == null) {
            return null;
        }
        return threads.get(threadId);
    }

    public Integer getPrintingStringType(long dialogId, int threadId) {
        SparseArray<Integer> threads = this.printingStringsTypes.get(dialogId);
        if (threads == null) {
            return null;
        }
        return threads.get(threadId);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.util.ArrayList<org.telegram.messenger.MessagesController$PrintingUser>> */
    private boolean updatePrintingUsersWithNewMessages(long uid, ArrayList<MessageObject> messages) {
        if (uid > 0) {
            if (this.printingUsers.get(Long.valueOf(uid)) != null) {
                this.printingUsers.remove(Long.valueOf(uid));
                return true;
            }
            return false;
        } else if (uid < 0) {
            ArrayList<Long> messagesUsers = new ArrayList<>();
            Iterator<MessageObject> it = messages.iterator();
            while (it.hasNext()) {
                MessageObject message = it.next();
                if (message.isFromUser() && !messagesUsers.contains(Long.valueOf(message.messageOwner.from_id.user_id))) {
                    messagesUsers.add(Long.valueOf(message.messageOwner.from_id.user_id));
                }
            }
            ConcurrentHashMap<Integer, ArrayList<PrintingUser>> concurrentHashMap = this.printingUsers.get(Long.valueOf(uid));
            boolean changed = false;
            if (concurrentHashMap != null) {
                ArrayList<Integer> threadsToRemove = null;
                for (Map.Entry<Integer, ArrayList<PrintingUser>> entry : concurrentHashMap.entrySet()) {
                    Integer threadId = entry.getKey();
                    ArrayList<PrintingUser> arr = entry.getValue();
                    int a = 0;
                    while (a < arr.size()) {
                        PrintingUser user = arr.get(a);
                        if (messagesUsers.contains(Long.valueOf(user.userId))) {
                            arr.remove(a);
                            a--;
                            if (arr.isEmpty()) {
                                if (threadsToRemove == null) {
                                    threadsToRemove = new ArrayList<>();
                                }
                                threadsToRemove.add(threadId);
                            }
                            changed = true;
                        }
                        a++;
                    }
                }
                if (threadsToRemove != null) {
                    int N = threadsToRemove.size();
                    for (int a2 = 0; a2 < N; a2++) {
                        concurrentHashMap.remove(threadsToRemove.get(a2));
                    }
                    if (concurrentHashMap.isEmpty()) {
                        this.printingUsers.remove(Long.valueOf(uid));
                    }
                }
            }
            return changed;
        } else {
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:37:0x00a2, code lost:
        if (r10.call.id != r15.messageOwner.action.call.id) goto L39;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean updateInterfaceWithMessages(final long r28, java.util.ArrayList<org.telegram.messenger.MessageObject> r30, boolean r31) {
        /*
            Method dump skipped, instructions count: 1046
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.updateInterfaceWithMessages(long, java.util.ArrayList, boolean):boolean");
    }

    /* renamed from: lambda$updateInterfaceWithMessages$343$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m886x14ecf514(TLRPC.Dialog dialogFinal, int mid, long dialogId, int param) {
        if (param != -1) {
            if (param != 0) {
                dialogFinal.folder_id = param;
                sortDialogs(null);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
            }
        } else if (mid > 0 && !DialogObject.isEncryptedDialog(dialogId)) {
            loadUnknownDialog(getInputPeer(dialogId), 0L);
        }
    }

    public void addDialogAction(long did, boolean clean) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(did);
        if (dialog == null) {
            return;
        }
        if (clean) {
            this.clearingHistoryDialogs.put(did, dialog);
        } else {
            this.deletingDialogs.put(did, dialog);
            this.allDialogs.remove(dialog);
            sortDialogs(null);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
    }

    public void removeDialogAction(long did, boolean clean, boolean apply) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(did);
        if (dialog == null) {
            return;
        }
        if (clean) {
            this.clearingHistoryDialogs.remove(did);
        } else {
            this.deletingDialogs.remove(did);
            if (!apply) {
                this.allDialogs.add(dialog);
                sortDialogs(null);
            }
        }
        if (!apply) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, true);
        }
    }

    public boolean isClearingDialog(long did) {
        return this.clearingHistoryDialogs.get(did) != null;
    }

    public void sortDialogs(LongSparseArray<TLRPC.Chat> chatsDict) {
        TLRPC.User user;
        TLRPC.Chat chat;
        TLRPC.Chat chat2;
        TLRPC.EncryptedChat encryptedChat;
        this.dialogsServerOnly.clear();
        this.dialogsCanAddUsers.clear();
        this.dialogsMyGroups.clear();
        this.dialogsMyChannels.clear();
        this.dialogsChannelsOnly.clear();
        this.dialogsGroupsOnly.clear();
        int a = 0;
        while (true) {
            DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
            if (a >= dialogFilterArr.length) {
                break;
            }
            if (dialogFilterArr[a] != null) {
                dialogFilterArr[a].dialogs.clear();
            }
            a++;
        }
        this.dialogsUsersOnly.clear();
        this.dialogsForBlock.clear();
        this.dialogsForward.clear();
        for (int a2 = 0; a2 < this.dialogsByFolder.size(); a2++) {
            ArrayList<TLRPC.Dialog> arrayList = this.dialogsByFolder.valueAt(a2);
            if (arrayList != null) {
                arrayList.clear();
            }
        }
        this.unreadUnmutedDialogs = 0;
        boolean selfAdded = false;
        long selfId = getUserConfig().getClientUserId();
        DialogFilter[] dialogFilterArr2 = this.selectedDialogFilter;
        if (dialogFilterArr2[0] != null || dialogFilterArr2[1] != null) {
            int b = 0;
            while (true) {
                DialogFilter[] dialogFilterArr3 = this.selectedDialogFilter;
                if (b >= dialogFilterArr3.length) {
                    break;
                }
                DialogFilter dialogFilter = dialogFilterArr3[b];
                this.sortingDialogFilter = dialogFilter;
                if (dialogFilter != null) {
                    Collections.sort(this.allDialogs, this.dialogDateComparator);
                    ArrayList<TLRPC.Dialog> dialogsByFilter = this.sortingDialogFilter.dialogs;
                    int N = this.allDialogs.size();
                    for (int a3 = 0; a3 < N; a3++) {
                        TLRPC.Dialog d = this.allDialogs.get(a3);
                        if (d instanceof TLRPC.TL_dialog) {
                            long dialogId = d.id;
                            if (DialogObject.isEncryptedDialog(dialogId) && (encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)))) != null) {
                                dialogId = encryptedChat.user_id;
                            }
                            if (this.sortingDialogFilter.includesDialog(getAccountInstance(), dialogId, d)) {
                                dialogsByFilter.add(d);
                            }
                        }
                    }
                }
                b++;
            }
        }
        Collections.sort(this.allDialogs, this.dialogComparator);
        this.isLeftPromoChannel = true;
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog != null && dialog.id < 0 && (chat2 = getChat(Long.valueOf(-this.promoDialog.id))) != null && !chat2.left) {
            this.isLeftPromoChannel = false;
        }
        int a4 = 0;
        int N2 = this.allDialogs.size();
        while (a4 < N2) {
            TLRPC.Dialog d2 = this.allDialogs.get(a4);
            if (d2 instanceof TLRPC.TL_dialog) {
                MessageObject messageObject = this.dialogMessage.get(d2.id);
                if (messageObject == null || messageObject.messageOwner.date >= this.dialogsLoadedTillDate) {
                    boolean canAddToForward = true;
                    if (!DialogObject.isEncryptedDialog(d2.id)) {
                        this.dialogsServerOnly.add(d2);
                        if (DialogObject.isChannel(d2)) {
                            TLRPC.Chat chat3 = getChat(Long.valueOf(-d2.id));
                            if (chat3 != null && (chat3.creator || ((chat3.megagroup && ((chat3.admin_rights != null && (chat3.admin_rights.post_messages || chat3.admin_rights.add_admins)) || chat3.default_banned_rights == null || !chat3.default_banned_rights.invite_users)) || (!chat3.megagroup && chat3.admin_rights != null && chat3.admin_rights.add_admins)))) {
                                if (chat3.creator || ((chat3.megagroup && chat3.admin_rights != null) || (!chat3.megagroup && chat3.admin_rights != null))) {
                                    if (chat3.megagroup) {
                                        this.dialogsMyGroups.add(d2);
                                    } else {
                                        this.dialogsMyChannels.add(d2);
                                    }
                                } else {
                                    this.dialogsCanAddUsers.add(d2);
                                }
                            }
                            if (chat3 != null && chat3.megagroup) {
                                this.dialogsGroupsOnly.add(d2);
                                canAddToForward = !chat3.gigagroup || ChatObject.hasAdminRights(chat3);
                            } else {
                                this.dialogsChannelsOnly.add(d2);
                                canAddToForward = ChatObject.hasAdminRights(chat3) && ChatObject.canPost(chat3);
                            }
                        } else if (d2.id < 0) {
                            if (chatsDict == null || (chat = chatsDict.get(-d2.id)) == null || chat.migrated_to == null) {
                                TLRPC.Chat chat4 = getChat(Long.valueOf(-d2.id));
                                if (chat4 != null && ((chat4.admin_rights != null && (chat4.admin_rights.add_admins || chat4.admin_rights.invite_users)) || chat4.creator)) {
                                    if (chat4.creator) {
                                        this.dialogsMyGroups.add(d2);
                                    } else {
                                        this.dialogsCanAddUsers.add(d2);
                                    }
                                }
                                this.dialogsGroupsOnly.add(d2);
                            } else {
                                this.allDialogs.remove(a4);
                                a4--;
                                N2--;
                            }
                        } else if (d2.id != selfId) {
                            this.dialogsUsersOnly.add(d2);
                            if (!UserObject.isReplyUser(d2.id)) {
                                this.dialogsForBlock.add(d2);
                            }
                        }
                    }
                    if (canAddToForward && d2.folder_id == 0) {
                        if (d2.id == selfId) {
                            this.dialogsForward.add(0, d2);
                            selfAdded = true;
                        } else {
                            this.dialogsForward.add(d2);
                        }
                    }
                }
                a4++;
            }
            if ((d2.unread_count != 0 || d2.unread_mark) && !isDialogMuted(d2.id)) {
                this.unreadUnmutedDialogs++;
            }
            if (this.promoDialog == null || d2.id != this.promoDialog.id || !this.isLeftPromoChannel) {
                addDialogToItsFolder(-1, d2);
            } else {
                this.allDialogs.remove(a4);
                a4--;
                N2--;
            }
            a4++;
        }
        TLRPC.Dialog dialog2 = this.promoDialog;
        if (dialog2 != null && this.isLeftPromoChannel) {
            this.allDialogs.add(0, dialog2);
            addDialogToItsFolder(-2, this.promoDialog);
        }
        if (!selfAdded && (user = getUserConfig().getCurrentUser()) != null) {
            TLRPC.Dialog dialog3 = new TLRPC.TL_dialog();
            dialog3.id = user.id;
            dialog3.notify_settings = new TLRPC.TL_peerNotifySettings();
            dialog3.peer = new TLRPC.TL_peerUser();
            dialog3.peer.user_id = user.id;
            this.dialogsForward.add(0, dialog3);
        }
        for (int a5 = 0; a5 < this.dialogsByFolder.size(); a5++) {
            int folderId = this.dialogsByFolder.keyAt(a5);
            ArrayList<TLRPC.Dialog> dialogs = this.dialogsByFolder.valueAt(a5);
            if (dialogs.isEmpty()) {
                this.dialogsByFolder.remove(folderId);
            }
        }
    }

    private void addDialogToItsFolder(int index, TLRPC.Dialog dialog) {
        int folderId;
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            folderId = 0;
        } else {
            folderId = dialog.folder_id;
        }
        ArrayList<TLRPC.Dialog> dialogs = this.dialogsByFolder.get(folderId);
        if (dialogs == null) {
            dialogs = new ArrayList<>();
            this.dialogsByFolder.put(folderId, dialogs);
        }
        if (index == -1) {
            dialogs.add(dialog);
        } else if (index == -2) {
            if (dialogs.isEmpty() || !(dialogs.get(0) instanceof TLRPC.TL_dialogFolder)) {
                dialogs.add(0, dialog);
            } else {
                dialogs.add(1, dialog);
            }
        } else {
            dialogs.add(index, dialog);
        }
    }

    public static String getRestrictionReason(ArrayList<TLRPC.TL_restrictionReason> reasons) {
        if (reasons.isEmpty()) {
            return null;
        }
        int N = reasons.size();
        for (int a = 0; a < N; a++) {
            TLRPC.TL_restrictionReason reason = reasons.get(a);
            if ("all".equals(reason.platform) || (!BuildVars.isStandaloneApp() && !BuildVars.isBetaApp() && "android".equals(reason.platform))) {
                return reason.text;
            }
        }
        return null;
    }

    private static void showCantOpenAlert(BaseFragment fragment, String reason) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", org.telegram.messenger.beta.R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", org.telegram.messenger.beta.R.string.OK), null);
        builder.setMessage(reason);
        fragment.showDialog(builder.create());
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment fragment) {
        return checkCanOpenChat(bundle, fragment, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean checkCanOpenChat(final Bundle bundle, final BaseFragment fragment, MessageObject originalMessage) {
        String reason;
        TLObject req;
        TLRPC.Chat chat;
        if (bundle == null || fragment == null) {
            return true;
        }
        TLRPC.User user = null;
        TLRPC.Chat chat2 = null;
        long userId = bundle.getLong("user_id", 0L);
        long chatId = bundle.getLong(ChatReactionsEditActivity.KEY_CHAT_ID, 0L);
        int messageId = bundle.getInt(Constants.MessagePayloadKeys.MSGID_SERVER, 0);
        if (userId != 0) {
            user = getUser(Long.valueOf(userId));
        } else if (chatId != 0) {
            chat2 = getChat(Long.valueOf(chatId));
        }
        if (user == null && chat2 == null) {
            return true;
        }
        if (chat2 != null) {
            reason = getRestrictionReason(chat2.restriction_reason);
        } else {
            reason = getRestrictionReason(user.restriction_reason);
        }
        if (reason != null) {
            showCantOpenAlert(fragment, reason);
            return false;
        } else if (messageId == 0 || originalMessage == null || chat2 == null) {
            return true;
        } else {
            if (chat2.access_hash == 0) {
                long did = originalMessage.getDialogId();
                if (!DialogObject.isEncryptedDialog(did)) {
                    final AlertDialog progressDialog = new AlertDialog(fragment.getParentActivity(), 3);
                    if (did < 0) {
                        chat2 = getChat(Long.valueOf(-did));
                    }
                    int messageId2 = (did > 0L ? 1 : (did == 0L ? 0 : -1));
                    if (messageId2 > 0) {
                        chat = chat2;
                    } else if (ChatObject.isChannel(chat2)) {
                        TLRPC.Chat chat3 = getChat(Long.valueOf(-did));
                        TLRPC.TL_channels_getMessages request = new TLRPC.TL_channels_getMessages();
                        request.channel = getInputChannel(chat3);
                        request.id.add(Integer.valueOf(originalMessage.getId()));
                        req = request;
                        final int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda254
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MessagesController.this.m590x5abd1d78(progressDialog, fragment, bundle, tLObject, tL_error);
                            }
                        });
                        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda295
                            @Override // android.content.DialogInterface.OnCancelListener
                            public final void onCancel(DialogInterface dialogInterface) {
                                MessagesController.this.m591x3ffe8c39(reqId, fragment, dialogInterface);
                            }
                        });
                        fragment.setVisibleDialog(progressDialog);
                        progressDialog.show();
                        return false;
                    } else {
                        chat = chat2;
                    }
                    TLRPC.TL_messages_getMessages request2 = new TLRPC.TL_messages_getMessages();
                    request2.id.add(Integer.valueOf(originalMessage.getId()));
                    req = request2;
                    final int reqId2 = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda254
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessagesController.this.m590x5abd1d78(progressDialog, fragment, bundle, tLObject, tL_error);
                        }
                    });
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda295
                        @Override // android.content.DialogInterface.OnCancelListener
                        public final void onCancel(DialogInterface dialogInterface) {
                            MessagesController.this.m591x3ffe8c39(reqId2, fragment, dialogInterface);
                        }
                    });
                    fragment.setVisibleDialog(progressDialog);
                    progressDialog.show();
                    return false;
                }
                return true;
            }
            return true;
        }
    }

    /* renamed from: lambda$checkCanOpenChat$345$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m590x5abd1d78(final AlertDialog progressDialog, final BaseFragment fragment, final Bundle bundle, final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda116
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m589x757baeb7(progressDialog, response, fragment, bundle);
                }
            });
        }
    }

    /* renamed from: lambda$checkCanOpenChat$344$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m589x757baeb7(AlertDialog progressDialog, TLObject response, BaseFragment fragment, Bundle bundle) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
        putUsers(res.users, false);
        putChats(res.chats, false);
        getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
        fragment.presentFragment(new ChatActivity(bundle), true);
    }

    /* renamed from: lambda$checkCanOpenChat$346$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m591x3ffe8c39(int reqId, BaseFragment fragment, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
        fragment.setVisibleDialog(null);
    }

    public static void openChatOrProfileWith(TLRPC.User user, TLRPC.Chat chat, BaseFragment fragment, int type, boolean closeLast) {
        String reason;
        if ((user == null && chat == null) || fragment == null) {
            return;
        }
        if (chat != null) {
            reason = getRestrictionReason(chat.restriction_reason);
        } else {
            reason = getRestrictionReason(user.restriction_reason);
            if (type != 3 && user.bot) {
                type = 1;
                closeLast = true;
            }
        }
        if (reason != null) {
            showCantOpenAlert(fragment, reason);
            return;
        }
        Bundle args = new Bundle();
        if (chat != null) {
            args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, chat.id);
        } else {
            args.putLong("user_id", user.id);
        }
        if (type == 0) {
            fragment.presentFragment(new ProfileActivity(args));
        } else if (type == 2) {
            fragment.presentFragment(new ChatActivity(args), true, true);
        } else {
            fragment.presentFragment(new ChatActivity(args), closeLast);
        }
    }

    public void openByUserName(String username, final BaseFragment fragment, final int type) {
        if (username == null || fragment == null) {
            return;
        }
        TLObject object = getUserOrChat(username);
        TLRPC.User user = null;
        TLRPC.Chat chat = null;
        if (object instanceof TLRPC.User) {
            user = (TLRPC.User) object;
            if (user.min) {
                user = null;
            }
        } else if (object instanceof TLRPC.Chat) {
            chat = (TLRPC.Chat) object;
            if (chat.min) {
                chat = null;
            }
        }
        if (user != null) {
            openChatOrProfileWith(user, null, fragment, type, false);
        } else if (chat != null) {
            openChatOrProfileWith(null, chat, fragment, 1, false);
        } else if (fragment.getParentActivity() == null) {
        } else {
            final AlertDialog[] progressDialog = {new AlertDialog(fragment.getParentActivity(), 3)};
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            req.username = username;
            final int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda267
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.this.m767x62d8c008(progressDialog, fragment, type, tLObject, tL_error);
                }
            });
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda126
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m769xfbb9b35f(progressDialog, reqId, fragment);
                }
            }, 500L);
        }
    }

    /* renamed from: lambda$openByUserName$348$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m767x62d8c008(final AlertDialog[] progressDialog, final BaseFragment fragment, final int type, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda127
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m766x7d975147(progressDialog, fragment, error, response, type);
            }
        });
    }

    /* renamed from: lambda$openByUserName$347$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m766x7d975147(AlertDialog[] progressDialog, BaseFragment fragment, TLRPC.TL_error error, TLObject response, int type) {
        try {
            progressDialog[0].dismiss();
        } catch (Exception e) {
        }
        progressDialog[0] = null;
        fragment.setVisibleDialog(null);
        if (error == null) {
            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
            putUsers(res.users, false);
            putChats(res.chats, false);
            getMessagesStorage().putUsersAndChats(res.users, res.chats, false, true);
            if (!res.chats.isEmpty()) {
                openChatOrProfileWith(null, res.chats.get(0), fragment, 1, false);
            } else if (!res.users.isEmpty()) {
                openChatOrProfileWith(res.users.get(0), null, fragment, type, false);
            }
        } else if (fragment.getParentActivity() != null) {
            try {
                BulletinFactory.of(fragment).createErrorBulletin(LocaleController.getString("NoUsernameFound", org.telegram.messenger.beta.R.string.NoUsernameFound)).show();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    /* renamed from: lambda$openByUserName$350$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m769xfbb9b35f(AlertDialog[] progressDialog, final int reqId, BaseFragment fragment) {
        if (progressDialog[0] == null) {
            return;
        }
        progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda222
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                MessagesController.this.m768x481a2ec9(reqId, dialogInterface);
            }
        });
        fragment.showDialog(progressDialog[0]);
    }

    /* renamed from: lambda$openByUserName$349$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m768x481a2ec9(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    public void ensureMessagesLoaded(final long dialogId, int messageId, final MessagesLoadedCallback callback) {
        int messageId2;
        long chatId;
        SharedPreferences sharedPreferences = getNotificationsSettings(this.currentAccount);
        if (messageId != 0) {
            messageId2 = messageId;
        } else {
            messageId2 = sharedPreferences.getInt("diditem" + dialogId, 0);
        }
        final int finalMessageId = messageId2;
        final int classGuid = ConnectionsManager.generateClassGuid();
        if (DialogObject.isChatDialog(dialogId)) {
            chatId = -dialogId;
        } else {
            chatId = 0;
        }
        if (chatId != 0) {
            TLRPC.Chat currentChat = getMessagesController().getChat(Long.valueOf(chatId));
            if (currentChat == null) {
                final MessagesStorage messagesStorage = getMessagesStorage();
                final long j = chatId;
                messagesStorage.getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda36
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.this.m664x412da119(messagesStorage, j, dialogId, finalMessageId, callback);
                    }
                });
                return;
            }
        }
        final int count = AndroidUtilities.isTablet() ? 30 : 20;
        NotificationCenter.NotificationCenterDelegate delegate = new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.messenger.MessagesController.1
            {
                MessagesController.this = this;
            }

            @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
            public void didReceivedNotification(int id, int account, Object... args) {
                if (id == NotificationCenter.messagesDidLoadWithoutProcess && ((Integer) args[0]).intValue() == classGuid) {
                    int size = ((Integer) args[1]).intValue();
                    boolean isCache = ((Boolean) args[2]).booleanValue();
                    boolean isEnd = ((Boolean) args[3]).booleanValue();
                    int lastMessageId = ((Integer) args[4]).intValue();
                    int i = count;
                    if (size >= i / 2 || isEnd || !isCache) {
                        MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.messagesDidLoadWithoutProcess);
                        MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.loadingMessagesFailed);
                        MessagesLoadedCallback messagesLoadedCallback = callback;
                        if (messagesLoadedCallback != null) {
                            messagesLoadedCallback.onMessagesLoaded(isCache);
                            return;
                        }
                        return;
                    }
                    int i2 = finalMessageId;
                    if (i2 != 0) {
                        MessagesController.this.loadMessagesInternal(dialogId, 0L, false, i, i2, 0, false, 0, classGuid, 3, lastMessageId, 0, 0, -1, 0, 0, 0, false, 0, true, false);
                        return;
                    } else {
                        MessagesController.this.loadMessagesInternal(dialogId, 0L, false, i, i2, 0, false, 0, classGuid, 2, lastMessageId, 0, 0, -1, 0, 0, 0, false, 0, true, false);
                        return;
                    }
                }
                int size2 = NotificationCenter.loadingMessagesFailed;
                if (id == size2 && ((Integer) args[0]).intValue() == classGuid) {
                    MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.messagesDidLoadWithoutProcess);
                    MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.loadingMessagesFailed);
                    MessagesLoadedCallback messagesLoadedCallback2 = callback;
                    if (messagesLoadedCallback2 != null) {
                        messagesLoadedCallback2.onError();
                    }
                }
            }
        };
        getNotificationCenter().addObserver(delegate, NotificationCenter.messagesDidLoadWithoutProcess);
        getNotificationCenter().addObserver(delegate, NotificationCenter.loadingMessagesFailed);
        if (messageId2 != 0) {
            loadMessagesInternal(dialogId, 0L, true, count, finalMessageId, 0, true, 0, classGuid, 3, 0, 0, 0, -1, 0, 0, 0, false, 0, true, false);
        } else {
            loadMessagesInternal(dialogId, 0L, true, count, finalMessageId, 0, true, 0, classGuid, 2, 0, 0, 0, -1, 0, 0, 0, false, 0, true, false);
        }
    }

    /* renamed from: lambda$ensureMessagesLoaded$352$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m664x412da119(MessagesStorage messagesStorage, long chatId, final long dialogId, final int finalMessageId, final MessagesLoadedCallback callback) {
        final TLRPC.Chat chat = messagesStorage.getChat(chatId);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda56
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m663x5bec3258(chat, dialogId, finalMessageId, callback);
            }
        });
    }

    /* renamed from: lambda$ensureMessagesLoaded$351$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m663x5bec3258(TLRPC.Chat chat, long dialogId, int finalMessageId, MessagesLoadedCallback callback) {
        if (chat != null) {
            getMessagesController().putChat(chat, true);
            ensureMessagesLoaded(dialogId, finalMessageId, callback);
        } else if (callback != null) {
            callback.onError();
        }
    }

    public int getChatPendingRequestsOnClosed(long chatId) {
        SharedPreferences sharedPreferences = this.mainPreferences;
        return sharedPreferences.getInt("chatPendingRequests" + chatId, 0);
    }

    public void setChatPendingRequestsOnClose(long chatId, int count) {
        SharedPreferences.Editor edit = this.mainPreferences.edit();
        edit.putInt("chatPendingRequests" + chatId, count).apply();
    }

    public void markSponsoredAsRead(long dialog_id, MessageObject object) {
    }

    public void deleteMessagesRange(final long dialogId, final long channelId, final int minDate, final int maxDate, boolean forAll, final Runnable callback) {
        TLRPC.TL_messages_deleteHistory req = new TLRPC.TL_messages_deleteHistory();
        req.peer = getInputPeer(dialogId);
        req.flags = 12;
        req.min_date = minDate;
        req.max_date = maxDate;
        req.revoke = forAll;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda211
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m645x9b6dab5a(dialogId, minDate, maxDate, channelId, callback, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$deleteMessagesRange$356$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m645x9b6dab5a(final long dialogId, final int minDate, final int maxDate, final long channelId, final Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_affectedHistory res = (TLRPC.TL_messages_affectedHistory) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda325
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m644xd0eacdd8(dialogId, minDate, maxDate, channelId, callback);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda350
            @Override // java.lang.Runnable
            public final void run() {
                callback.run();
            }
        });
    }

    /* renamed from: lambda$deleteMessagesRange$354$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m644xd0eacdd8(long dialogId, int minDate, int maxDate, final long channelId, final Runnable callback) {
        final ArrayList<Integer> dbMessages = getMessagesStorage().getCachedMessagesInRange(dialogId, minDate, maxDate);
        getMessagesStorage().markMessagesAsDeleted(dialogId, dbMessages, false, true, false);
        getMessagesStorage().updateDialogsWithDeletedMessages(dialogId, 0L, dbMessages, null, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.this.m643xeba95f17(dbMessages, channelId, callback);
            }
        });
    }

    /* renamed from: lambda$deleteMessagesRange$353$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m643xeba95f17(ArrayList dbMessages, long channelId, Runnable callback) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, dbMessages, Long.valueOf(channelId), false);
        callback.run();
    }

    public void setChatReactions(final long chatId, final List<String> reactions) {
        TLRPC.TL_messages_setChatAvailableReactions req = new TLRPC.TL_messages_setChatAvailableReactions();
        req.peer = getInputPeer(-chatId);
        req.available_reactions.addAll(reactions);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda224
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.this.m851xff13c3c0(chatId, reactions, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$setChatReactions$358$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m851xff13c3c0(final long chatId, List reactions, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            processUpdates((TLRPC.Updates) response, false);
            TLRPC.ChatFull full = getChatFull(chatId);
            if (full != null) {
                if (full instanceof TLRPC.TL_chatFull) {
                    full.flags |= 262144;
                }
                if (full instanceof TLRPC.TL_channelFull) {
                    full.flags |= C.BUFFER_FLAG_ENCRYPTED;
                }
                full.available_reactions = new ArrayList<>(reactions);
                getMessagesStorage().updateChatInfo(full, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda321
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.this.m850x19d254ff(chatId);
                }
            });
        }
    }

    /* renamed from: lambda$setChatReactions$357$org-telegram-messenger-MessagesController */
    public /* synthetic */ void m850x19d254ff(long chatId) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatAvailableReactionsUpdated, Long.valueOf(chatId));
    }

    public void checkIsInChat(TLRPC.Chat chat, TLRPC.User user, final IsInChatCheckedCallback callback) {
        boolean z = false;
        if (chat == null || user == null) {
            if (callback != null) {
                callback.run(false, null, null);
            }
        } else if (chat.megagroup || ChatObject.isChannel(chat)) {
            TLRPC.TL_channels_getParticipant req = new TLRPC.TL_channels_getParticipant();
            req.channel = getInputChannel(chat.id);
            req.participant = getInputPeer(user);
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.MessagesController$$ExternalSyntheticLambda146
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.lambda$checkIsInChat$359(MessagesController.IsInChatCheckedCallback.this, tLObject, tL_error);
                }
            });
        } else {
            TLRPC.ChatFull chatFull = getChatFull(chat.id);
            if (chatFull != null) {
                TLRPC.ChatParticipant userParticipant = null;
                if (chatFull.participants != null && chatFull.participants.participants != null) {
                    int count = chatFull.participants.participants.size();
                    int i = 0;
                    while (true) {
                        if (i < count) {
                            TLRPC.ChatParticipant participant = chatFull.participants.participants.get(i);
                            if (participant == null || participant.user_id != user.id) {
                                i++;
                            } else {
                                userParticipant = participant;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                if (callback != null) {
                    if (userParticipant != null) {
                        z = true;
                    }
                    callback.run(z, (chatFull.participants == null || chatFull.participants.admin_id != user.id) ? null : ChatRightsEditActivity.emptyAdminRights(true), null);
                }
            } else if (callback != null) {
                callback.run(false, null, null);
            }
        }
    }

    public static /* synthetic */ void lambda$checkIsInChat$359(IsInChatCheckedCallback callback, TLObject res, TLRPC.TL_error err) {
        if (callback != null) {
            String str = null;
            TLRPC.ChannelParticipant participant = res instanceof TLRPC.TL_channels_channelParticipant ? ((TLRPC.TL_channels_channelParticipant) res).participant : null;
            boolean z = err == null && participant != null && !participant.left;
            TLRPC.TL_chatAdminRights tL_chatAdminRights = participant != null ? participant.admin_rights : null;
            if (participant != null) {
                str = participant.rank;
            }
            callback.run(z, tL_chatAdminRights, str);
        }
    }

    private void applySoundSettings(TLRPC.NotificationSound settings, SharedPreferences.Editor editor, long dialogId, int globalType, boolean serverUpdate) {
        String soundDocPref;
        String soundPathPref;
        String soundPref;
        if (settings == null) {
            return;
        }
        if (dialogId != 0) {
            soundPref = "sound_" + dialogId;
            soundPathPref = "sound_path_" + dialogId;
            soundDocPref = "sound_document_id_" + dialogId;
        } else if (globalType == 0) {
            soundPref = "GroupSound";
            soundDocPref = "GroupSoundDocId";
            soundPathPref = "GroupSoundPath";
        } else if (globalType == 1) {
            soundPref = "GlobalSound";
            soundDocPref = "GlobalSoundDocId";
            soundPathPref = "GlobalSoundPath";
        } else {
            soundPref = "ChannelSound";
            soundDocPref = "ChannelSoundDocId";
            soundPathPref = "ChannelSoundPath";
        }
        if (settings instanceof TLRPC.TL_notificationSoundDefault) {
            editor.putString(soundPref, "Default");
            editor.putString(soundPathPref, "Default");
            editor.remove(soundDocPref);
        } else if (settings instanceof TLRPC.TL_notificationSoundNone) {
            editor.putString(soundPref, "NoSound");
            editor.putString(soundPathPref, "NoSound");
            editor.remove(soundDocPref);
        } else if (settings instanceof TLRPC.TL_notificationSoundLocal) {
            TLRPC.TL_notificationSoundLocal localSound = (TLRPC.TL_notificationSoundLocal) settings;
            editor.putString(soundPref, localSound.title);
            editor.putString(soundPathPref, localSound.data);
            editor.remove(soundDocPref);
        } else if (settings instanceof TLRPC.TL_notificationSoundRingtone) {
            TLRPC.TL_notificationSoundRingtone soundRingtone = (TLRPC.TL_notificationSoundRingtone) settings;
            editor.putLong(soundDocPref, soundRingtone.id);
            getMediaDataController().checkRingtones();
            if (serverUpdate && dialogId != 0) {
                editor.putBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + dialogId, true);
            }
            getMediaDataController().ringtoneDataStore.getDocument(soundRingtone.id);
        }
    }
}
