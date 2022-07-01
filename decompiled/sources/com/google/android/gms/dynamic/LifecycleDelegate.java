package com.google.android.gms.dynamic;

import android.os.Bundle;
import androidx.annotation.RecentlyNonNull;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public interface LifecycleDelegate {
    void onCreate(@RecentlyNonNull Bundle bundle);

    void onDestroy();

    void onLowMemory();

    void onPause();

    void onResume();
}
