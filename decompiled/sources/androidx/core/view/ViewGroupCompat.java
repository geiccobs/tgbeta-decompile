package androidx.core.view;

import android.os.Build;
import android.view.ViewGroup;
import androidx.core.R$id;
/* loaded from: classes.dex */
public final class ViewGroupCompat {
    public static boolean isTransitionGroup(ViewGroup group) {
        if (Build.VERSION.SDK_INT >= 21) {
            return group.isTransitionGroup();
        }
        Boolean bool = (Boolean) group.getTag(R$id.tag_transition_group);
        return ((bool == null || !bool.booleanValue()) && group.getBackground() == null && ViewCompat.getTransitionName(group) == null) ? false : true;
    }
}
