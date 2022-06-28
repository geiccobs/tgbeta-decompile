package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.WallpapersListActivity;
/* loaded from: classes4.dex */
public class WallpaperCell extends FrameLayout {
    private Paint backgroundPaint;
    private Drawable checkDrawable;
    private Paint circlePaint;
    private int currentType;
    private Paint framePaint;
    private boolean isBottom;
    private boolean isTop;
    private int spanCount = 3;
    private WallpaperView[] wallpaperViews = new WallpaperView[5];

    /* loaded from: classes4.dex */
    public class WallpaperView extends FrameLayout {
        private AnimatorSet animator;
        private AnimatorSet animatorSet;
        private CheckBox checkBox;
        private Object currentWallpaper;
        private BackupImageView imageView;
        private ImageView imageView2;
        private boolean isSelected;
        private View selector;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public WallpaperView(Context context) {
            super(context);
            WallpaperCell.this = r8;
            setWillNotDraw(false);
            BackupImageView backupImageView = new BackupImageView(context) { // from class: org.telegram.ui.Cells.WallpaperCell.WallpaperView.1
                @Override // org.telegram.ui.Components.BackupImageView, android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if ((WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) || (WallpaperView.this.currentWallpaper instanceof WallpapersListActivity.FileWallpaper)) {
                        canvas.drawLine(1.0f, 0.0f, getMeasuredWidth() - 1, 0.0f, WallpaperCell.this.framePaint);
                        canvas.drawLine(0.0f, 0.0f, 0.0f, getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas.drawLine(getMeasuredWidth() - 1, 0.0f, getMeasuredWidth() - 1, getMeasuredHeight(), WallpaperCell.this.framePaint);
                        canvas.drawLine(1.0f, getMeasuredHeight() - 1, getMeasuredWidth() - 1, getMeasuredHeight() - 1, WallpaperCell.this.framePaint);
                    }
                    if (WallpaperView.this.isSelected) {
                        WallpaperCell.this.circlePaint.setColor(Theme.serviceMessageColorBackup);
                        int cx = getMeasuredWidth() / 2;
                        int cy = getMeasuredHeight() / 2;
                        canvas.drawCircle(cx, cy, AndroidUtilities.dp(20.0f), WallpaperCell.this.circlePaint);
                        WallpaperCell.this.checkDrawable.setBounds(cx - (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2), cy - (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2), (WallpaperCell.this.checkDrawable.getIntrinsicWidth() / 2) + cx, (WallpaperCell.this.checkDrawable.getIntrinsicHeight() / 2) + cy);
                        WallpaperCell.this.checkDrawable.draw(canvas);
                    }
                }
            };
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(-1, -1, 51));
            ImageView imageView = new ImageView(context);
            this.imageView2 = imageView;
            imageView.setImageResource(R.drawable.ic_gallery_background);
            this.imageView2.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView2, LayoutHelper.createFrame(-1, -1, 51));
            View view = new View(context);
            this.selector = view;
            view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
            CheckBox checkBox = new CheckBox(context, R.drawable.round_check2);
            this.checkBox = checkBox;
            checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 2.0f, 2.0f, 0.0f));
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }

        public void setWallpaper(Object object, Object selectedWallpaper, Drawable themedWallpaper, boolean themed) {
            TLRPC.PhotoSize image;
            int patternColor;
            int patternColor2;
            this.currentWallpaper = object;
            int size = 0;
            this.imageView.setVisibility(0);
            this.imageView2.setVisibility(4);
            this.imageView.setBackgroundDrawable(null);
            this.imageView.getImageReceiver().setColorFilter(null);
            this.imageView.getImageReceiver().setAlpha(1.0f);
            this.imageView.getImageReceiver().setBlendMode(null);
            this.imageView.getImageReceiver().setGradientBitmap(null);
            this.isSelected = object == selectedWallpaper;
            String imageFilter = "180_180";
            String thumbFilter = "100_100_b";
            if (object instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, AndroidUtilities.dp(100));
                TLRPC.PhotoSize image2 = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, AndroidUtilities.dp(180));
                if (image2 == thumb) {
                    image2 = null;
                }
                long size2 = image2 != null ? image2.size : wallPaper.document.size;
                if (wallPaper.pattern) {
                    if (wallPaper.settings.third_background_color != 0) {
                        MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color, true);
                        if (wallPaper.settings.intensity >= 0 || !Theme.getActiveTheme().isDark()) {
                            this.imageView.setBackground(motionBackgroundDrawable);
                            if (Build.VERSION.SDK_INT >= 29) {
                                this.imageView.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                            }
                        } else {
                            this.imageView.getImageReceiver().setGradientBitmap(motionBackgroundDrawable.getBitmap());
                        }
                        int patternColor3 = MotionBackgroundDrawable.getPatternColor(wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color);
                        patternColor2 = patternColor3;
                    } else {
                        this.imageView.setBackgroundColor(Theme.getWallpaperColor(wallPaper.settings.background_color));
                        patternColor2 = AndroidUtilities.getPatternColor(wallPaper.settings.background_color);
                    }
                    if (Build.VERSION.SDK_INT < 29 || wallPaper.settings.third_background_color == 0) {
                        this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(patternColor2), PorterDuff.Mode.SRC_IN));
                    }
                    if (image2 != null) {
                        this.imageView.setImage(ImageLocation.getForDocument(image2, wallPaper.document), imageFilter, ImageLocation.getForDocument(thumb, wallPaper.document), null, "jpg", size2, 1, wallPaper);
                    } else {
                        this.imageView.setImage(ImageLocation.getForDocument(thumb, wallPaper.document), imageFilter, null, null, "jpg", size2, 1, wallPaper);
                    }
                    this.imageView.getImageReceiver().setAlpha(Math.abs(wallPaper.settings.intensity) / 100.0f);
                } else if (image2 != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(image2, wallPaper.document), imageFilter, ImageLocation.getForDocument(thumb, wallPaper.document), thumbFilter, "jpg", size2, 1, wallPaper);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(wallPaper.document), imageFilter, ImageLocation.getForDocument(thumb, wallPaper.document), thumbFilter, "jpg", size2, 1, wallPaper);
                }
            } else if (!(object instanceof WallpapersListActivity.ColorWallpaper)) {
                if (object instanceof WallpapersListActivity.FileWallpaper) {
                    WallpapersListActivity.FileWallpaper wallPaper2 = (WallpapersListActivity.FileWallpaper) object;
                    if (wallPaper2.originalPath != null) {
                        this.imageView.setImage(wallPaper2.originalPath.getAbsolutePath(), imageFilter, null);
                    } else if (wallPaper2.path != null) {
                        this.imageView.setImage(wallPaper2.path.getAbsolutePath(), imageFilter, null);
                    } else if (Theme.THEME_BACKGROUND_SLUG.equals(wallPaper2.slug)) {
                        BackupImageView backupImageView = this.imageView;
                        backupImageView.setImageDrawable(Theme.getThemedWallpaper(true, backupImageView));
                    } else {
                        this.imageView.setImageResource(wallPaper2.thumbResId);
                    }
                } else if (object instanceof MediaController.SearchImage) {
                    MediaController.SearchImage wallPaper3 = (MediaController.SearchImage) object;
                    if (wallPaper3.photo != null) {
                        TLRPC.PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(wallPaper3.photo.sizes, AndroidUtilities.dp(100));
                        TLRPC.PhotoSize image3 = FileLoader.getClosestPhotoSizeWithSize(wallPaper3.photo.sizes, AndroidUtilities.dp(180));
                        if (image3 != thumb2) {
                            image = image3;
                        } else {
                            image = null;
                        }
                        if (image != null) {
                            size = image.size;
                        }
                        this.imageView.setImage(ImageLocation.getForPhoto(image, wallPaper3.photo), imageFilter, ImageLocation.getForPhoto(thumb2, wallPaper3.photo), thumbFilter, "jpg", size, 1, wallPaper3);
                        return;
                    }
                    this.imageView.setImage(wallPaper3.thumbUrl, imageFilter, null);
                } else {
                    this.isSelected = false;
                }
            } else {
                WallpapersListActivity.ColorWallpaper wallPaper4 = (WallpapersListActivity.ColorWallpaper) object;
                if (wallPaper4.path != null || wallPaper4.pattern != null || Theme.DEFAULT_BACKGROUND_SLUG.equals(wallPaper4.slug)) {
                    if (wallPaper4.gradientColor2 != 0) {
                        MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable(wallPaper4.color, wallPaper4.gradientColor1, wallPaper4.gradientColor2, wallPaper4.gradientColor3, true);
                        if (wallPaper4.intensity < 0.0f) {
                            this.imageView.getImageReceiver().setGradientBitmap(motionBackgroundDrawable2.getBitmap());
                        } else {
                            this.imageView.setBackground(new MotionBackgroundDrawable(wallPaper4.color, wallPaper4.gradientColor1, wallPaper4.gradientColor2, wallPaper4.gradientColor3, true));
                            if (Build.VERSION.SDK_INT >= 29) {
                                this.imageView.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                            }
                        }
                        patternColor = MotionBackgroundDrawable.getPatternColor(wallPaper4.color, wallPaper4.gradientColor1, wallPaper4.gradientColor2, wallPaper4.gradientColor3);
                    } else {
                        patternColor = AndroidUtilities.getPatternColor(wallPaper4.color);
                    }
                    if (Theme.DEFAULT_BACKGROUND_SLUG.equals(wallPaper4.slug)) {
                        if (wallPaper4.defaultCache == null) {
                            wallPaper4.defaultCache = SvgHelper.getBitmap((int) R.raw.default_pattern, 100, 180, -16777216);
                        }
                        this.imageView.setImageBitmap(wallPaper4.defaultCache);
                        this.imageView.getImageReceiver().setAlpha(Math.abs(wallPaper4.intensity));
                        return;
                    } else if (wallPaper4.path != null) {
                        this.imageView.setImage(wallPaper4.path.getAbsolutePath(), imageFilter, null);
                        return;
                    } else {
                        TLRPC.PhotoSize thumb3 = FileLoader.getClosestPhotoSizeWithSize(wallPaper4.pattern.document.thumbs, 100);
                        long size3 = thumb3 != null ? thumb3.size : wallPaper4.pattern.document.size;
                        this.imageView.setImage(ImageLocation.getForDocument(thumb3, wallPaper4.pattern.document), imageFilter, null, null, "jpg", size3, 1, wallPaper4.pattern);
                        this.imageView.getImageReceiver().setAlpha(Math.abs(wallPaper4.intensity));
                        if (Build.VERSION.SDK_INT < 29 || wallPaper4.gradientColor2 == 0) {
                            this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.getPatternColor(patternColor), PorterDuff.Mode.SRC_IN));
                            return;
                        }
                        return;
                    }
                }
                this.imageView.setImageBitmap(null);
                if (wallPaper4.isGradient) {
                    this.imageView.setBackground(new MotionBackgroundDrawable(wallPaper4.color, wallPaper4.gradientColor1, wallPaper4.gradientColor2, wallPaper4.gradientColor3, true));
                } else if (wallPaper4.gradientColor1 != 0) {
                    this.imageView.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{wallPaper4.color | (-16777216), wallPaper4.gradientColor1 | (-16777216)}));
                } else {
                    this.imageView.setBackgroundColor(wallPaper4.color | (-16777216));
                }
            }
        }

        public void setChecked(final boolean checked, boolean animated) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animator = null;
            }
            float f = 0.8875f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animator = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView = this.imageView;
                float[] fArr = new float[1];
                fArr[0] = checked ? 0.8875f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, "scaleX", fArr);
                BackupImageView backupImageView2 = this.imageView;
                float[] fArr2 = new float[1];
                if (!checked) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView2, "scaleY", fArr2);
                animatorSet2.playTogether(animatorArr);
                this.animator.setDuration(200L);
                this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.WallpaperCell.WallpaperView.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animation)) {
                            WallpaperView.this.animator = null;
                            if (!checked) {
                                WallpaperView.this.setBackgroundColor(0);
                            }
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        if (WallpaperView.this.animator != null && WallpaperView.this.animator.equals(animation)) {
                            WallpaperView.this.animator = null;
                        }
                    }
                });
                this.animator.start();
            } else {
                this.imageView.setScaleX(checked ? 0.8875f : 1.0f);
                BackupImageView backupImageView3 = this.imageView;
                if (!checked) {
                    f = 1.0f;
                }
                backupImageView3.setScaleY(f);
            }
            invalidate();
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.imageView.invalidate();
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
            if (this.checkBox.isChecked() || !this.imageView.getImageReceiver().hasBitmapImage() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f) {
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), WallpaperCell.this.backgroundPaint);
            }
        }
    }

    public WallpaperCell(Context context) {
        super(context);
        int a = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (a < wallpaperViewArr.length) {
                final WallpaperView wallpaperView = new WallpaperView(context);
                wallpaperViewArr[a] = wallpaperView;
                final int num = a;
                addView(wallpaperView);
                wallpaperView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.WallpaperCell$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        WallpaperCell.this.m1688lambda$new$0$orgtelegramuiCellsWallpaperCell(wallpaperView, num, view);
                    }
                });
                wallpaperView.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Cells.WallpaperCell$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view) {
                        return WallpaperCell.this.m1689lambda$new$1$orgtelegramuiCellsWallpaperCell(wallpaperView, num, view);
                    }
                });
                a++;
            } else {
                Paint paint = new Paint();
                this.framePaint = paint;
                paint.setColor(AndroidUtilities.DARK_STATUS_BAR_OVERLAY);
                this.circlePaint = new Paint(1);
                this.checkDrawable = context.getResources().getDrawable(R.drawable.background_selected).mutate();
                Paint paint2 = new Paint();
                this.backgroundPaint = paint2;
                paint2.setColor(Theme.getColor(Theme.key_sharedMedia_photoPlaceholder));
                return;
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-WallpaperCell */
    public /* synthetic */ void m1688lambda$new$0$orgtelegramuiCellsWallpaperCell(WallpaperView wallpaperView, int num, View v) {
        onWallpaperClick(wallpaperView.currentWallpaper, num);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-WallpaperCell */
    public /* synthetic */ boolean m1689lambda$new$1$orgtelegramuiCellsWallpaperCell(WallpaperView wallpaperView, int num, View v) {
        return onWallpaperLongClick(wallpaperView.currentWallpaper, num);
    }

    protected void onWallpaperClick(Object wallPaper, int index) {
    }

    protected boolean onWallpaperLongClick(Object wallPaper, int index) {
        return false;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - AndroidUtilities.dp(((this.spanCount - 1) * 6) + 28);
        int itemWidth = availableWidth / this.spanCount;
        int height = this.currentType == 0 ? AndroidUtilities.dp(180.0f) : itemWidth;
        float f = 14.0f;
        int dp = (this.isTop ? AndroidUtilities.dp(14.0f) : 0) + height;
        if (!this.isBottom) {
            f = 6.0f;
        }
        setMeasuredDimension(width, dp + AndroidUtilities.dp(f));
        int a = 0;
        while (true) {
            int i = this.spanCount;
            if (a < i) {
                this.wallpaperViews[a].measure(View.MeasureSpec.makeMeasureSpec(a == i + (-1) ? availableWidth : itemWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                availableWidth -= itemWidth;
                a++;
            } else {
                return;
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int l = AndroidUtilities.dp(14.0f);
        int t = this.isTop ? AndroidUtilities.dp(14.0f) : 0;
        for (int a = 0; a < this.spanCount; a++) {
            int w = this.wallpaperViews[a].getMeasuredWidth();
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            wallpaperViewArr[a].layout(l, t, l + w, wallpaperViewArr[a].getMeasuredHeight() + t);
            l += AndroidUtilities.dp(6.0f) + w;
        }
    }

    public void setParams(int columns, boolean top, boolean bottom) {
        this.spanCount = columns;
        this.isTop = top;
        this.isBottom = bottom;
        int a = 0;
        while (true) {
            WallpaperView[] wallpaperViewArr = this.wallpaperViews;
            if (a < wallpaperViewArr.length) {
                wallpaperViewArr[a].setVisibility(a < columns ? 0 : 8);
                this.wallpaperViews[a].clearAnimation();
                a++;
            } else {
                return;
            }
        }
    }

    public void setWallpaper(int type, int index, Object wallpaper, Object selectedWallpaper, Drawable themedWallpaper, boolean themed) {
        this.currentType = type;
        if (wallpaper == null) {
            this.wallpaperViews[index].setVisibility(8);
            this.wallpaperViews[index].clearAnimation();
            return;
        }
        this.wallpaperViews[index].setVisibility(0);
        this.wallpaperViews[index].setWallpaper(wallpaper, selectedWallpaper, themedWallpaper, themed);
    }

    public void setChecked(int index, boolean checked, boolean animated) {
        this.wallpaperViews[index].setChecked(checked, animated);
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        for (int a = 0; a < this.spanCount; a++) {
            this.wallpaperViews[a].invalidate();
        }
    }
}
