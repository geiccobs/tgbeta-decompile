package com.googlecode.mp4parser.h264.model;

import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public class ScalingMatrix {
    public ScalingList[] ScalingList4x4;
    public ScalingList[] ScalingList8x8;

    public String toString() {
        StringBuilder sb = new StringBuilder("ScalingMatrix{ScalingList4x4=");
        ScalingList[] scalingListArr = this.ScalingList4x4;
        List list = null;
        sb.append(scalingListArr == null ? null : Arrays.asList(scalingListArr));
        sb.append("\n");
        sb.append(", ScalingList8x8=");
        ScalingList[] scalingListArr2 = this.ScalingList8x8;
        if (scalingListArr2 != null) {
            list = Arrays.asList(scalingListArr2);
        }
        sb.append(list);
        sb.append("\n");
        sb.append('}');
        return sb.toString();
    }
}
