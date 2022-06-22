package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.annotation.RecentlyNonNull;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public class LifecycleCallback {
    @RecentlyNonNull
    protected final LifecycleFragment mLifecycleFragment;

    @Keep
    private static LifecycleFragment getChimeraLifecycleFragmentImpl(LifecycleActivity lifecycleActivity) {
        throw new IllegalStateException("Method not available in SDK.");
    }

    public void dump(@RecentlyNonNull String str, @RecentlyNonNull FileDescriptor fileDescriptor, @RecentlyNonNull PrintWriter printWriter, @RecentlyNonNull String[] strArr) {
    }

    public void onActivityResult(int i, int i2, @RecentlyNonNull Intent intent) {
    }

    public void onCreate(Bundle bundle) {
    }

    public void onDestroy() {
    }

    public void onResume() {
    }

    public void onSaveInstanceState(@RecentlyNonNull Bundle bundle) {
    }

    public void onStart() {
    }

    public void onStop() {
    }

    @RecentlyNonNull
    public static LifecycleFragment getFragment(@RecentlyNonNull LifecycleActivity lifecycleActivity) {
        if (lifecycleActivity.isSupport()) {
            return zzc.zza(lifecycleActivity.asFragmentActivity());
        }
        if (lifecycleActivity.zza()) {
            return zzb.zza(lifecycleActivity.asActivity());
        }
        throw new IllegalArgumentException("Can't get fragment for unexpected activity.");
    }

    @RecentlyNonNull
    public static LifecycleFragment getFragment(@RecentlyNonNull Activity activity) {
        return getFragment(new LifecycleActivity(activity));
    }

    public LifecycleCallback(@RecentlyNonNull LifecycleFragment lifecycleFragment) {
        this.mLifecycleFragment = lifecycleFragment;
    }

    @RecentlyNonNull
    public Activity getActivity() {
        return this.mLifecycleFragment.getLifecycleActivity();
    }
}
