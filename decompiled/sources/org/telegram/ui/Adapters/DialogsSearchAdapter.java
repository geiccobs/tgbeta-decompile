package org.telegram.ui.Adapters;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;
/* loaded from: classes4.dex */
public class DialogsSearchAdapter extends RecyclerListView.SelectionAdapter {
    private Runnable cancelShowMoreAnimation;
    private int currentItemCount;
    private String currentMessagesQuery;
    private DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
    private FilteredSearchView.Delegate filtersDelegate;
    private int folderId;
    private RecyclerListView innerListView;
    private DefaultItemAnimator itemAnimator;
    private int lastGlobalSearchId;
    private int lastLocalSearchId;
    private int lastMessagesSearchId;
    private String lastMessagesSearchString;
    private int lastReqId;
    private int lastSearchId;
    private String lastSearchText;
    private long lastShowMoreUpdate;
    private boolean localTipArchive;
    private Context mContext;
    private boolean messagesSearchEndReached;
    private int needMessagesSearch;
    private int nextSearchRate;
    private SearchAdapterHelper searchAdapterHelper;
    private Runnable searchRunnable;
    private Runnable searchRunnable2;
    private boolean searchWas;
    public View showMoreHeader;
    public int showMoreLastItem;
    int waitingResponseCount;
    private final int VIEW_TYPE_PROFILE_CELL = 0;
    private final int VIEW_TYPE_GRAY_SECTION = 1;
    private final int VIEW_TYPE_DIALOG_CELL = 2;
    private final int VIEW_TYPE_LOADING = 3;
    private final int VIEW_TYPE_HASHTAG_CELL = 4;
    private final int VIEW_TYPE_CATEGORY_LIST = 5;
    private final int VIEW_TYPE_ADD_BY_PHONE = 6;
    private ArrayList<Object> searchResult = new ArrayList<>();
    private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private ArrayList<String> searchResultHashtags = new ArrayList<>();
    private int reqId = 0;
    public boolean showMoreAnimation = false;
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<RecentSearchObject> recentSearchObjects = new ArrayList<>();
    private ArrayList<RecentSearchObject> filteredRecentSearchObjects = new ArrayList<>();
    private String filteredRecentQuery = null;
    private LongSparseArray<RecentSearchObject> recentSearchObjectsById = new LongSparseArray<>();
    private ArrayList<FiltersView.DateData> localTipDates = new ArrayList<>();
    boolean globalSearchCollapsed = true;
    boolean phoneCollapsed = true;
    private long selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();

