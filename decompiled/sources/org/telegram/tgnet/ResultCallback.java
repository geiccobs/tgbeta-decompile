package org.telegram.tgnet;

import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public interface ResultCallback<T> {
    void onComplete(T t);

    void onError(Throwable th);

    void onError(TLRPC.TL_error tL_error);

    /* renamed from: org.telegram.tgnet.ResultCallback$-CC */
    /* loaded from: classes4.dex */
    public final /* synthetic */ class CC {
        public static void $default$onError(ResultCallback resultCallback, TLRPC.TL_error error) {
        }

        public static void $default$onError(ResultCallback resultCallback, Throwable throwable) {
        }
    }
}
