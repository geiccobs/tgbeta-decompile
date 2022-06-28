package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.exoplayer2.C;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.Size;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoAlbumPickerActivity;
/* loaded from: classes4.dex */
public class CameraScanActivity extends BaseFragment {
    public static final int TYPE_MRZ = 0;
    public static final int TYPE_QR = 1;
    public static final int TYPE_QR_LOGIN = 2;
    private CameraView cameraView;
    private int currentType;
    private CameraScanActivityDelegate delegate;
    private TextView descriptionText;
    private AnimatorSet flashAnimator;
    private ImageView flashButton;
    private ImageView galleryButton;
    private Handler handler;
    private boolean needGalleryButton;
    private RectF normalBounds;
    private boolean recognized;
    private ValueAnimator recognizedAnimator;
    private TextView recognizedMrzView;
    private long recognizedStart;
    private String recognizedText;
    private int sps;
    private TextView titleTextView;
    private SpringAnimation useRecognizedBoundsAnimator;
    private HandlerThread backgroundHandlerThread = new HandlerThread("ScanCamera");
    private Paint paint = new Paint();
    private Paint cornerPaint = new Paint(1);
    private Path path = new Path();
    private float backShadowAlpha = 0.5f;
    protected boolean shownAsBottomSheet = false;
    private SpringAnimation qrAppearing = null;
    private float qrAppearingValue = 0.0f;
    private RectF fromBounds = new RectF();
    private RectF bounds = new RectF();
    private long lastBoundsUpdate = 0;
    private final long boundsUpdateDuration = 75;
    private int recognizeFailed = 0;
    private int recognizeIndex = 0;
    private boolean qrLoading = false;
    private boolean qrLoaded = false;
    private QRCodeReader qrReader = null;
    private BarcodeDetector visionQrReader = null;
    private float recognizedT = 0.0f;
    private float useRecognizedBounds = 0.0f;
    private Runnable requestShot = new AnonymousClass7();
    private float averageProcessTime = 0.0f;
    private long processTimesCount = 0;

    /* loaded from: classes4.dex */
    public interface CameraScanActivityDelegate {
        void didFindMrzInfo(MrzRecognizer.Result result);

        void didFindQr(String str);

        boolean processQr(String str, Runnable runnable);

