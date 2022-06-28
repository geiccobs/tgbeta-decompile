package com.google.android.gms.internal.p001authapi;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import org.telegram.tgnet.ConnectionsManager;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* renamed from: com.google.android.gms.internal.auth-api.zbay */
/* loaded from: classes3.dex */
public final class zbay {
    public static final int zba;

    static {
        int i = Build.VERSION.SDK_INT;
        int i2 = ConnectionsManager.FileTypeVideo;
        if (i < 31 && (Build.VERSION.SDK_INT < 30 || Build.VERSION.CODENAME.length() != 1 || Build.VERSION.CODENAME.charAt(0) < 'S' || Build.VERSION.CODENAME.charAt(0) > 'Z')) {
            i2 = 0;
        }
        zba = i2;
    }

    public static PendingIntent zba(Context context, int i, Intent intent, int i2) {
        return PendingIntent.getActivity(context, 2000, intent, i2);
    }
}
