package org.telegram.messenger;

import org.telegram.messenger.ChatObject;
/* loaded from: classes4.dex */
public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ ChatObject.Call f$0;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda8(ChatObject.Call call) {
        this.f$0 = call;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.checkQueue();
    }
}
