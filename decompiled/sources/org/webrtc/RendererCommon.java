package org.webrtc;

import android.graphics.Point;
import android.opengl.Matrix;
/* loaded from: classes5.dex */
public class RendererCommon {
    private static float BALANCED_VISIBLE_FRACTION = 0.5625f;

    /* loaded from: classes5.dex */
    public interface GlDrawer {
        void drawOes(int i, int i2, int i3, int i4, int i5, float[] fArr, int i6, int i7, int i8, int i9, int i10, int i11, boolean z);

        void drawRgb(int i, int i2, int i3, int i4, int i5, float[] fArr, int i6, int i7, int i8, int i9, int i10, int i11, boolean z);

        void drawYuv(int[] iArr, int i, int i2, int i3, int i4, float[] fArr, int i5, int i6, int i7, int i8, int i9, int i10, boolean z);

        void release();
    }

    /* loaded from: classes5.dex */
    public interface RendererEvents {
        void onFirstFrameRendered();

        void onFrameResolutionChanged(int i, int i2, int i3);
    }

    /* loaded from: classes5.dex */
    public enum ScalingType {
        SCALE_ASPECT_FIT,
        SCALE_ASPECT_FILL,
        SCALE_ASPECT_BALANCED
    }

    /* loaded from: classes5.dex */
    public static class VideoLayoutMeasure {
        private float visibleFractionMatchOrientation = RendererCommon.convertScalingTypeToVisibleFraction(ScalingType.SCALE_ASPECT_BALANCED);
        private float visibleFractionMismatchOrientation = RendererCommon.convertScalingTypeToVisibleFraction(ScalingType.SCALE_ASPECT_BALANCED);

        public void setScalingType(ScalingType scalingType) {
            setScalingType(scalingType, scalingType);
        }

        public void setScalingType(ScalingType scalingTypeMatchOrientation, ScalingType scalingTypeMismatchOrientation) {
            this.visibleFractionMatchOrientation = RendererCommon.convertScalingTypeToVisibleFraction(scalingTypeMatchOrientation);
            this.visibleFractionMismatchOrientation = RendererCommon.convertScalingTypeToVisibleFraction(scalingTypeMismatchOrientation);
        }

        public void setVisibleFraction(float visibleFractionMatchOrientation, float visibleFractionMismatchOrientation) {
            this.visibleFractionMatchOrientation = visibleFractionMatchOrientation;
            this.visibleFractionMismatchOrientation = visibleFractionMismatchOrientation;
        }

