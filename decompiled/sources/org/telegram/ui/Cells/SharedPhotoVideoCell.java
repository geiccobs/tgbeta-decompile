package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes4.dex */
public class SharedPhotoVideoCell extends FrameLayout {
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_GLOBAL_SEARCH = 1;
    private Paint backgroundPaint;
    private int currentAccount;
    private SharedPhotoVideoCellDelegate delegate;
    private boolean ignoreLayout;
    private int[] indeces;
    private boolean isFirst;
    private int itemsCount;
    private MessageObject[] messageObjects;
    private PhotoVideoView[] photoVideoViews;
    private int type;

    /* loaded from: classes4.dex */
    public interface SharedPhotoVideoCellDelegate {
        void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);

        boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2);
    }

    public SharedPhotoVideoCellDelegate getDelegate() {
        return this.delegate;
    }

    /* loaded from: classes4.dex */
    public class PhotoVideoView extends FrameLayout {
        private AnimatorSet animator;
        private CheckBox2 checkBox;
        private FrameLayout container;
        private MessageObject currentMessageObject;
        private BackupImageView imageView;
        private View selector;
        private FrameLayout videoInfoContainer;
        private TextView videoTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public PhotoVideoView(Context context) {
            super(context);
            SharedPhotoVideoCell.this = this$0;
            setWillNotDraw(false);
            FrameLayout frameLayout = new FrameLayout(context);
            this.container = frameLayout;
            addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            backupImageView.getImageReceiver().setNeedsQualityThumb(true);
            this.imageView.getImageReceiver().setShouldGenerateQualityThumb(true);
            this.container.addView(this.imageView, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Cells.SharedPhotoVideoCell.PhotoVideoView.1
                private RectF rect = new RectF();

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                }
            };
            this.videoInfoContainer = frameLayout2;
            frameLayout2.setWillNotDraw(false);
            this.videoInfoContainer.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), 0);
            this.container.addView(this.videoInfoContainer, LayoutHelper.createFrame(-2, 17.0f, 83, 4.0f, 0.0f, 0.0f, 4.0f));
            ImageView imageView1 = new ImageView(context);
            imageView1.setImageResource(R.drawable.play_mini_video);
            this.videoInfoContainer.addView(imageView1, LayoutHelper.createFrame(-2, -2, 19));
            TextView textView = new TextView(context);
            this.videoTextView = textView;
            textView.setTextColor(-1);
            this.videoTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.videoTextView.setTextSize(1, 12.0f);
            this.videoTextView.setImportantForAccessibility(2);
            this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 13.0f, -0.7f, 0.0f, 0.0f));
            View view = new View(context);
            this.selector = view;
            view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(null, Theme.key_sharedMedia_photoPlaceholder, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(1);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 1.0f, 1.0f, 0.0f));
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }

        public void setChecked(boolean checked, boolean animated) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout = this.container;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = checked ? 0.81f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
                FrameLayout frameLayout2 = this.container;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                if (checked) {
                    f = 0.81f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200L);
                this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.SharedPhotoVideoCell.PhotoVideoView.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animation)) {
                            PhotoVideoView.this.animator = null;
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        if (PhotoVideoView.this.animator != null && PhotoVideoView.this.animator.equals(animation)) {
                            PhotoVideoView.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            this.container.setScaleX(checked ? 0.85f : 1.0f);
            FrameLayout frameLayout3 = this.container;
            if (checked) {
                f = 0.85f;
            }
            frameLayout3.setScaleY(f);
        }

        public void setMessageObject(MessageObject messageObject) {
            TLRPC.PhotoSize currentPhotoObjectThumb;
            TLRPC.PhotoSize qualityThumb;
            this.currentMessageObject = messageObject;
            this.imageView.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(messageObject), false);
            String restrictionReason = MessagesController.getRestrictionReason(messageObject.messageOwner.restriction_reason);
            if (!TextUtils.isEmpty(restrictionReason)) {
                this.videoInfoContainer.setVisibility(4);
                this.imageView.setImageResource(R.drawable.photo_placeholder_in);
            } else if (messageObject.isVideo()) {
                this.videoInfoContainer.setVisibility(0);
                this.videoTextView.setText(AndroidUtilities.formatShortDuration(messageObject.getDuration()));
                TLRPC.Document document = messageObject.getDocument();
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                TLRPC.PhotoSize qualityThumb2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, GroupCallActivity.TABLET_LIST_SIZE);
                if (thumb != qualityThumb2) {
                    qualityThumb = qualityThumb2;
                } else {
                    qualityThumb = null;
                }
                if (thumb != null) {
                    if (messageObject.strippedThumb != null) {
                        this.imageView.setImage(ImageLocation.getForDocument(qualityThumb, document), "100_100", (String) null, messageObject.strippedThumb, messageObject);
                        return;
                    } else {
                        this.imageView.setImage(ImageLocation.getForDocument(qualityThumb, document), "100_100", ImageLocation.getForDocument(thumb, document), "b", ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.photo_placeholder_in), null, null, 0, messageObject);
                        return;
                    }
                }
                this.imageView.setImageResource(R.drawable.photo_placeholder_in);
            } else if ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && messageObject.messageOwner.media.photo != null && !messageObject.photoThumbs.isEmpty()) {
                this.videoInfoContainer.setVisibility(4);
                TLRPC.PhotoSize currentPhotoObjectThumb2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50);
                TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, GroupCallActivity.TABLET_LIST_SIZE, false, currentPhotoObjectThumb2, false);
                if (messageObject.mediaExists || DownloadController.getInstance(SharedPhotoVideoCell.this.currentAccount).canDownloadMedia(messageObject)) {
                    if (currentPhotoObject != currentPhotoObjectThumb2) {
                        currentPhotoObjectThumb = currentPhotoObjectThumb2;
                    } else {
                        currentPhotoObjectThumb = null;
                    }
                    long j = 0;
                    if (messageObject.strippedThumb != null) {
                        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
                        ImageLocation forObject = ImageLocation.getForObject(currentPhotoObject, messageObject.photoThumbsObject);
                        BitmapDrawable bitmapDrawable = messageObject.strippedThumb;
                        if (currentPhotoObject != null) {
                            j = currentPhotoObject.size;
                        }
                        imageReceiver.setImage(forObject, "100_100", null, null, bitmapDrawable, j, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                        return;
                    }
                    ImageReceiver imageReceiver2 = this.imageView.getImageReceiver();
                    ImageLocation forObject2 = ImageLocation.getForObject(currentPhotoObject, messageObject.photoThumbsObject);
                    ImageLocation forObject3 = ImageLocation.getForObject(currentPhotoObjectThumb, messageObject.photoThumbsObject);
                    if (currentPhotoObject != null) {
                        j = currentPhotoObject.size;
                    }
                    imageReceiver2.setImage(forObject2, "100_100", forObject3, "b", j, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                } else if (messageObject.strippedThumb != null) {
                    this.imageView.setImage(null, null, null, null, messageObject.strippedThumb, null, null, 0, messageObject);
                } else {
                    this.imageView.setImage(null, null, ImageLocation.getForObject(currentPhotoObjectThumb2, messageObject.photoThumbsObject), "b", ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.photo_placeholder_in), null, null, 0, messageObject);
                }
            } else {
                this.videoInfoContainer.setVisibility(4);
                this.imageView.setImageResource(R.drawable.photo_placeholder_in);
            }
        }

        @Override // android.view.View
        public void clearAnimation() {
            super.clearAnimation();
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || PhotoViewer.isShowingImage(this.currentMessageObject)) {
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), SharedPhotoVideoCell.this.backgroundPaint);
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            if (this.currentMessageObject.isVideo()) {
                info.setText(LocaleController.getString("AttachVideo", R.string.AttachVideo) + ", " + LocaleController.formatDuration(this.currentMessageObject.getDuration()));
            } else {
                info.setText(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
            }
            if (this.checkBox.isChecked()) {
                info.setCheckable(true);
                info.setChecked(true);
            }
        }
    }

    public SharedPhotoVideoCell(Context context) {
        this(context, 0);
    }

    public SharedPhotoVideoCell(Context context, int type) {
        super(context);
        this.backgroundPaint = new Paint();
        this.currentAccount = UserConfig.selectedAccount;
        this.type = type;
        this.backgroundPaint.setColor(Theme.getColor(Theme.key_sharedMedia_photoPlaceholder));
        this.messageObjects = new MessageObject[6];
        this.photoVideoViews = new PhotoVideoView[6];
        this.indeces = new int[6];
        for (int a = 0; a < 6; a++) {
            this.photoVideoViews[a] = new PhotoVideoView(context);
            addView(this.photoVideoViews[a]);
            this.photoVideoViews[a].setVisibility(4);
            this.photoVideoViews[a].setTag(Integer.valueOf(a));
            this.photoVideoViews[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.SharedPhotoVideoCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SharedPhotoVideoCell.this.m1670lambda$new$0$orgtelegramuiCellsSharedPhotoVideoCell(view);
                }
            });
            this.photoVideoViews[a].setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Cells.SharedPhotoVideoCell$$ExternalSyntheticLambda1
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    return SharedPhotoVideoCell.this.m1671lambda$new$1$orgtelegramuiCellsSharedPhotoVideoCell(view);
                }
            });
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-SharedPhotoVideoCell */
    public /* synthetic */ void m1670lambda$new$0$orgtelegramuiCellsSharedPhotoVideoCell(View v) {
        if (this.delegate != null) {
            int a1 = ((Integer) v.getTag()).intValue();
            this.delegate.didClickItem(this, this.indeces[a1], this.messageObjects[a1], a1);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-SharedPhotoVideoCell */
    public /* synthetic */ boolean m1671lambda$new$1$orgtelegramuiCellsSharedPhotoVideoCell(View v) {
        if (this.delegate != null) {
            int a12 = ((Integer) v.getTag()).intValue();
            return this.delegate.didLongClickItem(this, this.indeces[a12], this.messageObjects[a12], a12);
        }
        return false;
    }

    public void updateCheckboxColor() {
        for (int a = 0; a < 6; a++) {
            this.photoVideoViews[a].checkBox.invalidate();
        }
    }

    @Override // android.view.View
    public void invalidate() {
        for (int a = 0; a < 6; a++) {
            this.photoVideoViews[a].invalidate();
        }
        super.invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setDelegate(SharedPhotoVideoCellDelegate delegate) {
        this.delegate = delegate;
    }

    public void setItemsCount(int count) {
        int a = 0;
        while (true) {
            PhotoVideoView[] photoVideoViewArr = this.photoVideoViews;
            if (a < photoVideoViewArr.length) {
                photoVideoViewArr[a].clearAnimation();
                this.photoVideoViews[a].setVisibility(a < count ? 0 : 4);
                a++;
            } else {
                this.itemsCount = count;
                return;
            }
        }
    }

    public BackupImageView getImageView(int a) {
        if (a < this.itemsCount) {
            return this.photoVideoViews[a].imageView;
        }
        return null;
    }

    public PhotoVideoView getView(int a) {
        if (a >= this.itemsCount) {
            return null;
        }
        return this.photoVideoViews[a];
    }

    public MessageObject getMessageObject(int a) {
        if (a >= this.itemsCount) {
            return null;
        }
        return this.messageObjects[a];
    }

    public int getIndeces(int a) {
        if (a >= this.itemsCount) {
            return -1;
        }
        return this.indeces[a];
    }

    public void setIsFirst(boolean first) {
        this.isFirst = first;
    }

    public void setChecked(int a, boolean checked, boolean animated) {
        this.photoVideoViews[a].setChecked(checked, animated);
    }

    public void setItem(int a, int index, MessageObject messageObject) {
        this.messageObjects[a] = messageObject;
        this.indeces[a] = index;
        if (messageObject != null) {
            this.photoVideoViews[a].setVisibility(0);
            this.photoVideoViews[a].setMessageObject(messageObject);
            return;
        }
        this.photoVideoViews[a].clearAnimation();
        this.photoVideoViews[a].setVisibility(4);
        this.messageObjects[a] = null;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int itemWidth;
        int i;
        if (this.type == 1) {
            itemWidth = (View.MeasureSpec.getSize(widthMeasureSpec) - ((this.itemsCount - 1) * AndroidUtilities.dp(2.0f))) / this.itemsCount;
        } else {
            itemWidth = getItemSize(this.itemsCount);
        }
        this.ignoreLayout = true;
        int a = 0;
        while (true) {
            i = 0;
            if (a >= this.itemsCount) {
                break;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.photoVideoViews[a].getLayoutParams();
            if (!this.isFirst) {
                i = AndroidUtilities.dp(2.0f);
            }
            layoutParams.topMargin = i;
            layoutParams.leftMargin = (AndroidUtilities.dp(2.0f) + itemWidth) * a;
            if (a == this.itemsCount - 1) {
                if (AndroidUtilities.isTablet()) {
                    layoutParams.width = AndroidUtilities.dp(490.0f) - ((this.itemsCount - 1) * (AndroidUtilities.dp(2.0f) + itemWidth));
                } else {
                    layoutParams.width = AndroidUtilities.displaySize.x - ((this.itemsCount - 1) * (AndroidUtilities.dp(2.0f) + itemWidth));
                }
            } else {
                layoutParams.width = itemWidth;
            }
            layoutParams.height = itemWidth;
            layoutParams.gravity = 51;
            this.photoVideoViews[a].setLayoutParams(layoutParams);
            a++;
        }
        this.ignoreLayout = false;
        if (!this.isFirst) {
            i = AndroidUtilities.dp(2.0f);
        }
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(i + itemWidth, C.BUFFER_FLAG_ENCRYPTED));
    }

    public static int getItemSize(int itemsCount) {
        if (AndroidUtilities.isTablet()) {
            int itemWidth = (AndroidUtilities.dp(490.0f) - ((itemsCount - 1) * AndroidUtilities.dp(2.0f))) / itemsCount;
            return itemWidth;
        }
        int itemWidth2 = (AndroidUtilities.displaySize.x - ((itemsCount - 1) * AndroidUtilities.dp(2.0f))) / itemsCount;
        return itemWidth2;
    }
}
