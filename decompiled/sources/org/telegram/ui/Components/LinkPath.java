package org.telegram.ui.Components;

import android.graphics.CornerPathEffect;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class LinkPath extends Path {
    private static CornerPathEffect roundedEffect;
    private static int roundedEffectRadius;
    private int baselineShift;
    private Layout currentLayout;
    private int currentLine;
    private float heightOffset;
    private int lineHeight;
    private boolean useRoundRect;
    private float lastTop = -1.0f;
    private boolean allowReset = true;

    public static int getRadius() {
        return AndroidUtilities.dp(4.0f);
    }

    public static CornerPathEffect getRoundedEffect() {
        if (roundedEffect == null || roundedEffectRadius != getRadius()) {
            int radius = getRadius();
            roundedEffectRadius = radius;
            roundedEffect = new CornerPathEffect(radius);
        }
        return roundedEffect;
    }

    public LinkPath() {
    }

    public LinkPath(boolean roundRect) {
        this.useRoundRect = roundRect;
    }

    public void setCurrentLayout(Layout layout, int start, float yOffset) {
        int lineCount;
        this.currentLayout = layout;
        this.currentLine = layout.getLineForOffset(start);
        this.lastTop = -1.0f;
        this.heightOffset = yOffset;
        if (Build.VERSION.SDK_INT >= 28 && (lineCount = layout.getLineCount()) > 0) {
            this.lineHeight = layout.getLineBottom(lineCount - 1) - layout.getLineTop(lineCount - 1);
        }
    }

    public void setAllowReset(boolean value) {
        this.allowReset = value;
    }

    public void setUseRoundRect(boolean value) {
        this.useRoundRect = value;
    }

    public boolean isUsingRoundRect() {
        return this.useRoundRect;
    }

    public void setBaselineShift(int value) {
        this.baselineShift = value;
    }

    @Override // android.graphics.Path
    public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
        float right2;
        float left2;
        float y2;
        float y22;
        float y;
        float f = this.heightOffset;
        float top2 = top + f;
        float bottom2 = bottom + f;
        float f2 = this.lastTop;
        if (f2 == -1.0f) {
            this.lastTop = top2;
        } else if (f2 != top2) {
            this.lastTop = top2;
            this.currentLine++;
        }
        float lineRight = this.currentLayout.getLineRight(this.currentLine);
        float lineLeft = this.currentLayout.getLineLeft(this.currentLine);
        if (left < lineRight) {
            if (left <= lineLeft && right <= lineLeft) {
                return;
            }
            if (right <= lineRight) {
                right2 = right;
            } else {
                right2 = lineRight;
            }
            if (left >= lineLeft) {
                left2 = left;
            } else {
                left2 = lineLeft;
            }
            float f3 = 0.0f;
            if (Build.VERSION.SDK_INT >= 28) {
                y2 = bottom2;
                if (bottom2 - top2 > this.lineHeight) {
                    y2 = this.heightOffset + (bottom2 != ((float) this.currentLayout.getHeight()) ? this.currentLayout.getLineBottom(this.currentLine) - this.currentLayout.getSpacingAdd() : 0.0f);
                }
            } else {
                y2 = bottom2 - (bottom2 != ((float) this.currentLayout.getHeight()) ? this.currentLayout.getSpacingAdd() : 0.0f);
            }
            int i = this.baselineShift;
            if (i < 0) {
                y = top2;
                y22 = y2 + i;
            } else if (i > 0) {
                y = top2 + i;
                y22 = y2;
            } else {
                y = top2;
                y22 = y2;
            }
            if (this.useRoundRect) {
                float radius = left2 - (0 != 0 ? 0.0f : getRadius() / 2.0f);
                if (0 == 0) {
                    f3 = getRadius() / 2.0f;
                }
                super.addRect(radius, y, f3 + right2, y22, dir);
                return;
            }
            super.addRect(left2, y, right2, y22, dir);
        }
    }

    @Override // android.graphics.Path
    public void reset() {
        if (!this.allowReset) {
            return;
        }
        super.reset();
    }
}
