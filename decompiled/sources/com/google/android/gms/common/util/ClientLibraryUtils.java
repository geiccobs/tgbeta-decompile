package com.google.android.gms.common.util;

import android.content.Context;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.wrappers.Wrappers;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public class ClientLibraryUtils {
    public static boolean isPackageSide() {
        return false;
    }

    public static boolean zza(@RecentlyNonNull Context context, @RecentlyNonNull String str) {
        "com.google.android.gms".equals(str);
        return (Wrappers.packageManager(context).getApplicationInfo(str, 0).flags & 2097152) != 0;
    }
}
