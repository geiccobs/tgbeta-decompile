package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;
/* loaded from: classes5.dex */
public class PaintingOverlay extends FrameLayout {
    private Drawable backgroundDrawable;
    private boolean ignoreLayout;
    private HashMap<View, VideoEditedInfo.MediaEntity> mediaEntityViews;
    private Bitmap paintBitmap;

    public PaintingOverlay(Context context) {
        super(context);
    }

    public void setData(String paintPath, ArrayList<VideoEditedInfo.MediaEntity> entities, boolean isVideo, boolean startAfterSet) {
        setEntities(entities, isVideo, startAfterSet);
        if (paintPath != null) {
            this.paintBitmap = BitmapFactory.decodeFile(paintPath);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(this.paintBitmap);
            this.backgroundDrawable = bitmapDrawable;
            setBackground(bitmapDrawable);
            return;
        }
        this.paintBitmap = null;
        this.backgroundDrawable = null;
        setBackground(null);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.ignoreLayout = true;
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        if (this.mediaEntityViews != null) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int N = getChildCount();
            for (int a = 0; a < N; a++) {
                View child = getChildAt(a);
                VideoEditedInfo.MediaEntity entity = this.mediaEntityViews.get(child);
                if (entity != null) {
                    if (child instanceof EditTextOutline) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(entity.viewWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
                        float sc = (entity.textViewWidth * width) / entity.viewWidth;
                        child.setScaleX(entity.scale * sc);
                        child.setScaleY(entity.scale * sc);
                    } else {
                        child.measure(View.MeasureSpec.makeMeasureSpec((int) (width * entity.width), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (height * entity.height), C.BUFFER_FLAG_ENCRYPTED));
                    }
                }
            }
        }
        this.ignoreLayout = false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int y;
        int x;
        if (this.mediaEntityViews != null) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int N = getChildCount();
            for (int a = 0; a < N; a++) {
                View child = getChildAt(a);
                VideoEditedInfo.MediaEntity entity = this.mediaEntityViews.get(child);
                if (entity != null) {
                    if (child instanceof EditTextOutline) {
                        x = ((int) (width * entity.textViewX)) - (child.getMeasuredWidth() / 2);
                        y = ((int) (height * entity.textViewY)) - (child.getMeasuredHeight() / 2);
                    } else {
                        x = (int) (width * entity.x);
                        y = (int) (height * entity.y);
                    }
                    child.layout(x, y, child.getMeasuredWidth() + x, child.getMeasuredHeight() + y);
                }
            }
        }
    }

    public void reset() {
        this.paintBitmap = null;
        this.backgroundDrawable = null;
        setBackground(null);
        HashMap<View, VideoEditedInfo.MediaEntity> hashMap = this.mediaEntityViews;
        if (hashMap != null) {
            hashMap.clear();
        }
        removeAllViews();
    }

    public void showAll() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).setVisibility(0);
        }
        setBackground(this.backgroundDrawable);
    }

    public void hideEntities() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).setVisibility(4);
        }
    }

    public void hideBitmap() {
        setBackground(null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setEntities(ArrayList<VideoEditedInfo.MediaEntity> entities, boolean isVideo, boolean startAfterSet) {
        reset();
        this.mediaEntityViews = new HashMap<>();
        if (entities != null && !entities.isEmpty()) {
            int N = entities.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = entities.get(a);
                View child = null;
                if (entity.type == 0) {
                    BackupImageView imageView = new BackupImageView(getContext());
                    imageView.setAspectFit(true);
                    ImageReceiver imageReceiver = imageView.getImageReceiver();
                    if (isVideo) {
                        imageReceiver.setAllowDecodeSingleFrame(true);
                        imageReceiver.setAllowStartLottieAnimation(false);
                        if (startAfterSet) {
                            imageReceiver.setDelegate(PaintingOverlay$$ExternalSyntheticLambda0.INSTANCE);
                        }
                    }
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(entity.document.thumbs, 90);
                    imageReceiver.setImage(ImageLocation.getForDocument(entity.document), (String) null, ImageLocation.getForDocument(thumb, entity.document), (String) null, "webp", entity.parentObject, 1);
                    if ((entity.subType & 2) != 0) {
                        imageView.setScaleX(-1.0f);
                    }
                    child = imageView;
                    entity.view = imageView;
                } else if (entity.type == 1) {
                    EditTextOutline editText = new EditTextOutline(getContext()) { // from class: org.telegram.ui.Components.PaintingOverlay.1
                        @Override // org.telegram.ui.Components.EditTextEffects, android.view.View
                        public boolean dispatchTouchEvent(MotionEvent event) {
                            return false;
                        }

                        @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                        public boolean onTouchEvent(MotionEvent event) {
                            return false;
                        }
                    };
                    editText.setBackgroundColor(0);
                    editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
                    editText.setTextSize(0, entity.fontSize);
                    editText.setText(entity.text);
                    editText.setTypeface(null, 1);
                    editText.setGravity(17);
                    editText.setHorizontallyScrolling(false);
                    editText.setImeOptions(268435456);
                    editText.setFocusableInTouchMode(true);
                    editText.setEnabled(false);
                    editText.setInputType(editText.getInputType() | 16384);
                    if (Build.VERSION.SDK_INT >= 23) {
                        editText.setBreakStrategy(0);
                    }
                    if ((1 & entity.subType) != 0) {
                        editText.setTextColor(-1);
                        editText.setStrokeColor(entity.color);
                        editText.setFrameColor(0);
                        editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                    } else if ((entity.subType & 4) != 0) {
                        editText.setTextColor(-16777216);
                        editText.setStrokeColor(0);
                        editText.setFrameColor(entity.color);
                        editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                    } else {
                        editText.setTextColor(entity.color);
                        editText.setStrokeColor(0);
                        editText.setFrameColor(0);
                        editText.setShadowLayer(5.0f, 0.0f, 1.0f, 1711276032);
                    }
                    child = editText;
                    entity.view = editText;
                }
                if (child != null) {
                    addView(child);
                    double d = -entity.rotation;
                    Double.isNaN(d);
                    child.setRotation((float) ((d / 3.141592653589793d) * 180.0d));
                    this.mediaEntityViews.put(child, entity);
                }
            }
        }
    }

    public static /* synthetic */ void lambda$setEntities$0(ImageReceiver imageReceiver1, boolean set, boolean thumb, boolean memCache) {
        RLottieDrawable drawable;
        if (set && !thumb && (drawable = imageReceiver1.getLottieAnimation()) != null) {
            drawable.start();
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.paintBitmap = bitmap;
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        this.backgroundDrawable = bitmapDrawable;
        setBackground(bitmapDrawable);
    }

    public Bitmap getBitmap() {
        return this.paintBitmap;
    }

    @Override // android.view.View
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        Drawable drawable = this.backgroundDrawable;
        if (drawable != null) {
            drawable.setAlpha((int) (255.0f * alpha));
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getParent() == this) {
                child.setAlpha(alpha);
            }
        }
    }

    public Bitmap getThumb() {
        float w = getMeasuredWidth();
        float h = getMeasuredHeight();
        float scale = Math.max(w / AndroidUtilities.dp(120.0f), h / AndroidUtilities.dp(120.0f));
        Bitmap bitmap = Bitmap.createBitmap((int) (w / scale), (int) (h / scale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1.0f / scale, 1.0f / scale);
        draw(canvas);
        return bitmap;
    }
}
