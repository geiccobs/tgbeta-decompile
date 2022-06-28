package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Cells.PhotoEditRadioCell;
import org.telegram.ui.Cells.PhotoEditToolCell;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.PhotoEditorSeekBar;
import org.telegram.ui.Components.PhotoFilterBlurControl;
import org.telegram.ui.Components.PhotoFilterCurvesControl;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.VideoEditTextureView;
/* loaded from: classes5.dex */
public class PhotoFilterView extends FrameLayout implements FilterShaders.FilterShadersDelegate {
    private static final int curveDataStep = 2;
    private static final int curveGranularity = 100;
    private Bitmap bitmapToEdit;
    private float blurAngle;
    private PhotoFilterBlurControl blurControl;
    private float blurExcludeBlurSize;
    private Point blurExcludePoint;
    private float blurExcludeSize;
    private ImageView blurItem;
    private FrameLayout blurLayout;
    private TextView blurLinearButton;
    private TextView blurOffButton;
    private TextView blurRadialButton;
    private int blurType;
    private TextView cancelTextView;
    private int contrastTool;
    private float contrastValue;
    private ImageView curveItem;
    private FrameLayout curveLayout;
    private RadioButton[] curveRadioButton = new RadioButton[4];
    private PhotoFilterCurvesControl curvesControl;
    private CurvesToolValue curvesToolValue;
    private TextView doneTextView;
    private FilterGLThread eglThread;
    private int enhanceTool;
    private float enhanceValue;
    private int exposureTool;
    private float exposureValue;
    private int fadeTool;
    private float fadeValue;
    private int grainTool;
    private float grainValue;
    private int highlightsTool;
    private float highlightsValue;
    private boolean inBubbleMode;
    private boolean isMirrored;
    private MediaController.SavedFilterState lastState;
    private int orientation;
    private boolean ownsTextureView;
    private PaintingOverlay paintingOverlay;
    private RecyclerListView recyclerListView;
    private final Theme.ResourcesProvider resourcesProvider;
    private int rowsCount;
    private int saturationTool;
    private float saturationValue;
    private int selectedTool;
    private int shadowsTool;
    private float shadowsValue;
    private int sharpenTool;
    private float sharpenValue;
    private boolean showOriginal;
    private int softenSkinTool;
    private float softenSkinValue;
    private TextureView textureView;
    private int tintHighlightsColor;
    private int tintHighlightsTool;
    private int tintShadowsColor;
    private int tintShadowsTool;
    private FrameLayout toolsView;
    private ImageView tuneItem;
    private int vignetteTool;
    private float vignetteValue;
    private int warmthTool;
    private float warmthValue;

    /* loaded from: classes5.dex */
    public static class CurvesValue {
        public float[] cachedDataPoints;
        public float blacksLevel = 0.0f;
        public float shadowsLevel = 25.0f;
        public float midtonesLevel = 50.0f;
        public float highlightsLevel = 75.0f;
        public float whitesLevel = 100.0f;
        public float previousBlacksLevel = 0.0f;
        public float previousShadowsLevel = 25.0f;
        public float previousMidtonesLevel = 50.0f;
        public float previousHighlightsLevel = 75.0f;
        public float previousWhitesLevel = 100.0f;

        public float[] getDataPoints() {
            if (this.cachedDataPoints == null) {
                interpolateCurve();
            }
            return this.cachedDataPoints;
        }

        public void saveValues() {
            this.previousBlacksLevel = this.blacksLevel;
            this.previousShadowsLevel = this.shadowsLevel;
            this.previousMidtonesLevel = this.midtonesLevel;
            this.previousHighlightsLevel = this.highlightsLevel;
            this.previousWhitesLevel = this.whitesLevel;
        }

        public void restoreValues() {
            this.blacksLevel = this.previousBlacksLevel;
            this.shadowsLevel = this.previousShadowsLevel;
            this.midtonesLevel = this.previousMidtonesLevel;
            this.highlightsLevel = this.previousHighlightsLevel;
            this.whitesLevel = this.previousWhitesLevel;
            interpolateCurve();
        }

