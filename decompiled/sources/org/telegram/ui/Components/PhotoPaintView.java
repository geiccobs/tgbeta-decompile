package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.math.BigInteger;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.Paint.Brush;
import org.telegram.ui.Components.Paint.Painting;
import org.telegram.ui.Components.Paint.PhotoFace;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.UndoStore;
import org.telegram.ui.Components.Paint.Views.ColorPicker;
import org.telegram.ui.Components.Paint.Views.EntitiesContainerView;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.StickerView;
import org.telegram.ui.Components.Paint.Views.TextPaintView;
import org.telegram.ui.Components.StickerMasksAlert;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes5.dex */
public class PhotoPaintView extends FrameLayout implements EntityView.EntityViewDelegate {
    private static final int gallery_menu_done = 1;
    private FrameLayout backgroundView;
    private float baseScale;
    private Bitmap bitmapToEdit;
    private Swatch brushSwatch;
    private TextView cancelTextView;
    private org.telegram.ui.Components.Paint.Views.ColorPicker colorPicker;
    private Animator colorPickerAnimator;
    int currentBrush;
    private MediaController.CropState currentCropState;
    private EntityView currentEntityView;
    private FrameLayout curtainView;
    private FrameLayout dimView;
    private TextView doneTextView;
    private Point editedTextPosition;
    private float editedTextRotation;
    private float editedTextScale;
    private boolean editingText;
    private EntitiesContainerView entitiesView;
    private ArrayList<PhotoFace> faces;
    private Bitmap facesBitmap;
    private boolean ignoreLayout;
    private boolean inBubbleMode;
    private String initialText;
    private BigInteger lcm;
    private int originalBitmapRotation;
    private ImageView paintButton;
    private Size paintingSize;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private android.graphics.Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private RenderView renderView;
    private final Theme.ResourcesProvider resourcesProvider;
    private FrameLayout selectionContainerView;
    private FrameLayout textDimView;
    private FrameLayout toolsView;
    private float transformX;
    private float transformY;
    private UndoStore undoStore;
    private Brush[] brushes = {new Brush.Radial(), new Brush.Elliptical(), new Brush.Neon(), new Brush.Arrow()};
    private float[] temp = new float[2];
    private int selectedTextType = 2;
    private int[] pos = new int[2];
    private DispatchQueue queue = new DispatchQueue("Paint");

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public PhotoPaintView(Context context, Bitmap bitmap, Bitmap originalBitmap, int originalRotation, ArrayList<VideoEditedInfo.MediaEntity> entities, MediaController.CropState cropState, final Runnable onInit, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        Drawable backgroundDrawable;
        EntityView view;
        int type;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = entities;
        this.resourcesProvider = resourcesProvider;
        this.inBubbleMode = context instanceof BubbleActivity;
        this.currentCropState = cropState;
        this.originalBitmapRotation = originalRotation;
        this.bitmapToEdit = bitmap;
        this.facesBitmap = originalBitmap;
        UndoStore undoStore = new UndoStore();
        this.undoStore = undoStore;
        undoStore.setDelegate(new UndoStore.UndoStoreDelegate() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.Components.Paint.UndoStore.UndoStoreDelegate
            public final void historyChanged() {
                PhotoPaintView.this.m2830lambda$new$0$orgtelegramuiComponentsPhotoPaintView();
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.curtainView = frameLayout;
        frameLayout.setBackgroundColor(570425344);
        this.curtainView.setVisibility(4);
        addView(this.curtainView, LayoutHelper.createFrame(-1, -1.0f));
        RenderView renderView = new RenderView(context, new Painting(getPaintingSize()), bitmap);
        this.renderView = renderView;
        renderView.setDelegate(new RenderView.RenderViewDelegate() { // from class: org.telegram.ui.Components.PhotoPaintView.1
            @Override // org.telegram.ui.Components.Paint.RenderView.RenderViewDelegate
            public void onFirstDraw() {
                onInit.run();
            }

            @Override // org.telegram.ui.Components.Paint.RenderView.RenderViewDelegate
            public void onBeganDrawing() {
                if (PhotoPaintView.this.currentEntityView != null) {
                    PhotoPaintView.this.selectEntity(null);
                }
            }

            @Override // org.telegram.ui.Components.Paint.RenderView.RenderViewDelegate
            public void onFinishedDrawing(boolean moved) {
                PhotoPaintView.this.colorPicker.setUndoEnabled(PhotoPaintView.this.undoStore.canUndo());
            }

            @Override // org.telegram.ui.Components.Paint.RenderView.RenderViewDelegate
            public boolean shouldDraw() {
                boolean draw = PhotoPaintView.this.currentEntityView == null;
                if (!draw) {
                    PhotoPaintView.this.selectEntity(null);
                }
                return draw;
            }
        });
        this.renderView.setUndoStore(this.undoStore);
        this.renderView.setQueue(this.queue);
        this.renderView.setVisibility(4);
        this.renderView.setBrush(this.brushes[0]);
        addView(this.renderView, LayoutHelper.createFrame(-1, -1, 51));
        EntitiesContainerView entitiesContainerView = new EntitiesContainerView(context, new EntitiesContainerView.EntitiesContainerViewDelegate() { // from class: org.telegram.ui.Components.PhotoPaintView.2
            @Override // org.telegram.ui.Components.Paint.Views.EntitiesContainerView.EntitiesContainerViewDelegate
            public boolean shouldReceiveTouches() {
                return PhotoPaintView.this.textDimView.getVisibility() != 0;
            }

            @Override // org.telegram.ui.Components.Paint.Views.EntitiesContainerView.EntitiesContainerViewDelegate
            public EntityView onSelectedEntityRequest() {
                return PhotoPaintView.this.currentEntityView;
            }

            @Override // org.telegram.ui.Components.Paint.Views.EntitiesContainerView.EntitiesContainerViewDelegate
            public void onEntityDeselect() {
                PhotoPaintView.this.selectEntity(null);
            }
        });
        this.entitiesView = entitiesContainerView;
        addView(entitiesContainerView);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.dimView = frameLayout2;
        frameLayout2.setAlpha(0.0f);
        this.dimView.setBackgroundColor(1711276032);
        this.dimView.setVisibility(8);
        addView(this.dimView);
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.textDimView = frameLayout3;
        frameLayout3.setAlpha(0.0f);
        this.textDimView.setBackgroundColor(1711276032);
        this.textDimView.setVisibility(8);
        this.textDimView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoPaintView.this.m2831lambda$new$1$orgtelegramuiComponentsPhotoPaintView(view2);
            }
        });
        this.backgroundView = new FrameLayout(context);
        Drawable backgroundDrawable2 = getResources().getDrawable(R.drawable.gradient_bottom).mutate();
        backgroundDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.backgroundView.setBackground(backgroundDrawable2);
        addView(this.backgroundView, LayoutHelper.createFrame(-1, 72, 87));
        FrameLayout frameLayout4 = new FrameLayout(context) { // from class: org.telegram.ui.Components.PhotoPaintView.3
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                return false;
            }
        };
        this.selectionContainerView = frameLayout4;
        addView(frameLayout4);
        org.telegram.ui.Components.Paint.Views.ColorPicker colorPicker = new org.telegram.ui.Components.Paint.Views.ColorPicker(context);
        this.colorPicker = colorPicker;
        addView(colorPicker);
        this.colorPicker.setDelegate(new ColorPicker.ColorPickerDelegate() { // from class: org.telegram.ui.Components.PhotoPaintView.4
            @Override // org.telegram.ui.Components.Paint.Views.ColorPicker.ColorPickerDelegate
            public void onBeganColorPicking() {
                if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                    PhotoPaintView.this.setDimVisibility(true);
                }
            }

            @Override // org.telegram.ui.Components.Paint.Views.ColorPicker.ColorPickerDelegate
            public void onColorValueChanged() {
                PhotoPaintView photoPaintView = PhotoPaintView.this;
                photoPaintView.setCurrentSwatch(photoPaintView.colorPicker.getSwatch(), false);
            }

            @Override // org.telegram.ui.Components.Paint.Views.ColorPicker.ColorPickerDelegate
            public void onFinishedColorPicking() {
                PhotoPaintView photoPaintView = PhotoPaintView.this;
                photoPaintView.setCurrentSwatch(photoPaintView.colorPicker.getSwatch(), false);
                if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                    PhotoPaintView.this.setDimVisibility(false);
                }
            }

            @Override // org.telegram.ui.Components.Paint.Views.ColorPicker.ColorPickerDelegate
            public void onSettingsPressed() {
                if (PhotoPaintView.this.currentEntityView != null) {
                    if (PhotoPaintView.this.currentEntityView instanceof StickerView) {
                        PhotoPaintView.this.mirrorSticker();
                        return;
                    } else if (PhotoPaintView.this.currentEntityView instanceof TextPaintView) {
                        PhotoPaintView.this.showTextSettings();
                        return;
                    } else {
                        return;
                    }
                }
                PhotoPaintView.this.showBrushSettings();
            }

            @Override // org.telegram.ui.Components.Paint.Views.ColorPicker.ColorPickerDelegate
            public void onUndoPressed() {
                PhotoPaintView.this.undoStore.undo();
            }
        });
        FrameLayout frameLayout5 = new FrameLayout(context);
        this.toolsView = frameLayout5;
        frameLayout5.setBackgroundColor(-16777216);
        addView(this.toolsView, LayoutHelper.createFrame(-1, 48, 83));
        TextView textView = new TextView(context);
        this.cancelTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        this.cancelTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.toolsView.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        TextView textView2 = new TextView(context);
        this.doneTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.toolsView.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        ImageView imageView = new ImageView(context);
        this.paintButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.paintButton.setContentDescription(LocaleController.getString("AccDescrPaint", R.string.AccDescrPaint));
        this.paintButton.setImageResource(R.drawable.msg_photo_draw);
        this.paintButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(this.paintButton, LayoutHelper.createFrame(54, -1.0f, 17, 0.0f, 0.0f, 56.0f, 0.0f));
        this.paintButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda15
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoPaintView.this.m2832lambda$new$2$orgtelegramuiComponentsPhotoPaintView(view2);
            }
        });
        ImageView stickerButton = new ImageView(context);
        stickerButton.setScaleType(ImageView.ScaleType.CENTER);
        stickerButton.setImageResource(R.drawable.msg_sticker);
        stickerButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(stickerButton, LayoutHelper.createFrame(54, -1, 17));
        stickerButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda16
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoPaintView.this.m2833lambda$new$3$orgtelegramuiComponentsPhotoPaintView(view2);
            }
        });
        ImageView textButton = new ImageView(context);
        textButton.setScaleType(ImageView.ScaleType.CENTER);
        textButton.setContentDescription(LocaleController.getString("AccDescrPlaceText", R.string.AccDescrPlaceText));
        textButton.setImageResource(R.drawable.msg_photo_text);
        textButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(textButton, LayoutHelper.createFrame(54, -1.0f, 17, 56.0f, 0.0f, 0.0f, 0.0f));
        textButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda17
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoPaintView.this.m2834lambda$new$4$orgtelegramuiComponentsPhotoPaintView(view2);
            }
        });
        this.colorPicker.setUndoEnabled(false);
        setCurrentSwatch(this.colorPicker.getSwatch(), false);
        updateSettingsButton();
        if (arrayList != null && !entities.isEmpty()) {
            int a = 0;
            int N = entities.size();
            while (a < N) {
                VideoEditedInfo.MediaEntity entity = arrayList.get(a);
                if (entity.type == 0) {
                    StickerView stickerView = createSticker(entity.parentObject, entity.document, false);
                    if ((entity.subType & 2) != 0) {
                        stickerView.mirror();
                    }
                    view = stickerView;
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.width = entity.viewWidth;
                    layoutParams.height = entity.viewHeight;
                } else if (entity.type == 1) {
                    TextPaintView textPaintView = createText(false);
                    if ((entity.subType & 1) != 0) {
                        type = 0;
                    } else {
                        int type2 = entity.subType;
                        if ((type2 & 4) != 0) {
                            type = 2;
                        } else {
                            type = 1;
                        }
                    }
                    textPaintView.setType(type);
                    textPaintView.setText(entity.text);
                    Swatch swatch = textPaintView.getSwatch();
                    int type3 = entity.color;
                    swatch.color = type3;
                    textPaintView.setSwatch(swatch);
                    view = textPaintView;
                } else {
                    backgroundDrawable = backgroundDrawable2;
                    a++;
                    arrayList = entities;
                    backgroundDrawable2 = backgroundDrawable;
                }
                view.setX((entity.x * this.paintingSize.width) - ((entity.viewWidth * (1.0f - entity.scale)) / 2.0f));
                view.setY((entity.y * this.paintingSize.height) - ((entity.viewHeight * (1.0f - entity.scale)) / 2.0f));
                backgroundDrawable = backgroundDrawable2;
                view.setPosition(new Point(view.getX() + (entity.viewWidth / 2), view.getY() + (entity.viewHeight / 2)));
                view.setScaleX(entity.scale);
                view.setScaleY(entity.scale);
                double d = -entity.rotation;
                Double.isNaN(d);
                view.setRotation((float) ((d / 3.141592653589793d) * 180.0d));
                a++;
                arrayList = entities;
                backgroundDrawable2 = backgroundDrawable;
            }
        }
        this.entitiesView.setVisibility(4);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2830lambda$new$0$orgtelegramuiComponentsPhotoPaintView() {
        this.colorPicker.setUndoEnabled(this.undoStore.canUndo());
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2831lambda$new$1$orgtelegramuiComponentsPhotoPaintView(View v) {
        closeTextEnter(true);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2832lambda$new$2$orgtelegramuiComponentsPhotoPaintView(View v) {
        selectEntity(null);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2833lambda$new$3$orgtelegramuiComponentsPhotoPaintView(View v) {
        openStickersView();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2834lambda$new$4$orgtelegramuiComponentsPhotoPaintView(View v) {
        createText(true);
    }

    public void onResume() {
        this.renderView.redraw();
    }

    public boolean onTouch(MotionEvent ev) {
        if (this.currentEntityView != null) {
            if (this.editingText) {
                closeTextEnter(true);
            } else {
                selectEntity(null);
            }
        }
        float x2 = ((ev.getX() - this.renderView.getTranslationX()) - (getMeasuredWidth() / 2)) / this.renderView.getScaleX();
        float y2 = (((ev.getY() - this.renderView.getTranslationY()) - (getMeasuredHeight() / 2)) + AndroidUtilities.dp(32.0f)) / this.renderView.getScaleY();
        float rotation = (float) Math.toRadians(-this.renderView.getRotation());
        double d = x2;
        double cos = Math.cos(rotation);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = y2;
        double sin = Math.sin(rotation);
        Double.isNaN(d3);
        float x = ((float) (d2 - (d3 * sin))) + (this.renderView.getMeasuredWidth() / 2);
        double d4 = x2;
        double sin2 = Math.sin(rotation);
        Double.isNaN(d4);
        double d5 = d4 * sin2;
        double d6 = y2;
        double cos2 = Math.cos(rotation);
        Double.isNaN(d6);
        float y = ((float) (d5 + (d6 * cos2))) + (this.renderView.getMeasuredHeight() / 2);
        MotionEvent event = MotionEvent.obtain(0L, 0L, ev.getActionMasked(), x, y, 0);
        this.renderView.onTouch(event);
        event.recycle();
        return true;
    }

    private Size getPaintingSize() {
        Size size = this.paintingSize;
        if (size != null) {
            return size;
        }
        float width = this.bitmapToEdit.getWidth();
        float height = this.bitmapToEdit.getHeight();
        Size size2 = new Size(width, height);
        size2.width = 1280.0f;
        size2.height = (float) Math.floor((size2.width * height) / width);
        if (size2.height > 1280.0f) {
            size2.height = 1280.0f;
            size2.width = (float) Math.floor((size2.height * width) / height);
        }
        this.paintingSize = size2;
        return size2;
    }

    private void updateSettingsButton() {
        int resource = R.drawable.photo_paint_brush;
        this.colorPicker.settingsButton.setContentDescription(LocaleController.getString("AccDescrBrushType", R.string.AccDescrBrushType));
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            if (entityView instanceof StickerView) {
                resource = R.drawable.msg_photo_flip;
                this.colorPicker.settingsButton.setContentDescription(LocaleController.getString("AccDescrMirror", R.string.AccDescrMirror));
            } else if (entityView instanceof TextPaintView) {
                resource = R.drawable.photo_outline;
                this.colorPicker.settingsButton.setContentDescription(LocaleController.getString("PaintOutlined", R.string.PaintOutlined));
            }
            this.paintButton.setImageResource(R.drawable.msg_photo_draw);
            this.paintButton.setColorFilter((ColorFilter) null);
        } else {
            Swatch swatch = this.brushSwatch;
            if (swatch != null) {
                setCurrentSwatch(swatch, true);
                this.brushSwatch = null;
            }
            this.paintButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
            this.paintButton.setImageResource(R.drawable.msg_photo_draw);
        }
        this.backgroundView.setVisibility(this.currentEntityView instanceof TextPaintView ? 4 : 0);
        this.colorPicker.setSettingsButtonImage(resource);
    }

    public void updateColors() {
        ImageView imageView = this.paintButton;
        if (imageView != null && imageView.getColorFilter() != null) {
            this.paintButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingButton), PorterDuff.Mode.MULTIPLY));
        }
        TextView textView = this.doneTextView;
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_dialogFloatingButton));
        }
    }

    public void init() {
        this.entitiesView.setVisibility(0);
        this.renderView.setVisibility(0);
        if (this.facesBitmap != null) {
            detectFaces();
        }
    }

    public void shutdown() {
        this.renderView.shutdown();
        this.entitiesView.setVisibility(8);
        this.selectionContainerView.setVisibility(8);
        this.queue.postRunnable(PhotoPaintView$$ExternalSyntheticLambda9.INSTANCE);
    }

    public static /* synthetic */ void lambda$shutdown$5() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            looper.quit();
        }
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public FrameLayout getColorPickerBackground() {
        return this.backgroundView;
    }

    public FrameLayout getCurtainView() {
        return this.curtainView;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    public org.telegram.ui.Components.Paint.Views.ColorPicker getColorPicker() {
        return this.colorPicker;
    }

    public boolean hasChanges() {
        return this.undoStore.canUndo();
    }

    public Bitmap getBitmap(ArrayList<VideoEditedInfo.MediaEntity> entities, Bitmap[] thumbBitmap) {
        int count;
        Canvas canvas;
        Canvas canvas2;
        Canvas thumbCanvas;
        Canvas thumbCanvas2;
        long duration;
        PhotoPaintView photoPaintView = this;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = entities;
        Bitmap bitmap = photoPaintView.renderView.getResultBitmap();
        photoPaintView.lcm = BigInteger.ONE;
        if (bitmap != null && photoPaintView.entitiesView.entitiesCount() > 0) {
            Canvas canvas3 = null;
            Canvas thumbCanvas3 = null;
            int count2 = photoPaintView.entitiesView.getChildCount();
            int i = 0;
            while (i < count2) {
                boolean skipDrawToBitmap = false;
                View v = photoPaintView.entitiesView.getChildAt(i);
                if (!(v instanceof EntityView)) {
                    canvas = canvas3;
                    canvas2 = thumbCanvas3;
                    count = count2;
                } else {
                    EntityView entity = (EntityView) v;
                    Point position = entity.getPosition();
                    if (arrayList != null) {
                        VideoEditedInfo.MediaEntity mediaEntity = new VideoEditedInfo.MediaEntity();
                        byte b = 4;
                        if (entity instanceof TextPaintView) {
                            mediaEntity.type = (byte) 1;
                            TextPaintView textPaintView = (TextPaintView) entity;
                            mediaEntity.text = textPaintView.getText();
                            int type = textPaintView.getType();
                            if (type == 0) {
                                mediaEntity.subType = (byte) (1 | mediaEntity.subType);
                            } else if (type == 2) {
                                mediaEntity.subType = (byte) (mediaEntity.subType | 4);
                            }
                            mediaEntity.color = textPaintView.getSwatch().color;
                            mediaEntity.fontSize = textPaintView.getTextSize();
                            thumbCanvas2 = thumbCanvas3;
                        } else if (!(entity instanceof StickerView)) {
                            canvas = canvas3;
                            canvas2 = thumbCanvas3;
                            count = count2;
                        } else {
                            mediaEntity.type = (byte) 0;
                            StickerView stickerView = (StickerView) entity;
                            Size size = stickerView.getBaseSize();
                            mediaEntity.width = size.width;
                            mediaEntity.height = size.height;
                            mediaEntity.document = stickerView.getSticker();
                            mediaEntity.parentObject = stickerView.getParentObject();
                            TLRPC.Document document = stickerView.getSticker();
                            mediaEntity.text = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true).getAbsolutePath();
                            if (MessageObject.isAnimatedStickerDocument(document, true) || MessageObject.isVideoStickerDocument(document)) {
                                boolean isAnimatedSticker = MessageObject.isAnimatedStickerDocument(document, true);
                                byte b2 = mediaEntity.subType;
                                if (isAnimatedSticker) {
                                    b = 1;
                                }
                                mediaEntity.subType = (byte) (b2 | b);
                                if (isAnimatedSticker) {
                                    duration = stickerView.getDuration();
                                } else {
                                    duration = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                                }
                                if (duration != 0) {
                                    BigInteger x = BigInteger.valueOf(duration);
                                    thumbCanvas2 = thumbCanvas3;
                                    photoPaintView.lcm = photoPaintView.lcm.multiply(x).divide(photoPaintView.lcm.gcd(x));
                                } else {
                                    thumbCanvas2 = thumbCanvas3;
                                }
                                skipDrawToBitmap = true;
                            } else {
                                thumbCanvas2 = thumbCanvas3;
                            }
                            if (stickerView.isMirrored()) {
                                mediaEntity.subType = (byte) (mediaEntity.subType | 2);
                            }
                        }
                        arrayList.add(mediaEntity);
                        float scaleX = v.getScaleX();
                        float scaleY = v.getScaleY();
                        float x2 = v.getX();
                        float y = v.getY();
                        mediaEntity.viewWidth = v.getWidth();
                        mediaEntity.viewHeight = v.getHeight();
                        mediaEntity.width = (v.getWidth() * scaleX) / photoPaintView.entitiesView.getMeasuredWidth();
                        mediaEntity.height = (v.getHeight() * scaleY) / photoPaintView.entitiesView.getMeasuredHeight();
                        mediaEntity.x = (((v.getWidth() * (1.0f - scaleX)) / 2.0f) + x2) / photoPaintView.entitiesView.getMeasuredWidth();
                        mediaEntity.y = (((v.getHeight() * (1.0f - scaleY)) / 2.0f) + y) / photoPaintView.entitiesView.getMeasuredHeight();
                        count = count2;
                        double d = -v.getRotation();
                        Double.isNaN(d);
                        mediaEntity.rotation = (float) (d * 0.017453292519943295d);
                        mediaEntity.textViewX = ((v.getWidth() / 2) + x2) / photoPaintView.entitiesView.getMeasuredWidth();
                        mediaEntity.textViewY = ((v.getHeight() / 2) + y) / photoPaintView.entitiesView.getMeasuredHeight();
                        mediaEntity.textViewWidth = mediaEntity.viewWidth / photoPaintView.entitiesView.getMeasuredWidth();
                        mediaEntity.textViewHeight = mediaEntity.viewHeight / photoPaintView.entitiesView.getMeasuredHeight();
                        mediaEntity.scale = scaleX;
                        if (thumbBitmap[0] == null) {
                            thumbBitmap[0] = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                            thumbCanvas3 = new Canvas(thumbBitmap[0]);
                            thumbCanvas3.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                        } else {
                            thumbCanvas3 = thumbCanvas2;
                        }
                    } else {
                        count = count2;
                    }
                    Canvas canvas4 = new Canvas(bitmap);
                    int k = 0;
                    while (k < 2) {
                        Canvas currentCanvas = k == 0 ? canvas4 : thumbCanvas3;
                        if (currentCanvas == null) {
                            thumbCanvas = thumbCanvas3;
                        } else if (k == 0 && skipDrawToBitmap) {
                            thumbCanvas = thumbCanvas3;
                        } else {
                            currentCanvas.save();
                            currentCanvas.translate(position.x, position.y);
                            currentCanvas.scale(v.getScaleX(), v.getScaleY());
                            currentCanvas.rotate(v.getRotation());
                            currentCanvas.translate((-entity.getWidth()) / 2, (-entity.getHeight()) / 2);
                            if (v instanceof TextPaintView) {
                                Bitmap b3 = Bitmaps.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas c = new Canvas(b3);
                                v.draw(c);
                                thumbCanvas = thumbCanvas3;
                                currentCanvas.drawBitmap(b3, (android.graphics.Rect) null, new android.graphics.Rect(0, 0, b3.getWidth(), b3.getHeight()), (Paint) null);
                                try {
                                    c.setBitmap(null);
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                                b3.recycle();
                            } else {
                                thumbCanvas = thumbCanvas3;
                                v.draw(currentCanvas);
                            }
                            currentCanvas.restore();
                        }
                        k++;
                        thumbCanvas3 = thumbCanvas;
                    }
                    canvas3 = canvas4;
                    i++;
                    photoPaintView = this;
                    arrayList = entities;
                    count2 = count;
                }
                thumbCanvas3 = canvas2;
                canvas3 = canvas;
                i++;
                photoPaintView = this;
                arrayList = entities;
                count2 = count;
            }
        }
        return bitmap;
    }

    public long getLcm() {
        return this.lcm.longValue();
    }

    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity parentActivity, final Runnable okRunnable) {
        if (this.editingText) {
            closeTextEnter(false);
        } else if (hasChanges()) {
            if (parentActivity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
            builder.setMessage(LocaleController.getString("PhotoEditorDiscardAlert", R.string.PhotoEditorDiscardAlert));
            builder.setTitle(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    okRunnable.run();
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            photoViewer.showAlertDialog(builder);
        } else {
            okRunnable.run();
        }
    }

    public void setCurrentSwatch(Swatch swatch, boolean updateInterface) {
        this.renderView.setColor(swatch.color);
        this.renderView.setBrushSize(swatch.brushWeight);
        if (updateInterface) {
            if (this.brushSwatch == null && this.paintButton.getColorFilter() != null) {
                this.brushSwatch = this.colorPicker.getSwatch();
            }
            this.colorPicker.setSwatch(swatch);
        }
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            ((TextPaintView) entityView).setSwatch(swatch);
        }
    }

    public void setDimVisibility(final boolean visible) {
        Animator animator;
        if (!visible) {
            animator = ObjectAnimator.ofFloat(this.dimView, View.ALPHA, 1.0f, 0.0f);
        } else {
            this.dimView.setVisibility(0);
            animator = ObjectAnimator.ofFloat(this.dimView, View.ALPHA, 0.0f, 1.0f);
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PhotoPaintView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PhotoPaintView.this.dimView.setVisibility(8);
                }
            }
        });
        animator.setDuration(200L);
        animator.start();
    }

    private void setTextDimVisibility(final boolean visible, EntityView view) {
        Animator animator;
        if (visible && view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (this.textDimView.getParent() != null) {
                ((EntitiesContainerView) this.textDimView.getParent()).removeView(this.textDimView);
            }
            parent.addView(this.textDimView, parent.indexOfChild(view));
        }
        view.setSelectionVisibility(!visible);
        if (!visible) {
            animator = ObjectAnimator.ofFloat(this.textDimView, View.ALPHA, 1.0f, 0.0f);
        } else {
            this.textDimView.setVisibility(0);
            animator = ObjectAnimator.ofFloat(this.textDimView, View.ALPHA, 0.0f, 1.0f);
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PhotoPaintView.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PhotoPaintView.this.textDimView.setVisibility(8);
                    if (PhotoPaintView.this.textDimView.getParent() != null) {
                        ((EntitiesContainerView) PhotoPaintView.this.textDimView.getParent()).removeView(PhotoPaintView.this.textDimView);
                    }
                }
            }
        });
        animator.setDuration(200L);
        animator.start();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float bitmapH;
        float bitmapW;
        this.ignoreLayout = true;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        int fullHeight = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
        int maxHeight = fullHeight - AndroidUtilities.dp(48.0f);
        Bitmap bitmap = this.bitmapToEdit;
        if (bitmap != null) {
            bitmapW = bitmap.getWidth();
            bitmapH = this.bitmapToEdit.getHeight();
        } else {
            bitmapW = width;
            bitmapH = (height - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f);
        }
        float renderWidth = width;
        float renderHeight = (float) Math.floor((renderWidth * bitmapH) / bitmapW);
        if (renderHeight > maxHeight) {
            renderHeight = maxHeight;
            renderWidth = (float) Math.floor((renderHeight * bitmapW) / bitmapH);
        }
        this.renderView.measure(View.MeasureSpec.makeMeasureSpec((int) renderWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) renderHeight, C.BUFFER_FLAG_ENCRYPTED));
        float f = renderWidth / this.paintingSize.width;
        this.baseScale = f;
        this.entitiesView.setScaleX(f);
        this.entitiesView.setScaleY(this.baseScale);
        this.entitiesView.measure(View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, C.BUFFER_FLAG_ENCRYPTED));
        this.dimView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(maxHeight, Integer.MIN_VALUE));
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            entityView.updateSelectionView();
        }
        this.selectionContainerView.measure(View.MeasureSpec.makeMeasureSpec((int) renderWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) renderHeight, C.BUFFER_FLAG_ENCRYPTED));
        this.colorPicker.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(maxHeight, C.BUFFER_FLAG_ENCRYPTED));
        this.toolsView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
        this.curtainView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(maxHeight, C.BUFFER_FLAG_ENCRYPTED));
        this.backgroundView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f), C.BUFFER_FLAG_ENCRYPTED));
        this.ignoreLayout = false;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int status = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
        int actionBarHeight2 = actionBarHeight + status;
        int dp = (AndroidUtilities.displaySize.y - actionBarHeight) - AndroidUtilities.dp(48.0f);
        int x = (int) Math.ceil((width - this.renderView.getMeasuredWidth()) / 2);
        int y = ((((height - actionBarHeight2) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / 2) + AndroidUtilities.dp(8.0f) + status;
        RenderView renderView = this.renderView;
        renderView.layout(x, y, renderView.getMeasuredWidth() + x, this.renderView.getMeasuredHeight() + y);
        int x2 = ((this.renderView.getMeasuredWidth() - this.entitiesView.getMeasuredWidth()) / 2) + x;
        int y2 = ((this.renderView.getMeasuredHeight() - this.entitiesView.getMeasuredHeight()) / 2) + y;
        EntitiesContainerView entitiesContainerView = this.entitiesView;
        entitiesContainerView.layout(x2, y2, entitiesContainerView.getMeasuredWidth() + x2, this.entitiesView.getMeasuredHeight() + y2);
        FrameLayout frameLayout = this.dimView;
        frameLayout.layout(0, status, frameLayout.getMeasuredWidth(), this.dimView.getMeasuredHeight() + status);
        FrameLayout frameLayout2 = this.selectionContainerView;
        frameLayout2.layout(x, y, frameLayout2.getMeasuredWidth() + x, this.selectionContainerView.getMeasuredHeight() + y);
        org.telegram.ui.Components.Paint.Views.ColorPicker colorPicker = this.colorPicker;
        colorPicker.layout(0, actionBarHeight2, colorPicker.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + actionBarHeight2);
        FrameLayout frameLayout3 = this.toolsView;
        frameLayout3.layout(0, height - frameLayout3.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), height);
        FrameLayout frameLayout4 = this.curtainView;
        frameLayout4.layout(0, y, frameLayout4.getMeasuredWidth(), this.curtainView.getMeasuredHeight() + y);
        this.backgroundView.layout(0, (height - AndroidUtilities.dp(45.0f)) - this.backgroundView.getMeasuredHeight(), this.backgroundView.getMeasuredWidth(), height - AndroidUtilities.dp(45.0f));
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView.EntityViewDelegate
    public boolean onEntitySelected(EntityView entityView) {
        return selectEntity(entityView);
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView.EntityViewDelegate
    public boolean onEntityLongClicked(EntityView entityView) {
        showMenuForEntity(entityView);
        return true;
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView.EntityViewDelegate
    public float[] getTransformedTouch(float x, float y) {
        float x2 = x - (AndroidUtilities.displaySize.x / 2);
        float y2 = y - (AndroidUtilities.displaySize.y / 2);
        float rotation = (float) Math.toRadians(-this.entitiesView.getRotation());
        float[] fArr = this.temp;
        double d = x2;
        double cos = Math.cos(rotation);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = y2;
        double sin = Math.sin(rotation);
        Double.isNaN(d3);
        fArr[0] = ((float) (d2 - (d3 * sin))) + (AndroidUtilities.displaySize.x / 2);
        float[] fArr2 = this.temp;
        double d4 = x2;
        double sin2 = Math.sin(rotation);
        Double.isNaN(d4);
        double d5 = d4 * sin2;
        double d6 = y2;
        double cos2 = Math.cos(rotation);
        Double.isNaN(d6);
        fArr2[1] = ((float) (d5 + (d6 * cos2))) + (AndroidUtilities.displaySize.y / 2);
        return this.temp;
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView.EntityViewDelegate
    public int[] getCenterLocation(EntityView entityView) {
        return getCenterLocationInWindow(entityView);
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView.EntityViewDelegate
    public boolean allowInteraction(EntityView entityView) {
        return !this.editingText;
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean restore = false;
        if ((child == this.renderView || child == this.entitiesView || child == this.selectionContainerView) && this.currentCropState != null) {
            canvas.save();
            int status = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
            int actionBarHeight = ActionBar.getCurrentActionBarHeight();
            int actionBarHeight2 = actionBarHeight + status;
            int vw = child.getMeasuredWidth();
            int vh = child.getMeasuredHeight();
            int tr = this.currentCropState.transformRotation;
            if (tr == 90 || tr == 270) {
                vw = vh;
                vh = vw;
            }
            int w = (int) (((vw * this.currentCropState.cropPw) * child.getScaleX()) / this.currentCropState.cropScale);
            int h = (int) (((vh * this.currentCropState.cropPh) * child.getScaleY()) / this.currentCropState.cropScale);
            float x = ((float) Math.ceil((getMeasuredWidth() - w) / 2)) + this.transformX;
            float y = ((((getMeasuredHeight() - actionBarHeight2) - AndroidUtilities.dp(48.0f)) - h) / 2) + AndroidUtilities.dp(8.0f) + status + this.transformY;
            canvas.clipRect(Math.max(0.0f, x), Math.max(0.0f, y), Math.min(w + x, getMeasuredWidth()), Math.min(getMeasuredHeight(), h + y));
            restore = true;
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (restore) {
            canvas.restore();
        }
        return result;
    }

    private Point centerPositionForEntity() {
        MediaController.CropState cropState;
        Size paintingSize = getPaintingSize();
        float x = paintingSize.width / 2.0f;
        float y = paintingSize.height / 2.0f;
        if (this.currentCropState != null) {
            float rotation = (float) Math.toRadians(-(cropState.transformRotation + this.currentCropState.cropRotate));
            double d = this.currentCropState.cropPx;
            double cos = Math.cos(rotation);
            Double.isNaN(d);
            double d2 = d * cos;
            double d3 = this.currentCropState.cropPy;
            double sin = Math.sin(rotation);
            Double.isNaN(d3);
            float px = (float) (d2 - (d3 * sin));
            double d4 = this.currentCropState.cropPx;
            double sin2 = Math.sin(rotation);
            Double.isNaN(d4);
            double d5 = d4 * sin2;
            double d6 = this.currentCropState.cropPy;
            double cos2 = Math.cos(rotation);
            Double.isNaN(d6);
            float py = (float) (d5 + (d6 * cos2));
            x -= paintingSize.width * px;
            y -= paintingSize.height * py;
        }
        return new Point(x, y);
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        float offset = 200.0f;
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            offset = 200.0f / cropState.cropScale;
        }
        if (entityView != null) {
            Point position = entityView.getPosition();
            return new Point(position.x + offset, position.y + offset);
        }
        float minimalDistance = 100.0f;
        MediaController.CropState cropState2 = this.currentCropState;
        if (cropState2 != null) {
            minimalDistance = 100.0f / cropState2.cropScale;
        }
        Point position2 = centerPositionForEntity();
        while (true) {
            boolean occupied = false;
            for (int index = 0; index < this.entitiesView.getChildCount(); index++) {
                View view = this.entitiesView.getChildAt(index);
                if (view instanceof EntityView) {
                    Point location = ((EntityView) view).getPosition();
                    float distance = (float) Math.sqrt(Math.pow(location.x - position2.x, 2.0d) + Math.pow(location.y - position2.y, 2.0d));
                    if (distance < minimalDistance) {
                        occupied = true;
                    }
                }
            }
            if (occupied) {
                position2 = new Point(position2.x + offset, position2.y + offset);
            } else {
                return position2;
            }
        }
    }

    public ArrayList<TLRPC.InputDocument> getMasks() {
        ArrayList<TLRPC.InputDocument> result = null;
        int count = this.entitiesView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.entitiesView.getChildAt(a);
            if (child instanceof StickerView) {
                TLRPC.Document document = ((StickerView) child).getSticker();
                if (result == null) {
                    result = new ArrayList<>();
                }
                TLRPC.TL_inputDocument inputDocument = new TLRPC.TL_inputDocument();
                inputDocument.id = document.id;
                inputDocument.access_hash = document.access_hash;
                inputDocument.file_reference = document.file_reference;
                if (inputDocument.file_reference == null) {
                    inputDocument.file_reference = new byte[0];
                }
                result.add(inputDocument);
            }
        }
        return result;
    }

    public void setTransform(float scale, float trX, float trY, float imageWidth, float imageHeight) {
        View view;
        float rotation;
        float rotation2;
        float tx;
        float f = trX;
        this.transformX = f;
        this.transformY = trY;
        int a = 0;
        while (a < 3) {
            float tx2 = 1.0f;
            if (a == 0) {
                view = this.entitiesView;
            } else if (a == 1) {
                view = this.selectionContainerView;
            } else {
                view = this.renderView;
            }
            MediaController.CropState cropState = this.currentCropState;
            if (cropState != null) {
                float additionlScale = 1.0f * cropState.cropScale;
                int w = view.getMeasuredWidth();
                int h = view.getMeasuredHeight();
                if (w == 0 || h == 0) {
                    return;
                }
                int tr = this.currentCropState.transformRotation;
                int fw = w;
                int rotatedW = w;
                int fh = h;
                int rotatedH = h;
                if (tr == 90 || tr == 270) {
                    rotatedW = fh;
                    fw = fh;
                    rotatedH = fw;
                    fh = fw;
                }
                float sc = Math.max(imageWidth / ((int) (fw * this.currentCropState.cropPw)), imageHeight / ((int) (fh * this.currentCropState.cropPh)));
                float additionlScale2 = additionlScale * sc;
                float additionlScale3 = rotatedW;
                float tx3 = f + (this.currentCropState.cropPx * additionlScale3 * scale * sc * this.currentCropState.cropScale);
                float ty = trY + (this.currentCropState.cropPy * rotatedH * scale * sc * this.currentCropState.cropScale);
                float ty2 = tr;
                float rotation3 = this.currentCropState.cropRotate + ty2;
                rotation = rotation3;
                tx = ty;
                rotation2 = tx3;
                tx2 = additionlScale2;
            } else {
                if (a == 0) {
                    tx2 = 1.0f * this.baseScale;
                }
                rotation2 = trX;
                tx = trY;
                rotation = 0.0f;
            }
            float finalScale = scale * tx2;
            if (Float.isNaN(finalScale)) {
                finalScale = 1.0f;
            }
            view.setScaleX(finalScale);
            view.setScaleY(finalScale);
            view.setTranslationX(rotation2);
            view.setTranslationY(tx);
            view.setRotation(rotation);
            view.invalidate();
            a++;
            f = trX;
        }
        invalidate();
    }

    public boolean selectEntity(EntityView entityView) {
        boolean changed = false;
        EntityView entityView2 = this.currentEntityView;
        if (entityView2 != null) {
            if (entityView2 == entityView) {
                if (!this.editingText) {
                    showMenuForEntity(entityView2);
                }
                return true;
            }
            entityView2.deselect();
            changed = true;
        }
        EntityView oldEntity = this.currentEntityView;
        this.currentEntityView = entityView;
        if (oldEntity instanceof TextPaintView) {
            TextPaintView textPaintView = (TextPaintView) oldEntity;
            if (TextUtils.isEmpty(textPaintView.getText())) {
                m2837x59ab8b73(oldEntity);
            }
        }
        EntityView entityView3 = this.currentEntityView;
        if (entityView3 != null) {
            entityView3.select(this.selectionContainerView);
            this.entitiesView.bringViewToFront(this.currentEntityView);
            EntityView entityView4 = this.currentEntityView;
            if (entityView4 instanceof TextPaintView) {
                setCurrentSwatch(((TextPaintView) entityView4).getSwatch(), true);
            }
            changed = true;
        }
        updateSettingsButton();
        return changed;
    }

    /* renamed from: removeEntity */
    public void m2837x59ab8b73(EntityView entityView) {
        EntityView entityView2 = this.currentEntityView;
        if (entityView == entityView2) {
            entityView2.deselect();
            if (this.editingText) {
                closeTextEnter(false);
            }
            this.currentEntityView = null;
            updateSettingsButton();
        }
        this.entitiesView.removeView(entityView);
        this.undoStore.unregisterUndo(entityView.getUUID());
    }

    private void duplicateSelectedEntity() {
        EntityView entityView = this.currentEntityView;
        if (entityView == null) {
            return;
        }
        EntityView entityView2 = null;
        Point position = startPositionRelativeToEntity(entityView);
        EntityView entityView3 = this.currentEntityView;
        if (entityView3 instanceof StickerView) {
            EntityView newStickerView = new StickerView(getContext(), (StickerView) this.currentEntityView, position);
            newStickerView.setDelegate(this);
            this.entitiesView.addView(newStickerView);
            entityView2 = newStickerView;
        } else if (entityView3 instanceof TextPaintView) {
            TextPaintView newTextPaintView = new TextPaintView(getContext(), (TextPaintView) this.currentEntityView, position);
            newTextPaintView.setDelegate(this);
            newTextPaintView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
            this.entitiesView.addView(newTextPaintView, LayoutHelper.createFrame(-2, -2.0f));
            entityView2 = newTextPaintView;
        }
        registerRemovalUndo(entityView2);
        selectEntity(entityView2);
        updateSettingsButton();
    }

    private void openStickersView() {
        StickerMasksAlert stickerMasksAlert = new StickerMasksAlert(getContext(), this.facesBitmap == null, this.resourcesProvider);
        stickerMasksAlert.setDelegate(new StickerMasksAlert.StickerMasksAlertDelegate() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda13
            @Override // org.telegram.ui.Components.StickerMasksAlert.StickerMasksAlertDelegate
            public final void onStickerSelected(Object obj, TLRPC.Document document) {
                PhotoPaintView.this.m2835x3df5f043(obj, document);
            }
        });
        stickerMasksAlert.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda11
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PhotoPaintView.this.m2836x23375f04(dialogInterface);
            }
        });
        stickerMasksAlert.show();
        onOpenCloseStickersAlert(true);
    }

    /* renamed from: lambda$openStickersView$7$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2835x3df5f043(Object parentObject, TLRPC.Document sticker) {
        createSticker(parentObject, sticker, true);
    }

    /* renamed from: lambda$openStickersView$8$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2836x23375f04(DialogInterface dialog) {
        onOpenCloseStickersAlert(false);
    }

    protected void onOpenCloseStickersAlert(boolean open) {
    }

    protected void onTextAdd() {
    }

    private Size baseStickerSize() {
        double d = getPaintingSize().width;
        Double.isNaN(d);
        float side = (float) Math.floor(d * 0.5d);
        return new Size(side, side);
    }

    private void registerRemovalUndo(final EntityView entityView) {
        this.undoStore.registerUndo(entityView.getUUID(), new Runnable() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                PhotoPaintView.this.m2837x59ab8b73(entityView);
            }
        });
    }

    private StickerView createSticker(Object parentObject, TLRPC.Document sticker, boolean select) {
        StickerPosition position = calculateStickerPosition(sticker);
        StickerView view = new StickerView(getContext(), position.position, position.angle, position.scale, baseStickerSize(), sticker, parentObject) { // from class: org.telegram.ui.Components.PhotoPaintView.7
            @Override // org.telegram.ui.Components.Paint.Views.StickerView
            protected void didSetAnimatedSticker(RLottieDrawable drawable) {
                PhotoPaintView.this.didSetAnimatedSticker(drawable);
            }
        };
        view.setDelegate(this);
        this.entitiesView.addView(view);
        if (select) {
            registerRemovalUndo(view);
            selectEntity(view);
        }
        return view;
    }

    protected void didSetAnimatedSticker(RLottieDrawable drawable) {
    }

    public void mirrorSticker() {
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof StickerView) {
            ((StickerView) entityView).mirror();
        }
    }

    private TextPaintView createText(boolean select) {
        Swatch swatch;
        onTextAdd();
        Swatch currentSwatch = this.colorPicker.getSwatch();
        int i = this.selectedTextType;
        if (i == 0) {
            swatch = new Swatch(-16777216, 0.85f, currentSwatch.brushWeight);
        } else if (i == 1) {
            swatch = new Swatch(-1, 1.0f, currentSwatch.brushWeight);
        } else {
            swatch = new Swatch(-1, 1.0f, currentSwatch.brushWeight);
        }
        Size paintingSize = getPaintingSize();
        TextPaintView view = new TextPaintView(getContext(), startPositionRelativeToEntity(null), (int) (paintingSize.width / 9.0f), "", swatch, this.selectedTextType);
        view.setDelegate(this);
        view.setMaxWidth((int) (paintingSize.width - 20.0f));
        this.entitiesView.addView(view, LayoutHelper.createFrame(-2, -2.0f));
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            view.scale(1.0f / cropState.cropScale);
            view.rotate(-(this.currentCropState.transformRotation + this.currentCropState.cropRotate));
        }
        if (select) {
            registerRemovalUndo(view);
            selectEntity(view);
            editSelectedTextEntity();
        }
        setCurrentSwatch(swatch, true);
        return view;
    }

    private void editSelectedTextEntity() {
        if (!(this.currentEntityView instanceof TextPaintView) || this.editingText) {
            return;
        }
        this.curtainView.setVisibility(0);
        TextPaintView textPaintView = (TextPaintView) this.currentEntityView;
        this.initialText = textPaintView.getText();
        this.editingText = true;
        this.editedTextPosition = textPaintView.getPosition();
        this.editedTextRotation = textPaintView.getRotation();
        this.editedTextScale = textPaintView.getScale();
        textPaintView.setPosition(centerPositionForEntity());
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            textPaintView.setRotation(-(cropState.transformRotation + this.currentCropState.cropRotate));
            textPaintView.setScale(1.0f / this.currentCropState.cropScale);
        } else {
            textPaintView.setRotation(0.0f);
            textPaintView.setScale(1.0f);
        }
        this.toolsView.setVisibility(8);
        setTextDimVisibility(true, textPaintView);
        textPaintView.beginEditing();
        View view = textPaintView.getFocusedView();
        view.requestFocus();
        AndroidUtilities.showKeyboard(view);
    }

    public void closeTextEnter(boolean apply) {
        if (this.editingText) {
            EntityView entityView = this.currentEntityView;
            if (!(entityView instanceof TextPaintView)) {
                return;
            }
            TextPaintView textPaintView = (TextPaintView) entityView;
            this.toolsView.setVisibility(0);
            AndroidUtilities.hideKeyboard(textPaintView.getFocusedView());
            textPaintView.getFocusedView().clearFocus();
            textPaintView.endEditing();
            if (!apply) {
                textPaintView.setText(this.initialText);
            }
            if (textPaintView.getText().trim().length() == 0) {
                this.entitiesView.removeView(textPaintView);
                selectEntity(null);
            } else {
                textPaintView.setPosition(this.editedTextPosition);
                textPaintView.setRotation(this.editedTextRotation);
                textPaintView.setScale(this.editedTextScale);
                this.editedTextPosition = null;
                this.editedTextRotation = 0.0f;
                this.editedTextScale = 0.0f;
            }
            setTextDimVisibility(false, textPaintView);
            this.editingText = false;
            this.initialText = null;
            this.curtainView.setVisibility(8);
        }
    }

    private void setBrush(int brush) {
        RenderView renderView = this.renderView;
        Brush[] brushArr = this.brushes;
        this.currentBrush = brush;
        renderView.setBrush(brushArr[brush]);
    }

    private void setType(int type) {
        this.selectedTextType = type;
        if (this.currentEntityView instanceof TextPaintView) {
            Swatch currentSwatch = this.colorPicker.getSwatch();
            if (type == 0 && currentSwatch.color == -1) {
                Swatch blackSwatch = new Swatch(-16777216, 0.85f, currentSwatch.brushWeight);
                setCurrentSwatch(blackSwatch, true);
            } else if ((type == 1 || type == 2) && currentSwatch.color == -16777216) {
                Swatch blackSwatch2 = new Swatch(-1, 1.0f, currentSwatch.brushWeight);
                setCurrentSwatch(blackSwatch2, true);
            }
            ((TextPaintView) this.currentEntityView).setType(type);
        }
    }

    private int[] getCenterLocationInWindow(View view) {
        MediaController.CropState cropState;
        view.getLocationInWindow(this.pos);
        float rotation = view.getRotation();
        float rotation2 = (float) Math.toRadians(rotation + (this.currentCropState != null ? cropState.cropRotate + this.currentCropState.transformRotation : 0.0f));
        float width = view.getWidth() * view.getScaleX() * this.entitiesView.getScaleX();
        float height = view.getHeight() * view.getScaleY() * this.entitiesView.getScaleY();
        double d = width;
        double cos = Math.cos(rotation2);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = height;
        double sin = Math.sin(rotation2);
        Double.isNaN(d3);
        float px = (float) (d2 - (d3 * sin));
        double d4 = width;
        double sin2 = Math.sin(rotation2);
        Double.isNaN(d4);
        double d5 = d4 * sin2;
        double d6 = height;
        double cos2 = Math.cos(rotation2);
        Double.isNaN(d6);
        float py = (float) (d5 + (d6 * cos2));
        int[] iArr = this.pos;
        iArr[0] = (int) (iArr[0] + (px / 2.0f));
        iArr[1] = (int) (iArr[1] + (py / 2.0f));
        return iArr;
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView.EntityViewDelegate
    public float getCropRotation() {
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            return cropState.cropRotate + this.currentCropState.transformRotation;
        }
        return 0.0f;
    }

    private void showMenuForEntity(final EntityView entityView) {
        int[] pos = getCenterLocationInWindow(entityView);
        int x = pos[0];
        int y = pos[1] - AndroidUtilities.dp(32.0f);
        showPopup(new Runnable() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                PhotoPaintView.this.m2842xbc214a01(entityView);
            }
        }, this, 51, x, y);
    }

    /* renamed from: lambda$showMenuForEntity$13$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2842xbc214a01(final EntityView entityView) {
        LinearLayout parent = new LinearLayout(getContext());
        parent.setOrientation(0);
        TextView deleteView = new TextView(getContext());
        deleteView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        deleteView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        deleteView.setGravity(16);
        deleteView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
        deleteView.setTextSize(1, 18.0f);
        deleteView.setTag(0);
        deleteView.setText(LocaleController.getString("PaintDelete", R.string.PaintDelete));
        deleteView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoPaintView.this.m2839xc5cfdbe(entityView, view);
            }
        });
        parent.addView(deleteView, LayoutHelper.createLinear(-2, 48));
        if (entityView instanceof TextPaintView) {
            TextView editView = new TextView(getContext());
            editView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
            editView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            editView.setGravity(16);
            editView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            editView.setTextSize(1, 18.0f);
            editView.setTag(1);
            editView.setText(LocaleController.getString("PaintEdit", R.string.PaintEdit));
            editView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda18
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoPaintView.this.m2840xf19e6c7f(view);
                }
            });
            parent.addView(editView, LayoutHelper.createLinear(-2, 48));
        }
        TextView duplicateView = new TextView(getContext());
        duplicateView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        duplicateView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        duplicateView.setGravity(16);
        duplicateView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
        duplicateView.setTextSize(1, 18.0f);
        duplicateView.setTag(2);
        duplicateView.setText(LocaleController.getString("PaintDuplicate", R.string.PaintDuplicate));
        duplicateView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda19
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoPaintView.this.m2841xd6dfdb40(view);
            }
        });
        parent.addView(duplicateView, LayoutHelper.createLinear(-2, 48));
        this.popupLayout.addView(parent);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        params.width = -2;
        params.height = -2;
        parent.setLayoutParams(params);
    }

    /* renamed from: lambda$showMenuForEntity$10$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2839xc5cfdbe(EntityView entityView, View v) {
        m2837x59ab8b73(entityView);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* renamed from: lambda$showMenuForEntity$11$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2840xf19e6c7f(View v) {
        editSelectedTextEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* renamed from: lambda$showMenuForEntity$12$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2841xd6dfdb40(View v) {
        duplicateSelectedEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    private LinearLayout buttonForBrush(final int brush, int icon, String text, boolean selected) {
        LinearLayout button = new LinearLayout(getContext()) { // from class: org.telegram.ui.Components.PhotoPaintView.8
            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };
        int i = 0;
        button.setOrientation(0);
        button.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        button.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda20
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoPaintView.this.m2827xb19dd1f5(brush, view);
            }
        });
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(icon);
        imageView.setColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        button.addView(imageView, LayoutHelper.createLinear(-2, -2, 19, 16, 0, 16, 0));
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setTextSize(1, 16.0f);
        textView.setText(text);
        textView.setMinWidth(AndroidUtilities.dp(70.0f));
        button.addView(textView, LayoutHelper.createLinear(-2, -2, 19, 0, 0, 16, 0));
        ImageView check = new ImageView(getContext());
        check.setImageResource(R.drawable.msg_text_check);
        check.setScaleType(ImageView.ScaleType.CENTER);
        check.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_radioBackgroundChecked), PorterDuff.Mode.MULTIPLY));
        if (!selected) {
            i = 4;
        }
        check.setVisibility(i);
        button.addView(check, LayoutHelper.createLinear(50, -1));
        return button;
    }

    /* renamed from: lambda$buttonForBrush$14$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2827xb19dd1f5(int brush, View v) {
        setBrush(brush);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    public void showBrushSettings() {
        showPopup(new Runnable() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                PhotoPaintView.this.m2838xae966b73();
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    /* renamed from: lambda$showBrushSettings$15$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2838xae966b73() {
        boolean z = false;
        View radial = buttonForBrush(0, R.drawable.msg_draw_pen, LocaleController.getString("PaintPen", R.string.PaintPen), this.currentBrush == 0);
        this.popupLayout.addView(radial, LayoutHelper.createLinear(-1, 54));
        View elliptical = buttonForBrush(1, R.drawable.msg_draw_marker, LocaleController.getString("PaintMarker", R.string.PaintMarker), this.currentBrush == 1);
        this.popupLayout.addView(elliptical, LayoutHelper.createLinear(-1, 54));
        View neon = buttonForBrush(2, R.drawable.msg_draw_neon, LocaleController.getString("PaintNeon", R.string.PaintNeon), this.currentBrush == 2);
        this.popupLayout.addView(neon, LayoutHelper.createLinear(-1, 54));
        String string = LocaleController.getString("PaintArrow", R.string.PaintArrow);
        if (this.currentBrush == 3) {
            z = true;
        }
        View arrow = buttonForBrush(3, R.drawable.msg_draw_arrow, string, z);
        this.popupLayout.addView(arrow, LayoutHelper.createLinear(-1, 54));
    }

    private LinearLayout buttonForText(final int type, String text, int icon, boolean selected) {
        LinearLayout button = new LinearLayout(getContext()) { // from class: org.telegram.ui.Components.PhotoPaintView.9
            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };
        button.setOrientation(0);
        button.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        button.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda21
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoPaintView.this.m2828x41484eb0(type, view);
            }
        });
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(icon);
        imageView.setColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        button.addView(imageView, LayoutHelper.createLinear(-2, -2, 19, 16, 0, 16, 0));
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setTextSize(1, 16.0f);
        textView.setText(text);
        button.addView(textView, LayoutHelper.createLinear(-2, -2, 19, 0, 0, 16, 0));
        if (selected) {
            ImageView check = new ImageView(getContext());
            check.setImageResource(R.drawable.msg_text_check);
            check.setScaleType(ImageView.ScaleType.CENTER);
            check.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_radioBackgroundChecked), PorterDuff.Mode.MULTIPLY));
            button.addView(check, LayoutHelper.createLinear(50, -1));
        }
        return button;
    }

    /* renamed from: lambda$buttonForText$16$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2828x41484eb0(int type, View v) {
        setType(type);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    public void showTextSettings() {
        showPopup(new Runnable() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                PhotoPaintView.this.m2846xdd81e6ee();
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    /* renamed from: lambda$showTextSettings$17$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2846xdd81e6ee() {
        int icon;
        String text;
        for (int a = 0; a < 3; a++) {
            boolean z = true;
            if (a == 0) {
                text = LocaleController.getString("PaintOutlined", R.string.PaintOutlined);
                icon = R.drawable.msg_text_outlined;
            } else if (a == 1) {
                text = LocaleController.getString("PaintRegular", R.string.PaintRegular);
                icon = R.drawable.msg_text_regular;
            } else {
                text = LocaleController.getString("PaintFramed", R.string.PaintFramed);
                icon = R.drawable.msg_text_framed;
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
            if (this.selectedTextType != a) {
                z = false;
            }
            actionBarPopupWindowLayout.addView((View) buttonForText(a, text, icon, z), LayoutHelper.createLinear(-1, 48));
        }
    }

    private void showPopup(Runnable setupRunnable, View parent, int gravity, int x, int y) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
            return;
        }
        if (this.popupLayout == null) {
            this.popupRect = new android.graphics.Rect();
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            this.popupLayout.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda2
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return PhotoPaintView.this.m2843lambda$showPopup$18$orgtelegramuiComponentsPhotoPaintView(view, motionEvent);
                }
            });
            this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda10
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    PhotoPaintView.this.m2844lambda$showPopup$19$orgtelegramuiComponentsPhotoPaintView(keyEvent);
                }
            });
            this.popupLayout.setShownFromBottom(true);
        }
        this.popupLayout.removeInnerViews();
        setupRunnable.run();
        if (this.popupWindow == null) {
            ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(this.popupLayout, -2, -2);
            this.popupWindow = actionBarPopupWindow2;
            actionBarPopupWindow2.setAnimationEnabled(false);
            this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
            this.popupWindow.setOutsideTouchable(true);
            this.popupWindow.setClippingEnabled(true);
            this.popupWindow.setInputMethodMode(2);
            this.popupWindow.setSoftInputMode(0);
            this.popupWindow.getContentView().setFocusableInTouchMode(true);
            this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda3
                @Override // android.widget.PopupWindow.OnDismissListener
                public final void onDismiss() {
                    PhotoPaintView.this.m2845lambda$showPopup$20$orgtelegramuiComponentsPhotoPaintView();
                }
            });
        }
        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.popupWindow.setFocusable(true);
        if ((gravity & 48) != 0) {
            x -= this.popupLayout.getMeasuredWidth() / 2;
            y -= this.popupLayout.getMeasuredHeight();
        }
        this.popupWindow.showAtLocation(parent, gravity, x, y);
        this.popupWindow.startAnimation();
    }

    /* renamed from: lambda$showPopup$18$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ boolean m2843lambda$showPopup$18$orgtelegramuiComponentsPhotoPaintView(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            v.getHitRect(this.popupRect);
            if (!this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                this.popupWindow.dismiss();
                return false;
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$showPopup$19$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2844lambda$showPopup$19$orgtelegramuiComponentsPhotoPaintView(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    /* renamed from: lambda$showPopup$20$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2845lambda$showPopup$20$orgtelegramuiComponentsPhotoPaintView() {
        this.popupLayout.removeInnerViews();
    }

    private int getFrameRotation() {
        switch (this.originalBitmapRotation) {
            case 90:
                return 1;
            case 180:
                return 2;
            case 270:
                return 3;
            default:
                return 0;
        }
    }

    private boolean isSidewardOrientation() {
        int i = this.originalBitmapRotation;
        return i % 360 == 90 || i % 360 == 270;
    }

    private void detectFaces() {
        this.queue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PhotoPaintView.this.m2829lambda$detectFaces$21$orgtelegramuiComponentsPhotoPaintView();
            }
        });
    }

    /* renamed from: lambda$detectFaces$21$org-telegram-ui-Components-PhotoPaintView */
    public /* synthetic */ void m2829lambda$detectFaces$21$orgtelegramuiComponentsPhotoPaintView() {
        FaceDetector faceDetector = null;
        try {
            try {
                faceDetector = new FaceDetector.Builder(getContext()).setMode(1).setLandmarkType(1).setTrackingEnabled(false).build();
            } catch (Throwable th) {
                if (0 != 0) {
                    faceDetector.release();
                }
                throw th;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (0 == 0) {
                return;
            }
        }
        if (!faceDetector.isOperational()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("face detection is not operational");
            }
            if (faceDetector == null) {
                return;
            }
            faceDetector.release();
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(this.facesBitmap).setRotation(getFrameRotation()).build();
        try {
            SparseArray<Face> faces = faceDetector.detect(frame);
            ArrayList<PhotoFace> result = new ArrayList<>();
            Size targetSize = getPaintingSize();
            for (int i = 0; i < faces.size(); i++) {
                int key = faces.keyAt(i);
                Face f = faces.get(key);
                PhotoFace face = new PhotoFace(f, this.facesBitmap, targetSize, isSidewardOrientation());
                if (face.isSufficient()) {
                    result.add(face);
                }
            }
            this.faces = result;
            if (faceDetector == null) {
                return;
            }
            faceDetector.release();
        } catch (Throwable e2) {
            FileLog.e(e2);
            if (faceDetector == null) {
                return;
            }
            faceDetector.release();
        }
    }

    private StickerPosition calculateStickerPosition(TLRPC.Document document) {
        MediaController.CropState cropState;
        float baseScale;
        float rotation;
        ArrayList<PhotoFace> arrayList;
        TLRPC.TL_maskCoords maskCoords = null;
        int a = 0;
        while (true) {
            if (a >= document.attributes.size()) {
                break;
            }
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else {
                maskCoords = attribute.mask_coords;
                break;
            }
        }
        if (this.currentCropState != null) {
            rotation = -(cropState.transformRotation + this.currentCropState.cropRotate);
            baseScale = 0.75f / this.currentCropState.cropScale;
        } else {
            rotation = 0.0f;
            baseScale = 0.75f;
        }
        StickerPosition defaultPosition = new StickerPosition(centerPositionForEntity(), baseScale, rotation);
        if (maskCoords == null || (arrayList = this.faces) == null) {
            return defaultPosition;
        }
        if (arrayList.size() == 0) {
            return defaultPosition;
        }
        int anchor = maskCoords.n;
        PhotoFace face = getRandomFaceWithVacantAnchor(anchor, document.id, maskCoords);
        if (face == null) {
            return defaultPosition;
        }
        Point referencePoint = face.getPointForAnchor(anchor);
        float referenceWidth = face.getWidthForAnchor(anchor);
        float angle = face.getAngle();
        Size baseSize = baseStickerSize();
        double d = referenceWidth / baseSize.width;
        double d2 = maskCoords.zoom;
        Double.isNaN(d);
        float scale = (float) (d * d2);
        float radAngle = (float) Math.toRadians(angle);
        double d3 = radAngle;
        Double.isNaN(d3);
        double sin = Math.sin(1.5707963267948966d - d3);
        double d4 = referenceWidth;
        Double.isNaN(d4);
        float xCompX = (float) (sin * d4 * maskCoords.x);
        double d5 = radAngle;
        Double.isNaN(d5);
        double cos = Math.cos(1.5707963267948966d - d5);
        double d6 = referenceWidth;
        Double.isNaN(d6);
        float xCompY = (float) (cos * d6 * maskCoords.x);
        double d7 = radAngle;
        Double.isNaN(d7);
        double cos2 = Math.cos(d7 + 1.5707963267948966d);
        double d8 = referenceWidth;
        Double.isNaN(d8);
        float yCompX = (float) (cos2 * d8 * maskCoords.y);
        double d9 = radAngle;
        Double.isNaN(d9);
        double sin2 = Math.sin(d9 + 1.5707963267948966d);
        double d10 = referenceWidth;
        Double.isNaN(d10);
        float yCompY = (float) (sin2 * d10 * maskCoords.y);
        float x = referencePoint.x + xCompX + yCompX;
        float y = referencePoint.y + xCompY + yCompY;
        return new StickerPosition(new Point(x, y), scale, angle);
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int anchor, long documentId, TLRPC.TL_maskCoords maskCoords) {
        if (anchor < 0 || anchor > 3 || this.faces.isEmpty()) {
            return null;
        }
        int count = this.faces.size();
        int randomIndex = Utilities.random.nextInt(count);
        int i = randomIndex;
        for (int remaining = count; remaining > 0; remaining--) {
            PhotoFace face = this.faces.get(i);
            if (isFaceAnchorOccupied(face, anchor, documentId, maskCoords)) {
                i = (i + 1) % count;
            } else {
                return face;
            }
        }
        return null;
    }

    private boolean isFaceAnchorOccupied(PhotoFace face, int anchor, long documentId, TLRPC.TL_maskCoords maskCoords) {
        Point anchorPoint = face.getPointForAnchor(anchor);
        if (anchorPoint == null) {
            return true;
        }
        float minDistance = face.getWidthForAnchor(0) * 1.1f;
        for (int index = 0; index < this.entitiesView.getChildCount(); index++) {
            View view = this.entitiesView.getChildAt(index);
            if (view instanceof StickerView) {
                StickerView stickerView = (StickerView) view;
                if (stickerView.getAnchor() != anchor) {
                    continue;
                } else {
                    Point location = stickerView.getPosition();
                    float distance = (float) Math.hypot(location.x - anchorPoint.x, location.y - anchorPoint.y);
                    if ((documentId == stickerView.getSticker().id || this.faces.size() > 1) && distance < minDistance) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* loaded from: classes5.dex */
    public static class StickerPosition {
        private float angle;
        private Point position;
        private float scale;

        StickerPosition(Point position, float scale, float angle) {
            this.position = position;
            this.scale = scale;
            this.angle = angle;
        }
    }
}
