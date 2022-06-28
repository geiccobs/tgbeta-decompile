package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class SenderSelectPopup extends ActionBarPopupWindow {
    public static final int AVATAR_SIZE_DP = 40;
    private static final float SCALE_START = 0.25f;
    private static final int SHADOW_DURATION = 150;
    public static final float SPRING_STIFFNESS = 750.0f;
    private TLRPC.ChatFull chatFull;
    private boolean clicked;
    public View dimView;
    private boolean dismissed;
    private View headerShadow;
    public TextView headerText;
    private Boolean isHeaderShadowVisible;
    private LinearLayoutManager layoutManager;
    public LinearLayout recyclerContainer;
    private RecyclerListView recyclerView;
    protected boolean runningCustomSprings;
    private FrameLayout scrimPopupContainerLayout;
    private TLRPC.TL_channels_sendAsPeers sendAsPeers;
    protected List<SpringAnimation> springAnimations = new ArrayList();

    /* loaded from: classes5.dex */
    public interface OnSelectCallback {
        void onPeerSelected(RecyclerView recyclerView, SenderView senderView, TLRPC.Peer peer);
    }

    public SenderSelectPopup(Context context, ChatActivity parentFragment, final MessagesController messagesController, final TLRPC.ChatFull chatFull, TLRPC.TL_channels_sendAsPeers sendAsPeers, final OnSelectCallback selectCallback) {
        super(context);
        this.chatFull = chatFull;
        this.sendAsPeers = sendAsPeers;
        BackButtonFrameLayout backButtonFrameLayout = new BackButtonFrameLayout(context);
        this.scrimPopupContainerLayout = backButtonFrameLayout;
        backButtonFrameLayout.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f));
        setContentView(this.scrimPopupContainerLayout);
        setWidth(-2);
        setHeight(-2);
        setBackgroundDrawable(null);
        Drawable shadowDrawable = ContextCompat.getDrawable(context, R.drawable.popup_fixed_alert).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
        this.scrimPopupContainerLayout.setBackground(shadowDrawable);
        android.graphics.Rect padding = new android.graphics.Rect();
        shadowDrawable.getPadding(padding);
        this.scrimPopupContainerLayout.setPadding(padding.left, padding.top, padding.right, padding.bottom);
        View view = new View(context);
        this.dimView = view;
        view.setBackgroundColor(AndroidUtilities.DARK_STATUS_BAR_OVERLAY);
        final int maxHeight = AndroidUtilities.dp(450.0f);
        final int maxWidth = (int) (parentFragment.contentView.getWidth() * 0.75f);
        LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.SenderSelectPopup.1
            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(widthMeasureSpec), maxWidth), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(heightMeasureSpec), maxHeight), View.MeasureSpec.getMode(heightMeasureSpec)));
            }

            @Override // android.view.View
            protected int getSuggestedMinimumWidth() {
                return AndroidUtilities.dp(260.0f);
            }
        };
        this.recyclerContainer = linearLayout;
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.headerText = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue));
        this.headerText.setTextSize(1, 16.0f);
        this.headerText.setText(LocaleController.getString("SendMessageAsTitle", R.string.SendMessageAsTitle));
        this.headerText.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM), 1);
        int dp = AndroidUtilities.dp(18.0f);
        this.headerText.setPadding(dp, AndroidUtilities.dp(12.0f), dp, AndroidUtilities.dp(12.0f));
        this.recyclerContainer.addView(this.headerText);
        FrameLayout recyclerFrameLayout = new FrameLayout(context);
        final List<TLRPC.Peer> peers = sendAsPeers.peers;
        this.recyclerView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setAdapter(new RecyclerListView.SelectionAdapter() { // from class: org.telegram.ui.Components.SenderSelectPopup.2
            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new SenderView(parent.getContext()));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                SenderView senderView = (SenderView) holder.itemView;
                TLRPC.Peer peer = (TLRPC.Peer) peers.get(position);
                long peerId = 0;
                if (peer.channel_id != 0) {
                    peerId = -peer.channel_id;
                }
                if (peerId == 0 && peer.user_id != 0) {
                    peerId = peer.user_id;
                }
                boolean z = true;
                if (peerId < 0) {
                    TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-peerId));
                    if (chat != null) {
                        senderView.title.setText(chat.title);
                        senderView.subtitle.setText(LocaleController.formatPluralString((!ChatObject.isChannel(chat) || chat.megagroup) ? "Members" : "Subscribers", chat.participants_count, new Object[0]));
                        senderView.avatar.setAvatar(chat);
                    }
                    SimpleAvatarView simpleAvatarView = senderView.avatar;
                    if (chatFull.default_send_as == null || chatFull.default_send_as.channel_id != peer.channel_id) {
                        z = false;
                    }
                    simpleAvatarView.setSelected(z, false);
                    return;
                }
                TLRPC.User user = messagesController.getUser(Long.valueOf(peerId));
                if (user != null) {
                    senderView.title.setText(UserObject.getUserName(user));
                    senderView.subtitle.setText(LocaleController.getString("VoipGroupPersonalAccount", R.string.VoipGroupPersonalAccount));
                    senderView.avatar.setAvatar(user);
                }
                SimpleAvatarView simpleAvatarView2 = senderView.avatar;
                if (chatFull.default_send_as == null || chatFull.default_send_as.user_id != peer.user_id) {
                    z = false;
                }
                simpleAvatarView2.setSelected(z, false);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return peers.size();
            }
        });
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.SenderSelectPopup.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean show = SenderSelectPopup.this.layoutManager.findFirstCompletelyVisibleItemPosition() != 0;
                if (SenderSelectPopup.this.isHeaderShadowVisible == null || show != SenderSelectPopup.this.isHeaderShadowVisible.booleanValue()) {
                    SenderSelectPopup.this.headerShadow.animate().cancel();
                    SenderSelectPopup.this.headerShadow.animate().alpha(show ? 1.0f : 0.0f).setDuration(150L).start();
                    SenderSelectPopup.this.isHeaderShadowVisible = Boolean.valueOf(show);
                }
            }
        });
        this.recyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i) {
                SenderSelectPopup.this.m2986lambda$new$0$orgtelegramuiComponentsSenderSelectPopup(selectCallback, peers, view2, i);
            }
        });
        recyclerFrameLayout.addView(this.recyclerView);
        this.headerShadow = new View(context);
        Drawable shadowDrawable2 = ContextCompat.getDrawable(context, R.drawable.header_shadow);
        shadowDrawable2.setAlpha(153);
        this.headerShadow.setBackground(shadowDrawable2);
        this.headerShadow.setAlpha(0.0f);
        recyclerFrameLayout.addView(this.headerShadow, LayoutHelper.createFrame(-1, 4.0f));
        this.recyclerContainer.addView(recyclerFrameLayout, LayoutHelper.createFrame(-1, -2.0f));
        this.scrimPopupContainerLayout.addView(this.recyclerContainer);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2986lambda$new$0$orgtelegramuiComponentsSenderSelectPopup(OnSelectCallback selectCallback, List peers, View view, int position) {
        if (this.clicked) {
            return;
        }
        this.clicked = true;
        selectCallback.onPeerSelected(this.recyclerView, (SenderView) view, (TLRPC.Peer) peers.get(position));
    }

    @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
    public void dismiss() {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        super.dismiss();
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x00d7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void startShowAnimation() {
        /*
            Method dump skipped, instructions count: 491
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SenderSelectPopup.startShowAnimation():void");
    }

    /* renamed from: lambda$startShowAnimation$1$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2992xdbb89fef(DynamicAnimation animation, float value, float velocity) {
        this.recyclerContainer.setScaleX(1.0f / value);
    }

    /* renamed from: lambda$startShowAnimation$2$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2993x95302d8e(DynamicAnimation animation, float value, float velocity) {
        this.recyclerContainer.setScaleY(1.0f / value);
    }

    /* renamed from: lambda$startShowAnimation$3$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2994x4ea7bb2d(SpringAnimation animation, DynamicAnimation animation1, boolean canceled, float value, float velocity) {
        if (!canceled) {
            this.springAnimations.remove(animation);
            animation1.cancel();
        }
    }

    public void startDismissAnimation(SpringAnimation... animations) {
        for (SpringAnimation springAnimation : this.springAnimations) {
            springAnimation.cancel();
        }
        this.springAnimations.clear();
        this.scrimPopupContainerLayout.setPivotX(AndroidUtilities.dp(8.0f));
        FrameLayout frameLayout = this.scrimPopupContainerLayout;
        frameLayout.setPivotY(frameLayout.getMeasuredHeight() - AndroidUtilities.dp(8.0f));
        this.recyclerContainer.setPivotX(0.0f);
        this.recyclerContainer.setPivotY(0.0f);
        this.scrimPopupContainerLayout.setScaleX(1.0f);
        this.scrimPopupContainerLayout.setScaleY(1.0f);
        this.recyclerContainer.setAlpha(1.0f);
        this.dimView.setAlpha(1.0f);
        List<SpringAnimation> newSpringAnimations = new ArrayList<>();
        boolean z = true;
        newSpringAnimations.addAll(Arrays.asList(new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_X).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda4
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.m2987x4cc0e76f(dynamicAnimation, f, f2);
            }
        }), new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda5
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.m2988x638750e(dynamicAnimation, f, f2);
            }
        }), new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.recyclerContainer, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.dimView, DynamicAnimation.ALPHA).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda0
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                SenderSelectPopup.this.m2989xbfb002ad(dynamicAnimation, z2, f, f2);
            }
        })));
        newSpringAnimations.addAll(Arrays.asList(animations));
        if (animations.length <= 0) {
            z = false;
        }
        this.runningCustomSprings = z;
        newSpringAnimations.get(0).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda1
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                SenderSelectPopup.this.m2990x7927904c(dynamicAnimation, z2, f, f2);
            }
        });
        for (final SpringAnimation springAnimation2 : newSpringAnimations) {
            this.springAnimations.add(springAnimation2);
            springAnimation2.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda2
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                    SenderSelectPopup.this.m2991x329f1deb(springAnimation2, dynamicAnimation, z2, f, f2);
                }
            });
            springAnimation2.start();
        }
    }

    /* renamed from: lambda$startDismissAnimation$4$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2987x4cc0e76f(DynamicAnimation animation, float value, float velocity) {
        this.recyclerContainer.setScaleX(1.0f / value);
    }

    /* renamed from: lambda$startDismissAnimation$5$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2988x638750e(DynamicAnimation animation, float value, float velocity) {
        this.recyclerContainer.setScaleY(1.0f / value);
    }

    /* renamed from: lambda$startDismissAnimation$6$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2989xbfb002ad(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (this.dimView.getParent() != null) {
            ((ViewGroup) this.dimView.getParent()).removeView(this.dimView);
        }
        dismiss();
    }

    /* renamed from: lambda$startDismissAnimation$7$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2990x7927904c(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        this.runningCustomSprings = false;
    }

    /* renamed from: lambda$startDismissAnimation$8$org-telegram-ui-Components-SenderSelectPopup */
    public /* synthetic */ void m2991x329f1deb(SpringAnimation springAnimation, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (!canceled) {
            this.springAnimations.remove(springAnimation);
            animation.cancel();
        }
    }

    /* loaded from: classes5.dex */
    public static final class SenderView extends LinearLayout {
        public final SimpleAvatarView avatar;
        public final TextView subtitle;
        public final TextView title;

        public SenderView(Context context) {
            super(context);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            setOrientation(0);
            setGravity(16);
            int padding = AndroidUtilities.dp(14.0f);
            setPadding(padding, padding / 2, padding, padding / 2);
            SimpleAvatarView simpleAvatarView = new SimpleAvatarView(context);
            this.avatar = simpleAvatarView;
            addView(simpleAvatarView, LayoutHelper.createFrame(40, 40.0f));
            LinearLayout textRow = new LinearLayout(context);
            textRow.setOrientation(1);
            addView(textRow, LayoutHelper.createLinear(0, -1, 1.0f, 12, 0, 0, 0));
            TextView textView = new TextView(context);
            this.title = textView;
            textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
            textView.setTextSize(1, 16.0f);
            textView.setTag(textView);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textRow.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitle = textView2;
            textView2.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), 102));
            textView2.setTextSize(1, 14.0f);
            textView2.setTag(textView2);
            textView2.setMaxLines(1);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textRow.addView(textView2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class BackButtonFrameLayout extends FrameLayout {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BackButtonFrameLayout(Context context) {
            super(context);
            SenderSelectPopup.this = r1;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == 4 && event.getRepeatCount() == 0 && SenderSelectPopup.this.isShowing()) {
                SenderSelectPopup.this.dismiss();
            }
            return super.dispatchKeyEvent(event);
        }
    }
}
