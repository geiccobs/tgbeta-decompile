package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.microsoft.appcenter.Constants;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class PhotoPickerAlbumsCell extends FrameLayout {
    private int albumsCount;
    private PhotoPickerAlbumsCellDelegate delegate;
    private Paint backgroundPaint = new Paint();
    private MediaController.AlbumEntry[] albumEntries = new MediaController.AlbumEntry[4];
    private AlbumView[] albumViews = new AlbumView[4];

    /* loaded from: classes4.dex */
    public interface PhotoPickerAlbumsCellDelegate {
        void didSelectAlbum(MediaController.AlbumEntry albumEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class AlbumView extends FrameLayout {
        private TextView countTextView;
        private BackupImageView imageView;
        private TextView nameTextView;
        private View selector;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AlbumView(Context context) {
            super(context);
            PhotoPickerAlbumsCell.this = r18;
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            linearLayout.setBackgroundResource(R.drawable.album_shadow);
            addView(linearLayout, LayoutHelper.createFrame(-1, 60, 83));
            TextView textView = new TextView(context);
            this.nameTextView = textView;
            textView.setTextSize(1, 13.0f);
            this.nameTextView.setTextColor(-1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setGravity(80);
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 8, 0, 0, 5));
            TextView textView2 = new TextView(context);
            this.countTextView = textView2;
            textView2.setTextSize(1, 13.0f);
            this.countTextView.setTextColor(-1);
            this.countTextView.setSingleLine(true);
            this.countTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.countTextView.setMaxLines(1);
            this.countTextView.setGravity(80);
            linearLayout.addView(this.countTextView, LayoutHelper.createLinear(-2, -1, 4.0f, 0.0f, 7.0f, 5.0f));
            View view = new View(context);
            this.selector = view;
            view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (!this.imageView.getImageReceiver().hasNotThumb() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f) {
                PhotoPickerAlbumsCell.this.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_attachPhotoBackground));
                canvas.drawRect(0.0f, 0.0f, this.imageView.getMeasuredWidth(), this.imageView.getMeasuredHeight(), PhotoPickerAlbumsCell.this.backgroundPaint);
            }
        }
    }

    public PhotoPickerAlbumsCell(Context context) {
        super(context);
        for (int a = 0; a < 4; a++) {
            this.albumViews[a] = new AlbumView(context);
            addView(this.albumViews[a]);
            this.albumViews[a].setVisibility(4);
            this.albumViews[a].setTag(Integer.valueOf(a));
            this.albumViews[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.PhotoPickerAlbumsCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoPickerAlbumsCell.this.m1663lambda$new$0$orgtelegramuiCellsPhotoPickerAlbumsCell(view);
                }
            });
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-PhotoPickerAlbumsCell */
    public /* synthetic */ void m1663lambda$new$0$orgtelegramuiCellsPhotoPickerAlbumsCell(View v) {
        PhotoPickerAlbumsCellDelegate photoPickerAlbumsCellDelegate = this.delegate;
        if (photoPickerAlbumsCellDelegate != null) {
            photoPickerAlbumsCellDelegate.didSelectAlbum(this.albumEntries[((Integer) v.getTag()).intValue()]);
        }
    }

    public void setAlbumsCount(int count) {
        int a = 0;
        while (true) {
            AlbumView[] albumViewArr = this.albumViews;
            if (a < albumViewArr.length) {
                albumViewArr[a].setVisibility(a < count ? 0 : 4);
                a++;
            } else {
                this.albumsCount = count;
                return;
            }
        }
    }

    public void setDelegate(PhotoPickerAlbumsCellDelegate delegate) {
        this.delegate = delegate;
    }

    public void setAlbum(int a, MediaController.AlbumEntry albumEntry) {
        this.albumEntries[a] = albumEntry;
        if (albumEntry != null) {
            AlbumView albumView = this.albumViews[a];
            albumView.imageView.setOrientation(0, true);
            if (albumEntry.coverPhoto == null || albumEntry.coverPhoto.path == null) {
                albumView.imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
            } else {
                albumView.imageView.setOrientation(albumEntry.coverPhoto.orientation, true);
                if (albumEntry.coverPhoto.isVideo) {
                    BackupImageView backupImageView = albumView.imageView;
                    backupImageView.setImage("vthumb://" + albumEntry.coverPhoto.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + albumEntry.coverPhoto.path, null, Theme.chat_attachEmptyDrawable);
                } else {
                    BackupImageView backupImageView2 = albumView.imageView;
                    backupImageView2.setImage("thumb://" + albumEntry.coverPhoto.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + albumEntry.coverPhoto.path, null, Theme.chat_attachEmptyDrawable);
                }
            }
            albumView.nameTextView.setText(albumEntry.bucketName);
            albumView.countTextView.setText(String.format("%d", Integer.valueOf(albumEntry.photos.size())));
            return;
        }
        this.albumViews[a].setVisibility(4);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int itemWidth;
        if (AndroidUtilities.isTablet()) {
            itemWidth = ((AndroidUtilities.dp(490.0f) - AndroidUtilities.dp(12.0f)) - ((this.albumsCount - 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        } else {
            itemWidth = ((AndroidUtilities.displaySize.x - AndroidUtilities.dp(12.0f)) - ((this.albumsCount - 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        }
        for (int a = 0; a < this.albumsCount; a++) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.albumViews[a].getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = (AndroidUtilities.dp(4.0f) + itemWidth) * a;
            layoutParams.width = itemWidth;
            layoutParams.height = itemWidth;
            layoutParams.gravity = 51;
            this.albumViews[a].setLayoutParams(layoutParams);
        }
        int a2 = AndroidUtilities.dp(4.0f);
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(a2 + itemWidth, C.BUFFER_FLAG_ENCRYPTED));
    }
}
