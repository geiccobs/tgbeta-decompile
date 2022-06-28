package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.FlickerLoadingView;
/* loaded from: classes4.dex */
public class SharedPhotoVideoCell2 extends View {
    static boolean lastAutoDownload;
    static long lastUpdateDownloadSettingsTime;
    ValueAnimator animator;
    private boolean attached;
    CheckBoxBase checkBoxBase;
    float checkBoxProgress;
    float crossfadeProgress;
    float crossfadeToColumnsCount;
    SharedPhotoVideoCell2 crossfadeView;
    int currentAccount;
    MessageObject currentMessageObject;
    int currentParentColumnsCount;
    FlickerLoadingView globalGradientView;
    float highlightProgress;
    SharedResources sharedResources;
    boolean showVideoLayout;
    StaticLayout videoInfoLayot;
    String videoText;
    public ImageReceiver imageReceiver = new ImageReceiver();
    float imageAlpha = 1.0f;
    float imageScale = 1.0f;

    public SharedPhotoVideoCell2(Context context, SharedResources sharedResources, int currentAccount) {
        super(context);
        this.sharedResources = sharedResources;
        this.currentAccount = currentAccount;
        setChecked(false, false);
        this.imageReceiver.setParentView(this);
    }

    public void setMessageObject(MessageObject messageObject, int parentColumnsCount) {
        int stride;
        TLRPC.PhotoSize currentPhotoObjectThumb;
        TLRPC.PhotoSize qualityThumb;
        int stride2;
        String imageFilter;
        int oldParentColumsCount = this.currentParentColumnsCount;
        this.currentParentColumnsCount = parentColumnsCount;
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 == null && messageObject == null) {
            return;
        }
        if (messageObject2 != null && messageObject != null && messageObject2.getId() == messageObject.getId() && oldParentColumsCount == parentColumnsCount) {
            return;
        }
        this.currentMessageObject = messageObject;
        if (messageObject == null) {
            this.imageReceiver.onDetachedFromWindow();
            this.videoText = null;
            this.videoInfoLayot = null;
            this.showVideoLayout = false;
            return;
        }
        if (this.attached) {
            this.imageReceiver.onAttachedToWindow();
        }
        String restrictionReason = MessagesController.getRestrictionReason(messageObject.messageOwner.restriction_reason);
        int width = (int) ((AndroidUtilities.displaySize.x / parentColumnsCount) / AndroidUtilities.density);
        String imageFilter2 = this.sharedResources.getFilterString(width);
        boolean showImageStub = false;
        if (parentColumnsCount <= 2) {
            stride = AndroidUtilities.getPhotoSize();
        } else if (parentColumnsCount == 3) {
            stride = 320;
        } else if (parentColumnsCount == 5) {
            stride = 320;
        } else {
            stride = 320;
        }
        this.videoText = null;
        this.videoInfoLayot = null;
        this.showVideoLayout = false;
        if (!TextUtils.isEmpty(restrictionReason)) {
            showImageStub = true;
        } else if (messageObject.isVideo()) {
            this.showVideoLayout = true;
            if (parentColumnsCount != 9) {
                this.videoText = AndroidUtilities.formatShortDuration(messageObject.getDuration());
            }
            if (messageObject.mediaThumb != null) {
                if (messageObject.strippedThumb != null) {
                    this.imageReceiver.setImage(messageObject.mediaThumb, imageFilter2, messageObject.strippedThumb, null, messageObject, 0);
                } else {
                    this.imageReceiver.setImage(messageObject.mediaThumb, imageFilter2, messageObject.mediaSmallThumb, imageFilter2 + "_b", null, 0L, null, messageObject, 0);
                }
            } else {
                TLRPC.Document document = messageObject.getDocument();
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                TLRPC.PhotoSize qualityThumb2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, stride);
                if (thumb != qualityThumb2) {
                    qualityThumb = qualityThumb2;
                } else {
                    qualityThumb = null;
                }
                if (thumb == null) {
                    stride2 = stride;
                    imageFilter = imageFilter2;
                    showImageStub = true;
                } else if (messageObject.strippedThumb != null) {
                    this.imageReceiver.setImage(ImageLocation.getForDocument(qualityThumb, document), imageFilter2, messageObject.strippedThumb, null, messageObject, 0);
                    stride2 = stride;
                    imageFilter = imageFilter2;
                } else {
                    imageFilter = imageFilter2;
                    stride2 = stride;
                    this.imageReceiver.setImage(ImageLocation.getForDocument(qualityThumb, document), imageFilter2, ImageLocation.getForDocument(thumb, document), imageFilter2 + "_b", null, 0L, null, messageObject, 0);
                }
            }
        } else {
            int stride3 = stride;
            if ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && messageObject.messageOwner.media.photo != null && !messageObject.photoThumbs.isEmpty()) {
                if (messageObject.mediaExists || canAutoDownload(messageObject)) {
                    if (messageObject.mediaThumb != null) {
                        if (messageObject.strippedThumb != null) {
                            this.imageReceiver.setImage(messageObject.mediaThumb, imageFilter2, messageObject.strippedThumb, null, messageObject, 0);
                        } else {
                            this.imageReceiver.setImage(messageObject.mediaThumb, imageFilter2, messageObject.mediaSmallThumb, imageFilter2 + "_b", null, 0L, null, messageObject, 0);
                        }
                    } else {
                        TLRPC.PhotoSize currentPhotoObjectThumb2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50);
                        TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, stride3, false, currentPhotoObjectThumb2, false);
                        if (currentPhotoObject != currentPhotoObjectThumb2) {
                            currentPhotoObjectThumb = currentPhotoObjectThumb2;
                        } else {
                            currentPhotoObjectThumb = null;
                        }
                        if (messageObject.strippedThumb == null) {
                            this.imageReceiver.setImage(ImageLocation.getForObject(currentPhotoObject, messageObject.photoThumbsObject), imageFilter2, ImageLocation.getForObject(currentPhotoObjectThumb, messageObject.photoThumbsObject), imageFilter2 + "_b", currentPhotoObject != null ? currentPhotoObject.size : 0L, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                        } else {
                            this.imageReceiver.setImage(ImageLocation.getForObject(currentPhotoObject, messageObject.photoThumbsObject), imageFilter2, null, null, messageObject.strippedThumb, currentPhotoObject != null ? currentPhotoObject.size : 0L, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                        }
                    }
                } else if (messageObject.strippedThumb != null) {
                    this.imageReceiver.setImage(null, null, null, null, messageObject.strippedThumb, 0L, null, messageObject, 0);
                } else {
                    this.imageReceiver.setImage(null, null, ImageLocation.getForObject(FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50), messageObject.photoThumbsObject), "b", null, 0L, null, messageObject, 0);
                }
            } else {
                showImageStub = true;
            }
        }
        if (showImageStub) {
            this.imageReceiver.setImageBitmap(ContextCompat.getDrawable(getContext(), R.drawable.photo_placeholder_in));
        }
        invalidate();
    }

    private boolean canAutoDownload(MessageObject messageObject) {
        if (System.currentTimeMillis() - lastUpdateDownloadSettingsTime > DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
            lastUpdateDownloadSettingsTime = System.currentTimeMillis();
            lastAutoDownload = DownloadController.getInstance(this.currentAccount).canDownloadMedia(messageObject);
        }
        return lastAutoDownload;
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x00b4  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0104  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x010c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x010d  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onDraw(android.graphics.Canvas r26) {
        /*
            Method dump skipped, instructions count: 757
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedPhotoVideoCell2.onDraw(android.graphics.Canvas):void");
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        CheckBoxBase checkBoxBase = this.checkBoxBase;
        if (checkBoxBase != null) {
            checkBoxBase.onAttachedToWindow();
        }
        if (this.currentMessageObject != null) {
            this.imageReceiver.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        CheckBoxBase checkBoxBase = this.checkBoxBase;
        if (checkBoxBase != null) {
            checkBoxBase.onDetachedFromWindow();
        }
        if (this.currentMessageObject != null) {
            this.imageReceiver.onDetachedFromWindow();
        }
    }

    public void setGradientView(FlickerLoadingView globalGradientView) {
        this.globalGradientView = globalGradientView;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED));
    }

    public int getMessageId() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            return messageObject.getId();
        }
        return 0;
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public void setImageAlpha(float alpha, boolean invalidate) {
        if (this.imageAlpha != alpha) {
            this.imageAlpha = alpha;
            if (invalidate) {
                invalidate();
            }
        }
    }

    public void setImageScale(float scale, boolean invalidate) {
        if (this.imageScale != scale) {
            this.imageScale = scale;
            if (invalidate) {
                invalidate();
            }
        }
    }

    public void setCrossfadeView(SharedPhotoVideoCell2 cell, float crossfadeProgress, int crossfadeToColumnsCount) {
        this.crossfadeView = cell;
        this.crossfadeProgress = crossfadeProgress;
        this.crossfadeToColumnsCount = crossfadeToColumnsCount;
    }

    public void drawCrossafadeImage(Canvas canvas) {
        if (this.crossfadeView != null) {
            canvas.save();
            canvas.translate(getX(), getY());
            float scale = ((getMeasuredWidth() - AndroidUtilities.dp(2.0f)) * this.imageScale) / (this.crossfadeView.getMeasuredWidth() - AndroidUtilities.dp(2.0f));
            this.crossfadeView.setImageScale(scale, false);
            this.crossfadeView.draw(canvas);
            canvas.restore();
        }
    }

    public View getCrossfadeView() {
        return this.crossfadeView;
    }

    public void setChecked(final boolean checked, boolean animated) {
        CheckBoxBase checkBoxBase = this.checkBoxBase;
        boolean currentIsChecked = checkBoxBase != null && checkBoxBase.isChecked();
        if (currentIsChecked == checked) {
            return;
        }
        if (this.checkBoxBase == null) {
            CheckBoxBase checkBoxBase2 = new CheckBoxBase(this, 21, null);
            this.checkBoxBase = checkBoxBase2;
            checkBoxBase2.setColor(null, Theme.key_sharedMedia_photoPlaceholder, Theme.key_checkboxCheck);
            this.checkBoxBase.setDrawUnchecked(false);
            this.checkBoxBase.setBackgroundType(1);
            this.checkBoxBase.setBounds(0, 0, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            if (this.attached) {
                this.checkBoxBase.onAttachedToWindow();
            }
        }
        this.checkBoxBase.setChecked(checked, animated);
        if (this.animator != null) {
            ValueAnimator animatorFinal = this.animator;
            this.animator = null;
            animatorFinal.cancel();
        }
        float f = 1.0f;
        if (animated) {
            float[] fArr = new float[2];
            fArr[0] = this.checkBoxProgress;
            if (!checked) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.SharedPhotoVideoCell2.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SharedPhotoVideoCell2.this.checkBoxProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    SharedPhotoVideoCell2.this.invalidate();
                }
            });
            this.animator.setDuration(200L);
            this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.SharedPhotoVideoCell2.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (SharedPhotoVideoCell2.this.animator != null && SharedPhotoVideoCell2.this.animator.equals(animation)) {
                        SharedPhotoVideoCell2.this.checkBoxProgress = checked ? 1.0f : 0.0f;
                        SharedPhotoVideoCell2.this.animator = null;
                    }
                }
            });
            this.animator.start();
        } else {
            if (!checked) {
                f = 0.0f;
            }
            this.checkBoxProgress = f;
        }
        invalidate();
    }

    public void startHighlight() {
    }

    public void setHighlightProgress(float p) {
        if (this.highlightProgress != p) {
            this.highlightProgress = p;
            invalidate();
        }
    }

    public void moveImageToFront() {
        this.imageReceiver.moveImageToFront();
    }

    /* loaded from: classes4.dex */
    public static class SharedResources {
        Drawable playDrawable;
        TextPaint textPaint = new TextPaint(1);
        private Paint backgroundPaint = new Paint();
        Paint highlightPaint = new Paint();
        SparseArray<String> imageFilters = new SparseArray<>();

        public SharedResources(Context context, Theme.ResourcesProvider resourcesProvider) {
            this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            this.textPaint.setColor(-1);
            this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.play_mini_video);
            this.playDrawable = drawable;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), this.playDrawable.getIntrinsicHeight());
            this.backgroundPaint.setColor(Theme.getColor(Theme.key_sharedMedia_photoPlaceholder, resourcesProvider));
        }

        public String getFilterString(int width) {
            String str = this.imageFilters.get(width);
            if (str == null) {
                String str2 = width + "_" + width + "_isc";
                this.imageFilters.put(width, str2);
                return str2;
            }
            return str;
        }
    }
}
