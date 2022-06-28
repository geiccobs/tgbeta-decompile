package org.telegram.ui.Cells;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ThemeSetUrlActivity;
/* loaded from: classes4.dex */
public class ThemesHorizontalListCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    public static byte[] bytes = new byte[1024];
    private ThemesListAdapter adapter;
    private int currentType;
    private ArrayList<Theme.ThemeInfo> customThemes;
    private ArrayList<Theme.ThemeInfo> defaultThemes;
    private boolean drawDivider;
    private LinearLayoutManager horizontalLayoutManager;
    private HashMap<String, Theme.ThemeInfo> loadingThemes = new HashMap<>();
    private HashMap<Theme.ThemeInfo, String> loadingWallpapers = new HashMap<>();
    private int prevCount;
    private Theme.ThemeInfo prevThemeInfo;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ThemesListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        ThemesListAdapter(Context context) {
            ThemesHorizontalListCell.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new InnerThemeView(this.mContext));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<Theme.ThemeInfo> arrayList;
            InnerThemeView view = (InnerThemeView) holder.itemView;
            int p = position;
            if (position < ThemesHorizontalListCell.this.defaultThemes.size()) {
                arrayList = ThemesHorizontalListCell.this.defaultThemes;
            } else {
                arrayList = ThemesHorizontalListCell.this.customThemes;
                p -= ThemesHorizontalListCell.this.defaultThemes.size();
            }
            Theme.ThemeInfo themeInfo = arrayList.get(p);
            boolean z = true;
            boolean z2 = position == getItemCount() - 1;
            if (position != 0) {
                z = false;
            }
            view.setTheme(themeInfo, z2, z);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            ThemesHorizontalListCell themesHorizontalListCell = ThemesHorizontalListCell.this;
            return themesHorizontalListCell.prevCount = themesHorizontalListCell.defaultThemes.size() + ThemesHorizontalListCell.this.customThemes.size();
        }
    }

    /* loaded from: classes4.dex */
    public class InnerThemeView extends FrameLayout {
        private ObjectAnimator accentAnimator;
        private boolean accentColorChanged;
        private int accentId;
        private float accentState;
        private int backColor;
        private Drawable backgroundDrawable;
        private BitmapShader bitmapShader;
        private RadioButton button;
        private int checkColor;
        private boolean hasWhiteBackground;
        private int inColor;
        private Drawable inDrawable;
        private boolean isFirst;
        private boolean isLast;
        private long lastDrawTime;
        private int loadingColor;
        private Drawable loadingDrawable;
        private int oldBackColor;
        private int oldCheckColor;
        private int oldInColor;
        private int oldOutColor;
        private Drawable optionsDrawable;
        private int outColor;
        private Drawable outDrawable;
        private float placeholderAlpha;
        private boolean pressed;
        private Theme.ThemeInfo themeInfo;
        private RectF rect = new RectF();
        private Paint paint = new Paint(1);
        private TextPaint textPaint = new TextPaint(1);
        private final ArgbEvaluator evaluator = new ArgbEvaluator();
        private Paint bitmapPaint = new Paint(3);
        private Matrix shaderMatrix = new Matrix();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public InnerThemeView(Context context) {
            super(context);
            ThemesHorizontalListCell.this = r8;
            setWillNotDraw(false);
            this.inDrawable = context.getResources().getDrawable(R.drawable.minibubble_in).mutate();
            this.outDrawable = context.getResources().getDrawable(R.drawable.minibubble_out).mutate();
            this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
            RadioButton radioButton = new RadioButton(context);
            this.button = radioButton;
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 51, 27.0f, 75.0f, 0.0f, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i = 22;
            int i2 = (this.isLast ? 22 : 15) + 76;
            if (!this.isFirst) {
                i = 0;
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(i2 + i), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            Theme.ThemeInfo themeInfo;
            if (this.optionsDrawable == null || (themeInfo = this.themeInfo) == null || ((themeInfo.info != null && !this.themeInfo.themeLoaded) || ThemesHorizontalListCell.this.currentType != 0)) {
                return super.onTouchEvent(event);
            }
            int action = event.getAction();
            if (action == 0 || action == 1) {
                float x = event.getX();
                float y = event.getY();
                if (x > this.rect.centerX() && y < this.rect.centerY() - AndroidUtilities.dp(10.0f)) {
                    if (action == 0) {
                        this.pressed = true;
                    } else {
                        performHapticFeedback(3);
                        ThemesHorizontalListCell.this.showOptionsForTheme(this.themeInfo);
                    }
                }
                if (action == 1) {
                    this.pressed = false;
                }
            }
            return this.pressed;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:116:0x027c  */
        /* JADX WARN: Removed duplicated region for block: B:117:0x027d A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:120:0x0285 A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:123:0x028d A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:126:0x0295 A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:129:0x029d A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:132:0x02a5 A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:137:0x02b1  */
        /* JADX WARN: Removed duplicated region for block: B:138:0x02b2 A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:139:0x02b7 A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:140:0x02bc A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:141:0x02c1 A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:142:0x02c7 A[Catch: all -> 0x02d3, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /* JADX WARN: Removed duplicated region for block: B:143:0x02cd A[Catch: all -> 0x02d3, TRY_LEAVE, TryCatch #8 {all -> 0x02d3, blocks: (B:91:0x0215, B:93:0x021b, B:95:0x0221, B:97:0x0227, B:99:0x022d, B:103:0x0238, B:105:0x0246, B:108:0x0255, B:111:0x025d, B:113:0x026d, B:114:0x0275, B:115:0x0279, B:117:0x027d, B:120:0x0285, B:123:0x028d, B:126:0x0295, B:129:0x029d, B:132:0x02a5, B:136:0x02ae, B:138:0x02b2, B:139:0x02b7, B:140:0x02bc, B:141:0x02c1, B:142:0x02c7, B:143:0x02cd), top: B:199:0x0215, inners: #10 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean parseTheme() {
            /*
                Method dump skipped, instructions count: 982
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.InnerThemeView.parseTheme():boolean");
        }

        /* renamed from: lambda$parseTheme$1$org-telegram-ui-Cells-ThemesHorizontalListCell$InnerThemeView */
        public /* synthetic */ void m1687xe745e278(final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Cells.ThemesHorizontalListCell$InnerThemeView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ThemesHorizontalListCell.InnerThemeView.this.m1686xf3b65e37(response);
                }
            });
        }

        /* renamed from: lambda$parseTheme$0$org-telegram-ui-Cells-ThemesHorizontalListCell$InnerThemeView */
        public /* synthetic */ void m1686xf3b65e37(TLObject response) {
            if (response instanceof TLRPC.TL_wallPaper) {
                TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) response;
                String name = FileLoader.getAttachFileName(wallPaper.document);
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(name)) {
                    ThemesHorizontalListCell.this.loadingThemes.put(name, this.themeInfo);
                    FileLoader.getInstance(this.themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
                    return;
                }
                return;
            }
            this.themeInfo.badWallpaper = true;
        }

        public void applyTheme() {
            this.inDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.getPreviewInColor(), PorterDuff.Mode.MULTIPLY));
            this.outDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.getPreviewOutColor(), PorterDuff.Mode.MULTIPLY));
            if (this.themeInfo.pathToFile == null) {
                updateColors(false);
                this.optionsDrawable = null;
            } else {
                this.optionsDrawable = getResources().getDrawable(R.drawable.preview_dots).mutate();
                int previewBackgroundColor = this.themeInfo.getPreviewBackgroundColor();
                this.backColor = previewBackgroundColor;
                this.oldBackColor = previewBackgroundColor;
            }
            this.bitmapShader = null;
            this.backgroundDrawable = null;
            double[] hsv = null;
            if (this.themeInfo.previewBackgroundGradientColor1 != 0 && this.themeInfo.previewBackgroundGradientColor2 != 0) {
                MotionBackgroundDrawable drawable = new MotionBackgroundDrawable(this.themeInfo.getPreviewBackgroundColor(), this.themeInfo.previewBackgroundGradientColor1, this.themeInfo.previewBackgroundGradientColor2, this.themeInfo.previewBackgroundGradientColor3, true);
                drawable.setRoundRadius(AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = drawable;
                hsv = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            } else if (this.themeInfo.previewBackgroundGradientColor1 != 0) {
                GradientDrawable drawable2 = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{this.themeInfo.getPreviewBackgroundColor(), this.themeInfo.previewBackgroundGradientColor1});
                drawable2.setCornerRadius(AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = drawable2;
                hsv = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            } else if (this.themeInfo.previewWallpaperOffset > 0 || this.themeInfo.pathToWallpaper != null) {
                Bitmap wallpaper = AndroidUtilities.getScaledBitmap(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(97.0f), this.themeInfo.pathToWallpaper, this.themeInfo.pathToFile, this.themeInfo.previewWallpaperOffset);
                if (wallpaper != null) {
                    this.backgroundDrawable = new BitmapDrawable(wallpaper);
                    BitmapShader bitmapShader = new BitmapShader(wallpaper, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    this.bitmapShader = bitmapShader;
                    this.bitmapPaint.setShader(bitmapShader);
                    int[] colors = AndroidUtilities.calcDrawableColor(this.backgroundDrawable);
                    hsv = AndroidUtilities.rgbToHsv(Color.red(colors[0]), Color.green(colors[0]), Color.blue(colors[0]));
                }
            } else if (this.themeInfo.getPreviewBackgroundColor() != 0) {
                hsv = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            }
            if (hsv != null && hsv[1] <= 0.10000000149011612d && hsv[2] >= 0.9599999785423279d) {
                this.hasWhiteBackground = true;
            } else {
                this.hasWhiteBackground = false;
            }
            if (this.themeInfo.getPreviewBackgroundColor() == 0 && this.themeInfo.previewParsed && this.backgroundDrawable == null) {
                Drawable createDefaultWallpaper = Theme.createDefaultWallpaper(100, 200);
                this.backgroundDrawable = createDefaultWallpaper;
                if (createDefaultWallpaper instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) createDefaultWallpaper).setRoundRadius(AndroidUtilities.dp(6.0f));
                }
            }
            invalidate();
        }

        public void setTheme(Theme.ThemeInfo theme, boolean last, boolean first) {
            this.themeInfo = theme;
            this.isFirst = first;
            this.isLast = last;
            this.accentId = theme.currentAccentId;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.button.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(this.isFirst ? 49.0f : 27.0f);
            this.button.setLayoutParams(layoutParams);
            this.placeholderAlpha = 0.0f;
            if (this.themeInfo.pathToFile != null && !this.themeInfo.previewParsed) {
                this.themeInfo.setPreviewInColor(Theme.getDefaultColor(Theme.key_chat_inBubble));
                this.themeInfo.setPreviewOutColor(Theme.getDefaultColor(Theme.key_chat_outBubble));
                File file = new File(this.themeInfo.pathToFile);
                boolean fileExists = file.exists();
                boolean parsed = fileExists && parseTheme();
                if ((!parsed || !fileExists) && this.themeInfo.info != null) {
                    if (this.themeInfo.info.document != null) {
                        this.themeInfo.themeLoaded = false;
                        this.placeholderAlpha = 1.0f;
                        Drawable mutate = getResources().getDrawable(R.drawable.msg_theme).mutate();
                        this.loadingDrawable = mutate;
                        int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                        this.loadingColor = color;
                        Theme.setDrawableColor(mutate, color);
                        if (!fileExists) {
                            String name = FileLoader.getAttachFileName(this.themeInfo.info.document);
                            if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(name)) {
                                ThemesHorizontalListCell.this.loadingThemes.put(name, this.themeInfo);
                                FileLoader.getInstance(this.themeInfo.account).loadFile(this.themeInfo.info.document, this.themeInfo.info, 1, 1);
                            }
                        }
                    } else {
                        Drawable mutate2 = getResources().getDrawable(R.drawable.preview_custom).mutate();
                        this.loadingDrawable = mutate2;
                        int color2 = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                        this.loadingColor = color2;
                        Theme.setDrawableColor(mutate2, color2);
                    }
                }
            }
            applyTheme();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            Theme.ThemeInfo t = ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.button.setChecked(this.themeInfo == t, false);
            Theme.ThemeInfo themeInfo = this.themeInfo;
            if (themeInfo != null && themeInfo.info != null && !this.themeInfo.themeLoaded) {
                String name = FileLoader.getAttachFileName(this.themeInfo.info.document);
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(name) && !ThemesHorizontalListCell.this.loadingWallpapers.containsKey(this.themeInfo)) {
                    this.themeInfo.themeLoaded = true;
                    this.placeholderAlpha = 0.0f;
                    parseTheme();
                    applyTheme();
                }
            }
        }

        public void updateCurrentThemeCheck() {
            Theme.ThemeInfo t = ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.button.setChecked(this.themeInfo == t, true);
        }

        void updateColors(boolean animate) {
            int backAccent;
            int myAccentColor;
            int accentColor;
            this.oldInColor = this.inColor;
            this.oldOutColor = this.outColor;
            this.oldBackColor = this.backColor;
            this.oldCheckColor = this.checkColor;
            Theme.ThemeAccent accent = this.themeInfo.getAccent(false);
            if (accent != null) {
                accentColor = accent.accentColor;
                myAccentColor = accent.myMessagesAccentColor != 0 ? accent.myMessagesAccentColor : accentColor;
                int backgroundOverrideColor = (int) accent.backgroundOverrideColor;
                backAccent = backgroundOverrideColor != 0 ? backgroundOverrideColor : accentColor;
            } else {
                accentColor = 0;
                myAccentColor = 0;
                backAccent = 0;
            }
            Theme.ThemeInfo themeInfo = this.themeInfo;
            this.inColor = Theme.changeColorAccent(themeInfo, accentColor, themeInfo.getPreviewInColor());
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            this.outColor = Theme.changeColorAccent(themeInfo2, myAccentColor, themeInfo2.getPreviewOutColor());
            Theme.ThemeInfo themeInfo3 = this.themeInfo;
            this.backColor = Theme.changeColorAccent(themeInfo3, backAccent, themeInfo3.getPreviewBackgroundColor());
            this.checkColor = this.outColor;
            this.accentId = this.themeInfo.currentAccentId;
            ObjectAnimator objectAnimator = this.accentAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (animate) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "accentState", 0.0f, 1.0f);
                this.accentAnimator = ofFloat;
                ofFloat.setDuration(200L);
                this.accentAnimator.start();
                return;
            }
            setAccentState(1.0f);
        }

        public float getAccentState() {
            return this.accentState;
        }

        public void setAccentState(float state) {
            this.accentState = state;
            this.accentColorChanged = true;
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float f;
            float bitmapW;
            boolean drawContent = true;
            if (this.accentId != this.themeInfo.currentAccentId) {
                updateColors(true);
            }
            int x = this.isFirst ? AndroidUtilities.dp(22.0f) : 0;
            int y = AndroidUtilities.dp(11.0f);
            this.rect.set(x, y, AndroidUtilities.dp(76.0f) + x, AndroidUtilities.dp(97.0f) + y);
            String name = getThemeName();
            int maxWidth = (getMeasuredWidth() - AndroidUtilities.dp(this.isFirst ? 10.0f : 15.0f)) - (this.isLast ? AndroidUtilities.dp(7.0f) : 0);
            String text = TextUtils.ellipsize(name, this.textPaint, maxWidth, TextUtils.TruncateAt.END).toString();
            int width = (int) Math.ceil(this.textPaint.measureText(text));
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.drawText(text, ((AndroidUtilities.dp(76.0f) - width) / 2) + x, AndroidUtilities.dp(131.0f), this.textPaint);
            if (this.themeInfo.info != null && (this.themeInfo.info.document == null || !this.themeInfo.themeLoaded)) {
                drawContent = false;
            }
            if (drawContent) {
                this.paint.setColor(blend(this.oldBackColor, this.backColor));
                if (this.accentColorChanged) {
                    this.inDrawable.setColorFilter(new PorterDuffColorFilter(blend(this.oldInColor, this.inColor), PorterDuff.Mode.MULTIPLY));
                    this.outDrawable.setColorFilter(new PorterDuffColorFilter(blend(this.oldOutColor, this.outColor), PorterDuff.Mode.MULTIPLY));
                    this.accentColorChanged = false;
                }
                Drawable drawable = this.backgroundDrawable;
                if (drawable == null) {
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.paint);
                } else if (this.bitmapShader == null) {
                    drawable.setBounds((int) this.rect.left, (int) this.rect.top, (int) this.rect.right, (int) this.rect.bottom);
                    this.backgroundDrawable.draw(canvas);
                } else {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    float bitmapW2 = bitmapDrawable.getBitmap().getWidth();
                    float bitmapH = bitmapDrawable.getBitmap().getHeight();
                    float scaleW = bitmapW2 / this.rect.width();
                    float scaleH = bitmapH / this.rect.height();
                    this.shaderMatrix.reset();
                    float scale = 1.0f / Math.min(scaleW, scaleH);
                    if (bitmapW2 / scaleH <= this.rect.width()) {
                        this.shaderMatrix.setTranslate(x, y - (((bitmapH / scaleW) - this.rect.height()) / 2.0f));
                        bitmapW = bitmapW2;
                    } else {
                        bitmapW = bitmapW2 / scaleH;
                        this.shaderMatrix.setTranslate(x - ((bitmapW - this.rect.width()) / 2.0f), y);
                    }
                    this.shaderMatrix.preScale(scale, scale);
                    this.bitmapShader.setLocalMatrix(this.shaderMatrix);
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.bitmapPaint);
                }
                this.button.setColor(1728053247, -1);
                if (this.themeInfo.accentBaseColor != 0) {
                    if ("Day".equals(this.themeInfo.name) || "Arctic Blue".equals(this.themeInfo.name)) {
                        this.button.setColor(-5000269, blend(this.oldCheckColor, this.checkColor));
                        Theme.chat_instantViewRectPaint.setColor(733001146);
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                        f = 6.0f;
                    } else {
                        f = 6.0f;
                    }
                } else if (this.hasWhiteBackground) {
                    this.button.setColor(-5000269, this.themeInfo.getPreviewOutColor());
                    Theme.chat_instantViewRectPaint.setColor(733001146);
                    f = 6.0f;
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                } else {
                    f = 6.0f;
                }
                this.inDrawable.setBounds(AndroidUtilities.dp(f) + x, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(49.0f) + x, AndroidUtilities.dp(36.0f));
                this.inDrawable.draw(canvas);
                this.outDrawable.setBounds(AndroidUtilities.dp(27.0f) + x, AndroidUtilities.dp(41.0f), AndroidUtilities.dp(70.0f) + x, AndroidUtilities.dp(55.0f));
                this.outDrawable.draw(canvas);
                if (this.optionsDrawable != null && ThemesHorizontalListCell.this.currentType == 0) {
                    int x2 = ((int) this.rect.right) - AndroidUtilities.dp(16.0f);
                    int y2 = ((int) this.rect.top) + AndroidUtilities.dp(6.0f);
                    Drawable drawable2 = this.optionsDrawable;
                    drawable2.setBounds(x2, y2, drawable2.getIntrinsicWidth() + x2, this.optionsDrawable.getIntrinsicHeight() + y2);
                    this.optionsDrawable.draw(canvas);
                }
            }
            if (this.themeInfo.info != null && this.themeInfo.info.document == null) {
                this.button.setAlpha(0.0f);
                Theme.chat_instantViewRectPaint.setColor(733001146);
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                if (this.loadingDrawable != null) {
                    int newColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                    if (this.loadingColor != newColor) {
                        Drawable drawable3 = this.loadingDrawable;
                        this.loadingColor = newColor;
                        Theme.setDrawableColor(drawable3, newColor);
                    }
                    int x3 = (int) (this.rect.centerX() - (this.loadingDrawable.getIntrinsicWidth() / 2));
                    int y3 = (int) (this.rect.centerY() - (this.loadingDrawable.getIntrinsicHeight() / 2));
                    Drawable drawable4 = this.loadingDrawable;
                    drawable4.setBounds(x3, y3, drawable4.getIntrinsicWidth() + x3, this.loadingDrawable.getIntrinsicHeight() + y3);
                    this.loadingDrawable.draw(canvas);
                }
            } else if ((this.themeInfo.info != null && !this.themeInfo.themeLoaded) || this.placeholderAlpha > 0.0f) {
                this.button.setAlpha(1.0f - this.placeholderAlpha);
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                this.paint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.paint);
                if (this.loadingDrawable != null) {
                    int newColor2 = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                    if (this.loadingColor != newColor2) {
                        Drawable drawable5 = this.loadingDrawable;
                        this.loadingColor = newColor2;
                        Theme.setDrawableColor(drawable5, newColor2);
                    }
                    int x4 = (int) (this.rect.centerX() - (this.loadingDrawable.getIntrinsicWidth() / 2));
                    int y4 = (int) (this.rect.centerY() - (this.loadingDrawable.getIntrinsicHeight() / 2));
                    this.loadingDrawable.setAlpha((int) (this.placeholderAlpha * 255.0f));
                    Drawable drawable6 = this.loadingDrawable;
                    drawable6.setBounds(x4, y4, drawable6.getIntrinsicWidth() + x4, this.loadingDrawable.getIntrinsicHeight() + y4);
                    this.loadingDrawable.draw(canvas);
                }
                if (this.themeInfo.themeLoaded) {
                    long newTime = SystemClock.elapsedRealtime();
                    long dt = Math.min(17L, newTime - this.lastDrawTime);
                    this.lastDrawTime = newTime;
                    float f2 = this.placeholderAlpha - (((float) dt) / 180.0f);
                    this.placeholderAlpha = f2;
                    if (f2 < 0.0f) {
                        this.placeholderAlpha = 0.0f;
                    }
                    invalidate();
                }
            } else if (this.button.getAlpha() != 1.0f) {
                this.button.setAlpha(1.0f);
            }
        }

        private String getThemeName() {
            String name = this.themeInfo.getName();
            if (name.toLowerCase().endsWith(".attheme")) {
                return name.substring(0, name.lastIndexOf(46));
            }
            return name;
        }

        private int blend(int color1, int color2) {
            float f = this.accentState;
            if (f == 1.0f) {
                return color2;
            }
            return ((Integer) this.evaluator.evaluate(f, Integer.valueOf(color1), Integer.valueOf(color2))).intValue();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(getThemeName());
            info.setClassName(Button.class.getName());
            info.setChecked(this.button.isChecked());
            info.setCheckable(true);
            info.setEnabled(true);
            if (Build.VERSION.SDK_INT >= 21) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions)));
            }
        }
    }

    public ThemesHorizontalListCell(Context context, int type, ArrayList<Theme.ThemeInfo> def, ArrayList<Theme.ThemeInfo> custom) {
        super(context);
        this.customThemes = custom;
        this.defaultThemes = def;
        this.currentType = type;
        if (type == 2) {
            setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        } else {
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        setItemAnimator(null);
        setLayoutAnimation(null);
        this.horizontalLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.Cells.ThemesHorizontalListCell.1
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        setPadding(0, 0, 0, 0);
        setClipToPadding(false);
        this.horizontalLayoutManager.setOrientation(0);
        setLayoutManager(this.horizontalLayoutManager);
        ThemesListAdapter themesListAdapter = new ThemesListAdapter(context);
        this.adapter = themesListAdapter;
        setAdapter(themesListAdapter);
        setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Cells.ThemesHorizontalListCell$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ThemesHorizontalListCell.this.m1684lambda$new$0$orgtelegramuiCellsThemesHorizontalListCell(view, i);
            }
        });
        setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Cells.ThemesHorizontalListCell$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return ThemesHorizontalListCell.this.m1685lambda$new$1$orgtelegramuiCellsThemesHorizontalListCell(view, i);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ThemesHorizontalListCell */
    public /* synthetic */ void m1684lambda$new$0$orgtelegramuiCellsThemesHorizontalListCell(View view1, int position) {
        selectTheme(((InnerThemeView) view1).themeInfo);
        int left = view1.getLeft();
        int right = view1.getRight();
        if (left < 0) {
            smoothScrollBy(left - AndroidUtilities.dp(8.0f), 0);
        } else if (right > getMeasuredWidth()) {
            smoothScrollBy(right - getMeasuredWidth(), 0);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-ThemesHorizontalListCell */
    public /* synthetic */ boolean m1685lambda$new$1$orgtelegramuiCellsThemesHorizontalListCell(View view12, int position) {
        InnerThemeView innerThemeView = (InnerThemeView) view12;
        showOptionsForTheme(innerThemeView.themeInfo);
        return true;
    }

    public void selectTheme(Theme.ThemeInfo themeInfo) {
        if (themeInfo.info != null) {
            if (!themeInfo.themeLoaded) {
                return;
            }
            if (themeInfo.info.document == null) {
                presentFragment(new ThemeSetUrlActivity(themeInfo, null, true));
                return;
            }
        }
        if (!TextUtils.isEmpty(themeInfo.assetName)) {
            Theme.PatternsLoader.createLoader(false);
        }
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        editor.putString((this.currentType == 1 || themeInfo.isDark()) ? "lastDarkTheme" : "lastDayTheme", themeInfo.getKey());
        editor.commit();
        if (this.currentType == 1) {
            if (themeInfo == Theme.getCurrentNightTheme()) {
                return;
            }
            Theme.setCurrentNightTheme(themeInfo);
        } else if (themeInfo == Theme.getCurrentTheme()) {
            return;
        } else {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, null, -1);
        }
        updateRows();
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof InnerThemeView) {
                ((InnerThemeView) child).updateCurrentThemeCheck();
            }
        }
        int a2 = themeInfo.currentAccentId;
        EmojiThemes.saveCustomTheme(themeInfo, a2);
    }

    public void setDrawDivider(boolean draw) {
        this.drawDivider = draw;
    }

    public void notifyDataSetChanged(int width) {
        if (this.prevCount == this.adapter.getItemCount()) {
            return;
        }
        this.adapter.notifyDataSetChanged();
        Theme.ThemeInfo t = this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        if (this.prevThemeInfo != t) {
            scrollToCurrentTheme(width, false);
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (getParent() != null && getParent().getParent() != null) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        invalidateViews();
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            String fileName = (String) args[0];
            final File file = (File) args[1];
            final Theme.ThemeInfo info = this.loadingThemes.get(fileName);
            if (info != null) {
                this.loadingThemes.remove(fileName);
                if (this.loadingWallpapers.remove(info) != null) {
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Cells.ThemesHorizontalListCell$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ThemesHorizontalListCell.this.m1683x4f4ea2e7(info, file);
                        }
                    });
                } else {
                    m1682x15840108(info);
                }
            }
        } else if (id == NotificationCenter.fileLoadFailed) {
            this.loadingThemes.remove((String) args[0]);
        }
    }

    /* renamed from: lambda$didReceivedNotification$3$org-telegram-ui-Cells-ThemesHorizontalListCell */
    public /* synthetic */ void m1683x4f4ea2e7(final Theme.ThemeInfo info, File file) {
        info.badWallpaper = !info.createBackground(file, info.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Cells.ThemesHorizontalListCell$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ThemesHorizontalListCell.this.m1682x15840108(info);
            }
        });
    }

    /* renamed from: checkVisibleTheme */
    public void m1682x15840108(Theme.ThemeInfo info) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof InnerThemeView) {
                InnerThemeView view = (InnerThemeView) child;
                if (view.themeInfo == info && view.parseTheme()) {
                    view.themeInfo.themeLoaded = true;
                    view.applyTheme();
                }
            }
        }
    }

    public void scrollToCurrentTheme(int width, boolean animated) {
        View parent;
        if (width == 0 && (parent = (View) getParent()) != null) {
            width = parent.getMeasuredWidth();
        }
        if (width == 0) {
            return;
        }
        Theme.ThemeInfo currentNightTheme = this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        this.prevThemeInfo = currentNightTheme;
        int index = this.defaultThemes.indexOf(currentNightTheme);
        if (index < 0 && (index = this.customThemes.indexOf(this.prevThemeInfo) + this.defaultThemes.size()) < 0) {
            return;
        }
        if (animated) {
            smoothScrollToPosition(index);
        } else {
            this.horizontalLayoutManager.scrollToPositionWithOffset(index, (width - AndroidUtilities.dp(76.0f)) / 2);
        }
    }

    protected void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
    }

    protected void presentFragment(BaseFragment fragment) {
    }

    protected void updateRows() {
    }
}
