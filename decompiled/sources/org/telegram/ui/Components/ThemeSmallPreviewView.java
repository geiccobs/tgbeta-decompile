package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatThemeBottomSheet;
/* loaded from: classes5.dex */
public class ThemeSmallPreviewView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int PATTERN_BITMAP_MAXHEIGHT = 140;
    private static final int PATTERN_BITMAP_MAXWIDTH = 120;
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_GRID = 1;
    public static final int TYPE_QR = 2;
    ThemeDrawable animateOutThemeDrawable;
    Runnable animationCancelRunnable;
    private BackupImageView backupImageView;
    public ChatThemeBottomSheet.ChatThemeItem chatThemeItem;
    private final int currentAccount;
    private int currentType;
    private boolean hasAnimatedEmoji;
    boolean isSelected;
    public int lastThemeIndex;
    private TextPaint noThemeTextPaint;
    int patternColor;
    private final Theme.ResourcesProvider resourcesProvider;
    private float selectionProgress;
    private ValueAnimator strokeAlphaAnimator;
    private StaticLayout textLayout;
    private final float STROKE_RADIUS = AndroidUtilities.dp(8.0f);
    private final float INNER_RADIUS = AndroidUtilities.dp(6.0f);
    private final float INNER_RECT_SPACE = AndroidUtilities.dp(4.0f);
    private final float BUBBLE_HEIGHT = AndroidUtilities.dp(21.0f);
    private final float BUBBLE_WIDTH = AndroidUtilities.dp(41.0f);
    ThemeDrawable themeDrawable = new ThemeDrawable();
    private float changeThemeProgress = 1.0f;
    Paint outlineBackgroundPaint = new Paint(1);
    private final Paint backgroundFillPaint = new Paint(1);
    private final RectF rectF = new RectF();
    private final Path clipPath = new Path();
    Theme.MessageDrawable messageDrawableOut = new Theme.MessageDrawable(0, true, false);
    Theme.MessageDrawable messageDrawableIn = new Theme.MessageDrawable(0, false, false);

    public ThemeSmallPreviewView(Context context, int currentAccount, Theme.ResourcesProvider resourcesProvider, int currentType) {
        super(context);
        this.currentType = currentType;
        this.currentAccount = currentAccount;
        this.resourcesProvider = resourcesProvider;
        setBackgroundColor(getThemedColor(Theme.key_dialogBackgroundGray));
        BackupImageView backupImageView = new BackupImageView(context);
        this.backupImageView = backupImageView;
        backupImageView.getImageReceiver().setCrossfadeWithOldImage(true);
        this.backupImageView.getImageReceiver().setAllowStartLottieAnimation(false);
        this.backupImageView.getImageReceiver().setAutoRepeat(0);
        if (currentType == 0 || currentType == 2) {
            addView(this.backupImageView, LayoutHelper.createFrame(28, 28.0f, 81, 0.0f, 0.0f, 0.0f, 12.0f));
        } else {
            addView(this.backupImageView, LayoutHelper.createFrame(36, 36.0f, 81, 0.0f, 0.0f, 0.0f, 12.0f));
        }
        this.outlineBackgroundPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.outlineBackgroundPaint.setStyle(Paint.Style.STROKE);
        this.outlineBackgroundPaint.setColor(551805923);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.currentType == 1) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (width * 1.2f), C.BUFFER_FLAG_ENCRYPTED));
        } else {
            int width2 = AndroidUtilities.dp(77.0f);
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            if (height == 0) {
                height = (int) (width2 * 1.35f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(width2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
        }
        BackupImageView backupImageView = this.backupImageView;
        backupImageView.setPivotY(backupImageView.getMeasuredHeight());
        BackupImageView backupImageView2 = this.backupImageView;
        backupImageView2.setPivotX(backupImageView2.getMeasuredWidth() / 2.0f);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == oldw && h == oldh) {
            return;
        }
        RectF rectF = this.rectF;
        float f = this.INNER_RECT_SPACE;
        rectF.set(f, f, w - f, h - f);
        this.clipPath.reset();
        Path path = this.clipPath;
        RectF rectF2 = this.rectF;
        float f2 = this.INNER_RADIUS;
        path.addRoundRect(rectF2, f2, f2, Path.Direction.CW);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        ThemeDrawable themeDrawable;
        ThemeDrawable themeDrawable2;
        if (this.chatThemeItem == null) {
            super.dispatchDraw(canvas);
            return;
        }
        if (this.changeThemeProgress != 1.0f && (themeDrawable2 = this.animateOutThemeDrawable) != null) {
            themeDrawable2.drawBackground(canvas, 1.0f);
        }
        float f = this.changeThemeProgress;
        if (f != 0.0f) {
            this.themeDrawable.drawBackground(canvas, f);
        }
        if (this.changeThemeProgress != 1.0f && (themeDrawable = this.animateOutThemeDrawable) != null) {
            themeDrawable.draw(canvas, 1.0f);
        }
        float f2 = this.changeThemeProgress;
        if (f2 != 0.0f) {
            this.themeDrawable.draw(canvas, f2);
        }
        float f3 = this.changeThemeProgress;
        if (f3 != 1.0f) {
            float f4 = f3 + 0.10666667f;
            this.changeThemeProgress = f4;
            if (f4 >= 1.0f) {
                this.changeThemeProgress = 1.0f;
            }
            invalidate();
        }
        super.dispatchDraw(canvas);
    }

    public void setItem(final ChatThemeBottomSheet.ChatThemeItem item, boolean animated) {
        TLRPC.Document document;
        boolean z = true;
        boolean itemChanged = this.chatThemeItem != item;
        if (this.lastThemeIndex == item.themeIndex) {
            z = false;
        }
        boolean darkModeChanged = z;
        this.lastThemeIndex = item.themeIndex;
        this.chatThemeItem = item;
        this.hasAnimatedEmoji = false;
        if (item.chatTheme.getEmoticon() == null) {
            document = null;
        } else {
            document = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(item.chatTheme.getEmoticon());
        }
        if (itemChanged) {
            Runnable runnable = this.animationCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.animationCancelRunnable = null;
            }
            this.backupImageView.animate().cancel();
            this.backupImageView.setScaleX(1.0f);
            this.backupImageView.setScaleY(1.0f);
        }
        if (itemChanged) {
            Drawable thumb = null;
            if (document != null) {
                thumb = DocumentObject.getSvgThumb(document, Theme.key_emptyListPlaceholder, 0.2f);
            }
            if (thumb == null) {
                Emoji.preloadEmoji(item.chatTheme.getEmoticon());
                thumb = Emoji.getEmojiDrawable(item.chatTheme.getEmoticon());
            }
            this.backupImageView.setImage(ImageLocation.getForDocument(document), "50_50", thumb, (Object) null);
        }
        if (itemChanged || darkModeChanged) {
            if (animated) {
                this.changeThemeProgress = 0.0f;
                this.animateOutThemeDrawable = this.themeDrawable;
                this.themeDrawable = new ThemeDrawable();
                invalidate();
            } else {
                this.changeThemeProgress = 1.0f;
            }
            updatePreviewBackground(this.themeDrawable);
            TLRPC.TL_theme theme = item.chatTheme.getTlTheme(this.lastThemeIndex);
            if (theme == null) {
                Theme.ThemeInfo themeInfo = item.chatTheme.getThemeInfo(this.lastThemeIndex);
                Theme.ThemeAccent accent = null;
                if (themeInfo.themeAccentsMap != null) {
                    Theme.ThemeAccent accent2 = themeInfo.themeAccentsMap.get(item.chatTheme.getAccentId(this.lastThemeIndex));
                    accent = accent2;
                }
                if (accent != null && accent.info != null && accent.info.settings.size() > 0) {
                    final TLRPC.WallPaper wallPaper = accent.info.settings.get(0).wallpaper;
                    if (wallPaper != null && wallPaper.document != null) {
                        TLRPC.Document wallpaperDocument = wallPaper.document;
                        TLRPC.PhotoSize thumbSize = FileLoader.getClosestPhotoSizeWithSize(wallpaperDocument.thumbs, PATTERN_BITMAP_MAXWIDTH);
                        ImageLocation imageLocation = ImageLocation.getForDocument(thumbSize, wallpaperDocument);
                        ImageReceiver imageReceiver = new ImageReceiver();
                        imageReceiver.setImage(imageLocation, "120_140", null, null, null, 1);
                        imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.Components.ThemeSmallPreviewView$$ExternalSyntheticLambda4
                            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                            public final void didSetImage(ImageReceiver imageReceiver2, boolean z2, boolean z3, boolean z4) {
                                ThemeSmallPreviewView.this.m3146x734d6a8d(item, wallPaper, imageReceiver2, z2, z3, z4);
                            }

                            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver2) {
                                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
                            }
                        });
                        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
                    }
                } else if (accent != null && accent.info == null) {
                    ChatThemeController.chatThemeQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ThemeSmallPreviewView$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            ThemeSmallPreviewView.this.m3148x56a0b6cb(item);
                        }
                    });
                }
            } else {
                final long themeId = theme.id;
                TLRPC.WallPaper wallPaper2 = item.chatTheme.getWallpaper(this.lastThemeIndex);
                if (wallPaper2 != null) {
                    final int intensity = wallPaper2.settings.intensity;
                    item.chatTheme.loadWallpaperThumb(this.lastThemeIndex, new ResultCallback() { // from class: org.telegram.ui.Components.ThemeSmallPreviewView$$ExternalSyntheticLambda5
                        @Override // org.telegram.tgnet.ResultCallback
                        public final void onComplete(Object obj) {
                            ThemeSmallPreviewView.this.m3145x81a3c46e(themeId, item, intensity, (Pair) obj);
                        }

                        @Override // org.telegram.tgnet.ResultCallback
                        public /* synthetic */ void onError(Throwable th) {
                            ResultCallback.CC.$default$onError(this, th);
                        }

                        @Override // org.telegram.tgnet.ResultCallback
                        public /* synthetic */ void onError(TLRPC.TL_error tL_error) {
                            ResultCallback.CC.$default$onError(this, tL_error);
                        }
                    });
                }
            }
        }
        if (!animated) {
            this.backupImageView.animate().cancel();
            this.backupImageView.setScaleX(1.0f);
            this.backupImageView.setScaleY(1.0f);
            AndroidUtilities.cancelRunOnUIThread(this.animationCancelRunnable);
            if (this.backupImageView.getImageReceiver().getLottieAnimation() != null) {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
            }
        }
        if (this.chatThemeItem.chatTheme == null || this.chatThemeItem.chatTheme.showAsDefaultStub) {
            setContentDescription(LocaleController.getString("ChatNoTheme", R.string.ChatNoTheme));
        } else {
            setContentDescription(this.chatThemeItem.chatTheme.getEmoticon());
        }
    }

    /* renamed from: lambda$setItem$0$org-telegram-ui-Components-ThemeSmallPreviewView */
    public /* synthetic */ void m3145x81a3c46e(long themeId, ChatThemeBottomSheet.ChatThemeItem item, int intensity, Pair result) {
        if (result != null && ((Long) result.first).longValue() == themeId) {
            if (item.previewDrawable instanceof MotionBackgroundDrawable) {
                MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) item.previewDrawable;
                motionBackgroundDrawable.setPatternBitmap(intensity >= 0 ? 100 : -100, prescaleBitmap((Bitmap) result.second), true);
                motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            }
            invalidate();
        }
    }

    /* renamed from: lambda$setItem$1$org-telegram-ui-Components-ThemeSmallPreviewView */
    public /* synthetic */ void m3146x734d6a8d(ChatThemeBottomSheet.ChatThemeItem item, TLRPC.WallPaper wallPaper, ImageReceiver receiver, boolean set, boolean thumb, boolean memCache) {
        Bitmap resultBitmap;
        ImageReceiver.BitmapHolder holder = receiver.getBitmapSafe();
        if (set && holder != null && (resultBitmap = holder.bitmap) != null && (item.previewDrawable instanceof MotionBackgroundDrawable)) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) item.previewDrawable;
            motionBackgroundDrawable.setPatternBitmap((wallPaper.settings == null || wallPaper.settings.intensity >= 0) ? 100 : -100, prescaleBitmap(resultBitmap), true);
            motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            invalidate();
        }
    }

    /* renamed from: lambda$setItem$3$org-telegram-ui-Components-ThemeSmallPreviewView */
    public /* synthetic */ void m3148x56a0b6cb(final ChatThemeBottomSheet.ChatThemeItem item) {
        final Bitmap bitmap = SvgHelper.getBitmap(R.raw.default_pattern, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(140.0f), -16777216, AndroidUtilities.density);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ThemeSmallPreviewView$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ThemeSmallPreviewView.this.m3147x64f710ac(item, bitmap);
            }
        });
    }

    /* renamed from: lambda$setItem$2$org-telegram-ui-Components-ThemeSmallPreviewView */
    public /* synthetic */ void m3147x64f710ac(ChatThemeBottomSheet.ChatThemeItem item, Bitmap bitmap) {
        if (item.previewDrawable instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) item.previewDrawable;
            motionBackgroundDrawable.setPatternBitmap(100, prescaleBitmap(bitmap), true);
            motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            invalidate();
        }
    }

    public void setSelected(final boolean selected, boolean animated) {
        float f = 1.0f;
        if (!animated) {
            ValueAnimator valueAnimator = this.strokeAlphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.isSelected = selected;
            if (!selected) {
                f = 0.0f;
            }
            this.selectionProgress = f;
            invalidate();
            return;
        }
        if (this.isSelected != selected) {
            float currentProgress = this.selectionProgress;
            ValueAnimator valueAnimator2 = this.strokeAlphaAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = currentProgress;
            if (!selected) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.strokeAlphaAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ThemeSmallPreviewView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    ThemeSmallPreviewView.this.m3149x9e27f2e2(valueAnimator3);
                }
            });
            this.strokeAlphaAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ThemeSmallPreviewView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ThemeSmallPreviewView.this.selectionProgress = selected ? 1.0f : 0.0f;
                    ThemeSmallPreviewView.this.invalidate();
                }
            });
            this.strokeAlphaAnimator.setDuration(250L);
            this.strokeAlphaAnimator.start();
        }
        this.isSelected = selected;
    }

    /* renamed from: lambda$setSelected$4$org-telegram-ui-Components-ThemeSmallPreviewView */
    public /* synthetic */ void m3149x9e27f2e2(ValueAnimator valueAnimator) {
        this.selectionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private Bitmap prescaleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        float scale = Math.max(AndroidUtilities.dp(120.0f) / bitmap.getWidth(), AndroidUtilities.dp(140.0f) / bitmap.getHeight());
        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0 || Math.abs(scale - 1.0f) < 0.0125f) {
            return bitmap;
        }
        int w = (int) (bitmap.getWidth() * scale);
        int h = (int) (bitmap.getHeight() * scale);
        if (h <= 0 || w <= 0) {
            return bitmap;
        }
        return Bitmap.createScaledBitmap(bitmap, w, h, true);
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.backgroundFillPaint.setColor(getThemedColor(Theme.key_dialogBackgroundGray));
        TextPaint textPaint = this.noThemeTextPaint;
        if (textPaint != null) {
            textPaint.setColor(getThemedColor(Theme.key_chat_emojiPanelTrendingDescription));
        }
        invalidate();
    }

    private void fillOutBubblePaint(Paint paint, List<Integer> messageColors) {
        if (messageColors.size() > 1) {
            int[] colors = new int[messageColors.size()];
            for (int i = 0; i != messageColors.size(); i++) {
                colors[i] = messageColors.get(i).intValue();
            }
            float top = this.INNER_RECT_SPACE + AndroidUtilities.dp(8.0f);
            paint.setShader(new LinearGradient(0.0f, top, 0.0f, top + this.BUBBLE_HEIGHT, colors, (float[]) null, Shader.TileMode.CLAMP));
            return;
        }
        paint.setShader(null);
    }

    public void updatePreviewBackground(ThemeDrawable themeDrawable) {
        int strokeColor;
        ChatThemeBottomSheet.ChatThemeItem chatThemeItem = this.chatThemeItem;
        if (chatThemeItem == null || chatThemeItem.chatTheme == null) {
            return;
        }
        EmojiThemes.ThemeItem themeItem = this.chatThemeItem.chatTheme.getThemeItem(this.chatThemeItem.themeIndex);
        int color = themeItem.inBubbleColor;
        themeDrawable.inBubblePaint.setColor(color);
        int color2 = themeItem.outBubbleColor;
        themeDrawable.outBubblePaintSecond.setColor(color2);
        if (this.chatThemeItem.chatTheme.showAsDefaultStub) {
            strokeColor = getThemedColor(Theme.key_featuredStickers_addButton);
        } else {
            strokeColor = themeItem.outLineColor;
        }
        int strokeAlpha = themeDrawable.strokePaint.getAlpha();
        themeDrawable.strokePaint.setColor(strokeColor);
        themeDrawable.strokePaint.setAlpha(strokeAlpha);
        TLRPC.TL_theme tlTheme = this.chatThemeItem.chatTheme.getTlTheme(this.chatThemeItem.themeIndex);
        if (tlTheme != null) {
            int index = this.chatThemeItem.chatTheme.getSettingsIndex(this.chatThemeItem.themeIndex);
            TLRPC.ThemeSettings themeSettings = tlTheme.settings.get(index);
            fillOutBubblePaint(themeDrawable.outBubblePaintSecond, themeSettings.message_colors);
            themeDrawable.outBubblePaintSecond.setAlpha(255);
            getPreviewDrawable(tlTheme, index);
        } else {
            EmojiThemes.ThemeItem item = this.chatThemeItem.chatTheme.getThemeItem(this.chatThemeItem.themeIndex);
            getPreviewDrawable(item);
        }
        themeDrawable.previewDrawable = this.chatThemeItem.previewDrawable;
        invalidate();
    }

    private Drawable getPreviewDrawable(TLRPC.TL_theme theme, int settingsIndex) {
        MotionBackgroundDrawable motionBackgroundDrawable;
        if (this.chatThemeItem == null) {
            return null;
        }
        int color1 = 0;
        int color2 = 0;
        int color3 = 0;
        int color4 = 0;
        if (settingsIndex >= 0) {
            TLRPC.ThemeSettings themeSettings = theme.settings.get(settingsIndex);
            TLRPC.WallPaperSettings wallPaperSettings = themeSettings.wallpaper.settings;
            color1 = wallPaperSettings.background_color;
            color2 = wallPaperSettings.second_background_color;
            color3 = wallPaperSettings.third_background_color;
            color4 = wallPaperSettings.fourth_background_color;
        }
        if (color2 != 0) {
            motionBackgroundDrawable = new MotionBackgroundDrawable(color1, color2, color3, color4, true);
            this.patternColor = motionBackgroundDrawable.getPatternColor();
        } else {
            motionBackgroundDrawable = new MotionBackgroundDrawable(color1, color1, color1, color1, true);
            this.patternColor = -16777216;
        }
        this.chatThemeItem.previewDrawable = motionBackgroundDrawable;
        return motionBackgroundDrawable;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Drawable getPreviewDrawable(EmojiThemes.ThemeItem item) {
        if (this.chatThemeItem == null) {
            return null;
        }
        Drawable drawable = null;
        int color1 = item.patternBgColor;
        int color2 = item.patternBgGradientColor1;
        int color3 = item.patternBgGradientColor2;
        int color4 = item.patternBgGradientColor3;
        int rotation = item.patternBgRotation;
        if (item.themeInfo.getAccent(false) != null) {
            if (color2 != 0) {
                MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(color1, color2, color3, color4, rotation, true);
                this.patternColor = motionBackgroundDrawable.getPatternColor();
                drawable = motionBackgroundDrawable;
            } else {
                drawable = new MotionBackgroundDrawable(color1, color1, color1, color1, rotation, true);
                this.patternColor = -16777216;
            }
        } else if (color1 != 0 && color2 != 0) {
            drawable = new MotionBackgroundDrawable(color1, color2, color3, color4, rotation, true);
        } else if (color1 != 0) {
            drawable = new ColorDrawable(color1);
        } else if (item.themeInfo != null && (item.themeInfo.previewWallpaperOffset > 0 || item.themeInfo.pathToWallpaper != null)) {
            Bitmap wallpaper = AndroidUtilities.getScaledBitmap(AndroidUtilities.dp(112.0f), AndroidUtilities.dp(134.0f), item.themeInfo.pathToWallpaper, item.themeInfo.pathToFile, item.themeInfo.previewWallpaperOffset);
            if (wallpaper != null) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(wallpaper);
                bitmapDrawable.setFilterBitmap(true);
                drawable = bitmapDrawable;
            }
        } else {
            drawable = new MotionBackgroundDrawable(-2368069, -9722489, -2762611, -7817084, true);
        }
        this.chatThemeItem.previewDrawable = drawable;
        return drawable;
    }

    public StaticLayout getNoThemeStaticLayout() {
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            return staticLayout;
        }
        TextPaint textPaint = new TextPaint((int) TsExtractor.TS_STREAM_TYPE_AC3);
        this.noThemeTextPaint = textPaint;
        textPaint.setColor(getThemedColor(Theme.key_chat_emojiPanelTrendingDescription));
        this.noThemeTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.noThemeTextPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        StaticLayout createStaticLayout2 = StaticLayoutEx.createStaticLayout2(LocaleController.getString("ChatNoTheme", R.string.ChatNoTheme), this.noThemeTextPaint, AndroidUtilities.dp(52.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true, TextUtils.TruncateAt.END, AndroidUtilities.dp(52.0f), 3);
        this.textLayout = createStaticLayout2;
        return createStaticLayout2;
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void playEmojiAnimation() {
        if (this.backupImageView.getImageReceiver().getLottieAnimation() != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationCancelRunnable);
            this.backupImageView.setVisibility(0);
            if (!this.backupImageView.getImageReceiver().getLottieAnimation().isRunning) {
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, true);
                this.backupImageView.getImageReceiver().getLottieAnimation().start();
            }
            this.backupImageView.animate().scaleX(2.0f).scaleY(2.0f).setDuration(300L).setInterpolator(AndroidUtilities.overshootInterpolator).start();
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ThemeSmallPreviewView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ThemeSmallPreviewView.this.m3144x92d17632();
                }
            };
            this.animationCancelRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 2500L);
        }
    }

    /* renamed from: lambda$playEmojiAnimation$5$org-telegram-ui-Components-ThemeSmallPreviewView */
    public /* synthetic */ void m3144x92d17632() {
        this.animationCancelRunnable = null;
        this.backupImageView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    public void cancelAnimation() {
        Runnable runnable = this.animationCancelRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.animationCancelRunnable.run();
        }
    }

    /* loaded from: classes5.dex */
    public class ThemeDrawable {
        Drawable previewDrawable;
        private final Paint strokePaint;
        private final Paint outBubblePaintSecond = new Paint(1);
        private final Paint inBubblePaint = new Paint(1);

        ThemeDrawable() {
            ThemeSmallPreviewView.this = r3;
            Paint paint = new Paint(1);
            this.strokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }

        public void drawBackground(Canvas canvas, float alpha) {
            if (this.previewDrawable == null) {
                canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.backgroundFillPaint);
                return;
            }
            canvas.save();
            canvas.clipPath(ThemeSmallPreviewView.this.clipPath);
            Drawable drawable = this.previewDrawable;
            if (drawable instanceof BitmapDrawable) {
                int drawableW = drawable.getIntrinsicWidth();
                int drawableH = this.previewDrawable.getIntrinsicHeight();
                if (drawableW / drawableH > ThemeSmallPreviewView.this.getWidth() / ThemeSmallPreviewView.this.getHeight()) {
                    int w = (int) ((ThemeSmallPreviewView.this.getWidth() * drawableH) / drawableW);
                    int padding = (w - ThemeSmallPreviewView.this.getWidth()) / 2;
                    this.previewDrawable.setBounds(padding, 0, padding + w, ThemeSmallPreviewView.this.getHeight());
                } else {
                    int h = (int) ((ThemeSmallPreviewView.this.getHeight() * drawableH) / drawableW);
                    int padding2 = (ThemeSmallPreviewView.this.getHeight() - h) / 2;
                    this.previewDrawable.setBounds(0, padding2, ThemeSmallPreviewView.this.getWidth(), padding2 + h);
                }
            } else {
                drawable.setBounds(0, 0, ThemeSmallPreviewView.this.getWidth(), ThemeSmallPreviewView.this.getHeight());
            }
            this.previewDrawable.setAlpha((int) (255.0f * alpha));
            this.previewDrawable.draw(canvas);
            Drawable drawable2 = this.previewDrawable;
            if ((drawable2 instanceof ColorDrawable) || ((drawable2 instanceof MotionBackgroundDrawable) && ((MotionBackgroundDrawable) drawable2).isOneColor())) {
                int wasAlpha = ThemeSmallPreviewView.this.outlineBackgroundPaint.getAlpha();
                ThemeSmallPreviewView.this.outlineBackgroundPaint.setAlpha((int) (wasAlpha * alpha));
                float padding3 = ThemeSmallPreviewView.this.INNER_RECT_SPACE;
                AndroidUtilities.rectTmp.set(padding3, padding3, ThemeSmallPreviewView.this.getWidth() - padding3, ThemeSmallPreviewView.this.getHeight() - padding3);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.outlineBackgroundPaint);
                ThemeSmallPreviewView.this.outlineBackgroundPaint.setAlpha(wasAlpha);
            }
            canvas.restore();
        }

        public void draw(Canvas canvas, float alpha) {
            int strokeColor;
            if (ThemeSmallPreviewView.this.isSelected || ThemeSmallPreviewView.this.strokeAlphaAnimator != null) {
                EmojiThemes.ThemeItem themeItem = ThemeSmallPreviewView.this.chatThemeItem.chatTheme.getThemeItem(ThemeSmallPreviewView.this.chatThemeItem.themeIndex);
                if (ThemeSmallPreviewView.this.chatThemeItem.chatTheme.showAsDefaultStub) {
                    strokeColor = ThemeSmallPreviewView.this.getThemedColor(Theme.key_featuredStickers_addButton);
                } else {
                    strokeColor = themeItem.outLineColor;
                }
                this.strokePaint.setColor(strokeColor);
                this.strokePaint.setAlpha((int) (ThemeSmallPreviewView.this.selectionProgress * alpha * 255.0f));
                float rectSpace = (this.strokePaint.getStrokeWidth() * 0.5f) + (AndroidUtilities.dp(4.0f) * (1.0f - ThemeSmallPreviewView.this.selectionProgress));
                ThemeSmallPreviewView.this.rectF.set(rectSpace, rectSpace, ThemeSmallPreviewView.this.getWidth() - rectSpace, ThemeSmallPreviewView.this.getHeight() - rectSpace);
                canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.STROKE_RADIUS, ThemeSmallPreviewView.this.STROKE_RADIUS, this.strokePaint);
            }
            this.outBubblePaintSecond.setAlpha((int) (alpha * 255.0f));
            this.inBubblePaint.setAlpha((int) (255.0f * alpha));
            ThemeSmallPreviewView.this.rectF.set(ThemeSmallPreviewView.this.INNER_RECT_SPACE, ThemeSmallPreviewView.this.INNER_RECT_SPACE, ThemeSmallPreviewView.this.getWidth() - ThemeSmallPreviewView.this.INNER_RECT_SPACE, ThemeSmallPreviewView.this.getHeight() - ThemeSmallPreviewView.this.INNER_RECT_SPACE);
            if (ThemeSmallPreviewView.this.chatThemeItem.chatTheme == null || ThemeSmallPreviewView.this.chatThemeItem.chatTheme.showAsDefaultStub) {
                canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.backgroundFillPaint);
                canvas.save();
                StaticLayout textLayout = ThemeSmallPreviewView.this.getNoThemeStaticLayout();
                canvas.translate((ThemeSmallPreviewView.this.getWidth() - textLayout.getWidth()) * 0.5f, AndroidUtilities.dp(18.0f));
                textLayout.draw(canvas);
                canvas.restore();
            } else if (ThemeSmallPreviewView.this.currentType != 2) {
                float bubbleTop = ThemeSmallPreviewView.this.INNER_RECT_SPACE + AndroidUtilities.dp(8.0f);
                float bubbleLeft = ThemeSmallPreviewView.this.INNER_RECT_SPACE + AndroidUtilities.dp(22.0f);
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    ThemeSmallPreviewView.this.rectF.set(bubbleLeft, bubbleTop, ThemeSmallPreviewView.this.BUBBLE_WIDTH + bubbleLeft, ThemeSmallPreviewView.this.BUBBLE_HEIGHT + bubbleTop);
                } else {
                    bubbleTop = ThemeSmallPreviewView.this.getMeasuredHeight() * 0.12f;
                    float bubbleLeft2 = ThemeSmallPreviewView.this.getMeasuredWidth() - (ThemeSmallPreviewView.this.getMeasuredWidth() * 0.65f);
                    float bubbleRight = ThemeSmallPreviewView.this.getMeasuredWidth() - (ThemeSmallPreviewView.this.getMeasuredWidth() * 0.1f);
                    float bubbleBottom = ThemeSmallPreviewView.this.getMeasuredHeight() * 0.32f;
                    ThemeSmallPreviewView.this.rectF.set(bubbleLeft2, bubbleTop, bubbleRight, bubbleBottom);
                }
                Paint paint = this.outBubblePaintSecond;
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.rectF.height() * 0.5f, ThemeSmallPreviewView.this.rectF.height() * 0.5f, paint);
                } else {
                    ThemeSmallPreviewView.this.messageDrawableOut.setBounds((int) ThemeSmallPreviewView.this.rectF.left, ((int) ThemeSmallPreviewView.this.rectF.top) - AndroidUtilities.dp(2.0f), ((int) ThemeSmallPreviewView.this.rectF.right) + AndroidUtilities.dp(4.0f), ((int) ThemeSmallPreviewView.this.rectF.bottom) + AndroidUtilities.dp(2.0f));
                    ThemeSmallPreviewView.this.messageDrawableOut.setRoundRadius((int) (ThemeSmallPreviewView.this.rectF.height() * 0.5f));
                    ThemeSmallPreviewView.this.messageDrawableOut.draw(canvas, paint);
                }
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    float bubbleLeft3 = ThemeSmallPreviewView.this.INNER_RECT_SPACE + AndroidUtilities.dp(5.0f);
                    float bubbleTop2 = bubbleTop + ThemeSmallPreviewView.this.BUBBLE_HEIGHT + AndroidUtilities.dp(4.0f);
                    ThemeSmallPreviewView.this.rectF.set(bubbleLeft3, bubbleTop2, ThemeSmallPreviewView.this.BUBBLE_WIDTH + bubbleLeft3, ThemeSmallPreviewView.this.BUBBLE_HEIGHT + bubbleTop2);
                } else {
                    float bubbleTop3 = ThemeSmallPreviewView.this.getMeasuredHeight() * 0.35f;
                    float bubbleLeft4 = 0.1f * ThemeSmallPreviewView.this.getMeasuredWidth();
                    float bubbleRight2 = ThemeSmallPreviewView.this.getMeasuredWidth() * 0.65f;
                    float bubbleBottom2 = ThemeSmallPreviewView.this.getMeasuredHeight() * 0.55f;
                    ThemeSmallPreviewView.this.rectF.set(bubbleLeft4, bubbleTop3, bubbleRight2, bubbleBottom2);
                }
                if (ThemeSmallPreviewView.this.currentType != 0) {
                    ThemeSmallPreviewView.this.messageDrawableIn.setBounds(((int) ThemeSmallPreviewView.this.rectF.left) - AndroidUtilities.dp(4.0f), ((int) ThemeSmallPreviewView.this.rectF.top) - AndroidUtilities.dp(2.0f), (int) ThemeSmallPreviewView.this.rectF.right, ((int) ThemeSmallPreviewView.this.rectF.bottom) + AndroidUtilities.dp(2.0f));
                    ThemeSmallPreviewView.this.messageDrawableIn.setRoundRadius((int) (ThemeSmallPreviewView.this.rectF.height() * 0.5f));
                    ThemeSmallPreviewView.this.messageDrawableIn.draw(canvas, this.inBubblePaint);
                    return;
                }
                canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.rectF.height() * 0.5f, ThemeSmallPreviewView.this.rectF.height() * 0.5f, this.inBubblePaint);
            } else if (ThemeSmallPreviewView.this.chatThemeItem.icon != null) {
                float left = (ThemeSmallPreviewView.this.getWidth() - ThemeSmallPreviewView.this.chatThemeItem.icon.getWidth()) * 0.5f;
                canvas.drawBitmap(ThemeSmallPreviewView.this.chatThemeItem.icon, left, AndroidUtilities.dp(21.0f), (Paint) null);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            invalidate();
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(true);
        info.setSelected(this.isSelected);
    }
}
