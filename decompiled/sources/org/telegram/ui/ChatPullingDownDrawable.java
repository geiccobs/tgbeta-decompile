package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CounterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
/* loaded from: classes4.dex */
public class ChatPullingDownDrawable implements NotificationCenter.NotificationCenterDelegate {
    boolean animateCheck;
    public boolean animateSwipeToRelease;
    float bounceProgress;
    StaticLayout chatNameLayout;
    int chatNameWidth;
    float checkProgress;
    float circleRadius;
    private final int currentAccount;
    private final long currentDialog;
    public int dialogFilterId;
    public int dialogFolderId;
    boolean drawFolderBackground;
    boolean emptyStub;
    private final int filterId;
    private final int folderId;
    private final View fragmentView;
    long lastHapticTime;
    float lastProgress;
    public long lastShowingReleaseTime;
    int lastWidth;
    StaticLayout layout1;
    int layout1Width;
    StaticLayout layout2;
    int layout2Width;
    TLRPC.Chat nextChat;
    public long nextDialogId;
    Runnable onAnimationFinishRunnable;
    View parentView;
    float progressToBottomPanel;
    private final Theme.ResourcesProvider resourcesProvider;
    boolean showBottomPanel;
    AnimatorSet showReleaseAnimator;
    float swipeToReleaseProgress;
    Paint arrowPaint = new Paint(1);
    TextPaint textPaint = new TextPaint(1);
    TextPaint textPaint2 = new TextPaint(1);
    private Paint xRefPaint = new Paint(1);
    Path path = new Path();
    ImageReceiver imageReceiver = new ImageReceiver();
    CounterView.CounterDrawable counterDrawable = new CounterView.CounterDrawable(null, true, null);
    int[] params = new int[3];

    public ChatPullingDownDrawable(int currentAccount, View fragmentView, long currentDialog, int folderId, int filterId, Theme.ResourcesProvider resourcesProvider) {
        this.fragmentView = fragmentView;
        this.currentAccount = currentAccount;
        this.currentDialog = currentDialog;
        this.folderId = folderId;
        this.filterId = filterId;
        this.resourcesProvider = resourcesProvider;
        this.arrowPaint.setStrokeWidth(AndroidUtilities.dpf2(2.8f));
        this.arrowPaint.setStrokeCap(Paint.Cap.ROUND);
        this.counterDrawable.gravity = 3;
        this.counterDrawable.setType(1);
        this.counterDrawable.addServiceGradient = true;
        this.counterDrawable.circlePaint = getThemedPaint(Theme.key_paint_chatActionBackground);
        this.counterDrawable.textPaint = this.textPaint;
        this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textPaint2.setTextSize(AndroidUtilities.dp(14.0f));
        this.xRefPaint.setColor(-16777216);
        this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        updateDialog();
    }

