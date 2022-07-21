package com.huawei.agconnect;

import android.content.Context;
import android.util.Log;
import com.huawei.agconnect.core.a.b;
/* loaded from: classes.dex */
public abstract class AGConnectInstance {
    public static AGConnectInstance buildInstance(AGConnectOptions aGConnectOptions) {
        return b.a(aGConnectOptions);
    }

    public static AGConnectInstance getInstance() {
        return b.a();
    }

    public static synchronized void initialize(Context context) {
        synchronized (AGConnectInstance.class) {
            Log.i("AGConnectInstance", "AGConnectInstance#initialize");
            b.a(context);
        }
    }

    public abstract Context getContext();

    public abstract AGConnectOptions getOptions();
}
