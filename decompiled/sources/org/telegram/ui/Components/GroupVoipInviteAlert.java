package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class GroupVoipInviteAlert extends UsersAlertBase {
    private int addNewRow;
    private boolean contactsEndReached;
    private int contactsEndRow;
    private int contactsHeaderRow;
    private int contactsStartRow;
    private TLRPC.Chat currentChat;
    private int delayResults;
    private GroupVoipInviteAlertDelegate delegate;
    private int emptyRow;
    private boolean firstLoaded;
    private int flickerProgressRow;
    private LongSparseArray<TLRPC.TL_groupCallParticipant> ignoredUsers;
    private TLRPC.ChatFull info;
    private HashSet<Long> invitedUsers;
    private int lastRow;
    private boolean loadingUsers;
    private int membersHeaderRow;
    private int participantsEndRow;
    private int participantsStartRow;
    private int rowCount;
    private final SearchAdapter searchAdapter;
    private boolean showContacts;
    private ArrayList<TLObject> participants = new ArrayList<>();
    private ArrayList<TLObject> contacts = new ArrayList<>();
    private LongSparseArray<TLObject> participantsMap = new LongSparseArray<>();
    private LongSparseArray<TLObject> contactsMap = new LongSparseArray<>();

    /* loaded from: classes5.dex */
    public interface GroupVoipInviteAlertDelegate {
        void copyInviteLink();

        void inviteUser(long j);

        void needOpenSearch(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor);
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    protected void updateColorKeys() {
        this.keyScrollUp = Theme.key_voipgroup_scrollUp;
        this.keyListSelector = Theme.key_voipgroup_listSelector;
        this.keySearchBackground = Theme.key_voipgroup_searchBackground;
        this.keyInviteMembersBackground = Theme.key_voipgroup_inviteMembersBackground;
        this.keyListViewBackground = Theme.key_voipgroup_listViewBackground;
        this.keyActionBarUnscrolled = Theme.key_voipgroup_actionBarUnscrolled;
        this.keyNameText = Theme.key_voipgroup_nameText;
        this.keyLastSeenText = Theme.key_voipgroup_lastSeenText;
        this.keyLastSeenTextUnscrolled = Theme.key_voipgroup_lastSeenTextUnscrolled;
        this.keySearchPlaceholder = Theme.key_voipgroup_searchPlaceholder;
        this.keySearchText = Theme.key_voipgroup_searchText;
        this.keySearchIcon = Theme.key_voipgroup_mutedIcon;
        this.keySearchIconUnscrolled = Theme.key_voipgroup_mutedIconUnscrolled;
    }

    public GroupVoipInviteAlert(Context context, int account, TLRPC.Chat chat, TLRPC.ChatFull chatFull, LongSparseArray<TLRPC.TL_groupCallParticipant> participants, HashSet<Long> invited) {
        super(context, false, account, null);
        setDimBehindAlpha(75);
        this.currentChat = chat;
        this.info = chatFull;
        this.ignoredUsers = participants;
        this.invitedUsers = invited;
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                GroupVoipInviteAlert.this.m2668lambda$new$0$orgtelegramuiComponentsGroupVoipInviteAlert(view, i);
            }
        });
        SearchAdapter searchAdapter = new SearchAdapter(context);
        this.searchAdapter = searchAdapter;
        this.searchListViewAdapter = searchAdapter;
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        loadChatParticipants(0, 200);
        updateRows();
        setColorProgress(0.0f);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GroupVoipInviteAlert */
    public /* synthetic */ void m2668lambda$new$0$orgtelegramuiComponentsGroupVoipInviteAlert(View view, int position) {
        if (position == this.addNewRow) {
            this.delegate.copyInviteLink();
            dismiss();
        } else if (view instanceof ManageChatUserCell) {
            ManageChatUserCell cell = (ManageChatUserCell) view;
            if (this.invitedUsers.contains(Long.valueOf(cell.getUserId()))) {
                return;
            }
            this.delegate.inviteUser(cell.getUserId());
        }
    }

    public void setDelegate(GroupVoipInviteAlertDelegate groupVoipInviteAlertDelegate) {
        this.delegate = groupVoipInviteAlertDelegate;
    }

    private void updateRows() {
        this.addNewRow = -1;
        this.emptyRow = -1;
        this.participantsStartRow = -1;
        this.participantsEndRow = -1;
        this.contactsHeaderRow = -1;
        this.contactsStartRow = -1;
        this.contactsEndRow = -1;
        this.membersHeaderRow = -1;
        this.lastRow = -1;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.emptyRow = 0;
        if (!TextUtils.isEmpty(this.currentChat.username) || ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addNewRow = i;
        }
        if (!this.loadingUsers || this.firstLoaded) {
            boolean hasAnyOther = false;
            if (!this.contacts.isEmpty()) {
                int i2 = this.rowCount;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.contactsHeaderRow = i2;
                this.contactsStartRow = i3;
                int size = i3 + this.contacts.size();
                this.rowCount = size;
                this.contactsEndRow = size;
                hasAnyOther = true;
            }
            if (!this.participants.isEmpty()) {
                if (hasAnyOther) {
                    int i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.membersHeaderRow = i4;
                }
                int i5 = this.rowCount;
                this.participantsStartRow = i5;
                int size2 = i5 + this.participants.size();
                this.rowCount = size2;
                this.participantsEndRow = size2;
            }
        }
        boolean hasAnyOther2 = this.loadingUsers;
        if (hasAnyOther2) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.flickerProgressRow = i6;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.lastRow = i7;
    }

    private void loadChatParticipants(int offset, int count) {
        if (this.loadingUsers) {
            return;
        }
        this.contactsEndReached = false;
        loadChatParticipants(offset, count, true);
    }

    private void fillContacts() {
        if (!this.showContacts) {
            return;
        }
        this.contacts.addAll(ContactsController.getInstance(this.currentAccount).contacts);
        long selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
        int a = 0;
        int N = this.contacts.size();
        while (a < N) {
            TLObject object = this.contacts.get(a);
            if (object instanceof TLRPC.TL_contact) {
                long userId = ((TLRPC.TL_contact) object).user_id;
                if (userId == selfId || this.ignoredUsers.indexOfKey(userId) >= 0 || this.invitedUsers.contains(Long.valueOf(userId))) {
                    this.contacts.remove(a);
                    a--;
                    N--;
                }
            }
            a++;
        }
        int a2 = this.currentAccount;
        final int currentTime = ConnectionsManager.getInstance(a2).getCurrentTime();
        final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        Collections.sort(this.contacts, new Comparator() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return GroupVoipInviteAlert.lambda$fillContacts$1(MessagesController.this, currentTime, (TLObject) obj, (TLObject) obj2);
            }
        });
    }

    public static /* synthetic */ int lambda$fillContacts$1(MessagesController messagesController, int currentTime, TLObject o1, TLObject o2) {
        TLRPC.User user1 = messagesController.getUser(Long.valueOf(((TLRPC.TL_contact) o2).user_id));
        TLRPC.User user2 = messagesController.getUser(Long.valueOf(((TLRPC.TL_contact) o1).user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 > 0 && status2 > 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if (status1 < 0 && status2 < 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
            return -1;
        } else {
            return (status2 < 0 || status1 != 0) ? 1 : 0;
        }
    }

    protected void loadChatParticipants(int offset, int count, boolean reset) {
        LongSparseArray<TLRPC.TL_groupCallParticipant> longSparseArray;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            if (this.info != null) {
                long selfUserId = UserConfig.getInstance(this.currentAccount).clientUserId;
                int size = this.info.participants.participants.size();
                for (int a = 0; a < size; a++) {
                    TLRPC.ChatParticipant participant = this.info.participants.participants.get(a);
                    if (participant.user_id != selfUserId && ((longSparseArray = this.ignoredUsers) == null || longSparseArray.indexOfKey(participant.user_id) < 0)) {
                        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(participant.user_id));
                        if (!UserObject.isDeleted(user) && !user.bot) {
                            this.participants.add(participant);
                            this.participantsMap.put(participant.user_id, participant);
                        }
                    }
                }
                if (this.participants.isEmpty()) {
                    this.showContacts = true;
                    fillContacts();
                }
            }
            updateRows();
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.loadingUsers = true;
        if (this.emptyView != null) {
            this.emptyView.showProgress(true, false);
        }
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        final TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = MessagesController.getInputChannel(this.currentChat);
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null && chatFull.participants_count <= 200) {
            req.filter = new TLRPC.TL_channelParticipantsRecent();
        } else if (!this.contactsEndReached) {
            this.delayResults = 2;
            req.filter = new TLRPC.TL_channelParticipantsContacts();
            this.contactsEndReached = true;
            loadChatParticipants(0, 200, false);
        } else {
            req.filter = new TLRPC.TL_channelParticipantsRecent();
        }
        req.filter.q = "";
        req.offset = offset;
        req.limit = count;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupVoipInviteAlert.this.m2667x8a32b9c(req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadChatParticipants$4$org-telegram-ui-Components-GroupVoipInviteAlert */
    public /* synthetic */ void m2667x8a32b9c(final TLRPC.TL_channels_getParticipants req, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                GroupVoipInviteAlert.this.m2666x919919b(error, response, req);
            }
        });
    }

    /* renamed from: lambda$loadChatParticipants$3$org-telegram-ui-Components-GroupVoipInviteAlert */
    public /* synthetic */ void m2666x919919b(TLRPC.TL_error error, TLObject response, TLRPC.TL_channels_getParticipants req) {
        int num;
        LongSparseArray<TLObject> map;
        ArrayList<TLObject> objects;
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            long selfId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int a = 0;
            while (true) {
                if (a < res.participants.size()) {
                    if (MessageObject.getPeerId(res.participants.get(a).peer) != selfId) {
                        a++;
                    } else {
                        res.participants.remove(a);
                        break;
                    }
                } else {
                    break;
                }
            }
            int a2 = this.delayResults;
            this.delayResults = a2 - 1;
            if (req.filter instanceof TLRPC.TL_channelParticipantsContacts) {
                ArrayList<TLObject> objects2 = this.contacts;
                map = this.contactsMap;
                objects = objects2;
            } else {
                ArrayList<TLObject> objects3 = this.participants;
                map = this.participantsMap;
                objects = objects3;
            }
            objects.clear();
            objects.addAll(res.participants);
            int size = res.participants.size();
            for (int a3 = 0; a3 < size; a3++) {
                TLRPC.ChannelParticipant participant = res.participants.get(a3);
                map.put(MessageObject.getPeerId(participant.peer), participant);
            }
            int a4 = 0;
            int N = this.participants.size();
            while (a4 < N) {
                long peerId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) this.participants.get(a4)).peer);
                boolean remove = false;
                if (this.contactsMap.get(peerId) != null) {
                    remove = true;
                } else {
                    LongSparseArray<TLRPC.TL_groupCallParticipant> longSparseArray = this.ignoredUsers;
                    if (longSparseArray != null && longSparseArray.indexOfKey(peerId) >= 0) {
                        remove = true;
                    }
                }
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId));
                if ((user != null && user.bot) || UserObject.isDeleted(user)) {
                    remove = true;
                }
                if (remove) {
                    this.participants.remove(a4);
                    this.participantsMap.remove(peerId);
                    a4--;
                    N--;
                }
                a4++;
            }
            try {
                if (this.info.participants_count <= 200) {
                    final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                    Collections.sort(objects, new Comparator() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$$ExternalSyntheticLambda2
                        @Override // java.util.Comparator
                        public final int compare(Object obj, Object obj2) {
                            return GroupVoipInviteAlert.this.m2665x98ff79a(currentTime, (TLObject) obj, (TLObject) obj2);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (this.delayResults <= 0) {
            this.loadingUsers = false;
            this.firstLoaded = true;
            if (this.flickerProgressRow == 1) {
                num = 1;
            } else {
                num = this.listViewAdapter != null ? this.listViewAdapter.getItemCount() - 1 : 0;
            }
            showItemsAnimated(num);
            if (this.participants.isEmpty()) {
                this.showContacts = true;
                fillContacts();
            }
        }
        updateRows();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
            if (this.emptyView != null && this.listViewAdapter.getItemCount() == 0 && this.firstLoaded) {
                this.emptyView.showProgress(false, true);
            }
        }
    }

    /* renamed from: lambda$loadChatParticipants$2$org-telegram-ui-Components-GroupVoipInviteAlert */
    public /* synthetic */ int m2665x98ff79a(int currentTime, TLObject lhs, TLObject rhs) {
        TLRPC.ChannelParticipant p1 = (TLRPC.ChannelParticipant) lhs;
        TLRPC.ChannelParticipant p2 = (TLRPC.ChannelParticipant) rhs;
        TLRPC.User user1 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(p1.peer)));
        TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(p2.peer)));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null && user1.status != null) {
            status1 = user1.self ? currentTime + 50000 : user1.status.expires;
        }
        if (user2 != null && user2.status != null) {
            status2 = user2.self ? currentTime + 50000 : user2.status.expires;
        }
        if (status1 > 0 && status2 > 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if (status1 < 0 && status2 < 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
            return -1;
        } else {
            return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
        }
    }

    /* loaded from: classes5.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int emptyRow;
        private int globalStartRow;
        private int groupStartRow;
        private int lastRow;
        private int lastSearchId;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private boolean searchInProgress;
        private Runnable searchRunnable;
        private int totalCount;

        public SearchAdapter(Context context) {
            GroupVoipInviteAlert.this = r3;
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.1
                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public void onDataSetChanged(int searchId) {
                    if (searchId < 0 || searchId != SearchAdapter.this.lastSearchId || SearchAdapter.this.searchInProgress) {
                        return;
                    }
                    boolean emptyViewWasVisible = true;
                    int oldItemCount = SearchAdapter.this.getItemCount() - 1;
                    if (GroupVoipInviteAlert.this.emptyView.getVisibility() != 0) {
                        emptyViewWasVisible = false;
                    }
                    SearchAdapter.this.notifyDataSetChanged();
                    if (SearchAdapter.this.getItemCount() > oldItemCount) {
                        GroupVoipInviteAlert.this.showItemsAnimated(oldItemCount);
                    }
                    if (!SearchAdapter.this.searchAdapterHelper.isSearchInProgress() && GroupVoipInviteAlert.this.listView.emptyViewIsVisible()) {
                        GroupVoipInviteAlert.this.emptyView.showProgress(false, emptyViewWasVisible);
                    }
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public LongSparseArray<TLRPC.TL_groupCallParticipant> getExcludeCallParticipants() {
                    return GroupVoipInviteAlert.this.ignoredUsers;
                }
            });
        }

        public void searchUsers(final String query) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, true, false, true, false, false, GroupVoipInviteAlert.this.currentChat.id, false, 2, -1);
            if (!TextUtils.isEmpty(query)) {
                GroupVoipInviteAlert.this.emptyView.showProgress(true, true);
                GroupVoipInviteAlert.this.listView.setAnimateEmptyView(false, 0);
                notifyDataSetChanged();
                GroupVoipInviteAlert.this.listView.setAnimateEmptyView(true, 0);
                this.searchInProgress = true;
                final int searchId = this.lastSearchId + 1;
                this.lastSearchId = searchId;
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        GroupVoipInviteAlert.SearchAdapter.this.m2671xc1aff363(query, searchId);
                    }
                };
                this.searchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
                if (GroupVoipInviteAlert.this.listView.getAdapter() != GroupVoipInviteAlert.this.searchListViewAdapter) {
                    GroupVoipInviteAlert.this.listView.setAdapter(GroupVoipInviteAlert.this.searchListViewAdapter);
                    return;
                }
                return;
            }
            this.lastSearchId = -1;
        }

        /* renamed from: lambda$searchUsers$0$org-telegram-ui-Components-GroupVoipInviteAlert$SearchAdapter */
        public /* synthetic */ void m2671xc1aff363(String query, int searchId) {
            if (this.searchRunnable == null) {
                return;
            }
            this.searchRunnable = null;
            processSearch(query, searchId);
        }

        private void processSearch(final String query, final int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    GroupVoipInviteAlert.SearchAdapter.this.m2670x7920c6dc(query, searchId);
                }
            });
        }

        /* renamed from: lambda$processSearch$2$org-telegram-ui-Components-GroupVoipInviteAlert$SearchAdapter */
        public /* synthetic */ void m2670x7920c6dc(final String query, final int searchId) {
            final ArrayList<TLObject> participantsCopy = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) && GroupVoipInviteAlert.this.info != null) {
                participantsCopy = new ArrayList<>(GroupVoipInviteAlert.this.info.participants.participants);
            }
            if (participantsCopy != null) {
                Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        GroupVoipInviteAlert.SearchAdapter.this.m2669x8591429b(query, searchId, participantsCopy);
                    }
                });
            } else {
                this.searchInProgress = false;
            }
            this.searchAdapterHelper.queryServerSearch(query, ChatObject.canAddUsers(GroupVoipInviteAlert.this.currentChat), false, true, false, false, ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) ? GroupVoipInviteAlert.this.currentChat.id : 0L, false, 2, searchId);
        }

        /* JADX WARN: Code restructure failed: missing block: B:42:0x00ea, code lost:
            if (r15.contains(" " + r3) != false) goto L49;
         */
        /* JADX WARN: Removed duplicated region for block: B:52:0x010a A[LOOP:1: B:33:0x00a8->B:52:0x010a, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:63:0x0106 A[SYNTHETIC] */
        /* renamed from: lambda$processSearch$1$org-telegram-ui-Components-GroupVoipInviteAlert$SearchAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m2669x8591429b(java.lang.String r22, int r23, java.util.ArrayList r24) {
            /*
                Method dump skipped, instructions count: 301
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.m2669x8591429b(java.lang.String, int, java.util.ArrayList):void");
        }

        private void updateSearchResults(final ArrayList<TLObject> participants, final int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GroupVoipInviteAlert.SearchAdapter.this.m2672x4ff98acb(searchId, participants);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$3$org-telegram-ui-Components-GroupVoipInviteAlert$SearchAdapter */
        public /* synthetic */ void m2672x4ff98acb(int searchId, ArrayList participants) {
            if (searchId != this.lastSearchId) {
                return;
            }
            this.searchInProgress = false;
            if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat)) {
                this.searchAdapterHelper.addGroupMembers(participants);
            }
            boolean emptyViewWasVisible = true;
            int oldItemCount = getItemCount() - 1;
            if (GroupVoipInviteAlert.this.emptyView.getVisibility() != 0) {
                emptyViewWasVisible = false;
            }
            notifyDataSetChanged();
            if (getItemCount() > oldItemCount) {
                GroupVoipInviteAlert.this.showItemsAnimated(oldItemCount);
            }
            if (!this.searchInProgress && !this.searchAdapterHelper.isSearchInProgress() && GroupVoipInviteAlert.this.listView.emptyViewIsVisible()) {
                GroupVoipInviteAlert.this.emptyView.showProgress(false, emptyViewWasVisible);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ManageChatUserCell cell = (ManageChatUserCell) holder.itemView;
                if (GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(cell.getUserId()))) {
                    return false;
                }
            }
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.totalCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.totalCount = 0;
            this.totalCount = 0 + 1;
            this.emptyRow = 0;
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                int i = this.totalCount;
                this.groupStartRow = i;
                this.totalCount = i + count + 1;
            } else {
                this.groupStartRow = -1;
            }
            int count2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count2 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + count2 + 1;
            } else {
                this.globalStartRow = -1;
            }
            int i3 = this.totalCount;
            this.totalCount = i3 + 1;
            this.lastRow = i3;
            super.notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int i2 = this.groupStartRow;
            if (i2 >= 0 && i > i2 && i < i2 + 1 + this.searchAdapterHelper.getGroupSearch().size()) {
                return this.searchAdapterHelper.getGroupSearch().get((i - this.groupStartRow) - 1);
            }
            int i3 = this.globalStartRow;
            if (i3 >= 0 && i > i3 && i < i3 + 1 + this.searchAdapterHelper.getGlobalSearch().size()) {
                return this.searchAdapterHelper.getGlobalSearch().get((i - this.globalStartRow) - 1);
            }
            return null;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 2, 2, false);
                    manageChatUserCell.setCustomRightImage(R.drawable.msg_invited);
                    manageChatUserCell.setNameColor(Theme.getColor(Theme.key_voipgroup_nameText));
                    manageChatUserCell.setStatusColors(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled), Theme.getColor(Theme.key_voipgroup_listeningText));
                    manageChatUserCell.setDividerColor(Theme.key_voipgroup_listViewBackground);
                    view = manageChatUserCell;
                    break;
                case 1:
                    GraySectionCell cell = new GraySectionCell(this.mContext);
                    cell.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_actionBarUnscrolled));
                    cell.setTextColor(Theme.key_voipgroup_searchPlaceholder);
                    view = cell;
                    break;
                case 2:
                    View view2 = new View(this.mContext);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    view = view2;
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:65:0x0149  */
        /* JADX WARN: Removed duplicated region for block: B:68:0x016e  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                Method dump skipped, instructions count: 414
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == this.emptyRow) {
                return 2;
            }
            if (i == this.lastRow) {
                return 3;
            }
            if (i == this.globalStartRow || i == this.groupStartRow) {
                return 1;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            GroupVoipInviteAlert.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ManageChatUserCell cell = (ManageChatUserCell) holder.itemView;
                if (GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(cell.getUserId()))) {
                    return false;
                }
            }
            int viewType = holder.getItemViewType();
            return viewType == 0 || viewType == 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return GroupVoipInviteAlert.this.rowCount;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 6, 2, false);
                    manageChatUserCell.setCustomRightImage(R.drawable.msg_invited);
                    manageChatUserCell.setNameColor(Theme.getColor(Theme.key_voipgroup_nameText));
                    manageChatUserCell.setStatusColors(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled), Theme.getColor(Theme.key_voipgroup_listeningText));
                    manageChatUserCell.setDividerColor(Theme.key_voipgroup_actionBar);
                    view = manageChatUserCell;
                    break;
                case 1:
                    ManageChatTextCell manageChatTextCell = new ManageChatTextCell(this.mContext);
                    manageChatTextCell.setColors(Theme.key_voipgroup_listeningText, Theme.key_voipgroup_listeningText);
                    manageChatTextCell.setDividerColor(Theme.key_voipgroup_actionBar);
                    view = manageChatTextCell;
                    break;
                case 2:
                    GraySectionCell cell = new GraySectionCell(this.mContext);
                    cell.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_actionBarUnscrolled));
                    cell.setTextColor(Theme.key_voipgroup_searchPlaceholder);
                    view = cell;
                    break;
                case 3:
                    View view2 = new View(this.mContext);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    view = view2;
                    break;
                case 4:
                default:
                    view = new View(this.mContext);
                    break;
                case 5:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setViewType(6);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setColors(Theme.key_voipgroup_inviteMembersBackground, Theme.key_voipgroup_searchBackground, Theme.key_voipgroup_actionBarUnscrolled);
                    view = flickerLoadingView;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long userId;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    TLObject item = getItem(position);
                    int lastRow = (position < GroupVoipInviteAlert.this.participantsStartRow || position >= GroupVoipInviteAlert.this.participantsEndRow) ? GroupVoipInviteAlert.this.contactsEndRow : GroupVoipInviteAlert.this.participantsEndRow;
                    if (item instanceof TLRPC.TL_contact) {
                        TLRPC.TL_contact contact = (TLRPC.TL_contact) item;
                        userId = contact.user_id;
                    } else if (item instanceof TLRPC.User) {
                        userId = ((TLRPC.User) item).id;
                    } else if (item instanceof TLRPC.ChannelParticipant) {
                        TLRPC.ChannelParticipant participant = (TLRPC.ChannelParticipant) item;
                        userId = MessageObject.getPeerId(participant.peer);
                    } else {
                        TLRPC.ChatParticipant participant2 = (TLRPC.ChatParticipant) item;
                        userId = participant2.user_id;
                    }
                    TLRPC.User user = MessagesController.getInstance(GroupVoipInviteAlert.this.currentAccount).getUser(Long.valueOf(userId));
                    if (user != null) {
                        userCell.setCustomImageVisible(GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(user.id)));
                        if (position != lastRow - 1) {
                            z = true;
                        }
                        userCell.setData(user, null, null, z);
                        return;
                    }
                    return;
                case 1:
                    ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                    if (position == GroupVoipInviteAlert.this.addNewRow) {
                        boolean showDivider = (!GroupVoipInviteAlert.this.loadingUsers || GroupVoipInviteAlert.this.firstLoaded) && GroupVoipInviteAlert.this.membersHeaderRow == -1 && !GroupVoipInviteAlert.this.participants.isEmpty();
                        actionCell.setText(LocaleController.getString("VoipGroupCopyInviteLink", R.string.VoipGroupCopyInviteLink), null, R.drawable.msg_link, 7, showDivider);
                        return;
                    }
                    return;
                case 2:
                    GraySectionCell sectionCell = (GraySectionCell) holder.itemView;
                    if (position != GroupVoipInviteAlert.this.membersHeaderRow) {
                        if (position == GroupVoipInviteAlert.this.contactsHeaderRow) {
                            if (GroupVoipInviteAlert.this.showContacts) {
                                sectionCell.setText(LocaleController.getString("YourContactsToInvite", R.string.YourContactsToInvite));
                                return;
                            } else {
                                sectionCell.setText(LocaleController.getString("GroupContacts", R.string.GroupContacts));
                                return;
                            }
                        }
                        return;
                    }
                    sectionCell.setText(LocaleController.getString("ChannelOtherMembers", R.string.ChannelOtherMembers));
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if ((position < GroupVoipInviteAlert.this.participantsStartRow || position >= GroupVoipInviteAlert.this.participantsEndRow) && (position < GroupVoipInviteAlert.this.contactsStartRow || position >= GroupVoipInviteAlert.this.contactsEndRow)) {
                if (position != GroupVoipInviteAlert.this.addNewRow) {
                    if (position != GroupVoipInviteAlert.this.membersHeaderRow && position != GroupVoipInviteAlert.this.contactsHeaderRow) {
                        if (position != GroupVoipInviteAlert.this.emptyRow) {
                            if (position == GroupVoipInviteAlert.this.lastRow) {
                                return 4;
                            }
                            return position == GroupVoipInviteAlert.this.flickerProgressRow ? 5 : 0;
                        }
                        return 3;
                    }
                    return 2;
                }
                return 1;
            }
            return 0;
        }

        public TLObject getItem(int position) {
            if (position < GroupVoipInviteAlert.this.participantsStartRow || position >= GroupVoipInviteAlert.this.participantsEndRow) {
                if (position >= GroupVoipInviteAlert.this.contactsStartRow && position < GroupVoipInviteAlert.this.contactsEndRow) {
                    return (TLObject) GroupVoipInviteAlert.this.contacts.get(position - GroupVoipInviteAlert.this.contactsStartRow);
                }
                return null;
            }
            return (TLObject) GroupVoipInviteAlert.this.participants.get(position - GroupVoipInviteAlert.this.participantsStartRow);
        }
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    public void search(String text) {
        this.searchAdapter.searchUsers(text);
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    protected void onSearchViewTouched(MotionEvent ev, EditTextBoldCursor searchEditText) {
        this.delegate.needOpenSearch(ev, searchEditText);
    }
}
