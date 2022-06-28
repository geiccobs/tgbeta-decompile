package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class SearchAdapter extends RecyclerListView.SelectionAdapter {
    private boolean allowBots;
    private boolean allowChats;
    private boolean allowPhoneNumbers;
    private boolean allowSelf;
    private boolean allowUsernameSearch;
    private long channelId;
    private LongSparseArray<?> checkedMap;
    private LongSparseArray<TLRPC.User> ignoreUsers;
    private Context mContext;
    private boolean onlyMutual;
    private SearchAdapterHelper searchAdapterHelper;
    private boolean searchInProgress;
    private int searchPointer;
    private int searchReqId;
    private ArrayList<Object> searchResult = new ArrayList<>();
    private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
    private Timer searchTimer;
    private boolean useUserCell;

    public SearchAdapter(Context context, LongSparseArray<TLRPC.User> arg1, boolean usernameSearch, boolean mutual, boolean chats, boolean bots, boolean self, boolean phones, int searchChannelId) {
        this.mContext = context;
        this.ignoreUsers = arg1;
        this.onlyMutual = mutual;
        this.allowUsernameSearch = usernameSearch;
        this.allowChats = chats;
        this.allowBots = bots;
        this.channelId = searchChannelId;
        this.allowSelf = self;
        this.allowPhoneNumbers = phones;
        SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper;
        searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.Adapters.SearchAdapter.1
            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public /* synthetic */ boolean canApplySearchResults(int i) {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
            }

            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
            }

            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public void onDataSetChanged(int searchId) {
                SearchAdapter.this.notifyDataSetChanged();
                if (searchId != 0) {
                    SearchAdapter.this.onSearchProgressChanged();
                }
            }

            @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
            public LongSparseArray<TLRPC.User> getExcludeUsers() {
                return SearchAdapter.this.ignoreUsers;
            }
        });
    }

    public void setCheckedMap(LongSparseArray<?> map) {
        this.checkedMap = map;
    }

    public void setUseUserCell(boolean value) {
        this.useUserCell = value;
    }

    public void searchDialogs(final String query) {
        try {
            Timer timer = this.searchTimer;
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.searchResult.clear();
        this.searchResultNames.clear();
        if (this.allowUsernameSearch) {
            this.searchAdapterHelper.queryServerSearch(null, true, this.allowChats, this.allowBots, this.allowSelf, false, this.channelId, this.allowPhoneNumbers, 0, 0);
        }
        notifyDataSetChanged();
        if (!TextUtils.isEmpty(query)) {
            Timer timer2 = new Timer();
            this.searchTimer = timer2;
            timer2.schedule(new TimerTask() { // from class: org.telegram.ui.Adapters.SearchAdapter.2
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    try {
                        SearchAdapter.this.searchTimer.cancel();
                        SearchAdapter.this.searchTimer = null;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200L, 300L);
        }
    }

    public void processSearch(final String query) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.SearchAdapter$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SearchAdapter.this.m1493lambda$processSearch$1$orgtelegramuiAdaptersSearchAdapter(query);
            }
        });
    }

    /* renamed from: lambda$processSearch$1$org-telegram-ui-Adapters-SearchAdapter */
    public /* synthetic */ void m1493lambda$processSearch$1$orgtelegramuiAdaptersSearchAdapter(final String query) {
        if (this.allowUsernameSearch) {
            this.searchAdapterHelper.queryServerSearch(query, true, this.allowChats, this.allowBots, this.allowSelf, false, this.channelId, this.allowPhoneNumbers, -1, 1);
        }
        final int currentAccount = UserConfig.selectedAccount;
        final ArrayList<TLRPC.TL_contact> contactsCopy = new ArrayList<>(ContactsController.getInstance(currentAccount).contacts);
        this.searchInProgress = true;
        int i = this.searchPointer;
        this.searchPointer = i + 1;
        this.searchReqId = i;
        final int searchReqIdFinal = this.searchReqId;
        Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Adapters.SearchAdapter$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SearchAdapter.this.m1492lambda$processSearch$0$orgtelegramuiAdaptersSearchAdapter(query, searchReqIdFinal, contactsCopy, currentAccount);
            }
        });
    }

    /* renamed from: lambda$processSearch$0$org-telegram-ui-Adapters-SearchAdapter */
    public /* synthetic */ void m1492lambda$processSearch$0$orgtelegramuiAdaptersSearchAdapter(String query, int searchReqIdFinal, ArrayList contactsCopy, int currentAccount) {
        String[] search;
        String search1;
        String search2;
        boolean z;
        String search12 = query.trim().toLowerCase();
        if (search12.length() == 0) {
            updateSearchResults(searchReqIdFinal, new ArrayList<>(), new ArrayList<>());
            return;
        }
        String search22 = LocaleController.getInstance().getTranslitString(search12);
        if (search12.equals(search22) || search22.length() == 0) {
            search22 = null;
        }
        char c = 0;
        char c2 = 1;
        String[] search3 = new String[(search22 != null ? 1 : 0) + 1];
        search3[0] = search12;
        if (search22 != null) {
            search3[1] = search22;
        }
        ArrayList<Object> resultArray = new ArrayList<>();
        ArrayList<CharSequence> resultArrayNames = new ArrayList<>();
        int a = 0;
        while (a < contactsCopy.size()) {
            TLRPC.TL_contact contact = (TLRPC.TL_contact) contactsCopy.get(a);
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(contact.user_id));
            if ((this.allowSelf || !user.self) && (!this.onlyMutual || user.mutual_contact)) {
                LongSparseArray<TLRPC.User> longSparseArray = this.ignoreUsers;
                if (longSparseArray != null && longSparseArray.indexOfKey(contact.user_id) >= 0) {
                    search1 = search12;
                    search2 = search22;
                    search = search3;
                } else {
                    String[] names = new String[3];
                    names[c] = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                    names[c2] = LocaleController.getInstance().getTranslitString(names[c]);
                    if (names[c].equals(names[c2])) {
                        names[c2] = null;
                    }
                    if (UserObject.isReplyUser(user)) {
                        names[2] = LocaleController.getString("RepliesTitle", R.string.RepliesTitle).toLowerCase();
                    } else if (user.self) {
                        names[2] = LocaleController.getString("SavedMessages", R.string.SavedMessages).toLowerCase();
                    }
                    boolean z2 = false;
                    int length = search3.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            search1 = search12;
                            search2 = search22;
                            search = search3;
                            break;
                        }
                        String q = search3[i];
                        search1 = search12;
                        int i2 = 0;
                        while (true) {
                            search2 = search22;
                            if (i2 >= names.length) {
                                search = search3;
                                break;
                            }
                            String name = names[i2];
                            if (name == null) {
                                z = z2;
                                search = search3;
                            } else if (name.startsWith(q)) {
                                search = search3;
                                break;
                            } else {
                                z = z2;
                                StringBuilder sb = new StringBuilder();
                                search = search3;
                                sb.append(" ");
                                sb.append(q);
                                if (name.contains(sb.toString())) {
                                    break;
                                }
                            }
                            i2++;
                            search22 = search2;
                            z2 = z;
                            search3 = search;
                        }
                        z2 = true;
                        if (!z2 && user.username != null && user.username.startsWith(q)) {
                            z2 = true;
                        }
                        if (!z2) {
                            i++;
                            search22 = search2;
                            search12 = search1;
                            search3 = search;
                        } else {
                            if (z2) {
                                resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                            } else {
                                resultArrayNames.add(AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q));
                            }
                            resultArray.add(user);
                        }
                    }
                }
            } else {
                search1 = search12;
                search2 = search22;
                search = search3;
            }
            a++;
            search22 = search2;
            search12 = search1;
            search3 = search;
            c = 0;
            c2 = 1;
        }
        updateSearchResults(searchReqIdFinal, resultArray, resultArrayNames);
    }

    private void updateSearchResults(final int searchReqIdFinal, final ArrayList<Object> users, final ArrayList<CharSequence> names) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.SearchAdapter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SearchAdapter.this.m1494x2c9031e2(searchReqIdFinal, users, names);
            }
        });
    }

    /* renamed from: lambda$updateSearchResults$2$org-telegram-ui-Adapters-SearchAdapter */
    public /* synthetic */ void m1494x2c9031e2(int searchReqIdFinal, ArrayList users, ArrayList names) {
        if (searchReqIdFinal == this.searchReqId) {
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            this.searchInProgress = false;
            notifyDataSetChanged();
            onSearchProgressChanged();
        }
    }

    protected void onSearchProgressChanged() {
    }

    public boolean searchInProgress() {
        return this.searchInProgress || this.searchAdapterHelper.isSearchInProgress();
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int type = holder.getItemViewType();
        return type == 0 || type == 2;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        int count = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        if (globalCount != 0) {
            count += globalCount + 1;
        }
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (phoneCount != 0) {
            return count + phoneCount;
        }
        return count;
    }

    public boolean isGlobalSearch(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (i >= 0 && i < localCount) {
            return false;
        }
        if ((i > localCount && i < localCount + phoneCount) || i <= localCount + phoneCount || i > globalCount + phoneCount + localCount) {
            return false;
        }
        return true;
    }

    public Object getItem(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (i >= 0 && i < localCount) {
            return this.searchResult.get(i);
        }
        int i2 = i - localCount;
        if (i2 >= 0 && i2 < phoneCount) {
            return this.searchAdapterHelper.getPhoneSearch().get(i2);
        }
        int i3 = i2 - phoneCount;
        if (i3 > 0 && i3 <= globalCount) {
            return this.searchAdapterHelper.getGlobalSearch().get(i3 - 1);
        }
        return null;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                if (this.useUserCell) {
                    View view2 = new UserCell(this.mContext, 1, 1, false);
                    if (this.checkedMap != null) {
                        ((UserCell) view2).setChecked(false, false);
                    }
                    view = view2;
                    break;
                } else {
                    view = new ProfileSearchCell(this.mContext);
                    break;
                }
            case 1:
                view = new GraySectionCell(this.mContext);
                break;
            default:
                view = new TextCell(this.mContext, 16, false);
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0144  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x015e  */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
        /*
            Method dump skipped, instructions count: 418
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        Object item = getItem(i);
        if (item == null) {
            return 1;
        }
        if (item instanceof String) {
            String str = (String) item;
            if ("section".equals(str)) {
                return 1;
            }
            return 2;
        }
        return 0;
    }
}
