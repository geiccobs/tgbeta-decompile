package org.telegram.ui.Charts.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.SegmentTree;
/* loaded from: classes4.dex */
public class StackBarChartData extends ChartData {
    public int[] ySum;
    public SegmentTree ySumSegmentTree;

    public StackBarChartData(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        init();
    }

    public void init() {
        int n = this.lines.get(0).y.length;
        int k = this.lines.size();
        this.ySum = new int[n];
        for (int i = 0; i < n; i++) {
            this.ySum[i] = 0;
            for (int j = 0; j < k; j++) {
                int[] iArr = this.ySum;
                iArr[i] = iArr[i] + this.lines.get(j).y[i];
            }
        }
        this.ySumSegmentTree = new SegmentTree(this.ySum);
    }

    public int findMax(int start, int end) {
        return this.ySumSegmentTree.rMaxQ(start, end);
    }
}
