package androidx.core.view.accessibility;

import android.os.Build;
import android.view.View;
import android.view.accessibility.AccessibilityRecord;
/* loaded from: classes.dex */
public class AccessibilityRecordCompat {
    public static void setSource(AccessibilityRecord record, View root, int virtualDescendantId) {
        if (Build.VERSION.SDK_INT >= 16) {
            record.setSource(root, virtualDescendantId);
        }
    }

    public static void setMaxScrollX(AccessibilityRecord record, int maxScrollX) {
        if (Build.VERSION.SDK_INT >= 15) {
            record.setMaxScrollX(maxScrollX);
        }
    }

    public static void setMaxScrollY(AccessibilityRecord record, int maxScrollY) {
        if (Build.VERSION.SDK_INT >= 15) {
            record.setMaxScrollY(maxScrollY);
        }
    }
}
