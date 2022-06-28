package org.telegram.ui.Delegates;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MemberRequestsController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AvatarPreviewPagerIndicator;
import org.telegram.ui.Cells.MemberRequestCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Delegates.MemberRequestsDelegate;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes5.dex */
public class MemberRequestsDelegate implements MemberRequestCell.OnClickListener {
    private final long chatId;
    private final MemberRequestsController controller;
    private final int currentAccount;
    private StickerEmptyView emptyView;
    private final BaseFragment fragment;
    private boolean hasMore;
    private TLRPC.TL_chatInviteImporter importer;
    public final boolean isChannel;
    private boolean isDataLoaded;
    private boolean isLoading;
    public boolean isNeedRestoreList;
    private boolean isSearchExpanded;
    private final FrameLayout layoutContainer;
    private FlickerLoadingView loadingView;
    private PreviewDialog previewDialog;
    private String query;
    private RecyclerListView recyclerView;
    private FrameLayout rootLayout;
    private StickerEmptyView searchEmptyView;
    private int searchRequestId;
    private Runnable searchRunnable;
    private final boolean showSearchMenu;
    private final List<TLRPC.TL_chatInviteImporter> currentImporters = new ArrayList();
    private final LongSparseArray<TLRPC.User> users = new LongSparseArray<>();
    private final ArrayList<TLRPC.TL_chatInviteImporter> allImporters = new ArrayList<>();
    private final Adapter adapter = new Adapter();
    private boolean isFirstLoading = true;
    private boolean isShowLastItemDivider = true;
    private final RecyclerView.OnScrollListener listScrollListener = new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate.2
        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (MemberRequestsDelegate.this.hasMore && !MemberRequestsDelegate.this.isLoading && layoutManager != null) {
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (MemberRequestsDelegate.this.adapter.getItemCount() - lastPosition < 10) {
                    MemberRequestsDelegate.this.loadMembers();
                }
            }
        }
    };

    public MemberRequestsDelegate(BaseFragment fragment, FrameLayout layoutContainer, long chatId, boolean showSearchMenu) {
        this.fragment = fragment;
        this.layoutContainer = layoutContainer;
        this.chatId = chatId;
        int currentAccount = fragment.getCurrentAccount();
        this.currentAccount = currentAccount;
        this.isChannel = ChatObject.isChannelAndNotMegaGroup(chatId, currentAccount);
        this.showSearchMenu = showSearchMenu;
        this.controller = MemberRequestsController.getInstance(currentAccount);
    }

    public FrameLayout getRootLayout() {
        if (this.rootLayout == null) {
            FrameLayout frameLayout = new FrameLayout(this.fragment.getParentActivity());
            this.rootLayout = frameLayout;
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray, this.fragment.getResourceProvider()));
            FlickerLoadingView loadingView = getLoadingView();
            this.loadingView = loadingView;
            this.rootLayout.addView(loadingView, -1, -1);
            StickerEmptyView searchEmptyView = getSearchEmptyView();
            this.searchEmptyView = searchEmptyView;
            this.rootLayout.addView(searchEmptyView, -1, -1);
            StickerEmptyView emptyView = getEmptyView();
            this.emptyView = emptyView;
            this.rootLayout.addView(emptyView, LayoutHelper.createFrame(-1, -1.0f));
            LinearLayoutManager layoutManager = new LinearLayoutManager(this.fragment.getParentActivity());
            RecyclerListView recyclerListView = new RecyclerListView(this.fragment.getParentActivity());
            this.recyclerView = recyclerListView;
            recyclerListView.setAdapter(this.adapter);
            this.recyclerView.setLayoutManager(layoutManager);
            this.recyclerView.setOnItemClickListener(new MemberRequestsDelegate$$ExternalSyntheticLambda9(this));
            this.recyclerView.setOnScrollListener(this.listScrollListener);
            this.recyclerView.setSelectorDrawableColor(Theme.getColor(Theme.key_listSelector, this.fragment.getResourceProvider()));
            this.rootLayout.addView(this.recyclerView, -1, -1);
        }
        return this.rootLayout;
    }

    public void setShowLastItemDivider(boolean showLastItemDivider) {
        this.isShowLastItemDivider = showLastItemDivider;
    }

    public Adapter getAdapter() {
        return this.adapter;
    }

    public FlickerLoadingView getLoadingView() {
        if (this.loadingView == null) {
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.fragment.getParentActivity(), this.fragment.getResourceProvider());
            this.loadingView = flickerLoadingView;
            flickerLoadingView.setAlpha(0.0f);
            if (this.isShowLastItemDivider) {
                this.loadingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.fragment.getResourceProvider()));
            }
            this.loadingView.setColors(Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundGray, null);
            this.loadingView.setViewType(15);
        }
        return this.loadingView;
    }

    public StickerEmptyView getEmptyView() {
        String str;
        int i;
        String str2;
        int i2;
        if (this.emptyView == null) {
            StickerEmptyView stickerEmptyView = new StickerEmptyView(this.fragment.getParentActivity(), null, 2, this.fragment.getResourceProvider());
            this.emptyView = stickerEmptyView;
            TextView textView = stickerEmptyView.title;
            if (this.isChannel) {
                i = R.string.NoSubscribeRequests;
                str = "NoSubscribeRequests";
            } else {
                i = R.string.NoMemberRequests;
                str = "NoMemberRequests";
            }
            textView.setText(LocaleController.getString(str, i));
            TextView textView2 = this.emptyView.subtitle;
            if (this.isChannel) {
                i2 = R.string.NoSubscribeRequestsDescription;
                str2 = "NoSubscribeRequestsDescription";
            } else {
                i2 = R.string.NoMemberRequestsDescription;
                str2 = "NoMemberRequestsDescription";
            }
            textView2.setText(LocaleController.getString(str2, i2));
            this.emptyView.setAnimateLayoutChange(true);
            this.emptyView.setVisibility(8);
        }
        return this.emptyView;
    }

    public StickerEmptyView getSearchEmptyView() {
        if (this.searchEmptyView == null) {
            StickerEmptyView stickerEmptyView = new StickerEmptyView(this.fragment.getParentActivity(), null, 1, this.fragment.getResourceProvider());
            this.searchEmptyView = stickerEmptyView;
            if (this.isShowLastItemDivider) {
                stickerEmptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.fragment.getResourceProvider()));
            }
            this.searchEmptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
            this.searchEmptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
            this.searchEmptyView.setAnimateLayoutChange(true);
            this.searchEmptyView.setVisibility(8);
        }
        return this.searchEmptyView;
    }

    public void setRecyclerView(RecyclerListView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setOnItemClickListener(new MemberRequestsDelegate$$ExternalSyntheticLambda9(this));
        final RecyclerView.OnScrollListener currentScrollListener = recyclerView.getOnScrollListener();
        if (currentScrollListener == null) {
            recyclerView.setOnScrollListener(this.listScrollListener);
        } else {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate.1
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView2, int newState) {
                    super.onScrollStateChanged(recyclerView2, newState);
                    currentScrollListener.onScrollStateChanged(recyclerView2, newState);
                    MemberRequestsDelegate.this.listScrollListener.onScrollStateChanged(recyclerView2, newState);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView2, int dx, int dy) {
                    super.onScrolled(recyclerView2, dx, dy);
                    currentScrollListener.onScrolled(recyclerView2, dx, dy);
                    MemberRequestsDelegate.this.listScrollListener.onScrolled(recyclerView2, dx, dy);
                }
            });
        }
    }

    public void onItemClick(View view, int position) {
        if (view instanceof MemberRequestCell) {
            if (this.isSearchExpanded) {
                AndroidUtilities.hideKeyboard(this.fragment.getParentActivity().getCurrentFocus());
            }
            final MemberRequestCell cell = (MemberRequestCell) view;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MemberRequestsDelegate.this.m3324xf2d2b3f6(cell);
                }
            }, this.isSearchExpanded ? 100L : 0L);
        }
    }

    /* renamed from: lambda$onItemClick$1$org-telegram-ui-Delegates-MemberRequestsDelegate */
    public /* synthetic */ void m3324xf2d2b3f6(MemberRequestCell cell) {
        TLRPC.TL_chatInviteImporter importer = cell.getImporter();
        this.importer = importer;
        TLRPC.User user = this.users.get(importer.user_id);
        if (user == null) {
            return;
        }
        this.fragment.getMessagesController().putUser(user, false);
        boolean isLandscape = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
        boolean showProfile = user.photo == null || isLandscape;
        if (showProfile) {
            this.isNeedRestoreList = true;
            this.fragment.dismissCurrentDialog();
            Bundle args = new Bundle();
            ProfileActivity profileActivity = new ProfileActivity(args);
            args.putLong("user_id", user.id);
            args.putBoolean("removeFragmentOnChatOpen", false);
            this.fragment.presentFragment(profileActivity);
        } else if (this.previewDialog == null) {
            RecyclerListView parentListView = (RecyclerListView) cell.getParent();
            PreviewDialog previewDialog = new PreviewDialog(this.fragment.getParentActivity(), parentListView, this.fragment.getResourceProvider(), this.isChannel);
            this.previewDialog = previewDialog;
            previewDialog.setImporter(this.importer, cell.getAvatarImageView());
            this.previewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    MemberRequestsDelegate.this.m3323x1290dd7(dialogInterface);
                }
            });
            this.previewDialog.show();
        }
    }

    /* renamed from: lambda$onItemClick$0$org-telegram-ui-Delegates-MemberRequestsDelegate */
    public /* synthetic */ void m3323x1290dd7(DialogInterface dialog) {
        this.previewDialog = null;
    }

    public boolean onBackPressed() {
        PreviewDialog previewDialog = this.previewDialog;
        if (previewDialog != null) {
            previewDialog.dismiss();
            return false;
        }
        return true;
    }

    public void setSearchExpanded(boolean isExpanded) {
        this.isSearchExpanded = isExpanded;
    }

    public void setQuery(String query) {
        if (this.searchRunnable != null) {
            Utilities.searchQueue.cancelRunnable(this.searchRunnable);
            this.searchRunnable = null;
        }
        int i = 0;
        if (this.searchRequestId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.searchRequestId, false);
            this.searchRequestId = 0;
        }
        this.query = query;
        if (this.isDataLoaded && this.allImporters.isEmpty()) {
            setViewVisible(this.loadingView, false, false);
            return;
        }
        if (TextUtils.isEmpty(query)) {
            this.adapter.setItems(this.allImporters);
            setViewVisible(this.recyclerView, true, true);
            setViewVisible(this.loadingView, false, false);
            StickerEmptyView stickerEmptyView = this.searchEmptyView;
            if (stickerEmptyView != null) {
                stickerEmptyView.setVisibility(4);
            }
            if (query == null && this.showSearchMenu) {
                ActionBarMenuItem item = this.fragment.getActionBar().createMenu().getItem(0);
                if (this.allImporters.isEmpty()) {
                    i = 8;
                }
                item.setVisibility(i);
            }
        } else {
            this.adapter.setItems(Collections.emptyList());
            setViewVisible(this.recyclerView, false, false);
            setViewVisible(this.loadingView, true, true);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    MemberRequestsDelegate.this.loadMembers();
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }
        if (query != null) {
            StickerEmptyView stickerEmptyView2 = this.emptyView;
            if (stickerEmptyView2 != null) {
                stickerEmptyView2.setVisibility(4);
            }
            StickerEmptyView stickerEmptyView3 = this.searchEmptyView;
            if (stickerEmptyView3 != null) {
                stickerEmptyView3.setVisibility(4);
            }
        }
    }

    public void loadMembers() {
        TLRPC.TL_messages_chatInviteImporters firstImporters;
        boolean isNeedShowLoading = true;
        if (this.isFirstLoading && (firstImporters = this.controller.getCachedImporters(this.chatId)) != null) {
            isNeedShowLoading = false;
            this.isDataLoaded = true;
            onImportersLoaded(firstImporters, null, true, true);
        }
        final boolean needShowLoading = isNeedShowLoading;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                MemberRequestsDelegate.this.m3322xbbbcf315(needShowLoading);
            }
        });
    }

    /* renamed from: lambda$loadMembers$5$org-telegram-ui-Delegates-MemberRequestsDelegate */
    public /* synthetic */ void m3322xbbbcf315(boolean needShowLoading) {
        Runnable runnable;
        TLRPC.TL_chatInviteImporter lastInvitedUser;
        final boolean isEmptyQuery = TextUtils.isEmpty(this.query);
        final boolean isEmptyOffset = this.currentImporters.isEmpty() || this.isFirstLoading;
        final String lastQuery = this.query;
        this.isLoading = true;
        this.isFirstLoading = false;
        if (isEmptyQuery && needShowLoading) {
            runnable = new Runnable() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MemberRequestsDelegate.this.m3319xe6c000b8();
                }
            };
        } else {
            runnable = null;
        }
        final Runnable showLoadingRunnable = runnable;
        if (isEmptyQuery) {
            AndroidUtilities.runOnUIThread(showLoadingRunnable, 300L);
        }
        if (!isEmptyQuery && !this.currentImporters.isEmpty()) {
            List<TLRPC.TL_chatInviteImporter> list = this.currentImporters;
            lastInvitedUser = list.get(list.size() - 1);
        } else {
            lastInvitedUser = null;
        }
        this.searchRequestId = this.controller.getImporters(this.chatId, lastQuery, lastInvitedUser, this.users, new RequestDelegate() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda8
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MemberRequestsDelegate.this.m3321xca134cf6(isEmptyQuery, showLoadingRunnable, lastQuery, isEmptyOffset, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadMembers$2$org-telegram-ui-Delegates-MemberRequestsDelegate */
    public /* synthetic */ void m3319xe6c000b8() {
        setViewVisible(this.loadingView, true, true);
    }

    /* renamed from: lambda$loadMembers$4$org-telegram-ui-Delegates-MemberRequestsDelegate */
    public /* synthetic */ void m3321xca134cf6(final boolean isEmptyQuery, final Runnable showLoadingRunnable, final String lastQuery, final boolean isEmptyOffset, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                MemberRequestsDelegate.this.m3320xd869a6d7(isEmptyQuery, showLoadingRunnable, lastQuery, error, response, isEmptyOffset);
            }
        });
    }

    /* renamed from: lambda$loadMembers$3$org-telegram-ui-Delegates-MemberRequestsDelegate */
    public /* synthetic */ void m3320xd869a6d7(boolean isEmptyQuery, Runnable showLoadingRunnable, String lastQuery, TLRPC.TL_error error, TLObject response, boolean isEmptyOffset) {
        this.isLoading = false;
        this.isDataLoaded = true;
        if (isEmptyQuery) {
            AndroidUtilities.cancelRunOnUIThread(showLoadingRunnable);
        }
        setViewVisible(this.loadingView, false, false);
        if (TextUtils.equals(lastQuery, this.query) && error == null) {
            this.isDataLoaded = true;
            TLRPC.TL_messages_chatInviteImporters importers = (TLRPC.TL_messages_chatInviteImporters) response;
            onImportersLoaded(importers, lastQuery, isEmptyOffset, false);
        }
    }

    private void onImportersLoaded(TLRPC.TL_messages_chatInviteImporters importers, String lastQuery, boolean isEmptyOffset, boolean fromCache) {
        for (int i = 0; i < importers.users.size(); i++) {
            TLRPC.User user = importers.users.get(i);
            this.users.put(user.id, user);
        }
        if (isEmptyOffset) {
            this.adapter.setItems(importers.importers);
        } else {
            this.adapter.appendItems(importers.importers);
        }
        boolean z = false;
        if (TextUtils.isEmpty(lastQuery)) {
            this.allImporters.clear();
            this.allImporters.addAll(importers.importers);
            if (this.showSearchMenu) {
                this.fragment.getActionBar().createMenu().getItem(0).setVisibility(this.allImporters.isEmpty() ? 8 : 0);
            }
        }
        onImportersChanged(lastQuery, fromCache, false);
        if (this.currentImporters.size() < importers.count) {
            z = true;
        }
        this.hasMore = z;
    }

    @Override // org.telegram.ui.Cells.MemberRequestCell.OnClickListener
    public void onAddClicked(TLRPC.TL_chatInviteImporter importer) {
        hideChatJoinRequest(importer, true);
    }

    @Override // org.telegram.ui.Cells.MemberRequestCell.OnClickListener
    public void onDismissClicked(TLRPC.TL_chatInviteImporter importer) {
        hideChatJoinRequest(importer, false);
    }

    public void setAdapterItemsEnabled(boolean adapterItemsEnabled) {
        int position;
        if (this.recyclerView != null && (position = this.adapter.extraFirstHolders()) >= 0 && position < this.recyclerView.getChildCount()) {
            this.recyclerView.getChildAt(position).setEnabled(adapterItemsEnabled);
        }
    }

    public void onImportersChanged(String query, boolean fromCache, boolean fromHide) {
        boolean isListVisible;
        if (TextUtils.isEmpty(query)) {
            isListVisible = !this.allImporters.isEmpty() || fromCache;
            StickerEmptyView stickerEmptyView = this.emptyView;
            if (stickerEmptyView != null) {
                stickerEmptyView.setVisibility(isListVisible ? 4 : 0);
            }
            StickerEmptyView stickerEmptyView2 = this.searchEmptyView;
            if (stickerEmptyView2 != null) {
                stickerEmptyView2.setVisibility(4);
            }
        } else {
            isListVisible = !this.currentImporters.isEmpty() || fromCache;
            StickerEmptyView stickerEmptyView3 = this.emptyView;
            if (stickerEmptyView3 != null) {
                stickerEmptyView3.setVisibility(4);
            }
            StickerEmptyView stickerEmptyView4 = this.searchEmptyView;
            if (stickerEmptyView4 != null) {
                stickerEmptyView4.setVisibility(isListVisible ? 4 : 0);
            }
        }
        setViewVisible(this.recyclerView, isListVisible, true);
        if (this.allImporters.isEmpty()) {
            StickerEmptyView stickerEmptyView5 = this.emptyView;
            if (stickerEmptyView5 != null) {
                stickerEmptyView5.setVisibility(0);
            }
            StickerEmptyView stickerEmptyView6 = this.searchEmptyView;
            if (stickerEmptyView6 != null) {
                stickerEmptyView6.setVisibility(4);
            }
            setViewVisible(this.loadingView, false, false);
            if (this.isSearchExpanded && this.showSearchMenu) {
                this.fragment.getActionBar().createMenu().closeSearchField(true);
            }
        }
    }

    public boolean hasAllImporters() {
        return !this.allImporters.isEmpty();
    }

    private void hideChatJoinRequest(final TLRPC.TL_chatInviteImporter importer, final boolean isApproved) {
        final TLRPC.User user = this.users.get(importer.user_id);
        if (user == null) {
            return;
        }
        final TLRPC.TL_messages_hideChatJoinRequest req = new TLRPC.TL_messages_hideChatJoinRequest();
        req.approved = isApproved;
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
        req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MemberRequestsDelegate.this.m3318xa1bba2bb(importer, isApproved, user, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$hideChatJoinRequest$7$org-telegram-ui-Delegates-MemberRequestsDelegate */
    public /* synthetic */ void m3318xa1bba2bb(final TLRPC.TL_chatInviteImporter importer, final boolean isApproved, final TLRPC.User user, final TLRPC.TL_messages_hideChatJoinRequest req, final TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MemberRequestsDelegate.this.m3317xb011fc9c(error, response, importer, isApproved, user, req);
            }
        });
    }

    /* renamed from: lambda$hideChatJoinRequest$6$org-telegram-ui-Delegates-MemberRequestsDelegate */
    public /* synthetic */ void m3317xb011fc9c(TLRPC.TL_error error, TLObject response, TLRPC.TL_chatInviteImporter importer, boolean isApproved, TLRPC.User user, TLRPC.TL_messages_hideChatJoinRequest req) {
        String message;
        int i = 0;
        if (error != null) {
            AlertsCreator.processError(this.currentAccount, error, this.fragment, req, new Object[0]);
            return;
        }
        TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
        if (!updates.chats.isEmpty()) {
            TLRPC.Chat chat = updates.chats.get(0);
            MessagesController.getInstance(this.currentAccount).loadFullChat(chat.id, 0, true);
        }
        int i2 = 0;
        while (true) {
            if (i2 >= this.allImporters.size()) {
                break;
            } else if (this.allImporters.get(i2).user_id != importer.user_id) {
                i2++;
            } else {
                this.allImporters.remove(i2);
                break;
            }
        }
        this.adapter.removeItem(importer);
        onImportersChanged(this.query, false, true);
        if (isApproved) {
            Bulletin.MultiLineLayout layout = new Bulletin.MultiLineLayout(this.fragment.getParentActivity(), this.fragment.getResourceProvider());
            layout.imageView.setRoundRadius(AndroidUtilities.dp(15.0f));
            layout.imageView.setForUserOrChat(user, new AvatarDrawable(user));
            String userName = UserObject.getFirstName(user);
            if (this.isChannel) {
                message = LocaleController.formatString("HasBeenAddedToChannel", R.string.HasBeenAddedToChannel, userName);
            } else {
                message = LocaleController.formatString("HasBeenAddedToGroup", R.string.HasBeenAddedToGroup, userName);
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(message);
            int start = message.indexOf(userName);
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), start, userName.length() + start, 18);
            layout.textView.setText(stringBuilder);
            if (this.allImporters.isEmpty()) {
                Bulletin.make(this.fragment, layout, (int) Bulletin.DURATION_LONG).show();
            } else {
                Bulletin.make(this.layoutContainer, layout, (int) Bulletin.DURATION_LONG).show();
            }
        }
        ActionBarMenu menu = this.fragment.getActionBar().createMenu();
        if (TextUtils.isEmpty(this.query) && this.showSearchMenu) {
            ActionBarMenuItem item = menu.getItem(0);
            if (this.allImporters.isEmpty()) {
                i = 8;
            }
            item.setVisibility(i);
        }
    }

    public void hidePreview() {
        this.previewDialog.dismiss();
        this.importer = null;
    }

    private void setViewVisible(View view, boolean isVisible, boolean isAnimated) {
        if (view == null) {
            return;
        }
        int i = 0;
        boolean isCurrentVisible = view.getVisibility() == 0;
        float targetAlpha = isVisible ? 1.0f : 0.0f;
        if (isVisible == isCurrentVisible && targetAlpha == view.getAlpha()) {
            return;
        }
        if (isAnimated) {
            if (isVisible) {
                view.setAlpha(0.0f);
            }
            view.setVisibility(0);
            view.animate().alpha(targetAlpha).setDuration(150L).start();
            return;
        }
        if (!isVisible) {
            i = 4;
        }
        view.setVisibility(i);
    }

    /* loaded from: classes5.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
            MemberRequestsDelegate.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerListView.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    View view2 = new View(parent.getContext());
                    view2.setBackground(Theme.getThemedDrawable(parent.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    view = view2;
                    break;
                case 2:
                    view = new View(parent.getContext()) { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate.Adapter.1
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 3:
                    view = new View(parent.getContext());
                    break;
                default:
                    Context context = parent.getContext();
                    MemberRequestsDelegate memberRequestsDelegate = MemberRequestsDelegate.this;
                    MemberRequestCell cell = new MemberRequestCell(context, memberRequestsDelegate, memberRequestsDelegate.isChannel);
                    cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, MemberRequestsDelegate.this.fragment.getResourceProvider()));
                    view = cell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() != 0) {
                if (holder.getItemViewType() == 2) {
                    holder.itemView.requestLayout();
                    return;
                }
                return;
            }
            MemberRequestCell cell = (MemberRequestCell) holder.itemView;
            int position2 = position - extraFirstHolders();
            LongSparseArray<TLRPC.User> longSparseArray = MemberRequestsDelegate.this.users;
            TLRPC.TL_chatInviteImporter tL_chatInviteImporter = (TLRPC.TL_chatInviteImporter) MemberRequestsDelegate.this.currentImporters.get(position2);
            boolean z = true;
            if (position2 == MemberRequestsDelegate.this.currentImporters.size() - 1) {
                z = false;
            }
            cell.setData(longSparseArray, tL_chatInviteImporter, z);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return extraFirstHolders() + MemberRequestsDelegate.this.currentImporters.size() + extraLastHolders();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (MemberRequestsDelegate.this.isShowLastItemDivider) {
                return (position != MemberRequestsDelegate.this.currentImporters.size() || MemberRequestsDelegate.this.currentImporters.isEmpty()) ? 0 : 1;
            } else if (position == 0) {
                return 2;
            } else {
                if (position == getItemCount() - 1) {
                    return 3;
                }
                return 0;
            }
        }

        public void setItems(List<TLRPC.TL_chatInviteImporter> newItems) {
            MemberRequestsDelegate.this.currentImporters.clear();
            MemberRequestsDelegate.this.currentImporters.addAll(newItems);
            notifyDataSetChanged();
        }

        public void appendItems(List<TLRPC.TL_chatInviteImporter> newItems) {
            MemberRequestsDelegate.this.currentImporters.addAll(newItems);
            if (MemberRequestsDelegate.this.currentImporters.size() > newItems.size()) {
                notifyItemChanged((MemberRequestsDelegate.this.currentImporters.size() - newItems.size()) - 1);
            }
            notifyItemRangeInserted(MemberRequestsDelegate.this.currentImporters.size() - newItems.size(), newItems.size());
        }

        public void removeItem(TLRPC.TL_chatInviteImporter item) {
            int position = -1;
            int i = 0;
            while (true) {
                if (i >= MemberRequestsDelegate.this.currentImporters.size()) {
                    break;
                } else if (((TLRPC.TL_chatInviteImporter) MemberRequestsDelegate.this.currentImporters.get(i)).user_id != item.user_id) {
                    i++;
                } else {
                    position = i;
                    break;
                }
            }
            if (position >= 0) {
                MemberRequestsDelegate.this.currentImporters.remove(position);
                notifyItemRemoved(extraFirstHolders() + position);
                if (MemberRequestsDelegate.this.currentImporters.isEmpty()) {
                    notifyItemRemoved(1);
                }
            }
        }

        public int extraFirstHolders() {
            return !MemberRequestsDelegate.this.isShowLastItemDivider ? 1 : 0;
        }

        private int extraLastHolders() {
            return (!MemberRequestsDelegate.this.isShowLastItemDivider || !MemberRequestsDelegate.this.currentImporters.isEmpty()) ? 1 : 0;
        }
    }

    /* loaded from: classes5.dex */
    public class PreviewDialog extends Dialog {
        private float animationProgress;
        private ValueAnimator animator;
        private BitmapDrawable backgroundDrawable;
        private final TextView bioText;
        private final ViewGroup contentView;
        private BackupImageView imageView;
        private TLRPC.TL_chatInviteImporter importer;
        private final TextView nameText;
        private final AvatarPreviewPagerIndicator pagerIndicator;
        private final Drawable pagerShadowDrawable;
        private final ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
        private final int shadowPaddingLeft;
        private final int shadowPaddingTop;
        private final ProfileGalleryView viewPager;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public PreviewDialog(Context context, RecyclerListView parentListView, Theme.ResourcesProvider resourcesProvider, boolean isChannel) {
            super(context, R.style.TransparentDialog2);
            String str;
            int i;
            MemberRequestsDelegate.this = r11;
            Drawable mutate = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert2).mutate();
            this.pagerShadowDrawable = mutate;
            TextView textView = new TextView(getContext());
            this.nameText = textView;
            TextView textView2 = new TextView(getContext());
            this.bioText = textView2;
            ViewGroup viewGroup = new ViewGroup(getContext()) { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate.PreviewDialog.3
                private final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate.PreviewDialog.3.1
                    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                    public boolean onSingleTapUp(MotionEvent e) {
                        boolean isTouchInsideContent = PreviewDialog.this.pagerShadowDrawable.getBounds().contains((int) e.getX(), (int) e.getY()) || (((float) PreviewDialog.this.popupLayout.getLeft()) < e.getX() && e.getX() < ((float) PreviewDialog.this.popupLayout.getRight()) && ((float) PreviewDialog.this.popupLayout.getTop()) < e.getY() && e.getY() < ((float) PreviewDialog.this.popupLayout.getBottom()));
                        if (!isTouchInsideContent) {
                            PreviewDialog.this.dismiss();
                        }
                        return super.onSingleTapUp(e);
                    }
                });
                private final Path clipPath = new Path();
                private final RectF rectF = new RectF();
                private boolean firstSizeChange = true;

                @Override // android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    return this.gestureDetector.onTouchEvent(event);
                }

                @Override // android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    setWillNotDraw(false);
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int minSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
                    double measuredHeight = getMeasuredHeight();
                    Double.isNaN(measuredHeight);
                    int pagerSize = Math.min(minSize, (int) (measuredHeight * 0.66d)) - (AndroidUtilities.dp(12.0f) * 2);
                    int pagerSpec = View.MeasureSpec.makeMeasureSpec(pagerSize, Integer.MIN_VALUE);
                    PreviewDialog.this.viewPager.measure(pagerSpec, pagerSpec);
                    PreviewDialog.this.pagerIndicator.measure(pagerSpec, pagerSpec);
                    int textWidthSpec = View.MeasureSpec.makeMeasureSpec(pagerSize - (AndroidUtilities.dp(16.0f) * 2), C.BUFFER_FLAG_ENCRYPTED);
                    PreviewDialog.this.nameText.measure(textWidthSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
                    PreviewDialog.this.bioText.measure(textWidthSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
                    PreviewDialog.this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(PreviewDialog.this.viewPager.getMeasuredWidth() + (PreviewDialog.this.shadowPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
                }

                @Override // android.view.ViewGroup, android.view.View
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    int top = (getHeight() - PreviewDialog.this.getContentHeight()) / 2;
                    int left = (getWidth() - PreviewDialog.this.viewPager.getMeasuredWidth()) / 2;
                    PreviewDialog.this.viewPager.layout(left, top, PreviewDialog.this.viewPager.getMeasuredWidth() + left, PreviewDialog.this.viewPager.getMeasuredHeight() + top);
                    PreviewDialog.this.pagerIndicator.layout(PreviewDialog.this.viewPager.getLeft(), PreviewDialog.this.viewPager.getTop(), PreviewDialog.this.viewPager.getRight(), PreviewDialog.this.viewPager.getTop() + PreviewDialog.this.pagerIndicator.getMeasuredHeight());
                    int top2 = top + PreviewDialog.this.viewPager.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                    PreviewDialog.this.nameText.layout(PreviewDialog.this.viewPager.getLeft() + AndroidUtilities.dp(16.0f), top2, PreviewDialog.this.viewPager.getRight() - AndroidUtilities.dp(16.0f), PreviewDialog.this.nameText.getMeasuredHeight() + top2);
                    int top3 = top2 + PreviewDialog.this.nameText.getMeasuredHeight();
                    int i2 = 8;
                    if (PreviewDialog.this.bioText.getVisibility() != 8) {
                        int top4 = top3 + AndroidUtilities.dp(4.0f);
                        PreviewDialog.this.bioText.layout(PreviewDialog.this.nameText.getLeft(), top4, PreviewDialog.this.nameText.getRight(), PreviewDialog.this.bioText.getMeasuredHeight() + top4);
                        top3 = top4 + PreviewDialog.this.bioText.getMeasuredHeight();
                    }
                    int top5 = top3 + AndroidUtilities.dp(12.0f);
                    PreviewDialog.this.pagerShadowDrawable.setBounds(PreviewDialog.this.viewPager.getLeft() - PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.viewPager.getTop() - PreviewDialog.this.shadowPaddingTop, PreviewDialog.this.viewPager.getRight() + PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.shadowPaddingTop + top5);
                    PreviewDialog.this.popupLayout.layout((PreviewDialog.this.viewPager.getRight() - PreviewDialog.this.popupLayout.getMeasuredWidth()) + PreviewDialog.this.shadowPaddingLeft, top5, PreviewDialog.this.viewPager.getRight() + PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.popupLayout.getMeasuredHeight() + top5);
                    ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = PreviewDialog.this.popupLayout;
                    if (PreviewDialog.this.popupLayout.getBottom() < b) {
                        i2 = 0;
                    }
                    actionBarPopupWindowLayout.setVisibility(i2);
                    int radius = AndroidUtilities.dp(6.0f);
                    this.rectF.set(PreviewDialog.this.viewPager.getLeft(), PreviewDialog.this.viewPager.getTop(), PreviewDialog.this.viewPager.getRight(), PreviewDialog.this.viewPager.getTop() + (radius * 2));
                    this.clipPath.reset();
                    this.clipPath.addRoundRect(this.rectF, radius, radius, Path.Direction.CW);
                    this.rectF.set(l, PreviewDialog.this.viewPager.getTop() + radius, r, b);
                    this.clipPath.addRect(this.rectF, Path.Direction.CW);
                }

                @Override // android.view.View
                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    boolean isLandscape = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
                    if (isLandscape) {
                        PreviewDialog.super.dismiss();
                    }
                    if (w != oldw && h != oldh) {
                        if (!this.firstSizeChange) {
                            PreviewDialog.this.updateBackgroundBitmap();
                        }
                        this.firstSizeChange = false;
                    }
                }

                @Override // android.view.ViewGroup, android.view.View
                protected void dispatchDraw(Canvas canvas) {
                    canvas.save();
                    canvas.clipPath(this.clipPath);
                    super.dispatchDraw(canvas);
                    canvas.restore();
                }

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    PreviewDialog.this.pagerShadowDrawable.draw(canvas);
                    super.onDraw(canvas);
                }

                @Override // android.view.View
                protected boolean verifyDrawable(Drawable who) {
                    return who == PreviewDialog.this.pagerShadowDrawable || super.verifyDrawable(who);
                }
            };
            this.contentView = viewGroup;
            setCancelable(true);
            viewGroup.setVisibility(4);
            int backgroundColor = Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, r11.fragment.getResourceProvider());
            mutate.setColorFilter(new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY));
            mutate.setCallback(viewGroup);
            Rect paddingRect = new Rect();
            mutate.getPadding(paddingRect);
            this.shadowPaddingTop = paddingRect.top;
            this.shadowPaddingLeft = paddingRect.left;
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, resourcesProvider);
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setBackgroundColor(backgroundColor);
            viewGroup.addView(actionBarPopupWindowLayout);
            AvatarPreviewPagerIndicator avatarPreviewPagerIndicator = new AvatarPreviewPagerIndicator(getContext()) { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate.PreviewDialog.1
                @Override // org.telegram.ui.AvatarPreviewPagerIndicator, android.view.View
                public void onDraw(Canvas canvas) {
                    if (this.profileGalleryView.getRealCount() > 1) {
                        super.onDraw(canvas);
                    }
                }
            };
            this.pagerIndicator = avatarPreviewPagerIndicator;
            ProfileGalleryView profileGalleryView = new ProfileGalleryView(context, r11.fragment.getActionBar(), parentListView, avatarPreviewPagerIndicator);
            this.viewPager = profileGalleryView;
            profileGalleryView.setCreateThumbFromParent(true);
            viewGroup.addView(profileGalleryView);
            avatarPreviewPagerIndicator.setProfileGalleryView(profileGalleryView);
            viewGroup.addView(avatarPreviewPagerIndicator);
            textView.setMaxLines(1);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, r11.fragment.getResourceProvider()));
            textView.setTextSize(16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            viewGroup.addView(textView);
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, r11.fragment.getResourceProvider()));
            textView2.setTextSize(14.0f);
            viewGroup.addView(textView2);
            ActionBarMenuSubItem addCell = new ActionBarMenuSubItem(context, true, false);
            addCell.setColors(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider), Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, resourcesProvider));
            addCell.setSelectorColor(Theme.getColor(Theme.key_dialogButtonSelector, resourcesProvider));
            if (isChannel) {
                i = R.string.AddToChannel;
                str = "AddToChannel";
            } else {
                i = R.string.AddToGroup;
                str = "AddToGroup";
            }
            addCell.setTextAndIcon(LocaleController.getString(str, i), R.drawable.msg_requests);
            addCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    MemberRequestsDelegate.PreviewDialog.this.m3325x8a31fab9(view);
                }
            });
            actionBarPopupWindowLayout.addView(addCell);
            ActionBarMenuSubItem sendMsgCell = new ActionBarMenuSubItem(context, false, false);
            sendMsgCell.setColors(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider), Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, resourcesProvider));
            sendMsgCell.setSelectorColor(Theme.getColor(Theme.key_dialogButtonSelector, resourcesProvider));
            sendMsgCell.setTextAndIcon(LocaleController.getString("SendMessage", R.string.SendMessage), R.drawable.msg_msgbubble3);
            sendMsgCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    MemberRequestsDelegate.PreviewDialog.this.m3326x892fe98(view);
                }
            });
            actionBarPopupWindowLayout.addView(sendMsgCell);
            ActionBarMenuSubItem dismissCell = new ActionBarMenuSubItem(context, false, true);
            dismissCell.setColors(Theme.getColor(Theme.key_dialogTextRed2, resourcesProvider), Theme.getColor(Theme.key_dialogRedIcon, resourcesProvider));
            dismissCell.setSelectorColor(Theme.getColor(Theme.key_dialogButtonSelector, resourcesProvider));
            dismissCell.setTextAndIcon(LocaleController.getString("DismissRequest", R.string.DismissRequest), R.drawable.msg_remove);
            dismissCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    MemberRequestsDelegate.PreviewDialog.this.m3327x86f40277(view);
                }
            });
            actionBarPopupWindowLayout.addView(dismissCell);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog */
        public /* synthetic */ void m3325x8a31fab9(View v) {
            TLRPC.TL_chatInviteImporter tL_chatInviteImporter = this.importer;
            if (tL_chatInviteImporter != null) {
                MemberRequestsDelegate.this.onAddClicked(tL_chatInviteImporter);
            }
            MemberRequestsDelegate.this.hidePreview();
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog */
        public /* synthetic */ void m3326x892fe98(View v) {
            if (this.importer != null) {
                MemberRequestsDelegate.this.isNeedRestoreList = true;
                super.dismiss();
                MemberRequestsDelegate.this.fragment.dismissCurrentDialog();
                Bundle args = new Bundle();
                args.putLong("user_id", this.importer.user_id);
                ChatActivity chatActivity = new ChatActivity(args);
                MemberRequestsDelegate.this.fragment.presentFragment(chatActivity);
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog */
        public /* synthetic */ void m3327x86f40277(View v) {
            TLRPC.TL_chatInviteImporter tL_chatInviteImporter = this.importer;
            if (tL_chatInviteImporter != null) {
                MemberRequestsDelegate.this.onDismissClicked(tL_chatInviteImporter);
            }
            MemberRequestsDelegate.this.hidePreview();
        }

        @Override // android.app.Dialog
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setWindowAnimations(R.style.DialogNoAnimation);
            setContentView(this.contentView, new ViewGroup.LayoutParams(-1, -1));
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = -1;
            params.height = -1;
            params.dimAmount = 0.0f;
            params.flags &= -3;
            params.gravity = 51;
            if (Build.VERSION.SDK_INT >= 21) {
                params.flags |= -2147417856;
            }
            if (Build.VERSION.SDK_INT >= 28) {
                params.layoutInDisplayCutoutMode = 1;
            }
            getWindow().setAttributes(params);
        }

        public void setImporter(TLRPC.TL_chatInviteImporter importer, BackupImageView imageView) {
            this.importer = importer;
            this.imageView = imageView;
            this.viewPager.setParentAvatarImage(imageView);
            this.viewPager.setData(importer.user_id, true);
            TLRPC.User user = (TLRPC.User) MemberRequestsDelegate.this.users.get(importer.user_id);
            this.nameText.setText(UserObject.getUserName(user));
            this.bioText.setText(importer.about);
            this.bioText.setVisibility(TextUtils.isEmpty(importer.about) ? 8 : 0);
            this.contentView.requestLayout();
        }

        @Override // android.app.Dialog
        public void show() {
            super.show();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MemberRequestsDelegate.PreviewDialog.this.m3329x8f2a92bf();
                }
            }, 80L);
        }

        /* renamed from: lambda$show$3$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog */
        public /* synthetic */ void m3329x8f2a92bf() {
            updateBackgroundBitmap();
            runAnimation(true);
        }

        @Override // android.app.Dialog, android.content.DialogInterface
        public void dismiss() {
            runAnimation(false);
        }

        private void runAnimation(final boolean show) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            int[] location = new int[2];
            this.imageView.getLocationOnScreen(location);
            float f = 1.0f;
            final float fromScale = (this.imageView.getWidth() * 1.0f) / getContentWidth();
            final float fromRadius = (this.imageView.getWidth() / 2.0f) / fromScale;
            final float xFrom = location[0] - (this.viewPager.getLeft() + ((int) ((getContentWidth() * (1.0f - fromScale)) / 2.0f)));
            final float yFrom = location[1] - (this.viewPager.getTop() + ((int) ((getContentHeight() * (1.0f - fromScale)) / 2.0f)));
            final int popupLayoutTranslation = (-this.popupLayout.getTop()) / 2;
            float[] fArr = new float[2];
            fArr[0] = show ? 0.0f : 1.0f;
            if (!show) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    MemberRequestsDelegate.PreviewDialog.this.m3328x49c0d8e2(fromScale, xFrom, yFrom, fromRadius, popupLayoutTranslation, valueAnimator2);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Delegates.MemberRequestsDelegate.PreviewDialog.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    PreviewDialog.this.contentView.setVisibility(0);
                    if (show) {
                        PreviewDialog.this.contentView.setScaleX(fromScale);
                        PreviewDialog.this.contentView.setScaleY(fromScale);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (!show) {
                        PreviewDialog.super.dismiss();
                    }
                }
            });
            this.animator.setDuration(220L);
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.start();
        }

        /* renamed from: lambda$runAnimation$4$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog */
        public /* synthetic */ void m3328x49c0d8e2(float fromScale, float xFrom, float yFrom, float fromRadius, int popupLayoutTranslation, ValueAnimator animation) {
            float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
            this.animationProgress = floatValue;
            float scale = ((1.0f - fromScale) * floatValue) + fromScale;
            this.contentView.setScaleX(scale);
            this.contentView.setScaleY(scale);
            this.contentView.setTranslationX((1.0f - this.animationProgress) * xFrom);
            this.contentView.setTranslationY((1.0f - this.animationProgress) * yFrom);
            int roundRadius = (int) ((1.0f - this.animationProgress) * fromRadius);
            this.viewPager.setRoundRadius(roundRadius, roundRadius);
            float alpha = MathUtils.clamp((this.animationProgress * 2.0f) - 1.0f, 0.0f, 1.0f);
            this.pagerShadowDrawable.setAlpha((int) (alpha * 255.0f));
            this.nameText.setAlpha(alpha);
            this.bioText.setAlpha(alpha);
            this.popupLayout.setTranslationY(popupLayoutTranslation * (1.0f - this.animationProgress));
            this.popupLayout.setAlpha(alpha);
            BitmapDrawable bitmapDrawable = this.backgroundDrawable;
            if (bitmapDrawable != null) {
                bitmapDrawable.setAlpha((int) (this.animationProgress * 255.0f));
            }
            this.pagerIndicator.setAlpha(alpha);
        }

        private Bitmap getBlurredBitmap() {
            int width = (int) (this.contentView.getMeasuredWidth() / 6.0f);
            int height = (int) (this.contentView.getMeasuredHeight() / 6.0f);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(1.0f / 6.0f, 1.0f / 6.0f);
            canvas.save();
            ((LaunchActivity) MemberRequestsDelegate.this.fragment.getParentActivity()).getActionBarLayout().draw(canvas);
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, 76));
            Dialog dialog = MemberRequestsDelegate.this.fragment.getVisibleDialog();
            if (dialog != null) {
                dialog.getWindow().getDecorView().draw(canvas);
            }
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(width, height) / 180));
            return bitmap;
        }

        public void updateBackgroundBitmap() {
            int oldAlpha = 255;
            if (this.backgroundDrawable != null && Build.VERSION.SDK_INT >= 19) {
                oldAlpha = this.backgroundDrawable.getAlpha();
            }
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getContext().getResources(), getBlurredBitmap());
            this.backgroundDrawable = bitmapDrawable;
            bitmapDrawable.setAlpha(oldAlpha);
            getWindow().setBackgroundDrawable(this.backgroundDrawable);
        }

        public int getContentHeight() {
            int height = this.viewPager.getMeasuredHeight() + AndroidUtilities.dp(12.0f) + this.nameText.getMeasuredHeight();
            if (this.bioText.getVisibility() != 8) {
                height += AndroidUtilities.dp(4.0f) + this.bioText.getMeasuredHeight();
            }
            return height + AndroidUtilities.dp(12.0f) + this.popupLayout.getMeasuredHeight();
        }

        private int getContentWidth() {
            return this.viewPager.getMeasuredWidth();
        }
    }
}
