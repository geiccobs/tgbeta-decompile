package com.huawei.agconnect.core.a;

import com.huawei.agconnect.AGCInitFinishManager;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public class a extends AGCInitFinishManager {
    private static final List<AGCInitFinishManager.AGCInitFinishCallback> a = new CopyOnWriteArrayList();

    public static void a() {
        for (AGCInitFinishManager.AGCInitFinishCallback aGCInitFinishCallback : a) {
            aGCInitFinishCallback.onFinish();
        }
    }
}
