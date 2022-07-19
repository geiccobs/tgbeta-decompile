package org.telegram.ui.Components.Reactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ReactionsContainerLayout;
/* loaded from: classes3.dex */
public class ReactionsEffectOverlay {
    @SuppressLint({"StaticFieldLeak"})
    public static ReactionsEffectOverlay currentOverlay;
    @SuppressLint({"StaticFieldLeak"})
    public static ReactionsEffectOverlay currentShortOverlay;
    private static long lastHapticTime;
    private static int uniqPrefix;
    float animateInProgress;
    float animateOutProgress;
    private final int animationType;
    private ChatMessageCell cell;
    private final FrameLayout container;
    private ViewGroup decorView;
    private float dismissProgress;
    private boolean dismissed;
    private final AnimationView effectImageView;
    private final AnimationView emojiImageView;
    private final AnimationView emojiStaticImageView;
    private boolean finished;
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

    static /* synthetic */ float access$216(ReactionsEffectOverlay reactionsEffectOverlay, float f) {
        float f2 = reactionsEffectOverlay.dismissProgress + f;
        reactionsEffectOverlay.dismissProgress = f2;
        return f2;
    }

    /* JADX WARN: Removed duplicated region for block: B:113:0x05d0  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x017b  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x01a1  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x02e8  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x02f9  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0399  */
    /* JADX WARN: Type inference failed for: r14v1, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r14v2 */
    /* JADX WARN: Type inference failed for: r14v3 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private ReactionsEffectOverlay(android.content.Context r33, org.telegram.ui.ActionBar.BaseFragment r34, org.telegram.ui.Components.ReactionsContainerLayout r35, org.telegram.ui.Cells.ChatMessageCell r36, float r37, float r38, java.lang.String r39, int r40, int r41) {
        /*
            Method dump skipped, instructions count: 1492
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.Components.ReactionsContainerLayout, org.telegram.ui.Cells.ChatMessageCell, float, float, java.lang.String, int, int):void");
    }

    /* renamed from: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay$1 */
    /* loaded from: classes3.dex */
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
        AnonymousClass1(Context context, BaseFragment baseFragment, ChatMessageCell chatMessageCell, String str, ChatActivity chatActivity, int i, int i2, boolean z, float f, float f2, float f3) {
            super(context);
            ReactionsEffectOverlay.this = r1;
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

        /* JADX WARN: Removed duplicated region for block: B:165:0x0445  */
        /* JADX WARN: Removed duplicated region for block: B:173:0x0474  */
        /* JADX WARN: Removed duplicated region for block: B:174:0x0477  */
        /* JADX WARN: Removed duplicated region for block: B:177:0x053d  */
        /* JADX WARN: Removed duplicated region for block: B:182:0x054a  */
        /* JADX WARN: Removed duplicated region for block: B:183:0x055e  */
        /* JADX WARN: Removed duplicated region for block: B:186:0x0568  */
        /* JADX WARN: Removed duplicated region for block: B:190:0x057b  */
        @Override // android.view.ViewGroup, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void dispatchDraw(android.graphics.Canvas r20) {
            /*
                Method dump skipped, instructions count: 1432
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ReactionsEffectOverlay.AnonymousClass1.dispatchDraw(android.graphics.Canvas):void");
        }

        public /* synthetic */ void lambda$dispatchDraw$0() {
            ReactionsEffectOverlay.this.removeCurrentView();
        }

        public /* synthetic */ void lambda$dispatchDraw$1() {
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
        } catch (Exception unused) {
        }
    }

