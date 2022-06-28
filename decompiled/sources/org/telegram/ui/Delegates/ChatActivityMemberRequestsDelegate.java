package org.telegram.ui.Delegates;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MemberRequestsBottomSheet;
/* loaded from: classes5.dex */
public class ChatActivityMemberRequestsDelegate {
    private AvatarsImageView avatarsView;
    private MemberRequestsBottomSheet bottomSheet;
    private final Callback callback;
    private TLRPC.ChatFull chatInfo;
    private int closePendingRequestsCount = -1;
    private ImageView closeView;
    private final int currentAccount;
    private final TLRPC.Chat currentChat;
    private final BaseFragment fragment;
    private ValueAnimator pendingRequestsAnimator;
    private int pendingRequestsCount;
    private float pendingRequestsEnterOffset;
    private TextView requestsCountTextView;
    private FrameLayout root;

    /* loaded from: classes5.dex */
    public interface Callback {
        void onEnterOffsetChanged();
    }

    public ChatActivityMemberRequestsDelegate(BaseFragment fragment, TLRPC.Chat currentChat, Callback callback) {
        this.fragment = fragment;
        this.currentChat = currentChat;
        this.currentAccount = fragment.getCurrentAccount();
        this.callback = callback;
    }

    public View getView() {
        if (this.root == null) {
            FrameLayout frameLayout = new FrameLayout(this.fragment.getParentActivity());
            this.root = frameLayout;
            frameLayout.setBackgroundResource(R.drawable.blockpanel);
            this.root.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(this.fragment.getThemedColor(Theme.key_chat_topPanelBackground), PorterDuff.Mode.MULTIPLY));
            this.root.setVisibility(8);
            this.pendingRequestsEnterOffset = -getViewHeight();
            View pendingRequestsSelector = new View(this.fragment.getParentActivity());
            pendingRequestsSelector.setBackground(Theme.getSelectorDrawable(false));
            pendingRequestsSelector.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityMemberRequestsDelegate.this.m3315x24bd3119(view);
                }
            });
            this.root.addView(pendingRequestsSelector, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 2.0f));
            LinearLayout requestsDataLayout = new LinearLayout(this.fragment.getParentActivity());
            requestsDataLayout.setOrientation(0);
            this.root.addView(requestsDataLayout, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 36.0f, 0.0f));
            AvatarsImageView avatarsImageView = new AvatarsImageView(this.fragment.getParentActivity(), false) { // from class: org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate.1
                @Override // org.telegram.ui.Components.AvatarsImageView, android.view.View
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = this.avatarsDarawable.count == 0 ? 0 : ((this.avatarsDarawable.count - 1) * 20) + 24;
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(width), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                }
            };
            this.avatarsView = avatarsImageView;
            avatarsImageView.reset();
            requestsDataLayout.addView(this.avatarsView, LayoutHelper.createFrame(-2, -1.0f, 48, 8.0f, 0.0f, 10.0f, 0.0f));
            TextView textView = new TextView(this.fragment.getParentActivity());
            this.requestsCountTextView = textView;
            textView.setEllipsize(TextUtils.TruncateAt.END);
            this.requestsCountTextView.setGravity(16);
            this.requestsCountTextView.setSingleLine();
            this.requestsCountTextView.setText((CharSequence) null);
            this.requestsCountTextView.setTextColor(this.fragment.getThemedColor(Theme.key_chat_topPanelTitle));
            this.requestsCountTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            requestsDataLayout.addView(this.requestsCountTextView, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            this.closeView = new ImageView(this.fragment.getParentActivity());
            if (Build.VERSION.SDK_INT >= 21) {
                this.closeView.setBackground(Theme.createSelectorDrawable(this.fragment.getThemedColor(Theme.key_inappPlayerClose) & 436207615, 1, AndroidUtilities.dp(14.0f)));
            }
            this.closeView.setColorFilter(new PorterDuffColorFilter(this.fragment.getThemedColor(Theme.key_chat_topPanelClose), PorterDuff.Mode.MULTIPLY));
            this.closeView.setContentDescription(LocaleController.getString("Close", R.string.Close));
            this.closeView.setImageResource(R.drawable.miniplayer_close);
            this.closeView.setScaleType(ImageView.ScaleType.CENTER);
            this.closeView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatActivityMemberRequestsDelegate.this.m3316xb8fba0b8(view);
                }
            });
            this.root.addView(this.closeView, LayoutHelper.createFrame(36, -1.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null) {
                setPendingRequests(chatFull.requests_pending, this.chatInfo.recent_requesters, false);
            }
        }
        return this.root;
    }

    /* renamed from: lambda$getView$0$org-telegram-ui-Delegates-ChatActivityMemberRequestsDelegate */
    public /* synthetic */ void m3315x24bd3119(View v) {
        showBottomSheet();
    }

    /* renamed from: lambda$getView$1$org-telegram-ui-Delegates-ChatActivityMemberRequestsDelegate */
    public /* synthetic */ void m3316xb8fba0b8(View v) {
        this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, this.pendingRequestsCount);
        this.closePendingRequestsCount = this.pendingRequestsCount;
        animatePendingRequests(false, true);
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo, boolean animated) {
        this.chatInfo = chatInfo;
        if (chatInfo != null) {
            setPendingRequests(chatInfo.requests_pending, chatInfo.recent_requesters, animated);
        }
    }

    public int getViewHeight() {
        return AndroidUtilities.dp(40.0f);
    }

    public float getViewEnterOffset() {
        return this.pendingRequestsEnterOffset;
    }

    public void onBackToScreen() {
        MemberRequestsBottomSheet memberRequestsBottomSheet = this.bottomSheet;
        if (memberRequestsBottomSheet != null && memberRequestsBottomSheet.isNeedRestoreDialog()) {
            showBottomSheet();
        }
    }

    private void showBottomSheet() {
        if (this.bottomSheet == null) {
            this.bottomSheet = new MemberRequestsBottomSheet(this.fragment, this.currentChat.id) { // from class: org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate.2
                @Override // org.telegram.ui.Components.UsersAlertBase, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
                public void dismiss() {
                    if (ChatActivityMemberRequestsDelegate.this.bottomSheet != null && !ChatActivityMemberRequestsDelegate.this.bottomSheet.isNeedRestoreDialog()) {
                        ChatActivityMemberRequestsDelegate.this.bottomSheet = null;
                    }
                    super.dismiss();
                }
            };
        }
        this.fragment.showDialog(this.bottomSheet);
    }

    private void setPendingRequests(int count, List<Long> recentRequestersIdList, boolean animated) {
        if (this.root == null) {
            return;
        }
        if (count <= 0) {
            if (this.currentChat != null) {
                this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, 0);
                this.closePendingRequestsCount = 0;
            }
            animatePendingRequests(false, animated);
            this.pendingRequestsCount = 0;
        } else if (this.pendingRequestsCount != count) {
            this.pendingRequestsCount = count;
            this.requestsCountTextView.setText(LocaleController.formatPluralString("JoinUsersRequests", count, new Object[0]));
            animatePendingRequests(true, animated);
            if (recentRequestersIdList != null && !recentRequestersIdList.isEmpty()) {
                int usersCount = Math.min(3, recentRequestersIdList.size());
                for (int i = 0; i < usersCount; i++) {
                    TLRPC.User user = this.fragment.getMessagesController().getUser(recentRequestersIdList.get(i));
                    if (user != null) {
                        this.avatarsView.setObject(i, this.currentAccount, user);
                    }
                }
                this.avatarsView.setCount(usersCount);
                this.avatarsView.commitTransition(true);
            }
        }
    }

    private void animatePendingRequests(final boolean appear, boolean animated) {
        int i = 0;
        boolean isVisibleNow = this.root.getVisibility() == 0;
        if (appear == isVisibleNow) {
            return;
        }
        if (appear) {
            if (this.closePendingRequestsCount == -1 && this.currentChat != null) {
                this.closePendingRequestsCount = this.fragment.getMessagesController().getChatPendingRequestsOnClosed(this.currentChat.id);
            }
            int i2 = this.pendingRequestsCount;
            int i3 = this.closePendingRequestsCount;
            if (i2 == i3) {
                return;
            }
            if (i3 != 0 && this.currentChat != null) {
                this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, 0);
            }
        }
        ValueAnimator valueAnimator = this.pendingRequestsAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float f = 0.0f;
        if (animated) {
            float[] fArr = new float[2];
            fArr[0] = appear ? 0.0f : 1.0f;
            if (appear) {
                f = 1.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.pendingRequestsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChatActivityMemberRequestsDelegate.this.m3314x95fe199e(valueAnimator2);
                }
            });
            this.pendingRequestsAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    if (appear) {
                        ChatActivityMemberRequestsDelegate.this.root.setVisibility(0);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (!appear) {
                        ChatActivityMemberRequestsDelegate.this.root.setVisibility(8);
                    }
                }
            });
            this.pendingRequestsAnimator.setDuration(200L);
            this.pendingRequestsAnimator.start();
            return;
        }
        FrameLayout frameLayout = this.root;
        if (!appear) {
            i = 8;
        }
        frameLayout.setVisibility(i);
        if (!appear) {
            f = -getViewHeight();
        }
        this.pendingRequestsEnterOffset = f;
        Callback callback = this.callback;
        if (callback != null) {
            callback.onEnterOffsetChanged();
        }
    }

    /* renamed from: lambda$animatePendingRequests$2$org-telegram-ui-Delegates-ChatActivityMemberRequestsDelegate */
    public /* synthetic */ void m3314x95fe199e(ValueAnimator animation) {
        float progress = ((Float) animation.getAnimatedValue()).floatValue();
        this.pendingRequestsEnterOffset = (-getViewHeight()) * (1.0f - progress);
        Callback callback = this.callback;
        if (callback != null) {
            callback.onEnterOffsetChanged();
        }
    }

    public void fillThemeDescriptions(List<ThemeDescription> themeDescriptions) {
        themeDescriptions.add(new ThemeDescription(this.root, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground));
        themeDescriptions.add(new ThemeDescription(this.requestsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle));
        themeDescriptions.add(new ThemeDescription(this.closeView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose));
    }
}
