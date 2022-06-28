package androidx.fragment.app;

import androidx.lifecycle.ViewModelStore;
import java.util.List;
/* loaded from: classes3.dex */
public class FragmentManagerNonConfig {
    private final List<FragmentManagerNonConfig> mChildNonConfigs;
    private final List<Fragment> mFragments;
    private final List<ViewModelStore> mViewModelStores;

    public FragmentManagerNonConfig(List<Fragment> fragments, List<FragmentManagerNonConfig> childNonConfigs, List<ViewModelStore> viewModelStores) {
        this.mFragments = fragments;
        this.mChildNonConfigs = childNonConfigs;
        this.mViewModelStores = viewModelStores;
    }

    public List<Fragment> getFragments() {
        return this.mFragments;
    }

    public List<FragmentManagerNonConfig> getChildNonConfigs() {
        return this.mChildNonConfigs;
    }

    public List<ViewModelStore> getViewModelStores() {
        return this.mViewModelStores;
    }
}
