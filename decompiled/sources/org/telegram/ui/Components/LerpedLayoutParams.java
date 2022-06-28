package org.telegram.ui.Components;

import android.view.ViewGroup;
/* loaded from: classes5.dex */
public class LerpedLayoutParams extends ViewGroup.MarginLayoutParams {
    private ViewGroup.LayoutParams from;
    private ViewGroup.LayoutParams to;

    public LerpedLayoutParams(ViewGroup.LayoutParams from, ViewGroup.LayoutParams to) {
        super(from == null ? to : from);
        this.from = from;
        this.to = to;
    }

    public void apply(float t) {
        float t2 = Math.min(Math.max(t, 0.0f), 1.0f);
        this.width = lerpSz(this.from.width, this.to.width, t2);
        this.height = lerpSz(this.from.height, this.to.height, t2);
        ViewGroup.LayoutParams layoutParams = this.from;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.LayoutParams layoutParams2 = this.to;
            if (layoutParams2 instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginFrom = (ViewGroup.MarginLayoutParams) layoutParams;
                ViewGroup.MarginLayoutParams marginTo = (ViewGroup.MarginLayoutParams) layoutParams2;
                this.topMargin = lerp(marginFrom.topMargin, marginTo.topMargin, t2);
                this.leftMargin = lerp(marginFrom.leftMargin, marginTo.leftMargin, t2);
                this.rightMargin = lerp(marginFrom.rightMargin, marginTo.rightMargin, t2);
                this.bottomMargin = lerp(marginFrom.bottomMargin, marginTo.bottomMargin, t2);
                return;
            }
        }
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginFrom2 = (ViewGroup.MarginLayoutParams) layoutParams;
            this.topMargin = marginFrom2.topMargin;
            this.leftMargin = marginFrom2.leftMargin;
            this.rightMargin = marginFrom2.rightMargin;
            this.bottomMargin = marginFrom2.bottomMargin;
            return;
        }
        ViewGroup.LayoutParams layoutParams3 = this.to;
        if (layoutParams3 instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginTo2 = (ViewGroup.MarginLayoutParams) layoutParams3;
            this.topMargin = marginTo2.topMargin;
            this.leftMargin = marginTo2.leftMargin;
            this.rightMargin = marginTo2.rightMargin;
            this.bottomMargin = marginTo2.bottomMargin;
        }
    }

    private int lerp(int from, int to, float t) {
        return (int) (from + ((to - from) * t));
    }

    private int lerpSz(int from, int to, float t) {
        if (from < 0 || to < 0) {
            return t < 0.5f ? from : to;
        }
        return lerp(from, to, t);
    }
}
