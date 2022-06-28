package com.googlecode.mp4parser.h264.model;

import androidx.core.view.InputDeviceCompat;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.h264.write.CAVLCWriter;
import java.io.IOException;
/* loaded from: classes3.dex */
public class ScalingList {
    public int[] scalingList;
    public boolean useDefaultScalingMatrixFlag;

    public void write(CAVLCWriter out) throws IOException {
        if (this.useDefaultScalingMatrixFlag) {
            out.writeSE(0, "SPS: ");
            return;
        }
        int lastScale = 8;
        int j = 0;
        while (true) {
            int[] iArr = this.scalingList;
            if (j < iArr.length) {
                if (8 != 0) {
                    int deltaScale = (iArr[j] - lastScale) + InputDeviceCompat.SOURCE_ANY;
                    out.writeSE(deltaScale, "SPS: ");
                }
                lastScale = this.scalingList[j];
                j++;
            } else {
                return;
            }
        }
    }

    public static ScalingList read(CAVLCReader is, int sizeOfScalingList) throws IOException {
        ScalingList sl = new ScalingList();
        sl.scalingList = new int[sizeOfScalingList];
        int lastScale = 8;
        int nextScale = 8;
        int j = 0;
        while (j < sizeOfScalingList) {
            if (nextScale != 0) {
                int deltaScale = is.readSE("deltaScale");
                int nextScale2 = ((lastScale + deltaScale) + 256) % 256;
                sl.useDefaultScalingMatrixFlag = j == 0 && nextScale2 == 0;
                nextScale = nextScale2;
            }
            int[] iArr = sl.scalingList;
            iArr[j] = nextScale == 0 ? lastScale : nextScale;
            lastScale = iArr[j];
            j++;
        }
        return sl;
    }

    public String toString() {
        return "ScalingList{scalingList=" + this.scalingList + ", useDefaultScalingMatrixFlag=" + this.useDefaultScalingMatrixFlag + '}';
    }
}