        public float[] interpolateCurve() {
            float f = this.blacksLevel;
            int i = 1;
            float f2 = 0.5f;
            float f3 = this.whitesLevel;
            float[] points = {-0.001f, f / 100.0f, 0.0f, f / 100.0f, 0.25f, this.shadowsLevel / 100.0f, 0.5f, this.midtonesLevel / 100.0f, 0.75f, this.highlightsLevel / 100.0f, 1.0f, f3 / 100.0f, 1.001f, f3 / 100.0f};
            int i2 = 100;
            ArrayList<Float> dataPoints = new ArrayList<>(100);
            ArrayList<Float> interpolatedPoints = new ArrayList<>(100);
            interpolatedPoints.add(Float.valueOf(points[0]));
            interpolatedPoints.add(Float.valueOf(points[1]));
            int index = 1;
            while (index < (points.length / 2) - 2) {
                float point0x = points[(index - 1) * 2];
                float point0y = points[((index - 1) * 2) + i];
                float point1x = points[index * 2];
                float point1y = points[(index * 2) + 1];
                float point2x = points[(index + 1) * 2];
                float point2y = points[((index + 1) * 2) + 1];
                float point3x = points[(index + 2) * 2];
                float point3y = points[((index + 2) * 2) + 1];
                int i3 = 1;
                while (i3 < i2) {
                    float t = i3 * 0.01f;
                    float tt = t * t;
                    float ttt = tt * t;
                    float pix = ((point1x * 2.0f) + ((point2x - point0x) * t) + (((((point0x * 2.0f) - (point1x * 5.0f)) + (point2x * 4.0f)) - point3x) * tt) + (((((point1x * 3.0f) - point0x) - (point2x * 3.0f)) + point3x) * ttt)) * f2;
                    float piy = Math.max(0.0f, Math.min(1.0f, ((point1y * 2.0f) + ((point2y - point0y) * t) + (((((2.0f * point0y) - (5.0f * point1y)) + (4.0f * point2y)) - point3y) * tt) + (((((point1y * 3.0f) - point0y) - (3.0f * point2y)) + point3y) * ttt)) * f2));
                    if (pix > point0x) {
                        interpolatedPoints.add(Float.valueOf(pix));
                        interpolatedPoints.add(Float.valueOf(piy));
                    }
                    if ((i3 - 1) % 2 == 0) {
                        dataPoints.add(Float.valueOf(piy));
                    }
                    i3++;
                    f2 = 0.5f;
                    i2 = 100;
                }
                interpolatedPoints.add(Float.valueOf(point2x));
                interpolatedPoints.add(Float.valueOf(point2y));
                index++;
                i = 1;
                f2 = 0.5f;
                i2 = 100;
            }
            interpolatedPoints.add(Float.valueOf(points[12]));
            interpolatedPoints.add(Float.valueOf(points[13]));
            this.cachedDataPoints = new float[dataPoints.size()];
            int a = 0;
            while (true) {
                float[] fArr = this.cachedDataPoints;
                if (a >= fArr.length) {
                    break;
                }
                fArr[a] = dataPoints.get(a).floatValue();
                a++;
            }
            int a2 = interpolatedPoints.size();
            float[] retValue = new float[a2];
            for (int a3 = 0; a3 < retValue.length; a3++) {
                retValue[a3] = interpolatedPoints.get(a3).floatValue();
            }
            return retValue;
        }

        public boolean isDefault() {
            return ((double) Math.abs(this.blacksLevel - 0.0f)) < 1.0E-5d && ((double) Math.abs(this.shadowsLevel - 25.0f)) < 1.0E-5d && ((double) Math.abs(this.midtonesLevel - 50.0f)) < 1.0E-5d && ((double) Math.abs(this.highlightsLevel - 75.0f)) < 1.0E-5d && ((double) Math.abs(this.whitesLevel - 100.0f)) < 1.0E-5d;
        }
    }

    /* loaded from: classes5.dex */
    public static class CurvesToolValue {
        public static final int CurvesTypeBlue = 3;
        public static final int CurvesTypeGreen = 2;
        public static final int CurvesTypeLuminance = 0;
        public static final int CurvesTypeRed = 1;
        public int activeType;
        public ByteBuffer curveBuffer;
        public CurvesValue luminanceCurve = new CurvesValue();
        public CurvesValue redCurve = new CurvesValue();
        public CurvesValue greenCurve = new CurvesValue();
        public CurvesValue blueCurve = new CurvesValue();

        public CurvesToolValue() {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(800);
            this.curveBuffer = allocateDirect;
            allocateDirect.order(ByteOrder.LITTLE_ENDIAN);
        }

        public void fillBuffer() {
            this.curveBuffer.position(0);
            float[] luminanceCurveData = this.luminanceCurve.getDataPoints();
            float[] redCurveData = this.redCurve.getDataPoints();
            float[] greenCurveData = this.greenCurve.getDataPoints();
            float[] blueCurveData = this.blueCurve.getDataPoints();
            for (int a = 0; a < 200; a++) {
                this.curveBuffer.put((byte) (redCurveData[a] * 255.0f));
                this.curveBuffer.put((byte) (greenCurveData[a] * 255.0f));
                this.curveBuffer.put((byte) (blueCurveData[a] * 255.0f));
                this.curveBuffer.put((byte) (luminanceCurveData[a] * 255.0f));
            }
            this.curveBuffer.position(0);
        }

        public boolean shouldBeSkipped() {
            return this.luminanceCurve.isDefault() && this.redCurve.isDefault() && this.greenCurve.isDefault() && this.blueCurve.isDefault();
        }
    }

