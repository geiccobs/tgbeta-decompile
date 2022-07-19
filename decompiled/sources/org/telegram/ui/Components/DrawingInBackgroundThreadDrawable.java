package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes3.dex */
public class DrawingInBackgroundThreadDrawable implements NotificationCenter.NotificationCenterDelegate {
    private static DispatchQueue backgroundQueue;
    boolean attachedToWindow;
    Bitmap backgroundBitmap;
    Canvas backgroundCanvas;
    Bitmap bitmap;
    Canvas bitmapCanvas;
    private boolean bitmapUpdating;
    private int currentLayerNum;
    private int currentOpenedLayerFlags;
    boolean error;
    int frameGuid;
    int height;
    private int lastFrameId;
    Bitmap nextRenderingBitmap;
    Canvas nextRenderingCanvas;
    protected boolean paused;
    private boolean reset;
    int width;
    private Paint paint = new Paint(1);
    Runnable bitmapCreateTask = new Runnable() { // from class: org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.1
        /* JADX WARN: Code restructure failed: missing block: B:7:0x001a, code lost:
            if (r1.backgroundBitmap.getHeight() == org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this.height) goto L12;
         */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                r4 = this;
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch: java.lang.Exception -> L50
                if (r0 == 0) goto L1c
                int r0 = r0.getWidth()     // Catch: java.lang.Exception -> L50
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r1 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                int r2 = r1.width     // Catch: java.lang.Exception -> L50
                if (r0 != r2) goto L1c
                android.graphics.Bitmap r0 = r1.backgroundBitmap     // Catch: java.lang.Exception -> L50
                int r0 = r0.getHeight()     // Catch: java.lang.Exception -> L50
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r1 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                int r1 = r1.height     // Catch: java.lang.Exception -> L50
                if (r0 == r1) goto L40
            L1c:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch: java.lang.Exception -> L50
                if (r0 == 0) goto L25
                r0.recycle()     // Catch: java.lang.Exception -> L50
            L25:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                int r1 = r0.width     // Catch: java.lang.Exception -> L50
                int r2 = r0.height     // Catch: java.lang.Exception -> L50
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch: java.lang.Exception -> L50
                android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r2, r3)     // Catch: java.lang.Exception -> L50
                r0.backgroundBitmap = r1     // Catch: java.lang.Exception -> L50
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                android.graphics.Canvas r1 = new android.graphics.Canvas     // Catch: java.lang.Exception -> L50
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r2 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                android.graphics.Bitmap r2 = r2.backgroundBitmap     // Catch: java.lang.Exception -> L50
                r1.<init>(r2)     // Catch: java.lang.Exception -> L50
                r0.backgroundCanvas = r1     // Catch: java.lang.Exception -> L50
            L40:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                android.graphics.Bitmap r0 = r0.backgroundBitmap     // Catch: java.lang.Exception -> L50
                r1 = 0
                r0.eraseColor(r1)     // Catch: java.lang.Exception -> L50
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this     // Catch: java.lang.Exception -> L50
                android.graphics.Canvas r1 = r0.backgroundCanvas     // Catch: java.lang.Exception -> L50
                r0.drawInBackground(r1)     // Catch: java.lang.Exception -> L50
                goto L59
            L50:
                r0 = move-exception
                org.telegram.messenger.FileLog.e(r0)
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this
                r1 = 1
                r0.error = r1
            L59:
                org.telegram.ui.Components.DrawingInBackgroundThreadDrawable r0 = org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.this
                java.lang.Runnable r0 = r0.uiFrameRunnable
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.AnonymousClass1.run():void");
        }
    };
    Runnable uiFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.DrawingInBackgroundThreadDrawable.2
        @Override // java.lang.Runnable
        public void run() {
            DrawingInBackgroundThreadDrawable.this.bitmapUpdating = false;
            DrawingInBackgroundThreadDrawable.this.onFrameReady();
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable = DrawingInBackgroundThreadDrawable.this;
            if (drawingInBackgroundThreadDrawable.attachedToWindow) {
                if (drawingInBackgroundThreadDrawable.frameGuid != drawingInBackgroundThreadDrawable.lastFrameId) {
                    return;
                }
                DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable2 = DrawingInBackgroundThreadDrawable.this;
                Bitmap bitmap = drawingInBackgroundThreadDrawable2.bitmap;
                Canvas canvas = drawingInBackgroundThreadDrawable2.bitmapCanvas;
                drawingInBackgroundThreadDrawable2.bitmap = drawingInBackgroundThreadDrawable2.nextRenderingBitmap;
                drawingInBackgroundThreadDrawable2.bitmapCanvas = drawingInBackgroundThreadDrawable2.nextRenderingCanvas;
                drawingInBackgroundThreadDrawable2.nextRenderingBitmap = drawingInBackgroundThreadDrawable2.backgroundBitmap;
                drawingInBackgroundThreadDrawable2.nextRenderingCanvas = drawingInBackgroundThreadDrawable2.backgroundCanvas;
                drawingInBackgroundThreadDrawable2.backgroundBitmap = bitmap;
                drawingInBackgroundThreadDrawable2.backgroundCanvas = canvas;
                return;
            }
            Bitmap bitmap2 = drawingInBackgroundThreadDrawable.backgroundBitmap;
            if (bitmap2 == null) {
                return;
            }
            bitmap2.recycle();
            DrawingInBackgroundThreadDrawable.this.backgroundBitmap = null;
        }
    };

    public void drawInBackground(Canvas canvas) {
        throw null;
    }

    protected void drawInUiThread(Canvas canvas) {
        throw null;
    }

    public void onFrameReady() {
    }

    public void onPaused() {
    }

    public void onResume() {
    }

    public void prepareDraw(long j) {
        throw null;
    }

    public DrawingInBackgroundThreadDrawable() {
        if (backgroundQueue == null) {
            backgroundQueue = new DispatchQueue("draw_background_queue");
        }
    }

    public void draw(Canvas canvas, long j, int i, int i2, float f) {
        if (this.error) {
            return;
        }
        this.height = i2;
        this.width = i;
        Bitmap bitmap = this.bitmap;
        if ((bitmap == null && this.nextRenderingBitmap == null) || this.reset) {
            this.reset = false;
            if (bitmap != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.bitmap);
                AndroidUtilities.recycleBitmaps(arrayList);
                this.bitmap = null;
            }
            Bitmap bitmap2 = this.nextRenderingBitmap;
            if (bitmap2 == null || bitmap2.getHeight() != this.height || this.nextRenderingBitmap.getWidth() != this.width) {
                this.nextRenderingBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
                this.nextRenderingCanvas = new Canvas(this.nextRenderingBitmap);
            } else {
                this.nextRenderingBitmap.eraseColor(0);
            }
            drawInUiThread(this.nextRenderingCanvas);
        }
        if (!this.bitmapUpdating && !this.paused) {
            this.bitmapUpdating = true;
            prepareDraw(j);
            this.lastFrameId = this.frameGuid;
            backgroundQueue.postRunnable(this.bitmapCreateTask);
        }
        Bitmap bitmap3 = this.bitmap;
        if (bitmap3 == null && this.nextRenderingBitmap == null) {
            return;
        }
        if (bitmap3 == null) {
            bitmap3 = this.nextRenderingBitmap;
        }
        this.paint.setAlpha((int) (f * 255.0f));
        canvas.drawBitmap(bitmap3, 0.0f, 0.0f, this.paint);
    }

    public void onAttachToWindow() {
        this.attachedToWindow = true;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
    }

    public void onDetachFromWindow() {
        ArrayList arrayList = new ArrayList();
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            arrayList.add(bitmap);
        }
        Bitmap bitmap2 = this.nextRenderingBitmap;
        if (bitmap2 != null) {
            arrayList.add(bitmap2);
        }
        this.bitmap = null;
        this.nextRenderingBitmap = null;
        AndroidUtilities.recycleBitmaps(arrayList);
        this.attachedToWindow = false;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        if (i == NotificationCenter.stopAllHeavyOperations) {
            Integer num = (Integer) objArr[0];
            if (this.currentLayerNum >= num.intValue()) {
                return;
            }
            int intValue = num.intValue() | this.currentOpenedLayerFlags;
            this.currentOpenedLayerFlags = intValue;
            if (intValue == 0 || this.paused) {
                return;
            }
            this.paused = true;
            onPaused();
        } else if (i != NotificationCenter.startAllHeavyOperations) {
        } else {
            Integer num2 = (Integer) objArr[0];
            if (this.currentLayerNum >= num2.intValue() || (i3 = this.currentOpenedLayerFlags) == 0) {
                return;
            }
            int intValue2 = (num2.intValue() ^ (-1)) & i3;
            this.currentOpenedLayerFlags = intValue2;
            if (intValue2 != 0 || !this.paused) {
                return;
            }
            this.paused = false;
            onResume();
        }
    }

    public void reset() {
        this.reset = true;
        this.frameGuid++;
        if (this.bitmap != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.bitmap);
            this.bitmap = null;
            AndroidUtilities.recycleBitmaps(arrayList);
        }
    }
}
