package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes5.dex */
public class SearchDownloadsContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    boolean checkingFilesExist;
    private final int currentAccount;
    StickerEmptyView emptyView;
    private boolean hasCurrentDownload;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    String lastQueryString;
    Runnable lastSearchRunnable;
    private final FlickerLoadingView loadingView;
    Activity parentActivity;
    BaseFragment parentFragment;
    public RecyclerListView recyclerListView;
    int rowCount;
    String searchQuery;
    FilteredSearchView.UiCallback uiCallback;
    DownloadsAdapter adapter = new DownloadsAdapter();
    ArrayList<MessageObject> currentLoadingFiles = new ArrayList<>();
    ArrayList<MessageObject> recentLoadingFiles = new ArrayList<>();
    ArrayList<MessageObject> currentLoadingFilesTmp = new ArrayList<>();
    ArrayList<MessageObject> recentLoadingFilesTmp = new ArrayList<>();
    int downloadingFilesHeader = -1;
    int downloadingFilesStartRow = -1;
    int downloadingFilesEndRow = -1;
    int recentFilesHeader = -1;
    int recentFilesStartRow = -1;
    int recentFilesEndRow = -1;
    private final FilteredSearchView.MessageHashId messageHashIdTmp = new FilteredSearchView.MessageHashId(0, 0);

    public SearchDownloadsContainer(BaseFragment fragment, int currentAccount) {
        super(fragment.getParentActivity());
        this.parentFragment = fragment;
        this.parentActivity = fragment.getParentActivity();
        this.currentAccount = currentAccount;
        BlurredRecyclerView blurredRecyclerView = new BlurredRecyclerView(getContext());
        this.recyclerListView = blurredRecyclerView;
        addView(blurredRecyclerView);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(fragment.getParentActivity()) { // from class: org.telegram.ui.Components.SearchDownloadsContainer.1
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        this.recyclerListView.setAdapter(this.adapter);
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.SearchDownloadsContainer.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(SearchDownloadsContainer.this.parentActivity.getCurrentFocus());
                }
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.SearchDownloadsContainer$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                SearchDownloadsContainer.this.m2975lambda$new$0$orgtelegramuiComponentsSearchDownloadsContainer(view, i);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.SearchDownloadsContainer$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return SearchDownloadsContainer.this.m2976lambda$new$1$orgtelegramuiComponentsSearchDownloadsContainer(view, i);
            }
        });
        this.itemsEnterAnimator = new RecyclerItemsEnterAnimator(this.recyclerListView, true);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext());
        this.loadingView = flickerLoadingView;
        addView(flickerLoadingView);
        flickerLoadingView.setUseHeaderOffset(true);
        flickerLoadingView.setViewType(3);
        flickerLoadingView.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(getContext(), flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        addView(stickerEmptyView);
        this.recyclerListView.setEmptyView(this.emptyView);
        FileLoader.getInstance(currentAccount).getCurrentLoadingFiles(this.currentLoadingFiles);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SearchDownloadsContainer */
    public /* synthetic */ void m2975lambda$new$0$orgtelegramuiComponentsSearchDownloadsContainer(View view, int position) {
        MessageObject messageObject = this.adapter.getMessage(position);
        if (messageObject == null) {
            return;
        }
        if (this.uiCallback.actionModeShowing()) {
            this.uiCallback.toggleItemSelection(messageObject, view, 0);
            this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
            this.adapter.notifyItemChanged(position);
            return;
        }
        if (view instanceof Cell) {
            SharedDocumentCell cell = ((Cell) view).sharedDocumentCell;
            MessageObject message = cell.getMessage();
            TLRPC.Document document = message.getDocument();
            if (cell.isLoaded()) {
                if (message.isRoundVideo() || message.isVoice()) {
                    MediaController.getInstance().playMessage(message);
                    return;
                } else if (message.canPreviewDocument()) {
                    PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                    ArrayList<MessageObject> documents = new ArrayList<>();
                    documents.add(message);
                    PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                    PhotoViewer.getInstance().openPhoto(documents, 0, 0L, 0L, new PhotoViewer.EmptyPhotoViewerProvider());
                    return;
                } else {
                    AndroidUtilities.openDocument(message, this.parentActivity, this.parentFragment);
                }
            } else if (!cell.isLoading()) {
                messageObject.putInDownloadsStore = true;
                AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, messageObject, 0, 0);
                cell.updateFileExistIcon(true);
            } else {
                AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(document);
                cell.updateFileExistIcon(true);
            }
            update(true);
        }
        if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).didPressedButton();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-SearchDownloadsContainer */
    public /* synthetic */ boolean m2976lambda$new$1$orgtelegramuiComponentsSearchDownloadsContainer(View view, int position) {
        MessageObject messageObject = this.adapter.getMessage(position);
        if (messageObject == null) {
            return false;
        }
        if (!this.uiCallback.actionModeShowing()) {
            this.uiCallback.showActionMode();
        }
        if (this.uiCallback.actionModeShowing()) {
            this.uiCallback.toggleItemSelection(messageObject, view, 0);
            this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
            this.adapter.notifyItemChanged(position);
            return true;
        }
        return true;
    }

    private void checkFilesExist() {
        if (this.checkingFilesExist) {
            return;
        }
        this.checkingFilesExist = true;
        Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.SearchDownloadsContainer$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SearchDownloadsContainer.this.m2974x6715ce55();
            }
        });
    }

    /* renamed from: lambda$checkFilesExist$3$org-telegram-ui-Components-SearchDownloadsContainer */
    public /* synthetic */ void m2974x6715ce55() {
        ArrayList<MessageObject> currentLoadingFiles = new ArrayList<>();
        ArrayList<MessageObject> recentLoadingFiles = new ArrayList<>();
        final ArrayList<MessageObject> moveToRecent = new ArrayList<>();
        final ArrayList<MessageObject> removeFromRecent = new ArrayList<>();
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(currentLoadingFiles);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(recentLoadingFiles);
        for (int i = 0; i < currentLoadingFiles.size(); i++) {
            if (FileLoader.getInstance(this.currentAccount).getPathToMessage(currentLoadingFiles.get(i).messageOwner).exists()) {
                moveToRecent.add(currentLoadingFiles.get(i));
            }
        }
        for (int i2 = 0; i2 < recentLoadingFiles.size(); i2++) {
            if (!FileLoader.getInstance(this.currentAccount).getPathToMessage(recentLoadingFiles.get(i2).messageOwner).exists()) {
                removeFromRecent.add(recentLoadingFiles.get(i2));
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SearchDownloadsContainer$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                SearchDownloadsContainer.this.m2973xd9db1cd4(moveToRecent, removeFromRecent);
            }
        });
    }

    /* renamed from: lambda$checkFilesExist$2$org-telegram-ui-Components-SearchDownloadsContainer */
    public /* synthetic */ void m2973xd9db1cd4(ArrayList moveToRecent, ArrayList removeFromRecent) {
        for (int i = 0; i < moveToRecent.size(); i++) {
            DownloadController.getInstance(this.currentAccount).onDownloadComplete((MessageObject) moveToRecent.get(i));
        }
        if (!removeFromRecent.isEmpty()) {
            DownloadController.getInstance(this.currentAccount).deleteRecentFiles(removeFromRecent);
        }
        this.checkingFilesExist = false;
        update(true);
    }

    public void update(boolean animated) {
        if (TextUtils.isEmpty(this.searchQuery) || isEmptyDownloads()) {
            if (this.rowCount == 0) {
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            if (this.checkingFilesExist) {
                this.currentLoadingFilesTmp.clear();
                this.recentLoadingFilesTmp.clear();
            }
            FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(this.currentLoadingFilesTmp);
            FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(this.recentLoadingFilesTmp);
            for (int i = 0; i < this.currentLoadingFiles.size(); i++) {
                this.currentLoadingFiles.get(i).setQuery(null);
            }
            for (int i2 = 0; i2 < this.recentLoadingFiles.size(); i2++) {
                this.recentLoadingFiles.get(i2).setQuery(null);
            }
            this.lastQueryString = null;
            updateListInternal(animated, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
            if (this.rowCount == 0) {
                this.emptyView.showProgress(false, false);
                this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewDownloads", R.string.SearchEmptyViewDownloads));
                this.emptyView.subtitle.setVisibility(8);
            }
            this.emptyView.setStickerType(9);
            return;
        }
        this.emptyView.setStickerType(1);
        final ArrayList<MessageObject> currentLoadingFilesTmp = new ArrayList<>();
        final ArrayList<MessageObject> recentLoadingFilesTmp = new ArrayList<>();
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(currentLoadingFilesTmp);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(recentLoadingFilesTmp);
        final String q = this.searchQuery.toLowerCase();
        boolean sameQuery = q.equals(this.lastQueryString);
        this.lastQueryString = q;
        Utilities.searchQueue.cancelRunnable(this.lastSearchRunnable);
        DispatchQueue dispatchQueue = Utilities.searchQueue;
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.SearchDownloadsContainer$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                SearchDownloadsContainer.this.m2980x22f5f052(currentLoadingFilesTmp, q, recentLoadingFilesTmp);
            }
        };
        this.lastSearchRunnable = runnable;
        dispatchQueue.postRunnable(runnable, sameQuery ? 0L : 300L);
        this.recentLoadingFilesTmp.clear();
        this.currentLoadingFilesTmp.clear();
        if (!sameQuery) {
            this.emptyView.showProgress(true, true);
            updateListInternal(animated, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
        }
    }

    /* renamed from: lambda$update$5$org-telegram-ui-Components-SearchDownloadsContainer */
    public /* synthetic */ void m2980x22f5f052(ArrayList currentLoadingFilesTmp, final String q, ArrayList recentLoadingFilesTmp) {
        final ArrayList<MessageObject> currentLoadingFilesRes = new ArrayList<>();
        final ArrayList<MessageObject> recentLoadingFilesRes = new ArrayList<>();
        for (int i = 0; i < currentLoadingFilesTmp.size(); i++) {
            if (FileLoader.getDocumentFileName(((MessageObject) currentLoadingFilesTmp.get(i)).getDocument()).toLowerCase().contains(q)) {
                MessageObject messageObject = new MessageObject(this.currentAccount, ((MessageObject) currentLoadingFilesTmp.get(i)).messageOwner, false, false);
                messageObject.mediaExists = ((MessageObject) currentLoadingFilesTmp.get(i)).mediaExists;
                messageObject.setQuery(this.searchQuery);
                currentLoadingFilesRes.add(messageObject);
            }
        }
        for (int i2 = 0; i2 < recentLoadingFilesTmp.size(); i2++) {
            if (FileLoader.getDocumentFileName(((MessageObject) recentLoadingFilesTmp.get(i2)).getDocument()).toLowerCase().contains(q)) {
                MessageObject messageObject2 = new MessageObject(this.currentAccount, ((MessageObject) recentLoadingFilesTmp.get(i2)).messageOwner, false, false);
                messageObject2.mediaExists = ((MessageObject) recentLoadingFilesTmp.get(i2)).mediaExists;
                messageObject2.setQuery(this.searchQuery);
                recentLoadingFilesRes.add(messageObject2);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SearchDownloadsContainer$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SearchDownloadsContainer.this.m2979x95bb3ed1(q, currentLoadingFilesRes, recentLoadingFilesRes);
            }
        });
    }

    /* renamed from: lambda$update$4$org-telegram-ui-Components-SearchDownloadsContainer */
    public /* synthetic */ void m2979x95bb3ed1(String q, ArrayList currentLoadingFilesRes, ArrayList recentLoadingFilesRes) {
        if (q.equals(this.lastQueryString)) {
            if (this.rowCount == 0) {
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            updateListInternal(true, currentLoadingFilesRes, recentLoadingFilesRes);
            if (this.rowCount == 0) {
                this.emptyView.showProgress(false, true);
                this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", R.string.SearchEmptyViewTitle2));
                this.emptyView.subtitle.setVisibility(0);
                this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
            }
        }
    }

    private boolean isEmptyDownloads() {
        return DownloadController.getInstance(this.currentAccount).downloadingFiles.isEmpty() && DownloadController.getInstance(this.currentAccount).recentDownloadingFiles.isEmpty();
    }

    private void updateListInternal(boolean animated, ArrayList<MessageObject> currentLoadingFilesTmp, ArrayList<MessageObject> recentLoadingFilesTmp) {
        ArrayList<MessageObject> oldDownloadingLoadingFiles;
        if (animated) {
            final int oldDownloadingFilesHeader = this.downloadingFilesHeader;
            final int oldDownloadingFilesStartRow = this.downloadingFilesStartRow;
            final int oldDownloadingFilesEndRow = this.downloadingFilesEndRow;
            final int oldRecentFilesHeader = this.recentFilesHeader;
            final int oldRecentFilesStartRow = this.recentFilesStartRow;
            final int oldRecentFilesEndRow = this.recentFilesEndRow;
            final int oldRowCount = this.rowCount;
            final ArrayList<MessageObject> oldDownloadingLoadingFiles2 = new ArrayList<>(this.currentLoadingFiles);
            final ArrayList<MessageObject> oldRecentLoadingFiles = new ArrayList<>(this.recentLoadingFiles);
            updateRows(currentLoadingFilesTmp, recentLoadingFilesTmp);
            DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: org.telegram.ui.Components.SearchDownloadsContainer.3
                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getOldListSize() {
                    return oldRowCount;
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getNewListSize() {
                    return SearchDownloadsContainer.this.rowCount;
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    if (oldItemPosition >= 0 && newItemPosition >= 0) {
                        if (oldItemPosition == oldDownloadingFilesHeader && newItemPosition == SearchDownloadsContainer.this.downloadingFilesHeader) {
                            return true;
                        }
                        if (oldItemPosition == oldRecentFilesHeader && newItemPosition == SearchDownloadsContainer.this.recentFilesHeader) {
                            return true;
                        }
                    }
                    MessageObject oldItem = null;
                    MessageObject newItem = null;
                    int i = oldDownloadingFilesStartRow;
                    if (oldItemPosition >= i && oldItemPosition < oldDownloadingFilesEndRow) {
                        oldItem = (MessageObject) oldDownloadingLoadingFiles2.get(oldItemPosition - i);
                    } else {
                        int i2 = oldRecentFilesStartRow;
                        if (oldItemPosition >= i2 && oldItemPosition < oldRecentFilesEndRow) {
                            oldItem = (MessageObject) oldRecentLoadingFiles.get(oldItemPosition - i2);
                        }
                    }
                    if (newItemPosition >= SearchDownloadsContainer.this.downloadingFilesStartRow && newItemPosition < SearchDownloadsContainer.this.downloadingFilesEndRow) {
                        newItem = SearchDownloadsContainer.this.currentLoadingFiles.get(newItemPosition - SearchDownloadsContainer.this.downloadingFilesStartRow);
                    } else if (newItemPosition >= SearchDownloadsContainer.this.recentFilesStartRow && newItemPosition < SearchDownloadsContainer.this.recentFilesEndRow) {
                        newItem = SearchDownloadsContainer.this.recentLoadingFiles.get(newItemPosition - SearchDownloadsContainer.this.recentFilesStartRow);
                    }
                    return (newItem == null || oldItem == null || newItem.getDocument().id != oldItem.getDocument().id) ? false : true;
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return areItemsTheSame(oldItemPosition, newItemPosition);
                }
            }).dispatchUpdatesTo(this.adapter);
            int i = 0;
            while (i < this.recyclerListView.getChildCount()) {
                View child = this.recyclerListView.getChildAt(i);
                int p = this.recyclerListView.getChildAdapterPosition(child);
                if (p < 0) {
                    oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                } else {
                    RecyclerView.ViewHolder holder = this.recyclerListView.getChildViewHolder(child);
                    if (holder == null) {
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                    } else if (holder.shouldIgnore()) {
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                    } else if (child instanceof GraySectionCell) {
                        this.adapter.onBindViewHolder(holder, p);
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                    } else if (!(child instanceof Cell)) {
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                    } else {
                        Cell cell = (Cell) child;
                        cell.sharedDocumentCell.updateFileExistIcon(true);
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                        this.messageHashIdTmp.set(cell.sharedDocumentCell.getMessage().getId(), cell.sharedDocumentCell.getMessage().getDialogId());
                        cell.sharedDocumentCell.setChecked(this.uiCallback.isSelected(this.messageHashIdTmp), true);
                    }
                }
                i++;
                oldDownloadingLoadingFiles2 = oldDownloadingLoadingFiles;
            }
            return;
        }
        updateRows(currentLoadingFilesTmp, recentLoadingFilesTmp);
        this.adapter.notifyDataSetChanged();
    }

    private void updateRows(ArrayList<MessageObject> currentLoadingFilesTmp, ArrayList<MessageObject> recentLoadingFilesTmp) {
        this.currentLoadingFiles.clear();
        this.currentLoadingFiles.addAll(currentLoadingFilesTmp);
        this.recentLoadingFiles.clear();
        this.recentLoadingFiles.addAll(recentLoadingFilesTmp);
        this.rowCount = 0;
        this.downloadingFilesHeader = -1;
        this.downloadingFilesStartRow = -1;
        this.downloadingFilesEndRow = -1;
        this.recentFilesHeader = -1;
        this.recentFilesStartRow = -1;
        this.recentFilesEndRow = -1;
        this.hasCurrentDownload = false;
        if (!this.currentLoadingFiles.isEmpty()) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.downloadingFilesHeader = i;
            this.downloadingFilesStartRow = i2;
            int size = i2 + this.currentLoadingFiles.size();
            this.rowCount = size;
            this.downloadingFilesEndRow = size;
            int i3 = 0;
            while (true) {
                if (i3 >= this.currentLoadingFiles.size()) {
                    break;
                } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentLoadingFiles.get(i3).getFileName())) {
                    i3++;
                } else {
                    this.hasCurrentDownload = true;
                    break;
                }
            }
        }
        if (!this.recentLoadingFiles.isEmpty()) {
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.recentFilesHeader = i4;
            this.recentFilesStartRow = i5;
            int size2 = i5 + this.recentLoadingFiles.size();
            this.rowCount = size2;
            this.recentFilesEndRow = size2;
        }
    }

    public void search(String query) {
        this.searchQuery = query;
        update(false);
    }

    /* loaded from: classes5.dex */
    public class DownloadsAdapter extends RecyclerListView.SelectionAdapter {
        private DownloadsAdapter() {
            SearchDownloadsContainer.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new GraySectionCell(parent.getContext());
            } else if (viewType == 1) {
                view = new Cell(parent.getContext());
            } else {
                view = new SharedAudioCell(parent.getContext()) { // from class: org.telegram.ui.Components.SearchDownloadsContainer.DownloadsAdapter.1
                    @Override // org.telegram.ui.Cells.SharedAudioCell
                    public boolean needPlayMessage(MessageObject messageObject) {
                        return MediaController.getInstance().playMessage(messageObject);
                    }
                };
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = holder.getItemViewType();
            if (type == 0) {
                GraySectionCell graySectionCell = (GraySectionCell) holder.itemView;
                if (position == SearchDownloadsContainer.this.downloadingFilesHeader) {
                    String header = LocaleController.getString("Downloading", R.string.Downloading);
                    if (graySectionCell.getText().equals(header)) {
                        graySectionCell.setRightText(SearchDownloadsContainer.this.hasCurrentDownload ? LocaleController.getString("PauseAll", R.string.PauseAll) : LocaleController.getString("ResumeAll", R.string.ResumeAll), SearchDownloadsContainer.this.hasCurrentDownload);
                        return;
                    } else {
                        graySectionCell.setText(header, SearchDownloadsContainer.this.hasCurrentDownload ? LocaleController.getString("PauseAll", R.string.PauseAll) : LocaleController.getString("ResumeAll", R.string.ResumeAll), new View.OnClickListener() { // from class: org.telegram.ui.Components.SearchDownloadsContainer.DownloadsAdapter.2
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view) {
                                for (int i = 0; i < SearchDownloadsContainer.this.currentLoadingFiles.size(); i++) {
                                    MessageObject messageObject = SearchDownloadsContainer.this.currentLoadingFiles.get(i);
                                    if (SearchDownloadsContainer.this.hasCurrentDownload) {
                                        AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(messageObject.getDocument());
                                    } else {
                                        AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(messageObject.getDocument(), messageObject, 0, 0);
                                    }
                                }
                                SearchDownloadsContainer.this.update(true);
                            }
                        });
                        return;
                    }
                } else if (position == SearchDownloadsContainer.this.recentFilesHeader) {
                    graySectionCell.setText(LocaleController.getString("RecentlyDownloaded", R.string.RecentlyDownloaded), LocaleController.getString("Settings", R.string.Settings), new View.OnClickListener() { // from class: org.telegram.ui.Components.SearchDownloadsContainer.DownloadsAdapter.3
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            SearchDownloadsContainer.this.showSettingsDialog();
                        }
                    });
                    return;
                } else {
                    return;
                }
            }
            MessageObject messageObject = getMessage(position);
            if (messageObject != null) {
                boolean z = false;
                if (type == 1) {
                    Cell view = (Cell) holder.itemView;
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    int oldId = view.sharedDocumentCell.getMessage() == null ? 0 : view.sharedDocumentCell.getMessage().getId();
                    view.sharedDocumentCell.setDocument(messageObject, true);
                    SearchDownloadsContainer.this.messageHashIdTmp.set(view.sharedDocumentCell.getMessage().getId(), view.sharedDocumentCell.getMessage().getDialogId());
                    SharedDocumentCell sharedDocumentCell = view.sharedDocumentCell;
                    boolean isSelected = SearchDownloadsContainer.this.uiCallback.isSelected(SearchDownloadsContainer.this.messageHashIdTmp);
                    if (oldId == messageObject.getId()) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(isSelected, z);
                } else if (type == 2) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                    sharedAudioCell.setMessageObject(messageObject, true);
                    int oldId2 = sharedAudioCell.getMessage() == null ? 0 : sharedAudioCell.getMessage().getId();
                    boolean isSelected2 = SearchDownloadsContainer.this.uiCallback.isSelected(SearchDownloadsContainer.this.messageHashIdTmp);
                    if (oldId2 == messageObject.getId()) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(isSelected2, z);
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == SearchDownloadsContainer.this.downloadingFilesHeader || position == SearchDownloadsContainer.this.recentFilesHeader) {
                return 0;
            }
            MessageObject messageObject = getMessage(position);
            if (messageObject == null || !messageObject.isMusic()) {
                return 1;
            }
            return 2;
        }

        public MessageObject getMessage(int position) {
            if (position >= SearchDownloadsContainer.this.downloadingFilesStartRow && position < SearchDownloadsContainer.this.downloadingFilesEndRow) {
                return SearchDownloadsContainer.this.currentLoadingFiles.get(position - SearchDownloadsContainer.this.downloadingFilesStartRow);
            }
            if (position >= SearchDownloadsContainer.this.recentFilesStartRow && position < SearchDownloadsContainer.this.recentFilesEndRow) {
                return SearchDownloadsContainer.this.recentLoadingFiles.get(position - SearchDownloadsContainer.this.recentFilesStartRow);
            }
            return null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return SearchDownloadsContainer.this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1 || holder.getItemViewType() == 2;
        }
    }

    public void showSettingsDialog() {
        if (this.parentFragment == null || this.parentActivity == null) {
            return;
        }
        final BottomSheet bottomSheet = new BottomSheet(this.parentActivity, false);
        Context context = this.parentFragment.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        StickerImageView imageView = new StickerImageView(context, this.currentAccount);
        imageView.setStickerNum(9);
        imageView.getImageReceiver().setAutoRepeat(1);
        linearLayout.addView(imageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
        TextView title = new TextView(context);
        title.setGravity(1);
        title.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        title.setTextSize(1, 24.0f);
        title.setText(LocaleController.getString("DownloadedFiles", R.string.DownloadedFiles));
        linearLayout.addView(title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
        TextView description = new TextView(context);
        description.setGravity(1);
        description.setTextSize(1, 15.0f);
        description.setTextColor(Theme.getColor(Theme.key_dialogTextHint));
        description.setText(LocaleController.formatString("DownloadedFilesMessage", R.string.DownloadedFilesMessage, new Object[0]));
        linearLayout.addView(description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
        TextView buttonTextView = new TextView(context);
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView.setText(LocaleController.getString("ManageDeviceStorage", R.string.ManageDeviceStorage));
        buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 120)));
        linearLayout.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
        TextView buttonTextView2 = new TextView(context);
        buttonTextView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView2.setGravity(17);
        buttonTextView2.setTextSize(1, 14.0f);
        buttonTextView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonTextView2.setText(LocaleController.getString("ClearDownloadsList", R.string.ClearDownloadsList));
        buttonTextView2.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        buttonTextView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_featuredStickers_addButton), 120)));
        linearLayout.addView(buttonTextView2, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 0.0f, 16.0f, 16.0f));
        NestedScrollView scrollView = new NestedScrollView(context);
        scrollView.addView(linearLayout);
        bottomSheet.setCustomView(scrollView);
        bottomSheet.show();
        if (Build.VERSION.SDK_INT >= 23) {
            AndroidUtilities.setLightStatusBar(bottomSheet.getWindow(), !Theme.isCurrentThemeDark());
            AndroidUtilities.setLightNavigationBar(bottomSheet.getWindow(), !Theme.isCurrentThemeDark());
        }
        buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SearchDownloadsContainer$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SearchDownloadsContainer.this.m2977x8655ff2(bottomSheet, view);
            }
        });
        buttonTextView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SearchDownloadsContainer$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SearchDownloadsContainer.this.m2978x95a01173(bottomSheet, view);
            }
        });
    }

    /* renamed from: lambda$showSettingsDialog$6$org-telegram-ui-Components-SearchDownloadsContainer */
    public /* synthetic */ void m2977x8655ff2(BottomSheet bottomSheet, View view) {
        bottomSheet.dismiss();
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            baseFragment.presentFragment(new CacheControlActivity());
        }
    }

    /* renamed from: lambda$showSettingsDialog$7$org-telegram-ui-Components-SearchDownloadsContainer */
    public /* synthetic */ void m2978x95a01173(BottomSheet bottomSheet, View view) {
        bottomSheet.dismiss();
        DownloadController.getInstance(this.currentAccount).clearRecentDownloadedFiles();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onDownloadingFilesChanged);
        if (getVisibility() == 0) {
            DownloadController.getInstance(this.currentAccount).clearUnviewedDownloads();
        }
        checkFilesExist();
        update(false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.onDownloadingFilesChanged);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.onDownloadingFilesChanged) {
            if (getVisibility() == 0) {
                DownloadController.getInstance(this.currentAccount).clearUnviewedDownloads();
            }
            update(true);
        }
    }

    /* loaded from: classes5.dex */
    public class Cell extends FrameLayout {
        SharedDocumentCell sharedDocumentCell;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public Cell(Context context) {
            super(context);
            SearchDownloadsContainer.this = r2;
            SharedDocumentCell sharedDocumentCell = new SharedDocumentCell(context, 2);
            this.sharedDocumentCell = sharedDocumentCell;
            sharedDocumentCell.rightDateTextView.setVisibility(8);
            addView(this.sharedDocumentCell);
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            this.sharedDocumentCell.onInitializeAccessibilityNodeInfo(info);
        }
    }

    public void setUiCallback(FilteredSearchView.UiCallback callback) {
        this.uiCallback = callback;
    }

    public void setKeyboardHeight(int keyboardSize, boolean animated) {
        this.emptyView.setKeyboardHeight(keyboardSize, animated);
    }
}
