package androidx.fragment.app;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes.dex */
public abstract class FragmentManager {

    /* loaded from: classes.dex */
    public static abstract class FragmentLifecycleCallbacks {
    }

    /* loaded from: classes.dex */
    public interface OnBackStackChangedListener {
        void onBackStackChanged();
    }

    public abstract FragmentTransaction beginTransaction();

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    public abstract Fragment findFragmentByTag(String str);

    public abstract List<Fragment> getFragments();

    public abstract boolean isStateSaved();

    public abstract void popBackStack(int i, int i2);

    public abstract boolean popBackStackImmediate();
}
