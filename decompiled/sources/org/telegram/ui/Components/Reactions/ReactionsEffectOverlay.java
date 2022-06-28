package org.telegram.ui.Components.Reactions;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;
/* loaded from: classes5.dex */
public class ReactionsEffectOverlay {
    public static final int LONG_ANIMATION = 0;
    public static final int ONLY_MOVE_ANIMATION = 2;
    public static final int SHORT_ANIMATION = 1;
    public static ReactionsEffectOverlay currentOverlay;
    public static ReactionsEffectOverlay currentShortOverlay;
    private static long lastHapticTime;
    private static int uniqPrefix;
    boolean animateIn;
    float animateInProgress;
    float animateOutProgress;
    private final int animationType;
    BackupImageView backupImageView;
    private ChatMessageCell cell;
    private final FrameLayout container;
    private final int currentAccount;
    private ViewGroup decorView;
    private float dismissProgress;
    private boolean dismissed;
    private final AnimationView effectImageView;
    private final AnimationView emojiImageView;
    private final AnimationView emojiStaticImageView;
    private boolean finished;
    private final BaseFragment fragment;
    private final long groupId;
    private ReactionsContainerLayout.ReactionHolderView holderView;
    private float lastDrawnToX;
    private float lastDrawnToY;
    private final int messageId;
    private final String reaction;
    private boolean started;
    private boolean useWindow;
    private boolean wasScrolled;
    private WindowManager windowManager;
    FrameLayout windowView;
    int[] loc = new int[2];
    ArrayList<AvatarParticle> avatars = new ArrayList<>();

