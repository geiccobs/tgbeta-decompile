package com.google.firebase.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import com.google.firebase.events.Publisher;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
public class DataCollectionConfigStorage {
    private boolean dataCollectionDefaultEnabled = readAutoDataCollectionEnabled();
    private final Context deviceProtectedContext;
    private final Publisher publisher;
    private final SharedPreferences sharedPreferences;

    public DataCollectionConfigStorage(Context context, String str, Publisher publisher) {
        Context directBootSafe = directBootSafe(context);
        this.deviceProtectedContext = directBootSafe;
        this.sharedPreferences = directBootSafe.getSharedPreferences("com.google.firebase.common.prefs:" + str, 0);
        this.publisher = publisher;
    }

    private static Context directBootSafe(Context context) {
        return Build.VERSION.SDK_INT < 24 ? context : ContextCompat.createDeviceProtectedStorageContext(context);
    }

    public synchronized boolean isEnabled() {
        return this.dataCollectionDefaultEnabled;
    }

    private boolean readManifestDataCollectionEnabled() {
        ApplicationInfo applicationInfo;
        Bundle bundle;
        try {
            PackageManager packageManager = this.deviceProtectedContext.getPackageManager();
            if (packageManager != null && (applicationInfo = packageManager.getApplicationInfo(this.deviceProtectedContext.getPackageName(), ConnectionsManager.RequestFlagNeedQuickAck)) != null && (bundle = applicationInfo.metaData) != null && bundle.containsKey("firebase_data_collection_default_enabled")) {
                return applicationInfo.metaData.getBoolean("firebase_data_collection_default_enabled");
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return true;
        }
    }

    private boolean readAutoDataCollectionEnabled() {
        if (this.sharedPreferences.contains("firebase_data_collection_default_enabled")) {
            return this.sharedPreferences.getBoolean("firebase_data_collection_default_enabled", true);
        }
        return readManifestDataCollectionEnabled();
    }
}