    public static void show(BaseFragment baseFragment, ReactionsContainerLayout reactionsContainerLayout, ChatMessageCell chatMessageCell, float f, float f2, String str, int i, int i2) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (chatMessageCell == null || str == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        boolean z = true;
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            return;
        }
        if (i2 == 2 || i2 == 0) {
            show(baseFragment, null, chatMessageCell, 0.0f, 0.0f, str, i, 1);
        }
        ReactionsEffectOverlay reactionsEffectOverlay = new ReactionsEffectOverlay(baseFragment.getParentActivity(), baseFragment, reactionsContainerLayout, chatMessageCell, f, f2, str, i, i2);
        if (i2 == 1) {
            currentShortOverlay = reactionsEffectOverlay;
        } else {
            currentOverlay = reactionsEffectOverlay;
        }
        if (!(baseFragment instanceof ChatActivity) || (actionBarPopupWindow = ((ChatActivity) baseFragment).scrimPopupWindow) == null || !actionBarPopupWindow.isShowing()) {
            z = false;
        }
        reactionsEffectOverlay.useWindow = z;
        if (z) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.height = -1;
            layoutParams.width = -1;
            layoutParams.type = 1000;
            layoutParams.flags = 65816;
            layoutParams.format = -3;
            WindowManager windowManager = baseFragment.getParentActivity().getWindowManager();
            reactionsEffectOverlay.windowManager = windowManager;
            windowManager.addView(reactionsEffectOverlay.windowView, layoutParams);
        } else {
            FrameLayout frameLayout = (FrameLayout) baseFragment.getParentActivity().getWindow().getDecorView();
            reactionsEffectOverlay.decorView = frameLayout;
            frameLayout.addView(reactionsEffectOverlay.windowView);
        }
        chatMessageCell.invalidate();
        if (chatMessageCell.getCurrentMessagesGroup() == null || chatMessageCell.getParent() == null) {
            return;
        }
        ((View) chatMessageCell.getParent()).invalidate();
    }

    public static void startAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.started = true;
            if (reactionsEffectOverlay.animationType != 0 || System.currentTimeMillis() - lastHapticTime <= 200) {
                return;
            }
            lastHapticTime = System.currentTimeMillis();
            currentOverlay.cell.performHapticFeedback(3);
            return;
        }
        startShortAnimation();
        ReactionsEffectOverlay reactionsEffectOverlay2 = currentShortOverlay;
        if (reactionsEffectOverlay2 == null) {
            return;
        }
        reactionsEffectOverlay2.cell.reactionsLayoutInBubble.animateReaction(reactionsEffectOverlay2.reaction);
    }

    public static void startShortAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentShortOverlay;
        if (reactionsEffectOverlay == null || reactionsEffectOverlay.started) {
            return;
        }
        reactionsEffectOverlay.started = true;
        if (reactionsEffectOverlay.animationType != 1 || System.currentTimeMillis() - lastHapticTime <= 200) {
            return;
        }
        lastHapticTime = System.currentTimeMillis();
        currentShortOverlay.cell.performHapticFeedback(3);
    }

    public static void removeCurrent(boolean z) {
        int i = 0;
        while (i < 2) {
            ReactionsEffectOverlay reactionsEffectOverlay = i == 0 ? currentOverlay : currentShortOverlay;
            if (reactionsEffectOverlay != null) {
                if (z) {
                    reactionsEffectOverlay.removeCurrentView();
                } else {
                    reactionsEffectOverlay.dismissed = true;
                }
            }
            i++;
        }
        currentShortOverlay = null;
        currentOverlay = null;
    }

    public static boolean isPlaying(int i, long j, String str) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            int i2 = reactionsEffectOverlay.animationType;
            if (i2 != 2 && i2 != 0) {
                return false;
            }
            long j2 = reactionsEffectOverlay.groupId;
            return ((j2 != 0 && j == j2) || i == reactionsEffectOverlay.messageId) && reactionsEffectOverlay.reaction.equals(str);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
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

    public static void onScrolled(int i) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.lastDrawnToY -= i;
            if (i == 0) {
                return;
            }
            reactionsEffectOverlay.wasScrolled = true;
        }
    }

    public static int sizeForBigReaction() {
        int dp = AndroidUtilities.dp(350.0f);
        Point point = AndroidUtilities.displaySize;
        return (int) (Math.round(Math.min(dp, Math.min(point.x, point.y)) * 0.7f) / AndroidUtilities.density);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
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

        private AvatarParticle(ReactionsEffectOverlay reactionsEffectOverlay) {
        }

        /* synthetic */ AvatarParticle(ReactionsEffectOverlay reactionsEffectOverlay, AnonymousClass1 anonymousClass1) {
            this(reactionsEffectOverlay);
        }
    }
}
