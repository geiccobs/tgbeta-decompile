package org.telegram.ui.Adapters;

import android.util.Pair;
import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Components.ShareAlert;
/* loaded from: classes4.dex */
public class SearchAdapterHelper {
    private boolean allResultsAreGlobal;
    private SearchAdapterHelperDelegate delegate;
    private ArrayList<HashtagObject> hashtags;
    private HashMap<String, HashtagObject> hashtagsByText;
    private String lastFoundChannel;
    private ArrayList<DialogsSearchAdapter.RecentSearchObject> localRecentResults;
    private ArrayList<Object> localSearchResults;
    private ArrayList<Integer> pendingRequestIds = new ArrayList<>();
    private String lastFoundUsername = null;
    private ArrayList<TLObject> localServerSearch = new ArrayList<>();
    private ArrayList<TLObject> globalSearch = new ArrayList<>();
    private LongSparseArray<TLObject> globalSearchMap = new LongSparseArray<>();
    private ArrayList<TLObject> groupSearch = new ArrayList<>();
    private LongSparseArray<TLObject> groupSearchMap = new LongSparseArray<>();
    private LongSparseArray<TLObject> phoneSearchMap = new LongSparseArray<>();
    private ArrayList<Object> phonesSearch = new ArrayList<>();
    private int currentAccount = UserConfig.selectedAccount;
    private boolean allowGlobalResults = true;
    private boolean hashtagsLoadedFromDb = false;

    /* loaded from: classes4.dex */
    public static class HashtagObject {
        int date;
        String hashtag;
    }

    /* loaded from: classes4.dex */
    public interface SearchAdapterHelperDelegate {
        boolean canApplySearchResults(int i);

        LongSparseArray<TLRPC.TL_groupCallParticipant> getExcludeCallParticipants();

        LongSparseArray<TLRPC.User> getExcludeUsers();

        void onDataSetChanged(int i);

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);

        /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$onSetHashtags(SearchAdapterHelperDelegate _this, ArrayList arrayList, HashMap hashMap) {
            }

            public static LongSparseArray $default$getExcludeUsers(SearchAdapterHelperDelegate _this) {
                return null;
            }

            public static LongSparseArray $default$getExcludeCallParticipants(SearchAdapterHelperDelegate _this) {
                return null;
            }

