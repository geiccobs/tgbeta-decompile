package org.telegram.ui.Components.Premium;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Premium.CarouselView;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
/* loaded from: classes5.dex */
public class ReactionDrawingObject extends CarouselView.DrawingObject {
    long lastSelectedTime;
    private View parentView;
    private int position;
    TLRPC.TL_availableReaction reaction;
    private boolean selected;
    private float selectedProgress;
    ImageReceiver imageReceiver = new ImageReceiver();
    ImageReceiver actionReceiver = new ImageReceiver();
    ImageReceiver effectImageReceiver = new ImageReceiver();
    Rect rect = new Rect();

    public ReactionDrawingObject(int i) {
        this.position = i;
    }

    @Override // org.telegram.ui.Components.Premium.CarouselView.DrawingObject
    public void onAttachToWindow(View parentView, int i) {
        this.parentView = parentView;
        if (i == 0) {
            this.imageReceiver.setParentView(parentView);
            this.imageReceiver.onAttachedToWindow();
            this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(this.reaction.activate_animation, Theme.key_windowBackgroundGray, 0.5f);
            this.actionReceiver.setParentView(parentView);
            this.actionReceiver.onAttachedToWindow();
            this.actionReceiver.setLayerNum(Integer.MAX_VALUE);
            this.actionReceiver.setAllowStartLottieAnimation(false);
            this.actionReceiver.setImage(ImageLocation.getForDocument(this.reaction.activate_animation), "50_50_nolimit", null, null, svgThumb, 0L, "tgs", this.reaction, 0);
            this.actionReceiver.setAutoRepeat(0);
            if (this.actionReceiver.getLottieAnimation() != null) {
                this.actionReceiver.getLottieAnimation().setCurrentFrame(0, false);
                this.actionReceiver.getLottieAnimation().stop();
                return;
            }
            return;
        }
        this.effectImageReceiver.setParentView(parentView);
        this.effectImageReceiver.onAttachedToWindow();
        this.effectImageReceiver.setLayerNum(Integer.MAX_VALUE);
        this.effectImageReceiver.setAllowStartLottieAnimation(false);
        int size = ReactionsEffectOverlay.sizeForBigReaction();
        ImageReceiver imageReceiver = this.effectImageReceiver;
        ImageLocation forDocument = ImageLocation.getForDocument(this.reaction.around_animation);
        imageReceiver.setImage(forDocument, size + "_" + size, null, null, null, 0L, "tgs", this.reaction, 0);
        this.effectImageReceiver.setAutoRepeat(0);
        if (this.effectImageReceiver.getLottieAnimation() != null) {
            this.effectImageReceiver.getLottieAnimation().setCurrentFrame(0, false);
            this.effectImageReceiver.getLottieAnimation().stop();
        }
    }

    @Override // org.telegram.ui.Components.Premium.CarouselView.DrawingObject
    public void onDetachFromWindow() {
        this.imageReceiver.onDetachedFromWindow();
        this.imageReceiver.setParentView(null);
        this.effectImageReceiver.onDetachedFromWindow();
        this.effectImageReceiver.setParentView(null);
        this.actionReceiver.onDetachedFromWindow();
        this.actionReceiver.setParentView(null);
    }

