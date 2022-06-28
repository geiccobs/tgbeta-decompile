package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LinkEditActivity;
import org.telegram.ui.ManageLinksActivity;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes5.dex */
public class InviteLinkBottomSheet extends BottomSheet {
    Adapter adapter;
    private long chatId;
    int creatorHeaderRow;
    int creatorRow;
    int divider2Row;
    int divider3Row;
    int dividerRow;
    int emptyHintRow;
    int emptyView;
    int emptyView2;
    int emptyView3;
    BaseFragment fragment;
    boolean hasMore;
    private boolean ignoreLayout;
    TLRPC.ChatFull info;
    TLRPC.TL_chatInviteExported invite;
    InviteDelegate inviteDelegate;
    private boolean isChannel;
    int joinedEndRow;
    int joinedHeaderRow;
    int joinedStartRow;
    int linkActionRow;
    int linkInfoRow;
    private RecyclerListView listView;
    int loadingRow;
    private boolean permanent;
    int requestedEndRow;
    int requestedHeaderRow;
    int requestedStartRow;
    int rowCount;
    private int scrollOffsetY;
    private View shadow;
    private AnimatorSet shadowAnimation;
    private final long timeDif;
    private TextView titleTextView;
    private boolean titleVisible;
    HashMap<Long, TLRPC.User> users;
    boolean usersLoading;
    ArrayList<TLRPC.TL_chatInviteImporter> joinedUsers = new ArrayList<>();
    ArrayList<TLRPC.TL_chatInviteImporter> requestedUsers = new ArrayList<>();
    private boolean canEdit = true;
    public boolean isNeedReopen = false;

