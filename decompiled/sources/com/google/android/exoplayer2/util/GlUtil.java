package com.google.android.exoplayer2.util;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLDisplay;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.text.TextUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
/* loaded from: classes3.dex */
public final class GlUtil {
    private static final String EXTENSION_PROTECTED_CONTENT = "EGL_EXT_protected_content";
    private static final String EXTENSION_SURFACELESS_CONTEXT = "EGL_KHR_surfaceless_context";
    private static final String TAG = "GlUtil";

    /* loaded from: classes3.dex */
    public static final class Attribute {
        private Buffer buffer;
        private final int index;
        private final int location;
        public final String name;
        private int size;

        public Attribute(int program, int index) {
            int[] len = new int[1];
            GLES20.glGetProgramiv(program, 35722, len, 0);
            int[] type = new int[1];
            int[] size = new int[1];
            byte[] nameBytes = new byte[len[0]];
            int[] ignore = new int[1];
            GLES20.glGetActiveAttrib(program, index, len[0], ignore, 0, size, 0, type, 0, nameBytes, 0);
            String str = new String(nameBytes, 0, GlUtil.strlen(nameBytes));
            this.name = str;
            this.location = GLES20.glGetAttribLocation(program, str);
            this.index = index;
        }

        public void setBuffer(float[] buffer, int size) {
            this.buffer = GlUtil.createBuffer(buffer);
            this.size = size;
        }

        public void bind() {
            Buffer buffer = (Buffer) Assertions.checkNotNull(this.buffer, "call setBuffer before bind");
            GLES20.glBindBuffer(34962, 0);
            GLES20.glVertexAttribPointer(this.location, this.size, 5126, false, 0, buffer);
            GLES20.glEnableVertexAttribArray(this.index);
            GlUtil.checkGlError();
        }
    }

    /* loaded from: classes3.dex */
    public static final class Uniform {
        private final int location;
        public final String name;
        private int texId;
        private final int type;
        private int unit;
        private final float[] value = new float[1];

        public Uniform(int program, int index) {
            int[] len = new int[1];
            GLES20.glGetProgramiv(program, 35719, len, 0);
            int[] type = new int[1];
            int[] size = new int[1];
            byte[] name = new byte[len[0]];
            int[] ignore = new int[1];
            GLES20.glGetActiveUniform(program, index, len[0], ignore, 0, size, 0, type, 0, name, 0);
            String str = new String(name, 0, GlUtil.strlen(name));
            this.name = str;
            this.location = GLES20.glGetUniformLocation(program, str);
            this.type = type[0];
        }

        public void setSamplerTexId(int texId, int unit) {
            this.texId = texId;
            this.unit = unit;
        }

        public void setFloat(float value) {
            this.value[0] = value;
        }

        public void bind() {
            if (this.type == 5126) {
                GLES20.glUniform1fv(this.location, 1, this.value, 0);
                GlUtil.checkGlError();
            } else if (this.texId == 0) {
                throw new IllegalStateException("call setSamplerTexId before bind");
            } else {
                GLES20.glActiveTexture(this.unit + 33984);
                int i = this.type;
                if (i == 36198) {
                    GLES20.glBindTexture(36197, this.texId);
                } else if (i == 35678) {
                    GLES20.glBindTexture(3553, this.texId);
                } else {
                    throw new IllegalStateException("unexpected uniform type: " + this.type);
                }
                GLES20.glUniform1i(this.location, this.unit);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                GlUtil.checkGlError();
            }
        }
    }

    private GlUtil() {
    }

    public static boolean isProtectedContentExtensionSupported(Context context) {
        if (Util.SDK_INT < 24) {
            return false;
        }
        if (Util.SDK_INT < 26 && ("samsung".equals(Util.MANUFACTURER) || "XT1650".equals(Util.MODEL))) {
            return false;
        }
        if (Util.SDK_INT < 26 && !context.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance")) {
            return false;
        }
        EGLDisplay display = EGL14.eglGetDisplay(0);
        String eglExtensions = EGL14.eglQueryString(display, 12373);
        return eglExtensions != null && eglExtensions.contains(EXTENSION_PROTECTED_CONTENT);
    }

    public static boolean isSurfacelessContextExtensionSupported() {
        if (Util.SDK_INT < 17) {
            return false;
        }
        EGLDisplay display = EGL14.eglGetDisplay(0);
        String eglExtensions = EGL14.eglQueryString(display, 12373);
        return eglExtensions != null && eglExtensions.contains(EXTENSION_SURFACELESS_CONTEXT);
    }

    public static void checkGlError() {
        while (true) {
            int error = GLES20.glGetError();
            if (error != 0) {
                Log.e(TAG, "glError " + GLU.gluErrorString(error));
            } else {
                return;
            }
        }
    }

    public static int compileProgram(String[] vertexCode, String[] fragmentCode) {
        return compileProgram(TextUtils.join("\n", vertexCode), TextUtils.join("\n", fragmentCode));
    }

    public static int compileProgram(String vertexCode, String fragmentCode) {
        int program = GLES20.glCreateProgram();
        checkGlError();
        addShader(35633, vertexCode, program);
        addShader(35632, fragmentCode, program);
        GLES20.glLinkProgram(program);
        int[] linkStatus = {0};
        GLES20.glGetProgramiv(program, 35714, linkStatus, 0);
        if (linkStatus[0] != 1) {
            throwGlError("Unable to link shader program: \n" + GLES20.glGetProgramInfoLog(program));
        }
        checkGlError();
        return program;
    }

    public static Attribute[] getAttributes(int program) {
        int[] attributeCount = new int[1];
        GLES20.glGetProgramiv(program, 35721, attributeCount, 0);
        if (attributeCount[0] != 2) {
            throw new IllegalStateException("expected two attributes");
        }
        Attribute[] attributes = new Attribute[attributeCount[0]];
        for (int i = 0; i < attributeCount[0]; i++) {
            attributes[i] = new Attribute(program, i);
        }
        return attributes;
    }

    public static Uniform[] getUniforms(int program) {
        int[] uniformCount = new int[1];
        GLES20.glGetProgramiv(program, 35718, uniformCount, 0);
        Uniform[] uniforms = new Uniform[uniformCount[0]];
        for (int i = 0; i < uniformCount[0]; i++) {
            uniforms[i] = new Uniform(program, i);
        }
        return uniforms;
    }

    public static FloatBuffer createBuffer(float[] data) {
        return (FloatBuffer) createBuffer(data.length).put(data).flip();
    }

    public static FloatBuffer createBuffer(int capacity) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity * 4);
        return byteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public static int createExternalTexture() {
        int[] texId = new int[1];
        GLES20.glGenTextures(1, IntBuffer.wrap(texId));
        GLES20.glBindTexture(36197, texId[0]);
        GLES20.glTexParameteri(36197, 10241, 9729);
        GLES20.glTexParameteri(36197, 10240, 9729);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
        checkGlError();
        return texId[0];
    }

    private static void addShader(int type, String source, int program) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] result = {0};
        GLES20.glGetShaderiv(shader, 35713, result, 0);
        if (result[0] != 1) {
            throwGlError(GLES20.glGetShaderInfoLog(shader) + ", source: " + source);
        }
        GLES20.glAttachShader(program, shader);
        GLES20.glDeleteShader(shader);
        checkGlError();
    }

    private static void throwGlError(String errorMsg) {
        Log.e(TAG, errorMsg);
    }

    public static int strlen(byte[] strVal) {
        for (int i = 0; i < strVal.length; i++) {
            if (strVal[i] == 0) {
                return i;
            }
        }
        int i2 = strVal.length;
        return i2;
    }
}
