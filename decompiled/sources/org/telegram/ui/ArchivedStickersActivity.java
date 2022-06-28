package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ArchivedStickersActivity;
import org.telegram.ui.Cells.ArchivedStickerSetCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
/* loaded from: classes4.dex */
public class ArchivedStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int archiveInfoRow;
    private int currentType;
    private Runnable doOnTransitionEnd;
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private boolean isInTransition;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingStickers;
    private int rowCount;
    private int stickersEndRow;
    private int stickersLoadingRow;
    private int stickersShadowRow;
    private int stickersStartRow;
    private final LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets = new LongSparseArray<>();
    private ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList<>();

    public ArchivedStickersActivity(int type) {
        this.currentType = type;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getStickers();
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needAddArchivedStickers);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needAddArchivedStickers);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ArchivedStickersActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ArchivedStickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        if (this.currentType == 0) {
            emptyTextProgressView.setText(LocaleController.getString("ArchivedStickersEmpty", R.string.ArchivedStickersEmpty));
        } else {
            emptyTextProgressView.setText(LocaleController.getString("ArchivedMasksEmpty", R.string.ArchivedMasksEmpty));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.loadingStickers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setFocusable(true);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ArchivedStickersActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ArchivedStickersActivity.this.m1511lambda$createView$0$orgtelegramuiArchivedStickersActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ArchivedStickersActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!ArchivedStickersActivity.this.loadingStickers && !ArchivedStickersActivity.this.endReached && ArchivedStickersActivity.this.layoutManager.findLastVisibleItemPosition() > ArchivedStickersActivity.this.stickersLoadingRow - 2) {
                    ArchivedStickersActivity.this.getStickers();
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ArchivedStickersActivity */
    public /* synthetic */ void m1511lambda$createView$0$orgtelegramuiArchivedStickersActivity(final View view, int position) {
        TLRPC.InputStickerSet inputStickerSet;
        if (position >= this.stickersStartRow && position < this.stickersEndRow && getParentActivity() != null) {
            final TLRPC.StickerSetCovered stickerSet = this.sets.get(position - this.stickersStartRow);
            if (stickerSet.set.id != 0) {
                inputStickerSet = new TLRPC.TL_inputStickerSetID();
                inputStickerSet.id = stickerSet.set.id;
            } else {
                inputStickerSet = new TLRPC.TL_inputStickerSetShortName();
                inputStickerSet.short_name = stickerSet.set.short_name;
            }
            inputStickerSet.access_hash = stickerSet.set.access_hash;
            StickersAlert stickersAlert = new StickersAlert(getParentActivity(), this, inputStickerSet, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
            stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() { // from class: org.telegram.ui.ArchivedStickersActivity.2
                @Override // org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate
                public void onStickerSetInstalled() {
                    ((ArchivedStickerSetCell) view).setDrawProgress(true, true);
                    ArchivedStickersActivity.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                }

                @Override // org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate
                public void onStickerSetUninstalled() {
                }
            });
            showDialog(stickersAlert);
        }
    }

    private void updateRows() {
        int i;
        this.rowCount = 0;
        if (!this.sets.isEmpty()) {
            if (this.currentType == 0) {
                i = this.rowCount;
                this.rowCount = i + 1;
            } else {
                i = -1;
            }
            this.archiveInfoRow = i;
            int i2 = this.rowCount;
            this.stickersStartRow = i2;
            this.stickersEndRow = i2 + this.sets.size();
            int size = this.rowCount + this.sets.size();
            this.rowCount = size;
            if (!this.endReached) {
                this.rowCount = size + 1;
                this.stickersLoadingRow = size;
                this.stickersShadowRow = -1;
                return;
            }
            this.rowCount = size + 1;
            this.stickersShadowRow = size;
            this.stickersLoadingRow = -1;
            return;
        }
        this.archiveInfoRow = -1;
        this.stickersStartRow = -1;
        this.stickersEndRow = -1;
        this.stickersLoadingRow = -1;
        this.stickersShadowRow = -1;
    }

    public void getStickers() {
        long j;
        if (this.loadingStickers || this.endReached) {
            return;
        }
        boolean z = true;
        this.loadingStickers = true;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null && !this.firstLoaded) {
            emptyTextProgressView.showProgress();
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        TLRPC.TL_messages_getArchivedStickers req = new TLRPC.TL_messages_getArchivedStickers();
        if (this.sets.isEmpty()) {
            j = 0;
        } else {
            ArrayList<TLRPC.StickerSetCovered> arrayList = this.sets;
            j = arrayList.get(arrayList.size() - 1).set.id;
        }
        req.offset_id = j;
        req.limit = 15;
        if (this.currentType != 1) {
            z = false;
        }
        req.masks = z;
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ArchivedStickersActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ArchivedStickersActivity.this.m1513lambda$getStickers$2$orgtelegramuiArchivedStickersActivity(tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$getStickers$2$org-telegram-ui-ArchivedStickersActivity */
    public /* synthetic */ void m1513lambda$getStickers$2$orgtelegramuiArchivedStickersActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArchivedStickersActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ArchivedStickersActivity.this.m1512lambda$getStickers$1$orgtelegramuiArchivedStickersActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$getStickers$1$org-telegram-ui-ArchivedStickersActivity */
    public /* synthetic */ void m1512lambda$getStickers$1$orgtelegramuiArchivedStickersActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            m1514x2ad8e55f((TLRPC.TL_messages_archivedStickers) response);
        }
    }

    /* renamed from: processResponse */
    public void m1514x2ad8e55f(final TLRPC.TL_messages_archivedStickers res) {
        if (!this.isInTransition) {
            this.sets.addAll(res.sets);
            this.endReached = res.sets.size() != 15;
            this.loadingStickers = false;
            this.firstLoaded = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
            updateRows();
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.doOnTransitionEnd = new Runnable() { // from class: org.telegram.ui.ArchivedStickersActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ArchivedStickersActivity.this.m1514x2ad8e55f(res);
            }
        };
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        this.isInTransition = true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        this.isInTransition = false;
        Runnable runnable = this.doOnTransitionEnd;
        if (runnable != null) {
            runnable.run();
            this.doOnTransitionEnd = null;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        RecyclerListView recyclerListView;
        ArchivedStickerSetCell cell;
        TLRPC.StickerSetCovered stickersSet;
        if (id == NotificationCenter.needAddArchivedStickers) {
            List<TLRPC.StickerSetCovered> newSets = new ArrayList<>((List) args[0]);
            for (int i = newSets.size() - 1; i >= 0; i--) {
                int j = 0;
                int size2 = this.sets.size();
                while (true) {
                    if (j < size2) {
                        if (this.sets.get(j).set.id != newSets.get(i).set.id) {
                            j++;
                        } else {
                            newSets.remove(i);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            if (!newSets.isEmpty()) {
                this.sets.addAll(0, newSets);
                updateRows();
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyItemRangeInserted(this.stickersStartRow, newSets.size());
                }
            }
        } else if (id == NotificationCenter.stickersDidLoad && (recyclerListView = this.listView) != null) {
            int size = recyclerListView.getChildCount();
            for (int i2 = 0; i2 < size; i2++) {
                View view = this.listView.getChildAt(i2);
                if ((view instanceof ArchivedStickerSetCell) && (stickersSet = (cell = (ArchivedStickerSetCell) view).getStickersSet()) != null) {
                    boolean isInstalled = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickersSet.set.id);
                    if (isInstalled) {
                        this.installingStickerSets.remove(stickersSet.set.id);
                        cell.setDrawProgress(false, true);
                    }
                    cell.setChecked(isInstalled, true, false);
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            ArchivedStickersActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ArchivedStickersActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                int stickerSetPosition = position - ArchivedStickersActivity.this.stickersStartRow;
                ArchivedStickerSetCell cell = (ArchivedStickerSetCell) holder.itemView;
                final TLRPC.StickerSetCovered stickerSet = (TLRPC.StickerSetCovered) ArchivedStickersActivity.this.sets.get(stickerSetPosition);
                boolean z = true;
                cell.setStickersSet(stickerSet, stickerSetPosition != ArchivedStickersActivity.this.sets.size() - 1);
                boolean isInstalled = MediaDataController.getInstance(ArchivedStickersActivity.this.currentAccount).isStickerPackInstalled(stickerSet.set.id);
                cell.setChecked(isInstalled, false, false);
                if (isInstalled) {
                    ArchivedStickersActivity.this.installingStickerSets.remove(stickerSet.set.id);
                    cell.setDrawProgress(false, false);
                } else {
                    if (ArchivedStickersActivity.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                        z = false;
                    }
                    cell.setDrawProgress(z, false);
                }
                cell.setOnCheckedChangeListener(new ArchivedStickerSetCell.OnCheckedChangeListener() { // from class: org.telegram.ui.ArchivedStickersActivity$ListAdapter$$ExternalSyntheticLambda0
                    @Override // org.telegram.ui.Cells.ArchivedStickerSetCell.OnCheckedChangeListener
                    public final void onCheckedChanged(ArchivedStickerSetCell archivedStickerSetCell, boolean z2) {
                        ArchivedStickersActivity.ListAdapter.this.m1515x7ffa3458(stickerSet, archivedStickerSetCell, z2);
                    }
                });
            } else if (getItemViewType(position) == 2) {
                TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) holder.itemView;
                if (position == ArchivedStickersActivity.this.archiveInfoRow) {
                    cell2.setTopPadding(17);
                    cell2.setBottomPadding(10);
                    cell2.setText(LocaleController.getString("ArchivedStickersInfo", R.string.ArchivedStickersInfo));
                    return;
                }
                cell2.setTopPadding(10);
                cell2.setBottomPadding(17);
                cell2.setText(null);
            }
        }

        /* renamed from: lambda$onBindViewHolder$0$org-telegram-ui-ArchivedStickersActivity$ListAdapter */
        public /* synthetic */ void m1515x7ffa3458(TLRPC.StickerSetCovered stickerSet, ArchivedStickerSetCell c, boolean isChecked) {
            if (isChecked) {
                c.setChecked(false, false, false);
                if (ArchivedStickersActivity.this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
                    return;
                }
                c.setDrawProgress(true, true);
                ArchivedStickersActivity.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
            }
            MediaDataController.getInstance(ArchivedStickersActivity.this.currentAccount).toggleStickerSet(ArchivedStickersActivity.this.getParentActivity(), stickerSet, !isChecked ? 1 : 2, ArchivedStickersActivity.this, false, false);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ArchivedStickerSetCell(this.mContext, true);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new LoadingCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i < ArchivedStickersActivity.this.stickersStartRow || i >= ArchivedStickersActivity.this.stickersEndRow) {
                if (i == ArchivedStickersActivity.this.stickersLoadingRow) {
                    return 1;
                }
                return (i == ArchivedStickersActivity.this.stickersShadowRow || i == ArchivedStickersActivity.this.archiveInfoRow) ? 2 : 0;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ArchivedStickerSetCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LoadingCell.class, TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_removeButtonText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ArchivedStickerSetCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_removeButtonText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_buttonText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ArchivedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ArchivedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButtonPressed));
        return themeDescriptions;
    }
}
