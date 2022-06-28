package org.telegram.ui.Components.spoilers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.SharedConfig;
/* loaded from: classes5.dex */
public class SpoilerEffectBitmapFactory {
    private static SpoilerEffectBitmapFactory factory;
    Bitmap backgroundBitmap;
    Bitmap bufferBitmap;
    boolean isRunning;
    long lastUpdateTime;
    private Bitmap shaderBitmap;
    Canvas shaderCanvas;
    Paint shaderPaint;
    ArrayList<SpoilerEffect> shaderSpoilerEffects;
    int size;
    final DispatchQueue dispatchQueue = new DispatchQueue("SpoilerEffectBitmapFactory");
    Matrix localMatrix = new Matrix();

    public static SpoilerEffectBitmapFactory getInstance() {
        if (factory == null) {
            factory = new SpoilerEffectBitmapFactory();
        }
        return factory;
    }

    private SpoilerEffectBitmapFactory() {
        int maxSize = AndroidUtilities.dp(SharedConfig.getDevicePerformanceClass() == 2 ? 200.0f : 150.0f);
        int min = (int) Math.min(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f, maxSize);
        this.size = min;
        if (min < AndroidUtilities.dp(100.0f)) {
            this.size = AndroidUtilities.dp(100.0f);
        }
    }

    public Paint getPaint() {
        if (this.shaderBitmap == null) {
            int i = this.size;
            this.shaderBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
            this.shaderCanvas = new Canvas(this.shaderBitmap);
            this.shaderPaint = new Paint();
            this.shaderSpoilerEffects = new ArrayList<>(100);
            this.shaderPaint.setShader(new BitmapShader(this.shaderBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            int i2 = this.size;
            int step = (int) (i2 / 10.0f);
            int particleCount = (int) ((i2 / AndroidUtilities.dp(200.0f)) * 60.0f);
            for (int i3 = 0; i3 < 10; i3++) {
                for (int j = 0; j < 10; j++) {
                    SpoilerEffect shaderSpoilerEffect = new SpoilerEffect();
                    shaderSpoilerEffect.setBounds(step * i3, (step * j) - AndroidUtilities.dp(5.0f), (step * i3) + step + AndroidUtilities.dp(3.0f), (step * j) + step + AndroidUtilities.dp(5.0f));
                    shaderSpoilerEffect.drawPoints = true;
                    shaderSpoilerEffect.particlePoints = (float[][]) Array.newInstance(float.class, SpoilerEffect.ALPHAS.length, particleCount * 2);
                    shaderSpoilerEffect.setMaxParticlesCount(particleCount);
                    shaderSpoilerEffect.setColor(-1);
                    this.shaderSpoilerEffects.add(shaderSpoilerEffect);
                }
            }
            for (int i4 = 0; i4 < 10; i4++) {
                for (int j2 = 0; j2 < 10; j2++) {
                    this.shaderSpoilerEffects.get((i4 * 10) + j2).draw(this.shaderCanvas);
                }
            }
            this.shaderPaint.setShader(new BitmapShader(this.shaderBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            this.lastUpdateTime = System.currentTimeMillis();
        }
        return this.shaderPaint;
    }

    public void checkUpdate() {
        long time = System.currentTimeMillis();
        if (time - this.lastUpdateTime > 32 && !this.isRunning) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.isRunning = true;
            final Bitmap bufferBitmapFinall = this.bufferBitmap;
            this.dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.spoilers.SpoilerEffectBitmapFactory$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SpoilerEffectBitmapFactory.this.m3223x4b015506(bufferBitmapFinall);
                }
            });
        }
    }

    /* renamed from: lambda$checkUpdate$1$org-telegram-ui-Components-spoilers-SpoilerEffectBitmapFactory */
    public /* synthetic */ void m3223x4b015506(Bitmap bufferBitmapFinall) {
        Bitmap bitmap = bufferBitmapFinall;
        if (bitmap == null) {
            int i = this.size;
            bitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        }
        Bitmap bitmap2 = this.backgroundBitmap;
        if (bitmap2 == null) {
            int i2 = this.size;
            this.backgroundBitmap = Bitmap.createBitmap(i2, i2, Bitmap.Config.ARGB_8888);
        } else {
            bitmap2.eraseColor(0);
        }
        Canvas shaderCanvas = new Canvas(bitmap);
        Canvas backgroundCanvas = new Canvas(this.backgroundBitmap);
        for (int i3 = 0; i3 < 10; i3++) {
            for (int j = 0; j < 10; j++) {
                this.shaderSpoilerEffects.get((i3 * 10) + j).draw(backgroundCanvas);
            }
        }
        bitmap.eraseColor(0);
        shaderCanvas.drawBitmap(this.backgroundBitmap, 0.0f, 0.0f, (Paint) null);
        final Bitmap finalBitmap = bitmap;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.spoilers.SpoilerEffectBitmapFactory$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SpoilerEffectBitmapFactory.this.m3222xcca05127(finalBitmap);
            }
        });
    }

    /* renamed from: lambda$checkUpdate$0$org-telegram-ui-Components-spoilers-SpoilerEffectBitmapFactory */
    public /* synthetic */ void m3222xcca05127(Bitmap finalBitmap) {
        this.bufferBitmap = this.shaderBitmap;
        this.shaderBitmap = finalBitmap;
        this.shaderPaint.setShader(new BitmapShader(this.shaderBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        this.isRunning = false;
    }
}
