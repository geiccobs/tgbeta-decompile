package androidx.core.provider;

import android.os.Handler;
import android.os.Looper;
/* loaded from: classes3.dex */
class CalleeHandler {
    private CalleeHandler() {
    }

    public static Handler create() {
        if (Looper.myLooper() == null) {
            Handler handler = new Handler(Looper.getMainLooper());
            return handler;
        }
        Handler handler2 = new Handler();
        return handler2;
    }
}