    /* loaded from: classes4.dex */
    public static class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;
    }

    /* loaded from: classes4.dex */
    public interface DialogsSearchAdapterDelegate {
        void didPressedOnSubDialog(long j);

        boolean isSelected(long j);

        void needClearList();

        void needRemoveHint(long j);

        void runResultsEnterAnimation();

        void searchStateChanged(boolean z, boolean z2);
    }

    /* loaded from: classes4.dex */
    public interface OnRecentSearchLoaded {
        void setRecentSearch(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> longSparseArray);
    }

    /* loaded from: classes4.dex */
    public static class RecentSearchObject {
        public int date;
        public long did;
        public TLObject object;
    }

    public boolean isSearching() {
        return this.waitingResponseCount > 0;
    }

    /* loaded from: classes4.dex */
    public static class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
        private final int currentAccount;
        private boolean drawChecked;
        private boolean forceDarkTheme;
        private final Context mContext;

        public CategoryAdapterRecycler(Context context, int account, boolean drawChecked) {
            this.drawChecked = drawChecked;
            this.mContext = context;
            this.currentAccount = account;
        }

        public void setIndex(int value) {
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            HintDialogCell cell = new HintDialogCell(this.mContext, this.drawChecked);
            cell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(86.0f)));
            return new RecyclerListView.Holder(cell);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            HintDialogCell cell = (HintDialogCell) holder.itemView;
            TLRPC.TL_topPeer peer = MediaDataController.getInstance(this.currentAccount).hints.get(position);
            new TLRPC.TL_dialog();
            TLRPC.Chat chat = null;
            TLRPC.User user = null;
            long did = 0;
            if (peer.peer.user_id != 0) {
                did = peer.peer.user_id;
                user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peer.peer.user_id));
            } else if (peer.peer.channel_id != 0) {
                did = -peer.peer.channel_id;
                chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(peer.peer.channel_id));
            } else if (peer.peer.chat_id != 0) {
                did = -peer.peer.chat_id;
                chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(peer.peer.chat_id));
            }
            cell.setTag(Long.valueOf(did));
            String name = "";
            if (user != null) {
                name = UserObject.getFirstName(user);
            } else if (chat != null) {
                name = chat.title;
            }
            cell.setDialog(did, true, name);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return MediaDataController.getInstance(this.currentAccount).hints.size();
        }
    }

    public DialogsSearchAdapter(Context context, int messagesSearch, int type, DefaultItemAnimator itemAnimator) {
        this.itemAnimator = itemAnimator;
        SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(false);
        this.searchAdapterHelper = searchAdapterHelper;
        searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter.1
            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public /* synthetic */ LongSparseArray getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public void onDataSetChanged(int searchId) {
                DialogsSearchAdapter.this.waitingResponseCount--;
                DialogsSearchAdapter.this.lastGlobalSearchId = searchId;
                if (DialogsSearchAdapter.this.lastLocalSearchId != searchId) {
                    DialogsSearchAdapter.this.searchResult.clear();
                }
                if (DialogsSearchAdapter.this.lastMessagesSearchId != searchId) {
                    DialogsSearchAdapter.this.searchResultMessages.clear();
                }
                DialogsSearchAdapter.this.searchWas = true;
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(DialogsSearchAdapter.this.waitingResponseCount > 0, true);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.runResultsEnterAnimation();
                }
            }

            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                for (int a = 0; a < arrayList.size(); a++) {
                    DialogsSearchAdapter.this.searchResultHashtags.add(arrayList.get(a).hashtag);
                }
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(DialogsSearchAdapter.this.waitingResponseCount > 0, false);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }

            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public boolean canApplySearchResults(int searchId) {
                return searchId == DialogsSearchAdapter.this.lastSearchId;
            }
        });
        this.mContext = context;
        this.needMessagesSearch = messagesSearch;
        this.dialogsType = type;
        loadRecentSearch();
        MediaDataController.getInstance(this.currentAccount).loadHints(true);
    }

    public RecyclerListView getInnerListView() {
        return this.innerListView;
    }

    public void setDelegate(DialogsSearchAdapterDelegate delegate) {
        this.delegate = delegate;
    }

    public boolean isMessagesSearchEndReached() {
        return this.messagesSearchEndReached;
    }

    public void loadMoreSearchMessages() {
        if (this.reqId != 0) {
            return;
        }
        searchMessagesInternal(this.lastMessagesSearchString, this.lastMessagesSearchId);
    }

    public String getLastSearchString() {
        return this.lastMessagesSearchString;
    }

    private void searchMessagesInternal(final String query, final int searchId) {
        if (this.needMessagesSearch != 0) {
            if (TextUtils.isEmpty(this.lastMessagesSearchString) && TextUtils.isEmpty(query)) {
                return;
            }
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (TextUtils.isEmpty(query)) {
                this.filteredRecentQuery = null;
                this.searchResultMessages.clear();
                this.lastReqId = 0;
                this.lastMessagesSearchString = null;
                this.searchWas = false;
                notifyDataSetChanged();
                return;
            }
            filterRecent(query);
            this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
            final TLRPC.TL_messages_searchGlobal req = new TLRPC.TL_messages_searchGlobal();
            req.limit = 20;
            req.q = query;
            req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            req.flags |= 1;
            req.folder_id = this.folderId;
            if (query.equals(this.lastMessagesSearchString) && !this.searchResultMessages.isEmpty()) {
                ArrayList<MessageObject> arrayList = this.searchResultMessages;
                MessageObject lastMessage = arrayList.get(arrayList.size() - 1);
                req.offset_id = lastMessage.getId();
                req.offset_rate = this.nextSearchRate;
                long id = MessageObject.getPeerId(lastMessage.messageOwner.peer_id);
                req.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(id);
            } else {
                req.offset_rate = 0;
                req.offset_id = 0;
                req.offset_peer = new TLRPC.TL_inputPeerEmpty();
            }
            this.lastMessagesSearchString = query;
            final int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda13
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    DialogsSearchAdapter.this.m1474xbfab1706(query, currentReqId, searchId, req, tLObject, tL_error);
                }
            }, 2);
        }
    }

    /* renamed from: lambda$searchMessagesInternal$1$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1474xbfab1706(final String query, final int currentReqId, final int searchId, final TLRPC.TL_messages_searchGlobal req, final TLObject response, final TLRPC.TL_error error) {
        final ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            LongSparseArray<TLRPC.Chat> chatsMap = new LongSparseArray<>();
            LongSparseArray<TLRPC.User> usersMap = new LongSparseArray<>();
            for (int a = 0; a < res.chats.size(); a++) {
                TLRPC.Chat chat = res.chats.get(a);
                chatsMap.put(chat.id, chat);
            }
            for (int a2 = 0; a2 < res.users.size(); a2++) {
                TLRPC.User user = res.users.get(a2);
                usersMap.put(user.id, user);
            }
            for (int a3 = 0; a3 < res.messages.size(); a3++) {
                TLRPC.Message message = res.messages.get(a3);
                MessageObject messageObject = new MessageObject(this.currentAccount, message, usersMap, chatsMap, false, true);
                messageObjects.add(messageObject);
                messageObject.setQuery(query);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1473x4a30f0c5(currentReqId, searchId, error, query, response, req, messageObjects);
            }
        });
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> */
    /* renamed from: lambda$searchMessagesInternal$0$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1473x4a30f0c5(int currentReqId, int searchId, TLRPC.TL_error error, String query, TLObject response, TLRPC.TL_messages_searchGlobal req, ArrayList messageObjects) {
        boolean z;
        if (currentReqId == this.lastReqId && (searchId <= 0 || searchId == this.lastSearchId)) {
            this.waitingResponseCount--;
            if (error == null) {
                this.currentMessagesQuery = query;
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
                if (req.offset_id == 0) {
                    this.searchResultMessages.clear();
                }
                this.nextSearchRate = res.next_rate;
                for (int a = 0; a < res.messages.size(); a++) {
                    TLRPC.Message message = res.messages.get(a);
                    long did = MessageObject.getDialogId(message);
                    int maxId = MessagesController.getInstance(this.currentAccount).deletedHistory.get(did);
                    if (maxId == 0 || message.id > maxId) {
                        this.searchResultMessages.add((MessageObject) messageObjects.get(a));
                        long dialog_id = MessageObject.getDialogId(message);
                        ConcurrentHashMap<Long, Integer> concurrentHashMap = message.out ? MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max : MessagesController.getInstance(this.currentAccount).dialogs_read_inbox_max;
                        Integer value = concurrentHashMap.get(Long.valueOf(dialog_id));
                        if (value == null) {
                            value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, dialog_id));
                            concurrentHashMap.put(Long.valueOf(dialog_id), value);
                        }
                        message.unread = value.intValue() < message.id;
                    }
                }
                this.searchWas = true;
                this.messagesSearchEndReached = res.messages.size() != 20;
                if (searchId > 0) {
                    this.lastMessagesSearchId = searchId;
                    if (this.lastLocalSearchId != searchId) {
                        this.searchResult.clear();
                    }
                    if (this.lastGlobalSearchId != searchId) {
                        this.searchAdapterHelper.clear();
                    }
                }
                this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                if (dialogsSearchAdapterDelegate == null) {
                    z = true;
                } else {
                    z = true;
                    dialogsSearchAdapterDelegate.searchStateChanged(this.waitingResponseCount > 0, true);
                    this.delegate.runResultsEnterAnimation();
                }
                this.globalSearchCollapsed = z;
                this.phoneCollapsed = z;
                notifyDataSetChanged();
            }
        }
        this.reqId = 0;
    }

    public boolean hasRecentSearch() {
        int i = this.dialogsType;
        return (i == 2 || i == 4 || i == 5 || i == 6 || i == 11 || getRecentItemsCount() <= 0) ? false : true;
    }

    public boolean isSearchWas() {
        return this.searchWas;
    }

    public boolean isRecentSearchDisplayed() {
        return this.needMessagesSearch != 2 && hasRecentSearch();
    }

    public void loadRecentSearch() {
        loadRecentSearch(this.currentAccount, this.dialogsType, new OnRecentSearchLoaded() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda14
            @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.OnRecentSearchLoaded
            public final void setRecentSearch(ArrayList arrayList, LongSparseArray longSparseArray) {
                DialogsSearchAdapter.this.m1457xa34cacff(arrayList, longSparseArray);
            }
        });
    }

    public static void loadRecentSearch(final int currentAccount, final int dialogsType, final OnRecentSearchLoaded callback) {
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.lambda$loadRecentSearch$5(currentAccount, dialogsType, callback);
            }
        });
    }

    public static /* synthetic */ void lambda$loadRecentSearch$5(int currentAccount, int dialogsType, final OnRecentSearchLoaded callback) {
        Exception e;
        final ArrayList<RecentSearchObject> arrayList;
        final LongSparseArray<RecentSearchObject> hashMap;
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(currentAccount).getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            ArrayList<Integer> encryptedToLoad = new ArrayList<>();
            new ArrayList();
            arrayList = new ArrayList<>();
            hashMap = new LongSparseArray<>();
            while (cursor.next()) {
                long did = cursor.longValue(0);
                boolean add = false;
                if (DialogObject.isEncryptedDialog(did)) {
                    if (dialogsType == 0 || dialogsType == 3) {
                        int encryptedChatId = DialogObject.getEncryptedChatId(did);
                        if (!encryptedToLoad.contains(Integer.valueOf(encryptedChatId))) {
                            encryptedToLoad.add(Integer.valueOf(encryptedChatId));
                            add = true;
                        }
                    }
                } else if (DialogObject.isUserDialog(did)) {
                    if (dialogsType != 2 && !usersToLoad.contains(Long.valueOf(did))) {
                        usersToLoad.add(Long.valueOf(did));
                        add = true;
                    }
                } else if (!chatsToLoad.contains(Long.valueOf(-did))) {
                    chatsToLoad.add(Long.valueOf(-did));
                    add = true;
                }
                if (add) {
                    RecentSearchObject recentSearchObject = new RecentSearchObject();
                    recentSearchObject.did = did;
                    recentSearchObject.date = cursor.intValue(1);
                    arrayList.add(recentSearchObject);
                    hashMap.put(recentSearchObject.did, recentSearchObject);
                }
            }
            cursor.dispose();
            ArrayList<TLRPC.User> users = new ArrayList<>();
            if (!encryptedToLoad.isEmpty()) {
                ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
                MessagesStorage.getInstance(currentAccount).getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad), encryptedChats, usersToLoad);
                for (int a = 0; a < encryptedChats.size(); a++) {
                    RecentSearchObject recentSearchObject2 = hashMap.get(DialogObject.makeEncryptedDialogId(encryptedChats.get(a).id));
                    if (recentSearchObject2 != null) {
                        recentSearchObject2.object = encryptedChats.get(a);
                    }
                }
            }
            if (!chatsToLoad.isEmpty()) {
                ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                MessagesStorage.getInstance(currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                for (int a2 = 0; a2 < chats.size(); a2++) {
                    TLRPC.Chat chat = chats.get(a2);
                    long did2 = -chat.id;
                    if (chat.migrated_to != null) {
                        RecentSearchObject recentSearchObject3 = hashMap.get(did2);
                        hashMap.remove(did2);
                        if (recentSearchObject3 != null) {
                            arrayList.remove(recentSearchObject3);
                        }
                    } else {
                        RecentSearchObject recentSearchObject4 = hashMap.get(did2);
                        if (recentSearchObject4 != null) {
                            recentSearchObject4.object = chat;
                        }
                    }
                }
            }
            if (!usersToLoad.isEmpty()) {
                MessagesStorage.getInstance(currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                for (int a3 = 0; a3 < users.size(); a3++) {
                    TLRPC.User user = users.get(a3);
                    RecentSearchObject recentSearchObject5 = hashMap.get(user.id);
                    if (recentSearchObject5 != null) {
                        recentSearchObject5.object = user;
                    }
                }
            }
            Collections.sort(arrayList, DialogsSearchAdapter$$ExternalSyntheticLambda10.INSTANCE);
        } catch (Exception e2) {
            e = e2;
        }
        try {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    DialogsSearchAdapter.OnRecentSearchLoaded.this.setRecentSearch(arrayList, hashMap);
                }
            });
        } catch (Exception e3) {
            e = e3;
            FileLog.e(e);
        }
    }

    public static /* synthetic */ int lambda$loadRecentSearch$3(RecentSearchObject lhs, RecentSearchObject rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public void putRecentSearch(final long did, TLObject object) {
        RecentSearchObject recentSearchObject = this.recentSearchObjectsById.get(did);
        if (recentSearchObject == null) {
            recentSearchObject = new RecentSearchObject();
            this.recentSearchObjectsById.put(did, recentSearchObject);
        } else {
            this.recentSearchObjects.remove(recentSearchObject);
        }
        this.recentSearchObjects.add(0, recentSearchObject);
        recentSearchObject.did = did;
        recentSearchObject.object = object;
        recentSearchObject.date = (int) (System.currentTimeMillis() / 1000);
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1467x2f7d5292(did);
            }
        });
    }

    /* renamed from: lambda$putRecentSearch$6$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1467x2f7d5292(long did) {
        try {
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
            state.requery();
            state.bindLong(1, did);
            state.bindInteger(2, (int) (System.currentTimeMillis() / 1000));
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearRecentSearch() {
        StringBuilder queryFilter = null;
        if (this.searchWas) {
            while (this.filteredRecentSearchObjects.size() > 0) {
                RecentSearchObject obj = this.filteredRecentSearchObjects.remove(0);
                this.recentSearchObjects.remove(obj);
                this.recentSearchObjectsById.remove(obj.did);
                if (queryFilter == null) {
                    queryFilter = new StringBuilder("did IN (");
                    queryFilter.append(obj.did);
                } else {
                    queryFilter.append(", ");
                    queryFilter.append(obj.did);
                }
            }
            if (queryFilter == null) {
                queryFilter = new StringBuilder(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
            } else {
                queryFilter.append(")");
            }
        } else {
            this.filteredRecentSearchObjects.clear();
            this.recentSearchObjects.clear();
            this.recentSearchObjectsById.clear();
            queryFilter = new StringBuilder(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
        }
        final StringBuilder finalQueryFilter = queryFilter;
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1456x988c9e51(finalQueryFilter);
            }
        });
    }

    /* renamed from: lambda$clearRecentSearch$7$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1456x988c9e51(StringBuilder finalQueryFilter) {
        try {
            finalQueryFilter.insert(0, "DELETE FROM search_recent WHERE ");
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast(finalQueryFilter.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void removeRecentSearch(final long did) {
        RecentSearchObject object = this.recentSearchObjectsById.get(did);
        if (object == null) {
            return;
        }
        this.recentSearchObjectsById.remove(did);
        this.recentSearchObjects.remove(object);
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1468xfb99f9e3(did);
            }
        });
    }

    /* renamed from: lambda$removeRecentSearch$8$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1468xfb99f9e3(long did) {
        try {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
            database.executeFast("DELETE FROM search_recent WHERE did = " + did).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void addHashtagsFromMessage(CharSequence message) {
        this.searchAdapterHelper.addHashtagsFromMessage(message);
    }

    /* renamed from: setRecentSearch */
    public void m1457xa34cacff(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> hashMap) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = hashMap;
        for (int a = 0; a < this.recentSearchObjects.size(); a++) {
            RecentSearchObject recentSearchObject = this.recentSearchObjects.get(a);
            if (recentSearchObject.object instanceof TLRPC.User) {
                MessagesController.getInstance(this.currentAccount).putUser((TLRPC.User) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof TLRPC.Chat) {
                MessagesController.getInstance(this.currentAccount).putChat((TLRPC.Chat) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof TLRPC.EncryptedChat) {
                MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat) recentSearchObject.object, true);
            }
        }
        notifyDataSetChanged();
    }

    private void searchDialogsInternal(final String query, final int searchId) {
        if (this.needMessagesSearch == 2) {
            return;
        }
        final String q = query.trim().toLowerCase();
        if (q.length() == 0) {
            this.lastSearchId = 0;
            updateSearchResults(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), this.lastSearchId);
            return;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1471x16ee3c0b(q, searchId, query);
            }
        });
    }

    /* renamed from: lambda$searchDialogsInternal$10$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1471x16ee3c0b(String q, int searchId, String query) {
        ArrayList<Object> resultArray = new ArrayList<>();
        ArrayList<CharSequence> resultArrayNames = new ArrayList<>();
        ArrayList<TLRPC.User> encUsers = new ArrayList<>();
        MessagesStorage.getInstance(this.currentAccount).localSearch(this.dialogsType, q, resultArray, resultArrayNames, encUsers, -1);
        updateSearchResults(resultArray, resultArrayNames, encUsers, searchId);
        FiltersView.fillTipDates(q, this.localTipDates);
        this.localTipArchive = false;
        if (q.length() >= 3 && (LocaleController.getString("ArchiveSearchFilter", R.string.ArchiveSearchFilter).toLowerCase().startsWith(q) || "archive".startsWith(query))) {
            this.localTipArchive = true;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1472xd91b03c3();
            }
        });
    }

    /* renamed from: lambda$searchDialogsInternal$9$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1472xd91b03c3() {
        FilteredSearchView.Delegate delegate = this.filtersDelegate;
        if (delegate != null) {
            delegate.updateFiltersView(false, null, this.localTipDates, this.localTipArchive);
        }
    }

    private void updateSearchResults(final ArrayList<Object> result, final ArrayList<CharSequence> names, final ArrayList<TLRPC.User> encUsers, final int searchId) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1476xbfa838c8(searchId, result, names, encUsers);
            }
        });
    }

    /* renamed from: lambda$updateSearchResults$12$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1476xbfa838c8(int searchId, ArrayList result, ArrayList names, ArrayList encUsers) {
        this.waitingResponseCount--;
        if (searchId != this.lastSearchId) {
            return;
        }
        this.lastLocalSearchId = searchId;
        if (this.lastGlobalSearchId != searchId) {
            this.searchAdapterHelper.clear();
        }
        if (this.lastMessagesSearchId != searchId) {
            this.searchResultMessages.clear();
        }
        this.searchWas = true;
        int recentCount = this.filteredRecentSearchObjects.size();
        int a = 0;
        while (a < result.size()) {
            final Object obj = result.get(a);
            long dialogId = 0;
            if (obj instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) obj;
                MessagesController.getInstance(this.currentAccount).putUser(user, true);
                dialogId = user.id;
            } else if (obj instanceof TLRPC.Chat) {
                TLRPC.Chat chat = (TLRPC.Chat) obj;
                MessagesController.getInstance(this.currentAccount).putChat(chat, true);
                dialogId = -chat.id;
            } else if (obj instanceof TLRPC.EncryptedChat) {
                MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat) obj, true);
            }
            if (dialogId != 0) {
                TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(dialogId);
                if (dialog == null) {
                    final long finalDialogId = dialogId;
                    MessagesStorage.getInstance(this.currentAccount).getDialogFolderId(dialogId, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda12
                        @Override // org.telegram.messenger.MessagesStorage.IntCallback
                        public final void run(int i) {
                            DialogsSearchAdapter.this.m1475x4a2e1287(finalDialogId, obj, i);
                        }
                    });
                }
            }
            boolean foundInRecent = false;
            int j = 0;
            while (true) {
                if (j < recentCount) {
                    RecentSearchObject o = this.filteredRecentSearchObjects.get(j);
                    if (o == null || o.did != dialogId) {
                        j++;
                    } else {
                        foundInRecent = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (foundInRecent) {
                result.remove(a);
                names.remove(a);
                a--;
            }
            a++;
        }
        int a2 = this.currentAccount;
        MessagesController.getInstance(a2).putUsers(encUsers, true);
        this.searchResult = result;
        this.searchResultNames = names;
        this.searchAdapterHelper.mergeResults(result, this.filteredRecentSearchObjects);
        notifyDataSetChanged();
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.searchStateChanged(this.waitingResponseCount > 0, true);
            this.delegate.runResultsEnterAnimation();
        }
    }

    /* renamed from: lambda$updateSearchResults$11$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1475x4a2e1287(long finalDialogId, Object obj, int param) {
        if (param != -1) {
            TLRPC.Dialog newDialog = new TLRPC.TL_dialog();
            newDialog.id = finalDialogId;
            if (param != 0) {
                newDialog.folder_id = param;
            }
            if (obj instanceof TLRPC.Chat) {
                newDialog.flags = ChatObject.isChannel((TLRPC.Chat) obj) ? 1 : 0;
            }
            MessagesController.getInstance(this.currentAccount).dialogs_dict.put(finalDialogId, newDialog);
            MessagesController.getInstance(this.currentAccount).getAllDialogs().add(newDialog);
            MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        }
    }

    public boolean isHashtagSearch() {
        return !this.searchResultHashtags.isEmpty();
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
    }

    public void searchDialogs(final String text, int folderId) {
        final String query;
        if (text != null && text.equals(this.lastSearchText) && (folderId == this.folderId || TextUtils.isEmpty(text))) {
            return;
        }
        this.lastSearchText = text;
        this.folderId = folderId;
        if (this.searchRunnable != null) {
            Utilities.searchQueue.cancelRunnable(this.searchRunnable);
            this.searchRunnable = null;
        }
        Runnable runnable = this.searchRunnable2;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable2 = null;
        }
        if (text != null) {
            query = text.trim();
        } else {
            query = null;
        }
        if (TextUtils.isEmpty(query)) {
            this.filteredRecentQuery = null;
            this.searchAdapterHelper.unloadRecentHashtags();
            this.searchResult.clear();
            this.searchResultNames.clear();
            this.searchResultHashtags.clear();
            this.searchAdapterHelper.mergeResults(null, null);
            SearchAdapterHelper searchAdapterHelper = this.searchAdapterHelper;
            int i = this.dialogsType;
            searchAdapterHelper.queryServerSearch(null, true, true, i != 11, i != 11, i == 2 || i == 11, 0L, i == 0, 0, 0);
            this.searchWas = false;
            this.lastSearchId = 0;
            this.waitingResponseCount = 0;
            this.globalSearchCollapsed = true;
            this.phoneCollapsed = true;
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
            if (dialogsSearchAdapterDelegate != null) {
                dialogsSearchAdapterDelegate.searchStateChanged(false, true);
            }
            searchMessagesInternal(null, 0);
            notifyDataSetChanged();
            this.localTipDates.clear();
            this.localTipArchive = false;
            FilteredSearchView.Delegate delegate = this.filtersDelegate;
            if (delegate != null) {
                delegate.updateFiltersView(false, null, this.localTipDates, false);
                return;
            }
            return;
        }
        filterRecent(query);
        this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
        if (this.needMessagesSearch != 2 && query.startsWith("#") && query.length() == 1) {
            this.messagesSearchEndReached = true;
            if (this.searchAdapterHelper.loadRecentHashtags()) {
                this.searchResultMessages.clear();
                this.searchResultHashtags.clear();
                ArrayList<SearchAdapterHelper.HashtagObject> hashtags = this.searchAdapterHelper.getHashtags();
                for (int a = 0; a < hashtags.size(); a++) {
                    this.searchResultHashtags.add(hashtags.get(a).hashtag);
                }
                this.globalSearchCollapsed = true;
                this.phoneCollapsed = true;
                this.waitingResponseCount = 0;
                notifyDataSetChanged();
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate2 = this.delegate;
                if (dialogsSearchAdapterDelegate2 != null) {
                    dialogsSearchAdapterDelegate2.searchStateChanged(false, false);
                }
            }
        } else {
            this.searchResultHashtags.clear();
        }
        final int searchId = this.lastSearchId + 1;
        this.lastSearchId = searchId;
        this.waitingResponseCount = 3;
        this.globalSearchCollapsed = true;
        this.phoneCollapsed = true;
        notifyDataSetChanged();
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate3 = this.delegate;
        if (dialogsSearchAdapterDelegate3 != null) {
            dialogsSearchAdapterDelegate3.searchStateChanged(true, false);
        }
        DispatchQueue dispatchQueue = Utilities.searchQueue;
        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1470x4783704c(query, searchId, text);
            }
        };
        this.searchRunnable = runnable2;
        dispatchQueue.postRunnable(runnable2, 300L);
    }

    /* renamed from: lambda$searchDialogs$14$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1470x4783704c(final String query, final int searchId, final String text) {
        this.searchRunnable = null;
        searchDialogsInternal(query, searchId);
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                DialogsSearchAdapter.this.m1469xd2094a0b(searchId, query, text);
            }
        };
        this.searchRunnable2 = runnable;
        AndroidUtilities.runOnUIThread(runnable);
    }

    /* renamed from: lambda$searchDialogs$13$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1469xd2094a0b(int searchId, String query, String text) {
        this.searchRunnable2 = null;
        if (searchId != this.lastSearchId) {
            return;
        }
        if (this.needMessagesSearch == 2) {
            this.waitingResponseCount -= 2;
        } else {
            SearchAdapterHelper searchAdapterHelper = this.searchAdapterHelper;
            int i = this.dialogsType;
            searchAdapterHelper.queryServerSearch(query, true, i != 4, true, (i == 4 || i == 11) ? false : true, i == 2 || i == 1, 0L, i == 0, 0, searchId);
        }
        if (this.needMessagesSearch == 0) {
            this.waitingResponseCount--;
        } else {
            searchMessagesInternal(text, searchId);
        }
    }

    public int getRecentItemsCount() {
        ArrayList<RecentSearchObject> recent = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
        int i = 1;
        int size = !recent.isEmpty() ? recent.size() + 1 : 0;
        if (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
            i = 0;
        }
        return size + i;
    }

    public int getRecentResultsCount() {
        ArrayList<RecentSearchObject> recent = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
        if (recent != null) {
            return recent.size();
        }
        return 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.waitingResponseCount == 3) {
            return 0;
        }
        int count = 0;
        if (!this.searchResultHashtags.isEmpty()) {
            int count2 = 0 + this.searchResultHashtags.size() + 1;
            return count2;
        }
        if (isRecentSearchDisplayed()) {
            count = 0 + getRecentItemsCount();
            if (!this.searchWas) {
                return count;
            }
        }
        int resultsCount = this.searchResult.size();
        int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
        int count3 = count + resultsCount + localServerCount;
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        if (globalCount > 3 && this.globalSearchCollapsed) {
            globalCount = 3;
        }
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (phoneCount > 3 && this.phoneCollapsed) {
            phoneCount = 3;
        }
        int messagesCount = this.searchResultMessages.size();
        if (resultsCount + localServerCount > 0 && getRecentItemsCount() > 0) {
            count3++;
        }
        if (globalCount != 0) {
            count3 += globalCount + 1;
        }
        if (phoneCount != 0) {
            count3 += phoneCount;
        }
        if (messagesCount != 0) {
            count3 += messagesCount + 1 + (!this.messagesSearchEndReached ? 1 : 0);
        }
        this.currentItemCount = count3;
        return count3;
    }

    public Object getItem(int i) {
        TLRPC.Chat chat;
        if (!this.searchResultHashtags.isEmpty()) {
            if (i <= 0) {
                return null;
            }
            return this.searchResultHashtags.get(i - 1);
        }
        int messagesCount = 0;
        if (isRecentSearchDisplayed()) {
            int offset = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
            ArrayList<RecentSearchObject> recent = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
            if (i > offset && (i - 1) - offset < recent.size()) {
                TLObject object = recent.get((i - 1) - offset).object;
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((TLRPC.User) object).id));
                    if (user != null) {
                        return user;
                    }
                    return object;
                } else if ((object instanceof TLRPC.Chat) && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(((TLRPC.Chat) object).id))) != null) {
                    return chat;
                } else {
                    return object;
                }
            }
            i -= getRecentItemsCount();
        }
        ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
        ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
        ArrayList<Object> phoneSearch = this.searchAdapterHelper.getPhoneSearch();
        int localCount = this.searchResult.size();
        int localServerCount = localServerSearch.size();
        if (localCount + localServerCount > 0 && getRecentItemsCount() > 0) {
            if (i == 0) {
                return null;
            }
            i--;
        }
        int phoneCount = phoneSearch.size();
        if (phoneCount > 3 && this.phoneCollapsed) {
            phoneCount = 3;
        }
        int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        if (globalCount > 4 && this.globalSearchCollapsed) {
            globalCount = 4;
        }
        if (!this.searchResultMessages.isEmpty()) {
            messagesCount = this.searchResultMessages.size() + 1;
        }
        if (i >= 0 && i < localCount) {
            return this.searchResult.get(i);
        }
        int i2 = i - localCount;
        if (i2 >= 0 && i2 < localServerCount) {
            return localServerSearch.get(i2);
        }
        int i3 = i2 - localServerCount;
        if (i3 >= 0 && i3 < phoneCount) {
            return phoneSearch.get(i3);
        }
        int i4 = i3 - phoneCount;
        if (i4 > 0 && i4 < globalCount) {
            return globalSearch.get(i4 - 1);
        }
        int i5 = i4 - globalCount;
        if (i5 > 0 && i5 < messagesCount) {
            return this.searchResultMessages.get(i5 - 1);
        }
        return null;
    }

    public boolean isGlobalSearch(int i) {
        if (this.searchWas && this.searchResultHashtags.isEmpty()) {
            if (isRecentSearchDisplayed()) {
                int offset = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
                ArrayList<RecentSearchObject> recent = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
                if (i > offset && (i - 1) - offset < recent.size()) {
                    return false;
                }
                i -= getRecentItemsCount();
            }
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            int localCount = this.searchResult.size();
            int localServerCount = localServerSearch.size();
            int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
            if (phoneCount > 3 && this.phoneCollapsed) {
                phoneCount = 3;
            }
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (globalCount > 4 && this.globalSearchCollapsed) {
                globalCount = 4;
            }
            if (!this.searchResultMessages.isEmpty()) {
                int size = this.searchResultMessages.size() + 1;
            }
            if (i >= 0 && i < localCount) {
                return false;
            }
            int i2 = i - localCount;
            if (i2 >= 0 && i2 < localServerCount) {
                return false;
            }
            int i3 = i2 - localServerCount;
            if (i3 > 0 && i3 < phoneCount) {
                return false;
            }
            int i4 = i3 - phoneCount;
            if (i4 > 0 && i4 < globalCount) {
                return true;
            }
            int i5 = i4 - globalCount;
            return false;
        }
        return false;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int type = holder.getItemViewType();
        return (type == 1 || type == 3) ? false : true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new ProfileSearchCell(this.mContext);
                break;
            case 1:
                view = new GraySectionCell(this.mContext);
                break;
            case 2:
                view = new DialogCell(null, this.mContext, false, true);
                break;
            case 3:
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                flickerLoadingView.setViewType(1);
                flickerLoadingView.setIsSingleCell(true);
                view = flickerLoadingView;
                break;
            case 4:
                view = new HashtagSearchCell(this.mContext);
                break;
            case 5:
                RecyclerListView horizontalListView = new RecyclerListView(this.mContext) { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter.2
                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                    public boolean onInterceptTouchEvent(MotionEvent e) {
                        if (getParent() != null && getParent().getParent() != null) {
                            ViewParent parent2 = getParent().getParent();
                            boolean z = true;
                            if (!canScrollHorizontally(-1) && !canScrollHorizontally(1)) {
                                z = false;
                            }
                            parent2.requestDisallowInterceptTouchEvent(z);
                        }
                        return super.onInterceptTouchEvent(e);
                    }
                };
                horizontalListView.setSelectorDrawableColor(Theme.getColor(Theme.key_listSelector));
                horizontalListView.setTag(9);
                horizontalListView.setItemAnimator(null);
                horizontalListView.setLayoutAnimation(null);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this.mContext) { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter.3
                    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                layoutManager.setOrientation(0);
                horizontalListView.setLayoutManager(layoutManager);
                horizontalListView.setAdapter(new CategoryAdapterRecycler(this.mContext, this.currentAccount, false));
                horizontalListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda15
                    @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                    public final void onItemClick(View view2, int i) {
                        DialogsSearchAdapter.this.m1465xc378b56a(view2, i);
                    }
                });
                horizontalListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda16
                    @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                    public final boolean onItemClick(View view2, int i) {
                        return DialogsSearchAdapter.this.m1466x38f2dbab(view2, i);
                    }
                });
                view = horizontalListView;
                this.innerListView = horizontalListView;
                break;
            default:
                view = new TextCell(this.mContext, 16, false);
                break;
        }
        if (viewType == 5) {
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(86.0f)));
        } else {
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }
        return new RecyclerListView.Holder(view);
    }

    /* renamed from: lambda$onCreateViewHolder$15$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1465xc378b56a(View view1, int position) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.didPressedOnSubDialog(((Long) view1.getTag()).longValue());
        }
    }

    /* renamed from: lambda$onCreateViewHolder$16$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ boolean m1466x38f2dbab(View view12, int position) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needRemoveHint(((Long) view12.getTag()).longValue());
            return true;
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:198:0x045f  */
    /* JADX WARN: Removed duplicated region for block: B:201:0x0477  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x047e  */
    /* JADX WARN: Removed duplicated region for block: B:222:0x04df  */
    /* JADX WARN: Removed duplicated region for block: B:223:0x04e1  */
    /* JADX WARN: Removed duplicated region for block: B:226:0x0506  */
    /* JADX WARN: Removed duplicated region for block: B:227:0x0508  */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r33, final int r34) {
        /*
            Method dump skipped, instructions count: 1314
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    /* renamed from: lambda$onBindViewHolder$17$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1458x6020530b(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* renamed from: lambda$onBindViewHolder$18$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1459xd59a794c(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* renamed from: lambda$onBindViewHolder$19$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1460x4b149f8d(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* renamed from: lambda$onBindViewHolder$20$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1461x6393e923(GraySectionCell cell) {
        String str;
        int i;
        boolean z = !this.phoneCollapsed;
        this.phoneCollapsed = z;
        if (z) {
            i = R.string.ShowMore;
            str = "ShowMore";
        } else {
            i = R.string.ShowLess;
            str = "ShowLess";
        }
        cell.setRightText(LocaleController.getString(str, i));
        notifyDataSetChanged();
    }

    /* renamed from: lambda$onBindViewHolder$23$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1464xc4025be6(ArrayList globalSearch, final int rawPosition, GraySectionCell cell) {
        String str;
        int i;
        long now = SystemClock.elapsedRealtime();
        if (now - this.lastShowMoreUpdate < 300) {
            return;
        }
        this.lastShowMoreUpdate = now;
        int totalGlobalCount = globalSearch.isEmpty() ? 0 : globalSearch.size();
        boolean disableRemoveAnimation = getItemCount() > (Math.min(totalGlobalCount, this.globalSearchCollapsed ? 4 : Integer.MAX_VALUE) + rawPosition) + 1;
        DefaultItemAnimator defaultItemAnimator = this.itemAnimator;
        if (defaultItemAnimator != null) {
            long j = 200;
            defaultItemAnimator.setAddDuration(disableRemoveAnimation ? 45L : 200L);
            DefaultItemAnimator defaultItemAnimator2 = this.itemAnimator;
            if (disableRemoveAnimation) {
                j = 80;
            }
            defaultItemAnimator2.setRemoveDuration(j);
            this.itemAnimator.setRemoveDelay(disableRemoveAnimation ? 270L : 0L);
        }
        boolean z = !this.globalSearchCollapsed;
        this.globalSearchCollapsed = z;
        if (z) {
            i = R.string.ShowMore;
            str = "ShowMore";
        } else {
            i = R.string.ShowLess;
            str = "ShowLess";
        }
        cell.setRightText(LocaleController.getString(str, i), this.globalSearchCollapsed);
        this.showMoreHeader = null;
        final View parent = (View) cell.getParent();
        if (parent instanceof RecyclerView) {
            RecyclerView listView = (RecyclerView) parent;
            int nextGraySectionPosition = !this.globalSearchCollapsed ? rawPosition + 4 : rawPosition + totalGlobalCount + 1;
            int i2 = 0;
            while (true) {
                if (i2 >= listView.getChildCount()) {
                    break;
                }
                View child = listView.getChildAt(i2);
                if (listView.getChildAdapterPosition(child) != nextGraySectionPosition) {
                    i2++;
                } else {
                    this.showMoreHeader = child;
                    break;
                }
            }
        }
        if (!this.globalSearchCollapsed) {
            notifyItemChanged(rawPosition + 3);
            notifyItemRangeInserted(rawPosition + 4, totalGlobalCount - 3);
        } else {
            notifyItemRangeRemoved(rawPosition + 4, totalGlobalCount - 3);
            if (disableRemoveAnimation) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda22
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsSearchAdapter.this.m1462xd90e0f64(rawPosition);
                    }
                }, 350L);
            } else {
                notifyItemChanged(rawPosition + 3);
            }
        }
        Runnable runnable = this.cancelShowMoreAnimation;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        if (disableRemoveAnimation) {
            this.showMoreAnimation = true;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    DialogsSearchAdapter.this.m1463x4e8835a5(parent);
                }
            };
            this.cancelShowMoreAnimation = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 400L);
            return;
        }
        this.showMoreAnimation = false;
    }

    /* renamed from: lambda$onBindViewHolder$21$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1462xd90e0f64(int rawPosition) {
        notifyItemChanged(rawPosition + 3);
    }

    /* renamed from: lambda$onBindViewHolder$22$org-telegram-ui-Adapters-DialogsSearchAdapter */
    public /* synthetic */ void m1463x4e8835a5(View parent) {
        this.showMoreAnimation = false;
        this.showMoreHeader = null;
        if (parent != null) {
            parent.invalidate();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (!this.searchResultHashtags.isEmpty()) {
            return i == 0 ? 1 : 4;
        }
        if (isRecentSearchDisplayed()) {
            int offset = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
            if (i < offset) {
                return 5;
            }
            if (i == offset) {
                return 1;
            }
            if (i < getRecentItemsCount()) {
                return 0;
            }
            i -= getRecentItemsCount();
        }
        ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
        int localCount = this.searchResult.size();
        int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
        if (localCount + localServerCount > 0 && getRecentItemsCount() > 0) {
            if (i == 0) {
                return 1;
            }
            i--;
        }
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (phoneCount > 3 && this.phoneCollapsed) {
            phoneCount = 3;
        }
        int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        if (globalCount > 4 && this.globalSearchCollapsed) {
            globalCount = 4;
        }
        int messagesCount = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
        if (i >= 0 && i < localCount) {
            return 0;
        }
        int i2 = i - localCount;
        if (i2 >= 0 && i2 < localServerCount) {
            return 0;
        }
        int i3 = i2 - localServerCount;
        if (i3 >= 0 && i3 < phoneCount) {
            Object object = getItem(i3);
            if (!(object instanceof String)) {
                return 0;
            }
            String str = (String) object;
            return "section".equals(str) ? 1 : 6;
        }
        int i4 = i3 - phoneCount;
        if (i4 >= 0 && i4 < globalCount) {
            return i4 == 0 ? 1 : 0;
        }
        int i5 = i4 - globalCount;
        if (i5 < 0 || i5 >= messagesCount) {
            return 3;
        }
        return i5 == 0 ? 1 : 2;
    }

    public void setFiltersDelegate(FilteredSearchView.Delegate filtersDelegate, boolean update) {
        this.filtersDelegate = filtersDelegate;
        if (filtersDelegate != null && update) {
            filtersDelegate.updateFiltersView(false, null, this.localTipDates, this.localTipArchive);
        }
    }

    public int getCurrentItemCount() {
        return this.currentItemCount;
    }

    public void filterRecent(String query) {
        this.filteredRecentQuery = query;
        this.filteredRecentSearchObjects.clear();
        if (TextUtils.isEmpty(query)) {
            return;
        }
        String lowerCasedQuery = query.toLowerCase();
        int count = this.recentSearchObjects.size();
        for (int i = 0; i < count; i++) {
            RecentSearchObject obj = this.recentSearchObjects.get(i);
            if (obj != null && obj.object != null) {
                String title = null;
                String username = null;
                if (obj.object instanceof TLRPC.Chat) {
                    title = ((TLRPC.Chat) obj.object).title;
                    username = ((TLRPC.Chat) obj.object).username;
                } else if (obj.object instanceof TLRPC.User) {
                    title = UserObject.getUserName((TLRPC.User) obj.object);
                    username = ((TLRPC.User) obj.object).username;
                } else if (obj.object instanceof TLRPC.ChatInvite) {
                    title = ((TLRPC.ChatInvite) obj.object).title;
                }
                if ((title != null && wordStartsWith(title.toLowerCase(), lowerCasedQuery)) || (username != null && wordStartsWith(username.toLowerCase(), lowerCasedQuery))) {
                    this.filteredRecentSearchObjects.add(obj);
                }
                if (this.filteredRecentSearchObjects.size() >= 5) {
                    return;
                }
            }
        }
    }

    private boolean wordStartsWith(String loweredTitle, String loweredQuery) {
        if (loweredQuery == null || loweredTitle == null) {
            return false;
        }
        String[] words = loweredTitle.toLowerCase().split(" ");
        for (int j = 0; j < words.length; j++) {
            if (words[j] != null && (words[j].startsWith(loweredQuery) || loweredQuery.startsWith(words[j]))) {
                return true;
            }
        }
        return false;
    }
}
