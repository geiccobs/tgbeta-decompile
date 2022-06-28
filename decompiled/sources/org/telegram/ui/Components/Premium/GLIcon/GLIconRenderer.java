package org.telegram.ui.Components.Premium.GLIcon;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import androidx.core.graphics.ColorUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class GLIconRenderer implements GLSurfaceView.Renderer {
    public static final int DIALOG_STYLE = 1;
    public static final int FRAGMENT_STYLE = 0;
    private static final float Z_FAR = 200.0f;
    private static final float Z_NEAR = 1.0f;
    Bitmap backgroundBitmap;
    int color1;
    int color2;
    Context context;
    public float gradientScaleX;
    public float gradientScaleY;
    public float gradientStartX;
    public float gradientStartY;
    public boolean isDarkBackground;
    private int mHeight;
    private int mWidth;
    public Star3DIcon star;
    private final int style;
    public float angleX = 0.0f;
    public float angleX2 = 0.0f;
    public float angleY = 0.0f;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    public String colorKey1 = Theme.key_premiumStartGradient1;
    public String colorKey2 = Theme.key_premiumStartGradient2;

    public GLIconRenderer(Context context, int style) {
        this.context = context;
        this.style = style;
        updateColors();
    }

    public static int loadShader(int type, String shaderSrc) {
        int[] compiled = new int[1];
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            return 0;
        }
        GLES20.glShaderSource(shader, shaderSrc);
        GLES20.glCompileShader(shader);
        GLES20.glGetShaderiv(shader, 35713, compiled, 0);
        if (compiled[0] == 0) {
            throw new RuntimeException("Could not compile program: " + GLES20.glGetShaderInfoLog(shader) + " " + shaderSrc);
        }
        return shader;
    }

    public static void checkGlError(String glOperation, int program) {
        int error = GLES20.glGetError();
        if (error != 0) {
            throw new RuntimeException(glOperation + ": glError " + error + GLES20.glGetShaderInfoLog(program));
        }
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Star3DIcon star3DIcon = this.star;
        if (star3DIcon != null) {
            star3DIcon.destroy();
        }
        Star3DIcon star3DIcon2 = new Star3DIcon(this.context);
        this.star = star3DIcon2;
        Bitmap bitmap = this.backgroundBitmap;
        if (bitmap != null) {
            star3DIcon2.setBackground(bitmap);
        }
        if (this.isDarkBackground) {
            this.star.spec1 = 1.0f;
            this.star.spec2 = 0.2f;
        }
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(16640);
        GLES20.glEnable(2929);
        Matrix.setLookAtM(this.mViewMatrix, 0, 0.0f, 0.0f, 100.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.setIdentityM(this.mRotationMatrix, 0);
        Matrix.translateM(this.mRotationMatrix, 0, 0.0f, this.angleX2, 0.0f);
        Matrix.rotateM(this.mRotationMatrix, 0, -this.angleY, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(this.mRotationMatrix, 0, -this.angleX, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(this.mMVPMatrix, 0, this.mViewMatrix, 0, this.mRotationMatrix, 0);
        float[] fArr = this.mMVPMatrix;
        Matrix.multiplyMM(fArr, 0, this.mProjectionMatrix, 0, fArr, 0);
        Star3DIcon star3DIcon = this.star;
        if (star3DIcon != null) {
            star3DIcon.gradientColor1 = this.color1;
            this.star.gradientColor2 = this.color2;
            this.star.draw(this.mMVPMatrix, this.mRotationMatrix, this.mWidth, this.mHeight, this.gradientStartX, this.gradientScaleX, this.gradientStartY, this.gradientScaleY);
        }
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        GLES20.glViewport(0, 0, width, height);
        float aspect = width / height;
        Matrix.perspectiveM(this.mProjectionMatrix, 0, 53.13f, aspect, 1.0f, 200.0f);
    }

    public void setBackground(Bitmap gradientTextureBitmap) {
        Star3DIcon star3DIcon = this.star;
        if (star3DIcon != null) {
            star3DIcon.setBackground(gradientTextureBitmap);
        }
        this.backgroundBitmap = gradientTextureBitmap;
    }

    public void updateColors() {
        this.color1 = Theme.getColor(this.colorKey1);
        this.color2 = Theme.getColor(this.colorKey2);
        boolean z = true;
        if (this.style != 1 || ColorUtils.calculateLuminance(Theme.getColor(Theme.key_dialogBackground)) >= 0.5d) {
            z = false;
        }
        this.isDarkBackground = z;
    }
}
