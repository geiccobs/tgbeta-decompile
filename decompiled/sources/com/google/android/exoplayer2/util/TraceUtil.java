package com.google.android.exoplayer2.util;

import android.annotation.TargetApi;
import android.os.Trace;
import org.telegram.messenger.R;
/* loaded from: classes.dex */
public final class TraceUtil {
    public static void beginSection(String str) {
        if (Util.SDK_INT >= 18) {
            beginSectionV18(str);
        }
    }

    public static void endSection() {
        if (Util.SDK_INT >= 18) {
            endSectionV18();
        }
    }

    @TargetApi(R.styleable.MapAttrs_uiScrollGesturesDuringRotateOrZoom)
    private static void beginSectionV18(String str) {
        Trace.beginSection(str);
    }

    @TargetApi(R.styleable.MapAttrs_uiScrollGesturesDuringRotateOrZoom)
    private static void endSectionV18() {
        Trace.endSection();
    }
}
