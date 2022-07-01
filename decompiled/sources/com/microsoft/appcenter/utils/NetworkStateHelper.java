package com.microsoft.appcenter.utils;

import android.annotation.SuppressLint;
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
/* loaded from: classes.dex */
public class NetworkStateHelper implements Closeable {
    @SuppressLint({"StaticFieldLeak"})
    private static NetworkStateHelper sSharedInstance;
    private final ConnectivityManager mConnectivityManager;
    private ConnectivityReceiver mConnectivityReceiver;
    private final Context mContext;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private final Set<Listener> mListeners = new CopyOnWriteArraySet();
    private final AtomicBoolean mConnected = new AtomicBoolean();

    /* loaded from: classes.dex */
    public interface Listener {
        void onNetworkStateUpdated(boolean z);
    }

    public NetworkStateHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        reopen();
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
                NetworkRequest.Builder builder = new NetworkRequest.Builder();
                builder.addCapability(12);
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
                this.mConnectivityManager.registerNetworkCallback(builder.build(), this.mNetworkCallback);
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
            Network[] allNetworks = this.mConnectivityManager.getAllNetworks();
            if (allNetworks == null) {
                return false;
            }
            for (Network network : allNetworks) {
                NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(network);
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
            }
        } else {
            NetworkInfo[] allNetworkInfo = this.mConnectivityManager.getAllNetworkInfo();
            if (allNetworkInfo == null) {
                return false;
            }
            for (NetworkInfo networkInfo2 : allNetworkInfo) {
                if (networkInfo2 != null && networkInfo2.isConnected()) {
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
        Network[] allNetworks = this.mConnectivityManager.getAllNetworks();
        if (!(allNetworks == null || allNetworks.length == 0 || Arrays.equals(allNetworks, new Network[]{network})) || !this.mConnected.compareAndSet(true, false)) {
            return;
        }
        notifyNetworkStateUpdated(false);
    }

    public void handleNetworkStateUpdate() {
        boolean isAnyNetworkConnected = isAnyNetworkConnected();
        if (this.mConnected.compareAndSet(!isAnyNetworkConnected, isAnyNetworkConnected)) {
            notifyNetworkStateUpdated(isAnyNetworkConnected);
        }
    }

    private void notifyNetworkStateUpdated(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("Network has been ");
        sb.append(z ? "connected." : "disconnected.");
        AppCenterLog.debug("AppCenter", sb.toString());
        for (Listener listener : this.mListeners) {
            listener.onNetworkStateUpdated(z);
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

    /* loaded from: classes.dex */
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
