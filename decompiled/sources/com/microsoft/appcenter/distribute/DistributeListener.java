package com.microsoft.appcenter.distribute;

import android.app.Activity;
/* loaded from: classes.dex */
public interface DistributeListener {
    boolean onReleaseAvailable(Activity activity, ReleaseDetails releaseDetails);
}
