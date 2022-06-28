package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Rect;
import org.telegram.ui.Components.Size;
/* loaded from: classes5.dex */
public class StickerView extends EntityView {
    private int anchor;
    private Size baseSize;
    private ImageReceiver centerImage;
    private FrameLayoutDrawer containerView;
    private boolean mirrored;
    private Object parentObject;
    private TLRPC.Document sticker;

    /* loaded from: classes5.dex */
    public class FrameLayoutDrawer extends FrameLayout {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FrameLayoutDrawer(Context context) {
            super(context);
            StickerView.this = r1;
            setWillNotDraw(false);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            StickerView.this.stickerDraw(canvas);
        }
    }

    public StickerView(Context context, Point position, Size baseSize, TLRPC.Document sticker, Object parentObject) {
        this(context, position, 0.0f, 1.0f, baseSize, sticker, parentObject);
    }

    public StickerView(Context context, Point position, float angle, float scale, Size baseSize, TLRPC.Document sticker, Object parentObject) {
        super(context, position);
        this.anchor = -1;
        this.mirrored = false;
        this.centerImage = new ImageReceiver();
        setRotation(angle);
        setScale(scale);
        this.sticker = sticker;
        this.baseSize = baseSize;
        this.parentObject = parentObject;
        int a = 0;
        while (true) {
            if (a >= sticker.attributes.size()) {
                break;
            }
            TLRPC.DocumentAttribute attribute = sticker.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.mask_coords != null) {
                this.anchor = attribute.mask_coords.n;
            }
        }
        FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(context);
        this.containerView = frameLayoutDrawer;
        addView(frameLayoutDrawer, LayoutHelper.createFrame(-1, -1.0f));
        this.centerImage.setAspectFit(true);
        this.centerImage.setInvalidateAll(true);
        this.centerImage.setParentView(this.containerView);
        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90);
        this.centerImage.setImage(ImageLocation.getForDocument(sticker), (String) null, ImageLocation.getForDocument(thumb, sticker), (String) null, "webp", parentObject, 1);
        this.centerImage.setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.Components.Paint.Views.StickerView$$ExternalSyntheticLambda0
            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                StickerView.this.m2790lambda$new$0$orgtelegramuiComponentsPaintViewsStickerView(imageReceiver, z, z2, z3);
            }

            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        });
        updatePosition();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Paint-Views-StickerView */
    public /* synthetic */ void m2790lambda$new$0$orgtelegramuiComponentsPaintViewsStickerView(ImageReceiver imageReceiver, boolean set, boolean isThumb, boolean memCache) {
        RLottieDrawable drawable;
        if (set && !isThumb && (drawable = imageReceiver.getLottieAnimation()) != null) {
            didSetAnimatedSticker(drawable);
        }
    }

    public StickerView(Context context, StickerView stickerView, Point position) {
        this(context, position, stickerView.getRotation(), stickerView.getScale(), stickerView.baseSize, stickerView.sticker, stickerView.parentObject);
        if (stickerView.mirrored) {
            mirror();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.centerImage.onDetachedFromWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.centerImage.onAttachedToWindow();
    }

    public int getAnchor() {
        return this.anchor;
    }

    public void mirror() {
        this.mirrored = !this.mirrored;
        this.containerView.invalidate();
    }

    public boolean isMirrored() {
        return this.mirrored;
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView
    public void updatePosition() {
        float halfWidth = this.baseSize.width / 2.0f;
        float halfHeight = this.baseSize.height / 2.0f;
        setX(this.position.x - halfWidth);
        setY(this.position.y - halfHeight);
        updateSelectionView();
    }

    protected void didSetAnimatedSticker(RLottieDrawable drawable) {
    }

    protected void stickerDraw(Canvas canvas) {
        if (this.containerView == null) {
            return;
        }
        canvas.save();
        if (this.mirrored) {
            canvas.scale(-1.0f, 1.0f);
            canvas.translate(-this.baseSize.width, 0.0f);
        }
        this.centerImage.setImageCoords(0.0f, 0.0f, (int) this.baseSize.width, (int) this.baseSize.height);
        this.centerImage.draw(canvas);
        canvas.restore();
    }

    public long getDuration() {
        RLottieDrawable rLottieDrawable = this.centerImage.getLottieAnimation();
        if (rLottieDrawable != null) {
            return rLottieDrawable.getDuration();
        }
        AnimatedFileDrawable animatedFileDrawable = this.centerImage.getAnimation();
        if (animatedFileDrawable != null) {
            return animatedFileDrawable.getDurationMs();
        }
        return 0L;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) this.baseSize.width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) this.baseSize.height, C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView
    protected Rect getSelectionBounds() {
        ViewGroup parentView = (ViewGroup) getParent();
        float scale = parentView.getScaleX();
        float side = getMeasuredWidth() * (getScale() + 0.4f);
        return new Rect((this.position.x - (side / 2.0f)) * scale, (this.position.y - (side / 2.0f)) * scale, side * scale, side * scale);
    }

    @Override // org.telegram.ui.Components.Paint.Views.EntityView
    protected EntityView.SelectionView createSelectionView() {
        return new StickerViewSelectionView(getContext());
    }

    public TLRPC.Document getSticker() {
        return this.sticker;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public Size getBaseSize() {
        return this.baseSize;
    }

    /* loaded from: classes5.dex */
    public class StickerViewSelectionView extends EntityView.SelectionView {
        private Paint arcPaint = new Paint(1);
        private RectF arcRect = new RectF();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public StickerViewSelectionView(Context context) {
            super(context);
            StickerView.this = this$0;
            this.arcPaint.setColor(-1);
            this.arcPaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
            this.arcPaint.setStyle(Paint.Style.STROKE);
        }

        @Override // org.telegram.ui.Components.Paint.Views.EntityView.SelectionView
        protected int pointInsideHandle(float x, float y) {
            float thickness = AndroidUtilities.dp(1.0f);
            float radius = AndroidUtilities.dp(19.5f);
            float inset = radius + thickness;
            float middle = ((getMeasuredHeight() - (inset * 2.0f)) / 2.0f) + inset;
            if (x > inset - radius && y > middle - radius && x < inset + radius && y < middle + radius) {
                return 1;
            }
            if (x > ((getMeasuredWidth() - (inset * 2.0f)) + inset) - radius && y > middle - radius && x < (getMeasuredWidth() - (inset * 2.0f)) + inset + radius && y < middle + radius) {
                return 2;
            }
            float selectionRadius = getMeasuredWidth() / 2.0f;
            if (Math.pow(x - selectionRadius, 2.0d) + Math.pow(y - selectionRadius, 2.0d) < Math.pow(selectionRadius, 2.0d)) {
                return 3;
            }
            return 0;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float thickness = AndroidUtilities.dp(1.0f);
            float radius = AndroidUtilities.dp(4.5f);
            float inset = radius + thickness + AndroidUtilities.dp(15.0f);
            float mainRadius = (getMeasuredWidth() / 2) - inset;
            this.arcRect.set(inset, inset, (mainRadius * 2.0f) + inset, (mainRadius * 2.0f) + inset);
            for (int i = 0; i < 48; i++) {
                canvas.drawArc(this.arcRect, (4.0f + 4.0f) * i, 4.0f, false, this.arcPaint);
            }
            canvas.drawCircle(inset, inset + mainRadius, radius, this.dotPaint);
            canvas.drawCircle(inset, inset + mainRadius, radius, this.dotStrokePaint);
            canvas.drawCircle((mainRadius * 2.0f) + inset, inset + mainRadius, radius, this.dotPaint);
            canvas.drawCircle((2.0f * mainRadius) + inset, inset + mainRadius, radius, this.dotStrokePaint);
        }
    }
}
