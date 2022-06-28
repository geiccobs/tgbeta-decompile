package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.Components.SeekBar;
/* loaded from: classes5.dex */
public class SeekBarWaveform {
    private static Paint paintInner;
    private static Paint paintOuter;
    private Path alphaPath;
    private ArrayList<Float> animatedValues;
    private SeekBar.SeekBarDelegate delegate;
    private float[] fromHeights;
    private int fromWidth;
    private int height;
    private float[] heights;
    private int innerColor;
    private boolean isUnread;
    private boolean loading;
    private Paint loadingPaint;
    private int loadingPaintColor1;
    private int loadingPaintColor2;
    private float loadingPaintWidth;
    private long loadingStart;
    private MessageObject messageObject;
    private int outerColor;
    private View parentView;
    private Path path;
    private float progress;
    private boolean selected;
    private int selectedColor;
    private float startX;
    private float[] toHeights;
    private int toWidth;
    private byte[] waveformBytes;
    private int width;
    private int thumbX = 0;
    private int thumbDX = 0;
    private boolean startDraging = false;
    private boolean pressed = false;
    private float clearProgress = 1.0f;
    private AnimatedFloat appearFloat = new AnimatedFloat(125, 450, CubicBezierInterpolator.EASE_OUT_QUINT);
    private float waveScaling = 1.0f;
    private AnimatedFloat loadingFloat = new AnimatedFloat(150, CubicBezierInterpolator.DEFAULT);

    public SeekBarWaveform(Context context) {
        if (paintInner == null) {
            paintInner = new Paint(1);
            paintOuter = new Paint(1);
            paintInner.setStyle(Paint.Style.FILL);
            paintOuter.setStyle(Paint.Style.FILL);
        }
    }

