package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.PhotoPickerActivity;
/* loaded from: classes5.dex */
public class ChatAttachAlertDocumentLayout extends ChatAttachAlert.AttachAlertLayout {
    private static final int ANIMATION_BACKWARD = 2;
    private static final int ANIMATION_FORWARD = 1;
    private static final int ANIMATION_NONE = 0;
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_MUSIC = 1;
    public static final int TYPE_RINGTONE = 2;
    private static final int search_button = 0;
    private static final int sort_button = 6;
    private float additionalTranslationY;
    private boolean allowMusic;
    private LinearLayoutManager backgroundLayoutManager;
    private ListAdapter backgroundListAdapter;
    private RecyclerListView backgroundListView;
    private boolean canSelectOnlyImageFiles;
    private int currentAnimationType;
    private File currentDir;
    private DocumentSelectActivityDelegate delegate;
    private StickerEmptyView emptyView;
    private FiltersView filtersView;
    private AnimatorSet filtersViewAnimator;
    private boolean hasFiles;
    private boolean ignoreLayout;
    public boolean isSoundPicker;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    ValueAnimator listAnimation;
    private RecyclerListView listView;
    private FlickerLoadingView loadingView;
    private boolean receiverRegistered;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searching;
    private boolean sendPressed;
    private boolean sortByName;
    private ActionBarMenuItem sortItem;
    private int type;
    private HashMap<String, ListItem> selectedFiles = new HashMap<>();
    private ArrayList<String> selectedFilesOrder = new ArrayList<>();
    private HashMap<FilteredSearchView.MessageHashId, MessageObject> selectedMessages = new HashMap<>();
    private int maxSelectedFiles = -1;
    private BroadcastReceiver receiver = new AnonymousClass1();

    /* loaded from: classes5.dex */
    public interface DocumentSelectActivityDelegate {
        void didSelectFiles(ArrayList<String> arrayList, String str, ArrayList<MessageObject> arrayList2, boolean z, int i);

        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i);

        void startDocumentSelectActivity();

        void startMusicSelectActivity();

