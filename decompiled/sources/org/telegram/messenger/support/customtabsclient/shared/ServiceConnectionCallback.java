package org.telegram.messenger.support.customtabsclient.shared;

import org.telegram.messenger.support.customtabs.CustomTabsClient;
/* loaded from: classes4.dex */
public interface ServiceConnectionCallback {
    void onServiceConnected(CustomTabsClient customTabsClient);

    void onServiceDisconnected();
}
