package com.google.android.exoplayer2.video.spherical;

import com.google.android.exoplayer2.util.Assertions;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class Projection {
    public static final int DRAW_MODE_TRIANGLES = 0;
    public static final int DRAW_MODE_TRIANGLES_FAN = 2;
    public static final int DRAW_MODE_TRIANGLES_STRIP = 1;
    public static final int POSITION_COORDS_PER_VERTEX = 3;
    public static final int TEXTURE_COORDS_PER_VERTEX = 2;
    public final Mesh leftMesh;
    public final Mesh rightMesh;
    public final boolean singleMesh;
    public final int stereoMode;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DrawMode {
    }

    public static Projection createEquirectangular(int stereoMode) {
        return createEquirectangular(50.0f, 36, 72, 180.0f, 360.0f, stereoMode);
    }

    public static Projection createEquirectangular(float radius, int latitudes, int longitudes, float verticalFovDegrees, float horizontalFovDegrees, int stereoMode) {
        int k;
        int i = latitudes;
        int i2 = longitudes;
        Assertions.checkArgument(radius > 0.0f);
        Assertions.checkArgument(i >= 1);
        Assertions.checkArgument(i2 >= 1);
        Assertions.checkArgument(verticalFovDegrees > 0.0f && verticalFovDegrees <= 180.0f);
        Assertions.checkArgument(horizontalFovDegrees > 0.0f && horizontalFovDegrees <= 360.0f);
        float verticalFovRads = (float) Math.toRadians(verticalFovDegrees);
        float horizontalFovRads = (float) Math.toRadians(horizontalFovDegrees);
        float quadHeightRads = verticalFovRads / i;
        float quadWidthRads = horizontalFovRads / i2;
        int vertexCount = (((i2 + 1) * 2) + 2) * i;
        float[] vertexData = new float[vertexCount * 3];
        float[] textureData = new float[vertexCount * 2];
        int vOffset = 0;
        int tOffset = 0;
        int k2 = 0;
        while (k2 < i) {
            float phiLow = (k2 * quadHeightRads) - (verticalFovRads / 2.0f);
            float phiHigh = ((k2 + 1) * quadHeightRads) - (verticalFovRads / 2.0f);
            int i3 = 0;
            while (i3 < i2 + 1) {
                int k3 = 0;
                while (k3 < 2) {
                    float phi = k3 == 0 ? phiLow : phiHigh;
                    float phiLow2 = phiLow;
                    float phiLow3 = i3;
                    float theta = ((phiLow3 * quadWidthRads) + 3.1415927f) - (horizontalFovRads / 2.0f);
                    int vOffset2 = vOffset + 1;
                    int vertexCount2 = vertexCount;
                    float phiHigh2 = phiHigh;
                    double d = radius;
                    int k4 = k3;
                    double sin = Math.sin(theta);
                    Double.isNaN(d);
                    vertexData[vOffset] = -((float) (d * sin * Math.cos(phi)));
                    int vOffset3 = vOffset2 + 1;
                    double d2 = radius;
                    float verticalFovRads2 = verticalFovRads;
                    int j = k2;
                    double sin2 = Math.sin(phi);
                    Double.isNaN(d2);
                    vertexData[vOffset2] = (float) (d2 * sin2);
                    double d3 = radius;
                    vOffset = vOffset3 + 1;
                    double cos = Math.cos(theta);
                    Double.isNaN(d3);
                    vertexData[vOffset3] = (float) (d3 * cos * Math.cos(phi));
                    int tOffset2 = tOffset + 1;
                    textureData[tOffset] = (i3 * quadWidthRads) / horizontalFovRads;
                    int tOffset3 = tOffset2 + 1;
                    textureData[tOffset2] = ((j + k4) * quadHeightRads) / verticalFovRads2;
                    if (i3 == 0 && k4 == 0) {
                        i2 = longitudes;
                        k = k4;
                    } else {
                        i2 = longitudes;
                        if (i3 == i2) {
                            k = k4;
                            if (k != 1) {
                            }
                        } else {
                            k = k4;
                        }
                        tOffset = tOffset3;
                        verticalFovRads = verticalFovRads2;
                        k3 = k + 1;
                        phiLow = phiLow2;
                        vertexCount = vertexCount2;
                        phiHigh = phiHigh2;
                        k2 = j;
                    }
                    System.arraycopy(vertexData, vOffset - 3, vertexData, vOffset, 3);
                    System.arraycopy(textureData, tOffset3 - 2, textureData, tOffset3, 2);
                    tOffset = tOffset3 + 2;
                    vOffset += 3;
                    verticalFovRads = verticalFovRads2;
                    k3 = k + 1;
                    phiLow = phiLow2;
                    vertexCount = vertexCount2;
                    phiHigh = phiHigh2;
                    k2 = j;
                }
                int j2 = k2;
                i3++;
                vertexCount = vertexCount;
                k2 = j2;
            }
            int j3 = k2;
            k2 = j3 + 1;
            i = latitudes;
            vertexCount = vertexCount;
        }
        SubMesh subMesh = new SubMesh(0, vertexData, textureData, 1);
        return new Projection(new Mesh(subMesh), stereoMode);
    }

    public Projection(Mesh mesh, int stereoMode) {
        this(mesh, mesh, stereoMode);
    }

    public Projection(Mesh leftMesh, Mesh rightMesh, int stereoMode) {
        this.leftMesh = leftMesh;
        this.rightMesh = rightMesh;
        this.stereoMode = stereoMode;
        this.singleMesh = leftMesh == rightMesh;
    }

    /* loaded from: classes3.dex */
    public static final class SubMesh {
        public static final int VIDEO_TEXTURE_ID = 0;
        public final int mode;
        public final float[] textureCoords;
        public final int textureId;
        public final float[] vertices;

        public SubMesh(int textureId, float[] vertices, float[] textureCoords, int mode) {
            this.textureId = textureId;
            Assertions.checkArgument(((long) vertices.length) * 2 == ((long) textureCoords.length) * 3);
            this.vertices = vertices;
            this.textureCoords = textureCoords;
            this.mode = mode;
        }

        public int getVertexCount() {
            return this.vertices.length / 3;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Mesh {
        private final SubMesh[] subMeshes;

        public Mesh(SubMesh... subMeshes) {
            this.subMeshes = subMeshes;
        }

        public int getSubMeshCount() {
            return this.subMeshes.length;
        }

        public SubMesh getSubMesh(int index) {
            return this.subMeshes[index];
        }
    }
}
