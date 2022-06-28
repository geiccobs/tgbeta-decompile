package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import j$.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class ReactedUsersListView extends FrameLayout {
    public static final int ITEM_HEIGHT_DP = 48;
    public static final int VISIBLE_ITEMS = 6;
    private RecyclerView.Adapter adapter;
    private int currentAccount;
    private String filter;
    public boolean isLoaded;
    public boolean isLoading;
    public RecyclerListView listView;
    private FlickerLoadingView loadingView;
    private MessageObject message;
    private String offset;
    private OnHeightChangedListener onHeightChangedListener;
    private OnProfileSelectedListener onProfileSelectedListener;
    private boolean onlySeenNow;
    private int predictiveCount;
    private List<TLRPC.TL_messagePeerReaction> userReactions = new ArrayList();
    private LongSparseArray<TLRPC.TL_messagePeerReaction> peerReactionMap = new LongSparseArray<>();
    public boolean canLoadMore = true;

    /* loaded from: classes5.dex */
    public interface OnHeightChangedListener {
        void onHeightChanged(ReactedUsersListView reactedUsersListView, int i);
    }

    /* loaded from: classes5.dex */
    public interface OnProfileSelectedListener {
        void onProfileSelected(ReactedUsersListView reactedUsersListView, long j);
    }

    public ReactedUsersListView(final Context context, Theme.ResourcesProvider resourcesProvider, int currentAccount, MessageObject message, TLRPC.TL_reactionCount reactionCount, boolean addPadding) {
        super(context);
        this.currentAccount = currentAccount;
        this.message = message;
        this.filter = reactionCount == null ? null : reactionCount.reaction;
        this.predictiveCount = reactionCount == null ? 6 : reactionCount.count;
        this.listView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ReactedUsersListView.1
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int widthSpec, int heightSpec) {
                super.onMeasure(widthSpec, heightSpec);
                ReactedUsersListView.this.updateHeight();
            }
        };
        final LinearLayoutManager llm = new LinearLayoutManager(context);
        this.listView.setLayoutManager(llm);
        if (addPadding) {
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
            this.listView.setClipToPadding(false);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            this.listView.setVerticalScrollbarThumbDrawable(new ColorDrawable(Theme.getColor(Theme.key_listSelector)));
        }
        RecyclerListView recyclerListView = this.listView;
        RecyclerView.Adapter adapter = new RecyclerView.Adapter() { // from class: org.telegram.ui.Components.ReactedUsersListView.2
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new ReactedUserHolderView(context));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ReactedUserHolderView rhv = (ReactedUserHolderView) holder.itemView;
                rhv.setUserReaction((TLRPC.TL_messagePeerReaction) ReactedUsersListView.this.userReactions.get(position));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return ReactedUsersListView.this.userReactions.size();
            }
        };
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ReactedUsersListView.this.m2943lambda$new$0$orgtelegramuiComponentsReactedUsersListView(view, i);
            }
        });
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ReactedUsersListView.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (ReactedUsersListView.this.isLoaded && ReactedUsersListView.this.canLoadMore && !ReactedUsersListView.this.isLoading && llm.findLastVisibleItemPosition() >= (ReactedUsersListView.this.adapter.getItemCount() - 1) - ReactedUsersListView.this.getLoadCount()) {
                    ReactedUsersListView.this.load();
                }
            }
        });
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setAlpha(0.0f);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, resourcesProvider);
        this.loadingView = flickerLoadingView;
        flickerLoadingView.setViewType(16);
        this.loadingView.setIsSingleCell(true);
        this.loadingView.setItemsCount(this.predictiveCount);
        addView(this.loadingView, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ReactedUsersListView */
    public /* synthetic */ void m2943lambda$new$0$orgtelegramuiComponentsReactedUsersListView(View view, int position) {
        OnProfileSelectedListener onProfileSelectedListener = this.onProfileSelectedListener;
        if (onProfileSelectedListener != null) {
            onProfileSelectedListener.onProfileSelected(this, MessageObject.getPeerId(this.userReactions.get(position).peer_id));
        }
    }

    public ReactedUsersListView setSeenUsers(List<TLRPC.User> users) {
        List<TLRPC.TL_messagePeerReaction> nr = new ArrayList<>(users.size());
        for (TLRPC.User u : users) {
            if (this.peerReactionMap.get(u.id) == null) {
                TLRPC.TL_messagePeerReaction r = new TLRPC.TL_messagePeerReaction();
                r.reaction = null;
                r.peer_id = new TLRPC.TL_peerUser();
                r.peer_id.user_id = u.id;
                this.peerReactionMap.put(MessageObject.getPeerId(r.peer_id), r);
                nr.add(r);
            }
        }
        if (this.userReactions.isEmpty()) {
            this.onlySeenNow = true;
        }
        this.userReactions.addAll(nr);
        this.adapter.notifyDataSetChanged();
        updateHeight();
        return this;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isLoaded && !this.isLoading) {
            load();
        }
    }

    public void load() {
        this.isLoading = true;
        MessagesController ctrl = MessagesController.getInstance(this.currentAccount);
        TLRPC.TL_messages_getMessageReactionsList getList = new TLRPC.TL_messages_getMessageReactionsList();
        getList.peer = ctrl.getInputPeer(this.message.getDialogId());
        getList.id = this.message.getId();
        getList.limit = getLoadCount();
        getList.reaction = this.filter;
        getList.offset = this.offset;
        if (this.filter != null) {
            getList.flags = 1 | getList.flags;
        }
        if (this.offset != null) {
            getList.flags |= 2;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getList, new RequestDelegate() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda4
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ReactedUsersListView.this.m2942lambda$load$5$orgtelegramuiComponentsReactedUsersListView(tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$load$4$org-telegram-ui-Components-ReactedUsersListView */
    public /* synthetic */ void m2941lambda$load$4$orgtelegramuiComponentsReactedUsersListView(final TLObject response) {
        NotificationCenter.getInstance(this.currentAccount).doOnIdle(new Runnable() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ReactedUsersListView.this.m2940lambda$load$3$orgtelegramuiComponentsReactedUsersListView(response);
            }
        });
    }

    /* renamed from: lambda$load$5$org-telegram-ui-Components-ReactedUsersListView */
    public /* synthetic */ void m2942lambda$load$5$orgtelegramuiComponentsReactedUsersListView(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ReactedUsersListView.this.m2941lambda$load$4$orgtelegramuiComponentsReactedUsersListView(response);
            }
        });
    }

    /* renamed from: lambda$load$3$org-telegram-ui-Components-ReactedUsersListView */
    public /* synthetic */ void m2940lambda$load$3$orgtelegramuiComponentsReactedUsersListView(TLObject response) {
        if (response instanceof TLRPC.TL_messages_messageReactionsList) {
            TLRPC.TL_messages_messageReactionsList res = (TLRPC.TL_messages_messageReactionsList) response;
            Iterator<TLRPC.User> it = res.users.iterator();
            while (it.hasNext()) {
                TLRPC.User u = it.next();
                MessagesController.getInstance(this.currentAccount).putUser(u, false);
            }
            for (int i = 0; i < res.reactions.size(); i++) {
                this.userReactions.add(res.reactions.get(i));
                long peerId = MessageObject.getPeerId(res.reactions.get(i).peer_id);
                TLRPC.TL_messagePeerReaction reaction = this.peerReactionMap.get(peerId);
                if (reaction != null) {
                    this.userReactions.remove(reaction);
                }
                this.peerReactionMap.put(peerId, res.reactions.get(i));
            }
            if (this.onlySeenNow) {
                Collections.sort(this.userReactions, Comparator.CC.comparingInt(ReactedUsersListView$$ExternalSyntheticLambda3.INSTANCE));
            }
            if (this.onlySeenNow) {
                this.onlySeenNow = false;
            }
            this.adapter.notifyDataSetChanged();
            if (!this.isLoaded) {
                ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
                anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ReactedUsersListView.this.m2939lambda$load$2$orgtelegramuiComponentsReactedUsersListView(valueAnimator);
                    }
                });
                anim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ReactedUsersListView.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ReactedUsersListView.this.loadingView.setVisibility(8);
                    }
                });
                anim.start();
                updateHeight();
                this.isLoaded = true;
            }
            String str = res.next_offset;
            this.offset = str;
            if (str == null) {
                this.canLoadMore = false;
            }
            this.isLoading = false;
            return;
        }
        this.isLoading = false;
    }

    public static /* synthetic */ int lambda$load$1(TLRPC.TL_messagePeerReaction o) {
        return o.reaction != null ? 0 : 1;
    }

    /* renamed from: lambda$load$2$org-telegram-ui-Components-ReactedUsersListView */
    public /* synthetic */ void m2939lambda$load$2$orgtelegramuiComponentsReactedUsersListView(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.listView.setAlpha(val);
        this.loadingView.setAlpha(1.0f - val);
    }

    public void updateHeight() {
        int h;
        if (this.onHeightChangedListener != null) {
            int count = this.userReactions.size();
            if (count == 0) {
                count = this.predictiveCount;
            }
            if (this.listView.getMeasuredHeight() != 0) {
                h = Math.min(this.listView.getMeasuredHeight(), AndroidUtilities.dp(count * 48));
            } else {
                int h2 = count * 48;
                h = AndroidUtilities.dp(h2);
            }
            this.onHeightChangedListener.onHeightChanged(this, h);
        }
    }

    public int getLoadCount() {
        return this.filter == null ? 100 : 50;
    }

    /* loaded from: classes5.dex */
    private final class ReactedUserHolderView extends FrameLayout {
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        BackupImageView avatarView;
        View overlaySelectorView;
        BackupImageView reactView;
        TextView titleView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ReactedUserHolderView(Context context) {
            super(context);
            ReactedUsersListView.this = r10;
            setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(48.0f)));
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
            addView(this.avatarView, LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388627, 8.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setLines(1);
            this.titleView.setTextSize(1, 16.0f);
            this.titleView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
            this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            this.titleView.setImportantForAccessibility(2);
            addView(this.titleView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 58.0f, 0.0f, 44.0f, 0.0f));
            BackupImageView backupImageView2 = new BackupImageView(context);
            this.reactView = backupImageView2;
            addView(backupImageView2, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388629, 0.0f, 0.0f, 12.0f, 0.0f));
            View view = new View(context);
            this.overlaySelectorView = view;
            view.setBackground(Theme.getSelectorDrawable(false));
            addView(this.overlaySelectorView, LayoutHelper.createFrame(-1, -1.0f));
        }

        void setUserReaction(TLRPC.TL_messagePeerReaction reaction) {
            TLRPC.User u = MessagesController.getInstance(ReactedUsersListView.this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(reaction.peer_id)));
            if (u == null) {
                return;
            }
            this.avatarDrawable.setInfo(u);
            this.titleView.setText(UserObject.getUserName(u));
            Drawable thumb = this.avatarDrawable;
            if (u.photo != null && u.photo.strippedBitmap != null) {
                thumb = u.photo.strippedBitmap;
            }
            this.avatarView.setImage(ImageLocation.getForUser(u, 1), "50_50", thumb, u);
            if (reaction.reaction != null) {
                TLRPC.TL_availableReaction r = MediaDataController.getInstance(ReactedUsersListView.this.currentAccount).getReactionsMap().get(reaction.reaction);
                if (r != null) {
                    SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(r.static_icon.thumbs, Theme.key_windowBackgroundGray, 1.0f);
                    this.reactView.setImage(ImageLocation.getForDocument(r.center_icon), "40_40_lastframe", "webp", svgThumb, r);
                } else {
                    this.reactView.setImageDrawable(null);
                }
                setContentDescription(LocaleController.formatString("AccDescrReactedWith", R.string.AccDescrReactedWith, UserObject.getUserName(u), reaction.reaction));
                return;
            }
            this.reactView.setImageDrawable(null);
            setContentDescription(LocaleController.formatString("AccDescrPersonHasSeen", R.string.AccDescrPersonHasSeen, UserObject.getUserName(u)));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
        }
    }

    public ReactedUsersListView setOnProfileSelectedListener(OnProfileSelectedListener onProfileSelectedListener) {
        this.onProfileSelectedListener = onProfileSelectedListener;
        return this;
    }

    public ReactedUsersListView setOnHeightChangedListener(OnHeightChangedListener onHeightChangedListener) {
        this.onHeightChangedListener = onHeightChangedListener;
        return this;
    }

    public void setPredictiveCount(int predictiveCount) {
        this.predictiveCount = predictiveCount;
        this.loadingView.setItemsCount(predictiveCount);
    }

    /* loaded from: classes5.dex */
    public static class ContainerLinerLayout extends LinearLayout {
        public boolean hasHeader;

        public ContainerLinerLayout(Context context) {
            super(context);
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int maxWidth = 0;
            RecyclerListView listView = null;
            if (!this.hasHeader) {
                for (int k = 0; k < getChildCount(); k++) {
                    if (getChildAt(k) instanceof ReactedUsersListView) {
                        listView = ((ReactedUsersListView) getChildAt(k)).listView;
                        if (listView.getAdapter().getItemCount() == listView.getChildCount()) {
                            int count = listView.getChildCount();
                            for (int i = 0; i < count; i++) {
                                listView.getChildAt(i).measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), 0), heightMeasureSpec);
                                if (listView.getChildAt(i).getMeasuredWidth() > maxWidth) {
                                    maxWidth = listView.getChildAt(i).getMeasuredWidth();
                                }
                            }
                            maxWidth += AndroidUtilities.dp(16.0f);
                        }
                    }
                }
            }
            int size = View.MeasureSpec.getSize(widthMeasureSpec);
            if (size < AndroidUtilities.dp(240.0f)) {
                size = AndroidUtilities.dp(240.0f);
            }
            if (size > AndroidUtilities.dp(280.0f)) {
                size = AndroidUtilities.dp(280.0f);
            }
            if (size < 0) {
                size = 0;
            }
            if (maxWidth != 0 && maxWidth < size) {
                size = maxWidth;
            }
            if (listView != null) {
                for (int i2 = 0; i2 < listView.getChildCount(); i2++) {
                    listView.getChildAt(i2).measure(View.MeasureSpec.makeMeasureSpec(size, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                }
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
        }
    }
}
