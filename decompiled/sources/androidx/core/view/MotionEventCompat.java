package androidx.core.view;

import android.view.MotionEvent;
/* loaded from: classes.dex */
public final class MotionEventCompat {
    public static boolean isFromSource(MotionEvent event, int source) {
        return (event.getSource() & source) == source;
    }
}