    static /* synthetic */ float access$216(ReactionsEffectOverlay x0, float x1) {
        float f = x0.dismissProgress + x1;
        x0.dismissProgress = f;
        return f;
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x01bf  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private ReactionsEffectOverlay(android.content.Context r43, org.telegram.ui.ActionBar.BaseFragment r44, org.telegram.ui.Components.ReactionsContainerLayout r45, org.telegram.ui.Cells.ChatMessageCell r46, float r47, float r48, java.lang.String r49, int r50, int r51) {
        /*
            Method dump skipped, instructions count: 1629
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.Components.ReactionsContainerLayout, org.telegram.ui.Cells.ChatMessageCell, float, float, java.lang.String, int, int):void");
    }

    /* renamed from: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 extends FrameLayout {
        final /* synthetic */ int val$animationType;
        final /* synthetic */ ChatMessageCell val$cell;
        final /* synthetic */ ChatActivity val$chatActivity;
        final /* synthetic */ int val$emojiSize;
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ boolean val$fromHolder;
        final /* synthetic */ float val$fromScale;
        final /* synthetic */ float val$fromX;
        final /* synthetic */ float val$fromY;
        final /* synthetic */ String val$reaction;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Context arg0, BaseFragment baseFragment, ChatMessageCell chatMessageCell, String str, ChatActivity chatActivity, int i, int i2, boolean z, float f, float f2, float f3) {
            super(arg0);
            ReactionsEffectOverlay.this = this$0;
            this.val$fragment = baseFragment;
            this.val$cell = chatMessageCell;
            this.val$reaction = str;
            this.val$chatActivity = chatActivity;
            this.val$emojiSize = i;
            this.val$animationType = i2;
            this.val$fromHolder = z;
            this.val$fromScale = f;
            this.val$fromX = f2;
            this.val$fromY = f3;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            ChatMessageCell drawingCell;
            float toH;
            float toY;
            float toX;
            float animateInProgressX;
            float animateInProgressY;
            float y;
            float x;
            float y2;
            float animateInProgressY2;
            float animateOutProgress;
            float x2;
            float toH2;
            RLottieDrawable animation;
            boolean isLeft;
            float toY2;
            float toX2;
            float previewY;
            int i;
            float jumpProgress;
            float f;
            if (ReactionsEffectOverlay.this.dismissed) {
                if (ReactionsEffectOverlay.this.dismissProgress != 1.0f) {
                    ReactionsEffectOverlay.access$216(ReactionsEffectOverlay.this, 0.10666667f);
                    if (ReactionsEffectOverlay.this.dismissProgress > 1.0f) {
                        ReactionsEffectOverlay.this.dismissProgress = 1.0f;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                ReactionsEffectOverlay.AnonymousClass1.this.m2944xd3d15369();
                            }
                        });
                    }
                }
                if (ReactionsEffectOverlay.this.dismissProgress != 1.0f) {
                    setAlpha(1.0f - ReactionsEffectOverlay.this.dismissProgress);
                    super.dispatchDraw(canvas);
                }
                invalidate();
            } else if (ReactionsEffectOverlay.this.started) {
                if (ReactionsEffectOverlay.this.holderView != null) {
                    ReactionsEffectOverlay.this.holderView.backupImageView.setAlpha(0.0f);
                    ReactionsEffectOverlay.this.holderView.pressedBackupImageView.setAlpha(0.0f);
                }
                BaseFragment baseFragment = this.val$fragment;
                if (baseFragment instanceof ChatActivity) {
                    drawingCell = ((ChatActivity) baseFragment).findMessageCell(ReactionsEffectOverlay.this.messageId, false);
                } else {
                    drawingCell = this.val$cell;
                }
                if (this.val$cell.getMessageObject().shouldDrawReactionsInLayout()) {
                    toH = AndroidUtilities.dp(20.0f);
                } else {
                    toH = AndroidUtilities.dp(14.0f);
                }
                if (drawingCell == null) {
                    toX = ReactionsEffectOverlay.this.lastDrawnToX;
                    toY = ReactionsEffectOverlay.this.lastDrawnToY;
                } else {
                    this.val$cell.getLocationInWindow(ReactionsEffectOverlay.this.loc);
                    ReactionsLayoutInBubble.ReactionButton reactionButton = this.val$cell.getReactionButton(this.val$reaction);
                    toX = ReactionsEffectOverlay.this.loc[0] + this.val$cell.reactionsLayoutInBubble.x;
                    toY = ReactionsEffectOverlay.this.loc[1] + this.val$cell.reactionsLayoutInBubble.y;
                    if (reactionButton != null) {
                        toX += reactionButton.x + reactionButton.imageReceiver.getImageX();
                        toY += reactionButton.y + reactionButton.imageReceiver.getImageY();
                    }
                    ChatActivity chatActivity = this.val$chatActivity;
                    if (chatActivity != null) {
                        toY += chatActivity.drawingChatLisViewYoffset;
                    }
                    if (drawingCell.drawPinnedBottom && !drawingCell.shouldDrawTimeOnMedia()) {
                        toY += AndroidUtilities.dp(2.0f);
                    }
                    ReactionsEffectOverlay.this.lastDrawnToX = toX;
                    ReactionsEffectOverlay.this.lastDrawnToY = toY;
                }
                if (this.val$fragment.getParentActivity() != null && this.val$fragment.getFragmentView().getParent() != null && this.val$fragment.getFragmentView().getVisibility() == 0 && this.val$fragment.getFragmentView() != null) {
                    this.val$fragment.getFragmentView().getLocationOnScreen(ReactionsEffectOverlay.this.loc);
                    setAlpha(((View) this.val$fragment.getFragmentView().getParent()).getAlpha());
                    int i2 = this.val$emojiSize;
                    float previewX = toX - ((i2 - toH) / 2.0f);
                    float previewY2 = toY - ((i2 - toH) / 2.0f);
                    if (this.val$animationType != 1) {
                        if (previewX < ReactionsEffectOverlay.this.loc[0]) {
                            previewX = ReactionsEffectOverlay.this.loc[0];
                        }
                        if (this.val$emojiSize + previewX > ReactionsEffectOverlay.this.loc[0] + getMeasuredWidth()) {
                            previewX = (ReactionsEffectOverlay.this.loc[0] + getMeasuredWidth()) - this.val$emojiSize;
                        }
                    }
                    float animateOutProgress2 = CubicBezierInterpolator.DEFAULT.getInterpolation(ReactionsEffectOverlay.this.animateOutProgress);
                    if (this.val$animationType == 2) {
                        animateInProgressX = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(animateOutProgress2);
                        animateInProgressY = CubicBezierInterpolator.DEFAULT.getInterpolation(animateOutProgress2);
                    } else if (this.val$fromHolder) {
                        animateInProgressX = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(ReactionsEffectOverlay.this.animateInProgress);
                        animateInProgressY = CubicBezierInterpolator.DEFAULT.getInterpolation(ReactionsEffectOverlay.this.animateInProgress);
                    } else {
                        float f2 = ReactionsEffectOverlay.this.animateInProgress;
                        animateInProgressY = f2;
                        animateInProgressX = f2;
                    }
                    float scale = ((1.0f - animateInProgressX) * this.val$fromScale) + animateInProgressX;
                    float toScale = toH / this.val$emojiSize;
                    if (this.val$animationType == 1) {
                        x = previewX;
                        scale = 1.0f;
                        y = previewY2;
                    } else {
                        x = (this.val$fromX * (1.0f - animateInProgressX)) + (previewX * animateInProgressX);
                        y = (this.val$fromY * (1.0f - animateInProgressY)) + (previewY2 * animateInProgressY);
                    }
                    ReactionsEffectOverlay.this.effectImageView.setTranslationX(x);
                    ReactionsEffectOverlay.this.effectImageView.setTranslationY(y);
                    ReactionsEffectOverlay.this.effectImageView.setAlpha(1.0f - animateOutProgress2);
                    ReactionsEffectOverlay.this.effectImageView.setScaleX(scale);
                    ReactionsEffectOverlay.this.effectImageView.setScaleY(scale);
                    int i3 = this.val$animationType;
                    if (i3 == 2) {
                        scale = (this.val$fromScale * (1.0f - animateInProgressX)) + (toScale * animateInProgressX);
                        x = (this.val$fromX * (1.0f - animateInProgressX)) + (toX * animateInProgressX);
                        y = (this.val$fromY * (1.0f - animateInProgressY)) + (toY * animateInProgressY);
                    } else if (animateOutProgress2 != 0.0f) {
                        scale = ((1.0f - animateOutProgress2) * scale) + (toScale * animateOutProgress2);
                        x = ((1.0f - animateOutProgress2) * x) + (toX * animateOutProgress2);
                        y = ((1.0f - animateOutProgress2) * y) + (toY * animateOutProgress2);
                    }
                    if (i3 != 1) {
                        ReactionsEffectOverlay.this.emojiStaticImageView.setAlpha(animateOutProgress2 > 0.7f ? (animateOutProgress2 - 0.7f) / 0.3f : 0.0f);
                    }
                    ReactionsEffectOverlay.this.container.setTranslationX(x);
                    ReactionsEffectOverlay.this.container.setTranslationY(y);
                    ReactionsEffectOverlay.this.container.setScaleX(scale);
                    ReactionsEffectOverlay.this.container.setScaleY(scale);
                    super.dispatchDraw(canvas);
                    if ((this.val$animationType == 1 || ReactionsEffectOverlay.this.emojiImageView.wasPlaying) && ReactionsEffectOverlay.this.animateInProgress != 1.0f) {
                        if (this.val$fromHolder) {
                            ReactionsEffectOverlay.this.animateInProgress += 0.045714285f;
                        } else {
                            ReactionsEffectOverlay.this.animateInProgress += 0.07272727f;
                        }
                        if (ReactionsEffectOverlay.this.animateInProgress > 1.0f) {
                            ReactionsEffectOverlay.this.animateInProgress = 1.0f;
                        }
                    }
                    if (this.val$animationType != 2 && ((!ReactionsEffectOverlay.this.wasScrolled || this.val$animationType != 0) && (this.val$animationType == 1 || !ReactionsEffectOverlay.this.emojiImageView.wasPlaying || ReactionsEffectOverlay.this.emojiImageView.getImageReceiver().getLottieAnimation() == null || ReactionsEffectOverlay.this.emojiImageView.getImageReceiver().getLottieAnimation().isRunning()))) {
                        if (this.val$animationType == 1) {
                            if (!ReactionsEffectOverlay.this.effectImageView.wasPlaying || ReactionsEffectOverlay.this.effectImageView.getImageReceiver().getLottieAnimation() == null || ReactionsEffectOverlay.this.effectImageView.getImageReceiver().getLottieAnimation().isRunning()) {
                            }
                        }
                        if (ReactionsEffectOverlay.this.avatars.isEmpty() && ReactionsEffectOverlay.this.effectImageView.wasPlaying) {
                            RLottieDrawable animation2 = ReactionsEffectOverlay.this.effectImageView.getImageReceiver().getLottieAnimation();
                            int i4 = 0;
                            while (i4 < ReactionsEffectOverlay.this.avatars.size()) {
                                AvatarParticle particle = ReactionsEffectOverlay.this.avatars.get(i4);
                                float toScale2 = toScale;
                                float toScale3 = particle.progress;
                                if (animation2 == null || !animation2.isRunning()) {
                                    animation = animation2;
                                    toH2 = toH;
                                    x2 = x;
                                    animateOutProgress = animateOutProgress2;
                                    animateInProgressY2 = animateInProgressY;
                                    y2 = y;
                                    isLeft = true;
                                } else {
                                    animation = animation2;
                                    toH2 = toH;
                                    x2 = x;
                                    long duration = ReactionsEffectOverlay.this.effectImageView.getImageReceiver().getLottieAnimation().getDuration();
                                    int totalFramesCount = ReactionsEffectOverlay.this.effectImageView.getImageReceiver().getLottieAnimation().getFramesCount();
                                    animateOutProgress = animateOutProgress2;
                                    int currentFrame = ReactionsEffectOverlay.this.effectImageView.getImageReceiver().getLottieAnimation().getCurrentFrame();
                                    animateInProgressY2 = animateInProgressY;
                                    y2 = y;
                                    int timeLeft = (int) (((float) duration) - (((float) duration) * (currentFrame / totalFramesCount)));
                                    isLeft = timeLeft < particle.leftTime;
                                }
                                if (isLeft && particle.outProgress != 1.0f) {
                                    particle.outProgress += 0.10666667f;
                                    if (particle.outProgress > 1.0f) {
                                        particle.outProgress = 1.0f;
                                        ReactionsEffectOverlay.this.avatars.remove(i4);
                                        i4--;
                                        previewY = previewY2;
                                        toX2 = toX;
                                        toY2 = toY;
                                        i = 1;
                                        i4 += i;
                                        toScale = toScale2;
                                        animation2 = animation;
                                        toH = toH2;
                                        x = x2;
                                        animateOutProgress2 = animateOutProgress;
                                        animateInProgressY = animateInProgressY2;
                                        y = y2;
                                        previewY2 = previewY;
                                        toX = toX2;
                                        toY = toY2;
                                    }
                                }
                                if (toScale3 < 0.5f) {
                                    jumpProgress = toScale3 / 0.5f;
                                    f = 1.0f;
                                } else {
                                    f = 1.0f;
                                    jumpProgress = 1.0f - ((toScale3 - 0.5f) / 0.5f);
                                }
                                float avatarX = (particle.fromX * (f - toScale3)) + (particle.toX * toScale3);
                                float avatarY = ((particle.fromY * (f - toScale3)) + (particle.toY * toScale3)) - (particle.jumpY * jumpProgress);
                                float s = particle.randomScale * toScale3 * (1.0f - particle.outProgress);
                                float cx = ReactionsEffectOverlay.this.effectImageView.getX() + (ReactionsEffectOverlay.this.effectImageView.getWidth() * ReactionsEffectOverlay.this.effectImageView.getScaleX() * avatarX);
                                float cy = ReactionsEffectOverlay.this.effectImageView.getY() + (ReactionsEffectOverlay.this.effectImageView.getHeight() * ReactionsEffectOverlay.this.effectImageView.getScaleY() * avatarY);
                                int size = AndroidUtilities.dp(16.0f);
                                float avatarY2 = size;
                                previewY = previewY2;
                                toX2 = toX;
                                toY2 = toY;
                                ReactionsEffectOverlay.this.avatars.get(i4).imageReceiver.setImageCoords(cx - (avatarY2 / 2.0f), cy - (size / 2.0f), size, size);
                                ReactionsEffectOverlay.this.avatars.get(i4).imageReceiver.setRoundRadius(size >> 1);
                                canvas.save();
                                canvas.translate(0.0f, particle.globalTranslationY);
                                canvas.scale(s, s, cx, cy);
                                canvas.rotate(particle.currentRotation, cx, cy);
                                ReactionsEffectOverlay.this.avatars.get(i4).imageReceiver.draw(canvas);
                                canvas.restore();
                                if (particle.progress < 1.0f) {
                                    particle.progress += 0.045714285f;
                                    if (particle.progress > 1.0f) {
                                        particle.progress = 1.0f;
                                    }
                                }
                                if (toScale3 >= 1.0f) {
                                    particle.globalTranslationY += (AndroidUtilities.dp(20.0f) * 16.0f) / 500.0f;
                                }
                                if (particle.incrementRotation) {
                                    particle.currentRotation += particle.randomRotation / 250.0f;
                                    if (particle.currentRotation > particle.randomRotation) {
                                        particle.incrementRotation = false;
                                        i = 1;
                                    } else {
                                        i = 1;
                                    }
                                } else {
                                    particle.currentRotation -= particle.randomRotation / 250.0f;
                                    if (particle.currentRotation >= (-particle.randomRotation)) {
                                        i = 1;
                                    } else {
                                        i = 1;
                                        particle.incrementRotation = true;
                                    }
                                }
                                i4 += i;
                                toScale = toScale2;
                                animation2 = animation;
                                toH = toH2;
                                x = x2;
                                animateOutProgress2 = animateOutProgress;
                                animateInProgressY = animateInProgressY2;
                                y = y2;
                                previewY2 = previewY;
                                toX = toX2;
                                toY = toY2;
                            }
                        }
                        invalidate();
                    }
                    if (ReactionsEffectOverlay.this.animateOutProgress != 1.0f) {
                        int i5 = this.val$animationType;
                        if (i5 == 1) {
                            ReactionsEffectOverlay.this.animateOutProgress = 1.0f;
                        } else {
                            float duration2 = i5 == 2 ? 350.0f : 220.0f;
                            ReactionsEffectOverlay reactionsEffectOverlay = ReactionsEffectOverlay.this;
                            float scale2 = reactionsEffectOverlay.animateOutProgress;
                            reactionsEffectOverlay.animateOutProgress = scale2 + (16.0f / duration2);
                        }
                        if (ReactionsEffectOverlay.this.animateOutProgress > 0.7f && !ReactionsEffectOverlay.this.finished) {
                            ReactionsEffectOverlay.startShortAnimation();
                        }
                        if (ReactionsEffectOverlay.this.animateOutProgress >= 1.0f) {
                            int i6 = this.val$animationType;
                            if (i6 == 0 || i6 == 2) {
                                this.val$cell.reactionsLayoutInBubble.animateReaction(this.val$reaction);
                            }
                            ReactionsEffectOverlay.this.animateOutProgress = 1.0f;
                            if (this.val$animationType == 1) {
                                ReactionsEffectOverlay.currentShortOverlay = null;
                            } else {
                                ReactionsEffectOverlay.currentOverlay = null;
                            }
                            this.val$cell.invalidate();
                            if (this.val$cell.getCurrentMessagesGroup() != null && this.val$cell.getParent() != null) {
                                ((View) this.val$cell.getParent()).invalidate();
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1$$ExternalSyntheticLambda1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ReactionsEffectOverlay.AnonymousClass1.this.m2945xc760d7aa();
                                }
                            });
                        }
                    }
                    if (ReactionsEffectOverlay.this.avatars.isEmpty()) {
                    }
                    invalidate();
                }
            } else {
                invalidate();
            }
        }

        /* renamed from: lambda$dispatchDraw$0$org-telegram-ui-Components-Reactions-ReactionsEffectOverlay$1 */
        public /* synthetic */ void m2944xd3d15369() {
            ReactionsEffectOverlay.this.removeCurrentView();
        }

        /* renamed from: lambda$dispatchDraw$1$org-telegram-ui-Components-Reactions-ReactionsEffectOverlay$1 */
        public /* synthetic */ void m2945xc760d7aa() {
            ReactionsEffectOverlay.this.removeCurrentView();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            for (int i = 0; i < ReactionsEffectOverlay.this.avatars.size(); i++) {
                ReactionsEffectOverlay.this.avatars.get(i).imageReceiver.onAttachedToWindow();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            for (int i = 0; i < ReactionsEffectOverlay.this.avatars.size(); i++) {
                ReactionsEffectOverlay.this.avatars.get(i).imageReceiver.onDetachedFromWindow();
            }
        }
    }

    public void removeCurrentView() {
        try {
            if (this.useWindow) {
                this.windowManager.removeView(this.windowView);
            } else {
                this.decorView.removeView(this.windowView);
            }
        } catch (Exception e) {
        }
    }

    public static void show(BaseFragment baseFragment, ReactionsContainerLayout reactionsLayout, ChatMessageCell cell, float x, float y, String reaction, int currentAccount, int animationType) {
        if (cell == null || reaction == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        boolean animationEnabled = MessagesController.getGlobalMainSettings().getBoolean("view_animations", true);
        if (!animationEnabled) {
            return;
        }
        if (animationType == 2 || animationType == 0) {
            show(baseFragment, null, cell, 0.0f, 0.0f, reaction, currentAccount, 1);
        }
        ReactionsEffectOverlay reactionsEffectOverlay = new ReactionsEffectOverlay(baseFragment.getParentActivity(), baseFragment, reactionsLayout, cell, x, y, reaction, currentAccount, animationType);
        if (animationType == 1) {
            currentShortOverlay = reactionsEffectOverlay;
        } else {
            currentOverlay = reactionsEffectOverlay;
        }
        boolean useWindow = false;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            if (chatActivity.scrimPopupWindow != null && chatActivity.scrimPopupWindow.isShowing()) {
                useWindow = true;
            }
        }
        reactionsEffectOverlay.useWindow = useWindow;
        if (useWindow) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.height = -1;
            lp.width = -1;
            lp.type = 1000;
            lp.flags = 65816;
            lp.format = -3;
            WindowManager windowManager = baseFragment.getParentActivity().getWindowManager();
            reactionsEffectOverlay.windowManager = windowManager;
            windowManager.addView(reactionsEffectOverlay.windowView, lp);
        } else {
            FrameLayout frameLayout = (FrameLayout) baseFragment.getParentActivity().getWindow().getDecorView();
            reactionsEffectOverlay.decorView = frameLayout;
            frameLayout.addView(reactionsEffectOverlay.windowView);
        }
        cell.invalidate();
        if (cell.getCurrentMessagesGroup() != null && cell.getParent() != null) {
            ((View) cell.getParent()).invalidate();
        }
    }

    public static void startAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.started = true;
            if (reactionsEffectOverlay.animationType == 0 && System.currentTimeMillis() - lastHapticTime > 200) {
                lastHapticTime = System.currentTimeMillis();
                currentOverlay.cell.performHapticFeedback(3);
                return;
            }
            return;
        }
        startShortAnimation();
        ReactionsEffectOverlay reactionsEffectOverlay2 = currentShortOverlay;
        if (reactionsEffectOverlay2 != null) {
            reactionsEffectOverlay2.cell.reactionsLayoutInBubble.animateReaction(currentShortOverlay.reaction);
        }
    }

    public static void startShortAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentShortOverlay;
        if (reactionsEffectOverlay != null && !reactionsEffectOverlay.started) {
            reactionsEffectOverlay.started = true;
            if (reactionsEffectOverlay.animationType == 1 && System.currentTimeMillis() - lastHapticTime > 200) {
                lastHapticTime = System.currentTimeMillis();
                currentShortOverlay.cell.performHapticFeedback(3);
            }
        }
    }

    public static void removeCurrent(boolean instant) {
        int i = 0;
        while (i < 2) {
            ReactionsEffectOverlay overlay = i == 0 ? currentOverlay : currentShortOverlay;
            if (overlay != null) {
                if (instant) {
                    overlay.removeCurrentView();
                } else {
                    overlay.dismissed = true;
                }
            }
            i++;
        }
        currentShortOverlay = null;
        currentOverlay = null;
    }

    public static boolean isPlaying(int messageId, long groupId, String reaction) {
        int i;
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay == null || !((i = reactionsEffectOverlay.animationType) == 2 || i == 0)) {
            return false;
        }
        long j = reactionsEffectOverlay.groupId;
        return ((j != 0 && groupId == j) || messageId == reactionsEffectOverlay.messageId) && reactionsEffectOverlay.reaction.equals(reaction);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class AnimationView extends BackupImageView {
        boolean wasPlaying;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AnimationView(Context context) {
            super(context);
            ReactionsEffectOverlay.this = r1;
        }

        @Override // org.telegram.ui.Components.BackupImageView, android.view.View
        public void onDraw(Canvas canvas) {
            if (getImageReceiver().getLottieAnimation() != null && getImageReceiver().getLottieAnimation().isRunning()) {
                this.wasPlaying = true;
            }
            if (!this.wasPlaying && getImageReceiver().getLottieAnimation() != null && !getImageReceiver().getLottieAnimation().isRunning()) {
                if (ReactionsEffectOverlay.this.animationType == 2) {
                    getImageReceiver().getLottieAnimation().setCurrentFrame(getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
                } else {
                    getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    getImageReceiver().getLottieAnimation().start();
                }
            }
            super.onDraw(canvas);
        }
    }

    public static void onScrolled(int dy) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.lastDrawnToY -= dy;
            if (dy != 0) {
                reactionsEffectOverlay.wasScrolled = true;
            }
        }
    }

    public static int sizeForBigReaction() {
        return (int) (Math.round(Math.min(AndroidUtilities.dp(350.0f), Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f) / AndroidUtilities.density);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class AvatarParticle {
        float currentRotation;
        float fromX;
        float fromY;
        float globalTranslationY;
        ImageReceiver imageReceiver;
        boolean incrementRotation;
        float jumpY;
        public int leftTime;
        float outProgress;
        float progress;
        float randomRotation;
        float randomScale;
        float toX;
        float toY;

        private AvatarParticle() {
            ReactionsEffectOverlay.this = r1;
        }

        /* synthetic */ AvatarParticle(ReactionsEffectOverlay x0, AnonymousClass1 x1) {
            this();
        }
    }
}