    public PhotoFilterView(Context context, VideoEditTextureView videoTextureView, Bitmap bitmap, int rotation, MediaController.SavedFilterState state, PaintingOverlay overlay, int hasFaces, boolean mirror, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        LinearLayoutManager layoutManager;
        this.resourcesProvider = resourcesProvider;
        this.inBubbleMode = context instanceof BubbleActivity;
        this.paintingOverlay = overlay;
        this.isMirrored = mirror;
        this.rowsCount = 0;
        if (hasFaces == 1) {
            this.rowsCount = 0 + 1;
            this.softenSkinTool = 0;
        } else if (hasFaces == 0) {
            this.softenSkinTool = -1;
        }
        int i = this.rowsCount;
        int i2 = i + 1;
        this.rowsCount = i2;
        this.enhanceTool = i;
        int i3 = i2 + 1;
        this.rowsCount = i3;
        this.exposureTool = i2;
        int i4 = i3 + 1;
        this.rowsCount = i4;
        this.contrastTool = i3;
        int i5 = i4 + 1;
        this.rowsCount = i5;
        this.saturationTool = i4;
        int i6 = i5 + 1;
        this.rowsCount = i6;
        this.warmthTool = i5;
        int i7 = i6 + 1;
        this.rowsCount = i7;
        this.fadeTool = i6;
        int i8 = i7 + 1;
        this.rowsCount = i8;
        this.highlightsTool = i7;
        int i9 = i8 + 1;
        this.rowsCount = i9;
        this.shadowsTool = i8;
        int i10 = i9 + 1;
        this.rowsCount = i10;
        this.vignetteTool = i9;
        if (hasFaces == 2) {
            this.rowsCount = i10 + 1;
            this.softenSkinTool = i10;
        }
        if (videoTextureView == null) {
            int i11 = this.rowsCount;
            this.rowsCount = i11 + 1;
            this.grainTool = i11;
        } else {
            this.grainTool = -1;
        }
        int i12 = this.rowsCount;
        int i13 = i12 + 1;
        this.rowsCount = i13;
        this.sharpenTool = i12;
        int i14 = i13 + 1;
        this.rowsCount = i14;
        this.tintShadowsTool = i13;
        this.rowsCount = i14 + 1;
        this.tintHighlightsTool = i14;
        if (state != null) {
            this.enhanceValue = state.enhanceValue;
            this.softenSkinValue = state.softenSkinValue;
            this.exposureValue = state.exposureValue;
            this.contrastValue = state.contrastValue;
            this.warmthValue = state.warmthValue;
            this.saturationValue = state.saturationValue;
            this.fadeValue = state.fadeValue;
            this.tintShadowsColor = state.tintShadowsColor;
            this.tintHighlightsColor = state.tintHighlightsColor;
            this.highlightsValue = state.highlightsValue;
            this.shadowsValue = state.shadowsValue;
            this.vignetteValue = state.vignetteValue;
            this.grainValue = state.grainValue;
            this.blurType = state.blurType;
            this.sharpenValue = state.sharpenValue;
            this.curvesToolValue = state.curvesToolValue;
            this.blurExcludeSize = state.blurExcludeSize;
            this.blurExcludePoint = state.blurExcludePoint;
            this.blurExcludeBlurSize = state.blurExcludeBlurSize;
            this.blurAngle = state.blurAngle;
            this.lastState = state;
        } else {
            this.curvesToolValue = new CurvesToolValue();
            this.blurExcludeSize = 0.35f;
            this.blurExcludePoint = new Point(0.5f, 0.5f);
            this.blurExcludeBlurSize = 0.15f;
            this.blurAngle = 1.5707964f;
        }
        this.bitmapToEdit = bitmap;
        this.orientation = rotation;
        if (videoTextureView != null) {
            this.textureView = videoTextureView;
            videoTextureView.setDelegate(new VideoEditTextureView.VideoEditTextureViewDelegate() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda9
                @Override // org.telegram.ui.Components.VideoEditTextureView.VideoEditTextureViewDelegate
                public final void onEGLThreadAvailable(FilterGLThread filterGLThread) {
                    PhotoFilterView.this.m2814lambda$new$0$orgtelegramuiComponentsPhotoFilterView(filterGLThread);
                }
            });
        } else {
            this.ownsTextureView = true;
            TextureView textureView = new TextureView(context);
            this.textureView = textureView;
            addView(textureView, LayoutHelper.createFrame(-1, -1, 51));
            this.textureView.setVisibility(4);
            this.textureView.setSurfaceTextureListener(new AnonymousClass1());
        }
        PhotoFilterBlurControl photoFilterBlurControl = new PhotoFilterBlurControl(context);
        this.blurControl = photoFilterBlurControl;
        photoFilterBlurControl.setVisibility(4);
        addView(this.blurControl, LayoutHelper.createFrame(-1, -1, 51));
        this.blurControl.setDelegate(new PhotoFilterBlurControl.PhotoFilterLinearBlurControlDelegate() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.PhotoFilterBlurControl.PhotoFilterLinearBlurControlDelegate
            public final void valueChanged(Point point, float f, float f2, float f3) {
                PhotoFilterView.this.m2815lambda$new$1$orgtelegramuiComponentsPhotoFilterView(point, f, f2, f3);
            }
        });
        PhotoFilterCurvesControl photoFilterCurvesControl = new PhotoFilterCurvesControl(context, this.curvesToolValue);
        this.curvesControl = photoFilterCurvesControl;
        photoFilterCurvesControl.setDelegate(new PhotoFilterCurvesControl.PhotoFilterCurvesControlDelegate() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.PhotoFilterCurvesControl.PhotoFilterCurvesControlDelegate
            public final void valueChanged() {
                PhotoFilterView.this.m2816lambda$new$2$orgtelegramuiComponentsPhotoFilterView();
            }
        });
        this.curvesControl.setVisibility(4);
        addView(this.curvesControl, LayoutHelper.createFrame(-1, -1, 51));
        FrameLayout frameLayout = new FrameLayout(context);
        this.toolsView = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(-1, 186, 83));
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout2.setBackgroundColor(-16777216);
        this.toolsView.addView(frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        TextView textView = new TextView(context);
        this.cancelTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        this.cancelTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        frameLayout2.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        TextView textView2 = new TextView(context);
        this.doneTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        frameLayout2.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        LinearLayout linearLayout = new LinearLayout(context);
        frameLayout2.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 1));
        ImageView imageView = new ImageView(context);
        this.tuneItem = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.tuneItem.setImageResource(R.drawable.msg_photo_settings);
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
        this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        linearLayout.addView(this.tuneItem, LayoutHelper.createLinear(56, 48));
        this.tuneItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoFilterView.this.m2817lambda$new$3$orgtelegramuiComponentsPhotoFilterView(view);
            }
        });
        ImageView imageView2 = new ImageView(context);
        this.blurItem = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.blurItem.setImageResource(R.drawable.msg_photo_blur);
        this.blurItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        linearLayout.addView(this.blurItem, LayoutHelper.createLinear(56, 48));
        this.blurItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoFilterView.this.m2818lambda$new$4$orgtelegramuiComponentsPhotoFilterView(view);
            }
        });
        if (videoTextureView != null) {
            this.blurItem.setVisibility(8);
        }
        ImageView imageView3 = new ImageView(context);
        this.curveItem = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.curveItem.setImageResource(R.drawable.msg_photo_curve);
        this.curveItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        linearLayout.addView(this.curveItem, LayoutHelper.createLinear(56, 48));
        this.curveItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoFilterView.this.m2819lambda$new$5$orgtelegramuiComponentsPhotoFilterView(view);
            }
        });
        this.recyclerListView = new RecyclerListView(context);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(context);
        layoutManager2.setOrientation(1);
        this.recyclerListView.setLayoutManager(layoutManager2);
        this.recyclerListView.setClipToPadding(false);
        this.recyclerListView.setOverScrollMode(2);
        this.recyclerListView.setAdapter(new ToolsAdapter(context));
        this.toolsView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, 120, 51));
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.curveLayout = frameLayout3;
        frameLayout3.setVisibility(4);
        this.toolsView.addView(this.curveLayout, LayoutHelper.createFrame(-1, 78.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
        LinearLayout curveTextViewContainer = new LinearLayout(context);
        curveTextViewContainer.setOrientation(0);
        this.curveLayout.addView(curveTextViewContainer, LayoutHelper.createFrame(-2, -2, 1));
        int a = 0;
        while (a < 4) {
            FrameLayout frameLayout1 = new FrameLayout(context);
            frameLayout1.setTag(Integer.valueOf(a));
            this.curveRadioButton[a] = new RadioButton(context);
            LinearLayout linearLayout2 = linearLayout;
            this.curveRadioButton[a].setSize(AndroidUtilities.dp(20.0f));
            frameLayout1.addView(this.curveRadioButton[a], LayoutHelper.createFrame(30, 30, 49));
            TextView curveTextView = new TextView(context);
            curveTextView.setTextSize(1, 12.0f);
            curveTextView.setGravity(16);
            if (a == 0) {
                String str = LocaleController.getString("CurvesAll", R.string.CurvesAll);
                StringBuilder sb = new StringBuilder();
                layoutManager = layoutManager2;
                sb.append(str.substring(0, 1).toUpperCase());
                sb.append(str.substring(1).toLowerCase());
                curveTextView.setText(sb.toString());
                curveTextView.setTextColor(-1);
                this.curveRadioButton[a].setColor(-1, -1);
            } else {
                layoutManager = layoutManager2;
                if (a == 1) {
                    String str2 = LocaleController.getString("CurvesRed", R.string.CurvesRed);
                    curveTextView.setText(str2.substring(0, 1).toUpperCase() + str2.substring(1).toLowerCase());
                    curveTextView.setTextColor(-1684147);
                    this.curveRadioButton[a].setColor(-1684147, -1684147);
                } else if (a == 2) {
                    String str3 = LocaleController.getString("CurvesGreen", R.string.CurvesGreen);
                    curveTextView.setText(str3.substring(0, 1).toUpperCase() + str3.substring(1).toLowerCase());
                    curveTextView.setTextColor(-10831009);
                    this.curveRadioButton[a].setColor(-10831009, -10831009);
                } else if (a == 3) {
                    String str4 = LocaleController.getString("CurvesBlue", R.string.CurvesBlue);
                    curveTextView.setText(str4.substring(0, 1).toUpperCase() + str4.substring(1).toLowerCase());
                    curveTextView.setTextColor(-12734994);
                    this.curveRadioButton[a].setColor(-12734994, -12734994);
                }
            }
            frameLayout1.addView(curveTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 38.0f, 0.0f, 0.0f));
            curveTextViewContainer.addView(frameLayout1, LayoutHelper.createLinear(-2, -2, a == 0 ? 0.0f : 30.0f, 0.0f, 0.0f, 0.0f));
            frameLayout1.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoFilterView.this.m2820lambda$new$6$orgtelegramuiComponentsPhotoFilterView(view);
                }
            });
            a++;
            linearLayout = linearLayout2;
            layoutManager2 = layoutManager;
        }
        FrameLayout frameLayout4 = new FrameLayout(context);
        this.blurLayout = frameLayout4;
        frameLayout4.setVisibility(4);
        this.toolsView.addView(this.blurLayout, LayoutHelper.createFrame(280, 60.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.blurOffButton = textView3;
        textView3.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurOffButton.setTextSize(1, 13.0f);
        this.blurOffButton.setGravity(1);
        this.blurOffButton.setText(LocaleController.getString("BlurOff", R.string.BlurOff));
        this.blurLayout.addView(this.blurOffButton, LayoutHelper.createFrame(80, 60.0f));
        this.blurOffButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoFilterView.this.m2821lambda$new$7$orgtelegramuiComponentsPhotoFilterView(view);
            }
        });
        TextView textView4 = new TextView(context);
        this.blurRadialButton = textView4;
        textView4.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurRadialButton.setTextSize(1, 13.0f);
        this.blurRadialButton.setGravity(1);
        this.blurRadialButton.setText(LocaleController.getString("BlurRadial", R.string.BlurRadial));
        this.blurLayout.addView(this.blurRadialButton, LayoutHelper.createFrame(80, 80.0f, 51, 100.0f, 0.0f, 0.0f, 0.0f));
        this.blurRadialButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoFilterView.this.m2822lambda$new$8$orgtelegramuiComponentsPhotoFilterView(view);
            }
        });
        TextView textView5 = new TextView(context);
        this.blurLinearButton = textView5;
        textView5.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurLinearButton.setTextSize(1, 13.0f);
        this.blurLinearButton.setGravity(1);
        this.blurLinearButton.setText(LocaleController.getString("BlurLinear", R.string.BlurLinear));
        this.blurLayout.addView(this.blurLinearButton, LayoutHelper.createFrame(80, 80.0f, 51, 200.0f, 0.0f, 0.0f, 0.0f));
        this.blurLinearButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoFilterView$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoFilterView.this.m2823lambda$new$9$orgtelegramuiComponentsPhotoFilterView(view);
            }
        });
        updateSelectedBlurType();
        if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
            if (this.ownsTextureView) {
                ((FrameLayout.LayoutParams) this.textureView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
            }
            ((FrameLayout.LayoutParams) this.curvesControl.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2814lambda$new$0$orgtelegramuiComponentsPhotoFilterView(FilterGLThread thread) {
        this.eglThread = thread;
        thread.setFilterGLThreadDelegate(this);
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterView$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 implements TextureView.SurfaceTextureListener {
        AnonymousClass1() {
            PhotoFilterView.this = this$0;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (PhotoFilterView.this.eglThread == null && surface != null) {
                PhotoFilterView.this.eglThread = new FilterGLThread(surface, PhotoFilterView.this.bitmapToEdit, PhotoFilterView.this.orientation, PhotoFilterView.this.isMirrored);
                PhotoFilterView.this.eglThread.setFilterGLThreadDelegate(PhotoFilterView.this);
                PhotoFilterView.this.eglThread.setSurfaceTextureSize(width, height);
                PhotoFilterView.this.eglThread.requestRender(true, true, false);
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.setSurfaceTextureSize(width, height);
                PhotoFilterView.this.eglThread.requestRender(false, true, false);
                PhotoFilterView.this.eglThread.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.PhotoFilterView$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoFilterView.AnonymousClass1.this.m2824x9475d40a();
                    }
                });
            }
        }

        /* renamed from: lambda$onSurfaceTextureSizeChanged$0$org-telegram-ui-Components-PhotoFilterView$1 */
        public /* synthetic */ void m2824x9475d40a() {
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false, true, false);
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.shutdown();
                PhotoFilterView.this.eglThread = null;
                return true;
            }
            return true;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2815lambda$new$1$orgtelegramuiComponentsPhotoFilterView(Point centerPoint, float falloff, float size, float angle) {
        this.blurExcludeSize = size;
        this.blurExcludePoint = centerPoint;
        this.blurExcludeBlurSize = falloff;
        this.blurAngle = angle;
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2816lambda$new$2$orgtelegramuiComponentsPhotoFilterView() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2817lambda$new$3$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.selectedTool = 0;
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2818lambda$new$4$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.selectedTool = 1;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2819lambda$new$5$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.selectedTool = 2;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
        switchMode();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2820lambda$new$6$orgtelegramuiComponentsPhotoFilterView(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.curvesToolValue.activeType = num;
        int a1 = 0;
        while (a1 < 4) {
            this.curveRadioButton[a1].setChecked(a1 == num, true);
            a1++;
        }
        this.curvesControl.invalidate();
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2821lambda$new$7$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.blurType = 0;
        updateSelectedBlurType();
        this.blurControl.setVisibility(4);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2822lambda$new$8$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.blurType = 1;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(1);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-PhotoFilterView */
    public /* synthetic */ void m2823lambda$new$9$orgtelegramuiComponentsPhotoFilterView(View v) {
        this.blurType = 2;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(0);
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    public void updateColors() {
        TextView textView = this.doneTextView;
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
        }
        ImageView imageView = this.tuneItem;
        if (imageView != null && imageView.getColorFilter() != null) {
            this.tuneItem.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView2 = this.blurItem;
        if (imageView2 != null && imageView2.getColorFilter() != null) {
            this.blurItem.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.curveItem;
        if (imageView3 != null && imageView3.getColorFilter() != null) {
            this.curveItem.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
        }
        updateSelectedBlurType();
    }

    private void updateSelectedBlurType() {
        int i = this.blurType;
        if (i == 0) {
            Drawable drawable = this.blurOffButton.getContext().getResources().getDrawable(R.drawable.msg_blur_off).mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
            this.blurOffButton.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.msg_blur_radial, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.msg_blur_linear, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (i == 1) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.msg_blur_off, 0, 0);
            this.blurOffButton.setTextColor(-1);
            Drawable drawable2 = this.blurOffButton.getContext().getResources().getDrawable(R.drawable.msg_blur_radial).mutate();
            drawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable2, (Drawable) null, (Drawable) null);
            this.blurRadialButton.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.msg_blur_linear, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (i == 2) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.msg_blur_off, 0, 0);
            this.blurOffButton.setTextColor(-1);
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.msg_blur_radial, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            Drawable drawable3 = this.blurOffButton.getContext().getResources().getDrawable(R.drawable.msg_blur_linear).mutate();
            drawable3.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable3, (Drawable) null, (Drawable) null);
            this.blurLinearButton.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
        }
    }

    public MediaController.SavedFilterState getSavedFilterState() {
        MediaController.SavedFilterState state = new MediaController.SavedFilterState();
        state.enhanceValue = this.enhanceValue;
        state.exposureValue = this.exposureValue;
        state.contrastValue = this.contrastValue;
        state.warmthValue = this.warmthValue;
        state.saturationValue = this.saturationValue;
        state.fadeValue = this.fadeValue;
        state.softenSkinValue = this.softenSkinValue;
        state.tintShadowsColor = this.tintShadowsColor;
        state.tintHighlightsColor = this.tintHighlightsColor;
        state.highlightsValue = this.highlightsValue;
        state.shadowsValue = this.shadowsValue;
        state.vignetteValue = this.vignetteValue;
        state.grainValue = this.grainValue;
        state.blurType = this.blurType;
        state.sharpenValue = this.sharpenValue;
        state.curvesToolValue = this.curvesToolValue;
        state.blurExcludeSize = this.blurExcludeSize;
        state.blurExcludePoint = this.blurExcludePoint;
        state.blurExcludeBlurSize = this.blurExcludeBlurSize;
        state.blurAngle = this.blurAngle;
        this.lastState = state;
        return state;
    }

    public boolean hasChanges() {
        MediaController.SavedFilterState savedFilterState = this.lastState;
        return savedFilterState != null ? (this.enhanceValue == savedFilterState.enhanceValue && this.contrastValue == this.lastState.contrastValue && this.highlightsValue == this.lastState.highlightsValue && this.exposureValue == this.lastState.exposureValue && this.warmthValue == this.lastState.warmthValue && this.saturationValue == this.lastState.saturationValue && this.vignetteValue == this.lastState.vignetteValue && this.shadowsValue == this.lastState.shadowsValue && this.grainValue == this.lastState.grainValue && this.sharpenValue == this.lastState.sharpenValue && this.fadeValue == this.lastState.fadeValue && this.softenSkinValue == this.lastState.softenSkinValue && this.tintHighlightsColor == this.lastState.tintHighlightsColor && this.tintShadowsColor == this.lastState.tintShadowsColor && this.curvesToolValue.shouldBeSkipped()) ? false : true : (this.enhanceValue == 0.0f && this.contrastValue == 0.0f && this.highlightsValue == 0.0f && this.exposureValue == 0.0f && this.warmthValue == 0.0f && this.saturationValue == 0.0f && this.vignetteValue == 0.0f && this.shadowsValue == 0.0f && this.grainValue == 0.0f && this.sharpenValue == 0.0f && this.fadeValue == 0.0f && this.softenSkinValue == 0.0f && this.tintHighlightsColor == 0 && this.tintShadowsColor == 0 && this.curvesToolValue.shouldBeSkipped()) ? false : true;
    }

    public void onTouch(MotionEvent event) {
        if (event.getActionMasked() == 0 || event.getActionMasked() == 5) {
            TextureView textureView = this.textureView;
            if (textureView instanceof VideoEditTextureView) {
                if (((VideoEditTextureView) textureView).containsPoint(event.getX(), event.getY())) {
                    setShowOriginal(true);
                }
            } else if (event.getX() >= this.textureView.getX() && event.getY() >= this.textureView.getY() && event.getX() <= this.textureView.getX() + this.textureView.getWidth() && event.getY() <= this.textureView.getY() + this.textureView.getHeight()) {
                setShowOriginal(true);
            }
        } else if (event.getActionMasked() == 1 || event.getActionMasked() == 6) {
            setShowOriginal(false);
        }
    }

    private void setShowOriginal(boolean value) {
        if (this.showOriginal == value) {
            return;
        }
        this.showOriginal = value;
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false);
        }
    }

    public void switchMode() {
        int i = this.selectedTool;
        if (i == 0) {
            this.blurControl.setVisibility(4);
            this.blurLayout.setVisibility(4);
            this.curveLayout.setVisibility(4);
            this.curvesControl.setVisibility(4);
            this.recyclerListView.setVisibility(0);
        } else if (i == 1) {
            this.recyclerListView.setVisibility(4);
            this.curveLayout.setVisibility(4);
            this.curvesControl.setVisibility(4);
            this.blurLayout.setVisibility(0);
            if (this.blurType != 0) {
                this.blurControl.setVisibility(0);
            }
            updateSelectedBlurType();
        } else if (i == 2) {
            this.recyclerListView.setVisibility(4);
            this.blurLayout.setVisibility(4);
            this.blurControl.setVisibility(4);
            this.curveLayout.setVisibility(0);
            this.curvesControl.setVisibility(0);
            this.curvesToolValue.activeType = 0;
            int a = 0;
            while (a < 4) {
                this.curveRadioButton[a].setChecked(a == 0, false);
                a++;
            }
        }
    }

    public void shutdown() {
        if (this.ownsTextureView) {
            FilterGLThread filterGLThread = this.eglThread;
            if (filterGLThread != null) {
                filterGLThread.shutdown();
                this.eglThread = null;
            }
            this.textureView.setVisibility(8);
            return;
        }
        TextureView textureView = this.textureView;
        if (textureView instanceof VideoEditTextureView) {
            VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView;
            MediaController.SavedFilterState savedFilterState = this.lastState;
            if (savedFilterState == null) {
                videoEditTextureView.setDelegate(null);
            } else {
                this.eglThread.setFilterGLThreadDelegate(FilterShaders.getFilterShadersDelegate(savedFilterState));
            }
        }
    }

    public void init() {
        if (this.ownsTextureView) {
            this.textureView.setVisibility(0);
        }
    }

    public Bitmap getBitmap() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            return filterGLThread.getTexture();
        }
        return null;
    }

    private void fixLayout(int viewWidth, int viewHeight) {
        float bitmapH;
        float bitmapW;
        float bitmapH2;
        float bitmapW2;
        int viewWidth2 = viewWidth - AndroidUtilities.dp(28.0f);
        int viewHeight2 = viewHeight - (AndroidUtilities.dp(214.0f) + ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight));
        Bitmap bitmap = this.bitmapToEdit;
        if (bitmap != null) {
            int i = this.orientation;
            if (i % 360 == 90 || i % 360 == 270) {
                bitmapW = bitmap.getHeight();
                bitmapH = this.bitmapToEdit.getWidth();
            } else {
                bitmapW = bitmap.getWidth();
                bitmapH = this.bitmapToEdit.getHeight();
            }
        } else {
            bitmapW = this.textureView.getWidth();
            bitmapH = this.textureView.getHeight();
        }
        float scaleX = viewWidth2 / bitmapW;
        float scaleY = viewHeight2 / bitmapH;
        if (scaleX > scaleY) {
            bitmapH2 = viewHeight2;
            bitmapW2 = (int) Math.ceil(bitmapW * scaleY);
        } else {
            bitmapW2 = viewWidth2;
            bitmapH2 = (int) Math.ceil(bitmapH * scaleX);
        }
        int bitmapX = (int) Math.ceil(((viewWidth2 - bitmapW2) / 2.0f) + AndroidUtilities.dp(14.0f));
        int bitmapY = (int) Math.ceil(((viewHeight2 - bitmapH2) / 2.0f) + AndroidUtilities.dp(14.0f) + ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight));
        int width = (int) bitmapW2;
        int height = (int) bitmapH2;
        if (this.ownsTextureView) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textureView.getLayoutParams();
            layoutParams.leftMargin = bitmapX;
            layoutParams.topMargin = bitmapY;
            layoutParams.width = width;
            layoutParams.height = height;
        }
        this.curvesControl.setActualArea(bitmapX, bitmapY - ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight), width, height);
        this.blurControl.setActualAreaSize(width, height);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.blurControl.getLayoutParams();
        layoutParams2.height = AndroidUtilities.dp(38.0f) + viewHeight2;
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.curvesControl.getLayoutParams();
        layoutParams3.height = AndroidUtilities.dp(28.0f) + viewHeight2;
        if (AndroidUtilities.isTablet()) {
            int total = AndroidUtilities.dp(86.0f) * 10;
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams();
            if (total < viewWidth2) {
                layoutParams4.width = total;
                layoutParams4.leftMargin = (viewWidth2 - total) / 2;
                return;
            }
            layoutParams4.width = -1;
            layoutParams4.leftMargin = 0;
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (this.paintingOverlay != null && child == this.textureView) {
            canvas.save();
            canvas.translate(this.textureView.getLeft(), this.textureView.getTop());
            float scale = this.textureView.getMeasuredWidth() / this.paintingOverlay.getMeasuredWidth();
            canvas.scale(scale, scale);
            this.paintingOverlay.draw(canvas);
            canvas.restore();
        }
        return result;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        fixLayout(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getShadowsValue() {
        return ((this.shadowsValue * 0.55f) + 100.0f) / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getHighlightsValue() {
        return ((this.highlightsValue * 0.75f) + 100.0f) / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getEnhanceValue() {
        return this.enhanceValue / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getExposureValue() {
        return this.exposureValue / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getContrastValue() {
        return ((this.contrastValue / 100.0f) * 0.3f) + 1.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getWarmthValue() {
        return this.warmthValue / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getVignetteValue() {
        return this.vignetteValue / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getSharpenValue() {
        return ((this.sharpenValue / 100.0f) * 0.6f) + 0.11f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getGrainValue() {
        return (this.grainValue / 100.0f) * 0.04f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getFadeValue() {
        return this.fadeValue / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getSoftenSkinValue() {
        return this.softenSkinValue / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getTintHighlightsIntensityValue() {
        if (this.tintHighlightsColor == 0) {
            return 0.0f;
        }
        return 50.0f / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getTintShadowsIntensityValue() {
        if (this.tintShadowsColor == 0) {
            return 0.0f;
        }
        return 50.0f / 100.0f;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getSaturationValue() {
        float parameterValue = this.saturationValue / 100.0f;
        if (parameterValue > 0.0f) {
            parameterValue *= 1.05f;
        }
        return 1.0f + parameterValue;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public int getTintHighlightsColor() {
        return this.tintHighlightsColor;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public int getTintShadowsColor() {
        return this.tintShadowsColor;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public int getBlurType() {
        return this.blurType;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getBlurExcludeSize() {
        return this.blurExcludeSize;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getBlurExcludeBlurSize() {
        return this.blurExcludeBlurSize;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public float getBlurAngle() {
        return this.blurAngle;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public Point getBlurExcludePoint() {
        return this.blurExcludePoint;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public boolean shouldShowOriginal() {
        return this.showOriginal;
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public boolean shouldDrawCurvesPass() {
        return !this.curvesToolValue.shouldBeSkipped();
    }

    @Override // org.telegram.ui.Components.FilterShaders.FilterShadersDelegate
    public ByteBuffer fillAndGetCurveBuffer() {
        this.curvesToolValue.fillBuffer();
        return this.curvesToolValue.curveBuffer;
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public View getCurveControl() {
        return this.curvesControl;
    }

    public View getBlurControl() {
        return this.blurControl;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* loaded from: classes5.dex */
    public class ToolsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ToolsAdapter(Context context) {
            PhotoFilterView.this = this$0;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PhotoFilterView.this.rowsCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                PhotoEditToolCell cell = new PhotoEditToolCell(this.mContext, PhotoFilterView.this.resourcesProvider);
                view = cell;
                cell.setSeekBarDelegate(new PhotoEditorSeekBar.PhotoEditorSeekBarDelegate() { // from class: org.telegram.ui.Components.PhotoFilterView$ToolsAdapter$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.Components.PhotoEditorSeekBar.PhotoEditorSeekBarDelegate
                    public final void onProgressChanged(int i2, int i3) {
                        PhotoFilterView.ToolsAdapter.this.m2825x4dda4bfb(i2, i3);
                    }
                });
            } else {
                view = new PhotoEditRadioCell(this.mContext);
                view.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoFilterView$ToolsAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        PhotoFilterView.ToolsAdapter.this.m2826x736e54fc(view2);
                    }
                });
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-PhotoFilterView$ToolsAdapter */
        public /* synthetic */ void m2825x4dda4bfb(int i1, int progress) {
            if (i1 == PhotoFilterView.this.enhanceTool) {
                PhotoFilterView.this.enhanceValue = progress;
            } else if (i1 == PhotoFilterView.this.highlightsTool) {
                PhotoFilterView.this.highlightsValue = progress;
            } else if (i1 == PhotoFilterView.this.contrastTool) {
                PhotoFilterView.this.contrastValue = progress;
            } else if (i1 == PhotoFilterView.this.exposureTool) {
                PhotoFilterView.this.exposureValue = progress;
            } else if (i1 == PhotoFilterView.this.warmthTool) {
                PhotoFilterView.this.warmthValue = progress;
            } else if (i1 == PhotoFilterView.this.saturationTool) {
                PhotoFilterView.this.saturationValue = progress;
            } else if (i1 == PhotoFilterView.this.vignetteTool) {
                PhotoFilterView.this.vignetteValue = progress;
            } else if (i1 == PhotoFilterView.this.shadowsTool) {
                PhotoFilterView.this.shadowsValue = progress;
            } else if (i1 == PhotoFilterView.this.grainTool) {
                PhotoFilterView.this.grainValue = progress;
            } else if (i1 == PhotoFilterView.this.sharpenTool) {
                PhotoFilterView.this.sharpenValue = progress;
            } else if (i1 == PhotoFilterView.this.fadeTool) {
                PhotoFilterView.this.fadeValue = progress;
            } else if (i1 == PhotoFilterView.this.softenSkinTool) {
                PhotoFilterView.this.softenSkinValue = progress;
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(true);
            }
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Components-PhotoFilterView$ToolsAdapter */
        public /* synthetic */ void m2826x736e54fc(View v) {
            PhotoEditRadioCell cell = (PhotoEditRadioCell) v;
            Integer row = (Integer) cell.getTag();
            if (row.intValue() == PhotoFilterView.this.tintShadowsTool) {
                PhotoFilterView.this.tintShadowsColor = cell.getCurrentColor();
            } else {
                PhotoFilterView.this.tintHighlightsColor = cell.getCurrentColor();
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
            switch (holder.getItemViewType()) {
                case 0:
                    PhotoEditToolCell cell = (PhotoEditToolCell) holder.itemView;
                    cell.setTag(Integer.valueOf(i));
                    if (i == PhotoFilterView.this.enhanceTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Enhance", R.string.Enhance), PhotoFilterView.this.enhanceValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.highlightsTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Highlights", R.string.Highlights), PhotoFilterView.this.highlightsValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.contrastTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString(ExifInterface.TAG_CONTRAST, R.string.Contrast), PhotoFilterView.this.contrastValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.exposureTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Exposure", R.string.Exposure), PhotoFilterView.this.exposureValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.warmthTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Warmth", R.string.Warmth), PhotoFilterView.this.warmthValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.saturationTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString(ExifInterface.TAG_SATURATION, R.string.Saturation), PhotoFilterView.this.saturationValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.vignetteTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Vignette", R.string.Vignette), PhotoFilterView.this.vignetteValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.shadowsTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Shadows", R.string.Shadows), PhotoFilterView.this.shadowsValue, -100, 100);
                        return;
                    } else if (i == PhotoFilterView.this.grainTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Grain", R.string.Grain), PhotoFilterView.this.grainValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.sharpenTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Sharpen", R.string.Sharpen), PhotoFilterView.this.sharpenValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.fadeTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("Fade", R.string.Fade), PhotoFilterView.this.fadeValue, 0, 100);
                        return;
                    } else if (i == PhotoFilterView.this.softenSkinTool) {
                        cell.setIconAndTextAndValue(LocaleController.getString("SoftenSkin", R.string.SoftenSkin), PhotoFilterView.this.softenSkinValue, 0, 100);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    PhotoEditRadioCell cell2 = (PhotoEditRadioCell) holder.itemView;
                    cell2.setTag(Integer.valueOf(i));
                    if (i == PhotoFilterView.this.tintShadowsTool) {
                        cell2.setIconAndTextAndValue(LocaleController.getString("TintShadows", R.string.TintShadows), 0, PhotoFilterView.this.tintShadowsColor);
                        return;
                    } else if (i == PhotoFilterView.this.tintHighlightsTool) {
                        cell2.setIconAndTextAndValue(LocaleController.getString("TintHighlights", R.string.TintHighlights), 0, PhotoFilterView.this.tintHighlightsColor);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == PhotoFilterView.this.tintShadowsTool || position == PhotoFilterView.this.tintHighlightsTool) {
                return 1;
            }
            return 0;
        }
    }
}
