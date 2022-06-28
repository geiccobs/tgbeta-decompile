package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.net.MailTo;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes4.dex */
public class FilteredSearchView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static SpannableStringBuilder arrowSpan;
    RecyclerView.Adapter adapter;
    private SearchViewPager.ChatPreviewDelegate chatPreviewDelegate;
    private String currentDataQuery;
    boolean currentIncludeFolder;
    long currentSearchDialogId;
    FiltersView.MediaFilterData currentSearchFilter;
    long currentSearchMaxDate;
    long currentSearchMinDate;
    String currentSearchString;
    private Delegate delegate;
    StickerEmptyView emptyView;
    private boolean endReached;
    private AnimatorSet floatingDateAnimation;
    private final ChatActionCell floatingDateView;
    boolean ignoreRequestLayout;
    private boolean isLoading;
    public int keyboardHeight;
    int lastAccount;
    String lastMessagesSearchString;
    String lastSearchFilterQueryString;
    public final LinearLayoutManager layoutManager;
    private final FlickerLoadingView loadingView;
    boolean localTipArchive;
    private int nextSearchRate;
    Activity parentActivity;
    BaseFragment parentFragment;
    private int photoViewerClassGuid;
    public RecyclerListView recyclerListView;
    private int requestIndex;
    private int searchIndex;
    Runnable searchRunnable;
    private int totalCount;
    private UiCallback uiCallback;
    public ArrayList<MessageObject> messages = new ArrayList<>();
    public SparseArray<MessageObject> messagesById = new SparseArray<>();
    public ArrayList<String> sections = new ArrayList<>();
    public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
    private int columnsCount = 3;
    private final MessageHashId messageHashIdTmp = new MessageHashId(0, 0);
    ArrayList<Object> localTipChats = new ArrayList<>();
    ArrayList<FiltersView.DateData> localTipDates = new ArrayList<>();
    Runnable clearCurrentResultsRunnable = new Runnable() { // from class: org.telegram.ui.FilteredSearchView.1
        @Override // java.lang.Runnable
        public void run() {
            if (FilteredSearchView.this.isLoading) {
                FilteredSearchView.this.messages.clear();
                FilteredSearchView.this.sections.clear();
                FilteredSearchView.this.sectionArrays.clear();
                if (FilteredSearchView.this.adapter != null) {
                    FilteredSearchView.this.adapter.notifyDataSetChanged();
                }
            }
        }
    };
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.FilteredSearchView.2
        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getTotalImageCount() {
            return FilteredSearchView.this.totalCount;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean loadMore() {
            if (!FilteredSearchView.this.endReached) {
                FilteredSearchView filteredSearchView = FilteredSearchView.this;
                filteredSearchView.search(filteredSearchView.currentSearchDialogId, FilteredSearchView.this.currentSearchMinDate, FilteredSearchView.this.currentSearchMaxDate, FilteredSearchView.this.currentSearchFilter, FilteredSearchView.this.currentIncludeFolder, FilteredSearchView.this.lastMessagesSearchString, false);
                return true;
            }
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            View pinnedHeader;
            MessageObject message;
            if (messageObject == null) {
                return null;
            }
            RecyclerListView listView = FilteredSearchView.this.recyclerListView;
            int count = listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = listView.getChildAt(a);
                int[] coords = new int[2];
                ImageReceiver imageReceiver = null;
                if (view instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell cell = (SharedPhotoVideoCell) view;
                    for (int i = 0; i < 6 && (message = cell.getMessageObject(i)) != null; i++) {
                        if (message.getId() == messageObject.getId()) {
                            BackupImageView imageView = cell.getImageView(i);
                            imageReceiver = imageView.getImageReceiver();
                            imageView.getLocationInWindow(coords);
                        }
                    }
                } else if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell cell2 = (SharedDocumentCell) view;
                    if (cell2.getMessage().getId() == messageObject.getId()) {
                        BackupImageView imageView2 = cell2.getImageView();
                        imageReceiver = imageView2.getImageReceiver();
                        imageView2.getLocationInWindow(coords);
                    }
                } else if (view instanceof ContextLinkCell) {
                    ContextLinkCell cell3 = (ContextLinkCell) view;
                    MessageObject message2 = (MessageObject) cell3.getParentObject();
                    if (message2 != null && message2.getId() == messageObject.getId()) {
                        imageReceiver = cell3.getPhotoImage();
                        cell3.getLocationInWindow(coords);
                    }
                }
                if (imageReceiver != null) {
                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                    object.viewX = coords[0];
                    object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                    object.parentView = listView;
                    listView.getLocationInWindow(coords);
                    object.animatingImageViewYOffset = -coords[1];
                    object.imageReceiver = imageReceiver;
                    object.allowTakeAnimation = false;
                    object.radius = object.imageReceiver.getRoundRadius();
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    object.parentView.getLocationInWindow(coords);
                    object.clipTopAddition = 0;
                    if (PhotoViewer.isShowingImage(messageObject) && (pinnedHeader = listView.getPinnedHeader()) != null) {
                        int top = 0;
                        if (view instanceof SharedDocumentCell) {
                            top = 0 + AndroidUtilities.dp(8.0f);
                        }
                        int topOffset = top - object.viewY;
                        if (topOffset > view.getHeight()) {
                            listView.scrollBy(0, -(pinnedHeader.getHeight() + topOffset));
                        } else {
                            int bottomOffset = object.viewY - listView.getHeight();
                            if (view instanceof SharedDocumentCell) {
                                bottomOffset -= AndroidUtilities.dp(8.0f);
                            }
                            if (bottomOffset >= 0) {
                                listView.scrollBy(0, view.getHeight() + bottomOffset);
                            }
                        }
                    }
                    return object;
                }
            }
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public CharSequence getTitleFor(int i) {
            return FilteredSearchView.createFromInfoString(FilteredSearchView.this.messages.get(i));
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public CharSequence getSubtitleFor(int i) {
            return LocaleController.formatDateAudio(FilteredSearchView.this.messages.get(i).messageOwner.date, false);
        }
    };
    private boolean firstLoading = true;
    private int animationIndex = -1;
    private Runnable hideFloatingDateRunnable = new Runnable() { // from class: org.telegram.ui.FilteredSearchView$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            FilteredSearchView.this.m3444lambda$new$0$orgtelegramuiFilteredSearchView();
        }
    };
    private OnlyUserFiltersAdapter dialogsAdapter = new OnlyUserFiltersAdapter();
    private SharedPhotoVideoAdapter sharedPhotoVideoAdapter = new SharedPhotoVideoAdapter(getContext());
    private SharedDocumentsAdapter sharedDocumentsAdapter = new SharedDocumentsAdapter(getContext(), 1);
    private SharedLinksAdapter sharedLinksAdapter = new SharedLinksAdapter(getContext());
    private SharedDocumentsAdapter sharedAudioAdapter = new SharedDocumentsAdapter(getContext(), 4);
    private SharedDocumentsAdapter sharedVoiceAdapter = new SharedDocumentsAdapter(getContext(), 2);

    /* loaded from: classes4.dex */
    public interface Delegate {
        void updateFiltersView(boolean z, ArrayList<Object> arrayList, ArrayList<FiltersView.DateData> arrayList2, boolean z2);
    }

    /* loaded from: classes4.dex */
    public interface UiCallback {
        boolean actionModeShowing();

        int getFolderId();

        void goToMessage(MessageObject messageObject);

        boolean isSelected(MessageHashId messageHashId);

        void showActionMode();

        void toggleItemSelection(MessageObject messageObject, View view, int i);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-FilteredSearchView */
    public /* synthetic */ void m3444lambda$new$0$orgtelegramuiFilteredSearchView() {
        hideFloatingDateView(true);
    }

    public FilteredSearchView(BaseFragment fragment) {
        super(fragment.getParentActivity());
        this.parentFragment = fragment;
        Activity parentActivity = fragment.getParentActivity();
        this.parentActivity = parentActivity;
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        BlurredRecyclerView blurredRecyclerView = new BlurredRecyclerView(parentActivity) { // from class: org.telegram.ui.FilteredSearchView.3
            @Override // org.telegram.ui.Components.BlurredRecyclerView, org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                if (getAdapter() == FilteredSearchView.this.sharedPhotoVideoAdapter) {
                    for (int i = 0; i < getChildCount(); i++) {
                        if (getChildViewHolder(getChildAt(i)).getItemViewType() == 1) {
                            canvas.save();
                            canvas.translate(getChildAt(i).getX(), (getChildAt(i).getY() - getChildAt(i).getMeasuredHeight()) + AndroidUtilities.dp(2.0f));
                            getChildAt(i).draw(canvas);
                            canvas.restore();
                            invalidate();
                        }
                    }
                }
                super.dispatchDraw(canvas);
            }

            @Override // org.telegram.ui.Components.BlurredRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (getAdapter() == FilteredSearchView.this.sharedPhotoVideoAdapter && getChildViewHolder(child).getItemViewType() == 1) {
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.recyclerListView = blurredRecyclerView;
        blurredRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.FilteredSearchView$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                FilteredSearchView.this.m3445lambda$new$1$orgtelegramuiFilteredSearchView(view, i);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() { // from class: org.telegram.ui.FilteredSearchView.4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public boolean onItemClick(View view, int position, float x, float y) {
                if (view instanceof SharedDocumentCell) {
                    FilteredSearchView.this.onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
                } else if (view instanceof SharedLinkCell) {
                    FilteredSearchView.this.onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
                } else if (view instanceof SharedAudioCell) {
                    FilteredSearchView.this.onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0);
                } else if (view instanceof ContextLinkCell) {
                    FilteredSearchView.this.onItemLongClick(((ContextLinkCell) view).getMessageObject(), view, 0);
                } else if (view instanceof DialogCell) {
                    if (FilteredSearchView.this.uiCallback.actionModeShowing() || !((DialogCell) view).isPointInsideAvatar(x, y)) {
                        FilteredSearchView.this.onItemLongClick(((DialogCell) view).getMessage(), view, 0);
                    } else {
                        FilteredSearchView.this.chatPreviewDelegate.startChatPreview(FilteredSearchView.this.recyclerListView, (DialogCell) view);
                        return true;
                    }
                }
                return true;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public void onMove(float dx, float dy) {
                FilteredSearchView.this.chatPreviewDelegate.move(dy);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public void onLongClickRelease() {
                FilteredSearchView.this.chatPreviewDelegate.finish();
            }
        });
        this.recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        this.layoutManager = linearLayoutManager;
        this.recyclerListView.setLayoutManager(linearLayoutManager);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(parentActivity) { // from class: org.telegram.ui.FilteredSearchView.5
            @Override // org.telegram.ui.Components.FlickerLoadingView
            public int getColumnsCount() {
                return FilteredSearchView.this.columnsCount;
            }
        };
        this.loadingView = flickerLoadingView;
        addView(flickerLoadingView);
        addView(this.recyclerListView);
        this.recyclerListView.setSectionsType(2);
        this.recyclerListView.setOnScrollListener(new AnonymousClass6());
        ChatActionCell chatActionCell = new ChatActionCell(parentActivity);
        this.floatingDateView = chatActionCell;
        chatActionCell.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
        chatActionCell.setAlpha(0.0f);
        chatActionCell.setOverrideColor(Theme.key_chat_mediaTimeBackground, Theme.key_chat_mediaTimeText);
        chatActionCell.setTranslationY(-AndroidUtilities.dp(48.0f));
        addView(chatActionCell, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        StickerEmptyView stickerEmptyView = new StickerEmptyView(parentActivity, flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        addView(stickerEmptyView);
        this.recyclerListView.setEmptyView(this.emptyView);
        this.emptyView.setVisibility(8);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-FilteredSearchView */
    public /* synthetic */ void m3445lambda$new$1$orgtelegramuiFilteredSearchView(View view, int position) {
        if (view instanceof SharedDocumentCell) {
            onItemClick(position, view, ((SharedDocumentCell) view).getMessage(), 0);
        } else if (view instanceof SharedLinkCell) {
            onItemClick(position, view, ((SharedLinkCell) view).getMessage(), 0);
        } else if (view instanceof SharedAudioCell) {
            onItemClick(position, view, ((SharedAudioCell) view).getMessage(), 0);
        } else if (view instanceof ContextLinkCell) {
            onItemClick(position, view, ((ContextLinkCell) view).getMessageObject(), 0);
        } else if (view instanceof DialogCell) {
            onItemClick(position, view, ((DialogCell) view).getMessage(), 0);
        }
    }

    /* renamed from: org.telegram.ui.FilteredSearchView$6 */
    /* loaded from: classes4.dex */
    public class AnonymousClass6 extends RecyclerView.OnScrollListener {
        AnonymousClass6() {
            FilteredSearchView.this = this$0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1) {
                AndroidUtilities.hideKeyboard(FilteredSearchView.this.parentActivity.getCurrentFocus());
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView.getAdapter() == null || FilteredSearchView.this.adapter == null) {
                return;
            }
            int firstVisibleItem = FilteredSearchView.this.layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItem = FilteredSearchView.this.layoutManager.findLastVisibleItemPosition();
            int visibleItemCount = Math.abs(lastVisibleItem - firstVisibleItem) + 1;
            int totalItemCount = recyclerView.getAdapter().getItemCount();
            if (!FilteredSearchView.this.isLoading && visibleItemCount > 0 && lastVisibleItem >= totalItemCount - 10 && !FilteredSearchView.this.endReached) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilteredSearchView$6$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        FilteredSearchView.AnonymousClass6.this.m3449lambda$onScrolled$0$orgtelegramuiFilteredSearchView$6();
                    }
                });
            }
            if (FilteredSearchView.this.adapter == FilteredSearchView.this.sharedPhotoVideoAdapter) {
                if (dy != 0 && !FilteredSearchView.this.messages.isEmpty() && TextUtils.isEmpty(FilteredSearchView.this.currentDataQuery)) {
                    FilteredSearchView.this.showFloatingDateView();
                }
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                if (holder != null && holder.getItemViewType() == 0 && (holder.itemView instanceof SharedPhotoVideoCell)) {
                    SharedPhotoVideoCell cell = (SharedPhotoVideoCell) holder.itemView;
                    MessageObject messageObject = cell.getMessageObject(0);
                    if (messageObject != null) {
                        FilteredSearchView.this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
                    }
                }
            }
        }

        /* renamed from: lambda$onScrolled$0$org-telegram-ui-FilteredSearchView$6 */
        public /* synthetic */ void m3449lambda$onScrolled$0$orgtelegramuiFilteredSearchView$6() {
            FilteredSearchView filteredSearchView = FilteredSearchView.this;
            filteredSearchView.search(filteredSearchView.currentSearchDialogId, FilteredSearchView.this.currentSearchMinDate, FilteredSearchView.this.currentSearchMaxDate, FilteredSearchView.this.currentSearchFilter, FilteredSearchView.this.currentIncludeFolder, FilteredSearchView.this.lastMessagesSearchString, false);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static CharSequence createFromInfoString(MessageObject messageObject) {
        if (arrowSpan == null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("-");
            arrowSpan = spannableStringBuilder;
            spannableStringBuilder.setSpan(new ColoredImageSpan(ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.search_arrow).mutate()), 0, 1, 0);
        }
        CharSequence fromName = null;
        TLRPC.Chat chat = null;
        TLRPC.User user = messageObject.messageOwner.from_id.user_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(messageObject.messageOwner.from_id.user_id)) : null;
        TLRPC.Chat chatFrom = messageObject.messageOwner.from_id.chat_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(messageObject.messageOwner.peer_id.chat_id)) : null;
        if (chatFrom == null) {
            chatFrom = messageObject.messageOwner.from_id.channel_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(messageObject.messageOwner.peer_id.channel_id)) : null;
        }
        TLRPC.Chat chatTo = messageObject.messageOwner.peer_id.channel_id != 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(messageObject.messageOwner.peer_id.channel_id)) : null;
        if (chatTo == null) {
            if (messageObject.messageOwner.peer_id.chat_id != 0) {
                chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(messageObject.messageOwner.peer_id.chat_id));
            }
            chatTo = chat;
        }
        if (user != null && chatTo != null) {
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
            spannableStringBuilder2.append((CharSequence) ContactsController.formatName(user.first_name, user.last_name)).append(' ').append((CharSequence) arrowSpan).append(' ').append((CharSequence) chatTo.title);
            fromName = spannableStringBuilder2;
        } else if (user != null) {
            fromName = ContactsController.formatName(user.first_name, user.last_name);
        } else if (chatFrom != null) {
            fromName = chatFrom.title;
        }
        return fromName == null ? "" : fromName;
    }

    public void search(final long dialogId, final long minDate, final long maxDate, final FiltersView.MediaFilterData currentSearchFilter, final boolean includeFolder, final String query, boolean clearOldResults) {
        Locale locale = Locale.ENGLISH;
        Object[] objArr = new Object[6];
        objArr[0] = Long.valueOf(dialogId);
        objArr[1] = Long.valueOf(minDate);
        objArr[2] = Long.valueOf(maxDate);
        objArr[3] = Integer.valueOf(currentSearchFilter == null ? -1 : currentSearchFilter.filterType);
        objArr[4] = query;
        objArr[5] = Boolean.valueOf(includeFolder);
        final String currentSearchFilterQueryString = String.format(locale, "%d%d%d%d%s%s", objArr);
        String str = this.lastSearchFilterQueryString;
        final boolean filterAndQueryIsSame = str != null && str.equals(currentSearchFilterQueryString);
        boolean forceClear = !filterAndQueryIsSame && clearOldResults;
        this.currentSearchFilter = currentSearchFilter;
        this.currentSearchDialogId = dialogId;
        this.currentSearchMinDate = minDate;
        this.currentSearchMaxDate = maxDate;
        this.currentSearchString = query;
        this.currentIncludeFolder = includeFolder;
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        AndroidUtilities.cancelRunOnUIThread(this.clearCurrentResultsRunnable);
        if (filterAndQueryIsSame && clearOldResults) {
            return;
        }
        long j = 0;
        if (forceClear || (currentSearchFilter == null && dialogId == 0 && minDate == 0 && maxDate == 0)) {
            this.messages.clear();
            this.sections.clear();
            this.sectionArrays.clear();
            this.isLoading = true;
            this.emptyView.setVisibility(0);
            RecyclerView.Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            this.requestIndex++;
            this.firstLoading = true;
            if (this.recyclerListView.getPinnedHeader() != null) {
                this.recyclerListView.getPinnedHeader().setAlpha(0.0f);
            }
            this.localTipChats.clear();
            this.localTipDates.clear();
            if (!forceClear) {
                return;
            }
        } else if (clearOldResults && !this.messages.isEmpty()) {
            return;
        }
        this.isLoading = true;
        RecyclerView.Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
        if (!filterAndQueryIsSame) {
            this.clearCurrentResultsRunnable.run();
            this.emptyView.showProgress(true, !clearOldResults);
        }
        if (TextUtils.isEmpty(query)) {
            this.localTipDates.clear();
            this.localTipChats.clear();
            Delegate delegate = this.delegate;
            if (delegate != null) {
                delegate.updateFiltersView(false, null, null, false);
            }
        }
        this.requestIndex++;
        final int requestId = this.requestIndex;
        final int currentAccount = UserConfig.selectedAccount;
        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.FilteredSearchView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FilteredSearchView.this.m3448lambda$search$4$orgtelegramuiFilteredSearchView(dialogId, query, currentSearchFilter, currentAccount, minDate, maxDate, filterAndQueryIsSame, includeFolder, currentSearchFilterQueryString, requestId);
            }
        };
        this.searchRunnable = runnable2;
        if (!filterAndQueryIsSame || this.messages.isEmpty()) {
            j = 350;
        }
        AndroidUtilities.runOnUIThread(runnable2, j);
        if (currentSearchFilter == null) {
            this.loadingView.setViewType(1);
        } else if (currentSearchFilter.filterType != 0) {
            if (currentSearchFilter.filterType == 1) {
                this.loadingView.setViewType(3);
            } else if (currentSearchFilter.filterType == 3 || currentSearchFilter.filterType == 5) {
                this.loadingView.setViewType(4);
            } else if (currentSearchFilter.filterType == 2) {
                this.loadingView.setViewType(5);
            }
        } else if (!TextUtils.isEmpty(this.currentSearchString)) {
            this.loadingView.setViewType(1);
        } else {
            this.loadingView.setViewType(2);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$search$4$org-telegram-ui-FilteredSearchView */
    public /* synthetic */ void m3448lambda$search$4$orgtelegramuiFilteredSearchView(final long dialogId, final String query, final FiltersView.MediaFilterData currentSearchFilter, final int currentAccount, final long minDate, long maxDate, final boolean filterAndQueryIsSame, boolean includeFolder, String currentSearchFilterQueryString, final int requestId) {
        ArrayList<Object> resultArray;
        TLObject request;
        ArrayList<MessageObject> arrayList;
        ArrayList<MessageObject> arrayList2;
        ArrayList<Object> resultArray2 = null;
        if (dialogId != 0) {
            TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.q = query;
            req.limit = 20;
            req.filter = currentSearchFilter == null ? new TLRPC.TL_inputMessagesFilterEmpty() : currentSearchFilter.filter;
            req.peer = AccountInstance.getInstance(currentAccount).getMessagesController().getInputPeer(dialogId);
            if (minDate > 0) {
                req.min_date = (int) (minDate / 1000);
            }
            if (maxDate > 0) {
                req.max_date = (int) (maxDate / 1000);
            }
            if (filterAndQueryIsSame && query.equals(this.lastMessagesSearchString) && !this.messages.isEmpty()) {
                req.offset_id = this.messages.get(arrayList2.size() - 1).getId();
            } else {
                req.offset_id = 0;
            }
            resultArray = null;
            request = req;
        } else {
            if (!TextUtils.isEmpty(query)) {
                ArrayList<Object> resultArray3 = new ArrayList<>();
                ArrayList<CharSequence> resultArrayNames = new ArrayList<>();
                ArrayList<TLRPC.User> encUsers = new ArrayList<>();
                MessagesStorage.getInstance(currentAccount).localSearch(0, query, resultArray3, resultArrayNames, encUsers, includeFolder ? 1 : 0);
                resultArray2 = resultArray3;
            }
            TLRPC.TL_messages_searchGlobal req2 = new TLRPC.TL_messages_searchGlobal();
            req2.limit = 20;
            req2.q = query;
            req2.filter = currentSearchFilter == null ? new TLRPC.TL_inputMessagesFilterEmpty() : currentSearchFilter.filter;
            if (minDate > 0) {
                req2.min_date = (int) (minDate / 1000);
            }
            if (maxDate > 0) {
                req2.max_date = (int) (maxDate / 1000);
            }
            if (filterAndQueryIsSame && query.equals(this.lastMessagesSearchString) && !this.messages.isEmpty()) {
                MessageObject lastMessage = this.messages.get(arrayList.size() - 1);
                req2.offset_id = lastMessage.getId();
                req2.offset_rate = this.nextSearchRate;
                long id = MessageObject.getPeerId(lastMessage.messageOwner.peer_id);
                req2.offset_peer = MessagesController.getInstance(currentAccount).getInputPeer(id);
            } else {
                req2.offset_rate = 0;
                req2.offset_id = 0;
                req2.offset_peer = new TLRPC.TL_inputPeerEmpty();
            }
            req2.flags |= 1;
            req2.folder_id = includeFolder;
            resultArray = resultArray2;
            request = req2;
        }
        this.lastMessagesSearchString = query;
        this.lastSearchFilterQueryString = currentSearchFilterQueryString;
        final ArrayList<Object> finalResultArray = resultArray;
        final ArrayList<FiltersView.DateData> dateData = new ArrayList<>();
        FiltersView.fillTipDates(this.lastMessagesSearchString, dateData);
        ConnectionsManager.getInstance(currentAccount).sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.FilteredSearchView$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FilteredSearchView.this.m3447lambda$search$3$orgtelegramuiFilteredSearchView(currentAccount, query, requestId, filterAndQueryIsSame, currentSearchFilter, dialogId, minDate, finalResultArray, dateData, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$search$3$org-telegram-ui-FilteredSearchView */
    public /* synthetic */ void m3447lambda$search$3$orgtelegramuiFilteredSearchView(final int currentAccount, final String query, final int requestId, final boolean filterAndQueryIsSame, final FiltersView.MediaFilterData currentSearchFilter, final long dialogId, final long minDate, final ArrayList finalResultArray, final ArrayList dateData, final TLObject response, final TLRPC.TL_error error) {
        final ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            int n = res.messages.size();
            for (int i = 0; i < n; i++) {
                MessageObject messageObject = new MessageObject(currentAccount, res.messages.get(i), false, true);
                messageObject.setQuery(query);
                messageObjects.add(messageObject);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilteredSearchView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FilteredSearchView.this.m3446lambda$search$2$orgtelegramuiFilteredSearchView(requestId, error, response, currentAccount, filterAndQueryIsSame, query, messageObjects, currentSearchFilter, dialogId, minDate, finalResultArray, dateData);
            }
        });
    }

    /* renamed from: lambda$search$2$org-telegram-ui-FilteredSearchView */
    public /* synthetic */ void m3446lambda$search$2$orgtelegramuiFilteredSearchView(int requestId, TLRPC.TL_error error, TLObject response, final int currentAccount, boolean filterAndQueryIsSame, String query, ArrayList messageObjects, FiltersView.MediaFilterData currentSearchFilter, long dialogId, long minDate, ArrayList finalResultArray, ArrayList dateData) {
        TLRPC.messages_Messages res;
        String str;
        if (requestId == this.requestIndex) {
            this.isLoading = false;
            if (error == null) {
                this.emptyView.showProgress(false);
                TLRPC.messages_Messages res2 = (TLRPC.messages_Messages) response;
                this.nextSearchRate = res2.next_rate;
                MessagesStorage.getInstance(currentAccount).putUsersAndChats(res2.users, res2.chats, true, true);
                MessagesController.getInstance(currentAccount).putUsers(res2.users, false);
                MessagesController.getInstance(currentAccount).putChats(res2.chats, false);
                if (!filterAndQueryIsSame) {
                    this.messages.clear();
                    this.messagesById.clear();
                    this.sections.clear();
                    this.sectionArrays.clear();
                }
                this.totalCount = res2.count;
                this.currentDataQuery = query;
                int n = messageObjects.size();
                for (int i = 0; i < n; i++) {
                    MessageObject messageObject = (MessageObject) messageObjects.get(i);
                    ArrayList<MessageObject> messageObjectsByDate = this.sectionArrays.get(messageObject.monthKey);
                    if (messageObjectsByDate == null) {
                        messageObjectsByDate = new ArrayList<>();
                        this.sectionArrays.put(messageObject.monthKey, messageObjectsByDate);
                        this.sections.add(messageObject.monthKey);
                    }
                    messageObjectsByDate.add(messageObject);
                    this.messages.add(messageObject);
                    this.messagesById.put(messageObject.getId(), messageObject);
                    if (PhotoViewer.getInstance().isVisible()) {
                        PhotoViewer.getInstance().addPhoto(messageObject, this.photoViewerClassGuid);
                    }
                }
                if (this.messages.size() > this.totalCount) {
                    this.totalCount = this.messages.size();
                }
                this.endReached = this.messages.size() >= this.totalCount;
                if (this.messages.isEmpty()) {
                    if (currentSearchFilter != null) {
                        if (TextUtils.isEmpty(this.currentDataQuery) && dialogId == 0 && minDate == 0) {
                            this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle", R.string.SearchEmptyViewTitle));
                            if (currentSearchFilter.filterType == 1) {
                                str = LocaleController.getString("SearchEmptyViewFilteredSubtitleFiles", R.string.SearchEmptyViewFilteredSubtitleFiles);
                            } else if (currentSearchFilter.filterType == 0) {
                                str = LocaleController.getString("SearchEmptyViewFilteredSubtitleMedia", R.string.SearchEmptyViewFilteredSubtitleMedia);
                            } else if (currentSearchFilter.filterType == 2) {
                                str = LocaleController.getString("SearchEmptyViewFilteredSubtitleLinks", R.string.SearchEmptyViewFilteredSubtitleLinks);
                            } else if (currentSearchFilter.filterType == 3) {
                                str = LocaleController.getString("SearchEmptyViewFilteredSubtitleMusic", R.string.SearchEmptyViewFilteredSubtitleMusic);
                            } else {
                                str = LocaleController.getString("SearchEmptyViewFilteredSubtitleVoice", R.string.SearchEmptyViewFilteredSubtitleVoice);
                            }
                            this.emptyView.subtitle.setVisibility(0);
                            this.emptyView.subtitle.setText(str);
                        } else {
                            this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", R.string.SearchEmptyViewTitle2));
                            this.emptyView.subtitle.setVisibility(0);
                            this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
                        }
                    } else {
                        this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", R.string.SearchEmptyViewTitle2));
                        this.emptyView.subtitle.setVisibility(8);
                    }
                }
                if (currentSearchFilter == null) {
                    this.adapter = this.dialogsAdapter;
                } else {
                    switch (currentSearchFilter.filterType) {
                        case 0:
                            if (TextUtils.isEmpty(this.currentDataQuery)) {
                                this.adapter = this.sharedPhotoVideoAdapter;
                                break;
                            } else {
                                this.adapter = this.dialogsAdapter;
                                break;
                            }
                        case 1:
                            this.adapter = this.sharedDocumentsAdapter;
                            break;
                        case 2:
                            this.adapter = this.sharedLinksAdapter;
                            break;
                        case 3:
                            this.adapter = this.sharedAudioAdapter;
                            break;
                        case 5:
                            this.adapter = this.sharedVoiceAdapter;
                            break;
                    }
                }
                RecyclerView.Adapter adapter = this.recyclerListView.getAdapter();
                RecyclerView.Adapter adapter2 = this.adapter;
                if (adapter != adapter2) {
                    this.recyclerListView.setAdapter(adapter2);
                }
                if (!filterAndQueryIsSame) {
                    this.localTipChats.clear();
                    if (finalResultArray != null) {
                        this.localTipChats.addAll(finalResultArray);
                    }
                    if (query.length() >= 3 && (LocaleController.getString("SavedMessages", R.string.SavedMessages).toLowerCase().startsWith(query) || "saved messages".startsWith(query))) {
                        boolean found = false;
                        int i2 = 0;
                        while (true) {
                            if (i2 < this.localTipChats.size()) {
                                if (this.localTipChats.get(i2) instanceof TLRPC.User) {
                                    res = res2;
                                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == ((TLRPC.User) this.localTipChats.get(i2)).id) {
                                        found = true;
                                    }
                                } else {
                                    res = res2;
                                }
                                i2++;
                                res2 = res;
                            }
                        }
                        if (!found) {
                            this.localTipChats.add(0, UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser());
                        }
                    }
                    this.localTipDates.clear();
                    this.localTipDates.addAll(dateData);
                    this.localTipArchive = false;
                    if (query.length() >= 3 && (LocaleController.getString("ArchiveSearchFilter", R.string.ArchiveSearchFilter).toLowerCase().startsWith(query) || "archive".startsWith(query))) {
                        this.localTipArchive = true;
                    }
                    Delegate delegate = this.delegate;
                    if (delegate != null) {
                        delegate.updateFiltersView(TextUtils.isEmpty(this.currentDataQuery), this.localTipChats, this.localTipDates, this.localTipArchive);
                    }
                }
                this.firstLoading = false;
                View progressView = null;
                int progressViewPosition = -1;
                for (int i3 = 0; i3 < n; i3++) {
                    View child = this.recyclerListView.getChildAt(i3);
                    if (child instanceof FlickerLoadingView) {
                        progressView = child;
                        progressViewPosition = this.recyclerListView.getChildAdapterPosition(child);
                    }
                }
                final View finalProgressView = progressView;
                if (progressView != null) {
                    this.recyclerListView.removeView(progressView);
                }
                if ((this.loadingView.getVisibility() == 0 && this.recyclerListView.getChildCount() == 0) || (this.recyclerListView.getAdapter() != this.sharedPhotoVideoAdapter && progressView != null)) {
                    final int finalProgressViewPosition = progressViewPosition;
                    getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.FilteredSearchView.7
                        @Override // android.view.ViewTreeObserver.OnPreDrawListener
                        public boolean onPreDraw() {
                            FilteredSearchView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                            int n2 = FilteredSearchView.this.recyclerListView.getChildCount();
                            AnimatorSet animatorSet = new AnimatorSet();
                            for (int i4 = 0; i4 < n2; i4++) {
                                View child2 = FilteredSearchView.this.recyclerListView.getChildAt(i4);
                                if (finalProgressView == null || FilteredSearchView.this.recyclerListView.getChildAdapterPosition(child2) >= finalProgressViewPosition) {
                                    child2.setAlpha(0.0f);
                                    int s = Math.min(FilteredSearchView.this.recyclerListView.getMeasuredHeight(), Math.max(0, child2.getTop()));
                                    int delay = (int) ((s / FilteredSearchView.this.recyclerListView.getMeasuredHeight()) * 100.0f);
                                    ObjectAnimator a = ObjectAnimator.ofFloat(child2, View.ALPHA, 0.0f, 1.0f);
                                    a.setStartDelay(delay);
                                    a.setDuration(200L);
                                    animatorSet.playTogether(a);
                                }
                            }
                            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.FilteredSearchView.7.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    NotificationCenter.getInstance(currentAccount).onAnimationFinish(FilteredSearchView.this.animationIndex);
                                }
                            });
                            FilteredSearchView.this.animationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(FilteredSearchView.this.animationIndex, null);
                            animatorSet.start();
                            View view = finalProgressView;
                            if (view != null && view.getParent() == null) {
                                FilteredSearchView.this.recyclerListView.addView(finalProgressView);
                                final RecyclerView.LayoutManager layoutManager = FilteredSearchView.this.recyclerListView.getLayoutManager();
                                if (layoutManager != null) {
                                    layoutManager.ignoreView(finalProgressView);
                                    Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, finalProgressView.getAlpha(), 0.0f);
                                    animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.FilteredSearchView.7.2
                                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                        public void onAnimationEnd(Animator animation) {
                                            finalProgressView.setAlpha(1.0f);
                                            layoutManager.stopIgnoringView(finalProgressView);
                                            FilteredSearchView.this.recyclerListView.removeView(finalProgressView);
                                        }
                                    });
                                    animator.start();
                                }
                            }
                            return true;
                        }
                    });
                }
                this.adapter.notifyDataSetChanged();
                return;
            }
            this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", R.string.SearchEmptyViewTitle2));
            this.emptyView.subtitle.setVisibility(0);
            this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
            this.emptyView.showProgress(false, true);
        }
    }

    public void update() {
        RecyclerView.Adapter adapter = this.adapter;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setKeyboardHeight(int keyboardSize, boolean animated) {
        this.emptyView.setKeyboardHeight(keyboardSize, animated);
    }

    public void messagesDeleted(long channelId, ArrayList<Integer> markAsDeletedMessages) {
        RecyclerView.Adapter adapter;
        boolean changed = false;
        int j = 0;
        while (j < this.messages.size()) {
            MessageObject messageObject = this.messages.get(j);
            long dialogId = messageObject.getDialogId();
            int currentChannelId = (dialogId >= 0 || !ChatObject.isChannel((long) ((int) (-dialogId)), UserConfig.selectedAccount)) ? 0 : (int) (-dialogId);
            if (currentChannelId == channelId) {
                for (int i = 0; i < markAsDeletedMessages.size(); i++) {
                    if (messageObject.getId() == markAsDeletedMessages.get(i).intValue()) {
                        changed = true;
                        this.messages.remove(j);
                        this.messagesById.remove(messageObject.getId());
                        ArrayList<MessageObject> section = this.sectionArrays.get(messageObject.monthKey);
                        section.remove(messageObject);
                        if (section.size() == 0) {
                            this.sections.remove(messageObject.monthKey);
                            this.sectionArrays.remove(messageObject.monthKey);
                        }
                        j--;
                        this.totalCount--;
                    }
                }
            }
            j++;
        }
        if (changed && (adapter = this.adapter) != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /* loaded from: classes4.dex */
    public class SharedPhotoVideoAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public SharedPhotoVideoAdapter(Context context) {
            FilteredSearchView.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (!FilteredSearchView.this.messages.isEmpty()) {
                return ((int) Math.ceil(FilteredSearchView.this.messages.size() / FilteredSearchView.this.columnsCount)) + (!FilteredSearchView.this.endReached ? 1 : 0);
            }
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new SharedPhotoVideoCell(this.mContext, 1);
                    SharedPhotoVideoCell cell = (SharedPhotoVideoCell) view;
                    cell.setDelegate(new SharedPhotoVideoCell.SharedPhotoVideoCellDelegate() { // from class: org.telegram.ui.FilteredSearchView.SharedPhotoVideoAdapter.1
                        @Override // org.telegram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate
                        public void didClickItem(SharedPhotoVideoCell cell2, int index, MessageObject messageObject, int a) {
                            FilteredSearchView.this.onItemClick(index, cell2, messageObject, a);
                        }

                        @Override // org.telegram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate
                        public boolean didLongClickItem(SharedPhotoVideoCell cell2, int index, MessageObject messageObject, int a) {
                            if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                return FilteredSearchView.this.onItemLongClick(messageObject, cell2, a);
                            }
                            didClickItem(cell2, index, messageObject, a);
                            return true;
                        }
                    });
                    break;
                case 1:
                default:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext) { // from class: org.telegram.ui.FilteredSearchView.SharedPhotoVideoAdapter.2
                        @Override // org.telegram.ui.Components.FlickerLoadingView
                        public int getColumnsCount() {
                            return FilteredSearchView.this.columnsCount;
                        }
                    };
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(2);
                    view = flickerLoadingView;
                    break;
                case 2:
                    view = new GraySectionCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & (-218103809));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean animated = true;
            if (holder.getItemViewType() == 0) {
                ArrayList<MessageObject> messageObjects = FilteredSearchView.this.messages;
                SharedPhotoVideoCell cell = (SharedPhotoVideoCell) holder.itemView;
                cell.setItemsCount(FilteredSearchView.this.columnsCount);
                cell.setIsFirst(position == 0);
                for (int a = 0; a < FilteredSearchView.this.columnsCount; a++) {
                    int index = (FilteredSearchView.this.columnsCount * position) + a;
                    if (index < messageObjects.size()) {
                        MessageObject messageObject = messageObjects.get(index);
                        cell.setItem(a, FilteredSearchView.this.messages.indexOf(messageObject), messageObject);
                        if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                            FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                            cell.setChecked(a, FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), true);
                        } else {
                            cell.setChecked(a, false, true);
                        }
                    } else {
                        cell.setItem(a, index, null);
                    }
                }
                cell.requestLayout();
            } else if (holder.getItemViewType() == 3) {
                DialogCell cell2 = (DialogCell) holder.itemView;
                cell2.useSeparator = position != getItemCount() - 1;
                MessageObject messageObject2 = FilteredSearchView.this.messages.get(position);
                if (cell2.getMessage() == null || cell2.getMessage().getId() != messageObject2.getId()) {
                    animated = false;
                }
                cell2.setDialog(messageObject2.getDialogId(), messageObject2, messageObject2.messageOwner.date, false);
                if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                    FilteredSearchView.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                    cell2.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated);
                    return;
                }
                cell2.setChecked(false, animated);
            } else if (holder.getItemViewType() == 1) {
                FlickerLoadingView flickerLoadingView = (FlickerLoadingView) holder.itemView;
                int count = (int) Math.ceil(FilteredSearchView.this.messages.size() / FilteredSearchView.this.columnsCount);
                flickerLoadingView.skipDrawItemsCount(FilteredSearchView.this.columnsCount - ((FilteredSearchView.this.columnsCount * count) - FilteredSearchView.this.messages.size()));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            int count = (int) Math.ceil(FilteredSearchView.this.messages.size() / FilteredSearchView.this.columnsCount);
            if (position < count) {
                return 0;
            }
            return 1;
        }
    }

    public void onItemClick(int index, View view, MessageObject message, int a) {
        if (message == null) {
            return;
        }
        if (this.uiCallback.actionModeShowing()) {
            this.uiCallback.toggleItemSelection(message, view, a);
        } else if (view instanceof DialogCell) {
            this.uiCallback.goToMessage(message);
        } else if (this.currentSearchFilter.filterType == 0) {
            PhotoViewer.getInstance().setParentActivity(this.parentActivity);
            PhotoViewer.getInstance().openPhoto(this.messages, index, 0L, 0L, this.provider);
            this.photoViewerClassGuid = PhotoViewer.getInstance().getClassGuid();
        } else if (this.currentSearchFilter.filterType == 3 || this.currentSearchFilter.filterType == 5) {
            if (view instanceof SharedAudioCell) {
                ((SharedAudioCell) view).didPressedButton();
            }
        } else if (this.currentSearchFilter.filterType == 1) {
            if (view instanceof SharedDocumentCell) {
                SharedDocumentCell cell = (SharedDocumentCell) view;
                TLRPC.Document document = message.getDocument();
                if (cell.isLoaded()) {
                    if (message.canPreviewDocument()) {
                        PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                        int index2 = this.messages.indexOf(message);
                        if (index2 >= 0) {
                            PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                            PhotoViewer.getInstance().openPhoto(this.messages, index2, 0L, 0L, this.provider);
                            this.photoViewerClassGuid = PhotoViewer.getInstance().getClassGuid();
                            return;
                        }
                        ArrayList<MessageObject> documents = new ArrayList<>();
                        documents.add(message);
                        PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                        PhotoViewer.getInstance().openPhoto(documents, 0, 0L, 0L, this.provider);
                        this.photoViewerClassGuid = PhotoViewer.getInstance().getClassGuid();
                        return;
                    }
                    AndroidUtilities.openDocument(message, this.parentActivity, this.parentFragment);
                } else if (!cell.isLoading()) {
                    MessageObject messageObject = cell.getMessage();
                    messageObject.putInDownloadsStore = true;
                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, messageObject, 0, 0);
                    cell.updateFileExistIcon(true);
                } else {
                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(document);
                    cell.updateFileExistIcon(true);
                }
            }
        } else if (this.currentSearchFilter.filterType == 2) {
            try {
                TLRPC.WebPage webPage = message.messageOwner.media != null ? message.messageOwner.media.webpage : null;
                String link = null;
                if (webPage != null && !(webPage instanceof TLRPC.TL_webPageEmpty)) {
                    if (webPage.cached_page != null) {
                        ArticleViewer.getInstance().setParentActivity(this.parentActivity, this.parentFragment);
                        ArticleViewer.getInstance().open(message);
                        return;
                    } else if (webPage.embed_url != null && webPage.embed_url.length() != 0) {
                        openWebView(webPage, message);
                        return;
                    } else {
                        link = webPage.url;
                    }
                }
                if (link == null) {
                    link = ((SharedLinkCell) view).getLink(0);
                }
                if (link != null) {
                    openUrl(link);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;
        private final SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate = new AnonymousClass1();

        /* renamed from: org.telegram.ui.FilteredSearchView$SharedLinksAdapter$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 implements SharedLinkCell.SharedLinkCellDelegate {
            AnonymousClass1() {
                SharedLinksAdapter.this = this$1;
            }

            @Override // org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate
            public void needOpenWebView(TLRPC.WebPage webPage, MessageObject message) {
                FilteredSearchView.this.openWebView(webPage, message);
            }

            @Override // org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate
            public boolean canPerformActions() {
                return !FilteredSearchView.this.uiCallback.actionModeShowing();
            }

            @Override // org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate
            public void onLinkPress(final String urlFinal, boolean longPress) {
                if (!longPress) {
                    FilteredSearchView.this.openUrl(urlFinal);
                    return;
                }
                BottomSheet.Builder builder = new BottomSheet.Builder(FilteredSearchView.this.parentActivity);
                builder.setTitle(urlFinal);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilteredSearchView$SharedLinksAdapter$1$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FilteredSearchView.SharedLinksAdapter.AnonymousClass1.this.m3450xd2f1fe27(urlFinal, dialogInterface, i);
                    }
                });
                FilteredSearchView.this.parentFragment.showDialog(builder.create());
            }

            /* renamed from: lambda$onLinkPress$0$org-telegram-ui-FilteredSearchView$SharedLinksAdapter$1 */
            public /* synthetic */ void m3450xd2f1fe27(String urlFinal, DialogInterface dialog, int which) {
                if (which == 0) {
                    FilteredSearchView.this.openUrl(urlFinal);
                } else if (which == 1) {
                    String url = urlFinal;
                    if (url.startsWith(MailTo.MAILTO_SCHEME)) {
                        url = url.substring(7);
                    } else if (url.startsWith("tel:")) {
                        url = url.substring(4);
                    }
                    AndroidUtilities.addToClipboard(url);
                }
            }
        }

        public SharedLinksAdapter(Context context) {
            FilteredSearchView.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public Object getItem(int section, int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            return true;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getSectionCount() {
            int i = 0;
            if (FilteredSearchView.this.messages.isEmpty()) {
                return 0;
            }
            if (FilteredSearchView.this.sections.isEmpty() && FilteredSearchView.this.isLoading) {
                return 0;
            }
            int size = FilteredSearchView.this.sections.size();
            if (!FilteredSearchView.this.sections.isEmpty() && !FilteredSearchView.this.endReached) {
                i = 1;
            }
            return size + i;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getCountForSection(int section) {
            int i = 1;
            if (section < FilteredSearchView.this.sections.size()) {
                int size = FilteredSearchView.this.sectionArrays.get(FilteredSearchView.this.sections.get(section)).size();
                if (section == 0) {
                    i = 0;
                }
                return size + i;
            }
            return 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & (-218103809));
            }
            if (section == 0) {
                view.setAlpha(0.0f);
                return view;
            }
            if (section < FilteredSearchView.this.sections.size()) {
                view.setAlpha(1.0f);
                String name = FilteredSearchView.this.sections.get(section);
                ArrayList<MessageObject> messageObjects = FilteredSearchView.this.sectionArrays.get(name);
                MessageObject messageObject = messageObjects.get(0);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate(messageObject.messageOwner.date));
            }
            return view;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GraySectionCell(this.mContext);
                    break;
                case 1:
                    view = new SharedLinkCell(this.mContext, 1);
                    ((SharedLinkCell) view).setDelegate(this.sharedLinkCellDelegate);
                    break;
                default:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setViewType(5);
                    flickerLoadingView.setIsSingleCell(true);
                    view = flickerLoadingView;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                String name = FilteredSearchView.this.sections.get(section);
                ArrayList<MessageObject> messageObjects = FilteredSearchView.this.sectionArrays.get(name);
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate(messageObjects.get(0).messageOwner.date));
                        return;
                    case 1:
                        if (section != 0) {
                            position--;
                        }
                        final SharedLinkCell sharedLinkCell = (SharedLinkCell) holder.itemView;
                        final MessageObject messageObject = messageObjects.get(position);
                        final boolean animated = sharedLinkCell.getMessage() != null && sharedLinkCell.getMessage().getId() == messageObject.getId();
                        if (position != messageObjects.size() - 1 || (section == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                            z = true;
                        }
                        sharedLinkCell.setLink(messageObject, z);
                        sharedLinkCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.FilteredSearchView.SharedLinksAdapter.2
                            @Override // android.view.ViewTreeObserver.OnPreDrawListener
                            public boolean onPreDraw() {
                                sharedLinkCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                    FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                                    sharedLinkCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated);
                                    return true;
                                }
                                sharedLinkCell.setChecked(false, animated);
                                return true;
                            }
                        });
                        return;
                    default:
                        return;
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int section, int position) {
            if (section < FilteredSearchView.this.sections.size()) {
                if (section != 0 && position == 0) {
                    return 0;
                }
                return 1;
            }
            return 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SharedDocumentsAdapter extends RecyclerListView.SectionsAdapter {
        private int currentType;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int type) {
            FilteredSearchView.this = r1;
            this.mContext = context;
            this.currentType = type;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            return section == 0 || row != 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getSectionCount() {
            int i = 0;
            if (FilteredSearchView.this.sections.isEmpty()) {
                return 0;
            }
            int size = FilteredSearchView.this.sections.size();
            if (!FilteredSearchView.this.sections.isEmpty() && !FilteredSearchView.this.endReached) {
                i = 1;
            }
            return size + i;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public Object getItem(int section, int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getCountForSection(int section) {
            int i = 1;
            if (section < FilteredSearchView.this.sections.size()) {
                int size = FilteredSearchView.this.sectionArrays.get(FilteredSearchView.this.sections.get(section)).size();
                if (section == 0) {
                    i = 0;
                }
                return size + i;
            }
            return 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & (-218103809));
            }
            if (section == 0) {
                view.setAlpha(0.0f);
                return view;
            }
            if (section < FilteredSearchView.this.sections.size()) {
                view.setAlpha(1.0f);
                String name = FilteredSearchView.this.sections.get(section);
                ArrayList<MessageObject> messageObjects = FilteredSearchView.this.sectionArrays.get(name);
                MessageObject messageObject = messageObjects.get(0);
                String str = LocaleController.formatSectionDate(messageObject.messageOwner.date);
                ((GraySectionCell) view).setText(str);
            }
            return view;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GraySectionCell(this.mContext);
                    break;
                case 1:
                    view = new SharedDocumentCell(this.mContext, 2);
                    break;
                case 2:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    int i = this.currentType;
                    if (i == 2 || i == 4) {
                        flickerLoadingView.setViewType(4);
                    } else {
                        flickerLoadingView.setViewType(3);
                    }
                    flickerLoadingView.setIsSingleCell(true);
                    view = flickerLoadingView;
                    break;
                default:
                    view = new SharedAudioCell(this.mContext, 1, null) { // from class: org.telegram.ui.FilteredSearchView.SharedDocumentsAdapter.1
                        @Override // org.telegram.ui.Cells.SharedAudioCell
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean result = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(result ? FilteredSearchView.this.messages : null, false);
                                return result;
                            } else if (!messageObject.isMusic()) {
                                return false;
                            } else {
                                MediaController.PlaylistGlobalSearchParams params = new MediaController.PlaylistGlobalSearchParams(FilteredSearchView.this.currentDataQuery, FilteredSearchView.this.currentSearchDialogId, FilteredSearchView.this.currentSearchMinDate, FilteredSearchView.this.currentSearchMinDate, FilteredSearchView.this.currentSearchFilter);
                                params.endReached = FilteredSearchView.this.endReached;
                                params.nextSearchRate = FilteredSearchView.this.nextSearchRate;
                                params.totalCount = FilteredSearchView.this.totalCount;
                                params.folderId = FilteredSearchView.this.currentIncludeFolder ? 1 : 0;
                                return MediaController.getInstance().setPlaylist(FilteredSearchView.this.messages, messageObject, 0L, params);
                            }
                        }
                    };
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                String name = FilteredSearchView.this.sections.get(section);
                ArrayList<MessageObject> messageObjects = FilteredSearchView.this.sectionArrays.get(name);
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 0:
                        String str = LocaleController.formatSectionDate(messageObjects.get(0).messageOwner.date);
                        ((GraySectionCell) holder.itemView).setText(str);
                        return;
                    case 1:
                        if (section != 0) {
                            position--;
                        }
                        final SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                        final MessageObject messageObject = messageObjects.get(position);
                        final boolean animated = sharedDocumentCell.getMessage() != null && sharedDocumentCell.getMessage().getId() == messageObject.getId();
                        if (position != messageObjects.size() - 1 || (section == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                            z = true;
                        }
                        sharedDocumentCell.setDocument(messageObject, z);
                        sharedDocumentCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.FilteredSearchView.SharedDocumentsAdapter.2
                            @Override // android.view.ViewTreeObserver.OnPreDrawListener
                            public boolean onPreDraw() {
                                sharedDocumentCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                    FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                                    sharedDocumentCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated);
                                    return true;
                                }
                                sharedDocumentCell.setChecked(false, animated);
                                return true;
                            }
                        });
                        return;
                    case 2:
                    default:
                        return;
                    case 3:
                        if (section != 0) {
                            position--;
                        }
                        final SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                        final MessageObject messageObject2 = messageObjects.get(position);
                        final boolean animated2 = sharedAudioCell.getMessage() != null && sharedAudioCell.getMessage().getId() == messageObject2.getId();
                        if (position != messageObjects.size() - 1 || (section == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                            z = true;
                        }
                        sharedAudioCell.setMessageObject(messageObject2, z);
                        sharedAudioCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.FilteredSearchView.SharedDocumentsAdapter.3
                            @Override // android.view.ViewTreeObserver.OnPreDrawListener
                            public boolean onPreDraw() {
                                sharedAudioCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                    FilteredSearchView.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                                    sharedAudioCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated2);
                                    return true;
                                }
                                sharedAudioCell.setChecked(false, animated2);
                                return true;
                            }
                        });
                        return;
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int section, int position) {
            if (section < FilteredSearchView.this.sections.size()) {
                if (section != 0 && position == 0) {
                    return 0;
                }
                int i = this.currentType;
                if (i == 2 || i == 4) {
                    return 3;
                }
                return 1;
            }
            return 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }

    public void openUrl(String link) {
        if (AndroidUtilities.shouldShowUrlInAlert(link)) {
            AlertsCreator.showOpenUrlAlert(this.parentFragment, link, true, true);
        } else {
            Browser.openUrl(this.parentActivity, link);
        }
    }

    public void openWebView(TLRPC.WebPage webPage, MessageObject message) {
        EmbedBottomSheet.show(this.parentActivity, message, this.provider, webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height, false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int i = UserConfig.selectedAccount;
        this.lastAccount = i;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.lastAccount).removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            int n = this.recyclerListView.getChildCount();
            for (int i = 0; i < n; i++) {
                if (this.recyclerListView.getChildAt(i) instanceof DialogCell) {
                    ((DialogCell) this.recyclerListView.getChildAt(i)).update(0);
                }
                this.recyclerListView.getChildAt(i).invalidate();
            }
        }
    }

    public boolean onItemLongClick(MessageObject item, View view, int a) {
        if (!this.uiCallback.actionModeShowing()) {
            this.uiCallback.showActionMode();
        }
        if (this.uiCallback.actionModeShowing()) {
            this.uiCallback.toggleItemSelection(item, view, a);
            return true;
        }
        return true;
    }

    /* loaded from: classes4.dex */
    public static class MessageHashId {
        public long dialogId;
        public int messageId;

        public MessageHashId(int messageId, long dialogId) {
            this.dialogId = dialogId;
            this.messageId = messageId;
        }

        public void set(int messageId, long dialogId) {
            this.dialogId = dialogId;
            this.messageId = messageId;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MessageHashId that = (MessageHashId) o;
            return this.dialogId == that.dialogId && this.messageId == that.messageId;
        }

        public int hashCode() {
            return this.messageId;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class OnlyUserFiltersAdapter extends RecyclerListView.SelectionAdapter {
        OnlyUserFiltersAdapter() {
            FilteredSearchView.this = this$0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new DialogCell(null, parent.getContext(), true, false);
                    break;
                case 3:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(parent.getContext());
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(1);
                    view = flickerLoadingView;
                    break;
                default:
                    GraySectionCell cell = new GraySectionCell(parent.getContext());
                    cell.setText(LocaleController.getString("SearchMessages", R.string.SearchMessages));
                    view = cell;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                final DialogCell cell = (DialogCell) holder.itemView;
                final MessageObject messageObject = FilteredSearchView.this.messages.get(position);
                cell.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date, false);
                boolean z = true;
                cell.useSeparator = position != getItemCount() - 1;
                if (cell.getMessage() == null || cell.getMessage().getId() != messageObject.getId()) {
                    z = false;
                }
                final boolean animated = z;
                cell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.FilteredSearchView.OnlyUserFiltersAdapter.1
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        cell.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                            FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                            cell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated);
                            return true;
                        }
                        cell.setChecked(false, animated);
                        return true;
                    }
                });
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position >= FilteredSearchView.this.messages.size()) {
                return 3;
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (FilteredSearchView.this.messages.isEmpty()) {
                return 0;
            }
            return FilteredSearchView.this.messages.size() + (!FilteredSearchView.this.endReached ? 1 : 0);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        RecyclerView.Adapter adapter;
        int oldColumnsCount = this.columnsCount;
        if (AndroidUtilities.isTablet()) {
            this.columnsCount = 3;
        } else if (getResources().getConfiguration().orientation == 2) {
            this.columnsCount = 6;
        } else {
            this.columnsCount = 3;
        }
        if (oldColumnsCount != this.columnsCount && (adapter = this.adapter) == this.sharedPhotoVideoAdapter) {
            this.ignoreRequestLayout = true;
            adapter.notifyDataSetChanged();
            this.ignoreRequestLayout = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreRequestLayout) {
            return;
        }
        super.requestLayout();
    }

    public void setDelegate(Delegate delegate, boolean update) {
        this.delegate = delegate;
        if (update && delegate != null && !this.localTipChats.isEmpty()) {
            delegate.updateFiltersView(false, this.localTipChats, this.localTipDates, this.localTipArchive);
        }
    }

    public void setUiCallback(UiCallback callback) {
        this.uiCallback = callback;
    }

    public void showFloatingDateView() {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        AndroidUtilities.runOnUIThread(this.hideFloatingDateRunnable, 650L);
        if (this.floatingDateView.getTag() != null) {
            return;
        }
        AnimatorSet animatorSet = this.floatingDateAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.floatingDateView.setTag(1);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.floatingDateAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, 0.0f));
        this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.FilteredSearchView.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                FilteredSearchView.this.floatingDateAnimation = null;
            }
        });
        this.floatingDateAnimation.start();
    }

    private void hideFloatingDateView(boolean animated) {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        if (this.floatingDateView.getTag() == null) {
            return;
        }
        this.floatingDateView.setTag(null);
        AnimatorSet animatorSet = this.floatingDateAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.floatingDateAnimation = null;
        }
        if (!animated) {
            this.floatingDateView.setAlpha(0.0f);
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.floatingDateAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f)));
        this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.FilteredSearchView.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                FilteredSearchView.this.floatingDateAnimation = null;
            }
        });
        this.floatingDateAnimation.start();
    }

    public void setChatPreviewDelegate(SearchViewPager.ChatPreviewDelegate chatPreviewDelegate) {
        this.chatPreviewDelegate = chatPreviewDelegate;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_startStopLoadIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_startStopLoadIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_iconText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholderText));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholder));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_scamDrawable, Theme.dialogs_fakeDrawable}, null, Theme.key_chats_draft));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, null, Theme.key_chats_pinnedIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], null, null, Theme.key_chats_message_threeLines));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[0], null, null, Theme.key_chats_message));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, null, null, Theme.key_chats_nameMessage_threeLines));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_draft));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (String[]) null, Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionMessage));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, Theme.key_chats_date));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, null, null, Theme.key_chats_pinnedOverlay));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, null, null, Theme.key_chats_tabletSelectedOverlay));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable}, null, Theme.key_chats_sentCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, null, Theme.key_chats_sentReadCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_clockDrawable}, null, Theme.key_chats_sentClock));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, null, null, Theme.key_chats_sentError));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_errorDrawable}, null, Theme.key_chats_sentErrorIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_muteDrawable}, null, Theme.key_chats_muteIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_mentionDrawable}, null, Theme.key_chats_mentionIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_archivePinBackground));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_archiveBackground));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_chats_onlineCircle));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        return arrayList;
    }
}
