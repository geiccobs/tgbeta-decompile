package org.telegram.ui.Components.spoilers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.TextStyleSpan;
/* loaded from: classes5.dex */
public class SpoilerEffect extends Drawable {
    private static final int FPS = 30;
    private static final float KEYPOINT_DELTA = 5.0f;
    private static final int RAND_REPEAT = 14;
    private static final float VERTICAL_PADDING_DP = 2.5f;
    private static final int renderDelayMs = 34;
    private static Paint xRefPaint;
    public boolean drawPoints;
    private boolean enableAlpha;
    private boolean invalidateParent;
    private boolean isLowDevice;
    private List<Long> keyPoints;
    private int lastColor;
    private long lastDrawTime;
    private View mParent;
    private int maxParticles;
    private Runnable onRippleEndCallback;
    private Paint[] particlePaints;
    float[][] particlePoints;
    private int[] renderCount;
    private boolean reverseAnimator;
    private ValueAnimator rippleAnimator;
    private float rippleMaxRadius;
    private float rippleX;
    private float rippleY;
    private boolean shouldInvalidateColor;
    private boolean suppressUpdates;
    private RectF visibleRect;
    public static final int MAX_PARTICLES_PER_ENTITY = measureMaxParticlesCount();
    public static final int PARTICLES_PER_CHARACTER = measureParticlesPerCharacter();
    public static final float[] ALPHAS = {0.3f, 0.6f, 1.0f};
    private static Path tempPath = new Path();
    private Stack<Particle> particlesPool = new Stack<>();
    private float[] particleRands = new float[14];
    private ArrayList<Particle> particles = new ArrayList<>();
    private float rippleProgress = -1.0f;
    private List<RectF> spaces = new ArrayList();
    private int mAlpha = 255;
    private TimeInterpolator rippleInterpolator = SpoilerEffect$$ExternalSyntheticLambda0.INSTANCE;

    public static /* synthetic */ float lambda$new$0(float input) {
        return input;
    }

    private static int measureParticlesPerCharacter() {
        switch (SharedConfig.getDevicePerformanceClass()) {
            case 2:
                return 30;
            default:
                return 10;
        }
    }

    private static int measureMaxParticlesCount() {
        switch (SharedConfig.getDevicePerformanceClass()) {
            case 2:
                return 150;
            default:
                return 100;
        }
    }

    public SpoilerEffect() {
        float[] fArr = ALPHAS;
        this.particlePaints = new Paint[fArr.length];
        this.particlePoints = (float[][]) Array.newInstance(float.class, fArr.length, MAX_PARTICLES_PER_ENTITY * 2);
        this.renderCount = new int[fArr.length];
        for (int i = 0; i < ALPHAS.length; i++) {
            this.particlePaints[i] = new Paint();
            if (i == 0) {
                this.particlePaints[i].setStrokeWidth(AndroidUtilities.dp(1.4f));
                this.particlePaints[i].setStyle(Paint.Style.STROKE);
                this.particlePaints[i].setStrokeCap(Paint.Cap.ROUND);
            } else {
                this.particlePaints[i].setStrokeWidth(AndroidUtilities.dp(1.2f));
                this.particlePaints[i].setStyle(Paint.Style.STROKE);
                this.particlePaints[i].setStrokeCap(Paint.Cap.ROUND);
            }
        }
        int i2 = SharedConfig.getDevicePerformanceClass();
        this.isLowDevice = i2 == 0;
        this.enableAlpha = true;
        setColor(0);
    }

    public void setSuppressUpdates(boolean suppressUpdates) {
        this.suppressUpdates = suppressUpdates;
        invalidateSelf();
    }

    public void setInvalidateParent(boolean invalidateParent) {
        this.invalidateParent = invalidateParent;
    }

    public void updateMaxParticles() {
        int width = getBounds().width() / AndroidUtilities.dp(6.0f);
        int i = PARTICLES_PER_CHARACTER;
        setMaxParticlesCount(MathUtils.clamp(width * i, i, MAX_PARTICLES_PER_ENTITY));
    }

    public void setOnRippleEndCallback(Runnable onRippleEndCallback) {
        this.onRippleEndCallback = onRippleEndCallback;
    }

