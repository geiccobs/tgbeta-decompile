package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.UndoView;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
/* loaded from: classes4.dex */
public class SvgHelper {
    private static final double[] pow10 = new double[128];

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class Line {
        float x1;
        float x2;
        float y1;
        float y2;

        public Line(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class Circle {
        float rad;
        float x1;
        float y1;

        public Circle(float x1, float y1, float rad) {
            this.x1 = x1;
            this.y1 = y1;
            this.rad = rad;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class Oval {
        RectF rect;

        public Oval(RectF rect) {
            this.rect = rect;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class RoundRect {
        RectF rect;
        float rx;

        public RoundRect(RectF rect, float rx) {
            this.rect = rect;
            this.rx = rx;
        }
    }

    /* loaded from: classes4.dex */
    public static class SvgDrawable extends Drawable {
        private static float gradientWidth;
        private static long lastUpdateTime;
        private static int[] parentPosition = new int[2];
        private static WeakReference<Drawable> shiftDrawable;
        private static Runnable shiftRunnable;
        private static float totalTranslation;
        private Bitmap backgroundBitmap;
        private Canvas backgroundCanvas;
        private float colorAlpha;
        private int currentColor;
        private String currentColorKey;
        protected int height;
        private Paint overridePaint;
        private ImageReceiver parentImageReceiver;
        private LinearGradient placeholderGradient;
        private Matrix placeholderMatrix;
        protected int width;
        protected ArrayList<Object> commands = new ArrayList<>();
        protected HashMap<Object, Paint> paints = new HashMap<>();
        private float crossfadeAlpha = 1.0f;
        private boolean aspectFill = true;

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.width;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.height;
        }

        public void setAspectFill(boolean value) {
            this.aspectFill = value;
        }

        public void overrideWidthAndHeight(int w, int h) {
            this.width = w;
            this.height = h;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Paint paint;
            String str = this.currentColorKey;
            if (str != null) {
                setupGradient(str, this.colorAlpha);
            }
            Rect bounds = getBounds();
            float scale = getScale();
            canvas.save();
            canvas.translate(bounds.left, bounds.top);
            if (!this.aspectFill) {
                canvas.translate((bounds.width() - (this.width * scale)) / 2.0f, (bounds.height() - (this.height * scale)) / 2.0f);
            }
            canvas.scale(scale, scale);
            int N = this.commands.size();
            for (int a = 0; a < N; a++) {
                Object object = this.commands.get(a);
                if (object instanceof Matrix) {
                    canvas.save();
                    canvas.concat((Matrix) object);
                } else if (object == null) {
                    canvas.restore();
                } else {
                    if (this.overridePaint != null) {
                        paint = this.overridePaint;
                    } else {
                        paint = this.paints.get(object);
                    }
                    int originalAlpha = paint.getAlpha();
                    paint.setAlpha((int) (this.crossfadeAlpha * originalAlpha));
                    if (object instanceof Path) {
                        canvas.drawPath((Path) object, paint);
                    } else if (object instanceof Rect) {
                        canvas.drawRect((Rect) object, paint);
                    } else if (object instanceof RectF) {
                        canvas.drawRect((RectF) object, paint);
                    } else if (object instanceof Line) {
                        Line line = (Line) object;
                        canvas.drawLine(line.x1, line.y1, line.x2, line.y2, paint);
                    } else if (object instanceof Circle) {
                        Circle circle = (Circle) object;
                        canvas.drawCircle(circle.x1, circle.y1, circle.rad, paint);
                    } else if (object instanceof Oval) {
                        Oval oval = (Oval) object;
                        canvas.drawOval(oval.rect, paint);
                    } else if (object instanceof RoundRect) {
                        RoundRect rect = (RoundRect) object;
                        canvas.drawRoundRect(rect.rect, rect.rx, rect.rx, paint);
                    }
                    paint.setAlpha(originalAlpha);
                }
            }
            canvas.restore();
            if (this.placeholderGradient != null) {
                if (shiftRunnable == null || shiftDrawable.get() == this) {
                    long newUpdateTime = SystemClock.elapsedRealtime();
                    long dt = Math.abs(lastUpdateTime - newUpdateTime);
                    if (dt > 17) {
                        dt = 16;
                    }
                    lastUpdateTime = newUpdateTime;
                    totalTranslation += (((float) dt) * gradientWidth) / 1800.0f;
                    while (true) {
                        float f = totalTranslation;
                        float f2 = gradientWidth;
                        if (f < f2 / 2.0f) {
                            break;
                        }
                        totalTranslation = f - f2;
                    }
                    shiftDrawable = new WeakReference<>(this);
                    Runnable runnable = shiftRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    SvgHelper$SvgDrawable$$ExternalSyntheticLambda0 svgHelper$SvgDrawable$$ExternalSyntheticLambda0 = SvgHelper$SvgDrawable$$ExternalSyntheticLambda0.INSTANCE;
                    shiftRunnable = svgHelper$SvgDrawable$$ExternalSyntheticLambda0;
                    AndroidUtilities.runOnUIThread(svgHelper$SvgDrawable$$ExternalSyntheticLambda0, ((int) (1000.0f / AndroidUtilities.screenRefreshRate)) - 1);
                }
                ImageReceiver imageReceiver = this.parentImageReceiver;
                if (imageReceiver != null) {
                    imageReceiver.getParentPosition(parentPosition);
                }
                this.placeholderMatrix.reset();
                this.placeholderMatrix.postTranslate(((-parentPosition[0]) + totalTranslation) - bounds.left, 0.0f);
                this.placeholderMatrix.postScale(1.0f / scale, 1.0f / scale);
                this.placeholderGradient.setLocalMatrix(this.placeholderMatrix);
                ImageReceiver imageReceiver2 = this.parentImageReceiver;
                if (imageReceiver2 != null) {
                    imageReceiver2.invalidate();
                }
            }
        }

        public float getScale() {
            Rect bounds = getBounds();
            float scaleX = bounds.width() / this.width;
            float scaleY = bounds.height() / this.height;
            return this.aspectFill ? Math.max(scaleX, scaleY) : Math.min(scaleX, scaleY);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            this.crossfadeAlpha = alpha / 255.0f;
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        public void addCommand(Object command, Paint paint) {
            this.commands.add(command);
            this.paints.put(command, new Paint(paint));
        }

        public void addCommand(Object command) {
            this.commands.add(command);
        }

        public void setParent(ImageReceiver imageReceiver) {
            this.parentImageReceiver = imageReceiver;
        }

        public void setupGradient(String colorKey, float alpha) {
            Shader backgroundGradient;
            int color = Theme.getColor(colorKey);
            if (this.currentColor != color) {
                this.colorAlpha = alpha;
                this.currentColorKey = colorKey;
                this.currentColor = color;
                gradientWidth = AndroidUtilities.displaySize.x * 2;
                float w = AndroidUtilities.dp(180.0f) / gradientWidth;
                int color2 = Color.argb((int) ((Color.alpha(color) / 2) * this.colorAlpha), Color.red(color), Color.green(color), Color.blue(color));
                float centerX = (1.0f - w) / 2.0f;
                this.placeholderGradient = new LinearGradient(0.0f, 0.0f, gradientWidth, 0.0f, new int[]{0, 0, color2, 0, 0}, new float[]{0.0f, centerX - (w / 2.0f), centerX, (w / 2.0f) + centerX, 1.0f}, Shader.TileMode.REPEAT);
                if (Build.VERSION.SDK_INT < 28) {
                    if (this.backgroundBitmap == null) {
                        this.backgroundBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                        this.backgroundCanvas = new Canvas(this.backgroundBitmap);
                    }
                    this.backgroundCanvas.drawColor(color2);
                    backgroundGradient = new BitmapShader(this.backgroundBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                } else {
                    backgroundGradient = new LinearGradient(0.0f, 0.0f, gradientWidth, 0.0f, new int[]{color2, color2}, (float[]) null, Shader.TileMode.REPEAT);
                }
                Matrix matrix = new Matrix();
                this.placeholderMatrix = matrix;
                this.placeholderGradient.setLocalMatrix(matrix);
                for (Paint paint : this.paints.values()) {
                    if (Build.VERSION.SDK_INT <= 22) {
                        paint.setShader(backgroundGradient);
                    } else {
                        paint.setShader(new ComposeShader(this.placeholderGradient, backgroundGradient, PorterDuff.Mode.ADD));
                    }
                }
            }
        }

        public void setPaint(Paint paint) {
            this.overridePaint = paint;
        }
    }

    public static Bitmap getBitmap(int res, int width, int height, int color) {
        return getBitmap(res, width, height, color, 1.0f);
    }

    public static Bitmap getBitmap(int res, int width, int height, int color, float scale) {
        Exception e;
        try {
        } catch (Exception e2) {
            e = e2;
        }
        try {
            InputStream stream = ApplicationLoader.applicationContext.getResources().openRawResource(res);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            SVGHandler handler = new SVGHandler(width, height, Integer.valueOf(color), false, scale);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(stream));
            Bitmap bitmap = handler.getBitmap();
            if (stream != null) {
                stream.close();
            }
            return bitmap;
        } catch (Exception e3) {
            e = e3;
            FileLog.e(e);
            return null;
        }
    }

    public static Bitmap getBitmap(File file, int width, int height, boolean white) {
        Exception e;
        try {
        } catch (Exception e2) {
            e = e2;
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            SVGHandler handler = new SVGHandler(width, height, white ? -1 : null, false, 1.0f);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(stream));
            Bitmap bitmap = handler.getBitmap();
            stream.close();
            return bitmap;
        } catch (Exception e3) {
            e = e3;
            FileLog.e(e);
            return null;
        }
    }

    public static Bitmap getBitmap(String xml, int width, int height, boolean white) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            SVGHandler handler = new SVGHandler(width, height, white ? -1 : null, false, 1.0f);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(new StringReader(xml)));
            return handler.getBitmap();
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static SvgDrawable getDrawable(String xml) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            SVGHandler handler = new SVGHandler(0, 0, null, true, 1.0f);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(new StringReader(xml)));
            return handler.getDrawable();
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static SvgDrawable getDrawable(int resId, int color) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            SVGHandler handler = new SVGHandler(0, 0, Integer.valueOf(color), true, 1.0f);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(ApplicationLoader.applicationContext.getResources().openRawResource(resId)));
            return handler.getDrawable();
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static SvgDrawable getDrawableByPath(String pathString, int w, int h) {
        try {
            Path path = doPath(pathString);
            SvgDrawable drawable = new SvgDrawable();
            drawable.commands.add(path);
            drawable.paints.put(path, new Paint(1));
            drawable.width = w;
            drawable.height = h;
            return drawable;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static Bitmap getBitmapByPathOnly(String pathString, int svgWidth, int svgHeight, int width, int height) {
        try {
            Path path = doPath(pathString);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(width / svgWidth, height / svgHeight);
            Paint paint = new Paint();
            paint.setColor(-1);
            canvas.drawPath(path, paint);
            return bitmap;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private static NumberParse parseNumbers(String s) {
        int n = s.length();
        int p = 0;
        ArrayList<Float> numbers = new ArrayList<>();
        boolean skipChar = false;
        for (int i = 1; i < n; i++) {
            if (skipChar) {
                skipChar = false;
            } else {
                char c = s.charAt(i);
                switch (c) {
                    case '\t':
                    case '\n':
                    case ' ':
                    case ',':
                    case '-':
                        if (c == '-' && s.charAt(i - 1) == 'e') {
                            break;
                        } else {
                            String str = s.substring(p, i);
                            if (str.trim().length() > 0) {
                                Float f = Float.valueOf(Float.parseFloat(str));
                                numbers.add(f);
                                if (c == '-') {
                                    p = i;
                                    break;
                                } else {
                                    p = i + 1;
                                    skipChar = true;
                                    break;
                                }
                            } else {
                                p++;
                                continue;
                            }
                        }
                    case ')':
                    case VoIPService.CALL_MIN_LAYER /* 65 */:
                    case 'C':
                    case 'H':
                    case UndoView.ACTION_GIGAGROUP_SUCCESS /* 76 */:
                    case UndoView.ACTION_PAYMENT_SUCCESS /* 77 */:
                    case UndoView.ACTION_CLEAR_DATES /* 81 */:
                    case 'S':
                    case 'T':
                    case 'V':
                    case 'Z':
                    case 'a':
                    case 'c':
                    case LocationRequest.PRIORITY_LOW_POWER /* 104 */:
                    case 'l':
                    case 'm':
                    case 'q':
                    case 's':
                    case 't':
                    case 'v':
                    case 'z':
                        String str2 = s.substring(p, i);
                        if (str2.trim().length() > 0) {
                            Float f2 = Float.valueOf(Float.parseFloat(str2));
                            numbers.add(f2);
                        }
                        int p2 = i;
                        return new NumberParse(numbers, p2);
                }
            }
        }
        String last = s.substring(p);
        if (last.length() > 0) {
            try {
                numbers.add(Float.valueOf(Float.parseFloat(last)));
            } catch (NumberFormatException e) {
            }
            p = s.length();
        }
        return new NumberParse(numbers, p);
    }

    public static Matrix parseTransform(String s) {
        if (s.startsWith("matrix(")) {
            NumberParse np = parseNumbers(s.substring("matrix(".length()));
            if (np.numbers.size() == 6) {
                Matrix matrix = new Matrix();
                matrix.setValues(new float[]{((Float) np.numbers.get(0)).floatValue(), ((Float) np.numbers.get(2)).floatValue(), ((Float) np.numbers.get(4)).floatValue(), ((Float) np.numbers.get(1)).floatValue(), ((Float) np.numbers.get(3)).floatValue(), ((Float) np.numbers.get(5)).floatValue(), 0.0f, 0.0f, 1.0f});
                return matrix;
            }
            return null;
        } else if (s.startsWith("translate(")) {
            NumberParse np2 = parseNumbers(s.substring("translate(".length()));
            if (np2.numbers.size() <= 0) {
                return null;
            }
            float tx = ((Float) np2.numbers.get(0)).floatValue();
            float ty = 0.0f;
            if (np2.numbers.size() > 1) {
                ty = ((Float) np2.numbers.get(1)).floatValue();
            }
            Matrix matrix2 = new Matrix();
            matrix2.postTranslate(tx, ty);
            return matrix2;
        } else if (s.startsWith("scale(")) {
            NumberParse np3 = parseNumbers(s.substring("scale(".length()));
            if (np3.numbers.size() <= 0) {
                return null;
            }
            float sx = ((Float) np3.numbers.get(0)).floatValue();
            float sy = 0.0f;
            if (np3.numbers.size() > 1) {
                sy = ((Float) np3.numbers.get(1)).floatValue();
            }
            Matrix matrix3 = new Matrix();
            matrix3.postScale(sx, sy);
            return matrix3;
        } else if (s.startsWith("skewX(")) {
            NumberParse np4 = parseNumbers(s.substring("skewX(".length()));
            if (np4.numbers.size() <= 0) {
                return null;
            }
            float angle = ((Float) np4.numbers.get(0)).floatValue();
            Matrix matrix4 = new Matrix();
            matrix4.postSkew((float) Math.tan(angle), 0.0f);
            return matrix4;
        } else if (s.startsWith("skewY(")) {
            NumberParse np5 = parseNumbers(s.substring("skewY(".length()));
            if (np5.numbers.size() <= 0) {
                return null;
            }
            float angle2 = ((Float) np5.numbers.get(0)).floatValue();
            Matrix matrix5 = new Matrix();
            matrix5.postSkew(0.0f, (float) Math.tan(angle2));
            return matrix5;
        } else if (s.startsWith("rotate(")) {
            NumberParse np6 = parseNumbers(s.substring("rotate(".length()));
            if (np6.numbers.size() <= 0) {
                return null;
            }
            float angle3 = ((Float) np6.numbers.get(0)).floatValue();
            float cx = 0.0f;
            float cy = 0.0f;
            if (np6.numbers.size() > 2) {
                cx = ((Float) np6.numbers.get(1)).floatValue();
                cy = ((Float) np6.numbers.get(2)).floatValue();
            }
            Matrix matrix6 = new Matrix();
            matrix6.postTranslate(cx, cy);
            matrix6.postRotate(angle3);
            matrix6.postTranslate(-cx, -cy);
            return matrix6;
        } else {
            return null;
        }
    }

    public static Path doPath(String s) {
        char prevCmd;
        char cmd;
        float y;
        float x;
        float y2;
        float x2;
        float y1;
        float x1;
        float y3;
        float x3;
        float y22;
        float x22;
        int n = s.length();
        ParserHelper ph = new ParserHelper(s, 0);
        ph.skipWhitespace();
        Path p = new Path();
        char prevCmd2 = 0;
        float lastX = 0.0f;
        float lastY = 0.0f;
        float lastX1 = 0.0f;
        float lastY1 = 0.0f;
        float subPathStartX = 0.0f;
        float subPathStartY = 0.0f;
        while (ph.pos < n) {
            char cmd2 = s.charAt(ph.pos);
            switch (cmd2) {
                case '+':
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case UndoView.ACTION_USERNAME_COPIED /* 56 */:
                case UndoView.ACTION_HASHTAG_COPIED /* 57 */:
                    if (prevCmd2 == 'm' || prevCmd2 == 'M') {
                        cmd = (char) (prevCmd2 - 1);
                        prevCmd = prevCmd2;
                        break;
                    } else if (prevCmd2 == 'c' || prevCmd2 == 'C') {
                        cmd = prevCmd2;
                        prevCmd = prevCmd2;
                        break;
                    } else if (prevCmd2 == 'l' || prevCmd2 == 'L') {
                        cmd = prevCmd2;
                        prevCmd = prevCmd2;
                        break;
                    } else if (prevCmd2 == 's' || prevCmd2 == 'S') {
                        cmd = prevCmd2;
                        prevCmd = prevCmd2;
                        break;
                    } else if (prevCmd2 == 'h' || prevCmd2 == 'H') {
                        cmd = prevCmd2;
                        prevCmd = prevCmd2;
                        break;
                    } else if (prevCmd2 == 'v' || prevCmd2 == 'V') {
                        cmd = prevCmd2;
                        prevCmd = prevCmd2;
                        break;
                    }
                    break;
                case ',':
                case '.':
                case '/':
                default:
                    ph.advance();
                    cmd = cmd2;
                    prevCmd = cmd2;
                    break;
            }
            boolean wasCurve = false;
            switch (cmd) {
                case VoIPService.CALL_MIN_LAYER /* 65 */:
                case 'a':
                    float rx = ph.nextFloat();
                    float ry = ph.nextFloat();
                    float theta = ph.nextFloat();
                    int largeArc = (int) ph.nextFloat();
                    int sweepArc = (int) ph.nextFloat();
                    float x4 = ph.nextFloat();
                    float y4 = ph.nextFloat();
                    drawArc(p, lastX, lastY, x4, y4, rx, ry, theta, largeArc, sweepArc);
                    lastX = x4;
                    lastY = y4;
                    subPathStartY = subPathStartY;
                    subPathStartX = subPathStartX;
                    break;
                case 'C':
                case 'c':
                    wasCurve = true;
                    float x12 = ph.nextFloat();
                    float y12 = ph.nextFloat();
                    float x23 = ph.nextFloat();
                    float y23 = ph.nextFloat();
                    float x5 = ph.nextFloat();
                    float y5 = ph.nextFloat();
                    if (cmd != 'c') {
                        x1 = x12;
                        y1 = y12;
                        x2 = x23;
                        y2 = y23;
                        x = x5;
                        y = y5;
                    } else {
                        x1 = x12 + lastX;
                        y1 = y12 + lastY;
                        x2 = x23 + lastX;
                        y2 = y23 + lastY;
                        x = x5 + lastX;
                        y = y5 + lastY;
                    }
                    p.cubicTo(x1, y1, x2, y2, x, y);
                    lastX1 = x2;
                    lastY1 = y2;
                    float lastX2 = x;
                    float lastY2 = y;
                    lastX = lastX2;
                    lastY = lastY2;
                    break;
                case 'H':
                case LocationRequest.PRIORITY_LOW_POWER /* 104 */:
                    float x6 = ph.nextFloat();
                    if (cmd == 'h') {
                        p.rLineTo(x6, 0.0f);
                        lastX += x6;
                        break;
                    } else {
                        p.lineTo(x6, lastY);
                        lastX = x6;
                        break;
                    }
                case UndoView.ACTION_GIGAGROUP_SUCCESS /* 76 */:
                case 'l':
                    float x7 = ph.nextFloat();
                    float y6 = ph.nextFloat();
                    if (cmd == 'l') {
                        p.rLineTo(x7, y6);
                        lastX += x7;
                        lastY += y6;
                        break;
                    } else {
                        p.lineTo(x7, y6);
                        lastX = x7;
                        lastY = y6;
                        break;
                    }
                case UndoView.ACTION_PAYMENT_SUCCESS /* 77 */:
                case 'm':
                    float x8 = ph.nextFloat();
                    float y7 = ph.nextFloat();
                    if (cmd == 'm') {
                        subPathStartX += x8;
                        subPathStartY += y7;
                        p.rMoveTo(x8, y7);
                        lastX += x8;
                        lastY += y7;
                        break;
                    } else {
                        p.moveTo(x8, y7);
                        subPathStartX = x8;
                        subPathStartY = y7;
                        lastX = x8;
                        lastY = y7;
                        break;
                    }
                case 'S':
                case 's':
                    wasCurve = true;
                    float x24 = ph.nextFloat();
                    float y24 = ph.nextFloat();
                    float x9 = ph.nextFloat();
                    float y8 = ph.nextFloat();
                    if (cmd != 's') {
                        x22 = x24;
                        y22 = y24;
                        x3 = x9;
                        y3 = y8;
                    } else {
                        x22 = x24 + lastX;
                        y22 = y24 + lastY;
                        x3 = x9 + lastX;
                        y3 = y8 + lastY;
                    }
                    p.cubicTo((lastX * 2.0f) - lastX1, (2.0f * lastY) - lastY1, x22, y22, x3, y3);
                    lastX1 = x22;
                    lastY1 = y22;
                    float lastX3 = x3;
                    float lastY3 = y3;
                    lastX = lastX3;
                    lastY = lastY3;
                    break;
                case 'V':
                case 'v':
                    float y9 = ph.nextFloat();
                    if (cmd == 'v') {
                        p.rLineTo(0.0f, y9);
                        lastY += y9;
                        break;
                    } else {
                        p.lineTo(lastX, y9);
                        lastY = y9;
                        break;
                    }
                case 'Z':
                case 'z':
                    p.close();
                    p.moveTo(subPathStartX, subPathStartY);
                    float lastX4 = subPathStartX;
                    float lastY4 = subPathStartY;
                    lastX1 = subPathStartX;
                    lastY1 = subPathStartY;
                    wasCurve = true;
                    lastX = lastX4;
                    lastY = lastY4;
                    break;
            }
            if (!wasCurve) {
                float lastX12 = lastX;
                lastX1 = lastX12;
                lastY1 = lastY;
            }
            ph.skipWhitespace();
            prevCmd2 = prevCmd;
        }
        return p;
    }

    private static void drawArc(Path p, float lastX, float lastY, float x, float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
    }

    public static NumberParse getNumberParseAttr(String name, Attributes attributes) {
        int n = attributes.getLength();
        for (int i = 0; i < n; i++) {
            if (attributes.getLocalName(i).equals(name)) {
                return parseNumbers(attributes.getValue(i));
            }
        }
        return null;
    }

    public static String getStringAttr(String name, Attributes attributes) {
        int n = attributes.getLength();
        for (int i = 0; i < n; i++) {
            if (attributes.getLocalName(i).equals(name)) {
                return attributes.getValue(i);
            }
        }
        return null;
    }

    public static Float getFloatAttr(String name, Attributes attributes) {
        return getFloatAttr(name, attributes, null);
    }

    public static Float getFloatAttr(String name, Attributes attributes, Float defaultValue) {
        String v = getStringAttr(name, attributes);
        if (v == null) {
            return defaultValue;
        }
        if (v.endsWith("px")) {
            v = v.substring(0, v.length() - 2);
        } else if (v.endsWith("mm")) {
            return null;
        }
        return Float.valueOf(Float.parseFloat(v));
    }

    private static Integer getHexAttr(String name, Attributes attributes) {
        String v = getStringAttr(name, attributes);
        if (v == null) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(v.substring(1), 16));
        } catch (NumberFormatException e) {
            return getColorByName(v);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static Integer getColorByName(String name) {
        char c;
        String lowerCase = name.toLowerCase();
        switch (lowerCase.hashCode()) {
            case -734239628:
                if (lowerCase.equals("yellow")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 112785:
                if (lowerCase.equals("red")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3027034:
                if (lowerCase.equals("blue")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 3068707:
                if (lowerCase.equals("cyan")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 3181155:
                if (lowerCase.equals("gray")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 93818879:
                if (lowerCase.equals("black")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 98619139:
                if (lowerCase.equals("green")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 113101865:
                if (lowerCase.equals("white")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 828922025:
                if (lowerCase.equals("magenta")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return -16777216;
            case 1:
                return -7829368;
            case 2:
                return Integer.valueOf((int) SupportMenu.CATEGORY_MASK);
            case 3:
                return -16711936;
            case 4:
                return -16776961;
            case 5:
                return Integer.valueOf((int) InputDeviceCompat.SOURCE_ANY);
            case 6:
                return -16711681;
            case 7:
                return -65281;
            case '\b':
                return -1;
            default:
                return null;
        }
    }

    /* loaded from: classes4.dex */
    public static class NumberParse {
        private int nextCmd;
        private ArrayList<Float> numbers;

        public NumberParse(ArrayList<Float> numbers, int nextCmd) {
            this.numbers = numbers;
            this.nextCmd = nextCmd;
        }

        public int getNextCmd() {
            return this.nextCmd;
        }

        public float getNumber(int index) {
            return this.numbers.get(index).floatValue();
        }
    }

    /* loaded from: classes4.dex */
    public static class StyleSet {
        HashMap<String, String> styleMap;

        private StyleSet(StyleSet styleSet) {
            HashMap<String, String> hashMap = new HashMap<>();
            this.styleMap = hashMap;
            hashMap.putAll(styleSet.styleMap);
        }

        private StyleSet(String string) {
            this.styleMap = new HashMap<>();
            String[] styles = string.split(";");
            for (String s : styles) {
                String[] style = s.split(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
                if (style.length == 2) {
                    this.styleMap.put(style[0].trim(), style[1].trim());
                }
            }
        }

        public String getStyle(String name) {
            return this.styleMap.get(name);
        }
    }

    /* loaded from: classes4.dex */
    public static class Properties {
        Attributes atts;
        ArrayList<StyleSet> styles;

        private Properties(Attributes atts, HashMap<String, StyleSet> globalStyles) {
            this.atts = atts;
            String styleAttr = SvgHelper.getStringAttr(TtmlNode.TAG_STYLE, atts);
            if (styleAttr == null) {
                String classAttr = SvgHelper.getStringAttr("class", atts);
                if (classAttr != null) {
                    this.styles = new ArrayList<>();
                    String[] args = classAttr.split(" ");
                    for (String str : args) {
                        StyleSet set = globalStyles.get(str.trim());
                        if (set != null) {
                            this.styles.add(set);
                        }
                    }
                    return;
                }
                return;
            }
            ArrayList<StyleSet> arrayList = new ArrayList<>();
            this.styles = arrayList;
            arrayList.add(new StyleSet(styleAttr));
        }

        public String getAttr(String name) {
            String v = null;
            ArrayList<StyleSet> arrayList = this.styles;
            if (arrayList != null && !arrayList.isEmpty()) {
                int N = this.styles.size();
                for (int a = 0; a < N; a++) {
                    v = this.styles.get(a).getStyle(name);
                    if (v != null) {
                        break;
                    }
                }
            }
            if (v == null) {
                return SvgHelper.getStringAttr(name, this.atts);
            }
            return v;
        }

        public String getString(String name) {
            return getAttr(name);
        }

        public Integer getHex(String name) {
            String v = getAttr(name);
            if (v == null) {
                return null;
            }
            try {
                return Integer.valueOf(Integer.parseInt(v.substring(1), 16));
            } catch (NumberFormatException e) {
                return SvgHelper.getColorByName(v);
            }
        }

        public Float getFloat(String name, float defaultValue) {
            Float v = getFloat(name);
            if (v == null) {
                return Float.valueOf(defaultValue);
            }
            return v;
        }

        public Float getFloat(String name) {
            String v = getAttr(name);
            if (v == null) {
                return null;
            }
            try {
                return Float.valueOf(Float.parseFloat(v));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class SVGHandler extends DefaultHandler {
        private Bitmap bitmap;
        private boolean boundsMode;
        private Canvas canvas;
        private int desiredHeight;
        private int desiredWidth;
        private SvgDrawable drawable;
        private float globalScale;
        private HashMap<String, StyleSet> globalStyles;
        private Paint paint;
        private Integer paintColor;
        boolean pushed;
        private RectF rect;
        private RectF rectTmp;
        private float scale;
        private StringBuilder styles;

        private SVGHandler(int dw, int dh, Integer color, boolean asDrawable, float scale) {
            this.scale = 1.0f;
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.rectTmp = new RectF();
            this.globalScale = 1.0f;
            this.pushed = false;
            this.globalStyles = new HashMap<>();
            this.globalScale = scale;
            this.desiredWidth = dw;
            this.desiredHeight = dh;
            this.paintColor = color;
            if (asDrawable) {
                this.drawable = new SvgDrawable();
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startDocument() {
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endDocument() {
        }

        private boolean doFill(Properties atts) {
            if ("none".equals(atts.getString(Constants.ScionAnalytics.MessageType.DISPLAY_NOTIFICATION))) {
                return false;
            }
            String fillString = atts.getString("fill");
            if (fillString != null && fillString.startsWith("url(#")) {
                fillString.substring("url(#".length(), fillString.length() - 1);
                return false;
            }
            Integer color = atts.getHex("fill");
            if (color != null) {
                doColor(atts, color, true);
                this.paint.setStyle(Paint.Style.FILL);
                return true;
            } else if (atts.getString("fill") != null || atts.getString("stroke") != null) {
                return false;
            } else {
                this.paint.setStyle(Paint.Style.FILL);
                Integer num = this.paintColor;
                if (num != null) {
                    this.paint.setColor(num.intValue());
                } else {
                    this.paint.setColor(-16777216);
                }
                return true;
            }
        }

        private boolean doStroke(Properties atts) {
            Integer color;
            if (!"none".equals(atts.getString(Constants.ScionAnalytics.MessageType.DISPLAY_NOTIFICATION)) && (color = atts.getHex("stroke")) != null) {
                doColor(atts, color, false);
                Float width = atts.getFloat("stroke-width");
                if (width != null) {
                    this.paint.setStrokeWidth(width.floatValue());
                }
                String linecap = atts.getString("stroke-linecap");
                if ("round".equals(linecap)) {
                    this.paint.setStrokeCap(Paint.Cap.ROUND);
                } else if ("square".equals(linecap)) {
                    this.paint.setStrokeCap(Paint.Cap.SQUARE);
                } else if ("butt".equals(linecap)) {
                    this.paint.setStrokeCap(Paint.Cap.BUTT);
                }
                String linejoin = atts.getString("stroke-linejoin");
                if ("miter".equals(linejoin)) {
                    this.paint.setStrokeJoin(Paint.Join.MITER);
                } else if ("round".equals(linejoin)) {
                    this.paint.setStrokeJoin(Paint.Join.ROUND);
                } else if ("bevel".equals(linejoin)) {
                    this.paint.setStrokeJoin(Paint.Join.BEVEL);
                }
                this.paint.setStyle(Paint.Style.STROKE);
                return true;
            }
            return false;
        }

        private void doColor(Properties atts, Integer color, boolean fillMode) {
            Integer num = this.paintColor;
            if (num != null) {
                this.paint.setColor(num.intValue());
            } else {
                int c = (16777215 & color.intValue()) | (-16777216);
                this.paint.setColor(c);
            }
            Float opacity = atts.getFloat("opacity");
            if (opacity == null) {
                opacity = atts.getFloat(fillMode ? "fill-opacity" : "stroke-opacity");
            }
            if (opacity == null) {
                this.paint.setAlpha(255);
            } else {
                this.paint.setAlpha((int) (opacity.floatValue() * 255.0f));
            }
        }

        private void pushTransform(Attributes atts) {
            String transform = SvgHelper.getStringAttr("transform", atts);
            boolean z = transform != null;
            this.pushed = z;
            if (z) {
                Matrix matrix = SvgHelper.parseTransform(transform);
                SvgDrawable svgDrawable = this.drawable;
                if (svgDrawable != null) {
                    svgDrawable.addCommand(matrix);
                    return;
                }
                this.canvas.save();
                this.canvas.concat(matrix);
            }
        }

        private void popTransform() {
            if (this.pushed) {
                SvgDrawable svgDrawable = this.drawable;
                if (svgDrawable != null) {
                    svgDrawable.addCommand(null);
                } else {
                    this.canvas.restore();
                }
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            int i;
            String viewBox;
            if (this.boundsMode && !localName.equals(TtmlNode.TAG_STYLE)) {
                return;
            }
            char c = 65535;
            switch (localName.hashCode()) {
                case -1656480802:
                    if (localName.equals("ellipse")) {
                        c = '\b';
                        break;
                    }
                    break;
                case -1360216880:
                    if (localName.equals("circle")) {
                        c = 7;
                        break;
                    }
                    break;
                case -397519558:
                    if (localName.equals("polygon")) {
                        c = '\t';
                        break;
                    }
                    break;
                case 103:
                    if (localName.equals(ImageLoader.AUTOPLAY_FILTER)) {
                        c = 4;
                        break;
                    }
                    break;
                case 114276:
                    if (localName.equals("svg")) {
                        c = 0;
                        break;
                    }
                    break;
                case 3079438:
                    if (localName.equals("defs")) {
                        c = 1;
                        break;
                    }
                    break;
                case 3321844:
                    if (localName.equals("line")) {
                        c = 6;
                        break;
                    }
                    break;
                case 3433509:
                    if (localName.equals("path")) {
                        c = 11;
                        break;
                    }
                    break;
                case 3496420:
                    if (localName.equals("rect")) {
                        c = 5;
                        break;
                    }
                    break;
                case 109780401:
                    if (localName.equals(TtmlNode.TAG_STYLE)) {
                        c = 3;
                        break;
                    }
                    break;
                case 561938880:
                    if (localName.equals("polyline")) {
                        c = '\n';
                        break;
                    }
                    break;
                case 917656469:
                    if (localName.equals("clipPath")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    Float w = SvgHelper.getFloatAttr("width", atts);
                    Float h = SvgHelper.getFloatAttr("height", atts);
                    if ((w == null || h == null) && (viewBox = SvgHelper.getStringAttr("viewBox", atts)) != null) {
                        String[] args = viewBox.split(" ");
                        w = Float.valueOf(Float.parseFloat(args[2]));
                        h = Float.valueOf(Float.parseFloat(args[3]));
                    }
                    if (w == null || h == null) {
                        w = Float.valueOf(this.desiredWidth);
                        h = Float.valueOf(this.desiredHeight);
                    }
                    int width = (int) Math.ceil(w.floatValue());
                    int height = (int) Math.ceil(h.floatValue());
                    if (width == 0 || height == 0) {
                        width = this.desiredWidth;
                        height = this.desiredHeight;
                    } else {
                        int i2 = this.desiredWidth;
                        if (i2 != 0 && (i = this.desiredHeight) != 0) {
                            float min = Math.min(i2 / width, i / height);
                            this.scale = min;
                            width = (int) (width * min);
                            height = (int) (height * min);
                        }
                    }
                    SvgDrawable svgDrawable = this.drawable;
                    if (svgDrawable == null) {
                        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        this.bitmap = createBitmap;
                        createBitmap.eraseColor(0);
                        Canvas canvas = new Canvas(this.bitmap);
                        this.canvas = canvas;
                        float f = this.scale;
                        if (f != 0.0f) {
                            float f2 = this.globalScale;
                            canvas.scale(f2 * f, f2 * f);
                            return;
                        }
                        return;
                    }
                    svgDrawable.width = width;
                    this.drawable.height = height;
                    return;
                case 1:
                case 2:
                    this.boundsMode = true;
                    return;
                case 3:
                    this.styles = new StringBuilder();
                    return;
                case 4:
                    if ("bounds".equalsIgnoreCase(SvgHelper.getStringAttr("id", atts))) {
                        this.boundsMode = true;
                        return;
                    }
                    return;
                case 5:
                    Float x = SvgHelper.getFloatAttr("x", atts);
                    if (x == null) {
                        x = Float.valueOf(0.0f);
                    }
                    Float y = SvgHelper.getFloatAttr("y", atts);
                    if (y == null) {
                        y = Float.valueOf(0.0f);
                    }
                    Float width2 = SvgHelper.getFloatAttr("width", atts);
                    Float height2 = SvgHelper.getFloatAttr("height", atts);
                    Float rx = SvgHelper.getFloatAttr("rx", atts, null);
                    pushTransform(atts);
                    Properties props = new Properties(atts, this.globalStyles);
                    if (doFill(props)) {
                        SvgDrawable svgDrawable2 = this.drawable;
                        if (svgDrawable2 != null) {
                            if (rx != null) {
                                svgDrawable2.addCommand(new RoundRect(new RectF(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue()), rx.floatValue()), this.paint);
                            } else {
                                svgDrawable2.addCommand(new RectF(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue()), this.paint);
                            }
                        } else if (rx == null) {
                            this.canvas.drawRect(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue(), this.paint);
                        } else {
                            this.rectTmp.set(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue());
                            this.canvas.drawRoundRect(this.rectTmp, rx.floatValue(), rx.floatValue(), this.paint);
                        }
                    }
                    if (doStroke(props)) {
                        SvgDrawable svgDrawable3 = this.drawable;
                        if (svgDrawable3 != null) {
                            if (rx != null) {
                                svgDrawable3.addCommand(new RoundRect(new RectF(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue()), rx.floatValue()), this.paint);
                            } else {
                                svgDrawable3.addCommand(new RectF(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue()), this.paint);
                            }
                        } else if (rx == null) {
                            this.canvas.drawRect(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue(), this.paint);
                        } else {
                            this.rectTmp.set(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue());
                            this.canvas.drawRoundRect(this.rectTmp, rx.floatValue(), rx.floatValue(), this.paint);
                        }
                    }
                    popTransform();
                    return;
                case 6:
                    Float x1 = SvgHelper.getFloatAttr("x1", atts);
                    Float x2 = SvgHelper.getFloatAttr("x2", atts);
                    Float y1 = SvgHelper.getFloatAttr("y1", atts);
                    Float y2 = SvgHelper.getFloatAttr("y2", atts);
                    if (doStroke(new Properties(atts, this.globalStyles))) {
                        pushTransform(atts);
                        SvgDrawable svgDrawable4 = this.drawable;
                        if (svgDrawable4 != null) {
                            svgDrawable4.addCommand(new Line(x1.floatValue(), y1.floatValue(), x2.floatValue(), y2.floatValue()), this.paint);
                        } else {
                            this.canvas.drawLine(x1.floatValue(), y1.floatValue(), x2.floatValue(), y2.floatValue(), this.paint);
                        }
                        popTransform();
                        return;
                    }
                    return;
                case 7:
                    Float centerX = SvgHelper.getFloatAttr("cx", atts);
                    Float centerY = SvgHelper.getFloatAttr("cy", atts);
                    Float radius = SvgHelper.getFloatAttr("r", atts);
                    if (centerX != null && centerY != null && radius != null) {
                        pushTransform(atts);
                        Properties props2 = new Properties(atts, this.globalStyles);
                        if (doFill(props2)) {
                            SvgDrawable svgDrawable5 = this.drawable;
                            if (svgDrawable5 != null) {
                                svgDrawable5.addCommand(new Circle(centerX.floatValue(), centerY.floatValue(), radius.floatValue()), this.paint);
                            } else {
                                this.canvas.drawCircle(centerX.floatValue(), centerY.floatValue(), radius.floatValue(), this.paint);
                            }
                        }
                        if (doStroke(props2)) {
                            SvgDrawable svgDrawable6 = this.drawable;
                            if (svgDrawable6 != null) {
                                svgDrawable6.addCommand(new Circle(centerX.floatValue(), centerY.floatValue(), radius.floatValue()), this.paint);
                            } else {
                                this.canvas.drawCircle(centerX.floatValue(), centerY.floatValue(), radius.floatValue(), this.paint);
                            }
                        }
                        popTransform();
                        return;
                    }
                    return;
                case '\b':
                    Float centerX2 = SvgHelper.getFloatAttr("cx", atts);
                    Float centerY2 = SvgHelper.getFloatAttr("cy", atts);
                    Float radiusX = SvgHelper.getFloatAttr("rx", atts);
                    Float radiusY = SvgHelper.getFloatAttr("ry", atts);
                    if (centerX2 != null && centerY2 != null && radiusX != null && radiusY != null) {
                        pushTransform(atts);
                        Properties props3 = new Properties(atts, this.globalStyles);
                        this.rect.set(centerX2.floatValue() - radiusX.floatValue(), centerY2.floatValue() - radiusY.floatValue(), centerX2.floatValue() + radiusX.floatValue(), centerY2.floatValue() + radiusY.floatValue());
                        if (doFill(props3)) {
                            SvgDrawable svgDrawable7 = this.drawable;
                            if (svgDrawable7 != null) {
                                svgDrawable7.addCommand(new Oval(this.rect), this.paint);
                            } else {
                                this.canvas.drawOval(this.rect, this.paint);
                            }
                        }
                        if (doStroke(props3)) {
                            SvgDrawable svgDrawable8 = this.drawable;
                            if (svgDrawable8 != null) {
                                svgDrawable8.addCommand(new Oval(this.rect), this.paint);
                            } else {
                                this.canvas.drawOval(this.rect, this.paint);
                            }
                        }
                        popTransform();
                        return;
                    }
                    return;
                case '\t':
                case '\n':
                    NumberParse numbers = SvgHelper.getNumberParseAttr("points", atts);
                    if (numbers != null) {
                        Path p = new Path();
                        ArrayList<Float> points = numbers.numbers;
                        if (points.size() > 1) {
                            pushTransform(atts);
                            Properties props4 = new Properties(atts, this.globalStyles);
                            p.moveTo(points.get(0).floatValue(), points.get(1).floatValue());
                            for (int i3 = 2; i3 < points.size(); i3 += 2) {
                                p.lineTo(points.get(i3).floatValue(), points.get(i3 + 1).floatValue());
                            }
                            if (localName.equals("polygon")) {
                                p.close();
                            }
                            if (doFill(props4)) {
                                SvgDrawable svgDrawable9 = this.drawable;
                                if (svgDrawable9 != null) {
                                    svgDrawable9.addCommand(p, this.paint);
                                } else {
                                    this.canvas.drawPath(p, this.paint);
                                }
                            }
                            if (doStroke(props4)) {
                                SvgDrawable svgDrawable10 = this.drawable;
                                if (svgDrawable10 != null) {
                                    svgDrawable10.addCommand(p, this.paint);
                                } else {
                                    this.canvas.drawPath(p, this.paint);
                                }
                            }
                            popTransform();
                            return;
                        }
                        return;
                    }
                    return;
                case 11:
                    Path p2 = SvgHelper.doPath(SvgHelper.getStringAttr(Theme.DEFAULT_BACKGROUND_SLUG, atts));
                    pushTransform(atts);
                    Properties props5 = new Properties(atts, this.globalStyles);
                    if (doFill(props5)) {
                        SvgDrawable svgDrawable11 = this.drawable;
                        if (svgDrawable11 != null) {
                            svgDrawable11.addCommand(p2, this.paint);
                        } else {
                            this.canvas.drawPath(p2, this.paint);
                        }
                    }
                    if (doStroke(props5)) {
                        SvgDrawable svgDrawable12 = this.drawable;
                        if (svgDrawable12 != null) {
                            svgDrawable12.addCommand(p2, this.paint);
                        } else {
                            this.canvas.drawPath(p2, this.paint);
                        }
                    }
                    popTransform();
                    return;
                default:
                    return;
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void characters(char[] ch, int start, int length) {
            StringBuilder sb = this.styles;
            if (sb != null) {
                sb.append(ch, start, length);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String namespaceURI, String localName, String qName) {
            char c;
            int idx1;
            switch (localName.hashCode()) {
                case 103:
                    if (localName.equals(ImageLoader.AUTOPLAY_FILTER)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 114276:
                    if (localName.equals("svg")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 3079438:
                    if (localName.equals("defs")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 109780401:
                    if (localName.equals(TtmlNode.TAG_STYLE)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 917656469:
                    if (localName.equals("clipPath")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    StringBuilder sb = this.styles;
                    if (sb != null) {
                        String[] args = sb.toString().split("\\}");
                        for (int a = 0; a < args.length; a++) {
                            args[a] = args[a].trim().replace("\t", "").replace("\n", "");
                            if (args[a].length() != 0 && args[a].charAt(0) == '.' && (idx1 = args[a].indexOf(123)) >= 0) {
                                String name = args[a].substring(1, idx1).trim();
                                String style = args[a].substring(idx1 + 1);
                                this.globalStyles.put(name, new StyleSet(style));
                            }
                        }
                        this.styles = null;
                        return;
                    }
                    return;
                case 1:
                default:
                    return;
                case 2:
                case 3:
                case 4:
                    this.boundsMode = false;
                    return;
            }
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }

        public SvgDrawable getDrawable() {
            return this.drawable;
        }
    }

    static {
        int i = 0;
        while (true) {
            double[] dArr = pow10;
            if (i < dArr.length) {
                dArr[i] = Math.pow(10.0d, i);
                i++;
            } else {
                return;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class ParserHelper {
        private char current;
        private int n;
        public int pos;
        private CharSequence s;

        public ParserHelper(CharSequence s, int pos) {
            this.s = s;
            this.pos = pos;
            this.n = s.length();
            this.current = s.charAt(pos);
        }

        private char read() {
            int i = this.pos;
            int i2 = this.n;
            if (i < i2) {
                this.pos = i + 1;
            }
            int i3 = this.pos;
            if (i3 == i2) {
                return (char) 0;
            }
            return this.s.charAt(i3);
        }

        public void skipWhitespace() {
            while (true) {
                int i = this.pos;
                if (i < this.n && Character.isWhitespace(this.s.charAt(i))) {
                    advance();
                } else {
                    return;
                }
            }
        }

        public void skipNumberSeparator() {
            while (true) {
                int i = this.pos;
                if (i < this.n) {
                    char c = this.s.charAt(i);
                    switch (c) {
                        case '\t':
                        case '\n':
                        case ' ':
                        case ',':
                            advance();
                        default:
                            return;
                    }
                } else {
                    return;
                }
            }
        }

        public void advance() {
            this.current = read();
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:44:0x00a7  */
        /* JADX WARN: Removed duplicated region for block: B:48:0x00b1 A[LOOP:4: B:48:0x00b1->B:49:0x00b7, LOOP_START] */
        /* JADX WARN: Removed duplicated region for block: B:52:0x00bf  */
        /* JADX WARN: Removed duplicated region for block: B:72:0x00d5 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:73:0x00bc A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public float parseFloat() {
            /*
                Method dump skipped, instructions count: 554
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.ParserHelper.parseFloat():float");
        }

        private void reportUnexpectedCharacterError(char c) {
            throw new RuntimeException("Unexpected char '" + c + "'.");
        }

        public float buildFloat(int mant, int exp) {
            double d;
            if (exp < -125 || mant == 0) {
                return 0.0f;
            }
            if (exp >= 128) {
                return mant > 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
            } else if (exp == 0) {
                return mant;
            } else {
                if (mant >= 67108864) {
                    mant++;
                }
                double d2 = mant;
                double[] dArr = SvgHelper.pow10;
                if (exp > 0) {
                    double d3 = dArr[exp];
                    Double.isNaN(d2);
                    d = d2 * d3;
                } else {
                    double d4 = dArr[-exp];
                    Double.isNaN(d2);
                    d = d2 / d4;
                }
                return (float) d;
            }
        }

        public float nextFloat() {
            skipWhitespace();
            float f = parseFloat();
            skipNumberSeparator();
            return f;
        }
    }

    public static String decompress(byte[] encoded) {
        try {
            StringBuilder path = new StringBuilder(encoded.length * 2);
            path.append('M');
            for (byte b : encoded) {
                int num = b & 255;
                if (num >= 192) {
                    int start = (num - 128) - 64;
                    path.append("AACAAAAHAAALMAAAQASTAVAAAZaacaaaahaaalmaaaqastava.az0123456789-,".charAt(start));
                } else {
                    if (num >= 128) {
                        path.append(',');
                    } else if (num >= 64) {
                        path.append('-');
                    }
                    path.append(num & 63);
                }
            }
            path.append('z');
            return path.toString();
        } catch (Exception e) {
            FileLog.e(e);
            return "";
        }
    }
}
