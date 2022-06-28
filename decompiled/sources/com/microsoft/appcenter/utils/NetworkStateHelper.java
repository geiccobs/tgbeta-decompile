package com.microsoft.appcenter.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import java.io.Closeable;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes3.dex */
public class NetworkStateHelper implements Closeable {
    private static NetworkStateHelper sSharedInstance;
    private final ConnectivityManager mConnectivityManager;
    private ConnectivityReceiver mConnectivityReceiver;
    private final Context mContext;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private final Set<Listener> mListeners = new CopyOnWriteArraySet();
    private final AtomicBoolean mConnected = new AtomicBoolean();

    /* loaded from: classes3.dex */
    public interface Listener {
        void onNetworkStateUpdated(boolean z);
    }

    public NetworkStateHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        reopen();
    }

    public static synchronized void unsetInstance() {
        synchronized (NetworkStateHelper.class) {
            sSharedInstance = null;
        }
    }

    public static synchronized NetworkStateHelper getSharedInstance(Context context) {
        NetworkStateHelper networkStateHelper;
        synchronized (NetworkStateHelper.class) {
            if (sSharedInstance == null) {
                sSharedInstance = new NetworkStateHelper(context);
            }
            networkStateHelper = sSharedInstance;
        }
        return networkStateHelper;
    }

    public void reopen() {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                NetworkRequest.Builder request = new NetworkRequest.Builder();
                request.addCapability(12);
                this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.microsoft.appcenter.utils.NetworkStateHelper.1
                    @Override // android.net.ConnectivityManager.NetworkCallback
                    public void onAvailable(Network network) {
                        NetworkStateHelper.this.onNetworkAvailable(network);
                    }

                    @Override // android.net.ConnectivityManager.NetworkCallback
                    public void onLost(Network network) {
                        NetworkStateHelper.this.onNetworkLost(network);
                    }
                };
                this.mConnectivityManager.registerNetworkCallback(request.build(), this.mNetworkCallback);
            } else {
                ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
                this.mConnectivityReceiver = connectivityReceiver;
                this.mContext.registerReceiver(connectivityReceiver, getOldIntentFilter());
                handleNetworkStateUpdate();
            }
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Cannot access network state information.", e);
            this.mConnected.set(true);
        }
    }

    private IntentFilter getOldIntentFilter() {
        return new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    }

    public boolean isNetworkConnected() {
        return this.mConnected.get() || isAnyNetworkConnected();
    }

    private boolean isAnyNetworkConnected() {
        if (Build.VERSION.SDK_INT >= 21) {
            Network[] networks = this.mConnectivityManager.getAllNetworks();
            if (networks == null) {
                return false;
            }
            for (Network network : networks) {
                NetworkInfo info = this.mConnectivityManager.getNetworkInfo(network);
                if (info != null && info.isConnected()) {
                    return true;
                }
            }
        } else {
            NetworkInfo[] networks2 = this.mConnectivityManager.getAllNetworkInfo();
            if (networks2 == null) {
                return false;
            }
            for (NetworkInfo info2 : networks2) {
                if (info2 != null && info2.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onNetworkAvailable(Network network) {
        AppCenterLog.debug("AppCenter", "Network " + network + " is available.");
        if (this.mConnected.compareAndSet(false, true)) {
            notifyNetworkStateUpdated(true);
        }
    }

    public void onNetworkLost(Network network) {
        AppCenterLog.debug("AppCenter", "Network " + network + " is lost.");
        Network[] networks = this.mConnectivityManager.getAllNetworks();
        boolean noNetwork = networks == null || networks.length == 0 || Arrays.equals(networks, new Network[]{network});
        if (noNetwork && this.mConnected.compareAndSet(true, false)) {
            notifyNetworkStateUpdated(false);
        }
    }

    public void handleNetworkStateUpdate() {
        boolean connected = isAnyNetworkConnected();
        if (this.mConnected.compareAndSet(!connected, connected)) {
            notifyNetworkStateUpdated(connected);
        }
    }

    private void notifyNetworkStateUpdated(boolean connected) {
        StringBuilder sb = new StringBuilder();
        sb.append("Network has been ");
        sb.append(connected ? "connected." : "disconnected.");
        AppCenterLog.debug("AppCenter", sb.toString());
        for (Listener listener : this.mListeners) {
            listener.onNetworkStateUpdated(connected);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mConnected.set(false);
        if (Build.VERSION.SDK_INT >= 21) {
            this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
        } else {
            this.mContext.unregisterReceiver(this.mConnectivityReceiver);
        }
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    /* loaded from: classes3.dex */
    public class ConnectivityReceiver extends BroadcastReceiver {
        private ConnectivityReceiver() {
            NetworkStateHelper.this = r1;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NetworkStateHelper.this.handleNetworkStateUpdate();
        }
    }
}
