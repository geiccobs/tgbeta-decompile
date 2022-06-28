package com.google.android.exoplayer2.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class NotificationUtil {
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = -1000;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Importance {
    }

    @Deprecated
    public static void createNotificationChannel(Context context, String id, int nameResourceId, int importance) {
        createNotificationChannel(context, id, nameResourceId, 0, importance);
    }

    public static void createNotificationChannel(Context context, String id, int nameResourceId, int descriptionResourceId, int importance) {
        if (Util.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            NotificationChannel channel = new NotificationChannel(id, context.getString(nameResourceId), importance);
            if (descriptionResourceId != 0) {
                channel.setDescription(context.getString(descriptionResourceId));
            }
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setNotification(Context context, int id, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notification != null) {
            notificationManager.notify(id, notification);
        } else {
            notificationManager.cancel(id);
        }
    }

    private NotificationUtil() {
    }
}
