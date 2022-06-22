package com.google.firebase.remoteconfig;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.remoteconfig.internal.ConfigFetchHandler;
/* loaded from: classes.dex */
public final /* synthetic */ class FirebaseRemoteConfig$$ExternalSyntheticLambda2 implements SuccessContinuation {
    public static final /* synthetic */ FirebaseRemoteConfig$$ExternalSyntheticLambda2 INSTANCE = new FirebaseRemoteConfig$$ExternalSyntheticLambda2();

    private /* synthetic */ FirebaseRemoteConfig$$ExternalSyntheticLambda2() {
    }

    @Override // com.google.android.gms.tasks.SuccessContinuation
    public final Task then(Object obj) {
        Task forResult;
        ConfigFetchHandler.FetchResponse fetchResponse = (ConfigFetchHandler.FetchResponse) obj;
        forResult = Tasks.forResult(null);
        return forResult;
    }
}