            public static boolean $default$canApplySearchResults(SearchAdapterHelperDelegate _this, int searchId) {
                return true;
            }
        }
    }

    /* loaded from: classes4.dex */
    protected static final class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        protected DialogSearchResult() {
        }
    }

    public SearchAdapterHelper(boolean allAsGlobal) {
        this.allResultsAreGlobal = allAsGlobal;
    }

    public void setAllowGlobalResults(boolean value) {
        this.allowGlobalResults = value;
    }

    public boolean isSearchInProgress() {
        return this.pendingRequestIds.size() > 0;
    }

    public void queryServerSearch(String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, boolean canAddGroupsOnly, long channelId, boolean phoneNumbers, int type, int searchId) {
        queryServerSearch(query, allowUsername, allowChats, allowBots, allowSelf, canAddGroupsOnly, channelId, phoneNumbers, type, searchId, null);
    }

    public void queryServerSearch(final String query, boolean allowUsername, final boolean allowChats, final boolean allowBots, final boolean allowSelf, final boolean canAddGroupsOnly, long channelId, boolean phoneNumbers, int type, final int searchId, final Runnable onEnd) {
        boolean hasChanged;
        int i;
        Iterator<Integer> it = this.pendingRequestIds.iterator();
        while (it.hasNext()) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(it.next().intValue(), true);
        }
        this.pendingRequestIds.clear();
        if (query == null) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            this.delegate.onDataSetChanged(searchId);
            return;
        }
        final ArrayList<Pair<TLObject, RequestDelegate>> requests = new ArrayList<>();
        if (query.length() <= 0) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            hasChanged = true;
        } else {
            if (channelId == 0) {
                this.lastFoundChannel = query.toLowerCase();
            } else {
                TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
                if (type == 1) {
                    req.filter = new TLRPC.TL_channelParticipantsAdmins();
                } else if (type == 3) {
                    req.filter = new TLRPC.TL_channelParticipantsBanned();
                } else if (type == 0) {
                    req.filter = new TLRPC.TL_channelParticipantsKicked();
                } else {
                    req.filter = new TLRPC.TL_channelParticipantsSearch();
                }
                req.filter.q = query;
                req.limit = 50;
                req.offset = 0;
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
                requests.add(new Pair<>(req, new RequestDelegate() { // from class: org.telegram.ui.Adapters.SearchAdapterHelper$$ExternalSyntheticLambda7
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SearchAdapterHelper.this.m1499xb4a01a4(query, allowSelf, tLObject, tL_error);
                    }
                }));
            }
            hasChanged = false;
        }
        if (!allowUsername) {
            i = 3;
        } else if (query.length() > 0) {
            TLRPC.TL_contacts_search req2 = new TLRPC.TL_contacts_search();
            req2.q = query;
            req2.limit = 20;
            i = 3;
            requests.add(new Pair<>(req2, new RequestDelegate() { // from class: org.telegram.ui.Adapters.SearchAdapterHelper$$ExternalSyntheticLambda6
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SearchAdapterHelper.this.m1500xc4c18f43(searchId, allowChats, canAddGroupsOnly, allowBots, allowSelf, query, tLObject, tL_error);
                }
            }));
        } else {
            i = 3;
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            hasChanged = false;
        }
        if (!canAddGroupsOnly && phoneNumbers && query.startsWith("+") && query.length() > i) {
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            String phone = PhoneFormat.stripExceptNumbers(query);
            ArrayList<TLRPC.TL_contact> arrayList = ContactsController.getInstance(this.currentAccount).contacts;
            boolean hasFullMatch = false;
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_contact contact = arrayList.get(a);
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(contact.user_id));
                if (user != null && user.phone != null && user.phone.startsWith(phone)) {
                    if (!hasFullMatch) {
                        hasFullMatch = user.phone.length() == phone.length();
                    }
                    this.phonesSearch.add(user);
                    this.phoneSearchMap.put(user.id, user);
                }
            }
            if (!hasFullMatch) {
                this.phonesSearch.add("section");
                this.phonesSearch.add(phone);
            }
            hasChanged = false;
        }
        if (hasChanged) {
            this.delegate.onDataSetChanged(searchId);
        }
        final AtomicInteger gotResponses = new AtomicInteger(0);
        ArrayList<Pair<TLObject, TLRPC.TL_error>> responses = new ArrayList<>();
        int i2 = 0;
        while (i2 < requests.size()) {
            final int index = i2;
            Pair<TLObject, RequestDelegate> r = requests.get(i2);
            TLObject req3 = (TLObject) r.first;
            responses.add(null);
            final AtomicInteger reqId = new AtomicInteger();
            final ArrayList<Pair<TLObject, TLRPC.TL_error>> arrayList2 = responses;
            reqId.set(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req3, new RequestDelegate() { // from class: org.telegram.ui.Adapters.SearchAdapterHelper$$ExternalSyntheticLambda8
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SearchAdapterHelper.this.m1502x37b0aa81(arrayList2, index, reqId, gotResponses, requests, searchId, onEnd, tLObject, tL_error);
                }
            }));
            this.pendingRequestIds.add(Integer.valueOf(reqId.get()));
            i2++;
            responses = responses;
        }
    }

    /* renamed from: lambda$queryServerSearch$0$org-telegram-ui-Adapters-SearchAdapterHelper */
    public /* synthetic */ void m1499xb4a01a4(String query, boolean allowSelf, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            this.lastFoundChannel = query.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.groupSearch.addAll(res.participants);
            long currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int N = res.participants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.ChannelParticipant participant = res.participants.get(a);
                long peerId = MessageObject.getPeerId(participant.peer);
                if (!allowSelf && peerId == currentUserId) {
                    this.groupSearch.remove(participant);
                } else {
                    this.groupSearchMap.put(peerId, participant);
                }
            }
        }
    }

    /* renamed from: lambda$queryServerSearch$1$org-telegram-ui-Adapters-SearchAdapterHelper */
    public /* synthetic */ void m1500xc4c18f43(int searchId, boolean allowChats, boolean canAddGroupsOnly, boolean allowBots, boolean allowSelf, String query, TLObject response, TLRPC.TL_error error) {
        ArrayList<TLRPC.Peer> arrayList;
        ArrayList<TLRPC.Peer> arrayList2;
        if (this.delegate.canApplySearchResults(searchId) && error == null) {
            TLRPC.TL_contacts_found res = (TLRPC.TL_contacts_found) response;
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            int i = 1;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
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
            int b = 0;
            while (true) {
                long j = 0;
                if (b >= 2) {
                    break;
                }
                if (b == 0) {
                    if (!this.allResultsAreGlobal) {
                        b++;
                        i = 1;
                    } else {
                        arrayList = res.my_results;
                    }
                } else {
                    arrayList = res.results;
                }
                int a3 = 0;
                while (a3 < arrayList.size()) {
                    TLRPC.Peer peer = arrayList.get(a3);
                    TLRPC.User user2 = null;
                    TLRPC.Chat chat2 = null;
                    if (peer.user_id != j) {
                        user2 = usersMap.get(peer.user_id);
                    } else if (peer.chat_id != j) {
                        chat2 = chatsMap.get(peer.chat_id);
                    } else if (peer.channel_id != j) {
                        chat2 = chatsMap.get(peer.channel_id);
                    }
                    if (chat2 != null) {
                        if (allowChats && (!canAddGroupsOnly || ChatObject.canAddBotsToChat(chat2))) {
                            if (!this.allowGlobalResults && ChatObject.isNotInChat(chat2)) {
                                arrayList2 = arrayList;
                            } else {
                                this.globalSearch.add(chat2);
                                this.globalSearchMap.put(-chat2.id, chat2);
                                arrayList2 = arrayList;
                            }
                        } else {
                            arrayList2 = arrayList;
                        }
                    } else if (user2 == null) {
                        arrayList2 = arrayList;
                    } else if (canAddGroupsOnly) {
                        arrayList2 = arrayList;
                    } else if ((!allowBots && user2.bot) || (!allowSelf && user2.self)) {
                        arrayList2 = arrayList;
                    } else if (!this.allowGlobalResults && b == i && !user2.contact) {
                        arrayList2 = arrayList;
                    } else {
                        this.globalSearch.add(user2);
                        arrayList2 = arrayList;
                        this.globalSearchMap.put(user2.id, user2);
                    }
                    a3++;
                    arrayList = arrayList2;
                    i = 1;
                    j = 0;
                }
                b++;
                i = 1;
            }
            if (!this.allResultsAreGlobal) {
                for (int a4 = 0; a4 < res.my_results.size(); a4++) {
                    TLRPC.Peer peer2 = res.my_results.get(a4);
                    TLRPC.User user3 = null;
                    TLRPC.Chat chat3 = null;
                    if (peer2.user_id != 0) {
                        user3 = usersMap.get(peer2.user_id);
                    } else if (peer2.chat_id != 0) {
                        chat3 = chatsMap.get(peer2.chat_id);
                    } else if (peer2.channel_id != 0) {
                        chat3 = chatsMap.get(peer2.channel_id);
                    }
                    if (chat3 != null) {
                        if (allowChats && (!canAddGroupsOnly || ChatObject.canAddBotsToChat(chat3))) {
                            this.localServerSearch.add(chat3);
                            this.globalSearchMap.put(-chat3.id, chat3);
                        }
                    } else if (user3 != null && !canAddGroupsOnly && ((allowBots || !user3.bot) && (allowSelf || !user3.self))) {
                        this.localServerSearch.add(user3);
                        this.globalSearchMap.put(user3.id, user3);
                    }
                }
            }
            this.lastFoundUsername = query.toLowerCase();
        }
    }

    /* renamed from: lambda$queryServerSearch$3$org-telegram-ui-Adapters-SearchAdapterHelper */
    public /* synthetic */ void m1502x37b0aa81(final ArrayList responses, final int index, final AtomicInteger reqId, final AtomicInteger gotResponses, final ArrayList requests, final int searchId, final Runnable onEnd, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.SearchAdapterHelper$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SearchAdapterHelper.this.m1501x7e391ce2(responses, index, response, error, reqId, gotResponses, requests, searchId, onEnd);
            }
        });
    }

    /* renamed from: lambda$queryServerSearch$2$org-telegram-ui-Adapters-SearchAdapterHelper */
    public /* synthetic */ void m1501x7e391ce2(ArrayList responses, int index, TLObject response, TLRPC.TL_error error, AtomicInteger reqId, AtomicInteger gotResponses, ArrayList requests, int searchId, Runnable onEnd) {
        responses.set(index, new Pair(response, error));
        Integer reqIdValue = Integer.valueOf(reqId.get());
        if (!this.pendingRequestIds.contains(reqIdValue)) {
            return;
        }
        this.pendingRequestIds.remove(reqIdValue);
        if (gotResponses.incrementAndGet() == requests.size()) {
            for (int j = 0; j < requests.size(); j++) {
                RequestDelegate callback = (RequestDelegate) ((Pair) requests.get(j)).second;
                Pair<TLObject, TLRPC.TL_error> res = (Pair) responses.get(j);
                if (res != null) {
                    callback.run((TLObject) res.first, (TLRPC.TL_error) res.second);
                }
            }
            removeGroupSearchFromGlobal();
            ArrayList<Object> arrayList = this.localSearchResults;
            if (arrayList != null) {
                mergeResults(arrayList, this.localRecentResults);
            }
            mergeExcludeResults();
            this.delegate.onDataSetChanged(searchId);
            if (onEnd != null) {
                onEnd.run();
            }
        }
    }

    private void removeGroupSearchFromGlobal() {
        if (this.globalSearchMap.size() == 0) {
            return;
        }
        int N = this.groupSearchMap.size();
        for (int a = 0; a < N; a++) {
            long uid = this.groupSearchMap.keyAt(a);
            TLRPC.User u = (TLRPC.User) this.globalSearchMap.get(uid);
            if (u != null) {
                this.globalSearch.remove(u);
                this.localServerSearch.remove(u);
                this.globalSearchMap.remove(u.id);
            }
        }
    }

    public void clear() {
        this.globalSearch.clear();
        this.globalSearchMap.clear();
        this.localServerSearch.clear();
    }

    public void unloadRecentHashtags() {
        this.hashtagsLoadedFromDb = false;
    }

    public boolean loadRecentHashtags() {
        if (this.hashtagsLoadedFromDb) {
            return true;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.SearchAdapterHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SearchAdapterHelper.this.m1497x65aedb6f();
            }
        });
        return false;
    }

    /* renamed from: lambda$loadRecentHashtags$6$org-telegram-ui-Adapters-SearchAdapterHelper */
    public /* synthetic */ void m1497x65aedb6f() {
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
            final ArrayList<HashtagObject> arrayList = new ArrayList<>();
            final HashMap<String, HashtagObject> hashMap = new HashMap<>();
            while (cursor.next()) {
                HashtagObject hashtagObject = new HashtagObject();
                hashtagObject.hashtag = cursor.stringValue(0);
                hashtagObject.date = cursor.intValue(1);
                arrayList.add(hashtagObject);
                hashMap.put(hashtagObject.hashtag, hashtagObject);
            }
            cursor.dispose();
            Collections.sort(arrayList, SearchAdapterHelper$$ExternalSyntheticLambda5.INSTANCE);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.SearchAdapterHelper$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    SearchAdapterHelper.this.m1496xac374dd0(arrayList, hashMap);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ int lambda$loadRecentHashtags$4(HashtagObject lhs, HashtagObject rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public void addGroupMembers(ArrayList<TLObject> participants) {
        this.groupSearch.clear();
        this.groupSearch.addAll(participants);
        int N = participants.size();
        for (int a = 0; a < N; a++) {
            TLObject object = participants.get(a);
            if (object instanceof TLRPC.ChatParticipant) {
                this.groupSearchMap.put(((TLRPC.ChatParticipant) object).user_id, object);
            } else if (object instanceof TLRPC.ChannelParticipant) {
                this.groupSearchMap.put(MessageObject.getPeerId(((TLRPC.ChannelParticipant) object).peer), object);
            }
        }
        removeGroupSearchFromGlobal();
    }

    public void mergeResults(ArrayList<Object> localResults) {
        mergeResults(localResults, null);
    }

    public void mergeResults(ArrayList<Object> localResults, ArrayList<DialogsSearchAdapter.RecentSearchObject> recentResults) {
        this.localSearchResults = localResults;
        this.localRecentResults = recentResults;
        if (this.globalSearchMap.size() != 0) {
            if (localResults == null && recentResults == null) {
                return;
            }
            int recentResultsCount = 0;
            int localResultsCount = localResults == null ? 0 : localResults.size();
            if (recentResults != null) {
                recentResultsCount = recentResults.size();
            }
            int count = localResultsCount + recentResultsCount;
            int a = 0;
            while (a < count) {
                Object obj = a < localResultsCount ? localResults.get(a) : recentResults.get(a - localResultsCount);
                if (obj instanceof DialogsSearchAdapter.RecentSearchObject) {
                    obj = ((DialogsSearchAdapter.RecentSearchObject) obj).object;
                }
                if (obj instanceof ShareAlert.DialogSearchResult) {
                    ShareAlert.DialogSearchResult searchResult = (ShareAlert.DialogSearchResult) obj;
                    obj = searchResult.object;
                }
                if (obj instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) obj;
                    TLRPC.User u = (TLRPC.User) this.globalSearchMap.get(user.id);
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(u.id);
                    }
                    TLObject participant = this.groupSearchMap.get(user.id);
                    if (participant != null) {
                        this.groupSearch.remove(participant);
                        this.groupSearchMap.remove(user.id);
                    }
                    Object object = this.phoneSearchMap.get(user.id);
                    if (object != null) {
                        this.phonesSearch.remove(object);
                        this.phoneSearchMap.remove(user.id);
                    }
                } else if (obj instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) obj;
                    TLRPC.Chat c = (TLRPC.Chat) this.globalSearchMap.get(-chat.id);
                    if (c != null) {
                        this.globalSearch.remove(c);
                        this.localServerSearch.remove(c);
                        this.globalSearchMap.remove(-c.id);
                    }
                }
                a++;
            }
        }
    }

    public void mergeExcludeResults() {
        SearchAdapterHelperDelegate searchAdapterHelperDelegate = this.delegate;
        if (searchAdapterHelperDelegate == null) {
            return;
        }
        LongSparseArray<TLRPC.User> ignoreUsers = searchAdapterHelperDelegate.getExcludeUsers();
        if (ignoreUsers != null) {
            int size = ignoreUsers.size();
            for (int a = 0; a < size; a++) {
                TLRPC.User u = (TLRPC.User) this.globalSearchMap.get(ignoreUsers.keyAt(a));
                if (u != null) {
                    this.globalSearch.remove(u);
                    this.localServerSearch.remove(u);
                    this.globalSearchMap.remove(u.id);
                }
            }
        }
        LongSparseArray<TLRPC.TL_groupCallParticipant> ignoreParticipants = this.delegate.getExcludeCallParticipants();
        if (ignoreParticipants != null) {
            int size2 = ignoreParticipants.size();
            for (int a2 = 0; a2 < size2; a2++) {
                TLRPC.User u2 = (TLRPC.User) this.globalSearchMap.get(ignoreParticipants.keyAt(a2));
                if (u2 != null) {
                    this.globalSearch.remove(u2);
                    this.localServerSearch.remove(u2);
                    this.globalSearchMap.remove(u2.id);
                }
            }
        }
    }

    public void setDelegate(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
        this.delegate = searchAdapterHelperDelegate;
    }

    public void addHashtagsFromMessage(CharSequence message) {
        if (message == null) {
            return;
        }
        boolean changed = false;
        Pattern pattern = Pattern.compile("(^|\\s)#[^0-9][\\w@.]+");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (message.charAt(start) != '@' && message.charAt(start) != '#') {
                start++;
            }
            String hashtag = message.subSequence(start, end).toString();
            if (this.hashtagsByText == null) {
                this.hashtagsByText = new HashMap<>();
                this.hashtags = new ArrayList<>();
            }
            HashtagObject hashtagObject = this.hashtagsByText.get(hashtag);
            if (hashtagObject == null) {
                hashtagObject = new HashtagObject();
                hashtagObject.hashtag = hashtag;
                this.hashtagsByText.put(hashtagObject.hashtag, hashtagObject);
            } else {
                this.hashtags.remove(hashtagObject);
            }
            hashtagObject.date = (int) (System.currentTimeMillis() / 1000);
            this.hashtags.add(0, hashtagObject);
            changed = true;
        }
        if (changed) {
            putRecentHashtags(this.hashtags);
        }
    }

    private void putRecentHashtags(final ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.SearchAdapterHelper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SearchAdapterHelper.this.m1498x31dfee5f(arrayList);
            }
        });
    }

    /* renamed from: lambda$putRecentHashtags$7$org-telegram-ui-Adapters-SearchAdapterHelper */
    public /* synthetic */ void m1498x31dfee5f(ArrayList arrayList) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
            for (int a = 0; a < arrayList.size() && a != 100; a++) {
                HashtagObject hashtagObject = (HashtagObject) arrayList.get(a);
                state.requery();
                state.bindString(1, hashtagObject.hashtag);
                state.bindInteger(2, hashtagObject.date);
                state.step();
            }
            state.dispose();
            if (arrayList.size() > 100) {
                SQLitePreparedStatement state2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = ?");
                for (int a2 = 100; a2 < arrayList.size(); a2++) {
                    state2.requery();
                    state2.bindString(1, ((HashtagObject) arrayList.get(a2)).hashtag);
                    state2.step();
                }
                state2.dispose();
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void removeUserId(long userId) {
        Object object = this.globalSearchMap.get(userId);
        if (object != null) {
            this.globalSearch.remove(object);
        }
        Object object2 = this.groupSearchMap.get(userId);
        if (object2 != null) {
            this.groupSearch.remove(object2);
        }
    }

    public ArrayList<TLObject> getGlobalSearch() {
        return this.globalSearch;
    }

    public ArrayList<Object> getPhoneSearch() {
        return this.phonesSearch;
    }

    public ArrayList<TLObject> getLocalServerSearch() {
        return this.localServerSearch;
    }

    public ArrayList<TLObject> getGroupSearch() {
        return this.groupSearch;
    }

    public ArrayList<HashtagObject> getHashtags() {
        return this.hashtags;
    }

    public String getLastFoundUsername() {
        return this.lastFoundUsername;
    }

    public String getLastFoundChannel() {
        return this.lastFoundChannel;
    }

    public void clearRecentHashtags() {
        this.hashtags = new ArrayList<>();
        this.hashtagsByText = new HashMap<>();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.SearchAdapterHelper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SearchAdapterHelper.this.m1495x6a670640();
            }
        });
    }

    /* renamed from: lambda$clearRecentHashtags$8$org-telegram-ui-Adapters-SearchAdapterHelper */
    public /* synthetic */ void m1495x6a670640() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: setHashtags */
    public void m1496xac374dd0(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
