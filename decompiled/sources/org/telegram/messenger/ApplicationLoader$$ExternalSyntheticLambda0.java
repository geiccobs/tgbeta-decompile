package org.telegram.messenger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
/* loaded from: classes.dex */
public final /* synthetic */ class ApplicationLoader$$ExternalSyntheticLambda0 implements OnCompleteListener {
    public static final /* synthetic */ ApplicationLoader$$ExternalSyntheticLambda0 INSTANCE = new ApplicationLoader$$ExternalSyntheticLambda0();

    private /* synthetic */ ApplicationLoader$$ExternalSyntheticLambda0() {
    }

    @Override // com.google.android.gms.tasks.OnCompleteListener
    public final void onComplete(Task task) {
        ApplicationLoader.lambda$initPushServices$0(task);
    }
}
