package com.google.firebase.installations;

import com.google.android.gms.tasks.Task;
/* loaded from: classes.dex */
public interface FirebaseInstallationsApi {
    Task<String> getId();

    Task<InstallationTokenResult> getToken(boolean z);
}
