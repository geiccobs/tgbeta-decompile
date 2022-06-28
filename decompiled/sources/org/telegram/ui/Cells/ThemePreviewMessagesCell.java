package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.style.CharacterStyle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.PinchToZoomHelper;
/* loaded from: classes4.dex */
public class ThemePreviewMessagesCell extends LinearLayout {
    public static final int TYPE_REACTIONS_DOUBLE_TAP = 2;
    private Drawable backgroundDrawable;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    public BaseFragment fragment;
    private Drawable oldBackgroundDrawable;
    private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
    private ActionBarLayout parentLayout;
    private Drawable shadowDrawable;
    private final int type;
    private final Runnable invalidateRunnable = new Runnable() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            ThemePreviewMessagesCell.this.invalidate();
        }
    };
    private ChatMessageCell[] cells = new ChatMessageCell[2];

    public ThemePreviewMessagesCell(Context context, ActionBarLayout layout, int type) {
        super(context);
        MessageObject message2;
        MessageObject message1;
        this.type = type;
        int currentAccount = UserConfig.selectedAccount;
        this.parentLayout = layout;
        setWillNotDraw(false);
        setOrientation(1);
        setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
        this.shadowDrawable = Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
        int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
        if (type == 2) {
            TLRPC.Message message = new TLRPC.TL_message();
            message.message = LocaleController.getString("DoubleTapPreviewMessage", R.string.DoubleTapPreviewMessage);
            message.date = date + 60;
            message.dialog_id = 1L;
            message.flags = 259;
            message.from_id = new TLRPC.TL_peerUser();
            message.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            message.id = 1;
            message.media = new TLRPC.TL_messageMediaEmpty();
            message.out = false;
            message.peer_id = new TLRPC.TL_peerUser();
            message.peer_id.user_id = 0L;
            MessageObject message12 = new MessageObject(UserConfig.selectedAccount, message, true, false);
            message12.resetLayout();
            message12.eventId = 1L;
            message12.customName = LocaleController.getString("DoubleTapPreviewSenderName", R.string.DoubleTapPreviewSenderName);
            message12.customAvatarDrawable = ContextCompat.getDrawable(context, R.drawable.dino_pic);
            message1 = message12;
            message2 = null;
        } else {
            TLRPC.Message message3 = new TLRPC.TL_message();
            if (type == 0) {
                message3.message = LocaleController.getString("FontSizePreviewReply", R.string.FontSizePreviewReply);
            } else {
                message3.message = LocaleController.getString("NewThemePreviewReply", R.string.NewThemePreviewReply);
            }
            message3.date = date + 60;
            message3.dialog_id = 1L;
            message3.flags = 259;
            message3.from_id = new TLRPC.TL_peerUser();
            message3.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            message3.id = 1;
            message3.media = new TLRPC.TL_messageMediaEmpty();
            message3.out = true;
            message3.peer_id = new TLRPC.TL_peerUser();
            message3.peer_id.user_id = 0L;
            MessageObject replyMessageObject = new MessageObject(UserConfig.selectedAccount, message3, true, false);
            TLRPC.Message message4 = new TLRPC.TL_message();
            if (type == 0) {
                message4.message = LocaleController.getString("FontSizePreviewLine2", R.string.FontSizePreviewLine2);
            } else {
                String text = LocaleController.getString("NewThemePreviewLine3", R.string.NewThemePreviewLine3);
                StringBuilder builder = new StringBuilder(text);
                int index1 = text.indexOf(42);
                int index2 = text.lastIndexOf(42);
                if (index1 != -1 && index2 != -1) {
                    builder.replace(index2, index2 + 1, "");
                    builder.replace(index1, index1 + 1, "");
                    TLRPC.TL_messageEntityTextUrl entityUrl = new TLRPC.TL_messageEntityTextUrl();
                    entityUrl.offset = index1;
                    entityUrl.length = (index2 - index1) - 1;
                    entityUrl.url = "https://telegram.org";
                    message4.entities.add(entityUrl);
                }
                message4.message = builder.toString();
            }
            message4.date = date + 960;
            message4.dialog_id = 1L;
            message4.flags = 259;
            message4.from_id = new TLRPC.TL_peerUser();
            message4.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            message4.id = 1;
            message4.media = new TLRPC.TL_messageMediaEmpty();
            message4.out = true;
            message4.peer_id = new TLRPC.TL_peerUser();
            message4.peer_id.user_id = 0L;
            MessageObject message13 = new MessageObject(UserConfig.selectedAccount, message4, true, false);
            message13.resetLayout();
            message13.eventId = 1L;
            TLRPC.Message message5 = new TLRPC.TL_message();
            if (type == 0) {
                message5.message = LocaleController.getString("FontSizePreviewLine1", R.string.FontSizePreviewLine1);
            } else {
                message5.message = LocaleController.getString("NewThemePreviewLine1", R.string.NewThemePreviewLine1);
            }
            message5.date = date + 60;
            message5.dialog_id = 1L;
            message5.flags = 265;
            message5.from_id = new TLRPC.TL_peerUser();
            message5.id = 1;
            message5.reply_to = new TLRPC.TL_messageReplyHeader();
            message5.reply_to.reply_to_msg_id = 5;
            message5.media = new TLRPC.TL_messageMediaEmpty();
            message5.out = false;
            message5.peer_id = new TLRPC.TL_peerUser();
            message5.peer_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            MessageObject message22 = new MessageObject(UserConfig.selectedAccount, message5, true, false);
            if (type == 0) {
                message22.customReplyName = LocaleController.getString("FontSizePreviewName", R.string.FontSizePreviewName);
            } else {
                message22.customReplyName = LocaleController.getString("NewThemePreviewName", R.string.NewThemePreviewName);
            }
            message22.eventId = 1L;
            message22.resetLayout();
            message22.replyMessageObject = replyMessageObject;
            message1 = message13;
            message2 = message22;
        }
        int i = 0;
        while (true) {
            int a = i;
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (a < chatMessageCellArr.length) {
                chatMessageCellArr[a] = new ChatMessageCell(context, context, currentAccount, type) { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell.1
                    private GestureDetector gestureDetector;
                    final /* synthetic */ Context val$context;
                    final /* synthetic */ int val$currentAccount;
                    final /* synthetic */ int val$type;

                    /* JADX INFO: Access modifiers changed from: package-private */
                    /* renamed from: org.telegram.ui.Cells.ThemePreviewMessagesCell$1$1 */
                    /* loaded from: classes4.dex */
                    public class C00451 extends GestureDetector.SimpleOnGestureListener {
                        C00451() {
                            AnonymousClass1.this = this$1;
                        }

                        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                        public boolean onDoubleTap(MotionEvent e) {
                            boolean added = getMessageObject().selectReaction(MediaDataController.getInstance(AnonymousClass1.this.val$currentAccount).getDoubleTapReaction(), false, false);
                            AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                            anonymousClass1.setMessageObject(anonymousClass1.getMessageObject(), null, false, false);
                            requestLayout();
                            ReactionsEffectOverlay.removeCurrent(false);
                            if (added) {
                                ReactionsEffectOverlay.show(ThemePreviewMessagesCell.this.fragment, null, ThemePreviewMessagesCell.this.cells[1], e.getX(), e.getY(), MediaDataController.getInstance(AnonymousClass1.this.val$currentAccount).getDoubleTapReaction(), AnonymousClass1.this.val$currentAccount, 0);
                                ReactionsEffectOverlay.startAnimation();
                            }
                            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver$OnPreDrawListenerC00461());
                            return true;
                        }

                        /* renamed from: org.telegram.ui.Cells.ThemePreviewMessagesCell$1$1$1 */
                        /* loaded from: classes4.dex */
                        public class ViewTreeObserver$OnPreDrawListenerC00461 implements ViewTreeObserver.OnPreDrawListener {
                            ViewTreeObserver$OnPreDrawListenerC00461() {
                                C00451.this = this$2;
                            }

                            @Override // android.view.ViewTreeObserver.OnPreDrawListener
                            public boolean onPreDraw() {
                                getViewTreeObserver().removeOnPreDrawListener(this);
                                getTransitionParams().resetAnimation();
                                getTransitionParams().animateChange();
                                getTransitionParams().animateChange = true;
                                getTransitionParams().animateChangeProgress = 0.0f;
                                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell$1$1$1$$ExternalSyntheticLambda0
                                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                        ThemePreviewMessagesCell.AnonymousClass1.C00451.ViewTreeObserver$OnPreDrawListenerC00461.this.m1681x31463cd3(valueAnimator2);
                                    }
                                });
                                valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell.1.1.1.1
                                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        getTransitionParams().resetAnimation();
                                        getTransitionParams().animateChange = false;
                                        getTransitionParams().animateChangeProgress = 1.0f;
                                    }
                                });
                                valueAnimator.start();
                                return false;
                            }

                            /* renamed from: lambda$onPreDraw$0$org-telegram-ui-Cells-ThemePreviewMessagesCell$1$1$1 */
                            public /* synthetic */ void m1681x31463cd3(ValueAnimator valueAnimator1) {
                                getTransitionParams().animateChangeProgress = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
                                invalidate();
                            }
                        }
                    }

                    {
                        ThemePreviewMessagesCell.this = this;
                        this.val$context = context;
                        this.val$currentAccount = currentAccount;
                        this.val$type = type;
                        this.gestureDetector = new GestureDetector(context, new C00451());
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell, android.view.View
                    public boolean onTouchEvent(MotionEvent event) {
                        this.gestureDetector.onTouchEvent(event);
                        return true;
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void dispatchDraw(Canvas canvas) {
                        if (getAvatarImage() != null && getAvatarImage().getImageHeight() != 0.0f) {
                            getAvatarImage().setImageCoords(getAvatarImage().getImageX(), (getMeasuredHeight() - getAvatarImage().getImageHeight()) - AndroidUtilities.dp(4.0f), getAvatarImage().getImageWidth(), getAvatarImage().getImageHeight());
                            getAvatarImage().setRoundRadius((int) (getAvatarImage().getImageHeight() / 2.0f));
                            getAvatarImage().draw(canvas);
                        } else if (this.val$type == 2) {
                            invalidate();
                        }
                        super.dispatchDraw(canvas);
                    }
                };
                this.cells[a].setDelegate(new ChatMessageCell.ChatMessageCellDelegate() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell.2
                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean canDrawOutboundsContent() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean canPerformActions() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i2, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, chat, i2, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i2, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell, i2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell, i2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount, z);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell, i2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressSideButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell, str);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell, j);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i2, int i3, int i4) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i2, i3, i4);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ String getAdminRank(long j) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ PinchToZoomHelper getPinchToZoomHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean hasSelectedMessages() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void invalidateBlur() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean isLandscape() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean keyboardIsOpened() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject, str, str2, str3, str4, i2, i3);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void needReloadPolls() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void needShowPremiumFeatures(String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumFeatures(this, str);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean onAccessibilityAction(int i2, Bundle bundle) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i2, bundle);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void onDiceFinished() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public /* synthetic */ void videoTimerReached() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                    }
                });
                this.cells[a].isChat = type == 2;
                this.cells[a].setFullyDraw(true);
                MessageObject messageObject = a == 0 ? message2 : message1;
                if (messageObject != null) {
                    this.cells[a].setMessageObject(messageObject, null, false, false);
                    addView(this.cells[a], LayoutHelper.createLinear(-1, -2));
                }
                i = a + 1;
            } else {
                return;
            }
        }
    }

    public ChatMessageCell[] getCells() {
        return this.cells;
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        int a = 0;
        while (true) {
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (a < chatMessageCellArr.length) {
                chatMessageCellArr[a].invalidate();
                a++;
            } else {
                return;
            }
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable newDrawable;
        int a;
        int alpha;
        Drawable drawable;
        Drawable newDrawable2;
        Drawable newDrawable3;
        Drawable newDrawable4 = Theme.getCachedWallpaperNonBlocking();
        if (Theme.wallpaperLoadTask != null) {
            invalidate();
        }
        if (newDrawable4 != this.backgroundDrawable && newDrawable4 != null) {
            if (Theme.isAnimatingColor()) {
                this.oldBackgroundDrawable = this.backgroundDrawable;
                this.oldBackgroundGradientDisposable = this.backgroundGradientDisposable;
            } else {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
            }
            this.backgroundDrawable = newDrawable4;
        }
        float themeAnimationValue = this.parentLayout.getThemeAnimationValue();
        int a2 = 0;
        while (a2 < 2) {
            Drawable drawable2 = a2 == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
            if (drawable2 == null) {
                newDrawable = newDrawable4;
                a = a2;
            } else {
                if (a2 == 1 && this.oldBackgroundDrawable != null && this.parentLayout != null) {
                    alpha = (int) (255.0f * themeAnimationValue);
                } else {
                    alpha = 255;
                }
                if (alpha <= 0) {
                    newDrawable = newDrawable4;
                    a = a2;
                } else {
                    drawable2.setAlpha(alpha);
                    if ((drawable2 instanceof ColorDrawable) || (drawable2 instanceof GradientDrawable)) {
                        newDrawable = newDrawable4;
                        a = a2;
                        newDrawable2 = drawable2;
                    } else if (drawable2 instanceof MotionBackgroundDrawable) {
                        newDrawable = newDrawable4;
                        a = a2;
                        newDrawable2 = drawable2;
                    } else {
                        if (!(drawable2 instanceof BitmapDrawable)) {
                            newDrawable = newDrawable4;
                            a = a2;
                        } else {
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
                            bitmapDrawable.setFilterBitmap(true);
                            if (bitmapDrawable.getTileModeX() == Shader.TileMode.REPEAT) {
                                canvas.save();
                                float scale = 2.0f / AndroidUtilities.density;
                                canvas.scale(scale, scale);
                                drawable2.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / scale), (int) Math.ceil(getMeasuredHeight() / scale));
                                newDrawable = newDrawable4;
                                a = a2;
                                newDrawable3 = drawable2;
                            } else {
                                int viewHeight = getMeasuredHeight();
                                float scaleX = getMeasuredWidth() / drawable2.getIntrinsicWidth();
                                float scaleY = viewHeight / drawable2.getIntrinsicHeight();
                                float scale2 = Math.max(scaleX, scaleY);
                                a = a2;
                                int width = (int) Math.ceil(drawable2.getIntrinsicWidth() * scale2);
                                Drawable drawable3 = drawable2;
                                int height = (int) Math.ceil(drawable2.getIntrinsicHeight() * scale2);
                                int x = (getMeasuredWidth() - width) / 2;
                                int y = (viewHeight - height) / 2;
                                canvas.save();
                                canvas.clipRect(0, 0, width, getMeasuredHeight());
                                newDrawable = newDrawable4;
                                newDrawable3 = drawable3;
                                newDrawable3.setBounds(x, y, x + width, y + height);
                            }
                            newDrawable3.draw(canvas);
                            canvas.restore();
                        }
                        if (a != 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                            BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
                            if (disposable2 == null) {
                                drawable = null;
                            } else {
                                disposable2.dispose();
                                drawable = null;
                                this.oldBackgroundGradientDisposable = null;
                            }
                            this.oldBackgroundDrawable = drawable;
                            invalidate();
                        }
                    }
                    newDrawable2.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    if (newDrawable2 instanceof BackgroundGradientDrawable) {
                        BackgroundGradientDrawable backgroundGradientDrawable = (BackgroundGradientDrawable) newDrawable2;
                        this.backgroundGradientDisposable = backgroundGradientDrawable.drawExactBoundsSize(canvas, this);
                    } else {
                        newDrawable2.draw(canvas);
                    }
                    if (a != 0) {
                    }
                }
            }
            a2 = a + 1;
            newDrawable4 = newDrawable;
        }
        this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.shadowDrawable.draw(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.backgroundGradientDisposable = null;
        }
        BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
        if (disposable2 != null) {
            disposable2.dispose();
            this.oldBackgroundGradientDisposable = null;
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.type == 2) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.type == 2) {
            return super.dispatchTouchEvent(ev);
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSetPressed(boolean pressed) {
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.type == 2) {
            return super.onTouchEvent(event);
        }
        return false;
    }
}