    /* loaded from: classes5.dex */
    public interface InviteDelegate {
        void linkRevoked(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void onLinkDeleted(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void onLinkEdited(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void permanentLinkReplaced(TLRPC.TL_chatInviteExported tL_chatInviteExported, TLRPC.TL_chatInviteExported tL_chatInviteExported2);
    }

    public InviteLinkBottomSheet(Context context, final TLRPC.TL_chatInviteExported invite, TLRPC.ChatFull info, final HashMap<Long, TLRPC.User> users, final BaseFragment fragment, long chatId, boolean permanent, boolean isChannel) {
        super(context, false);
        this.invite = invite;
        this.users = users;
        this.fragment = fragment;
        this.info = info;
        this.chatId = chatId;
        this.permanent = permanent;
        this.isChannel = isChannel;
        fixNavigationBar(getThemedColor(Theme.key_graySection));
        if (this.users == null) {
            this.users = new HashMap<>();
        }
        this.timeDif = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - (System.currentTimeMillis() / 1000);
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.InviteLinkBottomSheet.1
            private boolean fullHeight;
            private RectF rect = new RectF();

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && InviteLinkBottomSheet.this.scrollOffsetY != 0 && ev.getY() < InviteLinkBottomSheet.this.scrollOffsetY) {
                    InviteLinkBottomSheet.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !InviteLinkBottomSheet.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                if (Build.VERSION.SDK_INT >= 21) {
                    InviteLinkBottomSheet.this.ignoreLayout = true;
                    setPadding(InviteLinkBottomSheet.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, InviteLinkBottomSheet.this.backgroundPaddingLeft, 0);
                    InviteLinkBottomSheet.this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                this.fullHeight = true;
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                InviteLinkBottomSheet.this.updateLayout();
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (InviteLinkBottomSheet.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int top = (InviteLinkBottomSheet.this.scrollOffsetY - InviteLinkBottomSheet.this.backgroundPaddingTop) - AndroidUtilities.dp(8.0f);
                int height = getMeasuredHeight() + AndroidUtilities.dp(36.0f) + InviteLinkBottomSheet.this.backgroundPaddingTop;
                int statusBarHeight = 0;
                float radProgress = 1.0f;
                if (Build.VERSION.SDK_INT >= 21) {
                    top += AndroidUtilities.statusBarHeight;
                    height -= AndroidUtilities.statusBarHeight;
                    if (this.fullHeight) {
                        if (InviteLinkBottomSheet.this.backgroundPaddingTop + top < AndroidUtilities.statusBarHeight * 2) {
                            int diff = Math.min(AndroidUtilities.statusBarHeight, ((AndroidUtilities.statusBarHeight * 2) - top) - InviteLinkBottomSheet.this.backgroundPaddingTop);
                            top -= diff;
                            height += diff;
                            radProgress = 1.0f - Math.min(1.0f, (diff * 2) / AndroidUtilities.statusBarHeight);
                        }
                        if (InviteLinkBottomSheet.this.backgroundPaddingTop + top < AndroidUtilities.statusBarHeight) {
                            statusBarHeight = Math.min(AndroidUtilities.statusBarHeight, (AndroidUtilities.statusBarHeight - top) - InviteLinkBottomSheet.this.backgroundPaddingTop);
                        }
                    }
                }
                InviteLinkBottomSheet.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                InviteLinkBottomSheet.this.shadowDrawable.draw(canvas);
                if (radProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_dialogBackground));
                    this.rect.set(InviteLinkBottomSheet.this.backgroundPaddingLeft, InviteLinkBottomSheet.this.backgroundPaddingTop + top, getMeasuredWidth() - InviteLinkBottomSheet.this.backgroundPaddingLeft, InviteLinkBottomSheet.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * radProgress, AndroidUtilities.dp(12.0f) * radProgress, Theme.dialogs_onlineCirclePaint);
                }
                if (statusBarHeight > 0) {
                    int color1 = Theme.getColor(Theme.key_dialogBackground);
                    int finalColor = Color.argb(255, (int) (Color.red(color1) * 0.8f), (int) (Color.green(color1) * 0.8f), (int) (Color.blue(color1) * 0.8f));
                    Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                    canvas.drawRect(InviteLinkBottomSheet.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight - statusBarHeight, getMeasuredWidth() - InviteLinkBottomSheet.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        this.containerView.setWillNotDraw(false);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(48.0f);
        View view = new View(context);
        this.shadow = view;
        view.setAlpha(0.0f);
        this.shadow.setVisibility(4);
        this.shadow.setTag(1);
        this.containerView.addView(this.shadow, frameLayoutParams);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.InviteLinkBottomSheet.2
            int lastH;

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (InviteLinkBottomSheet.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int widthSpec, int heightSpec) {
                if (this.lastH != View.MeasureSpec.getSize(heightSpec)) {
                    this.lastH = View.MeasureSpec.getSize(heightSpec);
                    InviteLinkBottomSheet.this.ignoreLayout = true;
                    InviteLinkBottomSheet.this.listView.setPadding(0, 0, 0, 0);
                    InviteLinkBottomSheet.this.ignoreLayout = false;
                    measure(widthSpec, View.MeasureSpec.makeMeasureSpec(heightSpec, Integer.MIN_VALUE));
                    int contentSize = getMeasuredHeight();
                    int i = this.lastH;
                    int padding = (int) ((i / 5.0f) * 2.0f);
                    if (padding < (i - contentSize) + AndroidUtilities.dp(60.0f)) {
                        padding = this.lastH - contentSize;
                    }
                    InviteLinkBottomSheet.this.ignoreLayout = true;
                    InviteLinkBottomSheet.this.listView.setPadding(0, padding, 0, 0);
                    InviteLinkBottomSheet.this.ignoreLayout = false;
                    measure(widthSpec, View.MeasureSpec.makeMeasureSpec(heightSpec, Integer.MIN_VALUE));
                }
                super.onMeasure(widthSpec, heightSpec);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setTag(14);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.listView.setLayoutManager(layoutManager);
        RecyclerListView recyclerListView2 = this.listView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView2.setAdapter(adapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setNestedScrollingEnabled(true);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                InviteLinkBottomSheet.this.updateLayout();
                if (InviteLinkBottomSheet.this.hasMore && !InviteLinkBottomSheet.this.usersLoading) {
                    int lastPosition = layoutManager.findLastVisibleItemPosition();
                    if (InviteLinkBottomSheet.this.rowCount - lastPosition < 10) {
                        InviteLinkBottomSheet.this.loadUsers();
                    }
                }
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i) {
                InviteLinkBottomSheet.this.m2702lambda$new$1$orgtelegramuiComponentsInviteLinkBottomSheet(invite, users, fragment, view2, i);
            }
        });
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        if (permanent) {
            this.titleTextView.setText(LocaleController.getString("InviteLink", R.string.InviteLink));
            this.titleVisible = false;
            this.titleTextView.setVisibility(4);
            this.titleTextView.setAlpha(0.0f);
        } else {
            if (invite.expired) {
                this.titleTextView.setText(LocaleController.getString("ExpiredLink", R.string.ExpiredLink));
            } else if (!invite.revoked) {
                this.titleTextView.setText(LocaleController.getString("InviteLink", R.string.InviteLink));
            } else {
                this.titleTextView.setText(LocaleController.getString("RevokedLink", R.string.RevokedLink));
            }
            this.titleVisible = true;
        }
        if (!TextUtils.isEmpty(invite.title)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(invite.title);
            Emoji.replaceEmoji(builder, this.titleTextView.getPaint().getFontMetricsInt(), (int) this.titleTextView.getPaint().getTextSize(), false);
            this.titleTextView.setText(builder);
        }
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, !this.titleVisible ? 0.0f : 44.0f, 0.0f, 0.0f));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, !this.titleVisible ? 44.0f : 50.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        updateRows();
        loadUsers();
        if (users == null || users.get(Long.valueOf(invite.admin_id)) == null) {
            loadCreator();
        }
        updateColors();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-InviteLinkBottomSheet */
    public /* synthetic */ void m2702lambda$new$1$orgtelegramuiComponentsInviteLinkBottomSheet(TLRPC.TL_chatInviteExported invite, HashMap users, final BaseFragment fragment, View view, int position) {
        if (position == this.creatorRow && invite.admin_id == UserConfig.getInstance(this.currentAccount).clientUserId) {
            return;
        }
        boolean isRequestedUserRow = true;
        boolean isJoinedUserRow = position >= this.joinedStartRow && position < this.joinedEndRow;
        if (position < this.requestedStartRow || position >= this.requestedEndRow) {
            isRequestedUserRow = false;
        }
        if ((position == this.creatorRow || isJoinedUserRow || isRequestedUserRow) && users != null) {
            long userId = invite.admin_id;
            if (isJoinedUserRow) {
                userId = this.joinedUsers.get(position - this.joinedStartRow).user_id;
            } else if (isRequestedUserRow) {
                userId = this.requestedUsers.get(position - this.requestedStartRow).user_id;
            }
            final TLRPC.User user = (TLRPC.User) users.get(Long.valueOf(userId));
            if (user != null) {
                MessagesController.getInstance(UserConfig.selectedAccount).putUser(user, false);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteLinkBottomSheet.this.m2701lambda$new$0$orgtelegramuiComponentsInviteLinkBottomSheet(user, fragment);
                    }
                }, 100L);
                dismiss();
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-InviteLinkBottomSheet */
    public /* synthetic */ void m2701lambda$new$0$orgtelegramuiComponentsInviteLinkBottomSheet(TLRPC.User user, BaseFragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.id);
        ProfileActivity profileActivity = new ProfileActivity(bundle);
        fragment.presentFragment(profileActivity);
        this.isNeedReopen = true;
    }

    public void updateColors() {
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.titleTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
            this.titleTextView.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
            if (!this.titleVisible) {
                this.titleTextView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        }
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.shadow.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        int count = this.listView.getHiddenChildCount();
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            updateColorForView(this.listView.getChildAt(i));
        }
        for (int a = 0; a < count; a++) {
            updateColorForView(this.listView.getHiddenChildAt(a));
        }
        int count2 = this.listView.getCachedChildCount();
        for (int a2 = 0; a2 < count2; a2++) {
            updateColorForView(this.listView.getCachedChildAt(a2));
        }
        int count3 = this.listView.getAttachedScrapChildCount();
        for (int a3 = 0; a3 < count3; a3++) {
            updateColorForView(this.listView.getAttachedScrapChildAt(a3));
        }
        this.containerView.invalidate();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        this.isNeedReopen = false;
    }