        /* JADX WARN: Code restructure failed: missing block: B:32:0x0061, code lost:
            if (r14 == r9) goto L33;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public android.graphics.Point measure(boolean r17, int r18, int r19, int r20, int r21) {
            /*
                r16 = this;
                r0 = r16
                r1 = r20
                r2 = r21
                r3 = 2147483647(0x7fffffff, float:NaN)
                r4 = r18
                int r5 = android.view.View.getDefaultSize(r3, r4)
                r6 = r19
                int r3 = android.view.View.getDefaultSize(r3, r6)
                if (r1 == 0) goto L66
                if (r2 == 0) goto L66
                if (r5 == 0) goto L66
                if (r3 != 0) goto L1e
                goto L66
            L1e:
                float r7 = (float) r1
                float r8 = (float) r2
                float r7 = r7 / r8
                float r8 = (float) r5
                float r9 = (float) r3
                float r8 = r8 / r9
                r9 = 1
                r10 = 0
                r11 = 1065353216(0x3f800000, float:1.0)
                int r12 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
                if (r12 <= 0) goto L2e
                r12 = 1
                goto L2f
            L2e:
                r12 = 0
            L2f:
                int r13 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
                if (r13 <= 0) goto L35
                r13 = 1
                goto L36
            L35:
                r13 = 0
            L36:
                if (r12 != r13) goto L3b
                float r12 = r0.visibleFractionMatchOrientation
                goto L3d
            L3b:
                float r12 = r0.visibleFractionMismatchOrientation
            L3d:
                android.graphics.Point r13 = org.webrtc.RendererCommon.getDisplaySize(r12, r7, r5, r3)
                if (r17 != 0) goto L65
                int r14 = android.view.View.MeasureSpec.getMode(r18)
                r15 = 1073741824(0x40000000, float:2.0)
                if (r14 != r15) goto L4e
                r13.x = r5
            L4e:
                int r14 = android.view.View.MeasureSpec.getMode(r19)
                if (r14 == r15) goto L63
                int r14 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
                if (r14 <= 0) goto L5a
                r14 = 1
                goto L5b
            L5a:
                r14 = 0
            L5b:
                int r11 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
                if (r11 <= 0) goto L60
                goto L61
            L60:
                r9 = 0
            L61:
                if (r14 != r9) goto L65
            L63:
                r13.y = r3
            L65:
                return r13
            L66:
                android.graphics.Point r7 = new android.graphics.Point
                r7.<init>(r5, r3)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.RendererCommon.VideoLayoutMeasure.measure(boolean, int, int, int, int):android.graphics.Point");
        }
    }

    public static float[] getLayoutMatrix(boolean mirror, float videoAspectRatio, float displayAspectRatio) {
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        if (displayAspectRatio > videoAspectRatio) {
            scaleY = videoAspectRatio / displayAspectRatio;
        } else {
            scaleX = displayAspectRatio / videoAspectRatio;
        }
        if (mirror) {
            scaleX *= -1.0f;
        }
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        Matrix.scaleM(matrix, 0, scaleX, scaleY, 1.0f);
        adjustOrigin(matrix);
        return matrix;
    }

    public static android.graphics.Matrix convertMatrixToAndroidGraphicsMatrix(float[] matrix4x4) {
        float[] values = {matrix4x4[0], matrix4x4[4], matrix4x4[12], matrix4x4[1], matrix4x4[5], matrix4x4[13], matrix4x4[3], matrix4x4[7], matrix4x4[15]};
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.setValues(values);
        return matrix;
    }

    public static float[] convertMatrixFromAndroidGraphicsMatrix(android.graphics.Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        float[] matrix4x4 = {values[0], values[3], 0.0f, values[6], values[1], values[4], 0.0f, values[7], 0.0f, 0.0f, 1.0f, 0.0f, values[2], values[5], 0.0f, values[8]};
        return matrix4x4;
    }

    public static Point getDisplaySize(ScalingType scalingType, float videoAspectRatio, int maxDisplayWidth, int maxDisplayHeight) {
        return getDisplaySize(convertScalingTypeToVisibleFraction(scalingType), videoAspectRatio, maxDisplayWidth, maxDisplayHeight);
    }

    private static void adjustOrigin(float[] matrix) {
        matrix[12] = matrix[12] - ((matrix[0] + matrix[4]) * 0.5f);
        matrix[13] = matrix[13] - ((matrix[1] + matrix[5]) * 0.5f);
        matrix[12] = matrix[12] + 0.5f;
        matrix[13] = matrix[13] + 0.5f;
    }

    /* renamed from: org.webrtc.RendererCommon$1 */
    /* loaded from: classes5.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$RendererCommon$ScalingType;

        static {
            int[] iArr = new int[ScalingType.values().length];
            $SwitchMap$org$webrtc$RendererCommon$ScalingType = iArr;
            try {
                iArr[ScalingType.SCALE_ASPECT_FIT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$webrtc$RendererCommon$ScalingType[ScalingType.SCALE_ASPECT_FILL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$webrtc$RendererCommon$ScalingType[ScalingType.SCALE_ASPECT_BALANCED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static float convertScalingTypeToVisibleFraction(ScalingType scalingType) {
        switch (AnonymousClass1.$SwitchMap$org$webrtc$RendererCommon$ScalingType[scalingType.ordinal()]) {
            case 1:
                return 1.0f;
            case 2:
                return 0.0f;
            case 3:
                return BALANCED_VISIBLE_FRACTION;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Point getDisplaySize(float minVisibleFraction, float videoAspectRatio, int maxDisplayWidth, int maxDisplayHeight) {
        if (minVisibleFraction == 0.0f || videoAspectRatio == 0.0f) {
            return new Point(maxDisplayWidth, maxDisplayHeight);
        }
        int width = Math.min(maxDisplayWidth, Math.round((maxDisplayHeight / minVisibleFraction) * videoAspectRatio));
        int height = Math.min(maxDisplayHeight, Math.round((maxDisplayWidth / minVisibleFraction) / videoAspectRatio));
        return new Point(width, height);
    }
}
