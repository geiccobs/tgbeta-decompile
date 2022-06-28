package org.telegram.ui.Components.Paint;

import java.util.Arrays;
import java.util.Vector;
/* loaded from: classes5.dex */
public class Path {
    private float baseWeight;
    private Brush brush;
    private int color;
    private Vector<Point> points;
    public double remainder;

    public Path(Point point) {
        Vector<Point> vector = new Vector<>();
        this.points = vector;
        vector.add(point);
    }

    public Path(Point[] points) {
        Vector<Point> vector = new Vector<>();
        this.points = vector;
        vector.addAll(Arrays.asList(points));
    }

    public int getLength() {
        Vector<Point> vector = this.points;
        if (vector == null) {
            return 0;
        }
        return vector.size();
    }

    public Point[] getPoints() {
        Point[] points = new Point[this.points.size()];
        this.points.toArray(points);
        return points;
    }

    public int getColor() {
        return this.color;
    }

    public float getBaseWeight() {
        return this.baseWeight;
    }

    public Brush getBrush() {
        return this.brush;
    }

    public void setup(int color, float baseWeight, Brush brush) {
        this.color = color;
        this.baseWeight = baseWeight;
        this.brush = brush;
    }
}
