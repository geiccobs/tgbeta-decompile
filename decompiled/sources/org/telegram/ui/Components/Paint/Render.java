package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
/* loaded from: classes5.dex */
public class Render {
    public static RectF RenderPath(Path path, RenderState state) {
        state.baseWeight = path.getBaseWeight();
        state.spacing = path.getBrush().getSpacing();
        state.alpha = path.getBrush().getAlpha();
        state.angle = path.getBrush().getAngle();
        state.scale = path.getBrush().getScale();
        int length = path.getLength();
        if (length == 0) {
            return null;
        }
        if (length == 1) {
            PaintStamp(path.getPoints()[0], state);
        } else {
            Point[] points = path.getPoints();
            state.prepare();
            for (int i = 0; i < points.length - 1; i++) {
                PaintSegment(points[i], points[i + 1], state);
            }
        }
        path.remainder = state.remainder;
        return Draw(state);
    }

    private static void PaintSegment(Point lastPoint, Point point, RenderState state) {
        Point unitVector;
        double distance = lastPoint.getDistanceTo(point);
        Point vector = point.substract(lastPoint);
        Point unitVector2 = new Point(1.0d, 1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
        float vectorAngle = Math.abs(state.angle) > 0.0f ? state.angle : (float) Math.atan2(vector.y, vector.x);
        float brushWeight = ((state.baseWeight * state.scale) * 1.0f) / state.viewportScale;
        double step = Math.max(1.0f, state.spacing * brushWeight);
        if (distance <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            unitVector = unitVector2;
        } else {
            Double.isNaN(distance);
            Point unitVector3 = vector.multiplyByScalar(1.0d / distance);
            unitVector = unitVector3;
        }
        float boldenedAlpha = Math.min(1.0f, state.alpha * 1.15f);
        boolean boldenHead = lastPoint.edge;
        boolean boldenTail = point.edge;
        double d = state.remainder;
        Double.isNaN(distance);
        Double.isNaN(step);
        int count = (int) Math.ceil((distance - d) / step);
        int currentCount = state.getCount();
        state.appendValuesCount(count);
        state.setPosition(currentCount);
        Point start = lastPoint.add(unitVector.multiplyByScalar(state.remainder));
        double f = state.remainder;
        boolean succeed = true;
        boolean boldenHead2 = boldenHead;
        Point start2 = start;
        while (f <= distance) {
            float alpha = boldenHead2 ? boldenedAlpha : state.alpha;
            Point start3 = start2;
            int currentCount2 = currentCount;
            int count2 = count;
            succeed = state.addPoint(start2.toPointF(), brushWeight, vectorAngle, alpha, -1);
            if (!succeed) {
                break;
            }
            start2 = start3.add(unitVector.multiplyByScalar(step));
            boldenHead2 = false;
            Double.isNaN(step);
            f += step;
            currentCount = currentCount2;
            count = count2;
        }
        if (succeed && boldenTail) {
            state.appendValuesCount(1);
            state.addPoint(point.toPointF(), brushWeight, vectorAngle, boldenedAlpha, -1);
        }
        Double.isNaN(distance);
        state.remainder = f - distance;
    }

    private static void PaintStamp(Point point, RenderState state) {
        float brushWeight = ((state.baseWeight * state.scale) * 1.0f) / state.viewportScale;
        PointF start = point.toPointF();
        float angle = Math.abs(state.angle) > 0.0f ? state.angle : 0.0f;
        float alpha = state.alpha;
        state.prepare();
        state.appendValuesCount(1);
        state.addPoint(start, brushWeight, angle, alpha, 0);
    }

    private static RectF Draw(RenderState state) {
        RectF dataBounds = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        int count = state.getCount();
        if (count == 0) {
            return dataBounds;
        }
        int capacity = 20 * ((count * 4) + ((count - 1) * 2));
        ByteBuffer bb = ByteBuffer.allocateDirect(capacity);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexData = bb.asFloatBuffer();
        vertexData.position(0);
        state.setPosition(0);
        int i = 0;
        int n = 0;
        while (i < count) {
            float x = state.read();
            float y = state.read();
            float size = state.read();
            float angle = state.read();
            float alpha = state.read();
            int capacity2 = capacity;
            RectF rect = new RectF(x - size, y - size, x + size, y + size);
            float[] points = {rect.left, rect.top, rect.right, rect.top, rect.left, rect.bottom, rect.right, rect.bottom};
            float centerX = rect.centerX();
            float centerY = rect.centerY();
            Matrix t = new Matrix();
            ByteBuffer bb2 = bb;
            t.setRotate((float) Math.toDegrees(angle), centerX, centerY);
            t.mapPoints(points);
            t.mapRect(rect);
            Utils.RectFIntegral(rect);
            dataBounds.union(rect);
            if (n != 0) {
                float angle2 = points[0];
                vertexData.put(angle2);
                vertexData.put(points[1]);
                vertexData.put(0.0f);
                vertexData.put(0.0f);
                vertexData.put(alpha);
                n++;
            }
            vertexData.put(points[0]);
            vertexData.put(points[1]);
            vertexData.put(0.0f);
            vertexData.put(0.0f);
            vertexData.put(alpha);
            vertexData.put(points[2]);
            vertexData.put(points[3]);
            vertexData.put(1.0f);
            vertexData.put(0.0f);
            vertexData.put(alpha);
            vertexData.put(points[4]);
            vertexData.put(points[5]);
            vertexData.put(0.0f);
            vertexData.put(1.0f);
            vertexData.put(alpha);
            vertexData.put(points[6]);
            vertexData.put(points[7]);
            vertexData.put(1.0f);
            vertexData.put(1.0f);
            vertexData.put(alpha);
            n = n + 1 + 1 + 1 + 1;
            if (i != count - 1) {
                vertexData.put(points[6]);
                vertexData.put(points[7]);
                vertexData.put(1.0f);
                vertexData.put(1.0f);
                vertexData.put(alpha);
                n++;
            }
            i++;
            capacity = capacity2;
            bb = bb2;
        }
        vertexData.position(0);
        FloatBuffer coordData = vertexData.slice();
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 20, (Buffer) coordData);
        GLES20.glEnableVertexAttribArray(0);
        vertexData.position(2);
        FloatBuffer texData = vertexData.slice();
        GLES20.glVertexAttribPointer(1, 2, 5126, true, 20, (Buffer) texData);
        GLES20.glEnableVertexAttribArray(1);
        vertexData.position(4);
        FloatBuffer alphaData = vertexData.slice();
        GLES20.glVertexAttribPointer(2, 1, 5126, true, 20, (Buffer) alphaData);
        GLES20.glEnableVertexAttribArray(2);
        GLES20.glDrawArrays(5, 0, n);
        return dataBounds;
    }
}
