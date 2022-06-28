package org.telegram.messenger.support.customtabsclient.shared;

import android.content.ComponentName;
import java.lang.ref.WeakReference;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
/* loaded from: classes4.dex */
public class ServiceConnection extends CustomTabsServiceConnection {
    private WeakReference<ServiceConnectionCallback> mConnectionCallback;

    public ServiceConnection(ServiceConnectionCallback connectionCallback) {
        this.mConnectionCallback = new WeakReference<>(connectionCallback);
    }

    @Override // org.telegram.messenger.support.customtabs.CustomTabsServiceConnection
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        ServiceConnectionCallback connectionCallback = this.mConnectionCallback.get();
        if (connectionCallback != null) {
            connectionCallback.onServiceConnected(client);
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        ServiceConnectionCallback connectionCallback = this.mConnectionCallback.get();
        if (connectionCallback != null) {
            connectionCallback.onServiceDisconnected();
        }
    }
}
