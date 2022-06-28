package com.google.android.exoplayer2;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.google.android.exoplayer2.util.Log;
/* loaded from: classes3.dex */
final class WifiLockManager {
    private static final String TAG = "WifiLockManager";
    private static final String WIFI_LOCK_TAG = "ExoPlayer:WifiLockManager";
    private boolean enabled;
    private boolean stayAwake;
    private WifiManager.WifiLock wifiLock;
    private final WifiManager wifiManager;

    public WifiLockManager(Context context) {
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
    }

    public void setEnabled(boolean enabled) {
        if (enabled && this.wifiLock == null) {
            WifiManager wifiManager = this.wifiManager;
            if (wifiManager == null) {
                Log.w(TAG, "WifiManager is null, therefore not creating the WifiLock.");
                return;
            }
            WifiManager.WifiLock createWifiLock = wifiManager.createWifiLock(3, WIFI_LOCK_TAG);
            this.wifiLock = createWifiLock;
            createWifiLock.setReferenceCounted(false);
        }
        this.enabled = enabled;
        updateWifiLock();
    }

    public void setStayAwake(boolean stayAwake) {
        this.stayAwake = stayAwake;
        updateWifiLock();
    }

    private void updateWifiLock() {
        WifiManager.WifiLock wifiLock = this.wifiLock;
        if (wifiLock == null) {
            return;
        }
        if (this.enabled && this.stayAwake) {
            wifiLock.acquire();
        } else {
            wifiLock.release();
        }
    }
}
