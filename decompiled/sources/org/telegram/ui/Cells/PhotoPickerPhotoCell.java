package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.microsoft.appcenter.Constants;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.GroupCallActivity;
/* loaded from: classes4.dex */
public class PhotoPickerPhotoCell extends FrameLayout {
    private Paint backgroundPaint = new Paint();
    public CheckBox2 checkBox;
    public FrameLayout checkFrame;
    private int extraWidth;
    public BackupImageView imageView;
    private int itemWidth;
    public FrameLayout videoInfoContainer;
    public TextView videoTextView;

    public PhotoPickerPhotoCell(Context context) {
        super(context);
        setWillNotDraw(false);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        addView(this.imageView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.checkFrame = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(42, 42, 53));
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Cells.PhotoPickerPhotoCell.1
            private Path path = new Path();
            float[] radii = new float[8];
            private RectF rect = new RectF();
            private Paint paint = new Paint(1);

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                float[] fArr = this.radii;
                fArr[3] = 0.0f;
                fArr[2] = 0.0f;
                fArr[1] = 0.0f;
                fArr[0] = 0.0f;
                float dp = AndroidUtilities.dp(4.0f);
                fArr[7] = dp;
                fArr[6] = dp;
                fArr[5] = dp;
                fArr[4] = dp;
                this.path.reset();
                this.path.addRoundRect(this.rect, this.radii, Path.Direction.CW);
                this.path.close();
                this.paint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                canvas.drawPath(this.path, this.paint);
            }
        };
        this.videoInfoContainer = frameLayout2;
        frameLayout2.setWillNotDraw(false);
        this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
        ImageView imageView1 = new ImageView(context);
        imageView1.setImageResource(R.drawable.ic_video);
        this.videoInfoContainer.addView(imageView1, LayoutHelper.createFrame(-2, -2, 19));
        TextView textView = new TextView(context);
        this.videoTextView = textView;
        textView.setTextColor(-1);
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoTextView.setImportantForAccessibility(2);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, -0.7f, 0.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 24);
        this.checkBox = checkBox2;
        checkBox2.setDrawBackgroundAsArc(11);
        this.checkBox.setColor(Theme.key_chat_attachCheckBoxBackground, Theme.key_chat_attachPhotoBackground, Theme.key_chat_attachCheckBoxCheck);
        addView(this.checkBox, LayoutHelper.createFrame(26, 26.0f, 51, 55.0f, 4.0f, 0.0f, 0.0f));
        this.checkBox.setVisibility(0);
        setFocusable(true);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemWidth + this.extraWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(this.itemWidth, C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
    }

    public void setItemWidth(int width, int extra) {
        this.itemWidth = width;
        this.extraWidth = extra;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.checkFrame.getLayoutParams();
        layoutParams.rightMargin = extra;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.imageView.getLayoutParams();
        layoutParams2.rightMargin = extra;
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.videoInfoContainer.getLayoutParams();
        layoutParams3.rightMargin = extra;
    }

    public void updateColors() {
        this.checkBox.setColor(Theme.key_chat_attachCheckBoxBackground, Theme.key_chat_attachPhotoBackground, Theme.key_chat_attachCheckBoxCheck);
    }

    public void setNum(int num) {
        this.checkBox.setNum(num);
    }

    public void setImage(MediaController.PhotoEntry photoEntry) {
        Drawable thumb = getResources().getDrawable(R.drawable.nophotos);
        if (photoEntry.thumbPath != null) {
            this.imageView.setImage(photoEntry.thumbPath, null, thumb);
        } else if (photoEntry.path != null) {
            this.imageView.setOrientation(photoEntry.orientation, true);
            if (photoEntry.isVideo) {
                this.videoInfoContainer.setVisibility(0);
                this.videoTextView.setText(AndroidUtilities.formatShortDuration(photoEntry.duration));
                setContentDescription(LocaleController.getString("AttachVideo", R.string.AttachVideo) + ", " + LocaleController.formatDuration(photoEntry.duration));
                BackupImageView backupImageView = this.imageView;
                backupImageView.setImage("vthumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path, null, thumb);
                return;
            }
            this.videoInfoContainer.setVisibility(4);
            setContentDescription(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
            BackupImageView backupImageView2 = this.imageView;
            backupImageView2.setImage("thumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path, null, thumb);
        } else {
            this.imageView.setImageDrawable(thumb);
        }
    }

    public void setImage(MediaController.SearchImage searchImage) {
        Drawable thumb = getResources().getDrawable(R.drawable.nophotos);
        if (searchImage.thumbPhotoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(searchImage.thumbPhotoSize, searchImage.photo), (String) null, thumb, searchImage);
        } else if (searchImage.photoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(searchImage.photoSize, searchImage.photo), "80_80", thumb, searchImage);
        } else if (searchImage.thumbPath != null) {
            this.imageView.setImage(searchImage.thumbPath, null, thumb);
        } else if (searchImage.thumbUrl != null && searchImage.thumbUrl.length() > 0) {
            this.imageView.setImage(searchImage.thumbUrl, null, thumb);
        } else if (MessageObject.isDocumentHasThumb(searchImage.document)) {
            TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(searchImage.document.thumbs, GroupCallActivity.TABLET_LIST_SIZE);
            this.imageView.setImage(ImageLocation.getForDocument(photoSize, searchImage.document), (String) null, thumb, searchImage);
        } else {
            this.imageView.setImageDrawable(thumb);
        }
    }

    public void setChecked(int num, boolean checked, boolean animated) {
        this.checkBox.setChecked(num, checked, animated);
    }
}
