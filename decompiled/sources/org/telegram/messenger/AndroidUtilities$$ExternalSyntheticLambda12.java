package org.telegram.messenger;

import com.google.android.gms.tasks.OnSuccessListener;
/* loaded from: classes4.dex */
public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda12 implements OnSuccessListener {
    public static final /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda12 INSTANCE = new AndroidUtilities$$ExternalSyntheticLambda12();

    private /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda12() {
    }

    @Override // com.google.android.gms.tasks.OnSuccessListener
    public final void onSuccess(Object obj) {
        AndroidUtilities.lambda$setWaitingForSms$5((Void) obj);
    }
}
