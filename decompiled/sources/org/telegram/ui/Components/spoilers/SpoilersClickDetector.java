package org.telegram.ui.Components.spoilers;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.GestureDetectorCompat;
import java.util.List;
/* loaded from: classes5.dex */
public class SpoilersClickDetector {
    private GestureDetectorCompat gestureDetector;
    private boolean trackingTap;

    /* loaded from: classes5.dex */
    public interface OnSpoilerClickedListener {
        void onSpoilerClicked(SpoilerEffect spoilerEffect, float f, float f2);
    }

    public SpoilersClickDetector(View v, List<SpoilerEffect> spoilers, OnSpoilerClickedListener clickedListener) {
        this(v, spoilers, true, clickedListener);
    }

    public SpoilersClickDetector(final View v, final List<SpoilerEffect> spoilers, final boolean offsetPadding, final OnSpoilerClickedListener clickedListener) {
        this.gestureDetector = new GestureDetectorCompat(v.getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.spoilers.SpoilersClickDetector.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                int x = (int) e.getX();
                int y = ((int) e.getY()) + v.getScrollY();
                if (offsetPadding) {
                    x -= v.getPaddingLeft();
                    y -= v.getPaddingTop();
                }
                for (SpoilerEffect eff : spoilers) {
                    if (eff.getBounds().contains(x, y)) {
                        SpoilersClickDetector.this.trackingTap = true;
                        return true;
                    }
                }
                return false;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                if (SpoilersClickDetector.this.trackingTap) {
                    v.playSoundEffect(0);
                    SpoilersClickDetector.this.trackingTap = false;
                    int x = (int) e.getX();
                    int y = ((int) e.getY()) + v.getScrollY();
                    if (offsetPadding) {
                        x -= v.getPaddingLeft();
                        y -= v.getPaddingTop();
                    }
                    for (SpoilerEffect eff : spoilers) {
                        if (eff.getBounds().contains(x, y)) {
                            clickedListener.onSpoilerClicked(eff, x, y);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return this.gestureDetector.onTouchEvent(ev);
    }
}