    private void updateColorForView(View view) {
        if (view instanceof HeaderCell) {
            ((HeaderCell) view).getTextView().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        } else if (view instanceof LinkActionView) {
            ((LinkActionView) view).updateColors();
        } else if (view instanceof TextInfoPrivacyCell) {
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(view.getContext(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            combinedDrawable.setFullsize(true);
            view.setBackground(combinedDrawable);
            ((TextInfoPrivacyCell) view).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        } else if (view instanceof UserCell) {
            ((UserCell) view).update(0);
        }
        RecyclerView.ViewHolder holder = this.listView.getChildViewHolder(view);
        if (holder != null) {
            if (holder.getItemViewType() == 7) {
                Drawable shadowDrawable = Theme.getThemedDrawable(view.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
                Drawable background = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
                CombinedDrawable combinedDrawable2 = new CombinedDrawable(background, shadowDrawable, 0, 0);
                combinedDrawable2.setFullsize(true);
                view.setBackgroundDrawable(combinedDrawable2);
            } else if (holder.getItemViewType() == 2) {
                Drawable shadowDrawable2 = Theme.getThemedDrawable(view.getContext(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                Drawable background2 = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
                CombinedDrawable combinedDrawable3 = new CombinedDrawable(background2, shadowDrawable2, 0, 0);
                combinedDrawable3.setFullsize(true);
                view.setBackgroundDrawable(combinedDrawable3);
            }
        }
    }

    private void loadCreator() {
        TLRPC.TL_users_getUsers req = new TLRPC.TL_users_getUsers();
        req.id.add(MessagesController.getInstance(UserConfig.selectedAccount).getInputUser(this.invite.admin_id));
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$$ExternalSyntheticLambda2
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                InviteLinkBottomSheet.this.m2698x3644d1d(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadCreator$2$org-telegram-ui-Components-InviteLinkBottomSheet */
    public /* synthetic */ void m2698x3644d1d(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet.4
            @Override // java.lang.Runnable
            public void run() {
                if (error == null) {
                    TLRPC.Vector vector = (TLRPC.Vector) response;
                    TLRPC.User user = (TLRPC.User) vector.objects.get(0);
                    InviteLinkBottomSheet.this.users.put(Long.valueOf(InviteLinkBottomSheet.this.invite.admin_id), user);
                    InviteLinkBottomSheet.this.adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    private void updateRows() {
        boolean needLoadUsers = false;
        this.rowCount = 0;
        this.dividerRow = -1;
        this.divider2Row = -1;
        this.divider3Row = -1;
        this.joinedHeaderRow = -1;
        this.joinedStartRow = -1;
        this.joinedEndRow = -1;
        this.emptyView2 = -1;
        this.emptyView3 = -1;
        this.linkActionRow = -1;
        this.linkInfoRow = -1;
        this.emptyHintRow = -1;
        this.requestedHeaderRow = -1;
        this.requestedStartRow = -1;
        this.requestedEndRow = -1;
        this.loadingRow = -1;
        if (!this.permanent) {
            int i = 0 + 1;
            this.rowCount = i;
            this.linkActionRow = 0;
            this.rowCount = i + 1;
            this.linkInfoRow = i;
        }
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.creatorHeaderRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.creatorRow = i3;
        this.rowCount = i4 + 1;
        this.emptyView = i4;
        boolean needUsers = this.invite.usage > 0 || this.invite.usage_limit > 0 || this.invite.requested > 0;
        if (this.invite.usage > this.joinedUsers.size() || (this.invite.request_needed && this.invite.requested > this.requestedUsers.size())) {
            needLoadUsers = true;
        }
        boolean usersLoaded = false;
        if (!this.joinedUsers.isEmpty()) {
            int i5 = this.rowCount;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.dividerRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.joinedHeaderRow = i6;
            this.joinedStartRow = i7;
            int size = i7 + this.joinedUsers.size();
            this.rowCount = size;
            this.joinedEndRow = size;
            this.rowCount = size + 1;
            this.emptyView2 = size;
            usersLoaded = true;
        }
        if (!this.requestedUsers.isEmpty()) {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.divider2Row = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.requestedHeaderRow = i9;
            this.requestedStartRow = i10;
            int size2 = i10 + this.requestedUsers.size();
            this.rowCount = size2;
            this.requestedEndRow = size2;
            this.rowCount = size2 + 1;
            this.emptyView3 = size2;
            usersLoaded = true;
        }
        if ((needUsers || needLoadUsers) && !usersLoaded) {
            int i11 = this.rowCount;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.dividerRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.loadingRow = i12;
            this.rowCount = i13 + 1;
            this.emptyView2 = i13;
        }
        if (this.emptyHintRow == -1) {
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.divider3Row = i14;
        }
        this.adapter.notifyDataSetChanged();
    }

    /* loaded from: classes5.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
            InviteLinkBottomSheet.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == InviteLinkBottomSheet.this.creatorHeaderRow || position == InviteLinkBottomSheet.this.requestedHeaderRow || position == InviteLinkBottomSheet.this.joinedHeaderRow) {
                return 0;
            }
            if (position == InviteLinkBottomSheet.this.creatorRow) {
                return 1;
            }
            if (position >= InviteLinkBottomSheet.this.requestedStartRow && position < InviteLinkBottomSheet.this.requestedEndRow) {
                return 1;
            }
            if (position >= InviteLinkBottomSheet.this.joinedStartRow && position < InviteLinkBottomSheet.this.joinedEndRow) {
                return 1;
            }
            if (position == InviteLinkBottomSheet.this.dividerRow || position == InviteLinkBottomSheet.this.divider2Row) {
                return 2;
            }
            if (position == InviteLinkBottomSheet.this.linkActionRow) {
                return 3;
            }
            if (position == InviteLinkBottomSheet.this.linkInfoRow) {
                return 4;
            }
            if (position == InviteLinkBottomSheet.this.loadingRow) {
                return 5;
            }
            if (position == InviteLinkBottomSheet.this.emptyView || position == InviteLinkBottomSheet.this.emptyView2 || position == InviteLinkBottomSheet.this.emptyView3) {
                return 6;
            }
            if (position == InviteLinkBottomSheet.this.divider3Row) {
                return 7;
            }
            return position == InviteLinkBottomSheet.this.emptyHintRow ? 8 : 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 1:
                    view = new UserCell(context, 12, 0, true);
                    break;
                case 2:
                    view = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_windowBackgroundGray));
                    break;
                case 3:
                    BaseFragment baseFragment = InviteLinkBottomSheet.this.fragment;
                    InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
                    LinkActionView linkActionView = new LinkActionView(context, baseFragment, inviteLinkBottomSheet, inviteLinkBottomSheet.chatId, false, InviteLinkBottomSheet.this.isChannel);
                    linkActionView.setDelegate(new AnonymousClass1());
                    linkActionView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = linkActionView;
                    break;
                case 4:
                    View view2 = new TimerPrivacyCell(context);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable.setFullsize(true);
                    view2.setBackground(combinedDrawable);
                    view = view2;
                    break;
                case 5:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(10);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setPaddingLeft(AndroidUtilities.dp(10.0f));
                    view = flickerLoadingView;
                    break;
                case 6:
                    view = new View(context) { // from class: org.telegram.ui.Components.InviteLinkBottomSheet.Adapter.2
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(5.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 7:
                    View view3 = new ShadowSectionCell(context, 12);
                    Drawable shadowDrawable = Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
                    Drawable background = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
                    CombinedDrawable combinedDrawable2 = new CombinedDrawable(background, shadowDrawable, 0, 0);
                    combinedDrawable2.setFullsize(true);
                    view3.setBackgroundDrawable(combinedDrawable2);
                    view = view3;
                    break;
                case 8:
                    view = new EmptyHintRow(context);
                    break;
                default:
                    HeaderCell headerCell = new HeaderCell(context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, true);
                    headerCell.getTextView2().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                    headerCell.getTextView2().setTextSize(15);
                    headerCell.getTextView2().setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    view = headerCell;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1 */
        /* loaded from: classes5.dex */
        public class AnonymousClass1 implements LinkActionView.Delegate {
            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public /* synthetic */ void showUsersForPermanentLink() {
                LinkActionView.Delegate.CC.$default$showUsersForPermanentLink(this);
            }

            AnonymousClass1() {
                Adapter.this = this$1;
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public void revokeLink() {
                if (InviteLinkBottomSheet.this.fragment instanceof ManageLinksActivity) {
                    ((ManageLinksActivity) InviteLinkBottomSheet.this.fragment).revokeLink(InviteLinkBottomSheet.this.invite);
                } else {
                    TLRPC.TL_messages_editExportedChatInvite req = new TLRPC.TL_messages_editExportedChatInvite();
                    req.link = InviteLinkBottomSheet.this.invite.link;
                    req.revoked = true;
                    req.peer = MessagesController.getInstance(InviteLinkBottomSheet.this.currentAccount).getInputPeer(-InviteLinkBottomSheet.this.chatId);
                    ConnectionsManager.getInstance(InviteLinkBottomSheet.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda3
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            InviteLinkBottomSheet.Adapter.AnonymousClass1.this.m2706x3dc68cc2(tLObject, tL_error);
                        }
                    });
                }
                InviteLinkBottomSheet.this.dismiss();
            }

            /* renamed from: lambda$revokeLink$1$org-telegram-ui-Components-InviteLinkBottomSheet$Adapter$1 */
            public /* synthetic */ void m2706x3dc68cc2(final TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteLinkBottomSheet.Adapter.AnonymousClass1.this.m2705x37c2c163(error, response);
                    }
                });
            }

            /* renamed from: lambda$revokeLink$0$org-telegram-ui-Components-InviteLinkBottomSheet$Adapter$1 */
            public /* synthetic */ void m2705x37c2c163(TLRPC.TL_error error, TLObject response) {
                if (error == null) {
                    if (response instanceof TLRPC.TL_messages_exportedChatInviteReplaced) {
                        TLRPC.TL_messages_exportedChatInviteReplaced replaced = (TLRPC.TL_messages_exportedChatInviteReplaced) response;
                        if (InviteLinkBottomSheet.this.info != null) {
                            InviteLinkBottomSheet.this.info.exported_invite = (TLRPC.TL_chatInviteExported) replaced.new_invite;
                        }
                        if (InviteLinkBottomSheet.this.inviteDelegate != null) {
                            InviteLinkBottomSheet.this.inviteDelegate.permanentLinkReplaced(InviteLinkBottomSheet.this.invite, InviteLinkBottomSheet.this.info.exported_invite);
                            return;
                        }
                        return;
                    }
                    if (InviteLinkBottomSheet.this.info != null) {
                        TLRPC.ChatFull chatFull = InviteLinkBottomSheet.this.info;
                        chatFull.invitesCount--;
                        if (InviteLinkBottomSheet.this.info.invitesCount < 0) {
                            InviteLinkBottomSheet.this.info.invitesCount = 0;
                        }
                        MessagesStorage.getInstance(InviteLinkBottomSheet.this.currentAccount).saveChatLinksCount(InviteLinkBottomSheet.this.chatId, InviteLinkBottomSheet.this.info.invitesCount);
                    }
                    if (InviteLinkBottomSheet.this.inviteDelegate != null) {
                        InviteLinkBottomSheet.this.inviteDelegate.linkRevoked(InviteLinkBottomSheet.this.invite);
                    }
                }
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public void editLink() {
                if (InviteLinkBottomSheet.this.fragment instanceof ManageLinksActivity) {
                    ((ManageLinksActivity) InviteLinkBottomSheet.this.fragment).editLink(InviteLinkBottomSheet.this.invite);
                } else {
                    LinkEditActivity activity = new LinkEditActivity(1, InviteLinkBottomSheet.this.chatId);
                    activity.setInviteToEdit(InviteLinkBottomSheet.this.invite);
                    activity.setCallback(new LinkEditActivity.Callback() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet.Adapter.1.1
                        @Override // org.telegram.ui.LinkEditActivity.Callback
                        public void onLinkCreated(TLObject response) {
                        }

                        @Override // org.telegram.ui.LinkEditActivity.Callback
                        public void onLinkEdited(TLRPC.TL_chatInviteExported inviteToEdit, TLObject response) {
                            if (InviteLinkBottomSheet.this.inviteDelegate != null) {
                                InviteLinkBottomSheet.this.inviteDelegate.onLinkEdited(inviteToEdit);
                            }
                        }

                        @Override // org.telegram.ui.LinkEditActivity.Callback
                        public void onLinkRemoved(TLRPC.TL_chatInviteExported inviteFinal) {
                        }

                        @Override // org.telegram.ui.LinkEditActivity.Callback
                        public void revokeLink(TLRPC.TL_chatInviteExported inviteFinal) {
                        }
                    });
                    InviteLinkBottomSheet.this.fragment.presentFragment(activity);
                }
                InviteLinkBottomSheet.this.dismiss();
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public void removeLink() {
                if (InviteLinkBottomSheet.this.fragment instanceof ManageLinksActivity) {
                    ((ManageLinksActivity) InviteLinkBottomSheet.this.fragment).deleteLink(InviteLinkBottomSheet.this.invite);
                } else {
                    TLRPC.TL_messages_deleteExportedChatInvite req = new TLRPC.TL_messages_deleteExportedChatInvite();
                    req.link = InviteLinkBottomSheet.this.invite.link;
                    req.peer = MessagesController.getInstance(InviteLinkBottomSheet.this.currentAccount).getInputPeer(-InviteLinkBottomSheet.this.chatId);
                    ConnectionsManager.getInstance(InviteLinkBottomSheet.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda2
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            InviteLinkBottomSheet.Adapter.AnonymousClass1.this.m2704x3f406942(tLObject, tL_error);
                        }
                    });
                }
                InviteLinkBottomSheet.this.dismiss();
            }

            /* renamed from: lambda$removeLink$3$org-telegram-ui-Components-InviteLinkBottomSheet$Adapter$1 */
            public /* synthetic */ void m2704x3f406942(TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteLinkBottomSheet.Adapter.AnonymousClass1.this.m2703x393c9de3(error);
                    }
                });
            }

            /* renamed from: lambda$removeLink$2$org-telegram-ui-Components-InviteLinkBottomSheet$Adapter$1 */
            public /* synthetic */ void m2703x393c9de3(TLRPC.TL_error error) {
                if (error == null && InviteLinkBottomSheet.this.inviteDelegate != null) {
                    InviteLinkBottomSheet.this.inviteDelegate.onLinkDeleted(InviteLinkBottomSheet.this.invite);
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.User user;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == InviteLinkBottomSheet.this.creatorHeaderRow) {
                        headerCell.setText(LocaleController.getString("LinkCreatedeBy", R.string.LinkCreatedeBy));
                        headerCell.setText2(null);
                        return;
                    } else if (position == InviteLinkBottomSheet.this.joinedHeaderRow) {
                        if (InviteLinkBottomSheet.this.invite.usage > 0) {
                            headerCell.setText(LocaleController.formatPluralString("PeopleJoined", InviteLinkBottomSheet.this.invite.usage, new Object[0]));
                        } else {
                            headerCell.setText(LocaleController.getString("NoOneJoined", R.string.NoOneJoined));
                        }
                        if (!InviteLinkBottomSheet.this.invite.expired && !InviteLinkBottomSheet.this.invite.revoked && InviteLinkBottomSheet.this.invite.usage_limit > 0 && InviteLinkBottomSheet.this.invite.usage > 0) {
                            headerCell.setText2(LocaleController.formatPluralString("PeopleJoinedRemaining", InviteLinkBottomSheet.this.invite.usage_limit - InviteLinkBottomSheet.this.invite.usage, new Object[0]));
                            return;
                        } else {
                            headerCell.setText2(null);
                            return;
                        }
                    } else if (position == InviteLinkBottomSheet.this.requestedHeaderRow) {
                        headerCell.setText(LocaleController.formatPluralString("JoinRequests", InviteLinkBottomSheet.this.invite.requested, new Object[0]));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    UserCell userCell = (UserCell) holder.itemView;
                    String role = null;
                    String status = null;
                    if (position == InviteLinkBottomSheet.this.creatorRow) {
                        user = InviteLinkBottomSheet.this.users.get(Long.valueOf(InviteLinkBottomSheet.this.invite.admin_id));
                        if (user == null) {
                            user = MessagesController.getInstance(InviteLinkBottomSheet.this.currentAccount).getUser(Long.valueOf(InviteLinkBottomSheet.this.invite.admin_id));
                        }
                        if (user != null) {
                            status = LocaleController.formatDateAudio(InviteLinkBottomSheet.this.invite.date, false);
                        }
                        if (InviteLinkBottomSheet.this.info != null && user != null && InviteLinkBottomSheet.this.info.participants != null) {
                            int i = 0;
                            while (true) {
                                if (i < InviteLinkBottomSheet.this.info.participants.participants.size()) {
                                    if (InviteLinkBottomSheet.this.info.participants.participants.get(i).user_id != user.id) {
                                        i++;
                                    } else {
                                        TLRPC.ChatParticipant part = InviteLinkBottomSheet.this.info.participants.participants.get(i);
                                        if (part instanceof TLRPC.TL_chatChannelParticipant) {
                                            TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) part).channelParticipant;
                                            role = !TextUtils.isEmpty(channelParticipant.rank) ? channelParticipant.rank : channelParticipant instanceof TLRPC.TL_channelParticipantCreator ? LocaleController.getString("ChannelCreator", R.string.ChannelCreator) : channelParticipant instanceof TLRPC.TL_channelParticipantAdmin ? LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin) : null;
                                        } else {
                                            role = part instanceof TLRPC.TL_chatParticipantCreator ? LocaleController.getString("ChannelCreator", R.string.ChannelCreator) : part instanceof TLRPC.TL_chatParticipantAdmin ? LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin) : null;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        int startRow = InviteLinkBottomSheet.this.joinedStartRow;
                        List<TLRPC.TL_chatInviteImporter> usersList = InviteLinkBottomSheet.this.joinedUsers;
                        if (InviteLinkBottomSheet.this.requestedStartRow != -1 && position >= InviteLinkBottomSheet.this.requestedStartRow) {
                            startRow = InviteLinkBottomSheet.this.requestedStartRow;
                            usersList = InviteLinkBottomSheet.this.requestedUsers;
                        }
                        TLRPC.TL_chatInviteImporter invitedUser = usersList.get(position - startRow);
                        user = InviteLinkBottomSheet.this.users.get(Long.valueOf(invitedUser.user_id));
                    }
                    userCell.setAdminRole(role);
                    userCell.setData(user, null, status, 0, false);
                    return;
                case 2:
                case 5:
                case 6:
                case 7:
                default:
                    return;
                case 3:
                    LinkActionView actionView = (LinkActionView) holder.itemView;
                    actionView.setUsers(0, null);
                    actionView.setLink(InviteLinkBottomSheet.this.invite.link);
                    actionView.setRevoke(InviteLinkBottomSheet.this.invite.revoked);
                    actionView.setPermanent(InviteLinkBottomSheet.this.invite.permanent);
                    actionView.setCanEdit(InviteLinkBottomSheet.this.canEdit);
                    actionView.hideRevokeOption(!InviteLinkBottomSheet.this.canEdit);
                    return;
                case 4:
                    TimerPrivacyCell privacyCell = (TimerPrivacyCell) holder.itemView;
                    privacyCell.cancelTimer();
                    privacyCell.timer = false;
                    privacyCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                    privacyCell.setFixedSize(0);
                    if (InviteLinkBottomSheet.this.invite.revoked) {
                        privacyCell.setText(LocaleController.getString("LinkIsNoActive", R.string.LinkIsNoActive));
                        return;
                    } else if (InviteLinkBottomSheet.this.invite.expired) {
                        if (InviteLinkBottomSheet.this.invite.usage_limit > 0 && InviteLinkBottomSheet.this.invite.usage_limit == InviteLinkBottomSheet.this.invite.usage) {
                            privacyCell.setText(LocaleController.getString("LinkIsExpiredLimitReached", R.string.LinkIsExpiredLimitReached));
                            return;
                        }
                        privacyCell.setText(LocaleController.getString("LinkIsExpired", R.string.LinkIsExpired));
                        privacyCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                        return;
                    } else if (InviteLinkBottomSheet.this.invite.expire_date > 0) {
                        long currentTime = System.currentTimeMillis() + (InviteLinkBottomSheet.this.timeDif * 1000);
                        long expireTime = InviteLinkBottomSheet.this.invite.expire_date * 1000;
                        long timeLeft = expireTime - currentTime;
                        if (timeLeft < 0) {
                            timeLeft = 0;
                        }
                        if (timeLeft > 86400000) {
                            String time = LocaleController.formatDateAudio(InviteLinkBottomSheet.this.invite.expire_date, false);
                            privacyCell.setText(LocaleController.formatString("LinkExpiresIn", R.string.LinkExpiresIn, time));
                            return;
                        }
                        int s = (int) ((timeLeft / 1000) % 60);
                        int m = (int) (((timeLeft / 1000) / 60) % 60);
                        int h = (int) (((timeLeft / 1000) / 60) / 60);
                        String time2 = String.format(Locale.ENGLISH, "%02d", Integer.valueOf(h)) + String.format(Locale.ENGLISH, ":%02d", Integer.valueOf(m)) + String.format(Locale.ENGLISH, ":%02d", Integer.valueOf(s));
                        privacyCell.timer = true;
                        privacyCell.runTimer();
                        privacyCell.setText(LocaleController.formatString("LinkExpiresInTime", R.string.LinkExpiresInTime, time2));
                        return;
                    } else {
                        privacyCell.setFixedSize(12);
                        privacyCell.setText(null);
                        return;
                    }
                case 8:
                    EmptyHintRow emptyHintRow = (EmptyHintRow) holder.itemView;
                    if (InviteLinkBottomSheet.this.invite.usage_limit > 0) {
                        emptyHintRow.textView.setText(LocaleController.formatPluralString("PeopleCanJoinViaLinkCount", InviteLinkBottomSheet.this.invite.usage_limit, new Object[0]));
                        emptyHintRow.textView.setVisibility(0);
                        return;
                    }
                    emptyHintRow.textView.setVisibility(8);
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return InviteLinkBottomSheet.this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == InviteLinkBottomSheet.this.creatorRow ? InviteLinkBottomSheet.this.invite.admin_id != UserConfig.getInstance(InviteLinkBottomSheet.this.currentAccount).clientUserId : (position >= InviteLinkBottomSheet.this.joinedStartRow && position < InviteLinkBottomSheet.this.joinedEndRow) || (position >= InviteLinkBottomSheet.this.requestedStartRow && position < InviteLinkBottomSheet.this.requestedEndRow);
        }
    }

    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.titleTextView.setTranslationY(this.scrollOffsetY);
            this.shadow.setTranslationY(this.scrollOffsetY);
            this.containerView.invalidate();
            return;
        }
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
            runShadowAnimation(false);
        } else {
            runShadowAnimation(true);
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            TextView textView = this.titleTextView;
            if (textView != null) {
                textView.setTranslationY(this.scrollOffsetY);
            }
            this.shadow.setTranslationY(this.scrollOffsetY);
            this.containerView.invalidate();
        }
    }

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
                this.shadow.setVisibility(0);
                this.titleTextView.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            if (!this.titleVisible) {
                AnimatorSet animatorSet3 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                TextView textView = this.titleTextView;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr2[0] = ObjectAnimator.ofFloat(textView, property2, fArr2);
                animatorSet3.playTogether(animatorArr2);
            }
            this.shadowAnimation.setDuration(150L);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (InviteLinkBottomSheet.this.shadowAnimation != null && InviteLinkBottomSheet.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            InviteLinkBottomSheet.this.shadow.setVisibility(4);
                        }
                        InviteLinkBottomSheet.this.shadowAnimation = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (InviteLinkBottomSheet.this.shadowAnimation != null && InviteLinkBottomSheet.this.shadowAnimation.equals(animation)) {
                        InviteLinkBottomSheet.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void loadUsers() {
        final boolean loadRequestedUsers;
        if (this.usersLoading) {
            return;
        }
        boolean z = false;
        boolean hasMoreJoinedUsers = this.invite.usage > this.joinedUsers.size();
        if (this.invite.request_needed && this.invite.requested > this.requestedUsers.size()) {
            z = true;
        }
        final boolean hasMoreRequestedUsers = z;
        if (hasMoreJoinedUsers) {
            loadRequestedUsers = false;
        } else if (hasMoreRequestedUsers) {
            loadRequestedUsers = true;
        } else {
            return;
        }
        final List<TLRPC.TL_chatInviteImporter> importersList = loadRequestedUsers ? this.requestedUsers : this.joinedUsers;
        TLRPC.TL_messages_getChatInviteImporters req = new TLRPC.TL_messages_getChatInviteImporters();
        req.flags |= 2;
        req.link = this.invite.link;
        req.peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer(-this.chatId);
        req.requested = loadRequestedUsers;
        if (importersList.isEmpty()) {
            req.offset_user = new TLRPC.TL_inputUserEmpty();
        } else {
            TLRPC.TL_chatInviteImporter invitedUser = importersList.get(importersList.size() - 1);
            req.offset_user = MessagesController.getInstance(this.currentAccount).getInputUser(this.users.get(Long.valueOf(invitedUser.user_id)));
            req.offset_date = invitedUser.date;
        }
        this.usersLoading = true;
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                InviteLinkBottomSheet.this.m2700x3da8babf(importersList, loadRequestedUsers, hasMoreRequestedUsers, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadUsers$4$org-telegram-ui-Components-InviteLinkBottomSheet */
    public /* synthetic */ void m2700x3da8babf(final List importersList, final boolean loadRequestedUsers, final boolean hasMoreRequestedUsers, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InviteLinkBottomSheet.this.m2699x4bff14a0(error, response, importersList, loadRequestedUsers, hasMoreRequestedUsers);
            }
        });
    }

    /* renamed from: lambda$loadUsers$3$org-telegram-ui-Components-InviteLinkBottomSheet */
    public /* synthetic */ void m2699x4bff14a0(TLRPC.TL_error error, TLObject response, List importersList, boolean loadRequestedUsers, boolean hasMoreRequestedUsers) {
        if (error == null) {
            TLRPC.TL_messages_chatInviteImporters inviteImporters = (TLRPC.TL_messages_chatInviteImporters) response;
            importersList.addAll(inviteImporters.importers);
            for (int i = 0; i < inviteImporters.users.size(); i++) {
                TLRPC.User user = inviteImporters.users.get(i);
                this.users.put(Long.valueOf(user.id), user);
            }
            boolean z = true;
            if (loadRequestedUsers) {
                if (importersList.size() >= inviteImporters.count) {
                    z = false;
                }
            } else if (importersList.size() >= inviteImporters.count && !hasMoreRequestedUsers) {
                z = false;
            }
            this.hasMore = z;
            updateRows();
        }
        this.usersLoading = false;
    }

    public void setInviteDelegate(InviteDelegate inviteDelegate) {
        this.inviteDelegate = inviteDelegate;
    }

    /* loaded from: classes5.dex */
    public class TimerPrivacyCell extends TextInfoPrivacyCell {
        boolean timer;
        Runnable timerRunnable = new Runnable() { // from class: org.telegram.ui.Components.InviteLinkBottomSheet.TimerPrivacyCell.1
            @Override // java.lang.Runnable
            public void run() {
                int p;
                if (InviteLinkBottomSheet.this.listView != null && InviteLinkBottomSheet.this.listView.getAdapter() != null && (p = InviteLinkBottomSheet.this.listView.getChildAdapterPosition(TimerPrivacyCell.this)) >= 0) {
                    InviteLinkBottomSheet.this.adapter.onBindViewHolder(InviteLinkBottomSheet.this.listView.getChildViewHolder(TimerPrivacyCell.this), p);
                }
                AndroidUtilities.runOnUIThread(this);
            }
        };

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TimerPrivacyCell(Context context) {
            super(context);
            InviteLinkBottomSheet.this = r1;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            runTimer();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            cancelTimer();
        }

        public void cancelTimer() {
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
        }

        public void runTimer() {
            cancelTimer();
            if (this.timer) {
                AndroidUtilities.runOnUIThread(this.timerRunnable, 500L);
            }
        }
    }

    /* loaded from: classes5.dex */
    public class EmptyHintRow extends FrameLayout {
        TextView textView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmptyHintRow(Context context) {
            super(context);
            InviteLinkBottomSheet.this = r8;
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 14.0f);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            this.textView.setGravity(1);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 16, 60.0f, 0.0f, 60.0f, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
