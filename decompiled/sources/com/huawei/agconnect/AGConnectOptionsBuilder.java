package com.huawei.agconnect;

import android.content.Context;
import com.huawei.agconnect.config.impl.b;
import com.huawei.agconnect.core.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public final class AGConnectOptionsBuilder {
    private InputStream inputStream;
    private String packageName;
    private AGCRoutePolicy routePolicy = AGCRoutePolicy.UNKNOWN;
    private final Map<String, String> customConfigMap = new HashMap();
    private final List<Service> customServices = new ArrayList();

    public AGConnectOptions build(Context context) {
        return new b(context, this.packageName, this.routePolicy, this.inputStream, this.customConfigMap, this.customServices, null);
    }

    public AGConnectOptionsBuilder setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }
}
