package com.google.android.gms.common.api.internal;

import android.app.Activity;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zaa extends ActivityLifecycleObserver {
    private final WeakReference<C0004zaa> zaa;

    public zaa(Activity activity) {
        this(C0004zaa.zab(activity));
    }

    private zaa(C0004zaa c0004zaa) {
        this.zaa = new WeakReference<>(c0004zaa);
    }

    @Override // com.google.android.gms.common.api.internal.ActivityLifecycleObserver
    public final ActivityLifecycleObserver onStopCallOnce(Runnable runnable) {
        C0004zaa c0004zaa = this.zaa.get();
        if (c0004zaa == null) {
            throw new IllegalStateException("The target activity has already been GC'd");
        }
        c0004zaa.zaa(runnable);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* renamed from: com.google.android.gms.common.api.internal.zaa$zaa */
    /* loaded from: classes3.dex */
    public static class C0004zaa extends LifecycleCallback {
        private List<Runnable> zaa = new ArrayList();

        public static C0004zaa zab(Activity activity) {
            C0004zaa c0004zaa;
            synchronized (activity) {
                LifecycleFragment fragment = getFragment(activity);
                c0004zaa = (C0004zaa) fragment.getCallbackOrNull("LifecycleObserverOnStop", C0004zaa.class);
                if (c0004zaa == null) {
                    c0004zaa = new C0004zaa(fragment);
                }
            }
            return c0004zaa;
        }

        private C0004zaa(LifecycleFragment lifecycleFragment) {
            super(lifecycleFragment);
            this.mLifecycleFragment.addCallback("LifecycleObserverOnStop", this);
        }

        public final synchronized void zaa(Runnable runnable) {
            this.zaa.add(runnable);
        }

        @Override // com.google.android.gms.common.api.internal.LifecycleCallback
        public void onStop() {
            List<Runnable> list;
            synchronized (this) {
                list = this.zaa;
                this.zaa = new ArrayList();
            }
            for (Runnable runnable : list) {
                runnable.run();
            }
        }
    }
}
