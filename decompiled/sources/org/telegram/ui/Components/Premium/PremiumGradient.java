package org.telegram.ui.Components.Premium;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class PremiumGradient {
    private static PremiumGradient instance = null;
    private static final int size = 100;
    private static final int sizeHalf = 50;
    private int lastStarColor;
    Paint lockedPremiumPaint;
    private final GradientTools mainGradient;
    private final Paint mainGradientPaint;
    public Drawable premiumStarDrawableMini = ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_premium_liststar).mutate();
    public InternalDrawable premiumStarMenuDrawable = createGradientDrawable(ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_settings_premium));
    public InternalDrawable premiumStarMenuDrawable2 = createGradientDrawable(ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_premium_normal));
    public Drawable premiumStarColoredDrawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_premium_liststar).mutate();

    public static PremiumGradient getInstance() {
        if (instance == null) {
            instance = new PremiumGradient();
        }
        return instance;
    }

    private PremiumGradient() {
        GradientTools gradientTools = new GradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, Theme.key_premiumGradient3, Theme.key_premiumGradient4);
        this.mainGradient = gradientTools;
        this.mainGradientPaint = gradientTools.paint;
        gradientTools.chekColors();
        checkIconColors();
    }

    public InternalDrawable createGradientDrawable(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getMinimumHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        this.mainGradient.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.mainGradient.gradientMatrix(0, 0, width, height, -width, 0.0f);
        canvas.drawRect(0.0f, 0.0f, width, height, this.mainGradient.paint);
        this.mainGradient.paint.setXfermode(null);
        return new InternalDrawable(drawable, bitmap, this.mainGradient.colors);
    }

    public void checkIconColors() {
        if (Theme.getColor(Theme.key_chats_verifiedBackground) != this.lastStarColor) {
            this.lastStarColor = Theme.getColor(Theme.key_chats_verifiedBackground);
            this.premiumStarDrawableMini.setColorFilter(new PorterDuffColorFilter(this.lastStarColor, PorterDuff.Mode.MULTIPLY));
        }
        this.premiumStarMenuDrawable = checkColors(this.premiumStarMenuDrawable);
        this.premiumStarMenuDrawable2 = checkColors(this.premiumStarMenuDrawable2);
    }

    private InternalDrawable checkColors(InternalDrawable internalDrawable) {
        if (this.mainGradient.colors[0] != internalDrawable.colors[0] || this.mainGradient.colors[1] != internalDrawable.colors[1] || this.mainGradient.colors[2] != internalDrawable.colors[2] || this.mainGradient.colors[3] != internalDrawable.colors[3]) {
            return createGradientDrawable(internalDrawable.originDrawable);
        }
        return internalDrawable;
    }

    public void updateMainGradientMatrix(int x, int y, int width, int height, float xOffset, float yOffset) {
        this.mainGradient.gradientMatrix(x, y, width, height, xOffset, yOffset);
    }

    /* loaded from: classes5.dex */
    public static class InternalDrawable extends BitmapDrawable {
        public int[] colors;
        Drawable originDrawable;

        public InternalDrawable(Drawable originDrawable, Bitmap bitmap, int[] colors) {
            super(ApplicationLoader.applicationContext.getResources(), bitmap);
            this.originDrawable = originDrawable;
            int[] iArr = new int[colors.length];
            this.colors = iArr;
            System.arraycopy(colors, 0, iArr, 0, colors.length);
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(int color, PorterDuff.Mode mode) {
        }
    }

    public Paint getMainGradientPaint() {
        if (MessagesController.getInstance(UserConfig.selectedAccount).premiumLocked) {
            if (this.lockedPremiumPaint == null) {
                this.lockedPremiumPaint = new Paint(1);
            }
            this.lockedPremiumPaint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
            return this.lockedPremiumPaint;
        }
        return this.mainGradientPaint;
    }

    /* loaded from: classes5.dex */
    public static class GradientTools {
        final String colorKey1;
        final String colorKey2;
        final String colorKey3;
        final String colorKey4;
        public boolean exactly;
        Shader shader;
        public float cx = 0.5f;
        public float cy = 0.5f;
        Matrix matrix = new Matrix();
        public final Paint paint = new Paint(1);
        final int[] colors = new int[4];
        public float x1 = 0.0f;
        public float y1 = 1.0f;
        public float x2 = 1.5f;
        public float y2 = 0.0f;

        public GradientTools(String colorKey1, String colorKey2, String colorKey3, String colorKey4) {
            this.colorKey1 = colorKey1;
            this.colorKey2 = colorKey2;
            this.colorKey3 = colorKey3;
            this.colorKey4 = colorKey4;
        }

        public void gradientMatrix(int x, int y, int x1, int y1, float xOffset, float yOffset) {
            chekColors();
            if (this.exactly) {
                float sx = (x1 - x) / 100.0f;
                float sy = (y1 - y) / 100.0f;
                this.matrix.reset();
                this.matrix.postScale(sx, sy, this.cx * 100.0f, this.cy * 100.0f);
                this.matrix.postTranslate(xOffset, yOffset);
                this.shader.setLocalMatrix(this.matrix);
                return;
            }
            int height = y1 - y;
            int gradientHeight = height + height;
            float sx2 = (x1 - x) / 100.0f;
            float sy2 = gradientHeight / 100.0f;
            chekColors();
            this.matrix.reset();
            this.matrix.postScale(sx2, sy2, 75.0f, 50.0f);
            this.matrix.postTranslate(xOffset, (-gradientHeight) + yOffset);
            this.shader.setLocalMatrix(this.matrix);
        }

        public void chekColors() {
            int c1 = Theme.getColor(this.colorKey1);
            int c2 = Theme.getColor(this.colorKey2);
            String str = this.colorKey3;
            int c3 = str == null ? 0 : Theme.getColor(str);
            String str2 = this.colorKey4;
            int c4 = str2 == null ? 0 : Theme.getColor(str2);
            int[] iArr = this.colors;
            if (iArr[0] != c1 || iArr[1] != c2 || iArr[2] != c3 || iArr[3] != c4) {
                iArr[0] = c1;
                iArr[1] = c2;
                iArr[2] = c3;
                iArr[3] = c4;
                if (c3 == 0) {
                    float f = this.x1 * 100.0f;
                    float f2 = this.y1 * 100.0f;
                    float f3 = this.x2 * 100.0f;
                    float f4 = this.y2 * 100.0f;
                    int[] iArr2 = this.colors;
                    this.shader = new LinearGradient(f, f2, f3, f4, new int[]{iArr2[0], iArr2[1]}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                } else if (c4 == 0) {
                    float f5 = this.x1 * 100.0f;
                    float f6 = this.y1 * 100.0f;
                    float f7 = this.x2 * 100.0f;
                    float f8 = this.y2 * 100.0f;
                    int[] iArr3 = this.colors;
                    this.shader = new LinearGradient(f5, f6, f7, f8, new int[]{iArr3[0], iArr3[1], iArr3[2]}, new float[]{0.0f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
                } else {
                    float f9 = 100.0f * this.y2;
                    int[] iArr4 = this.colors;
                    this.shader = new LinearGradient(this.x1 * 100.0f, this.y1 * 100.0f, this.x2 * 100.0f, f9, new int[]{iArr4[0], iArr4[1], iArr4[2], iArr4[3]}, new float[]{0.0f, 0.5f, 0.78f, 1.0f}, Shader.TileMode.CLAMP);
                }
                this.shader.setLocalMatrix(this.matrix);
                this.paint.setShader(this.shader);
            }
        }

        public void gradientMatrixLinear(float totalHeight, float offset) {
            chekColors();
            this.matrix.reset();
            this.matrix.postScale(1.0f, totalHeight / 100.0f, 0.0f, 0.0f);
            this.matrix.postTranslate(0.0f, offset);
            this.shader.setLocalMatrix(this.matrix);
        }
    }
}