    public void updateDialog() {
        TLRPC.Dialog dialog = getNextUnreadDialog(this.currentDialog, this.folderId, this.filterId, true, this.params);
        if (dialog != null) {
            this.nextDialogId = dialog.id;
            int[] iArr = this.params;
            this.drawFolderBackground = iArr[0] == 1;
            this.dialogFolderId = iArr[1];
            this.dialogFilterId = iArr[2];
            this.emptyStub = false;
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id));
            this.nextChat = chat;
            if (chat == null) {
                MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(dialog.id));
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(this.nextChat);
            this.imageReceiver.setImage(ImageLocation.getForChat(this.nextChat, 1), "50_50", avatarDrawable, null, UserConfig.getInstance(0).getCurrentUser(), 0);
            MessagesController.getInstance(this.currentAccount).ensureMessagesLoaded(dialog.id, 0, null);
            this.counterDrawable.setCount(dialog.unread_count, false);
            return;
        }
        this.nextChat = null;
        this.drawFolderBackground = false;
        this.emptyStub = true;
    }

    public void setWidth(int width) {
        String str2;
        String str1;
        int i;
        if (width != this.lastWidth) {
            this.circleRadius = AndroidUtilities.dp(56.0f) / 2.0f;
            this.lastWidth = width;
            TLRPC.Chat chat = this.nextChat;
            String nameStr = chat != null ? chat.title : LocaleController.getString("SwipeToGoNextChannelEnd", R.string.SwipeToGoNextChannelEnd);
            int measureText = (int) this.textPaint.measureText(nameStr);
            this.chatNameWidth = measureText;
            this.chatNameWidth = Math.min(measureText, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.chatNameLayout = new StaticLayout(nameStr, this.textPaint, this.chatNameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            boolean z = this.drawFolderBackground;
            if (z && (i = this.dialogFolderId) != this.folderId && i != 0) {
                str1 = LocaleController.getString("SwipeToGoNextArchive", R.string.SwipeToGoNextArchive);
                str2 = LocaleController.getString("ReleaseToGoNextArchive", R.string.ReleaseToGoNextArchive);
            } else if (z) {
                str1 = LocaleController.getString("SwipeToGoNextFolder", R.string.SwipeToGoNextFolder);
                str2 = LocaleController.getString("ReleaseToGoNextFolder", R.string.ReleaseToGoNextFolder);
            } else {
                str1 = LocaleController.getString("SwipeToGoNextChannel", R.string.SwipeToGoNextChannel);
                str2 = LocaleController.getString("ReleaseToGoNextChannel", R.string.ReleaseToGoNextChannel);
            }
            int measureText2 = (int) this.textPaint2.measureText(str1);
            this.layout1Width = measureText2;
            this.layout1Width = Math.min(measureText2, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.layout1 = new StaticLayout(str1, this.textPaint2, this.layout1Width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            int measureText3 = (int) this.textPaint2.measureText(str2);
            this.layout2Width = measureText3;
            this.layout2Width = Math.min(measureText3, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.layout2 = new StaticLayout(str2, this.textPaint2, this.layout2Width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            float cx = this.lastWidth / 2.0f;
            float cy = AndroidUtilities.dp(12.0f) + this.circleRadius;
            this.imageReceiver.setImageCoords(cx - (AndroidUtilities.dp(40.0f) / 2.0f), cy - (AndroidUtilities.dp(40.0f) / 2.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            this.imageReceiver.setRoundRadius((int) (AndroidUtilities.dp(40.0f) / 2.0f));
            this.counterDrawable.setSize(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(100.0f));
        }
    }

    public void draw(Canvas canvas, View parent, float progress, float alpha) {
        float alpha2;
        int oldAlpha3;
        float offset;
        int oldAlpha32;
        int oldAlpha33;
        int oldAlpha34;
        this.parentView = parent;
        this.counterDrawable.setParent(parent);
        float offset2 = AndroidUtilities.dp(110.0f) * progress;
        if (offset2 < AndroidUtilities.dp(8.0f)) {
            return;
        }
        if (progress >= 0.2f) {
            alpha2 = alpha;
        } else {
            alpha2 = 5.0f * progress * alpha;
        }
        Theme.applyServiceShaderMatrix(this.lastWidth, parent.getMeasuredHeight(), 0.0f, parent.getMeasuredHeight() - offset2);
        this.textPaint.setColor(getThemedColor(Theme.key_chat_serviceText));
        this.arrowPaint.setColor(getThemedColor(Theme.key_chat_serviceText));
        this.textPaint2.setColor(getThemedColor(Theme.key_chat_messagePanelHint));
        int oldAlpha = getThemedPaint(Theme.key_paint_chatActionBackground).getAlpha();
        int oldAlpha1 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
        int oldAlpha2 = this.textPaint.getAlpha();
        int oldAlpha35 = this.arrowPaint.getAlpha();
        Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (oldAlpha1 * alpha2));
        getThemedPaint(Theme.key_paint_chatActionBackground).setAlpha((int) (oldAlpha * alpha2));
        this.textPaint.setAlpha((int) (oldAlpha2 * alpha2));
        this.imageReceiver.setAlpha(alpha2);
        if ((progress < 1.0f || this.lastProgress >= 1.0f) && (progress >= 1.0f || this.lastProgress != 1.0f)) {
            oldAlpha3 = oldAlpha35;
        } else {
            long time = System.currentTimeMillis();
            oldAlpha3 = oldAlpha35;
            if (time - this.lastHapticTime > 100) {
                parent.performHapticFeedback(3, 2);
                this.lastHapticTime = time;
            }
            this.lastProgress = progress;
        }
        if (progress == 1.0f && !this.animateSwipeToRelease) {
            this.animateSwipeToRelease = true;
            this.animateCheck = true;
            showReleaseState(true, parent);
            this.lastShowingReleaseTime = System.currentTimeMillis();
        } else if (progress != 1.0f && this.animateSwipeToRelease) {
            this.animateSwipeToRelease = false;
            showReleaseState(false, parent);
        }
        float cx = this.lastWidth / 2.0f;
        float bounceOffset = this.bounceProgress * (-AndroidUtilities.dp(4.0f));
        if (!this.emptyStub) {
            offset = offset2;
        } else {
            offset = offset2 - bounceOffset;
        }
        float widthRadius = Math.max(0.0f, Math.min(this.circleRadius, ((offset / 2.0f) - (AndroidUtilities.dp(16.0f) * progress)) - AndroidUtilities.dp(4.0f)));
        float widthRadius2 = Math.max(0.0f, Math.min(this.circleRadius * progress, (offset / 2.0f) - (AndroidUtilities.dp(8.0f) * progress)));
        float dp2 = ((widthRadius2 * 2.0f) - AndroidUtilities.dp2(16.0f)) * (1.0f - this.swipeToReleaseProgress);
        float f = this.swipeToReleaseProgress;
        float size = (AndroidUtilities.dp(56.0f) * f) + dp2;
        if (f < 1.0f || this.emptyStub) {
            float bottom = ((-AndroidUtilities.dp(8.0f)) * (1.0f - this.swipeToReleaseProgress)) + (((-offset) + AndroidUtilities.dp(56.0f)) * this.swipeToReleaseProgress);
            AndroidUtilities.rectTmp.set(cx - widthRadius, -offset, cx + widthRadius, bottom);
            if (this.swipeToReleaseProgress > 0.0f && !this.emptyStub) {
                float inset = AndroidUtilities.dp(16.0f) * this.swipeToReleaseProgress;
                AndroidUtilities.rectTmp.inset(inset, inset);
            }
            drawBackground(canvas, AndroidUtilities.rectTmp);
            float arrowCy = (((-offset) + AndroidUtilities.dp(24.0f)) + (AndroidUtilities.dp(8.0f) * (1.0f - progress))) - (AndroidUtilities.dp(36.0f) * this.swipeToReleaseProgress);
            canvas.save();
            AndroidUtilities.rectTmp.inset(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
            canvas.clipRect(AndroidUtilities.rectTmp);
            float f2 = this.swipeToReleaseProgress;
            if (f2 > 0.0f) {
                this.arrowPaint.setAlpha((int) ((1.0f - f2) * 255.0f));
            }
            drawArrow(canvas, cx, arrowCy, AndroidUtilities.dp(24.0f) * progress);
            if (this.emptyStub) {
                float top = ((((-AndroidUtilities.dp(8.0f)) - (AndroidUtilities.dp2(8.0f) * progress)) - size) * (1.0f - this.swipeToReleaseProgress)) + (((-offset) - AndroidUtilities.dp(2.0f)) * this.swipeToReleaseProgress) + bounceOffset;
                oldAlpha32 = oldAlpha3;
                this.arrowPaint.setAlpha(oldAlpha32);
                canvas.save();
                canvas.scale(progress, progress, cx, AndroidUtilities.dp(28.0f) + top);
                drawCheck(canvas, cx, AndroidUtilities.dp(28.0f) + top);
                canvas.restore();
            } else {
                oldAlpha32 = oldAlpha3;
            }
            canvas.restore();
        } else {
            oldAlpha32 = oldAlpha3;
        }
        if (this.chatNameLayout == null || this.swipeToReleaseProgress <= 0.0f) {
            oldAlpha33 = oldAlpha32;
        } else {
            getThemedPaint(Theme.key_paint_chatActionBackground).setAlpha((int) (oldAlpha * alpha2));
            this.textPaint.setAlpha((int) (oldAlpha2 * alpha2));
            float y = ((AndroidUtilities.dp(20.0f) * (1.0f - this.swipeToReleaseProgress)) - (AndroidUtilities.dp(36.0f) * this.swipeToReleaseProgress)) + bounceOffset;
            RectF rectF = AndroidUtilities.rectTmp;
            int i = this.lastWidth;
            int i2 = this.chatNameWidth;
            oldAlpha33 = oldAlpha32;
            rectF.set((i - i2) / 2.0f, y, i - ((i - i2) / 2.0f), this.chatNameLayout.getHeight() + y);
            AndroidUtilities.rectTmp.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(4.0f));
            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), getThemedPaint(Theme.key_paint_chatActionBackground));
            if (hasGradientService()) {
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            canvas.save();
            canvas.translate((this.lastWidth - this.chatNameWidth) / 2.0f, y);
            this.chatNameLayout.draw(canvas);
            canvas.restore();
        }
        if (this.emptyStub || size <= 0.0f) {
            oldAlpha34 = oldAlpha33;
        } else {
            float top2 = ((((-AndroidUtilities.dp(8.0f)) - (AndroidUtilities.dp2(8.0f) * progress)) - size) * (1.0f - this.swipeToReleaseProgress)) + (((-offset) + AndroidUtilities.dp(4.0f)) * this.swipeToReleaseProgress) + bounceOffset;
            this.imageReceiver.setRoundRadius((int) (size / 2.0f));
            this.imageReceiver.setImageCoords(cx - (size / 2.0f), top2, size, size);
            if (this.swipeToReleaseProgress <= 0.0f) {
                oldAlpha34 = oldAlpha33;
                this.imageReceiver.draw(canvas);
            } else {
                oldAlpha34 = oldAlpha33;
                canvas.saveLayerAlpha(this.imageReceiver.getImageX(), this.imageReceiver.getImageY(), this.imageReceiver.getImageWidth() + this.imageReceiver.getImageX(), this.imageReceiver.getImageHeight() + this.imageReceiver.getImageY(), 255, 31);
                this.imageReceiver.draw(canvas);
                float f3 = this.swipeToReleaseProgress;
                canvas.scale(f3, f3, cx + AndroidUtilities.dp(12.0f) + this.counterDrawable.getCenterX(), (top2 - AndroidUtilities.dp(6.0f)) + AndroidUtilities.dp(14.0f));
                canvas.translate(cx + AndroidUtilities.dp(12.0f), top2 - AndroidUtilities.dp(6.0f));
                this.counterDrawable.updateBackgroundRect();
                this.counterDrawable.rectF.inset(-AndroidUtilities.dp(2.0f), -AndroidUtilities.dp(2.0f));
                canvas.drawRoundRect(this.counterDrawable.rectF, this.counterDrawable.rectF.height() / 2.0f, this.counterDrawable.rectF.height() / 2.0f, this.xRefPaint);
                canvas.restore();
                canvas.save();
                float f4 = this.swipeToReleaseProgress;
                canvas.scale(f4, f4, cx + AndroidUtilities.dp(12.0f) + this.counterDrawable.getCenterX(), (top2 - AndroidUtilities.dp(6.0f)) + AndroidUtilities.dp(14.0f));
                canvas.translate(cx + AndroidUtilities.dp(12.0f), top2 - AndroidUtilities.dp(6.0f));
                this.counterDrawable.draw(canvas);
                canvas.restore();
            }
        }
        getThemedPaint(Theme.key_paint_chatActionBackground).setAlpha(oldAlpha);
        Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(oldAlpha1);
        this.textPaint.setAlpha(oldAlpha2);
        this.arrowPaint.setAlpha(oldAlpha34);
        this.imageReceiver.setAlpha(1.0f);
    }

    private void drawCheck(Canvas canvas, float cx, float cy) {
        if (!this.animateCheck) {
            return;
        }
        float f = this.checkProgress;
        if (f < 1.0f) {
            float f2 = f + 0.07272727f;
            this.checkProgress = f2;
            if (f2 > 1.0f) {
                this.checkProgress = 1.0f;
            }
        }
        float f3 = this.checkProgress;
        float p1 = f3 > 0.5f ? 1.0f : f3 / 0.5f;
        float p2 = f3 < 0.5f ? 0.0f : (f3 - 0.5f) / 0.5f;
        canvas.save();
        canvas.clipRect(AndroidUtilities.rectTmp);
        canvas.translate(cx - AndroidUtilities.dp(24.0f), cy - AndroidUtilities.dp(24.0f));
        float x1 = AndroidUtilities.dp(16.0f);
        float y1 = AndroidUtilities.dp(26.0f);
        float x2 = AndroidUtilities.dp(22.0f);
        float y2 = AndroidUtilities.dp(32.0f);
        float x3 = AndroidUtilities.dp(32.0f);
        float y3 = AndroidUtilities.dp(20.0f);
        float x32 = ((1.0f - p1) * y1) + (y2 * p1);
        canvas.drawLine(x1, y1, ((1.0f - p1) * x1) + (x2 * p1), x32, this.arrowPaint);
        if (p2 > 0.0f) {
            canvas.drawLine(x2, y2, ((1.0f - p2) * x2) + (x3 * p2), (y3 * p2) + ((1.0f - p2) * y2), this.arrowPaint);
        }
        canvas.restore();
    }

    private void drawBackground(Canvas canvas, RectF rectTmp) {
        if (this.drawFolderBackground) {
            this.path.reset();
            float roundRadius = rectTmp.width() * 0.2f;
            float folderOffset = rectTmp.width() * 0.1f;
            float folderOffset2 = rectTmp.width() * 0.03f;
            float roundRadius2 = folderOffset / 2.0f;
            float h = rectTmp.height() - folderOffset;
            this.path.moveTo(rectTmp.right, rectTmp.top + roundRadius + folderOffset);
            this.path.rQuadTo(0.0f, -roundRadius, -roundRadius, -roundRadius);
            this.path.rLineTo((((-(rectTmp.width() - (roundRadius * 2.0f))) / 2.0f) + (roundRadius2 * 2.0f)) - folderOffset2, 0.0f);
            this.path.rQuadTo((-roundRadius2) / 2.0f, 0.0f, (-roundRadius2) * 2.0f, (-folderOffset) / 2.0f);
            this.path.rQuadTo((-roundRadius2) / 2.0f, (-folderOffset) / 2.0f, (-roundRadius2) * 2.0f, (-folderOffset) / 2.0f);
            this.path.rLineTo(((-(rectTmp.width() - (roundRadius * 2.0f))) / 2.0f) + (roundRadius2 * 2.0f) + folderOffset2, 0.0f);
            this.path.rQuadTo(-roundRadius, 0.0f, -roundRadius, roundRadius);
            this.path.rLineTo(0.0f, (h + folderOffset) - (roundRadius * 2.0f));
            this.path.rQuadTo(0.0f, roundRadius, roundRadius, roundRadius);
            this.path.rLineTo(rectTmp.width() - (roundRadius * 2.0f), 0.0f);
            this.path.rQuadTo(roundRadius, 0.0f, roundRadius, -roundRadius);
            this.path.rLineTo(0.0f, -(h - (2.0f * roundRadius)));
            this.path.close();
            canvas.drawPath(this.path, getThemedPaint(Theme.key_paint_chatActionBackground));
            if (hasGradientService()) {
                canvas.drawPath(this.path, Theme.chat_actionBackgroundGradientDarkenPaint);
                return;
            }
            return;
        }
        RectF rectF = AndroidUtilities.rectTmp;
        float f = this.circleRadius;
        canvas.drawRoundRect(rectF, f, f, getThemedPaint(Theme.key_paint_chatActionBackground));
        if (hasGradientService()) {
            RectF rectF2 = AndroidUtilities.rectTmp;
            float f2 = this.circleRadius;
            canvas.drawRoundRect(rectF2, f2, f2, Theme.chat_actionBackgroundGradientDarkenPaint);
        }
    }

    private void showReleaseState(boolean show, final View parent) {
        AnimatorSet animatorSet = this.showReleaseAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.showReleaseAnimator.cancel();
        }
        if (show) {
            ValueAnimator out = ValueAnimator.ofFloat(this.swipeToReleaseProgress, 1.0f);
            out.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatPullingDownDrawable.this.m2116xaa989d1b(parent, valueAnimator);
                }
            });
            out.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            out.setDuration(250L);
            this.bounceProgress = 0.0f;
            ValueAnimator bounceUp = ValueAnimator.ofFloat(0.0f, 1.0f);
            bounceUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatPullingDownDrawable.this.m2117x3738c81c(parent, valueAnimator);
                }
            });
            bounceUp.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            bounceUp.setDuration(180L);
            ValueAnimator bounceDown = ValueAnimator.ofFloat(1.0f, -0.5f);
            bounceDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatPullingDownDrawable.this.m2118xc3d8f31d(parent, valueAnimator);
                }
            });
            bounceDown.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            bounceDown.setDuration(120L);
            ValueAnimator bounceOut = ValueAnimator.ofFloat(-0.5f, 0.0f);
            bounceOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatPullingDownDrawable.this.m2119x50791e1e(parent, valueAnimator);
                }
            });
            bounceOut.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            bounceOut.setDuration(100L);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.showReleaseAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatPullingDownDrawable.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ChatPullingDownDrawable.this.bounceProgress = 0.0f;
                    ChatPullingDownDrawable.this.swipeToReleaseProgress = 1.0f;
                    parent.invalidate();
                    ChatPullingDownDrawable.this.fragmentView.invalidate();
                    if (ChatPullingDownDrawable.this.onAnimationFinishRunnable != null) {
                        ChatPullingDownDrawable.this.onAnimationFinishRunnable.run();
                        ChatPullingDownDrawable.this.onAnimationFinishRunnable = null;
                    }
                }
            });
            AnimatorSet bounce = new AnimatorSet();
            bounce.playSequentially(bounceUp, bounceDown, bounceOut);
            this.showReleaseAnimator.playTogether(out, bounce);
            this.showReleaseAnimator.start();
            return;
        }
        ValueAnimator out2 = ValueAnimator.ofFloat(this.swipeToReleaseProgress, 0.0f);
        out2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatPullingDownDrawable.this.m2120xdd19491f(parent, valueAnimator);
            }
        });
        out2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        out2.setDuration(220L);
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.showReleaseAnimator = animatorSet3;
        animatorSet3.playTogether(out2);
        this.showReleaseAnimator.start();
    }

    /* renamed from: lambda$showReleaseState$0$org-telegram-ui-ChatPullingDownDrawable */
    public /* synthetic */ void m2116xaa989d1b(View parent, ValueAnimator animation) {
        this.swipeToReleaseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        parent.invalidate();
        this.fragmentView.invalidate();
    }

    /* renamed from: lambda$showReleaseState$1$org-telegram-ui-ChatPullingDownDrawable */
    public /* synthetic */ void m2117x3738c81c(View parent, ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        parent.invalidate();
    }

    /* renamed from: lambda$showReleaseState$2$org-telegram-ui-ChatPullingDownDrawable */
    public /* synthetic */ void m2118xc3d8f31d(View parent, ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        parent.invalidate();
    }

    /* renamed from: lambda$showReleaseState$3$org-telegram-ui-ChatPullingDownDrawable */
    public /* synthetic */ void m2119x50791e1e(View parent, ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        parent.invalidate();
    }

    /* renamed from: lambda$showReleaseState$4$org-telegram-ui-ChatPullingDownDrawable */
    public /* synthetic */ void m2120xdd19491f(View parent, ValueAnimator animation) {
        this.swipeToReleaseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.fragmentView.invalidate();
        parent.invalidate();
    }

    private void drawArrow(Canvas canvas, float cx, float cy, float size) {
        canvas.save();
        float s = size / AndroidUtilities.dpf2(24.0f);
        canvas.scale(s, s, cx, cy - AndroidUtilities.dp(20.0f));
        canvas.translate(cx - AndroidUtilities.dp2(12.0f), cy - AndroidUtilities.dp(12.0f));
        canvas.drawLine(AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(4.0f), AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(22.0f), this.arrowPaint);
        canvas.drawLine(AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(12.0f), AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(3.5f), this.arrowPaint);
        canvas.drawLine(AndroidUtilities.dpf2(21.5f), AndroidUtilities.dpf2(12.0f), AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(3.5f), this.arrowPaint);
        canvas.restore();
    }

    public void onAttach() {
        this.imageReceiver.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onDetach() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        this.imageReceiver.onDetachedFromWindow();
        this.lastProgress = 0.0f;
        this.lastHapticTime = 0L;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.Dialog dialog;
        if (this.nextDialogId != 0 && (dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.nextDialogId)) != null) {
            this.counterDrawable.setCount(dialog.unread_count, true);
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public static TLRPC.Dialog getNextUnreadDialog(long currentDialogId, int folderId, int filterId) {
        return getNextUnreadDialog(currentDialogId, folderId, filterId, true, null);
    }

    public static TLRPC.Dialog getNextUnreadDialog(long currentDialogId, int folderId, int filterId, boolean searchNext, int[] params) {
        ArrayList<TLRPC.Dialog> dialogs;
        TLRPC.Dialog dialog;
        TLRPC.Dialog dialog2;
        MessagesController messagesController = AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController();
        if (params != null) {
            params[0] = 0;
            params[1] = folderId;
            params[2] = filterId;
        }
        if (filterId != 0) {
            MessagesController.DialogFilter filter = messagesController.dialogFiltersById.get(filterId);
            if (filter == null) {
                return null;
            }
            dialogs = filter.dialogs;
        } else {
            dialogs = messagesController.getDialogs(folderId);
        }
        if (dialogs == null) {
            return null;
        }
        for (int i = 0; i < dialogs.size(); i++) {
            TLRPC.Dialog dialog3 = dialogs.get(i);
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-dialog3.id));
            if (chat != null && dialog3.id != currentDialogId && dialog3.unread_count > 0 && DialogObject.isChannel(dialog3) && !chat.megagroup && !messagesController.isPromoDialog(dialog3.id, false)) {
                String reason = MessagesController.getRestrictionReason(chat.restriction_reason);
                if (reason == null) {
                    return dialog3;
                }
            }
        }
        if (searchNext) {
            if (filterId != 0) {
                for (int i2 = 0; i2 < messagesController.dialogFilters.size(); i2++) {
                    int newFilterId = messagesController.dialogFilters.get(i2).id;
                    if (filterId != newFilterId && (dialog2 = getNextUnreadDialog(currentDialogId, folderId, newFilterId, false, params)) != null) {
                        if (params != null) {
                            params[0] = 1;
                        }
                        return dialog2;
                    }
                }
            }
            for (int i3 = 0; i3 < messagesController.dialogsByFolder.size(); i3++) {
                int newFolderId = messagesController.dialogsByFolder.keyAt(i3);
                if (folderId != newFolderId && (dialog = getNextUnreadDialog(currentDialogId, newFolderId, 0, false, params)) != null) {
                    if (params != null) {
                        params[0] = 1;
                    }
                    return dialog;
                }
            }
        }
        return null;
    }

    public long getChatId() {
        return this.nextChat.id;
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x006c  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00b1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawBottomPanel(android.graphics.Canvas r12, int r13, int r14, int r15) {
        /*
            Method dump skipped, instructions count: 250
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatPullingDownDrawable.drawBottomPanel(android.graphics.Canvas, int, int, int):void");
    }

    public void showBottomPanel(boolean b) {
        this.showBottomPanel = b;
        this.fragmentView.invalidate();
    }

    public boolean needDrawBottomPanel() {
        return (this.showBottomPanel || this.progressToBottomPanel > 0.0f) && !this.emptyStub;
    }

    public boolean animationIsRunning() {
        return this.swipeToReleaseProgress != 1.0f;
    }

    public void runOnAnimationFinish(Runnable runnable) {
        AnimatorSet animatorSet = this.showReleaseAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.showReleaseAnimator.cancel();
        }
        this.onAnimationFinishRunnable = runnable;
        this.showReleaseAnimator = new AnimatorSet();
        ValueAnimator out = ValueAnimator.ofFloat(this.swipeToReleaseProgress, 1.0f);
        out.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatPullingDownDrawable.this.m2114x9c73c7c6(valueAnimator);
            }
        });
        ValueAnimator bounceOut = ValueAnimator.ofFloat(this.bounceProgress, 0.0f);
        bounceOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatPullingDownDrawable.this.m2115x2913f2c7(valueAnimator);
            }
        });
        this.showReleaseAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatPullingDownDrawable.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ChatPullingDownDrawable.this.bounceProgress = 0.0f;
                ChatPullingDownDrawable.this.swipeToReleaseProgress = 1.0f;
                if (ChatPullingDownDrawable.this.parentView != null) {
                    ChatPullingDownDrawable.this.parentView.invalidate();
                }
                ChatPullingDownDrawable.this.fragmentView.invalidate();
                if (ChatPullingDownDrawable.this.onAnimationFinishRunnable != null) {
                    ChatPullingDownDrawable.this.onAnimationFinishRunnable.run();
                    ChatPullingDownDrawable.this.onAnimationFinishRunnable = null;
                }
            }
        });
        this.showReleaseAnimator.playTogether(out, bounceOut);
        this.showReleaseAnimator.setDuration(120L);
        this.showReleaseAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.showReleaseAnimator.start();
    }

    /* renamed from: lambda$runOnAnimationFinish$5$org-telegram-ui-ChatPullingDownDrawable */
    public /* synthetic */ void m2114x9c73c7c6(ValueAnimator animation) {
        this.swipeToReleaseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.fragmentView.invalidate();
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    /* renamed from: lambda$runOnAnimationFinish$6$org-telegram-ui-ChatPullingDownDrawable */
    public /* synthetic */ void m2115x2913f2c7(ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void reset() {
        this.checkProgress = 0.0f;
        this.animateCheck = false;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }
}