    public void setDelegate(SeekBar.SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public void setColors(int inner, int outer, int selected) {
        this.innerColor = inner;
        this.outerColor = outer;
        this.selectedColor = selected;
    }

    public void setWaveform(byte[] waveform) {
        this.waveformBytes = waveform;
        this.heights = calculateHeights((int) (this.width / AndroidUtilities.dpf2(3.0f)));
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public void setMessageObject(MessageObject object) {
        MessageObject messageObject;
        if (this.animatedValues != null && (messageObject = this.messageObject) != null && object != null && messageObject.getId() != object.getId()) {
            this.animatedValues.clear();
        }
        this.messageObject = object;
    }

    public void setParentView(View view) {
        this.parentView = view;
        this.loadingFloat.setParent(view);
        this.appearFloat.setParent(view);
    }

    public boolean isStartDraging() {
        return this.startDraging;
    }

    public boolean onTouch(int action, float x, float y) {
        SeekBar.SeekBarDelegate seekBarDelegate;
        if (action == 0) {
            if (0.0f <= x && x <= this.width && y >= 0.0f && y <= this.height) {
                this.startX = x;
                this.pressed = true;
                this.thumbDX = (int) (x - this.thumbX);
                this.startDraging = false;
                return true;
            }
        } else if (action == 1 || action == 3) {
            if (this.pressed) {
                if (action == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(this.thumbX / this.width);
                }
                this.pressed = false;
                return true;
            }
        } else if (action == 2 && this.pressed) {
            if (this.startDraging) {
                int i = (int) (x - this.thumbDX);
                this.thumbX = i;
                if (i < 0) {
                    this.thumbX = 0;
                } else {
                    int i2 = this.width;
                    if (i > i2) {
                        this.thumbX = i2;
                    }
                }
                this.progress = this.thumbX / this.width;
            }
            float f = this.startX;
            if (f != -1.0f && Math.abs(x - f) > AndroidUtilities.getPixelsInCM(0.2f, true)) {
                View view = this.parentView;
                if (view != null && view.getParent() != null) {
                    this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
                }
                this.startDraging = true;
                this.startX = -1.0f;
            }
            return true;
        }
        return false;
    }

    public float getProgress() {
        return this.thumbX / this.width;
    }

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    public void setProgress(float progress, boolean animated) {
        boolean z = this.isUnread;
        this.progress = z ? 1.0f : progress;
        int currentThumbX = z ? this.width : this.thumbX;
        if (animated && currentThumbX != 0 && progress == 0.0f) {
            this.clearProgress = 0.0f;
        } else if (!animated) {
            this.clearProgress = 1.0f;
        }
        int ceil = (int) Math.ceil(this.width * progress);
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
            return;
        }
        int i = this.width;
        if (ceil > i) {
            this.thumbX = i;
        }
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSize(int w, int h) {
        setSize(w, h, w, w);
    }

    public void setSize(int w, int h, int fromW, int toW) {
        this.width = w;
        this.height = h;
        float[] fArr = this.heights;
        if (fArr == null || fArr.length != ((int) (w / AndroidUtilities.dpf2(3.0f)))) {
            this.heights = calculateHeights((int) (this.width / AndroidUtilities.dpf2(3.0f)));
        }
        if (fromW != toW && (this.fromWidth != fromW || this.toWidth != toW)) {
            this.fromWidth = fromW;
            this.toWidth = toW;
            this.fromHeights = calculateHeights((int) (fromW / AndroidUtilities.dpf2(3.0f)));
            this.toHeights = calculateHeights((int) (this.toWidth / AndroidUtilities.dpf2(3.0f)));
        } else if (fromW == toW) {
            this.toHeights = null;
            this.fromHeights = null;
        }
    }

    public void setSent() {
        this.appearFloat.set(0.0f, true);
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    private float[] calculateHeights(int count) {
        int samplesCount;
        SeekBarWaveform seekBarWaveform = this;
        byte[] bArr = seekBarWaveform.waveformBytes;
        if (bArr == null || count <= 0) {
            return null;
        }
        float[] heights = new float[count];
        int samplesCount2 = (bArr.length * 8) / 5;
        float samplesPerBar = samplesCount2 / count;
        float barCounter = 0.0f;
        int nextBarNum = 0;
        int barNum = 0;
        int a = 0;
        while (a < samplesCount2) {
            if (a != nextBarNum) {
                samplesCount = samplesCount2;
            } else {
                int drawBarCount = 0;
                int lastBarNum = nextBarNum;
                while (lastBarNum == nextBarNum) {
                    barCounter += samplesPerBar;
                    nextBarNum = (int) barCounter;
                    drawBarCount++;
                }
                int bitPointer = a * 5;
                int byteNum = bitPointer / 8;
                int byteBitOffset = bitPointer - (byteNum * 8);
                int currentByteCount = 8 - byteBitOffset;
                int nextByteRest = 5 - currentByteCount;
                byte value = (byte) ((seekBarWaveform.waveformBytes[byteNum] >> byteBitOffset) & ((2 << (Math.min(5, currentByteCount) - 1)) - 1));
                if (nextByteRest > 0) {
                    int i = byteNum + 1;
                    samplesCount = samplesCount2;
                    byte[] bArr2 = seekBarWaveform.waveformBytes;
                    if (i < bArr2.length) {
                        value = (byte) ((bArr2[byteNum + 1] & ((2 << (nextByteRest - 1)) - 1)) | ((byte) (value << nextByteRest)));
                    }
                } else {
                    samplesCount = samplesCount2;
                }
                int b = 0;
                while (b < drawBarCount) {
                    if (barNum < heights.length) {
                        heights[barNum] = Math.max(0.0f, (value * 7) / 31.0f);
                        b++;
                        barNum++;
                    } else {
                        return heights;
                    }
                }
                continue;
            }
            a++;
            seekBarWaveform = this;
            samplesCount2 = samplesCount;
        }
        return heights;
    }

    public void draw(Canvas canvas, View parentView) {
        int i;
        float[] fArr;
        if (this.waveformBytes == null || (i = this.width) == 0) {
            return;
        }
        float totalBarsCount = i / AndroidUtilities.dpf2(3.0f);
        if (totalBarsCount <= 0.1f) {
            return;
        }
        float f = this.clearProgress;
        if (f != 1.0f) {
            float f2 = f + 0.10666667f;
            this.clearProgress = f2;
            if (f2 > 1.0f) {
                this.clearProgress = 1.0f;
            } else {
                parentView.invalidate();
            }
        }
        float appearProgress = this.appearFloat.set(1.0f);
        Path path = this.path;
        if (path == null) {
            this.path = new Path();
        } else {
            path.reset();
        }
        float alpha = 0.0f;
        Path path2 = this.alphaPath;
        if (path2 == null) {
            this.alphaPath = new Path();
        } else {
            path2.reset();
        }
        float[] fArr2 = this.fromHeights;
        if (fArr2 != null && (fArr = this.toHeights) != null) {
            int i2 = this.width;
            int i3 = this.fromWidth;
            float t = (i2 - i3) / (this.toWidth - i3);
            int maxlen = Math.max(fArr2.length, fArr.length);
            int minlen = Math.min(this.fromHeights.length, this.toHeights.length);
            float[] fArr3 = this.fromHeights;
            int length = fArr3.length;
            float[] fArr4 = this.toHeights;
            float[] minarr = length < fArr4.length ? fArr3 : fArr4;
            float[] maxarr = fArr3.length < fArr4.length ? fArr4 : fArr3;
            float T = fArr3.length < fArr4.length ? t : 1.0f - t;
            int k = -1;
            int barNum = 0;
            while (barNum < maxlen) {
                float appearProgress2 = appearProgress;
                int l = MathUtils.clamp((int) Math.floor((barNum / maxlen) * minlen), 0, minlen - 1);
                if (k < l) {
                    float x = AndroidUtilities.lerp(l, barNum, T) * AndroidUtilities.dpf2(3.0f);
                    float h = AndroidUtilities.dpf2(AndroidUtilities.lerp(minarr[l], maxarr[barNum], T));
                    addBar(this.path, x, h);
                    k = l;
                } else {
                    float x2 = AndroidUtilities.lerp(l, barNum, T) * AndroidUtilities.dpf2(3.0f);
                    float h2 = AndroidUtilities.dpf2(AndroidUtilities.lerp(minarr[l], maxarr[barNum], T));
                    addBar(this.alphaPath, x2, h2);
                    alpha = T;
                }
                barNum++;
                appearProgress = appearProgress2;
            }
        } else if (this.heights != null) {
            for (int barNum2 = 0; barNum2 < totalBarsCount && barNum2 < this.heights.length; barNum2++) {
                float x3 = barNum2 * AndroidUtilities.dpf2(3.0f);
                float bart = MathUtils.clamp((appearProgress * totalBarsCount) - barNum2, 0.0f, 1.0f);
                float h3 = AndroidUtilities.dpf2(this.heights[barNum2]) * bart;
                addBar(this.path, x3, h3);
            }
        }
        if (alpha > 0.0f) {
            canvas.save();
            canvas.clipPath(this.alphaPath);
            drawFill(canvas, alpha);
            canvas.restore();
        }
        canvas.save();
        canvas.clipPath(this.path);
        drawFill(canvas, 1.0f);
        canvas.restore();
    }

    private void drawFill(Canvas canvas, float alpha) {
        Paint paint;
        Paint paint2;
        float strokeWidth = AndroidUtilities.dpf2(2.0f);
        MessageObject messageObject = this.messageObject;
        boolean z = messageObject != null && messageObject.isContentUnread() && !this.messageObject.isOut() && this.progress <= 0.0f;
        this.isUnread = z;
        paintInner.setColor(z ? this.outerColor : this.selected ? this.selectedColor : this.innerColor);
        paintOuter.setColor(this.outerColor);
        this.loadingFloat.setParent(this.parentView);
        boolean isPlaying = MediaController.getInstance().isPlayingMessage(this.messageObject);
        float loadingT = this.loadingFloat.set((!this.loading || isPlaying) ? 0.0f : 1.0f);
        Paint paint3 = paintInner;
        paint3.setColor(ColorUtils.blendARGB(paint3.getColor(), this.innerColor, loadingT));
        paintOuter.setAlpha((int) (paint.getAlpha() * (1.0f - loadingT) * alpha));
        paintInner.setAlpha((int) (paint2.getAlpha() * alpha));
        canvas.drawRect(0.0f, 0.0f, this.width + strokeWidth, this.height, paintInner);
        if (loadingT < 1.0f) {
            canvas.drawRect(0.0f, 0.0f, this.progress * (this.width + strokeWidth) * (1.0f - loadingT), this.height, paintOuter);
        }
        if (loadingT > 0.0f) {
            if (this.loadingPaint == null || Math.abs(this.loadingPaintWidth - this.width) > AndroidUtilities.dp(8.0f) || this.loadingPaintColor1 != this.innerColor || this.loadingPaintColor2 != this.outerColor) {
                if (this.loadingPaint == null) {
                    this.loadingPaint = new Paint(1);
                }
                this.loadingPaintColor1 = this.innerColor;
                this.loadingPaintColor2 = this.outerColor;
                Paint paint4 = this.loadingPaint;
                float f = this.width;
                this.loadingPaintWidth = f;
                int i = this.loadingPaintColor1;
                paint4.setShader(new LinearGradient(0.0f, 0.0f, f, 0.0f, new int[]{i, this.loadingPaintColor2, i}, new float[]{0.0f, 0.2f, 0.4f}, Shader.TileMode.CLAMP));
            }
            this.loadingPaint.setAlpha((int) (255.0f * loadingT * alpha));
            canvas.save();
            float t = ((float) (SystemClock.elapsedRealtime() - this.loadingStart)) / 270.0f;
            float dx = ((((float) Math.pow(t, 0.75d)) % 1.6f) - 0.6f) * this.loadingPaintWidth;
            canvas.translate(dx, 0.0f);
            canvas.drawRect(-dx, 0.0f, (this.width + 5) - dx, this.height, this.loadingPaint);
            canvas.restore();
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    private void addBar(Path path, float x, float h) {
        float strokeWidth = AndroidUtilities.dpf2(2.0f);
        int y = (this.height - AndroidUtilities.dp(14.0f)) / 2;
        float h2 = h * this.waveScaling;
        AndroidUtilities.rectTmp.set((AndroidUtilities.dpf2(1.0f) + x) - (strokeWidth / 2.0f), AndroidUtilities.dp(7.0f) + y + ((-h2) - (strokeWidth / 2.0f)), AndroidUtilities.dpf2(1.0f) + x + (strokeWidth / 2.0f), AndroidUtilities.dp(7.0f) + y + (strokeWidth / 2.0f) + h2);
        path.addRoundRect(AndroidUtilities.rectTmp, strokeWidth, strokeWidth, Path.Direction.CW);
    }

    public void setWaveScaling(float waveScaling) {
        this.waveScaling = waveScaling;
    }

    public void setLoading(boolean loading) {
        if (!this.loading && loading && this.loadingFloat.get() <= 0.0f) {
            this.loadingStart = SystemClock.elapsedRealtime();
        }
        this.loading = loading;
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }
}