    @Override // org.telegram.ui.Components.Premium.CarouselView.DrawingObject
    public void draw(Canvas canvas, float cX, float cY, float globalScale) {
        int imageSize = (int) (AndroidUtilities.dp(120.0f) * globalScale);
        int effectSize = (int) (AndroidUtilities.dp(350.0f) * globalScale);
        this.rect.set((int) (cX - (imageSize / 2.0f)), (int) (cY - (imageSize / 2.0f)), (int) ((imageSize / 2.0f) + cX), (int) ((imageSize / 2.0f) + cY));
        this.imageReceiver.setImageCoords(cX - (imageSize / 2.0f), cY - (imageSize / 2.0f), imageSize, imageSize);
        this.actionReceiver.setImageCoords(cX - (imageSize / 2.0f), cY - (imageSize / 2.0f), imageSize, imageSize);
        if (this.actionReceiver.getLottieAnimation() != null && this.actionReceiver.getLottieAnimation().hasBitmap()) {
            this.actionReceiver.draw(canvas);
            if ((this.actionReceiver.getLottieAnimation() == null || !this.actionReceiver.getLottieAnimation().isLastFrame()) && this.selected && this.actionReceiver.getLottieAnimation() != null && !this.actionReceiver.getLottieAnimation().isRunning()) {
                this.actionReceiver.getLottieAnimation().start();
            }
        }
        if (this.selected || this.selectedProgress != 0.0f) {
            this.effectImageReceiver.setImageCoords(cX - (effectSize / 2.0f), cY - (effectSize / 2.0f), effectSize, effectSize);
            this.effectImageReceiver.setAlpha(this.selectedProgress);
            float f = this.selectedProgress;
            if (f != 1.0f) {
                float s = (f * 0.3f) + 0.7f;
                canvas.save();
                canvas.scale(s, s, cX, cY);
                this.effectImageReceiver.draw(canvas);
                canvas.restore();
            } else {
                this.effectImageReceiver.draw(canvas);
            }
            if (this.selected && this.effectImageReceiver.getLottieAnimation() != null && this.effectImageReceiver.getLottieAnimation().isLastFrame()) {
                this.carouselView.autoplayToNext();
            }
            if (this.selected && this.effectImageReceiver.getLottieAnimation() != null && !this.effectImageReceiver.getLottieAnimation().isRunning() && !this.effectImageReceiver.getLottieAnimation().isLastFrame()) {
                this.effectImageReceiver.getLottieAnimation().start();
            }
            if (this.selected && this.effectImageReceiver.getLottieAnimation() != null && !this.effectImageReceiver.getLottieAnimation().isRunning() && this.effectImageReceiver.getLottieAnimation().isLastFrame()) {
                this.selected = false;
            }
            boolean z = this.selected;
            if (z) {
                float f2 = this.selectedProgress;
                if (f2 != 1.0f) {
                    float f3 = f2 + 0.08f;
                    this.selectedProgress = f3;
                    if (f3 > 1.0f) {
                        this.selectedProgress = 1.0f;
                        return;
                    }
                    return;
                }
            }
            if (!z) {
                float f4 = this.selectedProgress - 0.08f;
                this.selectedProgress = f4;
                if (f4 < 0.0f) {
                    this.selectedProgress = 0.0f;
                }
            }
        }
    }

    @Override // org.telegram.ui.Components.Premium.CarouselView.DrawingObject
    public boolean checkTap(float x, float y) {
        if (this.rect.contains((int) x, (int) y)) {
            select();
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.Components.Premium.CarouselView.DrawingObject
    public void select() {
        if (this.selected) {
            return;
        }
        this.selected = true;
        if (this.selectedProgress == 0.0f) {
            this.selectedProgress = 1.0f;
        }
        this.lastSelectedTime = System.currentTimeMillis();
        if (this.effectImageReceiver.getLottieAnimation() != null) {
            this.effectImageReceiver.getLottieAnimation().setCurrentFrame(0, false);
            this.effectImageReceiver.getLottieAnimation().start();
        }
        if (this.actionReceiver.getLottieAnimation() != null) {
            this.actionReceiver.getLottieAnimation().setCurrentFrame(0, false);
            this.actionReceiver.getLottieAnimation().start();
        }
        this.parentView.invalidate();
    }

    @Override // org.telegram.ui.Components.Premium.CarouselView.DrawingObject
    public void hideAnimation() {
        super.hideAnimation();
        this.selected = false;
    }

    public void set(TLRPC.TL_availableReaction reaction) {
        this.reaction = reaction;
    }
}
