package com.microsoft.appcenter.utils;

import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.util.UUID;
/* loaded from: classes3.dex */
public class IdHelper {
    public static UUID getInstallId() {
        String installIdString = SharedPreferencesManager.getString(PrefStorageConstants.KEY_INSTALL_ID, "");
        try {
            return UUID.fromString(installIdString);
        } catch (Exception e) {
            AppCenterLog.warn("AppCenter", "Unable to get installID from Shared Preferences");
            UUID installId = UUID.randomUUID();
            SharedPreferencesManager.putString(PrefStorageConstants.KEY_INSTALL_ID, installId.toString());
            return installId;
        }
    }
}
