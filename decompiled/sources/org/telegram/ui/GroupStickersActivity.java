package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.GroupStickersActivity;
/* loaded from: classes4.dex */
public class GroupStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private long chatId;
    private FrameLayout emptyFrameView;
    private StickerEmptyView emptyView;
    private int headerRow;
    private TLRPC.ChatFull info;
    private int infoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private FlickerLoadingView loadingView;
    private boolean removeStickerSet;
    private int rowCount;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searching;
    private TLRPC.TL_messages_stickerSet selectedStickerSet;
    private int selectedStickerSetIndex = -1;
    private int stickersEndRow;
    private int stickersStartRow;

    public GroupStickersActivity(long id) {
        this.chatId = id;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
        updateRows();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        if (this.selectedStickerSet != null || this.removeStickerSet) {
            saveStickerSet();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupStickers", R.string.GroupStickers));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.GroupStickersActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupStickersActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem addItem = menu.addItem(0, R.drawable.ic_ab_search);
        this.searchItem = addItem;
        addItem.setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.GroupStickersActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                if (!GroupStickersActivity.this.searching) {
                    return;
                }
                GroupStickersActivity.this.searchAdapter.onSearchStickers(null);
                GroupStickersActivity.this.searching = false;
                GroupStickersActivity.this.listView.setAdapter(GroupStickersActivity.this.listAdapter);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                GroupStickersActivity.this.searchAdapter.onSearchStickers(text);
                boolean newSearching = !TextUtils.isEmpty(text);
                if (newSearching != GroupStickersActivity.this.searching) {
                    GroupStickersActivity.this.searching = newSearching;
                    if (GroupStickersActivity.this.listView != null) {
                        GroupStickersActivity.this.listView.setAdapter(GroupStickersActivity.this.searching ? GroupStickersActivity.this.searchAdapter : GroupStickersActivity.this.listAdapter);
                    }
                }
            }
        });
        this.searchItem.setSearchFieldHint(LocaleController.getString((int) R.string.Search));
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(true);
        this.listView.setItemAnimator(defaultItemAnimator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.emptyFrameView = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, getResourceProvider());
        this.loadingView = flickerLoadingView;
        flickerLoadingView.setViewType(19);
        this.loadingView.setIsSingleCell(true);
        this.loadingView.setItemsCount((int) Math.ceil(AndroidUtilities.displaySize.y / AndroidUtilities.dpf2(58.0f)));
        this.emptyFrameView.addView(this.loadingView, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.loadingView, 1);
        this.emptyView = stickerEmptyView;
        VerticalPositionAutoAnimator.attach(stickerEmptyView);
        this.emptyFrameView.addView(this.emptyView);
        frameLayout.addView(this.emptyFrameView);
        this.emptyFrameView.setVisibility(8);
        this.listView.setEmptyView(this.emptyFrameView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.GroupStickersActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                GroupStickersActivity.this.m3557lambda$createView$0$orgtelegramuiGroupStickersActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.GroupStickersActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-GroupStickersActivity */
    public /* synthetic */ void m3557lambda$createView$0$orgtelegramuiGroupStickersActivity(View view, int position) {
        if (getParentActivity() == null) {
            return;
        }
        if (!this.searching) {
            if (position >= this.stickersStartRow && position < this.stickersEndRow) {
                TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSets(0).get(position - this.stickersStartRow);
                onStickerSetClicked(view, stickerSet, false);
            }
        } else if (position > this.searchAdapter.searchEntries.size()) {
            onStickerSetClicked(view, (TLRPC.TL_messages_stickerSet) this.searchAdapter.localSearchEntries.get((position - this.searchAdapter.searchEntries.size()) - 1), false);
        } else if (position == this.searchAdapter.searchEntries.size()) {
        } else {
            onStickerSetClicked(view, (TLRPC.TL_messages_stickerSet) this.searchAdapter.searchEntries.get(position), true);
        }
    }

    private void onStickerSetClicked(View view, final TLRPC.TL_messages_stickerSet stickerSet, boolean remote) {
        TLRPC.TL_inputStickerSetShortName tL_inputStickerSetShortName = null;
        if (remote) {
            TLRPC.TL_inputStickerSetShortName inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
            inputStickerSetShortName.short_name = stickerSet.set.short_name;
            tL_inputStickerSetShortName = inputStickerSetShortName;
        }
        StickersAlert stickersAlert = new StickersAlert(getParentActivity(), this, tL_inputStickerSetShortName, !remote ? stickerSet : null, (StickersAlert.StickersAlertDelegate) null);
        final boolean isSelected = ((StickerSetCell) view).isChecked();
        stickersAlert.setCustomButtonDelegate(new StickersAlert.StickersAlertCustomButtonDelegate() { // from class: org.telegram.ui.GroupStickersActivity.4
            @Override // org.telegram.ui.Components.StickersAlert.StickersAlertCustomButtonDelegate
            public String getCustomButtonTextColorKey() {
                return isSelected ? Theme.key_dialogTextRed : Theme.key_featuredStickers_buttonText;
            }

            @Override // org.telegram.ui.Components.StickersAlert.StickersAlertCustomButtonDelegate
            public String getCustomButtonRippleColorKey() {
                if (!isSelected) {
                    return Theme.key_featuredStickers_addButtonPressed;
                }
                return null;
            }

            @Override // org.telegram.ui.Components.StickersAlert.StickersAlertCustomButtonDelegate
            public String getCustomButtonColorKey() {
                if (!isSelected) {
                    return Theme.key_featuredStickers_addButton;
                }
                return null;
            }

            @Override // org.telegram.ui.Components.StickersAlert.StickersAlertCustomButtonDelegate
            public String getCustomButtonText() {
                return LocaleController.getString(isSelected ? R.string.RemoveGroupStickerSet : R.string.SetAsGroupStickerSet);
            }

            @Override // org.telegram.ui.Components.StickersAlert.StickersAlertCustomButtonDelegate
            public boolean onCustomButtonPressed() {
                int row = GroupStickersActivity.this.layoutManager.findFirstVisibleItemPosition();
                int top = Integer.MAX_VALUE;
                RecyclerListView.Holder holder = (RecyclerListView.Holder) GroupStickersActivity.this.listView.findViewHolderForAdapterPosition(row);
                if (holder != null) {
                    top = holder.itemView.getTop();
                }
                int prevIndex = GroupStickersActivity.this.selectedStickerSetIndex;
                if (isSelected) {
                    GroupStickersActivity.this.selectedStickerSet = null;
                    GroupStickersActivity.this.removeStickerSet = true;
                } else {
                    GroupStickersActivity.this.selectedStickerSet = stickerSet;
                    GroupStickersActivity.this.removeStickerSet = false;
                }
                GroupStickersActivity.this.updateSelectedStickerSetIndex();
                if (prevIndex != -1) {
                    boolean found = false;
                    if (!GroupStickersActivity.this.searching) {
                        int i = 0;
                        while (true) {
                            if (i >= GroupStickersActivity.this.listView.getChildCount()) {
                                break;
                            }
                            View ch = GroupStickersActivity.this.listView.getChildAt(i);
                            if (GroupStickersActivity.this.listView.getChildViewHolder(ch).getAdapterPosition() != GroupStickersActivity.this.stickersStartRow + prevIndex) {
                                i++;
                            } else {
                                ((StickerSetCell) ch).setChecked(false, true);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        GroupStickersActivity.this.listAdapter.notifyItemChanged(prevIndex);
                    }
                }
                if (GroupStickersActivity.this.selectedStickerSetIndex != -1) {
                    boolean found2 = false;
                    if (!GroupStickersActivity.this.searching) {
                        int i2 = 0;
                        while (true) {
                            if (i2 >= GroupStickersActivity.this.listView.getChildCount()) {
                                break;
                            }
                            View ch2 = GroupStickersActivity.this.listView.getChildAt(i2);
                            if (GroupStickersActivity.this.listView.getChildViewHolder(ch2).getAdapterPosition() != GroupStickersActivity.this.stickersStartRow + GroupStickersActivity.this.selectedStickerSetIndex) {
                                i2++;
                            } else {
                                ((StickerSetCell) ch2).setChecked(true, true);
                                found2 = true;
                                break;
                            }
                        }
                    }
                    if (!found2) {
                        GroupStickersActivity.this.listAdapter.notifyItemChanged(GroupStickersActivity.this.selectedStickerSetIndex);
                    }
                }
                if (top != Integer.MAX_VALUE) {
                    GroupStickersActivity.this.layoutManager.scrollToPositionWithOffset(row + 1, top);
                }
                if (GroupStickersActivity.this.searching) {
                    GroupStickersActivity.this.searchItem.setSearchFieldText("", false);
                    GroupStickersActivity.this.actionBar.closeSearchField(true);
                }
                return true;
            }
        });
        stickersAlert.show();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (((Integer) args[0]).intValue() == 0) {
                updateRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && chatFull.stickerset != null) {
                    this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                }
                this.info = chatFull;
                updateRows();
            }
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            long setId = ((Long) args[0]).longValue();
            TLRPC.ChatFull chatFull2 = this.info;
            if (chatFull2 != null && chatFull2.stickerset != null && this.info.stickerset.id == setId) {
                updateRows();
            }
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null && chatFull.stickerset != null) {
            this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
        }
    }

    private void saveStickerSet() {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            if (chatFull.stickerset != null && (tL_messages_stickerSet = this.selectedStickerSet) != null && tL_messages_stickerSet.set.id == this.info.stickerset.id) {
                return;
            }
            if (this.info.stickerset == null && this.selectedStickerSet == null) {
                return;
            }
            TLRPC.TL_channels_setStickers req = new TLRPC.TL_channels_setStickers();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
            if (this.removeStickerSet) {
                req.stickerset = new TLRPC.TL_inputStickerSetEmpty();
            } else {
                SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
                edit.remove("group_hide_stickers_" + this.info.id).apply();
                req.stickerset = new TLRPC.TL_inputStickerSetID();
                req.stickerset.id = this.selectedStickerSet.set.id;
                req.stickerset.access_hash = this.selectedStickerSet.set.access_hash;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupStickersActivity$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    GroupStickersActivity.this.m3559lambda$saveStickerSet$2$orgtelegramuiGroupStickersActivity(tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$saveStickerSet$2$org-telegram-ui-GroupStickersActivity */
    public /* synthetic */ void m3559lambda$saveStickerSet$2$orgtelegramuiGroupStickersActivity(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupStickersActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                GroupStickersActivity.this.m3558lambda$saveStickerSet$1$orgtelegramuiGroupStickersActivity(error);
            }
        });
    }

    /* renamed from: lambda$saveStickerSet$1$org-telegram-ui-GroupStickersActivity */
    public /* synthetic */ void m3558lambda$saveStickerSet$1$orgtelegramuiGroupStickersActivity(TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.selectedStickerSet;
            if (tL_messages_stickerSet == null) {
                this.info.stickerset = null;
            } else {
                this.info.stickerset = tL_messages_stickerSet.set;
                MediaDataController.getInstance(this.currentAccount).putGroupStickerSet(this.selectedStickerSet);
            }
            updateSelectedStickerSetIndex();
            if (this.info.stickerset == null) {
                this.info.flags |= 256;
            } else {
                this.info.flags &= -257;
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(this.info, false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, true, false);
            finishFragment();
        } else if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text, 0).show();
        }
    }

    public void updateSelectedStickerSetIndex() {
        long selectedSet;
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
        this.selectedStickerSetIndex = -1;
        if (this.removeStickerSet) {
            selectedSet = 0;
        } else {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.selectedStickerSet;
            if (tL_messages_stickerSet != null) {
                selectedSet = tL_messages_stickerSet.set.id;
            } else {
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull != null && chatFull.stickerset != null) {
                    selectedSet = this.info.stickerset.id;
                } else {
                    selectedSet = 0;
                }
            }
        }
        if (selectedSet != 0) {
            for (int i = 0; i < stickerSets.size(); i++) {
                TLRPC.TL_messages_stickerSet set = stickerSets.get(i);
                if (set.set.id == selectedSet) {
                    this.selectedStickerSetIndex = i;
                    return;
                }
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
        if (!stickerSets.isEmpty()) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.headerRow = i;
            this.stickersStartRow = i2;
            this.stickersEndRow = i2 + stickerSets.size();
            this.rowCount += stickerSets.size();
        } else {
            this.headerRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
        }
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.infoRow = i3;
        updateSelectedStickerSetIndex();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* loaded from: classes4.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private static final int TYPE_MY_STICKERS_HEADER = 1;
        private static final int TYPE_STICKER_SET = 0;
        private Runnable lastCallback;
        private String lastQuery;
        private Context mContext;
        private int reqId;
        private List<TLRPC.TL_messages_stickerSet> searchEntries = new ArrayList();
        private List<TLRPC.TL_messages_stickerSet> localSearchEntries = new ArrayList();

        public SearchAdapter(Context context) {
            GroupStickersActivity.this = r1;
            this.mContext = context;
            setHasStableIds(true);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int position) {
            if (getItemViewType(position) == 0) {
                List<TLRPC.TL_messages_stickerSet> arrayList = position > this.searchEntries.size() ? this.localSearchEntries : this.searchEntries;
                int row = position > this.searchEntries.size() ? (position - this.searchEntries.size()) - 1 : position;
                return arrayList.get(row).set.id;
            }
            return -1L;
        }

        public void onSearchStickers(final String query) {
            if (this.reqId != 0) {
                GroupStickersActivity.this.getConnectionsManager().cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            Runnable runnable = this.lastCallback;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.lastCallback = null;
            }
            this.lastQuery = null;
            int count = getItemCount();
            if (count > 0) {
                this.searchEntries.clear();
                this.localSearchEntries.clear();
                notifyItemRangeRemoved(0, count);
            }
            if (TextUtils.isEmpty(query)) {
                GroupStickersActivity.this.emptyView.setVisibility(8);
                GroupStickersActivity.this.emptyView.showProgress(false, true);
                return;
            }
            if (GroupStickersActivity.this.emptyView.getVisibility() != 0) {
                GroupStickersActivity.this.emptyView.setVisibility(0);
                GroupStickersActivity.this.emptyView.showProgress(true, false);
            } else {
                GroupStickersActivity.this.emptyView.showProgress(true, true);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GroupStickersActivity.SearchAdapter.this.m3562x6d5579f5(query);
                }
            };
            this.lastCallback = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
        }

        /* renamed from: lambda$onSearchStickers$2$org-telegram-ui-GroupStickersActivity$SearchAdapter */
        public /* synthetic */ void m3562x6d5579f5(final String query) {
            this.lastQuery = query;
            final TLRPC.TL_messages_searchStickerSets searchStickerSets = new TLRPC.TL_messages_searchStickerSets();
            searchStickerSets.q = query;
            this.reqId = GroupStickersActivity.this.getConnectionsManager().sendRequest(searchStickerSets, new RequestDelegate() { // from class: org.telegram.ui.GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    GroupStickersActivity.SearchAdapter.this.m3561xe01ac874(searchStickerSets, query, tLObject, tL_error);
                }
            }, 66);
        }

        /* renamed from: lambda$onSearchStickers$1$org-telegram-ui-GroupStickersActivity$SearchAdapter */
        public /* synthetic */ void m3561xe01ac874(TLRPC.TL_messages_searchStickerSets searchStickerSets, final String query, TLObject response, TLRPC.TL_error error) {
            if (ColorUtils$$ExternalSyntheticBackport0.m(this.lastQuery, searchStickerSets.q) && (response instanceof TLRPC.TL_messages_foundStickerSets)) {
                final List<TLRPC.TL_messages_stickerSet> newSearchEntries = new ArrayList<>();
                TLRPC.TL_messages_foundStickerSets foundStickerSets = (TLRPC.TL_messages_foundStickerSets) response;
                Iterator<TLRPC.StickerSetCovered> it = foundStickerSets.sets.iterator();
                while (it.hasNext()) {
                    TLRPC.StickerSetCovered stickerSetCovered = it.next();
                    TLRPC.TL_messages_stickerSet set = new TLRPC.TL_messages_stickerSet();
                    set.set = stickerSetCovered.set;
                    set.documents = stickerSetCovered.covers;
                    newSearchEntries.add(set);
                }
                String lowQuery = query.toLowerCase(Locale.ROOT).trim();
                final List<TLRPC.TL_messages_stickerSet> newLocalEntries = new ArrayList<>();
                Iterator<TLRPC.TL_messages_stickerSet> it2 = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0).iterator();
                while (it2.hasNext()) {
                    TLRPC.TL_messages_stickerSet localSet = it2.next();
                    if (localSet.set.short_name.toLowerCase(Locale.ROOT).contains(lowQuery) || localSet.set.title.toLowerCase(Locale.ROOT).contains(lowQuery)) {
                        newLocalEntries.add(localSet);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        GroupStickersActivity.SearchAdapter.this.m3560x52e016f3(newSearchEntries, newLocalEntries, query);
                    }
                });
            }
        }

        /* renamed from: lambda$onSearchStickers$0$org-telegram-ui-GroupStickersActivity$SearchAdapter */
        public /* synthetic */ void m3560x52e016f3(List newSearchEntries, List newLocalEntries, String query) {
            this.searchEntries = newSearchEntries;
            this.localSearchEntries = newLocalEntries;
            notifyDataSetChanged();
            GroupStickersActivity.this.emptyView.title.setVisibility(8);
            GroupStickersActivity.this.emptyView.subtitle.setText(LocaleController.formatString(R.string.ChooseStickerNoResultsFound, query));
            GroupStickersActivity.this.emptyView.showProgress(false, true);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new StickerSetCell(this.mContext, 3);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteGrayText4, 21, 0, 0, false, GroupStickersActivity.this.getResourceProvider());
                    view.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    ((HeaderCell) view).setText(LocaleController.getString((int) R.string.ChooseStickerMyStickerSets));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long id;
            switch (getItemViewType(position)) {
                case 0:
                    boolean z = true;
                    boolean local = position > this.searchEntries.size();
                    List<TLRPC.TL_messages_stickerSet> arrayList = local ? this.localSearchEntries : this.searchEntries;
                    int row = local ? (position - this.searchEntries.size()) - 1 : position;
                    StickerSetCell cell = (StickerSetCell) holder.itemView;
                    TLRPC.TL_messages_stickerSet set = arrayList.get(row);
                    cell.setStickersSet(set, row != arrayList.size() - 1, !local);
                    String str = this.lastQuery;
                    cell.setSearchQuery(set, str != null ? str.toLowerCase(Locale.ROOT) : "", GroupStickersActivity.this.getResourceProvider());
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        id = GroupStickersActivity.this.selectedStickerSet.set.id;
                    } else {
                        id = (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) ? 0L : GroupStickersActivity.this.info.stickerset.id;
                    }
                    if (set.set.id != id) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            return this.searchEntries.size() == position ? 1 : 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.searchEntries.size() + this.localSearchEntries.size() + (!this.localSearchEntries.isEmpty() ? 1 : 0);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = getItemViewType(holder.getAdapterPosition());
            return viewType == 0;
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int TYPE_CHOOSE_HEADER = 4;
        private static final int TYPE_INFO = 1;
        private static final int TYPE_STICKER_SET = 0;
        private Context mContext;

        public ListAdapter(Context context) {
            GroupStickersActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long id;
            switch (holder.getItemViewType()) {
                case 0:
                    ArrayList<TLRPC.TL_messages_stickerSet> arrayList = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                    int row = position - GroupStickersActivity.this.stickersStartRow;
                    StickerSetCell cell = (StickerSetCell) holder.itemView;
                    TLRPC.TL_messages_stickerSet set = arrayList.get(row);
                    boolean z = true;
                    cell.setStickersSet(arrayList.get(row), row != arrayList.size() - 1);
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        id = GroupStickersActivity.this.selectedStickerSet.set.id;
                    } else {
                        id = (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) ? 0L : GroupStickersActivity.this.info.stickerset.id;
                    }
                    if (set.set.id != id) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                    return;
                case 1:
                    if (position == GroupStickersActivity.this.infoRow) {
                        String text = LocaleController.getString("ChooseStickerSetMy", R.string.ChooseStickerSetMy);
                        int index = text.indexOf("@stickers");
                        if (index != -1) {
                            try {
                                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                                URLSpanNoUnderline spanNoUnderline = new URLSpanNoUnderline("@stickers") { // from class: org.telegram.ui.GroupStickersActivity.ListAdapter.1
                                    @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
                                    public void onClick(View widget) {
                                        MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                                    }
                                };
                                stringBuilder.setSpan(spanNoUnderline, index, "@stickers".length() + index, 18);
                                ((TextInfoPrivacyCell) holder.itemView).setText(stringBuilder);
                                return;
                            } catch (Exception e) {
                                FileLog.e(e);
                                ((TextInfoPrivacyCell) holder.itemView).setText(text);
                                return;
                            }
                        }
                        ((TextInfoPrivacyCell) holder.itemView).setText(text);
                        return;
                    }
                    return;
                case 2:
                case 3:
                default:
                    return;
                case 4:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString((int) R.string.ChooseStickerSetHeader));
                    return;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new StickerSetCell(this.mContext, 3);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                default:
                    View view3 = new HeaderCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i < GroupStickersActivity.this.stickersStartRow || i >= GroupStickersActivity.this.stickersEndRow) {
                if (i == GroupStickersActivity.this.headerRow) {
                    return 4;
                }
                return i == GroupStickersActivity.this.infoRow ? 1 : 0;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menuSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menu));
        return themeDescriptions;
    }
}