        /* renamed from: org.telegram.ui.CameraScanActivity$CameraScanActivityDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$didFindMrzInfo(CameraScanActivityDelegate _this, MrzRecognizer.Result result) {
            }

            public static void $default$didFindQr(CameraScanActivityDelegate _this, String text) {
            }

            public static boolean $default$processQr(CameraScanActivityDelegate _this, String text, Runnable onLoadEnd) {
                return false;
            }
        }
    }

    public static ActionBarLayout[] showAsSheet(BaseFragment parentFragment, boolean gallery, int type, CameraScanActivityDelegate cameraDelegate) {
        if (parentFragment == null || parentFragment.getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayout = {new ActionBarLayout(parentFragment.getParentActivity())};
        BottomSheet bottomSheet = new AnonymousClass1(parentFragment.getParentActivity(), false, actionBarLayout, type, gallery, cameraDelegate);
        bottomSheet.setUseLightStatusBar(false);
        AndroidUtilities.setLightNavigationBar(bottomSheet.getWindow(), false);
        AndroidUtilities.setNavigationBarColor(bottomSheet.getWindow(), -16777216, false);
        bottomSheet.setUseLightStatusBar(false);
        bottomSheet.getWindow().addFlags(512);
        bottomSheet.show();
        return actionBarLayout;
    }

    /* renamed from: org.telegram.ui.CameraScanActivity$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends BottomSheet {
        CameraScanActivity fragment;
        final /* synthetic */ ActionBarLayout[] val$actionBarLayout;
        final /* synthetic */ CameraScanActivityDelegate val$cameraDelegate;
        final /* synthetic */ boolean val$gallery;
        final /* synthetic */ int val$type;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Context context, boolean needFocus, ActionBarLayout[] actionBarLayoutArr, int i, boolean z, CameraScanActivityDelegate cameraScanActivityDelegate) {
            super(context, needFocus);
            this.val$actionBarLayout = actionBarLayoutArr;
            this.val$type = i;
            this.val$gallery = z;
            this.val$cameraDelegate = cameraScanActivityDelegate;
            actionBarLayoutArr[0].init(new ArrayList<>());
            CameraScanActivity cameraScanActivity = new CameraScanActivity(i) { // from class: org.telegram.ui.CameraScanActivity.1.1
                {
                    AnonymousClass1.this = this;
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public void finishFragment() {
                    AnonymousClass1.this.dismiss();
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public void removeSelfFromStack() {
                    AnonymousClass1.this.dismiss();
                }
            };
            this.fragment = cameraScanActivity;
            cameraScanActivity.shownAsBottomSheet = true;
            this.fragment.needGalleryButton = z;
            actionBarLayoutArr[0].addFragmentToStack(this.fragment);
            actionBarLayoutArr[0].showLastFragment();
            actionBarLayoutArr[0].setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
            this.fragment.setDelegate(cameraScanActivityDelegate);
            this.containerView = actionBarLayoutArr[0];
            setApplyBottomPadding(false);
            setApplyBottomPadding(false);
            setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.CameraScanActivity$1$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    CameraScanActivity.AnonymousClass1.this.m1625lambda$new$0$orgtelegramuiCameraScanActivity$1(dialogInterface);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CameraScanActivity$1 */
        public /* synthetic */ void m1625lambda$new$0$orgtelegramuiCameraScanActivity$1(DialogInterface dialog) {
            this.fragment.onFragmentDestroy();
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet
        protected boolean canDismissWithSwipe() {
            return false;
        }

        @Override // android.app.Dialog
        public void onBackPressed() {
            ActionBarLayout[] actionBarLayoutArr = this.val$actionBarLayout;
            if (actionBarLayoutArr[0] == null || actionBarLayoutArr[0].fragmentsStack.size() <= 1) {
                super.onBackPressed();
            } else {
                this.val$actionBarLayout[0].onBackPressed();
            }
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
        public void dismiss() {
            super.dismiss();
            this.val$actionBarLayout[0] = null;
        }
    }

    public CameraScanActivity(int type) {
        this.currentType = type;
        if (isQr()) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    CameraScanActivity.this.m1615lambda$new$0$orgtelegramuiCameraScanActivity();
                }
            });
        }
        switch (SharedConfig.getDevicePerformanceClass()) {
            case 0:
                this.sps = 8;
                return;
            case 1:
                this.sps = 24;
                return;
            default:
                this.sps = 40;
                return;
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1615lambda$new$0$orgtelegramuiCameraScanActivity() {
        this.qrReader = new QRCodeReader();
        this.visionQrReader = new BarcodeDetector.Builder(ApplicationLoader.applicationContext).setBarcodeFormats(256).build();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        destroy(false, null);
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(-1);
        }
        BarcodeDetector barcodeDetector = this.visionQrReader;
        if (barcodeDetector != null) {
            barcodeDetector.release();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (this.shownAsBottomSheet) {
            this.actionBar.setItemsColor(-1, false);
            this.actionBar.setItemsBackgroundColor(-1, false);
            this.actionBar.setTitleColor(-1);
        } else {
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        }
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet() && !isQr()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.CameraScanActivity.2
            {
                CameraScanActivity.this = this;
            }

            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    CameraScanActivity.this.finishFragment();
                }
            }
        });
        this.paint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.cornerPaint.setColor(-1);
        this.cornerPaint.setStyle(Paint.Style.FILL);
        ViewGroup viewGroup = new ViewGroup(context) { // from class: org.telegram.ui.CameraScanActivity.3
            {
                CameraScanActivity.this = this;
            }

            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                CameraScanActivity.this.actionBar.measure(widthMeasureSpec, heightMeasureSpec);
                if (CameraScanActivity.this.currentType == 0) {
                    if (CameraScanActivity.this.cameraView != null) {
                        CameraScanActivity.this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (width * 0.704f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                } else {
                    if (CameraScanActivity.this.cameraView != null) {
                        CameraScanActivity.this.cameraView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                    }
                    CameraScanActivity.this.recognizedMrzView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                    if (CameraScanActivity.this.galleryButton != null) {
                        CameraScanActivity.this.galleryButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                    CameraScanActivity.this.flashButton.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), C.BUFFER_FLAG_ENCRYPTED));
                }
                CameraScanActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(72.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                CameraScanActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (width * 0.9f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, 0));
                setMeasuredDimension(width, height);
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int x;
                int width = r - l;
                int height = b - t;
                if (CameraScanActivity.this.currentType == 0) {
                    if (CameraScanActivity.this.cameraView != null) {
                        CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight() + 0);
                    }
                    CameraScanActivity.this.recognizedMrzView.setTextSize(0, height / 22);
                    CameraScanActivity.this.recognizedMrzView.setPadding(0, 0, 0, height / 15);
                    int y = (int) (height * 0.65f);
                    CameraScanActivity.this.titleTextView.layout(AndroidUtilities.dp(36.0f), y, AndroidUtilities.dp(36.0f) + CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + y);
                } else {
                    CameraScanActivity.this.actionBar.layout(0, 0, CameraScanActivity.this.actionBar.getMeasuredWidth(), CameraScanActivity.this.actionBar.getMeasuredHeight());
                    if (CameraScanActivity.this.cameraView != null) {
                        CameraScanActivity.this.cameraView.layout(0, 0, CameraScanActivity.this.cameraView.getMeasuredWidth(), CameraScanActivity.this.cameraView.getMeasuredHeight());
                    }
                    int size = (int) (Math.min(width, height) / 1.5f);
                    int y2 = CameraScanActivity.this.currentType == 1 ? (((height - size) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight()) - AndroidUtilities.dp(30.0f) : (((height - size) / 2) - CameraScanActivity.this.titleTextView.getMeasuredHeight()) - AndroidUtilities.dp(64.0f);
                    CameraScanActivity.this.titleTextView.layout(AndroidUtilities.dp(36.0f), y2, AndroidUtilities.dp(36.0f) + CameraScanActivity.this.titleTextView.getMeasuredWidth(), CameraScanActivity.this.titleTextView.getMeasuredHeight() + y2);
                    CameraScanActivity.this.recognizedMrzView.layout(0, getMeasuredHeight() - CameraScanActivity.this.recognizedMrzView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                    if (!CameraScanActivity.this.needGalleryButton) {
                        x = (width / 2) - (CameraScanActivity.this.flashButton.getMeasuredWidth() / 2);
                    } else {
                        x = (width / 2) + AndroidUtilities.dp(35.0f);
                    }
                    int y3 = ((height - size) / 2) + size + AndroidUtilities.dp(80.0f);
                    CameraScanActivity.this.flashButton.layout(x, y3, CameraScanActivity.this.flashButton.getMeasuredWidth() + x, CameraScanActivity.this.flashButton.getMeasuredHeight() + y3);
                    if (CameraScanActivity.this.galleryButton != null) {
                        int x2 = ((width / 2) - AndroidUtilities.dp(35.0f)) - CameraScanActivity.this.galleryButton.getMeasuredWidth();
                        CameraScanActivity.this.galleryButton.layout(x2, y3, CameraScanActivity.this.galleryButton.getMeasuredWidth() + x2, CameraScanActivity.this.galleryButton.getMeasuredHeight() + y3);
                    }
                }
                int y4 = (int) (height * 0.74f);
                int x3 = (int) (width * 0.05f);
                CameraScanActivity.this.descriptionText.layout(x3, y4, CameraScanActivity.this.descriptionText.getMeasuredWidth() + x3, CameraScanActivity.this.descriptionText.getMeasuredHeight() + y4);
                CameraScanActivity.this.updateNormalBounds();
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (CameraScanActivity.this.isQr() && child == CameraScanActivity.this.cameraView) {
                    RectF bounds = CameraScanActivity.this.getBounds();
                    int cx = (int) (child.getWidth() * bounds.centerX());
                    int cy = (int) (child.getHeight() * bounds.centerY());
                    int sizex = (int) (((int) (child.getWidth() * bounds.width())) * ((CameraScanActivity.this.qrAppearingValue * 0.5f) + 0.5f));
                    int sizey = (int) (((int) (child.getHeight() * bounds.height())) * ((CameraScanActivity.this.qrAppearingValue * 0.5f) + 0.5f));
                    int x = cx - (sizex / 2);
                    int y = cy - (sizey / 2);
                    CameraScanActivity.this.paint.setAlpha((int) ((1.0f - ((1.0f - CameraScanActivity.this.backShadowAlpha) * Math.min(1.0f, CameraScanActivity.this.qrAppearingValue))) * 255.0f));
                    canvas.drawRect(0.0f, 0.0f, child.getMeasuredWidth(), y, CameraScanActivity.this.paint);
                    canvas.drawRect(0.0f, y + sizey, child.getMeasuredWidth(), child.getMeasuredHeight(), CameraScanActivity.this.paint);
                    canvas.drawRect(0.0f, y, x, y + sizey, CameraScanActivity.this.paint);
                    canvas.drawRect(x + sizex, y, child.getMeasuredWidth(), y + sizey, CameraScanActivity.this.paint);
                    CameraScanActivity.this.paint.setAlpha((int) (Math.max(0.0f, 1.0f - CameraScanActivity.this.qrAppearingValue) * 255.0f));
                    canvas.drawRect(x, y, x + sizex, y + sizey, CameraScanActivity.this.paint);
                    int lineWidth = AndroidUtilities.lerp(0, AndroidUtilities.dp(4.0f), Math.min(1.0f, CameraScanActivity.this.qrAppearingValue * 20.0f));
                    int halfLineWidth = lineWidth / 2;
                    int lineLength = AndroidUtilities.lerp(Math.min(sizex, sizey), AndroidUtilities.dp(20.0f), Math.min(1.2f, (float) Math.pow(CameraScanActivity.this.qrAppearingValue, 1.7999999523162842d)));
                    CameraScanActivity.this.cornerPaint.setAlpha((int) (Math.min(1.0f, CameraScanActivity.this.qrAppearingValue) * 255.0f));
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.arcTo(aroundPoint(x, y + lineLength, halfLineWidth), 0.0f, 180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (x + (lineWidth * 1.5f)), (int) (y + (lineWidth * 1.5f)), lineWidth * 2), 180.0f, 90.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint(x + lineLength, y, halfLineWidth), 270.0f, 180.0f);
                    CameraScanActivity.this.path.lineTo(x + halfLineWidth, y + halfLineWidth);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (x + (lineWidth * 1.5f)), (int) (y + (lineWidth * 1.5f)), lineWidth), 270.0f, -90.0f);
                    CameraScanActivity.this.path.close();
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.arcTo(aroundPoint(x + sizex, y + lineLength, halfLineWidth), 180.0f, -180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) ((x + sizex) - (lineWidth * 1.5f)), (int) (y + (lineWidth * 1.5f)), lineWidth * 2), 0.0f, -90.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((x + sizex) - lineLength, y, halfLineWidth), 270.0f, -180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) ((x + sizex) - (lineWidth * 1.5f)), (int) (y + (lineWidth * 1.5f)), lineWidth), 270.0f, 90.0f);
                    CameraScanActivity.this.path.close();
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.arcTo(aroundPoint(x, (y + sizey) - lineLength, halfLineWidth), 0.0f, -180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (x + (lineWidth * 1.5f)), (int) ((y + sizey) - (lineWidth * 1.5f)), lineWidth * 2), 180.0f, -90.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint(x + lineLength, y + sizey, halfLineWidth), 90.0f, -180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) (x + (lineWidth * 1.5f)), (int) ((y + sizey) - (lineWidth * 1.5f)), lineWidth), 90.0f, 90.0f);
                    CameraScanActivity.this.path.close();
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                    CameraScanActivity.this.path.reset();
                    CameraScanActivity.this.path.arcTo(aroundPoint(x + sizex, (y + sizey) - lineLength, halfLineWidth), 180.0f, 180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) ((x + sizex) - (lineWidth * 1.5f)), (int) ((y + sizey) - (lineWidth * 1.5f)), lineWidth * 2), 0.0f, 90.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((x + sizex) - lineLength, y + sizey, halfLineWidth), 90.0f, 180.0f);
                    CameraScanActivity.this.path.arcTo(aroundPoint((int) ((x + sizex) - (lineWidth * 1.5f)), (int) ((y + sizey) - (lineWidth * 1.5f)), lineWidth), 90.0f, -90.0f);
                    CameraScanActivity.this.path.close();
                    canvas.drawPath(CameraScanActivity.this.path, CameraScanActivity.this.cornerPaint);
                }
                return result;
            }

            private RectF aroundPoint(int x, int y, int r) {
                AndroidUtilities.rectTmp.set(x - r, y - r, x + r, y + r);
                return AndroidUtilities.rectTmp;
            }
        };
        viewGroup.setOnTouchListener(CameraScanActivity$$ExternalSyntheticLambda14.INSTANCE);
        this.fragmentView = viewGroup;
        int i = this.currentType;
        if (i == 1 || i == 2) {
            this.fragmentView.postDelayed(new Runnable() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    CameraScanActivity.this.initCameraView();
                }
            }, 450L);
        } else {
            initCameraView();
        }
        if (this.currentType == 0) {
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        } else {
            this.actionBar.setBackgroundDrawable(null);
            this.actionBar.setAddToContainer(false);
            this.actionBar.setTitleColor(-1);
            this.actionBar.setItemsColor(-1, false);
            this.actionBar.setItemsBackgroundColor(587202559, false);
            viewGroup.setBackgroundColor(Theme.getColor(Theme.key_wallet_blackBackground));
            viewGroup.addView(this.actionBar);
        }
        if (this.currentType == 2) {
            this.actionBar.setTitle(LocaleController.getString("AuthAnotherClientScan", R.string.AuthAnotherClientScan));
        }
        final Paint selectionPaint = new Paint(1);
        selectionPaint.setPathEffect(LinkPath.getRoundedEffect());
        selectionPaint.setColor(ColorUtils.setAlphaComponent(-1, 40));
        TextView textView = new TextView(context) { // from class: org.telegram.ui.CameraScanActivity.4
            LinkSpanDrawable.LinkCollector links = new LinkSpanDrawable.LinkCollector(this);
            private LinkSpanDrawable<URLSpanNoUnderline> pressedLink;
            LinkPath textPath;

            {
                CameraScanActivity.this = this;
            }

            @Override // android.widget.TextView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (getText() instanceof Spanned) {
                    Spanned spanned = (Spanned) getText();
                    URLSpanNoUnderline[] innerSpans = (URLSpanNoUnderline[]) spanned.getSpans(0, spanned.length(), URLSpanNoUnderline.class);
                    if (innerSpans != null && innerSpans.length > 0) {
                        LinkPath linkPath = new LinkPath(true);
                        this.textPath = linkPath;
                        linkPath.setAllowReset(false);
                        for (int a = 0; a < innerSpans.length; a++) {
                            int start = spanned.getSpanStart(innerSpans[a]);
                            int end = spanned.getSpanEnd(innerSpans[a]);
                            this.textPath.setCurrentLayout(getLayout(), start, 0.0f);
                            int shift = getText() != null ? getPaint().baselineShift : 0;
                            this.textPath.setBaselineShift(shift != 0 ? AndroidUtilities.dp(shift > 0 ? 5.0f : -2.0f) + shift : 0);
                            getLayout().getSelectionPath(start, end, this.textPath);
                        }
                        this.textPath.setAllowReset(true);
                    }
                }
            }

            @Override // android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                Layout textLayout = getLayout();
                int x = (int) (e.getX() - 0);
                int y = (int) (e.getY() - 0);
                if (e.getAction() == 0 || e.getAction() == 1) {
                    int line = textLayout.getLineForVertical(y);
                    int off = textLayout.getOffsetForHorizontal(line, x);
                    float left = textLayout.getLineLeft(line);
                    if (left <= x && textLayout.getLineWidth(line) + left >= x && y >= 0 && y <= textLayout.getHeight()) {
                        Spannable buffer = (Spannable) textLayout.getText();
                        ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                        if (link.length != 0) {
                            this.links.clear();
                            if (e.getAction() == 0) {
                                LinkSpanDrawable<URLSpanNoUnderline> linkSpanDrawable = new LinkSpanDrawable<>(link[0], null, e.getX(), e.getY());
                                this.pressedLink = linkSpanDrawable;
                                linkSpanDrawable.setColor(771751935);
                                this.links.addLink(this.pressedLink);
                                int start = buffer.getSpanStart(this.pressedLink.getSpan());
                                int end = buffer.getSpanEnd(this.pressedLink.getSpan());
                                LinkPath path = this.pressedLink.obtainNewPath();
                                path.setCurrentLayout(textLayout, start, 0);
                                textLayout.getSelectionPath(start, end, path);
                                return true;
                            } else if (e.getAction() == 1) {
                                LinkSpanDrawable<URLSpanNoUnderline> linkSpanDrawable2 = this.pressedLink;
                                if (linkSpanDrawable2 != null && linkSpanDrawable2.getSpan() == link[0]) {
                                    link[0].onClick(this);
                                }
                                this.pressedLink = null;
                                return true;
                            } else {
                                return true;
                            }
                        }
                    }
                }
                if (e.getAction() == 1 || e.getAction() == 3) {
                    this.links.clear();
                    this.pressedLink = null;
                }
                return super.onTouchEvent(e);
            }

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                LinkPath linkPath = this.textPath;
                if (linkPath != null) {
                    canvas.drawPath(linkPath, selectionPaint);
                }
                if (this.links.draw(canvas)) {
                    invalidate();
                }
                super.onDraw(canvas);
            }
        };
        this.titleTextView = textView;
        textView.setGravity(1);
        this.titleTextView.setTextSize(1, 24.0f);
        viewGroup.addView(this.titleTextView);
        TextView textView2 = new TextView(context);
        this.descriptionText = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        viewGroup.addView(this.descriptionText);
        TextView textView3 = new TextView(context);
        this.recognizedMrzView = textView3;
        textView3.setTextColor(-1);
        this.recognizedMrzView.setGravity(81);
        this.recognizedMrzView.setAlpha(0.0f);
        int i2 = this.currentType;
        if (i2 == 0) {
            this.titleTextView.setText(LocaleController.getString("PassportScanPassport", R.string.PassportScanPassport));
            this.descriptionText.setText(LocaleController.getString("PassportScanPassportInfo", R.string.PassportScanPassportInfo));
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.recognizedMrzView.setTypeface(Typeface.MONOSPACE);
        } else {
            if (!this.needGalleryButton) {
                if (i2 == 1) {
                    this.titleTextView.setText(LocaleController.getString("AuthAnotherClientScan", R.string.AuthAnotherClientScan));
                } else {
                    SpannableStringBuilder spanned = new SpannableStringBuilder(LocaleController.getString("AuthAnotherClientInfo5", R.string.AuthAnotherClientInfo5));
                    String[] links = {LocaleController.getString("AuthAnotherClientDownloadClientUrl", R.string.AuthAnotherClientDownloadClientUrl), LocaleController.getString("AuthAnotherWebClientUrl", R.string.AuthAnotherWebClientUrl)};
                    for (String str : links) {
                        String text = spanned.toString();
                        int index1 = text.indexOf(42);
                        int index2 = text.indexOf(42, index1 + 1);
                        if (index1 == -1 || index2 == -1 || index1 == index2) {
                            break;
                        }
                        this.titleTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spanned.replace(index2, index2 + 1, (CharSequence) " ");
                        spanned.replace(index1, index1 + 1, (CharSequence) " ");
                        int index12 = index1 + 1;
                        int index22 = index2 + 1;
                        spanned.setSpan(new URLSpanNoUnderline(str, true), index12, index22 - 1, 33);
                        spanned.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), index12, index22 - 1, 33);
                    }
                    this.titleTextView.setLinkTextColor(-1);
                    this.titleTextView.setTextSize(1, 16.0f);
                    this.titleTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                    this.titleTextView.setPadding(0, 0, 0, 0);
                    this.titleTextView.setText(spanned);
                }
            }
            this.titleTextView.setTextColor(-1);
            this.recognizedMrzView.setTextSize(1, 16.0f);
            this.recognizedMrzView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            if (!this.needGalleryButton) {
                this.recognizedMrzView.setText(LocaleController.getString("AuthAnotherClientNotFound", R.string.AuthAnotherClientNotFound));
            }
            viewGroup.addView(this.recognizedMrzView);
            if (this.needGalleryButton) {
                ImageView imageView = new ImageView(context);
                this.galleryButton = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.galleryButton.setImageResource(R.drawable.qr_gallery);
                this.galleryButton.setBackgroundDrawable(Theme.createSelectorDrawableFromDrawables(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), 587202559), Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), 1157627903)));
                viewGroup.addView(this.galleryButton);
                this.galleryButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda12
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        CameraScanActivity.this.m1609lambda$createView$2$orgtelegramuiCameraScanActivity(view);
                    }
                });
            }
            ImageView imageView2 = new ImageView(context);
            this.flashButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.flashButton.setImageResource(R.drawable.qr_flashlight);
            this.flashButton.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(60.0f), 587202559));
            viewGroup.addView(this.flashButton);
            this.flashButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda13
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CameraScanActivity.this.m1611lambda$createView$4$orgtelegramuiCameraScanActivity(view);
                }
            });
        }
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(1);
        }
        this.fragmentView.setKeepScreenOn(true);
        return this.fragmentView;
    }

    public static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1609lambda$createView$2$orgtelegramuiCameraScanActivity(View currentImage) {
        if (getParentActivity() == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
            return;
        }
        PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(PhotoAlbumPickerActivity.SELECT_TYPE_QR, false, false, null);
        fragment.setMaxSelectedPhotos(1, false);
        fragment.setAllowSearchImages(false);
        fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() { // from class: org.telegram.ui.CameraScanActivity.5
            {
                CameraScanActivity.this = this;
            }

            @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
            public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean notify, int scheduleDate) {
                Throwable e;
                try {
                    if (!arrayList.isEmpty()) {
                        try {
                            SendMessagesHelper.SendingMediaInfo info = arrayList.get(0);
                            if (info.path != null) {
                                Point screenSize = AndroidUtilities.getRealScreenSize();
                                Bitmap bitmap = ImageLoader.loadBitmap(info.path, null, screenSize.x, screenSize.y, true);
                                QrResult res = CameraScanActivity.this.tryReadQr(null, null, 0, 0, 0, bitmap);
                                if (res != null) {
                                    if (CameraScanActivity.this.delegate != null) {
                                        CameraScanActivity.this.delegate.didFindQr(res.text);
                                    }
                                    CameraScanActivity.this.removeSelfFromStack();
                                }
                            }
                        } catch (Throwable th) {
                            e = th;
                            FileLog.e(e);
                        }
                    }
                } catch (Throwable th2) {
                    e = th2;
                }
            }

            @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
            public void startPhotoSelectActivity() {
                try {
                    Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                    photoPickerIntent.setType("image/*");
                    CameraScanActivity.this.getParentActivity().startActivityForResult(photoPickerIntent, 11);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        });
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1611lambda$createView$4$orgtelegramuiCameraScanActivity(View currentImage) {
        CameraSession session;
        CameraView cameraView = this.cameraView;
        if (cameraView != null && (session = cameraView.getCameraSession()) != null) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) this.flashButton.getBackground();
            AnimatorSet animatorSet = this.flashAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.flashAnimator = null;
            }
            this.flashAnimator = new AnimatorSet();
            Property<ShapeDrawable, Integer> property = AnimationProperties.SHAPE_DRAWABLE_ALPHA;
            int[] iArr = new int[1];
            iArr[0] = this.flashButton.getTag() == null ? 68 : 34;
            ObjectAnimator animator = ObjectAnimator.ofInt(shapeDrawable, property, iArr);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    CameraScanActivity.this.m1610lambda$createView$3$orgtelegramuiCameraScanActivity(valueAnimator);
                }
            });
            this.flashAnimator.playTogether(animator);
            this.flashAnimator.setDuration(200L);
            this.flashAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.flashAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.CameraScanActivity.6
                {
                    CameraScanActivity.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CameraScanActivity.this.flashAnimator = null;
                }
            });
            this.flashAnimator.start();
            if (this.flashButton.getTag() == null) {
                this.flashButton.setTag(1);
                session.setTorchEnabled(true);
                return;
            }
            this.flashButton.setTag(null);
            session.setTorchEnabled(false);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1610lambda$createView$3$orgtelegramuiCameraScanActivity(ValueAnimator animation) {
        this.flashButton.invalidate();
    }

    public void updateRecognized() {
        ValueAnimator valueAnimator = this.recognizedAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float newRecognizedT = this.recognized ? 1.0f : 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.recognizedT, newRecognizedT);
        this.recognizedAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda11
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                CameraScanActivity.this.m1623lambda$updateRecognized$5$orgtelegramuiCameraScanActivity(valueAnimator2);
            }
        });
        this.recognizedAnimator.setDuration(Math.abs(this.recognizedT - newRecognizedT) * 300.0f);
        this.recognizedAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.recognizedAnimator.start();
        SpringAnimation springAnimation = this.useRecognizedBoundsAnimator;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder((this.recognized ? this.useRecognizedBounds : 1.0f - this.useRecognizedBounds) * 500.0f));
        this.useRecognizedBoundsAnimator = springAnimation2;
        springAnimation2.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda17
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                CameraScanActivity.this.m1624lambda$updateRecognized$6$orgtelegramuiCameraScanActivity(dynamicAnimation, f, f2);
            }
        });
        this.useRecognizedBoundsAnimator.setSpring(new SpringForce(500.0f));
        this.useRecognizedBoundsAnimator.getSpring().setDampingRatio(1.0f);
        this.useRecognizedBoundsAnimator.getSpring().setStiffness(500.0f);
        this.useRecognizedBoundsAnimator.start();
    }

    /* renamed from: lambda$updateRecognized$5$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1623lambda$updateRecognized$5$orgtelegramuiCameraScanActivity(ValueAnimator a) {
        float floatValue = ((Float) a.getAnimatedValue()).floatValue();
        this.recognizedT = floatValue;
        this.titleTextView.setAlpha(1.0f - floatValue);
        this.flashButton.setAlpha(1.0f - this.recognizedT);
        this.backShadowAlpha = (this.recognizedT * 0.25f) + 0.5f;
        this.fragmentView.invalidate();
    }

    /* renamed from: lambda$updateRecognized$6$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1624lambda$updateRecognized$6$orgtelegramuiCameraScanActivity(DynamicAnimation animation, float value, float velocity) {
        this.useRecognizedBounds = this.recognized ? value / 500.0f : 1.0f - (value / 500.0f);
        this.fragmentView.invalidate();
    }

    public void initCameraView() {
        TextView textView;
        if (this.fragmentView == null) {
            return;
        }
        CameraController.getInstance().initCamera(null);
        CameraView cameraView = new CameraView(this.fragmentView.getContext(), false);
        this.cameraView = cameraView;
        cameraView.setUseMaxPreview(true);
        this.cameraView.setOptimizeForBarcode(true);
        this.cameraView.setDelegate(new CameraView.CameraViewDelegate() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.messenger.camera.CameraView.CameraViewDelegate
            public final void onCameraInit() {
                CameraScanActivity.this.m1614lambda$initCameraView$9$orgtelegramuiCameraScanActivity();
            }
        });
        ((ViewGroup) this.fragmentView).addView(this.cameraView, 0, LayoutHelper.createFrame(-1, -1.0f));
        if (this.currentType == 0 && (textView = this.recognizedMrzView) != null) {
            this.cameraView.addView(textView);
        }
    }

    /* renamed from: lambda$initCameraView$9$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1614lambda$initCameraView$9$orgtelegramuiCameraScanActivity() {
        startRecognizing();
        if (isQr()) {
            SpringAnimation springAnimation = this.qrAppearing;
            if (springAnimation != null) {
                springAnimation.cancel();
                this.qrAppearing = null;
            }
            SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder(0.0f));
            this.qrAppearing = springAnimation2;
            springAnimation2.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda16
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    CameraScanActivity.this.m1612lambda$initCameraView$7$orgtelegramuiCameraScanActivity(dynamicAnimation, f, f2);
                }
            });
            this.qrAppearing.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda15
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    CameraScanActivity.this.m1613lambda$initCameraView$8$orgtelegramuiCameraScanActivity(dynamicAnimation, z, f, f2);
                }
            });
            this.qrAppearing.setSpring(new SpringForce(500.0f));
            this.qrAppearing.getSpring().setDampingRatio(0.8f);
            this.qrAppearing.getSpring().setStiffness(250.0f);
            this.qrAppearing.start();
        }
    }

    /* renamed from: lambda$initCameraView$7$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1612lambda$initCameraView$7$orgtelegramuiCameraScanActivity(DynamicAnimation animation, float value, float velocity) {
        this.qrAppearingValue = value / 500.0f;
        this.fragmentView.invalidate();
    }

    /* renamed from: lambda$initCameraView$8$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1613lambda$initCameraView$8$orgtelegramuiCameraScanActivity(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        SpringAnimation springAnimation = this.qrAppearing;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.qrAppearing = null;
        }
    }

    private void updateRecognizedBounds(RectF newBounds) {
        long now = SystemClock.elapsedRealtime();
        long j = this.lastBoundsUpdate;
        if (j == 0) {
            this.lastBoundsUpdate = now - 75;
            this.bounds.set(newBounds);
            this.fromBounds.set(newBounds);
        } else {
            RectF rectF = this.fromBounds;
            if (rectF != null && now - j < 75) {
                float t = ((float) (now - j)) / 75.0f;
                float t2 = Math.min(1.0f, Math.max(0.0f, t));
                RectF rectF2 = this.fromBounds;
                AndroidUtilities.lerp(rectF2, this.bounds, t2, rectF2);
            } else {
                if (rectF == null) {
                    this.fromBounds = new RectF();
                }
                this.fromBounds.set(this.bounds);
            }
            this.bounds.set(newBounds);
            this.lastBoundsUpdate = now;
        }
        this.fragmentView.invalidate();
    }

    private RectF getRecognizedBounds() {
        if (this.fromBounds == null) {
            return this.bounds;
        }
        float t = Math.min(1.0f, Math.max(0.0f, ((float) (SystemClock.elapsedRealtime() - this.lastBoundsUpdate)) / 75.0f));
        if (t < 1.0f) {
            this.fragmentView.invalidate();
        }
        AndroidUtilities.lerp(this.fromBounds, this.bounds, t, AndroidUtilities.rectTmp);
        return AndroidUtilities.rectTmp;
    }

    public void updateNormalBounds() {
        if (this.normalBounds == null) {
            this.normalBounds = new RectF();
        }
        int width = Math.max(AndroidUtilities.displaySize.x, this.fragmentView.getWidth());
        int height = Math.max(AndroidUtilities.displaySize.y, this.fragmentView.getHeight());
        int side = (int) (Math.min(width, height) / 1.5f);
        this.normalBounds.set(((width - side) / 2.0f) / width, ((height - side) / 2.0f) / height, ((width + side) / 2.0f) / width, ((height + side) / 2.0f) / height);
    }

    public RectF getBounds() {
        RectF recognizedBounds = getRecognizedBounds();
        if (this.useRecognizedBounds < 1.0f) {
            if (this.normalBounds == null) {
                updateNormalBounds();
            }
            AndroidUtilities.lerp(this.normalBounds, recognizedBounds, this.useRecognizedBounds, recognizedBounds);
        }
        return recognizedBounds;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Throwable e;
        if (resultCode == -1) {
            if (requestCode == 11 && data != null && data.getData() != null) {
                try {
                    Point screenSize = AndroidUtilities.getRealScreenSize();
                    Bitmap bitmap = ImageLoader.loadBitmap(null, data.getData(), screenSize.x, screenSize.y, true);
                    QrResult res = tryReadQr(null, null, 0, 0, 0, bitmap);
                    if (res != null) {
                        try {
                            CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
                            if (cameraScanActivityDelegate != null) {
                                cameraScanActivityDelegate.didFindQr(res.text);
                            }
                            finishFragment();
                        } catch (Throwable th) {
                            e = th;
                            FileLog.e(e);
                        }
                    }
                } catch (Throwable th2) {
                    e = th2;
                }
            }
        }
    }

    public void setDelegate(CameraScanActivityDelegate cameraScanActivityDelegate) {
        this.delegate = cameraScanActivityDelegate;
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.destroy(async, beforeDestroyRunnable);
            this.cameraView = null;
        }
        this.backgroundHandlerThread.quitSafely();
    }

    /* renamed from: org.telegram.ui.CameraScanActivity$7 */
    /* loaded from: classes4.dex */
    public class AnonymousClass7 implements Runnable {
        AnonymousClass7() {
            CameraScanActivity.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (CameraScanActivity.this.cameraView != null && !CameraScanActivity.this.recognized && CameraScanActivity.this.cameraView.getCameraSession() != null) {
                CameraScanActivity.this.handler.post(new Runnable() { // from class: org.telegram.ui.CameraScanActivity$7$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraScanActivity.AnonymousClass7.this.m1626lambda$run$0$orgtelegramuiCameraScanActivity$7();
                    }
                });
            }
        }

        /* renamed from: lambda$run$0$org-telegram-ui-CameraScanActivity$7 */
        public /* synthetic */ void m1626lambda$run$0$orgtelegramuiCameraScanActivity$7() {
            if (CameraScanActivity.this.cameraView != null) {
                CameraScanActivity cameraScanActivity = CameraScanActivity.this;
                cameraScanActivity.processShot(cameraScanActivity.cameraView.getTextureView().getBitmap());
            }
        }
    }

    private void startRecognizing() {
        this.backgroundHandlerThread.start();
        this.handler = new Handler(this.backgroundHandlerThread.getLooper());
        AndroidUtilities.runOnUIThread(this.requestShot, 0L);
    }

    private void onNoQrFound() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CameraScanActivity.this.m1616lambda$onNoQrFound$10$orgtelegramuiCameraScanActivity();
            }
        });
    }

    /* renamed from: lambda$onNoQrFound$10$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1616lambda$onNoQrFound$10$orgtelegramuiCameraScanActivity() {
        if (this.recognizedMrzView.getTag() != null) {
            this.recognizedMrzView.setTag(null);
            this.recognizedMrzView.animate().setDuration(200L).alpha(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:72:0x017a  */
    /* JADX WARN: Removed duplicated region for block: B:77:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void processShot(android.graphics.Bitmap r17) {
        /*
            Method dump skipped, instructions count: 386
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CameraScanActivity.processShot(android.graphics.Bitmap):void");
    }

    /* renamed from: lambda$processShot$11$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1617lambda$processShot$11$orgtelegramuiCameraScanActivity(MrzRecognizer.Result res) {
        this.recognizedMrzView.setText(res.rawMRZ);
        this.recognizedMrzView.animate().setDuration(200L).alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindMrzInfo(res);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                CameraScanActivity.this.finishFragment();
            }
        }, 1200L);
    }

    /* renamed from: lambda$processShot$13$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1619lambda$processShot$13$orgtelegramuiCameraScanActivity() {
        CameraView cameraView = this.cameraView;
        if (cameraView != null && cameraView.getCameraSession() != null) {
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CameraScanActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                CameraScanActivity.this.m1618lambda$processShot$12$orgtelegramuiCameraScanActivity();
            }
        });
    }

    /* renamed from: lambda$processShot$12$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1618lambda$processShot$12$orgtelegramuiCameraScanActivity() {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(this.recognizedText);
        }
        finishFragment();
    }

    /* renamed from: lambda$processShot$14$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1620lambda$processShot$14$orgtelegramuiCameraScanActivity(QrResult res) {
        updateRecognizedBounds(res.bounds);
    }

    /* renamed from: lambda$processShot$15$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1621lambda$processShot$15$orgtelegramuiCameraScanActivity() {
        CameraScanActivityDelegate cameraScanActivityDelegate = this.delegate;
        if (cameraScanActivityDelegate != null) {
            cameraScanActivityDelegate.didFindQr(this.recognizedText);
        }
        finishFragment();
    }

    /* renamed from: lambda$processShot$16$org-telegram-ui-CameraScanActivity */
    public /* synthetic */ void m1622lambda$processShot$16$orgtelegramuiCameraScanActivity() {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            processShot(cameraView.getTextureView().getBitmap());
        }
    }

    /* loaded from: classes4.dex */
    public class QrResult {
        RectF bounds;
        String text;

        private QrResult() {
            CameraScanActivity.this = r1;
        }

        /* synthetic */ QrResult(CameraScanActivity x0, AnonymousClass1 x1) {
            this();
        }
    }

    public QrResult tryReadQr(byte[] data, Size size, int x, int y, int side, Bitmap bitmap) {
        String text;
        AnonymousClass1 anonymousClass1;
        LuminanceSource source;
        Frame frame;
        try {
            RectF bounds = new RectF();
            int width = 1;
            int height = 1;
            BarcodeDetector barcodeDetector = this.visionQrReader;
            if (barcodeDetector != null && barcodeDetector.isOperational()) {
                if (bitmap != null) {
                    frame = new Frame.Builder().setBitmap(bitmap).build();
                    int width2 = bitmap.getWidth();
                    width = width2;
                    height = bitmap.getHeight();
                } else {
                    frame = new Frame.Builder().setImageData(ByteBuffer.wrap(data), size.getWidth(), size.getHeight(), 17).build();
                    int width3 = size.getWidth();
                    width = width3;
                    height = size.getWidth();
                }
                SparseArray<Barcode> codes = this.visionQrReader.detect(frame);
                if (codes != null && codes.size() > 0) {
                    Barcode code = codes.valueAt(0);
                    text = code.rawValue;
                    if (code.cornerPoints != null && code.cornerPoints.length != 0) {
                        float minX = Float.MAX_VALUE;
                        float maxX = Float.MIN_VALUE;
                        float minY = Float.MAX_VALUE;
                        float maxY = Float.MIN_VALUE;
                        Point[] pointArr = code.cornerPoints;
                        int length = pointArr.length;
                        int i = 0;
                        while (i < length) {
                            Point point = pointArr[i];
                            minX = Math.min(minX, point.x);
                            maxX = Math.max(maxX, point.x);
                            minY = Math.min(minY, point.y);
                            maxY = Math.max(maxY, point.y);
                            i++;
                            codes = codes;
                            frame = frame;
                        }
                        bounds.set(minX, minY, maxX, maxY);
                    }
                    bounds = null;
                } else {
                    text = null;
                }
            } else if (this.qrReader != null) {
                if (bitmap != null) {
                    int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
                    bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                    source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
                    int width4 = bitmap.getWidth();
                    int height2 = bitmap.getWidth();
                    width = width4;
                    height = height2;
                } else {
                    source = new PlanarYUVLuminanceSource(data, size.getWidth(), size.getHeight(), x, y, side, side, false);
                    int width5 = size.getWidth();
                    int height3 = size.getHeight();
                    width = width5;
                    height = height3;
                }
                Result result = this.qrReader.decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
                if (result == null) {
                    onNoQrFound();
                    return null;
                }
                text = result.getText();
                if (result.getResultPoints() != null && result.getResultPoints().length != 0) {
                    float minX2 = Float.MAX_VALUE;
                    float maxX2 = Float.MIN_VALUE;
                    float minY2 = Float.MAX_VALUE;
                    float maxY2 = Float.MIN_VALUE;
                    ResultPoint[] resultPoints = result.getResultPoints();
                    int length2 = resultPoints.length;
                    int i2 = 0;
                    while (i2 < length2) {
                        ResultPoint point2 = resultPoints[i2];
                        minX2 = Math.min(minX2, point2.getX());
                        maxX2 = Math.max(maxX2, point2.getX());
                        minY2 = Math.min(minY2, point2.getY());
                        maxY2 = Math.max(maxY2, point2.getY());
                        i2++;
                        source = source;
                    }
                    bounds.set(minX2, minY2, maxX2, maxY2);
                }
                bounds = null;
            } else {
                text = null;
            }
            if (TextUtils.isEmpty(text)) {
                onNoQrFound();
                return null;
            }
            if (this.needGalleryButton) {
                if (!text.startsWith("ton://transfer/")) {
                    return null;
                }
                Uri uri = Uri.parse(text);
                uri.getPath().replace("/", "");
                anonymousClass1 = null;
            } else if (text.startsWith("tg://login?token=")) {
                anonymousClass1 = null;
            } else {
                onNoQrFound();
                return null;
            }
            QrResult qrResult = new QrResult(this, anonymousClass1);
            if (bounds != null) {
                int paddingx = AndroidUtilities.dp(25.0f);
                int paddingy = AndroidUtilities.dp(15.0f);
                bounds.set(bounds.left - paddingx, bounds.top - paddingy, bounds.right + paddingx, bounds.bottom + paddingy);
                bounds.set(bounds.left / width, bounds.top / height, bounds.right / width, bounds.bottom / height);
            }
            qrResult.bounds = bounds;
            qrResult.text = text;
            return qrResult;
        } catch (Throwable th) {
            onNoQrFound();
            return null;
        }
    }

    public boolean isQr() {
        int i = this.currentType;
        return i == 1 || i == 2;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        if (isQr()) {
            return themeDescriptions;
        }
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarWhiteSelector));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        return themeDescriptions;
    }
}