    public void startRipple(float rX, float rY, float radMax) {
        startRipple(rX, rY, radMax, false);
    }

    public void startRipple(float rX, float rY, float radMax, boolean reverse) {
        this.rippleX = rX;
        this.rippleY = rY;
        this.rippleMaxRadius = radMax;
        float f = 1.0f;
        this.rippleProgress = reverse ? 1.0f : 0.0f;
        this.reverseAnimator = reverse;
        ValueAnimator valueAnimator = this.rippleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final int startAlpha = this.reverseAnimator ? 255 : this.particlePaints[ALPHAS.length - 1].getAlpha();
        float[] fArr = new float[2];
        fArr[0] = this.rippleProgress;
        if (reverse) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(MathUtils.clamp(this.rippleMaxRadius * 0.3f, 250.0f, 550.0f));
        this.rippleAnimator = duration;
        duration.setInterpolator(this.rippleInterpolator);
        this.rippleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.spoilers.SpoilerEffect$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                SpoilerEffect.this.m3221x4218b496(startAlpha, valueAnimator2);
            }
        });
        this.rippleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.spoilers.SpoilerEffect.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Iterator<Particle> it = SpoilerEffect.this.particles.iterator();
                while (it.hasNext()) {
                    Particle p = it.next();
                    if (SpoilerEffect.this.particlesPool.size() < SpoilerEffect.this.maxParticles) {
                        SpoilerEffect.this.particlesPool.push(p);
                    }
                    it.remove();
                }
                if (SpoilerEffect.this.onRippleEndCallback != null) {
                    SpoilerEffect.this.onRippleEndCallback.run();
                    SpoilerEffect.this.onRippleEndCallback = null;
                }
                SpoilerEffect.this.rippleAnimator = null;
                SpoilerEffect.this.invalidateSelf();
            }
        });
        this.rippleAnimator.start();
        invalidateSelf();
    }

    /* renamed from: lambda$startRipple$1$org-telegram-ui-Components-spoilers-SpoilerEffect */
    public /* synthetic */ void m3221x4218b496(int startAlpha, ValueAnimator animation) {
        float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
        this.rippleProgress = floatValue;
        setAlpha((int) (startAlpha * (1.0f - floatValue)));
        this.shouldInvalidateColor = true;
        invalidateSelf();
    }

    public void setRippleInterpolator(TimeInterpolator rippleInterpolator) {
        this.rippleInterpolator = rippleInterpolator;
    }

    public void setKeyPoints(List<Long> keyPoints) {
        this.keyPoints = keyPoints;
        invalidateSelf();
    }

    public void getRipplePath(Path path) {
        path.addCircle(this.rippleX, this.rippleY, this.rippleMaxRadius * MathUtils.clamp(this.rippleProgress, 0.0f, 1.0f), Path.Direction.CW);
    }

    public float getRippleProgress() {
        return this.rippleProgress;
    }

    public boolean shouldInvalidateColor() {
        boolean b = this.shouldInvalidateColor;
        this.shouldInvalidateColor = false;
        return b;
    }

    public void setRippleProgress(float rippleProgress) {
        ValueAnimator valueAnimator;
        this.rippleProgress = rippleProgress;
        if (rippleProgress == -1.0f && (valueAnimator = this.rippleAnimator) != null) {
            valueAnimator.cancel();
        }
        this.shouldInvalidateColor = true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        Iterator<Particle> it = this.particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            if (!getBounds().contains((int) p.x, (int) p.y)) {
                it.remove();
            }
            if (this.particlesPool.size() < this.maxParticles) {
                this.particlesPool.push(p);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        float rf;
        int np;
        Particle newParticle;
        int dt;
        float rf2;
        int i;
        long curTime;
        int i2;
        int i3;
        Particle particle;
        if (this.drawPoints) {
            long curTime2 = System.currentTimeMillis();
            int dt2 = (int) Math.min(curTime2 - this.lastDrawTime, 34L);
            this.lastDrawTime = curTime2;
            int left = getBounds().left;
            int top = getBounds().top;
            int right = getBounds().right;
            int bottom = getBounds().bottom;
            for (int i4 = 0; i4 < ALPHAS.length; i4++) {
                this.renderCount[i4] = 0;
            }
            int i5 = 0;
            while (i5 < this.particles.size()) {
                Particle particle2 = this.particles.get(i5);
                particle2.currentTime = Math.min(particle2.currentTime + dt2, particle2.lifeTime);
                if (particle2.currentTime >= particle2.lifeTime) {
                    particle = particle2;
                    curTime = curTime2;
                    i3 = i5;
                } else {
                    curTime = curTime2;
                    i3 = i5;
                    if (isOutOfBounds(left, top, right, bottom, particle2.x, particle2.y)) {
                        particle = particle2;
                    } else {
                        float hdt = (particle2.velocity * dt2) / 500.0f;
                        Particle.access$516(particle2, particle2.vecX * hdt);
                        Particle.access$616(particle2, particle2.vecY * hdt);
                        int alphaIndex = particle2.alpha;
                        this.particlePoints[alphaIndex][this.renderCount[alphaIndex] * 2] = particle2.x;
                        this.particlePoints[alphaIndex][(this.renderCount[alphaIndex] * 2) + 1] = particle2.y;
                        int[] iArr = this.renderCount;
                        iArr[alphaIndex] = iArr[alphaIndex] + 1;
                        i2 = i3;
                        i5 = i2 + 1;
                        curTime2 = curTime;
                    }
                }
                if (this.particlesPool.size() < this.maxParticles) {
                    this.particlesPool.push(particle);
                }
                this.particles.remove(i3);
                i2 = i3 - 1;
                i5 = i2 + 1;
                curTime2 = curTime;
            }
            int size = this.particles.size();
            int i6 = this.maxParticles;
            if (size < i6) {
                int np2 = i6 - this.particles.size();
                float f = -1.0f;
                Arrays.fill(this.particleRands, -1.0f);
                int i7 = 0;
                while (i7 < np2) {
                    float[] fArr = this.particleRands;
                    float rf3 = fArr[i7 % 14];
                    if (rf3 != f) {
                        rf = rf3;
                    } else {
                        float rf4 = Utilities.fastRandom.nextFloat();
                        fArr[i7 % 14] = rf4;
                        rf = rf4;
                    }
                    Particle newParticle2 = !this.particlesPool.isEmpty() ? this.particlesPool.pop() : new Particle();
                    int attempts = 0;
                    while (true) {
                        generateRandomLocation(newParticle2, i7);
                        int attempts2 = attempts + 1;
                        np = np2;
                        newParticle = newParticle2;
                        dt = dt2;
                        rf2 = rf;
                        i = i7;
                        if (isOutOfBounds(left, top, right, bottom, newParticle2.x, newParticle2.y) && attempts2 < 4) {
                            newParticle2 = newParticle;
                            attempts = attempts2;
                            rf = rf2;
                            np2 = np;
                            dt2 = dt;
                            i7 = i;
                        }
                    }
                    double d = rf2;
                    Double.isNaN(d);
                    double angleRad = ((d * 3.141592653589793d) * 2.0d) - 3.141592653589793d;
                    float vx = (float) Math.cos(angleRad);
                    float vy = (float) Math.sin(angleRad);
                    newParticle.vecX = vx;
                    newParticle.vecY = vy;
                    newParticle.currentTime = 0.0f;
                    newParticle.lifeTime = Math.abs(Utilities.fastRandom.nextInt(2000)) + 1000;
                    newParticle.velocity = (6.0f * rf2) + 4.0f;
                    newParticle.alpha = Utilities.fastRandom.nextInt(ALPHAS.length);
                    this.particles.add(newParticle);
                    int alphaIndex2 = newParticle.alpha;
                    this.particlePoints[alphaIndex2][this.renderCount[alphaIndex2] * 2] = newParticle.x;
                    this.particlePoints[alphaIndex2][(this.renderCount[alphaIndex2] * 2) + 1] = newParticle.y;
                    int[] iArr2 = this.renderCount;
                    iArr2[alphaIndex2] = iArr2[alphaIndex2] + 1;
                    i7 = i + 1;
                    np2 = np;
                    dt2 = dt;
                    f = -1.0f;
                }
            }
            for (int a = this.enableAlpha ? 0 : ALPHAS.length - 1; a < ALPHAS.length; a++) {
                int renderCount = 0;
                int off = 0;
                for (int i8 = 0; i8 < this.particles.size(); i8++) {
                    Particle p = this.particles.get(i8);
                    RectF rectF = this.visibleRect;
                    if ((rectF != null && !rectF.contains(p.x, p.y)) || (p.alpha != a && this.enableAlpha)) {
                        off++;
                    } else {
                        this.particlePoints[a][(i8 - off) * 2] = p.x;
                        this.particlePoints[a][((i8 - off) * 2) + 1] = p.y;
                        renderCount += 2;
                    }
                }
                canvas.drawPoints(this.particlePoints[a], 0, renderCount, this.particlePaints[a]);
            }
            return;
        }
        Paint shaderPaint = SpoilerEffectBitmapFactory.getInstance().getPaint();
        shaderPaint.setColorFilter(new PorterDuffColorFilter(this.lastColor, PorterDuff.Mode.SRC_IN));
        canvas.drawRect(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom, SpoilerEffectBitmapFactory.getInstance().getPaint());
        invalidateSelf();
        SpoilerEffectBitmapFactory.getInstance().checkUpdate();
    }

    public void setVisibleBounds(float left, float top, float right, float bottom) {
        if (this.visibleRect == null) {
            this.visibleRect = new RectF();
        }
        this.visibleRect.left = left;
        this.visibleRect.top = top;
        this.visibleRect.right = right;
        this.visibleRect.bottom = bottom;
        invalidateSelf();
    }

    private boolean isOutOfBounds(int left, int top, int right, int bottom, float x, float y) {
        if (x < left || x > right || y < AndroidUtilities.dp(VERTICAL_PADDING_DP) + top || y > bottom - AndroidUtilities.dp(VERTICAL_PADDING_DP)) {
            return true;
        }
        for (int i = 0; i < this.spaces.size(); i++) {
            if (this.spaces.get(i).contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    private void generateRandomLocation(Particle newParticle, int i) {
        List<Long> list = this.keyPoints;
        if (list != null && !list.isEmpty()) {
            float rf = this.particleRands[i % 14];
            long kp = this.keyPoints.get(Utilities.fastRandom.nextInt(this.keyPoints.size())).longValue();
            newParticle.x = (((float) (getBounds().left + (kp >> 16))) + (AndroidUtilities.dp(KEYPOINT_DELTA) * rf)) - AndroidUtilities.dp(VERTICAL_PADDING_DP);
            newParticle.y = (((float) (getBounds().top + (65535 & kp))) + (AndroidUtilities.dp(KEYPOINT_DELTA) * rf)) - AndroidUtilities.dp(VERTICAL_PADDING_DP);
            return;
        }
        newParticle.x = getBounds().left + (Utilities.fastRandom.nextFloat() * getBounds().width());
        newParticle.y = getBounds().top + (Utilities.fastRandom.nextFloat() * getBounds().height());
    }

    @Override // android.graphics.drawable.Drawable
    public void invalidateSelf() {
        super.invalidateSelf();
        if (this.mParent != null) {
            View v = this.mParent;
            if (v.getParent() != null && this.invalidateParent) {
                ((View) v.getParent()).invalidate();
            } else {
                v.invalidate();
            }
        }
    }

    public void setParentView(View parentView) {
        this.mParent = parentView;
    }

    public View getParentView() {
        return this.mParent;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
        int i = 0;
        while (true) {
            float[] fArr = ALPHAS;
            if (i < fArr.length) {
                this.particlePaints[i].setAlpha((int) (fArr[i] * alpha));
                i++;
            } else {
                return;
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        Paint[] paintArr;
        for (Paint p : this.particlePaints) {
            p.setColorFilter(colorFilter);
        }
    }

    public void setColor(int color) {
        if (this.lastColor != color) {
            int i = 0;
            while (true) {
                float[] fArr = ALPHAS;
                if (i < fArr.length) {
                    this.particlePaints[i].setColor(ColorUtils.setAlphaComponent(color, (int) (this.mAlpha * fArr[i])));
                    i++;
                } else {
                    this.lastColor = color;
                    return;
                }
            }
        }
    }

    public boolean hasColor() {
        return this.lastColor != 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    public static synchronized List<Long> measureKeyPoints(Layout textLayout) {
        int h;
        synchronized (SpoilerEffect.class) {
            int w = textLayout.getWidth();
            int h2 = textLayout.getHeight();
            if (w != 0 && h2 != 0) {
                Bitmap measureBitmap = Bitmap.createBitmap(Math.round(w), Math.round(h2), Bitmap.Config.ARGB_4444);
                Canvas measureCanvas = new Canvas(measureBitmap);
                textLayout.draw(measureCanvas);
                int[] pixels = new int[measureBitmap.getWidth() * measureBitmap.getHeight()];
                measureBitmap.getPixels(pixels, 0, measureBitmap.getWidth(), 0, 0, w, h2);
                int sX = -1;
                ArrayList<Long> keyPoints = new ArrayList<>(pixels.length);
                for (int x = 0; x < w; x++) {
                    int y = 0;
                    while (y < h2) {
                        int clr = pixels[(measureBitmap.getWidth() * y) + x];
                        if (Color.alpha(clr) < 128) {
                            h = h2;
                        } else {
                            if (sX == -1) {
                                sX = x;
                            }
                            h = h2;
                            keyPoints.add(Long.valueOf(((x - sX) << 16) + y));
                        }
                        y++;
                        h2 = h;
                    }
                }
                keyPoints.trimToSize();
                measureBitmap.recycle();
                return keyPoints;
            }
            return Collections.emptyList();
        }
    }

    public int getMaxParticlesCount() {
        return this.maxParticles;
    }

    public void setMaxParticlesCount(int maxParticles) {
        this.maxParticles = maxParticles;
        while (this.particlesPool.size() + this.particles.size() < maxParticles) {
            this.particlesPool.push(new Particle());
        }
    }

    public static void addSpoilers(TextView tv, Stack<SpoilerEffect> spoilersPool, List<SpoilerEffect> spoilers) {
        addSpoilers(tv, tv.getLayout(), (Spanned) tv.getText(), spoilersPool, spoilers);
    }

    public static void addSpoilers(View v, Layout textLayout, Stack<SpoilerEffect> spoilersPool, List<SpoilerEffect> spoilers) {
        if (textLayout.getText() instanceof Spanned) {
            addSpoilers(v, textLayout, (Spanned) textLayout.getText(), spoilersPool, spoilers);
        }
    }

    public static void addSpoilers(View v, Layout textLayout, Spanned spannable, Stack<SpoilerEffect> spoilersPool, List<SpoilerEffect> spoilers) {
        float t;
        float b;
        int start;
        int end;
        TextStyleSpan[] textStyleSpanArr;
        int i;
        int i2;
        for (int line = 0; line < textLayout.getLineCount(); line++) {
            float l = textLayout.getLineLeft(line);
            float t2 = textLayout.getLineTop(line);
            float r = textLayout.getLineRight(line);
            float b2 = textLayout.getLineBottom(line);
            int start2 = textLayout.getLineStart(line);
            int end2 = textLayout.getLineEnd(line);
            TextStyleSpan[] textStyleSpanArr2 = (TextStyleSpan[]) spannable.getSpans(start2, end2, TextStyleSpan.class);
            int length = textStyleSpanArr2.length;
            int i3 = 0;
            while (i3 < length) {
                TextStyleSpan span = textStyleSpanArr2[i3];
                if (span.isSpoiler()) {
                    int ss = spannable.getSpanStart(span);
                    int se = spannable.getSpanEnd(span);
                    int realStart = Math.max(start2, ss);
                    int realEnd = Math.min(end2, se);
                    int len = realEnd - realStart;
                    if (len == 0) {
                        i2 = i3;
                        i = length;
                        textStyleSpanArr = textStyleSpanArr2;
                        end = end2;
                        start = start2;
                        b = b2;
                        t = t2;
                    } else {
                        int se2 = start2;
                        int ss2 = end2;
                        i2 = i3;
                        i = length;
                        textStyleSpanArr = textStyleSpanArr2;
                        end = end2;
                        start = start2;
                        b = b2;
                        t = t2;
                        addSpoilersInternal(v, spannable, textLayout, se2, ss2, l, t2, r, b2, realStart, realEnd, spoilersPool, spoilers);
                    }
                } else {
                    i2 = i3;
                    i = length;
                    textStyleSpanArr = textStyleSpanArr2;
                    end = end2;
                    start = start2;
                    b = b2;
                    t = t2;
                }
                i3 = i2 + 1;
                length = i;
                textStyleSpanArr2 = textStyleSpanArr;
                end2 = end;
                start2 = start;
                b2 = b;
                t2 = t;
            }
        }
        if ((v instanceof TextView) && spoilersPool != null) {
            spoilersPool.clear();
        }
    }

    private static void addSpoilersInternal(View v, Spanned spannable, Layout textLayout, int lineStart, int lineEnd, float lineLeft, float lineTop, float lineRight, float lineBottom, int realStart, int realEnd, Stack<SpoilerEffect> spoilersPool, List<SpoilerEffect> spoilers) {
        TextStyleSpan[] textStyleSpanArr;
        URLSpan[] uRLSpanArr;
        StaticLayout newLayout;
        int i;
        Layout layout = textLayout;
        SpannableStringBuilder vSpan = SpannableStringBuilder.valueOf(AndroidUtilities.replaceNewLines(new SpannableStringBuilder(spannable, realStart, realEnd)));
        for (TextStyleSpan styleSpan : (TextStyleSpan[]) vSpan.getSpans(0, vSpan.length(), TextStyleSpan.class)) {
            vSpan.removeSpan(styleSpan);
        }
        for (URLSpan urlSpan : (URLSpan[]) vSpan.getSpans(0, vSpan.length(), URLSpan.class)) {
            vSpan.removeSpan(urlSpan);
        }
        int tLen = vSpan.toString().trim().length();
        if (tLen == 0) {
            return;
        }
        int width = textLayout.getEllipsizedWidth() > 0 ? textLayout.getEllipsizedWidth() : textLayout.getWidth();
        TextPaint measurePaint = new TextPaint(textLayout.getPaint());
        measurePaint.setColor(-16777216);
        if (Build.VERSION.SDK_INT >= 24) {
            newLayout = StaticLayout.Builder.obtain(vSpan, 0, vSpan.length(), measurePaint, width).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(textLayout.getSpacingAdd(), textLayout.getSpacingMultiplier()).build();
            i = 0;
        } else {
            i = 0;
            newLayout = new StaticLayout(vSpan, measurePaint, width, Layout.Alignment.ALIGN_NORMAL, textLayout.getSpacingMultiplier(), textLayout.getSpacingAdd(), false);
        }
        boolean rtlInNonRTL = (LocaleController.isRTLCharacter(vSpan.charAt(i)) || LocaleController.isRTLCharacter(vSpan.charAt(vSpan.length() + (-1)))) && !LocaleController.isRTL;
        SpoilerEffect spoilerEffect = (spoilersPool == null || spoilersPool.isEmpty()) ? new SpoilerEffect() : spoilersPool.remove(i);
        spoilerEffect.setRippleProgress(-1.0f);
        float ps = realStart == lineStart ? lineLeft : layout.getPrimaryHorizontal(realStart);
        float pe = (realEnd == lineEnd || (rtlInNonRTL && realEnd == lineEnd + (-1) && spannable.charAt(lineEnd + (-1)) == 8230)) ? lineRight : layout.getPrimaryHorizontal(realEnd);
        spoilerEffect.setBounds((int) Math.min(ps, pe), (int) lineTop, (int) Math.max(ps, pe), (int) lineBottom);
        spoilerEffect.setColor(textLayout.getPaint().getColor());
        spoilerEffect.setRippleInterpolator(Easings.easeInQuad);
        if (!spoilerEffect.isLowDevice) {
            spoilerEffect.setKeyPoints(measureKeyPoints(newLayout));
        }
        spoilerEffect.updateMaxParticles();
        if (v != null) {
            spoilerEffect.setParentView(v);
        }
        spoilerEffect.spaces.clear();
        int i2 = 0;
        while (i2 < vSpan.length()) {
            if (vSpan.charAt(i2) == ' ') {
                RectF r = new RectF();
                int off = realStart + i2;
                int line = layout.getLineForOffset(off);
                r.top = layout.getLineTop(line);
                r.bottom = layout.getLineBottom(line);
                float lh = layout.getPrimaryHorizontal(off);
                float rh = layout.getPrimaryHorizontal(off + 1);
                r.left = (int) Math.min(lh, rh);
                r.right = (int) Math.max(lh, rh);
                if (Math.abs(lh - rh) <= AndroidUtilities.dp(20.0f)) {
                    spoilerEffect.spaces.add(r);
                }
            }
            i2++;
            layout = textLayout;
        }
        spoilers.add(spoilerEffect);
    }

    public static void clipOutCanvas(Canvas canvas, List<SpoilerEffect> spoilers) {
        tempPath.rewind();
        for (SpoilerEffect eff : spoilers) {
            Rect b = eff.getBounds();
            tempPath.addRect(b.left, b.top, b.right, b.bottom, Path.Direction.CW);
        }
        canvas.clipPath(tempPath, Region.Op.DIFFERENCE);
    }

    public static void renderWithRipple(View v, boolean invalidateSpoilersParent, int spoilersColor, int verticalOffset, AtomicReference<Layout> patchedLayoutRef, Layout textLayout, List<SpoilerEffect> spoilers, Canvas canvas, boolean useParentWidth) {
        Layout pl;
        int w;
        Layout layout;
        Layout pl2;
        int i;
        TextStyleSpan[] textStyleSpanArr;
        if (spoilers.isEmpty()) {
            textLayout.draw(canvas);
            return;
        }
        Layout pl3 = patchedLayoutRef.get();
        if (pl3 == null || !textLayout.getText().toString().equals(pl3.getText().toString()) || textLayout.getWidth() != pl3.getWidth() || textLayout.getHeight() != pl3.getHeight()) {
            SpannableStringBuilder sb = new SpannableStringBuilder(textLayout.getText());
            if (textLayout.getText() instanceof Spannable) {
                Spannable sp = (Spannable) textLayout.getText();
                TextStyleSpan[] textStyleSpanArr2 = (TextStyleSpan[]) sp.getSpans(0, sp.length(), TextStyleSpan.class);
                int length = textStyleSpanArr2.length;
                int i2 = 0;
                while (i2 < length) {
                    TextStyleSpan ss = textStyleSpanArr2[i2];
                    if (!ss.isSpoiler()) {
                        pl2 = pl3;
                        textStyleSpanArr = textStyleSpanArr2;
                        i = length;
                    } else {
                        int start = sp.getSpanStart(ss);
                        int end = sp.getSpanEnd(ss);
                        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) sp.getSpans(start, end, Emoji.EmojiSpan.class);
                        int length2 = emojiSpanArr.length;
                        pl2 = pl3;
                        int i3 = 0;
                        while (i3 < length2) {
                            TextStyleSpan[] textStyleSpanArr3 = textStyleSpanArr2;
                            final Emoji.EmojiSpan e = emojiSpanArr[i3];
                            sb.setSpan(new ReplacementSpan() { // from class: org.telegram.ui.Components.spoilers.SpoilerEffect.2
                                @Override // android.text.style.ReplacementSpan
                                public int getSize(Paint paint, CharSequence text, int start2, int end2, Paint.FontMetricsInt fm) {
                                    return e.getSize(paint, text, start2, end2, fm);
                                }

                                @Override // android.text.style.ReplacementSpan
                                public void draw(Canvas canvas2, CharSequence text, int start2, int end2, float x, int top, int y, int bottom, Paint paint) {
                                }
                            }, sp.getSpanStart(e), sp.getSpanEnd(e), sp.getSpanFlags(ss));
                            sb.removeSpan(e);
                            i3++;
                            textStyleSpanArr2 = textStyleSpanArr3;
                            length = length;
                            emojiSpanArr = emojiSpanArr;
                            length2 = length2;
                            start = start;
                        }
                        textStyleSpanArr = textStyleSpanArr2;
                        i = length;
                        sb.setSpan(new ForegroundColorSpan(0), sp.getSpanStart(ss), sp.getSpanEnd(ss), sp.getSpanFlags(ss));
                        sb.removeSpan(ss);
                    }
                    i2++;
                    textStyleSpanArr2 = textStyleSpanArr;
                    length = i;
                    pl3 = pl2;
                }
            }
            if (Build.VERSION.SDK_INT >= 24) {
                layout = StaticLayout.Builder.obtain(sb, 0, sb.length(), textLayout.getPaint(), textLayout.getWidth()).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(textLayout.getSpacingAdd(), textLayout.getSpacingMultiplier()).build();
            } else {
                layout = new StaticLayout(sb, textLayout.getPaint(), textLayout.getWidth(), textLayout.getAlignment(), textLayout.getSpacingMultiplier(), textLayout.getSpacingAdd(), false);
            }
            patchedLayoutRef.set(layout);
            pl = layout;
        } else {
            pl = pl3;
        }
        if (!spoilers.isEmpty()) {
            canvas.save();
            canvas.translate(0.0f, verticalOffset);
            pl.draw(canvas);
            canvas.restore();
        } else {
            textLayout.draw(canvas);
        }
        if (!spoilers.isEmpty()) {
            tempPath.rewind();
            for (SpoilerEffect eff : spoilers) {
                Rect b = eff.getBounds();
                tempPath.addRect(b.left, b.top, b.right, b.bottom, Path.Direction.CW);
            }
            if (!spoilers.isEmpty() && spoilers.get(0).rippleProgress != -1.0f) {
                canvas.save();
                canvas.clipPath(tempPath);
                tempPath.rewind();
                if (!spoilers.isEmpty()) {
                    spoilers.get(0).getRipplePath(tempPath);
                }
                canvas.clipPath(tempPath);
                canvas.translate(0.0f, -v.getPaddingTop());
                textLayout.draw(canvas);
                canvas.restore();
            }
            boolean useAlphaLayer = spoilers.get(0).rippleProgress != -1.0f;
            if (useAlphaLayer) {
                int w2 = v.getMeasuredWidth();
                if (useParentWidth && (v.getParent() instanceof View)) {
                    int w3 = ((View) v.getParent()).getMeasuredWidth();
                    w = w3;
                } else {
                    w = w2;
                }
                canvas.saveLayer(0.0f, 0.0f, w, v.getMeasuredHeight(), null, 31);
            } else {
                canvas.save();
            }
            canvas.translate(0.0f, -v.getPaddingTop());
            for (SpoilerEffect eff2 : spoilers) {
                eff2.setInvalidateParent(invalidateSpoilersParent);
                if (eff2.getParentView() != v) {
                    eff2.setParentView(v);
                }
                if (eff2.shouldInvalidateColor()) {
                    eff2.setColor(ColorUtils.blendARGB(spoilersColor, Theme.chat_msgTextPaint.getColor(), Math.max(0.0f, eff2.getRippleProgress())));
                } else {
                    eff2.setColor(spoilersColor);
                }
                eff2.draw(canvas);
            }
            if (useAlphaLayer) {
                tempPath.rewind();
                spoilers.get(0).getRipplePath(tempPath);
                if (xRefPaint == null) {
                    Paint paint = new Paint(1);
                    xRefPaint = paint;
                    paint.setColor(-16777216);
                    xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                }
                canvas.drawPath(tempPath, xRefPaint);
            }
            canvas.restore();
        }
    }

    /* loaded from: classes5.dex */
    public static class Particle {
        private int alpha;
        private float currentTime;
        private float lifeTime;
        private float vecX;
        private float vecY;
        private float velocity;
        private float x;
        private float y;

        private Particle() {
        }

        static /* synthetic */ float access$516(Particle x0, float x1) {
            float f = x0.x + x1;
            x0.x = f;
            return f;
        }

        static /* synthetic */ float access$616(Particle x0, float x1) {
            float f = x0.y + x1;
            x0.y = f;
            return f;
        }
    }
}