        /* renamed from: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$DocumentSelectActivityDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$didSelectPhotos(DocumentSelectActivityDelegate _this, ArrayList arrayList, boolean notify, int scheduleDate) {
            }

            public static void $default$startDocumentSelectActivity(DocumentSelectActivityDelegate _this) {
            }

            public static void $default$startMusicSelectActivity(DocumentSelectActivityDelegate _this) {
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class ListItem {
        public String ext;
        public File file;
        public int icon;
        public String subtitle;
        public String thumb;
        public String title;

        private ListItem() {
            this.subtitle = "";
            this.ext = "";
        }

        /* synthetic */ ListItem(AnonymousClass1 x0) {
            this();
        }
    }

    /* loaded from: classes5.dex */
    public static class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }

        /* synthetic */ HistoryEntry(AnonymousClass1 x0) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
            ChatAttachAlertDocumentLayout.this = this$0;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertDocumentLayout.AnonymousClass1.this.m2442xf4e8da1();
                }
            };
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                ChatAttachAlertDocumentLayout.this.listView.postDelayed(r, 1000L);
            } else {
                r.run();
            }
        }

        /* renamed from: lambda$onReceive$0$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$1 */
        public /* synthetic */ void m2442xf4e8da1() {
            try {
                if (ChatAttachAlertDocumentLayout.this.currentDir == null) {
                    ChatAttachAlertDocumentLayout.this.listRoots();
                } else {
                    ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                    chatAttachAlertDocumentLayout.listFiles(chatAttachAlertDocumentLayout.currentDir);
                }
                ChatAttachAlertDocumentLayout.this.updateSearchButton();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public ChatAttachAlertDocumentLayout(ChatAttachAlert alert, Context context, int type, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        this.receiverRegistered = false;
        this.listAdapter = new ListAdapter(context);
        this.allowMusic = type == 1;
        this.isSoundPicker = type == 2;
        this.sortByName = SharedConfig.sortFilesByName;
        loadRecentFiles();
        this.searching = false;
        if (!this.receiverRegistered) {
            this.receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            filter.addAction("android.intent.action.MEDIA_CHECKING");
            filter.addAction("android.intent.action.MEDIA_EJECT");
            filter.addAction("android.intent.action.MEDIA_MOUNTED");
            filter.addAction("android.intent.action.MEDIA_NOFS");
            filter.addAction("android.intent.action.MEDIA_REMOVED");
            filter.addAction("android.intent.action.MEDIA_SHARED");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            filter.addDataScheme("file");
            ApplicationLoader.applicationContext.registerReceiver(this.receiver, filter);
        }
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.2
            {
                ChatAttachAlertDocumentLayout.this = this;
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                ChatAttachAlertDocumentLayout.this.searching = true;
                ChatAttachAlertDocumentLayout.this.sortItem.setVisibility(8);
                ChatAttachAlertDocumentLayout.this.parentAlert.makeFocusable(ChatAttachAlertDocumentLayout.this.searchItem.getSearchField(), true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                ChatAttachAlertDocumentLayout.this.searching = false;
                ChatAttachAlertDocumentLayout.this.sortItem.setVisibility(0);
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
                }
                ChatAttachAlertDocumentLayout.this.listAdapter.notifyDataSetChanged();
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(null, true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(editText.getText().toString(), false);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchFilterCleared(FiltersView.MediaFilterData filterData) {
                ChatAttachAlertDocumentLayout.this.searchAdapter.removeSearchFilter(filterData);
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(ChatAttachAlertDocumentLayout.this.searchItem.getSearchField().getText().toString(), false);
                ChatAttachAlertDocumentLayout.this.searchAdapter.updateFiltersView(true, null, null, true);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        EditTextBoldCursor editText = this.searchItem.getSearchField();
        editText.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        editText.setCursorColor(getThemedColor(Theme.key_dialogTextBlack));
        editText.setHintTextColor(getThemedColor(Theme.key_chat_messagePanelHint));
        ActionBarMenuItem addItem = menu.addItem(6, this.sortByName ? R.drawable.msg_contacts_time : R.drawable.msg_contacts_name);
        this.sortItem = addItem;
        addItem.setContentDescription(LocaleController.getString("AccDescrContactSorting", R.string.AccDescrContactSorting));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, resourcesProvider);
        this.loadingView = flickerLoadingView;
        addView(flickerLoadingView);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.loadingView, 1, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.3
            {
                ChatAttachAlertDocumentLayout.this = this;
            }

            @Override // android.view.View
            public void setTranslationY(float translationY) {
                super.setTranslationY(ChatAttachAlertDocumentLayout.this.additionalTranslationY + translationY);
            }

            @Override // android.view.View
            public float getTranslationY() {
                return super.getTranslationY() - ChatAttachAlertDocumentLayout.this.additionalTranslationY;
            }
        };
        this.emptyView = stickerEmptyView;
        addView(stickerEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setVisibility(8);
        this.emptyView.setOnTouchListener(ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda1.INSTANCE);
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.4
            Paint paint = new Paint();

            {
                ChatAttachAlertDocumentLayout.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                if (ChatAttachAlertDocumentLayout.this.currentAnimationType == 2 && getChildCount() > 0) {
                    float top = 2.14748365E9f;
                    for (int i = 0; i < getChildCount(); i++) {
                        if (getChildAt(i).getY() < top) {
                            top = getChildAt(i).getY();
                        }
                    }
                    this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                }
                super.dispatchDraw(canvas);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                if (ChatAttachAlertDocumentLayout.this.currentAnimationType != 0) {
                    return false;
                }
                return super.onTouchEvent(e);
            }
        };
        this.backgroundListView = recyclerListView;
        recyclerListView.setSectionsType(2);
        this.backgroundListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.backgroundListView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, 1, false, AndroidUtilities.dp(56.0f), this.backgroundListView);
        this.backgroundLayoutManager = fillLastLinearLayoutManager;
        recyclerListView2.setLayoutManager(fillLastLinearLayoutManager);
        this.backgroundListView.setClipToPadding(false);
        RecyclerListView recyclerListView3 = this.backgroundListView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.backgroundListAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.backgroundListView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        addView(this.backgroundListView, LayoutHelper.createFrame(-1, -1.0f));
        this.backgroundListView.setVisibility(8);
        RecyclerListView recyclerListView4 = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.5
            Paint paint = new Paint();

            {
                ChatAttachAlertDocumentLayout.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                if (ChatAttachAlertDocumentLayout.this.currentAnimationType == 1 && getChildCount() > 0) {
                    float top = 2.14748365E9f;
                    for (int i = 0; i < getChildCount(); i++) {
                        if (getChildAt(i).getY() < top) {
                            top = getChildAt(i).getY();
                        }
                    }
                    this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                }
                super.dispatchDraw(canvas);
            }
        };
        this.listView = recyclerListView4;
        recyclerListView4.setSectionsType(2);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView5 = this.listView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager2 = new FillLastLinearLayoutManager(context, 1, false, AndroidUtilities.dp(56.0f), this.listView) { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.6
            {
                ChatAttachAlertDocumentLayout.this = this;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.6.1
                    {
                        AnonymousClass6.this = this;
                    }

                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        int dy = super.calculateDyToMakeVisible(view, snapPreference);
                        return dy - (ChatAttachAlertDocumentLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(56.0f));
                    }

                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager = fillLastLinearLayoutManager2;
        recyclerListView5.setLayoutManager(fillLastLinearLayoutManager2);
        this.listView.setClipToPadding(false);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchAdapter = new SearchAdapter(context);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.7
            {
                ChatAttachAlertDocumentLayout.this = this;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertDocumentLayout.this.parentAlert.updateLayout(ChatAttachAlertDocumentLayout.this, true, dy);
                ChatAttachAlertDocumentLayout.this.updateEmptyViewPosition();
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() == ChatAttachAlertDocumentLayout.this.searchAdapter) {
                    int firstVisibleItem = ChatAttachAlertDocumentLayout.this.layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = ChatAttachAlertDocumentLayout.this.layoutManager.findLastVisibleItemPosition();
                    int visibleItemCount = Math.abs(lastVisibleItem - firstVisibleItem) + 1;
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    if (visibleItemCount > 0 && lastVisibleItem >= totalItemCount - 10) {
                        ChatAttachAlertDocumentLayout.this.searchAdapter.loadMore();
                    }
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerListView.Holder holder;
                boolean z = false;
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertDocumentLayout.this.parentAlert.getBackgroundPaddingTop();
                    int top = (ChatAttachAlertDocumentLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - offset;
                    if (top + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertDocumentLayout.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > AndroidUtilities.dp(56.0f)) {
                        ChatAttachAlertDocumentLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(56.0f));
                    }
                }
                if (newState == 1 && ChatAttachAlertDocumentLayout.this.searching && ChatAttachAlertDocumentLayout.this.listView.getAdapter() == ChatAttachAlertDocumentLayout.this.searchAdapter) {
                    AndroidUtilities.hideKeyboard(ChatAttachAlertDocumentLayout.this.parentAlert.getCurrentFocus());
                }
                ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                if (newState != 0) {
                    z = true;
                }
                chatAttachAlertDocumentLayout.scrolling = z;
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChatAttachAlertDocumentLayout.this.m2436x1f877fd7(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return ChatAttachAlertDocumentLayout.this.m2437xac7496f6(view, i);
            }
        });
        FiltersView filtersView = new FiltersView(context, resourcesProvider);
        this.filtersView = filtersView;
        filtersView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChatAttachAlertDocumentLayout.this.m2438x3961ae15(view, i);
            }
        });
        this.filtersView.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        addView(this.filtersView, LayoutHelper.createFrame(-1, -2, 48));
        this.filtersView.setTranslationY(-AndroidUtilities.dp(44.0f));
        this.filtersView.setVisibility(4);
        listRoots();
        updateSearchButton();
        updateEmptyView();
    }

    public static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertDocumentLayout */
    public /* synthetic */ void m2436x1f877fd7(View view, int position) {
        Object object;
        ChatActivity chatActivity;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter = this.listAdapter;
        if (adapter == listAdapter) {
            object = listAdapter.getItem(position);
        } else {
            object = this.searchAdapter.getItem(position);
        }
        if (object instanceof ListItem) {
            ListItem item = (ListItem) object;
            File file = item.file;
            boolean isExternalStorageManager = false;
            if (Build.VERSION.SDK_INT >= 30) {
                isExternalStorageManager = Environment.isExternalStorageManager();
            }
            if (!BuildVars.NO_SCOPED_STORAGE && ((item.icon == R.drawable.files_storage || item.icon == R.drawable.files_internal) && !isExternalStorageManager)) {
                this.delegate.startDocumentSelectActivity();
                return;
            } else if (file == null) {
                if (item.icon == R.drawable.files_gallery) {
                    final HashMap<Object, Object> selectedPhotos = new HashMap<>();
                    final ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
                    if (this.parentAlert.baseFragment instanceof ChatActivity) {
                        chatActivity = (ChatActivity) this.parentAlert.baseFragment;
                    } else {
                        chatActivity = null;
                    }
                    PhotoPickerActivity fragment = new PhotoPickerActivity(0, MediaController.allMediaAlbumEntry, selectedPhotos, selectedPhotosOrder, 0, chatActivity != null, chatActivity, false);
                    fragment.setDocumentsPicker(true);
                    fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.8
                        {
                            ChatAttachAlertDocumentLayout.this = this;
                        }

                        @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
                        public void selectedPhotosChanged() {
                        }

                        @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
                        public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                            if (!canceled) {
                                ChatAttachAlertDocumentLayout.this.sendSelectedPhotos(selectedPhotos, selectedPhotosOrder, notify, scheduleDate);
                            }
                        }

                        @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
                        public void onCaptionChanged(CharSequence text) {
                        }

                        @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
                        public void onOpenInPressed() {
                            ChatAttachAlertDocumentLayout.this.delegate.startDocumentSelectActivity();
                        }
                    });
                    fragment.setMaxSelectedPhotos(this.maxSelectedFiles, false);
                    this.parentAlert.baseFragment.presentFragment(fragment);
                    this.parentAlert.dismiss(true);
                    return;
                } else if (item.icon == R.drawable.files_music) {
                    DocumentSelectActivityDelegate documentSelectActivityDelegate = this.delegate;
                    if (documentSelectActivityDelegate != null) {
                        documentSelectActivityDelegate.startMusicSelectActivity();
                        return;
                    }
                    return;
                } else {
                    int top = getTopForScroll();
                    prepareAnimation();
                    HistoryEntry he = (HistoryEntry) this.listAdapter.history.remove(this.listAdapter.history.size() - 1);
                    this.parentAlert.actionBar.setTitle(he.title);
                    if (he.dir != null) {
                        listFiles(he.dir);
                    } else {
                        listRoots();
                    }
                    updateSearchButton();
                    this.layoutManager.scrollToPositionWithOffset(0, top);
                    runAnimation(2);
                    return;
                }
            } else if (file.isDirectory()) {
                HistoryEntry he2 = new HistoryEntry(null);
                View child = this.listView.getChildAt(0);
                RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(child);
                if (holder != null) {
                    he2.scrollItem = holder.getAdapterPosition();
                    he2.scrollOffset = child.getTop();
                    he2.dir = this.currentDir;
                    he2.title = this.parentAlert.actionBar.getTitle();
                    prepareAnimation();
                    this.listAdapter.history.add(he2);
                    if (listFiles(file)) {
                        runAnimation(1);
                        this.parentAlert.actionBar.setTitle(item.title);
                        return;
                    }
                    this.listAdapter.history.remove(he2);
                    return;
                }
                return;
            } else {
                onItemClick(view, item);
                return;
            }
        }
        onItemClick(view, object);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertDocumentLayout */
    public /* synthetic */ boolean m2437xac7496f6(View view, int position) {
        Object object;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter = this.listAdapter;
        if (adapter == listAdapter) {
            object = listAdapter.getItem(position);
        } else {
            object = this.searchAdapter.getItem(position);
        }
        return onItemClick(view, object);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlertDocumentLayout */
    public /* synthetic */ void m2438x3961ae15(View view, int position) {
        this.filtersView.cancelClickRunnables(true);
        this.searchAdapter.addSearchFilter(this.filtersView.getFilterAt(position));
    }

    private void runAnimation(final int animationType) {
        final float xTranslate;
        ValueAnimator valueAnimator = this.listAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.currentAnimationType = animationType;
        int listViewChildIndex = 0;
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            } else if (getChildAt(i) != this.listView) {
                i++;
            } else {
                listViewChildIndex = i;
                break;
            }
        }
        if (animationType == 1) {
            xTranslate = AndroidUtilities.dp(150.0f);
            this.backgroundListView.setAlpha(1.0f);
            this.backgroundListView.setScaleX(1.0f);
            this.backgroundListView.setScaleY(1.0f);
            this.backgroundListView.setTranslationX(0.0f);
            removeView(this.backgroundListView);
            addView(this.backgroundListView, listViewChildIndex);
            this.backgroundListView.setVisibility(0);
            this.listView.setTranslationX(xTranslate);
            this.listView.setAlpha(0.0f);
            this.listAnimation = ValueAnimator.ofFloat(1.0f, 0.0f);
        } else {
            xTranslate = AndroidUtilities.dp(150.0f);
            this.listView.setAlpha(0.0f);
            this.listView.setScaleX(0.95f);
            this.listView.setScaleY(0.95f);
            this.backgroundListView.setScaleX(1.0f);
            this.backgroundListView.setScaleY(1.0f);
            this.backgroundListView.setTranslationX(0.0f);
            this.backgroundListView.setAlpha(1.0f);
            removeView(this.backgroundListView);
            addView(this.backgroundListView, listViewChildIndex + 1);
            this.backgroundListView.setVisibility(0);
            this.listAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        }
        this.listAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ChatAttachAlertDocumentLayout.this.m2439xe59159a1(animationType, xTranslate, valueAnimator2);
            }
        });
        this.listAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.9
            {
                ChatAttachAlertDocumentLayout.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ChatAttachAlertDocumentLayout.this.backgroundListView.setVisibility(8);
                ChatAttachAlertDocumentLayout.this.currentAnimationType = 0;
                ChatAttachAlertDocumentLayout.this.listView.setAlpha(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setScaleX(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setScaleY(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setTranslationX(0.0f);
                ChatAttachAlertDocumentLayout.this.listView.invalidate();
            }
        });
        if (animationType == 1) {
            this.listAnimation.setDuration(220L);
        } else {
            this.listAnimation.setDuration(200L);
        }
        this.listAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.listAnimation.start();
    }

    /* renamed from: lambda$runAnimation$4$org-telegram-ui-Components-ChatAttachAlertDocumentLayout */
    public /* synthetic */ void m2439xe59159a1(int animationType, float xTranslate, ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        if (animationType == 1) {
            this.listView.setTranslationX(xTranslate * value);
            this.listView.setAlpha(1.0f - value);
            this.listView.invalidate();
            this.backgroundListView.setAlpha(value);
            float s = (0.05f * value) + 0.95f;
            this.backgroundListView.setScaleX(s);
            this.backgroundListView.setScaleY(s);
            return;
        }
        this.backgroundListView.setTranslationX(xTranslate * value);
        this.backgroundListView.setAlpha(Math.max(0.0f, 1.0f - value));
        this.backgroundListView.invalidate();
        this.listView.setAlpha(value);
        float s2 = (0.05f * value) + 0.95f;
        this.listView.setScaleX(s2);
        this.listView.setScaleY(s2);
        this.backgroundListView.invalidate();
    }

    private void prepareAnimation() {
        View childView;
        this.backgroundListAdapter.history.clear();
        this.backgroundListAdapter.history.addAll(this.listAdapter.history);
        this.backgroundListAdapter.items.clear();
        this.backgroundListAdapter.items.addAll(this.listAdapter.items);
        this.backgroundListAdapter.recentItems.clear();
        this.backgroundListAdapter.recentItems.addAll(this.listAdapter.recentItems);
        this.backgroundListAdapter.notifyDataSetChanged();
        this.backgroundListView.setVisibility(0);
        this.backgroundListView.setPadding(this.listView.getPaddingLeft(), this.listView.getPaddingTop(), this.listView.getPaddingRight(), this.listView.getPaddingBottom());
        int p = this.layoutManager.findFirstVisibleItemPosition();
        if (p >= 0 && (childView = this.layoutManager.findViewByPosition(p)) != null) {
            this.backgroundLayoutManager.scrollToPositionWithOffset(p, childView.getTop() - this.backgroundListView.getPaddingTop());
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onDestroy() {
        try {
            if (this.receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.parentAlert.actionBar.closeSearchField();
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        menu.removeView(this.sortItem);
        menu.removeView(this.searchItem);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onMenuItemClick(int id) {
        if (id == 6) {
            SharedConfig.toggleSortFilesByName();
            this.sortByName = SharedConfig.sortFilesByName;
            sortRecentItems();
            sortFileItems();
            this.listAdapter.notifyDataSetChanged();
            this.sortItem.setIcon(this.sortByName ? R.drawable.msg_contacts_time : R.drawable.msg_contacts_name);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int needsActionBar() {
        return 1;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        int newOffset = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = ((int) child.getY()) - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        return AndroidUtilities.dp(13.0f) + newOffset;
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(5.0f);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        int padding2;
        if (this.parentAlert.actionBar.isSearchFieldVisible() || this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = AndroidUtilities.dp(56.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                padding2 = (int) (availableHeight / 3.5f);
            } else {
                padding2 = (availableHeight / 5) * 2;
            }
            padding = padding2 - AndroidUtilities.dp(1.0f);
            if (padding < 0) {
                padding = 0;
            }
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != padding) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(48.0f));
            this.ignoreLayout = false;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.filtersView.getLayoutParams();
        layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getButtonsHideOffset() {
        return AndroidUtilities.dp(62.0f);
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getSelectedItemsCount() {
        return this.selectedFiles.size() + this.selectedMessages.size();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void sendSelectedItems(boolean notify, int scheduleDate) {
        if ((this.selectedFiles.size() == 0 && this.selectedMessages.size() == 0) || this.delegate == null || this.sendPressed) {
            return;
        }
        this.sendPressed = true;
        ArrayList<MessageObject> fmessages = new ArrayList<>();
        for (FilteredSearchView.MessageHashId hashId : this.selectedMessages.keySet()) {
            fmessages.add(this.selectedMessages.get(hashId));
        }
        ArrayList<String> files = new ArrayList<>(this.selectedFilesOrder);
        this.delegate.didSelectFiles(files, this.parentAlert.commentTextView.getText().toString(), fmessages, notify, scheduleDate);
        this.parentAlert.dismiss(true);
    }

    private boolean onItemClick(View view, Object object) {
        boolean add;
        if (object instanceof ListItem) {
            ListItem item = (ListItem) object;
            if (item.file == null || item.file.isDirectory()) {
                return false;
            }
            String path = item.file.getAbsolutePath();
            if (this.selectedFiles.containsKey(path)) {
                this.selectedFiles.remove(path);
                this.selectedFilesOrder.remove(path);
                add = false;
            } else if (!item.file.canRead()) {
                showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
                return false;
            } else if (this.canSelectOnlyImageFiles && item.thumb == null) {
                showErrorBox(LocaleController.formatString("PassportUploadNotImage", R.string.PassportUploadNotImage, new Object[0]));
                return false;
            } else if ((item.file.length() > FileLoader.DEFAULT_MAX_FILE_SIZE && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) || item.file.length() > FileLoader.DEFAULT_MAX_FILE_SIZE_PREMIUM) {
                LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this.parentAlert.baseFragment, this.parentAlert.getContainer().getContext(), 6, UserConfig.selectedAccount);
                limitReachedBottomSheet.setVeryLargeFile(true);
                limitReachedBottomSheet.show();
                return false;
            } else {
                if (this.maxSelectedFiles >= 0) {
                    int size = this.selectedFiles.size();
                    int i = this.maxSelectedFiles;
                    if (size >= i) {
                        showErrorBox(LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", i, new Object[0])));
                        return false;
                    }
                }
                if ((this.isSoundPicker && !isRingtone(item.file)) || item.file.length() == 0) {
                    return false;
                }
                this.selectedFiles.put(path, item);
                this.selectedFilesOrder.add(path);
                add = true;
            }
            this.scrolling = false;
        } else if (!(object instanceof MessageObject)) {
            return false;
        } else {
            MessageObject message = (MessageObject) object;
            FilteredSearchView.MessageHashId hashId = new FilteredSearchView.MessageHashId(message.getId(), message.getDialogId());
            if (this.selectedMessages.containsKey(hashId)) {
                this.selectedMessages.remove(hashId);
                add = false;
            } else if (this.selectedMessages.size() >= 100) {
                return false;
            } else {
                this.selectedMessages.put(hashId, message);
                add = true;
            }
        }
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(add, true);
        }
        this.parentAlert.updateCountButton(add ? 1 : 2);
        return true;
    }

    public boolean isRingtone(File file) {
        int millSecond;
        String mimeType = null;
        String extension = FileLoader.getFileExtension(file);
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (file.length() == 0 || mimeType == null || !RingtoneDataStore.ringtoneSupportedMimeType.contains(mimeType)) {
            BulletinFactory.of(this.parentAlert.getContainer(), null).createErrorBulletinSubtitle(LocaleController.formatString("InvalidFormatError", R.string.InvalidFormatError, new Object[0]), LocaleController.formatString("ErrorInvalidRingtone", R.string.ErrorRingtoneInvalidFormat, new Object[0]), null).show();
            return false;
        } else if (file.length() > MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax) {
            BulletinFactory.of(this.parentAlert.getContainer(), null).createErrorBulletinSubtitle(LocaleController.formatString("TooLargeError", R.string.TooLargeError, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", R.string.ErrorRingtoneSizeTooBig, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax / 1024)), null).show();
            return false;
        } else {
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(ApplicationLoader.applicationContext, Uri.fromFile(file));
                String durationStr = mmr.extractMetadata(9);
                millSecond = Integer.parseInt(durationStr);
            } catch (Exception e) {
                millSecond = Integer.MAX_VALUE;
            }
            if (millSecond <= MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax * 1000) {
                return true;
            }
            BulletinFactory.of(this.parentAlert.getContainer(), null).createErrorBulletinSubtitle(LocaleController.formatString("TooLongError", R.string.TooLongError, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", R.string.ErrorRingtoneDurationTooLong, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax)), null).show();
            return false;
        }
    }

    public void setMaxSelectedFiles(int value) {
        this.maxSelectedFiles = value;
    }

    public void setCanSelectOnlyImageFiles(boolean value) {
        this.canSelectOnlyImageFiles = value;
    }

    public void sendSelectedPhotos(HashMap<Object, Object> photos, ArrayList<Object> order, boolean notify, int scheduleDate) {
        if (photos.isEmpty() || this.delegate == null || this.sendPressed) {
            return;
        }
        this.sendPressed = true;
        ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
        for (int a = 0; a < order.size(); a++) {
            Object object = photos.get(order.get(a));
            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
            media.add(info);
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                if (photoEntry.imagePath != null) {
                    info.path = photoEntry.imagePath;
                } else {
                    info.path = photoEntry.path;
                }
                info.thumbPath = photoEntry.thumbPath;
                info.videoEditedInfo = photoEntry.editedInfo;
                info.isVideo = photoEntry.isVideo;
                info.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                info.entities = photoEntry.entities;
                info.masks = photoEntry.stickers;
                info.ttl = photoEntry.ttl;
            }
        }
        this.delegate.didSelectPhotos(media, notify, scheduleDate);
    }

    public void loadRecentFiles() {
        try {
            if (this.isSoundPicker) {
                int i = 2;
                String[] projection = {"_id", "_data", "duration", "_size", "mime_type"};
                try {
                    Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, "is_music != 0", null, "date_added DESC");
                    while (cursor.moveToNext()) {
                        File file = new File(cursor.getString(1));
                        long duration = cursor.getLong(i);
                        long fileSize = cursor.getLong(3);
                        String mimeType = cursor.getString(4);
                        if (duration > MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax * 1000 || fileSize > MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax) {
                            i = 2;
                        } else if (TextUtils.isEmpty(mimeType) || MimeTypes.AUDIO_MPEG.equals(mimeType) || !"audio/mpeg4".equals(mimeType)) {
                            ListItem item = new ListItem(null);
                            item.title = file.getName();
                            item.file = file;
                            String fname = file.getName();
                            String[] sp = fname.split("\\.");
                            item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                            item.subtitle = AndroidUtilities.formatFileSize(file.length());
                            String fname2 = fname.toLowerCase();
                            if (fname2.endsWith(".jpg") || fname2.endsWith(".png") || fname2.endsWith(".gif") || fname2.endsWith(".jpeg")) {
                                item.thumb = file.getAbsolutePath();
                            }
                            this.listAdapter.recentItems.add(item);
                            i = 2;
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                return;
            }
            checkDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            sortRecentItems();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    private void checkDirectory(File rootDir) {
        File[] files = rootDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().equals("Telegram")) {
                    checkDirectory(file);
                } else {
                    ListItem item = new ListItem(null);
                    item.title = file.getName();
                    item.file = file;
                    String fname = file.getName();
                    String[] sp = fname.split("\\.");
                    item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                    item.subtitle = AndroidUtilities.formatFileSize(file.length());
                    String fname2 = fname.toLowerCase();
                    if (fname2.endsWith(".jpg") || fname2.endsWith(".png") || fname2.endsWith(".gif") || fname2.endsWith(".jpeg")) {
                        item.thumb = file.getAbsolutePath();
                    }
                    this.listAdapter.recentItems.add(item);
                }
            }
        }
    }

    private void sortRecentItems() {
        Collections.sort(this.listAdapter.recentItems, new Comparator() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda3
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ChatAttachAlertDocumentLayout.this.m2441x1b4cac6c((ChatAttachAlertDocumentLayout.ListItem) obj, (ChatAttachAlertDocumentLayout.ListItem) obj2);
            }
        });
    }

    /* renamed from: lambda$sortRecentItems$5$org-telegram-ui-Components-ChatAttachAlertDocumentLayout */
    public /* synthetic */ int m2441x1b4cac6c(ListItem o1, ListItem o2) {
        if (this.sortByName) {
            return o1.file.getName().compareToIgnoreCase(o2.file.getName());
        }
        long lm = o1.file.lastModified();
        long rm = o2.file.lastModified();
        if (lm == rm) {
            return 0;
        }
        if (lm > rm) {
            return -1;
        }
        return 1;
    }

    private void sortFileItems() {
        if (this.currentDir == null) {
            return;
        }
        Collections.sort(this.listAdapter.items, new Comparator() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ChatAttachAlertDocumentLayout.this.m2440x6a58bd6c((ChatAttachAlertDocumentLayout.ListItem) obj, (ChatAttachAlertDocumentLayout.ListItem) obj2);
            }
        });
    }

    /* renamed from: lambda$sortFileItems$6$org-telegram-ui-Components-ChatAttachAlertDocumentLayout */
    public /* synthetic */ int m2440x6a58bd6c(ListItem lhs, ListItem rhs) {
        if (lhs.file == null) {
            return -1;
        }
        if (rhs.file == null) {
            return 1;
        }
        boolean isDir1 = lhs.file.isDirectory();
        boolean isDir2 = rhs.file.isDirectory();
        if (isDir1 != isDir2) {
            return isDir1 ? -1 : 1;
        } else if (isDir1 || this.sortByName) {
            return lhs.file.getName().compareToIgnoreCase(rhs.file.getName());
        } else {
            long lm = lhs.file.lastModified();
            long rm = rhs.file.lastModified();
            if (lm == rm) {
                return 0;
            }
            return lm > rm ? -1 : 1;
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        SearchAdapter searchAdapter = this.searchAdapter;
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.selectedFiles.clear();
        this.selectedMessages.clear();
        this.searchAdapter.currentSearchFilters.clear();
        this.selectedFilesOrder.clear();
        this.listAdapter.history.clear();
        listRoots();
        updateSearchButton();
        updateEmptyView();
        this.parentAlert.actionBar.setTitle(LocaleController.getString("SelectFile", R.string.SelectFile));
        this.sortItem.setVisibility(0);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHide() {
        this.sortItem.setVisibility(8);
        this.searchItem.setVisibility(8);
    }

    public void updateEmptyViewPosition() {
        View child;
        if (this.emptyView.getVisibility() != 0 || (child = this.listView.getChildAt(0)) == null) {
            return;
        }
        float oldTranslation = this.emptyView.getTranslationY();
        this.additionalTranslationY = ((this.emptyView.getMeasuredHeight() - getMeasuredHeight()) + child.getTop()) / 2;
        this.emptyView.setTranslationY(oldTranslation);
    }

    public void updateEmptyView() {
        boolean visible;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchAdapter;
        int i = 0;
        boolean z = true;
        if (adapter != searchAdapter) {
            if (this.listAdapter.getItemCount() != 1) {
                z = false;
            }
            visible = z;
        } else {
            if (!searchAdapter.searchResult.isEmpty() || !this.searchAdapter.sections.isEmpty()) {
                z = false;
            }
            visible = z;
        }
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (!visible) {
            i = 8;
        }
        stickerEmptyView.setVisibility(i);
        updateEmptyViewPosition();
    }

    public void updateSearchButton() {
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem == null || actionBarMenuItem.isSearchFieldVisible()) {
            return;
        }
        this.searchItem.setVisibility((this.hasFiles || this.listAdapter.history.isEmpty()) ? 0 : 8);
    }

    private int getTopForScroll() {
        View child = this.listView.getChildAt(0);
        RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(child);
        int top = -this.listView.getPaddingTop();
        if (holder != null && holder.getAdapterPosition() == 0) {
            return top + child.getTop();
        }
        return top;
    }

    private boolean canClosePicker() {
        if (this.listAdapter.history.size() > 0) {
            prepareAnimation();
            HistoryEntry he = (HistoryEntry) this.listAdapter.history.remove(this.listAdapter.history.size() - 1);
            this.parentAlert.actionBar.setTitle(he.title);
            int top = getTopForScroll();
            if (he.dir != null) {
                listFiles(he.dir);
            } else {
                listRoots();
            }
            updateSearchButton();
            this.layoutManager.scrollToPositionWithOffset(0, top);
            runAnimation(2);
            return false;
        }
        return true;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onBackPressed() {
        if (!canClosePicker()) {
            return true;
        }
        return super.onBackPressed();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateEmptyViewPosition();
    }

    public void setDelegate(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.delegate = documentSelectActivityDelegate;
    }

    public boolean listFiles(File dir) {
        this.hasFiles = false;
        if (!dir.canRead()) {
            if ((dir.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) || dir.getAbsolutePath().startsWith("/sdcard") || dir.getAbsolutePath().startsWith("/mnt/sdcard")) && !Environment.getExternalStorageState().equals("mounted") && !Environment.getExternalStorageState().equals("mounted_ro")) {
                this.currentDir = dir;
                this.listAdapter.items.clear();
                Environment.getExternalStorageState();
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
                return true;
            }
            showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
            return false;
        }
        try {
            File[] files = dir.listFiles();
            if (files == null) {
                showErrorBox(LocaleController.getString("UnknownError", R.string.UnknownError));
                return false;
            }
            this.currentDir = dir;
            this.listAdapter.items.clear();
            for (File file : files) {
                if (file.getName().indexOf(46) != 0) {
                    ListItem item = new ListItem(null);
                    item.title = file.getName();
                    item.file = file;
                    if (file.isDirectory()) {
                        item.icon = R.drawable.files_folder;
                        item.subtitle = LocaleController.getString("Folder", R.string.Folder);
                    } else {
                        this.hasFiles = true;
                        String fname = file.getName();
                        String[] sp = fname.split("\\.");
                        item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                        item.subtitle = AndroidUtilities.formatFileSize(file.length());
                        String fname2 = fname.toLowerCase();
                        if (fname2.endsWith(".jpg") || fname2.endsWith(".png") || fname2.endsWith(".gif") || fname2.endsWith(".jpeg")) {
                            item.thumb = file.getAbsolutePath();
                        }
                    }
                    this.listAdapter.items.add(item);
                }
            }
            ListItem item2 = new ListItem(null);
            item2.title = "..";
            if (this.listAdapter.history.size() <= 0) {
                item2.subtitle = LocaleController.getString("Folder", R.string.Folder);
            } else {
                HistoryEntry entry = (HistoryEntry) this.listAdapter.history.get(this.listAdapter.history.size() - 1);
                if (entry.dir == null) {
                    item2.subtitle = LocaleController.getString("Folder", R.string.Folder);
                } else {
                    item2.subtitle = entry.dir.toString();
                }
            }
            item2.icon = R.drawable.files_folder;
            item2.file = null;
            this.listAdapter.items.add(0, item2);
            sortFileItems();
            updateSearchButton();
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            int top = getTopForScroll();
            this.listAdapter.notifyDataSetChanged();
            this.layoutManager.scrollToPositionWithOffset(0, top);
            return true;
        } catch (Exception e) {
            showErrorBox(e.getLocalizedMessage());
            return false;
        }
    }

    private void showErrorBox(String error) {
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(error).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x0159 A[Catch: Exception -> 0x0192, all -> 0x01b8, TryCatch #4 {all -> 0x01b8, blocks: (B:16:0x0098, B:17:0x00a5, B:19:0x00ac, B:21:0x00b4, B:23:0x00bc, B:25:0x00c0, B:26:0x00c3, B:29:0x00dc, B:31:0x00e4, B:33:0x00ec, B:35:0x00f4, B:37:0x00fc, B:39:0x0104, B:41:0x010c, B:43:0x0117, B:45:0x0120, B:49:0x0145, B:50:0x0148, B:52:0x0159, B:53:0x0163, B:54:0x0171, B:56:0x017d, B:60:0x0196, B:70:0x01bd), top: B:98:0x0098, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0163 A[Catch: Exception -> 0x0192, all -> 0x01b8, TryCatch #4 {all -> 0x01b8, blocks: (B:16:0x0098, B:17:0x00a5, B:19:0x00ac, B:21:0x00b4, B:23:0x00bc, B:25:0x00c0, B:26:0x00c3, B:29:0x00dc, B:31:0x00e4, B:33:0x00ec, B:35:0x00f4, B:37:0x00fc, B:39:0x0104, B:41:0x010c, B:43:0x0117, B:45:0x0120, B:49:0x0145, B:50:0x0148, B:52:0x0159, B:53:0x0163, B:54:0x0171, B:56:0x017d, B:60:0x0196, B:70:0x01bd), top: B:98:0x0098, inners: #2 }] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:66:0x01b2 -> B:100:0x01c6). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void listRoots() {
        /*
            Method dump skipped, instructions count: 647
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.listRoots():void");
    }

    private String getRootSubtitle(String path) {
        try {
            StatFs stat = new StatFs(path);
            long total = stat.getBlockCount() * stat.getBlockSize();
            long free = stat.getAvailableBlocks() * stat.getBlockSize();
            if (total == 0) {
                return "";
            }
            return LocaleController.formatString("FreeOfTotal", R.string.FreeOfTotal, AndroidUtilities.formatFileSize(free), AndroidUtilities.formatFileSize(total));
        } catch (Exception e) {
            FileLog.e(e);
            return path;
        }
    }

    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<ListItem> items = new ArrayList<>();
        private ArrayList<HistoryEntry> history = new ArrayList<>();
        private ArrayList<ListItem> recentItems = new ArrayList<>();

        public ListAdapter(Context context) {
            ChatAttachAlertDocumentLayout.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count = this.items.size();
            if (this.history.isEmpty() && !this.recentItems.isEmpty()) {
                count += this.recentItems.size() + 2;
            }
            return count + 1;
        }

        public ListItem getItem(int position) {
            int position2;
            int itemsSize = this.items.size();
            if (position < itemsSize) {
                return this.items.get(position);
            }
            if (this.history.isEmpty() && !this.recentItems.isEmpty() && position != itemsSize && position != itemsSize + 1 && (position2 = position - (this.items.size() + 2)) < this.recentItems.size()) {
                return this.recentItems.get(position2);
            }
            return null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return 3;
            }
            int itemsSize = this.items.size();
            if (position == itemsSize) {
                return 2;
            }
            return position == itemsSize + 1 ? 0 : 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                    break;
                case 1:
                    view = new SharedDocumentCell(this.mContext, 1, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                    break;
                case 2:
                    view = new ShadowSectionCell(this.mContext);
                    Drawable drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(ChatAttachAlertDocumentLayout.this.getThemedColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (ChatAttachAlertDocumentLayout.this.sortByName) {
                        headerCell.setText(LocaleController.getString("RecentFilesAZ", R.string.RecentFilesAZ));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString("RecentFiles", R.string.RecentFiles));
                        return;
                    }
                case 1:
                    ListItem item = getItem(position);
                    SharedDocumentCell documentCell = (SharedDocumentCell) holder.itemView;
                    if (item.icon != 0) {
                        documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, null, null, item.icon, position != this.items.size() - 1);
                    } else {
                        String type = item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4));
                        documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, type, item.thumb, 0, false);
                    }
                    if (item.file != null) {
                        documentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(item.file.toString()), !ChatAttachAlertDocumentLayout.this.scrolling);
                        return;
                    } else {
                        documentCell.setChecked(false, !ChatAttachAlertDocumentLayout.this.scrolling);
                        return;
                    }
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.updateEmptyView();
        }
    }

    /* loaded from: classes5.dex */
    public class SearchAdapter extends RecyclerListView.SectionsAdapter {
        private String currentDataQuery;
        private long currentSearchDialogId;
        private FiltersView.MediaFilterData currentSearchFilter;
        private long currentSearchMaxDate;
        private long currentSearchMinDate;
        private boolean endReached;
        private boolean isLoading;
        private String lastMessagesSearchString;
        private String lastSearchFilterQueryString;
        private Runnable localSearchRunnable;
        private Context mContext;
        private int nextSearchRate;
        private int requestIndex;
        private int searchIndex;
        private Runnable searchRunnable;
        private ArrayList<ListItem> searchResult = new ArrayList<>();
        private final FilteredSearchView.MessageHashId messageHashIdTmp = new FilteredSearchView.MessageHashId(0, 0);
        private ArrayList<Object> localTipChats = new ArrayList<>();
        private ArrayList<FiltersView.DateData> localTipDates = new ArrayList<>();
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public SparseArray<MessageObject> messagesById = new SparseArray<>();
        public ArrayList<String> sections = new ArrayList<>();
        public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
        private ArrayList<FiltersView.MediaFilterData> currentSearchFilters = new ArrayList<>();
        private boolean firstLoading = true;
        private int animationIndex = -1;
        private Runnable clearCurrentResultsRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.1
            {
                SearchAdapter.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (SearchAdapter.this.isLoading) {
                    SearchAdapter.this.messages.clear();
                    SearchAdapter.this.sections.clear();
                    SearchAdapter.this.sectionArrays.clear();
                    SearchAdapter.this.notifyDataSetChanged();
                }
            }
        };

        public SearchAdapter(Context context) {
            ChatAttachAlertDocumentLayout.this = this$0;
            this.mContext = context;
        }

        public void search(final String query, boolean reset) {
            Runnable runnable = this.localSearchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.localSearchRunnable = null;
            }
            if (!TextUtils.isEmpty(query)) {
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertDocumentLayout.SearchAdapter.this.m2444xbcebf2b8(query);
                    }
                };
                this.localSearchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
            } else {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
                }
                notifyDataSetChanged();
            }
            if (!ChatAttachAlertDocumentLayout.this.canSelectOnlyImageFiles && ChatAttachAlertDocumentLayout.this.listAdapter.history.isEmpty()) {
                long dialogId = 0;
                long minDate = 0;
                long maxDate = 0;
                for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                    FiltersView.MediaFilterData data = this.currentSearchFilters.get(i);
                    if (data.filterType == 4) {
                        if (data.chat instanceof TLRPC.User) {
                            dialogId = ((TLRPC.User) data.chat).id;
                        } else if (data.chat instanceof TLRPC.Chat) {
                            dialogId = -((TLRPC.Chat) data.chat).id;
                        }
                    } else if (data.filterType == 6) {
                        long minDate2 = data.dateData.minDate;
                        minDate = minDate2;
                        maxDate = data.dateData.maxDate;
                    }
                }
                searchGlobal(dialogId, minDate, maxDate, FiltersView.filters[2], query, reset);
            }
        }

        /* renamed from: lambda$search$1$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter */
        public /* synthetic */ void m2444xbcebf2b8(final String query) {
            final ArrayList<ListItem> copy = new ArrayList<>(ChatAttachAlertDocumentLayout.this.listAdapter.items);
            if (ChatAttachAlertDocumentLayout.this.listAdapter.history.isEmpty()) {
                copy.addAll(0, ChatAttachAlertDocumentLayout.this.listAdapter.recentItems);
            }
            final boolean hasFilters = !this.currentSearchFilters.isEmpty();
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.m2443x3aa13dd9(query, hasFilters, copy);
                }
            });
        }

        /* renamed from: lambda$search$0$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter */
        public /* synthetic */ void m2443x3aa13dd9(String query, boolean hasFilters, ArrayList copy) {
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList<>(), query);
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[(search2 != null ? 1 : 0) + 1];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<ListItem> resultArray = new ArrayList<>();
            if (!hasFilters) {
                for (int a = 0; a < copy.size(); a++) {
                    ListItem entry = (ListItem) copy.get(a);
                    if (entry.file != null && !entry.file.isDirectory()) {
                        int b = 0;
                        while (true) {
                            if (b < search.length) {
                                String q = search[b];
                                boolean ok = false;
                                if (entry.title != null) {
                                    ok = entry.title.toLowerCase().contains(q);
                                }
                                if (!ok) {
                                    b++;
                                } else {
                                    resultArray.add(entry);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            updateSearchResults(resultArray, query);
        }

        public void loadMore() {
            FiltersView.MediaFilterData mediaFilterData;
            if (ChatAttachAlertDocumentLayout.this.searchAdapter.isLoading || ChatAttachAlertDocumentLayout.this.searchAdapter.endReached || (mediaFilterData = this.currentSearchFilter) == null) {
                return;
            }
            searchGlobal(this.currentSearchDialogId, this.currentSearchMinDate, this.currentSearchMaxDate, mediaFilterData, this.lastMessagesSearchString, false);
        }

        public void removeSearchFilter(FiltersView.MediaFilterData filterData) {
            this.currentSearchFilters.remove(filterData);
        }

        public void clear() {
            this.currentSearchFilters.clear();
        }

        public void addSearchFilter(FiltersView.MediaFilterData filter) {
            if (!this.currentSearchFilters.isEmpty()) {
                for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                    if (filter.isSameType(this.currentSearchFilters.get(i))) {
                        return;
                    }
                }
            }
            this.currentSearchFilters.add(filter);
            ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.setSearchFilter(filter);
            ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.setSearchFieldText("");
            updateFiltersView(true, null, null, true);
        }

        public void updateFiltersView(boolean showMediaFilters, ArrayList<Object> users, ArrayList<FiltersView.DateData> dates, boolean animated) {
            int i;
            boolean hasMediaFilter = false;
            boolean hasUserFilter = false;
            boolean hasDataFilter = false;
            int i2 = 0;
            while (true) {
                i = 4;
                if (i2 >= this.currentSearchFilters.size()) {
                    break;
                }
                if (this.currentSearchFilters.get(i2).isMedia()) {
                    hasMediaFilter = true;
                } else if (this.currentSearchFilters.get(i2).filterType == 4) {
                    hasUserFilter = true;
                } else if (this.currentSearchFilters.get(i2).filterType == 6) {
                    hasDataFilter = true;
                }
                i2++;
            }
            boolean visible = false;
            boolean hasUsersOrDates = (users != null && !users.isEmpty()) || (dates != null && !dates.isEmpty());
            Integer num = null;
            if ((hasMediaFilter || hasUsersOrDates || !showMediaFilters) && hasUsersOrDates) {
                ArrayList<Object> finalUsers = (users == null || users.isEmpty() || hasUserFilter) ? null : users;
                ArrayList<FiltersView.DateData> finalDates = (dates == null || dates.isEmpty() || hasDataFilter) ? null : dates;
                if (finalUsers != null || finalDates != null) {
                    visible = true;
                    ChatAttachAlertDocumentLayout.this.filtersView.setUsersAndDates(finalUsers, finalDates, false);
                }
            }
            if (!visible) {
                ChatAttachAlertDocumentLayout.this.filtersView.setUsersAndDates(null, null, false);
            }
            ChatAttachAlertDocumentLayout.this.filtersView.setEnabled(visible);
            if (!visible || ChatAttachAlertDocumentLayout.this.filtersView.getTag() == null) {
                if (visible || ChatAttachAlertDocumentLayout.this.filtersView.getTag() != null) {
                    FiltersView filtersView = ChatAttachAlertDocumentLayout.this.filtersView;
                    if (visible) {
                        num = 1;
                    }
                    filtersView.setTag(num);
                    if (ChatAttachAlertDocumentLayout.this.filtersViewAnimator != null) {
                        ChatAttachAlertDocumentLayout.this.filtersViewAnimator.cancel();
                    }
                    if (!animated) {
                        ChatAttachAlertDocumentLayout.this.filtersView.getAdapter().notifyDataSetChanged();
                        ChatAttachAlertDocumentLayout.this.listView.setTranslationY(visible ? AndroidUtilities.dp(44.0f) : 0.0f);
                        ChatAttachAlertDocumentLayout.this.filtersView.setTranslationY(visible ? 0.0f : -AndroidUtilities.dp(44.0f));
                        ChatAttachAlertDocumentLayout.this.loadingView.setTranslationY(visible ? AndroidUtilities.dp(44.0f) : 0.0f);
                        ChatAttachAlertDocumentLayout.this.emptyView.setTranslationY(visible ? AndroidUtilities.dp(44.0f) : 0.0f);
                        FiltersView filtersView2 = ChatAttachAlertDocumentLayout.this.filtersView;
                        if (visible) {
                            i = 0;
                        }
                        filtersView2.setVisibility(i);
                        return;
                    }
                    if (visible) {
                        ChatAttachAlertDocumentLayout.this.filtersView.setVisibility(0);
                    }
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator = new AnimatorSet();
                    AnimatorSet animatorSet = ChatAttachAlertDocumentLayout.this.filtersViewAnimator;
                    Animator[] animatorArr = new Animator[4];
                    RecyclerListView recyclerListView = ChatAttachAlertDocumentLayout.this.listView;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    fArr[0] = visible ? AndroidUtilities.dp(44.0f) : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(recyclerListView, property, fArr);
                    FiltersView filtersView3 = ChatAttachAlertDocumentLayout.this.filtersView;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    fArr2[0] = visible ? 0.0f : -AndroidUtilities.dp(44.0f);
                    animatorArr[1] = ObjectAnimator.ofFloat(filtersView3, property2, fArr2);
                    FlickerLoadingView flickerLoadingView = ChatAttachAlertDocumentLayout.this.loadingView;
                    Property property3 = View.TRANSLATION_Y;
                    float[] fArr3 = new float[1];
                    fArr3[0] = visible ? AndroidUtilities.dp(44.0f) : 0.0f;
                    animatorArr[2] = ObjectAnimator.ofFloat(flickerLoadingView, property3, fArr3);
                    StickerEmptyView stickerEmptyView = ChatAttachAlertDocumentLayout.this.emptyView;
                    Property property4 = View.TRANSLATION_Y;
                    float[] fArr4 = new float[1];
                    fArr4[0] = visible ? AndroidUtilities.dp(44.0f) : 0.0f;
                    animatorArr[3] = ObjectAnimator.ofFloat(stickerEmptyView, property4, fArr4);
                    animatorSet.playTogether(animatorArr);
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.2
                        {
                            SearchAdapter.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (ChatAttachAlertDocumentLayout.this.filtersView.getTag() == null) {
                                ChatAttachAlertDocumentLayout.this.filtersView.setVisibility(4);
                            }
                            ChatAttachAlertDocumentLayout.this.filtersViewAnimator = null;
                        }
                    });
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.setDuration(180L);
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.start();
                }
            }
        }

        private void searchGlobal(final long dialogId, final long minDate, final long maxDate, FiltersView.MediaFilterData searchFilter, final String query, boolean clearOldResults) {
            final String currentSearchFilterQueryString = String.format(Locale.ENGLISH, "%d%d%d%d%s", Long.valueOf(dialogId), Long.valueOf(minDate), Long.valueOf(maxDate), Integer.valueOf(searchFilter.filterType), query);
            String str = this.lastSearchFilterQueryString;
            final boolean filterAndQueryIsSame = str != null && str.equals(currentSearchFilterQueryString);
            boolean forceClear = !filterAndQueryIsSame && clearOldResults;
            boolean z = dialogId == this.currentSearchDialogId && this.currentSearchMinDate == minDate && this.currentSearchMaxDate == maxDate;
            this.currentSearchFilter = searchFilter;
            this.currentSearchDialogId = dialogId;
            this.currentSearchMinDate = minDate;
            this.currentSearchMaxDate = maxDate;
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            AndroidUtilities.cancelRunOnUIThread(this.clearCurrentResultsRunnable);
            if (filterAndQueryIsSame && clearOldResults) {
                return;
            }
            if (forceClear) {
                this.messages.clear();
                this.sections.clear();
                this.sectionArrays.clear();
                this.isLoading = true;
                ChatAttachAlertDocumentLayout.this.emptyView.setVisibility(0);
                notifyDataSetChanged();
                this.requestIndex++;
                this.firstLoading = true;
                if (ChatAttachAlertDocumentLayout.this.listView.getPinnedHeader() != null) {
                    ChatAttachAlertDocumentLayout.this.listView.getPinnedHeader().setAlpha(0.0f);
                }
                this.localTipChats.clear();
                this.localTipDates.clear();
            }
            this.isLoading = true;
            notifyDataSetChanged();
            if (!filterAndQueryIsSame) {
                this.clearCurrentResultsRunnable.run();
                ChatAttachAlertDocumentLayout.this.emptyView.showProgress(true, !clearOldResults);
            }
            if (!TextUtils.isEmpty(query)) {
                this.requestIndex++;
                final int requestId = this.requestIndex;
                final AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertDocumentLayout.SearchAdapter.this.m2447x66d53cb2(dialogId, query, accountInstance, minDate, maxDate, filterAndQueryIsSame, currentSearchFilterQueryString, requestId);
                    }
                };
                this.searchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, (!filterAndQueryIsSame || this.messages.isEmpty()) ? 350L : 0L);
                ChatAttachAlertDocumentLayout.this.loadingView.setViewType(3);
                return;
            }
            this.localTipDates.clear();
            this.localTipChats.clear();
            updateFiltersView(false, null, null, true);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* renamed from: lambda$searchGlobal$4$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter */
        public /* synthetic */ void m2447x66d53cb2(final long dialogId, final String query, final AccountInstance accountInstance, final long minDate, long maxDate, final boolean filterAndQueryIsSame, String currentSearchFilterQueryString, final int requestId) {
            ArrayList<Object> resultArray;
            TLObject request;
            ArrayList<MessageObject> arrayList;
            long id;
            ArrayList<MessageObject> arrayList2;
            ArrayList<Object> resultArray2 = null;
            if (dialogId != 0) {
                TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
                req.q = query;
                req.limit = 20;
                req.filter = this.currentSearchFilter.filter;
                req.peer = accountInstance.getMessagesController().getInputPeer(dialogId);
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
                    accountInstance.getMessagesStorage().localSearch(0, query, resultArray3, resultArrayNames, encUsers, -1);
                    resultArray2 = resultArray3;
                }
                TLRPC.TL_messages_searchGlobal req2 = new TLRPC.TL_messages_searchGlobal();
                req2.limit = 20;
                req2.q = query;
                req2.filter = this.currentSearchFilter.filter;
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
                    if (lastMessage.messageOwner.peer_id.channel_id != 0) {
                        id = -lastMessage.messageOwner.peer_id.channel_id;
                    } else if (lastMessage.messageOwner.peer_id.chat_id != 0) {
                        id = -lastMessage.messageOwner.peer_id.chat_id;
                    } else {
                        id = lastMessage.messageOwner.peer_id.user_id;
                    }
                    req2.offset_peer = accountInstance.getMessagesController().getInputPeer(id);
                } else {
                    req2.offset_rate = 0;
                    req2.offset_id = 0;
                    req2.offset_peer = new TLRPC.TL_inputPeerEmpty();
                }
                resultArray = resultArray2;
                request = req2;
            }
            this.lastMessagesSearchString = query;
            this.lastSearchFilterQueryString = currentSearchFilterQueryString;
            final ArrayList<Object> finalResultArray = resultArray;
            final ArrayList<FiltersView.DateData> dateData = new ArrayList<>();
            FiltersView.fillTipDates(this.lastMessagesSearchString, dateData);
            accountInstance.getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.m2446xe48a87d3(accountInstance, query, requestId, filterAndQueryIsSame, dialogId, minDate, finalResultArray, dateData, tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$searchGlobal$3$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter */
        public /* synthetic */ void m2446xe48a87d3(final AccountInstance accountInstance, final String query, final int requestId, final boolean filterAndQueryIsSame, final long dialogId, final long minDate, final ArrayList finalResultArray, final ArrayList dateData, final TLObject response, final TLRPC.TL_error error) {
            final ArrayList<MessageObject> messageObjects = new ArrayList<>();
            if (error == null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                int n = res.messages.size();
                for (int i = 0; i < n; i++) {
                    MessageObject messageObject = new MessageObject(accountInstance.getCurrentAccount(), res.messages.get(i), false, true);
                    messageObject.setQuery(query);
                    messageObjects.add(messageObject);
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.m2445x623fd2f4(requestId, error, response, accountInstance, filterAndQueryIsSame, query, messageObjects, dialogId, minDate, finalResultArray, dateData);
                }
            });
        }

        /* renamed from: lambda$searchGlobal$2$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter */
        public /* synthetic */ void m2445x623fd2f4(int requestId, TLRPC.TL_error error, TLObject response, final AccountInstance accountInstance, boolean filterAndQueryIsSame, String query, ArrayList messageObjects, long dialogId, long minDate, ArrayList finalResultArray, ArrayList dateData) {
            if (requestId == this.requestIndex) {
                this.isLoading = false;
                if (error != null) {
                    ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", R.string.SearchEmptyViewTitle2));
                    ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                    ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
                    ChatAttachAlertDocumentLayout.this.emptyView.showProgress(false, true);
                    return;
                }
                ChatAttachAlertDocumentLayout.this.emptyView.showProgress(false);
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                this.nextSearchRate = res.next_rate;
                accountInstance.getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                accountInstance.getMessagesController().putUsers(res.users, false);
                accountInstance.getMessagesController().putChats(res.chats, false);
                if (!filterAndQueryIsSame) {
                    this.messages.clear();
                    this.messagesById.clear();
                    this.sections.clear();
                    this.sectionArrays.clear();
                }
                int totalCount = res.count;
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
                }
                if (this.messages.size() > totalCount) {
                    totalCount = this.messages.size();
                }
                this.endReached = this.messages.size() >= totalCount;
                if (this.messages.isEmpty()) {
                    if (!TextUtils.isEmpty(this.currentDataQuery) || dialogId != 0 || minDate != 0) {
                        ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", R.string.SearchEmptyViewTitle2));
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
                    } else {
                        ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle", R.string.SearchEmptyViewTitle));
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitleFiles", R.string.SearchEmptyViewFilteredSubtitleFiles));
                    }
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
                            if (i2 >= this.localTipChats.size()) {
                                break;
                            } else if (!(this.localTipChats.get(i2) instanceof TLRPC.User) || UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id != ((TLRPC.User) this.localTipChats.get(i2)).id) {
                                i2++;
                            } else {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            this.localTipChats.add(0, UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser());
                        }
                    }
                    this.localTipDates.clear();
                    this.localTipDates.addAll(dateData);
                    updateFiltersView(TextUtils.isEmpty(this.currentDataQuery), this.localTipChats, this.localTipDates, true);
                }
                this.firstLoading = false;
                View progressView = null;
                int progressViewPosition = -1;
                for (int i3 = 0; i3 < n; i3++) {
                    View child = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i3);
                    if (child instanceof FlickerLoadingView) {
                        progressView = child;
                        progressViewPosition = ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(child);
                    }
                }
                final View finalProgressView = progressView;
                if (progressView != null) {
                    ChatAttachAlertDocumentLayout.this.listView.removeView(progressView);
                }
                if ((ChatAttachAlertDocumentLayout.this.loadingView.getVisibility() == 0 && ChatAttachAlertDocumentLayout.this.listView.getChildCount() <= 1) || progressView != null) {
                    final int finalProgressViewPosition = progressViewPosition;
                    ChatAttachAlertDocumentLayout.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.3
                        {
                            SearchAdapter.this = this;
                        }

                        @Override // android.view.ViewTreeObserver.OnPreDrawListener
                        public boolean onPreDraw() {
                            ChatAttachAlertDocumentLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                            int n2 = ChatAttachAlertDocumentLayout.this.listView.getChildCount();
                            AnimatorSet animatorSet = new AnimatorSet();
                            for (int i4 = 0; i4 < n2; i4++) {
                                View child2 = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i4);
                                if (finalProgressView == null || ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(child2) >= finalProgressViewPosition) {
                                    child2.setAlpha(0.0f);
                                    int s = Math.min(ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight(), Math.max(0, child2.getTop()));
                                    int delay = (int) ((s / ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight()) * 100.0f);
                                    ObjectAnimator a = ObjectAnimator.ofFloat(child2, View.ALPHA, 0.0f, 1.0f);
                                    a.setStartDelay(delay);
                                    a.setDuration(200L);
                                    animatorSet.playTogether(a);
                                }
                            }
                            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.3.1
                                {
                                    AnonymousClass3.this = this;
                                }

                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    accountInstance.getNotificationCenter().onAnimationFinish(SearchAdapter.this.animationIndex);
                                }
                            });
                            SearchAdapter.this.animationIndex = accountInstance.getNotificationCenter().setAnimationInProgress(SearchAdapter.this.animationIndex, null);
                            animatorSet.start();
                            View view = finalProgressView;
                            if (view != null && view.getParent() == null) {
                                ChatAttachAlertDocumentLayout.this.listView.addView(finalProgressView);
                                final RecyclerView.LayoutManager layoutManager = ChatAttachAlertDocumentLayout.this.listView.getLayoutManager();
                                if (layoutManager != null) {
                                    layoutManager.ignoreView(finalProgressView);
                                    Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, finalProgressView.getAlpha(), 0.0f);
                                    animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.3.2
                                        {
                                            AnonymousClass3.this = this;
                                        }

                                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                        public void onAnimationEnd(Animator animation) {
                                            finalProgressView.setAlpha(1.0f);
                                            layoutManager.stopIgnoringView(finalProgressView);
                                            ChatAttachAlertDocumentLayout.this.listView.removeView(finalProgressView);
                                        }
                                    });
                                    animator.start();
                                }
                            }
                            return true;
                        }
                    });
                }
                notifyDataSetChanged();
            }
        }

        private void updateSearchResults(final ArrayList<ListItem> result, String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.m2448xe95e5031(result);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$5$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter */
        public /* synthetic */ void m2448xe95e5031(ArrayList result) {
            if (ChatAttachAlertDocumentLayout.this.searching && ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.searchAdapter) {
                ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.searchAdapter);
            }
            this.searchResult = result;
            notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            int type = holder.getItemViewType();
            return type == 1 || type == 4;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getSectionCount() {
            if (!this.sections.isEmpty()) {
                int count = 2 + this.sections.size() + (!this.endReached ? 1 : 0);
                return count;
            }
            return 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public Object getItem(int section, int position) {
            ArrayList<MessageObject> arrayList;
            if (section == 0) {
                if (position < this.searchResult.size()) {
                    return this.searchResult.get(position);
                }
                return null;
            }
            int section2 = section - 1;
            if (section2 >= this.sections.size() || (arrayList = this.sectionArrays.get(this.sections.get(section2))) == null) {
                return null;
            }
            return arrayList.get(position - ((section2 != 0 || !this.searchResult.isEmpty()) ? 1 : 0));
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getCountForSection(int section) {
            if (section == 0) {
                return this.searchResult.size();
            }
            int section2 = section - 1;
            int i = 1;
            if (section2 >= this.sections.size()) {
                return 1;
            }
            ArrayList<MessageObject> arrayList = this.sectionArrays.get(this.sections.get(section2));
            if (arrayList == null) {
                return 0;
            }
            int size = arrayList.size();
            if (section2 == 0 && this.searchResult.isEmpty()) {
                i = 0;
            }
            return size + i;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public View getSectionHeaderView(int section, View view) {
            String str;
            GraySectionCell sectionCell = (GraySectionCell) view;
            if (sectionCell == null) {
                sectionCell = new GraySectionCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                sectionCell.setBackgroundColor(ChatAttachAlertDocumentLayout.this.getThemedColor(Theme.key_graySection) & (-218103809));
            }
            if (section == 0 || (section == 1 && this.searchResult.isEmpty())) {
                sectionCell.setAlpha(0.0f);
                return sectionCell;
            }
            int section2 = section - 1;
            if (section2 < this.sections.size()) {
                sectionCell.setAlpha(1.0f);
                String name = this.sections.get(section2);
                ArrayList<MessageObject> messageObjects = this.sectionArrays.get(name);
                if (messageObjects != null) {
                    MessageObject messageObject = messageObjects.get(0);
                    if (section2 == 0 && !this.searchResult.isEmpty()) {
                        str = LocaleController.getString("GlobalSearch", R.string.GlobalSearch);
                    } else {
                        str = LocaleController.formatSectionDate(messageObject.messageOwner.date);
                    }
                    sectionCell.setText(str);
                }
            }
            return view;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int i = 1;
            switch (viewType) {
                case 0:
                    view = new GraySectionCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                    break;
                case 1:
                case 4:
                    Context context = this.mContext;
                    if (viewType != 1) {
                        i = 2;
                    }
                    SharedDocumentCell documentCell = new SharedDocumentCell(context, i, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                    documentCell.setDrawDownloadIcon(false);
                    view = documentCell;
                    break;
                case 2:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                    flickerLoadingView.setViewType(3);
                    flickerLoadingView.setIsSingleCell(true);
                    view = flickerLoadingView;
                    break;
                case 3:
                default:
                    view = new View(this.mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            String str;
            int position2 = position;
            int viewType = holder.getItemViewType();
            if (viewType == 2 || viewType == 3) {
                return;
            }
            boolean z = false;
            switch (viewType) {
                case 0:
                    int section2 = section - 1;
                    String name = this.sections.get(section2);
                    ArrayList<MessageObject> messageObjects = this.sectionArrays.get(name);
                    if (messageObjects == null) {
                        return;
                    }
                    MessageObject messageObject = messageObjects.get(0);
                    if (section2 == 0 && !this.searchResult.isEmpty()) {
                        str = LocaleController.getString("GlobalSearch", R.string.GlobalSearch);
                    } else {
                        str = LocaleController.formatSectionDate(messageObject.messageOwner.date);
                    }
                    ((GraySectionCell) holder.itemView).setText(str);
                    return;
                case 1:
                case 4:
                    final SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                    if (section == 0) {
                        ListItem item = (ListItem) getItem(position2);
                        SharedDocumentCell documentCell = (SharedDocumentCell) holder.itemView;
                        if (item.icon == 0) {
                            String type = item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4));
                            documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, type, item.thumb, 0, false);
                        } else {
                            documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, null, null, item.icon, false);
                        }
                        if (item.file == null) {
                            documentCell.setChecked(false, true ^ ChatAttachAlertDocumentLayout.this.scrolling);
                            break;
                        } else {
                            documentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(item.file.toString()), true ^ ChatAttachAlertDocumentLayout.this.scrolling);
                            break;
                        }
                    } else {
                        int section3 = section - 1;
                        if (section3 != 0 || !this.searchResult.isEmpty()) {
                            position2--;
                        }
                        String name2 = this.sections.get(section3);
                        ArrayList<MessageObject> messageObjects2 = this.sectionArrays.get(name2);
                        if (messageObjects2 == null) {
                            return;
                        }
                        final MessageObject messageObject2 = messageObjects2.get(position2);
                        final boolean animated = sharedDocumentCell.getMessage() != null && sharedDocumentCell.getMessage().getId() == messageObject2.getId();
                        if (position2 != messageObjects2.size() - 1 || (section3 == this.sections.size() - 1 && this.isLoading)) {
                            z = true;
                        }
                        sharedDocumentCell.setDocument(messageObject2, z);
                        sharedDocumentCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.4
                            {
                                SearchAdapter.this = this;
                            }

                            @Override // android.view.ViewTreeObserver.OnPreDrawListener
                            public boolean onPreDraw() {
                                sharedDocumentCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                if (ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.isActionModeShowed()) {
                                    SearchAdapter.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                                    sharedDocumentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedMessages.containsKey(SearchAdapter.this.messageHashIdTmp), animated);
                                    return true;
                                }
                                sharedDocumentCell.setChecked(false, animated);
                                return true;
                            }
                        });
                        return;
                    }
                    break;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int section, int position) {
            if (section == 0) {
                return 1;
            }
            if (section == getSectionCount() - 1) {
                return 3;
            }
            int section2 = section - 1;
            if (section2 < this.sections.size()) {
                if ((section2 != 0 || !this.searchResult.isEmpty()) && position == 0) {
                    return 0;
                }
                return 4;
            }
            return 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.updateEmptyView();
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

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIconBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_iconText));
        return themeDescriptions;
    }
}
