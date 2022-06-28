package com.googlecode.mp4parser.h264.model;
/* loaded from: classes3.dex */
public class AspectRatio {
    public static final AspectRatio Extended_SAR = new AspectRatio(255);
    private int value;

    private AspectRatio(int value) {
        this.value = value;
    }

    public static AspectRatio fromValue(int value) {
        AspectRatio aspectRatio = Extended_SAR;
        if (value == aspectRatio.value) {
            return aspectRatio;
        }
        return new AspectRatio(value);
    }

    public int getValue() {
        return this.value;
    }

    public String toString() {
        return "AspectRatio{value=" + this.value + '}';
    }
}
