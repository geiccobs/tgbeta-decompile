package org.telegram.ui.Components;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Delegates.MemberRequestsDelegate;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes5.dex */
public class MemberRequestsBottomSheet extends UsersAlertBase {
    private final FlickerLoadingView currentLoadingView;
    private final MemberRequestsDelegate delegate;
    private boolean enterEventSent;
    private final StickerEmptyView membersEmptyView;
    private final StickerEmptyView membersSearchEmptyView;
    private final int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private float yOffset;

    public MemberRequestsBottomSheet(BaseFragment fragment, long chatId) {
        super(fragment.getParentActivity(), false, fragment.getCurrentAccount(), fragment.getResourceProvider());
        this.needSnapToTop = false;
        this.isEmptyViewVisible = false;
        MemberRequestsDelegate memberRequestsDelegate = new MemberRequestsDelegate(fragment, this.container, chatId, false) { // from class: org.telegram.ui.Components.MemberRequestsBottomSheet.1
            @Override // org.telegram.ui.Delegates.MemberRequestsDelegate
            public void onImportersChanged(String query, boolean fromCache, boolean fromHide) {
                if (!hasAllImporters()) {
                    if (MemberRequestsBottomSheet.this.membersEmptyView.getVisibility() != 4) {
                        MemberRequestsBottomSheet.this.membersEmptyView.setVisibility(4);
                    }
                    MemberRequestsBottomSheet.this.dismiss();
                } else if (fromHide) {
                    MemberRequestsBottomSheet.this.searchView.searchEditText.setText("");
                } else {
                    super.onImportersChanged(query, fromCache, fromHide);
                }
            }
        };
        this.delegate = memberRequestsDelegate;
        memberRequestsDelegate.setShowLastItemDivider(false);
        setDimBehindAlpha(75);
        this.searchView.searchEditText.setHint(LocaleController.getString("SearchMemberRequests", R.string.SearchMemberRequests));
        MemberRequestsDelegate.Adapter adapter = memberRequestsDelegate.getAdapter();
        this.listViewAdapter = adapter;
        this.searchListViewAdapter = adapter;
        this.listView.setAdapter(this.listViewAdapter);
        memberRequestsDelegate.setRecyclerView(this.listView);
        int position = ((ViewGroup) this.listView.getParent()).indexOfChild(this.listView);
        FlickerLoadingView loadingView = memberRequestsDelegate.getLoadingView();
        this.currentLoadingView = loadingView;
        this.containerView.addView(loadingView, position, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView emptyView = memberRequestsDelegate.getEmptyView();
        this.membersEmptyView = emptyView;
        this.containerView.addView(emptyView, position, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView searchEmptyView = memberRequestsDelegate.getSearchEmptyView();
        this.membersSearchEmptyView = searchEmptyView;
        this.containerView.addView(searchEmptyView, position, LayoutHelper.createFrame(-1, -1.0f));
        memberRequestsDelegate.loadMembers();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        if (this.delegate.isNeedRestoreList && this.scrollOffsetY == 0) {
            this.scrollOffsetY = AndroidUtilities.dp(8.0f);
        }
        super.show();
        this.delegate.isNeedRestoreList = false;
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        if (this.delegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public boolean isNeedRestoreDialog() {
        return this.delegate.isNeedRestoreList;
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    protected boolean isAllowSelectChildAtPosition(float x, float y) {
        return y >= ((float) (this.scrollOffsetY + this.frameLayout.getMeasuredHeight()));
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    public void setTranslationY(int newOffset) {
        super.setTranslationY(newOffset);
        this.currentLoadingView.setTranslationY(this.frameLayout.getMeasuredHeight() + newOffset);
        this.membersEmptyView.setTranslationY(newOffset);
        this.membersSearchEmptyView.setTranslationY(newOffset);
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    public void updateLayout() {
        int newOffset;
        if (this.listView.getChildCount() <= 0) {
            if (this.listView.getVisibility() == 0) {
                newOffset = this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f);
            } else {
                newOffset = 0;
            }
            if (this.scrollOffsetY != newOffset) {
                this.scrollOffsetY = newOffset;
                setTranslationY(newOffset);
                return;
            }
            return;
        }
        super.updateLayout();
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    public void search(String text) {
        super.search(text);
        this.delegate.setQuery(text);
    }

    @Override // org.telegram.ui.Components.UsersAlertBase
    protected void onSearchViewTouched(MotionEvent ev, final EditTextBoldCursor searchEditText) {
        if (ev.getAction() == 0) {
            this.yOffset = this.scrollOffsetY;
            this.delegate.setAdapterItemsEnabled(false);
        } else if (ev.getAction() == 1 && Math.abs(this.scrollOffsetY - this.yOffset) < this.touchSlop && !this.enterEventSent) {
            Activity activity = AndroidUtilities.findActivity(getContext());
            BaseFragment fragment = null;
            if (activity instanceof LaunchActivity) {
                BaseFragment fragment2 = ((LaunchActivity) activity).getActionBarLayout().fragmentsStack.get(((LaunchActivity) activity).getActionBarLayout().fragmentsStack.size() - 1);
                fragment = fragment2;
            }
            if (fragment instanceof ChatActivity) {
                boolean keyboardVisible = ((ChatActivity) fragment).needEnterText();
                this.enterEventSent = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.MemberRequestsBottomSheet$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        MemberRequestsBottomSheet.this.m2762x7052ead7(searchEditText);
                    }
                }, keyboardVisible ? 200L : 0L);
            } else {
                this.enterEventSent = true;
                setFocusable(true);
                searchEditText.requestFocus();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.MemberRequestsBottomSheet$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                    }
                });
            }
        }
        if (ev.getAction() == 1 || ev.getAction() == 3) {
            this.delegate.setAdapterItemsEnabled(true);
        }
    }

    /* renamed from: lambda$onSearchViewTouched$1$org-telegram-ui-Components-MemberRequestsBottomSheet */
    public /* synthetic */ void m2762x7052ead7(final EditTextBoldCursor searchEditText) {
        setFocusable(true);
        searchEditText.requestFocus();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.MemberRequestsBottomSheet$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
            }
        });
    }
}
